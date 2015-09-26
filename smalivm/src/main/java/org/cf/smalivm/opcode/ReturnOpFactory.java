package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction11x;

public class ReturnOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        Instruction11x instr = (Instruction11x) location.getInstruction();
        int register = instr.getRegisterA();

        return new ReturnOp(location, register);
    }

}
