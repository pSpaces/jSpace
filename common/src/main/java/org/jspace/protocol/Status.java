package org.jspace.protocol;

/**
 * A status object represents a status returned by the server.
 * @param code is a HTTP style status code.
 * @param message is some message which should be related to the code
 */
public class Status {
    private int code;
    private String message;

    public Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @return Returns the code of this status object
     */
    public int getCode() {
        return this.code;
    }

    /**
     * @return Returns the message of this status object
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return Returns string representation of the object
     */
    @Override
    public String toString() {
        return "" + this.code + ": " + this.message;
    }

    /**
     * sets the code field to an integer
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * sets the message field to a string
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
