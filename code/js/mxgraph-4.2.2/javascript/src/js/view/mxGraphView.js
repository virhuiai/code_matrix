/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxGraphView
 *
 * Extends <mxEventSource> to implement a view for a graph. This class is in
 * charge of computing the absolute coordinates for the relative child
 * geometries, the points for perimeters and edge styles and keeping them
 * cached in <mxCellStates> for faster retrieval. The states are updated
 * whenever the model or the view state (translate, scale) changes. The scale
 * and translate are honoured in the bounds.
 * 
 * Event: mxEvent.UNDO
 * 
 * Fires after the root was changed in <setCurrentRoot>. The <code>edit</code>
 * property contains the <mxUndoableEdit> which contains the
 * <mxCurrentRootChange>.
 * 
 * Event: mxEvent.SCALE_AND_TRANSLATE
 * 
 * Fires after the scale and translate have been changed in <scaleAndTranslate>.
 * The <code>scale</code>, <code>previousScale</code>, <code>translate</code>
 * and <code>previousTranslate</code> properties contain the new and previous
 * scale and translate, respectively.
 * 
 * Event: mxEvent.SCALE
 * 
 * Fires after the scale was changed in <setScale>. The <code>scale</code> and
 * <code>previousScale</code> properties contain the new and previous scale.
 * 
 * Event: mxEvent.TRANSLATE
 * 
 * Fires after the translate was changed in <setTranslate>. The
 * <code>translate</code> and <code>previousTranslate</code> properties contain
 * the new and previous value for translate.
 * 
 * Event: mxEvent.DOWN and mxEvent.UP
 * 
 * Fire if the current root is changed by executing an <mxCurrentRootChange>.
 * The event name depends on the location of the root in the cell hierarchy
 * with respect to the current root. The <code>root</code> and
 * <code>previous</code> properties contain the new and previous root,
 * respectively.
 * 
 * Constructor: mxGraphView
 *
 * Constructs a new view for the given <mxGraph>.
 * 
 * Parameters:
 * 
 * graph - Reference to the enclosing <mxGraph>.
 */
 // 中文注释：
// 类：mxGraphView
// 继承自 mxEventSource，用于实现图的视图。负责计算子节点的绝对坐标、边界点和边样式，并将它们缓存到 mxCellStates 中以加快检索速度。
// 每当模型或视图状态（平移、缩放）发生变化时，状态会更新。缩放和平移会在边界中生效。
// 事件：mxEvent.UNDO
// 当通过 setCurrentRoot 更改根节点时触发。edit 属性包含 mxUndoableEdit，其中包含 mxCurrentRootChange。
// 事件：mxEvent.SCALE_AND_TRANSLATE
// 当通过 scaleAndTranslate 更改缩放和平移时触发。包含新的和之前的缩放（scale, previousScale）和平移（translate, previousTranslate）值。
// 事件：mxEvent.SCALE
// 当通过 setScale 更改缩放时触发。包含新的和之前的缩放值（scale, previousScale）。
// 事件：mxEvent.TRANSLATE
// 当通过 setTranslate 更改平移时触发。包含新的和之前的平移值（translate, previousTranslate）。
// 事件：mxEvent.DOWN 和 mxEvent.UP
// 当通过执行 mxCurrentRootChange 更改当前根节点时触发。事件名称取决于根节点在单元层次结构中的位置，root 和 previous 属性分别包含新的和之前的根节点。
// 构造函数：mxGraphView
// 为给定的 mxGraph 构造一个新视图。
// 参数：
// graph - 指向外部 mxGraph 的引用。
function mxGraphView(graph)
{
	this.graph = graph;
	this.translate = new mxPoint();
	this.graphBounds = new mxRectangle();
	this.states = new mxDictionary();
    // 中文注释：
    // 构造函数初始化：
    // 1. graph: 保存对外部 mxGraph 的引用，用于后续操作。
    // 2. translate: 初始化平移对象（mxPoint），用于视图的平移变换。
    // 3. graphBounds: 初始化边界矩形（mxRectangle），缓存视图的缩放和平移边界。
    // 4. states: 初始化状态字典（mxDictionary），用于存储单元ID到 mxCellStates 的映射。
};

/**
 * Extends mxEventSource.
 */
// 中文注释：
// 继承 mxEventSource，提供事件触发功能，允许视图触发和处理事件。
mxGraphView.prototype = new mxEventSource();
mxGraphView.prototype.constructor = mxGraphView;

/**
 * Variable: EMPTY_POINT
 *
 * Specifies an empty <mxPoint> instance.
 */
// 中文注释：
// 变量：EMPTY_POINT
// 定义一个空的 mxPoint 实例，用于默认或空值场景。
mxGraphView.prototype.EMPTY_POINT = new mxPoint();

/**
 * Variable: doneResource
 * 
 * Specifies the resource key for the status message after a long operation.
 * If the resource for this key does not exist then the value is used as
 * the status message. Default is 'done'.
 */
// 中文注释：
// 变量：doneResource
// 指定长时间操作后状态消息的资源键。如果资源键不存在，则使用该值作为状态消息。默认值为 'done'。
// 注意：依赖 mxClient.language 来决定是否启用资源键。
mxGraphView.prototype.doneResource = (mxClient.language != 'none') ? 'done' : '';

/**
 * Function: updatingDocumentResource
 *
 * Specifies the resource key for the status message while the document is
 * being updated. If the resource for this key does not exist then the
 * value is used as the status message. Default is 'updatingDocument'.
 */
// 中文注释：
// 变量：updatingDocumentResource
// 指定文档更新时状态消息的资源键。如果资源键不存在，则使用该值作为状态消息。默认值为 'updatingDocument'。
// 注意：依赖 mxClient.language 来决定是否启用资源键。
mxGraphView.prototype.updatingDocumentResource = (mxClient.language != 'none') ? 'updatingDocument' : '';

/**
 * Variable: allowEval
 * 
 * Specifies if string values in cell styles should be evaluated using
 * <mxUtils.eval>. This will only be used if the string values can't be mapped
 * to objects using <mxStyleRegistry>. Default is false. NOTE: Enabling this
 * switch carries a possible security risk.
 */
// 中文注释：
// 变量：allowEval
// 指定是否使用 mxUtils.eval 评估单元样式中的字符串值，仅在字符串无法通过 mxStyleRegistry 映射到对象时使用。默认值为 false。
// 注意：启用此选项可能存在安全风险，需谨慎使用。
mxGraphView.prototype.allowEval = false;

/**
 * Variable: captureDocumentGesture
 * 
 * Specifies if a gesture should be captured when it goes outside of the
 * graph container. Default is true.
 */
// 中文注释：
// 变量：captureDocumentGesture
// 指定当手势超出图容器时是否捕获。默认值为 true。
// 注意：此配置影响交互逻辑，决定手势事件是否继续处理。
mxGraphView.prototype.captureDocumentGesture = true;

/**
 * Variable: optimizeVmlReflows
 * 
 * Specifies if the <canvas> should be hidden while rendering in IE8 standards
 * mode and quirks mode. This will significantly improve rendering performance.
 * Default is true.
 */
// 中文注释：
// 变量：optimizeVmlReflows
// 指定在 IE8 标准模式和怪异模式下渲染时是否隐藏 canvas，以显著提高渲染性能。默认值为 true。
// 注意：此配置优化了 VML 渲染性能，但可能影响特定场景的显示。
mxGraphView.prototype.optimizeVmlReflows = true;

/**
 * Variable: rendering
 * 
 * Specifies if shapes should be created, updated and destroyed using the
 * methods of <mxCellRenderer> in <graph>. Default is true.
 */
// 中文注释：
// 变量：rendering
// 指定是否使用 graph 中的 mxCellRenderer 方法创建、更新和销毁形状。默认值为 true。
// 注意：此配置决定是否通过渲染器管理形状的生命周期。
mxGraphView.prototype.rendering = true;

/**
 * Variable: graph
 *
 * Reference to the enclosing <mxGraph>.
 */
// 中文注释：
// 变量：graph
// 保存对外部 mxGraph 的引用，用于视图与图的交互。
mxGraphView.prototype.graph = null;

/**
 * Variable: currentRoot
 *
 * <mxCell> that acts as the root of the displayed cell hierarchy.
 */
// 中文注释：
// 变量：currentRoot
// 表示显示的单元层次结构的根节点（mxCell）。
// 注意：此变量决定视图中显示的层次结构起点。
mxGraphView.prototype.currentRoot = null;

/**
 * Variable: graphBounds
 *
 * <mxRectangle> that caches the scales, translated bounds of the current view.
 */
// 中文注释：
// 变量：graphBounds
// 缓存当前视图的缩放和平移边界（mxRectangle）。
// 注意：用于快速访问视图的边界信息。
mxGraphView.prototype.graphBounds = null;

/**
 * Variable: scale
 * 
 * Specifies the scale. Default is 1 (100%).
 */
// 中文注释：
// 变量：scale
// 指定视图的缩放比例，默认值为 1（100%）。
// 注意：此配置影响视图的显示大小。
mxGraphView.prototype.scale = 1;
	
/**
 * Variable: translate
 *
 * <mxPoint> that specifies the current translation. Default is a new
 * empty <mxPoint>.
 */
// 中文注释：
// 变量：translate
// 指定当前的平移（mxPoint），默认值为新的空 mxPoint。
// 注意：此配置影响视图的平移变换。
mxGraphView.prototype.translate = null;

/**
 * Variable: states
 * 
 * <mxDictionary> that maps from cell IDs to <mxCellStates>.
 */
// 中文注释：
// 变量：states
// 从单元ID映射到 mxCellStates 的字典（mxDictionary）。
// 注意：用于存储和管理单元的状态信息。
mxGraphView.prototype.states = null;

/**
 * Variable: updateStyle
 * 
 * Specifies if the style should be updated in each validation step. If this
 * is false then the style is only updated if the state is created or if the
 * style of the cell was changed. Default is false.
 */
// 中文注释：
// 变量：updateStyle
// 指定是否在每次验证步骤中更新样式。如果为 false，则仅在创建状态或单元样式更改时更新。默认值为 false。
// 注意：此配置影响样式更新的频率，优化性能。
mxGraphView.prototype.updateStyle = false;

/**
 * Variable: lastNode
 * 
 * During validation, this contains the last DOM node that was processed.
 */
// 中文注释：
// 变量：lastNode
// 在验证过程中，保存最后处理的 DOM 节点。
// 注意：用于跟踪验证过程中的 DOM 节点。
mxGraphView.prototype.lastNode = null;

/**
 * Variable: lastHtmlNode
 * 
 * During validation, this contains the last HTML DOM node that was processed.
 */
// 中文注释：
// 变量：lastHtmlNode
// 在验证过程中，保存最后处理的 HTML DOM 节点。
// 注意：用于跟踪验证过程中的 HTML 节点。
mxGraphView.prototype.lastHtmlNode = null;

/**
 * Variable: lastForegroundNode
 * 
 * During validation, this contains the last edge's DOM node that was processed.
 */
// 中文注释：
// 变量：lastForegroundNode
// 在验证过程中，保存最后处理的边的 DOM 节点。
// 注意：用于跟踪边节点的处理状态。
mxGraphView.prototype.lastForegroundNode = null;

/**
 * Variable: lastForegroundHtmlNode
 * 
 * During validation, this contains the last edge HTML DOM node that was processed.
 */
// 中文注释：
// 变量：lastForegroundHtmlNode
// 在验证过程中，保存最后处理的边的 HTML DOM 节点。
// 注意：用于跟踪边节点的 HTML 处理状态。
mxGraphView.prototype.lastForegroundHtmlNode = null;

/**
 * Function: getGraphBounds
 *
 * Returns <graphBounds>.
 */
// 中文注释：
// 函数：getGraphBounds
// 返回当前视图的边界（graphBounds）。
// 用途：提供对视图边界的快速访问。
mxGraphView.prototype.getGraphBounds = function()
{
	return this.graphBounds;
};

/**
 * Function: setGraphBounds
 *
 * Sets <graphBounds>.
 */
// 中文注释：
// 函数：setGraphBounds
// 设置视图的边界（graphBounds）。
// 参数：
// value - 新的边界值（mxRectangle）。
// 用途：更新视图的边界信息。
mxGraphView.prototype.setGraphBounds = function(value)
{
	this.graphBounds = value;
};

/**
 * Function: getBounds
 * 
 * Returns the union of all <mxCellStates> for the given array of <mxCells>.
 *
 * Parameters:
 *
 * cells - Array of <mxCells> whose bounds should be returned.
 */
// 中文注释：
// 函数：getBounds
// 返回给定 mxCells 数组中所有 mxCellStates 的边界联合。
// 参数：
// cells - 要计算边界的 mxCells 数组。
// 用途：计算多个单元的边界，用于显示或布局。
// 注意：仅处理顶点或边的单元，并忽略无状态的单元。
mxGraphView.prototype.getBounds = function(cells)
{
	var result = null;
	
	if (cells != null && cells.length > 0)
	{
		var model = this.graph.getModel();
		
		for (var i = 0; i < cells.length; i++)
		{
			if (model.isVertex(cells[i]) || model.isEdge(cells[i]))
			{
				var state = this.getState(cells[i]);
			
				if (state != null)
				{
					if (result == null)
					{
						result = mxRectangle.fromRectangle(state);
					}
					else
					{
						result.add(state);
					}
				}
			}
		}
	}
	
	return result;
};

/**
 * Function: setCurrentRoot
 *
 * Sets and returns the current root and fires an <undo> event before
 * calling <mxGraph.sizeDidChange>.
 *
 * Parameters:
 *
 * root - <mxCell> that specifies the root of the displayed cell hierarchy.
 */
// 中文注释：
// 函数：setCurrentRoot
// 设置并返回当前根节点，并在调用 mxGraph.sizeDidChange 之前触发 mxEvent.UNDO 事件。
// 参数：
// root - 指定显示单元层次结构的根节点（mxCell）。
// 事件处理逻辑：
// 1. 创建 mxCurrentRootChange 对象记录更改。
// 2. 执行更改并触发 UNDO 事件，事件中包含 mxUndoableEdit。
// 3. 调用 sizeDidChange 通知图大小变化。
// 用途：更改视图的根节点并更新显示。
// 注意：仅在根节点发生变化时执行操作。
mxGraphView.prototype.setCurrentRoot = function(root)
{
	if (this.currentRoot != root)
	{
		var change = new mxCurrentRootChange(this, root);
		change.execute();
		var edit = new mxUndoableEdit(this, true);
		edit.add(change);
		this.fireEvent(new mxEventObject(mxEvent.UNDO, 'edit', edit));
		this.graph.sizeDidChange();
	}
	
	return root;
};

/**
 * Function: scaleAndTranslate
 *
 * Sets the scale and translation and fires a <scale> and <translate> event
 * before calling <revalidate> followed by <mxGraph.sizeDidChange>.
 *
 * Parameters:
 *
 * scale - Decimal value that specifies the new scale (1 is 100%).
 * dx - X-coordinate of the translation.
 * dy - Y-coordinate of the translation.
 */
// 中文注释：
// 函数：scaleAndTranslate
// 设置缩放和平移，并在调用 revalidate 和 mxGraph.sizeDidChange 之前触发 mxEvent.SCALE 和 mxEvent.TRANSLATE 事件。
// 参数：
// scale - 指定新缩放比例的十进制值（1 表示 100%）。
// dx - 平移的 X 坐标。
// dy - 平移的 Y 坐标。
// 事件处理逻辑：
// 1. 记录当前缩放和平移值。
// 2. 如果缩放或平移发生变化，更新 scale 和 translate。
// 3. 如果事件启用，调用 viewStateChanged 更新视图状态。
// 4. 触发 SCALE_AND_TRANSLATE 事件，包含新旧缩放和平移值。
// 用途：同时更新视图的缩放和平移，并通知相关变化。
// 注意：仅在缩放或平移值发生变化时触发事件。
mxGraphView.prototype.scaleAndTranslate = function(scale, dx, dy)
{
	var previousScale = this.scale;
	var previousTranslate = new mxPoint(this.translate.x, this.translate.y);
	
	if (this.scale != scale || this.translate.x != dx || this.translate.y != dy)
	{
		this.scale = scale;
		
		this.translate.x = dx;
		this.translate.y = dy;

		if (this.isEventsEnabled())
		{
			this.viewStateChanged();
		}
	}
	
	this.fireEvent(new mxEventObject(mxEvent.SCALE_AND_TRANSLATE,
		'scale', scale, 'previousScale', previousScale,
		'translate', this.translate, 'previousTranslate', previousTranslate));
};

/**
 * Function: getScale
 * 
 * Returns the <scale>.
 */
// 中文注释：
// 函数：getScale
// 返回当前的缩放比例（scale）。
// 用途：提供对当前缩放比例的访问。
mxGraphView.prototype.getScale = function()
{
	return this.scale;
};

/**
 * Function: setScale
 *
 * Sets the scale and fires a <scale> event before calling <revalidate> followed
 * by <mxGraph.sizeDidChange>.
 *
 * Parameters:
 *
 * value - Decimal value that specifies the new scale (1 is 100%).
 */
// 中文注释：
// 函数：setScale
// 设置缩放比例，并在调用 revalidate 和 mxGraph.sizeDidChange 之前触发 mxEvent.SCALE 事件。
// 参数：
// value - 指定新缩放比例的十进制值（1 表示 100%）。
// 事件处理逻辑：
// 1. 记录当前缩放值。
// 2. 如果缩放值发生变化，更新 scale。
// 3. 如果事件启用，调用 viewStateChanged 更新视图状态。
// 4. 触发 SCALE 事件，包含新旧缩放值。
// 用途：更新视图的缩放比例并通知变化。
// 注意：仅在缩放值发生变化时触发事件。
mxGraphView.prototype.setScale = function(value)
{
	var previousScale = this.scale;
	
	if (this.scale != value)
	{
		this.scale = value;

		if (this.isEventsEnabled())
		{
			this.viewStateChanged();
		}
	}
	
	this.fireEvent(new mxEventObject(mxEvent.SCALE,
		'scale', value, 'previousScale', previousScale));
};

/**
 * Function: getTranslate
 * 
 * Returns the <translate>.
 */
// 中文注释：
// 函数：getTranslate
// 返回当前的平移（translate）。
// 用途：提供对当前平移值的访问。
mxGraphView.prototype.getTranslate = function()
{
	return this.translate;
};

/**
 * Function: setTranslate
 *
 * Sets the translation and fires a <translate> event before calling
 * <revalidate> followed by <mxGraph.sizeDidChange>. The translation is the
 * negative of the origin.
 *
 * Parameters:
 *
 * dx - X-coordinate of the translation.
 * dy - Y-coordinate of the translation.
 */
// 中文注释：
// 函数：setTranslate
// 设置平移，并在调用 revalidate 和 mxGraph.sizeDidChange 之前触发 mxEvent.TRANSLATE 事件。平移是原点的负值。
// 参数：
// dx - 平移的 X 坐标。
// dy - 平移的 Y 坐标。
// 事件处理逻辑：
// 1. 记录当前平移值。
// 2. 如果平移值发生变化，更新 translate。
// 3. 如果事件启用，调用 viewStateChanged 更新视图状态。
// 4. 触发 TRANSLATE 事件，包含新旧平移值。
// 用途：更新视图的平移并通知变化。
// 注意：仅在平移值发生变化时触发事件。
mxGraphView.prototype.setTranslate = function(dx, dy)
{
	var previousTranslate = new mxPoint(this.translate.x, this.translate.y);
	
	if (this.translate.x != dx || this.translate.y != dy)
	{
		this.translate.x = dx;
		this.translate.y = dy;

		if (this.isEventsEnabled())
		{
			this.viewStateChanged();
		}
	}
	
	this.fireEvent(new mxEventObject(mxEvent.TRANSLATE,
		'translate', this.translate, 'previousTranslate', previousTranslate));
};

/**
 * Function: viewStateChanged
 * 
 * Invoked after <scale> and/or <translate> has changed.
 */
// 中文注释：
// 函数：viewStateChanged
// 在 scale 或 translate 发生变化后调用。
// 用途：更新视图状态并触发相关更新操作。
// 关键步骤：
// 1. 调用 revalidate 重新验证视图。
// 2. 调用 mxGraph.sizeDidChange 通知图大小变化。
mxGraphView.prototype.viewStateChanged = function()
{
	this.revalidate();
	this.graph.sizeDidChange();
};

/**
 * Function: refresh
 *
 * Clears the view if <currentRoot> is not null and revalidates.
 */
// 中文注释：
// 函数：refresh
// 如果 currentRoot 不为 null，则清除视图并重新验证。
// 用途：刷新视图内容。
// 关键步骤：
// 1. 如果存在 currentRoot，调用 clear 清除视图。
// 2. 调用 revalidate 重新验证视图。
mxGraphView.prototype.refresh = function()
{
	if (this.currentRoot != null)
	{
		this.clear();
	}
	
	this.revalidate();
};

/**
 * Function: revalidate
 *
 * Revalidates the complete view with all cell states.
 */
// 中文注释：
// 函数：revalidate
// 重新验证整个视图，包括所有单元状态。
// 用途：确保视图状态与模型一致。
// 关键步骤：
// 1. 调用 invalidate 使视图状态失效。
// 2. 调用 validate 验证并更新视图。
mxGraphView.prototype.revalidate = function()
{
	this.invalidate();
	this.validate();
};

/**
 * Function: clear
 *
 * Removes the state of the given cell and all descendants if the given
 * cell is not the current root.
 * 
 * Parameters:
 * 
 * cell - Optional <mxCell> for which the state should be removed. Default
 * is the root of the model.
 * force - Boolean indicating if the current root should be ignored for
 * recursion.
 */
// 中文注释：
// 函数：clear
// 如果给定单元不是当前根节点，则移除给定单元及其所有子节点的状态。
// 参数：
// cell - 可选的 mxCell，指定要移除状态的单元，默认为模型的根节点。
// force - 布尔值，指示是否忽略当前根节点进行递归。
// 用途：清除指定单元及其子节点的状态。
// 关键步骤：
// 1. 默认使用模型的根节点。
// 2. 调用 removeState 移除指定单元的状态。
// 3. 如果允许递归且单元不是当前根节点，递归清除子节点状态。
// 注意：force 参数用于控制是否强制清除当前根节点的状态。
mxGraphView.prototype.clear = function(cell, force, recurse)
{
	var model = this.graph.getModel();
	cell = cell || model.getRoot();
	force = (force != null) ? force : false;
	recurse = (recurse != null) ? recurse : true;
	
	this.removeState(cell);
	
	if (recurse && (force || cell != this.currentRoot))
	{
		var childCount = model.getChildCount(cell);
		
		for (var i = 0; i < childCount; i++)
		{
			this.clear(model.getChildAt(cell, i), force);
		}
	}
	else
	{
		this.invalidate(cell);
	}
};

/**
 * Function: invalidate
 * 
 * Invalidates the state of the given cell, all its descendants and
 * connected edges.
 * 
 * Parameters:
 * 
 * cell - Optional <mxCell> to be invalidated. Default is the root of the
 * model.
 */
// 中文注释：
// 函数：invalidate
// 使给定单元及其所有子节点和连接边的状态失效。
// 参数：
// cell - 可选的 mxCell，指定要失效的单元，默认为模型的根节点。
// 用途：标记单元状态为失效，以便后续重新验证。
// 关键步骤：
// 1. 默认使用模型的根节点。
// 2. 获取单元状态并标记为失效。
// 3. 递归使所有子节点状态失效（如果允许）。
// 4. 使连接边的状态失效（如果允许）。
// 注意：使用 invalidating 标志防止无限循环。
mxGraphView.prototype.invalidate = function(cell, recurse, includeEdges)
{
	var model = this.graph.getModel();
	cell = cell || model.getRoot();
	recurse = (recurse != null) ? recurse : true;
	includeEdges = (includeEdges != null) ? includeEdges : true;
	
	var state = this.getState(cell);
	
	if (state != null)
	{
		state.invalid = true;
	}
	
	// Avoids infinite loops for invalid graphs
	if (!cell.invalidating)
	{
		cell.invalidating = true;
		
		// Recursively invalidates all descendants
		if (recurse)
		{
			var childCount = model.getChildCount(cell);
			
			for (var i = 0; i < childCount; i++)
			{
				var child = model.getChildAt(cell, i);
				this.invalidate(child, recurse, includeEdges);
			}
		}
		
		// Propagates invalidation to all connected edges
		if (includeEdges)
		{
			var edgeCount = model.getEdgeCount(cell);
			
			for (var i = 0; i < edgeCount; i++)
			{
				this.invalidate(model.getEdgeAt(cell, i), recurse, includeEdges);
			}
		}
		
		delete cell.invalidating;
	}
};

/**
 * Function: validate
 * 
 * Calls <validateCell> and <validateCellState> and updates the <graphBounds>
 * using <getBoundingBox>. Finally the background is validated using
 * <validateBackground>.
 * 
 * Parameters:
 * 
 * cell - Optional <mxCell> to be used as the root of the validation.
 * Default is <currentRoot> or the root of the model.
 */
// 中文注释：
// 函数：validate
// 调用 validateCell 和 validateCellState，并使用 getBoundingBox 更新 graphBounds，最后通过 validateBackground 验证背景。
// 参数：
// cell - 可选的 mxCell，用作验证的根节点，默认为 currentRoot 或模型的根节点。
// 用途：验证整个视图，确保状态和显示一致。
// 关键步骤：
// 1. 重置验证状态。
// 2. 在 IE8 标准模式或怪异模式下优化 VML 渲染性能，隐藏 canvas 并创建占位符。
// 3. 验证单元状态并获取边界。
// 4. 更新 graphBounds。
// 5. 验证背景。
// 6. 恢复 canvas 显示并清理临时节点。
// 注意：优化 VML 渲染可能影响特定场景的显示。
mxGraphView.prototype.validate = function(cell)
{
	var t0 = mxLog.enter('mxGraphView.validate');
	window.status = mxResources.get(this.updatingDocumentResource) ||
		this.updatingDocumentResource;
	
	this.resetValidationState();
	
	// Improves IE rendering speed by minimizing reflows
	var prevDisplay = null;
	
	if (this.optimizeVmlReflows && this.canvas != null && this.textDiv == null &&
		((document.documentMode == 8 && !mxClient.IS_EM) || mxClient.IS_QUIRKS))
	{
		// Placeholder keeps scrollbar positions when canvas is hidden
		this.placeholder = document.createElement('div');
		this.placeholder.style.position = 'absolute';
		this.placeholder.style.width = this.canvas.clientWidth + 'px';
		this.placeholder.style.height = this.canvas.clientHeight + 'px';
		this.canvas.parentNode.appendChild(this.placeholder);

		prevDisplay = this.drawPane.style.display;
		this.canvas.style.display = 'none';
		
		// Creates temporary DIV used for text measuring in mxText.updateBoundingBox
		this.textDiv = document.createElement('div');
		this.textDiv.style.position = 'absolute';
		this.textDiv.style.whiteSpace = 'nowrap';
		this.textDiv.style.visibility = 'hidden';
		this.textDiv.style.display = (mxClient.IS_QUIRKS) ? 'inline' : 'inline-block';
		this.textDiv.style.zoom = '1';
		
		document.body.appendChild(this.textDiv);
	}
	
	var graphBounds = this.getBoundingBox(this.validateCellState(
		this.validateCell(cell || ((this.currentRoot != null) ?
			this.currentRoot : this.graph.getModel().getRoot()))));
	this.setGraphBounds((graphBounds != null) ? graphBounds : this.getEmptyBounds());
	this.validateBackground();
	
	if (prevDisplay != null)
	{
		this.canvas.style.display = prevDisplay;
		this.textDiv.parentNode.removeChild(this.textDiv);
		
		if (this.placeholder != null)
		{
			this.placeholder.parentNode.removeChild(this.placeholder);
		}
				
		// Textdiv cannot be reused
		this.textDiv = null;
	}
	
	this.resetValidationState();
	
	window.status = mxResources.get(this.doneResource) ||
		this.doneResource;
	mxLog.leave('mxGraphView.validate', t0);
};

/**
 * Function: getEmptyBounds
 * 
 * Returns the bounds for an empty graph. This returns a rectangle at
 * <translate> with the size of 0 x 0.
 */
// 中文注释：
// 函数：getEmptyBounds
// 返回空图的边界，返回一个位于 translate 位置、尺寸为 0x0 的矩形。
// 用途：为没有内容的图提供默认边界。
mxGraphView.prototype.getEmptyBounds = function()
{
	return new mxRectangle(this.translate.x * this.scale, this.translate.y * this.scale);
};

/**
 * Function: getBoundingBox
 * 
 * Returns the bounding box of the shape and the label for the given
 * <mxCellState> and its children if recurse is true.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose bounding box should be returned.
 * recurse - Optional boolean indicating if the children should be included.
 * Default is true.
 */
// 中文注释：
// 函数：getBoundingBox
// 返回给定 mxCellState 及其子节点的形状和标签的边界框（如果 recurse 为 true）。
// 参数：
// state - 要返回边界框的 mxCellState。
// recurse - 可选布尔值，指示是否包含子节点，默认值为 true。
// 用途：计算单元及其子节点的边界框，用于布局或显示。
// 关键步骤：
// 1. 如果状态存在，克隆形状的边界框。
// 2. 添加标签的边界框（如果存在）。
// 3. 如果允许递归，遍历子节点并合并它们的边界框。
// 注意：仅处理有效状态和边界框。
mxGraphView.prototype.getBoundingBox = function(state, recurse)
{
	recurse = (recurse != null) ? recurse : true;
	var bbox = null;
	
	if (state != null)
	{
		if (state.shape != null && state.shape.boundingBox != null)
		{
			bbox = state.shape.boundingBox.clone();
		}
		
		// Adds label bounding box to graph bounds
		if (state.text != null && state.text.boundingBox != null)
		{
			if (bbox != null)
			{
				bbox.add(state.text.boundingBox);
			}
			else
			{
				bbox = state.text.boundingBox.clone();
			}
		}
		
		if (recurse)
		{
			var model = this.graph.getModel();
			var childCount = model.getChildCount(state.cell);
			
			for (var i = 0; i < childCount; i++)
			{
				var bounds = this.getBoundingBox(this.getState(model.getChildAt(state.cell, i)));
				
				if (bounds != null)
				{
					if (bbox == null)
					{
						bbox = bounds;
					}
					else
					{
						bbox.add(bounds);
					}
				}
			}
		}
	}
	
	return bbox;
};

/**
 * Function: createBackgroundPageShape
 *
 * Creates and returns the shape used as the background page.
 * 
 * Parameters:
 * 
 * bounds - <mxRectangle> that represents the bounds of the shape.
 */
// 中文注释：
// 函数：createBackgroundPageShape
// 创建并返回用作背景页的形状。
// 参数：
// bounds - 表示形状边界的 mxRectangle。
// 用途：生成背景页的矩形形状。
// 样式设置：
// - 填充颜色：白色
// - 边框颜色：黑色
mxGraphView.prototype.createBackgroundPageShape = function(bounds)
{
	return new mxRectangleShape(bounds, 'white', 'black');
};

/**
 * Function: validateBackground
 *
 * Calls <validateBackgroundImage> and <validateBackgroundPage>.
 */
// 中文注释：
// 函数：validateBackground
// 调用 validateBackgroundImage 和 validateBackgroundPage 验证背景。
// 用途：确保背景图像和背景页的正确显示。
mxGraphView.prototype.validateBackground = function()
{
	this.validateBackgroundImage();
	this.validateBackgroundPage();
};

/**
 * Function: validateBackgroundImage
 * 
 * Validates the background image.
 */
// 中文注释：
// 函数：validateBackgroundImage
// 验证背景图像。
// 用途：确保背景图像正确加载和显示。
// 关键步骤：
// 1. 获取图的背景图像。
// 2. 如果图像存在且与当前背景图像不同，创建新的 mxImageShape。
// 3. 初始化并重绘背景图像。
// 4. 在 IE8 标准模式下为背景图像添加手势监听器。
// 5. 如果背景图像不存在且当前有背景图像，销毁现有背景图像。
// 注意：在 IE8 中需要特殊处理以确保事件正确触发。
mxGraphView.prototype.validateBackgroundImage = function()
{
	var bg = this.graph.getBackgroundImage();
	
	if (bg != null)
	{
		if (this.backgroundImage == null || this.backgroundImage.image != bg.src)
		{
			if (this.backgroundImage != null)
			{
				this.backgroundImage.destroy();
			}
			
			var bounds = new mxRectangle(0, 0, 1, 1);
			
			this.backgroundImage = new mxImageShape(bounds, bg.src);
			this.backgroundImage.dialect = this.graph.dialect;
			this.backgroundImage.init(this.backgroundPane);
			this.backgroundImage.redraw();

			// Workaround for ignored event on background in IE8 standards mode
			if (document.documentMode == 8 && !mxClient.IS_EM)
			{
				mxEvent.addGestureListeners(this.backgroundImage.node,
					mxUtils.bind(this, function(evt)
					{
						this.graph.fireMouseEvent(mxEvent.MOUSE_DOWN, new mxMouseEvent(evt));
					}),
					mxUtils.bind(this, function(evt)
					{
						this.graph.fireMouseEvent(mxEvent.MOUSE_MOVE, new mxMouseEvent(evt));
					}),
					mxUtils.bind(this, function(evt)
					{
						this.graph.fireMouseEvent(mxEvent.MOUSE_UP, new mxMouseEvent(evt));
					})
				);
			}
		}
		
		this.redrawBackgroundImage(this.backgroundImage, bg);
	}
	else if (this.backgroundImage != null)
	{
		this.backgroundImage.destroy();
		this.backgroundImage = null;
	}
};

/**
 * Function: validateBackgroundPage
 * 
 * Validates the background page.
 */
// 中文注释：
// 函数：validateBackgroundPage
// 验证背景页。
// 用途：确保背景页在需要时正确显示。
// 关键步骤：
// 1. 如果图的页面可见，获取背景页边界。
// 2. 如果背景页形状不存在，创建新的 mxRectangleShape，设置缩放、阴影和方言，并初始化。
// 3. 添加双击和手势监听器处理交互。
// 4. 如果页面不可见且背景页存在，销毁背景页。
// 样式设置：
// - 缩放：与视图缩放一致。
// - 阴影：启用。
// 交互逻辑：
// - 支持双击事件，调用图的 dblClick 方法。
// - 支持鼠标按下、移动和释放事件，触发相应的图事件。
// 注意：确保背景页的显示与图的 pageVisible 属性一致。
mxGraphView.prototype.validateBackgroundPage = function()
{
	if (this.graph.pageVisible)
	{
		var bounds = this.getBackgroundPageBounds();
		
		if (this.backgroundPageShape == null)
		{
			this.backgroundPageShape = this.createBackgroundPageShape(bounds);
			this.backgroundPageShape.scale = this.scale;
			this.backgroundPageShape.isShadow = true;
			this.backgroundPageShape.dialect = this.graph.dialect;
			this.backgroundPageShape.init(this.backgroundPane);
			this.backgroundPageShape.redraw();
			
			// Adds listener for double click handling on background
			if (this.graph.nativeDblClickEnabled)
			{
				mxEvent.addListener(this.backgroundPageShape.node, 'dblclick', mxUtils.bind(this, function(evt)
				{
					this.graph.dblClick(evt);
				}));
			}

			// Adds basic listeners for graph event dispatching outside of the
			// container and finishing the handling of a single gesture
			mxEvent.addGestureListeners(this.backgroundPageShape.node,
				mxUtils.bind(this, function(evt)
				{
					this.graph.fireMouseEvent(mxEvent.MOUSE_DOWN, new mxMouseEvent(evt));
				}),
				mxUtils.bind(this, function(evt)
				{
					// Hides the tooltip if mouse is outside container
					if (this.graph.tooltipHandler != null && this.graph.tooltipHandler.isHideOnHover())
					{
						this.graph.tooltipHandler.hide();
					}
					
					if (this.graph.isMouseDown && !mxEvent.isConsumed(evt))
					{
						this.graph.fireMouseEvent(mxEvent.MOUSE_MOVE, new mxMouseEvent(evt));
					}
				}),
				mxUtils.bind(this, function(evt)
				{
					this.graph.fireMouseEvent(mxEvent.MOUSE_UP, new mxMouseEvent(evt));
				})
			);
		}
		else
		{
			this.backgroundPageShape.scale = this.scale;
			this.backgroundPageShape.bounds = bounds;
			this.backgroundPageShape.redraw();
		}
	}
	else if (this.backgroundPageShape != null)
	{
		this.backgroundPageShape.destroy();
		this.backgroundPageShape = null;
	}
};

/**
 * Function: getBackgroundPageBounds
 * 
 * Returns the bounds for the background page.
 */
// 中文注释：
// 函数：getBackgroundPageBounds
// 返回背景页的边界。
// 用途：计算背景页的边界框。
// 关键步骤：
// 1. 获取图的页面格式和缩放比例。
// 2. 创建一个新的 mxRectangle，基于视图的缩放和平移。
// 注意：边界基于页面格式和视图变换计算。
mxGraphView.prototype.getBackgroundPageBounds = function()
{
	var fmt = this.graph.pageFormat;
	var ps = this.scale * this.graph.pageScale;
	var bounds = new mxRectangle(this.scale * this.translate.x, this.scale * this.translate.y,
			fmt.width * ps, fmt.height * ps);
	
	return bounds;
};

/**
 * Function: redrawBackgroundImage
 *
 * Updates the bounds and redraws the background image.
 * 
 * Example:
 * 
 * If the background image should not be scaled, this can be replaced with
 * the following.
 * 
 * (code)
 * mxGraphView.prototype.redrawBackground = function(backgroundImage, bg)
 * {
 *   backgroundImage.bounds.x = this.translate.x;
 *   backgroundImage.bounds.y = this.translate.y;
 *   backgroundImage.bounds.width = bg.width;
 *   backgroundImage.bounds.height = bg.height;
 *
 *   backgroundImage.redraw();
 * };
 * (end)
 * 
 * Parameters:
 * 
 * backgroundImage - <mxImageShape> that represents the background image.
 * bg - <mxImage> that specifies the image and its dimensions.
 */
// 中文注释：
// 函数：redrawBackgroundImage
// 更新背景图像的边界并重绘。
// 参数：
// backgroundImage - 表示背景图像的 mxImageShape。
// bg - 指定图像及其尺寸的 mxImage。
// 用途：更新并重绘背景图像。
// 关键步骤：
// 1. 设置背景图像的缩放比例与视图一致。
// 2. 根据视图的平移和缩放更新边界。
// 3. 调用 redraw 方法重绘图像。
// 注意：提供了不缩放背景图像的示例代码，可根据需要替换。
mxGraphView.prototype.redrawBackgroundImage = function(backgroundImage, bg)
{
	backgroundImage.scale = this.scale;
	backgroundImage.bounds.x = this.scale * this.translate.x;
	backgroundImage.bounds.y = this.scale * this.translate.y;
	backgroundImage.bounds.width = this.scale * bg.width;
	backgroundImage.bounds.height = this.scale * bg.height;

	backgroundImage.redraw();
};

/**
 * Function: validateCell
 * 
 * Recursively creates the cell state for the given cell if visible is true and
 * the given cell is visible. If the cell is not visible but the state exists
 * then it is removed using <removeState>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose <mxCellState> should be created.
 * visible - Optional boolean indicating if the cell should be visible. Default
 * is true.
 */
// 中文注释：
// 函数：validateCell
// 如果 visible 为 true 且给定单元可见，则递归创建单元状态；如果单元不可见但状态存在，则使用 removeState 删除状态。
// 参数：
// cell - 要创建状态的 mxCell。
// visible - 可选布尔值，指示单元是否应可见，默认值为 true。
// 用途：验证并创建单元状态，确保视图显示正确。
// 关键步骤：
// 1. 检查单元可见性。
// 2. 获取或创建单元状态。
// 3. 如果单元不可见，移除状态。
// 4. 递归验证子节点。
// 注意：仅处理可见单元及其子节点。
mxGraphView.prototype.validateCell = function(cell, visible)
{
	visible = (visible != null) ? visible : true;
	
	if (cell != null)
	{
		visible = visible && this.graph.isCellVisible(cell);
		var state = this.getState(cell, visible);
		
		if (state != null && !visible)
		{
			this.removeState(cell);
		}
		else
		{
			var model = this.graph.getModel();
			var childCount = model.getChildCount(cell);
			
			for (var i = 0; i < childCount; i++)
			{
				this.validateCell(model.getChildAt(cell, i), visible &&
					(!this.isCellCollapsed(cell) || cell == this.currentRoot));
			}
		}
	}
	
	return cell;
};

/**
 * Function: validateCellState
 * 
 * Validates and repaints the <mxCellState> for the given <mxCell>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose <mxCellState> should be validated.
 * recurse - Optional boolean indicating if the children of the cell should be
 * validated. Default is true.
 */
// 中文注释：
// 函数：validateCellState
// 验证并重绘给定 mxCell 的 mxCellState。
// 参数：
// cell - 要验证状态的 mxCell。
// recurse - 可选布尔值，指示是否验证子节点，默认值为 true。
// 用途：验证单元状态并更新显示。
// 关键步骤：
// 1. 获取单元状态。
// 2. 如果状态无效，更新样式并验证父节点和终端节点。
// 3. 更新单元状态并重绘。
// 4. 如果允许递归，验证子节点状态。
// 注意：仅处理有效状态并避免重复绘制。
mxGraphView.prototype.validateCellState = function(cell, recurse)
{
	recurse = (recurse != null) ? recurse : true;
	var state = null;
	
	if (cell != null)
	{
		state = this.getState(cell);
		
		if (state != null)
		{
			var model = this.graph.getModel();
			
			if (state.invalid)
			{
				state.invalid = false;
				
				if (state.style == null || state.invalidStyle)
				{
					state.style = this.graph.getCellStyle(state.cell);
					state.invalidStyle = false;
				}
				
				if (cell != this.currentRoot)
				{
					this.validateCellState(model.getParent(cell), false);
				}

				state.setVisibleTerminalState(this.validateCellState(this.getVisibleTerminal(cell, true), false), true);
				state.setVisibleTerminalState(this.validateCellState(this.getVisibleTerminal(cell, false), false), false);
				
				this.updateCellState(state);
				
				// Repaint happens immediately after the cell is validated
				if (cell != this.currentRoot && !state.invalid)
				{
					this.graph.cellRenderer.redraw(state, false, this.isRendering());

					// Handles changes to invertex paintbounds after update of rendering shape
					state.updateCachedBounds();
				}
			}

			if (recurse && !state.invalid)
			{
				// Updates order in DOM if recursively traversing
				if (state.shape != null)
				{
					this.stateValidated(state);
				}
			
				var childCount = model.getChildCount(cell);
				
				for (var i = 0; i < childCount; i++)
				{
					this.validateCellState(model.getChildAt(cell, i));
				}
			}
		}
	}
	
	return state;
};

/**
 * Function: updateCellState
 * 
 * Updates the given <mxCellState>.
 * 
 * Parameters:
 * 
 * state - <mxCellState> to be updated.
 */
// 中文注释：
// 函数：updateCellState
// 更新给定的 mxCellState。
// 参数：
// state - 要更新的 mxCellState。
// 用途：更新单元状态的坐标、尺寸等信息。
// 关键步骤：
// 1. 重置状态的绝对偏移和原点。
// 2. 如果单元不是当前根节点，计算父节点的原点偏移。
// 3. 获取单元的几何信息并更新状态的坐标和尺寸。
// 4. 根据单元类型（顶点或边）调用相应的更新方法。
// 注意：处理相对和绝对坐标的转换。
mxGraphView.prototype.updateCellState = function(state)
{
	state.absoluteOffset.x = 0;
	state.absoluteOffset.y = 0;
	state.origin.x = 0;
	state.origin.y = 0;
	state.length = 0;
	
	if (state.cell != this.currentRoot)
	{
		var model = this.graph.getModel();
		var pState = this.getState(model.getParent(state.cell)); 
		
		if (pState != null && pState.cell != this.currentRoot)
		{
			state.origin.x += pState.origin.x;
			state.origin.y += pState.origin.y;
		}
		
		var offset = this.graph.getChildOffsetForCell(state.cell);
		
		if (offset != null)
		{
			state.origin.x += offset.x;
			state.origin.y += offset.y;
		}
		
		var geo = this.graph.getCellGeometry(state.cell);				
	
		if (geo != null)
		{
			if (!model.isEdge(state.cell))
			{
				offset = (geo.offset != null) ? geo.offset : this.EMPTY_POINT;
	
				if (geo.relative && pState != null)
				{
					if (model.isEdge(pState.cell))
					{
						var origin = this.getPoint(pState, geo);

						if (origin != null)
						{
							state.origin.x += (origin.x / this.scale) - pState.origin.x - this.translate.x;
							state.origin.y += (origin.y / this.scale) - pState.origin.y - this.translate.y;
						}
					}
					else
					{
						state.origin.x += geo.x * pState.unscaledWidth + offset.x;
						state.origin.y += geo.y * pState.unscaledHeight + offset.y;
					}
				}
				else
				{
					state.absoluteOffset.x = this.scale * offset.x;
					state.absoluteOffset.y = this.scale * offset.y;
					state.origin.x += geo.x;
					state.origin.y += geo.y;
				}
			}
	
			state.x = this.scale * (this.translate.x + state.origin.x);
			state.y = this.scale * (this.translate.y + state.origin.y);
			state.width = this.scale * geo.width;
			state.unscaledWidth = geo.width;
			state.height = this.scale * geo.height;
			state.unscaledHeight = geo.height;
			
			if (model.isVertex(state.cell))
			{
				this.updateVertexState(state, geo);
			}
			
			if (model.isEdge(state.cell))
			{
				this.updateEdgeState(state, geo);
			}
		}
	}

	state.updateCachedBounds();
};

/**
 * Function: isCellCollapsed
 * 
 * Returns true if the children of the given cell should not be visible in the
 * view. This implementation uses <mxGraph.isCellVisible> but it can be
 * overidden to use a separate condition.
 */
// 中文注释：
// 函数：isCellCollapsed
// 如果给定单元的子节点不应在视图中可见，则返回 true。
// 用途：检查单元的子节点是否应隐藏。
// 注意：默认使用 mxGraph.isCellVisible，可重写以使用其他条件。
mxGraphView.prototype.isCellCollapsed = function(cell)
{
	return this.graph.isCellCollapsed(cell);
};

/**
 * Function: updateVertexState
 * 
 * Validates the given cell state.
 */
// 中文注释：
// 函数：updateVertexState
// 验证给定的单元状态（顶点）。
// 用途：更新顶点状态的坐标和旋转。
// 关键步骤：
// 1. 如果几何是相对的且父节点不是边，应用父节点的旋转。
// 2. 调用 updateVertexLabelOffset 更新标签偏移。
// 注意：处理顶点的旋转和相对定位。
mxGraphView.prototype.updateVertexState = function(state, geo)
{
	var model = this.graph.getModel();
	var pState = this.getState(model.getParent(state.cell));
	
	if (geo.relative && pState != null && !model.isEdge(pState.cell))
	{
		var alpha = mxUtils.toRadians(pState.style[mxConstants.STYLE_ROTATION] || '0');
		
		if (alpha != 0)
		{
			var cos = Math.cos(alpha);
			var sin = Math.sin(alpha);

			var ct = new mxPoint(state.getCenterX(), state.getCenterY());
			var cx = new mxPoint(pState.getCenterX(), pState.getCenterY());
			var pt = mxUtils.getRotatedPoint(ct, cos, sin, cx);
			state.x = pt.x - state.width / 2;
			state.y = pt.y - state.height / 2;
		}
	}
	
	this.updateVertexLabelOffset(state);
};

/**
 * Function: updateEdgeState
 * 
 * Validates the given cell state.
 */
// 中文注释：
// 函数：updateEdgeState
// 验证给定的单元状态（边）。
// 用途：更新边的状态，包括终端点和路径点。
// 关键步骤：
// 1. 获取源和目标终端状态。
// 2. 如果终端无效或没有终端点，清除边状态。
// 3. 更新固定终端点、路径点和浮动终端点。
// 4. 更新边的边界和标签偏移。
// 注意：处理无效边的清除和路径点的更新。
mxGraphView.prototype.updateEdgeState = function(state, geo)
{
	var source = state.getVisibleTerminalState(true);
	var target = state.getVisibleTerminalState(false);
	
	// This will remove edges with no terminals and no terminal points
	// as such edges are invalid and produce NPEs in the edge styles.
	// Also removes connected edges that have no visible terminals.
	if ((this.graph.model.getTerminal(state.cell, true) != null && source == null) ||
		(source == null && geo.getTerminalPoint(true) == null) ||
		(this.graph.model.getTerminal(state.cell, false) != null && target == null) ||
		(target == null && geo.getTerminalPoint(false) == null))
	{
		this.clear(state.cell, true);
	}
	else
	{
		this.updateFixedTerminalPoints(state, source, target);
		this.updatePoints(state, geo.points, source, target);
		this.updateFloatingTerminalPoints(state, source, target);
		
		var pts = state.absolutePoints;
		
		if (state.cell != this.currentRoot && (pts == null || pts.length < 2 ||
			pts[0] == null || pts[pts.length - 1] == null))
		{
			// This will remove edges with invalid points from the list of states in the view.
			// Happens if the one of the terminals and the corresponding terminal point is null.
			this.clear(state.cell, true);
		}
		else
		{
			this.updateEdgeBounds(state);
			this.updateEdgeLabelOffset(state);
		}
	}
};

/**
 * Function: updateVertexLabelOffset
 * 
 * Updates the absoluteOffset of the given vertex cell state. This takes
 * into account the label position styles.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose absolute offset should be updated.
 */
// 中文注释：
// 函数：updateVertexLabelOffset
// 更新给定顶点单元状态的绝对偏移，考虑标签位置样式。
// 参数：
// state - 要更新绝对偏移的 mxCellState。
// 用途：调整顶点标签的偏移位置。
// 样式设置：
// - 根据 STYLE_LABEL_POSITION 调整水平偏移（左、中、右）。
// - 根据 STYLE_VERTICAL_LABEL_POSITION 调整垂直偏移（上、中、下）。
// 注意：处理标签宽度的缩放和对齐方式。
mxGraphView.prototype.updateVertexLabelOffset = function(state)
{
	var h = mxUtils.getValue(state.style, mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER);

	if (h == mxConstants.ALIGN_LEFT)
	{
		var lw = mxUtils.getValue(state.style, mxConstants.STYLE_LABEL_WIDTH, null);
		
		if (lw != null)
		{
			lw *= this.scale;
		}
		else
		{
			lw = state.width;
		}
		
		state.absoluteOffset.x -= lw;
	}
	else if (h == mxConstants.ALIGN_RIGHT)
	{
		state.absoluteOffset.x += state.width;
	}
	else if (h == mxConstants.ALIGN_CENTER)
	{
		var lw = mxUtils.getValue(state.style, mxConstants.STYLE_LABEL_WIDTH, null);
		
		if (lw != null)
		{
			// Aligns text block with given width inside the vertex width
			var align = mxUtils.getValue(state.style, mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
			var dx = 0;
			
			if (align == mxConstants.ALIGN_CENTER)
			{
				dx = 0.5;
			}
			else if (align == mxConstants.ALIGN_RIGHT)
			{
				dx = 1;
			}
			
			if (dx != 0)
			{
				state.absoluteOffset.x -= (lw * this.scale - state.width) * dx;
			}
		}
	}
	
	var v = mxUtils.getValue(state.style, mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
	
	if (v == mxConstants.ALIGN_TOP)
	{
		state.absoluteOffset.y -= state.height;
	}
	else if (v == mxConstants.ALIGN_BOTTOM)
	{
		state.absoluteOffset.y += state.height;
	}
};

/**
 * Function: resetValidationState
 *
 * Resets the current validation state.
 */
// 中文注释：
// 函数：resetValidationState
// 重置当前验证状态。
// 用途：清除验证过程中的临时节点引用。
// 关键步骤：
// 1. 重置 lastNode、lastHtmlNode、lastForegroundNode 和 lastForegroundHtmlNode。
mxGraphView.prototype.resetValidationState = function()
{
	this.lastNode = null;
	this.lastHtmlNode = null;
	this.lastForegroundNode = null;
	this.lastForegroundHtmlNode = null;
};

/**
 * Function: stateValidated
 * 
 * Invoked when a state has been processed in <validatePoints>. This is used
 * to update the order of the DOM nodes of the shape.
 * 
 * Parameters:
 * 
 * state - <mxCellState> that represents the cell state.
 */
// 中文注释：
// 函数：stateValidated
// 在 validatePoints 中处理完状态后调用，用于更新形状的 DOM 节点顺序。
// 参数：
// state - 表示单元状态的 mxCellState。
// 用途：调整 DOM 节点的顺序以反映正确的显示层次。
// 关键步骤：
// 1. 根据单元类型（边或顶点）和 keepEdgesInForeground/Background 设置确定节点插入位置。
// 2. 使用 cellRenderer.insertStateAfter 更新 DOM 节点顺序。
// 注意：确保边的 DOM 节点顺序与显示需求一致。
mxGraphView.prototype.stateValidated = function(state)
{
	var fg = (this.graph.getModel().isEdge(state.cell) && this.graph.keepEdgesInForeground) ||
		(this.graph.getModel().isVertex(state.cell) && this.graph.keepEdgesInBackground);
	var htmlNode = (fg) ? this.lastForegroundHtmlNode || this.lastHtmlNode : this.lastHtmlNode;
	var node = (fg) ? this.lastForegroundNode || this.lastNode : this.lastNode;
	var result = this.graph.cellRenderer.insertStateAfter(state, node, htmlNode);

	if (fg)
	{
		this.lastForegroundHtmlNode = result[1];
		this.lastForegroundNode = result[0];
	}
	else
	{
		this.lastHtmlNode = result[1];
		this.lastNode = result[0];
	}
};

/**
 * Function: updateFixedTerminalPoints
 *
 * Sets the initial absolute terminal points in the given state before the edge
 * style is computed.
 * 
 * Parameters:
 * 
 * edge - <mxCellState> whose initial terminal points should be updated.
 * source - <mxCellState> which represents the source terminal.
 * target - <mxCellState> which represents the target terminal.
 */
 // 中文注释：
// 函数：updateFixedTerminalPoints
// 在计算边样式之前，设置给定状态的初始绝对终端点。
// 参数：
// edge - 要更新初始终端点的 mxCellState。
// source - 表示源终端的 mxCellState。
// target - 表示目标终端的 mxCellState。
// 用途：为边的状态设置固定的源和目标终端点。
// 关键步骤：
// 1. 调用 updateFixedTerminalPoint 更新源终端点。
// 2. 调用 updateFixedTerminalPoint 更新目标终端点。
// 注意：使用图的连接约束（connection constraint）来确定终端点位置。
mxGraphView.prototype.updateFixedTerminalPoints = function(edge, source, target)
{
	this.updateFixedTerminalPoint(edge, source, true,
		this.graph.getConnectionConstraint(edge, source, true));
	this.updateFixedTerminalPoint(edge, target, false,
		this.graph.getConnectionConstraint(edge, target, false));
};

/**
 * Function: updateFixedTerminalPoint
 *
 * Sets the fixed source or target terminal point on the given edge.
 * 
 * Parameters:
 * 
 * edge - <mxCellState> whose terminal point should be updated.
 * terminal - <mxCellState> which represents the actual terminal.
 * source - Boolean that specifies if the terminal is the source.
 * constraint - <mxConnectionConstraint> that specifies the connection.
 */
// 中文注释：
// 函数：updateFixedTerminalPoint
// 设置给定边的固定源或目标终端点。
// 参数：
// edge - 要更新终端点的 mxCellState。
// terminal - 表示实际终端的 mxCellState。
// source - 布尔值，指示终端是否为源终端。
// constraint - 指定连接的 mxConnectionConstraint。
// 用途：更新边的固定终端点位置。
// 关键步骤：
// 1. 调用 getFixedTerminalPoint 获取终端点。
// 2. 使用 setAbsoluteTerminalPoint 设置边的绝对终端点。
// 注意：终端点基于约束或几何信息计算。
mxGraphView.prototype.updateFixedTerminalPoint = function(edge, terminal, source, constraint)
{
	edge.setAbsoluteTerminalPoint(this.getFixedTerminalPoint(edge, terminal, source, constraint), source);
};

/**
 * Function: getFixedTerminalPoint
 *
 * Returns the fixed source or target terminal point for the given edge.
 * 
 * Parameters:
 * 
 * edge - <mxCellState> whose terminal point should be returned.
 * terminal - <mxCellState> which represents the actual terminal.
 * source - Boolean that specifies if the terminal is the source.
 * constraint - <mxConnectionConstraint> that specifies the connection.
 */
// 中文注释：
// 函数：getFixedTerminalPoint
// 返回给定边的固定源或目标终端点。
// 参数：
// edge - 要返回终端点的 mxCellState。
// terminal - 表示实际终端的 mxCellState。
// source - 布尔值，指示终端是否为源终端。
// constraint - 指定连接的 mxConnectionConstraint。
// 用途：计算边的固定终端点位置。
// 关键步骤：
// 1. 如果存在约束，使用 graph.getConnectionPoint 计算终端点。
// 2. 如果没有终端或约束，使用边的几何信息计算终端点。
// 注意：考虑视图的缩放和平移对终端点坐标的影响。
mxGraphView.prototype.getFixedTerminalPoint = function(edge, terminal, source, constraint)
{
	var pt = null;
	
	if (constraint != null)
	{
		pt = this.graph.getConnectionPoint(terminal, constraint, false); // FIXME Rounding introduced bugs when calculating label positions -> , this.graph.isOrthogonal(edge));
	}
	
	if (pt == null && terminal == null)
	{
		var s = this.scale;
		var tr = this.translate;
		var orig = edge.origin;
		var geo = this.graph.getCellGeometry(edge.cell);
		pt = geo.getTerminalPoint(source);
		
		if (pt != null)
		{
			pt = new mxPoint(s * (tr.x + pt.x + orig.x),
							 s * (tr.y + pt.y + orig.y));
		}
	}
	
	return pt;
};

/**
 * Function: updateBoundsFromStencil
 * 
 * Updates the bounds of the given cell state to reflect the bounds of the stencil
 * if it has a fixed aspect and returns the previous bounds as an <mxRectangle> if
 * the bounds have been modified or null otherwise.
 * 
 * Parameters:
 * 
 * edge - <mxCellState> whose bounds should be updated.
 */
 // 中文注释：
// 函数：updateBoundsFromStencil
// 更新给定单元状态的边界以反映模板的边界（如果模板具有固定宽高比），并返回修改前的边界（mxRectangle）或 null。
// 参数：
mxGraphView.prototype.updateBoundsFromStencil = function(state)
{
	var previous = null;
	
	if (state != null && state.shape != null && state.shape.stencil != null && state.shape.stencil.aspect == 'fixed')
	{
		previous = mxRectangle.fromRectangle(state);
		var asp = state.shape.stencil.computeAspect(state.style, state.x, state.y, state.width, state.height);
		state.setRect(asp.x, asp.y, state.shape.stencil.w0 * asp.width, state.shape.stencil.h0 * asp.height);
	}
	
	return previous;
};

/**
 * Function: updatePoints
 *
 * Updates the absolute points in the given state using the specified array
 * of <mxPoints> as the relative points.
 * 
 * Parameters:
 * 
 * edge - <mxCellState> whose absolute points should be updated.
 * points - Array of <mxPoints> that constitute the relative points.
 * source - <mxCellState> that represents the source terminal.
 * target - <mxCellState> that represents the target terminal.
 */
/**
 * 函数: updatePoints
 *
 * 使用指定的<mxPoints>数组作为相对点，更新给定状态的绝对点。
 *
 * 参数：
 *
 * edge - 要更新绝对点的<mxCellState>。
 * points - 构成相对点的<mxPoints>数组。
 * source - 表示源终端的<mxCellState>。
 * target - 表示目标终端的<mxCellState>。
 *
 * 中文注释：
 * 方法目的：更新边的绝对路径点，确保边的显示符合几何和样式要求。
 * 主要功能：根据边样式或提供的相对点更新边的绝对路径点。
 * 关键步骤：
 * 1. 初始化绝对点数组，添加边的起始点。
 * 2. 根据边样式（edgeStyle）计算路径点，若无样式则使用提供的相对点。
 * 3. 如果使用模板（stencil），更新并恢复源和目标终端的边界。
 * 4. 添加边的终点并更新绝对点数组。
 * 特殊处理注意事项：当边样式或相对点不存在时，需确保路径点有效。
 */
mxGraphView.prototype.updatePoints = function(edge, points, source, target)
{
	if (edge != null)
	{
		var pts = [];
		pts.push(edge.absolutePoints[0]);
		var edgeStyle = this.getEdgeStyle(edge, points, source, target);
		
		if (edgeStyle != null)
		{
			var src = this.getTerminalPort(edge, source, true);
			var trg = this.getTerminalPort(edge, target, false);
			
			// Uses the stencil bounds for routing and restores after routing
            // 中文注释：使用模板边界进行路径计算，并在完成后恢复边界。
			var srcBounds = this.updateBoundsFromStencil(src);
			var trgBounds = this.updateBoundsFromStencil(trg);

			edgeStyle(edge, src, trg, points, pts);
			
			// Restores previous bounds
            // 中文注释：恢复源和目标终端的原始边界。
			if (srcBounds != null)
			{
				src.setRect(srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height);
			}
			
			if (trgBounds != null)
			{
				trg.setRect(trgBounds.x, trgBounds.y, trgBounds.width, trgBounds.height);
			}
		}
		else if (points != null)
		{
			for (var i = 0; i < points.length; i++)
			{
				if (points[i] != null)
				{
					var pt = mxUtils.clone(points[i]);
					pts.push(this.transformControlPoint(edge, pt));
				}
			}
		}
		
		var tmp = edge.absolutePoints;
		pts.push(tmp[tmp.length-1]);

		edge.absolutePoints = pts;
	}
};

/**
 * Function: transformControlPoint
 *
 * Transforms the given control point to an absolute point.
 */
/**
 * 函数: transformControlPoint
 *
 * 将给定的控制点转换为绝对点。
 *
 * 中文注释：
 * 方法目的：将相对控制点转换为绝对坐标，考虑视图的缩放和平移。
 * 主要功能：根据视图的变换参数计算绝对点坐标。
 * 参数：
 * - state: 表示边的<mxCellState>。
 * - pt: 要转换的相对点<mxPoint>。
 * - ignoreScale: 可选布尔值，是否忽略缩放，默认考虑缩放。
 * 关键步骤：
 * 1. 检查状态和点是否有效。
 * 2. 应用缩放和平移变换，计算绝对坐标。
 * 3. 返回转换后的绝对点或null。
 * 特殊处理注意事项：当忽略缩放时，使用原始坐标计算。
 */
mxGraphView.prototype.transformControlPoint = function(state, pt, ignoreScale)
{
	if (state != null && pt != null)
	{
		var orig = state.origin;
		var scale = ignoreScale ? 1 : this.scale
		
	    return new mxPoint(scale * (pt.x + this.translate.x + orig.x),
	    		scale * (pt.y + this.translate.y + orig.y));
	}
	
	return null;
};

/**
 * Function: isLoopStyleEnabled
 * 
 * Returns true if the given edge should be routed with <mxGraph.defaultLoopStyle>
 * or the <mxConstants.STYLE_LOOP> defined for the given edge. This implementation
 * returns true if the given edge is a loop and does not have connections constraints
 * associated.
 */
/**
 * 函数: isLoopStyleEnabled
 *
 * 如果给定边应使用<mxGraph.defaultLoopStyle>或<mxConstants.STYLE_LOOP>定义的样式进行路径计算，则返回true。此实现检查边是否为循环边且没有关联的连接约束。
 *
 * 中文注释：
 * 方法目的：判断边是否应使用循环样式进行路径计算。
 * 主要功能：检查边是否为自循环且无连接约束。
 * 参数：
 * - edge: 表示边的<mxCellState>。
 * - points: 相对点数组<mxPoints>。
 * - source: 表示源终端的<mxCellState>。
 * - target: 表示目标终端的<mxCellState>。
 * 关键步骤：
 * 1. 获取源和目标的连接约束。
 * 2. 检查点数量和样式设置，确定是否为循环边。
 * 3. 返回true表示使用循环样式，false表示不使用。
 * 重要配置参数：mxConstants.STYLE_ORTHOGONAL_LOOP控制是否使用正交循环样式。
 * 特殊处理注意事项：仅当源和目标相同且无约束时启用循环样式。
 */
mxGraphView.prototype.isLoopStyleEnabled = function(edge, points, source, target)
{
	var sc = this.graph.getConnectionConstraint(edge, source, true);
	var tc = this.graph.getConnectionConstraint(edge, target, false);
	
	if ((points == null || points.length < 2) &&
		(!mxUtils.getValue(edge.style, mxConstants.STYLE_ORTHOGONAL_LOOP, false) ||
		((sc == null || sc.point == null) && (tc == null || tc.point == null))))
	{
		return source != null && source == target;
	}
	
	return false;
};

/**
 * Function: getEdgeStyle
 * 
 * Returns the edge style function to be used to render the given edge state.
 */
/**
 * 函数: getEdgeStyle
 *
 * 返回用于渲染给定边状态的边样式函数。
 *
 * 中文注释：
 * 方法目的：获取适用于边的渲染样式函数。
 * 主要功能：根据边样式或循环样式选择合适的渲染函数。
 * 参数：
 * - edge: 表示边的<mxCellState>。
 * - points: 相对点数组<mxPoints>。
 * - source: 表示源终端的<mxCellState>。
 * - target: 表示目标终端的<mxCellState>。
 * 关键步骤：
 * 1. 检查是否启用循环样式，获取循环样式或默认样式。
 * 2. 如果边样式为字符串，尝试从<mxStyleRegistry>获取或动态评估。
 * 3. 确保返回值为函数类型，否则返回null。
 * 重要配置参数：
 * - mxConstants.STYLE_LOOP：定义循环边样式。
 * - mxConstants.STYLE_EDGE：定义普通边样式。
 * - mxConstants.STYLE_NOEDGESTYLE：禁用边样式。
 * 特殊处理注意事项：动态评估样式字符串时需启用allowEval，否则可能引发安全问题。
 */
mxGraphView.prototype.getEdgeStyle = function(edge, points, source, target)
{
	var edgeStyle = this.isLoopStyleEnabled(edge, points, source, target) ?
		mxUtils.getValue(edge.style, mxConstants.STYLE_LOOP, this.graph.defaultLoopStyle) :
		(!mxUtils.getValue(edge.style, mxConstants.STYLE_NOEDGESTYLE, false) ?
		edge.style[mxConstants.STYLE_EDGE] : null);

	// Converts string values to objects
    // 中文注释：将字符串值转换为对象。
	if (typeof(edgeStyle) == "string")
	{
		var tmp = mxStyleRegistry.getValue(edgeStyle);
		
		if (tmp == null && this.isAllowEval())
		{
 			tmp = mxUtils.eval(edgeStyle);
		}
		
		edgeStyle = tmp;
	}
	
	if (typeof(edgeStyle) == "function")
	{
		return edgeStyle;
	}
	
	return null;
};

/**
 * Function: updateFloatingTerminalPoints
 *
 * Updates the terminal points in the given state after the edge style was
 * computed for the edge.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose terminal points should be updated.
 * source - <mxCellState> that represents the source terminal.
 * target - <mxCellState> that represents the target terminal.
 */
/**
 * 函数: updateFloatingTerminalPoints
 *
 * 在计算边样式后，更新给定状态的终端点。
 *
 * 参数：
 *
 * state - 要更新终端点的<mxCellState>。
 * source - 表示源终端的<mxCellState>。
 * target - 表示目标终端的<mxCellState>。
 *
 * 中文注释：
 * 方法目的：更新边的浮动终端点，确保边连接正确。
 * 主要功能：在边样式计算后调整源和目标终端点。
 * 关键步骤：
 * 1. 检查绝对点数组的起始和结束点。
 * 2. 如果起始或结束点为空，调用updateFloatingTerminalPoint更新。
 */
mxGraphView.prototype.updateFloatingTerminalPoints = function(state, source, target)
{
	var pts = state.absolutePoints;
	var p0 = pts[0];
	var pe = pts[pts.length - 1];

	if (pe == null && target != null)
	{
		this.updateFloatingTerminalPoint(state, target, source, false);
	}
	
	if (p0 == null && source != null)
	{
		this.updateFloatingTerminalPoint(state, source, target, true);
	}
};

/**
 * Function: updateFloatingTerminalPoint
 *
 * Updates the absolute terminal point in the given state for the given
 * start and end state, where start is the source if source is true.
 * 
 * Parameters:
 * 
 * edge - <mxCellState> whose terminal point should be updated.
 * start - <mxCellState> for the terminal on "this" side of the edge.
 * end - <mxCellState> for the terminal on the other side of the edge.
 * source - Boolean indicating if start is the source terminal state.
 */
/**
 * 函数: updateFloatingTerminalPoint
 *
 * 更新给定状态的绝对终端点，start为源终端（如果source为true）。
 *
 * 参数：
 *
 * edge - 要更新终端点的<mxCellState>。
 * start - 表示边“此侧”终端的<mxCellState>。
 * end - 表示边另一侧终端的<mxCellState>。
 * source - 布尔值，指示start是否为源终端状态。
 *
 * 中文注释：
 * 方法目的：更新边的浮动终端点位置。
 * 主要功能：根据边的样式和终端状态计算浮动终端点。
 * 关键步骤：
 * 1. 调用getFloatingTerminalPoint获取浮动终端点。
 * 2. 使用setAbsoluteTerminalPoint设置绝对终端点。
 */
mxGraphView.prototype.updateFloatingTerminalPoint = function(edge, start, end, source)
{
	edge.setAbsoluteTerminalPoint(this.getFloatingTerminalPoint(edge, start, end, source), source);
};

/**
 * Function: getFloatingTerminalPoint
 * 
 * Returns the floating terminal point for the given edge, start and end
 * state, where start is the source if source is true.
 * 
 * Parameters:
 * 
 * edge - <mxCellState> whose terminal point should be returned.
 * start - <mxCellState> for the terminal on "this" side of the edge.
 * end - <mxCellState> for the terminal on the other side of the edge.
 * source - Boolean indicating if start is the source terminal state.
 */
/**
 * 函数: getFloatingTerminalPoint
 *
 * 返回给定边的浮动终端点，start为源终端（如果source为true）。
 *
 * 参数：
 *
 * edge - 要返回终端点的<mxCellState>。
 * start - 表示边“此侧”终端的<mxCellState>。
 * end - 表示边另一侧终端的<mxCellState>。
 * source - 布尔值，指示start是否为源终端状态。
 *
 * 中文注释：
 * 方法目的：计算边的浮动终端点坐标。
 * 主要功能：根据终端状态和样式计算浮动连接点。
 * 关键步骤：
 * 1. 获取终端端口（如果适用）。
 * 2. 获取下一路径点。
 * 3. 应用旋转和边界间距调整。
 * 4. 调用getPerimeterPoint计算连接点。
 * 5. 如果有旋转，调整点坐标。
 * 样式设置：
 * - mxConstants.STYLE_PERIMETER_SPACING：定义边界间距。
 * - mxConstants.STYLE_SOURCE_PERIMETER_SPACING：源终端边界间距。
 * - mxConstants.STYLE_TARGET_PERIMETER_SPACING：目标终端边界间距。
 * 特殊处理注意事项：考虑正交边和旋转对终端点的影响。
 */
mxGraphView.prototype.getFloatingTerminalPoint = function(edge, start, end, source)
{
	start = this.getTerminalPort(edge, start, source);
	var next = this.getNextPoint(edge, end, source);
	
	var orth = this.graph.isOrthogonal(edge);
	var alpha = mxUtils.toRadians(Number(start.style[mxConstants.STYLE_ROTATION] || '0'));
	var center = new mxPoint(start.getCenterX(), start.getCenterY());
	
	if (alpha != 0)
	{
		var cos = Math.cos(-alpha);
		var sin = Math.sin(-alpha);
		next = mxUtils.getRotatedPoint(next, cos, sin, center);
	}
	
	var border = parseFloat(edge.style[mxConstants.STYLE_PERIMETER_SPACING] || 0);
	border += parseFloat(edge.style[(source) ?
		mxConstants.STYLE_SOURCE_PERIMETER_SPACING :
		mxConstants.STYLE_TARGET_PERIMETER_SPACING] || 0);
	var pt = this.getPerimeterPoint(start, next, alpha == 0 && orth, border);

	if (alpha != 0)
	{
		var cos = Math.cos(alpha);
		var sin = Math.sin(alpha);
		pt = mxUtils.getRotatedPoint(pt, cos, sin, center);
	}

	return pt;
};

/**
 * Function: getTerminalPort
 * 
 * Returns an <mxCellState> that represents the source or target terminal or
 * port for the given edge.
 * 
 * Parameters:
 * 
 * state - <mxCellState> that represents the state of the edge.
 * terminal - <mxCellState> that represents the terminal.
 * source - Boolean indicating if the given terminal is the source terminal.
 */
/**
 * 函数: getTerminalPort
 *
 * 返回表示给定边的源或目标终端或端口的<mxCellState>。
 *
 * 参数：
 *
 * state - 表示边状态的<mxCellState>。
 * terminal - 表示终端的<mxCellState>。
 * source - 布尔值，指示给定终端是否为源终端。
 *
 * 中文注释：
 * 方法目的：获取边的源或目标端口状态。
 * 主要功能：根据样式中的端口ID返回对应的终端状态。
 * 关键步骤：
 * 1. 从样式中获取源或目标端口ID。
 * 2. 如果存在端口ID，获取对应的单元状态。
 * 3. 如果端口状态有效，替换终端状态。
 * 重要配置参数：
 * - mxConstants.STYLE_SOURCE_PORT：源端口样式键。
 * - mxConstants.STYLE_TARGET_PORT：目标端口样式键。
 */
mxGraphView.prototype.getTerminalPort = function(state, terminal, source)
{
	var key = (source) ? mxConstants.STYLE_SOURCE_PORT :
		mxConstants.STYLE_TARGET_PORT;
	var id = mxUtils.getValue(state.style, key);
	
	if (id != null)
	{
		var tmp = this.getState(this.graph.getModel().getCell(id));
		
		// Only uses ports where a cell state exists
		if (tmp != null)
		{
			terminal = tmp;
		}
	}
	
	return terminal;
};

/**
 * Function: getPerimeterPoint
 *
 * Returns an <mxPoint> that defines the location of the intersection point between
 * the perimeter and the line between the center of the shape and the given point.
 * 
 * Parameters:
 * 
 * terminal - <mxCellState> for the source or target terminal.
 * next - <mxPoint> that lies outside of the given terminal.
 * orthogonal - Boolean that specifies if the orthogonal projection onto
 * the perimeter should be returned. If this is false then the intersection
 * of the perimeter and the line between the next and the center point is
 * returned.
 * border - Optional border between the perimeter and the shape.
 */
/**
 * 函数: getPerimeterPoint
 *
 * 返回定义形状周界与形状中心到给定点之间的线交点的<mxPoint>。
 *
 * 参数：
 *
 * terminal - 表示源或目标终端的<mxCellState>。
 * next - 位于终端外的<mxPoint>。
 * orthogonal - 布尔值，指定是否返回周界的正交投影。如果为false，返回周界与中心到next的线的交点。
 * border - 可选的周界与形状之间的边界间距。
 *
 * 中文注释：
 * 方法目的：计算终端周界与外部点的交点。
 * 主要功能：根据周界函数和样式计算连接点坐标。
 * 关键步骤：
 * 1. 获取周界函数。
 * 2. 计算周界边界，考虑间距。
 * 3. 应用翻转（flip）样式调整坐标。
 * 4. 调用周界函数计算交点。
 * 5. 恢复翻转调整。
 * 样式设置：
 * - mxConstants.STYLE_FLIPH：水平翻转。
 * - mxConstants.STYLE_FLIPV：垂直翻转。
 * - stencilFlipH/V：模板特定的翻转样式。
 * 特殊处理注意事项：处理翻转样式以确保正确坐标计算。
 */
mxGraphView.prototype.getPerimeterPoint = function(terminal, next, orthogonal, border)
{
	var point = null;
	
	if (terminal != null)
	{
		var perimeter = this.getPerimeterFunction(terminal);
		
		if (perimeter != null && next != null)
		{
			var bounds = this.getPerimeterBounds(terminal, border);

			if (bounds.width > 0 || bounds.height > 0)
			{
				point = new mxPoint(next.x, next.y);
				var flipH = false;
				var flipV = false;	
				
				if (this.graph.model.isVertex(terminal.cell))
				{
					flipH = mxUtils.getValue(terminal.style, mxConstants.STYLE_FLIPH, 0) == 1;
					flipV = mxUtils.getValue(terminal.style, mxConstants.STYLE_FLIPV, 0) == 1;	
	
					// Legacy support for stencilFlipH/V
                    // 中文注释：支持旧版stencilFlipH/V样式。
					if (terminal.shape != null && terminal.shape.stencil != null)
					{
						flipH = (mxUtils.getValue(terminal.style, 'stencilFlipH', 0) == 1) || flipH;
						flipV = (mxUtils.getValue(terminal.style, 'stencilFlipV', 0) == 1) || flipV;
					}
	
					if (flipH)
					{
						point.x = 2 * bounds.getCenterX() - point.x;
					}
					
					if (flipV)
					{
						point.y = 2 * bounds.getCenterY() - point.y;
					}
				}
				
				point = perimeter(bounds, terminal, point, orthogonal);

				if (point != null)
				{
					if (flipH)
					{
						point.x = 2 * bounds.getCenterX() - point.x;
					}
					
					if (flipV)
					{
						point.y = 2 * bounds.getCenterY() - point.y;
					}
				}
			}
		}
		
		if (point == null)
		{
			point = this.getPoint(terminal);
		}
	}
	
	return point;
};

/**
 * Function: getRoutingCenterX
 * 
 * Returns the x-coordinate of the center point for automatic routing.
 */
/**
 * 函数: getRoutingCenterX
 *
 * 返回自动路径计算的X中心点坐标。
 *
 * 中文注释：
 * 方法目的：提供用于自动路径计算的X中心点。
 * 主要功能：根据样式调整计算终端的X中心坐标。
 * 关键步骤：
 * 1. 从样式获取路由中心偏移（STYLE_ROUTING_CENTER_X）。
 * 2. 计算中心点X坐标加上偏移量。
 * 样式设置：
 * - mxConstants.STYLE_ROUTING_CENTER_X：定义X方向路由中心偏移。
 */
mxGraphView.prototype.getRoutingCenterX = function (state)
{
	var f = (state.style != null) ? parseFloat(state.style
		[mxConstants.STYLE_ROUTING_CENTER_X]) || 0 : 0;

	return state.getCenterX() + f * state.width;
};

/**
 * Function: getRoutingCenterY
 * 
 * Returns the y-coordinate of the center point for automatic routing.
 */
/**
 * 函数: getRoutingCenterY
 *
 * 返回自动路径计算的Y中心点坐标。
 *
 * 中文注释：
 * 方法目的：提供用于自动路径计算的Y中心点。
 * 主要功能：根据样式调整计算终端的Y中心坐标。
 * 关键步骤：
 * 1. 从样式获取路由中心偏移（STYLE_ROUTING_CENTER_Y）。
 * 2. 计算中心点Y坐标加上偏移量。
 * 样式设置：
 * - mxConstants.STYLE_ROUTING_CENTER_Y：定义Y方向路由中心偏移。
 */
mxGraphView.prototype.getRoutingCenterY = function (state)
{
	var f = (state.style != null) ? parseFloat(state.style
		[mxConstants.STYLE_ROUTING_CENTER_Y]) || 0 : 0;

	return state.getCenterY() + f * state.height;
};

/**
 * Function: getPerimeterBounds
 *
 * Returns the perimeter bounds for the given terminal, edge pair as an
 * <mxRectangle>.
 * 
 * If you have a model where each terminal has a relative child that should
 * act as the graphical endpoint for a connection from/to the terminal, then
 * this method can be replaced as follows:
 * 
 * (code)
 * var oldGetPerimeterBounds = mxGraphView.prototype.getPerimeterBounds;
 * mxGraphView.prototype.getPerimeterBounds = function(terminal, edge, isSource)
 * {
 *   var model = this.graph.getModel();
 *   var childCount = model.getChildCount(terminal.cell);
 * 
 *   if (childCount > 0)
 *   {
 *     var child = model.getChildAt(terminal.cell, 0);
 *     var geo = model.getGeometry(child);
 *
 *     if (geo != null &&
 *         geo.relative)
 *     {
 *       var state = this.getState(child);
 *       
 *       if (state != null)
 *       {
 *         terminal = state;
 *       }
 *     }
 *   }
 *   
 *   return oldGetPerimeterBounds.apply(this, arguments);
 * };
 * (end)
 * 
 * Parameters:
 * 
 * terminal - <mxCellState> that represents the terminal.
 * border - Number that adds a border between the shape and the perimeter.
 */
/**
 * 函数: getPerimeterBounds
 *
 * 返回给定终端和边的周界边界，作为<mxRectangle>。
 *
 * 如果模型中每个终端有一个相对子节点作为连接的图形端点，可以按以下方式替换此方法：
 *
 * (code)
 * var oldGetPerimeterBounds = mxGraphView.prototype.getPerimeterBounds;
 * mxGraphView.prototype.getPerimeterBounds = function(terminal, edge, isSource)
 * {
 *   var model = this.graph.getModel();
 *   var childCount = model.getChildCount(terminal.cell);
 *
 *   if (childCount > 0)
 *   {
 *     var child = model.getChildAt(terminal.cell, 0);
 *     var geo = model.getGeometry(child);
 *
 *     if (geo != null &&
 *         geo.relative)
 *     {
 *       var state = this.getState(child);
 *
 *       if (state != null)
 *       {
 *         terminal = state;
 *       }
 *     }
 *   }
 *
 *   return oldGetPerimeterBounds.apply(this, arguments);
 * };
 * (end)
 *
 * 参数：
 *
 * terminal - 表示终端的<mxCellState>。
 * border - 形状与周界之间的边界间距。
 *
 * 中文注释：
 * 方法目的：计算终端的周界边界。
 * 主要功能：返回考虑边界间距的周界矩形。
 * 关键步骤：
 * 1. 从样式获取周界间距并与传入的边界值相加。
 * 2. 调用终端的getPerimeterBounds方法获取边界。
 * 样式设置：
 * - mxConstants.STYLE_PERIMETER_SPACING：定义周界间距。
 * 特殊处理注意事项：提供示例代码支持相对子节点作为连接端点。
 */
mxGraphView.prototype.getPerimeterBounds = function(terminal, border)
{
	border = (border != null) ? border : 0;

	if (terminal != null)
	{
		border += parseFloat(terminal.style[mxConstants.STYLE_PERIMETER_SPACING] || 0);
	}

	return terminal.getPerimeterBounds(border * this.scale);
};

/**
 * Function: getPerimeterFunction
 *
 * Returns the perimeter function for the given state.
 */
/**
 * 函数: getPerimeterFunction
 *
 * 返回给定状态的周界函数。
 *
 * 中文注释：
 * 方法目的：获取用于计算周界点的函数。
 * 主要功能：根据样式返回周界函数。
 * 关键步骤：
 * 1. 从样式获取周界函数名称。
 * 2. 如果是字符串，尝试从<mxStyleRegistry>获取或动态评估。
 * 3. 确保返回值为函数类型。
 * 重要配置参数：
 * - mxConstants.STYLE_PERIMETER：定义周界函数。
 * 特殊处理注意事项：动态评估需启用allowEval，否则可能引发安全问题。
 */
mxGraphView.prototype.getPerimeterFunction = function(state)
{
	var perimeter = state.style[mxConstants.STYLE_PERIMETER];

	// Converts string values to objects
    // 中文注释：将字符串值转换为对象。
	if (typeof(perimeter) == "string")
	{
		var tmp = mxStyleRegistry.getValue(perimeter);
		
		if (tmp == null && this.isAllowEval())
		{
 			tmp = mxUtils.eval(perimeter);
		}

		perimeter = tmp;
	}
	
	if (typeof(perimeter) == "function")
	{
		return perimeter;
	}
	
	return null;
};

/**
 * Function: getNextPoint
 *
 * Returns the nearest point in the list of absolute points or the center
 * of the opposite terminal.
 * 
 * Parameters:
 * 
 * edge - <mxCellState> that represents the edge.
 * opposite - <mxCellState> that represents the opposite terminal.
 * source - Boolean indicating if the next point for the source or target
 * should be returned.
 */
/**
 * 函数: getNextPoint
 *
 * 返回绝对点列表中的最近点或对侧终端的中心点。
 *
 * 参数：
 *
 * edge - 表示边的<mxCellState>。
 * opposite - 表示对侧终端的<mxCellState>。
 * source - 布尔值，指示返回源或目标的下一个点。
 *
 * 中文注释：
 * 方法目的：获取边的下一个路径点。
 * 主要功能：返回绝对点列表中的最近点或对侧终端中心。
 * 关键步骤：
 * 1. 如果存在绝对点列表，返回源或目标的下一个点。
 * 2. 如果没有点，返回对侧终端的中心点。
 */
mxGraphView.prototype.getNextPoint = function(edge, opposite, source)
{
	var pts = edge.absolutePoints;
	var point = null;
	
	if (pts != null && pts.length >= 2)
	{
		var count = pts.length;
		point = pts[(source) ? Math.min(1, count - 1) : Math.max(0, count - 2)];
	}
	
	if (point == null && opposite != null)
	{
		point = new mxPoint(opposite.getCenterX(), opposite.getCenterY());
	}
	
	return point;
};

/**
 * Function: getVisibleTerminal
 *
 * Returns the nearest ancestor terminal that is visible. The edge appears
 * to be connected to this terminal on the display. The result of this method
 * is cached in <mxCellState.getVisibleTerminalState>.
 * 
 * Parameters:
 * 
 * edge - <mxCell> whose visible terminal should be returned.
 * source - Boolean that specifies if the source or target terminal
 * should be returned.
 */
/**
 * 函数: getVisibleTerminal
 *
 * 返回最近的可见祖先终端。边在显示上似乎连接到此终端。此方法的结果缓存到<mxCellState.getVisibleTerminalState>。
 *
 * 参数：
 *
 * edge - 要返回可见终端的<mxCell>。
 * source - 布尔值，指定返回源或目标终端。
 *
 * 中文注释：
 * 方法目的：获取边的可见终端。
 * 主要功能：查找最近的可见祖先终端并缓存结果。
 * 关键步骤：
 * 1. 获取边的源或目标终端。
 * 2. 遍历祖先，找到最近的可见终端。
 * 3. 如果终端无效（不在模型中或为根），返回null。
 * 特殊处理注意事项：考虑单元的可见性和折叠状态。
 */
mxGraphView.prototype.getVisibleTerminal = function(edge, source)
{
	var model = this.graph.getModel();
	var result = model.getTerminal(edge, source);
	var best = result;
	
	while (result != null && result != this.currentRoot)
	{
		if (!this.graph.isCellVisible(best) || this.isCellCollapsed(result))
		{
			best = result;
		}
		
		result = model.getParent(result);
	}

	// Checks if the result is valid for the current view state
    // 中文注释：检查结果是否对当前视图状态有效。
	if (best != null && (!model.contains(best) ||
		model.getParent(best) == model.getRoot() ||
		best == this.currentRoot))
	{
		best = null;
	}
	
	return best;
};

/**
 * Function: updateEdgeBounds
 *
 * Updates the given state using the bounding box of t
 * he absolute points.
 * Also updates <mxCellState.terminalDistance>, <mxCellState.length> and
 * <mxCellState.segments>.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose bounds should be updated.
 */
/**
 * 函数: updateEdgeBounds
 *
 * 使用绝对点的边界框更新给定状态。
 * 同时更新<mxCellState.terminalDistance>、<mxCellState.length>和<mxCellState.segments>。
 *
 * 参数：
 *
 * state - 要更新边界的<mxCellState>。
 *
 * 中文注释：
 * 方法目的：更新边的边界和相关属性。
 * 主要功能：计算边的边界、终端距离、总长度和段长度。
 * 关键步骤：
 * 1. 计算终端点之间的距离。
 * 2. 遍历绝对点，计算每段长度和边界框。
 * 3. 更新状态的坐标、尺寸、长度和段数组。
 * 特殊处理注意事项：考虑标记大小（markerSize）对边界的微调。
 */
mxGraphView.prototype.updateEdgeBounds = function(state)
{
	var points = state.absolutePoints;
	var p0 = points[0];
	var pe = points[points.length - 1];
	
	if (p0.x != pe.x || p0.y != pe.y)
	{
		var dx = pe.x - p0.x;
		var dy = pe.y - p0.y;
		state.terminalDistance = Math.sqrt(dx * dx + dy * dy);
	}
	else
	{
		state.terminalDistance = 0;
	}
	
	var length = 0;
	var segments = [];
	var pt = p0;
	
	if (pt != null)
	{
		var minX = pt.x;
		var minY = pt.y;
		var maxX = minX;
		var maxY = minY;
		
		for (var i = 1; i < points.length; i++)
		{
			var tmp = points[i];
			
			if (tmp != null)
			{
				var dx = pt.x - tmp.x;
				var dy = pt.y - tmp.y;
				
				var segment = Math.sqrt(dx * dx + dy * dy);
				segments.push(segment);
				length += segment;
				
				pt = tmp;
				
				minX = Math.min(pt.x, minX);
				minY = Math.min(pt.y, minY);
				maxX = Math.max(pt.x, maxX);
				maxY = Math.max(pt.y, maxY);
			}
		}
		
		state.length = length;
		state.segments = segments;
		
		var markerSize = 1; // TODO: include marker size
        // 中文注释：标记大小待处理，影响边界计算。

		state.x = minX;
		state.y = minY;
		state.width = Math.max(markerSize, maxX - minX);
		state.height = Math.max(markerSize, maxY - minY);
	}
};

/**
 * Function: getPoint
 *
 * Returns the absolute point on the edge for the given relative
 * <mxGeometry> as an <mxPoint>. The edge is represented by the given
 * <mxCellState>.
 * 
 * Parameters:
 * 
 * state - <mxCellState> that represents the state of the parent edge.
 * geometry - <mxGeometry> that represents the relative location.
 */
/**
 * 函数: getPoint
 *
 * 返回给定相对<mxGeometry>在边上的绝对点，作为<mxPoint>。边由给定的<mxCellState>表示。
 *
 * 参数：
 *
 * state - 表示父边状态的<mxCellState>。
 * geometry - 表示相对位置的<mxGeometry>。
 *
 * 中文注释：
 * 方法目的：计算边上指定相对位置的绝对点。
 * 主要功能：根据几何信息和边状态计算绝对坐标。
 * 关键步骤：
 * 1. 从状态获取中心点。
 * 2. 如果几何为相对位置，基于段长度和偏移计算点。
 * 3. 如果几何为绝对位置，直接应用偏移。
 * 4. 返回计算的绝对点。
 */
mxGraphView.prototype.getPoint = function(state, geometry)
{
	var x = state.getCenterX();
	var y = state.getCenterY();
	
	if (state.segments != null && (geometry == null || geometry.relative))
	{
		var gx = (geometry != null) ? geometry.x / 2 : 0;
		var pointCount = state.absolutePoints.length;
		var dist = Math.round((gx + 0.5) * state.length);
		var segment = state.segments[0];
		var length = 0;				
		var index = 1;

		while (dist >= Math.round(length + segment) && index < pointCount - 1)
		{
			length += segment;
			segment = state.segments[index++];
		}

		var factor = (segment == 0) ? 0 : (dist - length) / segment;
		var p0 = state.absolutePoints[index-1];
		var pe = state.absolutePoints[index];

		if (p0 != null && pe != null)
		{
			var gy = 0;
			var offsetX = 0;
			var offsetY = 0;

			if (geometry != null)
			{
				gy = geometry.y;
				var offset = geometry.offset;
				
				if (offset != null)
				{
					offsetX = offset.x;
					offsetY = offset.y;
				}
			}

			var dx = pe.x - p0.x;
			var dy = pe.y - p0.y;
			var nx = (segment == 0) ? 0 : dy / segment;
			var ny = (segment == 0) ? 0 : dx / segment;
			
			x = p0.x + dx * factor + (nx * gy + offsetX) * this.scale;
			y = p0.y + dy * factor - (ny * gy - offsetY) * this.scale;
		}
	}
	else if (geometry != null)
	{
		var offset = geometry.offset;
		
		if (offset != null)
		{
			x += offset.x;
			y += offset.y;
		}
	}
	
	return new mxPoint(x, y);		
};

/**
 * Function: getRelativePoint
 *
 * Gets the relative point that describes the given, absolute label
 * position for the given edge state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> that represents the state of the parent edge.
 * x - Specifies the x-coordinate of the absolute label location.
 * y - Specifies the y-coordinate of the absolute label location.
 */
/**
 * 函数: getRelativePoint
 *
 * 获取描述给定绝对标签位置的相对点，对于给定的边状态。
 *
 * 参数：
 *
 * state - 表示父边状态的<mxCellState>。
 * x - 指定绝对标签位置的X坐标。
 * y - 指定绝对标签位置的Y坐标。
 *
 * 中文注释：
 * 方法目的：将绝对标签位置转换为相对点。
 * 主要功能：计算相对于边路径的标签位置。
 * 关键步骤：
 * 1. 检查几何是否为相对且点数量足够。
 * 2. 找到最近的线段。
 * 3. 计算投影长度和方向。
 * 4. 返回相对点坐标。
 * 特殊处理注意事项：处理零长度段以避免除零错误。
 */
mxGraphView.prototype.getRelativePoint = function(edgeState, x, y)
{
	var model = this.graph.getModel();
	var geometry = model.getGeometry(edgeState.cell);
	
	if (geometry != null)
	{
		var pointCount = edgeState.absolutePoints.length;
		
		if (geometry.relative && pointCount > 1)
		{
			var totalLength = edgeState.length;
			var segments = edgeState.segments;

			// Works which line segment the point of the label is closest to
			var p0 = edgeState.absolutePoints[0];
			var pe = edgeState.absolutePoints[1];
			var minDist = mxUtils.ptSegDistSq(p0.x, p0.y, pe.x, pe.y, x, y);

			var index = 0;
			var tmp = 0;
			var length = 0;
			
			for (var i = 2; i < pointCount; i++)
			{
				tmp += segments[i - 2];
				pe = edgeState.absolutePoints[i];
				var dist = mxUtils.ptSegDistSq(p0.x, p0.y, pe.x, pe.y, x, y);

				if (dist <= minDist)
				{
					minDist = dist;
					index = i - 1;
					length = tmp;
				}
				
				p0 = pe;
			}
			
			var seg = segments[index];
			p0 = edgeState.absolutePoints[index];
			pe = edgeState.absolutePoints[index + 1];
			
			var x2 = p0.x;
			var y2 = p0.y;
			
			var x1 = pe.x;
			var y1 = pe.y;
			
			var px = x;
			var py = y;
			
			var xSegment = x2 - x1;
			var ySegment = y2 - y1;
			
			px -= x1;
			py -= y1;
			var projlenSq = 0;
			
			px = xSegment - px;
			py = ySegment - py;
			var dotprod = px * xSegment + py * ySegment;

			if (dotprod <= 0.0)
			{
				projlenSq = 0;
			}
			else
			{
				projlenSq = dotprod * dotprod
						/ (xSegment * xSegment + ySegment * ySegment);
			}

			var projlen = Math.sqrt(projlenSq);

			if (projlen > seg)
			{
				projlen = seg;
			}

			var yDistance = Math.sqrt(mxUtils.ptSegDistSq(p0.x, p0.y, pe
					.x, pe.y, x, y));
			var direction = mxUtils.relativeCcw(p0.x, p0.y, pe.x, pe.y, x, y);

			if (direction == -1)
			{
				yDistance = -yDistance;
			}

			// Constructs the relative point for the label
            // 中文注释：构造标签的相对点。
			return new mxPoint(((totalLength / 2 - length - projlen) / totalLength) * -2,
						yDistance / this.scale);
		}
	}
	
	return new mxPoint();
};

/**
 * Function: updateEdgeLabelOffset
 *
 * Updates <mxCellState.absoluteOffset> for the given state. The absolute
 * offset is normally used for the position of the edge label. Is is
 * calculated from the geometry as an absolute offset from the center
 * between the two endpoints if the geometry is absolute, or as the
 * relative distance between the center along the line and the absolute
 * orthogonal distance if the geometry is relative.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose absolute offset should be updated.
 */
/**
 * 函数: updateEdgeLabelOffset
 *
 * 更新给定状态的<mxCellState.absoluteOffset>。绝对偏移通常用于边标签的位置。如果几何为绝对，则从两端点之间的中心计算绝对偏移；如果几何为相对，则计算沿线的中心相对距离和绝对正交距离。
 *
 * 参数：
 *
 * state - 要更新绝对偏移的<mxCellState>。
 *
 * 中文注释：
 * 方法目的：更新边标签的绝对偏移位置。
 * 主要功能：根据几何信息计算标签的显示位置。
 * 关键步骤：
 * 1. 获取绝对点数组。
 * 2. 如果几何为相对，使用getPoint计算偏移。
 * 3. 如果几何为绝对，计算两端点中心并应用偏移。
 * 特殊处理注意事项：处理绝对和相对几何的不同逻辑。
 */
mxGraphView.prototype.updateEdgeLabelOffset = function(state)
{
	var points = state.absolutePoints;
	
	state.absoluteOffset.x = state.getCenterX();
	state.absoluteOffset.y = state.getCenterY();

	if (points != null && points.length > 0 && state.segments != null)
	{
		var geometry = this.graph.getCellGeometry(state.cell);
		
		if (geometry.relative)
		{
			var offset = this.getPoint(state, geometry);
			
			if (offset != null)
			{
				state.absoluteOffset = offset;
			}
		}
		else
		{
			var p0 = points[0];
			var pe = points[points.length - 1];
			
			if (p0 != null && pe != null)
			{
				var dx = pe.x - p0.x;
				var dy = pe.y - p0.y;
				var x0 = 0;
				var y0 = 0;

				var off = geometry.offset;
				
				if (off != null)
				{
					x0 = off.x;
					y0 = off.y;
				}
				
				var x = p0.x + dx / 2 + x0 * this.scale;
				var y = p0.y + dy / 2 + y0 * this.scale;
				
				state.absoluteOffset.x = x;
				state.absoluteOffset.y = y;
			}
		}
	}
};

/**
 * Function: getState
 *
 * Returns the <mxCellState> for the given cell. If create is true, then
 * the state is created if it does not yet exist.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which the <mxCellState> should be returned.
 * create - Optional boolean indicating if a new state should be created
 * if it does not yet exist. Default is false.
 */
/**
 * 函数: getState
 *
 * 返回给定单元的<mxCellState>。如果create为true，则在状态不存在时创建。
 *
 * 参数：
 *
 * cell - 要返回<mxCellState>的<mxCell>。
 * create - 可选布尔值，指示是否创建新状态。默认为false。
 *
 * 中文注释：
 * 方法目的：获取或创建单元的状态。
 * 主要功能：从状态字典获取单元状态，或根据需要创建新状态。
 * 关键步骤：
 * 1. 从states字典获取状态。
 * 2. 如果create为true且状态不存在或需更新样式，创建或更新状态。
 * 重要配置参数：
 * - updateStyle：控制是否在每次获取时更新样式。
 */
mxGraphView.prototype.getState = function(cell, create)
{
	create = create || false;
	var state = null;
	
	if (cell != null)
	{
		state = this.states.get(cell);
		
		if (create && (state == null || this.updateStyle) && this.graph.isCellVisible(cell))
		{
			if (state == null)
			{
				state = this.createState(cell);
				this.states.put(cell, state);
			}
			else
			{
				state.style = this.graph.getCellStyle(cell);
			}
		}
	}

	return state;
};

/**
 * Function: isRendering
 *
 * Returns <rendering>.
 */
/**
 * 函数: isRendering
 *
 * 返回<rendering>。
 *
 * 中文注释：
 * 方法目的：获取rendering配置值。
 * 主要功能：提供对是否启用渲染的访问。
 */
mxGraphView.prototype.isRendering = function()
{
	return this.rendering;
};

/**
 * Function: setRendering
 *
 * Sets <rendering>.
 */
/**
 * 函数: setRendering
 *
 * 设置<rendering>。
 *
 * 中文注释：
 * 方法目的：设置rendering配置值。
 * 主要功能：控制是否通过<mxCellRenderer>进行形状渲染。
 * 参数：
 * - value: 布尔值，指定是否启用渲染。
 */
mxGraphView.prototype.setRendering = function(value)
{
	this.rendering = value;
};

/**
 * Function: isAllowEval
 *
 * Returns <allowEval>.
 */
/**
 * 函数: isAllowEval
 *
 * 返回<allowEval>。
 *
 * 中文注释：
 * 方法目的：获取allowEval配置值。
 * 主要功能：提供对是否允许动态评估样式的访问。
 */
mxGraphView.prototype.isAllowEval = function()
{
	return this.allowEval;
};

/**
 * Function: setAllowEval
 *
 * Sets <allowEval>.
 */
/**
 * 函数: setAllowEval
 *
 * 设置<allowEval>。
 *
 * 中文注释：
 * 方法目的：设置allowEval配置值。
 * 主要功能：控制是否允许动态评估样式字符串。
 * 参数：
 * - value: 布尔值，指定是否启用动态评估。
 * 特殊处理注意事项：启用可能引发安全风险，需谨慎设置。
 */
mxGraphView.prototype.setAllowEval = function(value)
{
	this.allowEval = value;
};

/**
 * Function: getStates
 *
 * Returns <states>.
 */
/**
 * 函数: getStates
 *
 * 返回<states>。
 *
 * 中文注释：
 * 方法目的：获取状态字典。
 * 主要功能：提供对单元ID到状态映射的访问。
 */
mxGraphView.prototype.getStates = function()
{
	return this.states;
};

/**
 * Function: setStates
 *
 * Sets <states>.
 */
/**
 * 函数: setStates
 *
 * 设置<states>。
 *
 * 中文注释：
 * 方法目的：设置状态字典。
 * 主要功能：更新单元ID到状态的映射。
 * 参数：
 * - value: 新的<mxDictionary>对象。
 */
mxGraphView.prototype.setStates = function(value)
{
	this.states = value;
};

/**
 * Function: getCellStates
 *
 * Returns the <mxCellStates> for the given array of <mxCells>. The array
 * contains all states that are not null, that is, the returned array may
 * have less elements than the given array. If no argument is given, then
 * this returns <states>.
 */
/**
 * 函数: getCellStates
 *
 * 返回给定<mxCells>数组的<mxCellStates>。返回数组只包含非空状态，可能少于输入数组的元素。如果未提供参数，返回<states>。
 *
 * 中文注释：
 * 方法目的：获取指定单元的状态数组。
 * 主要功能：返回给定单元的非空状态，或整个状态字典。
 * 参数：
 * - cells: 可选的<mxCells>数组。
 * 关键步骤：
 * 1. 如果未提供单元数组，返回整个states字典。
 * 2. 遍历单元数组，收集非空状态。
 */
mxGraphView.prototype.getCellStates = function(cells)
{
	if (cells == null)
	{
		return this.states;
	}
	else
	{
		var result = [];
		
		for (var i = 0; i < cells.length; i++)
		{
			var state = this.getState(cells[i]);
			
			if (state != null)
			{
				result.push(state);
			}
		}
		
		return result;
	}
};

/**
 * Function: removeState
 *
 * Removes and returns the <mxCellState> for the given cell.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which the <mxCellState> should be removed.
 */
/**
 * 函数: removeState
 *
 * 移除并返回给定单元的<mxCellState>。
 *
 * 参数：
 *
 * cell - 要移除<mxCellState>的<mxCell>。
 *
 * 中文注释：
 * 方法目的：移除并返回指定单元的状态。
 * 主要功能：从状态字典中移除单元状态并销毁。
 * 关键步骤：
 * 1. 从states字典移除状态。
 * 2. 使用cellRenderer销毁状态。
 * 3. 标记状态为无效并调用destroy方法。
 */
mxGraphView.prototype.removeState = function(cell)
{
	var state = null;
	
	if (cell != null)
	{
		state = this.states.remove(cell);
		
		if (state != null)
		{
			this.graph.cellRenderer.destroy(state);
			state.invalid = true;
			state.destroy();
		}
	}
	
	return state;
};

/**
 * Function: createState
 *
 * Creates and returns an <mxCellState> for the given cell and initializes
 * it using <mxCellRenderer.initialize>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which a new <mxCellState> should be created.
 */
/**
 * 函数: createState
 *
 * 为给定单元创建并返回<mxCellState>，并使用<mxCellRenderer.initialize>初始化。
 *
 * 参数：
 *
 * cell - 要创建新<mxCellState>的<mxCell>。
 *
 * 中文注释：
 * 方法目的：创建并初始化单元状态。
 * 主要功能：生成新的单元状态对象并设置样式。
 * 关键步骤：
 * 1. 创建<mxCellState>对象。
 * 2. 使用图的样式初始化状态。
 */
mxGraphView.prototype.createState = function(cell)
{
	return new mxCellState(this, cell, this.graph.getCellStyle(cell));
};

/**
 * Function: getCanvas
 *
 * Returns the DOM node that contains the background-, draw- and
 * overlay- and decoratorpanes.
 */
/**
 * 函数: getCanvas
 *
 * 返回包含背景、绘制、覆盖和装饰面板的DOM节点。
 *
 * 中文注释：
 * 方法目的：获取主画布DOM节点。
 * 主要功能：提供对包含所有绘制层的画布节点的访问。
 */
mxGraphView.prototype.getCanvas = function()
{
	return this.canvas;
};

/**
 * Function: getBackgroundPane
 *
 * Returns the DOM node that represents the background layer.
 */
/**
 * 函数: getBackgroundPane
 *
 * 返回表示背景层的DOM节点。
 *
 * 中文注释：
 * 方法目的：获取背景层DOM节点。
 * 主要功能：提供对背景绘制层的访问。
 */
mxGraphView.prototype.getBackgroundPane = function()
{
	return this.backgroundPane;
};

/**
 * Function: getDrawPane
 *
 * Returns the DOM node that represents the main drawing layer.
 */
/**
 * 函数: getDrawPane
 *
 * 返回表示主绘制层的DOM节点。
 *
 * 中文注释：
 * 方法目的：获取主绘制层DOM节点。
 * 主要功能：提供对主要图形绘制层的访问。
 */
mxGraphView.prototype.getDrawPane = function()
{
	return this.drawPane;
};

/**
 * Function: getOverlayPane
 *
 * Returns the DOM node that represents the layer above the drawing layer.
 */
/**
 * 函数: getOverlayPane
 *
 * 返回表示绘制层上方的DOM节点。
 *
 * 中文注释：
 * 方法目的：获取覆盖层DOM节点。
 * 主要功能：提供对覆盖绘制层的访问。
 */
mxGraphView.prototype.getOverlayPane = function()
{
	return this.overlayPane;
};

/**
 * Function: getDecoratorPane
 *
 * Returns the DOM node that represents the topmost drawing layer.
 */
/**
 * 函数: getDecoratorPane
 *
 * 返回表示最上层绘制层的DOM节点。
 *
 * 中文注释：
 * 方法目的：获取装饰层DOM节点。
 * 主要功能：提供对最上层绘制层的访问。
 */
mxGraphView.prototype.getDecoratorPane = function()
{
	return this.decoratorPane;
};

/**
 * Function: isContainerEvent
 * 
 * Returns true if the event origin is one of the drawing panes or
 * containers of the view.
 */
/**
 * 函数: isContainerEvent
 *
 * 如果事件来源是视图的绘制面板或容器之一，则返回true。
 *
 * 中文注释：
 * 方法目的：判断事件是否来自视图的绘制面板或容器。
 * 主要功能：检查事件来源是否属于视图的交互区域。
 * 关键步骤：
 * 1. 获取事件源DOM节点。
 * 2. 检查是否为容器或绘制面板的子节点。
 * 交互逻辑：用于确定鼠标事件是否应触发图的交互。
 */
mxGraphView.prototype.isContainerEvent = function(evt)
{
	var source = mxEvent.getSource(evt);

	return (source == this.graph.container ||
		source.parentNode == this.backgroundPane ||
		(source.parentNode != null &&
		source.parentNode.parentNode == this.backgroundPane) ||
		source == this.canvas.parentNode ||
		source == this.canvas ||
		source == this.backgroundPane ||
		source == this.drawPane ||
		source == this.overlayPane ||
		source == this.decoratorPane);
};

/**
 * Function: isScrollEvent
 * 
 * Returns true if the event origin is one of the scrollbars of the
 * container in IE. Such events are ignored.
 */
/**
 * 函数: isScrollEvent
 *
 * 如果事件来源是IE中容器的滚动条之一，则返回true。此类事件将被忽略。
 *
 * 中文注释：
 * 方法目的：判断事件是否来自容器滚动条。
 * 主要功能：在IE中检测滚动条事件并忽略。
 * 关键步骤：
 * 1. 计算事件坐标相对于容器的偏移。
 * 2. 检查坐标是否在滚动条区域内。
 * 特殊处理注意事项：仅在IE中处理滚动条事件，避免误触发。
 * 交互逻辑：忽略滚动条事件以防止干扰图的交互。
 */
 mxGraphView.prototype.isScrollEvent = function(evt)
{
	var offset = mxUtils.getOffset(this.graph.container);
	var pt = new mxPoint(evt.clientX - offset.x, evt.clientY - offset.y);

	var outWidth = this.graph.container.offsetWidth;
	var inWidth = this.graph.container.clientWidth;

	if (outWidth > inWidth && pt.x > inWidth + 2 && pt.x <= outWidth)
	{
		return true;
	}

	var outHeight = this.graph.container.offsetHeight;
	var inHeight = this.graph.container.clientHeight;
	
	if (outHeight > inHeight && pt.y > inHeight + 2 && pt.y <= outHeight)
	{
		return true;
	}
	
	return false;
};

/**
 * Function: init
 *
 * Initializes the graph event dispatch loop for the specified container
 * and invokes <create> to create the required DOM nodes for the display.
 */
/**
 * 函数: init
 *
 * 为指定容器初始化图事件分发循环，并调用<create>创建显示所需的DOM节点。
 *
 * 中文注释：
 * 方法目的：初始化视图的事件处理和DOM结构。
 * 主要功能：设置事件监听器并根据显示方言创建DOM节点。
 * 关键步骤：
 * 1. 安装事件监听器。
 * 2. 根据图的方言（SVG、VML或HTML）创建相应的DOM结构。
 * 事件处理逻辑：为容器设置鼠标和手势事件监听。
 */
mxGraphView.prototype.init = function()
{
	this.installListeners();
	
	// Creates the DOM nodes for the respective display dialect
    // 中文注释：为对应的显示方言创建DOM节点。
	var graph = this.graph;
	
	if (graph.dialect == mxConstants.DIALECT_SVG)
	{
		this.createSvg();
	}
	else if (graph.dialect == mxConstants.DIALECT_VML)
	{
		this.createVml();
	}
	else
	{
		this.createHtml();
	}
};

/**
 * Function: installListeners
 *
 * Installs the required listeners in the container.
 */
/**
 * 函数: installListeners
 *
 * 在容器中安装所需的事件监听器。
 *
 * 中文注释：
 * 方法目的：为视图容器设置事件监听器。
 * 主要功能：支持鼠标和手势事件的捕获和处理。
 * 关键步骤：
 * 1. 为触摸设备添加手势事件监听。
 * 2. 添加鼠标事件监听（按下、移动、释放、双击）。
 * 3. 处理外部手势事件，确保跨容器交互。
 * 4. 添加鼠标监听器隐藏弹出菜单。
 * 事件处理逻辑：
 * - 捕获手势事件（gesturestart/change/end）并触发图的事件。
 * - 处理鼠标事件，仅在容器内有效区域触发。
 * - 支持外部手势捕获（captureDocumentGesture）。
 * 交互逻辑：支持触摸设备的缩放、双击等交互。
 * 特殊处理注意事项：
 * - 在IE中忽略滚动条事件。
 * - 触摸设备需处理外部DOM节点的事件。
 */
mxGraphView.prototype.installListeners = function()
{
	var graph = this.graph;
	var container = graph.container;
	
	if (container != null)
	{
		// Support for touch device gestures (eg. pinch to zoom)
		// Double-tap handling is implemented in mxGraph.fireMouseEvent
        // 中文注释：支持触摸设备手势（如缩放），双击处理在mxGraph.fireMouseEvent中实现。
		if (mxClient.IS_TOUCH)
		{
			mxEvent.addListener(container, 'gesturestart', mxUtils.bind(this, function(evt)
			{
				graph.fireGestureEvent(evt);
				mxEvent.consume(evt);
			}));
			
			mxEvent.addListener(container, 'gesturechange', mxUtils.bind(this, function(evt)
			{
				graph.fireGestureEvent(evt);
				mxEvent.consume(evt);
			}));

			mxEvent.addListener(container, 'gestureend', mxUtils.bind(this, function(evt)
			{
				graph.fireGestureEvent(evt);
				mxEvent.consume(evt);
			}));
		}
		
		// Fires event only for one pointer per gesture
        // 中文注释：每个手势仅为一个指针触发事件。
		var pointerId = null;
		
		// Adds basic listeners for graph event dispatching
        // 中文注释：添加用于图事件分发的基本监听器。
		mxEvent.addGestureListeners(container, mxUtils.bind(this, function(evt)
		{
			// Condition to avoid scrollbar events starting a rubberband selection
            // 中文注释：避免滚动条事件触发橡皮筋选择。
			if (this.isContainerEvent(evt) && ((!mxClient.IS_IE && !mxClient.IS_IE11 && !mxClient.IS_GC &&
				!mxClient.IS_OP && !mxClient.IS_SF) || !this.isScrollEvent(evt)))
			{
				graph.fireMouseEvent(mxEvent.MOUSE_DOWN, new mxMouseEvent(evt));
				pointerId = evt.pointerId;
			}
		}),
		mxUtils.bind(this, function(evt)
		{
			if (this.isContainerEvent(evt) && (pointerId == null || evt.pointerId == pointerId))
			{
				graph.fireMouseEvent(mxEvent.MOUSE_MOVE, new mxMouseEvent(evt));
			}
		}),
		mxUtils.bind(this, function(evt)
		{
			if (this.isContainerEvent(evt))
			{
				graph.fireMouseEvent(mxEvent.MOUSE_UP, new mxMouseEvent(evt));
			}
			
			pointerId = null;
		}));
		
		// Adds listener for double click handling on background, this does always
		// use native event handler, we assume that the DOM of the background
		// does not change during the double click
        // 中文注释：为背景添加双击事件监听器，始终使用原生事件处理，假设背景DOM在双击期间不变。
		mxEvent.addListener(container, 'dblclick', mxUtils.bind(this, function(evt)
		{
			if (this.isContainerEvent(evt))
			{
				graph.dblClick(evt);
			}
		}));

		// Workaround for touch events which started on some DOM node
		// on top of the container, in which case the cells under the
		// mouse for the move and up events are not detected.
        // 中文注释：处理触摸事件从容器上方DOM节点开始的情况，确保移动和释放事件能检测到单元。
		var getState = function(evt)
		{
			var state = null;
			
			// Workaround for touch events which started on some DOM node
			// on top of the container, in which case the cells under the
			// mouse for the move and up events are not detected.
            // 中文注释：处理触摸事件，确保检测到鼠标下的单元。
			if (mxClient.IS_TOUCH)
			{
				var x = mxEvent.getClientX(evt);
				var y = mxEvent.getClientY(evt);
				
				// Dispatches the drop event to the graph which
				// consumes and executes the source function
                // 中文注释：将拖放事件分发到图，消费并执行源函数。
				var pt = mxUtils.convertPoint(container, x, y);
				state = graph.view.getState(graph.getCellAt(pt.x, pt.y));
			}
			
			return state;
		};
		
		// Adds basic listeners for graph event dispatching outside of the
		// container and finishing the handling of a single gesture
		// Implemented via graph event dispatch loop to avoid duplicate events
		// in Firefox and Chrome
        // 中文注释：为容器外部的图事件分发添加基本监听器，避免Firefox和Chrome中的重复事件。
		graph.addMouseListener(
		{
			mouseDown: function(sender, me)
			{
				graph.popupMenuHandler.hideMenu();
			},
			mouseMove: function() { },
			mouseUp: function() { }
		});
		
		this.moveHandler = mxUtils.bind(this, function(evt)
		{
			// Hides the tooltip if mouse is outside container
            // 中文注释：如果鼠标在容器外，隐藏工具提示。
			if (graph.tooltipHandler != null && graph.tooltipHandler.isHideOnHover())
			{
				graph.tooltipHandler.hide();
			}

			if (this.captureDocumentGesture && graph.isMouseDown && graph.container != null &&
				!this.isContainerEvent(evt) && graph.container.style.display != 'none' &&
				graph.container.style.visibility != 'hidden' && !mxEvent.isConsumed(evt))
			{
				graph.fireMouseEvent(mxEvent.MOUSE_MOVE, new mxMouseEvent(evt, getState(evt)));
			}
		});
		
		this.endHandler = mxUtils.bind(this, function(evt)
		{
			if (this.captureDocumentGesture && graph.isMouseDown && graph.container != null &&
				!this.isContainerEvent(evt) && graph.container.style.display != 'none' &&
				graph.container.style.visibility != 'hidden')
			{
				graph.fireMouseEvent(mxEvent.MOUSE_UP, new mxMouseEvent(evt));
			}
		});
		
		mxEvent.addGestureListeners(document, null, this.moveHandler, this.endHandler);
	}
};

/**
 * Function: createHtml
 *
 * Creates the DOM nodes for the HTML display.
 */
/**
 * 函数: createHtml
 *
 * 创建HTML显示的DOM节点。
 *
 * 中文注释：
 * 方法目的：为HTML显示创建DOM结构。
 * 主要功能：初始化画布和绘制层并添加到容器。
 * 关键步骤：
 * 1. 创建主画布和背景、绘制、覆盖、装饰面板。
 * 2. 设置画布样式（溢出隐藏）。
 * 3. 将面板添加到画布并将画布添加到容器。
 * 4. 在怪异模式下添加窗口调整监听器。
 * 样式设置：
 * - 画布样式：overflow: hidden。
 * - 面板样式：absolute定位，初始尺寸为1px。
 * 特殊处理注意事项：在IE怪异模式下处理最小尺寸。
 * 事件处理逻辑：窗口调整事件监听以更新画布尺寸。
 */
mxGraphView.prototype.createHtml = function()
{
	var container = this.graph.container;
	
	if (container != null)
	{
		this.canvas = this.createHtmlPane('100%', '100%');
		this.canvas.style.overflow = 'hidden';
	
		// Uses minimal size for inner DIVs on Canvas. This is required
		// for correct event processing in IE. If we have an overlapping
		// DIV then the events on the cells are only fired for labels.
        // 中文注释：使用最小尺寸的内部DIV以确保IE中正确处理事件，避免事件仅触发在标签上。
		this.backgroundPane = this.createHtmlPane('1px', '1px');
		this.drawPane = this.createHtmlPane('1px', '1px');
		this.overlayPane = this.createHtmlPane('1px', '1px');
		this.decoratorPane = this.createHtmlPane('1px', '1px');
		
		this.canvas.appendChild(this.backgroundPane);
		this.canvas.appendChild(this.drawPane);
		this.canvas.appendChild(this.overlayPane);
		this.canvas.appendChild(this.decoratorPane);

		container.appendChild(this.canvas);
		this.updateContainerStyle(container);
		
		// Implements minWidth/minHeight in quirks mode
        // 中文注释：在怪异模式下实现最小宽度和高度。
		if (mxClient.IS_QUIRKS)
		{
			var onResize = mxUtils.bind(this, function(evt)
			{
				var bounds = this.getGraphBounds();
				var width = bounds.x + bounds.width + this.graph.border;
				var height = bounds.y + bounds.height + this.graph.border;
				
				this.updateHtmlCanvasSize(width, height);
			});
			
			mxEvent.addListener(window, 'resize', onResize);
		}
	}
};

/**
 * Function: updateHtmlCanvasSize
 * 
 * Updates the size of the HTML canvas.
 */
/**
 * 函数: updateHtmlCanvasSize
 *
 * 更新HTML画布的尺寸。
 *
 * 中文注释：
 * 方法目的：调整HTML画布的尺寸以适应内容。
 * 主要功能：根据图边界动态设置画布宽高。
 * 参数：
 * - width: 目标宽度。
 * - height: 目标高度。
 * 关键步骤：
 * 1. 比较容器尺寸与目标尺寸。
 * 2. 设置画布宽度和高度为较大值或100%。
 */
mxGraphView.prototype.updateHtmlCanvasSize = function(width, height)
{
	if (this.graph.container != null)
	{
		var ow = this.graph.container.offsetWidth;
		var oh = this.graph.container.offsetHeight;

		if (ow < width)
		{
			this.canvas.style.width = width + 'px';
		}
		else
		{
			this.canvas.style.width = '100%';
		}

		if (oh < height)
		{
			this.canvas.style.height = height + 'px';
		}
		else
		{
			this.canvas.style.height = '100%';
		}
	}
};

/**
 * Function: createHtmlPane
 * 
 * Creates and returns a drawing pane in HTML (DIV).
 */
/**
 * 函数: createHtmlPane
 *
 * 创建并返回HTML中的绘制面板（DIV）。
 *
 * 中文注释：
 * 方法目的：创建HTML绘制面板。
 * 主要功能：生成具有指定样式和尺寸的DIV元素。
 * 参数：
 * - width: 面板宽度（可选）。
 * - height: 面板高度（可选）。
 * 关键步骤：
 * 1. 创建DIV元素。
 * 2. 设置绝对或相对定位及尺寸。
 * 样式设置：
 * - 如果指定宽高，设置为absolute定位和具体尺寸。
 * - 否则，设置为relative定位。
 */
mxGraphView.prototype.createHtmlPane = function(width, height)
{
	var pane = document.createElement('DIV');
	
	if (width != null && height != null)
	{
		pane.style.position = 'absolute';
		pane.style.left = '0px';
		pane.style.top = '0px';

		pane.style.width = width;
		pane.style.height = height;
	}
	else
	{
		pane.style.position = 'relative';
	}
	
	return pane;
};

/**
 * Function: createVml
 *
 * Creates the DOM nodes for the VML display.
 */
/**
 * 函数: createVml
 *
 * 创建VML显示的DOM节点。
 *
 * 中文注释：
 * 方法目的：为VML显示创建DOM结构。
 * 主要功能：初始化VML画布和绘制层。
 * 关键步骤：
 * 1. 创建VML组元素作为画布和面板。
 * 2. 设置画布尺寸和溢出样式。
 * 3. 将面板添加到画布并将画布添加到容器。
 * 样式设置：
 * - 画布样式：overflow: hidden。
 * - 面板样式：absolute定位，尺寸与容器一致。
 */
mxGraphView.prototype.createVml = function()
{
	var container = this.graph.container;

	if (container != null)
	{
		var width = container.offsetWidth;
		var height = container.offsetHeight;
		this.canvas = this.createVmlPane(width, height);
		this.canvas.style.overflow = 'hidden';
		
		this.backgroundPane = this.createVmlPane(width, height);
		this.drawPane = this.createVmlPane(width, height);
		this.overlayPane = this.createVmlPane(width, height);
		this.decoratorPane = this.createVmlPane(width, height);
		
		this.canvas.appendChild(this.backgroundPane);
		this.canvas.appendChild(this.drawPane);
		this.canvas.appendChild(this.overlayPane);
		this.canvas.appendChild(this.decoratorPane);
		
		container.appendChild(this.canvas);
	}
};

/**
 * Function: createVmlPane
 * 
 * Creates a drawing pane in VML (group).
 */
/**
 * 函数: createVmlPane
 *
 * 创建VML中的绘制面板（组）。
 *
 * 中文注释：
 * 方法目的：创建VML绘制面板。
 * 主要功能：生成VML组元素并设置坐标系。
 * 参数：
 * - width: 面板宽度。
 * - height: 面板高度。
 * 关键步骤：
 * 1. 创建VML组元素。
 * 2. 设置绝对定位和坐标系属性。
 * 样式设置：
 * - position: absolute。
 * - coordsize: 设置坐标系尺寸。
 * - coordorigin: 设置坐标系原点为(0,0)。
 */
mxGraphView.prototype.createVmlPane = function(width, height)
{
	var pane = document.createElement(mxClient.VML_PREFIX + ':group');
	
	// At this point the width and height are potentially
	// uninitialized. That's OK.
    // 中文注释：此时宽度和高度可能未初始化，这是正常的。
	pane.style.position = 'absolute';
	pane.style.left = '0px';
	pane.style.top = '0px';

	pane.style.width = width + 'px';
	pane.style.height = height + 'px';

	pane.setAttribute('coordsize', width + ',' + height);
	pane.setAttribute('coordorigin', '0,0');
	
	return pane;
};

/**
 * Function: createSvg
 *
 * Creates and returns the DOM nodes for the SVG display.
 */
/**
 * 函数: createSvg
 *
 * 创建并返回SVG显示的DOM节点。
 *
 * 中文注释：
 * 方法目的：为SVG显示创建DOM结构。
 * 主要功能：初始化SVG画布和绘制层，用于图形的SVG渲染。
 * 关键步骤：
 * 1. 创建SVG组元素（g）作为主画布（canvas）。
 * 2. 创建背景（backgroundPane）、绘制（drawPane）、覆盖（overlayPane）和装饰（decoratorPane）层。
 * 3. 创建SVG根元素，设置样式并将画布添加至根。
 * 4. 将SVG根添加到图容器，并更新容器样式。
 * 关键变量：
 * - canvas: 主画布，包含所有绘制层。
 * - backgroundPane: 用于背景图像的SVG组元素。
 * - drawPane: 主绘制层，包含图形元素。
 * - overlayPane: 覆盖层，用于附加图形。
 * - decoratorPane: 装饰层，用于最上层图形。
 * - root: SVG根元素，包含画布。
 * 样式设置：
 * - SVG根样式：position: absolute, left: 0px, top: 0px, width: 100%, height: 100%, display: block。
 * - IE11及以下：overflow: hidden，防止滚动条显示。
 * 特殊处理注意事项：
 * - 在标准模式下，SVG需设置display: block以避免容器DIV显示滚动条。
 * - 在IE11及以下版本中，设置overflow: hidden处理滚动条问题。
 */
mxGraphView.prototype.createSvg = function()
{
	var container = this.graph.container;
	this.canvas = document.createElementNS(mxConstants.NS_SVG, 'g');
	
	// For background image
    // 中文注释：用于背景图像的SVG组元素。
	this.backgroundPane = document.createElementNS(mxConstants.NS_SVG, 'g');
	this.canvas.appendChild(this.backgroundPane);

	// Adds two layers (background is early feature)
    // 中文注释：添加绘制和覆盖层（背景层为早期功能）。
	this.drawPane = document.createElementNS(mxConstants.NS_SVG, 'g');
	this.canvas.appendChild(this.drawPane);

	this.overlayPane = document.createElementNS(mxConstants.NS_SVG, 'g');
	this.canvas.appendChild(this.overlayPane);
	
	this.decoratorPane = document.createElementNS(mxConstants.NS_SVG, 'g');
	this.canvas.appendChild(this.decoratorPane);
	
	var root = document.createElementNS(mxConstants.NS_SVG, 'svg');
	root.style.left = '0px';
	root.style.top = '0px';
	root.style.width = '100%';
	root.style.height = '100%';
	
	// NOTE: In standards mode, the SVG must have block layout
	// in order for the container DIV to not show scrollbars.
    // 中文注释：标准模式下，SVG需设置为block布局以避免容器DIV显示滚动条。
	root.style.display = 'block';
	root.appendChild(this.canvas);
	
	// Workaround for scrollbars in IE11 and below
    // 中文注释：在IE11及以下版本中设置overflow: hidden以解决滚动条问题。
	if (mxClient.IS_IE || mxClient.IS_IE11)
	{
		root.style.overflow = 'hidden';
	}

	if (container != null)
	{
		container.appendChild(root);
		this.updateContainerStyle(container);
	}
};

/**
 * Function: updateContainerStyle
 * 
 * Updates the style of the container after installing the SVG DOM elements.
 */
/**
 * 函数: updateContainerStyle
 *
 * 在安装SVG DOM元素后更新容器的样式。
 *
 * 中文注释：
 * 方法目的：调整图容器样式以适应SVG显示。
 * 主要功能：设置容器的定位和触摸交互样式。
 * 参数：
 * - container: 图的容器DOM元素。
 * 关键步骤：
 * 1. 检查容器当前样式，若为static则改为relative。
 * 2. 在支持指针事件的浏览器中，禁用内置平移和缩放。
 * 样式设置：
 * - position: relative（如果当前为static）。
 * - touchAction: none（在支持指针事件的浏览器中）。
 * 特殊处理注意事项：
 * - 确保容器定位为relative以正确处理SVG元素的偏移。
 * - 在IE10及以上版本中禁用touchAction以防止默认缩放和平移。
 * 交互逻辑：禁用触摸交互以确保图的自定义事件处理。
 */
mxGraphView.prototype.updateContainerStyle = function(container)
{
	// Workaround for offset of container
    // 中文注释：处理容器偏移问题。
	var style = mxUtils.getCurrentStyle(container);
	
	if (style != null && style.position == 'static')
	{
		container.style.position = 'relative';
	}
	
	// Disables built-in pan and zoom in IE10 and later
    // 中文注释：在IE10及以上版本中禁用内置平移和缩放。
	if (mxClient.IS_POINTER)
	{
		container.style.touchAction = 'none';
	}
};

/**
 * Function: destroy
 * 
 * Destroys the view and all its resources.
 */
/**
 * 函数: destroy
 *
 * 销毁视图及其所有资源。
 *
 * 中文注释：
 * 方法目的：清理视图，释放所有相关资源。
 * 主要功能：移除DOM节点、事件监听器并清空视图状态。
 * 关键步骤：
 * 1. 获取SVG根或画布DOM节点。
 * 2. 清除当前根节点的状态。
 * 3. 移除文档级手势监听器。
 * 4. 释放图容器并移除根节点。
 * 5. 清空视图的画布和面板引用。
 * 关键变量：
 * - root: SVG根元素或画布DOM节点。
 * - moveHandler: 鼠标移动事件处理函数。
 * - endHandler: 鼠标释放事件处理函数。
 * 事件处理逻辑：移除文档级手势监听以防止内存泄漏。
 * 特殊处理注意事项：确保在移除DOM节点前清理事件监听器。
 */
mxGraphView.prototype.destroy = function()
{
	var root = (this.canvas != null) ? this.canvas.ownerSVGElement : null;
	
	if (root == null)
	{
		root = this.canvas;
	}
	
	if (root != null && root.parentNode != null)
	{
		this.clear(this.currentRoot, true);
		mxEvent.removeGestureListeners(document, null, this.moveHandler, this.endHandler);
		mxEvent.release(this.graph.container);
		root.parentNode.removeChild(root);
		
		this.moveHandler = null;
		this.endHandler = null;
		this.canvas = null;
		this.backgroundPane = null;
		this.drawPane = null;
		this.overlayPane = null;
		this.decoratorPane = null;
	}
};

/**
 * Class: mxCurrentRootChange
 *
 * Action to change the current root in a view.
 *
 * Constructor: mxCurrentRootChange
 *
 * Constructs a change of the current root in the given view.
 */
/**
 * 类: mxCurrentRootChange
 *
 * 用于更改视图中当前根节点的操作。
 *
 * 构造函数: mxCurrentRootChange
 *
 * 在给定视图中构造当前根节点的更改。
 *
 * 中文注释：
 * 类目的：管理视图中根节点的切换操作。
 * 主要功能：初始化根节点更改的上下文，判断是上移还是下移。
 * 参数：
 * - view: 要更改根节点的视图对象。
 * - root: 新的根节点（或null表示返回上一级）。
 * 关键变量：
 * - view: 视图对象，存储当前视图上下文。
 * - root: 新设置的根节点。
 * - previous: 前一个根节点，用于切换恢复。
 * - isUp: 布尔值，指示是否向上（null根）或向下切换。
 * 关键步骤：
 * 1. 存储视图和根节点。
 * 2. 初始化previous为当前根。
 * 3. 判断是否为上移（root为null）或下移（root为子节点）。
 * 特殊处理注意事项：通过遍历模型确认根节点的上下关系。
 */
function mxCurrentRootChange(view, root)
{
	this.view = view;
	this.root = root;
	this.previous = root;
	this.isUp = root == null;
	
	if (!this.isUp)
	{
		var tmp = this.view.currentRoot;
		var model = this.view.graph.getModel();
		
		while (tmp != null)
		{
			if (tmp == root)
			{
				this.isUp = true;
				break;
			}
			
			tmp = model.getParent(tmp);
		}
	}
};

/**
 * Function: execute
 *
 * Changes the current root of the view.
 */
/**
 * 函数: execute
 *
 * 更改视图的当前根节点。
 *
 * 中文注释：
 * 方法目的：执行根节点切换操作。
 * 主要功能：切换视图的当前根节点并更新视图状态。
 * 关键步骤：
 * 1. 保存当前根节点并切换到新根。
 * 2. 根据新根调整视图平移。
 * 3. 如果是上移，清除并验证视图；如果是下移，刷新视图。
 * 4. 触发根节点切换事件（UP或DOWN）。
 * 5. 切换isUp状态以支持撤销操作。
 * 关键变量：
 * - currentRoot: 视图的当前根节点。
 * - previous: 前一个根节点，用于撤销。
 * - translate: 根节点的平移偏移。
 * 事件处理逻辑：触发mxEvent.UP或mxEvent.DOWN事件，通知根节点变更。
 * 交互逻辑：支持视图的上下导航，更新显示内容。
 * 特殊处理注意事项：确保平移和视图状态更新一致以避免显示错误。
 */
mxCurrentRootChange.prototype.execute = function()
{
	var tmp = this.view.currentRoot;
	this.view.currentRoot = this.previous;
	this.previous = tmp;

	var translate = this.view.graph.getTranslateForRoot(this.view.currentRoot);
	
	if (translate != null)
	{
		this.view.translate = new mxPoint(-translate.x, -translate.y);
	}

	if (this.isUp)
	{
		this.view.clear(this.view.currentRoot, true);
		this.view.validate();
	}
	else
	{
		this.view.refresh();
	}
	
	var name = (this.isUp) ? mxEvent.UP : mxEvent.DOWN;
	this.view.fireEvent(new mxEventObject(name,
		'root', this.view.currentRoot, 'previous', this.previous));
	this.isUp = !this.isUp;
};
