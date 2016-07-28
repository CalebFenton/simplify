package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.util.ClassNameUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(Enclosed.class)
public class ConstOpTest {

    public static class IntegrationTest {

        private static final String CLASS_NAME = "Lconst_test;";

        private VMState expected;

        @Test
        public void canConst() throws NoSuchFieldException, SecurityException {
            expected.setRegisters(0, 0x42424242, "D");

            VMTester.test(CLASS_NAME, "const()V", expected);
        }

        @Test
        public void canConst16() {
            expected.setRegisters(0, 0x4242, "I");

            VMTester.test(CLASS_NAME, "const16()V", expected);
        }

        @Test
        public void canConst4() {
            expected.setRegisters(0, -3, "I");

            VMTester.test(CLASS_NAME, "const4()V", expected);
        }

        @Test
        public void canConstClassLocal() throws ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM();
            Class<?> expectedClass = vm.getClassLoader().loadClass(ClassNameUtils.internalToBinary(CLASS_NAME));
            expected.setRegisters(0, expectedClass, CommonTypes.CLASS);
            ExecutionGraph graph = VMTester.execute(vm, CLASS_NAME, "constClassLocal()V");

            VMTester.testState(graph, expected);
        }

        @Test
        public void canConstClassRemote() {
            expected.setRegisters(0, Object.class, CommonTypes.CLASS);

            VMTester.test(CLASS_NAME, "constClassRemote()V", expected);
        }

        @Test
        public void canConstHigh16() {
            expected.setRegisters(0, 0x42420000, "I");

            VMTester.test(CLASS_NAME, "constHigh16()V", expected);
        }

        @Test
        public void canConstString() {
            expected.setRegisters(0, "When I need to identify rebels, I look for men with principles.",
                    "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "constString()V", expected);
        }

        @Test
        public void canConstStringJumbo() {
            expected.setRegisters(0,
                    "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles.",
                    "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "constStringJumbo()V", expected);
        }

        @Test
        public void canConstWide() {
            expected.setRegisters(0, 0x4242424242424242L, "J");

            VMTester.test(CLASS_NAME, "constWide()V", expected);
        }

        @Test
        public void canConstWide16() {
            expected.setRegisters(0, 0x4242L, "J");

            VMTester.test(CLASS_NAME, "constWide16()V", expected);
        }

        @Test
        public void canConstWide32() {
            expected.setRegisters(0, 0x1b7740L, "J");

            VMTester.test(CLASS_NAME, "constWide32()V", expected);
        }

        @Test
        public void canConstWideHigh16() {
            expected.setRegisters(0, 0x4242000000000000L, "J");

            VMTester.test(CLASS_NAME, "constWideHigh16()V", expected);
        }

        @Test
        public void canNegativeConst16() {
            expected.setRegisters(0, -0xf, "I");

            VMTester.test(CLASS_NAME, "const16Negative()V", expected);
        }

        @Test
        public void nonExistentClassNameThrowsException() {
            testException("constClassUnknown()V", ClassNotFoundException.class);
        }

        private static void testException(String methodDescriptor, Class<?> exceptionClass) {
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, methodDescriptor);

            HeapItem item = graph.getTerminatingRegisterConsensus(0);
            assertEquals(exceptionClass, item.getValue().getClass());
            assertEquals(ClassNameUtils.toInternal(exceptionClass), item.getType());

            assertFalse(graph.wasAddressReached(2));

            MethodState mState = graph.getNodePile(0).get(0).getContext().getMethodState();
            assertEquals(0, mState.getRegistersAssigned().length);
        }

        @Before
        public void setUp() {
            expected = new VMState();
        }

    }

}
