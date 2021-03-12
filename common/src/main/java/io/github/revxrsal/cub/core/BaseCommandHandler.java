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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
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
    final Map<String, CommandCondition> conditions = new HashMap<>();
    final List<CommandCondition> globalConditions = new ArrayList<>();
    final HashSetMultimap<Class<?>, ParameterValidator<?>> validators = new HashSetMultimap<>();
    final Map<Class<?>, ResponseHandler> responseHandlers = new HashMap<>();

    protected final Map<Class<?>, Supplier<?>> dependencies = new HashMap<>();
    protected final List<ResolverFactory<?>> resolverFactories = new ArrayList<>();
    @Nullable CommandHelpWriter<?> helpWriter;

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
        registerTypeResolver(TargetHandledCommand.class, (args, subject, parameter) -> {
            HandledCommand found = parameter.getDeclaringCommand().getParent();
            if (found == null) found = parameter.getDeclaringCommand();
            String text;
            found = found.getSubcommands().get(text = args.popForParameter(parameter));
            if (found == null)
                throw new InvalidValueException(InvalidValueException.SUBCOMMAND, text);
            HandledCommand finalFound = found;
            return () -> finalFound;
        });
        registerContextResolver(CommandHandler.class, (args, sender, parameter) -> BaseCommandHandler.this);
        registerContextResolver(CommandSubject.class, (args, sender, parameter) -> sender);
        registerContextResolver(HandledCommand.class, (args, sender, parameter) -> parameter.getDeclaringCommand());
//        registerContextResolver((Class) CommandHelp.class, new BaseCommandHelp.Resolver(this));
        registerContextResolver(CommandHelpWriter.class, ParameterResolver.ContextResolver.of(this::getHelpWriter));
        registerGlobalCondition((subject, args, command, context) -> {
            if (!command.getPermission().canExecute(subject))
                throw new MissingPermissionException(command.getPermission());
        });
        addFactory(new EnumValueFactory());
        addFactory((ContextResolverFactory) (parameter, command, handler) -> {
            if (!parameter.hasAnnotation(Dependency.class)) return null;
            return ParameterResolver.ContextResolver.of(c(dependencies.get(parameter.getType()), "No dependency supplier registered for " + parameter.getType()));
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

    @Override public <T> CommandHelpWriter<T> getHelpWriter() {
        return (CommandHelpWriter<T>) helpWriter;
    }

    @Override public <T> CommandHandler setHelpWriter(@NotNull CommandHelpWriter<T> writer) {
        n(writer, "CommandHelpWriter cannot be null!");
        helpWriter = writer;
        return this;
    }

    @Override public CommandHandler registerValueResolverFactory(@NotNull ValueResolverFactory factory) {
        n(factory, "ValueResolverFactory cannot be null!");
        addFactory(factory);
        return this;
    }

    @Override public CommandHandler registerContextResolverFactory(@NotNull ContextResolverFactory factory) {
        n(factory, "ContextResolverFactory cannot be null!");
        addFactory(factory);
        return this;
    }

    // for some reason the Java 8 compiler can't infer lambdas correctly,
    // so we have no choice but turn them into anonymous classes.
    @SuppressWarnings("Convert2Lambda")
    private void addFactory(ResolverFactory factory) {
        if (factory instanceof ValueResolverFactory) {
            resolverFactories.add(0, (ValueResolverFactory) (parameter, command, handler) -> {
                ParameterResolver resolver = factory.create(parameter, command, handler);
                if (resolver == null) return null;
                return new ParameterResolver.ValueResolver<Object>() {
                    @Override public Object resolve(@NotNull @Unmodifiable ArgumentStack args, @NotNull CommandSubject subject, @NotNull CommandParameter parameter) throws Throwable {
                        Object value = resolver.resolve(args, subject, parameter);
                        for (ParameterValidator validator : getValidators(parameter.getType())) {
                            validator.validate(value, parameter, subject);
                        }
                        return value;
                    }
                };
            });
        } else {
            resolverFactories.add(0, (parameter, command, handler) -> {
                ParameterResolver resolver = factory.create(parameter, command, handler);
                if (resolver == null) return null;
                return new ParameterResolver.ContextResolver<Object>() {
                    @Override public Object resolve(@NotNull @Unmodifiable List<String> args, @NotNull CommandSubject subject, @NotNull CommandParameter parameter) throws Throwable {
                        Object value = resolver.resolve(args, subject, parameter);
                        for (ParameterValidator validator : getValidators(parameter.getType())) {
                            validator.validate(value, parameter, subject);
                        }
                        return value;
                    }
                };
            });
        }

    }

    @Override public CommandHandler registerCommand(@NotNull Object... instances) {
        for (Object instance : instances) {
            addCommand(new BaseHandledCommand(this, instance, null, null));
            setDependencies(instance);
        }
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
                    addFactory(ValueResolverFactory.forType(((Class) tr.value()), (a, b, parameter) -> {
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

                        return handle.invokeWithArguments(ia);
                    }));
                }
                ContextResolver cr = method.getAnnotation(ContextResolver.class);
                if (cr != null) {
                    checkReturns(method);
                    ensureAccessible(method);
                    MethodHandle handle = bind(MethodHandles.lookup().unreflect(method), resolver);
                    Class<?>[] ptypes = method.getParameterTypes();
                    addFactory(ContextResolverFactory.forType((Class) cr.value(), (a, b, parameter) -> {
                        List<Object> ia = new ArrayList<>();
                        for (Class<?> ptype : ptypes) {
                            if (List.class.isAssignableFrom(ptype))
                                ia.add(a);
                            else if (CommandSubject.class.isAssignableFrom(ptype))
                                ia.add(b);
                            else if (CommandParameter.class.isAssignableFrom(ptype))
                                ia.add(parameter);
                        }
                        return handle.invokeWithArguments(ia);
                    }));
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
        return this;
    }

    public <T> CommandHandler registerTypeResolver(@NotNull Class<T> type, ParameterResolver.@NotNull ValueResolver<T> resolver) {
        addFactory(ValueResolverFactory.forType(type, resolver));
        return this;
    }

    public <T> CommandHandler registerContextResolver(@NotNull Class<T> type, ParameterResolver.@NotNull ContextResolver<T> resolver) {
        addFactory(ContextResolverFactory.forType(type, resolver));
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

    public ParameterResolver<?, ?> getResolver(CommandParameter parameter) {
        HandledCommand command = parameter.getDeclaringCommand();
        for (ResolverFactory<?> factory : resolverFactories) {
            ParameterResolver<?, ?> resolver = factory.create(parameter, command, this);
            if (resolver != null) return resolver;
        }
        throw new IllegalArgumentException("Don't know how to resolve parameter '" + parameter.getName() + "'of type " + parameter.getType());
    }

    protected void injectValues(Class<?> type, @NotNull CommandSubject sender, @NotNull List<String> args, @NotNull HandledCommand command, @NotNull CommandContext bcmd, List<Object> ia) {
    }

}
