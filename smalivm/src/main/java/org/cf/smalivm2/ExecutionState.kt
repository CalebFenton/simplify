package org.cf.smalivm2

import com.rits.cloning.Cloner
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.context.*
import org.cf.smalivm.context.ClonerFactory
import org.cf.smalivm.opcode.Op
import org.cf.smalivm.type.*
import org.cf.util.Utils
import org.jf.dexlib2.iface.instruction.*
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.HashMap

class ExecutionState(
    val cloner: Cloner,
    val registerCount: Int,
    val parameterCount: Int = 0,
    val parameterSize: Int = 0,
    val fieldCount: Int = 0,
    initializedClassesSize: Int = 0,
    registersAssignedSize: Int = 0,
    registersReadSize: Int = 0
) {
    // TODO: should be able to look at op and decide how many registers are assigned and read, should save in future allocations
    protected val values = HashMap<String, Value>(registerCount + fieldCount)
    protected val initializedClasses = HashSet<String>(initializedClassesSize)
    protected val registersAssigned: MutableSet<Int> = HashSet<Int>(registersAssignedSize)
    protected val registersRead: MutableSet<Int> = HashSet<Int>(registersReadSize)
    lateinit var node: ExecutionNode


    companion object {
        private val log = LoggerFactory.getLogger(ExecutionState::class.java.simpleName)

        fun build(method: VirtualMethod, op: Op, classManager: ClassManager, classLoader: ClassLoader, configuration: Configuration): ExecutionState {
            if (!method.hasImplementation()) {
                // Native or abstract methods have no implementation. Shouldn't be executing them.
                throw IllegalArgumentException("No implementation for $method");
            }

            val cloner = ClonerFactory.instance(classManager, classLoader, configuration)
            val registerCount = method.registerCount
            val parameterTypeNames = method.parameterTypeNames
            val parameterCount = parameterTypeNames.size
            val parameterSize = Utils.getRegisterSize(parameterTypeNames)
            val firstParameterRegister = registerCount - parameterSize
            val fieldCount = method.definingClass.fields.size

            // TODO: every op should know how many registers it reads and assigns. It can't be inferred from interfaces
            // and depends on the specific op. Once that's added, states can know exactly how big to make certain internal
            // data structures and save on initializationk
            val instr = op.instruction
            val registersRead = when (instr) {
                is OneRegisterInstruction -> 1
                is TwoRegisterInstruction -> 2
                is ThreeRegisterInstruction -> 3
                is FiveRegisterInstruction -> 5
                is VariableRegisterInstruction -> (instr as VariableRegisterInstruction).registerCount
                else -> 0
            }

            val state = ExecutionState(cloner, registerCount, parameterCount, parameterSize, fieldCount)

            val currentRegister = firstParameterRegister
            for ( typeName in method.parameterTypeNames) {
                val value = if (currentRegister == firstParameterRegister && !method.isStatic && method.name == "<init>") {
                    val instance = UninitializedInstance(method.definingClass)
                    Value.wrap(instance, typeName)
                } else {
                    Value.wrap(UnknownValue(), typeName)
                }
                state.
            }
            for (VirtualField field : virtualClass.getFields()) {
                Object value = field.getInitialValue();
                String type = field.getType();
                cState.pokeField(field, new HeapItem(value, type));
            }

//        VirtualMethod method = context.getMethod();
//        int registerCount = method.getRegisterCount();
//        List<String> parameterTypes = method.getParameterTypeNames();
//        int parameterSize = Utils.getRegisterSize(parameterTypes);
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
//        ExecutionContext spawnedContext = new ExecutionContext(this, method);
//        ClassState templateClassState = TemplateStateFactory.forClass(spawnedContext, method.getDefiningClass());
//        spawnedContext.setClassState(templateClassState);
//
//        MethodState templateMethodState = TemplateStateFactory.forMethod(spawnedContext);
//        spawnedContext.setMethodState(templateMethodState);
//
//        if (callerContext != null) {
//            spawnedContext.registerCaller(callerContext, callerAddress);
//        }

        }
    }

    fun wasRegisterAssigned(register: Int): Boolean {
        return registersAssigned.contains(register)
    }

    fun assignRegister(register: Int, value: Value, updateIdentities: Boolean = false) {
        registersAssigned.add(register)
        if (updateIdentities) {
            node.value heap update identities
        } else {
            pokeRegister(register, value)
        }
    }

    fun assignRegisterAndUpdateIdentities(register: Int, value: HeapItem?, heapId: String?) {
        registersAssigned.add(register)
        context.heap.update(heapId, register, item)
    }

//    open val parent: BaseState?
//        get() {
//            val parentContext = context.parent
//            var parent: MethodState? = null
//            if (parentContext != null) {
//                parent = parentContext.methodState
//            }
//            return parent
//        }

    fun hasRegister(register: Int, heapId: String?): Boolean {
        return context.heap.hasRegister(heapId, register)
    }

    fun peekRegister(register: Int, heapId: String?): HeapItem {
        return context.heap[heapId, register]
    }

    fun pokeRegister(register: Int, item: HeapItem?, heapId: String?) {
        if (log.isTraceEnabled) {
            val sb = StringBuilder()
            sb.append("Setting ").append(heapId).append(':').append(register).append(" = ").append(item)
            // VERY noisy
            // StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            // for (int i = 2; i < ste.length; i++) {
            // sb.append("\n\t").append(ste[i]);
            // }
            log.trace(sb.toString())
        }
        context.heap[heapId, register] = item
    }

    public fun readRegister(register: Int, heapId: String?): HeapItem {
        registersRead.add(register)
        return peekRegister(register, heapId)
    }

    fun removeRegister(register: Int, heapId: String?) {
        context.heap.remove(heapId, register)
    }

    fun wasRegisterRead(register: Int, heapId: String?): Boolean {
        if (registersRead.contains(register)) {
            return true
        }
        val item = peekRegister(register, heapId) ?: return false

        /*
         * Since multiple registers may hold the same object class, need to examine other registers for identity.
         * However, result register must be excluded because move-result will always read and assign an identical object
         * every time it's executed.
         */for (currentRegister in getRegistersRead()) {
            if (currentRegister == MethodState.ResultRegister) {
                continue
            }
            val currentItem = peekRegister(currentRegister, heapId)
            if (item.value === currentItem.value) {
                return true
            }
        }
        return false
    }

    fun assignField(field: VirtualField, value: Any?) {
        val register = 0
        val heapId = getHeapId(field)
        val type = field.type
        assignRegister(register, HeapItem(value, type), heapId)
    }

    fun assignField(field: VirtualField, item: HeapItem?) {
        val register = 0
        val heapId = getHeapId(field)
        assignRegister(register, item, heapId)
    }

    fun getChild(childContext: ExecutionContext): ClassState {
        return ClassState(this, childContext)
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(1337, 13).append(virtualClass).hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        } else if (obj === this) {
            return true
        } else if (obj.javaClass != javaClass) {
            return false
        }
        val other = obj as ClassState
        return EqualsBuilder().append(virtualClass, other.virtualClass).isEquals
    }

    override fun toString(): String {
        val sb = StringBuilder("Fields:\n")
        for (field in virtualClass.fields) {
            sb.append(field).append(" = ").append(peekField(field)).append('\n')
        }
        sb.setLength(sb.length - 1)
        sb.append('\n')
        return sb.toString()
    }

    fun peekField(field: VirtualField): HeapItem? {
        val register = 0
        val heapId = getHeapId(field)
        var fieldItem: HeapItem? = peekRegister(register, heapId)
        if (fieldItem == null) {
            log.error("Undefined field: {}; returning unknown", field)
            fieldItem = HeapItem.newUnknown(field.type)
        }
        return fieldItem
    }

    fun pokeField(field: VirtualField, value: Any?) {
        val register = 0
        val heapId = getHeapId(field)
        val type = field.type
        pokeRegister(register, HeapItem(value, type), heapId)
    }

    fun pokeField(field: VirtualField, item: HeapItem?) {
        val register = 0
        val heapId = getHeapId(field)
        pokeRegister(register, item, heapId)
    }

    fun updateIdentities(field: VirtualField, item: HeapItem?) {
        val register = 0
        val heapId = getHeapId(field)
        val heapKey = "$heapId:$register"
        updateKey(heapKey, item)
    }

}
