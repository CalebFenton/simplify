package org.cf.simplify

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import java.io.File
import java.util.regex.Pattern

object SimplifyOptionsParser {
    fun parse(args: Array<String>): SimplifyOptions {
        val command = DefaultParser().parse(options, args)
        val result = SimplifyOptions()
        var output: File? = null
        for (option in command.options) {
            val value = option.value
            when (option.longOpt) {
                "output" -> output = File(value)
                "include-types" -> result.includeFilter = Pattern.compile(value)
                "exclude-types" -> result.excludeFilter = Pattern.compile(value)
                "max-execution-time" -> result.maxExecutionTime = nonNegativeInt(option.longOpt, value)
                "max-address-visits" -> result.maxAddressVisits = nonNegativeInt(option.longOpt, value)
                "max-call-depth" -> result.maxCallDepth = nonNegativeInt(option.longOpt, value)
                "max-method-visits" -> result.maxMethodVisits = nonNegativeInt(option.longOpt, value)
                "max-passes" -> result.maxOptimizationPasses = nonNegativeInt(option.longOpt, value)
                "output-api-level" -> result.outputAPILevel = nonNegativeInt(option.longOpt, value)
                "include-support" -> result.includeSupportLibrary = true
                "help" -> result.help = true
                "ignore-errors" -> result.ignoreErrors = true
                "quiet" -> result.quiet = true
                "verbose" -> result.verbosity = value?.let { nonNegativeInt(option.longOpt, it) } ?: 1
            }
        }
        if (result.help) return result
        if (command.args.isEmpty()) throw ParseException("Missing input file.")
        result.readInput(File(command.args[0]))
        result.outFile = output ?: run {
            val file = result.inFile
            val extension = if (result.isDirectory) "dex" else file.extension
            File(file.nameWithoutExtension + "_simple" + if (extension.isEmpty()) "" else ".$extension")
        }
        return result
    }

    private fun nonNegativeInt(option: String, value: String): Int {
        val parsed = value.toIntOrNull() ?: throw ParseException("--$option requires an integer: $value")
        if (parsed < 0) throw ParseException("--$option must be non-negative: $value")
        return parsed
    }

    val options: Options get() {
        val defaults = SimplifyOptions()
        return Options().apply {
            addOption(Option.builder("o").longOpt("output").hasArg().argName("file").desc("Output simplified input to FILE").get())
            addOption(Option.builder("it").longOpt("include-types").hasArg().argName("pattern").desc("Limit execution to classes and methods matching REGEX").get())
            addOption(Option.builder("et").longOpt("exclude-types").hasArg().argName("pattern").desc("Exclude classes and methods matching REGEX, applied after include-types").get())
            fun limit(name: String, description: String, default: Int) {
                addOption(Option.builder().longOpt(name).hasArg().argName("N").desc("$description, default: $default").get())
            }
            limit("max-execution-time", "Give up executing a method after N seconds", defaults.maxExecutionTime)
            limit("max-address-visits", "Give up after visiting the same address N times", defaults.maxAddressVisits)
            limit("max-call-depth", "Do not call methods after reaching call depth N", defaults.maxCallDepth)
            limit("max-method-visits", "Give up after executing N instructions in a method", defaults.maxMethodVisits)
            limit("max-passes", "Do not commit optimizations on a method more than N times", defaults.maxOptimizationPasses)
            limit("output-api-level", "Set output DEX API compatibility", defaults.outputAPILevel)
            addOption(Option.builder().longOpt("include-support").desc("Optimize Android support library packages").get())
            addOption(Option.builder("h").longOpt("help").desc("Display this message").get())
            addOption(Option.builder("ie").longOpt("ignore-errors").desc("Ignore errors while executing and optimizing methods. This may lead to unexpected behavior.").get())
            addOption(Option.builder("q").longOpt("quiet").desc("Be quiet").get())
            addOption(Option.builder("v").longOpt("verbose").hasArg().optionalArg(true).argName("LEVEL").desc("Set verbosity to LEVEL, default: ${defaults.verbosity}").get())
        }
    }
}
