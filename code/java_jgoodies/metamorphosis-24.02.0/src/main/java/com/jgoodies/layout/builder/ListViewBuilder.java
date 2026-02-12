package com.jgoodies.layout.builder;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.BuilderSupport;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.internal.CommonFormats;
import com.jgoodies.layout.FormsSetup;
import com.jgoodies.layout.factories.ComponentFactory;
import com.jgoodies.layout.factories.Forms;
import com.jgoodies.layout.factories.Paddings;
import com.jgoodies.layout.internal.InternalFocusSetupUtils;
import com.jgoodies.layout.util.FocusTraversalType;
import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.Border;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/builder/ListViewBuilder.class */
public final class ListViewBuilder {
    private ComponentFactory factory;
    private JComponent label;
    private JComponent filterView;
    private JComponent listView;
    private JComponent listBarView;
    private JComponent listExtrasView;
    private JComponent detailsView;
    private JComponent listStackView;
    private Border border;
    private Component initialComponent;
    private FocusTraversalType focusTraversalType;
    private FocusTraversalPolicy focusTraversalPolicy;
    private JComponent panel;
    private final BuilderSupport support = new BuilderSupport();
    private boolean honorsVisibility = true;
    private boolean consistentHeaderHeight = false;
    private String namePrefix = "ListView";
    private String filterViewColSpec = "[100dlu, p]";
    private String listViewRowSpec = "fill:[100dlu, d]:grow";
    private int verticalScrollBarPolicy = -1;
    private int horizontalScrollBarPolicy = -1;

    public ListViewBuilder border(Border border) {
        this.support.checkNotCalledTwice("border or #padding");
        this.border = border;
        invalidatePanel();
        return this;
    }

    public ListViewBuilder padding(Paddings.Padding padding) {
        border(padding);
        return this;
    }

    public ListViewBuilder padding(String paddingSpec, Object... args) {
        padding(Paddings.createPadding(paddingSpec, args));
        return this;
    }

    public ListViewBuilder initialComponent(JComponent initialComponent) {
        this.support.checkNotCalledTwice("initialComponent");
        Preconditions.checkNotNull(initialComponent, Messages.MUST_NOT_BE_NULL, "initial component");
        checkValidFocusTraversalSetup();
        this.initialComponent = initialComponent;
        return this;
    }

    public ListViewBuilder focusTraversalType(FocusTraversalType focusTraversalType) {
        this.support.checkNotCalledTwice("focusTraversalType or #focusTraversalPolicy");
        Preconditions.checkNotNull(focusTraversalType, Messages.MUST_NOT_BE_NULL, "focus traversal type");
        checkValidFocusTraversalSetup();
        this.focusTraversalType = focusTraversalType;
        return this;
    }

    public ListViewBuilder focusTraversalPolicy(FocusTraversalPolicy policy) {
        this.support.checkNotCalledTwice("focusTraversalType or #focusTraversalPolicy");
        Preconditions.checkNotNull(policy, Messages.MUST_NOT_BE_NULL, "focus traversal policy");
        checkValidFocusTraversalSetup();
        this.focusTraversalPolicy = policy;
        return this;
    }

    public ListViewBuilder honorVisibility(boolean b) {
        this.support.checkNotCalledTwice("honorsVisibility");
        this.honorsVisibility = b;
        invalidatePanel();
        return this;
    }

    public ListViewBuilder consistentHeaderHeight(boolean b) {
        this.support.checkNotCalledTwice("consistentHeaderHeight");
        this.consistentHeaderHeight = b;
        invalidatePanel();
        return this;
    }

    public ListViewBuilder namePrefix(String namePrefix) {
        this.support.checkNotCalledTwice("namePrefix");
        this.namePrefix = namePrefix;
        return this;
    }

    public ListViewBuilder factory(ComponentFactory factory) {
        this.support.checkNotCalledTwice("factory");
        this.factory = factory;
        return this;
    }

    public ListViewBuilder label(JComponent labelView) {
        this.support.checkNotCalledTwice("label, #labelText, or #headerText");
        this.label = labelView;
        overrideNameIfBlank(labelView, "label");
        invalidatePanel();
        return this;
    }

    public ListViewBuilder labelText(String markedText, Object... args) {
        label(getFactory().createLabel(Strings.get(markedText, args)));
        return this;
    }

    public ListViewBuilder headerText(String markedText, Object... args) {
        label(getFactory().createHeaderLabel(Strings.get(markedText, args)));
        return this;
    }

    public ListViewBuilder filterView(JComponent filterView) {
        this.support.checkNotCalledTwice("filterView");
        this.filterView = filterView;
        overrideNameIfBlank(filterView, "filter");
        invalidatePanel();
        return this;
    }

    public ListViewBuilder filterViewColumn(String colSpec, Object... args) {
        this.support.checkNotCalledTwice("filterViewColumn");
        Preconditions.checkNotNull(colSpec, Messages.MUST_NOT_BE_BLANK, "filter view column specification");
        this.filterViewColSpec = Strings.get(colSpec, args);
        invalidatePanel();
        return this;
    }

    public ListViewBuilder listView(JComponent listView) {
        this.support.checkNotCalledTwice("listView");
        Preconditions.checkNotNull(listView, Messages.MUST_NOT_BE_BLANK, "list view");
        this.listView = listView;
        overrideNameIfBlank(listView, "listView");
        invalidatePanel();
        return this;
    }

    public ListViewBuilder listViewRow(String rowSpec, Object... args) {
        this.support.checkNotCalledTwice("listViewRow");
        Preconditions.checkNotNull(rowSpec, Messages.MUST_NOT_BE_BLANK, "list view row specification");
        this.listViewRowSpec = Strings.get(rowSpec, args);
        invalidatePanel();
        return this;
    }

    public ListViewBuilder listVerticalScrollBarPolicy(int policy) {
        this.verticalScrollBarPolicy = policy;
        return this;
    }

    public ListViewBuilder listHorizontalScrollBarPolicy(int policy) {
        this.horizontalScrollBarPolicy = policy;
        return this;
    }

    public ListViewBuilder listBarView(JComponent listBarView) {
        this.support.checkNotCalledTwice("listBar or #listBarView");
        this.listBarView = listBarView;
        overrideNameIfBlank(listBarView, "listBarView");
        invalidatePanel();
        return this;
    }

    public ListViewBuilder listBar(JComponent... buttons) {
        listBarView(Forms.buttonBar(buttons));
        return this;
    }

    public ListViewBuilder listStackView(JComponent listStackView) {
        this.support.checkNotCalledTwice("listStack or #listStackView");
        this.listStackView = listStackView;
        overrideNameIfBlank(listStackView, "listStackView");
        invalidatePanel();
        return this;
    }

    public ListViewBuilder listStack(JComponent... buttons) {
        listStackView(Forms.buttonStack(buttons));
        return this;
    }

    public ListViewBuilder listExtrasView(JComponent listExtrasView) {
        this.support.checkNotCalledTwice("listExtrasView");
        this.listExtrasView = listExtrasView;
        overrideNameIfBlank(listExtrasView, "listExtrasView");
        invalidatePanel();
        return this;
    }

    public ListViewBuilder detailsView(JComponent detailsView) {
        this.support.checkNotCalledTwice("detailsView");
        this.detailsView = detailsView;
        overrideNameIfBlank(detailsView, "detailsView");
        invalidatePanel();
        return this;
    }

    public JComponent build() {
        if (this.panel == null) {
            this.panel = buildPanel();
        }
        return this.panel;
    }

    private ComponentFactory getFactory() {
        if (this.factory == null) {
            this.factory = FormsSetup.getComponentFactoryDefault();
        }
        return this.factory;
    }

    private void invalidatePanel() {
        this.panel = null;
    }

    private JComponent buildPanel() {
        Preconditions.checkNotNull(this.listView, "The list view must be set before #build is invoked.");
        String stackGap = hasStack() ? "$rg" : CommonFormats.ALMOST_ZERO;
        String detailsGap = hasDetails() ? "8dlu" : CommonFormats.ALMOST_ZERO;
        FormBuilder builder = new FormBuilder().columns("fill:default:grow, %s, p", stackGap).rows("p, %1$s, p, %2$s, p", this.listViewRowSpec, detailsGap).honorsVisibility(this.honorsVisibility).border(this.border).add(hasHeader(), (Component) buildHeader()).xy(1, 1).add(true, (Component) getListView()).xy(1, 2).add(hasOperations(), (Component) buildOperations()).xy(1, 3).add(hasStack(), (Component) this.listStackView).xy(3, 2).add(hasDetails(), (Component) this.detailsView).xy(1, 5);
        if (this.label instanceof JLabel) {
            JLabel theLabel = this.label;
            if (theLabel.getLabelFor() == null) {
                theLabel.setLabelFor(this.listView);
            }
        }
        InternalFocusSetupUtils.setupFocusTraversalPolicyAndProvider(builder.getPanel(), this.focusTraversalPolicy, this.focusTraversalType, this.initialComponent);
        return builder.build();
    }

    private JComponent buildHeader() {
        if (!hasHeader()) {
            return null;
        }
        String columnSpec = hasFilter() ? "default:grow, 9dlu, %s" : "default:grow, 0,    0";
        FormBuilder columns = new FormBuilder().columns(columnSpec, this.filterViewColSpec);
        Object[] objArr = new Object[1];
        objArr[0] = (this.consistentHeaderHeight || hasFilter()) ? "[14dlu, p]" : "p";
        return columns.rows("%s, $lcg", objArr).labelForFeatureEnabled(false).add(hasLabel(), (Component) this.label).xy(1, 1).add(hasFilter(), (Component) this.filterView).xy(3, 1).build();
    }

    private JScrollPane buildScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        if (this.verticalScrollBarPolicy != -1) {
            scrollPane.setVerticalScrollBarPolicy(this.verticalScrollBarPolicy);
        }
        if (this.horizontalScrollBarPolicy != -1) {
            scrollPane.setHorizontalScrollBarPolicy(this.horizontalScrollBarPolicy);
        }
        return scrollPane;
    }

    private JComponent buildOperations() {
        if (!hasOperations()) {
            return null;
        }
        String gap = hasListExtras() ? "9dlu" : CommonFormats.ALMOST_ZERO;
        return new FormBuilder().columns("left:default, %s:grow, right:pref", gap).rows("$rgap, p", new Object[0]).honorsVisibility(this.honorsVisibility).add(hasListBar(), (Component) this.listBarView).xy(1, 2).add(hasListExtras(), (Component) this.listExtrasView).xy(3, 2).build();
    }

    private JComponent getListView() {
        return ((this.listView instanceof JTable) || (this.listView instanceof JList) || (this.listView instanceof JTree)) ? buildScrollPane(this.listView) : this.listView;
    }

    private boolean hasLabel() {
        return this.label != null;
    }

    private boolean hasFilter() {
        return this.filterView != null;
    }

    private boolean hasHeader() {
        return hasLabel() || hasFilter();
    }

    private boolean hasListBar() {
        return this.listBarView != null;
    }

    private boolean hasListExtras() {
        return this.listExtrasView != null;
    }

    private boolean hasOperations() {
        return hasListBar() || hasListExtras();
    }

    private boolean hasStack() {
        return this.listStackView != null;
    }

    private boolean hasDetails() {
        return this.detailsView != null;
    }

    private void overrideNameIfBlank(JComponent component, String suffix) {
        if (component != null && Strings.isBlank(component.getName())) {
            component.setName(this.namePrefix + '.' + suffix);
        }
    }

    private void checkValidFocusTraversalSetup() {
        InternalFocusSetupUtils.checkValidFocusTraversalSetup(this.focusTraversalPolicy, this.focusTraversalType, this.initialComponent);
    }
}
