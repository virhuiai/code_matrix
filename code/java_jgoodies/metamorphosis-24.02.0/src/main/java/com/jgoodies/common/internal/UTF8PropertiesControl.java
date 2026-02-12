package com.jgoodies.common.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/internal/UTF8PropertiesControl.class */
public final class UTF8PropertiesControl extends ResourceBundle.Control {
    @Override // java.util.ResourceBundle.Control
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        URLConnection connection;
        if (!"java.properties".equals(format)) {
            return super.newBundle(baseName, locale, format, loader, reload);
        }
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        ResourceBundle bundle = null;
        InputStream stream = null;
        if (reload) {
            URL url = loader.getResource(resourceName);
            if (url != null && (connection = url.openConnection()) != null) {
                connection.setUseCaches(false);
                stream = connection.getInputStream();
            }
        } else {
            stream = loader.getResourceAsStream(resourceName);
        }
        if (stream != null) {
            try {
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
                stream.close();
            } catch (Throwable th) {
                stream.close();
                throw th;
            }
        }
        return bundle;
    }
}
