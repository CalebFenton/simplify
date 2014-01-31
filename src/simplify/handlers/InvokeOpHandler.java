package simplify.handlers;

import gnu.trove.list.TIntList;

import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.util.ReferenceUtil;

import simplify.Main;
import simplify.MethodReflector;
import simplify.SmaliClassUtils;
import simplify.emulate.MethodEmulator;
import simplify.vm.ContextGraph;
import simplify.vm.MethodContext;
import simplify.vm.RegisterStore;
import simplify.vm.VirtualMachine;
import simplify.vm.types.UnknownValue;

public class InvokeOpHandler extends OpHandler {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static void addCalleeParameters(MethodContext calleeContext, MethodContext callerContext, int[] registers,
                    int address, boolean isStatic) {
        int offset = 0;

        if (!isStatic) {
            // First register is instance references.
            int register = registers[0];
            RegisterStore registerStore = callerContext.getRegister(register, address);
            calleeContext.setParameter(-1, registerStore);
            offset = 1;
        }

        for (int i = offset; i < registers.length; i++) {
            int register = registers[i];
            // Passing actual value references since they'll be updated correctly by the JVM.
            RegisterStore registerStore = callerContext.getRegister(register, address);
            calleeContext.setParameter(i - offset, registerStore);

            if (registerStore.getType().equals("J")) {
                // This register index and the next both refer to this variable.
                i++;
            }
        }
    }

    private static boolean allArgumentsKnown(MethodContext mctx) {
        List<RegisterStore> registerStores = mctx.getRegisterToStore().getValues();
        for (RegisterStore registerStore : registerStores) {
            if (registerStore.getValue() instanceof UnknownValue) {
                return false;
            }
        }

        return true;
    }

    private static void assumeMaximumUnknown(VirtualMachine vm, MethodContext callerContext, int[] registers,
                    String returnType) {
        for (int i = 0; i < registers.length; i++) {
            int register = registers[i];
            String className = callerContext.peekRegisterType(register);
            if (SmaliClassUtils.isImmutableClass(className)) {
                if (className.equals("J")) {
                    i++;
                }

                log.fine(className + " is immutable");
                continue;
            }

            log.fine(className + " is mutable and passed into strange method, marking unknown");
            callerContext.pokeRegister(register, className, new UnknownValue());
        }

        if (!returnType.equals("V")) {
            callerContext.setResultRegister(returnType, new UnknownValue());
        }
    }

    private static MethodContext buildCalleeContext(MethodContext callerContext, int[] registers, int address,
                    boolean isStatic) {
        int parameterCount = registers.length;
        int registerCount = parameterCount;
        int callDepth = callerContext.getCallDepth() + 1;

        if (!isStatic && (registerCount > 0)) {
            parameterCount--;
        }

        MethodContext calleeContext = new MethodContext(registerCount, parameterCount, callDepth);

        addCalleeParameters(calleeContext, callerContext, registers, address, isStatic);

        return calleeContext;
    }

    private static void updateInstanceAndMutableArguments(VirtualMachine vm, MethodContext callerContext,
                    ContextGraph graph, boolean isStatic) {
        MethodContext calleeContext = graph.getNodePile(0).get(0).getContext();
        int calleeParamStart = calleeContext.getParameterStart();
        TIntList addresses = graph.getConnectedTerminatingAddresses();

        if (!isStatic) {
            int register = callerContext.getParameterStart() - 1;
            RegisterStore callerInstance = callerContext.peekRegister(register);
            Object value = graph.getRegisterConsensus(addresses, calleeParamStart - 1).getValue();

            log.fine("updating instance value: " + callerInstance + " to " + value);
            callerInstance.setValue(value);
        }

        int callerParamStart = callerContext.getParameterStart();
        int paramCount = callerContext.getRegisterCount() - callerParamStart;
        for (int i = 0; i < paramCount; i++) {
            RegisterStore registerStore = callerContext.peekRegister(callerParamStart + i);
            if (!SmaliClassUtils.isImmutableClass(registerStore.getType())) {
                Object value = graph.getRegisterConsensus(addresses, calleeParamStart + i).getValue();
                registerStore.setValue(value);
                log.fine(registerStore.getType() + " is mutable, updating with callee value = " + registerStore);
            }
        }
    }

    private void updateInstanceAndMutableArguments(MethodContext callerContext, MethodContext calleeContext) {
        int calleeParamStart = calleeContext.getParameterStart();

        System.out.println("updating caller / callee (" + callerContext.getParameterStart() + "):\n" + callerContext
                        + "\n" + calleeContext);
        if (!isStatic) {
            RegisterStore callerInstance = callerContext.peekRegister(registers[0]);
            RegisterStore calleeInstance = calleeContext.peekRegister(calleeParamStart - 1);
            Object value = calleeInstance.getValue();

            log.fine("Updating instance: " + callerInstance + " to " + value);
            callerInstance.setValue(value);

            // If this is instance initialization, regardless of mutability, consider the instance to have been assigned
            // at this address (in addition to having been read earlier). Dead code remover will try to determine
            // liveness from this point.
            if (methodDescriptor.contains("<init>")) {
                callerContext.setRegister(registers[0], callerInstance.getType(), callerInstance.getValue(), address);
            }
        }

        int callerParamStart = callerContext.getParameterStart();
        int paramCount = callerContext.getRegisterCount() - callerParamStart;
        for (int i = 0; i < paramCount; i++) {
            RegisterStore registerStore = callerContext.peekRegister(callerParamStart + i);
            if (!SmaliClassUtils.isImmutableClass(registerStore.getType())) {
                Object value = calleeContext.peekRegisterValue(calleeParamStart + i);
                registerStore.setValue(value);

                log.fine(registerStore.getType() + " is mutable, updating with callee value = " + registerStore);
            }
        }
    }

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
    private final int childAddress;
    private final boolean isStatic;
    private final MethodReference methodReference;
    private final String methodDescriptor;
    private final String returnType;
    private final String opName;
    private final int[] registers;
    private final VirtualMachine vm;
    private boolean hasSideEffects;

    private InvokeOpHandler(int address, String opName, int childAddress, MethodReference methodReference,
                    int[] registers, VirtualMachine vm) {
        this.address = address;
        this.opName = opName;
        this.childAddress = childAddress;
        this.methodReference = methodReference;
        this.methodDescriptor = ReferenceUtil.getMethodDescriptor(methodReference);
        this.returnType = methodReference.getReturnType();
        this.registers = registers;
        this.vm = vm;
        isStatic = opName.contains("-static");
    }

    @Override
    public int[] execute(MethodContext callerContext) {
        hasSideEffects = true; // assume true for most cases, just to be safe

        boolean returnsVoid = returnType.equals("V");
        if (vm.isMethodDefined(methodDescriptor)) {
            // VM has this method, so it knows how to execute it.
            MethodContext calleeContext = vm.getInstructionGraph(methodDescriptor).getRootContext();
            calleeContext.incrementCallDepth();
            addCalleeParameters(calleeContext, callerContext, registers, address, isStatic);

            ContextGraph graph = vm.execute(methodDescriptor, calleeContext);
            if (graph == null) {
                // Couldn't execute the method. Maybe node visits or call depth exceeded?
                log.info("Problem executing " + methodDescriptor + ", propigating ambiguity.");
                assumeMaximumUnknown(vm, callerContext, registers, returnType);

                return getPossibleChildren();
            }

            // TODO: fix
            // Register value object references are passed in, but each instruction has a new clone, which doesn't point
            // to the original. So updates to objects in callee aren't propagated back to caller.
            // updateInstanceAndMutableArguments(vm, callerContext, graph, isStatic);

            if (!returnsVoid) {
                // This also happens in method reflector.
                TIntList terminating = graph.getConnectedTerminatingAddresses();
                RegisterStore consensus = graph.getRegisterConsensus(terminating, MethodContext.ReturnRegister);
                Object value = consensus.getValue();
                callerContext.setResultRegister(returnType, value);
            }
        } else {
            MethodContext calleeContext = buildCalleeContext(callerContext, registers, address, isStatic);
            boolean allArgumentsKnown = allArgumentsKnown(calleeContext);
            if (allArgumentsKnown && MethodEmulator.canEmulate(methodDescriptor)) {
                MethodEmulator.emulate(calleeContext, methodDescriptor);
            } else if (allArgumentsKnown && MethodReflector.canReflect(methodDescriptor)) {
                MethodReflector reflector = new MethodReflector(methodReference, isStatic);
                reflector.reflect(calleeContext); // player play

                // TOOD: investigate better marking of side effects. this is very conservative and depends on reflected
                // methods not actually having side effects
                hasSideEffects = false;
            } else {
                // Method not found and either all arguments are not known, couldn't emulate or reflect
                log.fine("Unknown argument or couldn't find/emulate/reflect " + methodDescriptor
                                + " so propigating ambiguity.");
                assumeMaximumUnknown(vm, callerContext, registers, returnType);
                return getPossibleChildren();
            }

            if (!isStatic) {
                // Consider the instance reference assigned here. This is so the dead code remover can check if it's
                // used elsewhere. If it's not and there are no side-effects, it's removed.
                RegisterStore instance = callerContext.peekRegister(registers[0]);
                callerContext.setRegister(registers[0], instance.getType(), instance.getValue(), address);
            }

            if (!returnsVoid) {
                RegisterStore returnRegister = calleeContext.getReturnRegister();
                callerContext.setResultRegister(returnRegister.getType(), returnRegister.getValue());
            }
        }

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

    public String getReturnType() {
        return returnType;
    }

    public boolean hasSideEffects() {
        return hasSideEffects;
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

}
