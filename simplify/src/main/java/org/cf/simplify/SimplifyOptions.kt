package org.cf.simplify

import org.cf.smalivm.dex.SmaliParser
import java.io.File
import java.io.Serializable
import java.util.regex.Pattern

class SimplifyOptions : Serializable {
    var excludeFilter: Pattern? = null
    var includeFilter: Pattern? = null
    var help = false
    var ignoreErrors = false
    var includeSupportLibrary = false
    lateinit var inFile: File
    lateinit var outFile: File
    var maxAddressVisits = 10_000
    var maxCallDepth = 50
    var maxExecutionTime = 5 * 60
    var maxMethodVisits = 1_000_000
    var maxOptimizationPasses = 100
    var outputAPILevel = SmaliParser.DEX_API_LEVEL
    var quiet = false
    var verbosity = 0
    private var inputType: InputType? = null
    val isDex get() = inputType == InputType.DEX
    val isDirectory get() = inputType == InputType.DIRECTORY
    val isZip get() = inputType == InputType.ZIP

    internal fun readInput(file: File) {
        inFile = file
        inputType = if (file.isDirectory) InputType.DIRECTORY else {
            val magic = file.inputStream().use { it.readNBytes(3) }
            when {
                magic.contentEquals(byteArrayOf(0x64, 0x65, 0x78)) -> InputType.DEX
                magic.contentEquals(byteArrayOf(0x50, 0x4b, 0x03)) -> InputType.ZIP
                else -> throw IllegalArgumentException("Unknown input file type; magic: ${magic.contentToString()}")
            }
        }
    }

    override fun toString() = """
        Input file: $inFile
        Output file: $outFile
        Include filter: $includeFilter
        Exclude filter: $excludeFilter
        Max execution time: $maxExecutionTime
        Max address visits: $maxAddressVisits
        Max call depth: $maxCallDepth
        Max method visits: $maxMethodVisits
        Max optimization passes: $maxOptimizationPasses
        Output API level: $outputAPILevel
        Include support library: $includeSupportLibrary
        Ignore method errors: $ignoreErrors
    """.trimIndent()

    private enum class InputType { DEX, DIRECTORY, ZIP }
    companion object { private const val serialVersionUID = -8592147369856820020L }
}
