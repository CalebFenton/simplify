package org.cf.simplify;

import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.opcode.AGetOp;
import org.cf.smalivm.opcode.BinaryMathOp;
import org.cf.smalivm.opcode.MoveOp;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.opcode.SGetOp;
import org.cf.smalivm.opcode.UnaryMathOp;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConstantBuilder implements Dependency {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ConstantBuilder.class.getSimpleName());

    private static final String LAST_16_BITS_ZERO = "0000000000000000";
    private static final String LAST_48_BITS_ZERO = "000000000000000000000000000000000000000000000000";

    // Don't include ReturnOp.class. It adds a constant which is then removed as dead, and the optimizers keep running.
    private static final Set<Class<?>> ConstantizableOps =
            new HashSet<>(Arrays.asList(BinaryMathOp.class, UnaryMathOp.class, MoveOp.class, SGetOp.class,
                    AGetOp.class));

    private static final Set<String> ConstantizableTypes =
            new HashSet<>(Arrays.asList("I", "Z", "B", "S", "C", "J", "F", "D", "Ljava/lang/String;",
                    "Ljava/lang/Class;"));

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
        if (value % 1 == 0) {
            // No decimal portion
            if (value <= Integer.MAX_VALUE) {
                return buildConstant(((Float) value).intValue(), register);
            }
        }
        int intBits = Float.floatToIntBits(value);
        String binaryValue = Integer.toBinaryString(intBits);
        BuilderInstruction constant;
        if (binaryValue.endsWith(LAST_16_BITS_ZERO)) {
            constant = new BuilderInstruction21ih(Opcode.CONST_HIGH16, register, intBits);
        } else {
            constant = new BuilderInstruction31i(Opcode.CONST, register, intBits);
        }

        return constant;
    }

    public static BuilderInstruction buildConstant(Short value, int register) {
        return buildConstant(value.intValue(), register);
    }

    public static BuilderInstruction buildConstant(Byte value, int register) {
        return buildConstant(value.intValue(), register);
    }

    public static BuilderInstruction buildConstant(int value, int register) {
        BuilderInstruction result;
        int bitCount = value == 0 ? 1 : 1 + Integer.numberOfTrailingZeros(Integer.highestOneBit(value));
        if (bitCount < 4 && (register & 0xFFFFFFF0) == 0) {
            result = new BuilderInstruction11n(Opcode.CONST_4, register, value);
        } else if (bitCount < 16 && (register & 0xFFFFFF00) == 0) {
            result = new BuilderInstruction21s(Opcode.CONST_16, register, value);
        } else {
            result = new BuilderInstruction31i(Opcode.CONST, register, value);
        }

        return result;
    }

    public static BuilderInstruction buildConstant(long value, int register) {
        BuilderInstruction result;
        int bitCount = Long.SIZE - Long.numberOfLeadingZeros(value);
        if (bitCount < 16 && (register & 0xFFFFFF00) == 0) {
            result = new BuilderInstruction21s(Opcode.CONST_WIDE_16, register, (int) value);
        } else if (bitCount < 32 && (register & 0xFFFFFF00) == 0) {
            result = new BuilderInstruction31i(Opcode.CONST_WIDE_32, register, (int) value);
        } else {
            result = new BuilderInstruction51l(Opcode.CONST_WIDE, register, value);
        }

        return result;
    }

    public static BuilderInstruction buildConstant(Object value, String type, int register, DexBuilder dexBuilder) {
        BuilderInstruction constant = null;
        switch (type) {
            case "I":
                if (value instanceof Integer) {
                    constant = buildConstant((Integer) value, register);
                } else {
                    constant = buildConstant(Utils.getIntegerValue(value), register);
                }
                break;
            case "B":
                if (value instanceof Byte) {
                    constant = buildConstant((Byte) value, register);
                } else {
                    constant = buildConstant(Utils.getIntegerValue(value), register);
                }
                break;
            case "S":
                if (value instanceof Short) {
                    constant = buildConstant((Short) value, register);
                } else {
                    constant = buildConstant(Utils.getIntegerValue(value), register);
                }
                break;
            case "C":
                if (value instanceof Character) {
                    constant = buildConstant((Character) value, register);
                } else {
                    constant = buildConstant(Utils.getIntegerValue(value), register);
                }
                break;
            case "Z":
                if (value instanceof Boolean) {
                    constant = buildConstant((Boolean) value, register);
                } else {
                    constant = buildConstant(Utils.getIntegerValue(value), register);
                }
                break;
            case "J":
                constant = buildConstant(Utils.getLongValue(value), register);
                break;
            case "F":
                constant = buildConstant(Utils.getFloatValue(value), register);
                break;
            case "D":
                // const op has no notion of actual class, just wide/narrow and bits
                // must coax correct value when needed
                constant = buildConstant(Utils.getDoubleValue(value), register);
                break;
            case "Ljava/lang/String;":
                BuilderStringReference stringRef = dexBuilder.internStringReference(value.toString());
                constant = new BuilderInstruction21c(Opcode.CONST_STRING, register, stringRef);
                break;
            case "Ljava/lang/Class;":
                Class<?> klazz = (Class<?>) value;
                String className = ClassNameUtils.toInternal(klazz);
                BuilderTypeReference typeRef = dexBuilder.internTypeReference(className);
                constant = new BuilderInstruction21c(Opcode.CONST_CLASS, register, typeRef);
                break;
            default:
                if (log.isWarnEnabled()) {
                    log.warn("Unrecognized constant class: {} for value: {}. This will cause failures.", type, value);
                }
                break;
        }

        return constant;
    }

    public static BuilderInstruction buildConstant(int address, ExecutionGraphManipulator manipulator) {
        DexBuilder dexBuilder = manipulator.getDexBuilder();
        OneRegisterInstruction instruction = (OneRegisterInstruction) manipulator.getInstruction(address);
        int register = instruction.getRegisterA();
        HeapItem item = manipulator.getRegisterConsensus(address, register);
        Object value = item.getValue();
        String type = item.isPrimitive() ? item.getType() : item.getUnboxedValueType();
        BuilderInstruction constant = buildConstant(value, type, register, dexBuilder);

        return constant;
    }

    public boolean canConstantizeOp(Op op) {
        return ConstantizableOps.contains(op.getClass());
    }

    public boolean canConstantizeType(String type) {
        return ConstantizableTypes.contains(type);
    }

    public boolean canConstantizeType(Class<?> klazz) {
        String type = ClassNameUtils.toInternal(klazz);

        return ConstantizableTypes.contains(type);
    }

}
