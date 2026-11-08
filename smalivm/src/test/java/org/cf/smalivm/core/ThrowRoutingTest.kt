package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Bug classes (audit + spec §7): (a) a guest throwable rebuilt from (class, message) and a
 * reflective constructor call, so the caught object was not the thrown object; (b) two disagreeing
 * handler resolvers; (c) an ambiguous throw type resolving to one arbitrary handler instead of
 * forking every feasible one -- which silently drops explored paths and corrupts consensus; and
 * (d) the MIRROR of (c), added by Plan 7 Task 12 -- forking a throw whose outcome is PROVABLE, which
 * invents reachable handlers and tells a deobfuscator a dead `catch` block is live. See the boundary
 * comment above `throwingTheNullIdiomResolvesToTheNullPointerExceptionHandlerAlone` for which values
 * fall on which side of that line, and why the split is legitimate rather than merely convenient.
 * Oracle: Dalvik try/catch semantics at the deobfuscation bar (spec §7) and the soundness direction
 * in spec §2.2 (never fewer feasible paths).
 */
class ThrowRoutingTest {
    private val classIndex = ClassIndex.open(File("src/test/resources/smali"))
    private val vmServices = VmServices(classIndex)
    private val runtimeException = VirtualType.of("Ljava/lang/RuntimeException;")
    private val illegalStateException = VirtualType.of("Ljava/lang/IllegalStateException;")
    private val ioException = VirtualType.of("Ljava/io/IOException;")
    private val npe = VirtualType.of("Ljava/lang/NullPointerException;")
    private val objectType = VirtualType.of("Ljava/lang/Object;")
    private val string = VirtualType.of("Ljava/lang/String;")
    private val int = VirtualType.of("I")

    private val caughtDescriptor = "throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;"
    private val twoHandlersDescriptor = "throwTwoHandlers(Ljava/lang/Object;)Ljava/lang/Object;"
    private val catchAllDescriptor = "throwCatchAll(Ljava/lang/Object;)Ljava/lang/Object;"
    private val unloadableDescriptor = "throwUnloadableCatchType(Ljava/lang/RuntimeException;)Ljava/lang/Object;"
    private val afterNopDescriptor = "throwCaughtAfterNop(Ljava/lang/RuntimeException;)Ljava/lang/Object;"

    private fun session(
        descriptor: String,
        limits: Limits = Limits(),
        seed: (Frame, MethodView) -> Unit = { _, _ -> },
    ): ExecutionSession {
        val view = classIndex.requireMethodView("Lspine/spine;", descriptor)
        val frame = Frame.entryFrame(view).also { seed(it, view) }
        return vmServices.newSession(view, limits = limits, entryFrame = frame)
    }

    /**
     * A fixture method with try blocks built from its view. A handler at [MethodView.endAddress] names no
     * instruction, which reaches the malformed-try-block guard that collides with the escape's own `endWith` if
     * throw routing records in the wrong order.
     */
    private fun withTryBlocks(descriptor: String, tryBlocks: (MethodView) -> List<TryBlock>): MethodView {
        val real = classIndex.requireMethodView("Lspine/spine;", descriptor)
        return classIndex.rebuiltView(real, tryBlocks(real))
    }

    @Test
    fun caughtNullPointerExceptionIsRethrownWithTheSameIdentity() {
        // A null receiver throws; move-exception captures that NPE; throw must preserve its identity.
        val view = classIndex.requireMethodView("Lthrow_test;", "throwNullPointerException()V")
        val graph = vmServices.newSession(view).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        val escaped = (graph.outcomes.single() as Outcome.Threw).exception as Value.Known
        assertEquals(npe, escaped.type)
        val terminal = graph.nodePile(6).single()
        val frame = terminal.terminalFrame
        assertEquals(escaped, frame.read(0))
        assertEquals(escaped, frame.thrownOrNull())
        assertTrue(frame.heap.hostObjectAt(escaped.raw as HeapRef) is NullPointerException)
        assertEquals(listOf(0, 1, 5, 6), graph.addresses().filter(graph::wasReached))
    }

    @Test
    fun knownExceptionRoutesToTheTypedHandlerPreservingIdentity() {
        // Bug class (a): the value that arrives in the handler must be the very value thrown.
        val thrown = Value.Known(IllegalStateException("boom"), runtimeException)
        val s = session(caughtDescriptor) { frame, view -> frame.assign(view.parameters.single().register, thrown) }
        val graph = s.run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        assertEquals(illegalStateException, returned.type)
        assertTrue(returned.raw is HeapRef, "the imported throwable must have guest heap identity")
        // A caught throw is a guest outcome, not an abort, so this run COMPLETES -- which is why the
        // trusted view (spec §9's gate) is the right accessor here rather than the ungated twin.
        assertEquals(returned, graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun handlerPathIsReachedAndTheThrowingPathDoesNotEscape() {
        // Bug class: a throw recorded as escaping the method even though a handler caught it, which
        // both loses the handler's path and lets a throwing outcome contaminate a consumer that
        // partitions by outcome kind (spec §6.5). Oracle: Dalvik try/catch -- a covered throw whose
        // type matches the clause never leaves the method.
        val thrown = Value.Known(IllegalStateException("boom"), runtimeException)
        val s = session(caughtDescriptor) { frame, view -> frame.assign(view.parameters.single().register, thrown) }
        val graph = s.run()
        val handlerAddress = graph.methodView.tryBlocks.single().catches.single().handlerAddress
        assertTrue(graph.wasReached(handlerAddress), "the typed handler must execute")
        assertTrue(graph.outcomes.none { it is Outcome.Threw }, "a handled throw does not escape the method")
    }

    @Test
    fun unhandledExceptionEndsThePathAsThrew() {
        // Bug class: an unhandled throw reported as `Aborted` (the old engine's untyped `aborted`
        // boolean could not tell a guest exception from an engine failure), which would make a
        // perfectly ordinary escaping exception poison the run's completion status. Oracle: spec §4.4
        // -- a guest throw is `Threw`, carrying the thrown Value; only engine/limit failures abort.
        val thrown = Value.Known(IllegalStateException("boom"), runtimeException)
        val s = session("throwUncaught(Ljava/lang/RuntimeException;)V") { frame, view ->
            frame.assign(view.parameters.single().register, thrown)
        }
        val graph = s.run()
        val escaped = (graph.outcomes.single() as Outcome.Threw).exception as Value.Known
        assertEquals(illegalStateException, escaped.type)
        assertTrue(escaped.raw is HeapRef)
    }

    @Test
    fun exceptionOfAnUnrelatedTypeIsNotCaught() {
        // The fixture catches RuntimeException; an IOException must escape.
        val thrown = Value.Known(java.io.IOException("io"), VirtualType.of("Ljava/io/IOException;"))
        val s = session(caughtDescriptor) { frame, view -> frame.assign(view.parameters.single().register, thrown) }
        val graph = s.run()
        val escaped = (graph.outcomes.single() as Outcome.Threw).exception as Value.Known
        assertEquals(VirtualType.of("Ljava/io/IOException;"), escaped.type)
        assertTrue(escaped.raw is HeapRef)
        val handlerAddress = graph.methodView.tryBlocks.single().catches.single().handlerAddress
        assertFalse(graph.wasReached(handlerAddress))
    }

    @Test
    fun subtypeIsCaughtByItsSupertypeHandler() {
        // Bug class: resolving a catch clause by descriptor EQUALITY instead of subtyping, which
        // silently un-catches every real handler (almost no clause names the exact runtime class) and
        // is invisible unless a test throws a strict subtype. This is also the one test that proves
        // the session is wired to the REAL class model rather than a fake hierarchy. Oracle: the JDK
        // hierarchy -- NullPointerException IS-A RuntimeException.
        val thrown = Value.Known(NullPointerException("npe"), VirtualType.of("Ljava/lang/NullPointerException;"))
        val s = session(caughtDescriptor) { frame, view -> frame.assign(view.parameters.single().register, thrown) }
        val graph = s.run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        assertEquals(npe, returned.type, "NPE is a RuntimeException")
        assertTrue(returned.raw is HeapRef)
    }

    @Test
    fun aSymbolicRuntimeExceptionUsesItsPositiveTypeConstraint() {
        // EX-03: the parameter's declared RuntimeException type is a positive symbolic constraint.
        // The RuntimeException clause consumes that complete domain, so no escape remains.
        val s = session(caughtDescriptor) // no seeding: parameter stays Unknown
        val graph = s.run()
        val handlerAddress = graph.methodView.tryBlocks.single().catches.single().handlerAddress
        assertTrue(graph.wasReached(handlerAddress))
        assertTrue(graph.outcomes.none { it is Outcome.Threw })
        assertEquals(1, graph.outcomes.count { it is Outcome.Returned })
    }

    @Test
    fun aKnownThrowResolvesAgainstItsOwnAddressNotTheMethodStart() {
        // Bug class: handing handler resolution a CONSTANT address instead of the throwing node's own.
        // `HandlerResolutionTest` pins the pure resolver's address logic with fakes, and the ambiguous
        // path is pinned because it calls `candidateHandlers(node.address, ...)` -- but the KNOWN-type
        // path's session->resolver wiring was covered by nothing: every other throw fixture in
        // spine.smali throws at address 0, so `resolveHandler(0, ...)` resolved all of them identically
        // and the whole suite stayed green under that mutation.
        //
        // Concretely what the mutation allows: this method's `nop` puts the `throw` at address 1 with
        // its try block covering [1, 2), so resolving at address 0 finds no covering block, reports
        // `Outcome.Threw`, and does it on a run that still says Complete -- an unexplored feasible path
        // on a fully-trusted run, which is the one failure this VM must not have (spec §2.2). Oracle:
        // Dalvik try/catch -- a `throw` inside a try range whose clause names a supertype of the thrown
        // class is caught there, and try ranges are per-address, not per-method.
        val thrown = Value.Known(IllegalStateException("boom"), runtimeException)
        val s = session(afterNopDescriptor) { frame, view -> frame.assign(view.parameters.single().register, thrown) }
        val graph = s.run()

        // The fixture's shape IS the discriminator, so pin it: a try block that started at 0 would make
        // this test pass under the mutation it exists to catch.
        val tryBlock = graph.methodView.tryBlocks.single()
        assertEquals(1, tryBlock.startAddress, "the try block must start after the nop")
        assertFalse(tryBlock.covers(0), "and must NOT cover address 0, or this test proves nothing")
        val throwAddress = graph.methodView.addresses.first {
            graph.methodView.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.THROW
        }
        assertEquals(1, throwAddress, "the throw is at a non-zero address")

        assertTrue(graph.wasReached(tryBlock.catches.single().handlerAddress), "the handler must execute")
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        assertEquals(illegalStateException, returned.type)
        assertTrue(returned.raw is HeapRef, "the throw is caught and keeps imported guest identity")
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    // --- first-match partitions and residual escape ---

    @Test
    fun anAmbiguousThrowReachesEachFirstMatchPartitionAndTheEscape() {
        // EX-03 route order is covered by HandlerResolutionTest's independent table. This public case
        // proves that both feasible handlers and the residual escape survive scheduler integration.
        val graph = session(twoHandlersDescriptor).run()
        val handlers = graph.methodView.tryBlocks.flatMap { it.catches }.map { it.handlerAddress }

        handlers.forEach { assertTrue(graph.wasReached(it), "handler $it must execute") }
        assertEquals(
            setOf(
                Outcome.Threw(Value.Unknown(objectType)),
                Outcome.Returned(Value.Unknown(objectType)),
                Outcome.Returned(Value.Known("later", string)),
            ),
            graph.outcomes.toSet(),
        )
        assertEquals(3, graph.outcomes.size)
        assertEquals(CompletionStatus.Complete, graph.completion(), "ambiguity is not an abort")
    }

    // --- a Known value that cannot name a throwable's runtime class ---

    @Test
    fun throwingTheNullIdiomResolvesToTheNullPointerExceptionHandlerAlone() {
        // Bug class, and what the heap unlocked: `throw` of Dalvik's null idiom (`const/4 v0, 0x0`, i.e.
        // Known(0, "I")) used to fork EVERY covering handler plus the escape, because the engine could
        // not construct the NPE Dalvik actually throws and so `resolvableTypeOf` could not resolve a
        // type. That was conservative and correct, but it explored handlers no device can enter and it
        // bound an int 0 into their `move-exception`. It now synthesizes the NPE and resolves definitely.
        //
        // Driven from real smali rather than a seeded frame, because reachability from ordinary bytecode
        // is precisely the point. Expected addresses derived from the fixture, never from engine output.
        val graph = session("throwNullConst()Ljava/lang/Object;").run()
        assertEquals(CompletionStatus.Complete, graph.completion())

        // The fixture fact the claim rests on: the single clause names RuntimeException, and an NPE IS a
        // RuntimeException. A clause naming anything an NPE is not would make this handler unreachable
        // and the test would be asserting the opposite of what it says.
        val tryBlock = graph.methodView.tryBlocks.single()
        assertEquals(runtimeException, tryBlock.catches.single().exceptionType)
        val handlerAddress = tryBlock.catches.single().handlerAddress
        // EXCLUSIVITY, not existence. The returned outcome and empty escape set reject the old
        // ambiguous fork without depending on scheduler children or node-local outcome placement.
        assertTrue(CoreTester.escapingTypes(graph).isEmpty(),
            "the synthesized NPE is caught in this method, so NOTHING escapes: ${CoreTester.escapingTypes(graph)}")
        assertTrue(graph.wasReached(handlerAddress), "and the handler really did run")

        // The value that reaches the handler is the SYNTHESIZED NPE, not the int 0 the register held --
        // which is the half `wasReached` cannot see and the half that used to be wrong.
        val returned = graph.outcomes.single()
        assertTrue(returned is Outcome.Returned, "the only path is the handler's, and it returns: $returned")
        val caught = (returned as Outcome.Returned).value
        assertTrue(caught is Value.Known && caught.type == npe, "move-exception bound an NPE, not an int: $caught")
        val host = CoreTester.terminatingHostObject(graph, (caught as Value.Known).raw as HeapRef) as Throwable
        assertTrue(host is NullPointerException, "got ${host.javaClass.name}")
        // Message-less on purpose, and it is [ART]-faithful rather than arbitrary: `THROW()` calls the
        // NO-ARGUMENT ThrowNullPointerException(), which is ThrowException("Ljava/lang/NullPointerException;")
        // with no message (runtime/common_throws.cc:689-691). Unlike ArrayLengthOp's NPE, no corpus
        // expectation asserts a string here, so there is nothing pulling the other way.
        assertNull(host.message, "ART's `throw`-of-null NPE carries no message")
    }

    @Test
    fun theNullIdiomEntersOnlyTheHandlerThatCatchesNullPointerException() {
        // THE OBSERVABLE CONSEQUENCE, which one handler cannot state: handler REACHABILITY. The fixture
        // covers one null-idiom throw with two typed clauses -- `Ljava/io/IOException;` and
        // `Ljava/lang/RuntimeException;` -- and an NPE is only the second. Under the old ambiguous fork
        // BOTH ran, so a deobfuscator reading this graph was told a `catch (IOException)` block is live
        // when no device can enter it: the same invented-reachable-handler defect design decision 4
        // rejects for `new-array`, and the mirror of the unexplored-feasible-path defect.
        val graph = session("throwNullConstTwoHandlers()Ljava/lang/Object;").run()
        assertEquals(CompletionStatus.Complete, graph.completion())

        // Flattened across try blocks, not `tryBlocks.single()`: dexlib2 emits ONE TryBlock per `.catch`
        // directive even when the ranges are identical, so a two-clause fixture has two blocks.
        val clauses = graph.methodView.tryBlocks.flatMap { it.catches }
        val npeHandler = clauses.single { it.exceptionType == runtimeException }.handlerAddress
        val notAnNpeHandler = clauses.single { it.exceptionType == ioException }.handlerAddress
        val throwAddress = graph.methodView.addresses.first {
            graph.methodView.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.THROW
        }
        // The `assertFalse(wasReached(...))` below is silently vacuous for an address no instruction
        // lives at, so prove the address exists first -- the same guard Task 11 had to add.
        assertTrue(graph.methodView.addresses.contains(notAnNpeHandler),
            "address $notAnNpeHandler must be a real instruction or the negative below proves nothing")
        // And the fixture's second deliberate property: the UNREACHED handler is the throw's own
        // fall-through address, so a router that fell through instead of resolving would land exactly
        // where this test forbids.
        assertEquals(graph.methodView.nextAddress(throwAddress), notAnNpeHandler,
            "the IOException handler must sit at the throw's fall-through address, or this test is weaker than it reads")

        assertTrue(graph.wasReached(npeHandler), "the clause that catches a NullPointerException must run")
        assertFalse(graph.wasReached(notAnNpeHandler),
            "an NPE is not an IOException, so that handler is DEAD for this input and must not be explored")
        assertTrue(CoreTester.escapingTypes(graph).isEmpty(), "and nothing escapes: ${CoreTester.escapingTypes(graph)}")
        assertEquals(1, graph.outcomes.size, "exactly one path: ${graph.outcomes}")
    }

    @Test
    fun aDefinitelyNullThrowSynthesizesTheNpeInsteadOfBindingNullIntoTheHandler() {
        // The other spelling of the same provable case, and the one whose old behavior was worst:
        // `Known(null, RuntimeException)` used to fork the RuntimeException handler AND the escape, and
        // the value it bound into `move-exception` was the NULL. With a `catch (IllegalStateException)`
        // clause and a null of static type ISE that even entered a handler no real run reaches. Kept
        // separate from the null-idiom tests because the two spellings reach `ThrowOp` through different
        // predicates (`isNull` vs `isDalvikNull`) and a fix for one need not fix the other.
        val thrown = Value.Known(null, runtimeException)
        val s = session(caughtDescriptor) { frame, view -> frame.assign(view.parameters.single().register, thrown) }
        val graph = s.run()
        assertEquals(CompletionStatus.Complete, graph.completion())
        val handlerAddress = graph.methodView.tryBlocks.single().catches.single().handlerAddress
        assertTrue(graph.wasReached(handlerAddress), "an NPE is a RuntimeException, so this handler catches it")
        assertTrue(CoreTester.escapingTypes(graph).isEmpty(), "and the throw is definitely caught, so nothing escapes")

        // The load-bearing assertion: what the handler received. NOT the null that was thrown.
        val caught = (graph.outcomes.single() as Outcome.Returned).value
        assertTrue(caught is Value.Known && caught.type == npe,
            "the handler must receive the synthesized NPE, not the null reference: $caught")
        assertNotEquals(thrown, caught, "specifically: not the value the register held")
    }

    @Test
    fun aNonZeroPrimitiveThrowResolvesToTheNullPointerExceptionHandlerJustLikeZeroDoes() {
        // THE TEST THAT USED TO PIN THE DEFECT, inverted in Task 12's fix round. It asserted that
        // `Known(5, "I")` forks every covering handler plus the escape, on the reasoning that ART
        // "reinterprets the 5 as an object pointer and dereferences it", i.e. undefined behavior. THAT IS
        // WRONG, and it was wrong in the direction that invents reachable handlers.
        //
        // ART's SWITCH interpreter does not reinterpret the 5. `THROW()` fetches its operand with
        // `GetVRegReference(A())`, which reads `ShadowFrame`'s PARALLEL reference array; every primitive write
        // clears the matching slot
        // (`SetVReg`/`SetVRegFloat`/`SetVRegLong`/`SetVRegDouble` all end in `References()[i].Clear()`,
        // shadow_frame.h:190-228), and ART states the invariant in its own prose at :401-408: "when a
        // primitive is stored in vX, the second (reference) part of the array will be null". So the operand
        // IS null, `:939 ThrowNullPointerException()` runs, and the register's VALUE is irrelevant --
        // `const/4 v0, 0x5` reaches the same definite, message-less NPE as `const/4 v0, 0x0`.
        //
        // TIER, corrected 2026-08-04: `nterp` -- the DEFAULT interpreter -- reads the raw register instead and
        // WOULD deliver the 5 as a Throwable*, so this row is VERIFIER tier (spec §2.2 on input
        // method_verifier.cc:3253-3255 hard-fails as "thrown value of non-reference type"), NOT [ART]. The
        // sibling zero fixture IS [ART] under both tiers. THIS EXPECTATION DOES NOT CHANGE -- a definite NPE
        // is the sound direction and matches the switch tier exactly; only the justification does.
        //
        // This fixture is byte-identical to `throwNullConstTwoHandlers` except for one nibble, so the pair
        // now asserts that the outcome does NOT depend on that nibble -- which is the whole claim.
        val graph = session("throwNonZeroConstTwoHandlers()Ljava/lang/Object;").run()
        assertEquals(CompletionStatus.Complete, graph.completion())

        // Flattened for the same reason as the test above: one TryBlock per `.catch` directive.
        val clauses = graph.methodView.tryBlocks.flatMap { it.catches }
        val npeHandler = clauses.single { it.exceptionType == runtimeException }.handlerAddress
        val notAnNpeHandler = clauses.single { it.exceptionType == ioException }.handlerAddress
        assertTrue(graph.methodView.addresses.contains(notAnNpeHandler),
            "address $notAnNpeHandler must be a real instruction or the negative below proves nothing")

        assertTrue(graph.wasReached(npeHandler), "the RuntimeException handler must receive the synthesized NPE")
        assertFalse(graph.wasReached(notAnNpeHandler),
            "the `catch (IOException)` is DEAD for this input -- reporting it live is the defect this inverts")
        assertTrue(CoreTester.escapingTypes(graph).isEmpty(),
            "and no `I` escape either: the throw is definitely caught. Was ${CoreTester.escapingTypes(graph)}")

        // The handler receives the synthesized NPE, not the int 5 -- the half `wasReached` cannot see.
        val caught = (graph.outcomes.single() as Outcome.Returned).value
        assertTrue(caught is Value.Known && caught.type == npe, "move-exception bound an NPE, not the int 5: $caught")
        assertNotEquals(Value.Known(5, int), caught, "specifically: not the value the register held")
    }

    @Test
    fun aZeroTaggedWithAReferenceTypeEscapesAsInternalError() {
        // A host Integer under a String tag is neither a null reference nor a heap object, so the throw names a
        // non-throwable and synthesizes InternalError, as for any thrown non-throwable.
        val thrown = Value.Known(0, string)
        val s = session(caughtDescriptor) { frame, view -> frame.assign(view.parameters.single().register, thrown) }
        val graph = s.run()
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/InternalError;"), CoreTester.escapingTypes(graph))
    }

    @Test
    fun anUnknownPrimitiveTypedThrowStillForksBecauseNothingIsPROVED() {
        // THE SOUNDNESS LINE of the fix round, behaviourally. `readsAsNullReference` answers true for every
        // primitive-typed *Known*; the tempting next step is to answer true for a primitive-typed `Unknown`
        // too, on the grounds that its tag says primitive. That would be the narrowing direction spec §2.2
        // forbids: a tag is an upper BOUND, so `Unknown(I)` means "I could not prove what this register
        // holds", and the register may hold a reference the engine simply failed to track -- in which case a
        // device runs a handler this engine would have stopped exploring.
        //
        // Measured gap this closes: mutating the predicate to admit `Unknown` killed only `ValueTest`, because
        // every ambiguous-throw fixture in this file has a REFERENCE-typed parameter, so a primitive-typed
        // `Unknown` was never thrown anywhere. This test makes the line lethal from the outside too.
        val s = session(twoHandlersDescriptor) { frame, view ->
            frame.assign(view.parameters.single().register, Value.Unknown(int))
        }
        val graph = s.run()
        assertEquals(CompletionStatus.Complete, graph.completion())
        val handlers = graph.methodView.tryBlocks.flatMap { it.catches }.map { it.handlerAddress }
        for (handler in handlers) assertTrue(graph.wasReached(handler), "handler $handler must stay explored")
        // The escape stays feasible and carries the `Unknown` -- no NPE is invented for it. Ground truth,
        // through `outcomes` directly.
        assertEquals(
            setOf(
                Outcome.Threw(Value.Unknown(int)),
                Outcome.Returned(Value.Unknown(int)),
                Outcome.Returned(Value.Known("later", string)),
            ),
            graph.outcomes.toSet(),
            "the escape stays feasible and carries the Unknown -- no NPE is invented for it",
        )
        assertEquals(3, graph.outcomes.size)
        // THE TOTALITY PIN (Plan 8 Task 11, `CARRY-FORWARD.md` item 9). This test USED to note that
        // `escapingTypes` answered `[]` here "even though a path genuinely threw", because the four
        // hand-rolled copies read `(it.exception as? Value.Known)?.type?.descriptor` through `mapNotNull` and
        // dropped a non-`Known` escape. The single hoisted `CoreTester.escapingTypes` is now TOTAL over the
        // lattice and renders the genuinely-thrown `Unknown(I)`. Reverting it to the old blind body drops
        // this row and answers `[]`, which fails here -- the mutation Task 11 Step 8 names.
        assertEquals(
            listOf("Unknown(I)"), CoreTester.escapingTypes(graph),
            "the total helper must SEE the non-Known escape the four blind copies dropped",
        )
    }

    @Test
    fun synthesizingTheNpeIsNotASideEffect() {
        // Allocating the NPE is a fully modelled write to the run's OWN heap: fresh object, fresh HeapRef,
        // no other tracked object's contents change, so it declares no effect. A spurious STRONG would stop
        // every constant fold in any method that contains a `throw` of a null, because
        // `ExploredPaths.methodSideEffectLevel` maxes over addresses; a missing declaration would be the
        // opposite error, so the level is asserted either way.
        val graph = session("throwNullConst()Ljava/lang/Object;").run()
        val throwAddress = graph.methodView.addresses.first {
            graph.methodView.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.THROW
        }
        assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(throwAddress),
            "heap-allocating a guest throwable is modelled state, not an unmodelled effect")
        assertEquals(SideEffectLevel.NONE, graph.explored.methodSideEffectLevel(),
            "and nothing else in this method declares one either, so a consumer may still fold")
    }

    // --- catch-all: precedence, and the escape it makes infeasible ---

    @Test
    fun aCoveringCatchAllMakesTheUnhandledEscapeInfeasible() {
        // Bug class: forking "escapes unhandled" unconditionally for an unknown thrown type. A
        // covering catch-all catches everything, so an escape is NOT a feasible path and recording
        // Outcome.Threw for it would invent a throwing path the method cannot take -- which then
        // contaminates any consumer partitioning outcomes by kind. Oracle: `candidateHandlers`'
        // documented contract in HandlerResolution.kt (the caller adds the escape UNLESS a covering
        // catch-all is present) and Dalvik catch-all semantics.
        val s = session(catchAllDescriptor) // parameter stays Unknown, so the thrown type is unknown
        val graph = s.run()
        assertTrue(graph.outcomes.none { it is Outcome.Threw },
            "a catch-all guarantees the exception is handled in this method")
        assertEquals(
            setOf(
                Outcome.Returned(Value.Unknown(objectType)),
                Outcome.Returned(Value.Known("all", string)),
            ),
            graph.outcomes.toSet(),
            "both the typed handler and the catch-all handler are still feasible",
        )
        assertEquals(2, graph.outcomes.size)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun aKnownTypeNoTypedClauseMatchesFallsToTheCatchAll() {
        // The session half of HandlerResolution's catch-all precedence rule: a covering block whose
        // typed clause does not match must not stop the search, and the catch-all must catch rather
        // than the throw escaping. Oracle: Dalvik try/catch semantics (spec §7).
        val thrown = Value.Known(java.io.IOException("io"), VirtualType.of("Ljava/io/IOException;"))
        val s = session(catchAllDescriptor) { frame, view ->
            frame.assign(view.parameters.single().register, thrown)
        }
        val graph = s.run()
        assertEquals(listOf(Outcome.Returned(Value.Known("all", string))), graph.outcomes)
    }

    // --- the parked decision: a well-formed but UNLOADABLE catch type ---

    @Test
    fun anUnloadableCatchTypeIsAmbiguityNotAHostFailure() {
        // THE DECISION, parked since Task 1 and pinned here: `Lspine/absent;` is a syntactically
        // valid descriptor with no class behind it -- the normal case in a real obfuscated APK -- so
        // `ClassIndex.isSubtypeOf` cannot answer "does this handler catch?" and the class model
        // raises instead (ClassIndex.kt:29 reaches it unguarded). Two answers were defensible:
        // abort the path via the host-failure boundary, or treat the clause as "might catch, might
        // not" and join the ambiguous fork set. This asserts the SECOND: the handler is explored,
        // the escape is explored, and -- the discriminating assertion -- the run stays Complete.
        // Under the abort answer the handler would never run and completion would be
        // Partial(HOST_FAILURE), which switches off consumer trust for the whole method because one
        // catch descriptor was missing. Oracle: spec §2.2's soundness direction (never fewer
        // feasible paths) and spec §7's rule for classes absent from the registry (conservative
        // Unknown/Aborted, never a host crash).
        val thrown = Value.Known(IllegalStateException("boom"), runtimeException)
        val s = session(unloadableDescriptor) { frame, view ->
            frame.assign(view.parameters.single().register, thrown)
        }
        val graph = s.run()
        val handlerAddress = graph.methodView.tryBlocks.single().catches.single().handlerAddress
        assertTrue(graph.wasReached(handlerAddress), "an unanswerable catch type MIGHT catch, so the handler is feasible")
        val escaped = (graph.outcomes.single { it is Outcome.Threw } as Outcome.Threw).exception as Value.Known
        val returned = (graph.outcomes.single { it is Outcome.Returned } as Outcome.Returned).value
        assertEquals(escaped, returned, "all routes of one imported event share one HeapRef")
        assertEquals(CompletionStatus.Complete, graph.completion(),
            "an absent catch class is ordinary obfuscated input, not a host failure")
    }

    @Test
    fun aNonHeapValueOfAnUnloadableTypeEscapesAsInternalError() {
        // A host String under an unloadable class tag is not a heap object, so its tag never reaches the subtype
        // query: the throw names a non-throwable and synthesizes InternalError rather than ending the run.
        val thrown = Value.Known("stand-in", VirtualType.of("Lspine/absent;"))
        val s = session(caughtDescriptor) { frame, view ->
            frame.assign(view.parameters.single().register, thrown)
        }
        val graph = s.run()
        assertEquals(listOf("Ljava/lang/InternalError;"), CoreTester.escapingTypes(graph))
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    // --- malformed try-block tables and atomic routing ---

    @Test
    fun aMalformedHandlerAddressAbortsTheThrowingPathInsteadOfCrashing() {
        // Bug class: handing a handler address straight to decode, whose `requireNotNull` on the
        // instruction throws out of `step()` and kills the run. Oracle: spec §4.4 -- an
        // unrepresentable successor is Aborted, never a crash and never a wrong answer -- the same
        // containment a jump off the end of a method gets.
        //
        // MALFORMED_INPUT, not HOST_FAILURE: a try-block table naming a handler address the method
        // does not contain is the bytecode asking for something impossible, which is the same
        // statement about the input that an off-the-end jump makes. `OnUnbuildable.ALL_OR_NOTHING`
        // decides how much of the routing is abandoned; it does not decide whose fault the loss is.
        val view = withTryBlocks(caughtDescriptor) {
            listOf(TryBlock(0, 1, listOf(CatchClause(runtimeException, it.endAddress)), null))
        }
        val thrown = Value.Known(IllegalStateException("boom"), runtimeException)
        val frame = Frame.entryFrame(view).also { it.assign(view.parameters.single().register, thrown) }
        val graph = vmServices.newSession(view, Limits(), frame).run()
        assertEquals(listOf(Outcome.Aborted(AbortReason.MALFORMED_INPUT)), graph.outcomes)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
    }

    @Test
    fun anAmbiguousThrowWithAMalformedHandlerAbortsOnceWithoutEscaping() {
        // Atomic commit rejects the complete routing group. The public result is one malformed-input
        // abort, no escaped throw, and Partial completion; node-local outcome placement is irrelevant.
        val view = withTryBlocks(caughtDescriptor) {
            listOf(TryBlock(0, 1, listOf(CatchClause(runtimeException, it.endAddress)), null))
        }
        val graph = vmServices.newSession(view).run() // parameter Unknown -> ambiguous
        assertEquals(listOf(Outcome.Aborted(AbortReason.MALFORMED_INPUT)), graph.outcomes,
            "exactly one outcome: the malformed table aborted the throw instead of routing it")
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
    }

    @Test
    fun oneMalformedHandlerAbortsTheWholeForkRatherThanCommittingHalfOfIt() {
        // The malformed handler is the SECOND one forked, so an implementation that scheduled handlers
        // as it went would already have committed the first one: a half-applied fork plus an abort.
        // `OnUnbuildable.ALL_OR_NOTHING`: a malformed try-block table makes ALL of that table's routing
        // suspect, so the throwing path aborts and routes nothing, and the abort makes the run Partial.
        //
        // The reason is MALFORMED_INPUT: the policy answers HOW MUCH is abandoned (all of that table's
        // routing, plus its escape conclusion), the reason answers WHY (a handler address the method
        // does not contain -- bad input).
        val view = withTryBlocks(caughtDescriptor) {
            listOf(
                TryBlock(
                    0, 1,
                    listOf(
                        CatchClause(runtimeException, 1),                                       // real handler
                        CatchClause(VirtualType.of("Ljava/io/IOException;"), it.endAddress),     // malformed
                    ),
                    null,
                ),
            )
        }
        val frame = Frame.entryFrame(view).also { it.assign(view.parameters.single().register, Value.Top) }
        val graph = vmServices.newSession(view, Limits(), frame).run()
        assertEquals(listOf(Outcome.Aborted(AbortReason.MALFORMED_INPUT)), graph.outcomes)
        assertFalse(graph.wasReached(1), "the well-formed handler must not have been committed either")
    }
}
