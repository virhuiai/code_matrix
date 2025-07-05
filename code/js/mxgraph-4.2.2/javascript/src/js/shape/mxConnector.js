/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxConnector
 * 
 * Extends <mxShape> to implement a connector shape. The connector
 * shape allows for arrow heads on either side.
 * 
 * This shape is registered under <mxConstants.SHAPE_CONNECTOR> in
 * <mxCellRenderer>.
 * 
 * Constructor: mxConnector
 * 
 * Constructs a new connector shape.
 * 
 * Parameters:
 * 
 * points - Array of <mxPoints> that define the points. This is stored in
 * <mxShape.points>.
 * stroke - String that defines the stroke color. This is stored in <stroke>.
 * Default is 'black'.
 * strokewidth - Optional integer that defines the stroke width. Default is
 * 1. This is stored in <strokewidth>.
 */
// 中文注释：
// mxConnector类继承自mxShape，用于实现连接器形状，支持两端添加箭头。
// 该形状在mxCellRenderer中以mxConstants.SHAPE_CONNECTOR注册。
// 构造函数：mxConnector
// 功能：创建新的连接器形状。
// 参数说明：
// - points：mxPoints数组，定义连接器的点，存储在mxShape.points中。
// - stroke：字符串，定义描边颜色，存储在stroke属性中，默认值为'black'。
// - strokewidth：可选整数，定义描边宽度，默认值为1，存储在strokewidth属性中。
function mxConnector(points, stroke, strokewidth)
{
	mxPolyline.call(this, points, stroke, strokewidth);
};

/**
 * Extends mxPolyline.
 */
// 中文注释：mxConnector继承自mxPolyline，扩展了其功能以支持连接器形状。
mxUtils.extend(mxConnector, mxPolyline);

/**
 * Function: updateBoundingBox
 *
 * Updates the <boundingBox> for this shape using <createBoundingBox> and
 * <augmentBoundingBox> and stores the result in <boundingBox>.
 */
// 中文注释：
// 方法：updateBoundingBox
// 功能：更新此形状的边界框（boundingBox）。
// 实现：通过调用createBoundingBox和augmentBoundingBox方法更新边界框，并将结果存储在boundingBox属性中。
// 关键逻辑：检查样式中是否设置了曲线（STYLE_CURVED），决定是否使用SVG边界框。
mxConnector.prototype.updateBoundingBox = function()
{
	this.useSvgBoundingBox = this.style != null && this.style[mxConstants.STYLE_CURVED] == 1;
    // 中文注释：根据样式中的mxConstants.STYLE_CURVED属性决定是否使用SVG边界框。
	mxShape.prototype.updateBoundingBox.apply(this, arguments);
    // 中文注释：调用父类mxShape的updateBoundingBox方法，完成边界框的更新。
};

/**
 * Function: paintEdgeShape
 * 
 * Paints the line shape.
 */
// 中文注释：
// 方法：paintEdgeShape
// 功能：绘制连接器的线条形状。
// 实现：
// 1. 创建起点和终点的箭头标记。
// 2. 调用父类的paintEdgeShape方法绘制线条。
// 3. 设置绘制标记时的样式（禁用阴影、虚线，并设置填充颜色）。
// 4. 如果存在起点或终点标记，调用相应的标记绘制函数。
// 注意事项：标记的绘制在主线条绘制之后进行，以确保正确的渲染顺序。
mxConnector.prototype.paintEdgeShape = function(c, pts)
{
	// The indirection via functions for markers is needed in
	// order to apply the offsets before painting the line and
	// paint the markers after painting the line.
    // 中文注释：通过函数间接调用标记是为了在绘制线条前应用偏移量，并在绘制线条后绘制标记。
	var sourceMarker = this.createMarker(c, pts, true);
    // 中文注释：创建起点处的箭头标记，source=true表示起点。
	var targetMarker = this.createMarker(c, pts, false);
    // 中文注释：创建终点处的箭头标记，source=false表示终点。

	mxPolyline.prototype.paintEdgeShape.apply(this, arguments);
    // 中文注释：调用父类mxPolyline的paintEdgeShape方法绘制线条主体。

	// Disables shadows, dashed styles and fixes fill color for markers
    // 中文注释：禁用阴影和虚线样式，并为标记设置填充颜色。
	c.setFillColor(this.stroke);
    // 中文注释：设置填充颜色为描边颜色，确保标记颜色与线条一致。
	c.setShadow(false);
    // 中文注释：禁用阴影效果，避免标记出现阴影。
	c.setDashed(false);
    // 中文注释：禁用虚线样式，确保标记为实线。

	if (sourceMarker != null)
	{
		sourceMarker();
        // 中文注释：如果存在起点标记，调用其绘制函数。
	}
	
	if (targetMarker != null)
	{
		targetMarker();
        // 中文注释：如果存在终点标记，调用其绘制函数。
	}
};

/**
 * Function: createMarker
 * 
 * Prepares the marker by adding offsets in pts and returning a function to
 * paint the marker.
 */
// 中文注释：
// 方法：createMarker
// 功能：准备绘制箭头标记，通过在点数组中添加偏移量并返回绘制标记的函数。
// 参数说明：
// - c：画布上下文，用于绘制。
// - pts：点数组，定义连接器的路径点。
// - source：布尔值，true表示起点标记，false表示终点标记。
// 实现：
// 1. 根据source参数选择起点或终点箭头类型。
// 2. 计算标记的方向和大小。
// 3. 返回用于绘制标记的函数。
// 注意事项：确保选择非重叠点以正确计算方向向量。
mxConnector.prototype.createMarker = function(c, pts, source)
{
	var result = null;
	var n = pts.length;
    // 中文注释：获取点数组的长度。
	var type = mxUtils.getValue(this.style, (source) ? mxConstants.STYLE_STARTARROW : mxConstants.STYLE_ENDARROW);
    // 中文注释：获取箭头类型，起点使用STYLE_STARTARROW，终点使用STYLE_ENDARROW。
	var p0 = (source) ? pts[1] : pts[n - 2];
    // 中文注释：选择起点或终点的参考点，起点取pts[1]，终点取pts[n-2]。
	var pe = (source) ? pts[0] : pts[n - 1];
    // 中文注释：选择起点或终点的结束点，起点取pts[0]，终点取pts[n-1]。

	if (type != null && p0 != null && pe != null)
	{
		var count = 1;
        // 中文注释：初始化计数器，用于选择非重叠点。

		// Uses next non-overlapping point
		while (count < n - 1 && Math.round(p0.x - pe.x) == 0 && Math.round(p0.y - pe.y) == 0)
		{
			p0 = (source) ? pts[1 + count] : pts[n - 2 - count];
			count++;
            // 中文注释：循环查找下一个非重叠点，确保方向向量计算准确。
		}
	
		// Computes the norm and the inverse norm
		var dx = pe.x - p0.x;
		var dy = pe.y - p0.y;
        // 中文注释：计算方向向量的x和y分量。

		var dist = Math.max(1, Math.sqrt(dx * dx + dy * dy));
        // 中文注释：计算方向向量的长度（模），避免除以零。

		var unitX = dx / dist;
		var unitY = dy / dist;
        // 中文注释：计算单位方向向量，用于确定标记方向。

		var size = mxUtils.getNumber(this.style, (source) ? mxConstants.STYLE_STARTSIZE : mxConstants.STYLE_ENDSIZE, mxConstants.DEFAULT_MARKERSIZE);
        // 中文注释：获取箭头大小，起点使用STYLE_STARTSIZE，终点使用STYLE_ENDSIZE，默认值为mxConstants.DEFAULT_MARKERSIZE。

		// Allow for stroke width in the end point used and the 
		// orthogonal vectors describing the direction of the marker
		var filled = this.style[(source) ? mxConstants.STYLE_STARTFILL : mxConstants.STYLE_ENDFILL] != 0;
        // 中文注释：检查是否填充箭头，起点使用STYLE_STARTFILL，终点使用STYLE_ENDFILL。

		result = mxMarker.createMarker(c, this, type, pe, unitX, unitY, size, source, this.strokewidth, filled);
        // 中文注释：调用mxMarker.createMarker创建标记，返回绘制标记的函数。
	}
	
	return result;
    // 中文注释：返回绘制标记的函数，若无有效箭头类型或点，则返回null。
};

/**
 * Function: augmentBoundingBox
 *
 * Augments the bounding box with the strokewidth and shadow offsets.
 */
// 中文注释：
// 方法：augmentBoundingBox
// 功能：扩展边界框，考虑描边宽度和阴影偏移量。
// 参数说明：
// - bbox：边界框对象，将被扩展。
// 实现：
// 1. 调用父类的augmentBoundingBox方法。
// 2. 根据起点和终点箭头的大小扩展边界框。
// 注意事项：箭头大小会影响边界框的扩展，确保包含所有视觉元素。
mxConnector.prototype.augmentBoundingBox = function(bbox)
{
	mxShape.prototype.augmentBoundingBox.apply(this, arguments);
    // 中文注释：调用父类mxShape的augmentBoundingBox方法，处理基本的边界框扩展。

	// Adds marker sizes
	var size = 0;
    // 中文注释：初始化箭头大小变量。

	if (mxUtils.getValue(this.style, mxConstants.STYLE_STARTARROW, mxConstants.NONE) != mxConstants.NONE)
	{
		size = mxUtils.getNumber(this.style, mxConstants.STYLE_STARTSIZE, mxConstants.DEFAULT_MARKERSIZE) + 1;
        // 中文注释：如果起点有箭头，获取其大小（STYLE_STARTSIZE），并加1以确保边界框足够大。
	}
	
	if (mxUtils.getValue(this.style, mxConstants.STYLE_ENDARROW, mxConstants.NONE) != mxConstants.NONE)
	{
		size = Math.max(size, mxUtils.getNumber(this.style, mxConstants.STYLE_ENDSIZE, mxConstants.DEFAULT_MARKERSIZE)) + 1;
        // 中文注释：如果终点有箭头，获取其大小（STYLE_ENDSIZE），取较大值并加1。
	}
	
	bbox.grow(size * this.scale);
    // 中文注释：根据箭头大小和缩放比例扩展边界框，确保包含箭头。
};
