package org.cf.smalivm.opcode;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cf.smalivm.MethodReflector;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.emulate.MethodEmulator;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxMethodVisitsExceeded;
import org.cf.smalivm.type.LocalType;
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
        if (classManager.isLocalMethod(methodDescriptor)
                        && !(classManager.isFramework(methodDescriptor) && !classManager
                                        .isSafeFramework(methodDescriptor))) {
            parameterTypes = classManager.getParameterTypes(methodDescriptor);
        } else {
            parameterTypes = Utils.getParameterTypes(methodDescriptor);
            if (!isStatic) {
                parameterTypes.add(0, methodReference.getDefiningClass());
            }
        }

        int i = 0;
        TIntList parameterRegisters = new TIntLinkedList(parameterTypes.size());
        for (String parameterType : parameterTypes) {
            parameterRegisters.add(registers[i]);
            i++;
            if ("J".equals(parameterType) || "D".equals(parameterType)) {
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
        // TODO: In order to get working call stacks, refactor this to delegate most of the work to MethodExecutor.
        // This will remove InvokeOp as a weirdly complex op, and probably allow some methods to be made protected.
        // It also keeps things clear with method execution delegated to the class with the same name.
        // MethodExecutor can maintain a mapping such that calleeContext -> (callerContext, caller address)
        // With this mapping, stack traces can be reconstructed.
        String targetMethod = methodDescriptor;
        if (getName().contains("-virtual")) { // -virtual/range
            // Method call might be to interface or abstract class.
            // Try and resolve what the actual virtual target is.
            int targetRegister = parameterRegisters[0];
            HeapItem item = ectx.getMethodState().peekRegister(targetRegister);
            targetMethod = getLocalTargetForVirtualMethod(item.getValue());
        }

        MethodState callerMethodState = ectx.getMethodState();
        // Try to reflect or emulate before using local class.
        if (MethodReflector.canReflect(targetMethod) || MethodEmulator.canEmulate(targetMethod)) {
            ExecutionContext calleeContext = buildNonLocalCalleeContext(callerMethodState);
            boolean allArgumentsKnown = allArgumentsKnown(calleeContext.getMethodState());
            if (allArgumentsKnown || MethodEmulator.canHandleUnknownValues(targetMethod)) {
                executeNonLocalMethod(targetMethod, callerMethodState, calleeContext);

                return getChildren();
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Not emulating / reflecting " + targetMethod + " because all args not known.");
                }
                assumeMaximumUnknown(callerMethodState);
            }
        } else {
            // This assumes if reflection or emulation fails, not worth it to try possibly cached framework classes.

            SmaliClassManager classManager = vm.getClassManager();
            if (classManager.isLocalMethod(targetMethod)) {
                if (classManager.isFramework(targetMethod) && !classManager.isSafeFramework(targetMethod)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Not executing unsafe local method: " + targetMethod
                                        + ". Assuming maxiumum ambiguity.");
                    }
                    assumeMaximumUnknown(callerMethodState);

                    return getChildren();
                }

                if (!classManager.methodHasImplementation(targetMethod)) {
                    if (log.isWarnEnabled()) {
                        if (!classManager.isNativeMethod(targetMethod)) {
                            log.warn("Attempting to execute local method without implementation: " + targetMethod
                                            + ". Assuming maxiumum ambiguity.");
                        } else {
                            log.warn("Cannot execute local native method: " + targetMethod
                                            + ". Assuming maxiumum ambiguity.");
                        }
                    }
                    assumeMaximumUnknown(callerMethodState);

                    return getChildren();
                }

                ExecutionContext calleeContext = buildLocalCalleeContext(targetMethod, ectx);
                executeLocalMethod(targetMethod, ectx, calleeContext);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Unknown method: " + targetMethod + ". Assuming maximum ambiguity.");
                }
                assumeMaximumUnknown(callerMethodState);
            }
        }

        return getChildren();
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
        for (int parameterRegister = mState.getParameterStart(); parameterRegister < mState.getRegisterCount();) {
            HeapItem item = mState.peekParameter(parameterRegister);
            if (item.isUnknown()) {
                return false;
            }
            String type = item.getType();
            parameterRegister += "J".equals(type) || "D".equals(type) ? 2 : 1;
        }

        return true;
    }

    private void assignCalleeMethodStateParameters(MethodState callerState, MethodState calleeState) {
        int parameterRegister = calleeState.getParameterStart();
        for (int i = 0; i < parameterRegisters.length; i++) {
            int callerRegister = parameterRegisters[i];
            HeapItem item = callerState.readRegister(callerRegister);
            // Since we have explicit type, over ride any implied type as it may be inaccurate.
            // For example, might think it's an int when really it's a short or boolean.
            String type = parameterTypes.get(i);
            calleeState.assignParameter(parameterRegister, new HeapItem(item.getValue(), type));
            parameterRegister += "J".equals(type) || "D".equals(type) ? 2 : 1;
        }
    }

    private void assumeMaximumUnknown(MethodState mState) {
        // TODO: add option to mark all class states unknown instead of just method state
        for (int i = 0; i < parameterTypes.size(); i++) {
            // Always prefer explicit type over implied from heap item.
            // I.e. "const/4 v0, 0x0" can mean null, 0x1 can be true, etc.
            String type = parameterTypes.get(i);
            int register = parameterRegisters[i];
            HeapItem item = mState.readRegister(register);
            Object value = item.getValue();
            if (null == value) {
                // Nulls don't mutate.
                continue;
            }

            boolean isInitializing = methodDescriptor.contains(";-><init>(");
            if (!isInitializing) {
                // May be immutable type, but if this is the initializer, internal state would be changing.
                if (ImmutableUtils.isImmutableClass(type)) {
                    if (log.isTraceEnabled()) {
                        log.trace(type + " (parameter) is immutable");
                    }
                    continue;
                }

                if (item.isImmutable()) {
                    // Parameter type might be "Ljava/lang/Object;" but actual type is "Ljava/lang/String";
                    if (log.isTraceEnabled()) {
                        log.trace(type + " (actual) is immutable");
                    }
                    continue;
                }
            }

            item = HeapItem.newUnknown(type);
            if (log.isDebugEnabled()) {
                log.debug(type + " is mutable and passed into unresolvable method execution, making Unknown");
            }

            mState.pokeRegister(register, item);
        }

        if (!"V".equals(returnType)) {
            HeapItem item = HeapItem.newUnknown(returnType);
            mState.assignResultRegister(item);
        }
    }

    private ExecutionContext buildLocalCalleeContext(String methodDescriptor, ExecutionContext callerContext) {
        ExecutionContext calleeContext = vm.spawnExecutionContext(methodDescriptor, callerContext, getAddress());
        MethodState callerMethodState = callerContext.getMethodState();
        MethodState calleeMethodState = calleeContext.getMethodState();
        assignCalleeMethodStateParameters(callerMethodState, calleeMethodState);

        // Class state merging is handled by the VM.

        return calleeContext;
    }

    private ExecutionContext buildNonLocalCalleeContext(MethodState callerMethodState) {
        ExecutionContext ectx = new ExecutionContext(vm, methodDescriptor);
        int parameterSize = Utils.getRegisterSize(parameterTypes);
        int registerCount = parameterSize;
        MethodState calleeMethodState = new MethodState(ectx, registerCount, parameterTypes.size(), parameterSize);
        assignCalleeMethodStateParameters(callerMethodState, calleeMethodState);
        ectx.setMethodState(calleeMethodState);

        return ectx;
    }

    private void executeLocalMethod(String methodDescriptor, ExecutionContext callerContext,
                    ExecutionContext calleeContext) {
        ExecutionGraph graph = null;
        try {
            graph = vm.execute(methodDescriptor, calleeContext, callerContext, parameterRegisters);
        } catch (MaxAddressVisitsExceeded | MaxCallDepthExceeded | MaxMethodVisitsExceeded e) {
            if (log.isWarnEnabled()) {
                log.warn(e.toString());
            }
        }

        if (graph == null) {
            // Problem executing the method. Maybe node visits or call depth exceeded?
            log.info("Problem executing " + methodDescriptor + ", propagating ambiguity.");
            assumeMaximumUnknown(callerContext.getMethodState());

            return;
        }

        if (!returnType.equals("V")) {
            HeapItem consensus = graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);
            callerContext.getMethodState().assignResultRegister(consensus);
        }

        sideEffectLevel = graph.getHighestSideEffectLevel();
    }

    private void executeNonLocalMethod(String methodDescriptor, MethodState callerContext,
                    ExecutionContext calleeContext) {
        if (MethodEmulator.canEmulate(methodDescriptor)) {
            sideEffectLevel = MethodEmulator.emulate(vm, calleeContext, methodDescriptor, getParameterRegisters());
        } else if (MethodReflector.canReflect(methodDescriptor)) {
            assert allArgumentsKnown(calleeContext.getMethodState());

            MethodReflector reflector = new MethodReflector(methodDescriptor, returnType, parameterTypes, isStatic);
            reflector.reflect(calleeContext.getMethodState()); // playa play

            // Only safe, non-side-effect methods are allowed to be reflected.
            sideEffectLevel = SideEffect.Level.NONE;
        }

        if (!isStatic) {
            // Handle updating the instance reference
            HeapItem originalInstanceItem = callerContext.peekRegister(parameterRegisters[0]);
            HeapItem newInstanceItem = calleeContext.getMethodState().peekParameter(0);
            if (originalInstanceItem.getValue() != newInstanceItem.getValue()) {
                // Instance went from UninitializedInstance class to something else.
                // TODO: add test for this!
                callerContext.assignRegisterAndUpdateIdentities(parameterRegisters[0], newInstanceItem);
            } else {
                // The instance reference could have changed, so mark it as assigned here.
                callerContext.assignRegister(parameterRegisters[0], newInstanceItem);
            }
        }

        if (!"V".equals(returnType)) {
            HeapItem returnItem = calleeContext.getMethodState().readReturnRegister();
            callerContext.assignResultRegister(returnItem);
        }
    }

    private String getLocalTargetForVirtualMethod(Object value) {
        String actualType;
        if (value instanceof LocalType) {
            actualType = ((LocalType) value).getName();
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

    private static boolean doesNonLocalMethodExist(String className, String methodSignature) {
        Class<?> klazz = null;
        try {
            klazz = Class.forName(SmaliClassUtils.smaliClassToJava(className));
        } catch (ClassNotFoundException e) {
            return false;
        }

        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(methodSignature);
        List<String> paramList = Utils.getParameterTypes(sb.toString());
        Class<?>[] params = new Class<?>[paramList.size()];
        for (int i = 0; i < paramList.size(); i++) {
            String paramName = paramList.get(i);
            try {
                if (SmaliClassUtils.isPrimitiveType(paramName)) {
                    params[i] = SmaliClassUtils.getPrimitiveType(SmaliClassUtils.smaliClassToJava(paramName));
                } else {
                    params[i] = Class.forName(SmaliClassUtils.smaliClassToJava(paramName));
                }
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        String methodName = methodSignature.split("\\(")[0];
        try {
            klazz.getMethod(methodName, params);
        } catch (NoSuchMethodException e) {
            return false;
        } catch (SecurityException e) {
            return false;
        }

        return true;
    }

    private String getLocalTargetForVirtualMethod(String className, String methodSignature,
                    SmaliClassManager classManager, Set<String> visited) {
        visited.add(className);
        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(methodSignature);
        String methodDescriptor = sb.toString();

        boolean isLocalMethod = classManager.isLocalMethod(methodDescriptor);
        if ((isLocalMethod && classManager.methodHasImplementation(methodDescriptor))) {
            return methodDescriptor;
        }

        if (MethodReflector.isSafe(methodDescriptor) && doesNonLocalMethodExist(className, methodSignature)) {
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
