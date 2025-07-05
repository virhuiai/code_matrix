/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxTriangle
 * 
 * Implementation of the triangle shape.
 * 
 * Constructor: mxTriangle
 *
 * Constructs a new triangle shape.
 */
// 中文注释：mxTriangle 类
// 功能：实现三角形图形的功能。
// 构造函数：mxTriangle 用于创建新的三角形图形实例。
function mxTriangle()
{
	mxActor.call(this);
};

/**
 * Extends mxActor.
 */
// 中文注释：继承 mxActor
// 说明：通过 mxUtils.extend 方法使 mxTriangle 继承 mxActor 类，扩展其功能。
mxUtils.extend(mxTriangle, mxActor);

/**
 * Function: isRoundable
 * 
 * Adds roundable support.
 */
// 中文注释：isRoundable 方法
// 功能：支持三角形图形的圆角设置。
// 返回值：返回 true，表示此图形支持圆角。
mxTriangle.prototype.isRoundable = function()
{
	return true;
};

/**
 * Function: redrawPath
 *
 * Draws the path for this shape.
 */
// 中文注释：redrawPath 方法
// 功能：绘制三角形图形的路径。
// 参数说明：
//   - c: 画布上下文，用于绘制图形。
//   - x: 图形的 x 坐标（起始位置）。
//   - y: 图形的 y 坐标（起始位置）。
//   - w: 图形的宽度。
//   - h: 图形的高度。
// 关键步骤：
//   1. 获取样式中的圆角大小（arcSize），默认为 mxConstants.LINE_ARCSIZE / 2。
//   2. 调用 addPoints 方法绘制三角形的三条边，顶点为 (0,0)、(w,0.5*h)、(0,h)。
//   3. 根据 isRounded 属性决定是否应用圆角，圆角大小由 arcSize 参数控制。
// 特殊处理：
//   - 如果样式中未定义 mxConstants.STYLE_ARCSIZE，则使用默认值 mxConstants.LINE_ARCSIZE。
//   - 最后一个参数 true 表示路径闭合，形成完整的三角形。
mxTriangle.prototype.redrawPath = function(c, x, y, w, h)
{
	var arcSize = mxUtils.getValue(this.style, mxConstants.STYLE_ARCSIZE, mxConstants.LINE_ARCSIZE) / 2;
    // 中文注释：获取圆角大小
    // 说明：从样式中获取圆角大小（STYLE_ARCSIZE），若未定义则使用默认值 LINE_ARCSIZE 的一半。

	this.addPoints(c, [new mxPoint(0, 0), new mxPoint(w, 0.5 * h), new mxPoint(0, h)], this.isRounded, arcSize, true);
    // 中文注释：绘制三角形路径
    // 说明：通过 addPoints 方法在画布上绘制三角形，指定三个顶点坐标。
    // 参数说明：
    //   - 顶点数组：[new mxPoint(0, 0), new mxPoint(w, 0.5 * h), new mxPoint(0, h)] 表示三角形的三个顶点。
    //   - isRounded：是否启用圆角，来自类的属性。
    //   - arcSize：圆角的大小。
    //   - true：表示路径闭合，形成完整的三角形。
};
