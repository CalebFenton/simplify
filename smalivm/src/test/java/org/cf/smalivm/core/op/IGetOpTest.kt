package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/** `iget*`. Expected behaviour follows `docs/DALVIK-SEMANTICS.md#fields`. */
class IGetOpTest {

    /** `new-instance` seeds every instance field with its type's default ([JLS] §12.5). */
    @TestFactory
    fun aFreshObjectHoldsEachFieldTypesDefault(): List<DynamicTest> = listOf(
        returns("getIntFromFreshObject()I", Value.Known(0, type("I")), register = 1),
        returns("getLongFromFreshObject()J", Value.Known(0L, type("J")), register = 1),
        returns(
            "getStringFromFreshObject()Ljava/lang/String;",
            Value.Known(null, type("Ljava/lang/String;")),
            register = 1,
        ),
    )

    /**
     * A read returns the cell its resolved field names. Resolution walks the superclass chain ([JVMS] §5.4.3.2),
     * so an inherited field written through the child reads back through the parent, and a parent `I` field is a
     * different cell from a child `J` field of the same name.
     */
    @TestFactory
    fun aReadReturnsTheResolvedFieldsCell(): List<DynamicTest> = listOf(
        returns("putThenGetInt()I", Value.Known(0x42, type("I")), register = 2),
        returns("putThenGetLong()J", Value.Known(0x2aL, type("J")), register = 1),
        returns(
            "putThenGetString()Ljava/lang/String;",
            Value.Known("do not pray for an easy life", type("Ljava/lang/String;")),
            register = 2,
        ),
        returns("putInheritedThroughChildGetThroughParent()I", Value.Known(7, type("I")), register = 2),
        returns("putShadowedIntReadsParentsCellNotChildsLong()I", Value.Known(7, type("I")), register = 1),
    )

    /** A literal zero and a primitive-tagged register both read as a null receiver. */
    @TestFactory
    fun aReceiverThatReadsAsNullThrowsTheOnlyOutcome(): List<DynamicTest> =
        listOf("getIntFromNullReceiver()I", "getIntFromPrimitiveTaggedReceiver()I").map { method ->
            DynamicTest.dynamicTest(method) {
                // ART's `ThrowNullPointerExceptionForFieldAccess` text over the resolved field and the running method.
                CoreTester.testThrows(
                    CLASS_NAME, method,
                    exceptionType = "Ljava/lang/NullPointerException;",
                    message = "Attempt to read from field 'int iget_test.myInt' on a null object reference in " +
                        "method 'int iget_test.${method.substringBefore('(')}()'",
                )
                val graph = CoreTester.execute(CLASS_NAME, method)
                assertEquals(CompletionStatus.Complete, graph.completion())
                assertEquals(
                    listOf("Ljava/lang/NullPointerException;"),
                    CoreTester.escapingTypes(graph),
                    "the null read has one feasible outcome, not an existence-only throw assertion",
                )
                assertFalse(graph.wasReached(3), "a definite NPE must not also continue")
                assertEquals(0, graph.nodePile(1).single().children.size, "a definite escaping throw has no sibling")
            }
        }

    /**
     * Resolution precedes the receiver null check, and a static-only declaration is absent from instance
     * resolution (`docs/DALVIK-SEMANTICS.md#resolution`).
     */
    @TestFactory
    fun aResolutionFailureThrowsBeforeTheRead(): List<DynamicTest> = listOf(
        resolutionFailure(
            "getFieldOfUnknownClassOnNullReceiver()I", "Ljava/lang/NoClassDefFoundError;", getAt = 1, nextAt = 3,
        ),
        resolutionFailure("getMissingField()I", "Ljava/lang/NoSuchFieldError;", getAt = 2, nextAt = 4),
        resolutionFailure("getStaticFieldWithInstanceOpcode()I", "Ljava/lang/NoSuchFieldError;", getAt = 2, nextAt = 4),
    )

    @Test
    fun anUnknownReceiverPreservesTheNormalAndNullOutcomesWithoutInventingAFieldTarget() {
        val graph = CoreTester.execute(CLASS_NAME, "getIntThroughUnknownReceiver(Liget_test;)I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph))
        assertTrue(graph.outcomes.contains(Outcome.Returned(Value.Unknown(type("I")))))
        assertEquals(SideEffectLevel.NONE, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun anUnknownReceiverAssignsOnlyTheNormalSuccessorWhenNpeIsCaught() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "unknownReceiverPreservesDestinationOnCaughtNpe(Liget_test;)I",
        )
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph), "the local handler catches the NPE")
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }
        assertEquals(2, returned.size, "both the normal and caught-exception paths must return")
        assertEquals(
            mapOf(Value.Unknown(type("I")) to 1, Value.Known(42, type("I")) to 1),
            returned.groupingBy { it }.eachCount(),
            "only the normal successor receives Unknown; the handler keeps the prior destination",
        )
        assertEquals(
            listOf(4, 5),
            graph.nodePile(2).single().children.map { it.address }.sorted(),
            "the unknown receiver must fork to both fall-through and the local NPE handler",
        )
        assertTrue(graph.wasReached(5), "the NPE handler must execute; otherwise Known(42) is vacuous")
    }

    @Test
    fun resultWidthComesFromTheResolvedFieldAndOnlyTheReceiverIsRead() {
        val narrow = CoreTester.execute(CLASS_NAME, "getIntFromFreshObject()I")
        assertEquals(CompletionStatus.Complete, narrow.completion())
        assertEquals(setOf(0), narrow.registersRead(2))
        assertEquals(setOf(1), narrow.registersAssigned(2))
        assertEquals(SideEffectLevel.NONE, narrow.explored.sideEffectLevel(2))

        val wide = CoreTester.execute(CLASS_NAME, "getLongFromFreshObject()J")
        assertEquals(CompletionStatus.Complete, wide.completion())
        assertEquals(setOf(0), wide.registersRead(2))
        assertEquals(setOf(1, 2), wide.registersAssigned(2))
    }

    private fun returns(method: String, expected: Value, register: Int): DynamicTest =
        DynamicTest.dynamicTest("$method -> $expected") {
            CoreTester.assertReturns(CLASS_NAME, method, expected, register = register)
        }

    private fun resolutionFailure(method: String, error: String, getAt: Int, nextAt: Int): DynamicTest =
        DynamicTest.dynamicTest("$method -> $error") {
            val graph = CoreTester.execute(CLASS_NAME, method)
            assertEquals(CompletionStatus.Complete, graph.completion())
            assertEquals(listOf(error), CoreTester.escapingTypes(graph))
            assertFalse(graph.wasReached(nextAt), "a definite resolution failure must not continue")
            assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(getAt))
        }

    private fun type(descriptor: String) = VirtualType.of(descriptor)

    private companion object {
        private const val CLASS_NAME = "Liget_test;"
    }
}
