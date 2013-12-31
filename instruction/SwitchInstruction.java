package simplify.instruction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import simplify.ExecutionContext;
import simplify.Method;
import simplify.Simplifier;

public class SwitchInstruction implements ControlInstruction {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private static final Pattern PATTERN = Pattern.compile("((?:packed|sparse)-switch) [vp]\\d+, :(\\w+)");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(ExecutionContext ectx, String... args) {
    }

    @Override
    public List<String> getPossibleBranchLabels(Method method, String... args) {
        String label = args[2];

        List<String> result = new ArrayList<String>();

        Instruction labelInstruction = new Label();
        int startLine = method.getJump(label);
        System.out.println("switch label: " + label + " sl: " + startLine);
        while (true) {
            String line = method.getLine(startLine);
            if (line.contains(" -> ")) {
                line = line.split(" -> ")[1];
            }

            Matcher m = labelInstruction.getPattern().matcher(line);
            if (m.matches()) {
                result.add(m.group(1));
            } else {
                if (line.startsWith(".end ")) {
                    break;
                }
            }

            startLine++;
        }

        return result;
    }

}
