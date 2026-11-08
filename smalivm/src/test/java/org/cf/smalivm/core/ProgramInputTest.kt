package org.cf.smalivm.core

import java.lang.reflect.Modifier
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.cf.smalivm.dex.SmaliParser
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.iface.value.IntEncodedValue
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef
import com.android.tools.smali.dexlib2.immutable.ImmutableDexFile
import com.android.tools.smali.dexlib2.immutable.ImmutableField
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
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
