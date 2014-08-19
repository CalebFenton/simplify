package simplifier.optimize;

import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction11n;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21s;
import org.jf.dexlib2.builder.instruction.BuilderInstruction31i;
import org.jf.dexlib2.builder.instruction.BuilderInstruction51l;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.writer.builder.BuilderStringReference;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplifier.Main;
import simplifier.vm.context.ContextGraph;
import simplifier.vm.context.ContextNode;
import simplifier.vm.op_handler.BinaryMathOp;
import simplifier.vm.op_handler.MoveOp;
import simplifier.vm.op_handler.Op;
import simplifier.vm.op_handler.UnaryMathOp;
import simplifier.vm.type.UnknownValue;
import util.SmaliClassUtils;

public class ConstantBuilder {

    private static final String[] ConstantValueTypes = new String[] { "I", "Z", "B", "S", "C", "J", "F", "D",
        "java.lang.String", "java.lang.Class" };

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final Class<?>[] OpHandlersToMakeConst = new Class<?>[] { BinaryMathOp.class, UnaryMathOp.class,
        MoveOp.class };

    private static BuilderInstruction buildConstant(int registerA, String type, Object value, DexBuilder dexBuilder) {
        BuilderInstruction result = null;

        if (type.equals("I") || type.equals("B") || type.equals("S") || type.equals("C")) {
            // Bytes, shorts and characters are all represented by const/4 or const/16.
            int literal = (Integer) value;
            int bitSize = getBitSize(literal);

            if (bitSize < 4) {
                result = new BuilderInstruction11n(Opcode.CONST_4, registerA, literal);
            } else if (bitSize < 16) {
                result = new BuilderInstruction21s(Opcode.CONST_16, registerA, literal);
            } else {
                result = new BuilderInstruction31i(Opcode.CONST, registerA, literal);
            }
        } else if (type.equals("Z")) {
            boolean literal = ((Boolean) value);
            result = new BuilderInstruction11n(Opcode.CONST_4, registerA, literal ? 1 : 0);
        } else if (type.equals("J")) {
            long literal = (Long) value;
            int bitSize = getBitSize(literal);

            if (bitSize < 16) {
                result = new BuilderInstruction21s(Opcode.CONST_WIDE_16, registerA, (int) literal);
            } else if (bitSize < 32) {
                result = new BuilderInstruction31i(Opcode.CONST_WIDE_32, registerA, (int) literal);
            } else {
                result = new BuilderInstruction51l(Opcode.CONST_WIDE, registerA, literal);
            }
        } else if (type.equals("F")) {
            float literal = (Float) value;
            log.warning("WOOP WOOP no idea how to const floats: " + literal);
            // TODO: implement
        } else if (type.equals("D")) {
            double literal = (Double) value;
            log.warning("WOOP WOOP no idea how to const doubles: " + literal);
            // TODO: implement
        } else if (type.equals("java.lang.String")) {
            BuilderStringReference stringRef = dexBuilder.internStringReference(value.toString());
            result = new BuilderInstruction21c(Opcode.CONST_STRING, registerA, stringRef);
        } else if (type.equals("java.lang.Class")) {
            BuilderTypeReference typeRef = dexBuilder.internTypeReference(value.toString());
            result = new BuilderInstruction21c(Opcode.CONST_CLASS, registerA, typeRef);
        }

        return result;
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

    private static boolean isConstableHandler(Op handler) {
        for (Class<?> clazz : OpHandlersToMakeConst) {
            if (handler.getClass() == clazz) {
                return true;
            }
        }

        return false;
    }

    private static boolean isConstableType(String type) {
        for (String ct : ConstantValueTypes) {
            if (type.equals(ct)) {
                return true;
            }
        }

        return false;
    }

    static BuilderInstruction buildConstantForAddress(int address, ContextGraph graph,
                    BuilderInstruction originalInstruction, DexBuilder dexBuilder) {
        List<ContextNode> nodePile = graph.getNodePile(address);
        if (nodePile.size() == 0) {
            // Node wasn't reached.
            return null;
        }

        // Check handler first since we expect to be able to cast instructions to OneRegisterInstruction
        Op handler = nodePile.get(0).getOpHandler();
        if (!isConstableHandler(handler)) {
            log.finer("Can't make hanlder constant: " + handler);
            return null;
        }

        int registerA = ((OneRegisterInstruction) originalInstruction).getRegisterA();
        Object consensus = graph.getRegisterConsensus(address, registerA);
        if (consensus instanceof UnknownValue) {
            return null;
        }

        String type = SmaliClassUtils.getValueType(consensus);
        type = getUnboxedType(type);

        if (!isConstableType(type)) {
            log.fine("Can't make type constant: " + type);
            return null;
        }

        log.fine("Build constant for r" + registerA + ", type=" + type + ", value=" + consensus + ", @" + address);
        BuilderInstruction result = buildConstant(registerA, type, consensus, dexBuilder);

        return result;
    }

}
