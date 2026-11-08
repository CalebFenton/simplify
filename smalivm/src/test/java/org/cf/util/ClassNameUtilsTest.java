package org.cf.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import org.junit.jupiter.api.Test;

public class ClassNameUtilsTest {

    // Binary names as Class.getName() reports them (JLS 13.1, Class.getName javadoc).
    private static final Map<String, String> internalNameToBinaryName = Map.of(
            "Lthis/is/Test;", "this.is.Test",
            "[Lthis/is/Test;", "[Lthis.is.Test;",
            "I", "int",
            "B", "byte",
            "V", "void",
            "[I", "[I",
            "[[Z", "[[Z");

    @Test
    public void canConvertBinaryToInternal() {
        internalNameToBinaryName.forEach((internal, binary) -> assertEquals(internal, ClassNameUtils.binaryToInternal(binary)));
    }

    @Test
    public void canConvertInternalToBinary() {
        internalNameToBinaryName.forEach((internal, binary) -> assertEquals(binary, ClassNameUtils.internalToBinary(internal)));
    }

    @Test
    public void hostClassNamesConvertToInternal() {
        assertEquals("[[Ljava/lang/String;", ClassNameUtils.toInternal(String[][].class));
        assertEquals("[J", ClassNameUtils.toInternal(long[].class));
        assertEquals("C", ClassNameUtils.toInternal(char.class));
    }

    @Test
    public void primitiveClassesResolveOnlyForValuePrimitives() {
        assertEquals(int.class, ClassNameUtils.getPrimitiveClass("I"));
        assertEquals(boolean.class, ClassNameUtils.getPrimitiveClass("Z"));
        assertNull(ClassNameUtils.getPrimitiveClass("V"));
        assertNull(ClassNameUtils.getPrimitiveClass("[I"));
        assertNull(ClassNameUtils.getPrimitiveClass("Ljava/lang/Integer;"));
    }
}
