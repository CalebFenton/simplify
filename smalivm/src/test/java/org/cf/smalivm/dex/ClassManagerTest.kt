package org.cf.smalivm.dex

import org.cf.smalivm.Tester
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.ClassManagerFactory
import org.cf.smalivm.type.VirtualArray
import org.cf.smalivm.type.VirtualPrimitive
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.IOException

class ClassManagerTest {
    @Test
    fun `Can get virtual class from string`() {
        val virtualClass = classManager.getVirtualClass(CommonTypes.STRING)
        Assertions.assertEquals(CommonTypes.STRING, virtualClass.name)
    }

    @Test
    fun `Can get virtual class from class`() {
        val virtualClass = classManager.getVirtualClass(String::class.java)
        Assertions.assertEquals(CommonTypes.STRING, virtualClass.name)
    }

    @Test
    fun `Can get virtual primitive from string`() {
        val virtualType = classManager.getVirtualType(CommonTypes.INTEGER)
        Assertions.assertEquals(CommonTypes.INTEGER, virtualType.name)
        Assertions.assertTrue(virtualType is VirtualPrimitive)
    }

    @Test
    fun `Can get virtual primitive from class`() {
        val virtualType = classManager.getVirtualType(CommonTypes.INTEGER)
        Assertions.assertEquals(CommonTypes.INTEGER, virtualType.name)
        Assertions.assertTrue(virtualType is VirtualPrimitive)
    }

    @Test
    fun `Can get virtual array from string`() {
        val typeSignature = "[" + CommonTypes.STRING
        val virtualType = classManager.getVirtualType(typeSignature)
        Assertions.assertEquals(typeSignature, virtualType.name)
        Assertions.assertTrue(virtualType is VirtualArray)
    }

    @Test
    fun `Can get virtual array from class`() {
        val virtualType = classManager.getVirtualType(Array<String>::class.java)
        Assertions.assertEquals("[" + CommonTypes.STRING, virtualType.name)
        Assertions.assertTrue(virtualType is VirtualArray)
    }

    companion object {
        private lateinit var classManager: ClassManager

        @BeforeAll
        @JvmStatic
        @Throws(IOException::class)
        fun getClassManager() {
            classManager = ClassManagerFactory().build(Tester.TEST_CLASS_PATH)
        }
    }
}
