package org.cf.smalivm.context;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class MethodState extends BaseState {

    public static final int ResultRegister = -1;
    public static final int ReturnRegister = -2;
    public static final int ReturnAddressRegister = -3;
    public static final int ThrowRegister = -4;
    public static final int ExceptionRegister = -5;

    public static final String MUTABLE_PARAMETER_HEAP = "mutable";
    public static final String METHOD_HEAP = "method";
    private static final Logger log = LoggerFactory.getLogger(MethodState.class.getSimpleName());
    private final int parameterCount;
    private final int parameterSize;
    private final TIntSet mutableParameters;

    public MethodState(ExecutionContext context, int registerCount) {
        this(context, registerCount, 0, 0);
    }

    public MethodState(ExecutionContext context, int registerCount, int parameterCount, int parameterSize) {
        super(context, registerCount);

        this.parameterCount = parameterCount;
        this.parameterSize = parameterSize;
        mutableParameters = new TIntHashSet(parameterCount);
    }

    MethodState(MethodState other, ExecutionContext context) {
        super(other, context);

        this.parameterCount = other.parameterCount;
        this.parameterSize = other.parameterSize;
        mutableParameters = new TIntHashSet(other.mutableParameters);
    }

    private MethodState(MethodState parent, ExecutionContext context, TIntSet mutableParameters) {
        super(parent, context);

        this.parameterCount = parent.parameterCount;
        this.parameterSize = parent.parameterSize;
        this.mutableParameters = parent.mutableParameters;
    }

    public void assignExceptionRegister(Throwable exception) {
        String className = ClassNameUtils.toInternal(exception.getClass());
        HeapItem item = new HeapItem(exception, className);
        pokeRegister(ExceptionRegister, item, METHOD_HEAP);
    }

    public void assignParameter(int parameterRegister, HeapItem item) {
        assignRegister(parameterRegister, item, METHOD_HEAP);

        boolean mutable = !item.isImmutable();
        if (mutable) {
            pokeRegister(parameterRegister, item, MUTABLE_PARAMETER_HEAP);
            mutableParameters.add(parameterRegister);
        }
    }

    public void assignParameter(int parameterRegister, Object value, String type) {
        assignParameter(parameterRegister, new HeapItem(value, type));
    }

    public void assignRegister(int register, HeapItem item) {
        assignRegister(register, item, METHOD_HEAP);
    }

    public void assignRegister(int register, Object value, String type) {
        assignRegister(register, new HeapItem(value, type));
    }

    /**
     * Identical to {@link #assignRegister(int, HeapItem)} but also updates any register with an identical value to that
     * stored in the target register with the new value.
     */
    public void assignRegisterAndUpdateIdentities(int register, HeapItem item) {
        assignRegisterAndUpdateIdentities(register, item, METHOD_HEAP);
    }

    public void assignResultRegister(HeapItem item) {
        assignRegister(ResultRegister, item, METHOD_HEAP);
    }

    public void assignResultRegister(Object value, String type) {
        assignRegister(ResultRegister, new HeapItem(value, type));
    }

    public void assignReturnRegister(HeapItem item) {
        pokeRegister(ReturnRegister, item, METHOD_HEAP);
    }

    public void assignReturnRegister(Object value, String type) {
        pokeRegister(ReturnRegister, new HeapItem(value, type), METHOD_HEAP);
    }

    public void assignThrowRegister(HeapItem item) {
        pokeRegister(ThrowRegister, item, METHOD_HEAP);
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public int getParameterStart() {
        return getRegisterCount() - parameterSize;
    }

    @Override
    public MethodState getParent() {
        return (MethodState) super.getParent();
    }

    public MethodLocation getPseudoInstructionReturnInstruction() {
        return (MethodLocation) peekRegister(ReturnAddressRegister).getValue();
    }

    public HeapItem peekExceptionRegister() {
        return peekRegister(ExceptionRegister);
    }

    public HeapItem peekParameter(int parameterRegister) {
        HeapItem item;
        if (mutableParameters.contains(parameterRegister)) {
            item = peekRegister(parameterRegister, MUTABLE_PARAMETER_HEAP);
        } else {
            item = peekRegister(parameterRegister);
        }

        return item;
    }

    public
    @Nullable
    HeapItem peekRegister(int register) {
        if (register == MethodState.ResultRegister) {
            if (!hasRegister(register, METHOD_HEAP)) {
                if (getParent() != null && !getParent().hasRegister(register, METHOD_HEAP)) {
                    // ResultRegister can only be read by the instruction immediately after it's set.
                    // It's not in this instruction or its parent, so it effectively doesn't exist.
                    // log.warn("Attempting to read result register but it's not in current or parent context!
                    // Returning null.");
                    return null;
                }
            }
        }

        return peekRegister(register, METHOD_HEAP);
    }

    public HeapItem peekResultRegister() {
        return peekRegister(ResultRegister);
    }

    public HeapItem peekReturnRegister() {
        return peekRegister(ReturnRegister);
    }

    public HeapItem peekThrowRegister() {
        return peekRegister(ThrowRegister);
    }

    public void pokeRegister(int register, HeapItem item) {
        pokeRegister(register, item, METHOD_HEAP);
    }

    public void pokeRegister(int register, Object value, String type) {
        pokeRegister(register, new HeapItem(value, type));
    }

    public HeapItem readRegister(int register) {
        return readRegister(register, METHOD_HEAP);
    }

    public HeapItem readResultRegister() {
        HeapItem item = readRegister(ResultRegister, METHOD_HEAP);

        return item;
    }

    public HeapItem readReturnRegister() {
        return readRegister(ReturnRegister);
    }

    public void setPseudoInstructionReturnLocation(MethodLocation location) {
        // Pseudo instructions like array-data-payload need return addresses.
        pokeRegister(ReturnAddressRegister, location, METHOD_HEAP);
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean onlyPeekCachedRegisters) {
        StringBuilder sb = new StringBuilder();
        int localsCount = getRegisterCount() - getParameterCount();
        sb.append("params: ").append(parameterCount).append(", ");
        sb.append("locals: ").append(localsCount).append('\n');
        StringBuilder ctx = new StringBuilder();
        for (int register = 0; register < getRegisterCount(); register++) {
            if (onlyPeekCachedRegisters && !hasRegister(register)) {
                continue;
            }

            ctx.append('v').append(register);
            boolean isLocal = register < getParameterStart();
            if (!isLocal) {
                ctx.append("(p").append(register - getParameterStart()).append(')');
            }
            HeapItem item = peekRegister(register);
            ctx.append(": ").append(item).append('\n');

            register += Utils.getRegisterSize(item.getType());
            if (Utils.getRegisterSize(item.getType()) == 2) {
                register += 1;
            }
        }

        if (onlyPeekCachedRegisters && hasRegister(ResultRegister)) {
            ctx.append("result: ").append(peekRegister(ResultRegister)).append('\n');
        }

        if (onlyPeekCachedRegisters && hasRegister(ReturnRegister)) {
            ctx.append("return: ").append(peekRegister(ReturnRegister)).append('\n');
        }

        if (ctx.length() > 0) {
            ctx.setLength(ctx.length() - 1);
        }
        sb.append(ctx);

        return sb.toString();
    }

    public boolean wasRegisterRead(int register) {
        return wasRegisterRead(register, METHOD_HEAP);
    }

    MethodState getChild(ExecutionContext childContext) {
        MethodState child = new MethodState(this, childContext, mutableParameters);

        return child;
    }

    boolean hasRegister(int register) {
        return hasRegister(register, METHOD_HEAP);
    }

}
