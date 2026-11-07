package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.readsAsNullReference

/**
 * The assignment a field read makes: a reference-typed field reads a cell holding primitive bits as `null`,
 * the way ART fetches a reference, and a volatile read records `WEAK`.
 */
internal fun fieldRead(destRegister: Int, cell: Value, fieldType: VirtualType, isVolatile: Boolean): OpUpdate {
    val loaded = if (fieldType.isReference && cell.readsAsNullReference) Value.Known(null, fieldType) else cell
    val assign = OpUpdate.assign(destRegister, loaded)
    return if (isVolatile) assign.then(OpUpdate.effect(SideEffectLevel.WEAK)) else assign
}

/**
 * The value a field or array element of [slotType] holds after a store of [value]. Only the `-object` store
 * forms read their value operand as a reference ([readsAsReference]), so only they store a primitive register
 * as `null`; a `Z`/`B`/`C`/`S` slot keeps the register's low bits ([Coerce.narrowStore]).
 */
internal fun slotWrite(value: Value, slotType: VirtualType, readsAsReference: Boolean): Value =
    if (readsAsReference && value.readsAsNullReference) Value.Known(null, slotType) else Coerce.narrowStore(value, slotType)
