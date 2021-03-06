package org.cf.smalivm.opcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.cf.smalivm.ExceptionFactory;
import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.VirtualMachineImpl;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction12x;
import org.jf.dexlib2.builder.instruction.BuilderInstruction22s;
import org.jf.dexlib2.builder.instruction.BuilderInstruction23x;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;
import org.jf.dexlib2.iface.instruction.formats.Instruction22s;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BinaryMathOpTest {

    private static final String CLASS_NAME = "Lbinary_math_test;";

    public static class DoubleMath {

        private VMState expected;
        private VMState initial;

        @Test
        public void canAddDouble() throws ClassNotFoundException {
            initial.setRegisters(0, 0.5D, "D", 2, 20.5D, "D");
            expected.setRegisters(0, 0.5D + 20.5D, "D");

            VMTester.test(CLASS_NAME, "addDouble()V", initial, expected);
        }

        @Test
        public void canAddDouble2Addr() {
            initial.setRegisters(0, 0.5D, "D", 2, 20.5D, "D");
            expected.setRegisters(0, 0.5D + 20.5D, "D");

            VMTester.test(CLASS_NAME, "addDouble2Addr()V", initial, expected);
        }

        @Test
        public void canDivDouble() {
            initial.setRegisters(0, 22.9D, "D", 2, 0.5D, "D");
            expected.setRegisters(0, 22.9D / 0.5D, "D");

            VMTester.test(CLASS_NAME, "divDouble()V", initial, expected);
        }

        @Test
        public void canDivDouble2Addr() {
            initial.setRegisters(0, 22.9D, "D", 2, 0.5D, "D");
            expected.setRegisters(0, 22.9D / 0.5D, "D");

            VMTester.test(CLASS_NAME, "divDouble2Addr()V", initial, expected);
        }

        @Test
        public void canDivDoubleWithDivisionByZero() {
            initial.setRegisters(0, 5D, "D", 1, 0D, "D");
            // Floats and doubles do not throw exceptions for div0
            int[] expected = new int[] { 0, 2 };

            VMTester.testVisitation(CLASS_NAME, "divDoubleWithCatch()V", initial, expected);
        }

        @Test
        public void canMulDouble() {
            initial.setRegisters(0, 0.5D, "D", 2, 20.5D, "D");
            expected.setRegisters(0, 0.5D * 20.5D, "D");

            VMTester.test(CLASS_NAME, "mulDouble()V", initial, expected);
        }

        @Test
        public void canMulDouble2Addr() {
            initial.setRegisters(0, 0.5D, "D", 2, 20.5D, "D");
            expected.setRegisters(0, 0.5D * 20.5D, "D");

            VMTester.test(CLASS_NAME, "mulDouble2Addr()V", initial, expected);
        }

        @Test
        public void canRemDouble() {
            initial.setRegisters(0, 22.9D, "D", 2, 0.5D, "D");
            expected.setRegisters(0, 22.9D % 0.5D, "D");

            VMTester.test(CLASS_NAME, "remDouble()V", initial, expected);
        }

        @Test
        public void canRemDouble2Addr() {
            initial.setRegisters(0, 22.9D, "D", 2, 0.5D, "D");
            expected.setRegisters(0, 22.9D % 0.5D, "D");

            VMTester.test(CLASS_NAME, "remDouble2Addr()V", initial, expected);
        }

        @Test
        public void canSubDouble() {
            initial.setRegisters(0, 0.5D, "D", 2, 20.5D, "D");
            expected.setRegisters(0, 0.5D - 20.5D, "D");

            VMTester.test(CLASS_NAME, "subDouble()V", initial, expected);
        }

        @Test
        public void canSubDouble2Addr() {
            initial.setRegisters(0, 0.5D, "D", 2, 20.5D, "D");
            expected.setRegisters(0, 0.5D - 20.5D, "D");

            VMTester.test(CLASS_NAME, "subDouble2Addr()V", initial, expected);
        }

        @BeforeEach
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }
    }

    public static class FloatMath {

        private VMState expected;
        private VMState initial;

        @Test
        public void canAddFloat() {
            initial.setRegisters(0, 0.5F, "F", 1, 20.5F, "F");
            expected.setRegisters(0, 0.5F + 20.5F, "F");

            VMTester.test(CLASS_NAME, "addFloat()V", initial, expected);
        }

        @Test
        public void canAddFloat2Addr() {
            initial.setRegisters(0, 0.5F, "F", 1, 20.5F, "F");
            expected.setRegisters(0, 0.5F + 20.5F, "F");

            VMTester.test(CLASS_NAME, "addFloat2Addr()V", initial, expected);
        }

        @Test
        public void canDivFloat() {
            initial.setRegisters(0, 22.9F, "F", 1, 0.5F, "F");
            expected.setRegisters(0, 22.9F / 0.5F, "F");

            VMTester.test(CLASS_NAME, "divFloat()V", initial, expected);
        }

        @Test
        public void canDivFloat2Addr() {
            initial.setRegisters(0, 22.9F, "F", 1, 0.5F, "F");
            expected.setRegisters(0, 22.9F / 0.5F, "F");

            VMTester.test(CLASS_NAME, "divFloat2Addr()V", initial, expected);
        }

        @Test
        public void canDivFloatWithDivisionByZero() {
            initial.setRegisters(0, 5F, "F", 1, 0F, "F");
            // Floats and doubles do not throw exceptions for div0
            int[] expected = new int[] { 0, 2 };

            VMTester.testVisitation(CLASS_NAME, "divFloatWithCatch()V", initial, expected);
        }

        @Test
        public void canMulFloat() {
            initial.setRegisters(0, 0.5F, "F", 1, 20.5F, "F");
            expected.setRegisters(0, 0.5F * 20.5F, "F");

            VMTester.test(CLASS_NAME, "mulFloat()V", initial, expected);
        }

        @Test
        public void canMulFloat2Addr() {
            initial.setRegisters(0, 0.5F, "F", 1, 20.5F, "F");
            expected.setRegisters(0, 0.5F * 20.5F, "F");

            VMTester.test(CLASS_NAME, "mulFloat2Addr()V", initial, expected);
        }

        @Test
        public void canRemFloat() {
            initial.setRegisters(0, 22.9F, "F", 1, 0.5F, "F");
            expected.setRegisters(0, 22.9F % 0.5F, "F");

            VMTester.test(CLASS_NAME, "remFloat()V", initial, expected);
        }

        @Test
        public void canRemFloat2Addr() {
            initial.setRegisters(0, 22.9F, "F", 1, 0.5F, "F");
            expected.setRegisters(0, 22.9F % 0.5F, "F");

            VMTester.test(CLASS_NAME, "remFloat2Addr()V", initial, expected);
        }

        @Test
        public void canSubFloat() {
            initial.setRegisters(0, 0.5F, "F", 1, 20.5F, "F");
            expected.setRegisters(0, 0.5F - 20.5F, "F");

            VMTester.test(CLASS_NAME, "subFloat()V", initial, expected);
        }

        @Test
        public void canSubFloat2Addr() {
            initial.setRegisters(0, 0.5F, "F", 1, 20.5F, "F");
            expected.setRegisters(0, 0.5F - 20.5F, "F");

            VMTester.test(CLASS_NAME, "subFloat2Addr()V", initial, expected);
        }

        @BeforeEach
        public void setUp() {
            initial = new VMState();
            expected = new VMState();
        }
    }

    public static class IntegerMath {

        private VMState expected;
        private VMState initial;

        @Test
        public void canAddByteAndChar() {
            Byte b = 0xf;
            initial.setRegisters(0, 'a', "C", 1, b, "B");
            expected.setRegisters(0, 'a' + b, "I");

            VMTester.test(CLASS_NAME, "addInt()V", initial, expected);
        }

        @Test
        public void canAddInt() {
            initial.setRegisters(0, -3, "I", 1, 7, "I");
            expected.setRegisters(0, -3 + 7, "I");

            VMTester.test(CLASS_NAME, "addInt()V", initial, expected);
        }

        @Test
        public void canAddInt2Addr() {
            initial.setRegisters(0, -3, "I", 1, 7, "I");
            expected.setRegisters(0, -3 + 7, "I");

            VMTester.test(CLASS_NAME, "addInt2Addr()V", initial, expected);
        }

        @Test
        public void canAddIntAndBoolean() {
            Boolean value = true;
            initial.setRegisters(0, value, "Z", 1, 11, "I");
            expected.setRegisters(0, 1 + 11, "I");

            VMTester.test(CLASS_NAME, "addInt()V", initial, expected);
        }

        @Test
        public void canAddIntAndByte() {
            Byte b = 0xf;
            initial.setRegisters(0, -3, "I", 1, b, "B");
            expected.setRegisters(0, 12, "I");

            VMTester.test(CLASS_NAME, "addInt()V", initial, expected);
        }

        @Test
        public void canAddIntAndChar() {
            // Compiler will actually produce something like this.
            initial.setRegisters(0, "$".charAt(0), "C", 1, 11, "I");
            expected.setRegisters(0, "$".charAt(0) + 11, "I");

            VMTester.test(CLASS_NAME, "addInt()V", initial, expected);
        }

        @Test
        public void canAddIntAndShort() {
            Short value = 5;
            initial.setRegisters(0, value, "S", 1, 11, "I");
            expected.setRegisters(0, value + 11, "I");

            VMTester.test(CLASS_NAME, "addInt()V", initial, expected);
        }

        @Test
        public void canAddIntAndUnknownChar() {
            initial.setRegisters(0, new UnknownValue(), "C", 1, 5, "I");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "addInt()V", initial, expected);
        }

        @Test
        public void canAddIntAndUnknownCharAndByte() {
            initial.setRegisters(0, new UnknownValue(), "C", 1, new UnknownValue(), "B");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "addInt()V", initial, expected);
        }

        @Test
        public void canAddIntAndUnknownInt() {
            initial.setRegisters(0, -3, "I", 1, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "addInt()V", initial, expected);
        }

        @Test
        public void canAddIntLit16() {
            initial.setRegisters(0, -0xf, "I");
            expected.setRegisters(0, (-0xf + 0xff), "I");
            VMTester.test(CLASS_NAME, "addIntLit16()V", initial, expected);
        }

        @Test
        public void canAddIntLit8() {
            initial.setRegisters(0, -0xf, "I");
            expected.setRegisters(0, (-0xf + 0xf), "I");
            VMTester.test(CLASS_NAME, "addIntLit8()V", initial, expected);
        }

        @Test
        public void canAndInt() {
            initial.setRegisters(0, 2, "I", 1, 7, "I");
            expected.setRegisters(0, 2 & 7, "I");

            VMTester.test(CLASS_NAME, "andInt()V", initial, expected);
        }

        @Test
        public void canAndInt2Addr() {
            initial.setRegisters(0, 2, "I", 1, 7, "I");
            expected.setRegisters(0, 2 & 7, "I");

            VMTester.test(CLASS_NAME, "andInt2Addr()V", initial, expected);
        }

        @Test
        public void canAndIntLit16() {
            initial.setRegisters(0, 2, "I");
            expected.setRegisters(0, 2 & 0xff, "I");

            VMTester.test(CLASS_NAME, "andIntLit16()V", initial, expected);
        }

        @Test
        public void canAndIntLit8() {
            initial.setRegisters(0, 2, "I");
            expected.setRegisters(0, 2 & 0xf, "I");

            VMTester.test(CLASS_NAME, "andIntLit8()V", initial, expected);
        }

        @Test
        public void canDivInt() {
            initial.setRegisters(0, 12, "I", 1, 3, "I");
            expected.setRegisters(0, 12 / 3, "I");

            VMTester.test(CLASS_NAME, "divInt()V", initial, expected);
        }

        @Test
        public void canDivInt2Addr() {
            initial.setRegisters(0, 12, "I", 1, 3, "I");
            expected.setRegisters(0, 12 / 3, "I");

            VMTester.test(CLASS_NAME, "divInt2Addr()V", initial, expected);
        }

        @Test
        public void canDivIntLit16() {
            initial.setRegisters(0, 0x100, "I");
            expected.setRegisters(0, 1, "I");

            VMTester.test(CLASS_NAME, "divIntLit16()V", initial, expected);
        }

        @Test
        public void canDivIntLit8() {
            initial.setRegisters(0, 0x10, "I");
            expected.setRegisters(0, 1, "I");

            VMTester.test(CLASS_NAME, "divIntLit8()V", initial, expected);
        }

        @Test
        public void canDivIntWithCatchWithUnknownValueVisitsExceptionHandler() {
            initial.setRegisters(0, new UnknownValue(), "I", 1, 5, "I");
            int[] expected = new int[] { 0, 2, 3, 4 };

            VMTester.testVisitation(CLASS_NAME, "divIntWithCatch()V", initial, expected);
        }

        @Test
        public void canMulInt() {
            initial.setRegisters(0, -3, "I", 1, 7, "I");
            expected.setRegisters(0, -3 * 7, "I");

            VMTester.test(CLASS_NAME, "mulInt()V", initial, expected);
        }

        @Test
        public void canMulInt2Addr() {
            initial.setRegisters(0, -3, "I", 1, 10, "I");
            expected.setRegisters(0, -3 * 10, "I");

            VMTester.test(CLASS_NAME, "mulInt2Addr()V", initial, expected);
        }

        @Test
        public void canMulIntLit16() {
            initial.setRegisters(0, -3, "I");
            expected.setRegisters(0, -3 * 0xff, "I");

            VMTester.test(CLASS_NAME, "mulIntLit16()V", initial, expected);
        }

        @Test
        public void canMulIntLit8() {
            initial.setRegisters(0, -3, "I");
            expected.setRegisters(0, -3 * 0xf, "I");

            VMTester.test(CLASS_NAME, "mulIntLit8()V", initial, expected);
        }

        @Test
        public void canOrInt() {
            initial.setRegisters(0, 5, "I", 1, 2, "I");
            expected.setRegisters(0, 5 | 2, "I");

            VMTester.test(CLASS_NAME, "orInt()V", initial, expected);
        }

        @Test
        public void canOrInt2Addr() {
            initial.setRegisters(0, 5, "I", 1, 2, "I");
            expected.setRegisters(0, 5 | 2, "I");

            VMTester.test(CLASS_NAME, "orInt2Addr()V", initial, expected);
        }

        @Test
        public void canOrIntLit16() {
            initial.setRegisters(0, 5, "I");
            expected.setRegisters(0, 5 | 0xff, "I");

            VMTester.test(CLASS_NAME, "orIntLit16()V", initial, expected);
        }

        @Test
        public void canOrIntLit8() {
            initial.setRegisters(0, 5, "I");
            expected.setRegisters(0, 5 | 0xf, "I");

            VMTester.test(CLASS_NAME, "orIntLit8()V", initial, expected);
        }

        @Test
        public void canRemInt() {
            initial.setRegisters(0, 7, "I", 1, 5, "I");
            expected.setRegisters(0, 7 % 5, "I");

            VMTester.test(CLASS_NAME, "remInt()V", initial, expected);
        }

        @Test
        public void canRemInt2Addr() {
            initial.setRegisters(0, 7, "I", 1, 5, "I");
            expected.setRegisters(0, 7 % 5, "I");

            VMTester.test(CLASS_NAME, "remInt2Addr()V", initial, expected);
        }

        @Test
        public void canRemIntLit16() {
            initial.setRegisters(0, 0x100, "I");
            expected.setRegisters(0, 0x100 % 0xff, "I");

            VMTester.test(CLASS_NAME, "remIntLit16()V", initial, expected);
        }

        @Test
        public void canRemIntLit8() {
            initial.setRegisters(0, 0x10, "I");
            expected.setRegisters(0, 0x10 % 0xf, "I");

            VMTester.test(CLASS_NAME, "remIntLit8()V", initial, expected);
        }

        @Test
        public void canRSubInt() {
            initial.setRegisters(0, 7, "I");
            expected.setRegisters(0, 0xff - 7, "I");

            VMTester.test(CLASS_NAME, "RSubInt()V", initial, expected);
        }

        @Test
        public void canRSubIntLit8() {
            initial.setRegisters(0, 7, "I");
            expected.setRegisters(0, 0xf - 7, "I");

            VMTester.test(CLASS_NAME, "RSubIntLit8()V", initial, expected);
        }

        @Test
        public void canShlInt() {
            initial.setRegisters(0, 5, "I", 1, 2, "I");
            expected.setRegisters(0, 5 << 2, "I");

            VMTester.test(CLASS_NAME, "shlInt()V", initial, expected);
        }

        @Test
        public void canShlInt2Addr() {
            initial.setRegisters(0, 5, "I", 1, 2, "I");
            expected.setRegisters(0, 5 << 2, "I");

            VMTester.test(CLASS_NAME, "shlInt2Addr()V", initial, expected);
        }

        @Test
        public void canShlIntLit8() {
            initial.setRegisters(0, 3, "I");
            expected.setRegisters(0, 3 << 0x2, "I");

            VMTester.test(CLASS_NAME, "shlIntLit8()V", initial, expected);
        }

        @Test
        public void canShrInt() {
            initial.setRegisters(0, 7, "I", 1, 2, "I");
            expected.setRegisters(0, 7 >> 2, "I");

            VMTester.test(CLASS_NAME, "shrInt()V", initial, expected);
        }

        @Test
        public void canShrInt2Addr() {
            initial.setRegisters(0, 7, "I", 1, 2, "I");
            expected.setRegisters(0, 7 >> 2, "I");

            VMTester.test(CLASS_NAME, "shrInt2Addr()V", initial, expected);
        }

        @Test
        public void canShrIntLit8() {
            initial.setRegisters(0, 7, "I", 1, 2, "I");
            expected.setRegisters(0, 7 >> 2, "I");

            VMTester.test(CLASS_NAME, "shrIntLit8()V", initial, expected);
        }

        @Test
        public void canSubInt() {
            initial.setRegisters(0, -3, "I", 1, 7, "I");
            expected.setRegisters(0, -3 - 7, "I");

            VMTester.test(CLASS_NAME, "subInt()V", initial, expected);
        }

        @Test
        public void canSubInt2Addr() {
            initial.setRegisters(0, -3, "I", 1, 7, "I");
            expected.setRegisters(0, -3 - 7, "I");

            VMTester.test(CLASS_NAME, "subInt2Addr()V", initial, expected);
        }

        @Test
        public void canUshrInt() {
            initial.setRegisters(0, -14, "I", 1, 2, "I");
            expected.setRegisters(0, -14 >>> 2, "I");

            VMTester.test(CLASS_NAME, "ushrInt()V", initial, expected);
        }

        @Test
        public void canUshrInt2Addr() {
            initial.setRegisters(0, -14, "I", 1, 2, "I");
            expected.setRegisters(0, -14 >>> 2, "I");

            VMTester.test(CLASS_NAME, "ushrInt2Addr()V", initial, expected);
        }

        @Test
        public void canUshrIntLit8() {
            initial.setRegisters(0, -14, "I");
            expected.setRegisters(0, -14 >>> 2, "I");

            VMTester.test(CLASS_NAME, "ushrIntLit8()V", initial, expected);
        }

        @Test
        public void canXorInt() {
            initial.setRegisters(0, 7, "I", 1, 2, "I");
            expected.setRegisters(0, 7 ^ 2, "I");

            VMTester.test(CLASS_NAME, "xorInt()V", initial, expected);
        }

        @Test
        public void canXorInt2Addr() {
            initial.setRegisters(0, 7, "I", 1, 2, "I");
            expected.setRegisters(0, 7 ^ 2, "I");

            VMTester.test(CLASS_NAME, "xorInt2Addr()V", initial, expected);
        }

        @Test
        public void canXorIntLit16() {
            initial.setRegisters(0, 7, "I");
            expected.setRegisters(0, 7 ^ 0x10, "I");

            VMTester.test(CLASS_NAME, "xorIntLit16()V", initial, expected);
        }

        @Test
        public void canXorIntLit8() {
            initial.setRegisters(0, 7, "I");
            expected.setRegisters(0, 7 ^ 3, "I");

            VMTester.test(CLASS_NAME, "xorIntLit8()V", initial, expected);
        }

        @BeforeEach
        public void setUp() {
            initial = new VMState();
            expected = new VMState();
        }

    }

    public static class LongMath {

        private VMState expected;
        private VMState initial;

        @Test
        public void canAddLong() {
            initial.setRegisters(0, 0x100000000L, "J", 2, 0x200000000L, "J");
            expected.setRegisters(0, 0x100000000L + 0x200000000L, "J");

            VMTester.test(CLASS_NAME, "addLong()V", initial, expected);
        }

        @Test
        public void canAddLong2Addr() {
            initial.setRegisters(0, 0x100000000L, "J", 2, 0x200000000L, "J");
            expected.setRegisters(0, 0x100000000L + 0x200000000L, "J");

            VMTester.test(CLASS_NAME, "addLong2Addr()V", initial, expected);
        }

        @Test
        public void canAndLong() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J");
            expected.setRegisters(0, 0x12345abcdL & 0x1234567890L, "J");

            VMTester.test(CLASS_NAME, "andLong()V", initial, expected);
        }

        @Test
        public void canAndLong2Addr() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J");
            expected.setRegisters(0, 0x12345abcdL & 0x1234567890L, "J");

            VMTester.test(CLASS_NAME, "andLong2Addr()V", initial, expected);
        }

        @Test
        public void canDivLong() {
            initial.setRegisters(0, 0x300000000L, "J", 2, 3L, "J");
            expected.setRegisters(0, 0x300000000L / 3L, "J");

            VMTester.test(CLASS_NAME, "divLong()V", initial, expected);
        }

        @Test
        public void canDivLong2Addr() {
            initial.setRegisters(0, 0x300000000L, "J", 2, 3L, "J");
            expected.setRegisters(0, 0x300000000L / 3L, "J");

            VMTester.test(CLASS_NAME, "divLong2Addr()V", initial, expected);
        }

        @Test
        public void canDivLongWithCatchWithUnkownValueVisitsExceptionHandler() {
            initial.setRegisters(0, 5L, "J", 1, new UnknownValue(), "J");
            int[] expected = new int[] { 0, 2, 3, 4 };

            VMTester.testVisitation(CLASS_NAME, "divLongWithCatch()V", initial, expected);
        }

        @Test
        public void canMulLong() {
            initial.setRegisters(0, 0x300000000L, "J", 2, 3L, "J");
            expected.setRegisters(0, 0x300000000L * 3L, "J");

            VMTester.test(CLASS_NAME, "mulLong()V", initial, expected);
        }

        @Test
        public void canMulLong2Addr() {
            initial.setRegisters(0, 0x300000000L, "J", 2, 3L, "J");
            expected.setRegisters(0, 0x300000000L * 3L, "J");

            VMTester.test(CLASS_NAME, "mulLong2Addr()V", initial, expected);
        }

        @Test
        public void canOrLong() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J");
            expected.setRegisters(0, 0x12345abcdL | 0x1234567890L, "J");

            VMTester.test(CLASS_NAME, "orLong()V", initial, expected);
        }

        @Test
        public void canOrLong2Addr() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J");
            expected.setRegisters(0, 0x12345abcdL | 0x1234567890L, "J");

            VMTester.test(CLASS_NAME, "orLong2Addr()V", initial, expected);
        }

        @Test
        public void canRemLong() {
            initial.setRegisters(0, 0x300000123L, "J", 2, 3L, "J");
            expected.setRegisters(0, 0x300000123L % 3L, "J");

            VMTester.test(CLASS_NAME, "remLong()V", initial, expected);
        }

        @Test
        public void canRemLong2Addr() {
            initial.setRegisters(0, 0x300000123L, "J", 2, 3L, "J");
            expected.setRegisters(0, 0x300000123L % 3L, "J");

            VMTester.test(CLASS_NAME, "remLong2Addr()V", initial, expected);
        }

        @Test
        public void canShlLong() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J");
            expected.setRegisters(0, 0x10000L << 5L, "J");

            VMTester.test(CLASS_NAME, "shlLong()V", initial, expected);
        }

        @Test
        public void canShlLong2Addr() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J");
            expected.setRegisters(0, 0x10000L << 5L, "J");

            VMTester.test(CLASS_NAME, "shlLong2Addr()V", initial, expected);
        }

        @Test
        public void canShrLong() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J");
            expected.setRegisters(0, 0x10000L >> 5L, "J");

            VMTester.test(CLASS_NAME, "shrLong()V", initial, expected);
        }

        @Test
        public void canShrLong2Addr() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J");
            expected.setRegisters(0, 0x10000L >> 5L, "J");

            VMTester.test(CLASS_NAME, "shrLong2Addr()V", initial, expected);
        }

        @Test
        public void canSubLong() {
            initial.setRegisters(0, 0x100000000L, "J", 2, 0x200000000L, "J");
            expected.setRegisters(0, 0x100000000L - 0x200000000L, "J");

            VMTester.test(CLASS_NAME, "subLong()V", initial, expected);
        }

        @Test
        public void canSubLong2Addr() {
            initial.setRegisters(0, 0x100000000L, "J", 2, 0x200000000L, "J");
            expected.setRegisters(0, 0x100000000L - 0x200000000L, "J");

            VMTester.test(CLASS_NAME, "subLong2Addr()V", initial, expected);
        }

        @Test
        public void canUshrLong() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J");
            expected.setRegisters(0, 0x10000L >>> 5L, "J");

            VMTester.test(CLASS_NAME, "ushrLong()V", initial, expected);
        }

        @Test
        public void canUshrLong2Addr() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J");
            expected.setRegisters(0, 0x10000L >>> 5L, "J");

            VMTester.test(CLASS_NAME, "ushrLong2Addr()V", initial, expected);
        }

        @Test
        public void canXorLong() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J");
            expected.setRegisters(0, 0x12345abcdL ^ 0x1234567890L, "J");

            VMTester.test(CLASS_NAME, "xorLong()V", initial, expected);
        }

        @Test
        public void canXorLong2Addr() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J");
            expected.setRegisters(0, 0x12345abcdL ^ 0x1234567890L, "J");

            VMTester.test(CLASS_NAME, "xorLong2Addr()V", initial, expected);
        }

        @BeforeEach
        public void setUp() {
            initial = new VMState();
            expected = new VMState();
        }
    }

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int ARG1_REGISTER = 2;
        private static final int ARG2_REGISTER = 4;
        private static final int DEST_REGISTER = 0;

        private TIntObjectMap<MethodLocation> addressToLocation;
        private MethodLocation location;
        private MethodState mState;
        private ExecutionNode node;
        private BinaryMathOp op;
        private BinaryMathOpFactory opFactory;
        private VirtualMachine vm;

        @Test
        public void doubleDivisionWithTwoRegistersEqualsExpected() {
            double value1 = 1586.2D;
            double value2 = 2536.9D;
            double expected = value1 / value2;
            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "D");
            VMTester.setRegisterMock(mState, ARG2_REGISTER, value2, "D");

            buildInstruction12x(Opcode.DIV_DOUBLE);
            op = opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(ARG1_REGISTER), eq(expected), eq("D"));
        }

        @Test
        public void floatDivisionWithTwoRegistersEqualsExpected() {
            float value1 = 1120403456.43F;
            float value2 = 1149239296.32F;
            float expected = value1 / value2;
            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "F");
            VMTester.setRegisterMock(mState, ARG2_REGISTER, value2, "F");

            buildInstruction12x(Opcode.DIV_FLOAT);
            op = opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(ARG1_REGISTER), eq(expected), eq("F"));
        }

        @Test
        public void intDivisionWithLiteralEqualsExpected() {
            int value1 = 10;
            int value2 = 4;
            int expected = value1 / value2;
            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "I");

            buildInstruction22s(Opcode.DIV_INT, value2);
            op = opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(DEST_REGISTER), eq(expected), eq("I"));
        }

        @Test
        public void integerDivisionByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
            int value1 = 10;
            int value2 = 0;
            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "I");

            buildInstruction22s(Opcode.DIV_INT_LIT16, value2);
            op = opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VMTester.verifyExceptionHandling(ArithmeticException.class, "/ by zero", node, mState);
        }

        @Test
        public void integerModuloByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
            int value1 = 10;
            int value2 = 0;
            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "I");

            buildInstruction22s(Opcode.REM_INT_LIT16, value2);
            op = opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VMTester.verifyExceptionHandling(ArithmeticException.class, "/ by zero", node, mState);
        }

        @Test
        public void longDivisionByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
            long value1 = 1120403456L;
            long value2 = 0L;
            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "J");
            VMTester.setRegisterMock(mState, ARG2_REGISTER, value2, "J");

            buildInstruction23x(Opcode.DIV_LONG);
            op = opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VMTester.verifyExceptionHandling(ArithmeticException.class, "/ by zero", node, mState);
        }

        @Test
        public void longDivisionWithThreeArgumentsAndResultLessThanOneEqualsZero() {
            long value1 = 1120403456L;
            long value2 = 1149239296L;
            long expected = value1 / value2; // 0 since long division drops decimal value
            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "J");
            VMTester.setRegisterMock(mState, ARG2_REGISTER, value2, "J");

            buildInstruction23x(Opcode.DIV_LONG);
            op = opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(DEST_REGISTER), eq(expected), eq("J"));
            assertEquals("div-long r" + DEST_REGISTER + ", r" + ARG1_REGISTER + ", r" + ARG2_REGISTER, op.toString());
        }

        @Test
        public void longModuloByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
            long value1 = 1120403456L;
            long value2 = 0L;
            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "J");
            VMTester.setRegisterMock(mState, ARG2_REGISTER, value2, "J");

            buildInstruction23x(Opcode.REM_LONG);
            op = opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VMTester.verifyExceptionHandling(ArithmeticException.class, "/ by zero", node, mState);
        }

        @BeforeEach
        public void setUp() {
            vm = mock(VirtualMachineImpl.class);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
            location = mock(MethodLocation.class);
            when(location.getCodeAddress()).thenReturn(ADDRESS);

            ExceptionFactory exceptionFactory = mock(ExceptionFactory.class);
            when(vm.getExceptionFactory()).thenReturn(exceptionFactory);

            addressToLocation = new TIntObjectHashMap<>();
            addressToLocation.put(ADDRESS, location);

            opFactory = new BinaryMathOpFactory();
        }

        private BuilderInstruction12x buildInstruction12x(Opcode opcode) {
            BuilderInstruction12x instruction = mock(BuilderInstruction12x.class);
            setupInstruction(instruction, opcode);
            when(((Instruction12x) instruction).getRegisterA()).thenReturn(ARG1_REGISTER);
            when(((Instruction12x) instruction).getRegisterB()).thenReturn(ARG2_REGISTER);

            return instruction;
        }

        private BuilderInstruction22s buildInstruction22s(Opcode opcode, int value) {
            BuilderInstruction22s instruction = mock(BuilderInstruction22s.class);
            setupInstruction(instruction, opcode);
            when(((Instruction22s) instruction).getRegisterA()).thenReturn(DEST_REGISTER);
            when(((Instruction22s) instruction).getRegisterB()).thenReturn(ARG1_REGISTER);
            when(((Instruction22s) instruction).getNarrowLiteral()).thenReturn(value);

            return instruction;
        }

        private BuilderInstruction23x buildInstruction23x(Opcode opcode) {
            BuilderInstruction23x instruction = mock(BuilderInstruction23x.class);
            setupInstruction(instruction, opcode);
            when(((Instruction23x) instruction).getRegisterA()).thenReturn(DEST_REGISTER);
            when(((Instruction23x) instruction).getRegisterB()).thenReturn(ARG1_REGISTER);
            when(((Instruction23x) instruction).getRegisterC()).thenReturn(ARG2_REGISTER);

            return instruction;
        }

        private void setupInstruction(BuilderInstruction instruction, Opcode opcode) {
            when(location.getInstruction()).thenReturn(instruction);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(instruction.getOpcode()).thenReturn(opcode);
        }

    }

}
