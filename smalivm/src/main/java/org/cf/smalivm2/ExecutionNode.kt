package org.cf.smalivm2

import org.cf.smalivm.opcode.Op
import org.jf.dexlib2.iface.instruction.DualReferenceInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.iface.reference.MethodReference
import org.jf.dexlib2.iface.reference.TypeReference
import java.util.*

abstract class BaseNode(val op: Op, val state: ExecutionState) {
    val children: MutableList<BaseNode>
    val literalClassReferences: Set<String>
    open val initializedClasses: MutableSet<String> = HashSet(0)

    init {
        children = ArrayList(op.children.size)
        literalClassReferences = HashSet<String>(2)
        val instr = this.op.instruction
        if (instr is ReferenceInstruction) {
            when (val reference = instr.reference) {
                is TypeReference -> literalClassReferences.add(reference.type)
                is MethodReference -> literalClassReferences.add(reference.definingClass)
                is FieldReference -> literalClassReferences.add(reference.definingClass)
            }
        }
        if (instr is DualReferenceInstruction) {
            when (val reference = instr.reference2) {
                is TypeReference -> literalClassReferences.add(reference.type)
                is MethodReference -> literalClassReferences.add(reference.definingClass)
                is FieldReference -> literalClassReferences.add(reference.definingClass)
            }
        }
    }

    fun execute(): Array<ExecutionNode> {
        return arrayOf()
    }

    fun setClassInitialized(classSignature: String) {
        initializedClasses.add(classSignature)
    }

    fun isClassInitialized(classSignature: String): Boolean {
        return initializedClasses.contains(classSignature)
    }

    fun getInvocationTarget(): String {
        return ""
    }

    fun resume(): Array<ExecutionNode> {
        // TODO: this is for invoke ops
        return arrayOf()
    }
}

class ExecutionNode(op: Op, state: ExecutionState, val parent: BaseNode) : BaseNode(op, state) {
    override val initializedClasses: MutableSet<String> = HashSet(0)
}

class EntrpointNode(op: Op, state: ExecutionState) : BaseNode(op, state) {
    override val initializedClasses: MutableSet<String> = HashSet()

//        val instr = this.op.instruction
//        val valuesCapacity = when (instr) {
//            is OneRegisterInstruction -> 1
//            is TwoRegisterInstruction -> 2
//            is ThreeRegisterInstruction -> 3
//            is FiveRegisterInstruction -> 5
//            is VariableRegisterInstruction -> (instr as VariableRegisterInstruction).registerCount
//            else -> 0
//        }
}

