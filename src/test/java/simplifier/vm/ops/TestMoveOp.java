package simplifier.vm.ops;

import static org.junit.Assert.assertTrue;
import gnu.trove.list.TIntList;
import gnu.trove.map.TIntObjectMap;

import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.Test;

import simplifier.Main;
import simplifier.vm.VMTester;
import simplifier.vm.context.ContextGraph;
import simplifier.vm.context.MethodContext;
import simplifier.vm.type.UnknownValue;

public class TestMoveOp {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Lmove_test;";

    class IdentityTest {
        public boolean equals(IdentityTest rhs) {
            System.out.println("ident test " + this + " called " + rhs);
            return this == rhs;
        }
    }

    @Test
    public void TestMoveRegisterPrimitive() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 42);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 42, 1, 42);

        VMTester.testState(CLASS_NAME, "TestMoveRegisterPrimitive()V", initial, expected);
    }

    @Test
    public void TestMoveRegisterObject() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new Object());

        // Must invoke VM directly to ensure reference identity
        ContextGraph graph = VMTester.execute(CLASS_NAME, "TestMoveRegisterObject()V", initial);
        TIntList addresses = graph.getConnectedTerminatingAddresses();
        assertTrue("Should terminate when expected: " + addresses + " == {1}",
                        Arrays.equals(addresses.toArray(), new int[] { 1 }));

        Object register0 = graph.getRegisterConsensus(1, 0);
        Object register1 = graph.getRegisterConsensus(1, 1);

        assertTrue(register0 == register1);
        assertTrue(register0 instanceof Object);
    }

    @Test
    public void TestMoveResult() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(MethodContext.ResultRegister, 42);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 42);

        VMTester.testState(CLASS_NAME, "TestMoveResult()V", initial, expected);
    }

    @Test
    public void TestMoveException() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("Ljava/lang/Exception;"));

        VMTester.test(CLASS_NAME, "TestMoveException()V", expected);
    }

}
