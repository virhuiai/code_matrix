package com.jgoodies.framework.component;

import com.jgoodies.application.TaskMonitor;
import com.jgoodies.layout.builder.FormBuilder;
import com.jgoodies.layout.factories.Paddings;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/component/OverlayProgressView.class */
public final class OverlayProgressView extends AbstractProgressView {
    private JLabel messageLabel;
    private JProgressBar progressBar;

    public OverlayProgressView(TaskMonitor taskMonitor) {
        super(taskMonitor);
    }

    private void initComponents() {
        this.messageLabel = new JLabel("");
        this.progressBar = new JProgressBar();
    }

    @Override // com.jgoodies.framework.component.AbstractProgressView
    protected JComponent buildPanel() {
        initComponents();
        JPanel panel = new FormBuilder().columns("right:[25dlu,pref], 6dlu, 30dlu", new Object[0]).rows("p", new Object[0]).background(new Color(230, 230, 230)).border(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), Paddings.createPadding("4, 4, 4, 4", new Object[0]))).add((Component) this.messageLabel).xy(1, 1).add((Component) this.progressBar).xy(3, 1).build();
        panel.setVisible(false);
        return panel;
    }

    @Override // com.jgoodies.framework.component.AbstractProgressView
    public void setMessageForeground(Color color) {
        getPanel();
        this.messageLabel.setForeground(color);
    }

    @Override // com.jgoodies.framework.component.AbstractProgressView
    public void progressMessage(String message) {
        getPanel();
        this.messageLabel.setText(message);
    }

    @Override // com.jgoodies.framework.component.AbstractProgressView
    public void progressValue(int value) {
        getPanel();
        this.progressBar.setValue(value);
    }

    @Override // com.jgoodies.framework.component.AbstractProgressView
    public void progressVisible(boolean b) {
        getPanel().setVisible(b);
    }

    @Override // com.jgoodies.framework.component.AbstractProgressView
    public void progressIndeterminate(boolean b) {
        getPanel();
        this.progressBar.setIndeterminate(b);
    }
}
