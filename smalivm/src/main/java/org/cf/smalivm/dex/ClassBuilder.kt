package org.cf.smalivm.dex

import com.google.common.primitives.Ints
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.ClassManagerFactory
import org.jf.dexlib2.formatter.DexFormatter
import org.jf.dexlib2.iface.Annotation
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.iface.Field
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.writer.builder.BuilderEncodedValues.*
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.stream.Collectors

class ClassBuilder(private val classManager: ClassManager) {
    fun build(classDef: ClassDef): ByteArray {
        log.debug("Building input class: {}", classDef)
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
        visitClass(classDef, classWriter)
        visitFields(classDef.fields, classWriter, classDef.accessFlags)
        if (classDef.accessFlags and Opcodes.ACC_ENUM != 0) {
            visitEnumMethods(classDef, classDef.fields, classWriter)
        } else {
            visitMethods(classDef, classDef.methods, classWriter)
        }
        classWriter.visitEnd()
        return classWriter.toByteArray()
    }

    private fun buildASMSignature(classDef: ClassDef): String? {
        var signature: String? = null
        outer@ for (annotation in classDef.annotations) {
            if (annotation.type != "Ldalvik/annotation/Signature;") {
                continue
            }
            val sb = StringBuilder()
            for (e in annotation.elements) {
                val ev = e.value as BuilderArrayEncodedValue
                for (v in ev.value) {
                    val value = v as BuilderStringEncodedValue
                    sb.append(value.value)
                }
            }
            signature = sb.toString()
            break
        }
        return signature
    }

    private fun buildDescriptor(method: Method): String {
        val sb = StringBuilder()
        sb.append('(')
        method.parameterTypes.forEach { s: CharSequence? -> sb.append(s) }
        sb.append(')')
        sb.append(method.returnType)
        return sb.toString()
    }

    private fun buildExceptions(method: Method): Array<String>? {
        if (method.implementation == null) {
            return null
        }
        val exceptionTypes: MutableSet<String> = HashSet()
        for (tryBlock in method.implementation!!.tryBlocks) {
            for (handler in tryBlock.exceptionHandlers) {
                val type = handler.exceptionType
                    ?: // Type is null if it's a catchall
                    continue
                exceptionTypes.add(stripName(type))
            }
        }
        return exceptionTypes.toTypedArray()
    }

    private fun buildInterfaces(classDef: ClassDef): Array<String?> {
        val interfaces = classDef.interfaces
        return interfaces.map { internalName: String -> stripName(internalName) }.toTypedArray()
    }

    private fun stripName(internalName: String): String {
        return internalName.substring(1, internalName.length - 1)
    }

    private fun visitInnerClasses(value: BuilderTypeEncodedValue, classWriter: ClassWriter) {
        // String name, String outerName, String innerName, int access
        val internalName = value.value
        val fullName = stripName(value.value)
        val parts = fullName.split(Regex("\\$"), 2)
        val outerName = parts[0]
        var innerName: String? = parts[1]
        val isAnonymous = innerName == "1" || Ints.tryParse(innerName!!) != null
        if (isAnonymous) {
            innerName = null
        }
        val innerAccess = classManager.getVirtualClass(internalName).classDef.accessFlags
        classWriter.visitInnerClass(fullName, outerName, innerName, innerAccess)
    }

    private fun visitEnclosingMethod(value: BuilderMethodEncodedValue, classWriter: ClassWriter) {
        val methodRef = value.value
        val owner = stripName(methodRef.definingClass)
        val name = methodRef.name
        val descriptor = DexFormatter.INSTANCE.getMethodDescriptor(methodRef).split("->")[1]
        val desc = descriptor.substring(descriptor.indexOf('('))
        classWriter.visitOuterClass(owner, name, desc)
    }

    private fun visitClassAnnotations(annotations: Set<Annotation>, classWriter: ClassWriter) {
        for (annotation in annotations) {
            when (annotation.type) {
                "Ldalvik/annotation/EnclosingMethod;" -> for (e in annotation.elements) {
                    val v = e.value as BuilderMethodEncodedValue
                    visitEnclosingMethod(v, classWriter)
                    // There should only ever be one enclosing method.
                    break
                }
                "Ldalvik/annotation/MemberClasses;" -> for (e in annotation.elements) {
                    val ev = e.value as BuilderArrayEncodedValue
                    for (v in ev.value) {
                        val value = v as BuilderTypeEncodedValue
                        visitInnerClasses(value, classWriter)
                    }
                }
            }
        }
    }

    private fun visitClass(classDef: ClassDef, classWriter: ClassWriter) {
        val version = Opcodes.V1_8
        val access = classDef.accessFlags
        val name = stripName(classDef.type)
        // Signature should be type signature for the class, which is uncommon.
        val signature = buildASMSignature(classDef)
        var superName: String? = null
        if (classDef.superclass != null) {
            superName = stripName(classDef.superclass!!)
        }
        val interfaces = buildInterfaces(classDef)
        classWriter.visit(version, access, name, signature, superName, interfaces)
        classWriter.visitSource(classDef.sourceFile, null)
        visitClassAnnotations(classDef.annotations, classWriter)
    }

    private fun visitClInitStub(mv: MethodVisitor) {
        mv.visitCode()
        mv.visitInsn(Opcodes.RETURN)
    }

    private fun visitInitStub(classDef: ClassDef, mv: MethodVisitor) {
        mv.visitCode()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        if (classDef.superclass != null) {
            val superName = stripName(classDef.superclass!!)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "<init>", "()V", false)
        }
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(0, 0)
        mv.visitEnd()
    }

    private fun visitFields(fields: Iterable<Field>, classWriter: ClassWriter, classAccessFlags: Int) {
        for (field in fields) {
            /*
             * It's possible to declare a Smali interface with private fields. Android doesn't seem to mind, but
             * the Java class loader will complain. To get around this, just set them public, which is what they
             * should be anyway.
             */
            var fieldAccessFlags = field.accessFlags
            if (classAccessFlags and Opcodes.ACC_INTERFACE != 0) {
                if (fieldAccessFlags and Opcodes.ACC_PRIVATE != 0) {
                    fieldAccessFlags = fieldAccessFlags and Opcodes.ACC_PRIVATE.inv()
                    fieldAccessFlags = fieldAccessFlags or Opcodes.ACC_PUBLIC
                }
            }
            val name = field.name
            val desc = field.type
            val signature: String? = null
            val value: Any? = null
            classWriter.visitField(fieldAccessFlags, name, desc, signature, value)
        }
    }

    private fun visitMethod(classDef: ClassDef, method: Method, mv: MethodVisitor) {
        mv.visitCode()
        val methodName = method.name
        if (methodName == "<clinit>") {
            visitClInitStub(mv)
        } else if (methodName == "<init>") {
            visitInitStub(classDef, mv)
        } else if (methodName == "hashCode" && method.returnType == "I") {
            visitCallObjectHashCode(mv)
        } else {
            visitMethodStub(mv)
        }
        // Do this at the end so ASM can calculate max stack and locals sizes
        mv.visitMaxs(0, 0)
        mv.visitEnd()
    }

    private fun visitEnumMethods(classDef: ClassDef, fields: Iterable<Field>, classWriter: ClassWriter) {
        val name = stripName(classDef.type)
        val access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC + Opcodes.ACC_ENUM
        classWriter.visitField(access, "\$shadow_instance", classDef.type, null, null)
        var mv = classWriter.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null)
        mv.visitCode()
        val fieldList: MutableList<Field> = LinkedList()
        fields.forEach { fieldList.add(it) }

        // Set the fields for each enum value
        val fieldNames = fieldList.stream().filter { f: Field -> f.accessFlags and Opcodes.ACC_ENUM != 0 }
            .map { obj: Field -> obj.name }
            .collect(Collectors.toList())
        fieldNames.add("\$shadow_instance")
        val fieldCount = fieldNames.size
        for (i in 0 until fieldCount) {
            val fieldName = fieldNames[i]
            mv.visitTypeInsn(Opcodes.NEW, name)
            mv.visitInsn(Opcodes.DUP)
            mv.visitLdcInsn(fieldName)
            mv.visitIntInsn(Opcodes.BIPUSH, i)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, name, "<init>", "(Ljava/lang/String;I)V", false)
            mv.visitFieldInsn(Opcodes.PUTSTATIC, name, fieldName, classDef.type)
        }

        // Create array to hold values destined for $VALUES
        mv.visitIntInsn(Opcodes.BIPUSH, fieldCount)
        mv.visitTypeInsn(Opcodes.ANEWARRAY, name)

        // Set elements of $VALUES
        for (i in 0 until fieldCount) {
            val fieldName = fieldNames[i]
            mv.visitInsn(Opcodes.DUP)
            mv.visitIntInsn(Opcodes.BIPUSH, i)
            mv.visitFieldInsn(Opcodes.GETSTATIC, name, fieldName, classDef.type)
            mv.visitInsn(Opcodes.AASTORE)
        }
        var valuesFieldName = "\$VALUES"
        for (field in fieldList) {
            /*
             * Enums must contain an array field which holds instances of the enum initialized with
             * their string value. This is normally called $VALUES, but it's not safe to assume this
             * is always true. For example, Proguard may change the name. Instead, look for a field
             * which looks right and use that name.
             */
            val fieldFlags = field.accessFlags
            if (fieldFlags and Opcodes.ACC_SYNTHETIC != 0 && fieldFlags and Opcodes.ACC_PRIVATE != 0 && fieldFlags and Opcodes.ACC_STATIC != 0 && fieldFlags and Opcodes.ACC_FINAL != 0 && field
                    .type[0] == '['
            ) {
                valuesFieldName = field.name
                break
            }
        }
        mv.visitFieldInsn(Opcodes.PUTSTATIC, name, valuesFieldName, "[" + classDef.type)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(0, 0)
        mv.visitEnd()
        mv = classWriter.visitMethod(Opcodes.ACC_PRIVATE, "<init>", "(Ljava/lang/String;I)V", null, null)
        mv.visitCode()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitVarInsn(Opcodes.ALOAD, 1)
        mv.visitVarInsn(Opcodes.ILOAD, 2)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V", false)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(0, 0)
        mv.visitEnd()
        mv = classWriter.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "values", "()[" + classDef.type, null, null)
        mv.visitCode()
        mv.visitFieldInsn(Opcodes.GETSTATIC, name, valuesFieldName, "[" + classDef.type)
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.ASTORE, 0)
        mv.visitInsn(Opcodes.ICONST_0)
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitInsn(Opcodes.ARRAYLENGTH)
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.ISTORE, 1)
        mv.visitTypeInsn(Opcodes.ANEWARRAY, name)
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.ASTORE, 2)
        mv.visitInsn(Opcodes.ICONST_0)
        mv.visitVarInsn(Opcodes.ILOAD, 1)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V", false)
        mv.visitVarInsn(Opcodes.ALOAD, 2)
        mv.visitInsn(Opcodes.ARETURN)
        mv.visitMaxs(0, 0)
        mv.visitEnd()
        mv = classWriter.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "valueOf", "(Ljava/lang/String;)" + classDef.type, null, null)
        mv.visitCode()
        mv.visitLdcInsn(Type.getType(classDef.type))
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Enum", "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;", false)
        mv.visitTypeInsn(Opcodes.CHECKCAST, name)
        mv.visitInsn(Opcodes.ARETURN)
        mv.visitMaxs(0, 0)
        mv.visitEnd()
    }

    private fun visitMethods(classDef: ClassDef, methods: Iterable<Method>, classWriter: ClassWriter) {
        var hasDefaultConstructor = false
        for (method in methods) {
            val access = method.accessFlags
            val name = method.name
            val desc = buildDescriptor(method)
            val signature: String? = null
            val exceptions = buildExceptions(method)
            val mv = classWriter.visitMethod(access, name, desc, signature, exceptions)
            if (method.implementation != null) {
                if (method.name == "<init>" && desc == "()V") {
                    hasDefaultConstructor = true
                }
                visitMethod(classDef, method, mv)
            }
        }
        if (!hasDefaultConstructor) {
            val isInterface = Opcodes.ACC_INTERFACE and classDef.accessFlags != 0
            if (!isInterface) {
                /*
                 * Every class gets a default constructor. This is a give-away but it simplifies things. Right now,
                 * <init> stubs just call the empty arg / default constructor of the super, which may not exist. To do
                 * this properly, would need to maybe randomly pick a constructor and call that one, which may kind of
                 * one day affect correctness.
                 */
                val mods = Opcodes.ACC_PUBLIC
                val mv = classWriter.visitMethod(mods, "<init>", "()V", null, null)
                visitInitStub(classDef, mv)
            }
        }
    }

    private fun visitCallObjectHashCode(mv: MethodVisitor) {
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        val owner = "java/lang/Object"
        val name = "hashCode"
        val desc = "()I"
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, name, desc, false)
        mv.visitInsn(Opcodes.IRETURN)
    }

    private fun visitMethodStub(mv: MethodVisitor) {
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/RuntimeException")
        mv.visitInsn(Opcodes.DUP)
        mv.visitLdcInsn("Stub!")
        val owner = "java/lang/RuntimeException"
        val name = "<init>"
        val desc = "(Ljava/lang/String;)V"
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, name, desc, false)
        mv.visitInsn(Opcodes.ATHROW)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ClassBuilder::class.java.simpleName)

        @Throws(IOException::class, ClassNotFoundException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val smaliPath = args[0]
            val className = args[1]
            val classManager = ClassManagerFactory().build(smaliPath)
            val virtualClass = classManager.getVirtualClass(className)
            val builder = ClassBuilder(classManager)
            val classBytes = builder.build(virtualClass.classDef)
            FileOutputStream(File("temp.class")).write(classBytes)
            println("Generated class bytes for " + virtualClass + ", size=" + classBytes.size)
            val classLoader = SmaliClassLoader(classManager)
            classLoader.loadClass(virtualClass.sourceName)
        }
    }
}