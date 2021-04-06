package org.cf.smalivm.opcode

import org.cf.smalivm.SideEffect
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.UninitializedInstance
import org.cf.smalivm.type.VirtualClass
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.Value
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.reference.TypeReference

class NewInstanceOp internal constructor(
    location: MethodLocation,
    private val destRegister: Int,
    private val virtualClass: VirtualClass,
    val sideEffectLevel: SideEffect.Level
) :
    Op(location) {

    override val registersReadCount = 0
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        return if (!node.state.isClassInitialized(virtualClass)) {
            // New-instance causes static initialization (but not new-array!)
            val clinit = virtualClass.getMethod("<clinit>()V")!!
            callMethod(clinit)
        } else {
            resume(node)
        }
    }

    override fun resume(node: ExecutionNode): Array<out UnresolvedChild> {
        val rawInstance = UninitializedInstance(virtualClass)
        val instance = Value.wrap(rawInstance, virtualClass.name)
        node.state.assignRegister(destRegister, instance)
        return finish(sideEffectLevel = sideEffectLevel)
    }

    override fun toString() = "$name r$destRegister, $virtualClass"

    companion object : OpFactory {
        override fun build(
            location: MethodLocation,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val instr = location.instruction as Instruction21c
            val destRegister = instr.registerA
            val typeRef = instr.reference as TypeReference
            val className = typeRef.type
            val virtualClass = classManager.getVirtualClass(className)
            val sideEffectLevel = when {
                configuration.isSafe(virtualClass) -> SideEffect.Level.NONE
                else -> SideEffect.Level.STRONG
            }
            return NewInstanceOp(location, destRegister, virtualClass, sideEffectLevel)
        }
    }
}
