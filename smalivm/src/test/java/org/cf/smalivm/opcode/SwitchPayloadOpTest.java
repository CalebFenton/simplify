package org.cf.smalivm.opcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.LinkedList;
import java.util.List;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.VirtualMachineImpl;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerImpl;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.SwitchElement;
import org.jf.dexlib2.iface.instruction.SwitchPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SwitchPayloadOpTest {

    public static class UnitTest {

        private static final int ADDRESS = 0;

        private TIntObjectMap<MethodLocation> addressToLocation;
        private ClassManager classManager;
        private MethodLocation location;
        private SwitchPayloadOp op;
        private SwitchPayloadOpFactory opFactory;
        private BuilderInstruction instruction;
        private VirtualMachine vm;

        @Test
        public void payloadOpHasExpectedToString() {
            when(instruction.getOpcode()).thenReturn(Opcode.PACKED_SWITCH_PAYLOAD);
            List<SwitchElement> elements = new LinkedList<SwitchElement>();
            doReturn(elements).when((SwitchPayload) instruction).getSwitchElements();
            SwitchElement element1 = mock(SwitchElement.class);
            when(element1.getKey()).thenReturn(1);
            when(element1.getOffset()).thenReturn(10);
            SwitchElement element2 = mock(SwitchElement.class);
            when(element2.getKey()).thenReturn(2);
            when(element2.getOffset()).thenReturn(20);
            elements.add(element2);
            elements.add(element1);

            op = (SwitchPayloadOp) opFactory.create(location, addressToLocation, vm);

            assertEquals("packed-switch-payload [1 -> :addr_10, 2 -> :addr_20]", op.toString());
        }

        @BeforeEach
        public void setUp() {
            vm = mock(VirtualMachineImpl.class);
            classManager = mock(ClassManagerImpl.class);
            when(vm.getClassManager()).thenReturn(classManager);

            location = mock(MethodLocation.class);
            instruction = mock(BuilderInstruction.class, withSettings().extraInterfaces(SwitchPayload.class));
            when(location.getInstruction()).thenReturn(instruction);
            when(location.getCodeAddress()).thenReturn(ADDRESS);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);

            addressToLocation = new TIntObjectHashMap<MethodLocation>();
            addressToLocation.put(ADDRESS, location);

            opFactory = new SwitchPayloadOpFactory();
        }

    }

}
