package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Bug class: the old engine had no static view of a method -- address->op lookup was split across
 * a VM-global template map and a graph-local address->location map, so "the shape of this method"
 * could only be obtained by executing it (spec §5.4). Oracle: the smali source of
 * Lspine/spine; plus Dalvik encoding rules (instruction widths in 16-bit code units,
 * parameters in the highest registers, wide types taking two registers).
 */
class MethodViewTest {
    private val classIndex = ClassIndex.open(File("src/test/resources/smali"))

    private fun view(descriptor: String) =
        classIndex.requireMethodView("Lspine/spine;", descriptor)

    @Test
    fun addressesAreSortedAndCoverEveryInstruction() {
        val v = view("returnFive()I")
        // const/4 is 1 code unit at 0; return is 1 code unit at 1.
        assertArrayEquals(intArrayOf(0, 1), v.addresses)
        assertTrue(v.hasAddress(0))
        assertFalse(v.hasAddress(99))
    }

    @Test
    fun opcodeAndNextAddressFollowInstructionWidth() {
        val v = view("nopThenConst()I")
        // nop(1) nop(1) const/16(2) return(1)
        assertArrayEquals(intArrayOf(0, 1, 2, 4), v.addresses)
        assertEquals(Opcode.NOP, v.opcodeAt(0))
        assertEquals(Opcode.CONST_16, v.opcodeAt(2))
        assertEquals(1, v.nextAddress(0))
        assertEquals(4, v.nextAddress(2))
    }

    @Test
    fun executableInstructionIndexesFollowVariableWidthCodeAddresses() {
        val v = view("nopThenConst()I")

        assertEquals(4, v.instructionCount)
        assertEquals(listOf(0, 1, 2, 4), (0 until v.instructionCount).map(v::codeAddressAtInstructionIndex))
        assertEquals(0, v.instructionIndexAtCodeAddress(0))
        assertEquals(1, v.instructionIndexAtCodeAddress(1))
        assertEquals(2, v.instructionIndexAtCodeAddress(2))
        assertEquals(3, v.instructionIndexAtCodeAddress(4))
        assertNull(v.instructionIndexAtCodeAddress(3))
        assertNull(v.instructionIndexAtCodeAddress(99))
        assertThrows(IllegalArgumentException::class.java) { v.codeAddressAtInstructionIndex(-1) }
        assertThrows(IllegalArgumentException::class.java) { v.codeAddressAtInstructionIndex(4) }
    }

    @Test
    fun payloadPseudoInstructionsAreExcludedFromExecutableInstructionIndexes() {
        val v = CoreTester.classIndex.requireMethodView("Lfill_array_data_test;", "fillArrayDataInt()V")

        assertEquals(2, v.instructionCount)
        assertEquals(0, v.codeAddressAtInstructionIndex(0))
        assertEquals(3, v.codeAddressAtInstructionIndex(1))
        assertNull(v.instructionIndexAtCodeAddress(4))
    }

    @Test
    fun instructionPayloadAndTerminalAddressArraysAreDefensiveCopies() {
        val v = CoreTester.classIndex.requireMethodView("Lfill_array_data_test;", "fillArrayDataInt()V")
        val instructionAddresses = v.instructionAddresses
        val payloadAddresses = v.payloadAddresses
        val terminalAddresses = v.terminalInstructionAddresses

        instructionAddresses[0] = 999
        payloadAddresses[0] = 999
        terminalAddresses[0] = 999

        assertArrayEquals(intArrayOf(0, 3), v.instructionAddresses)
        assertArrayEquals(intArrayOf(4), v.payloadAddresses)
        assertArrayEquals(intArrayOf(3), v.terminalInstructionAddresses)
    }

    @Test
    fun instructionAndPayloadLookupRejectTheOtherAddressKind() {
        val v = CoreTester.classIndex.requireMethodView("Lfill_array_data_test;", "fillArrayDataInt()V")
        val payloadAddress = v.payloadAddresses.single()

        assertEquals(Opcode.ARRAY_PAYLOAD, v.payloadAt(payloadAddress).opcode)
        assertThrows(IllegalArgumentException::class.java) { v.instructionAt(payloadAddress) }
        assertThrows(IllegalArgumentException::class.java) { v.payloadAt(0) }
    }

    @Test
    fun switchPayloadsAreExcludedWithoutShiftingExecutableInstructionIndexes() {
        for (descriptor in listOf("packedSwitch()V", "sparseSwitch()V")) {
            val v = CoreTester.classIndex.requireMethodView("Lswitch_test;", descriptor)

            assertArrayEquals(intArrayOf(8), v.payloadAddresses, descriptor)
            assertNull(v.instructionIndexAtCodeAddress(8), descriptor)
            assertEquals(6, v.instructionCount, descriptor)
            assertEquals(
                listOf(0, 1, 4, 5, 6, 7),
                (0 until v.instructionCount).map(v::codeAddressAtInstructionIndex),
                descriptor,
            )
            assertEquals(5, v.instructionIndexAtCodeAddress(7), descriptor)
            assertEquals(7, v.codeAddressAtInstructionIndex(5), descriptor)
        }
    }

    @Test
    fun executableInstructionIndexesDoNotUseThePublicMutableAddresses() {
        val v = view("nopThenConst()I")

        v.addresses[3] = 99

        assertEquals(4, v.instructionCount)
        assertEquals(4, v.codeAddressAtInstructionIndex(3))
        assertEquals(3, v.instructionIndexAtCodeAddress(4))
    }

    @Test
    fun terminalInstructionAddressesAreReturnsAndThrows() {
        // Bug class: omitting a throw, or a non-void/non-object return, from "terminating"
        // would make a path-exploring consumer treat a dead end as continuable and read past
        // the method's real exit. Oracle: Dalvik's defined terminator opcodes (RETURN_VOID,
        // RETURN, RETURN_WIDE, RETURN_OBJECT, THROW) applied to returnFive/throwUncaught's smali.
        val v = view("returnFive()I")
        assertArrayEquals(intArrayOf(1), v.terminalInstructionAddresses)
        val thrower = view("throwUncaught(Ljava/lang/RuntimeException;)V")
        assertArrayEquals(intArrayOf(0), thrower.terminalInstructionAddresses)
    }

    @Test
    fun staticNoArgMethodHasNoParameters() {
        val v = view("returnFive()I")
        assertTrue(v.isStatic)
        assertEquals(emptyList<ParameterSlot>(), v.parameters)
        assertEquals(1, v.registerCount)
        assertEquals(1, v.firstParameterRegister) // == registerCount when there are no parameters
        assertEquals(VirtualType.of("I"), v.returnType)
        assertEquals(VirtualType.of("Lspine/spine;"), v.definingClass)
    }

    @Test
    fun parametersOccupyHighestRegisters() {
        val v = view("throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;")
        // .registers 2, one reference parameter -> p0 is v1.
        assertEquals(2, v.registerCount)
        assertEquals(1, v.firstParameterRegister)
        assertEquals(listOf(ParameterSlot(1, VirtualType.of("Ljava/lang/RuntimeException;"))), v.parameters)
        assertTrue(MethodViewJavaMutationProbe.rejectsParameterMutation(v.parameters))
    }

    @Test
    fun instanceMethodWithNonTrailingWideParameterPinsRegisterLayout() {
        // Bug class: silent parameter/register misalignment -- the plan's single most-warned-
        // about trap, and previously unexercised (every other spine method is static, and no
        // other method has a wide parameter). Pins two invariants at once: (1) an instance
        // method's `this` occupies the lowest parameter register, not just any register named
        // "p0"; (2) a wide (J) parameter that is NOT last still consumes two registers, so every
        // parameter after it shifts by 2, not 1 -- undercounting this silently misaligns every
        // parameter read downstream (Task 3's Frame).
        // Oracle: Dalvik semantics -- parameters occupy the highest registers, and a wide type
        // occupies two of them. instanceWideParams(JLjava/lang/String;I)I has parameter widths
        // this=1, J=2, String=1, I=1, total width=5. With `.registers 8`, firstParameterRegister
        // = 8 - 5 = 3, so slots are: this=v3, J=v4 (pair v4/v5), String=v6, I=v7.
        val v = view("instanceWideParams(JLjava/lang/String;I)I")
        assertFalse(v.isStatic)
        assertEquals(8, v.registerCount)
        assertEquals(3, v.firstParameterRegister)
        assertEquals(
            listOf(
                ParameterSlot(3, VirtualType.of("Lspine/spine;")),
                ParameterSlot(4, VirtualType.of("J")),
                ParameterSlot(6, VirtualType.of("Ljava/lang/String;")),
                ParameterSlot(7, VirtualType.of("I")),
            ),
            v.parameters,
        )
    }

    @Test
    fun tryBlocksProjectToCoreModel() {
        val v = view("throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;")
        assertEquals(1, v.tryBlocks.size)
        val block = v.tryBlocks.single()
        assertTrue(block.covers(0), "the throw at address 0 must be covered")
        assertEquals(
            listOf(CatchClause(VirtualType.of("Ljava/lang/RuntimeException;"), 1)),
            block.catches,
        )
        assertNull(block.catchAllAddress)
    }

    @Test
    fun aPayloadPseudoInstructionIsReportedAsDataRatherThanAsAnOrdinaryAddress() {
        // Plan 13's editor must not relocate a payload as if it were an instruction, and Plan 14's
        // dead-code strategy must not read an unreached payload as dead code. Both need this set, and
        // `addresses()` alone cannot distinguish them.
        val view = CoreTester.classIndex.requireMethodView("Lfill_array_data_test;", "fillArrayDataInt()V")
        val payload = view.payloadAddresses.single()
        assertTrue(view.isPayload(payload))
        assertTrue(view.addresses.contains(payload), "still an address; just not code")
        assertFalse(view.isPayload(0), "the fill-array-data instruction itself is code")
    }

    @Test
    fun methodWithoutTryBlocksHasNone() {
        // Bug class: a placeholder/sentinel block (e.g. an all-covering default) instead of a
        // genuinely empty list would make handler resolution (spec §7) believe a throw is
        // covered when the method declares no .catch at all. Oracle: returnFive()I's smali
        // source has no try/catch directive, so tryBlocks must be exactly empty, not "unknown".
        assertEquals(emptyList<TryBlock>(), view("returnFive()I").tryBlocks)
    }
}
