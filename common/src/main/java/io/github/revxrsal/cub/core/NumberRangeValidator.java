package io.github.revxrsal.cub.core;

import io.github.revxrsal.cub.CommandParameter;
import io.github.revxrsal.cub.CommandSubject;
import io.github.revxrsal.cub.ParameterValidator;
import io.github.revxrsal.cub.annotation.Range;
import io.github.revxrsal.cub.exception.SimpleCommandException;
import org.jetbrains.annotations.NotNull;

final class NumberRangeValidator implements ParameterValidator<Number> {

    public static final NumberRangeValidator INSTANCE = new NumberRangeValidator();

    @Override public void validate(Number value, @NotNull CommandParameter parameter, @NotNull CommandSubject sender) {
        Range range = parameter.getAnnotation(Range.class);
        if (range == null) return;
        if (value.doubleValue() < range.min())
            throw new SimpleCommandException(parameter.getName() + " must be more than " + range.min());
        if (value.doubleValue() > range.max())
            throw new SimpleCommandException(parameter.getName() + " must be less than " + range.max());
    }
}
