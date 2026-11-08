package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

/** [VirtualType] parsing and classification. Oracle: the dex format's TypeDescriptor grammar. */
class VirtualTypeTest {
    @TestFactory
    fun classificationMatchesTheDescriptorGrammar(): List<DynamicTest> = listOf(
        "I" to setOf("primitive"),
        "F" to setOf("primitive"),
        "J" to setOf("primitive", "wide"),
        "D" to setOf("primitive", "wide"),
        "Ljava/lang/String;" to setOf("reference"),
        "Ljava/lang/Long;" to setOf("reference"),
        "[[I" to setOf("reference", "array"),
        // V is legal only as a return type and bears no value.
        "V" to setOf("void"),
    ).map { (descriptor, expected) ->
        DynamicTest.dynamicTest(descriptor) {
            val type = VirtualType.of(descriptor)
            val actual = buildSet {
                if (type.isPrimitive) add("primitive")
                if (type.isWide) add("wide")
                if (type.isReference) add("reference")
                if (type.isArray) add("array")
                if (type.isVoid) add("void")
            }
            assertEquals(expected, actual)
            if (!type.isArray) assertNull(type.componentType)
        }
    }

    @Test
    fun arrayTypeExposesComponent() {
        val arr = VirtualType.of("[[I")
        assertEquals(VirtualType.of("[I"), arr.componentType)
        assertEquals(VirtualType.of("I"), arr.componentType?.componentType)
    }

    @Test
    fun equalityIsStructuralOnDescriptor() {
        assertEquals(VirtualType.of("Ljava/lang/Object;"), VirtualType.OBJECT)
        assertNotEquals(VirtualType.of("I"), VirtualType.of("J"))
    }

    /**
     * Must-reject inputs, each against the dex format's TypeDescriptor grammar: `'V'`, or up to 255 `[`
     * before a primitive code or `'L' FullClassName ';'`, where a FullClassName is `/`-separated non-empty
     * SimpleNames of SimpleNameChars (ASCII letters and digits, `$`, `-`, `_`, space since DEX 040, and the
     * listed non-ASCII ranges; never a lone surrogate).
     */
    @TestFactory
    fun malformedDescriptorsAreRejectedNotRepaired(): List<DynamicTest> = listOf(
        "" to "empty",
        "X" to "not a primitive code",
        "II" to "trailing junk",
        "[" to "dangling array",
        "I[" to "trailing [",
        "[I[" to "trailing [ after a component",
        "V[" to "trailing [ after void",
        "[V" to "array of void",
        "[[V" to "array of array of void",
        "L" to "L without a name or ;",
        "Ljava/lang/String" to "L without ;",
        "L;" to "empty class name",
        "L/a;" to "empty leading package segment",
        "La/;" to "empty trailing simple name",
        "La//b;" to "empty middle package segment",
        "java.lang.String" to "a Java name, not a descriptor",
        "Ljava.lang.String;" to "'.' is not a SimpleNameChar",
        "La;b;" to "';' inside the name",
        "La<b;" to "'<' is not a SimpleNameChar",
        "La>b;" to "'>' is not a SimpleNameChar",
        "La(b;" to "'(' is not a SimpleNameChar",
        "La:b;" to "':' is not a SimpleNameChar",
        "La[b;" to "'[' inside the name",
        "La\nb;" to "a control character",
        "La\u0000b;" to "NUL",
        "La\u200bb;" to "U+200B is outside every SimpleNameChar range",
        "La\u2028b;" to "U+2028 is outside every SimpleNameChar range",
        "La\uffffb;" to "U+FFFF is outside every SimpleNameChar range",
        "La\ud800b;" to "a lone surrogate",
    ).map { (descriptor, why) ->
        DynamicTest.dynamicTest("rejects ${descriptor.escaped()}: $why") {
            assertThrows<IllegalArgumentException> { VirtualType.of(descriptor) }
        }
    }

    /** The grammar's edges that are valid, so the rejection above is not achieved by refusing too much. */
    @TestFactory
    fun everySimpleNameCharClassIsAccepted(): List<DynamicTest> = listOf(
        "La/b\$c-d_e0;",
        "La b;",
        "La\u00a0b;",
        "L\u00e9t\u00e9;",
        "La\u2000b;",
        "La\u2010b;",
        "La\u202fb;",
        "La\ud7ffb;",
        "La\ue000b;",
        "La\uffefb;",
        "L\ud83d\ude00;",
        "[[La/b;",
    ).map { descriptor ->
        DynamicTest.dynamicTest("accepts ${descriptor.escaped()}") {
            assertEquals(descriptor, VirtualType.of(descriptor).descriptor)
        }
    }

    private fun String.escaped(): String =
        map { if (it.code in 0x21..0x7e) it.toString() else "\\u%04x".format(it.code) }.joinToString("")

    @Test
    fun arrayDescriptorsAreLimitedTo255Dimensions() {
        // Dex format, TypeDescriptor: an array descriptor has at most 255 leading `[`.
        assertTrue(VirtualType.of("[".repeat(255) + "I").isArray)
        assertThrows<IllegalArgumentException> { VirtualType.of("[".repeat(256) + "I") }
        assertThrows<IllegalArgumentException> { VirtualType.of("[".repeat(256) + "Ljava/lang/String;") }
    }
}
