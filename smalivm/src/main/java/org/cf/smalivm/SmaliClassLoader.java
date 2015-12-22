package org.cf.smalivm;

import javax.annotation.Nullable;

import org.jf.dexlib2.writer.builder.BuilderClassDef;

public class SmaliClassLoader extends ClassLoader {

    private final ClassManager manager;

    SmaliClassLoader(ClassManager manager) {
        this.manager = manager;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
            Class<?> smaliClass = findSmaliClass(name);
            if (smaliClass == null) {
                throw e;
            }
            return smaliClass;
        }
    }

    private synchronized @Nullable Class<?> findSmaliClass(String name) {
        BuilderClassDef classDef = manager.getClass(name);

        return null;
    }

}
