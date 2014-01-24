package optimize;

import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.vm.ContextGraph;

public class NoopRemover {

    private int unreachableCount = 0;
    private int unusedCount = 0;

    NoopRemover(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph) {
        // TODO Auto-generated constructor stub
    }

    boolean perform() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("unreachable=").append(unreachableCount).append(", unused=").append(unusedCount);

        return sb.toString();
    }
}
