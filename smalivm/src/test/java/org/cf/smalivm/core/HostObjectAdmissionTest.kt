package org.cf.smalivm.core

import java.math.BigDecimal
import java.math.BigInteger
import org.cf.smalivm.core.op.APutOp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Admission of host objects into the VM heap: mutable host values live behind a HeapRef, each path owns its
 * own copy, and immutable values keep their runtime type.
 */
internal class HostObjectAdmissionTest : DirectHostTestBase() {
    @Test
    @Suppress("DEPRECATION")
    fun reflectedMethodClonesRemainInvocableAndIsolateAccessibility() {
        val method = String::class.java.getDeclaredMethod("length")
        val aliases = arrayOf(method, method)
        val source = HostGraphState.empty().addRoots(listOf(method, aliases)).state
        val clone = cloneHostGraph(source)
        val copied = clone.objectAt(requireNotNull(source.nodeIdOf(method))) as java.lang.reflect.Method
        val copiedAliases = clone.objectAt(requireNotNull(source.nodeIdOf(aliases))) as Array<*>

        assertNotSame(method, copied)
        assertSame(copied, copiedAliases[0])
        assertSame(copied, copiedAliases[1])
        assertEquals(4, copied.invoke("text"))
        assertEquals(HostGraphComparison.Equal, HostGraphComparator().compare(source, clone))

        copied.isAccessible = true
        assertFalse(method.isAccessible)
        assertEquals(HostGraphComparison.Different, HostGraphComparator().compare(source, clone))
        val next = cloneHostGraph(clone)
        val copiedAgain = next.objectAt(requireNotNull(source.nodeIdOf(method))) as java.lang.reflect.Method
        assertTrue(copiedAgain.isAccessible)
        assertEquals(4, copiedAgain.invoke("text"))
    }

    @Test
    fun mutableSeedIsStoredBehindAHeapRef() {
        val frame = Frame.entryFrame(view("Linvoke_reflected_test;", "invokeBooleanValueOf()V"))
        val builder = StringBuilder("seed")

        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(builder, objectType)),
                HostSeed(Value.Known(builder, objectType)),
            ),
        )
        val seeded = admitted[0] as Value.Known
        val ref = seeded.raw as HeapRef

        val stored = frame.heapObject(ref) as HeapObject.HostObject
        assertSame(builder, frame.heap.hostObjectAt(ref))
        assertEquals(builderType, stored.type)
        assertTrue(stored.contents is HostGraphContents.Known)
        assertEquals(ref, (admitted[1] as Value.Known).raw)
    }

    @Test
    fun oneGraphClonePreservesNestedAliasesAndCycles() {
        class Node(var next: Node? = null)

        val view = view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;")
        val frame = Frame.entryFrame(view)
        val shared = Node()
        val first = arrayListOf(shared)
        val second = arrayListOf(shared)
        shared.next = shared
        val firstRef = frame.heap.allocateHostObject(objectType, first)
        val secondRef = frame.heap.allocateHostObject(objectType, second)
        val selected = SelectedHostCall.from(
            prepared(
                listOf(objectType, objectType),
                listOf(Value.Known(firstRef, objectType), Value.Known(secondRef, objectType)),
            ),
            frame,
            CoreTester.classIndex.classLoader,
        )
        val copiedFirst = selected.arguments[0] as ArrayList<*>
        val copiedSecond = selected.arguments[1] as ArrayList<*>
        val copiedShared = copiedFirst.single() as Node

        assertNotSame(copiedFirst, copiedSecond)
        assertSame(copiedShared, copiedSecond.single())
        assertSame(copiedShared, copiedShared.next)
        assertNotSame(shared, copiedShared)
        selected.finalize(frame)
    }

    @Test
    fun siblingCopiesDoNotShareMutableHostRoots() {
        val view = view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;")
        val parent = Frame.entryFrame(view)
        val ref = parent.heap.allocateHostObject(builderType, StringBuilder("a"))
        val first = parent.copy()
        val second = parent.copy()
        val call = prepared(listOf(builderType), listOf(Value.Known(ref, builderType)))
        val firstSelected = SelectedHostCall.from(call, first, CoreTester.classIndex.classLoader)
        val secondSelected = SelectedHostCall.from(call, second, CoreTester.classIndex.classLoader)
        val firstBuilder = firstSelected.arguments.single() as StringBuilder
        val secondBuilder = secondSelected.arguments.single() as StringBuilder
        firstBuilder.append('b')
        firstSelected.finalize(first)
        secondSelected.finalize(second)

        assertEquals("ab", firstBuilder.toString())
        assertEquals("a", secondBuilder.toString())
        assertNotSame(firstBuilder, secondBuilder)
    }

    @Test
    fun equalDistinctHostObjectsKeepDistinctSchedulerIds() {
        val heap = HeapStore(HeapIds())

        val first = heap.allocateHostObject(objectType, arrayListOf(1))
        val second = heap.allocateHostObject(objectType, arrayListOf(1))

        assertNotEquals(first, second)
    }

    @Test
    fun batchAdmissionPreservesRepeatedRootsAndAnArrayNestedInAContainer() {
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val array = intArrayOf(1, 2)
        val container = arrayListOf<Any>(array)

        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(container, objectType)),
                HostSeed(Value.Known(array, intArrayType)),
                HostSeed(Value.Known(array, objectType)),
            ),
        ).map { it as Value.Known }
        val arrayRef = admitted[1].raw as HeapRef

        assertEquals(arrayRef, admitted[2].raw)
        assertTrue(frame.heapObject(arrayRef) is HeapObject.TrackedArray)
        val selected = SelectedHostCall.from(
            prepared(
                listOf(objectType, intArrayType),
                listOf(admitted[0], admitted[1]),
            ),
            frame,
            CoreTester.classIndex.classLoader,
        )
        assertSame((selected.arguments[0] as List<*>).single(), selected.arguments[1])
        selected.finalize(frame)
    }

    @Test
    fun mutableBigNumberSubclassesUseTrackedIdentityAndSiblingIsolation() {
        // Bug class: an `is BigInteger/BigDecimal` check stores mutable subclasses directly in Value.Known.
        // Oracle: only the exact JDK immutable classes are direct; subclass fields are mutable graph state.
        val raws = listOf<MutableHostNumber>(
            MutableHostBigInteger("7"),
            MutableHostBigDecimal("7.5"),
        )
        for (raw in raws) {
            assertFalse(raw.isDirectHostImmutable(), raw.javaClass.name)
            val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
            val admitted = frame.heap.admitHostSeeds(
                listOf(HostSeed(Value.Known(raw, objectType))),
            ).single() as Value.Known
            val ref = admitted.raw as HeapRef
            val sibling = frame.copy()
            val selected = SelectedHostCall.from(
                prepared(listOf(objectType), listOf(admitted)),
                sibling,
                CoreTester.classIndex.classLoader,
            )

            (selected.arguments.single() as MutableHostNumber).note.append("-changed")
            selected.finalize(sibling)

            assertEquals("seed", (frame.heap.hostObjectAt(ref) as MutableHostNumber).note.toString())
            assertEquals("seed-changed", (sibling.heap.hostObjectAt(ref) as MutableHostNumber).note.toString())
        }
        assertTrue(BigInteger.ONE.isDirectHostImmutable())
        assertTrue(BigDecimal.ONE.isDirectHostImmutable())
    }

    @Test
    fun mutableEnumUsesTrackedIdentityAndSiblingIsolation() {
        // Bug class: every Enum is treated as immutable, so a mutable enum field leaks across sibling paths.
        // Oracle: an enum runtime class is not an exact scalar class and its instance fields are graph state.
        MutableHostEnum.VALUE.note = StringBuilder("seed")
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val admitted = frame.heap.admitHostSeeds(
            listOf(HostSeed(Value.Known(MutableHostEnum.VALUE, objectType))),
        ).single() as Value.Known
        val ref = admitted.raw as HeapRef
        val sibling = frame.copy()
        val selected = SelectedHostCall.from(
            prepared(listOf(objectType), listOf(admitted)),
            sibling,
            CoreTester.classIndex.classLoader,
        )

        (selected.arguments.single() as MutableHostEnum).note.append("-changed")
        selected.finalize(sibling)

        assertEquals("seed", (frame.heap.hostObjectAt(ref) as MutableHostEnum).note.toString())
        assertEquals("seed-changed", (sibling.heap.hostObjectAt(ref) as MutableHostEnum).note.toString())
        assertFalse(MutableHostEnum.VALUE.isDirectHostImmutable())
    }

    @Test
    fun directImmutableRuntimeTypeOverridesLooseTagForObjectArrayStore() {
        val frame = Frame.entryFrame(view("Linvoke_reflected_test;", "invokeStringBuilderAppendWithLong()V"))
        val charSequence = VirtualType.of("Ljava/lang/CharSequence;")
        val charSequenceArray = VirtualType.of("[Ljava/lang/CharSequence;")
        val arrayRef = frame.allocateArray(
            charSequenceArray,
            arrayOf(Value.Known(null, charSequence)),
        ).raw as HeapRef
        frame.assign(0, Value.Known("value", objectType))
        frame.assign(1, Value.Known(arrayRef, charSequenceArray))
        frame.assign(2, Value.Known(0, intType))
        val op = APutOp(
            0,
            "aput-object",
            0,
            1,
            2,
            true,
            CoreTester.classIndex.currentSnapshot.hierarchy,
        )

        val arm = op.execute(frame).arms.single() as OpArm.Continue
        arm.update.applyTo(frame)
        assertEquals(Value.Known("value", charSequence), frame.heap.arrayAt(arrayRef)!!.element(0))
    }
}
