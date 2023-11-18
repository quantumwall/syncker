package org.quantum.synchronizer.exception;

public class FileAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FileAccessException(String message) {
        super(message);
    }

    public FileAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
