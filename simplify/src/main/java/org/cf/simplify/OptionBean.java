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

    @Option(name = "-h", aliases = { "--help" }, usage = "Be helpful", help = true)
    private boolean help;

    @Option(name = "-et", aliases = { "--exclude-types" }, metaVar = "regex", handler = PatternOptionHandler.class, usage = "Don't simplify matching types. Aapplied after include.")
    private Pattern excludeFilter;

    @Option(name = "-it", aliases = { "--include-types" }, metaVar = "regex", handler = PatternOptionHandler.class, usage = "Only simplify type signatures matching regex.")
    private Pattern includeFilter;

    @Option(name = "--max-call-depth", usage = "Limit method call depth. Lower is faster, but misses things.")
    private int maxCallDepth = 20;

    @Option(name = "--max-address-visits", usage = "Maximum visits of particular address. Raise for long loops.")
    private int maxAddressVisits = 500;

    @Option(name = "--max-method-visits", usage = "Maximum visits over all addresses in method. Higher for longer methods + loops.")
    private int maxMethodVisits = maxAddressVisits * 1000; // assuming ~1000 instructions per method

    @Option(name = "--max-passes", usage = "Limit optimization passes.")
    private int maxOptimizationPasses = 100;

    @Option(name = "-o", aliases = { "--output" }, metaVar = "output", handler = FileOptionHandler.class, usage = "Output DEX file, default is <infile>_simple.dex")
    private File outFile;

    @Option(name = "--output-api-level", usage = "Output DEX API compatibility level.")
    private int outputAPILevel = 15;

    @Option(name = "-v", aliases = { "--verbose" }, usage = "Be verbose")
    private boolean verbose;

    @Option(name = "-vv", aliases = { "--vverbose" }, usage = "Be very verbose")
    private boolean vverbose;

    private File inFile;

    public File getInFile() {
        return inFile;
    }

    public int getMaxCallDepth() {
        return maxCallDepth;
    }

    public int getMaxAddressVisits() {
        return maxAddressVisits;
    }

    public int getMaxMethodVisits() {
        return maxMethodVisits;
    }

    public int getMaxOptimizationPasses() {
        return maxOptimizationPasses;
    }

    public Pattern getExcludeFilter() {
        return excludeFilter;
    }

    public Pattern getIncludeFilter() {
        return includeFilter;
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
