package org.cf.smalivm.opcode;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.immutable.reference.ImmutableFieldReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestIGetOp {

    private static final int ADDRESS = 0;
    private static final int REGISTER_A = 0;
    private static final int REGISTER_B = 2;

    private VirtualMachine vm;
    private BuilderInstruction instruction;
    private IGetOpFactory opFactory;
    private ExecutionContext ectx;
    private MethodState mState;
    private ExecutionNode node;
    private HeapItem itemB;
    private IGetOp op;
    private ArgumentCaptor<HeapItem> setItem;
    private TIntObjectMap<BuilderInstruction> addressToInstruction;

    @Before
    public void setUp() {
        vm = mock(VirtualMachine.class);
        ectx = mock(ExecutionContext.class);
        mState = mock(MethodState.class);
        when(ectx.getMethodState()).thenReturn(mState);
        node = mock(ExecutionNode.class);

        itemB = mock(HeapItem.class);
        when(mState.readRegister(REGISTER_B)).thenReturn(itemB);

        setItem = ArgumentCaptor.forClass(HeapItem.class);

        instruction = mock(BuilderInstruction.class, withSettings().extraInterfaces(Instruction22c.class));
        MethodLocation location = mock(MethodLocation.class);
        when(location.getCodeAddress()).thenReturn(ADDRESS);
        when(instruction.getLocation()).thenReturn(location);
        when(instruction.getCodeUnits()).thenReturn(0);
        when(((Instruction22c) instruction).getRegisterA()).thenReturn(REGISTER_A);
        when(((Instruction22c) instruction).getRegisterB()).thenReturn(REGISTER_B);
        FieldReference fieldRef = new ImmutableFieldReference("Lsome/class;", "someMethod", "I");
        when(((Instruction22c) instruction).getReference()).thenReturn(fieldRef);

        addressToInstruction = new TIntObjectHashMap<BuilderInstruction>();
        addressToInstruction.put(ADDRESS, instruction);

        opFactory = new IGetOpFactory();
    }

    @Test
    public void testIGetReturnsUnknownValueOfCorrectType() {
        when(instruction.getOpcode()).thenReturn(Opcode.IGET);

        op = (IGetOp) opFactory.create(instruction, addressToInstruction, vm);
        op.execute(node, ectx);

        verify(mState, times(1)).readRegister(eq(REGISTER_B));
        verify(mState, times(1)).assignRegister(eq(REGISTER_A), setItem.capture());
        assertEquals(UnknownValue.class, setItem.getValue().getValue().getClass());
        assertEquals("I", setItem.getValue().getType());
        assertEquals("iget r" + REGISTER_A + ", r" + REGISTER_B + ", Lsome/class;->someMethod:I", op.toString());
    }

}
