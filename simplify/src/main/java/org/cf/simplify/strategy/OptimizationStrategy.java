package org.cf.simplify.strategy;

import java.util.Map;

public interface OptimizationStrategy {

    public Map<String, Integer> getOptimizationCounts();

    public boolean perform();

}
