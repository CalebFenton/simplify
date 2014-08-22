package simplify.optimize;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.vm.VirtualMachine;
import simplify.vm.context.ContextGraph;
import simplify.vm.op_handler.InvokeOp;
import simplify.vm.op_handler.Op;
import simplify.vm.op_handler.OpFactory;
import simplify.vm.type.UnknownValue;

public class PeepholeOptimizer {

    private static final String ClassForNameSignature = "Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;";

    private final DexBuilder dexBuilder;
    private final BuilderMethod method;
    private final String methodDescriptor;
    private final ContextGraph graph;
    private final MutableMethodImplementation implementation;
    private final TIntObjectMap<BuilderInstruction> addressToInstruction;
    private final OpFactory opFactory;

    PeepholeOptimizer(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph, VirtualMachine vm,
                    MutableMethodImplementation implementation, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    OpFactory opFactory) {
        this.dexBuilder = dexBuilder;
        this.method = method;
        methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        this.graph = graph;
        this.implementation = implementation;
        this.addressToInstruction = addressToInstruction;
        this.opFactory = opFactory;
    }

    void perform() {
        // NOTE: will need to replace address AND address + codeUnits with object in map if next instruction is
        // move-result
        getClassForNameAddressToReplacementMap();
    }

    TIntObjectMap<BuilderInstruction> getClassForNameAddressToReplacementMap() {
        TIntObjectMap<BuilderInstruction> result = new TIntObjectHashMap<BuilderInstruction>();
        for (int address : addressToInstruction.keys()) {
            Op handler = graph.getOpHandler(address);
            if (!(handler instanceof InvokeOp)) {
                continue;
            }

            BuilderInstruction instruction = addressToInstruction.get(address);
            ReferenceInstruction instr = (ReferenceInstruction) instruction;
            String ref = ReferenceUtil.getReferenceString(instr.getReference());
            if (!ref.equals(ClassForNameSignature)) {
                continue;
            }

            int[] parameterRegisters = ((InvokeOp) handler).getParameterRegisters();
            int registerA = parameterRegisters[0];
            Object classNameValue = graph.getRegisterConsensus(address, registerA);
            if (classNameValue instanceof UnknownValue) {
                continue;
            }

            String className = (String) classNameValue;
            BuilderTypeReference classRef = dexBuilder.internTypeReference(className);
            BuilderInstruction constClass = new BuilderInstruction21c(Opcode.CONST_CLASS, registerA, classRef);
            result.put(address, constClass);
        }

        return result;
    }
}
