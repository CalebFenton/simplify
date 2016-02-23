package org.cf.smalivm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cf.smalivm.reference.LocalMethod;
import org.cf.smalivm.smali.ClassManager;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.TryBlock;

public class ExceptionHandlerAddressResolver {

    private final ClassManager classManager;
    private final List<BuilderTryBlock> tryBlocks;

    ExceptionHandlerAddressResolver(ClassManager classManager, LocalMethod localMethod) {
        this.classManager = classManager;
        tryBlocks = localMethod.getTryBlocks();
    }

    int resolve(Exception ex, int address) {
        String exceptionClass = ClassNameUtils.binaryToInternal(ex.getClass().getName());

        return resolve(exceptionClass, address);
    }

    int resolve(VirtualException vex, int address) {
        return resolve(vex.getExceptionClass(), address);
    }

    private int resolve(String exceptionClass, int address) {
        Deque<String> classAncestors = new ArrayDeque<String>();
        classAncestors.add(exceptionClass);
        Set<String> visited = new HashSet<String>();

        String currentExceptionClass;
        while ((currentExceptionClass = classAncestors.poll()) != null) {
            for (TryBlock<? extends ExceptionHandler> tryBlock : tryBlocks) {
                if ((address < tryBlock.getStartCodeAddress()) || (address > (tryBlock.getStartCodeAddress() + tryBlock
                                .getCodeUnitCount()))) {
                    // address is not inside of this try/catch
                    continue;
                }

                List<? extends ExceptionHandler> handlers = tryBlock.getExceptionHandlers();
                for (ExceptionHandler handler : handlers) {
                    String handlerType = handler.getExceptionType();
                    if (currentExceptionClass.equals(handlerType)) {
                        int handlerAddress = handler.getHandlerCodeAddress();

                        return handlerAddress;
                    }
                }
            }

            visited.add(currentExceptionClass);
            Set<String> ancestors = classManager.getClassAncestors(currentExceptionClass);
            ancestors.removeAll(visited);
            classAncestors.addAll(ancestors);
        }

        // Not caught by anything. Look for a catch-all / finally.
        for (TryBlock<? extends ExceptionHandler> tryBlock : tryBlocks) {
            if ((address < tryBlock.getStartCodeAddress()) || (address > (tryBlock.getStartCodeAddress() + tryBlock
                            .getCodeUnitCount()))) {
                // address is not inside of this try/catch
                continue;
            }

            List<? extends ExceptionHandler> handlers = tryBlock.getExceptionHandlers();
            ExceptionHandler handler = handlers.get(handlers.size() - 1);
            if (null == handler.getExceptionType()) {
                // catch-all
                int handlerAddress = handler.getHandlerCodeAddress();

                return handlerAddress;
            }

        }

        return -1;
    }

}
