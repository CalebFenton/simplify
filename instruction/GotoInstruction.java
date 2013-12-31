package simplify.instruction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import simplify.ExecutionContext;
import simplify.Method;
import simplify.Simplifier;

public class GotoInstruction implements ControlInstruction {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private static final Pattern PATTERN = Pattern.compile("(goto[^ ]*) :(\\w+)");

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
        result.add(label);

        return result;
    }

}
