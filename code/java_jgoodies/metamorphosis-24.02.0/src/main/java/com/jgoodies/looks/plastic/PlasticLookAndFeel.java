package com.jgoodies.looks.plastic;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.swing.ScreenScaling;
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
import com.jgoodies.looks.plastic.PlasticModernBorders;
import com.jgoodies.looks.plastic.theme.SkyBluer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalLookAndFeel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticLookAndFeel.class */
public class PlasticLookAndFeel extends MetalLookAndFeel {
    public static final String IS_3D_KEY = "Plastic.is3D";
    public static final String DEFAULT_THEME_KEY = "Plastic.defaultTheme";
    private static List<PlasticTheme> installedThemes;
    static PropertyChangeListener focusOwnerTracker;
    private static final String THEME_CLASSNAME_PREFIX = "com.jgoodies.looks.plastic.theme.";

    public PlasticLookAndFeel() {
        getPlasticTheme();
    }

    public String getID() {
        return "Plastic";
    }

    public String getName() {
        return "JGoodies Plastic";
    }

    public String getDescription() {
        return "The JGoodies Plastic Look and Feel";
    }

    public static FontPolicy getFontPolicy() {
        FontPolicy policy = (FontPolicy) UIManager.get(Options.PLASTIC_FONT_POLICY_KEY);
        if (policy != null) {
            return policy;
        }
        FontPolicy defaultPolicy = FontPolicies.getDefaultPlasticPolicy();
        return FontPolicies.customSettingsPolicy(defaultPolicy);
    }

    public static void setFontPolicy(FontPolicy fontPolicy) {
        UIManager.put(Options.PLASTIC_FONT_POLICY_KEY, fontPolicy);
    }

    public static MicroLayoutPolicy getMicroLayoutPolicy() {
        MicroLayoutPolicy policy = (MicroLayoutPolicy) UIManager.get(Options.PLASTIC_MICRO_LAYOUT_POLICY_KEY);
        return policy != null ? policy : MicroLayoutPolicies.getDefaultPlasticPolicy();
    }

    public static void setMicroLayoutPolicy(MicroLayoutPolicy microLayoutPolicy) {
        UIManager.put(Options.PLASTIC_MICRO_LAYOUT_POLICY_KEY, microLayoutPolicy);
    }

    protected boolean is3DEnabled() {
        return false;
    }

    public void initialize() {
        super.initialize();
        ShadowPopupFactory.install();
        focusOwnerTracker = new FocusOwnerTracker();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", focusOwnerTracker);
    }

    public void uninitialize() {
        super.uninitialize();
        ShadowPopupFactory.uninstall();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("focusOwner", focusOwnerTracker);
        focusOwnerTracker = null;
    }

    public Icon getDisabledIcon(JComponent component, Icon icon) {
        Icon disabledIcon = RGBGrayFilter.getDisabledIcon(component, icon);
        if (disabledIcon != null) {
            return new IconUIResource(disabledIcon);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        Object[] uiDefaults = {"ButtonUI", "com.jgoodies.looks.plastic.PlasticButtonUI", "ToggleButtonUI", "com.jgoodies.looks.plastic.PlasticToggleButtonUI", "CheckBoxUI", "com.jgoodies.looks.plastic.PlasticCheckBoxUI", "RadioButtonUI", "com.jgoodies.looks.plastic.PlasticRadioButtonUI", "ComboBoxUI", "com.jgoodies.looks.plastic.PlasticComboBoxUI", "ScrollBarUI", "com.jgoodies.looks.plastic.PlasticScrollBarUI", "SpinnerUI", "com.jgoodies.looks.plastic.PlasticSpinnerUI", "MenuBarUI", "com.jgoodies.looks.plastic.PlasticMenuBarUI", "ToolBarUI", "com.jgoodies.looks.plastic.PlasticToolBarUI", "MenuUI", "com.jgoodies.looks.plastic.PlasticMenuUI", "MenuItemUI", "com.jgoodies.looks.common.ExtBasicMenuItemUI", "CheckBoxMenuItemUI", "com.jgoodies.looks.common.ExtBasicCheckBoxMenuItemUI", "RadioButtonMenuItemUI", "com.jgoodies.looks.common.ExtBasicRadioButtonMenuItemUI", "PopupMenuUI", "com.jgoodies.looks.plastic.PlasticPopupMenuUI", "PopupMenuSeparatorUI", "com.jgoodies.looks.common.ExtBasicPopupMenuSeparatorUI", "OptionPaneUI", "com.jgoodies.looks.plastic.PlasticOptionPaneUI", "ScrollPaneUI", "com.jgoodies.looks.plastic.PlasticScrollPaneUI", "SplitPaneUI", "com.jgoodies.looks.plastic.PlasticSplitPaneUI", "PasswordFieldUI", "com.jgoodies.looks.plastic.PlasticPasswordFieldUI", "TextAreaUI", "com.jgoodies.looks.plastic.PlasticTextAreaUI", "TreeUI", "com.jgoodies.looks.plastic.PlasticTreeUI", "InternalFrameUI", "com.jgoodies.looks.plastic.PlasticInternalFrameUI", "SeparatorUI", "com.jgoodies.looks.plastic.PlasticSeparatorUI", "ToolBarSeparatorUI", "com.jgoodies.looks.plastic.PlasticToolBarSeparatorUI", "FileChooserUI", "com.jgoodies.looks.plastic.PlasticFileChooserUI", "TabbedPaneUI", "com.jgoodies.looks.plastic.PlasticTabbedPaneUI", "TextFieldUI", "com.jgoodies.looks.plastic.PlasticTextFieldUI", "FormattedTextFieldUI", "com.jgoodies.looks.plastic.PlasticFormattedTextFieldUI"};
        table.putDefaults(uiDefaults);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        MicroLayout microLayout = getMicroLayoutPolicy().getMicroLayout(getName(), table);
        Insets buttonBorderInsets = microLayout.getButtonBorderInsets();
        Object marginBorder = new BasicBorders.MarginBorder();
        Object buttonBorder = PlasticBorders.getButtonBorder(buttonBorderInsets);
        Object comboBoxButtonBorder = PlasticBorders.getComboBoxArrowButtonBorder();
        Border comboBoxEditorBorder = PlasticBorders.getComboBoxEditorBorder();
        Object menuItemBorder = PlasticBorders.getMenuItemBorder();
        Object textFieldBorder = PlasticBorders.getTextFieldBorder();
        Object toggleButtonBorder = PlasticBorders.getToggleButtonBorder(buttonBorderInsets);
        Object scrollPaneBorder = PlasticBorders.getScrollPaneBorder();
        Object tableHeaderBorder = new BorderUIResource(table.getBorder("TableHeader.cellBorder"));
        Object menuBarSeparatorBorder = PlasticBorders.getSeparatorBorder();
        Object menuBarEtchedBorder = PlasticBorders.getEtchedBorder();
        Object menuBarHeaderBorder = PlasticBorders.getMenuBarHeaderBorder();
        Object toolBarSeparatorBorder = PlasticBorders.getSeparatorBorder();
        Object toolBarEtchedBorder = PlasticBorders.getEtchedBorder();
        Object toolBarHeaderBorder = PlasticBorders.getToolBarHeaderBorder();
        Object internalFrameBorder = getInternalFrameBorder();
        Object paletteBorder = getPaletteBorder();
        Color controlColor = table.getColor("control");
        Object focusColor = getFocusColor();
        Object checkBoxIcon = PlasticIconFactory.getCheckBoxIcon();
        Object checkBoxMargin = microLayout.getCheckBoxMargin();
        Object buttonMargin = microLayout.getButtonMargin();
        Object textInsets = microLayout.getTextInsets();
        Object wrappedTextInsets = microLayout.getWrappedTextInsets();
        InsetsUIResource comboBoxEditorInsets = microLayout.getComboBoxEditorInsets();
        Insets comboEditorBorderInsets = comboBoxEditorBorder.getBorderInsets((Component) null);
        int comboBorderSize = comboEditorBorderInsets.left;
        int comboPopupBorderSize = microLayout.getComboPopupBorderSize();
        int comboRendererGap = (((Insets) comboBoxEditorInsets).left + comboBorderSize) - comboPopupBorderSize;
        Object comboRendererBorder = new EmptyBorder(1, comboRendererGap, 1, comboRendererGap);
        Object comboTableEditorInsets = new Insets(0, 0, 0, 0);
        Object focusInsets = new Insets(3, 3, 4, 4);
        Object menuItemMargin = microLayout.getMenuItemMargin();
        Object menuMargin = microLayout.getMenuMargin();
        Icon menuItemCheckIcon = new MinimumSizedIcon();
        Icon checkBoxMenuItemIcon = PlasticIconFactory.getCheckBoxMenuItemIcon();
        Icon radioButtonMenuItemIcon = PlasticIconFactory.getRadioButtonMenuItemIcon();
        Color menuItemForeground = table.getColor("MenuItem.foreground");
        Color inactiveTextBackground = table.getColor("TextField.inactiveBackground");
        int treeFontSize = table.getFont("Tree.font").getSize();
        Integer rowHeight = Integer.valueOf(treeFontSize + (treeFontSize / 2));
        Object treeExpandedIcon = PlasticIconFactory.getExpandedTreeIcon();
        Object treeCollapsedIcon = PlasticIconFactory.getCollapsedTreeIcon();
        ColorUIResource gray = new ColorUIResource(Color.GRAY);
        Boolean is3D = Boolean.valueOf(is3DEnabled());
        Character passwordEchoChar = Character.valueOf(SystemUtils.IS_OS_WINDOWS ? (char) 9679 : (char) 8226);
        String iconPrefix = "icons/" + (ScreenScaling.isScale100() ? "32x32/" : "48x48/");
        Object errorIcon = makeIcon(PlasticLookAndFeel.class, iconPrefix + "dialog-error.png");
        Object informationIcon = makeIcon(PlasticLookAndFeel.class, iconPrefix + "dialog-information.png");
        Object helpIcon = makeIcon(PlasticLookAndFeel.class, iconPrefix + "dialog-help.png");
        Object warningIcon = makeIcon(PlasticLookAndFeel.class, iconPrefix + "dialog-warning.png");
        Integer physicalFour = ScreenScaling.physicalInteger(4);
        FontSet fonts = getFontPolicy().getFontSet("Plastic", table);
        Object[] defaults = {"Button.border", buttonBorder, "Button.margin", buttonMargin, "Button.defaultButtonFollowsFocus", Boolean.TRUE, "Button.borderPaintsFocus", Boolean.FALSE, "Button.focus", focusColor, "Button.focusInsets", focusInsets, "Button.iconTextGap", physicalFour, "CheckBox.margin", checkBoxMargin, "CheckBox.focus", focusColor, "CheckBox.icon", checkBoxIcon, "CheckBox.iconTextGap", physicalFour, "CheckBoxMenuItem.border", menuItemBorder, "CheckBoxMenuItem.margin", menuItemMargin, "CheckBoxMenuItem.checkIcon", checkBoxMenuItemIcon, "CheckBoxMenuItem.background", getMenuItemBackground(), "CheckBoxMenuItem.selectionForeground", getMenuItemSelectedForeground(), "CheckBoxMenuItem.selectionBackground", getMenuItemSelectedBackground(), "CheckBoxMenuItem.acceleratorForeground", menuItemForeground, "CheckBoxMenuItem.acceleratorSelectionForeground", getMenuItemSelectedForeground(), "CheckBoxMenuItem.acceleratorSelectionBackground", getMenuItemSelectedBackground(), "ComboBox.selectionForeground", getMenuSelectedForeground(), "ComboBox.selectionBackground", getMenuSelectedBackground(), "ComboBox.arrowIcon", PlasticIconFactory.getComboBoxButtonIcon(), "ComboBox.arrowButtonBorder", comboBoxButtonBorder, "ComboBox.editorBorder", comboBoxEditorBorder, "ComboBox.editorColumns", 5, "ComboBox.editorBorderInsets", comboEditorBorderInsets, "ComboBox.editorInsets", textInsets, "ComboBox.tableEditorInsets", comboTableEditorInsets, "ComboBox.rendererBorder", comboRendererBorder, "ComboBox.borderPaintsFocus", Boolean.FALSE, "ComboBox.focus", focusColor, "ComboBox.focusInsets", focusInsets, "EditorPane.margin", wrappedTextInsets, "InternalFrame.border", internalFrameBorder, "InternalFrame.paletteBorder", paletteBorder, "List.font", getControlTextFont(), "Menu.border", PlasticBorders.getMenuBorder(), "Menu.margin", menuMargin, "Menu.arrowIcon", PlasticIconFactory.getMenuArrowIcon(), "MenuBar.border", menuBarSeparatorBorder, "MenuBar.emptyBorder", marginBorder, "MenuBar.separatorBorder", menuBarSeparatorBorder, "MenuBar.etchedBorder", menuBarEtchedBorder, "MenuBar.headerBorder", menuBarHeaderBorder, "MenuItem.border", menuItemBorder, "MenuItem.checkIcon", menuItemCheckIcon, "MenuItem.margin", menuItemMargin, "MenuItem.background", getMenuItemBackground(), "MenuItem.selectionForeground", getMenuItemSelectedForeground(), "MenuItem.selectionBackground", getMenuItemSelectedBackground(), "MenuItem.acceleratorForeground", menuItemForeground, "MenuItem.acceleratorSelectionForeground", getMenuItemSelectedForeground(), "MenuItem.acceleratorSelectionBackground", getMenuItemSelectedBackground(), "OptionPane.errorIcon", errorIcon, "OptionPane.informationIcon", informationIcon, "OptionPane.questionIcon", helpIcon, "OptionPane.warningIcon", warningIcon, "OptionPane.buttonPadding", Integer.valueOf(microLayout.getButtonPadding()), "OptionPane.buttonMinimumWidth", Integer.valueOf(ScreenScaling.toPhysical(75)), "OptionPane.sameSizeButtons", false, "OptionPane.messageFont", fonts.getMessageFont(), "FileView.computerIcon", makeIcon(PlasticLookAndFeel.class, "icons/Computer.gif"), "FileView.directoryIcon", makeIcon(PlasticLookAndFeel.class, "icons/TreeClosed.gif"), "FileView.fileIcon", makeIcon(PlasticLookAndFeel.class, "icons/File.gif"), "FileView.floppyDriveIcon", makeIcon(PlasticLookAndFeel.class, "icons/FloppyDrive.gif"), "FileView.hardDriveIcon", makeIcon(PlasticLookAndFeel.class, "icons/HardDrive.gif"), "FileChooser.homeFolderIcon", makeIcon(PlasticLookAndFeel.class, "icons/HomeFolder.gif"), "FileChooser.newFolderIcon", makeIcon(PlasticLookAndFeel.class, "icons/NewFolder.gif"), "FileChooser.upFolderIcon", makeIcon(PlasticLookAndFeel.class, "icons/UpFolder.gif"), "FileChooser.useSystemIcons", Boolean.TRUE, "TabbedPane.focus", focusColor, "FormattedTextField.border", textFieldBorder, "FormattedTextField.margin", textInsets, "PasswordField.border", textFieldBorder, "PasswordField.margin", textInsets, "PasswordField.echoChar", passwordEchoChar, "PopupMenu.border", PlasticBorders.getPopupMenuBorder(), "PopupMenu.noMarginBorder", PlasticBorders.getNoMarginPopupMenuBorder(), "PopupMenuSeparator.margin", ScreenScaling.physicalInsetsUIResource(3, 4, 3, 4), "RadioButton.margin", checkBoxMargin, "RadioButton.focus", focusColor, "RadioButton.iconTextGap", physicalFour, "RadioButtonMenuItem.border", menuItemBorder, "RadioButtonMenuItem.checkIcon", radioButtonMenuItemIcon, "RadioButtonMenuItem.margin", menuItemMargin, "RadioButtonMenuItem.background", getMenuItemBackground(), "RadioButtonMenuItem.selectionForeground", getMenuItemSelectedForeground(), "RadioButtonMenuItem.selectionBackground", getMenuItemSelectedBackground(), "RadioButtonMenuItem.acceleratorForeground", menuItemForeground, "RadioButtonMenuItem.acceleratorSelectionForeground", getMenuItemSelectedForeground(), "RadioButtonMenuItem.acceleratorSelectionBackground", getMenuItemSelectedBackground(), "Separator.foreground", getControlDarkShadow(), "ScrollPane.border", scrollPaneBorder, "ScrollPane.etchedBorder", scrollPaneBorder, "ScrollBar.width", ScreenScaling.physicalInteger(17), "SimpleInternalFrame.activeTitleForeground", getSimpleInternalFrameForeground(), "SimpleInternalFrame.activeTitleBackground", getSimpleInternalFrameBackground(), "Slider.focus", focusColor, "Slider.trackWidth", ScreenScaling.physicalInteger(7), "Slider.majorTickLength", ScreenScaling.physicalInteger(6), "Spinner.border", PlasticBorders.getFlush3DBorder(), "Spinner.defaultEditorInsets", textInsets, "SplitPane.dividerSize", ScreenScaling.physicalInteger(7), "TabbedPane.focus", getFocusColor(), "TabbedPane.tabAreaInsets", ScreenScaling.physicalInsetsUIResource(4, 2, 0, 6), "TabbedPane.tabInsets", ScreenScaling.physicalInsetsUIResource(1, 4, 1, 4), "TabbedPane.textIconGap", physicalFour, "TabbedPane.highlight", table.get("controlShadow"), "TabbedPane.shadow", table.get("controlShadow"), "TabbedPane.darkShadow", table.getColor("controlLtHighlight"), "TabbedPane.background", table.getColor("Button.background"), "Table.foreground", table.get("textText"), "Table.gridColor", controlColor, "Table.scrollPaneBorder", scrollPaneBorder, "TableHeader.cellBorder", tableHeaderBorder, "TextArea.inactiveBackground", inactiveTextBackground, "TextArea.margin", wrappedTextInsets, "TextField.border", textFieldBorder, "TextField.margin", textInsets, "TitledBorder.font", getTitleTextFont(), "TitledBorder.titleColor", getTitleTextColor(), "TextEditor.borderPaintsFocus", Boolean.FALSE, "ToggleButton.border", toggleButtonBorder, "ToggleButton.margin", buttonMargin, "ToggleButton.borderPaintsFocus", Boolean.FALSE, "ToggleButton.focus", focusColor, "ToggleButton.focusInsets", focusInsets, "ToggleButton.iconTextGap", physicalFour, "ToolBar.emptyBorder", marginBorder, "ToolBar.separatorBorder", toolBarSeparatorBorder, "ToolBar.etchedBorder", toolBarEtchedBorder, "ToolBar.headerBorder", toolBarHeaderBorder, "ToolTip.hideAccelerator", Boolean.TRUE, "Tree.expandedIcon", treeExpandedIcon, "Tree.collapsedIcon", treeCollapsedIcon, "Tree.line", gray, "Tree.hash", gray, "Tree.rowHeight", rowHeight, "Tree.closedIcon", makeIcon(PlasticLookAndFeel.class, "icons/TreeClosed.gif"), "Tree.openIcon", makeIcon(PlasticLookAndFeel.class, "icons/TreeOpen.gif"), "Tree.leafIcon", makeIcon(PlasticLookAndFeel.class, "icons/tree-leaf.png"), "Button.is3DEnabled", is3D, "ComboBox.is3DEnabled", is3D, "MenuBar.is3DEnabled", is3D, "ToolBar.is3DEnabled", is3D, "ScrollBar.is3DEnabled", is3D, "ToggleButton.is3DEnabled", is3D, "CheckBox.border", marginBorder, "RadioButton.border", marginBorder, "ProgressBar.selectionForeground", getSystemTextColor(), "ProgressBar.selectionBackground", getSystemTextColor()};
        table.putDefaults(defaults);
    }

    protected void initSystemColorDefaults(UIDefaults table) {
        super.initSystemColorDefaults(table);
        table.put("unifiedControlShadow", table.getColor("controlDkShadow"));
        table.put("primaryControlHighlight", getPrimaryControlHighlight());
    }

    public static PlasticTheme createMyDefaultTheme() {
        String defaultName;
        if (SystemUtils.IS_LAF_WINDOWS_XP_ENABLED) {
            defaultName = getDefaultWindowsTheme();
        } else {
            defaultName = "LightGray";
        }
        String userName = LookUtils.getSystemProperty(DEFAULT_THEME_KEY, "");
        boolean overridden = userName.length() > 0;
        String themeName = overridden ? userName : defaultName;
        PlasticTheme theme = createTheme(themeName);
        PlasticTheme result = theme != null ? theme : new SkyBluer();
        if (overridden) {
            String className = result.getClass().getName().substring(THEME_CLASSNAME_PREFIX.length());
            if (className.equals(userName)) {
                LookUtils.log("I have successfully installed the '" + result.getName() + "' theme.");
            } else {
                LookUtils.log("I could not install the Plastic theme '" + userName + "'.");
                LookUtils.log("I have installed the '" + result.getName() + "' theme, instead.");
            }
        }
        return result;
    }

    private static String getDefaultWindowsTheme() {
        return "LightBlue";
    }

    public static List<PlasticTheme> getInstalledThemes() {
        if (null == installedThemes) {
            installDefaultThemes();
        }
        Collections.sort(installedThemes, (o1, o2) -> {
            return o1.getName().compareTo(o2.getName());
        });
        return installedThemes;
    }

    protected static void installDefaultThemes() {
        installedThemes = new ArrayList();
        String[] themeNames = {"BrownSugar", "DarkStar", "DesertBlue", "DesertBluer", "ExperienceBlue", "ExperienceRoyale", "LightBlue", "LightGray", "SkyBlue", "SkyBluer"};
        for (int i = themeNames.length - 1; i >= 0; i--) {
            installTheme(createTheme(themeNames[i]));
        }
    }

    protected static PlasticTheme createTheme(String themeName) {
        String className = THEME_CLASSNAME_PREFIX + themeName;
        try {
            return (PlasticTheme) Class.forName(className).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            LookUtils.log("Can't create theme " + className);
            return null;
        }
    }

    public static void installTheme(PlasticTheme theme) {
        if (null == installedThemes) {
            installDefaultThemes();
        }
        installedThemes.add(theme);
    }

    public static PlasticTheme getPlasticTheme() {
        PlasticTheme currentTheme = getCurrentTheme();
        if (currentTheme instanceof PlasticTheme) {
            return currentTheme;
        }
        PlasticTheme initialTheme = createMyDefaultTheme();
        setPlasticTheme(initialTheme);
        return initialTheme;
    }

    public static void setPlasticTheme(PlasticTheme theme) {
        setCurrentTheme(theme);
    }

    public static BorderUIResource getInternalFrameBorder() {
        return new BorderUIResource(PlasticBorders.getInternalFrameBorder());
    }

    public static BorderUIResource getPaletteBorder() {
        return new BorderUIResource(PlasticBorders.getPaletteBorder());
    }

    public static ColorUIResource getPrimaryControlDarkShadow() {
        return getPlasticTheme().getPrimaryControlDarkShadow();
    }

    public static ColorUIResource getPrimaryControlHighlight() {
        return getPlasticTheme().getPrimaryControlHighlight();
    }

    public static ColorUIResource getPrimaryControlInfo() {
        return getPlasticTheme().getPrimaryControlInfo();
    }

    public static ColorUIResource getPrimaryControlShadow() {
        return getPlasticTheme().getPrimaryControlShadow();
    }

    public static ColorUIResource getPrimaryControl() {
        return getPlasticTheme().getPrimaryControl();
    }

    public static ColorUIResource getControlHighlight() {
        return getPlasticTheme().getControlHighlight();
    }

    public static ColorUIResource getControlDarkShadow() {
        return getPlasticTheme().getControlDarkShadow();
    }

    public static ColorUIResource getControl() {
        return getPlasticTheme().getControl();
    }

    public static ColorUIResource getFocusColor() {
        return getPlasticTheme().getFocusColor();
    }

    public static ColorUIResource getMenuItemBackground() {
        return getPlasticTheme().getMenuItemBackground();
    }

    public static ColorUIResource getMenuItemSelectedBackground() {
        return getPlasticTheme().getMenuItemSelectedBackground();
    }

    public static ColorUIResource getMenuItemSelectedForeground() {
        return getPlasticTheme().getMenuItemSelectedForeground();
    }

    public static ColorUIResource getWindowTitleBackground() {
        return getPlasticTheme().getWindowTitleBackground();
    }

    public static ColorUIResource getWindowTitleForeground() {
        return getPlasticTheme().getWindowTitleForeground();
    }

    public static ColorUIResource getWindowTitleInactiveBackground() {
        return getPlasticTheme().getWindowTitleInactiveBackground();
    }

    public static ColorUIResource getWindowTitleInactiveForeground() {
        return getPlasticTheme().getWindowTitleInactiveForeground();
    }

    public static ColorUIResource getSimpleInternalFrameForeground() {
        return getPlasticTheme().getSimpleInternalFrameForeground();
    }

    public static ColorUIResource getSimpleInternalFrameBackground() {
        return getPlasticTheme().getSimpleInternalFrameBackground();
    }

    public static ColorUIResource getTitleTextColor() {
        return getPlasticTheme().getTitleTextColor();
    }

    public static FontUIResource getTitleTextFont() {
        return getPlasticTheme().getTitleTextFont();
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticLookAndFeel$FocusOwnerTracker.class */
    static final class FocusOwnerTracker implements PropertyChangeListener {
        FocusOwnerTracker() {
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent evt) {
            if (!"focusOwner".equals(evt.getPropertyName())) {
                return;
            }
            repaintFocusIfNecessary(evt.getOldValue());
            repaintFocusIfNecessary(evt.getNewValue());
        }

        private static void repaintFocusIfNecessary(Object owner) {
            if (!(owner instanceof JComponent)) {
                return;
            }
            JComponent c = (JComponent) owner;
            Container parent = c.getParent();
            if (c instanceof JTextField) {
                if (parent instanceof JComboBox) {
                    parent.repaint();
                } else {
                    c.repaint();
                }
            }
            if (parent != null && (parent.getParent() instanceof JSpinner)) {
                parent.getParent().repaint();
                return;
            }
            CompoundBorder border = c.getBorder();
            if (!(border instanceof BorderUIResource.CompoundBorderUIResource)) {
                return;
            }
            CompoundBorder compoundBorder = border;
            if (compoundBorder.getOutsideBorder() instanceof PlasticModernBorders.ModernTextFieldBorder) {
                c.repaint();
            }
        }
    }
}
