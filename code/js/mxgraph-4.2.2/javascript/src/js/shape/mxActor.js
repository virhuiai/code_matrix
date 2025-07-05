/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxActor
 *
 * Extends <mxShape> to implement an actor shape. If a custom shape with one
 * filled area is needed, then this shape's <redrawPath> should be overridden.
 * 
 * Example:
 * 
 * (code)
 * function SampleShape() { }
 * 
 * SampleShape.prototype = new mxActor();
 * SampleShape.prototype.constructor = vsAseShape;
 * 
 * mxCellRenderer.registerShape('sample', SampleShape);
 * SampleShape.prototype.redrawPath = function(path, x, y, w, h)
 * {
 *   path.moveTo(0, 0);
 *   path.lineTo(w, h);
 *   // ...
 *   path.close();
 * }
 * (end)
 * 
 * This shape is registered under <mxConstants.SHAPE_ACTOR> in
 * <mxCellRenderer>.
 * 
 * // 类：mxActor
 * // 继承自mxShape，用于实现一个演员形状。如果需要自定义单填充区域的形状，应重写redrawPath方法。
 * // 示例代码展示如何创建自定义形状并注册到mxCellRenderer中。
 * // 该形状在mxCellRenderer中以mxConstants.SHAPE_ACTOR注册。
 *
 * Constructor: mxActor
 *
 * Constructs a new actor shape.
 * 
 * Parameters:
 * 
 * bounds - <mxRectangle> that defines the bounds. This is stored in
 * <mxShape.bounds>.
 * fill - String that defines the fill color. This is stored in <fill>.
 * stroke - String that defines the stroke color. This is stored in <stroke>.
 * strokewidth - Optional integer that defines the stroke width. Default is
 * 1. This is stored in <strokewidth>.
 *
 * // 构造函数：mxActor
 * // 用于创建新的演员形状。
 * // 参数说明：
 * // bounds - mxRectangle对象，定义形状的边界，存储在mxShape.bounds中。
 * // fill - 字符串，定义填充颜色，存储在fill属性中。
 * // stroke - 字符串，定义描边颜色，存储在stroke属性中。
 * // strokewidth - 可选整数，定义描边宽度，默认值为1，存储在strokewidth属性中。
 */
function mxActor(bounds, fill, stroke, strokewidth)
{
	mxShape.call(this);
	this.bounds = bounds;
	this.fill = fill;
	this.stroke = stroke;
	this.strokewidth = (strokewidth != null) ? strokewidth : 1;
    // // 初始化mxShape基类。
    // // 将传入的边界、填充颜色、描边颜色和描边宽度存储到对应属性中。
    // // strokewidth如果未提供，则默认设置为1。
};

/**
 * Extends mxShape.
 * // 继承mxShape
 * // 通过mxUtils.extend方法使mxActor继承mxShape的属性和方法。
 */
mxUtils.extend(mxActor, mxShape);

/**
 * Function: paintVertexShape
 * 
 * Redirects to redrawPath for subclasses to work.
 * // 函数：paintVertexShape
 * // 用于绘制顶点形状，重定向到redrawPath方法以支持子类自定义。
 * // 参数说明：
 * // c - 画布上下文，用于绘制操作。
 * // x, y - 绘制起点的坐标。
 * // w, h - 形状的宽度和高度。
 */
mxActor.prototype.paintVertexShape = function(c, x, y, w, h)
{
	c.translate(x, y);
	c.begin();
	this.redrawPath(c, x, y, w, h);
	c.fillAndStroke();
    // // 将画布平移到指定坐标(x, y)。
    // // 开始新的绘制路径。
    // // 调用redrawPath方法绘制具体路径。
    // // 应用填充和描边样式，完成绘制。
};

/**
 * Function: redrawPath
 *
 * Draws the path for this shape.
 * // 函数：redrawPath
 * // 用于绘制该形状的路径。
 * // 参数说明：
 * // c - 画布上下文，用于绘制路径。
 * // x, y - 绘制起点的坐标（在此函数中未直接使用，但提供给子类可能需要）。
 * // w, h - 形状的宽度和高度，用于计算路径点。
 * // 注意事项：此方法定义了演员形状的默认路径，子类可重写以实现自定义形状。
 */
mxActor.prototype.redrawPath = function(c, x, y, w, h)
{
	var width = w/3;
	c.moveTo(0, h);
	c.curveTo(0, 3 * h / 5, 0, 2 * h / 5, w / 2, 2 * h / 5);
	c.curveTo(w / 2 - width, 2 * h / 5, w / 2 - width, 0, w / 2, 0);
	c.curveTo(w / 2 + width, 0, w / 2 + width, 2 * h / 5, w / 2, 2 * h / 5);
	c.curveTo(w, 2 * h / 5, w, 3 * h / 5, w, h);
	c.close();
    // // 定义变量width为形状宽度的1/3，用于路径计算。
    // // 从点(0, h)开始绘制路径。
    // // 使用贝塞尔曲线绘制四段路径，形成演员形状的轮廓。
    // // 第一段曲线从(0, h)到(w/2, 2h/5)，经过控制点(0, 3h/5)和(0, 2h/5)。
    // // 第二段曲线从(w/2, 2h/5)到(w/2, 0)，经过控制点(w/2 - width, 2h/5)和(w/2 - width, 0)。
    // // 第三段曲线从(w/2, 0)到(w/2, 2h/5)，经过控制点(w/2 + width, 0)和(w/2 + width, 2h/5)。
    // // 第四段曲线从(w/2, 2h/5)到(w, h)，经过控制点(w, 2h/5)和(w, 3h/5)。
    // // 闭合路径，完成形状绘制。
};
