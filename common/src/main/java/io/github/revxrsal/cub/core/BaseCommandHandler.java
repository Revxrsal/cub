package io.github.revxrsal.cub.core;

import io.github.revxrsal.cub.*;
import io.github.revxrsal.cub.annotation.ConditionEvaluator;
import io.github.revxrsal.cub.annotation.ContextResolver;
import io.github.revxrsal.cub.annotation.Dependency;
import io.github.revxrsal.cub.annotation.ValueResolver;
import io.github.revxrsal.cub.exception.CommandExceptionHandler;
import io.github.revxrsal.cub.exception.InvalidValueException;
import io.github.revxrsal.cub.exception.MissingPermissionException;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.revxrsal.cub.core.Utils.*;

/**
 * Basic implementation of {@link CommandHandler}.
 */
@SuppressWarnings("rawtypes")
public class BaseCommandHandler implements CommandHandler {

    final Map<String, HandledCommand> commands = new HashMap<>();
    final Map<Class<?>, ParameterResolver.ValueResolver<?>> typeResolvers = new HashMap<>();
    final Map<Class<?>, ParameterResolver.ContextResolver<?>> cxtResolvers = new HashMap<>();
    final Map<String, CommandCondition> conditions = new HashMap<>();
    final List<CommandCondition> globalConditions = new ArrayList<>();
    protected final Map<Class<?>, Supplier<?>> dependencies = new HashMap<>();

    final HashSetMultimap<Class<?>, ParameterValidator<?>> validators = new HashSetMultimap<>();
    final Map<Class<?>, ResponseHandler> responseHandlers = new HashMap<>();
    String switchPrefix = "-";
    String flagPrefix = "-";

    private CommandExceptionHandler exceptionHandler = null/*DefaultExceptionHandler.INSTANCE*/;

    public BaseCommandHandler() {
        registerParameterValidator(Number.class, NumberRangeValidator.INSTANCE);
        registerTypeResolver(String.class, (a, b, parameter) -> a.popForParameter(parameter));
        registerTypeResolver(int.class, (a, b, parameter) -> num(a, Integer::parseInt));
        registerTypeResolver(Integer.class, (a, b, parameter) -> num(a, Integer::parseInt));
        registerTypeResolver(double.class, (a, b, parameter) -> num(a, Double::parseDouble));
        registerTypeResolver(Double.class, (a, b, parameter) -> num(a, Double::parseDouble));
        registerTypeResolver(float.class, (a, b, parameter) -> num(a, Float::parseFloat));
        registerTypeResolver(Float.class, (a, b, parameter) -> num(a, Float::parseFloat));
        registerTypeResolver(byte.class, (a, b, parameter) -> num(a, Byte::parseByte));
        registerTypeResolver(Byte.class, (a, b, parameter) -> num(a, Byte::parseByte));
        registerTypeResolver(short.class, (a, b, parameter) -> num(a, Short::parseShort));
        registerTypeResolver(Short.class, (a, b, parameter) -> num(a, Short::parseShort));
        registerTypeResolver(long.class, (a, b, parameter) -> num(a, Long::parseLong));
        registerTypeResolver(Long.class, (a, b, parameter) -> num(a, Long::parseLong));
        registerTypeResolver(boolean.class, (a, b, parameter) -> resolveBoolean(a));
        registerTypeResolver(Boolean.class, (a, b, parameter) -> resolveBoolean(a));
        registerContextResolver(CommandHandler.class, (args, sender, parameter) -> BaseCommandHandler.this);
        registerContextResolver(CommandSubject.class, (args, sender, parameter) -> sender);
        registerContextResolver(HandledCommand.class, (args, sender, parameter) -> parameter.getDeclaringCommand());
        registerGlobalCondition((subject, args, command, context) -> {
            if (!command.getPermission().canExecute(subject))
                throw new MissingPermissionException(command.getPermission());
        });
        registerGlobalCondition(new CooldownCondition());
    }

    @Override public CommandHandler setExceptionHandler(@NotNull CommandExceptionHandler exceptionHandler) {
        this.exceptionHandler = n(exceptionHandler, "exceptionHandler");
        return this;
    }

    @Override public @NotNull String getSwitchPrefix() {
        return switchPrefix;
    }

    @Override public CommandHandler setSwitchPrefix(@NotNull String prefix) {
        n(prefix, "Switch prefix cannot be null!");
        if (prefix.isEmpty())
            throw new IllegalArgumentException("Switch prefix cannot be an empty string!");
        switchPrefix = prefix;
        return this;
    }

    @Override public @NotNull String getFlagPrefix() {
        return flagPrefix;
    }

    @Override public CommandHandler setFlagPrefix(@NotNull String prefix) {
        n(prefix, "Switch prefix cannot be null!");
        if (prefix.isEmpty())
            throw new IllegalArgumentException("Switch prefix cannot be an empty string!");
        flagPrefix = prefix;
        return this;
    }

    @Override public CommandHandler registerCommand(@NotNull Object instance) {
        addCommand(new BaseHandledCommand(this, instance, null, null));
        setDependencies(instance);
        return this;
    }

    @SneakyThrows
    @Override public CommandHandler registerResolvers(@NotNull Object... resolvers) {
        for (Object resolver : resolvers) {
            Class<?> type = getType(resolver);
            for (Method method : type.getDeclaredMethods()) {
                ValueResolver tr = method.getAnnotation(ValueResolver.class);
                if (tr != null) {
                    checkReturns(method);
                    ensureAccessible(method);
                    MethodHandle handle = bind(MethodHandles.lookup().unreflect(method), resolver);
                    Class<?>[] ptypes = method.getParameterTypes();
                    Set<ParameterValidator<?>> validators = getValidators(tr.value());
                    typeResolvers.put(tr.value(), (a, b, parameter) -> {
                        List<Object> ia = new ArrayList<>();
                        for (Class<?> ptype : ptypes) {
                            if (ArgumentStack.class.isAssignableFrom(ptype))
                                ia.add(a);
                            else if (List.class.isAssignableFrom(ptype))
                                ia.add(a.asImmutableList());
                            else if (CommandSubject.class.isAssignableFrom(ptype))
                                ia.add(b);
                            else if (CommandParameter.class.isAssignableFrom(ptype))
                                ia.add(parameter);
                            else if (String.class.isAssignableFrom(ptype))
                                ia.add(a.popForParameter(parameter));
                        }
                        Object value = handle.invokeWithArguments(ia);
                        for (ParameterValidator validator : validators) {
                            validator.validate(value, parameter, b);
                        }
                        return value;
                    });
                }
                ContextResolver cr = method.getAnnotation(ContextResolver.class);
                if (cr != null) {
                    checkReturns(method);
                    ensureAccessible(method);
                    MethodHandle handle = bind(MethodHandles.lookup().unreflect(method), resolver);
                    Class<?>[] ptypes = method.getParameterTypes();
                    cxtResolvers.put(cr.value(), (a, b, parameter) -> {
                        List<Object> ia = new ArrayList<>();
                        for (Class<?> ptype : ptypes) {
                            if (List.class.isAssignableFrom(ptype))
                                ia.add(a);
                            else if (CommandSubject.class.isAssignableFrom(ptype))
                                ia.add(b);
                            else if (CommandParameter.class.isAssignableFrom(ptype))
                                ia.add(parameter);
                        }
                        Object value = handle.invokeWithArguments(ia);
                        for (ParameterValidator validator : getValidators(cr.value())) {
                            validator.validate(value, parameter, b);
                        }
                        return value;
                    });
                }
                ConditionEvaluator ce = method.getAnnotation(ConditionEvaluator.class);
                if (ce != null) {
                    ensureAccessible(method);
                    MethodHandle handle = bind(MethodHandles.lookup().unreflect(method), resolver);
                    Class<?>[] ptypes = method.getParameterTypes();
                    registerCondition(ce.value(), (sender, args, command, bcmd) -> {
                        List<Object> ia = new ArrayList<>();
                        for (Class<?> ptype : ptypes) {
                            if (List.class.isAssignableFrom(ptype)) {
                                ia.add(args);
                            } else if (CommandSubject.class.isAssignableFrom(ptype)) {
                                ia.add(sender);
                            } else if (HandledCommand.class.isAssignableFrom(ptype)) {
                                ia.add(command);
                            } else if (CommandContext.class.isAssignableFrom(ptype)) {
                                ia.add(bcmd);
                            } else {
                                injectValues(type, sender, args, command, bcmd, ia);
                            }
                        }
                        handle.invokeWithArguments(ia);
                    });
                }
                addResolvers(method, resolver);
            }
        }
        return this;
    }

    protected void addResolvers(Method method, Object resolver) {
    }

    @Override public CommandHandler registerCondition(@NotNull String conditionID, @NotNull CommandCondition condition) {
        conditions.put(n(conditionID, "conditionID"), n(condition, "condition"));
        return this;
    }

    @Override public CommandHandler registerGlobalCondition(@NotNull CommandCondition condition) {
        globalConditions.add(n(condition, "condition"));
        return null;
    }

    public <T> CommandHandler registerTypeResolver(@NotNull Class<T> type, ParameterResolver.@NotNull ValueResolver<T> resolver) {
        typeResolvers.put(type, (a, b, parameter) -> {
            Object value = resolver.resolve(a, b, parameter);
            for (ParameterValidator validator : getValidators(type))
                validator.validate(value, parameter, b);
            return value;
        });
        return this;
    }

    public <T> CommandHandler registerContextResolver(@NotNull Class<T> type, ParameterResolver.@NotNull ContextResolver<T> resolver) {
        cxtResolvers.put(type, (a, b, parameter) -> {
            Object value = resolver.resolve(a, b, parameter);
            for (ParameterValidator validator : getValidators(type))
                validator.validate(value, parameter, b);
            return value;
        });
        return this;
    }

    public <T> CommandHandler registerParameterValidator(@NotNull Class<T> type, @NotNull ParameterValidator<T> validator) {
        validators.put(type, validator);
        return this;
    }

    @Override public <T> CommandHandler registerDependency(@NotNull Class<T> dependencyType, Supplier<T> supplier) {
        dependencies.put(dependencyType, supplier);
        return this;
    }

    @Override public <T> CommandHandler registerDependency(@NotNull Class<T> dependencyType, T value) {
        dependencies.put(dependencyType, () -> value);
        return this;
    }

    @Override public <T> CommandHandler registerResponseHandler(@NotNull Class<T> responseType, @NotNull ResponseHandler<T> responseHandler) {
        responseHandlers.put(responseType, responseHandler);
        return this;
    }

    @Override public @NotNull @UnmodifiableView Map<String, HandledCommand> getCommands() {
        return immutableCommands;
    }

    @Override public @NotNull CommandExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    protected void addCommand(BaseHandledCommand cmd) {
        HandledCommand replaced = commands.put(cmd.getName(), cmd);
        if (replaced != null)
            cmd.subcommands.putAll(replaced.getSubcommands());
        for (String alias : cmd.getAliases()) {
            replaced = commands.put(alias, cmd);
            if (replaced != null)
                cmd.subcommands.putAll(replaced.getSubcommands());
        }
    }

    @SneakyThrows protected void setDependencies(Object instance) {
        for (Field field : getType(instance).getDeclaredFields()) {
            if (field.isAnnotationPresent(Dependency.class)) {
                ensureAccessible(field);
                field.set(instance, c(dependencies.get(field.getType()), "No dependency supplier registered for " + field.getType()).get());
            }
        }
    }

    private final Map<String, HandledCommand> immutableCommands = Collections.unmodifiableMap(commands);

    private Set<ParameterValidator<?>> getValidators(@NotNull Class<?> type) {
        Set<ParameterValidator<?>> validators = new HashSet<>();
        for (Entry<Class<?>, Set<ParameterValidator<?>>> ve : this.validators.entries()) {
            for (ParameterValidator<?> validator : ve.getValue()) {
                if (ve.getKey().isAssignableFrom(Primitives.wrap(type)))
                    validators.add(validator);
            }
        }
        return validators;
    }

    private static <T> T num(ArgumentStack stack, Function<String, T> s) {
        String num = stack.pop();
        try {
            return s.apply(num);
        } catch (NumberFormatException e) {
            throw new InvalidValueException(InvalidValueException.NUMBER, num);
        }
    }

    private static boolean resolveBoolean(ArgumentStack stack) {
        String value = stack.pop();
        switch (value.toLowerCase()) {
            case "true":
            case "yes":
            case "ye":
            case "yeah":
            case "ofcourse":
            case "mhm":
                return true;
            default:
                return false;
        }
    }

    protected void injectValues(Class<?> type, @NotNull CommandSubject sender, @NotNull List<String> args, @NotNull HandledCommand command, @NotNull CommandContext bcmd, List<Object> ia) {
    }

}
