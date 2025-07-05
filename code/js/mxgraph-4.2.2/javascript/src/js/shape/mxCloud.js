/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxCloud
 *
 * Extends <mxActor> to implement a cloud shape.
 * 
 * This shape is registered under <mxConstants.SHAPE_CLOUD> in
 * <mxCellRenderer>.
 * 
 * Constructor: mxCloud
 *
 * Constructs a new cloud shape.
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
// 类名：mxCloud
// 功能：继承 mxActor 类，实现云形状的图形。
// 注册：在 mxCellRenderer 中以 mxConstants.SHAPE_CLOUD 注册此形状。
// 构造函数：mxCloud
// 作用：创建新的云形状实例。
// 参数说明：
// - bounds: mxRectangle 类型，定义图形边界，存储在 mxShape.bounds 中。
// - fill: 字符串，定义填充颜色，存储在 fill 属性中。
// - stroke: 字符串，定义描边颜色，存储在 stroke 属性中。
// - strokewidth: 可选整数，定义描边宽度，默认为 1，存储在 strokewidth 属性中。
function mxCloud(bounds, fill, stroke, strokewidth)
{
	mxActor.call(this);
    // 中文注释：
    // 调用父类 mxActor 的构造函数，初始化云形状。
	this.bounds = bounds;
    // 中文注释：
    // 设置图形边界，存储传入的 bounds 参数。
	this.fill = fill;
    // 中文注释：
    // 设置填充颜色，存储传入的 fill 参数。
	this.stroke = stroke;
    // 中文注释：
    // 设置描边颜色，存储传入的 stroke 参数。
	this.strokewidth = (strokewidth != null) ? strokewidth : 1;
    // 中文注释：
    // 设置描边宽度，若传入 strokewidth 则使用该值，否则默认为 1。
};

/**
 * Extends mxActor.
 */
// 中文注释：
// 功能：通过 mxUtils.extend 方法使 mxCloud 继承 mxActor 类，获取父类的属性和方法。
mxUtils.extend(mxCloud, mxActor);

/**
 * Function: redrawPath
 *
 * Draws the path for this shape.
 */
// 中文注释：
// 方法名：redrawPath
// 功能：绘制云形状的路径。
// 参数说明：
// - c: 画布上下文，用于执行绘制操作。
// - x: 水平偏移量，定义绘制起点的 x 坐标。
// - y: 垂直偏移量，定义绘制起点的 y 坐标。
// - w: 图形宽度，定义云形状的宽度。
// - h: 图形高度，定义云形状的高度。
// 注意事项：
// - 使用贝塞尔曲线（curveTo）绘制平滑的云形状路径。
// - 路径从指定起点开始，依次绘制多个曲线段，最终闭合形成云形状。
mxCloud.prototype.redrawPath = function(c, x, y, w, h)
{
	c.moveTo(0.25 * w, 0.25 * h);
    // 中文注释：
    // 将画笔移动到起点，坐标为 (0.25 * w, 0.25 * h)，即图形宽度的 1/4 和高度的 1/4。
	c.curveTo(0.05 * w, 0.25 * h, 0, 0.5 * h, 0.16 * w, 0.55 * h);
    // 中文注释：
    // 绘制第一段贝塞尔曲线，从当前点到 (0.16 * w, 0.55 * h)，控制点为 (0.05 * w, 0.25 * h) 和 (0, 0.5 * h)。
	c.curveTo(0, 0.66 * h, 0.18 * w, 0.9 * h, 0.31 * w, 0.8 * h);
    // 中文注释：
    // 绘制第二段贝塞尔曲线，到 (0.31 * w, 0.8 * h)，控制点为 (0, 0.66 * h) 和 (0.18 * w, 0.9 * h)。
	c.curveTo(0.4 * w, h, 0.7 * w, h, 0.8 * w, 0.8 * h);
    // 中文注释：
    // 绘制第三段贝塞尔曲线，到 (0.8 * w, 0.8 * h)，控制点为 (0.4 * w, h) 和 (0.7 * w, h)。
	c.curveTo(w, 0.8 * h, w, 0.6 * h, 0.875 * w, 0.5 * h);
    // 中文注释：
    // 绘制第四段贝塞尔曲线，到 (0.875 * the, 0.5 * h)，控制点为 (w, 0.8 * h) 和 (w, 0.6 * h)。
	c.curveTo(w, 0.3 * h, 0.8 * w, 0.1 * h, 0.625 * w, 0.2 * h);
    // 中文注释：
    // 绘制第五段贝塞尔曲线，到 (0.625 * w, 0.2 * h)，控制点为 (w, 0.3 * h) 和 (0.8 * w, 0.1 * h)。
	c.curveTo(0.5 * w, 0.05 * h, 0.3 * w, 0.05 * h, 0.25 * w, 0.25 * h);
    // 中文注释：
    // 绘制第六段贝塞尔曲线，返回起点 (0.25 * w, 0.25 * h)，控制点为 (0.5 * w, 0.05 * h) 和 (0.3 * w, 0.05 * h)。
	c.close();
    // 中文注释：
    // 闭合路径，完成云形状的绘制。
};
