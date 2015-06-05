package org.cf.simplify;

import java.io.IOException;

import org.cf.smalivm.exception.UnhandledVirtualException;

public class Main {

    private static Launcher launcher = new Launcher();

    public static void main(String[] args) throws IOException, UnhandledVirtualException {
        launcher.run(args);
    }

    static void setLauncher(Launcher launcher) {
        Main.launcher = launcher;
    }

}
