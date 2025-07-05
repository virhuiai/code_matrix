/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxText
 *
 * Extends <mxShape> to implement a text shape. To change vertical text from
 * bottom to top to top to bottom, the following code can be used:
 * 
 * (code)
 * mxText.prototype.verticalTextRotation = 90;
 * (end)
 * 
 * // 类：mxText
 * // 扩展 <mxShape> 以实现文本形状。可以通过以下代码更改垂直文本的方向，从底部到顶部变为顶部到底部：
 * // (代码)
 * // mxText.prototype.verticalTextRotation = 90;
 * // (结束)
 *
 * Constructor: mxText
 *
 * Constructs a new text shape.
 * 
 * Parameters:
 * 
 * value - String that represents the text to be displayed. This is stored in
 * <value>.
 * bounds - <mxRectangle> that defines the bounds. This is stored in
 * <mxShape.bounds>.
 * align - Specifies the horizontal alignment. Default is ''. This is stored in
 * <align>.
 * valign - Specifies the vertical alignment. Default is ''. This is stored in
 * <valign>.
 * color - String that specifies the text color. Default is 'black'. This is
 * stored in <color>.
 * family - String that specifies the font family. Default is
 * <mxConstants.DEFAULT_FONTFAMILY>. This is stored in <family>.
 * size - Integer that specifies the font size. Default is
 * <mxConstants.DEFAULT_FONTSIZE>. This is stored in <size>.
 * fontStyle - Specifies the font style. Default is 0. This is stored in
 * <fontStyle>.
 * spacing - Integer that specifies the global spacing. Default is 2. This is
 * stored in <spacing>.
 * spacingTop - Integer that specifies the top spacing. Default is 0. The
 * sum of the spacing and this is stored in <spacingTop>.
 * spacingRight - Integer that specifies the right spacing. Default is 0. The
 * sum of the spacing and this is stored in <spacingRight>.
 * spacingBottom - Integer that specifies the bottom spacing. Default is 0.The
 * sum of the spacing and this is stored in <spacingBottom>.
 * spacingLeft - Integer that specifies the left spacing. Default is 0. The
 * sum of the spacing and this is stored in <spacingLeft>.
 * horizontal - Boolean that specifies if the label is horizontal. Default is
 * true. This is stored in <horizontal>.
 * background - String that specifies the background color. Default is null.
 * This is stored in <background>.
 * border - String that specifies the label border color. Default is null.
 * This is stored in <border>.
 * wrap - Specifies if word-wrapping should be enabled. Default is false.
 * This is stored in <wrap>.
 * clipped - Specifies if the label should be clipped. Default is false.
 * This is stored in <clipped>.
 * overflow - Value of the overflow style. Default is 'visible'.
 *
 * // 构造函数：mxText
 * // 构造一个新的文本形状。
 * // 参数说明：
 * // value - 表示要显示的文本字符串，存储在 <value> 中。
 * // bounds - 定义边界范围的 <mxRectangle>，存储在 <mxShape.bounds> 中。
 * // align - 指定水平对齐方式，默认值为空字符串，存储在 <align> 中。
 * // valign - 指定垂直对齐方式，默认值为空字符串，存储在 <valign> 中。
 * // color - 指定文本颜色的字符串，默认值为 'black'，存储在 <color> 中。
 * // family - 指定字体家族的字符串，默认值为 <mxConstants.DEFAULT_FONTFAMILY>，存储在 <family> 中。
 * // size - 指定字体大小的整数，默认值为 <mxConstants.DEFAULT_FONTSIZE>，存储在 <size> 中。
 * // fontStyle - 指定字体样式的整数，默认值为 0，存储在 <fontStyle> 中。
 * // spacing - 指定全局间距的整数，默认值为 2，存储在 <spacing> 中。
 * // spacingTop - 指定顶部间距的整数，默认值为 0，存储值为 spacing + spacingTop，存储在 <spacingTop> 中。
 * // spacingRight - 指定右侧间距的整数，默认值为 0，存储值为 spacing + spacingRight，存储在 <spacingRight> 中。
 * // spacingBottom - 指定底部间距的整数，默认值为 0，存储值为 spacing + spacingBottom，存储在 <spacingBottom> 中。
 * // spacingLeft - 指定左侧间距的整数，默认值为 0，存储值为 spacing + spacingLeft，存储在 <spacingLeft> 中。
 * // horizontal - 指定标签是否水平显示的布尔值，默认值为 true，存储在 <horizontal> 中。
 * // background - 指定背景颜色的字符串，默认值为 null，存储在 <background> 中。
 * // border - 指定标签边框颜色的字符串，默认值为 null，存储在 <border> 中。
 * // wrap - 指定是否启用自动换行的布尔值，默认值为 false，存储在 <wrap> 中。
 * // clipped - 指定标签是否需要裁剪的布尔值，默认值为 false，存储在 <clipped> 中。
 * // overflow - 指定溢出样式的值，默认值为 'visible'。
 */
function mxText(value, bounds, align, valign, color,
	family,	size, fontStyle, spacing, spacingTop, spacingRight,
	spacingBottom, spacingLeft, horizontal, background, border,
	wrap, clipped, overflow, labelPadding, textDirection)
{
	mxShape.call(this);
	this.value = value;
	this.bounds = bounds;
	this.color = (color != null) ? color : 'black';
	this.align = (align != null) ? align : mxConstants.ALIGN_CENTER;
	this.valign = (valign != null) ? valign : mxConstants.ALIGN_MIDDLE;
	this.family = (family != null) ? family : mxConstants.DEFAULT_FONTFAMILY;
	this.size = (size != null) ? size : mxConstants.DEFAULT_FONTSIZE;
	this.fontStyle = (fontStyle != null) ? fontStyle : mxConstants.DEFAULT_FONTSTYLE;
	this.spacing = parseInt(spacing || 2);
	this.spacingTop = this.spacing + parseInt(spacingTop || 0);
	this.spacingRight = this.spacing + parseInt(spacingRight || 0);
	this.spacingBottom = this.spacing + parseInt(spacingBottom || 0);
	this.spacingLeft = this.spacing + parseInt(spacingLeft || 0);
	this.horizontal = (horizontal != null) ? horizontal : true;
	this.background = background;
	this.border = border;
	this.wrap = (wrap != null) ? wrap : false;
	this.clipped = (clipped != null) ? clipped : false;
	this.overflow = (overflow != null) ? overflow : 'visible';
	this.labelPadding = (labelPadding != null) ? labelPadding : 0;
	this.textDirection = textDirection;
	this.rotation = 0;
	this.updateMargin();

    // // 初始化 mxText 对象，继承自 mxShape。
    // // 功能：设置文本形状的属性，包括文本内容、边界、对齐方式、颜色、字体、间距等。
    // // 关键变量用途：
    // // - value：存储要显示的文本内容。
    // // - bounds：定义文本的边界范围（mxRectangle 对象）。
    // // - color：文本颜色，默认为黑色。
    // // - align/valign：控制文本水平和垂直对齐方式，默认居中。
    // // - family/size/fontStyle：定义字体家族、大小和样式，分别有默认值。
    // // - spacing*：控制文本四周的间距，计算方式为全局间距加上指定方向的间距。
    // // - horizontal：控制文本是否水平显示，默认为 true。
    // // - background/border：定义背景和边框颜色，默认无。
    // // - wrap/clipped/overflow：控制文本换行、裁剪和溢出行为。
    // // - labelPadding：标签内边距，默认值为 0。
    // // - textDirection：文本方向，支持自动、左到右或右到左。
    // // - rotation：文本旋转角度，默认为 0。
    // // 方法调用：updateMargin 用于更新文本的外边距。
};

/**
 * Extends mxShape.
 */
mxUtils.extend(mxText, mxShape);

/**
 * Variable: baseSpacingTop
 * 
 * Specifies the spacing to be added to the top spacing. Default is 0. Use the
 * value 5 here to get the same label positions as in mxGraph 1.x.
 */
mxText.prototype.baseSpacingTop = 0;

/**
 * Variable: baseSpacingBottom
 * 
 * Specifies the spacing to be added to the bottom spacing. Default is 0. Use the
 * value 1 here to get the same label positions as in mxGraph 1.x.
 */
mxText.prototype.baseSpacingBottom = 0;

/**
 * Variable: baseSpacingLeft
 * 
 * Specifies the spacing to be added to the left spacing. Default is 0.
 */
mxText.prototype.baseSpacingLeft = 0;

/**
 * Variable: baseSpacingRight
 * 
 * Specifies the spacing to be added to the right spacing. Default is 0.
 */
mxText.prototype.baseSpacingRight = 0;

/**
 * Variable: replaceLinefeeds
 * 
 * Specifies if linefeeds in HTML labels should be replaced with BR tags.
 * Default is true.
 */
mxText.prototype.replaceLinefeeds = true;

/**
 * Variable: verticalTextRotation
 * 
 * Rotation for vertical text. Default is -90 (bottom to top).
 */
mxText.prototype.verticalTextRotation = -90;

/**
 * Variable: ignoreClippedStringSize
 * 
 * Specifies if the string size should be measured in <updateBoundingBox> if
 * the label is clipped and the label position is center and middle. If this is
 * true, then the bounding box will be set to <bounds>. Default is true.
 * <ignoreStringSize> has precedence over this switch.
 *
 * // 变量：ignoreClippedStringSize
 * // 用途：指定当标签被裁剪且居中对齐时，是否在 <updateBoundingBox> 中测量字符串大小。
 * // 默认值：true（使用 bounds 作为边界框）。
 * // 注意事项：<ignoreStringSize> 的优先级高于此设置。
 */
mxText.prototype.ignoreClippedStringSize = true;

/**
 * Variable: ignoreStringSize
 * 
 * Specifies if the actual string size should be measured. If disabled the
 * boundingBox will not ignore the actual size of the string, otherwise
 * <bounds> will be used instead. Default is false.
 *
 * // 变量：ignoreStringSize
 * // 用途：指定是否测量实际字符串大小，默认值为 false。
 * // 说明：若为 false，则边界框考虑实际字符串大小；否则使用 <bounds>。
 */
mxText.prototype.ignoreStringSize = false;

/**
 * Variable: textWidthPadding
 * 
 * Specifies the padding to be added to the text width for the bounding box.
 * This is needed to make sure no clipping is applied to borders. Default is 4
 * for IE 8 standards mode and 3 for all others.
 *
 * // 变量：textWidthPadding
 * // 用途：指定边界框中文本宽度的内边距，确保边框不被裁剪。
 * // 默认值：IE 8 标准模式下为 4，其他情况为 3。
 */
mxText.prototype.textWidthPadding = (document.documentMode == 8 && !mxClient.IS_EM) ? 4 : 3;

/**
 * Variable: lastValue
 * 
 * Contains the last rendered text value. Used for caching.
 *
 * // 变量：lastValue
 * // 用途：存储最后渲染的文本值，用于缓存以提高性能。
 */
mxText.prototype.lastValue = null;

/**
 * Variable: cacheEnabled
 * 
 * Specifies if caching for HTML labels should be enabled. Default is true.
 *
 * // 变量：cacheEnabled
 * // 用途：指定是否为 HTML 标签启用缓存，默认值为 true。
 * // 说明：缓存可减少重复渲染，提高性能。
 */
mxText.prototype.cacheEnabled = true;

/**
 * Function: isParseVml
 * 
 * Text shapes do not contain VML markup and do not need to be parsed. This
 * method returns false to speed up rendering in IE8.
 *
 * // 函数：isParseVml
 * // 目的：判断文本形状是否需要解析 VML 标记。
 * // 返回值：false（文本形状不包含 VML 标记，无需解析）。
 * // 说明：用于在 IE8 中加速渲染。
 */
mxText.prototype.isParseVml = function()
{
	return false;
};

/**
 * Function: isHtmlAllowed
 * 
 * Returns true if HTML is allowed for this shape. This implementation returns
 * true if the browser is not in IE8 standards mode.
 *
 * // 函数：isHtmlAllowed
 * // 目的：判断是否允许为此形状使用 HTML 渲染。
 * // 返回值：如果浏览器不在 IE8 标准模式下，返回 true。
 * // 说明：确保在非 IE8 标准模式下支持 HTML 标签渲染。
 */
mxText.prototype.isHtmlAllowed = function()
{
	return document.documentMode != 8 || mxClient.IS_EM;
};

/**
 * Function: getSvgScreenOffset
 * 
 * Disables offset in IE9 for crisper image output.
 *
 * // 函数：getSvgScreenOffset
 * // 目的：在 IE9 中禁用偏移以获得更清晰的图像输出。
 * // 返回值：0（禁用偏移）。
 * // 说明：优化 SVG 渲染效果。
 */
mxText.prototype.getSvgScreenOffset = function()
{
	return 0;
};

/**
 * Function: checkBounds
 * 
 * Returns true if the bounds are not null and all of its variables are numeric.
 *
 * // 函数：checkBounds
 * // 目的：检查边界是否有效（非空且所有变量为数值）。
 * // 返回值：如果边界有效且缩放比例大于 0，返回 true。
 * // 说明：确保边界参数适合渲染。
 */
mxText.prototype.checkBounds = function()
{
	return (!isNaN(this.scale) && isFinite(this.scale) && this.scale > 0 &&
			this.bounds != null && !isNaN(this.bounds.x) && !isNaN(this.bounds.y) &&
			!isNaN(this.bounds.width) && !isNaN(this.bounds.height));
};

/**
 * Function: paint
 * 
 * Generic rendering code.
 *
 * // 函数：paint
 * // 目的：通用文本渲染逻辑。
 * // 参数：
 * // - c：画布对象，用于绘制文本。
 * // - update：布尔值，指示是否更新现有文本。
 * // 功能：根据缩放比例、边界和样式设置，在画布上绘制或更新文本。
 * // 说明：处理文本的对齐、换行、裁剪和旋转等样式。
 * // 特殊处理：支持 HTML 标签，自动检测文本方向，处理换行符。
 */
mxText.prototype.paint = function(c, update)
{
	// Scale is passed-through to canvas
	var s = this.scale;
	var x = this.bounds.x / s;
	var y = this.bounds.y / s;
	var w = this.bounds.width / s;
	var h = this.bounds.height / s;
	
	this.updateTransform(c, x, y, w, h);
	this.configureCanvas(c, x, y, w, h);
	
	if (update)
	{
		c.updateText(x, y, w, h, this.align, this.valign, this.wrap, this.overflow,
				this.clipped, this.getTextRotation(), this.node);
	}
	else
	{
		// Checks if text contains HTML markup
		var realHtml = mxUtils.isNode(this.value) || this.dialect == mxConstants.DIALECT_STRICTHTML;
		
		// Always renders labels as HTML in VML
		var fmt = (realHtml || c instanceof mxVmlCanvas2D) ? 'html' : '';
		var val = this.value;
		
		if (!realHtml && fmt == 'html')
		{
			val = mxUtils.htmlEntities(val, false);
		}
		
		if (fmt == 'html' && !mxUtils.isNode(this.value))
		{
			val = mxUtils.replaceTrailingNewlines(val, '<div><br></div>');			
		}
		
		// Handles trailing newlines to make sure they are visible in rendering output
		val = (!mxUtils.isNode(this.value) && this.replaceLinefeeds && fmt == 'html') ?
			val.replace(/\n/g, '<br/>') : val;
			
		var dir = this.textDirection;
	
		if (dir == mxConstants.TEXT_DIRECTION_AUTO && !realHtml)
		{
			dir = this.getAutoDirection();
		}
		
		if (dir != mxConstants.TEXT_DIRECTION_LTR && dir != mxConstants.TEXT_DIRECTION_RTL)
		{
			dir = null;
		}
		
		c.text(x, y, w, h, val, this.align, this.valign, this.wrap, fmt,
			this.overflow, this.clipped, this.getTextRotation(), dir);
	}

    // // 渲染逻辑：
    // // 1. 计算缩放后的边界坐标和尺寸。
    // // 2. 调用 updateTransform 设置画布变换（位置、旋转等）。
    // // 3. 调用 configureCanvas 配置画布的字体、颜色等样式。
    // // 4. 如果 update 为 true，更新现有文本；否则绘制新文本。
    // // 特殊处理：
    // // - 检查文本是否包含 HTML 标记，决定渲染格式（HTML 或纯文本）。
    // // - 在 VML 模式下始终使用 HTML 渲染。
    // // - 处理 HTML 编码和换行符替换，确保渲染输出正确。
    // // - 自动检测文本方向（左到右或右到左）。
    // // 交互逻辑：通过画布方法 c.text 或 c.updateText 实现文本绘制或更新。
};

/**
 * Function: redraw
 * 
 * Renders the text using the given DOM nodes.
 *
 * // 函数：redraw
 * // 目的：使用给定的 DOM 节点重新渲染文本。
 * // 功能：根据文本的可见性、边界有效性和缓存状态，决定如何渲染文本。
 * // 说明：支持 HTML 和非 HTML 渲染，处理 SVG 和 VML 画布的差异。
 * // 特殊处理：当使用缓存且值未改变时，直接更新现有 DOM 节点。
 */
mxText.prototype.redraw = function()
{
	if (this.visible && this.checkBounds() && this.cacheEnabled && this.lastValue == this.value &&
		(mxUtils.isNode(this.value) || this.dialect == mxConstants.DIALECT_STRICTHTML))
	{
		if (this.node.nodeName == 'DIV' && (this.isHtmlAllowed() || !mxClient.IS_VML))
		{
			if (mxClient.IS_SVG)
			{
				this.redrawHtmlShapeWithCss3();	
			}
			else
			{
				this.updateSize(this.node, (this.state == null || this.state.view.textDiv == null));
	
				if (mxClient.IS_IE && (document.documentMode == null || document.documentMode <= 8))
				{
					this.updateHtmlFilter();
				}
				else
				{
					this.updateHtmlTransform();
				}
			}
			
			this.updateBoundingBox();
		}
		else
		{
			var canvas = this.createCanvas();

			if (canvas != null && canvas.updateText != null)
			{
				// Specifies if events should be handled
				canvas.pointerEvents = this.pointerEvents;
	
				this.paint(canvas, true);
				this.destroyCanvas(canvas);
				this.updateBoundingBox();
			}
			else
			{
				// Fallback if canvas does not support updateText (VML)
				mxShape.prototype.redraw.apply(this, arguments);
			}
		}
	}
	else
	{
		mxShape.prototype.redraw.apply(this, arguments);
		
		if (mxUtils.isNode(this.value) || this.dialect == mxConstants.DIALECT_STRICTHTML)
		{
			this.lastValue = this.value;
		}
		else
		{
			this.lastValue = null;
		}
	}

    // // 渲染逻辑：
    // // 1. 检查文本可见性和边界有效性，决定是否渲染。
    // // 2. 如果启用缓存且值未改变，使用现有 DOM 节点更新。
    // // 3. 对于 HTML 标签，使用 CSS3（SVG）或 HTML 变换（非 SVG）渲染。
    // // 4. 对于非 HTML 或 VML 模式，使用画布绘制或回退到父类方法。
    // // 交互逻辑：处理指针事件（pointerEvents），支持用户交互。
    // // 特殊处理：缓存值（lastValue）用于优化性能，避免重复渲染。
};

/**
 * Function: resetStyles
 * 
 * Resets all styles.
 *
 * // 函数：resetStyles
 * // 目的：重置所有样式到默认值。
 * // 功能：调用父类的 resetStyles 方法，并重置文本特定的样式属性。
 * // 说明：确保文本样式恢复到初始状态，便于重新配置。
 */
mxText.prototype.resetStyles = function()
{
	mxShape.prototype.resetStyles.apply(this, arguments);
	
	this.color = 'black';
	this.align = mxConstants.ALIGN_CENTER;
	this.valign = mxConstants.ALIGN_MIDDLE;
	this.family = mxConstants.DEFAULT_FONTFAMILY;
	this.size = mxConstants.DEFAULT_FONTSIZE;
	this.fontStyle = mxConstants.DEFAULT_FONTSTYLE;
	this.spacing = 2;
	this.spacingTop = 2;
	this.spacingRight = 2;
	this.spacingBottom = 2;
	this.spacingLeft = 2;
	this.horizontal = true;
	delete this.background;
	delete this.border;
	this.textDirection = mxConstants.DEFAULT_TEXT_DIRECTION;
	delete this.margin;

    // // 样式设置：
    // // - color：重置为黑色。
    // // - align/valign：重置为居中对齐。
    // // - family/size/fontStyle：重置为默认字体家族、大小和样式。
    // // - spacing*：重置间距为默认值 2。
    // // - horizontal：重置为水平显示。
    // // - background/border：删除背景和边框。
    // // - textDirection：重置为默认文本方向。
    // // - margin：删除外边距。
};

/**
 * Function: apply
 * 
 * Extends mxShape to update the text styles.
 *
 * Parameters:
 *
 * state - <mxCellState> of the corresponding cell.
 *
 * // 函数：apply
 * // 目的：更新文本样式，扩展 mxShape 的样式应用逻辑。
 * // 参数：
 * // - state：<mxCellState> 对象，表示对应的单元格状态。
 * // 功能：根据状态和样式配置更新文本的字体、颜色、对齐等属性。
 * // 说明：从样式对象中获取值，若无则使用默认值。
 */
mxText.prototype.apply = function(state)
{
	var old = this.spacing;
	mxShape.prototype.apply.apply(this, arguments);
	
	if (this.style != null)
	{
		this.fontStyle = mxUtils.getValue(this.style, mxConstants.STYLE_FONTSTYLE, this.fontStyle);
		this.family = mxUtils.getValue(this.style, mxConstants.STYLE_FONTFAMILY, this.family);
		this.size = mxUtils.getValue(this.style, mxConstants.STYLE_FONTSIZE, this.size);
		this.color = mxUtils.getValue(this.style, mxConstants.STYLE_FONTCOLOR, this.color);
		this.align = mxUtils.getValue(this.style, mxConstants.STYLE_ALIGN, this.align);
		this.valign = mxUtils.getValue(this.style, mxConstants.STYLE_VERTICAL_ALIGN, this.valign);
		this.spacing = parseInt(mxUtils.getValue(this.style, mxConstants.STYLE_SPACING, this.spacing));
		this.spacingTop = parseInt(mxUtils.getValue(this.style, mxConstants.STYLE_SPACING_TOP, this.spacingTop - old)) + this.spacing;
		this.spacingRight = parseInt(mxUtils.getValue(this.style, mxConstants.STYLE_SPACING_RIGHT, this.spacingRight - old)) + this.spacing;
		this.spacingBottom = parseInt(mxUtils.getValue(this.style, mxConstants.STYLE_SPACING_BOTTOM, this.spacingBottom - old)) + this.spacing;
		this.spacingLeft = parseInt(mxUtils.getValue(this.style, mxConstants.STYLE_SPACING_LEFT, this.spacingLeft - old)) + this.spacing;
		this.horizontal = mxUtils.getValue(this.style, mxConstants.STYLE_HORIZONTAL, this.horizontal);
		this.background = mxUtils.getValue(this.style, mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, this.background);
		this.border = mxUtils.getValue(this.style, mxConstants.STYLE_LABEL_BORDERCOLOR, this.border);
		this.textDirection = mxUtils.getValue(this.style, mxConstants.STYLE_TEXT_DIRECTION, mxConstants.DEFAULT_TEXT_DIRECTION);
		this.opacity = mxUtils.getValue(this.style, mxConstants.STYLE_TEXT_OPACITY, 100);
		this.updateMargin();
	}
	
	this.flipV = null;
	this.flipH = null;

    // // 样式设置：
    // // - 从样式对象中提取字体样式、家族、大小、颜色、对齐、间距等。
    // // - 计算间距（spacing*）为全局间距加上指定方向的增量。
    // // - 支持背景、边框、文本方向和透明度的设置。
    // // 方法调用：updateMargin 更新外边距。
    // // 特殊处理：重置翻转属性（flipV 和 flipH）为 null。
};

/**
 * Function: getAutoDirection
 * 
 * Used to determine the automatic text direction. Returns
 * <mxConstants.TEXT_DIRECTION_LTR> or <mxConstants.TEXT_DIRECTION_RTL>
 * depending on the contents of <value>. This is not invoked for HTML, wrapped
 * content or if <value> is a DOM node.
 *
 * // 函数：getAutoDirection
 * // 目的：根据文本内容自动确定文本方向。
 * // 返回值：根据文本内容返回 <mxConstants.TEXT_DIRECTION_LTR>（左到右）或 <mxConstants.TEXT_DIRECTION_RTL>（右到左）。
 * // 说明：仅对非 HTML、非换行且非 DOM 节点的文本生效。
 * // 逻辑：检查文本中的强方向字符，判断语言方向。
 */
mxText.prototype.getAutoDirection = function()
{
	// Looks for strong (directional) characters
	var tmp = /[A-Za-z\u05d0-\u065f\u066a-\u06ef\u06fa-\u07ff\ufb1d-\ufdff\ufe70-\ufefc]/.exec(this.value);
	
	// Returns the direction defined by the character
	return (tmp != null && tmp.length > 0 && tmp[0] > 'z') ?
		mxConstants.TEXT_DIRECTION_RTL : mxConstants.TEXT_DIRECTION_LTR;

    // // 逻辑说明：
    // // 1. 使用正则表达式匹配文本中的字母或特定 Unicode 字符。
    // // 2. 如果匹配到字符且其 Unicode 值大于 'z'，返回右到左（RTL）；否则返回左到右（LTR）。
};

/**
 * Function: getContentNode
 * 
 * Returns the node that contains the rendered input.
 *
 * // 函数：getContentNode
 * // 目的：返回包含渲染内容的 DOM 节点。
 * // 返回值：渲染内容的 DOM 节点。
 * // 说明：根据是否使用 SVG，确定返回的节点层级。
 */
mxText.prototype.getContentNode = function()
{
	var result = this.node;
	
	if (result != null)
	{
		// Rendered with no foreignObject
		if (result.ownerSVGElement == null)
		{
			result = this.node.firstChild.firstChild;
		}
		else
		{
			// Innermost DIV that contains the actual content
			result = result.firstChild.firstChild.firstChild.firstChild.firstChild;
		}
	}
	
	return result;

    // // 逻辑说明：
    // // 1. 如果节点不在 SVG 环境中，返回第一级子节点的子节点。
    // // 2. 如果在 SVG 环境中，返回嵌套最深的 DIV 节点（实际内容节点）。
};

/**
 * Function: updateBoundingBox
 *
 * Updates the <boundingBox> for this shape using the given node and position.
 *
 * // 函数：updateBoundingBox
 * // 目的：根据给定的节点和位置更新形状的边界框（boundingBox）。
 * // 功能：根据文本内容、样式和缩放比例，计算并更新边界框。
 * // 说明：考虑裁剪、对齐、旋转和溢出等因素，调整边界框的大小和位置。
 */
mxText.prototype.updateBoundingBox = function()
{
	var node = this.node;
	this.boundingBox = this.bounds.clone();
	var rot = this.getTextRotation();
	
	var h = (this.style != null) ? mxUtils.getValue(this.style, mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER) : null;
	var v = (this.style != null) ? mxUtils.getValue(this.style, mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE) : null;

	if (!this.ignoreStringSize && node != null && this.overflow != 'fill' && (!this.clipped ||
		!this.ignoreClippedStringSize || h != mxConstants.ALIGN_CENTER || v != mxConstants.ALIGN_MIDDLE))
	{
		var ow = null;
		var oh = null;
		
		if (node.ownerSVGElement != null)
		{
			if (node.firstChild != null && node.firstChild.firstChild != null &&
				node.firstChild.firstChild.nodeName == 'foreignObject')
			{
				// Uses second inner DIV for font metrics
				node = node.firstChild.firstChild.firstChild.firstChild;
				oh = node.offsetHeight * this.scale;
				
				if (this.overflow == 'width')
				{
					ow = this.boundingBox.width;
				}
				else
				{
					ow = node.offsetWidth * this.scale;	
				}
			}
			else
			{
				try
				{
					var b = node.getBBox();
					
					// Workaround for bounding box of empty string
					if (typeof(this.value) == 'string' && mxUtils.trim(this.value) == 0)
					{
						this.boundingBox = null;
					}
					else if (b.width == 0 && b.height == 0)
					{
						this.boundingBox = null;
					}
					else
					{
						this.boundingBox = new mxRectangle(b.x, b.y, b.width, b.height);
					}
					
					return;
				}
				catch (e)
				{
					// Ignores NS_ERROR_FAILURE in FF if container display is none.
				}
			}
		}
		else
		{
			var td = (this.state != null) ? this.state.view.textDiv : null;

			// Use cached offset size
			if (this.offsetWidth != null && this.offsetHeight != null)
			{
				ow = this.offsetWidth * this.scale;
				oh = this.offsetHeight * this.scale;
			}
			else
			{
				// Cannot get node size while container hidden so a
				// shared temporary DIV is used for text measuring
				if (td != null)
				{
					this.updateFont(td);
					this.updateSize(td, false);
					this.updateInnerHtml(td);

					node = td;
				}
				
				var sizeDiv = node;

				if (document.documentMode == 8 && !mxClient.IS_EM)
				{
					var w = Math.round(this.bounds.width / this.scale);
	
					if (this.wrap && w > 0)
					{
						node.style.wordWrap = mxConstants.WORD_WRAP;
						node.style.whiteSpace = 'normal';

						if (node.style.wordWrap != 'break-word')
						{
							// Innermost DIV is used for measuring text
							var divs = sizeDiv.getElementsByTagName('div');
							
							if (divs.length > 0)
							{
								sizeDiv = divs[divs.length - 1];
							}
							
							ow = sizeDiv.offsetWidth + 2;
							divs = this.node.getElementsByTagName('div');
							
							if (this.clipped)
							{
								ow = Math.min(w, ow);
							}
							
							// Second last DIV width must be updated in DOM tree
							if (divs.length > 1)
							{
								divs[divs.length - 2].style.width = ow + 'px';
							}
						}
					}
					else
					{
						node.style.whiteSpace = 'nowrap';
					}
				}
				else if (sizeDiv.firstChild != null && sizeDiv.firstChild.nodeName == 'DIV')
				{
					sizeDiv = sizeDiv.firstChild;
				}

				this.offsetWidth = sizeDiv.offsetWidth + this.textWidthPadding;
				this.offsetHeight = sizeDiv.offsetHeight;
				
				ow = this.offsetWidth * this.scale;
				oh = this.offsetHeight * this.scale;
			}
		}

		if (ow != null && oh != null)
		{	
			this.boundingBox = new mxRectangle(this.bounds.x,
				this.bounds.y, ow, oh);
		}
	}

	if (this.boundingBox != null)
	{
		if (rot != 0)
		{
			// Accounts for pre-rotated x and y
			var bbox = mxUtils.getBoundingBox(new mxRectangle(
				this.margin.x * this.boundingBox.width,
				this.margin.y * this.boundingBox.height,
				this.boundingBox.width, this.boundingBox.height),
				rot, new mxPoint(0, 0));
			
			this.unrotatedBoundingBox = mxRectangle.fromRectangle(this.boundingBox);
			this.unrotatedBoundingBox.x += this.margin.x * this.unrotatedBoundingBox.width;
			this.unrotatedBoundingBox.y += this.margin.y * this.unrotatedBoundingBox.height;
			
			this.boundingBox.x += bbox.x;
			this.boundingBox.y += bbox.y;
			this.boundingBox.width = bbox.width;
			this.boundingBox.height = bbox.height;
		}
		else
		{
			this.boundingBox.x += this.margin.x * this.boundingBox.width;
			this.boundingBox.y += this.margin.y * this.boundingBox.height;
			this.unrotatedBoundingBox = null;
		}
	}

    // // 逻辑说明：
    // // 1. 克隆初始边界框（bounds）作为起点。
    // // 2. 根据溢出、裁剪和对齐设置，决定是否测量实际文本大小。
    // // 3. 在 SVG 环境中，尝试获取节点的边界框（getBBox）或使用 foreignObject 的子节点。
    // // 4. 在非 SVG 环境中，使用缓存的尺寸或临时 DIV（textDiv）测量文本。
    // // 5. 考虑旋转角度（rot），调整边界框的位置和尺寸。
    // // 特殊处理：
    // // - 处理空字符串或零尺寸边界框，设置为 null。
    // // - 在 IE8 标准模式下，添加额外的宽度内边距。
    // // - 支持换行（wrap）和裁剪（clipped）时的尺寸限制。
    // // 方法调用：updateFont、updateSize、updateInnerHtml 用于更新临时 DIV 的样式和内容。
};

/**
 * Function: getShapeRotation
 * 
 * Returns 0 to avoid using rotation in the canvas via updateTransform.
 *
 * // 函数：getShapeRotation
 * // 目的：返回形状的旋转角度。
 * // 返回值：始终返回 0，避免在画布变换中使用旋转。
 * // 说明：文本旋转由 getTextRotation 处理。
 */
mxText.prototype.getShapeRotation = function()
{
	return 0;
};

/**
 * Function: getTextRotation
 * 
 * Returns the rotation for the text label of the corresponding shape.
 *
 * // 函数：getTextRotation
 * // 目的：返回文本标签的旋转角度。
 * // 返回值：从状态对象（state.shape）获取旋转角度，若无则返回 0。
 * // 说明：确保文本旋转与形状保持一致。
 */
mxText.prototype.getTextRotation = function()
{
	return (this.state != null && this.state.shape != null) ? this.state.shape.getTextRotation() : 0;
};

/**
 * Function: isPaintBoundsInverted
 * 
 * Inverts the bounds if <mxShape.isBoundsInverted> returns true or if the
 * horizontal style is false.
 *
 * // 函数：isPaintBoundsInverted
 * // 目的：判断是否需要反转边界框。
 * // 返回值：如果父类边界反转或文本非水平显示，返回 true。
 * // 说明：用于处理垂直文本或特定单元格的边界反转。
 */
mxText.prototype.isPaintBoundsInverted = function()
{
	return !this.horizontal && this.state != null && this.state.view.graph.model.isVertex(this.state.cell);
};

/**
 * Function: configureCanvas
 * 
 * Sets the state of the canvas for drawing the shape.
 *
 * // 函数：configureCanvas
 * // 目的：为绘制形状配置画布状态。
 * // 参数：
 * // - c：画布对象。
 * // - x, y, w, h：缩放后的边界坐标和尺寸。
 * // 功能：设置画布的字体颜色、背景、边框、字体家族、大小和样式。
 * // 说明：调用父类的 configureCanvas 方法，并添加文本特定的样式设置。
 */
mxText.prototype.configureCanvas = function(c, x, y, w, h)
{
	mxShape.prototype.configureCanvas.apply(this, arguments);
	
	c.setFontColor(this.color);
	c.setFontBackgroundColor(this.background);
	c.setFontBorderColor(this.border);
	c.setFontFamily(this.family);
	c.setFontSize(this.size);
	c.setFontStyle(this.fontStyle);

    // // 样式设置：
    // // - 设置字体颜色（color）、背景（background）、边框（border）。
    // // - 设置字体家族（family）、大小（size）和样式（fontStyle，如粗体、斜体）。
};

/**
 * Function: updateVmlContainer
 * 
 * Sets the width and height of the container to 1px.
 *
 * // 函数：updateVmlContainer
 * // 目的：更新 VML 容器的样式。
 * // 功能：将容器的宽度和高度设置为 1px，并设置溢出为可见。
 * // 说明：确保 VML 渲染时容器大小最小化，内容可见。
 */
mxText.prototype.updateVmlContainer = function()
{
	this.node.style.left = Math.round(this.bounds.x) + 'px';
	this.node.style.top = Math.round(this.bounds.y) + 'px';
	this.node.style.width = '1px';
	this.node.style.height = '1px';
	this.node.style.overflow = 'visible';

    // // 样式设置：
    // // - left/top：设置容器位置为边界坐标。
    // // - width/height：固定为 1px。
    // // - overflow：设置为 visible，确保内容不被裁剪。
};

/**
 * Function: getHtmlValue
 * 
 * Private helper function to create SVG elements
 *
 * // 函数：getHtmlValue
 * // 目的：获取格式化的 HTML 文本值。
 * // 返回值：处理后的文本值，适用于 SVG 元素创建。
 * // 功能：对文本进行 HTML 编码，替换换行符，确保渲染正确。
 * // 说明：私有辅助函数，处理非严格 HTML 模式下的文本。
 */
mxText.prototype.getHtmlValue = function()
{
	var val = this.value;
	
	if (this.dialect != mxConstants.DIALECT_STRICTHTML)
	{
		val = mxUtils.htmlEntities(val, false);
	}
	
	// Handles trailing newlines to make sure they are visible in rendering output
	val = mxUtils.replaceTrailingNewlines(val, '<div><br></div>');
	val = (this.replaceLinefeeds) ? val.replace(/\n/g, '<br/>') : val;
	
	return val;

    // // 逻辑说明：
    // // 1. 如果非严格 HTML 模式，对文本进行 HTML 编码。
    // // 2. 替换末尾换行符为 <div><br></div>，确保可见。
    // // 3. 如果启用 replaceLinefeeds，将所有换行符替换为 <br/>。
};

/**
 * Function: getTextCss
 * 
 * Private helper function to create SVG elements
 *
 * // 函数：getTextCss
 * // 目的：生成文本的 CSS 样式字符串。
 * // 返回值：包含字体、颜色、对齐等样式的 CSS 字符串。
 * // 功能：根据文本属性生成 CSS，用于 SVG 渲染。
 * // 说明：私有辅助函数，支持粗体、斜体、下划线等样式。
 */
mxText.prototype.getTextCss = function()
{
	var lh = (mxConstants.ABSOLUTE_LINE_HEIGHT) ? (this.size * mxConstants.LINE_HEIGHT) + 'px' :
		mxConstants.LINE_HEIGHT;

	var css = 'display: inline-block; font-size: ' + this.size + 'px; ' +
		'font-family: ' + this.family + '; color: ' + this.color + '; line-height: ' + lh +
		'; pointer-events: ' + ((this.pointerEvents) ? 'all' : 'none') + '; ';

	if ((this.fontStyle & mxConstants.FONT_BOLD) == mxConstants.FONT_BOLD)
	{
		css += 'font-weight: bold; ';
	}

	if ((this.fontStyle & mxConstants.FONT_ITALIC) == mxConstants.FONT_ITALIC)
	{
		css += 'font-style: italic; ';
	}
	
	var deco = [];
	
	if ((this.fontStyle & mxConstants.FONT_UNDERLINE) == mxConstants.FONT_UNDERLINE)
	{
		deco.push('underline');
	}
	
	if ((this.fontStyle & mxConstants.FONT_STRIKETHROUGH) == mxConstants.FONT_STRIKETHROUGH)
	{
		deco.push('line-through');
	}
	
	if (deco.length > 0)
	{
		css += 'text-decoration: ' + deco.join(' ') + '; ';
	}

	return css;

    // // 样式设置：
    // // - display：inline-block，确保文本块正确显示。
    // // - font-size/family/color：设置字体大小、家族和颜色。
    // // - line-height：根据绝对行高或默认值设置行高。
    // // - pointer-events：根据 pointerEvents 属性控制交互。
    // // - font-weight/style：支持粗体和斜体。
    // // - text-decoration：支持下划线和删除线。
};

/**
 * Function: redrawHtmlShape
 *
 * Updates the HTML node(s) to reflect the latest bounds and scale.
 *
 * // 函数：redrawHtmlShape
 * // 目的：更新 HTML 节点以反映最新的边界和缩放比例。
 * // 功能：在 SVG 环境中使用 CSS3 渲染，否则使用 HTML 变换。
 * // 说明：重置节点样式，更新文本内容、字体和尺寸。
 */
mxText.prototype.redrawHtmlShape = function()
{
	if (mxClient.IS_SVG)
	{
		this.redrawHtmlShapeWithCss3();	
	}
	else
	{
		var style = this.node.style;
	
		// Resets CSS styles
		style.whiteSpace = 'normal';
		style.overflow = '';
		style.width = '';
		style.height = '';
		
		this.updateValue();
		this.updateFont(this.node);
		this.updateSize(this.node, (this.state == null || this.state.view.textDiv == null));
		
		this.offsetWidth = null;
		this.offsetHeight = null;
	
		if (mxClient.IS_IE && (document.documentMode == null || document.documentMode <= 8))
		{
			this.updateHtmlFilter();
		}
		else
		{
			this.updateHtmlTransform();
		}
	}

    // // 逻辑说明：
    // // 1. 如果是 SVG 环境，调用 redrawHtmlShapeWithCss3 使用 CSS3 渲染。
    // // 2. 否则，重置节点样式，更新文本内容（updateValue）、字体（updateFont）和尺寸（updateSize）。
    // // 3. 在 IE8 或更低版本中使用 HTML 过滤器（updateHtmlFilter），否则使用 HTML 变换（updateHtmlTransform）。
    // // 特殊处理：清除缓存的尺寸（offsetWidth/offsetHeight）。
};

/**
 * Function: redrawHtmlShapeWithCss3
 *
 * Updates the HTML node(s) to reflect the latest bounds and scale.
 *
 * // 函数：redrawHtmlShapeWithCss3
 * // 目的：使用 CSS3 更新 HTML 节点的边界和缩放。
 * // 功能：生成 CSS 样式，设置文本的对齐、背景、边框和变换。
 * // 说明：在 SVG 环境中使用 CSS3 实现高级渲染效果。
 */
mxText.prototype.redrawHtmlShapeWithCss3 = function()
{
	var w = Math.max(0, Math.round(this.bounds.width / this.scale));
	var h = Math.max(0, Math.round(this.bounds.height / this.scale));
	var flex = 'position: absolute; left: ' + Math.round(this.bounds.x) + 'px; ' +
		'top: ' + Math.round(this.bounds.y) + 'px; pointer-events: none; ';
	var block = this.getTextCss();
	
	mxSvgCanvas2D.createCss(w + 2, h, this.align, this.valign, this.wrap, this.overflow, this.clipped,
		(this.background != null) ? mxUtils.htmlEntities(this.background) : null,
		(this.border != null) ? mxUtils.htmlEntities(this.border) : null,
		flex, block, this.scale, mxUtils.bind(this, function(dx, dy, flex, item, block, ofl)
	{
		var r = this.getTextRotation();
		var tr = ((this.scale != 1) ? 'scale(' + this.scale + ') ' : '') +
			((r != 0) ? 'rotate(' + r + 'deg) ' : '') +
			((this.margin.x != 0 || this.margin.y != 0) ?
				'translate(' + (this.margin.x * 100) + '%,' +
					(this.margin.y * 100) + '%)' : '');
		
		if (tr != '')
		{
			tr = 'transform-origin: 0 0; transform: ' + tr + '; ';
		}

		if (ofl == '')
		{
			flex += item;
			item = 'display:inline-block; min-width: 100%; ' + tr;
		}
		else
		{
			item += tr;
			
			if (mxClient.IS_SF)
			{
				item += '-webkit-clip-path: content-box;';
			}
		}

		if (this.opacity < 100)
		{
			block += 'opacity: ' + (this.opacity / 100) + '; ';
		}
		
		this.node.setAttribute('style', flex);
		
		var html = (mxUtils.isNode(this.value)) ? this.value.outerHTML : this.getHtmlValue();
		
		if (this.node.firstChild == null)
		{
			this.node.innerHTML = '<div><div>' + html +'</div></div>';
		}

		this.node.firstChild.firstChild.setAttribute('style', block);
		this.node.firstChild.setAttribute('style', item);
	}));

    // // 样式设置：
    // // - flex：定位、禁用指针事件。
    // // - block：字体、颜色、行高、对齐等样式。
    // // - item：变换（缩放、旋转、平移）和裁剪路径。
    // // 交互逻辑：通过 CSS 变换实现旋转、缩放和定位。
    // // 特殊处理：Safari 浏览器的 -webkit-clip-path 支持内容裁剪。
};

/**
 * Function: updateHtmlTransform
 *
 * Returns the spacing as an <mxPoint>.
 *
 * // 函数：updateHtmlTransform
 * // 目的：更新 HTML 节点的变换属性。
 * // 功能：设置节点的缩放、旋转和定位样式。
 * // 说明：处理非 SVG 环境的 HTML 渲染。
 */
mxText.prototype.updateHtmlTransform = function()
{
	var theta = this.getTextRotation();
	var style = this.node.style;
	var dx = this.margin.x;
	var dy = this.margin.y;
	
	if (theta != 0)
	{
		mxUtils.setPrefixedStyle(style, 'transformOrigin', (-dx * 100) + '%' + ' ' + (-dy * 100) + '%');
		mxUtils.setPrefixedStyle(style, 'transform', 'translate(' + (dx * 100) + '%' + ',' + (dy * 100) + '%) ' +
			'scale(' + this.scale + ') rotate(' + theta + 'deg)');
	}
	else
	{
		mxUtils.setPrefixedStyle(style, 'transformOrigin', '0% 0%');
		mxUtils.setPrefixedStyle(style, 'transform', 'scale(' + this.scale + ') ' +
			'translate(' + (dx * 100) + '%' + ',' + (dy * 100) + '%)');
	}

	style.left = Math.round(this.bounds.x - Math.ceil(dx * ((this.overflow != 'fill' &&
		this.overflow != 'width') ? 3 : 1))) + 'px';
	style.top = Math.round(this.bounds.y - dy * ((this.overflow != 'fill') ? 3 : 1)) + 'px';
	
	if (this.opacity < 100)
	{
		style.opacity = this.opacity / 100;
	}
	else
	{
		style.opacity = '';
	}

    // // 样式设置：
    // // - transformOrigin：设置变换原点。
    // // - transform：应用缩放、平移和旋转。
    // // - left/top：调整节点位置，考虑外边距和溢出。
    // // - opacity：设置透明度，若为 100 则清除。
    // // 特殊处理：根据溢出模式调整定位偏移量。
};

/**
 * Function: updateInnerHtml
 * 
 * Sets the inner HTML of the given element to the <value>.
 *
 * // 函数：updateInnerHtml
 * // 目的：将元素的内部 HTML 设置为 <value>。
 * // 功能：处理文本或 DOM 节点的 HTML 内容，替换换行符。
 * // 说明：支持 HTML 编码和换行符替换。
 */
mxText.prototype.updateInnerHtml = function(elt)
{
	if (mxUtils.isNode(this.value))
	{
		elt.innerHTML = this.value.outerHTML;
	}
	else
	{
		var val = this.value;
		
		if (this.dialect != mxConstants.DIALECT_STRICTHTML)
		{
			// LATER: Can be cached in updateValue
			val = mxUtils.htmlEntities(val, false);
		}
		
		// Handles trailing newlines to make sure they are visible in rendering output
		val = mxUtils.replaceTrailingNewlines(val, '<div>&nbsp;</div>');
		val = (this.replaceLinefeeds) ? val.replace(/\n/g, '<br/>') : val;
		val = '<div style="display:inline-block;_display:inline;">' + val + '</div>';
		
		elt.innerHTML = val;
	}

    // // 逻辑说明：
    // // 1. 如果 value 是 DOM 节点，直接设置其 outerHTML。
    // // 2. 否则，处理文本值的 HTML 编码和换行符替换。
    // // 3. 确保内容以内联块显示。
};

/**
 * Function: updateHtmlFilter
 *
 * Rotated text rendering quality is bad for IE9 quirks/IE8 standards
 *
 * // 函数：updateHtmlFilter
 * // 目的：更新 IE8/9 怪异模式的 HTML 过滤器。
 * // 功能：处理文本的缩放、旋转和定位，确保兼容性。
 * // 说明：针对 IE8/9 的特殊渲染逻辑。
 * // 特殊处理：模拟 CSS max-height/max-width，调整偏移量。
 */
mxText.prototype.updateHtmlFilter = function()
{
	var style = this.node.style;
	var dx = this.margin.x;
	var dy = this.margin.y;
	var s = this.scale;
	
	// Resets filter before getting offsetWidth
	mxUtils.setOpacity(this.node, this.opacity);
	
	// Adds 1 to match table height in 1.x
	var ow = 0;
	var oh = 0;
	var td = (this.state != null) ? this.state.view.textDiv : null;
	var sizeDiv = this.node;
	
	// Fallback for hidden text rendering in IE quirks mode
	if (td != null)
	{
		td.style.overflow = '';
		td.style.height = '';
		td.style.width = '';
		
		this.updateFont(td);
		this.updateSize(td, false);
		this.updateInnerHtml(td);
		
		var w = Math.round(this.bounds.width / this.scale);

		if (this.wrap && w > 0)
		{
			td.style.whiteSpace = 'normal';
			td.style.wordWrap = mxConstants.WORD_WRAP;
			ow = w;
			
			if (this.clipped)
			{
				ow = Math.min(ow, this.bounds.width);
			}

			td.style.width = ow + 'px';
		}
		else
		{
			td.style.whiteSpace = 'nowrap';
		}
		
		sizeDiv = td;
		
		if (sizeDiv.firstChild != null && sizeDiv.firstChild.nodeName == 'DIV')
		{
			sizeDiv = sizeDiv.firstChild;
			
			if (this.wrap && td.style.wordWrap == 'break-word')
			{
				sizeDiv.style.width = '100%';
			}
		}

		// Required to update the height of the text box after wrapping width is known 
		if (!this.clipped && this.wrap && w > 0)
		{
			ow = sizeDiv.offsetWidth + this.textWidthPadding;
			td.style.width = ow + 'px';
		}
		
		oh = sizeDiv.offsetHeight + 2;
		
		if (mxClient.IS_QUIRKS && this.border != null && this.border != mxConstants.NONE)
		{
			oh += 3;
		}
	}
	else if (sizeDiv.firstChild != null && sizeDiv.firstChild.nodeName == 'DIV')
	{
		sizeDiv = sizeDiv.firstChild;
		oh = sizeDiv.offsetHeight;
	}

	ow = sizeDiv.offsetWidth + this.textWidthPadding;
	
	if (this.clipped)
	{
		oh = Math.min(oh, this.bounds.height);
	}

	var w = this.bounds.width / s;
	var h = this.bounds.height / s;

	// Handles special case for live preview with no wrapper DIV and no textDiv
	if (this.overflow == 'fill')
	{
		oh = h;
		ow = w;
	}
	else if (this.overflow == 'width')
	{
		oh = sizeDiv.scrollHeight;
		ow = w;
	}
	
	// Stores for later use
	this.offsetWidth = ow;
	this.offsetHeight = oh;
	
	// Simulates max-height CSS in quirks mode
	if (mxClient.IS_QUIRKS && (this.clipped || (this.overflow == 'width' && h > 0)))
	{
		h = Math.min(h, oh);
		style.height = Math.round(h) + 'px';
	}
	else
	{
		h = oh;
	}

	if (this.overflow != 'fill' && this.overflow != 'width')
	{
		if (this.clipped)
		{
			ow = Math.min(w, ow);
		}
		
		w = ow;

		// Simulates max-width CSS in quirks mode
		if ((mxClient.IS_QUIRKS && this.clipped) || this.wrap)
		{
			style.width = Math.round(w) + 'px';
		}
	}

	h *= s;
	w *= s;
	
	// Rotation case is handled via VML canvas
	var rad = this.getTextRotation() * (Math.PI / 180);
	
	// Precalculate cos and sin for the rotation
	var real_cos = parseFloat(parseFloat(Math.cos(rad)).toFixed(8));
	var real_sin = parseFloat(parseFloat(Math.sin(-rad)).toFixed(8));

	rad %= 2 * Math.PI;
	
	if (rad < 0)
	{
		rad += 2 * Math.PI;
	}
	
	rad %= Math.PI;
	
	if (rad > Math.PI / 2)
	{
		rad = Math.PI - rad;
	}
	
	var cos = Math.cos(rad);
	var sin = Math.sin(-rad);

	var tx = w * -(dx + 0.5);
	var ty = h * -(dy + 0.5);

	var top_fix = (h - h * cos + w * sin) / 2 + real_sin * tx - real_cos * ty;
	var left_fix = (w - w * cos + h * sin) / 2 - real_cos * tx - real_sin * ty;
	
	if (rad != 0)
	{
		var f = 'progid:DXImageTransform.Microsoft.Matrix(M11=' + real_cos + ', M12='+
			real_sin + ', M21=' + (-real_sin) + ', M22=' + real_cos + ', sizingMethod=\'auto expand\')';
		
		if (style.filter != null && style.filter.length > 0)
		{
			style.filter += ' ' + f;
		}
		else
		{
			style.filter = f;
		}
	}
	
	// Workaround for rendering offsets
	var dy = 0;
	
	if (this.overflow != 'fill' && mxClient.IS_QUIRKS)
	{
		if (this.valign == mxConstants.ALIGN_TOP)
		{
			dy -= 1;
		}
		else if (this.valign == mxConstants.ALIGN_BOTTOM)
		{
			dy += 2;
		}
		else
		{
			dy += 1;
		}
	}

	style.zoom = s;
	style.left = Math.round(this.bounds.x + left_fix - w / 2) + 'px';
	style.top = Math.round(this.bounds.y + top_fix - h / 2 + dy) + 'px';

    // // 逻辑说明：
    // // 1. 重置透明度和过滤器，获取节点尺寸。
    // // 2. 使用临时 DIV（textDiv）或当前节点测量文本尺寸。
    // // 3. 处理换行和裁剪，调整宽度和高度。
    // // 4. 应用旋转变换（Microsoft Matrix 过滤器）。
    // // 5. 调整位置（left/top），考虑外边距和旋转偏移。
    // // 特殊处理：
    // // - IE 怪异模式下模拟 max-height/max-width。
    // // - 针对不同溢出模式（fill/width）调整尺寸。
    // // - 边界和边框的额外高度处理。
};

/**
 * Function: updateValue
 *
 * Updates the HTML node(s) to reflect the latest bounds and scale.
 *
 * // 函数：updateValue
 * // 目的：更新 HTML 节点的文本内容。
 * // 功能：根据溢出、背景和边框设置，格式化并设置节点内容。
 * // 说明：处理 DOM 节点或文本值的 HTML 编码和样式。
 */
mxText.prototype.updateValue = function()
{
	if (mxUtils.isNode(this.value))
	{
		this.node.innerHTML = '';
		this.node.appendChild(this.value);
	}
	else
	{
		var val = this.value;
		
		if (this.dialect != mxConstants.DIALECT_STRICTHTML)
		{
			val = mxUtils.htmlEntities(val, false);
		}
		
		// Handles trailing newlines to make sure they are visible in rendering output
		val = mxUtils.replaceTrailingNewlines(val, '<div><br></div>');
		val = (this.replaceLinefeeds) ? val.replace(/\n/g, '<br/>') : val;
		var bg = (this.background != null && this.background != mxConstants.NONE) ? this.background : null;
		var bd = (this.border != null && this.border != mxConstants.NONE) ? this.border : null;

		if (this.overflow == 'fill' || this.overflow == 'width')
		{
			if (bg != null)
			{
				this.node.style.backgroundColor = bg;
			}
			
			if (bd != null)
			{
				this.node.style.border = '1px solid ' + bd;
			}
		}
		else
		{
			var css = '';
			
			if (bg != null)
			{
				css += 'background-color:' + mxUtils.htmlEntities(bg) + ';';
			}
			
			if (bd != null)
			{
				css += 'border:1px solid ' + mxUtils.htmlEntities(bd) + ';';
			}
			
			// Wrapper DIV for background, zoom needed for inline in quirks
			// and to measure wrapped font sizes in all browsers
			// FIXME: Background size in quirks mode for wrapped text
			var lh = (mxConstants.ABSOLUTE_LINE_HEIGHT) ? (this.size * mxConstants.LINE_HEIGHT) + 'px' :
				mxConstants.LINE_HEIGHT;
			val = '<div style="zoom:1;' + css + 'display:inline-block;_display:inline;text-decoration:inherit;' +
				'padding-bottom:1px;padding-right:1px;line-height:' + lh + '">' + val + '</div>';
		}

		this.node.innerHTML = val;
		
		// Sets text direction
		var divs = this.node.getElementsByTagName('div');
		
		if (divs.length > 0)
		{
			var dir = this.textDirection;

			if (dir == mxConstants.TEXT_DIRECTION_AUTO && this.dialect != mxConstants.DIALECT_STRICTHTML)
			{
				dir = this.getAutoDirection();
			}
			
			if (dir == mxConstants.TEXT_DIRECTION_LTR || dir == mxConstants.TEXT_DIRECTION_RTL)
			{
				divs[divs.length - 1].setAttribute('dir', dir);
			}
			else
			{
				divs[divs.length - 1].removeAttribute('dir');
			}
		}
	}

    // // 逻辑说明：
    // // 1. 如果 value 是 DOM 节点，清空并追加节点。
    // // 2. 否则，处理文本的 HTML 编码、换行符替换和样式。
    // // 3. 根据溢出模式（fill/width）设置背景和边框。
    // // 4. 设置文本方向（dir 属性），支持自动检测。
    // // 样式设置：
    // // - backgroundColor/border：应用背景和边框样式。
    // // - zoom：处理怪异模式下的缩放。
    // // - line-height：设置行高。
};

/**
 * Function: updateFont
 *
 * Updates the HTML node(s) to reflect the latest bounds and scale.
 *
 * // 函数：updateFont
 * // 目的：更新 HTML 节点的字体样式。
 * // 功能：设置字体大小、家族、颜色、粗体、斜体等样式。
 * // 说明：根据文本属性更新节点的 CSS 样式。
 */
mxText.prototype.updateFont = function(node)
{
	var style = node.style;
	
	style.lineHeight = (mxConstants.ABSOLUTE_LINE_HEIGHT) ? (this.size * mxConstants.LINE_HEIGHT) + 'px' : mxConstants.LINE_HEIGHT;
	style.fontSize = this.size + 'px';
	style.fontFamily = this.family;
	style.verticalAlign = 'top';
	style.color = this.color;
	
	if ((this.fontStyle & mxConstants.FONT_BOLD) == mxConstants.FONT_BOLD)
	{
		style.fontWeight = 'bold';
	}
	else
	{
		style.fontWeight = '';
	}

	if ((this.fontStyle & mxConstants.FONT_ITALIC) == mxConstants.FONT_ITALIC)
	{
		style.fontStyle = 'italic';
	}
	else
	{
		style.fontStyle = '';
	}
	
	var txtDecor = [];
	
	if ((this.fontStyle & mxConstants.FONT_UNDERLINE) == mxConstants.FONT_UNDERLINE)
	{
		txtDecor.push('underline');
	}
	
	if ((this.fontStyle & mxConstants.FONT_STRIKETHROUGH) == mxConstants.FONT_STRIKETHROUGH)
	{
		txtDecor.push('line-through');
	}
	
	style.textDecoration = txtDecor.join(' ');
	
	if (this.align == mxConstants.ALIGN_CENTER)
	{
		style.textAlign = 'center';
	}
	else if (this.align == mxConstants.ALIGN_RIGHT)
	{
		style.textAlign = 'right';
	}
	else
	{
		style.textAlign = 'left';
	}

    // // 样式设置：
    // // - lineHeight：设置行高（绝对或相对）。
    // // - fontSize/family/color：设置字体大小、家族和颜色。
    // // - fontWeight/style：支持粗体和斜体。
    // // - textDecoration：支持下划线和删除线。
    // // - textAlign：设置文本水平对齐（居中、右对齐或左对齐）。
};

/**
 * Function: updateSize
 *
 * Updates the HTML node(s) to reflect the latest bounds and scale.
 *
 * // 函数：updateSize
 * // 目的：更新 HTML 节点的尺寸。
 * // 功能：根据边界、缩放和溢出模式设置节点的宽度和高度。
 * // 说明：支持裁剪、填充和宽度溢出模式。
 */
mxText.prototype.updateSize = function(node, enableWrap)
{
	var w = Math.max(0, Math.round(this.bounds.width / this.scale));
	var h = Math.max(0, Math.round(this.bounds.height / this.scale));
	var style = node.style;
	
	// NOTE: Do not use maxWidth here because wrapping will
	// go wrong if the cell is outside of the viewable area
	if (this.clipped)
	{
		style.overflow = 'hidden';
		
		if (!mxClient.IS_QUIRKS)
		{
			style.maxHeight = h + 'px';
			style.maxWidth = w + 'px';
		}
		else
		{
			style.width = w + 'px';
		}
	}
	else if (this.overflow == 'fill')
	{
		style.width = (w + 1) + 'px';
		style.height = (h + 1) + 'px';
		style.overflow = 'hidden';
	}
	else if (this.overflow == 'width')
	{
		style.width = (w + 1) + 'px';
		style.maxHeight = (h + 1) + 'px';
		style.overflow = 'hidden';
	}
	
	if (this.wrap && w > 0)
	{
		style.wordWrap = mxConstants.WORD_WRAP;
		style.whiteSpace = 'normal';
		style.width = w + 'px';

		if (enableWrap && this.overflow != 'fill' && this.overflow != 'width')
		{
			var sizeDiv = node;
			
			if (sizeDiv.firstChild != null && sizeDiv.firstChild.nodeName == 'DIV')
			{
				sizeDiv = sizeDiv.firstChild;
				
				if (node.style.wordWrap == 'break-word')
				{
					sizeDiv.style.width = '100%';
				}
			}
			
			var tmp = sizeDiv.offsetWidth;
			
			// Workaround for text measuring in hidden containers
			if (tmp == 0)
			{
				var prev = node.parentNode;
				node.style.visibility = 'hidden';
				document.body.appendChild(node);
				tmp = sizeDiv.offsetWidth;
				node.style.visibility = '';
				prev.appendChild(node);
			}

			tmp += 3;
			
			if (this.clipped)
			{
				tmp = Math.min(tmp, w);
			}
			
			style.width = tmp + 'px';
		}
	}
	else
	{
		style.whiteSpace = 'nowrap';
	}

    // // 样式设置：
    // // - overflow：根据裁剪或溢出模式设置隐藏或可见。
    // // - maxHeight/maxWidth：非怪异模式下设置最大高度和宽度。
    // // - width/height：根据溢出模式（fill/width）设置尺寸。
    // // - wordWrap/whiteSpace：支持换行（normal）或不换行（nowrap）。
    // // 特殊处理：
    // // - 隐藏容器中的文本测量，通过临时添加到文档体。
    // // - 裁剪时限制宽度到边界值。
};

/**
 * Function: getMargin
 *
 * Returns the spacing as an <mxPoint>.
 *
 * // 函数：getMargin
 * // 目的：返回外边距。
 * // 返回值：<mxPoint> 对象，表示水平和垂直外边距。
 * // 功能：根据对齐方式计算外边距。
 */
mxText.prototype.updateMargin = function()
{
	this.margin = mxUtils.getAlignmentAsPoint(this.align, this.valign);

    // // 逻辑说明：
    // // - 使用 mxUtils.getAlignmentAsPoint 根据水平和垂直对齐方式计算外边距。
};

/**
 * Function: getSpacing
 *
 * Returns the spacing as an <mxPoint>.
 *
 * // 函数：getSpacing
 * // 目的：返回间距。
 * // 返回值：<mxPoint> 对象，表示水平和垂直间距。
 * // 功能：根据对齐方式和间距属性计算间距。
 */
mxText.prototype.getSpacing = function()
{
	var dx = 0;
	var dy = 0;

	if (this.align == mxConstants.ALIGN_CENTER)
	{
		dx = (this.spacingLeft - this.spacingRight) / 2;
	}
	else if (this.align == mxConstants.ALIGN_RIGHT)
	{
		dx = -this.spacingRight - this.baseSpacingRight;
	}
	else
	{
		dx = this.spacingLeft + this.baseSpacingLeft;
	}

	if (this.valign == mxConstants.ALIGN_MIDDLE)
	{
		dy = (this.spacingTop - this.spacingBottom) / 2;
	}
	else if (this.valign == mxConstants.ALIGN_BOTTOM)
	{
		dy = -this.spacingBottom - this.baseSpacingBottom;
	}
	else
	{
		dy = this.spacingTop + this.baseSpacingTop;
	}
	
	return new mxPoint(dx, dy);

    // // 逻辑说明：
    // // 1. 水平间距（dx）：根据对齐方式（居中、右、左）计算。
    // // 2. 垂直间距（dy）：根据垂直对齐方式（居中、下、上）计算。
    // // 3. 考虑基础间距（baseSpacing*）和指定间距（spacing*）。
};
