package org.cf.simplify.strategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cf.simplify.MethodBackedGraph;
import org.cf.smalivm.op_handler.BinaryMathOp;
import org.cf.smalivm.op_handler.MoveOp;
import org.cf.smalivm.op_handler.Op;
import org.cf.smalivm.op_handler.ReturnOp;
import org.cf.smalivm.op_handler.UnaryMathOp;
import org.cf.smalivm.type.TypeUtil;
import org.cf.smalivm.type.UnknownValue;
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

public class ConstantPropigationStrategy implements OptimizationStrategy {

    private static final Logger log = LoggerFactory.getLogger(ConstantPropigationStrategy.class.getSimpleName());

    private static final Set<Class<?>> ConstantizableOps = new HashSet<Class<?>>(Arrays.asList(BinaryMathOp.class,
                    UnaryMathOp.class, MoveOp.class, ReturnOp.class));

    private static final Set<String> ConstantizableTypes = new HashSet<String>(Arrays.asList("I", "Z", "B", "S", "C",
                    "J", "F", "D", "java.lang.String", "java.lang.Class"));

    private static final String LAST_16_BITS_ZERO = "0000000000000000";
    private static final String LAST_48_BITS_ZERO = "000000000000000000000000000000000000000000000000";

    private final MethodBackedGraph mbgraph;
    private int constantCount;

    public ConstantPropigationStrategy(MethodBackedGraph mbgraph) {
        this.mbgraph = mbgraph;
        constantCount = 0;
    }

    public BuilderInstruction buildConstant(int address) {
        DexBuilder dexBuilder = mbgraph.getDexBuilder();
        OneRegisterInstruction instruction = (OneRegisterInstruction) mbgraph.getInstruction(address);
        int register = instruction.getRegisterA();
        Object value = mbgraph.getRegisterConsensus(address, register);
        BuilderInstruction result = buildConstant(value, register, dexBuilder);

        return result;
    }

    private static boolean canConstantizeOp(Op op) {
        return ConstantizableOps.contains(op.getClass());
    }

    private static boolean canConstantizeType(String type) {
        return ConstantizableTypes.contains(type);
    }

    private static int getBitSize(long x) {
        int result = 1;
        while ((result < 64) && (x >= (1L << result))) {
            result++;
        }

        return result;
    }

    private static String getUnboxedType(String type) {
        String result = null;
        if (type.equals("java.lang.Integer")) {
            result = "I";
        } else if (type.equals("java.lang.Byte")) {
            result = "B";
        } else if (type.equals("java.lang.Boolean")) {
            result = "Z";
        } else if (type.equals("java.lang.Long")) {
            result = "J";
        } else if (type.equals("java.lang.Character")) {
            result = "C";
        } else if (type.equals("java.lang.Float")) {
            result = "F";
        } else if (type.equals("java.lang.Double")) {
            result = "D";
        } else {
            result = type;
        }

        return result;
    }

    static BuilderInstruction buildConstant(boolean value, int register) {
        int literal = value ? 1 : 0;

        return buildConstant(literal, register);
    }

    static BuilderInstruction buildConstant(double value, int register) {
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

    static BuilderInstruction buildConstant(float value, int register) {
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

    static BuilderInstruction buildConstant(int value, int register) {
        BuilderInstruction result;
        int bitSize = getBitSize(value);
        if (bitSize < 4) {
            result = new BuilderInstruction11n(Opcode.CONST_4, register, value);
        } else if (bitSize < 16) {
            result = new BuilderInstruction21s(Opcode.CONST_16, register, value);
        } else {
            result = new BuilderInstruction31i(Opcode.CONST, register, value);
        }

        return result;
    }

    static BuilderInstruction buildConstant(long value, int register) {
        BuilderInstruction result;
        int bitSize = getBitSize(value);
        if (bitSize < 16) {
            result = new BuilderInstruction21s(Opcode.CONST_WIDE_16, register, (int) value);
        } else if (bitSize < 32) {
            result = new BuilderInstruction31i(Opcode.CONST_WIDE_32, register, (int) value);
        } else {
            result = new BuilderInstruction51l(Opcode.CONST_WIDE, register, value);
        }

        return result;
    }

    static BuilderInstruction buildConstant(Object value, int register, DexBuilder dexBuilder) {
        String type = getUnboxedType(TypeUtil.getValueType(value));
        BuilderInstruction result = null;
        if (type.equals("I") || type.equals("B") || type.equals("S") || type.equals("C")) {
            result = buildConstant((Integer) value, register);
        } else if (type.equals("Z")) {
            result = buildConstant(((Boolean) value), register);
        } else if (type.equals("J")) {
            result = buildConstant((Long) value, register);
        } else if (type.equals("F")) {
            result = buildConstant((Float) value, register);
        } else if (type.equals("D")) {
            result = buildConstant((Double) value, register);
        } else if (type.equals("java.lang.String")) {
            BuilderStringReference stringRef = dexBuilder.internStringReference(value.toString());
            result = new BuilderInstruction21c(Opcode.CONST_STRING, register, stringRef);
        } else if (type.equals("java.lang.Class")) {
            BuilderTypeReference typeRef = dexBuilder.internTypeReference(value.toString());

            result = new BuilderInstruction21c(Opcode.CONST_CLASS, register, typeRef);
        }

        return result;
    }

    boolean canConstantizeAddress(int address) {
        if (!mbgraph.wasAddressReached(address)) {
            return false;
        }

        Op op = mbgraph.getOpHandler(address);
        if (!canConstantizeOp(op)) {
            return false;
        }

        OneRegisterInstruction instruction = (OneRegisterInstruction) mbgraph.getInstruction(address);
        int register = instruction.getRegisterA();
        Object consensus = mbgraph.getRegisterConsensus(address, register);
        if (consensus instanceof UnknownValue) {
            return false;
        }

        String unboxedValueType = getUnboxedType(TypeUtil.getValueType(consensus));
        if (!canConstantizeType(unboxedValueType)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean perform() {
        for (int address : mbgraph.getAddresses().toArray()) {
            BuilderInstruction original = mbgraph.getInstruction(address);
            if (canConstantizeAddress(address)) {
                BuilderInstruction constInstruction = buildConstant(address);
                if (original.getOpcode().name().startsWith("RETURN")) {
                    // TODO: insert const rather than replace return op
                    log.warn("Return op constantizing not impelemented.");
                } else {
                    mbgraph.replaceInstruction(address, constInstruction);
                }
                constantCount++;
            }
        }

        return constantCount > 0;
    }

    @Override
    public Map<String, Integer> getOptimizationCounts() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        result.put("constants", constantCount);

        return result;
    }

}
