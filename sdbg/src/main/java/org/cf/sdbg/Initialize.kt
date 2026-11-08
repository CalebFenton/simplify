package org.cf.sdbg

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.cf.smalivm.core.ClassIndex
import org.cf.smalivm.core.MalformedSignatureException
import org.cf.smalivm.core.MethodLookup
import org.cf.smalivm.core.MethodSignature
import org.cf.smalivm.core.VmServices
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.io.File
import java.util.concurrent.Callable

@CommandLine.Command(mixinStandardHelpOptions = true, versionProvider = ManifestVersion::class,
        description = ["Smali DeBuGger (SDBG)"])
internal class Initialize : Callable<Int> {
    /** The session this invocation started; null after `--help` or `--version`, which start none. */
    var debugSession: DebugSession? = null
        private set

    @CommandLine.Parameters(index = "0", paramLabel = "input-path", arity = "1",
            description = ["Input directory containing Smali"])
    private lateinit var inputFile: File
    @CommandLine.Parameters(index = "1", paramLabel = "method-signature", arity = "1",
            description = ["Method signature to execute"])
    private lateinit var methodSignature: String

    @CommandLine.Option(names = ["-v", "--verbose"],
            description = ["Specify multiple -v options to increase verbosity.", "For example, `-v -v -v` or `-vvv`"])
    private var verbosity = BooleanArray(0)

    private fun setLogLevel(verbosity: Int) {
        val rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        when (verbosity) {
            0 -> { rootLogger.level = Level.OFF }
            1 -> { rootLogger.level = Level.INFO }
            2 -> { rootLogger.level = Level.DEBUG }
            else -> { rootLogger.level = Level.TRACE }
        }
    }

    override fun call(): Int {
        setLogLevel(verbosity.size)
        val method = try {
            MethodSignature.parse(methodSignature)
        } catch (_: MalformedSignatureException) {
            null
        }
        require(method != null) { "Expected a method signature Lpkg/Name;->name(params)return: $methodSignature" }
        println("Starting debugger for $methodSignature from $inputFile")
        val services = VmServices(ClassIndex.open(inputFile))
        val lookup = services.classIndex.lookupMethod(method)
        val view = requireNotNull((lookup as? MethodLookup.Runnable)?.view) {
            "No concrete method: $methodSignature"
        }
        require(view.instructionCount > 0) { "Method has no executable instructions: $methodSignature" }
        debugSession = DebugSession(services, view)
        return 0
    }
}
