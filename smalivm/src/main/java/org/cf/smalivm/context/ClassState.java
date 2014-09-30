package org.cf.smalivm.context;

import gnu.trove.set.hash.THashSet;

public class ClassState extends BaseState {

    private final String className;
    private final THashSet<String> fieldNameAndTypes;

    public ClassState(ExecutionContext ectx, String className, int fieldCount) {
        super(ectx, fieldCount);

        fieldNameAndTypes = new THashSet<String>();
        this.className = className;
    }

    public ClassState(ClassState parent, ExecutionContext ectx) {
        super(parent, ectx);

        fieldNameAndTypes = new THashSet<String>(parent.fieldNameAndTypes);
        className = parent.className;
    }

    public void assignField(String fieldNameAndType, Object value) {
        int register = 0;
        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(fieldNameAndType);
        assignRegister(register, value, sb.toString());
    }

    public boolean equals(ClassState other) {
        return this.toString().equals(other.toString());
    }

    public Object peekField(String fieldNameAndType) {
        // int register = getRegister(fieldNameAndType);
        int register = 0;
        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(fieldNameAndType);
        return peekRegister(register, sb.toString());
    }

    public void pokeField(String fieldNameAndType, Object value) {
        int register = 0;
        fieldNameAndTypes.add(fieldNameAndType);
        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(fieldNameAndType);
        pokeRegister(register, value, sb.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Fields:\n");
        for (String fieldNameAndType : fieldNameAndTypes) {
            sb.append(fieldNameAndType).append(" = ").append(peekField(fieldNameAndType)).append("\n");
        }
        sb.setLength(sb.length() - 1);
        sb.append("\n");

        return sb.toString();
    }

    ClassState getChild(ExecutionContext childContext) {
        ClassState child = new ClassState(childContext, className, fieldNameAndTypes.size());

        return child;
    }

}
