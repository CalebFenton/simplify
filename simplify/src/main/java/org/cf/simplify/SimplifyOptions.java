package org.cf.simplify;

import org.cf.smalivm.dex.SmaliParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Pattern;

public class SimplifyOptions implements Serializable {

    private static final byte[] DEX_MAGIC = new byte[] { 0x64, 0x65, 0x78 };
    private static final byte[] PK_ZIP_MAGIC = new byte[] { 0x50, 0x4b, 0x3 };
    private static final long serialVersionUID = -8592147369856820020L;

    private Pattern excludeFilter;
    private boolean help;
    private boolean ignoreErrors;
    private Pattern includeFilter;
    private boolean includeSupportLibrary;
    private File inFile;
    private InputType inputType;
    private int maxAddressVisits = 10000;
    private int maxCallDepth = 50;
    private int maxExecutionTime = 5 * 60;
    private int maxMethodVisits = 1_000_000;
    private int maxOptimizationPasses = 100;
    private File outDexFile;
    private File outFile;
    private int outputAPILevel = SmaliParser.DEX_API_LEVEL;
    private boolean quiet;
    private boolean removeWeak = true;
    private int verbosity = 0;

    public Pattern getExcludeFilter() {
        return excludeFilter;
    }

    protected void setExcludeFilter(Pattern excludeFilter) {
        this.excludeFilter = excludeFilter;
    }

    public Pattern getIncludeFilter() {
        return includeFilter;
    }

    protected void setIncludeFilter(Pattern includeFilter) {
        this.includeFilter = includeFilter;
    }

    public File getInFile() {
        return inFile;
    }

    void setInFile(File inFile) {
        this.inFile = inFile;
        try {
            inputType = determineInputType();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getMaxAddressVisits() {
        return maxAddressVisits;
    }

    protected void setMaxAddressVisits(int maxAddressVisits) {
        this.maxAddressVisits = maxAddressVisits;
    }

    public int getMaxCallDepth() {
        return maxCallDepth;
    }

    protected void setMaxCallDepth(int maxCallDepth) {
        this.maxCallDepth = maxCallDepth;
    }

    public int getMaxExecutionTime() {
        return maxExecutionTime;
    }

    protected void setMaxExecutionTime(int maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }

    public int getMaxMethodVisits() {
        return maxMethodVisits;
    }

    protected void setMaxMethodVisits(int maxMethodVisits) {
        this.maxMethodVisits = maxMethodVisits;
    }

    public int getMaxOptimizationPasses() {
        return maxOptimizationPasses;
    }

    protected void setMaxOptimizationPasses(int maxOptimizationPasses) {
        this.maxOptimizationPasses = maxOptimizationPasses;
    }

    public File getOutDexFile() {
        return outDexFile;
    }

    public File getOutFile() {
        return outFile;
    }

    void setOutFile(File outFile) {
        this.outFile = outFile;
        if (isZip()) {
            try {
                outDexFile = File.createTempFile("simplify", "dex");
                outDexFile.deleteOnExit();
            } catch (IOException e) {
                throw new RuntimeException("Could not create temporary dex file:\n" + e);
            }
        } else {
            outDexFile = outFile;
        }
    }

    public int getOutputAPILevel() {
        return outputAPILevel;
    }

    protected void setOutputAPILevel(int outputAPILevel) {
        this.outputAPILevel = outputAPILevel;
    }

    public int getVerbosity() {
        return verbosity;
    }

    protected void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    public boolean includeSupportLibrary() {
        return includeSupportLibrary;
    }

    public boolean isDex() {
        return InputType.DEX.equals(inputType);
    }

    public boolean isDirectory() {
        return InputType.DIRECTORY.equals(inputType);
    }

    public boolean isHelp() {
        return help;
    }

    public boolean ignoreErrors() {
        return ignoreErrors;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public boolean isRemoveWeak() {
        return removeWeak;
    }

    protected void setRemoveWeak(boolean removeWeak) {
        this.removeWeak = removeWeak;
    }

    public boolean isZip() {
        return InputType.ZIP.equals(inputType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Input file: ").append(getInFile()).append('\n');
        sb.append("Output file: ").append(getOutFile()).append('\n');
        sb.append("Include filter: ").append(getIncludeFilter()).append('\n');
        sb.append("Exclude filter: ").append(getExcludeFilter()).append('\n');
        sb.append("Max execution time: ").append(getMaxExecutionTime()).append('\n');
        sb.append("Max address visits: ").append(getMaxAddressVisits()).append('\n');
        sb.append("Max call depth: ").append(getMaxCallDepth()).append('\n');
        sb.append("Max method visits: ").append(getMaxMethodVisits()).append('\n');
        sb.append("Max optimization passes: ").append(getMaxOptimizationPasses()).append('\n');
        sb.append("Output API level: ").append(getOutputAPILevel()).append('\n');
        sb.append("Include support library: ").append(includeSupportLibrary).append('\n');

        return sb.toString().trim();
    }

    protected void setIncludeSupportLibrary(boolean includeSupportLibrary) {
        this.includeSupportLibrary = includeSupportLibrary;
    }

    protected void setIsHelp() {
        help = true;
    }

    protected void setIgnoreErrors() {
        ignoreErrors = true;
    }

    protected void setIsQuiet() {
        quiet = true;
    }

    private InputType determineInputType() throws IOException {
        if (inFile.isDirectory()) {
            return InputType.DIRECTORY;
        }

        byte[] buf = new byte[3];
        try (FileInputStream fis = new FileInputStream(inFile)) {
            fis.read(buf);
        }

        if (Arrays.equals(DEX_MAGIC, buf)) {
            return InputType.DEX;
        } else if (Arrays.equals(PK_ZIP_MAGIC, buf)) {
            return InputType.ZIP;
        } else {
            throw new RuntimeException("Unknown input file type; magic: " + Arrays.toString(buf));
        }
    }

    private enum InputType {
        DEX, DIRECTORY, ZIP
    }

}
