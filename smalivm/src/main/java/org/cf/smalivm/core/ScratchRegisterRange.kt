package org.cf.smalivm.core

import java.util.Collections

/** Exact logical scratch-register range reserved by one method-edit transaction. */
public class ScratchRegisterRange internal constructor(registers: List<LogicalRegister>) : Iterable<LogicalRegister> {
    private val storedRegisters = Collections.unmodifiableList(registers.toList())

    internal val wordCount: Int get() = storedRegisters.size

    /** The scratch register at word [offset] of this range. */
    public operator fun get(offset: Int): LogicalRegister = storedRegisters[offset]

    override fun iterator(): Iterator<LogicalRegister> = storedRegisters.iterator()
}
