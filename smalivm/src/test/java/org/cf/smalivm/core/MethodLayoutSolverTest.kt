package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.ImmutableExceptionHandler
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.ImmutableTryBlock
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableArrayPayload
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21s
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutablePackedSwitchPayload
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableStringReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MethodLayoutSolverTest {
    @Test
    fun layoutAddsPaddingBeforeAnUnalignedPayload() {
        val body = MethodBody.import(
            ImmutableMethodImplementation(
                0,
                listOf(
                    ImmutableInstruction10x(Opcode.NOP),
                    ImmutablePackedSwitchPayload(emptyList()),
                ),
                emptyList(),
                emptyList(),
            ),
        )

        val assembled = MethodBodyAssembler.assemble(MethodLayoutSolver.solve(body))

        assertEquals(2, assembled.instructions.single { it.instruction.opcode == Opcode.PACKED_SWITCH_PAYLOAD }.codeAddress)
    }

    @Test
    fun layoutRemovesObsoleteSyntheticPaddingBeforeAPayload() {
        val body = MethodBody.import(
            ImmutableMethodImplementation(
                1,
                listOf(
                    constString(),
                    ImmutableInstruction10x(Opcode.RETURN_VOID),
                    ImmutableInstruction10x(Opcode.NOP),
                    ImmutableArrayPayload(4, listOf(1)),
                ),
                emptyList(),
                emptyList(),
            ),
        )

        val assembled = MethodBodyAssembler.assemble(
            MethodLayoutSolver.solve(body, StringInstructionEncoding.CONST_STRING_JUMBO),
        )

        assertEquals(4, assembled.instructions.single { it.instruction.opcode == Opcode.ARRAY_PAYLOAD }.codeAddress)
        assertTrue(assembled.instructions.none { it.instruction.opcode == Opcode.NOP })
    }

    @Test
    fun jumboRelayoutWidensGotoAfterStringInstructionsGrow() {
        val instructions = buildList {
            add(ImmutableInstruction10t(Opcode.GOTO, 121))
            repeat(60) { add(constString()) }
            add(ImmutableInstruction10x(Opcode.RETURN_VOID))
        }

        val body = MethodBodyAssembler.assemble(
            MethodLayoutSolver.solve(methodBody(instructions), StringInstructionEncoding.CONST_STRING_JUMBO),
        )

        assertEquals(Opcode.GOTO_16, body.instructions.first().instruction.opcode)
    }

    @Test
    fun jumboRelayoutExpandsAnOutOfRangeConditional() {
        val stringCount = 10_922
        val instructions = buildList {
            add(ImmutableInstruction21t(Opcode.IF_EQZ, 0, 2 + (2 * stringCount)))
            repeat(stringCount) { add(constString()) }
            add(ImmutableInstruction10x(Opcode.RETURN_VOID))
        }

        val body = MethodBodyAssembler.assemble(
            MethodLayoutSolver.solve(methodBody(instructions), StringInstructionEncoding.CONST_STRING_JUMBO),
        )

        assertEquals(Opcode.IF_NEZ, body.instructions[0].instruction.opcode)
        assertEquals(Opcode.GOTO_32, body.instructions[1].instruction.opcode)
    }

    @Test
    fun jumboGrowthSplitsAProtectedRangeAtTheDexLimit() {
        val instructions = buildList {
            add(ImmutableInstruction21c(Opcode.CONST_STRING, 0, ImmutableStringReference("grow")))
            repeat(65_532) { add(ImmutableInstruction10x(Opcode.NOP)) }
            add(ImmutableInstruction10x(Opcode.RETURN_VOID))
        }
        val implementation = ImmutableMethodImplementation(
            1,
            instructions,
            listOf(
                ImmutableTryBlock(
                    0,
                    65_535,
                    listOf(ImmutableExceptionHandler("Ljava/lang/Exception;", 65_534)),
                ),
            ),
            emptyList(),
        )

        val body = MethodBodyAssembler.assemble(
            MethodLayoutSolver.solve(MethodBody.import(implementation), StringInstructionEncoding.CONST_STRING_JUMBO),
        )

        assertEquals(listOf(65_535, 1), body.implementation.tryBlocks.map { it.codeUnitCount })
        assertEquals(65_535, body.implementation.tryBlocks.last().startCodeAddress)
    }

    @Test
    fun jumboGrowthSplitsProtectedRangesOnlyAtInstructionBoundaries() {
        val instructions = buildList {
            add(ImmutableInstruction21c(Opcode.CONST_STRING, 0, ImmutableStringReference("grow")))
            repeat(65_531) { add(ImmutableInstruction10x(Opcode.NOP)) }
            add(ImmutableInstruction21s(Opcode.CONST_16, 0, 1))
            add(ImmutableInstruction10x(Opcode.RETURN_VOID))
        }
        val implementation = ImmutableMethodImplementation(
            1,
            instructions,
            listOf(
                ImmutableTryBlock(
                    0,
                    65_535,
                    listOf(ImmutableExceptionHandler("Ljava/lang/Exception;", 65_535)),
                ),
            ),
            emptyList(),
        )

        val body = MethodBodyAssembler.assemble(
            MethodLayoutSolver.solve(MethodBody.import(implementation), StringInstructionEncoding.CONST_STRING_JUMBO),
        )

        assertEquals(listOf(65_534, 2), body.implementation.tryBlocks.map { it.codeUnitCount })
        assertEquals(65_534, body.implementation.tryBlocks.last().startCodeAddress)
    }

    private fun constString() = ImmutableInstruction21c(
        Opcode.CONST_STRING,
        0,
        ImmutableStringReference("value"),
    )

    private fun methodBody(instructions: List<com.android.tools.smali.dexlib2.iface.instruction.Instruction>): MethodBody =
        MethodBody.import(ImmutableMethodImplementation(1, instructions, emptyList(), emptyList()))
}
