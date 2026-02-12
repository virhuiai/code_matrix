package com.jgoodies.animation.swing.components;

import com.jgoodies.animation.AbstractAnimation;
import com.jgoodies.animation.Animation;
import com.jgoodies.animation.AnimationAdapter;
import com.jgoodies.animation.AnimationEvent;
import com.jgoodies.animation.Animator;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.FontUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/swing/components/AnimatedLabel.class */
public final class AnimatedLabel extends JPanel {
    public static final String PROPERTY_ANIMATED = "animated";
    public static final String PROPERTY_DURATION = "duration";
    public static final String PROPERTY_FOREGROUND = "foreground";
    public static final String PROPERTY_TEXT = "text";
    public static final int RIGHT = 4;
    public static final int CENTER = 0;
    public static final int LEFT = 2;
    public static final Color DEFAULT_BASE_COLOR = new Color(64, 64, 64);
    public static final int DEFAULT_FONT_EXTRA_SIZE = 8;
    private static final int DEFAULT_DURATION = 300;
    private static final int DEFAULT_ANIMATION_FPS = 30;
    private JLabel[] labels;
    private int foreground;
    private int background;
    private Color baseColor;
    private boolean animated;
    private final int orientation;
    private long duration;
    private final int fps;
    private Animation animation;
    private Animator animator;

    public AnimatedLabel() {
        this(DEFAULT_BASE_COLOR, 8, "");
    }

    public AnimatedLabel(Color baseColor, int fontExtraSize, String text) {
        this(baseColor, fontExtraSize, text, 2);
    }

    public AnimatedLabel(Color baseColor, int fontExtraSize, String text, int orientation) {
        this(baseColor, fontExtraSize, text, orientation, DEFAULT_DURATION, DEFAULT_ANIMATION_FPS);
    }

    public AnimatedLabel(Color baseColor, int fontExtraSize, String text, int orientation, int duration, int framesPerSecond) {
        super((LayoutManager) null);
        this.foreground = 0;
        this.background = 1;
        this.baseColor = baseColor;
        this.orientation = orientation;
        this.duration = duration;
        this.fps = framesPerSecond;
        this.animated = true;
        initComponents(fontExtraSize);
        build();
        setTextImmediately(text);
    }

    public boolean isAnimated() {
        return this.animated;
    }

    public long getDuration() {
        return this.duration;
    }

    public Color getForeground() {
        return this.baseColor;
    }

    public synchronized String getText() {
        return this.labels[this.foreground].getText();
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
        firePropertyChange(PROPERTY_ANIMATED, animated, animated);
    }

    public void setDuration(long newDuration) {
        long oldDuration = this.duration;
        this.duration = newDuration;
        this.animation = null;
        firePropertyChange(PROPERTY_DURATION, oldDuration, newDuration);
    }

    public void setForeground(Color newForeground) {
        Color oldForeground = getForeground();
        this.baseColor = newForeground;
        firePropertyChange(PROPERTY_FOREGROUND, oldForeground, newForeground);
    }

    public synchronized void setText(String newText) {
        String oldText = getText();
        if (oldText.equals(newText)) {
            return;
        }
        if (!isAnimated()) {
            setTextImmediately(newText);
            return;
        }
        this.labels[this.background].setText(newText);
        this.foreground = 1 - this.foreground;
        this.background = 1 - this.background;
        if (this.animator != null) {
            this.animator.stop();
        }
        this.animator = new Animator(animation(), this.fps);
        this.animator.start();
        firePropertyChange("text", oldText, newText);
    }

    public void setTextImmediately(String newText) {
        String oldText = getText();
        this.labels[this.background].setText(newText);
        this.foreground = 1 - this.foreground;
        this.background = 1 - this.background;
        setAlpha(255, 0);
        firePropertyChange("text", oldText, newText);
    }

    private Animation animation() {
        if (this.animation == null) {
            this.animation = new BlendOverAnimation(this.duration);
            this.animation.addAnimationListener(new AnimationAdapter() { // from class: com.jgoodies.animation.swing.components.AnimatedLabel.1
                @Override // com.jgoodies.animation.AnimationAdapter, com.jgoodies.animation.AnimationListener
                public void animationStopped(AnimationEvent e) {
                    AnimatedLabel.this.setAlpha(255, 0);
                }
            });
        }
        return this.animation;
    }

    private void initComponents(int fontExtraSize) {
        this.labels = new JLabel[2];
        this.labels[this.foreground] = createBoldLabel(fontExtraSize, getTranslucentColor(255));
        this.labels[this.background] = createBoldLabel(fontExtraSize, getTranslucentColor(255));
    }

    private void build() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = anchor();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(this.labels[this.foreground], gbc);
        add(this.labels[this.background], gbc);
    }

    private int anchor() {
        if (this.orientation == 4) {
            return 13;
        }
        if (this.orientation == 0) {
            return 10;
        }
        return 17;
    }

    private static JLabel createBoldLabel(int sizeIncrement, Color aForeground) {
        JLabel label = new BoldLargerLabel(sizeIncrement);
        label.setForeground(aForeground);
        return label;
    }

    private Color getTranslucentColor(int alpha) {
        return new Color(this.baseColor.getRed(), this.baseColor.getGreen(), this.baseColor.getBlue(), alpha);
    }

    private void setAlpha0(int foregroundAlpha, int backgroundAlpha) {
        this.labels[this.foreground].setForeground(getTranslucentColor(foregroundAlpha));
        this.labels[this.background].setForeground(getTranslucentColor(backgroundAlpha));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAlpha(int foregroundAlpha, int backgroundAlpha) {
        Runnable runnable = () -> {
            setAlpha0(foregroundAlpha, backgroundAlpha);
        };
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            EventQueue.invokeLater(runnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/swing/components/AnimatedLabel$BlendOverAnimation.class */
    public final class BlendOverAnimation extends AbstractAnimation {
        BlendOverAnimation(long duration) {
            super(duration, true);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.jgoodies.animation.AbstractAnimation
        public void applyEffect(long time) {
            int foregroundAlpha = (int) ((255 * time) / duration());
            int backgroundAlpha = 255 - foregroundAlpha;
            AnimatedLabel.this.setAlpha(foregroundAlpha, backgroundAlpha);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/swing/components/AnimatedLabel$BoldLargerLabel.class */
    public static final class BoldLargerLabel extends JLabel {
        private final int fontExtraSize;
        private final int fontStyle;

        private BoldLargerLabel(int fontExtraSize) {
            super("");
            this.fontStyle = 1;
            this.fontExtraSize = fontExtraSize;
            updateUI();
        }

        public void updateUI() {
            super.updateUI();
            Font font = getFont();
            if (0 == this.fontExtraSize) {
                if (font.getStyle() != this.fontStyle) {
                    setFont(new FontUIResource(font.deriveFont(this.fontStyle)));
                    return;
                }
                return;
            }
            setFont(new FontUIResource(new Font(font.getName(), this.fontStyle, font.getSize() + this.fontExtraSize)));
        }
    }
}
