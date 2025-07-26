package com.omaarr90.parser;

/**
 * Exception thrown when OpenQASM parsing fails.
 * This is a checked exception that provides detailed error information
 * including line and column numbers when available.
 */
public class ParseException extends Exception {
    
    /**
     * Constructs a new ParseException with the specified detail message.
     * 
     * @param message the detail message
     */
    public ParseException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ParseException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new ParseException with the specified cause.
     * 
     * @param cause the cause of this exception
     */
    public ParseException(Throwable cause) {
        super(cause);
    }
}