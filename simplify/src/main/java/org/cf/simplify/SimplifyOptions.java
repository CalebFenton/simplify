package org.cf.simplify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.cf.util.Dexifier;

public class SimplifyOptions implements Serializable {

    private static final byte[] DEX_MAGIC = new byte[] { 0x64, 0x65, 0x78 };

    private static final byte[] PK_ZIP_MAGIC = new byte[] { 0x50, 0x4b, 0x3 };;

    private static final long serialVersionUID = -8592147369856820020L;
    private Pattern excludeFilter;

    private boolean help;
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
    private int outputAPILevel = Dexifier.DEFAULT_API_LEVEL;
    private boolean quiet;
    private boolean removeWeak = true;
    private int verbosity = 0;

    private InputType determineInputType() throws IOException {
        if (inFile.isDirectory()) {
            return InputType.DIRECTORY;
        }

        FileInputStream fis = new FileInputStream(inFile);
        byte[] buf = new byte[3];
        try {
            fis.read(buf);
        } finally {
            fis.close();
        }

        if (Arrays.equals(DEX_MAGIC, buf)) {
            return InputType.DEX;
        } else if (Arrays.equals(PK_ZIP_MAGIC, buf)) {
            return InputType.ZIP;
        } else {
            throw new RuntimeException("Unknown input file type. Magic: " + Arrays.toString(buf));
        }
    }

    protected void setExcludeFilter(Pattern excludeFilter) {
        this.excludeFilter = excludeFilter;
    }

    protected void setIncludeFilter(Pattern includeFilter) {
        this.includeFilter = includeFilter;
    }

    protected void setIncludeSupportLibrary(boolean includeSupportLibrary) {
        this.includeSupportLibrary = includeSupportLibrary;
    }

    protected void setIsHelp() {
        help = true;
    }

    protected void setIsQuiet() {
        quiet = true;
    }

    protected void setMaxAddressVisits(int maxAddressVisits) {
        this.maxAddressVisits = maxAddressVisits;
    }

    protected void setMaxCallDepth(int maxCallDepth) {
        this.maxCallDepth = maxCallDepth;
    }

    protected void setMaxExecutionTime(int maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }

    protected void setMaxMethodVisits(int maxMethodVisits) {
        this.maxMethodVisits = maxMethodVisits;
    }

    protected void setMaxOptimizationPasses(int maxOptimizationPasses) {
        this.maxOptimizationPasses = maxOptimizationPasses;
    }

    protected void setOutputAPILevel(int outputAPILevel) {
        this.outputAPILevel = outputAPILevel;
    }

    protected void setRemoveWeak(boolean removeWeak) {
        this.removeWeak = removeWeak;
    }

    protected void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    void setInFile(File inFile) {
        this.inFile = inFile;
        try {
            inputType = determineInputType();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public int getMaxExecutionTime() {
        return maxExecutionTime;
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

    public int getVerbosity() {
        return verbosity;
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

    public boolean isQuiet() {
        return quiet;
    }

    public boolean isRemoveWeak() {
        return removeWeak;
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

    private static enum InputType {
        DEX, DIRECTORY, ZIP
    }

}
