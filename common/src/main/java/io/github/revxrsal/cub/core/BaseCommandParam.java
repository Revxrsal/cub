package io.github.revxrsal.cub.core;

import io.github.revxrsal.cub.CommandHandler;
import io.github.revxrsal.cub.CommandParameter;
import io.github.revxrsal.cub.ParameterResolver;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@AllArgsConstructor
final class BaseCommandParam implements CommandParameter {

    private final Parameter parameter;
    private final String name;
    private final int methodIndex;
    private final String def;
    private final boolean consumeString;
    private final CommandHandler handler;
    private final ParameterResolver<?, ?> resolver;
    private final boolean optional;
    private final @Nullable String switchName;
    private final boolean switchDef;

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
        return optional;
    }

    @Override public <A extends Annotation> A getAnnotation(@NotNull Class<A> annotation) {
        return parameter.getAnnotation(annotation);
    }

    @Override public boolean isSwitch() {
        return switchName != null;
    }

    @Override public @Nullable String getSwitchName() {
        return switchName;
    }

    @Override public boolean getDefaultSwitch() {
        return switchDef;
    }

    @Override public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return parameter.isAnnotationPresent(annotation);
    }

    @Override public @NotNull CommandHandler getCommandHandler() {
        return handler;
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
