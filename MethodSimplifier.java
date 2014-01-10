package simplify;

import org.jf.dexlib2.writer.builder.BuilderMethod;

import simplify.graph.InstructionNode;

import com.google.common.collect.Multimap;

public class MethodSimplifier {

    public static boolean simplify(BuilderMethod method, Multimap<Integer, InstructionNode> nodes) {
        // return true if changes made
        return false;
    }

}
