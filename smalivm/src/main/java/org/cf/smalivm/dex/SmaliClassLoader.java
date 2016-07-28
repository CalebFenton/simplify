package org.cf.smalivm.dex;

import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualClass;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.iface.ClassDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class SmaliClassLoader extends ClassLoader {

    private static final Logger log = LoggerFactory.getLogger(SmaliClassLoader.class.getSimpleName());

    // This jar is produced by FrameworkJarBuilder so that framework classes don't have to be built with ASM
    // every time they're needed. Instead, they can be loaded from here.
    private static final String FRAMEWORK_STUBS_JAR = "/framework/android-23.jar";

    private final Map<String, Class<?>> cachedClasses;
    private final ClassBuilder classBuilder;
    private final ClassManager classManager;
    private final URLClassLoader jarLoader;

    public SmaliClassLoader(ClassManager classManager) {
        super(SmaliClassLoader.class.getClassLoader());
        cachedClasses = new HashMap<>();
        URL jarURL = SmaliClassLoader.class.getResource(FRAMEWORK_STUBS_JAR);
        jarLoader = new URLClassLoader(new URL[] { jarURL });
        this.classBuilder = new ClassBuilder();
        this.classManager = classManager;
    }

    private static String getPackageName(String className) {
        int i = className.lastIndexOf('.');
        if (i > 0) {
            return className.substring(0, i);
        } else {
            // No package name, e.g. LsomeClass;
            return null;
        }
    }

    @Override
    protected Class findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
        }

        if (name.startsWith("java.")) {
            log.warn("Unable to load prohibited class name: {}\nThis error is likely the result of using a class " +
                     "which references a java.* class only available on Android. There's no work-around at this time " +
                     "since loading protected classes is a huge pain.", name);
            throw new ClassNotFoundException(name);
        }

        Class<?> klazz;
        try {
            klazz = jarLoader.loadClass(name);
            return klazz;
        } catch (ClassNotFoundException e) {
        }

        klazz = cachedClasses.get(name);
        if (klazz != null) {
            return klazz;
        }

        String internalName = ClassNameUtils.binaryToInternal(name);
        if (!classManager.getClassNames().contains(internalName)) {
            throw new ClassNotFoundException(name);
        }

        VirtualClass virtualClass = classManager.getVirtualClass(internalName);
        ClassDef classDef = virtualClass.getClassDef();

        byte[] b = classBuilder.build(classDef);
        klazz = defineClass(name, b, 0, b.length);
        cachedClasses.put(name, klazz);

        // No one ever tells you this is also necessary or you'll have a null package for the class.
        String packageName = getPackageName(name);
        if (packageName != null && getPackage(packageName) == null) {
            definePackage(getPackageName(name), null, null, null, null, null, null, null);
        }

        return klazz;
    }

}
