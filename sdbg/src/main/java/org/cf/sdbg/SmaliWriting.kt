package org.cf.sdbg

import org.jf.baksmali.Adaptors.AnnotationFormatter
import org.jf.baksmali.Adaptors.MethodDefinition
import org.jf.baksmali.Adaptors.MethodItem
import org.jf.baksmali.Adaptors.RegisterFormatter
import org.jf.baksmali.BaksmaliOptions
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.util.TypeUtils
import org.jf.util.IndentingWriter
import java.io.IOException

@Throws(IOException::class)
fun MethodDefinition.writeTo2(writer: IndentingWriter) {
    var parameterRegisterCount = 0
    if (!AccessFlags.STATIC.isSet(method.getAccessFlags())) {
        parameterRegisterCount++
    }
    writer.write(".method ")

    val writeAccessFlags = MethodDefinition::class.java
            .getDeclaredMethod(
                    "writeAccessFlags",
                    IndentingWriter::class.java, Int::class.java
            )
    writeAccessFlags.isAccessible = true
    writeAccessFlags.invoke(null, writer, method.getAccessFlags())
    writer.write(method.getName())
    writer.write("(")
    for (parameter in methodParameters) {
        val type = parameter.type
        writer.write(type)
        parameterRegisterCount++
        if (TypeUtils.isWideType(type)) {
            parameterRegisterCount++
        }
    }
    writer.write(")")
    writer.write(method.getReturnType())
    writer.write('\n'.toInt())
    writer.indent(4)
    if (classDef.options.localsDirective) {
        writer.write(".locals ")
        writer.printSignedIntAsDec(methodImpl.getRegisterCount() - parameterRegisterCount)
    } else {
        writer.write(".registers ")
        writer.printSignedIntAsDec(methodImpl.getRegisterCount())
    }
    writer.write('\n'.toInt())
    val writeParameters = MethodDefinition::class.java
            .getDeclaredMethod(
                    "writeParameters",
                    IndentingWriter::class.java, Method::class.java, List::class.java, BaksmaliOptions::class.java
            )
    writeParameters.isAccessible = true
    writeParameters.invoke(null, writer, method, methodParameters, classDef.options)
    if (registerFormatter == null) {
        registerFormatter = RegisterFormatter(classDef.options, methodImpl.getRegisterCount(),
                parameterRegisterCount)
    }
    var containingClass: String? = null
    if (classDef.options.implicitReferences) {
        containingClass = method.getDefiningClass()
    }
    AnnotationFormatter.writeTo(writer, method.getAnnotations(), containingClass)
    writer.write('\n'.toInt())
    val getMethodItems = MethodDefinition::class.java.getDeclaredMethod("getMethodItems")
    getMethodItems.isAccessible = true
    val methodItems: List<MethodItem> = getMethodItems.invoke(this) as List<MethodItem>
    for (methodItem in methodItems) {
        if (methodItem.writeTo(writer)) {
            writer.write("\n")
        }
    }
    writer.deindent(4)
    writer.write(".end method\n")
}

