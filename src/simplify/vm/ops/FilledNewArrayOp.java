package simplify.vm.ops;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;
import org.jf.dexlib2.util.ReferenceUtil;

import simplify.vm.MethodContext;
import simplify.vm.VirtualMachine;

public class FilledNewArrayOp extends Op {

    static FilledNewArrayOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        String typeReference = null;
        int[] dimensionRegisters = null;
        if (opName.endsWith("/range")) {
            Instruction3rc instr = (Instruction3rc) instruction;
            typeReference = ReferenceUtil.getReferenceString(instr.getReference());
            dimensionRegisters = new int[instr.getRegisterCount()];
            int startRegister = instr.getStartRegister();
            for (int i = 0; i < dimensionRegisters.length; i++) {
                dimensionRegisters[i] = startRegister + i;
            }
        } else {
            Instruction35c instr = (Instruction35c) instruction;
            typeReference = ReferenceUtil.getReferenceString(instr.getReference());

            dimensionRegisters = new int[instr.getRegisterCount()];
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

        String baseClassName = typeReference.replace("[", "");
        boolean isLocalClass = vm.isClassDefinedLocally(baseClassName);

        return new FilledNewArrayOp(address, opName, childAddress, dimensionRegisters, typeReference, isLocalClass);
    }

    private final int dimensionRegisters[];
    private final String typeReference;
    private final boolean isLocalClass;

    private FilledNewArrayOp(int address, String opName, int childAddress, int[] dimensionRegisters,
                    String typeReference, boolean isLocalClass) {
        super(address, opName, childAddress);

        this.dimensionRegisters = dimensionRegisters;
        this.typeReference = typeReference;
        this.isLocalClass = isLocalClass;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        /*
         * The array type is always [I. This only populates the array with values from paramters. It does NOT create
         * n-dimensional arrays, just the parameter for reflect.Arrays.newInstance(). If you use anything but [I the
         * code fails verification and a few decompilers (not disassemblers) choke.
         */
        int[] dimensions = new int[dimensionRegisters.length];
        for (int i = 0; i < dimensionRegisters.length; i++) {
            int register = dimensionRegisters[i];
            int dimension = (int) mctx.readRegister(register);
            dimensions[i] = dimension;
        }

        mctx.assignResultRegister(dimensions);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());

        sb.append("{");
        if (dimensionRegisters.length > 5) {
            sb.append("r").append(dimensionRegisters[0]).append(" .. r")
                            .append(dimensionRegisters[dimensionRegisters.length - 1]).append("}, ");
        } else {
            for (int dimensionRegister : dimensionRegisters) {
                sb.append("r").append(dimensionRegister).append(", ");
            }
            sb.setLength(sb.length() - 2);
        }
        sb.append("}, ").append(typeReference);

        return sb.toString();
    }
}
