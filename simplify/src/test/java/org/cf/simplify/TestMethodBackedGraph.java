package org.cf.simplify;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;

import java.util.Arrays;
import java.util.List;

import org.cf.smalivm.context.ExecutionNode;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction10x;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21s;
import org.junit.Test;

public class TestMethodBackedGraph {

    // TODO: add tests for register and field consensus, + same after modifications
    private static final String CLASS_NAME = "Lmethod_backed_graph_test;";

    private static void test(Object[][] expected, MethodBackedGraph mbgraph) {
        TIntObjectMap<BuilderInstruction> addressToInstruction = mbgraph.getAddressToInstruction();
        for (Object[] ex : expected) {
            int address = (Integer) ex[0];
            BuilderInstruction actualInstruction = addressToInstruction.get(address);
            Opcode expectedOpcode = (Opcode) ex[1];
            assertEquals(expectedOpcode, actualInstruction.getOpcode());

            Object[][][] exChildren = (Object[][][]) ex[2];
            List<ExecutionNode> actualNodePile = mbgraph.getNodePile(address);
            assertEquals(exChildren.length, actualNodePile.size());
            for (int i = 0; i < exChildren.length; i++) {
                ExecutionNode actualNode = actualNodePile.get(i);
                BuilderInstruction[] children = actualNode.getOp().getChildren();
                assertEquals(exChildren[i].length, children.length);
                for (int j = 0; j < exChildren[i].length; j++) {
                    assertEquals((int) exChildren[i][j][0], children[j].getLocation().getCodeAddress());
                    assertEquals((Opcode) exChildren[i][j][1], children[j].getOpcode());
                }
            }
        }
    }

    private MethodBackedGraph mbgraph;

    @Test
    public void testHasEveryRegisterAvailableAtEveryAddress() {
        mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, "verySimple()V");
        int[] addresses = mbgraph.getAddresses();
        int[] expectedAvailable = new int[] { 0, 1, 2, 3, 4, 5, };
        for (int address : addresses) {
            int[] actualAvailable = mbgraph.getAvailableRegisters(address).toArray();
            Arrays.sort(actualAvailable);
            assertArrayEquals(expectedAvailable, actualAvailable);
        }
    }

    @Test
    public void testHasExpectedBasicProperties() {
        mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, "verySimple()V");
        TIntObjectMap<BuilderInstruction> addressToInstruction = mbgraph.getAddressToInstruction();
        assertEquals(6, addressToInstruction.size());

        int[] expectedAddresses = new int[] { 0, 1, 2, 3, 4, 5, };
        int[] actualAddresses = mbgraph.getAddresses();
        Arrays.sort(actualAddresses);
        assertArrayEquals(expectedAddresses, actualAddresses);
    }

    @Test
    public void testInsertingManyNopsAfterGotoModifiesStateCorrectly() {
        int nops_to_insert = 127;

        Object[][] expected = new Object[3 + nops_to_insert][];
        expected[0] = new Object[] { 0, Opcode.GOTO_16, new Object[][][] { { { 2 + 1 + 127, Opcode.RETURN_VOID } } } };
        // No children, no node pile, these nops are dead code and never get executed
        expected[1] = new Object[] { 2, Opcode.NOP, new Object[0][0][0] };
        for (int i = 0; i < nops_to_insert; i++) {
            int index = i + 2;
            expected[index] = new Object[] { index + 1, Opcode.NOP, new Object[0][0][0] };
        }
        expected[129] = new Object[] { 130, Opcode.RETURN_VOID, new Object[1][0][0] };

        mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, "hasGotoAndOneNop()V");

        // Adding 126 bytes (nop) between goto and target offset causes dexlib to "fix" goto into goto/16
        for (int i = 0; i < nops_to_insert - 1; i++) {
            mbgraph.insertInstruction(1, new BuilderInstruction10x(Opcode.NOP));
        }
        // Addresses 0 and 1 are now goto/16, need to insert at 2
        mbgraph.insertInstruction(2, new BuilderInstruction10x(Opcode.NOP));

        test(expected, mbgraph);
    }

    @Test
    public void testInsertingThenRemovingManyNopsAfterGotoModifiesStateCorrectly() {
        Object[][] expected = new Object[3][];
        expected[0] = new Object[] { 0, Opcode.GOTO_16, new Object[][][] { { { 3, Opcode.RETURN_VOID } } } };
        expected[1] = new Object[] { 2, Opcode.NOP, new Object[0][0][0] };
        expected[2] = new Object[] { 3, Opcode.RETURN_VOID, new Object[1][0][0] };

        int nops_to_insert = 127;

        mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, "hasGotoAndOneNop()V");

        // Adding 126 bytes (nop) between goto and target offset causes dexlib to "fix" goto into goto/16
        for (int i = 0; i < nops_to_insert - 1; i++) {
            mbgraph.insertInstruction(1, new BuilderInstruction10x(Opcode.NOP));
        }
        // Addresses 0 and 1 are now goto/16, need to insert at 2
        mbgraph.insertInstruction(2, new BuilderInstruction10x(Opcode.NOP));

        TIntList removeList = new TIntArrayList();
        for (int i = 0; i < nops_to_insert; i++) {
            int removeAddress = i + 2;
            removeList.add(removeAddress);
        }
        mbgraph.removeInstructions(removeList);

        test(expected, mbgraph);
    }

    @Test
    public void testInsertInstructionModifiesStateCorrectly() {
        //@formatter:off
        Object[][] expected = new Object[][] {
                        { 0, Opcode.NOP, new Object[][][] { { { 1, Opcode.CONST_4 } } } },
                        { 1, Opcode.CONST_4, new Object[][][] { { { 2, Opcode.CONST_4 } } } },
                        { 2, Opcode.CONST_4, new Object[][][] { { { 3, Opcode.CONST_4 } } } },
                        { 3, Opcode.CONST_4, new Object[][][] { { { 4, Opcode.CONST_4 } } } },
                        { 4, Opcode.CONST_4, new Object[][][] { { { 5, Opcode.CONST_4 } } } },
                        { 5, Opcode.CONST_4, new Object[][][] { { { 6, Opcode.RETURN_VOID } } } },
                        { 6, Opcode.RETURN_VOID, new Object[1][0][0] },
        };
        //@formatter:on

        mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, "verySimple()V");
        mbgraph.insertInstruction(0, new BuilderInstruction10x(Opcode.NOP));

        test(expected, mbgraph);
    }

    @Test
    public void testRemoveInstructionModifiesStateCorrectly() {
        //@formatter:off
        Object[][] expected = new Object[][] {
                        { 0, Opcode.CONST_4, new Object[][][] { { { 1, Opcode.CONST_4 } } } },
                        { 1, Opcode.CONST_4, new Object[][][] { { { 2, Opcode.CONST_4 } } } },
                        { 2, Opcode.CONST_4, new Object[][][] { { { 3, Opcode.CONST_4 } } } },
                        { 3, Opcode.CONST_4, new Object[][][] { { { 4, Opcode.RETURN_VOID } } } },
                        { 4, Opcode.RETURN_VOID, new Object[1][0][0] },
        };
        //@formatter:on

        mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, "verySimple()V");
        mbgraph.removeInstruction(0);

        test(expected, mbgraph);
    }

    @Test
    public void testReplaceInstructionModifiesStateCorrectly() {
        //@formatter:off
        Object[][] expected = new Object[][] {
                        { 0, Opcode.CONST_16, new Object[][][] { { { 2, Opcode.CONST_4 } } } },
                        { 2, Opcode.CONST_4, new Object[][][] { { { 3, Opcode.CONST_4 } } } },
                        { 3, Opcode.CONST_4, new Object[][][] { { { 4, Opcode.CONST_4 } } } },
                        { 4, Opcode.CONST_4, new Object[][][] { { { 5, Opcode.CONST_4 } } } },
                        { 5, Opcode.CONST_4, new Object[][][] { { { 6, Opcode.RETURN_VOID } } } },
                        { 6, Opcode.RETURN_VOID, new Object[1][0][0] },
        };
        //@formatter:on

        mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, "verySimple()V");
        mbgraph.replaceInstruction(0, new BuilderInstruction21s(Opcode.CONST_16, 0, 0));

        test(expected, mbgraph);
    }

}
