package com.jgoodies.application;

import com.jgoodies.common.resource.StringAndIconResourceAccessor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/ResourceMap.class */
public interface ResourceMap extends StringAndIconResourceAccessor {
    ResourceMap getParent();

    String getBaseName();

    ClassLoader getClassLoader();

    ResourceBundle getBundle();

    String getResourceParentPath();

    String resolvePath(String str);

    <T> T getObject(String str, Class<T> cls);

    <T> T getObject(ResourceMap resourceMap, String str, Class<T> cls);

    boolean getBoolean(String str);

    byte getByte(String str);

    Calendar getCalendar(String str);

    char getCharacter(String str);

    Color getColor(String str);

    Date getDate(String str);

    LocalDate getLocalDate(String str);

    Dimension getDimension(String str);

    double getDouble(String str);

    EmptyBorder getEmptyBorder(String str);

    File getFile(String str);

    float getFloat(String str);

    Font getFont(String str);

    Icon getIcon(String str);

    Image getImage(String str);

    ImageIcon getImageIcon(String str);

    Insets getInsets(String str);

    int getInt(String str);

    KeyStroke getKeyStroke(String str);

    long getLong(String str);

    MessageFormat getMessageFormat(String str);

    Point getPoint(String str);

    Rectangle getRectangle(String str);

    short getShort(String str);

    URI getURI(String str);

    URL getURL(String str);

    InputStream getURLAsStream(String str);
}
