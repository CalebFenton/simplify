package org.cf.smalivm2

import ExceptionFactory
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.opcode.Op
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualMethod
import org.jf.dexlib2.builder.MethodLocation
import java.util.*

open class ExecutionNode(
    val op: Op,
    val method: VirtualMethod,
    val classManager: ClassManager,
    val classLoader: SmaliClassLoader,
    val configuration: Configuration,
    val exceptionFactory: ExceptionFactory,
    val state: ExecutionState = ExecutionState.build(op, method, classManager, classLoader, configuration),
    var parent: ExecutionNode? = null
) {
    private val initializedClasses: MutableSet<String> = HashSet(0)
    private var children: MutableList<ExecutionNode>? = null
    private var exceptions: MutableSet<Throwable>? = null

    fun setClassInitialized(classSignature: String) {
        initializedClasses.add(classSignature)
    }

    fun isClassInitialized(classSignature: String): Boolean {
        return when {
            initializedClasses.contains(classSignature) -> true
            parent != null -> parent!!.isClassInitialized(classSignature)
            else -> false
        }
    }

    fun getInvocationTarget(): String {
        return ""
    }

    fun resume(): Array<ExecutionNode> {
        // TODO: this is for invoke ops
        return arrayOf()
    }

    fun clearChildren() {
        children = children ?: LinkedList(0)
        children!!.clear()
    }

//    open fun getCallDepth(): Int {
//        return context.getCallDepth()
//    }

    open fun mayThrowException(): Boolean {
        return exceptions!!.size > 0
    }

    open fun removeChild(child: ExecutionNode) {
        // http://stream1.gifsoup.com/view/773318/not-the-father-dance-o.gif
        children!!.remove(child)
    }

    open fun replaceChild(oldChild: ExecutionNode, newChild: ExecutionNode) {
        removeChild(oldChild)
        newChild.parent = this
    }

    // exceptions == null means exceptions haven't been set, empty list means no exceptions, otherwise op threw
    fun clearExceptions() {
        exceptions = exceptions ?: HashSet(0)
        exceptions!!.clear()
    }

    fun addException(exception: Throwable) {
        exceptions = exceptions ?: HashSet(1)
        exceptions!!.add(exception)
    }

    fun setChildren(vararg locations: MethodLocation) {
        children = children ?: locations.toMutableList()
    }

    fun execute(): Array<MethodLocation> {
        op.execute(this)

//        // Op didn't set children; pull in template values.
//        if (childLocations == null) {
//            setChildLocations(*op.children)
//        }
//
//        // Op didn't set exceptions; pull in template values.
//        if (exceptions == null) {
//            setExceptions(op.exceptions)
//        }
    }

    fun spawnChild(childOp: Op): ExecutionNode {
        val childState = ExecutionState.build(op, method, classManager, classLoader, configuration)
        return ExecutionNode(
            op = childOp,
            method = method,
            classManager = classManager,
            classLoader = classLoader,
            configuration = configuration,
            exceptionFactory = exceptionFactory,
            state = childState,
            this
        )
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
