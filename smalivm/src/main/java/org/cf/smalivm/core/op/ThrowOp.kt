package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.ThrowEvidence

/**
 * Throws the register's value, carried as it is so its identity survives to the handler. The session
 * normalizes it: a value that reads as a null reference becomes a fresh message-less
 * `NullPointerException` ([JVMS] §6.5 `athrow`; ART's `THROW()`), and a non-`Throwable` reference becomes an
 * `InternalError`. Handler routing belongs to the session.
 */
internal class ThrowOp(address: Int, private val register: Int) : Op(address) {
    override val name: String = "throw"
    override val registersRead: Set<Int> = setOf(register)

    override fun execute(frame: Frame): OpExecution = OpExecution.single(
        OpArm.Throw(evidence = ThrowEvidence.Existing(frame.readFrameValue(register))),
    )
}
