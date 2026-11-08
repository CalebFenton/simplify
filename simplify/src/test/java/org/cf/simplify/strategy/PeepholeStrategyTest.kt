package org.cf.simplify.strategy

import org.cf.simplify.Optimizer
import org.cf.simplify.OptimizerTester
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.Limits
import java.io.File
import org.cf.smalivm.core.MethodView
import org.cf.smalivm.core.Value
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.cf.simplify.methodView
import org.cf.simplify.optimize

class PeepholeStrategyTest {
    private fun optimize(method: String) = OptimizerTester.optimize(
        "Lpeephole_kotlin_regression;", method, PeepholeStrategy())

    @Test fun constantPredicatesPreserveBothOutcomesThroughDexRoundTrip() {
        for ((name, expected) in listOf("taken" to 0, "fallthrough" to 2)) {
            val optimized = optimize("$name()I")
            assertFalse(optimized.method.instructionAddresses.any { optimized.method.opcodeAt(it) == Opcode.IF_EQZ })
            assertTrue(optimized.method.instructionAddresses.any {
                optimized.method.opcodeAt(it) == if (name == "taken") Opcode.GOTO_32 else Opcode.NOP
            })
            assertEquals(expected, (optimized.reexecute().trusted()!!.terminatingConsensus(0) as Value.Known).raw)
            assertEquals(expected, (optimized.dexRoundTrip().trusted()!!.terminatingConsensus(0) as Value.Known).raw)
        }
    }

    @Test fun unknownPredicateRetainsBothBranches() {
        val optimized = optimize("unknownPredicate(I)I")
        assertTrue(optimized.method.instructionAddresses.any { optimized.method.opcodeAt(it) == Opcode.IF_EQZ })
    }

    @Test fun ownClassLiteralUsesActualMoveResultDestination() {
        val optimized = optimize("ownClass()Ljava/lang/Class;")
        assertFalse(hasInvoke(optimized.method, "Ljava/lang/Class;", "forName"))
        val address = optimized.method.instructionAddresses.single { optimized.method.opcodeAt(it) == Opcode.CONST_CLASS }
        val instruction = optimized.method.instructionAt(address)
        assertEquals(1, (instruction as OneRegisterInstruction).registerA)
        assertEquals("Lpeephole_kotlin_regression;", ((instruction as ReferenceInstruction).reference as TypeReference).type)
        assertEquals(CompletionStatus.Complete, optimized.dexRoundTrip().completion())
    }

    @Test fun otherClassForNameRetainsInitializationAndFailureBehavior() {
        val optimized = optimize("otherClass()Ljava/lang/Class;")
        assertTrue(hasInvoke(optimized.method, "Ljava/lang/Class;", "forName"))
        assertTrue(optimized.method.instructionAddresses.any { optimized.method.opcodeAt(it) == Opcode.MOVE_RESULT_OBJECT })
    }

    @Test fun declaredExactCastIsRemovedButActiveCastIsRetained() {
        val redundant = optimize("cast(Ljava/lang/String;)Ljava/lang/String;")
        assertFalse(redundant.method.instructionAddresses.any { redundant.method.opcodeAt(it) == Opcode.CHECK_CAST })
        assertEquals(CompletionStatus.Complete, redundant.dexRoundTrip().completion())
        val active = optimize("activeCast(Ljava/lang/Object;)Ljava/lang/String;")
        assertTrue(active.method.instructionAddresses.any { active.method.opcodeAt(it) == Opcode.CHECK_CAST })
    }

    @Test fun stringConstructorAndRuntimeGetClassAreNotConstantized() {
        assertTrue(hasInvoke(optimize("stringIdentity()Ljava/lang/String;").method, "Ljava/lang/String;", "<init>"))
        assertTrue(hasInvoke(optimize("getRuntimeClass()Ljava/lang/Class;").method, "Ljava/lang/Object;", "getClass"))
    }

    private fun hasInvoke(method: MethodView, owner: String, name: String) = method.instructionAddresses.any {
        val reference = (method.instructionAt(it) as? ReferenceInstruction)?.reference as? MethodReference
        reference?.definingClass == owner && reference.name == name
    }

    @Test fun castRemovalCommitsInAMethodWhoseDeadPaddingNopCarriesALine() {
        // dx emits `.line` on the alignment nop between return-void and the array payload.
        val services = OptimizerTester.services(File("src/test/resources/obfuscated-example"))
        val signature = "Lorg/cf/obfuscated/Reflection;->reflectedFieldLookup()V"
        val result = Optimizer(services, Limits(), 1, listOf(PeepholeStrategy())).optimize(signature)
        assertEquals(emptyList<Optimizer.Rejection>(), result.rejections)
        assertEquals(1, result.committedEditCount)
        val method = requireNotNull(services.classIndex.methodView(signature))
        assertFalse(method.instructionAddresses.any { method.opcodeAt(it) == Opcode.CHECK_CAST })
    }
}
