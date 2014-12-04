package org.cf.smalivm.opcode;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cf.smalivm.MethodReflector;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.emulate.MethodEmulator;
import org.cf.smalivm.type.Type;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.ImmutableUtils;
import org.cf.util.SmaliClassUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvokeOp extends ExecutionContextOp {

    static InvokeOp create(Instruction instruction, int address, VirtualMachine vm) {
        int childAddress = address + instruction.getCodeUnits();
        String opName = instruction.getOpcode().name;

        MethodReference methodReference = (MethodReference) ((ReferenceInstruction) instruction).getReference();
        String methodDescriptor = ReferenceUtil.getMethodDescriptor(methodReference);

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

        String returnType = methodReference.getReturnType();
        List<String> parameterTypes;
        boolean isStatic = opName.contains("-static");
        SmaliClassManager classManager = vm.getClassManager();
        if (classManager.isLocalMethod(methodDescriptor)) {
            parameterTypes = classManager.getParameterTypes(methodDescriptor);
        } else {
            parameterTypes = Utils.getParameterTypes(methodDescriptor);
            if (!isStatic) {
                parameterTypes.add(0, methodReference.getDefiningClass());
            }
        }

        TIntList parameterRegisters = new TIntArrayList(parameterTypes.size());
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

    private static final Logger log = LoggerFactory.getLogger(InvokeOp.class.getSimpleName());

    private final boolean isStatic;
    private final String methodDescriptor;
    private final int[] parameterRegisters;
    private final List<String> parameterTypes;
    private final String returnType;
    private SideEffect.Level sideEffectLevel;
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
        sideEffectLevel = SideEffect.Level.STRONG;
    }

    @Override
    public int[] execute(ExecutionContext ectx) {
        String targetMethod = methodDescriptor;
        if (getName().endsWith("-virtual")) {
            // Method call might be to interface or abstract class.
            // Try and resolve what the actual virtual target is.
            int targetRegister = parameterRegisters[0];
            Object value = ectx.getMethodState().peekRegister(targetRegister);
            targetMethod = getLocalTargetForVirtualMethod(value);
        }

        MethodState callerContext = ectx.getMethodState();
        if (MethodReflector.canReflect(targetMethod) || MethodEmulator.canEmulate(targetMethod)) {
            MethodState calleeContext = buildNonLocalCalleeContext(callerContext);
            boolean allArgumentsKnown = allArgumentsKnown(calleeContext);
            if (allArgumentsKnown) {
                executeNonLocalMethod(targetMethod, callerContext, calleeContext);

                return getPossibleChildren();
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Not emulating / reflecting " + targetMethod + " because all args not known.");
                }
                assumeMaximumUnknown(callerContext);
            }
        } else {
            // This assumes if reflection fails, not worth it to try possibly cached framework classes.

            SmaliClassManager classManager = vm.getClassManager();
            if (classManager.isLocalMethod(targetMethod)) {
                ExecutionContext calleeContext = buildLocalCalleeContext(targetMethod, ectx);
                if (classManager.methodHasImplementation(targetMethod)) {
                    executeLocalMethod(targetMethod, ectx, calleeContext);
                } else {
                    if (log.isWarnEnabled()) {
                        log.warn("Attempting to execute local method without implementation: " + targetMethod
                                        + ". Assuming maxiumum ambiguity.");
                    }
                    assumeMaximumUnknown(callerContext);
                }
            } else {
                markCallerRegistersRead(callerContext);

                if (log.isWarnEnabled()) {
                    log.warn("Attempting to execute unknown method: " + targetMethod + ". Assuming maximum ambiguity.");
                }
                assumeMaximumUnknown(callerContext);
            }
        }

        return getPossibleChildren();
    }

    public int[] getParameterRegisters() {
        return parameterRegisters;
    }

    public String getReturnType() {
        return returnType;
    }

    @Override
    public SideEffect.Level sideEffectLevel() {
        return sideEffectLevel;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" {");
        if (getName().contains("/range")) {
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

    private boolean allArgumentsKnown(MethodState mState) {
        for (int parameterIndex = 0; parameterIndex < mState.getParameterCount(); parameterIndex++) {
            Object value = mState.peekParameter(parameterIndex);
            if (value instanceof UnknownValue) {
                return false;
            }
        }

        return true;
    }

    private void markCallerRegistersRead(MethodState callerState) {
        for (int callerRegister : parameterRegisters) {
            callerState.readRegister(callerRegister);
        }
    }

    private void assignCalleeMethodStateParameters(MethodState callerState, MethodState calleeState) {
        for (int parameterIndex = 0; parameterIndex < parameterRegisters.length; parameterIndex++) {
            int callerRegister = parameterRegisters[parameterIndex];
            Object value = callerState.readRegister(callerRegister);
            calleeState.assignParameter(parameterIndex, value);
        }
    }

    private void assumeMaximumUnknown(MethodState mState) {
        for (int i = 0; i < parameterTypes.size(); i++) {
            String type = parameterTypes.get(i);
            if (ImmutableUtils.isImmutableClass(type)) {
                if (log.isTraceEnabled()) {
                    log.trace(type + " is immutable");
                }
                continue;
            }

            // TODO: add option to mark all class states unknown instead of just method state

            int register = parameterRegisters[i];
            Object value = mState.peekRegister(register);
            if (null != value) {
                // It might be dumb to mark nulls as unknown..

                String actualType = SmaliClassUtils.javaClassToSmali(value.getClass());
                if (ImmutableUtils.isImmutableClass(actualType)) {
                    // I.e. parameter type might be Ljava/lang/Object;, but actual type is Ljava/lang/String;
                    if (log.isTraceEnabled()) {
                        log.trace(type + " is immutable");
                    }
                    continue;
                }
            }

            value = new UnknownValue(type);
            if (log.isDebugEnabled()) {
                log.debug(type + " is mutable and passed into unresolvable method execution, making Unknown");
            }

            mState.pokeRegister(register, value);
        }

        if (!returnType.equals("V")) {
            Object value = new UnknownValue(returnType);
            mState.assignResultRegister(value);
        }
    }

    private ExecutionContext buildLocalCalleeContext(String methodDescriptor, ExecutionContext callerContext) {
        ExecutionContext calleeContext = vm.getRootExecutionContext(methodDescriptor);
        calleeContext.setCallDepth(callerContext.getCallDepth() + 1);
        MethodState callerMethodState = callerContext.getMethodState();
        MethodState calleeMethodState = calleeContext.getMethodState();
        assignCalleeMethodStateParameters(callerMethodState, calleeMethodState);

        // Class state merging is handled by the VM.

        return calleeContext;
    }

    private MethodState buildNonLocalCalleeContext(MethodState callerMethodState) {
        ExecutionContext ectx = new ExecutionContext(vm);
        int parameterSize = VirtualMachine.getParameterSize(parameterTypes);
        int registerCount = parameterSize;
        MethodState calleeMethodState = new MethodState(ectx, registerCount, parameterTypes.size(), parameterSize);
        assignCalleeMethodStateParameters(callerMethodState, calleeMethodState);

        return calleeMethodState;
    }

    private void executeLocalMethod(String methodDescriptor, ExecutionContext callerContext,
                    ExecutionContext calleeContext) {
        ExecutionGraph graph = vm.execute(methodDescriptor, calleeContext, callerContext, parameterRegisters);
        if (graph == null) {
            // Problem executing the method. Maybe node visits or call depth exceeded?
            log.info("Problem executing " + methodDescriptor + ", propagating ambiguity.");
            assumeMaximumUnknown(callerContext.getMethodState());

            return;
        }

        if (!returnType.equals("V")) {
            Object consensus = graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);
            callerContext.getMethodState().assignResultRegister(consensus);
        }

        sideEffectLevel = graph.getHighestSideEffectLevel();
    }

    private void executeNonLocalMethod(String methodDescriptor, MethodState callerContext, MethodState calleeContext) {
        assert allArgumentsKnown(calleeContext);
        if (MethodEmulator.canEmulate(methodDescriptor)) {
            sideEffectLevel = MethodEmulator.emulate(calleeContext, methodDescriptor, getParameterRegisters());
        } else if (MethodReflector.canReflect(methodDescriptor)) {
            MethodReflector reflector = new MethodReflector(methodDescriptor, returnType, parameterTypes, isStatic);
            reflector.reflect(calleeContext); // playa play

            // Only safe, non-side-effect methods are allowed to be reflected.
            sideEffectLevel = SideEffect.Level.NONE;
        }

        if (!isStatic) {
            // Handle updating the instance reference
            Object originalInstance = callerContext.peekRegister(parameterRegisters[0]);
            Object newInstance = calleeContext.peekParameter(0);
            if (originalInstance != newInstance) {
                // Instance went from UninitializedInstance class to something else.
                // TODO: add test for this!
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

    private String getLocalTargetForVirtualMethod(Object value) {
        String actualType;
        if (value instanceof Type) {
            actualType = ((Type) value).getType();
        } else {
            actualType = SmaliClassUtils.javaClassToSmali(value.getClass().getName());
        }

        if (SmaliClassUtils.isPrimitiveType(actualType)) {
            actualType = SmaliClassUtils.smaliPrimitiveToJavaWrapper(actualType);
        }
        String methodSignature = methodDescriptor.split("->")[1];
        SmaliClassManager classManager = vm.getClassManager();
        String targetMethod = getLocalTargetForVirtualMethod(actualType, methodSignature, classManager,
                        new HashSet<String>());

        return targetMethod != null ? targetMethod : methodDescriptor;
    }

    private String getLocalTargetForVirtualMethod(String className, String methodSignature,
                    SmaliClassManager classManager, Set<String> visited) {
        visited.add(className);
        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(methodSignature);
        String methodDescriptor = sb.toString();

        boolean isLocalMethod = classManager.isLocalMethod(methodDescriptor);
        if ((isLocalMethod && classManager.methodHasImplementation(methodDescriptor))
                        || MethodReflector.isSafe(methodDescriptor)) {
            return methodDescriptor;
        }

        if (!classManager.isLocalClass(className)) {
            // Can't trace any further up.
            // Note, also checked if this is white-listed Java API
            return null;
        }

        BuilderClassDef classDef = classManager.getClass(className);
        Set<String> parents = new HashSet<String>();
        parents.addAll(classDef.getInterfaces());
        if (null != classDef.getSuperclass()) {
            parents.add(classDef.getSuperclass());
        }

        for (String parent : parents) {
            if (visited.contains(parent)) {
                continue;
            }
            String target = getLocalTargetForVirtualMethod(parent, methodSignature, classManager, visited);
            if (null != target) {
                return target;
            }
        }

        return null;
    }

}
