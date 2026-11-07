package org.cf.smalivm.dex

import java.nio.file.Files
import java.nio.file.Path
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class SmaliParserTest {
    @TempDir
    lateinit var root: Path

    @Test
    fun requestedApiReachesTheTreeWalker() {
        val source = root.resolve("polymorphic.smali")
        Files.writeString(source, POLYMORPHIC_SOURCE.trimIndent())

        val definition = SmaliParser.parse(
            source.toString(),
            Files.newInputStream(source),
            DexBuilder(Opcodes.forApi(30)),
            30,
        )

        Assertions.assertEquals(
            Opcode.INVOKE_POLYMORPHIC,
            definition.methods.single().implementation!!.instructions.first().opcode,
        )
    }

    private companion object {
        const val POLYMORPHIC_SOURCE = """
            .class public Lparser/Polymorphic;
            .super Ljava/lang/Object;

            .method public static call(Ljava/lang/invoke/MethodHandle;)V
                .registers 1
                invoke-polymorphic {p0}, Ljava/lang/invoke/MethodHandle;->invokeExact()V, ()V
                return-void
            .end method
        """
    }
}
