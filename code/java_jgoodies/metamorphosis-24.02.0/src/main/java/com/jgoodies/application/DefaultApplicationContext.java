package com.jgoodies.application;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.util.prefs.Preferences;
import javax.swing.ActionMap;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/DefaultApplicationContext.class */
public class DefaultApplicationContext {
    private final Application application;
    private final ResourceManager resourceManager;
    private final ActionManager actionManager;
    private final TaskService taskService;
    private final TaskMonitor taskMonitor;

    public DefaultApplicationContext(Application application, ActionManager actionManager, ResourceManager resourceManager, TaskService taskService) {
        this.application = (Application) Preconditions.checkNotNull(application, Messages.MUST_NOT_BE_NULL, "application");
        this.resourceManager = (ResourceManager) Preconditions.checkNotNull(resourceManager, Messages.MUST_NOT_BE_NULL, "resource manager");
        this.actionManager = (ActionManager) Preconditions.checkNotNull(actionManager, Messages.MUST_NOT_BE_NULL, "action manager");
        this.taskService = taskService != null ? taskService : new TaskService("default");
        this.taskMonitor = new TaskMonitor(this.taskService);
    }

    public final TaskService getTaskService() {
        return this.taskService;
    }

    public TaskMonitor getTaskMonitor() {
        return this.taskMonitor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Class<? extends Application> getApplicationClass() {
        return this.application.getClass();
    }

    private Application getApplication() {
        return this.application;
    }

    public final ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public final ResourceMap getResourceMap() {
        return getResourceManager().getResourceMap();
    }

    public final ResourceMap getResourceMap(Class<?> type) {
        return getResourceManager().getResourceMap(type);
    }

    public final ActionMap createActionMap(Object target) {
        return createActionMap(target, null);
    }

    public final ActionMap createActionMap(Object target, ResourceMap resourceMap) {
        Preconditions.checkNotNull(target, Messages.MUST_NOT_BE_NULL, "target");
        return this.actionManager.createActionMap(target, resourceMap);
    }

    public Preferences getUserPreferences() {
        return Preferences.userRoot().node(getApplication().getApplicationPreferencesNodeName());
    }

    public Preferences getUserPreferences(Class<?> type) {
        return getUserPreferences().node(preferencesNodeName(type));
    }

    public Preferences getSystemPreferences() {
        return Preferences.systemRoot().node(getApplication().getApplicationPreferencesNodeName());
    }

    public Preferences getSystemPreferences(Class<?> type) {
        return getSystemPreferences().node(preferencesNodeName(type));
    }

    public static String preferencesNodeName(Class<?> type) {
        Preconditions.checkArgument(!type.isArray(), "Arrays have no associated preferences node name.");
        String className = type.getName();
        return "/" + className.replace('.', '/').toLowerCase();
    }
}
