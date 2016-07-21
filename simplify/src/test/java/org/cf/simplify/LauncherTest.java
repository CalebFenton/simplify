package org.cf.simplify;

import org.cf.smalivm.UnhandledVirtualException;
import org.cf.smalivm.VirtualMachineFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class LauncherTest {

    @Test
    public void runsWithoutMajorFailureWithSmaliFolder() throws IOException, UnhandledVirtualException {
        Launcher launcher = new Launcher(new VirtualMachineFactory());
        File outFile = File.createTempFile("simplify-test", ".tmp");
        launcher.run(new String[] { "obfuscated-example", "-it", "WhiteNoise", "-o", outFile.getAbsolutePath() });

        assertTrue(outFile.exists());
        Files.delete(outFile.toPath());
    }

    @Test
    public void runsWithoutMajorFailureWithDexFile() throws IOException, UnhandledVirtualException {
        Launcher launcher = new Launcher(new VirtualMachineFactory());
        File outFile = File.createTempFile("simplify-test", ".tmp");
        launcher.run(new String[] { "resources/test/obfuscated-example.zip", "-it", "WhiteNoise", "-o",
                                    outFile.getAbsolutePath() });

        assertTrue(outFile.exists());
        Files.delete(outFile.toPath());
    }

}
