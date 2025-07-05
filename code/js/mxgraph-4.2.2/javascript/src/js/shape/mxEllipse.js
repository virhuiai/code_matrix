/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxEllipse
 *
 * Extends <mxShape> to implement an ellipse shape.
 * This shape is registered under <mxConstants.SHAPE_ELLIPSE>
 * in <mxCellRenderer>.
 * 
 * Constructor: mxEllipse
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
// 类：mxEllipse
// 继承自 mxShape，用于实现椭圆形状。
// 该形状在 mxCellRenderer 中以 mxConstants.SHAPE_ELLIPSE 注册。
// 构造函数：mxEllipse
// 用于创建新的椭圆形状实例。
// 参数说明：
// - bounds: mxRectangle 对象，定义椭圆的边界，存储在 mxShape.bounds 中。
// - fill: 字符串，定义填充颜色，存储在 fill 属性中。
// - stroke: 字符串，定义描边颜色，存储在 stroke 属性中。
// - strokewidth: 可选整数，定义描边宽度，默认值为 1，存储在 strokewidth 属性中。
function mxEllipse(bounds, fill, stroke, strokewidth)
{
	mxShape.call(this);
	this.bounds = bounds;
	this.fill = fill;
	this.stroke = stroke;
	this.strokewidth = (strokewidth != null) ? strokewidth : 1;
    // 中文注释：
    // 调用父类 mxShape 的构造函数，初始化椭圆形状。
    // 将传入的边界、填充颜色、描边颜色和描边宽度存储到实例属性中。
    // 如果未提供描边宽度，默认为 1。
    // 重要配置参数：
    // - bounds：定义椭圆的矩形边界，包含位置和大小信息。
    // - fill：设置椭圆的填充颜色，影响内部区域的视觉效果。
    // - stroke：设置椭圆的描边颜色，影响边框的视觉效果。
    // - strokewidth：设置描边宽度，控制边框粗细，默认值为 1。
};

/**
 * Extends mxShape.
 */
// 中文注释：
// 通过 mxUtils.extend 方法使 mxEllipse 继承 mxShape 的属性和方法。
// 目的：确保 mxEllipse 具备 mxShape 的所有功能，并在此基础上扩展椭圆特有的功能。
mxUtils.extend(mxEllipse, mxShape);

/**
 * Function: paintVertexShape
 * 
 * Paints the ellipse shape.
 */
// 中文注释：
// 函数：paintVertexShape
// 用于绘制椭圆形状。
// 参数说明：
// - c: 画布上下文对象，用于执行绘制操作。
// - x: 椭圆左上角的 x 坐标。
// - y: 椭圆左上角的 y 坐标。
// - w: 椭圆的宽度。
// - h: 椭圆的高度。
// 方法目的：根据提供的坐标和尺寸，在画布上绘制椭圆，并应用填充和描边样式。
// 交互逻辑：通过画布上下文 c 调用 ellipse 方法绘制椭圆形状，随后应用填充和描边。
// 特殊处理注意事项：
// - 确保画布上下文 c 已正确初始化。
// - 填充和描边样式依赖于构造函数中设置的 fill 和 stroke 属性。
mxEllipse.prototype.paintVertexShape = function(c, x, y, w, h)
{
	c.ellipse(x, y, w, h);
    // 中文注释：
    // 调用画布上下文的 ellipse 方法，绘制指定位置和大小的椭圆。
    // 参数 x, y 定义椭圆左上角的位置，w, h 定义椭圆的宽度和高度。

	c.fillAndStroke();
    // 中文注释：
    // 调用 fillAndStroke 方法，根据构造函数中设置的 fill 和 stroke 属性，
    // 对椭圆进行填充和描边操作，完成绘制。
    // 样式设置说明：
    // - 填充颜色由 this.fill 决定。
    // - 描边颜色由 this.stroke 决定。
    // - 描边宽度由 this.strokewidth 决定。
};
