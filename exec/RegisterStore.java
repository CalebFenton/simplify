package simplify.exec;

import java.util.HashSet;
import java.util.Set;

import com.rits.cloning.Cloner;

class RegisterStore {
    public final Set<Integer> used;
    public final Set<Integer> referenced;
    public final String type;
    public Object value;

    public RegisterStore(String type, Object value) {
        this(type, value, new HashSet<Integer>(), new HashSet<Integer>());
    }

    public RegisterStore(String type, Object value, Set<Integer> used, Set<Integer> referenced) {
        this.type = type;
        this.value = value;
        this.used = used;
        this.referenced = referenced;
    }

    @Override
    public RegisterStore clone() {
        Cloner cloner = new Cloner();
        Object valueClone = cloner.shallowClone(value);
        // Object newValue = ValueUtils.tryToClone(type, value);
        RegisterStore myClone = new RegisterStore(type, valueClone, new HashSet<Integer>(used), new HashSet<Integer>(
                        referenced));

        return myClone;
    }

    @Override
    public String toString() {
        return "type=" + type + ", value=" + value.toString() + ", used=" + used + ", referenced=" + referenced;
    }
}
