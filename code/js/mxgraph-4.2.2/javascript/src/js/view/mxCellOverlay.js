/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxCellOverlay
 *
 * Extends <mxEventSource> to implement a graph overlay, represented by an icon
 * and a tooltip. Overlays can handle and fire <click> events and are added to
 * the graph using <mxGraph.addCellOverlay>, and removed using
 * <mxGraph.removeCellOverlay>, or <mxGraph.removeCellOverlays> to remove all overlays.
 * The <mxGraph.getCellOverlays> function returns the array of overlays for a given
 * cell in a graph. If multiple overlays exist for the same cell, then
 * <getBounds> should be overridden in at least one of the overlays.
 * 
 * Overlays appear on top of all cells in a special layer. If this is not
 * desirable, then the image must be rendered as part of the shape or label of
 * the cell instead.
 *
 * Example:
 * 
 * The following adds a new overlays for a given vertex and selects the cell
 * if the overlay is clicked.
 *
 * (code)
 * var overlay = new mxCellOverlay(img, html);
 * graph.addCellOverlay(vertex, overlay);
 * overlay.addListener(mxEvent.CLICK, function(sender, evt)
 * {
 *   var cell = evt.getProperty('cell');
 *   graph.setSelectionCell(cell);
 * });
 * (end)
 * 
 * For cell overlays to be printed use <mxPrintPreview.printOverlays>.
 *
 * Event: mxEvent.CLICK
 *
 * Fires when the user clicks on the overlay. The <code>event</code> property
 * contains the corresponding mouse event and the <code>cell</code> property
 * contains the cell. For touch devices this is fired if the element receives
 * a touchend event.
 * 
 * Constructor: mxCellOverlay
 *
 * Constructs a new overlay using the given image and tooltip.
 * 
 * Parameters:
 * 
 * image - <mxImage> that represents the icon to be displayed.
 * tooltip - Optional string that specifies the tooltip.
 * align - Optional horizontal alignment for the overlay. Possible
 * values are <ALIGN_LEFT>, <ALIGN_CENTER> and <ALIGN_RIGHT>
 * (default).
 * verticalAlign - Vertical alignment for the overlay. Possible
 * values are <ALIGN_TOP>, <ALIGN_MIDDLE> and <ALIGN_BOTTOM>
 * (default).
 */
// 中文注释：
// 类：mxCellOverlay
// 扩展自 mxEventSource，用于实现图表覆盖物（overlay），表现为图标和工具提示。
// 覆盖物可以处理并触发点击事件，通过 mxGraph.addCellOverlay 添加到图表，
// 使用 mxGraph.removeCellOverlay 或 mxGraph.removeCellOverlays 删除。
// mxGraph.getCellOverlays 返回指定单元格的覆盖物数组。
// 如果同一单元格有多个覆盖物，需重写 getBounds 方法以避免重叠。
// 覆盖物显示在所有单元格之上的特殊图层中，若不希望如此，需将图像渲染为单元格的形状或标签。
// 示例：为顶点添加覆盖物，并在点击时选中对应单元格。
// 事件 mxEvent.CLICK：当用户点击覆盖物时触发，事件对象包含鼠标事件和单元格信息。
// 构造函数：mxCellOverlay
// 功能：使用指定的图像和工具提示创建新的覆盖物。
// 参数说明：
// - image: 表示要显示的图标（mxImage 对象）。
// - tooltip: 可选的工具提示字符串。
// - align: 覆盖物的水平对齐方式，可选值包括 ALIGN_LEFT、ALIGN_CENTER 和 ALIGN_RIGHT（默认）。
// - verticalAlign: 覆盖物的垂直对齐方式，可选值包括 ALIGN_TOP、ALIGN_MIDDLE 和 ALIGN_BOTTOM（默认）。

function mxCellOverlay(image, tooltip, align, verticalAlign, offset, cursor)
{
	this.image = image;
	this.tooltip = tooltip;
	this.align = (align != null) ? align : this.align;
	this.verticalAlign = (verticalAlign != null) ? verticalAlign : this.verticalAlign;
	this.offset = (offset != null) ? offset : new mxPoint();
	this.cursor = (cursor != null) ? cursor : 'help';
    // 中文注释：
    // 构造函数实现：
    // 初始化覆盖物的核心属性。
    // - image: 保存表示覆盖物图标的 mxImage 对象。
    // - tooltip: 保存工具提示字符串，若无则为 null。
    // - align: 设置水平对齐方式，若未提供则使用默认值（mxConstants.ALIGN_RIGHT）。
    // - verticalAlign: 设置垂直对齐方式，若未提供则使用默认值（mxConstants.ALIGN_BOTTOM）。
    // - offset: 设置偏移量（mxPoint 对象），若未提供则默认为 (0,0)。
    // - cursor: 设置鼠标悬停时的光标样式，默认值为 'help'。
    // 重要配置参数：align 和 verticalAlign 控制覆盖物的显示位置，offset 用于微调位置。
};

/**
 * Extends mxEventSource.
 */
mxCellOverlay.prototype = new mxEventSource();
mxCellOverlay.prototype.constructor = mxCellOverlay;
// 中文注释：
// 继承 mxEventSource，使 mxCellOverlay 具有事件触发能力。
// 设置构造函数为 mxCellOverlay，确保正确初始化。

/**
 * Variable: image
 *
 * Holds the <mxImage> to be used as the icon.
 */
mxCellOverlay.prototype.image = null;
// 中文注释：
// 变量：image
// 用途：存储用作覆盖物图标的 mxImage 对象。

/**
 * Variable: tooltip
 * 
 * Holds the optional string to be used as the tooltip.
 */
mxCellOverlay.prototype.tooltip = null;
// 中文注释：
// 变量：tooltip
// 用途：存储可选的工具提示字符串，鼠标悬停时显示。

/**
 * Variable: align
 * 
 * Holds the horizontal alignment for the overlay. Default is
 * <mxConstants.ALIGN_RIGHT>. For edges, the overlay always appears in the
 * center of the edge.
 */
mxCellOverlay.prototype.align = mxConstants.ALIGN_RIGHT;
// 中文注释：
// 变量：align
// 用途：存储覆盖物的水平对齐方式，默认为 mxConstants.ALIGN_RIGHT。
// 特殊处理：对于边（edge），覆盖物始终显示在边的中心。

/**
 * Variable: verticalAlign
 * 
 * Holds the vertical alignment for the overlay. Default is
 * <mxConstants.ALIGN_BOTTOM>. For edges, the overlay always appears in the
 * center of the edge.
 */
mxCellOverlay.prototype.verticalAlign = mxConstants.ALIGN_BOTTOM;
// 中文注释：
// 变量：verticalAlign
// 用途：存储覆盖物的垂直对齐方式，默认为 mxConstants.ALIGN_BOTTOM。
// 特殊处理：对于边（edge），覆盖物始终显示在边的中心。

/**
 * Variable: offset
 * 
 * Holds the offset as an <mxPoint>. The offset will be scaled according to the
 * current scale.
 */
mxCellOverlay.prototype.offset = null;
// 中文注释：
// 变量：offset
// 用途：存储覆盖物的偏移量（mxPoint 对象），会根据当前缩放比例进行缩放调整。
// 重要配置参数：用于微调覆盖物的显示位置。

/**
 * Variable: cursor
 * 
 * Holds the cursor for the overlay. Default is 'help'.
 */
mxCellOverlay.prototype.cursor = null;
// 中文注释：
// 变量：cursor
// 用途：存储覆盖物的鼠标光标样式，默认为 'help'。
// 样式设置：影响用户鼠标悬停时的视觉反馈。

/**
 * Variable: defaultOverlap
 * 
 * Defines the overlapping for the overlay, that is, the proportional distance
 * from the origin to the point defined by the alignment. Default is 0.5.
 */
mxCellOverlay.prototype.defaultOverlap = 0.5;
// 中文注释：
// 变量：defaultOverlap
// 用途：定义覆盖物的重叠比例，即从起始点到对齐点的比例距离，默认为 0.5。
// 重要配置参数：影响覆盖物相对于单元格的对齐位置。

/**
 * Function: getBounds
 * 
 * Returns the bounds of the overlay for the given <mxCellState> as an
 * <mxRectangle>. This should be overridden when using multiple overlays
 * per cell so that the overlays do not overlap.
 * 
 * The following example will place the overlay along an edge (where
 * x=[-1..1] from the start to the end of the edge and y is the
 * orthogonal offset in px).
 * 
 * (code)
 * overlay.getBounds = function(state)
 * {
 *   var bounds = mxCellOverlay.prototype.getBounds.apply(this, arguments);
 *   
 *   if (state.view.graph.getModel().isEdge(state.cell))
 *   {
 *     var pt = state.view.getPoint(state, {x: 0, y: 0, relative: true});
 *     
 *     bounds.x = pt.x - bounds.width / 2;
 *     bounds.y = pt.y - bounds.height / 2;
 *   }
 *   
 *   return bounds;
 * };
 * (end)
 * 
 * Parameters:
 * 
 * state - <mxCellState> that represents the current state of the
 * associated cell.
 */
mxCellOverlay.prototype.getBounds = function(state)
{
    // 中文注释：
    // 方法：getBounds
    // 目的：计算并返回覆盖物在给定 mxCellState 下的边界（mxRectangle 对象）。
    // 功能：根据单元格状态确定覆盖物的显示位置和大小。
    // 参数说明：
    // - state: 表示关联单元格当前状态的 mxCellState 对象，包含单元格的坐标、尺寸等信息。
    // 特殊处理：若同一单元格有多个覆盖物，需重写此方法以避免重叠。

	var isEdge = state.view.graph.getModel().isEdge(state.cell);
	var s = state.view.scale;
	var pt = null;
    // 中文注释：
    // 关键变量：
    // - isEdge: 布尔值，判断单元格是否为边（edge），通过 state.view.graph.getModel().isEdge(state.cell) 确定。
    // - s: 当前视图的缩放比例，用于调整覆盖物的尺寸和位置。
    // - pt: mxPoint 对象，表示覆盖物的参考点坐标，初始为 null。
    // 关键步骤：初始化核心变量，获取单元格类型和缩放比例，为后续位置计算做准备。

	var w = this.image.width;
	var h = this.image.height;
    // 中文注释：
    // 关键变量：
    // - w: 覆盖物图标的宽度，来自 this.image.width。
    // - h: 覆盖物图标的高度，来自 this.image.height。
    // 用途：用于计算覆盖物的边界尺寸，并根据缩放比例调整。

	if (isEdge)
	{
		var pts = state.absolutePoints;
		
		if (pts.length % 2 == 1)
		{
			pt = pts[Math.floor(pts.length / 2)];
		}
		else
		{
			var idx = pts.length / 2;
			var p0 = pts[idx-1];
			var p1 = pts[idx];
			pt = new mxPoint(p0.x + (p1.x - p0.x) / 2,
				p0.y + (p1.y - p0.y) / 2);
		}
        // 中文注释：
        // 逻辑分支：处理边（edge）的情况。
        // 关键步骤：
        // - 获取边的绝对点数组（pts = state.absolutePoints）。
        // - 如果点数为奇数，取中间点作为参考点（pt）。
        // - 如果点数为偶数，计算两点（p0 和 p1）之间的中点作为参考点。
        // 特殊处理：确保覆盖物始终位于边的中心位置。
	}
	else
	{
		pt = new mxPoint();
		
		if (this.align == mxConstants.ALIGN_LEFT)
		{
			pt.x = state.x;
		}
		else if (this.align == mxConstants.ALIGN_CENTER)
		{
			pt.x = state.x + state.width / 2;
		}
		else
		{
			pt.x = state.x + state.width;
		}
        // 中文注释：
        // 逻辑分支：处理顶点（vertex）的情况。
        // 关键步骤：根据水平对齐方式（align）设置参考点 x 坐标。
        // 重要配置参数：
        // - this.align: 控制覆盖物的水平对齐，可选值包括 ALIGN_LEFT、ALIGN_CENTER、ALIGN_RIGHT（默认）。
        // - pt.x 计算：
        //   - ALIGN_LEFT: 覆盖物位于单元格左侧（state.x）。
        //   - ALIGN_CENTER: 覆盖物位于单元格水平中心（state.x + state.width / 2）。
        //   - ALIGN_RIGHT: 覆盖物位于单元格右侧（state.x + state.width）。
        // 样式设置：align 属性直接影响覆盖物的水平显示位置。

		if (this.verticalAlign == mxConstants.ALIGN_TOP)
		{
			pt.y = state.y;
		}
		else if (this.verticalAlign == mxConstants.ALIGN_MIDDLE)
		{
			pt.y = state.y + state.height / 2;
		}
		else
		{
			pt.y = state.y + state.height;
		}
        // 中文注释：
        // 关键步骤：根据垂直对齐方式（verticalAlign）设置参考点 y 坐标。
  // 重要配置参数：
        // - this.verticalAlign: 控制覆盖物的垂直对齐，可选值包括 ALIGN_TOP、ALIGN_MIDDLE、ALIGN_BOTTOM（默认）。
        // - pt.y 计算：
        //   - ALIGN_TOP: 覆盖物位于单元格顶部（state.y）。
        //   - ALIGN_MIDDLE: 覆盖物位于单元格垂直中心（state.y + state.height / 2）。
        //   - ALIGN_BOTTOM: 覆盖物位于单元格底部（state.y + state.height）。
        // 样式设置：verticalAlign 属性直接影响覆盖物的垂直显示位置。
	}

	return new mxRectangle(Math.round(pt.x - (w * this.defaultOverlap - this.offset.x) * s),
		Math.round(pt.y - (h * this.defaultOverlap - this.offset.y) * s), w * s, h * s);
    // 中文注释：
    // 返回值：返回 mxRectangle 对象，表示覆盖物的边界（位置和尺寸）。
    // 关键步骤：
    // - 计算覆盖物的左上角坐标：
    //   - x 坐标：pt.x - (w * this.defaultOverlap - this.offset.x) * s。
    //   - y 坐标：pt.y - (h * this.defaultOverlap - this.offset.y) * s。
    // - 计算覆盖物的宽高：w * s（宽度缩放后），h * s（高度缩放后）。
    // 重要配置参数：
    // - this.defaultOverlap: 重叠比例（默认 0.5），控制覆盖物相对于参考点的偏移比例。
    // - this.offset: 偏移量（mxPoint 对象），用于微调覆盖物位置。
    // - s: 缩放比例，确保覆盖物大小和位置随图表缩放调整。
    // 特殊处理：坐标和尺寸均四舍五入（Math.round）以确保像素对齐。
    // 交互逻辑：通过 align、verticalAlign、offset 和 defaultOverlap 的组合，精确控制覆盖物的显示位置。
};
// 中文注释：
// 方法：getBounds
// 目的：返回覆盖物在给定 mxCellState 下的边界（mxRectangle 对象）。
// 功能：根据单元格状态计算覆盖物的显示位置和大小。
// 参数说明：
// - state: 表示关联单元格当前状态的 mxCellState 对象。
// 关键步骤：
// 1. 判断单元格是否为边（edge），通过 state.view.graph.getModel().isEdge(state.cell)。
// 2. 获取当前缩放比例 (s) 和图标的宽高 (w, h)。
// 3. 如果是边，计算边的中心点作为覆盖物位置；
//    - 若点的数量为奇数，取中间点；
//    - 若点的数量为偶数，取两点之间的中点。
// 4. 如果是顶点，根据 align 和 verticalAlign 属性确定覆盖物的 x 和 y 坐标。
// 5. 应用偏移量 (offset) 和重叠比例 (defaultOverlap)，并根据缩放比例调整最终边界。
// 返回值：mxRectangle 对象，包含覆盖物的位置和缩放后的尺寸。
// 特殊处理：
// - 对于边，覆盖物始终位于中心。
// - 若同一单元格有多个覆盖物，需重写此方法以避免重叠。
// - 偏移量和重叠比例会影响最终位置，需根据缩放比例调整。

/**
 * Function: toString
 * 
 * Returns the textual representation of the overlay to be used as the
 * tooltip. This implementation returns <tooltip>.
 */
mxCellOverlay.prototype.toString = function()
{
	return this.tooltip;
};
// 中文注释：
// 方法：toString
// 目的：返回覆盖物的文本表示，用于显示工具提示。
// 功能：直接返回存储在 tooltip 属性中的字符串。
// 返回值：tooltip 属性的值，若无则返回 null。