/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.contentstream.operator;

/**
 * PDF内容流操作符名称常量类
 * Contains constants for all PDF content stream operators.
 * 
 * @author Apache PDFBox团队
 * 
 * PDF操作符分类说明：
 * 1. 颜色操作符 - 控制填充和描边颜色
 * 2. 标记内容操作符 - 用于结构化内容和标签
 * 3. 状态操作符 - 管理图形状态
 * 4. 图形操作符 - 绘制基本图形元素
 * 5. 文本操作符 - 控制文本渲染
 * 6. Type3字体操作符 - 特殊字体指令
 * 7. 兼容性操作符 - 版本兼容性控制
 */
public final class OperatorName
{

    // 非描边颜色操作符 (non stroking color)
    // 用于设置填充颜色的各种操作符
    public static final String NON_STROKING_COLOR = "sc";        // 设置非描边颜色 (Set color for non-stroking operations)
    public static final String NON_STROKING_COLOR_N = "scn";     // 设置非描边颜色和特殊颜色空间 (Set color for non-stroking operations with special color spaces)
    public static final String NON_STROKING_RGB = "rg";          // 设置RGB非描边颜色 (Set RGB color for non-stroking operations)
    public static final String NON_STROKING_GRAY = "g";          // 设置灰度非描边颜色 (Set gray color for non-stroking operations)
    public static final String NON_STROKING_CMYK = "k";          // 设置CMYK非描边颜色 (Set CMYK color for non-stroking operations)
    public static final String NON_STROKING_COLORSPACE = "cs";   // 设置非描边颜色空间 (Set color space for non-stroking operations)

    // 描边颜色操作符 (stroking color)
    // 用于设置描边颜色的各种操作符
    public static final String STROKING_COLOR = "SC";            // 设置描边颜色 (Set color for stroking operations)
    public static final String STROKING_COLOR_N = "SCN";         // 设置描边颜色和特殊颜色空间 (Set color for stroking operations with special color spaces)
    public static final String STROKING_COLOR_RGB = "RG";        // 设置RGB描边颜色 (Set RGB color for stroking operations)
    public static final String STROKING_COLOR_GRAY = "G";        // 设置灰度描边颜色 (Set gray color for stroking operations)
    public static final String STROKING_COLOR_CMYK = "K";        // 设置CMYK描边颜色 (Set CMYK color for stroking operations)
    public static final String STROKING_COLORSPACE = "CS";       // 设置描边颜色空间 (Set color space for stroking operations)

    // 标记内容操作符 (marked content)
    // 用于PDF结构化内容和标签的操作符
    public static final String BEGIN_MARKED_CONTENT_SEQ = "BDC"; // 开始带属性的标记内容序列 (Begin marked content sequence with property list)
    public static final String BEGIN_MARKED_CONTENT = "BMC";     // 开始标记内容 (Begin marked content)
    public static final String END_MARKED_CONTENT = "EMC";       // 结束标记内容 (End marked content)
    public static final String MARKED_CONTENT_POINT_WITH_PROPS = "DP"; // 带属性的标记内容点 (Marked content point with property list)
    public static final String MARKED_CONTENT_POINT = "MP";      // 标记内容点 (Marked content point)
    public static final String DRAW_OBJECT = "Do";               // 绘制XObject对象 (Draw XObject)

    // 状态操作符 (state)
    // 控制图形状态的变换和保存/恢复
    public static final String CONCAT = "cm";                    // 连接变换矩阵 (Concatenate matrix to current transformation matrix)
    public static final String RESTORE = "Q";                    // 恢复图形状态 (Restore graphics state)
    public static final String SAVE = "q";                       // 保存图形状态 (Save graphics state)
    public static final String SET_FLATNESS = "i";               // 设置平面度容忍度 (Set flatness tolerance)
    public static final String SET_GRAPHICS_STATE_PARAMS = "gs"; // 设置图形状态参数 (Set graphics state parameters)
    public static final String SET_LINE_CAPSTYLE = "J";          // 设置线帽样式 (Set line cap style)
    public static final String SET_LINE_DASHPATTERN = "d";       // 设置线型模式 (Set line dash pattern)
    public static final String SET_LINE_JOINSTYLE = "j";         // 设置线连接样式 (Set line join style)
    public static final String SET_LINE_MITERLIMIT = "M";        // 设置斜接限制 (Set miter limit)
    public static final String SET_LINE_WIDTH = "w";             // 设置线宽 (Set line width)
    public static final String SET_MATRIX = "Tm";                // 设置文本矩阵 (Set text matrix)
    public static final String SET_RENDERINGINTENT = "ri";       // 设置渲染意图 (Set color rendering intent)

    // 图形操作符 (graphics)
    // 用于绘制各种图形元素
    public static final String APPEND_RECT = "re";               // 添加矩形到当前路径 (Append rectangle to current path)
    public static final String BEGIN_INLINE_IMAGE = "BI";        // 开始内联图像 (Begin inline image)
    public static final String BEGIN_INLINE_IMAGE_DATA = "ID";   // 开始内联图像数据 (Begin inline image data)
    public static final String END_INLINE_IMAGE = "EI";          // 结束内联图像 (End inline image)
    public static final String CLIP_EVEN_ODD = "W*";             // 设置奇偶环绕规则裁剪路径 (Set clipping path using even-odd rule)
    public static final String CLIP_NON_ZERO = "W";              // 设置非零环绕规则裁剪路径 (Set clipping path using nonzero winding number rule)
    public static final String CLOSE_AND_STROKE = "s";           // 闭合并描边路径 (Close and stroke path)
    public static final String CLOSE_FILL_EVEN_ODD_AND_STROKE = "b*"; // 闭合路径，奇偶规则填充并描边 (Close path, fill with even-odd rule and stroke)
    public static final String CLOSE_FILL_NON_ZERO_AND_STROKE = "b"; // 闭合路径，非零规则填充并描边 (Close path, fill with nonzero rule and stroke)
    public static final String CLOSE_PATH = "h";                 // 闭合当前路径 (Close current path)
    public static final String CURVE_TO = "c";                   // 绘制三次贝塞尔曲线 (Append curved segment to path)
    public static final String CURVE_TO_REPLICATE_FINAL_POINT = "y"; // 绘制贝塞尔曲线(复制终点) (Curve to replicate final point)
    public static final String CURVE_TO_REPLICATE_INITIAL_POINT = "v"; // 绘制贝塞尔曲线(复制起点) (Curve to replicate initial point)
    public static final String ENDPATH = "n";                    // 结束路径(不绘制) (End path without filling or stroking)
    public static final String FILL_EVEN_ODD_AND_STROKE = "B*";  // 奇偶规则填充并描边 (Fill with even-odd rule and stroke)
    public static final String FILL_EVEN_ODD = "f*";             // 奇偶规则填充 (Fill path using even-odd rule)
    public static final String FILL_NON_ZERO_AND_STROKE = "B";   // 非零规则填充并描边 (Fill with nonzero rule and stroke)
    public static final String FILL_NON_ZERO = "f";              // 非零规则填充 (Fill path using nonzero winding number rule)
    public static final String LEGACY_FILL_NON_ZERO = "F";       // 传统非零规则填充 (Legacy fill path using nonzero rule)
    public static final String LINE_TO = "l";                    // 绘制直线到指定点 (Append straight line segment to path)
    public static final String MOVE_TO = "m";                    // 移动到新位置开始新子路径 (Begin new subpath)
    public static final String SHADING_FILL = "sh";              // 使用着色对象填充 (Paint area defined by shading object)
    public static final String STROKE_PATH = "S";                // 描边当前路径 (Stroke path)

    // 文本操作符 (text)
    // 控制文本渲染的各种操作符
    public static final String BEGIN_TEXT = "BT";                // 开始文本对象 (Begin text object)
    public static final String END_TEXT = "ET";                  // 结束文本对象 (End text object)
    public static final String MOVE_TEXT = "Td";                 // 移动文本位置 (Move to start of next line)
    public static final String MOVE_TEXT_SET_LEADING = "TD";     // 移动文本位置并设置行距 (Move text position and set leading)
    public static final String NEXT_LINE = "T*";                 // 移动到下一行 (Move to start of next line)
    public static final String SET_CHAR_SPACING = "Tc";          // 设置字符间距 (Set character spacing)
    public static final String SET_FONT_AND_SIZE = "Tf";         // 设置字体和字号 (Set text font and size)
    public static final String SET_TEXT_HORIZONTAL_SCALING = "Tz"; // 设置文本水平缩放 (Set horizontal text scaling)
    public static final String SET_TEXT_LEADING = "TL";          // 设置文本行距 (Set text leading)
    public static final String SET_TEXT_RENDERINGMODE = "Tr";    // 设置文本渲染模式 (Set text rendering mode)
    public static final String SET_TEXT_RISE = "Ts";             // 设置文本基线偏移 (Set text rise)
    public static final String SET_WORD_SPACING = "Tw";          // 设置词间距 (Set word spacing)
    public static final String SHOW_TEXT = "Tj";                 // 显示文本 (Show text)
    public static final String SHOW_TEXT_ADJUSTED = "TJ";        // 显示调整间距的文本 (Show text with adjustable spacing)
    public static final String SHOW_TEXT_LINE = "'";             // 显示文本并移动到下一行 (Show text and move to next line)
    public static final String SHOW_TEXT_LINE_AND_SPACE = "\"";  // 显示文本并移动到下一行且设置词间距 (Show text, move to next line and set word spacing)

    // Type3字体操作符 (type3 font)
    // 专门用于Type3字体的特殊操作符
    public static final String TYPE3_D0 = "d0";                  // 设置Type3字体宽度向量(无轮廓) (Set width information for Type 3 glyph with no outline)
    public static final String TYPE3_D1 = "d1";                  // 设置Type3字体宽度向量(有轮廓) (Set width and bounding box for Type 3 glyph with outline)

    // 兼容性操作符 (compatibility section)
    // 用于处理不同PDF版本兼容性的操作符
    public static final String BEGIN_COMPATIBILITY_SECTION = "BX"; // 开始兼容性部分 (Begin compatibility section)
    public static final String END_COMPATIBILITY_SECTION = "EX";   // 结束兼容性部分 (End compatibility section)

    /**
     * 私有构造函数 - 防止实例化
     * private constructor
     */
    private OperatorName()
    {
    }

}