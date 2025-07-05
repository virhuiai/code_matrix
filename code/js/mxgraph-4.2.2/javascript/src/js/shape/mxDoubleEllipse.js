/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxDoubleEllipse
 *
 * Extends <mxShape> to implement a double ellipse shape. This shape is
 * registered under <mxConstants.SHAPE_DOUBLE_ELLIPSE> in <mxCellRenderer>.
 * Use the following override to only fill the inner ellipse in this shape:
 * 
 * (code)
 * mxDoubleEllipse.prototype.paintVertexShape = function(c, x, y, w, h)
 * {
 *   c.ellipse(x, y, w, h);
 *   c.stroke();
 *   
 *   var inset = mxUtils.getValue(this.style, mxConstants.STYLE_MARGIN, Math.min(3 + this.strokewidth, Math.min(w / 5, h / 5)));
 *   x += inset;
 *   y += inset;
 *   w -= 2 * inset;
 *   h -= 2 * inset;
 *   
 *   if (w > 0 && h > 0)
 *   {
 *     c.ellipse(x, y, w, h);
 *   }
 *   
 *   c.fillAndStroke();
 * };
 * (end)
 * 
 * Constructor: mxDoubleEllipse
 *
 * Constructs a new ellipse shape.
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
// mxDoubleEllipse类用于创建双椭圆形状，继承自mxShape，注册在mxCellRenderer的mxConstants.SHAPE_DOUBLE_ELLIPSE下。
// 构造函数用于初始化双椭圆形状，接收边界、填充颜色、描边颜色和描边宽度参数。
// 参数说明：
// - bounds: mxRectangle对象，定义形状的边界，存储在mxShape.bounds中。
// - fill: 字符串，定义填充颜色，存储在fill属性中。
// - stroke: 字符串，定义描边颜色，存储在stroke属性中。
// - strokewidth: 可选整数，定义描边宽度，默认为1，存储在strokewidth属性中。
function mxDoubleEllipse(bounds, fill, stroke, strokewidth)
{
	mxShape.call(this);
	this.bounds = bounds;
	this.fill = fill;
	this.stroke = stroke;
	this.strokewidth = (strokewidth != null) ? strokewidth : 1;
    // 中文注释：
    // 调用父类mxShape的构造函数，初始化形状。
    // 将传入的边界、填充颜色、描边颜色和描边宽度赋值给实例属性。
    // 如果strokewidth未提供，默认为1。
};

/**
 * Extends mxShape.
 */
// 中文注释：
// 通过mxUtils.extend方法使mxDoubleEllipse继承mxShape，扩展其功能以支持双椭圆形状的绘制。
mxUtils.extend(mxDoubleEllipse, mxShape);

/**
 * Variable: vmlScale
 * 
 * Scale for improving the precision of VML rendering. Default is 10.
 */
// 中文注释：
// vmlScale变量用于提高VML渲染的精度，默认值为10。
// 重要配置参数：此变量影响VML渲染的缩放比例，确保图形在特定渲染环境中显示更精确。
mxDoubleEllipse.prototype.vmlScale = 10;

/**
 * Function: paintBackground
 * 
 * Paints the background.
 */
// 中文注释：
// paintBackground方法用于绘制双椭圆的背景部分。
// 方法目的：绘制外椭圆，并应用填充和描边样式。
// 参数说明：
// - c: 画布上下文，用于执行绘图操作。
// - x, y: 椭圆左上角的坐标。
// - w, h: 椭圆的宽度和高度。
mxDoubleEllipse.prototype.paintBackground = function(c, x, y, w, h)
{
	c.ellipse(x, y, w, h);
	c.fillAndStroke();
    // 中文注释：
    // 在指定位置(x, y)绘制一个宽为w、高为h的椭圆。
    // 调用fillAndStroke方法填充椭圆并绘制描边，形成背景椭圆。
};

/**
 * Function: paintForeground
 * 
 * Paints the foreground.
 */
// 中文注释：
// paintForeground方法用于绘制双椭圆的前景部分（内椭圆）。
// 方法目的：绘制一个较小的内椭圆，仅描边不填充。
// 参数说明：
// - c: 画布上下文，用于执行绘图操作。
// - x, y: 椭圆左上角的坐标。
// - w, h: 椭圆的宽度和高度。
// 特殊处理注意事项：仅当outline属性为false时绘制内椭圆，避免在某些情况下重复绘制。
mxDoubleEllipse.prototype.paintForeground = function(c, x, y, w, h)
{
	if (!this.outline)
	{
		var margin = mxUtils.getValue(this.style, mxConstants.STYLE_MARGIN, Math.min(3 + this.strokewidth, Math.min(w / 5, h / 5)));
		x += margin;
		y += margin;
		w -= 2 * margin;
		h -= 2 * margin;
		
		// FIXME: Rounding issues in IE8 standards mode (not in 1.x)
        // 中文注释：
        // 计算内椭圆的边距(margin)，从样式中获取mxConstants.STYLE_MARGIN的值，默认为3加上描边宽度，或宽高的1/5中较小值。
        // 调整坐标和尺寸以绘制内椭圆，x和y增加边距，w和h减少两倍边距。
        // 注意事项：IE8标准模式下可能存在四舍五入问题，导致绘制精度偏差。

		if (w > 0 && h > 0)
		{
			c.ellipse(x, y, w, h);
		}
		
		c.stroke();
        // 中文注释：
        // 如果调整后的宽高大于0，绘制内椭圆。
        // 调用stroke方法，仅对内椭圆进行描边，不填充。
	}
};

/**
 * Function: getLabelBounds
 * 
 * Returns the bounds for the label.
 */
// 中文注释：
// getLabelBounds方法用于计算标签的边界区域。
// 方法目的：返回一个调整后的mxRectangle对象，用于放置标签，确保标签位于内椭圆内。
// 参数说明：
// - rect: mxRectangle对象，定义原始边界。
// 返回值：调整后的mxRectangle对象，表示标签的边界。
mxDoubleEllipse.prototype.getLabelBounds = function(rect)
{
	var margin = (mxUtils.getValue(this.style, mxConstants.STYLE_MARGIN, Math.min(3 + this.strokewidth,
			Math.min(rect.width / 5 / this.scale, rect.height / 5 / this.scale)))) * this.scale;
    // 中文注释：
    // 计算标签边距(margin)，从样式中获取mxConstants.STYLE_MARGIN的值，默认为3加上描边宽度，或宽高的1/5（考虑缩放比例）中较小值，再乘以缩放比例。
    // 重要配置参数：margin值决定了标签区域与外椭圆的间距，确保标签适配内椭圆。

	return new mxRectangle(rect.x + margin, rect.y + margin, rect.width - 2 * margin, rect.height - 2 * margin);
    // 中文注释：
    // 返回一个新的mxRectangle对象，边界向内收缩margin大小，确保标签位于内椭圆区域内。
};
