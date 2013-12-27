package simplify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import simplify.instruction.Instruction;

public class Executor {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private int localMaxDepth;
    private int externalMaxDepth;

    private final MethodContainer methods;
    private final List<Instruction> instructions;

    public Executor(MethodContainer methods, List<Instruction> instructions) {
        this.methods = methods;
        this.instructions = instructions;

        localMaxDepth = 10;
        externalMaxDepth = 5;
    }

    public void setLocalMaxDepth(int depth) {
        localMaxDepth = depth;
    }

    public void setExternalMaxDepth(int depth) {
        externalMaxDepth = depth;
    }

    public void execute() {
        // TODO: we're doing this elsewhere for sequence matching
        // Map<Integer, Instruction> instrMap = new HashMap<Integer, Instruction>();
        // int position = 1;
        // StringBuilder sb = new StringBuilder("(?:");
        // for (Instruction instruction : instructions) {
        // int endPosition = position + 1 + instruction.getGroupCount();
        // for (; position < endPosition; position++) {
        // instrMap.put(position, instruction);
        // }
        // sb.append("(").append(instruction.getRegex()).append(")|");
        //
        // }
        // sb.deleteCharAt(sb.length() - 1);
        // sb.append(")");
        // Pattern instrPattern = Pattern.compile(sb.toString());

        Set<String> signatures = methods.getMethodSignatures();
        for (String signature : signatures) {
            executeMethod(signature);
        }
    }

    public void executeMethod(String signature) {
        log.fine("Executing method: " + signature);

        ExecutionContext ectx = new ExecutionContext(signature);

        while (ectx.getPosition() >= 0) {
            String line = methods.getLine(signature, ectx.getPosition());
            log.finer("\tparsing line: " + line);

            boolean matched = false;
            for (Instruction instruction : instructions) {
                Matcher m = instruction.getPattern().matcher(line);

                if (m.matches()) {
                    String[] args = new String[m.groupCount() + 1];
                    for (int i = 0; i < args.length; i++) {
                        args[i] = m.group(i);
                    }

                    instruction.execute(ectx, args);
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                log.warning("Unrecognized instruction: " + line);
                System.exit(0);
            }
        }
    }

    public static Map<String, Instruction> buildInstructionMap(List<Instruction> instructions) {
        Map<String, Instruction> result = new HashMap<String, Instruction>();
        for (Instruction instruction : instructions) {
            result.put(instruction.getPatternString(), instruction);
        }

        return result;
    }

}
