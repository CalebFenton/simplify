package org.cf.simplify.strategy;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.cf.simplify.MethodBackedGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
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

    void peepMethodInvoke() {
        TIntList peepAddresses = new TIntArrayList();
        for (int address : addresses.toArray()) {
            if (canPeepMethodInvoke(address)) {
                peepAddresses.add(address);
            }
        }

        peepCount += peepAddresses.size();
        peepAddresses.sort();
        peepAddresses.reverse();
        for (int address : peepAddresses.toArray()) {
            BuilderInstruction replacement = buildMethodInvokeReplacement(address);
            mbgraph.replaceInstruction(address, replacement);
        }
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

        peepCount += peepAddresses.size();
        peepAddresses.sort();
        peepAddresses.reverse();
        for (int address : peepAddresses.toArray()) {
            BuilderInstruction original = mbgraph.getInstruction(address);
            Instruction35c instr = (Instruction35c) original;
            int instanceRegister = instr.getRegisterC();
            Object value = mbgraph.getRegisterConsensus(address, instanceRegister);
            BuilderInstruction replacement = ConstantPropigationStrategy.buildConstant(value, instanceRegister,
                            mbgraph.getDexBuilder());
            if (log.isDebugEnabled()) {
                log.debug("Peeping string init @" + address + " " + mbgraph.getOp(address));
            }
            mbgraph.replaceInstruction(address, replacement);
        }
    }

    public boolean perform() {
        addresses = getValidAddresses(mbgraph);
        peepClassForName();

        addresses = getValidAddresses(mbgraph);
        peepMethodInvoke();

        addresses = getValidAddresses(mbgraph);
        peepStringInit();

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

    BuilderInstruction buildMethodInvokeReplacement(int address) {
        Op op = mbgraph.getOp(address);
        TIntSet parentAddressSet = new TIntHashSet();
        for (ExecutionNode node : mbgraph.getNodePile(address)) {
            parentAddressSet.add(node.getParent().getAddress());
        }
        TIntList parentAddresses = new TIntArrayList(parentAddressSet);
        int[] parameterRegisters = ((InvokeOp) op).getParameterRegisters();
        int methodRegister = parameterRegisters[0];
        int targetRegister = parameterRegisters[1];
        int parametersRegister = parameterRegisters[2];

        Object methodValue = mbgraph.getRegisterConsensus(parentAddresses, methodRegister);
        Object targetValue = mbgraph.getRegisterConsensus(parentAddresses, targetRegister);
        Object parametersValue = mbgraph.getRegisterConsensus(parentAddresses, parametersRegister);

        TIntList availableRegisters = getAvailableRegisters(address);
        // need 0-?? available registers, then pull out the parameters with a-get into them
        // then build a proper invoke
        // String javaClassName = (String) mbgraph.getRegisterConsensus(address, register);
        // String smaliClassName = SmaliClassUtils.javaClassToSmali(javaClassName);
        // BuilderTypeReference classRef = mbgraph.getDexBuilder().internTypeReference(smaliClassName);
        // BuilderInstruction methodInvokeInstruction = new BuilderInstruction21c(Opcode.CONST_CLASS, register,
        // classRef);

        return null;
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

    boolean canPeepMethodInvoke(int address) {
        Op op = mbgraph.getOp(address);
        if (!(op instanceof InvokeOp)) {
            return false;
        }

        BuilderInstruction instruction = mbgraph.getInstruction(address);
        ReferenceInstruction instr = (ReferenceInstruction) instruction;
        String methodSignature = ReferenceUtil.getReferenceString(instr.getReference());
        if (!methodSignature.equals(MethodInvokeSignature)) {
            return false;
        }

        int[] parameterRegisters = ((InvokeOp) op).getParameterRegisters();
        int methodRegister = parameterRegisters[0];
        Object methodValue = mbgraph.getRegisterConsensus(address, methodRegister);
        if (methodValue instanceof UnknownValue) {
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

    private TIntList getAvailableRegisters(int address) {
        Deque<ExecutionNode> stack = new ArrayDeque<ExecutionNode>(mbgraph.getChildrenAtAddress(address));
        ExecutionNode node = stack.getFirst();
        if (null == node) {
            // Only return should not have children.
            MethodState mState = mbgraph.getNodePile(address).get(0).getContext().getMethodState();
            TIntList available = new TIntLinkedList();
            // They're all available!
            for (int i = 0; i < mState.getRegisterCount(); i++) {
                available.add(i);
            }

            return available;
        }

        int[] registers = new int[node.getContext().getMethodState().getRegisterCount()];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = i;
        }
        TIntSet unavailable = new TIntHashSet();
        TIntList available = new TIntLinkedList();
        while ((node = stack.poll()) != null) {
            MethodState mState = node.getContext().getMethodState();
            for (int register : registers) {
                if (unavailable.contains(register) || available.contains(register)) {
                    continue;
                }

                if (mState.wasRegisterRead(register)) {
                    unavailable.add(register);
                } else if (mState.wasRegisterAssigned(register)) {
                    available.add(register);
                }
            }
            stack.addAll(node.getChildren());
        }

        return available;
    }
}
