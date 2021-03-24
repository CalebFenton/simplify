package org.cf.smalivm.opcode

import org.cf.smalivm.SideEffect
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.UninitializedInstance
import org.cf.smalivm.type.VirtualClass
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.cf.smalivm2.Value
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.reference.TypeReference

class NewInstanceOp internal constructor(
    location: MethodLocation, child: MethodLocation, private val destRegister: Int, private val virtualClass: VirtualClass,
) : Op(location, child) {

    override val registersReadCount = 0
    override val registersAssignedCount = 1
    override lateinit var sideEffectLevel: SideEffect.Level

    override fun execute(node: ExecutionNode): Array<out OpChild> {
        if (node.configuration.isSafe(virtualClass)) {
            node.sideEffectLevel = SideEffect.Level.NONE
        } else {
            node.sideEffectLevel = SideEffect.Level.STRONG
            // New-instance causes static initialization (but not new-array!)
            val clinit = virtualClass.getMethod("<clinit>()V")!!
            return arrayOf(OpChild.build(clinit))
        }

        return resume(node)
    }

    override fun resume(node: ExecutionNode): Array<out OpChild> {
        val instance: Any = UninitializedInstance(virtualClass)
        val instanceItem = Value.wrap(instance, virtualClass.name)
        node.state.assignRegister(destRegister, instanceItem)
        return collectChildren()
    }

    override fun toString() = "$name r$destRegister, $virtualClass"

    companion object : OpFactory {
        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instr = location.instruction as Instruction21c
            val destRegister = instr.registerA
            val typeRef = instr.reference as TypeReference
            val className = typeRef.type
            val virtualClass = classManager.getVirtualClass(className)
            return NewInstanceOp(location, child, destRegister, virtualClass)
        }
    }
}
