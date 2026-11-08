package org.cf.smalivm.core

import java.lang.reflect.Modifier
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.cf.smalivm.dex.SmaliParser
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.iface.value.IntEncodedValue
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef
import com.android.tools.smali.dexlib2.immutable.ImmutableExceptionHandler
import com.android.tools.smali.dexlib2.immutable.ImmutableDexFile
import com.android.tools.smali.dexlib2.immutable.ImmutableField
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter
import com.android.tools.smali.dexlib2.immutable.ImmutableTryBlock
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction35c
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir

class ProgramInputTest {
    @TempDir
    lateinit var root: Path

    @Test
    fun applicationClassNamedLikeAFrameworkClassIsNotDefined() {
        // The application loader delegates to the boot class path first, so the framework's Integer is the one defined.
        Files.writeString(
            root.resolve("Integer.smali"),
            """
                .class public Ljava/lang/Integer;
                .super Ljava/lang/Object;
                .method public static appOnly()V
                    .registers 0
                    return-void
                .end method
            """.trimIndent(),
        )
        Files.writeString(root.resolve("Kept.smali"), ".class public Lcollide/Kept;\n.super Ljava/lang/Integer;")

        val index = ClassIndex.open(root.toFile())

        assertNull(index.currentSnapshot.applicationClass("Ljava/lang/Integer;"))
        assertTrue(index.currentSnapshot.hierarchy.isFrameworkClass(VirtualType.of("Ljava/lang/Integer;")))
        assertEquals(VirtualType.of("Ljava/lang/Number;"), index.superclassOf(VirtualType.of("Ljava/lang/Integer;")))
        assertNull(index.methodView("Ljava/lang/Integer;->appOnly()V"))
        assertEquals(setOf("Lcollide/Kept;"), index.currentSnapshot.applicationClasses.keys)
    }

    @Test
    fun firstApplicationDefinitionInPathOrderIsDefined() {
        for ((file, marker) in listOf("b.smali" to 2, "a.smali" to 1, "c/z.smali" to 3)) {
            val path = root.resolve(file)
            Files.createDirectories(path.parent)
            Files.writeString(
                path,
                ".class public Lcollide/Dup;\n.super Ljava/lang/Object;\n.field public static final marker:I = $marker",
            )
        }

        val definition = requireNotNull(ClassIndex.open(root.toFile()).currentSnapshot.applicationClass("Lcollide/Dup;"))

        assertEquals(1, (definition.staticFields.single().declaration.initialValue as IntEncodedValue).value)
    }

    @Test
    fun singleSmaliFileIsAProgram() {
        val source = root.resolve("Single.smali")
        Files.writeString(source, ".class public Linput/Single;\n.super Ljava/lang/Object;")

        assertEquals(setOf("Linput/Single;"), ClassIndex.open(source.toFile()).currentSnapshot.applicationClasses.keys)
    }

    @Test
    fun archiveWithSecondaryClassPathDexIsRejectedNamingTheEntriesTheRuntimeWouldLoad() {
        // A runtime loads classes.dex, classes2.dex, ... up to the first missing number, so classes5.dex is not loaded.
        val apk = archive(
            "multidex.apk",
            "classes.dex" to "Lmultidex/Primary;",
            "classes2.dex" to "Lmultidex/Second;",
            "classes3.dex" to "Lmultidex/Third;",
            "classes5.dex" to "Lmultidex/Fifth;",
        )

        val failure = assertThrows<ProgramInputException> { ClassIndex.open(apk.toFile()) }

        assertTrue(failure.message!!.endsWith("also has classes2.dex, classes3.dex"), failure.message)
    }

    @Test
    fun archiveDexOutsideTheClassPathDoesNotMakeItMultiDex() {
        val apk = archive(
            "single.apk",
            "classes.dex" to "Lsingle/Primary;",
            "assets/payload.dex" to "Lsingle/Payload;",
            "classes3.dex" to "Lsingle/Unreached;",
        )

        val index = ClassIndex.open(apk.toFile())

        assertEquals(setOf("Lsingle/Primary;"), index.currentSnapshot.applicationClasses.keys)
    }

    @Test
    fun malformedSmaliIsAnInputErrorNamingTheFile() {
        Files.writeString(root.resolve("Broken.smali"), ".class public Linput/Broken;\n.super")

        val failure = assertThrows<ProgramInputException> { ClassIndex.open(root.toFile()) }

        assertTrue(failure.message!!.contains("Broken.smali"), failure.message)
    }

    @Test
    fun aDexMemberWithAMalformedSignatureIsAnInputError() {
        // ART's DEX file verifier refuses a method or field id whose name is not a MemberName or whose type is
        // not a type descriptor (`dex_file_verifier.cc`, CheckInterMethodIdItem and CheckInterFieldIdItem).
        val method = ImmutableMethod("Linput/Bad;", "a.b", emptyList(), "V", Modifier.PUBLIC or Modifier.ABSTRACT, null, null, null)
        val field = ImmutableField("Linput/Bad;", "a.b", "I", Modifier.PUBLIC, null, null, null)
        for ((methods, fields) in listOf(listOf(method) to emptyList(), emptyList<ImmutableMethod>() to listOf(field))) {
            val dex = root.resolve("bad-${methods.size}.dex")
            val definition = ImmutableClassDef(
                "Linput/Bad;", Modifier.PUBLIC or Modifier.ABSTRACT, "Ljava/lang/Object;", emptyList(), null, emptySet(),
                fields, methods,
            )
            DexFileFactory.writeDexFile(dex.toString(), ImmutableDexFile(Opcodes.forApi(SmaliParser.DEX_API_LEVEL), listOf(definition)))

            val failure = assertThrows<ProgramInputException> { ClassIndex.open(dex.toFile()) }

            assertTrue(failure.message!!.contains("malformed signature"), failure.message)
        }
    }

    /**
     * ART's DEX file verifier refuses a file any of whose type ids is not a TypeDescriptor (`dex_file_verifier.cc`,
     * CheckInterTypeIdItem via `IsValidDescriptor`), whichever item uses it: a class, its superclass, a field or
     * method id, or an instruction operand.
     */
    @TestFactory
    fun aDexUsingAMalformedTypeDescriptorIsAnInputError(): List<DynamicTest> {
        val plain = "Linput/Plain;"
        fun definition(
            type: String = plain,
            superclass: String = "Ljava/lang/Object;",
            interfaces: List<String> = emptyList(),
            fields: List<ImmutableField> = emptyList(),
            methods: List<ImmutableMethod> = emptyList(),
        ) = ImmutableClassDef(type, Modifier.PUBLIC, superclass, interfaces, null, emptySet(), fields, methods)
        fun staticMethod(parameters: List<String>, vararg instructions: ImmutableInstruction) = ImmutableMethod(
            plain, "run", parameters.map { ImmutableMethodParameter(it, null, null) }, "V",
            Modifier.PUBLIC or Modifier.STATIC, null, null,
            ImmutableMethodImplementation(1 + parameters.size, instructions.toList() + ImmutableInstruction10x(Opcode.RETURN_VOID), null, null),
        )
        return listOf(
            "class descriptor" to definition(type = "Linput//Bad;"),
            "superclass descriptor" to definition(superclass = "Linput/Bad<1>;"),
            "field type" to definition(fields = listOf(ImmutableField(plain, "f", "Linput/Bad<1>;", Modifier.PUBLIC, null, null, null))),
            "method parameter type" to definition(methods = listOf(staticMethod(listOf("Linput/Bad<1>;")))),
            "interface descriptor" to definition(interfaces = listOf("Linput/Bad<1>;")),
            "catch type" to definition(
                methods = listOf(
                    ImmutableMethod(
                        plain, "run", emptyList(), "V", Modifier.PUBLIC or Modifier.STATIC, null, null,
                        ImmutableMethodImplementation(
                            1,
                            listOf(ImmutableInstruction10x(Opcode.RETURN_VOID)),
                            listOf(ImmutableTryBlock(0, 1, listOf(ImmutableExceptionHandler("Linput/Bad<1>;", 0)))),
                            null,
                        ),
                    ),
                ),
            ),
            "const-class operand" to definition(
                methods = listOf(staticMethod(emptyList(), ImmutableInstruction21c(Opcode.CONST_CLASS, 0, ImmutableTypeReference("Linput/Bad<1>;")))),
            ),
            "invoke-static owner" to definition(
                methods = listOf(
                    staticMethod(
                        emptyList(),
                        ImmutableInstruction35c(Opcode.INVOKE_STATIC, 0, 0, 0, 0, 0, 0, ImmutableMethodReference("Linput/Bad<1>;", "m", emptyList(), "V")),
                    ),
                ),
            ),
        ).map { (where, definition) ->
            DynamicTest.dynamicTest(where) {
                val dex = Files.createTempFile(root, "malformed-type", ".dex")
                DexFileFactory.writeDexFile(dex.toString(), ImmutableDexFile(Opcodes.forApi(SmaliParser.DEX_API_LEVEL), listOf(definition)))

                val failure = assertThrows<ProgramInputException> { ClassIndex.open(dex.toFile()) }

                assertTrue(failure.message!!.contains("Bad"), failure.message)
            }
        }
    }

    private fun archive(name: String, vararg entries: Pair<String, String>): Path {
        val archive = root.resolve(name)
        ZipOutputStream(Files.newOutputStream(archive)).use { zip ->
            for ((entry, type) in entries) {
                zip.putNextEntry(ZipEntry(entry))
                zip.write(dexDefining(type))
                zip.closeEntry()
            }
        }
        return archive
    }

    private fun dexDefining(type: String): ByteArray {
        val dex = Files.createTempFile(root, "class", ".dex")
        val definition = ImmutableClassDef(
            type, Modifier.PUBLIC, "Ljava/lang/Object;", emptyList(), null, emptySet(), emptyList(), emptyList(),
        )
        DexFileFactory.writeDexFile(
            dex.toString(),
            ImmutableDexFile(Opcodes.forApi(SmaliParser.DEX_API_LEVEL), listOf(definition)),
        )
        return Files.readAllBytes(dex)
    }
}
