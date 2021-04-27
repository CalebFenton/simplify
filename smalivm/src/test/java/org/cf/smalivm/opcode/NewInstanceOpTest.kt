package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester
import org.cf.smalivm.type.UninitializedInstance
import org.cf.smalivm2.VirtualMachine2
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NewInstanceOpTest {
    private lateinit var expected: TestState
    private lateinit var initial: TestState
    private lateinit var vm: VirtualMachine2

    companion object {
        private const val CLASS_NAME = "Lnew_instance_test;"
    }

    @BeforeEach
    fun setUp() {
        expected = TestState()
        initial = TestState()
        vm = Tester.spawnVM()
    }

//    @Test
//    @Throws(ClassNotFoundException::class)
//    fun canCreateLocalInstance() {
//        initial.setRegisters(0, 1, "I")
//        val graph = Tester.execute(CLASS_NAME, "newLocalInstance()V", initial, vm)
//        val instanceType = vm.classManager.getVirtualType(CLASS_NAME)
//        expected.setRegisters(0, UninitializedInstance(instanceType), CLASS_NAME)
//        Tester.testState(graph, expected)
//    }
//
//    @Test
//    fun canCreateNonLocalInstance() {
//        initial.setRegisters(0, 1, "I")
//        val graph = Tester.execute(CLASS_NAME, "newNonLocalInstance()V", initial, vm)
//        val instanceType = vm.classManager.getVirtualType("Ljava/lang/Integer;")
//        expected.setRegisters(0, UninitializedInstance(instanceType), "Ljava/lang/Integer;")
//        Tester.testState(graph, expected)
//    }
}
