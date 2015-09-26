package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;

public class UnaryMathOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        Instruction12x instr = (Instruction12x) location.getInstruction();
        int destRegister = instr.getRegisterA();
        int srcRegister = instr.getRegisterB();

        return new UnaryMathOp(location, child, destRegister, srcRegister);
    }

}
