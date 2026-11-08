package org.cf.simplify

import org.cf.simplify.strategy.OptimizationStrategy
import org.cf.simplify.strategy.StrategyInput
import org.cf.smalivm.core.*
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class OptimizerTest {
    @TempDir lateinit var temporaryDirectory: Path
    private fun services() = OptimizerTester.services(File("src/test/resources/task64"))
    private fun optimizer(services: VmServices, vararg strategies: OptimizationStrategy, passes: Int = 5) =
        Optimizer(services, Limits(500, 1_000_000, 0, 20), passes, strategies.toList())
    private fun replace(input: StrategyInput, value: Int) {
        input.transaction.replaceInstruction(0, listOf(InstructionTemplate.Encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, value))))
        input.recordOptimization("constants")
    }

    @Test fun unchangedExecutionWritesReloadableSnapshot() {
        val services = services()
        val result = optimizer(services, OptimizationStrategy {}).optimize(NO_OP)
        val file = temporaryDirectory.resolve("unchanged.dex").toFile()
        ProgramDexWriter().write(services.currentSnapshot, file)
        assertEquals(services.currentSnapshot.concreteApplicationMethodSignatures(), OptimizerTester.services(file).currentSnapshot.concreteApplicationMethodSignatures())
        assertEquals(0, result.committedEditCount)
        assertEquals(emptyList<Optimizer.Rejection>(), result.rejections)
    }

    @Test fun commitRestartsAtFirstStrategyWithFreshGraphAndMethod() {
        val services = services()
        val firstGraphs = mutableListOf<TrustedGraphView>()
        val secondGraphs = mutableListOf<TrustedGraphView>()
        val first = OptimizationStrategy { firstGraphs += it.graph }
        val second = OptimizationStrategy {
            secondGraphs += it.graph
            if (secondGraphs.size == 1) replace(it, 1)
        }
        val result = optimizer(services, first, second).optimize(CHANGED)
        assertEquals(1, result.committedEditCount)
        assertEquals(2, firstGraphs.size)
        assertEquals(2, secondGraphs.size)
        assertSame(firstGraphs[0], secondGraphs[0])
        assertSame(firstGraphs[1], secondGraphs[1])
        assertNotSame(firstGraphs[0], firstGraphs[1])
        assertNotSame(firstGraphs[0].methodView, firstGraphs[1].methodView)
        assertNotSame(firstGraphs[0].programRevision, firstGraphs[1].programRevision)
        assertNotSame(firstGraphs[0].methodRevision, firstGraphs[1].methodRevision)
        assertEquals(1, result.optimizationCounts["constants"])
        assertEquals(1, (OptimizerTester.execute(services, CHANGED).trusted()!!.terminatingConsensus(0) as Value.Known).raw)
    }

    @Test fun rejectedAndUnchangedEditsNeverPublishCountsAndDoNotBlockLaterCommit() {
        val services = services()
        var attempts = 0
        val rejected = OptimizationStrategy {
            it.transaction.removeInstruction(99)
            it.recordOptimization("rejected")
        }
        val unchanged = OptimizationStrategy { it.recordOptimization("unchanged") }
        val later = OptimizationStrategy { if (attempts++ == 0) replace(it, 2) }
        val result = optimizer(services, rejected, unchanged, later).optimize(CHANGED)
        assertEquals(1, result.committedEditCount)
        assertEquals(listOf(Optimizer.Rejection(rejected.name, MethodEditRejectionReason.InvalidCodeAddress(99))), result.rejections)
        assertEquals(mapOf("constants" to 1), result.optimizationCounts)
    }

    @Test fun rejectionBeforeACommitSurvivesThePassLimit() {
        val rejected = OptimizationStrategy { it.transaction.removeInstruction(99) }
        val result = optimizer(services(), rejected, OptimizationStrategy { replace(it, 2) }, passes = 1).optimize(CHANGED)
        assertTrue(result.passLimitReached)
        assertEquals(listOf(Optimizer.Rejection(rejected.name, MethodEditRejectionReason.InvalidCodeAddress(99))), result.rejections)
    }

    @Test fun rejectedEditDoesNotPublishOrRetry() {
        val services = services()
        val snapshot = services.currentSnapshot
        var attempts = 0
        val rejecting = OptimizationStrategy { attempts++; it.transaction.removeInstruction(99) }
        val result = optimizer(services, rejecting).optimize(CHANGED)
        assertSame(snapshot, services.currentSnapshot)
        assertEquals(1, attempts)
        assertEquals(0, result.committedEditCount)
        assertFalse(result.passLimitReached)
        assertEquals(listOf(Optimizer.Rejection(rejecting.name, MethodEditRejectionReason.InvalidCodeAddress(99))), result.rejections)
    }

    @Test fun strategyFailureDiscardsOpenTransaction() {
        val services = services()
        val snapshot = services.currentSnapshot
        val failure = assertThrows(Exception::class.java) {
            optimizer(services, OptimizationStrategy { replace(it, 1); throw Exception("checked failure") }).optimize(CHANGED)
        }
        assertEquals("checked failure", failure.message)
        assertSame(snapshot, services.currentSnapshot)
        var attempts = 0
        val result = optimizer(services, OptimizationStrategy { if (attempts++ == 0) replace(it, 2) }).optimize(CHANGED)
        assertEquals(1, result.committedEditCount)
    }

    @Test fun incompleteExecutionNeverInvokesStrategies() {
        val services = services()
        var attempts = 0
        val result = Optimizer(services, Limits(1, 100, 0, 20), 5, listOf(OptimizationStrategy { attempts++ })).optimize(LOOP)
        assertEquals(0, attempts)
        assertTrue(result.completionStatus is CompletionStatus.Partial)
        assertEquals(0, result.committedEditCount)
    }

    @Test fun passLimitStopsStrategiesAndKeepsTheLastCommit() {
        val services = services()
        var attempts = 0
        val result = optimizer(services, OptimizationStrategy { replace(it, ++attempts) }, passes = 2).optimize(CHANGED)
        assertEquals(2, attempts)
        assertEquals(2, result.committedEditCount)
        assertTrue(result.passLimitReached)
        assertEquals(2, (OptimizerTester.execute(services, CHANGED).trusted()!!.terminatingConsensus(0) as Value.Known).raw)
    }

    @Test fun lastAllowedCommitIsExecutedBeforeCompletionIsReported() {
        var attempts = 0
        val growing = OptimizationStrategy {
            val constant = InstructionTemplate.Encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 1))
            it.transaction.replaceInstruction(0, List(if (++attempts == 1) 1 else 4) { constant })
        }
        // changed() takes two steps; the second revision takes five, which exceeds the three-step limit.
        val result = Optimizer(services(), Limits(500, 3, 0, 20), 2, listOf(growing)).optimize(CHANGED)
        assertEquals(2, result.committedEditCount)
        assertTrue(result.completionStatus is CompletionStatus.Partial, "${result.completionStatus}")
    }

    companion object {
        val NO_OP = MethodSignature.parse("Ltask64/Skeleton;->aNoOp()V")
        val CHANGED = MethodSignature.parse("Ltask64/Skeleton;->changed()I")
        val LOOP = MethodSignature.parse("Ltask64/Skeleton;->loop()V")
    }
}
