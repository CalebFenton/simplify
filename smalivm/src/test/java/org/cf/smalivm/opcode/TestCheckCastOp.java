package org.cf.smalivm.opcode;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.cf.smalivm.ClassManager;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.UnknownAncestors;
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
public class TestCheckCastOp {

    public static class IntegrationTest {

        @Test
        public void testStringWithObjectTypeCastsToString() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, "great maker", "Ljava/lang/Object;");
            TIntObjectMap<HeapItem> expected = initial;

            VMTester.testMethodState(CLASS_NAME, "CheckCastIsString()V", initial, expected);
        }

        @Test
        public void testStringWithStringTypeCastsToObject() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, "great maker", "Ljava/lang/String;");
            TIntObjectMap<HeapItem> expected = initial;

            VMTester.testMethodState(CLASS_NAME, "CheckCastIsObject()V", initial, expected);
        }
    }

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int ARG1_REGISTER = 0;

        private VirtualMachine vm;
        private MethodState mState;
        private ExecutionNode node;
        private TypeReference typeRef;
        private ClassManager classManager;
        private BuilderInstruction instruction;
        private TIntObjectMap<BuilderInstruction> addressToInstruction;
        private CheckCastOpFactory opFactory;
        private CheckCastOp op;

        @Test
        public void nonObjectTypeCastToObjectTypeThrowsExpectedException() throws UnknownAncestors {
            String castType = "Llolmoney;";
            String registerType = "Lmoneylol;";
            when(typeRef.getType()).thenReturn(castType);
            when(classManager.isInstance(eq(registerType), eq(castType))).thenReturn(false);
            VMTester.addHeapItem(mState, ARG1_REGISTER, null, registerType);

            op = (CheckCastOp) opFactory.create(instruction, addressToInstruction, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ClassCastException.class,
                            "moneylol cannot be cast to lolmoney");
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Test
        public void nullValueWithCastableTypeThrowsNoException() throws UnknownAncestors {
            String castType = "Llolmoney;";
            String registerType = "Lmoneylol;";
            when(typeRef.getType()).thenReturn(castType);
            when(classManager.isInstance(eq(registerType), eq(castType))).thenReturn(true);
            VMTester.addHeapItem(mState, ARG1_REGISTER, null, registerType);

            op = (CheckCastOp) opFactory.create(instruction, addressToInstruction, vm);
            op.execute(node, mState);

            verify(node).clearExceptions();
        }

        @Before
        public void setUp() {
            vm = mock(VirtualMachine.class);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
            typeRef = mock(TypeReference.class);
            classManager = mock(ClassManager.class);
            when(vm.getClassManager()).thenReturn(classManager);

            instruction = mock(
                            BuilderInstruction.class,
                            withSettings().extraInterfaces(OneRegisterInstruction.class, ReferenceInstruction.class,
                                            Instruction21c.class));
            MethodLocation location = mock(MethodLocation.class);
            when(location.getCodeAddress()).thenReturn(ADDRESS);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(((ReferenceInstruction) instruction).getReference()).thenReturn(typeRef);
            when(instruction.getOpcode()).thenReturn(Opcode.CHECK_CAST);
            when(((Instruction21c) instruction).getRegisterA()).thenReturn(ARG1_REGISTER);

            addressToInstruction = new TIntObjectHashMap<BuilderInstruction>();
            addressToInstruction.put(ADDRESS, instruction);

            opFactory = new CheckCastOpFactory();
        }
    }

    private static final String CLASS_NAME = "Lcheck_cast_test;";

}
