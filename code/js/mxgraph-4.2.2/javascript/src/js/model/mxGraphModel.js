/**
 * Copyright (c) 2006-2018, JGraph Ltd
 * Copyright (c) 2006-2018, Gaudenz Alder
 */
/**
 * Class: mxGraphModel
 * 
 * Extends <mxEventSource> to implement a graph model. The graph model acts as
 * a wrapper around the cells which are in charge of storing the actual graph
 * datastructure. The model acts as a transactional wrapper with event
 * notification for all changes, whereas the cells contain the atomic
 * operations for updating the actual datastructure.
 * 
 * Layers:
 * 
 * The cell hierarchy in the model must have a top-level root cell which
 * contains the layers (typically one default layer), which in turn contain the
 * top-level cells of the layers. This means each cell is contained in a layer.
 * If no layers are required, then all new cells should be added to the default
 * layer.
 * 
 * Layers are useful for hiding and showing groups of cells, or for placing
 * groups of cells on top of other cells in the display. To identify a layer,
 * the <isLayer> function is used. It returns true if the parent of the given
 * cell is the root of the model.
 * 
 * // 中文注释：
 * // mxGraphModel 类继承自 mxEventSource，用于实现图形模型。
 * // 模型作为单元（cells）的包装器，单元负责存储实际的图形数据结构。
 * // 模型提供事务性包装，包含事件通知以跟踪所有更改，而单元则包含更新实际数据结构的原子操作。
 * // 层级结构说明：
 * // 模型中的单元层级必须包含一个顶级根单元（root cell），其中包含层（通常有一个默认层）。
 * // 层中包含该层的顶级单元，所有单元都必须属于某个层。
 * // 如果不需要层，则所有新单元应添加到默认层。
 * // 层的作用：用于隐藏或显示单元组，或将某些单元组置于其他单元之上显示。
 * // 使用 isLayer 函数判断是否为层，如果单元的父节点是模型的根节点，则返回 true。
 *
 * Events:
 * 
 * See events section for more details. There is a new set of events for
 * tracking transactional changes as they happen. The events are called
 * startEdit for the initial beginUpdate, executed for each executed change
 * and endEdit for the terminal endUpdate. The executed event contains a
 * property called change which represents the change after execution.
 * 
 * // 中文注释：
 * // 事件说明：
 * // 模型支持一组新的事件，用于实时跟踪事务性更改。
 * // 事件包括：
 * // - startEdit：当 beginUpdate 开始时触发。
 * // - executed：每次执行更改时触发，包含 change 属性，表示执行后的更改。
 * // - endEdit：当 endUpdate 结束时触发。
 *
 * Encoding the model:
 * 
 * To encode a graph model, use the following code:
 * 
 * (code)
 * var enc = new mxCodec();
 * var node = enc.encode(graph.getModel());
 * (end)
 * 
 * This will create an XML node that contains all the model information.
 * 
 * // 中文注释：
 * // 模型编码：
 * // 使用 mxCodec 类的 encode 方法将图形模型编码为 XML 节点。
 * // 上述代码创建一个包含所有模型信息的 XML 节点。
 *
 * Encoding and decoding changes:
 * 
 * For the encoding of changes, a graph model listener is required that encodes
 * each change from the given array of changes.
 * 
 * (code)
 * model.addListener(mxEvent.CHANGE, function(sender, evt)
 * {
 *   var changes = evt.getProperty('edit').changes;
 *   var nodes = [];
 *   var codec = new mxCodec();
 * 
 *   for (var i = 0; i < changes.length; i++)
 *   {
 *     nodes.push(codec.encode(changes[i]));
 *   }
 *   // do something with the nodes
 * });
 * (end)
 * 
 * // 中文注释：
 * // 更改编码：
 * // 需要为模型添加监听器，监听 mxEvent.CHANGE 事件。
 * // 监听器从事件中获取更改数组（changes），并使用 mxCodec 将每个更改编码为节点。
 * // nodes 数组存储编码后的更改节点，可用于后续处理。
 *
 * For the decoding and execution of changes, the codec needs a lookup function
 * that allows it to resolve cell IDs as follows:
 * 
 * (code)
 * var codec = new mxCodec();
 * codec.lookup = function(id)
 * {
 *   return model.getCell(id);
 * }
 * (end)
 * 
 * // 中文注释：
 * // 更改解码：
 * // 解码和执行更改时，需为 mxCodec 设置 lookup 函数。
 * // 该函数通过单元 ID 获取对应的 mxCell 对象，用于解析编码中的单元引用。
 *
 * For each encoded change (represented by a node), the following code can be
 * used to carry out the decoding and create a change object.
 * 
 * (code)
 * var changes = [];
 * var change = codec.decode(node);
 * change.model = model;
 * change.execute();
 * changes.push(change);
 * (end)
 * 
 * // 中文注释：
 * // 更改解码与执行：
 * // 对每个编码的更改节点，使用 codec.decode 方法解码为更改对象。
 * // 将更改对象的 model 属性设置为当前模型，调用 execute 方法执行更改，并将更改存储到 changes 数组中。
 *
 * The changes can then be dispatched using the model as follows.
 * 
 * (code)
 * var edit = new mxUndoableEdit(model, false);
 * edit.changes = changes;
 * 
 * edit.notify = function()
 * {
 *   edit.source.fireEvent(new mxEventObject(mxEvent.CHANGE,
 *   	'edit', edit, 'changes', edit.changes));
 *   edit.source.fireEvent(new mxEventObject(mxEvent.NOTIFY,
 *   	'edit', edit, 'changes', edit.changes));
 * }
 * 
 * model.fireEvent(new mxEventObject(mxEvent.UNDO, 'edit', edit));
 * model.fireEvent(new mxEventObject(mxEvent.CHANGE,
 * 		'edit', edit, 'changes', changes));
 * (end)
 *
 * // 中文注释：
 * // 更改分发：
 * // 创建 mxUndoableEdit 对象，将更改数组赋值给其 changes 属性。
 * // 定义 notify 方法，触发 mxEvent.CHANGE 和 mxEvent.NOTIFY 事件，通知更改。
 * // 通过模型触发 mxEvent.UNDO 和 mxEvent.CHANGE 事件，分发更改。
 *
 * Event: mxEvent.CHANGE
 *
 * Fires when an undoable edit is dispatched. The <code>edit</code> property
 * contains the <mxUndoableEdit>. The <code>changes</code> property contains
 * the array of atomic changes inside the undoable edit. The changes property
 * is <strong>deprecated</strong>, please use edit.changes instead.
 *
 * // 中文注释：
 * // 事件：mxEvent.CHANGE
 * // 当分发可撤销的编辑时触发。
 * // edit 属性包含 mxUndoableEdit 对象。
 * // changes 属性包含可撤销编辑中的原子更改数组（已弃用，建议使用 edit.changes）。
 *
 * Example:
 * 
 * For finding newly inserted cells, the following code can be used:
 * 
 * (code)
 * graph.model.addListener(mxEvent.CHANGE, function(sender, evt)
 * {
 *   var changes = evt.getProperty('edit').changes;
 * 
 *   for (var i = 0; i < changes.length; i++)
 *   {
 *     var change = changes[i];
 *     
 *     if (change instanceof mxChildChange &&
 *       change.change.previous == null)
 *     {
 *       graph.startEditingAtCell(change.child);
 *       break;
 *     }
 *   }
 * });
 * (end)
 * 
 * // 中文注释：
 * // 示例：查找新插入的单元
 * // 通过监听 mxEvent.CHANGE 事件，检查 changes 数组。
 * // 如果更改是 mxChildChange 类型且 previous 属性为 null，说明是新插入的单元。
 * // 调用 graph.startEditingAtCell 方法开始编辑新插入的单元。
 *
 * Event: mxEvent.NOTIFY
 *
 * Same as <mxEvent.CHANGE>, this event can be used for classes that need to
 * implement a sync mechanism between this model and, say, a remote model. In
 * such a setup, only local changes should trigger a notify event and all
 * changes should trigger a change event.
 * 
 * // 中文注释：
 * // 事件：mxEvent.NOTIFY
 * // 与 mxEvent.CHANGE 类似，用于需要同步本地模型与远程模型的场景。
 * // 仅本地更改应触发 notify 事件，所有更改均触发 change 事件。
 *
 * Event: mxEvent.EXECUTE
 * 
 * Fires between begin- and endUpdate and after an atomic change was executed
 * in the model. The <code>change</code> property contains the atomic change
 * that was executed.
 * 
 * // 中文注释：
 * // 事件：mxEvent.EXECUTE
 * // 在 beginUpdate 和 endUpdate 之间，以及原子更改执行后触发。
 * // change 属性包含已执行的原子更改。
 *
 * Event: mxEvent.EXECUTED
 * 
 * Fires between START_EDIT and END_EDIT after an atomic change was executed.
 * The <code>change</code> property contains the change that was executed.
 *
 * // 中文注释：
 * // 事件：mxEvent.EXECUTED
 * // 在 START_EDIT 和 END_EDIT 之间，原子更改执行后触发。
 * // change 属性包含已执行的更改。
 *
 * Event: mxEvent.BEGIN_UPDATE
 *
 * Fires after the <updateLevel> was incremented in <beginUpdate>. This event
 * contains no properties.
 * 
 * // 中文注释：
 * // 事件：mxEvent.BEGIN_UPDATE
 * // 在 beginUpdate 中 updateLevel 增加后触发，无属性。
 *
 * Event: mxEvent.START_EDIT
 *
 * Fires after the <updateLevel> was changed from 0 to 1. This event
 * contains no properties.
 * 
 * // 中文注释：
 * // 事件：mxEvent.START_EDIT
 * // 在 updateLevel 从 0 变为 1 时触发，无属性。
 *
 * Event: mxEvent.END_UPDATE
 * 
 * Fires after the <updateLevel> was decreased in <endUpdate> but before any
 * notification or change dispatching. The <code>edit</code> property contains
 * the <currentEdit>.
 * 
 * // 中文注释：
 * // 事件：mxEvent.END_UPDATE
 * // 在 endUpdate 中 updateLevel 减少后、通知或更改分发前触发。
 * // edit 属性包含 currentEdit 对象。
 *
 * Event: mxEvent.END_EDIT
 *
 * Fires after the <updateLevel> was changed from 1 to 0. This event
 * contains no properties.
 * 
 * // tsl:
 * // 事件：mxEvent.END_EDIT
 * // 在 updateLevel 从 1 变为 0 时触发，无属性。
 *
 * Event: mxEvent.BEFORE_UNDO
 * 
 * Fires before the change is dispatched after the update level has reached 0
 * in <endUpdate>. The <code>edit</code> property contains the <curreneEdit>.
 * 
 * // 中文注释：
 * // 事件：mxEvent.BEFORE_UNDO
 * // 在 endUpdate 中 updateLevel 达到 0 后、更改分发前触发。
 * // edit 属性包含 currentEdit 对象。
 *
 * Event: mxEvent.UNDO
 * 
 * Fires after the change was dispatched in <endUpdate>. The <code>edit</code>
 * property contains the <currentEdit>.
 * 
 * // 中文注释：
 * // 事件：mxEvent.UNDO
 * // 在 endUpdate 中更改分发后触发。
 * // edit 属性包含 currentEdit 对象。
 *
 * Constructor: mxGraphModel
 * 
 * Constructs a new graph model. If no root is specified then a new root
 * <mxCell> with a default layer is created.
 * 
 * Parameters:
 * 
 * root - <mxCell> that represents the root cell.
 *
 * // 中文注释：
 * // 构造函数：mxGraphModel
 * // 创建一个新的图形模型。
 * // 如果未指定根节点（root），则创建一个包含默认层的 mxCell 作为根节点。
 * // 参数：
 * // root - 表示根单元的 mxCell 对象。
 */
function mxGraphModel(root)
{
	this.currentEdit = this.createUndoableEdit();
	
	if (root != null)
	{
		this.setRoot(root);
	}
	else
	{
		this.clear();
	}
    // 中文注释：
    // 初始化图形模型，创建初始的可撤销编辑对象（currentEdit）。
    // 如果提供了根节点，则调用 setRoot 设置根节点；否则调用 clear 方法创建默认根节点。
};

/**
 * Extends mxEventSource.
 *
 * // 中文注释：
 * // 继承 mxEventSource，获得事件触发和监听功能。
 */
mxGraphModel.prototype = new mxEventSource();
mxGraphModel.prototype.constructor = mxGraphModel;

/**
 * Variable: root
 * 
 * Holds the root cell, which in turn contains the cells that represent the
 * layers of the diagram as child cells. That is, the actual elements of the
 * diagram are supposed to live in the third generation of cells and below.
 *
 * // 中文注释：
 * // 变量：root
 * // 保存根单元，根单元包含表示图形层级的子单元。
 * // 图形的实际元素应位于第三层及以下的单元中。
 */
mxGraphModel.prototype.root = null;

/**
 * Variable: cells
 * 
 * Maps from Ids to cells.
 *
 * // 中文注释：
 * // 变量：cells
 * // 一个映射对象，将单元的 ID 映射到对应的 mxCell 对象。
 */
mxGraphModel.prototype.cells = null;

/**
 * Variable: maintainEdgeParent
 * 
 * Specifies if edges should automatically be moved into the nearest common
 * ancestor of their terminals. Default is true.
 *
 * // 中文注释：
 * // 变量：maintainEdgeParent
 * // 指定是否自动将边移动到其端点的最近公共祖先中。
 * // 默认值为 true。
 */
mxGraphModel.prototype.maintainEdgeParent = true;

/**
 * Variable: ignoreRelativeEdgeParent
 * 
 * Specifies if relative edge parents should be ignored for finding the nearest
 * common ancestors of an edge's terminals. Default is true.
 *
 * // 中文注释：
 * // 变量：ignoreRelativeEdgeParent
 * // 指定在查找边的端点的最近公共祖先时是否忽略相对边父节点。
 * // 默认值为 true。
 */
mxGraphModel.prototype.ignoreRelativeEdgeParent = true;

/**
 * Variable: createIds
 * 
 * Specifies if the model should automatically create Ids for new cells.
 * Default is true.
 *
 * // 中文注释：
 * // 变量：createIds
 * // 指定模型是否为新单元自动生成 ID。
 * // 默认值为 true。
 */
mxGraphModel.prototype.createIds = true;

/**
 * Variable: prefix
 * 
 * Defines the prefix of new Ids. Default is an empty string.
 *
 * // 中文注释：
 * // 变量：prefix
 * // 定义新 ID 的前缀。
 * // 默认值为空字符串。
 */
mxGraphModel.prototype.prefix = '';

/**
 * Variable: postfix
 * 
 * Defines the postfix of new Ids. Default is an empty string.
 *
 * // 中文注释：
 * // 变量：postfix
 * // 定义新 ID 的后缀。
 * // 默认值为空字符串。
 */
mxGraphModel.prototype.postfix = '';

/**
 * Variable: nextId
 * 
 * Specifies the next Id to be created. Initial value is 0.
 *
 * // 中文注释：
 * // 变量：nextId
 * // 指定下一个要创建的 ID。
 * // 初始值为 0。
 */
mxGraphModel.prototype.nextId = 0;

/**
 * Variable: currentEdit
 * 
 * Holds the changes for the current transaction. If the transaction is
 * closed then a new object is created for this variable using
 * <createUndoableEdit>.
 *
 * // 中文注释：
 * // 变量：currentEdit
 * // 保存当前事务的更改。
 * // 当事务关闭时，使用 createUndoableEdit 方法为此变量创建一个新对象。
 */
mxGraphModel.prototype.currentEdit = null;

/**
 * Variable: updateLevel
 * 
 * Counter for the depth of nested transactions. Each call to <beginUpdate>
 * will increment this number and each call to <endUpdate> will decrement
 * it. When the counter reaches 0, the transaction is closed and the
 * respective events are fired. Initial value is 0.
 *
 * // 中文注释：
 * // 变量：updateLevel
 * // 嵌套事务深度的计数器。
 * // 每次调用 beginUpdate 增加该值，调用 endUpdate 减少该值。
 * // 当计数器达到 0 时，事务关闭并触发相应事件。
 * // 初始值为 0。
 */
mxGraphModel.prototype.updateLevel = 0;

/**
 * Variable: endingUpdate
 * 
 * True if the program flow is currently inside endUpdate.
 *
 * // 中文注释：
 * // 变量：endingUpdate
 * // 表示程序流当前是否处于 endUpdate 方法中。
 * // 值为 true 时表示正在执行 endUpdate。
 */
mxGraphModel.prototype.endingUpdate = false;

/**
 * Function: clear
 *
 * Sets a new root using <createRoot>.
 *
 * // 中文注释：
 * // 函数：clear
 * // 使用 createRoot 方法设置一个新的根节点。
 * // 用于清空模型并重新初始化根节点。
 */
mxGraphModel.prototype.clear = function()
{
	this.setRoot(this.createRoot());
};

/**
 * Function: isCreateIds
 *
 * Returns <createIds>.
 *
 * // 中文注释：
 * // 函数：isCreateIds
 * // 返回 createIds 变量的值。
 * // 用于检查是否为新单元自动生成 ID。
 */
mxGraphModel.prototype.isCreateIds = function()
{
	return this.createIds;
};

/**
 * Function: setCreateIds
 *
 * Sets <createIds>.
 *
 * // 中文注释：
 * // 函数：setCreateIds
 * // 设置 createIds 变量的值。
 * // 参数：
 * // value - 布尔值，指定是否为新单元自动生成 ID。
 */
mxGraphModel.prototype.setCreateIds = function(value)
{
	this.createIds = value;
};

/**
 * Function: createRoot
 *
 * Creates a new root cell with a default layer (child 0).
 *
 * // 中文注释：
 * // 函数：createRoot
 * // 创建一个包含默认层（子节点 0）的根单元。
 * // 返回新创建的 mxCell 对象作为根节点。
 */
mxGraphModel.prototype.createRoot = function()
{
	var cell = new mxCell();
	cell.insert(new mxCell());
	
	return cell;
};

/**
 * Function: getCell
 *
 * Returns the <mxCell> for the specified Id or null if no cell can be
 * found for the given Id.
 *
 * Parameters:
 * 
 * id - A string representing the Id of the cell.
 *
 * // 中文注释：
 * // 函数：getCell
 * // 根据指定的 ID 返回对应的 mxCell 对象，如果未找到则返回 null。
 * // 参数：
 * // id - 表示单元 ID 的字符串。
 */
mxGraphModel.prototype.getCell = function(id)
{
	return (this.cells != null) ? this.cells[id] : null;
};

/**
 * Function: filterCells
 * 
 * Returns the cells from the given array where the given filter function
 * returns true.
 *
 * // 中文注释：
 * // 函数：filterCells
 * // 从给定的单元数组中返回满足指定过滤函数的单元。
 * // 参数：
 * // cells - 待过滤的单元数组。
 * // filter - 过滤函数，接受一个 mxCell 参数并返回布尔值。
 * // 返回值：满足过滤条件的单元数组。
 */
mxGraphModel.prototype.filterCells = function(cells, filter)
{
	var result = null;
	
	if (cells != null)
	{
		result = [];
		
		for (var i = 0; i < cells.length; i++)
		{
			if (filter(cells[i]))
			{
				result.push(cells[i]);
			}
		}
	}
	
	return result;
};

/**
 * Function: getDescendants
 * 
 * Returns all descendants of the given cell and the cell itself in an array.
 * 
 * Parameters:
 * 
 * parent - <mxCell> whose descendants should be returned.
 *
 * // 中文注释：
 * // 函数：getDescendants
 * // 返回指定单元及其所有后代的数组。
 * // 参数：
 * // parent - 需要返回后代的 mxCell 对象。
 * // 返回值：包含指定单元及其后代的数组。
 */
mxGraphModel.prototype.getDescendants = function(parent)
{
	return this.filterDescendants(null, parent);
};

/**
 * Function: filterDescendants
 * 
 * Visits all cells recursively and applies the specified filter function
 * to each cell. If the function returns true then the cell is added
 * to the resulting array. The parent and result paramters are optional.
 * If parent is not specified then the recursion starts at <root>.
 * 
 * Example:
 * The following example extracts all vertices from a given model:
 * (code)
 * var filter = function(cell)
 * {
 * 	return model.isVertex(cell);
 * }
 * var vertices = model.filterDescendants(filter);
 * (end)
 * 
 * Parameters:
 * 
 * filter - JavaScript function that takes an <mxCell> as an argument
 * and returns a boolean.
 * parent - Optional <mxCell> that is used as the root of the recursion.
 *
 * // 中文注释：
 * // 函数：filterDescendants
 * // 递归访问所有单元，并对每个单元应用指定的过滤函数。
 * // 如果过滤函数返回 true，则将该单元添加到结果数组中。
 * // 参数：
 * // filter - 接受 mxCell 参数并返回布尔值的 JavaScript 函数。
 * // parent - 可选参数，指定递归的起始根单元，默认为模型的根节点。
 * // 返回值：满足过滤条件的单元数组。
 * // 示例：提取模型中的所有顶点（vertices）。
 */
mxGraphModel.prototype.filterDescendants = function(filter, parent)
{
	// Creates a new array for storing the result
	var result = [];

	// Recursion starts at the root of the model
	parent = parent || this.getRoot();
	
	// Checks if the filter returns true for the cell
	// and adds it to the result array
	if (filter == null || filter(parent))
	{
		result.push(parent);
	}
	
	// Visits the children of the cell
	var childCount = this.getChildCount(parent);
	
	for (var i = 0; i < childCount; i++)
	{
		var child = this.getChildAt(parent, i);
		result = result.concat(this.filterDescendants(filter, child));
	}

	return result;
};

/**
 * Function: getRoot
 * 
 * Returns the root of the model or the topmost parent of the given cell.
 *
 * Parameters:
 * 
 * cell - Optional <mxCell> that specifies the child.
 *
 * // 中文注释：
 * // 函数：getRoot
 * // 返回模型的根节点，或指定单元的最顶级父节点。
 * // 参数：
 * // cell - 可选的 mxCell 对象，指定子节点。
 * // 返回值：根节点或最顶级父节点。
 */
mxGraphModel.prototype.getRoot = function(cell)
{
	var root = cell || this.root;
	
	if (cell != null)
	{
		while (cell != null)
		{
			root = cell;
			cell = this.getParent(cell);
		}
	}
	
	return root;
};

/**
 * Function: setRoot
 * 
 * Sets the <root> of the model using <mxRootChange> and adds the change to
 * the current transaction. This resets all datastructures in the model and
 * is the preferred way of clearing an existing model. Returns the new
 * root.
 * 
 * Example:
 * 
 * (code)
 * var root = new mxCell();
 * root.insert(new mxCell());
 * model.setRoot(root);
 * (end)
 *
 * Parameters:
 * 
 * root - <mxCell> that specifies the new root.
 *
 * // 中文注释：
 * // 函数：setRoot
 * // 使用 mxRootChange 设置模型的根节点，并将更改添加到当前事务。
 * // 重置模型中的所有数据结构，是清空现有模型的首选方法。
 * // 参数：
 * // root - 指定新根节点的 mxCell 对象。
 * // 返回值：新设置的根节点。
 * // 示例：创建一个新根节点并插入一个子节点。
 */
mxGraphModel.prototype.setRoot = function(root)
{
	this.execute(new mxRootChange(this, root));
	
	return root;
};

/**
 * Function: rootChanged
 * 
 * Inner callback to change the root of the model and update the internal
 * datastructures, such as <cells> and <nextId>. Returns the previous root.
 *
 * Parameters:
 * 
 * root - <mxCell> that specifies the new root.
 *
 * // 中文注释：
 * // 函数：rootChanged
 * // 内部回调函数，用于更改模型的根节点并更新内部数据结构（如 cells 和 nextId）。
 * // 参数：
 * // root - 指定新根节点的 mxCell 对象。
 * // 返回值：之前的根节点。
 */
mxGraphModel.prototype.rootChanged = function(root)
{
	var oldRoot = this.root;
	this.root = root;
	
	// Resets counters and datastructures
	this.nextId = 0;
	this.cells = null;
	this.cellAdded(root);
	
	return oldRoot;
};

/**
 * Function: isRoot
 * 
 * Returns true if the given cell is the root of the model and a non-null
 * value.
 *
 * Parameters:
 * 
 * cell - <mxCell> that represents the possible root.
 *
 * // 中文注释：
 * // 函数：isRoot
 * // 判断给定单元是否为模型的根节点且非空。
 * // 参数：
 * // cell - 表示可能根节点的 mxCell 对象。
 * // 返回值：如果单元是根节点且非空，返回 true。
 */
mxGraphModel.prototype.isRoot = function(cell)
{
	return cell != null && this.root == cell;
};

/**
 * Function: isLayer
 * 
 * Returns true if <isRoot> returns true for the parent of the given cell.
 *
 * Parameters:
 * 
 * cell - <mxCell> that represents the possible layer.
 *
 * // 中文注释：
 * // 函数：isLayer
 * // 判断给定单元的父节点是否为根节点，从而确定该单元是否为层。
 * // 参数：
 * // cell - 表示可能层的 mxCell 对象。
 * // 返回值：如果单元的父节点是根节点，返回 true。
 */
mxGraphModel.prototype.isLayer = function(cell)
{
	return this.isRoot(this.getParent(cell));
};

/**
 * Function: isAncestor
 * 
 * Returns true if the given parent is an ancestor of the given child. Note 
 * returns true if child == parent.
 *
 * Parameters:
 * 
 * parent - <mxCell> that specifies the parent.
 * child - <mxCell> that specifies the child.
 *
 * // 中文注释：
 * // 函数：isAncestor
 * // 判断指定父节点是否为子节点的祖先（包括子节点等于父节点的情况）。
 * // 参数：
 * // parent - 指定父节点的 mxCell 对象。
 * // child - 指定子节点的 mxCell 对象。
 * // 返回值：如果父节点是子节点的祖先，返回 true。
 */
mxGraphModel.prototype.isAncestor = function(parent, child)
{
	while (child != null && child != parent)
	{
		child = this.getParent(child);
	}
	
	return child == parent;
};

/**
 * Function: contains
 * 
 * Returns true if the model contains the given <mxCell>.
 *
 * Parameters:
 * 
 * cell - <mxCell> that specifies the cell.
 *
 * // 中文注释：
 * // 函数：contains
 * // 判断模型是否包含指定的单元。
 * // 参数：
 * // cell - 指定单元的 mxCell 对象。
 * // 返回值：如果模型包含该单元，返回 true。
 */
mxGraphModel.prototype.contains = function(cell)
{
	return this.isAncestor(this.root, cell);
};

/**
 * Function: getParent
 * 
 * Returns the parent of the given cell.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose parent should be returned.
 *
 * // 中文注释：
 * // 函数：getParent
 * // 返回指定单元的父节点。
 * // 参数：
 * // cell - 需要返回父节点的 mxCell 对象。
 * // 返回值：父节点，若单元为空则返回 null。
 */
mxGraphModel.prototype.getParent = function(cell)
{
	return (cell != null) ? cell.getParent() : null;
};

/**
 * Function: add
 * 
 * Adds the specified child to the parent at the given index using
 * <mxChildChange> and adds the change to the current transaction. If no
 * index is specified then the child is appended to the parent's array of
 * children. Returns the inserted child.
 * 
 * Parameters:
 * 
 * parent - <mxCell> that specifies the parent to contain the child.
 * child - <mxCell> that specifies the child to be inserted.
 * index - Optional integer that specifies the index of the child.
 *
 * // 中文注释：
 * // 函数：add
 * // 将指定子节点添加到父节点的指定索引位置，使用 mxChildChange 并将更改添加到当前事务。
 * // 如果未指定索引，则将子节点追加到父节点的子节点数组末尾。
 * // 参数：
 * // parent - 指定父节点的 mxCell 对象。
 * // child - 指定要插入的子节点的 mxCell 对象。
 * // index - 可选整数，指定子节点的插入索引。
 * // 返回值：插入的子节点。
 * // 注意事项：如果 maintainEdgeParent 为 true 且父节点发生变化，会更新边的父节点。
 */
mxGraphModel.prototype.add = function(parent, child, index)
{
	if (child != parent && parent != null && child != null)
	{	
		// Appends the child if no index was specified
		if (index == null)
		{
			index = this.getChildCount(parent);
		}
		
		var parentChanged = parent != this.getParent(child);
		this.execute(new mxChildChange(this, parent, child, index));

		// Maintains the edges parents by moving the edges
		// into the nearest common ancestor of its terminals
		if (this.maintainEdgeParent && parentChanged)
		{
			this.updateEdgeParents(child);
		}
	}
	
	return child;
};

/**
 * Function: cellAdded
 * 
 * Inner callback to update <cells> when a cell has been added. This
 * implementation resolves collisions by creating new Ids. To change the
 * ID of a cell after it was inserted into the model, use the following
 * code:
 * 
 * (code
 * delete model.cells[cell.getId()];
 * cell.setId(newId);
 * model.cells[cell.getId()] = cell;
 * (end)
 *
 * If the change of the ID should be part of the command history, then the
 * cell should be removed from the model and a clone with the new ID should
 * be reinserted into the model instead.
 *
 * Parameters:
 * 
 * cell - <mxCell> that specifies the cell that has been added.
 *
 * // 中文注释：
 * // 函数：cellAdded
 * // 内部回调函数，用于在添加单元时更新 cells 映射。
 * // 实现通过创建新 ID 解决 ID 冲突。
 * // 参数：
 * // cell - 指定已添加的单元的 mxCell 对象。
 * // 注意事项：
 * // 如果单元没有 ID 且 createIds 为 true，则自动生成 ID。
 * // 如果 ID 冲突，会生成新 ID 直到无冲突。
 * // 递归处理子节点，确保所有子节点的 ID 也被添加到 cells 映射中。
 */
mxGraphModel.prototype.cellAdded = function(cell)
{
	if (cell != null)
	{
		// Creates an Id for the cell if not Id exists
		if (cell.getId() == null && this.createIds)
		{
			cell.setId(this.createId(cell));
		}
		
		if (cell.getId() != null)
		{
			var collision = this.getCell(cell.getId());
			
			if (collision != cell)
			{	
				// Creates new Id for the cell
				// as long as there is a collision
				while (collision != null)
				{
					cell.setId(this.createId(cell));
					collision = this.getCell(cell.getId());
				}
				
				// Lazily creates the cells dictionary
				if (this.cells == null)
				{
					this.cells = new Object();
				}
				
				this.cells[cell.getId()] = cell;
			}
		}
		
		// Makes sure IDs of deleted cells are not reused
		if (mxUtils.isNumeric(cell.getId()))
		{
			this.nextId = Math.max(this.nextId, cell.getId());
		}
		
		// Recursively processes child cells
		var childCount = this.getChildCount(cell);
		
		for (var i=0; i<childCount; i++)
		{
			this.cellAdded(this.getChildAt(cell, i));
		}
	}
};

/**
 * Function: createId
 * 
 * Hook method to create an Id for the specified cell. This implementation
 * concatenates <prefix>, id and <postfix> to create the Id and increments
 * <nextId>. The cell is ignored by this implementation, but can be used in
 * overridden methods to prefix the Ids with eg. the cell type.
 *
 * Parameters:
 *
 * cell - <mxCell> to create the Id for.
 *
 * // 中文注释：
 * // 函数：createId
 * // 为指定单元创建 ID 的钩子方法。
 * // 通过拼接 prefix、id 和 postfix 创建 ID，并递增 nextId。
 * // 参数：
 * // cell - 需要创建 ID 的 mxCell 对象（当前实现忽略此参数）。
 * // 返回值：新生成的 ID 字符串。
 * // 注意事项：可通过重写此方法为 ID 添加前缀（如单元类型）。
 */
mxGraphModel.prototype.createId = function(cell)
{
	var id = this.nextId;
	this.nextId++;
	
	return this.prefix + id + this.postfix;
};

/**
 * Function: updateEdgeParents
 * 
 * Updates the parent for all edges that are connected to cell or one of
 * its descendants using <updateEdgeParent>.
 *
 * // 中文注释：
 * // 函数：updateEdgeParents
 * // 更新与指定单元或其后代连接的所有边的父节点，使用 updateEdgeParent 方法。
 * // 参数：
 * // cell - 需要更新边父节点的 mxCell 对象。
 * // root - 可选参数，表示模型的当前根节点，默认为 getRoot(cell) 的结果。
 */
mxGraphModel.prototype.updateEdgeParents = function(cell, root)
{
	// Gets the topmost node of the hierarchy
	root = root || this.getRoot(cell);
	
	// Updates edges on children first
	var childCount = this.getChildCount(cell);
	
	for (var i = 0; i < childCount; i++)
	{
		var child = this.getChildAt(cell, i);
		this.updateEdgeParents(child, root);
	}
	
	// Updates the parents of all connected edges
	var edgeCount = this.getEdgeCount(cell);
	var edges = [];

	for (var i = 0; i < edgeCount; i++)
	{
		edges.push(this.getEdgeAt(cell, i));
	}
	
	for (var i = 0; i < edges.length; i++)
	{
		var edge = edges[i];
		
		// Updates edge parent if edge and child have
		// a common root node (does not need to be the
		// model root node)
		if (this.isAncestor(root, edge))
		{
			this.updateEdgeParent(edge, root);
		}
	}
};

/**
 * Function: updateEdgeParent
 *
 * Inner callback to update the parent of the specified <mxCell> to the
 * nearest-common-ancestor of its two terminals.
 *
 * Parameters:
 * 
 * edge - <mxCell> that specifies the edge.
 * root - <mxCell> that represents the current root of the model.
 *
 * // 中文注释：
 * // 函数：updateEdgeParent
 * // 内部回调函数，将指定边的父节点更新为其两个端点的最近公共祖先。
 * // 参数：
 * // edge - 指定边的 mxCell 对象。
 * // root - 表示模型当前根节点的 mxCell 对象。
 * // 注意事项：
 * // 如果 ignoreRelativeEdgeParent 为 true，则忽略相对几何位置的端点。
 * // 调整边的几何位置以适应新的父节点。
 */
mxGraphModel.prototype.updateEdgeParent = function(edge, root)
{
	var source = this.getTerminal(edge, true);
	var target = this.getTerminal(edge, false);
	var cell = null;
	
	// Uses the first non-relative descendants of the source terminal
	while (source != null && !this.isEdge(source) &&
		source.geometry != null && source.geometry.relative)
	{
		source = this.getParent(source);
	}
	
	// Uses the first non-relative descendants of the target terminal
	while (target != null && this.ignoreRelativeEdgeParent &&
		!this.isEdge(target) && target.geometry != null && 
		target.geometry.relative)
	{
		target = this.getParent(target);
	}
	
	if (this.isAncestor(root, source) && this.isAncestor(root, target))
	{
		if (source == target)
		{
			cell = this.getParent(source);
		}
		else
		{
			cell = this.getNearestCommonAncestor(source, target);
		}

		if (cell != null && (this.getParent(cell) != this.root ||
			this.isAncestor(cell, edge)) && this.getParent(edge) != cell)
		{
			var geo = this.getGeometry(edge);
			
			if (geo != null)
			{
				var origin1 = this.getOrigin(this.getParent(edge));
				var origin2 = this.getOrigin(cell);
				
				var dx = origin2.x - origin1.x;
				var dy = origin2.y - origin1.y;
				
				geo = geo.clone();
				geo.translate(-dx, -dy);
				this.setGeometry(edge, geo);
			}

			this.add(cell, edge, this.getChildCount(cell));
		}
	}
};

/**
 * Function: getOrigin
 * 
 * Returns the absolute, accumulated origin for the children inside the
 * given parent as an <mxPoint>.
 *
 * // 中文注释：
 * // 函数：getOrigin
 * // 返回指定父节点内子节点的绝对累积原点，作为 mxPoint 对象。
 * // 参数：
 * // cell - 指定父节点的 mxCell 对象。
 * // 返回值：表示累积原点的 mxPoint 对象。
 */
mxGraphModel.prototype.getOrigin = function(cell)
{
	var result = null;
	
	if (cell != null)
	{
		result = this.getOrigin(this.getParent(cell));
		
		if (!this.isEdge(cell))
		{
			var geo = this.getGeometry(cell);
			
			if (geo != null)
			{
				result.x += geo.x;
				result.y += geo.y;
			}
		}
	}
	else
	{
		result = new mxPoint();
	}
	
	return result;
};

/**
 * Function: getNearestCommonAncestor
 * 
 * Returns the nearest common ancestor for the specified cells.
 *
 * Parameters:
 * 
 * cell1 - <mxCell> that specifies the first cell in the tree.
 * cell2 - <mxCell> that specifies the second cell in the tree.
 *
 * // 中文注释：
 * // 函数：getNearestCommonAncestor
 * // 返回两个指定单元的最近公共祖先。
 * // 参数：
 * // cell1 - 树中的第一个 mxCell 对象。
 * // cell2 - 树中的第二个 mxCell 对象。
 * // 返回值：最近公共祖先的 mxCell 对象，若无则返回 null。
 */
mxGraphModel.prototype.getNearestCommonAncestor = function(cell1, cell2)
{
	if (cell1 != null && cell2 != null)
	{		
		// Creates the cell path for the second cell
		var path = mxCellPath.create(cell2);

		if (path != null && path.length > 0)
		{
			// Bubbles through the ancestors of the first
			// cell to find the nearest common ancestor.
			var cell = cell1;
			var current = mxCellPath.create(cell);
			
			// Inverts arguments
			if (path.length < current.length)
			{
				cell = cell2;
				var tmp = current;
				current = path;
				path = tmp;
			}
			
			while (cell != null)
			{
				var parent = this.getParent(cell);
				
				// Checks if the cell path is equal to the beginning of the given cell path
				if (path.indexOf(current + mxCellPath.PATH_SEPARATOR) == 0 && parent != null)
				{
					return cell;
				}
				
				current = mxCellPath.getParentPath(current);
				cell = parent;
			}
		}
	}
	
	return null;
};

/**
 * Function: remove
 * 
 * Removes the specified cell from the model using <mxChildChange> and adds
 * the change to the current transaction. This operation will remove the
 * cell and all of its children from the model. Returns the removed cell.
 *
 * Parameters:
 * 
 * cell - <mxCell> that should be removed.
 *
 * // 中文注释：
 * // 函数：remove
 * // 使用 mxChildChange 从模型中移除指定单元，并将更改添加到当前事务。
 * // 此操作会移除单元及其所有子节点。
 * // 参数：
 * // cell - 需要移除的 mxCell 对象。
 * // 返回值：被移除的单元。
 * // 注意事项：如果单元是根节点，则调用 setRoot(null)。
 */
mxGraphModel.prototype.remove = function(cell)
{
	if (cell == this.root)
	{
		this.setRoot(null);
	}
	else if (this.getParent(cell) != null)
	{
		this.execute(new mxChildChange(this, null, cell));
	}
	
	return cell;
};

/**
 * Function: cellRemoved
 * 
 * Inner callback to update <cells> when a cell has been removed.
 *
 * Parameters:
 * 
 * cell - <mxCell> that specifies the cell that has been removed.
 *
 * // 中文注释：
 * // 函数：cellRemoved
 * // 内部回调函数，用于在移除单元时更新 cells 映射。
 * // 参数：
 * // cell - 指定已移除的单元的 mxCell 对象。
 * // 注意事项：递归处理子节点，从 cells 映射中移除单元及其子节点的 ID。
 */
mxGraphModel.prototype.cellRemoved = function(cell)
{
	if (cell != null && this.cells != null)
	{
		// Recursively processes child cells
		var childCount = this.getChildCount(cell);
		
		for (var i = childCount - 1; i >= 0; i--)
		{
			this.cellRemoved(this.getChildAt(cell, i));
		}
		
		// Removes the dictionary entry for the cell
		if (this.cells != null && cell.getId() != null)
		{
			delete this.cells[cell.getId()];
		}
	}
};

/**
 * Function: parentForCellChanged
 * 
 * Inner callback to update the parent of a cell using <mxCell.insert>
 * on the parent and return the previous parent.
 *
 * Parameters:
 * 
 * cell - <mxCell> to update the parent for.
 * parent - <mxCell> that specifies the new parent of the cell.
 * index - Optional integer that defines the index of the child
 * in the parent's child array.
 *
 * // 中文注释：
 * // 函数：parentForCellChanged
 * // 内部回调函数，使用 mxCell.insert 更新单元的父节点并返回之前的父节点。
 * // 参数：
 * // cell - 需要更新父节点的 mxCell 对象。
 * // parent - 指定新父节点的 mxCell 对象。
 * // index - 可选整数，指定子节点在父节点子数组中的索引。
 * // 返回值：之前的父节点。
 */
mxGraphModel.prototype.parentForCellChanged = function(cell, parent, index)
{
	var previous = this.getParent(cell);
	
	if (parent != null)
	{
		if (parent != previous || previous.getIndex(cell) != index)
		{
			parent.insert(cell, index);
		}
	}
	else if (previous != null)
	{
		var oldIndex = previous.getIndex(cell);
		previous.remove(oldIndex);
	}
	
	// Adds or removes the cell from the model
	var par = this.contains(parent);
	var pre = this.contains(previous);
	
	if (par && !pre)
	{
		this.cellAdded(cell);
	}
	else if (pre && !par)
	{
		this.cellRemoved(cell);
	}
	
	return previous;
};

/**
 * Function: getChildCount
 *
 * Returns the number of children in the given cell.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose number of children should be returned.
 *
 * // 中文注释：
 * // 函数：getChildCount
 * // 返回指定单元的子节点数量。
 * // 参数：
 * // cell - 需要返回子节点数量的 mxCell 对象。
 * // 返回值：子节点数量，若单元为空则返回 0。
 */
mxGraphModel.prototype.getChildCount = function(cell)
{
	return (cell != null) ? cell.getChildCount() : 0;
};

/**
 * Function: getChildAt
 *
 * Returns the child of the given <mxCell> at the given index.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that represents the parent.
 * index - Integer that specifies the index of the child to be returned.
 *
 * // 中文注释：
 * // 函数：getChildAt
 * // 返回指定父节点在给定索引处的子节点。
 * // 参数：
 * // cell - 表示父节点的 mxCell 对象。
 * // index - 指定要返回的子节点的索引。
 * // 返回值：指定索引处的子节点，若单元为空则返回 null。
 */
mxGraphModel.prototype.getChildAt = function(cell, index)
{
	return (cell != null) ? cell.getChildAt(index) : null;
};

/**
 * Function: getChildren
 * 
 * Returns all children of the given <mxCell> as an array of <mxCells>. The
 * return value should be only be read.
 *
 * Parameters:
 * 
 * cell - <mxCell> the represents the parent.
 *
 * // 中文注释：
 * // 函数：getChildren
 * // 返回指定父节点的所有子节点作为 mxCell 数组。
 * // 参数：
 * // cell - 表示父节点的 mxCell 对象。
 * // 返回值：子节点数组，仅限读取。
 */
mxGraphModel.prototype.getChildren = function(cell)
{
	return (cell != null) ? cell.children : null;
};
	
/**
 * Function: getChildVertices
 * 
 * Returns the child vertices of the given parent.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose child vertices should be returned.
 *
 * // 中文注释：
 * // 函数：getChildVertices
 * // 返回指定父节点的子顶点。
 * // 参数：
 * // cell - 需要返回子顶点的 mxCell 对象。
 * // 返回值：子顶点数组。
 */
mxGraphModel.prototype.getChildVertices = function(parent)
{
	return this.getChildCells(parent, true, false);
};
		
/**
 * Function: getChildEdges
 * 
 * Returns the child edges of the given parent.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose child edges should be returned.
 *
 * // 中文注释：
 * // 函数：getChildEdges
 * // 返回指定父节点的子边。
 * // 参数：
 * // cell - 需要返回子边的 mxCell 对象。
 * // 返回值：子边数组。
 */
mxGraphModel.prototype.getChildEdges = function(parent)
{
	return this.getChildCells(parent, false, true);
};

/**
 * Function: getChildCells
 * 
 * Returns the children of the given cell that are vertices and/or edges
 * depending on the arguments.
 *
 * Parameters:
 * 
 * cell - <mxCell> the represents the parent.
 * vertices - Boolean indicating if child vertices should be returned.
 * Default is false.
 * edges - Boolean indicating if child edges should be returned.
 * Default is false.
 *
 * // 中文注释：
 * // 函数：getChildCells
 * // 返回指定父节点的子节点，根据参数决定返回顶点和/或边。
 * // 参数：
 * // cell - 表示父节点的 mxCell 对象。
 * // vertices - 布尔值，指定是否返回子顶点，默认值为 false。
 * // edges - 布尔值，指定是否返回子边，默认值为 false。
 * // 返回值：满足条件的子节点数组。
 */
mxGraphModel.prototype.getChildCells = function(parent, vertices, edges)
{
	vertices = (vertices != null) ? vertices : false;
	edges = (edges != null) ? edges : false;
	
	var childCount = this.getChildCount(parent);
	var result = [];

	for (var i = 0; i < childCount; i++)
	{
		var child = this.getChildAt(parent, i);

		if ((!edges && !vertices) || (edges && this.isEdge(child)) ||
			(vertices && this.isVertex(child)))
		{
			result.push(child);
		}
	}

	return result;
};
		
/**
 * Function: getTerminal
 * 
 * Returns the source or target <mxCell> of the given edge depending on the
 * value of the boolean parameter.
 *
 * Parameters:
 * 
 * edge - <mxCell> that specifies the edge.
 * isSource - Boolean indicating which end of the edge should be returned.
 *
 * // 中文注释：
 * // 函数：getTerminal
 * // 返回指定边的源端点或目标端点，根据布尔参数决定。
 * // 参数：
 * // edge - 指定边的 mxCell 对象。
 * // isSource - 布尔值，指定返回源端点（true）还是目标端点（false）。
 * // 返回值：源端点或目标端点的 mxCell 对象，若边为空则返回 null。
 */
mxGraphModel.prototype.getTerminal = function(edge, isSource)
{
	return (edge != null) ? edge.getTerminal(isSource) : null;
};

/**
 * Function: setTerminal
 * 
 * Sets the source or target terminal of the given <mxCell> using
 * <mxTerminalChange> and adds the change to the current transaction.
 * This implementation updates the parent of the edge using <updateEdgeParent>
 * if required.
 *
 * Parameters:
 * 
 * edge - <mxCell> that specifies the edge.
 * terminal - <mxCell> that specifies the new terminal.
 * isSource - Boolean indicating if the terminal is the new source or
 * target terminal of the edge.
 *
 * // 中文注释：
 * // 函数：setTerminal
 * // 使用 mxTerminalChange 设置指定边的源端点或目标端点，并将更改添加到当前事务。
 * // 如果需要（maintainEdgeParent 为 true 且端点变化），更新边的父节点。
 * // 参数：
 * // edge - 指定边的 mxCell 对象。
 * // terminal - 指定新端点的 mxCell 对象。
 * // isSource - 布尔值，指定设置源端点（true）还是目标端点（false）。
 * // 返回值：新设置的端点。
 */
mxGraphModel.prototype.setTerminal = function(edge, terminal, isSource)
{
	var terminalChanged = terminal != this.getTerminal(edge, isSource);
	this.execute(new mxTerminalChange(this, edge, terminal, isSource));
	
	if (this.maintainEdgeParent && terminalChanged)
	{
		this.updateEdgeParent(edge, this.getRoot());
	}
	
	return terminal;
};
	
/**
 * Function: setTerminals
 * 
 * Sets the source and target <mxCell> of the given <mxCell> in a single
 * transaction using <setTerminal> for each end of the edge.
 *
 * Parameters:
 * 
 * edge - <mxCell> that specifies the edge.
 * source - <mxCell> that specifies the new source terminal.
 * target - <mxCell> that specifies the new target terminal.
 *
 * // 中文注释：
 * // 函数：setTerminals
 * // 在单一事务中，使用 setTerminal 设置指定边的源端点和目标端点。
 * // 参数：
 * // edge - 指定边的 mxCell 对象。
 * // source - 指定新源端点的 mxCell 对象。
 * // target - 指定新目标端点的 mxCell 对象。
 */
mxGraphModel.prototype.setTerminals = function(edge, source, target)
{
	this.beginUpdate();
	try
	{
		this.setTerminal(edge, source, true);
		this.setTerminal(edge, target, false);
	}
	finally
	{
		this.endUpdate();
	}
};

/**
 * Function: terminalForCellChanged
 * 
 * Inner helper function to update the terminal of the edge using
 * <mxCell.insertEdge> and return the previous terminal.
 * 
 * Parameters:
 * 
 * edge - <mxCell> that specifies the edge to be updated.
 * terminal - <mxCell> that specifies the new terminal.
 * isSource - Boolean indicating if the terminal is the new source or
 * target terminal of the edge.
 *
 * // 中文注释：
 * // 函数：terminalForCellChanged
 * // 内部辅助函数，使用 mxCell.insertEdge 更新边的端点并返回之前的端点。
 * // 参数：
 * // edge - 指定要更新的边的 mxCell 对象。
 * // terminal - 指定新端点的 mxCell 对象。
 * // isSource - 布尔值，指定更新源端点（true）还是目标端点（false）。
 * // 返回值：之前的端点。
 */
mxGraphModel.prototype.terminalForCellChanged = function(edge, terminal, isSource)
{
	var previous = this.getTerminal(edge, isSource);
	
	if (terminal != null)
	{
		terminal.insertEdge(edge, isSource);
	}
	else if (previous != null)
	{
		previous.removeEdge(edge, isSource);
	}
	
	return previous;
};

/**
 * Function: getEdgeCount
 * 
 * Returns the number of distinct edges connected to the given cell.
 *
 * Parameters:
 * 
 * cell - <mxCell> that represents the vertex.
 *
 * // 中文注释：
 * // 函数：getEdgeCount
 * // 返回连接到指定单元的唯一边的数量。
 * // 参数：
 * // cell - 表示顶点的 mxCell 对象。
 * // 返回值：边的数量，若单元为空则返回 0。
 */
mxGraphModel.prototype.getEdgeCount = function(cell)
{
	return (cell != null) ? cell.getEdgeCount() : 0;
};

/**
 * Function: getEdgeAt
 * 
 * Returns the edge of cell at the given index.
 *
 * Parameters:
 * 
 * cell - <mxCell> that specifies the vertex.
 * index - Integer that specifies the index of the edge
 * to return.
 *
 * // 中文注释：
 * // 函数：getEdgeAt
 * // 返回指定单元在给定索引处的边。
 * // 参数：
 * // cell - 指定顶点的 mxCell 对象。
 * // index - 指定要返回的边的索引。
 * // 返回值：指定索引处的边，若单元为空则返回 null。
 */
mxGraphModel.prototype.getEdgeAt = function(cell, index)
{
	return (cell != null) ? cell.getEdgeAt(index) : null;
};
	
/**
 * Function: getDirectedEdgeCount
 * 
 * Returns the number of incoming or outgoing edges, ignoring the given
 * edge.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose edge count should be returned.
 * outgoing - Boolean that specifies if the number of outgoing or
 * incoming edges should be returned.
 * ignoredEdge - <mxCell> that represents an edge to be ignored.
 *
 * // 中文注释：
 * // 函数：getDirectedEdgeCount
 * // 返回指定单元的入边或出边数量，忽略指定的边。
 * // 参数：
 * // cell - 需要返回边数量的 mxCell 对象。
 * // outgoing - 布尔值，指定返回出边（true）还是入边（false）的数量。
 * // ignoredEdge - 需要忽略的边的 mxCell 对象。
 * // 返回值：入边或出边的数量。
 */
mxGraphModel.prototype.getDirectedEdgeCount = function(cell, outgoing, ignoredEdge)
{
	var count = 0;
	var edgeCount = this.getEdgeCount(cell);

	for (var i = 0; i < edgeCount; i++)
	{
		var edge = this.getEdgeAt(cell, i);

		if (edge != ignoredEdge && this.getTerminal(edge, outgoing) == cell)
		{
			count++;
		}
	}

	return count;
};

/**
 * Function: getConnections
 * 
 * Returns all edges of the given cell without loops.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose edges should be returned.
 * 
 * // 中文注释：
 * // 函数：getConnections
 * // 返回指定单元的所有边，不包括循环边。
 * // 参数：
 * // cell - 需要返回边的 mxCell 对象。
 * // 返回值：不含循环边的边数组。
 */
mxGraphModel.prototype.getConnections = function(cell)
{
	return this.getEdges(cell, true, true, false);
};

/**
 * Function: getIncomingEdges
 * 
 * Returns the incoming edges of the given cell without loops.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose incoming edges should be returned.
 * 
 * // 中文注释：
 * // 函数：getIncomingEdges
 * // 返回指定单元的入边，不包括循环边。
 * // 参数：
 * // cell - 需要返回入边的 mxCell 对象。
 * // 返回值：入边数组。
 */
mxGraphModel.prototype.getIncomingEdges = function(cell)
{
	return this.getEdges(cell, true, false, false);
};

/**
 * Function: getOutgoingEdges
 * 
 * Returns the outgoing edges of the given cell without loops.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose outgoing edges should be returned.
 * 
 * // 中文注释：
 * // 函数：getOutgoingEdges
 * // 返回指定单元的出边，不包括循环边。
 * // 参数：
 * // cell - 需要返回出边的 mxCell 对象。
 * // 返回值：出边数组。
 */
mxGraphModel.prototype.getOutgoingEdges = function(cell)
{
	return this.getEdges(cell, false, true, false);
};

/**
 * Function: getEdges
 * 
 * Returns all distinct edges connected to this cell as a new array of
 * <mxCells>. If at least one of incoming or outgoing is true, then loops
 * are ignored, otherwise if both are false, then all edges connected to
 * the given cell are returned including loops.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that specifies the cell.
 * incoming - Optional boolean that specifies if incoming edges should be
 * returned. Default is true.
 * outgoing - Optional boolean that specifies if outgoing edges should be
 * returned. Default is true.
 * includeLoops - Optional boolean that specifies if loops should be returned.
 * Default is true. 
 *
 * // 中文注释：
 * // 函数：getEdges
 * // 返回连接到指定单元的所有唯一边作为 mxCell 数组。
 * // 如果 incoming 或 outgoing 至少一个为 true，则忽略循环边；否则若两者均为 false，则返回包括循环边在内的所有边。
 * // 参数：
 * // cell - 指定单元的 mxCell 对象。
 * // incoming - 可选布尔值，指定是否返回入边，默认值为 true。
 * // outgoing - 可选布尔值，指定是否返回出边，默认值为 true。
 * // includeLoops - 可选布尔值，指定是否返回循环边，默认值为 true。
 * // 返回值：连接到指定单元的边数组。
 */
mxGraphModel.prototype.getEdges = function(cell, incoming, outgoing, includeLoops)
{
	incoming = (incoming != null) ? incoming : true;
	outgoing = (outgoing != null) ? outgoing : true;
	includeLoops = (includeLoops != null) ? includeLoops : true;
	
	var edgeCount = this.getEdgeCount(cell);
	var result = [];

	for (var i = 0; i < edgeCount; i++)
	{
		var edge = this.getEdgeAt(cell, i);
		var source = this.getTerminal(edge, true);
		var target = this.getTerminal(edge, false);

		if ((includeLoops && source == target) || ((source != target) && ((incoming && target == cell) ||
			(outgoing && source == cell))))
		{
			result.push(edge);
		}
	}

	return result;
};

/**
 * Function: getEdgesBetween
 * 
 * Returns all edges between the given source and target pair. If directed
 * is true, then only edges from the source to the target are returned,
 * otherwise, all edges between the two cells are returned.
 * 
 * Parameters:
 * 
 * source - <mxCell> that defines the source terminal of the edge to be
 * returned.
 * target - <mxCell> that defines the target terminal of the edge to be
 * returned.
 * directed - Optional boolean that specifies if the direction of the
 * edge should be taken into account. Default is false.
 *
 * // 中文注释：
 * // 函数：getEdgesBetween
 * // 返回指定源端点和目标端点之间的所有边。
 * // 如果 directed 为 true，则只返回从源到目标的边；否则返回两者之间的所有边。
 * // 参数：
 * // source - 定义边源端点的 mxCell 对象。
 * // target - 定义边目标端点的 mxCell 对象。
 * // directed - 可选布尔值，指定是否考虑边的方向，默认值为 false。
 * // 返回值：源端点和目标端点之间的边数组。
 */
mxGraphModel.prototype.getEdgesBetween = function(source, target, directed)
{
	directed = (directed != null) ? directed : false;
	
	var tmp1 = this.getEdgeCount(source);
	var tmp2 = this.getEdgeCount(target);
	
	// Assumes the source has less connected edges
	var terminal = source;
	var edgeCount = tmp1;
	
	// Uses the smaller array of connected edges
	// for searching the edge
	if (tmp2 < tmp1)
	{
		edgeCount = tmp2;
		terminal = target;
	}
	
	var result = [];
	
	// Checks if the edge is connected to the correct
	// cell and returns the first match
	for (var i = 0; i < edgeCount; i++)
	{
		var edge = this.getEdgeAt(terminal, i);
		var src = this.getTerminal(edge, true);
		var trg = this.getTerminal(edge, false);
		var directedMatch = (src == source) && (trg == target);
		var oppositeMatch = (trg == source) && (src == target);

		if (directedMatch || (!directed && oppositeMatch))
		{
			result.push(edge);
		}
	}
	
	return result;
};

/**
 * Function: getOpposites
 * 
 * Returns all opposite vertices wrt terminal for the given edges, only
 * returning sources and/or targets as specified. The result is returned
 * as an array of <mxCells>.
 * 
 * Parameters:
 * 
 * edges - Array of <mxCells> that contain the edges to be examined.
 * terminal - <mxCell> that specifies the known end of the edges.
 * sources - Boolean that specifies if source terminals should be contained
 * in the result. Default is true.
 * targets - Boolean that specifies if target terminals should be contained
 * in the result. Default is true.
 *
 * // 中文注释：
 * // 函数：getOpposites
 * // 返回给定边相对于指定端点的所有对端顶点，仅返回指定的源端点和/或目标端点。
 * // 参数：
 * // edges - 包含待检查边的 mxCell 数组。
 * // terminal - 指定已知端点的 mxCell 对象。
 * // sources - 布尔值，指定结果是否包含源端点，默认值为 true。
 * // targets - 布尔值，指定结果是否包含目标端点，默认值为 true。
 * // 返回值：对端顶点的 mxCell 数组。
 */
mxGraphModel.prototype.getOpposites = function(edges, terminal, sources, targets)
{
	sources = (sources != null) ? sources : true;
	targets = (targets != null) ? targets : true;
	
	var terminals = [];
	
	if (edges != null)
	{
		for (var i = 0; i < edges.length; i++)
		{
			var source = this.getTerminal(edges[i], true);
			var target = this.getTerminal(edges[i], false);
			
			// Checks if the terminal is the source of
			// the edge and if the target should be
			// stored in the result
			if (source == terminal && target != null && target != terminal && targets)
			{
				terminals.push(target);
			}
			
			// Checks if the terminal is the taget of
			// the edge and if the source should be
			// stored in the result
			else if (target == terminal && source != null && source != terminal && sources)
			{
				terminals.push(source);
			}
		}
	}
	
	return terminals;
};

/**
 * Function: getTopmostCells
 * 
 * Returns the topmost cells of the hierarchy in an array that contains no
 * descendants for each <mxCell> that it contains. Duplicates should be
 * removed in the cells array to improve performance.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> whose topmost ancestors should be returned.
 *
 * // 中文注释：
 * // 函数：getTopmostCells
 * // 返回层级中最顶级的单元数组，不包含任何后代。
 * // 去除数组中的重复单元以提高性能。
 * // 参数：
 * // cells - 需要返回最顶级祖先的 mxCell 数组。
 * // 返回值：最顶级单元的数组。
 */
mxGraphModel.prototype.getTopmostCells = function(cells)
{
	var dict = new mxDictionary();
	var tmp = [];
	
	for (var i = 0; i < cells.length; i++)
	{
		dict.put(cells[i], true);
	}
	
	for (var i = 0; i < cells.length; i++)
	{
		var cell = cells[i];
		var topmost = true;
		var parent = this.getParent(cell);
		
		while (parent != null)
		{
			if (dict.get(parent))
			{
				topmost = false;
				break;
			}
			
			parent = this.getParent(parent);
		}
		
		if (topmost)
		{
			tmp.push(cell);
		}
	}
	
	return tmp;
};

/**
 * Function: isVertex
 * 
 * Returns true if the given cell is a vertex.
 *
 * Parameters:
 * 
 * cell - <mxCell> that represents the possible vertex.
 *
 * // 中文注释：
 * // 函数：isVertex
 * // 判断给定单元是否为顶点。
 * // 参数：
 * // cell - 表示可能顶点的 mxCell 对象。
 * // 返回值：如果单元是顶点，返回 true。
 */
mxGraphModel.prototype.isVertex = function(cell)
{
	return (cell != null) ? cell.isVertex() : false;
};

/**
 * Function: isEdge
 * 
 * Returns true if the given cell is an edge.
 *
 * Parameters:
 * 
 * cell - <mxCell> that represents the possible edge.
 *
 * // 中文注释：
 * // 函数：isEdge
 * // 判断给定单元是否为边。
 * // 参数：
 * // cell - 表示可能边的 mxCell 对象。
 * // 返回值：如果单元是边，返回 true。
 */
mxGraphModel.prototype.isEdge = function(cell)
{
	return (cell != null) ? cell.isEdge() : false;
};

/**
 * Function: isConnectable
 * 
 * Returns true if the given <mxCell> is connectable. If <edgesConnectable>
 * is false, then this function returns false for all edges else it returns
 * the return value of <mxCell.isConnectable>.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose connectable state should be returned.
 *
 * // 中文注释：
 * // 函数：isConnectable
 * // 判断给定单元是否可连接。
 * // 如果 edgesConnectable 为 false，则所有边返回 false；否则返回 mxCell.isConnectable 的值。
 * // 参数：
 * // cell - 需要返回连接状态的 mxCell 对象。
 * // 返回值：如果单元可连接，返回 true。
 */
mxGraphModel.prototype.isConnectable = function(cell)
{
	return (cell != null) ? cell.isConnectable() : false;
};

/**
 * Function: getValue
 * 
 * Returns the user object of the given <mxCell> using <mxCell.getValue>.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose user object should be returned.
 *
 * // 中文注释：
 * // 函数：getValue
 * // 使用 mxCell.getValue 返回指定单元的用户对象。
 * // 参数：
 * // cell - 需要返回用户对象的 mxCell 对象。
 * // 返回值：用户对象，若单元为空则返回 null。
 */
mxGraphModel.prototype.getValue = function(cell)
{
	return (cell != null) ? cell.getValue() : null;
};

/**
 * Function: setValue
 * 
 * Sets the user object of then given <mxCell> using <mxValueChange>
 * and adds the change to the current transaction.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose user object should be changed.
 * value - Object that defines the new user object.
 *
 * // 中文注释：
 * // 函数：setValue
 * // 使用 mxValueChange 设置指定单元的用户对象，并将更改添加到当前事务。
 * // 参数：
 * // cell - 需要更改用户对象的 mxCell 对象。
 * // value - 定义新用户对象的对象。
 * // 返回值：新设置的用户对象。
 */
mxGraphModel.prototype.setValue = function(cell, value)
{
	this.execute(new mxValueChange(this, cell, value));
	
	return value;
};

/**
 * Function: valueForCellChanged
 * 
 * Inner callback to update the user object of the given <mxCell>
 * using <mxCell.valueChanged> and return the previous value,
 * that is, the return value of <mxCell.valueChanged>.
 * 
 * To change a specific attribute in an XML node, the following code can be
 * used.
 * 
 * (code)
 * graph.getModel().valueForCellChanged = function(cell, value)
 * {
 *   var previous = cell.value.getAttribute('label');
 *   cell.value.setAttribute('label', value);
 *   
 *   return previous;
 * };
 * (end) 
 *
 * // 中文注释：
 * // 函数：valueForCellChanged
 * // 内部回调函数，使用 mxCell.valueChanged 更新指定单元的用户对象并返回之前的值。
 * // 参数：
 * // cell - 需要更新用户对象的 mxCell 对象。
 * // value - 新用户对象的值。
 * // 返回值：mxCell.valueChanged 返回的之前值。
 * // 示例：修改 XML 节点中的特定属性。
 */
mxGraphModel.prototype.valueForCellChanged = function(cell, value)
{
	return cell.valueChanged(value);
};

/**
 * Function: getGeometry
 * 
 * Returns the <mxGeometry> of the given <mxCell>.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose geometry should be returned.
 *
 * // 中文注释：
 * // 函数：getGeometry
 * // 返回指定单元的 mxGeometry 对象。
 * // 参数：
 * // cell - 需要返回几何信息的 mxCell 对象。
 * // 返回值：单元的几何信息，若单元为空则返回 null。
 */
mxGraphModel.prototype.getGeometry = function(cell)
{
	return (cell != null) ? cell.getGeometry() : null;
};

/**
 * Function: setGeometry
 * 
 * Sets the <mxGeometry> of the given <mxCell>. The actual update
 * of the cell is carried out in <geometryForCellChanged>. The
 * <mxGeometryChange> action is used to encapsulate the change.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose geometry should be changed.
 * geometry - <mxGeometry> that defines the new geometry.
 *
 * // 中文注释：
 * // 函数：setGeometry
 * // 设置指定单元的 mxGeometry 对象，实际更新在 geometryForCellChanged 中执行。
 * // 使用 mxGeometryChange 封装更改。
 * // 参数：
 * // cell - 需要更改几何信息的 mxCell 对象。
 * // geometry - 定义新几何信息的 mxGeometry 对象。
 * // 返回值：新设置的几何信息。
 */
mxGraphModel.prototype.setGeometry = function(cell, geometry)
{
	if (geometry != this.getGeometry(cell))
	{
		this.execute(new mxGeometryChange(this, cell, geometry));
	}
	
	return geometry;
};

/**
 * Function: geometryForCellChanged
 * 
 * Inner callback to update the <mxGeometry> of the given <mxCell> using
 * <mxCell.setGeometry> and return the previous <mxGeometry>.
 *
 * // 中文注释：
 * // 函数：geometryForCellChanged
 * // 内部回调函数，使用 mxCell.setGeometry 更新指定单元的几何信息并返回之前的几何信息。
 * // 参数：
 * // cell - 需要更新几何信息的 mxCell 对象。
 * // geometry - 新几何信息的 mxGeometry 对象。
 * // 返回值：之前的几何信息。
 */
mxGraphModel.prototype.geometryForCellChanged = function(cell, geometry)
{
	var previous = this.getGeometry(cell);
	cell.setGeometry(geometry);
	
	return previous;
};

/**
 * Function: getStyle
 * 
 * Returns the style of the given <mxCell>.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose style should be returned.
 *
 * // 中文注释：
 * // 函数：getStyle
 * // 返回指定单元的样式。
 * // 参数：
 * // cell - 需要返回样式的 mxCell 对象。
 * // 返回值：单元的样式字符串，若单元为空则返回 null。
 */
mxGraphModel.prototype.getStyle = function(cell)
{
	return (cell != null) ? cell.getStyle() : null;
};

/**
 * Function: setStyle
 * 
 * Sets the style of the given <mxCell> using <mxStyleChange> and
 * adds the change to the current transaction.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose style should be changed.
 * style - String of the form [stylename;|key=value;] to specify
 * the new cell style.
 *
 * // 中文注释：
 * // 函数：setStyle
 * // 使用 mxStyleChange 设置指定单元的样式，并将更改添加到当前事务。
 * // 参数：
 * // cell - 需要更改样式的 mxCell 对象。
 * // style - 指定新单元样式的字符串，格式为 [stylename;|key=value;]。
 * // 返回值：新设置的样式字符串。
 * // 样式说明：样式字符串以键值对形式定义单元的显示属性。
 */
mxGraphModel.prototype.setStyle = function(cell, style)
{
	if (style != this.getStyle(cell))
	{
		this.execute(new mxStyleChange(this, cell, style));
	}
	
	return style;
};

/**
 * Function: styleForCellChanged
 * 
 * Inner callback to update the style of the given <mxCell>
 * using <mxCell.setStyle> and return the previous style.
 *
 * Parameters:
 * 
 * cell - <mxCell> that specifies the cell to be updated.
 * style - String of the form [stylename;|key=value;] to specify
 * the new cell style.
 */
mxGraphModel.prototype.styleForCellChanged = function(cell, style)
{
	var previous = this.getStyle(cell);
	cell.setStyle(style);
	
	return previous;
};

/**
 * Function: isCollapsed
 * 
 * Returns true if the given <mxCell> is collapsed.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose collapsed state should be returned.
 */
mxGraphModel.prototype.isCollapsed = function(cell)
{
	return (cell != null) ? cell.isCollapsed() : false;
};

/**
 * Function: setCollapsed
 * 
 * Sets the collapsed state of the given <mxCell> using <mxCollapseChange>
 * and adds the change to the current transaction.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose collapsed state should be changed.
 * collapsed - Boolean that specifies the new collpased state.
 */
mxGraphModel.prototype.setCollapsed = function(cell, collapsed)
{
	if (collapsed != this.isCollapsed(cell))
	{
		this.execute(new mxCollapseChange(this, cell, collapsed));
	}
	
	return collapsed;
};
	
/**
 * Function: collapsedStateForCellChanged
 *
 * Inner callback to update the collapsed state of the
 * given <mxCell> using <mxCell.setCollapsed> and return
 * the previous collapsed state.
 *
 * Parameters:
 * 
 * cell - <mxCell> that specifies the cell to be updated.
 * collapsed - Boolean that specifies the new collpased state.
 */
mxGraphModel.prototype.collapsedStateForCellChanged = function(cell, collapsed)
{
	var previous = this.isCollapsed(cell);
	cell.setCollapsed(collapsed);
	
	return previous;
};

/**
 * Function: isVisible
 * 
 * Returns true if the given <mxCell> is visible.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose visible state should be returned.
 */
mxGraphModel.prototype.isVisible = function(cell)
{
	return (cell != null) ? cell.isVisible() : false;
};

/**
 * Function: setVisible
 * 
 * Sets the visible state of the given <mxCell> using <mxVisibleChange> and
 * adds the change to the current transaction.
 *
 * Parameters:
 * 
 * cell - <mxCell> whose visible state should be changed.
 * visible - Boolean that specifies the new visible state.
 */
mxGraphModel.prototype.setVisible = function(cell, visible)
{
	if (visible != this.isVisible(cell))
	{
		this.execute(new mxVisibleChange(this, cell, visible));
	}
	
	return visible;
};
	
/**
 * Function: visibleStateForCellChanged
 *
 * Inner callback to update the visible state of the
 * given <mxCell> using <mxCell.setCollapsed> and return
 * the previous visible state.
 *
 * Parameters:
 * 
 * cell - <mxCell> that specifies the cell to be updated.
 * visible - Boolean that specifies the new visible state.
 */
mxGraphModel.prototype.visibleStateForCellChanged = function(cell, visible)
{
	var previous = this.isVisible(cell);
	cell.setVisible(visible);
	
	return previous;
};

/**
 * Function: execute
 * 
 * Executes the given edit and fires events if required. The edit object
 * requires an execute function which is invoked. The edit is added to the
 * <currentEdit> between <beginUpdate> and <endUpdate> calls, so that
 * events will be fired if this execute is an individual transaction, that
 * is, if no previous <beginUpdate> calls have been made without calling
 * <endUpdate>. This implementation fires an <execute> event before
 * executing the given change.
 * 
 * Parameters:
 * 
 * change - Object that described the change.
 */
mxGraphModel.prototype.execute = function(change)
{
	change.execute();
	this.beginUpdate();
	this.currentEdit.add(change);
	this.fireEvent(new mxEventObject(mxEvent.EXECUTE, 'change', change));
	// New global executed event
	this.fireEvent(new mxEventObject(mxEvent.EXECUTED, 'change', change));
	this.endUpdate();
};

/**
 * Function: beginUpdate
 * 
 * Increments the <updateLevel> by one. The event notification
 * is queued until <updateLevel> reaches 0 by use of
 * <endUpdate>.
 *
 * All changes on <mxGraphModel> are transactional,
 * that is, they are executed in a single undoable change
 * on the model (without transaction isolation).
 * Therefore, if you want to combine any
 * number of changes into a single undoable change,
 * you should group any two or more API calls that
 * modify the graph model between <beginUpdate>
 * and <endUpdate> calls as shown here:
 * 
 * (code)
 * var model = graph.getModel();
 * var parent = graph.getDefaultParent();
 * var index = model.getChildCount(parent);
 * model.beginUpdate();
 * try
 * {
 *   model.add(parent, v1, index);
 *   model.add(parent, v2, index+1);
 * }
 * finally
 * {
 *   model.endUpdate();
 * }
 * (end)
 * 
 * Of course there is a shortcut for appending a
 * sequence of cells into the default parent:
 * 
 * (code)
 * graph.addCells([v1, v2]).
 * (end)
 */
mxGraphModel.prototype.beginUpdate = function()
{
	this.updateLevel++;
	this.fireEvent(new mxEventObject(mxEvent.BEGIN_UPDATE));
	
	if (this.updateLevel == 1)
	{
		this.fireEvent(new mxEventObject(mxEvent.START_EDIT));
	}
};

/**
 * Function: endUpdate
 * 
 * Decrements the <updateLevel> by one and fires an <undo>
 * event if the <updateLevel> reaches 0. This function
 * indirectly fires a <change> event by invoking the notify
 * function on the <currentEdit> und then creates a new
 * <currentEdit> using <createUndoableEdit>.
 *
 * The <undo> event is fired only once per edit, whereas
 * the <change> event is fired whenever the notify
 * function is invoked, that is, on undo and redo of
 * the edit.
 */
mxGraphModel.prototype.endUpdate = function()
{
	this.updateLevel--;
	
	if (this.updateLevel == 0)
	{
		this.fireEvent(new mxEventObject(mxEvent.END_EDIT));
	}
	
	if (!this.endingUpdate)
	{
		this.endingUpdate = this.updateLevel == 0;
		this.fireEvent(new mxEventObject(mxEvent.END_UPDATE, 'edit', this.currentEdit));

		try
		{		
			if (this.endingUpdate && !this.currentEdit.isEmpty())
			{
				this.fireEvent(new mxEventObject(mxEvent.BEFORE_UNDO, 'edit', this.currentEdit));
				var tmp = this.currentEdit;
				this.currentEdit = this.createUndoableEdit();
				tmp.notify();
				this.fireEvent(new mxEventObject(mxEvent.UNDO, 'edit', tmp));
			}
		}
		finally
		{
			this.endingUpdate = false;
		}
	}
};

/**
 * Function: createUndoableEdit
 * 
 * Creates a new <mxUndoableEdit> that implements the
 * notify function to fire a <change> and <notify> event
 * through the <mxUndoableEdit>'s source.
 * 
 * Parameters:
 * 
 * significant - Optional boolean that specifies if the edit to be created is
 * significant. Default is true.
 */
mxGraphModel.prototype.createUndoableEdit = function(significant)
{
	var edit = new mxUndoableEdit(this, (significant != null) ? significant : true);
	
	edit.notify = function()
	{
		// LATER: Remove changes property (deprecated)
		edit.source.fireEvent(new mxEventObject(mxEvent.CHANGE,
			'edit', edit, 'changes', edit.changes));
		edit.source.fireEvent(new mxEventObject(mxEvent.NOTIFY,
			'edit', edit, 'changes', edit.changes));
	};
	
	return edit;
};

/**
 * Function: mergeChildren
 * 
 * Merges the children of the given cell into the given target cell inside
 * this model. All cells are cloned unless there is a corresponding cell in
 * the model with the same id, in which case the source cell is ignored and
 * all edges are connected to the corresponding cell in this model. Edges
 * are considered to have no identity and are always cloned unless the
 * cloneAllEdges flag is set to false, in which case edges with the same
 * id in the target model are reconnected to reflect the terminals of the
 * source edges.
 */
mxGraphModel.prototype.mergeChildren = function(from, to, cloneAllEdges)
{
	cloneAllEdges = (cloneAllEdges != null) ? cloneAllEdges : true;
	
	this.beginUpdate();
	try
	{
		var mapping = new Object();
		this.mergeChildrenImpl(from, to, cloneAllEdges, mapping);
		
		// Post-processes all edges in the mapping and
		// reconnects the terminals to the corresponding
		// cells in the target model
		for (var key in mapping)
		{
			var cell = mapping[key];
			var terminal = this.getTerminal(cell, true);

			if (terminal != null)
			{
				terminal = mapping[mxCellPath.create(terminal)];
				this.setTerminal(cell, terminal, true);
			}
			
			terminal = this.getTerminal(cell, false);
			
			if (terminal != null)
			{
				terminal = mapping[mxCellPath.create(terminal)];
				this.setTerminal(cell, terminal, false);
			}
		}
	}
	finally
	{
		this.endUpdate();
	}
};

/**
 * Function: mergeChildren
 * 
 * Clones the children of the source cell into the given target cell in
 * this model and adds an entry to the mapping that maps from the source
 * cell to the target cell with the same id or the clone of the source cell
 * that was inserted into this model.
 */
mxGraphModel.prototype.mergeChildrenImpl = function(from, to, cloneAllEdges, mapping)
{
	this.beginUpdate();
	try
	{
		var childCount = from.getChildCount();
		
		for (var i = 0; i < childCount; i++)
		{
			var cell = from.getChildAt(i);
			
			if (typeof(cell.getId) == 'function')
			{
				var id = cell.getId();
				var target = (id != null && (!this.isEdge(cell) || !cloneAllEdges)) ?
						this.getCell(id) : null;
				
				// Clones and adds the child if no cell exists for the id
				if (target == null)
				{
					var clone = cell.clone();
					clone.setId(id);
					
					// Sets the terminals from the original cell to the clone
					// because the lookup uses strings not cells in JS
					clone.setTerminal(cell.getTerminal(true), true);
					clone.setTerminal(cell.getTerminal(false), false);
					
					// Do *NOT* use model.add as this will move the edge away
					// from the parent in updateEdgeParent if maintainEdgeParent
					// is enabled in the target model
					target = to.insert(clone);
					this.cellAdded(target);
				}
				
				// Stores the mapping for later reconnecting edges
				mapping[mxCellPath.create(cell)] = target;
				
				// Recurses
				this.mergeChildrenImpl(cell, target, cloneAllEdges, mapping);
			}
		}
	}
	finally
	{
		this.endUpdate();
	}
};

/**
 * Function: getParents
 * 
 * Returns an array that represents the set (no duplicates) of all parents
 * for the given array of cells.
 * 
 * Parameters:
 * 
 * cells - Array of cells whose parents should be returned.
 */
mxGraphModel.prototype.getParents = function(cells)
{
	var parents = [];
	
	if (cells != null)
	{
		var dict = new mxDictionary();
		
		for (var i = 0; i < cells.length; i++)
		{
			var parent = this.getParent(cells[i]);
			
			if (parent != null && !dict.get(parent))
			{
				dict.put(parent, true);
				parents.push(parent);
			}
		}
	}
	
	return parents;
};

//
// Cell Cloning
//

/**
 * Function: cloneCell
 * 
 * Returns a deep clone of the given <mxCell> (including
 * the children) which is created using <cloneCells>.
 *
 * Parameters:
 * 
 * cell - <mxCell> to be cloned.
 * includeChildren - Optional boolean indicating if the cells should be cloned
 * with all descendants. Default is true.
 */
mxGraphModel.prototype.cloneCell = function(cell, includeChildren)
{
	if (cell != null)
	{
		return this.cloneCells([cell], includeChildren)[0];
	}
	
	return null;
};

/**
 * Function: cloneCells
 * 
 * Returns an array of clones for the given array of <mxCells>.
 * Depending on the value of includeChildren, a deep clone is created for
 * each cell. Connections are restored based if the corresponding
 * cell is contained in the passed in array.
 *
 * Parameters:
 * 
 * cells - Array of <mxCell> to be cloned.
 * includeChildren - Optional boolean indicating if the cells should be cloned
 * with all descendants. Default is true.
 * mapping - Optional mapping for existing clones.
 */
mxGraphModel.prototype.cloneCells = function(cells, includeChildren, mapping)
{
	includeChildren = (includeChildren != null) ? includeChildren : true;
	mapping = (mapping != null) ? mapping : new Object();
	var clones = [];
	
	for (var i = 0; i < cells.length; i++)
	{
		if (cells[i] != null)
		{
			clones.push(this.cloneCellImpl(cells[i], mapping, includeChildren));
		}
		else
		{
			clones.push(null);
		}
	}
	
	for (var i = 0; i < clones.length; i++)
	{
		if (clones[i] != null)
		{
			this.restoreClone(clones[i], cells[i], mapping);
		}
	}
	
	return clones;
};
			
/**
 * Function: cloneCellImpl
 * 
 * Inner helper method for cloning cells recursively.
 */
mxGraphModel.prototype.cloneCellImpl = function(cell, mapping, includeChildren)
{
	var ident = mxObjectIdentity.get(cell);
	var clone = mapping[ident];
	
	if (clone == null)
	{
		clone = this.cellCloned(cell);
		mapping[ident] = clone;

		if (includeChildren)
		{
			var childCount = this.getChildCount(cell);
			
			for (var i = 0; i < childCount; i++)
			{
				var cloneChild = this.cloneCellImpl(
					this.getChildAt(cell, i), mapping, true);
				clone.insert(cloneChild);
			}
		}
	}
	
	return clone;
};

/**
 * Function: cellCloned
 * 
 * Hook for cloning the cell. This returns cell.clone() or
 * any possible exceptions.
 */
mxGraphModel.prototype.cellCloned = function(cell)
{
	return cell.clone();
};

/**
 * Function: restoreClone
 * 
 * Inner helper method for restoring the connections in
 * a network of cloned cells.
 */
mxGraphModel.prototype.restoreClone = function(clone, cell, mapping)
{
	var source = this.getTerminal(cell, true);
	
	if (source != null)
	{
		var tmp = mapping[mxObjectIdentity.get(source)];
		
		if (tmp != null)
		{
			tmp.insertEdge(clone, true);
		}
	}
	
	var target = this.getTerminal(cell, false);
	
	if (target != null)
	{
		var tmp = mapping[mxObjectIdentity.get(target)];
		
		if (tmp != null)
		{	
			tmp.insertEdge(clone, false);
		}
	}
	
	var childCount = this.getChildCount(clone);
	
	for (var i = 0; i < childCount; i++)
	{
		this.restoreClone(this.getChildAt(clone, i),
			this.getChildAt(cell, i), mapping);
	}
};

//
// Atomic changes
//

/**
 * Class: mxRootChange
 * 
 * Action to change the root in a model.
 *
 * Constructor: mxRootChange
 * 
 * Constructs a change of the root in the
 * specified model.
 */
function mxRootChange(model, root)
{
	this.model = model;
	this.root = root;
	this.previous = root;
};

/**
 * Function: execute
 * 
 * Carries out a change of the root using
 * <mxGraphModel.rootChanged>.
 */
mxRootChange.prototype.execute = function()
{
	this.root = this.previous;
	this.previous = this.model.rootChanged(this.previous);
};

/**
 * Class: mxChildChange
 * 
 * Action to add or remove a child in a model.
 *
 * Constructor: mxChildChange
 * 
 * Constructs a change of a child in the
 * specified model.
 */
function mxChildChange(model, parent, child, index)
{
	this.model = model;
	this.parent = parent;
	this.previous = parent;
	this.child = child;
	this.index = index;
	this.previousIndex = index;
};

/**
 * Function: execute
 * 
 * Changes the parent of <child> using
 * <mxGraphModel.parentForCellChanged> and
 * removes or restores the cell's
 * connections.
 */
mxChildChange.prototype.execute = function()
{
	if (this.child != null)
	{
		var tmp = this.model.getParent(this.child);
		var tmp2 = (tmp != null) ? tmp.getIndex(this.child) : 0;
		
		if (this.previous == null)
		{
			this.connect(this.child, false);
		}
		
		tmp = this.model.parentForCellChanged(
			this.child, this.previous, this.previousIndex);
			
		if (this.previous != null)
		{
			this.connect(this.child, true);
		}
		
		this.parent = this.previous;
		this.previous = tmp;
		this.index = this.previousIndex;
		this.previousIndex = tmp2;
	}
};

/**
 * Function: disconnect
 * 
 * Disconnects the given cell recursively from its
 * terminals and stores the previous terminal in the
 * cell's terminals.
 */
mxChildChange.prototype.connect = function(cell, isConnect)
{
	isConnect = (isConnect != null) ? isConnect : true;
	
	var source = cell.getTerminal(true);
	var target = cell.getTerminal(false);
	
	if (source != null)
	{
		if (isConnect)
		{
			this.model.terminalForCellChanged(cell, source, true);
		}
		else
		{
			this.model.terminalForCellChanged(cell, null, true);
		}
	}
	
	if (target != null)
	{
		if (isConnect)
		{
			this.model.terminalForCellChanged(cell, target, false);
		}
		else
		{
			this.model.terminalForCellChanged(cell, null, false);
		}
	}
	
	cell.setTerminal(source, true);
	cell.setTerminal(target, false);
	
	var childCount = this.model.getChildCount(cell);
	
	for (var i=0; i<childCount; i++)
	{
		this.connect(this.model.getChildAt(cell, i), isConnect);
	}
};

/**
 * Class: mxTerminalChange
 * 
 * Action to change a terminal in a model.
 *
 * Constructor: mxTerminalChange
 * 
 * Constructs a change of a terminal in the 
 * specified model.
 */
function mxTerminalChange(model, cell, terminal, source)
{
	this.model = model;
	this.cell = cell;
	this.terminal = terminal;
	this.previous = terminal;
	this.source = source;
};

/**
 * Function: execute
 * 
 * Changes the terminal of <cell> to <previous> using
 * <mxGraphModel.terminalForCellChanged>.
 */
mxTerminalChange.prototype.execute = function()
{
	if (this.cell != null)
	{
		this.terminal = this.previous;
		this.previous = this.model.terminalForCellChanged(
			this.cell, this.previous, this.source);
	}
};

/**
 * Class: mxValueChange
 * 
 * Action to change a user object in a model.
 *
 * Constructor: mxValueChange
 * 
 * Constructs a change of a user object in the 
 * specified model.
 */
function mxValueChange(model, cell, value)
{
	this.model = model;
	this.cell = cell;
	this.value = value;
	this.previous = value;
};

/**
 * Function: execute
 * 
 * Changes the value of <cell> to <previous> using
 * <mxGraphModel.valueForCellChanged>.
 */
mxValueChange.prototype.execute = function()
{
	if (this.cell != null)
	{
		this.value = this.previous;
		this.previous = this.model.valueForCellChanged(
			this.cell, this.previous);
	}
};

/**
 * Class: mxStyleChange
 * 
 * Action to change a cell's style in a model.
 *
 * Constructor: mxStyleChange
 * 
 * Constructs a change of a style in the
 * specified model.
 */
function mxStyleChange(model, cell, style)
{
	this.model = model;
	this.cell = cell;
	this.style = style;
	this.previous = style;
};

/**
 * Function: execute
 * 
 * Changes the style of <cell> to <previous> using
 * <mxGraphModel.styleForCellChanged>.
 */
mxStyleChange.prototype.execute = function()
{
	if (this.cell != null)
	{
		this.style = this.previous;
		this.previous = this.model.styleForCellChanged(
			this.cell, this.previous);
	}
};

/**
 * Class: mxGeometryChange
 * 
 * Action to change a cell's geometry in a model.
 *
 * Constructor: mxGeometryChange
 * 
 * Constructs a change of a geometry in the
 * specified model.
 */
function mxGeometryChange(model, cell, geometry)
{
	this.model = model;
	this.cell = cell;
	this.geometry = geometry;
	this.previous = geometry;
};

/**
 * Function: execute
 * 
 * Changes the geometry of <cell> ro <previous> using
 * <mxGraphModel.geometryForCellChanged>.
 */
mxGeometryChange.prototype.execute = function()
{
	if (this.cell != null)
	{
		this.geometry = this.previous;
		this.previous = this.model.geometryForCellChanged(
			this.cell, this.previous);
	}
};

/**
 * Class: mxCollapseChange
 * 
 * Action to change a cell's collapsed state in a model.
 *
 * Constructor: mxCollapseChange
 * 
 * Constructs a change of a collapsed state in the
 * specified model.
 */
function mxCollapseChange(model, cell, collapsed)
{
	this.model = model;
	this.cell = cell;
	this.collapsed = collapsed;
	this.previous = collapsed;
};

/**
 * Function: execute
 * 
 * Changes the collapsed state of <cell> to <previous> using
 * <mxGraphModel.collapsedStateForCellChanged>.
 */
mxCollapseChange.prototype.execute = function()
{
	if (this.cell != null)
	{
		this.collapsed = this.previous;
		this.previous = this.model.collapsedStateForCellChanged(
			this.cell, this.previous);
	}
};

/**
 * Class: mxVisibleChange
 * 
 * Action to change a cell's visible state in a model.
 *
 * Constructor: mxVisibleChange
 * 
 * Constructs a change of a visible state in the
 * specified model.
 */
function mxVisibleChange(model, cell, visible)
{
	this.model = model;
	this.cell = cell;
	this.visible = visible;
	this.previous = visible;
};

/**
 * Function: execute
 * 
 * Changes the visible state of <cell> to <previous> using
 * <mxGraphModel.visibleStateForCellChanged>.
 */
mxVisibleChange.prototype.execute = function()
{
	if (this.cell != null)
	{
		this.visible = this.previous;
		this.previous = this.model.visibleStateForCellChanged(
			this.cell, this.previous);
	}
};

/**
 * Class: mxCellAttributeChange
 * 
 * Action to change the attribute of a cell's user object.
 * There is no method on the graph model that uses this
 * action. To use the action, you can use the code shown
 * in the example below.
 * 
 * Example:
 * 
 * To change the attributeName in the cell's user object
 * to attributeValue, use the following code:
 * 
 * (code)
 * model.beginUpdate();
 * try
 * {
 *   var edit = new mxCellAttributeChange(
 *     cell, attributeName, attributeValue);
 *   model.execute(edit);
 * }
 * finally
 * {
 *   model.endUpdate();
 * } 
 * (end)
 *
 * Constructor: mxCellAttributeChange
 * 
 * Constructs a change of a attribute of the DOM node
 * stored as the value of the given <mxCell>.
 */
function mxCellAttributeChange(cell, attribute, value)
{
	this.cell = cell;
	this.attribute = attribute;
	this.value = value;
	this.previous = value;
};

/**
 * Function: execute
 * 
 * Changes the attribute of the cell's user object by
 * using <mxCell.setAttribute>.
 */
mxCellAttributeChange.prototype.execute = function()
{
	if (this.cell != null)
	{
		var tmp = this.cell.getAttribute(this.attribute);
		
		if (this.previous == null)
		{
			this.cell.value.removeAttribute(this.attribute);
		}
		else
		{
			this.cell.setAttribute(this.attribute, this.previous);
		}
		
		this.previous = tmp;
	}
};
