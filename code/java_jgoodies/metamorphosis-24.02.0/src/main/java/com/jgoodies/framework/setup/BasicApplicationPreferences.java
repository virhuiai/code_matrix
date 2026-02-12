package com.jgoodies.framework.setup;

import com.jgoodies.application.Application;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.bean.Bean;
import java.util.prefs.Preferences;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/setup/BasicApplicationPreferences.class */
public class BasicApplicationPreferences extends Bean implements Cloneable {
    public static final String PROPERTY_MENU_VISIBLE_ALWAYS = "menuVisibleAlways";
    public static final String PROPERTY_OPTIMIZED_FOR_SCREEN_READER = "optimizedForScreenReader";
    public static final String PROPERTY_BODY_TEXT_MODE = "bodyTextMode";
    private static final String KEY_MENU_VISIBLE_ALWAYS = "menu_visible_always";
    private static final String KEY_OPTIMIZED_FOR_SCREEN_READER = "optimized_for_screen_reader";
    private static final String KEY_BODY_TEXT_MODE = "body_text_mode";
    private boolean menuVisibleAlways;
    private boolean optimizedForScreenReader;
    private BodyTextMode bodyTextMode;

    public static BasicApplicationPreferences fromUserPreferences() {
        return from(Application.getInstance().getContext().getUserPreferences());
    }

    public static BasicApplicationPreferences from(Preferences pres) {
        BasicApplicationPreferences appPrefs = new BasicApplicationPreferences();
        appPrefs.readFrom(pres);
        return appPrefs;
    }

    public void readFrom(Preferences prefs) {
        setMenuVisibleAlways(prefs.getBoolean(KEY_MENU_VISIBLE_ALWAYS, SystemUtils.IS_OS_MAC));
        setOptimizedForScreenReader(prefs.getBoolean(KEY_OPTIMIZED_FOR_SCREEN_READER, false));
        setBodyTextMode(BodyTextMode.valueOf(prefs.get(KEY_BODY_TEXT_MODE, BodyTextMode.CLASSIC.name())));
    }

    public void storeIn(Preferences prefs) {
        prefs.putBoolean(KEY_MENU_VISIBLE_ALWAYS, isMenuVisibleAlways());
        prefs.putBoolean(KEY_OPTIMIZED_FOR_SCREEN_READER, isOptimizedForScreenReader());
        prefs.put(KEY_BODY_TEXT_MODE, getBodyTextMode().name());
    }

    public boolean isMenuVisibleAlways() {
        return this.menuVisibleAlways;
    }

    public void setMenuVisibleAlways(boolean menuVisibleAlways) {
        boolean oldValue = isMenuVisibleAlways();
        this.menuVisibleAlways = menuVisibleAlways;
        firePropertyChange(PROPERTY_MENU_VISIBLE_ALWAYS, oldValue, menuVisibleAlways);
    }

    public BodyTextMode getBodyTextMode() {
        return this.bodyTextMode;
    }

    public void setBodyTextMode(BodyTextMode mode) {
        BodyTextMode oldValue = getBodyTextMode();
        this.bodyTextMode = mode;
        firePropertyChange(PROPERTY_BODY_TEXT_MODE, oldValue, mode);
    }

    public boolean isOptimizedForScreenReader() {
        return this.optimizedForScreenReader;
    }

    public void setOptimizedForScreenReader(boolean optimized) {
        boolean oldValue = isOptimizedForScreenReader();
        this.optimizedForScreenReader = optimized;
        firePropertyChange(PROPERTY_OPTIMIZED_FOR_SCREEN_READER, oldValue, optimized);
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public BasicApplicationPreferences m96clone() {
        try {
            return (BasicApplicationPreferences) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
