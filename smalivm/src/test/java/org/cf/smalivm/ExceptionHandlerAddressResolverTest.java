package org.cf.smalivm;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.jf.dexlib2.builder.BuilderExceptionHandler;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.junit.Before;
import org.junit.Test;

public class ExceptionHandlerAddressResolverTest {

    private static final String EXCEPTION1 = "Ljava/lang/Exception";
    private static final String EXCEPTION2 = "Ljava/lang/RuntimeException;";
    private static final String EXCEPTION3 = "Ljava/lang/NullPointerException;";
    private static final String METHOD = "someMethod";

    private List<BuilderTryBlock> tryBlocks;
    private ClassManager classManager;
    private VirtualException vex;

    @Before
    public void setUp() {
        vex = mock(VirtualException.class);
        tryBlocks = new LinkedList<BuilderTryBlock>();
        classManager = mock(ClassManager.class);
        when(classManager.getClassAncestors(EXCEPTION3)).thenReturn(new HashSet<String>(Arrays.asList(EXCEPTION2)));
        when(classManager.getClassAncestors(EXCEPTION2)).thenReturn(new HashSet<String>(Arrays.asList(EXCEPTION1)));
        when(classManager.getClassAncestors(EXCEPTION1)).thenReturn(new HashSet<String>());
        when(classManager.getTryBlocks(METHOD)).thenReturn(tryBlocks);
    }

    private static BuilderTryBlock buildTryBlock(int startAddress, int codeUnits, List handlers) {
        BuilderTryBlock tryBlock = mock(BuilderTryBlock.class);
        when(tryBlock.getStartCodeAddress()).thenReturn(0);
        when(tryBlock.getCodeUnitCount()).thenReturn(10);
        when(tryBlock.getExceptionHandlers()).thenReturn(handlers);

        return tryBlock;
    }

    private static BuilderExceptionHandler buildHandler(int codeAddress, String exceptionType) {
        BuilderExceptionHandler handler = mock(BuilderExceptionHandler.class);
        when(handler.getExceptionType()).thenReturn(exceptionType);
        when(handler.getHandlerCodeAddress()).thenReturn(codeAddress);

        return handler;
    }

    @Test
    public void testSimpleExceptionResolvedCorrectly() {
        int currentAddress = 1;
        int tryStartAddress = 1;
        int tryCodeUnits = 10;
        int handlerCodeAddress = 20;

        when(vex.getExceptionClass()).thenReturn(EXCEPTION1);

        List handlers = new LinkedList<BuilderExceptionHandler>();
        handlers.add(buildHandler(handlerCodeAddress, EXCEPTION1));
        tryBlocks.add(buildTryBlock(tryStartAddress, tryCodeUnits, handlers));

        ExceptionHandlerAddressResolver exceptionResolver = new ExceptionHandlerAddressResolver(classManager, METHOD);
        int actual = exceptionResolver.resolve(vex, currentAddress);

        assertEquals(handlerCodeAddress, actual);
    }

    @Test
    public void testOverlappingTryBlocksWithOneValidHandler() {
        int currentAddress = 2;
        int tryStartAddress1 = 1;
        int tryStartAddress2 = 2;
        int tryCodeUnits = 10;
        int handlerCodeAddress1 = 20;
        int handlerCodeAddress2 = 30;

        when(vex.getExceptionClass()).thenReturn(EXCEPTION1);

        List handlers1 = new LinkedList<BuilderExceptionHandler>();
        handlers1.add(buildHandler(handlerCodeAddress1, EXCEPTION1));
        tryBlocks.add(buildTryBlock(tryStartAddress1, tryCodeUnits, handlers1));

        List handlers2 = new LinkedList<BuilderExceptionHandler>();
        handlers2.add(buildHandler(handlerCodeAddress2, EXCEPTION2));
        tryBlocks.add(buildTryBlock(tryStartAddress2, tryCodeUnits, handlers2));

        ExceptionHandlerAddressResolver exceptionResolver = new ExceptionHandlerAddressResolver(classManager, METHOD);
        int actual = exceptionResolver.resolve(vex, currentAddress);

        assertEquals(handlerCodeAddress1, actual);
    }

    @Test
    public void testOverlappingTryBlocksWithMoreThanOneValidHandlerResolvesNearestAncestor() {
        int currentAddress = 2;
        int tryStartAddress1 = 1;
        int tryStartAddress2 = 2;
        int tryCodeUnits = 10;
        int handlerCodeAddress1 = 20;
        int handlerCodeAddress2 = 30;

        when(vex.getExceptionClass()).thenReturn(EXCEPTION3);

        List handlers1 = new LinkedList<BuilderExceptionHandler>();
        handlers1.add(buildHandler(handlerCodeAddress1, EXCEPTION1));
        tryBlocks.add(buildTryBlock(tryStartAddress1, tryCodeUnits, handlers1));

        List handlers2 = new LinkedList<BuilderExceptionHandler>();
        handlers2.add(buildHandler(handlerCodeAddress2, EXCEPTION2));
        tryBlocks.add(buildTryBlock(tryStartAddress2, tryCodeUnits, handlers2));

        ExceptionHandlerAddressResolver exceptionResolver = new ExceptionHandlerAddressResolver(classManager, METHOD);
        int actual = exceptionResolver.resolve(vex, currentAddress);

        assertEquals(handlerCodeAddress2, actual);
    }

}
