package org.cf.smalivm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerImpl;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualMethod;
import org.jf.dexlib2.builder.BuilderExceptionHandler;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExceptionHandlerAddressResolverTest {

    private static final String EXCEPTION1 = "Ljava/lang/Exception";
    private static final String EXCEPTION2 = "Ljava/lang/RuntimeException;";
    private static final String EXCEPTION3 = "Ljava/lang/NullPointerException;";
    private static VirtualMethod METHOD;

    private List<BuilderTryBlock> tryBlocks;
    private ClassManager classManager;
    private VirtualClass exceptionClass1;
    private VirtualClass exceptionClass2;
    private VirtualClass exceptionClass3;

    private static BuilderExceptionHandler buildHandler(int codeAddress, VirtualClass exceptionType) {
        BuilderExceptionHandler handler = mock(BuilderExceptionHandler.class);
        String name = exceptionType.getName();
        when(handler.getExceptionType()).thenReturn(name);
        when(handler.getHandlerCodeAddress()).thenReturn(codeAddress);

        return handler;
    }

    private static BuilderTryBlock buildTryBlock(int startAddress, int codeUnits,
                                                 List<? extends BuilderExceptionHandler> handlers) {
        BuilderTryBlock tryBlock = mock(BuilderTryBlock.class);
        when(tryBlock.getStartCodeAddress()).thenReturn(startAddress);
        when(tryBlock.getCodeUnitCount()).thenReturn(codeUnits);
        doReturn(handlers).when(tryBlock).getExceptionHandlers();

        return tryBlock;
    }

    @BeforeEach
    public void setUp() {
        tryBlocks = new LinkedList<>();

        exceptionClass1 = mockException("Ljava/lang/Exception");
        exceptionClass2 = mockException("Ljava/lang/RuntimeException;");
        exceptionClass3 = mockException("Ljava/lang/NullPointerException;");
        when(exceptionClass1.getAncestors()).thenReturn(new HashSet<>(0));
        Set<VirtualClass> ancestors;
        ancestors = new HashSet<>(Collections.singletonList(exceptionClass1));
        when(exceptionClass2.getAncestors()).thenReturn(ancestors);
        ancestors = new HashSet<>(Collections.singletonList(exceptionClass2));
        when(exceptionClass3.getAncestors()).thenReturn(ancestors);

        classManager = mock(ClassManagerImpl.class);
        when(classManager.getVirtualClass(EXCEPTION1)).thenReturn(exceptionClass1);
        when(classManager.getVirtualClass(EXCEPTION2)).thenReturn(exceptionClass2);
        when(classManager.getVirtualClass(EXCEPTION3)).thenReturn(exceptionClass3);

        METHOD = mock(VirtualMethod.class);
        when(METHOD.getTryBlocks()).thenReturn(tryBlocks);
    }

    @Test
    public void overlappingTryBlocksWithMoreThanOneValidHandlerResolvesNearestAncestor() {
        int currentAddress = 2;
        int tryStartAddress1 = 1;
        int tryStartAddress2 = 2;
        int tryCodeUnits = 10;
        int handlerCodeAddress1 = 20;
        int handlerCodeAddress2 = 30;

        List<BuilderExceptionHandler> handlers1 = new LinkedList<BuilderExceptionHandler>();
        handlers1.add(buildHandler(handlerCodeAddress1, exceptionClass1));
        tryBlocks.add(buildTryBlock(tryStartAddress1, tryCodeUnits, handlers1));

        List<BuilderExceptionHandler> handlers2 = new LinkedList<BuilderExceptionHandler>();
        handlers2.add(buildHandler(handlerCodeAddress2, exceptionClass2));
        tryBlocks.add(buildTryBlock(tryStartAddress2, tryCodeUnits, handlers2));

        ExceptionHandlerAddressResolver exceptionResolver = new ExceptionHandlerAddressResolver(classManager, METHOD);
        int actual = exceptionResolver.resolve(EXCEPTION3, currentAddress);

        assertEquals(handlerCodeAddress2, actual);
    }

    @Test
    public void overlappingTryBlocksWithOneValidHandler() {
        int currentAddress = 2;
        int tryStartAddress1 = 1;
        int tryStartAddress2 = 2;
        int tryCodeUnits = 10;
        int handlerCodeAddress1 = 20;
        int handlerCodeAddress2 = 30;

        List<BuilderExceptionHandler> handlers1 = new LinkedList<BuilderExceptionHandler>();
        handlers1.add(buildHandler(handlerCodeAddress1, exceptionClass1));
        tryBlocks.add(buildTryBlock(tryStartAddress1, tryCodeUnits, handlers1));

        List<BuilderExceptionHandler> handlers2 = new LinkedList<BuilderExceptionHandler>();
        handlers2.add(buildHandler(handlerCodeAddress2, exceptionClass2));
        tryBlocks.add(buildTryBlock(tryStartAddress2, tryCodeUnits, handlers2));

        ExceptionHandlerAddressResolver exceptionResolver = new ExceptionHandlerAddressResolver(classManager, METHOD);
        String name = exceptionClass1.getName();
        int actual = exceptionResolver.resolve(name, currentAddress);

        assertEquals(handlerCodeAddress1, actual);
    }

    @Test
    public void simpleExceptionResolvedCorrectly() {
        int currentAddress = 1;
        int tryStartAddress = 1;
        int tryCodeUnits = 10;
        int handlerCodeAddress = 20;

        List<BuilderExceptionHandler> handlers = new LinkedList<BuilderExceptionHandler>();
        handlers.add(buildHandler(handlerCodeAddress, exceptionClass1));
        tryBlocks.add(buildTryBlock(tryStartAddress, tryCodeUnits, handlers));

        ExceptionHandlerAddressResolver exceptionResolver = new ExceptionHandlerAddressResolver(classManager, METHOD);
        String name = exceptionClass1.getName();
        int actual = exceptionResolver.resolve(name, currentAddress);

        assertEquals(handlerCodeAddress, actual);
    }

    private VirtualClass mockException(String className) {
        VirtualClass exceptionClass = mock(VirtualClass.class);
        when(exceptionClass.getName()).thenReturn(className);

        return exceptionClass;
    }

}
