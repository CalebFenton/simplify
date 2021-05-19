package org.cf.smalivm

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.dex.SmaliParser
import org.cf.smalivm.emulate.MethodEmulator
import org.cf.smalivm.opcode.InvokeOp
import org.cf.smalivm.opcode.Op
import org.cf.smalivm.opcode.OpBuilder
import org.cf.smalivm.opcode.SwitchOp
import org.cf.smalivm.type.*
import org.cf.util.EnumAnalyzer
import org.cf.util.Utils
import org.jf.dexlib2.builder.BuilderTryBlock
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.reflect.Modifier
import java.util.*


data class ExecutionQueueEntry(
    val graph: ExecutionGraph,
    val nodes: LinkedList<ExecutionNode>,
    val addressToVisitCount: MutableMap<Int, Long> = HashMap()
)

class VirtualMachine private constructor(
    val maxAddressVisits: Int = 500,
    val maxCallDepth: Int = 20,
    val maxMethodVisits: Int = 1_000_000,
    val maxExecutionTime: Int = 5 * 60,
) {
    lateinit var classManager: ClassManager
    lateinit var classLoader: SmaliClassLoader
    val methodToTemplateOps: MutableMap<VirtualMethod, Map<MethodLocation, Op>> = HashMap()

    //    val methodToGraph: MutableMap<VirtualMethod, ExecutionGraph2> = HashMap()
    val configuration = Configuration.instance()
    val enumAnalyzer = EnumAnalyzer(this)
    var interactive = false
    var executionStart: Long = 0
    var methodToVisitCount: MutableMap<VirtualMethod, Long> = HashMap()

    private val executionQueue: Deque<ExecutionQueueEntry> = LinkedList()
    private val resumeNodes: MutableSet<ExecutionNode> = HashSet()
    private val invokeToCalleeGraph: MutableMap<ExecutionNode, ExecutionGraph> = HashMap()

    companion object {
        private val log = LoggerFactory.getLogger(VirtualMachine::class.java.simpleName)

        const val DEFAULT_MAX_ADDRESS_VISITS = 500
        const val DEFAULT_MAX_CALL_DEPTH = 20
        const val DEFAULT_MAX_METHOD_VISTS = 1_000_000
        const val DEFAULT_MAX_EXECUTION_TIME = 5 * 60
        const val DEFAULT_OUTPUT_API_LEVEL = SmaliParser.DEX_API_LEVEL

        @JvmStatic
        @JvmOverloads
        fun build(
            inputPath: String,
            maxAddressVisits: Int = DEFAULT_MAX_ADDRESS_VISITS,
            maxCallDepth: Int = DEFAULT_MAX_CALL_DEPTH,
            maxMethodVisits: Int = DEFAULT_MAX_METHOD_VISTS,
            maxExecutionTime: Int = DEFAULT_MAX_EXECUTION_TIME,
            outputAPILevel: Int = DEFAULT_OUTPUT_API_LEVEL
        ): VirtualMachine {
            return build(File(inputPath), maxAddressVisits, maxCallDepth, maxMethodVisits, maxExecutionTime, outputAPILevel)
        }

        @JvmStatic
        @JvmOverloads
        fun build(
            inputPath: File,
            maxAddressVisits: Int = DEFAULT_MAX_ADDRESS_VISITS,
            maxCallDepth: Int = DEFAULT_MAX_CALL_DEPTH,
            maxMethodVisits: Int = DEFAULT_MAX_METHOD_VISTS,
            maxExecutionTime: Int = DEFAULT_MAX_EXECUTION_TIME,
            outputAPILevel: Int = DEFAULT_OUTPUT_API_LEVEL
        ): VirtualMachine {
            return build(ClassManager(inputPath, outputAPILevel), maxAddressVisits, maxCallDepth, maxMethodVisits, maxExecutionTime)
        }

        @JvmStatic
        @JvmOverloads
        fun build(
            classManager: ClassManager,
            maxAddressVisits: Int = DEFAULT_MAX_ADDRESS_VISITS,
            maxCallDepth: Int = DEFAULT_MAX_CALL_DEPTH,
            maxMethodVisits: Int = DEFAULT_MAX_METHOD_VISTS,
            maxExecutionTime: Int = DEFAULT_MAX_EXECUTION_TIME,
        ): VirtualMachine {
            val vm = VirtualMachine(maxAddressVisits, maxCallDepth, maxMethodVisits, maxExecutionTime)
            vm.classManager = classManager
            vm.classLoader = SmaliClassLoader(vm.classManager)
            return vm
        }
    }

    fun reset() {
        executionQueue.clear()
        resumeNodes.clear()
        invokeToCalleeGraph.clear()
    }

    fun execute(className: String, methodDescriptor: String): ExecutionGraph {
        return execute("$className->$methodDescriptor")
    }

    fun execute(methodSignature: String): ExecutionGraph {
        val method = classManager.getVirtualMethod(methodSignature) ?: throw IllegalArgumentException("Method signature not found: $methodSignature")
        return execute(method)
    }

    @JvmOverloads
    fun execute(method: VirtualMethod, state: ExecutionState? = null, parent: ExecutionNode? = null): ExecutionGraph {
        methodToVisitCount.clear()
        executionStart = System.currentTimeMillis()

        val graph = getExecutionGraph(method)
        val entrypointState = state ?: spawnEntrypointState(method)
        val entrypointNode = graph.spawnEntrypointNode(parent, entrypointState)
        val methodNodes = LinkedList<ExecutionNode>()
        methodNodes.add(entrypointNode)

        executionQueue.add(ExecutionQueueEntry(graph, methodNodes))

        val endExecutionTime = executionStart + (maxExecutionTime * 1000)
        while (!interactive && executionQueue.isNotEmpty()) {
            if (maxExecutionTime > 0 && endExecutionTime < System.currentTimeMillis()) {
                val entry = executionQueue.peek()
                val current = entry.nodes.peek()
                log.warn("Exceeded execution time for top-level method $method in ${current.method}")
                break
            }

            step()
        }

        return graph
    }

    fun step() {
        val entry = executionQueue.peekLast()
        val current = entry.nodes.pollLast()
        log.debug("step = {}", current)

        // graphs may throw exceptions in some paths, so also consider tracking (unhandled?) at the graph level (graph.setException(node, unresolved child))
        // ONLY if the exception isn't handled within the method -- if it's handled within the method go there
        // if no handler can be found, bubble it up to the caller and see if the caller can handle it
        // if there IS no caller, abort with a warning and mark graph as incomplete
        // also collapsing graphs should move return values to result register if there's consensus
        // TODO: optimization - consider blacklisting method + arguments that execute too long, record as stat
        if (!interactive) {
            if (maxAddressVisits > 0) {
                val addressToVisitCount = entry.addressToVisitCount
                val address = current.op.address
                val newAddressVisitCount = (addressToVisitCount[address] ?: 0) + 1
                if (newAddressVisitCount > maxAddressVisits) {
                    log.warn("Exceeded maximum address visits in ${current.method} @$address")
                    finishStep(entry, current, abortMethod = true)
                    return
                }
                addressToVisitCount[address] = newAddressVisitCount
            }

            if (maxMethodVisits > 0) {
                val newMethodVisitCount = (methodToVisitCount[current.method] ?: 0) + 1
                if (newMethodVisitCount > maxMethodVisits) {
                    log.warn("Exceeded maximum method visits for ${current.method} @${current.op.address}")
                    finishStep(entry, current, abortMethod = true)
                    return
                }
                methodToVisitCount[current.method] = newMethodVisitCount
            }
        }

        var unresolvedChildren: Array<out UnresolvedChild>? = null
        if (current.isEntrypoint) {
            if (!interactive) {
                if (maxCallDepth > 0 && current.callDepth > maxCallDepth) {
                    log.warn("Exceeded maximum call depth at {} @{}", current.method, current.op.address)
                    finishStep(entry, current, abortMethod = true)
                    return
                }
            }

            val isStaticInit = current.method.name == "<clinit>"
            if (isStaticInit) {
                if (configuration.isUnsafeMethod(current.method.signature)) {
                    log.warn("Refusing to initialize unsafe class: {}", current.method)
                    finishStep(entry, current, abortMethod = true)
                    return
                }

                if (entry.graph.reflected) {
                    // No need to execute on the VM since this class is reflected
                    // Set this class as initialized in the caller since we're not going to collapse the multiverse
                    current?.caller?.state?.setClassInitialized(current.method.definingClass)
                    finishStep(entry, current, mergeChildClassStates = false)
                    return
                }
            } else {
                val initializationQueued = enqueueClassInitializationIfNecessary(current.method.definingClass, current)
                if (initializationQueued) {
                    // Need to initialize classes before executing this. Pretend it never happened.
                    entry.nodes.add(current)
                    return
                }

                if (entry.graph.emulatedOrReflected) {
                    val allArgumentsKnown = current.state.allArgumentsKnown()
                    unresolvedChildren = if (entry.graph.emulated && (allArgumentsKnown || MethodEmulator.canHandleUnknownValues(current.method))) {
                        log.debug("emulating {}", current.method)
                        MethodEmulator.emulate(current.method, current.state, current.parent, this)
                    } else if (entry.graph.reflected && allArgumentsKnown) {
                        log.debug("reflecting {}", current.method)
                        MethodReflector.reflect(current.method, current.state)
                    } else {
                        log.trace("All arguments not known for emulated or reflected method {}; assuming maximum unknown.", current.method)
                        finishStep(entry, current, abortMethod = true)
                        return
                    }
                } else {
                    // This can happen if a method returns an unknown value of a type which implements an interface
                    // Since the "real" value is unknown, the real class of the invocation target can't be determined.
                    val method = entry.graph.method
                    if (!method.hasImplementation()) {
                        log.warn("Unable to execute method without implementation: {}, isNative={}", method, method.isNative)
                        finishStep(entry, current, abortMethod = true)
                        return
                    }
                }
            }
        }

        if (unresolvedChildren != null) {
            entry.graph.addNode(current)
        } else {
            unresolvedChildren = if (resumeNodes.contains(current)) {
                resumeNodes.remove(current)
                if (current.op is InvokeOp) {
                    val calleeGraph = invokeToCalleeGraph.remove(current)!!
                    current.resume(calleeGraph)
                } else {
                    current.resume()
                }
            } else {
                entry.graph.addNode(current)
                current.execute()
            }
        }

        // TODO: if it's a method invocation and it's a static init, then put current node back in nodes and return
        // NEW: emulated + reflected methods should return child for "finish method" which has a sideEffectLevel
        for (unresolvedChild in unresolvedChildren) {
            when (unresolvedChild) {
                is UnresolvedContinueChild -> {
                    enqueueContinueChildNode(current, entry.graph, entry.nodes)
                }
                is UnresolvedStaticClassInit -> {
                    // This child is only returned if an Op *knows* a class should be initialized.
                    // But sometimes a class doesn't have a <clinit> method so always mark this node to be resumed
                    resumeNodes.add(current)
                    entry.nodes.add(current)
                    enqueueClassInitializationIfNecessary(unresolvedChild.virtualClass, current)
                }
                is UnresolvedMethodInvocationChild -> {
                    resumeNodes.add(current)
                    entry.nodes.add(current)
                    enqueueMethodInvocation(unresolvedChild.method, unresolvedChild.state, current)
                }
                is UnresolvedAddressChild -> {
                    enqueueChildNode(current, unresolvedChild.address, entry.graph, entry.nodes)
                }
                is UnresolvedExceptionChild -> {
                    // TODO: How does the finally work exactly? Any special handling needed?
                    // TODO: if all children are exceptional, remove caller from resume and execution queue (should be most recent, last index of)
                    enqueueExceptionChild(current, unresolvedChild, entry.graph)
                }
            }
        }

        finishStep(entry, current)
    }

    private fun enqueueExceptionChild(exceptional: ExecutionNode, unresolvedChild: UnresolvedExceptionChild, graph: ExecutionGraph) {
        enqueueExceptionChild(exceptional, exceptional, unresolvedChild, graph)
    }

    private fun enqueueExceptionChild(
        exceptional: ExecutionNode,
        current: ExecutionNode,
        unresolvedChild: UnresolvedExceptionChild,
        graph: ExecutionGraph
    ) {
        val exceptionClass = classManager.getVirtualClass(unresolvedChild.exceptionClass)
        val handlerAddress = if (graph.emulatedOrReflected) {
            // Exception was within emulated or reflected method so shouldn't use graph's try handlers.
            // Add to unhandled exceptions and it'll get bubbled up in calling methods.
            -1
        } else {
            findExceptionHandlerAddress(exceptionClass, current.address, current.method)
        }

        if (handlerAddress < 0) {
            log.debug("{} threw exception {} which is unhandled at {}", exceptional, unresolvedChild, current)
            graph.unhandledExceptions.add(UnhandledVirtualException(exceptional, unresolvedChild))
        } else {
            log.trace("{} threw exception {} which is handled in {} @{}", exceptional, unresolvedChild, current, handlerAddress)
            // TODO: Consider nodes data structure with faster lookup (LinkedHashSet)
            val handlerEntry = getEntryForNode(current)
            val child = enqueueChildNode(exceptional, handlerAddress, handlerEntry.graph, handlerEntry.nodes)
            val exception = buildException(exceptional, unresolvedChild.exceptionClass, unresolvedChild.message)
            child.state.assignExceptionRegister(exception)
        }
    }

    private fun buildException(current: ExecutionNode, exceptionClass: Class<out Throwable>, message: String?): Value {
        // TODO: Hard - Spoof the stack trace here. Probably will require reflection and messing with some private data structures.
        val rawException = try {
            val ctor = exceptionClass.getDeclaredConstructor(String::class.java)
            ctor.isAccessible = true
            ctor.newInstance(message)
        } catch (e: Exception) {
            Exception()
            log.error("Exception building exception $exceptionClass for $current", e)
        }

        return Value.wrap(rawException, exceptionClass)
    }

    private fun getEntryForNode(node: ExecutionNode): ExecutionQueueEntry {
        return executionQueue.first { it.graph.hasNode(node) } ?: executionQueue.first { it.nodes.contains(node) }
    }

    private fun enqueueContinueChildNode(current: ExecutionNode, graph: ExecutionGraph, methodNodes: MutableList<ExecutionNode>) {
        val opcode = current.op.instruction!!.opcode
        if (opcode.canContinue() && current.op !is SwitchOp) {
            enqueueChildNode(current, current.op.nextAddress, graph, methodNodes)
        }
    }

    private fun enqueueChildNode(
        parent: ExecutionNode,
        address: Int,
        graph: ExecutionGraph,
        methodNodes: MutableList<ExecutionNode>
    ): ExecutionNode {
        val location = graph.getLocation(address)!!
        val op = methodToTemplateOps[graph.method]!![location]!!
        val child = ExecutionNode(op, graph.method, classManager, classLoader, configuration, parent = parent)
        parent.children.add(child)
        methodNodes.add(child)
        return child
    }

    private fun finishStep(
        entry: ExecutionQueueEntry,
        current: ExecutionNode,
        mergeChildClassStates: Boolean = true,
        abortMethod: Boolean = false,
    ) {
        if (abortMethod) {
            entry.nodes.clear()
        }
        if (entry.nodes.isEmpty()) {
            executionQueue.remove(entry)
            val caller = current.caller
            if (caller != null) {
                for (unhandledException in entry.graph.unhandledExceptions) {
                    val callerEntry = getEntryForNode(caller)
                    enqueueExceptionChild(unhandledException.node, caller, unhandledException.unresolvedChild, callerEntry.graph)
                }
                if (abortMethod) {
                    entry.graph.aborted = true
                }
                if (mergeChildClassStates) {
                    mergeClassStates(current.method, entry.graph, caller.state)
                }
            }
        }
    }

    /*
     * Get the consensus of mutable objects of method and class states of called context and merge them into
     * the context of the caller. In other words, reflect changes to objects that happen in the called method back
     * into the caller method.
     */
    private fun mergeClassStates(
        calledMethod: VirtualMethod,
        graph: ExecutionGraph,
        callerState: ExecutionState,
    ) {
        if (calledMethod.descriptor == "<clinit>()V") {
            val sideEffectLevel = graph.getHighestMethodSideEffectLevel()
            callerState.setClassInitialized(calledMethod.definingClass, sideEffectLevel)
        }

        if (graph.executionType == ExecutionGraph.ExecutionType.REFLECT) {
            // Reflection doesn't initialize any classes in our VM
            return
        }

        // Class states (initialized, field values) may have changed. Merge into caller.
        // TODO: performance: this is expensive and happens frequently.
        val terminatingStates = graph.getTerminatingStates()
        for (virtualClass in classManager.getLoadedClasses()) {
            val isInitializedInCaller = callerState.isClassInitialized(virtualClass)
            if (!isInitializedInCaller) {
                // Not initialized in caller, but maybe initialized in callee multiverse.
                var isInitializedInCallee = false
                for (state in terminatingStates) {
                    if (state.isClassInitialized(virtualClass)) {
                        isInitializedInCallee = true
                        val level = graph.getHighestClassSideEffectLevel(virtualClass)
                        callerState.setClassInitialized(virtualClass, level)
                        break
                    }
                }
                if (!isInitializedInCallee) {
                    // Class was never initialized. Nothing to merge into caller method.
                    continue
                }
            }

            if (graph.aborted) {
                continue
            }

            val staticFields = virtualClass.fields.filter { it.isStatic }
            for (field in staticFields) {
                val consensus = graph.getTerminatingFieldConsensus(field)
                // TODO: Should this also include immutable types?
                if (consensus.isPrimitive) {
                    callerState.pokeField(field, consensus)
                } else {
                    callerState.pokeField(field, consensus, updateIdentities = true)
                }
            }
        }
    }

    private fun enqueueMethodInvocation(method: VirtualMethod, calleeState: ExecutionState, parent: ExecutionNode) {
        log.debug("enqueuing method {}, parent={}", method, parent)
        val graph = getExecutionGraph(method)
        val entrypointNode = graph.spawnEntrypointNode(caller = parent, state = calleeState)
        val methodNodes = LinkedList<ExecutionNode>()
        methodNodes.add(entrypointNode)
        executionQueue.add(ExecutionQueueEntry(graph, methodNodes))
        if (parent.op is InvokeOp) {
            invokeToCalleeGraph[parent] = graph
        }
    }

    private fun enqueueClassInitializationIfNecessary(virtualClass: VirtualType, parent: ExecutionNode): Boolean {
        /*
         * On the JVM, classed should be statically initialized when:
         * 1.) The invocation of a method declared by the class (not inherited from a superclass)
         * 2.) The invocation of a constructor of the class (covered by #1)
         * 3.) The use or assignment of a field declared by a class (not inherited from a superclass), except for fields
         * that are both static and final, and are initialized by a compile-time constant expression.
         */
        if (!parent.shouldInitializeClass(virtualClass)) {
            return false
        }

        // Initialize class ancestors as well
        for (ancestor in virtualClass.ancestors) {
            // TODO: easy - shove these modifier methods as convenience methods into VirtualType
            val accessFlags = ancestor.classDef.accessFlags
            if (Modifier.isAbstract(accessFlags) || Modifier.isInterface(accessFlags) || Modifier.isNative(accessFlags)) {
                continue
            }
            enqueueClassInitializationIfNecessary(ancestor, parent)
        }

        val method = virtualClass.getMethod("<clinit>()V")
        if (method == null) {
            // Class has no static initializer but still need to set any literal fields, e.g.
            // .field public static myInt:I = 0x4
            log.debug("initializing class without <clinit>()V: {}", virtualClass)
            initializeClassState(parent.state, virtualClass)
            parent.state.setClassInitialized(virtualClass)
            return false
        }
        val calleeState = ExecutionState.build(method, classManager, classLoader, configuration)
        enqueueMethodInvocation(method, calleeState, parent)
        return true
    }

    fun getExecutionGraph(className: String, methodDescriptor: String): ExecutionGraph {
        val method = classManager.getVirtualClass(className).getMethod(methodDescriptor)!!
        return getExecutionGraph(method)
    }

    fun getExecutionGraph(method: VirtualMethod): ExecutionGraph {
        if (!methodToTemplateOps.containsKey(method)) {
            updateTemplateOps(method)
        }
        return ExecutionGraph(method, this)
    }

    fun spawnEntrypointState(methodSignature: String): ExecutionState {
        val method = classManager.getVirtualMethod(methodSignature)!!
        return spawnEntrypointState(method)
    }

    fun spawnEntrypointState(className: String, methodDescriptor: String): ExecutionState {
        val klazz = classManager.getVirtualClass(className)
        val method = klazz.getMethod(methodDescriptor) ?: throw RuntimeException("Method signature not found: $className->$methodDescriptor")
        return spawnEntrypointState(method)
    }

    fun spawnEntrypointState(method: VirtualMethod): ExecutionState {
        if (!method.hasImplementation()) {
            // Native or abstract methods have no implementation. Shouldn't be executing them.
            throw IllegalArgumentException("No implementation for $method");
        }

        val state = ExecutionState.build(method, classManager, classLoader, configuration)
        initializeMethodState(state, method)
        initializeClassState(state, method.definingClass)

        return state
    }

    private fun initializeMethodState(state: ExecutionState, method: VirtualMethod) {
        // TODO: Should expose constants to the user so they could set arguments! Would be neat.
        val registerCount = method.registerCount
        val parameterTypeNames = method.parameterTypeNames
        val parameterSize = Utils.getRegisterSize(parameterTypeNames)
        val firstParameterRegister = registerCount - parameterSize
        val fieldCount = method.definingClass.fields.size
        var currentRegister = firstParameterRegister
        for (typeName in method.parameterTypeNames) {
            val value = if (currentRegister == firstParameterRegister && !method.isStatic && method.name == "<init>") {
                // Use defining class instead of typeName as it should be more specific
                Value.uninitializedInstance(method.definingClass)
            } else {
                Value.unknown(typeName)
            }
            state.assignRegister(currentRegister, value)
            currentRegister += Utils.getRegisterSize(typeName)
        }
    }

    private fun initializeClassState(state: ExecutionState, virtualClass: VirtualType) {
        val staticFields = virtualClass.fields.filter { it.isStatic }
        for (field in staticFields) {
            val value = Value.wrap(field.initialValue, field.type)
            state.pokeField(field, value)
        }
    }

    fun updateTemplateOps(method: VirtualMethod) {
        val opBuilder = OpBuilder(classManager, classLoader, configuration)
        val instructions = if (method.hasImplementation()) {
            method.implementation.instructions
        } else {
            // This is a bit of a hack. Need to create "placeholder" graph for those without implementations since the VM expects to be able step
            // into any method before deciding if it should be executed. Using Object;-><init>()V is nice because it's empty. The step function
            // will check if there's an implementation if it can't be reflected or emulated.
            val initMethod = classManager.getVirtualMethod("${CommonTypes.OBJECT}-><init>()V")!!
            initMethod.implementation.instructions
        }
        val locationToOp: MutableMap<MethodLocation, Op> = HashMap(instructions.size)
        for (instruction in instructions) {
            val location = instruction.location
            locationToOp[location] = opBuilder.build(location)
        }
        methodToTemplateOps[method] = locationToOp
    }

    fun findExceptionHandlerAddress(exceptionClass: VirtualClass, address: Int, method: VirtualMethod): Int {
        var handlerAddress = findExceptionHandlerAddress(exceptionClass, address, method, false)
        if (handlerAddress != -1) {
            return handlerAddress
        }
        handlerAddress = findExceptionHandlerAddress(exceptionClass, address, method, true)
        return handlerAddress
    }

    /*
     * Given an exception type and an address in code, this will try and find the correct exception handler. This is
     * complicated by the fact that handlers may overlap. There may be 6 overlapping exception handlers over one
     * particular address each catching different exception types with one handler as a catch-all.
     * This will try to find the most "immediate" exception handler in terms of class hierarchy.  If a handler for
     * the exact exception type can't be found, it'll try to find one for the exception type's parent, parent's
     * parent, and so on. If catchAll is true, it'll look for catch-all exception handlers.
     */
    private fun findExceptionHandlerAddress(exceptionClass: VirtualClass, address: Int, method: VirtualMethod, catchAll: Boolean): Int {
        val enclosingTryBlocks: MutableList<BuilderTryBlock> = LinkedList()
        for (tryBlock in method.tryBlocks) {
            val startAddress = tryBlock.startCodeAddress
            val endAddress = tryBlock.startCodeAddress + tryBlock.codeUnitCount
            if (address in startAddress until endAddress) {
                enclosingTryBlocks.add(tryBlock)
            }
        }
        if (enclosingTryBlocks.isEmpty()) {
            return -1
        }

        if (catchAll) {
            for (tryBlock in enclosingTryBlocks) {
                // The catchall handler will be last with a null type
                val handler = tryBlock.exceptionHandlers.last()
                if (handler.exceptionType == null) {
                    return handler.handlerCodeAddress
                }
            }
        } else {
            val exceptionClassLineage: MutableList<VirtualType> = LinkedList()
            exceptionClassLineage.add(exceptionClass)
            exceptionClassLineage.addAll(exceptionClass.ancestors)
            for (currentExceptionClass in exceptionClassLineage) {
                for (tryBlock in enclosingTryBlocks) {
                    val handlers = tryBlock.exceptionHandlers
                    for (handler in handlers) {
                        val handlerType = handler.exceptionType
                        if (currentExceptionClass.name == handlerType) {
                            return handler.handlerCodeAddress
                        }
                    }
                }
            }
        }

        return -1
    }
}
