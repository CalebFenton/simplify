package simplify;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction11n;
import org.jf.dexlib2.builder.instruction.BuilderInstruction11x;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21s;
import org.jf.dexlib2.builder.instruction.BuilderInstruction31i;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderStringReference;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.exec.MethodExecutionContext;
import simplify.exec.UnknownValue;
import simplify.graph.InstructionNode;

import com.google.common.collect.LinkedListMultimap;

public class MethodSimplifier {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String[] OpsToMakeConst = new String[] { "move", "neg-", "not-", "int-to", "long-to",
                    "float-to", "double-to", "add-", "sub-", "mul-", "div-", "rem-", "and-", "or-", "xor-", "shl-",
                    "shr-", "ushr-", "rsub-", "return" };

    private static final String[] ConstantValueTypes = new String[] { "I", "Z", "B", "S", "C", "J", "F", "D",
                    "Ljava/lang/String;", "Ljava/lang/Class;" };

    public static boolean simplify(DexBuilder dexBuilder, BuilderMethod method,
                    LinkedListMultimap<Integer, InstructionNode> nodes) {
        boolean changed = false;

        changed |= propagateConstants(dexBuilder, method, nodes);

        changed |= removeUnused(method, nodes);

        return changed;
    }

    private static boolean propagateConstants(DexBuilder dexBuilder, BuilderMethod method,
                    LinkedListMultimap<Integer, InstructionNode> nodes) {
        boolean changed = false;

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

            MethodExecutionContext ectx = firstNode.getContext();
            int registerA = ((OneRegisterInstruction) instruction).getRegisterA();
            String type = ectx.peekRegisterType(registerA);
            if (!canEmitType(type)) {
                continue;
            }

            Object value = ectx.peekRegisterValue(registerA);
            if (value instanceof UnknownValue) {
                continue;
            }

            String valueStr = value.toString();
            boolean identical = true;
            for (InstructionNode node : multiverse) {
                Object other = node.getContext().peekRegisterValue(registerA).toString();
                String otherStr = other.toString();
                if (!valueStr.equals(otherStr)) {
                    log.finer("Not all values equal, " + valueStr + " != " + otherStr);
                    identical = false;
                    break;
                }
            }

            if (identical) {
                BuilderInstruction constantInstruction = getConstantInstruction(dexBuilder, registerA, type, value);
                MutableMethodImplementation impl = ((MutableMethodImplementation) method.getImplementation());

                log.fine("Emitting: " + constantInstruction.getOpcode() + " @" + index);

                if (opName.startsWith("return")) {
                    /*
                     * Only emit a constant before a "return" if instruction directly before it is not a constant type.
                     * This will cover some cases of an "unknown" return, but many "known" cases look like this as well.
                     * That's fine because this step will *ensure* previous instruction is a constant if it's known. So
                     * this should only happen once, at most.
                     */
                    boolean constParents = true;
                    if (index > 0) {
                        List<InstructionNode> parents = nodes.get(index - 1);
                        for (InstructionNode parent : parents) {
                            if (!parent.getInstruction().getOpcode().name.startsWith("const")) {
                                constParents = false;
                                break;
                            }
                        }
                    }

                    if (!constParents) {
                        impl.replaceInstruction(index, constantInstruction);
                        BuilderInstruction newReturn = new BuilderInstruction11x(instruction.getOpcode(), registerA);
                        impl.addInstruction(index + 1, newReturn);
                        changed = true;
                    }
                } else {
                    impl.replaceInstruction(index, constantInstruction);
                    changed = true;
                }
            }
        }

        return changed;
    }

    private static BuilderInstruction getConstantInstruction(DexBuilder dexBuilder, int registerA, String type,
                    Object value) {
        BuilderInstruction result = null;

        System.out.println("get constant for " + registerA + " + type: " + type + " value: " + value);

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

        } else if (type.equals("F")) {

        } else if (type.equals("D")) {

        } else if (type.equals("Ljava/lang/String;")) {
            BuilderStringReference stringRef = dexBuilder.internStringReference(value.toString());
            result = new BuilderInstruction21c(Opcode.CONST_STRING, registerA, stringRef);
        } else if (type.equals("Ljava/lang/Class;")) {
            BuilderTypeReference typeRef = dexBuilder.internTypeReference(value.toString());
            result = new BuilderInstruction21c(Opcode.CONST_CLASS, registerA, typeRef);
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

        return false;
    }

    private static boolean removeUnused(BuilderMethod method, LinkedListMultimap<Integer, InstructionNode> nodes) {

        List<Integer> terminalIndicies = new ArrayList<Integer>();
        for (Integer index : nodes.keySet()) {
            // Only need the first node. They all have the same opcode.
            InstructionNode node = nodes.get(index).get(0);

            Opcode op = node.getInstruction().getOpcode();
            if (!op.canContinue()) {
                terminalIndicies.add(index);
            }
        }

        for (Integer index : terminalIndicies) {
            // Reconstruct node stack "multiverse" from here, working backwards through parents.
        }

        return false;
    }

    private static void mergeContexts(MethodExecutionContext ectx1, MethodExecutionContext ectx2) {

    }

}
