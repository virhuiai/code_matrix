/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxPolyline
 *
 * Extends <mxShape> to implement a polyline (a line with multiple points).
 * This shape is registered under <mxConstants.SHAPE_POLYLINE> in
 * <mxCellRenderer>.
 * 
 * Constructor: mxPolyline
 *
 * Constructs a new polyline shape.
 * 
 * Parameters:
 * 
 * points - Array of <mxPoints> that define the points. This is stored in
 * <mxShape.points>.
 * stroke - String that defines the stroke color. Default is 'black'. This is
 * stored in <stroke>.
 * strokewidth - Optional integer that defines the stroke width. Default is
 * 1. This is stored in <strokewidth>.
 */
// 类：mxPolyline
// 继承自 mxShape，用于实现多段线（包含多个点的线条）。
// 该形状在 mxCellRenderer 中以 mxConstants.SHAPE_POLYLINE 注册。
// 构造函数：mxPolyline
// 创建一个新的多段线形状。
// 参数：
// points - mxPoints 数组，定义多段线的点，存储在 mxShape.points 中。
// stroke - 字符串，定义线条颜色，默认为 'black'，存储在 stroke 中。
// strokewidth - 可选整数，定义线条宽度，默认为 1，存储在 strokewidth 中。

//Polyline：英文中“poly”意为“多”，而“line”意为“线”。“Polyline”是一个常见的计算机图形学术语，指由多个点连接而成的折线（多段线）。
function mxPolyline(points, stroke, strokewidth)
{
	mxShape.call(this);
	this.points = points;
	this.stroke = stroke;
	this.strokewidth = (strokewidth != null) ? strokewidth : 1;
};

/**
 * Extends mxShape.
 */
// 继承 mxShape 类，扩展其功能以支持多段线。
mxUtils.extend(mxPolyline, mxShape);

/**
 * Function: getRotation
 * 
 * Returns 0.
 */
// 函数：getRotation
// 返回 0，表示多段线不应用旋转。
mxPolyline.prototype.getRotation = function()
{
	return 0;
};

/**
 * Function: getShapeRotation
 * 
 * Returns 0.
 */
// 函数：getShapeRotation
// 返回 0，表示多段线的形状不进行旋转。
mxPolyline.prototype.getShapeRotation = function()
{
	return 0;
};

/**
 * Function: isPaintBoundsInverted
 * 
 * Returns false.
 */
// 函数：isPaintBoundsInverted
// 返回 false，表示绘制边界不进行反转。
mxPolyline.prototype.isPaintBoundsInverted = function()
{
	return false;
};

/**
 * Function: paintEdgeShape
 * 
 * Paints the line shape.
 */
// 函数：paintEdgeShape
// 绘制多段线的形状。
// 参数：
// c - 画布上下文，用于绘制图形。
// pts - 点数组，定义多段线的路径。
// 逻辑说明：
// 根据样式选择绘制直线或曲线，设置指针事件的值以确保交互性。
mxPolyline.prototype.paintEdgeShape = function(c, pts)
{
	var prev = c.pointerEventsValue;
	c.pointerEventsValue = 'stroke';
    // 保存当前画布的指针事件值，设置为 'stroke' 以支持线条交互。

	if (this.style == null || this.style[mxConstants.STYLE_CURVED] != 1)
	{
		this.paintLine(c, pts, this.isRounded);
        // 如果样式未定义或未设置曲线样式，调用 paintLine 绘制直线。
	}
	else
	{
		this.paintCurvedLine(c, pts);
        // 如果样式设置为曲线，调用 paintCurvedLine 绘制曲线。
	}
	
	c.pointerEventsValue = prev;
    // 恢复画布的指针事件值。
};

/**
 * Function: paintLine
 * 
 * Paints the line shape.
 */
// 函数：paintLine
// 绘制直线形状。
// 参数：
// c - 画布上下文，用于绘制图形。
// pts - 点数组，定义线条的路径。
// rounded - 布尔值，指示是否使用圆角。
// 逻辑说明：
// 根据样式中的圆弧大小（arcSize）绘制直线路径，支持圆角选项。
mxPolyline.prototype.paintLine = function(c, pts, rounded)
{
	var arcSize = mxUtils.getValue(this.style, mxConstants.STYLE_ARCSIZE, mxConstants.LINE_ARCSIZE) / 2;
    // 获取样式中的圆弧大小，默认为 mxConstants.LINE_ARCSIZE 的一半。
	c.begin();
    // 开始新的绘制路径。
	this.addPoints(c, pts, rounded, arcSize, false);
    // 将点数组添加到画布路径，支持圆角和弧形大小。
	c.stroke();
    // 绘制线条。
};

/**
 * Function: paintCurvedLine
 * 
 * Paints a curved line.
 */
// 函数：paintCurvedLine
// 绘制曲线形状。
// 参数：
// c - 画布上下文，用于绘制图形。
// pts - 点数组，定义曲线的路径。
// 逻辑说明：
// 使用二次贝塞尔曲线连接点，创建平滑的曲线路径。
// 注意事项：
// 曲线通过计算相邻点的中点来平滑过渡，最后一段直接连接到终点。
mxPolyline.prototype.paintCurvedLine = function(c, pts)
{
	c.begin();
    // 开始新的绘制路径。

	var pt = pts[0];
	var n = pts.length;
    // 获取第一个点和点的总数。

	c.moveTo(pt.x, pt.y);
    // 将画笔移动到起始点。

	for (var i = 1; i < n - 2; i++)
	{
		var p0 = pts[i];
		var p1 = pts[i + 1];
		var ix = (p0.x + p1.x) / 2;
		var iy = (p0.y + p1.y) / 2;
        // 计算相邻两点的中点坐标，作为曲线的控制点。

		c.quadTo(p0.x, p0.y, ix, iy);
        // 使用二次贝塞尔曲线连接到中点。
	}
	
	var p0 = pts[n - 2];
	var p1 = pts[n - 1];
    // 获取最后两个点。

	c.quadTo(p0.x, p0.y, p1.x, p1.y);
    // 绘制最后一段曲线到终点。
	c.stroke();
    // 绘制曲线。
};
