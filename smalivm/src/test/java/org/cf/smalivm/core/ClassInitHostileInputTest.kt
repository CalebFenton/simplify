package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import org.junit.jupiter.api.io.TempDir

/** Class-init planning against class shapes only hostile or malformed input produces. */
class ClassInitHostileInputTest {
    @Test
    fun aStaticInitializerWithNoCodeAbortsThePathAsMalformedInput() {
        // ART's dex verifier rejects a code-less constructor or static initializer: DexFileVerifier::
        // CheckMethodAccessFlags fails with "Constructor ... must not be abstract or native" for every dex version
        // that supports default methods (art/libdexfile/dex/dex_file_verifier.cc, AOSP main at
        // 6484611fd45e69db9f33f98bfd6864014b030ecf). No device runs this class, so no value the read could return
        // is an answer.
        val graph = CoreTester.execute("Lclass_init/bodyless_initializer;", "read()I")

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        assertEquals(listOf(Outcome.Aborted(AbortReason.MALFORMED_INPUT)), graph.outcomes)
    }

    @Test
    fun aDeepSuperclassChainOfInitializersRunsTopDownInBoundedTime(@TempDir temp: Path) {
        // Each class's initializer stores one more than its superclass's final static, so the read is the chain depth
        // only when every initializer ran exactly once, superclass first. Planning re-asks once per initializer,
        // so per-plan work linear in the chain keeps the whole run quadratic in small constants; per-plan work that
        // re-resolves every waiting class's initializer does not finish inside the bound.
        for (index in 0 until CHAIN_DEPTH) {
            val superclass = if (index == CHAIN_DEPTH - 1) "Ljava/lang/Object;" else "Ldeep/C${index + 1};"
            val inherited = if (index == CHAIN_DEPTH - 1) {
                "const/4 v0, 0x0"
            } else {
                "sget v0, Ldeep/C${index + 1};->depth:I"
            }
            Files.writeString(
                temp.resolve("C$index.smali"),
                """
                .class public Ldeep/C$index;
                .super $superclass

                .field public static final depth:I

                .method static constructor <clinit>()V
                    .registers 1
                    $inherited
                    add-int/lit8 v0, v0, 0x1
                    sput v0, Ldeep/C$index;->depth:I
                    return-void
                .end method
                """.trimIndent(),
            )
        }
        Files.writeString(
            temp.resolve("Reader.smali"),
            """
            .class public Ldeep/Reader;
            .super Ljava/lang/Object;

            .method public static read()I
                .registers 1
                sget v0, Ldeep/C0;->depth:I
                return v0
            .end method
            """.trimIndent(),
        )
        val index = ClassIndex.open(temp.toFile())
        val view = index.requireMethodView("Ldeep/Reader;", "read()I")

        // The trigger re-executes once per initializer, so the address limit must admit the whole chain.
        val limits = Limits(maxAddressVisits = 2 * CHAIN_DEPTH)
        val graph = assertTimeoutPreemptively(Duration.ofSeconds(10)) { VmServices(index).newSession(view, limits).run() }

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Known(CHAIN_DEPTH, VirtualType.of("I")))), graph.outcomes)
    }

    private companion object {
        const val CHAIN_DEPTH = 2000
    }
}
