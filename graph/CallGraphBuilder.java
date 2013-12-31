package simplify.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import simplify.Method;
import simplify.Simplifier;
import simplify.instruction.ControlInstruction;
import simplify.instruction.GotoInstruction;
import simplify.instruction.IfInstruction;
import simplify.instruction.Instruction;
import simplify.instruction.Label;
import simplify.instruction.ReturnInstruction;
import simplify.instruction.Skip;
import simplify.instruction.SwitchInstruction;

public class CallGraphBuilder {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    public static void build(Method method) {
        log.fine("Building graph for: " + method);

        addJumps(method);

        addNodes(method);

        addNodeConnections(method);
    }

    private static void addNodes(Method method) {
        List<Instruction> ignoredInstructions = getIgnoredNodeInstructions();

        List<String> lines = method.getLines();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            boolean ignore = false;
            for (Instruction instruction : ignoredInstructions) {
                Matcher m = instruction.getPattern().matcher(line);
                if (m.matches()) {
                    ignore = true;
                    break;
                }

            }

            if (!ignore) {
                method.addNode(i, new Node(line));
            }
        }
    }

    private static void addJumps(Method method) {
        Instruction labelInstruction = new Label();
        List<Instruction> ignoredInstructions = getIgnoredNodeInstructions();

        List<String> lines = method.getLines();
        for (int i = 0; i < lines.size(); i++) {
            Matcher m = labelInstruction.getPattern().matcher(lines.get(i));
            if (m.matches()) {
                String label = m.group(1);

                i++;

                // Need to find next instruction.
                for (; i < lines.size(); i++) {
                    boolean matched = false;
                    for (Instruction ignored : ignoredInstructions) {
                        Matcher m2 = ignored.getPattern().matcher(lines.get(i));
                        // System.out.println("should ignore? " + lines.get(i));
                        if (m2.matches()) {
                            // System.out.println(" yes! " + ignored.getPattern() + "  grp: " + m.group());
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) {
                        break;
                    }
                }

                if (method.getJump(label) == null) {
                    // Only add jumps if they don't already exist.
                    // Otherwise switches may redefine labels.
                    System.out.println("label: " + label + " i:" + i);
                    method.addJump(label, i);
                }
            }
        }
    }

    private static void addNodeConnections(Method method) {
        List<ControlInstruction> instructions = getControlInstructions();

        Map<Integer, Node> lineNodeMap = method.getLineNodeMap();
        Integer[] keys = lineNodeMap.keySet().toArray(new Integer[lineNodeMap.keySet().size()]);
        for (int i = 0; i < keys.length; i++) {
            Node currentNode = lineNodeMap.get(keys[i]);

            boolean matched = false;
            for (ControlInstruction instruction : instructions) {
                Matcher m = instruction.getPattern().matcher(currentNode.getLine());
                if (m.matches()) {
                    String[] args = new String[m.groupCount() + 1];
                    for (int j = 0; j < args.length; j++) {
                        args[j] = m.group(j);
                    }

                    List<String> labels = instruction.getPossibleBranchLabels(method, args);
                    for (String label : labels) {
                        Node child = null;
                        if (label.equals("[next]")) {
                            child = lineNodeMap.get(keys[i + 1]);
                        } else {
                            Integer line = method.getJump(label);
                            System.out.println("line: " + line + " = " + method.getLine(line));
                            child = lineNodeMap.get(line);
                        }

                        log.finer("Adding branch child: \"" + currentNode + "\" -> \"" + child + "\"");
                        currentNode.addChild(child);
                    }

                    matched = true;

                    break;
                }
            }

            if (!matched) {
                if ((i + 1) < keys.length) {
                    Node child = lineNodeMap.get(keys[i + 1]);
                    log.finer("Adding next child: \"" + currentNode + "\" -> \"" + child + "\"");
                    currentNode.addChild(child);
                } else {
                    System.out.println(lineNodeMap.get(keys[i]));
                    log.warning("Graph builder abruptly reached end of method: " + method + "@" + keys[i]);
                }
            }
        }
    }

    private static List<Instruction> getIgnoredNodeInstructions() {
        List<Instruction> instructions = new ArrayList<Instruction>();

        instructions.add(new Skip());
        instructions.add(new Label());

        return instructions;
    }

    private static List<ControlInstruction> getControlInstructions() {
        List<ControlInstruction> instructions = new ArrayList<ControlInstruction>();

        instructions.add(new GotoInstruction());
        instructions.add(new ReturnInstruction());
        instructions.add(new SwitchInstruction());
        instructions.add(new IfInstruction());

        return instructions;
    }
}
