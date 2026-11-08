package org.cf.smalivm.core

/** One pending start-inclusive, end-exclusive protected range and its ordered handlers. */
internal class TryRegionSpec(
    internal val start: PendingCodeAnchor,
    internal val end: PendingCodeAnchor,
    catchHandlers: List<CatchHandlerSpec>,
) {
    internal val catchHandlers: List<CatchHandlerSpec> =
        java.util.Collections.unmodifiableList(catchHandlers.toList())
}
