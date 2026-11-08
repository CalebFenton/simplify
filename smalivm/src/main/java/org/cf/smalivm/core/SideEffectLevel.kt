package org.cf.smalivm.core

/** How much a computation may have perturbed observable state (spec §4.5). Aggregation errs upward. */
public enum class SideEffectLevel { NONE, WEAK, STRONG }
