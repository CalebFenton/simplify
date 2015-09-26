package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

public class CmpOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        Instruction23x instr = (Instruction23x) location.getInstruction();
        int destRegister = instr.getRegisterA();
        int lhsRegister = instr.getRegisterB();
        int rhsRegister = instr.getRegisterC();

        return new CmpOp(location, child, destRegister, lhsRegister, rhsRegister);
    }

}
