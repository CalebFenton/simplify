package org.cf.simplify;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.FileOptionHandler;
import org.kohsuke.args4j.spi.PatternOptionHandler;

public class Options implements Serializable {

    private static final long serialVersionUID = -8592147369856820020L;

    @Option(name = "-h", aliases = { "--help" }, usage = "Be helpful", help = true)
    private boolean help;

    @Option(name = "-et", aliases = { "--exclude-types" }, metaVar = "regex", handler = PatternOptionHandler.class,
                    usage = "Don't simplify matching types. Applied after include.")
    private Pattern excludeFilter;

    @Option(name = "-it", aliases = { "--include-types" }, metaVar = "regex", handler = PatternOptionHandler.class,
                    usage = "Only simplify type signatures matching regex.")
    private Pattern includeFilter;

    @Option(name = "--max-call-depth", usage = "Limit method call depth. Lower is faster, but misses things.")
    private int maxCallDepth = 50;

    @Option(name = "--max-address-visits", usage = "Maximum visits of particular address. Raise for long loops.")
    private int maxAddressVisits = 10000;

    @Option(name = "--max-method-visits",
                    usage = "Maximum visits over all addresses in method. Higher for longer methods + loops.")
    private int maxMethodVisits = 1_000_000;

    @Option(name = "--max-passes", usage = "Limit optimization passes.")
    private int maxOptimizationPasses = 100;

    @Option(name = "-o", aliases = { "--output" }, metaVar = "output", handler = FileOptionHandler.class,
                    usage = "Output DEX file, default is <infile>_simple.dex")
    private File outFile;

    @Option(name = "--output-api-level", usage = "Output DEX API compatibility level.")
    private int outputAPILevel = 15;

    @Option(name = "-v", aliases = { "--verbose" }, usage = "Be verbose")
    private boolean verbose;

    @Option(name = "-vv", aliases = { "--vverbose" }, usage = "Be very verbose")
    private boolean vverbose;

    @Option(name = "-vvv", aliases = { "--vvverbose" }, usage = "Abandon all hope ye who enter here")
    private boolean vvverbose;

    @Option(name = "-q", aliases = { "--quiet" }, usage = "Be quiet")
    private boolean quiet;

    @Option(name = "--include-support", usage = "Include support library package path")
    private boolean includeSupportLibrary;

    @Option(name = "--remove-weak", usage = "Remove dead code if it may have weak side effects, default true")
    private boolean removeWeak = true;

    private File inFile;
    private File outDexFile;
    private boolean isApk;
    private boolean isDex;

    public Pattern getExcludeFilter() {
        return excludeFilter;
    }

    public Pattern getIncludeFilter() {
        return includeFilter;
    }

    public File getInFile() {
        return inFile;
    }

    public int getMaxAddressVisits() {
        return maxAddressVisits;
    }

    public int getMaxCallDepth() {
        return maxCallDepth;
    }

    public int getMaxMethodVisits() {
        return maxMethodVisits;
    }

    public int getMaxOptimizationPasses() {
        return maxOptimizationPasses;
    }

    public File getOutDexFile() {
        return outDexFile;
    }

    public File getOutFile() {
        return outFile;
    }

    public int getOutputAPILevel() {
        return outputAPILevel;
    }

    public boolean includeSupportLibrary() {
        return includeSupportLibrary;
    }

    public boolean isApk() {
        return isApk;
    }

    public boolean isDex() {
        return isDex;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public boolean isRemoveWeak() {
        return removeWeak;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isVverbose() {
        return vverbose;
    }

    public boolean isVvverbose() {
        return vvverbose;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Include support library: ").append(includeSupportLibrary).append('\n');
        sb.append("Max address visits: ").append(getMaxAddressVisits()).append('\n');
        sb.append("Max call depth: ").append(getMaxCallDepth()).append('\n');
        sb.append("Max method visits: ").append(getMaxMethodVisits()).append('\n');
        sb.append("Max optimization passes: ").append(getMaxOptimizationPasses()).append('\n');
        sb.append("Output API level: ").append(getOutputAPILevel()).append('\n');
        sb.append("Exclude filter: ").append(getExcludeFilter()).append('\n');
        sb.append("Include filter: ").append(getIncludeFilter()).append('\n');
        sb.append("Input file: ").append(getInFile()).append('\n');
        sb.append("Output file: ").append(getOutFile());

        return sb.toString();
    }

    private void determineInputType() {
        String type = FilenameUtils.getExtension(inFile.getAbsolutePath()).toLowerCase();
        if ("apk".equals(type)) {
            isApk = true;
        } else if ("dex".equals(type)) {
            isDex = true;
        }
    }

    @Option(name = "-i", aliases = { "--input" }, metaVar = "input", handler = FileOptionHandler.class,
                    usage = "Input SMALI file or folder", required = true)
    private void setInFile(File inFile) {
        this.inFile = inFile;
        determineInputType();

        String baseName = FilenameUtils.getBaseName(inFile.toString());
        if (isApk) {
            String ext = FilenameUtils.getExtension(inFile.toString());
            if (!ext.startsWith(".")) {
                ext = "." + ext;
            }
            outFile = new File(baseName + "_simple.apk");
            try {
                outDexFile = File.createTempFile("simplify", "dex");
            } catch (IOException e) {
                System.err.println("Could not create temp file.\n" + e);
                System.exit(-1);
            }
        } else {
            outDexFile = new File(baseName + "_simple.dex");
            outFile = outDexFile;
        }
    }

}
