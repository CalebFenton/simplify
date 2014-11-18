package org.cf.smalivm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.TryBlock;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderField;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderMethodParameter;

public class ClassManager {

    private static Set<String> buildLocalClasses(List<BuilderClassDef> classDefs) {
        Set<String> result = new HashSet<String>(classDefs.size());
        for (BuilderClassDef classDef : classDefs) {
            String className = ReferenceUtil.getReferenceString(classDef);
            result.add(className);
        }

        return result;
    }

    private static Map<String, BuilderMethod> buildMethodDescriptorToBuilderMethod(List<BuilderClassDef> classDefs) {
        Map<String, BuilderMethod> result = new HashMap<String, BuilderMethod>(classDefs.size());
        for (BuilderClassDef classDef : classDefs) {
            for (BuilderMethod method : classDef.getMethods()) {
                String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
                result.put(methodDescriptor, method);
            }
        }

        return result;
    }

    private static Map<String, List<String>> buildMethodDescriptorToParameterTypes(List<BuilderClassDef> classDefs) {
        Map<String, List<String>> result = new HashMap<String, List<String>>(classDefs.size());
        for (BuilderClassDef classDef : classDefs) {
            for (BuilderMethod method : classDef.getMethods()) {
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
                result.put(methodDescriptor, parameterTypes);
            }
        }

        return result;
    }

    private static Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> buildMethodDescriptorToTryCatchList(
                    List<BuilderClassDef> classDefs) {
        Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> result = new HashMap<String, List<? extends TryBlock<? extends ExceptionHandler>>>();
        for (BuilderClassDef classDef : classDefs) {
            for (BuilderMethod method : classDef.getMethods()) {
                String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
                MethodImplementation implementation = method.getImplementation();
                if (implementation == null) {
                    continue;
                }
                result.put(methodDescriptor, implementation.getTryBlocks());
            }
        }

        return result;

    }

    private final Set<String> localClasses;
    private final Map<String, BuilderClassDef> classDescriptorToBuilderClassDef;
    private final Map<String, BuilderMethod> methodDescriptorToBuilderMethod;
    private final Map<String, List<String>> methodDescriptorToParameterTypes;
    private final Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> methodDescriptorToTryCatchList;
    private final Map<String, List<String>> classNameToFieldNameAndType;

    public ClassManager(List<BuilderClassDef> classDefs) {
        localClasses = buildLocalClasses(classDefs);

        classDescriptorToBuilderClassDef = new HashMap<String, BuilderClassDef>(classDefs.size());
        for (BuilderClassDef classDef : classDefs) {
            String classDescriptor = ReferenceUtil.getReferenceString(classDef);
            classDescriptorToBuilderClassDef.put(classDescriptor, classDef);
        }
        methodDescriptorToBuilderMethod = buildMethodDescriptorToBuilderMethod(classDefs);
        methodDescriptorToParameterTypes = buildMethodDescriptorToParameterTypes(classDefs);
        methodDescriptorToTryCatchList = buildMethodDescriptorToTryCatchList(classDefs);
        classNameToFieldNameAndType = buildClassNameToFieldNameAndType(classDefs);
    }

    public BuilderClassDef getBuilderClass(String classDescriptor) {
        return classDescriptorToBuilderClassDef.get(classDescriptor);
    }

    public List<String> getFieldNameAndTypes(String className) {
        return classNameToFieldNameAndType.get(className);
    }

    public List<String> getLocalClasses() {
        String[] classNames = localClasses.toArray(new String[localClasses.size()]);

        return Arrays.asList(classNames);
    }

    public Set<String> getMethodDescriptors() {
        return methodDescriptorToBuilderMethod.keySet();
    }

    public List<String> getParameterTypes(String methodDescriptor) {
        return methodDescriptorToParameterTypes.get(methodDescriptor);
    }

    public List<? extends TryBlock<? extends ExceptionHandler>> getTryCatchList(String methodDescriptor) {
        return methodDescriptorToTryCatchList.get(methodDescriptor);
    }

    public boolean isLocalClass(String className) {
        return localClasses.contains(className);
    }

    public boolean isLocalMethod(String methodDescriptor) {
        return methodDescriptorToBuilderMethod.containsKey(methodDescriptor);
    }

    public boolean methodHasImplementation(String methodDescriptor) {
        BuilderMethod method = getBuilderMethod(methodDescriptor);

        return null != method.getImplementation();
    }

    private Map<String, List<String>> buildClassNameToFieldNameAndType(List<BuilderClassDef> classDefs) {
        Map<String, List<String>> classNameToFieldNameAndType = new HashMap<String, List<String>>();
        for (BuilderClassDef classDef : classDefs) {
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

        return classNameToFieldNameAndType;
    }

    BuilderMethod getBuilderMethod(String methodDescriptor) {
        return methodDescriptorToBuilderMethod.get(methodDescriptor);
    }

}
