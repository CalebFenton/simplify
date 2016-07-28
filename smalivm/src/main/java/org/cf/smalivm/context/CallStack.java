package org.cf.smalivm.context;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;

import java.util.LinkedList;
import java.util.List;

public class CallStack {

    private List<String> methodStack;
    private TIntList addressStack;

    CallStack() {
        methodStack = new LinkedList<>();
        addressStack = new TIntLinkedList();
    }

    CallStack(String methodDescriptor, int address) {
        this();
        push(methodDescriptor, address);
    }

    CallStack(CallStack callerStack) {
        this.methodStack = new LinkedList<>(callerStack.methodStack);
        this.addressStack = new TIntLinkedList(callerStack.addressStack);
    }

    void push(String methodDescriptor, int address) {
        methodStack.add(methodDescriptor);
        addressStack.add(address);
    }

    String peekMethodDescriptor() {
        return methodStack.get(size() - 1);
    }

    int size() {
        return methodStack.size();
    }

    void pokeAddress(int address) {
        addressStack.set(size() - 1, address);
    }

}
