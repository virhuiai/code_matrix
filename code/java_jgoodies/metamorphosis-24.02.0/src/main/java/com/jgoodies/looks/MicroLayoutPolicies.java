package com.jgoodies.looks;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.swing.ScreenScaling;
import javax.swing.UIDefaults;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/MicroLayoutPolicies.class */
public final class MicroLayoutPolicies {
    private MicroLayoutPolicies() {
    }

    public static MicroLayoutPolicy getDefaultPlasticPolicy() {
        return new DefaultPlasticPolicy();
    }

    public static MicroLayoutPolicy getDefaultWindowsPolicy() {
        return new DefaultWindowsPolicy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/MicroLayoutPolicies$DefaultPlasticPolicy.class */
    public static final class DefaultPlasticPolicy implements MicroLayoutPolicy {
        private DefaultPlasticPolicy() {
        }

        @Override // com.jgoodies.looks.MicroLayoutPolicy
        public MicroLayout getMicroLayout(String lafName, UIDefaults table) {
            ScreenScaling.ScaleFactor scaleFactor = ScreenScaling.getScaleFactor();
            boolean is100 = scaleFactor == ScreenScaling.ScaleFactor.F100;
            boolean is125 = scaleFactor == ScreenScaling.ScaleFactor.F125;
            if ("JGoodies Plastic".equals(lafName)) {
                if (is100) {
                    return MicroLayouts.createPlasticMicroLayout100();
                }
                if (is125) {
                    return MicroLayouts.createPlasticMicroLayout125();
                }
                return MicroLayouts.createPlasticModernMicroLayoutHigh(scaleFactor.intValue());
            }
            if (is100) {
                return MicroLayouts.createPlasticModernMicroLayout100();
            }
            if (is125) {
                return MicroLayouts.createPlasticModernMicroLayout125();
            }
            return MicroLayouts.createPlasticModernMicroLayoutHigh(scaleFactor.intValue());
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/MicroLayoutPolicies$DefaultWindowsPolicy.class */
    private static final class DefaultWindowsPolicy implements MicroLayoutPolicy {
        private DefaultWindowsPolicy() {
        }

        @Override // com.jgoodies.looks.MicroLayoutPolicy
        public MicroLayout getMicroLayout(String lafName, UIDefaults table) {
            ScreenScaling.ScaleFactor scaleFactor = ScreenScaling.getScaleFactor();
            boolean is100 = scaleFactor == ScreenScaling.ScaleFactor.F100;
            boolean is125 = scaleFactor == ScreenScaling.ScaleFactor.F125;
            boolean isClassic = !SystemUtils.IS_LAF_WINDOWS_XP_ENABLED;
            if (isClassic) {
                if (is100) {
                    return MicroLayouts.createWindowsClassicMicroLayout100();
                }
                if (is125) {
                    return MicroLayouts.createWindowsClassicMicroLayout125();
                }
                return MicroLayouts.createWindowsModernMicroLayoutHigh(scaleFactor.intValue());
            }
            if (is100) {
                return MicroLayouts.createWindowsModernMicroLayout100();
            }
            if (is125) {
                return MicroLayouts.createWindowsModernMicroLayout125();
            }
            return MicroLayouts.createWindowsModernMicroLayoutHigh(scaleFactor.intValue());
        }
    }
}
