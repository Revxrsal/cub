package io.github.revxrsal.cub.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class AnnReader {

    private final Map<Class<?>, Annotation> annotations = new HashMap<>();

    public AnnReader(AnnotatedElement element) {
        for (Annotation annotation : element.getDeclaredAnnotations()) {
            annotations.put(annotation.annotationType(), annotation);
        }
    }

    public boolean has(Class<? extends Annotation> type) {
        return annotations.containsKey(type);
    }

    public <A extends Annotation> A get(Class<A> type) {
        return (A) annotations.get(type);
    }

    public <T, A extends Annotation> T get(Class<A> type, Function<A, T> ifPresent, T otherwise) {
        A ann = get(type);
        if (ann == null) return otherwise;
        return ifPresent.apply(ann);
    }

}
