package org.cf.smalivm.smali;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.iface.ClassDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmaliClassLoader extends ClassLoader {

    private static final Logger log = LoggerFactory.getLogger(SmaliClassLoader.class.getSimpleName());

    private static final String FRAMEWORK_STUBS_JAR = "/framework/framework-23.jar";

    private Map<String, Class<?>> cachedClasses;
    private final ClassBuilder classBuilder;
    private final ClassManager classManager;
    private URLClassLoader jarLoader;

    public SmaliClassLoader(ClassManager classManager) {
        super(SmaliClassLoader.class.getClassLoader());
        cachedClasses = new HashMap<String, Class<?>>();
        URL jarURL = SmaliClassLoader.class.getResource(FRAMEWORK_STUBS_JAR);
        jarLoader = new URLClassLoader(new URL[] { jarURL });
        this.classBuilder = new ClassBuilder();
        this.classManager = classManager;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
        }

        if (name.startsWith("java.")) {
            log.warn("Unable to load prohibited class name: {}\nThis error is likely the result of using a class which references a java.* class only available on Android. There's no work-around at this time since loading protected classes is a huge pain.",
                            name);
            throw new ClassNotFoundException(name);
        }

        try {
            return jarLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
        }

        Class<?> klazz = cachedClasses.get(name);
        if (klazz != null) {
            return klazz;
        }

        String internalName = ClassNameUtils.binaryToInternal(name);
        if (!classManager.isLocalClass(internalName)) {
            throw new ClassNotFoundException(name);
        }

        ClassDef classDef = classManager.getClass(internalName);
        byte[] b = classBuilder.build(classDef);
        klazz = defineClass(name, b, 0, b.length);
        cachedClasses.put(name, klazz);

        // No one ever tells you this is also necessary, or you'll have null package for the class:
        String packageName = getPackageName(name);
        if (getPackage(packageName) == null) {
            definePackage(getPackageName(name), null, null, null, null, null, null, null);
        }

        return klazz;
    }

    private static String getPackageName(String className) {
        int i = className.lastIndexOf('.');
        return className.substring(0, i);
    }

}
