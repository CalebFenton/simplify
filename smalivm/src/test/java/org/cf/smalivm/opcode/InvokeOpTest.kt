package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.type.UninitializedInstance
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm.ExecutionState
import org.cf.util.ClassNameUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class InvokeOpTest {
    @Nested
    inner class InstanceInitialization {
        private lateinit var expected: TestState
        private lateinit var initial: TestState

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }

        @Test
        fun objectClassInitializationCreatesCorrectClass() {
            val vm = Tester.spawnVM()
            val type = vm.classManager.getVirtualType("Lhash_code;")
            initial.setRegisters(0, UninitializedInstance(type), "Lhash_code;")
            val graph = Tester.execute("Lhash_code;", "<init>()V")
            val consensus = graph.getTerminatingRegisterConsensus(0)!!
            assertEquals("Lhash_code;", consensus.type)
        }

        @Test
        fun enumClassInitializationCreatesCorrectClass() {
            val vm = Tester.spawnVM()
            val type = vm.classManager.getVirtualType("Lextends_enum;")
            initial.setRegisters(0, UninitializedInstance(type), "Lextends_enum;", 1, "NONE", "Ljava/lang/String;", 2, 0, "I", 3, 0, "I")
            val graph = Tester.execute("Lextends_enum;", "<init>(Ljava/lang/String;II)V", initial)
            val consensus = graph.getTerminatingRegisterConsensus(0)!!
            assertEquals("Lextends_enum;", consensus.type)
        }
    }

    @Nested
    inner class Reflected {
        private lateinit var expected: TestState
        private lateinit var initial: TestState
        private val CLASS_NAME = "Linvoke_reflected_test;"

        @Test
        fun canInsertObjectIntoListAndGetObjectBack() {
            initial.setRegisters(0, LinkedList<Boolean>(), "Ljava/util/LinkedList;", 1, java.lang.Boolean.TRUE, "Ljava/lang/Boolean;", 2, 0, "I")
            expected.setRegisters(0, java.lang.Boolean.TRUE, "Ljava/lang/Boolean;")
            Tester.test(CLASS_NAME, "addToListAndGet()V", initial, expected)
        }

        @Test
        fun intValueCorrectlyCoaxedToNull() {
            // If Dalvik sees an I type with a value of 0 as an argument for an object parameter class, it's null
            // There is no check-cast in addToListAndGet so result value is either null or an integer
            initial.setRegisters(0, LinkedList<Int>(), "Ljava/util/LinkedList;", 1, 0, "I", 2, 0, "I")
            expected.setRegisters(0, null, "Ljava/lang/Object;")
            Tester.test(CLASS_NAME, "addToListAndGet()V", initial, expected)
        }

        @Test
        fun intValueCorrectlyNotCoaxedToNull() {
            initial.setRegisters(0, LinkedList<Int>(), "Ljava/util/LinkedList;", 1, 5, "Ljava/lang/Integer;", 2, 0, "I")
            expected.setRegisters(0, 5, "Ljava/lang/Integer;")
            Tester.test(CLASS_NAME, "addToListAndGet()V", initial, expected)
        }

        @Test
        fun intValueWhichLooksLikeNullNotCoaxedToNullWhenUsedAsPrimitive() {
            initial.setRegisters(0, 0, "I")
            expected.setRegisters(ExecutionState.RESULT_REGISTER, java.lang.Boolean.FALSE, "Ljava/lang/Boolean;")
            Tester.test(CLASS_NAME, "invokeBooleanValueOf()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }

        @Test
        fun initStringWithByteArrayWithUnknownParameter() {
            val vm = Tester.spawnVM(true)
            val instanceType = vm.classManager.getVirtualClass("Ljava/lang/String;")
            initial.setRegisters(0, UninitializedInstance(instanceType), "Ljava/lang/String;", 1, UnknownValue(), "[B")
            expected.setRegisters(0, UnknownValue(), "Ljava/lang/String;", 1, UnknownValue(), "[B")
            Tester.test(CLASS_NAME, "initStringWithByteArray()V", initial, expected)
        }

        @Test
        fun invokeLongValueOfWithLongGetsCorrectParameters() {
            initial.setRegisters(0, 0x1000L, "J")
            expected.setRegisters(0, 0x1000L, "J")
            Tester.test(CLASS_NAME, "invokeLongValueOf()V", initial, expected)
        }

        @Test
        fun invokeStringBuilderAppendWithLong() {
            val value = 0x1234L
            initial.setRegisters(0, StringBuilder(), "Ljava/lang/StringBuilder;", 1, value, "J")
            val sb = StringBuilder().append(value)
            expected.setRegisters(0, sb, "Ljava/lang/StringBuilder;", 1, value, "J")
            Tester.test(CLASS_NAME, "invokeStringBuilderAppendWithLong()V", initial, expected)
        }
    }

    @Nested
    inner class Static {
        private lateinit var expected: TestState
        private lateinit var initial: TestState
        private val CLASS_NAME = "Linvoke_static_test;"
        private val CLASS_WITH_STATIC_INIT = "Lclass_with_static_init;"

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }

        @Test
        fun invokeMethodCausingAssumeMaximumUnknownWithSignatureTypeLessSpecificThanKnownArgumentsTypeRetainsSpecificType() {
            initial.setRegisters(0, ByteArray(0), "[B", 1, 0, "I", 2, ByteArray(0), "[B", 3, 0, "I", 4, UnknownValue(), "I")
            expected.setRegisters(0, UnknownValue(), "[B", 1, 0, "I", 2, UnknownValue(), "[B", 3, 0, "I", 4, UnknownValue(), "I")
            Tester.test(CLASS_NAME, "invokeArrayCopy()V", initial, expected)
        }

        @Test
        fun invokeMethodCausingAssumeMaximumUnknownWithSignatureTypeLessSpecificThanUnknownArgumentsTypeRetainsSpecificType() {
            initial.setRegisters(0, UnknownValue(), "[B", 1, 0, "I", 2, UnknownValue(), "[B", 3, 0, "I", 4, UnknownValue(), "I")
            expected.setRegisters(0, UnknownValue(), "[B", 1, 0, "I", 2, UnknownValue(), "[B", 3, 0, "I", 4, UnknownValue(), "I")
            Tester.test(CLASS_NAME, "invokeArrayCopy()V", initial, expected)
        }

        @Test
        fun invokeMethodOutsideClassThatAccessesThisClassReturnsExpectedValue() {
            val value = "i have been initialized"
            initial.setFields(CLASS_NAME, "sometimes_initialized:Ljava/lang/String;", value)
            expected.setFields(CLASS_WITH_STATIC_INIT, "string:Ljava/lang/String;", "Uhhh, about 11, sir.")
            expected.setRegisters(ExecutionState.RESULT_REGISTER, value, "Ljava/lang/String;")
            Tester.test(CLASS_NAME, "invokeMethodOutsideClassThatAccessesThisClass()V", initial, expected)
        }

        @Test
        fun invokeMutateStaticClassFieldNonDeterministicallyPropagatesUnknown() {
            initial.setFields(CLASS_NAME, "mutable:[I", intArrayOf(3, 3, 3))
            initial.setRegisters(0, UnknownValue(), "I")
            expected.setFields(CLASS_NAME, "mutable:[I", UnknownValue())
            Tester.test(CLASS_NAME, "invokeMutateStaticClassField2()V", initial, expected)
        }

        @Test
        fun invokeMutateStaticClassFieldPropagatesChanges() {
            initial.setFields(CLASS_NAME, "mutable:[I", intArrayOf(3, 3, 3))
            expected.setFields(CLASS_NAME, "mutable:[I", intArrayOf(0, 3, 3))
            Tester.test(CLASS_NAME, "invokeMutateStaticClassField()V", initial, expected)
        }

        @Test
        fun invokeMutateStringBuilderWithMultipleCallerCopiesOfStringBuilderDoesMutateParameterAndCopy() {
            val type = "Ljava/lang/StringBuilder;"
            initial.setRegisters(0, StringBuilder("duty is heavy as a mountain"), type)
            val expectedValue = StringBuilder("duty is heavy as a mountain, death as light as a feather")
            expected.setRegisters(0, expectedValue, type, 1, expectedValue, type)
            Tester.test(CLASS_NAME, "invokeMutateStringBuilderWithMultipleCallerCopiesOfStringBuilder()V", initial, expected)
        }

        @Test
        fun invokeMutateStringBuilderDoesMutateParameter() {
            initial.setRegisters(0, StringBuilder("duty is heavy as a mountain"), "Ljava/lang/StringBuilder;")
            expected.setRegisters(0, StringBuilder("duty is heavy as a mountain, death as light as a feather"), "Ljava/lang/StringBuilder;")
            Tester.test(CLASS_NAME, "invokeMutateStringBuilder()V", initial, expected)
        }

        @Test
        fun invokeMutateStringBuilderAsThirdParameterAndFirstMutableParameterDoesMutateParameter() {
            initial.setRegisters(0, 0x10, "I", 1, 0x20, "I", 2, StringBuilder("duty is heavy as a mountain"), "Ljava/lang/StringBuilder;")
            expected.setRegisters(0, 0x10, "I", 1, 0x20, "I", 2, StringBuilder("duty is heavy as a mountain, death as light as a feather"), "Ljava/lang/StringBuilder;")
            Tester.test(CLASS_NAME, "invokeMutateStringBuilderAsThirdParameterAndFirstMutableParameter()V", initial, expected)
        }

        @Test
        fun invokeMutateStringDoesNotMutateParameter() {
            initial.setRegisters(0, "not mutated", "Ljava/lang/String;")
            expected.setRegisters(0, "not mutated", "Ljava/lang/String;")
            Tester.test(CLASS_NAME, "invokeMutateString()V", initial, expected)
        }

        @Test
        fun invokeReturnIntReturnsInt() {
            expected.setRegisters(ExecutionState.RESULT_REGISTER, 0x7, "I")
            Tester.test(CLASS_NAME, "invokeReturnInt()V", expected)
        }

        @Test
        fun invokeReturnParameterReturnsParameter() {
            initial.setRegisters(0, 0x5, "I")
            expected.setRegisters(ExecutionState.RESULT_REGISTER, 0x5, "I")
            Tester.test(CLASS_NAME, "invokeReturnParameter()V", initial, expected)
        }

        @Test
        fun invokeReturnUninitializedFieldReturnsNull() {
            expected.setRegisters(ExecutionState.RESULT_REGISTER, null, "Ljava/lang/String;")
            Tester.test(CLASS_NAME, "invokeReturnUninitializedField()V", expected)
        }

        @Test
        fun invokeReturnVoidReturnsVoid() {
            val graph = Tester.execute(CLASS_NAME, "invokeReturnVoid()V")
            val consensus = graph.getTerminatingRegisterConsensus(ExecutionState.RESULT_REGISTER)
            assertNull(consensus, "Consensus should be null")
        }

        @Test
        fun knownMutableParametersAreMutatedWithDeterministicExecution() {
            initial.setRegisters(0, intArrayOf(0x5), "[I", 1, 0, "I")
            expected.setRegisters(0, intArrayOf(0x0), "[I", 1, 0, "I")
            Tester.test(CLASS_NAME, "invokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial, expected)
        }

        @Test
        fun knownMutableParametersAreMutatedWithNonDeterministicExecution() {
            initial.setRegisters(0, intArrayOf(0x5), "[I", 1, UnknownValue(), "I")
            expected.setRegisters(0, UnknownValue(), "[I", 1, UnknownValue(), "I")
            Tester.test(CLASS_NAME, "invokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial, expected)
        }

        @Test
        fun possiblyUninitializedClassHasUnknownFieldValues() {
            initial.setRegisters(0, UnknownValue(), "I")
            expected.setFields(CLASS_WITH_STATIC_INIT, "string:Ljava/lang/String;", UnknownValue())
            Tester.test(CLASS_NAME, "initializeClassIfArgumentIsZero(I)V", initial, expected)
        }

        @Test
        fun alwaysThrownExceptionIsBubbledUp() {
            val graph = Tester.execute(CLASS_NAME, "invokeMethodWhichThrowsNullPointerException()V")
            val value = graph.getTerminatingRegisterConsensus(0)!!
            assertTrue(value.raw is NullPointerException)
            assertEquals(ClassNameUtils.toInternal(NullPointerException::class.java), value.type)
            val expectedAddresses = intArrayOf(0, 4, 5)
            Tester.testVisitation(graph, expectedAddresses)
            val throwItem = graph.getTerminatingRegisterConsensus(ExecutionState.THROW_REGISTER)
            assertEquals(value, throwItem)
        }

        @Test
        fun sometimesThrownExceptionExecutesExceptionalAndNormalExecutionPaths() {
            initial.setRegisters(0, UnknownValue(), "I")
            val graph = Tester.execute(CLASS_NAME, "invokeMethodWhichMayThrowNullPointerException()V", initial)
            val expectedAddresses = intArrayOf(0, 3, 4, 5)
            Tester.testVisitation(graph, expectedAddresses)
            val exceptionClass = "Ljava/lang/NullPointerException;"
            // Unknown type for register 0 because could be I or exceptionClass
            expected.setRegisters(ExecutionState.THROW_REGISTER, UnknownValue(), exceptionClass, 0, UnknownValue(), "?")
            Tester.testState(graph, expected)
        }
    }

    @Nested
    inner class Direct {
        private lateinit var expected: TestState
        private lateinit var initial: TestState
        private val CLASS_NAME = "Linvoke_direct_test;"
        private val CLASS_NAME_BINARY = "invoke_direct_test"

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }

        @Test
        @Throws(ClassNotFoundException::class)
        fun canCallInitOfClassWithDefaultConstructorAndGetNewInstance() {
            val instanceClassName = "Lclass_with_default_constructor;"
            val vm = Tester.spawnVM(false)
            val instanceType = vm.classManager.getVirtualClass(instanceClassName)
            val virtualClass: Class<*> = vm.classLoader.loadClass(ClassNameUtils.internalToBinary(instanceClassName))
            initial.setRegisters(0, UninitializedInstance(instanceType), instanceClassName)
            val graph = Tester.execute(CLASS_NAME, "callInitOfClassWithDefaultConstructor()V", initial, vm)
            //Tester.testState(graph, expected);
            val consensus = graph.getTerminatingRegisterConsensus(0)!!
            assertEquals(instanceClassName, consensus.type)
            assertEquals(virtualClass.name, consensus.raw!!::class.java.name)
        }

        @Test
        @Throws(ClassNotFoundException::class)
        fun canCallInitOfClassWithoutDefaultConstructorAndGetNewInstance() {
            val instanceClassName = "Lclass_without_default_constructor;"
            val vm = Tester.spawnVM(false)
            val instanceType = vm.classManager.getVirtualClass(instanceClassName)
            val virtualClass: Class<*> = vm.classLoader.loadClass(ClassNameUtils.internalToBinary(instanceClassName))
            val initArg = charArrayOf('4', '2')
            initial.setRegisters(0, UninitializedInstance(instanceType), instanceClassName, 1, initArg, "[C")
            expected.setRegisters(1, initArg, "[C")
            val graph = Tester.execute(CLASS_NAME, "callInitOfClassWithoutDefaultConstructor()V", initial, vm)
            Tester.testState(graph, expected)
            val consensus = graph.getTerminatingRegisterConsensus(0)!!
            assertEquals(instanceClassName, consensus.type)
            assertEquals(virtualClass.name, consensus.raw!!::class.java.name)
        }

        @Test
        @Throws(ClassNotFoundException::class)
        fun canCallInitOfClassWithoutDefaultConstructorWhichChecksForNullArgumentAndGetNewInstance() {
            val instanceClassName = "Lclass_without_default_constructor_checks_null;"
            val vm = Tester.spawnVM(false)
            val instanceType = vm.classManager.getVirtualClass(instanceClassName)
            val virtualClass: Class<*> = vm.classLoader.loadClass(ClassNameUtils.internalToBinary(instanceClassName))
            val initArg = charArrayOf('4', '2')
            initial.setRegisters(0, UninitializedInstance(instanceType), instanceClassName, 1, initArg, "[C")
            expected.setRegisters(1, initArg, "[C")
            val graph =
                Tester.execute(CLASS_NAME, "callInitOfClassWithoutDefaultConstructorWhichChecksForNullArgument()V", initial, vm)
            Tester.testState(graph, expected)
            val consensus = graph.getTerminatingRegisterConsensus(0)!!
            assertEquals(instanceClassName, consensus.type)
            assertEquals(virtualClass.name, consensus.raw!!::class.java.name)
        }
    }

    @Nested
    inner class Virtual {
        private lateinit var expected: TestState
        private lateinit var initial: TestState
        private val CLASS_NAME = "Linvoke_virtual_test;"
        private val CLASS_NAME_BINARY = "invoke_virtual_test"

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }

        @Test
        fun cloningArrayWithArrayCloneCloneWorksAsExpected() {
            initial.setRegisters(0, UnknownValue(), "[I")
            expected.setRegisters(0, UnknownValue(), "[I", 1, UnknownValue(), "[I")
            Tester.test(CLASS_NAME, "invokeArrayClone()V", initial, expected)
        }

        @Test
        fun cloningUnknownValueWithArraySignatureWithArrayCloneReturnsUnknown() {
            val array = intArrayOf(1, 2, 3)
            initial.setRegisters(0, array, "[I")
            expected.setRegisters(0, array, "[I", 1, array.clone(), "[I")
            Tester.test(CLASS_NAME, "invokeArrayClone()V", initial, expected)
        }

        @Test
        fun cloningArrayWithObjectCloneWorksAsExpected() {
            val array = intArrayOf(1, 2, 3)
            initial.setRegisters(0, array, "[I")
            expected.setRegisters(0, array, "[I", 1, array.clone(), "[I")
            Tester.test(CLASS_NAME, "invokeObjectClone()V", initial, expected)
        }

        @Test
        fun cloningNullWithArrayCloneThrowsException() {
            // Calling with Object clone (and not [Object clone) will always throw a NPE because
            // there's not enough type information to know it's an array clone.
            initial.setRegisters(0, null, "[Ljava/lang/Object;")
            val graph = Tester.execute(CLASS_NAME, "invokeArrayClone()V", initial)
            val value = graph.getTerminatingRegisterConsensus(0)!!
            assertTrue(value.raw is NullPointerException)
            assertEquals(ClassNameUtils.toInternal(NullPointerException::class.java), value.type)
            val expectedAddresses = intArrayOf(0, 5, 6)
            Tester.testVisitation(graph, expectedAddresses)
        }

        @Test
        @Throws(ClassNotFoundException::class)
        fun instanceInitializerWorksAsExpected() {
            // TODO: easy, try reloadClasses false, or explain why it's needed
            val vm = Tester.spawnVM(true)
            val instanceType = vm.classManager.getVirtualClass(CLASS_NAME)
            initial.setRegisters(0, UninitializedInstance(instanceType), CLASS_NAME)
            val virtualClass: Class<*> = vm.classLoader.loadClass(CLASS_NAME_BINARY)
            val graph = Tester.execute(CLASS_NAME, "<init>()V", initial, vm)

            // Can't simply put instance in the expected state because mutable objects get cloned.
            // The object parse at consensus would be different than the initial parse.
            val consensus = graph.getTerminatingRegisterConsensus(0)!!
            assertEquals(CLASS_NAME, consensus.type)
            assertEquals(virtualClass, consensus.raw!!::class.java)
        }

        @Test
        fun invokeGetComponentTypeOnPrimitiveArrayReturnsExpectedValue() {
            initial.setRegisters(0, IntArray(0), "[I")
            expected.setRegisters(ExecutionState.RESULT_REGISTER, Int::class.javaPrimitiveType, CommonTypes.CLASS)
            Tester.test(CLASS_NAME, "invokeGetComponentType()V", initial, expected)
        }

        @Test
        @Throws(ClassNotFoundException::class, IllegalAccessException::class, InstantiationException::class)
        fun invokeReturnIntReturnsInt() {
            val vm = Tester.spawnVM()
            val virtualClass: Class<*> = vm.classLoader.loadClass(CLASS_NAME_BINARY)
            val instance = virtualClass.newInstance()
            initial.setRegisters(0, instance, CLASS_NAME)
            expected.setRegisters(ExecutionState.RESULT_REGISTER, 0x7, "I")
            Tester.test(CLASS_NAME, "invokeReturnInt()V", initial, expected)
        }

        @Test
        @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
        fun invokeReturnParameterReturnsParameter() {
            val vm = Tester.spawnVM()
            val virtualClass: Class<*> = vm.classLoader.loadClass(CLASS_NAME_BINARY)
            val instance = virtualClass.newInstance()
            initial.setRegisters(0, instance, CLASS_NAME, 1, 0x5, "I")
            expected.setRegisters(1, 0x5, "I", ExecutionState.RESULT_REGISTER, 0x5, "I")
            val graph = Tester.execute(CLASS_NAME, "invokeReturnParameter()V", initial, vm)
            Tester.testState(graph, expected)

            // Can't simply put instance in the expected state because mutable objects get cloned.
            // The object class at consensus would be different than the initial class.
            val consensus = graph.getTerminatingRegisterConsensus(0)!!
            assertEquals(CLASS_NAME, consensus.type)
            assertEquals(virtualClass.name, consensus.raw!!::class.java.name)
        }

        @Test
        @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
        fun invokeReturnVoidReturnsVoid() {
            val vm = Tester.spawnVM(true)
            val virtualClass: Class<*> = vm.classLoader.loadClass(CLASS_NAME_BINARY)
            val instance = virtualClass.newInstance()
            initial.setRegisters(0, instance, CLASS_NAME)
            val graph = Tester.execute(CLASS_NAME, "invokeReturnVoid()V", initial)
            val consensus = graph.getTerminatingRegisterConsensus(ExecutionState.RESULT_REGISTER)
            assertNull(consensus)
        }

        @Test
        @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
        fun invokeVirtualManyParametersThrowsNoExceptions() {
            val vm = Tester.spawnVM()
            val virtualClass: Class<*> = vm.classLoader.loadClass(CLASS_NAME_BINARY)
            val instance = virtualClass.newInstance()
            initial.setRegisters(0, instance, CLASS_NAME, 1, 0x100L, "J", 3, 0x200L, "J", 5, 0x300L, "J", 7, 0x3, "I")
            expected.setRegisters(ExecutionState.RESULT_REGISTER, 0x3, "I")
            Tester.test(CLASS_NAME, "invokeRangeManyParameters()V", initial, expected)
        }

        @Test
        @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
        fun invokeGetClassOnSelfReturnsCorrectClass() {
            val vm = Tester.spawnVM()
            val virtualClass: Class<*> = vm.classLoader.loadClass(CLASS_NAME_BINARY)
            val instance = virtualClass.newInstance()
            initial.setRegisters(0, instance, CLASS_NAME)
            val graph = Tester.execute(CLASS_NAME, "invokeGetClassOnThis()V", initial, vm)
            val consensus = graph.getTerminatingRegisterConsensus(ExecutionState.RESULT_REGISTER)!!
            assertEquals(instance.javaClass, consensus.raw)
        }
    }

    @Nested
    inner class MethodStateProperties {
        private lateinit var initial: TestState
        private val CLASS_NAME = "Linvoke_static_test;"

        @BeforeEach
        fun setUp() {
            initial = TestState()
        }

        //WithTwoArrayParametersWithUnknownParameterMutatesAllParameters
        @Test
        fun invokeNonExistentMethodThrowsException() {
            val exception: Throwable = assertThrows<RuntimeException> {
                Tester.execute(CLASS_NAME, "invokeNonExistentMethod()V")
            }
            assertEquals(exception.message, "Can't find Smali file for Lim_not_your_friend_buddy;")
        }

        @Test
        fun hasExpectedToString() {
            val vm = Tester.spawnVM()
            val virtualClass = vm.classManager.getVirtualClass(CLASS_NAME)
            val method = virtualClass.getMethod("invokeReturnVoid()V")!!
            val graph = vm.getExecutionGraph(method)
            val op = graph.getOp(0)
            assertEquals("invoke-static {}, $CLASS_NAME->returnVoid()V", op.toString())
        }
    }
}
