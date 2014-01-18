package refactor.exec;

import java.util.Map;


public class MethodExecutor {

    public MethodExecutor(Map<String, InstructionGraph> instructionGraphs) {
        // TODO Auto-generated constructor stub
    }

    public void execute(String methodDescriptor) {
        // TODO Auto-generated method stub
        // for every method that's called, check if that class has been static init'ed
        // for every static field ref, also static init' the class (sget / sput need vm)
    }

}
