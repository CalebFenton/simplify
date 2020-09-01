package org.cf.smalivm.dex;

import com.google.common.base.Charsets;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.cf.util.Utils;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.smali.smaliFlexLexer;
import org.jf.smali.smaliParser;
import org.jf.smali.smaliTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SmaliParser {

    public static final int DEX_API_LEVEL = 20;

    private static final Logger log = LoggerFactory.getLogger(SmaliParser.class.getSimpleName());

    public static List<BuilderClassDef> parse(String path) throws Exception {
        DexBuilder dexBuilder = new DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL));

        return parseFiles(new File(path), dexBuilder);
    }

    public static List<BuilderClassDef> parseFiles(File file, DexBuilder dexBuilder) throws Exception {
        List<File> smaliFiles;
        if (file.isDirectory()) {
            smaliFiles = Utils.getFilesWithSmaliExtension(file);
        } else {
            smaliFiles = new ArrayList<>();
            smaliFiles.add(file);
        }

        return parseFiles(smaliFiles, dexBuilder);
    }

    public static List<BuilderClassDef> parseFiles(List<File> smaliFiles, DexBuilder dexBuilder) throws Exception {
        List<BuilderClassDef> result = new ArrayList<>();
        for (File smaliFile : smaliFiles) {
            result.add(parse(smaliFile, dexBuilder));
        }

        return result;
    }

    public static BuilderClassDef parse(File smaliFile, DexBuilder dexBuilder) throws FileNotFoundException, RecognitionException {
        return parse(smaliFile.getAbsolutePath(), new FileInputStream(smaliFile), dexBuilder);
    }

    public static BuilderClassDef parse(String path, InputStream is, DexBuilder dexBuilder) throws RecognitionException {
        File smaliFile = new File(path);
        InputStreamReader reader = new InputStreamReader(is, Charsets.UTF_8);
        smaliFlexLexer lexer = new smaliFlexLexer(reader, DEX_API_LEVEL);
        lexer.setSourceFile(smaliFile);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        smaliParser parser = new smaliParser(tokens);
        parser.setApiLevel(DEX_API_LEVEL);

        smaliParser.smali_file_return result = parser.smali_file();
        if ((parser.getNumberOfSyntaxErrors() > 0) || (lexer.getNumberOfSyntaxErrors() > 0)) {
            throw new RuntimeException("Unable to parse: " + smaliFile);
        }

        CommonTree t = result.getTree();
        CommonTreeNodeStream treeStream = new CommonTreeNodeStream(t);
        treeStream.setTokenStream(tokens);

        smaliTreeWalker dexGen = new smaliTreeWalker(treeStream);
        dexGen.setVerboseErrors(false);
        dexGen.setDexBuilder(dexBuilder);
        BuilderClassDef classDef = (BuilderClassDef) dexGen.smali_file();
        if (dexGen.getNumberOfSyntaxErrors() != 0) {
            throw new RuntimeException("Unable to walk: " + smaliFile);
        }

        return classDef;
    }

    public static List<BuilderClassDef> parse(String path, DexBuilder dexBuilder) throws Exception {
        return parseFiles(new File(path), dexBuilder);
    }
}
