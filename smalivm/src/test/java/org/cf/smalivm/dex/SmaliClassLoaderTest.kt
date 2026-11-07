package org.cf.smalivm.dex

import android.util.Base64
import org.cf.smalivm.core.ClassIndex
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.reflect.Array
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import javax.crypto.CipherSpi
import javax.crypto.interfaces.PBEKey
import javax.crypto.spec.PBEKeySpec

class SmaliClassLoaderTest {
    private lateinit var classLoader: SmaliClassLoader

    companion object {
        private val OBJECT_METHODS = listOf(
            "public boolean java.lang.Object.equals(java.lang.Object)",
            "public final native java.lang.Class java.lang.Object.getClass()",
            "public final native void java.lang.Object.notify()",
            "public final native void java.lang.Object.notifyAll()",
            "public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException",
            "public final void java.lang.Object.wait() throws java.lang.InterruptedException",
            "public final void java.lang.Object.wait(long) throws java.lang.InterruptedException",
            "public java.lang.String java.lang.Object.toString()",
        )
        private const val TEST_SMALI_PATH = "src/test/resources/smali/class_builder"
    }

    @BeforeEach
    fun setUp() {
        classLoader = ClassIndex.open(File(TEST_SMALI_PATH)).classLoader
    }

    @Test
    fun bundledBase64KeepsHostClassIdentityAndExecutesEncodingAndDecoding() {
        val runtimeClass = classLoader.loadClass("android.util.Base64")
        assertSame(Base64::class.java, runtimeClass)
        for (helper in listOf("Coder", "Decoder", "Encoder")) {
            val name = "android.util.Base64\$$helper"
            assertSame(
                Class.forName(name, false, Base64::class.java.classLoader),
                classLoader.loadClass(name),
            )
        }

        val bytes = byteArrayOf(0x68, 0x69)
        val encode = runtimeClass.getMethod("encodeToString", ByteArray::class.java, Integer.TYPE)
        assertEquals("aGk=", encode.invoke(null, bytes, 2))
        val decode = runtimeClass.getMethod("decode", String::class.java, Integer.TYPE)
        assertArrayEquals(bytes, decode.invoke(null, "aGk=", 2) as ByteArray)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun canLoadEnumClassAndGetEnumValue() {
        val klazz = classLoader.loadClass("android.annotation.SdkConstant\$SdkConstantType")
        val enumConstant: Any = java.lang.Enum.valueOf(klazz as Class<out Enum<*>?>, "ACTIVITY_INTENT_ACTION")!!
        assertEquals(klazz, enumConstant.javaClass)
        val enumStrings = klazz.enumConstants.map { it.toString() }.toTypedArray()
        val expectedEnumStrings = arrayOf(
            "ACTIVITY_INTENT_ACTION", "BROADCAST_INTENT_ACTION", "FEATURE", "INTENT_CATEGORY",
            "SERVICE_ACTION", "\$shadow_instance"
        )
        assertArrayEquals(expectedEnumStrings, enumStrings)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun canLoadEnumClassWithNonEnumField() {
        val klazz = classLoader.loadClass("android.net.wifi.SupplicantState")
        val enumConstant: Any = java.lang.Enum.valueOf(klazz as Class<out Enum<*>?>, "ASSOCIATED")!!
        assertEquals(klazz, enumConstant.javaClass)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun loadingClassWhichReferencesNonExistentClassThrowsExceptionDuringVerification() {
        val className = "org.cf.test.NonExistentReference"
        val exception = assertThrows(NoClassDefFoundError::class.java) {
            val klazz = classLoader.loadClass(className)
            klazz.getMethods()
        }
        assertEquals("does/not/exist", exception.message)
    }

    @Test
    @Throws(Exception::class)
    fun canLoadClassWithCircularReferences() {
        val className1 = "org.cf.test.CircularReference1"
        val klazz1 = classLoader.loadClass(className1)
        val className2 = "org.cf.test.CircularReference2"
        val klazz2 = classLoader.loadClass(className2)
        assertHasObjectMethods(klazz1)
        assertHasObjectMethods(klazz2)
        val methods1 = getFilteredMethods(klazz1)
        assertEquals(1, methods1.size)
        val methods2 = getFilteredMethods(klazz2)
        assertEquals(1, methods2.size)
        assertEquals(methods1[0].returnType, klazz2)
        assertEquals(methods2[0].returnType, klazz1)
    }

    @Test
    @Throws(Exception::class)
    fun canLoadClassWithOverloadedFields() {
        val className = "org.cf.test.OverloadedFields"
        val klazz = classLoader.loadClass(className)
        assertHasObjectMethods(klazz)
        val fields = getFilteredFields(klazz)
        assertEquals(2, fields.size)
        assertEquals("public static int org.cf.test.OverloadedFields.field1", fields[0].toString())
        assertEquals("public static java.lang.Object org.cf.test.OverloadedFields.field1", fields[1].toString())
    }

    @Test
    fun interfacePrivateFieldsArePublicSoTheJvmLinksTheInterface() {
        val klazz = classLoader.loadClass("org.cf.test.IllegalFieldModifiers")

        val fields = getFilteredFields(klazz)
        assertTrue(fields.isNotEmpty())
        assertTrue(fields.all { Modifier.isPublic(it.modifiers) }, fields.toString())
    }

    @Test
    @Throws(Exception::class)
    fun canLoadComplexClass() {
        val className = "org.cf.test.ComplexClass"
        val klazz = classLoader.loadClass(className)
        assertHasObjectMethods(klazz)
        val superName = "org.cf.test.SuperClass"
        val superClass = classLoader.loadClass(superName)
        assertEquals(superName, superClass.name)
        assertEquals(superClass, klazz.superclass)
        val methods = getFilteredMethods(klazz)
        assertEquals(6, methods.size)
        val fields = getFilteredFields(klazz)
        assertEquals(1, fields.size)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun loadingProtectedJavaClassesReturnsRunningJVMClass() {
        val expectedClasses = arrayOf(String::class.java, Integer::class.java, Object::class.java)
        for (expected in expectedClasses) {
            val actual = classLoader.loadClass(expected.name)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun frameworkClassesAlsoInTheJvmAreBuiltFromFrameworkSmali() {
        val jvmClasses = listOf(PBEKeySpec::class.java, PBEKey::class.java, CipherSpi::class.java)
        for (jvmClass in jvmClasses) {
            val actual = classLoader.loadClass(jvmClass.name)
            assertEquals(jvmClass.name, actual.name)
            assertNotEquals(jvmClass, actual)
            assertSame(classLoader, actual.classLoader)
        }
    }

    @Test
    @Throws(Exception::class)
    fun canLoadSelfReferencingClass() {
        val className = "org.cf.test.SelfReference"
        val klazz = classLoader.loadClass(className)
        assertHasObjectMethods(klazz)
        val fields = getFilteredFields(klazz)
        assertEquals(1, fields.size)
        val arrayClass: Class<*> = Array.newInstance(klazz, 1).javaClass
        assertEquals(arrayClass, fields[0].type)
        val methods = getFilteredMethods(klazz)
        assertEquals(1, methods.size)
        val parameterTypes = methods[0].parameterTypes
        assertEquals(1, parameterTypes.size)
        assertEquals(klazz, parameterTypes[0])
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun canLoadSimpleClass() {
        val className = "org.cf.test.SimpleClass"
        val klazz = classLoader.loadClass(className)
        assertHasObjectMethods(klazz)
        val methods = getFilteredMethods(klazz)
        assertEquals(1, methods.size)
        assertEquals("public static void $className.simpleLoop(int,java.lang.Object)", methods[0].toString())
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun throwsExceptionWhenLoadingNonExistentAndReferencedClass() {
        val nonExistentClassName = "does/not/exist"
        val exception: Throwable = assertThrows(ClassNotFoundException::class.java) { classLoader.loadClass(nonExistentClassName) }
        assertEquals(exception.message, nonExistentClassName)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun throwsExceptionWhenLoadingNonExistentAndUnreferencedClass() {
        val nonExistentClassName = "asdfasdf"
        val exception = assertThrows(ClassNotFoundException::class.java) { classLoader.loadClass(nonExistentClassName) }
        assertEquals(exception.message, nonExistentClassName)
    }

    private fun assertHasObjectMethods(klazz: Class<*>) {
        val methodDescriptors = klazz.methods.map { it.toString() }.toSet()
        val missing = OBJECT_METHODS.filterNot(methodDescriptors::contains)
        assertEquals(emptyList<String>(), missing)
    }

    private fun getFilteredFields(klazz: Class<*>): List<Field> {
        val fields: MutableList<Field> = LinkedList()
        for (f in klazz.declaredFields) {
            if (f.name == "\$jacocoData") {
                // This is added by JaCoCo when testing from Gradle.
                continue
            }
            fields.add(f)
        }
        return fields
    }

    private fun getFilteredMethods(klazz: Class<*>): List<Method> {
        val methods: MutableList<Method> = LinkedList()
        for (m in klazz.declaredMethods) {
            if (m.name == "\$jacocoInit") {
                // This is the "probe" method added by JaCoCo when running from Gradle
                continue
            }
            methods.add(m)
        }
        return methods
    }
}
