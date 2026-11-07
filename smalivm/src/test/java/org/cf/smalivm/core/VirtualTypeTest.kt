package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Bug class: type tags as repairable strings (the old engine accepted and silently
 * "fixed" malformed type strings, spec §4.1). Oracle: the Dalvik type-descriptor
 * grammar (JVMS/dex spec): V Z B S C I J F D, Lfqcn;, [ prefixes.
 */
class VirtualTypeTest {
    @Test
    fun primitiveClassificationMatchesDalvikGrammar() {
        val i = VirtualType.of("I")
        assertTrue(i.isPrimitive)
        assertFalse(i.isWide)
        assertFalse(i.isReference)
        assertFalse(i.isArray)
        assertNull(i.componentType)
    }

    @Test
    fun widePrimitivesAreExactlyLongAndDouble() {
        assertTrue(VirtualType.of("J").isWide)
        assertTrue(VirtualType.of("D").isWide)
        assertFalse(VirtualType.of("I").isWide)
        assertFalse(VirtualType.of("F").isWide)
        assertFalse(VirtualType.of("Ljava/lang/Long;").isWide)
    }

    @Test
    fun classTypeIsReferenceNotPrimitive() {
        val s = VirtualType.of("Ljava/lang/String;")
        assertTrue(s.isReference)
        assertFalse(s.isPrimitive)
        assertFalse(s.isArray)
    }

    @Test
    fun arrayTypeExposesComponent() {
        val arr = VirtualType.of("[[I")
        assertTrue(arr.isArray)
        assertTrue(arr.isReference)
        assertEquals(VirtualType.of("[I"), arr.componentType)
        assertEquals(VirtualType.of("I"), arr.componentType?.componentType)
    }

    @Test
    fun equalityIsStructuralOnDescriptor() {
        assertEquals(VirtualType.of("Ljava/lang/Object;"), VirtualType.OBJECT)
        assertNotEquals(VirtualType.of("I"), VirtualType.of("J"))
    }

    @Test
    fun malformedDescriptorsAreRejectedNotRepaired() {
        // Bug class: silent repair of bad type tags.
        assertThrows<IllegalArgumentException> { VirtualType.of("") }
        assertThrows<IllegalArgumentException> { VirtualType.of("Ljava/lang/String") } // missing ;
        assertThrows<IllegalArgumentException> { VirtualType.of("X") }                 // not a primitive code
        assertThrows<IllegalArgumentException> { VirtualType.of("[") }                 // dangling array
        assertThrows<IllegalArgumentException> { VirtualType.of("II") }                // trailing junk
        assertThrows<IllegalArgumentException> { VirtualType.of("java.lang.String") }  // Java name, not descriptor
        assertThrows<IllegalArgumentException> { VirtualType.of("Ljava.lang.String;") } // dotted body, not '/'-separated
    }

    @Test
    fun voidIsValidButNeitherPrimitiveValueNorReference() {
        // Dalvik grammar: V is only legal as a return type; it is not a value-bearing type.
        val v = VirtualType.of("V")
        assertFalse(v.isReference)
        assertFalse(v.isWide)
        assertTrue(v.isVoid)
    }
}
