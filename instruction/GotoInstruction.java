package simplify.instruction;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import simplify.ExecutionContext;
import simplify.Simplifier;

public class GotoInstruction implements Instruction {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private static final Pattern PATTERN = Pattern.compile("^\\s*(goto[^ ]*) :(\\w+)");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(ExecutionContext ectx, String... args) {
        log.fine("args2: " + args[2]);

        ectx.incrementPosition();
    }

}
