package org.cf.smalivm.type;

public class TypeUtil {

    public static String getValueType(Object value) {
        String result = null;
        if (value instanceof Type) {
            result = ((Type) value).getType();
        } else {
            result = value.getClass().getName();
        }
    
        return result;
    }

}
