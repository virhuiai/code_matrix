package com.jgoodies.layout.builder;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.BuilderSupport;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.focus.FocusTraversalUtils;
import com.jgoodies.layout.FormsSetup;
import com.jgoodies.layout.debug.FormDebugPanel;
import com.jgoodies.layout.factories.CC;
import com.jgoodies.layout.factories.ComponentFactory;
import com.jgoodies.layout.factories.Forms;
import com.jgoodies.layout.factories.Paddings;
import com.jgoodies.layout.internal.InternalFocusSetupUtils;
import com.jgoodies.layout.layout.CellConstraints;
import com.jgoodies.layout.layout.ColumnSpec;
import com.jgoodies.layout.layout.FormLayout;
import com.jgoodies.layout.layout.LayoutMap;
import com.jgoodies.layout.layout.RowSpec;
import com.jgoodies.layout.util.FocusTraversalType;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FocusTraversalPolicy;
import java.awt.LayoutManager;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.Border;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/builder/AbstractFormBuilder.class */
public class AbstractFormBuilder<B extends AbstractFormBuilder<B>> {
    private static final String LABELED_BY_PROPERTY = "labeledBy";
    private LayoutMap layoutMap;
    private ColumnSpec[] columnSpecs;
    private RowSpec[] rowSpecs;
    private FormLayout layout;
    private JPanel panel;
    private JComponent initialComponent;
    private FocusTraversalType focusTraversalType;
    private FocusTraversalPolicy focusTraversalPolicy;
    private boolean debug;
    private int offsetX;
    private int offsetY;
    private ComponentFactory factory;
    private final BuilderSupport support = new BuilderSupport();
    private LabelType defaultLabelType = LabelType.DEFAULT;
    private WeakReference<JLabel> mostRecentlyAddedLabelReference = null;
    private boolean labelForFeatureEnabled = FormsSetup.getLabelForFeatureEnabledDefault();

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/builder/AbstractFormBuilder$LabelType.class */
    public enum LabelType {
        DEFAULT,
        READ_ONLY
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractFormBuilder() {
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetX = 0;
        this.offsetY = 0;
    }

    public JPanel build() {
        return getPanel();
    }

    @SuppressWarnings("unchecked")
    public B layoutMap(LayoutMap layoutMap) {
        this.support.checkNotCalledTwice("layoutMap");
        this.layoutMap = layoutMap;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B columns(String encodedColumnSpecs, Object... args) {
        this.support.checkNotCalledTwice("columns");
        this.columnSpecs = ColumnSpec.decodeSpecs(Strings.get(encodedColumnSpecs, args), getLayoutMap());
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B columns(ColumnSpec... columnSpecs) {
        this.support.checkNotCalledTwice("columns");
        this.columnSpecs = (ColumnSpec[]) Preconditions.checkNotNull(columnSpecs, Messages.MUST_NOT_BE_NULL, "column specifications");
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B appendColumns(String encodedColumnSpecs, Object... args) {
        ColumnSpec[] newColumnSpecs = ColumnSpec.decodeSpecs(Strings.get(encodedColumnSpecs, args), getLayoutMap());
        for (ColumnSpec columnSpec : newColumnSpecs) {
            getLayout().appendColumn(columnSpec);
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B rows(String encodedRowSpecs, Object... args) {
        this.support.checkNotCalledTwice("rows");
        this.rowSpecs = RowSpec.decodeSpecs(Strings.get(encodedRowSpecs, args), getLayoutMap());
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B rows(RowSpec... rowSpecs) {
        this.support.checkNotCalledTwice("rows");
        this.rowSpecs = (RowSpec[]) Preconditions.checkNotNull(rowSpecs, Messages.MUST_NOT_BE_NULL, "row specifications");
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B appendRows(String encodedRowSpecs, Object... args) {
        RowSpec[] newRowSpecs = RowSpec.decodeSpecs(Strings.get(encodedRowSpecs, args), getLayoutMap());
        for (RowSpec rowSpec : newRowSpecs) {
            getLayout().appendRow(rowSpec);
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B columnGroup(int... columnIndices) {
        this.support.checkNotCalledTwice("columnGroup or #columnGroups");
        getLayout().setColumnGroup(columnIndices);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B columnGroups(int[]... multipleColumnGroups) {
        this.support.checkNotCalledTwice("columnGroup or #columnGroups");
        getLayout().setColumnGroups(multipleColumnGroups);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B rowGroup(int... rowIndices) {
        this.support.checkNotCalledTwice("rowGroup or #rowGroups");
        getLayout().setRowGroup(rowIndices);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B rowGroups(int[]... multipleRowGroups) {
        this.support.checkNotCalledTwice("rowGroup or #rowGroups");
        getLayout().setRowGroups(multipleRowGroups);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B honorsVisibility(boolean b) {
        this.support.checkNotCalledTwice("honorsVisibility");
        getLayout().setHonorsVisibility(b);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B honorsVisibility(JComponent c, boolean b) {
        getLayout().setHonorsVisibility(c, Boolean.valueOf(b));
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B layout(FormLayout layout) {
        this.support.checkNotCalledTwice("layout");
        this.layout = (FormLayout) Preconditions.checkNotNull(layout, Messages.MUST_NOT_BE_NULL, "layout");
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B panel(JPanel panel) {
        this.support.checkNotCalledTwice("panel");
        this.panel = (JPanel) Preconditions.checkNotNull(panel, Messages.MUST_NOT_BE_NULL, "panel");
        this.panel.setLayout(getLayout());
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B debug(boolean b) {
        this.support.checkNotCalledTwice("debug");
        this.debug = b;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B name(String panelName) {
        this.support.checkNotCalledTwice("name");
        getPanel().setName(panelName);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B background(Color background) {
        this.support.checkNotCalledTwice("background");
        getPanel().setBackground(background);
        getPanel().setOpaque(background != null);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B border(Border border) {
        this.support.checkNotCalledTwice("border or #padding");
        getPanel().setBorder(border);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B padding(Paddings.Padding padding) {
        return border(padding);
    }

    @SuppressWarnings("unchecked")
    public B padding(String paddingSpec, Object... args) {
        return padding(Paddings.createPadding(paddingSpec, args));
    }

    @SuppressWarnings("unchecked")
    public B opaque(boolean b) {
        this.support.checkNotCalledTwice("opaque");
        getPanel().setOpaque(b);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B initialComponent(JComponent initialComponent) {
        this.support.checkNotCalledTwice("initialComponent");
        checkValidFocusTraversalSetup();
        this.initialComponent = initialComponent;
        setupFocusTraversalPolicyAndProvider();
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B focusTraversalType(FocusTraversalType focusTraversalType) {
        this.support.checkNotCalledTwice("focusTraversalType or #focusTraversalPolicy");
        Preconditions.checkNotNull(focusTraversalType, Messages.MUST_NOT_BE_NULL, "focus traversal type");
        checkValidFocusTraversalSetup();
        this.focusTraversalType = focusTraversalType;
        setupFocusTraversalPolicyAndProvider();
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B focusTraversalPolicy(FocusTraversalPolicy policy) {
        this.support.checkNotCalledTwice("focusTraversalType or #focusTraversalPolicy");
        Preconditions.checkNotNull(policy, Messages.MUST_NOT_BE_NULL, "focus traversal policy");
        checkValidFocusTraversalSetup();
        this.focusTraversalPolicy = policy;
        setupFocusTraversalPolicyAndProvider();
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B focusGroup(AbstractButton... buttons) {
        FocusTraversalUtils.group(buttons);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B focusGroup(List<AbstractButton> buttons) {
        FocusTraversalUtils.group((List<? extends AbstractButton>) buttons);
        return (B) this;
    }

    public FormLayout getLayout() {
        if (this.layout != null) {
            return this.layout;
        }
        Preconditions.checkNotNull(this.columnSpecs, "The layout columns must be specified.");
        Preconditions.checkNotNull(this.rowSpecs, "The layout rows must be specified.");
        this.layout = new FormLayout(this.columnSpecs, this.rowSpecs);
        return this.layout;
    }

    public JPanel getPanel() {
        if (this.panel == null) {
            this.panel = this.debug ? new FormDebugPanel() : new JPanel((LayoutManager) null);
            this.panel.setOpaque(FormsSetup.getOpaqueDefault());
        }
        return this.panel;
    }

    @SuppressWarnings("unchecked")
    public B factory(ComponentFactory factory) {
        this.support.checkNotCalledTwice("factory");
        this.factory = factory;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B labelForFeatureEnabled(boolean b) {
        this.support.checkNotCalledTwice("labelForFeatureEnabled");
        this.labelForFeatureEnabled = b;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B offset(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B translate(int dX, int dY) {
        this.offsetX += dX;
        this.offsetY += dY;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B defaultLabelType(LabelType newValue) {
        this.defaultLabelType = newValue;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B readOnlyLabels() {
        return defaultLabelType(LabelType.READ_ONLY);
    }

    @SuppressWarnings("unchecked")
    public B doWith(Consumer<B> consumer) {
        consumer.accept((B) this);
        return (B) this;
    }

    public ComponentAdder<B> add(Component c) {
        return add(true, c);
    }

    public ComponentAdder<B> addRaw(Component c) {
        return addRaw(true, c);
    }

    public ComponentAdder<B> addScrolled(Component c) {
        return addScrolled(true, c);
    }

    public ComponentAdder<B> addBar(JButton... buttons) {
        return addBar(true, buttons);
    }

    public ComponentAdder<B> addBar(JCheckBox... checkBoxes) {
        return addBar(true, checkBoxes);
    }

    public ComponentAdder<B> addBar(JRadioButton... radioButtons) {
        return addBar(true, radioButtons);
    }

    public ComponentAdder<B> addStack(JButton... buttons) {
        return addStack(true, buttons);
    }

    public ComponentAdder<B> addStack(JCheckBox... checkBoxes) {
        return addStack(true, checkBoxes);
    }

    public ComponentAdder<B> addStack(JRadioButton... radioButtons) {
        return addStack(true, radioButtons);
    }

    public RendererAdder<B> add(Consumer<B> renderer) {
        return add(true, (Consumer) renderer);
    }

    public ComponentAdder<B> add(String markedLabelText, Object... args) {
        return add(true, markedLabelText, args);
    }

    public ComponentAdder<B> addLabel(String markedText, Object... args) {
        return addLabel(true, markedText, args);
    }

    public ComponentAdder<B> addROLabel(String markedText, Object... args) {
        return addROLabel(true, markedText, args);
    }

    public ComponentAdder<B> addTitle(String markedText, Object... args) {
        return addTitle(true, markedText, args);
    }

    public ComponentAdder<B> addSeparator() {
        return addSeparator(true, "", new Object[0]);
    }

    public ComponentAdder<B> addSeparator(String markedText, Object... args) {
        return addSeparator(true, markedText, args);
    }

    public ComponentAdder<B> add(Icon image) {
        return add(true, image);
    }

    public ComponentAdder<B> add(boolean expression, Component c) {
        if (!expression || c == null) {
            return new NoOpComponentAdder((B) this);
        }
        if ((c instanceof JTable) || (c instanceof JList) || (c instanceof JTree)) {
            return addScrolled(expression, c);
        }
        return addRaw(expression, c);
    }

    public ComponentAdder<B> addRaw(boolean expression, Component c) {
        if (!expression || c == null) {
            return new NoOpComponentAdder((B) this);
        }
        return addImpl(c);
    }

    public ComponentAdder<B> addScrolled(boolean expression, Component c) {
        if (!expression || c == null) {
            return new NoOpComponentAdder((B) this);
        }
        return addImpl(new JScrollPane(c));
    }

    public ComponentAdder<B> addBar(boolean expression, JButton... buttons) {
        if (!expression || buttons == null) {
            return new NoOpComponentAdder((B) this);
        }
        return addImpl(Forms.buttonBar(buttons));
    }

    public ComponentAdder<B> addBar(boolean expression, JCheckBox... checkBoxes) {
        if (!expression) {
            return new NoOpComponentAdder((B) this);
        }
        return addImpl(Forms.checkBoxBar(checkBoxes));
    }

    public ComponentAdder<B> addBar(boolean expression, JRadioButton... radioButtons) {
        if (!expression) {
            return new NoOpComponentAdder((B) this);
        }
        return addImpl(Forms.radioButtonBar(radioButtons));
    }

    public ComponentAdder<B> addStack(boolean expression, JButton... buttons) {
        if (!expression || buttons == null) {
            return new NoOpComponentAdder((B) this);
        }
        return addImpl(Forms.buttonStack(buttons));
    }

    public ComponentAdder<B> addStack(boolean expression, JCheckBox... checkBoxes) {
        if (!expression) {
            return new NoOpComponentAdder((B) this);
        }
        return addImpl(Forms.checkBoxStack(checkBoxes));
    }

    public ComponentAdder<B> addStack(boolean expression, JRadioButton... radioButtons) {
        if (!expression || radioButtons == null) {
            return new NoOpComponentAdder((B) this);
        }
        return addImpl(Forms.radioButtonStack(radioButtons));
    }

    public RendererAdder<B> add(boolean expression, Consumer<B> renderer) {
        return new RendererAdder<>((B) this, expression, renderer);
    }

    public ComponentAdder<B> add(boolean expression, String markedLabelText, Object... args) {
        if (this.defaultLabelType == LabelType.DEFAULT) {
            return addLabel(expression, markedLabelText, args);
        }
        return addROLabel(expression, markedLabelText, args);
    }

    public ComponentAdder<B> addLabel(boolean expression, String markedText, Object... args) {
        return addRaw(expression, getFactory().createLabel(Strings.get(markedText, args)));
    }

    public ComponentAdder<B> addROLabel(boolean expression, String markedText, Object... args) {
        return addRaw(expression, getFactory().createReadOnlyLabel(Strings.get(markedText, args)));
    }

    public ComponentAdder<B> addTitle(boolean expression, String markedText, Object... args) {
        String text = Strings.get(markedText, args);
        return addRaw(expression, getFactory().createTitle(text));
    }

    public ComponentAdder<B> addSeparator(boolean expression, String markedText, Object... args) {
        int alignment = isLeftToRight() ? 2 : 4;
        String text = Strings.get(markedText, args);
        return addRaw(expression, getFactory().createSeparator(text, alignment));
    }

    public ComponentAdder<B> add(boolean expression, Icon image) {
        if (!expression || image == null) {
            return new NoOpComponentAdder((B) this);
        }
        return addImpl(new JLabel(image));
    }

    protected LayoutMap getLayoutMap() {
        if (this.layoutMap == null) {
            this.layoutMap = LayoutMap.getRoot();
        }
        return this.layoutMap;
    }

    protected ComponentFactory getFactory() {
        if (this.factory == null) {
            this.factory = FormsSetup.getComponentFactoryDefault();
        }
        return this.factory;
    }

    protected ComponentAdder<B> addImpl(Component c) {
        if (getPanel().getLayout() == null) {
            this.panel.setLayout(getLayout());
        }
        return new ComponentAdder<>((B) this, c);
    }

    void addImpl(Component component, CellConstraints rawConstraints) {
        CellConstraints translatedConstraints = rawConstraints.translate(this.offsetX, this.offsetY);
        getPanel().add(component, translatedConstraints);
        manageLabelsAndComponents(component);
    }

    private void manageLabelsAndComponents(Component c) {
        if (!this.labelForFeatureEnabled) {
            return;
        }
        if (c instanceof JLabel) {
            JLabel label = (JLabel) c;
            if (label.getLabelFor() == null) {
                setMostRecentlyAddedLabel(label);
                return;
            } else {
                clearMostRecentlyAddedLabel();
                return;
            }
        }
        JLabel mostRecentlyAddedLabel = getMostRecentlyAddedLabel();
        if (mostRecentlyAddedLabel != null && isLabelForApplicable(mostRecentlyAddedLabel, c)) {
            setLabelFor(mostRecentlyAddedLabel, c);
            clearMostRecentlyAddedLabel();
        }
    }

    private static boolean isLabelForApplicable(JLabel label, Component component) {
        if (label.getLabelFor() != null || !component.isFocusable()) {
            return false;
        }
        if (!(component instanceof JComponent)) {
            return true;
        }
        JComponent c = (JComponent) component;
        return c.getClientProperty(LABELED_BY_PROPERTY) == null;
    }

    private static void setLabelFor(JLabel label, Component component) {
        Component labeledComponent;
        if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            labeledComponent = scrollPane.getViewport().getView();
        } else {
            labeledComponent = component;
        }
        label.setLabelFor(labeledComponent);
    }

    private JLabel getMostRecentlyAddedLabel() {
        if (this.mostRecentlyAddedLabelReference == null) {
            return null;
        }
        return this.mostRecentlyAddedLabelReference.get();
    }

    private void setMostRecentlyAddedLabel(JLabel label) {
        this.mostRecentlyAddedLabelReference = new WeakReference<>(label);
    }

    private void clearMostRecentlyAddedLabel() {
        this.mostRecentlyAddedLabelReference = null;
    }

    private boolean isLeftToRight() {
        ComponentOrientation orientation = getPanel().getComponentOrientation();
        return orientation.isLeftToRight() || !orientation.isHorizontal();
    }

    private void checkValidFocusTraversalSetup() {
        InternalFocusSetupUtils.checkValidFocusTraversalSetup(this.focusTraversalPolicy, this.focusTraversalType, this.initialComponent);
    }

    private void setupFocusTraversalPolicyAndProvider() {
        InternalFocusSetupUtils.setupFocusTraversalPolicyAndProvider(getPanel(), this.focusTraversalPolicy, this.focusTraversalType, this.initialComponent);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/builder/AbstractFormBuilder$RendererAdder.class */
    public static final class RendererAdder<T extends AbstractFormBuilder<T>> {
        private final T builder;
        private final boolean expression;
        private final Consumer<T> renderer;

        RendererAdder(T builder, boolean expression, Consumer<T> renderer) {
            this.builder = builder;
            this.expression = expression;
            this.renderer = renderer;
        }

        public T xy(int col, int row) {
            if (this.expression && this.renderer != null) {
                this.builder.translate(col, row);
                this.renderer.accept(this.builder);
                this.builder.translate(-col, -row);
            }
            return this.builder;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/builder/AbstractFormBuilder$ComponentAdder.class */
    public static class ComponentAdder<T extends AbstractFormBuilder<T>> {
        protected final T builder;
        private final Component component;
        private boolean labelForSet = false;

        ComponentAdder(T builder, Component component) {
            this.builder = builder;
            this.component = component;
        }

        public final ComponentAdder<T> labelFor(Component c) {
            Preconditions.checkArgument(this.component instanceof JLabel, "#labelFor is applicable only to JLabels");
            Preconditions.checkArgument(!this.labelForSet, "You must set the label-for-relation only once.");
            this.component.setLabelFor(c);
            this.labelForSet = true;
            return this;
        }

        public final T at(CellConstraints constraints) {
            return add(constraints);
        }

        public final T xy(int col, int row) {
            return at(CC.xy(col, row));
        }

        public final T xy(int col, int row, String encodedAlignments) {
            return at(CC.xy(col, row, encodedAlignments));
        }

        public final T xy(int col, int row, CellConstraints.Alignment colAlign, CellConstraints.Alignment rowAlign) {
            return at(CC.xy(col, row, colAlign, rowAlign));
        }

        public final T xyw(int col, int row, int colSpan) {
            return at(CC.xyw(col, row, colSpan));
        }

        public final T xyw(int col, int row, int colSpan, String encodedAlignments) {
            return at(CC.xyw(col, row, colSpan, encodedAlignments));
        }

        public final T xyw(int col, int row, int colSpan, CellConstraints.Alignment colAlign, CellConstraints.Alignment rowAlign) {
            return at(CC.xyw(col, row, colSpan, colAlign, rowAlign));
        }

        public final T xywh(int col, int row, int colSpan, int rowSpan) {
            return at(CC.xywh(col, row, colSpan, rowSpan));
        }

        public final T xywh(int col, int row, int colSpan, int rowSpan, String encodedAlignments) {
            return at(CC.xywh(col, row, colSpan, rowSpan, encodedAlignments));
        }

        public final T xywh(int col, int row, int colSpan, int rowSpan, CellConstraints.Alignment colAlign, CellConstraints.Alignment rowAlign) {
            return at(CC.xywh(col, row, colSpan, rowSpan, colAlign, rowAlign));
        }

        public final T rc(int row, int col) {
            return at(CC.rc(row, col));
        }

        public final T rc(int row, int col, String encodedAlignments) {
            return at(CC.rc(row, col, encodedAlignments));
        }

        public final T rc(int row, int col, CellConstraints.Alignment rowAlign, CellConstraints.Alignment colAlign) {
            return at(CC.rc(row, col, rowAlign, colAlign));
        }

        public final T rcw(int row, int col, int colSpan) {
            return at(CC.rcw(row, col, colSpan));
        }

        public final T rcw(int row, int col, int colSpan, String encodedAlignments) {
            return at(CC.rcw(row, col, colSpan, encodedAlignments));
        }

        public final T rcw(int row, int col, int colSpan, CellConstraints.Alignment rowAlign, CellConstraints.Alignment colAlign) {
            return at(CC.rcw(row, col, colSpan, rowAlign, colAlign));
        }

        public final T rchw(int row, int col, int rowSpan, int colSpan) {
            return at(CC.rchw(row, col, rowSpan, colSpan));
        }

        public final T rchw(int row, int col, int rowSpan, int colSpan, String encodedAlignments) {
            return at(CC.rchw(row, col, rowSpan, colSpan, encodedAlignments));
        }

        public final T rchw(int row, int col, int rowSpan, int colSpan, CellConstraints.Alignment rowAlign, CellConstraints.Alignment colAlign) {
            return at(CC.rchw(col, row, rowSpan, colSpan, colAlign, rowAlign));
        }

        protected T add(CellConstraints constraints) {
            this.builder.addImpl(this.component, constraints);
            return this.builder;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/builder/AbstractFormBuilder$NoOpComponentAdder.class */
    public static final class NoOpComponentAdder<T extends AbstractFormBuilder<T>> extends ComponentAdder<T> {
        NoOpComponentAdder(T builder) {
            super(builder, null);
        }

        @Override // com.jgoodies.layout.builder.AbstractFormBuilder.ComponentAdder
        protected T add(CellConstraints constraints) {
            return this.builder;
        }
    }
}