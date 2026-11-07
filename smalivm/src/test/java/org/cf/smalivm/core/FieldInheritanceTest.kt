package org.cf.smalivm.core

import org.junit.jupiter.api.Test

/** Port of the three formerly commented `TestFieldInheritance` behaviors. */
class FieldInheritanceTest {

    @Test
    fun parentInitializedAndAccessible() {
        assertInt("getParentFieldThroughChild()I", 3)
        assertInt("getParentFieldThroughParent()I", 3)
    }

    @Test
    fun grandparentInitializedAndAccessible() {
        assertInt("getGrandparentFieldThroughChild()I", 4)
        assertInt("getGrandparentFieldThroughParent()I", 4)
        assertInt("getGrandparentFieldThroughGrandparent()I", 4)
    }

    @Test
    fun grandparentLiteralInitializedAndAccessible() {
        assertInt("getIntLiteralThroughChild()I", 5)
        assertInt("getIntLiteralThroughParent()I", 5)
        assertInt("getIntLiteralThroughGrandparent()I", 5)
    }

    private fun assertInt(method: String, expected: Int) =
        CoreTester.assertReturns(CLASS_NAME, method, Value.Known(expected, VirtualType.of("I")))

    private companion object {
        private const val CLASS_NAME = "Lchild_class;"
    }
}
