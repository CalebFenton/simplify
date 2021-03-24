package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction11x
import org.slf4j.LoggerFactory

class ThrowOp internal constructor(location: MethodLocation, private val register: Int) : Op(location) {

    override val registersReadCount = 1
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out OpChild> {
        val item = node.state.readRegister(register)
        node.state.assignThrowRegister(item)
        return collectChildren()
    }

    override fun toString() = "$name r$register"

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(ThrowOp::class.java.simpleName)
        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val instr = location.instruction as Instruction11x
            val register = instr.registerA
            return ThrowOp(location, register)
        }
    }
}
