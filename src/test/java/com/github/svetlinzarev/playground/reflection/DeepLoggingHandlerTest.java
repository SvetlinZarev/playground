package com.github.svetlinzarev.playground.reflection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeepLoggingHandlerTest {
    private interface ParentInterface {
        ParentInterface getObject();
    }

    private interface ChildInterface extends ParentInterface {
    }

    private static class ChildClass implements ParentInterface {
        @Override
        public ParentInterface getObject() {
            return null;
        }
    }

    private interface GenericInterface<T> {
        T getObject();
    }

    private ClassLoader classLoader;

    @BeforeAll
    public void beforeAll() {
        classLoader = getClassLoader();
    }

    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (null == classLoader) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return classLoader;
    }

    @Test
    public void testProxySimpleInterface() {
        final Runnable originalObject = new Runnable() {
            @Override
            public void run() {
                //do nothing
            }
        };

        final Runnable proxiedObject = DeepLoggingHandler.proxy(originalObject, Runnable.class, classLoader);
        assertTrue(Proxy.isProxyClass(proxiedObject.getClass()), "The object should have been proxied!");
    }

    @Test
    public void testProxySimpleInterfaceViaLambda() {
        final Runnable originalObject = () -> {/* do nothing */ };

        final Runnable proxiedObject = DeepLoggingHandler.proxy(originalObject, Runnable.class, classLoader);
        assertTrue(Proxy.isProxyClass(proxiedObject.getClass()), "The object should have been proxied!");
    }

    @Test
    public void testDeepProxyingOfInterfaces() {
        final ParentInterface original = new ParentInterface() {
            @Override
            public ParentInterface getObject() {
                return () -> null;
            }
        };

        final ParentInterface proxiedParent = DeepLoggingHandler.proxy(original, ParentInterface.class, classLoader);
        final ParentInterface proxiedChild = proxiedParent.getObject();

        assertTrue(Proxy.isProxyClass(proxiedChild.getClass()), "The object should have been proxied!");
    }

    @Test
    public void testDeepProxyingOfInterfaces_withInheritance() {
        final ParentInterface original = new ParentInterface() {
            @Override
            public ChildInterface getObject() { //The subclass explicitly returns the child interface
                return new ChildInterface() {
                    @Override
                    public ParentInterface getObject() {
                        return null;
                    }
                };
            }
        };

        final ParentInterface proxiedParent = DeepLoggingHandler.proxy(original, ParentInterface.class, classLoader);
        final ParentInterface proxiedChild = proxiedParent.getObject();

        assertTrue(Proxy.isProxyClass(proxiedChild.getClass()), "The object should have been proxied!");
        assertTrue(proxiedChild instanceof ChildInterface, "The object should be instance of the child interface");
    }

    @Test
    public void testDeepProxyingOfInterfaces_withSubClassing() {
        final ParentInterface original = new ParentInterface() {
            @Override
            public ChildClass getObject() { //The subclass explicitly returns the child class
                return new ChildClass();
            }
        };

        final ParentInterface proxiedParent = DeepLoggingHandler.proxy(original, ParentInterface.class, classLoader);
        final ParentInterface proxiedChild = proxiedParent.getObject();

        assertTrue(Proxy.isProxyClass(proxiedChild.getClass()), "The object should have been proxied!");
    }

    @Test
    public void testProxyingOfGenericInterfaces_withClass() {
        final GenericInterface<Object> original = new GenericInterface<Object>() {
            @Override
            public Object getObject() {
                return new Object();
            }
        };

        final GenericInterface<Object> proxied = DeepLoggingHandler.proxy(original, GenericInterface.class, classLoader);
        final Object proxiedChild = proxied.getObject();

        assertFalse(Proxy.isProxyClass(proxiedChild.getClass()));
    }

    @Test
    public void testProxyingOfGenericInterfaces_withInterfaceViaLambda() {
        final GenericInterface<ParentInterface> original = new GenericInterface<ParentInterface>() {
            @Override
            public ParentInterface getObject() {
                return () -> null;
            }
        };

        final GenericInterface<ParentInterface> proxied = DeepLoggingHandler.proxy(original, GenericInterface.class, classLoader);
        final ParentInterface proxiedChild = proxied.getObject();

        assertTrue(Proxy.isProxyClass(proxiedChild.getClass()));
    }

    @Test
    public void testProxyingOfGenericInterfaces_withSubInterfaceViaLambda() {
        final GenericInterface<ParentInterface> original = new GenericInterface<ParentInterface>() {
            @Override
            public ChildInterface getObject() {
                return () -> null;
            }
        };

        final GenericInterface<ParentInterface> proxied = DeepLoggingHandler.proxy(original, GenericInterface.class, classLoader);
        final ParentInterface proxiedChild = proxied.getObject();

        assertTrue(Proxy.isProxyClass(proxiedChild.getClass()), "The object should have been proxied!");
        assertTrue(proxiedChild instanceof ChildInterface, "The object should be instance of the child interface");
    }
}
