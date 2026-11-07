package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.ExecutionGraph
import org.cf.smalivm.core.ExecutionSession
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.FrameValue
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.MethodView
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.ThrowableDomain
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * `check-cast` (op 1f, format 21c = TWO code units). Migrated from `org.cf.smalivm.opcode.CheckCastOpTest`
 * -- all four tests, byte-identical expectations -- plus the rows the corpus never reached, which is where
 * this file's content is.
 *
 * **SPEC** [BC]: *"Throw a `ClassCastException` if the reference in the given register cannot be cast to
 * the indicated type."* One of only two `Exception` types [BC] names anywhere, so this op's exception is
 * genuinely SPEC rather than INHERITED (`docs/DALVIK-SEMANTICS.md#check-cast-21c`).
 *
 * **The four migrated tests cover ONE of this op's six rows.** All four seed a `Known`, three of them
 * satisfy the cast and the fourth fails it definitely -- so nothing in the corpus reaches the ambiguous
 * value, the arm-specific narrowing, `Value.Top`, or an unanswerable class walk.
 * `docs/architecture/exceptions.md:249` and `state-model.md:295` both list *"Unknown `check-cast`
 * exceptional alternatives"* among the behaviours with **no active coverage**, and `exceptions.md:247`
 * records the refinement half as untested too. Both were measured true here.
 */
class CheckCastOpTest {
    companion object {
        private const val CLASS_NAME = "Lcheck_cast_test;"

        /** `check-cast` is `21c`, two code units, so the `return-void` of the simple fixtures is at 2. */
        private const val ADDRESS_CHECK_CAST = 0
        private const val ADDRESS_RETURN = 2

        /**
         * `castToStringWithCatch()V` and `castToStringWithCatchOnAnotherRegister()V` share one layout:
         * `check-cast`@0 (two units), `nop`@2, `return-void`@3, `move-exception`@4, `goto`@5. @2 is the
         * fall-through's first instruction, so it is both `testCaught`'s `unreachedAddress` for a definite
         * throw and the CONTINUE arm's own node for the fork.
         */
        private const val ADDRESS_FALL_THROUGH = 2
        private const val ADDRESS_HANDLER = 4

        private const val OBJECT = "Ljava/lang/Object;"
        private const val STRING = "Ljava/lang/String;"
        private const val CLASS_CAST_EXCEPTION = "Ljava/lang/ClassCastException;"

        private val OBJECT_TYPE = VirtualType.of(OBJECT)
        private val STRING_TYPE = VirtualType.of(STRING)
        private val CCE_TYPE = VirtualType.of(CLASS_CAST_EXCEPTION)

        /** The message the migration keeps byte-identical. **CORPUS** tier and nothing more -- see below. */
        private const val CCE_MESSAGE = "java.lang.Integer cannot be cast to java.lang.String"
    }

    @Test
    fun refinedThrowableSucceedsWhenCastToSerializableInterface() {
        val classIndex = CoreTester.classIndex
        val view = classIndex.requireMethodView(CLASS_NAME, "castThrowableToSerializable()V")
        val frame = Frame.entryFrame(view)
        val throwable = VirtualType.of("Ljava/lang/Throwable;")
        val value = Value.Unknown(throwable)
        frame.assignFrameValue(
            0,
            FrameValue.refined(
                value,
                ThrowableDomain.constrainedTo(throwable, classIndex.throwableHierarchy),
            ),
        )

        val graph = ExecutionSession.of(view, classIndex, entryFrame = frame).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(null)), graph.outcomes)
    }

    @Test
    fun symbolicExceptionKeepsCloneableSuccessButConcreteTypesRemainExact() {
        val classIndex = CoreTester.classIndex
        val view = classIndex.requireMethodView(CLASS_NAME, "castExceptionToCloneable()V")
        val exception = VirtualType.of("Ljava/lang/Exception;")
        val npe = VirtualType.of("Ljava/lang/NullPointerException;")
        val cloneableException = VirtualType.of("Lcloneable_exception;")

        fun execute(value: FrameValue.RefinedThrowable): ExecutionGraph {
            val frame = Frame.entryFrame(view)
            frame.assignFrameValue(0, value)
            return ExecutionSession.of(view, classIndex, entryFrame = frame).run()
        }

        val symbolic = execute(
            FrameValue.refined(
                Value.Unknown(exception),
                ThrowableDomain.constrainedTo(exception, classIndex.throwableHierarchy),
            ),
        )
        val nonCloneableFrame = Frame.entryFrame(view)
        val nonCloneable = nonCloneableFrame.allocateThrowable(npe, NullPointerException("not cloneable"))
        nonCloneableFrame.assignFrameValue(
            0,
            FrameValue.refined(nonCloneable, ThrowableDomain.exact(npe, classIndex.throwableHierarchy)),
        )
        val nonCloneableGraph = ExecutionSession.of(view, classIndex, entryFrame = nonCloneableFrame).run()
        val cloneableFrame = Frame.entryFrame(view)
        val cloneable = cloneableFrame.allocateThrowable(cloneableException, Exception("cloneable"))
        cloneableFrame.assignFrameValue(
            0,
            FrameValue.refined(
                cloneable,
                ThrowableDomain.exact(cloneableException, classIndex.throwableHierarchy),
            ),
        )
        val cloneableGraph = ExecutionSession.of(view, classIndex, entryFrame = cloneableFrame).run()

        assertEquals(CompletionStatus.Complete, symbolic.completion())
        assertEquals(1, symbolic.outcomes.filterIsInstance<Outcome.Returned>().size)
        assertEquals(listOf(CLASS_CAST_EXCEPTION), CoreTester.escapingTypes(symbolic))
        assertEquals(listOf(CLASS_CAST_EXCEPTION), CoreTester.escapingTypes(nonCloneableGraph))
        assertEquals(listOf(Outcome.Returned(null)), cloneableGraph.outcomes)
    }

    /**
     * The four migrated tests, unchanged in expectation. `TestState` -> [CoreState] and `Tester` ->
     * [CoreTester] is the whole diff for the first three; the fourth changes which HARNESS ENTRY POINT
     * reads the value, which the plan's Global Constraints call out as not a change to the value.
     */
    @Nested
    @DisplayName("migrated from the oracle's CheckCastOpTest")
    inner class Migrated {
        private lateinit var expected: CoreState
        private lateinit var initial: CoreState

        @BeforeEach
        fun setUp() {
            expected = CoreState()
            initial = CoreState()
        }

        /**
         * **The migrated test that proves the null row RE-TAGS**, which is the one thing about it that is
         * easy to get wrong in the direction that looks harmless: the raw stays `null` and the DECLARED
         * type becomes the cast type. The oracle re-tags here through the same `assignRegister` its
         * satisfying path uses (`opcode/CheckCastOp.kt:28`), because its `isInstance` answers `true` for a
         * null (`:55-57`).
         *
         * INHERITED, [JVMS] `checkcast`, verbatim: *"If objectref is null, then the operand stack is
         * unchanged."* [BC] does not mention null at all.
         */
        @Test
        fun canCastNullWithObjectTypeToString() {
            initial.setRegisters(0, null, OBJECT)
            expected.setRegisters(0, null, STRING)
            CoreTester.test(CLASS_NAME, "castToString()V", initial, expected)
        }

        @Test
        fun canCastStringWithObjectTypeToString() {
            initial.setRegisters(0, "great maker", OBJECT)
            expected.setRegisters(0, "great maker", STRING)
            CoreTester.test(CLASS_NAME, "castToString()V", initial, expected)
        }

        /**
         * A **widening** re-tag, and it is deliberate on both engines rather than a leniency: [ART]'s
         * verifier *replaces* the register's type instead of intersecting it, and its own comment says the
         * upcast is simply not addressed (*"we will \"downcast\" register vA to the type in vB. (This could
         * be a \"upcast\" -- not expected, so we don't try to address it.)"*,
         * `runtime/verifier/method_verifier.cc:3122-3128`, the write at `:3165`). So `Ljava/lang/String;`
         * becoming `Ljava/lang/Object;` is the modelled behaviour and not information thrown away by
         * accident.
         */
        @Test
        fun canCastStringWithStringTypeToObject() {
            initial.setRegisters(0, "great maker", STRING)
            expected.setRegisters(0, "great maker", OBJECT)
            CoreTester.test(CLASS_NAME, "castToObject()V", initial, expected)
        }

        /**
         * **`CoreTester.testCaught`, not `testThrows`**, because this fixture's `check-cast` is inside
         * `.catch Ljava/lang/Exception;` and a definitely-caught throw produces **no** `Outcome.Threw` at
         * all -- `ExecutionSession.planThrow` gives it `successors=[handler], terminal=null`, and
         * `testThrows` asserts a non-empty `Outcome.Threw` list. That is a fail-on-arrival mistake rather
         * than a subtle one, and `testCaught`'s own KDoc names it.
         *
         * `testCaught` asserts the whole of the oracle `Tester.testSimpleException` claim: the run is
         * `Complete`, nothing ESCAPED with this type, `move-exception` bound it into v0, the message
         * matches, and the fall-through at @2 was not reached -- the last one **gated by the completion
         * check**, which a hand-written `assertFalse(wasReached(2))` would not be.
         *
         * **The `registersAssigned` half of the oracle's assertion does not port, and that is a harness
         * fact rather than a behavioural change.** `Tester.testSimpleException` ends with
         * `assertEquals(0, node.state.registersAssigned.size)` -- the **runtime** set on a *throwing* path,
         * where the oracle's `assignRegister` (`opcode/CheckCastOp.kt:28`) never ran.
         * `ExecutionGraphView.registersAssigned(address)` is **static**, `opOrNull(address)?.registersAssigned`
         * (`core/ExecutionGraph.kt:259-260`), so it answers `{0}` for this op on every path and
         * `assertEquals(emptySet<Int>(), …)` could not pass. The liveness claim is made in the non-vacuous
         * three-line shape instead, by [aSuccessfulCastAssignsItsTargetRegisterAndReadsIt].
         *
         * **The message is CORPUS tier.** It is reproduced byte-identically so the migration is a
         * relocation, and for no other reason: [RT] records that runtimes deliberately changed exception
         * text, so an asserted message is never evidence of Dalvik fidelity. It is built from the
         * **declared** type rather than from `raw.javaClass`, matching `opcode/CheckCastOp.kt:32`.
         */
        @Test
        fun objectNotOfCastTypeThrowsClassCastException() {
            initial.setRegisters(0, 0x42, "Ljava/lang/Integer;")
            CoreTester.testCaught(
                CLASS_NAME,
                "castToStringWithCatch()V",
                initial,
                exceptionType = CLASS_CAST_EXCEPTION,
                message = CCE_MESSAGE,
                caughtInRegister = 0,
                unreachedAddress = ADDRESS_FALL_THROUGH,
            )
        }
    }

    /**
     * 🔴 **THE DIVERGENCE: an ambiguous cast explores BOTH outcomes, where the oracle explores one and
     * reports the run complete.**
     *
     * `opcode/CheckCastOp.kt:38` returns `finishOp(mayThrow = true)` for an unprovable value, and the flag
     * is **inert for this op**: `finishOp` appends `defaultExceptions`
     * (`UnresolvedChildProducer.kt:36-43`), `Op`'s constructor defaults that array to `arrayOf()`
     * (`opcode/Op.kt:12-13`), and `CheckCastOp` declares `: Op(location)` with no second argument
     * (`:16-20`). Four sibling ops DO pass one, positionally (`opcode/BinaryMathOp.kt:23`,
     * `opcode/AGetOp.kt:20`, `opcode/ArrayLengthOp.kt:19`, `opcode/APutOp.kt:24`), so the mechanism is
     * live and this op is the one that requests it without declaring it -- which is why `:38` looks
     * correct in isolation and is not. The oracle therefore produces ONE fall-through child and **no
     * exception child** on a run it reports as complete: an unexplored feasible path, which spec §2.2 calls
     * the worst outcome this engine can have. Three audit sites reach the same verdict independently
     * (`exceptions.md:77`, `opcodes.md:128` and Correctness Defect 17 at `:172`, `state-model.md:193`).
     *
     * **The proof is NOT "two `Outcome.Threw`", and that is the whole reason this test is shaped the way it
     * is.** The fixture's handler COVERS the op, so the forked exception is caught and there is no
     * escaping outcome to count. So: the node at @0 has **two** children (the channel that can see fork
     * arity at all -- a migrated expectation cannot, because `terminatingConsensus` is a join), the handler
     * block at @4 is reached, the fall-through at @2 is reached, and the value `move-exception` bound is a
     * `ClassCastException` on the heap.
     *
     * **The seed is `Unknown(Ljava/lang/Object;)` and NOT `Unknown(Ljava/lang/String;)`, deliberately.**
     * A `String`-tagged unknown provably PASSES a cast to `String` (design decision 5), so seeding one
     * would pin the over-fork this plan exists to avoid as intended behaviour -- see
     * [anUnknownWhoseDeclaredTypeSatisfiesTheTargetNeverForks], which is this test's mirror.
     */
    @Test
    fun anAmbiguousCastExploresBothTheSuccessAndTheThrowAndTheThrowIsCaughtHere() {
        val graph = CoreTester.execute(CLASS_NAME, "castToStringWithCatch()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, OBJECT)
        })
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "the fork must be explored to the end, not truncated -- an arity claim on a Partial run is void",
        )
        assertEquals(1, graph.nodePile(ADDRESS_CHECK_CAST).size, "one node at the cast itself")
        assertEquals(
            2, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
            "TWO successors: the cast may succeed and it may throw, and both are feasible",
        )
        assertTrue(graph.wasReached(ADDRESS_FALL_THROUGH), "the success arm ran")
        assertTrue(
            graph.wasReached(ADDRESS_HANDLER),
            "and so did the handler -- this is the path `opcode/CheckCastOp.kt:38` never schedules",
        )

        // The EXCLUSION half. `wasReached` and a child count assert existence; the two assertions below
        // assert that the second child is the ClassCastException and nothing else, which is what
        // Global Constraints require of every ordering/arity claim in this plan (an existence assertion
        // cannot pin exclusivity -- `testThrows`' own KDoc records the 0-of-844 mutation that proved it).
        assertTrue(
            graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
            "the handler covers the op, so nothing escapes: ${graph.outcomes}",
        )
        val caught = graph.nodePile(ADDRESS_HANDLER).single().frame.peek(0)
        assertTrue(
            caught is Value.Known && caught.type == CCE_TYPE,
            "`move-exception v0` must bind a ClassCastException, but bound $caught",
        )
        val ref = (caught as Value.Known).raw
        assertTrue(ref is HeapRef, "a synthesized guest throwable is a tracked heap object (spec §4.1), not $ref")
        val thrown = graph.nodePile(ADDRESS_HANDLER).single().frame.heapObject(ref as HeapRef)
        assertTrue(thrown is HeapObject.GuestThrowable, "no synthesized throwable at $ref; got $thrown")
        assertEquals(
            "java.lang.Object cannot be cast to java.lang.String",
            (graph.nodePile(ADDRESS_HANDLER).single().frame.hostObjectAt(ref) as Throwable).message,
            "built from the DECLARED type, exactly as `opcode/CheckCastOp.kt:32` builds it",
        )
    }

    /**
     * 🔴 **The fork's `Continue` arm carries the register NARROWED and its `Throw` arm carries it
     * UNNARROWED -- and this closes the SECOND half of Correctness Defect 17.**
     *
     * `docs/architecture/opcodes.md:172` is two defects in one line: *"Unknown failed casts omit
     * `ClassCastException`, **while their normal path omits target-type refinement**."* Verified at source:
     * `opcode/CheckCastOp.kt:38` takes the unprovable exit with **no** `assignRegister`, while `:27-29`
     * refines only on the satisfying path -- so the oracle drops the refinement on exactly the path this
     * op forks. [anAmbiguousCastExploresBothTheSuccessAndTheThrowAndTheThrowIsCaughtHere] closes the first
     * half; this test closes the second, and a later reader closing item 17 needs both.
     *
     * **Neither direction is observable through `castToStringWithCatch()V`,** whose handler binds the
     * caught exception into **v0, the register the cast is on** -- so the narrowing is overwritten before
     * anything can read it. `castToStringWithCatchOnAnotherRegister()V` differs in that one register and
     * exists for this test; its smali says so.
     *
     * Asserted per ARM rather than only on the join, because the join alone is not lethal in both
     * directions: `consensus(2, 0)` is the success arm's own node and `consensus(4, 0)` is the handler's.
     * Narrowing the `Throw` arm too would claim v0 is a `String` on the path whose whole content is that
     * it is **not** one; dropping the narrowing from the `Continue` arm loses the refinement that makes
     * `check-cast` useful to a deobfuscator at all.
     *
     * [OpArm.Continue] carries the refinement as an [OpUpdate]. The scheduler applies it only to the
     * success arm's persistent frame; the throw arm retains the pre-op value.
     */
    @Test
    fun theForksSuccessArmIsNarrowedAndItsThrowingArmIsNot() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "castToStringWithCatchOnAnotherRegister()V",
            CoreState().apply { setRegisters(0, CoreState.UNKNOWN, OBJECT) },
        )
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        assertEquals(
            2, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
            "the premise: this is the forking row, so there are two arms to tell apart",
        )
        assertEquals(
            Value.Unknown(STRING_TYPE), trusted!!.consensus(ADDRESS_FALL_THROUGH, 0),
            "the arm on which the cast SUCCEEDED knows the register is a String (or null)",
        )
        assertEquals(
            Value.Unknown(OBJECT_TYPE), trusted.consensus(ADDRESS_HANDLER, 0),
            "the arm on which it THREW must not: the throw is the proof that it is not a String",
        )
        // ...and the consumer-facing join over both returning paths, which is what a deobfuscator reads.
        // `Unknown(String)` joined with the caught `Known(HeapRef, ClassCastException)` is
        // `Unknown(Ljava/lang/Object;)`; narrowing both arms would make it `Unknown(Ljava/lang/String;)`.
        assertEquals(
            Value.Unknown(OBJECT_TYPE), trusted.terminatingConsensus(0),
            "the two arms disagree about the type, so their join is the common supertype",
        )
        assertEquals(
            Value.Unknown(OBJECT_TYPE), trusted.consensus(ADDRESS_CHECK_CAST, 0),
            "the multi-arm source is the conservative join and never selects the successful arm's type",
        )
    }

    /**
     * 🔴 **The mirror defect, and the plan already held the proof: a cast whose declared type SATISFIES the
     * target cannot fail, so it must not fork.**
     *
     * `Unknown(T)` holds null, or a `T`, or a subtype of `T` -- and for a **cast** all three pass, which is
     * exactly where this op and `instance-of` are NOT symmetric (design decision 5): for an instance test
     * null and a satisfying `T` give OPPOSITE answers, which is why
     * `docs/architecture/opcodes.md:173` files Correctness Defect 18 against `InstanceOfOp` and files
     * nothing of the kind against `CheckCastOp`'s satisfying-unknown path. The oracle agrees with this core
     * here, by its own type-set loop.
     *
     * Two seeds, and the second is the one that matters in real input:
     *  - `Unknown(Ljava/lang/String;)` cast to `Ljava/lang/Object;`. **No cast to `Ljava/lang/Object;` can
     *    ever fail** -- every reference is an `Object` and null passes -- and design decision 4 records
     *    that the oracle's `type/VirtualClass.java:146-148` short-circuits `true` for an `Object` target
     *    before it even walks ancestors.
     *  - `Unknown(Ljava/lang/String;)` cast to `Ljava/lang/String;`, i.e. **essentially every method that
     *    takes a `String` parameter**: `Frame.entryFrame` seeds every reference parameter as
     *    `Value.Unknown(parameter.type)`. Forking here would tell a deobfuscator that a `catch` around an
     *    obfuscator's redundant `check-cast` is live.
     *
     * Inventing an infeasible path is graded equally with dropping a feasible one (spec §2.2), and Plan 7
     * shipped that mirror twice. Asserted through all three channels that can see fork arity, since the
     * value expectation below cannot: a join is idempotent when the arms agree.
     */
    @Test
    fun anUnknownWhoseDeclaredTypeSatisfiesTheTargetNeverForks() {
        for ((method, target) in listOf("castToObject()V" to OBJECT_TYPE, "castToString()V" to STRING_TYPE)) {
            val graph = CoreTester.execute(CLASS_NAME, method, CoreState().apply {
                setRegisters(0, CoreState.UNKNOWN, STRING)
            })
            assertEquals(CompletionStatus.Complete, graph.completion(), "$method: nothing truncated")
            assertEquals(
                1, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
                "$method: ONE successor -- this cast provably succeeds, so there is no exception to explore",
            )
            assertEquals(1, graph.nodePile(ADDRESS_RETURN).size, "$method: so the return is reached once")
            assertEquals(1, graph.outcomes.size, "$method: and the run has exactly one outcome")
            assertTrue(
                graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
                "$method: and it is not a throw: ${graph.outcomes}",
            )
            val trusted = graph.trusted()
            assertNotNull(trusted, "$method: a Complete exploration must yield a trusted view")
            assertEquals(
                Value.Unknown(target), trusted!!.terminatingConsensus(0),
                "$method: and the register is still re-tagged, because the cast succeeded",
            )
        }
    }

    /**
     * 🔴 **A register that READS AS A NULL REFERENCE passes any cast, and the row is keyed on
     * `Value.readsAsNullReference` rather than on `raw == null`.**
     *
     * `docs/DALVIK-SEMANTICS.md#check-cast-21c--opcodecheckcastopkt` files the oracle's version as **D11**,
     * an invented **definite** `ClassCastException` in two distinct ways, and names which conjunct of
     * `if (!itemType.isPrimitive && item.isNull)` (`opcode/CheckCastOp.kt:54-57`) fails per row:
     *  - `Known(5, "I")` -- **`item.isNull`** is the decisive conjunct. The type loop finds neither `I` nor
     *    `Ljava/lang/Integer;` castable to `String`, `target.isKnown` holds, and `:33-35` throws a definite
     *    CCE. **[ART]'s SWITCH interpreter** reads `GetVRegReference(A())` -> `nullptr`, so the
     *    `obj != nullptr` conjunct of
     *    `interpreter_switch_impl-inl.h:830` is false **in the same expression** as the cast test and the
     *    cast succeeds. ⚠️ **TIER, 2026-08-04: this seed is the one row ART's two interpreters disagree
     *    about, so its tier is VERIFIER and not [ART].** `nterp` -- the default on a modern device -- reads
     *    the **raw** register (`mterp/x86_64ng/object.S:1-22`) and dereferences the `5`;
     *    `method_verifier.cc:3156-3158` hard-fails the input so nothing links it. This core's *"the cast
     *    succeeds"* stands as the sound direction and matches the switch tier, and D11 stands because the
     *    oracle's definite CCE is wrong under either tier.
     *  - `Known(null, "I")` -- **`!itemType.isPrimitive`** is the decisive conjunct here, and this is the
     *    row the gate at `:55` exists for: the short-circuit is skipped, the type set collapses to `{"I"}`,
     *    and `VirtualPrimitive.instanceOf` (`type/VirtualPrimitive.java:35-40`) is `equals`-only, so the
     *    oracle throws a definite CCE **for a definite null**. This core keeps the
     *    `readsAsNullReference` answer -- it succeeds -- and no migrated expectation moves either way,
     *    because no fixture covers the shape.
     *
     * [BC]'s note that a primitive type operand *"will necessarily fail at runtime"* is **false** for these
     * rows for the same reason, and `castOutcome` orders its null row FIRST because of it.
     *
     * **The re-tag is where this test says something the plan did not, and it is a deliberate departure
     * from `opcode/CheckCastOp.kt:28`'s unconditional `assignRegister`.** A narrowing that would stop the
     * register reading as a null reference is **not applied**: `Known(5, "I")` stays `Known(5, "I")` rather
     * than becoming `Known(5, "Ljava/lang/String;")`. At runtime `check-cast` writes nothing, so in the switch
     * tier the `ShadowFrame`'s reference half is still cleared afterwards (`shadow_frame.h:401-408`) and a
     * subsequent `instance-of` on that register still answers `0` -- whereas the laundered `Known(5,
     * "Ljava/lang/String;")` would make it fold `Known(true, Z)`, which is registry D9 re-created one
     * opcode downstream. **That downstream argument is about this ENGINE's lattice, which is why it survives
     * the tier split intact**: whichever tier a device runs, laundering the tag would make this engine fold a
     * confident `true` for a value it had just proved reads as null, and no interpreter folds `true` for it.
     * The rows where the narrowing is safe still take it, which the two other seeds
     * below pin: a null raw and Dalvik's integral zero both keep reading as null under the new tag -- and
     * both of those are corroborated by **both** interpreter tiers.
     */
    @Test
    fun everySpellingOfReadingAsANullReferencePassesTheCast() {
        // The D11 row the oracle turns into a definite throw. `Known(5, "I")` under a cast to String:
        // succeeds, and the register is NOT laundered into a reference tag.
        val primitiveTyped = CoreState().apply { setRegisters(0, 5, "I") }
        val expectedPrimitiveTyped = CoreState().apply { setRegisters(0, 5, "I") }
        CoreTester.test(CLASS_NAME, "castToString()V", primitiveTyped, expectedPrimitiveTyped)

        // The OTHER D11 row -- `opcode/CheckCastOp.kt:55`'s primitive gate is what rejects this one. A
        // definite null under a primitive tag still reads as null, so it passes, and the re-tag is safe
        // because `Known(null, T)` reads as null under every T.
        val nullUnderPrimitiveTag = CoreState().apply { setRegisters(0, null, "I") }
        val expectedNullUnderPrimitiveTag = CoreState().apply { setRegisters(0, null, STRING) }
        CoreTester.test(CLASS_NAME, "castToString()V", nullUnderPrimitiveTag, expectedNullUnderPrimitiveTag)

        // Dalvik's integral zero under a REFERENCE tag -- [BC]: "(Object) null == (int) 0" -- which
        // `filled-new-array` and `const/4 v0, 0x0` really produce. Still reads as null under the new tag,
        // so the re-tag applies.
        val dalvikNull = CoreState().apply { setRegisters(0, 0, STRING) }
        val expectedDalvikNull = CoreState().apply { setRegisters(0, 0, OBJECT) }
        CoreTester.test(CLASS_NAME, "castToObject()V", dalvikNull, expectedDalvikNull)
    }

    /**
     * **A definite failing cast throws and explores NOTHING else** -- no fall-through, no second exception.
     *
     * The escaping counterpart of the migrated `objectNotOfCastTypeThrowsClassCastException`, on a fixture
     * with no handler, so the same row is pinned through `Outcome.Threw` as well as through a caught value.
     * That matters for the arity direction the migrated test cannot see: adding a spurious `Continue` to
     * this row would leave the caught test green (it asserts the type did not escape and v0 holds the CCE,
     * both still true) while inventing a path on which a definitely-failing cast fell through.
     *
     * **SPEC** [BC] for the exception itself; the message is CORPUS and is asserted only so it does not
     * drift silently.
     */
    @Test
    fun aDefinitelyFailingCastThrowsAndDoesNotAlsoContinue() {
        val initial = CoreState().apply { setRegisters(0, 0x42, "Ljava/lang/Integer;") }
        CoreTester.testThrows(
            CLASS_NAME, "castToString()V", initial,
            exceptionType = CLASS_CAST_EXCEPTION, message = CCE_MESSAGE,
        )
        val graph = CoreTester.execute(CLASS_NAME, "castToString()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            0, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
            "the throw escapes and nothing else is feasible, so the cast node has NO children",
        )
        assertFalse(
            graph.wasReached(ADDRESS_RETURN),
            "and the fall-through was not explored -- sound only because of the completion gate above",
        )
        assertEquals(1, graph.outcomes.size, "exactly one outcome: the throw")
    }

    /**
     * 🔴 **`Value.Top` forks UNCONDITIONALLY, and the op never asks its type.**
     *
     * A requirement rather than a choice, from `CastOutcome.Unprovable`'s own KDoc: `Value.Top` carries no
     * type at all (`core/Value.kt`'s `Top.type` is `null`), so an implementation that consulted
     * `isInstanceOf(value.type, target)` on this row would not even compile without a null guard -- and
     * with one, the honest answer is that nothing is known, so both outcomes are feasible.
     *
     * The Continue arm still narrows, and that is a genuine refinement rather than a technicality: after a
     * cast to `C` succeeds the register holds null or an instance of `C`, which is exactly
     * `Unknown(C)` -- strictly below `Top` in the lattice.
     *
     * The synthesized exception carries **no message**, because there is no declared type to name and
     * fabricating one would invent a constant. Same rule `core/op/APutOp`'s speculative
     * `ArrayStoreException` follows for a `Top` value.
     */
    @Test
    fun aTopRegisterForksAndTheSuccessArmIsStillRefined() {
        val graph = CoreTester.execute(CLASS_NAME, "castToString()V", CoreState().apply {
            setRegister(0, Value.Top)
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            1, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
            "one CHILD -- the throw escapes, so it has no node -- but two transitions, which the outcome " +
                "count below is what actually sees",
        )
        assertEquals(2, graph.outcomes.size, "two outcomes: the return and the escaping throw")
        val threw = graph.outcomes.filterIsInstance<Outcome.Threw>()
        assertEquals(1, threw.size, "exactly one throw, and it is the cast's")
        val exception = threw.single().exception
        assertTrue(
            exception is Value.Known && exception.type == CCE_TYPE,
            "a ClassCastException, not something derived from a type Top does not have: $exception",
        )
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        assertEquals(
            Value.Unknown(STRING_TYPE), trusted!!.terminatingConsensus(0),
            "the only RETURNING path is the one the cast succeeded on, and it knows more than Top did",
        )
    }

    /**
     * 🔴 **An unanswerable class walk FORKS, and the op must not re-ask the question to find out.**
     *
     * `CastOutcome.Unprovable` is reached for two unrelated reasons, and `CastOutcome`'s KDoc states the
     * consequence as a **requirement** on this op: a `Value.Known` gets here when
     * `ClassIndex.isInstanceOf` raised `ClassLoadFailure` for every candidate type and nothing satisfied.
     * Re-consulting `isInstanceOf` from the op would re-enter the *same* call on the *same* candidate and
     * **re-raise**, and a `ClassLoadFailure` escaping an op becomes `Aborted(HOST_FAILURE)` --
     * indistinguishable from an engine bug, which is the pair `CARRY-FORWARD.md`'s Plan 9 item 5 exists to
     * prevent. So the answer is a fork, taken from the outcome alone.
     *
     * The seed is the shape that reaches it: a declared tag naming a class this input does not ship
     * (`Lobf/absent;`, the same descriptor `core/op/InstanceOfOpTest` uses) over a raw whose own class
     * (`java.lang.Object`) does **not** satisfy the target either -- so the candidate set is
     * `{unanswerable, false}` and the whole answer turns on how the unanswerable half is absorbed.
     * Absorbing it into `false` would invent a **definite** `ClassCastException`; letting it escape would
     * abort the path. Both are wrong, in opposite directions.
     *
     * The `Complete` assertion is the lethal part: a re-ask makes this run `Partial(HOST_FAILURE)`, not
     * merely differently shaped.
     */
    @Test
    fun anUnanswerableClassWalkForksInsteadOfRaisingOrInventingAThrow() {
        val graph = CoreTester.execute(CLASS_NAME, "castToStringWithCatch()V", CoreState().apply {
            setRegisters(0, Any(), "Lobf/absent;")
        })
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "a ClassLoadFailure must not escape as a host failure: ${graph.outcomes}",
        )
        assertEquals(
            2, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
            "unprovable, so both outcomes are explored -- not a definite throw and not a definite success",
        )
        assertTrue(graph.wasReached(ADDRESS_FALL_THROUGH), "the success arm ran")
        assertTrue(graph.wasReached(ADDRESS_HANDLER), "and so did the throw arm's handler")
    }

    /**
     * **Side effects: NONE, on every row.** A cast writes at most one register and touches nothing this
     * engine does not own; the re-tag is a **register write**, covered by [registersAssigned], and effects
     * are about state outside this frame. `docs/DALVIK-SEMANTICS.md`'s own answer to the question is the
     * same, and it is stated rather than left implicit because silence invites the other answer.
     *
     * It is not even a class initialisation, and the grounds are [ART]'s WHOLE `CHECK_CAST` handler body
     * (`interpreter_switch_impl-inl.h:819-835`: resolve, `nullptr` bail, one `GetVRegReference(A())`, a
     * conditional `ThrowClassCastException`, `return true` -- nothing allocates and nothing else is called),
     * so `check-cast` genuinely does not run `<clinit>`. ⚠️ **NOT the `can_run_clinit = false` at `:824`, which
     * is what this KDoc used to argue:** `NEW_INSTANCE` (`:862-894`) passes the same `false` and initialises
     * downstream in `AllocObjectFromCode`, so the flag proves nothing about an opcode
     * (`docs/DALVIK-SEMANTICS.md#check-cast-21c`).
     *
     * A spurious `STRONG` would silently stop **every** constant fold in any method containing a cast,
     * because `methodSideEffectLevel` maxes over addresses -- Plan 7 Task 10 measured that exact defect and
     * its documented `NONE` row killed 0 of 858 tests, which is why the absence is *declared* and pinned.
     * Every row is driven, because `recordSideEffect` is a per-call thing and one row could carry it.
     */
    @Test
    fun aCastDeclaresNoSideEffectOnEveryRow() {
        val seeds = listOf(
            "a provable success" to CoreState().apply { setRegisters(0, "great maker", OBJECT) },
            "a null register" to CoreState().apply { setRegisters(0, null, OBJECT) },
            "a primitive-typed register" to CoreState().apply { setRegisters(0, 5, "I") },
            "a definite failure" to CoreState().apply { setRegisters(0, 0x42, "Ljava/lang/Integer;") },
            "an ambiguous fork" to CoreState().apply { setRegisters(0, CoreState.UNKNOWN, OBJECT) },
            "a satisfying unknown" to CoreState().apply { setRegisters(0, CoreState.UNKNOWN, STRING) },
        )
        for ((label, seed) in seeds) {
            val graph = CoreTester.execute(CLASS_NAME, "castToStringWithCatch()V", seed)
            assertEquals(
                SideEffectLevel.NONE, graph.sideEffectLevel(ADDRESS_CHECK_CAST),
                "$label: a cast mutates nothing outside its own frame",
            )
            val trusted = graph.trusted()
            assertNotNull(trusted, "$label: nothing aborted")
            assertEquals(
                SideEffectLevel.NONE, trusted!!.methodSideEffectLevel(),
                "$label: and the whole-method query a strategy actually reads agrees",
            )
        }
    }

    /**
     * The read/assignment metadata, which on this engine is the **sole** guarantor of a correct
     * free-register answer: `ExecutionGraph.availableRegisters` is purely static, so these two sets are
     * what `docs/architecture/graph-optimizer-contract.md:136`'s **Dead removal** row acts on.
     *
     * `registersAssigned` is `{0}` and not empty, because dexlib2 declares `check-cast` `SETS_REGISTER` and
     * that is **not** a bug: it reflects [ART]'s verifier narrowing the register's static type on the
     * success path (`method_verifier.cc:3165`). Three of the four migrated tests assert the re-tag, so the
     * declaration is load-bearing rather than decorative.
     *
     * Non-vacuous by construction, per `core/op/FilledNewArrayOpTest`'s recorded trap: a bare
     * `assertEquals(emptySet(), registersAssigned(0))` passes HARDEST when nothing works, because the base
     * `Op` answers `emptySet()`. So this gates on `Complete`, asserts `wasReached` positively, and asserts
     * a NON-empty set at the cast against an empty one at the `return-void`.
     */
    @Test
    fun aSuccessfulCastAssignsItsTargetRegisterAndReadsIt() {
        val graph = CoreTester.execute(CLASS_NAME, "castToString()V", CoreState().apply {
            setRegisters(0, "great maker", OBJECT)
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(ADDRESS_CHECK_CAST), "the op ran, so the sets below describe a live op")
        assertEquals(setOf(0), graph.registersAssigned(ADDRESS_CHECK_CAST), "vAA is re-tagged, so it is assigned")
        assertEquals(
            emptySet<Int>(), graph.registersAssigned(ADDRESS_RETURN),
            "the CONTROL: an op that assigns nothing answers the empty set, which is also what an " +
                "unbuilt op would answer at the address above",
        )
        val op = graph.nodePile(ADDRESS_CHECK_CAST).single().op
        assertEquals(setOf(0), op.registersRead, "and the same register is read -- one operand, read and written")
        assertEquals(setOf(0), op.registersAssigned)
    }

    /**
     * 🔴 **AN ABSENT CAST TYPE IS A DEFINITE GUEST `NoClassDefFoundError`, AND IT FIRES ON A NULL SOURCE**
     * (Plan 9 Task 15, the ROW-23 requirement; Plan 8 answered `Aborted(UNSUPPORTED_OP)` here and this test
     * did not exist).
     *
     * Two claims, and the second is why the seed is `null` rather than an object:
     *
     * 1. **The TYPE.** A `LinkageError`/`Error`, not the `ClassCastException` this op otherwise raises and
     *    not the oracle's checked exception. `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`,
     *    cross-validated `[DEVICE](Tier B, nterp default, API 36)` --
     *    `tools/art-runner/fixtures/Fx_CheckCastAbsentType.smali` against `Fx_CheckCastPresentType.smali`,
     *    and the family is tiered once at
     *    [`DALVIK-SEMANTICS.md`'s "Resolution failure is one family"](../../../../../../../../../docs/DALVIK-SEMANTICS.md#resolution-failure-is-one-family-tiered-once).
     * 2. **The ORDERING: resolution precedes the null check.** A null *passes* every cast
     *    ([CastOutcome.ReadsAsNull]), so if this op read its register first the seed below would fall through
     *    with a re-tagged `v0` and no throw at all. It throws instead, which is the same ordering the device
     *    fixture measures with its own null source, and the opposite of [JVMS]'s (*"if objectref is null,
     *    then the operand stack is unchanged"*). This assertion is the ONLY thing in the suite that would
     *    catch the two lines of `CheckCastOp.execute` being swapped.
     *
     * **The CONTROL is the `ReadsAsNull` row on a PRESENT type**, in this same test rather than by
     * cross-reference: without it, "a null source throws" also passes against a build where every cast on a
     * null throws, which would be a far worse defect than the one being fixed.
     */
    @Test
    fun anAbsentCastTypeThrowsNoClassDefFoundErrorEvenOnANullSource() {
        val nullSource = CoreState().apply { setRegisters(0, null, OBJECT) }

        // THE CONTROL: the same definite null against a PRESENT cast type passes and re-tags. So the throw
        // below is the absent TYPE's, not "a null register throws".
        CoreTester.test(
            CLASS_NAME, "castToString()V", nullSource,
            CoreState().apply { setRegisters(0, null, "Ljava/lang/String;") },
        )

        CoreTester.testThrows(
            CLASS_NAME, "castToAbsentClass()V", nullSource,
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
        )
        val graph = CoreTester.execute(CLASS_NAME, "castToAbsentClass()V", nullSource)
        assertEquals(CompletionStatus.Complete, graph.completion(), "so the negatives below are dead-code claims")
        assertEquals(
            listOf("Ljava/lang/NoClassDefFoundError;"), CoreTester.escapingTypes(graph),
            "exactly one escaping throw, of exactly this type -- in particular NOT a ClassCastException",
        )
        assertEquals(
            0, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
            "definite, not a fork: absence is a FACT (spec §3 makes multidex a non-goal), so there is no " +
                "feasible fall-through to explore",
        )
        assertFalse(graph.wasReached(ADDRESS_RETURN), "and the fall-through a null source would take is dead")
        // The register is untouched, which is the observable form of "before any register read": a cast that
        // resolved would have re-tagged v0 to the cast type on this row.
        assertEquals(
            Value.Known(null, VirtualType.of(OBJECT)), graph.nodePile(ADDRESS_CHECK_CAST).first().frame.peek(0),
            "the throw happened before the register was read, so no re-tag survives",
        )
    }

    /**
     * `toString` matches `opcode/CheckCastOp.kt:43` exactly. Unlike `instance-of`, whose oracle spelling is
     * filed as Correctness Defect 19, there is no defect here to decline to reproduce: the oracle prefixes
     * its one register.
     */
    @Test
    fun toStringMatchesTheOracles() {
        val graph = CoreTester.execute(CLASS_NAME, "castToString()V", CoreState().apply {
            setRegisters(0, "great maker", OBJECT)
        })
        assertEquals(
            "check-cast r0, Ljava/lang/String;",
            graph.nodePile(ADDRESS_CHECK_CAST).single().op.toString(),
        )
    }
}
