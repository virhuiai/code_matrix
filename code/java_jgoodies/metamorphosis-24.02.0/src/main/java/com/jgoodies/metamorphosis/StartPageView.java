package com.jgoodies.metamorphosis;

import com.jgoodies.animation.Animation;
import com.jgoodies.animation.Animator;
import com.jgoodies.animation.swing.animations.BasicTextAnimations;
import com.jgoodies.animation.swing.animations.FanAnimation;
import com.jgoodies.animation.swing.components.BasicTextLabel;
import com.jgoodies.animation.swing.components.FanComponent;
import com.jgoodies.animation.swing.components.GlyphLabel;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.layout.builder.FormBuilder;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/metamorphosis/StartPageView.class */
public final class StartPageView {
    private final Metamorphosis metamorphosis;
    private final List<JButton> buttons = new LinkedList();
    private FanComponent fan;
    private BasicTextLabel label1;
    private BasicTextLabel label2;
    private GlyphLabel glyphLabel;
    private Animator animator;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StartPageView(Metamorphosis metamorphosis) {
        this.metamorphosis = metamorphosis;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start() {
        this.animator.start();
    }

    private void initComponents() {
        Color baseColor = Color.LIGHT_GRAY;
        this.fan = new FanComponent(10, baseColor);
        this.fan.addMouseMotionListener(new MouseMotionAdapter() { // from class: com.jgoodies.metamorphosis.StartPageView.1
            public void mouseMoved(MouseEvent e) {
                StartPageView.this.fan.setOrigin(e.getPoint());
            }
        });
        this.label1 = new BasicTextLabel(" ");
        this.label1.setFont(Metamorphosis.getH1Font());
        this.label2 = new BasicTextLabel(" ");
        this.label2.setFont(Metamorphosis.getH1Font());
        this.glyphLabel = new GlyphLabel(" ", 5000L, 100L);
        this.glyphLabel.setFont(Metamorphosis.getH1Font());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Component build() {
        initComponents();
        JPanel panel = new FormBuilder().columns("25epx, 0:grow, right:pref, 4dlu, pref, 0:grow, 25epx", new Object[0]).rows("25epx, 0:grow, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 25epx:grow, pref, 25px", new Object[0]).background(Color.WHITE).add(label("Step 1:")).xy(3, 3).add((Component) button("Show Franken UI", Style.FRANKEN)).xy(5, 3).add(label("Step 2:")).xy(3, 5).add((Component) button("Show Rookie UI", Style.ROOKIE)).xy(5, 5).add(label("Step 3:")).xy(3, 7).add((Component) button("Show Standard UI", Style.STANDARD)).xy(5, 7).add(label("Step 4:")).xy(3, 9).add((Component) button("Show Advanced UI", Style.ADVANCED)).xy(5, 9).add(label("Step 5:")).xy(3, 11).add((Component) button("Show Elegant UI", Style.ELEGANT)).xy(5, 11).add(buildAnimationPanel()).xyw(2, 13, 5).build();
        this.animator = new Animator(createAnimation(), 30);
        return panel;
    }

    private Component buildAnimationPanel() {
        return new FormBuilder().columns("fill:200epx:grow", new Object[0]).rows("fill:50epx", new Object[0]).border(BorderFactory.createLineBorder(Color.GRAY)).add((Component) this.label1).xy(1, 1).add((Component) this.label2).xy(1, 1).add((Component) this.glyphLabel).xy(1, 1).add((Component) this.fan).xy(1, 1).build();
    }

    private static Component label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Metamorphosis.getH2Font());
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private JButton button(String label, Style style) {
        JButton button = new JButton(label);
        button.setOpaque(!SystemUtils.IS_OS_MAC);
        button.addActionListener(evt -> {
            this.metamorphosis.doLaunch(style);
        });
        this.buttons.add(button);
        return button;
    }

    private Animation createAnimation() {
        Color color = Color.DARK_GRAY;
        Animation fade1 = BasicTextAnimations.defaultFade(this.label1, this.label2, 4500L, 0L, "JGoodies.com|DESIGN|CONSULTING|LIBRARIES|SUPPORT", color);
        Animation fade2 = BasicTextAnimations.defaultFade(this.label1, this.label2, 4500L, -250L, "JGoodies.com|DESIGN|CONSULTING|LIBRARIES|SUPPORT", color);
        Animation fade3 = BasicTextAnimations.defaultFade(this.label1, this.label2, 4500L, -500L, "JGoodies.com|DESIGN|CONSULTING|LIBRARIES|SUPPORT", color);
        Animation space = BasicTextAnimations.defaultSpace(this.label1, this.label2, 4500L, 0L, "JGoodies.com|DESIGN|CONSULTING|LIBRARIES|SUPPORT", color);
        Animation fanAnimation = FanAnimation.defaultFan(this.fan, 20000L);
        Animation typo = Animation.sequential(fade1, fade2, fade3, space);
        return Animation.repeat(Animation.parallel(Animation.repeat(fanAnimation, 1000.0f), Animation.repeat(typo, 1000.0f)), 1000.0f);
    }
}
