package com.jgoodies.looks.plastic;

import java.io.File;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.plaf.metal.MetalFileChooserUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticFileChooserUI.class */
public final class PlasticFileChooserUI extends MetalFileChooserUI {
    private final BasicFileView fileView;

    public static ComponentUI createUI(JComponent c) {
        return new PlasticFileChooserUI((JFileChooser) c);
    }

    public PlasticFileChooserUI(JFileChooser fileChooser) {
        super(fileChooser);
        this.fileView = new SystemIconFileView();
    }

    public void clearIconCache() {
        this.fileView.clearIconCache();
    }

    public FileView getFileView(JFileChooser fc) {
        return this.fileView;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticFileChooserUI$SystemIconFileView.class */
    private final class SystemIconFileView extends BasicFileView {
        private SystemIconFileView() {
            super(PlasticFileChooserUI.this);
        }

        public Icon getIcon(File f) {
            Icon icon = getCachedIcon(f);
            if (icon != null) {
                return icon;
            }
            if (f != null && UIManager.getBoolean("FileChooser.useSystemIcons")) {
                icon = PlasticFileChooserUI.this.getFileChooser().getFileSystemView().getSystemIcon(f);
            }
            if (icon == null) {
                return super.getIcon(f);
            }
            cacheIcon(f, icon);
            return icon;
        }
    }
}
