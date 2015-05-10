package org.cf.smalivm.opcode;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.immutable.reference.ImmutableFieldReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestIPutOp {

    private static final int ADDRESS = 0;
    private static final int REGISTER_A = 0;
    private static final int REGISTER_B = 2;

    private BuilderInstruction instruction;
    private OpFactory opFactory;
    private ExecutionContext ectx;
    private MethodState mState;
    private ExecutionNode node;
    private HeapItem itemA;
    private HeapItem itemB;
    private IPutOp op;
    private ArgumentCaptor<HeapItem> setItem;

    @Before
    public void setUp() {
        VirtualMachine vm = mock(VirtualMachine.class);
        instruction = mock(BuilderInstruction.class, withSettings().extraInterfaces(Instruction22c.class));
        when(((Instruction22c) instruction).getRegisterA()).thenReturn(REGISTER_A);
        when(((Instruction22c) instruction).getRegisterB()).thenReturn(REGISTER_B);

        FieldReference fieldRef = new ImmutableFieldReference("Lsome/class;", "someMethod", "I");
        when(((Instruction22c) instruction).getReference()).thenReturn(fieldRef);
        opFactory = new OpFactory(vm);
        mState = mock(MethodState.class);
        node = mock(ExecutionNode.class);

        ectx = mock(ExecutionContext.class);
        when(ectx.getMethodState()).thenReturn(mState);

        itemA = mock(HeapItem.class);
        when(mState.readRegister(REGISTER_A)).thenReturn(itemA);

        itemB = mock(HeapItem.class);
        when(mState.readRegister(REGISTER_B)).thenReturn(itemB);

        setItem = ArgumentCaptor.forClass(HeapItem.class);
    }

    @Test
    public void testIGetReturnsUnknownValueOfCorrectType() {
        when(instruction.getOpcode()).thenReturn(Opcode.IPUT);

        op = (IPutOp) opFactory.create(instruction, ADDRESS);
        op.execute(node, ectx);

        verify(mState, times(1)).readRegister(eq(REGISTER_A));
        verify(mState, times(1)).readRegister(eq(REGISTER_B));
        verify(mState, times(1)).assignRegister(eq(REGISTER_B), any(HeapItem.class));
        assertEquals("iput r" + REGISTER_A + ", r" + REGISTER_B + ", Lsome/class;->someMethod:I", op.toString());
    }
}
