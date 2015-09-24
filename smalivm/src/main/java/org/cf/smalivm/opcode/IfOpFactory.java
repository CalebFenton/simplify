package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.opcode.IfOp.IfType;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22t;

public class IfOpFactory implements OpFactory {

    private static IfType getIfType(String opName) {
        IfType result = null;
        if (opName.contains("-eq")) {
            result = IfType.EQUAL;
        } else if (opName.contains("-ne")) {
            result = IfType.NOT_EQUAL;
        } else if (opName.contains("-lt")) {
            result = IfType.LESS;
        } else if (opName.contains("-le")) {
            result = IfType.LESS_OR_EQUAL;
        } else if (opName.contains("-gt")) {
            result = IfType.GREATER;
        } else if (opName.contains("-ge")) {
            result = IfType.GREATOR_OR_EQUAL;
        }

        return result;
    }

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        int address = instruction.getLocation().getCodeAddress();
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int targetAddress = address + branchOffset;
        BuilderInstruction child = Utils.getNextInstruction(instruction, addressToInstruction);
        BuilderInstruction target = addressToInstruction.get(targetAddress);

        String opName = instruction.getOpcode().name;
        IfType ifType = getIfType(opName);
        int register1 = ((OneRegisterInstruction) instruction).getRegisterA();

        if (instruction instanceof Instruction22t) {
            // if-* vA, vB, :label
            Instruction22t instr = (Instruction22t) instruction;

            return new IfOp(instruction, child, ifType, target, register1, instr.getRegisterB());
        } else {
            // if-*z vA, vB, :label (Instruction 21t)
            return new IfOp(instruction, child, ifType, target, register1);
        }
    }

}
