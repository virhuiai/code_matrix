/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxRhombus
 *
 * Extends <mxShape> to implement a rhombus (aka diamond) shape.
 * This shape is registered under <mxConstants.SHAPE_RHOMBUS>
 * in <mxCellRenderer>.
 * 
 * Constructor: mxRhombus
 *
 * Constructs a new rhombus shape.
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
// 类：mxRhombus
// 继承自 mxShape，用于实现菱形（也称为钻石形）图形。
// 该图形在 mxCellRenderer 中以 mxConstants.SHAPE_RHOMBUS 注册。
// 构造函数：mxRhombus
// 用于创建一个新的菱形图形。
// 参数说明：
// - bounds: mxRectangle 类型，定义图形的边界，存储在 mxShape.bounds 中。
// - fill: 字符串，定义填充颜色，存储在 fill 属性中。
// - stroke: 字符串，定义描边颜色，存储在 stroke 属性中。
// - strokewidth: 可选整数，定义描边宽度，默认为 1，存储在 strokewidth 属性中。
function mxRhombus(bounds, fill, stroke, strokewidth)
{
	mxShape.call(this);
	this.bounds = bounds;
	this.fill = fill;
	this.stroke = stroke;
	this.strokewidth = (strokewidth != null) ? strokewidth : 1;
    // 中文注释：
    // 调用父类 mxShape 的构造函数，初始化菱形图形。
    // 将传入的边界、填充颜色、描边颜色和描边宽度存储到对应属性中。
    // 如果未提供描边宽度，默认为 1。
};

/**
 * Extends mxShape.
 */
// 中文注释：
// 使用 mxUtils.extend 方法使 mxRhombus 继承自 mxShape，确保具备父类的所有功能。
mxUtils.extend(mxRhombus, mxShape);

/**
 * Function: isRoundable
 * 
 * Adds roundable support.
 */
// 中文注释：
// 函数：isRoundable
// 提供对圆角的支持，返回 true 表示该图形支持圆角处理。
mxRhombus.prototype.isRoundable = function()
{
	return true;
    // 中文注释：
    // 返回值：布尔值 true，表示该菱形图形支持圆角效果。
};

/**
 * Function: paintVertexShape
 * 
 * Generic painting implementation.
 */
// 中文注释：
// 函数：paintVertexShape
// 实现菱形图形的绘制逻辑，负责在画布上渲染菱形。
// 参数说明：
// - c: 画布上下文，用于执行绘图操作。
// - x: 图形左上角的 x 坐标。
// - y: 图形左上角的 y 坐标。
// - w: 图形的宽度。
// - h: 图形的高度。
mxRhombus.prototype.paintVertexShape = function(c, x, y, w, h)
{
	var hw = w / 2;
	var hh = h / 2;
    // 中文注释：
    // 计算菱形的宽度和高度的中间值，用于定位菱形的四个顶点。
    // - hw: 宽度的一半。
    // - hh: 高度的一半。

	var arcSize = mxUtils.getValue(this.style, mxConstants.STYLE_ARCSIZE, mxConstants.LINE_ARCSIZE) / 2;
    // 中文注释：
    // 获取圆角大小（arcSize），从样式中读取 mxConstants.STYLE_ARCSIZE 的值，默认为 mxConstants.LINE_ARCSIZE。
    // 最终值除以 2 以适应绘制需求。
    // 重要配置参数：arcSize 控制圆角的弧度大小，影响菱形是否显示圆角效果。

	c.begin();
    // 中文注释：
    // 开始一个新的绘图路径，准备绘制菱形。

	this.addPoints(c, [new mxPoint(x + hw, y), new mxPoint(x + w, y + hh), new mxPoint(x + hw, y + h),
	     new mxPoint(x, y + hh)], this.isRounded, arcSize, true);
    // 中文注释：
    // 调用 addPoints 方法添加菱形的四个顶点，依次为：
    // - 顶部顶点：(x + hw, y)
    // - 右侧顶点：(x + w, y + hh)
    // - 底部顶点：(x + hw, y + h)
    // - 左侧顶点：(x, y + hh)
    // 参数说明：
    // - c: 画布上下文。
    // - 顶点数组：定义菱形的四个顶点坐标。
    // - this.isRounded: 布尔值，决定是否应用圆角效果。
    // - arcSize: 圆角大小，控制圆角的弧度。
    // - true: 表示闭合路径，形成完整图形。

	c.fillAndStroke();
    // 中文注释：
    // 执行填充和描边操作，完成菱形的渲染。
    // 填充颜色由 this.fill 指定，描边颜色和宽度分别由 this.stroke 和 this.strokewidth 指定。
};
