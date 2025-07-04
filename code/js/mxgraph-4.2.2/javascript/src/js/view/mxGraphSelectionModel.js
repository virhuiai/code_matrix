/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxGraphSelectionModel
 *
 * Implements the selection model for a graph. Here is a listener that handles
 * all removed selection cells.
 * 
 * (code)
 * graph.getSelectionModel().addListener(mxEvent.CHANGE, function(sender, evt)
 * {
 *   var cells = evt.getProperty('added');
 *   
 *   for (var i = 0; i < cells.length; i++)
 *   {
 *     // Handle cells[i]...
 *   }
 * });
 * (end)
 * 
 * Event: mxEvent.UNDO
 * 
 * Fires after the selection was changed in <changeSelection>. The
 * <code>edit</code> property contains the <mxUndoableEdit> which contains the
 * <mxSelectionChange>.
 * 
 * Event: mxEvent.CHANGE
 * 
 * Fires after the selection changes by executing an <mxSelectionChange>. The
 * <code>added</code> and <code>removed</code> properties contain arrays of
 * cells that have been added to or removed from the selection, respectively.
 * The names are inverted due to historic reasons. This cannot be changed.
 * 
 * Constructor: mxGraphSelectionModel
 *
 * Constructs a new graph selection model for the given <mxGraph>.
 * 
 * Parameters:
 * 
 * graph - Reference to the enclosing <mxGraph>.
 */
// 中文注释：
// 类：mxGraphSelectionModel
// 实现图形的选取模型。以下是一个处理所有移除选取单元的监听器示例代码。
// 事件：mxEvent.UNDO
// 在 changeSelection 方法更改选取后触发，edit 属性包含 mxUndoableEdit 对象，其中包含 mxSelectionChange。
// 事件：mxEvent.CHANGE
// 在执行 mxSelectionChange 后触发，表示选取发生变化。added 和 removed 属性分别包含添加到或从选取中移除的单元数组。
// 由于历史原因，属性名称相反，无法更改。
// 构造函数：mxGraphSelectionModel
// 为给定的 mxGraph 创建一个新的图形选取模型。
// 参数：
// graph - 指向外部 mxGraph 的引用。
function mxGraphSelectionModel(graph)
{
	this.graph = graph;
	this.cells = [];
    // 中文注释：
    // 初始化函数，设置关联的图形对象 graph 和空的选取单元数组 cells。
    // graph：关联的 mxGraph 对象，用于操作图形。
    // cells：存储当前选取的单元数组，初始化为空。
};

/**
 * Extends mxEventSource.
 */
// 中文注释：
// 扩展 mxEventSource 类，使 mxGraphSelectionModel 具备事件触发能力。
mxGraphSelectionModel.prototype = new mxEventSource();
mxGraphSelectionModel.prototype.constructor = mxGraphSelectionModel;

/**
 * Variable: doneResource
 * 
 * Specifies the resource key for the status message after a long operation.
 * If the resource for this key does not exist then the value is used as
 * the status message. Default is 'done'.
 */
// 中文注释：
// 变量：doneResource
// 指定长时间操作完成后状态消息的资源键。
// 如果该键的资源不存在，则直接使用该值作为状态消息。
// 默认值：'done'（当 mxClient.language 不为 'none' 时）。
mxGraphSelectionModel.prototype.doneResource = (mxClient.language != 'none') ? 'done' : '';

/**
 * Variable: updatingSelectionResource
 *
 * Specifies the resource key for the status message while the selection is
 * being updated. If the resource for this key does not exist then the
 * value is used as the status message. Default is 'updatingSelection'.
 */
// 中文注释：
// 变量：updatingSelectionResource
// 指定选取更新时状态消息的资源键。
// 如果该键的资源不存在，则直接使用该值作为状态消息。
// 默认值：'updatingSelection'（当 mxClient.language 不为 'none' 时）。
mxGraphSelectionModel.prototype.updatingSelectionResource = (mxClient.language != 'none') ? 'updatingSelection' : '';

/**
 * Variable: graph
 * 
 * Reference to the enclosing <mxGraph>.
 */
// 中文注释：
// 变量：graph
// 指向外部 mxGraph 的引用，表示当前选取模型关联的图形对象。
mxGraphSelectionModel.prototype.graph = null;

/**
 * Variable: singleSelection
 *
 * Specifies if only one selected item at a time is allowed.
 * Default is false.
 */
// 中文注释：
// 变量：singleSelection
// 指定是否只允许同时选择一个单元。
// 默认值：false，表示允许多选。
mxGraphSelectionModel.prototype.singleSelection = false;

/**
 * Function: isSingleSelection
 *
 * Returns <singleSelection> as a boolean.
 */
// 中文注释：
// 函数：isSingleSelection
// 返回 singleSelection 属性的布尔值，用于判断是否为单选模式。
mxGraphSelectionModel.prototype.isSingleSelection = function()
{
	return this.singleSelection;
};

/**
 * Function: setSingleSelection
 *
 * Sets the <singleSelection> flag.
 * 
 * Parameters:
 * 
 * singleSelection - Boolean that specifies the new value for
 * <singleSelection>.
 */
// 中文注释：
// 函数：setSingleSelection
// 设置 singleSelection 标志，控制是否启用单选模式。
// 参数：
// singleSelection - 布尔值，指定 singleSelection 的新值。
mxGraphSelectionModel.prototype.setSingleSelection = function(singleSelection)
{
	this.singleSelection = singleSelection;
};

/**
 * Function: isSelected
 *
 * Returns true if the given <mxCell> is selected.
 */
// 中文注释：
// 函数：isSelected
// 判断给定的 mxCell 是否已被选取。
// 返回值：如果单元已被选取，返回 true；否则返回 false。
mxGraphSelectionModel.prototype.isSelected = function(cell)
{
	if (cell != null)
	{
		return mxUtils.indexOf(this.cells, cell) >= 0;
	}
	
	return false;
    // 中文注释：
    // 检查 cell 是否存在于 cells 数组中。
    // 参数：
    // cell - 要检查的 mxCell 对象。
    // 使用 mxUtils.indexOf 方法查找 cell 在 cells 数组中的索引，若索引 >= 0 则表示已选取。
};

/**
 * Function: isEmpty
 *
 * Returns true if no cells are currently selected.
 */
// 中文注释：
// 函数：isEmpty
// 判断当前是否没有选取任何单元。
// 返回值：如果 cells 数组长度为 0，返回 true；否则返回 false。
mxGraphSelectionModel.prototype.isEmpty = function()
{
	return this.cells.length == 0;
};

/**
 * Function: clear
 *
 * Clears the selection and fires a <change> event if the selection was not
 * empty.
 */
// 中文注释：
// 函数：clear
// 清空当前选取，并在其非空时触发 change 事件。
// 逻辑：调用 changeSelection 方法，传入 null 作为添加单元，传入当前 cells 数组作为移除单元。
mxGraphSelectionModel.prototype.clear = function()
{
	this.changeSelection(null, this.cells);
};

/**
 * Function: setCell
 *
 * Selects the specified <mxCell> using <setCells>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to be selected.
 */
// 中文注释：
// 函数：setCell
// 选择指定的 mxCell，调用 setCells 方法实现。
// 参数：
// cell - 要选择的 mxCell 对象。
// 逻辑：如果 cell 不为 null，则将其作为单一元素数组传递给 setCells。
mxGraphSelectionModel.prototype.setCell = function(cell)
{
	if (cell != null)
	{
		this.setCells([cell]);
	}
};

/**
 * Function: setCells
 *
 * Selects the given array of <mxCells> and fires a <change> event.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to be selected.
 */
// 中文注释：
// 函数：setCells
// 选择给定的 mxCell 数组，并触发 change 事件。
// 参数：
// cells - 要选择的 mxCell 对象数组。
// 逻辑：
// 1. 如果启用单选模式（singleSelection 为 true），则只选择第一个可选取的单元。
// 2. 遍历 cells 数组，仅将可选取的单元（通过 graph.isCellSelectable 判断）加入临时数组。
// 3. 调用 changeSelection 方法，传入新选取的单元和当前已选取的单元。
mxGraphSelectionModel.prototype.setCells = function(cells)
{
	if (cells != null)
	{
		if (this.singleSelection)
		{
			cells = [this.getFirstSelectableCell(cells)];
		}
	
		var tmp = [];
		
		for (var i = 0; i < cells.length; i++)
		{
			if (this.graph.isCellSelectable(cells[i]))
			{
				tmp.push(cells[i]);
			}	
		}

		this.changeSelection(tmp, this.cells);
	}
};

/**
 * Function: getFirstSelectableCell
 *
 * Returns the first selectable cell in the given array of cells.
 */
// 中文注释：
// 函数：getFirstSelectableCell
// 返回给定单元数组中第一个可选取的单元。
// 参数：
// cells - mxCell 对象数组。
// 返回值：第一个通过 graph.isCellSelectable 判断为可选取的单元，或 null（如果没有可选取的单元）。
mxGraphSelectionModel.prototype.getFirstSelectableCell = function(cells)
{
	if (cells != null)
	{
		for (var i = 0; i < cells.length; i++)
		{
			if (this.graph.isCellSelectable(cells[i]))
			{
				return cells[i];
			}
		}
	}
	
	return null;
};

/**
 * Function: addCell
 * 
 * Adds the given <mxCell> to the selection and fires a <select> event.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to add to the selection.
 */
// 中文注释：
// 函数：addCell
// 将指定的 mxCell 添加到选取中，并触发 select 事件。
// 参数：
// cell - 要添加的 mxCell 对象。
// 逻辑：如果 cell 不为 null，则将其作为单一元素数组传递给 addCells。
mxGraphSelectionModel.prototype.addCell = function(cell)
{
	if (cell != null)
	{
		this.addCells([cell]);
	}
};

/**
 * Function: addCells
 * 
 * Adds the given array of <mxCells> to the selection and fires a <select>
 * event.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to add to the selection.
 */
// 中文注释：
// 函数：addCells
// 将给定的 mxCell 数组添加到选取中，并触发 select 事件。
// 参数：
// cells - 要添加的 mxCell 对象数组。
// 逻辑：
// 1. 如果启用单选模式，则清空当前选取并只选择第一个可选取的单元。
// 2. 遍历 cells 数组，仅将未选取且可选取的单元加入临时数组。
// 3. 调用 changeSelection 方法，传入新添加的单元和需要移除的单元（单选模式下为当前 cells）。
mxGraphSelectionModel.prototype.addCells = function(cells)
{
	if (cells != null)
	{
		var remove = null;
		
		if (this.singleSelection)
		{
			remove = this.cells;
			cells = [this.getFirstSelectableCell(cells)];
		}

		var tmp = [];
		
		for (var i = 0; i < cells.length; i++)
		{
			if (!this.isSelected(cells[i]) &&
				this.graph.isCellSelectable(cells[i]))
			{
				tmp.push(cells[i]);
			}	
		}

		this.changeSelection(tmp, remove);
	}
};

/**
 * Function: removeCell
 *
 * Removes the specified <mxCell> from the selection and fires a <select>
 * event for the remaining cells.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to remove from the selection.
 */
// 中文注释：
// 函数：removeCell
// 从选取中移除指定的 mxCell，并为剩余单元触发 select 事件。
// 参数：
// cell - 要移除的 mxCell 对象。
// 逻辑：如果 cell 不为 null，则将其作为单一元素数组传递给 removeCells。
mxGraphSelectionModel.prototype.removeCell = function(cell)
{
	if (cell != null)
	{
		this.removeCells([cell]);
	}
};

/**
 * Function: removeCells
 */
// 中文注释：
// 函数：removeCells
// 从选取中移除给定的 mxCell 数组，并为剩余单元触发 select 事件。
// 参数：
// cells - 要移除的 mxCell 对象数组。
// 逻辑：
// 1. 遍历 cells 数组，仅将已选取的单元加入临时数组。
// 2. 调用 changeSelection 方法，传入 null 作为添加单元，传入需要移除的单元。
mxGraphSelectionModel.prototype.removeCells = function(cells)
{
	if (cells != null)
	{
		var tmp = [];
		
		for (var i = 0; i < cells.length; i++)
		{
			if (this.isSelected(cells[i]))
			{
				tmp.push(cells[i]);
			}
		}
		
		this.changeSelection(null, tmp);	
	}
};

/**
 * Function: changeSelection
 *
 * Adds/removes the specified arrays of <mxCell> to/from the selection.
 * 
 * Parameters:
 * 
 * added - Array of <mxCell> to add to the selection.
 * remove - Array of <mxCell> to remove from the selection.
 */
// 中文注释：
// 函数：changeSelection
// 添加或移除指定 mxCell 数组到/从选取中。
// 参数：
// added - 要添加的 mxCell 对象数组。
// remove - 要移除的 mxCell 对象数组。
// 逻辑：
// 1. 如果有单元需要添加或移除，则创建 mxSelectionChange 对象。
// 2. 执行更改操作，并创建一个 mxUndoableEdit 对象以支持撤销。
// 3. 触发 mxEvent.UNDO 事件，通知选取变化。
mxGraphSelectionModel.prototype.changeSelection = function(added, removed)
{
	if ((added != null &&
		added.length > 0 &&
		added[0] != null) ||
		(removed != null &&
		removed.length > 0 &&
		removed[0] != null))
	{
		var change = new mxSelectionChange(this, added, removed);
		change.execute();
		var edit = new mxUndoableEdit(this, false);
		edit.add(change);
		this.fireEvent(new mxEventObject(mxEvent.UNDO, 'edit', edit));
	}
};

/**
 * Function: cellAdded
 *
 * Inner callback to add the specified <mxCell> to the selection. No event
 * is fired in this implementation.
 * 
 * Paramters:
 * 
 * cell - <mxCell> to add to the selection.
 */
// 中文注释：
// 函数：cellAdded
// 内部回调函数，将指定的 mxCell 添加到选取中，不触发事件。
// 参数：
// cell - 要添加的 mxCell 对象。
// 逻辑：如果单元不为空且未被选取，则将其添加到 cells 数组。
mxGraphSelectionModel.prototype.cellAdded = function(cell)
{
	if (cell != null &&
		!this.isSelected(cell))
	{
		this.cells.push(cell);
	}
};

/**
 * Function: cellRemoved
 *
 * Inner callback to remove the specified <mxCell> from the selection. No
 * event is fired in this implementation.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to remove from the selection.
 */
// 中文注释：
// 函数：cellRemoved
// 内部回调函数，从选取中移除指定的 mxCell，不触发事件。
// 参数：
// cell - 要移除的 mxCell 对象。
// 逻辑：如果单元不为空且在 cells 数组中，则从数组中移除。
mxGraphSelectionModel.prototype.cellRemoved = function(cell)
{
	if (cell != null)
	{
		var index = mxUtils.indexOf(this.cells, cell);
		
		if (index >= 0)
		{
			this.cells.splice(index, 1);
		}
	}
};

/**
 * Class: mxSelectionChange
 *
 * Action to change the current root in a view.
 *
 * Constructor: mxCurrentRootChange
 *
 * Constructs a change of the current root in the given view.
 */
// 中文注释：
// 类：mxSelectionChange
// 用于更改视图中当前根的操作。
// 构造函数：mxCurrentRootChange
// 为给定的视图创建当前根的更改。
function mxSelectionChange(selectionModel, added, removed)
{
	this.selectionModel = selectionModel;
	this.added = (added != null) ? added.slice() : null;
	this.removed = (removed != null) ? removed.slice() : null;
    // 中文注释：
    // 初始化函数，设置选取模型及添加/移除的单元数组。
    // 参数：
    // selectionModel - 关联的 mxGraphSelectionModel 对象。
    // added - 要添加的 mxCell 数组，复制以避免修改原数组。
    // removed - 要移除的 mxCell 数组，复制以避免修改原数组。
};

/**
 * Function: execute
 *
 * Changes the current root of the view.
 */
// 中文注释：
// 函数：execute
// 执行选取更改操作，更新视图的当前根。
// 逻辑：
// 1. 设置状态消息为 updatingSelectionResource。
// 2. 移除 removed 数组中的单元，调用 cellRemoved。
// 3. 添加 added 数组中的单元，调用 cellAdded。
// 4. 交换 added 和 removed 数组以支持撤销操作。
// 5. 更新状态消息为 doneResource。
// 6. 触发 mxEvent.CHANGE 事件，通知选取变化，包含 added 和 removed 属性。
mxSelectionChange.prototype.execute = function()
{
	var t0 = mxLog.enter('mxSelectionChange.execute');
	window.status = mxResources.get(
		this.selectionModel.updatingSelectionResource) ||
		this.selectionModel.updatingSelectionResource;

	if (this.removed != null)
	{
		for (var i = 0; i < this.removed.length; i++)
		{
			this.selectionModel.cellRemoved(this.removed[i]);
		}
	}

	if (this.added != null)
	{
		for (var i = 0; i < this.added.length; i++)
		{
			this.selectionModel.cellAdded(this.added[i]);
		}
	}
	
	var tmp = this.added;
	this.added = this.removed;
	this.removed = tmp;

	window.status = mxResources.get(this.selectionModel.doneResource) ||
		this.selectionModel.doneResource;
	mxLog.leave('mxSelectionChange.execute', t0);
	
	this.selectionModel.fireEvent(new mxEventObject(mxEvent.CHANGE,
			'added', this.added, 'removed', this.removed));
};
