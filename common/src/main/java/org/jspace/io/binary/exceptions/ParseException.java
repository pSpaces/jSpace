package org.jspace.io.binary.exceptions;

public class ParseException extends Exception {
    public ParseException(String message){
        super(message);
    }
    public ParseException(String message, Exception innerException){
        super(message, innerException);
    }
}
