package org.cf.smalivm.core

/** One ordered typed or catch-all handler in a pending try region. */
internal sealed interface CatchHandlerSpec {
    /** The handler's first instruction. */
    val target: PendingCodeAnchor

    /** Catches throwables assignable to the exception type. */
    class Typed(
        internal val exceptionType: StagedDexReference.Type,
        override val target: PendingCodeAnchor,
    ) : CatchHandlerSpec

    /** Catches every throwable. */
    class CatchAll(
        override val target: PendingCodeAnchor,
    ) : CatchHandlerSpec
}
