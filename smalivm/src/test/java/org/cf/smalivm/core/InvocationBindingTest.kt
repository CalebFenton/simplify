package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InvocationBindingTest {
    @Test
    fun everyPrimitiveBindsWithItsExactDeclaredRawTypeAndWidePosition() {
        val arguments = listOf(
            known(1, "I"),
            known(0x101, "I"),
            known(65, "I"),
            known(0x10001, "I"),
            known(42, "I"),
            known(9L, "J"), Value.Top,
            known(3.0f, "F"),
            known(4.0, "D"), Value.Top,
            known(0, "I"),
        )
        val bindings = bindings(arguments)
        assertEquals(listOf(0, 1, 2, 3, 4, 5, 7, 8, 10), bindings.map { it.register })
        assertEquals(
            listOf(
                Value.Known(true, type("Z")),
                Value.Known(1.toByte(), type("B")),
                Value.Known('A', type("C")),
                Value.Known(1.toShort(), type("S")),
                Value.Known(42, type("I")),
                Value.Known(9L, type("J")),
                Value.Known(3.0f, type("F")),
                Value.Known(4.0, type("D")),
                Value.Known(null, VirtualType.OBJECT),
            ),
            bindings.map { it.value },
        )
        assertTrue((bindings[7].value as Value.Known).raw is Double)
    }

    @Test
    fun unknownPrimitiveAndReferenceArgumentsKeepTheirExactTypesAndWideSlots() {
        val arguments = listOf(
            unknown("Z"), unknown("B"), unknown("C"), unknown("S"), unknown("I"),
            unknown("J"), Value.Top,
            unknown("F"),
            unknown("D"), Value.Top,
            unknown("Ljava/lang/Object;"),
        )
        val bindings = bindings(arguments)
        assertEquals(listOf(0, 1, 2, 3, 4, 5, 7, 8, 10), bindings.map { it.register })
        assertEquals(arguments.filterIndexed { index, _ -> index !in setOf(6, 9) }, bindings.map { it.value })
    }

    private fun bindings(arguments: List<Value>): List<ParameterBinding> {
        val target = MethodDescriptor(
            type("Linvoke_review_callee;"),
            "bindingMatrix",
            "(ZBCSIJFDLjava/lang/Object;)V",
        )
        val invoker = Invoker(CoreTester.classIndex)
        val preparation = invoker.prepare(Invocation(target, InvokeKind.STATIC, arguments))
        val plan = invoker.plan(preparation as InvocationPreparation.Ready)
        return (plan as InvocationPlan.RunGuest).parameterBindings
    }

    private fun known(raw: Any, descriptor: String) = Value.Known(raw, type(descriptor))
    private fun unknown(descriptor: String) = Value.Unknown(type(descriptor))
    private fun type(descriptor: String) = VirtualType.of(descriptor)
}
