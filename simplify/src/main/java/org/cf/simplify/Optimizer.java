package org.cf.simplify;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cf.simplify.strategy.ConstantPropigationStrategy;
import org.cf.simplify.strategy.DeadRemovalStrategy;
import org.cf.simplify.strategy.OptimizationStrategy;
import org.cf.simplify.strategy.PeepholeStrategy;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Optimizer {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(Optimizer.class.getSimpleName());

    private final List<OptimizationStrategy> allStrategies;
    private final MethodBackedGraph mbgraph;
    private final String methodDescriptor;
    private final List<OptimizationStrategy> performOnceStrategies;
    private final List<OptimizationStrategy> performRepeatedlyStrategies;

    public Optimizer(ExecutionGraph graph, BuilderMethod method, VirtualMachine vm, DexBuilder dexBuilder) {
        methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        mbgraph = new MethodBackedGraph(graph, method, vm, dexBuilder);
        performOnceStrategies = new LinkedList<OptimizationStrategy>();
        performOnceStrategies.add(new ConstantPropigationStrategy(mbgraph));
        performOnceStrategies.add(new PeepholeStrategy(mbgraph));

        performRepeatedlyStrategies = new LinkedList<OptimizationStrategy>();
        performRepeatedlyStrategies.add(new DeadRemovalStrategy(mbgraph));

        allStrategies = new ArrayList<OptimizationStrategy>();
        allStrategies.addAll(performOnceStrategies);
        allStrategies.addAll(performRepeatedlyStrategies);
    }

    public boolean simplify(int maxSweeps) {
        System.out.println("Simplifying: " + methodDescriptor);

        for (OptimizationStrategy strategy : performOnceStrategies) {
            strategy.perform();
        }

        int sweep = 0;
        boolean madeChanges;
        do {
            madeChanges = false;
            for (OptimizationStrategy strategy : performRepeatedlyStrategies) {
                madeChanges |= strategy.perform();
            }
            sweep++;
        } while ((sweep < maxSweeps) && madeChanges);

        StringBuilder sb = new StringBuilder("Optimizations: ");
        int totalCount = 0;
        for (OptimizationStrategy strategy : allStrategies) {
            Map<String, Integer> optimizations = strategy.getOptimizationCounts();
            for (String key : optimizations.keySet()) {
                int count = optimizations.get(key);
                totalCount += count;
                sb.append(key).append("=").append(count).append(", ");
            }
        }
        if (sb.length() > "Optimizations: ".length()) {
            sb.setLength(sb.length() - 2);
        }

        System.out.println(sb.toString());

        return totalCount > 0;
    }

}
