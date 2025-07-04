/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
var mxEdgeStyle =
{
	/**
	 * Class: mxEdgeStyle
	 * 
	 * Provides various edge styles to be used as the values for
	 * <mxConstants.STYLE_EDGE> in a cell style.
	 *
	 * Example:
	 * 
	 * (code)
	 * var style = stylesheet.getDefaultEdgeStyle();
	 * style[mxConstants.STYLE_EDGE] = mxEdgeStyle.ElbowConnector;
	 * (end)
	 * 
	 * Sets the default edge style to <ElbowConnector>.
	 * 
	 * Custom edge style:
	 * 
	 * To write a custom edge style, a function must be added to the mxEdgeStyle
	 * object as follows:
	 * 
	 * (code)
	 * mxEdgeStyle.MyStyle = function(state, source, target, points, result)
	 * {
	 *   if (source != null && target != null)
	 *   {
	 *     var pt = new mxPoint(target.getCenterX(), source.getCenterY());
	 * 
	 *     if (mxUtils.contains(source, pt.x, pt.y))
	 *     {
	 *       pt.y = source.y + source.height;
	 *     }
	 * 
	 *     result.push(pt);
	 *   }
	 * };
	 * (end)
	 * 
	 * In the above example, a right angle is created using a point on the
	 * horizontal center of the target vertex and the vertical center of the source
	 * vertex. The code checks if that point intersects the source vertex and makes
	 * the edge straight if it does. The point is then added into the result array,
	 * which acts as the return value of the function.
	 *
	 * The new edge style should then be registered in the <mxStyleRegistry> as follows:
	 * (code)
	 * mxStyleRegistry.putValue('myEdgeStyle', mxEdgeStyle.MyStyle);
	 * (end)
	 * 
	 * The custom edge style above can now be used in a specific edge as follows:
	 * 
	 * (code)
	 * model.setStyle(edge, 'edgeStyle=myEdgeStyle');
	 * (end)
	 * 
	 * Note that the key of the <mxStyleRegistry> entry for the function should
	 * be used in string values, unless <mxGraphView.allowEval> is true, in
	 * which case you can also use mxEdgeStyle.MyStyle for the value in the
	 * cell style above.
	 * 
	 * Or it can be used for all edges in the graph as follows:
	 * 
	 * (code)
	 * var style = graph.getStylesheet().getDefaultEdgeStyle();
	 * style[mxConstants.STYLE_EDGE] = mxEdgeStyle.MyStyle;
	 * (end)
	 * 
	 * Note that the object can be used directly when programmatically setting
	 * the value, but the key in the <mxStyleRegistry> should be used when
	 * setting the value via a key, value pair in a cell style.
	 * 
     * // 类：mxEdgeStyle
     * // 提供多种边样式，用于在单元格样式中设置 <mxConstants.STYLE_EDGE> 的值。
     * //
     * // 示例：
     * // 设置默认边样式为 <ElbowConnector>。
     * //
     * // 自定义边样式：
     * // 要创建自定义边样式，需向 mxEdgeStyle 对象添加函数，函数定义了一个右角度边，
     * // 使用目标顶点的水平中心和源顶点的垂直中心点，检查该点是否与源顶点相交，
     * // 如果相交则使边变直，并将点添加到结果数组作为函数返回值。
     * //
     * // 自定义边样式需在 <mxStyleRegistry> 中注册，以便在特定边或图中所有边上使用。
     * // 注意：设置样式时，使用 <mxStyleRegistry> 中的键名字符串，除非 <mxGraphView.allowEval> 为 true。
     *
	 * Function: EntityRelation
	 * 
	 * Implements an entity relation style for edges (as used in database
	 * schema diagrams). At the time the function is called, the result
	 * array contains a placeholder (null) for the first absolute point,
	 * that is, the point where the edge and source terminal are connected.
	 * The implementation of the style then adds all intermediate waypoints
	 * except for the last point, that is, the connection point between the
     * edge and the target terminal. The first and the last point in the
	 * result array are then replaced with mxPoints that take into account
	 * the terminal's perimeter and next point on the edge.
	 *
	 * Parameters:
	 * 
	 * state - <mxCellState> that represents the edge to be updated.
	 * source - <mxCellState> that represents the source terminal.
	 * target - <mxCellState> that represents the target terminal.
	 * points - List of relative control points.
	 * result - Array of <mxPoints> that represent the actual points of the
	 * edge.
     *
     * // 函数：EntityRelation
     * // 实现数据库模式图中使用的实体关系边样式。
     * // 函数调用时，结果数组包含第一个绝对点的占位符（null），即边与源终端连接的点。
     * // 该样式实现添加除最后一个点（边与目标终端连接点）外的所有中间路径点。
     * // 结果数组的首尾点被替换为考虑终端周界和边上下一点的 mxPoints。
     * //
     * // 参数说明：
     * // state - 表示要更新的边的 <mxCellState>。
     * // source - 表示源终端的 <mxCellState>。
     * // target - 表示目标终端的 <mxCellState>。
     * // points - 相对控制点列表。
     * // result - 表示边实际点的 <mxPoints> 数组。
	 */
	 EntityRelation: function(state, source, target, points, result)
	 {
		var view = state.view;
	 	var graph = view.graph;
	 	var segment = mxUtils.getValue(state.style,
	 			mxConstants.STYLE_SEGMENT,
	 			mxConstants.ENTITY_SEGMENT) * view.scale;
        // // 获取边样式中的段长度（STYLE_SEGMENT），若未定义则使用默认值 ENTITY_SEGMENT，并按视图缩放比例调整。
        // // 重要配置参数：segment 控制边路径点的间距，影响边绘制的外观。

		var pts = state.absolutePoints;
		var p0 = pts[0];
		var pe = pts[pts.length-1];
        // // 获取边的绝对点数组，p0 为起点，pe 为终点。
        // // 关键变量：pts 存储边的绝对路径点，p0 和 pe 分别表示起点和终点坐标。

	 	var isSourceLeft = false;
        // // 定义布尔变量 isSourceLeft，判断源终端的连接点是否在左侧，初始为 false。

	 	if (source != null)
	 	{
 			var sourceGeometry = graph.getCellGeometry(source.cell);
            // // 获取源终端的几何信息，包含位置和尺寸。

		 	if (sourceGeometry.relative)
		 	{
		 		isSourceLeft = sourceGeometry.x <= 0.5;
                // // 如果源几何位置是相对的，检查 x 坐标是否小于等于 0.5，确定连接点是否在左侧。
		 	}
		 	else if (target != null)
		 	{
		 		isSourceLeft = ((pe != null) ? pe.x : target.x + target.width) < ((p0 != null) ? p0.x : source.x);
                // // 如果存在目标终端，比较终点 x 坐标与起点 x 坐标，判断源连接点是否在左侧。
		 	}
            // // 交互逻辑：根据源和目标的相对位置动态确定连接方向，影响边的绘制起点。
	 	}

		if (p0 != null)
		{
			source = new mxCellState();
			source.x = p0.x;
			source.y = p0.y;
            // // 如果起点 p0 存在，创建一个新的源终端状态，使用 p0 的坐标。
		}
		else if (source != null)
		{
			var constraint = mxUtils.getPortConstraints(source, state, true, mxConstants.DIRECTION_MASK_NONE);
            // // 获取源终端的端口约束，决定连接方向，默认无约束。
            // // 重要配置参数：constraint 定义允许的连接方向，影响边的起点方向。

			if (constraint != mxConstants.DIRECTION_MASK_NONE && constraint != mxConstants.DIRECTION_MASK_WEST +
				mxConstants.DIRECTION_MASK_EAST)
			{
				isSourceLeft = constraint == mxConstants.DIRECTION_MASK_WEST;
                // // 如果约束不是无或东西方向，检查是否为西向（左侧）约束，设置 isSourceLeft。
			}
		}
		else
		{
			return;
            // // 如果 source 为空，直接返回，终止处理。
            // // 特殊处理：无源终端时，跳出函数，避免无效计算。
		}
	 	
	 	var isTargetLeft = true;
        // // 定义布尔变量 isTargetLeft，判断目标终端的连接点是否在左侧，初始为 true。

	 	if (target != null)
	 	{
		 	var targetGeometry = graph.getCellGeometry(target.cell);
            // // 获取目标终端的几何信息，包含位置和尺寸。

		 	if (targetGeometry.relative)
		 	{
		 		isTargetLeft = targetGeometry.x <= 0.5;
                // // 如果目标几何位置是相对的，检查 x 坐标是否小于等于 0.5，确定连接点是否在左侧。
		 	}
		 	else if (source != null)
		 	{
		 		isTargetLeft = ((p0 != null) ? p0.x : source.x + source.width) < ((pe != null) ? pe.x : target.x);
                // // 如果存在源终端，比较起点 x 坐标与终点 x 坐标，判断目标连接点是否在左侧。
		 	}
            // // 交互逻辑：根据源和目标的相对位置动态确定目标连接方向，影响边的绘制终点。
	 	}
		
		if (pe != null)
		{
			target = new mxCellState();
			target.x = pe.x;
			target.y = pe.y;
            // // 如果终点 pe 存在，创建一个新的目标终端状态，使用 pe 的坐标。
		}
		else if (target != null)
	 	{
			var constraint = mxUtils.getPortConstraints(target, state, false, mxConstants.DIRECTION_MASK_NONE);
            // // 获取目标终端的端口约束，决定连接方向，默认无约束。
            // // 重要配置参数：constraint 定义允许的连接方向，影响边的终点方向。

			if (constraint != mxConstants.DIRECTION_MASK_NONE && constraint != mxConstants.DIRECTION_MASK_WEST +
				mxConstants.DIRECTION_MASK_EAST)
			{
				isTargetLeft = constraint == mxConstants.DIRECTION_MASK_WEST;
                // // 如果约束不是无或东西方向，检查是否为西向（左侧）约束，设置 isTargetLeft。
			}
	 	}
		
		if (source != null && target != null)
		{
			var x0 = (isSourceLeft) ? source.x : source.x + source.width;
			var y0 = view.getRoutingCenterY(source);
            // // 计算源终端的连接点 x0（左侧或右侧）和 y0（垂直中心）。

			var xe = (isTargetLeft) ? target.x : target.x + target.width;
			var ye = view.getRoutingCenterY(target);
            // // 计算目标终端的连接点 xe（左侧或右侧）和 ye（垂直中心）。

			var seg = segment;
            // // 使用之前定义的段长度 seg，控制路径点的间距。

			var dx = (isSourceLeft) ? -seg : seg;
			var dep = new mxPoint(x0 + dx, y0);
            // // 计算源终端的出发点 dep，基于 x0 向左或右偏移 seg。
            // // 关键变量：dep 表示边的起始路径点。

			dx = (isTargetLeft) ? -seg : seg;
			var arr = new mxPoint(xe + dx, ye);
            // // 计算目标终端的到达点 arr，基于 xe 向左或右偏移 seg。
            // // 关键变量：arr 表示边的结束路径点。

			// Adds intermediate points if both go out on same side
            // // 如果源和目标在同一侧，添加中间路径点。
			if (isSourceLeft == isTargetLeft)
			{
				var x = (isSourceLeft) ?
					Math.min(x0, xe)-segment :
					Math.max(x0, xe)+segment;
                // // 如果在同一侧，计算中间点 x 坐标，左侧取最小值减段长，右侧取最大值加段长。

				result.push(new mxPoint(x, y0));
				result.push(new mxPoint(x, ye));
                // // 添加两个中间点到结果数组，形成垂直路径。
                // // 交互逻辑：确保边在同一侧时形成清晰的垂直折线。
			}
			else if ((dep.x < arr.x) == isSourceLeft)
			{
				var midY = y0 + (ye - y0) / 2;
                // // 如果源和目标不在同一侧，计算中间 y 坐标（源和目标 y 坐标的中点）。

				result.push(dep);
				result.push(new mxPoint(dep.x, midY));
				result.push(new mxPoint(arr.x, midY));
				result.push(arr);
                // // 添加出发点、两个中间点和到达点，形成水平-垂直-水平路径。
                // // 交互逻辑：通过中间点形成平滑的折线路径。
			}
			else
			{
				result.push(dep);
				result.push(arr);
                // // 直接连接出发点和到达点，形成直线路径。
                // // 特殊处理：当无法形成复杂路径时，使用最简单直线连接。
			}
		}
        // // 方法目的：根据源和目标终端的位置、约束及段长度，计算实体关系边的路径点，存储在 result 数组中。
	 },

	 /**
	 * Function: Loop
	 * 
	 * Implements a self-reference, aka. loop.
     *
     * // 函数：Loop
     * // 实现自引用边（即循环边），用于连接同一节点的边。
	 */
	Loop: function(state, source, target, points, result)
	{
		var pts = state.absolutePoints;
        // // 获取边的绝对点数组。

		var p0 = pts[0];
		var pe = pts[pts.length-1];
        // // 获取起点 p0 和终点 pe。

		if (p0 != null && pe != null)
		{
			if (points != null && points.length > 0)
			{
				for (var i = 0; i < points.length; i++)
				{
					var pt = points[i];
					pt = state.view.transformControlPoint(state, pt);
					result.push(new mxPoint(pt.x, pt.y));
                    // // 如果存在控制点，转换每个控制点并添加到结果数组。
				}
			}
            // // 交互逻辑：处理已有控制点的情况，确保路径点正确转换并记录。
			return;
            // // 如果起点和终点都存在，直接返回，终止处理。
            // // 特殊处理：避免重复处理已有路径点。
		}
		
		if (source != null)
		{
			var view = state.view;
			var graph = view.graph;
			var pt = (points != null && points.length > 0) ? points[0] : null;
            // // 获取视图、图对象及第一个控制点（如果存在）。

			if (pt != null)
			{
				pt = view.transformControlPoint(state, pt);
                // // 转换控制点坐标到视图坐标系。

				if (mxUtils.contains(source, pt.x, pt.y))
				{
					pt = null;
                    // // 如果控制点在源节点内，忽略该点，设为 null。
                    // // 特殊处理：避免路径点与源节点重叠。
				}
			}
			
			var x = 0;
			var dx = 0;
			var y = 0;
			var dy = 0;
            // // 初始化坐标和偏移量变量。

		 	var seg = mxUtils.getValue(state.style, mxConstants.STYLE_SEGMENT,
		 		graph.gridSize) * view.scale;
            // // 获取段长度（STYLE_SEGMENT），默认使用网格大小，并按视图缩放比例调整。
            // // 重要配置参数：seg 控制循环边的大小，影响路径点间距。
			var dir = mxUtils.getValue(state.style, mxConstants.STYLE_DIRECTION,
				mxConstants.DIRECTION_WEST);
            // // 获取方向（STYLE_DIRECTION），默认向西。
            // // 重要配置参数：dir 决定循环边的绘制方向（北、南、东、西）。

			if (dir == mxConstants.DIRECTION_NORTH ||
				dir == mxConstants.DIRECTION_SOUTH)
			{
				x = view.getRoutingCenterX(source);
				dx = seg;
                // // 如果方向为南北，设置 x 为源节点水平中心，dx 为段长度。
			}
			else
			{
				y = view.getRoutingCenterY(source);
				dy = seg;
                // // 如果方向为东西，设置 y 为源节点垂直中心，dy 为段长度。
			}
			
			if (pt == null ||
				pt.x < source.x ||
				pt.x > source.x + source.width)
			{
				if (pt != null)
				{
					x = pt.x;
					dy = Math.max(Math.abs(y - pt.y), dy);
                    // // 如果控制点存在且在源节点外部，设置 x 为控制点 x 坐标，dy 为最大垂直偏移。
				}
				else
				{
					if (dir == mxConstants.DIRECTION_NORTH)
					{
						y = source.y - 2 * dx;
					}
					else if (dir == mxConstants.DIRECTION_SOUTH)
					{
						y = source.y + source.height + 2 * dx;
					}
					else if (dir == mxConstants.DIRECTION_EAST)
					{
						x = source.x - 2 * dy;
					}
					else
					{
						x = source.x + source.width + 2 * dy;
					}
                    // // 如果无控制点，根据方向设置 x 或 y 坐标，形成循环路径。
				}
			}
			else if (pt != null)
			{
				x = view.getRoutingCenterX(source);
				dx = Math.max(Math.abs(x - pt.x), dy);
				y = pt.y;
				dy = 0;
                // // 如果控制点在源节点内，设置 x 为水平中心，dx 为最大水平偏移，y 为控制点 y。
			}
			
			result.push(new mxPoint(x - dx, y - dy));
			result.push(new mxPoint(x + dx, y + dy));
            // // 添加两个路径点到结果数组，形成循环边的路径。
            // // 交互逻辑：根据方向和控制点计算循环边的路径点，确保路径清晰。
		}
        // // 方法目的：为自引用边生成路径点，形成循环路径，考虑方向和控制点。
	},
	
	/**
	 * Function: ElbowConnector
	 * 
	 * Uses either <SideToSide> or <TopToBottom> depending on the horizontal
	 * flag in the cell style. <SideToSide> is used if horizontal is true or
	 * unspecified. See <EntityRelation> for a description of the
	 * parameters.
     *
     * // 函数：ElbowConnector
     * // 根据单元格样式的水平标志选择 <SideToSide> 或 <TopToBottom> 边样式。
     * // 如果水平标志为 true 或未指定，使用 <SideToSide>。
     * // 参数说明参考 <EntityRelation>。
	 */
	ElbowConnector: function(state, source, target, points, result)
	{
		var pt = (points != null && points.length > 0) ? points[0] : null;
        // // 获取第一个控制点（如果存在）。

		var vertical = false;
		var horizontal = false;
        // // 定义布尔变量 vertical 和 horizontal，判断路径方向。

		if (source != null && target != null)
		{
			if (pt != null)
			{
				var left = Math.min(source.x, target.x);
				var right = Math.max(source.x + source.width,
					target.x + target.width);
                // // 计算源和目标的最左和最右 x 坐标。

				var top = Math.min(source.y, target.y);
				var bottom = Math.max(source.y + source.height,
					target.y + target.height);
                // // 计算源和目标的最上和最下 y 坐标。

				pt = state.view.transformControlPoint(state, pt);
                // // 转换控制点坐标到视图坐标系。

				vertical = pt.y < top || pt.y > bottom;
				horizontal = pt.x < left || pt.x > right;
                // // 判断控制点是否在垂直或水平范围外，确定路径方向。
			}
			else
			{
				var left = Math.max(source.x, target.x);
				var right = Math.min(source.x + source.width,
					target.x + target.width);
                // // 无控制点时，计算源和目标重叠区域的左右边界。

				vertical = left == right;
                // // 如果左右边界相等，设置为垂直路径。

				if (!vertical)
				{
					var top = Math.max(source.y, target.y);
					var bottom = Math.min(source.y + source.height,
						target.y + target.height);
                    // // 计算源和目标重叠区域的上下边界。

					horizontal = top == bottom;
                    // // 如果上下边界相等，设置为水平路径。
				}
			}
            // // 交互逻辑：根据源、目标位置和控制点确定路径是水平还是垂直。
		}

		if (!horizontal && (vertical ||
			state.style[mxConstants.STYLE_ELBOW] == mxConstants.ELBOW_VERTICAL))
		{
			mxEdgeStyle.TopToBottom(state, source, target, points, result);
            // // 如果非水平或样式指定垂直折线，调用 TopToBottom 方法。
		}
		else
		{
			mxEdgeStyle.SideToSide(state, source, target, points, result);
            // // 否则调用 SideToSide 方法，绘制水平折线。
		}
        // // 方法目的：根据样式和位置选择合适的折线样式（水平或垂直），生成边路径。
        // // 重要配置参数：STYLE_ELBOW 决定折线方向，影响路径选择。
	},

	/**
	 * Function: SideToSide
	 * 
	 * Implements a vertical elbow edge. See <EntityRelation> for a description
	 * of the parameters.
     *
     * // 函数：SideToSide
     * // 实现垂直折线边样式，参数说明参考 <EntityRelation>。
	 */
	SideToSide: function(state, source, target, points, result)
	{
		var view = state.view;
		var pt = (points != null && points.length > 0) ? points[0] : null;
		var pts = state.absolutePoints;
		var p0 = pts[0];
		var pe = pts[pts.length-1];
        // // 获取视图、第一个控制点、绝对点数组、起点 p0 和终点 pe。

		if (pt != null)
		{
			pt = view.transformControlPoint(state, pt);
            // // 转换控制点坐标到视图坐标系。
		}
		
		if (p0 != null)
		{
			source = new mxCellState();
			source.x = p0.x;
			source.y = p0.y;
            // // 如果起点存在，创建新的源终端状态，使用 p0 坐标。
		}
		
		if (pe != null)
		{
			target = new mxCellState();
			target.x = pe.x;
			target.y = pe.y;
            // // 如果终点存在，创建新的目标终端状态，使用 pe 坐标。
		}
		
		if (source != null && target != null)
		{
			var l = Math.max(source.x, target.x);
			var r = Math.min(source.x + source.width,
							 target.x + target.width);
            // // 计算源和目标的左右边界，l 为最大左边界，r 为最小右边界。

			var x = (pt != null) ? pt.x : Math.round(r + (l - r) / 2);
            // // 计算中间 x 坐标，若有控制点使用其 x，否则取左右边界中点。

			var y1 = view.getRoutingCenterY(source);
			var y2 = view.getRoutingCenterY(target);
            // // 获取源和目标的垂直中心 y 坐标。

			if (pt != null)
			{
				if (pt.y >= source.y && pt.y <= source.y + source.height)
				{
					y1 = pt.y;
                    // // 如果控制点在源节点垂直范围内，使用控制点 y 作为 y1。
				}
				
				if (pt.y >= target.y && pt.y <= target.y + target.height)
				{
					y2 = pt.y;
                    // // 如果控制点在目标节点垂直范围内，使用控制点 y 作为 y2。
				}
			}
			
			if (!mxUtils.contains(target, x, y1) &&
				!mxUtils.contains(source, x, y1))
			{
				result.push(new mxPoint(x,  y1));
                // // 如果 (x, y1) 不在源或目标节点内，添加路径点。
			}
	
			if (!mxUtils.contains(target, x, y2) &&
				!mxUtils.contains(source, x, y2))
			{
				result.push(new mxPoint(x, y2));
                // // 如果 (x, y2) 不在源或目标节点内，添加路径点。
			}
	
			if (result.length == 1)
			{
				if (pt != null)
				{
					if (!mxUtils.contains(target, x, pt.y) &&
						!mxUtils.contains(source, x, pt.y))
					{
						result.push(new mxPoint(x, pt.y));
                        // // 如果只有一个路径点且控制点有效，添加控制点 y 坐标的路径点。
					}
				}
				else
				{	
					var t = Math.max(source.y, target.y);
					var b = Math.min(source.y + source.height,
							 target.y + target.height);
                    // // 计算源和目标的上下边界，t 为最大上边界，b 为最小下边界。

					result.push(new mxPoint(x, t + (b - t) / 2));
                    // // 添加上下边界中点的路径点。
				}
			}
            // // 交互逻辑：根据控制点和节点位置计算垂直折线的路径点，确保路径不与节点重叠。
		}
        // // 方法目的：生成垂直折线边的路径点，连接源和目标节点。
	},

	/**
	 * Function: TopToBottom
	 * 
	 * Implements a horizontal elbow edge. See <EntityRelation> for a
	 * description of the parameters.
     *
     * // 函数：TopToBottom
     * // 实现水平折线边样式，参数说明参考 <EntityRelation>。
	 */
	TopToBottom: function(state, source, target, points, result)
	{
		var view = state.view;
		var pt = (points != null && points.length > 0) ? points[0] : null;
		var pts = state.absolutePoints;
		var p0 = pts[0];
		var pe = pts[pts.length-1];
        // // 获取视图、第一个控制点、绝对点数组、起点 p0 和终点 pe。

		if (pt != null)
		{
			pt = view.transformControlPoint(state, pt);
            // // 转换控制点坐标到视图坐标系。
		}
		
		if (p0 != null)
		{
			source = new mxCellState();
			source.x = p0.x;
			source.y = p0.y;
            // // 如果起点存在，创建新的源终端状态，使用 p0 坐标。
		}
		
		if (pe != null)
		{
			target = new mxCellState();
			target.x = pe.x;
			target.y = pe.y;
            // // 如果终点存在，创建新的目标终端状态，使用 pe 坐标。
		}

		if (source != null && target != null)
		{
			var t = Math.max(source.y, target.y);
			var b = Math.min(source.y + source.height,
							 target.y + target.height);
            // // 计算源和目标的上下边界，t 为最大上边界，b 为最小下边界。

			var x = view.getRoutingCenterX(source);
            // // 获取源节点的水平中心 x 坐标。

			if (pt != null &&
				pt.x >= source.x &&
				pt.x <= source.x + source.width)
			{
				x = pt.x;
                // // 如果控制点在源节点水平范围内，使用控制点 x 坐标。
			}
			
			var y = (pt != null) ? pt.y : Math.round(b + (t - b) / 2);
            // // 计算中间 y 坐标，若有控制点使用其 y，否则取上下边界中点。

			if (!mxUtils.contains(target, x, y) &&
				!mxUtils.contains(source, x, y))
			{
				result.push(new mxPoint(x, y));						
                // // 如果 (x, y) 不在源或目标节点内，添加路径点。
			}
			
			if (pt != null &&
				pt.x >= target.x &&
				pt.x <= target.x + target.width)
			{
				x = pt.x;
                // // 如果控制点在目标节点水平范围内，使用控制点 x 坐标。
			}
			else
			{
				x = view.getRoutingCenterX(target);
                // // 否则使用目标节点的水平中心 x 坐标。
			}
			
			if (!mxUtils.contains(target, x, y) &&
				!mxUtils.contains(source, x, y))
			{
				result.push(new mxPoint(x, y));						
                // // 如果 (x, y) 不在源或目标节点内，添加路径点。
			}
			
			if (result.length == 1)
			{
				if (pt != null && result.length == 1)
				{
					if (!mxUtils.contains(target, pt.x, y) &&
						!mxUtils.contains(source, pt.x, y))
					{
						result.push(new mxPoint(pt.x, y));
                        // // 如果只有一个路径点且控制点有效，添加控制点 x 坐标的路径点。
					}
				}
				else
				{
					var l = Math.max(source.x, target.x);
					var r = Math.min(source.x + source.width,
							 target.x + target.width);
                    // // 计算源和目标的左右边界，l 为最大左边界，r 为最小右边界。

					result.push(new mxPoint(l + (r - l) / 2, y));
                    // // 添加左右边界中点的路径点。
				}
			}
            // // 交互逻辑：根据控制点和节点位置计算水平折线的路径点，确保路径不与节点重叠。
		}
        // // 方法目的：生成水平折线边的路径点，连接源和目标节点。
	},

	/**
	 * Function: SegmentConnector
	 * 
	 * Implements an orthogonal edge style. Use <mxEdgeSegmentHandler>
	 * as an interactive handler for this style.
	 * 
	 * state - <mxCellState> that represents the edge to be updated.
	 * sourceScaled - <mxCellState> that represents the source terminal.
	 * targetScaled - <mxCellState> that represents the target terminal.
	 * controlHints - List of relative control points.
	 * result - Array of <mxPoints> that represent the actual points of the
	 * edge.
	 *
     * // 函数：SegmentConnector
     * // 实现正交边样式，建议使用 <mxEdgeSegmentHandler> 作为交互处理器。
     * // 参数说明：
     * // state - 表示要更新的边的 <mxCellState>。
     * // sourceScaled - 表示源终端的 <mxCellState>（已缩放）。
     * // targetScaled - 表示目标终端的 <mxCellState>（已缩放）。
     * // controlHints - 相对控制点列表。
     * // result - 表示边实际点的 <mxPoints> 数组。
	 */
	SegmentConnector: function(state, sourceScaled, targetScaled, controlHints, result)
	{
		// Creates array of all way- and terminalpoints
		var pts = mxEdgeStyle.scalePointArray(state.absolutePoints, state.view.scale);
		var source = mxEdgeStyle.scaleCellState(sourceScaled, state.view.scale);
		var target = mxEdgeStyle.scaleCellState(targetScaled, state.view.scale);
		var tol = 1;
        // // 缩放绝对点数组、源和目标终端状态，设置容差 tol 为 1。
        // // 关键变量：pts 存储缩放后的路径点，source 和 target 为缩放后的终端状态。

		// Whether the first segment outgoing from the source end is horizontal
		var lastPushed = (result.length > 0) ? result[0] : null;
		var horizontal = true;
		var hint = null;
        // // 定义变量：lastPushed 记录上一个添加的路径点，horizontal 决定首段是否水平，hint 存储当前控制点。

		// Adds waypoints only if outside of tolerance
		function pushPoint(pt)
		{
			pt.x = Math.round(pt.x * state.view.scale * 10) / 10;
			pt.y = Math.round(pt.y * state.view.scale * 10) / 10;
            // // 缩放并四舍五入路径点坐标，确保精度。

			if (lastPushed == null || Math.abs(lastPushed.x - pt.x) >= tol || Math.abs(lastPushed.y - pt.y) >= Math.max(1, state.view.scale))
			{
				result.push(pt);
				lastPushed = pt;
                // // 如果与上一个点的距离超出容差，添加新路径点并更新 lastPushed。
			}
            // // 方法目的：添加路径点，确保点间距满足容差要求，避免重复点。

			return lastPushed;
		};

		// Adds the first point
		var pt = pts[0];
        // // 获取第一个绝对点。

		if (pt == null && source != null)
		{
			pt = new mxPoint(state.view.getRoutingCenterX(source), state.view.getRoutingCenterY(source));
            // // 如果起点为空，使用源节点的中心点作为起点。
		}
		else if (pt != null)
		{
			pt = pt.clone();
            // // 如果起点存在，克隆起点坐标。
		}
		
		var lastInx = pts.length - 1;
        // // 记录最后一个点的索引。

		// Adds the waypoints
		if (controlHints != null && controlHints.length > 0)
		{
			// Converts all hints and removes nulls
			var hints = [];
            // // 创建控制点数组 hints。

			for (var i = 0; i < controlHints.length; i++)
			{
				var tmp = state.view.transformControlPoint(state, controlHints[i], true);
                // // 转换控制点坐标到视图坐标系。

				if (tmp != null)
				{
					hints.push(tmp);
                    // // 如果转换后的控制点有效，添加到 hints 数组。
				}
			}
			
			if (hints.length == 0)
			{
				return;
                // // 如果无有效控制点，直接返回。
                // // 特殊处理：无控制点时终止处理。
			}
			
			// Aligns source and target hint to fixed points
			if (pt != null && hints[0] != null)
			{
				if (Math.abs(hints[0].x - pt.x) < tol)
				{
					hints[0].x = pt.x;
                    // // 如果第一个控制点与起点的 x 坐标差小于容差，对齐 x 坐标。
				}
				
				if (Math.abs(hints[0].y - pt.y) < tol)
				{
					hints[0].y = pt.y;
                    // // 如果第一个控制点与起点的 y 坐标差小于容差，对齐 y 坐标。
				}
			}
			
			var pe = pts[lastInx];
            // // 获取最后一个绝对点。

			if (pe != null && hints[hints.length - 1] != null)
			{
				if (Math.abs(hints[hints.length - 1].x - pe.x) < tol)
				{
					hints[hints.length - 1].x = pe.x;
                    // // 如果最后一个控制点与终点的 x 坐标差小于容差，对齐 x 坐标。
				}
				
				if (Math.abs(hints[hints.length - 1].y - pe.y) < tol)
				{
					hints[hints.length - 1].y = pe.y;
                    // // 如果最后一个控制点与终点的 y 坐标差小于容差，对齐 y 坐标。
				}
			}
			
			hint = hints[0];
            // // 获取第一个控制点。

			var currentTerm = source;
			var currentPt = pts[0];
			var hozChan = false;
			var vertChan = false;
			var currentHint = hint;
            // // 初始化变量：currentTerm 为当前终端，currentPt 为当前点，hozChan 和 vertChan 判断是否在水平或垂直通道内，currentHint 为当前控制点。

			if (currentPt != null)
			{
				currentTerm = null;
                // // 如果当前点存在，清除当前终端。
			}
			
			// Check for alignment with fixed points and with channels
			// at source and target segments only
			for (var i = 0; i < 2; i++)
			{
				var fixedVertAlign = currentPt != null && currentPt.x == currentHint.x;
				var fixedHozAlign = currentPt != null && currentPt.y == currentHint.y;
                // // 检查当前点是否与控制点在垂直或水平方向对齐。

				var inHozChan = currentTerm != null && (currentHint.y >= currentTerm.y &&
						currentHint.y <= currentTerm.y + currentTerm.height);
				var inVertChan = currentTerm != null && (currentHint.x >= currentTerm.x &&
						currentHint.x <= currentTerm.x + currentTerm.width);
                // // 检查控制点是否在终端的水平或垂直通道内。

				hozChan = fixedHozAlign || (currentPt == null && inHozChan);
				vertChan = fixedVertAlign || (currentPt == null && inVertChan);
                // // 设置水平或垂直通道标志。

				// If the current hint falls in both the hor and vert channels in the case
				// of a floating port, or if the hint is exactly co-incident with a 
				// fixed point, ignore the source and try to work out the orientation
				// from the target end
				if (i==0 && ((hozChan && vertChan) || (fixedVertAlign && fixedHozAlign)))
				{
                    // // 如果控制点同时在水平和垂直通道内，或完全与固定点重合，忽略源端，尝试从目标端确定方向。
                    // // 特殊处理：处理浮动端口或重合点的情况。
				}
				else
				{
					if (currentPt != null && (!fixedHozAlign && !fixedVertAlign) && (inHozChan || inVertChan)) 
					{
						horizontal = inHozChan ? false : true;
						break;
                        // // 如果当前点存在且不在固定对齐位置，但在通道内，设置方向（水平或垂直）。
					}
			
					if (vertChan || hozChan)
					{
						horizontal = hozChan;
                        // // 如果在垂直或水平通道内，设置方向为水平通道。

						if (i == 1)
						{
							// Work back from target end
							horizontal = hints.length % 2 == 0 ? hozChan : vertChan;
                            // // 从目标端回溯，控制点数量决定方向（偶数为水平，奇数为垂直）。
						}
	
						break;
					}
				}
				
				currentTerm = target;
				currentPt = pts[lastInx];
                // // 更新当前终端为目标，当前点为最后一个绝对点。

				if (currentPt != null)
				{
					currentTerm = null;
                    // // 如果当前点存在，清除当前终端。
				}
				
				currentHint = hints[hints.length - 1];
                // // 更新当前控制点为最后一个控制点。

				if (fixedVertAlign && fixedHozAlign)
				{
					hints = hints.slice(1);
                    // // 如果垂直和水平都固定对齐，移除第一个控制点。
				}
			}

			if (horizontal && ((pts[0] != null && pts[0].y != hint.y) ||
				(pts[0] == null && source != null &&
				(hint.y < source.y || hint.y > source.y + source.height))))
			{
				pushPoint(new mxPoint(pt.x, hint.y));
                // // 如果为水平方向且起点与控制点 y 坐标不一致，添加路径点。
			}
			else if (!horizontal && ((pts[0] != null && pts[0].x != hint.x) ||
					(pts[0] == null && source != null &&
					(hint.x < source.x || hint.x > source.x + source.width))))
			{
				pushPoint(new mxPoint(hint.x, pt.y));
                // // 如果为垂直方向且起点与控制点 x 坐标不一致，添加路径点。
			}
			
			if (horizontal)
			{
				pt.y = hint.y;
                // // 如果为水平方向，设置路径点 y 为控制点 y。
			}
			else
			{
				pt.x = hint.x;
                // // 如果为垂直方向，设置路径点 x 为控制点 x。
			}
		
			for (var i = 0; i < hints.length; i++)
			{
				horizontal = !horizontal;
				hint = hints[i];
                // // 切换方向并更新当前控制点。

//				mxLog.show();
//				mxLog.debug('hint', i, hint.x, hint.y);
				
				if (horizontal)
				{
					pt.y = hint.y;
                    // // 如果为水平方向，设置路径点 y 为控制点 y。
				}
				else
				{
					pt.x = hint.x;
                    // // 如果为垂直方向，设置路径点 x 为控制点 x。
				}
		
				pushPoint(pt.clone());
                // // 添加克隆的路径点到结果数组。
			}
		}
		else
		{
			hint = pt;
			// FIXME: First click in connect preview toggles orientation
			horizontal = true;
            // // 如果无控制点，使用起点作为 hint，设置默认方向为水平。
            // // 特殊处理：处理无控制点的情况，确保路径生成。
		}

		// Adds the last point
		pt = pts[lastInx];
        // // 获取最后一个绝对点。

		if (pt == null && target != null)
		{
			pt = new mxPoint(state.view.getRoutingCenterX(target), state.view.getRoutingCenterY(target));
            // // 如果终点为空，使用目标节点的中心点作为终点。
		}
		
		if (pt != null)
		{
			if (hint != null)
			{
				if (horizontal && ((pts[lastInx] != null && pts[lastInx].y != hint.y) ||
					(pts[lastInx] == null && target != null &&
					(hint.y < target.y || hint.y > target.y + target.height))))
				{
					pushPoint(new mxPoint(pt.x, hint.y));
                    // // 如果为水平方向且终点与控制点 y 坐标不一致，添加路径点。
				}
				else if (!horizontal && ((pts[lastInx] != null && pts[lastInx].x != hint.x) ||
						(pts[lastInx] == null && target != null &&
						(hint.x < target.x || hint.x > target.x + target.width))))
				{
					pushPoint(new mxPoint(hint.x, pt.y));
                    // // 如果为垂直方向且终点与控制点 x 坐标不一致，添加路径点。
				}
			}
		}
		
		// Removes bends inside the source terminal for floating ports
		if (pts[0] == null && source != null)
		{
			while (result.length > 1 && result[1] != null &&
				mxUtils.contains(source, result[1].x, result[1].y))
			{
				result.splice(1, 1);
                // // 如果起点为空且路径点在源节点内，移除该点（处理浮动端口）。
                // // 特殊处理：避免路径点与源节点重叠。
			}
		}
		
		// Removes bends inside the target terminal
		if (pts[lastInx] == null && target != null)
		{
			while (result.length > 1 && result[result.length - 1] != null &&
				mxUtils.contains(target, result[result.length - 1].x, result[result.length - 1].y))
			{
				result.splice(result.length - 1, 1);
                // // 如果终点为空且路径点在目标节点内，移除该点。
                // // 特殊处理：避免路径点与目标节点重叠。
			}
		}
		
		// Removes last point if inside tolerance with end point
		if (pe != null && result[result.length - 1] != null &&
			Math.abs(pe.x - result[result.length - 1].x) <= tol &&
			Math.abs(pe.y - result[result.length - 1].y) <= tol)
		{
			result.splice(result.length - 1, 1);
            // // 如果最后一个路径点与终点坐标差在容差内，移除该点。

			// Lines up second last point in result with end point
			if (result[result.length - 1] != null)
			{
				if (Math.abs(result[result.length - 1].x - pe.x) < tol)
				{
					result[result.length - 1].x = pe.x;
                    // // 如果倒数第二个点的 x 坐标与终点差在容差内，对齐 x 坐标。
				}
				
				if (Math.abs(result[result.length - 1].y - pe.y) < tol)
				{
					result[result.length - 1].y = pe.y;
                    // // 如果倒数第二个点的 y 坐标与终点差在容差内，对齐 y 坐标。
				}
			}
            // // 特殊处理：确保路径点与终点对齐，优化路径平滑性。
		}
        // // 方法目的：生成正交边路径点，处理控制点和终端位置，确保路径平滑且符合正交规则。
	},
	
	orthBuffer: 10,
    // // 重要配置参数：orthBuffer 定义正交边样式的缓冲区大小，影响路径点与节点之间的距离。

	orthPointsFallback: true,
    // // 重要配置参数：orthPointsFallback 控制是否在正交路径失败时回退到其他连接方式（默认 true）。

	dirVectors: [ [ -1, 0 ],
			[ 0, -1 ], [ 1, 0 ], [ 0, 1 ], [ -1, 0 ], [ 0, -1 ], [ 1, 0 ] ],
    // // 关键变量：dirVectors 定义方向向量（西、北、东、南），用于正交路径计算。

	wayPoints1: [ [ 0, 0], [ 0, 0],  [ 0, 0], [ 0, 0], [ 0, 0],  [ 0, 0],
	              [ 0, 0],  [ 0, 0], [ 0, 0],  [ 0, 0], [ 0, 0],  [ 0, 0] ],
    // // 关键变量：wayPoints1 存储正交路径的临时路径点坐标。

	routePatterns: [
		[ [ 513, 2308, 2081, 2562 ], [ 513, 1090, 514, 2184, 2114, 2561 ],
			[ 513, 1090, 514, 2564, 2184, 2562 ],
			[ 513, 2308, 2561, 1090, 514, 2568, 2308 ] ],
	[ [ 514, 1057, 513, 2308, 2081, 2562 ], [ 514, 2184, 2114, 2561 ],
			[ 514, 2184, 2562, 1057, 513, 2564, 2184 ],
			[ 514, 1057, 513, 2568, 2308, 2561 ] ],
	[ [ 1090, 514, 1057, 513, 2308, 2081, 2562 ], [ 2114, 2561 ],
			[ 1090, 2562, 1057, 513, 2564, 2184 ],
			[ 1090, 514, 1057, 513, 2308, 2561, 2568 ] ],
	[ [ 2081, 2562 ], [ 1057, 513, 1090, 514, 2184, 2114, 2561 ],
			[ 1057, 513, 1090, 514, 2184, 2562, 2564 ],
			[ 1057, 2561, 1090, 514, 2568, 2308 ] ] ],
    // // 关键变量：routePatterns 定义正交路径的模式，基于方向和位置组合。

	inlineRoutePatterns: [
			[ null, [ 2114, 2568 ], null, null ],
			[ null, [ 514, 2081, 2114, 2568 ] , null, null ],
			[ null, [ 2114, 2561 ], null, null ],
			[ [ 2081, 2562 ], [ 1057, 2114, 2568 ],
					[ 2184, 2562 ],
					null ] ],
    // // 关键变量：inlineRoutePatterns 定义内联路径模式，用于特殊方向组合。

	vertexSeperations: [],
    // // 关键变量：vertexSeperations 存储源和目标节点之间的距离，用于路径计算。

	limits: [
	       [ 0, 0, 0, 0, 0, 0, 0, 0, 0 ],
	       [ 0, 0, 0, 0, 0, 0, 0, 0, 0 ] ],
    // // 关键变量：limits 存储源和目标节点的边界限制，用于路径点约束。

	LEFT_MASK: 32,

	TOP_MASK: 64,

	RIGHT_MASK: 128,

	BOTTOM_MASK: 256,

	LEFT: 1,

	TOP: 2,

	RIGHT: 4,

	BOTTOM: 8,
    // // 重要配置参数：定义方向掩码（LEFT_MASK, TOP_MASK 等）和方向常量（LEFT, TOP 等），用于控制路径方向。

	// TODO remove magic numbers
	SIDE_MASK: 480,
	//mxEdgeStyle.LEFT_MASK | mxEdgeStyle.TOP_MASK | mxEdgeStyle.RIGHT_MASK
	//| mxEdgeStyle.BOTTOM_MASK,
	// // 重要配置参数：SIDE_MASK 组合所有边界方向掩码，用于路径约束。

	CENTER_MASK: 512,

	SOURCE_MASK: 1024,

	TARGET_MASK: 2048,

	VERTEX_MASK: 3072,
	// mxEdgeStyle.SOURCE_MASK | mxEdgeStyle.TARGET_MASK,
	// // 重要配置参数：定义中心、源、目标和顶点掩码，用于路径点定位和约束。
	
	getJettySize: function(state, isSource)
	{
		var value = mxUtils.getValue(state.style, (isSource) ? mxConstants.STYLE_SOURCE_JETTY_SIZE :
			mxConstants.STYLE_TARGET_JETTY_SIZE, mxUtils.getValue(state.style,
					mxConstants.STYLE_JETTY_SIZE, mxEdgeStyle.orthBuffer));
        // // 获取源或目标的 jetty 大小，若未定义则使用默认 jetty 大小或 orthBuffer。
        // // 重要配置参数：STYLE_SOURCE_JETTY_SIZE 和 STYLE_TARGET_JETTY_SIZE 控制边与终端的连接间距。

		if (value == 'auto')
		{
			// Computes the automatic jetty size
			var type = mxUtils.getValue(state.style, (isSource) ? mxConstants.STYLE_STARTARROW : mxConstants.STYLE_ENDARROW, mxConstants.NONE);
            // // 获取箭头类型（起点或终点），默认无箭头。
            // // 重要配置参数：STYLE_STARTARROW 和 STYLE_ENDARROW 决定箭头样式。

			if (type != mxConstants.NONE)
			{
				var size = mxUtils.getNumber(state.style, (isSource) ? mxConstants.STYLE_STARTSIZE : mxConstants.STYLE_ENDSIZE, mxConstants.DEFAULT_MARKERSIZE);
				value = Math.max(2, Math.ceil((size + mxEdgeStyle.orthBuffer) / mxEdgeStyle.orthBuffer)) * mxEdgeStyle.orthBuffer;
                // // 如果有箭头，计算 jetty 大小，基于箭头大小和 orthBuffer。
			}
			else
			{
				value = 2 * mxEdgeStyle.orthBuffer;
                // // 如果无箭头，jetty 大小为 orthBuffer 的两倍。
			}
		}
        // // 交互逻辑：根据箭头样式动态计算 jetty 大小，影响边与终端的连接间距。

		return value;
        // // 方法目的：返回 jetty 大小，确保边与终端的连接点适当间隔。
	},
	
	/**
	 * Function: scalePointArray
	 * 
	 * Scales an array of <mxPoint>
	 * 
	 * Parameters:
	 * 
	 * points - array of <mxPoint> to scale
	 * scale - the scaling to divide by
	 * 
     * // 函数：scalePointArray
     * // 缩放 <mxPoint> 数组。
     * // 参数说明：
     * // points - 要缩放的 <mxPoint> 数组。
     * // scale - 缩放比例，用于除法运算。
	 */
	scalePointArray: function(points, scale)
	{
		var result = [];
        // // 创建结果数组存储缩放后的点。

		if (points != null)
		{
			for (var i = 0; i < points.length; i++)
			{
				if (points[i] != null)
				{
					var pt = new mxPoint(Math.round(points[i].x / scale * 10) / 10,
										Math.round(points[i].y / scale * 10) / 10);
					result[i] = pt;
                    // // 缩放并四舍五入每个点的 x 和 y 坐标，添加到结果数组。
				}
				else
				{
					result[i] = null;
                    // // 如果点为空，添加 null 到结果数组。
				}
			}
		}
		else
		{
			result = null;
            // // 如果输入点数组为空，返回 null。
		}
		
		return result;
        // // 方法目的：返回缩放后的点数组，用于调整路径点坐标。
	},
	
	/**
	 * Function: scaleCellState
	 * 
	 * Scales an <mxCellState>
	 * 
	 * Parameters:
	 * 
	 * state - <mxCellState> to scale
	 * scale - the scaling to divide by
	 * 
     * // 函数：scaleCellState
     * // 缩放 <mxCellState>。
     * // 参数说明：
     * // state - 要缩放的 <mxCellState>。
     * // scale - 缩放比例，用于除法运算。
	 */
	scaleCellState: function(state, scale)
	{
		var result = null;

		if (state != null)
		{
			result = state.clone();
			result.setRect(Math.round(state.x / scale * 10) / 10,
							Math.round(state.y / scale * 10) / 10,
							Math.round(state.width / scale * 10) / 10,
							Math.round(state.height / scale * 10) / 10);
            // // 克隆状态并缩放其 x、y、宽度和高度，四舍五入以确保精度。
		}
		else
		{
			result = null;
            // // 如果状态为空，返回 null。
		}
		
		return result;
        // // 方法目的：返回缩放后的单元格状态，用于调整终端位置和大小。
	},

	/**
	 * Function: OrthConnector
	 * 
	 * Implements a local orthogonal router between the given
	 * cells.
	 * 
	 * Parameters:
	 * 
	 * state - <mxCellState> that represents the edge to be updated.
	 * sourceScaled - <mxCellState> that represents the source terminal.
	 * targetScaled - <mxCellState> that represents the target terminal.
	 * controlHints - List of relative control points.
	 * result - Array of <mxPoints> that represent the actual points of the
	 * edge.
	 * 
     * // 函数：OrthConnector
     * // 实现给定单元格之间的局部正交路由。
     * // 参数说明：
     * // state - 表示要更新的边的 <mxCellState>。
     * // sourceScaled - 表示源终端的 <mxCellState>（已缩放）。
     * // targetScaled - 表示目标终端的 <mxCellState>（已缩放）。
     * // controlHints - 相对控制点列表。
     * // result - 表示边实际点的 <mxPoints> 数组。
	 */
	OrthConnector: function(state, sourceScaled, targetScaled, controlHints, result)
	{
		var graph = state.view.graph;
		var sourceEdge = source == null ? false : graph.getModel().isEdge(source.cell);
		var targetEdge = target == null ? false : graph.getModel().isEdge(target.cell);
        // // 检查源和目标是否为边，影响路由逻辑。
        // // 关键变量：sourceEdge 和 targetEdge 判断终端是否为边。

		var pts = mxEdgeStyle.scalePointArray(state.absolutePoints, state.view.scale);
		var source = mxEdgeStyle.scaleCellState(sourceScaled, state.view.scale);
		var target = mxEdgeStyle.scaleCellState(targetScaled, state.view.scale);
        // // 缩放绝对点数组、源和目标终端状态。

		var p0 = pts[0];
		var pe = pts[pts.length-1];
        // // 获取起点 p0 和终点 pe。

		var sourceX = source != null ? source.x : p0.x;
		var sourceY = source != null ? source.y : p0.y;
		var sourceWidth = source != null ? source.width : 0;
		var sourceHeight = source != null ? source.height : 0;
        // // 获取源节点的 x、y 坐标、宽度和高度，若无源节点使用起点坐标。

		var targetX = target != null ? target.x : pe.x;
		var targetY = target != null ? target.y : pe.y;
		var targetWidth = target != null ? target.width : 0;
		var targetHeight = target != null ? target.height : 0;
        // // 获取目标节点的 x、y 坐标、宽度和高度，若无目标节点使用终点坐标。

		var sourceBuffer = mxEdgeStyle.getJettySize(state, true);
		var targetBuffer = mxEdgeStyle.getJettySize(state, false);
        // // 获取源和目标的 jetty 大小，控制边与终端的间距。
        // // 重要配置参数：sourceBuffer 和 targetBuffer 决定边与节点的连接距离。

		//console.log('sourceBuffer', sourceBuffer);
		//console.log('targetBuffer', targetBuffer);
		// Workaround for loop routing within buffer zone
		if (source != null && target == source)
		{
			targetBuffer = Math.max(sourceBuffer, targetBuffer);
			sourceBuffer = targetBuffer;
            // // 如果源和目标相同（自循环），取最大 jetty 大小，确保路径一致。
            // // 特殊处理：处理自循环边，确保缓冲区一致。
		}
		
		var totalBuffer = targetBuffer + sourceBuffer;
		// // 计算总缓冲区大小。
		// console.log('totalBuffer', totalBuffer);
		var tooShort = false;
        // // 定义布尔变量 tooShort，判断路径是否过短。

		// Checks minimum distance for fixed points and falls back to segment connector
		if (p0 != null && pe != null)
		{
			var dx = pe.x - p0.x;
			var dy = pe.y - p0.y;
            // // 计算起点和终点的 x 和 y 差值。

			tooShort = dx * dx + dy * dy < totalBuffer * totalBuffer;
            // // 如果起点和终点距离小于总缓冲区平方，标记路径过短。
		}

		if (tooShort || (mxEdgeStyle.orthPointsFallback && (controlHints != null &&
				controlHints.length > 0)) || sourceEdge || targetEdge)
		{
			mxEdgeStyle.SegmentConnector(state, sourceScaled, targetScaled, controlHints, result);
            // // 如果路径过短、存在控制点或终端为边，回退到 SegmentConnector 方法。
            // // 特殊处理：处理路径过短或复杂情况，回退到正交连接器。
			return;
		}

		// Determine the side(s) of the source and target vertices
		// that the edge may connect to
		// portConstraint [source, target]
		var portConstraint = [mxConstants.DIRECTION_MASK_ALL, mxConstants.DIRECTION_MASK_ALL];
		var rotation = 0;
        // // 定义端口约束数组 portConstraint 和旋转角度 rotation。
        // // 重要配置参数：portConstraint 控制源和目标的连接方向。

		if (source != null)
		{
			portConstraint[0] = mxUtils.getPortConstraints(source, state, true, 
					mxConstants.DIRECTION_MASK_ALL);
			rotation = mxUtils.getValue(source.style, mxConstants.STYLE_ROTATION, 0);
            // // 获取源节点的端口约束和旋转角度。
            // // 重要配置参数：STYLE_ROTATION 控制节点的旋转角度，影响路径计算。

			//console.log('source rotation', rotation);
			
			if (rotation != 0)
			{
				var newRect = mxUtils.getBoundingBox(new mxRectangle(sourceX, sourceY, sourceWidth, sourceHeight), rotation);
				sourceX = newRect.x; 
				sourceY = newRect.y;
				sourceWidth = newRect.width;
				sourceHeight = newRect.height;
                // // 如果源节点有旋转，重新计算边界框，更新源节点的坐标和尺寸。
			}
		}

		if (target != null)
		{
			portConstraint[1] = mxUtils.getPortConstraints(target, state, false,
				mxConstants.DIRECTION_MASK_ALL);
			rotation = mxUtils.getValue(target.style, mxConstants.STYLE_ROTATION, 0);
            // // 获取目标节点的端口约束和旋转角度。

			//console.log('target rotation', rotation);

			if (rotation != 0)
			{
				var newRect = mxUtils.getBoundingBox(new mxRectangle(targetX, targetY, targetWidth, targetHeight), rotation);
				targetX = newRect.x;
				targetY = newRect.y;
				targetWidth = newRect.width;
				targetHeight = newRect.height;
                // // 如果目标节点有旋转，重新计算边界框，更新目标节点的坐标和尺寸。
			}
		}

		//console.log('source' , sourceX, sourceY, sourceWidth, sourceHeight);
		//console.log('targetX' , targetX, targetY, targetWidth, targetHeight);

		var dir = [0, 0];
        // // 定义方向数组 dir，存储源和目标的连接方向。

		// Work out which faces of the vertices present against each other
		// in a way that would allow a 3-segment connection if port constraints
		// permitted.
		// geo -> [source, target] [x, y, width, height]
		var geo = [ [sourceX, sourceY, sourceWidth, sourceHeight] ,
		            [targetX, targetY, targetWidth, targetHeight] ];
		var buffer = [sourceBuffer, targetBuffer];
        // // 定义几何信息数组 geo（源和目标的坐标及尺寸）和缓冲区数组 buffer。
        // // 关键变量：geo 存储源和目标的几何信息，buffer 存储 jetty 大小。

		for (var i = 0; i < 2; i++)
		{
			mxEdgeStyle.limits[i][1] = geo[i][0] - buffer[i];
			mxEdgeStyle.limits[i][2] = geo[i][1] - buffer[i];
			mxEdgeStyle.limits[i][4] = geo[i][0] + geo[i][2] + buffer[i];
			mxEdgeStyle.limits[i][8] = geo[i][1] + geo[i][3] + buffer[i];
            // // 计算源和目标的边界限制（左、上、右、下），考虑缓冲区。
		}
		
		// Work out which quad the target is in
		var sourceCenX = geo[0][0] + geo[0][2] / 2.0;
		var sourceCenY = geo[0][1] + geo[0][3] / 2.0;
		var targetCenX = geo[1][0] + geo[1][2] / 2.0;
		var targetCenY = geo[1][1] + geo[1][3] / 2.0;
        // // 计算源和目标节点的中心点坐标。

		var dx = sourceCenX - targetCenX;
		var dy = sourceCenY - targetCenY;
        // // 计算源和目标中心点的 x 和 y 差值。

		var quad = 0;
        // // 定义象限变量 quad，确定目标节点相对于源节点的方位。

		// 0 | 1
		// -----
		// 3 | 2
		
		if (dx < 0)
		{
			if (dy < 0)
			{
				quad = 2;
                // // 如果 dx 和 dy 均为负，目标在第二象限。
			}
			else
			{
				quad = 1;
                // // 如果 dx 负，dy 非负，目标在第一象限。
			}
		}
		else
		{
			if (dy <= 0)
			{
				quad = 3;
                // // 如果 dx 非负，dy 负或零，目标在第三象限。

				// Special case on x = 0 and negative y
				if (dx == 0)
				{
					quad = 2;
                    // // 特殊情况：如果 dx 为 0 且 dy 负，目标在第二象限。
				}
			}
		}
        // // 交互逻辑：根据源和目标的相对位置确定象限，影响路径方向。

		//console.log('quad', quad);

		// Check for connection constraints
		var currentTerm = null;
        // // 定义当前终端变量 currentTerm。

		if (source != null)
		{
			currentTerm = p0;
            // // 如果源存在，设置当前终端为起点。
		}

		var constraint = [ [0.5, 0.5] , [0.5, 0.5] ];
        // // 定义约束数组 constraint，存储源和目标的相对连接点（默认中心）。

		for (var i = 0; i < 2; i++)
		{
			if (currentTerm != null)
			{
				constraint[i][0] = (currentTerm.x - geo[i][0]) / geo[i][2];
                // // 计算当前终端相对于节点的 x 相对位置。

				if (Math.abs(currentTerm.x - geo[i][0]) <= 1)
				{
					dir[i] = mxConstants.DIRECTION_MASK_WEST;
                    // // 如果 x 坐标接近节点左边界，设置方向为西。
				}
				else if (Math.abs(currentTerm.x - geo[i][0] - geo[i][2]) <= 1)
				{
					dir[i] = mxConstants.DIRECTION_MASK_EAST;
                    // // 如果 x 坐标接近节点右边界，设置方向为东。
				}

				constraint[i][1] = (currentTerm.y - geo[i][1]) / geo[i][3];
                // // 计算当前终端相对于节点的 y 相对位置。

				if (Math.abs(currentTerm.y - geo[i][1]) <= 1)
				{
					dir[i] = mxConstants.DIRECTION_MASK_NORTH;
                    // // 如果 y 坐标接近节点上边界，设置方向为北。
				}
				else if (Math.abs(currentTerm.y - geo[i][1] - geo[i][3]) <= 1)
				{
					dir[i] = mxConstants.DIRECTION_MASK_SOUTH;
                    // // 如果 y 坐标接近节点下边界，设置方向为南。
				}
			}

			currentTerm = null;
			
			if (target != null)
			{
				currentTerm = pe;
                // // 如果目标存在，设置当前终端为终点。
			}
		}
        // // 交互逻辑：根据终端位置和节点边界确定连接方向。

		var sourceTopDist = geo[0][1] - (geo[1][1] + geo[1][3]);
		var sourceLeftDist = geo[0][0] - (geo[1][0] + geo[1][2]);
		var sourceBottomDist = geo[1][1] - (geo[0][1] + geo[0][3]);
		var sourceRightDist = geo[1][0] - (geo[0][0] + geo[0][2]);
        // // 计算源和目标节点间的上下左右距离。

		mxEdgeStyle.vertexSeperations[1] = Math.max(sourceLeftDist - totalBuffer, 0);
		mxEdgeStyle.vertexSeperations[2] = Math.max(sourceTopDist - totalBuffer, 0);
		mxEdgeStyle.vertexSeperations[4] = Math.max(sourceBottomDist - totalBuffer, 0);
		mxEdgeStyle.vertexSeperations[3] = Math.max(sourceRightDist - totalBuffer, 0);
        // // 计算节点间净距离（减去缓冲区），存储在 vertexSeperations 中。

		//==============================================================
		// Start of source and target direction determination

		// Work through the preferred orientations by relative positioning
		// of the vertices and list them in preferred and available order
		
		var dirPref = [];
		var horPref = [];
		var vertPref = [];
        // // 定义数组 dirPref、horPref 和 vertPref，存储首选方向和水平/垂直方向偏好。

		horPref[0] = (sourceLeftDist >= sourceRightDist) ? mxConstants.DIRECTION_MASK_WEST
				: mxConstants.DIRECTION_MASK_EAST;
		vertPref[0] = (sourceTopDist >= sourceBottomDist) ? mxConstants.DIRECTION_MASK_NORTH
				: mxConstants.DIRECTION_MASK_SOUTH;
        // // 根据源节点与目标节点的相对距离，设置首选水平和垂直方向。

		horPref[1] = mxUtils.reversePortConstraints(horPref[0]);
		vertPref[1] = mxUtils.reversePortConstraints(vertPref[0]);
		// // 设置次选方向（反转首选方向）。
       // // 交互逻辑：通过反转首选方向提供备选连接方向，增加路径选择的灵活性。
		var preferredHorizDist = sourceLeftDist >= sourceRightDist ? sourceLeftDist
				: sourceRightDist;
		var preferredVertDist = sourceTopDist >= sourceBottomDist ? sourceTopDist
				: sourceBottomDist;
        // // 计算首选水平和垂直距离，选择源节点与目标节点间较大的水平和垂直距离。
        // // 关键变量：preferredHorizDist 和 preferredVertDist 用于确定路径的优先方向。

		var prefOrdering = [ [0, 0] , [0, 0] ];
		var preferredOrderSet = false;
        // // 定义首选方向顺序数组 prefOrdering 和标志 preferredOrderSet，初始为 false。
        // // 关键变量：prefOrdering 存储源和目标的首选方向组合，preferredOrderSet 标记是否已设置方向顺序。

		// If the preferred port isn't available, switch it
		for (var i = 0; i < 2; i++)
		{
			if (dir[i] != 0x0)
			{
				continue;
                // // 如果方向已确定，跳过当前循环。
			}

			if ((horPref[i] & portConstraint[i]) == 0)
			{
				horPref[i] = mxUtils.reversePortConstraints(horPref[i]);
                // // 如果首选水平方向不可用，切换为反向。
			}

			if ((vertPref[i] & portConstraint[i]) == 0)
			{
				vertPref[i] = mxUtils.reversePortConstraints(vertPref[i]);
                // // 如果首选垂直方向不可用，切换为反向。
			}

			prefOrdering[i][0] = vertPref[i];
			prefOrdering[i][1] = horPref[i];
            // // 更新首选方向顺序，优先垂直方向，其次水平方向。
		}
        // // 交互逻辑：根据端口约束调整首选方向，确保路径符合约束条件。

		if (preferredVertDist > 0
				&& preferredHorizDist > 0)
		{
			// Possibility of two segment edge connection
			if (((horPref[0] & portConstraint[0]) > 0)
					&& ((vertPref[1] & portConstraint[1]) > 0))
			{
				prefOrdering[0][0] = horPref[0];
				prefOrdering[0][1] = vertPref[0];
				prefOrdering[1][0] = vertPref[1];
				prefOrdering[1][1] = horPref[1];
				preferredOrderSet = true;
                // // 如果首选水平和垂直方向均可用，设置两段式边连接的顺序（源水平，目标垂直）。
			}
			else if (((vertPref[0] & portConstraint[0]) > 0)
					&& ((horPref[1] & portConstraint[1]) > 0))
			{
				prefOrdering[0][0] = vertPref[0];
				prefOrdering[0][1] = horPref[0];
				prefOrdering[1][0] = horPref[1];
				prefOrdering[1][1] = vertPref[1];
				preferredOrderSet = true;
                // // 或者设置反向两段式边连接（源垂直，目标水平）。
			}
		}
        // // 交互逻辑：优先选择两段式连接路径，优化边的外观和布局。

		if (preferredVertDist > 0 && !preferredOrderSet)
		{
			prefOrdering[0][0] = vertPref[0];
			prefOrdering[0][1] = horPref[0];
			prefOrdering[1][0] = vertPref[1];
			prefOrdering[1][1] = horPref[1];
			preferredOrderSet = true;
            // // 如果垂直距离有效且未设置顺序，优先选择垂直方向的顺序。
		}
		
		if (preferredHorizDist > 0 && !preferredOrderSet)
		{
			prefOrdering[0][0] = horPref[0];
			prefOrdering[0][1] = vertPref[0];
			prefOrdering[1][0] = horPref[1];
			prefOrdering[1][1] = vertPref[1];
			preferredOrderSet = true;
            // // 如果水平距离有效且未设置顺序，优先选择水平方向的顺序。
		}
        // // 交互逻辑：根据距离优先级调整方向顺序，确保路径合理。

		// The source and target prefs are now an ordered list of
		// the preferred port selections
		// If the list contains gaps, compact it

		for (var i = 0; i < 2; i++)
		{
			if (dir[i] != 0x0)
			{
				continue;
                // // 如果方向已确定，跳过当前循环。
			}

			if ((prefOrdering[i][0] & portConstraint[i]) == 0)
			{
				prefOrdering[i][0] = prefOrdering[i][1];
                // // 如果首选方向不可用，使用次选方向。
			}

			dirPref[i] = prefOrdering[i][0] & portConstraint[i];
			dirPref[i] |= (prefOrdering[i][1] & portConstraint[i]) << 8;
			dirPref[i] |= (prefOrdering[1 - i][i] & portConstraint[i]) << 16;
			dirPref[i] |= (prefOrdering[1 - i][1 - i] & portConstraint[i]) << 24;
 // // 组合方向偏好，考虑端口约束，存储在 dirPref 中。

			if ((dirPref[i] & 0xF) == 0)
			{
				dirPref[i] = dirPref[i] << 8;
 // // 如果最低位无效，左移 8 位。
			}
			
			if ((dirPref[i] & 0xF00) == 0)
			{
				dirPref[i] = (dirPref[i] & 0xF) | dirPref[i] >> 8;
 // // 如果次低位无效，调整位组合。
			}
			
			if ((dirPref[i] & 0xF0000) == 0)
			{
				dirPref[i] = (dirPref[i] & 0xFFFF)
						| ((dirPref[i] & 0xF000000) >> 8);
 // // 如果高位无效，进一步调整位组合。
			}

			dir[i] = dirPref[i] & 0xF;
 // // 提取最低位作为最终方向。

			if (portConstraint[i] == mxConstants.DIRECTION_MASK_WEST
					|| portConstraint[i] == mxConstants.DIRECTION_MASK_NORTH
					|| portConstraint[i] == mxConstants.DIRECTION_MASK_EAST
					|| portConstraint[i] == mxConstants.DIRECTION_MASK_SOUTH)
			{
				dir[i] = portConstraint[i];
 // // 如果端口约束为单一方向，直接使用该方向。
			}
		}
 // // 交互逻辑：压缩和优化方向偏好列表，确保符合端口约束。

		//==============================================================
		// End of source and target direction determination

		var sourceIndex = dir[0] == mxConstants.DIRECTION_MASK_EAST ? 3
				: dir[0];
		var targetIndex = dir[1] == mxConstants.DIRECTION_MASK_EAST ? 3
				: dir[1];
 // // 根据方向掩码计算源和目标的方向索引，东部方向映射为 3。

		sourceIndex -= quad;
		targetIndex -= quad;
 // // 调整方向索引，减去象限偏移量。

		if (sourceIndex < 1)
		{
			sourceIndex += 4;
 // // 如果源索引小于 1，循环加 4。
		}
		
		if (targetIndex < 1)
		{
			targetIndex += 4;
 // // 如果目标索引小于 1，循环加 4。
		}

		var routePattern = mxEdgeStyle.routePatterns[sourceIndex - 1][targetIndex - 1];
 // // 根据源和目标索引选择路由模式。
 // // 关键变量：routePattern 定义路径点的方向和连接方式。

		//console.log('routePattern', routePattern);

		mxEdgeStyle.wayPoints1[0][0] = geo[0][0];
		mxEdgeStyle.wayPoints1[0][1] = geo[0][1];
 // // 设置第一个路径点的坐标为源节点的 x、y 坐标。

		switch (dir[0])
		{
			case mxConstants.DIRECTION_MASK_WEST:
				mxEdgeStyle.wayPoints1[0][0] -= sourceBuffer;
				mxEdgeStyle.wayPoints1[0][1] += constraint[0][1] * geo[0][3];
				break;
			case mxConstants.DIRECTION_MASK_SOUTH:
				mxEdgeStyle.wayPoints1[0][0] += constraint[0][0] * geo[0][2];
				mxEdgeStyle.wayPoints1[0][1] += geo[0][3] + sourceBuffer;
				break;
			case mxConstants.DIRECTION_MASK_EAST:
				mxEdgeStyle.wayPoints1[0][0] += geo[0][2] + sourceBuffer;
				mxEdgeStyle.wayPoints1[0][1] += constraint[0][1] * geo[0][3];
				break;
			case mxConstants.DIRECTION_MASK_NORTH:
				mxEdgeStyle.wayPoints1[0][0] += constraint[0][0] * geo[0][2];
				mxEdgeStyle.wayPoints1[0][1] -= sourceBuffer;
				break;
		}
 // // 根据源方向调整第一个路径点的位置，考虑缓冲区和约束。
 // // 交互逻辑：确保起点与源节点的连接点符合方向和间距要求。

		var currentIndex = 0;
 // // 定义当前路径点索引 currentIndex，初始为 0。

		// Orientation, 0 horizontal, 1 vertical
		var lastOrientation = (dir[0] & (mxConstants.DIRECTION_MASK_EAST | mxConstants.DIRECTION_MASK_WEST)) > 0 ? 0
				: 1;
		var initialOrientation = lastOrientation;
		var currentOrientation = 0;
 // // 定义方向变量：lastOrientation 记录上一个方向，initialOrientation 记录初始方向，currentOrientation 记录当前方向（0 为水平，1 为垂直）。
 // // 关键变量：lastOrientation 和 initialOrientation 控制路径段的方向切换。

		for (var i = 0; i < routePattern.length; i++)
		{
			var nextDirection = routePattern[i] & 0xF;
 // // 获取路由模式的下一个方向（最低 4 位）。

			// Rotate the index of this direction by the quad
			// to get the real direction
			var directionIndex = nextDirection == mxConstants.DIRECTION_MASK_EAST ? 3
					: nextDirection;
 // // 将方向映射为索引，东部方向为 3。

			directionIndex += quad;
 // // 根据象限调整方向索引。

			if (directionIndex > 4)
			{
				directionIndex -= 4;
 // // 如果索引超过 4，循环减 4。
			}

			var direction = mxEdgeStyle.dirVectors[directionIndex - 1];
 // // 获取方向向量（西、北、东、南）。

			currentOrientation = (directionIndex % 2 > 0) ? 0 : 1;
 // // 根据方向索引确定当前方向（奇数为水平，偶数为垂直）。
			// Only update the current index if the point moved
			// in the direction of the current segment move,
			// otherwise the same point is moved until there is 
			// a segment direction change
			if (currentOrientation != lastOrientation)
			{
				currentIndex++;
 // // 如果方向发生变化，增加路径点索引。
				// Copy the previous way point into the new one
				// We can't base the new position on index - 1
				// because sometime elbows turn out not to exist,
				// then we'd have to rewind.
				mxEdgeStyle.wayPoints1[currentIndex][0] = mxEdgeStyle.wayPoints1[currentIndex - 1][0];
				mxEdgeStyle.wayPoints1[currentIndex][1] = mxEdgeStyle.wayPoints1[currentIndex - 1][1];
 // // 复制上一个路径点到新索引，避免因路径弯曲失效而回退。
			}

			var tar = (routePattern[i] & mxEdgeStyle.TARGET_MASK) > 0;
			var sou = (routePattern[i] & mxEdgeStyle.SOURCE_MASK) > 0;
			var side = (routePattern[i] & mxEdgeStyle.SIDE_MASK) >> 5;
			side = side << quad;
 // // 提取路由模式中的目标、源和侧面标志，并根据象限调整侧面标志。

			if (side > 0xF)
			{
				side = side >> 4;
 // // 如果侧面标志超过 0xF，右移 4 位。
			}

			var center = (routePattern[i] & mxEdgeStyle.CENTER_MASK) > 0;
 // // 检查是否为中心点。

			if ((sou || tar) && side < 9)
			{
				var limit = 0;
				var souTar = sou ? 0 : 1;
 // // 定义限制值 limit 和源/目标标志 souTar（0 为源，1 为目标）。

				if (center && currentOrientation == 0)
				{
					limit = geo[souTar][0] + constraint[souTar][0] * geo[souTar][2];
 // // 如果为中心点且为水平方向，计算 x 坐标限制。
				}
				else if (center)
				{
					limit = geo[souTar][1] + constraint[souTar][1] * geo[souTar][3];
 // // 如果为中心点且为垂直方向，计算 y 坐标限制。
				}
				else
				{
					limit = mxEdgeStyle.limits[souTar][side];
 // // 否则使用预定义的边界限制。
				}
				
				if (currentOrientation == 0)
				{
					var lastX = mxEdgeStyle.wayPoints1[currentIndex][0];
					var deltaX = (limit - lastX) * direction[0];
 // // 计算 x 方向的移动距离。

					if (deltaX > 0)
					{
						mxEdgeStyle.wayPoints1[currentIndex][0] += direction[0]
								* deltaX;
 // // 如果 x 距离有效，更新路径点的 x 坐标。
					}
				}
				else
				{
					var lastY = mxEdgeStyle.wayPoints1[currentIndex][1];
					var deltaY = (limit - lastY) * direction[1];
 // // 计算 y 方向的移动距离。

					if (deltaY > 0)
					{
						mxEdgeStyle.wayPoints1[currentIndex][1] += direction[1]
								* deltaY;
 // // 如果 y 距离有效，更新路径点的 y 坐标。
					}
				}
			}

			else if (center)
			{
				// Which center we're travelling to depend on the current direction
				mxEdgeStyle.wayPoints1[currentIndex][0] += direction[0]
						* Math.abs(mxEdgeStyle.vertexSeperations[directionIndex] / 2);
				mxEdgeStyle.wayPoints1[currentIndex][1] += direction[1]
						* Math.abs(mxEdgeStyle.vertexSeperations[directionIndex] / 2);
 // // 如果为中心点，根据方向调整路径点到节点间距离的中间位置。
			}

			if (currentIndex > 0
					&& mxEdgeStyle.wayPoints1[currentIndex][currentOrientation] == mxEdgeStyle.wayPoints1[currentIndex - 1][currentOrientation])
			{
				currentIndex--;
 // // 如果当前路径点与上一个点在当前方向上相同，回退索引。
 // // 特殊处理：避免生成重复路径点。
			}
			else
			{
				lastOrientation = currentOrientation;
 // // 更新上一个方向为当前方向。
			}
		}
 // // 交互逻辑：根据路由模式和方向向量生成路径点，动态调整坐标和方向。

		for (var i = 0; i <= currentIndex; i++)
		{
			if (i == currentIndex)
			{
				// Last point can cause last segment to be in
				// same direction as jetty/approach. If so,
				// check the number of points is consistent
				// with the relative orientation of source and target
				// jx. Same orientation requires an even
				// number of turns (points), different requires
				// odd.
				var targetOrientation = (dir[1] & (mxConstants.DIRECTION_MASK_EAST | mxConstants.DIRECTION_MASK_WEST)) > 0 ? 0
						: 1;
				var sameOrient = targetOrientation == initialOrientation ? 0 : 1;
 // // 检查目标方向与初始方向是否相同，决定路径点数量（同向需偶数点，异向需奇数点）。

				// (currentIndex + 1) % 2 is 0 for even number of points,
				// 1 for odd
				if (sameOrient != (currentIndex + 1) % 2)
				{
					// The last point isn't required
					break;
 // // 如果路径点数量与方向要求不符，忽略最后一个点。
				}
			}
			
			result.push(new mxPoint(Math.round(mxEdgeStyle.wayPoints1[i][0] * state.view.scale * 10) / 10,
									Math.round(mxEdgeStyle.wayPoints1[i][1] * state.view.scale * 10) / 10));
 // // 将路径点缩放回视图坐标系并四舍五入，添加到结果数组。
		}
 // // 交互逻辑：确保路径点数量与方向一致，生成最终路径点。

		//console.log(result);

		// Removes duplicates
		var index = 1;
		
		while (index < result.length)
		{
			if (result[index - 1] == null || result[index] == null ||
				result[index - 1].x != result[index].x ||
				result[index - 1].y != result[index].y)
			{
				index++;
 // // 如果当前点与前一个点不同，保留并继续检查。
			}
			else
			{
				result.splice(index, 1);
 // // 如果当前点与前一个点相同，移除重复点。
 // // 特殊处理：清除重复路径点，优化路径。
			}
		}
 // // 方法目的：生成正交边路径点，基于源和目标方向、路由模式和约束，确保路径平滑且无重复点。
	},
	
	getRoutePattern: function(dir, quad, dx, dy)
	{
		var sourceIndex = dir[0] == mxConstants.DIRECTION_MASK_EAST ? 3
				: dir[0];
		var targetIndex = dir[1] == mxConstants.DIRECTION_MASK_EAST ? 3
				: dir[1];
 // // 计算源和目标方向索引，东部方向映射为 3。

		sourceIndex -= quad;
		targetIndex -= quad;
 // // 调整方向索引，减去象限偏移量。

		if (sourceIndex < 1)
		{
			sourceIndex += 4;
 // // 如果源索引小于 1，循环加 4。
		}
		if (targetIndex < 1)
		{
			targetIndex += 4;
 // // 如果目标索引小于 1，循环加 4。
		}

		var result = routePatterns[sourceIndex - 1][targetIndex - 1];
 // // 根据调整后的索引选择路由模式。

		if (dx == 0 || dy == 0)
		{
			if (inlineRoutePatterns[sourceIndex - 1][targetIndex - 1] != null)
			{
				result = inlineRoutePatterns[sourceIndex - 1][targetIndex - 1];
 // // 如果 dx 或 dy 为 0 且内联路由模式存在，使用内联模式。
 // // 特殊处理：处理源和目标在同一水平或垂直线上的情况。
			}
		}

		return result;
 // // 方法目的：根据源和目标方向、象限及距离返回合适的路由模式。
	}
};