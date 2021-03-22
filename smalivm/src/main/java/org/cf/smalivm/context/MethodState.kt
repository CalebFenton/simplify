package org.cf.smalivm.context

import org.cf.smalivm.context.BaseState
import gnu.trove.set.TIntSet
import kotlin.jvm.JvmOverloads
import org.cf.smalivm.context.ExecutionContext
import gnu.trove.set.hash.TIntHashSet
import org.cf.smalivm.context.MethodState
import org.cf.util.ClassNameUtils
import org.cf.smalivm.context.HeapItem
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory
import java.lang.StringBuilder

class MethodState : BaseState {
    val parameterCount: Int
    private val parameterSize: Int
    private val mutableParameters: TIntSet

    @JvmOverloads
    constructor(context: ExecutionContext, registerCount: Int, parameterCount: Int = 0, parameterSize: Int = 0) : super(context, registerCount) {
        this.parameterCount = parameterCount
        this.parameterSize = parameterSize
        mutableParameters = TIntHashSet(parameterCount)
    }

    internal constructor(other: MethodState, context: ExecutionContext) : super(other, context) {
        parameterCount = other.parameterCount
        parameterSize = other.parameterSize
        mutableParameters = TIntHashSet(other.mutableParameters)
    }

    private constructor(parent: MethodState, context: ExecutionContext, mutableParameters: TIntSet) : super(parent, context) {
        parameterCount = parent.parameterCount
        parameterSize = parent.parameterSize
        this.mutableParameters = parent.mutableParameters
    }

    fun assignExceptionRegister(exception: Throwable) {
        val className = ClassNameUtils.toInternal(exception.javaClass)
        val item = HeapItem(exception, className)
        pokeRegister(ExceptionRegister, item, METHOD_HEAP)
    }

    fun assignParameter(parameterRegister: Int, item: HeapItem) {
        assignRegister(parameterRegister, item, METHOD_HEAP)
        val mutable = !item.isImmutable
        if (mutable) {
            pokeRegister(parameterRegister, item, MUTABLE_PARAMETER_HEAP)
            mutableParameters.add(parameterRegister)
        }
    }

    fun assignParameter(parameterRegister: Int, value: Any?, type: String?) {
        assignParameter(parameterRegister, HeapItem(value, type))
    }

    fun assignRegister(register: Int, item: HeapItem?) {
        assignRegister(register, item, METHOD_HEAP)
    }

    fun assignRegister(register: Int, value: Any?, type: String?) {
        assignRegister(register, HeapItem(value, type))
    }

    /**
     * Identical to [.assignRegister] but also updates any register with an identical value to that
     * stored in the target register with the new value.
     */
    fun assignRegisterAndUpdateIdentities(register: Int, item: HeapItem) {
        assignRegisterAndUpdateIdentities(register, item, METHOD_HEAP)
    }

    fun assignResultRegister(item: HeapItem?) {
        assignRegister(ResultRegister, item, METHOD_HEAP)
    }

    fun assignResultRegister(value: Any?, type: String?) {
        assignRegister(ResultRegister, HeapItem(value, type))
    }

    fun assignReturnRegister(item: HeapItem) {
        pokeRegister(ReturnRegister, item, METHOD_HEAP)
    }

    fun assignReturnRegister(value: Any?, type: String) {
        pokeRegister(ReturnRegister, HeapItem(value, type), METHOD_HEAP)
    }

    fun assignThrowRegister(item: HeapItem) {
        pokeRegister(ThrowRegister, item, METHOD_HEAP)
    }

    val parameterStart: Int
        get() = registerCount - parameterSize

    fun getParent(): MethodState {
        return super.parent as MethodState
    }

    val pseudoInstructionReturnInstruction: MethodLocation?
        get() = peekRegister(ReturnAddressRegister)!!.value as MethodLocation?

    fun peekParameter(parameterRegister: Int): HeapItem? {
        val item: HeapItem?
        item = if (mutableParameters.contains(parameterRegister)) {
            peekRegister(parameterRegister, MUTABLE_PARAMETER_HEAP)
        } else {
            peekRegister(parameterRegister)
        }
        return item
    }

    fun peekRegister(register: Int): HeapItem? {
        if (register == ResultRegister) {
            if (!hasRegister(register, METHOD_HEAP)) {
                if (parent != null && !getParent().hasRegister(register, METHOD_HEAP)) {
                    // ResultRegister can only be read by the instruction immediately after it's set.
                    // It's not in this instruction or its parent, so it effectively doesn't exist.
                    // log.warn("Attempting to read result register but it's not in current or parent context!
                    // Returning null.");
                    return null
                }
            }
        }
        return peekRegister(register, METHOD_HEAP)
    }

    fun peekExceptionRegister(): HeapItem? {
        return peekRegister(ExceptionRegister)
    }

    fun peekResultRegister(): HeapItem? {
        return peekRegister(ResultRegister)
    }

    fun peekReturnRegister(): HeapItem? {
        return peekRegister(ReturnRegister)
    }

    fun peekThrowRegister(): HeapItem? {
        return peekRegister(ThrowRegister)
    }

    fun pokeRegister(register: Int, item: HeapItem?) {
        pokeRegister(register, item, METHOD_HEAP)
    }

    fun pokeRegister(register: Int, value: Any?, type: String?) {
        pokeRegister(register, HeapItem(value, type))
    }

    fun readRegister(register: Int): HeapItem {
        return readRegister(register, METHOD_HEAP)
    }

    fun readResultRegister(): HeapItem {
        return readRegister(ResultRegister, METHOD_HEAP)
    }

    fun readReturnRegister(): HeapItem {
        return readRegister(ReturnRegister)
    }

    fun setPseudoInstructionReturnLocation(location: MethodLocation?) {
        // Pseudo instructions like array-data-payload need return addresses.
        pokeRegister(ReturnAddressRegister, location, METHOD_HEAP)
    }

    override fun toString(): String {
        return toString(true)
    }

    fun toString(onlyPeekCachedRegisters: Boolean): String {
        val sb = StringBuilder()
        val localsCount = registerCount - parameterCount
        sb.append("params: ").append(parameterCount).append(", ")
        sb.append("locals: ").append(localsCount).append('\n')
        val ctx = StringBuilder()
        var register = 0
        while (register < registerCount) {
            if (onlyPeekCachedRegisters && !hasRegister(register)) {
                register += 1
                continue
            }
            ctx.append('v').append(register)
            val isLocal = register < parameterStart
            if (!isLocal) {
                ctx.append("(p").append(register - parameterStart).append(')')
            }
            val item = peekRegister(register)
            ctx.append(": ").append(item).append('\n')
            val registerSize = if (item == null) 1 else Utils.getRegisterSize(item.type)
            register += registerSize
        }
        if (hasRegister(ResultRegister)) {
            ctx.append("result: ").append(peekRegister(ResultRegister)).append('\n')
        }
        if (hasRegister(ReturnRegister)) {
            ctx.append("return: ").append(peekRegister(ReturnRegister)).append('\n')
        }
        if (ctx.length > 0) {
            ctx.setLength(ctx.length - 1)
        }
        sb.append(ctx)
        return sb.toString()
    }

    fun wasRegisterRead(register: Int): Boolean {
        return wasRegisterRead(register, METHOD_HEAP)
    }

    fun getChild(childContext: ExecutionContext): MethodState {
        return MethodState(this, childContext, mutableParameters)
    }

    fun hasRegister(register: Int): Boolean {
        return hasRegister(register, METHOD_HEAP)
    }

    companion object {
        const val ResultRegister = -1
        const val ReturnRegister = -2
        const val ReturnAddressRegister = -3
        const val ThrowRegister = -4
        const val ExceptionRegister = -5
        const val MUTABLE_PARAMETER_HEAP = "mutable"
        const val METHOD_HEAP = "method"
        private val log = LoggerFactory.getLogger(MethodState::class.java.simpleName)
    }
}
