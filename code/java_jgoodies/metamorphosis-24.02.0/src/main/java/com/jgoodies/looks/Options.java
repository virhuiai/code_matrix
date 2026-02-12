package com.jgoodies.looks;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.looks.common.ShadowPopup;
import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/Options.class */
public final class Options {
    public static final String PLASTIC_NAME = "com.jgoodies.looks.plastic.PlasticLookAndFeel";
    public static final String PLASTIC_AERO_NAME = "com.jgoodies.looks.plastic.PlasticAeroLookAndFeel";
    public static final String PLASTIC_METRO_NAME = "com.jgoodies.looks.plastic.PlasticMetroLookAndFeel";
    public static final String JGOODIES_WINDOWS_NAME = "com.jgoodies.looks.windows.WindowsLookAndFeel";
    public static final String DEFAULT_LOOK_NAME = "com.jgoodies.looks.plastic.PlasticMetroLookAndFeel";
    public static final String PLASTIC_FONT_POLICY_KEY = "Plastic.fontPolicy";
    public static final String PLASTIC_CONTROL_FONT_KEY = "Plastic.controlFont";
    public static final String PLASTIC_MENU_FONT_KEY = "Plastic.menuFont";
    public static final String WINDOWS_FONT_POLICY_KEY = "Windows.fontPolicy";
    public static final String WINDOWS_CONTROL_FONT_KEY = "Windows.controlFont";
    public static final String WINDOWS_MENU_FONT_KEY = "Windows.menuFont";
    public static final String USE_SYSTEM_FONTS_APP_KEY = "Application.useSystemFontSettings";
    public static final String PLASTIC_MICRO_LAYOUT_POLICY_KEY = "Plastic.MicroLayoutPolicy";
    public static final String WINDOWS_MICRO_LAYOUT_POLICY_KEY = "Windows.MicroLayoutPolicy";
    public static final String DEFAULT_ICON_SIZE_KEY = "jgoodies.defaultIconSize";
    public static final String HI_RES_GRAY_FILTER_ENABLED_KEY = "HiResGrayFilterEnabled";
    public static final String HI_RES_GRAY_FILTER_ALPHA_FACTOR_KEY = "HiResGrayFilterAlphaFactor";
    public static final String SELECT_ON_FOCUS_GAIN_KEY = "JGoodies.selectAllOnFocusGain";
    public static final String LARGER_BODY_TEXT_ENABLED_KEY = "JGoodies.largerBodyTextEnabled";
    public static final String TREE_PAINT_LINES_KEY = "Tree.paintLines";
    public static final String IS_ETCHED_KEY = "jgoodies.isEtched";
    public static final String NO_ICONS_KEY = "jgoodies.noIcons";
    public static final String NO_MARGIN_KEY = "JPopupMenu.noMargin";
    public static final String COMBO_POPUP_PROTOTYPE_DISPLAY_VALUE_KEY = "ComboBox.popupPrototypeDisplayValue";
    public static final String COMBO_RENDERER_IS_BORDER_REMOVABLE = "isBorderRemovable";
    public static final String HI_RES_DISABLED_ICON_CLIENT_KEY = "generateHiResDisabledIcon";
    public static final String SELECT_ON_FOCUS_GAIN_CLIENT_KEY = "JGoodies.selectAllOnFocusGain";
    public static final String INVERT_SELECTION_CLIENT_KEY = "JGoodies.invertSelection";
    public static final String SET_CARET_TO_START_ON_FOCUS_LOST_CLIENT_KEY = "JGoodies.setCaretToStartOnFocusLost";
    public static final String USE_SYSTEM_FONTS_KEY = "swing.useSystemFontSettings";
    private static final Boolean USE_SYSTEM_FONTS_SYSTEM_VALUE = LookUtils.getBooleanSystemProperty(USE_SYSTEM_FONTS_KEY, "Use system fonts");
    public static final String POPUP_DROP_SHADOW_ENABLED_KEY = "jgoodies.popupDropShadowEnabled";
    private static final Boolean POPUP_DROP_SHADOW_ENABLED_SYSTEM_VALUE = LookUtils.getBooleanSystemProperty(POPUP_DROP_SHADOW_ENABLED_KEY, "Popup drop shadows");
    private static final Dimension DEFAULT_ICON_SIZE = new Dimension(20, 20);

    private Options() {
    }

    public static boolean getUseSystemFonts() {
        if (USE_SYSTEM_FONTS_SYSTEM_VALUE != null) {
            return USE_SYSTEM_FONTS_SYSTEM_VALUE.booleanValue();
        }
        return !Boolean.FALSE.equals(UIManager.get(USE_SYSTEM_FONTS_APP_KEY));
    }

    public static void setUseSystemFonts(boolean useSystemFonts) {
        UIManager.put(USE_SYSTEM_FONTS_APP_KEY, Boolean.valueOf(useSystemFonts));
    }

    public static Dimension getDefaultIconSize() {
        Dimension size = UIManager.getDimension(DEFAULT_ICON_SIZE_KEY);
        return size == null ? DEFAULT_ICON_SIZE : size;
    }

    public static void setDefaultIconSize(Dimension defaultIconSize) {
        UIManager.put(DEFAULT_ICON_SIZE_KEY, defaultIconSize);
    }

    public static boolean isPopupDropShadowActive() {
        return !LookUtils.getToolkitUsesNativeDropShadows() && ShadowPopup.canSnapshot() && isPopupDropShadowEnabled();
    }

    public static boolean isPopupDropShadowEnabled() {
        if (POPUP_DROP_SHADOW_ENABLED_SYSTEM_VALUE != null) {
            return POPUP_DROP_SHADOW_ENABLED_SYSTEM_VALUE.booleanValue();
        }
        Object value = UIManager.get(POPUP_DROP_SHADOW_ENABLED_KEY);
        if (value == null) {
            return isPopupDropShadowEnabledDefault();
        }
        return Boolean.TRUE.equals(value);
    }

    public static void setPopupDropShadowEnabled(boolean b) {
        UIManager.put(POPUP_DROP_SHADOW_ENABLED_KEY, Boolean.valueOf(b));
    }

    private static boolean isPopupDropShadowEnabledDefault() {
        return true;
    }

    public static boolean isHiResGrayFilterEnabled() {
        return !Boolean.FALSE.equals(UIManager.get(HI_RES_GRAY_FILTER_ENABLED_KEY));
    }

    public static void setHiResGrayFilterEnabled(boolean b) {
        UIManager.put(HI_RES_GRAY_FILTER_ENABLED_KEY, Boolean.valueOf(b));
    }

    public static float getHiResGrayFilterAlphaFactor() {
        Object value = UIManager.get(HI_RES_GRAY_FILTER_ALPHA_FACTOR_KEY);
        if (!(value instanceof Float)) {
            return 1.0f;
        }
        float val = ((Float) value).floatValue();
        if (val > 0.0f && val <= 1.0f) {
            return val;
        }
        return 1.0f;
    }

    public static void setHiResGrayFilterAlphaFactor(float factor) {
        Preconditions.checkArgument(factor > 0.0f && factor <= 1.0f, "The hi-res gray filter alpha factor must be in (0, 1.0f].");
        UIManager.put(HI_RES_GRAY_FILTER_ALPHA_FACTOR_KEY, Float.valueOf(factor));
    }

    public static boolean isSelectOnFocusGainEnabled() {
        return !Boolean.FALSE.equals(UIManager.get("JGoodies.selectAllOnFocusGain"));
    }

    public static void setSelectOnFocusGainEnabled(boolean b) {
        UIManager.put("JGoodies.selectAllOnFocusGain", Boolean.valueOf(b));
    }

    public static boolean isSelectOnFocusGainActive(JTextComponent c) {
        Boolean enabled = getSelectOnFocusGainEnabled(c);
        if (enabled != null) {
            return enabled.booleanValue();
        }
        return isSelectOnFocusGainEnabled();
    }

    public static Boolean getSelectOnFocusGainEnabled(JTextComponent c) {
        return (Boolean) c.getClientProperty("JGoodies.selectAllOnFocusGain");
    }

    public static void setSelectOnFocusGainEnabled(JTextArea area, Boolean b) {
        area.putClientProperty("JGoodies.selectAllOnFocusGain", b);
    }

    public static void setSelectOnFocusGainEnabled(JTextField field, Boolean b) {
        field.putClientProperty("JGoodies.selectAllOnFocusGain", b);
    }

    public static boolean isLargerBodyTextEnabled() {
        return Boolean.TRUE.equals(UIManager.get(LARGER_BODY_TEXT_ENABLED_KEY));
    }

    public static void setLargerBodyTextEnabled(boolean b) {
        UIManager.put(LARGER_BODY_TEXT_ENABLED_KEY, Boolean.valueOf(b));
    }

    public static String getCrossPlatformLookAndFeelClassName() {
        return "com.jgoodies.looks.plastic.PlasticMetroLookAndFeel";
    }

    public static String getSystemLookAndFeelClassName() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return JGOODIES_WINDOWS_NAME;
        }
        if (SystemUtils.IS_OS_MAC) {
            return UIManager.getSystemLookAndFeelClassName();
        }
        return getCrossPlatformLookAndFeelClassName();
    }
}
