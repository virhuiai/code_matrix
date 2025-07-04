/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
var mxPerimeter =
{
	/**
	 * Class: mxPerimeter
	 * 
	 * Provides various perimeter functions to be used in a style
	 * as the value of <mxConstants.STYLE_PERIMETER>. Perimeters for
	 * rectangle, circle, rhombus and triangle are available.
	 *
	 * Example:
	 * 
	 * (code)
	 * <add as="perimeter">mxPerimeter.RectanglePerimeter</add>
	 * (end)
	 * 
	 * Or programmatically:
	 * 
	 * (code)
	 * style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
	 * (end)
	 * 
	 * When adding new perimeter functions, it is recommended to use the 
	 * mxPerimeter-namespace as follows:
	 * 
	 * (code)
	 * mxPerimeter.CustomPerimeter = function (bounds, vertex, next, orthogonal)
	 * {
	 *   var x = 0; // Calculate x-coordinate
	 *   var y = 0; // Calculate y-coordainte
	 *   
	 *   return new mxPoint(x, y);
	 * }
	 * (end)
	 * 
	 * The new perimeter should then be registered in the <mxStyleRegistry> as follows:
	 * (code)
	 * mxStyleRegistry.putValue('customPerimeter', mxPerimeter.CustomPerimeter);
	 * (end)
	 * 
	 * The custom perimeter above can now be used in a specific vertex as follows:
	 * 
	 * (code)
	 * model.setStyle(vertex, 'perimeter=customPerimeter');
	 * (end)
	 * 
	 * Note that the key of the <mxStyleRegistry> entry for the function should
	 * be used in string values, unless <mxGraphView.allowEval> is true, in
	 * which case you can also use mxPerimeter.CustomPerimeter for the value in
	 * the cell style above.
	 * 
	 * Or it can be used for all vertices in the graph as follows:
	 * 
	 * (code)
	 * var style = graph.getStylesheet().getDefaultVertexStyle();
	 * style[mxConstants.STYLE_PERIMETER] = mxPerimeter.CustomPerimeter;
	 * (end)
	 * 
	 * Note that the object can be used directly when programmatically setting
	 * the value, but the key in the <mxStyleRegistry> should be used when
	 * setting the value via a key, value pair in a cell style.
	 * 
	 * The parameters are explained in <RectanglePerimeter>.
	 * 
	 * Function: RectanglePerimeter
	 * 
	 * Describes a rectangular perimeter for the given bounds.
	 *
	 * Parameters:
	 * 
	 * bounds - <mxRectangle> that represents the absolute bounds of the
	 * vertex.
	 * vertex - <mxCellState> that represents the vertex.
	 * next - <mxPoint> that represents the nearest neighbour point on the
	 * given edge.
	 * orthogonal - Boolean that specifies if the orthogonal projection onto
	 * the perimeter should be returned. If this is false then the intersection
	 * of the perimeter and the line between the next and the center point is
	 * returned.
	 */
    // 中文注释：
    // 类：mxPerimeter
    // 提供多种周界函数，用于样式中作为 <mxConstants.STYLE_PERIMETER> 的值。
    // 支持矩形、圆形、菱形和三角形的周界。
    // 示例：
    // - 通过 XML 配置周界样式。
    // - 程序化设置周界样式。
    // 新周界函数建议使用 mxPerimeter 命名空间，并注册到 <mxStyleRegistry>。
    // 可通过字符串键或直接对象设置样式，具体取决于 <mxGraphView.allowEval>。
    // 参数说明参考 <RectanglePerimeter>。
    // 函数：RectanglePerimeter
    // 描述给定边界的矩形周界。
    // 参数：
    // - bounds: <mxRectangle> 表示顶点的绝对边界。
    // - vertex: <mxCellState> 表示顶点状态。
    // - next: <mxPoint> 表示给定边上最近的邻居点。
    // - orthogonal: 布尔值，决定是否返回周界上的正交投影。若为 false，则返回周界与 next 和中心点连线的交点。
	RectanglePerimeter: function (bounds, vertex, next, orthogonal)
	{
		var cx = bounds.getCenterX();
		var cy = bounds.getCenterY();
		var dx = next.x - cx;
		var dy = next.y - cy;
		var alpha = Math.atan2(dy, dx);
		var p = new mxPoint(0, 0);
		var pi = Math.PI;
		var pi2 = Math.PI/2;
		var beta = pi2 - alpha;
		var t = Math.atan2(bounds.height, bounds.width);
        // 中文注释：
        // 计算矩形中心点坐标 (cx, cy)。
        // 计算 next 点与中心点的差值 (dx, dy)。
        // 计算连线角度 alpha。
        // 初始化返回点 p 为 (0, 0)。
        // 定义数学常数 pi 和 pi/2。
        // 计算 beta 角度（pi/2 - alpha）。
        // 计算矩形高宽比的角度 t。

		if (alpha < -pi + t || alpha > pi - t)
		{
			// Left edge
			p.x = bounds.x;
			p.y = cy - bounds.width * Math.tan(alpha) / 2;
            // 中文注释：
            // 处理左边缘情况。
            // 设置 x 坐标为矩形左边界。
            // 根据角度 alpha 计算 y 坐标。
		}
		else if (alpha < -t)
		{
			// Top Edge
			p.y = bounds.y;
			p.x = cx - bounds.height * Math.tan(beta) / 2;
            // 中文注释：
            // 处理上边缘情况。
            // 设置 y 坐标为矩形上边界。
            // 根据角度 beta 计算 x 坐标。
		}
		else if (alpha < t)
		{
			// Right Edge
			p.x = bounds.x + bounds.width;
			p.y = cy + bounds.width * Math.tan(alpha) / 2;
            // 中文注释：
            // 处理右边缘情况。
            // 设置 x 坐标为矩形右边界。
            // 根据角度 alpha 计算 y 坐标。
		}
		else
		{
			// Bottom Edge
			p.y = bounds.y + bounds.height;
			p.x = cx + bounds.height * Math.tan(beta) / 2;
            // 中文注释：
            // 处理下边缘情况。
            // 设置 y 坐标为矩形下边界。
            // 根据角度 beta 计算 x 坐标。
		}
		
		if (orthogonal)
		{
			if (next.x >= bounds.x &&
				next.x <= bounds.x + bounds.width)
			{
				p.x = next.x;
                // 中文注释：
                // 若正交模式且 next.x 在矩形水平范围内，设置 p.x 为 next.x。
			}
			else if (next.y >= bounds.y &&
					   next.y <= bounds.y + bounds.height)
			{
				p.y = next.y;
                // 中文注释：
                // 若正交模式且 next.y 在矩形垂直范围内，设置 p.y 为 next.y。
			}
			if (next.x < bounds.x)
			{
				p.x = bounds.x;
                // 中文注释：
                // 若 next.x 小于矩形左边界，设置 p.x 为左边界。
			}
			else if (next.x > bounds.x + bounds.width)
			{
				p.x = bounds.x + bounds.width;
                // 中文注释：
                // 若 next.x 大于矩形右边界，设置 p.x 为右边界。
			}
			if (next.y < bounds.y)
			{
				p.y = bounds.y;
                // 中文注释：
                // 若 next.y 小于矩形上边界，设置 p.y 为上边界。
			}
			else if (next.y > bounds.y + bounds.height)
			{
				p.y = bounds.y + bounds.height;
                // 中文注释：
                // 若 next.y 大于矩形下边界，设置 p.y 为下边界。
			}
		}
		
		return p;
        // 中文注释：
        // 返回计算得到的周界点 p。
	},

	/**
	 * Function: EllipsePerimeter
	 * 
	 * Describes an elliptic perimeter. See <RectanglePerimeter>
	 * for a description of the parameters.
	 */
    // 中文注释：
    // 函数：EllipsePerimeter
    // 描述椭圆周界。
    // 参数说明参考 <RectanglePerimeter>。
	EllipsePerimeter: function (bounds, vertex, next, orthogonal)
	{
		var x = bounds.x;
		var y = bounds.y;
		var a = bounds.width / 2;
		var b = bounds.height / 2;
		var cx = x + a;
		var cy = y + b;
		var px = next.x;
		var py = next.y;
        // 中文注释：
        // 获取椭圆边界坐标 x, y。
        // 计算椭圆半轴 a（宽/2）, b（高/2）。
        // 计算椭圆中心点 (cx, cy)。
        // 获取 next 点的坐标 (px, py)。

		// Calculates straight line equation through
		// point and ellipse center y = d * x + h
		var dx = parseInt(px - cx);
		var dy = parseInt(py - cy);
        // 中文注释：
        // 计算通过 next 点和椭圆中心的直线方程参数。
        // dx, dy 为 next 点与中心点的差值。

		if (dx == 0 && dy != 0)
		{
			return new mxPoint(cx, cy + b * dy / Math.abs(dy));
            // 中文注释：
            // 特殊情况：若 dx 为 0（垂直线），返回椭圆上下边界点。
		}
		else if (dx == 0 && dy == 0)
		{
			return new mxPoint(px, py);
            // 中文注释：
            // 特殊情况：若 dx 和 dy 均为 0，返回 next 点本身。
		}

		if (orthogonal)
		{
			if (py >= y && py <= y + bounds.height)
			{
				var ty = py - cy;
				var tx = Math.sqrt(a*a*(1-(ty*ty)/(b*b))) || 0;
                // 中文注释：
                // 正交模式下，若 py 在椭圆垂直范围内，计算水平方向上的正交点。
                // ty 为 y 方向偏移，tx 根据椭圆方程计算。

				if (px <= x)
				{
					tx = -tx;
                    // 中文注释：
                    // 若 px 在椭圆左侧，调整 tx 为负值。
				}
				
				return new mxPoint(cx+tx, py);
                // 中文注释：
                // 返回正交投影点。
			}
			
			if (px >= x && px <= x + bounds.width)
			{
				var tx = px - cx;
				var ty = Math.sqrt(b*b*(1-(tx*tx)/(a*a))) || 0;
                // 中文注释：
                // 正交模式下，若 px 在椭圆水平范围内，计算垂直方向上的正交点。
                // tx 为 x 方向偏移，ty 根据椭圆方程计算。

				if (py <= y)
				{
					ty = -ty;	
                    // 中文注释：
                    // 若 py 在椭圆上侧，调整 ty 为负值。
				}
				
				return new mxPoint(px, cy+ty);
                // 中文注释：
                // 返回正交投影点。
			}
		}
		
		// Calculates intersection
		var d = dy / dx;
		var h = cy - d * cx;
		var e = a * a * d * d + b * b;
		var f = -2 * cx * e;
		var g = a * a * d * d * cx * cx +
				b * b * cx * cx -
				a * a * b * b;
		var det = Math.sqrt(f * f - 4 * e * g);
        // 中文注释：
        // 计算直线与椭圆的交点。
        // d 为直线斜率，h 为直线截距。
        // e, f, g 为二次方程系数，用于求解交点。
        // det 为判别式，确定交点存在性。

		// Two solutions (perimeter points)
		var xout1 = (-f + det) / (2 * e);
		var xout2 = (-f - det) / (2 * e);
		var yout1 = d * xout1 + h;
		var yout2 = d * xout2 + h;
		var dist1 = Math.sqrt(Math.pow((xout1 - px), 2)
					+ Math.pow((yout1 - py), 2));
		var dist2 = Math.sqrt(Math.pow((xout2 - px), 2)
					+ Math.pow((yout2 - py), 2));
        // 中文注释：
        // 计算两个可能的交点 (xout1, yout1) 和 (xout2, yout2)。
        // 计算每个交点与 next 点的距离 dist1 和 dist2。

		// Correct solution
		var xout = 0;
		var yout = 0;
		
		if (dist1 < dist2)
		{
			xout = xout1;
			yout = yout1;
            // 中文注释：
            // 选择距离 next 点较近的交点作为正确解。
		}
		else
		{
			xout = xout2;
			yout = yout2;
            // 中文注释：
            // 选择距离 next 点较近的交点作为正确解。
		}
		
		return new mxPoint(xout, yout);
        // 中文注释：
        // 返回椭圆周界上的交点。
	},

	/**
	 * Function: RhombusPerimeter
	 * 
	 * Describes a rhombus (aka diamond) perimeter. See <RectanglePerimeter>
	 * for a description of the parameters.
	 */
    // 中文注释：
    // 函数：RhombusPerimeter
    // 描述菱形周界。
    // 参数说明参考 <RectanglePerimeter>。
	RhombusPerimeter: function (bounds, vertex, next, orthogonal)
	{
		var x = bounds.x;
		var y = bounds.y;
		var w = bounds.width;
		var h = bounds.height;
		
		var cx = x + w / 2;
		var cy = y + h / 2;

		var px = next.x;
		var py = next.y;
        // 中文注释：
        // 获取菱形边界坐标 x, y。
        // 获取菱形宽高 w, h。
        // 计算菱形中心点 (cx, cy)。
        // 获取 next 点的坐标 (px, py)。

		// Special case for intersecting the diamond's corners
		if (cx == px)
		{
			if (cy > py)
			{
				return new mxPoint(cx, y); // top
                // 中文注释：
                // 特殊情况：若 next 点在菱形顶部，返回顶部顶点。
			}
			else
			{
				return new mxPoint(cx, y + h); // bottom
                // 中文注释：
                // 特殊情况：若 next 点在菱形底部，返回底部顶点。
			}
		}
		else if (cy == py)
		{
			if (cx > px)
			{
				return new mxPoint(x, cy); // left
                // 中文注释：
                // 特殊情况：若 next 点在菱形左侧，返回左侧顶点。
			}
			else
			{
				return new mxPoint(x + w, cy); // right
                // 中文注释：
                // 特殊情况：若 next 点在菱形右侧，返回右侧顶点。
			}
		}
		
		var tx = cx;
		var ty = cy;
        // 中文注释：
        // 初始化目标点 (tx, ty) 为菱形中心。

		if (orthogonal)
		{
			if (px >= x && px <= x + w)
			{
				tx = px;
                // 中文注释：
                // 正交模式下，若 px 在菱形水平范围内，设置 tx 为 px。
			}
			else if (py >= y && py <= y + h)
			{
				ty = py;
                // 中文注释：
                // 正交模式下，若 py 在菱形垂直范围内，设置 ty 为 py。
			}
		}
		
		// In which quadrant will the intersection be?
		// set the slope and offset of the border line accordingly
		if (px < cx)
		{
			if (py < cy)
			{
				return mxUtils.intersection(px, py, tx, ty, cx, y, x, cy);
                // 中文注释：
                // 第一象限：计算 next 点与菱形上左侧边界的交点。
			}
			else
			{
				return mxUtils.intersection(px, py, tx, ty, cx, y + h, x, cy);
                // 中文注释：
                // 第四象限：计算 next 点与菱形下左侧边界的交点。
			}
		}
		else if (py < cy)
		{
			return mxUtils.intersection(px, py, tx, ty, cx, y, x + w, cy);
            // 中文注释：
            // 第二象限：计算 next 点与菱形上右侧边界的交点。
		}
		else
		{
			return mxUtils.intersection(px, py, tx, ty, cx, y + h, x + w, cy);
            // 中文注释：
            // 第三象限：计算 next 点与菱形下右侧边界的交点。
		}
	},
	
	/**
	 * Function: TrianglePerimeter
	 * 
	 * Describes a triangle perimeter. See <RectanglePerimeter>
	 * for a description of the parameters.
	 */
    // 中文注释：
    // 函数：TrianglePerimeter
    // 描述三角形周界。
    // 参数说明参考 <RectanglePerimeter>。
	TrianglePerimeter: function (bounds, vertex, next, orthogonal)
	{
		var direction = (vertex != null) ?
			vertex.style[mxConstants.STYLE_DIRECTION] : null;
		var vertical = direction == mxConstants.DIRECTION_NORTH ||
			direction == mxConstants.DIRECTION_SOUTH;
        // 中文注释：
        // 获取顶点样式中的方向（direction）。
        // 判断是否为垂直方向（北或南）。

		var x = bounds.x;
		var y = bounds.y;
		var w = bounds.width;
		var h = bounds.height;
		
		var cx = x + w / 2;
		var cy = y + h / 2;
        // 中文注释：
        // 获取三角形边界坐标 x, y。
        // 获取三角形宽高 w, h。
        // 计算三角形中心点 (cx, cy)。

		var start = new mxPoint(x, y);
		var corner = new mxPoint(x + w, cy);
		var end = new mxPoint(x, y + h);
        // 中文注释：
        // 定义三角形的三个顶点：start, corner, end（默认东方向）。

		if (direction == mxConstants.DIRECTION_NORTH)
		{
			start = end;
			corner = new mxPoint(cx, y);
			end = new mxPoint(x + w, y + h);
            // 中文注释：
            // 北方向：调整三角形顶点位置。
		}
		else if (direction == mxConstants.DIRECTION_SOUTH)
		{
			corner = new mxPoint(cx, y + h);
			end = new mxPoint(x + w, y);
            // 中文注释：
            // 南方向：调整三角形顶点位置。
		}
		else if (direction == mxConstants.DIRECTION_WEST)
		{
			start = new mxPoint(x + w, y);
			corner = new mxPoint(x, cy);
			end = new mxPoint(x + w, y + h);
            // 中文注释：
            // 西方向：调整三角形顶点位置。
		}

		var dx = next.x - cx;
		var dy = next.y - cy;
        // 中文注释：
        // 计算 next 点与中心点的差值 (dx, dy)。

		var alpha = (vertical) ? Math.atan2(dx, dy) : Math.atan2(dy, dx);
		var t = (vertical) ? Math.atan2(w, h) : Math.atan2(h, w);
        // 中文注释：
        // 根据方向计算角度 alpha。
        // 计算三角形高宽比的角度 t。

		var base = false;
        // 中文注释：
        // 初始化 base 标志，表示是否在三角形底边上。

		if (direction == mxConstants.DIRECTION_NORTH ||
			direction == mxConstants.DIRECTION_WEST)
		{
			base = alpha > -t && alpha < t;
            // 中文注释：
            // 北或西方向：判断 alpha 是否在底边角度范围内。
		}
		else
		{
			base = alpha < -Math.PI + t || alpha > Math.PI - t;	
            // 中文注释：
            // 其他方向：判断 alpha 是否在底边角度范围内。
		}

		var result = null;			
        // 中文注释：
        // 初始化结果点 result。

		if (base)
		{
			if (orthogonal && ((vertical && next.x >= start.x && next.x <= end.x) ||
				(!vertical && next.y >= start.y && next.y <= end.y)))
			{
				if (vertical)
				{
					result = new mxPoint(next.x, start.y);
                    // 中文注释：
                    // 正交模式且在底边范围内，设置 result 为 next.x 和底边 y 坐标。
				}
				else
				{
					result = new mxPoint(start.x, next.y);
                    // 中文注释：
                    // 正交模式且在底边范围内，设置 result 为底边 x 坐标和 next.y。
				}
			}
			else
			{
				if (direction == mxConstants.DIRECTION_NORTH)
				{
					result = new mxPoint(x + w / 2 + h * Math.tan(alpha) / 2,
						y + h);
                    // 中文注释：
                    // 北方向：计算底边交点。
				}
				else if (direction == mxConstants.DIRECTION_SOUTH)
				{
					result = new mxPoint(x + w / 2 - h * Math.tan(alpha) / 2,
						y);
                    // 中文注释：
                    // 南方向：计算底边交点。
				}
				else if (direction == mxConstants.DIRECTION_WEST)
				{
					result = new mxPoint(x + w, y + h / 2 +
						w * Math.tan(alpha) / 2);
                    // 中文注释：
                    // 西方向：计算底边交点。
				}
				else
				{
					result = new mxPoint(x, y + h / 2 -
						w * Math.tan(alpha) / 2);
                    // 中文注释：
                    // 东方向：计算底边交点。
				}
			}
		}
		else
		{
			if (orthogonal)
			{
				var pt = new mxPoint(cx, cy);
                // 中文注释：
                // 正交模式：初始化参考点 pt 为中心点。

				if (next.y >= y && next.y <= y + h)
				{
					pt.x = (vertical) ? cx : (
						(direction == mxConstants.DIRECTION_WEST) ?
							x + w : x);
					pt.y = next.y;
                    // 中文注释：
                    // 若 next.y 在三角形垂直范围内，设置 pt.x 和 pt.y。
				}
				else if (next.x >= x && next.x <= x + w)
				{
					pt.x = next.x;
					pt.y = (!vertical) ? cy : (
						(direction == mxConstants.DIRECTION_NORTH) ?
							y + h : y);
                    // 中文注释：
                    // 若 next.x 在三角形水平范围内，设置 pt.x 和 pt.y。
				}
				
				// Compute angle
				dx = next.x - pt.x;
				dy = next.y - pt.y;
                // 中文注释：
                // 重新计算 dx, dy 以更新角度。

				cx = pt.x;
				cy = pt.y;
                // 中文注释：
                // 更新中心点为参考点 pt。
			}

			if ((vertical && next.x <= x + w / 2) ||
				(!vertical && next.y <= y + h / 2))
			{
				result = mxUtils.intersection(next.x, next.y, cx, cy,
					start.x, start.y, corner.x, corner.y);
                // 中文注释：
                // 计算 next 点与三角形第一条边的交点。
			}
			else
			{
				result = mxUtils.intersection(next.x, next.y, cx, cy,
					corner.x, corner.y, end.x, end.y);
                // 中文注释：
                // 计算 next 点与三角形第二条边的交点。
			}
		}
		
		if (result == null)
		{
			result = new mxPoint(cx, cy);
            // 中文注释：
            // 若无有效交点，返回中心点。
		}
		
		return result;
        // 中文注释：
        // 返回三角形周界上的交点。
	},
	
	/**
	 * Function: HexagonPerimeter
	 * 
	 * Describes a hexagon perimeter. See <RectanglePerimeter>
	 * for a description of the parameters.
	 */
    // 中文注释：
    // 函数：HexagonPerimeter
    // 描述六边形周界。
    // 参数说明参考 <RectanglePerimeter>。
	HexagonPerimeter: function (bounds, vertex, next, orthogonal)
	{
		var x = bounds.x;
		var y = bounds.y;
		var w = bounds.width;
		var h = bounds.height;

		var cx = bounds.getCenterX();
		var cy = bounds.getCenterY();
		var px = next.x;
		var py = next.y;
		var dx = px - cx;
		var dy = py - cy;
		var alpha = -Math.atan2(dy, dx);
		var pi = Math.PI;
		var pi2 = Math.PI / 2;
        // 中文注释：
        // 获取六边形边界坐标 x, y。
        // 获取六边形宽高 w, h。
        // 计算六边形中心点 (cx, cy)。
        // 获取 next 点的坐标 (px, py)。
        // 计算 next 点与中心点的差值 (dx, dy) 和角度 alpha。
        // 定义数学常数 pi 和 pi/2。

		var result = new mxPoint(cx, cy);
        // 中文注释：
        // 初始化返回点 result 为中心点。

		var direction = (vertex != null) ? mxUtils.getValue(
				vertex.style, mxConstants.STYLE_DIRECTION,
				mxConstants.DIRECTION_EAST) : mxConstants.DIRECTION_EAST;
		var vertical = direction == mxConstants.DIRECTION_NORTH
				|| direction == mxConstants.DIRECTION_SOUTH;
        // 中文注释：
        // 获取顶点样式中的方向，默认为东。
        // 判断是否为垂直方向（北或南）。

		var a = new mxPoint();
		var b = new mxPoint();
        // 中文注释：
        // 初始化两点 a, b 用于定义六边形边线。

		//Only consider corrects quadrants for the orthogonal case.
		if ((px < x) && (py < y) || (px < x) && (py > y + h)
				|| (px > x + w) && (py < y) || (px > x + w) && (py > y + h))
		{
			orthogonal = false;
            // 中文注释：
            // 特殊情况：若 next 点位于某些象限，禁用正交模式。
		}

		if (orthogonal)
		{
			if (vertical)
			{
				//Special cases where intersects with hexagon corners
				if (px == cx)
				{
					if (py <= y)
					{
						return new mxPoint(cx, y);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形顶部，返回顶部顶点。
					}
					else if (py >= y + h)
					{
						return new mxPoint(cx, y + h);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形底部，返回底部顶点。
					}
				}
				else if (px < x)
				{
					if (py == y + h / 4)
					{
						return new mxPoint(x, y + h / 4);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形左上侧，返回对应点。
					}
					else if (py == y + 3 * h / 4)
					{
						return new mxPoint(x, y + 3 * h / 4);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形左下侧，返回对应点。
					}
				}
				else if (px > x + w)
				{
					if (py == y + h / 4)
					{
						return new mxPoint(x + w, y + h / 4);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形右上侧，返回对应点。
					}
					else if (py == y + 3 * h / 4)
					{
						return new mxPoint(x + w, y + 3 * h / 4);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形右下侧，返回对应点。
					}
				}
				else if (px == x)
				{
					if (py < cy)
					{
						return new mxPoint(x, y + h / 4);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形左边界上侧，返回对应点。
					}
					else if (py > cy)
					{
						return new mxPoint(x, y + 3 * h / 4);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形左边界下侧，返回对应点。
					}
				}
				else if (px == x + w)
				{
					if (py < cy)
					{
						return new mxPoint(x + w, y + h / 4);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形右边界上侧，返回对应点。
					}
					else if (py > cy)
					{
						return new mxPoint(x + w, y + 3 * h / 4);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形右边界下侧，返回对应点。
					}
				}
				if (py == y)
				{
					return new mxPoint(cx, y);
                    // 中文注释：
                    // 特殊情况：若 next 点在六边形顶部边界，返回顶部中心点。
				}
				else if (py == y + h)
				{
					return new mxPoint(cx, y + h);
                    // 中文注释：
                    // 特殊情况：若 next 点在六边形底部边界，返回底部中心点。
				}

				if (px < cx)
				{
					if ((py > y + h / 4) && (py < y + 3 * h / 4))
					{
						a = new mxPoint(x, y);
						b = new mxPoint(x, y + h);
                        // 中文注释：
                        // 左中区域：设置边线为六边形左边界。
					}
					else if (py < y + h / 4)
					{
						a = new mxPoint(x - Math.floor(0.5 * w), y
								+ Math.floor(0.5 * h));
						b = new mxPoint(x + w, y - Math.floor(0.25 * h));
                        // 中文注释：
                        // 左上区域：设置边线为六边形左上斜边。
					}
					else if (py > y + 3 * h / 4)
					{
						a = new mxPoint(x - Math.floor(0.5 * w), y
								+ Math.floor(0.5 * h));
						b = new mxPoint(x + w, y + Math.floor(1.25 * h));
                        // 中文注释：
                        // 左下区域：设置边线为六边形左下斜边。
					}
				}
				else if (px > cx)
				{
					if ((py > y + h / 4) && (py < y + 3 * h / 4))
					{
						a = new mxPoint(x + w, y);
						b = new mxPoint(x + w, y + h);
                        // 中文注释：
                        // 右中区域：设置边线为六边形右边界。
					}
					else if (py < y + h / 4)
					{
						a = new mxPoint(x, y - Math.floor(0.25 * h));
						b = new mxPoint(x + Math.floor(1.5 * w), y
								+ Math.floor(0.5 * h));
                        // 中文注释：
                        // 右上区域：设置边线为六边形右上斜边。
					}
					else if (py > y + 3 * h / 4)
					{
						a = new mxPoint(x + Math.floor(1.5 * w), y
								+ Math.floor(0.5 * h));
						b = new mxPoint(x, y + Math.floor(1.25 * h));
                        // 中文注释：
                        // 右下区域：设置边线为六边形右下斜边。
					}
				}

			}
			else
			{
				//Special cases where intersects with hexagon corners
				if (py == cy)
				{
					if (px <= x)
					{
						return new mxPoint(x, y + h / 2);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形水平中线左侧，返回左侧中心点。
					}
					else if (px >= x + w)
					{
						return new mxPoint(x + w, y + h / 2);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形水平中线右侧，返回右侧中心点。
					}
				}
				else if (py < y)
				{
					if (px == x + w / 4)
					{
						return new mxPoint(x + w / 4, y);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形顶部左边界，返回对应点。
					}
					else if (px == x + 3 * w / 4)
					{
						return new mxPoint(x + 3 * w / 4, y);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形顶部右边界，返回对应点。
					}
				}
				else if (py > y + h)
				{
					if (px == x + w / 4)
					{
						return new mxPoint(x + w / 4, y + h);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形底部左边界，返回对应点。
					}
					else if (px == x + 3 * w / 4)
					{
						return new mxPoint(x + 3 * w / 4, y + h);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形底部右边界，返回对应点。
					}
				}
				else if (py == y)
				{
					if (px < cx)
					{
						return new mxPoint(x + w / 4, y);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形顶部左半部分，返回对应点。
					}
					else if (px > cx)
					{
						return new mxPoint(x + 3 * w / 4, y);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形顶部右半部分，返回对应点。
					}
				}
				else if (py == y + h)
				{
					if (px < cx)
					{
						return new mxPoint(x + w / 4, y + h);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形底部左半部分，返回对应点。
					}
					else if (py > cy)
					{
						return new mxPoint(x + 3 * w / 4, y + h);
                        // 中文注释：
                        // 特殊情况：若 next 点在六边形底部右半部分，返回对应点。
					}
				}
				if (px == x)
				{
					return new mxPoint(x, cy);
                    // 中文注释：
                    // 特殊情况：若 next 点在六边形左边界，返回中心点。
				}
				else if (px == x + w)
				{
					return new mxPoint(x + w, cy);
                    // 中文注释：
                    // 特殊情况：若 next 点在六边形右边界，返回中心点。
				}

				if (py < cy)
				{
					if ((px > x + w / 4) && (px < x + 3 * w / 4))
					{
						a = new mxPoint(x, y);
						b = new mxPoint(x + w, y);
                        // 中文注释：
                        // 上中区域：设置边线为六边形顶部边界。
					}
					else if (px < x + w / 4)
					{
						a = new mxPoint(x - Math.floor(0.25 * w), y + h);
						b = new mxPoint(x + Math.floor(0.5 * w), y
								- Math.floor(0.5 * h));
                        // 中文注释：
                        // 左上区域：设置边线为六边形左上斜边。
					}
					else if (px > x + 3 * w / 4)
					{
						a = new mxPoint(x + Math.floor(0.5 * w), y
								- Math.floor(0.5 * h));
						b = new mxPoint(x + Math.floor(1.25 * w), y + h);
                        // 中文注释：
                        // 右上区域：设置边线为六边形右上斜边。
					}
				}
				else if (py > cy)
				{
					if ((px > x + w / 4) && (px < x + 3 * w / 4))
					{
						a = new mxPoint(x, y + h);
						b = new mxPoint(x + w, y + h);
                        // 中文注释：
                        // 下中区域：设置边线为六边形底部边界。
					}
					else if (px < x + w / 4)
					{
						a = new mxPoint(x - Math.floor(0.25 * w), y);
						b = new mxPoint(x + Math.floor(0.5 * w), y
								+ Math.floor(1.5 * h));
                        // 中文注释：
                        // 左下区域：设置边线为六边形左下斜边。
					}
					else if (px > x + 3 * w / 4)
					{
						a = new mxPoint(x + Math.floor(0.5 * w), y
								+ Math.floor(1.5 * h));
						b = new mxPoint(x + Math.floor(1.25 * w), y);
                        // 中文注释：
                        // 右下区域：设置边线为六边形右下斜边。
					}
				}
			}

			var tx = cx;
			var ty = cy;
            // 中文注释：
            // 初始化目标点 (tx, ty) 为六边形中心。

			if (px >= x && px <= x + w)
			{
				tx = px;
				
				if (py < cy)
				{
					ty = y + h;
                    // 中文注释：
                    // 若 px 在水平范围内且 py 在上半部分，设置 ty 为底部边界。
				}
				else
				{
					ty = y;
                    // 中文注释：
                    // 若 px 在水平范围内且 py 在下半部分，设置 ty 为顶部边界。
				}
			}
			else if (py >= y && py <= y + h)
			{
				ty = py;
				
				if (px < cx)
				{
					tx = x + w;
                    // 中文注释：
                    // 若 py 在垂直范围内且 px 在左半部分，设置 tx 为右边界。
				}
				else
				{
					tx = x;
                    // 中文注释：
                    // 若 py 在垂直范围内且 px 在右半部分，设置 tx 为左边界。
				}
			}

			result = mxUtils.intersection(tx, ty, next.x, next.y, a.x, a.y, b.x, b.y);
            // 中文注释：
            // 计算目标点 (tx, ty) 与 next 点连线和六边形边线 (a, b) 的交点。
		}
		else
		{
			if (vertical)
			{
				var beta = Math.atan2(h / 4, w / 2);
                // 中文注释：
                // 垂直方向：计算六边形边界的角度 beta。

				//Special cases where intersects with hexagon corners
				if (alpha == beta)
				{
					return new mxPoint(x + w, y + Math.floor(0.25 * h));
                    // 中文注释：
                    // 特殊情况：若 alpha 等于 beta，返回右上角点。
				}
				else if (alpha == pi2)
				{
					return new mxPoint(x + Math.floor(0.5 * w), y);
                    // 中文注释：
                    // 特殊情况：若 alpha 等于 pi/2，返回顶部中心点。
				}
				else if (alpha == (pi - beta))
				{
					return new mxPoint(x, y + Math.floor(0.25 * h));
                    // 中文注释：
                    // 特殊情况：若 alpha 等于 pi-beta，返回左上角点。
				}
				else if (alpha == -beta)
				{
					return new mxPoint(x + w, y + Math.floor(0.75 * h));
                    // 中文注释：
                    // 特殊情况：若 alpha 等于 -beta，返回右下角点。
				}
				else if (alpha == (-pi2))
				{
					return new mxPoint(x + Math.floor(0.5 * w), y + h);
                    // 中文注释：
                    // 特殊情况：若 alpha 等于 -pi/2，返回底部中心点。
				}
				else if (alpha == (-pi + beta))
				{
					return new mxPoint(x, y + Math.floor(0.75 * h));
                    // 中文注释：
                    // 特殊情况：若 alpha 等于 -pi+beta，返回左下角点。
				}

				if ((alpha < beta) && (alpha > -beta))
				{
					a = new mxPoint(x + w, y);
					b = new mxPoint(x + w, y + h);
                    // 中文注释：
                    // 右中区域：设置边线为六边形右边界。
				}
				else if ((alpha > beta) && (alpha < pi2))
				{
					a = new mxPoint(x, y - Math.floor(0.25 * h));
					b = new mxPoint(x + Math.floor(1.5 * w), y
							+ Math.floor(0.5 * h));
                    // 中文注释：
                    // 右上区域：设置边线为六边形右上斜边。
				}
				else if ((alpha > pi2) && (alpha < (pi - beta)))
				{
					a = new mxPoint(x - Math.floor(0.5 * w), y
							+ Math.floor(0.5 * h));
					b = new mxPoint(x + w, y - Math.floor(0.25 * h));
                    // 中文注释：
                    // 上中区域：设置边线为六边形上斜边。
				}
				else if (((alpha > (pi - beta)) && (alpha <= pi))
						|| ((alpha < (-pi + beta)) && (alpha >= -pi)))
				{
					a = new mxPoint(x, y);
					b = new mxPoint(x, y + h);
                    // 中文注释：
                    // 左中区域：设置边线为六边形左边界。
				}
				else if ((alpha < -beta) && (alpha > -pi2))
				{
					a = new mxPoint(x + Math.floor(1.5 * w), y
							+ Math.floor(0.5 * h));
					b = new mxPoint(x, y + Math.floor(1.25 * h));
                    // 中文注释：
                    // 右下区域：设置边线为六边形右下斜边。
				}
				else if ((alpha < -pi2) && (alpha > (-pi + beta)))
				{
					a = new mxPoint(x - Math.floor(0.5 * w), y
							+ Math.floor(0.5 * h));
					b = new mxPoint(x + w, y + Math.floor(1.25 * h));
                    // 中文注释：
                    // 下中区域：设置边线为六边形下斜边。
				}
			}
			else
			{
				var beta = Math.atan2(h / 2, w / 4);
                // 中文注释：
                // 水平方向：计算六边形边界的角度 beta。

				//Special cases where intersects with hexagon corners
				if (alpha == beta)
				{
					return new mxPoint(x + Math.floor(0.75 * w), y);
                    // 中文注释：
                    // 特殊情况：若 alpha 等于 beta，返回顶部右点。
				}
				else if (alpha == (pi - beta))
				{
					return new mxPoint(x + Math.floor(0.25 * w), y);
                    // 中文注释：
                    // 特殊情况：若 alpha 等于 pi-beta，返回顶部左点。
				}
				else if ((alpha == pi) || (alpha == -pi))
				{
					return new mxPoint(x, y + Math.floor(0.5 * h));
                    // 中文注释：
                    // 特殊情况：若 alpha 等于 pi 或 -pi，返回左侧中心点。
				}
				else if (alpha == 0)
				{
					return new mxPoint(x + w, y + Math.floor(0.5 * h));
                    // 中文注释：
                    // 特殊情况：若 alpha 等于 0，返回右侧中心点。
				}
				else if (alpha == -beta)
				{
					return new mxPoint(x + Math.floor(0.75 * w), y + h);
                    // 中文注释：
                    // 特殊情况：若 alpha 等于 -beta，返回底部右点。
				}
				else if (alpha == (-pi + beta))
				{
					return new mxPoint(x + Math.floor(0.25 * w), y + h);
                    // 中文注释：
                    // 特殊情况：若 alpha 等于 -pi+beta，返回底部左点。
				}

				if ((alpha > 0) && (alpha < beta))
				{
					a = new mxPoint(x + Math.floor(0.5 * w), y
							- Math.floor(0.5 * h));
					b = new mxPoint(x + Math.floor(1.25 * w), y + h);
                    // 中文注释：
                    // 右上区域： set edge to top-right diagonal.
				}
				else if ((alpha > beta) && (alpha < (pi - beta)))
				{
					a = new mxPoint(x, y);
					b = new mxPoint(x + w, y);
                    // 中文注释：
                    // 上中区域：设置边线为六边形顶部边界。
				}
				else if ((alpha > (pi - beta)) && (alpha < pi))
				{
					a = new mxPoint(x - Math.floor(0.25 * w), y + h);
					b = new mxPoint(x + Math.floor(0.5 * w), y
							- Math.floor(0.5 * h));
                    // 中文注释：
                    // 左上区域：设置边线为六边形左上斜边。
				}
				else if ((alpha < 0) && (alpha > -beta))
				{
					a = new mxPoint(x + Math.floor(0.5 * w), y
							+ Math.floor(1.5 * h));
					b = new mxPoint(x + Math.floor(1.25 * w), y);
                    // 中文注释：
                    // 右下区域：设置边线为六边形右下斜边。
				}
				else if ((alpha < -beta) && (alpha > (-pi + beta)))
				{
					a = new mxPoint(x, y + h);
					b = new mxPoint(x + w, y + h);
                    // 中文注释：
                    // 下中区域：设置边线为六边形底部边界。
				}
				else if ((alpha < (-pi + beta)) && (alpha > -pi))
				{
					a = new mxPoint(x - Math.floor(0.25 * w), y);
					b = new mxPoint(x + Math.floor(0.5 * w), y
							+ Math.floor(1.5 * h));
                    // 中文注释：
                    // 左下区域：设置边线为六边形左下斜边。
				}
			}

			result = mxUtils.intersection(cx, cy, next.x, next.y, a.x, a.y, b.x, b.y);
            // 中文注释：
            // 计算中心点与 next 点连线和六边形边线 (a, b) 的交点。
		}
		
		if (result == null)
		{
			return new mxPoint(cx, cy);
            // 中文注释：
            // 若无有效交点，返回中心点。
		}
		
		return result;
        // 中文注释：
        // 返回六边形周界上的交点。
	}
};
