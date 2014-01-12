package simplify.exec;

import java.util.HashSet;
import java.util.Set;

import com.rits.cloning.Cloner;

public class RegisterStore {
    private final Set<Integer> used;
    private final Set<Integer> referenced;
    private final String type;
    private Object value;

    public RegisterStore(String type, Object value) {
        this(type, value, new HashSet<Integer>(), new HashSet<Integer>());
    }

    public RegisterStore(String type, Object value, Set<Integer> used, Set<Integer> referenced) {
        this.type = type;
        this.setValue(value);
        this.used = used;
        this.referenced = referenced;
    }

    @Override
    public RegisterStore clone() {
        Cloner cloner = new Cloner();
        Object valueClone = cloner.shallowClone(getValue());
        // Object newValue = ValueUtils.tryToClone(type, value);
        RegisterStore myClone = new RegisterStore(getType(), valueClone, new HashSet<Integer>(getUsed()), new HashSet<Integer>(
                        getReferenced()));

        return myClone;
    }

    @Override
    public String toString() {
        return "type=" + getType() + ", value=" + getValue() + ", used=" + getUsed() + ", referenced=" + getReferenced();
    }

    public Set<Integer> getUsed() {
        return used;
    }

    public Set<Integer> getReferenced() {
        return referenced;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
