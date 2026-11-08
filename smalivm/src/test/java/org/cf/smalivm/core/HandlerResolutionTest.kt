package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import kotlinx.collections.immutable.persistentSetOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/** Independent EX-03 first-match, EX-04 range, EX-13 metadata, EX-14 uncertainty, and cost checks. */
class HandlerResolutionTest {
    private val throwable = type("Ljava/lang/Throwable;")
    private val exception = type("Ljava/lang/Exception;")
    private val runtime = type("Ljava/lang/RuntimeException;")
    private val npe = type("Ljava/lang/NullPointerException;")
    private val io = type("Ljava/io/IOException;")
    private val objectType = type("Ljava/lang/Object;")
    private val cloneable = type("Ljava/lang/Cloneable;")
    private val serializable = type("Ljava/io/Serializable;")
    private val hierarchy = tableHierarchy()

    @Test
    fun `EX-03 declaration order wins instead of handler address order`() {
        val subject = symbolic(ThrowableDomain.constrainedTo(runtime, hierarchy))
        val resolution = resolve(
            subject,
            CatchClause(exception, 90),
            CatchClause(runtime, 10),
        )

        assertEquals(listOf(90), resolution.routes.map(HandlerRoute::handlerAddress))
        assertNull(resolution.unhandled)
    }

    @Test
    fun `broad handler shadows a later narrow handler while preserving the residual`() {
        val resolution = resolve(
            symbolic(ThrowableDomain.any()),
            CatchClause(exception, 90),
            CatchClause(runtime, 10),
        )

        assertEquals(listOf(90), resolution.routes.map(HandlerRoute::handlerAddress))
        assertEquals(persistentSetOf(exception), resolution.unhandled!!.subject.domain.excludedRoots)
    }

    @Test
    fun `narrow then broad handlers retain distinct first-match constraints`() {
        val resolution = resolve(
            symbolic(ThrowableDomain.any()),
            CatchClause(runtime, 10),
            CatchClause(exception, 90),
        )

        assertEquals(listOf(10, 90), resolution.routes.map(HandlerRoute::handlerAddress))
        assertEquals(runtime, resolution.routes[0].subject.domain.trustedUpperBound)
        assertEquals(exception, resolution.routes[1].subject.domain.trustedUpperBound)
        assertEquals(persistentSetOf(runtime), resolution.routes[1].subject.domain.excludedRoots)
        assertEquals(
            persistentSetOf(runtime, exception),
            resolution.unhandled!!.subject.domain.excludedRoots,
        )
    }

    @Test
    fun `duplicate clauses stay encoded but only the first duplicate is feasible`() {
        val method = view(
            listOf(block(CatchClause(runtime, 10), CatchClause(runtime, 20))),
        )

        val table = method.handlerTableAt(5) as HandlerTableLookup.Found
        val resolution = routed(resolveHandlers(5, symbolic(ThrowableDomain.any()), method, hierarchy))

        assertEquals(2, table.table.clauses.size)
        assertEquals(listOf(10), resolution.routes.map(HandlerRoute::handlerAddress))
    }

    @Test
    fun `identical ranges form one ordered canonical table`() {
        val method = view(
            listOf(
                block(CatchClause(runtime, 10)),
                block(CatchClause(exception, 20), catchAll = 30),
            ),
        )

        val table = (method.handlerTableAt(5) as HandlerTableLookup.Found).table

        assertEquals(
            listOf(
                HandlerClause.Typed(runtime, 10),
                HandlerClause.Typed(exception, 20),
                HandlerClause.CatchAll(30),
            ),
            table.clauses,
        )
    }

    @Test
    fun `canonical address indexing does not reorder the public TryBlock projection`() {
        val declared = listOf(
            TryBlock(20, 30, listOf(CatchClause(exception, 90)), null),
            TryBlock(0, 10, listOf(CatchClause(runtime, 10)), null),
        )
        val method = view(declared)

        assertEquals(declared, method.tryBlocks)
        assertEquals(90, routed(resolveHandlers(25, symbolic(ThrowableDomain.any()), method, hierarchy)).routes.single().handlerAddress)
        assertEquals(10, routed(resolveHandlers(5, symbolic(ThrowableDomain.any()), method, hierarchy)).routes.single().handlerAddress)
    }

    @Test
    fun `EX-13 overlapping distinct ranges are malformed metadata`() {
        val method = view(
            listOf(
                TryBlock(0, 10, listOf(CatchClause(runtime, 10)), null),
                TryBlock(5, 15, listOf(CatchClause(exception, 20)), null),
            ),
        )

        assertEquals(HandlerTableLookup.MalformedMetadata, method.handlerTableAt(5))
        assertTrue(
            resolveHandlers(5, symbolic(ThrowableDomain.any()), method, hierarchy) is
                HandlerResolution.MalformedMetadata,
        )
    }

    @Test
    fun `a class model failure becomes a host failure result carrying it`() {
        val failure = ClassLoadFailure("unreadable ancestry")
        val failing = object : ThrowableHierarchy by hierarchy {
            override fun facts(type: VirtualType): ThrowableTypeFacts =
                if (type == runtime) throw failure else hierarchy.facts(type)
        }

        val resolution = resolveHandlers(5, symbolic(ThrowableDomain.any()), view(listOf(block(CatchClause(runtime, 10)))), failing)

        assertSame(failure, (resolution as HandlerResolution.HostFailure).failure)
    }

    @Test
    fun `catch Throwable and catch all each consume the complete residual`() {
        val typed = resolve(
            symbolic(ThrowableDomain.any()),
            CatchClause(runtime, 10),
            CatchClause(throwable, 90),
        )
        val catchAll = routed(
            resolveHandlers(
                5,
                symbolic(ThrowableDomain.any()),
                view(listOf(block(CatchClause(runtime, 10), catchAll = 90))),
                hierarchy,
            ),
        )

        assertEquals(listOf(10, 90), typed.routes.map(HandlerRoute::handlerAddress))
        assertNull(typed.unhandled)
        assertEquals(listOf(10, 90), catchAll.routes.map(HandlerRoute::handlerAddress))
        assertNull(catchAll.unhandled)
    }

    @Test
    fun `missing catch facts preserve caught and residual subjects`() {
        val missing = type("Lmissing/Catch;")
        val resolution = resolve(symbolic(ThrowableDomain.any()), CatchClause(missing, 90))

        assertEquals(listOf(90), resolution.routes.map(HandlerRoute::handlerAddress))
        assertEquals(persistentSetOf(missing), resolution.routes.single().subject.domain.unresolvedRequiredRoots)
        assertEquals(persistentSetOf(missing), resolution.unhandled!!.subject.domain.excludedRoots)
        assertSame(resolution.routes.single().subject.value, resolution.unhandled!!.subject.value)
    }

    @Test
    fun `missing exact runtime facts preserve matching and escaping paths`() {
        val missingThrown = type("Lmissing/Thrown;")
        val subject = symbolic(ThrowableDomain.exact(missingThrown, hierarchy))
        val resolution = resolve(subject, CatchClause(runtime, 90))

        assertEquals(listOf(90), resolution.routes.map(HandlerRoute::handlerAddress))
        assertEquals(persistentSetOf(runtime), resolution.routes.single().subject.domain.unresolvedRequiredRoots)
        assertEquals(persistentSetOf(runtime), resolution.unhandled!!.subject.domain.excludedRoots)
    }

    @Test
    fun `broad handler excludes a later narrow handler when exact runtime facts are missing`() {
        val missingThrown = type("Lmissing/Thrown;")
        val subject = symbolic(ThrowableDomain.exact(missingThrown, hierarchy))
        val resolution = resolve(
            subject,
            CatchClause(exception, 90),
            CatchClause(runtime, 10),
        )

        assertEquals(listOf(90), resolution.routes.map(HandlerRoute::handlerAddress))
        assertEquals(persistentSetOf(exception), resolution.routes.single().subject.domain.unresolvedRequiredRoots)
        assertEquals(persistentSetOf(exception), resolution.unhandled!!.subject.domain.excludedRoots)
    }

    @Test
    fun `a required narrow root is fully consumed by a later broad handler`() {
        val missingThrown = type("Lmissing/Thrown;")
        val subject = symbolic(ThrowableDomain.exact(missingThrown, hierarchy))
        val narrow = resolve(subject, CatchClause(runtime, 90)).routes.single().subject

        val broad = resolve(narrow, CatchClause(exception, 10))

        assertEquals(listOf(10), broad.routes.map(HandlerRoute::handlerAddress))
        assertNull(broad.unhandled)
    }

    @Test
    fun `exact heap runtime type overrides a loose Value type tag`() {
        val method = view(emptyList())
        val frame = Frame.entryFrame(method)
        val allocated = frame.allocateThrowable(npe, NullPointerException("runtime witness"))
        val loose = Value.Known(allocated.raw, throwable)
        val subject = ThrownSubject.fromHeap(loose, frame, CoreTester.classIndex.currentSnapshot.hierarchy)

        val resolution = resolve(
            subject,
            CatchClause(npe, 90),
            CatchClause(throwable, 10),
        )

        assertEquals(listOf(90), resolution.routes.map(HandlerRoute::handlerAddress))
        assertEquals(npe, resolution.routes.single().subject.domain.exactRuntimeType)
        assertSame(subject, resolution.routes.single().subject)
    }

    @Test
    fun `concrete subject factories reject contradictory runtime domains and frames`() {
        val method = view(emptyList())
        val frame = Frame.entryFrame(method)
        val allocated = frame.allocateThrowable(npe, NullPointerException("runtime witness"))
        val loose = Value.Known(allocated.raw, throwable)
        val subject = ThrownSubject.fromHeap(loose, frame, hierarchy)
        val contradictory = ThrowableDomain.exact(runtime, hierarchy)
        val contradictoryValue = FrameValue.refined(loose, contradictory)

        assertThrows<IllegalArgumentException> {
            ThrownSubject.fromFrameValue(contradictoryValue, frame, hierarchy)
        }
        assertThrows<IllegalArgumentException> { subject.refinedTo(contradictory, hierarchy) }
        assertThrows<IllegalArgumentException> {
            ThrowPath.create(Frame.entryFrame(method), subject, hierarchy)
        }
        assertThrows<IllegalArgumentException> { ThrowableDomain.exact(objectType, hierarchy) }
    }

    @Test
    fun `known non-Throwable catch type is malformed metadata`() {
        val resolution = resolveHandlers(
            5,
            symbolic(ThrowableDomain.any()),
            view(listOf(block(CatchClause(objectType, 90)))),
            hierarchy,
        )

        assertTrue(resolution is HandlerResolution.MalformedMetadata)
    }

    @Test
    fun `ClassIndex builds superclass facts and caches an absent descriptor`() {
        val realHierarchy = CoreTester.classIndex.currentSnapshot.hierarchy
        val npeFacts = realHierarchy.facts(npe)
        val missingType = type("Lmissing/HierarchyFact;")
        val firstMissing = realHierarchy.facts(missingType)
        val secondMissing = realHierarchy.facts(missingType)

        assertEquals(TypeRelation.YES, npeFacts.throwableRelation)
        assertTrue(throwable in npeFacts.knownClassAncestors)
        assertTrue(npeFacts.ancestryComplete)
        assertEquals(ThrowableTypeKind.CLASS, npeFacts.kind)
        assertEquals(TypeRelation.UNKNOWN, firstMissing.throwableRelation)
        assertTrue(!firstMissing.descriptorPresent)
        assertSame(firstMissing, secondMissing)
    }

    @Test
    fun `Throwable domain can satisfy Serializable interface cast`() {
        val domain = ThrowableDomain.constrainedTo(throwable, hierarchy)

        val partition = partitionThrowableDomain(domain, serializable, hierarchy)

        assertSame(domain, partition.matching)
        assertNull(partition.residual)
    }

    @Test
    fun `symbolic class upper bound keeps interface cast success while exact type stays precise`() {
        val symbolic = ThrowableDomain.constrainedTo(exception, hierarchy)
        val exact = ThrowableDomain.exact(npe, hierarchy)

        val symbolicPartition = partitionThrowableDomain(symbolic, cloneable, hierarchy)
        val exactPartition = partitionThrowableDomain(exact, cloneable, hierarchy)

        assertTrue(symbolicPartition.matching != null)
        assertTrue(symbolicPartition.residual != null)
        assertNull(exactPartition.matching)
        assertSame(exact, exactPartition.residual)
    }

    @Test
    fun `EX-04 canonical ranges include start and exclude end`() {
        val method = view(listOf(block(CatchClause(throwable, 90))))
        val expected = mapOf(-1 to false, 0 to true, 9 to true, 10 to false)

        for ((pc, handled) in expected) {
            val resolution = routed(resolveHandlers(pc, symbolic(ThrowableDomain.any()), method, hierarchy))
            assertEquals(handled, resolution.routes.isNotEmpty(), "pc=$pc")
            assertEquals(!handled, resolution.unhandled != null, "pc=$pc")
        }
    }

    @Test
    fun `adversarial missing tables route every clause in near linear time`() {
        // Bug class: each missing catch type re-walks or copies the residual's excluded roots, which is
        // quadratic in the clause count. Oracle: every missing type may match, so each clause routes and the
        // residual stays unhandled. Logarithmic per-clause work costs about eight times as much for eight times
        // the clauses; walking every earlier excluded root costs about sixty-four times as much.
        for (handlerCount in listOf(1, 8, 64, 512, 2048)) {
            val resolution = resolve(symbolic(ThrowableDomain.any()), *missingCatches(handlerCount))

            assertEquals((100 until 100 + handlerCount).toList(), resolution.routes.map { it.handlerAddress })
            assertTrue(resolution.unhandled != null, "H=$handlerCount")
        }
        val subject = symbolic(ThrowableDomain.any())
        val small = view(listOf(block(*missingCatches(COST_CLAUSES))))
        val large = view(listOf(block(*missingCatches(8 * COST_CLAUSES))))

        assertCostRatio(24.0, { small }, { large }) { routed(resolveHandlers(5, subject, it, hierarchy)) }

        val resolution = routed(resolveHandlers(5, subject, large, hierarchy))
        assertEquals(8 * COST_CLAUSES, resolution.routes.size)
        assertTrue(resolution.unhandled != null)
    }

    private fun missingCatches(count: Int): Array<CatchClause> =
        Array(count) { index -> CatchClause(type("Lmissing/Catch$index;"), 100 + index) }

    private fun resolve(subject: ThrownSubject, vararg catches: CatchClause): HandlerResolution.Routed =
        routed(resolveHandlers(5, subject, view(listOf(block(*catches))), hierarchy))

    private fun routed(resolution: HandlerResolution): HandlerResolution.Routed {
        assertTrue(resolution is HandlerResolution.Routed) { "Expected routed resolution, got $resolution" }
        return resolution as HandlerResolution.Routed
    }

    private fun symbolic(domain: ThrowableDomain): ThrownSubject =
        ThrownSubject.symbolic(Value.Unknown(throwable), domain)

    private fun block(vararg catches: CatchClause, catchAll: Int? = null): TryBlock =
        TryBlock(0, 10, catches.toList(), catchAll)

    /** A static `()V` framework method of NOPs reaching every address [tryBlocks] names, then `return-void`. */
    private fun view(tryBlocks: List<TryBlock>): MethodView {
        val lastAddress = tryBlocks.maxOfOrNull { block ->
            maxOf(block.endAddress, block.catchAllAddress ?: 0, block.catches.maxOfOrNull(CatchClause::handlerAddress) ?: 0)
        } ?: 0
        val instructions = List(lastAddress + 1) { ImmutableInstruction10x(Opcode.NOP) } +
            ImmutableInstruction10x(Opcode.RETURN_VOID)
        val implementation = ImmutableMethodImplementation(0, instructions, dexTryBlocks(tryBlocks), null)
        val declaration = ImmutableMethod("Lresolver;", "test", null, "V", AccessFlags.STATIC.value, null, null, null)
        val method = FrameworkMethodDefinition(
            FrameworkMethodIdentity(MethodSignature.parse("Lresolver;->test()V")),
            declaration,
            MethodBody.import(implementation),
            DynamicLoadingEvidence.EMPTY,
        )
        return MethodView.of(method, CoreTester.classIndex.currentSnapshot.programRevision)
    }

    private fun tableHierarchy(): ThrowableHierarchy {
        val facts = mapOf(
            objectType to known(objectType),
            cloneable to knownInterface(cloneable),
            serializable to knownInterface(serializable),
            throwable to known(throwable, objectType, interfaces = persistentSetOf(serializable)),
            exception to known(exception, throwable, objectType, interfaces = persistentSetOf(serializable)),
            runtime to known(runtime, exception, throwable, objectType, interfaces = persistentSetOf(serializable)),
            npe to known(npe, runtime, exception, throwable, objectType, interfaces = persistentSetOf(serializable)),
            io to known(io, exception, throwable, objectType, interfaces = persistentSetOf(serializable)),
        )
        return ThrowableHierarchy { type ->
            facts[type] ?: ThrowableTypeFacts(
                type,
                persistentSetOf(),
                persistentSetOf(),
                ancestryComplete = false,
                interfaceAncestryComplete = false,
                throwableRelation = TypeRelation.UNKNOWN,
                kind = ThrowableTypeKind.UNKNOWN,
                descriptorPresent = false,
            )
        }
    }

    private fun known(
        type: VirtualType,
        vararg ancestors: VirtualType,
        interfaces: kotlinx.collections.immutable.PersistentSet<VirtualType> = persistentSetOf(),
    ): ThrowableTypeFacts {
        val all = persistentSetOf(type).addingAll(ancestors.toList())
        return ThrowableTypeFacts(
            type,
            all,
            interfaces,
            ancestryComplete = true,
            interfaceAncestryComplete = true,
            throwableRelation = if (throwable in all) TypeRelation.YES else TypeRelation.NO,
            kind = ThrowableTypeKind.CLASS,
            descriptorPresent = true,
        )
    }

    private fun knownInterface(type: VirtualType): ThrowableTypeFacts = ThrowableTypeFacts(
        type,
        persistentSetOf(objectType),
        persistentSetOf(),
        ancestryComplete = true,
        interfaceAncestryComplete = true,
        throwableRelation = TypeRelation.NO,
        kind = ThrowableTypeKind.INTERFACE,
        descriptorPresent = true,
    )

    private fun type(descriptor: String): VirtualType = VirtualType.of(descriptor)

    private companion object {
        const val COST_CLAUSES = 2_000
    }
}
