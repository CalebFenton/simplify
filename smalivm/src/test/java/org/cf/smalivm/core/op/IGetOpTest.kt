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
import org.junit.jupiter.api.Test

/** Behavioral coverage added for Task 19. The retired oracle test only pinned its placeholder mocks. */
class IGetOpTest {

    @Test
    fun freshObjectsExposeDeclaredTypeDefaults() {
        CoreTester.assertReturns(CLASS_NAME, "getIntFromFreshObject()I", Value.Known(0, type("I")), register = 1)
        CoreTester.assertReturns(CLASS_NAME, "getLongFromFreshObject()J", Value.Known(0L, type("J")), register = 1)
        CoreTester.assertReturns(
            CLASS_NAME,
            "getStringFromFreshObject()Ljava/lang/String;",
            Value.Known(null, type("Ljava/lang/String;")),
            register = 1,
        )
    }

    @Test
    fun readsBackNarrowWideAndReferenceWrites() {
        CoreTester.assertReturns(CLASS_NAME, "putThenGetInt()I", Value.Known(0x42, type("I")), register = 2)
        CoreTester.assertReturns(CLASS_NAME, "putThenGetLong()J", Value.Known(0x2aL, type("J")), register = 1)
        CoreTester.assertReturns(
            CLASS_NAME,
            "putThenGetString()Ljava/lang/String;",
            Value.Known("do not pray for an easy life", type("Ljava/lang/String;")),
            register = 2,
        )
    }

    @Test
    fun literalZeroAndPrimitiveTaggedReceiversAreDefiniteNull() {
        assertDefiniteNull("getIntFromNullReceiver()I")
        assertDefiniteNull("getIntFromPrimitiveTaggedReceiver()I")
    }

    @Test
    fun resolutionPrecedesTheReceiverNullCheck() {
        val graph = CoreTester.execute(CLASS_NAME, "getFieldOfUnknownClassOnNullReceiver()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoClassDefFoundError;"), CoreTester.escapingTypes(graph))
        assertFalse(graph.wasReached(3), "resolution failure is definite; the iget must not continue")
    }

    @Test
    fun aMissingMemberThrowsNoSuchFieldError() {
        val graph = CoreTester.execute(CLASS_NAME, "getMissingField()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoSuchFieldError;"), CoreTester.escapingTypes(graph))
        assertFalse(graph.wasReached(4), "a definite missing member must not continue")
    }

    @Test
    fun anInstanceReadCannotResolveAStaticOnlyField() {
        val graph = CoreTester.execute(CLASS_NAME, "getStaticFieldWithInstanceOpcode()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoSuchFieldError;"), CoreTester.escapingTypes(graph))
        assertFalse(graph.wasReached(4), "static-only declarations are absent from instance resolution")
        assertEquals(SideEffectLevel.NONE, graph.sideEffectLevel(2))
    }

    @Test
    fun anUnknownReceiverPreservesTheNormalAndNullOutcomesWithoutInventingAFieldTarget() {
        val graph = CoreTester.execute(CLASS_NAME, "getIntThroughUnknownReceiver(Liget_test;)I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph))
        assertTrue(graph.outcomes.contains(Outcome.Returned(Value.Unknown(type("I")))))
        assertEquals(SideEffectLevel.NONE, graph.methodSideEffectLevelOverExploredPaths())
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
    fun inheritedAndShadowedFieldsResolveToTheirDefiningCells() {
        CoreTester.assertReturns(
            CLASS_NAME,
            "putInheritedThroughChildGetThroughParent()I",
            Value.Known(7, type("I")),
            register = 2,
        )
        CoreTester.assertReturns(
            CLASS_NAME,
            "putShadowedIntReadsParentsCellNotChildsLong()I",
            Value.Known(7, type("I")),
            register = 1,
        )
    }

    @Test
    fun resultWidthComesFromTheResolvedFieldAndOnlyTheReceiverIsRead() {
        val narrow = CoreTester.execute(CLASS_NAME, "getIntFromFreshObject()I")
        assertEquals(CompletionStatus.Complete, narrow.completion())
        assertEquals(setOf(0), narrow.registersRead(2))
        assertEquals(setOf(1), narrow.registersAssigned(2))
        assertEquals(SideEffectLevel.NONE, narrow.sideEffectLevel(2))

        val wide = CoreTester.execute(CLASS_NAME, "getLongFromFreshObject()J")
        assertEquals(CompletionStatus.Complete, wide.completion())
        assertEquals(setOf(0), wide.registersRead(2))
        assertEquals(setOf(1, 2), wide.registersAssigned(2))
    }

    private fun assertDefiniteNull(method: String) {
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

    private fun type(descriptor: String) = VirtualType.of(descriptor)

    private companion object {
        private const val CLASS_NAME = "Liget_test;"
    }
}
