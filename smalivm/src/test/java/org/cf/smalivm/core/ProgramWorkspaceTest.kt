package org.cf.smalivm.core

import java.nio.file.Path
import kotlinx.collections.immutable.persistentMapOf
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir

class ProgramWorkspaceTest {
    @TempDir
    lateinit var root: Path

    @Test
    fun workspaceOwnsOneProgramIdentityAndRejectsForeignPublication() {
        val first = task53ClassIndex(root, SOURCE)
        val second = ClassIndex(task53ClassManager(root))

        assertSame(first.programWorkspace, VmServices(first).classIndex.programWorkspace)
        assertThrows<IllegalArgumentException> {
            first.programWorkspace.publish(second.currentSnapshot)
        }
        assertSame(first.currentSnapshot, first.programWorkspace.currentSnapshot)
    }

    @Test
    fun changedBodyRequiresANewSignatureBoundMethodRevision() {
        val index = task53ClassIndex(root, SOURCE)
        val snapshot = index.currentSnapshot
        val definition = requireNotNull(snapshot.applicationClass("Ltask53/Workspace;"))
        val method = definition.methods.single()
        val body = MethodBody.import(
            ImmutableMethodImplementation(
                1,
                listOf(
                    ImmutableInstruction11n(Opcode.CONST_4, 0, 2),
                    ImmutableInstruction11x(Opcode.RETURN, 0),
                ),
                emptyList(),
                emptyList(),
            ),
        ).let(MethodLayoutSolver::solve).let(MethodBodyAssembler::assemble)

        assertThrows<IllegalArgumentException> {
            ApplicationMethodDefinition.withBody(method, body, method.methodRevision)
                .let { changed -> candidate(index, definition, changed) }
        }
        assertThrows<IllegalArgumentException> {
            ApplicationMethodDefinition.withBody(
                method,
                body,
                index.programWorkspace.newApplicationMethodRevision("Lwrong;->value()I"),
            )
        }
    }

    @Test
    fun methodBodyCandidateCannotChangeClassStructure() {
        val index = task53ClassIndex(root, SOURCE)
        val definition = requireNotNull(index.currentSnapshot.applicationClass("Ltask53/Workspace;"))
        val changed = ApplicationClassDefinition(
            definition.type,
            definition.accessFlags,
            "Ltask53/OtherSuperclass;",
            definition.interfaces,
            definition.sourceFile,
            definition.annotations,
            definition.staticFields,
            definition.instanceFields,
            definition.methods,
        )

        assertThrows<IllegalArgumentException> {
            ProgramSnapshot.candidate(
                index.currentSnapshot,
                index.programWorkspace.newProgramRevision(),
                persistentMapOf(changed.type to changed),
            )
        }
    }

    @Test
    fun workspaceRejectsPublicationWhoseReferenceClosureExceedsADexLimit() {
        val index = task53ClassIndex(root, SOURCE)
        val current = index.currentSnapshot
        val original = requireNotNull(current.applicationClass("Ltask53/Workspace;"))
        val method = original.methods.single()
        val overflowingBody = MethodBody.import(
            ImmutableMethodImplementation(
                1,
                List(65_537) { sequence ->
                    ImmutableInstruction21c(
                        Opcode.SGET,
                        0,
                        ImmutableFieldReference("Ltask53/Owner;", "field$sequence", "I"),
                    )
                } + ImmutableInstruction11x(Opcode.RETURN, 0),
                emptyList(),
                emptyList(),
            ),
        )
        val overflowingMethod = ApplicationMethodDefinition.withBody(
            method,
            overflowingBody,
            index.programWorkspace.newApplicationMethodRevision(method.methodSignature),
        )
        val invalidSnapshot = candidate(index, original, overflowingMethod)
        val limit = DexReferenceLimits.validate(
            invalidSnapshot.dexReferenceClosure,
            invalidSnapshot.applicationClasses.values,
        )
        assertEquals("field_ids", limit?.section)

        assertThrows<IllegalArgumentException> {
            index.programWorkspace.publish(invalidSnapshot)
        }
        assertSame(current, index.currentSnapshot)
    }

    @Test
    fun workspaceRejectsCandidateBuiltFromAStalePredecessor() {
        val index = task53ClassIndex(root, SOURCE)
        val current = index.currentSnapshot
        val first = ProgramSnapshot.candidate(
            current,
            index.programWorkspace.newProgramRevision(),
            current.applicationClasses,
        )
        val stale = ProgramSnapshot.candidate(
            current,
            index.programWorkspace.newProgramRevision(),
            current.applicationClasses,
        )

        index.programWorkspace.publish(first)

        assertThrows<IllegalArgumentException> { index.programWorkspace.publish(stale) }
        assertSame(first, index.currentSnapshot)
    }

    @Test
    fun workspaceRejectsStructurallyInvalidChangedMethod() {
        val index = task53ClassIndex(root, SOURCE)
        val current = index.currentSnapshot
        val definition = requireNotNull(current.applicationClass("Ltask53/Workspace;"))
        val method = definition.methods.single()
        val invalidBody = MethodBody.import(
            ImmutableMethodImplementation(
                1,
                listOf(
                    ImmutableInstruction11n(Opcode.CONST_4, 0, 1),
                    ImmutableInstruction11x(Opcode.RETURN_OBJECT, 0),
                ),
                emptyList(),
                emptyList(),
            ),
        )
        val invalidMethod = ApplicationMethodDefinition.withBody(
            method,
            invalidBody,
            index.programWorkspace.newApplicationMethodRevision(method.methodSignature),
        )
        val invalidClass = definition.withMethod(invalidMethod)
        val invalidSnapshot = ProgramSnapshot.candidate(
            current,
            index.programWorkspace.newProgramRevision(),
            persistentMapOf(invalidClass.type to invalidClass),
        )

        assertThrows<IllegalArgumentException> { index.programWorkspace.publish(invalidSnapshot) }
        assertSame(current, index.currentSnapshot)
    }

    private fun candidate(
        index: ClassIndex,
        originalClass: ApplicationClassDefinition,
        changedMethod: ApplicationMethodDefinition,
    ): ProgramSnapshot {
        val changedClass = ApplicationClassDefinition(
            originalClass.type,
            originalClass.accessFlags,
            originalClass.superclass,
            originalClass.interfaces,
            originalClass.sourceFile,
            originalClass.annotations,
            originalClass.staticFields,
            originalClass.instanceFields,
            listOf(changedMethod),
        )
        return ProgramSnapshot.candidate(
            index.currentSnapshot,
            index.programWorkspace.newProgramRevision(),
            persistentMapOf(changedClass.type to changedClass),
        )
    }

    private companion object {
        const val SOURCE = """
            .class public Ltask53/Workspace;
            .super Ljava/lang/Object;

            .method public static value()I
                .registers 1
                const/4 v0, 0x1
                return v0
            .end method
        """
    }
}
