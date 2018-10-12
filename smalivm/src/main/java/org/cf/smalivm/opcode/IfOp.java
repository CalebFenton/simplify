package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(IfOp.class.getSimpleName());
    private final IfType ifType;
    private final int register1;
    private final MethodLocation target;
    private boolean compareToZero;
    private int register2;

    IfOp(MethodLocation location, MethodLocation child, IfType ifType, MethodLocation target, int register1) {
        super(location, new MethodLocation[] { child, target });
        this.ifType = ifType;
        this.target = target;
        this.register1 = register1;
        compareToZero = true;
    }

    IfOp(MethodLocation location, MethodLocation child, IfType ifType, MethodLocation target, int register1,
         int register2) {
        this(location, child, ifType, target, register1);
        this.register2 = register2;
        compareToZero = false;
    }

    private static boolean isTrue(IfType ifType, int cmp) {
        boolean isTrue = false;
        switch (ifType) {
            case EQUAL:
                isTrue = cmp == 0;
                break;
            case GREATER:
                isTrue = cmp == 1;
                break;
            case GREATER_OR_EQUAL:
                isTrue = cmp >= 0;
                break;
            case LESS:
                isTrue = cmp == -1;
                break;
            case LESS_OR_EQUAL:
                isTrue = cmp <= 0;
                break;
            case NOT_EQUAL:
                isTrue = cmp != 0;
                break;
        }

        return isTrue;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem lhsItem = mState.readRegister(register1);
        HeapItem rhsItem = compareToZero ? new HeapItem(0, "I") : mState.readRegister(register2);

        // Ambiguous predicate. Return to add both possible branches as children.
        if (lhsItem.isUnknown() || rhsItem.isUnknown()) {
            return;
        }

        Object lhs = lhsItem.getValue();
        Object rhs = rhsItem.getValue();
        int cmp;
        if (compareToZero) {
            if (lhs == null) {
                // if-*z ops are used to check for null refs
                cmp = 0;
            } else if ((lhs instanceof Number || lhs instanceof Boolean || lhs instanceof Character) &&
                       (rhs instanceof Number || rhs instanceof Boolean || rhs instanceof Character)) {
                Integer aIntValue = Utils.getIntegerValue(lhs);
                cmp = aIntValue.compareTo((Integer) rhs);
            } else {
                cmp = lhs == rhs ? 0 : 1;
            }
        } else if ((lhs instanceof Number || lhs instanceof Boolean || lhs instanceof Character) &&
                   (rhs instanceof Number || rhs instanceof Boolean || rhs instanceof Character)) {
            Integer aIntValue = Utils.getIntegerValue(lhs);
            Integer bIntValue = Utils.getIntegerValue(rhs);
            cmp = aIntValue.compareTo(bIntValue);
        } else {
            cmp = lhs == rhs ? 0 : 1;
        }

        if (log.isTraceEnabled()) {
            log.trace("IF compare: {} vs {} = {}", lhs, rhs, cmp);
        }

        int childIndex = isTrue(ifType, cmp) ? 1 : 0;
        node.setChildLocations(getChildren()[childIndex]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(register1);
        if (!compareToZero) {
            sb.append(", r").append(register2);
        }
        sb.append(", :addr_").append(target.getCodeAddress());

        return sb.toString();
    }

    enum IfType {
        EQUAL, GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL, NOT_EQUAL
    }

}
