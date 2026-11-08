package org.cf.smalivm.core

/** Transaction-bound register operand before final physical encoding. */
class LogicalRegister internal constructor(internal val allocation: Allocation) {
    internal sealed interface Allocation {
        data class Baseline(val register: Int) : Allocation
        data class Scratch(val offset: Int) : Allocation
    }
}
