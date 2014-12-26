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

    private ClassState(ClassState parent, ExecutionContext childContext, THashSet<String> fieldNameAndTypes) {
        super(childContext, fieldNameAndTypes.size());

        this.fieldNameAndTypes = fieldNameAndTypes;
        this.className = parent.className;
    }

    public ClassState(ClassState other, ExecutionContext ectx) {
        super(other, ectx);

        fieldNameAndTypes = new THashSet<String>(other.fieldNameAndTypes);
        className = other.className;
    }

    public void assignField(String fieldNameAndType, Object value) {
        int register = 0;
        String heapKey = getKey(fieldNameAndType);
        String type = fieldNameAndType.split(":")[1];
        assignRegister(register, new HeapItem(value, type), heapKey);
    }

    public void assignField(String fieldNameAndType, HeapItem item) {
        int register = 0;
        String heapKey = getKey(fieldNameAndType);
        assignRegister(register, item, heapKey);
    }

    public boolean equals(ClassState other) {
        return this.toString().equals(other.toString());
    }

    public HeapItem peekField(String fieldNameAndType) {
        int register = 0;
        String heapKey = getKey(fieldNameAndType);

        return peekRegister(register, heapKey);
    }

    public void pokeField(String fieldNameAndType, Object value) {
        int register = 0;
        String heapKey = getKey(fieldNameAndType);
        String type = fieldNameAndType.split(":")[1];
        pokeRegister(register, new HeapItem(value, type), heapKey);
    }

    public void pokeField(String fieldNameAndType, HeapItem item) {
        int register = 0;
        String heapKey = getKey(fieldNameAndType);
        pokeRegister(register, item, heapKey);
    }

    private String getKey(String fieldNameAndType) {
        fieldNameAndTypes.add(fieldNameAndType);
        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(fieldNameAndType);

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Fields:\n");
        for (String fieldNameAndType : fieldNameAndTypes) {
            sb.append(fieldNameAndType).append(" = ").append(peekField(fieldNameAndType)).append('\n');
        }
        sb.setLength(sb.length() - 1);
        sb.append('\n');

        return sb.toString();
    }

    ClassState getChild(ExecutionContext childContext) {
        ClassState child = new ClassState(this, childContext, fieldNameAndTypes);

        return child;
    }

}
