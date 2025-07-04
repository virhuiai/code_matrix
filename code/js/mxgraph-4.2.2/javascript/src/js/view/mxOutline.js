/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxOutline
 *
 * Implements an outline (aka overview) for a graph. Set <updateOnPan> to true
 * to enable updates while the source graph is panning.
 * 
 * Example:
 * 
 * (code)
 * var outline = new mxOutline(graph, div);
 * (end)
 * 
 * If an outline is used in an <mxWindow> in IE8 standards mode, the following
 * code makes sure that the shadow filter is not inherited and that any
 * transparent elements in the graph do not show the page background, but the
 * background of the graph container.
 * 
 * (code)
 * if (document.documentMode == 8)
 * {
 *   container.style.filter = 'progid:DXImageTransform.Microsoft.alpha(opacity=100)';
 * }
 * (end)
 * 
 * To move the graph to the top, left corner the following code can be used.
 * 
 * (code)
 * var scale = graph.view.scale;
 * var bounds = graph.getGraphBounds();
 * graph.view.setTranslate(-bounds.x / scale, -bounds.y / scale);
 * (end)
 * 
 * To toggle the suspended mode, the following can be used.
 * 
 * (code)
 * outline.suspended = !outln.suspended;
 * if (!outline.suspended)
 * {
 *   outline.update(true);
 * }
 * (end)
 * 
 * Constructor: mxOutline
 *
 * Constructs a new outline for the specified graph inside the given
 * container.
 * 
 * Parameters:
 * 
 * source - <mxGraph> to create the outline for.
 * container - DOM node that will contain the outline.
 */
// 中文注释：
// mxOutline 类用于为图形创建概要视图（即缩略图或导航视图）。
// 当 source 参数指定的图形在平移时，可通过设置 updateOnPan 为 true 启用更新。
// 示例代码展示如何创建概要视图、处理 IE8 的透明度问题、移动图形到左上角以及切换暂停模式。
// 构造函数参数：
// - source: 要为其创建概要视图的 mxGraph 对象。
// - container: 包含概要视图的 DOM 节点。
function mxOutline(source, container)
{
	this.source = source;

	if (container != null)
	{
		this.init(container);
	}
    // 中文注释：
    // 构造函数将传入的 source（mxGraph 对象）存储到实例变量，并检查 container 是否存在。
    // 如果 container 不为空，则调用 init 方法初始化概要视图。
};

/**
 * Function: source
 * 
 * Reference to the source <mxGraph>.
 */
// 中文注释：
// source 属性存储指向源图形的 mxGraph 对象的引用。
mxOutline.prototype.source = null;

/**
 * Function: outline
 * 
 * Reference to the <mxGraph> that renders the outline.
 */
// 中文注释：
// outline 属性存储用于渲染概要视图的 mxGraph 对象。
mxOutline.prototype.outline = null;

/**
 * Function: graphRenderHint
 * 
 * Renderhint to be used for the outline graph. Default is faster.
 */
// 中文注释：
// graphRenderHint 属性指定概要视图图形的渲染提示，默认为 faster，表示优化渲染速度。
mxOutline.prototype.graphRenderHint = mxConstants.RENDERING_HINT_FASTER;

/**
 * Variable: enabled
 * 
 * Specifies if events are handled. Default is true.
 */
// 中文注释：
// enabled 属性指定是否处理事件，默认值为 true，表示启用事件处理。
mxOutline.prototype.enabled = true;

/**
 * Variable: showViewport
 * 
 * Specifies a viewport rectangle should be shown. Default is true.
 */
// 中文注释：
// showViewport 属性指定是否显示视口矩形，默认值为 true，表示显示视口。
mxOutline.prototype.showViewport = true;

/**
 * Variable: border
 * 
 * Border to be added at the bottom and right. Default is 10.
 */
// 中文注释：
// border 属性指定在底部和右侧添加的边框宽度，默认值为 10 像素。
mxOutline.prototype.border = 10;

/**
 * Variable: enabled
 * 
 * Specifies the size of the sizer handler. Default is 8.
 */
// 中文注释：
// sizerSize 属性指定调整大小手柄的尺寸，默认值为 8 像素。
mxOutline.prototype.sizerSize = 8;

/**
 * Variable: labelsVisible
 * 
 * Specifies if labels should be visible in the outline. Default is false.
 */
// 中文注释：
// labelsVisible 属性指定概要视图中是否显示标签，默认值为 false，表示不显示标签。
mxOutline.prototype.labelsVisible = false;

/**
 * Variable: updateOnPan
 * 
 * Specifies if <update> should be called for <mxEvent.PAN> in the source
 * graph. Default is false.
 */
// 中文注释：
// updateOnPan 属性指定当源图形发生平移事件（mxEvent.PAN）时是否调用 update 方法，
// 默认值为 false，表示不自动更新。
mxOutline.prototype.updateOnPan = false;

/**
 * Variable: sizerImage
 * 
 * Optional <mxImage> to be used for the sizer. Default is null.
 */
// 中文注释：
// sizerImage 属性指定用于调整大小手柄的可选 mxImage 对象，默认值为 null，表示不使用自定义图片。
mxOutline.prototype.sizerImage = null;

/**
 * Variable: minScale
 * 
 * Minimum scale to be used. Default is 0.0001.
 */
// 中文注释：
// minScale 属性指定概要视图的最小缩放比例，默认值为 0.0001。
mxOutline.prototype.minScale = 0.0001;

/**
 * Variable: suspended
 * 
 * Optional boolean flag to suspend updates. Default is false. This flag will
 * also suspend repaints of the outline. To toggle this switch, use the
 * following code.
 * 
 * (code)
 * nav.suspended = !nav.suspended;
 * 
 * if (!nav.suspended)
 * {
 *   nav.update(true);
 * }
 * (end)
 */
// 中文注释：
// suspended 属性是一个可选的布尔标志，用于暂停更新，默认值为 false。
// 当该标志为 true 时，也会暂停概要视图的重绘。
// 示例代码展示如何切换暂停模式并在取消暂停时调用 update 方法。
mxOutline.prototype.suspended = false;

/**
 * Variable: forceVmlHandles
 * 
 * Specifies if VML should be used to render the handles in this control. This
 * is true for IE8 standards mode and false for all other browsers and modes.
 * This is a workaround for rendering issues of HTML elements over elements
 * with filters in IE 8 standards mode.
 */
// 中文注释：
// forceVmlHandles 属性指定是否使用 VML 渲染手柄，在 IE8 标准模式下为 true，其他浏览器和模式下为 false。
// 这是为了解决 IE8 标准模式下 HTML 元素覆盖带有滤镜的元素时的渲染问题。
mxOutline.prototype.forceVmlHandles = document.documentMode == 8;

/**
 * Function: createGraph
 * 
 * Creates the <mxGraph> used in the outline.
 */
// 中文注释：
// createGraph 方法创建用于概要视图的 mxGraph 对象。
// 参数 container：包含概要视图的 DOM 节点。
// 方法禁用折叠功能和自动滚动，使用源图形的模型和样式表，并设置渲染提示。
mxOutline.prototype.createGraph = function(container)
{
	var graph = new mxGraph(container, this.source.getModel(), this.graphRenderHint, this.source.getStylesheet());
	graph.foldingEnabled = false;
	graph.autoScroll = false;
	
	return graph;
    // 中文注释：
    // 创建一个新的 mxGraph 对象，用于渲染概要视图。
    // 使用源图形的模型（model）、渲染提示（graphRenderHint）和样式表（stylesheet）。
    // 禁用节点的折叠功能（foldingEnabled）和自动滚动（autoScroll）。
};

/**
 * Function: init
 * 
 * Initializes the outline inside the given container.
 */
// 中文注释：
// init 方法在指定容器内初始化概要视图。
// 参数 container：包含概要视图的 DOM 节点。
// 方法创建概要视图图形，设置事件监听器，优化 SVG 渲染，配置视口矩形和调整大小手柄，并绑定交互事件。
mxOutline.prototype.init = function(container)
{
	this.outline = this.createGraph(container);
    // 中文注释：
    // 调用 createGraph 方法创建概要视图的 mxGraph 对象并存储到 outline 属性。

	// Do not repaint when suspended
	var outlineGraphModelChanged = this.outline.graphModelChanged;
	this.outline.graphModelChanged = mxUtils.bind(this, function(changes)
	{
		if (!this.suspended && this.outline != null)
		{
			outlineGraphModelChanged.apply(this.outline, arguments);
		}
	});
    // 中文注释：
    // 重写 outline 的 graphModelChanged 方法，仅在未暂停（suspended=false）且 outline 存在时调用原始方法。
    // 避免在暂停模式下重绘概要视图。

	// Enables faster painting in SVG
	if (mxClient.IS_SVG)
	{
		var node = this.outline.getView().getCanvas().parentNode;
		node.setAttribute('shape-rendering', 'optimizeSpeed');
		node.setAttribute('image-rendering', 'optimizeSpeed');
	}
    // 中文注释：
    // 如果使用 SVG 渲染，设置 shape-rendering 和 image-rendering 为 optimizeSpeed，以提高渲染性能。

	// Hides cursors and labels
	this.outline.labelsVisible = this.labelsVisible;
	this.outline.setEnabled(false);
    // 中文注释：
    // 根据 labelsVisible 属性设置概要视图是否显示标签。
    // 禁用概要视图的事件处理（setEnabled(false)），避免不必要的交互。

	this.updateHandler = mxUtils.bind(this, function(sender, evt)
	{
		if (!this.suspended && !this.active)
		{
			this.update();
		}
	});
    // 中文注释：
    // 定义 updateHandler 方法，用于处理源图形的变化事件。
    // 仅在未暂停（suspended=false）且未处于交互状态（active=false）时调用 update 方法。

	// Updates the scale of the outline after a change of the main graph
	this.source.getModel().addListener(mxEvent.CHANGE, this.updateHandler);
	this.outline.addMouseListener(this);
    // 中文注释：
    // 为源图形的模型添加 CHANGE 事件监听器，触发 updateHandler 以同步概要视图。
    // 为概要视图添加鼠标事件监听器以处理交互。

	// Adds listeners to keep the outline in sync with the source graph
	var view = this.source.getView();
	view.addListener(mxEvent.SCALE, this.updateHandler);
	view.addListener(mxEvent.TRANSLATE, this.updateHandler);
	view.addListener(mxEvent.SCALE_AND_TRANSLATE, this.updateHandler);
	view.addListener(mxEvent.DOWN, this.updateHandler);
	view.addListener(mxEvent.UP, this.updateHandler);
    // 中文注释：
    // 为源图形的视图添加多个事件监听器（缩放、平移、缩放和平移、按下、抬起），
    // 以确保概要视图与源图形保持同步。

	// Updates blue rectangle on scroll
	mxEvent.addListener(this.source.container, 'scroll', this.updateHandler);
    // 中文注释：
    // 为源图形的容器添加滚动事件监听器，触发 updateHandler 以更新视口矩形。

	this.panHandler = mxUtils.bind(this, function(sender)
	{
		if (this.updateOnPan)
		{
			this.updateHandler.apply(this, arguments);
		}
	});
	this.source.addListener(mxEvent.PAN, this.panHandler);
    // 中文注释：
    // 定义 panHandler 方法，处理源图形的平移事件（mxEvent.PAN）。
    // 仅当 updateOnPan 为 true 时调用 updateHandler。

	// Refreshes the graph in the outline after a refresh of the main graph
	this.refreshHandler = mxUtils.bind(this, function(sender)
	{
		this.outline.setStylesheet(this.source.getStylesheet());
		this.outline.refresh();
	});
	this.source.addListener(mxEvent.REFRESH, this.refreshHandler);
    // 中文注释：
    // 定义 refreshHandler 方法，当源图形刷新时，同步概要视图的样式表并调用 refresh 方法。

	// Creates the blue rectangle for the viewport
	this.bounds = new mxRectangle(0, 0, 0, 0);
	this.selectionBorder = new mxRectangleShape(this.bounds, null,
		mxConstants.OUTLINE_COLOR, mxConstants.OUTLINE_STROKEWIDTH);
	this.selectionBorder.dialect = this.outline.dialect;
    // 中文注释：
    // 创建表示视口的蓝色矩形（selectionBorder），使用 mxRectangleShape。
    // 设置矩形的颜色（OUTLINE_COLOR）和边框宽度（OUTLINE_STROKEWIDTH），并与概要视图的渲染模式（dialect）保持一致。

	if (this.forceVmlHandles)
	{
		this.selectionBorder.isHtmlAllowed = function()
		{
			return false;
		};
	}
    // 中文注释：
    // 如果 forceVmlHandles 为 true（即 IE8 标准模式），禁用 selectionBorder 的 HTML 渲染，确保使用 VML。

	this.selectionBorder.init(this.outline.getView().getOverlayPane());
    // 中文注释：
    // 初始化 selectionBorder，将其添加到概要视图的叠加层（overlay pane）。

	// Handles event by catching the initial pointer start and then listening to the
	// complete gesture on the event target. This is needed because all the events
	// are routed via the initial element even if that element is removed from the
	// DOM, which happens when we repaint the selection border and zoom handles.
	var handler = mxUtils.bind(this, function(evt)
	{
		var t = mxEvent.getSource(evt);
		
		var redirect = mxUtils.bind(this, function(evt)
		{
			this.outline.fireMouseEvent(mxEvent.MOUSE_MOVE, new mxMouseEvent(evt));
		});
		
		var redirect2 = mxUtils.bind(this, function(evt)
		{
			mxEvent.removeGestureListeners(t, null, redirect, redirect2);
			this.outline.fireMouseEvent(mxEvent.MOUSE_UP, new mxMouseEvent(evt));
		});
		
		mxEvent.addGestureListeners(t, null, redirect, redirect2);
		this.outline.fireMouseEvent(mxEvent.MOUSE_DOWN, new mxMouseEvent(evt));
	});
    // 中文注释：
    // 定义鼠标事件处理函数，捕获初始指针按下事件，并监听整个手势（移动和释放）。
    // 事件通过初始元素路由，即使该元素在重绘 selectionBorder 或缩放手柄时从 DOM 中移除。
    // 方法通过触发 MOUSE_DOWN、MOUSE_MOVE 和 MOUSE_UP 事件来实现交互。

	mxEvent.addGestureListeners(this.selectionBorder.node, handler);
    // 中文注释：
    // 为 selectionBorder 的 DOM 节点添加手势事件监听器，处理交互逻辑。

	// Creates a small blue rectangle for sizing (sizer handle)
	this.sizer = this.createSizer();
    // 中文注释：
    // 调用 createSizer 方法创建调整大小的手柄（sizer），表示为一个小的蓝色矩形。

	if (this.forceVmlHandles)
	{
		this.sizer.isHtmlAllowed = function()
		{
			return false;
		};
	}
    // 中文注释：
    // 如果 forceVmlHandles 为 true，禁用 sizer 的 HTML 渲染，确保使用 VML。

	this.sizer.init(this.outline.getView().getOverlayPane());
    // 中文注释：
    // 初始化 sizer，将其添加到概要视图的叠加层。

	if (this.enabled)
	{
		this.sizer.node.style.cursor = 'nwse-resize';
	}
    // 中文注释：
    // 如果 enabled 为 true，设置 sizer 的光标样式为 nwse-resize，表示可调整大小。

	mxEvent.addGestureListeners(this.sizer.node, handler);
    // 中文注释：
    // 为 sizer 的 DOM 节点添加手势事件监听器，处理调整大小的交互。

	this.selectionBorder.node.style.display = (this.showViewport) ? '' : 'none';
	this.sizer.node.style.display = this.selectionBorder.node.style.display;
	this.selectionBorder.node.style.cursor = 'move';
    // 中文注释：
    // 根据 showViewport 属性设置 selectionBorder 和 sizer 的显示状态（显示或隐藏）。
    // 设置 selectionBorder 的光标样式为 move，表示可拖动。

	this.update(false);
    // 中文注释：
    // 调用 update 方法（revalidate=false）初始化概要视图的显示。
};

/**
 * Function: isEnabled
 * 
 * Returns true if events are handled. This implementation
 * returns <enabled>.
 */
// 中文注释：
// isEnabled 方法返回 enabled 属性的值，指示是否处理事件。
mxOutline.prototype.isEnabled = function()
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
 * value - Boolean that specifies the new enabled state.
 */
// 中文注释：
// setEnabled 方法启用或禁用事件处理，通过更新 enabled 属性。
// 参数 value：布尔值，指定新的启用状态。
mxOutline.prototype.setEnabled = function(value)
{
	this.enabled = value;
};

/**
 * Function: setZoomEnabled
 * 
 * Enables or disables the zoom handling by showing or hiding the respective
 * handle.
 * 
 * Parameters:
 * 
 * value - Boolean that specifies the new enabled state.
 */
// 中文注释：
// setZoomEnabled 方法通过显示或隐藏调整大小手柄（sizer）来启用或禁用缩放处理。
// 参数 value：布尔值，指定新的启用状态。
mxOutline.prototype.setZoomEnabled = function(value)
{
	this.sizer.node.style.visibility = (value) ? 'visible' : 'hidden';
};

/**
 * Function: refresh
 * 
 * Invokes <update> and revalidate the outline. This method is deprecated.
 */
// 中文注释：
// refresh 方法调用 update 方法并重新验证概要视图。
// 注意：此方法已废弃，推荐使用 update 方法。
mxOutline.prototype.refresh = function()
{
	this.update(true);
};

/**
 * Function: createSizer
 * 
 * Creates the shape used as the sizer.
 */
// 中文注释：
// createSizer 方法创建用于调整大小的形状（sizer）。
// 如果 sizerImage 不为空，则使用指定的图片；否则创建默认的矩形形状。
mxOutline.prototype.createSizer = function()
{
	if (this.sizerImage != null)
	{
		var sizer = new mxImageShape(new mxRectangle(0, 0, this.sizerImage.width, this.sizerImage.height), this.sizerImage.src);
		sizer.dialect = this.outline.dialect;
		
		return sizer;
        // 中文注释：
        // 如果 sizerImage 属性存在，创建基于指定图片的 mxImageShape 对象。
	}
	else
	{
		var sizer = new mxRectangleShape(new mxRectangle(0, 0, this.sizerSize, this.sizerSize),
			mxConstants.OUTLINE_HANDLE_FILLCOLOR, mxConstants.OUTLINE_HANDLE_STROKECOLOR);
		sizer.dialect = this.outline.dialect;
	
		return sizer;
        // 中文注释：
        // 如果 sizerImage 为空，创建默认的矩形形状（mxRectangleShape），
        // 使用 sizerSize 指定尺寸，OUTLINE_HANDLE_FILLCOLOR 指定填充颜色，
        // OUTLINE_HANDLE_STROKECOLOR 指定边框颜色。
	}
};

/**
 * Function: getSourceContainerSize
 * 
 * Returns the size of the source container.
 */
// 中文注释：
// getSourceContainerSize 方法返回源图形容器的尺寸（滚动区域的宽度和高度）。
mxOutline.prototype.getSourceContainerSize = function()
{
	return new mxRectangle(0, 0, this.source.container.scrollWidth, this.source.container.scrollHeight);
};

/**
 * Function: getOutlineOffset
 * 
 * Returns the offset for drawing the outline graph.
 */
// 中文注释：
// getOutlineOffset 方法返回绘制概要视图图形的偏移量，默认返回 null。
// 参数 scale：当前缩放比例。
mxOutline.prototype.getOutlineOffset = function(scale)
{
	return null;
};

/**
 * Function: getSourceGraphBounds
 * 
 * Returns the graph bound boxing of the source.
 */
// 中文注释：
// getSourceGraphBounds 方法返回源图形的边界框（bounding box）。
mxOutline.prototype.getSourceGraphBounds = function()
{
	return this.source.getGraphBounds();
};

/**
 * Function: update
 * 
 * Updates the outline.
 */
// 中文注释：
// update 方法更新概要视图的内容和状态。
// 参数 revalidate：布尔值，指定是否重新验证视图。
// 方法计算源图形的边界和缩放比例，调整概要视图的缩放和平移，更新视口矩形和调整大小手柄的位置。
mxOutline.prototype.update = function(revalidate)
{
	if (this.source != null && this.source.container != null &&
		this.outline != null && this.outline.container != null)
	{
		var sourceScale = this.source.view.scale;
		var scaledGraphBounds = this.getSourceGraphBounds();
		var unscaledGraphBounds = new mxRectangle(scaledGraphBounds.x / sourceScale + this.source.panDx,
				scaledGraphBounds.y / sourceScale + this.source.panDy, scaledGraphBounds.width / sourceScale,
				scaledGraphBounds.height / sourceScale);
        // 中文注释：
        // 获取源图形的缩放比例和边界框，转换为未缩放的边界（考虑平移偏移 panDx 和 panDy）。

		var unscaledFinderBounds = new mxRectangle(0, 0,
			this.source.container.clientWidth / sourceScale,
			this.source.container.clientHeight / sourceScale);
        // 中文注释：
        // 计算未缩放的视口边界（基于源图形容器的可见区域）。

		var union = unscaledGraphBounds.clone();
		union.add(unscaledFinderBounds);
        // 中文注释：
        // 合并未缩放的图形边界和视口边界，得到整个可显示区域。

		// Zooms to the scrollable area if that is bigger than the graph
		var size = this.getSourceContainerSize();
		var completeWidth = Math.max(size.width / sourceScale, union.width);
		var completeHeight = Math.max(size.height / sourceScale, union.height);
        // 中文注释：
        // 计算整个可滚动区域的宽度和高度，取图形边界和容器尺寸的最大值。

		var availableWidth = Math.max(0, this.outline.container.clientWidth - this.border);
		var availableHeight = Math.max(0, this.outline.container.clientHeight - this.border);
        // 中文注释：
        // 计算概要视图容器的可用宽度和高度（减去边框宽度 border）。

		var outlineScale = Math.min(availableWidth / completeWidth, availableHeight / completeHeight);
		var scale = (isNaN(outlineScale)) ? this.minScale : Math.max(this.minScale, outlineScale);
        // 中文注释：
        // 计算概要视图的缩放比例，确保适应容器尺寸，同时不低于 minScale。

		if (scale > 0)
		{
			if (this.outline.getView().scale != scale)
			{
				this.outline.getView().scale = scale;
				revalidate = true;
			}
            // 中文注释：
            // 如果计算的缩放比例与当前不同，更新概要视图的缩放比例并标记需要重新验证。

			var navView = this.outline.getView();
			
			if (navView.currentRoot != this.source.getView().currentRoot)
			{
				navView.setCurrentRoot(this.source.getView().currentRoot);
			}
            // 中文注释：
            // 确保概要视图的当前根节点与源图形的根节点一致。

			var t = this.source.view.translate;
			var tx = t.x + this.source.panDx;
			var ty = t.y + this.source.panDy;
            // 中文注释：
            // 获取源图形的平移值并加上平移偏移（panDx 和 panDy）。

			var off = this.getOutlineOffset(scale);
			
			if (off != null)
			{
				tx += off.x;
				ty += off.y;
			}
            // 中文注释：
            // 如果存在偏移量（getOutlineOffset 返回非 null），将其应用到平移值。

			if (unscaledGraphBounds.x < 0)
			{
				tx = tx - unscaledGraphBounds.x;
			}
			if (unscaledGraphBounds.y < 0)
			{
				ty = ty - unscaledGraphBounds.y;
			}
            // 中文注释：
            // 如果图形边界为负值，调整平移值以确保概要视图显示正确。

			if (navView.translate.x != tx || navView.translate.y != ty)
			{
				navView.translate.x = tx;
				navView.translate.y = ty;
				revalidate = true;
			}
            // 中文注释：
            // 如果概要视图的平移值需要更新，设置新的平移值并标记需要重新验证。

			// Prepares local variables for computations
			var t2 = navView.translate;
			scale = this.source.getView().scale;
			var scale2 = scale / navView.scale;
			var scale3 = 1.0 / navView.scale;
			var container = this.source.container;
            // 中文注释：
            // 准备计算所需的局部变量，包括概要视图的平移值、源图形的缩放比例和容器引用。

			// Updates the bounds of the viewrect in the navigation
			this.bounds = new mxRectangle(
				(t2.x - t.x - this.source.panDx) / scale3,
				(t2.y - t.y - this.source.panDy) / scale3,
				(container.clientWidth / scale2),
				(container.clientHeight / scale2));
            // 中文注释：
            // 更新导航视图中视口矩形的边界（bounds），基于源图形的平移和缩放比例。

			// Adds the scrollbar offset to the finder
			this.bounds.x += this.source.container.scrollLeft * navView.scale / scale;
			this.bounds.y += this.source.container.scrollTop * navView.scale / scale;
            // 中文注释：
            // 考虑源图形的滚动条偏移，调整视口矩形的位置。

			var b = this.selectionBorder.bounds;
			
			if (b.x != this.bounds.x || b.y != this.bounds.y || b.width != this.bounds.width || b.height != this.bounds.height)
			{
				this.selectionBorder.bounds = this.bounds;
				this.selectionBorder.redraw();
			}
            // 中文注释：
            // 如果视口矩形（selectionBorder）的边界发生变化，更新并重绘。

			// Updates the bounds of the zoom handle at the bottom right
			var b = this.sizer.bounds;
			var b2 = new mxRectangle(this.bounds.x + this.bounds.width - b.width / 2,
					this.bounds.y + this.bounds.height - b.height / 2, b.width, b.height);
            // 中文注释：
            // 更新调整大小手柄（sizer）的边界，位于视口矩形的右下角。

			if (b.x != b2.x || b.y != b2.y || b.width != b2.width || b.height != b2.height)
			{
				this.sizer.bounds = b2;
				
				// Avoids update of visibility in redraw for VML
				if (this.sizer.node.style.visibility != 'hidden')
				{
					this.sizer.redraw();
				}
			}
            // 中文注释：
            // 如果调整大小手柄的边界发生变化，更新并重绘（仅当可见时）。

			if (revalidate)
			{
				this.outline.view.revalidate();
			}
            // 中文注释：
            // 如果 revalidate 为 true，重新验证概要视图以确保显示正确。
		}
	}
};

/**
 * Function: mouseDown
 * 
 * Handles the event by starting a translation or zoom.
 */
// 中文注释：
// mouseDown 方法处理鼠标按下事件，启动平移或缩放操作。
// 方法检查事件是否启用（enabled=true）且视口是否显示（showViewport=true），
// 并根据点击位置判断是平移还是缩放操作。
mxOutline.prototype.mouseDown = function(sender, me)
{
	if (this.enabled && this.showViewport)
	{
		var tol = (!mxEvent.isMouseEvent(me.getEvent())) ? this.source.tolerance : 0;
		var hit = (this.source.allowHandleBoundsCheck && (mxClient.IS_IE || tol > 0)) ?
				new mxRectangle(me.getGraphX() - tol, me.getGraphY() - tol, 2 * tol, 2 * tol) : null;
		this.zoom = me.isSource(this.sizer) || (hit != null && mxUtils.intersects(shape.bounds, hit));
		this.startX = me.getX();
		this.startY = me.getY();
		this.active = true;
        // 中文注释：
        // 检查事件是否为鼠标事件，若不是则使用源图形的容差（tolerance）。
        // 判断点击是否在调整大小手柄（sizer）上或附近（考虑容差），设置 zoom 属性。
        // 记录鼠标按下的起始坐标（startX, startY）并设置 active 状态为 true。

		if (this.source.useScrollbarsForPanning && mxUtils.hasScrollbars(this.source.container))
		{
			this.dx0 = this.source.container.scrollLeft;
			this.dy0 = this.source.container.scrollTop;
		}
		else
		{
			this.dx0 = 0;
			this.dy0 = 0;
		}
        // 中文注释：
        // 如果源图形支持滚动条平移，记录当前滚动条位置（dx0, dy0）。
        // 否则将滚动偏移初始化为 0。
	}

	me.consume();
    // 中文注释：
    // 消费事件，防止进一步传播。
};

/**
 * Function: mouseMove
 * 
 * Handles the event by previewing the viewrect in <graph> and updating the
 * rectangle that represents the viewrect in the outline.
 */
// 中文注释：
// mouseMove 方法处理鼠标移动事件，预览源图形的视口并更新概要视图中的视口矩形。
// 方法根据 zoom 属性决定是执行平移还是缩放操作，并更新 selectionBorder 和 sizer 的位置。
mxOutline.prototype.mouseMove = function(sender, me)
{
	if (this.active)
	{
		this.selectionBorder.node.style.display = (this.showViewport) ? '' : 'none';
		this.sizer.node.style.display = this.selectionBorder.node.style.display; 
        // 中文注释：
        // 如果处于交互状态（active=true），根据 showViewport 属性设置 selectionBorder 和 sizer 的显示状态。

		var delta = this.getTranslateForEvent(me);
		var dx = delta.x;
		var dy = delta.y;
		var bounds = null;
        // 中文注释：
        // 调用 getTranslateForEvent 获取鼠标移动的偏移量（dx, dy）。

		if (!this.zoom)
		{
			// Previews the panning on the source graph
			var scale = this.outline.getView().scale;
			bounds = new mxRectangle(this.bounds.x + dx,
				this.bounds.y + dy, this.bounds.width, this.bounds.height);
			this.selectionBorder.bounds = bounds;
			this.selectionBorder.redraw();
			dx /= scale;
			dx *= this.source.getView().scale;
			dy /= scale;
			dy *= this.source.getView().scale;
			this.source.panGraph(-dx - this.dx0, -dy - this.dy0);
            // 中文注释：
            // 如果不是缩放操作（zoom=false），执行平移操作。
            // 更新视口矩形的位置（bounds），并在源图形上应用平移（panGraph）。
            // 考虑概要视图和源图形的缩放比例进行坐标转换。
		}
		else
		{
			// Does *not* preview zooming on the source graph
			var container = this.source.container;
			var viewRatio = container.clientWidth / container.clientHeight;
			dy = dx / viewRatio;
			bounds = new mxRectangle(this.bounds.x,
				this.bounds.y,
				Math.max(1, this.bounds.width + dx),
				Math.max(1, this.bounds.height + dy));
			this.selectionBorder.bounds = bounds;
			this.selectionBorder.redraw();
            // 中文注释：
            // 如果是缩放操作（zoom=true），仅更新视口矩形的尺寸，不直接在源图形上预览缩放。
            // 根据容器宽高比调整 dy，确保缩放比例协调。
		}
		
		// Updates the zoom handle
		var b = this.sizer.bounds;
		this.sizer.bounds = new mxRectangle(
			bounds.x + bounds.width - b.width / 2,
			bounds.y + bounds.height - b.height / 2,
			b.width, b.height);
        // 中文注释：
        // 更新调整大小手柄（sizer）的边界，保持其位于视口矩形的右下角。

		// Avoids update of visibility in redraw for VML
		if (this.sizer.node.style.visibility != 'hidden')
		{
			this.sizer.redraw();
		}
        // 中文注释：
        // 如果 sizer 可见，重绘调整大小手柄（避免 VML 渲染时的可见性问题）。

		me.consume();
        // 中文注释：
        // 消费事件，防止进一步传播。
	}
};

/**
 * Function: getTranslateForEvent
 * 
 * Gets the translate for the given mouse event. Here is an example to limit
 * the outline to stay within positive coordinates:
 * 
 * (code)
 * outline.getTranslateForEvent = function(me)
 * {
 *   var pt = new mxPoint(me.getX() - this.startX, me.getY() - this.startY);
 *   
 *   if (!this.zoom)
 *   {
 *     var tr = this.source.view.translate;
 *     pt.x = Math.max(tr.x * this.outline.view.scale, pt.x);
 *     pt.y = Math.max(tr.y * this.outline.view.scale, pt.y);
 *   }
 *   
 *   return pt;
 * };
 * (end)
 */
// 中文注释：
// getTranslateForEvent 方法计算鼠标事件的平移偏移量。
// 返回鼠标当前位置相对于起始位置（startX, startY）的偏移量（mxPoint 对象）。
// 示例代码展示如何限制概要视图保持在正坐标范围内。
mxOutline.prototype.getTranslateForEvent = function(me)
{
	return new mxPoint(me.getX() - this.startX, me.getY() - this.startY);
};

/**
 * Function: mouseUp
 * 
 * Handles the event by applying the translation or zoom to <graph>.
 */
// 中文注释：
// mouseUp 方法处理鼠标释放事件，将平移或缩放操作应用到源图形。
// 根据 zoom 属性决定是应用平移还是缩放，并更新概要视图。
mxOutline.prototype.mouseUp = function(sender, me)
{
	if (this.active)
	{
		var delta = this.getTranslateForEvent(me);
		var dx = delta.x;
		var dy = delta.y;
        // 中文注释：
        // 获取鼠标释放时的偏移量（dx, dy）。

		if (Math.abs(dx) > 0 || Math.abs(dy) > 0)
		{
			if (!this.zoom)
			{
				// Applies the new translation if the source
				// has no scrollbars
				if (!this.source.useScrollbarsForPanning ||
					!mxUtils.hasScrollbars(this.source.container))
				{
					this.source.panGraph(0, 0);
					dx /= this.outline.getView().scale;
					dy /= this.outline.getView().scale;
					var t = this.source.getView().translate;
					this.source.getView().setTranslate(t.x - dx, t.y - dy);
				}
                // 中文注释：
                // 如果不是缩放操作（zoom=false）且源图形不支持滚动条平移，
                // 应用新的平移值到源图形（setTranslate）。
			}
			else
			{
				// Applies the new zoom
				var w = this.selectionBorder.bounds.width;
				var scale = this.source.getView().scale;
				this.source.zoomTo(Math.max(this.minScale, scale - (dx * scale) / w), false);
                // 中文注释：
                // 如果是缩放操作（zoom=true），根据视口矩形宽度和偏移量计算新的缩放比例，
                // 并应用到源图形（zoomTo），确保不低于 minScale。
			}

			this.update();
			me.consume();
            // 中文注释：
            // 调用 update 方法更新概要视图，并消费事件以防止进一步传播。
		}
			
		// Resets the state of the handler
		this.index = null;
		this.active = false;
        // 中文注释：
        // 重置交互状态，清除 index 并设置 active 为 false。
	}
};

/**
 * Function: destroy
 * 
 * Destroy this outline and removes all listeners from <source>.
 */
// 中文注释：
// destroy 方法销毁概要视图并移除源图形上的所有事件监听器。
// 方法清理 source、outline、selectionBorder 和 sizer 的引用，确保释放资源。
mxOutline.prototype.destroy = function()
{
	if (this.source != null)
	{
		this.source.removeListener(this.panHandler);
		this.source.removeListener(this.refreshHandler);
		this.source.getModel().removeListener(this.updateHandler);
		this.source.getView().removeListener(this.updateHandler);
		mxEvent.removeListener(this.source.container, 'scroll', this.updateHandler);
		this.source = null;
	}
    // 中文注释：
    // 如果 source 存在，移除平移（panHandler）、刷新（refreshHandler）、
    // 模型变化（updateHandler）和滚动事件监听器，并清空 source 引用。

	if (this.outline != null)
	{
		this.outline.removeMouseListener(this);
		this.outline.destroy();
		this.outline = null;
	}
    // 中文注释：
    // 如果 outline 存在，移除鼠标事件监听器，销毁 outline 对象，并清空引用。

	if (this.selectionBorder != null)
	{
		this.selectionBorder.destroy();
		this.selectionBorder = null;
	}
    // 中文注释：
    // 如果 selectionBorder 存在，销毁对象并清空引用。

	if (this.sizer != null)
	{
		this.sizer.destroy();
		this.sizer = null;
	}
    // 中文注释：
    // 如果 sizer 存在，销毁对象并清空引用。
};
