package io.github.revxrsal.cub;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a command sender.
 */
public interface CommandSubject {

    /**
     * Returns the name of this subject. Varies depending on the
     * platform.
     *
     * @return The subject name
     */
    @NotNull String getName();

    /**
     * Returns the unique UID of this subject. Varies depending
     * on the platform.
     * <p>
     * Although some platforms explicitly have their underlying senders
     * have UUIDs, some platforms may have to generate this UUID based on other available
     * data.
     *
     * @return The UUID of this subject.
     */
    @NotNull UUID getUUID();

    /**
     * Replies to the sender with the specified message.
     * <p>
     * Varies depending on the platform.
     *
     * @param message Message to reply with.
     */
    void reply(@NotNull String message);

}
