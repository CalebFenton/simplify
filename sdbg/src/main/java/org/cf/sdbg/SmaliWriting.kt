package org.cf.sdbg

import org.cf.smalivm.debug.Debugger
import org.jf.baksmali.Adaptors.ClassDefinition
import org.jf.baksmali.Adaptors.CommentingIndentingWriter
import org.jf.baksmali.Adaptors.MethodDefinition
import org.jf.dexlib2.dexbacked.DexBackedClassDef
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.util.ReferenceUtil
import org.jf.util.IndentingWriter
import java.io.IOException
import java.io.StringWriter
import java.util.*
import java.util.stream.IntStream

@Throws(IOException::class)
fun ClassDefinition.writeTo2(writer: IndentingWriter, debugger: Debugger) {
    val writeClass = ClassDefinition::class.java.getDeclaredMethod("writeClass", IndentingWriter::class.java)
    writeClass.isAccessible = true
    writeClass.invoke(this, writer)

    val writeSuper = ClassDefinition::class.java.getDeclaredMethod("writeSuper", IndentingWriter::class.java)
    writeSuper.isAccessible = true
    writeSuper.invoke(this, writer)

    val writeSourceFile = ClassDefinition::class.java.getDeclaredMethod("writeSourceFile", IndentingWriter::class.java)
    writeSourceFile.isAccessible = true
    writeSourceFile.invoke(this, writer)

    val writeInterfaces = ClassDefinition::class.java.getDeclaredMethod("writeInterfaces", IndentingWriter::class.java)
    writeInterfaces.isAccessible = true
    writeInterfaces.invoke(this, writer)

    val writeAnnotations = ClassDefinition::class.java.getDeclaredMethod("writeAnnotations", IndentingWriter::class.java)
    writeAnnotations.isAccessible = true
    writeAnnotations.invoke(this, writer)

    val writeStaticFields = ClassDefinition::class.java.getDeclaredMethod("writeStaticFields", IndentingWriter::class.java)
    writeStaticFields.isAccessible = true
    val staticFields: Set<String> = writeStaticFields.invoke(this, writer) as Set<String>

    val writeInstanceFields = ClassDefinition::class.java.getDeclaredMethod("writeInstanceFields", IndentingWriter::class.java, Set::class.java)
    writeInstanceFields.isAccessible = true
    writeInstanceFields.invoke(this, writer, staticFields)

    val directMethods: Set<String> = this.writeDirectMethods2(writer, debugger)
    writeVirtualMethods2(writer, directMethods, debugger)
}

@Throws(IOException::class)
fun ClassDefinition.writeVirtualMethods2(writer: IndentingWriter, directMethods: Set<String>, debugger: Debugger) {
    var wroteHeader = false
    val writtenMethods: MutableSet<String> = HashSet()
    val virtualMethods: Iterable<Method> = if (classDef is DexBackedClassDef) {
        (classDef as DexBackedClassDef).getVirtualMethods(false)
    } else {
        classDef.virtualMethods
    }
    for (method in virtualMethods) {
        if (!wroteHeader) {
            writer.write("\n")
            writer.write("# virtual methods")
            wroteHeader = true
        }
        writer.write("\n")
        val methodString = ReferenceUtil.getMethodDescriptor(method, true)
        var methodWriter = writer
        if (!writtenMethods.add(methodString)) {
            writer.write("# duplicate method ignored\n")
            methodWriter = CommentingIndentingWriter(writer)
        } else if (directMethods.contains(methodString)) {
            writer.write("# There is both a direct and virtual method with this signature.\n" +
                    "# You will need to rename one of these methods, including all references.\n")
        }
        val methodImpl = method.implementation
        if (methodImpl == null) {
            MethodDefinition.writeEmptyMethodTo(methodWriter, method, options)
        } else {
            val methodDefinition = MethodDefinition(this, method, methodImpl)
            methodDefinition.writeTo2(methodWriter, debugger)
        }
    }
}

@Throws(IOException::class)
fun ClassDefinition.writeDirectMethods2(writer: IndentingWriter, debugger: Debugger): Set<String> {
    var wroteHeader = false
    val writtenMethods: MutableSet<String> = HashSet()
    val directMethods: Iterable<Method> = if (classDef is DexBackedClassDef) {
        (classDef as DexBackedClassDef).getDirectMethods(false)
    } else {
        classDef.directMethods
    }
    for (method in directMethods) {
        if (!wroteHeader) {
            writer.write("\n")
            writer.write("# direct methods")
            wroteHeader = true
        }
        writer.write('\n'.toInt())
        // TODO: check for method validation errors
        val methodString = ReferenceUtil.getMethodDescriptor(method, true)
        var methodWriter = writer
        if (!writtenMethods.add(methodString)) {
            writer.write("# duplicate method ignored\n")
            methodWriter = CommentingIndentingWriter(writer)
        }
        val methodImpl = method.implementation
        if (methodImpl == null) {
            MethodDefinition.writeEmptyMethodTo(methodWriter, method, options)
        } else {
            val methodDefinition = MethodDefinition(this, method, methodImpl)
            methodDefinition.writeTo2(methodWriter, debugger)
        }
    }
    return writtenMethods
}

fun MethodDefinition.writeTo2(writer: IndentingWriter, debugger: Debugger, start: Int = 0, stop: Int = Int.MAX_VALUE) {
    val stringWriter = StringWriter()
    val dummyWriter = IndentingWriter(stringWriter)
    this.writeTo(dummyWriter)
    val rawLines = stringWriter.toString().split("\n").toMutableList()
    val signature = ReferenceUtil.getMethodDescriptor(this.method)

    // I like seeing the full method signature.
    val parts = rawLines[0].split(' ').toMutableList()
    parts[parts.size - 1] = signature
    rawLines[0] = parts.joinToString(" ")

    var index = 0
    IntStream.range(0, rawLines.size).forEach { n: Int ->
        run {
            if (rawLines[n].startsWith(" ")) {
                val trimLine = rawLines[n].trim()
                if (trimLine.startsWith("#@")) {
                    rawLines[n] = "\n"

                    var instructionLineIndex = n + 1
                    while (rawLines[instructionLineIndex].trim().startsWith(".")) {
                        instructionLineIndex++
                    }
                    val sb = StringBuilder()
                    sb.append("$index:${getIndexPostfix(debugger, signature, index)}")
                    while (sb.length < 8) {
                        sb.append(' ')
                    }
                    val prefix = sb.toString()
                    val postfix = "  $trimLine"
                    val instructionLine = "${rawLines[instructionLineIndex].replaceFirst("    ", prefix)}$postfix"
                    if (index < start || index > stop) {
                        rawLines[instructionLineIndex] = "\n"
                    } else {
                        rawLines[instructionLineIndex] = instructionLine
                    }
                    index++
                } else {
                    if (rawLines[n].trim().length == rawLines[n].length - 4) {
                        rawLines[n] = rawLines[n].replace("    ", "        ")
                    }
                }
            }
        }
    }

    val methodLines = rawLines
            .filter { l -> l.trim().isNotEmpty() }
            .joinToString("\n")
    writer.write(methodLines)
    writer.write("\n")
}

private fun getIndexPostfix(debugger: Debugger, methodSignature: String, lineNumber: Int): String {
    val sb: StringBuilder = StringBuilder()
    if (debugger.isBreakpoint(methodSignature, lineNumber)) {
        sb.append('b')
    }
    if (methodSignature == debugger.currentMethodSignature && lineNumber == debugger.currentIndex) {
        sb.append('>')
    }
    return sb.toString()
}
