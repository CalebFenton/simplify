package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.util.ClassNameUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * `const*` (op family), migrated from `org.cf.smalivm.opcode.ConstOpTest` -- all fourteen tests,
 * expectations byte-identical to the oracle's, with the ONE adjudicated exception recorded on
 * [nonExistentClassNameThrowsException], **which Plan 9 Task 15 re-adjudicated a second time** (Plan 8's
 * decode-time `Partial(UNSUPPORTED_OP)` was not the final answer, and the STATUS ledger row that said it was
 * is corrected). The consequence that made the re-adjudication worth doing gets its own non-migrated test,
 * [aCatchExceptionDoesNotCatchAResolutionFailureButCatchThrowableAndCatchErrorDo].
 *
 * The eleven plain `const*` forms were implemented in Plan 3 and pass unchanged; the migration is a
 * `TestState`/`Tester` -> [CoreState]/[CoreTester] swap. `const-class` arrives here in Plan 8 Task 10.
 *
 * **`const-class`'s value (decisions 2 & 3).** A class constant is a reference to a `Class` object typed
 * `Ljava/lang/Class;`. It is kept HOST -- `Known(hostClass, "Ljava/lang/Class;")` -- not a `HeapRef`,
 * because a `Class` is immutable and `core/Value.kt`'s rule (only *mutable* VM-tracked objects must be
 * `HeapRef`s) does not force it onto the heap; Plan 11's reflection work inherits that decision. The host
 * `Class` is resolved at decode time by `OpBuilder.buildConstClass` through
 * `ProgramSnapshotResolver.loadClassConstant`, WITHOUT initializing it: ART's entire `CONST_CLASS` handler is a resolve, a
 * `nullptr` check and a `SetVRegReference` (`interpreter_switch_impl-inl.h:755-767`;
 * `docs/DALVIK-SEMANTICS.md#const-class-21c`), so a non-initializing load is the faithful
 * answer and the node's effect is genuinely `NONE`. ⚠️ **Not because ART passes `can_run_clinit = false`,
 * which is what this KDoc used to argue and which is a non-sequitur** -- `NEW_INSTANCE` passes the same
 * `false` and initializes downstream in `AllocObjectFromCode`. The oracle used the *initializing* two-arg
 * `ClassUtils.getClass` overload (Correctness Defect 21, `docs/architecture/opcodes.md:176`); this core
 * does not, and [aConstClassLoadDoesNotInitializeItsClass] pins the difference.
 */
class ConstOpTest {
    private lateinit var expected: CoreState

    @BeforeEach
    fun setUp() {
        expected = CoreState()
    }

    // ----------------------------------------------------------------------------------------------
    // The eleven plain forms (Plan 3). Byte-identical expectations.
    // ----------------------------------------------------------------------------------------------

    @Test
    fun canConst() {
        expected.setRegisters(0, 0x42424242, "I")
        CoreTester.test(CLASS_NAME, "const()V", expected)
    }

    @Test
    fun canConst16() {
        expected.setRegisters(0, 0x4242, "I")
        CoreTester.test(CLASS_NAME, "const16()V", expected)
    }

    @Test
    fun canConst4() {
        expected.setRegisters(0, -3, "I")
        CoreTester.test(CLASS_NAME, "const4()V", expected)
    }

    @Test
    fun canConstHigh16() {
        expected.setRegisters(0, 0x42420000, "I")
        CoreTester.test(CLASS_NAME, "constHigh16()V", expected)
    }

    @Test
    fun canConstString() {
        expected.setRegisters(
            0, "When I need to identify rebels, I look for men with principles.",
            "Ljava/lang/String;",
        )
        CoreTester.test(CLASS_NAME, "constString()V", expected)
    }

    @Test
    fun canConstStringJumbo() {
        expected.setRegisters(
            0,
            "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles.",
            "Ljava/lang/String;",
        )
        CoreTester.test(CLASS_NAME, "constStringJumbo()V", expected)
    }

    @Test
    fun canConstWide() {
        expected.setRegisters(0, 0x4242424242424242L, "J")
        CoreTester.test(CLASS_NAME, "constWide()V", expected)
    }

    @Test
    fun canConstWide16() {
        expected.setRegisters(0, 0x4242L, "J")
        CoreTester.test(CLASS_NAME, "constWide16()V", expected)
    }

    @Test
    fun canConstWide32() {
        expected.setRegisters(0, 0x1b7740L, "J")
        CoreTester.test(CLASS_NAME, "constWide32()V", expected)
    }

    @Test
    fun canConstWideHigh16() {
        expected.setRegisters(0, 0x4242000000000000L, "J")
        CoreTester.test(CLASS_NAME, "constWideHigh16()V", expected)
    }

    /**
     * Every wide const form writes the same 64 bits, so two paths that load one literal through different
     * forms agree at the join. A per-mnemonic tag (`D` for `const-wide`, `J` for the sized forms) makes the
     * two values' types incompatible and the join answers `Top`, losing a constant both paths hold.
     */
    @Test
    fun wideConstFormsOfOneLiteralAgreeAtTheJoin() {
        CoreTester.assertReturns(CLASS_NAME, "wideFormsOfOneLiteral(I)J", Value.Known(5L, VirtualType.of("J")))
    }

    @Test
    fun canNegativeConst16() {
        expected.setRegisters(0, -0xf, "I")
        CoreTester.test(CLASS_NAME, "const16Negative()V", expected)
    }

    // ----------------------------------------------------------------------------------------------
    // const-class (Plan 8 Task 10).
    // ----------------------------------------------------------------------------------------------

    /**
     * A guest class the fixture ships. The `Class` object must be identity-stable with what the engine
     * resolves, so the expected value is loaded through the SAME [CoreTester.classLoader] the core's
     * `OpBuilder.buildConstClass` resolves through ([CoreTester.classLoader] is
     * `ClassIndex.classLoader`); a different loader instance would `defineClass` a distinct `Class`.
     * `loadClass` does not initialize, so reading the expected value here runs no `<clinit>`.
     */
    @Test
    fun canConstClassLocal() {
        val expectedClass: Class<*> = CoreTester.classLoader.loadClass(ClassNameUtils.internalToBinary(CLASS_NAME))
        expected.setRegisters(0, expectedClass, CLASS)
        CoreTester.test(CLASS_NAME, "constClassLocal()V", expected)
    }

    /**
     * A framework class. `const-class Ljava/lang/Object;` resolves to the system `java.lang.Object` --
     * `Any::class.java` -- through the bootstrap loader regardless of which `SmaliClassLoader` asks, so
     * the expected value needs no special sourcing and is byte-identical to the oracle's.
     */
    @Test
    fun canConstClassRemote() {
        expected.setRegisters(0, Any::class.java, CLASS)
        CoreTester.test(CLASS_NAME, "constClassRemote()V", expected)
    }

    /**
     * **ADJUDICATED EXPECTATION CHANGE, and this is its SECOND adjudication -- the ledger row that recorded
     * Plan 8's as final was wrong** (Plan 9 Task 15, the ROW-23 requirement; the authority is
     * [`DALVIK-SEMANTICS.md`'s "Resolution failure is one family, tiered once"](../../../../../../../../../docs/DALVIK-SEMANTICS.md#resolution-failure-is-one-family-tiered-once),
     * tier `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, cross-validated
     * `[DEVICE](Tier B, nterp default, API 36)`,
     * `tools/art-runner/fixtures/Fx_ConstClassAbsentType.smali` against `Fx_ConstClassPresentType.smali`).
     *
     * **What the ORACLE did.** `Tester.testSimpleException(..., ClassNotFoundException, null, 2)`: it
     * resolved `Lunknown/class;` with an *initializing* `ClassUtils.getClass` at execution time, caught the
     * `ClassNotFoundException`, returned the caught exception object AS the constant
     * (`opcode/ConstOp.kt:75-77`), `execute` saw `constant is Throwable` and threw it
     * (`opcode/ConstOp.kt:34-36`), and the fixture's `.catch Ljava/lang/Exception;` caught it into `v0` at
     * the handler. So the oracle's answer was a **caught, checked `Exception`** -- and it is an artefact of
     * resolving through a host `ClassLoader`, not a statement about Dalvik (adjudication 7).
     *
     * **What Plan 8 did, and why it is now superseded.** It degraded the operand to a per-address
     * `UnsupportedOp` at decode time, making the whole run `Partial(UNSUPPORTED_OP)` with nothing thrown and
     * the handler unreached. That was defensible only while the exception TYPE was [ART]-only; it is now
     * measured on both interpreter tiers, and a `[DEVICE]` measurement outranks the caution that stood in
     * for one. The refusal also withdrew trust from every other address in the method in order to decline
     * one -- on the single commonest hostile input there is.
     *
     * **What this core does now: a definite guest `NoClassDefFoundError`, which ESCAPES the fixture's
     * `catch (Exception)`.** So the migration lands on [CoreTester.testThrows] rather than `testCaught`,
     * and *that choice of helper is itself the finding* -- the same tell
     * `core/op/NewArrayOpTest.nonExistentClassNameThrowsException` and
     * `core/op/FilledNewArrayOpTest.anUnloadableComponentTypeThrowsNoClassDefFoundError` already carry.
     * The fixture's `:catch_0` block is dead code under this core, and it is *supposed* to be: see
     * [aCatchExceptionDoesNotCatchAResolutionFailureButCatchThrowableAndCatchErrorDo] for the direct test of
     * the consequence, which is the reason this task exists.
     *
     * **The MESSAGE is deliberately not asserted.** The oracle asserted none either (the third argument to
     * `testSimpleException` was `null`), so there is no corpus text to hold byte-identical, and per the
     * Global Constraints an exception message is never evidence of Dalvik fidelity -- [RT] records that the
     * text was changed between runtimes. The throwable CLASS is the claim.
     */
    @Test
    fun nonExistentClassNameThrowsException() {
        CoreTester.testThrows(
            CLASS_NAME, "constClassUnknown()V",
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
        )
        val graph = CoreTester.execute(CLASS_NAME, "constClassUnknown()V")
        // Not redundant with the gate inside `testThrows`: the negatives below are dead-code claims, and a
        // `wasReached` false is indistinguishable from "the run never got there" on a truncated exploration.
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "a definite throw is not a truncation -- and NOT Partial(UNSUPPORTED_OP), which is what Plan 8 " +
                "answered here",
        )
        assertTrue(
            graph.wasReached(ADDRESS_CONST_CLASS),
            "the const-class ran: a positive reached-claim, so the throw is the op's and not the harness's",
        )
        assertFalse(
            graph.wasReached(ADDRESS_NOP_AFTER_CONST_CLASS),
            "a definite throw must not also explore the fall-through",
        )
        // THE DIVERGENCE, stated as an exclusion rather than as an existence claim: the oracle's handler
        // caught its ClassNotFoundException here. `NoClassDefFoundError` is an Error, so `catch (Exception)`
        // cannot cover the op and the handler is unreachable.
        assertFalse(
            graph.wasReached(ADDRESS_CATCH_HANDLER),
            "`catch (Exception)` does not catch an Error, so the handler is dead code -- the whole point",
        )
        assertEquals(
            listOf("Ljava/lang/NoClassDefFoundError;"), CoreTester.escapingTypes(graph),
            "exactly one escaping throw, of exactly this type -- no ClassNotFoundException anywhere",
        )
        assertEquals(0, graph.nodePile(ADDRESS_CONST_CLASS).first().children.size, "definite, not a fork")
    }

    /**
     * 🔴 **THE REACHABILITY CONSEQUENCE, TESTED DIRECTLY -- this is what the ROW-23 requirement is FOR.**
     *
     * Not "is the exception named `NoClassDefFoundError`". The question is whether a guest handler is
     * **reachable**, because that is what a deobfuscator acts on: obfuscator output is full of
     * `catch (Exception)` wrappers, and an `Error` and an `Exception` put such a wrapper on opposite sides of
     * live-versus-dead. Model this as the oracle's checked `ClassNotFoundException` and every one of those
     * wrappers becomes reachable in the engine's view while a device never enters it -- spec §2.2's
     * invented-infeasible-path direction, the mirror defect Plan 7 shipped twice.
     *
     * Three methods, one absent-type `const-class`, one `.catch` type each, plus a present-type CONTROL
     * under the identical `catch (Exception)` shape. The control is not decoration: without it, *"the
     * Exception handler is not reached"* also passes against a build where `const-class` never executes, or
     * where try/catch routing is broken outright.
     *
     * Tier: `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, cross-validated
     * `[DEVICE](Tier B, nterp default, API 36)` --
     * [`DALVIK-SEMANTICS.md`'s "Resolution failure is one family, tiered once"](../../../../../../../../../docs/DALVIK-SEMANTICS.md#resolution-failure-is-one-family-tiered-once),
     * whose own summary of this is *"a `catch (Exception)` wrapper does not catch it, which is the
     * handler-reachability consequence this document asserted from source and now measures."*
     *
     * **`testThrows` for the miss and `testCaught` for the two hits, which is the assertion asymmetry that
     * makes this non-vacuous.** `testCaught` asserts the type is ABSENT from the escaping outcomes *and*
     * that the handler ran, so it cannot be satisfied by "nothing happened" -- and it is the direction a
     * mutation to the throwable's type kills, because a `ClassNotFoundException` would be caught by the
     * `Exception` clause and would leave the `Error` clause unreached.
     */
    @Test
    fun aCatchExceptionDoesNotCatchAResolutionFailureButCatchThrowableAndCatchErrorDo() {
        // THE CONTROL, first: the same `catch (Exception)` shape over a PRESENT type completes normally and
        // never enters its handler either -- so the miss below is about the Error/Exception split and not
        // about this fixture shape being inert.
        val control = CoreTester.execute(CLASS_NAME, "constClassLocalWithCatch()V")
        assertEquals(
            CompletionStatus.Complete, control.completion(),
            "the CONTROL: a resolvable const-class under a catch(Exception) runs to completion",
        )
        assertTrue(control.wasReached(ADDRESS_NOP_AFTER_CONST_CLASS), "and it takes the fall-through")
        assertEquals(
            emptyList<String>(), CoreTester.escapingTypes(control),
            "nothing is thrown at all when the type resolves -- it is resolution failing, not the opcode",
        )

        // THE MISS. `catch (Exception)` cannot catch an Error, so the throw escapes the method.
        CoreTester.testThrows(
            CLASS_NAME, "constClassUnknown()V",
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
        )

        // THE TWO HITS. `Throwable` and `Error` are both supertypes of `NoClassDefFoundError`
        // (java/lang/{Throwable,Error,LinkageError,NoClassDefFoundError}.smali all ship in the framework the
        // tests load, so the hierarchy walk is real), so each handler covers the op and catches into v0.
        // `testCaught` asserts the type does NOT escape and that the handler executed -- the positive half
        // the escaping test cannot supply.
        for (method in listOf("constClassUnknownCatchingThrowable()V", "constClassUnknownCatchingError()V")) {
            CoreTester.testCaught(
                CLASS_NAME, method,
                exceptionType = "Ljava/lang/NoClassDefFoundError;",
                caughtInRegister = 0,
                unreachedAddress = ADDRESS_NOP_AFTER_CONST_CLASS,
            )
        }
    }

    // ----------------------------------------------------------------------------------------------
    // const-class side effects (decision 3). NONE is declared and its absence pinned.
    // ----------------------------------------------------------------------------------------------

    /**
     * **Side effects: NONE**, declared and pinned, the precedent `core/op/InstanceOfOpTest`'s
     * `aTypeTestDeclaresNoSideEffectOnEveryRow` sets. A class-constant load touches only its own frame:
     * it neither mutates tracked state nor -- see [aConstClassLoadDoesNotInitializeItsClass] -- runs a host
     * `<clinit>`. A spurious `STRONG` here would silently stop every constant fold in any method containing
     * a `const-class`, because `methodSideEffectLevel` maxes over addresses
     * (`core/ExecutionGraph.sideEffectLevel`'s KDoc narrates that exact regression for `aput`).
     */
    @Test
    fun aConstClassLoadReportsNoSideEffect() {
        val graph = CoreTester.execute(CLASS_NAME, "constClassLocal()V")
        assertEquals(
            SideEffectLevel.NONE, graph.explored.sideEffectLevel(ADDRESS_CONST_CLASS),
            "a class-constant load mutates nothing outside its own frame",
        )
        val trusted = graph.trusted()
        assertNotNull(trusted, "nothing threw and nothing aborted")
        assertEquals(
            SideEffectLevel.NONE, trusted!!.methodSideEffectLevel(),
            "and the whole-method query a strategy actually reads agrees",
        )
    }

    /**
     * **THE LOAD MUST NOT INITIALIZE THE HOST CLASS** (decision 3; oracle Correctness Defect 21,
     * `docs/architecture/opcodes.md:176`; ART's whole `CONST_CLASS` handler body,
     * `docs/DALVIK-SEMANTICS.md#const-class-21c` -- **not** its `can_run_clinit = false`, which `NEW_INSTANCE`
     * also passes while initializing).
     *
     * [ThrowingClinitProbe] is a **real** compiled class (resolved through the class loader's parent, like
     * `Ljava/lang/Object;` in [canConstClassRemote]) whose `<clinit>` throws -- a smali fixture cannot serve,
     * because `ClassBuilder` stubs every guest `<clinit>` to a bare `RETURN` (`ClassBuilder.kt:152-155`), so
     * initializing one is unobservable. The core resolves this class through the NON-initializing load, so it
     * is *defined* but never *initialized*: the const-class produces the `Class` object and the run
     * completes. Were the load switched to the initializing overload (the oracle's two-arg
     * `ClassUtils.getClass`), resolving the class at decode time would run `<clinit>`, which throws, and the
     * node could not be built -- this test dies. `loadClass` does not initialize either, so computing the
     * expected value below runs no `<clinit>`.
     */
    @Test
    fun aConstClassLoadDoesNotInitializeItsClass() {
        val probeClass: Class<*> = CoreTester.classLoader.loadClass(PROBE_BINARY_NAME)
        val graph = CoreTester.execute(CLASS_NAME, "constClassThrowingClinit()V")
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "a non-initializing load defines the class without running its throwing <clinit>",
        )
        val trusted = graph.trusted()
        assertNotNull(trusted, "the run completed, so it yields a trusted view")
        assertEquals(
            Value.Known(probeClass, VirtualType.of(CLASS)), trusted!!.terminatingConsensus(0),
            "the class constant is the resolved Class object, uninitialized",
        )
    }

    private companion object {
        private const val CLASS_NAME = "Lconst_test;"
        private const val CLASS = "Ljava/lang/Class;"
        private const val PROBE_BINARY_NAME = "org.cf.smalivm.core.op.ThrowingClinitProbe"

        /**
         * `const-class` is `21c`, two code units, so the `nop` after it sits at 2. In the four
         * `catch`-bearing witnesses the layout is identical: `const-class` @0, `nop` @2, `return-void` @3,
         * `move-exception` (11x) @4, `goto` (10t) @5.
         */
        private const val ADDRESS_CONST_CLASS = 0
        private const val ADDRESS_NOP_AFTER_CONST_CLASS = 2
        private const val ADDRESS_CATCH_HANDLER = 4
    }
}
