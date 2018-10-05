package org.cf.simplify.strategy;

import com.google.common.primitives.Ints;
import org.cf.simplify.ConstantBuilder;
import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.smalivm.opcode.InvokeOp;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.UnknownValue;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.*;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.immutable.reference.ImmutableMethodReference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderField;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UnreflectionStrategy implements OptimizationStrategy {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(UnreflectionStrategy.class.getSimpleName());

    private static final String METHOD_INVOKE_SIGNATURE =
            "Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";
    private static final String FIELD_GET_SIGNATURE =
            "Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;";

    private final ExecutionGraphManipulator manipulator;
    private int unreflectedMethodCount;
    private int unreflectedFieldCount;
    private int[] addresses;
    private boolean madeChanges;

    public UnreflectionStrategy(ExecutionGraphManipulator manipulator) {
        this.manipulator = manipulator;
        unreflectedMethodCount = 0;
        unreflectedFieldCount = 0;
    }

    static Opcode getGetOpcode(String type, boolean isStatic) {
        Opcode op;
        if (isStatic) {
            if (ClassNameUtils.isPrimitive(type)) {
                if ("J".equals(type) || "D".equals(type)) {
                    op = Opcode.SGET_WIDE;
                } else if ("Z".equals(type)) {
                    op = Opcode.SGET_BOOLEAN;
                } else if ("B".equals(type)) {
                    op = Opcode.SGET_BYTE;
                } else if ("C".equals(type)) {
                    op = Opcode.SGET_CHAR;
                } else if ("S".equals(type)) {
                    op = Opcode.SGET_SHORT;
                } else {
                    op = Opcode.SGET;
                }
            } else {
                op = Opcode.SGET_OBJECT;
            }
        } else {
            if (ClassNameUtils.isPrimitive(type)) {
                if ("J".equals(type) || "D".equals(type)) {
                    op = Opcode.IGET_WIDE;
                } else if ("Z".equals(type)) {
                    op = Opcode.IGET_BOOLEAN;
                } else if ("B".equals(type)) {
                    op = Opcode.IGET_BYTE;
                } else if ("C".equals(type)) {
                    op = Opcode.IGET_CHAR;
                } else if ("S".equals(type)) {
                    op = Opcode.IGET_SHORT;
                } else {
                    op = Opcode.IGET;
                }
            } else {
                op = Opcode.IGET_OBJECT;
            }
        }

        return op;
    }

    private static Integer[] getFourBitValues(List<Integer> values) {
        return values.stream().filter(v -> v <= 0b1111).toArray(Integer[]::new);
    }

    private static void setRegisterCount(BuilderMethod method, int registerCount) throws Exception {
        MethodImplementation implementation = method.getImplementation();
        Field f = implementation.getClass().getDeclaredField("registerCount");
        f.setAccessible(true); // hack the planet
        f.set(implementation, registerCount);
    }

    @Override
    public Map<String, Integer> getOptimizationCounts() {
        Map<String, Integer> result = new HashMap<>();
        result.put("unreflected methods", unreflectedMethodCount);
        result.put("unreflected fields", unreflectedFieldCount);

        return result;
    }

    @Override
    public boolean perform() {
        madeChanges = false;

        addresses = getValidAddresses(manipulator);
        replaceMethodInvoke();

        addresses = getValidAddresses(manipulator);
        replaceFieldGet();

        return madeChanges;
    }

    List<BuilderInstruction> buildMethodInvokeReplacement(int address) throws Exception {
        // The good news is: this is as complicated as it gets
        Op op = manipulator.getOp(address);
        int[] parameterRegisters = ((InvokeOp) op).getParameterRegisters();
        int methodRegister = parameterRegisters[0];
        int targetRegister = parameterRegisters[1];
        int parametersRegister = parameterRegisters[2];

        // As long as Method.invoke() is not emulated (it must never be white-listed as safe) current address
        // will have all unknown values. Get details from parents.
        int[] parentAddresses = manipulator.getParentAddresses(address);
        Object methodValue = manipulator.getRegisterConsensusValue(parentAddresses, methodRegister);

        java.lang.reflect.Method method = (java.lang.reflect.Method) methodValue;
        int methodAccessFlags = method.getModifiers();
        int classAccessFlags = method.getDeclaringClass().getModifiers();
        List<String> parameterTypes = ClassNameUtils.toInternal(method.getParameterTypes());
        int parameterRegisterCount = Utils.getRegisterSize(parameterTypes);
        MethodReference methodRef = buildMethodReference(method);

        boolean isStatic = Modifier.isStatic(methodAccessFlags);
        int invokeRegisterCount = parameterRegisterCount + (isStatic ? 0 : 1);

        boolean isRange = 5 < parameterRegisterCount;
        List<Integer> registers = new LinkedList<>();
        if (invokeRegisterCount > 0) {
            List<Integer> availableRegisters1 =
                    IntStream.of(manipulator.getAvailableRegisters(address)).boxed().sorted()
                            .collect(Collectors.toList());

            if (parameterTypes.size() > 0) {
                // Will need to pull values out of invocation parameters Object[]
                availableRegisters1.remove((Integer) parametersRegister);
            }

            if (availableRegisters1.size() < parameterRegisterCount) {
                // Add some more locals to this method
                VirtualMethod virtualMethod = manipulator.getMethod();
                BuilderMethod builderMethod = virtualMethod.getMethodDefinition();
                int oldRegisterCount = builderMethod.getImplementation().getRegisterCount();
                int registerCount = oldRegisterCount + invokeRegisterCount;
                setRegisterCount(builderMethod, registerCount);

                availableRegisters1.clear();
                for (Integer register = oldRegisterCount; register < registerCount; register++) {
                    availableRegisters1.add(register);
                }
            }

            Integer[] fourBitRegisters = getFourBitValues(availableRegisters1);
            if (fourBitRegisters.length < parameterRegisterCount) {
                // Not enough four bit registers, use invoke/range since not all registers need be < 4 bits
                isRange = true;
            }

            if (!isRange) {
                registers.addAll(Arrays.asList(fourBitRegisters).subList(0, parameterRegisterCount));
            } else {
                // Either find contiguous available or make them available
                for (int i = 0; i < availableRegisters1.size() && registers.size() < invokeRegisterCount; i++) {
                    Integer register = availableRegisters1.get(i);
                    registers.add(register);

                    if (i + 1 < availableRegisters1.size()) {
                        Integer nextRegister = availableRegisters1.get(i + 1);
                        if (nextRegister - register != 1) {
                            registers.clear();
                        }
                    }
                }

                if (registers.size() < invokeRegisterCount) {
                    // Couldn't find enough contiguous. Expand locals and use registers at the end.
                    registers.clear();
                    BuilderMethod builderMethod = manipulator.getMethod().getMethodDefinition();
                    int oldRegisterCount = builderMethod.getImplementation().getRegisterCount();
                    int registerCount = oldRegisterCount + invokeRegisterCount;
                    setRegisterCount(builderMethod, registerCount);
                    for (Integer register = oldRegisterCount; register < registerCount; register++) {
                        registers.add(register);
                    }
                }
            }
        }

        List<BuilderInstruction> instructions = new LinkedList<>();
        if (!isStatic && isRange) {
            int instanceRegister = registers.get(0);
            BuilderInstruction move =
                    new BuilderInstruction32x(Opcode.MOVE_OBJECT_16, instanceRegister, targetRegister);
            instructions.add(move);

            instructions.addAll(getArrayAccessorInstructions(parametersRegister, registers.subList(1, registers.size()),
                    parameterTypes));
        } else {
            instructions.addAll(getArrayAccessorInstructions(parametersRegister, registers, parameterTypes));
        }

        Opcode invokeOp = getInvokeOp(classAccessFlags, methodAccessFlags, isRange);
        BuilderInstruction invoke;
        if (isRange) {
            int startRegister = registers.get(0);
            invoke = new BuilderInstruction3rc(invokeOp, startRegister, invokeRegisterCount, methodRef);
        } else {
            while (registers.size() < 5) {
                registers.add(0);
            }

            if (isStatic) {
                invoke = new BuilderInstruction35c(invokeOp, invokeRegisterCount, registers.get(0), registers.get(1),
                        registers.get(2), registers.get(3), registers.get(4),
                        methodRef);
            } else {
                invoke = new BuilderInstruction35c(invokeOp, invokeRegisterCount, targetRegister, registers.get(0),
                        registers.get(1), registers.get(2), registers.get(3),
                        methodRef);
            }
        }
        instructions.add(invoke);

        return instructions;
    }

    boolean canReplaceFieldGet(int address) {
        Op op = manipulator.getOp(address);
        if (!(op instanceof InvokeOp)) {
            return false;
        }

        BuilderInstruction instruction = manipulator.getInstruction(address);
        ReferenceInstruction instr = (ReferenceInstruction) instruction;
        String methodSignature = ReferenceUtil.getReferenceString(instr.getReference());
        if (!methodSignature.equals(FIELD_GET_SIGNATURE)) {
            return false;
        }

        int[] parameterRegisters = ((InvokeOp) op).getParameterRegisters();
        int fieldRegister = parameterRegisters[0];
        Object fieldValue = manipulator.getRegisterConsensusValue(address, fieldRegister);
        if (fieldValue instanceof UnknownValue) {
            return false;
        }

        Field field = (Field) fieldValue;
        boolean isPublic = Modifier.isPublic(field.getModifiers());
        if (!isPublic && field.isAccessible()) {
            // TODO: need to also check if manipulator method has access to this field
            return false;
        }

        int nextAddress = address + instruction.getCodeUnits();
        BuilderInstruction nextInstr = manipulator.getInstruction(nextAddress);
        String opName = nextInstr.getOpcode().name;
        if (!opName.startsWith("move-result")) {
            int[] available = manipulator.getAvailableRegisters(address);
            if (available.length == 0) {
                // How often do you see field lookup where the result isn't used
                // and there are no available registers?
                return false;
            }
        }

        return true;
    }

    boolean canReplaceMethodInvoke(int address) {
        Op op = manipulator.getOp(address);
        if (!(op instanceof InvokeOp)) {
            return false;
        }

        BuilderInstruction instruction = manipulator.getInstruction(address);
        ReferenceInstruction instr = (ReferenceInstruction) instruction;
        String methodSignature = ReferenceUtil.getReferenceString(instr.getReference());
        if (!methodSignature.equals(METHOD_INVOKE_SIGNATURE)) {
            return false;
        }

        // Examine parent since maybe the method is not safe and invoke op will assume everything mutates to unknown.
        int[] parameterRegisters = ((InvokeOp) op).getParameterRegisters();
        int methodRegister = parameterRegisters[0];
        int[] parentAddresses = manipulator.getParentAddresses(address);
        Object methodValue = manipulator.getRegisterConsensusValue(parentAddresses, methodRegister);
        if (methodValue instanceof UnknownValue) {
            return false;
        }

        String className = manipulator.getMethod().getClassName();
        java.lang.reflect.Method method = (java.lang.reflect.Method) methodValue;
        int methodAccessFlags = method.getModifiers();
        String declaringClass = ClassNameUtils.toInternal(method.getDeclaringClass());

        boolean isPrivate = Modifier.isPrivate(methodAccessFlags);
        if (isPrivate && !declaringClass.equals(className) && !method.isAccessible()) {
            return false;
        }

        return true;
    }

    int[] getValidAddresses(ExecutionGraphManipulator manipulator) {
        int[] addresses = manipulator.getAddresses();
        List<Integer> validAddresses = new LinkedList<>();
        for (int address : addresses) {
            if (manipulator.wasAddressReached(address)) {
                validAddresses.add(address);
            }
        }

        return Ints.toArray(validAddresses);
    }

    private BuilderInstruction buildFieldGetReplacement(int address) {
        Op op = manipulator.getOp(address);
        int[] parameterRegisters = ((InvokeOp) op).getParameterRegisters();
        int fieldRegister = parameterRegisters[0];
        int targetRegister = parameterRegisters[1];
        Object fieldValue = manipulator.getRegisterConsensusValue(address, fieldRegister);
        // Object targetValue = manipulator.getRegisterConsensusValue(address, targetRegister);

        Field field = (Field) fieldValue;
        String fieldDescriptor = Utils.buildFieldDescriptor(field);

        String[] parts;
        parts = fieldDescriptor.split("->");
        String className = parts[0];
        String fieldNameAndType = parts[1];
        parts = fieldNameAndType.split(":");
        String fieldName = parts[0];
        String type = parts[1];

        boolean isStatic;
        BuilderField builderField =
                manipulator.getDexBuilder().internField(className, fieldName, type, field.getModifiers(), null, null);
        FieldReference fieldRef = manipulator.getDexBuilder().internFieldReference(builderField);

        isStatic = Modifier.isStatic(field.getModifiers());
        Opcode newOp = getGetOpcode(type, isStatic);

        BuilderInstruction instruction = manipulator.getInstruction(address);
        int nextAddress = address + instruction.getCodeUnits();
        BuilderInstruction nextInstr = manipulator.getInstruction(nextAddress);
        String opName = nextInstr.getOpcode().name;
        int destRegister;
        if (opName.startsWith("move-result")) {
            BuilderInstruction11x moveInstr = (BuilderInstruction11x) nextInstr;
            destRegister = moveInstr.getRegisterA();
        } else {
            destRegister = manipulator.getAvailableRegisters(address)[0];
        }

        BuilderInstruction replacement;
        if (isStatic) {
            replacement = new BuilderInstruction21c(newOp, destRegister, fieldRef);
        } else {
            replacement = new BuilderInstruction22c(newOp, destRegister, targetRegister, fieldRef);
        }

        return replacement;
    }

    private MethodReference buildMethodReference(java.lang.reflect.Method method) {
        String className = ClassNameUtils.toInternal(method.getDeclaringClass());
        String name = method.getName();
        List<String> parameterTypes = ClassNameUtils.toInternal(method.getParameterTypes());
        String returnType = ClassNameUtils.toInternal(method.getReturnType());

        ImmutableMethodReference immutableMethodRef =
                new ImmutableMethodReference(className, name, parameterTypes, returnType);
        MethodReference methodRef = manipulator.getDexBuilder().internMethodReference(immutableMethodRef);

        return methodRef;
    }

    private List<BuilderInstruction> getArrayAccessorInstructions(int arrayRegister, List<Integer> registers,
                                                                  List<String> parameterTypes) {
        List<BuilderInstruction> instructions = new LinkedList<>();
        for (int index = 0; index < parameterTypes.size(); index++) {
            int register = registers.get(index);
            BuilderInstruction constInstruction = ConstantBuilder.buildConstant(index, register);
            BuilderInstruction arrayGet =
                    new BuilderInstruction23x(Opcode.AGET_OBJECT, register, arrayRegister, register);
            String typeName = parameterTypes.get(index);
            if (ClassNameUtils.isPrimitive(typeName)) {
                // check-cast expects a non-primitive type (afaik)
                typeName = ClassNameUtils.binaryToInternal(ClassNameUtils.getWrapper(typeName));
            }
            BuilderTypeReference typeRef = manipulator.getDexBuilder().internTypeReference(typeName);
            BuilderInstruction checkCast = new BuilderInstruction21c(Opcode.CHECK_CAST, register, typeRef);

            instructions.add(constInstruction);
            instructions.add(arrayGet);
            instructions.add(checkCast);
        }

        return instructions;
    }

    private Opcode getInvokeOp(int classAccessFlags, int methodAccessFlags, boolean isRange) {
        Opcode invokeOp;

        boolean isInterface = Modifier.isInterface(classAccessFlags);
        if (isInterface) {
            invokeOp = isRange ? Opcode.INVOKE_INTERFACE_RANGE : Opcode.INVOKE_INTERFACE;

            return invokeOp;
        }

        boolean isStatic = Modifier.isStatic(methodAccessFlags);
        boolean isPrivate = Modifier.isPrivate(methodAccessFlags);
        boolean isConstructor = (methodAccessFlags & AccessFlags.CONSTRUCTOR.getValue()) != 0;
        if (isStatic) {
            invokeOp = isRange ? Opcode.INVOKE_STATIC_RANGE : Opcode.INVOKE_STATIC;
        } else if (isPrivate || isConstructor) {
            invokeOp = isRange ? Opcode.INVOKE_DIRECT_RANGE : Opcode.INVOKE_DIRECT;
        } else {
            invokeOp = isRange ? Opcode.INVOKE_VIRTUAL_RANGE : Opcode.INVOKE_VIRTUAL;
        }

        return invokeOp;
    }

    private void removeMoveResultIfNecessary(int address) {
        BuilderInstruction instruction = manipulator.getInstruction(address);
        int nextAddress = address + instruction.getCodeUnits();
        BuilderInstruction nextInstr = manipulator.getInstruction(nextAddress);

        String opName = nextInstr.getOpcode().name;
        if (opName.startsWith("move-result")) {
            manipulator.removeInstruction(nextAddress);
        }
    }

    private void replaceFieldGet() {
        List<Integer> getAddresses = new LinkedList<>();
        for (int address : addresses) {
            if (canReplaceFieldGet(address)) {
                getAddresses.add(address);
            }
        }

        if (0 == getAddresses.size()) {
            return;
        }

        madeChanges = true;
        unreflectedFieldCount += getAddresses.size();
        Collections.reverse(getAddresses);
        for (int address : getAddresses) {
            BuilderInstruction replacement = buildFieldGetReplacement(address);
            removeMoveResultIfNecessary(address);
            manipulator.replaceInstruction(address, replacement);
        }
    }

    private void replaceMethodInvoke() {
        int[] invokeAddresses = Arrays.stream(addresses)
                .filter(this::canReplaceMethodInvoke)
                .sorted()
                .toArray();

        int count = invokeAddresses.length;
        if (count == 0) {
            return;
        }

        madeChanges = true;
        unreflectedMethodCount += count;

        for (int i = count - 1; i >= 0; i--) {
            // Always replace in reverse order to avoid changing addresses
            int address = invokeAddresses[i];
            try {
                List<BuilderInstruction> replacements = buildMethodInvokeReplacement(address);
                manipulator.replaceInstruction(address, replacements);
            } catch (Exception e) {
                log.error("Unable to unreflect method invocation @" + address, e);
            }
        }
    }

}
