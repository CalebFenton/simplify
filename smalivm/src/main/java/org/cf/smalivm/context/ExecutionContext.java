package org.cf.smalivm.context;

import com.rits.cloning.Cloner;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.TemplateStateFactory;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.exception.VirtualMachineException;
import org.cf.smalivm.type.VirtualType;
import org.cf.smalivm.type.VirtualMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class ExecutionContext {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ExecutionContext.class.getSimpleName());
    private final VirtualMachine vm;
    private final Map<VirtualType, ClassStatus> classToStatus;
    private final Heap heap;
    private final VirtualMethod method;
    private MethodState mState;
    private ExecutionContext parent;
    private ExecutionContext callerContext;
    private int callerAddress;
    private int callDepth;

    public ExecutionContext(VirtualMachine vm, VirtualMethod method) {
        this.vm = vm;
        this.method = method;
        Cloner cloner = ClonerFactory.instance(vm.getClassManager(), vm.getClassLoader(), vm.getConfiguration());
        heap = new Heap(cloner);
        callDepth = 0;

        /*
         * Since there's an ExecutionContext for each execution of each op and these maps are only populated when
         * they're needed (by pulling from ancestors), so be frugal with size.
         */
        classToStatus = new HashMap<>(0);
    }

    public int getCallDepth() {
        return callDepth;
    }

    public int getCallerAddress() {
        return callerAddress;
    }

    public ExecutionContext getCallerContext() {
        return callerContext;
    }

    public SideEffect.Level getClassSideEffectLevel(VirtualType virtualClass) {
        ExecutionContext ancestor = getAncestorWithClass(virtualClass);
        if (ancestor == null) {
            return null;
        }

        return ancestor.classToStatus.get(virtualClass).getSideEffectLevel();
    }

    public Set<VirtualType> getInitializedClasses() {
        return classToStatus.entrySet().stream().filter(entry -> entry.getValue().isInitialized()).map(Entry::getKey)
                       .collect(Collectors.toSet());
    }

    public VirtualMethod getMethod() {
        return method;
    }

    public MethodState getMethodState() {
        return mState;
    }

    public void setMethodState(MethodState mState) {
        this.mState = mState;
    }

    public ExecutionContext getParent() {
        return parent;
    }

    public void setParent(ExecutionContext parent) {
        setShallowParent(parent);

        MethodState childMethodState = parent.getMethodState().getChild(this);
        setMethodState(childMethodState);
    }

    public void initializeClass(ClassState cState, SideEffect.Level level) {
        setClassState(cState);
        setClassInitialized(cState.getVirtualClass(), level);
    }

    public boolean isClassInitialized(VirtualType virtualClass) {
        ExecutionContext ancestor = getAncestorWithClass(virtualClass);

        return ancestor != null && ancestor.classToStatus.get(virtualClass).isInitialized();
    }

    public ClassState peekClassState(VirtualType virtualClass) {
        ExecutionContext ancestor = getAncestorWithClass(virtualClass);
        if (ancestor == null) {
            // VirtualClass has not been initialized; build template and cache it locally to track initialization state
            ClassState templateClassState = TemplateStateFactory.forClass(this, virtualClass);
            setClassState(templateClassState);

            return templateClassState;
        } else if (ancestor != this) {
            ClassState ancestorClassState = ancestor.peekClassState(virtualClass);
            ClassState cState = ancestorClassState.getChild(this);
            SideEffect.Level level = ancestor.getClassSideEffectLevel(virtualClass);
            // Must initialize, because the ancestor probably just has the template class state.
            initializeClass(cState, level);

            return cState;
        }

        return classToStatus.get(virtualClass).getClassState();
    }

    public ClassState readClassState(VirtualType virtualClass) {
        staticallyInitializeClassIfNecessary(virtualClass);

        return peekClassState(virtualClass);
    }

    public void registerCaller(ExecutionContext callerContext, int callerAddress) {
        this.callDepth = callerContext.getCallDepth() + 1;
        this.callerContext = callerContext;
        this.callerAddress = callerAddress;
    }

    public void setClassState(ClassState cState) {
        classToStatus.put(cState.getVirtualClass(), new ClassStatus(cState));
    }

    public void setShallowParent(ExecutionContext parent) {
        assert parent.getMethodState() != null;

        this.parent = parent;
        callDepth = parent.getCallDepth();
        getHeap().setParent(parent.getHeap());
    }

    public ExecutionContext spawnChild() {
        ExecutionContext child = new ExecutionContext(vm, method);
        child.setParent(this);

        return child;
    }

    public void staticallyInitializeClassIfNecessary(VirtualType virtualClass) {
        /*
         * This method should be called when a class is first used. A usage is:
         * 1.) The invocation of a method declared by the class (not inherited from a superclass)
         * 2.) The invocation of a constructor of the class (covered by #1)
         * 3.) The use or assignment of a field declared by a class (not inherited from a superclass), except for fields
         * that are both static and final, and are initialized by a compile-time constant expression.
         */
        if (vm.isSafe(virtualClass) || isClassInitialized(virtualClass)) {
            // Safe classes are OK to reflect to get fields. Don't need to also put them in VM.
            return;
        }

        // Initialize class ancestors as well
        for (VirtualType ancestor : virtualClass.getAncestors()) {
            int accessFlags = virtualClass.getClassDef().getAccessFlags();
            if (Modifier.isAbstract(accessFlags) || Modifier.isInterface(accessFlags) ||
                Modifier.isNative(accessFlags)) {
                continue;
            }
            staticallyInitializeClassIfNecessary(ancestor);
        }

        VirtualMethod method = virtualClass.getMethod("<clinit>()V");
        if (method == null) {
            /*
             * Class has no static initializer, but still need to peek and set the class state to set any fields
             * with literal values. E.g.
             * .field public static myInt:I = 0x4
             */
            ClassState cState = peekClassState(virtualClass);
            initializeClass(cState, SideEffect.Level.NONE);
            return;
        }

        // TODO: determine what the call stack actually is when the vm clinit's a class
        // this assumes the call stack is empty
        ExecutionContext initContext = vm.spawnRootContext(method);
        ClassState cState = initContext.peekClassState(virtualClass);
        initContext.initializeClass(cState, SideEffect.Level.NONE);

        ExecutionGraph graph = null;
        try {
            graph = vm.execute(initContext, this, null);
        } catch (VirtualMachineException e) {
            log.warn(e.toString());
            if (e instanceof UnhandledVirtualException) {
                // TODO: handle this properly by bubbling up the exception
            }
        }

        SideEffect.Level sideEffectLevel;
        if (graph == null) {
            // Must have been an execution error. Assume the worst case for side effects.
            sideEffectLevel = SideEffect.Level.STRONG;
        } else {
            sideEffectLevel = graph.getHighestSideEffectLevel();
        }
        classToStatus.get(virtualClass).setSideEffectLevel(sideEffectLevel);
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean onlyPeekCachedRegisters) {
        StringBuilder sb = new StringBuilder();
        if (mState != null) {
            sb.append(method.getSignature()).append(" State: ").append(mState.toString(onlyPeekCachedRegisters));
        }
        if (sb.length() > 0) {
            sb.append('\n');
        }
        Set<VirtualType> initializedClasses = getInitializedClasses();
        if (initializedClasses.size() < 4) {
            // Too many will trash the heap
            for (VirtualType virtualClass : initializedClasses) {
                ClassState cState = classToStatus.get(virtualClass).getClassState();
                sb.append("Class state: ").append(virtualClass).append(' ').append(cState);
            }
        }

        return sb.toString();
    }

    Heap getHeap() {
        return heap;
    }

    private ExecutionContext getAncestorWithClass(VirtualType virtualClass) {
        ExecutionContext ancestor = this;
        do {
            ClassStatus status = ancestor.classToStatus.get(virtualClass);
            if (status != null) {
                return ancestor;
            }

            ancestor = ancestor.getParent();
        } while (ancestor != null);

        return null;
    }

    private void setClassInitialized(VirtualType virtualClass, SideEffect.Level level) {
        peekClassState(virtualClass);
        classToStatus.get(virtualClass).setSideEffectLevel(level);
    }

    private static class ClassStatus {

        private boolean isInitialized;
        private ClassState cState;
        private SideEffect.Level level;

        ClassStatus(ClassState cState) {
            this.cState = cState;
        }

        ClassState getClassState() {
            return cState;
        }

        SideEffect.Level getSideEffectLevel() {
            return level;
        }

        void setSideEffectLevel(SideEffect.Level level) {
            this.level = level;
            isInitialized = true;
        }

        boolean isInitialized() {
            return isInitialized;
        }
    }

}
