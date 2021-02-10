package org.cf.smalivm.type

import org.cf.smalivm.dex.SmaliFile
import org.cf.smalivm.dex.SmaliFileFactory
import org.cf.smalivm.dex.SmaliParser
import org.cf.util.ClassNameUtils
import org.jf.baksmali.Main
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.iface.reference.TypeReference
import org.jf.dexlib2.writer.builder.BuilderClassDef
import org.jf.dexlib2.writer.builder.DexBuilder
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.util.stream.Collectors

// TODO: EASY - once the interface is removed, can make these vals again and use implicit getters
class ClassManager2(
    val inFile: File?,
    val outputAPILevel: Int = SmaliParser.DEX_API_LEVEL,
    private val dexBuilder: DexBuilder = DexBuilder(Opcodes.forApi(outputAPILevel))
) : ClassManager {
    private val log = LoggerFactory.getLogger(ClassManager2::class.java.simpleName)

    private val classNameToClass: HashMap<String, VirtualClass> = HashMap()
    private val classNameToSmaliFile: HashMap<String, SmaliFile> = HashMap()
    private val smaliFileFactory: SmaliFileFactory = SmaliFileFactory()
    private var frameworkClassNames: Set<String>? = null
    private var nonFrameworkClassNames: Set<String>? = null

    // Need a separate DexBuilder for interning framework classes to avoid including in output dex
    private val frameworkDexBuilder = DexBuilder(Opcodes.getDefault())

    init {

        if (inFile != null) {
            val smaliPath: File = if (inFile.isFile) disassemble(inFile) else inFile
            cacheSmaliFiles(smaliFileFactory.getSmaliFiles(smaliPath))
        } else {
            cacheSmaliFiles(smaliFileFactory.smaliFiles)
        }
    }

    constructor(inFile: File, dexBuilder: DexBuilder = DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL))) : this(
        inFile = null,
        dexBuilder = dexBuilder
    ) {
        if (!inFile.exists()) {
            throw RuntimeException("Input Smali path does not exist: $inFile")
        }
        val smaliPath: File = if (inFile.isFile) {
            disassemble(inFile)
        } else {
            inFile
        }
        cacheSmaliFiles(smaliFileFactory.getSmaliFiles(smaliPath))
    }

    private fun cacheSmaliFiles(smaliFiles: Set<SmaliFile>) {
        for (smaliFile in smaliFiles) {
            classNameToSmaliFile[smaliFile.className] = smaliFile
        }
    }

    private companion object {
        private const val TEMP_DIR_NAME = "simplify"

        fun disassemble(file: File): File {
            val tempDir = Files.createTempDirectory(TEMP_DIR_NAME)
            val args = arrayOf(
                "disassemble", "--use-locals", "--sequential-labels", "--code-offsets", file.absolutePath, "--output", tempDir.toString()
            )
            Main.main(args)
            return tempDir.toFile()
        }
    }

    /**
     * Does not load any Smali files.
     *
     * @return all local class names, including framework
     */
    override fun getClassNames(): Set<String?> {
        return classNameToSmaliFile.keys
    }

    /**
     * @return DexBuilder
     */
    override fun getDexBuilder(): DexBuilder {
        return dexBuilder
    }

    /**
     * Does not load any Smali files.
     *
     * @return all framework class names
     */
    override fun getFrameworkClassNames(): Set<String?>? {
        if (frameworkClassNames == null) {
            frameworkClassNames = classNameToSmaliFile.keys
                .parallelStream()
                .filter { className: String? -> smaliFileFactory.isFrameworkClass(className) }
                .collect(Collectors.toSet())
        }
        return frameworkClassNames
    }

    override fun getLoadedClasses(): Collection<VirtualClass?> {
        return classNameToClass.values
    }

    override fun getMethod(methodSignature: String): VirtualMethod? {
        val parts = methodSignature.split("->".toRegex()).toTypedArray()
        val className = parts[0]
        val methodDescriptor = parts[1]
        return getMethod(className, methodDescriptor)
    }

    override fun getMethod(className: String?, methodDescriptor: String?): VirtualMethod? {
        val virtualClass: VirtualType? = getVirtualClass(className)
        return virtualClass!!.getMethod(methodDescriptor)
    }

    /**
     * Does not load any Smali files.
     *
     * @return all local class names which are not part of the framework
     */
    override fun getNonFrameworkClassNames(): Set<String?>? {
        if (nonFrameworkClassNames == null) {
            nonFrameworkClassNames = classNameToSmaliFile.keys
                .parallelStream()
                .filter { className: String? -> !smaliFileFactory.isFrameworkClass(className) }
                .collect(Collectors.toSet())
        }
        return nonFrameworkClassNames
    }

    override fun getVirtualClass(klazz: Class<*>?): VirtualClass? {
        return getVirtualClass(ClassNameUtils.toInternal(klazz))
    }

    override fun getVirtualClass(className: String?): VirtualClass? {
        return getVirtualType(className) as VirtualClass?
    }

    override fun getVirtualType(typeReference: TypeReference): VirtualType? {
        val first = typeReference[0]
        return when {
            first == 'L' -> {
                val className = typeReference.type
                parseClassIfNecessary(className)
                classNameToClass[className]
            }
            first == '[' -> {
                VirtualArray(typeReference, this)
            }
            ClassNameUtils.isPrimitive(typeReference.type) -> {
                VirtualPrimitive(typeReference)
            }
            else -> {
                // TODO: throw IllegalArgumentException? Or Maybe before it's interned?
                throw RuntimeException("Unrecognized type: $typeReference")
            }
        }
    }

    override fun getVirtualType(typeClass: Class<*>?): VirtualType? {
        return getVirtualType(ClassNameUtils.toInternal(typeClass))
    }

    override fun getVirtualType(typeSignature: String?): VirtualType? {
        // TODO: consider validating the type Signature here rather than in getVirtualType
        val typeReference: TypeReference = getFrameworkDexBuilder().internTypeReference(typeSignature!!)
        return getVirtualType(typeReference)
    }

    override fun isFrameworkClass(virtualClass: VirtualType): Boolean {
        return smaliFileFactory.isFrameworkClass(virtualClass.name)
    }

    override fun isSafeFrameworkClass(virtualClass: VirtualType): Boolean {
        return smaliFileFactory.isSafeFrameworkClass(virtualClass.name)
    }

    override fun getFrameworkDexBuilder(): DexBuilder {
        return frameworkDexBuilder
    }

    private fun parseClass(className: String, builder: DexBuilder): BuilderClassDef {
        val classDef: BuilderClassDef
        try {
            val smaliFile = classNameToSmaliFile[className]!!
            smaliFile.open().use { classDef = SmaliParser.parse(smaliFile.path, it, builder) }
        } catch (e: NullPointerException) {
            throw RuntimeException("Can't find Smali file for $className")
        } catch (e: Exception) {
            throw RuntimeException("Error loading class definition: $className", e)
        }
        return classDef
    }

    private fun parseClassIfNecessary(className: String) {
        if (classNameToClass.containsKey(className)) {
            return
        }
        val isFramework = smaliFileFactory.isFrameworkClass(className)
        val builder = if (isFramework) frameworkDexBuilder else dexBuilder
        val classDef = parseClass(className, builder)
        val virtualClass = VirtualClass(classDef, this)
        classNameToClass[className] = virtualClass
    }
}
