package org.cf.smalivm.type;

import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.writer.builder.DexBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClassManagerFactory {

    private static final String TEMP_DIR_NAME = "simplify";

    private static File disassemble(File file) throws IOException {
        Path tempDir = Files.createTempDirectory(TEMP_DIR_NAME);
        String[] args = new String[]{
            "disassemble", "--use-locals", "--sequential-labels", "--code-offsets", file.getAbsolutePath(), "--output", tempDir.toString(),
        };
        org.jf.baksmali.Main.main(args);

        return tempDir.toFile();
    }

    /**
     * Create a ClassManager with no input Smali classes. It will only contain parse library
     * classes.
     */
    public ClassManager build() throws IOException {
        DexBuilder dexBuilder = new DexBuilder(Opcodes.getDefault());

        return build(dexBuilder);
    }

    /**
     * Create a ClassManager with no input Smali classes. It will only contain parse library
     * classes.
     */
    public ClassManager build(int outputAPILevel) throws IOException {
        DexBuilder dexBuilder = new DexBuilder(Opcodes.forApi(outputAPILevel));

        return build(dexBuilder);
    }

    /**
     * Create a ClassManager with no input Smali classes. It will only contain input library
     * classes.
     */
    public ClassManager build(DexBuilder dexBuilder)  {
        return new ClassManagerImpl(dexBuilder);
    }

    public ClassManager build(File inFile, DexBuilder dexBuilder) throws IOException {
        File smaliPath;
        if (!inFile.exists()) {
            throw new RuntimeException("Input Smali path does not exist: " + inFile);
        }
        if (inFile.isFile()) {
            smaliPath = disassemble(inFile);
        } else {
            smaliPath = inFile;
        }

        return new ClassManagerImpl(dexBuilder, smaliPath);
    }

    /**
     * @param smaliPath Path to Smali file or folder
     */
    public ClassManager build(String smaliPath) throws IOException {
        return build(smaliPath, new DexBuilder(Opcodes.getDefault()));
    }

    /**
     * @param smaliPath Path to Smali file or folder
     */
    public ClassManager build(String smaliPath, int outputAPILevel) throws IOException {
        return build(new File(smaliPath), outputAPILevel);
    }

    /**
     * @param smaliPath Path to Smali file or folder
     */
    public ClassManager build(File smaliPath, int outputAPILevel) throws IOException {
        return build(smaliPath, new DexBuilder(Opcodes.forApi(outputAPILevel)));
    }

    /**
     * @param smaliPath Path to Smali file or folder
     */
    public ClassManager build(String smaliPath, DexBuilder dexBuilder) throws IOException {
        return build(new File(smaliPath), dexBuilder);
    }
}
