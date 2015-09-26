package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.VariableRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.dexlib2.util.ReferenceUtil;

public class FilledNewArrayOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        BuilderInstruction instruction = (BuilderInstruction) location.getInstruction();
        Reference reference = ((ReferenceInstruction) instruction).getReference();
        String typeReference = ReferenceUtil.getReferenceString(reference);
        int registerCount = ((VariableRegisterInstruction) instruction).getRegisterCount();
        String opName = instruction.getOpcode().name;
        int[] dimensionRegisters = new int[registerCount];
        if (opName.endsWith("/range")) {
            Instruction3rc instr = (Instruction3rc) location.getInstruction();
            int startRegister = instr.getStartRegister();
            for (int i = 0; i < dimensionRegisters.length; i++) {
                dimensionRegisters[i] = startRegister + i;
            }
        } else {
            Instruction35c instr = (Instruction35c) location.getInstruction();
            switch (dimensionRegisters.length) {
            case 5:
                dimensionRegisters[4] = instr.getRegisterG();
            case 4:
                dimensionRegisters[3] = instr.getRegisterF();
            case 3:
                dimensionRegisters[2] = instr.getRegisterE();
            case 2:
                dimensionRegisters[1] = instr.getRegisterD();
            case 1:
                dimensionRegisters[0] = instr.getRegisterC();
                break;
            default:
                // Shouldn't pass parser if op has >5 registers
            }
        }

        return new FilledNewArrayOp(location, child, dimensionRegisters, typeReference);
    }

}
