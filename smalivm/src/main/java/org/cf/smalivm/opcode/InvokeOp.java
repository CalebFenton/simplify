package org.cf.smalivm.opcode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ClassUtils;
import org.cf.smalivm.MethodReflector;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.emulate.MethodEmulator;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxExecutionTimeExceeded;
import org.cf.smalivm.exception.MaxMethodVisitsExceeded;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.cf.smalivm.smali.ClassManager;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvokeOp extends ExecutionContextOp {

    private static final Logger log = LoggerFactory.getLogger(InvokeOp.class.getSimpleName());

    private final boolean isStatic;

    private final String methodSignature;
    private final int[] parameterRegisters;
    private final List<String> parameterTypes;
    private final String returnType;
    private SideEffect.Level sideEffectLevel;
    private final VirtualMachine vm;

    InvokeOp(MethodLocation location, MethodLocation child, String methodSignature, String returnType,
                    int[] parameterRegisters, List<String> parameterTypes, VirtualMachine vm, boolean isStatic) {
        super(location, child);

        this.methodSignature = methodSignature;
        this.returnType = returnType;
        this.parameterRegisters = parameterRegisters;
        this.parameterTypes = parameterTypes;
        this.vm = vm;
        this.isStatic = isStatic;
        sideEffectLevel = SideEffect.Level.STRONG;
    }

    @Override
    public void execute(ExecutionNode node, ExecutionContext ectx) {
        // TODO: In order to get working call stacks, refactor this to delegate most of the work to MethodExecutor.
        // This will remove InvokeOp as a weirdly complex op, and probably allow some methods to be made protected.
        // It also keeps things clear with method execution delegated to the class with the same name.
        // MethodExecutor can maintain a mapping such that calleeContext -> (callerContext, caller address)
        // With this mapping, stack traces can be reconstructed.
        String targetMethod = methodSignature;
        if (getName().startsWith("invoke-virtual")) { // -virtual/range
            // Resolve what the actual virtual target is because method call may be to interface or abstract class.
            int targetRegister = parameterRegisters[0];
            HeapItem item = ectx.getMethodState().peekRegister(targetRegister);
            targetMethod = resolveVirtualMethod(item.getValue());
        }

        MethodState callerMethodState = ectx.getMethodState();
        // Try to reflect or emulate before using local class.
        if (vm.getConfiguration().isSafe(targetMethod) || MethodEmulator.canEmulate(targetMethod)) {
            ExecutionContext calleeContext = buildNonLocalCalleeContext(ectx);
            boolean allArgumentsKnown = allArgumentsKnown(calleeContext.getMethodState());
            if (allArgumentsKnown || MethodEmulator.canHandleUnknownValues(targetMethod)) {
                executeNonLocalMethod(targetMethod, callerMethodState, calleeContext, node);
                return;
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Not emulating / reflecting {} because all args not known.", targetMethod);
                }
                assumeMaximumUnknown(callerMethodState);
            }
        } else {
            // This assumes if reflection or emulation fails, not worth it to try possibly cached framework classes.
            ClassManager classManager = vm.getClassManager();
            if (classManager.isLocalMethod(targetMethod)) {
                if (classManager.isFrameworkClass(targetMethod) && !classManager.isSafeFrameworkClass(targetMethod)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Not executing unsafe local method: {}. Assuming maxiumum ambiguity.", targetMethod);
                    }
                    assumeMaximumUnknown(callerMethodState);
                    return;
                }

                if (!classManager.methodHasImplementation(targetMethod)) {
                    if (log.isWarnEnabled()) {
                        if (!classManager.isNativeMethod(targetMethod)) {
                            log.warn("Attempting to execute local method without implementation: {}. Assuming maxiumum ambiguity.",
                                            targetMethod);
                        } else {
                            log.warn("Cannot execute local native method: {}. Assuming maxiumum ambiguity.",
                                            targetMethod);
                        }
                    }
                    assumeMaximumUnknown(callerMethodState);
                    return;
                }

                ExecutionContext calleeContext = buildLocalCalleeContext(ectx, targetMethod);
                executeLocalMethod(targetMethod, ectx, calleeContext);
            } else {
                log.debug("Unknown method: {}. Assuming maximum ambiguity.", targetMethod);
                assumeMaximumUnknown(callerMethodState);
            }
        }

        return;
    }

    public int[] getParameterRegisters() {
        return parameterRegisters;
    }

    public String getReturnType() {
        return returnType;
    }

    @Override
    public SideEffect.Level getSideEffectLevel() {
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
        sb.append("}, ").append(methodSignature);

        return sb.toString();
    }

    private boolean allArgumentsKnown(MethodState mState) {
        for (int parameterRegister = mState.getParameterStart(); parameterRegister < mState.getRegisterCount();) {
            HeapItem item = mState.peekParameter(parameterRegister);
            if (item.isUnknown()) {
                return false;
            }
            String type = item.getType();
            parameterRegister += Utils.getRegisterSize(type);
        }

        return true;
    }

    private void assignCalleeMethodArguments(MethodState callerState, MethodState calleeState) {
        int parameterRegister = calleeState.getParameterStart();
        for (int i = 0; i < parameterRegisters.length; i++) {
            int callerRegister = parameterRegisters[i];
            HeapItem item = callerState.readRegister(callerRegister);

            /*
             * Type can be confused here. For example, creating a short, int, or boolean all looks the same:
             * const/4 v0,0x1 (could be true, (int)1 or (short)1)
             * If a more restrictive type is given in the method signature, prefer that, for example:
             * method argument is an int but signature declares it as boolean, so switch it to boolean
             * However, if the type is less specific, such as a super class or interface, do not use the less specific
             * type. For example:
             * method argument is Lchild_class; but signature says Lparent_class;, prefer Lchild_class;
             */
            String type;
            if (ClassNameUtils.isPrimitive(item.getType())) {
                type = parameterTypes.get(i);
            } else {
                type = parameterTypes.get(i);
            }

            calleeState.assignParameter(parameterRegister, new HeapItem(item.getValue(), type));
            parameterRegister += Utils.getRegisterSize(type);
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

            boolean isInitializing = methodSignature.contains(";-><init>(");
            if (!isInitializing) {
                // May be immutable type, but if this is the initializer, internal state would be changing.
                if (vm.getConfiguration().isImmutable(type)) {
                    if (log.isTraceEnabled()) {
                        log.trace("{} (parameter) is immutable", type);
                    }
                    continue;
                }

                if (item.isImmutable()) {
                    // Parameter type might be "Ljava/lang/Object;" but actual type is "Ljava/lang/String";
                    if (log.isTraceEnabled()) {
                        log.trace("{} (actual) is immutable", type);
                    }
                    continue;
                }
            }

            item = HeapItem.newUnknown(type);
            if (log.isDebugEnabled()) {
                log.debug("{} is mutable and passed into unresolvable method execution, making Unknown", type);
            }

            mState.pokeRegister(register, item);
        }

        if (!"V".equals(returnType)) {
            HeapItem item = HeapItem.newUnknown(returnType);
            mState.assignResultRegister(item);
        }
    }

    private ExecutionContext buildLocalCalleeContext(ExecutionContext callerContext, String methodSignature) {
        ExecutionContext calleeContext = vm.spawnRootExecutionContext(methodSignature, callerContext, getAddress());
        MethodState callerMethodState = callerContext.getMethodState();
        MethodState calleeMethodState = calleeContext.getMethodState();
        assignCalleeMethodArguments(callerMethodState, calleeMethodState);

        // Class state merging is handled by the VM.

        return calleeContext;
    }

    private ExecutionContext buildNonLocalCalleeContext(ExecutionContext callerContext) {
        ExecutionContext calleeContext = new ExecutionContext(vm, methodSignature);
        int parameterSize = Utils.getRegisterSize(parameterTypes);
        int registerCount = parameterSize;
        MethodState calleeMethodState = new MethodState(calleeContext, registerCount, parameterTypes.size(),
                        parameterSize);
        assignCalleeMethodArguments(callerContext.getMethodState(), calleeMethodState);
        calleeContext.setMethodState(calleeMethodState);
        calleeContext.registerCaller(callerContext, getAddress());

        return calleeContext;
    }

    private void executeLocalMethod(String methodSignature, ExecutionContext callerContext,
                    ExecutionContext calleeContext) {
        ExecutionGraph graph = null;
        try {
            graph = vm.execute(methodSignature, calleeContext, callerContext, parameterRegisters);
        } catch (MaxAddressVisitsExceeded | MaxCallDepthExceeded | MaxMethodVisitsExceeded | MaxExecutionTimeExceeded e) {
            if (log.isWarnEnabled()) {
                log.warn(e.toString());
            }
        } catch (UnhandledVirtualException e) {
            // TODO: handle this properly - bubble up this exception to the node and stop executing here
            if (log.isWarnEnabled()) {
                log.warn(e.toString());
            }
        }

        if (graph == null) {
            // Maybe node visits or call depth exceeded?
            log.info("Problem executing {}, propagating ambiguity.", methodSignature);
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
                    ExecutionContext calleeContext, ExecutionNode node) {
        if (MethodEmulator.canEmulate(methodDescriptor)) {
            MethodEmulator emulator = new MethodEmulator(vm, calleeContext, methodDescriptor);
            emulator.emulate();
            sideEffectLevel = emulator.getSideEffectLevel();
            if (emulator.getExceptions().size() > 0) {
                node.clearChildren();
                node.setExceptions(emulator.getExceptions());
                return;
            }
        } else if (vm.getConfiguration().isSafe(methodDescriptor)) {
            assert allArgumentsKnown(calleeContext.getMethodState());

            MethodReflector reflector = new MethodReflector(methodDescriptor, returnType, parameterTypes, isStatic);
            reflector.reflect(calleeContext.getMethodState()); // playa play

            // Only safe, non-side-effect methods are allowed to be reflected.
            sideEffectLevel = SideEffect.Level.NONE;
        }

        if (!isStatic) {
            // This is virtual and the instance reference may have been initialized or mutated.
            HeapItem originalInstanceItem = callerContext.peekRegister(parameterRegisters[0]);
            HeapItem newInstanceItem = calleeContext.getMethodState().peekParameter(0);
            if (originalInstanceItem.getValue() != newInstanceItem.getValue()) {
                // Instance has been initialized, i.e. was UninitializedInstance
                // Use assignRegisterAndUpdateIdentities because multiple registers may have an identical
                // UninitializedInstance, and those need to be updated with the new instance.
                callerContext.assignRegisterAndUpdateIdentities(parameterRegisters[0], newInstanceItem);
            } else {
                boolean isMutable = !vm.getConfiguration().isImmutable(newInstanceItem.getType());
                if (isMutable) {
                    // The instance type is mutable so could have changed. Record that it was changed for the optimizer.
                    callerContext.assignRegister(parameterRegisters[0], newInstanceItem);
                }
            }
        }

        if (!"V".equals(returnType)) {
            HeapItem returnItem = calleeContext.getMethodState().readReturnRegister();
            callerContext.assignResultRegister(returnItem);
        }
    }

    /*
     * A method may not be declared in the type given by the method invocation. This method searches super and interface
     * hierarchy returns a method descriptor from the first class which implements the method. The method descriptor
     * points to the implementing class.
     */
    private @Nullable String resolveVirtualMethod(Object virtualTarget) {
        String virtualType = ClassNameUtils.toInternal(virtualTarget.getClass());
        String descriptor = methodSignature.split("->")[1];
        ClassManager classManager = vm.getClassManager();
        String targetMethod = resolveVirtualMethod(virtualType, descriptor, classManager, new HashSet<String>(4));

        return targetMethod != null ? targetMethod : methodSignature;
    }

    private @Nullable String resolveVirtualMethod(String className, String descriptor, ClassManager classManager,
                    Set<String> visited) {
        String signature = new StringBuilder(className).append("->").append(descriptor).toString();

        boolean isLocalMethod = classManager.isLocalMethod(signature);
        if (isLocalMethod && classManager.methodHasImplementation(signature)) {
            return signature;
        }

        if (vm.getConfiguration().isSafe(signature) && doesNonLocalMethodExist(className, descriptor)) {
            return signature;
        }

        if (!classManager.isLocalClass(className)) {
            // Can't trace any further up.
            // Note: already checked if this is white-listed Java API
            return null;
        }

        BuilderClassDef classDef = classManager.getClass(className);
        List<String> interfaces = classDef.getInterfaces();
        Set<String> parents = new HashSet<String>(interfaces.size() + 1, 1);
        parents.addAll(classDef.getInterfaces());
        if (null != classDef.getSuperclass()) {
            parents.add(classDef.getSuperclass());
        }

        for (String parent : parents) {
            if (visited.contains(parent)) {
                continue;
            }
            visited.add(className);
            String target = resolveVirtualMethod(parent, descriptor, classManager, visited);
            if (null != target) {
                return target;
            }
        }

        return null;
    }

    private static boolean doesNonLocalMethodExist(String className, String descriptor) {
        Class<?> klazz = null;
        try {
            klazz = Class.forName(ClassNameUtils.internalToBinary(className));
        } catch (ClassNotFoundException e) {
            return false;
        }

        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(descriptor);
        List<String> paramList = Utils.getParameterTypes(sb.toString());
        Class<?>[] params = new Class<?>[paramList.size()];
        for (int i = 0; i < paramList.size(); i++) {
            String paramName = paramList.get(i);
            try {
                if (ClassNameUtils.isPrimitive(paramName)) {
                    params[i] = ClassUtils.getClass(ClassNameUtils.internalToBinary(paramName));
                } else {
                    params[i] = Class.forName(ClassNameUtils.internalToBinary(paramName));
                }
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        String methodName = descriptor.split("\\(")[0];
        try {
            klazz.getMethod(methodName, params);
        } catch (NoSuchMethodException e) {
            return false;
        } catch (SecurityException e) {
            return false;
        }

        return true;
    }

}
