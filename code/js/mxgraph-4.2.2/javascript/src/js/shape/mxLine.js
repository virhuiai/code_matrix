/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxLine
 *
 * Extends <mxShape> to implement a horizontal line shape.
 * This shape is registered under <mxConstants.SHAPE_LINE> in
 * <mxCellRenderer>.
 * 
 * Constructor: mxLine
 *
 * Constructs a new line shape.
 * 
 * Parameters:
 * 
 * bounds - <mxRectangle> that defines the bounds. This is stored in
 * <mxShape.bounds>.
 * stroke - String that defines the stroke color. Default is 'black'. This is
 * stored in <stroke>.
 * strokewidth - Optional integer that defines the stroke width. Default is
 * 1. This is stored in <strokewidth>.
 */
// 中文注释：
// mxLine 类继承自 mxShape，用于实现水平或垂直线条形状。
// 该形状在 mxCellRenderer 中以 mxConstants.SHAPE_LINE 注册。
// 构造函数 mxLine 用于创建新的线条形状。
// 参数说明：
// - bounds: mxRectangle 类型，定义线条的边界，存储在 mxShape.bounds 中。
// - stroke: 字符串，指定线条颜色，默认为 'black'，存储在 stroke 属性中。
// - strokewidth: 可选整数，指定线条宽度，默认为 1，存储在 strokewidth 属性中。
// - vertical: 可选布尔值，指定是否为垂直线条，默认为 false（水平线）。
function mxLine(bounds, stroke, strokewidth, vertical)
{
	mxShape.call(this);
	this.bounds = bounds;
	this.stroke = stroke;
	this.strokewidth = (strokewidth != null) ? strokewidth : 1;
	this.vertical = (vertical != null) ? vertical : this.vertical;
    // 中文注释：
    // 初始化 mxShape 父类。
    // 将传入的 bounds、stroke 和 strokewidth 存储到实例属性中。
    // 如果未提供 strokewidth，默认值为 1。
    // vertical 参数决定线条方向，未提供时使用默认值 this.vertical（false，水平线）。
};

/**
 * Extends mxShape.
 */
// 中文注释：
// 使用 mxUtils.extend 方法使 mxLine 继承 mxShape，提供形状绘制的基础功能。
mxUtils.extend(mxLine, mxShape);

/**
 * Function: vertical
 * 
 * Whether to paint a vertical line.
 */
// 中文注释：
// vertical 属性：布尔值，控制线条是否为垂直方向，默认为 false（水平线）。
// 用途：决定线条的绘制方向（水平或垂直）。
mxLine.prototype.vertical = false;

/**
 * Function: paintVertexShape
 * 
 * Redirects to redrawPath for subclasses to work.
 */
// 中文注释：
// paintVertexShape 方法：绘制线条形状的核心方法。
// 方法目的：根据 vertical 属性绘制水平或垂直线条。
// 参数说明：
// - c: 画布上下文，用于绘制操作。
// - x, y: 线条的起始坐标（边界左上角）。
// - w, h: 线条的宽度和高度（边界尺寸）。
// 交互逻辑：
//   - 如果 vertical 为 true，绘制垂直线，从边界中点 (x + w/2, y) 到 (x + w/2, y + h)。
//   - 如果 vertical 为 false，绘制水平线，从 (x, y + h/2) 到 (x + w, y + h/2)。
// 特殊处理：
//   - 使用 c.begin() 开始绘制路径。
//   - c.moveTo 和 c.lineTo 设置线条起点和终点。
//   - c.stroke() 执行绘制操作，应用 stroke 和 strokewidth 样式。
mxLine.prototype.paintVertexShape = function(c, x, y, w, h)
{
	c.begin();

	if (this.vertical)
	{
		var mid = x + w / 2;
		c.moveTo(mid, y);
		c.lineTo(mid, y + h);
        // 中文注释：
        // 垂直线条绘制：
        // - 计算水平中点 mid = x + w/2。
        // - 从 (mid, y) 移动到起点。
        // - 绘制到 (mid, y + h) 形成垂直线。
	}
	else
	{
		var mid = y + h / 2;
		c.moveTo(x, mid);
		c.lineTo(x + w, mid);
        // 中文注释：
        // 水平线条绘制：
        // - 计算垂直中点 mid = y + h/2。
        // - 从 (x, mid) 移动到起点。
        // - 绘制到 (x + w, mid) 形成水平线。
	}

	c.stroke();
    // 中文注释：
    // 执行绘制操作，应用线条颜色（stroke）和宽度（strokewidth）。
    // 样式设置：颜色由 this.stroke 定义，宽度由 this.strokewidth 定义。
};
