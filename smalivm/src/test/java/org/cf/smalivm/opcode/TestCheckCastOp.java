package org.cf.smalivm.opcode;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import gnu.trove.map.TIntObjectMap;

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

    private static final String CLASS_NAME = "Lcheck_cast_test;";

    public static class IntegrationTest {

        @Test
        public void testStringWithStringTypeCastsToObject() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, "great maker", "Ljava/lang/String;");
            TIntObjectMap<HeapItem> expected = initial;

            VMTester.testMethodState(CLASS_NAME, "CheckCastIsObject()V", initial, expected);
        }

        @Test
        public void testStringWithObjectTypeCastsToString() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, "great maker", "Ljava/lang/Object;");
            TIntObjectMap<HeapItem> expected = initial;

            VMTester.testMethodState(CLASS_NAME, "CheckCastIsString()V", initial, expected);
        }
    }

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int ARG1_REGISTER = 0;

        private BuilderInstruction instruction;
        private OpFactory opFactory;
        private MethodState mState;
        private ExecutionNode node;
        private CheckCastOp op;
        private TypeReference typeRef;
        private ClassManager classManager;

        @Before
        public void setUp() {
            VirtualMachine vm = mock(VirtualMachine.class);
            opFactory = new OpFactory(vm);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
            instruction = mock(
                            BuilderInstruction.class,
                            withSettings().extraInterfaces(OneRegisterInstruction.class, ReferenceInstruction.class,
                                            Instruction21c.class));
            typeRef = mock(TypeReference.class);
            when(((ReferenceInstruction) instruction).getReference()).thenReturn(typeRef);
            when(instruction.getOpcode()).thenReturn(Opcode.CHECK_CAST);
            when(((Instruction21c) instruction).getRegisterA()).thenReturn(ARG1_REGISTER);

            classManager = mock(ClassManager.class);
            when(vm.getClassManager()).thenReturn(classManager);
        }

        @Test
        public void nullValueWithCastableTypeThrowsNoException() throws UnknownAncestors {
            String castType = "Llolmoney;";
            when(typeRef.getType()).thenReturn(castType);
            String registerType = "Lmoneylol;";
            when(classManager.isInstance(eq(registerType), eq(castType))).thenReturn(true);
            VMTester.addHeapItem(mState, ARG1_REGISTER, null, registerType);

            op = (CheckCastOp) opFactory.create(instruction, ADDRESS);
            op.execute(node, mState);

            verify(node).clearExceptions();
        }

        @Test
        public void nonObjectTypeCastToObjectTypeThrowsExpectedException() throws UnknownAncestors {
            String castType = "Llolmoney;";
            when(typeRef.getType()).thenReturn(castType);
            String registerType = "Lmoneylol;";
            when(classManager.isInstance(eq(registerType), eq(castType))).thenReturn(false);
            VMTester.addHeapItem(mState, ARG1_REGISTER, null, registerType);

            op = (CheckCastOp) opFactory.create(instruction, ADDRESS);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ClassCastException.class,
                            "moneylol cannot be cast to lolmoney");
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }
    }

}
