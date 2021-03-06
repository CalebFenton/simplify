package org.cf.smalivm.opcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.VirtualMachineImpl;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction22t;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IfOpTest {

    private static final int ADDRESS_IF = 0;
    private static final int ADDRESS_NOP = 2;
    private static final int ADDRESS_RETURN = 3;

    private static final String CLASS_NAME = "Lif_test;";

    private static final int[] IF_ALL_VISITATIONS = new int[] { ADDRESS_IF, ADDRESS_NOP, ADDRESS_RETURN };
    private static final int[] IF_FALSE_VISITATIONS = new int[] { ADDRESS_IF, ADDRESS_NOP, ADDRESS_RETURN };
    private static final int[] IF_TRUE_VISITATIONS = new int[] { ADDRESS_IF, ADDRESS_RETURN };

    public static class CompareIdenticalPrimitiveValueTypes {

        private VMState initial;

        @BeforeEach
        public void setUp() {
            initial = new VMState();
        }

        @Test
        public void testIfEqualWithOneAndZeroIsFalse() {
            initial.setRegisters(0, 1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_FALSE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithTwoEqualIntegersIsTrue() {
            initial.setRegisters(0, 0, "I", 1, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithTwoUnequalIntegersIsFalse() {
            initial.setRegisters(0, 0, "I", 1, 1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_FALSE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithZeroAndZeroIsTrue() {
            initial.setRegisters(0, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualZeroWith0ByteIsTrue() {
            initial.setRegisters(0, (byte) 0x0, "B");
            VMTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfGreaterOrEqualWith0And0IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfGreaterOrEqualWith0And1IsFalse() {
            initial.setRegisters(0, 0, "I", 1, 1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", initial, IF_FALSE_VISITATIONS);
        }

        @Test
        public void testIfGreaterOrEqualWith1And0IsTrue() {
            initial.setRegisters(0, 1, "I", 1, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfGreaterOrEqualWithOneAndZeroIsTrue() {
            initial.setRegisters(0, 1, "I", 1, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfGreaterOrEqualZeroWithNegativeOneIsFalse() {
            initial.setRegisters(0, -1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifGreaterOrEqualZero()V", initial, IF_FALSE_VISITATIONS);
        }

        @Test
        public void testIfGreaterOrEqualZeroWithOneIsTrue() {
            initial.setRegisters(0, 1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifGreaterOrEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfGreaterOrEqualZeroWithZeroIsTrue() {
            initial.setRegisters(0, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifGreaterOrEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfGreaterThanWith0And1IsFalse() {
            initial.setRegisters(0, 0, "I", 1, 1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifGreaterThan()V", initial, IF_FALSE_VISITATIONS);
        }

        @Test
        public void testIfGreaterThanWith1And0IsTrue() {
            initial.setRegisters(0, 1, "I", 1, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifGreaterThan()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfGreaterThanZeroWith0IsFalse() {
            initial.setRegisters(0, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifGreaterThanZero()V", initial, IF_FALSE_VISITATIONS);
        }

        @Test
        public void testIfGreaterThanZeroWithOneIsTrue() {
            initial.setRegisters(0, 1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifGreaterThanZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfLessOrEqualWith0And0IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifLessOrEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfLessOrEqualWith0And1IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifLessOrEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfLessOrEqualWith1And0IsFalse() {
            initial.setRegisters(0, 1, "I", 1, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifLessOrEqual()V", initial, IF_FALSE_VISITATIONS);
        }

        @Test
        public void testIfLessOrEqualZeroWith0IsTrue() {
            initial.setRegisters(0, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifLessOrEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfLessOrEqualZeroWithNegative1IsTrue() {
            initial.setRegisters(0, -1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifLessOrEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfLessOrEqualZeroWithOneIsFalse() {
            initial.setRegisters(0, 1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifLessOrEqualZero()V", initial, IF_FALSE_VISITATIONS);
        }

        @Test
        public void testIfLessThanWith0And1IsFalse() {
            initial.setRegisters(0, 0, "I", 1, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifLessThan()V", initial, IF_FALSE_VISITATIONS);
        }

        @Test
        public void testIfLessThanWith0And1IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifLessThan()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfLessThanZeroWith0IsFalse() {
            initial.setRegisters(0, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifLessThanZero()V", initial, IF_FALSE_VISITATIONS);
        }

        @Test
        public void testIfLessThanZeroWithNegativeOneIsTrue() {
            initial.setRegisters(0, -1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifLessThanZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfNotEqualWith0And0IsFalse() {
            initial.setRegisters(0, 0, "I", 1, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifNotEqual()V", initial, IF_FALSE_VISITATIONS);
        }

        @Test
        public void testIfNotEqualWith0And1IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifNotEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfNotEqualZeroWith0IsFalse() {
            initial.setRegisters(0, 0, "I");
            VMTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_FALSE_VISITATIONS);
        }

        @Test
        public void testIfNotEqualZeroWith7ByteIsTrue() {
            initial.setRegisters(0, (byte) 0x7, "B");
            VMTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfNotEqualZeroWithOneIsTrue() {
            initial.setRegisters(0, 1, "I");
            VMTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

    }

    public static class CompareObjectReferences {

        private VMState initial;

        @BeforeEach
        public void setUp() {
            initial = new VMState();
        }

        @Test
        public void testIdenticalObjectReferencesAreEqual() {
            String obj = "object string";
            initial.setRegisters(0, obj, "Ljava/lang/String;", 1, obj, "Ljava/lang/String;");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfFalseBooleanEqualZero() {
            initial.setRegisters(0, Boolean.FALSE, "Z");
            VMTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfNotEqualWithStringAndArrayReferenceIsTrue() {
            initial.setRegisters(0, "object string", "Ljava/lang/String;", 1, new int[0], "[I");
            VMTester.testVisitation(CLASS_NAME, "ifNotEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfNotEqualWithTwoDifferentStringReferencesIsTrue() {
            String obj1 = "object string";
            // Need to get crafty or javac will be smart enough to use same literal for both objects
            String obj2 = new StringBuilder(obj1).toString();
            initial.setRegisters(0, obj1, "Ljava/lang/String;", 1, obj2, "Ljava/lang/String;");
            VMTester.testVisitation(CLASS_NAME, "ifNotEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfNullEqualZero() {
            initial.setRegisters(0, null, "I");
            VMTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfObjectNotEqualZero() {
            initial.setRegisters(0, "object string", "Ljava/lang/String;");
            VMTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfPrimitiveArrayNotEqualZero() {
            initial.setRegisters(0, new byte[] { 0x1 }, "[B");
            VMTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfTrueBooleanNotEqualZero() {
            initial.setRegisters(0, Boolean.TRUE, "Z");
            VMTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfUnknownIntegerTakesBothPaths() {
            initial.setRegisters(0, new UnknownValue(), "I");
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, "ifEqualZero()V", initial);
            VMTester.testVisitation(graph, IF_ALL_VISITATIONS);

            assertEquals(1, graph.getNodePile(ADDRESS_NOP).size());
            // Two sepearate execution paths should reach the return op
            assertEquals(2, graph.getNodePile(ADDRESS_RETURN).size());
        }

        @Test
        public void testIfZeroIntegerEqualZero() {
            initial.setRegisters(0, Integer.valueOf(0), "I");
            VMTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

    }

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int ARG1_REGISTER = 2;
        private static final int ARG2_REGISTER = 4;

        private TIntObjectMap<MethodLocation> addressToLocation;
        private BuilderInstruction instruction;
        private MethodLocation location;
        private MethodState mState;
        private ExecutionNode node;
        private IfOp op;
        private IfOpFactory opFactory;
        private VirtualMachine vm;

        @Test
        public void hasExpectedToStringValue() {
            int value = 0;
            VMTester.setRegisterMock(mState, ARG1_REGISTER, value, "D");
            VMTester.setRegisterMock(mState, ARG2_REGISTER, value, "D");

            instruction = buildInstruction22t(Opcode.IF_GE, 0);
            op = (IfOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            assertEquals("if-ge r2, r4, :addr_0", op.toString());
        }

        @BeforeEach
        public void setUp() {
            vm = mock(VirtualMachineImpl.class);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
            location = mock(MethodLocation.class);
            when(location.getCodeAddress()).thenReturn(ADDRESS);

            addressToLocation = new TIntObjectHashMap<MethodLocation>();
            addressToLocation.put(ADDRESS, location);

            opFactory = new IfOpFactory();
        }

        private BuilderInstruction buildInstruction22t(Opcode opcode, int offset) {
            BuilderInstruction instruction =
                    mock(BuilderInstruction.class, withSettings().extraInterfaces(Instruction22t.class));
            when(location.getInstruction()).thenReturn(instruction);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(instruction.getOpcode()).thenReturn(opcode);
            when(((Instruction22t) instruction).getRegisterA()).thenReturn(ARG1_REGISTER);
            when(((Instruction22t) instruction).getRegisterB()).thenReturn(ARG2_REGISTER);
            when(((Instruction22t) instruction).getCodeOffset()).thenReturn(offset);

            return instruction;
        }

    }

    public static class ValueTypeCombinations {

        private VMState initial;

        @BeforeEach
        public void setUp() {
            initial = new VMState();
        }

        @Test
        public void testIfEqualWithBooleanAndChar() {
            initial.setRegisters(0, true, "Z", 1, (char) 1, "C");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithByteAndBoolean() {
            initial.setRegisters(0, (byte) 1, "B", 1, true, "Z");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithByteAndChar() {
            initial.setRegisters(0, (byte) 1, "B", 1, (char) 1, "C");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithByteAndDouble() {
            initial.setRegisters(0, (byte) 1, "B", 1, 1D, "D");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithByteAndFloat() {
            initial.setRegisters(0, (byte) 1, "B", 1, 1F, "F");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithByteAndLong() {
            initial.setRegisters(0, (byte) 1, "B", 1, 1L, "J");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithByteAndShort() {
            initial.setRegisters(0, (byte) 1, "B", 1, (short) 1, "S");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithDoubleAndBoolean() {
            initial.setRegisters(0, 1D, "D", 1, true, "Z");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithDoubleAndChar() {
            initial.setRegisters(0, 1D, "D", 1, (char) 1, "C");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithFloatAndBoolean() {
            initial.setRegisters(0, 1F, "F", 1, true, "Z");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithFloatAndChar() {
            initial.setRegisters(0, 1F, "F", 1, (char) 1, "C");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithFloatAndDouble() {
            // 3.2 is tricky, 3.2F != 3.2D && Double.compareTo(3.2F, 3.2D)
            initial.setRegisters(0, 3.2F, "F", 1, 3.2D, "D");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithIntAndBoolean() {
            initial.setRegisters(0, 1, "I", 1, true, "Z");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithIntAndByte() {
            initial.setRegisters(0, 1, "I", 1, (byte) 1, "B");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithIntAndChar() {
            initial.setRegisters(0, 1, "I", 1, (char) 1, "C");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithIntAndDouble() {
            initial.setRegisters(0, 1, "I", 1, 1D, "D");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithIntAndFloat() {
            initial.setRegisters(0, 1, "I", 1, 1F, "F");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithIntAndLong() {
            initial.setRegisters(0, 1, "I", 1, 1L, "J");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithIntAndShort() {
            initial.setRegisters(0, 1, "I", 1, (short) 1, "S");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithLongAndBoolean() {
            initial.setRegisters(0, 1L, "J", 1, true, "Z");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithLongAndChar() {
            initial.setRegisters(0, 1L, "J", 1, (char) 1, "C");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithLongAndDouble() {
            initial.setRegisters(0, 1L, "J", 1, 1D, "D");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithLongAndFloat() {
            initial.setRegisters(0, 1L, "J", 1, 1F, "F");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithShortAndBoolean() {
            initial.setRegisters(0, (short) 1, "S", 1, true, "Z");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithShortAndChar() {
            initial.setRegisters(0, (short) 1, "S", 1, (char) 1, "C");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithShortAndDouble() {
            initial.setRegisters(0, (short) 1, "S", 1, 1D, "D");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithShortAndFloat() {
            initial.setRegisters(0, (short) 1, "S", 1, 1F, "F");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualWithShortAndLong() {
            initial.setRegisters(0, (short) 1, "S", 1, 1L, "J");
            VMTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfEqualZeroWithFalseIsTrue() {
            initial.setRegisters(0, false, "Z");
            VMTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

        @Test
        public void testIfNotEqualZeroWithTrueIsTrue() {
            initial.setRegisters(0, true, "Z");
            VMTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS);
        }

    }

}
