package org.cf.simplify;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cf.simplify.strategy.ConstantPropigationStrategy;
import org.cf.simplify.strategy.DeadRemovalStrategy;
import org.cf.simplify.strategy.OptimizationStrategy;
import org.cf.simplify.strategy.PeepholeStrategy;
import org.cf.simplify.strategy.UnreflectionStrategy;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.reference.LocalMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Optimizer {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(Optimizer.class.getSimpleName());

    private static final Map<String, Integer> totalOptimizationCounts = new HashMap<String, Integer>();

    private final ExecutionGraphManipulator manipulator;
    private final LocalMethod localMethod;
    private final List<OptimizationStrategy> reoptimizeStrategies;
    private final List<OptimizationStrategy> reexecuteStrategies;
    private final List<OptimizationStrategy> allStrategies;

    private boolean madeAnyChanges;
    private boolean shouldReexecute;
    private Map<String, Integer> optimizationCounts;

    public Optimizer(ExecutionGraph graph, LocalMethod localMethod, VirtualMachine vm, DexBuilder dexBuilder,
                    SimplifyOptions opts) {
        manipulator = new ExecutionGraphManipulator(graph, localMethod, vm, dexBuilder);
        this.localMethod = localMethod;

        reoptimizeStrategies = new LinkedList<OptimizationStrategy>();
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        strategy.setRemoveWeak(opts.isRemoveWeak());
        reoptimizeStrategies.add(strategy);
        reoptimizeStrategies.add(new ConstantPropigationStrategy(manipulator));
        reoptimizeStrategies.add(new PeepholeStrategy(manipulator));

        /*
         * Some strategies may alter semantics. It's possible to remove method reflection without knowing the
         * result of the reflected method call. This leaves method states in a weird way, i.e. move-result has unknown
         * values. In these cases, re-execute the method to re-establish semantics.
         */
        reexecuteStrategies = new LinkedList<OptimizationStrategy>();
        reexecuteStrategies.add(new UnreflectionStrategy(manipulator));

        allStrategies = new LinkedList<OptimizationStrategy>();
        allStrategies.addAll(reoptimizeStrategies);
        allStrategies.addAll(reexecuteStrategies);

        optimizationCounts = new HashMap<String, Integer>();
    }

    public String getOptimizationCounts() {
        StringBuilder sb = new StringBuilder("Optimizations:\n");
        sb.append(buildOptimizationCounts(optimizationCounts));

        return sb.toString();
    }

    public boolean madeChanges() {
        return madeAnyChanges;
    }

    public boolean shouldReexecute() {
        return shouldReexecute;
    }

    public void simplify(int maxPasses) {
        System.out.println("Simplifying: " + localMethod);

        int pass = 0;
        madeAnyChanges = false;
        boolean madeChange;
        do {
            madeChange = false;
            for (OptimizationStrategy strategy : reoptimizeStrategies) {
                madeChange |= strategy.perform();
            }
            for (OptimizationStrategy strategy : reexecuteStrategies) {
                shouldReexecute |= strategy.perform();
            }

            madeAnyChanges |= madeChange | shouldReexecute;
            pass++;
        } while (madeChange && pass < maxPasses);

        updateOptimizationCounts();
    }

    private void updateOptimizationCounts() {
        for (OptimizationStrategy strategy : allStrategies) {
            Map<String, Integer> optimizations = strategy.getOptimizationCounts();
            for (Entry<String, Integer> entry : optimizations.entrySet()) {
                String key = entry.getKey();
                Integer currentCount = optimizationCounts.get(key);
                if (null == currentCount) {
                    currentCount = 0;
                }
                Integer totalCount = totalOptimizationCounts.get(key);
                if (null == totalCount) {
                    totalCount = 0;
                }

                int count = entry.getValue();
                currentCount += count;
                optimizationCounts.put(key, currentCount);
                totalCount += count;
                totalOptimizationCounts.put(key, totalCount);
            }
        }
    }

    public static String getTotalOptimizationCounts() {
        StringBuilder sb = new StringBuilder("Total optimizations:\n");
        sb.append(buildOptimizationCounts(totalOptimizationCounts));

        return sb.toString();
    }

    private static StringBuilder buildOptimizationCounts(Map<String, Integer> counts) {
        List<String> keys = new LinkedList<String>(counts.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append('\t').append(key).append(" = ").append(counts.get(key)).append("\n");
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 1);
        }

        return sb;
    }

}
