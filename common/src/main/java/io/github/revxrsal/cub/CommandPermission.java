package io.github.revxrsal.cub;

import org.jetbrains.annotations.NotNull;

public interface CommandPermission {

    boolean canExecute(@NotNull CommandSubject subject);

}
