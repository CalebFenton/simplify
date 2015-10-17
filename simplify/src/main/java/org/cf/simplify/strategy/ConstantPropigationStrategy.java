package org.cf.simplify.strategy;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;

import java.util.HashMap;
import java.util.Map;

import org.cf.simplify.ConstantBuilder;
import org.cf.simplify.Dependancy;
import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.opcode.Op;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantPropigationStrategy implements OptimizationStrategy {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ConstantPropigationStrategy.class.getSimpleName());

    private final ExecutionGraphManipulator mbgraph;
    private int constantCount;
    private boolean madeChanges;

    protected ConstantBuilder constantBuilder;

    public ConstantPropigationStrategy(ExecutionGraphManipulator mbgraph) {
        getDependancies();
        this.mbgraph = mbgraph;
        constantCount = 0;
    }

    @Override
    public Map<String, Integer> getOptimizationCounts() {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        counts.put("constants", constantCount);

        return counts;
    }

    @Override
    public boolean perform() {
        madeChanges = false;

        TIntList addresses = getValidAddresses();
        addresses.sort();
        addresses.reverse();
        for (int address : addresses.toArray()) {
            madeChanges = true;
            BuilderInstruction original = mbgraph.getInstruction(address);
            BuilderInstruction constInstruction = ConstantBuilder.buildConstant(address, mbgraph);
            boolean isReturn = original.getOpcode().name().startsWith("RETURN");
            if (isReturn) {
                mbgraph.addInstruction(address, constInstruction);
            } else {
                mbgraph.replaceInstruction(address, constInstruction);
            }
            constantCount++;
        }

        return madeChanges;
    }

    private TIntList getValidAddresses() {
        TIntList addresses = new TIntLinkedList();
        for (int address : mbgraph.getAddresses()) {
            if (canConstantizeAddress(address)) {
                addresses.add(address);
            }
        }

        return addresses;
    }

    private boolean canConstantizeAddress(int address) {
        if (!mbgraph.wasAddressReached(address)) {
            return false;
        }

        Op op = mbgraph.getOp(address);
        if (!constantBuilder.canConstantizeOp(op)) {
            return false;
        }

        OneRegisterInstruction instruction = (OneRegisterInstruction) mbgraph.getInstruction(address);
        if (instruction == null) {
            return false;
        }
        int register = instruction.getRegisterA();
        HeapItem consensus = mbgraph.getRegisterConsensus(address, register);
        // Consensus may be null if we have correct syntax without legitimate values (fake code)
        if ((consensus == null) || consensus.isUnknown()) {
            return false;
        }

        String valueType = consensus.getType();
        if (!constantBuilder.canConstantizeType(valueType)) {
            return false;
        }

        return true;
    }

    protected void getDependancies() {
        if (constantBuilder == null) {
            constantBuilder = new ConstantBuilder();
        }
    }

    protected void setDependancies(Dependancy dependancy) {
        constantBuilder = (ConstantBuilder) dependancy;
    }

}
