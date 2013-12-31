package simplify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import simplify.instruction.ConstantInstruction;
import simplify.instruction.GotoInstruction;
import simplify.instruction.IfInstruction;
import simplify.instruction.Instruction;
import simplify.instruction.NewInstanceInstruction;
import simplify.instruction.NopInstruction;
import simplify.instruction.ReturnInstruction;
import simplify.instruction.SwitchInstruction;

public class Simplifier {
    private static Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private static final Level LOG_LEVEL = Level.FINER;

    public static void main(String[] argv) throws IOException {
        log.setLevel(LOG_LEVEL);
        for (Handler handler : Logger.getLogger("").getHandlers()) {
            handler.setLevel(LOG_LEVEL);
        }

        List<String> smaliFiles = new ArrayList<String>();
        smaliFiles.add(argv[0]);

        MethodContainer container = MethodExtractor.extract(smaliFiles);
        List<Instruction> instructions = getInstructions();
        Executor exec = new Executor(container, instructions);
        exec.execute();

        // MethodSaver.saveMethods(container);
    }

    private static List<Instruction> getInstructions() {
        List<Instruction> instructions = new ArrayList<Instruction>();
        instructions.add(new ConstantInstruction());
        instructions.add(new GotoInstruction());
        instructions.add(new IfInstruction());
        instructions.add(new NewInstanceInstruction());
        instructions.add(new NopInstruction());
        instructions.add(new ReturnInstruction());
        instructions.add(new SwitchInstruction());

        return instructions;
    }
}
