package org.cf.smalivm

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.context.ExecutionGraph
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.dex.SmaliParser
import org.cf.smalivm.type.ClassManager2
import org.cf.smalivm.type.ClassManagerFactory
import org.cf.smalivm.type.VirtualMethod
import java.io.File
import java.io.IOException


class VirtualMachine2(
    val classManager: ClassManager2,
    val maxAddressVisits: Int = 500,
    val maxCallDepth: Int = 20,
    val maxMethodVisits: Int = 1_000_000,
    val maxExecutionTime: Int = 5 * 60,
    val outputAPILevel: Int = SmaliParser.DEX_API_LEVEL
) {
    val classLoader = SmaliClassLoader(classManager)
    val templateGraphs = HashMap<VirtualMethod, ExecutionGraph>()
    val configuration = Configuration.instance()
    var interactiveExecution = false
    //    val staticFieldAccessor = StaticFieldAccessor(this)
    //    val exceptionFactory = ExceptionFactory(this)

    fun execute(methodSignature: String) {
        val graph = ExecutionGraph()
    }

    fun main(args: Array<String>) {
        println("asdf")
    }

    constructor(
        inputPath: File, maxAddressVisits: Int, maxCallDepth: Int, maxMethodVisits: Int, maxExecutionTime: Int, , outputAPILevel: Int
    ): VirtualMachine {
        val factory = ClassManagerFactory()
        val manager = factory.build(inputPath, outputAPILevel)
        return VirtualMachine(manager, maxAddressVisits, maxCallDepth, maxMethodVisits, maxExecutionTime)
    }

    fun build(inputPath: File): VirtualMachine2? {
        return build(
            inputPath, SmaliParser.DEX_API_LEVEL, VirtualMachineFactory.DEFAULT_MAX_ADDRESS_VISITS, VirtualMachineFactory.DEFAULT_MAX_CALL_DEPTH,
            VirtualMachineFactory.DEFAULT_MAX_METHOD_VISITS, VirtualMachineFactory.DEFAULT_MAX_EXECUTION_TIME
        )
    }

    @Throws(IOException::class)
    fun build(inputPath: String?): VirtualMachine2? {
        return build(inputPath, SmaliParser.DEX_API_LEVEL)
    }

    @Throws(IOException::class)
    fun build(inputPath: String?, outputAPILevel: Int): VirtualMachine2? {
        return build(
            inputPath, outputAPILevel, VirtualMachineFactory.DEFAULT_MAX_ADDRESS_VISITS, VirtualMachineFactory.DEFAULT_MAX_CALL_DEPTH,
            VirtualMachineFactory.DEFAULT_MAX_METHOD_VISITS, VirtualMachineFactory.DEFAULT_MAX_EXECUTION_TIME
        )
    }

    @Throws(IOException::class)
    fun build(
        inputPath: String?, outputAPILevel: Int, maxAddressVisits: Int, maxCallDepth: Int,
        maxMethodVisits: Int, maxExecutionTime: Int
    ): VirtualMachine2? {
        return build(
            File(inputPath), outputAPILevel, maxAddressVisits, maxCallDepth, maxMethodVisits,
            maxExecutionTime
        )
    }
}
