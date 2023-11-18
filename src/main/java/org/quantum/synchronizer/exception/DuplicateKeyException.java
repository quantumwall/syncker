package org.quantum.synchronizer.exception;

public class DuplicateKeyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateKeyException(String message) {
        super(message);
    }

}
