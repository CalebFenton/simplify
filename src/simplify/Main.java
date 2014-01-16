package simplify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.apache.commons.io.FileUtils;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.jf.smali.LexerErrorInterface;
import org.jf.smali.smaliFlexLexer;
import org.jf.smali.smaliParser;
import org.jf.smali.smaliTreeWalker;

import simplify.exec.MaxNodeVisitsExceeded;
import simplify.exec.MethodExecutor;
import simplify.graph.CallGraphBuilder;
import simplify.graph.InstructionNode;

import com.google.common.collect.LinkedListMultimap;

public class Main {

    private static Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final Level LOG_LEVEL = Level.INFO;

    private static final int API_LEVEL = 15;
    private static final int MAX_NODE_VISITS = 10000;
    private static final int MAX_CALL_DEPTH = 10;

    public static void main(String[] argv) throws Exception {
        setupLogger();

        List<String> files = new ArrayList<String>();
        files.add(argv[0]);

        DexBuilder dexBuilder = DexBuilder.makeDexBuilder(API_LEVEL);
        List<BuilderClassDef> classes = new ArrayList<BuilderClassDef>();
        for (String file : files) {
            File smaliFile = new File(file);
            log.info("Dexifying: " + smaliFile);

            BuilderClassDef classDef = dexifySmaliFile(smaliFile, dexBuilder);
            classes.add(classDef);
        }

        List<BuilderMethod> methods = new ArrayList<BuilderMethod>();
        for (BuilderClassDef classDef : classes) {
            methods.addAll(classDef.getMethods());
        }

        MethodExecutor me = new MethodExecutor(MAX_NODE_VISITS, MAX_CALL_DEPTH);
        for (int i = 0; i < methods.size();) {
            BuilderMethod method = methods.get(i);

            LinkedListMultimap<Integer, InstructionNode> nodes;
            try {
                nodes = me.execute(classes, method);
            } catch (MaxNodeVisitsExceeded e) {
                log.warning("Skipping " + method.getName() + "\n" + e.getMessage());
                i++;
                continue;
            }

            if (MethodSimplifier.simplify(dexBuilder, method, nodes)) {
                log.info("Changes were made simplifying " + method.getName() + ", repeating...");
                continue;
            }

            i++;
        }

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

    private static Map<BuilderMethod, InstructionNode> buildCallGraphs(BuilderClassDef classDef) {
        Map<BuilderMethod, InstructionNode> result = new HashMap<BuilderMethod, InstructionNode>();
        for (BuilderMethod method : classDef.getMethods()) {
            InstructionNode rootNode = CallGraphBuilder.build(method);

            String graphs = rootNode.toGraph();
            try {
                FileUtils.write(new File("graphs/" + method.getDefiningClass() + "-" + method.getName() + ".txt"),
                                graphs);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
