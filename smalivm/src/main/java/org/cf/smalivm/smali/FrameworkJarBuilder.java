package org.cf.smalivm.smali;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import net.bytebuddy.dynamic.DynamicType.Loaded;

public class FrameworkJarBuilder {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        ClassManager classManager = getClassManager();
        ClassBuilder builder = new ClassBuilder(classManager);

        // Get comfortable!
        Set<String> classNames = classManager.getFrameworkClassNames();
        Loaded<?> loaded = builder.buildLoaded(classNames);

        File outPath = new File("framework.jar");
        loaded.toJar(outPath);
        System.out.println("Saved " + loaded.getAllTypes().size() + " classes to " + outPath);
    }

    private static ClassManager getClassManager() throws IOException {
        ClassManagerFactory managerFactory = new ClassManagerFactory();
        return managerFactory.build();
    }

}
