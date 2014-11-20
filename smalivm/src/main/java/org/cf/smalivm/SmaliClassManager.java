package org.cf.smalivm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cf.util.Dexifier;
import org.cf.util.SmaliClassNameParser;
import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.TryBlock;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderField;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderMethodParameter;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmaliClassManager {

    private static final Logger log = LoggerFactory.getLogger(SmaliClassManager.class.getSimpleName());

    private final Map<String, File> classNameToFile;
    private final DexBuilder dexBuilder;
    private final Map<String, BuilderClassDef> classNameToClassDef;
    private final Map<String, BuilderMethod> methodDescriptorToMethod;
    private final Map<String, List<String>> methodDescriptorToParameterTypes;
    private final Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> methodDescriptorToTryBlocks;
    private final Map<String, List<String>> classNameToFieldNameAndType;

    public SmaliClassManager(File smaliPath, DexBuilder dexBuilder) {
        classNameToFile = SmaliClassNameParser.getClassNameToFile(smaliPath);
        this.dexBuilder = dexBuilder;
        classNameToClassDef = new HashMap<String, BuilderClassDef>();
        methodDescriptorToMethod = new HashMap<String, BuilderMethod>();
        methodDescriptorToParameterTypes = new HashMap<String, List<String>>();
        methodDescriptorToTryBlocks = new HashMap<String, List<? extends TryBlock<? extends ExceptionHandler>>>();
        classNameToFieldNameAndType = new HashMap<String, List<String>>();
    }

    public SmaliClassManager(String smaliPath) {
        this(smaliPath, DexBuilder.makeDexBuilder());
    }

    public SmaliClassManager(String smaliPath, DexBuilder dexBuilder) {
        this(new File(smaliPath), dexBuilder);
    }

    public BuilderClassDef getClass(String className) {
        loadClassIfNecessary(className);

        return classNameToClassDef.get(className);
    }

    public Set<String> getClassNames() {
        return classNameToFile.keySet();
    }

    public List<String> getFieldNameAndType(String className) {
        loadClassIfNecessary(className);

        return classNameToFieldNameAndType.get(className);
    }

    public BuilderMethod getMethod(String methodDescriptor) {
        loadClassIfNecessary(methodDescriptor);

        return methodDescriptorToMethod.get(methodDescriptor);
    }

    public Set<String> getMethodDescriptors(String className) {
        loadClassIfNecessary(className);

        BuilderClassDef classDef = getClass(className);
        Set<String> methodNames = new HashSet<String>();
        for (BuilderMethod method : classDef.getMethods()) {
            String methodName = ReferenceUtil.getMethodDescriptor(method);
            methodNames.add(methodName);
        }

        return methodNames;
    }

    public List<String> getParameterTypes(String methodDescriptor) {
        loadClassIfNecessary(methodDescriptor);

        return methodDescriptorToParameterTypes.get(methodDescriptor);
    }

    public List<? extends TryBlock<? extends ExceptionHandler>> getTryBlocks(String methodDescriptor) {
        loadClassIfNecessary(methodDescriptor);

        return methodDescriptorToTryBlocks.get(methodDescriptor);
    }

    public boolean isLocalClass(String className) {
        return classNameToFile.containsKey(className);
    }

    public boolean isLocalMethod(String methodDescriptor) {
        String[] parts = methodDescriptor.split("->");
        String className = parts[0];
        if (!isLocalClass(className)) {
            return false;
        }

        return getMethod(methodDescriptor) != null;
    }

    public boolean methodHasImplementation(String methodDescriptor) {
        BuilderMethod method = getMethod(methodDescriptor);

        return null != method.getImplementation();
    }

    private void addFieldNameAndTypes(BuilderClassDef classDef) {
        String className = ReferenceUtil.getReferenceString(classDef);
        Collection<BuilderField> fields = classDef.getFields();
        List<String> fieldNameAndTypes = new LinkedList<String>();
        for (BuilderField field : fields) {
            String fieldDescriptor = ReferenceUtil.getFieldDescriptor(field);
            String fieldNameAndType = fieldDescriptor.split("->")[1];
            fieldNameAndTypes.add(fieldNameAndType);
        }
        classNameToFieldNameAndType.put(className, fieldNameAndTypes);
    }

    private void addMethods(BuilderClassDef classDef) {
        for (BuilderMethod method : classDef.getMethods()) {
            String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
            methodDescriptorToMethod.put(methodDescriptor, method);
            addParameterTypes(method);
            addTryBlocks(method);
        }
    }

    private void addParameterTypes(BuilderMethod method) {
        List<? extends BuilderMethodParameter> builderParameters = method.getParameters();
        List<String> parameterTypes = new ArrayList<String>(builderParameters.size());
        for (BuilderMethodParameter builderParameter : builderParameters) {
            parameterTypes.add(builderParameter.getType());
        }

        int accessFlags = method.getAccessFlags();
        boolean isStatic = ((accessFlags & AccessFlags.STATIC.getValue()) != 0);
        if (!isStatic) {
            // First "parameter" for non-static methods is instance ref
            parameterTypes.add(0, method.getDefiningClass());
        }

        String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        methodDescriptorToParameterTypes.put(methodDescriptor, parameterTypes);
    }

    private void addTryBlocks(BuilderMethod method) {
        String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        MethodImplementation implementation = method.getImplementation();
        if (implementation == null) {
            return;
        }
        methodDescriptorToTryBlocks.put(methodDescriptor, implementation.getTryBlocks());
    }

    private void loadClassIfNecessary(String typeDescriptor) {
        String[] parts = typeDescriptor.split("->");
        String className = parts[0];
        if (classNameToClassDef.containsKey(className)) {
            return;
        }

        File smaliFile = classNameToFile.get(className);
        BuilderClassDef classDef;
        try {
            classDef = Dexifier.dexifySmaliFile(smaliFile, dexBuilder);
        } catch (Exception e) {
            log.error("Unable to dexify " + smaliFile, e);
            System.exit(-1);
            return;
        }

        classNameToClassDef.put(className, classDef);
        addMethods(classDef);
        addFieldNameAndTypes(classDef);
    }

}
