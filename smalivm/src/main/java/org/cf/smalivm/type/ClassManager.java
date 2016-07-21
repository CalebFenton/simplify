package org.cf.smalivm.type;

import org.cf.smalivm.dex.Dexifier;
import org.cf.smalivm.dex.SmaliFile;
import org.cf.smalivm.dex.SmaliFileFactory;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.iface.reference.TypeReference;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The class manager is responsible for loading Smali files into Dexlib2 objects and making them available.
 */
public class ClassManager {

    private static final Logger log = LoggerFactory.getLogger(ClassManager.class.getSimpleName());

    private final HashMap<String, VirtualClass> classNameToVirtualClass;
    private final Map<String, SmaliFile> classNameToSmaliFile;

    private final SmaliFileFactory smaliFileFactory;
    private final DexBuilder dexBuilder;
    // Use separate DexBuilder to intern framework classes to avoid including in output dex
    private final DexBuilder frameworkDexBuilder = DexBuilder.makeDexBuilder();

    ClassManager(DexBuilder dexBuilder) {
        this(dexBuilder, false);
        cacheSmaliFiles(smaliFileFactory.getSmaliFiles());
    }

    private ClassManager(DexBuilder dexBuilder, boolean internalOnly) {
        this.dexBuilder = dexBuilder;
        smaliFileFactory = new SmaliFileFactory();
        classNameToVirtualClass = new HashMap<String, VirtualClass>();
        classNameToSmaliFile = new HashMap<String, SmaliFile>();
        VirtualGeneric.setClassManager(this);
    }

    ClassManager(DexBuilder dexBuilder, File smaliPath) throws IOException {
        this(dexBuilder, false);
        cacheSmaliFiles(smaliFileFactory.getSmaliFiles(smaliPath));
    }

    /**
     * Does not load any Smali files.
     *
     * @return all local class names, including framework
     */
    public Set<String> getClassNames() {
        return classNameToSmaliFile.keySet();
    }

    /**
     * @return DexBuilder
     */
    public DexBuilder getDexBuilder() {
        return dexBuilder;
    }

    /**
     * Does not load any Smali files.
     *
     * @return all framework class names
     */
    public Set<String> getFrameworkClassNames() {
        Set<String> classNames = new HashSet<String>();
        for (String className : classNameToSmaliFile.keySet()) {
            if (smaliFileFactory.isFrameworkClass(className)) {
                classNames.add(className);
            }
        }

        return classNames;
    }

    public Collection<VirtualClass> getLoadedClasses() {
        return classNameToVirtualClass.values();
    }

    public VirtualMethod getMethod(String methodSignature) {
        String[] parts = methodSignature.split("->");
        String className = parts[0];
        String methodDescriptor = parts[1];

        return getMethod(className, methodDescriptor);
    }

    public VirtualMethod getMethod(String className, String methodDescriptor) {
        VirtualGeneric virtualClass = getVirtualClass(className);

        return virtualClass.getMethod(methodDescriptor);
    }

    /**
     * Does not load any Smali files.
     *
     * @return all local class names which are not part of the framework
     */
    public Set<String> getNonFrameworkClassNames() {
        Set<String> classNames = new HashSet<String>();
        for (String className : classNameToSmaliFile.keySet()) {
            if (!smaliFileFactory.isFrameworkClass(className)) {
                classNames.add(className);
            }
        }

        return classNames;
    }

    public VirtualClass getVirtualClass(String className) {
        return (VirtualClass) getVirtualType(className);
    }

    public VirtualGeneric getVirtualType(TypeReference typeReference) {
        char first = typeReference.charAt(0);
        if (first == 'L') {
            String className = typeReference.getType();
            dexifyClassIfNecessary(className);

            return classNameToVirtualClass.get(className);
        } else if (first == '[') {
            return new VirtualArray(typeReference);
        } else if (ClassNameUtils.isPrimitive(typeReference.getType())) {
            return new VirtualPrimitive(typeReference);
        } else {
            throw new RuntimeException("Unrecognized type: " + typeReference);
        }
    }

    public VirtualGeneric getVirtualType(String typeSignature) {
        TypeReference typeReference = getFrameworkDexBuilder().internTypeReference(typeSignature);

        return getVirtualType(typeReference);
    }

    public boolean isFrameworkClass(VirtualClass virtualClass) {
        return smaliFileFactory.isFrameworkClass(virtualClass.getName());
    }

    public boolean isSafeFrameworkClass(VirtualClass virtualClass) {
        return smaliFileFactory.isSafeFrameworkClass(virtualClass.getName());
    }

    DexBuilder getFrameworkDexBuilder() {
        return frameworkDexBuilder;
    }

    private void cacheSmaliFiles(Set<SmaliFile> smaliFiles) {
        for (SmaliFile smaliFile : smaliFiles) {
            classNameToSmaliFile.put(smaliFile.getClassName(), smaliFile);
        }
    }

    private void dexifyClassIfNecessary(String className) {
        if (classNameToVirtualClass.containsKey(className)) {
            return;
        }

        SmaliFile smaliFile = classNameToSmaliFile.get(className);
        if (smaliFile == null) {
            throw new RuntimeException("Can't find Smali file containing " + className);
        }
        BuilderClassDef classDef;
        try {
            boolean isFramework = smaliFileFactory.isFrameworkClass(className);
            InputStream is = smaliFile.open();
            DexBuilder builder = isFramework ? frameworkDexBuilder : dexBuilder;
            classDef = Dexifier.dexifySmaliFile(smaliFile.getPath(), is, builder);
            is.close();
        } catch (Exception e) {
            throw new RuntimeException("Error while loading class definition: " + className, e);
        }

        VirtualClass localClass = new VirtualClass(classDef);
        classNameToVirtualClass.put(className, localClass);
    }

}
