package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilledNewArrayOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(FilledNewArrayOp.class.getSimpleName());

    private final int dimensionRegisters[];
    private final String typeReference;

    FilledNewArrayOp(MethodLocation location, MethodLocation child, int[] dimensionRegisters, String typeReference) {
        super(location, child);

        this.dimensionRegisters = dimensionRegisters;
        this.typeReference = typeReference;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        /*
         * This populates a 1-dimensional integer array with values from the parameters. It does NOT create
         * n-dimensional arrays. It's usually used to create parameter for Arrays.newInstance(). If you use anything but
         * [I as the virtual parse, the code fails verification and a few decompilers (not disassemblers) choke.
         */
        int[] dimensions = new int[dimensionRegisters.length];
        boolean foundUnknown = false;
        for (int i = 0; i < dimensionRegisters.length; i++) {
            int register = dimensionRegisters[i];
            HeapItem item = mState.readRegister(register);

            if (foundUnknown) {
                continue;
            }

            Object value = item.getValue();
            if (value instanceof Number) {
                dimensions[i] = ((Number) value).intValue();
            } else {
                if (!item.isUnknown()) {
                    if (log.isWarnEnabled()) {
                        log.warn("Unexpected value virtual for {}: {}", toString(), item);
                    }
                }

                // At least one value is unknown. Give up.
                foundUnknown = true;
            }
        }

        if (foundUnknown) {
            mState.assignResultRegister(HeapItem.newUnknown("[I"));
        } else {
            mState.assignResultRegister(dimensions, "[I");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" {");
        if (dimensionRegisters.length > 5) {
            sb.append('r').append(dimensionRegisters[0]).append(" .. r")
                    .append(dimensionRegisters[dimensionRegisters.length - 1]);
        } else {
            for (int dimensionRegister : dimensionRegisters) {
                sb.append('r').append(dimensionRegister).append(", ");
            }
            sb.setLength(sb.length() - 2);
        }
        sb.append("}, ").append(typeReference);

        return sb.toString();
    }

}
