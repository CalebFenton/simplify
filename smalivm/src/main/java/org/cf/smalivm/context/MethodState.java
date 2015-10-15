package org.cf.smalivm.context;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodState extends BaseState {

    private static final Logger log = LoggerFactory.getLogger(MethodState.class.getSimpleName());

    public static final int ResultRegister = -1;
    public static final int ReturnRegister = -2;
    public static final int ReturnAddress = -3;

    public static final String MUTABLE_PARAMETER_HEAP = "mutable";
    // TODO: refactor ExecutionContext's method descriptor here, it saves having an extra string * n ops
    public static final String METHOD_HEAP = "method";

    private final int parameterCount;
    private final int parameterSize;
    private final TIntSet mutableParameters;

    public MethodState(ExecutionContext ectx, int registerCount) {
        this(ectx, registerCount, 0, 0);
    }

    public MethodState(ExecutionContext ectx, int registerCount, int parameterCount, int parameterSize) {
        super(ectx, registerCount);

        this.parameterCount = parameterCount;
        this.parameterSize = parameterSize;
        mutableParameters = new TIntHashSet(parameterCount);
    }

    private MethodState(MethodState parent, ExecutionContext ectx, TIntSet mutableParameters) {
        super(parent, ectx);

        this.parameterCount = parent.parameterCount;
        this.parameterSize = parent.parameterSize;
        this.mutableParameters = parent.mutableParameters;
    }

    MethodState(MethodState other, ExecutionContext ectx) {
        super(other, ectx);

        this.parameterCount = other.parameterCount;
        this.parameterSize = other.parameterSize;
        mutableParameters = new TIntHashSet(other.mutableParameters);
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
        super.assignRegister(register, item, METHOD_HEAP);
    }

    public void assignRegister(int register, Object value, String type) {
        assignRegister(register, new HeapItem(value, type));
    }

    /**
     * Identical to {@link #assignRegister(int, HeapItem)} but also updates any register with an identical value to that
     * stored in the target register with the new value.
     *
     * @param register
     * @param item
     */
    public void assignRegisterAndUpdateIdentities(int register, HeapItem item) {
        super.assignRegisterAndUpdateIdentities(register, item, METHOD_HEAP);
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
        return (MethodLocation) peekRegister(ReturnAddress).getValue();
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

    public HeapItem peekRegister(int register) {
        if (register == MethodState.ResultRegister) {
            if (!hasRegister(register, METHOD_HEAP)) {
                if (getParent() != null && !getParent().hasRegister(register, METHOD_HEAP)) {
                    // ResultRegister can only be read by the instruction immediately after it's set.
                    // It's not in this instruction or its parent, so it effectively doesn't exist.
                    // log.warn("Attempting to read result register but it's not in current or parent context! Returning null.");
                    return null;
                }

            }
        }
        return super.peekRegister(register, METHOD_HEAP);
    }

    public void pokeRegister(int register, HeapItem item) {
        super.pokeRegister(register, item, METHOD_HEAP);
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
        return peekRegister(ReturnRegister);
    }

    public void setPseudoInstructionReturnLocation(MethodLocation location) {
        // Pseudo instructions like array-data-payload need return addresses.
        pokeRegister(ReturnAddress, location, METHOD_HEAP);
    }

    boolean hasRegister(int register) {
        return hasRegister(register, METHOD_HEAP);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int localsCount = getRegisterCount() - getParameterCount();
        sb.append("params: ").append(parameterCount).append(", ");
        sb.append("locals: ").append(localsCount).append('\n');
        StringBuilder ctx = new StringBuilder();
        for (int register = 0; register < getRegisterCount(); register++) {
            if (!hasRegister(register)) {
                continue;
            }

            boolean isLocal = register < getParameterStart();
            if (!isLocal) {
                ctx.append('p').append(register - getParameterStart());
            } else {
                ctx.append('v').append(register);
            }
            HeapItem item = peekRegister(register);
            ctx.append(": ").append(item).append('\n');

            if (Utils.getRegisterSize(item.getType()) == 2) {
                register += 1;
            }
        }

        if (hasRegister(ResultRegister)) {
            ctx.append("result: ").append(peekRegister(ResultRegister)).append('\n');
        }

        if (hasRegister(ReturnRegister)) {
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

}
