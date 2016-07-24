package org.cf.smalivm;

import org.cf.smalivm.opcode.Op;
import org.cf.util.ClassNameUtils;

import java.lang.reflect.Constructor;

public class ExceptionFactory {

    private final VirtualMachine vm;

    ExceptionFactory(VirtualMachine vm) {
        this.vm = vm;
    }

    public Throwable build(Op op, String className) {
        return build(op, className, null);
    }

    public Throwable build(Op op, Class<? extends Throwable> exceptionClass) {
        return build(op, exceptionClass, null);
    }

    public Throwable build(Op op, Class<? extends Throwable> exceptionClass, String message) {
        try {
            Constructor<? extends Throwable> ctor = exceptionClass.getDeclaredConstructor(String.class);
            ctor.setAccessible(true);
            return ctor.newInstance(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Exception();
    }

    public Throwable build(Op op, String className, String message) {
        String binaryName = ClassNameUtils.internalToBinary(className);
        try {
            @SuppressWarnings("unchecked")
            Class<Throwable> exceptionClass = (Class<Throwable>) vm.getClassLoader().loadClass(binaryName);
            return build(op, exceptionClass, message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Exception();
    }
}
