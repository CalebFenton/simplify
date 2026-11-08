package org.cf.smalivm.core

/** Ordered anchors for one submitted insertion. */
internal class InsertedInstructionAnchors internal constructor(anchors: List<PendingCodeAnchor>) {
    internal val anchors: List<PendingCodeAnchor> = java.util.Collections.unmodifiableList(anchors.toList())
}
