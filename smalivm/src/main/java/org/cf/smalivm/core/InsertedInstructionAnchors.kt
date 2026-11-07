package org.cf.smalivm.core

/** Ordered anchors for one submitted insertion. */
class InsertedInstructionAnchors internal constructor(anchors: List<PendingCodeAnchor>) {
    val anchors: List<PendingCodeAnchor> = java.util.Collections.unmodifiableList(anchors.toList())
}
