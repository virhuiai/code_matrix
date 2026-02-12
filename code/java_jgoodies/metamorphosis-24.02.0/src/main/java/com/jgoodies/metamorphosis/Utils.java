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

import com.jgoodies.metamorphosis.Style;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * 工具类，提供加载图标的实用方法。
 * 该类根据当前样式动态加载对应的图标资源。
 */
final class Utils {
    
    /** 图标资源路径的前缀 */
    private static final String PREFIX = "resources/icons/";

    /**
     * 私有构造方法，防止实例化此类。
     * 该类仅作为工具方法的容器使用。
     */
    private Utils() {
    }

    /**
     * 获取指定文件名的图标。
     * 根据当前样式选择适当的图标路径，如果找不到则尝试默认路径。
     * 
     * @param filename 图标文件名
     * @return 加载的ImageIcon对象，如果找不到资源则返回null
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public static ImageIcon getIcon(String filename) {
        // 获取当前样式的图标风格
        Style.IconStyle iconStyle = Style.getCurrent().iconStyle();
        
        // 构建基于图标的路径前缀
        String pathPrefix = PREFIX + iconStyle.pathSuffix() + "/";
        
        // 尝试从基于图标的路径加载资源
        URL url = Utils.class.getResource(pathPrefix + filename);
        
        // 如果找不到基于图标的资源，则尝试从默认路径加载
        if (url == null) {
            url = Utils.class.getResource(PREFIX + filename);
        }
        
        // 如果仍然找不到资源，返回null
        if (url == null) {
            return null;
        }
        
        // 创建并返回ImageIcon对象
        return new ImageIcon(url);
    }
}