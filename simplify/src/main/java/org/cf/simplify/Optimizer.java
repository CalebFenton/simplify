package org.cf.simplify;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cf.simplify.strategy.ConstantPropigationStrategy;
import org.cf.simplify.strategy.DeadRemovalStrategy;
import org.cf.simplify.strategy.OptimizationStrategy;
import org.cf.simplify.strategy.PeepholeStrategy;
import org.cf.simplify.strategy.ReflectionRemovalStrategy;
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

    private static final Map<String, Integer> totalOptimizationCounts = new HashMap<String, Integer>();

    private final List<OptimizationStrategy> allStrategies;
    private final MethodBackedGraph mbgraph;
    private final String methodDescriptor;
    private final List<OptimizationStrategy> performOnceStrategies;
    private final List<OptimizationStrategy> performRepeatedlyStrategies;
    private final List<OptimizationStrategy> methodReexecuteStrategies;

    private boolean madeChanges;
    private boolean reExecute;

    public Optimizer(ExecutionGraph graph, BuilderMethod method, VirtualMachine vm, DexBuilder dexBuilder) {
        methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        mbgraph = new MethodBackedGraph(graph, method, vm, dexBuilder);
        performOnceStrategies = new LinkedList<OptimizationStrategy>();
        performOnceStrategies.add(new ConstantPropigationStrategy(mbgraph));
        performOnceStrategies.add(new PeepholeStrategy(mbgraph));

        performRepeatedlyStrategies = new LinkedList<OptimizationStrategy>();
        performRepeatedlyStrategies.add(new DeadRemovalStrategy(mbgraph));

        methodReexecuteStrategies = new LinkedList<OptimizationStrategy>();
        methodReexecuteStrategies.add(new ReflectionRemovalStrategy(mbgraph));

        allStrategies = new LinkedList<OptimizationStrategy>();
        allStrategies.addAll(performOnceStrategies);
        allStrategies.addAll(performRepeatedlyStrategies);
        allStrategies.addAll(methodReexecuteStrategies);
    }

    public void simplify(int maxSweeps) {
        System.out.println("Simplifying: " + methodDescriptor);

        for (OptimizationStrategy strategy : performOnceStrategies) {
            strategy.perform();
        }

        reExecute = false;
        for (OptimizationStrategy strategy : methodReexecuteStrategies) {
            reExecute |= strategy.perform();
        }

        int sweep = 0;
        do {
            madeChanges = false;
            for (OptimizationStrategy strategy : performRepeatedlyStrategies) {
                madeChanges |= strategy.perform();
            }
            sweep++;
        } while ((sweep < maxSweeps) && madeChanges);

        StringBuilder sb = new StringBuilder("Optimizations: ");
        int sweepCount = 0;
        for (OptimizationStrategy strategy : allStrategies) {
            Map<String, Integer> optimizations = strategy.getOptimizationCounts();
            for (String key : optimizations.keySet()) {
                Integer totalCount = totalOptimizationCounts.get(key);
                if (null == totalCount) {
                    totalCount = 0;
                }

                int count = optimizations.get(key);
                totalCount += count;
                totalOptimizationCounts.put(key, totalCount);

                sweepCount += count;
                sb.append(key).append('=').append(count).append(", ");
            }
        }
        madeChanges = sweepCount > 0;

        if (sb.length() > "Optimizations: ".length()) {
            sb.setLength(sb.length() - 2);
        }
        System.out.println(sb.toString());
    }

    public boolean madeChanges() {
        return madeChanges;
    }

    public boolean reExecute() {
        return reExecute;
    }

    public static String getTotalOptimizationCounts() {
        StringBuilder sb = new StringBuilder("Total optimizations: ");
        for (Entry<String, Integer> entry : totalOptimizationCounts.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append(", ");
        }
        if (sb.length() > "Total optimizations: ".length()) {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }

}
