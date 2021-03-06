package org.cf.smalivm.opcode

import gnu.trove.list.TIntList
import gnu.trove.list.linked.TIntLinkedList
import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.Instruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc
import org.jf.dexlib2.iface.reference.MethodReference
import org.jf.dexlib2.util.ReferenceUtil

class InvokeOpFactory : OpFactory {
    private fun buildParameterRegisters(parameterTypes: List<String>, registers: IntArray): IntArray {
        val parameterRegisters: TIntList = TIntLinkedList(parameterTypes.size)
        var index = 0
        for (parameterType in parameterTypes) {
            parameterRegisters.add(registers[index])
            index += Utils.getRegisterSize(parameterType)
        }
        return parameterRegisters.toArray()
    }

    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, vm: VirtualMachine): Op {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instruction = location.instruction as ReferenceInstruction
        val methodReference = instruction.reference as MethodReference
        val registers = buildRegisters(location.instruction)
        val className = methodReference.definingClass
        val type = vm.classManager.getVirtualType(className)
        val methodSignature = ReferenceUtil.getMethodDescriptor(methodReference)
        val methodDescriptor = methodSignature.split("->").toTypedArray()[1]
        val method = type.getMethod(methodDescriptor) ?: throw RuntimeException("Method doesn't exist: $methodSignature")
        val parameterRegisters = buildParameterRegisters(method.parameterTypeNames, registers)
        return InvokeOp(location, child, method, parameterRegisters, vm)
    }

    companion object {
        private fun buildRegisters(instr: Instruction?): IntArray {
            return if (instr is Instruction3rc) {
                buildRegisters3rc(instr)
            } else {
                buildRegisters35c(instr as Instruction35c?)
            }
        }

        private fun buildRegisters35c(instruction: Instruction35c?): IntArray {
            val registerCount = instruction!!.registerCount
            val registers = IntArray(registerCount)
            when (registerCount) {
                5 -> {
                    registers[4] = instruction.registerG
                    registers[3] = instruction.registerF
                    registers[2] = instruction.registerE
                    registers[1] = instruction.registerD
                    registers[0] = instruction.registerC
                }
                4 -> {
                    registers[3] = instruction.registerF
                    registers[2] = instruction.registerE
                    registers[1] = instruction.registerD
                    registers[0] = instruction.registerC
                }
                3 -> {
                    registers[2] = instruction.registerE
                    registers[1] = instruction.registerD
                    registers[0] = instruction.registerC
                }
                2 -> {
                    registers[1] = instruction.registerD
                    registers[0] = instruction.registerC
                }
                1 -> registers[0] = instruction.registerC
            }
            return registers
        }

        private fun buildRegisters3rc(instruction: Instruction3rc): IntArray {
            val registerCount = instruction.registerCount
            val start = instruction.startRegister
            val end = start + registerCount
            val registers = IntArray(registerCount)
            for (i in start until end) {
                registers[i - start] = i
            }
            return registers
        }
    }
}
