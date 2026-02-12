package com.jgoodies.common.jsdl.i18n;

import com.jgoodies.application.Application;
import com.jgoodies.application.ResourceMap;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.MissingResourceException;
import java.util.logging.Logger;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/i18n/Resources.class */
public final class Resources {
    private static InjectionHandler defaultHandler = new DefaultInjectionHandler();

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/i18n/Resources$InjectionHandler.class */
    public interface InjectionHandler {
        void handleFieldSetFailed(Field field, String str, Exception exc);

        String handleMissingResource(Field field, ResourceMap resourceMap, String str, MissingResourceException missingResourceException);
    }

    private Resources() {
    }

    public static void setDefaultHandler(InjectionHandler newDefaultHandler) {
        defaultHandler = (InjectionHandler) Preconditions.checkNotNull(newDefaultHandler, Messages.MUST_NOT_BE_NULL, "new default handler");
    }

    public static <T> T get(Class<T> cls) {
        return (T) get(cls, defaultHandler);
    }

    private static <T> T get(Class<T> injectionClass, InjectionHandler handler) {
        Injector<T> injector = new Injector<>(injectionClass, handler);
        T instance = injector.createInstance();
        injector.injectResources(instance);
        return instance;
    }

    public static void injectInto(Object instance) {
        Injector<?> injector = new Injector<>(instance.getClass(), defaultHandler);
        injector.injectResources(instance);
    }

    public static void injectInto(Class<?> injectionClass) {
        injectInto(injectionClass, defaultHandler);
    }

    private static <T> void injectInto(Class<T> injectionClass, InjectionHandler handler) {
        Injector<T> injector = new Injector<>(injectionClass, handler);
        injector.injectResources(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/i18n/Resources$Injector.class */
    public static final class Injector<R> {
        private final Class<R> injectionClass;
        private final InjectionHandler handler;
        private final ResourceMap resourceMap;
        private final String prefix;

        Injector(Class<R> injectionClass, InjectionHandler handler) {
            Preconditions.checkNotNull(injectionClass, Messages.MUST_NOT_BE_NULL, "the class to inject resources into");
            Preconditions.checkNotNull(handler, Messages.MUST_NOT_BE_NULL, "the injection handler");
            this.injectionClass = injectionClass;
            this.handler = handler;
            ResourceProvider annotation = (ResourceProvider) injectionClass.getAnnotation(ResourceProvider.class);
            this.resourceMap = Application.getResourceMap((annotation == null || annotation.resourceClass() == Object.class) ? injectionClass : annotation.resourceClass());
            this.prefix = (annotation == null || Strings.isBlank(annotation.keyPrefix())) ? "" : annotation.keyPrefix() + '.';
        }

        R createInstance() {
            try {
                Constructor<R> constructor = this.injectionClass.getDeclaredConstructor(new Class[0]);
                constructor.setAccessible(true);
                return constructor.newInstance((Object[]) null);
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        void injectResources(Object instance) {
            Object value;
            for (Field field : this.injectionClass.getDeclaredFields()) {
                boolean isStatic = (field.getModifiers() & 8) != 0;
                if (!isStatic || instance == null) {
                    Class<?> fieldType = field.getType();
                    String key = mapToKey(field);
                    try {
                        value = this.resourceMap.getObject(key, fieldType);
                    } catch (MissingResourceException ex) {
                        value = this.handler.handleMissingResource(field, this.resourceMap, key, ex);
                    }
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    try {
                        field.set(instance, value);
                    } catch (IllegalAccessException | IllegalArgumentException ex2) {
                        this.handler.handleFieldSetFailed(field, key, ex2);
                    }
                }
            }
        }

        private String mapToKey(Field field) {
            Key keyAnnotation = (Key) field.getAnnotation(Key.class);
            if (keyAnnotation != null) {
                String key = keyAnnotation.value();
                Preconditions.checkNotBlank(key, Messages.MUST_NOT_BE_BLANK, "the @Key's text");
                return keyAnnotation.prefix() ? this.prefix + key : key;
            }
            String fieldName = field.getName();
            String fieldKey = fieldName.replace('_', '.');
            return this.prefix + fieldKey;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/i18n/Resources$DefaultInjectionHandler.class */
    public static class DefaultInjectionHandler implements InjectionHandler {
        private final String keyFormat;

        public DefaultInjectionHandler() {
            this("{%s}");
        }

        public DefaultInjectionHandler(String keyFormat) {
            this.keyFormat = keyFormat;
        }

        @Override // com.jgoodies.common.jsdl.i18n.Resources.InjectionHandler
        public void handleFieldSetFailed(Field field, String resourceKey, Exception ex) {
            ex.printStackTrace();
        }

        @Override // com.jgoodies.common.jsdl.i18n.Resources.InjectionHandler
        public String handleMissingResource(Field field, ResourceMap map, String key, MissingResourceException ex) {
            String message = String.format("Missing resource for key \"%1$s\"\nbundle base name=%2$s\ndeclaring class =%3$s\nkey       =\"%1$s\"\nfield name=\"%4$s\"\nfield modifiers=%5$s", key, map.getBaseName(), field.getDeclaringClass().getName(), field.getName(), Modifier.toString(field.getModifiers()));
            Logger.getLogger(DefaultInjectionHandler.class.getName()).info(message);
            return String.format(this.keyFormat, key);
        }
    }
}
