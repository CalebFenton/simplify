package simplify.vm.ops;

import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.VMTester;
import util.SparseArray;

public class TestSGetOp {

    private static final String CLASS_NAME = "Lsget_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    @Test
    public void TestStaticGetInt() {
        Map<String, Map<String, Object>> initialClassNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myInt:I", 0x42);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x42);

        VMTester.testExpectedMethodState(CLASS_NAME, "TestStaticGetInt()V", expected, initialClassNameToFieldValue);
    }

    @Test
    public void TestStaticGetWide() {
        Map<String, Map<String, Object>> initialClassNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myLong:J", 0xFFFFFFFFFL);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0xFFFFFFFFFL);

        VMTester.testExpectedMethodState(CLASS_NAME, "TestStaticGetWide()V", expected, initialClassNameToFieldValue);
    }

    @Test
    public void TestStaticGetObject() {
        Map<String, Map<String, Object>> initialClassNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myString:Ljava/lang/String;", "They tried and died.");
        SparseArray<Object> expected = VMTester.buildRegisterState(0, "They tried and died.");

        VMTester.testExpectedMethodState(CLASS_NAME, "TestStaticGetObject()V", expected, initialClassNameToFieldValue);
    }

    @Test
    public void TestStaticGetBoolean() {
        Map<String, Map<String, Object>> initialClassNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myBoolean:Z", true);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, true);

        VMTester.testExpectedMethodState(CLASS_NAME, "TestStaticGetBoolean()V", expected, initialClassNameToFieldValue);
    }

    @Test
    public void TestStaticGetByte() {
        Map<String, Map<String, Object>> initialClassNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myByte:B", (byte) 0xf);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, (byte) 0xf);

        VMTester.testExpectedMethodState(CLASS_NAME, "TestStaticGetByte()V", expected, initialClassNameToFieldValue);
    }

    @Test
    public void TestStaticGetChar() {
        Map<String, Map<String, Object>> initialClassNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myChar:C", 'x');
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 'x');

        VMTester.testExpectedMethodState(CLASS_NAME, "TestStaticGetChar()V", expected, initialClassNameToFieldValue);
    }

    @Test
    public void TestStaticGetShort() {
        Map<String, Map<String, Object>> initialClassNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myShort:S", (short) 0x100);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, (short) 0x100);

        VMTester.testExpectedMethodState(CLASS_NAME, "TestStaticGetShort()V", expected, initialClassNameToFieldValue);
    }

}
