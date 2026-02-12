package com.jgoodies.application;

import com.jgoodies.application.internal.Exceptions;
import com.jgoodies.binding.value.ComponentModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.ScreenScaling;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/DefaultResourceMap.class */
public class DefaultResourceMap extends AbstractResourceMap {
    private static final String DETAILED_MISSING_RESOURCE_EXCEPTIONS_KEY = "DefaultResourceMap.detailedMissingResourceExceptions";
    private final Locale locale;
    private final String resourceParentPath;
    private final Evaluator evaluator;
    private ResourceBundle bundle;
    private boolean bundleSearched;
    private Map<String, Object> cache;
    private static final Object NULL_RESOURCE = new String("Null resource");
    private static final Logger LOGGER = Logger.getLogger(DefaultResourceMap.class.getName());
    private static final Evaluator DEFAULT_EVALUATOR = new DefaultEvaluator();
    private static boolean detailedMissingResourceExceptionsEnabled = getDetailedMissingResourceExceptionEnabledFromSystemProperties();
    private static int counter = 0;

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/DefaultResourceMap$Evaluator.class */
    public interface Evaluator {
        String evaluate(ResourceMap resourceMap, String str, String str2);
    }

    public DefaultResourceMap(ResourceMap parent, ResourceBundle bundle, ClassLoader classLoader) {
        this(parent, bundle, classLoader, DEFAULT_EVALUATOR);
    }

    public DefaultResourceMap(ResourceMap parent, ResourceBundle bundle, ClassLoader classLoader, Evaluator evaluator) {
        this(parent, bundle.getBaseBundleName(), null, classLoader, evaluator);
        this.bundle = bundle;
        this.bundleSearched = true;
    }

    public DefaultResourceMap(ResourceMap parent, String baseName, Locale locale, ClassLoader classLoader) {
        this(parent, baseName, locale, classLoader, DEFAULT_EVALUATOR);
    }

    public DefaultResourceMap(ResourceMap parent, String baseName, Locale locale, ClassLoader classLoader, Evaluator evaluator) {
        super(parent, baseName, classLoader);
        this.locale = locale;
        this.evaluator = evaluator;
        this.resourceParentPath = resourceParentPath(baseName);
    }

    public static boolean getDetailedMissingResourceExceptionsEnabled() {
        return detailedMissingResourceExceptionsEnabled;
    }

    public static void setDetailedMissingResourceExceptionsEnabled(boolean enabled) {
        detailedMissingResourceExceptionsEnabled = enabled;
    }

    private static boolean getDetailedMissingResourceExceptionEnabledFromSystemProperties() {
        String property = System.getProperty(DETAILED_MISSING_RESOURCE_EXCEPTIONS_KEY);
        return ComponentModel.PROPERTY_ENABLED.equalsIgnoreCase(property) || "on".equalsIgnoreCase(property);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final synchronized ResourceBundle getBundle() {
        if (this.bundleSearched) {
            return this.bundle;
        }
        try {
            this.bundle = findBundle(getBaseName(), this.locale, getClassLoader());
        } catch (MissingResourceException e) {
        }
        this.bundleSearched = true;
        return this.bundle;
    }

    @Override // com.jgoodies.application.ResourceMap
    public final String getResourceParentPath() {
        return this.resourceParentPath;
    }

    @Override // com.jgoodies.application.AbstractResourceMap, com.jgoodies.application.ResourceMap
    public final <T> T getObject(String str, Class<T> cls) {
        return (T) getObject(this, str, cls);
    }

    @Override // com.jgoodies.application.AbstractResourceMap, com.jgoodies.application.ResourceMap
    public <T> T getObject(ResourceMap resourceMap, String str, Class<T> cls) {
        Object resource;
        Object object;
        Preconditions.checkNotNull(resourceMap, Messages.MUST_NOT_BE_NULL, "scope");
        Preconditions.checkNotNull(str, Messages.MUST_NOT_BE_NULL, "key");
        Preconditions.checkNotNull(cls, Messages.MUST_NOT_BE_NULL, "type");
        ResourceBundle bundle = getBundle();
        boolean z = resourceMap instanceof DefaultResourceMap;
        if (getParent() == null) {
            if (bundle == null) {
                throw new MissingResourceException("Missing resource bundle " + getBaseName(), getBaseName(), null);
            }
            if (z) {
                object = ((DefaultResourceMap) resourceMap).getResource(bundle, str, resourceMap);
            } else {
                object = resourceMap.getObject(str, cls);
            }
            resource = object;
        } else {
            if (bundle == null || !bundle.containsKey(str)) {
                return (T) getParent().getObject(resourceMap, str, cls);
            }
            resource = getResource(bundle, str, resourceMap);
        }
        boolean z2 = false;
        if (resource instanceof String) {
            String evaluate = this.evaluator.evaluate(resourceMap, str, (String) resource);
            z2 = evaluate != resource;
            if (z2) {
                if (z) {
                    ((DefaultResourceMap) resourceMap).putResource(str, evaluate);
                }
                resource = evaluate;
            }
        }
        if (resource == null) {
            return null;
        }
        if (cls.isInstance(resource)) {
            return cls.cast(resource);
        }
        if (resource instanceof CachedResource) {
            CachedResource cachedResource = (CachedResource) resource;
            if (cls == String.class) {
                return cls.cast(cachedResource.evaluatedString);
            }
            if (cls.isInstance(cachedResource.convertedObject)) {
                return cls.cast(cachedResource.convertedObject);
            }
            resource = cachedResource.evaluatedString;
            if (resource == null) {
                return null;
            }
        }
        if (!(resource instanceof String)) {
            wrongType(str, resource, cls);
        }
        ResourceConverter forType = ResourceConverters.forType(cls);
        if (forType == null) {
            throw new Exceptions.ResourceConversionException("No resource converter found for type " + cls, this, str, "" + resource);
        }
        T cast = cls.cast(forType.convert(this, str, ((String) resource).trim(), cls));
        if (!z2) {
            putResource(str, (String) resource, cast);
        } else if (z) {
            ((DefaultResourceMap) resourceMap).putResource(str, (String) resource, cast);
        }
        return cast;
    }

    protected ResourceBundle findBundle(String aBaseName, Locale aLocale, ClassLoader loader) {
        return ResourceBundle.getBundle(aBaseName, aLocale, getClassLoader());
    }

    private static String resourceParentPath(String baseName) {
        int index = baseName.lastIndexOf(46);
        return index == -1 ? "" : baseName.substring(0, index).replace('.', '/') + "/";
    }

    private void wrongType(String key, Object value, Class<?> type) {
        throw new ClassCastException("The resource value must either be a String or " + type.getName() + "\nBundle base name=" + getBaseName() + "\nkey=" + key + "\nvalue=" + value + "\nvalue type= " + (value == null ? "null" : value.getClass().getName()) + "\nreturn type=" + type);
    }

    private Object getResource(ResourceBundle rBundle, String key, ResourceMap scope) {
        Object value;
        if (this.cache != null && (value = this.cache.get(key)) != null) {
            if (value == NULL_RESOURCE) {
                return null;
            }
            return value;
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            Logger logger = LOGGER;
            int i = counter + 1;
            counter = i;
            logger.finer(String.format("Resource bundle access #%1$04d key=%2$s; bundle=%3$s", Integer.valueOf(i), key, getBaseName()));
        }
        if (!detailedMissingResourceExceptionsEnabled) {
            return rBundle.getObject(key);
        }
        try {
            return rBundle.getObject(key);
        } catch (MissingResourceException cause) {
            StringBuilder builder = new StringBuilder(cause.getMessage());
            builder.append("\nBase name=");
            builder.append(getBaseName());
            builder.append("\nResource map chain=");
            ResourceMap resourceMap = scope;
            while (true) {
                ResourceMap map = resourceMap;
                if (map == null) {
                    break;
                }
                builder.append("\n    ");
                builder.append(map.getBaseName());
                resourceMap = map.getParent();
            }
            throw new MissingResourceException(builder.toString(), cause.getClassName(), key);
        }
    }

    Object getCachedResourceString(String key) {
        Object value;
        if (this.cache == null || (value = this.cache.get(key)) == null || value == NULL_RESOURCE) {
            return null;
        }
        return ((CachedResource) value).evaluatedString;
    }

    Object getCachedResourceObject(String key) {
        Object value;
        if (this.cache == null || (value = this.cache.get(key)) == null || value == NULL_RESOURCE) {
            return null;
        }
        return ((CachedResource) value).convertedObject;
    }

    boolean cacheContainsKey(String key) {
        return this.cache != null && this.cache.containsKey(key);
    }

    private void putResource(String key, String evaluatedString, Object convertedObject) {
        Object entry = convertedObject == null ? NULL_RESOURCE : new CachedResource(evaluatedString, convertedObject);
        cachePut(key, entry);
    }

    private void putResource(String key, String evaluatedString) {
        cachePut(key, new CachedResource(evaluatedString, null));
    }

    private void cachePut(String key, Object entry) {
        if (this.cache == null) {
            this.cache = new ConcurrentHashMap(20);
        }
        this.cache.put(key, entry);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/DefaultResourceMap$CachedResource.class */
    public static final class CachedResource {
        final String evaluatedString;
        final Object convertedObject;

        private CachedResource(String evaluatedString, Object convertedObject) {
            this.evaluatedString = evaluatedString;
            this.convertedObject = convertedObject;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/DefaultResourceMap$DefaultEvaluator.class */
    public static class DefaultEvaluator implements Evaluator {
        @Override // com.jgoodies.application.DefaultResourceMap.Evaluator
        public String evaluate(ResourceMap scope, String key, String expr) {
            String variableValue;
            int cursor = 0;
            int start = expr.indexOf("${", 0);
            if (start == -1) {
                return expr;
            }
            if ("null}".equals(expr.substring(start + 2).trim()) && !isQuote(expr, start)) {
                return null;
            }
            StringBuilder result = new StringBuilder();
            boolean nonNullValue = false;
            do {
                if (start > 0 && expr.charAt(start - 1) == '\\') {
                    if (start > 1 && expr.charAt(start - 2) == '\\') {
                        result.append(expr.substring(cursor, start - 2));
                        cursor = start - 1;
                    } else {
                        result.append(expr.substring(cursor, start - 1));
                        result.append("${");
                        nonNullValue = true;
                        cursor = start + 2;
                        start = expr.indexOf("${", cursor);
                    }
                }
                int end = expr.indexOf(125, start);
                if (end <= start + 2) {
                    String rest = expr.substring(cursor);
                    throw new Exceptions.ResourceEvaluationException("Missing closing brace '}' in \"" + rest + "\"", scope, key, expr);
                }
                String variableName = expr.substring(start + 2, end);
                if (key.equals(variableName)) {
                    throw new Exceptions.ResourceEvaluationException("Resource expression refers to its defining key.", scope, key, expr);
                }
                if (variableName.startsWith("$")) {
                    variableValue = evaluateSystemVariable(scope, variableName, key, expr);
                } else {
                    String optionSuffix = extractOptionSuffix(scope, variableName, key, expr);
                    if (optionSuffix == null) {
                        try {
                            variableValue = scope.getString(variableName, new Object[0]);
                        } catch (MissingResourceException e) {
                            throw new Exceptions.ResourceEvaluationException("Undefined variable \"" + variableName + "\"", scope, key, expr, e);
                        }
                    } else {
                        int prefixLength = variableName.length() - (optionSuffix.length() + 2);
                        String variablePrefix = variableName.substring(0, prefixLength);
                        String optionValue = evaluateSystemVariable(scope, optionSuffix, key, expr);
                        String specificVariableName = variablePrefix + optionValue;
                        try {
                            variableValue = scope.getString(specificVariableName, new Object[0]);
                        } catch (MissingResourceException e2) {
                            try {
                                variableValue = scope.getString(variablePrefix + "default", new Object[0]);
                            } catch (MissingResourceException e22) {
                                throw new Exceptions.ResourceEvaluationException("Undefined option variable \"" + variablePrefix.substring(0, variablePrefix.length() - 1) + "\"", scope, key, expr, e22);
                            }
                        }
                    }
                }
                result.append(expr.substring(cursor, start));
                result.append(variableValue);
                nonNullValue = nonNullValue || variableValue != null;
                cursor = end + 1;
                start = expr.indexOf("${", cursor);
            } while (start != -1);
            boolean nonNullValue2 = nonNullValue || cursor != expr.length();
            result.append(expr.substring(cursor));
            String expandedExpr = nonNullValue2 ? result.toString() : null;
            if (DefaultResourceMap.LOGGER.isLoggable(Level.FINER)) {
                DefaultResourceMap.LOGGER.finer(String.format("Resource expression \"%1$s\" expands to \"%2$s\".", expr, expandedExpr));
            }
            return expandedExpr;
        }

        protected String extractOptionSuffix(ResourceMap scope, String variableName, String key, String expr) {
            int start = variableName.indexOf(91);
            if (start == -1) {
                return null;
            }
            int end = variableName.indexOf(93, start);
            if (end <= start + 1) {
                String rest = variableName.substring(start);
                throw new Exceptions.ResourceEvaluationException("Missing closing bracket ']' in \"" + rest + "\".", scope, key, expr);
            }
            return variableName.substring(start + 1, end);
        }

        protected String evaluateSystemVariable(ResourceMap scope, String systemVariableName, String key, String expr) {
            String value = evaluateSpecialVariable(systemVariableName);
            if (value != null) {
                return value;
            }
            String systemPropertyKey = systemVariableName.substring(1);
            String systemPropertyValue = getSystemProperty(systemPropertyKey);
            if (systemPropertyValue != null) {
                return systemPropertyValue;
            }
            throw new Exceptions.ResourceEvaluationException("Undefined system variable \"" + systemVariableName + "\".", scope, key, expr);
        }

        protected String evaluateSpecialVariable(String variableName) {
            if ("$laf".equals(variableName)) {
                return UIManager.getLookAndFeel().getID().toLowerCase(Locale.ENGLISH);
            }
            if ("$os".equals(variableName)) {
                if (SystemUtils.IS_OS_WINDOWS) {
                    return "win";
                }
                if (SystemUtils.IS_OS_MAC) {
                    return "mac";
                }
                if (SystemUtils.IS_OS_LINUX) {
                    return "linux";
                }
                if (SystemUtils.IS_OS_SOLARIS) {
                    return "solaris";
                }
                return "default";
            }
            if ("$scale".equals(variableName)) {
                return ScreenScaling.isScale100() ? "100" : "200";
            }
            return null;
        }

        protected static String getSystemProperty(String key) {
            try {
                return System.getProperty(key);
            } catch (SecurityException e) {
                DefaultResourceMap.LOGGER.warning("Can't access the System property " + key + ".");
                return "";
            }
        }

        protected static boolean isQuote(String expr, int start) {
            return start > 0 && expr.charAt(start - 1) == '\\' && (start <= 1 || expr.charAt(start - 2) != '\\');
        }
    }
}
