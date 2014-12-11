package org.cf.smalivm.emulate;

import static org.junit.Assert.assertEquals;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.EmulatedType;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class Test_java_lang_Class_getPackage {

    private static EmulatedMethod emulatedMethod = new java_lang_Class_getPackage();

    private MethodState getMethodState(Object register0) {
        VirtualMachine vm = VMTester.getTestVM();
        ExecutionContext ectx = new ExecutionContext(vm);
        MethodState mState = new MethodState(ectx, 1);
        mState.assignRegister(0, register0);

        return mState;
    }

    @Test
    public void testLocalClassReturnsExpectedEmulatedTypeType() throws Exception {
        MethodState mState = getMethodState(new LocalClass("Lsome/local/package/TestClass;"));
        emulatedMethod.execute(mState);
        EmulatedType emulatedPackage = new EmulatedType("Ljava/lang/Package;");
        emulatedPackage.setExtra("some.local.package");
        Object expected = emulatedPackage;
        Object actual = mState.readRegister(MethodState.ReturnRegister);

        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void testNonLocalClassReturnsPackage() throws Exception {
        MethodState mState = getMethodState(String.class);
        emulatedMethod.execute(mState);
        Object expected = String.class.getPackage();
        Object actual = mState.readRegister(MethodState.ReturnRegister);

        assertEquals(expected, actual);
    }

    @Test
    public void testUnknownValueReturnsUnknownValueOfExpectedType() throws Exception {
        MethodState mState = getMethodState(new UnknownValue("Ljava/lang/Class;"));
        emulatedMethod.execute(mState);
        Object expected = new UnknownValue("Ljava/lang/Package;");
        Object actual = mState.readRegister(MethodState.ReturnRegister);

        assertEquals(expected.toString(), actual.toString());
    }

}
