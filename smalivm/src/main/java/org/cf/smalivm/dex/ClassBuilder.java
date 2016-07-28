package org.cf.smalivm.dex;

import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.TryBlock;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ClassBuilder {

    private static final Logger log = LoggerFactory.getLogger(ClassBuilder.class.getSimpleName());

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

    private String buildASMSignature(Reference reference) {
        String signature = ReferenceUtil.getReferenceString(reference);

        return stripName(signature);
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
        for (TryBlock<? extends ExceptionHandler> tryBlock : method.getImplementation().getTryBlocks()) {
            for (ExceptionHandler handler : tryBlock.getExceptionHandlers()) {
                String type = handler.getExceptionType();
                if (type == null) {
                    // Type is null if it's a catchall
                    continue;
                }

                exceptionTypes.add(stripName(type));
            }
        }

        return exceptionTypes.toArray(new String[exceptionTypes.size()]);
    }

    private String[] buildInterfaces(ClassDef classDef) {
        List<String> interfaces = classDef.getInterfaces();

        return interfaces.stream().map(this::stripName).toArray(size -> new String[interfaces.size()]);
    }

    private String stripName(String internalName) {
        return internalName.substring(1, internalName.length() - 1);
    }

    private void visitClass(ClassDef classDef, ClassWriter classWriter) {
        int version = Opcodes.V1_7;
        int access = classDef.getAccessFlags();
        String name = stripName(classDef.getType());
        String signature = buildASMSignature(classDef);
        String superName = null;
        if (classDef.getSuperclass() != null) {
            superName = stripName(classDef.getSuperclass());
        }
        String[] interfaces = buildInterfaces(classDef);
        classWriter.visit(version, access, name, signature, superName, interfaces);
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

    private void visitFields(Iterable<? extends Field> fields, ClassWriter classWriter, int classAccessFlags) {
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
        if (method.getName().equals("<clinit>")) {
            visitClInitStub(mv);
        } else if (method.getName().equals("<init>")) {
            visitInitStub(classDef, mv);
        } else {
            visitMethodStub(mv);
        }
        // Do this at the end so ASM can calculate max stack and locals sizes
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void visitEnumMethods(ClassDef classDef, Iterable<? extends Field> fields, ClassWriter classWriter) {
        String name = stripName(classDef.getType());

        int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC + Opcodes.ACC_ENUM;
        classWriter.visitField(access, "$shadow_instance", classDef.getType(), null, null);

        MethodVisitor mv = classWriter.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();

        // Set the fields for each enum value
        Stream<? extends Field> fieldsStream = StreamSupport.stream(fields.spliterator(), false);
        List<String> fieldNames =
                fieldsStream.filter(f -> (f.getAccessFlags() & Opcodes.ACC_ENUM) != 0).map(Field::getName)
                        .collect(Collectors.toList());
        fieldNames.add("$shadow_instance");
        int fieldCount = fieldNames.size();
        for (int i = 0; i < fieldCount; i++) {
            String fieldName = fieldNames.get(i);
            mv.visitTypeInsn(Opcodes.NEW, name);
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn(fieldName);
            mv.visitIntInsn(Opcodes.BIPUSH, i);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, name, "<init>", "(Ljava/lang/String;I)V", false);
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

        // Store $VALUES
        mv.visitFieldInsn(Opcodes.PUTSTATIC, name, "$VALUES", "[" + classDef.getType());
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        mv = classWriter.visitMethod(Opcodes.ACC_PRIVATE, "<init>", "(Ljava/lang/String;I)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        mv = classWriter
                     .visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "values", "()[" + classDef.getType(), null,
                             null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, name, "$VALUES", "[" + classDef.getType());
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

    private void visitMethods(ClassDef classDef, Iterable<? extends Method> methods, ClassWriter classWriter) {
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
