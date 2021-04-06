package org.cf.smalivm2

import org.cf.smalivm.MethodReflector
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.dex.SmaliParser
import org.cf.smalivm.emulate.MethodEmulator
import org.cf.smalivm.opcode.Op
import org.cf.smalivm.opcode.OpBuilder
import org.cf.smalivm.type.*
import org.cf.util.EnumAnalyzer
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.reflect.Modifier
import java.util.*
import kotlin.collections.HashMap


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

    private val executionQueue: Deque<Pair<ExecutionGraph2, LinkedList<ExecutionNode>>> = LinkedList()
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
        val graph = getExecutionGraph(method)
        val entrypointState = state ?: spawnRootState(method)
        val root = graph.spawnEntrypointNode(parent, entrypointState)
        val methodNodes = LinkedList<ExecutionNode>()
        methodNodes.add(root)

        /*
        TODO: when you first execute a method,
         */
        executionQueue.add(Pair(graph, methodNodes))

        while (!this.interactive && executionQueue.isNotEmpty()) {
            step()
        }

        return graph
    }

//    fun execute(calleeContext: ExecutionContext?, callerContext: ExecutionContext?, parameterRegisters: IntArray?): ExecutionGraphImpl {
//        TODO("Not yet implemented")
//    }

//    private fun callMethod(methodSignature: String, graph: ExecutionGraph?) {
//
//    }
//
//    private fun initializeClassesIfNecessary(callerNode: org.cf.smalivm2.ClassReferencingNode, callerGraph: org.cf.smalivm2.ExecutionGraph?) {
//        for (classReference in callerNode.getClassSignaturesToLoad()) {
//            if (callerNode.isClassInitialized(classReference)) {
//                continue
//            }
//            val initSignature = "$classReference-><clinit>()V"
//            val calleeGraph = if (interactive) null else org.cf.smalivm2.ExecutionGraph()
//            val calleeNodes = Stack<org.cf.smalivm2.ExecutionNode>()
//            val calleeEntryNode = EntrypointNode(initSignature)
//            calleeNodes.push(calleeEntryNode)
//            execute(calleeNodes, calleeGraph)
//
//            if (interactive) {
//                /*
//                set caller node children to include calleeNode
//                set
//                 */
//            } else {
//
//                // merge consensus of calleeGraph with current state
//                // delete calleeGraph
//                // make sure no references to these nodes exist!
//                // set calleeEntryNode parent to null
//                // reset node children and
//
//            }
//        }
//    }

    fun step() {
        val currentPair = executionQueue.peek()
        val graph = currentPair.first
        val nodes = currentPair.second
        val current = nodes.poll()

        if (current.isEntrypoint) {
            if (!current.state.isClassInitialized(current.method.className) && current.method.descriptor != "<clinit>()V") {
//                val clinit = classManager.getMethod("${current.method.className}-><clinit>()V")
                staticallyInitializeClassIfNecessary(current.method.definingClass, current.state)
                return
            }
        }

        graph.addNode(current)
        val unresolvedChildren = if (resumeNodes.contains(current)) {
            resumeNodes.remove(current)
            current.resume()
        } else {
            current.execute()
        }

        for (unresolvedChild in unresolvedChildren) {
            when (unresolvedChild) {
                is UnresolvedMethodInvocationChild -> {
                    handleMethodExecution(unresolvedChild.method, unresolvedChild.state, unresolvedChild.analyzedParameterTypes, current)
                }
                is UnresolvedAddressChild -> {
                    val location = graph.getLocation(unresolvedChild.address)!!
                    val op = methodToTemplateOps[current.method]!![location]!!
                    val child = ExecutionNode(op, current.method, classManager, classLoader, configuration, parent = current)
                    nodes.add(child)
                }
                is UnresolvedExceptionChild -> {
                    // TODO: Hard - Should anything be done with the exception message here? Maybe it'll be more obvious elsewhere.
                    // TODO: Hard - exception resolution probably doesn't work in try/catch blocks outside current method!
                    // TODO: Medium - concept of entrypoint isn't sufficient. Shouldn't reach back beyond thrown exceptions when traversing state ancestory
                    val exceptionClass = classManager.getVirtualClass(unresolvedChild.exceptionClass)
                    val handlerAddress = findExceptionHandlerAddress(exceptionClass, current.address, current.method)
                    if (handlerAddress < 0) {
                        throw RuntimeException("Unhandled real exception $unresolvedChild thrown executing $current. Possible bug!")
                    } else {
                        log.warn("{} threw exception {} but was caught by exception handler", current, unresolvedChild)
                    }
                    val location = graph.getLocation(handlerAddress)!!
                    val op = methodToTemplateOps[current.method]!![location]!!
                    val child = ExecutionNode(op, current.method, classManager, classLoader, configuration, parent = current)
                    nodes.add(child)
                }
            }
        }

        if (nodes.isEmpty()) {
            executionQueue.remove(currentPair)
            val caller = current.caller
            if (caller != null) {
                val callerPair = executionQueue.first { it.first.hasNode(caller) }
                // TODO: collapse the multiverse, get graph consensus
                val resumeNode = ExecutionNode(caller.op, current.method, classManager, classLoader, configuration, parent = current)
                val a = callerPair.second.add(resumeNode)

                // graph.hasNode -- where is the caller graph
                // find the methodNodes for that graph and add the caller node back
                // need to know if it should be resumed! add mapping in the VM?x
            }
        }
    }

    private fun handleMethodExecution(method: VirtualMethod, calleeState: ExecutionState, analyzedParameterTypes: Array<String>, parent: ExecutionNode) {
        val graph = getExecutionGraph(method)
        val entryNode = graph.spawnEntrypointNode(parent = parent)
        val methodNodes = LinkedList<ExecutionNode>()
        methodNodes.add(entryNode)
        executionQueue.add(Pair(graph, methodNodes))
        resumeNodes.add(parent)

        val isSafe = configuration.isSafe(method)
        val canEmulate = MethodEmulator.canEmulate(method)
        if (isSafe || canEmulate) {
            val allArgumentsKnown = calleeState.allArgumentsKnown()
            val child: UnresolvedChild? = if (canEmulate && (allArgumentsKnown || MethodEmulator.canHandleUnknownValues(method))) {
                MethodEmulator.emulate(method, calleeState, parent, this)
            } else if (isSafe && allArgumentsKnown) {
                MethodReflector.reflect(method, calleeState, parent, enumAnalyzer)
            } else {
                log.trace("Not emulating / reflecting {}; all arguments are not known. Assuming maximum ambiguity.", method)
                // TODO: Is this calleeState? Should rename. Caller state gets modified for unknowns, should write function to collapse / merge
                assumeMaximumUnknown(parent.state, method, analyzedParameterTypes)
                null
            }

            if (child != null) {
                // Could be a nop or an exception
            }
        } else {
            if (classManager.isFrameworkClass(method.definingClass) && !classManager.isSafeFrameworkClass(method.definingClass)) {
                log.debug("Not executing unsafe framework method: {}. Assuming maximum ambiguity.", method)
                assumeMaximumUnknown(calleeState)
                return
            }

            // This can happen if a method returns an object which implements an interface
            // but the object is unknown, so the real class of the invocation target
            // can't be determined. That's why this is a warning and not an error.
            if (!method.hasImplementation()) {
                log.warn("Unable to execute method without implementation: {}, isNative={}", method, method.isNative)
                assumeMaximumUnknown(calleeState)
                return
            }
            val calleeContext = buildLocalCalleeContext(context, targetMethod)
            executeLocalMethod(calleeContext, context, node)
            return finishOp()

        }
    }

    private fun assumeMaximumUnknown(callerState: ExecutionState, method: VirtualMethod, analyzedParameterTypes: Array<String>) {
        // TODO: add option to mark all class states unknown instead of just method state
        // This is heavy handed in most cases, and maybe there's a way to optimize. It's hard to tell if the method we're
        // failing to execute modifies class state. If it did, we aren't capturing it here.
        val isInitializing = method.signature.contains(";-><init>(")
        val parameterRegisters = callerState.registersAssigned.filter { it >= callerState.firstParameterRegister }
        for (i in method.parameterTypeNames.indices) {
            val register = parameterRegisters[i]
            var value = callerState.readRegister(register)
            if (value.isNull) {
                // Nulls are immutable
                continue
            }
            var type: String
            if (value.isUnknown) {
                type = analyzedParameterTypes[i]
                if (type != value.type) {
                    val parameterType = classManager.getVirtualType(type)
                    val argumentType = classManager.getVirtualType(value.type)
                    if (parameterType.isAncestorOf(argumentType)) {
                        type = value.type
                    }
                }
            } else {
                // If argument is known, use that type rather than relying on method signature.
                // I.e. Parameter type might be "Ljava/lang/Object;" but actual type is "Ljava/lang/String";
                type = value.type
            }
            if (!isInitializing) {
                // Even if immutable type, internal state can change in the initializer.
                if (configuration.isImmutable(type)) {
                    log.trace("r{} was passed to unresolvable method execution, but is immutable type {} so not mutating", register, type)
                    continue
                }
            }
            value = Value.unknown(type)
            log.debug("r{} was passed to unresolvable method execution and is mutable type {}, marking Unknown", register, type)
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

    fun staticallyInitializeClassIfNecessary(virtualClass: VirtualType, parent: ExecutionNode) {
        /*
         * Classed should be statically initialized when:
         * 1.) The invocation of a method declared by the class (not inherited from a superclass)
         * 2.) The invocation of a constructor of the class (covered by #1)
         * 3.) The use or assignment of a field declared by a class (not inherited from a superclass), except for fields
         * that are both static and final, and are initialized by a compile-time constant expression.
         */
        if (parent.state.isClassInitialized(virtualClass)) {
            return
        }
        if (configuration.isSafe(virtualClass)) {
            // Safe classes will be reflected so there's no reason to init on the VM.
            parent.state.setClassInitialized(virtualClass)
            return
        }

        // Initialize class ancestors as well
        for (ancestor in virtualClass.ancestors) {
            // TODO: easy - shove these modifier methods as convenience methods into VirtualType
            val accessFlags = ancestor.classDef.accessFlags
            if (Modifier.isAbstract(accessFlags) || Modifier.isInterface(accessFlags) || Modifier.isNative(accessFlags)) {
                continue
            }
            staticallyInitializeClassIfNecessary(ancestor, parent)
        }
        val method = virtualClass.getMethod("<clinit>()V")
        if (method == null) {
            /*
             * Class has no static initializer but still need to set any literal fields, e.g.
             * .field public static myInt:I = 0x4
             */
            initializeClassState(parent.state, virtualClass)
            return
        }

        val state = ExecutionState.build(method, classManager, classLoader, configuration)
        handleMethodExecution(method, state, arrayOf(), parent)
    }

    private fun pushNodeToExecutionQueue(node: ExecutionNode) {
        executionQueue.peek().second.add(node)
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

    fun spawnRootState(methodSignature: String): ExecutionState {
        val method = classManager.getMethod(methodSignature)
        return spawnRootState(method)
    }

    fun spawnRootState(className: String, methodDescriptor: String): ExecutionState {
        val klazz = classManager.getVirtualClass(className)
        val method = klazz.getMethod(methodDescriptor) ?: throw RuntimeException("Method signature not found: $className->$methodDescriptor")
        return spawnRootState(method)
    }

    fun spawnRootState(method: VirtualMethod): ExecutionState {
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
