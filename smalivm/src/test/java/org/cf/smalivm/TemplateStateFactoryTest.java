//package org.cf.smalivm;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import org.cf.smalivm.context.ClassState;
//import org.cf.smalivm.context.ExecutionContext;
//import org.cf.smalivm.context.HeapItem;
//import org.cf.smalivm.context.MethodState;
//import org.cf.smalivm.exception.VirtualMachineException;
//import org.cf.smalivm.type.UninitializedInstance;
//import org.cf.smalivm.type.UnknownValue;
//import org.cf.smalivm.type.VirtualMethod;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class TemplateStateFactoryTest {
//
//    private static final String CLASS_NAME = "Lchild_class;";
//
//    private VirtualMachine vm;
//
//    @Test
//    public void methodStateForVirtualMethodCreatedCorrectly() throws VirtualMachineException {
//        String methodDescriptor = "someString()Ljava/lang/String;";
//
//        VirtualMethod method = vm.getClassManager().getMethod(CLASS_NAME, methodDescriptor);
//        ExecutionContext spawnedContext = new ExecutionContext(vm, method);
//        ClassState templateClassState = TemplateStateFactory.forClass(spawnedContext, method.getDefiningClass());
//        spawnedContext.setClassState(templateClassState);
//
//        MethodState templateMethodState = TemplateStateFactory.forMethod(spawnedContext);
//        spawnedContext.setMethodState(templateMethodState);
//
//        assertEquals(2, templateMethodState.getRegisterCount());
//        assertEquals(1, templateMethodState.getParameterCount());
//        int instanceRegister = templateMethodState.getParameterStart();
//        assertEquals(1, instanceRegister);
//
//        HeapItem instanceItem = templateMethodState.peekRegister(instanceRegister);
//        assertEquals(CLASS_NAME, instanceItem.getType());
//        assertEquals(UnknownValue.class, instanceItem.getValue().getClass());
//    }
//
//    @Test
//    public void methodStateForObjectInitializationMethodCreatedCorrectly() throws VirtualMachineException {
//        String methodDescriptor = "<init>()V";
//
//        VirtualMethod method = vm.getClassManager().getMethod(CLASS_NAME, methodDescriptor);
//        ExecutionContext spawnedContext = new ExecutionContext(vm, method);
//        ClassState templateClassState = TemplateStateFactory.forClass(spawnedContext, method.getDefiningClass());
//        spawnedContext.setClassState(templateClassState);
//
//        MethodState templateMethodState = TemplateStateFactory.forMethod(spawnedContext);
//        spawnedContext.setMethodState(templateMethodState);
//
//        assertEquals(1, templateMethodState.getRegisterCount());
//        assertEquals(1, templateMethodState.getParameterCount());
//        int instanceRegister = templateMethodState.getParameterStart();
//        assertEquals(0, instanceRegister);
//
//        HeapItem instanceItem = templateMethodState.peekRegister(instanceRegister);
//        assertEquals(CLASS_NAME, instanceItem.getType());
//        assertEquals(UninitializedInstance.class, instanceItem.getValue().getClass());
//    }
//
//    @BeforeEach
//    public void setupVM() {
//        vm = VMTester.spawnVM();
//    }
//
//}
