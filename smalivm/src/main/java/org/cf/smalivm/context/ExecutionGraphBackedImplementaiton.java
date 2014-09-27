package org.cf.smalivm.context;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;

public class ExecutionGraphBackedImplementaiton {

    private final ExecutionGraph graph;
    private final MutableMethodImplementation implementation;

    ExecutionGraphBackedImplementaiton(ExecutionGraph graph, MutableMethodImplementation implementation) {
        this.graph = graph;
        this.implementation = implementation;
    }

    public void removeInstruction(int index) {
        implementation.removeInstruction(index);
    }

    public void replaceInstructionWithConstant(int index, BuilderInstruction replacementInstruction) {
        BuilderInstruction originalInstruction = implementation.getInstructions().get(index);
        implementation.replaceInstruction(index, replacementInstruction);
        /*
         * setup new contextnode set assigned to whichever register / or execute?? for each node in nodepile at that
         * index, replace with this maintain parent -> this, this -> children relationship
         */
    }
}
