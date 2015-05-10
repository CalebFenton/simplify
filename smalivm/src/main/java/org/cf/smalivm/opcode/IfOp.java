package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.Utils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22t;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfOp extends MethodStateOp {

    private static enum IfType {
        EQUAL, GREATER, GREATOR_OR_EQUAL, LESS, LESS_OR_EQUAL, NOT_EQUAL
    }

    private static final Logger log = LoggerFactory.getLogger(IfOp.class.getSimpleName());

    private static IfType getIfType(String opName) {
        IfType result = null;
        if (opName.contains("-eq")) {
            result = IfType.EQUAL;
        } else if (opName.contains("-ne")) {
            result = IfType.NOT_EQUAL;
        } else if (opName.contains("-lt")) {
            result = IfType.LESS;
        } else if (opName.contains("-le")) {
            result = IfType.LESS_OR_EQUAL;
        } else if (opName.contains("-gt")) {
            result = IfType.GREATER;
        } else if (opName.contains("-ge")) {
            result = IfType.GREATOR_OR_EQUAL;
        }

        return result;
    }

    private static boolean isTrue(IfType ifType, int cmp) {
        boolean isTrue = false;
        switch (ifType) {
        case EQUAL:
            isTrue = (cmp == 0);
            break;
        case GREATER:
            isTrue = (cmp == 1);
            break;
        case GREATOR_OR_EQUAL:
            isTrue = (cmp >= 0);
            break;
        case LESS:
            isTrue = (cmp == -1);
            break;
        case LESS_OR_EQUAL:
            isTrue = (cmp <= 0);
            break;
        case NOT_EQUAL:
            isTrue = (cmp != 0);
            break;
        }

        return isTrue;
    }

    static IfOp create(Instruction instruction, int address) {
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int targetAddress = address + branchOffset;
        int childAddress = address + instruction.getCodeUnits();

        String opName = instruction.getOpcode().name;
        IfType ifType = getIfType(opName);
        int register1 = ((OneRegisterInstruction) instruction).getRegisterA();

        if (instruction instanceof Instruction22t) {
            // if-* vA, vB, :label
            Instruction22t instr = (Instruction22t) instruction;

            return new IfOp(address, opName, childAddress, ifType, targetAddress, register1, instr.getRegisterB());
        } else {
            // if-*z vA, vB, :label (Instruction 21t)
            return new IfOp(address, opName, childAddress, ifType, targetAddress, register1);
        }
    }

    private boolean compareToZero;
    private final IfType ifType;

    private final int register1;
    private int register2;

    private final int targetAddress;

    private IfOp(int address, String opName, int childAddress, IfType ifType, int targetAddress, int register1) {
        super(address, opName, new int[] { childAddress, targetAddress });

        this.ifType = ifType;
        this.targetAddress = targetAddress;
        this.register1 = register1;
        compareToZero = true;
    }

    private IfOp(int address, String opName, int childAddress, IfType ifType, int targetAddress, int register1,
                    int register2) {
        this(address, opName, childAddress, ifType, targetAddress, register1);
        this.register2 = register2;
        compareToZero = false;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem lhsItem = mState.readRegister(register1);
        HeapItem rhsItem = compareToZero ? new HeapItem(0, "I") : mState.readRegister(register2);

        // Ambiguous predicate. Follow both branches.
        if ((lhsItem.isUnknown()) || (rhsItem.isUnknown())) {
            return;
        }

        Object lhs = lhsItem.getValue();
        Object rhs = rhsItem.getValue();
        int cmp = Integer.MIN_VALUE;
        if (compareToZero) {
            if (lhs == null) {
                // if-*z ops are used to check for null refs
                cmp = lhs == null ? 0 : 1;
            } else if (((lhs instanceof Number) || (lhs instanceof Boolean) || (lhs instanceof Character)) && ((rhs instanceof Number) || (rhs instanceof Boolean) || (rhs instanceof Character))) {
                Integer aIntValue = Utils.getIntegerValue(lhs);
                cmp = aIntValue.compareTo((Integer) rhs);
            } else {
                cmp = lhs == rhs ? 0 : 1;
            }
        } else if (((lhs instanceof Number) || (lhs instanceof Boolean) || (lhs instanceof Character)) && ((rhs instanceof Number) || (rhs instanceof Boolean) || (rhs instanceof Character))) {
            Integer aIntValue = Utils.getIntegerValue(lhs);
            Integer bIntValue = Utils.getIntegerValue(rhs);
            cmp = aIntValue.compareTo(bIntValue);
        } else {
            cmp = lhs == rhs ? 0 : 1;
        }

        if (log.isTraceEnabled()) {
            log.trace("IF compare: " + lhs + " vs " + rhs + " = " + cmp);
        }

        int result = getChildren()[0];
        if (isTrue(ifType, cmp)) {
            result = targetAddress;
        }

        node.setChildAddresses(result);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(register1);
        if (!compareToZero) {
            sb.append(", r").append(register2);
        }
        sb.append(", #").append(targetAddress);

        return sb.toString();
    }

}
