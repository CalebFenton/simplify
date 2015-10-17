package org.cf.smalivm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.writer.builder.DexBuilder;

public class ClassManagerFactory {

    private static final String TEMP_DIR_NAME = "simplify";

    /**
     * 
     * @param smaliPath
     *            Path to Smali file or folder
     * @param dexBuilder
     * @return
     * @throws IOException
     */
    public ClassManager build(File inFile, DexBuilder dexBuilder) throws IOException {
        File smaliPath;
        if (inFile.isFile()) {
            smaliPath = disassemble(inFile);
        } else {
            smaliPath = inFile;
        }

        return new ClassManager(smaliPath, dexBuilder);
    }

    /**
     * 
     * @param smaliPath
     *            Path to Smali file or folder
     * @return
     * @throws IOException
     */
    public ClassManager build(String smaliPath) throws IOException {
        return build(smaliPath, DexBuilder.makeDexBuilder());
    }

    /**
     * 
     * @param smaliPath
     *            Path to Smali file or folder
     * @param outputAPILevel
     * @return
     * @throws IOException
     */
    public ClassManager build(String smaliPath, int outputAPILevel) throws IOException {
        return build(smaliPath, DexBuilder.makeDexBuilder(Opcodes.forApi(outputAPILevel)));
    }

    /**
     * 
     * @param smaliPath
     *            Path to Smali file or folder
     * @param outputAPILevel
     * @return
     * @throws IOException
     */
    public ClassManager build(File smaliPath, int outputAPILevel) throws IOException {
        return build(smaliPath, DexBuilder.makeDexBuilder(Opcodes.forApi(outputAPILevel)));
    }

    /**
     * 
     * @param smaliPath
     *            Path to Smali file or folder
     * @param dexBuilder
     * @return
     * @throws IOException
     */
    public ClassManager build(String smaliPath, DexBuilder dexBuilder) throws IOException {
        return build(new File(smaliPath), dexBuilder);
    }

    private static File disassemble(File file) throws IOException {
        Path tempDir = Files.createTempDirectory(TEMP_DIR_NAME);
        String[] args = new String[] {
                        "--use-locals", "--sequential-labels", "--code-offsets", file.getAbsolutePath(), "--output",
                        tempDir.toString(), };
        org.jf.baksmali.main.main(args);

        return tempDir.toFile();
    }

}
