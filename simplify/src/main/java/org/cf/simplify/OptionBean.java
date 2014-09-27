package org.cf.simplify;

import java.io.File;
import java.io.Serializable;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.FileOptionHandler;
import org.kohsuke.args4j.spi.PatternOptionHandler;

public class OptionBean implements Serializable {

    private static final long serialVersionUID = -8592147369856820020L;

    @Option(name = "-c", aliases = { "--class-filter" }, metaVar = "regex", handler = PatternOptionHandler.class, usage = "Only simplify classes matching filter")
    private Pattern classFilter;

    @Option(name = "-h", aliases = { "--help" }, usage = "Be helpful", help = true)
    private boolean help;

    private File inFile;

    @Option(name = "--max-call-depth", usage = "Limit method call depth. Lower is faster, but misses things.")
    private int maxCallDepth = 20;

    @Option(name = "--max-node-visits", usage = "Limit instruction visitation. Lower is faster, but misses things.")
    private int maxNodeVisits = 2000;

    @Option(name = "--max-passes", usage = "Limit optimization passes.")
    private int maxOptimizationPasses = 100;

    @Option(name = "-m", aliases = { "--method-filter" }, metaVar = "regex", handler = PatternOptionHandler.class, usage = "Only simplify methods matching filter")
    private Pattern methodFilter;

    @Option(name = "-o", aliases = { "--output" }, metaVar = "output", handler = FileOptionHandler.class, usage = "Output DEX file, default is <infile>_simple.dex")
    private File outFile;

    @Option(name = "--output-api-level", usage = "Output DEX API compatibility level.")
    private int outputAPILevel = 15;

    @Option(name = "-v", aliases = { "--verbose" }, usage = "Be verbose")
    private boolean verbose;

    @Option(name = "-vv", aliases = { "--vverbose" }, usage = "Be very verbose")
    private boolean vverbose;

    public Pattern getClassFilter() {
        return classFilter;
    }

    public File getInFile() {
        return inFile;
    }

    public int getMaxCallDepth() {
        return maxCallDepth;
    }

    public int getMaxNodeVisits() {
        return maxNodeVisits;
    }

    public int getMaxOptimizationPasses() {
        return maxOptimizationPasses;
    }

    public Pattern getMethodFilter() {
        return methodFilter;
    }

    public File getOutFile() {
        return outFile;
    }

    public int getOutputAPILevel() {
        return outputAPILevel;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isVverbose() {
        return vverbose;
    }

    @Option(name = "-i", aliases = { "--input" }, metaVar = "input", handler = FileOptionHandler.class, usage = "Input SMALI file or folder", required = true)
    private void setInFile(File inFile) {
        this.inFile = inFile;

        // Output will be in current directory
        String baseName = FilenameUtils.getBaseName(inFile.toString());
        outFile = new File(baseName + "_simple.dex");
    }

}
