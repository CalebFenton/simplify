package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.cf.smalivm.opcode.ConstOp.ConstantType
import org.cf.util.Utils
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction
import org.jf.dexlib2.iface.reference.StringReference
import org.jf.dexlib2.util.ReferenceUtil

class ConstOpFactory : OpFactory {
    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, vm: VirtualMachine): ConstOp {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instruction = location.instruction as BuilderInstruction
        val destRegister = (instruction as OneRegisterInstruction).registerA
        val constantType: ConstantType
        val literal: Any
        val opName = instruction.getOpcode().name
        if (opName.matches("const-string(?:/jumbo)?".toRegex())) {
            val instr = location.instruction as ReferenceInstruction
            literal = (instr.reference as StringReference).string
            constantType = ConstantType.STRING
        } else if (opName.endsWith("-class")) {
            // Don't ensure that the class exists here since we don't know what classes will be available.
            // Defer to actual execution to handle any possible exceptions.
            val instr = location.instruction as ReferenceInstruction
            val classRef = instr.reference
            literal = ReferenceUtil.getReferenceString(classRef)!!
            constantType = ConstantType.CLASS
        } else if (opName.contains("-wide")) {
            val instr = location.instruction as WideLiteralInstruction
            literal = instr.wideLiteral
            constantType = ConstantType.WIDE
        } else {
            val instr = location.instruction as NarrowLiteralInstruction
            literal = instr.narrowLiteral
            constantType = ConstantType.NARROW
        }
        return ConstOp(
            location, child, destRegister, constantType, literal, vm.classLoader,
            vm.exceptionFactory
        )
    }
}
