package simplifier.vm.emulate;

import java.util.logging.Logger;

import simplifier.Main;
import simplifier.vm.context.MethodContext;

public class Breakpoint implements EmulatedMethod {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public void execute(MethodContext mctx) {
        // To use, add invoke-static {}, Lsimplifier/Utils;->breakpoint()V, and set a breakpoint here.
        log.info("For a good time, set a breakpoint here!");
    }

}
