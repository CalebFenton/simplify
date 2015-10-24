package org.cf.smalivm.context;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxExecutionTimeExceeded;
import org.cf.smalivm.exception.MaxMethodVisitsExceeded;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionContext {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ExecutionContext.class.getSimpleName());

    private final VirtualMachine vm;
    private final TMap<String, ClassStatus> classNameToStatus;
    private final Heap heap;

    private MethodState mState;
    private ExecutionContext parent;
    private String methodDescriptor;

    private ExecutionContext callerContext;
    private int callerAddress;
    private int callDepth;

    private class ClassStatus {

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

        boolean isInitialized() {
            return isInitialized;
        }

        void setSideEffectLevel(SideEffect.Level level) {
            this.level = level;
            isInitialized = true;
        }
    }

    public ExecutionContext(VirtualMachine vm, String methodDescriptor) {
        this.vm = vm;
        this.methodDescriptor = methodDescriptor;
        heap = new Heap();
        callDepth = 0;

        // Since there's a context per execution for each address, these maps are
        // only populated as needed (by asking ancestors). So be frugal with size.
        classNameToStatus = new THashMap<String, ClassStatus>(0);
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

    public SideEffect.Level getClassSideEffectLevel(String className) {
        ExecutionContext ancestor = getAncestorWithClassName(className);
        if (ancestor == null) {
            return null;
        }

        return ancestor.classNameToStatus.get(className).getSideEffectLevel();
    }

    Heap getHeap() {
        return heap;
    }

    public Set<String> getInitializedClasses() {
        Set<String> classes = new HashSet<String>();
        for (Entry<String, ClassStatus> entry : classNameToStatus.entrySet()) {
            if (entry.getValue().isInitialized()) {
                classes.add(entry.getKey());
            }
        }

        return classes;
    }

    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    public MethodState getMethodState() {
        return mState;
    }

    public void initializeClass(String className, ClassState cState, SideEffect.Level level) {
        setClassState(className, cState);
        setClassInitialized(className, level);
    }

    public boolean isClassInitialized(String className) {
        ExecutionContext ancestor = getAncestorWithClassName(className);
        if (ancestor == null) {
            return false;
        }

        return ancestor.classNameToStatus.get(className).isInitialized();
    }

    public ClassState peekClassState(String className) {
        ExecutionContext ancestor = getAncestorWithClassName(className);
        if (ancestor == null) {
            ClassState templateClassState = vm.getTemplateClassState(this, className);
            setClassState(className, templateClassState);

            return templateClassState;
        } else if (ancestor != this) {
            ClassState ancestorClassState = ancestor.peekClassState(className);
            ClassState cState = ancestorClassState.getChild(this);
            SideEffect.Level level = ancestor.getClassSideEffectLevel(className);
            // Must initialize, because the ancestor probably just has the template class state.
            initializeClass(className, cState, level);

            return cState;
        }

        return classNameToStatus.get(className).getClassState();
    }

    public ClassState readClassState(String className) {
        staticallyInitializeClassIfNecessary(className);

        return peekClassState(className);
    }

    public void registerCaller(ExecutionContext callerContext, int callerAddress) {
        this.callDepth = callerContext.getCallDepth() + 1;
        this.callerContext = callerContext;
        this.callerAddress = callerAddress;
    }

    public void setClassState(String className, ClassState cState) {
        classNameToStatus.put(className, new ClassStatus(cState));
    }

    public void setMethodState(MethodState mState) {
        this.mState = mState;
    }

    public ExecutionContext spawnChild() {
        ExecutionContext child = new ExecutionContext(vm, getMethodDescriptor());
        child.setParent(this);

        return child;
    }

    public void staticallyInitializeClassIfNecessary(String className) {
        // This method should be called when a class is first used. A usage is:
        // 1.) The invocation of a method declared by the class (not inherited from a superclass)
        // 2.) The invocation of a constructor of the class (covered by #1)
        // 3.) The use or assignment of a field declared by a class (not inherited from a superclass), except for fields
        // that are both static and final, and are initialized by a compile-time constant expression.
        if (!vm.isLocalClass(className) || isClassInitialized(className)) {
            return;
        }

        String clinitDescriptor = className + "-><clinit>()V";
        if (vm.getClassManager().isLocalMethod(clinitDescriptor)) {
            // TODO: determine what the call stack actually is when the vm clinit's a class
            // this assumes the call stack is empty
            ExecutionContext initContext = vm.spawnRootExecutionContext(clinitDescriptor);

            ClassState cState = initContext.peekClassState(className);
            initContext.initializeClass(className, cState, SideEffect.Level.NONE);

            ExecutionGraph graph = null;
            try {
                graph = vm.execute(clinitDescriptor, initContext, this, null);
            } catch (MaxAddressVisitsExceeded | MaxCallDepthExceeded | MaxMethodVisitsExceeded | MaxExecutionTimeExceeded e) {
                log.warn(e.toString());
            } catch (UnhandledVirtualException e) {
                // TODO: handle this properly by bubbling up the exception
                if (log.isWarnEnabled()) {
                    log.warn(e.toString());
                }
            }

            SideEffect.Level sideEffectLevel;
            if (graph == null) {
                // Error executing. Assume the worst.
                sideEffectLevel = SideEffect.Level.STRONG;
            } else {
                sideEffectLevel = graph.getHighestSideEffectLevel();
            }
            setClassSideEffectLevel(className, sideEffectLevel);
        } else {
            // No static initializer for this class.
            setClassInitialized(className, SideEffect.Level.NONE);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (mState != null) {
            sb.append("Method state: ").append(mState.toString());
        }
        if (sb.length() > 0) {
            sb.append('\n');
        }
        Set<String> initializedClasses = getInitializedClasses();
        if (initializedClasses.size() < 4) {
            // Too many will trash the heap
            for (String className : initializedClasses) {
                // ClassState cState = peekClassState(className);
                ClassState cState = classNameToStatus.get(className).getClassState();
                sb.append("Class state: ").append(className).append(' ').append(cState);
            }
        }

        return sb.toString();
    }

    private ExecutionContext getAncestorWithClassName(String className) {
        ExecutionContext ancestor = this;
        do {
            ClassStatus status = ancestor.classNameToStatus.get(className);
            if (status != null) {
                return ancestor;
            }

            ancestor = ancestor.getParent();
        } while (ancestor != null);

        return null;
    }

    private void setClassInitialized(String className, SideEffect.Level level) {
        peekClassState(className);
        classNameToStatus.get(className).setSideEffectLevel(level);
    }

    public void setParent(ExecutionContext parent) {
        setShallowParent(parent);

        MethodState childMethodState = parent.getMethodState().getChild(this);
        setMethodState(childMethodState);
    }

    public void setShallowParent(ExecutionContext parent) {
        assert parent.getMethodState() != null;

        this.parent = parent;
        callDepth = parent.getCallDepth();
        getHeap().setParent(parent.getHeap());
    }

    public ExecutionContext getParent() {
        return parent;
    }

    void setClassSideEffectLevel(String className, SideEffect.Level level) {
        peekClassState(className);
        classNameToStatus.get(className).setSideEffectLevel(level);
    }

}
