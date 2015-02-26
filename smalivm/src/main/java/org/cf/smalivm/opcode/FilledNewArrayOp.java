package org.cf.smalivm.opcode;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.VariableRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilledNewArrayOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(FilledNewArrayOp.class.getSimpleName());

    static FilledNewArrayOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Reference reference = ((ReferenceInstruction) instruction).getReference();
        String typeReference = ReferenceUtil.getReferenceString(reference);
        int registerCount = ((VariableRegisterInstruction) instruction).getRegisterCount();
        int[] dimensionRegisters = new int[registerCount];
        if (opName.endsWith("/range")) {
            Instruction3rc instr = (Instruction3rc) instruction;
            int startRegister = instr.getStartRegister();
            for (int i = 0; i < dimensionRegisters.length; i++) {
                dimensionRegisters[i] = startRegister + i;
            }
        } else {
            Instruction35c instr = (Instruction35c) instruction;
            switch (dimensionRegisters.length) {
            case 5:
                dimensionRegisters[4] = instr.getRegisterG();
            case 4:
                dimensionRegisters[3] = instr.getRegisterF();
            case 3:
                dimensionRegisters[2] = instr.getRegisterE();
            case 2:
                dimensionRegisters[1] = instr.getRegisterD();
            case 1:
                dimensionRegisters[0] = instr.getRegisterC();
                break;
            default:
                // Shouldn't pass parser if op has >5 registers
            }
        }

        return new FilledNewArrayOp(address, opName, childAddress, dimensionRegisters, typeReference);
    }

    private final int dimensionRegisters[];
    private final String typeReference;

    private FilledNewArrayOp(int address, String opName, int childAddress, int[] dimensionRegisters,
                    String typeReference) {
        super(address, opName, childAddress);

        this.dimensionRegisters = dimensionRegisters;
        this.typeReference = typeReference;
    }

    @Override
    public int[] execute(MethodState mState) {
        /*
         * This populates a 1-dimensional integer array with values from the parameters. It does NOT create
         * n-dimensional arrays. It's usually used to create parameter for Arrays.newInstance(). If you use anything but
         * [I the code fails verification and a few decompilers (not disassemblers) choke.
         */
        int[] dimensions = new int[dimensionRegisters.length];
        boolean foundUnknown = false;
        for (int i = 0; i < dimensionRegisters.length; i++) {
            int register = dimensionRegisters[i];
            HeapItem item = mState.readRegister(register);

            if (foundUnknown) {
                continue;
            }

            Object value = item.getValue();
            if (value instanceof Number) {
                dimensions[i] = ((Number) value).intValue();
            } else {
                if (!(item.isUnknown())) {
                    if (log.isWarnEnabled()) {
                        log.warn("Unexpected value type for " + toString() + ": " + item);
                    }
                }

                // At least one value is unknown. Give up.
                foundUnknown = true;
            }
        }

        if (foundUnknown) {
            mState.assignResultRegister(HeapItem.newUnknown("[I"));
        } else {
            mState.assignResultRegister(dimensions, "[I");
        }

        return getChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" {");
        if (dimensionRegisters.length > 5) {
            sb.append('r').append(dimensionRegisters[0]).append(" .. r")
            .append(dimensionRegisters[dimensionRegisters.length - 1]);
        } else {
            for (int dimensionRegister : dimensionRegisters) {
                sb.append('r').append(dimensionRegister).append(", ");
            }
            sb.setLength(sb.length() - 2);
        }
        sb.append("}, ").append(typeReference);

        return sb.toString();
    }
}
