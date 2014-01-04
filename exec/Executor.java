package simplify.exec;

import java.util.Map;
import java.util.logging.Logger;

import org.jf.dexlib2.writer.builder.BuilderMethod;

import simplify.Simplifier;
import simplify.graph.Node;

public class Executor {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    public void execute(Map<BuilderMethod, Node> callGraphs, int maxLocalJumps, int maxCallDepth) {

    }

}
