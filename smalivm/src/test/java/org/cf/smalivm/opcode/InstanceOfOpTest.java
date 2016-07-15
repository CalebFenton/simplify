package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class InstanceOfOpTest {

    private static final String CLASS_NAME = "Linstanceof_test;";

    public static class IntegrationTest {

        private VMState expected;
        private VMState initial;

        @Test
        public void intArray2DIsInstanceOfObjectArray() {
            initial.setRegisters(0, new int[0][], CommonTypes.OBJECT);
            expected.setRegisters(0, new int[0][], CommonTypes.OBJECT, 1, true, CommonTypes.BOOL);

            VMTester.test(CLASS_NAME, "instanceOfObjectArray()V", initial, expected);
        }

        @Test
        public void intArray2DIsNotInstanceOf2DObjectArray() {
            initial.setRegisters(0, new int[0][], CommonTypes.OBJECT);
            expected.setRegisters(0, new int[0][], CommonTypes.OBJECT, 1, false, CommonTypes.BOOL);

            VMTester.test(CLASS_NAME, "instanceOf2DObjectArray()V", initial, expected);
        }

        @Test
        public void intArrayWithObjectAsDeclaredTypeIsInstanceOfIntArray() {
            initial.setRegisters(0, new int[0], CommonTypes.OBJECT);
            expected.setRegisters(0, new int[0], CommonTypes.OBJECT, 1, true, CommonTypes.BOOL);

            VMTester.test(CLASS_NAME, "instanceOfIntArray()V", initial, expected);
        }

        @Test
        public void objectIsNotInstanceOfString() {
            initial.setRegisters(0, new int[0], CommonTypes.OBJECT);
            expected.setRegisters(0, new int[0], CommonTypes.OBJECT, 1, false, CommonTypes.BOOL);

            VMTester.test(CLASS_NAME, "instanceOfString()V", initial, expected);
        }

        @Before
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }

        @Test
        public void stringArray2DIsInstanceOf2DObjectArray() {
            initial.setRegisters(0, new String[0][], CommonTypes.STRING);
            expected.setRegisters(0, new String[0][], CommonTypes.STRING, 1, true, CommonTypes.BOOL);

            VMTester.test(CLASS_NAME, "instanceOf2DObjectArray()V", initial, expected);
        }

        @Test
        public void stringIsInstanceOfObject() {
            initial.setRegisters(0, "great maker", CommonTypes.STRING);
            expected.setRegisters(0, "great maker", CommonTypes.STRING, 1, true, CommonTypes.BOOL);

            VMTester.test(CLASS_NAME, "instanceOfObject()V", initial, expected);
        }

        @Test
        public void unknownValueWithObjectAsDeclaredTypeIsUnknownIfStringType() {
            initial.setRegisters(0, new UnknownValue(), CommonTypes.OBJECT);
            expected.setRegisters(0, new UnknownValue(), CommonTypes.OBJECT, 1, new UnknownValue(), CommonTypes.BOOL);

            VMTester.test(CLASS_NAME, "instanceOfString()V", initial, expected);
        }

    }

}
