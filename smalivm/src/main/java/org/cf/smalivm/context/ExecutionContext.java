package org.cf.smalivm.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionContext {

    private static final Logger log = LoggerFactory.getLogger(ExecutionContext.class.getSimpleName());

    private int callDepth;
    private final Map<String, SideEffect.Level> classNameToSideEffectType;
    private final Map<String, ClassState> classNameToState;
    private final Heap heap;
    private final Set<String> initializedClasses;

    private MethodState mState;
    private ExecutionContext parent;
    private final VirtualMachine vm;

    public ExecutionContext(ExecutionContext other) {
        vm = other.vm;
        if (other.mState != null) {
            mState = new MethodState(other.mState, this);
        }
        classNameToState = new HashMap<String, ClassState>(other.classNameToState.size());
        for (String className : other.classNameToState.keySet()) {
            ClassState otherClassState = other.peekClassState(className);
            ClassState cState = new ClassState(otherClassState, this);
            setClassState(className, cState);
        }
        classNameToSideEffectType = new HashMap<String, SideEffect.Level>(other.classNameToSideEffectType);
        initializedClasses = new HashSet<String>(other.initializedClasses);
        heap = new Heap(other.getHeap());
        callDepth = other.getCallDepth();
    }

    public ExecutionContext(VirtualMachine vm) {
        this.vm = vm;
        classNameToState = new HashMap<String, ClassState>();
        classNameToSideEffectType = new HashMap<String, SideEffect.Level>();
        initializedClasses = new HashSet<String>();
        heap = new Heap();
        callDepth = 0;
    }

    public int getCallDepth() {
        return callDepth;
    }

    public ExecutionContext getChild() {
        ExecutionContext child = new ExecutionContext(this.vm);
        child.setCallDepth(callDepth);
        child.setParent(this);
        child.getHeap().setParent(this.getHeap());

        MethodState childMethodState = getMethodState().getChild(child);
        child.setMethodState(childMethodState);

        for (String className : classNameToState.keySet()) {
            ClassState childClassState = peekClassState(className).getChild(child);
            child.initializeClass(className, childClassState);
        }

        return child;
    }

    public Set<String> getInitializedClasses() {
        return initializedClasses;
    }

    public ClassState accessClassState(String className) {
        staticallyInitializeClassIfNecessary(className);

        return peekClassState(className);
    }

    public SideEffect.Level getClassStateSideEffectType(String className) {
        staticallyInitializeClassIfNecessary(className);

        return classNameToSideEffectType.get(className);
    }

    public Heap getHeap() {
        return heap;
    }

    public MethodState getMethodState() {
        return mState;
    }

    public void initializeClass(String className, ClassState cState) {
        setClassState(className, cState);
        initializedClasses.add(className);
    }

    public void setCallDepth(int callDepth) {
        this.callDepth = callDepth;
    }

    public void setClassState(String className, ClassState cState) {
        classNameToState.put(className, cState);
    }

    public void setMethodState(MethodState mState) {
        this.mState = mState;
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

        ExecutionContext templateContext = getTemplate();
        ClassState templateClassState = templateContext.peekClassState(className);
        ClassState cState = new ClassState(templateClassState, this);
        initializeClass(className, cState);

        SideEffect.Level sideEffectLevel = SideEffect.Level.NONE;
        String clinitDescriptor = className + "-><clinit>()V";

        if (vm.isLocalMethod(clinitDescriptor)) {
            ExecutionGraph graph = vm.execute(clinitDescriptor, this);
            if (graph == null) {
                // Error executing. Assume the worst.
                setClassSideEffectType(className, SideEffect.Level.STRONG);
            } else {
                sideEffectLevel = graph.getHighestSideEffectLevel();
            }
        } else {
            // No clinit for this class.
        }
        setClassSideEffectType(className, sideEffectLevel);
    }

    private ExecutionContext getTemplate() {
        ExecutionContext result = this;
        while (result.getParent() != null) {
            result = result.getParent();
        }

        return result;
    }

    public boolean isClassInitialized(String className) {
        return initializedClasses.contains(className);
    }

    private void setParent(ExecutionContext parent) {
        this.parent = parent;
    }

    ExecutionContext getParent() {
        return parent;
    }

    public ClassState peekClassState(String className) {
        return classNameToState.get(className);
    }

    void setClassSideEffectType(String className, SideEffect.Level sideEffectLevel) {
        classNameToSideEffectType.put(className, sideEffectLevel);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (mState != null) {
            sb.append(mState.toString());
        }
        for (String className : getInitializedClasses()) {
            ClassState cState = peekClassState(className);
            sb.append(cState);
        }

        return sb.toString();
    }

}
