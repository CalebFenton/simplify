package org.cf.smalivm.smali;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.jf.dexlib2.iface.ClassDef;

public class FrameworkJarBuilder {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        ClassManager classManager = getClassManager();
        ClassBuilder builder = new ClassBuilder();

        String outPath = "framework.jar";
        JarOutputStream out = new JarOutputStream(new FileOutputStream(outPath));
        Set<String> classNames = classManager.getFrameworkClassNames();
        for (String className : classNames) {
            ClassDef classDef = classManager.getClass(className);
            byte[] b = builder.build(classDef);

            String entryName = className.substring(1, className.length() - 1);
            out.putNextEntry(new ZipEntry(entryName + ".class"));
            out.write(b);
            out.closeEntry();
        }
        out.close();

        System.out.println("Saved " + classNames.size() + " classes to " + outPath);
    }

    private static ClassManager getClassManager() throws IOException {
        ClassManagerFactory managerFactory = new ClassManagerFactory();
        return managerFactory.build();
    }

}
