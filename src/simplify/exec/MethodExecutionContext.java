package simplify.exec;

import java.util.logging.Logger;

import org.jf.util.SparseArray;

import simplify.Main;

public class MethodExecutionContext {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    // TODO: argument object references should be updated with final states
    // if final states all have same value for some reference, update register store with new value
    // if not, update register store value with unknown value.
    // can use a map of original object and maintain it every clone
    private final SparseArray<RegisterStore> registers;
    private final int registerCount;
    private final int parameterCount;
    private final int remainingCallDepth;
    private RegisterStore returnRegister;
    private RegisterStore resultRegister;

    public MethodExecutionContext(int registerCount, int parameterCount, int remainingCallDepth) {
        registers = new SparseArray<RegisterStore>(registerCount);
        this.registerCount = registerCount;
        this.parameterCount = parameterCount;
        this.remainingCallDepth = remainingCallDepth;
    }

    public void addParameterRegister(int parameterIndex, RegisterStore rs) {
        addRegister(getParameterStart() + parameterIndex, rs);
    }

    public void addParameterRegister(int parameterIndex, String type, Object value) {
        RegisterStore current = new RegisterStore(type, value);
        addRegister(getParameterStart() + parameterIndex, current);
    }

    public void addRegister(int register, RegisterStore rs) {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        // for (int i = 2; i < ste.length; i++) {
        // sb.append("\n\t").append(ste[i]);
        // }
        log.finer("Adding register @" + register + " rs:" + rs + sb.toString());

        registers.put(register, rs);
    }

    public void addRegister(int register, String type, Object value, int index) {
        RegisterStore current = new RegisterStore(type, value);
        current.getReferenced().add(index);
        addRegister(register, current);
    }

    @Override
    public MethodExecutionContext clone() {
        MethodExecutionContext myClone = new MethodExecutionContext(this.registerCount, this.parameterCount,
                        this.remainingCallDepth);

        for (int i = 0; i < registerCount; i++) {
            RegisterStore rs = registers.get(i);
            if (rs == null) {
                continue;
            }

            if (myClone.registers.get(i) != null) {
                // Already cloned this one because it shared object reference with an other register.
                continue;
            }

            /*
             * Some registers may share the same object reference so this relationship must persist in clones. The
             * relationship will not persist after the children of an initial branch, however, but this won't be a
             * problem because each will be self-consistent and since each represents a possible execution path, they
             * shouldn't need to examine each other except at optimization.
             */
            RegisterStore rsClone = rs.clone();
            for (int j = 0; j < registerCount; j++) {
                if (j == i) {
                    continue;
                }

                RegisterStore possibleMatch = registers.get(j);
                if (possibleMatch == null) {
                    continue;
                }

                if (rs.getValue() == possibleMatch.getValue()) {
                    log.finest("context clone, r" + j + " == r" + i);
                    myClone.registers.put(j, rsClone);
                }
            }

            myClone.registers.put(i, rsClone);
        }

        if (resultRegister != null) {
            myClone.resultRegister = this.resultRegister.clone();
        }

        if (returnRegister != null) {
            myClone.returnRegister = this.returnRegister.clone();
        }

        return myClone;
    }

    public int getParameterStart() {
        return registerCount - parameterCount;
    }

    public RegisterStore getRegister(int register, int index) {
        RegisterStore rs = peekRegister(register);

        /*
         * Indexed access to a register implies it's being "read" by some instruction. Mark it as used at this
         * instruction index so the nop code optimizer knows not to remove this line.
         */
        rs.getReferenced().add(index);

        return rs;
    }

    public Object getRegisterValue(int register, int index) {
        // Convenience for those who don't care about the type
        return getRegister(register, index).getValue();
    }

    public int getRegisterCount() {
        return registerCount;
    }

    public int getRemaingCallDepth() {
        return remainingCallDepth;
    }

    public RegisterStore getResultRegister() {
        RegisterStore result = resultRegister;
        resultRegister = null;

        return result;
    }

    public RegisterStore getReturnRegister() {
        return returnRegister;
    }

    public RegisterStore peekRegister(int register) {
        RegisterStore rs = registers.get(register);

        if (rs == null) {
            log.warning("r" + register + " is being read but is null, likely a mistake!\n" + this);
        }

        return rs;
    }

    public String peekRegisterType(int register) {
        RegisterStore rs = peekRegister(register);

        return rs.getType();
    }

    public Object peekRegisterValue(int register) {
        RegisterStore rs = peekRegister(register);

        return rs.getValue();
    }

    public void setResultRegister(RegisterStore rs) {
        resultRegister = new RegisterStore(rs.getType(), rs.getValue());
    }

    public void setReturnRegister(int register, int index) {
        // Use get rather than peek because this counts as a usage.
        returnRegister = getRegister(register, index);
    }

    public void setReturnRegister(RegisterStore rs) {
        // Emulated methods maintain registers outside of a context (can't reference a register)
        returnRegister = rs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("registers: ").append(registerCount).append(", parameters: ").append(parameterCount)
                        .append(", remaing call depth: ").append(remainingCallDepth).append("\n");

        for (int i = 0; i < this.registerCount; i++) {
            RegisterStore rs = registers.get(i);
            if (rs == null) {
                continue;
            }

            sb.append("[").append("r").append(i).append(": ").append(rs).append(",\n");
        }

        if (resultRegister != null) {
            sb.append("result").append(": ").append(resultRegister).append("\n");
        }

        if (returnRegister != null) {
            sb.append("return").append(": ").append(returnRegister).append("\n");
        }

        return sb.deleteCharAt(sb.length() - 1).toString();
    }

}
