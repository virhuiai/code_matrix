/**
 * Copyright (c) 2006-2017, JGraph Ltd
 * Copyright (c) 2006-2017, Gaudenz Alder
 */
/**
 * Class: mxTemporaryCellStates
 * 
 * Creates a temporary set of cell states.
 */
// 中文注释：mxTemporaryCellStates 类用于创建一组临时的单元格状态，用于在不修改原始模型的情况下管理单元格的可视化状态。
function mxTemporaryCellStates(view, scale, cells, isCellVisibleFn, getLinkForCellState)
{
    // 中文注释：初始化缩放比例，如果未提供 scale 参数，则默认为 1。
	scale = (scale != null) ? scale : 1;
    // 中文注释：保存传入的视图对象，用于后续操作。
	this.view = view;
	
	// Stores the previous state
    // 中文注释：保存原始的单元格状态验证函数。
	this.oldValidateCellState = view.validateCellState;
    // 中文注释：保存原始的图形边界。
	this.oldBounds = view.getGraphBounds();
    // 中文注释：保存原始的状态字典。
	this.oldStates = view.getStates();
    // 中文注释：保存原始的缩放比例。
	this.oldScale = view.getScale();
    // 中文注释：保存原始的形状重绘函数。
	this.oldDoRedrawShape = view.graph.cellRenderer.doRedrawShape;

	var self = this;

	// Overrides doRedrawShape and paint shape to add links on shapes
    // 中文注释：如果提供了 getLinkForCellState 函数，则重写 doRedrawShape 方法，为形状添加超链接。
	if (getLinkForCellState != null)
	{
		view.graph.cellRenderer.doRedrawShape = function(state)
		{
            // 中文注释：保存原始的绘制函数。
			var oldPaint = state.shape.paint;
			
            // 中文注释：重写绘制函数以支持在形状上添加超链接。
			state.shape.paint = function(c)
			{
                // 中文注释：获取当前单元格状态的超链接。
				var link = getLinkForCellState(state);
				
                // 中文注释：如果存在超链接，则设置画布的链接属性。
				if (link != null)
				{
					c.setLink(link);
				}
				
                // 中文注释：调用原始绘制函数以保持原有绘制逻辑。
				oldPaint.apply(this, arguments);
				
                // 中文注释：在绘制完成后，如果设置了链接，则移除链接以恢复默认状态。
				if (link != null)
				{
					c.setLink(null);
				}
			};
			
            // 中文注释：调用原始的 doRedrawShape 方法，并恢复原始绘制函数。
			self.oldDoRedrawShape.apply(view.graph.cellRenderer, arguments);
			state.shape.paint = oldPaint;
		};
	}

	// Overrides validateCellState to ignore invisible cells
    // 中文注释：重写 validateCellState 方法以忽略不可见的单元格。
	view.validateCellState = function(cell, resurse)
	{
        // 中文注释：仅当单元格存在且通过 isCellVisibleFn 检查（如果提供）时，才验证单元格状态。
		if (cell == null || isCellVisibleFn == null || isCellVisibleFn(cell))
		{
            // 中文注释：调用原始的 validateCellState 方法以保持原有逻辑。
			return self.oldValidateCellState.apply(view, arguments);
		}
		
        // 中文注释：如果单元格不可见，则返回 null，跳过验证。
		return null;
	};
	
	// Creates space for new states
    // 中文注释：创建新的状态字典以存储临时状态。
	view.setStates(new mxDictionary());
    // 中文注释：设置视图的缩放比例。
	view.setScale(scale);
	
    // 中文注释：如果提供了单元格列表，则验证这些单元格并计算其边界框。
	if (cells != null)
	{
        // 中文注释：重置视图的验证状态。
		view.resetValidationState();
        // 中文注释：初始化边界框变量。
		var bbox = null;

		// Validates the vertices and edges without adding them to
		// the model so that the original cells are not modified
        // 中文注释：验证顶点和边，但不将其添加到模型中，以避免修改原始单元格。
		for (var i = 0; i < cells.length; i++)
		{
            // 中文注释：验证当前单元格并获取其边界框。
			var bounds = view.getBoundingBox(view.validateCellState(view.validateCell(cells[i])));
			
            // 中文注释：如果边界框为空，则直接使用当前单元格的边界框。
			if (bbox == null)
			{
				bbox = bounds;
			}
            // 中文注释：否则，将当前单元格的边界框合并到总边界框中。
			else
			{
				bbox.add(bounds);
			}
		}

        // 中文注释：设置视图的图形边界为计算得到的边界框，或默认空矩形。
		view.setGraphBounds(bbox || new mxRectangle());
	}
};

/**
 * Variable: view
 *
 * Holds the width of the rectangle. Default is 0.
 */
// 中文注释：view 变量存储视图对象，用于管理单元格状态和图形渲染。
mxTemporaryCellStates.prototype.view = null;

/**
 * Variable: oldStates
 *
 * Holds the height of the rectangle. Default is 0.
 */
// 中文注释：oldStates 变量保存原始的状态字典，用于在销毁时恢复。
mxTemporaryCellStates.prototype.oldStates = null;

/**
 * Variable: oldBounds
 *
 * Holds the height of the rectangle. Default is 0.
 */
// 中文注释：oldBounds 变量保存原始的图形边界，用于在销毁时恢复。
mxTemporaryCellStates.prototype.oldBounds = null;

/**
 * Variable: oldScale
 *
 * Holds the height of the rectangle. Default is 0.
 */
// 中文注释：oldScale 变量保存原始的缩放比例，用于在销毁时恢复。
mxTemporaryCellStates.prototype.oldScale = null;

/**
 * Function: destroy
 * 
 * Returns the top, left corner as a new <mxPoint>.
 */
// 中文注释：destroy 方法用于销毁临时单元格状态，恢复原始的视图设置。
mxTemporaryCellStates.prototype.destroy = function()
{
    // 中文注释：恢复原始缩放比例。
	this.view.setScale(this.oldScale);
    // 中文注释：恢复原始状态字典。
	this.view.setStates(this.oldStates);
    // 中文注释：恢复原始图形边界。
	this.view.setGraphBounds(this.oldBounds);
    // 中文注释：恢复原始单元格状态验证函数。
	this.view.validateCellState = this.oldValidateCellState;
    // 中文注释：恢复原始形状重绘函数。
	this.view.graph.cellRenderer.doRedrawShape = this.oldDoRedrawShape;
};
