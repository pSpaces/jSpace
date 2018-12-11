package org.jspace.io.binary.exceptions;

public class ClassRegistryException extends Exception {
    public ClassRegistryException(String message){
        super(message);
    }
    public ClassRegistryException(String message, Exception innerException){
        super(message, innerException);
    }
}
