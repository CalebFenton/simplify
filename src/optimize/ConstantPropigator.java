package optimize;

import gnu.trove.list.TIntList;

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
import org.jf.dexlib2.builder.instruction.BuilderInstruction51l;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderStringReference;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.Main;
import simplify.handlers.BinaryMathOpHandler;
import simplify.handlers.MoveOpHandler;
import simplify.handlers.OpHandler;
import simplify.handlers.ReturnOpHandler;
import simplify.handlers.UnaryMathOpHandler;
import simplify.vm.ContextGraph;
import simplify.vm.ContextNode;
import simplify.vm.RegisterStore;
import simplify.vm.UnknownValue;
import util.SparseArray;

public class ConstantPropigator {

    private static final String[] ConstantValueTypes = new String[] { "I", "Z", "B", "S", "C", "J", "F", "D",
                    "Ljava/lang/String;", "Ljava/lang/Class;" };

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final Class<?>[] OpHandlersToMakeConst = new Class<?>[] { BinaryMathOpHandler.class,
                    UnaryMathOpHandler.class, ReturnOpHandler.class, MoveOpHandler.class };

    private static int getBitSize(long x) {
        int result = 1;
        while ((result < 64) && (x >= (1L << result))) {
            result++;
        }

        return result;
    }

    private static boolean isConstableHandler(OpHandler handler) {
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

    private final SparseArray<BuilderInstruction> addressToInstruction;
    private final DexBuilder dexBuilder;
    private int emitCount = 0;
    private final ContextGraph graph;
    private final MutableMethodImplementation implementation;

    private int peepCount = 0;

    ConstantPropigator(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph) {
        this.dexBuilder = dexBuilder;
        this.graph = graph;

        MutableMethodImplementation impl = (MutableMethodImplementation) method.getImplementation();
        implementation = impl;

        addressToInstruction = Simplifier.buildAddressToInstruction(impl.getInstructions());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("constants=").append(emitCount).append(", peeps=").append(peepCount);

        return sb.toString();
    }

    private BuilderInstruction buildConstant(int registerA, String type, Object value) {
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
        } else if (type.equals("Ljava/lang/String;")) {
            BuilderStringReference stringRef = dexBuilder.internStringReference(value.toString());
            result = new BuilderInstruction21c(Opcode.CONST_STRING, registerA, stringRef);
        } else if (type.equals("Ljava/lang/Class;")) {
            BuilderTypeReference typeRef = dexBuilder.internTypeReference(value.toString());
            result = new BuilderInstruction21c(Opcode.CONST_CLASS, registerA, typeRef);
        }

        return result;
    }

    boolean perform() {
        boolean madeChanges = false;

        TIntList addresses = graph.getAddresses();
        for (int i = 0; i < addresses.size(); i++) {
            int address = addresses.get(i);

            List<ContextNode> nodePile = graph.getNodePile(address);
            if (nodePile.size() <= 0) {
                // Node wasn't reached.
                log.finest("Address wasn't reached: " + address);
                continue;
            }

            // Check handler first since we expect to be able to cast instructions to OneRegisterInstruction
            OpHandler handler = nodePile.get(0).getHandler();
            if (!isConstableHandler(handler)) {
                log.finer("Can't make hanlder constant: " + handler);
                continue;
            }

            BuilderInstruction originalInstruction = addressToInstruction.get(address);
            int registerA = ((OneRegisterInstruction) originalInstruction).getRegisterA();
            RegisterStore registerStore = graph.getRegisterConsensus(address, registerA);
            String type = registerStore.getType();
            if (!isConstableType(type)) {
                log.finer("Can't make type constant: " + type);
                continue;
            }

            Object value = registerStore.getValue();
            if (value instanceof UnknownValue) {
                log.finer("Can't make unknown value constant.");
                continue;
            }

            log.fine("Build constant for r" + registerA + ", type=" + type + ", value=" + value);
            BuilderInstruction constInstruction = buildConstant(registerA, type, value);

            int index = originalInstruction.getLocation().getIndex();
            if (originalInstruction.getOpcode().name.startsWith("return")) {
                // Add const before return only if previous instruction is not a const. Otherwise repeated sweeps will
                // always add one.
                BuilderInstruction prevInstr = implementation.getInstructions().get(index - 1);
                boolean previousConst = prevInstr.getOpcode().name.startsWith("const");
                if (previousConst) {
                    continue;
                }

                // Replace the return to retain labels
                implementation.replaceInstruction(index, constInstruction);

                BuilderInstruction newReturn = new BuilderInstruction11x(originalInstruction.getOpcode(), registerA);
                implementation.addInstruction(index + 1, newReturn);
                madeChanges = true;
            } else {
                implementation.replaceInstruction(index, constInstruction);
                madeChanges = true;
            }

            emitCount++;
        }

        return madeChanges;
    }

}
