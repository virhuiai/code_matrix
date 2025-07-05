/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxShape
 *
 * Base class for all shapes. A shape in mxGraph is a
 * separate implementation for SVG, VML and HTML. Which
 * implementation to use is controlled by the <dialect>
 * property which is assigned from within the <mxCellRenderer>
 * when the shape is created. The dialect must be assigned
 * for a shape, and it does normally depend on the browser and
 * the confiuration of the graph (see <mxGraph> rendering hint).
 *
 * For each supported shape in SVG and VML, a corresponding
 * shape exists in mxGraph, namely for text, image, rectangle,
 * rhombus, ellipse and polyline. The other shapes are a
 * combination of these shapes (eg. label and swimlane)
 * or they consist of one or more (filled) path objects
 * (eg. actor and cylinder). The HTML implementation is
 * optional but may be required for a HTML-only view of
 * the graph.
 *
 * Custom Shapes:
 *
 * To extend from this class, the basic code looks as follows.
 * In the special case where the custom shape consists only of
 * one filled region or one filled region and an additional stroke
 * the <mxActor> and <mxCylinder> should be subclassed,
 * respectively.
 *
 * (code)
 * function CustomShape() { }
 * 
 * CustomShape.prototype = new mxShape();
 * CustomShape.prototype.constructor = CustomShape; 
 * (end)
 *
 * To register a custom shape in an existing graph instance,
 * one must register the shape under a new name in the graph's
 * cell renderer as follows:
 *
 * (code)
 * mxCellRenderer.registerShape('customShape', CustomShape);
 * (end)
 *
 * The second argument is the name of the constructor.
 *
 * In order to use the shape you can refer to the given name above
 * in a stylesheet. For example, to change the shape for the default
 * vertex style, the following code is used:
 *
 * (code)
 * var style = graph.getStylesheet().getDefaultVertexStyle();
 * style[mxConstants.STYLE_SHAPE] = 'customShape';
 * (end)
 * 
 * Constructor: mxShape
 *
 * Constructs a new shape.
 */
// 中文注释：
// mxShape 是所有图形的基类，用于在 mxGraph 中实现 SVG、VML 和 HTML 图形。
// 图形实现由 dialect 属性控制，dialect 由 mxCellRenderer 在创建图形时分配。
// 每种图形（如文本、图像、矩形、菱形、椭圆、折线）在 SVG 和 VML 中都有对应实现。
// 自定义图形通过继承 mxShape 或其子类（如 mxActor、mxCylinder）实现。
// 自定义图形需通过 mxCellRenderer.registerShape 注册，并可通过样式表引用。

function mxShape(stencil)
{
	this.stencil = stencil;
	this.initStyles();
};
// 中文注释：
// 构造函数：mxShape
// 参数：stencil - 定义图形的 mxStencil 对象。
// 功能：初始化一个新的图形对象，设置 stencil 属性并调用 initStyles 初始化样式。

/**
 * Variable: dialect
 *
 * Holds the dialect in which the shape is to be painted.
 * This can be one of the DIALECT constants in <mxConstants>.
 */
// 中文注释：
// 变量：dialect
// 功能：存储图形的绘制格式（如 SVG、VML），由 mxConstants 中的 DIALECT 常量定义。
// 重要配置：决定了图形的渲染方式，通常由浏览器和图形配置决定。
mxShape.prototype.dialect = null;

/**
 * Variable: scale
 *
 * Holds the scale in which the shape is being painted.
 */
// 中文注释：
// 变量：scale
// 功能：存储图形的缩放比例。
// 重要配置：影响图形的大小，默认为 1（无缩放）。
mxShape.prototype.scale = 1;

/**
 * Variable: antiAlias
 * 
 * Rendering hint for configuring the canvas.
 */
// 中文注释：
// 变量：antiAlias
// 功能：控制画布是否启用抗锯齿渲染。
// 重要配置：影响图形边缘的平滑度，默认为 true（启用抗锯齿）。
mxShape.prototype.antiAlias = true;

/**
 * Variable: minSvgStrokeWidth
 * 
 * Minimum stroke width for SVG output.
 */
// 中文注释：
// 变量：minSvgStrokeWidth
// 功能：定义 SVG 输出时的最小描边宽度。
// 重要配置：确保 SVG 描边在缩放时保持可见，默认值为 1。
mxShape.prototype.minSvgStrokeWidth = 1;

/**
 * Variable: bounds
 *
 * Holds the <mxRectangle> that specifies the bounds of this shape.
 */
// 中文注释：
// 变量：bounds
// 功能：存储图形的边界框（mxRectangle 对象），定义图形的坐标和大小。
// 重要配置：用于确定图形的绘制位置和尺寸。
mxShape.prototype.bounds = null;

/**
 * Variable: points
 *
 * Holds the array of <mxPoints> that specify the points of this shape.
 */
// 中文注释：
// 变量：points
// 功能：存储图形的点数组（mxPoint 对象），用于定义折线或多边形的顶点。
// 用途说明：主要用于绘制折线或复杂多边形。
mxShape.prototype.points = null;

/**
 * Variable: node
 *
 * Holds the outermost DOM node that represents this shape.
 */
// 中文注释：
// 变量：node
// 功能：存储表示图形的 DOM 节点。
// 用途说明：作为图形的 DOM 表示，用于实际渲染。
mxShape.prototype.node = null;
 
/**
 * Variable: state
 * 
 * Optional reference to the corresponding <mxCellState>.
 */
// 中文注释：
// 变量：state
// 功能：可选引用，指向对应的 mxCellState 对象。
// 用途说明：存储与图形关联的单元状态信息。
mxShape.prototype.state = null;

/**
 * Variable: style
 *
 * Optional reference to the style of the corresponding <mxCellState>.
 */
// 中文注释：
// 变量：style
// 功能：可选引用，指向对应 mxCellState 的样式对象。
// 用途说明：定义图形的样式属性（如颜色、描边等）。
mxShape.prototype.style = null;

/**
 * Variable: boundingBox
 *
 * Contains the bounding box of the shape, that is, the smallest rectangle
 * that includes all pixels of the shape.
 */
// 中文注释：
// 变量：boundingBox
// 功能：存储图形的最小边界框（mxRectangle 对象），包含图形的所有像素。
// 用途说明：用于碰撞检测或事件处理。
mxShape.prototype.boundingBox = null;

/**
 * Variable: stencil
 *
 * Holds the <mxStencil> that defines the shape.
 */
// 中文注释：
// 变量：stencil
// 功能：存储定义图形的 mxStencil 对象。
// 用途说明：用于描述图形的具体绘制路径。
mxShape.prototype.stencil = null;

/**
 * Variable: svgStrokeTolerance
 *
 * Event-tolerance for SVG strokes (in px). Default is 8. This is only passed
 * to the canvas in <createSvgCanvas> if <pointerEvents> is true.
 */
// 中文注释：
// 变量：svgStrokeTolerance
// 功能：定义 SVG 描边的事件容差（以像素为单位），默认值为 8。
// 事件处理逻辑：仅在 pointerEvents 为 true 时传递给画布，影响描边区域的事件响应范围。
// 重要配置：控制用户点击描边时的灵敏度。
mxShape.prototype.svgStrokeTolerance = 8;

/**
 * Variable: pointerEvents
 * 
 * Specifies if pointer events should be handled. Default is true.
 */
// 中文注释：
// 变量：pointerEvents
// 功能：指定是否处理指针事件，默认为 true。
// 事件处理逻辑：控制图形是否响应鼠标事件（如点击、悬停）。
// 重要配置：启用后，图形可捕获用户交互。
mxShape.prototype.pointerEvents = true;

/**
 * Variable: svgPointerEvents
 * 
 * Specifies if pointer events should be handled. Default is true.
 */
// 中文注释：
// 变量：svgPointerEvents
// 功能：指定 SVG 图形是否处理指针事件，默认为 'all'。
// 事件处理逻辑：控制 SVG 图形的事件响应行为。
// 重要配置：值为 'all' 表示捕获所有指针事件。
mxShape.prototype.svgPointerEvents = 'all';

/**
 * Variable: shapePointerEvents
 * 
 * Specifies if pointer events outside of shape should be handled. Default
 * is false.
 */
// 中文注释：
// 变量：shapePointerEvents
// 功能：指定是否处理图形边界外的指针事件，默认为 false。
// 事件处理逻辑：当为 true 时，图形边界外的区域也能响应事件。
// 特殊处理：通常用于特殊交互场景。
mxShape.prototype.shapePointerEvents = false;

/**
 * Variable: stencilPointerEvents
 * 
 * Specifies if pointer events outside of stencils should be handled. Default
 * is false. Set this to true for backwards compatibility with the 1.x branch.
 */
// 中文注释：
// 变量：stencilPointerEvents
// 功能：指定是否处理 stencil 边界外的指针事件，默认为 false。
// 事件处理逻辑：当为 true 时，stencil 外的区域也能响应事件。
// 特殊处理：设置为 true 可兼容 1.x 分支的行为。
mxShape.prototype.stencilPointerEvents = false;

/**
 * Variable: vmlScale
 * 
 * Scale for improving the precision of VML rendering. Default is 1.
 */
// 中文注释：
// 变量：vmlScale
// 功能：定义 VML 渲染的缩放比例，用于提高精度，默认为 1。
// 重要配置：影响 VML 图形的绘制精度。
mxShape.prototype.vmlScale = 1;

/**
 * Variable: outline
 * 
 * Specifies if the shape should be drawn as an outline. This disables all
 * fill colors and can be used to disable other drawing states that should
 * not be painted for outlines. Default is false. This should be set before
 * calling <apply>.
 */
// 中文注释：
// 变量：outline
// 功能：指定是否以轮廓形式绘制图形，默认为 false。
// 特殊处理：启用后禁用填充颜色，仅绘制轮廓，需在调用 apply 方法前设置。
// 样式设置：用于突出显示图形轮廓。
mxShape.prototype.outline = false;

/**
 * Variable: visible
 * 
 * Specifies if the shape is visible. Default is true.
 */
// 中文注释：
// 变量：visible
// 功能：指定图形是否可见，默认为 true。
// 样式设置：控制图形的显示状态。
mxShape.prototype.visible = true;

/**
 * Variable: useSvgBoundingBox
 * 
 * Allows to use the SVG bounding box in SVG. Default is false for performance
 * reasons.
 */
// 中文注释：
// 变量：useSvgBoundingBox
// 功能：指定是否使用 SVG 的边界框，默认为 false（出于性能考虑）。
// 特殊处理：启用后使用 SVG 原生边界框，可能影响性能。
mxShape.prototype.useSvgBoundingBox = false;

/**
 * Function: init
 *
 * Initializes the shape by creaing the DOM node using <create>
 * and adding it into the given container.
 *
 * Parameters:
 *
 * container - DOM node that will contain the shape.
 */
// 中文注释：
// 方法：init
// 功能：初始化图形，通过 create 方法创建 DOM 节点并添加到指定容器。
// 参数：container - 包含图形的 DOM 节点。
// 关键步骤：检查 node 是否存在，若不存在则调用 create 创建并添加到容器。
mxShape.prototype.init = function(container)
{
	if (this.node == null)
	{
		this.node = this.create(container);
		
		if (container != null)
		{
			container.appendChild(this.node);
		}
	}
};

/**
 * Function: initStyles
 *
 * Sets the styles to their default values.
 */
// 中文注释：
// 方法：initStyles
// 功能：将图形的样式设置为默认值。
// 样式设置：包括描边宽度、旋转、透明度、填充透明度、描边透明度、水平翻转和垂直翻转等。
mxShape.prototype.initStyles = function(container)
{
	this.strokewidth = 1;
	this.rotation = 0;
	this.opacity = 100;
	this.fillOpacity = 100;
	this.strokeOpacity = 100;
	this.flipH = false;
	this.flipV = false;
};

/**
 * Function: isParseVml
 * 
 * Specifies if any VML should be added via insertAdjacentHtml to the DOM. This
 * is only needed in IE8 and only if the shape contains VML markup. This method
 * returns true.
 */
// 中文注释：
// 方法：isParseVml
// 功能：指定是否通过 insertAdjacentHtml 将 VML 添加到 DOM。
// 特殊处理：仅在 IE8 且图形包含 VML 标记时需要，返回 true。
mxShape.prototype.isParseVml = function()
{
	return true;
};

/**
 * Function: isHtmlAllowed
 * 
 * Returns true if HTML is allowed for this shape. This implementation always
 * returns false.
 */
// 中文注释：
// 方法：isHtmlAllowed
// 功能：检查是否允许使用 HTML 渲染图形，此实现始终返回 false。
// 特殊处理：默认禁用 HTML 渲染，子类可覆盖此方法。
mxShape.prototype.isHtmlAllowed = function()
{
	return false;
};

/**
 * Function: getSvgScreenOffset
 * 
 * Returns 0, or 0.5 if <strokewidth> % 2 == 1.
 */
// 中文注释：
// 方法：getSvgScreenOffset
// 功能：根据描边宽度计算 SVG 屏幕偏移量，若描边宽度为奇数则返回 0.5，否则返回 0。
// 用途说明：用于 SVG 渲染时调整描边位置以提高视觉效果。
mxShape.prototype.getSvgScreenOffset = function()
{
	var sw = this.stencil && this.stencil.strokewidth != 'inherit' ? Number(this.stencil.strokewidth) : this.strokewidth;
	
	return (mxUtils.mod(Math.max(1, Math.round(sw * this.scale)), 2) == 1) ? 0.5 : 0;
};

/**
 * Function: create
 *
 * Creates and returns the DOM node(s) for the shape in
 * the given container. This implementation invokes
 * <createSvg>, <createHtml> or <createVml> depending
 * on the <dialect> and style settings.
 *
 * Parameters:
 *
 * container - DOM node that will contain the shape.
 */
// 中文注释：
// 方法：create
// 功能：根据 dialect 和样式设置创建并返回图形的 DOM 节点。
// 参数：container - 包含图形的 DOM 节点。
// 关键步骤：根据容器类型和 dialect 调用 createSvg、createHtml 或 createVml。
mxShape.prototype.create = function(container)
{
	var node = null;
	
	if (container != null && container.ownerSVGElement != null)
	{
		node = this.createSvg(container);
	}
	else if (document.documentMode == 8 || !mxClient.IS_VML ||
		(this.dialect != mxConstants.DIALECT_VML && this.isHtmlAllowed()))
	{
		node = this.createHtml(container);
	}
	else
	{
		node = this.createVml(container);
	}
	
	return node;
};

/**
 * Function: createSvg
 *
 * Creates and returns the SVG node(s) to represent this shape.
 */
// 中文注释：
// 方法：createSvg
// 功能：创建并返回表示图形的 SVG 节点（<g> 元素）。
// 用途说明：用于 SVG 渲染环境的图形绘制。
mxShape.prototype.createSvg = function()
{
	return document.createElementNS(mxConstants.NS_SVG, 'g');
};

/**
 * Function: createVml
 *
 * Creates and returns the VML node to represent this shape.
 */
// 中文注释：
// 方法：createVml
// 功能：创建并返回表示图形的 VML 节点（<group> 元素）。
// 样式设置：设置节点为绝对定位。
mxShape.prototype.createVml = function()
{
	var node = document.createElement(mxClient.VML_PREFIX + ':group');
	node.style.position = 'absolute';
	
	return node;
};

/**
 * Function: createHtml
 *
 * Creates and returns the HTML DOM node(s) to represent
 * this shape. This implementation falls back to <createVml>
 * so that the HTML creation is optional.
 */
// 中文注释：
// 方法：createHtml
// 功能：创建并返回表示图形的 HTML DOM 节点（<div> 元素）。
// 特殊处理：若 HTML 渲染不可用，则回退到 createVml。
// 样式设置：设置节点为绝对定位。
mxShape.prototype.createHtml = function()
{
	var node = document.createElement('div');
	node.style.position = 'absolute';
	
	return node;
};

/**
 * Function: reconfigure
 *
 * Reconfigures this shape. This will update the colors etc in
 * addition to the bounds or points.
 */
// 中文注释：
// 方法：reconfigure
// 功能：重新配置图形，更新颜色、边界或点等。
// 用途说明：调用 redraw 方法实现重新渲染。
mxShape.prototype.reconfigure = function()
{
	this.redraw();
};

/**
 * Function: redraw
 *
 * Creates and returns the SVG node(s) to represent this shape.
 */
// 中文注释：
// 方法：redraw
// 功能：重新绘制图形，更新边界、样式和 DOM 节点。
// 关键步骤：检查可见性和边界有效性，调用 redrawHtmlShape 或 redrawShape 进行绘制。
// 事件处理逻辑：根据可见性设置 DOM 节点的 visibility 属性。
mxShape.prototype.redraw = function()
{
	this.updateBoundsFromPoints();
	
	if (this.visible && this.checkBounds())
	{
		this.node.style.visibility = 'visible';
		this.clear();
		
		if (this.node.nodeName == 'DIV' && (this.isHtmlAllowed() || !mxClient.IS_VML))
		{
			this.redrawHtmlShape();
		}
		else
		{	
			this.redrawShape();
		}

		this.updateBoundingBox();
	}
	else
	{
		this.node.style.visibility = 'hidden';
		this.boundingBox = null;
	}
};

/**
 * Function: clear
 * 
 * Removes all child nodes and resets all CSS.
 */
// 中文注释：
// 方法：clear
// 功能：移除所有子节点并重置 CSS 样式。
// 关键步骤：对于 SVG 节点，移除所有子节点；对于其他节点，重置 cssText 和 innerHTML。
// 样式设置：确保节点样式恢复到初始状态。
mxShape.prototype.clear = function()
{
	if (this.node.ownerSVGElement != null)
	{
		while (this.node.lastChild != null)
		{
			this.node.removeChild(this.node.lastChild);
		}
	}
	else
	{
		this.node.style.cssText = 'position:absolute;' + ((this.cursor != null) ?
			('cursor:' + this.cursor + ';') : '');
		this.node.innerHTML = '';
	}
};

/**
 * Function: updateBoundsFromPoints
 * 
 * Updates the bounds based on the points.
 */
// 中文注释：
// 方法：updateBoundsFromPoints
// 功能：根据点数组更新图形的边界框。
// 关键步骤：遍历 points 数组，计算包含所有点的边界框。
// 用途说明：确保 bounds 属性反映图形的实际范围。
mxShape.prototype.updateBoundsFromPoints = function()
{
	var pts = this.points;
	
	if (pts != null && pts.length > 0 && pts[0] != null)
	{
		this.bounds = new mxRectangle(Number(pts[0].x), Number(pts[0].y), 1, 1);
		
		for (var i = 1; i < this.points.length; i++)
		{
			if (pts[i] != null)
			{
				this.bounds.add(new mxRectangle(Number(pts[i].x), Number(pts[i].y), 1, 1));
			}
		}
	}
};

/**
 * Function: getLabelBounds
 * 
 * Returns the <mxRectangle> for the label bounds of this shape, based on the
 * given scaled and translated bounds of the shape. This method should not
 * change the rectangle in-place. This implementation returns the given rect.
 */
// 中文注释：
// 方法：getLabelBounds
// 功能：根据缩放和转换后的图形边界返回标签的边界框（mxRectangle）。
// 参数：rect - 图形的缩放和转换后的边界框。
// 关键步骤：根据样式方向调整边界框，处理垂直标签的特殊情况。
// 特殊处理：考虑翻转和方向调整以正确计算标签边界。
mxShape.prototype.getLabelBounds = function(rect)
{
	var d = mxUtils.getValue(this.style, mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_EAST);
	var bounds = rect;
	
	// Normalizes argument for getLabelMargins hook
	if (d != mxConstants.DIRECTION_SOUTH && d != mxConstants.DIRECTION_NORTH &&
		this.state != null && this.state.text != null &&
		this.state.text.isPaintBoundsInverted())
	{
		bounds = bounds.clone();
		var tmp = bounds.width;
		bounds.width = bounds.height;
		bounds.height = tmp;
	}
		
	var m = this.getLabelMargins(bounds);
	
	if (m != null)
	{
		var flipH = mxUtils.getValue(this.style, mxConstants.STYLE_FLIPH, false) == '1';
		var flipV = mxUtils.getValue(this.style, mxConstants.STYLE_FLIPV, false) == '1';
		
		// Handles special case for vertical labels
		if (this.state != null && this.state.text != null &&
			this.state.text.isPaintBoundsInverted())
		{
			var tmp = m.x;
			m.x = m.height;
			m.height = m.width;
			m.width = m.y;
			m.y = tmp;

			tmp = flipH;
			flipH = flipV;
			flipV = tmp;
		}
		
		return mxUtils.getDirectedBounds(rect, m, this.style, flipH, flipV);
	}
	
	return rect;
};

/**
 * Function: getLabelMargins
 * 
 * Returns the scaled top, left, bottom and right margin to be used for
 * computing the label bounds as an <mxRectangle>, where the bottom and right
 * margin are defined in the width and height of the rectangle, respectively.
 */
// 中文注释：
// 方法：getLabelMargins
// 功能：返回用于计算标签边界的上下左右缩放边距（mxRectangle）。
// 参数：rect - 图形的边界框。
// 用途说明：提供标签的边距信息，默认返回 null，子类可覆盖。
mxShape.prototype.getLabelMargins= function(rect)
{
	return null;
};

/**
 * Function: checkBounds
 * 
 * Returns true if the bounds are not null and all of its variables are numeric.
 */
// 中文注释：
// 方法：checkBounds
// 功能：检查边界框是否有效（不为空且所有变量为数字）。
// 用途说明：确保图形边界有效，用于绘制和事件处理。
mxShape.prototype.checkBounds = function()
{
	return (!isNaN(this.scale) && isFinite(this.scale) && this.scale > 0 &&
			this.bounds != null && !isNaN(this.bounds.x) && !isNaN(this.bounds.y) &&
			!isNaN(this.bounds.width) && !isNaN(this.bounds.height) &&
			this.bounds.width > 0 && this.bounds.height > 0);
};

/**
 * Function: createVmlGroup
 *
 * Returns the temporary element used for rendering in IE8 standards mode.
 */
// 中文注释：
// 方法：createVmlGroup
// 功能：创建用于 IE8 标准模式渲染的临时 VML 组元素。
// 样式设置：设置节点为绝对定位，并匹配当前节点的宽高。
mxShape.prototype.createVmlGroup = function()
{
	var node = document.createElement(mxClient.VML_PREFIX + ':group');
	node.style.position = 'absolute';
	node.style.width = this.node.style.width;
	node.style.height = this.node.style.height;
	
	return node;
};

/**
 * Function: redrawShape
 *
 * Updates the SVG or VML shape.
 */
// 中文注释：
// 方法：redrawShape
// 功能：更新 SVG 或 VML 图形的渲染。
// 关键步骤：创建画布，设置指针事件，调用绘制方法，处理 IE8 兼容性。
// 特殊处理：在 IE8 标准模式下使用透明滤镜处理事件。
mxShape.prototype.redrawShape = function()
{
	var canvas = this.createCanvas();
	
	if (canvas != null)
	{
		// Specifies if events should be handled
		canvas.pointerEvents = this.pointerEvents;
	
		this.beforePaint(canvas);
		this.paint(canvas);
		this.afterPaint(canvas);
	
		if (this.node != canvas.root)
		{
			// Forces parsing in IE8 standards mode - slow! avoid
			this.node.insertAdjacentHTML('beforeend', canvas.root.outerHTML);
		}
	
		if (this.node.nodeName == 'DIV' && document.documentMode == 8)
		{
			// Makes DIV transparent to events for IE8 in IE8 standards
			// mode (Note: Does not work for IE9 in IE8 standards mode
			// and not for IE11 in enterprise mode)
			this.node.style.filter = '';
			
			// Adds event transparency in IE8 standards
			mxUtils.addTransparentBackgroundFilter(this.node);
		}
		
		this.destroyCanvas(canvas);
	}
};

/**
 * Function: createCanvas
 * 
 * Creates a new canvas for drawing this shape. May return null.
 */
// 中文注释：
// 方法：createCanvas
// 功能：为图形绘制创建新画布，可能返回 null。
// 关键步骤：根据渲染环境（SVG 或 VML）调用 createSvgCanvas 或 createVmlCanvas。
// 特殊处理：若 outline 为 true，禁用部分画布绘制方法以仅绘制轮廓。
mxShape.prototype.createCanvas = function()
{
	var canvas = null;
	
	// LATER: Check if reusing existing DOM nodes improves performance
	if (this.node.ownerSVGElement != null)
	{
		canvas = this.createSvgCanvas();
	}
	else if (mxClient.IS_VML)
	{
		this.updateVmlContainer();
		canvas = this.createVmlCanvas();
	}
	
	if (canvas != null && this.outline)
	{
		canvas.setStrokeWidth(this.strokewidth);
		canvas.setStrokeColor(this.stroke);
		
		if (this.isDashed != null)
		{
			canvas.setDashed(this.isDashed);
		}
		
		canvas.setStrokeWidth = function() {};
		canvas.setStrokeColor = function() {};
		canvas.setFillColor = function() {};
		canvas.setGradient = function() {};
		canvas.setDashed = function() {};
		canvas.text = function() {};
	}

	return canvas;
};

/**
 * Function: createSvgCanvas
 * 
 * Creates and returns an <mxSvgCanvas2D> for rendering this shape.
 */
// 中文注释：
// 方法：createSvgCanvas
// 功能：创建并返回用于渲染图形的 mxSvgCanvas2D 对象。
// 关键步骤：设置描边容差、指针事件值和屏幕偏移，禁用抗锯齿时格式化值为整数。
// 样式设置：根据 antiAlias 属性调整渲染精度。
mxShape.prototype.createSvgCanvas = function()
{
	var canvas = new mxSvgCanvas2D(this.node, false);
	canvas.strokeTolerance = (this.pointerEvents) ? this.svgStrokeTolerance : 0;
	canvas.pointerEventsValue = this.svgPointerEvents;
	var off = this.getSvgScreenOffset();

	if (off != 0)
	{
		this.node.setAttribute('transform', 'translate(' + off + ',' + off + ')');
	}
	else
	{
		this.node.removeAttribute('transform');
	}

	canvas.minStrokeWidth = this.minSvgStrokeWidth;
	
	if (!this.antiAlias)
	{
		// Rounds all numbers in the SVG output to integers
		canvas.format = function(value)
		{
			return Math.round(parseFloat(value));
		};
	}
	
	return canvas;
};

/**
 * Function: createVmlCanvas
 * 
 * Creates and returns an <mxVmlCanvas2D> for rendering this shape.
 */
// 中文注释：
// 方法：createVmlCanvas
// 功能：创建并返回用于渲染图形的 mxVmlCanvas2D 对象。
// 关键步骤：处理 IE8 标准模式的 VML 渲染，设置坐标系和缩放。
// 特殊处理：在 IE8 中使用 createVmlGroup 创建临时组元素。
mxShape.prototype.createVmlCanvas = function()
{
	// Workaround for VML rendering bug in IE8 standards mode
	var node = (document.documentMode == 8 && this.isParseVml()) ? this.createVmlGroup() : this.node;
	var canvas = new mxVmlCanvas2D(node, false);
	
	if (node.tagUrn != '')
	{
		var w = Math.max(1, Math.round(this.bounds.width));
		var h = Math.max(1, Math.round(this.bounds.height));
		node.coordsize = (w * this.vmlScale) + ',' + (h * this.vmlScale);
		canvas.scale(this.vmlScale);
		canvas.vmlScale = this.vmlScale;
	}

	// Painting relative to top, left shape corner
	var s = this.scale;
	canvas.translate(-Math.round(this.bounds.x / s), -Math.round(this.bounds.y / s));
	
	return canvas;
};

/**
 * Function: updateVmlContainer
 * 
 * Updates the bounds of the VML container.
 */
// 中文注释：
// 方法：updateVmlContainer
// 功能：更新 VML 容器的边界。
// 样式设置：设置节点的 left、top、width、height 和 overflow 属性。
mxShape.prototype.updateVmlContainer = function()
{
	this.node.style.left = Math.round(this.bounds.x) + 'px';
	this.node.style.top = Math.round(this.bounds.y) + 'px';
	var w = Math.max(1, Math.round(this.bounds.width));
	var h = Math.max(1, Math.round(this.bounds.height));
	this.node.style.width = w + 'px';
	this.node.style.height = h + 'px';
	this.node.style.overflow = 'visible';
};

/**
 * Function: redrawHtml
 *
 * Allow optimization by replacing VML with HTML.
 */
// 中文注释：
// 方法：redrawHtmlShape
// 功能：通过 HTML 替换 VML 进行优化渲染。
// 关键步骤：更新 HTML 节点的边界、滤镜和颜色。
// 用途说明：用于支持 HTML 渲染环境的图形绘制。
mxShape.prototype.redrawHtmlShape = function()
{
	// LATER: Refactor methods
	this.updateHtmlBounds(this.node);
	this.updateHtmlFilters(this.node);
	this.updateHtmlColors(this.node);
};

/**
 * Function: updateHtmlFilters
 *
 * Allow optimization by replacing VML with HTML.
 */
// 中文注释：
// 方法：updateHtmlFilters
// 功能：更新 HTML 节点的滤镜效果。
// 样式设置：设置透明度、阴影和渐变效果。
// 特殊处理：阴影透明度无法通过滤镜实现，需注意。
mxShape.prototype.updateHtmlFilters = function(node)
{
	var f = '';
	
	if (this.opacity < 100)
	{
		f += 'alpha(opacity=' + (this.opacity) + ')';
	}
	
	if (this.isShadow)
	{
		// FIXME: Cannot implement shadow transparency with filter
		f += 'progid:DXImageTransform.Microsoft.dropShadow (' +
			'OffX=\'' + Math.round(mxConstants.SHADOW_OFFSET_X * this.scale) + '\', ' +
			'OffY=\'' + Math.round(mxConstants.SHADOW_OFFSET_Y * this.scale) + '\', ' +
			'Color=\'' + mxConstants.VML_SHADOWCOLOR + '\')';
	}
	
	if (this.fill != null && this.fill != mxConstants.NONE && this.gradient && this.gradient != mxConstants.NONE)
	{
		var start = this.fill;
		var end = this.gradient;
		var type = '0';
		
		var lookup = {east:0,south:1,west:2,north:3};
		var dir = (this.direction != null) ? lookup[this.direction] : 0;
		
		if (this.gradientDirection != null)
		{
			dir = mxUtils.mod(dir + lookup[this.gradientDirection] - 1, 4);
		}

		if (dir == 1)
		{
			type = '1';
			var tmp = start;
			start = end;
			end = tmp;
		}
		else if (dir == 2)
		{
			var tmp = start;
			start = end;
			end = tmp;
		}
		else if (dir == 3)
		{
			type = '1';
		}
		
		f += 'progid:DXImageTransform.Microsoft.gradient(' +
			'startColorStr=\'' + start + '\', endColorStr=\'' + end +
			'\', gradientType=\'' + type + '\')';
	}

	node.style.filter = f;
};

/**
 * Function: updateHtmlColors
 *
 * Allow optimization by replacing VML with HTML.
 */
// 中文注释：
// 方法：updateHtmlColors
// 功能：更新 HTML 节点的颜色和描边样式。
// 样式设置：设置边框颜色、样式、宽度和背景颜色。
// 特殊处理：处理透明背景和 IE8 兼容性。
mxShape.prototype.updateHtmlColors = function(node)
{
	var color = this.stroke;
	
	if (color != null && color != mxConstants.NONE)
	{
		node.style.borderColor = color;

		if (this.isDashed)
		{
			node.style.borderStyle = 'dashed';
		}
		else if (this.strokewidth > 0)
		{
			node.style.borderStyle = 'solid';
		}

		node.style.borderWidth = Math.max(1, Math.ceil(this.strokewidth * this.scale)) + 'px';
	}
	else
	{
		node.style.borderWidth = '0px';
	}

	color = (this.outline) ? null : this.fill;
	
	if (color != null && color != mxConstants.NONE)
	{
		node.style.backgroundColor = color;
		node.style.backgroundImage = 'none';
	}
	else if (this.pointerEvents)
	{
		 node.style.backgroundColor = 'transparent';
	}
	else if (document.documentMode == 8)
	{
		mxUtils.addTransparentBackgroundFilter(node);
	}
	else
	{
		this.setTransparentBackgroundImage(node);
	}
};

/**
 * Function: updateHtmlBounds
 *
 * Allow optimization by replacing VML with HTML.
 */
// 中文注释：
// 方法：updateHtmlBounds
// 功能：更新 HTML 节点的边界。
// 样式设置：设置边框宽度、溢出属性、位置和尺寸。
// 特殊处理：考虑浏览器兼容模式调整边框宽度。
mxShape.prototype.updateHtmlBounds = function(node)
{
	var sw = (document.documentMode >= 9) ? 0 : Math.ceil(this.strokewidth * this.scale);
	node.style.borderWidth = Math.max(1, sw) + 'px';
	node.style.overflow = 'hidden';
	
	node.style.left = Math.round(this.bounds.x - sw / 2) + 'px';
	node.style.top = Math.round(this.bounds.y - sw / 2) + 'px';

	if (document.compatMode == 'CSS1Compat')
	{
		sw = -sw;
	}
	
	node.style.width = Math.round(Math.max(0, this.bounds.width + sw)) + 'px';
	node.style.height = Math.round(Math.max(0, this.bounds.height + sw)) + 'px';
};

/**
 * Function: destroyCanvas
 * 
 * Destroys the given canvas which was used for drawing. This implementation
 * increments the reference counts on all shared gradients used in the canvas.
 */
// 中文注释：
// 方法：destroyCanvas
// 功能：销毁用于绘制的画布，增加共享渐变的引用计数。
// 关键步骤：对 SVG 画布中的渐变对象增加引用计数，释放旧渐变。
// 特殊处理：确保资源管理正确，避免内存泄漏。
mxShape.prototype.destroyCanvas = function(canvas)
{
	// Manages reference counts
	if (canvas instanceof mxSvgCanvas2D)
	{
		// Increments ref counts
		for (var key in canvas.gradients)
		{
			var gradient = canvas.gradients[key];
			
			if (gradient != null)
			{
				gradient.mxRefCount = (gradient.mxRefCount || 0) + 1;
			}
		}
		
		this.releaseSvgGradients(this.oldGradients);
		this.oldGradients = canvas.gradients;
	}
};

/**
 * Function: beforePaint
 * 
 * Invoked before paint is called.
 */
// 中文注释：
// 方法：beforePaint
// 功能：在调用 paint 方法前执行的钩子方法，子类可覆盖。
// 用途说明：提供绘制前的预处理机会。
mxShape.prototype.beforePaint = function(c) { }

/**
 * Function: afterPaint
 * 
 * Invokes after paint was called.
 */
// 中文注释：
// 方法：afterPaint
// 功能：在调用 paint 方法后执行的钩子方法，子类可覆盖。
// 用途说明：提供绘制后的后处理机会。
mxShape.prototype.afterPaint = function(c) { }

/**
 * Function: paint
 * 
 * Generic rendering code.
 */
// 中文注释：
// 方法：paint
// 功能：通用渲染代码，绘制图形的主要逻辑。
// 关键步骤：处理轮廓绘制、缩放、变换、背景矩形和形状绘制。
// 事件处理逻辑：为支持指针事件的图形添加透明背景矩形。
// 交互逻辑：根据 stencil 或 points 绘制不同类型的图形（顶点或边）。
mxShape.prototype.paint = function(c)
{
	var strokeDrawn = false;
	
	if (c != null && this.outline)
	{
		var stroke = c.stroke;
		
		c.stroke = function()
		{
			strokeDrawn = true;
			stroke.apply(this, arguments);
		};

		var fillAndStroke = c.fillAndStroke;
		
		c.fillAndStroke = function()
		{
			strokeDrawn = true;
			fillAndStroke.apply(this, arguments);
		};
	}

	// Scale is passed-through to canvas
	var s = this.scale;
	var x = this.bounds.x / s;
	var y = this.bounds.y / s;
	var w = this.bounds.width / s;
	var h = this.bounds.height / s;

	if (this.isPaintBoundsInverted())
	{
		var t = (w - h) / 2;
		x += t;
		y -= t;
		var tmp = w;
		w = h;
		h = tmp;
	}
	
	this.updateTransform(c, x, y, w, h);
	this.configureCanvas(c, x, y, w, h);

	// Adds background rectangle to capture events
	var bg = null;
	
	if ((this.stencil == null && this.points == null && this.shapePointerEvents) ||
		(this.stencil != null && this.stencilPointerEvents))
	{
		var bb = this.createBoundingBox();
		
		if (this.dialect == mxConstants.DIALECT_SVG)
		{
			bg = this.createTransparentSvgRectangle(bb.x, bb.y, bb.width, bb.height);
			this.node.appendChild(bg);
		}
		else
		{
			var rect = c.createRect('rect', bb.x / s, bb.y / s, bb.width / s, bb.height / s);
			rect.appendChild(c.createTransparentFill());
			rect.stroked = 'false';
			c.root.appendChild(rect);
		}
	}

	if (this.stencil != null)
	{
		this.stencil.drawShape(c, this, x, y, w, h);
	}
	else
	{
		// Stencils have separate strokewidth
		c.setStrokeWidth(this.strokewidth);
		
		if (this.points != null)
		{
			// Paints edge shape
			var pts = [];
			
			for (var i = 0; i < this.points.length; i++)
			{
				if (this.points[i] != null)
				{
					pts.push(new mxPoint(this.points[i].x / s, this.points[i].y / s));
				}
			}

			this.paintEdgeShape(c, pts);
		}
		else
		{
			// Paints vertex shape
			this.paintVertexShape(c, x, y, w, h);
		}
	}
	
	if (bg != null && c.state != null && c.state.transform != null)
	{
		bg.setAttribute('transform', c.state.transform);
	}
	
	// Draws highlight rectangle if no stroke was used
	if (c != null && this.outline && !strokeDrawn)
	{
		c.rect(x, y, w, h);
		c.stroke();
	}
};

/**
 * Function: configureCanvas
 * 
 * Sets the state of the canvas for drawing the shape.
 */
// 中文注释：
// 方法：configureCanvas
// 功能：设置画布状态以绘制图形。
// 关键步骤：设置透明度、阴影、虚线模式、填充颜色、描边颜色和渐变。
// 样式设置：根据样式属性配置画布的渲染参数。
mxShape.prototype.configureCanvas = function(c, x, y, w, h)
{
	var dash = null;
	
	if (this.style != null)
	{
		dash = this.style['dashPattern'];		
	}

	c.setAlpha(this.opacity / 100);
	c.setFillAlpha(this.fillOpacity / 100);
	c.setStrokeAlpha(this.strokeOpacity / 100);

	// Sets alpha, colors and gradients
	if (this.isShadow != null)
	{
		c.setShadow(this.isShadow);
	}
	
	// Dash pattern
	if (this.isDashed != null)
	{
		c.setDashed(this.isDashed, (this.style != null) ?
			mxUtils.getValue(this.style, mxConstants.STYLE_FIX_DASH, false) == 1 : false);
	}

	if (dash != null)
	{
		c.setDashPattern(dash);
	}

	if (this.fill != null && this.fill != mxConstants.NONE && this.gradient && this.gradient != mxConstants.NONE)
	{
		var b = this.getGradientBounds(c, x, y, w, h);
		c.setGradient(this.fill, this.gradient, b.x, b.y, b.width, b.height, this.gradientDirection);
	}
	else
	{
		c.setFillColor(this.fill);
	}

	c.setStrokeColor(this.stroke);
};

/**
 * Function: getGradientBounds
 * 
 * Returns the bounding box for the gradient box for this shape.
 */
// 中文注释：
// 方法：getGradientBounds
// 功能：返回图形的渐变边界框。
// 参数：c - 画布对象；x, y, w, h - 图形的坐标和尺寸。
// 用途说明：定义渐变效果的应用范围。
mxShape.prototype.getGradientBounds = function(c, x, y, w, h)
{
	return new mxRectangle(x, y, w, h);
};

/**
 * Function: updateTransform
 * 
 * Sets the scale and rotation on the given canvas.
 */
// 中文注释：
// 方法：updateTransform
// 功能：设置画布的缩放和旋转。
// 参数：c - 画布对象；x, y, w, h - 图形的坐标和尺寸。
// 关键步骤：应用缩放和旋转变换，基于图形中心点。
mxShape.prototype.updateTransform = function(c, x, y, w, h)
{
	// NOTE: Currently, scale is implemented in state and canvas. This will
	// move to canvas in a later version, so that the states are unscaled
	// and untranslated and do not need an update after zooming or panning.
	c.scale(this.scale);
	c.rotate(this.getShapeRotation(), this.flipH, this.flipV, x + w / 2, y + h / 2);
};

/**
 * Function: paintVertexShape
 * 
 * Paints the vertex shape.
 */
// 中文注释：
// 方法：paintVertexShape
// 功能：绘制顶点形状。
// 参数：c - 画布对象；x, y, w, h - 图形的坐标和尺寸。
// 关键步骤：调用 paintBackground 和 paintForeground 绘制背景和前景。
// 特殊处理：若 outline 为 true 或无背景轮廓，则禁用阴影绘制前景。
mxShape.prototype.paintVertexShape = function(c, x, y, w, h)
{
	this.paintBackground(c, x, y, w, h);
	
	if (!this.outline || this.style == null || mxUtils.getValue(
		this.style, mxConstants.STYLE_BACKGROUND_OUTLINE, 0) == 0)
	{
		c.setShadow(false);
		this.paintForeground(c, x, y, w, h);
	}
};

/**
 * Function: paintBackground
 * 
 * Hook for subclassers. This implementation is empty.
 */
// 中文注释：
// 方法：paintBackground
// 功能：绘制图形背景的钩子方法，子类可覆盖，默认空实现。
// 用途说明：提供自定义背景绘制的扩展点。
mxShape.prototype.paintBackground = function(c, x, y, w, h) { };

/**
 * Function: paintForeground
 * 
 * Hook for subclassers. This implementation is empty.
 */
// 中文注释：
// 方法：paintForeground
// 功能：绘制图形前景的钩子方法，子类可覆盖，默认空实现。
// 用途说明：提供自定义前景绘制的扩展点。
mxShape.prototype.paintForeground = function(c, x, y, w, h) { };

/**
 * Function: paintEdgeShape
 * 
 * Hook for subclassers. This implementation is empty.
 */
// 中文注释：
// 方法：paintEdgeShape
// 功能：绘制边形状的钩子方法，子类可覆盖，默认空实现。
// 参数：c - 画布对象；pts - 点数组。
// 用途说明：提供自定义边绘制的扩展点。
mxShape.prototype.paintEdgeShape = function(c, pts) { };

/**
 * Function: getArcSize
 * 
 * Returns the arc size for the given dimension.
 */
// 中文注释：
// 方法：getArcSize
// 功能：根据给定尺寸返回圆弧大小。
// 参数：w, h - 图形的宽度和高度。
// 关键步骤：根据样式属性选择绝对或相对圆弧大小。
// 用途说明：用于绘制圆角矩形等形状的圆弧。
mxShape.prototype.getArcSize = function(w, h)
{
	var r = 0;
	
	if (mxUtils.getValue(this.style, mxConstants.STYLE_ABSOLUTE_ARCSIZE, 0) == '1')
	{
		r = Math.min(w / 2, Math.min(h / 2, mxUtils.getValue(this.style,
			mxConstants.STYLE_ARCSIZE, mxConstants.LINE_ARCSIZE) / 2));
	}
	else
	{
		var f = mxUtils.getValue(this.style, mxConstants.STYLE_ARCSIZE,
			mxConstants.RECTANGLE_ROUNDING_FACTOR * 100) / 100;
		r = Math.min(w * f, h * f);
	}
	
	return r;
};

/**
 * Function: paintGlassEffect
 * 
 * Paints the glass gradient effect.
 */
// 中文注释：
// 方法：paintGlassEffect
// 功能：绘制玻璃渐变效果。
// 参数：c - 画布对象；x, y, w, h - 图形的坐标和尺寸；arc - 圆弧大小。
// 关键步骤：设置渐变填充，绘制圆角或直边路径。
// 样式设置：实现从白色到透明的玻璃效果。
mxShape.prototype.paintGlassEffect = function(c, x, y, w, h, arc)
{
	var sw = Math.ceil(this.strokewidth / 2);
	var size = 0.4;
	
	c.setGradient('#ffffff', '#ffffff', x, y, w, h * 0.6, 'south', 0.9, 0.1);
	c.begin();
	arc += 2 * sw;
		
	if (this.isRounded)
	{
		c.moveTo(x - sw + arc, y - sw);
		c.quadTo(x - sw, y - sw, x - sw, y - sw + arc);
		c.lineTo(x - sw, y + h * size);
		c.quadTo(x + w * 0.5, y + h * 0.7, x + w + sw, y + h * size);
		c.lineTo(x + w + sw, y - sw + arc);
		c.quadTo(x + w + sw, y - sw, x + w + sw - arc, y - sw);
	}
	else
	{
		c.moveTo(x - sw, y - sw);
		c.lineTo(x - sw, y + h * size);
		c.quadTo(x + w * 0.5, y + h * 0.7, x + w + sw, y + h * size);
		c.lineTo(x + w + sw, y - sw);
	}
	
	c.close();
	c.fill();
};

/**
 * Function: addPoints
 * 
 * Paints the given points with rounded corners.
 */
// 中文注释：
// 方法：addPoints
// 功能：绘制带圆角的点路径。
// 参数：c - 画布对象；pts - 点数组；rounded - 是否圆角；arcSize - 圆弧大小；close - 是否闭合路径；exclude - 排除的点索引；initialMove - 是否初始移动。
// 关键步骤：处理闭合路径的虚拟点，绘制直线和圆角曲线。
// 特殊处理：支持圆角路径和排除特定点的绘制。
mxShape.prototype.addPoints = function(c, pts, rounded, arcSize, close, exclude, initialMove)
{
	if (pts != null && pts.length > 0)
	{
		initialMove = (initialMove != null) ? initialMove : true;
		var pe = pts[pts.length - 1];
		
		// Adds virtual waypoint in the center between start and end point
		if (close && rounded)
		{
			pts = pts.slice();
			var p0 = pts[0];
			var wp = new mxPoint(pe.x + (p0.x - pe.x) / 2, pe.y + (p0.y - pe.y) / 2);
			pts.splice(0, 0, wp);
		}
	
		var pt = pts[0];
		var i = 1;
	
		// Draws the line segments
		if (initialMove)
		{
			c.moveTo(pt.x, pt.y);
		}
		else
		{
			c.lineTo(pt.x, pt.y);
		}
		
		while (i < ((close) ? pts.length : pts.length - 1))
		{
			var tmp = pts[mxUtils.mod(i, pts.length)];
			var dx = pt.x - tmp.x;
			var dy = pt.y - tmp.y;
	
			if (rounded && (dx != 0 || dy != 0) && (exclude == null || mxUtils.indexOf(exclude, i - 1) < 0))
			{
				// Draws a line from the last point to the current
				// point with a spacing of size off the current point
				// into direction of the last point
				var dist = Math.sqrt(dx * dx + dy * dy);
				var nx1 = dx * Math.min(arcSize, dist / 2) / dist;
				var ny1 = dy * Math.min(arcSize, dist / 2) / dist;
	
				var x1 = tmp.x + nx1;
				var y1 = tmp.y + ny1;
				c.lineTo(x1, y1);
	
				// Draws a curve from the last point to the current
				// point with a spacing of size off the current point
				// into direction of the next point
				var next = pts[mxUtils.mod(i + 1, pts.length)];
				
				// Uses next non-overlapping point
				while (i < pts.length - 2 && Math.round(next.x - tmp.x) == 0 && Math.round(next.y - tmp.y) == 0)
				{
					next = pts[mxUtils.mod(i + 2, pts.length)];
					i++;
				}
				
				dx = next.x - tmp.x;
				dy = next.y - tmp.y;
	
				dist = Math.max(1, Math.sqrt(dx * dx + dy * dy));
				var nx2 = dx * Math.min(arcSize, dist / 2) / dist;
				var ny2 = dy * Math.min(arcSize, dist / 2) / dist;
	
				var x2 = tmp.x + nx2;
				var y2 = tmp.y + ny2;
	
				c.quadTo(tmp.x, tmp.y, x2, y2);
				tmp = new mxPoint(x2, y2);
			}
			else
			{
				c.lineTo(tmp.x, tmp.y);
			}
	
			pt = tmp;
			i++;
		}
	
		if (close)
		{
			c.close();
		}
		else
		{
			c.lineTo(pe.x, pe.y);
		}
	}
};

/**
 * Function: resetStyles
 * 
 * Resets all styles.
 */
// 中文注释：
// 方法：resetStyles
// 功能：重置所有样式属性。
// 关键步骤：调用 initStyles 并重置其他样式属性（如填充、渐变、描边等）。
// 用途说明：恢复图形的默认样式状态。
mxShape.prototype.resetStyles = function()
{
	this.initStyles();

	this.spacing = 0;
	
	delete this.fill;
	delete this.gradient;
	delete this.gradientDirection;
	delete this.stroke;
	delete this.startSize;
	delete this.endSize;
	delete this.startArrow;
	delete this.endArrow;
	delete this.direction;
	delete this.isShadow;
	delete this.isDashed;
	delete this.isRounded;
	delete this.glass;
};

/**
 * Function: apply
 * 
 * Applies the style of the given <mxCellState> to the shape. This
 * implementation assigns the following styles to local fields:
 * 
 * - <mxConstants.STYLE_FILLCOLOR> => fill
 * - <mxConstants.STYLE_GRADIENTCOLOR> => gradient
 * - <mxConstants.STYLE_GRADIENT_DIRECTION> => gradientDirection
 * - <mxConstants.STYLE_OPACITY> => opacity
 * - <mxConstants.STYLE_FILL_OPACITY> => fillOpacity
 * - <mxConstants.STYLE_STROKE_OPACITY> => strokeOpacity
 * - <mxConstants.STYLE_STROKECOLOR> => stroke
 * - <mxConstants.STYLE_STROKEWIDTH> => strokewidth
 * - <mxConstants.STYLE_SHADOW> => isShadow
 * - <mxConstants.STYLE_DASHED> => isDashed
 * - <mxConstants.STYLE_SPACING> => spacing
 * - <mxConstants.STYLE_STARTSIZE> => startSize
 * - <mxConstants.STYLE_ENDSIZE> => endSize
 * - <mxConstants.STYLE_ROUNDED> => isRounded
 * - <mxConstants.STYLE_STARTARROW> => startArrow
 * - <mxConstants.STYLE_ENDARROW> => endArrow
 * - <mxConstants.STYLE_ROTATION> => rotation
 * - <mxConstants.STYLE_DIRECTION> => direction
 * - <mxConstants.STYLE_GLASS> => glass
 *
 * This keeps a reference to the <style>. If you need to keep a reference to
 * the cell, you can override this method and store a local reference to
 * state.cell or the <mxCellState> itself. If <outline> should be true, make
 * sure to set it before calling this method.
 *
 * Parameters:
 *
 * state - <mxCellState> of the corresponding cell.
 */
// 中文注释：
// 方法：apply
// 功能：将给定 mxCellState 的样式应用到图形。
// 参数：state - 对应的单元状态（mxCellState）。
// 关键步骤：从 state.style 中提取样式属性并赋值给本地字段。
// 样式设置：包括填充颜色、渐变、透明度、描边、阴影、虚线等。
// 特殊处理：处理方向和翻转逻辑，确保正确应用样式。
mxShape.prototype.apply = function(state)
{
	this.state = state;
	this.style = state.style;

	if (this.style != null)
	{
		this.fill = mxUtils.getValue(this.style, mxConstants.STYLE_FILLCOLOR, this.fill);
		this.gradient = mxUtils.getValue(this.style, mxConstants.STYLE_GRADIENTCOLOR, this.gradient);
		this.gradientDirection = mxUtils.getValue(this.style, mxConstants.STYLE_GRADIENT_DIRECTION, this.gradientDirection);
		this.opacity = mxUtils.getValue(this.style, mxConstants.STYLE_OPACITY, this.opacity);
		this.fillOpacity = mxUtils.getValue(this.style, mxConstants.STYLE_FILL_OPACITY, this.fillOpacity);
		this.strokeOpacity = mxUtils.getValue(this.style, mxConstants.STYLE_STROKE_OPACITY, this.strokeOpacity);
		this.stroke = mxUtils.getValue(this.style, mxConstants.STYLE_STROKECOLOR, this.stroke);
		this.strokewidth = mxUtils.getNumber(this.style, mxConstants.STYLE_STROKEWIDTH, this.strokewidth);
		this.spacing = mxUtils.getValue(this.style, mxConstants.STYLE_SPACING, this.spacing);
		this.startSize = mxUtils.getNumber(this.style, mxConstants.STYLE_STARTSIZE, this.startSize);
		this.endSize = mxUtils.getNumber(this.style, mxConstants.STYLE_ENDSIZE, this.endSize);
		this.startArrow = mxUtils.getValue(this.style, mxConstants.STYLE_STARTARROW, this.startArrow);
		this.endArrow = mxUtils.getValue(this.style, mxConstants.STYLE_ENDARROW, this.endArrow);
		this.rotation = mxUtils.getValue(this.style, mxConstants.STYLE_ROTATION, this.rotation);
		this.direction = mxUtils.getValue(this.style, mxConstants.STYLE_DIRECTION, this.direction);
		this.flipH = mxUtils.getValue(this.style, mxConstants.STYLE_FLIPH, 0) == 1;
		this.flipV = mxUtils.getValue(this.style, mxConstants.STYLE_FLIPV, 0) == 1;	
		
		// Legacy support for stencilFlipH/V
		if (this.stencil != null)
		{
			this.flipH = mxUtils.getValue(this.style, 'stencilFlipH', 0) == 1 || this.flipH;
			this.flipV = mxUtils.getValue(this.style, 'stencilFlipV', 0) == 1 || this.flipV;
		}
		
		if (this.direction == mxConstants.DIRECTION_NORTH || this.direction == mxConstants.DIRECTION_SOUTH)
		{
			var tmp = this.flipH;
			this.flipH = this.flipV;
			this.flipV = tmp;
		}

		this.isShadow = mxUtils.getValue(this.style, mxConstants.STYLE_SHADOW, this.isShadow) == 1;
		this.isDashed = mxUtils.getValue(this.style, mxConstants.STYLE_DASHED, this.isDashed) == 1;
		this.isRounded = mxUtils.getValue(this.style, mxConstants.STYLE_ROUNDED, this.isRounded) == 1;
		this.glass = mxUtils.getValue(this.style, mxConstants.STYLE_GLASS, this.glass) == 1;
		
		if (this.fill == mxConstants.NONE)
		{
			this.fill = null;
		}

		if (this.gradient == mxConstants.NONE)
		{
			this.gradient = null;
		}

		if (this.stroke == mxConstants.NONE)
		{
			this.stroke = null;
		}
	}
};

/**
 * Function: setCursor
 * 
 * Sets the cursor on the given shape.
 *
 * Parameters:
 *
 * cursor - The cursor to be used.
 */
// 中文注释：
// 方法：setCursor
// 功能：设置图形的鼠标光标样式。
// 参数：cursor - 要使用的光标样式。
// 交互逻辑：更新 DOM 节点的 cursor 样式以反映用户交互。
mxShape.prototype.setCursor = function(cursor)
{
	if (cursor == null)
	{
		cursor = '';
	}
	
	this.cursor = cursor;

	if (this.node != null)
	{
		this.node.style.cursor = cursor;
	}
};

/**
 * Function: getCursor
 * 
 * Returns the current cursor.
 */
// 中文注释：
// 方法：getCursor
// 功能：返回当前的光标样式。
// 用途说明：获取图形当前的鼠标光标状态。
mxShape.prototype.getCursor = function()
{
	return this.cursor;
};

/**
 * Function: isRoundable
 * 
 * Hook for subclassers.
 */
// 中文注释：
// 方法：isRoundable
// 功能：检查图形是否支持圆角，子类可覆盖，默认返回 false。
// 用途说明：提供圆角支持的扩展点。
mxShape.prototype.isRoundable = function()
{
	return false;
};

/**
 * Function: updateBoundingBox
 *
 * Updates the <boundingBox> for this shape using <createBoundingBox> and
 * <augmentBoundingBox> and stores the result in <boundingBox>.
 */
// 中文注释：
// 方法：update UpdatingBox
// 功能：更新图形的边界框。
// 关键步骤：尝试从 SVG 获取边界框，若失败则使用 createBoundingBox 和 augmentBoundingBox。
// 特殊处理：考虑描边宽度和旋转调整边界框。
mxShape.prototype.updateBoundingBox = function()
{
	// Tries to get bounding box from SVG subsystem
	// LATER: Use getBoundingClientRect for fallback in VML
	if (this.useSvgBoundingBox && this.node != null && this.node.ownerSVGElement != null)
	{
		try
		{
			var b = this.node.getBBox();
	
			if (b.width > 0 && b.height > 0)
			{
				this.boundingBox = new mxRectangle(b.x, b.y, b.width, b.height);
				
				// Adds strokeWidth
				this.boundingBox.grow(this.strokewidth * this.scale / 2);
				
				return;
			}
		}
		catch(e)
		{
			// fallback to code below
		}
	}

	if (this.bounds != null)
	{
		var bbox = this.createBoundingBox();
		
		if (bbox != null)
		{
			this.augmentBoundingBox(bbox);
			var rot = this.getShapeRotation();
			
			if (rot != 0)
			{
				bbox = mxUtils.getBoundingBox(bbox, rot);
			}
		}

		this.boundingBox = bbox;
	}
};

/**
 * Function: createBoundingBox
 *
 * Returns a new rectangle that represents the bounding box of the bare shape
 * with no shadows or strokewidths.
 */
// 中文注释：
// 方法：createBoundingBox
// 功能：返回表示无阴影和描边宽度的裸图形边界框。
// 关键步骤：克隆 bounds 属性，考虑方向调整边界框。
// 特殊处理：若 stencil 不为空且方向为南北，则旋转边界框。
mxShape.prototype.createBoundingBox = function()
{
	var bb = this.bounds.clone();

	if ((this.stencil != null && (this.direction == mxConstants.DIRECTION_NORTH ||
		this.direction == mxConstants.DIRECTION_SOUTH)) || this.isPaintBoundsInverted())
	{
		bb.rotate90();
	}
	
	return bb;
};

/**
 * Function: augmentBoundingBox
 *
 * Augments the bounding box with the strokewidth and shadow offsets.
 */
// 中文注释：
// 方法：augmentBoundingBox
// 功能：通过描边宽度和阴影偏移扩展边界框。
// 参数：bbox - 要扩展的边界框。
// 关键步骤：若有阴影，增加阴影偏移；扩展描边宽度。
mxShape.prototype.augmentBoundingBox = function(bbox)
{
	if (this.isShadow)
	{
		bbox.width += Math.ceil(mxConstants.SHADOW_OFFSET_X * this.scale);
		bbox.height += Math.ceil(mxConstants.SHADOW_OFFSET_Y * this.scale);
	}
	
	// Adds strokeWidth
	bbox.grow(this.strokewidth * this.scale / 2);
};

/**
 * Function: isPaintBoundsInverted
 * 
 * Returns true if the bounds should be inverted.
 */
// 中文注释：
// 方法：isPaintBoundsInverted
// 功能：检查边界框是否需要反转。
// 用途说明：当没有 stencil 且方向为南北时返回 true。
mxShape.prototype.isPaintBoundsInverted = function()
{
	// Stencil implements inversion via aspect
	return this.stencil == null && (this.direction == mxConstants.DIRECTION_NORTH ||
			this.direction == mxConstants.DIRECTION_SOUTH);
};

/**
 * Function: getRotation
 * 
 * Returns the rotation from the style.
 */
// 中文注释：
// 方法：getRotation
// 功能：返回样式的旋转角度。
// 用途说明：获取图形的旋转属性，默认返回 0。
mxShape.prototype.getRotation = function()
{
	return (this.rotation != null) ? this.rotation : 0;
};

/**
 * Function: getTextRotation
 * 
 * Returns the rotation for the text label.
 */
// 中文注释：
// 方法：getTextRotation
// 功能：返回文本标签的旋转角度。
// 关键步骤：根据样式是否水平调整旋转角度。
// 用途说明：用于正确旋转文本标签。
mxShape.prototype.getTextRotation = function()
{
	var rot = this.getRotation();
	
	if (mxUtils.getValue(this.style, mxConstants.STYLE_HORIZONTAL, 1) != 1)
	{
		rot += mxText.prototype.verticalTextRotation;
	}
	
	return rot;
};

/**
 * Function: getShapeRotation
 * 
 * Returns the actual rotation of the shape.
 */
// 中文注释：
// 方法：getShapeRotation
// 功能：返回图形的实际旋转角度。
// 关键步骤：根据 direction 属性调整旋转角度。
// 用途说明：确保图形方向与样式一致。
mxShape.prototype.getShapeRotation = function()
{
	var rot = this.getRotation();
	
	if (this.direction != null)
	{
		if (this.direction == mxConstants.DIRECTION_NORTH)
		{
			rot += 270;
		}
		else if (this.direction == mxConstants.DIRECTION_WEST)
		{
			rot += 180;
		}
		else if (this.direction == mxConstants.DIRECTION_SOUTH)
		{
			rot += 90;
		}
	}
	
	return rot;
};

/**
 * Function: createTransparentSvgRectangle
 * 
 * Adds a transparent rectangle that catches all events.
 */
// 中文注释：
// 方法：createTransparentSvgRectangle
// 功能：创建捕获所有事件的透明 SVG 矩形。
// 参数：x, y, w, h - 矩形的坐标和尺寸。
// 样式设置：设置填充和描边为无，启用所有指针事件。
// 事件处理逻辑：用于扩展图形的交互区域。
mxShape.prototype.createTransparentSvgRectangle = function(x, y, w, h)
{
	var rect = document.createElementNS(mxConstants.NS_SVG, 'rect');
	rect.setAttribute('x', x);
	rect.setAttribute('y', y);
	rect.setAttribute('width', w);
	rect.setAttribute('height', h);
	rect.setAttribute('fill', 'none');
	rect.setAttribute('stroke', 'none');
	rect.setAttribute('pointer-events', 'all');
	
	return rect;
};

/**
 * Function: setTransparentBackgroundImage
 * 
 * Sets a transparent background CSS style to catch all events.
 * 
 * Paints the line shape.
 */
// 中文注释：
// 方法：setTransparentBackgroundImage
// 功能：设置透明背景 CSS 样式以捕获所有事件。
// 参数：node - DOM 节点。
// 样式设置：使用透明 GIF 图像作为背景。
// 事件处理逻辑：确保节点捕获事件但视觉上透明。
mxShape.prototype.setTransparentBackgroundImage = function(node)
{
	node.style.backgroundImage = 'url(\'' + mxClient.imageBasePath + '/transparent.gif\')';
};

/**
 * Function: releaseSvgGradients
 * 
 * Paints the line shape.
 */
// 中文注释：
// 方法：releaseSvgGradients
// 功能：释放 SVG 渐变资源。
// 参数：grads - 渐变对象集合。
// 关键步骤：减少渐变引用计数，若计数为 0 则移除渐变。
// 特殊处理：确保资源正确释放，避免内存泄漏。
mxShape.prototype.releaseSvgGradients = function(grads)
{
	if (grads != null)
	{
		for (var key in grads)
		{
			var gradient = grads[key];
			
			if (gradient != null)
			{
				gradient.mxRefCount = (gradient.mxRefCount || 0) - 1;
				
				if (gradient.mxRefCount == 0 && gradient.parentNode != null)
				{
					gradient.parentNode.removeChild(gradient);
				}
			}
		}
	}
};

/**
 * Function: destroy
 *
 * Destroys the shape by removing it from the DOM and releasing the DOM
 * node associated with the shape using <mxEvent.release>.
 */
// 中文注释：
// 方法：destroy
// 功能：销毁图形，从 DOM 中移除并释放相关 DOM 节点。
// 关键步骤：释放事件监听器，移除 DOM 节点，释放渐变资源。
// 特殊处理：确保资源完全释放，避免内存泄漏。
mxShape.prototype.destroy = function()
{
	if (this.node != null)
	{
		mxEvent.release(this.node);
		
		if (this.node.parentNode != null)
		{
			this.node.parentNode.removeChild(this.node);
		}
		
		this.node = null;
	}
	
	// Decrements refCount and removes unused
	this.releaseSvgGradients(this.oldGradients);
	this.oldGradients = null;
};
