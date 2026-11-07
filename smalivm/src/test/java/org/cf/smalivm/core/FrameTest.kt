package org.cf.smalivm.core

import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Bug classes (audit + spec §4.2): (a) one string-keyed map holding registers, parameters and
 * pseudo-slots, so a "register" and a special slot could collide; (b) no wide-pair invariant, so
 * the high half of a long could be read as an independent value; (c) shared mutable state between
 * a normal and a speculative exceptional successor, so a speculative path saw the other path's
 * writes; (d) a consumer-facing read (`ExecutionState.readRegister`) forging liveness that only an
 * op driving the frame directly should record. Oracle: Dalvik register semantics (wide values
 * occupy a register pair), spec §4.2's consume/clear slot lifecycle, and StateContracts.kt's KDoc
 * on `readRegister`/`peekRegister` (neither records liveness).
 */
class FrameTest {
    private val classIndex = ClassIndex.open(File("src/test/resources/smali"))
    private val int = VirtualType.of("I")
    private val long = VirtualType.of("J")
    private val runtimeException = VirtualType.of("Ljava/lang/RuntimeException;")

    private fun view(descriptor: String) =
        classIndex.requireMethodView("Lspine/spine;", descriptor)

    private fun frame(descriptor: String = "moveChain()I") = Frame.entryFrame(view(descriptor))

    @Test
    fun refinedThrowableRejectsKnownNonReferenceStorage() {
        assertThrows(IllegalArgumentException::class.java) {
            FrameValue.refined(Value.Known(1, int), ThrowableDomain.any())
        }
    }

    @Test
    fun refinedThrowableRejectsNullStorage() {
        assertThrows(IllegalArgumentException::class.java) {
            FrameValue.refined(
                Value.Known(null, VirtualType.of("Ljava/lang/Throwable;")),
                ThrowableDomain.any(),
            )
        }
    }

    @Test
    fun atomicThrowableValueSurvivesRegistersSlotsAndFrameCopy() {
        val value = Value.Unknown(VirtualType.of("Ljava/lang/Throwable;"))
        val refined = FrameValue.refined(value, ThrowableDomain.any())
        val original = frame()
        original.assignFrameValue(0, refined)
        original.setResult(refined)
        original.setCaught(refined)

        val copied = original.copy()

        assertEquals(refined, copied.readFrameValue(0))
        assertEquals(refined, copied.takeResultFrameValue())
        assertEquals(refined, copied.takeCaughtFrameValue())
        assertSame(value, copied.peek(0), "the public projection must preserve the Value instance")
        assertEquals(refined, original.readFrameValue(0), "consuming copied slots must not change the source")
    }

    @Test
    fun frameValueJoinRetainsOnlyACommonRefinement() {
        val value = Value.Unknown(VirtualType.of("Ljava/lang/Throwable;"))
        val any = FrameValue.refined(value, ThrowableDomain.any())
        val exception = FrameValue.refined(
            value,
            ThrowableDomain.constrainedTo(
                VirtualType.of("Ljava/lang/Exception;"),
                classIndex.throwableHierarchy,
            ),
        )

        assertTrue(joinFrameValues(any, any, classIndex) is FrameValue.RefinedThrowable)
        assertTrue(joinFrameValues(any, exception, classIndex) is FrameValue.Plain)
        assertTrue(joinFrameValues(any, FrameValue.plain(value), classIndex) is FrameValue.Plain)
    }

    @Test
    fun publicSessionEntryStripsInternalThrowableRefinements() {
        val method = view("moveChain()I")
        val value = Value.Unknown(VirtualType.of("Ljava/lang/Throwable;"))
        val refined = FrameValue.refined(value, ThrowableDomain.any())
        val source = Frame.entryFrame(method)
        source.assignFrameValue(0, refined)
        source.setResult(refined)
        source.setCaught(refined)

        val session = VmServices(classIndex).newSession(method, entryFrame = source)
        val entry = session.frontier.single().frame

        assertTrue(source.peekFrameValue(0) is FrameValue.RefinedThrowable)
        assertTrue(entry.peekFrameValue(0) is FrameValue.Plain)
        assertTrue(entry.takeResultFrameValue() is FrameValue.Plain)
        assertTrue(entry.takeCaughtFrameValue() is FrameValue.Plain)
        assertSame(value, entry.peek(0))
    }

    @Test
    fun assignedRegisterReadsBack() {
        // Bug class: the base register-store identity failing -- a write landing in the wrong
        // slot, or peek/read disagreeing about a register that was actually assigned. Oracle:
        // Dalvik registers are ordinary read/write cells; this is the base case every other
        // invariant in this file builds on.
        val f = frame()
        f.assign(0, Value.Known(5, int))
        assertEquals(Value.Known(5, int), f.peek(0))
        assertEquals(Value.Known(5, int), f.read(0))
    }

    @Test
    fun unassignedRegisterPeeksNullAndReadsTop() {
        // Contract pinned in StateContracts.kt: peek is nullable, read is total.
        val f = frame()
        assertNull(f.peek(1))
        assertEquals(Value.Top, f.read(1))
    }

    @Test
    fun registersAssignedTracksOnlyWrites() {
        // Bug class: registersAssigned() reporting a register that was only read, or omitting
        // one assign() actually wrote -- either would mislead a consumer (e.g. Plan 14's
        // dead-code strategy) about which registers this frame's op actually produced. Oracle:
        // RegisterFile's contract (StateContracts.kt) -- registersAssigned is exactly the write
        // set.
        val f = frame()
        assertEquals(emptySet<Int>(), f.registersAssigned())
        f.assign(2, Value.Known(1, int))
        assertEquals(setOf(2), f.registersAssigned())
    }

    /**
     * The op -> scheduler side-effect channel (spec §4.5), added in Plan 7 Task 8 fix round 1 for `aput`'s
     * unmodellable-store rows. Two properties, both of which the op-level tests could NOT pin individually,
     * which is why they are here rather than only in `APutOpTest`:
     *
     *  - **Aggregation errs upward** (spec §4.5's own words). No op in this build calls `recordSideEffect`
     *    twice on one frame, so replacing the max with a plain assignment killed **nothing** in the
     *    end-to-end battery. It is still a real guard rather than speculation: Plan 9's `iput`/`invoke` will
     *    have several rows per op, and a `WEAK` landing after a `STRONG` would silently downgrade the claim
     *    the trust gate is built on. Pinned directly instead of left to a future task to discover.
     *  - **A copy does not inherit a declared level.** Also unkillable end-to-end on its own, because
     *    `ExecutionSession` clears the level before copying any successor frame -- the two protections are
     *    deliberately redundant, so breaking either alone is invisible. That redundancy is the point (the
     *    failure it prevents is `sideEffectLevel(a)` answering `STRONG` for every address after the first
     *    unprovable store, which no value assertion would notice), and this test is what makes the *second*
     *    protection individually falsifiable.
     */
    @Test
    fun aDeclaredSideEffectErrsUpwardAndIsClearedWhenTaken() {
        val f = frame()
        f.recordSideEffect(SideEffectLevel.STRONG)
        f.recordSideEffect(SideEffectLevel.WEAK)
        assertEquals(
            SideEffectLevel.STRONG, f.takeSideEffectLevel(),
            "a lower level must not downgrade a higher one -- effects err upward (spec §4.5)",
        )
        assertEquals(
            SideEffectLevel.NONE, f.takeSideEffectLevel(),
            "and the level is a ONE-OP handoff, so taking it clears it",
        )
    }

    @Test
    fun aFrameCopyDoesNotInheritADeclaredSideEffect() {
        val f = frame()
        f.recordSideEffect(SideEffectLevel.STRONG)
        assertEquals(
            SideEffectLevel.NONE, f.copy().takeSideEffectLevel(),
            "a successor's frame must not inherit its parent op's effect declaration",
        )
        assertEquals(
            SideEffectLevel.STRONG, f.takeSideEffectLevel(),
            "...and copying must not consume the original's either",
        )
    }

    @Test
    fun readRecordsLivenessButPeekDoesNot() {
        val f = frame()
        f.assign(0, Value.Known(5, int))
        assertEquals(emptySet<Int>(), f.registersRead())
        f.peek(0)
        assertEquals(emptySet<Int>(), f.registersRead(), "peek must not record liveness")
        f.read(0)
        assertEquals(setOf(0), f.registersRead())
    }

    @Test
    fun readRegisterFacadeIsTotalButDoesNotRecordLiveness() {
        // Bug class (d): the ExecutionState facade forging liveness on a mere consumer read. If
        // readRegister ever delegates to the liveness-recording Frame.read (as a first-draft
        // implementation of this file's own production code did), every debugger/analysis read
        // through ExecutionState would corrupt Plan 14's dead-code liveness signal --
        // indistinguishable from an op actually consuming the register. Oracle:
        // StateContracts.kt's KDoc on ExecutionState.readRegister -- "this read does not record
        // liveness ... not on consumer reads" -- while still being total (Top for an unassigned
        // register), unlike peekRegister.
        val f = frame()
        f.assign(0, Value.Known(5, int))
        val state: ExecutionState = f

        assertEquals(Value.Known(5, int), state.readRegister(0))
        assertEquals(Value.Top, state.readRegister(1), "readRegister is total: Top, not null, for unassigned")
        assertNull(state.peekRegister(1))
        assertEquals(emptySet<Int>(), f.registersRead(), "ExecutionState.readRegister must not record liveness")

        f.read(1)
        assertEquals(setOf(1), f.registersRead(), "only the concrete Frame.read records liveness")
    }

    @Test
    fun wideValueOccupiesTwoRegistersAndTheHighHalfIsNotReadable() {
        // Bug class (b): reading the high half of a wide pair as an independent value.
        val f = frame("returnWide()J")
        f.assign(0, Value.Known(42L, long))
        assertEquals(Value.Known(42L, long), f.read(0))
        assertEquals(Value.Top, f.read(1), "the high half of a wide pair is not an independent value")
    }

    @Test
    fun assigningOverTheHighHalfInvalidatesTheLowHalf() {
        // Bug class (b): overwriting the HIGH register of an existing wide pair must invalidate
        // the low register too, so the low half can never be read back as a stale long once its
        // other half has been overwritten independently. (See
        // assigningOverTheLowHalfInvalidatesTheHighHalf for the other direction.) Oracle: Dalvik
        // wide-pair semantics -- a register pair is only meaningful as a whole.
        val f = frame("returnWide()J")
        f.assign(0, Value.Known(42L, long))
        f.assign(1, Value.Known(7, int))
        assertEquals(Value.Known(7, int), f.read(1))
        assertEquals(Value.Top, f.read(0), "clobbering half a wide pair must not leave a readable long")
    }

    @Test
    fun assigningOverTheLowHalfInvalidatesTheHighHalf() {
        // Bug class (b), the other direction: overwriting the LOW register of an existing wide
        // pair must invalidate the reserved high register too, so it doesn't keep reporting
        // itself as "the high half of a J that's no longer there." Oracle: Dalvik wide-pair
        // semantics -- a register pair's meaning is defined by whatever currently occupies the
        // low register.
        val f = frame("returnWide()J")
        f.assign(0, Value.Known(42L, long))
        f.assign(0, Value.Known(3, int))
        assertEquals(Value.Known(3, int), f.read(0))
        assertEquals(Value.Top, f.read(1), "overwriting the low half must invalidate the reserved high register")
    }

    @Test
    fun copyIsIndependentOfItsSource() {
        // Bug class (c): the shared-mutated-state defect that let a speculative exceptional path
        // observe the normal path's writes.
        val f = frame()
        f.assign(0, Value.Known(1, int))
        val child = f.copy()
        child.assign(0, Value.Known(2, int))
        child.assign(1, Value.Known(3, int))
        assertEquals(Value.Known(1, int), f.read(0), "parent must not see the child's write")
        assertNull(f.peek(1))
        assertEquals(Value.Known(2, int), child.read(0))
    }

    @Test
    fun copyIsolatesLivenessAndConsumeSlotsFromItsSource() {
        // Bug class (c), continued: copy() must isolate the liveness set and the four consume
        // slots, not just register values -- exactly the state a speculative exceptional
        // successor must not share with (or corrupt for) the normal-path frame it forked from.
        // Oracle: spec §4.2's per-node frame copy, applied to the same shared-mutated-state
        // defect copyIsIndependentOfItsSource pins for registers.
        val f = frame()
        f.setResult(Value.Known(9, int))
        val child = f.copy()

        child.read(0)
        assertEquals(emptySet<Int>(), f.registersRead(), "the child's read must not appear in the parent's liveness")

        assertEquals(Value.Known(9, int), child.takeResult(), "child must see the result pending at copy time")
        assertEquals(
            Value.Known(9, int),
            f.takeResult(),
            "consuming the child's copy of a slot must not clear the parent's own copy",
        )
    }

    @Test
    fun copyPreservesWidePairs() {
        // Bug class: copy() reconstructing registers in a way that loses the WideHigh marker
        // (e.g. copying only Held slots, or re-deriving slots from raw values instead of copying
        // the RegisterSlot array itself) so the child's high register becomes independently
        // readable. Oracle: the same Dalvik wide-pair semantics as
        // wideValueOccupiesTwoRegistersAndTheHighHalfIsNotReadable, now required to survive
        // copy().
        val f = frame("returnWide()J")
        f.assign(0, Value.Known(42L, long))
        val child = f.copy()
        assertEquals(Value.Known(42L, long), child.read(0))
        assertEquals(Value.Top, child.read(1))
    }

    @Test
    fun slotsAreProducedThenConsumedOnce() {
        // spec §4.2: a slot is produced, moved once, then dead.
        val f = frame()
        assertNull(f.takeResult())
        f.setResult(Value.Known(1, int))
        assertEquals(Value.Known(1, int), f.takeResult())
        assertNull(f.takeResult(), "a consumed slot is dead")

        f.setCaught(Value.Known("boom", runtimeException))
        assertEquals(Value.Known("boom", runtimeException), f.takeCaught())
        assertNull(f.takeCaught())
    }

    @Test
    fun returnAndThrownSlotsRecordPathResult() {
        // Bug class: unlike result/caught, these two slots hold a path's FINAL outcome (spec
        // §4.2) and must not be cleared on read -- copying the take-and-clear lifecycle onto
        // returnValue/thrown would make a second read silently lose the path's outcome, and
        // aliasing the two slots onto one field would let a return value bleed into a throw or
        // vice versa. Oracle: spec §4.2's distinction between the move-once slot pair
        // (result/caught) and the path-outcome slot pair (returnValue/thrown).
        val f = frame()
        assertNull(f.returnValueOrNull())
        assertNull(f.thrownOrNull())
        f.setReturnValue(Value.Known(5, int))
        assertEquals(Value.Known(5, int), f.returnValueOrNull())
        f.setThrown(Value.Known("boom", runtimeException))
        assertEquals(Value.Known("boom", runtimeException), f.thrownOrNull())
        assertEquals(
            Value.Known(5, int),
            f.returnValueOrNull(),
            "reading returnValue again (after thrown was also set) must not clear or alias it",
        )
    }

    @Test
    fun entryFrameSeedsParametersUnknownAndLocalsEmpty() {
        val f = frame("throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;")
        assertEquals(Value.Unknown(runtimeException), f.peek(1), "a parameter's value is not known at entry")
        assertNull(f.peek(0), "locals start unassigned")
        assertEquals(emptySet<Int>(), f.registersAssigned(), "seeding parameters is not an assignment by the method")
    }

    @Test
    fun entryFrameSeedsNonTrailingWideParameterAcrossItsPair() {
        // Bug class: an off-by-one in MethodView's parameter-width arithmetic, or in
        // Frame.assign's wide-register reservation, landing String on v5 instead of v6 --
        // silently misaligning every parameter after a non-trailing wide one. Oracle: Dalvik
        // semantics -- parameters occupy the highest registers, and a wide type consumes two of
        // them. On instanceWideParams(JLjava/lang/String;I)I (.registers 8), parameter widths
        // are this=1, J=2, String=1, I=1 (total 5), so firstParameterRegister = 8 - 5 = 3 and
        // slots are this=v3, J=v4 (pair v4/v5), String=v6, I=v7 -- already pinned for MethodView
        // by MethodViewTest.instanceMethodWithNonTrailingWideParameterPinsRegisterLayout; this
        // test pins that Frame.entryFrame seeds registers consistently with that layout.
        val f = frame("instanceWideParams(JLjava/lang/String;I)I")
        val spine = VirtualType.of("Lspine/spine;")
        val string = VirtualType.of("Ljava/lang/String;")

        assertEquals(Value.Unknown(spine), f.peek(3), "this")
        assertEquals(Value.Unknown(long), f.peek(4), "low half of the wide J parameter")
        assertNull(f.peek(5), "the high half of a wide parameter is not independently peekable")
        assertEquals(Value.Top, f.read(5), "the high half of a wide parameter is not independently readable")
        assertEquals(Value.Unknown(string), f.peek(6), "String shifted by 2 registers past the wide parameter")
        assertEquals(Value.Unknown(int), f.peek(7))

        assertNull(f.peek(0), "locals start unassigned")
        assertNull(f.peek(1), "locals start unassigned")
        assertNull(f.peek(2), "locals start unassigned")
        assertEquals(emptySet<Int>(), f.registersAssigned(), "seeding parameters is not an assignment by the method")
    }

    @Test
    fun outOfRangeRegisterIsRejected() {
        // Bug class: silently clamping, wrapping, or returning Top/null for an out-of-range
        // register instead of rejecting it -- a decode bug that computes a bad register index
        // would then corrupt state silently instead of surfacing loudly. Oracle: the design
        // spec's "unrepresentable -> abort" soundness direction, applied via Kotlin's require()
        // convention for caller-provided invalid arguments; all three read/write entry points
        // must agree.
        val f = frame("returnFive()I") // .registers 1
        assertThrows(IllegalArgumentException::class.java) { f.assign(5, Value.Known(1, int)) }
        assertThrows(IllegalArgumentException::class.java) { f.read(5) }
        assertThrows(IllegalArgumentException::class.java) { f.peek(5) }
    }

    @Test
    fun aFramesCopyIsolatesHeapWritesAsWellAsRegisterWrites() {
        // Frame.copy() is the ONE primitive that isolates a forked successor (its own KDoc invariant 3).
        // Before the heap existed that meant registers and slots; an array write must be isolated by the
        // same call, or `commit`'s per-successor copy would leak heap mutation between siblings -- the
        // audit's shared-mutated-state defect with a new subject.
        val f = frame("returnFive()I")
        val array = f.allocateArray(VirtualType.of("[I"), 1)
        val ref = array.raw as HeapRef
        val forked = f.copy()
        forked.writeArrayElement(ref, 0, Value.Known(7, int))
        assertEquals(Value.Known(0, int), f.arrayAt(ref)!!.element(0))
    }

    @Test
    fun opArmReconciliationKeepsTheStaticOwnerThatTheCallPartitionMergeKeeps() {
        // Oracle: `ClassInitStore.merge` is the one rule for joining static cells across paths. A host
        // projection every arm holds unchanged stays host-owned; an arm that wrote the field makes it guest-owned.
        val descriptor = "Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;"
        val type = VirtualType.of("Ljava/lang/Boolean;")
        val projected = Value.Known(java.lang.Boolean.TRUE, type)
        val source = frame()
        source.writeHostProjection(descriptor, projected)
        val footprint = ReconciliationFootprint(
            persistentSetOf(),
            persistentMapOf(),
            persistentMapOf(),
            persistentSetOf(descriptor),
        )

        val untouched = source.reconcileOpArms(listOf(source.copy(), source.copy()), footprint, classIndex)
        assertEquals(
            StaticCell(projected, StaticCellProvenance.HOST_PROJECTION),
            untouched.staticCell(descriptor),
        )

        val writer = source.copy()
        writer.writeStaticField(descriptor, projected)
        val written = source.reconcileOpArms(listOf(source.copy(), writer), footprint, classIndex)
        assertEquals(StaticCell(projected, StaticCellProvenance.GUEST_WRITE), written.staticCell(descriptor))
    }
}
