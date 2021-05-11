package org.cf.smalivm.opcode

import org.cf.smalivm.SideEffectLevel
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.UninitializedInstance
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm2.*
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
//        if (method.signature == CommonTypes.OBJECT + "-><init>()V") {
//            // Object.<init> is a special little snow flake
//            return executeLocalObjectInit(node)
//        }
//
//        if (method.name == "clone" && parameterRegisters.size == 1 && method.signature[0] == '[') {
//            val targetRegister = parameterRegisters[0]
//            val value = node.state.peekRegister(targetRegister)!!
//            if (value.isNotNull && value.rawType[0] == '[') {
//                // Ljava/lang/Object;->clone()Ljava/lang/Object; is also a special snow flake
//                return executeObjectArrayClone(node)
//            }
//        }

        // Must do this at run time to take advantage of extra type information
        val targetMethod = if (!method.isFinal && (name.startsWith("invoke-virtual") || name.startsWith("invoke-interface"))) {
            val targetRegister = parameterRegisters[0]
            val target = node.state.peekRegister(targetRegister)!!
            resolveTargetMethod(target.raw, node.classManager)
        } else {
            method
        }

        val analyzedParameterTypes = analyzeParameterTypes(node)
        val calleeState = ExecutionState.build(targetMethod, node.classManager, node.classLoader, node.configuration)
        assignCalleeMethodArguments(node.state, calleeState, analyzedParameterTypes)
        return callMethod(targetMethod, calleeState)
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

//    private fun executeObjectArrayClone(node: ExecutionNode): Array<out UnresolvedChild> {
//        val instanceRegister = parameterRegisters[0]
//        val arrayItem = node.state.peekRegister(instanceRegister)!!
//        if (arrayItem.isNull) {
//            // This operation would have thrown a null pointer exception, and nothing else.
//            return throwException(NullPointerException::class.java)
//        }
//        val clone = when {
//            arrayItem.isUnknown -> UnknownValue()
//            else -> {
//                try {
//                    val m = Any::class.java.getDeclaredMethod("clone")
//                    m.isAccessible = true
//                    m.invoke(arrayItem.raw)
//                } catch (e: Exception) {
//                    log.error("Real exception initializing Object; returning unknown", e)
//                    UnknownValue()
//                }
//            }
//        }
//        node.state.assignResultRegister(clone, arrayItem.type)
//        return finishOp()
//    }

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
            val type = if (parameterTypeName == CommonTypes.OBJECT && argument.isArray) {
                argument.type
            } else if (argument.isPrimitive || ClassNameUtils.isPrimitive(baseTypeName)) {
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
            val value = callerState.readRegister(callerRegister)
            val parameterType = analyzedParameterTypes[i]
            var raw = value.raw
            if (value.isPrimitive && !value.isUnknown) {
                val hasNullByteValue = value.type == CommonTypes.INTEGER && raw is Number && value.toInteger() == 0
                raw = if (hasNullByteValue && ClassNameUtils.isObject(parameterType)) {
                    null
                } else {
                    // The "I" type may actually be "S", "B", "C", "Z", etc. Cast to the given parameter type.
                    Utils.castToPrimitive(raw, parameterType)
                }
            }
            val argument = Value.wrap(raw, parameterType)
            calleeState.assignParameter(parameterRegister, argument)
            parameterRegister += Utils.getRegisterSize(parameterType)
        }
    }

    override fun resume(node: ExecutionNode, calleeGraph: ExecutionGraph2): Array<out UnresolvedChild> {
        if (calleeGraph.aborted) {
            // VM either aborted execution because of some limit or it couldn't be reflected or emulated.
            assumeMaximumUnknown(node, calleeGraph.method)
            return finishOp()
        }

        // Mutable parameters may have been changed. Need to merge changes back into caller.
        // Reflection mutates parameters directly. No need to merge them ourselves.
        if (!calleeGraph.reflected) {
            val parameterTypes = calleeGraph.method.parameterTypeNames
            var parameterRegister = calleeGraph.root.state.firstParameterRegister
            for (parameterIndex in parameterTypes.indices) {
                val type = parameterTypes[parameterIndex]
                if (node.configuration.isMutable(type)) {
                    val value = calleeGraph.getTerminatingParameterConsensus(parameterRegister)
                    val register = parameterRegisters[parameterIndex]
                    node.state.assignParameter(register, value, updateIdentities = true)
                }
                parameterRegister += Utils.getRegisterSize(type)
            }
        }

        val hasOneReturnPath = calleeGraph.hasAtLeastOneReturnPath()
        if (hasOneReturnPath) {
            if (!method.returnsVoid()) {
                // Terminating addresses may include throw ops which may not have a return register set
                val result = calleeGraph.getRegisterConsensus(calleeGraph.connectedReturnAddresses, ExecutionState.RETURN_REGISTER)
                node.state.assignResultRegister(result)
            } else {
                if (calleeGraph.method.name == "<init>") {
                    // This was a call to a local parent <init> method
                    val calleeInstanceRegister = calleeGraph.root.state.firstParameterRegister
                    val newInstance = calleeGraph.getRegisterConsensus(calleeGraph.terminatingAddresses, calleeInstanceRegister)
                    val instanceRegister = parameterRegisters[0]
                    node.state.assignRegister(instanceRegister, newInstance, updateIdentities = true)
                }
            }
        }
        node.sideEffectLevel = calleeGraph.getHighestSideEffectLevel()

        // Unhandled exceptions in callee are handled by VM when the step is finished
        return if (hasOneReturnPath) {
            finishOp()
        } else {
            // This method never returns so it must only ever throw exceptions.
            finishOpWithoutChildren()
        }
    }

    private fun assumeMaximumUnknown(node: ExecutionNode, calledMethod: VirtualMethod) {
        // TODO: add option to mark all class states unknown instead of just method state
        // This is heavy handed in most cases, and maybe there's a way to optimize. It's hard to tell if the method we're
        // failing to execute modifies class state. If it did, we aren't capturing it here.
        node.sideEffectLevel = SideEffectLevel.STRONG
        for (i in parameterRegisters.indices) {
            val register = parameterRegisters[i]
            val arg = node.state.readRegister(register)
            if (arg.isNull) {
                // Null is immutable. No need to mark unknown.
                continue
            }
            if (i == 0 && calledMethod.name == "<init>") {
                // Instance initialization failed so mark unknown. Note this happens even if arg is immutable because state can change in <init>
                node.state.pokeRegister(register, Value.unknown(arg.type))
                continue
            } else if (arg.isImmutable) {
                // Could not have possibly mutated in aborted method.
                continue
            }
            node.state.pokeRegister(register, Value.unknown(arg.type))
        }

        if (!calledMethod.returnsVoid()) {
            val result = Value.unknown(calledMethod.returnType)
            node.state.assignResultRegister(result)
        }

        // TODO: add tests for this
        if (calledMethod.name == "<clinit>") {
            node.state.setClassInitialized(calledMethod.definingClass, SideEffectLevel.STRONG)
            for (field in calledMethod.definingClass.fields) {
                val value = Value.unknown(field.type)
                node.state.pokeField(field, value)
            }
        }
    }

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
            val methodDescriptor = methodSignature.split("->")[1]
            val method = type.getMethod(methodDescriptor) ?: throw RuntimeException("Method doesn't exist: $methodSignature")
            val parameterRegisters = buildParameterRegisters(method.parameterTypeNames, registers)
            return InvokeOp(location, method, parameterRegisters)
        }
    }
}
