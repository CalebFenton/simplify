package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester
import org.cf.smalivm.dex.CommonTypes
import org.cf.util.ClassNameUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ConstOpTest {
    private lateinit var expected: TestState

    companion object {
        private const val CLASS_NAME = "Lconst_test;"
    }

    @BeforeEach
    fun setUp() {
        expected = TestState()
    }

    @Test
    fun canConst() {
        expected.setRegisters(0, 0x42424242, "I")
        Tester.test(CLASS_NAME, "const()V", expected)
    }

    @Test
    fun canConst16() {
        expected.setRegisters(0, 0x4242, "I")
        Tester.test(CLASS_NAME, "const16()V", expected)
    }

    @Test
    fun canConst4() {
        expected.setRegisters(0, -3, "I")
        Tester.test(CLASS_NAME, "const4()V", expected)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun canConstClassLocal() {
        val vm = Tester.spawnVM()
        val expectedClass: Class<*> = vm.classLoader.loadClass(ClassNameUtils.internalToBinary(CLASS_NAME))
        expected.setRegisters(0, expectedClass, CommonTypes.CLASS)
        val graph = Tester.execute(CLASS_NAME, "constClassLocal()V", vm)
        Tester.testState(graph, expected)
    }

    @Test
    fun canConstClassRemote() {
        expected.setRegisters(0, Any::class.java, CommonTypes.CLASS)
        Tester.test(CLASS_NAME, "constClassRemote()V", expected)
    }

    @Test
    fun canConstHigh16() {
        expected.setRegisters(0, 0x42420000, "I")
        Tester.test(CLASS_NAME, "constHigh16()V", expected)
    }

    @Test
    fun canConstString() {
        expected.setRegisters(
            0, "When I need to identify rebels, I look for men with principles.",
            "Ljava/lang/String;"
        )
        Tester.test(CLASS_NAME, "constString()V", expected)
    }

    @Test
    fun canConstStringJumbo() {
        expected.setRegisters(
            0,
            "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles.",
            "Ljava/lang/String;"
        )
        Tester.test(CLASS_NAME, "constStringJumbo()V", expected)
    }

    @Test
    fun canConstWide() {
        expected.setRegisters(0, 0x4242424242424242L, "D")
        Tester.test(CLASS_NAME, "constWide()V", expected)
    }

    @Test
    fun canConstWide16() {
        expected.setRegisters(0, 0x4242L, "J")
        Tester.test(CLASS_NAME, "constWide16()V", expected)
    }

    @Test
    fun canConstWide32() {
        expected.setRegisters(0, 0x1b7740L, "J")
        Tester.test(CLASS_NAME, "constWide32()V", expected)
    }

    @Test
    fun canConstWideHigh16() {
        expected.setRegisters(0, 0x4242000000000000L, "J")
        Tester.test(CLASS_NAME, "constWideHigh16()V", expected)
    }

    @Test
    fun canNegativeConst16() {
        expected.setRegisters(0, -0xf, "I")
        Tester.test(CLASS_NAME, "const16Negative()V", expected)
    }

    @Test
    fun nonExistentClassNameThrowsException() {
        Tester.testSimpleException(CLASS_NAME, "constClassUnknown()V", ClassNotFoundException::class.java, null, 2)
    }
}
