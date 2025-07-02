/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxCellState
 * 
 * Represents the current state of a cell in a given <mxGraphView>.
 * 
 * For edges, the edge label position is stored in <absoluteOffset>.
 * 
 * The size for oversize labels can be retrieved using the boundingBox property
 * of the <text> field as shown below.
 * 
 * (code)
 * var bbox = (state.text != null) ? state.text.boundingBox : null;
 * (end)
 * 
 * Constructor: mxCellState
 * 
 * Constructs a new object that represents the current state of the given
 * cell in the specified view.
 * 
 * Parameters:
 * 
 * view - <mxGraphView> that contains the state.
 * cell - <mxCell> that this state represents.
 * style - Array of key, value pairs that constitute the style.
 */
// 中文注释：
// 构造函数 mxCellState
// 功能：创建一个表示指定视图中单元格当前状态的新对象。
// 参数说明：
//   - view: mxGraphView，包含该单元格状态的视图。
//   - cell: mxCell，该状态代表的单元格。
//   - style: 键值对数组，表示单元格的样式，若未提供则默认为空对象。
function mxCellState(view, cell, style)
{
	this.view = view;
	this.cell = cell;
	this.style = (style != null) ? style : {};
	
	this.origin = new mxPoint();
	this.absoluteOffset = new mxPoint();
    // 中文注释：
    // 初始化关键变量：
    //   - view: 保存视图引用。
    //   - cell: 保存单元格引用。
    //   - style: 保存样式键值对，默认为空对象。
    //   - origin: 初始化为新的 mxPoint 对象，表示子单元格的原点。
    //   - absoluteOffset: 初始化为新的 mxPoint 对象，表示标签的绝对偏移量（边的情况下为标签位置，顶点为相对于左上角的偏移）。
};

/**
 * Extends mxRectangle.
 */
// 中文注释：
// 继承 mxRectangle
// 功能：mxCellState 原型继承自 mxRectangle，扩展了矩形相关的属性和方法。
mxCellState.prototype = new mxRectangle();
mxCellState.prototype.constructor = mxCellState;

/**
 * Variable: view
 * 
 * Reference to the enclosing <mxGraphView>.
 */
// 中文注释：
// 变量：view
// 用途：引用包含该状态的 mxGraphView 对象，表示单元格所属的视图。
mxCellState.prototype.view = null;

/**
 * Variable: cell
 *
 * Reference to the <mxCell> that is represented by this state.
 */
// 中文注释：
// 变量：cell
// 用途：引用该状态代表的 mxCell 对象，表示具体的单元格。
mxCellState.prototype.cell = null;

/**
 * Variable: style
 * 
 * Contains an array of key, value pairs that represent the style of the
 * cell.
 */
// 中文注释：
// 变量：style
// 用途：存储键值对数组，表示单元格的样式配置，例如颜色、字体等。
// 重要配置参数：样式键值对控制单元格的视觉表现。
mxCellState.prototype.style = null;

/**
 * Variable: invalidStyle
 * 
 * Specifies if the style is invalid. Default is false.
 */
// 中文注释：
// 变量：invalidStyle
// 用途：布尔值，指示样式是否无效，默认值为 false。
// 特殊处理：用于标记样式是否需要重新加载或更新。
mxCellState.prototype.invalidStyle = false;

/**
 * Variable: invalid
 * 
 * Specifies if the state is invalid. Default is true.
 */
// 中文注释：
// 变量：invalid
// 用途：布尔值，指示状态是否无效，默认值为yair true。
// 特殊处理：无效状态可能需要重新计算或更新。
mxCellState.prototype.invalid = true;

/**
 * Variable: origin
 *
 * <mxPoint> that holds the origin for all child cells. Default is a new
 * empty <mxPoint>.
 */
// 中文注释：
// 变量：origin
// 用途：mxPoint 对象，存储所有子单元格的原点坐标，默认为空的 mxPoint。
// 说明：用于定位子单元格的相对位置。
mxCellState.prototype.origin = null;

/**
 * Variable: absolutePoints
 * 
 * Holds an array of <mxPoints> that represent the absolute points of an
 * edge.
 */
// 中文注释：
// 变量：absolutePoints
// 用途：mxPoint 数组，存储边的绝对坐标点。
// 说明：用于表示边的实际路径点，仅适用于边。
mxCellState.prototype.absolutePoints = null;

/**
 * Variable: absoluteOffset
 *
 * <mxPoint> that holds the absolute offset. For edges, this is the
 * absolute coordinates of the label position. For vertices, this is the
 * offset of the label relative to the top, left corner of the vertex. 
 */
// 中文注释：
// 变量：absoluteOffset
// 用途：mxPoint 对象，存储绝对偏移量。
// 说明：
//   - 对于边，表示标签的绝对坐标位置。
//   - 对于顶点，表示标签相对于顶点左上角的偏移量。
// 重要配置参数：控制标签的显示位置。
mxCellState.prototype.absoluteOffset = null;

/**
 * Variable: visibleSourceState
 * 
 * Caches the visible source terminal state.
 */
// 中文注释：
// 变量：visibleSourceState
// 用途：缓存可见的源端点状态。
// 说明：用于快速访问边的源端点状态。
mxCellState.prototype.visibleSourceState = null;

/**
 * Variable: visibleTargetState
 * 
 * Caches the visible target terminal state.
 */
// 中文注释：
// 变量：visibleTargetState
// 用途：缓存可见的目标端点状态。
// 说明：用于快速访问边的目标端点状态。
mxCellState.prototype.visibleTargetState = null;

/**
 * Variable: terminalDistance
 * 
 * Caches the distance between the end points for an edge.
 */
// 中文注释：
// 变量：terminalDistance
// 用途：缓存边两个端点之间的距离。
// 说明：用于优化边的绘制或计算。
mxCellState.prototype.terminalDistance = 0;

/**
 * Variable: length
 *
 * Caches the length of an edge.
 */
// 中文注释：
// 变量：length
// 用途：缓存边的总长度。
// 说明：用于存储边的完整长度，便于计算或显示。
mxCellState.prototype.length = 0;

/**
 * Variable: segments
 * 
 * Array of numbers that represent the cached length of each segment of the
 * edge.
 */
// 中文注释：
// 变量：segments
// 用途：数字数组，存储边每段的缓存长度。
// 说明：记录边分段的长度，便于绘制或计算。
mxCellState.prototype.segments = null;

/**
 * Variable: shape
 * 
 * Holds the <mxShape> that represents the cell graphically.
 */
// 中文注释：
// 变量：shape
// 用途：mxShape 对象，表示单元格的图形表现。
// 说明：用于绘制单元格的形状，例如矩形、圆形等。
mxCellState.prototype.shape = null;

/**
 * Variable: text
 * 
 * Holds the <mxText> that represents the label of the cell. Thi smay be
 * null if the cell has no label.
 */
// 中文注释：
// 变量：text
// 用途：mxText 对象，表示单元格的标签。
// 说明：如果单元格没有标签，则为 null。
// 特殊处理：用于控制标签的显示和样式。
mxCellState.prototype.text = null;

/**
 * Variable: unscaledWidth
 * 
 * Holds the unscaled width of the state.
 */
// 中文注释：
// 变量：unscaledWidth
// 用途：存储状态的未缩放宽度。
// 说明：表示单元格的原始宽度，未受视图缩放影响。
mxCellState.prototype.unscaledWidth = null;

/**
 * Variable: unscaledHeight
 * 
 * Holds the unscaled height of the state.
 */
// 中文注释：
// 变量：unscaledHeight
// 用途：存储状态的未缩放高度。
// 说明：表示单元格的原始高度，未受视图缩放影响。
mxCellState.prototype.unscaledHeight = null;

/**
 * Function: getPerimeterBounds
 * 
 * Returns the <mxRectangle> that should be used as the perimeter of the
 * cell.
 * 
 * Parameters:
 * 
 * border - Optional border to be added around the perimeter bounds.
 * bounds - Optional <mxRectangle> to be used as the initial bounds.
 */
// 中文注释：
// 函数：getPerimeterBounds
// 功能：返回单元格的边界矩形，用于定义单元格的外围范围。
// 参数说明：
//   - border: 可选参数，边界周围添加的额外边距，默认值为 0。
//   - bounds: 可选参数，初始边界矩形，若未提供则使用状态的 x, y, width, height。
// 特殊处理：
//   - 如果单元格有形状且形状为固定宽高比的模板，则调整边界以匹配模板比例。
//   - 如果 border 不为 0，则扩展边界。
mxCellState.prototype.getPerimeterBounds = function(border, bounds)
{
	border = border || 0;
	bounds = (bounds != null) ? bounds : new mxRectangle(this.x, this.y, this.width, this.height);
	
	if (this.shape != null && this.shape.stencil != null && this.shape.stencil.aspect == 'fixed')
	{
		var aspect = this.shape.stencil.computeAspect(this.style, bounds.x, bounds.y, bounds.width, bounds.height);
		
		bounds.x = aspect.x;
		bounds.y = aspect.y;
		bounds.width = this.shape.stencil.w0 * aspect.width;
		bounds.height = this.shape.stencil.h0 * aspect.height;
        // 中文注释：
        // 特殊处理：
        //   - 如果形状为固定宽高比的模板，调用 computeAspect 计算调整后的坐标和尺寸。
        //   - 根据模板的原始宽高 (w0, h0) 和宽高比调整边界。
	}
	
	if (border != 0)
	{
		bounds.grow(border);
        // 中文注释：
        // 扩展边界：根据 border 参数增加边界矩形的尺寸。
	}
	
	return bounds;
};

/**
 * Function: setAbsoluteTerminalPoint
 * 
 * Sets the first or last point in <absolutePoints> depending on isSource.
 * 
 * Parameters:
 * 
 * point - <mxPoint> that represents the terminal point.
 * isSource - Boolean that specifies if the first or last point should
 * be assigned.
 */
// 中文注释：
// 函数：setAbsoluteTerminalPoint
// 功能：设置 absolutePoints 数组中的起点或终点。
// 参数说明：
//   - point: mxPoint 对象，表示端点坐标。
//   - isSource: 布尔值，true 表示设置起点，false 表示设置终点。
// 交互逻辑：
//   - 如果是起点（isSource 为 true），设置 absolutePoints 的第一个元素。
//   - 如果是终点（isSource 为 false），设置 absolutePoints 的最后一个元素。
// 特殊处理：
//   - 如果 absolutePoints 为空，则初始化数组并添加点。
//   - 如果数组已有元素，则更新对应位置的点。
mxCellState.prototype.setAbsoluteTerminalPoint = function(point, isSource)
{
	if (isSource)
	{
		if (this.absolutePoints == null)
		{
			this.absolutePoints = [];
		}
		
		if (this.absolutePoints.length == 0)
		{
			this.absolutePoints.push(point);
		}
		else
		{
			this.absolutePoints[0] = point;
		}
	}
	else
	{
		if (this.absolutePoints == null)
		{
			this.absolutePoints = [];
			this.absolutePoints.push(null);
			this.absolutePoints.push(point);
		}
		else if (this.absolutePoints.length == 1)
		{
			this.absolutePoints.push(point);
		}
		else
		{
			this.absolutePoints[this.absolutePoints.length - 1] = point;
		}
	}
};

/**
 * Function: setCursor
 * 
 * Sets the given cursor on the shape and text shape.
 */
// 中文注释：
// 函数：setCursor
// 功能：为单元格的形状和文本标签设置光标样式。
// 参数说明：
//   - cursor: 字符串，表示光标样式（如 "pointer"、"default"等）。
// 交互逻辑：
//   - 检查 shape 是否存在，若存在则为其设置指定的光标样式。
//   - 检查 text 是否存在，若存在则为其设置指定的光标样式。
// 说明：
//   - 用于控制用户与单元格交互时鼠标光标的外观，增强用户体验。
//   - 通常用于响应用户操作，如鼠标悬停时的视觉反馈。
// 特殊处理：
//   - 仅当 shape 或 text 不为 null 时执行设置操作，避免空引用错误。
// 样式设置：
//   - 光标样式直接影响 shape 和 text 的视觉表现。
mxCellState.prototype.setCursor = function(cursor)
{
	if (this.shape != null)
	{
		this.shape.setCursor(cursor);
        // 中文注释：
        // 为形状对象设置光标样式。
        // 说明：调用 shape 对象的 setCursor 方法，应用指定的光标样式。
	}
	
	if (this.text != null)
	{
		this.text.setCursor(cursor);
        // 中文注释：
        // 为文本标签对象设置光标样式。
        // 说明：调用 text 对象的 setCursor 方法，应用指定的光标样式。
	}
};

/**
 * Function: getVisibleTerminal
 * 
 * Returns the visible source or target terminal cell.
 * 
 * Parameters:
 * 
 * source - Boolean that specifies if the source or target cell should be
 * returned.
 */
// 中文注释：
// 函数：getVisibleTerminal
// 功能：返回可见的源端点或目标端点的单元格。
// 参数说明：
//   - source: 布尔值，true 表示返回源端点单元格，false 表示返回目标端点单元格。
// 返回值：mxCell 对象，表示对应的端点单元格；若端点状态不存在，则返回 null。
// 关键步骤：
//   - 调用 getVisibleTerminalState 获取端点状态。
//   - 从状态中提取并返回对应的单元格对象。
// 交互逻辑：
//   - 用于获取边连接的可见端点单元格，通常用于图形交互或渲染。
// 特殊处理：
//   - 如果端点状态 (tmp) 为 null，则返回 null 以避免错误。
// 方法目的：
//   - 提供便捷方式访问边的源或目标端点的单元格。
// 关键变量和函数：
//   - tmp: 存储通过 getVisibleTerminalState 获取的端点状态。
//   - getVisibleTerminalState: 内部调用函数，用于获取端点状态。
mxCellState.prototype.getVisibleTerminal = function(source)
{
	var tmp = this.getVisibleTerminalState(source);
    // 中文注释：
    // 调用 getVisibleTerminalState 函数获取源端点或目标端点的状态。
    // 说明：根据 source 参数决定返回 visibleSourceState 或 visibleTargetState。

	return (tmp != null) ? tmp.cell : null;
// 中文注释：
    // 返回端点状态中的单元格对象。
    // 说明：若 tmp 不为 null，则返回其 cell 属性；否则返回 null
};

/**
 * Function: getVisibleTerminalState
 * 
 * Returns the visible source or target terminal state.
 * 
 * Parameters:
 * 
 * source - Boolean that specifies if the source or target state should be
 * returned.
 */
// 中文注释：
// 函数：getVisibleTerminalState
// 功能：返回可见的源端点或目标端点的状态。
// 参数说明：
//   - source: 布尔值，true 返回源端点状态，false 返回目标端点状态。
// 返回值：返回对应的 mxCellState 对象。
mxCellState.prototype.getVisibleTerminalState = function(source)
{
	return (source) ? this.visibleSourceState : this.visibleTargetState;
};

/**
 * Function: setVisibleTerminalState
 * 
 * Sets the visible source or target terminal state.
 * 
 * Parameters:
 * 
 * terminalState - <mxCellState> that represents the terminal.
 * source - Boolean that specifies if the source or target state should be set.
 */
// 中文注释：
// 函数：setVisibleTerminalState
// 功能：设置可见的源端点或目标端点状态。
// 参数说明：
//   - terminalState: mxCellState 对象，表示端点状态。
//   - source: 布尔值，true 设置源端点状态，false 设置目标端点状态。
// 交互逻辑：根据 source 参数更新对应的状态变量。
mxCellState.prototype.setVisibleTerminalState = function(terminalState, source)
{
	if (source)
	{
		this.visibleSourceState = terminalState;
	}
	else
	{
		this.visibleTargetState = terminalState;
	}
};

/**
 * Function: getCellBounds
 * 
 * Returns the unscaled, untranslated bounds.
 */
// 中文注释：
// 函数：getCellBounds
// 功能：返回未缩放、未平移的单元格边界。
// 返回值：mxRectangle 对象，表示单元格的原始边界。
mxCellState.prototype.getCellBounds = function()
{
	return this.cellBounds;
};

/**
 * Function: getPaintBounds
 * 
 * Returns the unscaled, untranslated paint bounds. This is the same as
 * <getCellBounds> but with a 90 degree rotation if the shape's
 * isPaintBoundsInverted returns true.
 */
// 中文注释：
// 函数：getPaintBounds
// 功能：返回未缩放、未平移的绘制边界。
// 说明：
//   - 与 getCellBounds 类似，但如果 shape 的 isPaintBoundsInverted 返回 true，则边界会旋转 90 度。
// 返回值：mxRectangle 对象，表示绘制边界。
// 特殊处理：根据形状的旋转状态调整边界。
mxCellState.prototype.getPaintBounds = function()
{
	return this.paintBounds;
};

/**
 * Function: updateCachedBounds
 * 
 * Updates the cellBounds and paintBounds.
 */
// 中文注释：
// 函数：updateCachedBounds
// 功能：更新缓存的 cellBounds 和 paintBounds，确保边界反映当前视图的缩放和平移状态。
// 关键步骤：
//   - 获取视图的平移 (translate) 和缩放 (scale) 参数。
//   - 计算未缩放、未平移的 cellBounds（基于 x, y, width, height）。
//   - 将 cellBounds 复制到 paintBounds。
//   - 如果形状存在且需要旋转边界，则对 paintBounds 进行 90 度旋转。
// 参数说明：
//   - 无显式参数，依赖于 this.view 的 translate 和 scale 属性。
// 重要配置参数：
//   - translate (tr): 视图的平移坐标，影响边界的位置。
//   - scale (s): 视图的缩放比例，影响边界的尺寸。
// 交互逻辑：
//   - 用于更新单元格的边界信息，以支持视图缩放或平移时的正确渲染。
// 特殊处理：
//   - 检查 shape 是否存在且需要旋转边界（通过 isPaintBoundsInverted 方法）。
//   - 仅当旋转条件满足时，执行 paintBounds 的 90 度旋转。
// 样式设置：
//   - 不直接涉及样式设置，但边界信息会影响单元格的图形渲染。
// 方法目的：
//   - 确保 cellBounds 和 paintBounds 反映当前的视图状态，用于准确绘制单元格。
// 关键变量和函数：
//   - tr: 视图的平移坐标 (mxPoint 对象)。
//   - s: 视图的缩放比例 (数字)。
//   - cellBounds: mxRectangle 对象，表示未缩放的单元格边界。
//   - paintBounds: mxRectangle 对象，表示用于绘制的边界，可能需要旋转。
//   - shape.isPaintBoundsInverted: 函数，检查是否需要旋转边界。
//   - paintBounds.rotate90: 函数，执行 90 度旋转操作。
mxCellState.prototype.updateCachedBounds = function()
{
	var tr = this.view.translate;
	var s = this.view.scale;
    // 中文注释：
    // 获取视图的平移 (translate) 和缩放 (scale) 参数。
    // 说明：tr 表示视图的坐标偏移，s 表示缩放比例，用于调整边界计算。

	this.cellBounds = new mxRectangle(this.x / s - tr.x, this.y / s - tr.y, this.width / s, this.height / s);
    // 中文注释：
    // 计算未缩放、未平移的单元格边界 (cellBounds)。
    // 说明：根据 x, y, width, height 除以缩放比例并减去平移坐标，生成新的 mxRectangle 对象。

	this.paintBounds = mxRectangle.fromRectangle(this.cellBounds);
    // 中文注释：
    // 初始化绘制边界 (paintBounds)。
    // 说明：通过复制 cellBounds 创建 paintBounds，作为绘制时的边界基础。

	if (this.shape != null && this.shape.isPaintBoundsInverted())
	{
		this.paintBounds.rotate90();
        // 中文注释：
        // 检查形状是否需要旋转边界，并执行 90 度旋转。
        // 说明：如果 shape 存在且 isPaintBoundsInverted 返回 true，则调用 rotate90 方法旋转 paintBounds。
	}
};

/**
 * Destructor: setState
 * 
 * Copies all fields from the given state to this state.
 */
// 中文注释：
// 函数：setState
// 功能：将给定状态对象的所有字段复制到当前状态对象，确保当前状态与源状态一致。
// 参数说明：
//   - state: mxCellState 对象，表示源状态，包含所有需要复制的字段。
// 关键步骤：
//   - 逐一复制源状态的字段到当前状态，包括视图、单元格、样式、坐标、边界等。
// 重要配置参数：
//   - state.view: 视图引用，定义状态所属的图形视图。
//   - state.style: 样式键值对，控制单元格的视觉表现。
//   - state.absoluteOffset: 标签的绝对偏移量，影响标签位置。
// 交互逻辑：
//   - 用于同步状态，通常在需要重置或更新单元格状态时调用。
// 特殊处理：
//   - 直接覆盖所有字段，未进行深拷贝，假设源状态字段为有效引用。
// 样式设置：
//   - 复制的 style 字段会影响单元格的图形渲染样式。
// 方法目的：
//   - 实现状态的完整复制，确保当前状态与源状态完全一致。
// 关键变量和函数：
//   - state: 源状态对象，包含所有待复制的字段。
//   - this.view, this.cell, this.style 等: 当前状态的字段，用于存储复制的值。
mxCellState.prototype.setState = function(state)
{
	this.view = state.view;
    // 中文注释：
    // 复制视图引用。
    // 说明：将源状态的视图 (mxGraphView) 赋值给当前状态。

	this.cell = state.cell;
    // 中文注释：
    // 复制单元格引用。
    // 说明：将源状态的单元格 (mxCell) 赋值给当前状态。

	this.style = state.style;
    // 中文注释：
    // 复制样式键值对。
    // 说明：将源状态的样式数组赋值给当前状态，影响图形渲染。

	this.absolutePoints = state.absolutePoints;
    // 中文注释：
    // 复制边的绝对坐标点数组。
    // 说明：用于存储边的路径点，仅适用于边。

	this.origin = state.origin;
    // 中文注释：
    // 复制子单元格的原点坐标。
    // 说明：将源状态的原点 (mxPoint) 赋值给当前状态。

	this.absoluteOffset = state.absoluteOffset;
    // 中文注释：
    // 复制标签的绝对偏移量。
    // 说明：对于边表示标签位置，对于顶点表示相对于左上角的偏移。

	this.boundingBox = state.boundingBox;
    // 中文注释：
    // 复制边界框。
    // 说明：将源状态的边界矩形 (mxRectangle) 赋值给当前状态。

	this.terminalDistance = state.terminalDistance;
    // 中文注释：
    // 复制端点间距离。
    // 说明：缓存边的两个端点之间的距离。

	this.segments = state.segments;
    // 中文注释：
    // 复制边各段的长度数组。
    // 说明：存储边的分段长度，用于绘制或计算。

	this.length = state.length;
    // 中文注释：
    // 复制边的总长度。
    // 说明：缓存边的完整长度。

	this.x = state.x;
    // 中文注释：
    // 复制 x 坐标。
    // 说明：表示单元格的水平位置。

	this.y = state.y;
    // 中文注释：
    // 复制 y 坐标。
    // 说明：表示单元格的垂直位置。

	this.width = state.width;
    // 中文注释：
    // 复制宽度。
    // 说明：表示单元格的缩放后宽度。

	this.height = state.height;
    // 中文注释：
    // 复制高度。
    // 说明：表示单元格的缩放后高度。

	this.unscaledWidth = state.unscaledWidth;
    // 中文注释：
    // 复制未缩放宽度。
    // 说明：表示单元格的原始宽度，未受视图缩放影响。

	this.unscaledHeight = state.unscaledHeight;
    // 中文注释：
    // 复制未缩放高度。
    // 说明：表示单元格的原始高度，未受视图缩放影响。
};

/**
 * Function: clone
 *
 * Returns a clone of this <mxPoint>.
 */
// 中文注释：
// 函数：clone
// 功能：返回当前状态的深拷贝副本。
// 关键步骤：
//   - 创建新的 mxCellState 对象。
//   - 复制所有属性，包括 absolutePoints、origin、absoluteOffset 和 boundingBox 的深拷贝。
// 返回值：新的 mxCellState 对象，包含当前状态的完整副本。
// 特殊处理：确保复杂对象的深拷贝以避免引用问题。
/**
 * Function: clone
 *
 * Returns a clone of this <mxPoint>.
 */
// 中文注释：
// 函数：clone
// 功能：创建并返回当前 mxCellState 对象的深拷贝副本。
// 关键步骤：
//   - 创建新的 mxCellState 对象，复制视图、单元格和样式。
//   - 深拷贝复杂对象（如 absolutePoints、origin、absoluteOffset、boundingBox）。
//   - 直接复制简单属性（如 terminalDistance、segments 等）。
// 参数说明：
//   - 无显式参数，依赖于当前对象的属性。
// 返回值：新的 mxCellState 对象，包含当前状态的完整副本。
// 重要配置参数：
//   - view: 视图引用，定义状态所属的图形视图。
//   - style: 样式键值对，控制单元格的视觉表现。
//   - absoluteOffset: 标签的绝对偏移量，影响标签位置。
// 交互逻辑：
//   - 用于创建状态的副本，通常在需要独立操作状态而不影响原对象时使用。
// 特殊处理：
//   - 对复杂对象（如 absolutePoints、origin 等）执行深拷贝以避免引用问题。
//   - 简单属性直接赋值，假设它们是基本类型或不需要深拷贝。
// 样式设置：
//   - 复制的 style 字段会保留单元格的图形样式。
// 方法目的：
//   - 提供当前状态的独立副本，确保修改副本不会影响原状态。
// 关键变量和函数：
//   - clone: 新创建的 mxCellState 对象，用于存储副本。
//   - absolutePoints: 边的绝对坐标点数组，需要深拷贝。
//   - origin: 子单元格的原点坐标 (mxPoint)，需要深拷贝。
//   - absoluteOffset: 标签的绝对偏移量 (mxPoint)，需要深拷贝。
//   - boundingBox: 边界矩形 (mxRectangle)，需要深拷贝。
//   - mxCellState: 构造函数，用于创建新状态对象。
//   - clone() 方法：用于深拷贝 mxPoint 和 mxRectangle 对象。
mxCellState.prototype.clone = function()
{
 	var clone = new mxCellState(this.view, this.cell, this.style);
    // 中文注释：
    // 创建新的 mxCellState 对象。
    // 说明：使用当前状态的 view、cell 和 style 初始化副本。

	// Clones the absolute points
	if (this.absolutePoints != null)
	{
		clone.absolutePoints = [];
        // 中文注释：
        // 初始化 absolutePoints 数组。
        // 说明：为副本创建新的空数组以存储深拷贝的坐标点。

		for (var i = 0; i < this.absolutePoints.length; i++)
		{
			clone.absolutePoints[i] = this.absolutePoints[i].clone();
            // 中文注释：
            // 深拷贝 absolutePoints 中的每个 mxPoint 对象。
            // 说明：调用 mxPoint 的 clone 方法，确保副本独立于原对象。
		}
	}

	if (this.origin != null)
	{
		clone.origin = this.origin.clone();
        // 中文注释：
        // 深拷贝 origin 对象。
        // 说明：复制子单元格的原点坐标 (mxPoint)，确保副本独立。
	}

	if (this.absoluteOffset != null)
	{
		clone.absoluteOffset = this.absoluteOffset.clone();
        // 中文注释：
        // 深拷贝 absoluteOffset 对象。
        // 说明：复制标签的绝对偏移量 (mxPoint)，用于边或顶点的标签定位。
	}

	if (this.boundingBox != null)
	{
		clone.boundingBox = this.boundingBox.clone();
        // 中文注释：
        // 深拷贝 boundingBox 对象。
        // 说明：复制边界矩形 (mxRectangle)，用于定义单元格的边界。
	}

	clone.terminalDistance = this.terminalDistance;
    // 中文注释：
    // 复制端点间距离。
    // 说明：直接赋值边的两个端点之间的缓存距离。

	clone.segments = this.segments;
    // 中文注释：
    // 复制边各段的长度数组。
    // 说明：直接赋值分段长度数组，假设不需要深拷贝。

	clone.length = this.length;
    // 中文注释：
    // 复制边的总长度。
    // 说明：直接赋值边的完整长度。

	clone.x = this.x;
    // 中文注释：
    // 复制 x 坐标。
    // 说明：直接赋值单元格的水平位置。

	clone.y = this.y;
    // 中文注释：
    // 复制 y 坐标。
    // 说明：直接赋值单元格的垂直位置。

	clone.width = this.width;
    // 中文注释：
    // 复制宽度。
    // 说明：直接赋值单元格的缩放后宽度。

	clone.height = this.height;
    // 中文注释：
    // 复制高度。
    // 说明：直接赋值单元格的缩放后高度。

	clone.unscaledWidth = this.unscaledWidth;
    // 中文注释：
    // 复制未缩放宽度。
    // 说明：直接赋值单元格的原始宽度，未受视图缩放影响。

	clone.unscaledHeight = this.unscaledHeight;
    // 中文注释：
    // 复制未缩放高度。
    // 说明：直接赋值单元格的原始高度，未受视图缩放影响。

	return clone;
    // 中文注释：
    // 返回克隆的状态对象。
    // 说明：返回包含所有复制字段的新 mxCellState 对象。
};

/**
 * Destructor: destroy
 * 
 * Destroys the state and all associated resources.
 */
// 中文注释：
// 函数：destroy
// 功能：销毁状态及其所有关联资源。
// 说明：调用视图的图形渲染器销毁方法，释放当前状态的资源。
mxCellState.prototype.destroy = function()
{
	this.view.graph.cellRenderer.destroy(this);
};
