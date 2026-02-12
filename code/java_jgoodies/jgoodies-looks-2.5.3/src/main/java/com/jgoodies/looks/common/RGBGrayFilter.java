/*
 * Copyright (c) 2001-2013 JGoodies Software GmbH. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Software GmbH nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.looks.common;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.jgoodies.looks.Options;

/**
 * 将图标转换为灰度图标的图像过滤器。由
 * JGoodies Windows和Plastic外观用于创建禁用图标。<p>
 *
 * 可以使用{@link Options#setHiResGrayFilterEnabled(boolean)}全局禁用高分辨率灰度过滤器；
 * 默认情况下它是启用的。
 * 可以通过将客户端属性键{@link Options#HI_RES_DISABLED_ICON_CLIENT_KEY}
 * 设置为{@code Boolean.FALSE}来按组件覆盖全局设置。<p>
 *
 * 感谢Andrej Golovnin提出了更简单的过滤公式。
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.13 $
 */
public final class RGBGrayFilter extends RGBImageFilter {

    /**
     * 覆盖默认构造函数；防止实例化。
     */
    private RGBGrayFilter() {
        canFilterIndexColorModel = true;
    }


    /**
     * 返回具有禁用外观的图标。当未指定禁用图标时，
     * 使用此方法生成禁用图标。
     *
     * @param component 将显示图标的组件，可能为null
     * @param icon 用于生成禁用图标的图标
     * @return 禁用图标，如果无法生成合适的图标则返回null
     */
    public static Icon getDisabledIcon(JComponent component, Icon icon) {
        if (   (icon == null)
            || (component == null)
            || (icon.getIconWidth() == 0)
            || (icon.getIconHeight() == 0)) {
            return null;
        }
        Image img;
        if (icon instanceof ImageIcon) {
            img = ((ImageIcon) icon).getImage();
        } else {
            img = new BufferedImage(
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            icon.paintIcon(component, img.getGraphics(), 0, 0);
        }
        if (   !Options.isHiResGrayFilterEnabled()
            || (Boolean.FALSE.equals(component.getClientProperty(Options.HI_RES_DISABLED_ICON_CLIENT_KEY)))) {
            return new ImageIcon(GrayFilter.createDisabledImage(img));
        }

        ImageProducer producer =
            new FilteredImageSource(img.getSource(), new RGBGrayFilter());

        return new ImageIcon(component.createImage(producer));
    }


    /**
     * 将默认RGB颜色模型中的单个输入像素转换为单个灰度像素。
     *
     * @param x 水平像素坐标
     * @param y 垂直像素坐标
     * @param rgb 默认RGB颜色模型中的整数像素表示
     * @return 默认RGB颜色模型中的灰度像素
     *
     * @see ColorModel#getRGBdefault
     * @see #filterRGBPixels
     */
    @Override
    public int filterRGB(int x, int y, int rgb) {
        // 计算红、绿、蓝的平均值
        float avg = (((rgb >> 16) & 0xff) / 255f +
                     ((rgb >>  8) & 0xff) / 255f +
                      (rgb        & 0xff) / 255f) / 3;
        // 提取alpha通道
        float alpha = (((rgb >> 24) & 0xff) / 255f);

        // 计算平均值
        // Sun的公式：Math.min(1.0f, (1f - avg) / (100.0f / 35.0f) + avg);
        // 以下公式使用较少的操作，因此更快
        avg = Math.min(1.0f, 0.35f + 0.65f * avg);
        // 转换回RGB
       return (int) (alpha * 255f) << 24 |
              (int) (avg   * 255f) << 16 |
              (int) (avg   * 255f) << 8  |
              (int) (avg   * 255f);
    }

}
