package org.cf.smalivm.core

/** How much a computation may have perturbed observable state. Aggregation errs upward. */
public enum class SideEffectLevel { NONE, WEAK, STRONG }
