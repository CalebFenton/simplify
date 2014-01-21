package simplify.vm;

public class UnknownValue {

    public UnknownValue() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public UnknownValue clone() {
        return new UnknownValue();
    }

    @Override
    public String toString() {
        return "*unknown*";
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }
}
