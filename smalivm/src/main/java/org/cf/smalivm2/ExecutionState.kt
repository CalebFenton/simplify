package org.cf.smalivm2

import com.rits.cloning.Cloner
import org.apache.commons.lang3.reflect.FieldUtils
import org.cf.smalivm.SideEffectLevel
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.context.ClonerFactory
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualField
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm.type.VirtualType
import org.cf.util.Utils
import org.slf4j.LoggerFactory

class ExecutionState internal constructor(
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
    val initializedClasses: MutableMap<VirtualType, SideEffectLevel> = HashMap(initializedClassesSize)
    val mutableParameters: MutableSet<Int> = HashSet(mutableParametersSize)
    val firstParameterRegister = registerCount - parameterSize
    val registersAssigned: MutableSet<Int> = HashSet(registersAssignedSize)
    val registersRead: MutableSet<Int> = HashSet(registersReadSize)
    var node: ExecutionNode? = null

    companion object {
        private val log = LoggerFactory.getLogger(ExecutionState::class.java.simpleName)

        const val RESULT_REGISTER = -1
        const val RETURN_REGISTER = -2
        const val THROW_REGISTER = -3
        const val EXCEPTION_REGISTER = -4
        const val PSEUDO_RETURN_ADDRESS_KEY = "*pseudo-return*"

        fun build(
            method: VirtualMethod,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration,
            registersReadCount: Int = 0,
            registersAssignedCount: Int = 0
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
            val fieldCount = method.definingClass.fields.size

            return ExecutionState(
                cloner,
                classManager,
                configuration,
                registerCount,
                parameterCount,
                parameterSize,
                fieldCount,
                0,
                registersReadCount,
                registersAssignedCount
            )
        }

        private fun getReassignedKeysBetweenChildAndAncestor(child: ExecutionState, ancestor: ExecutionState): Set<String> {
            val reassigned: MutableSet<String> = HashSet()
            var current: ExecutionState = child
            while (current != ancestor) {
                /*
                 * This assumes that just because a register exists in a state it must have been reassigned to a different value.
                 * However, it's strictly possible that a register could be assigned to itself but this seems like such an edge case
                 * that it won't be considered here.
                 */
                reassigned.addAll(current.values.keys)
                current = current.getParent() ?: break
            }
            return reassigned
        }
    }

    fun getParent(methodLocal: Boolean = true): ExecutionState? {
        // Registers are method local so shouldn't reach beyond a method's entry point
        // Fields and class initialization are not method local, and should reach all the way back
        if (methodLocal && (node == null || node!!.isEntrypoint)) {
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

    fun assignRegisters(vararg params: Any?) {
        var i = 0
        while (i < params.size) {
            val register = params[i] as Int
            val rawValue = params[i + 1]
            val type = params[i + 2] as String
            val value = Value.wrap(rawValue, type)
            assignRegister(register, value)
            i += 3
        }
    }

    fun assignField(field: VirtualField, value: Value, updateIdentities: Boolean = false) {
        pokeField(field, value, updateIdentities)
    }

    fun assignField(field: String, value: Value, updateIdentities: Boolean = false) {
        pokeField(field, value, updateIdentities)
    }

    fun assignResultRegister(value: Value) {
        assignRegister(RESULT_REGISTER, value)
    }

    fun assignResultRegister(v: Any?, type: String) {
        assignRegister(RESULT_REGISTER, Value.wrap(v, type))
    }

    fun assignReturnRegister(value: Value) {
        pokeRegister(RETURN_REGISTER, value)
    }

    fun assignReturnRegister(v: Any?, type: String) {
        pokeRegister(RETURN_REGISTER, Value.wrap(v, type))
    }

    fun assignExceptionRegister(value: Value) {
        pokeRegister(EXCEPTION_REGISTER, value)
    }

    fun assignThrowRegister(value: Value) {
        pokeRegister(THROW_REGISTER, value)
    }

    fun readResultRegister(): Value {
        return readRegister(RESULT_REGISTER)
    }

    fun readReturnRegister(): Value {
        return readRegister(RETURN_REGISTER)
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

    fun peekParameterOffset(parameterRegisterOffset: Int): Value? {
        return peekRegister(firstParameterRegister + parameterRegisterOffset)
    }

    fun peekRegister(register: Int, methodLocal: Boolean = true): Value? {
        return peekKey(register.toString(), methodLocal)
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
        return peekRegister(EXCEPTION_REGISTER)
    }

    fun peekResultRegister(): Value? {
        return peekRegister(RESULT_REGISTER)
    }

    fun peekReturnRegister(): Value? {
        return peekRegister(RETURN_REGISTER)
    }

    fun peekThrowRegister(): Value? {
        return peekRegister(THROW_REGISTER)
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
            if (currentRegister == RESULT_REGISTER) {
                continue
            }
            val currentValue = peekRegister(currentRegister)!!
            if (value.raw === currentValue.raw) {
                return true
            }
        }
        return false
    }

    fun allArgumentsKnown(): Boolean {
        var parameterRegister = firstParameterRegister
        while (parameterRegister < firstParameterRegister + parameterSize) {
            val value = peekRegister(parameterRegister)!!
            if (value.isUnknown) {
                return false
            }
            parameterRegister += value.registerSize
        }
        return true
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
            inner.append(": ").append(value).append('\n')
            val registerSize = Utils.getRegisterSize(value.type)
            register += registerSize
        }
        if (containsRegister(RESULT_REGISTER)) {
            inner.append("result: ").append(peekRegister(RESULT_REGISTER)).append('\n')
        }
        if (containsRegister(RETURN_REGISTER)) {
            inner.append("return: ").append(peekRegister(RETURN_REGISTER)).append('\n')
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

        val ancestor = findNearestAncestorWithKey(key, methodLocal)
        if (ancestor == null) {
            log.trace("Undefined value: {}; possible mistake!", key, Exception())
            return null
        }

        val targetItem = ancestor.values[key]!!
        val clone = cloneValue(targetItem)
        val reassigned = getReassignedKeysBetweenChildAndAncestor(this, ancestor)
        val potential = ancestor.values.keys
        for (currentKey in potential) {
            if (reassigned.contains(currentKey)) {
                continue
            }
            val currentItem = ancestor.values[currentKey]!!
            if (targetItem.raw === currentItem.raw) {
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
        if (oldValue.raw == null) {
            values[key] = newValue
        } else {
            for (currentKey in values.keys) {
                val currentValue = peekKey(currentKey)!!
                if (oldValue.raw == currentValue.raw) {
                    values[currentKey] = newValue
                }
            }
        }
    }

    private fun cloneValue(original: Value): Value {
        val cloneValue = cloner.deepClone(original.raw)
        return Value.wrap(cloneValue, original.type)
    }

    private fun findNearestAncestorWithKey(key: String, methodLocal: Boolean = true): ExecutionState? {
        var ancestor: ExecutionState? = this
        do {
            if (ancestor!!.hasKey(key)) {
                break
            }
            ancestor = ancestor.getParent(methodLocal)
        } while (ancestor != null)
        return ancestor
    }

    fun getPsuedoInstructionReturnAddress(): Int {
        return (peekKey(PSEUDO_RETURN_ADDRESS_KEY)?.raw!! as Int)
    }

    fun setPseudoInstructionReturnAddress(address: Int) {
        pokeKey(PSEUDO_RETURN_ADDRESS_KEY, Value.wrap(address, PSEUDO_RETURN_ADDRESS_KEY))
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


    fun getClassSideEffectLevel(virtualClass: VirtualType): SideEffectLevel? {
        val ancestor = getAncestorWithClass(virtualClass) ?: return null
        return ancestor.initializedClasses[virtualClass]!!
    }

    fun setClassInitialized(classSignature: String, level: SideEffectLevel = SideEffectLevel.NONE) {
        val virtualClass = classManager.getVirtualClass(classSignature)
        setClassInitialized(virtualClass, level)
    }

    fun setClassInitialized(virtualClass: VirtualType, level: SideEffectLevel = SideEffectLevel.NONE) {
        initializedClasses[virtualClass] = level
    }

    fun isClassInitialized(virtualClass: VirtualType): Boolean {
        return when {
            initializedClasses.contains(virtualClass) -> true
            getParent() != null -> getParent()!!.isClassInitialized(virtualClass)
            else -> false
        }
    }

    private fun getAncestorWithClass(virtualClass: VirtualType): ExecutionState? {
        var ancestor: ExecutionState? = this
        do {
            if (ancestor!!.initializedClasses.containsKey(virtualClass)) {
                return ancestor
            }
            ancestor = ancestor.getParent()
        } while (ancestor != null)
        return null
    }

    fun isClassInitialized(classSignature: String): Boolean {
        val virtualClass = classManager.getVirtualClass(classSignature)
        return isClassInitialized(virtualClass)
    }
}
