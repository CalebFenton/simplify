package org.cf.smalivm.dex;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.cf.util.Utils;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.smali.LexerErrorInterface;
import org.jf.smali.smaliFlexLexer;
import org.jf.smali.smaliParser;
import org.jf.smali.smaliTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Dexifier {

    public static final int DEFAULT_API_LEVEL = 15;

    private static final Logger log = LoggerFactory.getLogger(Dexifier.class.getSimpleName());

    public static List<BuilderClassDef> dexifySmaliFiles(String path) throws Exception {
        DexBuilder dexBuilder = DexBuilder.makeDexBuilder(Opcodes.forApi(Dexifier.DEFAULT_API_LEVEL));

        return dexifySmaliFiles(new File(path), dexBuilder);
    }

    public static List<BuilderClassDef> dexifySmaliFiles(File file, DexBuilder dexBuilder) throws Exception {
        List<File> smaliFiles;
        if (file.isDirectory()) {
            smaliFiles = Utils.getFilesWithSmaliExtension(file);
        } else {
            smaliFiles = new ArrayList<>();
            smaliFiles.add(file);
        }

        return dexifySmaliFiles(smaliFiles, dexBuilder);
    }

    public static List<BuilderClassDef> dexifySmaliFiles(List<File> smaliFiles, DexBuilder dexBuilder) throws
            Exception {
        List<BuilderClassDef> result = new ArrayList<>();
        for (File smaliFile : smaliFiles) {
            result.add(dexifySmaliFile(smaliFile, dexBuilder));
        }

        return result;
    }

    public static BuilderClassDef dexifySmaliFile(File smaliFile, DexBuilder dexBuilder) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Dexifying: {}", smaliFile);
        }

        return dexifySmaliFile(smaliFile.getAbsolutePath(), new FileInputStream(smaliFile), dexBuilder);
    }

    public static BuilderClassDef dexifySmaliFile(String filePath, InputStream is, DexBuilder dexBuilder) throws
            Exception {
        File smaliFile = new File(filePath);
        InputStreamReader reader = new InputStreamReader(is, "UTF-8");
        LexerErrorInterface lexer = new smaliFlexLexer(reader);
        ((smaliFlexLexer) lexer).setSourceFile(smaliFile);
        CommonTokenStream tokens = new CommonTokenStream((TokenSource) lexer);

        smaliParser parser = new smaliParser(tokens);
        parser.setApiLevel(DEFAULT_API_LEVEL, false);

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

    public static List<BuilderClassDef> dexifySmaliFiles(String path, DexBuilder dexBuilder) throws Exception {
        return dexifySmaliFiles(new File(path), dexBuilder);
    }

}
