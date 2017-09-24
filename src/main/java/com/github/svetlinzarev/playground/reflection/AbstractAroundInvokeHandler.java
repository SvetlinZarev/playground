package com.github.svetlinzarev.playground.reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractAroundInvokeHandler implements InvocationHandler {
    private static final Logger logger = Logger.getLogger(AbstractAroundInvokeHandler.class.getName());

    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            before(method, args);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Failed to execute around-invoke 'before' action: ", ex);
        }

        Throwable exception = null;
        Object result = null;
        try {
            result = invokeInternal(proxy, method, args);
        } catch (InvocationTargetException ex) {
            final Throwable cause = ex.getCause();
            if(null != cause){
                exception = cause;
            }else {
                exception = ex;
            }
        } catch (Exception ex) {
            exception = ex;
        } finally {
            try {
                after(method, args, result, exception);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Failed to execute around-invoke 'after' action: ", ex);
            }
        }

        if (null != exception) {
            throw exception;
        }

        return result;
    }


    protected abstract void before(Method method, Object[] args);

    protected abstract Object invokeInternal(Object proxy, Method method, Object[] args) throws Throwable;

    protected abstract void after(Method method, Object[] args, Object result, Throwable ex);
}
