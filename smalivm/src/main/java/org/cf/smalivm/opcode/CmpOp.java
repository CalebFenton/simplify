package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmpOp extends MethodStateOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(CmpOp.class.getSimpleName());

    private final int destRegister;
    private final int lhsRegister;
    private final int rhsRegister;

    CmpOp(MethodLocation location, MethodLocation child, int destRegister, int lhsRegister,
          int rhsRegister) {
        super(location, child);

        this.destRegister = destRegister;
        this.lhsRegister = lhsRegister;
        this.rhsRegister = rhsRegister;
    }

    public int getDestRegister() {
        return destRegister;
    }

    public int getLhsRegister() {
        return lhsRegister;
    }

    public int getRhsRegister() {
        return rhsRegister;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem lhsItem = mState.readRegister(lhsRegister);
        HeapItem rhsItem = mState.readRegister(rhsRegister);

        HeapItem item;
        if (lhsItem.isUnknown() || rhsItem.isUnknown()) {
            item = HeapItem.newUnknown("I");
        } else {
            Number lhs = (Number) lhsItem.getValue();
            Number rhs = (Number) rhsItem.getValue();

            assert lhs.getClass() == rhs.getClass();
            assert lhsItem.getType().equals(rhsItem.getType());

            int cmp = cmp(lhs, rhs);
            item = new HeapItem(cmp, "I");
        }

        mState.assignRegister(destRegister, item);
    }

    @Override
    public String toString() {
        return getName() + " r" + destRegister + ", r" + lhsRegister + ", r" + rhsRegister;
    }

    private int cmp(Number val1, Number val2) {
        boolean arg1IsNan = val1 instanceof Float && ((Float) val1)
                .isNaN() || val1 instanceof Double && ((Double) val1).isNaN();
        boolean arg2IsNan = val2 instanceof Float && ((Float) val2)
                .isNaN() || val2 instanceof Double && ((Double) val2).isNaN();

        int value = 0;
        if (arg1IsNan || arg2IsNan) {
            if (getName().startsWith("cmpg")) {
                value = 1;
            } else { // cmpl
                value = -1;
            }
        } else {
            if (getName().endsWith("float")) {
                Float castVal1 = Utils.getFloatValue(val1);
                Float castVal2 = Utils.getFloatValue(val2);
                // The docs say "b == c" but I don't think they mean identity.
                value = Float.compare(castVal1, castVal2);
            } else if (getName().endsWith("double")) {
                Double castVal1 = Utils.getDoubleValue(val1);
                Double castVal2 = Utils.getDoubleValue(val2);
                // The docs say "b == c" but I don't think they mean identity.
                value = Double.compare(castVal1, castVal2);
            } else {
                Long castVal1 = Utils.getLongValue(val1);
                Long castVal2 = Utils.getLongValue(val2);
                value = Long.compare(castVal1, castVal2);
            }
        }

        return value;
    }

}
