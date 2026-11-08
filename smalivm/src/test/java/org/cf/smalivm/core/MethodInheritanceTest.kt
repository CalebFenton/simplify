package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/** Invocation through a class hierarchy: abstract methods, inherited methods and `invoke-super`. */
class MethodInheritanceTest {
    @Test
    fun canInvokeAbstractMethodThroughParentReference() {
        CoreTester.assertReturns(CHILD, "callsAbstractMethod()Ljava/lang/String;", string("abstract?"))
    }

    @Test
    fun canInvokeImplementedAbstractMethod() {
        CoreTester.assertReturns(CHILD, "abstractMethod()Ljava/lang/String;", string("abstract?"))
    }

    @Test
    fun canInvokeParentMethodOfChild() {
        CoreTester.assertReturns(CHILD, "callsParentMethod()Ljava/lang/String;", string("parentMethod"))
    }

    @Test
    fun executingAbstractMethodHasNoRunnableBody() {
        assertThrows(IllegalArgumentException::class.java) {
            CoreTester.execute("Lparent_class;", "abstractMethod()Ljava/lang/String;")
        }
    }

    @Test
    fun invokeSuperStartsAboveTheCallerDefiningClass() {
        CoreTester.assertReturns(CHILD, "callsSuperParentMethodFromChild()Ljava/lang/String;", string("parentMethod"))
    }

    private fun string(raw: String) = Value.Known(raw, VirtualType.of("Ljava/lang/String;"))

    private companion object {
        const val CHILD = "Lchild_class;"
    }
}
