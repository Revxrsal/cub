package io.github.revxrsal.cub.core;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Utils {

    public static final char COLOR_CHAR = '\u00A7';

    public static <T> T n(T t) {
        return Objects.requireNonNull(t);
    }

    public static <T> T n(T t, String msg) {
        return Objects.requireNonNull(t, msg);
    }

    public static <T> T c(T t, String msg) {
        if (t == null)
            throw new IllegalArgumentException(msg);
        return t;
    }

    public static void ensureAccessible(AccessibleObject object) {
        if (!object.isAccessible())
            object.setAccessible(true);
    }

    public static <T extends Enum<T>> Class<T> toEnum(@NotNull Class<?> type) {
        return (Class<T>) type;
    }

    @SafeVarargs
    public static <T> T firstNotNull(T... values) {
        for (T v : values)
            if (v != null) return v;
        return null;
    }

    public static void checkReturns(Method method) {
        if (method.getReturnType() == Void.TYPE)
            throw new IllegalArgumentException("Method " + method.getName() + " must not return void!");
    }

    public static Class<?> getType(Object o) {
        return o instanceof Class ? (Class<?>) o : o.getClass();
    }

    public static MethodHandle bind(MethodHandle handle, Object instance) {
        if (!(instance instanceof Class))
            return handle.bindTo(instance);
        return handle;
    }

    @SafeVarargs public static <T> List<T> immutable(@NotNull T... values) {
        return Collections.unmodifiableList(Arrays.asList(values));
    }

    public static String colorize(String text) {
        char[] b = text.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

}
