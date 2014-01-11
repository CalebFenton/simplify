package simplify;

import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction11n;
import org.jf.dexlib2.builder.instruction.BuilderInstruction11x;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21s;
import org.jf.dexlib2.builder.instruction.BuilderInstruction31i;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.writer.builder.BuilderMethod;

import simplify.exec.ExecutionContext;
import simplify.graph.InstructionNode;

import com.google.common.collect.LinkedListMultimap;

public class MethodSimplifier {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private static final String[] OpsToMakeConst = new String[] { "move", "neg-", "not-", "int-to", "long-to",
                    "float-to", "double-to", "add-", "sub-", "mul-", "div-", "rem-", "and-", "or-", "xor-", "shl-",
                    "shr-", "ushr-", "rsub-", "return" };

    private static final String[] ConstantValueTypes = new String[] { "I", "Z", "B", "S", "C", "J", "F", "D",
                    "java.lang.String", "java.lang.Class" };

    // return true if changes made
    public static boolean simplify(BuilderMethod method, LinkedListMultimap<Integer, InstructionNode> nodes) {
        propagateConstants(method, nodes);

        removeUnused(method, nodes);

        return false;
    }

    private static void propagateConstants(BuilderMethod method, LinkedListMultimap<Integer, InstructionNode> nodes) {
        for (Integer index : nodes.keySet()) {
            List<InstructionNode> multiverse = nodes.get(index);

            InstructionNode firstNode = multiverse.get(0);
            log.finer("Propagate constants: " + firstNode);

            BuilderInstruction instruction = firstNode.getInstruction();
            String opName = instruction.getOpcode().name;
            if (opName.startsWith("const") || opName.equals("return-void")) {
                continue;
            }

            if (!opCouldMakeConstant(instruction.getOpcode())) {
                continue;
            }

            ExecutionContext ectx = firstNode.getContext();
            int registerA = ((OneRegisterInstruction) instruction).getRegisterA();
            String type = ectx.getRegisterType(registerA);
            if (!canEmitType(type)) {
                continue;
            }

            Object value = ectx.getRegisterValue(registerA);
            String valueStr = value.toString();
            boolean identical = true;
            for (InstructionNode node : multiverse) {
                Object other = node.getContext().getRegisterValue(registerA).toString();
                String otherStr = other.toString();
                if (!valueStr.equals(otherStr)) {
                    log.finer("Not all values equal, " + valueStr + " != " + otherStr);
                    identical = false;
                    break;
                }
            }

            if (identical) {
                BuilderInstruction constantInstruction = getConstantInstruction(registerA, type, value);
                MutableMethodImplementation impl = ((MutableMethodImplementation) method.getImplementation());

                log.fine("Emitting: " + constantInstruction + " @" + index);

                if (opName.startsWith("return")) {
                    impl.replaceInstruction(index, constantInstruction);
                    BuilderInstruction newReturn = new BuilderInstruction11x(instruction.getOpcode(), registerA);
                    impl.addInstruction(index + 1, newReturn);
                } else {
                    impl.replaceInstruction(index, constantInstruction);
                }
            }
        }
    }

    private static BuilderInstruction getConstantInstruction(int registerA, String type, Object value) {
        BuilderInstruction result = null;

        if (type.equals("I")) {
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
        } else if (type.equals("B")) {

        } else if (type.equals("S")) {

        } else if (type.equals("C")) {

        } else if (type.equals("J")) {

        } else if (type.equals("F")) {

        } else if (type.equals("D")) {

        } else if (type.equals("java.lang.String")) {
            // jumbo?
        } else if (type.equals("java.lang.Class")) {
            // const-class
        }

        return result;
    }

    private static int getBitSize(long x) {
        int s = 1;
        while ((s < 64) && (x >= (1L << s))) {
            s++;
        }
        return s;
    }

    private static boolean opCouldMakeConstant(Opcode opcode) {
        // probably wasteful of cycles but I do what I want
        String opName = opcode.name;
        for (String start : OpsToMakeConst) {
            if (opName.startsWith(start)) {
                return true;
            }
        }

        return false;
    }

    private static boolean canEmitType(String type) {
        for (String s : ConstantValueTypes) {
            if (type.equals(s)) {
                return true;
            }
        }

        return true;
    }

    private static void removeUnused(BuilderMethod method, LinkedListMultimap<Integer, InstructionNode> nodes) {

    }

}
