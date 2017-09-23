package com.github.svetlinzarev.playground.reflection;


import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DeepLoggingHandler extends AbstractAroundInvokeHandler {
    private static final Logger logger = Logger.getLogger(DeepLoggingHandler.class.getName());
    private static final String TAG_NULL = "[null]";
    private static final String TAG_BEFORE = "[BEFORE]";
    private static final String TAG_AFTER = "[AFTER]";
    private static final AsStringPrinter<Object> genericPrinter;
    private static final AsStringPrinter<Type> typePrinter;

    private interface AsStringPrinter<T> {
        String asString(T object);
    }

    static {
        genericPrinter = o -> o == null ? TAG_NULL : o.toString();
        typePrinter = o -> o == null ? TAG_NULL : o.getTypeName();
    }


    private final Object target;
    private final StringBuilder message;
    private final Map<Method, Class<?>> resolvedReturnTypes;


    public DeepLoggingHandler(Object target) {
        this.target = Objects.requireNonNull(target);
        this.message = new StringBuilder(128);
        this.resolvedReturnTypes = new HashMap<>();
    }

    @Override
    protected void before(Method method, Object[] args) {
        if (logger.isLoggable(Level.FINEST)) {
            message.setLength(0);
            message.append(TAG_BEFORE)
              .append(' ')
              .append(method.getDeclaringClass().getName())
              .append('.')
              .append(method.getName())
              .append('(');
            appendArrayTo(message, args, genericPrinter);
            message.append(')');

            final String msg = message.toString();
            logger.log(Level.FINEST, msg);
        }
    }

    @Override
    protected Object invokeInternal(Object proxy, Method method, Object[] args) throws Throwable {
        final Object result = method.invoke(target, args);
        final Class returnType = resolveActualReturnType(method);
        return proxy(result, returnType);
    }

    private Class<?> resolveActualReturnType(Method method) throws NoSuchMethodException {
        final Class<?> actualReturnType = resolvedReturnTypes.computeIfAbsent(method, m -> {
            final Class<?> proxiedClassReturnType = m.getReturnType();

            if (proxiedClassReturnType.equals(Void.TYPE)) {
                return proxiedClassReturnType;
            }

            //Due to type erasure, if the return type is generic then it will be object
            if (!proxiedClassReturnType.equals(Object.class)) {
                return proxiedClassReturnType;
            }

            //Try to resolve the actual type in case the interface return type is generic
            try {
                final String methodName = m.getName();
                final Class<?>[] parameterTypes = m.getParameterTypes();
                final Method actualMethod = target.getClass().getMethod(methodName, parameterTypes);
                return actualMethod.getReturnType();
            } catch (NoSuchMethodException ex) {
                //Should never happen: The target class implements the method from the proxied interface
                return proxiedClassReturnType;
            }
        });

        return actualReturnType;
    }

    @Override
    protected void after(Method method, Object[] args, Object result, Exception ex) {
        if (logger.isLoggable(Level.FINEST)) {
            message.setLength(0);
            message.append(TAG_AFTER)
              .append(' ')
              .append(method.getDeclaringClass().getName())
              .append('.')
              .append(method.getName())
              .append('(');
            appendArrayTo(message, method.getParameterTypes(), typePrinter);
            message.append(')');

            final String msg = message.toString();
            logger.log(Level.FINEST, msg);
        }
    }

    private <P, A extends P> void appendArrayTo(StringBuilder builder, A[] array, AsStringPrinter<P> printer) {
        if (null == array || array.length == 0) {
            return;
        }

        final int maxIndex = array.length - 1;
        for (int i = 0; i <= maxIndex; i++) {
            final String asString = printer.asString(array[i]);
            builder.append(asString);
            if (i < maxIndex) {
                builder.append(',').append(' ');
            }
        }
    }

    public static <T> T proxy(T instance, Class<T> iface) {
        if (null == instance || !iface.isInterface()) {
            return instance;
        }

        final ClassLoader classLoader = getClassLoader(iface);
        return (T) proxy(instance, new Class<?>[]{iface}, classLoader);
    }

    public static Object proxy(Object instance, Class<?>[] ifaces, ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, ifaces, new DeepLoggingHandler(instance));
    }

    private static ClassLoader getClassLoader(Class<?> iface) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (null == classLoader) {
            classLoader = iface.getClassLoader();
            if (null == classLoader) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }
        return classLoader;
    }
}
