package simplify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RegisterContainer {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    class Register {
        private final List<Integer> used;
        private final List<Integer> referenced;

        private Object value;
        private String type;

        Register(Object value, String type) {
            used = new ArrayList<Integer>();
            referenced = new ArrayList<Integer>();
            this.value = value;
            this.type = type;
        }

        public void used(int line) {
            used.add(line);
        }

        public void referenced(int line) {
            referenced.add(line);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("value=").append(value).append(", type=").append(type).append("\n\tused=")
                            .append(Arrays.toString(used.toArray())).append("\n\treferenced=")
                            .append(Arrays.toString(referenced.toArray()));

            return sb.toString();
        }
    }

    private final Map<String, Register> registers;

    public RegisterContainer() {
        registers = new HashMap<String, Register>();
    }

    public void set(String name, Object value, String type, int line) {
        Register r = registers.get(name);
        if (r == null) {
            r = new Register(value, type);
            registers.put(name, r);
        }
        r.referenced(line);
        r.value = value;
    }

    public Object get(String name, int line) {
        Register r = registers.get(name);
        r.used(line);

        return r.value;
    }

    public List<Integer> getUsed(String name) {
        return registers.get(name).used;
    }

    public List<Integer> getReferenced(String name) {
        return registers.get(name).referenced;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (String name : registers.keySet()) {
            Register r = registers.get(name);
            sb.append(name).append(" -> ").append(r.toString()).append("\n");
        }

        return sb.toString();
    }

}
