package com.jgoodies.components.util;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/TreeUtils.class */
public final class TreeUtils {
    private TreeUtils() {
    }

    public static void collapseAllSiblings(JTree tree, TreePath aPath) {
        TreeModel model = tree.getModel();
        Object node = aPath.getLastPathComponent();
        collapseChildren(tree, aPath);
        TreePath parentPath = aPath.getParentPath();
        while (true) {
            TreePath path = parentPath;
            if (path != null) {
                Object parent = path.getLastPathComponent();
                int siblingCount = model.getChildCount(parent);
                for (int i = 0; i < siblingCount; i++) {
                    Object aSibling = model.getChild(parent, i);
                    if (aSibling != node) {
                        TreePath siblingPath = path.pathByAddingChild(aSibling);
                        tree.collapsePath(siblingPath);
                    }
                }
                node = parent;
                parentPath = path.getParentPath();
            } else {
                return;
            }
        }
    }

    public static void collapseChildren(JTree tree, TreePath path) {
        TreeModel model = tree.getModel();
        Object parent = path.getLastPathComponent();
        int childCount = model.getChildCount(parent);
        for (int i = 0; i < childCount; i++) {
            Object child = model.getChild(parent, i);
            TreePath childPath = path.pathByAddingChild(child);
            tree.collapsePath(childPath);
        }
        tree.collapsePath(path);
    }

    public static void expand(JTree tree, int maxLevelToExpand) {
        for (int level = 0; level < maxLevelToExpand; level++) {
            for (int row = tree.getRowCount(); row >= 0; row--) {
                tree.expandRow(row);
            }
        }
    }

    public static void expandTopLevel(JTree tree) {
        expand(tree, 1);
    }
}
