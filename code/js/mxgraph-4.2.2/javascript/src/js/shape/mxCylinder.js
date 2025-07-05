/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxCylinder
 *
 * Extends <mxShape> to implement an cylinder shape. If a
 * custom shape with one filled area and an overlay path is
 * needed, then this shape's <redrawPath> should be overridden.
 * This shape is registered under <mxConstants.SHAPE_CYLINDER>
 * in <mxCellRenderer>.
 * 
 * Constructor: mxCylinder
 *
 * Constructs a new cylinder shape.
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
// mxCylinder 类继承自 mxShape，用于实现圆柱体形状。
// 如果需要自定义包含一个填充区域和覆盖路径的形状，应重写 redrawPath 方法。
// 该形状在 mxCellRenderer 中以 mxConstants.SHAPE_CYLINDER 注册。
// 构造函数 mxCylinder 用于创建新的圆柱体形状。
// 参数说明：
// - bounds: mxRectangle 类型，定义形状的边界，存储在 mxShape.bounds 中。
// - fill: 字符串，定义填充颜色，存储在 fill 属性中。
// - stroke: 字符串，定义描边颜色，存储在 stroke 属性中。
// - strokewidth: 可选整数，定义描边宽度，默认为 1，存储在 strokewidth 属性中。
function mxCylinder(bounds, fill, stroke, strokewidth)
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
// 中文注释：
// 通过 mxUtils.extend 使 mxCylinder 继承 mxShape，提供基础形状功能。
mxUtils.extend(mxCylinder, mxShape);

/**
 * Variable: maxHeight
 *
 * Defines the maximum height of the top and bottom part
 * of the cylinder shape.
 */
// 中文注释：
// maxHeight: 定义圆柱体顶部和底部椭圆部分的最大高度，值为 40。
// 重要配置参数：限制圆柱体上下部分的尺寸，影响形状外观。
mxCylinder.prototype.maxHeight = 40;

/**
 * Variable: svgStrokeTolerance
 *
 * Sets stroke tolerance to 0 for SVG.
 */
// 中文注释：
// svgStrokeTolerance: SVG 描边容差，设置为 0。
// 重要配置参数：确保 SVG 渲染时描边精确，无额外容差。
mxCylinder.prototype.svgStrokeTolerance = 0;

/**
 * Function: paintVertexShape
 * 
 * Redirects to redrawPath for subclasses to work.
 */
// 中文注释：
// paintVertexShape: 绘制圆柱体形状的方法，重定向到 redrawPath。
// 方法目的：为子类提供绘制路径的入口，支持自定义形状。
// 关键步骤：
// 1. 将画布平移到指定坐标 (x, y)。
// 2. 调用 redrawPath 绘制路径，支持填充和描边。
// 3. 根据 outline 和样式设置，决定是否绘制阴影或描边。
// 交互逻辑：根据样式配置（STYLE_BACKGROUND_OUTLINE）动态调整绘制行为。
// 特殊处理：当 outline 为 false 或样式中 STYLE_BACKGROUND_OUTLINE 为 0 时，禁用阴影。
mxCylinder.prototype.paintVertexShape = function(c, x, y, w, h)
{
	c.translate(x, y);
	c.begin();
	this.redrawPath(c, x, y, w, h, false);
	c.fillAndStroke();
	
	if (!this.outline || this.style == null || mxUtils.getValue(
		this.style, mxConstants.STYLE_BACKGROUND_OUTLINE, 0) == 0)
	{
		c.setShadow(false);
		c.begin();
		this.redrawPath(c, x, y, w, h, true);
		c.stroke();
	}
};

/**
 * Function: getCylinderSize
 *
 * Returns the cylinder size.
 */
// 中文注释：
// getCylinderSize: 返回圆柱体顶部和底部的椭圆高度。
// 方法目的：计算圆柱体上下椭圆部分的高度，用于绘制。
// 参数说明：
// - x, y: 形状的起始坐标。
// - w, h: 形状的宽度和高度。
// 返回值：取 maxHeight 和 h/5 的较小值，确保椭圆高度适配形状。
// 关键变量：maxHeight 限制椭圆最大高度，h/5 动态调整比例。
mxCylinder.prototype.getCylinderSize = function(x, y, w, h)
{
	return Math.min(this.maxHeight, Math.round(h / 5));
};

/**
 * Function: redrawPath
 *
 * Draws the path for this shape.
 */
// 中文注释：
// redrawPath: 绘制圆柱体形状的路径。
// 方法目的：定义圆柱体的具体绘制路径，包括顶部椭圆和主体。
// 参数说明：
// - c: 画布上下文，用于绘制路径。
// - x, y: 形状的起始坐标。
// - w, h: 形状的宽度和高度。
// - isForeground: 布尔值，决定绘制前景（描边）还是背景（填充）。
// 关键步骤：
// 1. 计算圆柱体顶部和底部的椭圆高度（dy）。
// 2. 根据 isForeground 和 fill 属性，决定绘制顶部椭圆的路径。
// 3. 若非前景模式，绘制圆柱体主体路径，包括侧面和底部椭圆。
// 交互逻辑：通过 isForeground 参数区分前景和背景绘制逻辑。
// 特殊处理：
// - 分离前景和背景路径，确保点击检测（hit-detection）准确。
// - 使用 curveTo 绘制平滑的椭圆曲线，lineTo 绘制直线侧面。
// 样式设置：路径支持填充（fill）和描边（stroke），由构造函数参数控制。
mxCylinder.prototype.redrawPath = function(c, x, y, w, h, isForeground)
{
	var dy = this.getCylinderSize(x, y, w, h);
	
	if ((isForeground && this.fill != null) || (!isForeground && this.fill == null))
	{
		c.moveTo(0, dy);
		c.curveTo(0, 2 * dy, w, 2 * dy, w, dy);
		
		// Needs separate shapes for correct hit-detection
        // 中文注释：分离形状以确保点击检测准确。
		if (!isForeground)
		{
			c.stroke();
			c.begin();
		}
	}
	
	if (!isForeground)
	{
		c.moveTo(0, dy);
		c.curveTo(0, -dy / 3, w, -dy / 3, w, dy);
		c.lineTo(w, h - dy);
		c.curveTo(w, h + dy / 3, 0, h + dy / 3, 0, h - dy);
		c.close();
	}
};
