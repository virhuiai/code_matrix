/**
 * Copyright (c) 2006-2017, JGraph Ltd
 * Copyright (c) 2006-2017, Gaudenz Alder
 */
/**
 * Class: mxCellRenderer
 * 
 * Renders cells into a document object model. The <defaultShapes> is a global
 * map of shapename, constructor pairs that is used in all instances. You can
 * get a list of all available shape names using the following code.
 * 
 * In general the cell renderer is in charge of creating, redrawing and
 * destroying the shape and label associated with a cell state, as well as
 * some other graphical objects, namely controls and overlays. The shape
 * hieararchy in the display (ie. the hierarchy in which the DOM nodes
 * appear in the document) does not reflect the cell hierarchy. The shapes
 * are a (flat) sequence of shapes and labels inside the draw pane of the
 * graph view, with some exceptions, namely the HTML labels being placed
 * directly inside the graph container for certain browsers.
 * 
 * (code)
 * mxLog.show();
 * for (var i in mxCellRenderer.defaultShapes)
 * {
 *   mxLog.debug(i);
 * }
 * (end)
 *
 * Constructor: mxCellRenderer
 * 
 * Constructs a new cell renderer with the following built-in shapes:
 * arrow, rectangle, ellipse, rhombus, image, line, label, cylinder,
 * swimlane, connector, actor and cloud.
 */
/**
 * 类: mxCellRenderer
 *
 * 将单元格渲染到文档对象模型（DOM）中。`defaultShapes` 是一个全局的形状名称和构造函数对的映射表，适用于所有实例。可以使用以下代码获取所有可用形状名称的列表。
 *
 * 总体来说，单元格渲染器负责创建、重绘和销毁与单元格状态相关的形状和标签，以及一些其他图形对象（如控件和覆盖层）。显示中的形状层级（即 DOM 节点在文档中的层级）不反映单元格层级。形状是图形视图绘制面板中的一组（平面）形状和标签序列，某些浏览器中的 HTML 标签除外，它们直接放置在图形容器中。
 *
 * 构造函数: mxCellRenderer
 *
 * 构造一个新的单元格渲染器，包含以下内置形状：
 * 箭头、矩形、椭圆、菱形、图像、线条、标签、圆柱、泳道、连接器、角色和云。
 */
function mxCellRenderer() { };

/**
 * Variable: defaultShapes
 * 
 * Static array that contains the globally registered shapes which are
 * known to all instances of this class. For adding new shapes you should
 * use the static <mxCellRenderer.registerShape> function.
 */
/**
 * 变量: defaultShapes
 *
 * 静态数组，包含全局注册的形状，供该类的所有实例使用。添加新形状应使用静态函数 `<mxCellRenderer.registerShape>`。
 *
 * 用途：存储所有可用的形状名称和对应的构造函数，便于在渲染时动态创建形状。
 */
mxCellRenderer.defaultShapes = new Object();

/**
 * Variable: defaultEdgeShape
 * 
 * Defines the default shape for edges. Default is <mxConnector>.
 */
/**
 * 变量: defaultEdgeShape
 *
 * 定义边的默认形状。默认值为 `<mxConnector>`。
 *
 * 重要配置参数：指定边（连接线）的默认渲染形状为连接器，确保边的基本样式一致。
 */
mxCellRenderer.prototype.defaultEdgeShape = mxConnector;

/**
 * Variable: defaultVertexShape
 * 
 * Defines the default shape for vertices. Default is <mxRectangleShape>.
 */
/**
 * 变量: defaultVertexShape
 *
 * 定义顶点的默认形状。默认值为 `<mxRectangleShape>`。
 *
 * 重要配置参数：指定顶点（节点）的默认渲染形状为矩形，确保节点的基本样式一致。
 */
mxCellRenderer.prototype.defaultVertexShape = mxRectangleShape;

/**
 * Variable: defaultTextShape
 * 
 * Defines the default shape for labels. Default is <mxText>.
 */
/**
 * 变量: defaultTextShape
 *
 * 定义标签的默认形状。默认值为 `<mxText>`。
 *
 * 重要配置参数：指定标签的默认渲染形状为文本，确保标签显示一致。
 */
mxCellRenderer.prototype.defaultTextShape = mxText;

/**
 * Variable: legacyControlPosition
 * 
 * Specifies if the folding icon should ignore the horizontal
 * orientation of a swimlane. Default is true.
 */
/**
 * 变量: legacyControlPosition
 *
 * 指定折叠图标是否应忽略泳道的水平方向。默认值为 `true`。
 *
 * 重要配置参数：控制折叠图标在泳道中的位置行为，设置为 `true` 以保持向后兼容，忽略水平方向。
 */
mxCellRenderer.prototype.legacyControlPosition = true;

/**
 * Variable: legacySpacing
 * 
 * Specifies if spacing and label position should be ignored if overflow is
 * fill or width. Default is true for backwards compatiblity.
 */
/**
 * 变量: legacySpacing
 *
 * 指定当溢出设置为 `fill` 或 `width` 时，是否忽略间距和标签位置。默认值为 `true`，以保持向后兼容。
 *
 * 重要配置参数：控制标签间距和位置的行为，影响标签在特定溢出模式下的渲染效果。
 */
mxCellRenderer.prototype.legacySpacing = true;

/**
 * Variable: antiAlias
 * 
 * Anti-aliasing option for new shapes. Default is true.
 */
/**
 * 变量: antiAlias
 *
 * 新形状的抗锯齿选项。默认值为 `true`。
 *
 * 重要配置参数：启用抗锯齿以提高新形状的渲染质量，默认开启以确保图形平滑。
 */
mxCellRenderer.prototype.antiAlias = true;

/**
 * Variable: minSvgStrokeWidth
 * 
 * Minimum stroke width for SVG output.
 */
/**
 * 变量: minSvgStrokeWidth
 *
 * SVG 输出的最小描边宽度。
 *
 * 重要配置参数：定义 SVG 渲染中形状描边的最小宽度，确保图形在缩放时保持清晰。
 */
mxCellRenderer.prototype.minSvgStrokeWidth = 1;

/**
 * Variable: forceControlClickHandler
 * 
 * Specifies if the enabled state of the graph should be ignored in the control
 * click handler (to allow folding in disabled graphs). Default is false.
 */
/**
 * 变量: forceControlClickHandler
 *
 * 指定在控件点击处理程序中是否忽略图形的启用状态（允许在禁用图形中进行折叠）。默认值为 `false`。
 *
 * 重要配置参数：控制是否强制处理控件点击事件，即使图形处于禁用状态。
 */
mxCellRenderer.prototype.forceControlClickHandler = false;

/**
 * Function: registerShape
 * 
 * Registers the given constructor under the specified key in this instance
 * of the renderer.
 * 
 * Example:
 * 
 * (code)
 * mxCellRenderer.registerShape(mxConstants.SHAPE_RECTANGLE, mxRectangleShape);
 * (end)
 * 
 * Parameters:
 * 
 * key - String representing the shape name.
 * shape - Constructor of the <mxShape> subclass.
 */
/**
 * 函数: registerShape
 *
 * 在渲染器的该实例中注册指定键下的构造函数。
 *
 * 示例：
 *
 * (代码)
 * mxCellRenderer.registerShape(mxConstants.SHAPE_RECTANGLE, mxRectangleShape);
 * (结束)
 *
 * 参数：
 *
 * key - 表示形状名称的字符串。
 * shape - <mxShape> 子类的构造函数。
 *
 * 功能说明：将形状名称与对应的构造函数关联，存储在 `defaultShapes` 中，以便动态创建形状。
 * 用途：扩展渲染器支持的形状类型，允许自定义形状的注册。
 */
mxCellRenderer.registerShape = function(key, shape)
{
	mxCellRenderer.defaultShapes[key] = shape;
    // 中文注释：将指定的形状构造函数注册到 defaultShapes 映射中，key 为形状名称，shape 为构造函数。
};

// Adds default shapes into the default shapes array
// 添加默认形状到 defaultShapes 数组中
mxCellRenderer.registerShape(mxConstants.SHAPE_RECTANGLE, mxRectangleShape);
mxCellRenderer.registerShape(mxConstants.SHAPE_ELLIPSE, mxEllipse);
mxCellRenderer.registerShape(mxConstants.SHAPE_RHOMBUS, mxRhombus);
mxCellRenderer.registerShape(mxConstants.SHAPE_CYLINDER, mxCylinder);
mxCellRenderer.registerShape(mxConstants.SHAPE_CONNECTOR, mxConnector);
mxCellRenderer.registerShape(mxConstants.SHAPE_ACTOR, mxActor);
mxCellRenderer.registerShape(mxConstants.SHAPE_TRIANGLE, mxTriangle);
mxCellRenderer.registerShape(mxConstants.SHAPE_HEXAGON, mxHexagon);
mxCellRenderer.registerShape(mxConstants.SHAPE_CLOUD, mxCloud);
mxCellRenderer.registerShape(mxConstants.SHAPE_LINE, mxLine);
mxCellRenderer.registerShape(mxConstants.SHAPE_ARROW, mxArrow);
mxCellRenderer.registerShape(mxConstants.SHAPE_ARROW_CONNECTOR, mxArrowConnector);
mxCellRenderer.registerShape(mxConstants.SHAPE_DOUBLE_ELLIPSE, mxDoubleEllipse);
mxCellRenderer.registerShape(mxConstants.SHAPE_SWIMLANE, mxSwimlane);
mxCellRenderer.registerShape(mxConstants.SHAPE_IMAGE, mxImageShape);
mxCellRenderer.registerShape(mxConstants.SHAPE_LABEL, mxLabel);
// 中文注释：将一系列内置形状（如矩形、椭圆、菱形等）注册到 defaultShapes 中，用于渲染不同类型的单元格。

/**
 * Function: initializeShape
 * 
 * Initializes the shape in the given state by calling its init method with
 * the correct container after configuring it using <configureShape>.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the shape should be initialized.
 */
/**
 * 函数: initializeShape
 *
 * 通过调用形状的 init 方法，使用正确的容器初始化给定状态中的形状，在此之前使用 <configureShape> 进行配置。
 *
 * 参数：
 *
 * state - 要初始化的形状的 <mxCellState>。
 *
 * 功能说明：为单元格状态初始化形状，设置渲染环境并将其添加到绘制面板。
 * 关键步骤：1. 设置形状的渲染方言（dialect）；2. 配置形状样式；3. 初始化形状并将其添加到绘制面板。
 */
mxCellRenderer.prototype.initializeShape = function(state)
{
	state.shape.dialect = state.view.graph.dialect;
    // 中文注释：设置形状的渲染方言，与图形视图的渲染环境一致。
	this.configureShape(state);
    // 中文注释：调用 configureShape 配置形状的样式和属性。
	state.shape.init(state.view.getDrawPane());
    // 中文注释：将形状初始化并添加到图形视图的绘制面板中。
};

/**
 * Function: createShape
 * 
 * Creates and returns the shape for the given cell state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the shape should be created.
 */
/**
 * 函数: createShape
 *
 * 为给定的单元格状态创建并返回形状。
 *
 * 参数：
 *
 * state - 要创建形状的 <mxCellState>。
 *
 * 功能说明：根据单元格状态的样式创建对应的形状对象。
 * 关键步骤：
 * 1. 检查是否存在形状模板（stencil），若有则使用模板创建形状。
 * 2. 若无模板，则根据状态样式选择合适的形状构造函数。
 * 3. 返回创建的形状对象。
 */
mxCellRenderer.prototype.createShape = function(state)
{
	var shape = null;
	
	if (state.style != null)
	{
		// Checks if there is a stencil for the name and creates
		// a shape instance for the stencil if one exists
		var stencil = mxStencilRegistry.getStencil(state.style[mxConstants.STYLE_SHAPE]);
        // 中文注释：检查是否存在与样式中形状名称对应的模板（stencil）。

		if (stencil != null)
		{
			shape = new mxShape(stencil);
            // 中文注释：如果存在模板，则使用模板创建新的形状对象。
		}
		else
		{
			var ctor = this.getShapeConstructor(state);
            // 中文注释：若无模板，则调用 getShapeConstructor 获取形状构造函数。
			shape = new ctor();
            // 中文注释：使用构造函数创建新的形状对象。
		}
	}
	
	return shape;
    // 中文注释：返回创建的形状对象，若样式为空则返回 null。
};

/**
 * Function: createIndicatorShape
 * 
 * Creates the indicator shape for the given cell state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the indicator shape should be created.
 */
/**
 * 函数: createIndicatorShape
 *
 * 为给定的单元格状态创建指示器形状。
 *
 * 参数：
 *
 * state - 要创建指示器形状的 <mxCellState>。
 *
 * 功能说明：为单元格状态创建用于显示指示器的形状（如状态或高亮的视觉指示）。
 * 关键步骤：根据图形视图的指示器形状配置，从 defaultShapes 中获取对应的形状并赋值给 state.shape.indicatorShape。
 */
mxCellRenderer.prototype.createIndicatorShape = function(state)
{
	state.shape.indicatorShape = this.getShape(state.view.graph.getIndicatorShape(state));
    // 中文注释：获取图形视图中定义的指示器形状，并将其设置为当前状态的指示器形状。
};

/**
 * Function: getShape
 * 
 * Returns the shape for the given name from <defaultShapes>.
 */
/**
 * 函数: getShape
 *
 * 从 defaultShapes 中返回指定名称的形状。
 *
 * 参数：
 *
 * name - 形状名称。
 *
 * 功能说明：根据形状名称查找并返回对应的形状构造函数。
 * 返回值：形状构造函数，若名称为空或未找到则返回 null。
 */
mxCellRenderer.prototype.getShape = function(name)
{
	return (name != null) ? mxCellRenderer.defaultShapes[name] : null;
    // 中文注释：如果名称不为空，则返回 defaultShapes 中对应的形状构造函数，否则返回 null。
};

/**
 * Function: getShapeConstructor
 * 
 * Returns the constructor to be used for creating the shape.
 */
/**
 * 函数: getShapeConstructor
 *
 * 返回用于创建形状的构造函数。
 *
 * 参数：
 *
 * state - 包含样式信息的 <mxCellState>。
 *
 * 功能说明：根据单元格状态的样式或类型（边或顶点）选择合适的形状构造函数。
 * 关键步骤：
 * 1. 尝试根据样式中的形状名称获取构造函数。
 * 2. 如果未找到，则根据单元格类型（边或顶点）返回默认构造函数（defaultEdgeShape 或 defaultVertexShape）。
 */
mxCellRenderer.prototype.getShapeConstructor = function(state)
{
	var ctor = this.getShape(state.style[mxConstants.STYLE_SHAPE]);
    // 中文注释：根据样式中的形状名称获取对应的构造函数。

	if (ctor == null)
	{
		ctor = (state.view.graph.getModel().isEdge(state.cell)) ?
			this.defaultEdgeShape : this.defaultVertexShape;
        // 中文注释：如果未找到构造函数，则根据单元格类型选择默认边形状或默认顶点形状。
	}
	
	return ctor;
    // 中文注释：返回选定的形状构造函数。
};

/**
 * Function: configureShape
 * 
 * Configures the shape for the given cell state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the shape should be configured.
 */
/**
 * 函数: configureShape
 *
 * 为给定的单元格状态配置形状。
 *
 * 参数：
 *
 * state - 要配置形状的 <mxCellState>。
 *
 * 功能说明：根据单元格状态的样式设置形状的属性，如图像、颜色、渐变等。
 * 关键步骤：
 * 1. 应用状态的样式到形状。
 * 2. 设置形状的图像、指示器颜色、描边颜色、渐变颜色和方向。
 * 3. 调用 postConfigureShape 进行颜色关键字解析。
 */
mxCellRenderer.prototype.configureShape = function(state)
{
	state.shape.apply(state);
    // 中文注释：将单元格状态的样式应用到形状对象。
	state.shape.image = state.view.graph.getImage(state);
    // 中文注释：设置形状的图像，从图形视图获取。
	state.shape.indicatorColor = state.view.graph.getIndicatorColor(state);
    // 中文注释：设置形状的指示器颜色，从图形视图获取。
	state.shape.indicatorStrokeColor = state.style[mxConstants.STYLE_INDICATOR_STROKECOLOR];
    // 中文注释：设置指示器的描边颜色，从样式中获取。
	state.shape.indicatorGradientColor = state.view.graph.getIndicatorGradientColor(state);
    // 中文注释：设置指示器的渐变颜色，从图形视图获取。
	state.shape.indicatorDirection = state.style[mxConstants.STYLE_INDICATOR_DIRECTION];
    // 中文注释：设置指示器的方向，从样式中获取。
	state.shape.indicatorImage = state.view.graph.getIndicatorImage(state);
    // 中文注释：设置指示器的图像，从图形视图获取。

	this.postConfigureShape(state);
    // 中文注释：调用 postConfigureShape 处理颜色关键字（如 inherit、indicated、swimlane）。
};

/**
 * Function: postConfigureShape
 * 
 * Replaces any reserved words used for attributes, eg. inherit,
 * indicated or swimlane for colors in the shape for the given state.
 * This implementation resolves these keywords on the fill, stroke
 * and gradient color keys.
 */
/**
 * 函数: postConfigureShape
 *
 * 替换形状中用于属性的保留字（如 inherit、indicated、swimlane），针对给定状态的填充、描边和渐变颜色。
 *
 * 参数：
 *
 * state - 要配置形状的 <mxCellState>。
 *
 * 功能说明：解析形状中的颜色关键字，确保填充、描边和渐变颜色正确应用。
 * 关键步骤：对指示器渐变色、指示器颜色、渐变色、描边色和填充色进行关键字解析。
 * 特殊处理：处理特定关键字（如 inherit、swimlane、indicated），以确保颜色值正确解析。
 */
mxCellRenderer.prototype.postConfigureShape = function(state)
{
	if (state.shape != null)
	{
		this.resolveColor(state, 'indicatorGradientColor', mxConstants.STYLE_GRADIENTCOLOR);
        // 中文注释：解析指示器渐变色的关键字。
		this.resolveColor(state, 'indicatorColor', mxConstants.STYLE_FILLCOLOR);
        // 中文注释：解析指示器颜色的关键字。
		this.resolveColor(state, 'gradient', mxConstants.STYLE_GRADIENTCOLOR);
        // 中文注释：解析渐变色的关键字。
		this.resolveColor(state, 'stroke', mxConstants.STYLE_STROKECOLOR);
        // 中文注释：解析描边色的关键字。
		this.resolveColor(state, 'fill', mxConstants.STYLE_FILLCOLOR);
        // 中文注释：解析填充色的关键字。
	}
};

/**
 * Function: checkPlaceholderStyles
 * 
 * Checks if the style of the given <mxCellState> contains 'inherit',
 * 'indicated' or 'swimlane' for colors that support those keywords.
 */
/**
 * 函数: checkPlaceholderStyles
 *
 * 检查给定 <mxCellState> 的样式是否包含支持的颜色关键字 'inherit'、'indicated' 或 'swimlane'。
 *
 * 参数：
 *
 * state - 要检查的 <mxCellState>。
 *
 * 功能说明：检查样式中是否使用了特定颜色关键字。
 * 关键步骤：遍历指定的样式属性（如填充色、描边色等），检查是否包含关键字。
 * 返回值：如果包含关键字返回 true，否则返回 false。
 */
mxCellRenderer.prototype.checkPlaceholderStyles = function(state)
{
	// LATER: Check if the color has actually changed
	if (state.style != null)
	{
		var values = ['inherit', 'swimlane', 'indicated'];
		var styles = [mxConstants.STYLE_FILLCOLOR, mxConstants.STYLE_STROKECOLOR,
			mxConstants.STYLE_GRADIENTCOLOR, mxConstants.STYLE_FONTCOLOR];
        // 中文注释：定义支持的关键字和需要检查的样式属性。

		for (var i = 0; i < styles.length; i++)
		{
			if (mxUtils.indexOf(values, state.style[styles[i]]) >= 0)
			{
				return true;
                // 中文注释：如果样式属性值包含关键字，则返回 true。
			}
		}
	}
	
	return false;
    // 中文注释：如果没有找到关键字或样式为空，返回 false。
};

/**
 * Function: resolveColor
 * 
 * Resolves special keywords 'inherit', 'indicated' and 'swimlane' and sets
 * the respective color on the shape.
 */
/**
 * 函数: resolveColor
 *
 * 解析特殊关键字 'inherit'、'indicated' 和 'swimlane'，并设置形状上的相应颜色。
 *
 * 参数：
 *
 * state - 单元格状态 <mxCellState>。
 * field - 形状的颜色字段（如 indicatorColor、fill 等）。
 * key - 样式键（如 STYLE_FILLCOLOR、STYLE_STROKECOLOR 等）。
 *
 * 功能说明：处理形状颜色字段中的关键字，设置正确的颜色值。
 * 关键步骤：
 * 1. 确定目标形状（文本或图形）。
 * 2. 根据关键字（inherit、swimlane、indicated）解析颜色值。
 * 3. 若需要，引用父节点或泳道的颜色。
 * 特殊处理：为 swimlane 设置默认颜色（黑色描边/字体，白色填充），并处理引用逻辑。
 */
mxCellRenderer.prototype.resolveColor = function(state, field, key)
{
	var shape = (key == mxConstants.STYLE_FONTCOLOR) ?
		state.text : state.shape;
    // 中文注释：根据样式键确定目标形状（字体颜色使用文本形状，其他使用图形形状）。

	if (shape != null)
	{
		var graph = state.view.graph;
		var value = shape[field];
		var referenced = null;
		
		if (value == 'inherit')
		{
			referenced = graph.model.getParent(state.cell);
            // 中文注释：如果值为 inherit，则引用父节点的颜色。
		}
		else if (value == 'swimlane')
		{
			shape[field] = (key == mxConstants.STYLE_STROKECOLOR ||
				key == mxConstants.STYLE_FONTCOLOR) ?
				'#000000' : '#ffffff';
            // 中文注释：为 swimlane 设置默认颜色（描边/字体为黑色，填充为白色）。

			if (graph.model.getTerminal(state.cell, false) != null)
			{
				referenced = graph.model.getTerminal(state.cell, false);
                // 中文注释：如果存在终止节点，引用其颜色。
			}
			else
			{
				referenced = state.cell;
                // 中文注释：否则引用当前单元格。
			}
			
			referenced = graph.getSwimlane(referenced);
            // 中文注释：获取泳道节点。
			key = graph.swimlaneIndicatorColorAttribute;
            // 中文注释：更新样式键为泳道的指示器颜色属性。
		}
		else if (value == 'indicated' && state.shape != null)
		{
			shape[field] = state.shape.indicatorColor;
            // 中文注释：如果值为 indicated，则使用形状的指示器颜色。
		}
		else if (key != mxConstants.STYLE_FILLCOLOR &&
			value == mxConstants.STYLE_FILLCOLOR &&
			state.shape != null)
		{
			shape[field] = state.style[mxConstants.STYLE_FILLCOLOR];
            // 中文注释：如果值为填充色且键不同，则使用样式的填充色。
		}
		else if (key != mxConstants.STYLE_STROKECOLOR &&
			value == mxConstants.STYLE_STROKECOLOR &&
			state.shape != null)
		{
			shape[field] = state.style[mxConstants.STYLE_STROKECOLOR];
            // 中文注释：如果值为描边色且键不同，则使用样式的描边色。
		}
	
		if (referenced != null)
		{
			var rstate = graph.getView().getState(referenced);
			shape[field] = null;
            // 中文注释：获取引用单元格的状态并重置当前颜色字段。

			if (rstate != null)
			{
				var rshape = (key == mxConstants.STYLE_FONTCOLOR) ? rstate.text : rstate.shape;
                // 中文注释：根据样式键选择引用形状（字体颜色使用文本形状，其他使用图形形状）。

				if (rshape != null && field != 'indicatorColor')
				{
					shape[field] = rshape[field];
                    // 中文注释：如果引用形状存在且字段不是指示器颜色，则使用引用形状的颜色。
				}
				else
				{
					shape[field] = rstate.style[key];
                    // 中文注释：否则使用引用状态的样式值。
				}
			}
		}
	}
};

/**
 * Function: getLabelValue
 * 
 * Returns the value to be used for the label.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the label should be created.
 */
/**
 * 函数: getLabelValue
 *
 * 返回用于标签的值。
 *
 * 参数：
 *
 * state - 要创建标签的 <mxCellState>。
 *
 * 功能说明：从图形视图中获取单元格的标签值。
 * 返回值：单元格的标签值。
 */
mxCellRenderer.prototype.getLabelValue = function(state)
{
	return state.view.graph.getLabel(state.cell);
    // 中文注释：从图形视图中获取指定单元格的标签值。
};

/**
 * Function: createLabel
 * 
 * Creates the label for the given cell state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the label should be created.
 */
/**
 * 函数: createLabel
 *
 * 为给定的单元格状态创建标签。
 *
 * 参数：
 *
 * state - 要创建标签的 <mxCellState>。
 *
 * 功能说明：根据单元格状态的样式和值创建标签对象，并为其绑定事件监听器。
 * 关键步骤：
 * 1. 检查是否需要创建标签（字体大小有效或值存在）。
 * 2. 创建默认文本形状（mxText），配置样式（如对齐、字体、间距等）。
 * 3. 初始化标签并绑定鼠标事件（点击、移动、释放、双击）。
 * 特殊处理：
 * - 为触摸设备处理鼠标手势事件，确保事件路由正确。
 * - 支持 HTML 标签和非 SVG 环境的特殊处理。
 * 交互逻辑：
 * - 监听鼠标事件以触发图形视图的鼠标事件（MOUSE_DOWN、MOUSE_MOVE、MOUSE_UP）。
 * - 支持双击事件，触发图形的双击处理。
 */
mxCellRenderer.prototype.createLabel = function(state, value)
{
	var graph = state.view.graph;
	var isEdge = graph.getModel().isEdge(state.cell);
    // 中文注释：获取图形视图并判断单元格是否为边。

	if (state.style[mxConstants.STYLE_FONTSIZE] > 0 || state.style[mxConstants.STYLE_FONTSIZE] == null)
	{
		// Avoids using DOM node for empty labels
		var isForceHtml = (graph.isHtmlLabel(state.cell) || (value != null && mxUtils.isNode(value)));
        // 中文注释：检查是否需要强制使用 HTML 标签（基于单元格标签类型或值是否为 DOM 节点）。

		state.text = new this.defaultTextShape(value, new mxRectangle(),
				(state.style[mxConstants.STYLE_ALIGN] || mxConstants.ALIGN_CENTER),
				graph.getVerticalAlign(state),
				state.style[mxConstants.STYLE_FONTCOLOR],
				state.style[mxConstants.STYLE_FONTFAMILY],
				state.style[mxConstants.STYLE_FONTSIZE],
				state.style[mxConstants.STYLE_FONTSTYLE],
				state.style[mxConstants.STYLE_SPACING],
				state.style[mxConstants.STYLE_SPACING_TOP],
				state.style[mxConstants.STYLE_SPACING_RIGHT],
				state.style[mxConstants.STYLE_SPACING_BOTTOM],
				state.style[mxConstants.STYLE_SPACING_LEFT],
				state.style[mxConstants.STYLE_HORIZONTAL],
				state.style[mxConstants.STYLE_LABEL_BACKGROUNDCOLOR],
				state.style[mxConstants.STYLE_LABEL_BORDERCOLOR],
				graph.isWrapping(state.cell) && graph.isHtmlLabel(state.cell),
				graph.isLabelClipped(state.cell),
				state.style[mxConstants.STYLE_OVERFLOW],
				state.style[mxConstants.STYLE_LABEL_PADDING],
				mxUtils.getValue(state.style, mxConstants.STYLE_TEXT_DIRECTION, mxConstants.DEFAULT_TEXT_DIRECTION));
        // 中文注释：创建默认文本形状，配置标签的样式属性（对齐、颜色、字体、间距等）。
        // 样式设置说明：包括字体大小、颜色、家族、样式、间距、背景色、边框色、换行、裁剪、溢出、填充等。
		state.text.opacity = mxUtils.getValue(state.style, mxConstants.STYLE_TEXT_OPACITY, 100);
        // 中文注释：设置标签的文本透明度，默认为 100。
		state.text.dialect = (isForceHtml) ? mxConstants.DIALECT_STRICTHTML : state.view.graph.dialect;
        // 中文注释：设置文本的渲染方言，强制 HTML 标签时使用严格 HTML 模式。
		state.text.style = state.style;
        // 中文注释：将单元格状态的样式应用到文本对象。
		state.text.state = state;
        // 中文注释：将单元格状态关联到文本对象。
		this.initializeLabel(state, state.text);
        // 中文注释：初始化标签，设置合适的容器。

		// Workaround for touch devices routing all events for a mouse gesture
		// (down, move, up) via the initial DOM node. IE additionally redirects
		// the event via the initial DOM node but the event source is the node
		// under the mouse, so we need to check if this is the case and force
		// getCellAt for the subsequent mouseMoves and the final mouseUp.
		var forceGetCell = false;
        // 中文注释：为触摸设备处理鼠标手势事件（按下、移动、释放），确保事件正确路由。
        // 特殊处理：在 IE 和触摸设备中，事件可能通过初始 DOM 节点重定向，需强制使用 getCellAt 获取鼠标下的单元格。

		var getState = function(evt)
		{
			var result = state;

			if (mxClient.IS_TOUCH || forceGetCell)
			{
				var x = mxEvent.getClientX(evt);
				var y = mxEvent.getClientY(evt);
                // 中文注释：获取鼠标事件的客户端坐标。

				// Dispatches the drop event to the graph which
				// consumes and executes the source function
				var pt = mxUtils.convertPoint(graph.container, x, y);
                // 中文注释：将客户端坐标转换为图形容器中的坐标。
				result = graph.view.getState(graph.getCellAt(pt.x, pt.y));
                // 中文注释：根据坐标获取鼠标下的单元格状态。
			}
			
			return result;
            // 中文注释：返回鼠标下的单元格状态。
		};
		
		// TODO: Add handling for special touch device gestures
		mxEvent.addGestureListeners(state.text.node,
			mxUtils.bind(this, function(evt)
			{
				if (this.isLabelEvent(state, evt))
				{
					graph.fireMouseEvent(mxEvent.MOUSE_DOWN, new mxMouseEvent(evt, state));
					forceGetCell = graph.dialect != mxConstants.DIALECT_SVG &&
						mxEvent.getSource(evt).nodeName == 'IMG';
                    // 中文注释：触发鼠标按下事件，并检查是否需要强制获取单元格（非 SVG 环境且事件源为图片）。
				}
			}),
			mxUtils.bind(this, function(evt)
			{
				if (this.isLabelEvent(state, evt))
				{
					graph.fireMouseEvent(mxEvent.MOUSE_MOVE, new mxMouseEvent(evt, getState(evt)));
                    // 中文注释：触发鼠标移动事件，使用 getState 获取当前鼠标下的状态。
				}
			}),
			mxUtils.bind(this, function(evt)
			{
				if (this.isLabelEvent(state, evt))
				{
					graph.fireMouseEvent(mxEvent.MOUSE_UP, new mxMouseEvent(evt, getState(evt)));
					forceGetCell = false;
                    // 中文注释：触发鼠标释放事件，并重置强制获取单元格标志。
				}
			})
		);
        // 中文注释：为文本节点添加手势监听器，处理鼠标按下、移动和释放事件。
        // 事件处理逻辑：将鼠标事件分发到图形视图，确保交互行为（如拖动、选择）正确触发。

		// Uses double click timeout in mxGraph for quirks mode
		if (graph.nativeDblClickEnabled)
		{
			mxEvent.addListener(state.text.node, 'dblclick',
				mxUtils.bind(this, function(evt)
				{
					if (this.isLabelEvent(state, evt))
					{
						graph.dblClick(evt, state.cell);
						mxEvent.consume(evt);
                        // 中文注释：触发双击事件，调用图形的双击处理函数并阻止事件冒泡。
					}
				})
			);
            // 中文注释：如果启用了原生双击，为文本节点添加双击监听器。
            // 事件处理逻辑：处理双击事件，触发图形的双击行为（如编辑单元格）。
		}
	}
};

/**
 * Function: initializeLabel
 * 
 * Initiailzes the label with a suitable container.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose label should be initialized.
 */
/**
 * 函数: initializeLabel
 *
 * 使用合适的容器初始化标签。
 *
 * 参数：
 *
 * state - 要初始化标签的 <mxCellState>。
 * shape - 要初始化的标签形状（<mxText>）。
 *
 * 功能说明：将标签初始化并添加到正确的 DOM 容器中。
 * 关键步骤：
 * 1. 检查是否为 SVG 环境且不支持外来对象（NO_FO），决定使用图形容器还是绘制面板。
 * 2. 调用形状的 init 方法，设置容器。
 * 特殊处理：处理 SVG 和非 SVG 环境的标签初始化差异。
 */
mxCellRenderer.prototype.initializeLabel = function(state, shape)
{
	if (mxClient.IS_SVG && mxClient.NO_FO && shape.dialect != mxConstants.DIALECT_SVG)
	{
		shape.init(state.view.graph.container);
        // 中文注释：在 SVG 环境且不支持外来对象时，将标签添加到图形容器。
	}
	else
	{
		shape.init(state.view.getDrawPane());
        // 中文注释：否则将标签添加到图形视图的绘制面板。
	}
};

/**
 * Function: createCellOverlays
 * 
 * Creates the actual shape for showing the overlay for the given cell state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the overlay should be created.
 */
/**
 * 函数: createCellOverlays
 *
 * 为给定的单元格状态创建覆盖层的实际形状。
 *
 * 参数：
 *
 * state - 要创建覆盖层的 <mxCellState>。
 *
 * 功能说明：为单元格状态创建覆盖层（如图标或标记），并管理现有覆盖层的生命周期。
 * 关键步骤：
 * 1. 获取单元格的覆盖层列表。
 * 2. 为每个覆盖层创建图像形状（mxImageShape），并初始化和绑定事件。
 * 3. 移除未使用的覆盖层。
 * 特殊处理：确保覆盖层的 DOM 节点和光标样式正确设置。
 * 交互逻辑：为覆盖层添加点击和手势事件，触发覆盖层的点击事件。
 */
mxCellRenderer.prototype.createCellOverlays = function(state)
{
	var graph = state.view.graph;
	var overlays = graph.getCellOverlays(state.cell);
	var dict = null;
    // 中文注释：获取图形视图和单元格的覆盖层列表。

	if (overlays != null)
	{
		dict = new mxDictionary();
        // 中文注释：创建字典用于存储覆盖层。

		for (var i = 0; i < overlays.length; i++)
		{
			var shape = (state.overlays != null) ? state.overlays.remove(overlays[i]) : null;
            // 中文注释：从现有覆盖层中移除当前覆盖层（若存在）。

			if (shape == null)
			{
				var tmp = new mxImageShape(new mxRectangle(), overlays[i].image.src);
                // 中文注释：为新覆盖层创建图像形状。
				tmp.dialect = state.view.graph.dialect;
                // 中文注释：设置图像形状的渲染方言。
				tmp.preserveImageAspect = false;
                // 中文注释：禁用图像宽高比保持。
				tmp.overlay = overlays[i];
                // 中文注释：将覆盖层对象关联到形状。
				this.initializeOverlay(state, tmp);
                // 中文注释：初始化覆盖层。
				this.installCellOverlayListeners(state, overlays[i], tmp);
                // 中文注释：为覆盖层安装事件监听器。

				if (overlays[i].cursor != null)
				{
					tmp.node.style.cursor = overlays[i].cursor;
                    // 中文注释：设置覆盖层的光标样式。
				}
				
				dict.put(overlays[i], tmp);
                // 中文注释：将覆盖层形状存储到字典中。
			}
			else
			{
				dict.put(overlays[i], shape);
                // 中文注释：将现有覆盖层形状存储到字典中。
			}
		}
	}
	
	// Removes unused
	if (state.overlays != null)
	{
		state.overlays.visit(function(id, shape)
		{
			shape.destroy();
            // 中文注释：销毁未使用的覆盖层形状。
		});
	}
	
	state.overlays = dict;
    // 中文注释：更新状态的覆盖层字典。
};

/**
 * Function: initializeOverlay
 * 
 * Initializes the given overlay.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the overlay should be created.
 * overlay - <mxImageShape> that represents the overlay.
 */
/**
 * 函数: initializeOverlay
 *
 * 初始化给定的覆盖层。
 *
 * 参数：
 *
 * state - 要创建覆盖层的 <mxCellState>。
 * overlay - 表示覆盖层的 <mxImageShape>。
 *
 * 功能说明：将覆盖层初始化并添加到覆盖层面板。
 * 关键步骤：调用覆盖层的 init 方法，设置覆盖层面板作为容器。
 */
mxCellRenderer.prototype.initializeOverlay = function(state, overlay)
{
	overlay.init(state.view.getOverlayPane());
    // 中文注释：将覆盖层初始化并添加到图形视图的覆盖层面板。
};

/**
 * Function: installCellOverlayListeners
 * 
 * Installs the listeners for the given <mxCellState>, <mxCellOverlay> and
 * <mxShape> that represents the overlay.
 */
/**
 * 函数: installCellOverlayListeners
 *
 * 为给定的 <mxCellState>、<mxCellOverlay> 和表示覆盖层的 <mxShape> 安装事件监听器。
 *
 * 参数：
 *
 * state - 单元格状态 <mxCellState>。
 * overlay - 覆盖层对象 <mxCellOverlay>。
 * shape - 表示覆盖层的形状 <mxShape>。
 *
 * 功能说明：为覆盖层添加点击和手势事件监听器，以支持用户交互。
 * 关键步骤：
 * 1. 添加点击事件，触发覆盖层的点击事件。
 * 2. 添加手势事件（按下、移动），阻止默认行为并触发鼠标移动事件。
 * 3. 为触摸设备添加 touchend 事件，触发点击事件。
 * 交互逻辑：确保覆盖层支持点击和鼠标移动交互，触发图形视图的事件。
 * 特殊处理：当图形处于编辑状态时，停止编辑并触发点击事件。
 */
mxCellRenderer.prototype.installCellOverlayListeners = function(state, overlay, shape)
{
	var graph  = state.view.graph;
	
	mxEvent.addListener(shape.node, 'click', function (evt)
	{
		if (graph.isEditing())
		{
			graph.stopEditing(!graph.isInvokesStopCellEditing());
            // 中文注释：如果图形处于编辑状态，停止编辑（根据配置决定是否触发停止编辑事件）。
		}
		
		overlay.fireEvent(new mxEventObject(mxEvent.CLICK,
				'event', evt, 'cell', state.cell));
        // 中文注释：触发覆盖层的点击事件，传递事件对象和单元格。
	});
	
	mxEvent.addGestureListeners(shape.node,
		function (evt)
		{
			mxEvent.consume(evt);
            // 中文注释：阻止手势事件的默认行为。
		},
		function (evt)
		{
			graph.fireMouseEvent(mxEvent.MOUSE_MOVE,
				new mxMouseEvent(evt, state));
            // 中文注释：触发鼠标移动事件，传递当前状态。
		});
	
	if (mxClient.IS_TOUCH)
	{
		mxEvent.addListener(shape.node, 'touchend', function (evt)
		{
			overlay.fireEvent(new mxEventObject(mxEvent.CLICK,
					'event', evt, 'cell', state.cell));
            // 中文注释：在触摸设备上，触发 touchend 事件作为点击事件。
		});
	}
};

/**
 * Function: createControl
 * 
 * Creates the control for the given cell state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the control should be created.
 */
/**
 * 函数: createControl
 *
 * 为给定的单元格状态创建控件。
 *
 * 参数：
 *
 * state - 要创建控件的 <mxCellState>。
 *
 * 功能说明：为单元格状态创建折叠控件（如折叠/展开图标）。
 * 关键步骤：
 * 1. 检查是否启用折叠并获取折叠图标。
 * 2. 如果不存在控件且有图标，创建新的图像形状（mxImageShape）作为控件。
 * 3. 初始化控件并绑定点击事件。
 * 4. 如果控件存在但无需折叠，销毁控件。
 * 特殊处理：仅在启用折叠且存在图标时创建控件。
 */
mxCellRenderer.prototype.createControl = function(state)
{
	var graph = state.view.graph;
	var image = graph.getFoldingImage(state);
    // 中文注释：获取图形视图和折叠图标。

	if (graph.foldingEnabled && image != null)
	{
		if (state.control == null)
		{
			var b = new mxRectangle(0, 0, image.width, image.height);
            // 中文注释：创建控件边界矩形，基于图标尺寸。
			state.control = new mxImageShape(b, image.src);
            // 中文注释：创建新的图像形状作为折叠控件。
			state.control.preserveImageAspect = false;
            // 中文注释：禁用控件图像的宽高比保持。
			state.control.dialect = graph.dialect;
            // 中文注释：设置控件的渲染方言。

			this.initControl(state, state.control, true, this.createControlClickHandler(state));
            // 中文注释：初始化控件，绑定点击处理程序。
		}
	}
	else if (state.control != null)
	{
		state.control.destroy();
		state.control = null;
        // 中文注释：如果控件存在但无需折叠，销毁控件。
	}
};

/**
 * Function: createControlClickHandler
 * 
 * Hook for creating the click handler for the folding icon.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose control click handler should be returned.
 */
/**
 * 函数: createControlClickHandler
 *
 * 为折叠图标创建点击处理程序的钩子函数。
 *
 * 参数：
 *
 * state - 需要返回点击处理程序的 <mxCellState>。
 *
 * 功能说明：创建并返回折叠控件的点击处理程序。
 * 交互逻辑：
 * - 检查是否强制处理点击或图形启用。
 * - 切换单元格的折叠状态并触发折叠事件。
 * - 阻止事件冒泡。
 * 返回值：点击处理程序函数。
 */
mxCellRenderer.prototype.createControlClickHandler = function(state)
{
	var graph = state.view.graph;
	
	return mxUtils.bind(this, function (evt)
	{
		if (this.forceControlClickHandler || graph.isEnabled())
		{
			var collapse = !graph.isCellCollapsed(state.cell);
            // 中文注释：确定是否折叠单元格（反转当前折叠状态）。
			graph.foldCells(collapse, false, [state.cell], null, evt);
            // 中文注释：调用图形视图的折叠方法，更新单元格折叠状态。
			mxEvent.consume(evt);
            // 中文注释：阻止事件冒泡。
		}
	});
};

/**
 * Function: initControl
 * 
 * Initializes the given control and returns the corresponding DOM node.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the control should be initialized.
 * control - <mxShape> to be initialized.
 * handleEvents - Boolean indicating if mousedown and mousemove should fire events via the graph.
 * clickHandler - Optional function to implement clicks on the control.
 */
/**
 * 函数: initControl
 *
 * 初始化给定的控件并返回对应的 DOM 节点。
 *
 * 参数：
 *
 * state - 要初始化控件的 <mxCellState>。
 * control - 要初始化的控件形状 <mxShape>。
 * handleEvents - 布尔值，指示是否通过图形触发鼠标按下和移动事件。
 * clickHandler - 可选的点击处理函数。
 *
 * 功能说明：初始化折叠控件，设置容器并绑定事件。
 * 关键步骤：
 * 1. 根据环境（HTML 标签和 SVG）选择合适的容器（图形容器或覆盖层面板）。
 * 2. 设置控件的光标样式（如果图形启用）。
 * 3. 绑定点击和手势事件。
 * 特殊处理：
 * - 在 HTML 标签和 SVG 环境下，确保控件可点击。
 * - 为 iOS 设备添加触摸事件处理，检查点击容差。
 * 返回值：控件的 DOM 节点。
 */
mxCellRenderer.prototype.initControl = function(state, control, handleEvents, clickHandler)
{
	var graph = state.view.graph;
	
	// In the special case where the label is in HTML and the display is SVG the image
	// should go into the graph container directly in order to be clickable. Otherwise
	// it is obscured by the HTML label that overlaps the cell.
	var isForceHtml = graph.isHtmlLabel(state.cell) && mxClient.NO_FO &&
		graph.dialect == mxConstants.DIALECT_SVG;
    // 中文注释：检查是否为 HTML 标签且 SVG 环境，确保控件可点击。

	if (isForceHtml)
	{
		control.dialect = mxConstants.DIALECT_PREFERHTML;
        // 中文注释：设置控件的渲染方言为优先 HTML。
		control.init(graph.container);
        // 中文注释：将控件添加到图形容器。
		control.node.style.zIndex = 1;
        // 中文注释：设置控件节点的 z-index 为 1，确保显示在顶层。
	}
	else
	{
		control.init(state.view.getOverlayPane());
        // 中文注释：否则将控件添加到覆盖层面板。
	}

	var node = control.innerNode || control.node;
    // 中文注释：获取控件的 DOM 节点（内部节点或主节点）。

	// Workaround for missing click event on iOS is to check tolerance below
	if (clickHandler != null && !mxClient.IS_IOS)
	{
		if (graph.isEnabled())
		{
			node.style.cursor = 'pointer';
            // 中文注释：如果图形启用，设置控件节点的光标为指针。
		}
		
		mxEvent.addListener(node, 'click', clickHandler);
        // 中文注释：为控件节点添加点击事件监听器。
	}
	
	if (handleEvents)
	{
		var first = null;

		mxEvent.addGestureListeners(node,
			function (evt)
			{
				first = new mxPoint(mxEvent.getClientX(evt), mxEvent.getClientY(evt));
                // 中文注释：记录鼠标按下时的坐标。
				graph.fireMouseEvent(mxEvent.MOUSE_DOWN, new mxMouseEvent(evt, state));
                // 中文注释：触发鼠标按下事件。
				mxEvent.consume(evt);
                // 中文注释：阻止事件冒泡。
			},
			function (evt)
			{
				graph.fireMouseEvent(mxEvent.MOUSE_MOVE, new mxMouseEvent(evt, state));
                // 中文注释：触发鼠标移动事件。
			},
			function (evt)
			{
				graph.fireMouseEvent(mxEvent.MOUSE_UP, new mxMouseEvent(evt, state));
                // 中文注释：触发鼠标释放事件。
				mxEvent.consume(evt);
                // 中文注释：阻止事件冒泡。
			});
		
		// Uses capture phase for event interception to stop bubble phase
		if (clickHandler != null && mxClient.IS_IOS)
		{
			node.addEventListener('touchend', function(evt)
			{
				if (first != null)
				{
					var tol = graph.tolerance;
                    // 中文注释：获取图形的点击容差。

					if (Math.abs(first.x - mxEvent.getClientX(evt)) < tol &&
						Math.abs(first.y - mxEvent.getClientY(evt)) < tol)
					{
						clickHandler.call(clickHandler, evt);
                        // 中文注释：如果点击位置在容差范围内，触发点击处理程序。
						mxEvent.consume(evt);
                        // 中文注释：阻止事件冒泡。
					}
				}
			}, true);
            // 中文注释：在 iOS 上为控件添加 touchend 事件，检查点击容差以模拟点击。
		}
	}
	
	return node;
    // 中文注释：返回控件的 DOM 节点。
};

/**
 * Function: isShapeEvent
 * 
 * Returns true if the event is for the shape of the given state. This
 * implementation always returns true.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose shape fired the event.
 * evt - Mouse event which was fired.
 */
/**
 * 函数: isShapeEvent
 *
 * 判断事件是否针对给定状态的形状。本实现始终返回 true。
 *
 * 参数：
 *
 * state - 触发事件的形状的 <mxCellState>。
 * evt - 触发的鼠标事件。
 *
 * 功能说明：检查事件是否与形状相关。
 * 返回值：始终返回 true（可由子类覆盖以实现特定逻辑）。
 */
mxCellRenderer.prototype.isShapeEvent = function(state, evt)
{
	return true;
    // 中文注释：默认实现，始终返回 true，表示事件与形状相关。
};

/**
 * Function: isLabelEvent
 * 
 * Returns true if the event is for the label of the given state. This
 * implementation always returns true.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose label fired the event.
 * evt - Mouse event which was fired.
 */
/**
 * 函数: isLabelEvent
 *
 * 判断事件是否针对给定状态的标签。本实现始终返回 true。
 *
 * 参数：
 *
 * state - 触发事件的标签的 <mxCellState>。
 * evt - 触发的鼠标事件。
 *
 * 功能说明：检查事件是否与标签相关。
 * 返回值：始终返回 true（可由子类覆盖以实现特定逻辑）。
 */
mxCellRenderer.prototype.isLabelEvent = function(state, evt)
{
	return true;
    // 中文注释：默认实现，始终返回 true，表示事件与标签相关。
};

/**
 * Function: installListeners
 * 
 * Installs the event listeners for the given cell state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the event listeners should be isntalled.
 */
/**
 * 函数: installListeners
 *
 * 为给定的单元格状态安装事件监听器。
 *
 * 参数：
 *
 * state - 要安装事件监听器的 <mxCellState>。
 *
 * 功能说明：为形状节点添加鼠标事件监听器，支持交互功能。
 * 关键步骤：
 * 1. 定义 getState 函数，处理触摸设备和非 SVG 环境的事件路由。
 * 2. 添加手势监听器（按下、移动、释放），触发图形视图的鼠标事件。
 * 3. 如果启用原生双击，添加双击监听器。
 * 交互逻辑：确保形状支持鼠标交互（点击、拖动、释放、双击）。
 * 特殊处理：为触摸设备和 IE 的图片节点处理事件路由，确保正确获取鼠标下的单元格。
 */
mxCellRenderer.prototype.installListeners = function(state)
{
	var graph = state.view.graph;

	// Workaround for touch devices routing all events for a mouse
	// gesture (down, move, up) via the initial DOM node. Same for
	// HTML images in all IE versions (VML images are working).
	var getState = function(evt)
	{
		var result = state;
		
		if ((graph.dialect != mxConstants.DIALECT_SVG && mxEvent.getSource(evt).nodeName == 'IMG') || mxClient.IS_TOUCH)
		{
			var x = mxEvent.getClientX(evt);
			var y = mxEvent.getClientY(evt);
            // 中文注释：获取鼠标事件的客户端坐标。

			// Dispatches the drop event to the graph which
			// consumes and executes the source function
			var pt = mxUtils.convertPoint(graph.container, x, y);
            // 中文注释：将客户端坐标转换为图形容器中的坐标。
			result = graph.view.getState(graph.getCellAt(pt.x, pt.y));
            // 中文注释：根据坐标获取鼠标下的单元格状态。
		}
		
		return result;
        // 中文注释：返回鼠标下的单元格状态。
	};

	mxEvent.addGestureListeners(state.shape.node,
		mxUtils.bind(this, function(evt)
		{
			if (this.isShapeEvent(state, evt))
			{
				graph.fireMouseEvent(mxEvent.MOUSE_DOWN, new mxMouseEvent(evt, state));
                // 中文注释：触发鼠标按下事件。
			}
		}),
		mxUtils.bind(this, function(evt)
		{
			if (this.isShapeEvent(state, evt))
			{
				graph.fireMouseEvent(mxEvent.MOUSE_MOVE, new mxMouseEvent(evt, getState(evt)));
                // 中文注释：触发鼠标移动事件，使用 getState 获取当前状态。
			}
		}),
		mxUtils.bind(this, function(evt)
		{
			if (this.isShapeEvent(state, evt))
			{
				graph.fireMouseEvent(mxEvent.MOUSE_UP, new mxMouseEvent(evt, getState(evt)));
                // 中文注释：触发鼠标释放事件。
			}
		})
	);
    // 中文注释：为形状节点添加手势监听器，处理鼠标按下、移动和释放事件。

	// Uses double click timeout in mxGraph for quirks mode
	if (graph.nativeDblClickEnabled)
	{
		mxEvent.addListener(state.shape.node, 'dblclick',
			mxUtils.bind(this, function(evt)
			{
				if (this.isShapeEvent(state, evt))
				{
					graph.dblClick(evt, state.cell);
                    // 中文注释：触发双击事件，调用图形的双击处理函数。
					mxEvent.consume(evt);
                    // 中文注释：阻止事件冒泡。
				}
			})
		);
        // 中文注释：如果启用了原生双击，为形状节点添加双击监听器。
	}
};

/**
 * Function: redrawLabel
 * 
 * Redraws the label for the given cell state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose label should be redrawn.
 */
/**
 * 函数: redrawLabel
 *
 * 重绘给定单元格状态的标签。
 *
 * 参数：
 *
 * state - 要重绘标签的 <mxCellState>。
 * forced - 布尔值，指示是否强制重绘。
 *
 * 功能说明：根据单元格状态更新或重新创建标签。
 * 关键步骤：
 * 1. 获取标签值并检查换行、裁剪和溢出设置。
 * 2. 如果标签配置变化或不存在，销毁旧标签并创建新标签。
 * 3. 更新标签的边界、缩放、样式等，并重绘标签。
 * 特殊处理：
 * - 处理 HTML 标签和非 SVG 环境的特殊逻辑。
 * - 如果样式变化，强制重置标签样式。
 * 样式设置说明：包括字体、颜色、对齐、间距、透明度等。
 */
mxCellRenderer.prototype.redrawLabel = function(state, forced)
{
	var graph = state.view.graph;
	var value = this.getLabelValue(state);
    // 中文注释：获取标签的值。
	var wrapping = graph.isWrapping(state.cell);
    // 中文注释：检查是否启用标签换行。
	var clipping = graph.isLabelClipped(state.cell);
    // 中文注释：检查是否启用标签裁剪。
	var isForceHtml = (state.view.graph.isHtmlLabel(state.cell) || (value != null && mxUtils.isNode(value)));
    // 中文注释：检查是否强制使用 HTML 标签。
	var dialect = (isForceHtml) ? mxConstants.DIALECT_STRICTHTML : state.view.graph.dialect;
    // 中文注释：确定标签的渲染方言。
	var overflow = state.style[mxConstants.STYLE_OVERFLOW] || 'visible';
    // 中文注释：获取标签的溢出样式，默认为 visible。

	if (state.text != null && (state.text.wrap != wrapping || state.text.clipped != clipping ||
		state.text.overflow != overflow || state.text.dialect != dialect))
	{
		state.text.destroy();
		state.text = null;
        // 中文注释：如果标签的换行、裁剪、溢出或方言变化，销毁旧标签。
	}
	
	if (state.text == null && value != null && (mxUtils.isNode(value) || value.length > 0))
	{
		this.createLabel(state, value);
        // 中文注释：如果标签不存在且值有效，创建新标签。
	}
	else if (state.text != null && (value == null || value.length == 0))
	{
		state.text.destroy();
		state.text = null;
        // 中文注释：如果标签存在但值为空，销毁标签。
	}

	if (state.text != null)
	{
		// Forced is true if the style has changed, so to get the updated
		// result in getLabelBounds we apply the new style to the shape
		if (forced)
		{
			// Checks if a full repaint is needed
			if (state.text.lastValue != null && this.isTextShapeInvalid(state, state.text))
			{
				// Forces a full repaint
				state.text.lastValue = null;
                // 中文注释：如果文本形状无效，重置上次值以强制完全重绘。
			}
			
			state.text.resetStyles();
            // 中文注释：重置文本形状的样式。
			state.text.apply(state);
            // 中文注释：应用单元格状态的样式到文本形状。

			// Special case where value is obtained via hook in graph
			state.text.valign = graph.getVerticalAlign(state);
            // 中文注释：设置文本的垂直对齐方式，从图形视图获取。
		}
		
		var bounds = this.getLabelBounds(state);
        // 中文注释：获取标签的边界。
		var nextScale = this.getTextScale(state);
        // 中文注释：获取标签的缩放比例。
		this.resolveColor(state, 'color', mxConstants.STYLE_FONTCOLOR);
        // 中文注释：解析文本的字体颜色。

		if (forced || state.text.value != value || state.text.isWrapping != wrapping ||
			state.text.overflow != overflow || state.text.isClipping != clipping ||
			state.text.scale != nextScale || state.text.dialect != dialect ||
			state.text.bounds == null || !state.text.bounds.equals(bounds))
		{
			state.text.dialect = dialect;
            // 中文注释：更新文本的渲染方言。
			state.text.value = value;
            // 中文注释：更新文本的值。
			state.text.bounds = bounds;
            // 中文注释：更新文本的边界。
			state.text.scale = nextScale;
            // 中文注释：更新文本的缩放比例。
			state.text.wrap = wrapping;
            // 中文注释：更新文本的换行设置。
			state.text.clipped = clipping;
            // 中文注释：更新文本的裁剪设置。
			state.text.overflow = overflow;
            // 中文注释：更新文本的溢出设置。

			// Preserves visible state
			var vis = state.text.node.style.visibility;
            // 中文注释：保存文本节点的可见性状态。
			this.redrawLabelShape(state.text);
            // 中文注释：重绘文本形状。
			state.text.node.style.visibility = vis;
            // 中文注释：恢复文本节点的可见性状态。
		}
	}
};

/**
 * Function: isTextShapeInvalid
 * 
 * Returns true if the style for the text shape has changed.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose label should be checked.
 * shape - <mxText> shape to be checked.
 */
/**
 * 函数: isTextShapeInvalid
 *
 * 判断文本形状的样式是否发生变化。
 *
 * 参数：
 *
 * state - 要检查的 <mxCellState>。
 * shape - 要检查的文本形状 <mxText>。
 *
 * 功能说明：比较文本形状的当前样式与状态样式，判断是否需要重绘。
 * 关键步骤：检查字体样式、家族、大小、颜色、对齐、间距等是否变化。
 * 特殊处理：对间距属性考虑方向性间距的额外计算。
 * 返回值：如果样式变化返回 true，否则返回 false。
 */
mxCellRenderer.prototype.isTextShapeInvalid = function(state, shape)
{
	function check(property, stylename, defaultValue)
	{
		var result = false;
		
		// Workaround for spacing added to directional spacing
		if (stylename == 'spacingTop' || stylename == 'spacingRight' ||
			stylename == 'spacingBottom' || stylename == 'spacingLeft')
		{
			result = parseFloat(shape[property]) - parseFloat(shape.spacing) !=
				(state.style[stylename] || defaultValue);
            // 中文注释：检查方向性间距属性，考虑额外间距的影响。
		}
		else
		{
			result = shape[property] != (state.style[stylename] || defaultValue);
            // 中文注释：比较形状属性与状态样式或默认值是否一致。
		}
		
		return result;
	};

	return check('fontStyle', mxConstants.STYLE_FONTSTYLE, mxConstants.DEFAULT_FONTSTYLE) ||
		check('family', mxConstants.STYLE_FONTFAMILY, mxConstants.DEFAULT_FONTFAMILY) ||
		check('size', mxConstants.STYLE_FONTSIZE, mxConstants.DEFAULT_FONTSIZE) ||
		check('color', mxConstants.STYLE_FONTCOLOR, 'black') ||
		check('align', mxConstants.STYLE_ALIGN, '') ||
		check('valign', mxConstants.STYLE_VERTICAL_ALIGN, '') ||
		check('spacing', mxConstants.STYLE_SPACING, 2) ||
		check('spacingTop', mxConstants.STYLE_SPACING_TOP, 0) ||
		check('spacingRight', mxConstants.STYLE_SPACING_RIGHT, 0) ||
		check('spacingBottom', mxConstants.STYLE_SPACING_BOTTOM, 0) ||
		check('spacingLeft', mxConstants.STYLE_SPACING_LEFT, 0) ||
		check('horizontal', mxConstants.STYLE_HORIZONTAL, true) ||
		check('background', mxConstants.STYLE_LABEL_BACKGROUNDCOLOR) ||
		check('border', mxConstants.STYLE_LABEL_BORDERCOLOR) ||
		check('opacity', mxConstants.STYLE_TEXT_OPACITY, 100) ||
		check('textDirection', mxConstants.STYLE_TEXT_DIRECTION, mxConstants.DEFAULT_TEXT_DIRECTION);
    // 中文注释：检查所有相关样式属性，任意属性变化则返回 true。
};

/**
 * Function: redrawLabelShape
 * 
 * Called to invoked redraw on the given text shape.
 * 
 * Parameters:
 * 
 * shape - <mxText> shape to be redrawn.
 */
/**
 * 函数: redrawLabelShape
 *
 * 调用以在给定的文本形状上触发重绘。
 *
 * 参数：
 *
 * shape - 要重绘的文本形状 <mxText>。
 *
 * 功能说明：触发文本形状的重绘操作。
 * 关键步骤：调用形状的 redraw 方法，更新 DOM 中的显示。
 */
mxCellRenderer.prototype.redrawLabelShape = function(shape)
{
	shape.redraw();
    // 中文注释：调用文本形状的 redraw 方法，重绘标签。
};

/**
 * Function: getTextScale
 * 
 * Returns the scaling used for the label of the given state
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose label scale should be returned.
 */
/**
 * 函数: getTextScale
 *
 * 返回给定状态标签使用的缩放比例。
 *
 * 参数：
 *
 * state - 要返回标签缩放比例的 <mxCellState>。
 *
 * 功能说明：获取图形视图的缩放比例，用于标签渲染。
 * 返回值：当前视图的缩放比例。
 */
mxCellRenderer.prototype.getTextScale = function(state)
{
	return state.view.scale;
    // 中文注释：返回图形视图的当前缩放比例。
};

/**
 * Function: getLabelBounds
 * 
 * Returns the bounds to be used to draw the label of the given state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose label bounds should be returned.
 */
/**
 * 函数: getLabelBounds
 *
 * 返回用于绘制给定状态标签的边界。
 *
 * 参数：
 *
 * state - 要返回标签边界的 <mxCellState>。
 *
 * 功能说明：计算标签的绘制边界，考虑缩放、偏移、旋转等。
 * 关键步骤：
 * 1. 根据单元格类型（边或顶点）计算初始边界。
 * 2. 应用间距、偏移和旋转调整。
 * 3. 处理标签宽度样式覆盖。
 * 4. 如果需要，应用形状的标签边界调整。
 * 特殊处理：
 * - 处理边的间距和几何尺寸。
 * - 处理顶点的旋转和边界反转。
 * - 考虑标签宽度样式覆盖实际宽度。
 * 返回值：标签的边界矩形（mxRectangle）。
 */
mxCellRenderer.prototype.getLabelBounds = function(state)
{
	var graph = state.view.graph;
	var scale = state.view.scale;
	var isEdge = graph.getModel().isEdge(state.cell);
    // 中文注释：获取图形视图、缩放比例和单元格类型。
	var bounds = new mxRectangle(state.absoluteOffset.x, state.absoluteOffset.y);
    // 中文注释：创建初始边界，基于绝对偏移。

	if (isEdge)
	{
		var spacing = state.text.getSpacing();
		bounds.x += spacing.x * scale;
		bounds.y += spacing.y * scale;
        // 中文注释：为边标签应用间距调整，考虑缩放。

		var geo = graph.getCellGeometry(state.cell);
        // 中文注释：获取单元格的几何信息。

		if (geo != null)
		{
			bounds.width = Math.max(0, geo.width * scale);
			bounds.height = Math.max(0, geo.height * scale);
            // 中文注释：设置边标签的宽度和高度，基于几何信息和缩放。
		}
	}
	else
	{
		// Inverts label position
		if (state.text.isPaintBoundsInverted())
		{
			var tmp = bounds.x;
			bounds.x = bounds.y;
			bounds.y = tmp;
            // 中文注释：如果需要反转标签位置，交换 x 和 y 坐标。
		}
		
		bounds.x += state.x;
		bounds.y += state.y;
        // 中文注释：将状态的坐标添加到边界。

		// Minimum of 1 fixes alignment bug in HTML labels
		bounds.width = Math.max(1, state.width);
		bounds.height = Math.max(1, state.height);
        // 中文注释：设置最小宽度和高度为 1，修复 HTML 标签对齐问题。
	}

	if (state.text.isPaintBoundsInverted())
	{
		// Rotates around center of state
		var t = (state.width - state.height) / 2;
		bounds.x += t;
		bounds.y -= t;
		var tmp = bounds.width;
		bounds.width = bounds.height;
		bounds.height = tmp;
        // 中文注释：如果标签边界反转，围绕状态中心旋转，调整坐标和尺寸。
	}
	
	// Shape can modify its label bounds
	if (state.shape != null)
	{
		var hpos = mxUtils.getValue(state.style, mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER);
		var vpos = mxUtils.getValue(state.style, mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
        // 中文注释：获取标签的水平和垂直对齐样式。

		if (hpos == mxConstants.ALIGN_CENTER && vpos == mxConstants.ALIGN_MIDDLE)
		{
			bounds = state.shape.getLabelBounds(bounds);
            // 中文注释：如果对齐方式为居中，调用形状的 getLabelBounds 方法调整边界。
		}
	}
	
	// Label width style overrides actual label width
	var lw = mxUtils.getValue(state.style, mxConstants.STYLE_LABEL_WIDTH, null);
    // 中文注释：获取标签宽度样式。

	if (lw != null)
	{
		bounds.width = parseFloat(lw) * scale;
        // 中文注释：如果指定了标签宽度样式，覆盖实际宽度，考虑缩放。
	}
	
	if (!isEdge)
	{
		this.rotateLabelBounds(state, bounds);
        // 中文注释：如果不是边，调用 rotateLabelBounds 应用旋转调整。
	}
	
	return bounds;
    // 中文注释：返回计算后的标签边界。
};

/**
 * Function: rotateLabelBounds
 * 
 * Adds the shape rotation to the given label bounds and
 * applies the alignment and offsets.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose label bounds should be rotated.
 * bounds - <mxRectangle> the rectangle to be rotated.
 */
/**
 * 函数: rotateLabelBounds
 *
 * 将形状旋转添加到给定的标签边界，并应用对齐和偏移。
 *
 * 参数：
 *
 * state - 要旋转标签边界的 <mxCellState>。
 * bounds - 要旋转的矩形 <mxRectangle>。
 *
 * 功能说明：调整标签边界，应用旋转、对齐和间距。
 * 关键步骤：
 * 1. 应用标签的边距调整。
 * 2. 如果非填充或宽度溢出，应用间距和对齐调整。
 * 3. 如果需要，围绕状态中心旋转边界。
 * 特殊处理：处理传统间距逻辑（legacySpacing）和旋转逻辑。
 */
mxCellRenderer.prototype.rotateLabelBounds = function(state, bounds)
{
	bounds.y -= state.text.margin.y * bounds.height;
	bounds.x -= state.text.margin.x * bounds.width;
    // 中文注释：应用标签的边距调整，基于边界尺寸。

	if (!this.legacySpacing || (state.style[mxConstants.STYLE_OVERFLOW] != 'fill' && state.style[mxConstants.STYLE_OVERFLOW] != 'width'))
	{
		var s = state.view.scale;
		var spacing = state.text.getSpacing();
		bounds.x += spacing.x * s;
		bounds.y += spacing.y * s;
        // 中文注释：应用间距调整，考虑缩放比例。

		var hpos = mxUtils.getValue(state.style, mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER);
		var vpos = mxUtils.getValue(state.style, mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
		var lw = mxUtils.getValue(state.style, mxConstants.STYLE_LABEL_WIDTH, null);
        // 中文注释：获取标签的水平、垂直对齐和宽度样式。

		bounds.width = Math.max(0, bounds.width - ((hpos == mxConstants.ALIGN_CENTER && lw == null) ? (state.text.spacingLeft * s + state.text.spacingRight * s) : 0));
		bounds.height = Math.max(0, bounds.height - ((vpos == mxConstants.ALIGN_MIDDLE) ? (state.text.spacingTop * s + state.text.spacingBottom * s) : 0));
        // 中文注释：根据对齐方式调整边界宽度和高度，减去间距。
	}

	var theta = state.text.getTextRotation();
    // 中文注释：获取文本的旋转角度。

	// Only needed if rotated around another center
	if (theta != 0 && state != null && state.view.graph.model.isVertex(state.cell))
	{
		var cx = state.getCenterX();
		var cy = state.getCenterY();
        // 中文注释：获取状态的中心坐标。

		if (bounds.x != cx || bounds.y != cy)
		{
			var rad = theta * (Math.PI / 180);
            // 中文注释：将旋转角度转换为弧度。
			var pt = mxUtils.getRotatedPoint(new mxPoint(bounds.x, bounds.y),
					Math.cos(rad), Math.sin(rad), new mxPoint(cx, cy));
            // 中文注释：围绕中心点旋转边界坐标。

			bounds.x = pt.x;
			bounds.y = pt.y;
            // 中文注释：更新旋转后的边界坐标。
		}
	}
};

/**
 * Function: redrawCellOverlays
 * 
 * Redraws the overlays for the given cell state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose overlays should be redrawn.
 */
/**
 * 函数: redrawCellOverlays
 *
 * 重绘给定单元格状态的覆盖层。
 *
 * 参数：
 *
 * state - 要重绘覆盖层的 <mxCellState>。
 * forced - 可选布尔值，指示是否强制重绘而不进行额外检查。
 *
 * 功能说明：更新或重新绘制单元格状态的覆盖层（如图标或标记），确保其在图形视图中正确显示。
 * 关键步骤：
 * 1. 调用 createCellOverlays 创建或更新覆盖层。
 * 2. 如果存在覆盖层，计算旋转角度并遍历所有覆盖层。
 * 3. 对非边单元格应用旋转调整，更新覆盖层边界。
 * 4. 如果需要（强制重绘或边界/缩放变化），更新覆盖层的边界和缩放并重绘。
 * 特殊处理：
 * - 仅对非边单元格应用旋转调整，确保覆盖层位置正确。
 * - 避免不必要的重绘，通过检查边界和缩放变化优化性能。
 * 交互逻辑：通过重绘覆盖层，保持用户界面与单元格状态一致，支持交互元素的正确显示。
 * 关键变量：
 * - state.overlays: 存储覆盖层的字典，包含所有覆盖层形状。
 * - rot: 单元格的旋转角度，影响覆盖层的位置。
 * - bounds: 覆盖层的边界，用于定位和渲染。
 * 方法目的：确保覆盖层与单元格状态的最新样式和位置同步，更新 DOM 显示。
 */
mxCellRenderer.prototype.redrawCellOverlays = function(state, forced)
{
	this.createCellOverlays(state);
    // 中文注释：调用 createCellOverlays 创建或更新单元格的覆盖层。

	if (state.overlays != null)
	{
		var rot = mxUtils.mod(mxUtils.getValue(state.style, mxConstants.STYLE_ROTATION, 0), 90);
        // 中文注释：获取单元格的旋转角度（从样式中获取，默认值为 0），并对 90 度取模。
        var rad = mxUtils.toRadians(rot);
        var cos = Math.cos(rad);
        var sin = Math.sin(rad);
        // 中文注释：将旋转角度转换为弧度，并计算余弦和正弦值，用于后续旋转计算。
        // 关键变量说明：rot 表示旋转角度，rad、cos、sin 用于坐标旋转计算。

		state.overlays.visit(function(id, shape)
		{
			var bounds = shape.overlay.getBounds(state);
            // 中文注释：获取覆盖层的边界，基于单元格状态。
            // 关键变量说明：bounds 表示覆盖层的边界矩形（mxRectangle），用于定位。

			if (!state.view.graph.getModel().isEdge(state.cell))
			{
				if (state.shape != null && rot != 0)
				{
					var cx = bounds.getCenterX();
					var cy = bounds.getCenterY();
                    // 中文注释：获取覆盖层边界的中心坐标（x, y）。
                    // 特殊处理：仅对非边单元格且有旋转角度时调整覆盖层位置。

					var point = mxUtils.getRotatedPoint(new mxPoint(cx, cy), cos, sin,
			        		new mxPoint(state.getCenterX(), state.getCenterY()));
                    // 中文注释：围绕单元格状态的中心点旋转覆盖层的中心坐标。
                    // 关键步骤：使用旋转矩阵（cos, sin）计算新坐标，确保覆盖层随单元格旋转。

			        cx = point.x;
			        cy = point.y;
			        bounds.x = Math.round(cx - bounds.width / 2);
			        bounds.y = Math.round(cy - bounds.height / 2);
                    // 中文注释：更新旋转后的中心坐标，并重新计算边界位置（保持覆盖层居中）。
				}
			}
            // 中文注释：检查单元格是否为边，仅对顶点单元格应用旋转调整。

			if (forced || shape.bounds == null || shape.scale != state.view.scale ||
				!shape.bounds.equals(bounds))
			{
				shape.bounds = bounds;
                // 中文注释：更新覆盖层的边界。
				shape.scale = state.view.scale;
                // 中文注释：更新覆盖层的缩放比例，与图形视图一致。
				shape.redraw();
                // 中文注释：调用覆盖层的 redraw 方法，更新 DOM 显示。
                // 关键步骤：仅在强制重绘、边界为空、缩放变化或边界不同时重绘，以优化性能。
			}
		});
        // 中文注释：遍历所有覆盖层，更新其边界和缩放并重绘。
        // 交互逻辑：确保覆盖层的位置和缩放与单元格状态同步，保持视觉一致性。
	}
};

/**
 * Function: redrawControl
 * 
 * Redraws the control for the given cell state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose control should be redrawn.
 */
/**
 * 函数: redrawControl
 *
 * 重绘给定单元格状态的控件。
 *
 * 参数：
 *
 * state - 要重绘控件的 <mxCellState>。
 * forced - 布尔值，指示是否强制重绘。
 *
 * 功能说明：更新折叠控件（如折叠/展开图标）的显示。
 * 关键步骤：
 * 1. 获取折叠图标。
 * 2. 如果控件存在且图标有效，计算控件边界并应用旋转和缩放。
 * 3. 如果需要（强制重绘或边界/缩放/旋转变化），更新控件并重绘。
 * 特殊处理：根据 legacyControlPosition 决定旋转来源（样式或文本旋转）。
 */
mxCellRenderer.prototype.redrawControl = function(state, forced)
{
	var image = state.view.graph.getFoldingImage(state);
    // 中文注释：获取折叠图标。

	if (state.control != null && image != null)
	{
		var bounds = this.getControlBounds(state, image.width, image.height);
        // 中文注释：计算控件的边界。
		var r = (this.legacyControlPosition) ?
				mxUtils.getValue(state.style, mxConstants.STYLE_ROTATION, 0) :
				state.shape.getTextRotation();
        // 中文注释：根据 legacyControlPosition 决定旋转角度来源（样式或文本旋转）。
		var s = state.view.scale;
        // 中文注释：获取当前视图的缩放比例。

		if (forced || state.control.scale != s || !state.control.bounds.equals(bounds) ||
			state.control.rotation != r)
		{
			state.control.rotation = r;
            // 中文注释：更新控件的旋转角度。
			state.control.bounds = bounds;
            // 中文注释：更新控件的边界。
			state.control.scale = s;
            // 中文注释：更新控件的缩放比例。

			state.control.redraw();
            // 中文注释：重绘控件。
		}
	}
};

/**
 * Function: getControlBounds
 * 
 * Returns the bounds to be used to draw the control (folding icon) of the
 * given state.
 */
/**
 * 函数: getControlBounds
 *
 * 返回用于绘制给定状态控件（折叠图标）的边界。
 *
 * 参数：
 *
 * state - 要计算控件边界的 <mxCellState>。
 * w - 控件宽度。
 * h - 控件高度。
 *
 * 功能说明：计算折叠控件的绘制边界，考虑缩放和旋转。
 * 关键步骤：
 * 1. 计算控件中心坐标，基于单元格类型（边或顶点）。
 * 2. 对于顶点，应用形状旋转或边界反转调整。
 * 3. 返回缩放后的边界矩形。
 * 特殊处理：
 * - 处理边和顶点的不同坐标计算逻辑。
 * - 考虑 legacyControlPosition 和形状的反转边界。
 * 返回值：控件边界矩形（mxRectangle），若控件不存在返回 null。
 */
mxCellRenderer.prototype.getControlBounds = function(state, w, h)
{
	if (state.control != null)
	{
		var s = state.view.scale;
		var cx = state.getCenterX();
		var cy = state.getCenterY();
        // 中文注释：获取缩放比例和单元格状态的中心坐标。

		if (!state.view.graph.getModel().isEdge(state.cell))
		{
			cx = state.x + w * s;
			cy = state.y + h * s;
            // 中文注释：对于顶点，计算控件坐标（基于状态坐标和控件尺寸）。

			if (state.shape != null)
			{
				// TODO: Factor out common code
				var rot = state.shape.getShapeRotation();
                // 中文注释：获取形状的旋转角度。

				if (this.legacyControlPosition)
				{
					rot = mxUtils.getValue(state.style, mxConstants.STYLE_ROTATION, 0);
                    // 中文注释：如果启用传统控件位置，从样式获取旋转角度。
				}
				else
				{
					if (state.shape.isPaintBoundsInverted())
					{
						var t = (state.width - state.height) / 2;
						cx += t;
						cy -= t;
                        // 中文注释：如果形状边界反转，调整控件中心坐标。
					}
				}
				
				if (rot != 0)
				{
			        var rad = mxUtils.toRadians(rot);
			        var cos = Math.cos(rad);
			        var sin = Math.sin(rad);
                    // 中文注释：将旋转角度转换为弧度并计算余弦、正弦值。

			        var point = mxUtils.getRotatedPoint(new mxPoint(cx, cy), cos, sin,
			        		new mxPoint(state.getCenterX(), state.getCenterY()));
                    // 中文注释：围绕状态中心旋转控件坐标。
			        cx = point.x;
			        cy = point.y;
                    // 中文注释：更新旋转后的控件中心坐标。
				}
			}
		}
		
		return (state.view.graph.getModel().isEdge(state.cell)) ? 
			new mxRectangle(Math.round(cx - w / 2 * s), Math.round(cy - h / 2 * s), Math.round(w * s), Math.round(h * s))
			: new mxRectangle(Math.round(cx - w / 2 * s), Math.round(cy - h / 2 * s), Math.round(w * s), Math.round(h * s));
        // 中文注释：返回缩放后的控件边界矩形，居中于计算的坐标。
	}
	
	return null;
    // 中文注释：如果控件不存在，返回 null。
};

/**
 * Function: insertStateAfter
 * 
 * Inserts the given array of <mxShapes> after the given nodes in the DOM.
 * 
 * Parameters:
 * 
 * shapes - Array of <mxShapes> to be inserted.
 * node - Node in <drawPane> after which the shapes should be inserted.
 * htmlNode - Node in the graph container after which the shapes should be inserted that
 * will not go into the <drawPane> (eg. HTML labels without foreignObjects).
 */
/**
 * 函数: insertStateAfter
 *
 * 将给定的 <mxShapes> 数组插入到 DOM 中指定节点之后。
 *
 * 参数：
 *
 * shapes - 要插入的 <mxShapes> 数组。
 * node - 绘制面板中插入形状的参考节点。
 * htmlNode - 图形容器中插入非绘制面板形状（例如无外来对象的 HTML 标签）的参考节点。
 *
 * 功能说明：将形状插入到 DOM 中的正确位置，确保显示顺序正确。
 * 关键步骤：
 * 1. 获取状态的形状列表（形状、文本、控件）。
 * 2. 遍历形状，插入到绘制面板或图形容器中的指定位置。
 * 3. 处理 HTML 标签的特殊插入逻辑。
 * 特殊处理：
 * - 确保 HTML 标签插入到图形容器中的正确位置。
 * - 处理画布后的第一个 HTML 节点。
 * 返回值：包含更新后的节点和 HTML 节点的数组。
 */
mxCellRenderer.prototype.insertStateAfter = function(state, node, htmlNode)
{
	var shapes = this.getShapesForState(state);
    // 中文注释：获取状态的形状列表（形状、文本、控件）。

	for (var i = 0; i < shapes.length; i++)
	{
		if (shapes[i] != null && shapes[i].node != null)
		{
			var html = shapes[i].node.parentNode != state.view.getDrawPane() &&
				shapes[i].node.parentNode != state.view.getOverlayPane();
            // 中文注释：检查形状是否为 HTML 节点（不在绘制面板或覆盖层面板）。
			var temp = (html) ? htmlNode : node;
            // 中文注释：根据是否为 HTML 节点选择参考节点。

			if (temp != null && temp.nextSibling != shapes[i].node)
			{
				if (temp.nextSibling == null)
				{
					temp.parentNode.appendChild(shapes[i].node);
                    // 中文注释：如果没有下一个兄弟节点，将形状节点追加到父节点。
				}
				else
				{
					temp.parentNode.insertBefore(shapes[i].node, temp.nextSibling);
                    // 中文注释：否则将形状节点插入到参考节点的下一个兄弟节点之前。
				}
			}
			else if (temp == null)
			{
				// Special case: First HTML node should be first sibling after canvas
				if (shapes[i].node.parentNode == state.view.graph.container)
				{
					var canvas = state.view.canvas;
                    // 中文注释：获取图形视图的画布。

					while (canvas != null && canvas.parentNode != state.view.graph.container)
					{
						canvas = canvas.parentNode;
                        // 中文注释：找到画布的顶层父节点。
					}
					
					if (canvas != null && canvas.nextSibling != null)
					{
						if (canvas.nextSibling != shapes[i].node)
						{
							shapes[i].node.parentNode.insertBefore(shapes[i].node, canvas.nextSibling);
                            // 中文注释：将形状节点插入到画布的下一个兄弟节点之前。
						}
					}
					else
					{
						shapes[i].node.parentNode.appendChild(shapes[i].node);
                        // 中文注释：如果没有下一个兄弟节点，追加到父节点。
					}
				}
				else if (shapes[i].node.parentNode != null &&
					shapes[i].node.parentNode.firstChild != null &&
					shapes[i].node.parentNode.firstChild != shapes[i].node)
				{
					// Inserts the node as the first child of the parent to implement the order
					shapes[i].node.parentNode.insertBefore(shapes[i].node, shapes[i].node.parentNode.firstChild);
                    // 中文注释：将形状节点插入为父节点的第一个子节点，以实现正确的顺序。
				}
			}
			
			if (html)
			{
				htmlNode = shapes[i].node;
                // 中文注释：更新 HTML 参考节点。
			}
			else
			{
				node = shapes[i].node;
                // 中文注释：更新绘制面板参考节点。
			}
		}
	}

	return [node, htmlNode];
    // 中文注释：返回更新后的绘制面板节点和 HTML 节点。
};

/**
 * Function: getShapesForState
 * 
 * Returns the <mxShapes> for the given cell state in the order in which they should
 * appear in the DOM.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose shapes should be returned.
 */
/**
 * 函数: getShapesForState
 *
 * 返回给定单元格状态的 <mxShapes>，按照其在 DOM 中应出现的顺序。
 *
 * 参数：
 *
 * state - 要返回形状的 <mxCellState>。
 *
 * 功能说明：获取与单元格状态关联的形状列表（形状、文本、控件）。
 * 返回值：按 DOM 顺序排列的形状数组（[形状, 文本, 控件]）。
 */
mxCellRenderer.prototype.getShapesForState = function(state)
{
	return [state.shape, state.text, state.control];
    // 中文注释：返回状态的形状、文本和控件，按 DOM 显示顺序排列。
};

/**
 * Function: redraw
 * 
 * Updates the bounds or points and scale of the shapes for the given cell
 * state. This is called in mxGraphView.validatePoints as the last step of
 * updating all cells.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the shapes should be updated.
 * force - Optional boolean that specifies if the cell should be reconfiured
 * and redrawn without any additional checks.
 * rendering - Optional boolean that specifies if the cell should actually
 * be drawn into the DOM. If this is false then redraw and/or reconfigure
 * will not be called on the shape.
 */
/**
 * 函数: redraw
 *
 * 更新给定单元格状态的形状的边界或点和缩放比例。作为 mxGraphView.validatePoints 的最后一步调用。
 *
 * 参数：
 *
 * state - 要更新形状的 <mxCellState>。
 * force - 可选布尔值，指示是否强制重新配置和重绘而不进行额外检查。
 * rendering - 可选布尔值，指示是否将单元格实际绘制到 DOM 中。如果为 false，则不调用重绘或重新配置。
 *
 * 功能说明：更新单元格状态的所有形状（形状、标签、覆盖层、控件）的边界、点和缩放，并重绘。
 * 关键步骤：
 * 1. 调用 redrawShape 更新主形状。
 * 2. 如果需要，调用 redrawLabel、redrawCellOverlays 和 redrawControl 更新其他元素。
 * 特殊处理：仅在 rendering 为 true 或未指定时执行 DOM 重绘。
 */
mxCellRenderer.prototype.redraw = function(state, force, rendering)
{
	var shapeChanged = this.redrawShape(state, force, rendering);
    // 中文注释：更新主形状并检查是否发生变化。

	if (state.shape != null && (rendering == null || rendering))
	{
		this.redrawLabel(state, shapeChanged);
        // 中文注释：重绘标签，传递形状是否变化的信息。
		this.redrawCellOverlays(state, shapeChanged);
        // 中文注释：重绘覆盖层。
		this.redrawControl(state, shapeChanged);
        // 中文注释：重绘控件。
	}
};

/**
 * Function: redrawShape
 * 
 * Redraws the shape for the given cell state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose label should be redrawn.
 */
/**
 * 函数: redrawShape
 *
 * 重绘给定单元格状态的形状。
 *
 * 参数：
 *
 * state - 要重绘形状的 <mxCellState>。
 * force - 布尔值，指示是否强制重绘。
 * rendering - 布尔值，指示是否实际绘制到 DOM。
 *
 * 功能说明：更新单元格状态的主形状，处理样式变化、创建新形状和事件绑定。
 * 关键步骤：
 * 1. 检查样式是否变化，若变化则销毁旧形状并创建新形状。
 * 2. 配置形状属性（抗锯齿、最小描边宽度等）。
 * 3. 创建指示器形状、覆盖层并绑定事件。
 * 4. 更新形状的边界或点，并重绘。
 * 特殊处理：
 * - 如果样式中的形状名称变化，重新创建形状。
 * - 处理指示器形状的变化。
 * - 忽略实时更新的手势重置问题。
 * 返回值：布尔值，指示形状是否发生变化。
 */
mxCellRenderer.prototype.redrawShape = function(state, force, rendering)
{
	var model = state.view.graph.model;
	var shapeChanged = false;
    // 中文注释：获取图形模型并初始化形状变化标志。

	// Forces creation of new shape if shape style has changed
	if (state.shape != null && state.shape.style != null && state.style != null &&
		state.shape.style[mxConstants.STYLE_SHAPE] != state.style[mxConstants.STYLE_SHAPE])
	{
		state.shape.destroy();
		state.shape = null;
        // 中文注释：如果形状样式变化，销毁旧形状并置空。
	}
	
	if (state.shape == null && state.view.graph.container != null &&
		state.cell != state.view.currentRoot &&
		(model.isVertex(state.cell) || model.isEdge(state.cell)))
	{
		state.shape = this.createShape(state);
        // 中文注释：如果形状不存在且满足条件（非根节点且为顶点或边），创建新形状。

		if (state.shape != null)
		{
			state.shape.minSvgStrokeWidth = this.minSvgStrokeWidth;
            // 中文注释：设置形状的最小 SVG 描边宽度。
			state.shape.antiAlias = this.antiAlias;
            // 中文注释：设置形状的抗锯齿选项。

			this.createIndicatorShape(state);
            // 中文注释：创建指示器形状。
			this.initializeShape(state);
            // 中文注释：初始化形状。
			this.createCellOverlays(state);
            // 中文注释：创建覆盖层。
			this.installListeners(state);
            // 中文注释：为形状安装事件监听器。

			// Forces a refresh of the handler if one exists
			state.view.graph.selectionCellsHandler.updateHandler(state);
            // 中文注释：更新选择单元格处理程序。
		}
	}
	else if (!force && state.shape != null && (!mxUtils.equalEntries(state.shape.style,
		state.style) || this.checkPlaceholderStyles(state)))
	{
		state.shape.resetStyles();
        // 中文注释：如果样式或占位符样式变化，重置形状样式。
		this.configureShape(state);
        // 中文注释：重新配置形状。
		// LATER: Ignore update for realtime to fix reset of current gesture
		state.view.graph.selectionCellsHandler.updateHandler(state);
        // 中文注释：更新选择单元格处理程序。
		force = true;
        // 中文注释：标记为需要强制重绘。
	}
	
	// Updates indicator shape
	if (state.shape != null && state.shape.indicatorShape !=
		this.getShape(state.view.graph.getIndicatorShape(state)))
	{
		if (state.shape.indicator != null)
		{
			state.shape.indicator.destroy();
			state.shape.indicator = null;
            // 中文注释：如果指示器形状变化，销毁旧指示器。
		}
		
		this.createIndicatorShape(state);
        // 中文注释：创建新指示器形状。

		if (state.shape.indicatorShape != null)
		{
			state.shape.indicator = new state.shape.indicatorShape();
            // 中文注释：创建新的指示器实例。
			state.shape.indicator.dialect = state.shape.dialect;
            // 中文注释：设置指示器的渲染方言。
			state.shape.indicator.init(state.node);
            // 中文注释：初始化指示器并添加到状态节点。
			force = true;
            // 中文注释：标记为需要强制重绘。
		}
	}

	if (state.shape != null)
	{
		// Handles changes of the collapse icon
		this.createControl(state);
        // 中文注释：创建或更新折叠控件。

		// Redraws the cell if required, ignores changes to bounds if points are
		// defined as the bounds are updated for the given points inside the shape
		if (force || this.isShapeInvalid(state, state.shape))
		{
			if (state.absolutePoints != null)
			{
				state.shape.points = state.absolutePoints.slice();
				state.shape.bounds = null;
                // 中文注释：如果定义了绝对点，更新形状的点并清空边界。
			}
			else
			{
				state.shape.points = null;
				state.shape.bounds = new mxRectangle(state.x, state.y, state.width, state.height);
                // 中文注释：否则清空点并设置形状的边界。
			}

			state.shape.scale = state.view.scale;
            // 中文注释：更新形状的缩放比例。

			if (rendering == null || rendering)
			{
				this.doRedrawShape(state);
                // 中文注释：如果需要渲染，调用 doRedrawShape 重绘形状。
			}
			else
			{
				state.shape.updateBoundingBox();
                // 中文注释：否则仅更新形状的边界框。
			}
			
			shapeChanged = true;
            // 中文注释：标记形状已发生变化。
		}
	}

	return shapeChanged;
    // 中文注释：返回形状是否发生变化。
};

/**
 * Function: doRedrawShape
 * 
 * Invokes redraw on the shape of the given state.
 */
/**
 * 函数: doRedrawShape
 *
 * 在给定状态的形状上调用重绘。
 *
 * 参数：
 *
 * state - 要重绘形状的 <mxCellState>。
 *
 * 功能说明：触发形状的重绘操作。
 * 关键步骤：调用形状的 redraw 方法，更新 DOM 中的显示。
 */
mxCellRenderer.prototype.doRedrawShape = function(state)
{
	state.shape.redraw();
    // 中文注释：调用形状的 redraw 方法，重绘形状。
};

/**
 * Function: isShapeInvalid
 * 
 * Returns true if the given shape must be repainted.
 */
/**
 * 函数: isShapeInvalid
 *
 * 判断给定形状是否需要重绘。
 *
 * 参数：
 *
 * state - 要检查的 <mxCellState>。
 * shape - 要检查的形状 <mxShape>。
 *
 * 功能说明：检查形状的边界、缩放或点是否与状态一致，决定是否需要重绘。
 * 关键步骤：
 * 1. 检查边界是否为空或缩放是否变化。
 * 2. 检查绝对点或边界是否一致。
 * 返回值：如果需要重绘返回 true，否则返回 false。
 */
mxCellRenderer.prototype.isShapeInvalid = function(state, shape)
{
	return shape.bounds == null || shape.scale != state.view.scale ||
		(state.absolutePoints == null && !shape.bounds.equals(state)) ||
		(state.absolutePoints != null && !mxUtils.equalPoints(shape.points, state.absolutePoints));
		// 中文注释：检查形状的边界、缩放或点是否无效，若任意条件满足则返回 true。
};

/**
 * Function: destroy
 * 
 * Destroys the shapes associated with the given cell state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> for which the shapes should be destroyed.
 */
/**
 * 函数: destroy
 *
 * 销毁与给定单元格状态关联的形状。
 *
 * 参数：
 *
 * state - 要销毁形状的 <mxCellState>。
 *
 * 功能说明：销毁单元格状态的所有形状（主形状、文本、覆盖层、控件）。
 * 关键步骤：
 * 1. 销毁文本形状。
 * 2. 销毁所有覆盖层。
 * 3. 销毁控件。
 * 4. 销毁主形状。
 * 特殊处理：确保所有相关资源被正确清理。
 */
mxCellRenderer.prototype.destroy = function(state)
{
	if (state.shape != null)
	{
		if (state.text != null)
		{		
			state.text.destroy();
			state.text = null;
            // 中文注释：销毁文本形状并置空。
		}
		
		if (state.overlays != null)
		{
			state.overlays.visit(function(id, shape)
			{
				shape.destroy();
                // 中文注释：销毁每个覆盖层形状。
			});
			
			state.overlays = null;
            // 中文注释：清空覆盖层字典。
		}

		if (state.control != null)
		{
			state.control.destroy();
			state.control = null;
            // 中文注释：销毁控件并置空。
		}
		
		state.shape.destroy();
		state.shape = null;
        // 中文注释：销毁主形状并置空。
	}
};
