package org.cf.simplify;

import java.io.IOException;

import org.cf.smalivm.VirtualMachineFactory;
import org.cf.smalivm.exception.UnhandledVirtualException;

public class Main {

    private static Launcher launcher;

    public static void main(String[] args) throws IOException, UnhandledVirtualException {
        createLauncher();
        launcher.run(args);
    }

    static void setLauncher(Launcher launcher) {
        Main.launcher = launcher;
    }

    private static void createLauncher() {
        if (launcher == null) {
            VirtualMachineFactory vmFactory = new VirtualMachineFactory();
            launcher = new Launcher(vmFactory);
        }
    }

}
