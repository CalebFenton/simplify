package org.cf.smalivm.opcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.VirtualMachineImpl;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class IGetOpTest {

    private static final int ADDRESS = 0;
    private static final int REGISTER_A = 0;
    private static final int REGISTER_B = 2;

    private TIntObjectMap<MethodLocation> addressToLocation;
    private ExecutionContext ectx;
    private BuilderInstruction instruction;
    private HeapItem itemB;
    private MethodLocation location;
    private MethodState mState;
    private ExecutionNode node;
    private IGetOp op;
    private IGetOpFactory opFactory;
    private ArgumentCaptor<HeapItem> setItem;
    private VirtualMachine vm;

    @BeforeEach
    public void setUp() {
        vm = mock(VirtualMachineImpl.class);
        ectx = mock(ExecutionContext.class);
        mState = mock(MethodState.class);
        when(ectx.getMethodState()).thenReturn(mState);
        node = mock(ExecutionNode.class);

        itemB = mock(HeapItem.class);
        when(mState.readRegister(REGISTER_B)).thenReturn(itemB);

        setItem = ArgumentCaptor.forClass(HeapItem.class);

        location = mock(MethodLocation.class);
        instruction = mock(BuilderInstruction.class, withSettings().extraInterfaces(Instruction22c.class));
        when(location.getInstruction()).thenReturn(instruction);
        when(location.getCodeAddress()).thenReturn(ADDRESS);
        when(instruction.getLocation()).thenReturn(location);
        when(instruction.getCodeUnits()).thenReturn(0);
        when(((Instruction22c) instruction).getRegisterA()).thenReturn(REGISTER_A);
        when(((Instruction22c) instruction).getRegisterB()).thenReturn(REGISTER_B);
        FieldReference fieldRef = new ImmutableFieldReference("Lsome/class;", "someMethod", "I");
        when(((Instruction22c) instruction).getReference()).thenReturn(fieldRef);

        addressToLocation = new TIntObjectHashMap<MethodLocation>();
        addressToLocation.put(ADDRESS, location);

        opFactory = new IGetOpFactory();
    }

    @Test
    public void testIGetReturnsUnknownValueOfCorrectType() {
        when(instruction.getOpcode()).thenReturn(Opcode.IGET);

        op = (IGetOp) opFactory.create(location, addressToLocation, vm);
        op.execute(node, ectx);

        verify(mState, times(1)).readRegister(eq(REGISTER_B));
        verify(mState, times(1)).assignRegister(eq(REGISTER_A), setItem.capture());
        assertEquals(UnknownValue.class, setItem.getValue().getValue().getClass());
        assertEquals("I", setItem.getValue().getType());
        assertEquals("iget r" + REGISTER_A + ", r" + REGISTER_B + ", Lsome/class;->someMethod:I", op.toString());
    }

}
