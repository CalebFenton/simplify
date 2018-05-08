package org.cf.smalivm;

import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.type.UninitializedInstance;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.util.ClassNameUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MethodReflectorTest {

    private static final String CLASS_NAME = "Lmethod_reflector_test;";
    private VMState expected;
    private VMState initial;

    @Before
    public void setUp() {
        expected = new VMState();
        initial = new VMState();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void canReflectivelyInstantiateAnEnum() throws ClassNotFoundException {
        String className = "Lextends_enum;";
        String methodDescriptor = "<init>(Ljava/lang/String;II)V";
        String enumName = "NONE";
        VirtualMachine vm = VMTester.spawnVM();
        VirtualClass virtualClass = vm.getClassManager().getVirtualClass(className);
        VirtualMethod method = virtualClass.getMethod(methodDescriptor);
        int offset = method.getRegisterCount() - method.getParameterSize();
        initial.setRegister(offset, new UninitializedInstance(virtualClass), className);
        initial.setRegister(offset + 1, enumName, CommonTypes.STRING);
        initial.setRegister(offset + 2, 0, CommonTypes.INTEGER);
        initial.setRegister(offset + 3, 0, CommonTypes.INTEGER);

        ExecutionGraph graph = VMTester.execute(vm, className, methodDescriptor, initial);
        HeapItem instance = graph.getTerminatingRegisterConsensus(0);

        Class<? extends Enum> klazz =
                (Class<? extends Enum>) vm.getClassLoader().loadClass(ClassNameUtils.internalToSource(className));
        Object expectedValue = Enum.valueOf(klazz, enumName);

        assertEquals(expectedValue, instance.getValue());
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void canReflectivelyInstantiateAnObfuscatedEnum() throws ClassNotFoundException {
        String className = "Lextends_enum_obfuscated;";
        String methodDescriptor = "<init>(Ljava/lang/String;II)V";
        String enumName = "WEAK";
        String obfuscatedEnumName = "c";
        VirtualMachine vm = VMTester.spawnVM();
        VirtualClass virtualClass = vm.getClassManager().getVirtualClass(className);
        VirtualMethod method = virtualClass.getMethod(methodDescriptor);
        int offset = method.getRegisterCount() - method.getParameterSize();
        initial.setRegister(offset, new UninitializedInstance(virtualClass), className);
        initial.setRegister(offset + 1, enumName, CommonTypes.STRING);
        initial.setRegister(offset + 2, 0, CommonTypes.INTEGER);
        initial.setRegister(offset + 3, 0, CommonTypes.INTEGER);

        ExecutionGraph graph = VMTester.execute(vm, className, methodDescriptor, initial);
        HeapItem instance = graph.getTerminatingRegisterConsensus(0);

        Class<? extends Enum> klazz =
                (Class<? extends Enum>) vm.getClassLoader().loadClass(ClassNameUtils.internalToSource(className));
        Object expectedValue = Enum.valueOf(klazz, obfuscatedEnumName);

        assertEquals(expectedValue, instance.getValue());
    }

    @Test
    public void methodWithPrimitiveReturnValueTypeActuallyReturnsPrimitiveType() {
        int intVal = 42;
        initial.setRegisters(0, Integer.valueOf(intVal), "Ljava/lang/Integer;");
        expected.setRegisters(0, intVal, CommonTypes.INTEGER);

        VMTester.test(CLASS_NAME, "intValueOfInteger()V", initial, expected);
    }

    @Test
    public void canCastIntegerToByte() {
        byte value = 6;
        initial.setRegisters(0, value, "B");
        expected.setRegisters(0, value, CommonTypes.BYTE_OBJ);

        VMTester.test(CLASS_NAME, "byteValueOfByte()V", initial, expected);
    }

    @Test
    public void canInitBooleanWithBoolean() {
        boolean value = true;
        initial.setRegisters(1, value, "Z");
        expected.setRegisters(0, value, CommonTypes.BOOLEAN_OBJ);

        VMTester.test(CLASS_NAME, "initBooleanWithBoolean()V", initial, expected);
    }

    @Test
    public void canInitCharacterWithChar() {
        char value = 'a';
        initial.setRegisters(1, value, "C");
        expected.setRegisters(0, value, CommonTypes.CHARACTER_OBJ);

        VMTester.test(CLASS_NAME, "initCharacterWithChar()V", initial, expected);
    }

    @Test
    public void canGetShortValueOfShort() {
        short value = 5;
        initial.setRegisters(0, value, "S");
        expected.setRegisters(0, value, CommonTypes.SHORT_OBJ);

        VMTester.test(CLASS_NAME, "shortValueOfShort()V", initial, expected);
    }

    @Test
    public void handlesNullArgument() throws NoSuchMethodException, SecurityException {
        initial.setRegisters(0, System.class, CommonTypes.CLASS, 1, "currentTimeMillis", CommonTypes.STRING, 2, 0, "I");
        expected.setRegisters(0, System.class.getMethod("currentTimeMillis", (Class<?>[]) null), "Ljava/lang/reflect/Method;");

        VMTester.test(CLASS_NAME, "getClassMethod()V", initial, expected);
    }

    @Test
    public void handlesException() throws NoSuchMethodException, SecurityException {
        initial.setRegisters(0, null, CommonTypes.STRING);
        int[] expected = new int[]{ 0, 4 };

        VMTester.testVisitation(CLASS_NAME, "stringLength()V", initial, expected);
    }

    @Test
    public void handlesNoException() throws NoSuchMethodException, SecurityException {
        initial.setRegisters(0, "four", CommonTypes.STRING);
        int[] expected = new int[]{ 0, 3, 4 };

        VMTester.testVisitation(CLASS_NAME, "stringLength()V", initial, expected);
    }
}
