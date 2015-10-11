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
import org.jf.dexlib2.iface.instruction.Instruction;
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

        int[] registers = buildRegisters(location.getInstruction());
        String returnType = methodReference.getReturnType();
        boolean isStatic = opName.contains("-static");
        ClassManager classManager = vm.getClassManager();
        List<String> parameterTypes = getParameterTypes(methodDescriptor, isStatic, classManager);
        int[] parameterRegisters = buildParameterRegisters(parameterTypes, registers);

        return new InvokeOp(location, child, methodDescriptor, returnType, parameterRegisters, parameterTypes, vm,
                        isStatic);
    }

    private int[] buildParameterRegisters(List<String> parameterTypes, int[] registers) {
        int i = 0;
        TIntList parameterRegisters = new TIntLinkedList(parameterTypes.size());
        for (String parameterType : parameterTypes) {
            parameterRegisters.add(registers[i]);
            i++;
            if (Utils.getRegisterSize(parameterType) == 2) {
                i++;
            }
        }

        return parameterRegisters.toArray();
    }

    private static List<String> getParameterTypes(String methodDescriptor, boolean isStatic, ClassManager classManager) {
        List<String> parameterTypes;
        if (classManager.isLocalMethod(methodDescriptor) && !classManager.isFramework(methodDescriptor) || classManager
                        .isSafeFramework(methodDescriptor)) {
            parameterTypes = classManager.getParameterTypes(methodDescriptor);
        } else {
            parameterTypes = Utils.getParameterTypes(methodDescriptor);
            if (!isStatic) {
                parameterTypes.add(0, methodDescriptor.split("->")[0]);
            }
        }

        return parameterTypes;
    }

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

}
