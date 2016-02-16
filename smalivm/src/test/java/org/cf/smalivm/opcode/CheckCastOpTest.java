package org.cf.smalivm.opcode;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.UnknownAncestors;
import org.cf.smalivm.smali.ClassManager;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class CheckCastOpTest {

    private static final String CLASS_NAME = "Lcheck_cast_test;";

    public static class IntegrationTest {

        private VMState expected;
        private VMState initial;

        @Test
        public void canCastNullWithObjectTypeToString() {
            initial.setRegisters(0, null, "Ljava/lang/Object;");
            expected.setRegisters(0, null, "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "castToString()V", initial, expected);
        }

        @Test
        public void canCastStringWithObjectTypeToString() {
            initial.setRegisters(0, "great maker", "Ljava/lang/Object;");
            expected.setRegisters(0, "great maker", "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "castToString()V", initial, expected);
        }

        @Test
        public void canCastStringWithStringTypeToObject() {
            initial.setRegisters(0, "great maker", "Ljava/lang/String;");
            expected = initial;

            VMTester.test(CLASS_NAME, "castToObject()V", initial, expected);
        }

        @Before
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }

    }

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int ARG1_REGISTER = 0;

        private TIntObjectMap<MethodLocation> addressToLocation;
        private TypeReference castTypeRef;
        private ClassManager classManager;
        private MethodLocation location;
        private MethodState mState;
        private ExecutionNode node;
        private CheckCastOp op;
        private CheckCastOpFactory opFactory;
        private VirtualMachine vm;

        @Test
        public void nullValueWithCastableTypeThrowsNoExceptionAndIsConverted() throws UnknownAncestors {
            String castType = "Llolmoney;";
            String registerType = "Lmoneylol;";
            when(castTypeRef.getType()).thenReturn(castType);
            when(classManager.isInstance(eq(registerType), eq(castType))).thenReturn(true);
            VMTester.setRegisterMock(mState, ARG1_REGISTER, null, registerType);

            op = (CheckCastOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(node).clearExceptions();
            verify(mState, times(1)).assignRegister(eq(ARG1_REGISTER), eq(null), eq(castType));
        }

        @Test
        public void objectNotOfCastTypeThrowsClassCastException() throws UnknownAncestors {
            String castType = "Llolmoney;";
            String registerType = "Lmoneylol;";
            when(castTypeRef.getType()).thenReturn(castType);
            when(classManager.isInstance(eq(registerType), eq(castType))).thenReturn(false);
            VMTester.setRegisterMock(mState, ARG1_REGISTER, new Object(), registerType);

            op = (CheckCastOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ClassCastException.class,
                            "moneylol cannot be cast to lolmoney");
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Before
        public void setUp() {
            vm = mock(VirtualMachine.class);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
            castTypeRef = mock(TypeReference.class);
            classManager = mock(ClassManager.class);
            when(vm.getClassManager()).thenReturn(classManager);

            location = mock(MethodLocation.class);
            BuilderInstruction instruction = mock(
                            BuilderInstruction.class,
                            withSettings().extraInterfaces(OneRegisterInstruction.class, ReferenceInstruction.class,
                                            Instruction21c.class));
            when(location.getInstruction()).thenReturn(instruction);
            when(location.getCodeAddress()).thenReturn(ADDRESS);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(((ReferenceInstruction) instruction).getReference()).thenReturn(castTypeRef);
            when(instruction.getOpcode()).thenReturn(Opcode.CHECK_CAST);
            when(((Instruction21c) instruction).getRegisterA()).thenReturn(ARG1_REGISTER);

            addressToLocation = new TIntObjectHashMap<MethodLocation>();
            addressToLocation.put(ADDRESS, location);

            opFactory = new CheckCastOpFactory();
        }
    }

}
