package org.cf.smalivm.core

/** One ordered typed or catch-all handler in a pending try region. */
sealed interface CatchHandlerSpec {
    val target: PendingCodeAnchor

    class Typed(
        val exceptionType: StagedDexReference.Type,
        override val target: PendingCodeAnchor,
    ) : CatchHandlerSpec

    class CatchAll(
        override val target: PendingCodeAnchor,
    ) : CatchHandlerSpec
}
