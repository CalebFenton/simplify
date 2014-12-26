package org.cf.smalivm.type;

import org.cf.util.SmaliClassUtils;

public class TypeUtil {

    @Deprecated
    public static String getValueType(Object value) {
        String javaTypeName;
        if (value == null) {
            return null;
        } else if (value instanceof LocalType) {
            javaTypeName = ((LocalType) value).getName();
        } else {
            javaTypeName = value.getClass().getName();
        }
        String smaliTypeName = SmaliClassUtils.javaClassToSmali(javaTypeName);

        return smaliTypeName;
    }

    @Deprecated
    public static String getUnboxedType(Object value) {
        return getUnboxedType(getValueType(value));
    }

    @Deprecated
    public static String getUnboxedType(String type) {
        String unboxedType = SmaliClassUtils.smaliWrapperToSmaliPrimitive(type);
        if (null == unboxedType) {
            unboxedType = type;
        }

        return unboxedType;
    }

}
