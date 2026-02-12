package com.jgoodies.components;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.format.EmptyDateFormat;
import com.jgoodies.common.format.EmptyFormat;
import com.jgoodies.common.format.EmptyNumberFormat;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.JSDLCommonSetup;
import com.jgoodies.common.jsdl.action.ConsumerAction;
import com.jgoodies.common.swing.MnemonicUtils;
import com.jgoodies.common.swing.ScreenScaling;
import com.jgoodies.common.swing.focus.FocusTraversalUtils;
import com.jgoodies.components.JGSearchField;
import com.jgoodies.components.internal.ReadOnlyEditorPane;
import com.jgoodies.components.internal.ReadOnlyFormattedTextField;
import com.jgoodies.components.internal.ReadOnlyTextArea;
import com.jgoodies.components.internal.ReadOnlyTextField;
import com.jgoodies.components.internal.StaticEditorPane;
import com.jgoodies.components.internal.StaticTextArea;
import com.jgoodies.components.plaf.ComponentSetup;
import com.jgoodies.components.renderer.JGBooleanTableCellRenderer;
import com.jgoodies.components.renderer.JGDefaultListCellRenderer;
import com.jgoodies.components.renderer.JGFormatTableCellRenderer;
import com.jgoodies.components.util.ComponentUtils;
import com.jgoodies.layout.factories.ComponentFactory;
import com.jgoodies.layout.factories.DefaultComponentFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.JTextComponent;
import javax.swing.text.NumberFormatter;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGComponentFactory.class */
public class JGComponentFactory implements ComponentFactory {
    private static JGComponentFactory current;
    private HyperlinkListener defaultHyperlinkListener;
    private TableCellRenderer defaultBooleanRenderer;
    private TableCellRenderer defaultLocalDateRenderer;
    private TableCellRenderer defaultLocalDateTimeRenderer;
    protected static int buttonCounter = 1;
    protected static int menuCounter = 1;
    protected static int menuItemCounter = 1;
    private static final KeyStroke ENTER = KeyStroke.getKeyStroke(10, 0);
    private static final KeyStroke SHIFT_ENTER = KeyStroke.getKeyStroke(10, 1);

    protected JGComponentFactory() {
    }

    public static JGComponentFactory getCurrent() {
        ComponentSetup.ensureSetup();
        if (current == null) {
            current = new JGComponentFactory();
        }
        return current;
    }

    public static void setCurrent(JGComponentFactory newInstance) {
        current = newInstance;
    }

    public HyperlinkListener getDefaultHyperlinkListener() {
        return this.defaultHyperlinkListener;
    }

    public void setDefaultHyperlinkListener(HyperlinkListener hyperlinkListener) {
        this.defaultHyperlinkListener = hyperlinkListener;
    }

    public JButton createButton() {
        StringBuilder append = new StringBuilder().append("Button ");
        int i = buttonCounter;
        buttonCounter = i + 1;
        return createButton(append.append(i).toString());
    }

    @Override // com.jgoodies.layout.factories.ComponentFactory
    public JButton createButton(Action action) {
        JGButton button = new JGButton(action);
        configure((AbstractButton) button);
        return button;
    }

    public JButton createButton(String markedText) {
        Preconditions.checkNotBlank(markedText, Messages.MUST_NOT_BE_BLANK, "button text");
        checkEllipsis(markedText);
        JButton button = new JGButton();
        MnemonicUtils.configure((AbstractButton) button, markedText);
        configure((AbstractButton) button);
        return button;
    }

    public JCheckBox createCheckBox(String markedText) {
        return createCheckBox(markedText, null);
    }

    public JCheckBox createCheckBox(String markedText, String accessibleName) {
        Preconditions.checkNotBlank(markedText, "The text must not be null, empty, or whitespace.\nA typical design mistake is to label a check box with empty text.\nThese check boxes are difficult to hit with a mouse.\nAnd they may lack the focus indicator.");
        JCheckBox checkBox = new JCheckBox();
        checkBox.setContentAreaFilled(false);
        MnemonicUtils.configure((AbstractButton) checkBox, markedText);
        configure((AbstractButton) checkBox);
        checkBox.getAccessibleContext().setAccessibleName(accessibleName);
        return checkBox;
    }

    public JCheckBoxMenuItem createCheckBoxMenuItem() {
        StringBuilder append = new StringBuilder().append("Check box item ");
        int i = menuItemCounter;
        menuItemCounter = i + 1;
        return createCheckBoxMenuItem(append.append(i).toString());
    }

    public JCheckBoxMenuItem createCheckBoxMenuItem(String markedText) {
        Preconditions.checkNotBlank(markedText, Messages.MUST_NOT_BE_BLANK, "check box menu item text");
        JCheckBoxMenuItem item = new JGCheckBoxMenuItem();
        MnemonicUtils.configure((AbstractButton) item, markedText);
        configure((AbstractButton) item);
        return item;
    }

    public JCheckBoxMenuItem createCheckBoxMenuItem(Action action) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
        configure((AbstractButton) item);
        return item;
    }

    public <E> JComboBox<E> createComboBox(E... items) {
        return createComboBox((ListCellRenderer) null, items);
    }

    public <E> JComboBox<E> createComboBox(List<E> items) {
        return createComboBox((ListCellRenderer) null, items);
    }

    public <E> JComboBox<E> createComboBox(ListCellRenderer<? super E> cellRenderer, E... items) {
        Preconditions.checkNotNull(items, Messages.MUST_NOT_BE_NULL, "combo box items");
        return createComboBox(cellRenderer, Arrays.asList(items));
    }

    public <E> JComboBox<E> createComboBox(ListCellRenderer<? super E> cellRenderer, List<E> items) {
        Preconditions.checkNotNull(items, Messages.MUST_NOT_BE_NULL, "combo box items");
        JComboBox<E> comboBox = new JComboBox<>(new Vector(items));
        if (cellRenderer != null) {
            comboBox.setRenderer(cellRenderer);
        }
        return comboBox;
    }

    public <E> JComboBox<E> createComboBox(Function<E, String> displayStringFunction, E... items) {
        return createComboBox(new JGDefaultListCellRenderer(displayStringFunction), items);
    }

    public <E> JComboBox<E> createComboBox(Function<E, String> displayStringFunction, List<E> items) {
        return createComboBox(new JGDefaultListCellRenderer(displayStringFunction), items);
    }

    public JGFormattedTextField createDateField() {
        DateFormat editFormat = DateFormat.getDateInstance(3);
        editFormat.setLenient(false);
        DateFormat displayFormat = DateFormat.getDateInstance();
        DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(new DateFormatter(new EmptyDateFormat(editFormat)), new DateFormatter(new EmptyDateFormat(displayFormat)));
        return createFormattedTextField((JFormattedTextField.AbstractFormatterFactory) formatterFactory);
    }

    public JGFormattedTextField createFormattedTextField(Format format) {
        Preconditions.checkNotNull(format, Messages.MUST_NOT_BE_NULL, "format");
        JGFormattedTextField field = new JGFormattedTextField(format);
        configure(field);
        return field;
    }

    public JGFormattedTextField createFormattedTextField(JFormattedTextField.AbstractFormatter formatter) {
        Preconditions.checkNotNull(formatter, Messages.MUST_NOT_BE_NULL, "formatter");
        JGFormattedTextField field = new JGFormattedTextField(formatter);
        configure(field);
        return field;
    }

    public JGFormattedTextField createFormattedTextField(JFormattedTextField.AbstractFormatterFactory formatterFactory) {
        Preconditions.checkNotNull(formatterFactory, Messages.MUST_NOT_BE_NULL, "formatter factory");
        JGFormattedTextField field = new JGFormattedTextField(formatterFactory);
        configure(field);
        return field;
    }

    @Override // com.jgoodies.layout.factories.ComponentFactory
    public JLabel createHeaderLabel(String markedText) {
        return createHeaderLabel(markedText, (Object[]) null);
    }

    public JLabel createHeaderLabel(String markedText, Object... args) {
        JLabel label = createLabel(markedText, args);
        label.setFont(getHeaderFont());
        label.setForeground(getHeaderForeground());
        return label;
    }

    public JGHyperlink createHelpLink() {
        StringBuilder append = new StringBuilder().append("Help link ");
        int i = buttonCounter;
        buttonCounter = i + 1;
        return createHelpLink(append.append(i).toString());
    }

    public JGHyperlink createHelpLink(Action action) {
        String text = (String) action.getValue("Name");
        Preconditions.checkNotBlank(text, Messages.MUST_NOT_BE_BLANK, "help link text");
        checkNoEndingEllipsis(text);
        checkNoEndingDotOrExclamationMark(text);
        checkNoMnemonic(action);
        JGHyperlink link = new JGHyperlink(action, false);
        configure((AbstractButton) link);
        return link;
    }

    public JGHyperlink createHelpLink(String text) {
        Preconditions.checkNotBlank(text, Messages.MUST_NOT_BE_BLANK, "help link text");
        checkNoEndingEllipsis(text);
        checkNoEndingDotOrExclamationMark(text);
        checkNoMnemonic(text);
        JGHyperlink link = new JGHyperlink(null, text, false);
        configure((AbstractButton) link);
        return link;
    }

    public JGHyperlink createHelpLink(String text, String url) {
        Preconditions.checkNotBlank(text, Messages.MUST_NOT_BE_BLANK, "help link text");
        return createHelpLink((Action) new HelpHyperlinkAction(text, url));
    }

    public JGFormattedTextField createIntegerField() {
        return createIntegerField(NumberFormat.getIntegerInstance(), (Integer) null);
    }

    public JGFormattedTextField createIntegerField(int emptyNumber) {
        return createIntegerField(NumberFormat.getIntegerInstance(), emptyNumber);
    }

    public JGFormattedTextField createIntegerField(NumberFormat numberFormat) {
        return createIntegerField(numberFormat, (Integer) null);
    }

    public JGFormattedTextField createIntegerField(NumberFormat numberFormat, int emptyNumber) {
        return createIntegerField(numberFormat, Integer.valueOf(emptyNumber));
    }

    public JGFormattedTextField createIntegerField(NumberFormat numberFormat, Integer emptyNumber) {
        NumberFormatter numberFormatter = new NumberFormatter(new EmptyNumberFormat(numberFormat, emptyNumber));
        numberFormatter.setValueClass(Integer.class);
        return createFormattedTextField((JFormattedTextField.AbstractFormatter) numberFormatter);
    }

    public JLabel createLabel() {
        return createLabel("");
    }

    @Override // com.jgoodies.layout.factories.ComponentFactory
    public JLabel createLabel(String markedText) {
        return createLabel(markedText, (Object[]) null);
    }

    public JLabel createLabel(String markedText, Object... args) {
        return DefaultComponentFactory.getInstance().createLabel(Strings.get(markedText, args));
    }

    public <E> JList<E> createList(E... items) {
        Preconditions.checkNotNull(items, Messages.MUST_NOT_BE_NULL, items);
        return createList((ListCellRenderer) null, items);
    }

    public <E> JList<E> createList(List<E> items) {
        Preconditions.checkNotNull(items, Messages.MUST_NOT_BE_NULL, items);
        return createList((ListCellRenderer) null, items);
    }

    public <E> JList<E> createList(ListCellRenderer<? super E> cellRenderer, E... items) {
        return createList(cellRenderer, Arrays.asList(items));
    }

    public <E> JList<E> createList(ListCellRenderer<? super E> cellRenderer, List<E> items) {
        JList<E> list = new JList<>(new Vector(items));
        if (cellRenderer != null) {
            list.setCellRenderer(cellRenderer);
        }
        return list;
    }

    public <E> JList<E> createList(Function<E, String> displayStringFunction, E... items) {
        return createList(new JGDefaultListCellRenderer(displayStringFunction), items);
    }

    public <E> JList<E> createList(Function<E, String> displayStringFunction, List<E> items) {
        return createList(new JGDefaultListCellRenderer(displayStringFunction), items);
    }

    public JGFormattedTextField createLongField() {
        return createLongField(NumberFormat.getIntegerInstance(), (Long) null);
    }

    public JGFormattedTextField createLongField(long emptyNumber) {
        return createLongField(NumberFormat.getIntegerInstance(), emptyNumber);
    }

    public JGFormattedTextField createLongField(NumberFormat numberFormat) {
        return createLongField(numberFormat, (Long) null);
    }

    public JGFormattedTextField createLongField(NumberFormat numberFormat, long emptyNumber) {
        return createLongField(numberFormat, Long.valueOf(emptyNumber));
    }

    public JGFormattedTextField createLongField(NumberFormat numberFormat, Long emptyNumber) {
        NumberFormatter numberFormatter = new NumberFormatter(new EmptyNumberFormat(numberFormat, emptyNumber));
        numberFormatter.setValueClass(Long.class);
        return createFormattedTextField((JFormattedTextField.AbstractFormatter) numberFormatter);
    }

    public JMenu createMenu() {
        StringBuilder append = new StringBuilder().append("Menu ");
        int i = menuCounter;
        menuCounter = i + 1;
        return createMenu(append.append(i).toString());
    }

    public JMenu createMenu(Action action) {
        JGMenu menu = new JGMenu(action);
        configure((AbstractButton) menu);
        return menu;
    }

    public JMenu createMenu(String markedText) {
        Preconditions.checkNotBlank(markedText, Messages.MUST_NOT_BE_BLANK, "menu text");
        JMenu menu = new JGMenu();
        MnemonicUtils.configure((AbstractButton) menu, markedText);
        configure((AbstractButton) menu);
        return menu;
    }

    public JGMenuButton createMenuButton(JMenuItem... items) {
        return createMenuButton(createMenu(items));
    }

    public JGMenuButton createMenuButton(JPopupMenu menu) {
        StringBuilder append = new StringBuilder().append("Button ");
        int i = buttonCounter;
        buttonCounter = i + 1;
        String text = append.append(i).toString();
        return createMenuButton(ConsumerAction.noOp(text), menu);
    }

    public JGMenuButton createMenuButton(Action action, JMenuItem... items) {
        return createMenuButton(action, createMenu(items));
    }

    public JGMenuButton createMenuButton(Action action, JPopupMenu menu) {
        JGMenuButton button = new JGMenuButton(action, menu);
        configure((AbstractButton) button);
        return button;
    }

    public JGMenuButton createMenuButton(String markedText, JMenuItem... items) {
        return createMenuButton(markedText, createMenu(items));
    }

    public JGMenuButton createMenuButton(String markedText, JPopupMenu menu) {
        JGMenuButton button = new JGMenuButton(null, null, menu);
        MnemonicUtils.configure((AbstractButton) button, markedText);
        configure((AbstractButton) button);
        return button;
    }

    public JMenuItem createMenuItem() {
        StringBuilder append = new StringBuilder().append("Item ");
        int i = menuItemCounter;
        menuItemCounter = i + 1;
        return createMenuItem(append.append(i).toString());
    }

    public JMenuItem createMenuItem(Action action) {
        JGMenuItem item = new JGMenuItem(action);
        configure((AbstractButton) item);
        return item;
    }

    public JMenuItem createMenuItem(String markedText) {
        return createMenuItem(markedText, null);
    }

    public JMenuItem createMenuItem(String markedText, Icon icon) {
        Preconditions.checkNotBlank(markedText, Messages.MUST_NOT_BE_BLANK, "menu item text");
        JMenuItem item = new JGMenuItem(icon);
        MnemonicUtils.configure((AbstractButton) item, markedText);
        configure((AbstractButton) item);
        return item;
    }

    public JGHyperlink createNavigationLink() {
        StringBuilder append = new StringBuilder().append("Navigation link ");
        int i = buttonCounter;
        buttonCounter = i + 1;
        return createNavigationLink(append.append(i).toString());
    }

    public JGHyperlink createNavigationLink(Action action) {
        String text = (String) action.getValue("Name");
        Preconditions.checkNotBlank(text, Messages.MUST_NOT_BE_BLANK, "navigation link text");
        checkNoEndingEllipsis(text);
        checkNoEndingDotOrExclamationMark(text);
        checkNoLeadingProtocol(text);
        checkNoLeadingWWW(text);
        checkNoMnemonic(action);
        JGHyperlink link = new JGHyperlink(action, true);
        link.setVisitedEnabled(true);
        configure((AbstractButton) link);
        return link;
    }

    public JGHyperlink createNavigationLink(String text) {
        Preconditions.checkNotBlank(text, Messages.MUST_NOT_BE_BLANK, "navigation link text");
        checkNoEndingEllipsis(text);
        checkNoEndingPunctuation(text);
        checkNoMnemonic(text);
        checkNoLeadingProtocol(text);
        checkNoLeadingWWW(text);
        JGHyperlink link = new JGHyperlink(null, text, true);
        link.setVisitedEnabled(true);
        configure((AbstractButton) link);
        return link;
    }

    public JGPasswordField createPasswordField() {
        return createPasswordField("");
    }

    public JGPasswordField createPasswordField(String text) {
        return createPasswordField(text, null);
    }

    public JGPasswordField createPasswordField(String text, String accessibleName) {
        JGPasswordField field = new JGPasswordField(text);
        field.getAccessibleContext().setAccessibleName(accessibleName);
        configure(field);
        return field;
    }

    public JRadioButton createRadioButton(String markedText) {
        return createRadioButton(markedText, null);
    }

    public JRadioButton createRadioButton(String markedText, String accessibleName) {
        Preconditions.checkNotBlank(markedText, "The text must not be null, empty, or whitespace.\nRadio buttons with empty text are difficult to hit with a mouse.\nAnd they may lack the focus indicator.");
        JRadioButton radioButton = new JRadioButton();
        radioButton.setContentAreaFilled(false);
        MnemonicUtils.configure((AbstractButton) radioButton, markedText);
        configure((AbstractButton) radioButton);
        radioButton.getAccessibleContext().setAccessibleName(accessibleName);
        return radioButton;
    }

    public JRadioButtonMenuItem createRadioButtonMenuItem() {
        StringBuilder append = new StringBuilder().append("Item ");
        int i = menuItemCounter;
        menuItemCounter = i + 1;
        return createRadioButtonMenuItem(append.append(i).toString());
    }

    public JRadioButtonMenuItem createRadioButtonMenuItem(Action action) {
        JGRadioButtonMenuItem item = new JGRadioButtonMenuItem(action);
        configure((AbstractButton) item);
        return item;
    }

    public JRadioButtonMenuItem createRadioButtonMenuItem(String markedText) {
        return createRadioButtonMenuItem(markedText, null);
    }

    public JRadioButtonMenuItem createRadioButtonMenuItem(String markedText, Icon icon) {
        Preconditions.checkNotBlank(markedText, Messages.MUST_NOT_BE_BLANK, "radio button menu item text");
        JRadioButtonMenuItem item = new JGRadioButtonMenuItem(icon);
        MnemonicUtils.configure((AbstractButton) item, markedText);
        configure((AbstractButton) item);
        return item;
    }

    public JEditorPane createReadOnlyEditorPane(URL url) {
        return createReadOnlyEditorPane(url, getDefaultHyperlinkListener());
    }

    public JEditorPane createReadOnlyEditorPane(URL url, HyperlinkListener hyperlinkListener) {
        ReadOnlyEditorPane editorPane = new ReadOnlyEditorPane("text/html", "");
        editorPane.setFocusable(JSDLCommonSetup.isOptimizedForScreenReader());
        try {
            editorPane.setPage(url);
            if (hyperlinkListener != null) {
                editorPane.addHyperlinkListener(hyperlinkListener);
            }
            return editorPane;
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not set the URL " + url, e);
        }
    }

    public JFormattedTextField createReadOnlyFormattedTextField(Format format) {
        return createReadOnlyFormattedTextField(format, null);
    }

    public JFormattedTextField createReadOnlyFormattedTextField(Format format, String accessibleName) {
        JFormattedTextField textField = new ReadOnlyFormattedTextField(format);
        textField.getAccessibleContext().setAccessibleName(accessibleName);
        return textField;
    }

    public JFormattedTextField createReadOnlyFormattedTextField(JFormattedTextField.AbstractFormatter formatter) {
        return new ReadOnlyFormattedTextField(formatter);
    }

    public JFormattedTextField createReadOnlyIntegerField() {
        return createReadOnlyIntegerField(NumberFormat.getIntegerInstance(), (Integer) null);
    }

    public JFormattedTextField createReadOnlyIntegerField(int emptyNumber) {
        return createReadOnlyIntegerField(NumberFormat.getIntegerInstance(), emptyNumber);
    }

    public JFormattedTextField createReadOnlyIntegerField(NumberFormat numberFormat) {
        return createReadOnlyIntegerField(numberFormat, (Integer) null);
    }

    public JFormattedTextField createReadOnlyIntegerField(NumberFormat numberFormat, int emptyNumber) {
        return createReadOnlyIntegerField(numberFormat, Integer.valueOf(emptyNumber));
    }

    public JFormattedTextField createReadOnlyIntegerField(NumberFormat numberFormat, Integer emptyNumber) {
        NumberFormatter numberFormatter = new NumberFormatter(new EmptyNumberFormat(numberFormat, emptyNumber));
        numberFormatter.setValueClass(Integer.class);
        return createReadOnlyFormattedTextField((JFormattedTextField.AbstractFormatter) numberFormatter);
    }

    @Override // com.jgoodies.layout.factories.ComponentFactory
    public JLabel createReadOnlyLabel(String markedText) {
        return createReadOnlyLabel(markedText, (Object[]) null);
    }

    public JLabel createReadOnlyLabel(String markedText, Object... args) {
        return DefaultComponentFactory.getInstance().createReadOnlyLabel(Strings.get(markedText, args));
    }

    public JFormattedTextField createReadOnlyLongField() {
        return createReadOnlyLongField(NumberFormat.getIntegerInstance(), (Long) null);
    }

    public JFormattedTextField createReadOnlyLongField(long emptyNumber) {
        return createReadOnlyLongField(NumberFormat.getIntegerInstance(), emptyNumber);
    }

    public JFormattedTextField createReadOnlyLongField(NumberFormat numberFormat) {
        return createReadOnlyLongField(numberFormat, (Long) null);
    }

    public JFormattedTextField createReadOnlyLongField(NumberFormat numberFormat, long emptyNumber) {
        return createReadOnlyLongField(numberFormat, Long.valueOf(emptyNumber));
    }

    public JFormattedTextField createReadOnlyLongField(NumberFormat numberFormat, Long emptyNumber) {
        NumberFormatter numberFormatter = new NumberFormatter(new EmptyNumberFormat(numberFormat, emptyNumber));
        numberFormatter.setValueClass(Long.class);
        return createReadOnlyFormattedTextField((JFormattedTextField.AbstractFormatter) numberFormatter);
    }

    public <E> JGStripedTable<E> createReadOnlyTable() {
        JGStripedTable<E> table = createTable();
        if (!JSDLCommonSetup.isOptimizedForScreenReader()) {
            ComponentUtils.clearFocusTraversalKeys(table);
            disableEnterBindings(table);
        }
        return table;
    }

    public <E> JGStripedTable<E> createReadOnlyTable(TableModel tableModel) {
        JGStripedTable<E> table = createReadOnlyTable();
        table.setModel(tableModel);
        return table;
    }

    public JTextArea createReadOnlyTextArea() {
        return createReadOnlyTextArea(null);
    }

    public JTextArea createReadOnlyTextArea(String text) {
        return createReadOnlyTextArea(text, null);
    }

    public JTextArea createReadOnlyTextArea(String text, String accessibleName) {
        JTextArea textArea = new ReadOnlyTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.getAccessibleContext().setAccessibleName(accessibleName);
        return textArea;
    }

    public JGTextField createReadOnlyTextField() {
        return createReadOnlyTextField("");
    }

    public JGTextField createReadOnlyTextField(String text) {
        return createReadOnlyTextField(text, null);
    }

    public JGTextField createReadOnlyTextField(String text, String accessibleName) {
        JGTextField field = new ReadOnlyTextField(text);
        field.getAccessibleContext().setAccessibleName(accessibleName);
        configure(field);
        return field;
    }

    public JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        configure(scrollPane);
        return scrollPane;
    }

    public JGSearchField createSearchField(JGSearchField.SearchMode mode) {
        JGSearchField field = new JGSearchField(mode);
        configure(field);
        return field;
    }

    @Override // com.jgoodies.layout.factories.ComponentFactory
    public JComponent createSeparator(String markedText, int alignment) {
        return DefaultComponentFactory.getInstance().createSeparator(markedText, alignment);
    }

    public JGSplitButton createSplitButton(JMenuItem... items) {
        return createSplitButton(createMenu(items));
    }

    public JGSplitButton createSplitButton(JPopupMenu menu) {
        StringBuilder append = new StringBuilder().append("Button ");
        int i = buttonCounter;
        buttonCounter = i + 1;
        String text = append.append(i).toString();
        return createSplitButton(ConsumerAction.noOp(text), menu);
    }

    public JGSplitButton createSplitButton(Action action, JMenuItem... items) {
        return createSplitButton(action, createMenu(items));
    }

    public JGSplitButton createSplitButton(Action action, JPopupMenu menu) {
        JGSplitButton button = new JGSplitButton(action, menu);
        configure((AbstractButton) button);
        return button;
    }

    public JGSplitButton createSplitButton(String markedText, JMenuItem... items) {
        return createSplitButton(markedText, createMenu(items));
    }

    public JGSplitButton createSplitButton(String markedText, JPopupMenu menu) {
        JGSplitButton button = new JGSplitButton(null, null, menu);
        MnemonicUtils.configure((AbstractButton) button, markedText);
        configure((AbstractButton) button);
        return button;
    }

    public JGSplitPane createSplitPane(int orientation, Component leftComponent, Component rightComponent) {
        JGSplitPane split = new JGSplitPane(orientation, leftComponent, rightComponent);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setOneTouchExpandable(false);
        return split;
    }

    public JGSplitPane createSplitPane(int orientation, Component leftComponent, Component rightComponent, double resizeWeight) {
        JGSplitPane split = createSplitPane(orientation, leftComponent, rightComponent);
        split.setResizeWeight(resizeWeight);
        return split;
    }

    public JTextComponent createStaticText(String plainOrHTMLText, Object... args) {
        return createStaticText(Strings.get(plainOrHTMLText, args), getDefaultHyperlinkListener());
    }

    public JTextComponent createStaticText(String plainOrHTMLText, HyperlinkListener hyperlinkListener) {
        if (BasicHTML.isHTMLString(plainOrHTMLText)) {
            return createStaticHTMLPane(plainOrHTMLText, hyperlinkListener);
        }
        return createStaticTextArea(plainOrHTMLText);
    }

    public JEditorPane createStaticHTMLText() {
        return createStaticHTMLText(null, null);
    }

    public JEditorPane createStaticHTMLText(HyperlinkListener hyperlinkListener) {
        return createStaticHTMLText(null, hyperlinkListener);
    }

    public JEditorPane createStaticHTMLText(URL url) {
        return createStaticHTMLText(url, null);
    }

    public JEditorPane createStaticHTMLText(URL url, HyperlinkListener hyperlinkListener) {
        JEditorPane editorPane = createStaticHTMLPane(null, hyperlinkListener);
        if (url != null) {
            try {
                editorPane.setPage(url);
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not set the URL " + url, e);
            }
        }
        return editorPane;
    }

    public JScrollPane createStrippedScrollPane(Component component) {
        JScrollPane scrollPane = createScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().putClientProperty("JScrollBar.isFreeStanding", Boolean.FALSE);
        scrollPane.getHorizontalScrollBar().putClientProperty("JScrollBar.isFreeStanding", Boolean.FALSE);
        return scrollPane;
    }

    public JTabbedPane createTabbedPane() {
        JTabbedPane pane = new JTabbedPane();
        pane.setRequestFocusEnabled(false);
        return pane;
    }

    public <E> JGStripedTable<E> createTable() {
        JGStripedTable<E> table = new JGStripedTable<>();
        table.setDefaultRenderer(Boolean.class, getDefaultBooleanRenderer());
        table.setDefaultRenderer(LocalDate.class, getDefaultLocalDateRenderer());
        table.setDefaultRenderer(LocalDateTime.class, getDefaultLocalDateTimeRenderer());
        return table;
    }

    public <E> JGStripedTable<E> createTable(TableModel tableModel) {
        JGStripedTable<E> table = createTable();
        table.setModel(tableModel);
        return table;
    }

    public JGHyperlink createTaskLink() {
        StringBuilder append = new StringBuilder().append("Task link ");
        int i = buttonCounter;
        buttonCounter = i + 1;
        return createTaskLink(append.append(i).toString());
    }

    public JGHyperlink createTaskLink(Action action) {
        String text = (String) action.getValue("Name");
        Preconditions.checkNotBlank(text, Messages.MUST_NOT_BE_BLANK, "task link text");
        checkNoEndingDotOrExclamationMark(text);
        checkNoMnemonic(action);
        JGHyperlink link = new JGHyperlink(action, true);
        configure((AbstractButton) link);
        return link;
    }

    public JGHyperlink createTaskLink(String text) {
        Preconditions.checkNotBlank(text, Messages.MUST_NOT_BE_BLANK, "task link text");
        checkNoMnemonic(text);
        checkNoEndingPunctuation(text);
        JGHyperlink link = new JGHyperlink(null, text, false);
        configure((AbstractButton) link);
        return link;
    }

    public JGTextField createTextField() {
        return createTextField("");
    }

    public JGTextField createTextField(String text) {
        return createTextField(text, null);
    }

    public JGTextField createTextField(String text, String accessibleName) {
        JGTextField textField = new JGTextField(text);
        textField.getAccessibleContext().setAccessibleName(accessibleName);
        configure(textField);
        return textField;
    }

    public JGTextArea createTextArea() {
        return createTextArea(null);
    }

    public JGTextArea createTextArea(String text) {
        return createTextArea(text, null);
    }

    public JGTextArea createTextArea(String text, String accessibleName) {
        JGTextArea textArea = new JGTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.getAccessibleContext().setAccessibleName(accessibleName);
        ComponentUtils.clearFocusTraversalKeys(textArea);
        configure(textArea);
        return textArea;
    }

    @Override // com.jgoodies.layout.factories.ComponentFactory
    public JLabel createTitle(String markedText) {
        return createTitle(markedText, (Object[]) null);
    }

    public JLabel createTitle(String markedText, Object... args) {
        return DefaultComponentFactory.getInstance().createTitle(Strings.get(markedText, args));
    }

    protected TableCellRenderer createDefaultBooleanRenderer() {
        return JGBooleanTableCellRenderer.INSTANCE;
    }

    protected TableCellRenderer createDefaultLocalDateRenderer() {
        return new JGFormatTableCellRenderer(new EmptyFormat(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).toFormat()));
    }

    protected TableCellRenderer createDefaultLocalDateTimeRenderer() {
        return new JGFormatTableCellRenderer(new EmptyFormat(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).toFormat()));
    }

    protected JEditorPane createStaticHTMLPane(String htmlText, HyperlinkListener hyperlinkListener) {
        JEditorPane editorPane = new StaticEditorPane("text/html", htmlText);
        editorPane.setMinimumSize(ScreenScaling.physicalDimension(150, 0));
        editorPane.setFocusable(JSDLCommonSetup.isOptimizedForScreenReader());
        FocusTraversalUtils.markAsStaticText(editorPane);
        if (hyperlinkListener == null) {
            hyperlinkListener = getDefaultHyperlinkListener();
        }
        if (hyperlinkListener != null) {
            editorPane.addHyperlinkListener(hyperlinkListener);
        }
        return editorPane;
    }

    protected JTextArea createStaticTextArea(String text) {
        JTextArea textArea = new StaticTextArea(text);
        textArea.setMinimumSize(ScreenScaling.physicalDimension(150, 0));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFocusable(JSDLCommonSetup.isOptimizedForScreenReader());
        FocusTraversalUtils.markAsStaticText(textArea);
        return textArea;
    }

    protected void configure(AbstractButton button) {
    }

    protected void configure(JScrollPane scrollPane) {
    }

    protected void configure(JGTextArea area) {
        area.setSelectOnFocusGainEnabled(false);
    }

    protected void configure(JGTextField field) {
    }

    protected void configure(JGFormattedTextField field) {
    }

    protected void configure(JGPasswordField field) {
    }

    protected void configure(JGSearchField field) {
    }

    @Deprecated
    protected void configureMenuLink(JGHyperlink link) {
        link.setForeground(UIManager.getColor("controlText"));
        link.setVisitedEnabled(false);
    }

    @Deprecated
    public final void configureMenuLinks(JGHyperlink... links) {
        for (JGHyperlink link : links) {
            configureMenuLink(link);
        }
    }

    protected Font getHeaderFont() {
        Font font = UIManager.getFont("AbstractStyledPane.header.font");
        if (font == null) {
            font = UIManager.getFont("Label.header.font");
        }
        return font;
    }

    protected Color getHeaderForeground() {
        Color foreground = UIManager.getColor("AbstractStyledPane.header.foreground");
        if (foreground == null) {
            foreground = UIManager.getColor("Label.header.foreground");
        }
        return foreground;
    }

    private TableCellRenderer getDefaultBooleanRenderer() {
        if (this.defaultBooleanRenderer == null) {
            this.defaultBooleanRenderer = createDefaultBooleanRenderer();
        }
        return this.defaultBooleanRenderer;
    }

    private TableCellRenderer getDefaultLocalDateRenderer() {
        if (this.defaultLocalDateRenderer == null) {
            this.defaultLocalDateRenderer = createDefaultLocalDateRenderer();
        }
        return this.defaultLocalDateRenderer;
    }

    private TableCellRenderer getDefaultLocalDateTimeRenderer() {
        if (this.defaultLocalDateTimeRenderer == null) {
            this.defaultLocalDateTimeRenderer = createDefaultLocalDateTimeRenderer();
        }
        return this.defaultLocalDateTimeRenderer;
    }

    private static void disableEnterBindings(JTable table) {
        InputMap inputMap = table.getInputMap(1);
        inputMap.put(ENTER, "dummy-binding effectively disabling ENTER");
        inputMap.put(SHIFT_ENTER, "dummy-binding effectively disabling shift ENTER");
    }

    private static JPopupMenu createMenu(JMenuItem... items) {
        Preconditions.checkNotNullOrEmpty(items, Messages.MUST_NOT_BE_NULL_OR_EMPTY, "menu items");
        JPopupMenu menu = new JPopupMenu();
        for (JMenuItem item : items) {
            menu.add(item);
        }
        return menu;
    }

    private static void checkEllipsis(String text) {
        Preconditions.checkArgument(text == null || !text.endsWith(Strings.NO_ELLIPSIS_STRING), Messages.USE_ELLIPSIS_NOT_THREE_DOTS, "text");
    }

    private static void checkNoEndingEllipsis(String text) {
        Preconditions.checkArgument((text.endsWith(Strings.NO_ELLIPSIS_STRING) || text.endsWith("…")) ? false : true, "The text must not end with an ellipsis ('…').");
    }

    private static void checkNoEndingPunctuation(String text) {
        Preconditions.checkArgument((text.endsWith(".") || text.endsWith("!") || text.endsWith("?")) ? false : true, "The  text must not end with punctuation ('.', '!', '?').");
    }

    private static void checkNoEndingDotOrExclamationMark(String text) {
        Preconditions.checkArgument((text.endsWith(".") || text.endsWith("!")) ? false : true, "The text must not end with dot or exclamation mark ('.', '!').");
    }

    private static void checkNoLeadingProtocol(String text) {
        Preconditions.checkArgument(!Strings.startsWithIgnoreCase(text, "http://"), "The text must not start with \"http://\".");
    }

    private static void checkNoLeadingWWW(String text) {
        Preconditions.checkArgument(!Strings.startsWithIgnoreCase(text, "www"), "The text must not start with \"www.\".");
    }

    private static void checkNoMnemonic(Action action) {
        boolean z;
        Object mnemonic = action.getValue("MnemonicKey");
        if (mnemonic != null) {
            Integer num = 0;
            if (!num.equals(mnemonic)) {
                z = false;
                Preconditions.checkArgument(z, "The action must not set a mnemonic.");
            }
        }
        z = true;
        Preconditions.checkArgument(z, "The action must not set a mnemonic.");
    }

    private static void checkNoMnemonic(String text) {
        Preconditions.checkArgument(!MnemonicUtils.containsMarker(text), "The text must not contain a mnemonic marker ('_').");
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGComponentFactory$HelpHyperlinkAction.class */
    private static final class HelpHyperlinkAction extends AbstractAction {
        private final String url;

        private HelpHyperlinkAction(String text, String url) {
            super((String) Preconditions.checkNotBlank(text, Messages.MUST_NOT_BE_BLANK, "text"));
            this.url = (String) Preconditions.checkNotNull(url, Messages.MUST_NOT_BE_NULL, "url");
        }

        public void actionPerformed(ActionEvent evt) {
            HyperlinkEvent hyperlinkEvent = new HyperlinkEvent(evt.getSource(), HyperlinkEvent.EventType.ACTIVATED, (URL) null, this.url);
            HyperlinkListener handler = JGComponentFactory.getCurrent().getDefaultHyperlinkListener();
            if (handler == null) {
                throw new IllegalStateException("You must invoke JGComponentFactory#setDefaultHyperlinkListener before a text link can be clicked.");
            }
            handler.hyperlinkUpdate(hyperlinkEvent);
        }
    }
}
