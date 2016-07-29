package org.cf.smalivm;

import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.TryBlock;

import java.util.LinkedList;
import java.util.List;

public class ExceptionHandlerAddressResolver {

    private final ClassManager classManager;
    private final List<BuilderTryBlock> tryBlocks;

    ExceptionHandlerAddressResolver(ClassManager classManager, VirtualMethod localMethod) {
        this.classManager = classManager;
        tryBlocks = localMethod.getTryBlocks();
    }

    int resolve(String className, int address) {
        VirtualClass exceptionClass = classManager.getVirtualClass(className);

        return resolve(exceptionClass, address);
    }

    int resolve(Throwable ex, int address) {
        String className = ClassNameUtils.binaryToInternal(ex.getClass().getName());

        return resolve(className, address);
    }

    int resolve(VirtualClass exceptionClass, int address) {
        int handlerAddress = findHandlerCodeAddress(exceptionClass, address, false);
        if (handlerAddress != -1) {
            return handlerAddress;
        }

        handlerAddress = findHandlerCodeAddress(exceptionClass, address, true);
        return handlerAddress;
    }

    /*
     * Given an exception type and an address in code, this will try and find the correct exception handler. This is
     * complicated by the fact that handlers may overlap. There may be 6 overlapping exception handlers over one
     * particular address each catching different exception types with one handler as a catch-all.
     * This will try to find the most "immediate" exception handler in terms of class hierarchy.  If a handler for
     * the exact exception type can't be found, it'll try to find one for the exception type's parent, parent's
     * parent, and so on. If catchAll is true, it'll look for catch-all exception handlers.
     */
    private int findHandlerCodeAddress(VirtualClass exceptionClass, int address, boolean catchAll) {
        List<VirtualClass> lineage = new LinkedList<>();
        lineage.add(exceptionClass);
        lineage.addAll(exceptionClass.getAncestors());

        for (VirtualClass currentExceptionClass : lineage) {
            String className = currentExceptionClass.getName();
            for (TryBlock<? extends ExceptionHandler> tryBlock : tryBlocks) {
                int startAddress = tryBlock.getStartCodeAddress();
                int endAddress = tryBlock.getStartCodeAddress() + tryBlock.getCodeUnitCount();
                boolean inTryBlock = (address >= startAddress && address < endAddress);
                if (!inTryBlock) {
                    continue;
                }

                List<? extends ExceptionHandler> handlers = tryBlock.getExceptionHandlers();
                if (catchAll) {
                    // If it's the last handler and it's null, it's a catchall
                    ExceptionHandler handler = handlers.get(handlers.size() - 1);
                    if (null == handler.getExceptionType()) {
                        return handler.getHandlerCodeAddress();
                    }
                } else {
                    for (ExceptionHandler handler : handlers) {
                        String handlerType = handler.getExceptionType();
                        if (className.equals(handlerType)) {
                            return handler.getHandlerCodeAddress();
                        }
                    }
                }
            }

            if (catchAll) {
                // This loop is unnecessary for catch-all because it doesn't look at the exception class.
                return -1;
            }
        }

        return -1;
    }

}
