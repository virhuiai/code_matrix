package com.jgoodies.application;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/DefaultResourceManager.class */
public class DefaultResourceManager implements ResourceManager {
    private final Class<? extends Application> applicationClass;
    private final Map<Class<?>, ResourceMap> classResourceMapCache = Collections.synchronizedMap(new WeakHashMap());
    private final Map<Package, ResourceMap> packageResourceMapCache = Collections.synchronizedMap(new WeakHashMap());
    private ResourceMap cachedApplicationResourceMap;
    private Locale cachedLocale;

    public DefaultResourceManager(Class<? extends Application> applicationClass) {
        this.applicationClass = (Class) Preconditions.checkNotNull(applicationClass, Messages.MUST_NOT_BE_NULL, "application class");
    }

    @Override // com.jgoodies.application.ResourceManager
    public ResourceMap getResourceMap() {
        ensureValidCache();
        if (this.cachedApplicationResourceMap == null) {
            this.cachedApplicationResourceMap = createApplicationResourceMap();
        }
        return this.cachedApplicationResourceMap;
    }

    @Override // com.jgoodies.application.ResourceManager
    public ResourceMap getResourceMap(Class<?> type) {
        if (type == null) {
            return getResourceMap();
        }
        ensureValidCache();
        ResourceMap resourceMap = this.classResourceMapCache.get(type);
        if (resourceMap != null) {
            return resourceMap;
        }
        ResourceMap parentMap = getResourceMap(type.getPackage(), type);
        ResourceMap classResourceMap = createResourceMap(parentMap, type);
        ResourceMap resourceMap2 = classResourceMap.getBundle() == null ? parentMap : classResourceMap;
        this.classResourceMapCache.put(type, resourceMap2);
        return resourceMap2;
    }

    @Override // com.jgoodies.application.ResourceManager
    public void clear() {
        this.cachedApplicationResourceMap = null;
        this.classResourceMapCache.clear();
        this.packageResourceMapCache.clear();
    }

    protected ResourceMap getResourceMap(Package aPackage, Class<?> type) {
        ensureValidCache();
        ResourceMap resourceMap = this.packageResourceMapCache.get(aPackage);
        if (resourceMap != null) {
            return resourceMap;
        }
        ResourceMap parentMap = getResourceMap();
        ResourceMap packageResourceMap = createResourceMap(parentMap, createPackageBundleBaseName(type), Locale.getDefault(), type.getClassLoader());
        ResourceMap resourceMap2 = packageResourceMap.getBundle() == null ? parentMap : packageResourceMap;
        this.packageResourceMapCache.put(aPackage, resourceMap2);
        return resourceMap2;
    }

    private ResourceMap createApplicationResourceMap() {
        if (getApplicationClass() == null) {
            return createResourceMap(null, Application.class);
        }
        return createApplicationResourceMap(getApplicationClass());
    }

    protected ResourceMap createApplicationResourceMap(Class<?> type) {
        if (type == Application.class) {
            return null;
        }
        ResourceMap parentMap = createApplicationResourceMap(type.getSuperclass());
        ResourceMap typeMap = createResourceMap(parentMap, type);
        return typeMap.getBundle() == null ? parentMap : typeMap;
    }

    protected ResourceMap createResourceMap(ResourceMap parent, Class<?> type) {
        return createResourceMap(parent, createClassBundleBaseName(type), Locale.getDefault(), type.getClassLoader());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ResourceMap createResourceMap(ResourceMap parent, String bundleBaseName, Locale locale, ClassLoader loader) {
        return new DefaultResourceMap(parent, bundleBaseName, locale, loader);
    }

    protected String createPackageBundleBaseName(Class<?> type) {
        return defaultPackageBundleBaseName(type);
    }

    protected String createClassBundleBaseName(Class<?> type) {
        return defaultClassBundleBaseName(type);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void ensureValidCache() {
        if (!Locale.getDefault().equals(this.cachedLocale)) {
            clear();
            this.cachedLocale = Locale.getDefault();
        }
    }

    public static String defaultPackageBundleBaseName(Class<?> type) {
        return type.getPackage().getName() + ".resources.Package";
    }

    public static String defaultClassBundleBaseName(Class<?> type) {
        String packageName = type.getPackage().getName();
        String className = type.getSimpleName();
        return packageName + ".resources." + className;
    }

    private Class<? extends Application> getApplicationClass() {
        return this.applicationClass;
    }
}
