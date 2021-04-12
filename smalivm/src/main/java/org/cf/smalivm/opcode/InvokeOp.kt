package org.cf.smalivm.opcode

import org.cf.smalivm.ObjectInstantiator
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.UninitializedInstance
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.Value
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
import java.util.*

class InvokeOp internal constructor(
    location: MethodLocation,
    val method: VirtualMethod,
    val parameterRegisters: IntArray,
) : Op(location) {

//    var isDebugMode: Boolean
//    var debuggedMethodExecutor: MethodExecutor? = null
//        private set
//    private var debuggedCallerContext: ExecutionContext? = null
//    private var debuggedCalleeContext: ExecutionContext? = null
//    private var debuggedNode: ExecutionNode? = null

//    init {
//        analyzedParameterTypes = arrayOfNulls(method.parameterTypeNames.size)
//        // TODO: set these in the node!
////        sideEffectLevel = SideEffect.Level.STRONG
//        isDebugMode = false
//    }

    override val registersReadCount = parameterRegisters.size
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        if (method.signature == CommonTypes.OBJECT + "-><init>()V") {
            // Object.<init> is a special little snow flake
            return executeLocalObjectInit(node)
        }

        if (method.name == "clone" && parameterRegisters.size == 1 && method.signature[0] == '[') {
            val targetRegister = parameterRegisters[0]
            val item = node.state.peekRegister(targetRegister)!!
            if (item.isNotNull && item.valueType[0] == '[') {
                // [Object;->clone()Ljava/lang/Object; is also a special snow flake
                return executeObjectArrayClone(node)
            }
        }

        // Must do this at run time to take advantage of extra type information
        val targetMethod = if (name.startsWith("invoke-virtual") && !method.isFinal) {
            val targetRegister = parameterRegisters[0]
            val target = node.state.peekRegister(targetRegister)!!
            resolveTargetMethod(target.value, node.classManager)
        } else {
            method
        }

        val analyzedParameterTypes = analyzeParameterTypes(node)
        val calleeState = ExecutionState.build(targetMethod, node.classManager, node.classLoader, node.configuration)
        assignCalleeMethodArguments(node.state, calleeState, analyzedParameterTypes)
        return callMethod(targetMethod, calleeState, analyzedParameterTypes)
    }

    val returnType: String
        get() = method.returnType

    override fun toString(): String {
        val sb = StringBuilder(name)
        sb.append(" {")
        if (name.contains("/range")) {
            sb.append('r').append(parameterRegisters[0])
                .append(" .. r").append(parameterRegisters[parameterRegisters.size - 1])
        } else {
            if (parameterRegisters.isNotEmpty()) {
                for (register in parameterRegisters) {
                    sb.append('r').append(register).append(", ")
                }
                sb.setLength(sb.length - 2)
            }
        }
        sb.append("}, ").append(method)
        return sb.toString()
    }

    private fun executeObjectArrayClone(node: ExecutionNode): Array<out UnresolvedChild> {
        val instanceRegister = parameterRegisters[0]
        val arrayItem = node.state.peekRegister(instanceRegister)!!
        if (arrayItem.isNull) {
            // This operation would have thrown a null pointer exception, and nothing else.
            return throwException(NullPointerException::class.java)
        }
        val clone = when {
            arrayItem.isUnknown -> UnknownValue()
            else -> {
                try {
                    val m = Any::class.java.getDeclaredMethod("clone")
                    m.isAccessible = true
                    m.invoke(arrayItem.value)
                } catch (e: Exception) {
                    log.error("Real exception initializing Object; returning unknown", e)
                    UnknownValue()
                }
            }
        }
        node.state.assignResultRegister(clone, arrayItem.type)
        return finishOp()
    }

    private fun analyzeParameterTypes(node: ExecutionNode): Array<String> {
        /*
         * Type can be confused here. For example, creating a short, int, boolean, or *null* all appear as:
         * const/4 v0, 0x0
         * Use type in method signature if it's more restrictive. For example, if the method argument is
         * an int but the method signature declares it as a boolean, use boolean.
         * However, if the class is less specific, such as a super class or interface, do not use the less specific
         * class. For example:
         * method argument is Lchild_class; but signature says Lparent_class;, prefer Lchild_class;
         */
        val parameterTypes = method.parameterTypeNames
        val analyzedParameterTypes = Array(parameterTypes.size) { parameterTypes[it] }
        for (i in parameterRegisters.indices) {
            val argument = node.state.readRegister(parameterRegisters[i])
            val parameterTypeName = parameterTypes[i]
            val baseTypeName = argument.componentBase
            val type = if (argument.isPrimitive || ClassNameUtils.isPrimitive(baseTypeName)) {
                parameterTypeName
            } else {
                val baseType = node.classManager.getVirtualType(baseTypeName)
                val parameterType = node.classManager.getVirtualType(parameterTypeName)
                when {
                    baseType.ancestors.contains(parameterType) -> argument.type
                    else -> parameterTypeName
                }
            }
            analyzedParameterTypes[i] = type
        }

        return analyzedParameterTypes
    }

    private fun assignCalleeMethodArguments(callerState: ExecutionState, calleeState: ExecutionState, analyzedParameterTypes: Array<String>) {
        var parameterRegister = calleeState.firstParameterRegister
        for (i in parameterRegisters.indices) {
            val callerRegister = parameterRegisters[i]
            val item = callerState.readRegister(callerRegister)
            val parameterType = analyzedParameterTypes[i]
            var value = item.value
            if (item.isPrimitive && !item.isUnknown) {
                val hasNullByteValue = item.type == CommonTypes.INTEGER && value is Number && item.toInteger() == 0
                value = if (hasNullByteValue && ClassNameUtils.isObject(parameterType)) {
                    null
                } else {
                    // The "I" type may actually be "S", "B", "C", "Z", etc. Cast to the given parameter type.
                    Utils.castToPrimitive(value, parameterType)
                }
            }
            val parameterItem = Value.wrap(value, parameterType)
            calleeState.assignRegister(parameterRegister, parameterItem)
            parameterRegister += Utils.getRegisterSize(parameterType)
        }
    }

//    private fun buildLocalCalleeContext(callerContext: ExecutionContext, method: VirtualMethod?): ExecutionContext {
//        val calleeContext = vm.spawnRootContext(method, callerContext, address)
//        val callerMethodState = callerContext.methodState
//        val calleeMethodState = calleeContext.methodState
//        assignCalleeMethodArguments(callerMethodState, calleeMethodState)
//
//        // VirtualClass state merging is handled by the VM.
//        return calleeContext
//    }
//
//    private fun buildNonLocalCalleeContext(callerContext: ExecutionContext): ExecutionContext {
//        val calleeContext = ExecutionContext(vm, method)
//        val parameterSize = method.parameterSize
//        val calleeMethodState = MethodState(calleeContext, parameterSize, method.parameterTypeNames.size, parameterSize)
//        assignCalleeMethodArguments(callerContext.methodState, calleeMethodState)
//        calleeContext.methodState = calleeMethodState
//        calleeContext.registerCaller(callerContext, address)
//        return calleeContext
//    }

//    private fun finishLocalMethodExecution(
//        calleeContext: ExecutionContext?,
//        callerContext: ExecutionContext?,
//        node: ExecutionNode?,
//        graph: ExecutionGraph?
//    ) {
//        if (graph == null) {
//            // Maybe node visits or call depth exceeded?
//            log.info("Problem executing {}, propagating ambiguity.", calleeContext!!.method)
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
//                        if (log.isWarnEnabled) {
//                            if (item.isUnknown) {
//                                log.warn(
//                                    "Method had possible execution path which throws an exception but cannot instantiate it because the value is unknown. Exception item: {}",
//                                    item
//                                )
//                            } else {
//                                // May just need to whitelist Exception class
//                                log.warn("Refusing to instantiate potentially unsafe thrown exception: {}.", item)
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

//    private fun executeLocalMethod(calleeContext: ExecutionContext, callerContext: ExecutionContext, node: ExecutionNode) {
//        if (isDebugMode) {
//            startDebugLocalMethod(calleeContext, callerContext, node)
//        } else {
//            var graph: ExecutionGraph? = null
//            try {
//                graph = vm.execute(calleeContext, callerContext, parameterRegisters)
//            } catch (e: VirtualMachineException) {
//                log.warn(e.toString())
//                if (e is UnhandledVirtualException) {
//                    // An exception was thrown but there was no exception handler to catch it.
//                    // It's not clear if it's smalivm's fault or the app's code.
//                    // TODO: bubble this up to the calling method
//                }
//            }
//            finishLocalMethodExecution(calleeContext, callerContext, node, graph)
//        }
//    }
//
//    fun startDebugLocalMethod(calleeContext: ExecutionContext?, callerContext: ExecutionContext?, node: ExecutionNode?) {
//        debuggedMethodExecutor = vm.startDebug(calleeContext, callerContext)
//        debuggedCalleeContext = calleeContext
//        debuggedCallerContext = callerContext
//        debuggedNode = node
//    }
//
//    fun finishDebugLocalMethod(graph: ExecutionGraph?) {
//        finishLocalMethodExecution(debuggedCalleeContext, debuggedCallerContext, debuggedNode, graph)
//    }

    private fun executeLocalObjectInit(node: ExecutionNode): Array<out UnresolvedChild> {
        // TODO: Is there a problem if the VERY first OP invokes this? Is Ljava/lang/Object; clinit'ed?
        val instanceRegister = parameterRegisters[0]
        val instanceItem = node.state.peekRegister(instanceRegister)!!
        val uninitializedInstance = instanceItem.value as UninitializedInstance
        val instanceType = uninitializedInstance.type
        val newInstanceItem = try {
            // Create a Java class of the true type
            val klazz = node.classLoader.loadClass(instanceType.binaryName)
            val newInstance = ObjectInstantiator.newInstance(klazz)
            Value.wrap(newInstance, instanceType.name)
        } catch (e: Exception) {
            log.error("Real exception initializing Object; returning unknown", e)
            Value.unknown(instanceType.name)
        }
        node.state.assignRegister(instanceRegister, newInstanceItem, updateIdentities = true)

        return finishOp()
    }

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

    private fun resolveTargetMethod(virtualReference: Any?, classManager: ClassManager): VirtualMethod {
        /*
         * A method may not be defined in the class referenced by invoke op. The method implementation may be part
         * of the super class. This method searches ancestor hierarchy for the class which implements the method.
         */
        if (virtualReference == null || virtualReference is UnknownValue) {
            return method
        }
        val referenceType = if (virtualReference is UninitializedInstance) {
            virtualReference.type
        } else {
            val targetType = ClassNameUtils.toInternal(virtualReference.javaClass)
            classManager.getVirtualType(targetType)
        }
        val targetMethod = referenceType.getMethod(method.descriptor)

        return if (targetMethod != null && targetMethod.hasImplementation()) {
            targetMethod
        } else {
            method
        }
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(InvokeOp::class.java.simpleName)
        private fun buildParameterRegisters(parameterTypes: List<String>, registers: IntArray): IntArray {
            val parameterRegisters = LinkedList<Int>()
            var index = 0
            for (parameterType in parameterTypes) {
                parameterRegisters.add(registers[index])
                index += Utils.getRegisterSize(parameterType)
            }
            return parameterRegisters.toIntArray()
        }

        private fun buildRegisters(instr: Instruction?): IntArray {
            return when (instr) {
                is Instruction3rc -> buildRegisters3rc(instr)
                else -> buildRegisters35c(instr as Instruction35c)
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
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val instruction = location.instruction as ReferenceInstruction
            val methodReference = instruction.reference as MethodReference
            val registers = buildRegisters(location.instruction)
            val className = methodReference.definingClass
            val type = classManager.getVirtualType(className)
            val methodSignature = ReferenceUtil.getMethodDescriptor(methodReference)
            val methodDescriptor = methodSignature.split("->").toTypedArray()[1]
            val method = type.getMethod(methodDescriptor) ?: throw RuntimeException("Method doesn't exist: $methodSignature")
            val parameterRegisters = buildParameterRegisters(method.parameterTypeNames, registers)
            return InvokeOp(location, method, parameterRegisters)
        }
    }
}
