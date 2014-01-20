package refactor.handler;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;

import refactor.vm.ContextGraph;
import refactor.vm.MethodContext;
import refactor.vm.RegisterStore;
import refactor.vm.VirtualMachine;

public class InvokeOpHandler extends OpHandler {

    private static enum InvokeType {
        REFLECT,
        EMULATE,
        INVOKE
    };

    static InvokeOpHandler create(Instruction instruction, int address, VirtualMachine vm) {
        int childAddress = address + instruction.getCodeUnits();
        String opName = instruction.getOpcode().name;

        int[] registers = null;
        if (opName.contains("/range")) {
            Instruction3rc instr = (Instruction3rc) instruction;
            int registerCount = instr.getRegisterCount();
            int start = instr.getStartRegister();
            int end = start + registerCount;

            registers = new int[registerCount];
            for (int i = start; i < end; i++) {
                registers[i - start] = i;
            }
        } else {
            Instruction35c instr = (Instruction35c) instruction;
            int registerCount = instr.getRegisterCount();

            registers = new int[registerCount];
            switch (registerCount) {
            case 5:
                registers[4] = instr.getRegisterG();
            case 4:
                registers[3] = instr.getRegisterF();
            case 3:
                registers[2] = instr.getRegisterE();
            case 2:
                registers[1] = instr.getRegisterD();
            case 1:
                registers[0] = instr.getRegisterC();
                break;
            }
        }

        return null;
    }

    private final int address;
    private final String opName;
    private final int childAddress;
    private final String methodDescriptor;
    private final int[] registers;
    private final boolean returnsVoid;
    private VirtualMachine vm;

    private InvokeOpHandler(int address, String opName, int childAddress, String methodDescriptor, int[] registers) {
        this.address = address;
        this.opName = opName;
        this.childAddress = childAddress;
        this.methodDescriptor = methodDescriptor;
        this.registers = registers;

        if (methodDescriptor.endsWith(")V")) {
            returnsVoid = true;
        } else {
            returnsVoid = false;
        }
    }

    private InvokeOpHandler(int address, String opName, int childAddress, String methodDescriptor, int[] registers,
                    VirtualMachine vm) {
        this(address, opName, childAddress, methodDescriptor, registers);
        this.vm = vm;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        MethodContext calleeContext = buildCalleeContext(mctx, registers, address);
        if (vm != null) {
            // VM has this method, so it knows how to execute it.
            ContextGraph graph = vm.execute(methodDescriptor, calleeContext);

            if (!returnsVoid) {
                RegisterStore registerStore = graph.getConsensusRegister(MethodContext.ReturnRegister);
                mctx.setResultRegister(registerStore);
            }
        } else {
            // Is it emulated?
            // Is it whitelisted as safe for reflection?

            // Not found, mark everything as ambiguous.
        }

        return getPossibleChildren();
    }

    private static MethodContext buildCalleeContext(MethodContext mctx, int[] registers, int address) {
        int parameterCount = registers.length;
        MethodContext calleeContext = new MethodContext(parameterCount, parameterCount, mctx.getCallDepth() + 1);

        for (int register : registers) {
            RegisterStore registerStore = mctx.getRegister(register, address);
            calleeContext.setRegister(register, registerStore);
        }

        return calleeContext;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opName);

        sb.append("{");
        if (opName.contains("/range")) {
            sb.append("r").append(registers[0]).append(" .. r").append(registers[registers.length - 1]);
        } else {
            for (int register : registers) {
                sb.append("r").append(register).append(", ");
            }
            sb.setLength(sb.length() - 2);
        }
        sb.append("}, ").append(methodDescriptor);

        return sb.toString();
    }

    @Override
    public int[] getPossibleChildren() {
        return new int[] { childAddress };
    }

    @Override
    public int getAddress() {
        return address;
    }

}
