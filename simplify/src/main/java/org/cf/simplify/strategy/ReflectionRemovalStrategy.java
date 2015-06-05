package org.cf.simplify.strategy;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cf.simplify.ConstantBuilder;
import org.cf.simplify.MethodBackedGraph;
import org.cf.smalivm.ClassManager;
import org.cf.smalivm.opcode.InvokeOp;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.LocalField;
import org.cf.smalivm.type.LocalMethod;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction11x;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.builder.instruction.BuilderInstruction22c;
import org.jf.dexlib2.builder.instruction.BuilderInstruction23x;
import org.jf.dexlib2.builder.instruction.BuilderInstruction32x;
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c;
import org.jf.dexlib2.builder.instruction.BuilderInstruction3rc;
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

public class ReflectionRemovalStrategy implements OptimizationStrategy {

    private static TIntList getFourBitValues(TIntList values) {
        TIntList fourBitValues = new TIntArrayList();
        for (int i = 0; i < values.size(); i++) {
            int value = values.get(i);
            if (value <= 0b1111) { // you don't see this literal format every day
                fourBitValues.add(value);
            }
        }

        return fourBitValues;
    }

    private static void setRegisterCount(BuilderMethod method, int registerCount) throws Exception {
        MethodImplementation implementation = method.getImplementation();
        Field f = implementation.getClass().getDeclaredField("registerCount");
        f.setAccessible(true); // hack the planet
        f.set(implementation, Integer.valueOf(registerCount));
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ReflectionRemovalStrategy.class.getSimpleName());

    private static final String MethodInvokeSignature = "Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";
    private static final String FieldGetSignature = "Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;";
    private final MethodBackedGraph mbgraph;

    private int unreflectCount;
    private TIntList addresses;
    private boolean madeChanges;

    public ReflectionRemovalStrategy(MethodBackedGraph mbgraph) {
        this.mbgraph = mbgraph;
        unreflectCount = 0;
    }

    @Override
    public Map<String, Integer> getOptimizationCounts() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        result.put("unreflects", unreflectCount);

        return result;
    }

    public boolean perform() {
        madeChanges = false;

        addresses = getValidAddresses(mbgraph);
        replaceMethodInvoke();

        addresses = getValidAddresses(mbgraph);
        replaceFieldGet();

        return madeChanges;
    }

    private MethodReference buildMethodReference(Method method) {
        String className = SmaliClassUtils.javaClassToSmali(method.getDeclaringClass());
        String name = method.getName();
        List<String> parameterTypes = SmaliClassUtils.javaClassToSmali(method.getParameterTypes());
        String returnType = SmaliClassUtils.javaClassToSmali(method.getReturnType());

        ImmutableMethodReference immutableMethodRef = new ImmutableMethodReference(className, name, parameterTypes,
                        returnType);
        MethodReference methodRef = mbgraph.getDexBuilder().internMethodReference(immutableMethodRef);

        return methodRef;
    }

    private List<BuilderInstruction> getArrayAccessorInstructions(int arrayRegister, TIntList registers,
                    List<String> parameterTypes) {
        List<BuilderInstruction> instructions = new LinkedList<BuilderInstruction>();
        for (int index = 0; index < parameterTypes.size(); index++) {
            int register = registers.get(index);
            BuilderInstruction constInstruction = ConstantBuilder.buildConstant(index, register);
            BuilderInstruction arrayGet = new BuilderInstruction23x(Opcode.AGET_OBJECT, register, arrayRegister,
                            register);
            String typeName = parameterTypes.get(index);
            if (SmaliClassUtils.isPrimitiveType(typeName)) {
                // Check cast expects a type reference, which I've never seen to be a primitive type.
                typeName = SmaliClassUtils.javaClassToSmali(SmaliClassUtils.smaliPrimitiveToJavaWrapper(typeName));
            }
            BuilderTypeReference typeRef = mbgraph.getDexBuilder().internTypeReference(typeName);
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

    List<BuilderInstruction> buildMethodInvokeReplacement(int address) throws Exception {
        // The good news is: this is as complicated as it gets
        Op op = mbgraph.getOp(address);
        int[] parameterRegisters = ((InvokeOp) op).getParameterRegisters();
        int methodRegister = parameterRegisters[0];
        int targetRegister = parameterRegisters[1];
        int parametersRegister = parameterRegisters[2];

        // As long as Method;->invoke is not emulated (it will never be white-listed!) current address will have all
        // unknown values. Get details from parents.
        TIntList parentAddresses = mbgraph.getParentAddresses(address);
        Object methodValue = mbgraph.getRegisterConsensusValue(parentAddresses, methodRegister);
        // Object targetValue = mbgraph.getRegisterConsensusValue(parentAddresses, targetRegister);
        Object parametersValue = mbgraph.getRegisterConsensusValue(parentAddresses, parametersRegister);
        assert (methodValue instanceof LocalMethod) || (methodValue instanceof Method);
        // assert !(targetValue instanceof UnknownValue);
        assert !(parametersValue instanceof UnknownValue);

        int methodAccessFlags = 0;
        int classAccessFlags = 0;
        int parameterRegisterCount = 0;
        List<String> parameterTypes = null;
        MethodReference methodRef = null;
        String declaringClass = null;
        if (methodValue instanceof Method) {
            Method method = (Method) methodValue;
            methodAccessFlags = method.getModifiers();
            classAccessFlags = method.getDeclaringClass().getModifiers();
            parameterTypes = SmaliClassUtils.javaClassToSmali(method.getParameterTypes());
            parameterRegisterCount = Utils.getRegisterSize(parameterTypes);
            methodRef = buildMethodReference(method);
        } else if (methodValue instanceof LocalMethod) {
            LocalMethod method = (LocalMethod) methodValue;
            BuilderMethod methodDef = mbgraph.getVM().getClassManager().getMethod(method.getName());
            methodAccessFlags = methodDef.getAccessFlags();
            declaringClass = method.getName().split("->")[0];
            ClassManager manager = mbgraph.getVM().getClassManager();
            classAccessFlags = manager.getClass(declaringClass).getAccessFlags();
            parameterTypes = Utils.builderTypeListToStringList(methodDef.getParameterTypes());
            parameterRegisterCount = Utils.getRegisterSize(methodDef.getParameterTypes());
            methodRef = mbgraph.getDexBuilder().internMethodReference(methodDef);
        }
        // TODO: easy - replace bitwise logic with Modifier.isStatic(int mod)
        boolean isStatic = Modifier.isStatic(methodAccessFlags);
        int invokeRegisterCount = parameterRegisterCount + (isStatic ? 0 : 1);

        boolean isRange = false;
        TIntList registers;
        if (0 == invokeRegisterCount) {
            registers = new TIntArrayList(new int[5]);
        } else {
            registers = new TIntArrayList();
            TIntList availableRegisters = mbgraph.getAvailableRegisters(address);
            availableRegisters.sort();
            if ((parametersValue instanceof Object[]) && (0 < ((Object[]) parametersValue).length)) {
                // Not just an empty array, so will need to pull values out later.
                availableRegisters.remove(parametersRegister);
            }

            if (5 < parameterRegisterCount) {
                isRange = true;
            }

            if (availableRegisters.size() < parameterRegisterCount) {
                // Add some more locals to this method
                String methodDescriptor = mbgraph.getMethodDescriptor();
                BuilderMethod builderMethod = mbgraph.getVM().getClassManager().getMethod(methodDescriptor);
                int oldRegisterCount = builderMethod.getImplementation().getRegisterCount();
                int registerCount = oldRegisterCount + invokeRegisterCount;
                setRegisterCount(builderMethod, registerCount);
                availableRegisters.clear();
                for (int register = oldRegisterCount; register < registerCount; register++) {
                    availableRegisters.add(register);
                }
            }

            TIntList fourBitRegisters = getFourBitValues(availableRegisters);
            if (fourBitRegisters.size() < parameterRegisterCount) {
                // invoke/range doesn't need all registers to be <4 bits
                isRange = true;
            }

            if (!isRange) {
                for (int i = 0; i < parameterRegisterCount; i++) {
                    registers.add(fourBitRegisters.get(i));
                }
            } else {
                // Either find contiguous available or make them available
                for (int i = 0; (i < availableRegisters.size()) && (registers.size() < invokeRegisterCount); i++) {
                    int register = availableRegisters.get(i);
                    registers.add(register);

                    if ((i + 1) < availableRegisters.size()) {
                        int nextRegister = availableRegisters.get(i + 1);
                        if ((nextRegister - register) != 1) {
                            registers.clear();
                        }
                    }
                }

                if (registers.size() < invokeRegisterCount) {
                    // Couldn't find enough contiguous. Expand locals and use registers at the end.
                    registers.clear();
                    String methodDescriptor = mbgraph.getMethodDescriptor();
                    BuilderMethod builderMethod = mbgraph.getVM().getClassManager().getMethod(methodDescriptor);
                    int oldRegisterCount = builderMethod.getImplementation().getRegisterCount();
                    int registerCount = oldRegisterCount + invokeRegisterCount;
                    setRegisterCount(builderMethod, registerCount);
                    for (int register = oldRegisterCount; register < registerCount; register++) {
                        registers.add(register);
                    }
                }
            }
        }

        List<BuilderInstruction> instructions = new LinkedList<BuilderInstruction>();

        if (!isStatic && isRange) {
            int instanceRegister = registers.get(0);
            BuilderInstruction move = new BuilderInstruction32x(Opcode.MOVE_OBJECT_16, instanceRegister, targetRegister);
            instructions.add(move);

            instructions.addAll(getArrayAccessorInstructions(parametersRegister,
                            registers.subList(1, registers.size()), parameterTypes));
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
                                registers.get(2), registers.get(3), registers.get(4), methodRef);
            } else {
                invoke = new BuilderInstruction35c(invokeOp, invokeRegisterCount, targetRegister, registers.get(0),
                                registers.get(1), registers.get(2), registers.get(3), methodRef);
            }
        }
        instructions.add(invoke);

        return instructions;
    }

    boolean canReplaceMethodInvoke(int address) {
        Op op = mbgraph.getOp(address);
        if (!(op instanceof InvokeOp)) {
            return false;
        }

        BuilderInstruction instruction = mbgraph.getInstruction(address);
        ReferenceInstruction instr = (ReferenceInstruction) instruction;
        String methodSignature = ReferenceUtil.getReferenceString(instr.getReference());
        if (!methodSignature.equals(MethodInvokeSignature)) {
            return false;
        }

        int[] parameterRegisters = ((InvokeOp) op).getParameterRegisters();
        int methodRegister = parameterRegisters[0];
        TIntList parentAddresses = mbgraph.getParentAddresses(address);
        Object methodValue = mbgraph.getRegisterConsensusValue(parentAddresses, methodRegister);
        if (methodValue instanceof UnknownValue) {
            return false;
        }

        int parametersRegister = parameterRegisters[2];
        Object parametersValue = mbgraph.getRegisterConsensusValue(parentAddresses, parametersRegister);
        if (parametersValue instanceof UnknownValue) {
            return false;
        }

        String className = mbgraph.getMethodDescriptor().split("->")[0];
        int methodAccessFlags = 0;
        String declaringClass = null;
        if (methodValue instanceof Method) {
            Method method = (Method) methodValue;
            methodAccessFlags = method.getModifiers();
            declaringClass = SmaliClassUtils.javaClassToSmali(method.getDeclaringClass());
        } else if (methodValue instanceof LocalMethod) {
            LocalMethod method = (LocalMethod) methodValue;
            ClassManager manager = mbgraph.getVM().getClassManager();
            BuilderMethod methodDef = manager.getMethod(method.getName());
            methodAccessFlags = methodDef.getAccessFlags();
            declaringClass = method.getName().split("->")[0];
        }

        boolean isPrivate = Modifier.isPrivate(methodAccessFlags);
        if (isPrivate && !declaringClass.equals(className)) {
            // Can't access this method without method.setAccessible(true)
            return false;
        }

        return true;
    }

    TIntList getValidAddresses(MethodBackedGraph mbgraph) {
        TIntList result = new TIntArrayList(mbgraph.getAddresses());
        for (int address : result.toArray()) {
            if (!mbgraph.wasAddressReached(address)) {
                result.remove(address);
            }
        }

        return result;
    }

    private void replaceFieldGet() {
        TIntList getAddresses = new TIntArrayList();
        for (int address : addresses.toArray()) {
            if (canReplaceFieldGet(address)) {
                getAddresses.add(address);
            }
        }

        if (0 == getAddresses.size()) {
            return;
        }

        madeChanges = true;
        unreflectCount += getAddresses.size();
        getAddresses.sort();
        getAddresses.reverse();
        for (int address : getAddresses.toArray()) {
            BuilderInstruction replacement = buildFieldGetReplacement(address);
            removeMoveResultIfNecessary(address);
            mbgraph.replaceInstruction(address, replacement);
        }
    }

    private void removeMoveResultIfNecessary(int address) {
        BuilderInstruction instruction = mbgraph.getInstruction(address);
        int nextAddress = address + instruction.getCodeUnits();
        BuilderInstruction nextInstr = mbgraph.getInstruction(nextAddress);

        String opName = nextInstr.getOpcode().name;
        if (opName.startsWith("move-result")) {
            mbgraph.removeInstruction(nextAddress);
        }
    }

    private BuilderInstruction buildFieldGetReplacement(int address) {
        Op op = mbgraph.getOp(address);
        int[] parameterRegisters = ((InvokeOp) op).getParameterRegisters();
        int fieldRegister = parameterRegisters[0];
        int targetRegister = parameterRegisters[1];
        Object fieldValue = mbgraph.getRegisterConsensusValue(address, fieldRegister);
        // Object targetValue = mbgraph.getRegisterConsensusValue(address, targetRegister);

        String fieldDescriptor = null;
        if (fieldValue instanceof LocalField) {
            LocalField field = (LocalField) fieldValue;
            fieldDescriptor = field.getName();
        } else if (fieldValue instanceof Field) {
            Field field = (Field) fieldValue;
            StringBuilder sb = new StringBuilder();
            sb.append(SmaliClassUtils.javaClassToSmali(field.getDeclaringClass()));
            sb.append("->").append(field.getName()).append(':');
            sb.append(SmaliClassUtils.javaClassToSmali(field.getType()));
            fieldDescriptor = sb.toString();
        }

        String[] parts;
        parts = fieldDescriptor.split("->");
        String className = parts[0];
        String fieldNameAndType = parts[1];
        parts = fieldNameAndType.split(":");
        String fieldName = parts[0];
        String type = parts[1];

        FieldReference fieldRef = null;
        boolean isStatic = false;
        if (fieldValue instanceof LocalField) {
            ClassManager classManager = mbgraph.getVM().getClassManager();
            BuilderField builderField = classManager.getField(className, fieldName);
            fieldRef = (FieldReference) builderField;
            isStatic = Modifier.isStatic(builderField.getAccessFlags());
        } else {
            Field field = (Field) fieldValue;
            fieldRef = mbgraph.getDexBuilder()
                            .internField(className, fieldName, type, field.getModifiers(), null, null);
            isStatic = Modifier.isStatic(field.getModifiers());
        }
        Opcode newOp = getGetOpcode(type, isStatic);

        BuilderInstruction instruction = mbgraph.getInstruction(address);
        int nextAddress = address + instruction.getCodeUnits();
        BuilderInstruction nextInstr = mbgraph.getInstruction(nextAddress);
        String opName = nextInstr.getOpcode().name;
        int destRegister;
        if (opName.startsWith("move-result")) {
            BuilderInstruction11x moveInstr = (BuilderInstruction11x) nextInstr;
            destRegister = moveInstr.getRegisterA();
        } else {
            destRegister = mbgraph.getAvailableRegisters(address).get(0);
        }

        BuilderInstruction replacement = null;
        if (isStatic) {
            replacement = new BuilderInstruction21c(newOp, destRegister, fieldRef);
        } else {
            replacement = new BuilderInstruction22c(newOp, destRegister, targetRegister, fieldRef);
        }

        return replacement;
    }

    static Opcode getGetOpcode(String type, boolean isStatic) {
        Opcode op;
        if (isStatic) {
            if (SmaliClassUtils.isPrimitiveType(type)) {
                // wide, boolean, byte, char, short, get
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
            if (SmaliClassUtils.isPrimitiveType(type)) {
                // wide, boolean, byte, char, short, get
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

    boolean canReplaceFieldGet(int address) {
        Op op = mbgraph.getOp(address);
        if (!(op instanceof InvokeOp)) {
            return false;
        }

        BuilderInstruction instruction = mbgraph.getInstruction(address);
        ReferenceInstruction instr = (ReferenceInstruction) instruction;
        String methodSignature = ReferenceUtil.getReferenceString(instr.getReference());
        if (!methodSignature.equals(FieldGetSignature)) {
            return false;
        }

        int[] parameterRegisters = ((InvokeOp) op).getParameterRegisters();
        int fieldRegister = parameterRegisters[0];
        // TIntList parentAddresses = mbgraph.getParentAddresses(address);
        Object fieldValue = mbgraph.getRegisterConsensusValue(address, fieldRegister);
        if (fieldValue instanceof UnknownValue) {
            return false;
        }

        if (fieldValue instanceof LocalField) {
            LocalField field = (LocalField) fieldValue;
            String[] parts = field.getName().split("->");
            String className = parts[0];
            String fieldNameAndType = parts[1];
            String fieldName = fieldNameAndType.split(":")[0];
            ClassManager classManager = mbgraph.getVM().getClassManager();
            BuilderField builderField = classManager.getField(className, fieldName);
            if (Modifier.isPublic(builderField.getAccessFlags()) && field.isAccessible()) {
                return false;
            }
        } else if (fieldValue instanceof Field) {
            Field field = (Field) fieldValue;
            boolean isPublic = Modifier.isPublic(field.getModifiers());
            if (!isPublic && field.isAccessible()) {
                // TODO: need to also check if mbgraph method has access to this field
                // same for localfields
                return false;
            }
        }

        int nextAddress = address + instruction.getCodeUnits();
        BuilderInstruction nextInstr = mbgraph.getInstruction(nextAddress);
        String opName = nextInstr.getOpcode().name;
        if (!opName.startsWith("move-result")) {
            TIntList available = mbgraph.getAvailableRegisters(address);
            if (available.size() == 0) {
                // How often do you see field lookup where the result isn't used
                // and there are no available registers?
                return false;
            }
        }

        return true;
    }

    private void replaceMethodInvoke() {
        TIntList invokeAddresses = new TIntArrayList();
        for (int address : addresses.toArray()) {
            if (canReplaceMethodInvoke(address)) {
                invokeAddresses.add(address);
            }
        }

        if (0 == invokeAddresses.size()) {
            return;
        }

        madeChanges = true;
        unreflectCount += invokeAddresses.size();

        invokeAddresses.sort();
        invokeAddresses.reverse();
        for (int address : invokeAddresses.toArray()) {
            List<BuilderInstruction> replacements = new LinkedList<BuilderInstruction>();
            try {
                replacements = buildMethodInvokeReplacement(address);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mbgraph.replaceWithMultipleInstructions(address, replacements);
        }
    }

}
