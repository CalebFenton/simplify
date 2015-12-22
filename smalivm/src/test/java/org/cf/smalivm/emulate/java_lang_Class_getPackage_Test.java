package org.cf.smalivm.emulate;

import static org.junit.Assert.assertEquals;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.EmulatedType;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Before;
import org.junit.Test;

public class java_lang_Class_getPackage_Test {

    private VirtualMachine vm;
    private MethodStateMethod emulatedMethod;

    @Before
    public void setUp() {
        vm = VMTester.getTestVM();
        emulatedMethod = new java_lang_Class_getPackage();
    }

    private static MethodState getMethodState(VirtualMachine vm, Object register0, String type) {
        ExecutionContext ectx = new ExecutionContext(vm, "Ljava/lang/Class;->getPackage()Ljava/lang/Package;");
        int registerCount = 1;
        MethodState mState = new MethodState(ectx, registerCount);
        mState.assignRegister(0, register0, type);

        return mState;
    }

    @Test
    public void testLocalClassReturnsExpectedEmulatedTypeType() throws Exception {
        String type = "Lsome/local/package/TestClass;";
        MethodState mState = getMethodState(vm, new LocalClass(type), type);

        emulatedMethod.execute(vm, mState);

        EmulatedType emulatedPackage = new EmulatedType("Ljava/lang/Package;");
        emulatedPackage.setExtra("some.local.package");
        HeapItem expected = new HeapItem(emulatedPackage, "Ljava/lang/Package;");
        HeapItem actual = mState.readRegister(MethodState.ReturnRegister);

        assertEquals(expected, actual);
    }

    @Test
    public void testNonLocalClassReturnsPackage() throws Exception {
        MethodState mState = getMethodState(vm, String.class, "Ljava/lang/String;");

        emulatedMethod.execute(vm, mState);

        HeapItem expected = new HeapItem(String.class.getPackage(), "Ljava/lang/Package;");
        HeapItem actual = mState.readRegister(MethodState.ReturnRegister);

        assertEquals(expected, actual);
    }

    @Test
    public void testUnknownValueReturnsUnknownValueOfExpectedType() throws Exception {
        MethodState mState = getMethodState(vm, new UnknownValue(), "Ljava/lang/Class;");

        emulatedMethod.execute(vm, mState);

        HeapItem expected = HeapItem.newUnknown("Ljava/lang/Package;");
        HeapItem actual = mState.readRegister(MethodState.ReturnRegister);

        assertEquals(expected.toString(), actual.toString());
    }

}
