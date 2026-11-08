package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path

internal fun task53ClassIndex(root: Path, source: String): ClassIndex {
    Files.writeString(root.resolve("task53.smali"), source.trimIndent())
    return ClassIndex.open(root.toFile())
}
