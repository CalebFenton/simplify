package org.cf.simplify;

import java.io.IOException;

import org.cf.smalivm.VirtualMachineFactory;
import org.cf.smalivm.UnhandledVirtualException;

public class Main {

    private static Launcher launcher;

    public static void main(String[] args) throws IOException, UnhandledVirtualException {
        setupLauncher();
        launcher.run(args);
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
