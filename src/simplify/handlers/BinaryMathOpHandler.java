package simplify.handlers;

import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;
import org.jf.dexlib2.iface.instruction.formats.Instruction22b;
import org.jf.dexlib2.iface.instruction.formats.Instruction22s;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

import simplify.Main;
import simplify.vm.MethodContext;
import simplify.vm.UnknownValue;

public class BinaryMathOpHandler extends OpHandler {

    private static enum MathOperandType {
        INT,
        LONG,
        FLOAT,
        DOUBLE
    }

    private static enum MathOperator {
        ADD,
        SUB,
        MUL,
        DIV,
        REM,
        AND,
        OR,
        XOR,
        SHR,
        SHL,
        USHR,
        RSUB
    };

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());;

    private static Object doOperation(MathOperator mathOperator, Double lhs, Double rhs) {
        Object result = null;
        switch (mathOperator) {
        case ADD:
            result = lhs + rhs;
            break;
        case DIV:
            result = lhs / rhs;
            break;
        case MUL:
            result = lhs * rhs;
            break;
        case REM:
            result = lhs % rhs;
            break;
        case SUB:
            result = lhs - rhs;
            break;
        default:
            break;
        }

        return result;
    }

    private static Object doOperation(MathOperator mathOperator, Float lhs, Float rhs) {
        Object result = null;
        switch (mathOperator) {
        case ADD:
            result = lhs + rhs;
            break;
        case DIV:
            result = lhs / rhs;
            break;
        case MUL:
            result = lhs * rhs;
            break;
        case REM:
            result = lhs % rhs;
            break;
        case SUB:
            result = lhs - rhs;
            break;
        default:
            break;
        }

        return result;
    }

    private static Object doOperation(MathOperator mathOperator, Integer lhs, Integer rhs) {
        Object result = null;
        switch (mathOperator) {
        case ADD:
            result = lhs + rhs;
            break;
        case AND:
            result = lhs & rhs;
            break;
        case DIV:
            result = lhs / rhs;
            break;
        case MUL:
            result = lhs * rhs;
            break;
        case OR:
            result = lhs | rhs;
            break;
        case REM:
            result = lhs % rhs;
            break;
        case RSUB:
            result = rhs - lhs;
            break;
        case SHL:
            result = lhs << (rhs & 0x1f);
            break;
        case SHR:
            result = lhs >> (rhs & 0x1f);
            break;
        case SUB:
            result = lhs - rhs;
            break;
        case USHR:
            result = lhs >>> (rhs & 0x1f);
            break;
        case XOR:
            result = lhs ^ rhs;
            break;
        default:
            break;
        }

        return result;
    }

    private static Object doOperation(MathOperator mathOperator, Long lhs, Long rhs) {
        Object result = null;
        switch (mathOperator) {
        case ADD:
            result = lhs + rhs;
            break;
        case AND:
            result = lhs & rhs;
            break;
        case DIV:
            result = lhs / rhs;
            break;
        case MUL:
            result = lhs * rhs;
            break;
        case OR:
            result = lhs | rhs;
            break;
        case REM:
            result = lhs % rhs;
            break;
        case RSUB:
            result = rhs - lhs;
            break;
        case SHL:
            result = lhs << rhs;
            break;
        case SHR:
            result = lhs >> rhs;
            break;
        case SUB:
            result = lhs - rhs;
            break;
        case USHR:
            result = lhs >>> rhs;
            break;
        case XOR:
            result = lhs ^ rhs;
            break;
        default:
            break;
        }

        return result;
    }

    private static MathOperator getMathOp(String opName) {
        MathOperator result = null;
        if (opName.startsWith("add")) {
            result = MathOperator.ADD;
        } else if (opName.startsWith("sub")) {
            result = MathOperator.SUB;
        } else if (opName.startsWith("mul")) {
            result = MathOperator.MUL;
        } else if (opName.startsWith("div")) {
            result = MathOperator.DIV;
        } else if (opName.startsWith("rem")) {
            result = MathOperator.REM;
        } else if (opName.startsWith("and")) {
            result = MathOperator.AND;
        } else if (opName.startsWith("or")) {
            result = MathOperator.OR;
        } else if (opName.startsWith("xor")) {
            result = MathOperator.XOR;
        } else if (opName.startsWith("shl")) {
            result = MathOperator.SHL;
        } else if (opName.startsWith("shr")) {
            result = MathOperator.SHR;
        } else if (opName.startsWith("ushr")) {
            result = MathOperator.USHR;
        } else if (opName.startsWith("rsub")) {
            result = MathOperator.RSUB;
        }

        return result;
    }

    private static MathOperandType getMathOperandType(String opName) {
        MathOperandType result = null;
        if (opName.contains("-int")) {
            result = MathOperandType.INT;
        } else if (opName.contains("-double")) {
            result = MathOperandType.DOUBLE;
        } else if (opName.contains("-float")) {
            result = MathOperandType.FLOAT;
        } else if (opName.contains("-long")) {
            result = MathOperandType.LONG;
        }

        return result;
    }

    static BinaryMathOpHandler create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();
        TwoRegisterInstruction instr = (TwoRegisterInstruction) instruction;
        int destRegister = instr.getRegisterA();
        int arg1Register = instr.getRegisterB();

        BinaryMathOpHandler result = null;
        if (instruction instanceof Instruction23x) {
            // add-int vAA, vBB, vCC
            int arg2Register = ((Instruction23x) instruction).getRegisterC();
            result = new BinaryMathOpHandler(address, opName, childAddress, destRegister, arg1Register, arg2Register,
                            false);
        } else if (instruction instanceof Instruction12x) {
            // add-int/2addr vAA, vBB
            int arg2Register = ((Instruction12x) instruction).getRegisterB();
            result = new BinaryMathOpHandler(address, opName, childAddress, destRegister, arg1Register, arg2Register,
                            false);
        } else if (instruction instanceof Instruction22b) {
            // add-int/lit8 vAA, vBB, #CC
            int arg2Literal = ((Instruction22b) instruction).getNarrowLiteral();
            result = new BinaryMathOpHandler(address, opName, childAddress, destRegister, arg1Register, arg2Literal,
                            true);
        } else if (instruction instanceof Instruction22s) {
            // add-int/lit16 vAA, vBB, #CCCC
            long arg2Literal = ((Instruction22s) instruction).getWideLiteral();
            result = new BinaryMathOpHandler(address, opName, childAddress, destRegister, arg1Register, arg2Literal);
        }

        return result;
    }

    private final int address;
    private final String opName;
    private final int childAddress;
    private final MathOperator mathOperator;
    private final MathOperandType mathOperandType;
    private final int destRegister;
    private final int arg1Register;
    private int arg2Register;
    private int narrowLiteral;
    private long wideLiteral;
    private boolean hasWideLiteral;
    private boolean hasNarrowLiteral;

    private BinaryMathOpHandler(int address, String opName, int childAddress, int destRegister, int arg1Register) {
        this.address = address;
        this.opName = opName;
        this.childAddress = childAddress;
        this.destRegister = destRegister;
        this.arg1Register = arg1Register;

        mathOperator = getMathOp(opName);
        mathOperandType = getMathOperandType(opName);
    }

    private BinaryMathOpHandler(int address, String opName, int childAddress, int destRegister, int arg1Register,
                    int otherValue, boolean hasLiteral) {
        this(address, opName, childAddress, destRegister, arg1Register);

        hasNarrowLiteral = hasLiteral;

        if (hasNarrowLiteral) {
            narrowLiteral = otherValue;
        } else {
            arg2Register = otherValue;
        }
    }

    private BinaryMathOpHandler(int address, String opName, int childAddress, int destRegister, int arg1Register,
                    long wideLiteral) {
        this(address, opName, childAddress, destRegister, arg1Register);

        this.wideLiteral = wideLiteral;

        hasWideLiteral = true;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        Object lhs = mctx.getRegisterValue(arg1Register, address);
        Object rhs = null;
        if (hasWideLiteral) {
            rhs = wideLiteral;
        } else if (hasNarrowLiteral) {
            rhs = narrowLiteral;
        } else {
            rhs = mctx.getRegisterValue(arg2Register, address);
        }

        System.out.println("mctx now: " + mctx);
        log.finest(mathOperator + " - " + mathOperandType + " lhs:" + lhs + ", rhs:" + rhs);

        Object result = new UnknownValue();
        if (!(lhs instanceof UnknownValue) && !(rhs instanceof UnknownValue)) {
            result = getResult(lhs, rhs);

            if (result == null) {
                log.warning("Null result in binary math. Not possibruuu!");
            }
        }

        // Destination register should be same as lhs op
        String type = mctx.peekRegisterType(arg1Register);
        mctx.setRegister(destRegister, type, result, address);

        mctx.getRegister(arg1Register, address);
        if (!hasWideLiteral && !hasNarrowLiteral) {
            mctx.getRegister(arg2Register, address);
        }

        System.out.println("and NOW: " + mctx);

        return getPossibleChildren();
    }

    @Override
    public int getAddress() {
        return address;
    }

    @Override
    public int[] getPossibleChildren() {
        return new int[] { childAddress };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opName);

        sb.append(" r").append(destRegister).append(", ");
        // if (destRegister != arg1Register) {
        sb.append("r").append(arg1Register).append(", ");
        // }

        if (hasNarrowLiteral) {
            sb.append("0x").append(Integer.toHexString(narrowLiteral));
        } else if (hasWideLiteral) {
            sb.append("0x").append(Long.toHexString(wideLiteral));
        } else {
            sb.append("r").append(arg2Register);
        }

        return sb.toString();
    }

    private Object getResult(Object lhs, Object rhs) {
        Object result = null;

        // TODO: shifts are different from this : http://source.android.com/devices/tech/dalvik/dalvik-bytecode.html
        // test shifts carefully
        switch (mathOperandType) {
        case INT:
            result = doOperation(mathOperator, (Integer) lhs, (Integer) rhs);
            break;
        case LONG:
            result = doOperation(mathOperator, (Long) lhs, (Long) rhs);
            break;
        case FLOAT:
            result = doOperation(mathOperator, (Float) lhs, (Float) rhs);
            break;
        case DOUBLE:
            result = doOperation(mathOperator, (Double) lhs, (Double) rhs);
            break;
        default: // return null and it'll get caught later
            break;
        }

        return result;
    }
}
