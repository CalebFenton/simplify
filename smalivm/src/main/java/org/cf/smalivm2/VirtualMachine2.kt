package org.cf.smalivm2

import org.cf.smalivm.MethodReflector
import org.cf.smalivm.configuration.Configuration
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
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.reflect.Modifier
import java.util.*


data class ExecutionQueueEntry(
    val graph: ExecutionGraph2,
    val nodes: LinkedList<ExecutionNode>,
    val addressToVisitCount: MutableMap<Int, Long> = HashMap()
)

class VirtualMachine2 private constructor(
    val maxAddressVisits: Int = 500,
    val maxCallDepth: Int = 20,
    val maxMethodVisits: Int = 1_000_000,
    val maxExecutionTime: Int = 5 * 60,
) {
    lateinit var classManager: ClassManager
    lateinit var classLoader: SmaliClassLoader
    val methodToTemplateOps: MutableMap<VirtualMethod, Map<MethodLocation, Op>> = HashMap()
    val methodToGraph: MutableMap<VirtualMethod, ExecutionGraph2> = HashMap()
    val configuration = Configuration.instance()
    val enumAnalyzer = EnumAnalyzer(this)
    var interactive = false
    var executionStart: Long = 0
    var methodToVisitCount: MutableMap<VirtualMethod, Long> = HashMap()

    private val executionQueue: Deque<ExecutionQueueEntry> = LinkedList()
    private val resumeNodes: MutableSet<ExecutionNode> = HashSet()

    companion object {
        private val log = LoggerFactory.getLogger(VirtualMachine2::class.java.simpleName)

        const val DEFAULT_MAX_ADDRESS_VISITS = 500
        const val DEFAULT_MAX_CALL_DEPTH = 20
        const val DEFAULT_MAX_METHOD_VISTS = 1_000_000
        const val DEFAULT_MAX_EXECUTION_TIME = 5 * 60
        const val DEFAULT_OUTPUT_API_LEVEL = SmaliParser.DEX_API_LEVEL

        fun build(
            inputPath: String,
            maxAddressVisits: Int = DEFAULT_MAX_ADDRESS_VISITS,
            maxCallDepth: Int = DEFAULT_MAX_CALL_DEPTH,
            maxMethodVisits: Int = DEFAULT_MAX_METHOD_VISTS,
            maxExecutionTime: Int = DEFAULT_MAX_EXECUTION_TIME,
            outputAPILevel: Int = DEFAULT_OUTPUT_API_LEVEL
        ): VirtualMachine2 {
            return build(File(inputPath), maxAddressVisits, maxCallDepth, maxMethodVisits, maxExecutionTime, outputAPILevel)
        }

        fun build(
            inputPath: File,
            maxAddressVisits: Int = DEFAULT_MAX_ADDRESS_VISITS,
            maxCallDepth: Int = DEFAULT_MAX_CALL_DEPTH,
            maxMethodVisits: Int = DEFAULT_MAX_METHOD_VISTS,
            maxExecutionTime: Int = DEFAULT_MAX_EXECUTION_TIME,
            outputAPILevel: Int = DEFAULT_OUTPUT_API_LEVEL
        ): VirtualMachine2 {
            return build(ClassManager2(inputPath, outputAPILevel), maxAddressVisits, maxCallDepth, maxMethodVisits, maxExecutionTime)
        }

        fun build(
            classManager: ClassManager,
            maxAddressVisits: Int = DEFAULT_MAX_ADDRESS_VISITS,
            maxCallDepth: Int = DEFAULT_MAX_CALL_DEPTH,
            maxMethodVisits: Int = DEFAULT_MAX_METHOD_VISTS,
            maxExecutionTime: Int = DEFAULT_MAX_EXECUTION_TIME,
        ): VirtualMachine2 {
            val vm = VirtualMachine2(maxAddressVisits, maxCallDepth, maxMethodVisits, maxExecutionTime)
            vm.classManager = classManager
            vm.classLoader = SmaliClassLoader(vm.classManager)
            return vm
        }
    }

    fun reset() {
        executionQueue.clear()
        resumeNodes.clear()
    }

    fun execute(className: String, methodDescriptor: String): ExecutionGraph2 {
        return execute("$className->$methodDescriptor")
    }

    fun execute(methodSignature: String): ExecutionGraph2 {
        val method = classManager.getMethod(methodSignature) ?: throw IllegalArgumentException("Method signature not found: $methodSignature")
        return execute(method)
    }

    fun execute(method: VirtualMethod, state: ExecutionState? = null, parent: ExecutionNode? = null): ExecutionGraph2 {
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

        // TODO: change to val and check if it's initialized later
        var unresolvedChildren: Array<out UnresolvedChild>? = null
        if (current.isEntrypoint) {
            if (!interactive) {
                if (maxCallDepth > 0 && current.callDepth > maxCallDepth) {
                    log.warn("Exceeded maximum call depth at ${current.method} @${current.op.address}")
                    finishStep(entry, current, abortMethod = true)
                    return
                }
            }

            val isSafe = configuration.isSafe(current.method)
            val isStaticInit = current.method.descriptor == "<clinit>()V"
            if (isStaticInit) {
                if (isSafe) {
                    // No need to execute on the VM since Safe class methods and fields are reflected
                    current.state.setClassInitialized(current.method.definingClass)
                    finishStep(entry, current, collapseMultiverse = false)
                    return
                }
            } else {
                val initializationQueued = enqueueStaticInitializationIfNecessary(current.method.definingClass, current)
                if (initializationQueued) {
                    // Need to initialize classes before executing this. Pretend it never happened.
                    entry.nodes.add(current)
                    return
                }

                val canEmulate = MethodEmulator.canEmulate(current.method)
                if (isSafe || canEmulate) {
                    val allArgumentsKnown = current.state.allArgumentsKnown()
                    unresolvedChildren = if (canEmulate && (allArgumentsKnown || MethodEmulator.canHandleUnknownValues(current.method))) {
                        MethodEmulator.emulate(current.method, current.state, current.parent, this)
                    } else if (isSafe && allArgumentsKnown) {
                        MethodReflector.reflect(current.method, current.state, current.classLoader, enumAnalyzer)
                    } else {
                        log.trace("Not emulating / reflecting {}; all arguments are not known. Assuming maximum unknown.", current.method)
                        finishStep(entry, current, abortMethod = true)
                        return
                    }
                } else {
                    if (classManager.isFrameworkClass(current.method.definingClass) &&
                        !classManager.isSafeFrameworkClass(current.method.definingClass)
                    ) {
                        log.debug("Not executing unsafe framework method: {}. Assuming maximum ambiguity.", current.method)
                        finishStep(entry, current, abortMethod = true)
                        return
                    }
                }
            }
        }

        var emulatedOrReflected = false
        if (unresolvedChildren == null) {
            unresolvedChildren = if (resumeNodes.contains(current)) {
                resumeNodes.remove(current)
                current.resume()
            } else {
                entry.graph.addNode(current)
                current.execute()
            }
        } else {
            entry.graph.addNode(current)
            emulatedOrReflected = true
        }

        // TODO: if it's a method invocation and it's a static init, then put current node back in nodes and return
        // invoke-op doesn't need a resume either, vm can just setup result register and collapse multiverse
        // invoke op should continue after everything is finished executing assuming there are no exceptions
        // can check caller and if caller is an invoke op and you're not calling a static init, you should know to add a resume
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
                    // TODO: consider putting this in enqueueMethodInvocation (and when resolving method invocation children)
                    entry.nodes.add(current)
                    enqueueStaticInitializationIfNecessary(unresolvedChild.virtualClass, current)
                }
                is UnresolvedMethodInvocationChild -> {
                    entry.nodes.add(current)
                    enqueueMethodInvocation(unresolvedChild.method, unresolvedChild.state, current)
                }
                is UnresolvedAddressChild -> {
                    enqueueChildNode(current, unresolvedChild.address, entry.graph, entry.nodes)
                }
                is UnresolvedExceptionChild -> {
                    // TODO: Should anything be done with the exception message here? Maybe it'll be more obvious elsewhere.
                    // TODO: How does the finally concept work here? Any special handling needed?
                    val exceptionClass = classManager.getVirtualClass(unresolvedChild.exceptionClass)
                    val handlerAddress = if (unresolvedChild.unhandled) {
                        -1
                    } else {
                        findExceptionHandlerAddress(exceptionClass, current.address, current.method)
                    }
                    if (handlerAddress < 0) {
                        log.trace("{} threw unhandled exception {}; will bubble up to caller.")
                        val e = UnhandledVirtualException(current, exceptionClass, unresolvedChild.message)
                        entry.graph.unhandledVirtualException.add(e)
                    } else {
                        log.trace("{} threw exception {} and is handled by exception handler @{}", current, unresolvedChild, handlerAddress)
                        val child = enqueueChildNode(current, handlerAddress, entry.graph, entry.nodes)
                        val exception = buildException(current, unresolvedChild.exceptionClass, unresolvedChild.message)
                        child.state.assignExceptionRegister(exception)
                    }
                }
            }
        }

        finishStep(entry, current)
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

    private fun enqueueContinueChildNode(current: ExecutionNode, graph: ExecutionGraph2, methodNodes: MutableList<ExecutionNode>) {
        val opcode = current.op.instruction!!.opcode
        if (opcode.canContinue() && current.op !is SwitchOp) {
            enqueueChildNode(current, current.op.nextAddress, graph, methodNodes)
        }
    }

    private fun enqueueChildNode(
        parent: ExecutionNode,
        address: Int,
        graph: ExecutionGraph2,
        methodNodes: MutableList<ExecutionNode>
    ): ExecutionNode {
        val location = graph.getLocation(address)!!
        val op = methodToTemplateOps[parent.method]!![location]!!
        val child = ExecutionNode(op, parent.method, classManager, classLoader, configuration, parent = parent)
        parent.children.add(child)
        methodNodes.add(child)
        return child
    }

    private fun finishStep(entry: ExecutionQueueEntry, current: ExecutionNode, collapseMultiverse: Boolean = true, abortMethod: Boolean = false) {
        if (abortMethod) {
            entry.nodes.clear()
        }
        if (entry.nodes.isEmpty()) {
            executionQueue.remove(entry)
            val caller = current.caller
            if (caller != null) {
                val callerEntry = executionQueue.first { it.graph.hasNode(caller) }
//                if (current.method.descriptor != "<clinit>()V") {
//                    callerEntry.nodes.add(caller)
//                }
                if (abortMethod) {
//                    entry.graph.aborted = true
                    assumeMaximumUnknown(caller.state, current.state, current.method)
                } else {
                    if (collapseMultiverse) {
                        if (caller.op is InvokeOp) {
                            collapseMultiverse(current.method, entry.graph, caller.state, caller.op.parameterRegisters)
                        } else {
                            collapseMultiverse(current.method, entry.graph, caller.state)
                        }
                    }
                }
            }
        }
    }

//    private fun finishLocalMethodExecution(
//        calleeContext: ExecutionContext?,
//        callerContext: ExecutionContext?,
//        node: ExecutionNode?,
//        graph: ExecutionGraph?
//    ) {
//        if (graph == null) {
//            // Maybe node visits or call depth exceeded?
//            InvokeOp.log.info("Problem executing {}, propagating ambiguity.", calleeContext!!.method)
//            assumeMaximumUnknown(callerContext!!.methodState)
//            return
//        }
//        var hasOneNonThrow = false
//        for (endAddress in graph.connectedTerminatingAddresses) {
//            val endOp = graph.getTemplateNode(endAddress)!!.op
//            if (endOp is ThrowOp) {
//                // At least one execution path leads to throwing an exception.
//                val items = graph.getRegisterItems(endAddress, MethodState.ThrowRegister)
//                for (item in items) {
//                    if (item.value is Throwable) {
//                        val exception = item.value as Throwable?
//                        addException(exception!!)
//                    } else {
//                        // Possibly UninitializedInstance
//                        if (InvokeOp.log.isWarnEnabled) {
//                            if (item.isUnknown) {
//                                InvokeOp.log.warn(
//                                    "Method had possible execution path which throws an exception but cannot instantiate it because the value is unknown. Exception item: {}",
//                                    item
//                                )
//                            } else {
//                                // May just need to whitelist Exception class
//                                InvokeOp.log.warn("Refusing to instantiate potentially unsafe thrown exception: {}.", item)
//                            }
//                        }
//                    }
//                }
//            } else {
//                hasOneNonThrow = true
//            }
//        }
//        if (!hasOneNonThrow) {
//            // Everything was an exception; don't go execute any normal children
//            node.clearChildren()
//        } else {
//            if (method.returnType != CommonTypes.VOID) {
//                // Terminating addresses may include throw ops which may not have a return register set
//                val returnOpAddresses: TIntList = TIntLinkedList()
//                for (address in graph.connectedTerminatingAddresses) {
//                    if (graph.getOp(address) is ReturnOp) {
//                        returnOpAddresses.add(address)
//                    }
//                }
//                val consensus = graph.getRegisterConsensus(returnOpAddresses.toArray(), MethodState.ReturnRegister)
//                callerContext!!.methodState.assignResultRegister(consensus)
//            } else {
//                if (calleeContext!!.method.descriptor.startsWith("<init>(")) {
//                    // This was a call to a local parent <init> method
//                    val calleeInstanceRegister = calleeContext.methodState.parameterStart
//                    val newInstance = graph.getTerminatingRegisterConsensus(calleeInstanceRegister)
//                    val instanceRegister = parameterRegisters[0]
//                    callerContext!!.methodState.assignRegisterAndUpdateIdentities(instanceRegister, newInstance)
//                }
//            }
//        }
//        sideEffectLevel = graph.highestSideEffectLevel
//    }
//
//    private fun executeNonLocalMethod(
//        methodDescriptor: String,
//        callerMethodState: MethodState,
//        calleeContext: ExecutionContext,
//        node: ExecutionNode
//    ) {
//        if (MethodEmulator.canEmulate(methodDescriptor)) {
//            val emulator = MethodEmulator(vm, calleeContext, methodDescriptor)
//            emulator.emulate(this)
//            sideEffectLevel = emulator.sideEffectLevel
//            if (emulator.exceptions.size > 0) {
//                node.clearChildren()
//                node.setExceptions(emulator.exceptions)
//                return
//            }
//        } else if (vm.configuration.isSafe(methodDescriptor)) {
//            val reflector = MethodReflector(vm, method)
//            try {
//                reflector.reflect(calleeContext.methodState) // playa play
//            } catch (e: Exception) {
//                node.addException(e)
//                node.clearChildren()
//                return
//            }
//
//            // Only safe, non-side-effect methods are allowed to be reflected.
//            sideEffectLevel = SideEffect.Level.NONE
//        }
//        if (!method.isStatic) {
//            // This is virtual and the instance parse may have been initialized or mutated.
//            val originalInstanceItem = callerMethodState.peekRegister(parameterRegisters[0])
//            val newInstanceItem = calleeContext.methodState.peekParameter(0)
//            if (originalInstanceItem!!.value !== newInstanceItem.value) {
//                // Instance has been initialized, i.e. was UninitializedInstance
//                // Use assignRegisterAndUpdateIdentities because multiple registers may have an identical
//                // UninitializedInstance, and those need to be updated with the new instance.
//                callerMethodState.assignRegisterAndUpdateIdentities(parameterRegisters[0], newInstanceItem)
//            } else {
//                val isMutable = !vm.configuration.isImmutable(newInstanceItem.type)
//                if (isMutable) {
//                    // The instance class is mutable so could have changed. Record that it was changed for the
//                    // optimizer.
//                    callerMethodState.assignRegister(parameterRegisters[0], newInstanceItem)
//                }
//            }
//        }
//        if (!method.returnsVoid()) {
//            val returnItem = calleeContext.methodState.readReturnRegister()
//            callerMethodState.assignResultRegister(returnItem)
//        }
//    }

    /*
     * Get the consensus of mutable objects of method and class states of called context and merge them into
     * the context of the caller. In other words, reflect changes to objects that happen in the called method back
     * into the caller method.
     */
    private fun collapseMultiverse(
        calledMethod: VirtualMethod,
        graph: ExecutionGraph2,
        callerState: ExecutionState,
        parameterRegisters: IntArray? = null
    ) {
        /*
        TODO: if every path in the graph is a throw (there are no connected returns), then what?
        a graph may have multiple unhandled virtual exceptions that need to be handled here or potentially bubbled up
        meaning a caller could have several children: unhandled exceptions and normal stuff
         */
        val terminatingAddresses = graph.getConnectedTerminatingAddresses()
        if (!(parameterRegisters == null || parameterRegisters.isEmpty())) {
            val parameterTypes = calledMethod.parameterTypeNames
            var parameterRegister = graph.getNodePile(0)[0].state.firstParameterRegister
            for (parameterIndex in parameterTypes.indices) {
                val type = parameterTypes[parameterIndex]
                if (configuration.isMutable(type)) {
                    val item = graph.getRegisterConsensus(terminatingAddresses, parameterRegister)
                    val register = parameterRegisters[parameterIndex]
                    callerState.assignRegister(register, item, updateIdentities = true)
                }
                parameterRegister += Utils.getRegisterSize(type)
            }
        }

        // TODO: performance: this is expensive and happens frequently.
        // classManager has all local classes. would be nice to keep track of all initialized classes
        // for each method execution, and use that to iterate instead
        val terminatingStates = graph.getTerminatingStates()
        for (virtualClass in classManager.loadedClasses) {
            val isInitializedInCaller = callerState.isClassInitialized(virtualClass)
            if (!isInitializedInCaller) {
                // Not initialized in caller, but maybe initialized in callee multiverse.
                var isInitializedInCallee = false
                for (state in terminatingStates) {
                    if (state.isClassInitialized(virtualClass)) {
                        isInitializedInCallee = true
                        break
                    }
                }

                if (isInitializedInCallee) {
                    break
                }

                // Class was never initialized. Nothing to merge into caller method.
                continue
            }

            if (!isInitializedInCaller) {
                val level = graph.getHighestClassSideEffectLevel(virtualClass)
                callerState.setClassInitialized(virtualClass, level)
            }
            for (field in virtualClass.fields) {
                val consensus = graph.getFieldConsensus(terminatingAddresses, field)
                if (consensus.isPrimitive) {
                    callerState.pokeField(field, consensus)
                } else {
                    callerState.pokeField(field, consensus, updateIdentities = true)
                }
            }
        }
    }

    private fun enqueueMethodInvocation(method: VirtualMethod, calleeState: ExecutionState, parent: ExecutionNode) {
        // This can happen if a method returns an unknown value of a type which implements an interface
        // Since the "real" value is unknown, the real class of the invocation target can't be determined.
        if (!method.hasImplementation()) {
            log.warn("Unable to execute method without implementation: {}, isNative={}", method, method.isNative)
            assumeMaximumUnknown(parent.state, calleeState, method)
            return
        }

        val graph = getExecutionGraph(method)
        val entrypointNode = graph.spawnEntrypointNode(parent = parent)
        val methodNodes = LinkedList<ExecutionNode>()
        methodNodes.add(entrypointNode)
        executionQueue.add(ExecutionQueueEntry(graph, methodNodes))
        resumeNodes.add(parent)
    }

    private fun assumeMaximumUnknown(callerState: ExecutionState, calleeState: ExecutionState, method: VirtualMethod) {
        // TODO: add option to mark all class states unknown instead of just method state
        // This is heavy handed in most cases, and maybe there's a way to optimize. It's hard to tell if the method we're
        // failing to execute modifies class state. If it did, we aren't capturing it here.
        val isInitializing = method.signature.contains(";-><init>(")
        val parameterRegisters = calleeState.registersAssigned.filter { it >= callerState.firstParameterRegister }
        for (i in method.parameterTypeNames.indices) {
            val register = parameterRegisters[i]
            var value = callerState.readRegister(register)
            if (value.isNull) {
                // Nulls are immutable
                continue
            }
            // TODO: after some testing, was this actually needed? doesn't make sense because calleeState should always have types
            // defined from analyzeParameterTypes.
            val type = value.type
//            var type: String
//            if (value.isUnknown) {
//                type = analyzedParameterTypes[i]
//                if (type != value.type) {
//                    val parameterType = classManager.getVirtualType(type)
//                    val argumentType = classManager.getVirtualType(value.type)
//                    if (parameterType.isAncestorOf(argumentType)) {
//                        type = value.type
//                    }
//                }
//            } else {
//                // If argument is known, use that type rather than relying on method signature.
//                // I.e. Parameter type might be "Ljava/lang/Object;" but actual type is "Ljava/lang/String";
//                type = value.type
//            }
            if (!isInitializing) {
                // Even if immutable type, internal state can change in the initializer.
                if (configuration.isImmutable(type)) {
                    log.trace("r{} was passed to unresolvable method execution, but is immutable type {}; not mutating.", register, type)
                    continue
                }
            }
            value = Value.unknown(type)
            log.debug("r{} was passed to unresolvable method execution and is mutable type {}; marking unknown.", register, type)
            callerState.pokeRegister(register, value)
        }
        if (isInitializing) {
            // TODO: If we're refusing to execute an <init> method, should create a new instance of at least the stub class or something
            // and update identities. That way we don't have weird Uninitialized instances floating around. Look at TestExceptionHandling
            // and how ExceptionalCode throws CustomException but Throwable isn't whitelisted.
        }
        if (!method.returnsVoid()) {
            val value = Value.unknown(method.returnType)
            callerState.assignResultRegister(value)
        }
    }

    private fun enqueueStaticInitializationIfNecessary(virtualClass: VirtualType, parent: ExecutionNode): Boolean {
        /*
         * Classed should be statically initialized when:
         * 1.) The invocation of a method declared by the class (not inherited from a superclass)
         * 2.) The invocation of a constructor of the class (covered by #1)
         * 3.) The use or assignment of a field declared by a class (not inherited from a superclass), except for fields
         * that are both static and final, and are initialized by a compile-time constant expression.
         */
        if (parent.state.isClassInitialized(virtualClass)) {
            return false
        }

        // Initialize class ancestors as well
        for (ancestor in virtualClass.ancestors) {
            // TODO: easy - shove these modifier methods as convenience methods into VirtualType
            val accessFlags = ancestor.classDef.accessFlags
            if (Modifier.isAbstract(accessFlags) || Modifier.isInterface(accessFlags) || Modifier.isNative(accessFlags)) {
                continue
            }
            enqueueStaticInitializationIfNecessary(ancestor, parent)
        }

        val method = virtualClass.getMethod("<clinit>()V")
        if (method == null) {
            // Class has no static initializer but still need to set any literal fields, e.g.
            // .field public static myInt:I = 0x4
            initializeClassState(parent.state, virtualClass)
            parent.state.setClassInitialized(virtualClass)
            return false
        }

        val calleeState = ExecutionState.build(method, classManager, classLoader, configuration)
        enqueueMethodInvocation(method, calleeState, parent)
        return true
    }


//    fun startDebug(calleeContext: ExecutionContext?, callerContext: ExecutionContext?): MethodExecutor {
//        TODO("Not yet implemented")
//    }
//
//    fun finishDebug(methodExecutor: MethodExecutor?, callerContext: ExecutionContext?, parameterRegisters: IntArray?): ExecutionGraphImpl {
//        TODO("Not yet implemented")
//    }


//    override fun getStaticFieldAccessor(): StaticFieldAccessor {
//        TODO("Not yet implemented")
//    }

    fun getExecutionGraph(className: String, methodDescriptor: String): ExecutionGraph2 {
        val method = classManager.getVirtualClass(className).getMethod(methodDescriptor)!!
        return getExecutionGraph(method)
    }

    fun getExecutionGraph(method: VirtualMethod): ExecutionGraph2 {
        if (!methodToGraph.containsKey(method)) {
            if (!methodToTemplateOps.containsKey(method)) {
                updateTemplateOps(method)
            }
            methodToGraph[method] = ExecutionGraph2(method, this)
        }
        return methodToGraph[method]!!
    }

    fun spawnEntrypointState(methodSignature: String): ExecutionState {
        val method = classManager.getMethod(methodSignature)
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
        for (field in virtualClass.fields) {
            val value = Value.wrap(field.initialValue, field.type)
            state.pokeField(field, value)
        }
    }

/*
public static MethodState forMethod(ExecutionContext context) {
    VirtualMethod method = context.getMethod();
    int registerCount = method.getRegisterCount();
    List<String> parameterTypes = method.getParameterTypeNames();
    int parameterSize = Utils.getRegisterSize(parameterTypes);
    MethodState mState = new MethodState(context, registerCount, parameterTypes.size(), parameterSize);
    int firstParameter = mState.getParameterStart();
    int parameterRegister = firstParameter;

    for (String type : parameterTypes) {
        HeapItem item;
        if (parameterRegister == firstParameter && !method.isStatic() && method.getName().equals("<init>")) {
            UninitializedInstance instance = new UninitializedInstance(method.getDefiningClass());
            item = new HeapItem(instance, type);
        } else {
            item = HeapItem.newUnknown(type);
        }
        mState.assignParameter(parameterRegister, item);
        parameterRegister += Utils.getRegisterSize(type);
    }

    return mState;
}

public static ClassState forClass(ExecutionContext context, VirtualType virtualClass) {
    ClassState cState = new ClassState(virtualClass, context);
    for (VirtualField field : virtualClass.getFields()) {
        Object value = field.getInitialValue();
        String type = field.getType();
        cState.pokeField(field, new HeapItem(value, type));
    }

    return cState;
}

 */

    private fun updateTemplateOps(method: VirtualMethod) {
        val opBuilder = OpBuilder(classManager, classLoader, configuration)
        val instructions = method.implementation.instructions
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
        val lineage: MutableList<VirtualClass> = LinkedList()
        lineage.add(exceptionClass)
        lineage.addAll(exceptionClass.ancestors)
        for (currentExceptionClass in lineage) {
            val className = currentExceptionClass.name
            for (tryBlock in method.tryBlocks) {
                val startAddress = tryBlock.startCodeAddress
                val endAddress = tryBlock.startCodeAddress + tryBlock.codeUnitCount
                val inTryBlock = address >= startAddress && address < endAddress
                if (!inTryBlock) {
                    continue
                }
                val handlers = tryBlock.exceptionHandlers
                if (catchAll) {
                    // If it's the last handler and it's null, it's a catchall
                    val handler = handlers[handlers.size - 1]
                    if (null == handler.exceptionType) {
                        return handler.handlerCodeAddress
                    }
                } else {
                    for (handler in handlers) {
                        val handlerType = handler.exceptionType
                        if (className == handlerType) {
                            return handler.handlerCodeAddress
                        }
                    }
                }
            }
            if (catchAll) {
                // This loop is unnecessary for catch-all because it doesn't look at the exception class.
                return -1
            }
        }
        return -1
    }

    object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            // TODO: remove this :D
            println("hey");
        }
    }

}
