package simplify.vm.ops;

import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.util.ReferenceUtil;

import simplify.Main;
import simplify.vm.ClassContext;
import simplify.vm.MethodContext;
import simplify.vm.VirtualMachine;

public class SGetOp extends Op {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    static SGetOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction21c instr = (Instruction21c) instruction;
        int destRegister = instr.getRegisterA();
        FieldReference reference = (FieldReference) instr.getReference();
        String fieldDescriptor = ReferenceUtil.getFieldDescriptor(reference);
        String parts[] = fieldDescriptor.split("->");
        String className = parts[0];
        String fieldReference = parts[1];
        ClassContext classCtx = vm.peekClassContext(className);

        return new SGetOp(address, opName, childAddress, destRegister, classCtx, className, fieldReference);
    }

    private final int destRegister;
    private final ClassContext classCtx;
    private final String className;
    private final String fieldReference;
    private final String fieldDescriptor;

    public SGetOp(int address, String opName, int childAddress, int destRegister, ClassContext classCtx,
                    String className, String fieldReference) {
        super(address, opName, childAddress);

        this.destRegister = destRegister;
        this.classCtx = classCtx;
        this.className = className;
        this.fieldReference = fieldReference;
        this.fieldDescriptor = className + "->" + fieldReference;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        String fieldReference = fieldDescriptor.split("->")[1];
        Object value = classCtx.readField(fieldReference);
        mctx.assignRegister(destRegister, value);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        sb.append(" r").append(destRegister).append(", ").append(fieldDescriptor);

        return sb.toString();
    }

}
