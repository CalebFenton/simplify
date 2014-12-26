package org.cf.simplify;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.opcode.AGetOp;
import org.cf.smalivm.opcode.BinaryMathOp;
import org.cf.smalivm.opcode.MoveOp;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.opcode.ReturnOp;
import org.cf.smalivm.opcode.SGetOp;
import org.cf.smalivm.opcode.UnaryMathOp;
import org.cf.smalivm.type.LocalClass;
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
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.writer.builder.BuilderStringReference;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantBuilder implements Dependancy {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ConstantBuilder.class.getSimpleName());

    private static final String LAST_16_BITS_ZERO = "0000000000000000";
    private static final String LAST_48_BITS_ZERO = "000000000000000000000000000000000000000000000000";

    private static final Set<Class<?>> ConstantizableOps = new HashSet<Class<?>>(Arrays.asList(BinaryMathOp.class,
                    UnaryMathOp.class, MoveOp.class, ReturnOp.class, SGetOp.class, AGetOp.class));

    private static final Set<String> ConstantizableTypes = new HashSet<String>(Arrays.asList("I", "Z", "B", "S", "C",
                    "J", "F", "D", "Ljava/lang/String;", "Ljava/lang/Class;"));

    public boolean canConstantizeOp(Op op) {
        return ConstantizableOps.contains(op.getClass());
    }

    public boolean canConstantizeType(String type) {
        return ConstantizableTypes.contains(type);
    }

    private static int getBitSize(long x) {
        int result = 1;
        while ((result < 64) && (x >= (1L << result))) {
            result++;
        }

        return result;
    }

    public static BuilderInstruction buildConstant(boolean value, int register) {
        int literal = value ? 1 : 0;

        return buildConstant(literal, register);
    }

    public static BuilderInstruction buildConstant(double value, int register) {
        long longBits = Double.doubleToLongBits(value);
        String binaryValue = Long.toBinaryString(longBits);
        BuilderInstruction result;
        if (binaryValue.endsWith(LAST_48_BITS_ZERO)) {
            result = new BuilderInstruction21lh(Opcode.CONST_WIDE_HIGH16, register, longBits);
        } else {
            result = new BuilderInstruction51l(Opcode.CONST_WIDE, register, longBits);
        }

        return result;
    }

    public static BuilderInstruction buildConstant(float value, int register) {
        int intBits = Float.floatToIntBits(value);
        String binaryValue = Integer.toBinaryString(intBits);
        BuilderInstruction result;
        if (binaryValue.endsWith(LAST_16_BITS_ZERO)) {
            result = new BuilderInstruction21ih(Opcode.CONST_HIGH16, register, intBits);
        } else {
            result = new BuilderInstruction31i(Opcode.CONST, register, intBits);
        }

        return result;
    }

    public static BuilderInstruction buildConstant(Short value, int register) {
        return buildConstant(value.intValue(), register);
    }

    public static BuilderInstruction buildConstant(Byte value, int register) {
        return buildConstant(value.intValue(), register);
    }

    public static BuilderInstruction buildConstant(int value, int register) {
        BuilderInstruction result;
        int bitSize = getBitSize(value);
        if ((bitSize < 4) && ((register & 0xFFFFFFF0) == 0)) {
            result = new BuilderInstruction11n(Opcode.CONST_4, register, value);
        } else if ((bitSize < 16) && ((register & 0xFFFFFF00) == 0)) {
            result = new BuilderInstruction21s(Opcode.CONST_16, register, value);
        } else {
            result = new BuilderInstruction31i(Opcode.CONST, register, value);
        }

        return result;
    }

    public static BuilderInstruction buildConstant(long value, int register) {
        BuilderInstruction result;
        int bitSize = getBitSize(value);
        if ((bitSize < 16) && ((register & 0xFFFFFF00) == 0)) {
            result = new BuilderInstruction21s(Opcode.CONST_WIDE_16, register, (int) value);
        } else if ((bitSize < 32) && ((register & 0xFFFFFF00) == 0)) {
            result = new BuilderInstruction31i(Opcode.CONST_WIDE_32, register, (int) value);
        } else {
            result = new BuilderInstruction51l(Opcode.CONST_WIDE, register, value);
        }

        return result;
    }

    public static BuilderInstruction buildConstant(Object value, String type, int register, DexBuilder dexBuilder) {
        BuilderInstruction result = null;
        if (type.equals("I")) {
            result = buildConstant((Integer) value, register);
        } else if (type.equals("B")) {
            result = buildConstant((Byte) value, register);
        } else if (type.equals("S")) {
            result = buildConstant((Short) value, register);
        } else if (type.equals("C")) {
            result = buildConstant((Character) value, register);
        } else if (type.equals("Z")) {
            result = buildConstant(((Boolean) value), register);
        } else if (type.equals("J")) {
            result = buildConstant((Long) value, register);
        } else if (type.equals("F")) {
            result = buildConstant((Float) value, register);
        } else if (type.equals("D")) {
            result = buildConstant((Double) value, register);
        } else if (type.equals("Ljava/lang/String;")) {
            BuilderStringReference stringRef = dexBuilder.internStringReference(value.toString());
            result = new BuilderInstruction21c(Opcode.CONST_STRING, register, stringRef);
        } else if (type.equals("Ljava/lang/Class;")) {
            String className;
            if (value instanceof LocalClass) {
                className = ((LocalClass) value).getName();
            } else {
                Class<?> klazz = (Class<?>) value;
                className = SmaliClassUtils.javaClassToSmali(klazz);
            }
            BuilderTypeReference typeRef = dexBuilder.internTypeReference(className);
            result = new BuilderInstruction21c(Opcode.CONST_CLASS, register, typeRef);
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Unrecognized constant type: " + type + " for value: " + value + ". This will cause failures.");
            }
        }

        return result;
    }

    public static BuilderInstruction buildConstant(int address, MethodBackedGraph mbgraph) {
        DexBuilder dexBuilder = mbgraph.getDexBuilder();
        OneRegisterInstruction instruction = (OneRegisterInstruction) mbgraph.getInstruction(address);
        int register = instruction.getRegisterA();
        HeapItem item = mbgraph.getRegisterConsensus(address, register);
        BuilderInstruction result = buildConstant(item.getValue(), item.getUnboxedValueType(), register, dexBuilder);

        return result;
    }

}
