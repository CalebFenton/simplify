package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import org.cf.smalivm.dex.SmaliParser
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.ClassManagerFactory
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder

internal fun task53ClassIndex(root: Path, source: String): ClassIndex {
    Files.writeString(root.resolve("task53.smali"), source.trimIndent())
    return ClassIndex(task53ClassManager(root))
}

internal fun task53ClassManager(root: Path): ClassManager = ClassManagerFactory().build(
    root.toString(),
    DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL)),
)
