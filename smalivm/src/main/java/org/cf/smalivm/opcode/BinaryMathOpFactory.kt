package org.cf.smalivm.opcode

import gnu.trove.map.TIntObjectMap
import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction12x
import org.jf.dexlib2.iface.instruction.formats.Instruction23x

class BinaryMathOpFactory : OpFactory {
    fun create(
        location: MethodLocation, addressToLocation: TIntObjectMap<MethodLocation?>?,
        vm: VirtualMachine
    ): BinaryMathOp? {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instruction = location.instruction as BuilderInstruction?
        val instr = location.instruction as TwoRegisterInstruction?
        val destRegister = instr!!.registerA
        var arg1Register = instr.registerB
        val exceptionFactory = vm.exceptionFactory
        return if (instruction is Instruction23x) {
            // add-int vAA, vBB, vCC
            val arg2Register = (instruction as Instruction23x).registerC
            BinaryMathOp(location, child, destRegister, arg1Register, arg2Register, false, exceptionFactory)
        } else if (instruction is Instruction12x) {
            // add-int/2addr vAA, vBB
            arg1Register = instr.registerA
            val arg2Register = (instruction as Instruction12x).registerB
            BinaryMathOp(location, child, destRegister, arg1Register, arg2Register, false, exceptionFactory)
        } else if (instruction is NarrowLiteralInstruction) {
            // Instruction22b - add-int/lit8 vAA, vBB, #CC
            // Instruction22s - add-int/lit16 vAA, vBB, #CCCC
            val arg2Literal = (instruction as NarrowLiteralInstruction).narrowLiteral
            BinaryMathOp(location, child, destRegister, arg1Register, arg2Literal, true, exceptionFactory)
        } else {
            null
        }
    }
}
