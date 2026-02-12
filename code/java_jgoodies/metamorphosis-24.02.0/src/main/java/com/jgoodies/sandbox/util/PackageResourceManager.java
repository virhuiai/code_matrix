package com.jgoodies.sandbox.util;

import com.jgoodies.application.Application;
import com.jgoodies.application.DefaultResourceManager;
import com.jgoodies.application.ResourceMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/util/PackageResourceManager.class */
public final class PackageResourceManager extends DefaultResourceManager {
    private final Map<Package, String> moduleBundleBaseNames;
    private final Map<Package, ResourceMap> moduleResourceMapCache;

    public PackageResourceManager(Class<? extends Application> applicationClass) {
        super(applicationClass);
        this.moduleBundleBaseNames = new HashMap();
        this.moduleResourceMapCache = Collections.synchronizedMap(new WeakHashMap());
    }

    public void registerModule(Package moduleRootPackage) {
        registerModule(moduleRootPackage, defaultModuleBundleBaseName(moduleRootPackage));
    }

    public void registerModule(Package moduleRootPackage, String moduleBundleBaseName) {
        this.moduleBundleBaseNames.put(moduleRootPackage, moduleBundleBaseName);
    }

    @Override // com.jgoodies.application.DefaultResourceManager, com.jgoodies.application.ResourceManager
    public ResourceMap getResourceMap(Class<?> type) {
        if (type == null) {
            return super.getResourceMap(type);
        }
        ensureValidCache();
        Package modulePackage = getModulePackageFor(type);
        if (modulePackage == null) {
            return super.getResourceMap(type);
        }
        ResourceMap resourceMap = this.moduleResourceMapCache.get(modulePackage);
        if (resourceMap != null) {
            return resourceMap;
        }
        ResourceMap parentMap = getResourceMap();
        ResourceMap moduleResourceMap = createResourceMap(parentMap, modulePackage, type);
        ResourceMap resourceMap2 = moduleResourceMap.getBundle() == null ? parentMap : moduleResourceMap;
        this.moduleResourceMapCache.put(modulePackage, resourceMap2);
        return resourceMap2;
    }

    @Override // com.jgoodies.application.DefaultResourceManager, com.jgoodies.application.ResourceManager
    public void clear() {
        super.clear();
        this.moduleResourceMapCache.clear();
    }

    private Package getModulePackageFor(Class<?> type) {
        String classPackageName = type.getPackage().getName();
        return this.moduleBundleBaseNames.keySet().stream().filter(moduleRootPackage -> {
            return classPackageName.startsWith(moduleRootPackage.getName());
        }).findAny().orElse(null);
    }

    private ResourceMap createResourceMap(ResourceMap parent, Package moduleRootPackage, Class<?> type) {
        return createResourceMap(parent, this.moduleBundleBaseNames.get(moduleRootPackage), Locale.getDefault(), type.getClassLoader());
    }

    private static String defaultModuleBundleBaseName(Package moduleRootPackage) {
        return moduleRootPackage.getName() + ".resources.Module";
    }
}
