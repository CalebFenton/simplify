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
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * `const*` and `const-class`. A class constant is kept as the host `Class` object, `Known(hostClass,
 * Ljava/lang/Class;)`, because a `Class` is immutable and needs no heap identity. Expected values follow
 * `docs/DALVIK-SEMANTICS.md`, "`const-class` (21c)" and "Resolution failure is one family, tiered once".
 */
class ConstOpTest {

    /** Each literal is read from `ops/const_test.smali`; the wide forms all write a 64-bit `J`. */
    @TestFactory
    fun eachConstFormWritesItsLiteral(): List<DynamicTest> = listOf(
        Triple("const()V", 0x42424242, "I"),
        Triple("const16()V", 0x4242, "I"),
        Triple("const16Negative()V", -0xf, "I"),
        Triple("const4()V", -3, "I"),
        Triple("constHigh16()V", 0x42420000, "I"),
        Triple("constString()V", "When I need to identify rebels, I look for men with principles.", STRING),
        Triple(
            "constStringJumbo()V",
            "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles.",
            STRING,
        ),
        Triple("constWide()V", 0x4242424242424242L, "J"),
        Triple("constWide16()V", 0x4242L, "J"),
        Triple("constWide32()V", 0x1b7740L, "J"),
        Triple("constWideHigh16()V", 0x4242000000000000L, "J"),
    ).map { (method, literal, type) ->
        DynamicTest.dynamicTest(method) {
            CoreTester.test(CLASS_NAME, method, CoreState().apply { setRegisters(0, literal, type) })
        }
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

    /**
     * The expected `Class` is loaded through the same [CoreTester.classLoader] the engine resolves through, so
     * identity holds; another loader would define a distinct `Class`. `loadClass` does not initialize.
     */
    @Test
    fun aConstClassOfAGuestClassIsItsClassObject() {
        val expectedClass: Class<*> = CoreTester.classLoader.loadClass(ClassNameUtils.internalToBinary(CLASS_NAME))
        CoreTester.test(CLASS_NAME, "constClassLocal()V", CoreState().apply { setRegisters(0, expectedClass, CLASS) })
    }

    /** A framework class resolves through the bootstrap loader to the host's own `Class`. */
    @Test
    fun aConstClassOfAFrameworkClassIsTheHostClassObject() {
        CoreTester.test(CLASS_NAME, "constClassRemote()V", CoreState().apply { setRegisters(0, Any::class.java, CLASS) })
    }

    /**
     * An absent class is a definite `NoClassDefFoundError` (device-measured by `Fx_ConstClassAbsentType`), an
     * `Error`, so the fixture's `catch (Exception)` handler is dead. The message is not asserted: the type is the
     * device-measured claim.
     */
    @Test
    fun anAbsentClassThrowsNoClassDefFoundErrorPastACatchException() {
        CoreTester.testThrows(
            CLASS_NAME, "constClassUnknown()V",
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
        )
        val graph = CoreTester.execute(CLASS_NAME, "constClassUnknown()V")
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "a definite throw is not a truncation, so the negatives below are dead-code claims",
        )
        assertTrue(
            graph.wasReached(ADDRESS_CONST_CLASS),
            "the const-class ran, so the throw is the op's and not the harness's",
        )
        assertFalse(
            graph.wasReached(ADDRESS_NOP_AFTER_CONST_CLASS),
            "a definite throw must not also explore the fall-through",
        )
        assertFalse(
            graph.wasReached(ADDRESS_CATCH_HANDLER),
            "`catch (Exception)` does not catch an Error, so the handler is dead code",
        )
        assertEquals(
            listOf("Ljava/lang/NoClassDefFoundError;"), CoreTester.escapingTypes(graph),
            "exactly one escaping throw, of exactly this type",
        )
        assertEquals(0, graph.nodePile(ADDRESS_CONST_CLASS).first().children.size, "definite, not a fork")
    }

    /**
     * Handler reachability is what a deobfuscator acts on: a resolution failure skips a `catch (Exception)`
     * wrapper and enters `catch (Throwable)` or `catch (Error)`. Bug class: modelling it as a checked exception,
     * which makes every `catch (Exception)` wrapper live where a device never enters it. The present-type control
     * shows the `catch (Exception)` fixture shape runs and falls through.
     */
    @Test
    fun aCatchExceptionDoesNotCatchAResolutionFailureButCatchThrowableAndCatchErrorDo() {
        val control = CoreTester.execute(CLASS_NAME, "constClassLocalWithCatch()V")
        assertEquals(
            CompletionStatus.Complete, control.completion(),
            "the control: a resolvable const-class under a catch(Exception) runs to completion",
        )
        assertTrue(control.wasReached(ADDRESS_NOP_AFTER_CONST_CLASS), "and it takes the fall-through")
        assertEquals(
            emptyList<String>(), CoreTester.escapingTypes(control),
            "nothing is thrown when the type resolves",
        )

        CoreTester.testThrows(
            CLASS_NAME, "constClassUnknown()V",
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
        )

        for (method in listOf("constClassUnknownCatchingThrowable()V", "constClassUnknownCatchingError()V")) {
            CoreTester.testCaught(
                CLASS_NAME, method,
                exceptionType = "Ljava/lang/NoClassDefFoundError;",
                caughtInRegister = 0,
                unreachedAddress = ADDRESS_NOP_AFTER_CONST_CLASS,
            )
        }
    }

    /** A class-constant load touches only its frame; a spurious `STRONG` would block every fold in the method. */
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
     * `const-class` does not initialize its class (device-measured by `Fx_ConstClassDoesNotInitialize`).
     * [ThrowingClinitProbe] is a compiled host class whose `<clinit>` throws; a smali fixture cannot serve because
     * `ClassBuilder` stubs guest `<clinit>` bodies. An initializing load would throw at decode and the run would not
     * complete.
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
        private const val STRING = "Ljava/lang/String;"
        private const val PROBE_BINARY_NAME = "org.cf.smalivm.core.op.ThrowingClinitProbe"

        /** Layout of the `const-class` fixtures: `const-class`@0, `nop`@2, `return-void`@3, `move-exception`@4. */
        private const val ADDRESS_CONST_CLASS = 0
        private const val ADDRESS_NOP_AFTER_CONST_CLASS = 2
        private const val ADDRESS_CATCH_HANDLER = 4
    }
}
