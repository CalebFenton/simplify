package simplify;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import simplify.instruction.Instruction;

public class Executor {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private int maxLocalJumps;
    private int maxCallDepth;

    private final MethodContainer methods;
    private final List<Instruction> instructions;

    public Executor(MethodContainer methods, List<Instruction> instructions) {
        this.methods = methods;
        this.instructions = instructions;

        maxLocalJumps = 100;
        maxCallDepth = 5;
    }

    public void setLocalMaxDepth(int depth) {
        maxLocalJumps = depth;
    }

    public void setExternalMaxDepth(int depth) {
        maxCallDepth = depth;
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
            ExecutionContext ectx = new ExecutionContext(methods, signature);

            try {
                executeMethod(ectx);
            } catch (MaxCallDepthExceeded | MaxLocalJumpsExceeded | UnsupportedInstruction e) {
                log.severe(e.toString());
            }
        }
    }

    public void executeMethod(ExecutionContext ectx) throws MaxCallDepthExceeded, MaxLocalJumpsExceeded,
                    UnsupportedInstruction {
        log.fine("Executing method: " + ectx.getMethod());

        while (ectx.getPosition() != null) {
            String line = methods.getLine(ectx.getMethod(), ectx.getPosition());

            log.finer("\tparsing line: '" + line.trim() + "'");

            boolean matched = false;
            for (Instruction instruction : instructions) {
                Matcher m = instruction.getPattern().matcher(line);

                if (m.matches()) {
                    String[] args = new String[m.groupCount() + 1];
                    for (int i = 0; i < args.length; i++) {
                        args[i] = m.group(i);
                    }

                    instruction.execute(ectx, args);

                    if (ectx.getCallDepth() > this.maxCallDepth) {
                        throw new MaxCallDepthExceeded("Last instruction: " + line.trim());
                    }

                    if (ectx.getLocalJumps() > this.maxLocalJumps) {
                        throw new MaxLocalJumpsExceeded("Last instruction: " + line.trim());
                    }

                    matched = true;
                    break;
                }
            }

            if (!matched) {
                throw new UnsupportedInstruction(line.trim());
            }
        }
    }

}
