package com.jgoodies.framework.util;

import com.jgoodies.application.Application;
import com.jgoodies.application.ExitListener;
import com.jgoodies.application.Task;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.JSDLCommonSetup;
import com.jgoodies.common.jsdl.icon.DynamicIconValue;
import com.jgoodies.common.swing.ScreenScaling;
import com.jgoodies.framework.util.ScreenUtils;
import com.jgoodies.framework.util.WindowUtils;
import com.jgoodies.layout.builder.FormBuilder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.prefs.Preferences;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/FrameBuilder.class */
public final class FrameBuilder {
    private static int frameCounter = 1;
    private final JFrame frame;
    private int defaultCloseOperation;
    private String title;
    private String name;
    private Icon splashIcon;
    private String splashTitleText;
    private Font splashTitleFont;
    private String splashSubtitleText;
    private Font splashSubtitleFont;
    private Color foreground;
    private JComponent contentPane;
    private Dimension[] effectiveInitialSizes;
    private ScreenUtils.ScreenLocation initialLocation;
    private boolean restoreBounds;
    private WindowListener windowListener;
    private Runnable applicationExitedRunnable;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/FrameBuilder$ApplicationExitedHandler.class */
    public static final class ApplicationExitedHandler implements ExitListener {
        private final Runnable exitedRunnable;

        ApplicationExitedHandler(Runnable exitedRunnable) {
            this.exitedRunnable = exitedRunnable;
        }

        @Override // com.jgoodies.application.ExitListener
        public void applicationExited() {
            this.exitedRunnable.run();
        }
    }

    public FrameBuilder() {
        this(new JFrame());
    }

    public FrameBuilder(JFrame frame) {
        this.defaultCloseOperation = 0;
        this.initialLocation = ScreenUtils.ScreenLocation.OPTICAL_CENTER;
        this.restoreBounds = true;
        this.frame = frame;
    }

    public FrameBuilder defaultCloseOperation(int defaultCloseOperation) {
        this.defaultCloseOperation = defaultCloseOperation;
        return this;
    }

    public FrameBuilder title(String title, Object... args) {
        Preconditions.checkNotBlank(title, Messages.MUST_NOT_BE_BLANK, Task.PROPERTY_TITLE);
        this.title = Strings.get(title, args);
        return this;
    }

    public FrameBuilder name(String name, Object... args) {
        Preconditions.checkNotBlank(name, Messages.MUST_NOT_BE_BLANK, "name");
        this.name = Strings.get(name, args);
        return this;
    }

    public FrameBuilder iconImages(Image... icons) {
        Preconditions.checkNotNullOrEmpty(icons, Messages.MUST_NOT_BE_NULL_OR_EMPTY, "icon images");
        this.frame.setIconImages(Arrays.asList(icons));
        return this;
    }

    public FrameBuilder minimumSize(int effectiveWidth, int effectiveHeight) {
        this.frame.setMinimumSize(ScreenScaling.physicalDimension(effectiveWidth, effectiveHeight));
        return this;
    }

    public FrameBuilder minimumSize(Dimension effectiveMinimumSize) {
        return minimumSize(effectiveMinimumSize.width, effectiveMinimumSize.height);
    }

    public FrameBuilder initialSize(int effectiveWidth, int effectiveHeight) {
        return initialSizes(new Dimension(effectiveWidth, effectiveHeight));
    }

    public FrameBuilder initialSizes(Dimension... effectiveInitialSizes) {
        Preconditions.checkNotNullOrEmpty(effectiveInitialSizes, Messages.MUST_NOT_BE_NULL_OR_EMPTY, "initial sizes");
        this.effectiveInitialSizes = effectiveInitialSizes;
        return this;
    }

    public FrameBuilder initialLocation(ScreenUtils.ScreenLocation location) {
        this.initialLocation = (ScreenUtils.ScreenLocation) Preconditions.checkNotNull(location, Messages.MUST_NOT_BE_NULL, "initial logical screen location");
        return this;
    }

    public FrameBuilder restoreBounds(boolean value) {
        this.restoreBounds = value;
        return this;
    }

    public FrameBuilder background(Color background) {
        this.frame.setBackground(background);
        return this;
    }

    public FrameBuilder foreground(Color foreground) {
        this.foreground = foreground;
        return this;
    }

    public FrameBuilder menuBar(JMenuBar menuBar) {
        this.frame.setJMenuBar(menuBar);
        return this;
    }

    public FrameBuilder contentPane(JComponent contentPane) {
        this.contentPane = contentPane;
        return this;
    }

    public FrameBuilder splashTitleIcon(Icon icon) {
        Preconditions.checkNotNull(icon, Messages.MUST_NOT_BE_NULL, "splash icon");
        this.splashIcon = icon;
        return this;
    }

    public FrameBuilder splashTitleIcon(DynamicIconValue iconValue) {
        return splashTitleIcon(iconValue.toIcon(72, Color.GRAY));
    }

    public FrameBuilder splashTitleText(String text, Object... args) {
        Preconditions.checkNotBlank(text, Messages.MUST_NOT_BE_BLANK, "splash title");
        this.splashTitleText = Strings.get(text, args);
        return this;
    }

    public FrameBuilder splashTitleFont(Font font) {
        this.splashTitleFont = font;
        return this;
    }

    public FrameBuilder splashTitleFont(int effectiveSize) {
        return splashTitleFont(getDefaultFont(1, effectiveSize));
    }

    public FrameBuilder splashSubtitleText(String subtitle, Object... args) {
        Preconditions.checkNotBlank(subtitle, Messages.MUST_NOT_BE_BLANK, "splash sub title");
        this.splashSubtitleText = Strings.get(subtitle, args);
        return this;
    }

    public FrameBuilder splashSubitleFont(Font font) {
        this.splashSubtitleFont = font;
        return this;
    }

    public FrameBuilder windowListener(WindowListener listener) {
        this.windowListener = listener;
        return this;
    }

    public FrameBuilder onApplicationExited(Runnable applicationExitedHandler) {
        this.applicationExitedRunnable = applicationExitedHandler;
        return this;
    }

    public FrameBuilder asRootFrame() {
        JSDLCommonSetup.setRootFrame(this.frame);
        return this;
    }

    public JFrame build() {
        this.frame.setTitle(getTitle());
        this.frame.setName(getName());
        this.frame.setDefaultCloseOperation(this.defaultCloseOperation);
        if (this.defaultCloseOperation == 0) {
            this.frame.addWindowListener(getWindowListener());
        }
        this.frame.setContentPane(buildContent());
        Preferences appUserPrefs = null;
        boolean restored = false;
        if (Application.hasInstance()) {
            appUserPrefs = Application.getInstance().getContext().getUserPreferences();
            restored = this.restoreBounds && WindowUtils.restoreBounds(this.frame, appUserPrefs);
            if (this.applicationExitedRunnable != null) {
                Application.getInstance().addExitListener(new ApplicationExitedHandler(this.applicationExitedRunnable));
            }
        }
        if (!restored) {
            if (this.effectiveInitialSizes != null) {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                Dimension[] dimensionArr = this.effectiveInitialSizes;
                int length = dimensionArr.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    Dimension effectiveInitialSize = dimensionArr[i];
                    Dimension initialSize = ScreenScaling.toPhysical(effectiveInitialSize);
                    if (initialSize.width > screenSize.width || initialSize.height > screenSize.height) {
                        i++;
                    } else {
                        this.frame.setSize(initialSize.width, initialSize.height);
                        break;
                    }
                }
            } else {
                this.frame.pack();
            }
            ScreenUtils.locateAt(this.frame, this.initialLocation);
        }
        if (appUserPrefs != null) {
            WindowUtils.restoreState(this.frame, appUserPrefs, false);
            this.frame.addComponentListener(new WindowUtils.PersistencyHandler(this.frame, appUserPrefs));
        }
        return this.frame;
    }

    public JFrame show() {
        JFrame frame = build();
        frame.setVisible(true);
        return frame;
    }

    private String getTitle() {
        return this.title != null ? this.title : Application.getResourceMap(null).getString("application.title", new Object[0]);
    }

    private String getName() {
        if (this.name != null) {
            return this.name;
        }
        StringBuilder append = new StringBuilder().append("frame");
        int i = frameCounter;
        frameCounter = i + 1;
        return append.append(i).toString();
    }

    private WindowListener getWindowListener() {
        return this.windowListener != null ? this.windowListener : Application.getInstance().getApplicationExitOnWindowClosingHandler();
    }

    private JComponent buildContent() {
        JLabel splashTitleLabel;
        if (this.contentPane != null) {
            return this.contentPane;
        }
        if (this.splashTitleText != null) {
            splashTitleLabel = new JLabel(this.splashTitleText);
            splashTitleLabel.setFont(getSplashTitleFont());
            splashTitleLabel.setForeground(getSplashTitleForeground());
        } else if (this.splashIcon != null) {
            splashTitleLabel = new JLabel(this.splashIcon);
        } else {
            splashTitleLabel = null;
        }
        JLabel splashSubtitleLabel = null;
        if (this.splashSubtitleText != null) {
            splashSubtitleLabel = new JLabel(this.splashSubtitleText);
            splashSubtitleLabel.setFont(getSplashSubitleFont());
            splashSubtitleLabel.setForeground(isDarkBackground() ? Color.WHITE : Color.DARK_GRAY);
        }
        return new FormBuilder().columns("0:grow, center:pref, 0:grow", new Object[0]).rows("0:g, p, 0, p, 0:g", new Object[0]).add((Component) splashTitleLabel).xy(2, 2).add((Component) splashSubtitleLabel).xy(2, 4).build();
    }

    private Font getSplashTitleFont() {
        return this.splashTitleFont != null ? this.splashTitleFont : getDefaultFont(1, 72);
    }

    private Font getSplashSubitleFont() {
        return this.splashSubtitleFont != null ? this.splashSubtitleFont : getDefaultFont(0, 24);
    }

    private static Font getDefaultFont(int style, int effectiveSize) {
        return ScreenScaling.physicalFont(new JLabel(), effectiveSize).deriveFont(style);
    }

    private Color getSplashTitleForeground() {
        if (this.foreground != null) {
            return this.foreground;
        }
        return isDarkBackground() ? new Color(241, 241, 241) : new Color(100, 100, 100);
    }

    private boolean isDarkBackground() {
        Color bg = this.frame.getBackground();
        float[] hsbVals = new float[3];
        Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), hsbVals);
        return hsbVals[2] < 0.8f;
    }
}
