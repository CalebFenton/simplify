package org.cf.simplify.strategy;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.HashMap;
import java.util.Map;

import org.cf.simplify.MethodBackedGraph;
import org.cf.smalivm.opcode.InvokeOp;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;

public class PeepholeStrategy implements OptimizationStrategy {

    private static final String ClassForNameSignature = "Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;";
    private static final String MethodInvokeSignature = "Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";

    private final MethodBackedGraph mbgraph;
    private int peepCount;
    private TIntList addresses;

    public PeepholeStrategy(MethodBackedGraph mbgraph) {
        this.mbgraph = mbgraph;
        peepCount = 0;
    }

    @Override
    public Map<String, Integer> getOptimizationCounts() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        result.put("peeps", peepCount);

        return result;
    }

    void peepClassForName() {
        TIntList peepAddresses = new TIntArrayList();
        for (int address : addresses.toArray()) {
            if (canPeepClassForName(address)) {
                peepAddresses.add(address);
            }
        }

        peepCount += peepAddresses.size();
        peepAddresses.sort();
        peepAddresses.reverse();
        for (int address : peepAddresses.toArray()) {
            BuilderInstruction original = mbgraph.getInstruction(address);
            int nextAddress = address + original.getCodeUnits();
            if (addresses.contains(nextAddress)) {
                BuilderInstruction nextInstruction = mbgraph.getInstruction(nextAddress);
                if (nextInstruction.getOpcode().name.startsWith("move-result")) {
                    // There is a move-result after the instruction being replaced. "Deal" with it.
                    mbgraph.removeInstruction(nextAddress);
                }
            }

            BuilderInstruction replacement = buildClassForNameReplacement(address);
            mbgraph.replaceInstruction(address, replacement);
        }
    }

    public boolean perform() {
        addresses = getValidAddresses(mbgraph);

        peepClassForName();

        return peepCount > 0;
    }

    BuilderInstruction buildClassForNameReplacement(int address) {
        InvokeOp op = (InvokeOp) mbgraph.getOp(address);
        int[] parameterRegisters = op.getParameterRegisters();
        int register = parameterRegisters[0];
        String javaClassName = (String) mbgraph.getRegisterConsensus(address, register);
        String smaliClassName = SmaliClassUtils.javaClassToSmali(javaClassName);
        BuilderTypeReference classRef = mbgraph.getDexBuilder().internTypeReference(smaliClassName);
        BuilderInstruction constClassInstruction = new BuilderInstruction21c(Opcode.CONST_CLASS, register, classRef);

        return constClassInstruction;
    }

    boolean canPeepClassForName(int address) {
        Op handler = mbgraph.getOp(address);
        if (!(handler instanceof InvokeOp)) {
            return false;
        }

        BuilderInstruction instruction = mbgraph.getInstruction(address);
        ReferenceInstruction instr = (ReferenceInstruction) instruction;
        String ref = ReferenceUtil.getReferenceString(instr.getReference());
        if (!ref.equals(ClassForNameSignature)) {
            return false;
        }

        int[] parameterRegisters = ((InvokeOp) handler).getParameterRegisters();
        int registerA = parameterRegisters[0];
        Object classNameValue = mbgraph.getRegisterConsensus(address, registerA);
        if (classNameValue instanceof UnknownValue) {
            return false;
        }

        return true;
    }

    boolean canPeepMethodInvoke(int address) {
        Op handler = mbgraph.getOp(address);
        if (!(handler instanceof InvokeOp)) {
            return false;
        }

        BuilderInstruction instruction = mbgraph.getInstruction(address);
        ReferenceInstruction instr = (ReferenceInstruction) instruction;
        String ref = ReferenceUtil.getReferenceString(instr.getReference());
        if (!ref.equals(MethodInvokeSignature)) {
            return false;
        }

        int[] parameterRegisters = ((InvokeOp) handler).getParameterRegisters();
        int methodRegister = parameterRegisters[0];
        int targetRegister = parameterRegisters[1];
        int parametersRegister = parameterRegisters[2];

        Object value1 = mbgraph.getRegisterConsensus(address, methodRegister);
        Object value2 = mbgraph.getRegisterConsensus(address, targetRegister);
        Object value3 = mbgraph.getRegisterConsensus(address, parametersRegister);

        // check instanceof UnknownValue
        System.out.println(value1 + " " + value2 + " " + value3);

        return false;
    }

    TIntList getValidAddresses(MethodBackedGraph mbgraph) {
        TIntList result = new TIntArrayList(mbgraph.getAddresses().toArray());
        for (int address : result.toArray()) {
            if (!mbgraph.wasAddressReached(address)) {
                result.remove(address);
            }
        }

        return result;
    }

}
