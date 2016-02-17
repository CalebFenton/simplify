package org.cf.smalivm.opcode;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ClassUtils;
import org.cf.smalivm.MethodReflector;
import org.cf.smalivm.ObjectInstantiator;
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
import org.jf.dexlib2.iface.ClassDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvokeOp extends ExecutionContextOp {

    private static final Logger log = LoggerFactory.getLogger(InvokeOp.class.getSimpleName());

    private final String methodSignature;
    private final int[] parameterRegisters;
    private final List<String> parameterTypes;
    private final String[] analyzedParameterTypes;
    private final String returnType;
    private final VirtualMachine vm;
    private final ClassManager classManager;
    private final boolean isStatic;
    private SideEffect.Level sideEffectLevel;

    InvokeOp(MethodLocation location, MethodLocation child, String methodSignature, String returnType,
                    int[] parameterRegisters, List<String> parameterTypes, VirtualMachine vm, boolean isStatic) {
        super(location, child);

        this.methodSignature = methodSignature;
        this.returnType = returnType;
        this.parameterRegisters = parameterRegisters;
        this.parameterTypes = parameterTypes;
        analyzedParameterTypes = new String[parameterTypes.size()];
        this.vm = vm;
        classManager = vm.getClassManager();
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

        MethodState callerMethodState = ectx.getMethodState();
        if (methodSignature.equals("Ljava/lang/Object;-><init>()V")) {
            // Object init is a special little snow flake
            try {
                executeObjectInit(callerMethodState);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.error("Unexpected real exception initializing Object", e);
            }
            return;
        }

        analyzeParameterTypes(callerMethodState);

        String targetMethod = methodSignature;
        if (getName().startsWith("invoke-virtual")) { // -virtual/range
            // Resolve what the actual virtual target is because method call may be to interface or abstract class.
            int targetRegister = parameterRegisters[0];
            HeapItem item = ectx.getMethodState().peekRegister(targetRegister);
            targetMethod = resolveVirtualMethod(item.getValue());
        }

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
            if (classManager.isLocalMethod(targetMethod)) {
                if (classManager.isFrameworkClass(targetMethod) && !classManager.isSafeFrameworkClass(targetMethod)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Not executing unsafe framework method: {}. Assuming maxiumum ambiguity.",
                                        targetMethod);
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

    private void analyzeParameterTypes(MethodState callerState) {
        /*
         * Type can be confused here. For example, creating a short, int, boolean, or *null* all appear:
         * const/4 v0,0x0 (could be true, (int)0, or (short)0, null, etc.)
         * If a more restrictive type is given in the method signature, prefer that, for example:
         * method argument is an int but signature declares it as boolean, so switch it to boolean
         * However, if the type is less specific, such as a super class or interface, do not use the less specific
         * type. For example:
         * method argument is Lchild_class; but signature says Lparent_class;, prefer Lchild_class;
         */
        for (int i = 0; i < parameterRegisters.length; i++) {
            int callerRegister = parameterRegisters[i];
            HeapItem item = callerState.readRegister(callerRegister);
            String parameterType = parameterTypes.get(i);
            String type;
            if (item.isPrimitive()) {
                type = parameterType;
            } else {
                String baseType = item.getComponentBase();
                Set<String> ancestorNames = getAncestorNames(baseType, new HashSet<String>());
                if (ancestorNames.contains(parameterType)) {
                    type = item.getType();
                } else {
                    type = parameterType;
                }
            }
            analyzedParameterTypes[i] = type;
        }
    }

    private void assignCalleeMethodArguments(MethodState callerState, MethodState calleeState) {
        int parameterRegister = calleeState.getParameterStart();
        for (int i = 0; i < parameterRegisters.length; i++) {
            int callerRegister = parameterRegisters[i];
            HeapItem item = callerState.readRegister(callerRegister);
            String parameterType = analyzedParameterTypes[i];
            Object value = item.getValue();
            if (item.isPrimitive() && !item.isUnknown()) {
                boolean hasNullByteValue = item.getType().equals("I") && value instanceof Number && item
                                .getIntegerValue() == 0;
                if (hasNullByteValue && ClassNameUtils.isObject(parameterType)) {
                    value = null;
                } else {
                    // An I type may actually be a S, B, C, etc. Pass the cast type to simplify things.
                    value = Utils.castToPrimitive(value, parameterType);
                }
            }
            HeapItem parameterItem = new HeapItem(value, parameterType);
            calleeState.assignParameter(parameterRegister, parameterItem);
            parameterRegister += Utils.getRegisterSize(parameterType);
        }
    }

    private void assumeMaximumUnknown(MethodState callerMethodState) {
        // TODO: add option to mark all class states unknown instead of just method state
        for (int i = 0; i < parameterTypes.size(); i++) {
            int register = parameterRegisters[i];
            HeapItem item = callerMethodState.readRegister(register);
            Object value = item.getValue();
            if (null == value) {
                // Nulls don't mutate.
                continue;
            }

            String type = analyzedParameterTypes[i];
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

            callerMethodState.pokeRegister(register, item);
        }

        if (!"V".equals(returnType)) {
            HeapItem item = HeapItem.newUnknown(returnType);
            callerMethodState.assignResultRegister(item);
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
        } else {
            if (methodSignature.contains(";-><init>(")) {
                // This was a call to a local parent <init> method
                int calleeInstanceRegister = calleeContext.getMethodState().getParameterStart();
                HeapItem newInstance = graph.getTerminatingRegisterConsensus(calleeInstanceRegister);
                int instanceRegister = parameterRegisters[0];
                callerContext.getMethodState().assignRegisterAndUpdateIdentities(instanceRegister, newInstance);
            }
        }

        sideEffectLevel = graph.getHighestSideEffectLevel();
    }

    private void executeNonLocalMethod(String methodDescriptor, MethodState callerMethodState,
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

            MethodReflector reflector = new MethodReflector(vm, methodDescriptor, returnType, parameterTypes, isStatic);
            reflector.reflect(calleeContext.getMethodState()); // playa play

            // Only safe, non-side-effect methods are allowed to be reflected.
            sideEffectLevel = SideEffect.Level.NONE;
        }

        if (!isStatic) {
            // This is virtual and the instance reference may have been initialized or mutated.
            HeapItem originalInstanceItem = callerMethodState.peekRegister(parameterRegisters[0]);
            HeapItem newInstanceItem = calleeContext.getMethodState().peekParameter(0);
            if (originalInstanceItem.getValue() != newInstanceItem.getValue()) {
                // Instance has been initialized, i.e. was UninitializedInstance
                // Use assignRegisterAndUpdateIdentities because multiple registers may have an identical
                // UninitializedInstance, and those need to be updated with the new instance.
                callerMethodState.assignRegisterAndUpdateIdentities(parameterRegisters[0], newInstanceItem);
            } else {
                boolean isMutable = !vm.getConfiguration().isImmutable(newInstanceItem.getType());
                if (isMutable) {
                    // The instance type is mutable so could have changed. Record that it was changed for the optimizer.
                    callerMethodState.assignRegister(parameterRegisters[0], newInstanceItem);
                }
            }
        }

        if (!"V".equals(returnType)) {
            HeapItem returnItem = calleeContext.getMethodState().readReturnRegister();
            callerMethodState.assignResultRegister(returnItem);
        }
    }

    private void executeObjectInit(MethodState callerMethodState) throws ClassNotFoundException,
                    InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        int instanceRegister = parameterRegisters[0];
        HeapItem instanceItem = callerMethodState.peekRegister(instanceRegister);
        String binaryName = ClassNameUtils.internalToBinary(instanceItem.getType());
        Class<?> klazz = vm.getClassLoader().loadClass(binaryName);
        Object newInstance = ObjectInstantiator.newInstance(klazz);
        HeapItem newInstanceItem = new HeapItem(newInstance, instanceItem.getType());
        callerMethodState.assignRegisterAndUpdateIdentities(instanceRegister, newInstanceItem);
    }

    private Set<String> getAncestorNames(String className, Set<String> ancestorNames) {
        if (ancestorNames.contains(className)) {
            return new HashSet<String>();
        }

        if (classManager.isLocalClass(className)) {
            // Prefer class manager to avoid having to load the class which fails for java.lang.* classes which are
            // unique to Android, among other complexities.
            ClassDef klazz = classManager.getClass(className);
            for (String interfaceName : klazz.getInterfaces()) {
                ancestorNames.add(interfaceName);
                getAncestorNames(interfaceName, ancestorNames);
            }
            String superName = klazz.getSuperclass();
            if (superName != null) {
                ancestorNames.add(superName);
                getAncestorNames(superName, ancestorNames);
            }
        } else {
            String binaryName = ClassNameUtils.internalToBinary(className);
            Class<?> klazz;
            try {
                klazz = vm.getClassLoader().loadClass(binaryName);
            } catch (ClassNotFoundException e) {
                log.error("Error loading class " + className, e);
                return ancestorNames;
            }
            for (Class<?> interfaceClass : klazz.getInterfaces()) {
                String internalName = ClassNameUtils.toInternal(interfaceClass);
                ancestorNames.add(internalName);
                getAncestorNames(internalName, ancestorNames);
            }
            if (klazz.getSuperclass() != null) {
                String internalName = ClassNameUtils.toInternal(klazz.getSuperclass());
                ancestorNames.add(internalName);
                getAncestorNames(internalName, ancestorNames);
            }
        }

        return ancestorNames;
    }

    private @Nullable String resolveVirtualMethod(Object virtualTarget) {
        /*
         * A method may not be declared in the type given by the method invocation. This method searches super and
         * interface
         * hierarchy returns a method descriptor from the first class which implements the method. The method descriptor
         * points to the implementing class.
         */
        String virtualType = ClassNameUtils.toInternal(virtualTarget.getClass());
        String descriptor = methodSignature.split("->")[1];
        String methodName = descriptor.split("\\(")[0];
        String targetMethod = resolveVirtualMethod(methodName, descriptor, parameterTypes, virtualType,
                        new HashSet<String>(4));

        return targetMethod != null ? targetMethod : methodSignature;
    }

    private @Nullable String resolveVirtualMethod(String methodName, String descriptor, List<String> parameterTypes,
                    String className, Set<String> visited) {
        String signature = new StringBuilder(className).append("->").append(descriptor).toString();
        boolean isLocalMethod = classManager.isLocalMethod(signature);
        if (isLocalMethod && classManager.methodHasImplementation(signature)) {
            return signature;
        }

        if (vm.getConfiguration().isSafe(signature) && doesNonLocalMethodExist(methodName, descriptor, parameterTypes,
                        className, signature)) {
            return signature;
        }

        if (!classManager.isLocalClass(className)) {
            // Can't trace any further up and already checked if white-listed Java API
            return null;
        }

        Set<String> ancestorNames = getAncestorNames(className, new HashSet<String>());
        for (String ancestorName : ancestorNames) {
            if (visited.contains(ancestorName)) {
                continue;
            }
            visited.add(className);
            String target = resolveVirtualMethod(methodName, descriptor, parameterTypes, ancestorName, visited);
            if (null != target) {
                return target;
            }
        }

        return null;
    }

    private static boolean doesNonLocalMethodExist(String methodName, String descriptor, List<String> paramList,
                    String className, String signature) {
        Class<?> klazz = null;
        try {
            klazz = Class.forName(ClassNameUtils.internalToBinary(className));
        } catch (ClassNotFoundException e) {
            return false;
        }

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
