package io.github.revxrsal.cub.exception;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class InvalidValueException extends CommandException {

    /**
     * Passed when an invalid player is inputted.
     */
    public static final ValueType PLAYER = new ValueType("player");

    /**
     * Passed when an invalid number (e.g a non-number char) is inputted.
     */
    public static final ValueType NUMBER = new ValueType("number");

    /**
     * Passed when an invalid world is inputted.
     */
    public static final ValueType WORLD = new ValueType("world");

    private final ValueType valueType;
    private final Object value;

    public InvalidValueException(@NotNull InvalidValueException.ValueType valueType, @NotNull Object value) {
        super(value.toString());
        this.valueType = valueType;
        this.value = value;
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ValueType {

        private final String id;

        @Override public String toString() {
            return id;
        }
    }

}
