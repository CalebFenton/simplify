package org.cf.smalivm2

import org.cf.smalivm.SideEffectLevel
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.opcode.Op
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm.type.VirtualType
import java.util.*

class ExecutionNode(
    val op: Op,
    val method: VirtualMethod,
    val classManager: ClassManager,
    val classLoader: SmaliClassLoader,
    val configuration: Configuration,
    val state: ExecutionState = ExecutionState.build(
        method,
        classManager,
        classLoader,
        configuration,
        op.registersReadCount,
        op.registersAssignedCount
    ),
    var parent: ExecutionNode? = null,
    var sideEffectLevel: SideEffectLevel = SideEffectLevel.NONE
) {
    var children: MutableList<ExecutionNode> = LinkedList()

    var _caller: ExecutionNode? = null
    var caller: ExecutionNode?
        set(value) {
            _caller = value
        }
        get() {
            var current: ExecutionNode? = this
            val startingMethod = this.method
            while (current != null) {
                while (current?.method != startingMethod) {
                    current = current?.parent ?: return null
                }
                if (current._caller != null) {
                    return current._caller
                }
                current = current.parent
            }
            return null
        }


    init {
        state.node = this
    }

    val address: Int
        get() = op.address

    val index: Int
        get() = op.index

    // It's not enough to just check if the current node is the method entrypoint.
    // An execution path may throw an exception, and nodes after the exception will have ancestory (parents)
    // reaching back before the exception but register lookups should not reach beyond the throw.
    val isEntrypoint: Boolean
        get() = address == 0 && (parent == null || parent!!.method != method)

    val callDepth: Int
        get() {
            var ancestor: ExecutionNode? = this
            var callDepth = 0
            while (ancestor != null) {
                if (ancestor.isEntrypoint) {
                    callDepth++
                }
                ancestor = ancestor.parent
            }
            return callDepth
        }

    val previousMethod: ExecutionNode?
        get() {
            var ancestor: ExecutionNode? = this
            while (ancestor != null) {
                if (ancestor.isEntrypoint) {
                    return ancestor.parent
                }
                ancestor = ancestor.parent
            }
            return null
        }

    override fun toString() = "ExecutionNode{op=$op, addr=$address, method=$method}"

    fun shouldInitializeClass(virtualClass: VirtualType): Boolean {
        if (!state.isClassInitialized(virtualClass) && !isStaticallyInitializing(virtualClass)) {
            return true
        }
        return false
    }

    private fun isStaticallyInitializing(virtualClass: VirtualType): Boolean {
        var current: ExecutionNode? = this
        while (current != null) {
            if (current.method.name == "<clinit>" && current.method.definingClass == virtualClass) {
                return true
            }
            current = current.caller
        }
        return false
    }

//    def shallowClone() : ExecutionNode {
//        if (this is EntrypointNode) {
//            return EntrypointNode(op, method, classManager, classLoader, configuration, state, parent, sideEffectLevel)
//        }
//    }

//    fun getClassSideEffectLevel(virtualClass: VirtualType) = initializedClasses[virtualClass]
//
//    fun getClassSideEffectLevel(classSignature: String) = getClassSideEffectLevel(classManager.getVirtualClass(classSignature))

//    open fun getCallDepth(): Int {
//        return context.getCallDepth()
//    }

//    open fun mayThrowException(): Boolean {
//        return children.any { c -> c is ExceptionChild }
//    }

    fun clearChildren() {
        children.forEach { c -> c.parent = null }
        children.clear()
    }

    fun removeChild(child: ExecutionNode) {
        // http://stream1.gifsoup.com/view/773318/not-the-father-dance-o.gif
        child.parent = null
        children.remove(child)
    }

    fun addChild(child: ExecutionNode) {
        child.parent = this
        children.add(child)
    }

    fun replaceChild(oldChild: ExecutionNode, newChild: ExecutionNode) {
        removeChild(oldChild)
        newChild.parent = this
        children.add(newChild)
    }

    fun execute(): Array<out UnresolvedChild> {
        return op.execute(this)
    }

    fun resume(): Array<out UnresolvedChild> {
        return op.resume(this)
    }

    fun resume(calleeGraph: ExecutionGraph2): Array<out UnresolvedChild> {
        return op.resume(this, calleeGraph)
    }

//    fun spawnChild(childOp: Op, childMethod: VirtualMethod = method): ExecutionNode {
//        val childState = ExecutionState.build(childMethod, classManager, classLoader, configuration)
//        val child = ExecutionNode(
//            op = childOp,
//            method = method,
//            classManager = classManager,
//            classLoader = classLoader,
//            configuration = configuration,
//            state = childState,
//            parent = this,
//        )
//        children.add(child)
//        return child
//    }

    // TODO: make this work
//    override fun toString(): String {
//        val sb = StringBuilder("ExecutionNode{")
//        if (this.context != null) {
//            sb.append("signature=").append(context.getMethod()).append(", ")
//        }
//        sb.append("op=").append(op.toString()).append(", ")
//        sb.append("@=").append(op.address).append('}')
//        return sb.toString()
//    }

//    private fun addChild(child: org.cf.smalivm.context.ExecutionNode) {
//        children.add(child)
////        rebuildChildLocationsFromChildren()
//    }

    // TODO: Is this needed? Old node did this though it has no side effects
//    private fun rebuildChildLocationsFromChildren() {
//        val childLocations = arrayOfNulls<MethodLocation>(children.size)
//        for (i in childLocations.indices) {
//            childLocations[i] = children[i].op.location
//        }
//    }

//    companion object {
//        fun build(
//            op: Op,
//            method: VirtualMethod,
//            classManager: ClassManager,
//            classLoader: SmaliClassLoader,
//            configuration: Configuration,
//            state: ExecutionState = ExecutionState.build(
//                method,
//                classManager,
//                classLoader,
//                configuration,
//                op.registersReadCount,
//                op.registersAssignedCount
//            ),
//            parent: ExecutionNode? = null,
//            sideEffectLevel: SideEffect.Level = op.sideEffectLevel
//        ): ExecutionNode {
//            if (op.address == 0) {
//                return EntrypointNode(op, method, classManager, classLoader, configuration, state, parent, sideEffectLevel)
//            } else {
//                return ExecutionNode(op, method, classManager, classLoader, configuration, state, parent, sideEffectLevel)
//            }
//        }
//    }
}


//class EntrypointNode(
//    op: Op,
//    method: VirtualMethod,
//    classManager: ClassManager,
//    classLoader: SmaliClassLoader,
//    configuration: Configuration,
//    state: ExecutionState = ExecutionState.build(method, classManager, classLoader, configuration, op.registersReadCount, op.registersAssignedCount),
//    parent: ExecutionNode? = null,
//    sideEffectLevel: SideEffect.Level = op.sideEffectLevel
//) : ExecutionNode(op, method, classManager, classLoader, configuration, state, parent, sideEffectLevel)
//

//data class ClassStatus constructor(val classSignature: String, val sideEffectLevel: SideEffect.Level) {
//    override fun equals(other: Any?): Boolean {
//        if (this === other) {
//            return true
//        }
//        if (other !is ClassStatus) {
//            return false
//        }
//        return this.classSignature == other.classSignature
//    }
//
//    override fun hashCode() = Objects.hash(classSignature)
//}
