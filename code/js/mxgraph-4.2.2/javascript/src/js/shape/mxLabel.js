/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxLabel
 *
 * Extends <mxShape> to implement an image shape with a label.
 * This shape is registered under <mxConstants.SHAPE_LABEL> in
 * <mxCellRenderer>.
 * 
 * Constructor: mxLabel
 *
 * Constructs a new label shape.
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
// mxLabel 类继承自 mxShape，用于创建带标签的图像形状。
// 该形状在 mxCellRenderer 中以 mxConstants.SHAPE_LABEL 注册。
// 构造函数 mxLabel 用于初始化一个新的标签形状。
// 参数说明：
// - bounds: mxRectangle 类型，定义形状的边界，存储在 mxShape.bounds 中。
// - fill: 字符串，定义填充颜色，存储在 fill 属性中。
// - stroke: 字符串，定义边框颜色，存储在 stroke 属性中。
// - strokewidth: 可选整数，定义边框宽度，默认为 1，存储在 strokewidth 属性中。
function mxLabel(bounds, fill, stroke, strokewidth)
{
	mxRectangleShape.call(this, bounds, fill, stroke, strokewidth);
};

/**
 * Extends mxShape.
 */
// 中文注释：
// 通过 mxUtils.extend 方法使 mxLabel 继承 mxRectangleShape，
// 使其具备矩形形状的基本功能，同时扩展为带标签的图像形状。
mxUtils.extend(mxLabel, mxRectangleShape);

/**
 * Variable: imageSize
 *
 * Default width and height for the image. Default is
 * <mxConstants.DEFAULT_IMAGESIZE>.
 */
// 中文注释：
// imageSize 变量定义图像的默认宽度和高度，
// 默认值为 mxConstants.DEFAULT_IMAGESIZE。
// 用途：用于设置图像的默认尺寸。
mxLabel.prototype.imageSize = mxConstants.DEFAULT_IMAGESIZE;

/**
 * Variable: spacing
 *
 * Default value for image spacing. Default is 2.
 */
// 中文注释：
// spacing 变量定义图像与标签之间的默认间距，
// 默认值为 2。
// 用途：控制图像与周围元素的间距，确保布局美观。
mxLabel.prototype.spacing = 2;

/**
 * Variable: indicatorSize
 *
 * Default width and height for the indicicator. Default is 10.
 */
// 中文注释：
// indicatorSize 变量定义指示器的默认宽度和高度，
// 默认值为 10。
// 用途：用于设置指示器（如状态图标）的默认尺寸。
mxLabel.prototype.indicatorSize = 10;

/**
 * Variable: indicatorSpacing
 *
 * Default spacing between image and indicator. Default is 2.
 */
// 中文注释：
// indicatorSpacing 变量定义图像与指示器之间的默认间距，
// 默认值为 2。
// 用途：控制图像与指示器之间的间距，确保布局清晰。
mxLabel.prototype.indicatorSpacing = 2;

/**
 * Function: init
 *
 * Initializes the shape and the <indicator>.
 */
// 中文注释：
// init 方法用于初始化形状及其指示器。
// 功能：调用父类的 init 方法，并根据需要初始化指示器对象。
// 关键步骤：
// 1. 调用 mxShape 的 init 方法，完成基础形状初始化。
// 2. 如果存在 indicatorShape，则创建并初始化指示器，设置其 dialect 和 node。
// 参数：
// - container: 形状的容器元素，用于渲染。
mxLabel.prototype.init = function(container)
{
	mxShape.prototype.init.apply(this, arguments);

	if (this.indicatorShape != null)
	{
		this.indicator = new this.indicatorShape();
		this.indicator.dialect = this.dialect;
		this.indicator.init(this.node);
	}
};

/**
 * Function: redraw
 *
 * Reconfigures this shape. This will update the colors of the indicator
 * and reconfigure it if required.
 */
// 中文注释：
// redraw 方法用于重新配置并绘制形状。
// 功能：更新指示器的颜色并重新绘制形状。
// 关键步骤：
// 1. 如果存在指示器，更新其填充色、边框色、渐变色和方向。
// 2. 调用指示器的 redraw 方法重新绘制指示器。
// 3. 调用父类的 redraw 方法完成其他绘制任务。
// 注意事项：确保指示器的样式与当前形状状态一致。
mxLabel.prototype.redraw = function()
{
	if (this.indicator != null)
	{
		this.indicator.fill = this.indicatorColor;
		this.indicator.stroke = this.indicatorStrokeColor;
		this.indicator.gradient = this.indicatorGradientColor;
		this.indicator.direction = this.indicatorDirection;
		this.indicator.redraw();
	}
	
	mxShape.prototype.redraw.apply(this, arguments);
};

/**
 * Function: isHtmlAllowed
 *
 * Returns true for non-rounded, non-rotated shapes with no glass gradient and
 * no indicator shape.
 */
// 中文注释：
// isHtmlAllowed 方法判断是否允许使用 HTML 渲染。
// 功能：检查形状是否满足特定条件以支持 HTML 渲染。
// 条件：
// 1. 形状非圆角、非旋转。
// 2. 无玻璃渐变效果。
// 3. 无指示器形状和颜色。
// 返回值：布尔值，true 表示允许 HTML 渲染。
// 用途：确保在特定条件下使用 HTML 渲染以提高性能。
mxLabel.prototype.isHtmlAllowed = function()
{
	return mxRectangleShape.prototype.isHtmlAllowed.apply(this, arguments) &&
		this.indicatorColor == null && this.indicatorShape == null;
};

/**
 * Function: paintForeground
 * 
 * Generic background painting implementation.
 */
// 中文注释：
// paintForeground 方法实现前景的通用绘制。
// 功能：绘制图像和指示器，并调用父类的前景绘制方法。
// 关键步骤：
// 1. 调用 paintImage 方法绘制图像。
// 2. 调用 paintIndicator 方法绘制指示器。
// 3. 调用父类的 paintForeground 方法完成其他前景绘制。
// 用途：负责形状前景的渲染，包括图像和指示器。
mxLabel.prototype.paintForeground = function(c, x, y, w, h)
{
	this.paintImage(c, x, y, w, h);
	this.paintIndicator(c, x, y, w, h);
	
	mxRectangleShape.prototype.paintForeground.apply(this, arguments);
};

/**
 * Function: paintImage
 * 
 * Generic background painting implementation.
 */
// 中文注释：
// paintImage 方法实现图像的绘制。
// 功能：根据图像属性绘制图像到画布。
// 关键步骤：
// 1. 检查是否存在图像（this.image 不为 null）。
// 2. 调用 getImageBounds 方法获取图像的边界。
// 3. 使用画布的 image 方法绘制图像。
// 参数：
// - c: 画布上下文，用于绘制。
// - x, y, w, h: 图像的坐标和尺寸。
// 注意事项：图像绘制时不保留纵横比、不裁剪、不翻转。
mxLabel.prototype.paintImage = function(c, x, y, w, h)
{
	if (this.image != null)
	{
		var bounds = this.getImageBounds(x, y, w, h);
		c.image(bounds.x, bounds.y, bounds.width, bounds.height, this.image, false, false, false);
	}
};

/**
 * Function: getImageBounds
 * 
 * Generic background painting implementation.
 */
// 中文注释：
// getImageBounds 方法计算图像的边界。
// 功能：根据样式和间距确定图像的绘制位置和大小。
// 关键步骤：
// 1. 获取水平对齐方式（默认左对齐）。
// 2. 获取垂直对齐方式（默认居中）。
// 3. 获取图像宽度和高度（默认 mxConstants.DEFAULT_IMAGESIZE）。
// 4. 计算间距（默认 spacing + 5）。
// 5. 根据对齐方式调整 x 和 y 坐标。
// 返回值：mxRectangle 对象，包含图像的边界（x, y, width, height）。
// 用途：为图像绘制提供精确的定位和尺寸。
mxLabel.prototype.getImageBounds = function(x, y, w, h)
{
	var align = mxUtils.getValue(this.style, mxConstants.STYLE_IMAGE_ALIGN, mxConstants.ALIGN_LEFT);
	var valign = mxUtils.getValue(this.style, mxConstants.STYLE_IMAGE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
	var width = mxUtils.getNumber(this.style, mxConstants.STYLE_IMAGE_WIDTH, mxConstants.DEFAULT_IMAGESIZE);
	var height = mxUtils.getNumber(this.style, mxConstants.STYLE_IMAGE_HEIGHT, mxConstants.DEFAULT_IMAGESIZE);
	var spacing = mxUtils.getNumber(this.style, mxConstants.STYLE_SPACING, this.spacing) + 5;

	if (align == mxConstants.ALIGN_CENTER)
	{
		x += (w - width) / 2;
	}
	else if (align == mxConstants.ALIGN_RIGHT)
	{
		x += w - width - spacing;
	}
	else // default is left
	{
		x += spacing;
	}

	if (valign == mxConstants.ALIGN_TOP)
	{
		y += spacing;
	}
	else if (valign == mxConstants.ALIGN_BOTTOM)
	{
		y += h - height - spacing;
	}
	else // default is middle
	{
		y += (h - height) / 2;
	}
	
	return new mxRectangle(x, y, width, height);
};

/**
 * Function: paintIndicator
 * 
 * Generic background painting implementation.
 */
// 中文注释：
// paintIndicator 方法实现指示器的绘制。
// 功能：根据指示器类型绘制指示器或指示器图像。
// 关键步骤：
// 1. 如果存在指示器对象（this.indicator），设置其边界并调用 paint 方法。
// 2. 如果存在指示器图像（this.indicatorImage），获取边界并绘制图像。
// 参数：
// - c: 画布上下文，用于绘制。
// - x, y, w, h: 指示器的坐标和尺寸。
// 注意事项：支持两种指示器绘制方式（对象或图像），需根据实际情况选择。
mxLabel.prototype.paintIndicator = function(c, x, y, w, h)
{
	if (this.indicator != null)
	{
		this.indicator.bounds = this.getIndicatorBounds(x, y, w, h);
		this.indicator.paint(c);
	}
	else if (this.indicatorImage != null)
	{
		var bounds = this.getIndicatorBounds(x, y, w, h);
		c.image(bounds.x, bounds.y, bounds.width, bounds.height, this.indicatorImage, false, false, false);
	}
};

/**
 * Function: getIndicatorBounds
 * 
 * Generic background painting implementation.
 */
// 中文注释：
// getIndicatorBounds 方法计算指示器的边界。
// 功能：根据样式和间距确定指示器的绘制位置和大小。
// 关键步骤：
// 1. 获取水平对齐方式（默认左对齐）。
// 2. 获取垂直对齐方式（默认居中）。
// 3. 获取指示器宽度和高度（默认 indicatorSize）。
// 4. 计算间距（默认 spacing + 5）。
// 5. 根据对齐方式调整 x 和 y 坐标。
// 返回值：mxRectangle 对象，包含指示器的边界（x, y, width, height）。
// 用途：为指示器绘制提供精确的定位和尺寸。
mxLabel.prototype.getIndicatorBounds = function(x, y, w, h)
{
	var align = mxUtils.getValue(this.style, mxConstants.STYLE_IMAGE_ALIGN, mxConstants.ALIGN_LEFT);
	var valign = mxUtils.getValue(this.style, mxConstants.STYLE_IMAGE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
	var width = mxUtils.getNumber(this.style, mxConstants.STYLE_INDICATOR_WIDTH, this.indicatorSize);
	var height = mxUtils.getNumber(this.style, mxConstants.STYLE_INDICATOR_HEIGHT, this.indicatorSize);
	var spacing = this.spacing + 5;		
	
	if (align == mxConstants.ALIGN_RIGHT)
	{
		x += w - width - spacing;
	}
	else if (align == mxConstants.ALIGN_CENTER)
	{
		x += (w - width) / 2;
	}
	else // default is left
	{
		x += spacing;
	}
	
	if (valign == mxConstants.ALIGN_BOTTOM)
	{
		y += h - height - spacing;
	}
	else if (valign == mxConstants.ALIGN_TOP)
	{
		y += spacing;
	}
	else // default is middle
	{
		y += (h - height) / 2;
	}
	
	return new mxRectangle(x, y, width, height);
};
/**
 * Function: redrawHtmlShape
 * 
 * Generic background painting implementation.
 */
// 中文注释：
// redrawHtmlShape 方法实现 HTML 形状的重新绘制。
// 功能：在 HTML 渲染模式下重新绘制形状，处理图像元素。
// 关键步骤：
// 1. 调用父类的 redrawHtmlShape 方法完成基础绘制。
// 2. 移除所有子节点以清空现有内容。
// 3. 如果存在图像（this.image），创建 img 元素并设置样式和属性。
// 4. 计算图像边界并调整相对位置。
// 5. 将图像元素添加到节点中。
// 样式设置：
// - position: relative，确保图像相对定位。
// - border: 0，移除图像边框。
// - left, top, width, height: 设置图像的精确位置和尺寸。
// 注意事项：图像坐标需转换为相对于父节点的相对坐标。
mxLabel.prototype.redrawHtmlShape = function()
{
	mxRectangleShape.prototype.redrawHtmlShape.apply(this, arguments);
	
	// Removes all children
	while(this.node.hasChildNodes())
	{
		this.node.removeChild(this.node.lastChild);
	}
	
	if (this.image != null)
	{
		var node = document.createElement('img');
		node.style.position = 'relative';
		node.setAttribute('border', '0');
		
		var bounds = this.getImageBounds(this.bounds.x, this.bounds.y, this.bounds.width, this.bounds.height);
		bounds.x -= this.bounds.x;
		bounds.y -= this.bounds.y;

		node.style.left = Math.round(bounds.x) + 'px';
		node.style.top = Math.round(bounds.y) + 'px';
		node.style.width = Math.round(bounds.width) + 'px';
		node.style.height = Math.round(bounds.height) + 'px';
		
		node.src = this.image;
		
		this.node.appendChild(node);
	}
};
