package com.github.svetlinzarev.playground.reflection;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.*;
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


    private final ClassLoader classLoader;
    private final Object target;
    private final Map<Method, Class<?>> resolvedReturnTypes;
    private final StringBuilder message;

    private long executionTimeNanos;


    private DeepLoggingHandler(Object target, ClassLoader classLoader) {
        this.target = Objects.requireNonNull(target);
        this.message = new StringBuilder(128);
        this.resolvedReturnTypes = new HashMap<>();
        this.classLoader = classLoader;
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
        final Object result = invokeAndMeasureExecutionTime(method, args);

        try {
            final Class returnType = resolveActualReturnType(method);
            return tryToProxy(result, returnType, classLoader);
        } catch (ReflectiveOperationException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Failed to proxy the result object: ", ex);
            return result;
        }
    }

    private Object invokeAndMeasureExecutionTime(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        final long startTime = System.nanoTime();
        try {
            return method.invoke(target, args);
        } finally {
            executionTimeNanos = System.nanoTime() - startTime;
        }
    }

    private Class<?> resolveActualReturnType(Method method) throws NoSuchMethodException {
        final Class<?> resolvedReturnType = resolvedReturnTypes.computeIfAbsent(method, m -> {
            final Class<?> proxiedClassReturnType = m.getReturnType();

            if (proxiedClassReturnType.equals(Void.TYPE)) {
                return proxiedClassReturnType;
            }

            //Due to type erasure, if the return type is generic then it will be object
            if (!proxiedClassReturnType.equals(Object.class)) {

                //The return type is not generic, but still might be an interface
                if (!proxiedClassReturnType.isInterface()) {
                    return proxiedClassReturnType;
                }
            }

            /*
             * Due to type erasure if the return type is generic, it will appear as  'java.lang.Object'
             * in the proxy method. Also the return type might be an interface that extends the one from
             * the super interface. So in those cases we have to check the actual return type from the
             * implementing class.
             */
            try {
                final String methodName = m.getName();
                final Class<?>[] parameterTypes = m.getParameterTypes();
                final Method actualMethod = target.getClass().getMethod(methodName, parameterTypes);
                final Class<?> actualMethodReturnType = actualMethod.getReturnType();
                if (actualMethodReturnType.isInterface()) {
                    return actualMethodReturnType;
                }

                /*
                 * The actual method return type is not an interface, so we'll not be able to proxy it.
                 * The proxy method return type is either object, or an interface, so return it instead.
                 */
                return proxiedClassReturnType;
            } catch (NoSuchMethodException ex) {
                //Should never happen because the target class implements the method from the proxied interface
                logger.log(Level.SEVERE, "Should never happen: ", ex);
                return proxiedClassReturnType;
            }
        });

        return resolvedReturnType;
    }

    private static Object tryToProxy(Object instance, Class<?> returnType, ClassLoader classLoader) {
        if (null == instance) {
            return null;
        }

        if (!returnType.isInterface()) {
            return instance;
        }

        final Class<?>[] interfaces = collectAllImplementedInterfaces(instance);

         /*
         * Note: asserts are not executed during "normal" execution
         *
         * As the result have been returned from the proxied method, it has to
         * implement directly or indirectly it's return type interface
         */
        assert Set.of(interfaces).stream().anyMatch(iface -> iface.isAssignableFrom(returnType));

        return Proxy.newProxyInstance(classLoader, interfaces, new DeepLoggingHandler(instance, classLoader));
    }

    private static Class<?>[] collectAllImplementedInterfaces(Object instance) {
        final Class<?> instanceClass = instance.getClass();
        final Set<Class<?>> interfaces = new HashSet<>();
        for (Class klass = instanceClass; !klass.equals(Object.class); klass = klass.getSuperclass()) {
            final Class<?>[] ifaces = klass.getInterfaces();
            for (Class<?> iface : ifaces) {
                interfaces.add(iface);
            }
        }

        return interfaces.toArray(new Class[interfaces.size()]);
    }

    @Override
    protected void after(Method method, Object[] args, Object result, Throwable ex) {
        if (logger.isLoggable(Level.FINEST)) {
            message.setLength(0);
            message.append(TAG_AFTER)
              .append(' ')
              .append(method.getDeclaringClass().getName())
              .append('.')
              .append(method.getName())
              .append('(');
            appendArrayTo(message, method.getParameterTypes(), typePrinter);
            message.append(')')
              .append(" finished for ")
              .append(executionTimeNanos / 1_000_000)
              .append(" millis");
            if (ex != null) {
                message.append(" and failed with exception: ").append(ex);
            }

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

    public static <T> T proxy(T instance, Class<T> iface, ClassLoader classLoader) {
        return (T) proxy(instance, new Class[]{iface}, classLoader);
    }

    public static Object proxy(Object instance, Class<?>[] ifaces, ClassLoader classLoader) {
        if (null == instance) {
            return instance;
        }

        if (null == ifaces || ifaces.length == 0) {
            throw new IllegalArgumentException("You must specify interfaces to proxy.");
        }

        for (Class<?> iface : ifaces) {
            if (!iface.isInterface()) {
                throw new IllegalArgumentException("The provided class is not an interface: " + iface);
            }

            if (!iface.isAssignableFrom(instance.getClass())) {
                throw new IllegalArgumentException("The provided object instance does not implement the provided interface: " + iface);
            }
        }

        return Proxy.newProxyInstance(classLoader, ifaces, new DeepLoggingHandler(instance, classLoader));
    }
}
