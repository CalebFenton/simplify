package simplify.instruction;

import java.util.regex.Pattern;

import simplify.ExecutionContext;

public class Skip implements Instruction {

    // comments, directives, empty lines, numbers
    private static final Pattern PATTERN = Pattern.compile("^(?:$|-?0x|#|\\.).*");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(ExecutionContext ectx, String... args) {
    }

}
