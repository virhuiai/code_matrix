/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxArrowConnector
 *
 * Extends <mxShape> to implement an new rounded arrow shape with support for
 * waypoints and double arrows. (The shape is used to represent edges, not
 * vertices.) This shape is registered under <mxConstants.SHAPE_ARROW_CONNECTOR>
 * in <mxCellRenderer>.
 * 
 * Constructor: mxArrowConnector
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
// mxArrowConnector 类继承自 mxShape，用于实现支持路径点和双箭头的圆角箭头形状。
// 该形状用于表示图中的边（而非顶点），并在 mxCellRenderer 中以 mxConstants.SHAPE_ARROW_CONNECTOR 注册。
// 构造函数：mxArrowConnector
// 功能：创建一个新的箭头形状。
// 参数说明：
// - points: mxPoints 数组，定义箭头的路径点，存储在 mxShape.points 中。
// - fill: 字符串，定义填充颜色，存储在 fill 属性中。
// - stroke: 字符串，定义边框颜色，存储在 stroke 属性中。
// - strokewidth: 可选整数，定义边框宽度，默认值为 1，存储在 strokewidth 属性中。
// - arrowWidth: 可选整数，定义箭头宽度，默认值为 mxConstants.ARROW_WIDTH，存储在 arrowWidth 属性中。
// - spacing: 可选整数，定义箭头形状与端点之间的间距，默认值为 mxConstants.ARROW_SPACING，存储在 spacing 属性中。
// - endSize: 可选整数，定义箭头头部大小，默认值为 mxConstants.ARROW_SIZE，存储在 endSize 属性中。
function mxArrowConnector(points, fill, stroke, strokewidth, arrowWidth, spacing, endSize)
{
	mxShape.call(this);
	this.points = points;
	this.fill = fill;
	this.stroke = stroke;
	this.strokewidth = (strokewidth != null) ? strokewidth : 1;
	this.arrowWidth = (arrowWidth != null) ? arrowWidth : mxConstants.ARROW_WIDTH;
	this.arrowSpacing = (spacing != null) ? spacing : mxConstants.ARROW_SPACING;
	this.startSize = mxConstants.ARROW_SIZE / 5;
	this.endSize = mxConstants.ARROW_SIZE / 5;
    // 中文注释：
    // 初始化 mxArrowConnector 实例，调用父类 mxShape 的构造函数。
    // 将传入的参数赋值给实例属性，并为未提供的参数设置默认值。
    // 关键变量说明：
    // - strokewidth: 边框宽度，默认值为 1。
    // - arrowWidth: 箭头宽度，默认值为 mxConstants.ARROW_WIDTH。
    // - arrowSpacing: 箭头与端点间距，默认值为 mxConstants.ARROW_SPACING。
    // - startSize/endSize: 起点和终点箭头大小，默认值为 mxConstants.ARROW_SIZE / 5。
};

/**
 * Extends mxShape.
 */
// 中文注释：
// 通过 mxUtils.extend 使 mxArrowConnector 继承 mxShape，提供基础形状功能。
mxUtils.extend(mxArrowConnector, mxShape);

/**
 * Variable: useSvgBoundingBox
 * 
 * Allows to use the SVG bounding box in SVG. Default is false for performance
 * reasons.
 */
// 中文注释：
// 变量：useSvgBoundingBox
// 功能：控制是否在 SVG 中使用 SVG 边界框，默认为 false 以优化性能。
// 注意事项：启用 SVG 边界框可能增加计算开销，适合需要更高精度的场景。
mxArrowConnector.prototype.useSvgBoundingBox = true;

/**
 * Function: isRoundable
 * 
 * Hook for subclassers.
 */
// 中文注释：
// 函数：isRoundable
// 功能：钩子函数，允许子类判断是否支持圆角绘制。
// 返回值：布尔值，表示是否支持圆角，默认为 true。
mxArrowConnector.prototype.isRoundable = function()
{
	return true;
};

/**
 * Variable: resetStyles
 * 
 * Overrides mxShape to reset spacing.
 */
// 中文注释：
// 函数：resetStyles
// 功能：重置样式，覆盖 mxShape 的 resetStyles 方法，专门重置箭头间距。
// 关键配置：将 arrowSpacing 重置为 mxConstants.ARROW_SPACING。
mxArrowConnector.prototype.resetStyles = function()
{
	mxShape.prototype.resetStyles.apply(this, arguments);
	
	this.arrowSpacing = mxConstants.ARROW_SPACING;
    // 中文注释：
    // 调用父类的 resetStyles 方法，并将 arrowSpacing 重置为默认值 mxConstants.ARROW_SPACING。
};

/**
 * Overrides apply to get smooth transition from default start- and endsize.
 */
// 中文注释：
// 函数：apply
// 功能：应用样式，覆盖 mxShape 的 apply 方法，确保起点和终点箭头大小平滑过渡。
// 参数说明：
// - state: 包含样式信息的对象，用于获取自定义样式。
// 关键逻辑：从 style 中提取 STARTSIZE 和 ENDSIZE，若未定义则使用默认值 mxConstants.ARROW_SIZE / 5，并放大 3 倍。
mxArrowConnector.prototype.apply = function(state)
{
	mxShape.prototype.apply.apply(this, arguments);

	if (this.style != null)
	{
		this.startSize = mxUtils.getNumber(this.style, mxConstants.STYLE_STARTSIZE, mxConstants.ARROW_SIZE / 5) * 3;
		this.endSize = mxUtils.getNumber(this.style, mxConstants.STYLE_ENDSIZE, mxConstants.ARROW_SIZE / 5) * 3;
	}
    // 中文注释：
    // 调用父类的 apply 方法，并根据 style 中的配置更新 startSize 和 endSize。
    // 重要配置参数：
    // - startSize: 起点箭头大小，默认为 mxConstants.ARROW_SIZE / 5 * 3。
    // - endSize: 终点箭头大小，默认为 mxConstants.ARROW_SIZE / 5 * 3。
};

/**
 * Function: augmentBoundingBox
 *
 * Augments the bounding box with the edge width and markers.
 */
// 中文注释：
// 函数：augmentBoundingBox
// 功能：扩展边界框，考虑边宽度和箭头标记的影响。
// 参数说明：
// - bbox: 边界框对象，用于存储扩展后的边界信息。
// 关键逻辑：根据边宽度、起点和终点箭头宽度以及缩放比例扩展边界框。
// 注意事项：确保边界框足够包含箭头形状和边框，避免裁剪。
mxArrowConnector.prototype.augmentBoundingBox = function(bbox)
{
	mxShape.prototype.augmentBoundingBox.apply(this, arguments);
	
	var w = this.getEdgeWidth();
	
	if (this.isMarkerStart())
	{
		w = Math.max(w, this.getStartArrowWidth());
	}
	
	if (this.isMarkerEnd())
	{
		w = Math.max(w, this.getEndArrowWidth());
	}
	
	bbox.grow((w / 2 + this.strokewidth) * this.scale);
    // 中文注释：
    // 调用父类的 augmentBoundingBox 方法。
    // 计算边宽度 w，并根据起点和终点是否有箭头标记，取最大宽度。
    // 根据边宽度、边框宽度和缩放比例扩展边界框。
    // 关键变量：
    // - w: 边的宽度，考虑箭头宽度和边框宽度。
    // - scale: 缩放比例，用于调整边界框大小。
};

/**
 * Function: paintEdgeShape
 * 
 * Paints the line shape.
 */
// 中文注释：
// 函数：paintEdgeShape
// 功能：绘制箭头形状的线条部分。
// 参数说明：
// - c: 画布对象，用于执行绘制操作。
// - pts: 路径点数组，定义箭头的路径。
// 关键逻辑：
// 1. 计算箭头和边的几何属性（如宽度、间距）。
// 2. 根据路径点绘制线条，支持圆角和箭头标记。
// 3. 处理起点和终点箭头的绘制逻辑。
// 交互逻辑：支持圆角绘制、动态调整线条连接方式（圆角或斜接）。
// 注意事项：
// - 如果路径点距离为 0，则直接返回，避免无效绘制。
// - 特殊处理开放端点（openEnded）和箭头标记（markerStart/markerEnd）。
mxArrowConnector.prototype.paintEdgeShape = function(c, pts)
{
	// Geometry of arrow
	var strokeWidth = this.strokewidth;
	
	if (this.outline)
	{
		strokeWidth = Math.max(1, mxUtils.getNumber(this.style, mxConstants.STYLE_STROKEWIDTH, this.strokewidth));
	}
	
	var startWidth = this.getStartArrowWidth() + strokeWidth;
	var endWidth = this.getEndArrowWidth() + strokeWidth;
	var edgeWidth = this.outline ? this.getEdgeWidth() + strokeWidth : this.getEdgeWidth();
	var openEnded = this.isOpenEnded();
	var markerStart = this.isMarkerStart();
	var markerEnd = this.isMarkerEnd();
	var spacing = (openEnded) ? 0 : this.arrowSpacing + strokeWidth / 2;
	var startSize = this.startSize + strokeWidth;
	var endSize = this.endSize + strokeWidth;
	var isRounded = this.isArrowRounded();
    // 中文注释：
    // 计算箭头的几何属性：
    // - strokeWidth: 边框宽度，outline 模式下从 style 中获取，默认使用 strokewidth。
    // - startWidth/endWidth: 起点和终点箭头宽度，包含边框宽度。
    // - edgeWidth: 边宽度，outline 模式下包含边框宽度。
    // - openEnded: 是否为开放端点，影响间距和绘制逻辑。
    // - markerStart/markerEnd: 是否绘制起点/终点箭头标记。
    // - spacing: 箭头与端点的间距，开放端点时为 0。
    // - startSize/endSize: 起点和终点箭头大小，包含边框宽度。
    // - isRounded: 是否绘制圆角箭头。

	// Base vector (between first points)
	var pe = pts[pts.length - 1];
    // 中文注释：
    // 获取终点坐标 pe，用于计算路径方向。

	// Finds first non-overlapping point
	var i0 = 1;
	
	while (i0 < pts.length - 1 && pts[i0].x == pts[0].x && pts[i0].y == pts[0].y)
	{
		i0++;
	}
    // 中文注释：
    // 查找第一个非重叠点，确保路径计算有效。

	var dx = pts[i0].x - pts[0].x;
	var dy = pts[i0].y - pts[0].y;
	var dist = Math.sqrt(dx * dx + dy * dy);
	
	if (dist == 0)
	{
		return;
	}
    // 中文注释：
    // 计算起点与第一个非重叠点之间的向量（dx, dy）和距离 dist。
    // 如果距离为 0，直接返回，避免无效绘制。

	// Computes the norm and the inverse norm
	var nx = dx / dist;
	var nx2, nx1 = nx;
	var ny = dy / dist;
	var ny2, ny1 = ny;
	var orthx = edgeWidth * ny;
	var orthy = -edgeWidth * nx;
    // 中文注释：
    // 计算单位向量（nx, ny）和正交向量（orthx, orthy），用于确定箭头方向和边宽度。
    // 关键变量：
    // - nx/ny: 单位方向向量。
    // - orthx/orthy: 正交向量，用于计算边的偏移。

	// Stores the inbound function calls in reverse order in fns
	var fns = [];
    // 中文注释：
    // 定义数组 fns，用于存储反向绘制的函数调用。

	if (isRounded)
	{
		c.setLineJoin('round');
	}
	else if (pts.length > 2)
	{
		// Only mitre if there are waypoints
		c.setMiterLimit(1.42);
	}
    // 中文注释：
    // 样式设置：
    // - 如果 isRounded 为 true，设置线条连接为圆角（round）。
    // - 如果路径点超过 2 个，设置斜接限制（miter limit）为 1.42。
    // 注意事项：斜接限制仅在有路径点时应用，优化绘制效果。

	c.begin();
    // 中文注释：
    // 开始绘制路径。

	var startNx = nx;
	var startNy = ny;
    // 中文注释：
    // 保存起点方向向量 startNx 和 startNy，用于后续绘制。

	if (markerStart && !openEnded)
	{
		this.paintMarker(c, pts[0].x, pts[0].y, nx, ny, startSize, startWidth, edgeWidth, spacing, true);
	}
	else
	{
		var outStartX = pts[0].x + orthx / 2 + spacing * nx;
		var outStartY = pts[0].y + orthy / 2 + spacing * ny;
		var inEndX = pts[0].x - orthx / 2 + spacing * nx;
		var inEndY = pts[0].y - orthy / 2 + spacing * ny;
		
		if (openEnded)
		{
			c.moveTo(outStartX, outStartY);
			
			fns.push(function()
			{
				c.lineTo(inEndX, inEndY);
			});
		}
		else
		{
			c.moveTo(inEndX, inEndY);
			c.lineTo(outStartX, outStartY);
		}
	}
    // 中文注释：
    // 事件处理逻辑：
    // - 如果起点有箭头标记（markerStart）且非开放端点，调用 paintMarker 绘制起点箭头。
    // - 否则，计算起点的外侧（outStartX, outStartY）和内侧（inEndX, inEndY）坐标。
    // 交互逻辑：
    // - 开放端点（openEnded）：移动到外侧起点并存储内侧绘制函数。
    // - 非开放端点：从内侧起点开始绘制到外侧起点。

	var dx1 = 0;
	var dy1 = 0;
	var dist1 = 0;
    // 中文注释：
    // 定义变量 dx1, dy1, dist1，用于后续路径段的向量和距离计算。

	for (var i = 0; i < pts.length - 2; i++)
	{
		// Work out in which direction the line is bending
		var pos = mxUtils.relativeCcw(pts[i].x, pts[i].y, pts[i+1].x, pts[i+1].y, pts[i+2].x, pts[i+2].y);
        // 中文注释：
        // 计算线段的弯曲方向（pos），使用 mxUtils.relativeCcw 判断路径点的相对位置。

		dx1 = pts[i+2].x - pts[i+1].x;
		dy1 = pts[i+2].y - pts[i+1].y;

		dist1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
		
		if (dist1 != 0)
		{
			nx1 = dx1 / dist1;
			ny1 = dy1 / dist1;
			
			var tmp1 = nx * nx1 + ny * ny1;
			var tmp = Math.max(Math.sqrt((tmp1 + 1) / 2), 0.04);
			
			// Work out the normal orthogonal to the line through the control point and the edge sides intersection
			nx2 = (nx + nx1);
			ny2 = (ny + ny1);
	
			var dist2 = Math.sqrt(nx2 * nx2 + ny2 * ny2);
			
			if (dist2 != 0)
			{
				nx2 = nx2 / dist2;
				ny2 = ny2 / dist2;
				
				// Higher strokewidths require a larger minimum bend, 0.35 covers all but the most extreme cases
				var strokeWidthFactor = Math.max(tmp, Math.min(this.strokewidth / 200 + 0.04, 0.35));
				var angleFactor = (pos != 0 && isRounded) ? Math.max(0.1, strokeWidthFactor) : Math.max(tmp, 0.06);
                // 中文注释：
                // 计算后续路径段的单位向量（nx1, ny1）和正交向量（nx2, ny2）。
                // 根据线段弯曲角度和边框宽度调整 strokeWidthFactor 和 angleFactor。
                // 关键变量：
                // - strokeWidthFactor: 控制最小弯曲半径，基于边框宽度，范围 [0.04, 0.35]。
                // - angleFactor: 控制角度调整，圆角时最小值为 0.1。
                // 注意事项：确保弯曲半径适应不同边框宽度，避免极端情况下的绘制错误。

				var outX = pts[i+1].x + ny2 * edgeWidth / 2 / angleFactor;
				var outY = pts[i+1].y - nx2 * edgeWidth / 2 / angleFactor;
				var inX = pts[i+1].x - ny2 * edgeWidth / 2 / angleFactor;
				var inY = pts[i+1].y + nx2 * edgeWidth / 2 / angleFactor;
                // 中文注释：
                // 计算路径点的外侧（outX, outY）和内侧（inX, inY）坐标，用于绘制线段。

				if (pos == 0 || !isRounded)
				{
					// If the two segments are aligned, or if we're not drawing curved sections between segments
					// just draw straight to the intersection point
					c.lineTo(outX, outY);
					
					(function(x, y)
					{
						fns.push(function()
						{
							c.lineTo(x, y);
						});
					})(inX, inY);
				}
				else if (pos == -1)
				{
					var c1x = inX + ny * edgeWidth;
					var c1y = inY - nx * edgeWidth;
					var c2x = inX + ny1 * edgeWidth;
					var c2y = inY - nx1 * edgeWidth;
					c.lineTo(c1x, c1y);
					c.quadTo(outX, outY, c2x, c2y);
					
					(function(x, y)
					{
						fns.push(function()
						{
							c.lineTo(x, y);
						});
					})(inX, inY);
				}
				else
				{
					c.lineTo(outX, outY);
					
					(function(x, y)
					{
						var c1x = outX - ny * edgeWidth;
						var c1y = outY + nx * edgeWidth;
						var c2x = outX - ny1 * edgeWidth;
						var c2y = outY + nx1 * edgeWidth;
						
						fns.push(function()
						{
							c.quadTo(x, y, c1x, c1y);
						});
						fns.push(function()
						{
							c.lineTo(c2x, c2y);
						});
					})(inX, inY);
				}
                // 中文注释：
                // 事件处理逻辑：
                // - 如果路径段对齐（pos == 0）或非圆角，直接绘制直线到交点。
                // - 如果路径向左弯曲（pos == -1），使用二次贝塞尔曲线绘制圆角。
                // - 如果路径向右弯曲（pos == 1），绘制直线并存储贝塞尔曲线绘制函数。
                // 交互逻辑：支持平滑过渡（圆角）或直接连接（直线），根据路径方向动态调整。

				nx = nx1;
				ny = ny1;
                // 中文注释：
                // 更新当前方向向量为下一段的 nx1, ny1。
			}
		}
	}
	
	orthx = edgeWidth * ny1;
	orthy = - edgeWidth * nx1;
    // 中文注释：
    // 更新正交向量 orthx, orthy，用于终点绘制。

	if (markerEnd && !openEnded)
	{
		this.paintMarker(c, pe.x, pe.y, -nx, -ny, endSize, endWidth, edgeWidth, spacing, false);
	}
	else
	{
		c.lineTo(pe.x - spacing * nx1 + orthx / 2, pe.y - spacing * ny1 + orthy / 2);
		
		var inStartX = pe.x - spacing * nx1 - orthx / 2;
		var inStartY = pe.y - spacing * ny1 - orthy / 2;

		if (!openEnded)
		{
			c.lineTo(inStartX, inStartY);
		}
		else
		{
			c.moveTo(inStartX, inStartY);
			
			fns.splice(0, 0, function()
			{
				c.moveTo(inStartX, inStartY);
			});
		}
	}
    // 中文注释：
    // 事件处理逻辑：
    // - 如果终点有箭头标记（markerEnd）且非开放端点，调用 paintMarker 绘制终点箭头。
    // - 否则，绘制终点外侧和内侧坐标。
    // 交互逻辑：
    // - 非开放端点：绘制到内侧终点。
    // - 开放端点：移动到内侧终点并存储绘制函数。

	for (var i = fns.length - 1; i >= 0; i--)
	{
		fns[i]();
	}
    // 中文注释：
    // 反向执行存储的绘制函数，完成路径的另一侧绘制。

	if (openEnded)
	{
		c.end();
		c.stroke();
	}
	else
	{
		c.close();
		c.fillAndStroke();
	}
    // 中文注释：
    // 样式设置：
    // - 开放端点：结束路径并仅描边。
    // - 非开放端点：闭合路径并进行填充和描边。

	// Workaround for shadow on top of base arrow
	c.setShadow(false);
    // 中文注释：
    // 特殊处理：禁用阴影绘制，避免箭头基础形状上的阴影重叠问题。

	// Need to redraw the markers without the low miter limit
	c.setMiterLimit(4);
	
	if (isRounded)
	{
		c.setLineJoin('flat');
	}
    // 中文注释：
    // 样式设置：
    // - 将斜接限制设置为 4，优化箭头标记的绘制。
    // - 如果是圆角箭头，设置线条连接为 flat。

	if (pts.length > 2)
	{
		// Only to repaint markers if no waypoints
		// Need to redraw the markers without the low miter limit
		c.setMiterLimit(4);
		if (markerStart && !openEnded)
		{
			c.begin();
			this.paintMarker(c, pts[0].x, pts[0].y, startNx, startNy, startSize, startWidth, edgeWidth, spacing, true);
			c.stroke();
			c.end();
		}
		
		if (markerEnd && !openEnded)
		{
			c.begin();
			this.paintMarker(c, pe.x, pe.y, -nx, -ny, endSize, endWidth, edgeWidth, spacing, true);
			c.stroke();
			c.end();
		}
	}
    // 中文注释：
    // 特殊处理：如果路径点超过 2 个，重新绘制起点和终点箭头标记。
    // - 使用更高的斜接限制（4）确保标记绘制质量。
    // - 仅在非开放端点且有箭头标记时绘制。
};

/**
 * Function: paintMarker
 * 
 * Paints the marker.
 */
// 中文注释：
// 函数：paintMarker
// 功能：绘制箭头标记（起点或终点箭头）。
// 参数说明：
// - c: 画布对象，用于执行绘制操作。
// - ptX, ptY: 标记的中心点坐标。
// - nx, ny: 方向向量，确定箭头方向。
// - size: 箭头大小。
// - arrowWidth: 箭头宽度。
// - edgeWidth: 边宽度。
// - spacing: 箭头与端点的间距。
// - initialMove: 是否为初始移动（true 表示移动到起点，false 表示绘制连接线）。
// 关键逻辑：绘制箭头形状的路径，包括外侧、内侧和尖端。
mxArrowConnector.prototype.paintMarker = function(c, ptX, ptY, nx, ny, size, arrowWidth, edgeWidth, spacing, initialMove)
{
	var widthArrowRatio = edgeWidth / arrowWidth;
	var orthx = edgeWidth * ny / 2;
	var orthy = -edgeWidth * nx / 2;

	var spaceX = (spacing + size) * nx;
	var spaceY = (spacing + size) * ny;

	if (initialMove)
	{
		c.moveTo(ptX - orthx + spaceX, ptY - orthy + spaceY);
	}
	else
	{
		c.lineTo(ptX - orthx + spaceX, ptY - orthy + spaceY);
	}

	c.lineTo(ptX - orthx / widthArrowRatio + spaceX, ptY - orthy / widthArrowRatio + spaceY);
	c.lineTo(ptX + spacing * nx, ptY + spacing * ny);
	c.lineTo(ptX + orthx / widthArrowRatio + spaceX, ptY + orthy / widthArrowRatio + spaceY);
	c.lineTo(ptX + orthx + spaceX, ptY + orthy + spaceY);
    // 中文注释：
    // 计算箭头形状的几何参数：
    // - widthArrowRatio: 边宽度与箭头宽度的比例。
    // - orthx, orthy: 正交向量，用于计算箭头侧边。
    // - spaceX, spaceY: 考虑间距和箭头大小的偏移量。
    // 绘制逻辑：
    // - 如果 initialMove 为 true，移动到起点坐标。
    // - 否则，绘制连接线到起点坐标。
    // - 按顺序绘制箭头的四个点，形成箭头形状。
};

/**
 * Function: isArrowRounded
 * 
 * Returns wether the arrow is rounded
 */
// 中文注释：
// 函数：isArrowRounded
// 功能：判断箭头是否为圆角。
// 返回值：布尔值，表示是否为圆角箭头，依赖于 isRounded 属性。
mxArrowConnector.prototype.isArrowRounded = function()
{
	return this.isRounded;
};

/**
 * Function: getStartArrowWidth
 * 
 * Returns the width of the start arrow
 */
// 中文注释：
// 函数：getStartArrowWidth
// 功能：获取起点箭头的宽度。
// 返回值：默认值为 mxConstants.ARROW_WIDTH。
mxArrowConnector.prototype.getStartArrowWidth = function()
{
	return mxConstants.ARROW_WIDTH;
};

/**
 * Function: getEndArrowWidth
 * 
 * Returns the width of the end arrow
 */
// 中文注释：
// 函数：getEndArrowWidth
// 功能：获取终点箭头的宽度。
// 返回值：默认值为 mxConstants.ARROW_WIDTH。
mxArrowConnector.prototype.getEndArrowWidth = function()
{
	return mxConstants.ARROW_WIDTH;
};

/**
 * Function: getEdgeWidth
 * 
 * Returns the width of the body of the edge
 */
// 中文注释：
// 函数：getEdgeWidth
// 功能：获取边的主体宽度。
// 返回值：默认值为 mxConstants.ARROW_WIDTH / 3。
mxArrowConnector.prototype.getEdgeWidth = function()
{
	return mxConstants.ARROW_WIDTH / 3;
};

/**
 * Function: isOpenEnded
 * 
 * Returns whether the ends of the shape are drawn
 */
// 中文注释：
// 函数：isOpenEnded
// 功能：判断是否绘制开放端点。
// 返回值：布尔值，默认为 false，表示端点闭合。
mxArrowConnector.prototype.isOpenEnded = function()
{
	return false;
};

/**
 * Function: isMarkerStart
 * 
 * Returns whether the start marker is drawn
 */
// 中文注释：
// 函数：isMarkerStart
// 功能：判断是否绘制起点箭头标记。
// 返回值：布尔值，基于 style 中的 STYLE_STARTARROW 属性，检查是否为 mxConstants.NONE。
mxArrowConnector.prototype.isMarkerStart = function()
{
	return (mxUtils.getValue(this.style, mxConstants.STYLE_STARTARROW, mxConstants.NONE) != mxConstants.NONE);
};

/**
 * Function: isMarkerEnd
 * 
 * Returns whether the end marker is drawn
 */
// 中文注释：
// 函数：isMarkerEnd
// 功能：判断是否绘制终点箭头标记。
// 返回值：布尔值，基于 style 中的 STYLE_ENDARROW 属性，检查是否为 mxConstants.NONE。
mxArrowConnector.prototype.isMarkerEnd = function()
{
	return (mxUtils.getValue(this.style, mxConstants.STYLE_ENDARROW, mxConstants.NONE) != mxConstants.NONE);
};