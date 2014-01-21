package refactor.handler;

import java.util.Arrays;
import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.util.ReferenceUtil;

import refactor.vm.ContextGraph;
import refactor.vm.MethodContext;
import refactor.vm.RegisterStore;
import refactor.vm.VirtualMachine;
import simplify.Main;
import simplify.emulate.MethodEmulator;
import simplify.exec.MethodReflector;
import simplify.exec.UnknownValue;

public class InvokeOpHandler extends OpHandler {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    static InvokeOpHandler create(Instruction instruction, int address, VirtualMachine vm) {
        int childAddress = address + instruction.getCodeUnits();
        String opName = instruction.getOpcode().name;

        int[] registers = null;
        MethodReference methodReference = null;
        if (opName.contains("/range")) {
            Instruction3rc instr = (Instruction3rc) instruction;
            int registerCount = instr.getRegisterCount();
            int start = instr.getStartRegister();
            int end = start + registerCount;

            registers = new int[registerCount];
            for (int i = start; i < end; i++) {
                registers[i - start] = i;
            }

            methodReference = (MethodReference) instr.getReference();
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

            methodReference = (MethodReference) instr.getReference();
        }

        return new InvokeOpHandler(address, opName, childAddress, methodReference, registers, vm);
    }

    private final int address;
    private final String opName;
    private final int childAddress;
    private final MethodReference methodReference;
    private final int[] registers;
    private final VirtualMachine vm;
    private final boolean isStatic;

    private InvokeOpHandler(int address, String opName, int childAddress, MethodReference methodReference,
                    int[] registers, VirtualMachine vm) {
        this.address = address;
        this.opName = opName;
        this.childAddress = childAddress;
        this.methodReference = methodReference;
        this.registers = registers;
        this.vm = vm;
        isStatic = opName.contains("-static");
    }

    @Override
    public int[] execute(MethodContext mctx) {
        String methodDescriptor = ReferenceUtil.getMethodDescriptor(methodReference);
        boolean returnsVoid = methodReference.getReturnType().equals("V");
        if (vm.isMethodDefined(methodDescriptor)) {
            // VM has this method, so it knows how to execute it.
            MethodContext calleeContext = vm.getInstructionGraph(methodDescriptor).getRootContext();
            if (isStatic) {
                // Exclude first register since it's an instance reference and will already be a parameter.
                int[] registers = Arrays.copyOfRange(this.registers, 1, this.registers.length);
                addCalleeParameters(calleeContext, mctx, registers, address);
            } else {
                addCalleeParameters(calleeContext, mctx, registers, address);
            }

            System.out.println("executing callee: " + calleeContext);
            ContextGraph graph = vm.execute(methodDescriptor, calleeContext);

            if (graph == null) {
                // Couldn't execute the method. Maybe node visits exceeded?
                log.info("Problem internally executing " + methodReference + ", propigating ambiguity.");
                assumeMaximumUnknown(vm, mctx, registers, returnsVoid);
                return getPossibleChildren();
            }

            // TODO: fix this
            // updateInstanceAndMutableArguments(vm, mctx, graph, isStatic);

            if (!returnsVoid) {
                RegisterStore registerStore = graph.getConsensusRegister(MethodContext.ReturnRegister);
                mctx.setResultRegister(registerStore);
            }
        } else {
            MethodContext calleeContext = buildCalleeContext(mctx, registers, address);
            boolean allArgumentsKnown = allArgumentsKnown(mctx);
            if (allArgumentsKnown && MethodEmulator.canEmulate(methodDescriptor)) {
                MethodEmulator.emulate(calleeContext, methodDescriptor);
            } else if (allArgumentsKnown && MethodReflector.canReflect(methodDescriptor)) {
                MethodReflector.reflect(calleeContext, methodReference);
            } else {
                // Method not found and either all arguments are not known, couldn't emulate or reflect
                log.info("Unknown argument or couldn't find/emulate/reflect " + methodDescriptor
                                + " so propigating ambiguity.");
                assumeMaximumUnknown(vm, mctx, registers, returnsVoid);
                return getPossibleChildren();
            }

            // TODO: fix these
            // updateInstanceAndMutableArguments(vm, mctx, calleeContext, isStatic);

            if (!returnsVoid) {
                System.out.println("working with: " + calleeContext);
                RegisterStore returnRegister = calleeContext.getReturnRegister();
                mctx.setResultRegister(returnRegister);
            }
        }

        return getPossibleChildren();
    }

    private static void updateInstanceAndMutableArguments(VirtualMachine vm, MethodContext callerContext,
                    MethodContext calleeContext, boolean isStatic) {
        if (!isStatic) {
            RegisterStore registerStore = callerContext.peekRegister(0);
            Object value = calleeContext.peekRegisterValue(0);
            registerStore.setValue(value);
            log.fine("updating instance value: " + registerStore);
        }

        for (int i = 0; i < callerContext.getRegisterCount(); i++) {
            RegisterStore registerStore = callerContext.peekRegister(i);
            if (!vm.isImmutableClass(registerStore.getType())) {
                Object value = calleeContext.peekRegisterValue(i);
                registerStore.setValue(value);
                log.fine(registerStore.getType() + " is mutable, updating with callee value = " + registerStore);
            }
        }

    }

    private static void updateInstanceAndMutableArguments(VirtualMachine vm, MethodContext callerContext,
                    ContextGraph graph, boolean isStatic) {
        if (!isStatic) {
            RegisterStore registerStore = callerContext.peekRegister(0);
            Object value = graph.getConsensusRegister(0).getValue();
            registerStore.setValue(value);
            log.fine("updating instance value: " + registerStore);
        }

        for (int i = 0; i < callerContext.getRegisterCount(); i++) {
            RegisterStore registerStore = callerContext.peekRegister(i);
            if (!vm.isImmutableClass(registerStore.getType())) {
                Object value = graph.getConsensusRegister(i).getValue();
                registerStore.setValue(value);
                log.fine(registerStore.getType() + " is mutable, updating with callee value = " + registerStore);
            }
        }
    }

    private static boolean allArgumentsKnown(MethodContext mctx) {
        for (int i = 0; i < mctx.getRegisterCount(); i++) {
            RegisterStore registerStore = mctx.peekRegister(i);
            if (registerStore.getValue() instanceof UnknownValue) {
                return false;
            }
        }

        return true;
    }

    private static void assumeMaximumUnknown(VirtualMachine vm, MethodContext mctx, int[] registers, boolean returnsVoid) {
        for (int register : registers) {
            String className = mctx.peekRegisterType(register);
            if (vm.isImmutableClass(className)) {
                log.info(className + " is immutable");
                continue;
            }

            log.info(className + " is mutable and passed into strange method, marking unknown");
            RegisterStore registerStore = new RegisterStore(className, new UnknownValue());
            mctx.pokeRegister(register, registerStore);
        }
    }

    private static void addCalleeParameters(MethodContext calleeContext, MethodContext callerContext, int[] registers,
                    int address) {
        for (int i = 0; i < registers.length; i++) {
            int register = registers[i];
            RegisterStore registerStore = callerContext.getRegister(register, address);
            registerStore = new RegisterStore(registerStore.getType(), registerStore.getValue());
            calleeContext.setParameter(i - 1, registerStore);
        }
    }

    private static MethodContext buildCalleeContext(MethodContext callerContext, int[] registers, int address) {
        int parameterCount = registers.length;
        MethodContext calleeContext = new MethodContext(parameterCount, parameterCount,
                        callerContext.getCallDepth() + 1);

        addCalleeParameters(calleeContext, callerContext, registers, address);

        return calleeContext;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opName);

        sb.append(" {");
        if (opName.contains("/range")) {
            sb.append("r").append(registers[0]).append(" .. r").append(registers[registers.length - 1]);
        } else {
            for (int register : registers) {
                sb.append("r").append(register).append(", ");
            }
            sb.setLength(sb.length() - 2);
        }
        sb.append("}, ").append(ReferenceUtil.getMethodDescriptor(methodReference));

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
