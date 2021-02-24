package io.github.revxrsal.cub.core;

import io.github.revxrsal.cub.*;
import io.github.revxrsal.cub.annotation.Optional;
import io.github.revxrsal.cub.annotation.*;
import io.github.revxrsal.cub.exception.ResolverFailedException;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.github.revxrsal.cub.core.BaseDispatcher.sneakyThrow;
import static io.github.revxrsal.cub.core.Utils.*;

public class BaseHandledCommand implements HandledCommand {

    private static final Executor ASYNC = Executors.newSingleThreadExecutor();
    private static final Executor SYNC = Runnable::run;

    protected String name;
    private List<String> aliases = new ArrayList<>();
    ResponseHandler responseHandler = ResponseHandler.VOID;
    private String description;
    private String usage;
    private List<CommandCondition> conditions;
    private boolean async;
    private Executor executor;
    private boolean isPrivate;
    private @Nullable MethodHandle method;
    @Nullable MethodHandle fallback;
    List<Parameter> fallbackParameters = new ArrayList<>();
    private @Nullable BaseHandledCommand parent;
    protected CommandPermission permission = sender -> true; // platform-dependent
    protected final BaseCommandHandler handler;
    protected final AnnReader annReader;
    final Map<String, HandledCommand> subcommands = new HashMap<>();
    private final List<CommandParameter> params = new ArrayList<>();

    @SneakyThrows public BaseHandledCommand(BaseCommandHandler handler, Object instance,
                                            @Nullable BaseHandledCommand parent,
                                            @Nullable AnnotatedElement ae) {
        this.handler = handler;
        handler.setDependencies(instance);
        if (ae == null) ae = getType(instance);
        if (ae instanceof Class) {
            AnnReader classAnnotations = annReader = new AnnReader(ae);
            if (classAnnotations.has(Command.class)) {
                Command commandAnnotation = classAnnotations.get(Command.class);
                method = null; // it's a category.
                this.parent = null;
                name = commandAnnotation.value();
                aliases = Utils.immutable(commandAnnotation.aliases());
            } else if (classAnnotations.has(Subcommand.class)) {
                if (parent == null) throw new IllegalArgumentException("@Subcommand " + ae + " has no parent!");
                Subcommand subcommand = classAnnotations.get(Subcommand.class);
                name = subcommand.value();
                aliases = Utils.immutable(subcommand.aliases());
            }
            setProperties0();
            // scan class methods
            for (Method method : ((Class<?>) ae).getDeclaredMethods()) {
                AnnReader reader = new AnnReader(method);
                if (reader.has(Subcommand.class)) {
                    registerSubcommand(newCommand(handler, instance, this, method));
                } else if (reader.has(Command.class)) {
                    handler.addCommand(newCommand(handler, instance, null, method));
                } else if (reader.has(CatchInvalid.class)) {
                    ensureAccessible(method);
                    fallback = bind(MethodHandles.lookup().unreflect(method), instance);
                    Collections.addAll(fallbackParameters, method.getParameters());
                }
            }

            // scan inner classes
            for (Class<?> innerClass : ((Class<?>) ae).getDeclaredClasses()) {
                AnnReader inner = new AnnReader(innerClass);
                if (inner.has(Subcommand.class)) {
                    registerSubcommand(newCommand(handler, innerClass.newInstance(), this, innerClass));
                } else if (inner.has(Command.class)) {
                    handler.addCommand(newCommand(handler, innerClass.newInstance(), null, innerClass));
                }
            }
        } else {
            AnnReader reader = annReader = new AnnReader(ae);
            if (parent != null) {
                if (parent != this) this.parent = parent;
                Subcommand subcommand = reader.get(Subcommand.class);
                name = subcommand.value();
                aliases = Utils.immutable(subcommand.aliases());
            } else {
                Command subcommand = reader.get(Command.class);
                name = subcommand.value();
                aliases = Utils.immutable(subcommand.aliases());
            }
            setProperties0();
            if (Future.class.isAssignableFrom(((Method) ae).getReturnType())
                    || CompletionStage.class.isAssignableFrom(((Method) ae).getReturnType())) {
                async = true;
                executor = ASYNC;
            }
            ensureAccessible((Method) ae);
            Parameter[] parameters = ((Method) ae).getParameters();
            for (int index = 0; index < parameters.length; index++) {
                Parameter parameter = parameters[index];
                AnnReader pr = new AnnReader(parameter);
                //noinspection RedundantCast
                BaseCommandParam param = new BaseCommandParam(
                        parameter,
                        pr.get(Named.class, Named::value, parameter.getName()),
                        index,
                        pr.get(Default.class, Default::value, null),
                        index == ((Method) ae).getParameterCount() - 1 && !pr.has(Single.class),
                        handler,
                        this,
                        c(firstNotNull(
                                handler.typeResolvers.get(parameter.getType()),
                                handler.cxtResolvers.get(parameter.getType()),
                                parameter.getType().isEnum() ? (ParameterResolver.ValueResolver<Enum<?>>) new ParameterResolver.ValueResolver<Enum<?>>() {
                                    @Override public Enum<?> resolve(@NotNull ArgumentStack args, @NotNull CommandSubject sender, @NotNull CommandParameter parameter1) throws Throwable {
                                        String value = args.pop();
                                        try {
                                            return Enum.valueOf(((Class<? extends Enum>) parameter.getType()), value.toUpperCase());
                                        } catch (NoSuchElementException e) {
                                            throw new ResolverFailedException(this, value, parameter1, null);
                                        }
                                    }
                                } : null),
                                "Don't know how to resolve parameter type " + parameter.getType()),
                        pr.has(Optional.class),
                        pr.get(Switch.class),
                        pr.get(Flag.class)
                );
                if (param.isSwitch() && Primitives.unwrap(param.getType()) != Boolean.TYPE)
                    throw new IllegalArgumentException("Cannot use @Switch on non-boolean parameters (" + param.getType().getSimpleName() + " " + param.getName() + ")");
                params.add(param);
            }
            method = bind(MethodHandles.lookup().unreflect((Method) ae), instance);
            Type returnType = ((Method) ae).getGenericReturnType();
            Class<?> crt = ((Method) ae).getReturnType();
            if (CompletionStage.class.isAssignableFrom(crt)) {
                returnType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
                Type finalReturnType = returnType;
                responseHandler = (ResponseHandler<CompletionStage<?>>) (response, subject, command, context) -> response.thenAcceptAsync(value -> {
                    try {
                        handler.responseHandlers.getOrDefault(
                                finalReturnType instanceof Class ? finalReturnType : crt,
                                ResponseHandler.VOID).handleResponse(value, subject, command, context);
                    } catch (Throwable t) {
                        SYNC.execute(() -> {
                            throw sneakyThrow(t);
                        });
                    }
                });
            } else responseHandler = handler.responseHandlers.getOrDefault(crt, ResponseHandler.VOID);
        }
        if (usage == null) usage = generateUsage(this);
    }

    private void registerSubcommand(@NotNull BaseHandledCommand c) {
        subcommands.put(c.name, c);
        for (String alias : c.aliases)
            subcommands.put(alias, c);
    }

    private void setProperties0() {
        description = annReader.get(Description.class, Description::value, null);
        usage = annReader.get(Usage.class, Usage::value, null);
        conditions = Arrays.stream(annReader.get(Conditions.class, Conditions::value, new String[0]))
                .map(id -> n(handler.conditions.get(id), "Invalid condition: " + id)).collect(Collectors.toList());
        conditions.addAll(handler.globalConditions);
        isPrivate = annReader.has(PrivateCommand.class);
        async = annReader.has(RunAsync.class);
        executor = async ? ASYNC : SYNC;
        setProperties();
    }

    protected void setProperties() {
    }

    @Override public String getName() {
        return name;
    }

    @Override public @Nullable String getDescription() {
        return description;
    }

    @Override public @NotNull String getUsage() {
        return usage;
    }

    @Override public @Nullable HandledCommand getParent() {
        return parent;
    }

    @Override public @NotNull CommandPermission getPermission() {
        return permission;
    }

    public boolean isRootCommand() {
        return parent == null;
    }

    @Override public @NotNull @Unmodifiable List<CommandParameter> getParameters() {
        return params;
    }

    @Override public @NotNull List<CommandCondition> getConditions() {
        return conditions;
    }

    @Override public @NotNull Map<String, HandledCommand> getSubcommands() {
        return immutableSubcommands;
    }

    @Override public boolean isAsync() {
        return async;
    }

    @Override public boolean isPrivate() {
        return isPrivate;
    }

    @Override public <A extends Annotation> A getAnnotation(@NotNull Class<A> annotation) {
        return annReader.get(annotation);
    }

    @Override public boolean hasAnnotation(@NotNull Class<? extends Annotation> annotation) {
        return annReader.has(annotation);
    }

    protected BaseHandledCommand newCommand(BaseCommandHandler handler, Object o, BaseHandledCommand parent, Class<?> innerClass) {
        return new BaseHandledCommand(handler, o, parent, innerClass);
    }

    protected BaseHandledCommand newCommand(BaseCommandHandler handler, Object o, BaseHandledCommand parent, Method method) {
        return new BaseHandledCommand(handler, o, parent, method);
    }

    @Override public @NotNull CommandHandler getCommandHandler() {
        return handler;
    }

    @Override public @NotNull Executor getExecutor() {
        return executor;
    }

    public @NotNull @Unmodifiable List<String> getAliases() {
        return aliases;
    }

    public @Nullable MethodHandle getMethodHandle() {
        return method;
    }

    private final Map<String, HandledCommand> immutableSubcommands = Collections.unmodifiableMap(subcommands);

    @Override public String toString() {
        return "HandledCommand{" +
                "name='" + name + '\'' +
                ", aliases=" + aliases +
                ", responseHandler=" + responseHandler +
                ", description='" + description + '\'' +
                ", usage='" + usage + '\'' +
                ", async=" + async +
                ", isPrivate=" + isPrivate +
                ", method=" + method +
                ", handler=" + handler +
                ", subcommands=" + subcommands +
                ", params=" + params +
                ", immutableSubcommands=" + immutableSubcommands +
                '}';
    }

    private static String generateUsage(HandledCommand command) {
        if (!command.getParameters().isEmpty()) {
            StringJoiner joiner = new StringJoiner(" ");
            for (CommandParameter parameter : command.getParameters())
                joiner.add(parameter.isOptional() ? "[" + parameter.getName() + "]" : "<" + parameter.getName() + ">");
            return joiner.toString();
        } else {
            StringJoiner joiner = new StringJoiner("\n");
            Set<HandledCommand> commands = new LinkedHashSet<>(command.getSubcommands().values());
            for (HandledCommand subcommand : commands) {
                joiner.add(generateUsage(subcommand));
            }
            return joiner.toString();
        }
    }
}
