package org.cf.simplify;

import org.cf.smalivm.UnhandledVirtualException;
import org.cf.smalivm.VirtualMachineFactory;

import java.io.File;
import java.io.IOException;

public class Main {

    private static Launcher launcher;

    public static File errorDexFile;

    public static void main(String[] args) throws IOException, UnhandledVirtualException {
        while (true) {
            try {
                setupLauncher();
                launcher.run(args);
                break;
            } catch (TryAgainException e) {
                System.out.println("simplify crashed - trying to resume");
            }
        }
    }

    static void setLauncher(Launcher launcher) {
        Main.launcher = launcher;
    }

    private static void setupLauncher() {
        if (launcher == null) {
            VirtualMachineFactory vmFactory = new VirtualMachineFactory();
            launcher = new Launcher(vmFactory);
        }
    }
}
