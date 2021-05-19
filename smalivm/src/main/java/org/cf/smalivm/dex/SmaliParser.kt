package org.cf.smalivm.dex

import com.google.common.base.Charsets
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.RecognitionException
import org.antlr.runtime.tree.CommonTreeNodeStream
import org.cf.util.Utils
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.writer.builder.BuilderClassDef
import org.jf.dexlib2.writer.builder.DexBuilder
import org.jf.smali.smaliFlexLexer
import org.jf.smali.smaliParser
import org.jf.smali.smaliTreeWalker
import org.slf4j.LoggerFactory
import java.io.*

object SmaliParser {
    const val DEX_API_LEVEL = 20
    private val log = LoggerFactory.getLogger(SmaliParser::class.java.simpleName)

    @Throws(Exception::class)
    fun parse(path: String): List<BuilderClassDef> {
        val dexBuilder = DexBuilder(Opcodes.forApi(DEX_API_LEVEL))
        return parseFiles(File(path), dexBuilder)
    }

    @Throws(Exception::class)
    fun parseFiles(file: File, dexBuilder: DexBuilder): List<BuilderClassDef> {
        val smaliFiles: MutableList<File>
        if (file.isDirectory) {
            smaliFiles = Utils.getFilesWithSmaliExtension(file)
        } else {
            smaliFiles = ArrayList()
            smaliFiles.add(file)
        }
        return parseFiles(smaliFiles, dexBuilder)
    }

    @Throws(Exception::class)
    fun parseFiles(smaliFiles: List<File>, dexBuilder: DexBuilder): List<BuilderClassDef> {
        val result: MutableList<BuilderClassDef> = ArrayList()
        for (smaliFile in smaliFiles) {
            result.add(parse(smaliFile, dexBuilder))
        }
        return result
    }

    @Throws(FileNotFoundException::class, RecognitionException::class)
    fun parse(smaliFile: File, dexBuilder: DexBuilder): BuilderClassDef {
        return parse(smaliFile.absolutePath, FileInputStream(smaliFile), dexBuilder)
    }

    @Throws(RecognitionException::class)
    fun parse(path: String, inputStream: InputStream, dexBuilder: DexBuilder): BuilderClassDef {
        val smaliFile = File(path)
        val reader = InputStreamReader(inputStream, Charsets.UTF_8)
        val lexer = smaliFlexLexer(reader, DEX_API_LEVEL)
        lexer.setSourceFile(smaliFile)
        val tokens = CommonTokenStream(lexer)
        val parser = smaliParser(tokens)
        parser.setApiLevel(DEX_API_LEVEL)
        val result = parser.smali_file()
        if (parser.numberOfSyntaxErrors > 0 || lexer.numberOfSyntaxErrors > 0) {
            throw RuntimeException("Unable to parse: $smaliFile")
        }
        val t = result.tree
        val treeStream = CommonTreeNodeStream(t)
        treeStream.tokenStream = tokens
        val dexGen = smaliTreeWalker(treeStream)
        dexGen.setVerboseErrors(false)
        dexGen.setDexBuilder(dexBuilder)
        val classDef = dexGen.smali_file() as BuilderClassDef
        if (dexGen.numberOfSyntaxErrors != 0) {
            throw RuntimeException("Unable to walk: $smaliFile")
        }
        return classDef
    }

    @Throws(Exception::class)
    fun parse(path: String, dexBuilder: DexBuilder): List<BuilderClassDef> {
        return parseFiles(File(path), dexBuilder)
    }
}