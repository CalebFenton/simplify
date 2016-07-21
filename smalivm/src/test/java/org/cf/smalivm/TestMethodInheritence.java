package org.cf.smalivm;

import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class TestMethodInheritence {

    public static class ChildClass {

        private static final String CLASS_NAME = "Lchild_class;";
        private static final String EXPECTED_VALUE = "abstract?";
        private VirtualMachine vm;

        @Before
        public void setupVM() {
            vm = VMTester.spawnVM();
        }

        @Test
        public void canInvokeAbstractMethodThroughParentReference() throws VirtualMachineException {
            String methodName = "callsAbstractMethod()Ljava/lang/String;";
            ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);
            HeapItem item = graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);

            assertEquals(EXPECTED_VALUE, item.getValue());
        }

        @Test
        public void canInvokeImplemenetedAbstractMethod() throws VirtualMachineException {
            String methodName = "abstractMethod()Ljava/lang/String;";
            ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);
            HeapItem item = graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);
            String value = (String) item.getValue();

            assertEquals(EXPECTED_VALUE, value);
        }

        @Test
        public void canInvokeParentMethodOfChild() throws VirtualMachineException {
            String methodName = "callsParentMethod()Ljava/lang/String;";
            ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);
            HeapItem item = graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);

            assertEquals("parentMethod", item.getValue());
        }
    }

    public static class ParentClass {

        private static final String CLASS_NAME = "Lparent_class;";

        @Rule
        public final ExpectedException exception = ExpectedException.none();

        private VirtualMachine vm;

        @Before
        public void setupVM() {
            vm = VMTester.spawnVM();
        }

        @Test
        public void executingAbstractMethodReturnsNull() throws VirtualMachineException {
            String methodName = "abstractMethod()Ljava/lang/String;";
            exception.expect(IllegalArgumentException.class);
            vm.execute(CLASS_NAME + "->" + methodName);
        }
    }

}
