package org.cf.simplify;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ContextGraph;
import org.cf.smalivm.op_handler.InvokeOp;
import org.cf.smalivm.op_handler.Op;
import org.cf.smalivm.op_handler.OpFactory;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.jf.dexlib2.writer.builder.DexBuilder;

public class PeepholeOptimizer {

    private static final String ClassForNameSignature = "Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;";
    private static final String MethodInvokeSignature = "Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";

    private final DexBuilder dexBuilder;
    private final BuilderMethod method;
    private final String methodDescriptor;
    private final ContextGraph graph;
    private final MutableMethodImplementation implementation;
    private final TIntObjectMap<BuilderInstruction> addressToInstruction;
    private final OpFactory opFactory;
    private final Optimizer optimizer;

    PeepholeOptimizer(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph, VirtualMachine vm,
                    MutableMethodImplementation implementation, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    OpFactory opFactory, Optimizer optimizer) {
        // ALL THE PARAMETERS! ULTRA-COUPLING!
        this.dexBuilder = dexBuilder;
        this.method = method;
        methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        this.graph = graph;
        this.implementation = implementation;
        this.addressToInstruction = addressToInstruction;
        this.opFactory = opFactory;
        this.optimizer = optimizer;
    }

    int perform() {
        TIntObjectMap<BuilderInstruction> addressToReplacement = getClassForNameAddressToReplacementMap();
        TIntList keys = new TIntArrayList(addressToReplacement.keys());
        keys.sort();
        keys.reverse();
        for (int address : keys.toArray()) {
            BuilderInstruction originalInstruction = addressToInstruction.get(address);
            BuilderInstruction replacementInstruction = addressToReplacement.get(address);
            optimizer.replaceInstruction(address, originalInstruction, replacementInstruction);

            int shift = replacementInstruction.getCodeUnits() - originalInstruction.getCodeUnits();
            int oldNextAddress = (address + shift) + originalInstruction.getCodeUnits();
            if (addressToInstruction.containsKey(oldNextAddress)) {
                BuilderInstruction oldNext = addressToInstruction.get(oldNextAddress);
                if (oldNext.getOpcode().name.startsWith("move-result")) {
                    // There is a move-result after the instruction being replaced. "Deal" with it.
                    optimizer.removeInstruction(oldNextAddress);
                }
            }
        }

        return keys.size();
    }

    TIntObjectMap<BuilderInstruction> getMethodInvokeToReplacement() {
        TIntObjectMap<BuilderInstruction> result = new TIntObjectHashMap<BuilderInstruction>();
        for (int address : addressToInstruction.keys()) {
            Op handler = graph.getOpHandler(address);
            if (!(handler instanceof InvokeOp)) {
                continue;
            }

            BuilderInstruction instruction = addressToInstruction.get(address);
            ReferenceInstruction instr = (ReferenceInstruction) instruction;
            String ref = ReferenceUtil.getReferenceString(instr.getReference());
            if (!ref.equals(MethodInvokeSignature)) {
                continue;
            }

            int[] parameterRegisters = ((InvokeOp) handler).getParameterRegisters();
            int methodRegister = parameterRegisters[0];
            int targetRegister = parameterRegisters[1];
            int parametersRegister = parameterRegisters[2];

            Object value1 = graph.getRegisterConsensus(address, methodRegister);
            Object value2 = graph.getRegisterConsensus(address, targetRegister);
            Object value3 = graph.getRegisterConsensus(address, parametersRegister);

            // check instanceof UnknownValue
            System.out.println(value1 + " " + value2 + " " + value3);
        }

        return result;
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

            String className = SmaliClassUtils.javaClassToSmali((String) classNameValue);
            BuilderTypeReference classRef = dexBuilder.internTypeReference(className);
            BuilderInstruction constClassInstruction = new BuilderInstruction21c(Opcode.CONST_CLASS, registerA,
                            classRef);
            result.put(address, constClassInstruction);
        }

        return result;
    }

}
