package org.cf.smalivm.opcode;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntObjectMap;

import java.util.List;

import org.cf.smalivm.ClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.util.ReferenceUtil;

public class InvokeOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        BuilderInstruction instruction = (BuilderInstruction) location.getInstruction();
        String opName = instruction.getOpcode().name;

        MethodReference methodReference = (MethodReference) ((ReferenceInstruction) instruction).getReference();
        String methodDescriptor = ReferenceUtil.getMethodDescriptor(methodReference);

        int[] registers = null;
        if (opName.contains("/range")) {
            Instruction3rc instr = (Instruction3rc) location.getInstruction();
            int registerCount = instr.getRegisterCount();
            int start = instr.getStartRegister();
            int end = start + registerCount;

            registers = new int[registerCount];
            for (int i = start; i < end; i++) {
                registers[i - start] = i;
            }
        } else {
            Instruction35c instr = (Instruction35c) location.getInstruction();
            int registerCount = instr.getRegisterCount();
            registers = new int[registerCount];
            switch (registerCount) {
            case 5:
                registers[4] = instr.getRegisterG();
            case 4:
                registers[3] = instr.getRegisterF();
            case 3:
                registers[2] = instr.getRegisterE();
            case 2:
                registers[1] = instr.getRegisterD();
            case 1:
                registers[0] = instr.getRegisterC();
                break;
            }
        }

        String returnType = methodReference.getReturnType();
        List<String> parameterTypes;
        boolean isStatic = opName.contains("-static");
        ClassManager classManager = vm.getClassManager();
        if (classManager.isLocalMethod(methodDescriptor) && !(classManager.isFramework(methodDescriptor) && !classManager
                        .isSafeFramework(methodDescriptor))) {
            parameterTypes = classManager.getParameterTypes(methodDescriptor);
        } else {
            parameterTypes = Utils.getParameterTypes(methodDescriptor);
            if (!isStatic) {
                parameterTypes.add(0, methodReference.getDefiningClass());
            }
        }

        int i = 0;
        TIntList parameterRegisters = new TIntLinkedList(parameterTypes.size());
        for (String parameterType : parameterTypes) {
            parameterRegisters.add(registers[i]);
            i++;
            if (Utils.getRegisterSize(parameterType) == 2) {
                i++;
            }
        }

        return new InvokeOp(location, child, methodDescriptor, returnType, parameterRegisters.toArray(),
                        parameterTypes, vm, isStatic);
    }

}
