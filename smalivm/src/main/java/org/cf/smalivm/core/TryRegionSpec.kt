package org.cf.smalivm.core

/** One pending start-inclusive, end-exclusive protected range and its ordered handlers. */
class TryRegionSpec(
    val start: PendingCodeAnchor,
    val end: PendingCodeAnchor,
    catchHandlers: List<CatchHandlerSpec>,
) {
    val catchHandlers: List<CatchHandlerSpec> =
        java.util.Collections.unmodifiableList(catchHandlers.toList())
}
