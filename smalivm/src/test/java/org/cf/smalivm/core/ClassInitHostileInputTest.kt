package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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
    fun aDeepSuperclassChainOfInitializersRunsTopDownAndPlansInLinearTime(@TempDir temp: Path) {
        // Each class's initializer stores one more than its superclass's final static, so the read is the chain depth
        // only when every initializer ran exactly once, superclass first. Planning re-asks once per initializer,
        // and each plan walks the chain once: eight times the chain costs a plan about eight times as much. A
        // walk that rescans the chain for every class it adds costs about sixty-four times as much.
        writeChain(temp, "shallow", SHALLOW_CHAIN)
        writeChain(temp, "deep", DEEP_CHAIN)
        val index = ClassIndex.open(temp.toFile())
        val view = index.requireMethodView("Ldeep/Reader;", "read()I")
        // The trigger re-executes once per initializer, so the address limit must admit the whole chain.
        val graph = VmServices(index).newSession(view, Limits(maxAddressVisits = 2 * DEEP_CHAIN)).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Known(DEEP_CHAIN, VirtualType.of("I")))), graph.outcomes)

        val planner = ClassInitPlanner(index)
        val uninitialized = Frame.entryFrame(view)
        val deepPlan = planner.plan(uninitialized, VirtualType.of("Ldeep/C0;"))
        assertEquals(VirtualType.of("Ldeep/C${DEEP_CHAIN - 1};"), deepPlan.context!!.activeInitializer.type)
        // A plan prepares only the classes it settles or starts, not every class waiting below them.
        assertEquals(setOf("Ldeep/C${DEEP_CHAIN - 1};->depth:I"), deepPlan.staticSeeds.keys)
        assertCostRatio(24.0, { VirtualType.of("Lshallow/C0;") }, { VirtualType.of("Ldeep/C0;") }) { type ->
            repeat(PLANS) { planner.plan(uninitialized, type) }
        }
    }

    /**
     * A chain of [depth] classes in [pkg], each extending the next, whose initializers each store one more than
     * the superclass's count, and a reader returning the most derived class's count.
     */
    private fun writeChain(directory: Path, pkg: String, depth: Int) {
        for (index in 0 until depth) {
            val top = index == depth - 1
            val superclass = if (top) "Ljava/lang/Object;" else "L$pkg/C${index + 1};"
            val inherited = if (top) "const/4 v0, 0x0" else "sget v0, L$pkg/C${index + 1};->depth:I"
            Files.writeString(
                directory.resolve("${pkg}_C$index.smali"),
                """
                .class public L$pkg/C$index;
                .super $superclass

                .field public static final depth:I

                .method static constructor <clinit>()V
                    .registers 1
                    $inherited
                    add-int/lit8 v0, v0, 0x1
                    sput v0, L$pkg/C$index;->depth:I
                    return-void
                .end method
                """.trimIndent(),
            )
        }
        Files.writeString(
            directory.resolve("${pkg}_Reader.smali"),
            """
            .class public L$pkg/Reader;
            .super Ljava/lang/Object;

            .method public static read()I
                .registers 1
                sget v0, L$pkg/C0;->depth:I
                return v0
            .end method
            """.trimIndent(),
        )
    }

    private companion object {
        const val DEEP_CHAIN = 2000
        const val SHALLOW_CHAIN = DEEP_CHAIN / 8
        const val PLANS = 50
    }
}
