/*
 * Copyright (c) 2000-2015 TeamDev Ltd. All rights reserved.
 * TeamDev PROPRIETARY and CONFIDENTIAL.
 * Use is subject to license terms.
 */

import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;

/**
 * This sample demonstrates how to work with plugin manager to get a list
 * of all available plugins and disable all plugins except PDF Viewer using
 * our own PluginFilter.
 */
public class PluginManagerSample {
    public static void main(String[] args) {
        LoggerProvider.setLevel(Level.OFF);

        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        PluginManager pluginManager = browser.getPluginManager();

        // Get information about all available plugins.
        List<PluginInfo> pluginsList = pluginManager.getPluginsInfo();
        for (PluginInfo plugin : pluginsList) {
            System.out.println("Plugin Name: " + plugin.getName());
            System.out.print("Plugin Mime-Types: ");
            for (String mimeType : plugin.getMimeTypes()) {
                System.out.print(mimeType + ';');
            }
            System.out.println("\n---------");
        }

        // Register plugin filter that disables all plugins except the one that
        // supports "application/pdf" mime type.
        pluginManager.setPluginFilter(new PluginFilter() {
            @Override
            public boolean isPluginAllowed(PluginInfo pluginInfo) {
                return pluginInfo.getMimeTypes().contains("application/pdf");
            }
        });
        browser.loadURL("http://www.orimi.com/pdf-test.pdf");
    }
}
