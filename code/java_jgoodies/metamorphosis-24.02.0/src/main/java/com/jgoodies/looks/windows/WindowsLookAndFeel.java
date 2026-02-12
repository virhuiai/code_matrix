package com.jgoodies.looks.windows;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.swing.ScreenScaling;
import com.jgoodies.components.internal.TextFieldSupport;
import com.jgoodies.looks.FontPolicies;
import com.jgoodies.looks.FontPolicy;
import com.jgoodies.looks.FontSet;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.MicroLayout;
import com.jgoodies.looks.MicroLayoutPolicies;
import com.jgoodies.looks.MicroLayoutPolicy;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.common.MinimumSizedIcon;
import com.jgoodies.looks.common.RGBGrayFilter;
import com.jgoodies.looks.common.ShadowPopupFactory;
import java.awt.Color;
import java.awt.Insets;
import java.lang.reflect.Method;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicBorders;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsLookAndFeel.class */
public class WindowsLookAndFeel extends com.sun.java.swing.plaf.windows.WindowsLookAndFeel {
    public String getName() {
        return "JGoodies Windows";
    }

    public String getDescription() {
        return "The JGoodies Windows Look and Feel - © 2001-2024 JGoodies Software GmbH";
    }

    public static FontPolicy getFontPolicy() {
        FontPolicy policy = (FontPolicy) UIManager.get(Options.WINDOWS_FONT_POLICY_KEY);
        if (policy != null) {
            return policy;
        }
        FontPolicy defaultPolicy = FontPolicies.getDefaultWindowsPolicy();
        return FontPolicies.customSettingsPolicy(defaultPolicy);
    }

    public static void setFontPolicy(FontPolicy fontPolicy) {
        UIManager.put(Options.WINDOWS_FONT_POLICY_KEY, fontPolicy);
    }

    public static MicroLayoutPolicy getMicroLayoutPolicy() {
        MicroLayoutPolicy policy = (MicroLayoutPolicy) UIManager.get(Options.WINDOWS_MICRO_LAYOUT_POLICY_KEY);
        return policy != null ? policy : MicroLayoutPolicies.getDefaultWindowsPolicy();
    }

    public static void setMicroLayoutPolicy(MicroLayoutPolicy microLayoutPolicy) {
        UIManager.put(Options.WINDOWS_MICRO_LAYOUT_POLICY_KEY, microLayoutPolicy);
    }

    public void initialize() {
        super.initialize();
        ShadowPopupFactory.install();
    }

    public void uninitialize() {
        super.uninitialize();
        ShadowPopupFactory.uninstall();
    }

    public Icon getDisabledIcon(JComponent component, Icon icon) {
        Icon disabledIcon = RGBGrayFilter.getDisabledIcon(component, icon);
        if (disabledIcon != null) {
            return new IconUIResource(disabledIcon);
        }
        return null;
    }

    public ActionMap getAudioActionMap() {
        return super.getAudioActionMap();
    }

    protected void initClassDefaults(UIDefaults table) {
        Object[] uiDefaults;
        super.initClassDefaults(table);
        Object[] uiDefaults2 = {"ButtonUI", "com.jgoodies.looks.windows.WindowsButtonUI", "CheckBoxUI", "com.jgoodies.looks.windows.WindowsCheckBoxUI", "ComboBoxUI", "com.jgoodies.looks.windows.WindowsComboBoxUI", "ScrollPaneUI", "com.jgoodies.looks.windows.WindowsScrollPaneUI", "MenuBarUI", "com.jgoodies.looks.windows.WindowsMenuBarUI", "OptionPaneUI", "com.jgoodies.looks.windows.WindowsOptionPaneUI", "PasswordFieldUI", "com.jgoodies.looks.windows.WindowsPasswordFieldUI", "PopupMenuUI", "com.jgoodies.looks.windows.WindowsPopupMenuUI", "RadioButtonUI", "com.jgoodies.looks.windows.WindowsRadioButtonUI", "SplitPaneUI", "com.jgoodies.looks.windows.WindowsSplitPaneUI", "TabbedPaneUI", "com.jgoodies.looks.windows.WindowsTabbedPaneUI", "TextAreaUI", "com.jgoodies.looks.windows.WindowsTextAreaUI", "TextFieldUI", "com.jgoodies.looks.windows.WindowsTextFieldUI", "FormattedTextFieldUI", "com.jgoodies.looks.windows.WindowsFormattedTextFieldUI", "ToolTipUI", "com.jgoodies.looks.common.ExtBasicToolTipUI", "TreeUI", "com.jgoodies.looks.windows.WindowsTreeUI", "SeparatorUI", "com.jgoodies.looks.windows.WindowsSeparatorUI", "SpinnerUI", "com.jgoodies.looks.windows.WindowsSpinnerUI"};
        if (!SystemUtils.IS_LAF_WINDOWS_XP_ENABLED) {
            uiDefaults2 = append(append(append(append(uiDefaults2, "MenuItemUI", "com.jgoodies.looks.windows.WindowsMenuItemUI"), "CheckBoxMenuItemUI", "com.jgoodies.looks.common.ExtBasicCheckBoxMenuItemUI"), "RadioButtonMenuItemUI", "com.jgoodies.looks.common.ExtBasicRadioButtonMenuItemUI"), "PopupMenuSeparatorUI", "com.jgoodies.looks.common.ExtBasicPopupMenuSeparatorUI");
        }
        if (SystemUtils.IS_LAF_WINDOWS_XP_ENABLED) {
            uiDefaults = append(append(uiDefaults2, "ToolBarUI", "com.jgoodies.looks.windows.WindowsXPToolBarUI"), "TableHeaderUI", "com.jgoodies.looks.windows.WindowsXPTableHeaderUI");
        } else {
            uiDefaults = append(append(append(uiDefaults2, "MenuUI", "com.jgoodies.looks.common.ExtBasicMenuUI"), "ToolBarUI", "com.jgoodies.looks.windows.WindowsToolBarUI"), "ScrollBarUI", "com.jgoodies.looks.windows.WindowsScrollBarUI");
        }
        table.putDefaults(uiDefaults);
    }

    protected void initComponentDefaults(UIDefaults table) {
        Object menuBorder;
        Object obj;
        Object obj2;
        Object obj3;
        super.initComponentDefaults(table);
        boolean isXP = SystemUtils.IS_LAF_WINDOWS_XP_ENABLED;
        boolean isClassic = !isXP;
        initFontDefaults(table);
        if (isClassic) {
            initComponentDefaultsClassic(table);
        }
        MicroLayout microLayout = getMicroLayoutPolicy().getMicroLayout("Windows", table);
        if (!isXP) {
            initClassicMenuItemDefaults(table, microLayout);
        }
        Object marginBorder = new BasicBorders.MarginBorder();
        Object checkBoxMargin = microLayout.getCheckBoxMargin();
        Object etchedBorder = new UIDefaults.ProxyLazyValue("javax.swing.plaf.BorderUIResource", "getEtchedBorderUIResource");
        Object buttonBorder = new SimpleProxyLazyValue(Options.JGOODIES_WINDOWS_NAME, "getButtonBorder");
        if (isXP) {
            menuBorder = WindowsBorders.getXPMenuBorder();
        } else {
            menuBorder = WindowsBorders.getMenuBorder();
        }
        Object menuBorder2 = menuBorder;
        Object menuBarSeparatorBorder = WindowsBorders.getSeparatorBorder();
        Object menuBarEtchedBorder = WindowsBorders.getEtchedBorder();
        Object menuBarHeaderBorder = WindowsBorders.getMenuBarHeaderBorder();
        Object toolBarSeparatorBorder = WindowsBorders.getSeparatorBorder();
        Object toolBarEtchedBorder = WindowsBorders.getEtchedBorder();
        Object toolBarHeaderBorder = WindowsBorders.getToolBarHeaderBorder();
        Object buttonMargin = microLayout.getButtonMargin();
        int physicalOne = ScreenScaling.toPhysical(1);
        Object textInsets = microLayout.getTextInsets();
        Object wrappedTextInsets = microLayout.getWrappedTextInsets();
        InsetsUIResource comboBoxEditorInsets = microLayout.getComboBoxEditorInsets();
        int comboBorderSize = microLayout.getComboBorderSize();
        int comboPopupBorderSize = microLayout.getComboPopupBorderSize();
        int comboRendererGap = (((Insets) comboBoxEditorInsets).left + comboBorderSize) - comboPopupBorderSize;
        Object comboRendererBorder = new EmptyBorder(physicalOne, comboRendererGap, physicalOne, comboRendererGap);
        Object comboTableEditorInsets = new Insets(0, 0, 0, 0);
        Object popupMenuSeparatorMargin = microLayout.getPopupMenuSeparatorMargin();
        int treeFontSize = table.getFont("Tree.font").getSize();
        Integer rowHeight = Integer.valueOf(treeFontSize + (treeFontSize / 2));
        Color controlColor = table.getColor("control");
        Object disabledTextBackground = table.getColor("TextField.disabledBackground");
        Object inactiveTextBackground = table.getColor("TextField.inactiveBackground");
        Object comboBoxDisabledBackground = isXP ? table.getColor("ComboBox.background") : disabledTextBackground;
        if (isXP) {
            obj = table.get("control");
        } else {
            obj = table.get(TextFieldSupport.PROPERTY_MENU);
        }
        Object menuBarBackground = obj;
        if (isXP) {
            obj2 = table.get("MenuItem.selectionBackground");
        } else {
            obj2 = table.get("Menu.background");
        }
        Object menuSelectionBackground = obj2;
        if (isXP) {
            obj3 = table.get("MenuItem.selectionForeground");
        } else {
            obj3 = table.get("Menu.foreground");
        }
        Object menuSelectionForeground = obj3;
        Integer physicalFour = ScreenScaling.physicalInteger(4);
        InsetsUIResource tabbedPaneContentBorderInsets = ScreenScaling.physicalInsetsUIResource(2, 2, 3, 3);
        InsetsUIResource menuItemMargin = ScreenScaling.physicalInsetsUIResource(2, 2, 2, 2);
        Object[] defaultCueList = {"OptionPane.errorSound", "OptionPane.informationSound", "OptionPane.questionSound", "OptionPane.warningSound"};
        Object[] defaults = {"AuditoryCues.defaultCueList", defaultCueList, "AuditoryCues.playList", defaultCueList, "Button.border", buttonBorder, "Button.margin", buttonMargin, "Button.iconTextGap", physicalFour, "CheckBox.border", marginBorder, "CheckBox.margin", checkBoxMargin, "CheckBox.iconTextGap", physicalFour, "ComboBox.disabledBackground", comboBoxDisabledBackground, "ComboBox.editorBorder", marginBorder, "ComboBox.editorColumns", 5, "ComboBox.editorInsets", comboBoxEditorInsets, "ComboBox.tableEditorInsets", comboTableEditorInsets, "ComboBox.rendererBorder", comboRendererBorder, "EditorPane.margin", wrappedTextInsets, "Menu.border", menuBorder2, "Menu.borderPainted", Boolean.TRUE, "Menu.background", menuBarBackground, "Menu.selectionForeground", menuSelectionForeground, "Menu.selectionBackground", menuSelectionBackground, "Menu.margin", menuItemMargin, "MenuBar.background", menuBarBackground, "MenuBar.border", menuBarSeparatorBorder, "MenuBar.emptyBorder", marginBorder, "MenuBar.separatorBorder", menuBarSeparatorBorder, "MenuBar.etchedBorder", menuBarEtchedBorder, "MenuBar.headerBorder", menuBarHeaderBorder, "MenuItem.margin", menuItemMargin, "RadioButtonMenuItem.margin", menuItemMargin, "CheckBoxMenuItem.margin", menuItemMargin, "FormattedTextField.disabledBackground", disabledTextBackground, "FormattedTextField.inactiveBackground", inactiveTextBackground, "FormattedTextField.margin", textInsets, "OptionPane.buttonPadding", Integer.valueOf(microLayout.getButtonPadding()), "OptionPane.sameSizeButtons", false, "PasswordField.margin", textInsets, "PasswordField.echoChar", (char) 9679, "PopupMenu.border", WindowsBorders.getPopupMenuBorder(), "PopupMenu.noMarginBorder", WindowsBorders.getNoMarginPopupMenuBorder(), "PopupMenuSeparator.margin", popupMenuSeparatorMargin, "ProgressBar.cellLength", ScreenScaling.physicalInteger(7), "ProgressBar.cellSpacing", ScreenScaling.physicalInteger(2), "ProgressBar.indeterminateInsets", ScreenScaling.physicalInsets(3, 3, 3, 3), "ProgressBar.horizontalSize", ScreenScaling.physicalDimensionUIResource(146, 12), "ProgressBar.verticalSize", ScreenScaling.physicalDimensionUIResource(12, 146), "ScrollPane.etchedBorder", etchedBorder, "Spinner.defaultEditorInsets", textInsets, "RadioButton.border", marginBorder, "RadioButton.margin", checkBoxMargin, "RadioButton.iconTextGap", physicalFour, "Spinner.border", table.get("TextField.border"), "TabbedPane.tabInsets", new XPValue(ScreenScaling.physicalInsetsUIResource(1, 4, 1, 4), ScreenScaling.physicalInsetsUIResource(0, 4, 1, 4)), "TabbedPane.tabAreaInsets", new XPValue(ScreenScaling.physicalInsetsUIResource(3, 2, 2, 2), ScreenScaling.physicalInsetsUIResource(3, 2, 0, 2)), "TabbedPane.contentBorderInsets", tabbedPaneContentBorderInsets, "TabbedPane.textIconGap", physicalFour, "Table.gridColor", controlColor, "TextArea.margin", wrappedTextInsets, "TextArea.disabledBackground", disabledTextBackground, "TextArea.inactiveBackground", inactiveTextBackground, "TextField.margin", textInsets, "ToggleButton.margin", buttonMargin, "ToggleButton.iconTextGap", physicalFour, "ToolBar.emptyBorder", marginBorder, "ToolBar.separatorBorder", toolBarSeparatorBorder, "ToolBar.etchedBorder", toolBarEtchedBorder, "ToolBar.headerBorder", toolBarHeaderBorder, "ToolBar.separatorSize", null, "ToolBar.margin", ScreenScaling.physicalInsetsUIResource(0, 10, 0, 0), "Tree.selectionBorderColor", controlColor, "Tree.rowHeight", rowHeight, "Tree.leftChildIndent", Integer.valueOf(ScreenScaling.toPhysical(7)), "Tree.rightChildIndent", Integer.valueOf(ScreenScaling.toPhysical(13))};
        table.putDefaults(defaults);
        initComponentDefaultsMetro(table);
    }

    private static void initComponentDefaultsClassic(UIDefaults table) {
        Object checkBoxIcon = new SimpleProxyLazyValue(Options.JGOODIES_WINDOWS_NAME, "getCheckBoxIcon");
        Object radioButtonIcon = new SimpleProxyLazyValue(Options.JGOODIES_WINDOWS_NAME, "getRadioButtonIcon");
        Object[] defaults = {"CheckBox.checkColor", table.get("controlText"), "CheckBox.icon", checkBoxIcon, "RadioButton.checkColor", table.get("controlText"), "RadioButton.icon", radioButtonIcon, "Table.scrollPaneBorder", new BasicBorders.FieldBorder(table.getColor("controlShadow"), table.getColor("controlDkShadow"), table.getColor("controlHighlight"), table.getColor("controlLtHighlight"))};
        table.putDefaults(defaults);
    }

    private static void initComponentDefaultsMetro(UIDefaults table) {
        Object[] defaults = {Options.TREE_PAINT_LINES_KEY, Boolean.FALSE};
        table.putDefaults(defaults);
        if (!ScreenScaling.isScale100()) {
            return;
        }
        Object treeExpandedIcon = makeIcon(WindowsLookAndFeel.class, "icons/tree-expanded.png");
        Object treeCollapsedIcon = makeIcon(WindowsLookAndFeel.class, "icons/tree-collapsed.png");
        Object treeLeafIcon = makeIcon(WindowsLookAndFeel.class, "icons/tree-leaf.png");
        Object[] defaults2 = {"Tree.expandedIcon", treeExpandedIcon, "Tree.collapsedIcon", treeCollapsedIcon, "Tree.leafIcon", treeLeafIcon};
        table.putDefaults(defaults2);
    }

    private static void initFontDefaults(UIDefaults table) {
        FontPolicy fontPolicy = getFontPolicy();
        FontSet fontSet = fontPolicy.getFontSet("Windows", table);
        initFontDefaults(table, fontSet);
    }

    private static void initClassicMenuItemDefaults(UIDefaults table, MicroLayout microLayout) {
        Object menuMargin = microLayout.getMenuMargin();
        Object menuItemMargin = microLayout.getMenuItemMargin();
        Icon menuItemCheckIcon = new MinimumSizedIcon();
        Object[] defaults = {"Menu.margin", menuMargin, "MenuItem.borderPainted", Boolean.TRUE, "MenuItem.checkIcon", menuItemCheckIcon, "MenuItem.margin", menuItemMargin, "CheckBoxMenuItem.margin", menuItemMargin, "RadioButtonMenuItem.margin", menuItemMargin};
        table.putDefaults(defaults);
    }

    private static void initFontDefaults(UIDefaults table, FontSet fontSet) {
        FontUIResource controlFont = fontSet.getControlFont();
        FontUIResource menuFont = fontSet.getMenuFont();
        FontUIResource messageFont = fontSet.getMessageFont();
        FontUIResource smallFont = fontSet.getSmallFont();
        Object[] defaults = {"Button.font", controlFont, "CheckBox.font", controlFont, "ColorChooser.font", controlFont, "ComboBox.font", controlFont, "EditorPane.font", controlFont, "FormattedTextField.font", controlFont, "Label.font", controlFont, "List.font", controlFont, "OptionPane.buttonFont", controlFont, "Panel.font", controlFont, "PasswordField.font", controlFont, "ProgressBar.font", controlFont, "RadioButton.font", controlFont, "ScrollPane.font", controlFont, "Slider.font", controlFont, "Spinner.font", controlFont, "TabbedPane.font", controlFont, "Table.font", controlFont, "TableHeader.font", controlFont, "TextArea.font", controlFont, "TextField.font", controlFont, "TextPane.font", controlFont, "ToolBar.font", controlFont, "ToggleButton.font", controlFont, "Tree.font", controlFont, "Viewport.font", controlFont, "InternalFrame.titleFont", fontSet.getWindowTitleFont(), "OptionPane.messageFont", messageFont, "TitledBorder.font", fontSet.getTitleFont(), "ToolTip.font", smallFont, "CheckBoxMenuItem.font", menuFont, "CheckBoxMenuItem.acceleratorFont", null, "Menu.font", menuFont, "Menu.acceleratorFont", null, "MenuBar.font", menuFont, "MenuItem.font", menuFont, "MenuItem.acceleratorFont", null, "PopupMenu.font", menuFont, "RadioButtonMenuItem.font", menuFont, "RadioButtonMenuItem.acceleratorFont", null};
        table.putDefaults(defaults);
    }

    public static Border getButtonBorder() {
        return WindowsBorders.getButtonBorder();
    }

    public static Icon getCheckBoxIcon() {
        return WindowsIconFactory.getCheckBoxIcon();
    }

    public static Icon getRadioButtonIcon() {
        return WindowsIconFactory.getRadioButtonIcon();
    }

    private static Object[] append(Object[] source, String key, Object value) {
        int length = source.length;
        Object[] destination = new Object[length + 2];
        System.arraycopy(source, 0, destination, 0, length);
        destination[length] = key;
        destination[length + 1] = value;
        return destination;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsLookAndFeel$SimpleProxyLazyValue.class */
    public static final class SimpleProxyLazyValue implements UIDefaults.LazyValue {
        private final String className;
        private final String methodName;

        SimpleProxyLazyValue(String c, String m) {
            this.className = c;
            this.methodName = m;
        }

        public Object createValue(UIDefaults table) {
            ClassLoader contextClassLoader;
            Object instance = null;
            try {
                if (table != null) {
                    contextClassLoader = (ClassLoader) table.get("ClassLoader");
                } else {
                    contextClassLoader = Thread.currentThread().getContextClassLoader();
                }
                ClassLoader classLoader = contextClassLoader;
                if (classLoader == null) {
                    classLoader = getClass().getClassLoader();
                }
                Class<?> c = Class.forName(this.className, true, classLoader);
                Method m = c.getMethod(this.methodName, (Class[]) null);
                instance = m.invoke(c, (Object[]) null);
            } catch (Throwable t) {
                LookUtils.log("Problem creating " + this.className + " with method " + this.methodName + t);
            }
            return instance;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsLookAndFeel$XPValue.class */
    private static final class XPValue implements UIDefaults.ActiveValue {
        protected Object classicValue;
        protected Object xpValue;
        private static final Object NULL_VALUE = new Object();

        XPValue(Object xpValue, Object classicValue) {
            this.xpValue = xpValue;
            this.classicValue = classicValue;
        }

        public Object createValue(UIDefaults table) {
            Object value = null;
            if (SystemUtils.IS_LAF_WINDOWS_XP_ENABLED) {
                value = getXPValue(table);
            }
            if (value == null) {
                value = getClassicValue(table);
            } else if (value == NULL_VALUE) {
                value = null;
            }
            return value;
        }

        protected Object getXPValue(UIDefaults table) {
            return recursiveCreateValue(this.xpValue, table);
        }

        protected Object getClassicValue(UIDefaults table) {
            return recursiveCreateValue(this.classicValue, table);
        }

        private static Object recursiveCreateValue(Object value, UIDefaults table) {
            if (value instanceof UIDefaults.LazyValue) {
                value = ((UIDefaults.LazyValue) value).createValue(table);
            }
            if (value instanceof UIDefaults.ActiveValue) {
                return ((UIDefaults.ActiveValue) value).createValue(table);
            }
            return value;
        }
    }
}
