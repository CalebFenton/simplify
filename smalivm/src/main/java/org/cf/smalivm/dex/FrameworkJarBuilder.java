package org.cf.smalivm.dex;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerFactory;
import org.jf.dexlib2.iface.ClassDef;

public class FrameworkJarBuilder {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        ClassManager classManager = getClassManager();
        ClassBuilder builder = new ClassBuilder();

        String outPath = "android-framework.jar";
        JarOutputStream out = new JarOutputStream(new FileOutputStream(outPath));
        Set<String> classNames = classManager.getFrameworkClassNames();
        for (String className : classNames) {
            VirtualClass virtualClass = classManager.getVirtualClass(className);
            ClassDef classDef = virtualClass.getClassDef();
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
