package org.cf.smalivm.dex;

import com.google.common.primitives.Ints;

import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerFactory;
import org.cf.smalivm.type.VirtualClass;
import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.AnnotationElement;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.TryBlock;
import org.jf.dexlib2.iface.value.EncodedValue;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderEncodedValues;
import org.jf.dexlib2.writer.builder.BuilderMethodReference;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassBuilder {

    private static final Logger log = LoggerFactory.getLogger(ClassBuilder.class.getSimpleName());

    private final ClassManager classManager;

    public ClassBuilder(ClassManager classManager) {
        this.classManager = classManager;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String smaliPath = args[0];
        String className = args[1];
        ClassManager classManager = new ClassManagerFactory().build(smaliPath);
        VirtualClass virtualClass = classManager.getVirtualClass(className);

        ClassBuilder builder = new ClassBuilder(classManager);
        byte[] classBytes = builder.build(virtualClass.getClassDef());

        new FileOutputStream(new java.io.File("temp.class")).write(classBytes);
        System.out.println(
                "Generated class bytes for " + virtualClass + ", size=" + classBytes.length);
        SmaliClassLoader classLoader = new SmaliClassLoader(classManager);
        classLoader.loadClass(virtualClass.getSourceName());
    }

    public byte[] build(ClassDef classDef) {
        log.debug("Building input class: {}", classDef);

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        visitClass(classDef, classWriter);
        visitFields(classDef.getFields(), classWriter, classDef.getAccessFlags());
        if ((classDef.getAccessFlags() & Opcodes.ACC_ENUM) != 0) {
            visitEnumMethods(classDef, classDef.getFields(), classWriter);
        } else {
            visitMethods(classDef, classDef.getMethods(), classWriter);
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }

    private String buildASMSignature(ClassDef classDef) {
        String signature = null;
        outer:
        for (Annotation annotation : classDef.getAnnotations()) {
            if (!annotation.getType().equals("Ldalvik/annotation/Signature;")) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (AnnotationElement e : annotation.getElements()) {
                BuilderEncodedValues.BuilderArrayEncodedValue ev = (BuilderEncodedValues.BuilderArrayEncodedValue) e
                        .getValue();
                for (EncodedValue v : ev.getValue()) {
                    BuilderEncodedValues.BuilderStringEncodedValue value = (BuilderEncodedValues.BuilderStringEncodedValue) v;
                    sb.append(value.getValue());
                }
            }
            signature = sb.toString();
            break;
        }

        return signature;
    }

    private String buildDescriptor(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        method.getParameterTypes().forEach(sb::append);
        sb.append(')');
        sb.append(method.getReturnType());

        return sb.toString();
    }

    private String[] buildExceptions(Method method) {
        if (method.getImplementation() == null) {
            return null;
        }

        Set<String> exceptionTypes = new HashSet<>();
        for (TryBlock<? extends ExceptionHandler> tryBlock : method.getImplementation()
                .getTryBlocks()) {
            for (ExceptionHandler handler : tryBlock.getExceptionHandlers()) {
                String type = handler.getExceptionType();
                if (type == null) {
                    // Type is null if it's a catchall
                    continue;
                }

                exceptionTypes.add(stripName(type));
            }
        }

        return exceptionTypes.toArray(new String[0]);
    }

    private String[] buildInterfaces(ClassDef classDef) {
        List<String> interfaces = classDef.getInterfaces();

        return interfaces.stream().map(this::stripName)
                .toArray(size -> new String[interfaces.size()]);
    }

    private String stripName(String internalName) {
        return internalName.substring(1, internalName.length() - 1);
    }

    private void visitInnerClasses(BuilderEncodedValues.BuilderTypeEncodedValue value,
                                   ClassWriter classWriter) {
        // String name, String outerName, String innerName, int access
        String internalName = value.getValue();
        String fullName = stripName(value.getValue());
        String[] parts = fullName.split("\\$", 2);
        String outerName = parts[0];
        String innerName = parts[1];
        boolean isAnonymous = innerName.equals("1") || Ints.tryParse(innerName) != null;
        if (isAnonymous) {
            innerName = null;
        }
        int innerAccess = classManager.getVirtualClass(internalName).getClassDef().getAccessFlags();
        classWriter.visitInnerClass(fullName, outerName, innerName, innerAccess);
    }

    private void visitEnclosingMethod(BuilderEncodedValues.BuilderMethodEncodedValue value,
                                      ClassWriter classWriter) {
        BuilderMethodReference methodRef = value.getValue();
        String owner = stripName(methodRef.getDefiningClass());
        String name = methodRef.getName();
        String descriptor = ReferenceUtil.getMethodDescriptor(methodRef).split("->")[1];
        String desc = descriptor.substring(descriptor.indexOf('('));
        classWriter.visitOuterClass(owner, name, desc);
    }

    private void visitClassAnnotations(Set<? extends Annotation> annotations,
                                       ClassWriter classWriter) {
        for (Annotation annotation : annotations) {
            switch (annotation.getType()) {
                case "Ldalvik/annotation/EnclosingMethod;":
                    for (AnnotationElement e : annotation.getElements()) {
                        BuilderEncodedValues.BuilderMethodEncodedValue v = (BuilderEncodedValues.BuilderMethodEncodedValue) e
                                .getValue();
                        visitEnclosingMethod(v, classWriter);
                        // There should only ever be one enclosing method.
                        break;
                    }
                    break;
                case "Ldalvik/annotation/MemberClasses;":
                    for (AnnotationElement e : annotation.getElements()) {
                        BuilderEncodedValues.BuilderArrayEncodedValue ev = (BuilderEncodedValues.BuilderArrayEncodedValue) e
                                .getValue();
                        for (EncodedValue v : ev.getValue()) {
                            BuilderEncodedValues.BuilderTypeEncodedValue value = (BuilderEncodedValues.BuilderTypeEncodedValue) v;
                            visitInnerClasses(value, classWriter);
                        }
                    }
                    break;
            }
        }
    }

    private void visitClass(ClassDef classDef, ClassWriter classWriter) {
        int version = Opcodes.V1_8;
        int access = classDef.getAccessFlags();
        String name = stripName(classDef.getType());
        // Signature should be type signature for the class, which is uncommon.
        String signature = buildASMSignature(classDef);
        String superName = null;
        if (classDef.getSuperclass() != null) {
            superName = stripName(classDef.getSuperclass());
        }
        String[] interfaces = buildInterfaces(classDef);
        classWriter.visit(version, access, name, signature, superName, interfaces);
        classWriter.visitSource(classDef.getSourceFile(), null);
        visitClassAnnotations(classDef.getAnnotations(), classWriter);
    }

    private void visitClInitStub(MethodVisitor mv) {
        mv.visitCode();
        mv.visitInsn(Opcodes.RETURN);
    }

    private void visitInitStub(ClassDef classDef, MethodVisitor mv) {
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        if (classDef.getSuperclass() != null) {
            String superName = stripName(classDef.getSuperclass());
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "<init>", "()V", false);
        }
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void visitFields(Iterable<? extends Field> fields, ClassWriter classWriter,
                             int classAccessFlags) {
        for (Field field : fields) {
            /*
             * It's possible to declare a Smali interface with private fields. Android doesn't seem to mind, but
             * the Java class loader will complain. To get around this, just set them public, which is what they
             * should be anyway.
             */
            int fieldAccessFlags = field.getAccessFlags();
            if ((classAccessFlags & Opcodes.ACC_INTERFACE) != 0) {
                if ((fieldAccessFlags & Opcodes.ACC_PRIVATE) != 0) {
                    fieldAccessFlags &= ~Opcodes.ACC_PRIVATE;
                    fieldAccessFlags |= Opcodes.ACC_PUBLIC;
                }
            }

            String name = field.getName();
            String desc = field.getType();
            String signature = null;
            Object value = null;
            classWriter.visitField(fieldAccessFlags, name, desc, signature, value);
        }
    }

    private void visitMethod(ClassDef classDef, Method method, MethodVisitor mv) {
        mv.visitCode();

        String methodName = method.getName();
        if (methodName.equals("<clinit>")) {
            visitClInitStub(mv);
        } else if (methodName.equals("<init>")) {
            visitInitStub(classDef, mv);
        } else if (methodName.equals("hashCode") && method.getReturnType().equals("I")) {
            visitCallObjectHashCode(mv);
        } else {
            visitMethodStub(mv);
        }
        // Do this at the end so ASM can calculate max stack and locals sizes
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void visitEnumMethods(ClassDef classDef, Iterable<? extends Field> fields,
                                  ClassWriter classWriter) {
        String name = stripName(classDef.getType());

        int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC + Opcodes.ACC_ENUM;
        classWriter.visitField(access, "$shadow_instance", classDef.getType(), null, null);

        MethodVisitor mv = classWriter
                .visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();

        List<Field> fieldList = new LinkedList<>();
        fields.forEach(fieldList::add);

        // Set the fields for each enum value
        List<String> fieldNames = fieldList.stream()
                .filter(f -> (f.getAccessFlags() & Opcodes.ACC_ENUM) != 0).map(Field::getName)
                .collect(Collectors.toList());
        fieldNames.add("$shadow_instance");
        int fieldCount = fieldNames.size();
        for (int i = 0; i < fieldCount; i++) {
            String fieldName = fieldNames.get(i);
            mv.visitTypeInsn(Opcodes.NEW, name);
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn(fieldName);
            mv.visitIntInsn(Opcodes.BIPUSH, i);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, name, "<init>", "(Ljava/lang/String;I)V",
                               false);
            mv.visitFieldInsn(Opcodes.PUTSTATIC, name, fieldName, classDef.getType());
        }

        // Create array to hold values destined for $VALUES
        mv.visitIntInsn(Opcodes.BIPUSH, fieldCount);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, name);

        // Set elements of $VALUES
        for (int i = 0; i < fieldCount; i++) {
            String fieldName = fieldNames.get(i);
            mv.visitInsn(Opcodes.DUP);
            mv.visitIntInsn(Opcodes.BIPUSH, i);
            mv.visitFieldInsn(Opcodes.GETSTATIC, name, fieldName, classDef.getType());
            mv.visitInsn(Opcodes.AASTORE);
        }

        String valuesFieldName = "$VALUES";
        for (Field field : fieldList) {
            /*
             * Enums must contain an array field which holds instances of the enum initialized with
             * their string value. This is normally called $VALUES, but it's not safe to assume this
             * is always true. For example, Proguard may change the name. Instead, look for a field
             * which looks right and use that name.
             */
            int fieldFlags = field.getAccessFlags();
            if ((fieldFlags & Opcodes.ACC_SYNTHETIC) != 0 && (fieldFlags & Opcodes.ACC_PRIVATE) != 0 && (fieldFlags & Opcodes.ACC_STATIC) != 0 && (fieldFlags & Opcodes.ACC_FINAL) != 0 && field.getType().charAt(0) == '[') {
                valuesFieldName = field.getName();
                break;
            }
        }

        mv.visitFieldInsn(Opcodes.PUTSTATIC, name, valuesFieldName, "[" + classDef.getType());
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        mv = classWriter
                .visitMethod(Opcodes.ACC_PRIVATE, "<init>", "(Ljava/lang/String;I)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Enum", "<init>",
                           "(Ljava/lang/String;I)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        mv = classWriter.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "values",
                                     "()[" + classDef.getType(), null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, name, valuesFieldName, "[" + classDef.getType());
        mv.visitInsn(Opcodes.DUP);
        mv.visitVarInsn(Opcodes.ASTORE, 0);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ARRAYLENGTH);
        mv.visitInsn(Opcodes.DUP);
        mv.visitVarInsn(Opcodes.ISTORE, 1);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, name);
        mv.visitInsn(Opcodes.DUP);
        mv.visitVarInsn(Opcodes.ASTORE, 2);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitVarInsn(Opcodes.ILOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "arraycopy",
                           "(Ljava/lang/Object;ILjava/lang/Object;II)V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        mv = classWriter.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "valueOf",
                                     "(Ljava/lang/String;)" + classDef.getType(), null, null);
        mv.visitCode();
        mv.visitLdcInsn(Type.getType(classDef.getType()));
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Enum", "valueOf",
                           "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;", false);
        mv.visitTypeInsn(Opcodes.CHECKCAST, name);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void visitMethods(ClassDef classDef, Iterable<? extends Method> methods,
                              ClassWriter classWriter) {
        boolean hasDefaultConstructor = false;
        for (Method method : methods) {
            int access = method.getAccessFlags();
            String name = method.getName();
            String desc = buildDescriptor(method);
            String signature = null;
            String[] exceptions = buildExceptions(method);
            MethodVisitor mv = classWriter.visitMethod(access, name, desc, signature, exceptions);
            if (method.getImplementation() != null) {
                if (method.getName().equals("<init>") && desc.equals("()V")) {
                    hasDefaultConstructor = true;
                }
                visitMethod(classDef, method, mv);
            }
        }

        if (!hasDefaultConstructor) {
            boolean isInterface = (Opcodes.ACC_INTERFACE & classDef.getAccessFlags()) != 0;
            if (!isInterface) {
                /*
                 * Every class gets a default constructor. This is a give-away but it simplifies things. Right now,
                 * <init> stubs just call the empty arg / default constructor of the super, which may not exist. To do
                 * this properly, would need to maybe randomly pick a constructor and call that one, which may kind of
                 * one day affect correctness.
                 */
                int mods = Opcodes.ACC_PUBLIC;
                MethodVisitor mv = classWriter.visitMethod(mods, "<init>", "()V", null, null);
                visitInitStub(classDef, mv);
            }
        }
    }

    private void visitCallObjectHashCode(MethodVisitor mv) {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        String owner = "java/lang/Object";
        String name = "hashCode";
        String desc = "()I";
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, name, desc, false);
        mv.visitInsn(Opcodes.IRETURN);
    }

    private void visitMethodStub(MethodVisitor mv) {
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/RuntimeException");
        mv.visitInsn(Opcodes.DUP);
        mv.visitLdcInsn("Stub!");
        String owner = "java/lang/RuntimeException";
        String name = "<init>";
        String desc = "(Ljava/lang/String;)V";
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, name, desc, false);
        mv.visitInsn(Opcodes.ATHROW);
    }

}
