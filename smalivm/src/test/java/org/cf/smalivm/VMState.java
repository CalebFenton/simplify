package org.cf.smalivm;

import org.cf.smalivm.context.HeapItem;

import java.util.HashMap;
import java.util.Map;

public class VMState {

    private final Map<String, Map<String, HeapItem>> classNameToFieldDescriptorToItem;
    private final Map<Integer, HeapItem> registerToItem;

    public VMState() {
        classNameToFieldDescriptorToItem = new HashMap<String, Map<String, HeapItem>>();
        registerToItem = new HashMap<Integer, HeapItem>();
    }

    public HeapItem getField(String className, String fieldName) {
        return classNameToFieldDescriptorToItem.get(className).get(fieldName);
    }

    public Map<String, Map<String, HeapItem>> getFields() {
        return classNameToFieldDescriptorToItem;
    }

    public HeapItem getRegister(Integer register) {
        return registerToItem.get(register);
    }

    public Map<Integer, HeapItem> getRegisters() {
        return registerToItem;
    }

    public void setRegisters(Object... params) {
        // register, value, type
        for (int i = 0; i < params.length; i += 3) {
            Integer register = (Integer) params[i];
            String type = (String) params[i + 2];
            HeapItem item = new HeapItem(params[i + 1], type);
            setRegister(register, item);
        }
    }

    public void setField(String className, String fieldDescriptor, HeapItem item) {
        if (!classNameToFieldDescriptorToItem.containsKey(className)) {
            classNameToFieldDescriptorToItem.put(className, new HashMap<String, HeapItem>());
        }
        classNameToFieldDescriptorToItem.get(className).put(fieldDescriptor, item);
    }

    public void setFields(String className, Object... params) {
        for (int i = 0; i < params.length; i += 2) {
            String fieldDescriptor = (String) params[i];
            String type = fieldDescriptor.split(":")[1];
            HeapItem item = new HeapItem(params[i + 1], type);
            setField(className, fieldDescriptor, item);
        }
    }

    public void setRegister(Integer register, HeapItem item) {
        registerToItem.put(register, item);
    }

}
