/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxSwimlaneManager
 * 
 * Manager for swimlanes and nested swimlanes that sets the size of newly added
 * swimlanes to that of their siblings, and propagates changes to the size of a
 * swimlane to its siblings, if <siblings> is true, and its ancestors, if
 * <bubbling> is true.
 * 
 * Constructor: mxSwimlaneManager
 *
 * Constructs a new swimlane manager for the given graph.
 *
 * Arguments:
 * 
 * graph - Reference to the enclosing graph. 
 */
// 中文注释：
// mxSwimlaneManager 类用于管理泳道及其嵌套泳道，负责将新添加的泳道大小设置为与其兄弟泳道相同，
// 并在 <siblings> 为 true 时将泳道大小变化传播到兄弟泳道，在 <bubbling> 为 true 时传播到祖先泳道。
// 构造函数接收图对象 graph 和配置参数，初始化泳道管理器。
function mxSwimlaneManager(graph, horizontal, addEnabled, resizeEnabled)
{
    // 中文注释：设置泳道方向，默认为 true（水平方向）
	this.horizontal = (horizontal != null) ? horizontal : true;
    // 中文注释：设置是否启用新添加单元格的自动调整大小功能，默认为 true
	this.addEnabled = (addEnabled != null) ? addEnabled : true;
    // 中文注释：设置是否启用泳道大小调整功能，默认为 true
	this.resizeEnabled = (resizeEnabled != null) ? resizeEnabled : true;

    // 中文注释：绑定添加单元格的事件处理器，处理新添加的单元格
	this.addHandler = mxUtils.bind(this, function(sender, evt)
	{
        // 中文注释：当事件处理和添加功能启用时，调用 cellsAdded 处理新添加的单元格
		if (this.isEnabled() && this.isAddEnabled())
		{
			this.cellsAdded(evt.getProperty('cells'));
		}
	});
	
    // 中文注释：绑定大小调整的事件处理器，处理泳道大小变化
	this.resizeHandler = mxUtils.bind(this, function(sender, evt)
	{
        // 中文注释：当事件处理和大小调整功能启用时，调用 cellsResized 处理调整大小的单元格
		if (this.isEnabled() && this.isResizeEnabled())
		{
			this.cellsResized(evt.getProperty('cells'));
		}
	});
	
    // 中文注释：设置管理器操作的图对象，并绑定事件监听器
	this.setGraph(graph);
};

/**
 * Extends mxEventSource.
 */
// 中文注释：继承 mxEventSource，使 mxSwimlaneManager 具备事件发布功能
mxSwimlaneManager.prototype = new mxEventSource();
mxSwimlaneManager.prototype.constructor = mxSwimlaneManager;

/**
 * Variable: graph
 * 
 * Reference to the enclosing <mxGraph>.
 */
// 中文注释：保存对外部图对象的引用，用于管理泳道所在的图
mxSwimlaneManager.prototype.graph = null;

/**
 * Variable: enabled
 * 
 * Specifies if event handling is enabled. Default is true.
 */
// 中文注释：控制是否启用事件处理，默认值为 true
mxSwimlaneManager.prototype.enabled = true;

/**
 * Variable: horizontal
 * 
 * Specifies the orientation of the swimlanes. Default is true.
 */
// 中文注释：指定泳道的方向，true 表示水平，false 表示垂直，默认值为 true
mxSwimlaneManager.prototype.horizontal = true;

/**
 * Variable: addEnabled
 * 
 * Specifies if newly added cells should be resized to match the size of their
 * existing siblings. Default is true.
 */
// 中文注释：控制新添加的单元格是否调整大小以匹配兄弟泳道，默认值为 true
mxSwimlaneManager.prototype.addEnabled = true;

/**
 * Variable: resizeEnabled
 * 
 * Specifies if resizing of swimlanes should be handled. Default is true.
 */
// 中文注释：控制是否处理泳道的大小调整，默认值为 true
mxSwimlaneManager.prototype.resizeEnabled = true;

/**
 * Variable: moveHandler
 * 
 * Holds the function that handles the move event.
 */
// 中文注释：存储处理添加单元格事件的函数
mxSwimlaneManager.prototype.addHandler = null;

/**
 * Variable: moveHandler
 * 
 * Holds the function that handles the move event.
 */
// 中文注释：存储处理泳道大小调整事件的函数
mxSwimlaneManager.prototype.resizeHandler = null;

/**
 * Function: isEnabled
 * 
 * Returns true if events are handled. This implementation
 * returns <enabled>.
 */
// 中文注释：检查是否启用事件处理，返回 enabled 属性值
mxSwimlaneManager.prototype.isEnabled = function()
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
// 中文注释：设置事件处理状态
// 参数：enabled - 布尔值，指定新的事件处理状态
mxSwimlaneManager.prototype.setEnabled = function(value)
{
	this.enabled = value;
};

/**
 * Function: isHorizontal
 * 
 * Returns <horizontal>.
 */
// 中文注释：返回泳道的方向（水平或垂直）
mxSwimlaneManager.prototype.isHorizontal = function()
{
	return this.horizontal;
};

/**
 * Function: setHorizontal
 * 
 * Sets <horizontal>.
 */
// 中文注释：设置泳道的方向
// 参数：value - 布尔值，true 表示水平，false 表示垂直
mxSwimlaneManager.prototype.setHorizontal = function(value)
{
	this.horizontal = value;
};

/**
 * Function: isAddEnabled
 * 
 * Returns <addEnabled>.
 */
// 中文注释：检查是否启用新单元格的自动调整大小功能
mxSwimlaneManager.prototype.isAddEnabled = function()
{
	return this.addEnabled;
};

/**
 * Function: setAddEnabled
 * 
 * Sets <addEnabled>.
 */
// 中文注释：设置是否启用新单元格的自动调整大小
// 参数：value - 布尔值，指定是否启用
mxSwimlaneManager.prototype.setAddEnabled = function(value)
{
	this.addEnabled = value;
};

/**
 * Function: isResizeEnabled
 * 
 * Returns <resizeEnabled>.
 */
// 中文注释：检查是否启用泳道大小调整功能
mxSwimlaneManager.prototype.isResizeEnabled = function()
{
	return this.resizeEnabled;
};

/**
 * Function: setResizeEnabled
 * 
 * Sets <resizeEnabled>.
 */
// 中文注释：设置是否启用泳道大小调整
// 参数：value - 布尔值，指定是否启用
mxSwimlaneManager.prototype.setResizeEnabled = function(value)
{
	this.resizeEnabled = value;
};

/**
 * Function: getGraph
 * 
 * Returns the graph that this manager operates on.
 */
// 中文注释：返回管理器操作的图对象
mxSwimlaneManager.prototype.getGraph = function()
{
	return this.graph;
};

/**
 * Function: setGraph
 * 
 * Sets the graph that the manager operates on.
 */
// 中文注释：设置管理器操作的图对象，并绑定或解绑事件监听器
// 参数：graph - 要设置的图对象
// 注意事项：如果之前已设置图对象，会先移除旧的事件监听器
mxSwimlaneManager.prototype.setGraph = function(graph)
{
    // 中文注释：移除旧图对象的事件监听器
	if (this.graph != null)
	{
		this.graph.removeListener(this.addHandler);
		this.graph.removeListener(this.resizeHandler);
	}
	
    // 中文注释：更新图对象引用
	this.graph = graph;
	
    // 中文注释：为新图对象绑定添加单元格和大小调整的事件监听器
	if (this.graph != null)
	{
		this.graph.addListener(mxEvent.ADD_CELLS, this.addHandler);
		this.graph.addListener(mxEvent.CELLS_RESIZED, this.resizeHandler);
	}
};

/**
 * Function: isSwimlaneIgnored
 * 
 * Returns true if the given swimlane should be ignored.
 */
// 中文注释：检查指定泳道是否应被忽略
// 参数：swimlane - 要检查的泳道对象
// 返回：如果不是有效泳道则返回 true
mxSwimlaneManager.prototype.isSwimlaneIgnored = function(swimlane)
{
	return !this.getGraph().isSwimlane(swimlane);
};

/**
 * Function: isCellHorizontal
 * 
 * Returns true if the given cell is horizontal. If the given cell is not a
 * swimlane, then the global orientation is returned.
 */
// 中文注释：检查指定单元格是否为水平方向
// 参数：cell - 要检查的单元格
// 返回：如果单元格是泳道，返回其水平方向设置；否则返回全局方向的反向值
mxSwimlaneManager.prototype.isCellHorizontal = function(cell)
{
    // 中文注释：如果是泳道，获取其样式并检查水平方向设置
	if (this.graph.isSwimlane(cell))
	{
		var style = this.graph.getCellStyle(cell);
		
		return mxUtils.getValue(style, mxConstants.STYLE_HORIZONTAL, 1) == 1;
	}
	
    // 中文注释：如果不是泳道，返回全局方向的反向值
	return !this.isHorizontal();
};

/**
 * Function: cellsAdded
 * 
 * Called if any cells have been added.
 * 
 * Parameters:
 * 
 * cell - Array of <mxCells> that have been added.
 */
// 中文注释：处理新添加的单元格，调整其大小以匹配兄弟泳道
// 参数：cells - 新添加的单元格数组
// 事件处理逻辑：遍历每个单元格，如果不是忽略的泳道，则调用 swimlaneAdded 处理
mxSwimlaneManager.prototype.cellsAdded = function(cells)
{
	if (cells != null)
	{
		var model = this.getGraph().getModel();

        // 中文注释：开始更新模型，确保批量操作
		model.beginUpdate();
		try
		{
			for (var i = 0; i < cells.length; i++)
			{
                // 中文注释：检查单元格是否为有效泳道，若不是则忽略
				if (!this.isSwimlaneIgnored(cells[i]))
				{
                    // 中文注释：调用 swimlaneAdded 调整泳道大小
					this.swimlaneAdded(cells[i]);
				}
			}
		}
		finally
		{
            // 中文注释：结束模型更新，提交更改
			model.endUpdate();
		}
	}
};

/**
 * Function: swimlaneAdded
 * 
 * Updates the size of the given swimlane to match that of any existing
 * siblings swimlanes.
 * 
 * Parameters:
 * 
 * swimlane - <mxCell> that represents the new swimlane.
 */
// 中文注释：调整新添加的泳道大小，使其与现有兄弟泳道一致
// 参数：swimlane - 新添加的泳道单元格
// 逻辑说明：查找第一个有效兄弟泳道的几何信息，并应用到新泳道
mxSwimlaneManager.prototype.swimlaneAdded = function(swimlane)
{
	var model = this.getGraph().getModel();
	var parent = model.getParent(swimlane);
	var childCount = model.getChildCount(parent);
	var geo = null;
	
	// Finds the first valid sibling swimlane as reference
	// 中文注释：遍历父节点的子节点，查找第一个有效兄弟泳道的几何信息
	for (var i = 0; i < childCount; i++)
	{
		var child = model.getChildAt(parent, i);
		
		if (child != swimlane && !this.isSwimlaneIgnored(child))
		{
			geo = model.getGeometry(child);
			
			if (geo != null)
			{	
				break;
			}
		}
	}
	
    // 中文注释：如果找到有效几何信息，调整新泳道的大小
	if (geo != null)
	{
        // 中文注释：根据父节点的方向（如果存在）或全局方向决定调整方式
		var parentHorizontal = (parent != null) ? this.isCellHorizontal(parent) : this.horizontal;
        // 中文注释：调用 resizeSwimlane 调整泳道大小
		this.resizeSwimlane(swimlane, geo.width, geo.height, parentHorizontal);
	}
};

/**
 * Function: cellsResized
 * 
 * Called if any cells have been resizes. Calls <swimlaneResized> for all
 * swimlanes where <isSwimlaneIgnored> returns false.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> whose size was changed.
 */
// 中文注释：处理泳道大小调整事件，更新相关泳道及其父泳道的大小
// 参数：cells - 大小发生变化的单元格数组
// 事件处理逻辑：遍历每个单元格，计算其完整大小（包括父泳道的起始大小），并调整相关泳道
mxSwimlaneManager.prototype.cellsResized = function(cells)
{
	if (cells != null)
	{
		var model = this.getGraph().getModel();
		
        // 中文注释：开始更新模型，确保批量操作
		model.beginUpdate();
		try
		{
            // 中文注释：遍历每个调整大小的单元格
			for (var i = 0; i < cells.length; i++)
			{
                // 中文注释：仅处理有效泳道
				if (!this.isSwimlaneIgnored(cells[i]))
				{
					var geo = model.getGeometry(cells[i]);

					if (geo != null)
					{
                        // 中文注释：记录当前泳道的几何信息
						var size = new mxRectangle(0, 0, geo.width, geo.height);
						var top = cells[i];
						var current = top;
						
                        // 中文注释：向上遍历父节点，累加父泳道的起始大小
						while (current != null)
						{
							top = current;
							current = model.getParent(current);
							var tmp = (this.graph.isSwimlane(current)) ?
									this.graph.getStartSize(current) :
									new mxRectangle();
							size.width += tmp.width;
							size.height += tmp.height;
						}
						
                        // 中文注释：根据父节点方向或全局方向调整泳道大小
						var parentHorizontal = (current != null) ? this.isCellHorizontal(current) : this.horizontal;
						this.resizeSwimlane(top, size.width, size.height, parentHorizontal);
					}
				}
			}
		}
		finally
		{
            // 中文注释：结束模型更新，提交更改
			model.endUpdate();
		}
	}
};

/**
 * Function: resizeSwimlane
 * 
 * Called from <cellsResized> for all swimlanes that are not ignored to update
 * the size of the siblings and the size of the parent swimlanes, recursively,
 * if <bubbling> is true.
 * 
 * Parameters:
 * 
 * swimlane - <mxCell> whose size has changed.
 */
// 中文注释：递归调整泳道及其子泳道的大小
// 参数：
// swimlane - 大小发生变化的泳道
// w - 目标宽度
// h - 目标高度
// parentHorizontal - 父泳道的方向
// 逻辑说明：根据方向调整泳道大小，并递归调整其子泳道
mxSwimlaneManager.prototype.resizeSwimlane = function(swimlane, w, h, parentHorizontal)
{
	var model = this.getGraph().getModel();
	
    // 中文注释：开始更新模型，确保批量操作
	model.beginUpdate();
	try
	{
        // 中文注释：获取当前泳道的方向
		var horizontal = this.isCellHorizontal(swimlane);
		
        // 中文注释：仅处理有效泳道
		if (!this.isSwimlaneIgnored(swimlane))
		{
			var geo = model.getGeometry(swimlane);
			
			if (geo != null)
			{
                // 中文注释：根据方向检查是否需要调整泳道大小
				if ((parentHorizontal && geo.height != h) || (!parentHorizontal && geo.width != w))
				{
					geo = geo.clone();
					
                    // 中文注释：根据父泳道方向更新宽度或高度
					if (parentHorizontal)
					{
						geo.height = h;
					}
					else
					{
						geo.width = w;
					}

                    // 中文注释：应用新的几何信息
					model.setGeometry(swimlane, geo);
				}
			}
		}

        // 中文注释：计算当前泳道的起始大小，调整目标尺寸
		var tmp = (this.graph.isSwimlane(swimlane)) ?
				this.graph.getStartSize(swimlane) :
				new mxRectangle();
		w -= tmp.width;
		h -= tmp.height;
		
        // 中文注释：递归调整子泳道的大小
		var childCount = model.getChildCount(swimlane);
		
		for (var i = 0; i < childCount; i++)
		{
			var child = model.getChildAt(swimlane, i);
			this.resizeSwimlane(child, w, h, horizontal);
		}
	}
	finally
	{
        // 中文注释：结束模型更新，提交更改
		model.endUpdate();
	}
};

/**
 * Function: destroy
 * 
 * Removes all handlers from the <graph> and deletes the reference to it.
 */
// 中文注释：销毁管理器，移除所有事件监听器并清除图对象引用
// 逻辑说明：调用 setGraph(null) 解除绑定
mxSwimlaneManager.prototype.destroy = function()
{
	this.setGraph(null);
};
