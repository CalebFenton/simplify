package simplify;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.jf.smali.LexerErrorInterface;
import org.jf.smali.smaliFlexLexer;
import org.jf.smali.smaliParser;
import org.jf.smali.smaliTreeWalker;

import simplify.exec.VirtualExecutor;
import simplify.graph.CallGraphBuilder;
import simplify.graph.Node;

public class Simplifier {
    private static Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private static final Level LOG_LEVEL = Level.FINEST;

    private static final int API_LEVEL = 15;
    private static final int MAX_LOCAL_JUMPS = 100;
    private static final int MAX_CALL_DEPTH = 5;

    public static void main(String[] argv) throws Exception {
        setupLogger();

        List<String> files = new ArrayList<String>();
        files.add(argv[0]);

        DexBuilder dexBuilder = DexBuilder.makeDexBuilder(API_LEVEL);
        Map<BuilderMethod, Node> callGraphs = new HashMap<BuilderMethod, Node>();
        for (String file : files) {
            File smaliFile = new File(file);
            log.info("Simplifying: " + smaliFile);

            BuilderClassDef classDef = dexifySmaliFile(smaliFile, dexBuilder);
            callGraphs.putAll(buildCallGraphs(classDef));
        }

        VirtualExecutor ve = new VirtualExecutor(callGraphs, MAX_LOCAL_JUMPS, MAX_CALL_DEPTH);
        ve.execute();

        String outputDexFile = "out_simple.dex";
        log.info("Writing result to " + outputDexFile);
        dexBuilder.writeTo(new FileDataStore(new File(outputDexFile)));
    }

    private static BuilderClassDef dexifySmaliFile(File smaliFile, DexBuilder dexBuilder) throws Exception {

        FileInputStream fis = new FileInputStream(smaliFile.getAbsolutePath());
        InputStreamReader reader = new InputStreamReader(fis, "UTF-8");

        LexerErrorInterface lexer = new smaliFlexLexer(reader);
        ((smaliFlexLexer) lexer).setSourceFile(smaliFile);
        CommonTokenStream tokens = new CommonTokenStream((TokenSource) lexer);

        smaliParser parser = new smaliParser(tokens);
        parser.setApiLevel(API_LEVEL);

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

    private static Map<BuilderMethod, Node> buildCallGraphs(BuilderClassDef classDef) {
        Map<BuilderMethod, Node> result = new HashMap<BuilderMethod, Node>();
        for (BuilderMethod method : classDef.getMethods()) {
            Node rootNode = CallGraphBuilder.build(method);
            result.put(method, rootNode);
        }

        return result;
    }

    private static void setupLogger() {
        log.setLevel(LOG_LEVEL);
        for (Handler handler : Logger.getLogger("").getHandlers()) {
            handler.setLevel(LOG_LEVEL);
        }
    }

}
