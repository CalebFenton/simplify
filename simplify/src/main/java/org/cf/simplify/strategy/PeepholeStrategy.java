package org.cf.simplify.strategy;

import org.cf.simplify.ConstantBuilder;
import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.InvokeOp;
import org.cf.smalivm.opcode.MoveOp;
import org.cf.smalivm.opcode.Op;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderOffsetInstruction;
import org.jf.dexlib2.builder.Label;
import org.jf.dexlib2.builder.instruction.BuilderInstruction10x;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.builder.instruction.BuilderInstruction30t;
import org.jf.dexlib2.iface.instruction.FiveRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21t;
import org.jf.dexlib2.iface.instruction.formats.Instruction22t;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PeepholeStrategy implements OptimizationStrategy {

    private static final Logger log = LoggerFactory.getLogger(PeepholeStrategy.class.getSimpleName());

    private static final String CLASS_FOR_NAME_SIGNATURE =
            "Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;";
    private static final String OBJECT_GET_CLASS_SIGNATURE = "Ljava/lang/Object;->getClass()Ljava/lang/Class;";

    private final ExecutionGraphManipulator manipulator;
    private int peepCount;
    private int constantIfCount;
    private List<Integer> addresses;
    private boolean madeChanges;

    public PeepholeStrategy(ExecutionGraphManipulator manipulator) {
        this.manipulator = manipulator;
        peepCount = 0;
        constantIfCount = 0;
    }

    @Override
    public Map<String, Integer> getOptimizationCounts() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("peephole optmizations", peepCount);
        counts.put("constantized ifs", constantIfCount);

        return counts;
    }

    @Override
    public boolean perform() {
        madeChanges = false;

        addresses = getValidAddresses(manipulator);
        peepClassForName();

        addresses = getValidAddresses(manipulator);
        peepStringInit();

        addresses = getValidAddresses(manipulator);
        peepConstantPredicate();

        addresses = getValidAddresses(manipulator);
        peepCheckCast();

        addresses = getValidAddresses(manipulator);
        peepUninitializedInstanceThisReference();

        return madeChanges;
    }

    BuilderInstruction buildClassForNameReplacement(int address) {
        InvokeOp op = (InvokeOp) manipulator.getOp(address);
        int[] parameterRegisters = op.getParameterRegisters();
        int register = parameterRegisters[0];
        String javaClassName = (String) manipulator.getRegisterConsensusValue(address, register);
        String smaliClassName = ClassNameUtils.binaryToInternal(javaClassName);
        HeapItem klazz = manipulator.getRegisterConsensus(address, MethodState.ResultRegister);
        if (klazz == null) {
            log.warn("Optimizing Class.forName of potentially non-existent class: {}", smaliClassName);
        }
        BuilderTypeReference classRef = manipulator.getDexBuilder().internTypeReference(smaliClassName);
        BuilderInstruction constClassInstruction = new BuilderInstruction21c(Opcode.CONST_CLASS, register, classRef);

        return constClassInstruction;
    }

    BuilderInstruction buildUninitializedInstanceReferenceResultReplacement(int address) {
        InvokeOp invokeOp = (InvokeOp) manipulator.getOp(address);
        int instanceRegister = invokeOp.getParameterRegisters()[0];
        HeapItem item = manipulator.getRegisterConsensus(address, instanceRegister);

        BuilderInstruction original = manipulator.getInstruction(address);
        int nextAddress = address + original.getCodeUnits();
        MoveOp moveOp = (MoveOp) manipulator.getOp(nextAddress);
        int destRegsiter = moveOp.getToRegister();

        ReferenceInstruction instr = (ReferenceInstruction) original;
        String methodDescriptor = ReferenceUtil.getReferenceString(instr.getReference());

        BuilderInstruction replacementInstruction = null;
        if (methodDescriptor.equals(OBJECT_GET_CLASS_SIGNATURE)) {
            String smaliClassName = item.getType();
            BuilderTypeReference classRef = manipulator.getDexBuilder().internTypeReference(smaliClassName);
            replacementInstruction = new BuilderInstruction21c(Opcode.CONST_CLASS, destRegsiter, classRef);
        }

        return replacementInstruction;
    }

    boolean canPeepCheckCast(int address) {
        Op op = manipulator.getOp(address);
        if (!op.toString().startsWith("check-cast")) {
            return false;
        }

        BuilderInstruction21c original = (BuilderInstruction21c) manipulator.getInstruction(address);
        int registerA = original.getRegisterA();

        // Heap item at address would have been recast. Need to examine parents.
        // Also, don't care about values. Just collecting types.
        Set<String> ancestorTypes = new HashSet<>();
        for (int parentAddress : manipulator.getParentAddresses(address)) {
            ancestorTypes.addAll(manipulator.getRegisterItems(parentAddress, registerA).stream().map(HeapItem::getType)
                    .collect(Collectors.toList()));
        }

        if (ancestorTypes.size() > 1) {
            // More than one ancestor. At least one item was cast.
            return false;
        }

        String preCastType;
        if (ancestorTypes.size() > 0) {
            preCastType = ancestorTypes.toArray(new String[1])[0];
        } else {
            // check-cast is first op with no parents
            // this implies it's acting on a parameter register
            // look at freshly spawned execution context class
            ExecutionContext context = manipulator.getVM().spawnRootContext(manipulator.getMethod());
            HeapItem item = context.getMethodState().peekRegister(registerA);
            preCastType = item.getType();
        }

        String referenceType = ReferenceUtil.getReferenceString(original.getReference());
        if (!preCastType.equals(referenceType)) {
            // Item was cast to new class
            return false;
        }

        return true;
    }

    boolean canPeepClassForName(int address) {
        Op op = manipulator.getOp(address);
        if (!(op instanceof InvokeOp)) {
            return false;
        }

        BuilderInstruction instruction = manipulator.getInstruction(address);
        ReferenceInstruction instr = (ReferenceInstruction) instruction;
        String methodDescriptor = ReferenceUtil.getReferenceString(instr.getReference());
        if (!methodDescriptor.equals(CLASS_FOR_NAME_SIGNATURE)) {
            return false;
        }

        int[] parameterRegisters = ((InvokeOp) op).getParameterRegisters();
        int registerA = parameterRegisters[0];
        HeapItem className = manipulator.getRegisterConsensus(address, registerA);
        if (className.isUnknown()) {
            return false;
        }

        return true;
    }

    boolean canPeepStringInit(int address) {
        BuilderInstruction original = manipulator.getInstruction(address);
        if (original.getOpcode() != Opcode.INVOKE_DIRECT) {
            return false;
        }

        Instruction35c instr = (Instruction35c) original;
        MethodReference methodReference = (MethodReference) instr.getReference();
        String methodDescriptor = ReferenceUtil.getMethodDescriptor(methodReference);
        if (!methodDescriptor.startsWith("Ljava/lang/String;-><init>(")) {
            return false;
        }

        int instanceRegister = instr.getRegisterC();
        HeapItem item = manipulator.getRegisterConsensus(address, instanceRegister);
        if (!(item.getValue() instanceof String)) {
            return false;
        }

        return true;
    }

    boolean canPeepUninitializedInstanceThisReference(int address) {
        Op op = manipulator.getOp(address);
        if (!op.getName().startsWith("invoke-virtual")) {
            return false;
        }

        InvokeOp invokeOp = (InvokeOp) op;
        int instanceRegister = invokeOp.getParameterRegisters()[0];
        HeapItem item = manipulator.getRegisterConsensus(address, instanceRegister);
        if (!item.getType().equals(manipulator.getMethod().getClassName())) {
            // It's a "this" reference. It'll probably be unknown after invoking if it wasn't handled by InvokeOp.
            return false;
        }

        String methodSignature = ((InvokeOp) op).getMethod().getSignature();

        // May eventually add a few other methods here
        if (!methodSignature.equals(OBJECT_GET_CLASS_SIGNATURE)) {
            return false;
        }

        BuilderInstruction original = manipulator.getInstruction(address);
        int nextAddress = address + original.getCodeUnits();
        if (!addresses.contains(nextAddress)) {
            return false;
        }

        BuilderInstruction nextInstruction = manipulator.getInstruction(nextAddress);
        if (!nextInstruction.getOpcode().name.startsWith("move-result")) {
            return false;
        }

        return true;
    }

    List<Integer> getValidAddresses(ExecutionGraphManipulator manipulator) {
        return IntStream.of(manipulator.getAddresses()).boxed().filter(manipulator::wasAddressReached)
                .collect(Collectors.toList());
    }

    void peepCheckCast() {
        List<Integer> peepAddresses = addresses.stream().filter(this::canPeepCheckCast).collect(Collectors.toList());
        if (0 == peepAddresses.size()) {
            return;
        }

        madeChanges = true;
        peepCount += peepAddresses.size();

        Collections.sort(peepAddresses, Collections.reverseOrder());
        for (int address : peepAddresses) {
            log.debug("Removing useless check-cast @{} {}", address, manipulator.getOp(address));
            manipulator.removeInstruction(address);
        }
    }

    void peepClassForName() {
        List<Integer> peepAddresses =
                addresses.stream().filter(this::canPeepClassForName).collect(Collectors.toList());
        if (0 == peepAddresses.size()) {
            return;
        }

        madeChanges = true;
        peepCount += peepAddresses.size();

        Collections.sort(peepAddresses, Collections.reverseOrder());
        for (int address : peepAddresses) {
            BuilderInstruction original = manipulator.getInstruction(address);
            int nextAddress = address + original.getCodeUnits();
            if (addresses.contains(nextAddress)) {
                BuilderInstruction nextInstruction = manipulator.getInstruction(nextAddress);
                if (nextInstruction.getOpcode().name.startsWith("move-result")) {
                    // There is a move-result after the instruction being replaced. "Deal" with it.
                    manipulator.removeInstruction(nextAddress);
                }
            }

            BuilderInstruction replacement = buildClassForNameReplacement(address);
            manipulator.replaceInstruction(address, replacement);
        }
    }

    void peepUninitializedInstanceThisReference() {
        List<Integer> peepAddresses =
                addresses.stream().filter(this::canPeepUninitializedInstanceThisReference).collect(Collectors.toList());
        if (0 == peepAddresses.size()) {
            return;
        }

        madeChanges = true;
        peepCount += peepAddresses.size();

        Collections.sort(peepAddresses, Collections.reverseOrder());
        for (int address : peepAddresses) {
            BuilderInstruction replacement = buildUninitializedInstanceReferenceResultReplacement(address);
            manipulator.replaceInstruction(address, replacement);
            int nextAddress = address + replacement.getCodeUnits();
            manipulator.removeInstruction(nextAddress);
        }
    }

    void peepConstantPredicate() {
        List<Integer> peepAddresses = new LinkedList<>();
        Set<Integer> nextAddresses = new HashSet<>();
        for (int address : addresses) {
            BuilderInstruction original = manipulator.getInstruction(address);
            if (!(original instanceof Instruction22t || original instanceof Instruction21t)) {
                continue;
            }

            Set<ExecutionNode> children = new HashSet<>();
            List<ExecutionNode> pile = manipulator.getNodePile(address);
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

        Collections.sort(peepAddresses, Collections.reverseOrder());
        for (int address : peepAddresses) {
            BuilderInstruction replacement;
            if (nextAddresses.contains(address)) {
                replacement = new BuilderInstruction10x(Opcode.NOP);
            } else {
                BuilderOffsetInstruction original = (BuilderOffsetInstruction) manipulator.getInstruction(address);
                Label target = original.getTarget();
                replacement = new BuilderInstruction30t(Opcode.GOTO_32, target);
            }

            if (log.isDebugEnabled()) {
                log.debug("Constantizing if @{} {}", address, manipulator.getOp(address));
            }
            manipulator.replaceInstruction(address, replacement);
        }
    }

    void peepStringInit() {
        List<Integer> peepAddresses = addresses.stream().filter(this::canPeepStringInit).collect(Collectors.toList());
        if (0 == peepAddresses.size()) {
            return;
        }

        madeChanges = true;
        peepCount += peepAddresses.size();

        Collections.sort(peepAddresses, Collections.reverseOrder());
        for (int address : peepAddresses) {
            BuilderInstruction original = manipulator.getInstruction(address);
            Instruction35c instr = (Instruction35c) original;
            int instanceRegister = instr.getRegisterC();
            HeapItem item = manipulator.getRegisterConsensus(address, instanceRegister);
            BuilderInstruction replacement = ConstantBuilder.buildConstant(item.getValue(), item.getUnboxedType(),
                    instanceRegister, manipulator.getDexBuilder());
            if (log.isDebugEnabled()) {
                log.debug("Peeping string init @{} {}", address, manipulator.getOp(address));
            }
            manipulator.replaceInstruction(address, replacement);
        }
    }

}
