package simplify.instruction;

import java.util.List;

import simplify.Method;

public interface ControlInstruction extends Instruction {

    public List<String> getPossibleBranchLabels(Method method, String... args);

}
