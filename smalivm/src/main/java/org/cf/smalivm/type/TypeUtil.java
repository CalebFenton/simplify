package org.cf.smalivm.type;

public class TypeUtil {

    public static String getValueType(Object value) {
        String result;
        if (value == null) {
            result = "null";
        } else if (value instanceof Type) {
            result = ((Type) value).getType();
        } else {
            result = value.getClass().getName();
        }

        return result;
    }

}
