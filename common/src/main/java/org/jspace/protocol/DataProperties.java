package org.jspace.protocol;
import java.util.Arrays;


public class DataProperties {
    public String type;
    public Object value;

    public DataProperties() {
        this.type = null;
        this.value = null;
    }

    public DataProperties(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    /**
     * @return Returns the type of the data
     */
    public String getType() {
        return this.type;
    }

    /**
     * @return Return the value of the data
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * sets the type field of the data object
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * sets the value object of the data object
     */
    public void setValue(Object value) {
        this.value = value;
    }

    public String toString() {
        return "DataProps {Type: "+getType() + ", Value ("+getValue().getClass()+"): "+ getValue().toString()+"}";
    }
}
