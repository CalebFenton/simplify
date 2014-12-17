package org.cf.simplify.strategy;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.HashMap;
import java.util.Map;

import org.cf.simplify.ConstantBuilder;
import org.cf.simplify.MethodBackedGraph;
import org.cf.smalivm.opcode.InvokeOp;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeepholeStrategy implements OptimizationStrategy {

    private static final Logger log = LoggerFactory.getLogger(PeepholeStrategy.class.getSimpleName());

    private static final String ClassForNameSignature = "Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;";

    private final MethodBackedGraph mbgraph;
    private int peepCount;
    private TIntList addresses;
    private boolean madeChanges;

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

        if (0 == peepAddresses.size()) {
            return;
        }

        madeChanges = true;
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

    void peepStringInit() {
        TIntList peepAddresses = new TIntArrayList();
        for (int address : addresses.toArray()) {
            BuilderInstruction original = mbgraph.getInstruction(address);
            if (!(original instanceof Instruction35c)) {
                // Not an invoke direct
                continue;
            }

            Instruction35c instr = (Instruction35c) original;
            MethodReference methodReference = (MethodReference) instr.getReference();
            String methodDescriptor = ReferenceUtil.getMethodDescriptor(methodReference);
            if (!methodDescriptor.startsWith("Ljava/lang/String;-><init>(")) {
                continue;
            }

            int instanceRegister = instr.getRegisterC();
            Object value = mbgraph.getRegisterConsensus(address, instanceRegister);
            if (!(value instanceof String)) {
                // Not UnknownValue
                continue;
            }
            peepAddresses.add(address);
        }

        if (0 == peepAddresses.size()) {
            return;
        }

        madeChanges = true;
        peepCount += peepAddresses.size();

        peepAddresses.sort();
        peepAddresses.reverse();
        for (int address : peepAddresses.toArray()) {
            BuilderInstruction original = mbgraph.getInstruction(address);
            Instruction35c instr = (Instruction35c) original;
            int instanceRegister = instr.getRegisterC();
            Object value = mbgraph.getRegisterConsensus(address, instanceRegister);
            BuilderInstruction replacement = ConstantBuilder.buildConstant(value, instanceRegister,
                            mbgraph.getDexBuilder());
            if (log.isDebugEnabled()) {
                log.debug("Peeping string init @" + address + " " + mbgraph.getOp(address));
            }
            mbgraph.replaceInstruction(address, replacement);
        }
    }

    public boolean perform() {
        madeChanges = false;

        addresses = getValidAddresses(mbgraph);
        peepClassForName();

        addresses = getValidAddresses(mbgraph);
        peepStringInit();

        return madeChanges;
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
        Op op = mbgraph.getOp(address);
        if (!(op instanceof InvokeOp)) {
            return false;
        }

        BuilderInstruction instruction = mbgraph.getInstruction(address);
        ReferenceInstruction instr = (ReferenceInstruction) instruction;
        String methodDescriptor = ReferenceUtil.getReferenceString(instr.getReference());
        if (!methodDescriptor.equals(ClassForNameSignature)) {
            return false;
        }

        int[] parameterRegisters = ((InvokeOp) op).getParameterRegisters();
        int registerA = parameterRegisters[0];
        Object classNameValue = mbgraph.getRegisterConsensus(address, registerA);
        if (classNameValue instanceof UnknownValue) {
            return false;
        }

        return true;
    }

    TIntList getValidAddresses(MethodBackedGraph mbgraph) {
        TIntList result = new TIntArrayList(mbgraph.getAddresses());
        for (int address : result.toArray()) {
            if (!mbgraph.wasAddressReached(address)) {
                result.remove(address);
            }
        }

        return result;
    }

}
