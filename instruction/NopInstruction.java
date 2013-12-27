package simplify.instruction;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import simplify.ExecutionContext;
import simplify.Simplifier;

public class NopInstruction implements Instruction {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    // nop, comments, directives and empty lines
    private static final Pattern PATTERN = Pattern.compile("^\\s*(?:nop|#.*|\\..*)?");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(ExecutionContext ectx, String... args) {
        ectx.incrementPosition();
    }

}
