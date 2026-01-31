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
package org.apache.pdfbox.contentstream;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.state.EmptyGraphicsStackException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.filter.MissingImageReaderException;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.MissingResourceException;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3CharProc;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDTextState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;

/**
 * Processes a PDF content stream and executes certain operations.
 * Provides a callback interface for clients that want to do things with the stream.
 * 
 * PDF内容流处理引擎核心类
 * 处理PDF内容流并执行相应操作，为希望对流进行处理的客户端提供回调接口
 * 
 * 主要功能：
 * - 解析和处理PDF内容流操作符
 * - 管理图形状态栈
 * - 处理文本渲染
 * - 处理图形对象（表单、图案、透明度组等）
 * - 坐标变换和裁剪
 * 
 * @author Ben Litchfield
 */
public abstract class PDFStreamEngine
{
    private static final Log LOG = LogFactory.getLog(PDFStreamEngine.class);

    // 操作符处理器映射表 - 存储所有可用的操作符处理器
    private final Map<String, OperatorProcessor> operators = new HashMap<>();

    // 图形状态栈 - 用于保存和恢复图形状态
    private Deque<PDGraphicsState> graphicsStack = new ArrayDeque<>();

    // 当前资源和页面引用
    private PDResources resources;
    private PDPage currentPage;
    private boolean isProcessingPage;
    private Matrix initialMatrix;

    // 递归操作监控级别 - 用于防止过深的递归调用
    private int level = 0;

    // 默认字体 - 在没有可用字体时使用
    private PDFont defaultFont;

    // 颜色操作符处理标志 - 在某些情况下为false（如type3字符过程或无色平铺图案）
    private boolean shouldProcessColorOperators;

    /**
     * Creates a new PDFStreamEngine.
     * 创建新的PDF流引擎实例
     */
    protected PDFStreamEngine()
    {
    }

    /**
     * Adds an operator processor to the engine.
     *
     * @param op operator processor
     * 
     * 向引擎添加操作符处理器
     * 将指定的操作符处理器注册到引擎中，用于处理对应的操作符
     */
    public final void addOperator(OperatorProcessor op)
    {
        operators.put(op.getName(), op);
    }

    /**
     * Initializes the stream engine for the given page.
     * 
     * 初始化给定页面的流引擎
     * 为处理指定页面做准备，包括清空图形状态栈、设置初始状态等
     */
    private void initPage(PDPage page)
    {
        if (page == null)
        {
            throw new IllegalArgumentException("Page cannot be null");
        }
        currentPage = page;
        graphicsStack.clear();
        graphicsStack.push(new PDGraphicsState(page.getCropBox()));
        resources = null;
        initialMatrix = page.getMatrix();
    }

    /**
     * Provide standard 14 Helvetica font as default if there isn't any font available.  
     * @return the default font
     * 
     * 提供标准14号Helvetica字体作为默认字体
     * 当没有可用字体时返回默认字体实例
     */
    private PDFont getDefaultFont()
    {
        if (defaultFont == null)
        {
            defaultFont = new PDType1Font(FontName.HELVETICA);
        }
        return defaultFont;
    }
    
    /**
     * This will initialize and process the contents of the stream.
     *
     * @param page the page to process
     * @throws IOException if there is an error accessing the stream
     * 
     * 初始化并处理流的内容
     * 对指定页面进行完整的处理流程，包括初始化和流解析
     */
    public void processPage(PDPage page) throws IOException
    {
        initPage(page);
        if (page.hasContents())
        {
            isProcessingPage = true;
            processStream(page);
            isProcessingPage = false;
        }
    }

    /**
     * Shows a transparency group from the content stream.
     *
     * @param form transparency group (form) XObject
     * @throws IOException if the transparency group cannot be processed
     * 
     * 显示内容流中的透明度组
     * 处理并显示来自内容流的透明度组对象
     */
    public void showTransparencyGroup(PDTransparencyGroup form) throws IOException
    {
        processTransparencyGroup(form);
    }

    /**
     * Shows a form from the content stream.
     *
     * @param form form XObject
     * @throws IOException if the form cannot be processed
     * 
     * 显示内容流中的表单
     * 处理并显示来自内容流的表单对象
     */
    public void showForm(PDFormXObject form) throws IOException
    {
        if (currentPage == null)
        {
            throw new IllegalStateException("No current page, call " +
                    "#processChildStream(PDContentStream, PDPage) instead");
        }
        if (form.getCOSObject().getLength() > 0)
        {
            processStream(form);
        }
    }

    /**
     * Processes a soft mask transparency group stream.
     * 
     * @param group transparency group used for the soft mask
     * @throws IOException if the transparency group cannot be processed
     * 
     * 处理软遮罩透明度组流
     * 处理用于软遮罩的透明度组流，设置相应的图形状态
     */
    protected void processSoftMask(PDTransparencyGroup group) throws IOException
    {
        saveGraphicsState();
        PDGraphicsState graphicsState = getGraphicsState();
        Matrix softMaskCTM = graphicsState.getSoftMask().getInitialTransformationMatrix();
        graphicsState.setCurrentTransformationMatrix(softMaskCTM);
        graphicsState.setTextMatrix(new Matrix());
        graphicsState.setTextLineMatrix(new Matrix());
        graphicsState.setNonStrokingColorSpace(PDDeviceGray.INSTANCE);
        graphicsState.setNonStrokingColor(PDDeviceGray.INSTANCE.getInitialColor());
        graphicsState.setStrokingColorSpace(PDDeviceGray.INSTANCE);
        graphicsState.setStrokingColor(PDDeviceGray.INSTANCE.getInitialColor());

        try
        {
            processTransparencyGroup(group);
        }
        finally
        {
            restoreGraphicsState();
        }
    }

    /**
     * Processes a transparency group stream.
     * 
     * @param group transparency group to be processed
     * @throws IOException if the transparency group cannot be processed
     * 
     * 处理透明度组流
     * 处理透明度组对象，包括状态保存、矩阵变换、混合模式设置等
     */
    protected void processTransparencyGroup(PDTransparencyGroup group) throws IOException
    {
        if (currentPage == null)
        {
            throw new IllegalStateException("No current page, call " +
                    "#processChildStream(PDContentStream, PDPage) instead");
        }

        PDResources parent = pushResources(group);
        Deque<PDGraphicsState> savedStack = saveGraphicsStack();
        
        Matrix parentMatrix = initialMatrix;
        PDGraphicsState graphicsState = getGraphicsState();

        // 流的初始矩阵包含父CTM，这允许缩放的表单
        initialMatrix = graphicsState.getCurrentTransformationMatrix().clone();

        // 使用流的矩阵变换CTM
        graphicsState.getCurrentTransformationMatrix().concatenate(group.getMatrix());

        // 在执行透明度组XObject的内容流之前，
        // 图形状态中的当前混合模式应初始化为Normal，
        // 当前描边和非描边alpha常数为1.0，当前软遮罩为None
        graphicsState.setBlendMode(BlendMode.NORMAL);
        graphicsState.setAlphaConstant(1);
        graphicsState.setNonStrokeAlphaConstant(1);
        graphicsState.setSoftMask(null);

        // 裁剪到边界框
        clipToRect(group.getBBox());

        try
        {
            processStreamOperators(group);
        }
        finally
        {
            initialMatrix = parentMatrix;

            restoreGraphicsStack(savedStack);
            popResources(parent);
        }
    }

    /**
     * Processes a Type 3 character stream.
     *
     * @param charProc Type 3 character procedure
     * @param textRenderingMatrix the Text Rendering Matrix
     * @throws IOException if there is an error reading or parsing the character content stream.
     * 
     * 处理Type 3字符流
     * 处理Type 3字体的字符过程流，设置相应的文本渲染矩阵
     */
    protected void processType3Stream(PDType3CharProc charProc, Matrix textRenderingMatrix)
            throws IOException
    {
        if (currentPage == null)
        {
            throw new IllegalStateException("No current page, call " +
                    "#processChildStream(PDContentStream, PDPage) instead");
        }

        PDResources parent = pushResources(charProc);
        Deque<PDGraphicsState> savedStack = saveGraphicsStack();
        PDGraphicsState graphicsState = getGraphicsState();

        // 用TRM替换CTM
        graphicsState.setCurrentTransformationMatrix(textRenderingMatrix);

        // 使用流的矩阵变换CTM（这是字体矩阵）
        textRenderingMatrix.concatenate(charProc.getMatrix());

        // 注意：我们不对BBox进行裁剪，因为它经常是错误的，参见PDFBOX-1917

        graphicsState.setTextMatrix(new Matrix());
        graphicsState.setTextLineMatrix(new Matrix());

        try
        {
            processStreamOperators(charProc);
        }
        finally
        {
            restoreGraphicsStack(savedStack);
            popResources(parent);
        }
    }

    /**
     * Process the given annotation with the specified appearance stream.
     *
     * @param annotation The annotation containing the appearance stream to process.
     * @param appearance The appearance stream to process.
     * @throws IOException If there is an error reading or parsing the appearance content stream.
     * 
     * 处理具有指定外观流的给定注释
     * 处理注释及其外观流，包括坐标变换和裁剪操作
     */
    protected void processAnnotation(PDAnnotation annotation, PDAppearanceStream appearance)
            throws IOException
    {
        PDRectangle bbox = appearance.getBBox();
        PDRectangle rect = annotation.getRectangle();

        // 零尺寸矩形无效
        if (rect != null && rect.getWidth() > 0 && rect.getHeight() > 0 &&
            bbox != null && bbox.getWidth() > 0 && bbox.getHeight() > 0)
        {
            PDResources parent = pushResources(appearance);
            Deque<PDGraphicsState> savedStack = saveGraphicsStack();

            Matrix matrix = appearance.getMatrix();

            // 变换后的外观框  fixme: 可能是任意形状
            Rectangle2D transformedBox = bbox.transform(matrix).getBounds2D();

            // 计算一个矩阵，该矩阵缩放和平移变换后的外观框以与注释矩形的边缘对齐
            Matrix a = Matrix.getTranslateInstance(rect.getLowerLeftX(), rect.getLowerLeftY());
            a.scale((float)(rect.getWidth() / transformedBox.getWidth()),
                    (float)(rect.getHeight() / transformedBox.getHeight()));
            a.translate((float) -transformedBox.getX(), (float) -transformedBox.getY());

            // 矩阵应与A连接形成矩阵AA，该矩阵从外观的坐标系映射到默认用户空间中注释的矩形
            //
            // 然而只有相反的顺序对带有外观流中矩阵的旋转页面上的
            // 填充字段/注释有效，参见PDFBOX-3083
            Matrix aa = Matrix.concatenate(a, matrix);

            // 使矩阵AA成为CTM
            getGraphicsState().setCurrentTransformationMatrix(aa);

            // 裁剪到边界框
            clipToRect(bbox);

            // 外观流中图案所需，例如PDFBOX-2182
            initialMatrix = aa.clone();

            try
            {
                processStreamOperators(appearance);
            }
            finally
            {
                restoreGraphicsStack(savedStack);
                popResources(parent);
            }
        }
    }

    /**
     * Process the given tiling pattern.
     *
     * @param tilingPattern the tiling pattern
     * @param color color to use, if this is an uncoloured pattern, otherwise null.
     * @param colorSpace color space to use, if this is an uncoloured pattern, otherwise null.
     * @throws IOException if there is an error reading or parsing the tiling pattern content stream.
     * 
     * 处理给定的平铺图案
     * 处理平铺图案，包括颜色空间设置和坐标变换
     */
    protected final void processTilingPattern(PDTilingPattern tilingPattern, PDColor color,
                                              PDColorSpace colorSpace) throws IOException
    {
        processTilingPattern(tilingPattern, color, colorSpace, tilingPattern.getMatrix());
    }

    /**
     * Process the given tiling pattern. Allows the pattern matrix to be overridden for custom
     * rendering.
     *
     * @param tilingPattern the tiling pattern
     * @param color color to use, if this is an uncoloured pattern, otherwise null.
     * @param colorSpace color space to use, if this is an uncoloured pattern, otherwise null.
     * @param patternMatrix the pattern matrix, may be overridden for custom rendering.
     * @throws IOException if there is an error reading or parsing the tiling pattern content stream.
     * 
     * 处理给定的平铺图案，允许覆盖图案矩阵以进行自定义渲染
     * 处理平铺图案，支持自定义图案矩阵变换
     */
    protected final void processTilingPattern(PDTilingPattern tilingPattern, PDColor color,
                                              PDColorSpace colorSpace, Matrix patternMatrix)
            throws IOException
    {
        PDResources parent = pushResources(tilingPattern);

        Matrix parentMatrix = initialMatrix;
        initialMatrix = Matrix.concatenate(initialMatrix, patternMatrix);

        // 保存原始图形状态
        Deque<PDGraphicsState> savedStack = saveGraphicsStack();

        // 保存干净的状态（新的裁剪路径、线条路径等）
        PDRectangle tilingBBox = tilingPattern.getBBox();
        Rectangle2D bbox = tilingBBox.transform(patternMatrix).getBounds2D();
        PDRectangle rect = new PDRectangle((float)bbox.getX(), (float)bbox.getY(),
                (float)bbox.getWidth(), (float)bbox.getHeight());
        graphicsStack.push(new PDGraphicsState(rect));
        PDGraphicsState graphicsState = getGraphicsState();

        // 无色图案必须给定颜色
        if (colorSpace != null)
        {
            color = new PDColor(color.getComponents(), colorSpace);
            graphicsState.setNonStrokingColorSpace(colorSpace);
            graphicsState.setNonStrokingColor(color);
            graphicsState.setStrokingColorSpace(colorSpace);
            graphicsState.setStrokingColor(color);
        }

        // 使用流的矩阵变换CTM
        graphicsState.getCurrentTransformationMatrix().concatenate(patternMatrix);

        // 裁剪到边界框
        clipToRect(tilingBBox);

        try
        {
            processStreamOperators(tilingPattern);
        }
        finally
        {
            initialMatrix = parentMatrix;
            restoreGraphicsStack(savedStack);
            popResources(parent);
        }
    }

    /**
     * Shows the given annotation.
     *
     * @param annotation An annotation on the current page.
     * @throws IOException If an error occurred reading the annotation
     * 
     * 显示给定的注释
     * 显示当前页面上的注释对象
     */
    public void showAnnotation(PDAnnotation annotation) throws IOException
    {
        PDAppearanceStream appearanceStream = getAppearance(annotation);
        if (appearanceStream != null)
        {
            processAnnotation(annotation, appearanceStream);
        }
    }

    /**
     * Returns the appearance stream to process for the given annotation. May be used to render
     * a specific appearance such as "hover".
     *
     * @param annotation The current annotation.
     * @return The stream to process.
     * 
     * 返回给定注释要处理的外观流
     * 可用于渲染特定外观，如"悬停"状态
     */
    public PDAppearanceStream getAppearance(PDAnnotation annotation)
    {
        return annotation.getNormalAppearanceStream();
    }

    /**
     * Process a child stream of the given page. Cannot be used with {@link #processPage(PDPage)}.
     *
     * @param contentStream the child content stream
     * @param page the page to be used for processing
     * @throws IOException if there is an exception while processing the stream
     * 
     * 处理给定页面的子流
     * 处理页面的子内容流，不能与processPage()同时使用
     */
    protected void processChildStream(PDContentStream contentStream, PDPage page) throws IOException
    {
        if (isProcessingPage)
        {
            throw new IllegalStateException("Current page has already been set via " +
                    " #processPage(PDPage) call #processChildStream(PDContentStream) instead");
        }
        initPage(page);
        processStream(contentStream);
        currentPage = null;
    }

    /**
     * Process a content stream.
     *
     * @param contentStream the content stream
     * @throws IOException if there is an exception while processing the stream
     * 
     * 处理内容流
     * 处理PDF内容流，包括资源管理和矩阵变换
     */
    private void processStream(PDContentStream contentStream) throws IOException
    {
        PDResources parent = pushResources(contentStream);
        Deque<PDGraphicsState> savedStack = saveGraphicsStack();
        Matrix parentMatrix = initialMatrix;
        PDGraphicsState graphicsState = getGraphicsState();

        // 使用流的矩阵变换CTM
        graphicsState.getCurrentTransformationMatrix().concatenate(contentStream.getMatrix());

        // 流的初始矩阵包含父CTM，例如这允许缩放的表单
        initialMatrix = graphicsState.getCurrentTransformationMatrix().clone();

        // 裁剪到边界框
        PDRectangle bbox = contentStream.getBBox();
        clipToRect(bbox);

        try
        {
            processStreamOperators(contentStream);
        }
        finally
        {
            initialMatrix = parentMatrix;
            restoreGraphicsStack(savedStack);
            popResources(parent);
        }
    }

    /**
     * Processes the operators of the given content stream.
     *
     * @param contentStream to content stream to parse.
     * @throws IOException if there is an error reading or parsing the content stream.
     * 
     * 处理给定内容流的操作符
     * 解析并处理内容流中的所有操作符
     */
    private void processStreamOperators(PDContentStream contentStream) throws IOException
    {
        List<COSBase> arguments = new ArrayList<>();
        PDFStreamParser parser = new PDFStreamParser(contentStream);
        Object token = parser.parseNextToken();

        boolean isFirstOperator = true;
        boolean oldShouldProcessColorOperators = shouldProcessColorOperators;
        shouldProcessColorOperators = true;
        if (contentStream instanceof PDTilingPattern &&
            ((PDTilingPattern) contentStream).getPaintType() == PDTilingPattern.PAINT_UNCOLORED)
        {
            shouldProcessColorOperators = false;
        }
        try
        {
            while (token != null)
            {
                if (token instanceof Operator)
                {
                    if (isFirstOperator && contentStream instanceof PDType3CharProc && 
                        OperatorName.TYPE3_D1.equals(((Operator) token).getName()))
                    {
                        shouldProcessColorOperators = false;
                    }
                    isFirstOperator = false;
                    processOperator((Operator) token, arguments);
                    arguments.clear();
                }
                else
                {
                    arguments.add((COSBase) token);
                }
                token = parser.parseNextToken();
            }
        }
        finally
        {
            shouldProcessColorOperators = oldShouldProcessColorOperators;
        }
    }

    /**
     * Pushes the given stream's resources, returning the previous resources.
     * 
     * 推入给定流的资源，返回之前的资源
     * 资源查找：首先查找流资源，然后回退到当前页面
     */
    private PDResources pushResources(PDContentStream contentStream)
    {
        // resource lookup: first look for stream resources, then fallback to the current page
        PDResources parentResources = resources;
        PDResources streamResources = contentStream.getResources();
        if (streamResources != null)
        {
            resources = streamResources;
        }
        else if (resources != null)
        {
            // 直接从父流继承，这不在PDF规范中，但是PDFBOX-1359中的文件这样做并在Acrobat中工作
        }
        else
        {
            resources = currentPage.getResources();

            // PDF中需要资源
            if (resources == null)
            {
                resources = new PDResources();
            }
        }

        return parentResources;
    }

    /**
     * Pops the current resources, replacing them with the given resources.
     * 
     * 弹出当前资源，用给定资源替换它们
     */
    private void popResources(PDResources parentResources)
    {
        resources = parentResources;
    }

    /**
     * Transforms the given rectangle using the CTM and then intersects it with the current
     * clipping area.
     * 
     * 使用CTM变换给定矩形，然后与当前裁剪区域相交
     */
    private void clipToRect(PDRectangle rectangle)
    {
        if (rectangle != null)
        {
            PDGraphicsState graphicsState = getGraphicsState();
            GeneralPath clip = rectangle.transform(graphicsState.getCurrentTransformationMatrix());
            graphicsState.intersectClippingPath(clip);
        }
    }

    /**
     * Called when the BT operator is encountered. This method is for overriding in subclasses, the
     * default implementation does nothing.
     *
     * @throws IOException if there was an error processing the text
     * 
     * 遇到BT操作符时调用
     * 此方法用于在子类中重写，默认实现不执行任何操作
     */
    public void beginText() throws IOException
    {
        // overridden in subclasses
    }

    /**
     * Called when the ET operator is encountered. This method is for overriding in subclasses, the
     * default implementation does nothing.
     *
     * @throws IOException if there was an error processing the text
     * 
     * 遇到ET操作符时调用
     * 此方法用于在子类中重写，默认实现不执行任何操作
     */
    public void endText() throws IOException
    {
        // overridden in subclasses
    }

    /**
     * Called when a string of text is to be shown.
     *
     * @param string the encoded text
     * @throws IOException if there was an error showing the text
     * 
     * 当要显示文本字符串时调用
     * 处理要显示的编码文本字符串
     */
    public void showTextString(byte[] string) throws IOException
    {
        showText(string);
    }

    /**
     * Called when a string of text with spacing adjustments is to be shown.
     *
     * @param array array of encoded text strings and adjustments
     * @throws IOException if there was an error showing the text
     * 
     * 当要显示带有间距调整的文本字符串时调用
     * 处理带有间距调整的编码文本数组
     */
    public void showTextStrings(COSArray array) throws IOException
    {
        PDTextState textState = getGraphicsState().getTextState();
        float fontSize = textState.getFontSize();
        float horizontalScaling = textState.getHorizontalScaling() / 100f;
        PDFont font = textState.getFont();
        boolean isVertical = false;
        if (font != null)
        {
            isVertical = font.isVertical();
        }

        for (COSBase obj : array)
        {
            if (obj instanceof COSNumber)
            {
                float tj = ((COSNumber)obj).floatValue();

                // 计算组合位移
                float tx;
                float ty;
                if (isVertical)
                {
                    tx = 0;
                    ty = -tj / 1000 * fontSize;
                }
                else
                {
                    tx = -tj / 1000 * fontSize * horizontalScaling;
                    ty = 0;
                }

                applyTextAdjustment(tx, ty);
            }
            else if(obj instanceof COSString)
            {
                byte[] string = ((COSString)obj).getBytes();
                showText(string);
            }
            else if (obj instanceof COSArray)
            {
                LOG.error("Nested arrays are not allowed in an array for TJ operation: " + obj);
            }
            else
            {
                LOG.error("Unknown type " + obj.getClass().getSimpleName() +
                        " in array for TJ operation: " + obj);
            }
        }
    }

    /**
     * Applies a text position adjustment from the TJ operator. May be overridden in subclasses.
     *
     * @param tx x-translation
     * @param ty y-translation
     * 
     * 应用来自TJ操作符的文本位置调整
     * 可在子类中重写
     */
    protected void applyTextAdjustment(float tx, float ty)
    {
        // 更新文本矩阵
        getGraphicsState().getTextMatrix().translate(tx, ty);
    }

    /**
     * Process text from the PDF Stream. You should override this method if you want to
     * perform an action when encoded text is being processed.
     *
     * @param string the encoded text
     * @throws IOException if there is an error processing the string
     * 
     * 处理PDF流中的文本
     * 如果要在处理编码文本时执行操作，应该重写此方法
     */
    protected void showText(byte[] string) throws IOException
    {
        PDGraphicsState state = getGraphicsState();
        PDTextState textState = state.getTextState();

        // 获取当前字体
        PDFont font = textState.getFont();
        if (font == null)
        {
            LOG.warn("No current font, will use default");
            font = getDefaultFont();
        }

        float fontSize = textState.getFontSize();
        float horizontalScaling = textState.getHorizontalScaling() / 100f;
        float charSpacing = textState.getCharacterSpacing();

        // 将文本状态参数放入矩阵形式
        Matrix parameters = new Matrix(
                fontSize * horizontalScaling, 0, // 0
                0, fontSize,                     // 0
                0, textState.getRise());         // 1
        
        Matrix textMatrix = state.getTextMatrix();

        // 读取流直到为空
        InputStream in = new ByteArrayInputStream(string);
        while (in.available() > 0)
        {
            // 解码字符
            int before = in.available();
            int code = font.readCode(in);
            int codeLength = before - in.available();

            // 词间距应应用于字符串中每次出现的单字节字符代码
            // 32，当使用简单字体或定义代码32为单字节代码的复合字体时
            float wordSpacing = 0;
            if (codeLength == 1 && code == 32)
            {
                wordSpacing += textState.getWordSpacing();
            }

            // 文本渲染矩阵（文本空间 -> 设备空间）
            Matrix ctm = state.getCurrentTransformationMatrix();
            Matrix textRenderingMatrix = parameters.multiply(textMatrix).multiply(ctm);

            // 获取字形的位置向量（如果是垂直文本）
            // 对垂直文本的更改应该用PDFBOX-2294和PDFBOX-1422进行测试
            if (font.isVertical())
            {
                // 位置向量，在文本空间中
                Vector v = font.getPositionVector(code);

                // 将位置向量应用到水平原点以获得垂直原点
                textRenderingMatrix.translate(v);
            }

            // 获取字形的水平和垂直位移，在文本空间中
            Vector w = font.getDisplacement(code);

            // 处理解码的字形
            showGlyph(textRenderingMatrix, font, code, w);

            // 计算组合位移
            float tx;
            float ty;
            if (font.isVertical())
            {
                tx = 0;
                ty = w.getY() * fontSize + charSpacing + wordSpacing;
            }
            else
            {
                tx = (w.getX() * fontSize + charSpacing + wordSpacing) * horizontalScaling;
                ty = 0;
            }

            // 更新文本矩阵
            textMatrix.translate(tx, ty);
        }
    }

    /**
     * Called when a glyph is to be processed. This method is intended for overriding in subclasses,
     * the default implementation does nothing.
     *
     * @param textRenderingMatrix the current text rendering matrix, T<sub>rm</sub>
     * @param font the current font
     * @param code internal PDF character code for the glyph
     * @param displacement the displacement (i.e. advance) of the glyph in text space
     * @throws IOException if the glyph cannot be processed
     * 
     * 处理字形时调用
     * 此方法旨在在子类中重写，默认实现不执行任何操作
     */
    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement)
            throws IOException
    {
        if (font instanceof PDType3Font)
        {
            showType3Glyph(textRenderingMatrix, (PDType3Font) font, code, displacement);
        }
        else
        {
            showFontGlyph(textRenderingMatrix, font, code, displacement);
        }
    }

    /**
     * Called when a glyph is to be processed. This method is intended for overriding in subclasses,
     * the default implementation does nothing.
     *
     * @param textRenderingMatrix the current text rendering matrix, T<sub>rm</sub>
     * @param font the current font
     * @param code internal PDF character code for the glyph
     * @param displacement the displacement (i.e. advance) of the glyph in text space
     * @throws IOException if the glyph cannot be processed
     * 
     * 处理字形时调用
     * 此方法旨在在子类中重写，默认实现不执行任何操作
     */
    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font,
            int code, Vector displacement) throws IOException
    {
        // overridden in subclasses
    }

    /**
     * Called when a glyph is to be processed. This method is intended for overriding in subclasses,
     * the default implementation does nothing.
     *
     * @param textRenderingMatrix the current text rendering matrix, T<sub>rm</sub>
     * @param font the current font
     * @param code internal PDF character code for the glyph
     * @param displacement the displacement (i.e. advance) of the glyph in text space
     * @throws IOException if the glyph cannot be processed
     * 
     * 处理字形时调用
     * 此方法旨在在子类中重写，默认实现不执行任何操作
     */
    protected void showType3Glyph(Matrix textRenderingMatrix, PDType3Font font, int code,
            Vector displacement) throws IOException
    {
        PDType3CharProc charProc = font.getCharProc(code);
        if (charProc != null)
        {
            processType3Stream(charProc, textRenderingMatrix);
        }
    }

    /**
     * Called when a marked content group begins
     *
     * @param tag indicates the role or significance of the sequence
     * @param properties optional properties
     * 
     * 标记内容组开始时调用
     * 标识序列的角色或重要性
     */
    public void beginMarkedContentSequence(COSName tag, COSDictionary properties)
    {
        // overridden in subclasses
    }

    /**
     * Called when a marked content group ends
     * 
     * 标记内容组结束时调用
     */
    public void endMarkedContentSequence()
    {
        // overridden in subclasses
    }

    /**
     * This is used to handle an operation.
     * 
     * @param operation The operation to perform.
     * @param arguments The list of arguments.
     * @throws IOException If there is an error processing the operation.
     * 
     * 用于处理操作
     * 执行指定的操作及其参数
     */
    public void processOperator(String operation, List<COSBase> arguments) throws IOException
    {
        Operator operator = Operator.getOperator(operation);
        processOperator(operator, arguments);
    }

    /**
     * This is used to handle an operation.
     * 
     * @param operator The operation to perform.
     * @param operands The list of arguments.
     * @throws IOException If there is an error processing the operation.
     * 
     * 用于处理操作
     * 执行指定的操作符及其操作数
     */
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException
    {
        String name = operator.getName();
        OperatorProcessor processor = operators.get(name);
        if (processor != null)
        {
            try
            {
                processor.process(operator, operands);
            }
            catch (IOException e)
            {
                operatorException(operator, operands, e);
            }
        }
        else
        {
            unsupportedOperator(operator, operands);
        }
    }

    /**
     * Called when an unsupported operator is encountered.
     *
     * @param operator The unknown operator.
     * @param operands The list of operands.
     * 
     * @throws IOException if there is an error processing the unsupported operator
     * 
     * 遇到不支持的操作符时调用
     * 处理遇到的未知操作符
     */
    protected void unsupportedOperator(Operator operator, List<COSBase> operands) throws IOException
    {
        // overridden in subclasses
    }

    /**
     * Called when an exception is thrown by an operator.
     *
     * @param operator The unknown operator.
     * @param operands The list of operands.
     * @param exception the exception which occurred when processing the operator
     * 
     * @throws IOException if there is an error processing the operator exception
     * 
     * 操作符抛出异常时调用
     * 处理操作符处理过程中发生的异常
     */
    protected void operatorException(Operator operator, List<COSBase> operands, IOException exception)
            throws IOException
    {
        if (exception instanceof MissingOperandException ||
            exception instanceof MissingResourceException ||
            exception instanceof MissingImageReaderException)
        {
            LOG.error(exception.getMessage(), exception);
        }
        else if (exception instanceof EmptyGraphicsStackException)
        {
            LOG.warn(exception.getMessage(), exception);
        }
        else if (operator.getName().equals("Do"))
        {
            // todo: 这太宽容了，但PDFBox一直以来都是这样工作的
            //       需要仔细重构
            LOG.warn(exception.getMessage(), exception);
        }
        else if (exception.getCause() instanceof DataFormatException)
        {
            LOG.warn(exception.getMessage(), exception);
        }
        else
        {
            throw exception;
        }
    }

    /**
     * Pushes the current graphics state to the stack.
     * 
     * 将当前图形状态推入栈中
     */
    public void saveGraphicsState()
    {
        graphicsStack.push(graphicsStack.peek().clone());
    }

    /**
     * Pops the current graphics state from the stack.
     * 
     * 从栈中弹出当前图形状态
     */
    public void restoreGraphicsState()
    {
        graphicsStack.pop();
    }

    /**
     * Saves the entire graphics stack.
     * 
     * @return the saved graphics state stack.
     * 
     * 保存整个图形状态栈
     * 返回保存的图形状态栈
     */
    protected final Deque<PDGraphicsState> saveGraphicsStack()
    {
        Deque<PDGraphicsState> savedStack = graphicsStack;
        graphicsStack = new ArrayDeque<>(1);
        graphicsStack.add(savedStack.peek().clone());
        return savedStack;
    }

    /**
     * Restores the entire graphics stack.
     * 
     * @param snapshot the graphics state to be restored
     * 
     * 恢复整个图形状态栈
     * 恢复指定的图形状态快照
     */
    protected final void restoreGraphicsStack(Deque<PDGraphicsState> snapshot)
    {
        graphicsStack = snapshot;
    }
    
    /**
     * @return Returns the size of the graphicsStack.
     * 
     * 返回图形状态栈的大小
     */
    public int getGraphicsStackSize()
    {
        return graphicsStack.size();
    }

    /**
     * @return Returns the graphicsState.
     * 
     * 返回当前图形状态
     */
    public PDGraphicsState getGraphicsState()
    {
        return graphicsStack.peek();
    }

    /**
     * @return Returns the textLineMatrix.
     * 
     * 返回文本行矩阵
     */
    public Matrix getTextLineMatrix()
    {
        return getGraphicsState().getTextLineMatrix();
    }

    /**
     * @param value The textLineMatrix to set.
     * 
     * 设置文本行矩阵
     */
    public void setTextLineMatrix(Matrix value)
    {
        getGraphicsState().setTextLineMatrix(value);
    }

    /**
     * @return Returns the textMatrix.
     * 
     * 返回文本矩阵
     */
    public Matrix getTextMatrix()
    {
        return getGraphicsState().getTextMatrix();
    }

    /**
     * @param value The textMatrix to set.
     * 
     * 设置文本矩阵
     */
    public void setTextMatrix(Matrix value)
    {
        getGraphicsState().setTextMatrix(value);
    }

    /**
     * @param array dash array
     * @param phase dash phase
     * 
     * 设置线型模式（虚线数组和相位）
     */
    public void setLineDashPattern(COSArray array, int phase)
    {
        PDLineDashPattern lineDash = new PDLineDashPattern(array, phase);
        getGraphicsState().setLineDashPattern(lineDash);
    }

    /**
     * @return the stream' resources. This is mainly to be used by the {@link OperatorProcessor}
     * classes.
     * 
     * 返回流的资源
     * 主要供OperatorProcessor类使用
     */
    public PDResources getResources()
    {
        return resources;
    }

    /**
     * Returns the current page.
     * 
     * @return the current page
     * 
     * 返回当前页面
     */
    public PDPage getCurrentPage()
    {
        return currentPage;
    }

    /**
     * Gets the stream's initial matrix.
     * 
     * @return the initial matrix
     * 
     * 获取流的初始矩阵
     */
    public Matrix getInitialMatrix()
    {
        return initialMatrix;
    }

    /**
     * Transforms a point using the CTM.
     * 
     * @param x the x-coordinate of the point to be transformed
     * @param y the y-coordinate of the point to be transformed
     * 
     * @return the transformed point
     * 
     * 使用CTM变换点
     * 返回变换后的点坐标
     */
    public Point2D.Float transformedPoint(float x, float y)
    {
        float[] position = { x, y };
        getGraphicsState().getCurrentTransformationMatrix().createAffineTransform()
                .transform(position, 0, position, 0, 1);
        return new Point2D.Float(position[0], position[1]);
    }

    /**
     * Transforms a width using the CTM.
     * 
     * @param width the width to be transformed
     * 
     * @return the transformed width
     * 
     * 使用CTM变换宽度
     * 返回变换后的宽度值
     */
    protected float transformWidth(float width)
    {
        Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
        float x = ctm.getScaleX() + ctm.getShearX();
        float y = ctm.getScaleY() + ctm.getShearY();
        return width * (float)Math.sqrt((x * x + y * y) * 0.5);
    }

    /**
     * Get the current level. This can be used to decide whether a recursion has done too deep and
     * an operation should be skipped to avoid a stack overflow.
     *
     * @return the current level.
     * 
     * 获取当前级别
     * 可用于决定递归是否过深，应跳过操作以避免栈溢出
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * Increase the level. Call this before running a potentially recursive operation.
     * 
     * 增加级别
     * 在运行潜在的递归操作之前调用此方法
     */
    public void increaseLevel()
    {
        ++level;
    }

    /**
     * Decrease the level. Call this after running a potentially recursive operation. A log message
     * is shown if the level is below 0. This can happen if the level is not decreased after an
     * operation is done, e.g. by using a "finally" block.
     * 
     * 减少级别
     * 在运行潜在的递归操作之后调用此方法
     * 如果级别低于0会显示日志消息
     * 如果操作完成后没有减少级别（例如未使用"finally"块）可能发生这种情况
     */
    public void decreaseLevel()
    {
        --level;
        if (level < 0)
        {
            LOG.error("level is " + level);
        }
    }

    /**
     * Tells whether color operators should be processed. To be used in some OperatorProcessor
     * classes.
     *
     * @return true if color operators should be processed, false if not, e.g. in type3 charprocs
     * with d1 or in uncolored tiling patterns.
     * 
     * 指示是否应处理颜色操作符
     * 在某些OperatorProcessor类中使用
     * 
     * @return 如果应处理颜色操作符则返回true，否则返回false
     *         例如在带有d1的type3字符过程或无色平铺图案中返回false
     */
    public boolean isShouldProcessColorOperators()
    {
        return shouldProcessColorOperators;
    }

    /**
     * Handles MP and DP operators.
     *
     * @param tag indicates the role or significance of the sequence
     * @param properties optional properties
     * 
     * 处理MP和DP操作符
     * 标识序列的角色或重要性
     */
    public void markedContentPoint(COSName tag, COSDictionary properties)
    {
        // overridden in subclasses
    }
}