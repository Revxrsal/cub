package io.github.revxrsal.cub.core;

import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.CommandParameter;
import io.github.revxrsal.cub.HandledCommand;
import io.github.revxrsal.cub.ParameterResolver;
import io.github.revxrsal.cub.annotation.Flag;
import io.github.revxrsal.cub.annotation.Switch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

final class BaseCommandParam implements CommandParameter {

    private final Parameter parameter;
    private final String name;
    private final int methodIndex;
    private final String def;
    private final boolean consumeString;
    private final BaseCommandHandler handler;
    private final HandledCommand command;
    private final ParameterResolver<?, ?> resolver;
    private final boolean optional;
    private final @Nullable Switch switchAnn;
    private final @Nullable Flag flag;

    public BaseCommandParam(Parameter parameter, String name, int methodIndex, String def, boolean consumeString, BaseCommandHandler handler, HandledCommand command, boolean optional, @Nullable Switch switchAnn, @Nullable Flag flag) {
        this.parameter = parameter;
        this.name = name;
        this.methodIndex = methodIndex;
        this.def = def;
        this.consumeString = consumeString;
        this.handler = handler;
        this.command = command;
        this.optional = optional;
        this.switchAnn = switchAnn;
        this.flag = flag;
        this.resolver = handler.getResolver(this);
    }

    @Override public @NotNull String getName() {
        return name;
    }

    @Override public int getMethodIndex() {
        return methodIndex;
    }

    @Override public @NotNull Class<?> getType() {
        return parameter.getType();
    }

    @Override public @NotNull Type getFullType() {
        return parameter.getParameterizedType();
    }

    @Override public @Nullable String getDefaultValue() {
        return def;
    }

    @Override public boolean consumesAllString() {
        return consumeString;
    }

    @Override public Parameter getJavaParameter() {
        return parameter;
    }

    @Override public boolean isOptional() {
        return optional || getDefaultValue() != null;
    }

    @Override public <A extends Annotation> A getAnnotation(@NotNull Class<A> annotation) {
        return parameter.getAnnotation(annotation);
    }

    @Override public boolean isSwitch() {
        return switchAnn != null;
    }

    @Override public @Nullable String getSwitchName() {
        return isSwitch() ? (switchAnn.value().isEmpty() ? getName() : switchAnn.value()) : null;
    }

    @Override public boolean getDefaultSwitch() {
        return isSwitch() && switchAnn.defaultValue();
    }

    @Override public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return parameter.isAnnotationPresent(annotation);
    }

    @Override public boolean isFlag() {
        return flag != null;
    }

    @Override public @Nullable String getFlagName() {
        return isFlag() ? (flag.value().isEmpty() ? getName() : flag.value()) : null;
    }

    @Override public @NotNull CommandHandler getCommandHandler() {
        return handler;
    }

    @Override public @NotNull HandledCommand getDeclaringCommand() {
        return command;
    }

    @Override public @NotNull <T> ParameterResolver<?, T> getResolver() {
        return (ParameterResolver<?, T>) resolver;
    }

    @Override public String toString() {
        return "CommandParameter{" +
                "parameter=" + parameter +
                ", name='" + name + '\'' +
                ", methodIndex=" + methodIndex +
                ", def='" + def + '\'' +
                ", consumeString=" + consumeString +
                ", handler=" + handler +
                ", resolver=" + resolver +
                ", optional=" + optional +
                '}';
    }
}
