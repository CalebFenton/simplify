package simplify.instruction;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import simplify.ExecutionContext;
import simplify.Simplifier;

public class ConstantInstruction implements Instruction {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    public static final Pattern PATTERN = Pattern.compile("^\\s*const[^ ]+ ([vp]\\d+), (.*)");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(ExecutionContext ectx, String... args) {
        log.fine("args0: " + args[0]);

        ectx.incrementPosition();
    }

}
