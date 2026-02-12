package com.jgoodies.framework.search;

import com.jgoodies.application.ResourceMap;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.layout.builder.FormBuilder;
import com.jgoodies.layout.factories.Paddings;
import com.jgoodies.layout.layout.FormLayout;
import com.jgoodies.quicksearch.Activatable;
import com.jgoodies.quicksearch.QuickSearchManager;
import com.jgoodies.quicksearch.QuickSearchProcessEvent;
import javax.swing.Icon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/QuickSearchCategoryView.class */
public final class QuickSearchCategoryView {
    private final QuickSearchManager manager;
    private final ResourceMap resources;
    private final JPanel container;
    private JComponent categoryArea;
    private JComponent separator;
    private final List<ResultRow> rows = new ArrayList();
    private int selectionIndex = -1;
    private final List<RowComponent> rowComponents = new ArrayList(this.rows.size());

    /* JADX INFO: Access modifiers changed from: package-private */
    public QuickSearchCategoryView(QuickSearchManager manager, ResourceMap resources) {
        this.manager = (QuickSearchManager) Preconditions.checkNotNull(manager, Messages.MUST_NOT_BE_NULL, "QuickSearchManager");
        this.resources = (ResourceMap) Preconditions.checkNotNull(resources, Messages.MUST_NOT_BE_NULL, "ResourceMap");
        initComponents();
        this.container = new JPanel(new BorderLayout());
    }

    private void initComponents() {
        this.categoryArea = new JPanel((LayoutManager) null);
        this.categoryArea.setBackground(this.resources.getColor("QuickSearch.categoryArea.background"));
        this.separator = createVerticalSeparator();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JComponent getPanel() {
        return this.container;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void update(List<Activatable> activatables) {
        int oldSelectionIndex = getSelectionIndex();
        this.rows.clear();
        this.rowComponents.clear();
        this.rows.addAll(flatten(categorizeAndSort(activatables)));
        if (this.manager.getOpenPreferencesAction() != null && hasSearchText()) {
            this.rows.add(new ResultRow(this.manager.getOpenPreferencesAction()));
        }
        this.container.removeAll();
        this.container.add(buildContent(), "Center");
        if (this.rowComponents.isEmpty()) {
            setSelectionIndex(-1);
        } else {
            setSelectionIndex(Math.max(0, Math.min(lastIndex(), oldSelectionIndex)));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void activateSelection(EventObject evt) {
        int index = getSelectionIndex();
        if ((!noResultFound() || index == 0) && index >= 0) {
            this.rows.get(index).activate(evt);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearSelection() {
        setSelectionIndex(-1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void selectFirst() {
        if (!this.rowComponents.isEmpty()) {
            setSelectionIndex(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void selectLast() {
        setSelectionIndex(this.rowComponents.size() - 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void selectPrevious() {
        if (getSelectionIndex() > 0) {
            setSelectionIndex(getSelectionIndex() - 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void selectNext() {
        if (getSelectionIndex() < lastIndex()) {
            setSelectionIndex(getSelectionIndex() + 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void select(MouseEvent evt) {
        Point translatedPoint = new Point(evt.getPoint());
        Point origin = getPanel().getLocation();
        translatedPoint.translate(origin.x, origin.y);
        for (RowComponent rowComponent : this.rowComponents) {
            if (rowComponent.contains(evt.getPoint())) {
                setSelectionIndex(this.rowComponents.indexOf(rowComponent));
            }
        }
    }

    private int lastIndex() {
        return this.rowComponents.size() - 1;
    }

    private int getSelectionIndex() {
        return this.selectionIndex;
    }

    private void setSelectionIndex(int newIndex) {
        int oldIndex = getSelectionIndex();
        if (oldIndex != -1 && oldIndex < this.rowComponents.size()) {
            this.rowComponents.get(oldIndex).setSelected(false);
        }
        this.selectionIndex = newIndex;
        if (newIndex != -1) {
            this.rowComponents.get(newIndex).setSelected(true);
        }
        this.container.repaint();
    }

    private JComponent buildContent() {
        FormBuilder builder = new FormBuilder().columns("3dlu, right:65dlu, 3dlu, p, 2dlu, 3epx, 100dlu, 3dlu", new Object[0]).rows("", new Object[0]).background(this.resources.getColor("QuickSearch.resultArea.background")).panel(new JPanel());
        builder.getPanel().setFocusable(false);
        int row = 0;
        if (noResultFound()) {
            builder.appendRows("4dlu", new Object[0]);
            int row2 = 0 + 1;
            builder.appendRows("p", new Object[0]);
            row = row2 + 1;
            JLabel label = new JLabel(this.resources.getString("QuickSearch.noResultFound", new Object[0]));
            label.setFont(label.getFont().deriveFont(2));
            builder.add((Component) label).xy(7, row);
        }
        Iterator<ResultRow> it = this.rows.iterator();
        while (it.hasNext()) {
            if (it.next().gap) {
                builder.appendRows("4dlu", new Object[0]);
            }
            builder.appendRows("p", new Object[0]);
        }
        FormLayout layout = (FormLayout) builder.getPanel().getLayout(); // 明确转换为 FormLayout
        if (layout.getRowCount() == 0) {
            return builder.build();
        }
        builder.appendRows("4dlu", new Object[0]);
        int rowCount = layout.getRowCount();
        builder.add((Component) this.separator).xywh(4, 1, 1, rowCount);
        for (ResultRow resultRow : this.rows) {
            row += resultRow.gap ? 2 : 1;
            RowComponent rowComponent = new RowComponent(resultRow);
            this.rowComponents.add(rowComponent);
            rowComponent.addTo(builder, row);
        }
        builder.add((Component) this.categoryArea).xywh(1, 1, 3, rowCount);
        return builder.build();
    }

    private boolean hasSearchText() {
        return Strings.isNotBlank(this.manager.getSearchText());
    }

    private boolean noResultFound() {
        return this.manager.getState() == QuickSearchProcessEvent.State.PROCESS_SUCCEEDED && hasSearchText() && this.manager.getActivatables().isEmpty();
    }

    private static JComponent createVerticalSeparator() {
        JPanel panel = new JPanel((LayoutManager) null);
        panel.setBackground(new ColorUIResource(233, 233, 233));
        return panel;
    }

    private static List<Category> categorizeAndSort(List<Activatable> activatables) {
        Map<String, Category> map = new HashMap<>();
        for (Activatable activatable : activatables) {
            String categoryName = activatable.getCategory();
            Category category = map.get(categoryName);
            if (category == null) {
                category = new Category(categoryName);
                map.put(categoryName, category);
            }
            category.add(activatable);
        }
        List<Category> categories = new ArrayList<>(map.values());
        Collections.sort(categories);
        return categories;
    }

    private static List<ResultRow> flatten(List<Category> categories) {
        List<ResultRow> result = new ArrayList<>();
        for (Category category : categories) {
            boolean first = true;
            for (Activatable activatable : category.getSortedList()) {
                result.add(new ResultRow(first, activatable));
                first = false;
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/QuickSearchCategoryView$Category.class */
    public static final class Category implements Comparable<Category> {
        private final String name;
        private final List<Activatable> activatables = new ArrayList();
        private int maxRank = 0;

        Category(String name) {
            this.name = name;
        }

        void add(Activatable activatable) {
            this.maxRank = Math.max(this.maxRank, activatable.getRank());
            this.activatables.add(activatable);
        }

        String getName() {
            return this.name;
        }

        List<Activatable> getSortedList() {
            Collections.sort(this.activatables, (x$0, x$1) -> {
                return QuickSearchCategoryView.compareRank(x$0, x$1);
            });
            return Collections.unmodifiableList(this.activatables);
        }

        @Override // java.lang.Comparable
        public int compareTo(Category c) {
            if (this == c) {
                return 0;
            }
            int r1 = this.maxRank;
            int r2 = c.maxRank;
            if (r1 == r2) {
                return getName().compareTo(c.getName());
            }
            if (r1 < r2) {
                return 1;
            }
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    private static int compareRank(Activatable o1, Activatable o2) {
        if (o1 == o2) {
            return 0;
        }
        int r1 = o1.getRank();
        int r2 = o2.getRank();
        if (r1 == r2) {
            return o1.getDisplayString().compareTo(o2.getDisplayString());
        }
        return r1 > r2 ? -1 : 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/QuickSearchCategoryView$ResultRow.class */
    public static final class ResultRow {
        private final boolean gap;
        private final String category;
        private final Activatable activatable;

        private ResultRow(boolean gap, String category, Activatable activatable) {
            this.gap = gap;
            this.category = category;
            this.activatable = activatable;
        }

        ResultRow(boolean first, Activatable activatable) {
            this(first, first ? activatable.getCategory() : null, activatable);
        }

        ResultRow(Action action) {
            this(true, null, new ActionActivatable("unused", action, -1)); // 确保导入了 ActionActivatable
        }

        Icon getIcon() {
            return this.activatable.getIcon();
        }

        String getDescription() {
            return this.activatable.getDisplayString();
        }

        void activate(EventObject evt) {
            this.activatable.activate(evt);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/QuickSearchCategoryView$RowComponent.class */
    public static final class RowComponent {
        JComponent categoryLabel;
        JComponent iconLabel;
        JLabel descriptionLabel;
        JPanel background;

        RowComponent(ResultRow row) {
            this.categoryLabel = row.category == null ? null : createCategoryLabel(row.category);
            this.iconLabel = row.getIcon() == null ? null : new JLabel(row.getIcon());
            this.descriptionLabel = new JLabel(row.getDescription());
            this.descriptionLabel.setBorder(Paddings.createPadding("1dlu, 0, 1dlu, 0", new Object[0]));
            this.background = new JPanel((LayoutManager) null);
            this.background.setBackground(UIManager.getColor("List.selectionBackground"));
            setSelected(false);
        }

        void setSelected(boolean selected) {
            Color foreground = UIManager.getColor(selected ? "List.selectionForeground" : "List.foreground");
            if (this.categoryLabel != null) {
                this.categoryLabel.setForeground(foreground);
            }
            this.descriptionLabel.setForeground(foreground);
            this.background.setOpaque(selected);
        }

        void addTo(FormBuilder builder, int row) {
            if (this.categoryLabel != null) {
                builder.add((Component) this.categoryLabel).xy(2, row);
            }
            if (this.iconLabel != null) {
            }
            builder.add((Component) this.descriptionLabel).xy(7, row);
            builder.add((Component) this.background).xyw(1, row, 8, "fill, fill");
        }

        boolean contains(Point p) {
            return this.background.getBounds().contains(p);
        }

        private static JComponent createCategoryLabel(String category) {
            JLabel label = new JLabel(category);
            Font smallerFont = label.getFont().deriveFont(label.getFont().getSize2D() - 1.0f);
            label.setFont(smallerFont);
            label.setBorder(new EmptyBorder(1, 0, 0, 0));
            return label;
        }
    }
}