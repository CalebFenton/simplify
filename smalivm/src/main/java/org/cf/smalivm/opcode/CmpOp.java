package org.cf.smalivm.opcode;

import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmpOp extends MethodStateOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(CmpOp.class.getSimpleName());

    static CmpOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction23x instr = (Instruction23x) instruction;
        int destRegister = instr.getRegisterA();
        int arg1Register = instr.getRegisterB();
        int arg2Register = instr.getRegisterC();

        return new CmpOp(address, opName, childAddress, destRegister, arg1Register, arg2Register);
    }

    private final int destRegister;
    private final int arg1Register;
    private final int arg2Register;

    public CmpOp(int address, String opName, int childAddress, int destRegister, int arg1Register, int arg2Register) {
        super(address, opName, childAddress);

        this.destRegister = destRegister;
        this.arg1Register = arg1Register;
        this.arg2Register = arg2Register;
    }

    @Override
    public int[] execute(MethodState mState) {
        Object arg1 = mState.readRegister(arg1Register);
        Object arg2 = mState.readRegister(arg2Register);

        Object value;
        if ((arg1 instanceof UnknownValue) || (arg2 instanceof UnknownValue)) {
            if (getName().endsWith("float")) {
                value = new UnknownValue("F");
            } else if (getName().endsWith("double")) {
                value = new UnknownValue("D");
            } else {
                value = new UnknownValue("J");
            }
        } else {
            assert arg1 instanceof Number;
            assert arg2 instanceof Number;
            assert arg1.getClass() == arg2.getClass();

            value = cmp((Number) arg1, (Number) arg2);
        }

        mState.assignRegister(destRegister, value);

        return getPossibleChildren();
    }

    private int cmp(Number val1, Number val2) {
        boolean arg1IsNan = ((val1 instanceof Float) && ((Float) val1).isNaN())
                        || ((val1 instanceof Double) && ((Double) val1).isNaN());
        boolean arg2IsNan = ((val2 instanceof Float) && ((Float) val2).isNaN())
                        || ((val2 instanceof Double) && ((Double) val2).isNaN());

        int value = 0;
        if (arg1IsNan || arg2IsNan) {
            if (getName().startsWith("cmpg")) {
                value = 1;
            } else { // cmpl
                value = -1;
            }
        } else {
            if (getName().endsWith("float")) {
                Float castVal1 = (Float) val1;
                Float castVal2 = (Float) val2;
                if (castVal1.equals(castVal2)) {
                    // The docs say "b == c" but I don't think they mean identity.
                    value = 0;
                } else if (castVal1 > castVal2) {
                    value = 1;
                } else if (castVal1 < castVal2) {
                    value = -1;
                }
            } else if (getName().endsWith("double")) {
                Double castVal1 = (Double) val1;
                Double castVal2 = (Double) val2;
                if (castVal1.equals(castVal2)) {
                    // The docs say "b == c" but I don't think they mean identity.
                    value = 0;
                } else if (castVal1 > castVal2) {
                    value = 1;
                } else if (castVal1 < castVal2) {
                    value = -1;
                }
            } else {
                Long castVal1 = (Long) val1;
                Long castVal2 = (Long) val2;
                if (castVal1 == castVal2) {
                    value = 0;
                } else if (castVal1 > castVal2) {
                    value = 1;
                } else if (castVal1 < castVal2) {
                    value = -1;
                }
            }
        }

        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", r").append(arg1Register).append(", r").append(arg2Register);

        return sb.toString();
    }

}
