package org.cf.smalivm.core

/** Opaque stable position scoped to one logical method body. */
internal class LogicalCodePosition private constructor(
    private val bodyIdentity: Any,
    internal val ordinal: Int,
) {
    override fun equals(other: Any?): Boolean =
        other is LogicalCodePosition && bodyIdentity === other.bodyIdentity && ordinal == other.ordinal

    override fun hashCode(): Int = 31 * System.identityHashCode(bodyIdentity) + ordinal

    override fun toString(): String = "position[$ordinal]"

    companion object {
        fun forBody(positionCount: Int): List<LogicalCodePosition> {
            require(positionCount > 0) { "A logical method body needs an end position" }
            val identity = Any()
            return List(positionCount) { ordinal -> LogicalCodePosition(identity, ordinal) }
        }
    }
}
