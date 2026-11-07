package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir

class ProgramSnapshotExecutionTest {
    @TempDir
    lateinit var root: Path

    @Test
    fun applicationViewAndGraphExposeTheCapturedRevisionObjects() {
        val index = index("revisions")
        val snapshot = index.currentSnapshot
        val definition = requireNotNull(snapshot.applicationMethod(CALLER))
        val view = requireNotNull(index.methodView(CALLER))

        val graph = VmServices(index).newSession(view).run()

        assertSame(snapshot.programRevision, view.programRevision)
        assertSame(definition.methodRevision, view.methodRevision)
        assertSame(view.programRevision, graph.programRevision)
        assertSame(view.methodRevision, graph.methodRevision)
    }

    @Test
    fun newSessionsRejectStaleViewsAndForeignOrRevisionMismatchedEntryFrames() {
        val index = index("stale")
        val services = VmServices(index)
        val oldView = requireNotNull(index.methodView(CALLER))
        val oldFrame = Frame.entryFrame(oldView)
        val current = index.currentSnapshot
        val replacement = ProgramSnapshot.candidate(
            current,
            index.programWorkspace.newProgramRevision(),
            current.applicationClasses,
        )
        index.programWorkspace.replaceCurrentSnapshotForTest(replacement)
        val currentView = requireNotNull(index.methodView(CALLER))

        assertThrows<IllegalArgumentException> { services.newSession(oldView) }
        assertThrows<IllegalArgumentException> { services.newSession(currentView, entryFrame = oldFrame) }

        val foreignIndex = index("foreign")
        val foreignView = requireNotNull(foreignIndex.methodView(CALLER))
        assertThrows<IllegalArgumentException> {
            services.newSession(currentView, entryFrame = Frame.entryFrame(foreignView))
        }
    }

    @Test
    fun pausedSessionResolvesNestedApplicationCallsFromCapturedSnapshot() {
        val index = index("nested")
        val snapshotA = index.currentSnapshot
        val callerA = requireNotNull(index.methodView(CALLER))
        val sessionA = VmServices(index).newSession(callerA)
        sessionA.step()

        val snapshotB = replaceMethod(index, CALLEE, returnsInt(2))
        index.programWorkspace.replaceCurrentSnapshotForTest(snapshotB)

        val graphA = sessionA.run()
        val callerB = requireNotNull(index.methodView(CALLER))
        val graphB = VmServices(index).newSession(callerB).run()

        assertSame(snapshotA.programRevision, graphA.programRevision)
        assertSame(snapshotB.programRevision, graphB.programRevision)
        assertEquals(Value.Known(1, INT), graphA.trusted()!!.terminatingConsensus(0))
        assertEquals(Value.Known(2, INT), graphB.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun recursiveApplicationActivationMatchingIncludesProgramAndMethodRevision() {
        val index = index("activation-revision")
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(RECURSIVE))
        val resolverA = index.resolver(snapshotA)
        lateinit var viewB: MethodView
        val scheduleCurrentView = object : Backend {
            override val name: String = "schedule-current-test-view"
            override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe =
                BackendProbe.Schedule(viewB, 0)
        }
        val invoker = Invoker(resolverA, listOf(scheduleCurrentView))
        val frame = Frame.entryFrame(viewA).also {
            it.assign(viewA.parameters.single().register, Value.Known(1, INT))
        }
        val sessionA = ExecutionSession.of(
            viewA,
            index,
            Limits(),
            frame,
            OpBuilder::build,
            invoker,
        )
        val snapshotB = replaceMethod(index, RECURSIVE, returnsInt(7))
        index.programWorkspace.replaceCurrentSnapshotForTest(snapshotB)
        viewB = requireNotNull(index.methodView(RECURSIVE))

        val graphA = sessionA.run()

        assertEquals(Value.Known(7, INT), graphA.trusted()!!.terminatingConsensus(0))
        assertTrue(
            graphA.addresses().flatMap(graphA::nodePile).all { it.view.hasSameExecutionIdentity(viewA) },
            "a same-signature activation from snapshot B must not enter graph A's piles",
        )
    }

    @Test
    fun pausedSessionPlansClassInitializationFromCapturedSnapshot() {
        val index = index("class-init")
        val triggerA = requireNotNull(index.methodView(INIT_TRIGGER))
        val sessionA = VmServices(index).newSession(triggerA)
        sessionA.step()

        val snapshotB = replaceMethod(index, CLASS_INITIALIZER, initializesStatic(2))
        index.programWorkspace.replaceCurrentSnapshotForTest(snapshotB)

        val graphA = sessionA.run()
        val triggerB = requireNotNull(index.methodView(INIT_TRIGGER))
        val graphB = VmServices(index).newSession(triggerB).run()

        assertEquals(Value.Known(1, INT), graphA.trusted()!!.terminatingConsensus(0))
        assertEquals(Value.Known(2, INT), graphB.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun handlerChecksAndGraphTypeQueriesUseCapturedHierarchy() {
        val index = index("hierarchy")
        val handlerA = requireNotNull(index.methodView(HANDLER))
        val chooseA = requireNotNull(index.methodView(CHOOSE))
        val handlerSessionA = VmServices(index).newSession(handlerA)
        val chooseSessionA = VmServices(index).newSession(chooseA)
        val snapshotA = index.currentSnapshot
        val hierarchyB = changedHierarchy(snapshotA.hierarchy)
        val snapshotB = ProgramSnapshot.replacementForTest(
            snapshotA,
            index.programWorkspace.newProgramRevision(),
            hierarchy = hierarchyB,
        )
        index.programWorkspace.replaceCurrentSnapshotForTest(snapshotB)

        val handlerGraphA = handlerSessionA.run()
        val chooseGraphA = chooseSessionA.run()
        val handlerGraphB = VmServices(index).newSession(requireNotNull(index.methodView(HANDLER))).run()
        val chooseGraphB = VmServices(index).newSession(requireNotNull(index.methodView(CHOOSE))).run()

        assertTrue(handlerGraphA.outcomes.none { it is Outcome.Threw })
        assertTrue(handlerGraphB.outcomes.any { it is Outcome.Threw })
        assertEquals(Value.Unknown(BASE), chooseGraphA.trusted()!!.terminatingConsensus(0))
        assertEquals(Value.Unknown(VirtualType.OBJECT), chooseGraphB.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun capturedFrameworkIdentityAndBodySurviveCurrentCatalogReplacement() {
        val index = index("framework")
        val snapshotA = index.currentSnapshot
        val expectedMethod = requireNotNull(snapshotA.frameworkCatalog.method(FRAMEWORK_METHOD))
        val expectedView = MethodView.of(expectedMethod, snapshotA.programRevision)
        val callerA = requireNotNull(index.methodView(FRAMEWORK_CALLER))
        val resolverA = index.resolver(snapshotA)
        val hostDecline = object : Backend {
            override val name: String = "declining-host"
            override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe =
                BackendProbe.Declined("test host decline")
        }
        val invoker = Invoker(
            resolverA,
            listOf(
                ApplicationBytecodeBackend(resolverA),
                hostDecline,
                FrameworkBytecodeBackend(resolverA),
            ),
        )
        val sessionA = ExecutionSession.of(
            callerA,
            index,
            Limits(),
            Frame.entryFrame(callerA),
            OpBuilder::build,
            invoker,
        )
        sessionA.step()
        val replacementCatalog = FrameworkCatalog.fromArtifact(snapshotA.frameworkCatalog.artifact)
        val snapshotB = ProgramSnapshot.replacementForTest(
            snapshotA,
            index.programWorkspace.newProgramRevision(),
            frameworkCatalog = replacementCatalog,
        )
        index.programWorkspace.replaceCurrentSnapshotForTest(snapshotB)

        sessionA.step()
        sessionA.step()
        val scheduledFrameworkView = sessionA.frontier.last().view

        assertSame(expectedMethod.identity, scheduledFrameworkView.frameworkMethodIdentity)
        assertSame(expectedView.instructionAt(0), scheduledFrameworkView.instructionAt(0))
        val replacementView = MethodView.of(
            requireNotNull(replacementCatalog.method(FRAMEWORK_METHOD)),
            snapshotB.programRevision,
        )
        assertFalse(scheduledFrameworkView.matchesActivationMethod(replacementView))
        assertEquals(Value.Known(31, INT), sessionA.run().trusted()!!.terminatingConsensus(0))
    }

    private fun index(name: String): ClassIndex {
        val directory = Files.createDirectory(root.resolve(name))
        SOURCES.forEachIndexed { index, source ->
            Files.writeString(directory.resolve("fixture-$index.smali"), source.trimIndent())
        }
        return ClassIndex.open(directory.toFile())
    }

    private fun replaceMethod(
        index: ClassIndex,
        methodSignature: String,
        implementation: ImmutableMethodImplementation,
    ): ProgramSnapshot {
        val current = index.currentSnapshot
        val method = requireNotNull(current.applicationMethod(methodSignature))
        val body = MethodBody.import(implementation).let(MethodLayoutSolver::solve).let(MethodBodyAssembler::assemble)
        val changedMethod = ApplicationMethodDefinition.withBody(
            method,
            body,
            index.programWorkspace.newApplicationMethodRevision(methodSignature),
        )
        val classType = methodSignature.substringBefore("->")
        val currentClass = requireNotNull(current.applicationClass(classType))
        val changedClass = ApplicationClassDefinition(
            currentClass.type,
            currentClass.accessFlags,
            currentClass.superclass,
            currentClass.interfaces,
            currentClass.sourceFile,
            currentClass.annotations,
            currentClass.staticFields,
            currentClass.instanceFields,
            currentClass.methods.map { if (it.methodSignature == methodSignature) changedMethod else it },
        )
        return ProgramSnapshot.candidate(
            current,
            index.programWorkspace.newProgramRevision(),
            current.applicationClasses.putting(classType, changedClass),
        )
    }

    private fun returnsInt(value: Int): ImmutableMethodImplementation = ImmutableMethodImplementation(
        1,
        listOf(
            ImmutableInstruction11n(Opcode.CONST_4, 0, value),
            ImmutableInstruction11x(Opcode.RETURN, 0),
        ),
        emptyList(),
        emptyList(),
    )

    private fun initializesStatic(value: Int): ImmutableMethodImplementation = ImmutableMethodImplementation(
        1,
        listOf(
            ImmutableInstruction11n(Opcode.CONST_4, 0, value),
            ImmutableInstruction21c(
                Opcode.SPUT,
                0,
                ImmutableFieldReference("Ltask54/Init;", "value", "I"),
            ),
            ImmutableInstruction10x(Opcode.RETURN_VOID),
        ),
        emptyList(),
        emptyList(),
    )

    private fun changedHierarchy(current: HierarchySnapshot): HierarchySnapshot {
        val declarations = current.declarations.builder()
        for (type in listOf("Ltask54/Left;", "Ltask54/Right;", "Ltask54/ChildException;")) {
            val declaration = requireNotNull(declarations[type])
            declarations[type] = declaration.copy(superclass = "Ljava/lang/Object;")
        }
        return HierarchySnapshot(declarations.build())
    }

    private companion object {
        val INT = VirtualType.of("I")
        val BASE = VirtualType.of("Ltask54/Base;")
        const val CALLER = "Ltask54/Main;->caller()I"
        const val CALLEE = "Ltask54/Main;->callee()I"
        const val HANDLER = "Ltask54/Main;->handler(Ltask54/ChildException;)I"
        const val CHOOSE = "Ltask54/Main;->choose(Z)Ljava/lang/Object;"
        const val INIT_TRIGGER = "Ltask54/Init;->trigger()I"
        const val CLASS_INITIALIZER = "Ltask54/Init;-><clinit>()V"
        const val FRAMEWORK_CALLER = "Ltask54/Main;->frameworkCaller()I"
        const val RECURSIVE = "Ltask54/Main;->recursiveOnce(I)I"
        const val FRAMEWORK_METHOD = "Ljava/lang/Integer;->numberOfLeadingZeros(I)I"

        val SOURCES = listOf(
            """
                .class public Ltask54/Main;
                .super Ljava/lang/Object;

                .method public static caller()I
                    .registers 1
                    nop
                    invoke-static {}, Ltask54/Main;->callee()I
                    move-result v0
                    return v0
                .end method

                .method public static callee()I
                    .registers 1
                    const/4 v0, 0x1
                    return v0
                .end method

                .method public static frameworkCaller()I
                    .registers 1
                    nop
                    const/4 v0, 0x1
                    invoke-static {v0}, Ljava/lang/Integer;->numberOfLeadingZeros(I)I
                    move-result v0
                    return v0
                .end method

                .method public static recursiveOnce(I)I
                    .registers 1
                    if-eqz p0, :done
                    add-int/lit8 p0, p0, -0x1
                    invoke-static {p0}, Ltask54/Main;->recursiveOnce(I)I
                    move-result p0
                    :done
                    return p0
                .end method

                .method public static handler(Ltask54/ChildException;)I
                    .registers 2
                    :try_start
                    throw p0
                    :try_end
                    .catch Ltask54/BaseException; {:try_start .. :try_end} :caught
                    const/4 v0, 0x0
                    return v0
                    :caught
                    move-exception v0
                    const/4 v0, 0x1
                    return v0
                .end method

                .method public static choose(Z)Ljava/lang/Object;
                    .registers 2
                    if-eqz p0, :right
                    new-instance v0, Ltask54/Left;
                    return-object v0
                    :right
                    new-instance v0, Ltask54/Right;
                    return-object v0
                .end method
            """,
            """
                .class public Ltask54/Base;
                .super Ljava/lang/Object;
            """,
            """
                .class public Ltask54/Left;
                .super Ltask54/Base;
            """,
            """
                .class public Ltask54/Right;
                .super Ltask54/Base;
            """,
            """
                .class public Ltask54/BaseException;
                .super Ljava/lang/RuntimeException;
            """,
            """
                .class public Ltask54/ChildException;
                .super Ltask54/BaseException;
            """,
            """
                .class public Ltask54/Init;
                .super Ljava/lang/Object;
                .field public static value:I

                .method static constructor <clinit>()V
                    .registers 1
                    const/4 v0, 0x1
                    sput v0, Ltask54/Init;->value:I
                    return-void
                .end method

                .method public static trigger()I
                    .registers 1
                    nop
                    sget v0, Ltask54/Init;->value:I
                    return v0
                .end method
            """,
        )
    }
}
