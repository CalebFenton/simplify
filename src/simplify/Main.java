package simplify;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.jf.smali.LexerErrorInterface;
import org.jf.smali.smaliFlexLexer;
import org.jf.smali.smaliParser;
import org.jf.smali.smaliTreeWalker;

import simplify.optimize.Simplifier;
import simplify.vm.ContextGraph;
import simplify.vm.VirtualMachine;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final Level LOG_LEVEL = Level.FINE;

    private static final int API_LEVEL = 15;
    private static final int MAX_NODE_VISITS = 1000;
    private static final int MAX_CALL_DEPTH = 10;

    public static void main(String[] argv) throws Exception {
        setupLogger();

        List<File> files = new ArrayList<File>();
        File f = new File(argv[0]);
        if (f.isDirectory()) {
            files.addAll(Arrays.asList(f.listFiles()));
        } else {
            files.add(f);
        }

        DexBuilder dexBuilder = DexBuilder.makeDexBuilder(API_LEVEL);
        List<BuilderClassDef> classDefs = new ArrayList<BuilderClassDef>();
        List<BuilderMethod> methods = new ArrayList<BuilderMethod>();
        for (File file : files) {
            log.info("Dexifying: " + file);

            BuilderClassDef classDef = dexifySmaliFile(file, dexBuilder);
            classDefs.add(classDef);
            methods.addAll(classDef.getMethods());
        }

        VirtualMachine vm = new VirtualMachine(classDefs, MAX_NODE_VISITS, MAX_CALL_DEPTH);

        outer: for (BuilderMethod method : methods) {
            String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);

            boolean madeChanges = false;
            int sweeps = 0;
            do {
                if (sweeps >= 3) {
                    // break;
                }

                ContextGraph graph = vm.execute(methodDescriptor);
                if (graph == null) {
                    log.info("Skipping " + methodDescriptor);
                    continue outer;
                }

                // String methodName = method.getName();
                // FileUtils.writeStringToFile(new File("graphs/" + methodName + ".dot"), graph.toGraph());

                madeChanges = Simplifier.simplify(dexBuilder, method, graph);

                if (madeChanges) {
                    // Method implementations will have changed, so prepare to execute this again with the changes.
                    vm.updateInstructionGraph(method);
                }

                sweeps++;
            } while (madeChanges);
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

    private static void setupLogger() {
        log.setLevel(LOG_LEVEL);

        for (Handler handler : Logger.getLogger("").getHandlers()) {
            handler.setLevel(LOG_LEVEL);
        }
    }

}
