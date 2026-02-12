package com.jgoodies.animation.swing.animations;

import com.jgoodies.animation.Animation;
import com.jgoodies.animation.swing.components.BasicTextLabel;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/swing/animations/BasicTextAnimations.class */
public final class BasicTextAnimations {
    private static final int FADE_TYPE = 0;
    private static final int SCALE_TYPE = 1;
    private static final int SPACE_TYPE = 2;

    private BasicTextAnimations() {
    }

    public static Animation defaultFade(BasicTextLabel label1, BasicTextLabel label2, long singleDuration, long beginOffset, String separatedTexts, Color baseColor) {
        return createTextSequence(label1, label2, singleDuration, beginOffset, separatedTexts, baseColor, 0);
    }

    public static Animation defaultScale(BasicTextLabel label1, BasicTextLabel label2, long singleDuration, long beginOffset, String separatedTexts, Color baseColor) {
        return createTextSequence(label1, label2, singleDuration, beginOffset, separatedTexts, baseColor, SCALE_TYPE);
    }

    public static Animation defaultSpace(BasicTextLabel label1, BasicTextLabel label2, long singleDuration, long beginOffset, String separatedTexts, Color baseColor) {
        return createTextSequence(label1, label2, singleDuration, beginOffset, separatedTexts, baseColor, 2);
    }

    private static Animation createTextSequence(BasicTextLabel label1, BasicTextLabel label2, long singleDuration, long beginOffset, String separatedTexts, Color baseColor, int type) {
        String[] texts = separatedTexts.split("\\|");
        List<Animation> animations = new LinkedList<>();
        long beginTime = 0;
        for (int i = 0; i < texts.length; i += SCALE_TYPE) {
            BasicTextLabel label = i % 2 == 0 ? label1 : label2;
            Animation animation = animation(label, singleDuration, texts[i], baseColor, type);
            animations.add(Animation.offset(animation, beginTime));
            beginTime += singleDuration + beginOffset;
        }
        return Animation.parallel(animations);
    }

    private static Animation animation(BasicTextLabel label, long duration, String text, Color baseColor, int type) {
        switch (type) {
            case 0:
                return BasicTextAnimation.defaultFade(label, duration, text, baseColor);
            case SCALE_TYPE /* 1 */:
                return BasicTextAnimation.defaultScale(label, duration, text, baseColor);
            case 2:
                return BasicTextAnimation.defaultSpace(label, duration, text, baseColor);
            default:
                return null;
        }
    }
}
