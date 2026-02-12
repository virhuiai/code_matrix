package com.jgoodies.layout.debug;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.internal.CommonFormats;
import com.jgoodies.layout.layout.CellConstraints;
import com.jgoodies.layout.layout.ColumnSpec;
import com.jgoodies.layout.layout.FormLayout;
import com.jgoodies.layout.layout.RowSpec;
import java.awt.Container;
import javax.swing.JLabel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/debug/FormDebugUtils.class */
public final class FormDebugUtils {
    private FormDebugUtils() {
    }

    public static void dumpAll(Container container) {
        if (!(container.getLayout() instanceof FormLayout)) {
            System.out.println("The container's layout is not a FormLayout.");
            return;
        }
        FormLayout layout = container.getLayout();
        dumpColumnSpecs(layout);
        dumpRowSpecs(layout);
        System.out.println();
        dumpColumnGroups(layout);
        dumpRowGroups(layout);
        System.out.println();
        dumpConstraints(container);
        dumpGridBounds(container);
    }

    public static void dumpColumnSpecs(FormLayout layout) {
        System.out.print("COLUMN SPECS:");
        for (int col = 1; col <= layout.getColumnCount(); col++) {
            ColumnSpec colSpec = layout.getColumnSpec(col);
            System.out.print(colSpec.toShortString());
            if (col < layout.getColumnCount()) {
                System.out.print(CommonFormats.COMMA_DELIMITER);
            }
        }
        System.out.println();
    }

    public static void dumpRowSpecs(FormLayout layout) {
        System.out.print("ROW SPECS:   ");
        for (int row = 1; row <= layout.getRowCount(); row++) {
            RowSpec rowSpec = layout.getRowSpec(row);
            System.out.print(rowSpec.toShortString());
            if (row < layout.getRowCount()) {
                System.out.print(CommonFormats.COMMA_DELIMITER);
            }
        }
        System.out.println();
    }

    public static void dumpColumnGroups(FormLayout layout) {
        dumpGroups("COLUMN GROUPS: ", layout.getColumnGroups());
    }

    public static void dumpRowGroups(FormLayout layout) {
        dumpGroups("ROW GROUPS:    ", layout.getRowGroups());
    }

    public static void dumpGridBounds(Container container) {
        System.out.println("GRID BOUNDS");
        dumpGridBounds(getLayoutInfo(container));
    }

    public static void dumpGridBounds(FormLayout.LayoutInfo layoutInfo) {
        System.out.print("COLUMN ORIGINS: ");
        for (int columnOrigin : layoutInfo.columnOrigins) {
            System.out.print(columnOrigin + " ");
        }
        System.out.println();
        System.out.print("ROW ORIGINS:    ");
        for (int rowOrigin : layoutInfo.rowOrigins) {
            System.out.print(rowOrigin + " ");
        }
        System.out.println();
    }

    public static void dumpConstraints(Container container) {
        System.out.println("COMPONENT CONSTRAINTS");
        if (!(container.getLayout() instanceof FormLayout)) {
            System.out.println("The container's layout is not a FormLayout.");
            return;
        }
        FormLayout layout = container.getLayout();
        int childCount = container.getComponentCount();
        for (int i = 0; i < childCount; i++) {
            JLabel component = container.getComponent(i);
            CellConstraints cc = layout.getConstraints(component);
            String ccString = cc == null ? "no constraints" : cc.toShortString(layout);
            System.out.print(ccString);
            System.out.print("; ");
            String childType = component.getClass().getName();
            System.out.print(childType);
            if (component instanceof JLabel) {
                JLabel label = component;
                System.out.print("      \"" + label.getText() + "\"");
            }
            if (component.getName() != null) {
                System.out.print("; name=");
                System.out.print(component.getName());
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void dumpGroups(String title, int[][] allGroups) {
        System.out.print(title + " {");
        for (int group = 0; group < allGroups.length; group++) {
            int[] groupIndices = allGroups[group];
            System.out.print(" {");
            for (int i = 0; i < groupIndices.length; i++) {
                System.out.print(groupIndices[i]);
                if (i < groupIndices.length - 1) {
                    System.out.print(CommonFormats.COMMA_DELIMITER);
                }
            }
            System.out.print("} ");
            if (group < allGroups.length - 1) {
                System.out.print(CommonFormats.COMMA_DELIMITER);
            }
        }
        System.out.println("}");
    }

    public static FormLayout.LayoutInfo getLayoutInfo(Container container) {
        Preconditions.checkNotNull(container, Messages.MUST_NOT_BE_NULL, "container");
        Preconditions.checkArgument(container.getLayout() instanceof FormLayout, "The container must use an instance of FormLayout.");
        FormLayout layout = container.getLayout();
        return layout.getLayoutInfo(container);
    }
}
