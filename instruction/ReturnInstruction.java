package simplify.instruction;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import simplify.ExecutionContext;
import simplify.Simplifier;

public class ReturnInstruction implements Instruction {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private static final Pattern PATTERN = Pattern.compile("^\\s*(return[^ ]*)( [vp]\\d+)?");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(ExecutionContext ectx, String... args) {

        if (args.length == 3) {
            ectx.getRegisterValue(args[2]);
        }
        // else return-void

        ectx.exitMethod();
    }

}
