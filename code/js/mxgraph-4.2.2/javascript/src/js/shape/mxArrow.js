/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxArrow
 *
 * Extends <mxShape> to implement an arrow shape. (The shape
 * is used to represent edges, not vertices.)
 * This shape is registered under <mxConstants.SHAPE_ARROW>
 * in <mxCellRenderer>.
 * 
 * Constructor: mxArrow
 *
 * Constructs a new arrow shape.
 * 
 * Parameters:
 * 
 * points - Array of <mxPoints> that define the points. This is stored in
 * <mxShape.points>.
 * fill - String that defines the fill color. This is stored in <fill>.
 * stroke - String that defines the stroke color. This is stored in <stroke>.
 * strokewidth - Optional integer that defines the stroke width. Default is
 * 1. This is stored in <strokewidth>.
 * arrowWidth - Optional integer that defines the arrow width. Default is
 * <mxConstants.ARROW_WIDTH>. This is stored in <arrowWidth>.
 * spacing - Optional integer that defines the spacing between the arrow shape
 * and its endpoints. Default is <mxConstants.ARROW_SPACING>. This is stored in
 * <spacing>.
 * endSize - Optional integer that defines the size of the arrowhead. Default
 * is <mxConstants.ARROW_SIZE>. This is stored in <endSize>.
 */
// 中文注释：
// 类：mxArrow
// 继承自 mxShape，用于实现箭头形状（用于表示边，而不是顶点）。
// 该形状在 mxCellRenderer 中以 mxConstants.SHAPE_ARROW 注册。
// 构造函数：mxArrow
// 功能：创建新的箭头形状。
// 参数说明：
// points - 定义箭头路径的 mxPoints 数组，存储在 mxShape.points 中。
// fill - 定义填充颜色的字符串，存储在 fill 属性中。
// stroke - 定义描边颜色的字符串，存储在 stroke 属性中。
// strokewidth - 可选整数，定义描边宽度，默认为 1，存储在 strokewidth 属性中。
// arrowWidth - 可选整数，定义箭头宽度，默认为 mxConstants.ARROW_WIDTH，存储在 arrowWidth 属性中。
// spacing - 可选整数，定义箭头形状与端点之间的间距，默认为 mxConstants.ARROW_SPACING，存储在 spacing 属性中。
// endSize - 可选整数，定义箭头头部大小，默认为 mxConstants.ARROW_SIZE，存储在 endSize 属性中。
function mxArrow(points, fill, stroke, strokewidth, arrowWidth, spacing, endSize)
{
	mxShape.call(this);
	this.points = points;
	this.fill = fill;
	this.stroke = stroke;
	this.strokewidth = (strokewidth != null) ? strokewidth : 1;
	this.arrowWidth = (arrowWidth != null) ? arrowWidth : mxConstants.ARROW_WIDTH;
	this.spacing = (spacing != null) ? spacing : mxConstants.ARROW_SPACING;
	this.endSize = (endSize != null) ? endSize : mxConstants.ARROW_SIZE;
};

/**
 * Extends mxShape.
 */
// 中文注释：
// 功能：继承 mxShape 类，使 mxArrow 具备 mxShape 的所有功能。
mxUtils.extend(mxArrow, mxShape);

/**
 * Function: augmentBoundingBox
 *
 * Augments the bounding box with the edge width and markers.
 */
// 中文注释：
// 方法：augmentBoundingBox
// 功能：扩展边界框，考虑箭头的宽度和标记。
// 参数：
// bbox - 边界框对象。
// 逻辑说明：
// 1. 调用父类的 augmentBoundingBox 方法，继承其边界框计算逻辑。
// 2. 计算箭头宽度和箭头头部大小的最大值，结合描边宽度和缩放比例，扩展边界框。
mxArrow.prototype.augmentBoundingBox = function(bbox)
{
	mxShape.prototype.augmentBoundingBox.apply(this, arguments);
	
	var w = Math.max(this.arrowWidth, this.endSize);
	bbox.grow((w / 2 + this.strokewidth) * this.scale);
};

/**
 * Function: paintEdgeShape
 * 
 * Paints the line shape.
 */
// 中文注释：
// 方法：paintEdgeShape
// 功能：绘制箭头形状的线条。
// 参数：
// c - 画布上下文，用于绘制图形。
// pts - 点的数组，定义箭头的路径。
// 逻辑说明：
// 1. 定义箭头的几何参数，包括间距、宽度和箭头头部大小。
// 2. 计算起点和终点之间的向量，确定箭头长度和方向。
// 3. 计算法向量和逆法向量，用于绘制箭头的形状。
// 4. 根据几何计算，生成箭头的关键点坐标。
// 5. 使用画布上下文绘制箭头的路径，并填充和描边。
// 重要配置参数：
// - mxConstants.ARROW_SPACING：箭头与端点的间距。
// - mxConstants.ARROW_WIDTH：箭头宽度。
// - mxConstants.ARROW_SIZE：箭头头部大小。
// 特殊处理注意事项：
// - 确保箭头长度减去间距和箭头头部大小后仍为正值。
// - 路径点计算需精确以保证箭头形状的正确性。
// 样式设置说明：
// - 使用 fill 和 stroke 属性定义填充和描边颜色。
// - 描边宽度由 strokewidth 属性控制。
// 交互逻辑：
// - 该方法不直接处理用户交互，仅负责绘制箭头形状。
mxArrow.prototype.paintEdgeShape = function(c, pts)
{
	// Geometry of arrow
	var spacing =  mxConstants.ARROW_SPACING;
	var width = mxConstants.ARROW_WIDTH;
	var arrow = mxConstants.ARROW_SIZE;

	// Base vector (between end points)
	var p0 = pts[0];
	var pe = pts[pts.length - 1];
	var dx = pe.x - p0.x;
	var dy = pe.y - p0.y;
	var dist = Math.sqrt(dx * dx + dy * dy);
	var length = dist - 2 * spacing - arrow;
	
	// Computes the norm and the inverse norm
	var nx = dx / dist;
	var ny = dy / dist;
	var basex = length * nx;
	var basey = length * ny;
	var floorx = width * ny/3;
	var floory = -width * nx/3;
	
	// Computes points
	var p0x = p0.x - floorx / 2 + spacing * nx;
	var p0y = p0.y - floory / 2 + spacing * ny;
	var p1x = p0x + floorx;
	var p1y = p0y + floory;
	var p2x = p1x + basex;
	var p2y = p1y + basey;
	var p3x = p2x + floorx;
	var p3y = p2y + floory;
	// p4 not necessary
	var p5x = p3x - 3 * floorx;
	var p5y = p3y - 3 * floory;
	
    // 中文注释：
    // 关键变量说明：
    // - p0, pe：起点和终点的坐标。
    // - dx, dy：起点到终点的水平和垂直距离。
    // - dist：起点到终点的总距离。
    // - length：箭头主体长度（扣除间距和箭头头部）。
    // - nx, ny：单位方向向量。
    // - basex, basey：箭头主体的向量分量。
    // - floorx, floory：箭头宽度的偏移量，用于构建箭头形状。
    // - p0x, p0y 到 p5x, p5y：箭头路径的关键点坐标。
    // 绘制逻辑：
    // 1. 使用 c.begin() 开始新的路径。
    // 2. 从 p0x, p0y 开始，依次连接到 p1x, p1y, p2x, p2y, p3x, p3y 等点。
    // 3. 绘制到终点附近（考虑间距），然后连接到 p5x, p5y 形成箭头尖端。
    // 4. 使用 c.close() 闭合路径。
    // 5. 调用 c.fillAndStroke() 填充并描边路径，形成最终箭头形状。

	c.begin();
	c.moveTo(p0x, p0y);
	c.lineTo(p1x, p1y);
	c.lineTo(p2x, p2y);
	c.lineTo(p3x, p3y);
	c.lineTo(pe.x - spacing * nx, pe.y - spacing * ny);
	c.lineTo(p5x, p5y);
	c.lineTo(p5x + floorx, p5y + floory);
	c.close();

	c.fillAndStroke();
};
