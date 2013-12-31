package simplify.instruction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import simplify.ExecutionContext;
import simplify.Method;
import simplify.Simplifier;

public class IfInstruction implements ControlInstruction {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private static final Pattern PATTERN = Pattern.compile("(if-[^ ]+) ([vp]\\d+), :(\\w+)");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(ExecutionContext ectx, String... args) {
    }

    @Override
    public List<String> getPossibleBranchLabels(Method method, String... args) {
        String label = args[3];

        List<String> result = new ArrayList<String>();
        result.add(label);
        result.add("[next]"); // hack to include next line as branch

        return result;
    }

}
