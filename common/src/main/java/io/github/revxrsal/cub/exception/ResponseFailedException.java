package io.github.revxrsal.cub.exception;

import lombok.Getter;

@Getter
public class ResponseFailedException extends CommandException {

    private final Object response;

    public ResponseFailedException(Object response) {
        this.response = response;
    }

    public ResponseFailedException(Throwable cause, Object response) {
        super(cause);
        this.response = response;
    }
}
