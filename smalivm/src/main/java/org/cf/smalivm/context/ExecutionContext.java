package org.cf.smalivm.context;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionContext {

    private static final Logger log = LoggerFactory.getLogger(ExecutionContext.class.getSimpleName());

    private int callDepth;
    private final TMap<String, SideEffect.Level> classNameToSideEffectLevel;
    private final TMap<String, ClassState> classNameToState;
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
        classNameToState = new THashMap<String, ClassState>(other.classNameToState.size());
        for (String className : other.classNameToState.keySet()) {
            ClassState otherClassState = other.peekClassState(className);
            ClassState cState = new ClassState(otherClassState, this);
            setClassState(className, cState);
        }
        classNameToSideEffectLevel = new THashMap<String, SideEffect.Level>(other.classNameToSideEffectLevel);
        initializedClasses = new THashSet<String>(other.initializedClasses);
        heap = new Heap(other.getHeap());
        callDepth = other.getCallDepth();
    }

    public ExecutionContext(VirtualMachine vm) {
        this.vm = vm;
        classNameToState = new THashMap<String, ClassState>(0);
        classNameToSideEffectLevel = new THashMap<String, SideEffect.Level>(0);
        initializedClasses = new THashSet<String>(0);
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

        // Almost every op access the method state
        MethodState childMethodState = getMethodState().getChild(child);
        child.setMethodState(childMethodState);

        // Sparse context testing, for ref only
        // for (String className : classNameToState.keySet()) {
        // ClassState childClassState = peekClassState(className).getChild(child);
        // child.initializeClass(className, childClassState);
        // }

        return child;
    }

    public Set<String> getInitializedClasses() {
        return initializedClasses;
    }

    public ClassState readClassState(String className) {
        staticallyInitializeClassIfNecessary(className);

        return peekClassState(className);
    }

    public SideEffect.Level getClassStateSideEffectLevel(String className) {
        ExecutionContext ancestor = getAncestorWithClassName(className);
        SideEffect.Level level = ancestor.classNameToSideEffectLevel.get(className);
        if (ancestor != this) {
            classNameToSideEffectLevel.put(className, level);
        }

        return level;
    }

    public Heap getHeap() {
        return heap;
    }

    public MethodState getMethodState() {
        return mState;
    }

    public void initializeClass(String className, ClassState cState, SideEffect.Level level) {
        setClassState(className, cState);
        initializedClasses.add(className);
        classNameToSideEffectLevel.put(className, level);
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

        SideEffect.Level sideEffectLevel = SideEffect.Level.NONE;
        String clinitDescriptor = className + "-><clinit>()V";
        if (vm.isLocalMethod(clinitDescriptor)) {
            ExecutionContext initContext = vm.getRootExecutionContext(clinitDescriptor);
            ClassState templateClassState = getTemplate().peekClassState(className);
            ClassState cState = new ClassState(templateClassState, initContext);
            // real level is set after clinit execution
            initContext.initializeClass(className, cState, SideEffect.Level.NONE);
            for (String currentClassName : vm.getLocalClasses()) {
                if (!isClassInitialized(currentClassName)) {
                    continue;
                }

                ClassState callerClassState = peekClassState(currentClassName);
                cState = new ClassState(callerClassState, initContext);
                for (String fieldNameAndType : vm.getFieldNameAndTypes(currentClassName)) {
                    Object value = callerClassState.peekField(fieldNameAndType);
                    cState.pokeField(fieldNameAndType, value);
                }
                SideEffect.Level level = getClassStateSideEffectLevel(currentClassName);
                initContext.initializeClass(currentClassName, cState, level);
            }

            ExecutionGraph graph = vm.execute(clinitDescriptor, initContext, this, null);
            if (graph == null) {
                // Error executing. Assume the worst.
                sideEffectLevel = SideEffect.Level.STRONG;
            } else {
                sideEffectLevel = graph.getHighestSideEffectLevel();

            }
        } else {
            // No clinit for this class.
            ClassState cState = new ClassState(this, className, 0);
            initializeClass(className, cState, SideEffect.Level.NONE);
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
        ExecutionContext ancestor = getAncestorWithClassName(className);
        if (ancestor != this) {
            if (ancestor.initializedClasses.contains(className)) {
                initializedClasses.add(className);
            }
        }

        return initializedClasses.contains(className);
    }

    private void setParent(ExecutionContext parent) {
        this.parent = parent;
    }

    ExecutionContext getParent() {
        return parent;
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

    public ClassState peekClassState(String className) {
        ExecutionContext ancestor = getAncestorWithClassName(className);
        if (ancestor != this) {
            ClassState ancestorClassState = ancestor.peekClassState(className);
            ClassState cState = ancestorClassState.getChild(this);
            SideEffect.Level level = ancestor.getClassStateSideEffectLevel(className);
            initializeClass(className, cState, level);
        }

        return classNameToState.get(className);
    }

    void setClassSideEffectType(String className, SideEffect.Level sideEffectLevel) {
        classNameToSideEffectLevel.put(className, sideEffectLevel);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (mState != null) {
            sb.append("Method: ").append(mState.toString());
        }
        if (sb.length() > 0) {
            sb.append("\n");
        }
        if (getInitializedClasses().size() < 4) {
            // Too many and can blow up heap
            for (String className : getInitializedClasses()) {
                ClassState cState = peekClassState(className);
                sb.append("Class: ").append(className).append(" ").append(cState);
            }
        }

        return sb.toString();
    }

}
