package com.jgoodies.application;

import com.jgoodies.application.internal.Exceptions;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/AbstractResourceMap.class */
public abstract class AbstractResourceMap implements ResourceMap {
    private final ResourceMap parent;
    private final String baseName;
    private final ClassLoader classLoader;

    @Override // com.jgoodies.application.ResourceMap
    public abstract <T> T getObject(String str, Class<T> cls);

    @Override // com.jgoodies.application.ResourceMap
    public abstract <T> T getObject(ResourceMap resourceMap, String str, Class<T> cls);

    public AbstractResourceMap(ResourceMap parent, String baseName, ClassLoader classLoader) {
        Preconditions.checkNotNull(baseName, Messages.MUST_NOT_BE_NULL, "resource bundle base name");
        Preconditions.checkNotNull(classLoader, Messages.MUST_NOT_BE_NULL, "class loader");
        this.parent = parent;
        this.baseName = baseName;
        this.classLoader = classLoader;
    }

    @Override // com.jgoodies.application.ResourceMap
    public final ResourceMap getParent() {
        return this.parent;
    }

    @Override // com.jgoodies.application.ResourceMap
    public final String getBaseName() {
        return this.baseName;
    }

    @Override // com.jgoodies.application.ResourceMap
    public final ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override // com.jgoodies.application.ResourceMap
    public final String resolvePath(String path) {
        if (path == null) {
            return path;
        }
        if (path.startsWith("/")) {
            return path.substring(1);
        }
        return getResourceParentPath() + path;
    }

    @Override // com.jgoodies.application.ResourceMap
    public final boolean getBoolean(String key) {
        return ((Boolean) getObject(key, Boolean.class)).booleanValue();
    }

    @Override // com.jgoodies.application.ResourceMap
    public final byte getByte(String key) {
        return ((Byte) getObject(key, Byte.class)).byteValue();
    }

    @Override // com.jgoodies.application.ResourceMap
    public final Calendar getCalendar(String key) {
        return (Calendar) getObject(key, Calendar.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final char getCharacter(String key) {
        return ((Character) getObject(key, Character.class)).charValue();
    }

    @Override // com.jgoodies.application.ResourceMap
    public final Color getColor(String key) {
        return (Color) getObject(key, Color.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final Date getDate(String key) {
        return (Date) getObject(key, Date.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final LocalDate getLocalDate(String key) {
        return (LocalDate) getObject(key, LocalDate.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final Dimension getDimension(String key) {
        return (Dimension) getObject(key, Dimension.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final double getDouble(String key) {
        return ((Double) getObject(key, Double.class)).doubleValue();
    }

    @Override // com.jgoodies.application.ResourceMap
    public final EmptyBorder getEmptyBorder(String key) {
        return (EmptyBorder) getObject(key, EmptyBorder.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final File getFile(String key) {
        return (File) getObject(key, File.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final float getFloat(String key) {
        return ((Float) getObject(key, Float.class)).floatValue();
    }

    @Override // com.jgoodies.application.ResourceMap
    public final Font getFont(String key) {
        return (Font) getObject(key, Font.class);
    }

    @Override // com.jgoodies.application.ResourceMap, com.jgoodies.common.resource.IconResourceAccessor
    public final Icon getIcon(String key) {
        return (Icon) getObject(key, Icon.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final Image getImage(String key) {
        return (Image) getObject(key, Image.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final ImageIcon getImageIcon(String key) {
        return (ImageIcon) getObject(key, ImageIcon.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final Insets getInsets(String key) {
        return (Insets) getObject(key, Insets.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final int getInt(String key) {
        return ((Integer) getObject(key, Integer.class)).intValue();
    }

    @Override // com.jgoodies.application.ResourceMap
    public final KeyStroke getKeyStroke(String key) {
        return (KeyStroke) getObject(key, KeyStroke.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final long getLong(String key) {
        return ((Long) getObject(key, Long.class)).longValue();
    }

    @Override // com.jgoodies.application.ResourceMap
    public final MessageFormat getMessageFormat(String key) {
        return (MessageFormat) getObject(key, MessageFormat.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final Point getPoint(String key) {
        return (Point) getObject(key, Point.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final Rectangle getRectangle(String key) {
        return (Rectangle) getObject(key, Rectangle.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final short getShort(String key) {
        return ((Short) getObject(key, Short.class)).shortValue();
    }

    @Override // com.jgoodies.common.resource.StringResourceAccessor
    public final String getString(String key, Object... args) {
        String str = (String) getObject(key, String.class);
        if (args == null || args.length == 0) {
            return str;
        }
        try {
            return String.format(str, args);
        } catch (IllegalFormatException ex) {
            throw new Exceptions.ResourceFormatException(this, key, str, ex);
        }
    }

    @Override // com.jgoodies.application.ResourceMap
    public final URI getURI(String key) {
        return (URI) getObject(key, URI.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final URL getURL(String key) {
        return (URL) getObject(key, URL.class);
    }

    @Override // com.jgoodies.application.ResourceMap
    public final InputStream getURLAsStream(String key) {
        URL url = getURL(key);
        if (url == null) {
            return null;
        }
        try {
            return url.openStream();
        } catch (IOException e) {
            return null;
        }
    }
}
