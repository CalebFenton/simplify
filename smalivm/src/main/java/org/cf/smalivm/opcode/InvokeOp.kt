package org.cf.smalivm.opcode

import gnu.trove.list.TIntList
import gnu.trove.list.linked.TIntLinkedList
import org.cf.smalivm.*
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.context.ExecutionContext
import org.cf.smalivm.context.ExecutionGraph
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.emulate.MethodEmulator
import org.cf.smalivm.exception.UnhandledVirtualException
import org.cf.smalivm.exception.VirtualMachineException
import org.cf.smalivm.type.*
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.cf.util.ClassNameUtils
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.Instruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc
import org.jf.dexlib2.iface.reference.MethodReference
import org.jf.dexlib2.util.ReferenceUtil
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class InvokeOp internal constructor(
    location: MethodLocation,
    child: MethodLocation,
    val method: VirtualMethod,
    val parameterRegisters: IntArray,
    vm: VirtualMachine
) : Op(location, child) {
    val analyzedParameterTypes: Array<String?>
    private val vm: VirtualMachine
    private val classManager: ClassManager

    var isDebugMode: Boolean
    var debuggedMethodExecutor: MethodExecutor? = null
        private set
    private var debuggedCallerContext: ExecutionContext? = null
    private var debuggedCalleeContext: ExecutionContext? = null
    private var debuggedNode: ExecutionNode? = null

    init {
        analyzedParameterTypes = arrayOfNulls(method.parameterTypeNames.size)
        this.vm = vm
        classManager = vm.classManager
        // TODO: set these in the node!
//        sideEffectLevel = SideEffect.Level.STRONG
        isDebugMode = false
    }

    override val registersReadCount: Int
        get() = TODO("Not yet implemented")
    override val registersAssignedCount: Int
        get() = TODO("Not yet implemented")
    override lateinit var sideEffectLevel: SideEffect.Level

    override fun execute(node: ExecutionNode): Array<out OpChild> {
        // TODO: In order to get working call stacks, refactor this to delegate most of the work to MethodExecutor.
        // This will remove InvokeOp as a weirdly complex op, and probably allow some methods to be made protected.
        // It also keeps things clear with method execution delegated to the class with the same name.
        // MethodExecutor can maintain a mapping such that calleeContext -> (callerContext, caller address)
        // With this mapping, stack traces can be reconstructed.
        val callerMethodState = context.methodState
        if (method.signature == CommonTypes.OBJECT + "-><init>()V") {
            // Object.<init> is a special little snow flake
            try {
                executeLocalObjectInit(callerMethodState)
            } catch (e: ClassNotFoundException) {
                log.error("Unexpected real exception initializing Object", e)
            } catch (e: InstantiationException) {
                log.error("Unexpected real exception initializing Object", e)
            } catch (e: IllegalAccessException) {
                log.error("Unexpected real exception initializing Object", e)
            } catch (e: IllegalArgumentException) {
                log.error("Unexpected real exception initializing Object", e)
            } catch (e: InvocationTargetException) {
                log.error("Unexpected real exception initializing Object", e)
            }
            return
        } else {
            if (method.name == "clone" && parameterRegisters.size == 1) {
                val targetRegister = parameterRegisters[0]
                val item = context.methodState.peekRegister(targetRegister)
                val signature = method.signature
                if (signature[0] == '[' || !item!!.isNull && item.valueType[0] == '[') {
                    // [Object;->clone()Ljava/lang/Object; is also a special snow flake
                    executeArrayClone(callerMethodState, node)
                    return
                }
            }
        }
        analyzeParameterTypes(callerMethodState)

        // Have to do this at run time because robust type flow analysis is harder than just examining at run time
        var targetMethod: VirtualMethod? = method
        if (name.startsWith("invoke-virtual") && !method.isFinal) {
            val targetRegister = parameterRegisters[0]
            val item = context.methodState.peekRegister(targetRegister)
            targetMethod = resolveTargetMethod(item!!.value)
        }
        // Shouldn't reference method member now. Should use targetMethod for everything.
        val targetSignature = targetMethod!!.signature

        // Try to reflect or emulate before executing local method.
        if (vm.configuration.isSafe(targetSignature) || MethodEmulator.canEmulate(targetSignature)) {
            val calleeContext = buildNonLocalCalleeContext(context)
            val allArgumentsKnown = allArgumentsKnown(calleeContext.methodState)
            if (allArgumentsKnown || MethodEmulator.canHandleUnknownValues(targetSignature)) {
                executeNonLocalMethod(targetSignature, callerMethodState, calleeContext, node)
                return
            } else {
                if (log.isTraceEnabled) {
                    log.trace("Not emulating / reflecting {}; not all arguments are known", targetSignature)
                }
                assumeMaximumUnknown(callerMethodState)
                return
            }
        }
        if (classManager.isFrameworkClass(targetMethod.definingClass) &&
            !classManager.isSafeFrameworkClass(targetMethod.definingClass)
        ) {
            if (log.isDebugEnabled) {
                log.debug("Not executing unsafe framework method: {}. Assuming maximum ambiguity.", targetSignature)
            }
            assumeMaximumUnknown(callerMethodState)
            return
        }
        if (!targetMethod.hasImplementation()) {
            if (log.isWarnEnabled) {
                if (!targetMethod.isNative) {
                    // This can happen if a method returns an object which implements an interface
                    // but the object is unknown, so the real virtual of the invocation target
                    // can't be determined. That's why this is a warning and not an error.
                    log.warn(
                        "Attempting to execute local native method without implementation: {}. Assuming maximum " +
                                "ambiguity.", targetSignature
                    )
                } else {
                    log.warn("Cannot execute local native method: {}. Assuming maximum ambiguity.", targetSignature)
                }
            }
            assumeMaximumUnknown(callerMethodState)
            return
        }
        val calleeContext = buildLocalCalleeContext(context, targetMethod)
        executeLocalMethod(calleeContext, context, node)
    }

    val returnType: String
        get() = method.returnType

    override fun toString(): String {
        val sb = StringBuilder(name)
        sb.append(" {")
        if (name.contains("/range")) {
            sb.append('r').append(parameterRegisters[0]).append(" .. r")
                .append(parameterRegisters[parameterRegisters.size - 1])
        } else {
            if (parameterRegisters.size > 0) {
                for (register in parameterRegisters) {
                    sb.append('r').append(register).append(", ")
                }
                sb.setLength(sb.length - 2)
            }
        }
        sb.append("}, ").append(method)
        return sb.toString()
    }

    private fun executeArrayClone(callerMethodState: MethodState, node: ExecutionNode) {
        val instanceRegister = parameterRegisters[0]
        val arrayItem = callerMethodState.peekRegister(instanceRegister)
        if (arrayItem!!.isUnknown) {
            callerMethodState.assignResultRegister(UnknownValue(), arrayItem.type)
        } else if (arrayItem.isNull) {
            // This operation would have thrown a null pointer exception, and nothing else.
            val exception: Throwable = NullPointerException()
            addException(exception)
            node.clearChildren()
        } else {
            var m: Method? = null
            try {
                m = Any::class.java.getDeclaredMethod("clone")
                m.isAccessible = true
                val clone = m.invoke(arrayItem.value)
                callerMethodState.assignResultRegister(clone, arrayItem.type)
            } catch (e: Exception) {
                // TODO: should handle exceptions here and bubble them up
                e.printStackTrace()
            }
        }
    }

    private fun allArgumentsKnown(mState: MethodState): Boolean {
        var parameterRegister = mState.parameterStart
        while (parameterRegister < mState.registerCount) {
            val item = mState.peekParameter(parameterRegister)
            if (item.isUnknown) {
                return false
            }
            val type = item.type
            parameterRegister += Utils.getRegisterSize(type)
        }
        return true
    }

    private fun analyzeParameterTypes(callerState: MethodState) {
        /*
         * Type can be confused here. For example, creating a short, int, boolean, or *null* all appear:
         * const/4 v0, 0x0 (could be (bool)true, (int)0, or (short)0, null, etc.)
         * Use type in method signature if it's more restrictive. For example, if the method argument is
         * an int but the method signature declares it as a boolean, use boolean.
         * However, if the class is less specific, such as a super class or interface, do not use the less specific
         * class. For example:
         * method argument is Lchild_class; but signature says Lparent_class;, prefer Lchild_class;
         */
        val parameterTypes = method.parameterTypeNames
        for (i in parameterRegisters.indices) {
            val callerRegister = parameterRegisters[i]
            val item = callerState.readRegister(callerRegister)
            val parameterTypeName = parameterTypes[i]
            val baseTypeName = item.componentBase
            var type: String?
            type = if (item.isPrimitive || ClassNameUtils.isPrimitive(baseTypeName)) {
                parameterTypeName
            } else {
                val baseType = vm.classManager.getVirtualType(baseTypeName)
                val parameterType = vm.classManager.getVirtualType(parameterTypeName)
                if (baseType.ancestors.contains(parameterType)) {
                    item.type
                } else {
                    parameterTypeName
                }
            }
            analyzedParameterTypes[i] = type
        }
    }

    private fun assignCalleeMethodArguments(callerState: MethodState, calleeState: MethodState) {
        var parameterRegister = calleeState.parameterStart
        for (i in parameterRegisters.indices) {
            val callerRegister = parameterRegisters[i]
            val item = callerState.readRegister(callerRegister)
            val parameterType = analyzedParameterTypes[i]
            var value = item.value
            if (item.isPrimitive && !item.isUnknown) {
                val hasNullByteValue = item.type == "I" && value is Number && item.asInteger() == 0
                value = if (hasNullByteValue && ClassNameUtils.isObject(parameterType)) {
                    null
                } else {
                    // The "I" type may actually be "S", "B", "C", etc. Cast to the given parameter type.
                    Utils.castToPrimitive(value, parameterType)
                }
            }
            val parameterItem = HeapItem(value, parameterType)
            calleeState.assignParameter(parameterRegister, parameterItem)
            parameterRegister += Utils.getRegisterSize(parameterType)
        }
    }

    private fun assumeMaximumUnknown(callerMethodState: MethodState) {
        // TODO: add option to mark all class states unknown instead of just method state
        // This is heavy handed in most cases, but it's impossible to tell if the method we're
        // failing to execute modifies class state. If it did, we aren't capturing it here.
        val isInitializing = method.signature.contains(";-><init>(")
        for (i in method.parameterTypeNames.indices) {
            val register = parameterRegisters[i]
            var item = callerMethodState.readRegister(register)
            val value = item.value
                ?: // Null references can't mutate.
                continue
            var type: String?
            if (item.isUnknown) {
                type = analyzedParameterTypes[i]
                if (type != item.type) {
                    val parameterType = vm.classManager.getVirtualType(type)
                    val argumentType = vm.classManager.getVirtualType(item.type)
                    if (parameterType.isAncestorOf(argumentType)) {
                        type = item.type
                    }
                }
            } else {
                // If argument is known, use that type rather than relying on method signature.
                // Parameter type might be "Ljava/lang/Object;" but actual type is "Ljava/lang/String";
                type = item.type
            }
            if (!isInitializing) {
                // Even if immutable type, internal state can change in the initializer.
                if (vm.configuration.isImmutable(type)) {
                    if (log.isTraceEnabled) {
                        log.trace("{} is immutable", type)
                    }
                    continue
                }
            }
            item = HeapItem.newUnknown(type)
            if (log.isDebugEnabled) {
                log.debug("{} is mutable and passed into unresolvable method execution, making Unknown", type)
            }
            callerMethodState.pokeRegister(register, item)
        }
        if (isInitializing) {
            // TODO: If we're refusing to execute an <init> method, should create a new instance of at least the stub class or something
            // and update identities. That way we don't have weird Uninitialized instances floating around. Look at TestExceptionHandling
            // and how ExceptionalCode throws CustomException but Throwable isn't whitelisted.
        }
        if (!method.returnsVoid()) {
            val item = HeapItem.newUnknown(method.returnType)
            callerMethodState.assignResultRegister(item)
        }
    }

    private fun buildLocalCalleeContext(callerContext: ExecutionContext, method: VirtualMethod?): ExecutionContext {
        val calleeContext = vm.spawnRootContext(method, callerContext, address)
        val callerMethodState = callerContext.methodState
        val calleeMethodState = calleeContext.methodState
        assignCalleeMethodArguments(callerMethodState, calleeMethodState)

        // VirtualClass state merging is handled by the VM.
        return calleeContext
    }

    private fun buildNonLocalCalleeContext(callerContext: ExecutionContext): ExecutionContext {
        val calleeContext = ExecutionContext(vm, method)
        val parameterSize = method.parameterSize
        val calleeMethodState = MethodState(calleeContext, parameterSize, method.parameterTypeNames.size, parameterSize)
        assignCalleeMethodArguments(callerContext.methodState, calleeMethodState)
        calleeContext.methodState = calleeMethodState
        calleeContext.registerCaller(callerContext, address)
        return calleeContext
    }

    private fun finishLocalMethodExecution(
        calleeContext: ExecutionContext?,
        callerContext: ExecutionContext?,
        node: ExecutionNode?,
        graph: ExecutionGraph?
    ) {
        if (graph == null) {
            // Maybe node visits or call depth exceeded?
            log.info("Problem executing {}, propagating ambiguity.", calleeContext!!.method)
            assumeMaximumUnknown(callerContext!!.methodState)
            return
        }
        var hasOneNonThrow = false
        for (endAddress in graph.connectedTerminatingAddresses) {
            val endOp = graph.getTemplateNode(endAddress)!!.op
            if (endOp is ThrowOp) {
                // At least one execution path leads to throwing an exception.
                val items = graph.getRegisterItems(endAddress, MethodState.ThrowRegister)
                for (item in items) {
                    if (item.value is Throwable) {
                        val exception = item.value as Throwable?
                        addException(exception!!)
                    } else {
                        // Possibly UninitializedInstance
                        if (log.isWarnEnabled) {
                            if (item.isUnknown) {
                                log.warn(
                                    "Method had possible execution path which throws an exception but cannot instantiate it because the value is unknown. Exception item: {}",
                                    item
                                )
                            } else {
                                // May just need to whitelist Exception class
                                log.warn("Refusing to instantiate potentially unsafe thrown exception: {}.", item)
                            }
                        }
                    }
                }
            } else {
                hasOneNonThrow = true
            }
        }
        if (!hasOneNonThrow) {
            // Everything was an exception; don't go execute any normal children
            node.clearChildren()
        } else {
            if (method.returnType != CommonTypes.VOID) {
                // Terminating addresses may include throw ops which may not have a return register set
                val returnOpAddresses: TIntList = TIntLinkedList()
                for (address in graph.connectedTerminatingAddresses) {
                    if (graph.getOp(address) is ReturnOp) {
                        returnOpAddresses.add(address)
                    }
                }
                val consensus = graph.getRegisterConsensus(returnOpAddresses.toArray(), MethodState.ReturnRegister)
                callerContext!!.methodState.assignResultRegister(consensus)
            } else {
                if (calleeContext!!.method.descriptor.startsWith("<init>(")) {
                    // This was a call to a local parent <init> method
                    val calleeInstanceRegister = calleeContext.methodState.parameterStart
                    val newInstance = graph.getTerminatingRegisterConsensus(calleeInstanceRegister)
                    val instanceRegister = parameterRegisters[0]
                    callerContext!!.methodState.assignRegisterAndUpdateIdentities(instanceRegister, newInstance)
                }
            }
        }
        sideEffectLevel = graph.highestSideEffectLevel
    }

    private fun executeLocalMethod(calleeContext: ExecutionContext, callerContext: ExecutionContext, node: ExecutionNode) {
        if (isDebugMode) {
            startDebugLocalMethod(calleeContext, callerContext, node)
        } else {
            var graph: ExecutionGraph? = null
            try {
                graph = vm.execute(calleeContext, callerContext, parameterRegisters)
            } catch (e: VirtualMachineException) {
                log.warn(e.toString())
                if (e is UnhandledVirtualException) {
                    // An exception was thrown but there was no exception handler to catch it.
                    // It's not clear if it's smalivm's fault or the app's code.
                    // TODO: bubble this up to the calling method
                }
            }
            finishLocalMethodExecution(calleeContext, callerContext, node, graph)
        }
    }

    fun startDebugLocalMethod(calleeContext: ExecutionContext?, callerContext: ExecutionContext?, node: ExecutionNode?) {
        debuggedMethodExecutor = vm.startDebug(calleeContext, callerContext)
        debuggedCalleeContext = calleeContext
        debuggedCallerContext = callerContext
        debuggedNode = node
    }

    fun finishDebugLocalMethod(graph: ExecutionGraph?) {
        finishLocalMethodExecution(debuggedCalleeContext, debuggedCallerContext, debuggedNode, graph)
    }

    @Throws(
        ClassNotFoundException::class,
        InstantiationException::class,
        IllegalAccessException::class,
        IllegalArgumentException::class,
        InvocationTargetException::class
    )
    private fun executeLocalObjectInit(callerMethodState: MethodState) {
        val instanceRegister = parameterRegisters[0]
        val instanceItem = callerMethodState.peekRegister(instanceRegister)
        val uninitializedInstance = instanceItem!!.value as UninitializedInstance?
        val instanceType = uninitializedInstance!!.type

        // Create a Java class of the true type
        val klazz = vm.classLoader.loadClass(instanceType.binaryName)
        val newInstance = ObjectInstantiator.newInstance(klazz)
        val newInstanceItem = HeapItem(newInstance, instanceType.name)
        callerMethodState.assignRegisterAndUpdateIdentities(instanceRegister, newInstanceItem)
    }

    private fun executeNonLocalMethod(
        methodDescriptor: String,
        callerMethodState: MethodState,
        calleeContext: ExecutionContext,
        node: ExecutionNode
    ) {
        if (MethodEmulator.canEmulate(methodDescriptor)) {
            val emulator = MethodEmulator(vm, calleeContext, methodDescriptor)
            emulator.emulate(this)
            sideEffectLevel = emulator.sideEffectLevel
            if (emulator.exceptions.size > 0) {
                node.clearChildren()
                node.setExceptions(emulator.exceptions)
                return
            }
        } else if (vm.configuration.isSafe(methodDescriptor)) {
            val reflector = MethodReflector(vm, method)
            try {
                reflector.reflect(calleeContext.methodState) // playa play
            } catch (e: Exception) {
                node.addException(e)
                node.clearChildren()
                return
            }

            // Only safe, non-side-effect methods are allowed to be reflected.
            sideEffectLevel = SideEffect.Level.NONE
        }
        if (!method.isStatic) {
            // This is virtual and the instance parse may have been initialized or mutated.
            val originalInstanceItem = callerMethodState.peekRegister(parameterRegisters[0])
            val newInstanceItem = calleeContext.methodState.peekParameter(0)
            if (originalInstanceItem!!.value !== newInstanceItem.value) {
                // Instance has been initialized, i.e. was UninitializedInstance
                // Use assignRegisterAndUpdateIdentities because multiple registers may have an identical
                // UninitializedInstance, and those need to be updated with the new instance.
                callerMethodState.assignRegisterAndUpdateIdentities(parameterRegisters[0], newInstanceItem)
            } else {
                val isMutable = !vm.configuration.isImmutable(newInstanceItem.type)
                if (isMutable) {
                    // The instance class is mutable so could have changed. Record that it was changed for the
                    // optimizer.
                    callerMethodState.assignRegister(parameterRegisters[0], newInstanceItem)
                }
            }
        }
        if (!method.returnsVoid()) {
            val returnItem = calleeContext.methodState.readReturnRegister()
            callerMethodState.assignResultRegister(returnItem)
        }
    }

    private fun resolveTargetMethod(virtualReference: Any?): VirtualMethod? {
        /*
         * A method may not be defined in the class referenced by invoke op. The method implementation may be part
         * of the super class. This method searches ancestor hierarchy for the class which implements the method.
         */
        val referenceType: VirtualType
        if (virtualReference == null || virtualReference is UnknownValue) {
            return method
        }
        referenceType = if (virtualReference is UninitializedInstance) {
            virtualReference.type
        } else {
            val targetType = ClassNameUtils.toInternal(virtualReference.javaClass)
            classManager.getVirtualType(targetType)
        }
        val targetMethod = referenceType.getMethod(method.descriptor)
        return if (targetMethod != null && targetMethod.hasImplementation()) {
            targetMethod
        } else method
    }

    companion object {
        private val log = LoggerFactory.getLogger(InvokeOp::class.java.simpleName)
        private fun buildParameterRegisters(parameterTypes: List<String>, registers: IntArray): IntArray {
            val parameterRegisters: TIntList = TIntLinkedList(parameterTypes.size)
            var index = 0
            for (parameterType in parameterTypes) {
                parameterRegisters.add(registers[index])
                index += Utils.getRegisterSize(parameterType)
            }
            return parameterRegisters.toArray()
        }

        private fun buildRegisters(instr: Instruction?): IntArray {
            return if (instr is Instruction3rc) {
                buildRegisters3rc(instr)
            } else {
                buildRegisters35c(instr as Instruction35c?)
            }
        }

        private fun buildRegisters35c(instruction: Instruction35c?): IntArray {
            val registerCount = instruction!!.registerCount
            val registers = IntArray(registerCount)
            when (registerCount) {
                5 -> {
                    registers[4] = instruction.registerG
                    registers[3] = instruction.registerF
                    registers[2] = instruction.registerE
                    registers[1] = instruction.registerD
                    registers[0] = instruction.registerC
                }
                4 -> {
                    registers[3] = instruction.registerF
                    registers[2] = instruction.registerE
                    registers[1] = instruction.registerD
                    registers[0] = instruction.registerC
                }
                3 -> {
                    registers[2] = instruction.registerE
                    registers[1] = instruction.registerD
                    registers[0] = instruction.registerC
                }
                2 -> {
                    registers[1] = instruction.registerD
                    registers[0] = instruction.registerC
                }
                1 -> registers[0] = instruction.registerC
            }
            return registers
        }

        private fun buildRegisters3rc(instruction: Instruction3rc): IntArray {
            val registerCount = instruction.registerCount
            val start = instruction.startRegister
            val end = start + registerCount
            val registers = IntArray(registerCount)
            for (i in start until end) {
                registers[i - start] = i
            }
            return registers
        }

        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instruction = location.instruction as ReferenceInstruction
            val methodReference = instruction.reference as MethodReference
            val registers = buildRegisters(location.instruction)
            val className = methodReference.definingClass
            val type = classManager.getVirtualType(className)
            val methodSignature = ReferenceUtil.getMethodDescriptor(methodReference)
            val methodDescriptor = methodSignature.split("->").toTypedArray()[1]
            val method = type.getMethod(methodDescriptor) ?: throw RuntimeException("Method doesn't exist: $methodSignature")
            val parameterRegisters = buildParameterRegisters(method.parameterTypeNames, registers)
            return InvokeOp(location, child, method, parameterRegisters)
        }
    }
}
