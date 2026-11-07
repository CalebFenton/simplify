package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Format
import com.android.tools.smali.dexlib2.Opcode

/**
 * Opcode families shared by the op builder and the method-edit pipeline. A family that one dexlib2
 * instruction format names is derived from that format, so it cannot drift from dexlib2's opcode table.
 */
internal object OpcodeGroups {
    val GOTO: Set<Opcode> = withFormat(Format.Format10t, Format.Format20t, Format.Format30t)
    val CONDITIONAL_BRANCH: Set<Opcode> = withFormat(Format.Format21t, Format.Format22t)
    val BRANCH: Set<Opcode> = GOTO + CONDITIONAL_BRANCH

    /** `fill-array-data` and the two switches: the instructions whose target is payload data. */
    val PAYLOAD_OWNER: Set<Opcode> = withFormat(Format.Format31t)
    val SWITCH: Set<Opcode> = PAYLOAD_OWNER - Opcode.FILL_ARRAY_DATA

    /** Payload pseudo-instructions are data, never executed and never a control target. */
    val PAYLOAD: Set<Opcode> = withFormat(Format.ArrayPayload, Format.PackedSwitchPayload, Format.SparseSwitchPayload)

    val RETURN: Set<Opcode> = setOf(Opcode.RETURN_VOID, Opcode.RETURN, Opcode.RETURN_WIDE, Opcode.RETURN_OBJECT)

    /** Register-to-register copies; `move-result*` and `move-exception` take no source register. */
    val MOVE: Set<Opcode> = setOf(
        Opcode.MOVE, Opcode.MOVE_FROM16, Opcode.MOVE_16,
        Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16,
        Opcode.MOVE_OBJECT, Opcode.MOVE_OBJECT_FROM16, Opcode.MOVE_OBJECT_16,
    )

    private fun withFormat(vararg formats: Format): Set<Opcode> =
        Opcode.values().filterTo(LinkedHashSet()) { it.format in formats }
}
