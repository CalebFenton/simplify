package org.cf.smalivm2

import ExceptionFactory
import org.cf.smalivm.SideEffect
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.context.ExecutionContext
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.opcode.Op
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm.type.VirtualType
import java.util.*

open class ExecutionNode(
    val op: Op,
    val method: VirtualMethod,
    val classManager: ClassManager,
    val classLoader: SmaliClassLoader,
    val configuration: Configuration,
    val exceptionFactory: ExceptionFactory,
    var state: ExecutionState = ExecutionState.build(op, method, classManager, classLoader, configuration),
    var parent: ExecutionNode? = null,
    var sideEffectLevel: SideEffect.Level = op.sideEffectLevel
) {
    val address = op.address
    val index = op.index
    private val initializedClasses: MutableMap<VirtualType, SideEffect.Level> = HashMap(0)
    var children: MutableList<ExecutionNode> = LinkedList()

    private fun getAncestorWithClass(virtualClass: VirtualType): ExecutionNode? {
        var ancestor: ExecutionNode? = this
        do {
            if (ancestor!!.initializedClasses.containsKey(virtualClass)) {
                return ancestor
            }
            ancestor = ancestor.parent
        } while (ancestor != null)
        return null
    }

    fun getClassSideEffectLevel(virtualClass: VirtualType): SideEffect.Level? {
        val ancestor = getAncestorWithClass(virtualClass) ?: return null
        return ancestor.initializedClasses[virtualClass]!!
    }

    fun setClassInitialized(classSignature: String, level: SideEffect.Level) {
        val virtualClass = classManager.getVirtualClass(classSignature)
        setClassInitialized(virtualClass, level)
    }

    fun setClassInitialized(virtualClass: VirtualType, level: SideEffect.Level) {
        initializedClasses[virtualClass] = level
    }

    fun isClassInitialized(virtualClass: VirtualType): Boolean {
        return when {
            initializedClasses.contains(virtualClass) -> true
            parent != null -> parent!!.isClassInitialized(virtualClass)
            else -> false
        }
    }

    fun isClassInitialized(classSignature: String): Boolean {
        val virtualClass = classManager.getVirtualClass(classSignature)
        return isClassInitialized(virtualClass)
    }

//    fun getClassSideEffectLevel(virtualClass: VirtualType) = initializedClasses[virtualClass]
//
//    fun getClassSideEffectLevel(classSignature: String) = getClassSideEffectLevel(classManager.getVirtualClass(classSignature))

    fun resume(): Array<ExecutionNode> {
        // TODO: this is for invoke ops
        return arrayOf()
    }

//    open fun getCallDepth(): Int {
//        return context.getCallDepth()
//    }

//    open fun mayThrowException(): Boolean {
//        return children.any { c -> c is ExceptionChild }
//    }

    fun clearChildren() {
        children.forEach { c -> c.parent = null}
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

    fun execute(): Array<out OpChild> {
        return op.execute(this)
    }

    fun spawnChild(childOp: Op, childMethod: VirtualMethod = method): ExecutionNode {
        val childState = ExecutionState.build(op, childMethod, classManager, classLoader, configuration)
        val child = ExecutionNode(
            op = childOp,
            method = method,
            classManager = classManager,
            classLoader = classLoader,
            configuration = configuration,
            exceptionFactory = exceptionFactory,
            state = childState,
            parent = this,
        )
        children.add(child)
        return child
    }

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
}


class EntrypointNode(
    op: Op,
    method: VirtualMethod,
    classManager: ClassManager,
    classLoader: SmaliClassLoader,
    configuration: Configuration,
    exceptionFactory: ExceptionFactory,
    state: ExecutionState = ExecutionState.build(op, method, classManager, classLoader, configuration),
) : ExecutionNode(op, method, classManager, classLoader, configuration, exceptionFactory, state)


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
