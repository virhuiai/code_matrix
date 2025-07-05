/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxGeometry
 * 
 * Extends <mxRectangle> to represent the geometry of a cell.
 * 
 * For vertices, the geometry consists of the x- and y-location, and the width
 * and height. For edges, the geometry consists of the optional terminal- and
 * control points. The terminal points are only required if an edge is
 * unconnected, and are stored in the <sourcePoint> and <targetPoint>
 * variables, respectively.
 * 
 * Example:
 * 
 * If an edge is unconnected, that is, it has no source or target terminal,
 * then a geometry with terminal points for a new edge can be defined as
 * follows.
 * 
 * (code)
 * geometry.setTerminalPoint(new mxPoint(x1, y1), true);
 * geometry.points = [new mxPoint(x2, y2)];
 * geometry.setTerminalPoint(new mxPoint(x3, y3), false);
 * (end)
 * 
 * Control points are used regardless of the connected state of an edge and may
 * be ignored or interpreted differently depending on the edge's <mxEdgeStyle>.
 * 
 * To disable automatic reset of control points after a cell has been moved or
 * resized, the the <mxGraph.resizeEdgesOnMove> and
 * <mxGraph.resetEdgesOnResize> may be used.
 *
 * Edge Labels:
 * 
 * Using the x- and y-coordinates of a cell's geometry, it is possible to
 * position the label on edges on a specific location on the actual edge shape
 * as it appears on the screen. The x-coordinate of an edge's geometry is used
 * to describe the distance from the center of the edge from -1 to 1 with 0
 * being the center of the edge and the default value. The y-coordinate of an
 * edge's geometry is used to describe the absolute, orthogonal distance in
 * pixels from that point. In addition, the <mxGeometry.offset> is used as an
 * absolute offset vector from the resulting point.
 * 
 * This coordinate system is applied if <relative> is true, otherwise the
 * offset defines the absolute vector from the edge's center point to the
 * label and the values for <x> and <y> are ignored.
 * 
 * The width and height parameter for edge geometries can be used to set the
 * label width and height (eg. for word wrapping).
 * 
 * Ports:
 * 
 * The term "port" refers to a relatively positioned, connectable child cell,
 * which is used to specify the connection between the parent and another cell
 * in the graph. Ports are typically modeled as vertices with relative
 * geometries.
 * 
 * Offsets:
 * 
 * The <offset> field is interpreted in 3 different ways, depending on the cell
 * and the geometry. For edges, the offset defines the absolute offset for the
 * edge label. For relative geometries, the offset defines the absolute offset
 * for the origin (top, left corner) of the vertex, otherwise the offset
 * defines the absolute offset for the label inside the vertex or group.
 * 
 * Constructor: mxGeometry
 *
 * Constructs a new object to describe the size and location of a vertex or
 * the control points of an edge.
 */
// 中文注释：mxGeometry 类继承自 mxRectangle，用于描述单元格的几何信息。
// 对于顶点，几何信息包括 x、y 坐标及宽度和高度；对于边，几何信息包括可选的起点、终点和控制点。
// 构造函数用于初始化一个顶点的位置和大小，或定义边的控制点。
function mxGeometry(x, y, width, height)
{
	mxRectangle.call(this, x, y, width, height);
    // 中文注释：调用父类 mxRectangle 的构造函数，初始化 x、y 坐标及宽度和高度。
};

/**
 * Extends mxRectangle.
 */
// 中文注释：mxGeometry 继承自 mxRectangle，扩展了其功能以支持顶点和边的几何描述。
mxGeometry.prototype = new mxRectangle();
mxGeometry.prototype.constructor = mxGeometry;

/**
 * Variable: TRANSLATE_CONTROL_POINTS
 * 
 * Global switch to translate the points in translate. Default is true.
 */
// 中文注释：全局开关 TRANSLATE_CONTROL_POINTS，控制在平移操作中是否移动控制点，默认为 true。
// 重要配置参数：决定控制点是否随平移操作移动，影响边的几何变换行为。
mxGeometry.prototype.TRANSLATE_CONTROL_POINTS = true;

/**
 * Variable: alternateBounds
 *
 * Stores alternate values for x, y, width and height in a rectangle. See
 * <swap> to exchange the values. Default is null.
 */
// 中文注释：alternateBounds 存储 x、y、宽度和高度的备用值，以矩形形式保存。
// 可通过 swap 方法交换当前值与备用值，默认为 null。
// 关键变量用途：用于临时存储几何信息的备用值，便于在特定场景下切换几何状态。
mxGeometry.prototype.alternateBounds = null;

/**
 * Variable: sourcePoint
 *
 * Defines the source <mxPoint> of the edge. This is used if the
 * corresponding edge does not have a source vertex. Otherwise it is
 * ignored. Default is  null.
 */
// 中文注释：sourcePoint 定义边的起点坐标（mxPoint 类型）。
// 当边没有关联源顶点时使用，否则忽略，默认为 null。
// 关键变量用途：存储未连接边的起点坐标，用于定义边的几何路径。
mxGeometry.prototype.sourcePoint = null;

/**
 * Variable: targetPoint
 *
 * Defines the target <mxPoint> of the edge. This is used if the
 * corresponding edge does not have a target vertex. Otherwise it is
 * ignored. Default is null.
 */
// 中文注释：targetPoint 定义边的终点坐标（mxPoint 类型）。
// 当边没有关联目标顶点时使用，否则忽略，默认为 null。
// 关键变量用途：存储未连接边的终点坐标，用于定义边的几何路径。
mxGeometry.prototype.targetPoint = null;

/**
 * Variable: points
 *
 * Array of <mxPoints> which specifies the control points along the edge.
 * These points are the intermediate points on the edge, for the endpoints
 * use <targetPoint> and <sourcePoint> or set the terminals of the edge to
 * a non-null value. Default is null.
 */
// 中文注释：points 是一个 mxPoint 数组，指定边上的控制点。
// 这些控制点是边的中间点，端点使用 sourcePoint 和 targetPoint 定义，默认为 null。
// 关键变量用途：定义边的中间控制点，控制边的形状和路径。
mxGeometry.prototype.points = null;

/**
 * Variable: offset
 *
 * For edges, this holds the offset (in pixels) from the position defined
 * by <x> and <y> on the edge. For relative geometries (for vertices), this
 * defines the absolute offset from the point defined by the relative
 * coordinates. For absolute geometries (for vertices), this defines the
 * offset for the label. Default is null.
 */
// 中文注释：offset 定义偏移量（像素单位）。
// 对于边，表示边标签相对于 x、y 位置的绝对偏移；对于相对几何（顶点），表示相对于相对坐标的绝对偏移；
// 对于绝对几何（顶点），表示标签的绝对偏移，默认为 null。
// 关键变量用途：控制边标签或顶点位置的偏移，影响显示位置。
mxGeometry.prototype.offset = null;

/**
 * Variable: relative
 *
 * Specifies if the coordinates in the geometry are to be interpreted as
 * relative coordinates. For edges, this is used to define the location of
 * the edge label relative to the edge as rendered on the display. For
 * vertices, this specifies the relative location inside the bounds of the
 * parent cell.
 * 
 * If this is false, then the coordinates are relative to the origin of the
 * parent cell or, for edges, the edge label position is relative to the
 * center of the edge as rendered on screen.
 * 
 * Default is false.
 */
// 中文注释：relative 指定几何坐标是否为相对坐标。
// 对于边，决定标签相对于边在屏幕上的位置；对于顶点，决定在父单元格边界内的相对位置。
// 如果为 false，坐标相对于父单元格原点或边中心，默认为 false。
// 重要配置参数：决定坐标的解释方式，影响标签和顶点的位置计算。
mxGeometry.prototype.relative = false;

/**
 * Function: swap
 * 
 * Swaps the x, y, width and height with the values stored in
 * <alternateBounds> and puts the previous values into <alternateBounds> as
 * a rectangle. This operation is carried-out in-place, that is, using the
 * existing geometry instance. If this operation is called during a graph
 * model transactional change, then the geometry should be cloned before
 * calling this method and setting the geometry of the cell using
 * <mxGraphModel.setGeometry>.
 */
// 中文注释：swap 方法将当前的 x、y、宽度和高度与 alternateBounds 中的值交换，并将原值存入 alternateBounds。
// 该操作直接修改当前几何实例，建议在图形模型事务更改时克隆几何对象。
// 方法目的：快速切换当前几何信息与备用几何信息。
// 特殊处理注意事项：在事务性更改中调用时需克隆几何对象以避免意外修改。
mxGeometry.prototype.swap = function()
{
	if (this.alternateBounds != null)
	{
		var old = new mxRectangle(
			this.x, this.y, this.width, this.height);
        // 中文注释：创建当前几何值的副本，存储 x、y、宽度和高度。

		this.x = this.alternateBounds.x;
		this.y = this.alternateBounds.y;
		this.width = this.alternateBounds.width;
		this.height = this.alternateBounds.height;
        // 中文注释：将 alternateBounds 的值赋给当前几何属性。

		this.alternateBounds = old;
        // 中文注释：将原几何值存储到 alternateBounds 中。
	}
};

/**
 * Function: getTerminalPoint
 * 
 * Returns the <mxPoint> representing the source or target point of this
 * edge. This is only used if the edge has no source or target vertex.
 * 
 * Parameters:
 * 
 * isSource - Boolean that specifies if the source or target point
 * should be returned.
 */
// 中文注释：getTerminalPoint 方法返回边的起点或终点（mxPoint 类型）。
// 仅在边没有源顶点或目标顶点时使用。
// 参数说明：isSource - 布尔值，true 返回起点，false 返回终点。
// 方法目的：获取未连接边的起点或终点坐标。
mxGeometry.prototype.getTerminalPoint = function(isSource)
{
	return (isSource) ? this.sourcePoint : this.targetPoint;
    // 中文注释：根据 isSource 参数返回 sourcePoint 或 targetPoint。
};

/**
 * Function: setTerminalPoint
 * 
 * Sets the <sourcePoint> or <targetPoint> to the given <mxPoint> and
 * returns the new point.
 * 
 * Parameters:
 * 
 * point - Point to be used as the new source or target point.
 * isSource - Boolean that specifies if the source or target point
 * should be set.
 */
// 中文注释：setTerminalPoint 方法设置边的起点或终点为指定 mxPoint，并返回该点。
// 参数说明：point - 新的起点或终点坐标；isSource - 布尔值，true 设置起点，false 设置终点。
// 方法目的：更新未连接边的起点或终点坐标。
mxGeometry.prototype.setTerminalPoint = function(point, isSource)
{
	if (isSource)
	{
		this.sourcePoint = point;
        // 中文注释：设置 sourcePoint 为指定点。
	}
	else
	{
		this.targetPoint = point;
        // 中文注释：设置 targetPoint 为指定点。
	}
	
	return point;
    // 中文注释：返回设置的点。
};

/**
 * Function: rotate
 * 
 * Rotates the geometry by the given angle around the given center. That is,
 * <x> and <y> of the geometry, the <sourcePoint>, <targetPoint> and all
 * <points> are translated by the given amount. <x> and <y> are only
 * translated if <relative> is false.
 * 
 * Parameters:
 * 
 * angle - Number that specifies the rotation angle in degrees.
 * cx - <mxPoint> that specifies the center of the rotation.
 */
// 中文注释：rotate 方法将几何对象绕指定中心点旋转指定角度。
// 旋转影响几何的 x、y、sourcePoint、targetPoint 和所有控制点。
// 如果 relative 为 false，则 x、y 也会被旋转。
// 参数说明：angle - 旋转角度（度）；cx - 旋转中心点（mxPoint 类型）。
// 方法目的：实现几何对象的旋转变换。
// 特殊处理注意事项：relative 属性决定是否旋转 x、y 坐标。
mxGeometry.prototype.rotate = function(angle, cx)
{
	var rad = mxUtils.toRadians(angle);
	var cos = Math.cos(rad);
	var sin = Math.sin(rad);
    // 中文注释：将角度转换为弧度，并计算余弦和正弦值，用于旋转计算。

	// Rotates the geometry
	if (!this.relative)
	{
		var ct = new mxPoint(this.getCenterX(), this.getCenterY());
		var pt = mxUtils.getRotatedPoint(ct, cos, sin, cx);
		
		this.x = Math.round(pt.x - this.width / 2);
		this.y = Math.round(pt.y - this.height / 2);
        // 中文注释：如果 relative 为 false，计算几何中心点，旋转后更新 x、y 坐标。
	}

	// Rotates the source point
	if (this.sourcePoint != null)
	{
		var pt = mxUtils.getRotatedPoint(this.sourcePoint, cos, sin, cx);
		this.sourcePoint.x = Math.round(pt.x);
		this.sourcePoint.y = Math.round(pt.y);
        // 中文注释：旋转 sourcePoint 坐标并更新。
	}
	
	// Translates the target point
	if (this.targetPoint != null)
	{
		var pt = mxUtils.getRotatedPoint(this.targetPoint, cos, sin, cx);
		this.targetPoint.x = Math.round(pt.x);
		this.targetPoint.y = Math.round(pt.y);	
        // 中文注释：旋转 targetPoint 坐标并更新。
	}
	
	// Translate the control points
	if (this.points != null)
	{
		for (var i = 0; i < this.points.length; i++)
		{
			if (this.points[i] != null)
			{
				var pt = mxUtils.getRotatedPoint(this.points[i], cos, sin, cx);
				this.points[i].x = Math.round(pt.x);
				this.points[i].y = Math.round(pt.y);
                // 中文注释：遍历并旋转所有控制点坐标。
			}
		}
	}
};

/**
 * Function: translate
 * 
 * Translates the geometry by the specified amount. That is, <x> and <y> of the
 * geometry, the <sourcePoint>, <targetPoint> and all <points> are translated
 * by the given amount. <x> and <y> are only translated if <relative> is false.
 * If <TRANSLATE_CONTROL_POINTS> is false, then <points> are not modified by
 * this function.
 * 
 * Parameters:
 * 
 * dx - Number that specifies the x-coordinate of the translation.
 * dy - Number that specifies the y-coordinate of the translation.
 */
// 中文注释：translate 方法将几何对象平移指定距离。
// 平移影响 x、y、sourcePoint、targetPoint 和所有控制点（如果 TRANSLATE_CONTROL_POINTS 为 true）。
// 如果 relative 为 false，则 x、y 也会被平移。
// 参数说明：dx - x 方向平移距离；dy - y 方向平移距离。
// 方法目的：实现几何对象的平移变换。
// 特殊处理注意事项：TRANSLATE_CONTROL_POINTS 控制是否平移控制点。
mxGeometry.prototype.translate = function(dx, dy)
{
	dx = parseFloat(dx);
	dy = parseFloat(dy);
    // 中文注释：将平移距离转换为浮点数，确保计算精度。

	// Translates the geometry
	if (!this.relative)
	{
		this.x = parseFloat(this.x) + dx;
		this.y = parseFloat(this.y) + dy;
        // 中文注释：如果 relative 为 false，平移 x、y 坐标。
	}

	// Translates the source point
	if (this.sourcePoint != null)
	{
		this.sourcePoint.x = parseFloat(this.sourcePoint.x) + dx;
		this.sourcePoint.y = parseFloat(this.sourcePoint.y) + dy;
        // 中文注释：平移 sourcePoint 坐标。
	}
	
	// Translates the target point
	if (this.targetPoint != null)
	{
		this.targetPoint.x = parseFloat(this.targetPoint.x) + dx;
		this.targetPoint.y = parseFloat(this.targetPoint.y) + dy;		
        // 中文注释：平移 targetPoint 坐标。
	}

	// Translate the control points
	if (this.TRANSLATE_CONTROL_POINTS && this.points != null)
	{
		for (var i = 0; i < this.points.length; i++)
		{
			if (this.points[i] != null)
			{
				this.points[i].x = parseFloat(this.points[i].x) + dx;
				this.points[i].y = parseFloat(this.points[i].y) + dy;
                // 中文注释：如果 TRANSLATE_CONTROL_POINTS 为 true，平移所有控制点坐标。
			}
		}
	}
};

/**
 * Function: scale
 * 
 * Scales the geometry by the given amount. That is, <x> and <y> of the
 * geometry, the <sourcePoint>, <targetPoint> and all <points> are scaled
 * by the given amount. <x>, <y>, <width> and <height> are only scaled if
 * <relative> is false. If <fixedAspect> is true, then the smaller value
 * is used to scale the width and the height.
 * 
 * Parameters:
 * 
 * sx - Number that specifies the horizontal scale factor.
 * sy - Number that specifies the vertical scale factor.
 * fixedAspect - Optional boolean to keep the aspect ratio fixed.
 */
// 中文注释：scale 方法按指定比例缩放几何对象。
// 缩放影响 x、y、sourcePoint、targetPoint 和所有控制点。
// 如果 relative 为 false，则 x、y、宽度和高度也会被缩放。
// 如果 fixedAspect 为 true，使用较小的缩放比例以保持宽高比。
// 参数说明：sx - 水平缩放比例；sy - 垂直缩放比例；fixedAspect - 可选布尔值，是否固定宽高比。
// 方法目的：实现几何对象的缩放变换。
// 特殊处理注意事项：fixedAspect 参数控制是否保持宽高比。
mxGeometry.prototype.scale = function(sx, sy, fixedAspect)
{
	sx = parseFloat(sx);
	sy = parseFloat(sy);
    // 中文注释：将缩放比例转换为浮点数，确保计算精度。

	// Translates the source point
	if (this.sourcePoint != null)
	{
		this.sourcePoint.x = parseFloat(this.sourcePoint.x) * sx;
		this.sourcePoint.y = parseFloat(this.sourcePoint.y) * sy;
        // 中文注释：缩放 sourcePoint 坐标。
	}
	
	// Translates the target point
	if (this.targetPoint != null)
	{
		this.targetPoint.x = parseFloat(this.targetPoint.x) * sx;
		this.targetPoint.y = parseFloat(this.targetPoint.y) * sy;		
        // 中文注释：缩放 targetPoint 坐标。
	}

	// Translate the control points
	if (this.points != null)
	{
		for (var i = 0; i < this.points.length; i++)
		{
			if (this.points[i] != null)
			{
				this.points[i].x = parseFloat(this.points[i].x) * sx;
				this.points[i].y = parseFloat(this.points[i].y) * sy;
                // 中文注释：缩放所有控制点坐标。
			}
		}
	}
	
	// Translates the geometry
	if (!this.relative)
	{
		this.x = parseFloat(this.x) * sx;
		this.y = parseFloat(this.y) * sy;
        // 中文注释：如果 relative 为 false，缩放 x、y 坐标。

		if (fixedAspect)
		{
			sy = sx = Math.min(sx, sy);
            // 中文注释：如果 fixedAspect 为 true，使用较小的缩放比例以保持宽高比。
		}
		
		this.width = parseFloat(this.width) * sx;
		this.height = parseFloat(this.height) * sy;
        // 中文注释：缩放宽度和高度。
	}
};

/**
 * Function: equals
 * 
 * Returns true if the given object equals this geometry.
 */
// 中文注释：equals 方法比较当前几何对象与指定对象是否相等。
// 方法目的：检查两个几何对象的所有属性是否完全相同。
// 比较内容包括父类 mxRectangle 的属性、relative、sourcePoint、targetPoint、points 和 offset。
mxGeometry.prototype.equals = function(obj)
{
	return mxRectangle.prototype.equals.apply(this, arguments) &&
		this.relative == obj.relative &&
		((this.sourcePoint == null && obj.sourcePoint == null) || (this.sourcePoint != null && this.sourcePoint.equals(obj.sourcePoint))) &&
		((this.targetPoint == null && obj.targetPoint == null) || (this.targetPoint != null && this.targetPoint.equals(obj.targetPoint))) &&
		((this.points == null && obj.points == null) || (this.points != null && mxUtils.equalPoints(this.points, obj.points))) &&
		((this.alternateBounds == null && obj.alternateBounds == null) || (this.alternateBounds != null && this.alternateBounds.equals(obj.alternateBounds))) &&
		((this.offset == null && obj.offset == null) || (this.offset != null && this.offset.equals(obj.offset)));
        // 中文注释：调用父类 equals 方法，并比较 relative、sourcePoint、targetPoint、points 和 offset 是否相等。
};
