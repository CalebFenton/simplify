package org.cf.smalivm.dex

import org.cf.smalivm.Tester
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.ClassManagerFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.lang.reflect.Array
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import java.util.stream.Collectors
import javax.crypto.CipherSpi
import javax.crypto.interfaces.PBEKey
import javax.crypto.spec.PBEKeySpec

class SmaliClassLoaderTest {
    private var classLoader: SmaliClassLoader? = null
    private var classManager: ClassManager? = null

    @Test
    @Throws(ClassNotFoundException::class)
    fun canLoadEnumClassAndGetEnumValue() {
        val klazz = classLoader!!.loadClass("android.annotation.SdkConstant\$SdkConstantType")
        val enumConstant: Any = java.lang.Enum.valueOf(klazz as Class<out Enum<*>?>, "ACTIVITY_INTENT_ACTION")!!
        Assertions.assertEquals(klazz, enumConstant.javaClass)
        val enumStrings = klazz.enumConstants.map { it -> it.toString() }.toTypedArray()
        val expectedEnumStrings = arrayOf(
            "ACTIVITY_INTENT_ACTION", "BROADCAST_INTENT_ACTION", "FEATURE", "INTENT_CATEGORY",
            "SERVICE_ACTION", "\$shadow_instance"
        )
        Assertions.assertArrayEquals(expectedEnumStrings, enumStrings)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun canLoadEnumClassWithNonEnumField() {
        val klazz = classLoader!!.loadClass("android.net.wifi.SupplicantState")
        val enumConstant: Any = java.lang.Enum.valueOf(klazz as Class<out Enum<*>?>, "ASSOCIATED")!!
        Assertions.assertEquals(klazz, enumConstant.javaClass)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun loadingClassWhichReferencesNonExistentClassThrowsExceptionDuringVerification() {
        val className = "org.cf.test.NonExistentReference"
        val exception: Throwable = Assertions.assertThrows(NoClassDefFoundError::class.java) {
            val klazz = classLoader!!.loadClass(className)
            klazz.getMethods()
        }
        println(exception.message)
        Assertions.assertEquals(exception.message, "does/not/exist")
    }

    @Test
    @Throws(Exception::class)
    fun canLoadClassWithCircularReferences() {
        val className1 = "org.cf.test.CircularReference1"
        val klazz1 = classLoader!!.loadClass(className1)
        val className2 = "org.cf.test.CircularReference2"
        val klazz2 = classLoader!!.loadClass(className2)
        assertHasObjectMethods(klazz1)
        assertHasObjectMethods(klazz2)
        val methods1 = getFilteredMethods(klazz1)
        Assertions.assertEquals(1, methods1.size)
        val methods2 = getFilteredMethods(klazz2)
        Assertions.assertEquals(1, methods2.size)
        Assertions.assertEquals(methods1[0].returnType, klazz2)
        Assertions.assertEquals(methods2[0].returnType, klazz1)
    }

    @Test
    @Throws(Exception::class)
    fun canLoadClassWithOverloadedFields() {
        val className = "org.cf.test.OverloadedFields"
        val klazz = classLoader!!.loadClass(className)
        assertHasObjectMethods(klazz)
        val fields = getFilteredFields(klazz)
        Assertions.assertEquals(2, fields.size)
        Assertions.assertEquals("public static int org.cf.test.OverloadedFields.field1", fields[0].toString())
        Assertions.assertEquals("public static java.lang.Object org.cf.test.OverloadedFields.field1", fields[1].toString())
    }

    @Test
    @Throws(Exception::class)
    fun canLoadClassWithIllegalFieldModifiers() {
        val className = "org.cf.test.IllegalFieldModifiers"
        val klazz = classLoader!!.loadClass(className)
    }

    @Test
    @Throws(Exception::class)
    fun canLoadComplexClass() {
        val className = "org.cf.test.ComplexClass"
        val klazz = classLoader!!.loadClass(className)
        assertHasObjectMethods(klazz)
        val superName = "org.cf.test.SuperClass"
        val superClass = classLoader!!.loadClass(superName)
        Assertions.assertEquals(superName, superClass.name)
        Assertions.assertEquals(superClass, klazz.superclass)
        val methods = getFilteredMethods(klazz)
        Assertions.assertEquals(6, methods.size)
        val fields = getFilteredFields(klazz)
        Assertions.assertEquals(1, fields.size)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun loadingProtectedJavaClassesReturnsRunningJVMClass() {
        val expectedClasses = arrayOf(String::class.java, Int::class.java, Any::class.java)
        for (expected in expectedClasses) {
            val actual = classLoader!!.loadClass(expected.name)
            Assertions.assertEquals(expected, actual)
        }
    }

    @Test
    @Throws(ClassNotFoundException::class, NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
    fun loadingNonProtectedFrameworkClassesWhichAreAlsoInJVMReturnsFrameworkJarClass() {
        val expectedClassNames = arrayOf(
            PBEKeySpec::class.java.name, PBEKey::class.java.name, CipherSpi::class.java.name
        )
        for (expectedClassName in expectedClassNames) {
            val actual = classLoader!!.loadClass(expectedClassName)
            Assertions.assertEquals(expectedClassName, actual.name)
            val jvmClass = classLoader!!.parent.loadClass(expectedClassName)
            Assertions.assertNotEquals(jvmClass, actual)
        }
    }

    @Test
    @Throws(Exception::class)
    fun canLoadSelfReferencingClass() {
        val className = "org.cf.test.SelfReference"
        val klazz = classLoader!!.loadClass(className)
        assertHasObjectMethods(klazz)
        val fields = getFilteredFields(klazz)
        Assertions.assertEquals(1, fields.size)
        val arrayClass: Class<*> = Array.newInstance(klazz, 1).javaClass
        Assertions.assertEquals(arrayClass, fields[0].type)
        val methods = getFilteredMethods(klazz)
        Assertions.assertEquals(1, methods.size)
        val parameterTypes = methods[0].parameterTypes
        Assertions.assertEquals(1, parameterTypes.size)
        Assertions.assertEquals(klazz, parameterTypes[0])
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun canLoadSimpleClass() {
        val className = "org.cf.test.SimpleClass"
        val klazz = classLoader!!.loadClass(className)
        assertHasObjectMethods(klazz)
        val methods = getFilteredMethods(klazz)
        Assertions.assertEquals(1, methods.size)
        Assertions.assertEquals(
            "public static void $className.simpleLoop(int,java.lang.Object)",
            methods[0].toString()
        )
    }

    @BeforeEach
    @Throws(IOException::class)
    fun setUp() {
        val managerFactory = ClassManagerFactory()
        classManager = managerFactory.build(TEST_SMALI_PATH)
        classLoader = SmaliClassLoader(classManager)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun throwsExceptionWhenLoadingNonExistentAndReferencedClass() {
        val nonExistentClassName = "does.not.exist"
        val exception: Throwable = Assertions.assertThrows(ClassNotFoundException::class.java) { classLoader!!.loadClass(nonExistentClassName) }
        Assertions.assertEquals(exception.message, nonExistentClassName)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun throwsExceptionWhenLoadingNonExistentAndUnreferencedClass() {
        val nonExistentClassName = "asdfasdf"
        val exception: Throwable = Assertions.assertThrows(ClassNotFoundException::class.java) { classLoader!!.loadClass(nonExistentClassName) }
        Assertions.assertEquals(exception.message, nonExistentClassName)
    }

    private fun assertHasObjectMethods(klazz: Class<*>) {
        val methods = Arrays.stream(klazz.methods).map { obj: Method -> obj.toString() }
        val methodDescriptors = methods.collect(Collectors.toList())
        for (objectMethod in OBJECT_METHODS) {
            Assertions.assertTrue(methodDescriptors.contains(objectMethod), "Must have method: $objectMethod")
        }
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

    companion object {
        private val OBJECT_METHODS = arrayOf(
            "public boolean java.lang.Object.equals(java.lang.Object)",
            "public final native java.lang.Class java.lang.Object.getClass()",
            "public final native void java.lang.Object.notify()",
            "public final native void java.lang.Object.notifyAll()",
            "public final native void java.lang.Object.wait(long) throws java.lang.InterruptedException",
            "public final void java.lang.Object.wait() throws java.lang.InterruptedException",
            "public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException",
            "public java.lang.String java.lang.Object.toString()",
            "public native int java.lang.Object.hashCode()"
        )
        private const val TEST_SMALI_PATH = Tester.TEST_CLASS_PATH + "/class_builder"
    }
}
