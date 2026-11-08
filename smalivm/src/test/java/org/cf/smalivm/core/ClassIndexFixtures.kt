package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path

internal fun classIndexFromSource(root: Path, source: String): ClassIndex {
    Files.writeString(root.resolve("fixture.smali"), source.trimIndent())
    return ClassIndex.open(root.toFile())
}
