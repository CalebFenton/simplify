package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction20bc
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction22c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction31c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction35c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction3rc
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction45cc
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction4rcc
import com.android.tools.smali.dexlib2.iface.instruction.DualReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.PayloadInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ThreeRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction10t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction10x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction11n
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction11x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction12x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction20bc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction20t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21ih
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21lh
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21s
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22b
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22cs
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22s
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction23x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction30t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31i
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction32x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35mi
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35ms
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction3rc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction3rmi
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction3rms
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction45cc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction4rcc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction51l
import com.android.tools.smali.dexlib2.iface.reference.Reference
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction12x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction20t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21ih
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21lh
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21s
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22b
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22cs
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22s
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction23x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction30t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31i
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction32x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction35mi
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction35ms
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction3rmi
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction3rms
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction51l

/** Register operands in encoded order: A, B, C for fixed formats, each argument word for variable formats. */
internal fun Instruction.registerOperands(): List<Int> = when (this) {
    is FiveRegisterInstruction ->
        listOf(registerC, registerD, registerE, registerF, registerG).subList(0, registerCount)
    is RegisterRangeInstruction -> List(registerCount) { startRegister + it }
    is ThreeRegisterInstruction -> listOf(registerA, registerB, registerC)
    is TwoRegisterInstruction -> listOf(registerA, registerB)
    is OneRegisterInstruction -> listOf(registerA)
    else -> emptyList()
}

internal fun Instruction.referenceOperands(): List<Reference> = when (this) {
    is DualReferenceInstruction -> listOf(reference, reference2)
    is ReferenceInstruction -> listOf(reference)
    else -> emptyList()
}

/**
 * This instruction with [registers] in [registerOperands] order. A register the format cannot encode throws
 * [IllegalArgumentException]. A range instruction takes its start from the first register and keeps its count.
 */
internal fun Instruction.withRegisters(registers: List<Int>): Instruction =
    rebuild(this, registers, referenceOperands())

/**
 * This instruction with [references] in [referenceOperands] order. References are stored as given, so
 * references interned by a `DexBuilder` survive; `ImmutableInstruction.of` detaches the result when needed.
 */
internal fun Instruction.withReferences(references: List<Reference>): Instruction =
    rebuild(this, registerOperands(), references)

/** The `/range` form [rangeOpcode] of this five-register instruction over contiguous [registers]. */
internal fun Instruction.toRangeForm(rangeOpcode: Opcode, registers: List<Int>): Instruction {
    val start = registers.firstOrNull() ?: 0
    return when (this) {
        is Instruction35c -> BuilderInstruction3rc(rangeOpcode, start, registers.size, reference)
        is Instruction35mi -> ImmutableInstruction3rmi(rangeOpcode, start, registers.size, inlineIndex)
        is Instruction35ms -> ImmutableInstruction3rms(rangeOpcode, start, registers.size, vtableIndex)
        is Instruction45cc -> BuilderInstruction4rcc(rangeOpcode, start, registers.size, reference, reference2)
        else -> error("$opcode has no range form")
    }
}

@Suppress("ComplexMethod", "LongMethod")
private fun rebuild(source: Instruction, registers: List<Int>, references: List<Reference>): Instruction {
    val opcode = source.opcode
    fun register(index: Int): Int = registers.getOrElse(index) { 0 }
    return when (source) {
        is Instruction10x -> ImmutableInstruction10x(opcode)
        is Instruction10t -> ImmutableInstruction10t(opcode, source.codeOffset)
        is Instruction11n -> ImmutableInstruction11n(opcode, registers[0], source.narrowLiteral)
        is Instruction11x -> ImmutableInstruction11x(opcode, registers[0])
        is Instruction12x -> ImmutableInstruction12x(opcode, registers[0], registers[1])
        is Instruction20bc -> BuilderInstruction20bc(opcode, source.verificationError, references[0])
        is Instruction20t -> ImmutableInstruction20t(opcode, source.codeOffset)
        is Instruction21c -> BuilderInstruction21c(opcode, registers[0], references[0])
        is Instruction21ih -> ImmutableInstruction21ih(opcode, registers[0], source.narrowLiteral)
        is Instruction21lh -> ImmutableInstruction21lh(opcode, registers[0], source.wideLiteral)
        is Instruction21s -> ImmutableInstruction21s(opcode, registers[0], source.narrowLiteral)
        is Instruction21t -> ImmutableInstruction21t(opcode, registers[0], source.codeOffset)
        is Instruction22b -> ImmutableInstruction22b(opcode, registers[0], registers[1], source.narrowLiteral)
        is Instruction22c -> BuilderInstruction22c(opcode, registers[0], registers[1], references[0])
        is Instruction22cs -> ImmutableInstruction22cs(opcode, registers[0], registers[1], source.fieldOffset)
        is Instruction22s -> ImmutableInstruction22s(opcode, registers[0], registers[1], source.narrowLiteral)
        is Instruction22t -> ImmutableInstruction22t(opcode, registers[0], registers[1], source.codeOffset)
        is Instruction22x -> ImmutableInstruction22x(opcode, registers[0], registers[1])
        is Instruction23x -> ImmutableInstruction23x(opcode, registers[0], registers[1], registers[2])
        is Instruction30t -> ImmutableInstruction30t(opcode, source.codeOffset)
        is Instruction31c -> BuilderInstruction31c(opcode, registers[0], references[0])
        is Instruction31i -> ImmutableInstruction31i(opcode, registers[0], source.narrowLiteral)
        is Instruction31t -> ImmutableInstruction31t(opcode, registers[0], source.codeOffset)
        is Instruction32x -> ImmutableInstruction32x(opcode, registers[0], registers[1])
        is Instruction35c -> BuilderInstruction35c(
            opcode, registers.size, register(0), register(1), register(2), register(3), register(4), references[0],
        )
        is Instruction35mi -> ImmutableInstruction35mi(
            opcode, registers.size, register(0), register(1), register(2), register(3), register(4), source.inlineIndex,
        )
        is Instruction35ms -> ImmutableInstruction35ms(
            opcode, registers.size, register(0), register(1), register(2), register(3), register(4), source.vtableIndex,
        )
        is Instruction3rc -> BuilderInstruction3rc(
            opcode,
            registers.firstOrNull() ?: source.startRegister,
            source.registerCount,
            references[0],
        )
        is Instruction3rmi ->
            ImmutableInstruction3rmi(opcode, registers.firstOrNull() ?: source.startRegister, source.registerCount, source.inlineIndex)
        is Instruction3rms ->
            ImmutableInstruction3rms(opcode, registers.firstOrNull() ?: source.startRegister, source.registerCount, source.vtableIndex)
        is Instruction45cc -> BuilderInstruction45cc(
            opcode,
            registers.size,
            register(0),
            register(1),
            register(2),
            register(3),
            register(4),
            references[0],
            references[1],
        )
        is Instruction4rcc -> BuilderInstruction4rcc(
            opcode,
            registers.firstOrNull() ?: source.startRegister,
            source.registerCount,
            references[0],
            references[1],
        )
        is Instruction51l -> ImmutableInstruction51l(opcode, registers[0], source.wideLiteral)
        is PayloadInstruction -> ImmutableInstruction.of(source)
        else -> error("Unknown instruction format ${opcode.format}")
    }
}
