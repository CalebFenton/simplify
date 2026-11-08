package org.cf.sdbg

import org.cf.smalivm.core.Breakpoint
import org.cf.smalivm.core.Debugger
import org.cf.smalivm.core.MethodLookup
import org.cf.smalivm.core.MethodView
import org.cf.smalivm.core.ProgramDexWriter
import org.cf.smalivm.core.VmServices
import org.cf.smalivm.core.VirtualType
import com.android.tools.smali.baksmali.Adaptors.ClassDefinition
import com.android.tools.smali.baksmali.Adaptors.MethodDefinition
import com.android.tools.smali.baksmali.BaksmaliOptions
import com.android.tools.smali.baksmali.formatter.BaksmaliFormatter
import com.android.tools.smali.baksmali.formatter.BaksmaliWriter
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.formatter.DexFormatter
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.Method
import java.io.StringWriter
import java.io.Writer

/** A listing of the immutable program used by this debug session. */
internal class SmaliListing(private val services: VmServices) {
    private val snapshot = services.currentSnapshot

    // The public snapshot boundary preserves annotations, debug items and bodyless methods too.
    // Materialize only on the first listing, never merely to start or step a session.
    private val classes: Map<String, ClassDef> by lazy {
        DexBackedDexFile(null, ProgramDexWriter().writeToBytes(snapshot)).classes.associateBy { it.type }
    }

    fun hasClass(descriptor: String): Boolean = snapshot.applicationClass(descriptor) != null ||
        snapshot.frameworkCatalog.classDefinition(descriptor) != null

    fun writeClass(writer: Writer, descriptor: String, debugger: Debugger) {
        val definition = classDefinition(descriptor)
        val text = StringWriter()
        definition.writeTo(BaksmaliWriter(text))
        writeListing(writer, text.toString(), definition.classDef.methods.iterator(), debugger, 0, Int.MAX_VALUE)
    }

    fun writeMethod(writer: Writer, signature: String, debugger: Debugger, start: Int = 0, stop: Int = Int.MAX_VALUE): Boolean {
        require(start >= 0 && stop >= start) { "Invalid instruction range: $start,$stop" }
        val owner = signature.substringBefore("->")
        if (!hasClass(owner)) return false
        val lookup = services.classIndex.lookupMethod(VirtualType.of(owner), signature.substringAfter("->"))
        val resolvedSignature = (lookup as? MethodLookup.Runnable)?.view?.signature ?: signature
        val definition = classDefinition(resolvedSignature.substringBefore("->"))
        val method = definition.classDef.methods.firstOrNull {
            DexFormatter.INSTANCE.getMethodDescriptor(it) == resolvedSignature
        }
            ?: return false
        val text = StringWriter()
        val output = BaksmaliWriter(text)
        val implementation = method.implementation
        if (implementation == null) {
            MethodDefinition.writeEmptyMethodTo(output, method, definition)
        } else {
            MethodDefinition(definition, method, implementation).writeTo(output)
        }
        writeListing(writer, text.toString(), listOf(method).iterator(), debugger, start, stop)
        return true
    }

    private fun classDefinition(descriptor: String): ClassDefinition {
        val options = BaksmaliOptions().apply {
            sequentialLabels = true
            codeOffsets = true
            localsDirective = true
        }
        val classDef = if (snapshot.applicationClass(descriptor) != null) {
            classes.getValue(descriptor)
        } else {
            requireNotNull(snapshot.frameworkCatalog.classDefinition(descriptor)) { "Class not found: $descriptor" }
        }
        return ClassDefinition(options, classDef)
    }

    private fun writeListing(
        writer: Writer,
        text: String,
        methods: Iterator<Method>,
        debugger: Debugger,
        start: Int,
        stop: Int,
    ) {
        val breakpoints = debugger.breakpoints
        val cursor = debugger.cursor
        val formatter = BaksmaliFormatter()
        var view: MethodView? = null
        var signature: String? = null
        var address: Int? = null
        for (line in text.lineSequence()) {
            val trimmed = line.trim()
            if (line.startsWith(".method ")) {
                val method = methods.next()
                signature = DexFormatter.INSTANCE.getMethodDescriptor(method)
                view = (services.classIndex.lookupMethod(
                    VirtualType.of(method.definingClass), signature.substringAfter("->")) as? MethodLookup.Runnable)?.view
                address = null
                val shortDescriptor = formatter.getShortMethodDescriptor(method)
                check(line.endsWith(shortDescriptor)) { "Unexpected Baksmali method header: $line" }
                writer.write(line.removeSuffix(shortDescriptor) + signature + "\n")
                continue
            }
            if (trimmed == ".end method") {
                view = null
                signature = null
                address = null
            }
            val offset = CODE_OFFSET.matchEntire(trimmed)
            if (offset != null) {
                address = offset.groupValues[1].toInt(16)
                // Keep payload offsets as context, but never give data an executable index.
                if (view?.instructionIndexAtCodeAddress(address) != null) continue
            }
            val methodView = view
            val codeAddress = address
            val index = if (methodView != null && codeAddress != null) {
                methodView.instructionIndexAtCodeAddress(codeAddress)
            } else null
            // A code-offset comment can precede labels and debug directives. Only the actual
            // instruction with this address receives the index, not the next nonempty line.
            if (index != null && methodView != null && codeAddress != null &&
                trimmed.substringBefore(' ') == methodView.opcodeAt(codeAddress).name) {
                address = null
                if (index !in start..stop) continue
                val marker = buildString {
                    if (Breakpoint(signature!!, index) in breakpoints) append('b')
                    if (cursor != null && cursor.view.signature == signature && cursor.address == codeAddress) append('>')
                }
                writer.write("$index:$marker".padEnd(8) + trimmed + "  #@${codeAddress.toString(16)}\n")
            } else {
                writer.write(line)
                writer.write("\n")
            }
        }
    }

    private companion object {
        val CODE_OFFSET = Regex("#@([0-9a-fA-F]+)")
    }
}
