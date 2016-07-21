package org.cf.simplify.strategy;

import java.util.Map;

public interface OptimizationStrategy {

    Map<String, Integer> getOptimizationCounts();

    boolean perform();

}
