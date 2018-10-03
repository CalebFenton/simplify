package org.cf.smalivm.opcode;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntObjectMap;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.smalivm.type.VirtualType;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.util.ReferenceUtil;

import java.util.List;

public class InvokeOpFactory implements OpFactory {

    private static int[] buildRegisters(Instruction instr) {
        if (instr instanceof Instruction3rc) {
            return buildRegisters3rc((Instruction3rc) instr);
        } else {
            return buildRegisters35c((Instruction35c) instr);
        }
    }

    private static int[] buildRegisters35c(Instruction35c instruction) {
        int registerCount = instruction.getRegisterCount();
        int[] registers = new int[registerCount];
        switch (registerCount) {
            case 5:
                registers[4] = instruction.getRegisterG();
            case 4:
                registers[3] = instruction.getRegisterF();
            case 3:
                registers[2] = instruction.getRegisterE();
            case 2:
                registers[1] = instruction.getRegisterD();
            case 1:
                registers[0] = instruction.getRegisterC();
                break;
        }

        return registers;
    }

    private static int[] buildRegisters3rc(Instruction3rc instruction) {
        int registerCount = instruction.getRegisterCount();
        int start = instruction.getStartRegister();
        int end = start + registerCount;

        int[] registers = new int[registerCount];
        for (int i = start; i < end; i++) {
            registers[i - start] = i;
        }

        return registers;
    }

    private int[] buildParameterRegisters(List<String> parameterTypes, int[] registers) {
        TIntList parameterRegisters = new TIntLinkedList(parameterTypes.size());
        int index = 0;
        for (String parameterType : parameterTypes) {
            parameterRegisters.add(registers[index]);
            index += Utils.getRegisterSize(parameterType);
        }

        return parameterRegisters.toArray();
    }

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        ReferenceInstruction instruction = (ReferenceInstruction) location.getInstruction();
        MethodReference methodReference = (MethodReference) instruction.getReference();
        int[] registers = buildRegisters(location.getInstruction());

        String className = methodReference.getDefiningClass();
        VirtualType type = vm.getClassManager().getVirtualType(className);
        String methodSignature = ReferenceUtil.getMethodDescriptor(methodReference);
        String methodDescriptor = methodSignature.split("->")[1];
        VirtualMethod method = type.getMethod(methodDescriptor);
        if (method == null) {
            throw new RuntimeException("Method doesn't exist: " + methodSignature);
        }
        int[] parameterRegisters = buildParameterRegisters(method.getParameterTypeNames(), registers);

        return new InvokeOp(location, child, method, parameterRegisters, vm);
    }

}
