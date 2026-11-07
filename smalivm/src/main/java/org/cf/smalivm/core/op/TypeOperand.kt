package org.cf.smalivm.core.op

import org.cf.smalivm.core.VirtualType

/**
 * An instruction's type reference, resolved at decode time against the loaded class set. [Present] carries
 * what the op needs from the resolved class; [Absent] makes the op throw a guest `NoClassDefFoundError`
 * ([org.cf.smalivm.core.resolutionFailure]).
 */
internal sealed interface TypeOperand<out T> {
    val type: VirtualType

    class Present<out T>(override val type: VirtualType, val resolved: T) : TypeOperand<T>

    class Absent(override val type: VirtualType) : TypeOperand<Nothing>
}
