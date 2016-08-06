package org.cf.simplify.strategy;

import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.simplify.OptimizerTester;
import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UninitializedInstance;
import org.cf.smalivm.type.VirtualClass;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DeadRemovalStrategyTest {

    private static final String CLASS_NAME = "Ldead_removal_strategy_test;";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(DeadRemovalStrategyTest.class.getSimpleName());

    private static ExecutionGraphManipulator getOptimizedGraph(String methodName, Object... args) {
        VMState initial = new VMState();
        if (args.length > 0) {
            initial.setRegisters(args);
        }
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        strategy.perform();

        return manipulator;
    }

    @Test
    public void doesNotDetectAssignmentReassignedInOnlyOneMultiverse() {
        String methodName = "reassignedInOnlyOneMultiverse(I)I";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAssignmentAddresses();
        Collections.sort(found);
        List<Integer> expected = new LinkedList<Integer>();

        assertEquals(expected, found);
    }

    @Test
    public void doesNotDetectInstanceInitializer() {
        VirtualMachine vm = VMTester.spawnVM(true);
        VirtualClass virtualClass = vm.getClassManager().getVirtualClass(CLASS_NAME);
        VMState initial = new VMState();
        initial.setRegisters(0, new UninitializedInstance(virtualClass), CLASS_NAME);

        String methodName = "<init>()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        strategy.perform();

        BuilderInstruction instruction = manipulator.getInstruction(0);
        assertEquals(Opcode.INVOKE_DIRECT, instruction.getOpcode());
    }

    @Test
    public void doesNotDetectMoveOpWithOnlyOneRegisterReassigned() {
        String methodName = "moveP0IntoV0With30Locals(I)V";
        ExecutionGraphManipulator manipulator = getOptimizedGraph(methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAssignmentAddresses();

        assertEquals(0, found.size());
    }

    @Test
    public void detectsDeadCodeWithStrongSideEffect() {
        String methodName = "deadCodeWithStrongSideEffect()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAddresses();
        Collections.sort(found);
        List<Integer> expected = Collections.singletonList(1);

        assertEquals(expected, found);
    }

    @Test
    public void detectsDeadNopPadding() {
        String methodName = "hasNopPadding()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAddresses();
        List<Integer> expected = new LinkedList<Integer>();

        assertEquals(expected, found);
    }

    @Test
    public void detectsDeadTryCatchBlock() {
        String methodName = "deadTryCatchBlock()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAssignmentAddresses();
        List<Integer> expected = Collections.singletonList(0);

        assertEquals(expected, found);
    }

    @Test
    public void detectsUnusedResultOfMethodInvocationWithNoSideEffects() {
        String methodName = "unusedResultNoSideEffects()I";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadResultAddresses();
        List<Integer> expected = Collections.singletonList(1);

        assertEquals(expected, found);
    }

    @Test
    public void detectsMoveResultWithUnusedAssignment() {
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME,
                "moveResult()Ljava/lang/String;", MethodState.ResultRegister, "some string", "Ljava/lang/String;");
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAssignmentAddresses();
        List<Integer> expected = Arrays.asList(3, 7);
        assertEquals(expected, found);
    }

    @Test
    public void detectsSimpleDeadCode() {
        String methodName = "deadCode()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAddresses();
        Collections.sort(found);
        List<Integer> expected = Arrays.asList(2, 3, 4, 5);

        assertEquals(expected, found);
    }

    @Test
    public void detectsUnusedAssignment() {
        String methodName = "unusedAssignment()I";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAssignmentAddresses();
        List<Integer> expected = Collections.singletonList(0);

        assertEquals(expected, found);
    }

    @Test
    public void detectsUnusedResultOfMethodInvocationWithSideEffects() {
        String methodName = "unusedResultWithSideEffects()I";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAssignmentAddresses();
        List<Integer> expected = new LinkedList<Integer>();

        assertEquals(expected, found);
    }

    @Test
    public void detectsUselessGoto() {
        String methodName = "uselessGoto()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getUselessBranchAddresses();
        List<Integer> expected = Collections.singletonList(0);

        assertEquals(expected, found);
    }

}
