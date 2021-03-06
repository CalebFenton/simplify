package org.cf.smalivm2

import org.cf.smalivm.opcode.Op
import org.jf.dexlib2.iface.instruction.*
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.iface.reference.MethodReference
import org.jf.dexlib2.iface.reference.TypeReference
import java.util.ArrayList
import java.util.HashSet

open class ExecutionNode(val op: Op, val children: MutableList<ExecutionNode> = ArrayList(op.children.size)) {
    // TODO: Possible optimization - could subclass this a bit and avoid having many instances of objects with many member variables.
    // Eg. move resume to InvokeNode, move classReferences to ClassReferencingNode, move method to RootNode
    var parent: ExecutionNode? = null
    private lateinit var state: ExecutionState
    val values: MutableMap<String, Value>
    val initializedClasses: MutableSet<String>
    val classReferences: Set<String>

    constructor(other: ExecutionNode) : this(other.op, ArrayList(other.children))

    init {
        val instr = this.op.instruction
        val valuesCapacity = when (instr) {
            is OneRegisterInstruction -> 1
            is TwoRegisterInstruction -> 2
            is ThreeRegisterInstruction -> 3
            is FiveRegisterInstruction -> 5
            is VariableRegisterInstruction -> (instr as VariableRegisterInstruction).registerCount
            else -> 0
        }
        values = HashMap(valuesCapacity)
        initializedClasses = HashSet<String>(0)
        classReferences = HashSet<String>()
        if (instr is ReferenceInstruction) {
            when (val reference = instr.reference) {
                is TypeReference -> classReferences.add(reference.type)
                is MethodReference -> classReferences.add(reference.definingClass)
                is FieldReference -> classReferences.add(reference.definingClass)
            }
        }
        if (instr is DualReferenceInstruction) {
            when (val reference = instr.reference2) {
                is TypeReference -> classReferences.add(reference.type)
                is MethodReference -> classReferences.add(reference.definingClass)
                is FieldReference -> classReferences.add(reference.definingClass)
            }
        }
    }

    fun setState(state: ExecutionState) {
        this.state = state
        state.node = this
    }

    fun getState(): ExecutionState {
        return this.state
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
}

class EntrypointNode(op: Op, children: MutableList<ExecutionNode> = ArrayList(op.children.size)): ExecutionNode(op, children)
class InvocationNode(op: Op, children: MutableList<ExecutionNode> = ArrayList(op.children.size)): ExecutionNode(op, children) {
    fun getInvocationTarget(): String {
        return ""
    }

    fun resume(): Array<ExecutionNode> {
        // TODO: this is for invoke ops
        return arrayOf()
    }

}
