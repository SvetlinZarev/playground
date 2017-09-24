package com.github.svetlinzarev.playground.reflection;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AbstractAroundInvokeHandlerTest {
    private AbstractAroundInvokeHandler handler;

    @BeforeEach
    public void beforeEach() {
        handler = spy(AbstractAroundInvokeHandler.class);
    }

    @Test
    public void testBeforeDoesNotFailTheExecution() throws Throwable {
        doThrow(RuntimeException.class).when(handler).before(any(), any());
        createProxyAndExecute();
        verifyCallbacksNoTargetInstanceException();
    }

    @Test
    public void testAfterDoesNotFailTheExecution() throws Throwable {
        doThrow(RuntimeException.class).when(handler).after(any(), any(), any(), any());
        createProxyAndExecute();
        verifyCallbacksNoTargetInstanceException();
    }

    @Test
    public void testAfterIsCalledEvenInCaseOfException() throws Throwable {
        doThrow(RuntimeException.class).when(handler).invokeInternal(any(), any(), any());
        assertThrows(Exception.class, () -> createProxyAndExecute());
        verify(handler).before(any(), any());
        verify(handler).invokeInternal(any(), any(), any());
        verify(handler).after(any(), any(), isNull(), isA(RuntimeException.class));
    }

    @Test
    public void testInvokeInternalPropagatesCorrectException() throws Throwable {
        final String exceptionMessage = "Expected from test";
        doThrow(new Exception(exceptionMessage)).when(handler).invokeInternal(any(), any(), any());

        final Exception receivedException = assertThrows(Exception.class, () -> createProxyAndExecute());
        assertEquals(exceptionMessage, receivedException.getMessage());
        assertEquals(Exception.class, receivedException.getClass());
    }

    @Test
    public void testInvokeInternalPropagatesCorrectExceptionInCaseOfInvocationTargetException() throws Throwable {
        final String exceptionMessage = "Expected from test";
        doThrow(new InvocationTargetException(new RuntimeException(exceptionMessage))).when(handler).invokeInternal(any(), any(), any());

        final RuntimeException receivedException = assertThrows(RuntimeException.class, () -> createProxyAndExecute());
        assertEquals(exceptionMessage, receivedException.getMessage());
        assertEquals(RuntimeException.class, receivedException.getClass());
    }

    private void createProxyAndExecute() throws Exception {
        final Callable proxyInstance = (Callable) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{Callable.class}, handler);
        proxyInstance.call();
    }

    private void verifyCallbacksNoTargetInstanceException() throws Throwable {
        verify(handler).before(any(), any());
        verify(handler).invokeInternal(any(), any(), any());
        verify(handler).after(any(), any(), isNull(), isNull());
    }
}
