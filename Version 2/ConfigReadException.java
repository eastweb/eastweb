package edu.sdstate.eastweb.prototype;

import java.io.IOException;

/**
 * Thrown when something goes wrong in {@code Config.getInstance()}.
 * 
 * @author Michael VanBemmel
 */
public class ConfigReadException extends IOException {
    private static final long serialVersionUID = 1L;

    public ConfigReadException() {
        super();
    }

    public ConfigReadException(String message) {
        super(message);
    }

    public ConfigReadException(Throwable cause) {
        super(cause);
    }

    public ConfigReadException(String message, Throwable cause) {
        super(message, cause);
    }
}