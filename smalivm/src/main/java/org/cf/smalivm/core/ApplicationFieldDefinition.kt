package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.immutable.ImmutableField

/** Immutable application field declaration and output metadata. */
internal class ApplicationFieldDefinition internal constructor(internal val declaration: ImmutableField) {
    internal val name: String get() = declaration.name
    internal val type: String get() = declaration.type
    internal val accessFlags: Int get() = declaration.accessFlags
}
