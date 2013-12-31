package simplify.instruction;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import simplify.ExecutionContext;
import simplify.Simplifier;

public class ConstantInstruction implements Instruction {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private static final Pattern PATTERN = Pattern.compile("(const[^ ]*) ([vp]\\d+), (.*)");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(ExecutionContext ectx, String... args) {
        String op = args[1];
        String name = args[2];
        String literal = args[3];

        Object value = null;
        String type = null;
        if (op.matches("const-string(?:/jumbo)?")) {
            type = String.class.getName();
            value = literal;
        } else if (op.matches("const(?:/4|/16|high16)?")) {
            type = "I";
            literal = literal.substring(2);
            value = Integer.valueOf(Integer.parseInt(literal, 16));
        } else if (op.matches("const-wide(?:/16|/32|/high16)?")) {
            type = "J";
            literal = literal.substring(2);
            value = Long.valueOf(Long.parseLong(literal, 16));
        } else if (op.matches("const-class")) {
            type = literal;
            // TODO: should be made ambiguous until I figure out how to handle these
            log.warning("should be made ambiguous: " + op);
        }

        ectx.addRegister(name, value, type);
    }

}
