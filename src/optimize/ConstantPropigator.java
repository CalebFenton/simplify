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
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderStringReference;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.util.SparseArray;

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

public class ConstantPropigator {

    private static final String[] ConstantValueTypes = new String[] { "I", "Z", "B", "S", "C", "J", "F", "D",
                    "Ljava/lang/String;", "Ljava/lang/Class;" };

    private static Logger log = Logger.getLogger(Main.class.getSimpleName());

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

    private final BuilderMethod method;

    private int peepCount = 0;

    ConstantPropigator(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph) {
        this.dexBuilder = dexBuilder;
        this.method = method;
        this.graph = graph;

        MutableMethodImplementation impl = (MutableMethodImplementation) method.getImplementation();
        implementation = impl;

        addressToInstruction = buildAddressToInstruction(impl.getInstructions());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("constants=").append(emitCount).append(", peeps=").append(peepCount);

        return sb.toString();
    }

    private SparseArray<BuilderInstruction> buildAddressToInstruction(List<BuilderInstruction> instructions) {
        SparseArray<BuilderInstruction> result = new SparseArray<BuilderInstruction>(instructions.size());

        for (BuilderInstruction instruction : instructions) {
            int address = instruction.getLocation().getCodeAddress();
            result.put(address, instruction);
        }

        return result;
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
            // TODO: implement
        } else if (type.equals("F")) {
            // TODO: implement
        } else if (type.equals("D")) {
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
                log.fine("Can't make hanlder constant: " + handler);
                continue;
            }

            BuilderInstruction originalInstruction = addressToInstruction.get(address);
            int registerA = ((OneRegisterInstruction) originalInstruction).getRegisterA();
            RegisterStore registerStore = graph.getConsensus(address, registerA);
            String type = registerStore.getType();
            if (!isConstableType(type)) {
                log.fine("Can't make type constant: " + type);
                continue;
            }

            Object value = registerStore.getValue();
            if (value instanceof UnknownValue) {
                log.fine("Can't make unknown value constant.");
                continue;
            }

            log.info("Build constant for r" + registerA + ", type=" + type + ", value=" + value);
            BuilderInstruction constInstruction = buildConstant(registerA, type, value);

            int index = originalInstruction.getLocation().getIndex();
            if (originalInstruction.getOpcode().name.startsWith("return")) {
                // Add consts directly preceding "returns"s to make the Smali even more readable
                // Replace the return to retain labels
                implementation.replaceInstruction(index, constInstruction);

                BuilderInstruction newReturn = new BuilderInstruction11x(originalInstruction.getOpcode(), registerA);
                implementation.addInstruction(index + 1, newReturn);
            } else {
                implementation.replaceInstruction(index, constInstruction);
            }

            emitCount++;
        }

        return madeChanges;
    }

}
