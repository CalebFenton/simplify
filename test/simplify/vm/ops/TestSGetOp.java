package simplify.vm.ops;

import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.ClassContext;
import simplify.vm.VMTester;
import util.SparseArray;

public class TestSGetOp {

    private static final String CLASS_NAME = "Lsget_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    @Test
    public void TestStaticGetInt() {
        Map<String, Object> initialFieldToValue = VMTester.buildFieldToValue("myInt:I", new int[] { 0x42 });
        Map<String, ClassContext> initialClassNameToContext = VMTester.buildClassNameToContext(CLASS_NAME,
                        initialFieldToValue);

        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x42);

        VMTester.testState(CLASS_NAME, "TestStaticGetInt()V", expected, initialClassNameToContext);
    }

}
