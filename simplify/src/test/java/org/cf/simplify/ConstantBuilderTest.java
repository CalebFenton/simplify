package org.cf.simplify;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction11n;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21ih;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21lh;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21s;
import org.jf.dexlib2.builder.instruction.BuilderInstruction31i;
import org.jf.dexlib2.builder.instruction.BuilderInstruction51l;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction;
import org.jf.dexlib2.iface.reference.StringReference;
import org.jf.dexlib2.iface.reference.TypeReference;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@RunWith(Enclosed.class)
public class ConstantBuilderTest {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ConstantBuilderTest.class.getSimpleName());
    private static final int REGISTER = 0;

    private static ExecutionGraphManipulator getMockedGraph(int address, HeapItem value) {
        ExecutionGraphManipulator manipulator = mock(ExecutionGraphManipulator.class);
        BuilderInstruction instruction =
                mock(BuilderInstruction.class, withSettings().extraInterfaces(OneRegisterInstruction.class));
        when(((OneRegisterInstruction) instruction).getRegisterA()).thenReturn(REGISTER);
        when(manipulator.getRegisterConsensus(address, REGISTER)).thenReturn(value);
        when(manipulator.getInstruction(address)).thenReturn(instruction);

        return manipulator;
    }

    private static void testEquals(Instruction expectedInstr, Instruction actualInstr) {
        assertEquals(expectedInstr.getOpcode(), actualInstr.getOpcode());

        if (expectedInstr instanceof OneRegisterInstruction) {
            OneRegisterInstruction expected = (OneRegisterInstruction) expectedInstr;
            OneRegisterInstruction actual = (OneRegisterInstruction) actualInstr;

            assertEquals(expected.getRegisterA(), actual.getRegisterA());
        }

        if (expectedInstr instanceof NarrowLiteralInstruction) {
            NarrowLiteralInstruction expected = (NarrowLiteralInstruction) expectedInstr;
            NarrowLiteralInstruction actual = (NarrowLiteralInstruction) actualInstr;

            assertEquals(expected.getNarrowLiteral(), actual.getNarrowLiteral());
        }

        if (expectedInstr instanceof WideLiteralInstruction) {
            WideLiteralInstruction expected = (WideLiteralInstruction) expectedInstr;
            WideLiteralInstruction actual = (WideLiteralInstruction) actualInstr;

            assertEquals(expected.getWideLiteral(), actual.getWideLiteral());
        }

        if (expectedInstr instanceof ReferenceInstruction) {
            ReferenceInstruction expected = (ReferenceInstruction) expectedInstr;
            ReferenceInstruction actual = (ReferenceInstruction) actualInstr;

            assertEquals(expected.getReferenceType(), actual.getReferenceType());
            assertEquals(expected.getReference(), actual.getReference());
        }
    }

    public static class BuildBoolean {

        @Test
        public void withFalse() {
            Boolean value = Boolean.FALSE;
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, 0);

            testEquals(expected, actual);
        }

        @Test
        public void withHeapItemWithBooleanTypeAndIntegerValue() {
            int intValue = 1;
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, intValue);
            HeapItem value = new HeapItem(intValue, "Z");
            int address = 0;
            ExecutionGraphManipulator manipulator = getMockedGraph(address, value);

            Instruction actual = ConstantBuilder.buildConstant(address, manipulator);

            testEquals(expected, actual);
        }

        @Test
        public void withTrue() {
            Boolean value = Boolean.TRUE;
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, 1);

            testEquals(expected, actual);
        }
    }

    public static class BuildByte {

        @Test
        public void with4BitLiteral() {
            byte value = 7;
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void with7BitLiteral() {
            byte value = Byte.MAX_VALUE;
            Instruction expected = new BuilderInstruction21s(Opcode.CONST_16, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void withHeapItemWitByteTypeAndIntegerValue() {
            int intValue = 5;
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, intValue);
            HeapItem value = new HeapItem(intValue, "B");
            int address = 0;
            ExecutionGraphManipulator manipulator = getMockedGraph(address, value);

            Instruction actual = ConstantBuilder.buildConstant(address, manipulator);

            testEquals(expected, actual);
        }
    }

    public static class BuildDouble {

        @Test
        public void withHeapItemWithDoubleTypeAndLongValue() {
            double doubleValue = Math.pow(2, 15);
            long longValue = (long) doubleValue;
            long longBits = Double.doubleToLongBits(doubleValue);
            Instruction expected = new BuilderInstruction21lh(Opcode.CONST_WIDE_HIGH16, REGISTER, longBits);
            HeapItem value = new HeapItem(longValue, "D");
            int address = 0;
            ExecutionGraphManipulator manipulator = getMockedGraph(address, value);

            Instruction actual = ConstantBuilder.buildConstant(address, manipulator);

            testEquals(expected, actual);
        }

        @Test
        public void withLast48BitsNotZero() {
            double value = 127.01;
            long longBits = Double.doubleToLongBits(value);
            Instruction expected = new BuilderInstruction51l(Opcode.CONST_WIDE, REGISTER, longBits);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void withValueWithLast48BitsZero() {
            double value = Math.pow(2, 48);
            long longBits = Double.doubleToLongBits(value);
            Instruction expected = new BuilderInstruction21lh(Opcode.CONST_WIDE_HIGH16, REGISTER, longBits);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }
    }

    public static class BuildFloat {

        @Test
        public void withHeapItemWitFloatTypeAndIntegerValue() {
            int intValue = 5;
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, intValue);
            HeapItem value = new HeapItem(intValue, "F");
            int address = 0;
            ExecutionGraphManipulator manipulator = getMockedGraph(address, value);

            Instruction actual = ConstantBuilder.buildConstant(address, manipulator);

            testEquals(expected, actual);
        }

        @Test
        public void withLast16BitsNotZero() {
            float value = 127.01F;
            int intBits = Float.floatToIntBits(value);
            Instruction expected = new BuilderInstruction31i(Opcode.CONST, REGISTER, intBits);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void withValueWithLast16BitsZero() {
            int intBits = 0xFF0000;
            float value = Float.intBitsToFloat(intBits);
            Instruction expected = new BuilderInstruction21ih(Opcode.CONST_HIGH16, REGISTER, intBits);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }
    }

    public static class BuildInteger {

        @Test
        public void with15BitLiteral() {
            int value = (int) (Math.pow(2, 15) - 1);
            Instruction expected = new BuilderInstruction21s(Opcode.CONST_16, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void with16BitLiteral() {
            int value = 50000;
            Instruction expected = new BuilderInstruction31i(Opcode.CONST, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void with31BitLiteral() {
            int value = Integer.MAX_VALUE;
            Instruction expected = new BuilderInstruction31i(Opcode.CONST, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void with4BitLiteral() {
            int value = 7;
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void withMinValue() {
            int value = Integer.MIN_VALUE;
            Instruction expected = new BuilderInstruction31i(Opcode.CONST, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }
    }

    public static class BuildLong {

        @Test
        public void with31BitLiteral() {
            long value = (long) (Math.pow(2, 31) - 1);
            Instruction expected = new BuilderInstruction31i(Opcode.CONST_WIDE_32, REGISTER, (int) value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void with4BitLiteral() {
            long value = 7L;
            Instruction expected = new BuilderInstruction21s(Opcode.CONST_WIDE_16, REGISTER, (int) value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void with64BitLiteral() {
            long value = Long.MAX_VALUE;
            Instruction expected = new BuilderInstruction51l(Opcode.CONST_WIDE, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void withNegative25BitLiteral() {
            long value = 0 - (long) (Math.pow(2, 25) - 1);
            Instruction expected = new BuilderInstruction51l(Opcode.CONST_WIDE, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }
    }

    public static class BuildObject {

        DexBuilder dexBuilder;

        @Before
        public void setUp() {
            dexBuilder = VMTester.getDexBuilder();
        }

        @Test
        public void withClass() {
            Class<?> value = String.class;
            String className = ClassNameUtils.toInternal(value);
            TypeReference typeRef = dexBuilder.internTypeReference(className);
            Instruction expected = new BuilderInstruction21c(Opcode.CONST_CLASS, REGISTER, typeRef);
            Instruction actual = ConstantBuilder.buildConstant(value, "Ljava/lang/Class;", REGISTER, dexBuilder);

            testEquals(expected, actual);
        }

        @Test
        public void withString() {
            String value = "Ever sift sand through a screen?";
            StringReference stringRef = dexBuilder.internStringReference(value);
            Instruction expected = new BuilderInstruction21c(Opcode.CONST_STRING, REGISTER, stringRef);
            Instruction actual = ConstantBuilder.buildConstant(value, "Ljava/lang/String;", REGISTER, dexBuilder);

            testEquals(expected, actual);
        }
    }

    public static class BuildShort {

        @Test
        public void with15BitLiteral() {
            short value = Short.MAX_VALUE;
            Instruction expected = new BuilderInstruction21s(Opcode.CONST_16, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void with4BitLiteral() {
            short value = 7;
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void withHeapItemWitShortTypeAndIntegerValue() {
            int intValue = 5;
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, intValue);
            HeapItem value = new HeapItem(intValue, "S");
            int address = 0;
            ExecutionGraphManipulator manipulator = getMockedGraph(address, value);

            Instruction actual = ConstantBuilder.buildConstant(address, manipulator);

            testEquals(expected, actual);
        }

    }

}
