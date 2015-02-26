package org.cf.simplify;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import gnu.trove.map.TIntObjectMap;

import java.util.Arrays;
import java.util.List;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.opcode.Op;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction10x;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21s;
import org.junit.Before;
import org.junit.Test;

public class TestMethodBackedGraph {

    // TODO: add tests for register and field consensus, + same after modifications
    private static final String CLASS_NAME = "Lmethod_backed_graph_test;";
    private static final String METHOD_NAME = "verySimple()V";

    private MethodBackedGraph mbgraph;

    @Before
    public void setUp() {
        mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, METHOD_NAME);
    }

    @Test
    public void testHasExpectedBasicProperties() {
        TIntObjectMap<BuilderInstruction> addressToInstruction = mbgraph.getAddressToInstruction();
        assertEquals(6, addressToInstruction.size());

        int[] expectedAddresses = new int[] { 0, 1, 2, 3, 4, 5, };
        int[] actualAddresses = mbgraph.getAddresses();
        Arrays.sort(actualAddresses);
        assertArrayEquals(expectedAddresses, actualAddresses);
    }

    @Test
    public void testHasEveryRegisterAvailableAtEveryAddress() {
        int[] addresses = mbgraph.getAddresses();
        int[] expectedAvailable = new int[] { 0, 1, 2, 3, 4, 5, };
        for (int address : addresses) {
            int[] actualAvailable = mbgraph.getAvailableRegisters(address).toArray();
            Arrays.sort(actualAvailable);
            assertArrayEquals(expectedAvailable, actualAvailable);
        }
    }

    @Test
    public void testInsertInstructionModifiesStateCorrectly() {
        TIntObjectMap<BuilderInstruction> addressToInstruction = mbgraph.getAddressToInstruction();
        assertEquals(6, addressToInstruction.size());
        mbgraph.insertInstruction(0, new BuilderInstruction10x(Opcode.NOP));

        assertEquals(7, addressToInstruction.size());
        assertEquals(Opcode.NOP, addressToInstruction.get(0).getOpcode());
        assertEquals(Opcode.CONST_4, addressToInstruction.get(1).getOpcode());

        for (int address : mbgraph.getAddresses()) {
            ExecutionNode templateNode = mbgraph.getTemplateNode(address);
            assertEquals(address, templateNode.getOp().getAddress());

            List<ExecutionNode> nodePile = mbgraph.getNodePile(address);
            assertEquals(1, nodePile.size());
            for (ExecutionNode node : nodePile) {
                Op op = node.getOp();
                assertEquals(address, op.getAddress());

                int[] children = op.getChildren();
                if (children.length > 0) {
                    int nextAddress = address + addressToInstruction.get(address).getCodeUnits();
                    assertArrayEquals(new int[] { nextAddress }, children);
                }
            }
        }
    }

    @Test
    public void testReplaceInstructionModifiesStateCorrectly() {
        TIntObjectMap<BuilderInstruction> addressToInstruction = mbgraph.getAddressToInstruction();
        assertEquals(6, addressToInstruction.size());
        mbgraph.replaceInstruction(0, new BuilderInstruction21s(Opcode.CONST_16, 0, 0));

        assertEquals(6, addressToInstruction.size());
        assertEquals(Opcode.CONST_16, addressToInstruction.get(0).getOpcode());
        assertEquals(Opcode.CONST_4, addressToInstruction.get(3).getOpcode());

        for (int address : mbgraph.getAddresses()) {
            ExecutionNode templateNode = mbgraph.getTemplateNode(address);
            assertEquals(address, templateNode.getOp().getAddress());

            List<ExecutionNode> nodePile = mbgraph.getNodePile(address);
            assertEquals(1, nodePile.size());
            for (ExecutionNode node : nodePile) {
                Op op = node.getOp();
                assertEquals(address, op.getAddress());

                int[] children = op.getChildren();
                if (children.length > 0) {
                    int nextAddress = address + addressToInstruction.get(address).getCodeUnits();
                    assertArrayEquals(new int[] { nextAddress }, children);
                }
            }
        }
    }

    @Test
    public void testRemoveInstructionModifiesStateCorrectly() {
        TIntObjectMap<BuilderInstruction> addressToInstruction = mbgraph.getAddressToInstruction();
        assertEquals(6, addressToInstruction.size());
        mbgraph.removeInstruction(0);

        assertEquals(5, addressToInstruction.size());
        assertEquals(Opcode.CONST_4, addressToInstruction.get(0).getOpcode());
        assertEquals(Opcode.CONST_4, addressToInstruction.get(1).getOpcode());

        for (int address : mbgraph.getAddresses()) {
            ExecutionNode templateNode = mbgraph.getTemplateNode(address);
            assertEquals(address, templateNode.getOp().getAddress());

            List<ExecutionNode> nodePile = mbgraph.getNodePile(address);
            assertEquals(1, nodePile.size());
            for (ExecutionNode node : nodePile) {
                Op op = node.getOp();
                assertEquals(address, op.getAddress());

                int[] children = op.getChildren();
                if (children.length > 0) {
                    int nextAddress = address + addressToInstruction.get(address).getCodeUnits();
                    assertArrayEquals(new int[] { nextAddress, }, children);
                }
            }
        }
    }

}
