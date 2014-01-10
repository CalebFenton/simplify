package simplify.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import simplify.Simplifier;
import simplify.ValueUtils;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class ExecutionContext {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    class RegisterValue {
        public final List<Integer> used;
        public final List<Integer> declared;
        public final String type;
        public final Object value;

        public RegisterValue(String type, Object value) {
            this(type, value, new ArrayList<Integer>(), new ArrayList<Integer>());
        }

        public RegisterValue(String type, Object value, List<Integer> used, List<Integer> declared) {
            this.type = type;
            this.value = value;
            this.used = used;
            this.declared = declared;
        }

        @Override
        public RegisterValue clone() {
            Object newValue = ValueUtils.tryToClone(type, value);
            RegisterValue myClone = new RegisterValue(type, newValue, new ArrayList<Integer>(used),
                            new ArrayList<Integer>(declared));

            return myClone;
        }
    }

    private final Multimap<Integer, RegisterValue> registers;

    public ExecutionContext() {
        registers = LinkedHashMultimap.create();
    }

    public void addRegister(int register, String type, Object value) {
        registers.put(register, new RegisterValue(type, value));
    }

    private void addRegister(int register, RegisterValue value) {
        registers.put(register, value);
    }

    @Override
    public ExecutionContext clone() {
        ExecutionContext myClone = new ExecutionContext();
        for (Integer register : registers.keySet()) {
            for (RegisterValue value : registers.get(register)) {
                myClone.addRegister(register, value.clone());
            }
        }

        return myClone;
    }

}
