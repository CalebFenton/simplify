package org.cf.smalivm.opcode;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.ThreeRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.junit.Before;
import org.junit.Test;

public class TestAPutOp {

    private static final String CLASS_NAME = "Laput_test;";

    @Test
    public void testPutWithInteger() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, 0, "I", 2, 4, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[] { 4 }, "[I");

        VMTester.testMethodState(CLASS_NAME, "put()V", initial, expected);
    }

    @Test
    public void testPutIntegerWithShortIndex() {
        Short index = 0;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, index, "S", 2, 4, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[] { 4 }, "[I");

        VMTester.testMethodState(CLASS_NAME, "put()V", initial, expected);
    }

    @Test
    public void testPutIntegerWithIndexOutOfBoundsReturnsUnknownValue() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, 5, "I", 2, 4, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "[I");

        VMTester.testMethodState(CLASS_NAME, "put()V", initial, expected);
    }

    @Test
    public void testPutBoolean() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new boolean[1], "[Z", 1, 0, "I", 2, 0x1, "Z");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new boolean[] { true }, "[Z");

        VMTester.testMethodState(CLASS_NAME, "putBoolean()V", initial, expected);
    }

    @Test
    public void testPutBooleanWithShortValue() {
        Short value = 0x1;
        TIntObjectMap<HeapItem> initial = VMTester
                        .buildRegisterState(0, new boolean[1], "[Z", 1, 0, "I", 2, value, "Z");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new boolean[] { true }, "[Z");

        VMTester.testMethodState(CLASS_NAME, "putBoolean()V", initial, expected);
    }

    @Test
    public void testPutByte() {
        Byte value = 0xf;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new byte[1], "[B", 1, 0, "I", 2, value, "B");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new byte[] { value }, "[B");

        VMTester.testMethodState(CLASS_NAME, "putByte()V", initial, expected);
    }

    @Test
    public void testPutByteFromInt() {
        int value = 0xf;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new byte[1], "[B", 1, 0, "I", 2, value, "B");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new byte[] { (byte) value }, "[B");

        VMTester.testMethodState(CLASS_NAME, "putByte()V", initial, expected);
    }

    @Test
    public void testPutChar() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new char[1], "[C", 1, 0, "I", 2, '$', "C");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new char[] { '$' }, "[C");

        VMTester.testMethodState(CLASS_NAME, "putChar()V", initial, expected);
    }

    @Test
    public void testPutCharFromInt() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new char[1], "[C", 1, 0, "I", 2, (int) '$',
                        "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new char[] { '$' }, "[C");

        VMTester.testMethodState(CLASS_NAME, "putChar()V", initial, expected);
    }

    @Test
    public void testPutObject() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new LocalInstance[1], "[" + CLASS_NAME, 1, 0,
                        "I", 2, new LocalInstance(CLASS_NAME), CLASS_NAME);
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new LocalInstance[] { new LocalInstance(
                        CLASS_NAME) }, "[" + CLASS_NAME);

        VMTester.testMethodState(CLASS_NAME, "putObject()V", initial, expected);
    }

    @Test
    public void testPutShort() {
        Short value = 0x42;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new short[1], "[S", 1, 0, "I", 2, value, "S");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new short[] { value }, "[S");

        VMTester.testMethodState(CLASS_NAME, "putShort()V", initial, expected);
    }

    @Test
    public void testPutShortWithIntegerValue() {
        int value = 0x42;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new short[1], "[S", 1, 0, "I", 2, value, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new short[] { (short) value }, "[S");

        VMTester.testMethodState(CLASS_NAME, "putShort()V", initial, expected);
    }

    @Test
    public void testPutWithUnknownIndex() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, new UnknownValue(), "I",
                        2, 5, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "[I");

        VMTester.testMethodState(CLASS_NAME, "put()V", initial, expected);
    }

    @Test
    public void testPutUnknownValue() {
        // TODO: Ideally, setting an element unknown shouldn't set entire array unknown. See APutOp for more details.
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, 0, "I", 2,
                        new UnknownValue(), "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "[I");

        VMTester.testMethodState(CLASS_NAME, "put()V", initial, expected);
    }

    @Test
    public void testPutWideWithLong() {
        Long value = 10000000000L;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new long[1], "[J", 1, 0, "I", 2, value, "J");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new long[] { value }, "[J");

        VMTester.testMethodState(CLASS_NAME, "putWide()V", initial, expected);
    }

    @Test
    public void testPutWideWithDouble() {
        Double value = 100000000000D;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new double[1], "[D", 1, 0, "I", 2, value, "D");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new double[] { value }, "[D");

        VMTester.testMethodState(CLASS_NAME, "putWide()V", initial, expected);
    }

    @Test
    public void testPutWideWithFloat() {
        Float value = 10.45F;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new float[1], "[F", 1, 0, "I", 2, value, "F");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new float[] { value }, "[F");

        VMTester.testMethodState(CLASS_NAME, "putWide()V", initial, expected);
    }

    public static class UnfinishedUnitTest {

        private static final int REGISTER_PUT = 0;
        private static final int REGISTER_ARRAY = 1;
        private static final int REGISTER_ITEM = 2;

        private BuilderInstruction instruction;
        private OpFactory opFactory;
        private MethodState mState;
        private ExecutionNode node;
        private APutOp op;

        @Before
        public void setUp() {
            VirtualMachine vm = mock(VirtualMachine.class);
            instruction = mock(BuilderInstruction.class,
                            withSettings().extraInterfaces(ThreeRegisterInstruction.class, Instruction23x.class));
            when(((Instruction23x) instruction).getRegisterA()).thenReturn(REGISTER_PUT);
            when(((Instruction23x) instruction).getRegisterB()).thenReturn(REGISTER_ARRAY);
            when(((Instruction23x) instruction).getRegisterC()).thenReturn(REGISTER_ITEM);
            opFactory = new OpFactory(vm);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
        }

        // @Test
        public void canInsertLocalClassAndClass() {
            // Need to change new-array to return [HeapItem and make everything aware of that
            // First part easy, second part hard
            when(mState.readRegister(REGISTER_PUT)).thenReturn(
                            new HeapItem(new LocalClass("Landroid/app/Activity;"), "Ljava/lang/Class;"));
            when(mState.readRegister(REGISTER_ARRAY)).thenReturn(
                            new HeapItem(new LocalClass("Landroid/app/Activity;"), "Ljava/lang/Class;"));

            when(instruction.getOpcode()).thenReturn(Opcode.APUT_OBJECT);

            op = (APutOp) opFactory.create(instruction, 0);
            op.execute(node, mState);

            // Division result is zero since long division drops decimal value
            // verify(mState, times(1)).assignRegister(any(Integer.class), eq(value1 / value2), eq("J"));
        }
    }

}
