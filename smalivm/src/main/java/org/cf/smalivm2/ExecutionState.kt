package org.cf.smalivm2

import com.rits.cloning.Cloner
import org.apache.commons.lang3.reflect.FieldUtils
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.context.ClonerFactory
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.opcode.Op
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualField
import org.cf.smalivm.type.VirtualMethod
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.HashMap

class ExecutionState(
    val cloner: Cloner,
    val classManager: ClassManager,
    val configuration: Configuration,
    val registerCount: Int,
    val parameterCount: Int = 0,
    val parameterSize: Int = 0,
    val fieldCount: Int = 0,
    initializedClassesSize: Int = 0,
    registersReadSize: Int = 0,
    registersAssignedSize: Int = 0,
    mutableParametersSize: Int = 0
) {
    // TODO: should be able to look at op and decide how many registers are assigned and read, should save in future allocations
    val values = HashMap<String, Value>(registerCount + fieldCount)
    val initializedClasses = HashSet<String>(initializedClassesSize)
    val mutableParameters: MutableSet<Int> = HashSet<Int>(mutableParametersSize)
    val firstParameterRegister = registerCount - parameterSize
    val registersAssigned: MutableSet<Int> = HashSet<Int>(registersAssignedSize)
    val registersRead: MutableSet<Int> = HashSet<Int>(registersReadSize)
    var node: ExecutionNode? = null

    companion object {
        private val log = LoggerFactory.getLogger(ExecutionState::class.java.simpleName)

        const val ResultRegister = -1
        const val ReturnRegister = -2
        const val ThrowRegister = -3
        const val ExceptionRegister = -4
        const val PSEUDO_RETURN_ADDRESS_KEY = "*pseudo-return*"

        fun build(
            op: Op,
            method: VirtualMethod,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): ExecutionState {
            if (!method.hasImplementation()) {
                // May be native or abstract and thus no implementation and thus shouldn't execute
                throw IllegalArgumentException("No implementation for $method");
            }

            val cloner = ClonerFactory.instance(classManager, classLoader, configuration)
            val registerCount = method.registerCount
            val parameterTypeNames = method.parameterTypeNames
            val parameterCount = parameterTypeNames.size
            val parameterSize = Utils.getRegisterSize(parameterTypeNames)
            val firstParameterRegister = registerCount - parameterSize
            val fieldCount = method.definingClass.fields.size

            val state = ExecutionState(
                cloner,
                classManager,
                configuration,
                registerCount,
                parameterCount,
                parameterSize,
                fieldCount,
                0,
                op.registersReadCount,
                op.registersAssignedCount
            )

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

            for (field in method.definingClass.fields) {
                val value = Value.wrap(field.initialValue, field.type)
                state.pokeField(field, value)
            }

            return state
        }

        private fun getReassignedKeysBetweenChildAndAncestor(child: ExecutionState, ancestor: ExecutionState): Set<String?> {
            val reassigned: MutableSet<String> = HashSet()
            var current: ExecutionState = child
            while (current !== ancestor) {
                reassigned.addAll(current.values.keys)
                val parent = current.getParent() ?: break
                current = parent
            }
            return reassigned
        }
    }

    // Don't extend back beyond an entrypoint node to potential caller nodes to ensure we're only looking at method state
    fun getParent(methodLocal: Boolean = true): ExecutionState? {
        if (methodLocal && (node == null || node is EntrypointNode)) {
            return null
        }
        return this.node?.parent?.state
    }

    fun wasRegisterAssigned(register: Int): Boolean {
        return registersAssigned.contains(register)
    }

    fun assignRegister(register: Int, v: Any?, type: String, updateIdentities: Boolean = false) {
        val value = Value.wrap(v, type)
        return assignRegister(register, value, updateIdentities)
    }

    fun assignRegister(register: Int, value: Value, updateIdentities: Boolean = false) {
        registersAssigned.add(register)
        if (register >= firstParameterRegister && value.isMutable) {
            mutableParameters.add(register)
        }

        pokeKey(register.toString(), value, updateIdentities)
    }

    fun assignField(field: VirtualField, value: Value, updateIdentities: Boolean = false) {
        pokeField(field, value, updateIdentities)
    }

    fun assignField(field: String, value: Value, updateIdentities: Boolean = false) {
        pokeField(field, value, updateIdentities)
    }

    fun assignResultRegister(value: Value) {
        assignRegister(MethodState.ResultRegister, value)
    }

    fun assignResultRegister(v: Any?, type: String) {
        assignRegister(MethodState.ResultRegister, Value.wrap(v, type))
    }

    fun assignReturnRegister(value: Value) {
        pokeRegister(MethodState.ReturnRegister, value)
    }

    fun assignReturnRegister(v: Any?, type: String) {
        pokeRegister(MethodState.ReturnRegister, Value.wrap(v, type))
    }

    fun assignThrowRegister(value: Value) {
        pokeRegister(ThrowRegister, value)
    }

    fun readResultRegister(): Value {
        return readRegister(ResultRegister)
    }

    fun readReturnRegister(): Value {
        return readRegister(ReturnRegister)
    }

    fun pokeRegister(register: Int, v: Any?, type: String, updateIdentities: Boolean = false) {
        pokeKey(register.toString(), Value.wrap(v, type), updateIdentities)
    }

    fun pokeRegister(register: Int, value: Value, updateIdentities: Boolean = false) {
        pokeKey(register.toString(), value, updateIdentities)
    }

    fun pokeField(fieldSignature: String, value: Value, updateIdentities: Boolean = false) {
        val field = classManager.getVirtualField(fieldSignature)
        pokeField(field, value, updateIdentities)
    }

    fun pokeField(field: VirtualField, value: Value, updateIdentities: Boolean = false) {
        if (configuration.isSafe(field.definingClass)) {
            // TODO: consider enabling this, is it safe?
            log.warn("Ignoring static assignment of non-local field: {} = {}", field, value)
        } else {
            pokeKey(field.toString(), value, updateIdentities)
        }
    }

    private fun pokeKey(key: String, value: Value, updateIdentities: Boolean = false) {
        log.trace("poke {} = {}", key, value)
        if (updateIdentities) {
            update(key, value)
        } else {
            values[key] = value
        }
    }

    fun peekRegister(register: Int): Value? {
        return peekKey(register.toString())
    }

    fun peekField(fieldSignature: String): Value {
        val field = classManager.getVirtualField(fieldSignature)
        return peekField(field)
    }

    fun peekField(field: VirtualField): Value {
        val value = if (configuration.isSafe(field.definingClass)) {
            getSafeField(field)
        } else {
            peekKey(field.toString(), false)
        }
        if (value == null) {
            log.error("undefined field: {}; returning unknown", field)
            return Value.unknown(field.type)
        }
        return value
    }

    fun peekExceptionRegister(): Value? {
        return peekRegister(ExceptionRegister)
    }

    fun peekResultRegister(): Value? {
        return peekRegister(ResultRegister)
    }

    fun peekReturnRegister(): Value? {
        return peekRegister(ReturnRegister)
    }

    fun peekThrowRegister(): Value? {
        return peekRegister(ThrowRegister)
    }

    fun containsRegister(register: Int): Boolean {
        return values.containsKey(register.toString())
    }

    fun readRegister(register: Int): Value {
        // TODO: what happens if an op tries to read an uninitialized register? this assumes an op will never need to read (vs peek) one
        registersRead.add(register)
        return peekRegister(register)!!
    }

    fun removeRegister(register: Int) {
        removeKey(register.toString())
    }

    private fun removeKey(key: String) {
        values.remove(key)
    }

    // TODO: note that this returns true if a register hasn't been read but another register which points to the same object has been read
    fun wasRegisterRead(register: Int): Boolean {
        if (registersRead.contains(register)) {
            return true
        }
        val value = peekRegister(register) ?: return false

        /*
         * Since the goal of this method is to identify which registers were read / used and thus shouldn't be removed,
         * need to exclude the result register as the move-result op will always read and assign an identical object
         * but this shouldn't mean move-result counts as a real usage.
         *
         * E.g. If addTwoNumbers(int, int): int is called and the return value is never used, the call should be removed,
         * regardless of whether or not move-result is called.
         */
        for (currentRegister in registersRead) {
            if (currentRegister == MethodState.ResultRegister) {
                continue
            }
            val currentValue = peekRegister(currentRegister)!!
            if (value.value === currentValue.value) {
                return true
            }
        }
        return false
    }

//    fun getChild(childContext: ExecutionContext): ClassState {
//        return ClassState(this, childContext)
//    }

//    override fun hashCode(): Int {
//        return HashCodeBuilder(1337, 13).append(virtualClass).hashCode()
//    }
//
//    override fun equals(obj: Any?): Boolean {
//        if (obj == null) {
//            return false
//        } else if (obj === this) {
//            return true
//        } else if (obj.javaClass != javaClass) {
//            return false
//        }
//        val other = obj as ClassState
//        return EqualsBuilder().append(virtualClass, other.virtualClass).isEquals
//    }

    override fun toString(): String {
        return toString(true)
    }

    fun toString(onlyPeekCachedRegisters: Boolean): String {
        val sb = StringBuilder()
        val localsCount = registerCount - parameterSize
        val firstParameterRegister = registerCount - parameterSize
        sb.append("params: ").append(parameterCount).append(", ")
        sb.append("locals: ").append(localsCount).append('\n')
        val inner = StringBuilder()
        var register = 0
        while (register < registerCount) {
            if (onlyPeekCachedRegisters && !containsRegister(register)) {
                register += 1
                continue
            }
            inner.append('v').append(register)
            val isLocal = register < firstParameterRegister
            if (!isLocal) {
                inner.append("(p").append(register - firstParameterRegister).append(')')
            }
            val value = peekRegister(register)!!
            inner.append("`: ").append(value).append('\n')
            val registerSize = Utils.getRegisterSize(value.type)
            register += registerSize
        }
        if (containsRegister(MethodState.ResultRegister)) {
            inner.append("result: ").append(peekRegister(MethodState.ResultRegister)).append('\n')
        }
        if (containsRegister(MethodState.ReturnRegister)) {
            inner.append("return: ").append(peekRegister(MethodState.ReturnRegister)).append('\n')
        }
        if (inner.isNotEmpty()) {
            inner.setLength(inner.length - 1)
            sb.append(inner)
        }

        inner.clear()
        for (key in values.keys) {
            val isRegister = key.toIntOrNull() != null
            if (isRegister) {
                continue
            }
            inner.append(key).append(" = ").append(peekField(key)).append('\n')
        }
        if (inner.isNotEmpty()) {
            inner.setLength(inner.length - 1)
            sb.append('\n')
            sb.append(inner)
        }

        return sb.toString()
    }


    /**
     * Retrieves the value indexed by `key`. Searches ancestors if the value is not stored in the current state. If key is found in an ancestor, this
     * will clone the value and store a mapping in current state of all keys which point to the same value. Cloning is done to allow for value
     * modifications while preserving previous values and thus history.
     *
     * E.g. While peeking v0, it's found that the value is stored in an ancestor. In that ancestor, v0, v1, and v2 all point to the same object.
     * However, v2 was reassigned between the ancestor and this state, so only v0 and v1 are cloned and stored locally.
     * @param key
     * @param methodLocal only look within parents for this method (i.e. looking up registers instead of fields)
     * @return
     */
    private fun peekKey(key: String, methodLocal: Boolean = true): Value? {
        if (hasKey(key)) {
            return values[key]
        }

        val ancestor = getAncestorWithKey(key, methodLocal)
        if (ancestor == null) {
            log.trace("Undefined value: {}; possible mistake!", key, Exception())
            return null
        }

        val targetItem = ancestor.peekKey(key)!!
        val clone = cloneValue(targetItem)
        val reassigned = getReassignedKeysBetweenChildAndAncestor(this, ancestor)
        val potential = ancestor.values.keys
        for (currentKey in potential) {
            if (reassigned.contains(currentKey)) {
                continue
            }
            val currentItem = ancestor.peekKey(currentKey)!!
            if (targetItem.value === currentItem.value) {
                pokeKey(currentKey, clone)
            }
        }
        return clone
    }

    fun hasKey(key: String): Boolean {
        return values.containsKey(key)
    }

    /**
     * This behaves like [set][.set] and also updates values for all keys
     * which point to values identical to what was stored in `key` originally. This is
     * necessary because the same item may exist under multiple mappings that need to be updated.
     * For example, when an uninitialized instance is stored under multiple mappings and is
     * initialized, rather than simply setting a single key with the updated instance, it's
     * necessary to look for any other identical copies of the uninitialized instance and update
     * them with the new initialized instance value.
     *
     * @param key
     * @param newValue
     */
    fun update(key: String, newValue: Value) {
        val oldValue = peekKey(key)!!
        if (oldValue.value == null) {
            values[key] = newValue
        } else {
            for (currentKey in values.keys) {
                val currentValue = peekKey(currentKey)!!
                if (oldValue.value == currentValue.value) {
                    values[currentKey] = newValue
                }
            }
        }
    }

    private fun cloneValue(original: Value): Value {
        val cloneValue = cloner.deepClone(original.value)
        return Value.wrap(cloneValue, original.type)
    }

    private fun getAncestorWithKey(key: String, methodLocal: Boolean = true): ExecutionState? {
        var ancestor: ExecutionState? = this
        do {
            if (ancestor!!.hasKey(key)) {
                break
            }
            ancestor = ancestor.getParent(methodLocal)
        } while (ancestor != null)
        return ancestor
    }

    fun getPsuedoInstructionReturnLocation(): MethodLocation {
        return (peekKey(PSEUDO_RETURN_ADDRESS_KEY)?.value!! as MethodLocation)
    }

    fun setPseudoInstructionReturnLocation(location: MethodLocation) {
        pokeKey(PSEUDO_RETURN_ADDRESS_KEY, Value.wrap(location, PSEUDO_RETURN_ADDRESS_KEY))
    }

    private fun getSafeField(field: VirtualField): Value {
        val className = field.definingClass.binaryName
        try {
            val klazz = Class.forName(className)
            val realField = FieldUtils.getField(klazz, field.name)
            val fieldValue = realField[null]
            return Value.wrap(fieldValue, field.type)
        } catch (e: Exception) {
            // TODO: medium - throw these exceptions and handle them by setting correct virtual exceptions
            log.warn("Couldn't access field: {}", field.toString())
            log.debug("Stack trace:", e)
        }
        return Value.unknown(field.type)
    }


//    private fun keys(): Set<String> {
//        // Note: mutating this directly alters keyToHeapItem's keys
//        return keyToValues.keys
//    }
//
//    private fun remove(key: String) {
//        keyToValues.remove(key)
//    }

//    private fun set(key: String, value: Value) {
//        values[key] = value
//    }

}
