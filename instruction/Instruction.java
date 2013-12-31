package simplify.instruction;

import java.util.regex.Pattern;

import simplify.ExecutionContext;

public interface Instruction {

    public Pattern getPattern();

    public void execute(ExecutionContext ectx, String... args);

}
