package org.cf.smalivm.core

import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class FrameworkCatalogTest {
    @TempDir
    lateinit var root: Path

    @Test
    fun indexedCatalogKeepsStableRunnableFrameworkMethodIdentityAcrossPublication() {
        val index = task53ClassIndex(root, SOURCE)
        val workspace = index.programWorkspace
        val first = workspace.currentSnapshot
        val method = first.frameworkCatalog.method("Ljava/lang/Object;->clone()Ljava/lang/Object;")

        assertNotNull(method)
        assertNotNull(method!!.body)
        assertTrue(MethodView.of(method, first.programRevision).frameworkMethodIdentity === method.identity)

        val second = ProgramSnapshot.candidate(
            first,
            workspace.newProgramRevision(),
            first.applicationClasses,
        )
        workspace.publish(second)

        assertSame(first.frameworkCatalog, second.frameworkCatalog)
        assertSame(method, second.frameworkCatalog.method(method.methodSignature))
        assertSame(method.identity, second.frameworkCatalog.method(method.methodSignature)!!.identity)
        assertTrue(first.frameworkCatalog.classCount > 20_000)
        assertTrue(first.hierarchy.contains("Landroid/app/Activity;"))
        val lazyMethod = requireNotNull(first.frameworkCatalog.method("Landroid/app/Activity;->isFinishing()Z"))
        val secondProjection = requireNotNull(first.frameworkCatalog.method(lazyMethod.methodSignature))
        assertEquals(lazyMethod.identity, secondProjection.identity)
        assertNotNull(lazyMethod.body)
    }

    @Test
    fun concreteCapturedFrameworkBodyRemainsExecutableAsGuestBytecode() {
        val index = task53ClassIndex(root, SOURCE)
        val method = requireNotNull(
            index.currentSnapshot.frameworkCatalog.method("Ljava/lang/Integer;->numberOfLeadingZeros(I)I"),
        )
        val view = MethodView.of(method, index.currentSnapshot.programRevision)
        val frame = Frame.entryFrame(view, HostObjectCloner())
        frame.assign(view.firstParameterRegister, Value.Known(1, VirtualType.of("I")))

        val graph = VmServices(index).newSession(view, entryFrame = frame).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(31, VirtualType.of("I")), graph.trusted()!!.terminatingConsensus(0))
    }


    private companion object {
        const val SOURCE = """
            .class public Ltask53/FrameworkCaller;
            .super Ljava/lang/Object;

            .method public static call(Ljava/lang/Object;)Ljava/lang/Object;
                .registers 1
                invoke-virtual {p0}, Ljava/lang/Object;->clone()Ljava/lang/Object;
                move-result-object p0
                return-object p0
            .end method

            .method public static leadingZeros(I)I
                .registers 1
                invoke-static {p0}, Ljava/lang/Integer;->numberOfLeadingZeros(I)I
                move-result p0
                return p0
            .end method

        """
    }
}
