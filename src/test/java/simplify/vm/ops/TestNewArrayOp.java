package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplifier.Main;
import simplifier.vm.type.LocalInstance;
import simplifier.vm.type.UnknownValue;
import simplify.vm.VMTester;
import util.SparseArray;

public class TestNewArrayOp {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Lnew_array_test;";

    @Test
    public void TestNewArrayPrimitive() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 1);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new int[1]);

        VMTester.testState(CLASS_NAME, "TestNewArrayPrimitive()V", initial, expected);
    }

    @Test
    public void TestNewArrayPrimitiveUnkonwnDimension() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"));

        VMTester.testState(CLASS_NAME, "TestNewArrayPrimitive()V", initial, expected);
    }

    @Test
    public void TestNewArrayPrimitiveMultidimensional() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 3);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new int[3][]);

        VMTester.testState(CLASS_NAME, "TestNewArrayPrimitiveMultidimensional()V", initial, expected);
    }

    @Test
    public void TestNewArrayLocal() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 1);
        LocalInstance[] instances = new LocalInstance[] { new LocalInstance(CLASS_NAME) };
        SparseArray<Object> expected = VMTester.buildRegisterState(0, instances);

        VMTester.testState(CLASS_NAME, "TestNewArrayLocal()V", initial, expected);
    }

    @Test
    public void TestNewArrayLocalMultidimensional() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 5);
        LocalInstance[][] instances = new LocalInstance[5][];
        SparseArray<Object> expected = VMTester.buildRegisterState(0, instances);

        VMTester.testState(CLASS_NAME, "TestNewArrayLocalMultidimensional()V", initial, expected);
    }

}
