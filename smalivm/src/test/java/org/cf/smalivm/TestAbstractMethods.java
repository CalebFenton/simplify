package org.cf.smalivm;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestAbstractMethods {

    public static class ChildClass {
        private static final String CLASS_NAME = "Lchild_class;";
        private static final String EXPECTED_VALUE = "abstract?";
        private VirtualMachine vm;

        @Before
        public void setupVM() {
            vm = getVM();
        }

        @Test
        public void TestInvokingAbstractMethodThroughParentReferenceWorks() {
            String methodName = "callsAbstractMethod()Ljava/lang/String;";
            ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);
            String value = (String) graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);

            assertEquals(EXPECTED_VALUE, value);
        }

        @Test
        public void TestInvokingImplemenetedAbstractMethodWorks() {
            String methodName = "abstractMethod()Ljava/lang/String;";
            ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);
            String value = (String) graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);

            assertEquals(EXPECTED_VALUE, value);
        }
    }

    public static class ParentClass {
        private static final String CLASS_NAME = "Lparent_class;";

        private VirtualMachine vm;

        @Before
        public void setupVM() {
            vm = getVM();
        }

        @Test
        public void TestExecutingAbstractMethodReturnsNull() {
            String methodName = "abstractMethod()Ljava/lang/String;";
            ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

            assertEquals(null, graph);
        }
    }

    private static VirtualMachine getVM() {
        Map<String, BuilderClassDef> classNameToDef = VMTester.buildClassNameToBuilderClassDef();
        List<BuilderClassDef> classDefs = new ArrayList<BuilderClassDef>(classNameToDef.values());
        return new VirtualMachine(classDefs);
    }

}
