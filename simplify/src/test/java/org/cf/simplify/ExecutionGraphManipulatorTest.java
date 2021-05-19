package org.cf.simplify;

import org.cf.smalivm.ExecutionGraph;
import org.cf.smalivm.ExecutionNode;
import org.cf.smalivm.ExecutionState;
import org.cf.smalivm.Value;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.ClassManager;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.Label;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction10x;
import org.jf.dexlib2.builder.instruction.BuilderInstruction11n;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21s;
import org.jf.dexlib2.builder.instruction.BuilderInstruction30t;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExecutionGraphTest {

    private static final String CLASS_NAME = "Lexecution_graph_manipulator_test;";

    private ExecutionGraph graph;

    private static void test(Object[][] expected, ExecutionGraph graph) {
        for (Object[] ex : expected) {
            int address = (Integer) ex[0];
            BuilderInstruction actualInstruction = graph.getInstruction(address);
            Opcode expectedOpcode = (Opcode) ex[1];
            assertEquals(expectedOpcode, actualInstruction.getOpcode());

            Object[][][] exChildren = (Object[][][]) ex[2];
            List<ExecutionNode> actualNodePile = graph.getNodePile(address);
            assertEquals(exChildren.length, actualNodePile.size(), expectedOpcode.name + " @" + address + " node pile size");
            for (int i = 0; i < exChildren.length; i++) {
                ExecutionNode actualNode = actualNodePile.get(i);
                List<ExecutionNode> childNodes = actualNode.getChildren();
                BuilderInstruction[] children = new BuilderInstruction[childNodes.size()];
                for (int j = 0; j < children.length; j++) {
                    children[j] = childNodes.get(j).getOp().getInstruction();
                }

                assertEquals(exChildren[i].length, children.length, expectedOpcode.name + " @" + address + " children size");
                for (int j = 0; j < exChildren[i].length; j++) {
                    assertEquals((int) exChildren[i][j][0], children[j].getLocation().getCodeAddress(), expectedOpcode.name + " @" + address + " child address");
                    assertEquals(exChildren[i][j][1], children[j].getOpcode(), expectedOpcode.name + " @" + address + " child opcode");
                }
            }
        }
    }

    private static void testHeritage(ExecutionGraph manipulator, int address) {
        ExecutionNode template = manipulator.getTemplateNode(address);
        assertEquals(0, template.getChildren().size());
        assertNotNull(template.getOp().getChildLocations());

        ExecutionNode node = manipulator.getNodePile(address).get(0);
        assertEquals(template.getOp(), node.getOp());

        List<ExecutionNode> children = node.getChildren();
        assertEquals(1, children.size());

        MethodLocation[] childLocations = node.getChildLocations();
        assertEquals(1, childLocations.length);

        ExecutionNode child = node.getChildren().get(0);
        assertEquals(node, child.getParent());
        assertEquals(node.getContext(), child.getContext().getParent());

        Op childOp = child.getOp();
        assertEquals(childOp.getLocation(), childLocations[0]);
        assertEquals(childOp.getLocation(), node.getOp().getChildLocations()[0]);
    }

    @Test
    public void addingInstructionModifiesStateCorrectly() {
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

        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "verySimple()V");
        BuilderInstruction addition = new BuilderInstruction10x(Opcode.NOP);
        graph.addInstruction(0, addition);

        test(expected, graph);
        testHeritage(graph, 0);
        testHeritage(graph, 1);
    }

    @Test
    public void addingInstructionThatCausesNopPaddingToBeAddedModifiesStateCorrectly() {
        //@formatter:off
        Object[][] expected = new Object[][] {
                        { 0, Opcode.CONST_16, new Object[][][] { { { 2, Opcode.NEW_ARRAY } } } },
                        { 2, Opcode.NEW_ARRAY, new Object[][][] { { { 4, Opcode.NOP } } } },
                        { 4, Opcode.NOP, new Object[][][] { { { 5, Opcode.FILL_ARRAY_DATA } } } },
                        { 5, Opcode.FILL_ARRAY_DATA, new Object[][][] { { { 0xa, Opcode.ARRAY_PAYLOAD } } } },
                        { 8, Opcode.RETURN_VOID, new Object[1][0][0] },
                        { 9, Opcode.NOP, new Object[0][0][0] },
                        { 0xa, Opcode.ARRAY_PAYLOAD, new Object[][][] {
                                        { { 8, Opcode.RETURN_VOID } },
                        } },
        };
        //@formatter:on

        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "hasNoNopPadding()V");
        BuilderInstruction addition = new BuilderInstruction10x(Opcode.NOP);
        graph.addInstruction(4, addition);

        test(expected, graph);
        testHeritage(graph, 2);
        testHeritage(graph, 4);
        testHeritage(graph, 5);
    }

    @Test
    public void addingManyNopsAfterGotoModifiesStateCorrectly() {
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

        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "hasGotoAndOneNop()V");

        // Adding 126 bytes (nop) between goto and target offset causes dexlib to "fix" goto into goto/16
        for (int i = 0; i < nops_to_insert - 1; i++) {
            graph.addInstruction(1, new BuilderInstruction10x(Opcode.NOP));
        }
        // Addresses 0 and 1 are now goto/16, need to insert at 2
        graph.addInstruction(2, new BuilderInstruction10x(Opcode.NOP));

        test(expected, graph);
    }

    @Test
    public void addingThenRemovingManyNopsAfterGotoModifiesStateCorrectly() {
        Object[][] expected = new Object[3][];
        expected[0] = new Object[] { 0, Opcode.GOTO_16, new Object[][][] { { { 3, Opcode.RETURN_VOID } } } };
        expected[1] = new Object[] { 2, Opcode.NOP, new Object[0][0][0] };
        expected[2] = new Object[] { 3, Opcode.RETURN_VOID, new Object[1][0][0] };

        int nops_to_insert = 127;

        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "hasGotoAndOneNop()V");

        // Adding 126 bytes (nop) between goto and target offset causes dexlib to "fix" goto into goto/16
        for (int i = 0; i < nops_to_insert - 1; i++) {
            graph.addInstruction(1, new BuilderInstruction10x(Opcode.NOP));
        }
        // Addresses 0 and 1 are now goto/16, need to insert at 2
        graph.addInstruction(2, new BuilderInstruction10x(Opcode.NOP));

        List<Integer> removeList = new LinkedList<Integer>();
        // for (int removeAddress = 2, i = 0; i < nops_to_insert; removeAddress++, i++) {
        for (int i = 1; i < nops_to_insert; i++) {
            int removeAddress = i + 2;
            removeList.add(removeAddress);
        }
        graph.removeInstructions(removeList);
        graph.removeInstruction(2);

        test(expected, graph);
    }

    @Test
    public void hasEveryRegisterAvailableAtEveryAddress() {
        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "verySimple()V");
        int[] addresses = graph.getAddresses();
        for (int address : addresses) {
            int[] actualAvailable = graph.getAvailableRegisters(address);

            assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, actualAvailable);
        }
    }

    @Test
    public void hasExpectedBasicProperties() {
        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "verySimple()V");

        int[] expectedAddresses = new int[] { 0, 1, 2, 3, 4, 5, };
        int[] actualAddresses = graph.getAddresses();
        assertArrayEquals(expectedAddresses, actualAddresses);
    }

    @Test
    public void removeInstructionThatCausesNopPaddingToBeRemovedAndHasParentWhichWModifiesStateCorrectly() {
        //@formatter:off
        Object[][] expected = new Object[][] {
                        { 0, Opcode.CONST_16, new Object[][][] { { { 2, Opcode.NEW_ARRAY } } } },
                        { 2, Opcode.NEW_ARRAY, new Object[][][] { { { 4, Opcode.FILL_ARRAY_DATA } } } },
                        { 4, Opcode.FILL_ARRAY_DATA, new Object[][][] { { { 8, Opcode.ARRAY_PAYLOAD } } } },
                        { 7, Opcode.RETURN_VOID, new Object[1][0][0] },
                        { 8, Opcode.ARRAY_PAYLOAD, new Object[][][] { { { 7, Opcode.RETURN_VOID } } } },
        };
        //@formatter:on

        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "hasNopPadding()V");
        graph.removeInstruction(4);

        test(expected, graph);
        testHeritage(graph, 2);
        testHeritage(graph, 4);
    }

    @Test
    public void removeInstructionWithNoParentModifiesStateCorrectly() {
        //@formatter:off
        Object[][] expected = new Object[][] {
                        { 0, Opcode.CONST_4, new Object[][][] { { { 1, Opcode.CONST_4 } } } },
                        { 1, Opcode.CONST_4, new Object[][][] { { { 2, Opcode.CONST_4 } } } },
                        { 2, Opcode.CONST_4, new Object[][][] { { { 3, Opcode.CONST_4 } } } },
                        { 3, Opcode.CONST_4, new Object[][][] { { { 4, Opcode.RETURN_VOID } } } },
                        { 4, Opcode.RETURN_VOID, new Object[1][0][0] },
        };
        //@formatter:on

        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "verySimple()V");
        graph.removeInstruction(0);

        test(expected, graph);
        testHeritage(graph, 0);
    }

    @Test
    public void removeInstructionWithParentModifiesStateCorrectly() {
        //@formatter:off
        Object[][] expected = new Object[][] {
                        { 0, Opcode.CONST_4, new Object[][][] { { { 1, Opcode.CONST_4 } } } },
                        { 1, Opcode.CONST_4, new Object[][][] { { { 2, Opcode.CONST_4 } } } },
                        { 2, Opcode.CONST_4, new Object[][][] { { { 3, Opcode.CONST_4 } } } },
                        { 3, Opcode.CONST_4, new Object[][][] { { { 4, Opcode.RETURN_VOID } } } },
                        { 4, Opcode.RETURN_VOID, new Object[1][0][0] },
        };
        //@formatter:on

        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "verySimple()V");
        graph.removeInstruction(1);

        test(expected, graph);
        testHeritage(graph, 0);
        testHeritage(graph, 1);
        testHeritage(graph, 2);

        ExecutionState parentState = graph.getNodePile(0).get(0).getState();
        assertArrayEquals(new int[] { 0 }, parentState.getRegistersAssigned());

        ExecutionState childState = graph.getNodePile(1).get(0).getState();
        assertArrayEquals(new int[] { 2 }, childState.getRegistersAssigned());

        ExecutionState grandchildState = graph.getNodePile(2).get(0).getState();
        assertArrayEquals(new int[] { 3 }, grandchildState.getRegistersAssigned());
    }

    @Test
    public void replaceInstructionExecutesNewNodeCorrectly() {
        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "constantPredicate()I");

        BuilderInstruction returnVoid = graph.getNodePile(4).get(0).getOp().getInstruction();
        Label target = returnVoid.getLocation().addNewLabel();

        // GOTO_32 shifts addresses around so mappings could break
        BuilderInstruction replacement = new BuilderInstruction30t(Opcode.GOTO_32, target);
        graph.replaceInstruction(1, replacement);

        testHeritage(graph, 0);
    }

    @Test
    public void emptyingATryBlockWithTwoHandlersWhichCreatesNullStartAndEndLocationsIsRemovedWithoutIncident()
            throws IOException {
        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "tryBlockWithTwoCatches()V");
        assertEquals(2, graph.getTryBlocks().size());

        graph.removeInstruction(0);
        assertEquals(0, graph.getTryBlocks().size());

        // Exception is thrown when saving. Make sure doesn't happen.
        ClassManager classManager = graph.getVm().getClassManager();
        File out = File.createTempFile("test", "simplify");
        classManager.getDexBuilder().writeTo(new FileDataStore(out));
        out.delete();
    }

    @Test
    public void replacingInstructionWithDifferentOpcodeWidthModifiesStateCorrectly() {
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

        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "verySimple()V");
        BuilderInstruction replacement = new BuilderInstruction21s(Opcode.CONST_16, 0, 0);
        graph.replaceInstruction(0, replacement);

        test(expected, graph);

        Value consensus = graph.getRegisterConsensus(0, 0);
        assertEquals(0, consensus.getRaw());
    }

    @Test
    public void replaceInstructionWithMultipleModifiesStateCorrectly() {
        //@formatter:off
        Object[][] expected = new Object[][] {
                        { 0, Opcode.CONST_4, new Object[][][] { { { 1, Opcode.CONST_16 } } } },
                        { 1, Opcode.CONST_16, new Object[][][] { { { 3, Opcode.CONST_16 } } } },
                        { 3, Opcode.CONST_16, new Object[][][] { { { 5, Opcode.CONST_4 } } } },
                        { 5, Opcode.CONST_4, new Object[][][] { { { 6, Opcode.CONST_4 } } } },
                        { 6, Opcode.CONST_4, new Object[][][] { { { 7, Opcode.CONST_4 } } } },
                        { 7, Opcode.CONST_4, new Object[][][] { { { 8, Opcode.RETURN_VOID } } } },
                        { 8, Opcode.RETURN_VOID, new Object[1][0][0] },
        };
        //@formatter:on

        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "verySimple()V");
        BuilderInstruction replacement1 = new BuilderInstruction21s(Opcode.CONST_16, 1, 1);
        BuilderInstruction replacement2 = new BuilderInstruction21s(Opcode.CONST_16, 2, 2);
        List<BuilderInstruction> replacements = new LinkedList<BuilderInstruction>();
        replacements.add(replacement1);
        replacements.add(replacement2);
        graph.replaceInstruction(1, replacements);

        test(expected, graph);
        testHeritage(graph, 0);
        testHeritage(graph, 1);
        testHeritage(graph, 3);

        Value consensus;
        consensus = graph.getRegisterConsensus(1, 1);
        assertEquals(1, consensus.getRaw());

        consensus = graph.getRegisterConsensus(3, 2);
        assertEquals(2, consensus.getRaw());
    }

    @Test
    public void replacingInstructionGetsLabelsAtInsertionAddress() {
        graph = OptimizerTester.getGraphManipulator(CLASS_NAME, "hasLabelOnConstantizableOp(I)I");
        BuilderInstruction addition = new BuilderInstruction11n(Opcode.CONST_4, 0, 2);

        assertEquals(1, graph.getInstruction(3).getLocation().getLabels().size());
        graph.replaceInstruction(3, addition);
        assertEquals(1, graph.getInstruction(3).getLocation().getLabels().size());
    }

}
