package org.cf.simplify.strategy;

import java.util.Map;

public interface OptimizationStrategy {

    public boolean perform();

    public Map<String, Integer> getOptimizationCounts();

}
