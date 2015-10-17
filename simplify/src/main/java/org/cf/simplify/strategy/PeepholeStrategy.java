package org.cf.simplify.strategy;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cf.simplify.ConstantBuilder;
import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.InvokeOp;
import org.cf.smalivm.opcode.Op;
import org.cf.util.SmaliClassUtils;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderOffsetInstruction;
import org.jf.dexlib2.builder.Label;
import org.jf.dexlib2.builder.instruction.BuilderInstruction10x;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.builder.instruction.BuilderInstruction30t;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21t;
import org.jf.dexlib2.iface.instruction.formats.Instruction22t;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeepholeStrategy implements OptimizationStrategy {

    private static final Logger log = LoggerFactory.getLogger(PeepholeStrategy.class.getSimpleName());

    private static final String ClassForNameSignature = "Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;";

    private final ExecutionGraphManipulator mbgraph;
    private int peepCount;
    private int constantIfCount;
    private TIntList addresses;
    private boolean madeChanges;

    public PeepholeStrategy(ExecutionGraphManipulator mbgraph) {
        this.mbgraph = mbgraph;
        peepCount = 0;
        constantIfCount = 0;
    }

    @Override
    public Map<String, Integer> getOptimizationCounts() {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        counts.put("peeps", peepCount);
        counts.put("constantIf", constantIfCount);

        return counts;
    }

    public boolean perform() {
        madeChanges = false;

        addresses = getValidAddresses(mbgraph);
        peepClassForName();

        addresses = getValidAddresses(mbgraph);
        peepStringInit();

        addresses = getValidAddresses(mbgraph);
        peepConstantPredicate();

        return madeChanges;
    }

    BuilderInstruction buildClassForNameReplacement(int address) {
        InvokeOp op = (InvokeOp) mbgraph.getOp(address);
        int[] parameterRegisters = op.getParameterRegisters();
        int register = parameterRegisters[0];
        String javaClassName = (String) mbgraph.getRegisterConsensusValue(address, register);
        String smaliClassName = SmaliClassUtils.javaClassToSmali(javaClassName);
        HeapItem klazz = mbgraph.getRegisterConsensus(address, MethodState.ResultRegister);
        if (klazz == null) {
            log.warn("Optimizing Class.forName of potentially non-existant class: {}", smaliClassName);
        }
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
        HeapItem className = mbgraph.getRegisterConsensus(address, registerA);
        if (className.isUnknown()) {
            return false;
        }

        return true;
    }

    TIntList getValidAddresses(ExecutionGraphManipulator mbgraph) {
        TIntList result = new TIntArrayList(mbgraph.getAddresses());
        for (int address : result.toArray()) {
            if (!mbgraph.wasAddressReached(address)) {
                result.remove(address);
            }
        }

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

    void peepConstantPredicate() {
        TIntList peepAddresses = new TIntArrayList();
        TIntSet nextAddresses = new TIntHashSet();
        for (int address : addresses.toArray()) {
            BuilderInstruction original = mbgraph.getInstruction(address);
            if (!(original instanceof Instruction22t || original instanceof Instruction21t)) {
                continue;
            }

            Set<ExecutionNode> children = new HashSet<ExecutionNode>();
            List<ExecutionNode> pile = mbgraph.getNodePile(address);
            for (ExecutionNode node : pile) {
                children.addAll(node.getChildren());
            }
            if (children.size() != 1) {
                break;
            }
            // Child is the same across all multiverses / execution paths.

            ExecutionNode child = pile.get(0).getChildren().get(0);
            boolean isNext = child.getAddress() == original.getLocation().getCodeAddress() + original.getCodeUnits();
            peepAddresses.add(address);
            if (isNext) {
                // if-* is false, so it moves to next instruction
                nextAddresses.add(address);
            }
        }

        if (0 == peepAddresses.size()) {
            return;
        }

        madeChanges = true;
        constantIfCount += peepAddresses.size();

        peepAddresses.sort();
        peepAddresses.reverse();
        for (int address : peepAddresses.toArray()) {
            BuilderInstruction replacement;
            if (nextAddresses.contains(address)) {
                replacement = new BuilderInstruction10x(Opcode.NOP);
            } else {
                BuilderOffsetInstruction original = (BuilderOffsetInstruction) mbgraph.getInstruction(address);
                Label target = original.getTarget();
                replacement = new BuilderInstruction30t(Opcode.GOTO_32, target);
            }

            if (log.isDebugEnabled()) {
                log.debug("Peeping constant predicate @{} {}", address, mbgraph.getOp(address));
            }
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
            HeapItem item = mbgraph.getRegisterConsensus(address, instanceRegister);
            if (!(item.getValue() instanceof String)) {
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
            HeapItem item = mbgraph.getRegisterConsensus(address, instanceRegister);
            BuilderInstruction replacement = ConstantBuilder.buildConstant(item.getValue(), item.getUnboxedValueType(),
                            instanceRegister, mbgraph.getDexBuilder());
            if (log.isDebugEnabled()) {
                log.debug("Peeping string init @{} {}", address, mbgraph.getOp(address));
            }
            mbgraph.replaceInstruction(address, replacement);
        }
    }

}
