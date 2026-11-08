package org.cf.smalivm.dex

import org.cf.smalivm.core.ApplicationClassDefinition
import org.cf.smalivm.core.ProgramSnapshot
import org.cf.util.ClassNameUtils
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef

/**
 * Defines host classes with the declared shape of [snapshot]'s application and framework classes; every method
 * body is a stub. Framework classes are defined child-first so their Android shapes replace same-named JVM
 * classes; application classes are defined only when the parent loader lacks the name. Defining a class does
 * not authorize reflection: callers apply the reflection policy before executing anything.
 */
internal class SmaliClassLoader internal constructor(private val snapshot: ProgramSnapshot) :
    ClassLoader(SmaliClassLoader::class.java.classLoader) {
    private val classBuilder = ClassBuilder { type -> snapshot.hierarchy.declarations[type]?.accessFlags }

    override fun loadClass(name: String, resolve: Boolean): Class<*> = synchronized(getClassLoadingLock(name)) {
        findLoadedClass(name)?.let { return it }
        if (name in BUNDLED_RUNTIME_CLASSES) return parent.loadClass(name)
        if (isFrameworkClass(name)) {
            try {
                return findClass(name)
            } catch (_: ClassNotFoundException) {
                // A java.* framework class: the JVM's own definition is the only one the host can have.
            }
        }
        return super.loadClass(name, resolve)
    }

    override fun findClass(name: String): Class<*> {
        // The JVM refuses to define java.* classes in any loader but its own.
        if (name.startsWith("java.")) throw ClassNotFoundException(name)
        val classDef = classDefinition(ClassNameUtils.binaryToInternal(name)) ?: throw ClassNotFoundException(name)
        // The shape comes from the analysed input; a shape the host cannot build is a linkage failure.
        val bytes = try {
            classBuilder.build(classDef)
        } catch (failure: RuntimeException) {
            throw ClassFormatError("Cannot build a host class for $name: ${failure.message}").apply { initCause(failure) }
        }
        val packageName = name.substringBeforeLast('.', "")
        if (packageName.isNotEmpty() && getDefinedPackage(packageName) == null) {
            definePackage(packageName, null, null, null, null, null, null, null)
        }
        return defineClass(name, bytes, 0, bytes.size)
    }

    private fun isFrameworkClass(name: String): Boolean =
        ClassNameUtils.binaryToInternal(name) in snapshot.frameworkCatalog.artifact.classes

    private fun classDefinition(type: String): ClassDef? =
        snapshot.applicationClass(type)?.let(::declaredShape) ?: snapshot.frameworkCatalog.classDefinition(type)

    private companion object {
        // Vendored AOSP Base64.java supplies exactly this runtime family. Keep its helper classes with the
        // outer class; synthesizing any of them would replace executable methods with throwing stubs.
        val BUNDLED_RUNTIME_CLASSES = setOf(
            "android.util.Base64",
            "android.util.Base64\$Coder",
            "android.util.Base64\$Decoder",
            "android.util.Base64\$Encoder",
        )

        val DIRECT_METHOD_FLAGS = AccessFlags.STATIC.value or AccessFlags.PRIVATE.value or AccessFlags.CONSTRUCTOR.value

        fun declaredShape(definition: ApplicationClassDefinition): ClassDef {
            val (direct, virtual) = definition.methods.map { it.declaration }
                .partition { it.accessFlags and DIRECT_METHOD_FLAGS != 0 }
            return ImmutableClassDef(
                definition.type,
                definition.accessFlags,
                definition.superclass,
                definition.interfaces,
                definition.sourceFile,
                definition.annotations,
                definition.staticFields.map { it.declaration },
                definition.instanceFields.map { it.declaration },
                direct,
                virtual,
            )
        }
    }
}
