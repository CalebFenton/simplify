package simplify.vm.ops;

import gnu.trove.map.TIntObjectMap;

import java.util.logging.Logger;

import org.junit.Test;

import simplifier.Main;
import simplifier.vm.type.LocalInstance;
import simplifier.vm.type.UnknownValue;
import simplify.vm.VMTester;

public class TestAPutOp {

    private static final String CLASS_NAME = "Laput_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    @Test
    public void TestArrayPut() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[1], 1, 0, 2, 4);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new int[] { 4 });

        VMTester.testState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void TestArrayPutBoolean() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new boolean[1], 1, 0, 2, 0x1);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new boolean[] { true });

        VMTester.testState(CLASS_NAME, "TestArrayPutBoolean()V", initial, expected);
    }

    @Test
    public void TestArrayPutByte() {
        Byte b = 0xf;
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new byte[1], 1, 0, 2, b);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new byte[] { 0xf });

        VMTester.testState(CLASS_NAME, "TestArrayPutByte()V", initial, expected);
    }

    @Test
    public void TestArrayPutChar() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new char[1], 1, 0, 2, (int) '$');
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new char[] { '$' });

        VMTester.testState(CLASS_NAME, "TestArrayPutChar()V", initial, expected);
    }

    @Test
    public void TestArrayPutObject() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new LocalInstance[1], 1, 0, 2,
                        new LocalInstance(CLASS_NAME));
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new LocalInstance[] { new LocalInstance(
                        CLASS_NAME) });

        VMTester.testState(CLASS_NAME, "TestArrayPutObject()V", initial, expected);
    }

    @Test
    public void TestArrayPutShort() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new short[1], 1, 0, 2, 0x42);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new short[] { 0x42 });

        VMTester.testState(CLASS_NAME, "TestArrayPutShort()V", initial, expected);
    }

    @Test
    public void TestArrayPutUnkownValue() {
        // Ideally, setting an element unknown shouldn't set the entire array unknown. See APutOp for more details.
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[1], 1, 0, 2, new UnknownValue("I"));
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"));

        VMTester.testState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void TestArrayPutUnkownIndex() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[1], 1, new UnknownValue("I"), 2, 5);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"));

        VMTester.testState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void TestArrayPutWide() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new long[1], 1, 0, 2, 10000000000L);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new long[] { 10000000000L });

        VMTester.testState(CLASS_NAME, "TestArrayPutWide()V", initial, expected);
    }

}
