/*
 * Copyright (c) 2024, JGoodies Software GmbH.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  - Neither the name of JGoodies Software GmbH nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.metamorphosis;

import com.jgoodies.binding.beans.DelayedPropertyChangeHandler;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * 样式枚举类，定义了不同外观风格的配置。
 * 包含多个预定义的样式，每个样式具有不同的外观特性。
 */
final class Style {
    
    /** 存储所有样式实例的列表 */
    private static final List<Style> ALL = new LinkedList<>();
    
    /** Franken样式：演示常见GUI设计错误的示例 */
    static final Style FRANKEN = new Style("Franken", IconStyle.MIXED, false, false, false, false, false, false);
    
    /** Rookie样式：避免了一些严重图形错误但仍需改进 */
    static final Style ROOKIE = new Style("Rookie", IconStyle.JFA, true, true, false, false, false, false);
    
    /** Standard样式：有一些改进的标准样式 */
    static final Style STANDARD = new Style("Standard", IconStyle.WIN, true, true, true, false, false, false);
    
    /** Advanced样式：可以与原生应用竞争的高级样式 */
    static final Style ADVANCED = new Style("Advanced", IconStyle.WIN, true, true, true, true, false, false);
    
    /** Elegant样式：优雅的Eclipse风格 */
    static final Style ELEGANT = new Style("Elegant", IconStyle.WIN, true, true, true, true, true, true);
    
    /** StandardX样式：标准样式但不使用Eclipse边框 */
    static final Style STANDARDX = new Style("StandardX", IconStyle.WIN, true, true, false, false, false, false);
    
    /** AdvancedX样式：高级样式但不使用Eclipse边框 */
    static final Style ADVANCEDX = new Style("AdvancedX", IconStyle.WIN, true, true, false, true, false, false);
    
    /** ElegantX样式：优雅样式但不使用Eclipse边框 */
    static final Style ELEGANTX = new Style("ElegantX", IconStyle.WIN, true, true, false, true, true, true);
    
    /** 当前活动的样式 */
    static Style current = ELEGANT;
    
    /** 样式名称 */
    private final String name;
    
    /** 图标样式 */
    private final IconStyle iconStyle;
    
    /** 是否减少边框 */
    private final boolean reduceBorders;
    
    /** 是否强制使用更好的颜色主题 */
    private final boolean forceBetterColorTheme;
    
    /** 是否强制使用系统外观和感觉 */
    private final boolean forceSystemLookAndFeel;
    
    /** 是否使用扩展的外观和感觉 */
    private final boolean useExtendedLookAndFeel;
    
    /** 是否使用Eclipse边框 */
    private final boolean useEclipseBorder;
    
    /** 是否使用Eclipse渐变效果 */
    private final boolean useEclipseGradient;

    /**
     * 图标样式枚举，定义了不同的图标集风格。
     */
    enum IconStyle {
        /** 混合风格：混合了多种图标风格 */
        MIXED,
        /** JFA风格：一致的图标集，但颜色过饱和 */
        JFA,
        /** WIN风格：适合大多数平台的图标集，特别是Windows */
        WIN;

        /**
         * 获取图标样式的小写路径后缀。
         * 
         * @return 小写的路径后缀
         */
        public String pathSuffix() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

    /**
     * 构造一个新的样式实例。
     * 
     * @param name 样式名称
     * @param iconStyle 图标样式
     * @param reduceBorders 是否减少边框
     * @param forceBetterColorTheme 是否强制使用更好的颜色主题
     * @param forceSystemLookAndFeel 是否强制使用系统外观和感觉
     * @param useExtendedLookAndFeel 是否使用扩展的外观和感觉
     * @param useEclipseBorder 是否使用Eclipse边框
     * @param useEclipseGradient 是否使用Eclipse渐变效果
     */
    Style(String name, IconStyle iconStyle, boolean reduceBorders, boolean forceBetterColorTheme, 
          boolean forceSystemLookAndFeel, boolean useExtendedLookAndFeel, boolean useEclipseBorder, 
          boolean useEclipseGradient) {
        this.name = name;
        this.iconStyle = iconStyle;
        this.reduceBorders = reduceBorders;
        this.forceBetterColorTheme = forceBetterColorTheme;
        this.forceSystemLookAndFeel = forceSystemLookAndFeel;
        this.useExtendedLookAndFeel = useExtendedLookAndFeel;
        this.useEclipseBorder = useEclipseBorder;
        this.useEclipseGradient = useEclipseGradient;
        ALL.add(this);  // 将新样式添加到全局列表中
    }

    /**
     * 获取当前活动的样式。
     * 
     * @return 当前样式实例
     */
    public static Style getCurrent() {
        return current;
    }

    /**
     * 设置当前活动的样式。
     * 
     * @param newStyle 新的样式实例
     */
    public static void setCurrent(Style newStyle) {
        current = newStyle;
    }

    /**
     * 根据样式名称获取对应的样式实例。
     * 
     * @param styleName 样式名称
     * @return 匹配的样式实例
     * @throws IllegalArgumentException 如果找不到匹配的样式
     */
    public static Style valueOf(String styleName) {
        String loweredName = styleName.toLowerCase(Locale.ENGLISH);
        return ALL.stream()
            .filter(style -> {
                return style.toString().toLowerCase(Locale.ENGLISH).equals(loweredName);
            })
            .findAny()
            .orElseThrow(() -> {
                return new IllegalArgumentException("Unknown style name: " + styleName);
            });
    }

    /**
     * 获取此样式的图标样式。
     * 
     * @return 图标样式
     */
    public IconStyle iconStyle() {
        return this.iconStyle;
    }

    /**
     * 检查此样式是否减少边框。
     * 
     * @return 如果减少边框则返回true，否则返回false
     */
    public boolean reduceBorders() {
        return this.reduceBorders;
    }

    /**
     * 检查此样式是否强制使用更好的颜色主题。
     * 
     * @return 如果强制使用更好的颜色主题则返回true，否则返回false
     */
    public boolean forceBetterColorTheme() {
        return this.forceBetterColorTheme;
    }

    /**
     * 检查此样式是否强制使用系统外观和感觉。
     * 
     * @return 如果强制使用系统外观和感觉则返回true，否则返回false
     */
    public boolean forceSystemLookAndFeel() {
        return this.forceSystemLookAndFeel;
    }

    /**
     * 检查此样式是否使用扩展的外观和感觉。
     * 
     * @return 如果使用扩展的外观和感觉则返回true，否则返回false
     */
    public boolean useExtendedLookAndFeel() {
        return this.useExtendedLookAndFeel;
    }

    /**
     * 检查此样式是否使用Eclipse边框。
     * 
     * @return 如果使用Eclipse边框则返回true，否则返回false
     */
    public boolean useEclipseBorder() {
        return this.useEclipseBorder;
    }

    /**
     * 检查此样式是否使用Eclipse渐变效果。
     * 
     * @return 如果使用Eclipse渐变效果则返回true，否则返回false
     */
    public boolean useEclipseGradient() {
        return this.useEclipseGradient;
    }

    /**
     * 获取此样式的字符串表示形式（即样式名称）。
     * 
     * @return 样式名称
     */
    public String toString() {
        return this.name;
    }

    /**
     * 获取此样式的详细描述。
     * 
     * @return 样式的详细描述文本
     */
    public String getDescription() {
        // 创建一个StringBuilder来构建描述文本
        StringBuilder builder = new StringBuilder(DelayedPropertyChangeHandler.DEFAULT_DELAY);
        
        // 根据不同的样式添加总体外观描述
        builder.append("THE OVERALL APPEARANCE ");
        if (this == FRANKEN) {
            builder.append("sucks! It demos a lot of common GUI design errors that one can see so often in Java clients.");
        } else if (this == ROOKIE) {
            builder.append("avoids the most dramatic graphical bloopers. However, it still sucks.");
        } else if (this == STANDARD) {
            builder.append("has been improved a bit. Nevertheless, it still lags behind the look and feel of recent native applications.");
        } else if (this == ADVANCED) {
            builder.append("can compete with native applications. It avoids the most common Java gui bloopers and works around some Swing look&feel deficiencies.");
        } else if (this == ELEGANT) {
            builder.append("is elegant; it has been copied from the eclipse workbench, see www.eclipse.org.");
        } else {
            builder.append("has been customized by you, see the following details.");
        }
        
        // 添加图标集描述
        builder.append("\n\nTHE ICON SET ");
        if (this.iconStyle == IconStyle.MIXED) {
            builder.append("is a mixture of several icon styles with different sizes, colors schemes and symbol styles. Most icons use oversatured colors.");
        } else if (this.iconStyle == IconStyle.JFA) {
            builder.append("is consistent. Nevertheless, it suffers from oversaturated colors and uncommon symbols.");
        } else {
            builder.append("is consistent and is well suited for most platforms, especially Windows. ");
        }
        
        // 添加边框描述
        builder.append("\n\nBORDERS ");
        if (this.useEclipseBorder) {
            builder.append("around panels have been replaced by Eclipse-like borders.");
        } else if (!this.reduceBorders) {
            builder.append("are used heavily and clutter the user interface. A source of trouble is the JSplitPane that comes with three borders for both components and the divider.");
        } else {
            builder.append("have been removed from all JSplitPanes.");
        }
        
        // 添加颜色方案描述
        builder.append("\n\nTHE COLOR SCHEME ");
        if (this.forceSystemLookAndFeel) {
            builder.append("is based on system colors.");
        } else if (!this.forceBetterColorTheme) {
            builder.append("conflicts with the Windows default desktop colors.");
        } else {
            builder.append("works well with the majority of default desktop settings, including Windows default colors.");
        }
        
        // 添加外观和感觉描述
        builder.append("\n\nTHE LOOK&FEEL ");
        if (!this.forceSystemLookAndFeel) {
            builder.append("is the Java look&feel ");
        } else {
            builder.append("is the system look&feel ");
        }
        if (!useExtendedLookAndFeel()) {
            builder.append("and so, uses non-standard fonts on most Windows environments.");
        } else {
            builder.append("as implemented by JGoodies; fonts, menus, trees, tables, and text panes have been tweaked to more precisely emulate the native widgetry.");
        }
        
        // 添加菜单项对齐描述
        builder.append("\n\nTHE MENU ITEMS in the File, Edit, and Source menus are ");
        if (!this.useExtendedLookAndFeel) {
            builder.append("not ");
        }
        builder.append("aligned.");
        
        // 如果使用Eclipse渐变效果，则添加相应描述
        if (this.useEclipseGradient) {
            builder.append("\n\nIN ADDITION, the tasks panel uses a gradient.");
        }
        
        return builder.toString();
    }
}