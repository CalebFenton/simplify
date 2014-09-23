package org.cf.smalivm.opcode;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

import org.cf.smalivm.MethodReflector;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ContextGraph;
import org.cf.smalivm.context.ContextNode;
import org.cf.smalivm.context.MethodContext;
import org.cf.smalivm.emulate.MethodEmulator;
import org.cf.smalivm.type.TypeUtil;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvokeOp extends Op {

    private static final Logger log = LoggerFactory.getLogger(InvokeOp.class.getSimpleName());

    private static boolean allArgumentsKnown(MethodContext mctx) {
        Object[] registerValues = mctx.getRegisterToValue().values();
        for (Object value : registerValues) {
            if (value instanceof UnknownValue) {
                return false;
            }
        }

        return true;
    }

    private static Object getMutableParameterConsensus(TIntList addressList, ContextGraph graph, int parameterIndex) {
        ContextNode firstNode = graph.getNodePile(addressList.get(0)).get(0);
        Object value = firstNode.getMethodContext().getMutableParameter(parameterIndex);
        int[] addresses = addressList.toArray();
        for (int address : addresses) {
            List<ContextNode> nodes = graph.getNodePile(address);
            for (ContextNode node : nodes) {
                Object otherValue = node.getMethodContext().getMutableParameter(parameterIndex);

                if (value != otherValue) {
                    log.trace("No conensus value for parameterIndex #" + parameterIndex + ", returning unknown");

                    return new UnknownValue(TypeUtil.getValueType(value));
                }
            }

        }

        return value;
    }

    static InvokeOp create(Instruction instruction, int address, VirtualMachine vm) {
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

        String methodDescriptor = ReferenceUtil.getMethodDescriptor(methodReference);
        String returnType = methodReference.getReturnType();
        List<String> parameterTypes = new ArrayList<String>();
        boolean isStatic = opName.contains("-static");
        if (!isStatic) {
            parameterTypes.add(methodReference.getDefiningClass());
        }
        parameterTypes.addAll(Utils.getParameterTypes(methodDescriptor));

        TIntList parameterRegisters = new TIntArrayList();
        for (int i = 0; i < parameterTypes.size(); i++) {
            parameterRegisters.add(registers[i]);
            String type = parameterTypes.get(i);
            if (type.equals("J") || type.equals("D")) {
                i++;
            }
        }

        return new InvokeOp(address, opName, childAddress, methodDescriptor, returnType, parameterRegisters.toArray(),
                        parameterTypes, vm, isStatic);
    }

    private final boolean isStatic;

    private final String methodDescriptor;

    private final int[] parameterRegisters;
    private final List<String> parameterTypes;
    private final String returnType;
    private SideEffect.Type sideEffectType;
    private final VirtualMachine vm;

    private InvokeOp(int address, String opName, int childAddress, String methodDescriptor, String returnType,
                    int[] parameterRegisters, List<String> parameterTypes, VirtualMachine vm, boolean isStatic) {
        super(address, opName, childAddress);

        this.methodDescriptor = methodDescriptor;
        this.returnType = returnType;
        this.parameterRegisters = parameterRegisters;
        this.parameterTypes = parameterTypes;
        this.vm = vm;
        this.isStatic = isStatic;
        sideEffectType = SideEffect.Type.STRONG;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        if (vm.isMethodDefined(methodDescriptor)) {
            executeLocalMethod(methodDescriptor, mctx);
        } else {
            executeNonLocalMethod(methodDescriptor, mctx);
        }

        return getPossibleChildren();
    }

    public String getReturnType() {
        return returnType;
    }

    @Override
    public SideEffect.Type sideEffectType() {
        return sideEffectType;
    }

    public int[] getParameterRegisters() {
        return parameterRegisters;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        sb.append(" {");
        if (getOpName().contains("/range")) {
            sb.append("r").append(parameterRegisters[0]).append(" .. r")
                            .append(parameterRegisters[parameterRegisters.length - 1]);
        } else {
            if (parameterRegisters.length > 0) {
                for (int register : parameterRegisters) {
                    sb.append("r").append(register).append(", ");
                }
                sb.setLength(sb.length() - 2);
            }
        }
        sb.append("}, ").append(methodDescriptor);

        return sb.toString();
    }

    private void assumeMaximumUnknown(MethodContext callerContext) {
        for (int i = 0; i < parameterTypes.size(); i++) {
            String type = parameterTypes.get(i);
            if (SmaliClassUtils.isImmutableClass(type)) {
                log.debug(type + " is immutable");
                continue;
            }

            log.debug(type + " is mutable and passed into unresolvable method execution, making Unknown");
            int register = parameterRegisters[i];
            Object value = new UnknownValue(type);
            callerContext.pokeRegister(register, value);
        }

        if (!returnType.equals("V")) {
            Object value = new UnknownValue(returnType);
            callerContext.assignResultRegister(value);
        }
    }

    private MethodContext buildLocalCalleeContext(MethodContext callerContext) {
        MethodContext result = vm.buildRootMethodContext(methodDescriptor);
        result.setCallDepth(callerContext.getCallDepth() + 1);
        assignCalleeContextParameters(callerContext, result);

        return result;
    }

    private MethodContext buildNonLocalCalleeContext(MethodContext callerContext) {
        int parameterSize = VirtualMachine.getParameterSize(parameterTypes);
        int registerCount = parameterSize;
        int callDepth = callerContext.getCallDepth() + 1;
        MethodContext calleeContext = new MethodContext(registerCount, parameterSize, callDepth);
        assignCalleeContextParameters(callerContext, calleeContext);

        return calleeContext;
    }

    private void assignCalleeContextParameters(MethodContext callerContext, MethodContext calleeContext) {
        for (int parameterIndex = 0; parameterIndex < parameterRegisters.length; parameterIndex++) {
            int callerRegister = parameterRegisters[parameterIndex];
            Object value = callerContext.readRegister(callerRegister);
            calleeContext.assignParameter(parameterIndex, value);
        }
    }

    private void executeLocalMethod(String methodDescriptor, MethodContext callerContext) {
        MethodContext calleeContext = buildLocalCalleeContext(callerContext);
        ContextGraph graph = vm.execute(methodDescriptor, calleeContext);
        if (graph == null) {
            // Problem executing the method. Maybe node visits or call depth exceeded?
            log.info("Problem executing " + methodDescriptor + ", propigating ambiguity.");
            assumeMaximumUnknown(callerContext);

            return;
        }

        updateInstanceAndMutableArguments(callerContext, graph);

        if (!returnType.equals("V")) {
            TIntList terminating = graph.getConnectedTerminatingAddresses();
            // TODO: use getTerminatingRegisterConsensus
            Object consensus = graph.getRegisterConsensus(terminating, MethodContext.ReturnRegister);
            callerContext.assignResultRegister(consensus);
        }

        sideEffectType = graph.getStrongestSideEffectType();
    }

    private void executeNonLocalMethod(String methodDescriptor, MethodContext callerContext) {
        MethodContext calleeContext = buildNonLocalCalleeContext(callerContext);
        boolean allArgumentsKnown = allArgumentsKnown(calleeContext);
        if (allArgumentsKnown && MethodEmulator.canEmulate(methodDescriptor)) {
            sideEffectType = MethodEmulator.emulate(calleeContext, methodDescriptor, getParameterRegisters());
        } else if (allArgumentsKnown && MethodReflector.canReflect(methodDescriptor)) {
            MethodReflector reflector = new MethodReflector(methodDescriptor, returnType, parameterTypes, isStatic);
            reflector.reflect(calleeContext); // playa play

            // Only safe, non-side-effect methods are allowed to be reflected.
            sideEffectType = SideEffect.Type.NONE;
        } else {
            log.debug("Unknown argument(s) or can't find/emulate/reflect " + methodDescriptor
                            + ". Propigating ambiguity.");
            assumeMaximumUnknown(callerContext);

            return;
        }

        if (!isStatic) {
            // Handle updating the instance reference
            Object originalInstance = callerContext.peekRegister(parameterRegisters[0]);
            Object newInstance = calleeContext.getParameter(0);
            if (originalInstance != newInstance) {
                // Instance went from UninitializedInstance class to something else.
                callerContext.assignRegisterAndUpdateIdentities(parameterRegisters[0], newInstance);
            } else {
                // The instance reference could have changed, so mark it as assigned here.
                callerContext.assignRegister(parameterRegisters[0], newInstance);
            }
        }

        if (!returnType.equals("V")) {
            Object returnRegister = calleeContext.readReturnRegister();
            callerContext.assignResultRegister(returnRegister);
        }
    }

    private void updateInstanceAndMutableArguments(MethodContext callerContext, ContextGraph graph) {
        TIntList terminatingAddresses = graph.getConnectedTerminatingAddresses();
        for (int parameterIndex = 0; parameterIndex < parameterRegisters.length; parameterIndex++) {
            String type = parameterTypes.get(parameterIndex);
            boolean mutable = !SmaliClassUtils.isImmutableClass(type);
            if (!mutable) {
                continue;
            }

            int register = parameterRegisters[parameterIndex];
            Object value = getMutableParameterConsensus(terminatingAddresses, graph, parameterIndex);
            callerContext.assignRegister(register, value);
        }
    }

}
