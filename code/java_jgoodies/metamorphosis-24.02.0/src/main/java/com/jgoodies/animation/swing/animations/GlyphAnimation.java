package com.jgoodies.animation.swing.animations;

import com.jgoodies.animation.AbstractAnimation;
import com.jgoodies.animation.swing.components.GlyphLabel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/swing/animations/GlyphAnimation.class */
public final class GlyphAnimation extends AbstractAnimation {
    private final GlyphLabel label;
    private final String text;

    public GlyphAnimation(GlyphLabel label, long duration, long glyphDelay, String text) {
        super(duration);
        this.label = label;
        this.text = text;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.animation.AbstractAnimation
    public void applyEffect(long time) {
        this.label.setText(time == 0 ? " " : this.text);
        if (time < duration()) {
            this.label.setTime(time);
        }
    }
}
