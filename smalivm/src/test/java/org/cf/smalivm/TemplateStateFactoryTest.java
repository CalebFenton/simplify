package org.cf.smalivm;

import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UninitializedInstance;
import org.cf.smalivm.type.VirtualMethod;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemplateStateFactoryTest {

    private static final String CLASS_NAME = "Lchild_class;";

    private VirtualMachine vm;

    @Test
    public void methodStateCreatedCorrectly() throws VirtualMachineException {
        String methodDescriptor = "someString()Ljava/lang/String;";

        VirtualMethod method = vm.getClassManager().getMethod(CLASS_NAME, methodDescriptor);
        ExecutionContext spawnedContext = new ExecutionContext(vm, method);
        ClassState templateClassState = TemplateStateFactory.forClass(spawnedContext, method.getDefiningClass());
        spawnedContext.setClassState(templateClassState);

        MethodState templateMethodState = TemplateStateFactory.forMethod(spawnedContext);
        spawnedContext.setMethodState(templateMethodState);

        assertEquals(2, templateMethodState.getRegisterCount());
        assertEquals(1, templateMethodState.getParameterCount());
        int instanceRegister = templateMethodState.getParameterStart();
        assertEquals(1, instanceRegister);

        HeapItem instanceItem = templateMethodState.peekRegister(instanceRegister);
        assertEquals(CLASS_NAME, instanceItem.getType());
        assertEquals(UninitializedInstance.class, instanceItem.getValue().getClass());
    }

    @Before
    public void setupVM() throws Exception {
        vm = VMTester.spawnVM();
    }

}
