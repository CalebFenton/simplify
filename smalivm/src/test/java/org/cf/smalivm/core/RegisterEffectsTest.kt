package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Format
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RegisterEffectsTest {
    @Test
    fun everyFixedRegisterOpcodeAgreesWithDexlibFormatAndWriteFlags() {
        for (opcode in Opcode.values()) {
            val registerFields = FIXED_REGISTER_FIELDS[opcode.format] ?: continue
            val operands = RegisterEffects.operands(bare(opcode))
            val destination = operands.firstOrNull()

            assertEquals(registerFields, operands.size, "$opcode register fields")
            assertEquals(opcode.setsRegister(), destination?.writes == true, "$opcode writes vA")
            assertEquals(
                opcode.setsWideRegister(),
                destination?.writes == true && destination.category == ValueCategory.WIDE,
                "$opcode writes a wide vA",
            )
        }
    }

    private fun bare(opcode: Opcode): Instruction = object : Instruction {
        override fun getOpcode(): Opcode = opcode
        override fun getCodeUnits(): Int = opcode.format.size / 2
    }

    private companion object {
        val FIXED_REGISTER_FIELDS = mapOf(
            Format.Format10x to 0, Format.Format10t to 0, Format.Format20t to 0, Format.Format30t to 0,
            Format.Format20bc to 0, Format.ArrayPayload to 0, Format.PackedSwitchPayload to 0,
            Format.SparseSwitchPayload to 0,
            Format.Format11n to 1, Format.Format11x to 1, Format.Format21c to 1, Format.Format21ih to 1,
            Format.Format21lh to 1, Format.Format21s to 1, Format.Format21t to 1, Format.Format31c to 1,
            Format.Format31i to 1, Format.Format31t to 1, Format.Format51l to 1,
            Format.Format12x to 2, Format.Format22b to 2, Format.Format22c to 2, Format.Format22cs to 2,
            Format.Format22s to 2, Format.Format22t to 2, Format.Format22x to 2, Format.Format32x to 2,
            Format.Format23x to 3,
        )
    }
}
