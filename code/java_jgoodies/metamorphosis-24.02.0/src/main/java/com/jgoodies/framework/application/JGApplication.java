package com.jgoodies.framework.application;

import com.jgoodies.application.Application;
import com.jgoodies.application.ResourceMap;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.promise.Promise;
import com.jgoodies.common.swing.focus.JGLayoutFocusTraversalPolicy;
import com.jgoodies.components.JGComponentFactory;
import com.jgoodies.components.util.TextComponentUtils;
import com.jgoodies.framework.osx.OSXUtils;
import com.jgoodies.layout.FormsSetup;
import com.jgoodies.looks.LookUtils;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.EventObject;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/application/JGApplication.class */
public abstract class JGApplication extends Application {
    private static final String WINDOWS_APPDATA = "APPDATA";
    private static final String MAC_LIBRARY_APP_SUPPORT = "Library/Application Support";
    private String[] args;
    private File applicationDataDirectory;

    protected abstract void createAndShowGUI();

    @Override // com.jgoodies.application.Application
    protected void startup(String[] args) {
        this.args = args;
        preConfiguration();
        configureSystemProperties();
        configureLogging();
        configureUI();
        configureHelp();
        preCreateAndShowGUI();
        createAndShowGUI();
        postCreateAndShowGUI();
    }

    @Override // com.jgoodies.application.Application
    protected void shutdown() {
        disposeFramesAndWindows();
    }

    protected void preConfiguration() {
    }

    protected void configureSystemProperties() {
        ResourceMap resourceMap = getResourceMap();
        String[] keys = {"application.name", "application.fullVersion", "application.vendor", "application.vendor.url", "application.vendor.mail"};
        for (String key : keys) {
            System.setProperty(key, resourceMap.getString(key, new Object[0]));
        }
    }

    protected void configureLogging() {
        String logType = System.getProperty("logging.type", "default");
        String configurationFileName = "resources/logging." + logType + ".properties";
        configureLoggingFromProperties(getClass().getResource(configurationFileName));
        addLogFileHandler(getApplicationDataDirectory(), "console%u.log");
    }

    protected void configureHelp() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void configureUI() {
        OSXUtils.setUseScreenMenuBar(true);
        UIManager.put("ClassLoader", LookUtils.class.getClassLoader());
    }

    protected void preCreateAndShowGUI() {
        FormsSetup.setComponentFactoryDefault(JGComponentFactory.getCurrent());
        JGLayoutFocusTraversalPolicy.installAsDefault();
        setDefaultInputBlocker(new GlassPaneBlocker());
    }

    protected void postCreateAndShowGUI() {
    }

    protected final String[] getArgs() {
        return this.args;
    }

    protected static final void disposeFramesAndWindows() {
        for (JFrame jFrame : Frame.getFrames()) {
            if (jFrame instanceof JFrame) {
                JFrame jframe = jFrame;
                Window[] ownedWindows = jframe.getOwnedWindows();
                for (Window element : ownedWindows) {
                    element.dispose();
                }
            }
            jFrame.dispose();
        }
    }

    @Override // com.jgoodies.application.Application
    public Promise<Boolean> exit(EventObject event) {
        TextComponentUtils.commitImmediately();
        return super.exit(event);
    }

    public final File getApplicationDataDirectory() {
        if (this.applicationDataDirectory == null) {
            this.applicationDataDirectory = lookupApplicationDataDirectory();
        }
        return this.applicationDataDirectory;
    }

    protected File lookupApplicationDataDirectory() {
        String vendorPath;
        String appPath;
        File baseDir = lookupApplicationDataBaseDirectory();
        String vendorId = getResourceMap().getString("application.vendor.id", new Object[0]);
        String appId = getResourceMap().getString("application.id", new Object[0]);
        if (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_MAC) {
            vendorPath = vendorId;
            appPath = appId;
        } else {
            vendorPath = "." + vendorId.replaceAll("\\s", "").toLowerCase();
            appPath = appId.replaceAll("\\s", "").toLowerCase();
        }
        return new File(new File(baseDir, vendorPath), appPath);
    }

    protected File lookupApplicationDataBaseDirectory() {
        File userHome = new File(System.getProperty("user.home"));
        if (SystemUtils.IS_OS_MAC) {
            return new File(userHome, MAC_LIBRARY_APP_SUPPORT);
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            try {
                String appDataValue = System.getenv(WINDOWS_APPDATA);
                if (appDataValue != null) {
                    return new File(appDataValue);
                }
            } catch (SecurityException e) {
            }
        }
        return userHome;
    }

    protected void configureLoggingFromProperties(URL configurationURL) {
        Preconditions.checkNotNull(configurationURL, Messages.MUST_NOT_BE_NULL, "configuration URL");
        String errorMessage = "Failed to open the logging configuration URL: " + configurationURL;
        try {
            InputStream in = configurationURL.openStream();
            Throwable th = null;
            try {
                try {
                    LogManager.getLogManager().readConfiguration(in);
                    if (in != null) {
                        if (0 != 0) {
                            try {
                                in.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            in.close();
                        }
                    }
                } finally {
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (IOException | SecurityException e) {
            System.err.println(errorMessage);
            e.printStackTrace();
        }
    }

    protected void addLogFileHandler(File logDirectory, String logFilePattern) {
        if (!logDirectory.exists() && !logDirectory.mkdirs()) {
            System.err.println("Failed to create the logfile directory: " + logDirectory);
            return;
        }
        try {
            String pattern = logDirectory.getAbsolutePath() + "/" + logFilePattern;
            FileHandler handler = new FileHandler(pattern);
            Logger.getLogger("").addHandler(handler);
        } catch (IOException e) {
            System.err.println("Failed to configure the logging: " + e.getLocalizedMessage());
        }
    }
}
