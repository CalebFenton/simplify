package org.cf.smalivm;

import org.cf.smalivm.context.*;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualField;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.smalivm2.ExecutionNode;

public class MethodExecutorFactory {
    private final VirtualMachine vm;
    private int maxAddressVisits = 10_000;
    private int maxCallDepth = 50;
    private int maxExecutionTime = 5 * 60;
    private int maxMethodVisits = 1_000_000;
    private boolean interactive = false;

    public MethodExecutorFactory(VirtualMachine vm) {
        this.vm = vm;
    }

    public MethodExecutorFactory setMaxAddressVisits(int maxAddressVisits) {
        this.maxAddressVisits = maxAddressVisits;
        return this;
    }

    public MethodExecutorFactory setMaxCallDepth(int maxCallDepth) {
        this.maxCallDepth = maxCallDepth;
        return this;
    }

    public MethodExecutorFactory setMaxExecutionTime(int maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
        return this;
    }

    public MethodExecutorFactory setMaxMethodVisits(int maxMethodVisits) {
        this.maxMethodVisits = maxMethodVisits;
        return this;
    }

    public MethodExecutorFactory setInteractive() {
        interactive = true;
        return this;
    }

    public MethodExecutor build(VirtualMethod virtualMethod) {
        ExecutionContext calleeContext = vm.spawnRootContext(virtualMethod);
        return build(calleeContext);
    }

    public MethodExecutor build(ExecutionContext calleeContext) {
        return build(calleeContext, null);
    }

    public MethodExecutor build(ExecutionContext calleeContext, ExecutionContext callerContext) {
        if (callerContext != null) {
            inheritClassStates(callerContext, calleeContext);
        }

        VirtualMethod virtualMethod = calleeContext.getMethod();
        calleeContext.staticallyInitializeClassIfNecessary(virtualMethod.getDefiningClass());

        ExecutionGraphImpl graph = vm.spawnInstructionGraph(virtualMethod);
        org.cf.smalivm2.ExecutionNode rootNode = new ExecutionNode(graph.getRoot());
        rootNode.setContext(calleeContext);
        graph.addNode(rootNode);

        if (interactive) {
            return new MethodExecutor(vm.getClassManager(), graph);
        } else {
            return new NonInteractiveMethodExecutor(vm.getClassManager(), graph, maxCallDepth, maxAddressVisits, maxMethodVisits, maxExecutionTime);
        }
    }

    private void inheritClassStates(ExecutionContext parentContext, ExecutionContext childContext) {
        for (VirtualClass virtualClass : vm.getClassManager().getLoadedClasses()) {
            if (!parentContext.isClassInitialized(virtualClass)) {
                continue;
            }

            ClassState fromClassState = parentContext.peekClassState(virtualClass);
            ClassState toClassState = fromClassState.getChild(childContext);
            for (VirtualField field : fromClassState.getVirtualClass().getFields()) {
                HeapItem item = fromClassState.peekField(field);
                // TODO: should update field here?
                toClassState.pokeField(field, item);
            }
            SideEffect.Level level = parentContext.getClassSideEffectLevel(virtualClass);
            childContext.initializeClass(toClassState, level);
        }
    }
}
