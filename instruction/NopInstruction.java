package simplify.instruction;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import simplify.ExecutionContext;

public class NopInstruction implements Instruction {

    private static final Logger log = Logger.getLogger(NewInstanceInsturction.class.getSimpleName());

    public static final Pattern PATTERN = Pattern.compile("^\\s*(?:nop|#.*|\\..*)?");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(ExecutionContext ectx, String... args) {
        log.info("nop nop!");
        ectx.incrementPosition();
    }

}
