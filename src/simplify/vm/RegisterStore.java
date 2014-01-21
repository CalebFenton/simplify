package simplify.vm;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import com.rits.cloning.Cloner;

public class RegisterStore {

    private final TIntSet used;
    private final TIntSet referenced;
    private final String type;

    private Object value;

    public RegisterStore(String type, Object value) {
        this(type, value, new TIntHashSet(), new TIntHashSet());
    }

    RegisterStore(String type, Object value, TIntSet used, TIntSet referenced) {
        this.type = type;
        this.setValue(value);
        this.used = used;
        this.referenced = referenced;
    }

    RegisterStore(RegisterStore other) {
        Cloner cloner = new Cloner();
        Object valueClone = cloner.deepClone(other.getValue());
        TIntSet newUsed = new TIntHashSet(other.getUsed());
        TIntSet newReferenced = new TIntHashSet(other.getReferenced());

        this.type = other.type;
        this.setValue(valueClone);
        this.used = newUsed;
        this.referenced = newReferenced;
    }

    @Override
    public String toString() {
        return "type=" + getType() + ", value=" + getValue() + ", used=" + getUsed() + ", refs=" + getReferenced();
    }

    public TIntSet getUsed() {
        return used;
    }

    public TIntSet getReferenced() {
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
