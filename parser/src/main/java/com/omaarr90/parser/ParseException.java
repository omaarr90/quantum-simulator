package com.omaarr90.parser;

/**
 * Exception thrown when OpenQASM parsing fails. This is a checked exception that provides detailed
 * error information including line and column numbers when available.
 */
public class ParseException extends Exception {

    private final int line;
    private final int column;
    private final String offendingSymbol;
    private final String suggestion;

    /**
     * Constructs a new ParseException with the specified detail message.
     *
     * @param message the detail message
     */
    public ParseException(String message) {
        this(message, -1, -1, null, null, null);
    }

    /**
     * Constructs a new ParseException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public ParseException(String message, Throwable cause) {
        this(message, -1, -1, null, null, cause);
    }

    /**
     * Constructs a new ParseException with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public ParseException(Throwable cause) {
        this(cause.getMessage(), -1, -1, null, null, cause);
    }

    /**
     * Constructs a new ParseException with detailed error information.
     *
     * @param message the detail message
     * @param line the line number where the error occurred (1-based, -1 if unknown)
     * @param column the column number where the error occurred (0-based, -1 if unknown)
     * @param offendingSymbol the symbol that caused the error (null if unknown)
     * @param suggestion helpful suggestion for fixing the error (null if none)
     */
    public ParseException(
            String message, int line, int column, String offendingSymbol, String suggestion) {
        this(message, line, column, offendingSymbol, suggestion, null);
    }

    /**
     * Constructs a new ParseException with detailed error information and cause.
     *
     * @param message the detail message
     * @param line the line number where the error occurred (1-based, -1 if unknown)
     * @param column the column number where the error occurred (0-based, -1 if unknown)
     * @param offendingSymbol the symbol that caused the error (null if unknown)
     * @param suggestion helpful suggestion for fixing the error (null if none)
     * @param cause the cause of this exception
     */
    public ParseException(
            String message,
            int line,
            int column,
            String offendingSymbol,
            String suggestion,
            Throwable cause) {
        super(formatMessage(message, line, column, offendingSymbol, suggestion), cause);
        this.line = line;
        this.column = column;
        this.offendingSymbol = offendingSymbol;
        this.suggestion = suggestion;
    }

    /**
     * Gets the line number where the error occurred.
     *
     * @return the line number (1-based), or -1 if unknown
     */
    public int getLine() {
        return line;
    }

    /**
     * Gets the column number where the error occurred.
     *
     * @return the column number (0-based), or -1 if unknown
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets the symbol that caused the error.
     *
     * @return the offending symbol, or null if unknown
     */
    public String getOffendingSymbol() {
        return offendingSymbol;
    }

    /**
     * Gets the helpful suggestion for fixing the error.
     *
     * @return the suggestion, or null if none available
     */
    public String getSuggestion() {
        return suggestion;
    }

    private static String formatMessage(
            String message, int line, int column, String offendingSymbol, String suggestion) {
        StringBuilder sb = new StringBuilder();

        if (line > 0 && column >= 0) {
            sb.append("Line ").append(line).append(":").append(column).append(" - ");
        }

        sb.append(message);

        if (offendingSymbol != null) {
            sb.append(" (near '").append(offendingSymbol).append("')");
        }

        if (suggestion != null) {
            sb.append(". ").append(suggestion);
        }

        return sb.toString();
    }
}
