package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction35c
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir

class SnapshotReExecutionTest {
    @TempDir
    lateinit var root: Path

    private var fixtureSequence = 0

    @Test
    fun publishedEditLeavesPausedNestedCallOnSnapshotAAndExecutesCommittedViewB() {
        val index = index()
        val services = VmServices(index)
        val snapshotA = index.currentSnapshot
        val callerA = requireNotNull(index.methodView(CALLER))
        val targetA = requireNotNull(index.methodView(TARGET))
        val sessionA = services.newSession(callerA)
        assertEquals(0, sessionA.step()!!.address)

        val edit = services.methodEditor.beginEdit(targetA)
        edit.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 2))))
        val committed = edit.commit() as MethodEditCommitResult.Committed
        val snapshotB = index.currentSnapshot

        val graphA = sessionA.run()
        val graphB = services.newSession(committed.editedMethodView).run()
        val callerB = requireNotNull(index.methodView(CALLER))
        val callerGraphB = services.newSession(callerB).run()

        assertSame(snapshotA.programRevision, graphA.programRevision)
        assertSame(snapshotB.programRevision, graphB.programRevision)
        assertSame(committed.editedMethodView, graphB.methodView)
        assertEquals(Value.Known(1, INT), graphA.trusted()!!.terminatingConsensus(0))
        assertEquals(Value.Known(2, INT), graphB.trusted()!!.terminatingConsensus(0))
        assertEquals(Value.Known(2, INT), callerGraphB.trusted()!!.terminatingConsensus(0))
        assertThrows<IllegalArgumentException> { services.newSession(callerA) }
    }

    @Test
    fun reExecutionBuildsNewNodesFramesOutcomesAndCanonicalHandlers() {
        val index = index()
        val services = VmServices(index)
        val viewA = requireNotNull(index.methodView(TARGET))
        val handlerA = (viewA.handlerTableAt(0) as HandlerTableLookup.Found).table
        val graphA = services.newSession(viewA).run()

        val edit = services.methodEditor.beginEdit(viewA)
        edit.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 2))))
        val committed = edit.commit() as MethodEditCommitResult.Committed
        val viewB = committed.editedMethodView
        val handlerB = (viewB.handlerTableAt(0) as HandlerTableLookup.Found).table
        val graphB = services.newSession(viewB).run()
        val nodesA = graphA.addresses().flatMap(graphA::nodePile)
        val nodesB = graphB.addresses().flatMap(graphB::nodePile)

        assertTrue(nodesA.none { old -> nodesB.any { current -> current === old } })
        assertTrue(nodesA.none { old -> nodesB.any { current -> current.frame === old.frame } })
        assertEquals(graphA.outcomes.size, graphB.outcomes.size)
        graphA.outcomes.zip(graphB.outcomes).forEach { (old, current) -> assertNotSame(old, current) }
        assertNotSame(handlerA, handlerB)
        handlerA.clauses.zip(handlerB.clauses).forEach { (old, current) -> assertNotSame(old, current) }
        assertEquals(Value.Known(1, INT), graphA.trusted()!!.terminatingConsensus(0))
        assertEquals(Value.Known(2, INT), graphB.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun nestedClassInitializerStaysInTheCapturedSnapshot() {
        val index = index()
        val services = VmServices(index)
        val snapshotA = index.currentSnapshot
        val initTriggerA = requireNotNull(index.methodView(INIT_TRIGGER))
        val initSessionA = services.newSession(initTriggerA)
        assertEquals(0, initSessionA.step()!!.address)

        val initEdit = services.methodEditor.beginEdit(requireNotNull(index.methodView(CLASS_INITIALIZER)))
        initEdit.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 2))))
        initEdit.commit() as MethodEditCommitResult.Committed

        val initGraphA = initSessionA.run()
        val initGraphB = services.newSession(requireNotNull(index.methodView(INIT_TRIGGER))).run()
        assertEquals(Value.Known(1, INT), initGraphA.trusted()!!.terminatingConsensus(0))
        assertEquals(Value.Known(2, INT), initGraphB.trusted()!!.terminatingConsensus(0))
        assertNotSame(snapshotA.programRevision, index.currentSnapshot.programRevision)
    }

    @Test
    fun nestedFrameworkCallKeepsCapturedCatalogIdentityAndConcreteBody() {
        val index = index()
        val services = VmServices(index)
        val capturedSnapshot = index.currentSnapshot
        val resolver = index.resolver(capturedSnapshot)
        val frameworkCaller = requireNotNull(index.methodView(FRAMEWORK_CALLER))
        val capturedFrameworkMethod = requireNotNull(capturedSnapshot.frameworkCatalog.method(MethodSignature.parse(FRAMEWORK_METHOD)))
        val hostDecline = object : Backend {
            override val name: String = "declining-host"

            override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe =
                BackendProbe.Declined("test host decline")
        }
        val frameworkSession = services.newSession(
            frameworkCaller,
            Limits(),
            entryFrame = null,
            backends = listOf(
                ApplicationBytecodeBackend(resolver),
                hostDecline,
                FrameworkBytecodeBackend(resolver),
            ),
            decode = OpBuilder::build,
        )
        assertEquals(0, frameworkSession.step()!!.address)

        val unrelatedEdit = services.methodEditor.beginEdit(requireNotNull(index.methodView(OTHER)))
        unrelatedEdit.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 4))))
        unrelatedEdit.commit() as MethodEditCommitResult.Committed

        frameworkSession.step()
        frameworkSession.step()
        val scheduledFrameworkView = frameworkSession.frontier.last().view

        assertSame(capturedFrameworkMethod.identity, scheduledFrameworkView.frameworkMethodIdentity)
        assertSame(capturedSnapshot.frameworkCatalog, index.currentSnapshot.frameworkCatalog)
        assertEquals(Value.Known(31, INT), frameworkSession.run().trusted()!!.terminatingConsensus(0))
        assertNotSame(capturedSnapshot.programRevision, index.currentSnapshot.programRevision)
    }

    @Test
    fun finalDynamicLoadingSiteRemovalAndInsertionChangeUnknownReceiverDispatchPrecision() {
        val index = index()
        val services = VmServices(index)
        val snapshotA = index.currentSnapshot
        val siteA = requireNotNull(snapshotA.applicationMethod(DYNAMIC_SITE))
        val implementationA = requireNotNull(snapshotA.applicationMethod(DYNAMIC_IMPLEMENTATION))
        assertEquals(setOf(DYNAMIC_SITE), snapshotA.dynamicLoadingEvidence.methodSignatures)
        assertEquals(Value.Unknown(INT), execute(services, DYNAMIC_CALLER).trusted()!!.terminatingConsensus(0))

        val remove = services.methodEditor.beginEdit(requireNotNull(index.methodView(DYNAMIC_SITE)))
        remove.removeInstructions(listOf(2, 5))
        val removed = remove.commit() as MethodEditCommitResult.Committed
        val snapshotB = index.currentSnapshot
        val siteB = requireNotNull(snapshotB.applicationMethod(DYNAMIC_SITE))

        assertFalse(siteB.dynamicLoadingEvidence.isPresent)
        assertFalse(snapshotB.dynamicLoadingEvidence.isPresent)
        assertTrue(index.resolver(snapshotB).dispatchWorldIsComplete(DYNAMIC_INTERFACE))
        assertEquals(Value.Known(7, INT), execute(services, DYNAMIC_CALLER).trusted()!!.terminatingConsensus(0))
        assertSame(implementationA, snapshotB.applicationMethod(DYNAMIC_IMPLEMENTATION))
        assertSame(siteA.declaration, siteB.declaration)
        assertTrue(siteA.dynamicLoadingEvidence.isPresent)

        val insert = services.methodEditor.beginEdit(removed.editedMethodView)
        val forName = insert.stageMethod(
            "Ljava/lang/Class;",
            "forName",
            listOf("Ljava/lang/String;"),
            "Ljava/lang/Class;",
        )
        insert.insertInstructionsBefore(
            removed.editedMethodView.terminalInstructionAddresses.single(),
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction35c(
                        Opcode.INVOKE_STATIC,
                        1,
                        0,
                        0,
                        0,
                        0,
                        0,
                        ImmutableMethodReference(
                            "Ljava/lang/Class;",
                            "forName",
                            listOf("Ljava/lang/String;"),
                            "Ljava/lang/Class;",
                        ),
                    ),
                    DexReferenceOperand.Staged(forName),
                ),
                encoded(ImmutableInstruction11x(Opcode.MOVE_RESULT_OBJECT, 0)),
            ),
        )
        insert.commit() as MethodEditCommitResult.Committed
        val snapshotC = index.currentSnapshot
        val siteC = requireNotNull(snapshotC.applicationMethod(DYNAMIC_SITE))

        assertEquals(setOf(DYNAMIC_SITE), siteC.dynamicLoadingEvidence.methodSignatures)
        assertEquals(setOf(DYNAMIC_SITE), snapshotC.dynamicLoadingEvidence.methodSignatures)
        assertFalse(index.resolver(snapshotC).dispatchWorldIsComplete(DYNAMIC_INTERFACE))
        assertEquals(Value.Unknown(INT), execute(services, DYNAMIC_CALLER).trusted()!!.terminatingConsensus(0))
        assertSame(implementationA, snapshotC.applicationMethod(DYNAMIC_IMPLEMENTATION))
        assertSame(siteB.declaration, siteC.declaration)
    }

    @Test
    fun inheritedClassLoaderCallCountsAsDynamicLoadingEvidence() {
        val index = index(SOURCES + SUBCLASS_SOURCES)
        val services = VmServices(index)
        assertEquals(
            setOf(DYNAMIC_SITE, SUBCLASS_DYNAMIC_SITE),
            index.currentSnapshot.dynamicLoadingEvidence.methodSignatures,
        )

        val direct = services.methodEditor.beginEdit(requireNotNull(index.methodView(DYNAMIC_SITE)))
        direct.removeInstructions(listOf(2, 5))
        direct.commit() as MethodEditCommitResult.Committed
        val inheritedOnly = index.currentSnapshot

        assertEquals(setOf(SUBCLASS_DYNAMIC_SITE), inheritedOnly.dynamicLoadingEvidence.methodSignatures)
        assertFalse(index.resolver(inheritedOnly).dispatchWorldIsComplete(DYNAMIC_INTERFACE))

        val inherited = services.methodEditor.beginEdit(requireNotNull(index.methodView(SUBCLASS_DYNAMIC_SITE)))
        inherited.removeInstructions(listOf(0, 3))
        inherited.commit() as MethodEditCommitResult.Committed
        val noSites = index.currentSnapshot

        assertFalse(noSites.dynamicLoadingEvidence.isPresent)
        assertTrue(index.resolver(noSites).dispatchWorldIsComplete(DYNAMIC_INTERFACE))
    }

    @Test
    fun committedViewAndSnapshotSurvivePartialExecutionButTrustIsRefused() {
        val index = index()
        val services = VmServices(index)
        val edit = services.methodEditor.beginEdit(requireNotNull(index.methodView(OTHER)))
        edit.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 4))))
        val committed = edit.commit() as MethodEditCommitResult.Committed
        val snapshotB = index.currentSnapshot

        val graph = services.newSession(committed.editedMethodView, Limits(maxSteps = 1)).run()

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.METHOD_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        assertSame(snapshotB, index.currentSnapshot)
        assertSame(committed.editedMethodView, graph.methodView)
        assertSame(snapshotB.programRevision, graph.programRevision)
        assertEquals(Opcode.CONST_4, committed.editedMethodView.opcodeAt(0))
    }

    @Test
    fun currentSessionRejectsStaleMismatchedAndForeignEntryFramesAfterCommit() {
        val index = index()
        val services = VmServices(index)
        val viewA = requireNotNull(index.methodView(TARGET))
        val frameA = Frame.entryFrame(viewA)
        val edit = services.methodEditor.beginEdit(viewA)
        edit.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 2))))
        val committed = edit.commit() as MethodEditCommitResult.Committed
        val viewB = committed.editedMethodView

        assertThrows<IllegalArgumentException> { services.newSession(viewB, entryFrame = frameA) }
        assertThrows<IllegalArgumentException> {
            services.newSession(viewB, entryFrame = Frame.entryFrame(requireNotNull(index.methodView(OTHER))))
        }

        val foreign = index()
        assertThrows<IllegalArgumentException> {
            services.newSession(
                viewB,
                entryFrame = Frame.entryFrame(requireNotNull(foreign.methodView(TARGET))),
            )
        }
    }

    @Test
    fun serializationReadsOnlyItsSnapshotAndMutatesNeitherRevision() {
        val index = index()
        val services = VmServices(index)
        val writer = ProgramDexWriter()
        val snapshotA = index.currentSnapshot
        val classesA = snapshotA.applicationClasses
        val targetA = requireNotNull(snapshotA.applicationMethod(TARGET))
        val bytesA = writer.writeToBytes(snapshotA)

        val edit = services.methodEditor.beginEdit(requireNotNull(index.methodView(TARGET)))
        edit.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 2))))
        edit.commit() as MethodEditCommitResult.Committed
        val snapshotB = index.currentSnapshot
        val classesB = snapshotB.applicationClasses
        val targetB = requireNotNull(snapshotB.applicationMethod(TARGET))
        val bytesB = writer.writeToBytes(snapshotB)

        assertFalse(bytesA.contentEquals(bytesB))
        assertArrayEquals(bytesA, writer.writeToBytes(snapshotA))
        assertArrayEquals(bytesB, writer.writeToBytes(snapshotB))
        assertSame(snapshotB, index.currentSnapshot)
        assertSame(classesA, snapshotA.applicationClasses)
        assertSame(classesB, snapshotB.applicationClasses)
        assertSame(snapshotA.hierarchy, snapshotB.hierarchy)
        assertSame(snapshotA.frameworkCatalog, snapshotB.frameworkCatalog)
        assertSame(snapshotA.outputApiPolicy, snapshotB.outputApiPolicy)
        assertSame(snapshotA.stringInstructionEncoding, snapshotB.stringInstructionEncoding)
        assertSame(targetA.declaration, targetB.declaration)
        assertSame(snapshotA.applicationClass(UNRELATED), snapshotB.applicationClass(UNRELATED))
        assertEquals(
            snapshotA.dynamicLoadingEvidence.methodSignatures,
            snapshotB.dynamicLoadingEvidence.methodSignatures,
        )
        assertEquals(snapshotA.dexReferenceClosure.strings, snapshotB.dexReferenceClosure.strings)
        assertEquals(snapshotA.dexReferenceClosure.types, snapshotB.dexReferenceClosure.types)
        assertEquals(snapshotA.dexReferenceClosure.fields, snapshotB.dexReferenceClosure.fields)
        assertEquals(snapshotA.dexReferenceClosure.methods, snapshotB.dexReferenceClosure.methods)
        assertEquals(snapshotA.dexReferenceClosure.prototypes, snapshotB.dexReferenceClosure.prototypes)
        assertEquals(snapshotA.dexReferenceClosure.methodHandles, snapshotB.dexReferenceClosure.methodHandles)
        assertEquals(snapshotA.dexReferenceClosure.callSites, snapshotB.dexReferenceClosure.callSites)
    }

    private fun index(sources: List<String> = SOURCES): ClassIndex {
        val directory = Files.createDirectory(root.resolve("fixture-${fixtureSequence++}"))
        sources.forEachIndexed { index, source ->
            Files.writeString(directory.resolve("fixture-$index.smali"), source.trimIndent())
        }
        return ClassIndex.open(directory.toFile())
    }

    private fun execute(services: VmServices, methodSignature: String): ExecutionGraph =
        services.newSession(requireNotNull(services.classIndex.methodView(methodSignature))).run()

    private fun encoded(instruction: com.android.tools.smali.dexlib2.iface.instruction.Instruction): InstructionTemplate =
        InstructionTemplate.Encoded(instruction)

    private companion object {
        val INT = VirtualType.of("I")
        val DYNAMIC_INTERFACE = VirtualType.of("Lreexecution/Dispatch;")
        const val CALLER = "Lreexecution/Main;->caller()I"
        const val TARGET = "Lreexecution/Main;->target()I"
        const val OTHER = "Lreexecution/Main;->other()I"
        const val FRAMEWORK_CALLER = "Lreexecution/Main;->frameworkCaller()I"
        const val FRAMEWORK_METHOD = "Ljava/lang/Integer;->numberOfLeadingZeros(I)I"
        const val INIT_TRIGGER = "Lreexecution/Init;->trigger()I"
        const val CLASS_INITIALIZER = "Lreexecution/Init;-><clinit>()V"
        const val DYNAMIC_SITE = "Lreexecution/DynamicSite;->site()V"
        const val SUBCLASS_DYNAMIC_SITE = "Lreexecution/SubclassDynamicSite;->site(Lreexecution/MyLoader;Ljava/lang/String;)V"
        const val DYNAMIC_CALLER = "Lreexecution/DynamicCaller;->call(Lreexecution/Dispatch;)I"
        const val DYNAMIC_IMPLEMENTATION = "Lreexecution/OnlyImplementation;->value()I"
        const val UNRELATED = "Lreexecution/Unrelated;"

        val SOURCES = listOf(
            """
                .class public Lreexecution/Main;
                .super Ljava/lang/Object;

                .method public static caller()I
                    .registers 1
                    nop
                    invoke-static {}, Lreexecution/Main;->target()I
                    move-result v0
                    return v0
                .end method

                .method public static target()I
                    .registers 2
                    :try_start
                    const/4 v0, 0x1
                    :try_end
                    .catchall {:try_start .. :try_end} :handler
                    return v0
                    :handler
                    move-exception v1
                    const/4 v0, -0x1
                    return v0
                .end method

                .method public static other()I
                    .registers 1
                    const/4 v0, 0x3
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
            """,
            """
                .class public Lreexecution/Init;
                .super Ljava/lang/Object;
                .field public static value:I

                .method static constructor <clinit>()V
                    .registers 1
                    const/4 v0, 0x1
                    sput v0, Lreexecution/Init;->value:I
                    return-void
                .end method

                .method public static trigger()I
                    .registers 1
                    nop
                    sget v0, Lreexecution/Init;->value:I
                    return v0
                .end method
            """,
            """
                .class public interface abstract Lreexecution/Dispatch;
                .super Ljava/lang/Object;

                .method public abstract value()I
                .end method
            """,
            """
                .class public Lreexecution/OnlyImplementation;
                .super Ljava/lang/Object;
                .implements Lreexecution/Dispatch;

                .method public value()I
                    .registers 2
                    const/4 v0, 0x7
                    return v0
                .end method
            """,
            """
                .class public Lreexecution/DynamicSite;
                .super Ljava/lang/Object;

                .method public static site()V
                    .registers 1
                    const-string v0, "java.lang.String"
                    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
                    move-result-object v0
                    return-void
                .end method
            """,
            """
                .class public Lreexecution/DynamicCaller;
                .super Ljava/lang/Object;

                .method public static call(Lreexecution/Dispatch;)I
                    .registers 2
                    invoke-interface {p0}, Lreexecution/Dispatch;->value()I
                    move-result v0
                    return v0
                .end method
            """,
            """
                .class public Lreexecution/Unrelated;
                .super Ljava/lang/Object;

                .method public static value()I
                    .registers 1
                    const/4 v0, 0x5
                    return v0
                .end method
            """,
        )
        val SUBCLASS_SOURCES = listOf(
            """
                .class public Lreexecution/MyLoader;
                .super Ljava/lang/ClassLoader;
            """,
            """
                .class public Lreexecution/SubclassDynamicSite;
                .super Ljava/lang/Object;

                .method public static site(Lreexecution/MyLoader;Ljava/lang/String;)V
                    .registers 2
                    invoke-virtual {p0, p1}, Lreexecution/MyLoader;->loadClass(Ljava/lang/String;)Ljava/lang/Class;
                    move-result-object p0
                    return-void
                .end method
            """,
        )
    }
}
