package org.cf.smalivm.core.op

import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.VirtualType

/**
 * An instruction's type reference, resolved at decode time against the loaded class set. [Present] carries
 * what the op needs from the resolved class; [Absent] makes the op throw [Absent.failure]
 * ([org.cf.smalivm.core.resolutionFailure]).
 */
internal sealed interface TypeOperand<out T> {
    val type: VirtualType

    class Present<out T>(override val type: VirtualType, val resolved: T) : TypeOperand<T>

    /** [failure] is [org.cf.smalivm.core.GuestThrowables.unresolvedClass] of [type]. */
    class Absent(override val type: VirtualType, val failure: ThrowableRecipe) : TypeOperand<Nothing>
}
