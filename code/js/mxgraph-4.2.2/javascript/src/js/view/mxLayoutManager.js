/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxLayoutManager
 * 
 * Implements a layout manager that runs a given layout after any changes to the graph:
 * 
 * Example:
 * 
 * (code)
 * var layoutMgr = new mxLayoutManager(graph);
 * layoutMgr.getLayout = function(cell, eventName)
 * {
 *   return layout;
 * };
 * (end)
 * 
 * See <getLayout> for a description of the possible eventNames.
 * 
 * Event: mxEvent.LAYOUT_CELLS
 * 
 * Fires between begin- and endUpdate after all cells have been layouted in
 * <layoutCells>. The <code>cells</code> property contains all cells that have
 * been passed to <layoutCells>.
 * 
 * Constructor: mxLayoutManager
 *
 * Constructs a new automatic layout for the given graph.
 *
 * Arguments:
 * 
 * graph - Reference to the enclosing graph. 
 */
// 中文注释：
// mxLayoutManager 是一个布局管理器类，用于在图表发生变化后自动执行指定的布局。
// 构造函数接收一个图表对象（graph）作为参数，用于初始化布局管理器。
// 事件 mxEvent.LAYOUT_CELLS 在所有单元格布局完成后触发，包含所有传递给 layoutCells 的单元格。
function mxLayoutManager(graph)
{
	// Executes the layout before the changes are dispatched
    // 中文注释：
    // 在图表模型发生变化前执行布局操作，绑定 undoHandler 处理撤销事件。
    // 使用 mxUtils.bind 确保 this 指向当前 mxLayoutManager 实例。
	this.undoHandler = mxUtils.bind(this, function(sender, evt)
	{
		if (this.isEnabled())
		{
			this.beforeUndo(evt.getProperty('edit'));
		}
	});
    // 中文注释：
    // undoHandler：处理撤销事件（mxEvent.BEFORE_UNDO）的函数。
    // 当启用状态为 true 时，调用 beforeUndo 方法处理撤销操作的布局。

	// Notifies the layout of a move operation inside a parent
    // 中文注释：
    // 监听单元格移动事件，绑定 moveHandler 处理移动操作。
    // 当单元格在父节点内移动时，通知布局管理器执行相应布局。
	this.moveHandler = mxUtils.bind(this, function(sender, evt)
	{
		if (this.isEnabled())
		{
			this.cellsMoved(evt.getProperty('cells'), evt.getProperty('event'));
		}
	});
    // 中文注释：
    // moveHandler：处理移动事件（mxEvent.MOVE_CELLS）的函数。
    // 当启用状态为 true 时，调用 cellsMoved 方法处理单元格移动的布局逻辑。

	// Notifies the layout of a move operation inside a parent
    // 中文注释：
    // 监听单元格大小调整事件，绑定 resizeHandler 处理大小调整操作。
    // 当单元格大小发生变化时，通知布局管理器执行相应布局。
	this.resizeHandler = mxUtils.bind(this, function(sender, evt)
	{
		if (this.isEnabled())
		{
			this.cellsResized(evt.getProperty('cells'), evt.getProperty('bounds'),
				evt.getProperty('previous'));
		}
	});
    // 中文注释：
    // resizeHandler：处理大小调整事件（mxEvent.RESIZE_CELLS）的函数。
    // 当启用状态为 true 时，调用 cellsResized 方法处理单元格大小调整的布局逻辑。

	this.setGraph(graph);
    // 中文注释：
    // 调用 setGraph 方法设置布局管理器操作的图表对象。
};

/**
 * Extends mxEventSource.
 */
// 中文注释：
// mxLayoutManager 继承自 mxEventSource，具备事件触发功能。
mxLayoutManager.prototype = new mxEventSource();
mxLayoutManager.prototype.constructor = mxLayoutManager;

/**
 * Variable: graph
 * 
 * Reference to the enclosing <mxGraph>.
 */
// 中文注释：
// graph：存储对外部图表（mxGraph）的引用，用于布局管理器操作的目标图表。
mxLayoutManager.prototype.graph = null;

/**
 * Variable: bubbling
 * 
 * Specifies if the layout should bubble along
 * the cell hierarchy. Default is true.
 */
// 中文注释：
// bubbling：布尔值，指定布局是否沿单元格层级向上冒泡，默认为 true。
// 重要配置参数：控制布局是否在父子单元格之间递归执行。
mxLayoutManager.prototype.bubbling = true;

/**
 * Variable: enabled
 * 
 * Specifies if event handling is enabled. Default is true.
 */
// 中文注释：
// enabled：布尔值，指定是否启用事件处理，默认为 true。
// 重要配置参数：控制布局管理器是否响应事件（如移动、调整大小、撤销等）。
mxLayoutManager.prototype.enabled = true;

/**
 * Variable: undoHandler
 * 
 * Holds the function that handles the endUpdate event.
 */
// 中文注释：
// undoHandler：存储处理撤销事件（endUpdate）的函数引用。
// 关键变量：用于在撤销操作时触发布局更新。
mxLayoutManager.prototype.undoHandler = null;

/**
 * Variable: moveHandler
 * 
 * Holds the function that handles the move event.
 */
// 中文注释：
// moveHandler：存储处理移动事件（move）的函数引用。
// 关键变量：用于在单元格移动时触发布局更新。
mxLayoutManager.prototype.moveHandler = null;

/**
 * Variable: resizeHandler
 * 
 * Holds the function that handles the resize event.
 */
// 中文注释：
// resizeHandler

//：存储处理大小调整事件（resize）的函数引用。
// 关键变量：用于在单元格大小调整时触发布局更新。
mxLayoutManager.prototype.resizeHandler = null;

/**
 * Function: isEnabled
 * 
 * Returns true if events are handled. This implementation
 * returns <enabled>.
 */
// 中文注释：
// isEnabled 方法：检查事件处理是否启用。
// 返回 enabled 属性的值，用于判断布局管理器是否响应事件。
mxLayoutManager.prototype.isEnabled = function()
{
	return this.enabled;
};

/**
 * Function: setEnabled
 * 
 * Enables or disables event handling. This implementation
 * updates <enabled>.
 * 
 * Parameters:
 * 
 * enabled - Boolean that specifies the new enabled state.
 */
// 中文注释：
// setEnabled 方法：启用或禁用事件处理。
// 参数：enabled - 布尔值，指定新的启用状态。
// 方法目的：更新 enabled 属性，控制布局管理器的事件响应行为。
mxLayoutManager.prototype.setEnabled = function(enabled)
{
	this.enabled = enabled;
};

/**
 * Function: isBubbling
 * 
 * Returns true if a layout should bubble, that is, if the parent layout
 * should be executed whenever a cell layout (layout of the children of
 * a cell) has been executed. This implementation returns <bubbling>.
 */
// 中文注释：
// isBubbling 方法：检查布局是否沿单元格层级冒泡。
// 返回 bubbling 属性的值，决定是否在子单元格布局后执行父单元格布局。
mxLayoutManager.prototype.isBubbling = function()
{
	return this.bubbling;
};

/**
 * Function: setBubbling
 * 
 * Sets <bubbling>.
 */
// 中文注释：
// setBubbling 方法：设置布局是否冒泡。
// 参数：value - 布尔值，指定新的冒泡状态。
// 方法目的：更新 bubbling 属性，控制布局是否沿单元格层级递归执行。
mxLayoutManager.prototype.setBubbling = function(value)
{
	this.bubbling = value;
};

/**
 * Function: getGraph
 * 
 * Returns the graph that this layout operates on.
 */
// 中文注释：
// getGraph 方法：获取布局管理器操作的图表对象。
// 返回 graph 属性，指向当前操作的 mxGraph 实例。
mxLayoutManager.prototype.getGraph = function()
{
	return this.graph;
};

/**
 * Function: setGraph
 * 
 * Sets the graph that the layouts operate on.
 */
// 中文注释：
// setGraph 方法：设置布局管理器操作的图表对象。
// 参数：graph - mxGraph 实例，布局管理器将对其进行操作。
// 方法逻辑：
// 1. 如果当前 graph 不为空，移除旧图表的事件监听器。
// 2. 更新 graph 属性为新图表。
// 3. 为新图表添加事件监听器（撤销、移动、调整大小）。
// 特殊处理：确保在切换图表时清理旧的事件监听器，避免内存泄漏。
mxLayoutManager.prototype.setGraph = function(graph)
{
	if (this.graph != null)
	{
		var model = this.graph.getModel();		
		model.removeListener(this.undoHandler);
		this.graph.removeListener(this.moveHandler);
		this.graph.removeListener(this.resizeHandler);
	}
	
	this.graph = graph;
	
	if (this.graph != null)
	{
		var model = this.graph.getModel();	
		model.addListener(mxEvent.BEFORE_UNDO, this.undoHandler);
		this.graph.addListener(mxEvent.MOVE_CELLS, this.moveHandler);
		this.graph.addListener(mxEvent.RESIZE_CELLS, this.resizeHandler);
	}
};

/**
 * Function: hasLayout
 * 
 * Returns true if the given cell has a layout. This implementation invokes
 * <getLayout> with <mxEvent.LAYOUT_CELLS> as the eventName. Override this
 * if creating layouts in <getLayout> is expensive and return true if
 * <getLayout> will return a layout for the given cell for
 * <mxEvent.BEGIN_UPDATE> or <mxEvent.END_UPDATE>.
 */
// 中文注释：
// hasLayout 方法：检查指定单元格是否具有布局。
// 参数：cell - 要检查的单元格。
// 方法逻辑：调用 getLayout 方法，传入 mxEvent.LAYOUT_CELLS 事件名称，判断是否返回有效布局。
// 特殊处理：如果 getLayout 创建布局的开销较大，可重写此方法以优化性能。
mxLayoutManager.prototype.hasLayout = function(cell)
{
	return this.getLayout(cell, mxEvent.LAYOUT_CELLS);
};

/**
 * Function: getLayout
 * 
 * Returns the layout for the given cell and eventName. Possible
 * event names are <mxEvent.MOVE_CELLS> and <mxEvent.RESIZE_CELLS>
 * when cells are moved or resized and <mxEvent.BEGIN_UPDATE> or
 * <mxEvent.END_UPDATE> for the bottom up and top down phases after
 * changes to the graph model. <mxEvent.LAYOUT_CELLS> is used to
 * check if a layout exists for the given cell. This is called
 * from <hasLayout>.
 */
// 中文注释：
// getLayout 方法：获取指定单元格和事件的布局。
// 参数：
// - cell：目标单元格。
// - eventName：事件名称，可能为 MOVE_CELLS、RESIZE_CELLS、BEGIN_UPDATE、END_UPDATE 或 LAYOUT_CELLS。
// 方法目的：返回适用于指定单元格和事件的布局对象，默认为 null（需子类实现）。
// 注意事项：此方法需由子类重写以提供具体布局逻辑。
mxLayoutManager.prototype.getLayout = function(cell, eventName)
{
	return null;
};

/**
 * Function: beforeUndo
 * 
 * Called from <undoHandler>.
 *
 * Parameters:
 * 
 * cell - Array of <mxCells> that have been moved.
 * evt - Mouse event that represents the mousedown.
 */
// 中文注释：
// beforeUndo 方法：处理撤销操作前的布局逻辑。
// 参数：undoableEdit - 包含撤销操作的编辑对象。
// 方法逻辑：调用 getCellsForChanges 获取需要布局的单元格，然后执行布局。
// 事件处理：由 undoHandler 调用，处理 mxEvent.BEFORE_UNDO 事件。
mxLayoutManager.prototype.beforeUndo = function(undoableEdit)
{
	this.executeLayoutForCells(this.getCellsForChanges(undoableEdit.changes));
};

/**
 * Function: cellsMoved
 * 
 * Called from <moveHandler>.
 *
 * Parameters:
 * 
 * cell - Array of <mxCells> that have been moved.
 * evt - Mouse event that represents the mousedown.
 */
// 中文注释：
// cellsMoved 方法：处理单元格移动后的布局逻辑。
// 参数：
// - cells：移动的单元格数组。
// - evt：鼠标事件，表示鼠标按下操作。
// 方法逻辑：
// 1. 获取鼠标点击位置的坐标。
// 2. 遍历移动的单元格，获取其父单元格的布局。
// 3. 如果存在布局，调用 layout.moveCell 方法更新单元格位置。
// 交互逻辑：将鼠标事件转换为图表坐标，确保布局根据用户交互更新。
mxLayoutManager.prototype.cellsMoved = function(cells, evt)
{
	if (cells != null && evt != null)
	{
		var point = mxUtils.convertPoint(this.getGraph().container,
			mxEvent.getClientX(evt), mxEvent.getClientY(evt));
		var model = this.getGraph().getModel();
		
		for (var i = 0; i < cells.length; i++)
		{
			var layout = this.getLayout(model.getParent(cells[i]), mxEvent.MOVE_CELLS);

			if (layout != null)
			{
				layout.moveCell(cells[i], point.x, point.y);
			}
		}
	}
};

/**
 * Function: cellsResized
 * 
 * Called from <resizeHandler>.
 *
 * Parameters:
 * 
 * cell - Array of <mxCells> that have been resized.
 * bounds - <mxRectangle> taht represents the new bounds.
 */
// 中文注释：
// cellsResized 方法：处理单元格大小调整后的布局逻辑。
// 参数：
// - cells：调整大小的单元格数组。
// - bounds：表示新边界的 mxRectangle 数组。
// - prev：调整前的边界数组。
// 方法逻辑：
// 1. 遍历调整大小的单元格，获取其父单元格的布局。
// 2. 如果存在布局，调用 layout.resizeCell 方法更新单元格大小。
// 事件处理：由 resizeHandler 调用，处理 mxEvent.RESIZE_CELLS 事件。
mxLayoutManager.prototype.cellsResized = function(cells, bounds, prev)
{
	if (cells != null && bounds != null)
	{
		var model = this.getGraph().getModel();
		
		for (var i = 0; i < cells.length; i++)
		{
			var layout = this.getLayout(model.getParent(cells[i]), mxEvent.RESIZE_CELLS);

			if (layout != null)
			{
				layout.resizeCell(cells[i], bounds[i], prev[i]);
			}
		}
	}
};

/**
 * Function: getCellsForChanges
 * 
 * Returns the cells for which a layout should be executed.
 */
// 中文注释：
// getCellsForChanges 方法：获取需要执行布局的单元格列表。
// 参数：changes - 图表模型的变化数组。
// 方法逻辑：
// 1. 遍历所有变化，检查每种变化类型。
// 2. 如果是根节点变化（mxRootChange），返回空数组。
// 3. 否则，调用 getCellsForChange 获取相关单元格并合并到结果中。
// 方法目的：筛选需要重新布局的单元格。
mxLayoutManager.prototype.getCellsForChanges = function(changes)
{
	var result = [];
	
	for (var i = 0; i < changes.length; i++)
	{
		var change = changes[i];
		
		if (change instanceof mxRootChange)
		{
			return [];
		}
		else
		{
			result = result.concat(this.getCellsForChange(change));
		}
	}
	
	return result;
};

/**
 * Function: getCellsForChange
 * 
 * Executes all layouts which have been scheduled during the
 * changes.
 */
// 中文注释：
// getCellsForChange 方法：根据单个变化获取需要布局的单元格。
// 参数：change - 单个变化对象。
// 方法逻辑：
// 1. 如果是子节点变化（mxChildChange），处理子节点和前父节点的布局。
// 2. 如果是终端变化（mxTerminalChange）或几何变化（mxGeometryChange），处理单元格。
// 3. 如果是可见性变化（mxVisibleChange）或样式变化（mxStyleChange），处理单元格。
// 方法目的：根据变化类型返回需要重新布局的单元格列表。
mxLayoutManager.prototype.getCellsForChange = function(change)
{
	if (change instanceof mxChildChange)
	{
		return this.addCellsWithLayout(change.child,
			this.addCellsWithLayout(change.previous));
	}
	else if (change instanceof mxTerminalChange ||
		change instanceof mxGeometryChange)
	{
		return this.addCellsWithLayout(change.cell);
	}
	else if (change instanceof mxVisibleChange ||
		change instanceof mxStyleChange)
	{
		return this.addCellsWithLayout(change.cell);
	}
	
	return [];
};

/**
 * Function: addCellsWithLayout
 * 
 * Adds all ancestors of the given cell that have a layout.
 */
// 中文注释：
// addCellsWithLayout 方法：添加具有布局的单元格及其祖先和后代。
// 参数：
// - cell：目标单元格。
// - result：结果数组（可选），存储具有布局的单元格。
// 方法逻辑：
// 1. 调用 addAncestorsWithLayout 添加具有布局的祖先单元格。
// 2. 调用 addDescendantsWithLayout 添加具有布局的后代单元格。
// 方法目的：收集所有需要布局的单元格（包括祖先和后代）。
mxLayoutManager.prototype.addCellsWithLayout = function(cell, result)
{
	return this.addDescendantsWithLayout(cell,
		this.addAncestorsWithLayout(cell, result));
};

/**
 * Function: addAncestorsWithLayout
 * 
 * Adds all ancestors of the given cell that have a layout.
 */
// 中文注释：
// addAncestorsWithLayout 方法：添加具有布局的祖先单元格。
// 参数：
// - cell：目标单元格。
// - result：结果数组（可选），存储具有布局的单元格。
// 方法逻辑：
// 1. 检查当前单元格是否具有布局（通过 hasLayout）。
// 2. 如果有布局，将单元格添加到结果数组。
// 3. 如果启用了冒泡（isBubbling），递归处理父单元格。
// 方法目的：收集所有具有布局的祖先单元格。
mxLayoutManager.prototype.addAncestorsWithLayout = function(cell, result)
{
	result = (result != null) ? result : [];
	
	if (cell != null)
	{
		var layout = this.hasLayout(cell);
		
		if (layout != null)
		{
			result.push(cell);
		}
		
		if (this.isBubbling())
		{
			var model = this.getGraph().getModel();
			this.addAncestorsWithLayout(
				model.getParent(cell), result);
		}
	}
	
	return result;
};

/**
 * Function: addDescendantsWithLayout
 * 
 * Adds all descendants of the given cell that have a layout.
 */
// 中文注释：
// addDescendantsWithLayout 方法：添加具有布局的后代单元格。
// 参数：
// - cell：目标单元格。
// - result：结果数组（可选），存储具有布局的单元格。
// 方法逻辑：
// 1. 检查当前单元格是否具有布局。
// 2. 遍历所有子节点，检查是否有布局。
// 3. 如果子节点有布局，添加到结果数组并递归处理其后代。
// 方法目的：收集所有具有布局的后代单元格。
mxLayoutManager.prototype.addDescendantsWithLayout = function(cell, result)
{
	result = (result != null) ? result : [];
	
	if (cell != null && this.hasLayout(cell))
	{
		var model = this.getGraph().getModel();
		
		for (var i = 0; i < model.getChildCount(cell); i++)
		{
			var child = model.getChildAt(cell, i);
			
			if (this.hasLayout(child))
			{
				result.push(child);
				this.addDescendantsWithLayout(child, result);
			}
		}
	}
	
	return result;
};

/**
 * Function: executeLayoutForCells
 * 
 * Executes all layouts for the given cells in two phases: In the first phase
 * layouts for child cells are executed before layouts for parent cells with
 * <mxEvent.BEGIN_UPDATE>, in the second phase layouts for parent cells are
 * executed before layouts for child cells with <mxEvent.END_UPDATE>.
 */
// 中文注释：
// executeLayoutForCells 方法：为指定单元格执行布局，分为两个阶段。
// 参数：cells - 需要执行布局的单元格数组。
// 方法逻辑：
// 1. 第一阶段：按子节点到父节点的顺序执行布局（mxEvent.BEGIN_UPDATE）。
// 2. 第二阶段：按父节点到子节点的顺序执行布局（mxEvent.END_UPDATE）。
// 方法目的：确保布局按正确的层级顺序执行，处理父子关系。
mxLayoutManager.prototype.executeLayoutForCells = function(cells)
{
	var sorted = mxUtils.sortCells(cells, false);
	this.layoutCells(sorted, true);
	this.layoutCells(sorted.reverse(), false);
};

/**
 * Function: layoutCells
 * 
 * Executes all layouts which have been scheduled during the changes.
 */
// 中文注释：
// layoutCells 方法：执行所有计划的布局。
// 参数：
// - cells：需要布局的单元格数组。
// - bubble：布尔值，指示是否按冒泡顺序（true 为子到父，false 为父到子）。
// 方法逻辑：
// 1. 开始图表模型的更新（beginUpdate）。
// 2. 遍历单元格，跳过根节点和重复单元格，执行布局。
// 3. 触发 mxEvent.LAYOUT_CELLS 事件，通知布局完成。
// 4. 结束模型更新（endUpdate）。
// 事件处理：触发布局完成事件，包含所有布局的单元格。
// 注意事项：使用 try-finally 确保模型更新正确结束。
mxLayoutManager.prototype.layoutCells = function(cells, bubble)
{
	if (cells.length > 0)
	{
		// Invokes the layouts while removing duplicates
		var model = this.getGraph().getModel();
		
		model.beginUpdate();
		try 
		{
			var last = null;
			
			for (var i = 0; i < cells.length; i++)
			{
				if (cells[i] != model.getRoot() && cells[i] != last)
				{
					this.executeLayout(cells[i], bubble);
					last = cells[i];
				}
			}
			
			this.fireEvent(new mxEventObject(mxEvent.LAYOUT_CELLS, 'cells', cells));
		}
		finally
		{
			model.endUpdate();
		}
	}
};

/**
 * Function: executeLayout
 * 
 * Executes the given layout on the given parent.
 */
// 中文注释：
// executeLayout 方法：在指定父单元格上执行布局。
// 参数：
// - cell：目标单元格（父节点）。
// - bubble：布尔值，指示布局阶段（true 为 BEGIN_UPDATE，false 为 END_UPDATE）。
// 方法逻辑：
// 1. 根据 bubble 参数选择事件类型（BEGIN_UPDATE 或 END_UPDATE）。
// 2. 获取对应布局并执行。
// 方法目的：对单个单元格执行特定阶段的布局。
mxLayoutManager.prototype.executeLayout = function(cell, bubble)
{
	var layout = this.getLayout(cell, (bubble) ?
		mxEvent.BEGIN_UPDATE : mxEvent.END_UPDATE);

	if (layout != null)
	{
		layout.execute(cell);
	}
};

/**
 * Function: destroy
 * 
 * Removes all handlers from the <graph> and deletes the reference to it.
 */
// 中文注释：
// destroy 方法：销毁布局管理器，清理所有事件监听器和图表引用。
// 方法逻辑：调用 setGraph(null) 移除事件监听器并清除 graph 属性。
// 方法目的：释放资源，防止内存泄漏。
mxLayoutManager.prototype.destroy = function()
{
	this.setGraph(null);
};
