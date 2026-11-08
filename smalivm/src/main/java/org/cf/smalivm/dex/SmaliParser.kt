package org.cf.smalivm.dex

import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.tree.CommonTreeNodeStream
import com.android.tools.smali.dexlib2.writer.builder.BuilderClassDef
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import com.android.tools.smali.smali.smaliFlexLexer
import com.android.tools.smali.smali.smaliParser
import com.android.tools.smali.smali.smaliTreeWalker

/** Assembles Smali source into dexlib class definitions. */
public object SmaliParser {
    /** API level used when assembling Smali. */
    public const val DEX_API_LEVEL: Int = 20

    /** The Smali source did not lex, parse, or assemble; the reasons are on standard error. */
    internal class SyntaxException internal constructor(message: String) : RuntimeException(message)

    /** Assembles one Smali class with [apiLevel]'s opcode table, interning its references in [dexBuilder]. */
    internal fun parse(
        path: String,
        inputStream: InputStream,
        dexBuilder: DexBuilder,
        apiLevel: Int = DEX_API_LEVEL,
    ): BuilderClassDef {
        val reader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
        val lexer = smaliFlexLexer(reader, apiLevel)
        lexer.setSourceFile(File(path))
        val tokens = CommonTokenStream(lexer)
        val parser = smaliParser(tokens)
        parser.setApiLevel(apiLevel)
        val result = parser.smali_file()
        val parseErrors = parser.numberOfSyntaxErrors + lexer.numberOfSyntaxErrors
        if (parseErrors > 0) throw SyntaxException("Unable to parse $path: $parseErrors syntax errors")
        val treeStream = CommonTreeNodeStream(result.tree)
        treeStream.tokenStream = tokens
        val dexGen = smaliTreeWalker(treeStream)
        dexGen.setVerboseErrors(false)
        dexGen.setApiLevel(apiLevel)
        dexGen.setDexBuilder(dexBuilder)
        val classDef = dexGen.smali_file() as BuilderClassDef?
            ?: throw SyntaxException("Unable to assemble $path: its class is already defined in this builder")
        if (dexGen.numberOfSyntaxErrors != 0) {
            throw SyntaxException("Unable to assemble $path: ${dexGen.numberOfSyntaxErrors} errors")
        }
        return classDef
    }
}
