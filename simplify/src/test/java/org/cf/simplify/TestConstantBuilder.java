package org.cf.simplify;

import static org.junit.Assert.assertEquals;

import org.cf.smalivm.VMTester;
import org.cf.util.SmaliClassUtils;
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
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Enclosed.class)
public class TestConstantBuilder {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TestConstantBuilder.class.getSimpleName());

    private static final int REGISTER = 0;

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

    @RunWith(MockitoJUnitRunner.class)
    public static class buildConstantUnitTest {
        ConstantBuilder underTest;

        @Before
        public void setUp() {
            underTest = new ConstantBuilder();
        }

        @Test
        public void testIntLowValueLowRegister() {
            BuilderInstruction bi = underTest.buildConstant(1, 2);
            assertEquals(BuilderInstruction11n.FORMAT, bi.getFormat());
        }

        @Test
        public void testIntLowValueHighRegister() {
            BuilderInstruction bi = underTest.buildConstant(1, 16);
            assertEquals(BuilderInstruction21s.FORMAT, bi.getFormat());
        }

        @Test
        public void testIntHighValueNormalRegister() {
            BuilderInstruction bi = underTest.buildConstant(255555, 16);
            assertEquals(BuilderInstruction31i.FORMAT, bi.getFormat());
        }

        @Test
        public void testLongLowValueLowRegister() {
            BuilderInstruction bi = underTest.buildConstant(1L, 2);
            assertEquals(BuilderInstruction21s.FORMAT, bi.getFormat());
        }

        @Test
        public void testLongLowValueHighRegister() {
            BuilderInstruction bi = underTest.buildConstant(255555L, 16);
            assertEquals(BuilderInstruction31i.FORMAT, bi.getFormat());
        }

        @Test
        public void testLongHighValueNormalRegister() {
            BuilderInstruction bi = underTest.buildConstant(2555555555L, 32);
            assertEquals(BuilderInstruction51l.FORMAT, bi.getFormat());
        }

    }

    public static class TestBuildBoolean {
        @Test
        public void testWithTrue() {
            Boolean value = Boolean.TRUE;
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, 1);

            testEquals(expected, actual);
        }

        @Test
        public void testWithFalse() {
            Boolean value = Boolean.FALSE;
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, 0);

            testEquals(expected, actual);
        }
    }

    public static class TestBuildDouble {
        @Test
        public void testWithValueWithLast48BitsZero() {
            double value = Math.pow(2, 48);
            long longBits = Double.doubleToLongBits(value);
            Instruction expected = new BuilderInstruction21lh(Opcode.CONST_WIDE_HIGH16, REGISTER, longBits);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void testWithLast48BitsNotZero() {
            double value = 127.01;
            long longBits = Double.doubleToLongBits(value);
            Instruction expected = new BuilderInstruction51l(Opcode.CONST_WIDE, REGISTER, longBits);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }
    }

    public static class TestBuildFloat {
        @Test
        public void testWithValueWithLast16BitsZero() {
            float value = 127F;
            int intBits = Float.floatToIntBits(value);
            Instruction expected = new BuilderInstruction21ih(Opcode.CONST_HIGH16, REGISTER, intBits);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void testWithLast16BitsNotZero() {
            float value = 127.01F;
            int intBits = Float.floatToIntBits(value);
            Instruction expected = new BuilderInstruction31i(Opcode.CONST, REGISTER, intBits);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }
    }

    public static class TestBuildShort {
        @Test
        public void testWith4BitLiteral() {
            short value = 7;
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void testWith15BitLiteral() {
            short value = Short.MAX_VALUE;
            Instruction expected = new BuilderInstruction21s(Opcode.CONST_16, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }
    }

    public static class TestBuildByte {
        @Test
        public void testWith4BitLiteral() {
            byte value = 7;
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void testWith7BitLiteral() {
            byte value = Byte.MAX_VALUE;
            Instruction expected = new BuilderInstruction21s(Opcode.CONST_16, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }
    }

    public static class TestBuildInteger {
        @Test
        public void testWith4BitLiteral() {
            int value = 7;
            Instruction expected = new BuilderInstruction11n(Opcode.CONST_4, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void testWith15BitLiteral() {
            int value = (int) (Math.pow(2, 15) - 1);
            Instruction expected = new BuilderInstruction21s(Opcode.CONST_16, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void testWith31BitLiteral() {
            int value = Integer.MAX_VALUE;
            Instruction expected = new BuilderInstruction31i(Opcode.CONST, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }
    }

    public static class TestBuildLong {
        @Test
        public void testWith4BitLiteral() {
            long value = 7L;
            Instruction expected = new BuilderInstruction21s(Opcode.CONST_WIDE_16, REGISTER, (int) value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void testWith31BitLiteral() {
            long value = (long) (Math.pow(2, 31) - 1);
            Instruction expected = new BuilderInstruction31i(Opcode.CONST_WIDE_32, REGISTER, (int) value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }

        @Test
        public void testWith64BitLiteral() {
            long value = Long.MAX_VALUE;
            Instruction expected = new BuilderInstruction51l(Opcode.CONST_WIDE, REGISTER, value);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER);

            testEquals(expected, actual);
        }
    }

    public static class TestBuildObject {
        DexBuilder dexBuilder;

        @Before
        public void setUp() {
            dexBuilder = VMTester.getDexBuilder();
        }

        @Test
        public void testWithString() {
            String value = "Ever sift sand through a screen?";
            StringReference stringRef = dexBuilder.internStringReference(value);
            Instruction expected = new BuilderInstruction21c(Opcode.CONST_STRING, REGISTER, stringRef);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER, dexBuilder);

            testEquals(expected, actual);
        }

        @Test
        public void testWithClass() {
            Class<?> value = String.class;
            String className = SmaliClassUtils.javaClassToSmali(value);
            TypeReference typeRef = dexBuilder.internTypeReference(className);
            Instruction expected = new BuilderInstruction21c(Opcode.CONST_CLASS, REGISTER, typeRef);
            Instruction actual = ConstantBuilder.buildConstant(value, REGISTER, dexBuilder);

            testEquals(expected, actual);
        }
    }

}
