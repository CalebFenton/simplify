package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

public class APutOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        Instruction23x instr = (Instruction23x) location.getInstruction();
        int putRegister = instr.getRegisterA();
        int arrayRegister = instr.getRegisterB();
        int indexRegister = instr.getRegisterC();

        return new APutOp(location, child, putRegister, arrayRegister, indexRegister, vm.getClassManager(),
                                 vm.getExceptionFactory());
    }

}
