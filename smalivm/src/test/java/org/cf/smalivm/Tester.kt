package org.cf.smalivm


import com.google.common.primitives.Ints
import org.cf.smalivm.dex.SmaliParser
import org.cf.smalivm.type.ClassManagerFactory
import org.cf.smalivm.type.VirtualMethod
import org.cf.util.ClassNameUtils
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.writer.builder.DexBuilder
import org.junit.jupiter.api.Assertions.*
import java.io.IOException
import java.util.*


object Tester {
    const val TEST_CLASS_PATH = "src/test/resources/smali"

    @JvmStatic
    var dexBuilder = DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL))

    @JvmStatic
    var classManager = ClassManagerFactory().build(TEST_CLASS_PATH, dexBuilder)

    @JvmStatic
    fun execute(className: String, methodDescriptor: String, vm: VirtualMachine = spawnVM()): ExecutionGraph {
        return vm.execute(className, methodDescriptor)
    }

    @JvmStatic
    fun execute(className: String, methodDescriptor: String, initial: TestState, vm: VirtualMachine = spawnVM()): ExecutionGraph {
        val method = classManager.getVirtualMethod(className, methodDescriptor)!!
        val state = buildInitialExecutionState(vm, method, initial)
        return vm.execute(method, state)
    }

    @JvmStatic
    fun test(className: String, methodSignature: String, expectedState: TestState) {
        test(className, methodSignature, TestState(), expectedState)
    }

    @JvmStatic
    fun test(className: String, methodSignature: String, initial: TestState, expected: TestState) {
        val graph = execute(className, methodSignature, initial)
        testState(graph, expected)
    }

    @JvmStatic
    fun testState(graph: ExecutionGraph, expected: TestState) {
        assertNotNull(graph, "Graph is null; method execution failed?")

        for ((register, value) in expected.registers) {
            val actual = graph.getTerminatingRegisterConsensus(register)
            testValueEquals(value, actual!!)
        }

        for ((className, fieldDescriptorToItem) in expected.fields) {
            val virtualClass = graph.classManager.getVirtualClass(className)
            for ((fieldDescriptor, value) in fieldDescriptorToItem) {
                val fieldName = fieldDescriptor.split(":").first()
                val field = virtualClass.getField(fieldName)!!
                val actual = graph.getTerminatingFieldConsensus(field)
                testValueEquals(value, actual)
            }
        }
    }

    @JvmStatic
    fun testVisitation(className: String, methodSignature: String, expectedAddresses: IntArray) {
        testVisitation(className, methodSignature, TestState(), expectedAddresses)
    }

    @JvmStatic
    fun testVisitation(className: String, methodSignature: String, initialState: TestState, expectedAddresses: IntArray) {
        val graph = execute(className, methodSignature, initialState)
        testVisitation(graph, expectedAddresses)
    }

    @JvmStatic
    fun testVisitation(graph: ExecutionGraph, expectedAddresses: IntArray) {
        val addresses: IntArray = graph.getAddresses()
        val visitedAddresses: MutableList<Int> = LinkedList<Int>()
        for (address in addresses) {
            if (graph.wasAddressReached(address)) {
                visitedAddresses.add(address)
            }
        }
        val actualAddresses = Ints.toArray(visitedAddresses)
        Arrays.sort(expectedAddresses)
        Arrays.sort(actualAddresses)
        assertArrayEquals(expectedAddresses, actualAddresses)
    }

    private fun buildInitialExecutionState(vm: VirtualMachine, method: VirtualMethod, initial: TestState): ExecutionState {
        val state = vm.spawnEntrypointState(method)
        for ((register, value) in initial.registers) {
            state.pokeRegister(register, value)
        }
        for ((className, fieldDescriptorToItem) in initial.fields) {
            val virtualClass = vm.classManager.getVirtualClass(className)
            for ((fieldNameAndType, item) in fieldDescriptorToItem) {
                val fieldName = fieldNameAndType.split(":")[0]
                val field = virtualClass.getField(fieldName)!!
                state.pokeField(field, item)
            }
            state.setClassInitialized(virtualClass, SideEffectLevel.NONE)
        }
        return state
    }


//    @JvmStatic
//    fun setRegisterMock(state: ExecutionState, register: Int, mockRaw: Any?, mockType: String) {
////        val value: Value = mockk() {
////            every { raw } returns mockRaw
////            every { type } returns mockType
////
////            // TODO: call actual methods for most of this?
////            every { componentBase } returns ClassNameUtils.getComponentBase(mockType)
////            every { toInteger() } answers { callOriginal() }
////            every { isUnknown } answers { callOriginal() }
////        }
////
////        every { value }
////        value
////        every { value.isKnown } answers { callOriginal() }
////        every { state.readRegister(register) } returns value
//    }

    /**
     * Create a new [VirtualMachineImpl] for testing. Since this is heavily used, it tries to avoid the main cost of creating a [ ] by reusing the same [ClassManagerImpl] by default.
     * If `reloadClasses` is true, a new [ClassManagerImpl] is created
     * and all classes are loaded again. This is necessary if method implementations are modified. For example, Simplify optimization strategy tests
     * modify method implementation and in order for each test to have the true method implementations, many of those tests set `reloadClasses`
     * to `true`.
     *
     * @param reloadClasses if true, rebuild [ClassManagerImpl], otherwise reuse existing
     * @return [VirtualMachineImpl] for tests
     */
    @JvmStatic
    fun spawnVM(reloadClasses: Boolean = false): VirtualMachine {
        if (reloadClasses) {
            try {
                dexBuilder = DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL))
                classManager = ClassManagerFactory().build(TEST_CLASS_PATH, dexBuilder)
            } catch (e: IOException) {
                throw RuntimeException("Exception building class manager for $TEST_CLASS_PATH", e)
            }
        }
        return VirtualMachine.build(classManager, maxExecutionTime = 0)
    }

    fun verifyContinueChild(children: Array<out UnresolvedChild>) {
        assertEquals(1, children.size)
        assertTrue(children[0] is UnresolvedContinueChild)
    }

    fun verifyExceptionChild(children: Array<out UnresolvedChild>, exceptionClass: Class<out Throwable>, message: String? = null) {
        assertEquals(1, children.size)
        assertTrue(children[0] is UnresolvedExceptionChild)
        assertEquals(exceptionClass, (children[0] as UnresolvedExceptionChild).exceptionClass)
        assertEquals(message, (children[0] as UnresolvedExceptionChild).message)
    }

    fun testSimpleException(
        className: String,
        methodDescriptor: String,
        exceptionClass: Class<*>,
        initial: TestState? = null,
        nextAddress: Int,
        exceptionMessage: String? = null
    ) {

        val graph = if (initial != null) {
            execute(className, methodDescriptor, initial)
        } else {
            execute(className, methodDescriptor)
        }
        val value = graph.getTerminatingRegisterConsensus(0)!!
        assertEquals(exceptionClass, value.raw!!.javaClass)
        assertEquals(ClassNameUtils.toInternal(exceptionClass), value.type)
        if (exceptionMessage != null) {
            assertEquals(exceptionMessage, (value.raw as Throwable).message)
        }
        assertFalse(graph.wasAddressReached(nextAddress), "Should not reach next address of non-exceptional execution path")
        val node = graph.getNodePile(0)[0]
        assertEquals(0, node.state.registersAssigned.size)
    }

    private fun testValueEquals(expected: Value, consensus: Value) {
        val expectedValue = expected.raw
        val consensusValue = consensus.raw
        if (expectedValue != null) {
            assertNotNull(consensusValue, "No consensus for value")
        }
        if (expectedValue == null) {
            assertEquals(expected, consensus)
        } else if (expected.isUnknown) {
            // Normally unknown doesn't equal anything, including itself, but tests are more relaxed.
            assertEquals(expected.toString(), consensus.toString())
        } else if (expectedValue.javaClass.isArray) {
            assertEquals(expected.type, consensus.type)
            assertEquals(expectedValue.javaClass, consensusValue!!.javaClass)
            if (expectedValue is Array<*> && consensusValue is Array<*>) {
                assertArrayEquals(expectedValue as Array<Any?>?, consensusValue as Array<Any?>?)
            } else if (expectedValue is ByteArray && consensusValue is ByteArray) {
                assertArrayEquals(expectedValue as ByteArray?, consensusValue as ByteArray?)
            } else if (expectedValue is ShortArray && consensusValue is ShortArray) {
                assertArrayEquals(expectedValue as ShortArray?, consensusValue as ShortArray?)
            } else if (expectedValue is IntArray && consensusValue is IntArray) {
                assertArrayEquals(expectedValue as IntArray?, consensusValue as IntArray?)
            } else if (expectedValue is LongArray && consensusValue is LongArray) {
                assertArrayEquals(expectedValue as LongArray?, consensusValue as LongArray?)
            } else if (expectedValue is CharArray && consensusValue is CharArray) {
                assertArrayEquals(expectedValue as CharArray?, consensusValue as CharArray?)
            } else if (expectedValue is FloatArray && consensusValue is FloatArray) {
                assertArrayEquals(expectedValue as FloatArray?, consensusValue as FloatArray?, 0.001f)
            } else if (expectedValue is DoubleArray && consensusValue is DoubleArray) {
                assertArrayEquals(expectedValue as DoubleArray?, consensusValue as DoubleArray?, 0.001)
            } else if (expectedValue is BooleanArray && consensusValue is BooleanArray) {
                assertArrayEquals(expectedValue as BooleanArray?, consensusValue as BooleanArray?)
            } else {
                assertEquals(expectedValue, consensusValue)
            }
        } else if (expectedValue is StringBuilder) {
            assertEquals(expectedValue.toString(), consensusValue.toString())
        } else {
            assertEquals(expectedValue, consensusValue)
            assertEquals(expected.type, consensus.type)
        }
    }
}
