package io.github.revxrsal.cub;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a command sender.
 */
public interface CommandSubject {

    String getName();

    void reply(@NotNull String message);

    UUID getUUID();

}
