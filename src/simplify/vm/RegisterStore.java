package simplify.vm;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import com.rits.cloning.Cloner;

public class RegisterStore {

    private final TIntSet assigned;
    private final TIntSet read;
    private final String type;

    private Object value;

    public RegisterStore(String type, Object value) {
        this(type, value, new TIntHashSet(), new TIntHashSet());
    }

    RegisterStore(String type, Object value, TIntSet assigned, TIntSet read) {
        this.type = type;
        this.setValue(value);
        this.assigned = assigned;
        this.read = read;
    }

    RegisterStore(RegisterStore other) {
        Cloner cloner = new Cloner();
        Object valueClone = cloner.deepClone(other.getValue());
        TIntSet newAssigned = new TIntHashSet(other.getAssigned());
        TIntSet newRead = new TIntHashSet(other.getRead());

        this.type = other.type;
        this.setValue(valueClone);
        this.assigned = newAssigned;
        this.read = newRead;
    }

    @Override
    public String toString() {
        return "type=" + getType() + ", value=" + getValue() + ", assigned=" + getAssigned() + ", read=" + getRead();
    }

    public TIntSet getAssigned() {
        return assigned;
    }

    public TIntSet getRead() {
        return read;
    }

    public void addRead(int address) {
        read.add(address);
    }

    public void addAssigned(int address) {
        assigned.add(address);
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
