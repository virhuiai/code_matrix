/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxRectangleShape
 *
 * Extends <mxShape> to implement a rectangle shape.
 * This shape is registered under <mxConstants.SHAPE_RECTANGLE>
 * in <mxCellRenderer>.
 * 
 * Constructor: mxRectangleShape
 *
 * Constructs a new rectangle shape.
 * 
 * Parameters:
 * 
 * bounds - <mxRectangle> that defines the bounds. This is stored in
 * <mxShape.bounds>.
 * fill - String that defines the fill color. This is stored in <fill>.
 * stroke - String that defines the stroke color. This is stored in <stroke>.
 * strokewidth - Optional integer that defines the stroke width. Default is
 * 1. This is stored in <strokewidth>.
 */
// 中文注释：
// 类：mxRectangleShape
// 扩展 mxShape 类，用于实现矩形形状。
// 该形状在 mxCellRenderer 中以 mxConstants.SHAPE_RECTANGLE 注册。
// 构造函数：mxRectangleShape
// 创建一个新的矩形形状对象。
// 参数说明：
// - bounds: mxRectangle 类型，定义矩形的边界，存储在 mxShape.bounds 中。
// - fill: 字符串，定义填充颜色，存储在 fill 属性中。
// - stroke: 字符串，定义描边颜色，存储在 stroke 属性中。
// - strokewidth: 可选整数，定义描边宽度，默认为 1，存储在 strokewidth 属性中。
function mxRectangleShape(bounds, fill, stroke, strokewidth)
{
	mxShape.call(this);
	this.bounds = bounds;
	this.fill = fill;
	this.stroke = stroke;
	this.strokewidth = (strokewidth != null) ? strokewidth : 1;
};

/**
 * Extends mxShape.
 */
// 中文注释：
// 使用 mxUtils.extend 方法扩展 mxShape 类，使 mxRectangleShape 继承 mxShape 的功能。
mxUtils.extend(mxRectangleShape, mxShape);

/**
 * Function: isHtmlAllowed
 *
 * Returns true for non-rounded, non-rotated shapes with no glass gradient.
 */
// 中文注释：
// 函数：isHtmlAllowed
// 用途：判断是否允许使用 HTML 渲染。
// 逻辑：当形状非圆角、非旋转且无玻璃渐变效果时返回 true。
// 事件处理：检查样式中的 mxConstants.STYLE_POINTER_EVENTS 是否为 '1'，决定是否启用事件。
// 返回值：true 表示允许 HTML 渲染，false 表示不允许。
// 特殊处理：仅当形状非圆角、无玻璃效果、无旋转，且填充色非空或描边非空时，返回 true。
mxRectangleShape.prototype.isHtmlAllowed = function()
{
	var events = true;
	
	if (this.style != null)
	{
		events = mxUtils.getValue(this.style, mxConstants.STYLE_POINTER_EVENTS, '1') == '1';		
	}
	
	return !this.isRounded && !this.glass && this.rotation == 0 && (events ||
		(this.fill != null && this.fill != mxConstants.NONE));
};

/**
 * Function: paintBackground
 * 
 * Generic background painting implementation.
 */
// 中文注释：
// 函数：paintBackground
// 用途：实现矩形的背景绘制。
// 参数说明：
// - c: 画布上下文，用于绘制。
// - x, y: 矩形左上角的坐标。
// - w, h: 矩形的宽度和高度。
// 事件处理逻辑：检查样式中的 mxConstants.STYLE_POINTER_EVENTS，若为 '1' 则启用事件。
// 关键逻辑：
// 1. 根据事件、填充色和描边色决定是否绘制。
// 2. 如果是圆角矩形，计算圆角半径并调用 roundrect 方法绘制圆角矩形。
// 3. 否则调用 rect 方法绘制普通矩形。
// 4. 最后调用 fillAndStroke 方法填充和描边。
// 重要配置参数：
// - mxConstants.STYLE_ABSOLUTE_ARCSIZE: 控制是否使用绝对圆角大小。
// - mxConstants.STYLE_ARCSIZE: 定义圆角大小，默认为 mxConstants.LINE_ARCSIZE。
// - mxConstants.RECTANGLE_ROUNDING_FACTOR: 圆角比例因子，默认为 100。
// 特殊处理：当事件禁用且无填充色时，禁用画布的 pointerEvents。
mxRectangleShape.prototype.paintBackground = function(c, x, y, w, h)
{
	var events = true;
	
	if (this.style != null)
	{
		events = mxUtils.getValue(this.style, mxConstants.STYLE_POINTER_EVENTS, '1') == '1';
	}
	
	if (events || (this.fill != null && this.fill != mxConstants.NONE) ||
		(this.stroke != null && this.stroke != mxConstants.NONE))
	{
		if (!events && (this.fill == null || this.fill == mxConstants.NONE))
		{
			c.pointerEvents = false;
		}
		
		if (this.isRounded)
		{
			var r = 0;
			
			if (mxUtils.getValue(this.style, mxConstants.STYLE_ABSOLUTE_ARCSIZE, 0) == '1')
			{
				r = Math.min(w / 2, Math.min(h / 2, mxUtils.getValue(this.style,
					mxConstants.STYLE_ARCSIZE, mxConstants.LINE_ARCSIZE) / 2));
                // 中文注释：
                // 计算圆角半径 r，取宽度和高度的一半与样式中定义的圆角大小的最小值。
                // mxConstants.STYLE_ABSOLUTE_ARCSIZE 为 '1' 时使用绝对圆角大小。
			}
			else
			{
				var f = mxUtils.getValue(this.style, mxConstants.STYLE_ARCSIZE,
					mxConstants.RECTANGLE_ROUNDING_FACTOR * 100) / 100;
				r = Math.min(w * f, h * f);
                // 中文注释：
                // 使用相对圆角大小，基于 mxConstants.RECTANGLE_ROUNDING_FACTOR 计算比例因子 f。
                // r 为宽度和高度乘以比例因子 f 的最小值。
			}
			
			c.roundrect(x, y, w, h, r, r);
            // 中文注释：
            // 调用画布的 roundrect 方法绘制圆角矩形，参数为坐标、宽高和圆角半径。
		}
		else
		{
			c.rect(x, y, w, h);
            // 中文注释：
            // 调用画布的 rect 方法绘制普通矩形，参数为坐标和宽高。
		}
			
		c.fillAndStroke();
        // 中文注释：
        // 调用 fillAndStroke 方法，根据填充色和描边色完成矩形的填充和描边。
	}
};

/**
 * Function: isRoundable
 * 
 * Adds roundable support.
 */
// 中文注释：
// 函数：isRoundable
// 用途：判断矩形是否支持圆角。
// 返回值：始终返回 true，表示支持圆角。
mxRectangleShape.prototype.isRoundable = function(c, x, y, w, h)
{
	return true;
};

/**
 * Function: paintForeground
 * 
 * Generic background painting implementation.
 */
// 中文注释：
// 函数：paintForeground
// 用途：实现矩形的前景绘制，主要是玻璃效果。
// 参数说明：
// - c: 画布上下文，用于绘制。
// - x, y: 矩形左上角的坐标。
// - w, h: 矩形的宽度和高度。
// 交互逻辑：当启用玻璃效果（glass 为 true）、非轮廓模式（outline 为 false）且填充色非空时，绘制玻璃效果。
// 关键步骤：调用 paintGlassEffect 方法，传入矩形坐标、宽高和基于描边宽度的圆角大小。
// 特殊处理：仅在满足玻璃效果条件时执行绘制。
mxRectangleShape.prototype.paintForeground = function(c, x, y, w, h)
{
	if (this.glass && !this.outline && this.fill != null && this.fill != mxConstants.NONE)
	{
		this.paintGlassEffect(c, x, y, w, h, this.getArcSize(w + this.strokewidth, h + this.strokewidth));
        // 中文注释：
        // 调用 paintGlassEffect 方法绘制玻璃效果，圆角大小基于矩形宽高和描边宽度计算。
	}
};
