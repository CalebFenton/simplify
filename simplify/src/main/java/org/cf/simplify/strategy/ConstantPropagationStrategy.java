package org.cf.simplify.strategy;

import org.cf.simplify.ConstantBuilder;
import org.cf.simplify.Dependency;
import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.opcode.Op;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConstantPropagationStrategy implements OptimizationStrategy {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ConstantPropagationStrategy.class.getSimpleName());

    private final ExecutionGraphManipulator manipulator;
    protected ConstantBuilder constantBuilder;
    private int constantCount;
    private boolean madeChanges;

    public ConstantPropagationStrategy(ExecutionGraphManipulator manipulator) {
        getDependencies();
        this.manipulator = manipulator;
        constantCount = 0;
    }

    @Override
    public Map<String, Integer> getOptimizationCounts() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("constantized ops", constantCount);

        return counts;
    }

    @Override
    public boolean perform() {
        madeChanges = false;

        List<Integer> addresses = getValidAddresses();
        Collections.sort(addresses, Collections.reverseOrder());
        for (int address : addresses) {
            madeChanges = true;
            BuilderInstruction original = manipulator.getInstruction(address);
            BuilderInstruction constInstruction = ConstantBuilder.buildConstant(address, manipulator);
            boolean isReturn = original.getOpcode().name().startsWith("RETURN");
            if (isReturn) {
                manipulator.addInstruction(address, constInstruction);
            } else {
                manipulator.replaceInstruction(address, constInstruction);
            }
            constantCount++;
        }

        return madeChanges;
    }

    protected void getDependencies() {
        if (constantBuilder == null) {
            constantBuilder = new ConstantBuilder();
        }
    }

    protected void setDependencies(Dependency dependency) {
        constantBuilder = (ConstantBuilder) dependency;
    }

    private boolean canConstantizeAddress(int address) {
        if (!manipulator.wasAddressReached(address)) {
            return false;
        }

        Op op = manipulator.getOp(address);
        if (!constantBuilder.canConstantizeOp(op)) {
            return false;
        }

        OneRegisterInstruction instruction = (OneRegisterInstruction) manipulator.getInstruction(address);
        if (instruction == null) {
            return false;
        }
        int register = instruction.getRegisterA();
        HeapItem consensus = manipulator.getRegisterConsensus(address, register);
        // Consensus may be null if we have correct syntax without legitimate values (fake code)
        if (consensus == null || consensus.isUnknown()) {
            return false;
        }

        String type = consensus.isPrimitive() ? consensus.getType() : consensus.getValueType();
        if (!constantBuilder.canConstantizeType(type)) {
            return false;
        }

        return true;
    }

    private List<Integer> getValidAddresses() {
        return IntStream.of(manipulator.getAddresses()).boxed().filter(this::canConstantizeAddress)
                       .collect(Collectors.toList());
    }

}
