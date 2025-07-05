/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxSwimlane
 *
 * Extends <mxShape> to implement a swimlane shape. This shape is registered
 * under <mxConstants.SHAPE_SWIMLANE> in <mxCellRenderer>. Use the
 * <mxConstants.STYLE_STYLE_STARTSIZE> to define the size of the title
 * region, <mxConstants.STYLE_SWIMLANE_FILLCOLOR> for the content area fill,
 * <mxConstants.STYLE_SEPARATORCOLOR> to draw an additional vertical separator
 * and <mxConstants.STYLE_SWIMLANE_LINE> to hide the line between the title
 * region and the content area. The <mxConstants.STYLE_HORIZONTAL> affects
 * the orientation of this shape, not only its label.
 * 
 * Constructor: mxSwimlane
 *
 * Constructs a new swimlane shape.
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
function mxSwimlane(bounds, fill, stroke, strokewidth)
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
mxUtils.extend(mxSwimlane, mxShape);

/**
 * Variable: imageSize
 *
 * Default imagewidth and imageheight if an image but no imagewidth
 * and imageheight are defined in the style. Value is 16.
 */
mxSwimlane.prototype.imageSize = 16;

/**
 * Function: isRoundable
 * 
 * Adds roundable support.
 */
mxSwimlane.prototype.isRoundable = function(c, x, y, w, h)
{
	return true;
};

/**
 * Function: getTitleSize
 * 
 * Returns the title size.
 */
mxSwimlane.prototype.getTitleSize = function()
{
	return Math.max(0, mxUtils.getValue(this.style, mxConstants.STYLE_STARTSIZE, mxConstants.DEFAULT_STARTSIZE));
};

/**
 * Function: getLabelBounds
 * 
 * Returns the bounding box for the label.
 */
mxSwimlane.prototype.getLabelBounds = function(rect)
{
	var start = this.getTitleSize();
	var bounds = new mxRectangle(rect.x, rect.y, rect.width, rect.height);
	var horizontal = this.isHorizontal();
	
	var flipH = mxUtils.getValue(this.style, mxConstants.STYLE_FLIPH, 0) == 1;
	var flipV = mxUtils.getValue(this.style, mxConstants.STYLE_FLIPV, 0) == 1;	
	
	// East is default
	var shapeVertical = (this.direction == mxConstants.DIRECTION_NORTH ||
			this.direction == mxConstants.DIRECTION_SOUTH);
	var realHorizontal = horizontal == !shapeVertical;
	
	var realFlipH = !realHorizontal && flipH != (this.direction == mxConstants.DIRECTION_SOUTH ||
			this.direction == mxConstants.DIRECTION_WEST);
	var realFlipV = realHorizontal && flipV != (this.direction == mxConstants.DIRECTION_SOUTH ||
			this.direction == mxConstants.DIRECTION_WEST);

	// Shape is horizontal
	if (!shapeVertical)
	{
		var tmp = Math.min(bounds.height, start * this.scale);

		if (realFlipH || realFlipV)
		{
			bounds.y += bounds.height - tmp;
		}

		bounds.height = tmp;
	}
	else
	{
		var tmp = Math.min(bounds.width, start * this.scale);
		
		if (realFlipH || realFlipV)
		{
			bounds.x += bounds.width - tmp;	
		}

		bounds.width = tmp;
	}
	
	return bounds;
};

/**
 * Function: getGradientBounds
 * 
 * Returns the bounding box for the gradient box for this shape.
 */
mxSwimlane.prototype.getGradientBounds = function(c, x, y, w, h)
{
	var start = this.getTitleSize();
	
	if (this.isHorizontal())
	{
		start = Math.min(start, h);
		return new mxRectangle(x, y, w, start);
	}
	else
	{
		start = Math.min(start, w);
		return new mxRectangle(x, y, start, h);
	}
};

/**
 * Function: getSwimlaneArcSize
 * 
 * Returns the arcsize for the swimlane.
 */
mxSwimlane.prototype.getSwimlaneArcSize = function(w, h, start)
{
	if (mxUtils.getValue(this.style, mxConstants.STYLE_ABSOLUTE_ARCSIZE, 0) == '1')
	{
		return Math.min(w / 2, Math.min(h / 2, mxUtils.getValue(this.style,
			mxConstants.STYLE_ARCSIZE, mxConstants.LINE_ARCSIZE) / 2));
	}
	else
	{
		var f = mxUtils.getValue(this.style, mxConstants.STYLE_ARCSIZE, mxConstants.RECTANGLE_ROUNDING_FACTOR * 100) / 100;

		return start * f * 3; 
	}
};

/**
 * Function: isHorizontal
 *
 * Paints the swimlane vertex shape.
 */
mxSwimlane.prototype.isHorizontal = function()
{
	return mxUtils.getValue(this.style, mxConstants.STYLE_HORIZONTAL, 1) == 1;
};

/**
 * Function: paintVertexShape
 *
 * Paints the swimlane vertex shape.
 */
// 中文注释：
// 方法：paintVertexShape
// 功能：绘制泳道顶点形状。
// 参数：c - 画布对象；x, y - 坐标；w, h - 宽度和高度。
// 关键步骤：
// 1. 获取标题区域大小、填充颜色（STYLE_SWIMLANE_FILLCOLOR）、分隔线设置（STYLE_SWIMLANE_LINE）。
// 2. 根据是否启用圆角（isRounded），调用普通绘制（paintSwimlane）或圆角绘制（paintRoundedSwimlane）。
// 3. 绘制分隔线（STYLE_SEPARATORCOLOR）。
// 4. 若存在图像，绘制图像。
// 5. 若启用玻璃效果（glass），绘制玻璃效果。
// 重要配置参数：
// - STYLE_SWIMLANE_FILLCOLOR：内容区域填充颜色。
// - STYLE_SWIMLANE_LINE：标题与内容区域之间的分隔线是否显示。
// - STYLE_SEPARATORCOLOR：额外分隔线的颜色。
// 交互逻辑：根据配置动态调整绘制行为，支持圆角、图像和玻璃效果。
// 特殊处理：确保标题区域大小不超过形状的宽度或高度。
mxSwimlane.prototype.paintVertexShape = function(c, x, y, w, h)
{
	var start = this.getTitleSize();
	var fill = mxUtils.getValue(this.style, mxConstants.STYLE_SWIMLANE_FILLCOLOR, mxConstants.NONE);
	var swimlaneLine = mxUtils.getValue(this.style, mxConstants.STYLE_SWIMLANE_LINE, 1) == 1;
	var r = 0;
	
	if (this.isHorizontal())
	{
		start = Math.min(start, h);
	}
	else
	{
		start = Math.min(start, w);
	}
	
	c.translate(x, y);
	
	if (!this.isRounded)
	{
		this.paintSwimlane(c, x, y, w, h, start, fill, swimlaneLine);
	}
	else
	{
		r = this.getSwimlaneArcSize(w, h, start);
		r = Math.min(((this.isHorizontal()) ? h : w) - start, Math.min(start, r));
		this.paintRoundedSwimlane(c, x, y, w, h, start, r, fill, swimlaneLine);
	}
	
	var sep = mxUtils.getValue(this.style, mxConstants.STYLE_SEPARATORCOLOR, mxConstants.NONE);
	this.paintSeparator(c, x, y, w, h, start, sep);

	if (this.image != null)
	{
		var bounds = this.getImageBounds(x, y, w, h);
		c.image(bounds.x - x, bounds.y - y, bounds.width, bounds.height,
				this.image, false, false, false);
	}
	
	if (this.glass)
	{
		c.setShadow(false);
		this.paintGlassEffect(c, 0, 0, w, start, r);
	}
};

/**
 * Function: paintSwimlane
 *
 * Paints the swimlane vertex shape.
 */
// 中文注释：
// 方法：paintSwimlane
// 功能：绘制非圆角泳道顶点形状。
// 参数：c - 画布对象；x, y - 坐标；w, h - 宽度和高度；start - 标题区域大小；fill - 填充颜色；swimlaneLine - 是否绘制分隔线。
// 关键步骤：
// 1. 检查是否启用指针事件（STYLE_POINTER_EVENTS）。
// 2. 根据方向（水平或垂直）绘制标题区域和内容区域。
// 3. 若填充颜色为 NONE 或禁用事件，关闭指针事件。
// 4. 若启用分隔线，调用 paintDivider 绘制。
// 事件处理逻辑：根据 STYLE_POINTER_EVENTS 控制是否响应指针事件。
// 特殊处理：确保填充颜色和事件设置正确影响交互行为。
mxSwimlane.prototype.paintSwimlane = function(c, x, y, w, h, start, fill, swimlaneLine)
{
	c.begin();
	
	var events = true;
	
	if (this.style != null)
	{
		events = mxUtils.getValue(this.style, mxConstants.STYLE_POINTER_EVENTS, '1') == '1';
	}
	
	if (!events && (this.fill == null || this.fill == mxConstants.NONE))
	{
		c.pointerEvents = false;
	}
	
	if (this.isHorizontal())
	{
		c.moveTo(0, start);
		c.lineTo(0, 0);
		c.lineTo(w, 0);
		c.lineTo(w, start);
		c.fillAndStroke();

		if (start < h)
		{
			if (fill == mxConstants.NONE || !events)
			{
				c.pointerEvents = false;
			}
			
			if (fill != mxConstants.NONE)
			{
				c.setFillColor(fill);
			}
			
			c.begin();
			c.moveTo(0, start);
			c.lineTo(0, h);
			c.lineTo(w, h);
			c.lineTo(w, start);
			
			if (fill == mxConstants.NONE)
			{
				c.stroke();
			}
			else
			{
				c.fillAndStroke();
			}
		}
	}
	else
	{
		c.moveTo(start, 0);
		c.lineTo(0, 0);
		c.lineTo(0, h);
		c.lineTo(start, h);
		c.fillAndStroke();
		
		if (start < w)
		{
			if (fill == mxConstants.NONE || !events)
			{
				c.pointerEvents = false;
			}
			
			if (fill != mxConstants.NONE)
			{
				c.setFillColor(fill);
			}
			
			c.begin();
			c.moveTo(start, 0);
			c.lineTo(w, 0);
			c.lineTo(w, h);
			c.lineTo(start, h);
			
			if (fill == mxConstants.NONE)
			{
				c.stroke();
			}
			else
			{
				c.fillAndStroke();
			}
		}
	}
	
	if (swimlaneLine)
	{
		this.paintDivider(c, x, y, w, h, start, fill == mxConstants.NONE);
	}
};

/**
 * Function: paintRoundedSwimlane
 *
 * Paints the swimlane vertex shape.
 */
// 中文注释：
// 方法：paintRoundedSwimlane
// 功能：绘制带圆角的泳道顶点形状。
// 参数：c - 画布对象；x, y - 坐标；w, h - 宽度和高度；start - 标题区域大小；r - 圆角大小；fill - 填充颜色；swimlaneLine - 是否绘制分隔线。
// 关键步骤：
// 1. 检查是否启用指针事件（STYLE_POINTER_EVENTS）。
// 2. 根据方向（水平或垂直）绘制带圆角的标题区域和内容区域，使用 quadTo 绘制圆角曲线。
// 3. 若启用分隔线，调用 paintDivider 绘制。
// 事件处理逻辑：根据 STYLE_POINTER_EVENTS 控制是否响应指针事件。
// 特殊处理：确保圆角绘制正确，填充颜色和事件设置影响交互行为。
mxSwimlane.prototype.paintRoundedSwimlane = function(c, x, y, w, h, start, r, fill, swimlaneLine)
{
	c.begin();
	
	var events = true;
	
	if (this.style != null)
	{
		events = mxUtils.getValue(this.style, mxConstants.STYLE_POINTER_EVENTS, '1') == '1';
	}
	
	if (!events && (this.fill == null || this.fill == mxConstants.NONE))
	{
		c.pointerEvents = false;
	}
	
	if (this.isHorizontal())
	{
		c.moveTo(w, start);
		c.lineTo(w, r);
		c.quadTo(w, 0, w - Math.min(w / 2, r), 0);
		c.lineTo(Math.min(w / 2, r), 0);
		c.quadTo(0, 0, 0, r);
		c.lineTo(0, start);
		c.fillAndStroke();
		
		if (start < h)
		{
			if (fill == mxConstants.NONE || !events)
			{
				c.pointerEvents = false;
			}
			
			if (fill != mxConstants.NONE)
			{
				c.setFillColor(fill);
			}
			
			c.begin();
			c.moveTo(0, start);
			c.lineTo(0, h - r);
			c.quadTo(0, h, Math.min(w / 2, r), h);
			c.lineTo(w - Math.min(w / 2, r), h);
			c.quadTo(w, h, w, h - r);
			c.lineTo(w, start);
			
			if (fill == mxConstants.NONE)
			{
				c.stroke();
			}
			else
			{
				c.fillAndStroke();
			}
		}
	}
	else
	{
		c.moveTo(start, 0);
		c.lineTo(r, 0);
		c.quadTo(0, 0, 0, Math.min(h / 2, r));
		c.lineTo(0, h - Math.min(h / 2, r));
		c.quadTo(0, h, r, h);
		c.lineTo(start, h);
		c.fillAndStroke();

		if (start < w)
		{
			if (fill == mxConstants.NONE || !events)
			{
				c.pointerEvents = false;
			}
			
			if (fill != mxConstants.NONE)
			{
				c.setFillColor(fill);
			}
			
			c.begin();
			c.moveTo(start, h);
			c.lineTo(w - r, h);
			c.quadTo(w, h, w, h - Math.min(h / 2, r));
			c.lineTo(w, Math.min(h / 2, r));
			c.quadTo(w, 0, w - r, 0);
			c.lineTo(start, 0);
			
			if (fill == mxConstants.NONE)
			{
				c.stroke();
			}
			else
			{
				c.fillAndStroke();
			}
		}
	}

	if (swimlaneLine)
	{
		this.paintDivider(c, x, y, w, h, start, fill == mxConstants.NONE);
	}
};

/**
 * Function: paintDivider
 *
 * Paints the divider between swimlane title and content area.
 */
// 中文注释：
// 方法：paintDivider
// 功能：绘制标题区域与内容区域之间的分隔线。
// 参数：c - 画布对象；x, y - 坐标；w, h - 宽度和高度；start - 标题区域大小；shadow - 是否禁用阴影。
// 关键步骤：
// 1. 若禁用阴影，设置画布无阴影。
// 2. 根据方向（水平或垂直）绘制直线分隔线。
// 特殊处理：确保阴影设置不影响分隔线绘制。
mxSwimlane.prototype.paintDivider = function(c, x, y, w, h, start, shadow)
{
	if (!shadow)
	{
		c.setShadow(false);
	}

	c.begin();
	
	if (this.isHorizontal())
	{
		c.moveTo(0, start);
		c.lineTo(w, start);
	}
	else
	{
		c.moveTo(start, 0);
		c.lineTo(start, h);
	}

	c.stroke();
};

/**
 * Function: paintSeparator
 *
 * Paints the vertical or horizontal separator line between swimlanes.
 */
// 中文注释：
// 方法：paintSeparator
// 功能：绘制泳道之间的垂直或水平分隔线。
// 参数：c - 画布对象；x, y - 坐标；w, h - 宽度和高度；start - 标题区域大小；color - 分隔线颜色。
// 关键步骤：
// 1. 若颜色不为 NONE，设置描边颜色并启用虚线样式。
// 2. 根据方向绘制分隔线（水平或垂直）。
// 3. 绘制后恢复非虚线样式。
// 重要配置参数：STYLE_SEPARATORCOLOR - 定义分隔线颜色。
// 特殊处理：仅在颜色有效时绘制，确保虚线样式正确应用和恢复。
mxSwimlane.prototype.paintSeparator = function(c, x, y, w, h, start, color)
{
	if (color != mxConstants.NONE)
	{
		c.setStrokeColor(color);
		c.setDashed(true);
		c.begin();
		
		if (this.isHorizontal())
		{
			c.moveTo(w, start);
			c.lineTo(w, h);
		}
		else
		{
			c.moveTo(start, 0);
			c.lineTo(w, 0);
		}
		
		c.stroke();
		c.setDashed(false);
	}
};

/**
 * Function: getImageBounds
 *
 * Paints the swimlane vertex shape.
 */
// 中文注释：
// 方法：getImageBounds
// 功能：计算并返回图像的边界框。
// 参数：x, y - 坐标；w, h - 宽度和高度。
// 关键步骤：根据方向（水平或垂直）计算图像位置，默认使用 imageSize（16）作为图像尺寸。
// 返回值：mxRectangle 对象，表示图像的边界框。
mxSwimlane.prototype.getImageBounds = function(x, y, w, h)
{
	if (this.isHorizontal())
	{
		return new mxRectangle(x + w - this.imageSize, y, this.imageSize, this.imageSize);
	}
	else
	{
		return new mxRectangle(x, y, this.imageSize, this.imageSize);
	}
};
