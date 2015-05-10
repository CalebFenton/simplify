package org.cf.smalivm.context;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxMethodVisitsExceeded;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionContext {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ExecutionContext.class.getSimpleName());

    private final VirtualMachine vm;
    // TODO: see if these can be reduced to one or two.
    // esp. try to get rid of initializedClasses by using classNameToState (?)
    private final TMap<String, ClassState> classNameToState;
    private final TMap<String, SideEffect.Level> classNameToSideEffectLevel;
    private final Set<String> initializedClasses;
    private final Heap heap;

    private MethodState mState;
    private ExecutionContext parent;
    private String methodDescriptor;

    private ExecutionContext callerContext;
    private int callerAddress;
    private int callDepth;

    public ExecutionContext(VirtualMachine vm, String methodDescriptor) {
        this.vm = vm;
        // There is a context per execution of each address, so be frugal with sizes.
        classNameToState = new THashMap<String, ClassState>(0);
        classNameToSideEffectLevel = new THashMap<String, SideEffect.Level>(0);
        initializedClasses = new THashSet<String>(0);
        heap = new Heap();
        this.methodDescriptor = methodDescriptor;
        callDepth = 0;
    }

    public int getCallDepth() {
        return callDepth;
    }

    public SideEffect.Level getClassStateSideEffectLevel(String className) {
        ExecutionContext ancestor = getAncestorWithClassName(className);
        if (ancestor == null) {
            return null;
        }

        SideEffect.Level level = ancestor.classNameToSideEffectLevel.get(className);
        if (ancestor != this) {
            classNameToSideEffectLevel.put(className, level);
        }

        return level;
    }

    public Heap getHeap() {
        return heap;
    }

    public Set<String> getInitializedClasses() {
        return initializedClasses;
    }

    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    public MethodState getMethodState() {
        return mState;
    }

    public void initializeClass(String className, ClassState cState, SideEffect.Level level) {
        setClassState(className, cState, level);
        setClassInitialized(className);
    }

    public boolean isClassInitialized(String className) {
        ExecutionContext ancestor = getAncestorWithClassName(className);
        if (ancestor == null) {
            return false;
        }

        if (ancestor != this) {
            if (ancestor.initializedClasses.contains(className)) {
                initializedClasses.add(className);
            }
        }

        return initializedClasses.contains(className);
    }

    public ClassState peekClassState(String className) {
        ExecutionContext ancestor = getAncestorWithClassName(className);
        if (ancestor == null) {
            vm.addTemplateClassState(this, className);
        } else if (ancestor != this) {
            ClassState ancestorClassState = ancestor.peekClassState(className);
            ClassState cState = ancestorClassState.getChild(this);
            SideEffect.Level level = ancestor.getClassStateSideEffectLevel(className);
            // Must initialize, because the ancestor probably just has the template class state.
            initializeClass(className, cState, level);
        }

        return classNameToState.get(className);
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

    public void setClassState(String className, ClassState cState, SideEffect.Level level) {
        classNameToState.put(className, cState);
        classNameToSideEffectLevel.put(className, level);
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

        SideEffect.Level sideEffectLevel = SideEffect.Level.NONE;
        String clinitDescriptor = className + "-><clinit>()V";
        if (vm.getClassManager().isLocalMethod(clinitDescriptor)) {
            // TODO: determine what the call stack actually is when the vm clinit's a class
            // this assumes the call stack is empty
            ExecutionContext initContext = vm.spawnExecutionContext(clinitDescriptor);

            ClassState cState = initContext.peekClassState(className);
            initContext.initializeClass(className, cState, SideEffect.Level.NONE);

            ExecutionGraph graph = null;
            try {
                graph = vm.execute(clinitDescriptor, initContext, this, null);
            } catch (MaxAddressVisitsExceeded | MaxCallDepthExceeded | MaxMethodVisitsExceeded e) {
                log.warn(e.toString());
            } catch (UnhandledVirtualException e) {
                // TODO: handle this properly
                if (log.isWarnEnabled()) {
                    log.warn(e.toString());
                }
            }

            if (graph == null) {
                // Error executing. Assume the worst.
                sideEffectLevel = SideEffect.Level.STRONG;
            } else {
                sideEffectLevel = graph.getHighestSideEffectLevel();
            }
        } else {
            // No clinit for this class.
            setClassInitialized(className);
        }
        setClassSideEffectType(className, sideEffectLevel);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (mState != null) {
            sb.append("Method: ").append(mState.toString());
        }
        if (sb.length() > 0) {
            sb.append('\n');
        }
        if (getInitializedClasses().size() < 4) {
            // Too many will trash the heap
            for (String className : getInitializedClasses()) {
                ClassState cState = peekClassState(className);
                sb.append("Class: ").append(className).append(' ').append(cState);
            }
        }

        return sb.toString();
    }

    private ExecutionContext getAncestorWithClassName(String className) {
        ExecutionContext ancestor = this;
        do {
            if (ancestor.classNameToState.containsKey(className)) {
                return ancestor;
            }

            ancestor = ancestor.getParent();
        } while (ancestor != null);

        return ancestor;
    }

    private void setClassInitialized(String className) {
        initializedClasses.add(className);
    }

    private void setParent(ExecutionContext parent) {
        assert parent.getMethodState() != null;

        this.parent = parent;
        callDepth = parent.getCallDepth();
        getHeap().setParent(parent.getHeap());

        MethodState childMethodState = parent.getMethodState().getChild(this);
        setMethodState(childMethodState);
    }

    ExecutionContext getParent() {
        return parent;
    }

    void setClassSideEffectType(String className, SideEffect.Level sideEffectLevel) {
        classNameToSideEffectLevel.put(className, sideEffectLevel);
    }

}
