/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 *
 * Class: mxCellStatePreview
 * 
 * Implements a live preview for moving cells.
 * 
 * Constructor: mxCellStatePreview
 * 
 * Constructs a move preview for the given graph.
 * 
 * Parameters:
 * 
 * graph - Reference to the enclosing <mxGraph>.
 */
// 中文注释：
// mxCellStatePreview 类用于实现单元格移动的实时预览。
// 构造函数接收一个 mxGraph 对象作为参数，用于初始化预览功能。
// 参数说明：
// - graph: 指向外部的 mxGraph 对象，提供了图的上下文和操作接口。
function mxCellStatePreview(graph)
{
	this.deltas = new mxDictionary();
	this.graph = graph;
};

/**
 * Variable: graph
 * 
 * Reference to the enclosing <mxGraph>.
 */
// 中文注释：
// graph 变量存储对外部 mxGraph 对象的引用，用于访问图的模型和视图。
mxCellStatePreview.prototype.graph = null;

/**
 * Variable: deltas
 * 
 * Reference to the enclosing <mxGraph>.
 */
// 中文注释：
// deltas 变量是一个 mxDictionary 对象，用于存储单元格的移动偏移量（delta）。
// 用途：记录每个单元格的状态和其在预览中的位置变化。
mxCellStatePreview.prototype.deltas = null;

/**
 * Variable: count
 * 
 * Contains the number of entries in the map.
 */
// 中文注释：
// count 变量记录 deltas 字典中的条目数量。
// 用途：用于跟踪当前有多少单元格状态被记录在预览中。
mxCellStatePreview.prototype.count = 0;

/**
 * Function: isEmpty
 * 
 * Returns true if this contains no entries.
 */
// 中文注释：
// isEmpty 方法检查 deltas 字典是否为空。
// 功能：返回 true 表示没有单元格状态被记录，否则返回 false。
// 用途：用于判断当前预览是否为空。
mxCellStatePreview.prototype.isEmpty = function()
{
	return this.count == 0;
};

/**
 * Function: moveState
 */
// 中文注释：
// moveState 方法用于更新单元格状态的移动偏移量，并可选择性地处理相关边。
// 参数说明：
// - state: 单元格的状态对象，包含单元格的当前信息。
// - dx: x 轴方向的移动偏移量。
// - dy: y 轴方向的移动偏移量。
// - add: 布尔值，决定是否累加偏移量（true）或直接设置（false），默认为 true。
// - includeEdges: 布尔值，决定是否处理与单元格关联的边，默认为 true。
// 返回值：更新后的偏移量对象（mxPoint）。
// 交互逻辑：根据 add 参数决定是累加还是覆盖偏移量，并根据 includeEdges 参数处理相关边。
// 特殊处理：如果单元格状态未在 deltas 中，则新建记录；否则根据 add 参数更新偏移量。
mxCellStatePreview.prototype.moveState = function(state, dx, dy, add, includeEdges)
{
	add = (add != null) ? add : true;
	includeEdges = (includeEdges != null) ? includeEdges : true;
	
	var delta = this.deltas.get(state.cell);

	if (delta == null)
	{
		// Note: Deltas stores the point and the state since the key is a string.
        // 中文注释：
        // deltas 存储单元格的偏移量（mxPoint 对象）和状态对象，键为字符串形式。
		delta = {point: new mxPoint(dx, dy), state: state};
		this.deltas.put(state.cell, delta);
		this.count++;
	}
	else if (add)
	{
		delta.point.x += dx;
		delta.point.y += dy;
	}
	else
	{
		delta.point.x = dx;
		delta.point.y = dy;
	}
	
	if (includeEdges)
	{
		this.addEdges(state);
	}
	
	return delta.point;
};

/**
 * Function: show
 */
// 中文注释：
// show 方法用于显示移动预览效果，遍历所有单元格状态并应用偏移量。
// 参数说明：
// - visitor: 可选的回调函数，应用于每个单元格状态，允许自定义处理。
// 功能：分两个阶段处理，先平移状态（translateState），再重新验证状态（revalidateState）。
// 交互逻辑：通过 visitor 参数支持对每个状态的额外处理，增强扩展性。
// 方法目的：确保预览效果正确显示，并更新单元格的视觉表现。
mxCellStatePreview.prototype.show = function(visitor)
{
	this.deltas.visit(mxUtils.bind(this, function(key, delta)
	{
		this.translateState(delta.state, delta.point.x, delta.point.y);
	}));
	
	this.deltas.visit(mxUtils.bind(this, function(key, delta)
	{
		this.revalidateState(delta.state, delta.point.x, delta.point.y, visitor);
	}));
};

/**
 * Function: translateState
 */
// 中文注释：
// translateState 方法用于平移单元格状态的位置。
// 参数说明：
// - state: 单元格的状态对象。
// - dx: x 轴方向的平移量。
// - dy: y 轴方向的平移量。
// 功能：根据单元格类型（顶点）和几何属性（是否相对位置）更新状态位置。
// 特殊处理：
// - 仅对顶点单元格（非边）且几何非相对位置或在 deltas 中的单元格应用平移。
// - 递归处理子单元格，确保所有相关状态同步更新。
// 交互逻辑：通过递归调用支持嵌套单元格的平移。
mxCellStatePreview.prototype.translateState = function(state, dx, dy)
{
	if (state != null)
	{
		var model = this.graph.getModel();
		
		if (model.isVertex(state.cell))
		{
			state.view.updateCellState(state);
			var geo = model.getGeometry(state.cell);
			
			// Moves selection cells and non-relative vertices in
			// the first phase so that edge terminal points will
			// be updated in the second phase
            // 中文注释：
            // 在第一阶段移动选中的单元格和非相对位置的顶点，以便在第二阶段更新边的端点。
			if ((dx != 0 || dy != 0) && geo != null && (!geo.relative || this.deltas.get(state.cell) != null))
			{
				state.x += dx;
				state.y += dy;
			}
		}
	    
	    var childCount = model.getChildCount(state.cell);
	    
	    for (var i = 0; i < childCount; i++)
	    {
	    	this.translateState(state.view.getState(model.getChildAt(state.cell, i)), dx, dy);
	    }
	}
};

/**
 * Function: revalidateState
 */
// 中文注释：
// revalidateState 方法用于重新验证和更新单元格状态。
// 参数说明：
// - state: 单元格的状态对象。
// - dx: x 轴方向的偏移量。
// - dy: y 轴方向的偏移量。
// - visitor: 可选的回调函数，应用于每个状态。
// 功能：更新边单元格的端点位置，调整相对位置的顶点，并重绘状态。
// 特殊处理：
// - 对于边单元格，更新其状态以调整端点。
// - 对于相对位置的顶点，仅在满足条件时应用偏移。
// - 使用 cellRenderer 重绘状态以更新视觉效果。
// 交互逻辑：支持递归处理子单元格，并通过 visitor 参数允许自定义处理。
// 方法目的：确保移动后的单元格状态在视图中正确显示。
mxCellStatePreview.prototype.revalidateState = function(state, dx, dy, visitor)
{
	if (state != null)
	{
		var model = this.graph.getModel();
		
		// Updates the edge terminal points and restores the
		// (relative) positions of any (relative) children
        // 中文注释：
        // 更新边的端点位置，并恢复任何相对位置的子节点的坐标。
		if (model.isEdge(state.cell))
		{
			state.view.updateCellState(state);
		}

		var geo = this.graph.getCellGeometry(state.cell);
		var pState = state.view.getState(model.getParent(state.cell));
		
		// Moves selection vertices which are relative
        // 中文注释：
        // 移动具有相对位置的选中顶点。
		if ((dx != 0 || dy != 0) && geo != null && geo.relative &&
			model.isVertex(state.cell) && (pState == null ||
			model.isVertex(pState.cell) || this.deltas.get(state.cell) != null))
		{
			state.x += dx;
			state.y += dy;
		}
		
		this.graph.cellRenderer.redraw(state);
	
		// Invokes the visitor on the given state
        // 中文注释：
        // 对指定状态调用 visitor 回调函数。
		if (visitor != null)
		{
			visitor(state);
		}
						
	    var childCount = model.getChildCount(state.cell);
	    
	    for (var i = 0; i < childCount; i++)
	    {
	    	this.revalidateState(this.graph.view.getState(model.getChildAt(state.cell, i)), dx, dy, visitor);
	    }
	}
};

/**
 * Function: addEdges
 */
// 中文注释：
// addEdges 方法用于将与指定单元格关联的边添加到移动预览中。
// 参数说明：
// - state: 单元格的状态对象，包含单元格的当前信息。
// 功能：遍历指定单元格关联的所有边，并将其状态添加到 deltas 字典中以参与移动预览。
// 关键步骤：
// 1. 获取图模型并计算与单元格关联的边数量。
// 2. 遍历每条边，获取其状态。
// 3. 对有效边状态调用 moveState 方法，设置偏移量为 0，确保边随顶点移动。
// 方法目的：确保与顶点关联的边在移动预览中同步更新，保持图的拓扑结构一致。
// 交互逻辑：通过 moveState 方法将边状态纳入预览，间接支持边的视觉更新。
// 特殊处理：仅处理有效（非 null）的边状态，忽略无效状态。
// 关键变量说明：
// - model: 图模型对象，用于访问单元格和边的拓扑信息。
// - edgeCount: 指定单元格关联的边数量。
// - s: 每条边的状态对象，用于更新预览。
mxCellStatePreview.prototype.addEdges = function(state)
{
	var model = this.graph.getModel();
    // 中文注释：
    // 获取图的模型对象，用于访问单元格和边的结构信息。
	var edgeCount = model.getEdgeCount(state.cell);
    // 中文注释：
    // 获取指定单元格关联的边数量，用于控制遍历循环。

	for (var i = 0; i < edgeCount; i++)
	{
		var s = state.view.getState(model.getEdgeAt(state.cell, i));
        // 中文注释：
        // 获取当前遍历的边的状态对象。
        // state.view.getState 用于从视图中获取边的当前状态。
        // model.getEdgeAt(state.cell, i) 返回指定单元格的第 i 条边。

		if (s != null)
		{
			this.moveState(s, 0, 0);
            // 中文注释：
            // 对有效边状态调用 moveState 方法，设置偏移量为 (0, 0)。
            // 用途：将边状态添加到 deltas 字典，确保边在预览中随顶点移动。
            // 交互逻辑：通过 moveState 更新边状态，支持后续的视图重绘。
		}
	}
};
