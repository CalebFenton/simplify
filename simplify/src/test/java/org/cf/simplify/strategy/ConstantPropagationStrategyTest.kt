package org.cf.simplify.strategy

import java.nio.file.Path
import java.io.File
import org.cf.simplify.Optimizer
import org.cf.smalivm.core.*
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class ConstantPropagationStrategyTest {
    @TempDir lateinit var temporaryDirectory: Path

    @Test fun equalStringContentsFromLiteralAndFreshPathsDoNotProveCanonicalIdentity() {
        val services = services()
        for (name in listOf("mergedLiteralFirst", "mergedFreshFirst")) {
            val method = "Ltask65/ConstantIdentity;->$name(I)Ljava/lang/String;"
            val original = view(services, method)
            val move = original.instructionAddresses.single { original.opcodeAt(it) == Opcode.MOVE_OBJECT }
            val graph = services.newSession(original, LIMITS).run()
            val consensus = requireNotNull(graph.trusted()).consensus(move, 2) as Value.Known
            assertEquals(123456789, (consensus.raw as String).toInt())
            optimize(services, method)
            assertTrue(has(view(services, method), Opcode.MOVE_OBJECT), name)
            assertTrue(has(view(services, method), Opcode.MOVE_RESULT_OBJECT), name)
            assertEquals(123456789, (returned(services, method, 2).raw as String).toInt())
            assertTrue(has(view(reload(services), method), Opcode.MOVE_OBJECT), name)
        }
    }

    @Test fun linearStringOriginProofFollowsCopiesAndIgnoresUnrelatedWrites() {
        val services = services()
        val method = "Ltask65/ConstantIdentity;->linearCopies()Ljava/lang/String;"
        val before = returned(services, method, 2).raw
        optimize(services, method)
        assertFalse(has(view(services, method), Opcode.MOVE_OBJECT))
        assertEquals(before, returned(services, method, 2).raw)
        assertEquals(before, returned(reload(services), method, 2).raw)
    }

    @Test fun constructedStringIdentityIsNotReplacedByAnInternedLiteral() {
        val services = services()
        val method = "Ltask65/ConstantIdentity;->run()I"
        assertEquals(1, returned(services, method, 0).raw)
        optimize(services, method)
        assertTrue(has(view(services, method), Opcode.MOVE_RESULT_OBJECT))
        assertEquals(1, returned(services, method, 0).raw)
        assertEquals(1, returned(reload(services), method, 0).raw)
    }

    @Test fun freshHostReturnedStringIsNotAssumedCanonical() {
        val services = services()
        val method = "Ltask65/ConstantIdentity;->hostString()Ljava/lang/String;"
        val before = returned(services, method, 0).raw
        assertTrue(before is String)
        assertEquals(123456789, (before as String).toInt())
        optimize(services, method)
        assertTrue(has(view(services, method), Opcode.MOVE_RESULT_OBJECT))
        assertEquals(before, returned(services, method, 0).raw)
    }

    @Test fun moveFamiliesKeepUnencodableDestinationsAndFoldLocalCanonicalStrings() {
        val services = services()
        for ((name, register) in listOf(
            "allNarrowMoveEncodings()I" to 255,
            "allWideMoveEncodings()J" to 254,
            "allObjectMoveEncodings()Ljava/lang/String;" to 255,
        )) {
            val method = OWNER + name
            val before = returned(services, method, register).raw
            optimize(services, method)
            val edited = view(services, method)
            val moves = edited.instructionAddresses.map { edited.instructionAt(it) }
                .filter { (it.opcode as Enum<*>).name.startsWith("MOVE") }
            assertTrue(moves.any { (it as OneRegisterInstruction).registerA == 256 }, name)
            assertFalse(moves.any { (it as OneRegisterInstruction).registerA == register }, name)
            assertEquals(before, returned(services, method, register).raw)
        }
        val method = OWNER + "allMoveResultOpcodes()J"
        optimize(services, method)
        assertFalse(has(view(services, method), Opcode.MOVE_RESULT))
        assertFalse(has(view(services, method), Opcode.MOVE_RESULT_WIDE))
        // The object's callee-local literal has no identity proof in this method.
        assertTrue(has(view(services, method), Opcode.MOVE_RESULT_OBJECT))
        assertEquals(6L, returned(services, method, 2).raw)
    }

    @Test fun originalUnaryAndBinaryFamiliesFoldAndPreserveExecutedResults() {
        val services = services()
        for ((name, register) in listOf("allBinaryOpcodes()D" to 4, "allUnaryOpcodes()S" to 1)) {
            val method = OWNER + name
            val before = returned(services, method, register).raw
            val original = view(services, method)
            val arithmetic = original.instructionAddresses.map { original.opcodeAt(it) }
                .filterNot { (it as Enum<*>).name.startsWith("CONST") || (it as Enum<*>).name.startsWith("RETURN") }.toSet()
            assertTrue(arithmetic.isNotEmpty())
            optimize(services, method)
            val edited = view(services, method)
            assertFalse(edited.instructionAddresses.any { edited.opcodeAt(it) in arithmetic })
            val after = returned(services, method, register).raw
            if (name.endsWith("D")) assertEquals(doubleBits(before), doubleBits(after))
            else assertEquals((before as Number).toInt(), (after as Number).toInt())
        }
    }

    @Test fun allArrayAndStaticReadEncodingsPreserveTheirReturnedValues() {
        val services = services()
        for ((name, register, prefix) in listOf(
            Triple("allAgetOpcodes()J", 3, "AGET"),
            Triple("allSgetOpcodes()Ljava/lang/Class;", 0, "SGET"),
        )) {
            val method = OWNER + name
            val before = returned(services, method, register).raw
            optimize(services, method)
            val edited = view(services, method)
            assertFalse(edited.instructionAddresses.any { (edited.opcodeAt(it) as Enum<*>).name.startsWith(prefix) })
            assertEquals(before, returned(services, method, register).raw)
        }
    }

    @Test fun computedIntegerAndMovesCommitAndExecuteAsEquivalentLiterals() {
        val services = services()
        val method = OWNER + "computedInteger()I"
        assertEquals(5, returned(services, method, 1).raw)
        optimize(services, method)
        val edited = view(services, method)
        assertFalse(has(edited, Opcode.ADD_INT))
        assertFalse(has(edited, Opcode.MOVE))
        assertTrue(edited.instructionAddresses.any {
            (edited.instructionAt(it) as? NarrowLiteralInstruction)?.narrowLiteral == 5
        })
        assertEquals(5, returned(services, method, 1).raw)
    }

    @Test fun returnedStringWithoutLocalLiteralIdentityProofRemainsUnchangedAcrossReload() {
        val services = services()
        val method = OWNER + "readmeDecryptorShape()Ljava/lang/String;"
        val before = returned(services, method, 0).raw
        optimize(services, method)
        val edited = view(services, method)
        assertTrue(has(edited, Opcode.INVOKE_STATIC))
        assertTrue(has(edited, Opcode.MOVE_RESULT_OBJECT))
        assertEquals(before, returned(services, method, 0).raw)
        val reloaded = reload(services)
        assertEquals(before, returned(reloaded, method, 0).raw)
        assertTrue(has(view(reloaded, method), Opcode.MOVE_RESULT_OBJECT))
    }

    @Test fun floatingArithmeticPreservesExactBitsIncludingNegativeZeroAndNanPayloads() {
        val services = services()
        for (name in listOf("computedFloat", "computedNegativeZeroFloat", "computedNaNFloat")) {
            val method = OWNER + "$name()F"
            val before = floatBits(returned(services, method, 0).raw)
            optimize(services, method)
            assertFalse(has(view(services, method), Opcode.NEG_FLOAT), name)
            assertEquals(before, floatBits(returned(services, method, 0).raw), name)
            if (name == "computedNegativeZeroFloat") assertEquals(Int.MIN_VALUE, before)
        }
        for (name in listOf("computedDouble", "computedNegativeZeroDouble", "computedNaNDouble")) {
            val method = OWNER + "$name()D"
            val before = doubleBits(returned(services, method, 0).raw)
            optimize(services, method)
            assertFalse(has(view(services, method), Opcode.NEG_DOUBLE), name)
            assertEquals(before, doubleBits(returned(services, method, 0).raw), name)
            if (name == "computedNegativeZeroDouble") assertEquals(Long.MIN_VALUE, before)
        }
        val reloaded = reload(services)
        for (name in listOf("computedFloat", "computedNegativeZeroFloat", "computedNaNFloat")) {
            val method = OWNER + "$name()F"
            assertEquals(floatBits(returned(services, method, 0).raw), floatBits(returned(reloaded, method, 0).raw))
        }
        for (name in listOf("computedDouble", "computedNegativeZeroDouble", "computedNaNDouble")) {
            val method = OWNER + "$name()D"
            assertEquals(doubleBits(returned(services, method, 0).raw), doubleBits(returned(reloaded, method, 0).raw))
        }
    }

    @Test fun wideDestination255IsLegalButDestination256CannotUseConstEncoding() {
        val services = services()
        val wide = OWNER + "wideDestination255()J"
        val narrow = OWNER + "destinationAboveLiteralLimit()I"
        optimize(services, wide)
        optimize(services, narrow)
        val wideView = view(services, wide)
        assertFalse(has(wideView, Opcode.ADD_LONG))
        assertTrue(wideView.instructionAddresses.any {
            val instruction = wideView.instructionAt(it)
            instruction is WideLiteralInstruction && instruction.wideLiteral == 16L &&
                instruction is OneRegisterInstruction && instruction.registerA == 255
        })
        val narrowView = view(services, narrow)
        assertTrue(narrowView.instructionAddresses.any {
            val instruction = narrowView.instructionAt(it)
            instruction.opcode == Opcode.MOVE_16 && (instruction as OneRegisterInstruction).registerA == 256
        })
        val reloaded = reload(services)
        assertEquals(16L, returned(reloaded, wide, 255).raw)
        assertEquals(7, returned(reloaded, narrow, 1).raw)
    }

    @Test fun provenArrayStaticAndArithmeticValuesFoldWithoutDroppingTheirProducers() {
        val services = services()
        for ((name, opcode, register, expected) in listOf(
            Fold("knownAget()I", Opcode.AGET, 2, 7),
            Fold("knownSget()I", Opcode.SGET, 0, 42),
            Fold("knownDivision()I", Opcode.DIV_INT, 0, 3),
            Fold("knownRemainder()I", Opcode.REM_INT, 0, 1),
            Fold("computedLong()J", Opcode.ADD_LONG, 0, 16L),
        )) {
            val method = OWNER + name
            assertEquals(expected, returned(services, method, register).raw, name)
            optimize(services, method)
            assertFalse(has(view(services, method), opcode), name)
            assertEquals(expected, returned(services, method, register).raw, name)
        }
        assertTrue(has(view(services, OWNER + "knownAget()I"), Opcode.NEW_ARRAY))
        assertTrue(has(view(services, OWNER + "knownAget()I"), Opcode.APUT))
    }

    @Test fun classNullAndObjectArrayStringResultsKeepTheirValues() {
        val services = services()
        for ((name, register, opcode) in listOf(
            Triple("computedClass()Ljava/lang/Class;", 0, Opcode.MOVE_RESULT_OBJECT),
            Triple("computedNull()Ljava/lang/String;", 0, Opcode.MOVE_RESULT_OBJECT),
            Triple("knownAgetString()Ljava/lang/String;", 3, Opcode.AGET_OBJECT),
            Triple("knownAgetObjectString()Ljava/lang/Object;", 3, Opcode.AGET_OBJECT),
            Triple("knownAgetClass()Ljava/lang/Class;", 3, Opcode.AGET_OBJECT),
        )) {
            val method = OWNER + name
            val before = returned(services, method, register).raw
            optimize(services, method)
            // Array reads do not establish canonical string provenance, even with known contents.
            if (before is String) assertTrue(has(view(services, method), opcode), name)
            else assertFalse(has(view(services, method), opcode), name)
            val after = returned(services, method, register).raw
            if (before == null) assertTrue(after == null || after == 0) else assertEquals(before, after, name)
        }
    }

    @Test fun unknownDisagreeingUnreachedAndUnsupportedInstructionsRemain() {
        val services = services()
        for ((name, opcode) in listOf(
            "unknownMove(I)I" to Opcode.MOVE,
            "unknownBinary(I)I" to Opcode.ADD_INT,
            "unknownAgetArray([I)I" to Opcode.AGET,
            "unknownAgetIndex(I)I" to Opcode.AGET,
            "boxedInteger()Ljava/lang/Integer;" to Opcode.MOVE_RESULT_OBJECT,
            "disagreeingPaths(I)I" to Opcode.MOVE,
            "unreachedMove()I" to Opcode.MOVE,
            "knownCmp()I" to Opcode.CMP_LONG,
            "knownCheckCast()Ljava/lang/String;" to Opcode.CHECK_CAST,
            "knownArrayLength()I" to Opcode.ARRAY_LENGTH,
            "computedMutableArray()[I" to Opcode.MOVE_RESULT_OBJECT,
            "caughtMoveExceptionRemains()Ljava/lang/Throwable;" to Opcode.MOVE_EXCEPTION,
        )) {
            val method = OWNER + name
            optimize(services, method)
            assertTrue(has(view(services, method), opcode), name)
        }
    }

    @Test fun mixedNormalThrowAndVolatileReadAndInitializingClassLookupAreNotConstantized() {
        val services = services()
        for ((name, opcode) in listOf(
            "mixedNormalThrowDivision(I)I" to Opcode.DIV_INT,
            "knownVolatileRead()I" to Opcode.SGET,
            "classForNameMoveResultMustRemain()Ljava/lang/Class;" to Opcode.MOVE_RESULT_OBJECT,
        )) {
            val method = "LFx_ConstantPropagationGuards;->$name"
            optimize(services, method)
            assertTrue(has(view(services, method), opcode), name)
        }
        assertEquals(7, returned(services, "LFx_ConstantPropagationGuards;->knownVolatileRead()I", 0).raw)
        assertEquals(String::class.java, returned(services,
            "LFx_ConstantPropagationGuards;->classForNameMoveResultMustRemain()Ljava/lang/Class;", 1).raw)
    }

    @Test fun partialExecutionCannotPublishAnyEdits() {
        val services = services()
        val snapshot = services.currentSnapshot
        val result = Optimizer(services, Limits(1, 100, 0, 20), 5, listOf(ConstantPropagationStrategy()))
            .optimize(OWNER + "partialLoop()V")
        assertTrue(result.completionStatus is CompletionStatus.Partial)
        assertEquals(0, result.committedEditCount)
        assertSame(snapshot, services.currentSnapshot)
    }

    private data class Fold(val name: String, val opcode: Opcode, val register: Int, val expected: Any)
    private fun optimize(services: VmServices, method: String) =
        Optimizer(services, LIMITS, 10, listOf(ConstantPropagationStrategy())).optimize(method)
    private fun view(services: VmServices, method: String) = requireNotNull(services.classIndex.methodView(method))
    private fun returned(services: VmServices, method: String, register: Int): Value.Known {
        val graph = services.newSession(view(services, method), LIMITS).run()
        val trusted = requireNotNull(graph.trusted()) { "$method: ${graph.completion()}" }
        return trusted.terminatingConsensus(register) as Value.Known
    }
    private fun has(view: MethodView, opcode: Opcode) = view.instructionAddresses.any { view.opcodeAt(it) == opcode }
    private fun floatBits(raw: Any?) = if (raw is Float) raw.toRawBits() else (raw as Number).toInt()
    private fun doubleBits(raw: Any?) = if (raw is Double) raw.toRawBits() else (raw as Number).toLong()
    private fun services() = VmServices(ClassIndex.open(File("src/test/resources/task65"), 20))
    private fun reload(services: VmServices): VmServices {
        val dex = temporaryDirectory.resolve("constants.dex").toFile()
        ProgramDexWriter().write(services.currentSnapshot, dex)
        return VmServices(ClassIndex.open(dex, 20))
    }
    companion object {
        private const val OWNER = "Ltask65/Constants;->"
        private val LIMITS = Limits(500, 1_000_000, 0, 20)
    }
}
