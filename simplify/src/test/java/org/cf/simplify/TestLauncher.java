package org.cf.simplify;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.cf.smalivm.VirtualMachineFactory;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.junit.Test;

public class TestLauncher {

    @Test
    public void runsWithoutMajorFailureWithSmaliFolder() throws IOException, UnhandledVirtualException {
        Launcher launcher = new Launcher(new VirtualMachineFactory());
        File outFile = File.createTempFile("simplify-test", ".tmp");
        outFile.deleteOnExit();
        launcher.run(new String[] { "-i", "obfuscated-example", "-it", "WhiteNoise", "-o", outFile.getAbsolutePath() });

        assertTrue(outFile.exists());
    }

    @Test
    public void runsWithoutMajorFailureWithDexFile() throws IOException, UnhandledVirtualException {
        Launcher launcher = new Launcher(new VirtualMachineFactory());
        File outFile = File.createTempFile("simplify-test", ".tmp");
        outFile.deleteOnExit();
        launcher.run(new String[] {
                        "-i", "resources/test/obfuscated-example.dex", "-it", "WhiteNoise", "-o",
                        outFile.getAbsolutePath() });

        assertTrue(outFile.exists());
    }

}
