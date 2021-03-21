package org.cf.smalivm.opcode

import ExceptionFactory
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualType
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.cf.smalivm2.Value
import org.cf.util.ClassNameUtils
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.reference.TypeReference
import org.slf4j.LoggerFactory

class CheckCastOp internal constructor(
    location: MethodLocation,
    child: MethodLocation,
    private val targetRegister: Int,
    private val castType: VirtualType,
) : Op(location, child) {

    override val registersReadCount = 1
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out OpChild> {
        val target = node.state.readRegister(targetRegister)
        if (isInstance(target, castType, node.classManager)) {
            node.state.assignRegister(targetRegister, target.value, castType.name)
            return collectChildren(mayThrow = false)
        } else {
            // E.g. java.lang.ClassCastException: java.lang.String cannot be cast to java.io.File
            val errorMessage = ClassNameUtils.internalToBinary(target.type) + " cannot be cast to " + castType.binaryName
            return if (target.isKnown) {
                // Exception is certain to happen since we had all class information
                throwChild(ClassCastException::class.java, errorMessage)
            } else {
                // Not enough information to know if it throws or works fine.
                collectChildren(mayThrow = true, includeChildren = true)
            }
        }
    }

    override fun toString() = "$name r$targetRegister, $castType"

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(CheckCastOp::class.java.simpleName)

        private fun isInstance(item: Value, referenceType: VirtualType, classManager: ClassManager): Boolean {
            /*
             * This covers cases where class type cannot be implied from value. E.g.
             * Object o = null;
             * String s = (String) o; // check-cast o as null passes
             */
            val itemType = classManager.getVirtualType(item.type)
            if (!itemType.isPrimitive && item.isNull) {
                return true
            }
            for (typeName in item.declaredAndValueTypeNames) {
                val type = classManager.getVirtualType(typeName)
                if (type.instanceOf(referenceType)) {
                    return true
                }
            }
            return false
        }

        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            exceptionFactory: ExceptionFactory,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instr = location.instruction as Instruction21c
            val targetRegister = instr.registerA
            val reference = instr.reference as TypeReference
            val referenceType = classManager.getVirtualType(reference)
            return CheckCastOp(location, child, targetRegister, referenceType)
        }
    }
}
