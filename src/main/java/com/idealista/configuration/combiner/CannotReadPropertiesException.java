package com.idealista.configuration.combiner;

public class CannotReadPropertiesException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CannotReadPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }    
    
}