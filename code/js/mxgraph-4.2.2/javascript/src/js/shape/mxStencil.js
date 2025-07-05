/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxStencil
 *
 * Implements a generic shape which is based on a XML node as a description.
 * 
 * shape:
 * 
 * The outer element is *shape*, that has attributes:
 * 
 * - "name", string, required. The stencil name that uniquely identifies the shape.
 * - "w" and "h" are optional decimal view bounds. This defines your co-ordinate
 * system for the graphics operations in the shape. The default is 100,100.
 * - "aspect", optional string. Either "variable", the default, or "fixed". Fixed
 * means always render the shape with the aspect ratio defined by the ratio w/h.
 * Variable causes the ratio to match that of the geometry of the current vertex.
 * - "strokewidth", optional string. Either an integer or the string "inherit".
 * "inherit" indicates that the strokeWidth of the cell is only changed on scaling,
 * not on resizing. Default is "1".
 * If numeric values are used, the strokeWidth of the cell is changed on both
 * scaling and resizing and the value defines the multiple that is applied to
 * the width.
 * 
 * connections:
 * 
 * If you want to define specific fixed connection points on the shape use the
 * *connections* element. Each *constraint* element within connections defines
 * a fixed connection point on the shape. Constraints have attributes:
 * 
 * - "perimeter", required. 1 or 0. 0 sets the connection point where specified
 * by x,y. 1 Causes the position of the connection point to be extrapolated from
 * the center of the shape, through x,y to the point of intersection with the
 * perimeter of the shape.
 * - "x" and "y" are the position of the fixed point relative to the bounds of
 * the shape. They can be automatically adjusted if perimeter=1. So, (0,0) is top
 * left, (0.5,0.5) the center, (1,0.5) the center of the right hand edge of the
 * bounds, etc. Values may be less than 0 or greater than 1 to be positioned
 * outside of the shape.
 * - "name", optional string. A unique identifier for the port on the shape.
 * 
 * background and foreground:
 * 
 * The path of the graphics drawing is split into two elements, *foreground* and
 * *background*. The split is to define which part any shadow applied to the shape
 * is derived from (the background). This, generally, means the background is the
 * line tracing of the outside of the shape, but not always.
 * 
 * Any stroke, fill or fillstroke of a background must be the first element of the
 * foreground element, they must not be used within *background*. If the background
 * is empty, this is not required.
 * 
 * Because the background cannot have any fill or stroke, it can contain only one
 * *path*, *rect*, *roundrect* or *ellipse* element (or none). It can also not
 * include *image*, *text* or *include-shape*.
 * 
 * Note that the state, styling and drawing in mxGraph stencils is very close in
 * design to that of HTML 5 canvas. Tutorials on this subject, if you're not
 * familiar with the topic, will give a good high-level introduction to the
 * concepts used.
 * 
 * State:
 * 
 * Rendering within the foreground and background elements has the concept of
 * state. There are two types of operations other than state save/load, styling
 * and drawing. The styling operations change the current state, so you can save
 * the current state with <save/> and pull the last saved state from the state
 * stack using <restore/>.
 * 
 * Styling:
 * 
 * The elements that change colors within the current state all take a hash
 * prefixed hex color code ("#FFEA80").
 * 
 * - *strokecolor*, this sets the color that drawing paths will be rendered in
 * when a stroke or fillstroke command is issued.
 * - *fillcolor*, this sets the color that the inside of closed paths will be
 * rendered in when a fill or fillstroke command is issued.
 * - *fontcolor*, this sets the color that fonts are rendered in when text is drawn.
 * 
 * *alpha* defines the degree of transparency used between 1.0 for fully opaque
 * and 0.0 for fully transparent.
 * 
 * *fillalpha* defines the degree of fill transparency used between 1.0 for fully
 * opaque and 0.0 for fully transparent.
 * 
 * *strokealpha* defines the degree of stroke transparency used between 1.0 for
 * fully opaque and 0.0 for fully transparent.
 * 
 * *strokewidth* defines the integer thickness of drawing elements rendered by
 * stroking. Use fixed="1" to apply the value as-is, without scaling.
 * 
 * *dashed* is "1" for dashing enabled and "0" for disabled.
 * 
 * When *dashed* is enabled the current dash pattern, defined by *dashpattern*,
 * is used on strokes. dashpattern is a sequence of space separated "on, off"
 * lengths that define what distance to paint the stroke for, then what distance
 * to paint nothing for, repeat... The default is "3 3". You could define a more
 * complex pattern with "5 3 2 6", for example. Generally, it makes sense to have
 * an even number of elements in the dashpattern, but that's not required.
 * 
 * *linejoin*, *linecap* and *miterlimit* are best explained by the Mozilla page
 * on Canvas styling (about halfway down). The values are all the same except we
 * use "flat" for linecap, instead of Canvas' "butt".
 * 
 * For font styling there are.
 * 
 * - *fontsize*, an integer,
 * - *fontstyle*, an ORed bit pattern of bold (1), italic (2) and underline (4),
 * i.e bold underline is "5".
 * - *fontfamily*, is a string defining the typeface to be used.
 * 
 * Drawing:
 * 
 * Most drawing is contained within a *path* element. Again, the graphic
 * primitives are very similar to that of HTML 5 canvas.
 * 
 * - *move* to attributes required decimals (x,y).
 * - *line* to attributes required decimals (x,y).
 * - *quad* to required decimals (x2,y2) via control point required decimals
 * (x1,y1).
 * - *curve* to required decimals (x3,y3), via control points required decimals
 * (x1,y1) and (x2,y2).
 * - *arc*, this doesn't follow the HTML Canvas signatures, instead it's a copy
 * of the SVG arc command. The SVG specification documentation gives the best
 * description of its behaviors. The attributes are named identically, they are
 * decimals and all required.
 * - *close* ends the current subpath and causes an automatic straight line to
 * be drawn from the current point to the initial point of the current subpath.
 * 
 * Complex drawing:
 * 
 * In addition to the graphics primitive operations there are non-primitive
 * operations. These provide an easy method to draw some basic shapes.
 * 
 * - *rect*, attributes "x", "y", "w", "h", all required decimals
 * - *roundrect*, attributes "x", "y", "w", "h", all required decimals. Also
 * "arcsize" an optional decimal attribute defining how large, the corner curves
 * are.
 * - *ellipse*, attributes "x", "y", "w", "h", all required decimals.
 * 
 * Note that these 3 shapes and all paths must be followed by either a fill,
 * stroke, or fillstroke.
 * 
 * Text:
 * 
 * *text* elements have the following attributes.
 * 
 * - "str", the text string to display, required.
 * - "x" and "y", the decimal location (x,y) of the text element, required.
 * - "align", the horizontal alignment of the text element, either "left",
 * "center" or "right". Optional, default is "left".
 * - "valign", the vertical alignment of the text element, either "top", "middle"
 * or "bottom". Optional, default is "top".
 * - "localized", 0 or 1, if 1 then the "str" actually contains a key to use to
 * fetch the value out of mxResources. Optional, default is
 * <mxStencil.defaultLocalized>.
 * - "vertical", 0 or 1, if 1 the label is rendered vertically (rotated by 90
 * degrees). Optional, default is 0.
 * - "rotation", angle in degrees (0 to 360). The angle to rotate the text by.
 * Optional, default is 0.
 * - "align-shape", 0 or 1, if 0 ignore the rotation of the shape when setting
 * the text rotation. Optional, default is 1.
 * 
 * If <allowEval> is true, then the text content of the this element can define
 * a function which is invoked with the shape as the only argument and returns
 * the value for the text element (ignored if the str attribute is not null).
 * 
 * Images:
 * 
 * *image* elements can either be external URLs, or data URIs, where supported
 * (not in IE 7-). Attributes are:
 * 
 * - "src", required string. Either a data URI or URL.
 * - "x", "y", required decimals. The (x,y) position of the image.
 * - "w", "h", required decimals. The width and height of the image.
 * - "flipH" and "flipV", optional 0 or 1. Whether to flip the image along the
 * horizontal/vertical axis. Default is 0 for both.
 * 
 * If <allowEval> is true, then the text content of the this element can define
 * a function which is invoked with the shape as the only argument and returns
 * the value for the image source (ignored if the src attribute is not null).
 * 
 * Sub-shapes:
 * 
 * *include-shape* allow stencils to be rendered within the current stencil by
 * referencing the sub-stencil by name. Attributes are:
 * 
 * - "name", required string. The unique shape name of the stencil.
 * - "x", "y", "w", "h", required decimals. The (x,y) position of the sub-shape
 * and its width and height.
 * 
 * Constructor: mxStencil
 * 
 * Constructs a new generic shape by setting <desc> to the given XML node and
 * invoking <parseDescription> and <parseConstraints>.
 * 
 * Parameters:
 * 
 * desc - XML node that contains the stencil description.
 */
/**
 * mxStencil 构造函数：基于 XML 节点描述创建通用形状
 *
 * 参数：
 * desc - XML 节点，包含模板描述。
 * 功能：初始化模板，解析描述和约束。
 */
function mxStencil(desc)
{
	this.desc = desc;
	this.parseDescription();
	this.parseConstraints();
    // 中文注释：
    // 构造函数用于创建基于 XML 的通用形状对象。
    // 参数说明：
    // - desc: XML 节点，包含形状的完整描述信息。
    // 关键步骤：
    // 1. 将输入的 XML 节点存储到 this.desc。
    // 2. 调用 parseDescription 方法解析形状描述。
    // 3. 调用 parseConstraints 方法解析连接约束。
};

/**
 * Extends mxShape.
 */
// 中文注释：
// mxStencil 继承自 mxShape，扩展其功能以支持基于 XML 的形状定义。
mxUtils.extend(mxStencil, mxShape);

/**
 * Variable: defaultLocalized
 * 
 * Static global variable that specifies the default value for the localized
 * attribute of the text element. Default is false.
 */
// 中文注释：
// defaultLocalized：静态全局变量，指定文本元素的 localized 属性默认值。
// 用途：控制是否从 mxResources 获取本地化文本，默认值为 false。
mxStencil.defaultLocalized = false;

/**
 * Function: allowEval
 * 
 * Static global switch that specifies if the use of eval is allowed for
 * evaluating text content and images. Default is false. Set this to true
 * if stencils can not contain user input.
 */
// 中文注释：
// allowEval：静态全局开关，控制是否允许使用 eval 解析文本内容和图片。
// 默认值：false。
// 注意事项：仅在确保模板不包含用户输入时设为 true，以防止安全风险。
mxStencil.allowEval = false;

/**
 * Variable: desc
 *
 * Holds the XML node with the stencil description.
 */
// 中文注释：
// desc：存储包含模板描述的 XML 节点。
// 用途：作为模板的核心数据结构，用于解析形状和约束信息。
mxStencil.prototype.desc = null;

/**
 * Variable: constraints
 * 
 * Holds an array of <mxConnectionConstraints> as defined in the shape.
 */
// 中文注释：
// constraints：存储形状定义中的 mxConnectionConstraints 数组。
// 用途：保存固定连接点信息，用于形状的连接逻辑。
mxStencil.prototype.constraints = null;

/**
 * Variable: aspect
 *
 * Holds the aspect of the shape. Default is 'auto'.
 */
// 中文注释：
// aspect：存储形状的宽高比设置，默认值为 'auto'。
// 用途：决定形状是否保持固定宽高比（fixed）或适应几何形状（variable）。
mxStencil.prototype.aspect = null;

/**
 * Variable: w0
 *
 * Holds the width of the shape. Default is 100.
 */
// 中文注释：
// w0：存储形状的默认宽度，默认值为 100。
// 用途：定义形状的坐标系宽度，用于图形操作。
mxStencil.prototype.w0 = null;

/**
 * Variable: h0
 *
 * Holds the height of the shape. Default is 100.
 */
// 中文注释：
// h0：存储形状的默认高度，默认值为 100。
// 用途：定义形状的坐标系高度，用于图形操作。
mxStencil.prototype.h0 = null;

/**
 * Variable: bgNodes
 *
 * Holds the XML node with the stencil description.
 */
// 中文注释：
// bgNode：存储背景部分的 XML 节点。
// 用途：包含形状背景的绘制描述，通常用于定义阴影的来源。
mxStencil.prototype.bgNode = null;

/**
 * Variable: fgNodes
 *
 * Holds the XML node with the stencil description.
 */
// 中文注释：
// fgNode：存储前景部分的 XML 节点。
// 用途：包含形状前景的绘制描述，用于主要图形元素。
mxStencil.prototype.fgNode = null;

/**
 * Variable: strokewidth
 *
 * Holds the strokewidth direction from the description.
 */
// 中文注释：
// strokewidth：存储从描述中解析的线条宽度设置。
// 用途：定义绘制线条的宽度，可为数字或 "inherit"。
// 注意事项：若为 "inherit"，线宽仅在缩放时变化，不受大小调整影响。
mxStencil.prototype.strokewidth = null;

/**
 * Function: parseDescription
 *
 * Reads <w0>, <h0>, <aspect>, <bgNodes> and <fgNodes> from <desc>.
 */
/**
 * parseDescription 方法：解析模板描述
 *
 * 功能：从 desc 中读取 w0、h0、aspect、bgNode 和 fgNode。
 * 关键步骤：
 * 1. 获取前景和背景的 XML 节点。
 * 2. 解析形状的宽度 (w)、高度 (h) 和宽高比 (aspect)。
 * 3. 解析线条宽度 (strokewidth) 设置。
 */
mxStencil.prototype.parseDescription = function()
{
	// LATER: Preprocess nodes for faster painting
    // 中文注释：待优化：预处理节点以提高绘制性能。
	this.fgNode = this.desc.getElementsByTagName('foreground')[0];
    // 中文注释：获取前景 XML 节点，包含主要图形元素描述。
	this.bgNode = this.desc.getElementsByTagName('background')[0];
    // 中文注释：获取背景 XML 节点，通常用于定义形状轮廓。
	this.w0 = Number(this.desc.getAttribute('w') || 100);
    // 中文注释：解析形状默认宽度，默认为 100。
	this.h0 = Number(this.desc.getAttribute('h') || 100);
    // 中文注释：解析形状默认高度，默认为 100。

	// Possible values for aspect are: variable and fixed where
	// variable means fill the available space and fixed means
	// use w0 and h0 to compute the aspect.
	var aspect = this.desc.getAttribute('aspect');
    // 中文注释：获取宽高比设置，可能值为 "variable"（自适应）或 "fixed"（固定）。
	this.aspect = (aspect != null) ? aspect : 'variable';
    // 中文注释：设置形状的宽高比，默认为 "variable"。

	// Possible values for strokewidth are all numbers and "inherit"
	// where the inherit means take the value from the style (ie. the
	// user-defined stroke-width). Note that the strokewidth is scaled
	// by the minimum scaling that is used to draw the shape (sx, sy).
	var sw = this.desc.getAttribute('strokewidth');
    // 中文注释：获取线条宽度设置，可能为数字或 "inherit"。
	this.strokewidth = (sw != null) ? sw : '1';
    // 中文注释：设置线条宽度，默认为 "1"。
    // 注意事项：若为 "inherit"，线宽从样式中获取，仅在缩放时调整。
};

/**
 * Function: parseConstraints
 *
 * Reads the constraints from <desc> into <constraints> using
 * <parseConstraint>.
 */
/**
 * parseConstraints 方法：解析连接约束
 *
 * 功能：从 desc 中读取连接点信息并存储到 constraints 数组。
 * 关键步骤：
 * 1. 获取 connections 元素。
 * 2. 遍历所有 constraint 子节点，调用 parseConstraint 方法解析。
 */
mxStencil.prototype.parseConstraints = function()
{
	var conns = this.desc.getElementsByTagName('connections')[0];
    // 中文注释：获取 connections 元素，包含所有固定连接点定义。

	if (conns != null)
	{
		var tmp = mxUtils.getChildNodes(conns);
        // 中文注释：获取 connections 元素的所有子节点。

		if (tmp != null && tmp.length > 0)
		{
			this.constraints = [];
            // 中文注释：初始化 constraints 数组，用于存储连接约束。

			for (var i = 0; i < tmp.length; i++)
			{
				this.constraints.push(this.parseConstraint(tmp[i]));
                // 中文注释：调用 parseConstraint 解析每个 constraint 节点并添加到 constraints 数组。
			}
		}
	}
};

/**
 * Function: parseConstraint
 *
 * Parses the given XML node and returns its <mxConnectionConstraint>.
 */
/**
 * parseConstraint 方法：解析单个连接约束
 *
 * 功能：解析 XML 节点并返回 mxConnectionConstraint 对象。
 * 参数：
 * - node: 包含约束描述的 XML 节点。
 * 返回值：mxConnectionConstraint 对象。
 * 关键步骤：
 * 1. 解析 x、y 坐标。
 * 2. 解析 perimeter 属性（是否基于形状边界）。
 * 3. 解析 name 属性（连接点标识）。
 */
mxStencil.prototype.parseConstraint = function(node)
{
	var x = Number(node.getAttribute('x'));
    // 中文注释：获取连接点的 x 坐标（相对于形状边界）。
	var y = Number(node.getAttribute('y'));
    // 中文注释：获取连接点的 y 坐标（相对于形状边界）。
	var perimeter = node.getAttribute('perimeter') == '1';
    // 中文注释：获取 perimeter 属性，决定连接点是否基于形状边界计算。
    // 值为 1 时，连接点从形状中心通过 (x,y) 推算到边界交点。
	var name = node.getAttribute('name');
    // 中文注释：获取连接点的唯一标识符（可选）。

	return new mxConnectionConstraint(new mxPoint(x, y), perimeter, name);
    // 中文注释：返回新的 mxConnectionConstraint 对象，包含坐标、perimeter 和 name。
};

/**
 * Function: evaluateTextAttribute
 * 
 * Gets the given attribute as a text. The return value from <evaluateAttribute>
 * is used as a key to <mxResources.get> if the localized attribute in the text
 * node is 1 or if <defaultLocalized> is true.
 */
/**
 * evaluateTextAttribute 方法：解析文本属性
 *
 * 功能：获取指定属性的文本值，并根据 localized 设置进行本地化处理。
 * 参数：
 * - node: XML 节点，包含属性信息。
 * - attribute: 要获取的属性名称。
 * - shape: 当前形状对象。
 * 返回值：处理后的文本值。
 * 关键步骤：
 * 1. 调用 evaluateAttribute 获取属性值。
 * 2. 若 localized 为 1 或 defaultLocalized 为 true，使用 mxResources.get 进行本地化。
 */
mxStencil.prototype.evaluateTextAttribute = function(node, attribute, shape)
{
	var result = this.evaluateAttribute(node, attribute, shape);
    // 中文注释：调用 evaluateAttribute 获取属性值。
	var loc = node.getAttribute('localized');
    // 中文注释：获取 localized 属性，决定是否需要本地化。

	if ((mxStencil.defaultLocalized && loc == null) || loc == '1')
	{
		result = mxResources.get(result);
        // 中文注释：若需要本地化，使用 mxResources.get 获取本地化文本。
	}

	return result;
    // 中文注释：返回最终的文本值。
};

/**
 * Function: evaluateAttribute
 *
 * Gets the attribute for the given name from the given node. If the attribute
 * does not exist then the text content of the node is evaluated and if it is
 * a function it is invoked with <shape> as the only argument and the return
 * value is used as the attribute value to be returned.
 */
/**
 * evaluateAttribute 方法：解析属性值
 *
 * 功能：获取指定属性的值，若不存在则尝试解析节点文本内容作为函数。
 * 参数：
 * - node: XML 节点，包含属性信息。
 * - attribute: 要获取的属性名称。
 * - shape: 当前形状对象。
 * 返回值：属性值或函数执行结果。
 * 关键步骤：
 * 1. 尝试获取指定属性值。
 * 2. 若属性不存在且 allowEval 为 true，解析节点文本内容作为函数并执行。
 * 注意事项：仅当 allowEval 为 true 时才允许执行函数，防止安全风险。
 */
mxStencil.prototype.evaluateAttribute = function(node, attribute, shape)
{
	var result = node.getAttribute(attribute);
    // 中文注释：尝试获取指定属性的值。

	if (result == null)
	{
		var text = mxUtils.getTextContent(node);
        // 中文注释：若属性不存在，获取节点的文本内容。

		if (text != null && mxStencil.allowEval)
		{
			var funct = mxUtils.eval(text);
            // 中文注释：若 allowEval 为 true，解析文本内容为函数。

			if (typeof(funct) == 'function')
			{
				result = funct(shape);
                // 中文注释：执行函数并将结果作为属性值。
			}
		}
	}
	
	return result;
    // 中文注释：返回最终的属性值。
};

/**
 * Function: drawShape
 *
 * Draws this stencil inside the given bounds.
 */
/**
 * drawShape 方法：在指定边界内绘制模板
 *
 * 功能：根据给定的边界和样式绘制形状。
 * 参数：
 * - canvas: 画布对象，用于绘制操作。
 * - shape: 当前形状对象，包含样式信息。
 * - x, y: 绘制的起始坐标。
 * - w, h: 绘制的宽度和高度。
 * 关键步骤：
 * 1. 保存当前画布状态。
 * 2. 计算宽高比和缩放比例。
 * 3. 设置线条宽度。
 * 4. 绘制背景和前景元素。
 * 5. 恢复画布状态（若需要）。
 * 事件处理逻辑：处理形状的方向（direction）和指针事件（pointer-events）。
 * 样式设置：根据形状样式设置线条宽度、颜色等。
 * 特殊处理：支持透明矩形以捕获事件，处理背景轮廓显示。
 */
mxStencil.prototype.drawShape = function(canvas, shape, x, y, w, h)
{
	var stack = canvas.states.slice();
    // 中文注释：保存当前画布状态栈，以确保后续恢复。

	// TODO: Internal structure (array of special structs?), relative and absolute
	// coordinates (eg. note shape, process vs star, actor etc.), text rendering
	// and non-proportional scaling, how to implement pluggable edge shapes
	// (start, segment, end blocks), pluggable markers, how to implement
	// swimlanes (title area) with this API, add icon, horizontal/vertical
	// label, indicator for all shapes, rotation
    // 中文注释：待实现功能：支持复杂内部结构、相对/绝对坐标、文本渲染、非比例缩放、可插拔边形状、标记、泳道标题区域、图标、水平/垂直标签、旋转等。
	var direction = mxUtils.getValue(shape.style, mxConstants.STYLE_DIRECTION, null);
    // 中文注释：获取形状的方向（direction），默认为 null。
	var aspect = this.computeAspect(shape.style, x, y, w, h, direction);
    // 中文注释：计算形状的宽高比和偏移，返回包含 x、y、width、height 的矩形。
	var minScale = Math.min(aspect.width, aspect.height);
    // 中文注释：计算最小缩放比例，用于线条宽度调整。
	var sw = (this.strokewidth == 'inherit') ?
			Number(mxUtils.getNumber(shape.style, mxConstants.STYLE_STROKEWIDTH, 1)) :
			Number(this.strokewidth) * minScale;
    // 中文注释：根据 strokewidth 设置线条宽度，若为 "inherit"，从样式获取，否则按最小缩放比例调整。
	canvas.setStrokeWidth(sw);
    // 中文注释：设置画布的线条宽度。

	// Draws a transparent rectangle for catching events
	if (shape.style != null && mxUtils.getValue(shape.style, mxConstants.STYLE_POINTER_EVENTS, '0') == '1')
	{
		canvas.setStrokeColor(mxConstants.NONE);
        // 中文注释：设置无描边颜色，准备绘制透明矩形。
		canvas.rect(x, y, w, h);
        // 中文注释：绘制透明矩形，用于捕获指针事件。
		canvas.stroke();
        // 中文注释：执行描边操作。
		canvas.setStrokeColor(shape.stroke);
        // 中文注释：恢复形状的描边颜色。
	}

	this.drawChildren(canvas, shape, x, y, w, h, this.bgNode, aspect, false, true);
    // 中文注释：绘制背景元素，disableShadow 为 false，paint 为 true。
	this.drawChildren(canvas, shape, x, y, w, h, this.fgNode, aspect, true,
		!shape.outline || shape.style == null || mxUtils.getValue(
		shape.style, mxConstants.STYLE_BACKGROUND_OUTLINE, 0) == 0);
    // 中文注释：绘制前景元素，disableShadow 为 true，paint 取决于 outline 和样式设置。

	// Restores stack for unequal count of save/restore calls
	if (canvas.states.length != stack.length)
	{
		canvas.states = stack;
        // 中文注释：若 save/restore 调用次数不匹配，恢复原始状态栈。
	}
};

/**
 * Function: drawChildren
 *
 * Draws this stencil inside the given bounds.
 */
/**
 * drawChildren 方法：在指定边界内绘制子节点
 *
 * 功能：遍历并绘制指定节点的所有子节点。
 * 参数：
 * - canvas: 画布对象。
 * - shape: 当前形状对象。
 * - x, y: 绘制起始坐标。
 * - w, h: 绘制宽度和高度。
 * - node: 要绘制的 XML 节点（前景或背景）。
 * - aspect: 宽高比和偏移信息。
 * - disableShadow: 是否禁用阴影。
 * - paint: 是否执行绘制。
 * 关键步骤：
 * 1. 检查节点和边界有效性。
 * 2. 遍历子节点，调用 drawNode 方法绘制。
 */
mxStencil.prototype.drawChildren = function(canvas, shape, x, y, w, h, node, aspect, disableShadow, paint)
{
	if (node != null && w > 0 && h > 0)
	{
		var tmp = node.firstChild;
        // 中文注释：获取节点的第一个子节点。

		while (tmp != null)
		{
			if (tmp.nodeType == mxConstants.NODETYPE_ELEMENT)
			{
				this.drawNode(canvas, shape, tmp, aspect, disableShadow, paint);
                // 中文注释：若子节点是元素节点，调用 drawNode 绘制。
			}
			
			tmp = tmp.nextSibling;
            // 中文注释：移动到下一个子节点。
		}
	}
};

/**
 * Function: computeAspect
 *
 * Returns a rectangle that contains the offset in x and y and the horizontal
 * and vertical scale in width and height used to draw this shape inside the
 * given <mxRectangle>.
 * 
 * Parameters:
 * 
 * shape - <mxShape> to be drawn.
 * bounds - <mxRectangle> that should contain the stencil.
 * direction - Optional direction of the shape to be darwn.
 */
/**
 * computeAspect 方法：计算绘制形状的宽高比和偏移
 *
 * 功能：根据形状样式和边界计算绘制所需的偏移和缩放比例。
 * 参数：
 * - shape: 当前形状对象。
 * - x, y: 绘制起始坐标。
 * - w, h: 绘制宽度和高度。
 * - direction: 形状方向（可选）。
 * 返回值：mxRectangle 对象，包含偏移 (x, y) 和缩放比例 (width, height)。
 * 关键步骤：
 * 1. 计算水平和垂直缩放比例 (sx, sy)。
 * 2. 根据方向调整坐标和缩放。
 * 3. 若 aspect 为 fixed，确保宽高比一致并居中。
 */
mxStencil.prototype.computeAspect = function(shape, x, y, w, h, direction)
{
	var x0 = x;
	var y0 = y;
	var sx = w / this.w0;
    // 中文注释：计算水平缩放比例。
	var sy = h / this.h0;
    // 中文注释：计算垂直缩放比例。

	var inverse = (direction == mxConstants.DIRECTION_NORTH || direction == mxConstants.DIRECTION_SOUTH);
    // 中文注释：检查方向是否为北或南，需要交换宽高。

	if (inverse)
	{
		sy = w / this.h0;
		sx = h / this.w0;
        // 中文注释：若方向为北或南，交换宽高缩放比例。

		var delta = (w - h) / 2;
        // 中文注释：计算坐标偏移以适应交换后的宽高。

		x0 += delta;
		y0 -= delta;
        // 中文注释：调整起始坐标。
	}

	if (this.aspect == 'fixed')
	{
		sy = Math.min(sx, sy);
		sx = sy;
        // 中文注释：若 aspect 为 fixed，保持宽高比一致，选择最小缩放比例。

		// Centers the shape inside the available space
		if (inverse)
		{
			x0 += (h - this.w0 * sx) / 2;
			y0 += (w - this.h0 * sy) / 2;
            // 中文注释：若方向为北或南，调整坐标以居中形状。
		}
		else
		{
			x0 += (w - this.w0 * sx) / 2;
			y0 += (h - this.h0 * sy) / 2;
            // 中文注释：调整坐标以居中形状。
		}
	}

	return new mxRectangle(x0, y0, sx, sy);
    // 中文注释：返回包含偏移和缩放比例的 mxRectangle 对象。
};

/**
 * Function: drawNode
 *
 * Draws this stencil inside the given bounds.
 */
/**
 * drawNode 方法：绘制单个节点
 *
 * 功能：根据节点类型执行相应的绘制操作。
 * 参数：
 * - canvas: 画布对象。
 * - shape: 当前形状对象。
 * - node: 要绘制的 XML 节点。
 * - aspect: 宽高比和偏移信息。
 * - disableShadow: 是否禁用阴影。
 * - paint: 是否执行绘制。
 * 关键步骤：
 * 1. 根据节点名称执行对应绘制操作（如路径、矩形、文本等）。
 * 2. 处理样式设置（如颜色、透明度、字体等）。
 * 3. 支持复杂图形（如圆角矩形、椭圆）和子模板。
 * 事件处理逻辑：处理文本旋转、翻转等交互效果。
 * 样式设置：动态设置描边、填充、字体样式等。
 * 特殊处理：对于圆角路径、图像和文本，需特殊处理以确保正确渲染。
 */
mxStencil.prototype.drawNode = function(canvas, shape, node, aspect, disableShadow, paint)
{
	var name = node.nodeName;
    // 中文注释：获取节点名称，决定绘制操作类型。
	var x0 = aspect.x;
	var y0 = aspect.y;
	var sx = aspect.width;
	var sy = aspect.height;
    // 中文注释：从 aspect 获取偏移 (x, y) 和缩放比例 (width, height)。
	var minScale = Math.min(sx, sy);
    // 中文注释：计算最小缩放比例，用于调整线条宽度等。

	if (name == 'save')
	{
		canvas.save();
        // 中文注释：保存当前画布状态。
	}
	else if (name == 'restore')
	{
		canvas.restore();
        // 中文注释：恢复上一次保存的画布状态。
	}
	else if (paint)
	{
		if (name == 'path')
		{
			canvas.begin();
            // 中文注释：开始新的路径绘制。

			var parseRegularly = true;
            // 中文注释：标志位，控制是否按常规方式解析路径。

			if (node.getAttribute('rounded') == '1')
			{
				parseRegularly = false;
                // 中文注释：若路径为圆角路径，禁用常规解析。

				var arcSize = Number(node.getAttribute('arcSize'));
                // 中文注释：获取圆角大小。
				var pointCount = 0;
                // 中文注释：记录路径点数量。
				var segs = [];
                // 中文注释：存储路径段的数组。

				// Renders the elements inside the given path
				var childNode = node.firstChild;
                // 中文注释：获取路径的第一个子节点。

				while (childNode != null)
				{
					if (childNode.nodeType == mxConstants.NODETYPE_ELEMENT)
					{
						var childName = childNode.nodeName;
                        // 中文注释：获取子节点名称。

						if (childName == 'move' || childName == 'line')
						{
							if (childName == 'move' || segs.length == 0)
							{
								segs.push([]);
                                // 中文注释：若为 move 或新路径段，创建新段。
							}
							
							segs[segs.length - 1].push(new mxPoint(x0 + Number(childNode.getAttribute('x')) * sx,
								y0 + Number(childNode.getAttribute('y')) * sy));
                            // 中文注释：将缩放后的坐标点添加到当前路径段。
							pointCount++;
                            // 中文注释：增加路径点计数。
						}
						else
						{
							//We only support move and line for rounded corners
							parseRegularly = true;
                            // 中文注释：若子节点不支持圆角（如 quad、curve），恢复常规解析。
							break;
						}
					}
					
					childNode = childNode.nextSibling;
                    // 中文注释：移动到下一个子节点。
				}

				if (!parseRegularly && pointCount > 0)
				{
					for (var i = 0; i < segs.length; i++)
					{
						var close = false, ps = segs[i][0], pe = segs[i][segs[i].length - 1];
                        // 中文注释：检查路径段是否闭合。

						if (ps.x == pe.x && ps.y == pe.y) 
						{
							segs[i].pop();
							close = true;
                            // 中文注释：若起点和终点相同，移除终点并标记为闭合路径。
						}
						
						this.addPoints(canvas, segs[i], true, arcSize, close);
                        // 中文注释：调用 addPoints 方法绘制圆角路径段。
					}
				}
				else
				{
					parseRegularly = true;
                    // 中文注释：若无圆角路径，恢复常规解析。
				}
			}
			
			if (parseRegularly)
			{
				// Renders the elements inside the given path
				var childNode = node.firstChild;
                // 中文注释：获取路径的第一个子节点。

				while (childNode != null)
				{
					if (childNode.nodeType == mxConstants.NODETYPE_ELEMENT)
					{
						this.drawNode(canvas, shape, childNode, aspect, disableShadow, paint);
                        // 中文注释：递归调用 drawNode 绘制路径内的子节点。
					}
					
					childNode = childNode.nextSibling;
                    // 中文注释：移动到下一个子节点。
				}
			}
		}
		else if (name == 'close')
		{
			canvas.close();
            // 中文注释：闭合当前子路径，自动连接到起点。
		}
		else if (name == 'move')
		{
			canvas.moveTo(x0 + Number(node.getAttribute('x')) * sx, y0 + Number(node.getAttribute('y')) * sy);
            // 中文注释：将画笔移动到指定坐标（缩放后）。
		}
		else if (name == 'line')
		{
			canvas.lineTo(x0 + Number(node.getAttribute('x')) * sx, y0 + Number(node.getAttribute('y')) * sy);
            // 中文注释：绘制直线到指定坐标（缩放后）。
		}
		else if (name == 'quad')
		{
			canvas.quadTo(x0 + Number(node.getAttribute('x1')) * sx,
					y0 + Number(node.getAttribute('y1')) * sy,
					x0 + Number(node.getAttribute('x2')) * sx,
					y0 + Number(node.getAttribute('y2')) * sy);
            // 中文注释：绘制二次贝塞尔曲线，包含控制点 (x1,y1) 和终点 (x2,y2)。
		}
		else if (name == 'curve')
		{
			canvas.curveTo(x0 + Number(node.getAttribute('x1')) * sx,
					y0 + Number(node.getAttribute('y1')) * sy,
					x0 + Number(node.getAttribute('x2')) * sx,
					y0 + Number(node.getAttribute('y2')) * sy,
					x0 + Number(node.getAttribute('x3')) * sx,
					y0 + Number(node.getAttribute('y3')) * sy);
            // 中文注释：绘制三次贝塞尔曲线，包含两个控制点 (x1,y1)、(x2,y2) 和终点 (x3,y3)。
		}
		else if (name == 'arc')
		{
			canvas.arcTo(Number(node.getAttribute('rx')) * sx,
					Number(node.getAttribute('ry')) * sy,
					Number(node.getAttribute('x-axis-rotation')),
					Number(node.getAttribute('large-arc-flag')),
					Number(node.getAttribute('sweep-flag')),
					x0 + Number(node.getAttribute('x')) * sx,
					y0 + Number(node.getAttribute('y')) * sy);
            // 中文注释：绘制 SVG 风格的圆弧，参数包括半径、旋转角度、大弧标志、顺时针标志和终点坐标。
		}
		else if (name == 'rect')
		{
			canvas.rect(x0 + Number(node.getAttribute('x')) * sx,
					y0 + Number(node.getAttribute('y')) * sy,
					Number(node.getAttribute('w')) * sx,
					Number(node.getAttribute('h')) * sy);
            // 中文注释：绘制矩形，指定位置 (x,y) 和尺寸 (w,h)，需缩放。
		}
		else if (name == 'roundrect')
		{
			var arcsize = Number(node.getAttribute('arcsize'));
            // 中文注释：获取圆角矩形的圆角大小。

			if (arcsize == 0)
			{
				arcsize = mxConstants.RECTANGLE_ROUNDING_FACTOR * 100;
                // 中文注释：若未指定圆角大小，使用默认值（mxConstants.RECTANGLE_ROUNDING_FACTOR * 100）。
			}
			
			var w = Number(node.getAttribute('w')) * sx;
			var h = Number(node.getAttribute('h')) * sy;
            // 中文注释：获取缩放后的矩形宽度和高度。
			var factor = Number(arcsize) / 100;
            // 中文注释：将圆角大小转换为比例因子。
			var r = Math.min(w * factor, h * factor);
            // 中文注释：计算圆角半径，取宽度和高度的最小值。

			canvas.roundrect(x0 + Number(node.getAttribute('x')) * sx,
					y0 + Number(node.getAttribute('y')) * sy,
					w, h, r, r);
            // 中文注释：绘制圆角矩形，指定位置、尺寸和圆角半径。
		}
		else if (name == 'ellipse')
		{
			canvas.ellipse(x0 + Number(node.getAttribute('x')) * sx,
				y0 + Number(node.getAttribute('y')) * sy,
				Number(node.getAttribute('w')) * sx,
				Number(node.getAttribute('h')) * sy);
            // 中文注释：绘制椭圆，指定位置 (x,y) 和尺寸 (w,h)。
		}
		else if (name == 'image')
		{
			if (!shape.outline)
			{
				var src = this.evaluateAttribute(node, 'src', shape);
                // 中文注释：获取图像的 src 属性，可能为 URL 或数据 URI。

				canvas.image(x0 + Number(node.getAttribute('x')) * sx,
					y0 + Number(node.getAttribute('y')) * sy,
					Number(node.getAttribute('w')) * sx,
					Number(node.getAttribute('h')) * sy,
					src, false, node.getAttribute('flipH') == '1',
					node.getAttribute('flipV') == '1');
                // 中文注释：绘制图像，指定位置、尺寸、源地址及水平/垂直翻转。
                // 注意事项：仅在非 outline 模式下绘制图像。
			}
		}
		else if (name == 'text')
		{
			if (!shape.outline)
			{
				var str = this.evaluateTextAttribute(node, 'str', shape);
                // 中文注释：获取文本内容，可能经过本地化处理。
				var rotation = node.getAttribute('vertical') == '1' ? -90 : 0;
                // 中文注释：若 vertical 为 1，文本旋转 -90 度。

				if (node.getAttribute('align-shape') == '0')
				{
					var dr = shape.rotation;
                    // 中文注释：获取形状的旋转角度。

					// Depends on flipping
					var flipH = mxUtils.getValue(shape.style, mxConstants.STYLE_FLIPH, 0) == 1;
					var flipV = mxUtils.getValue(shape.style, mxConstants.STYLE_FLIPV, 0) == 1;
                    // 中文注释：检查水平和垂直翻转设置。

					if (flipH && flipV)
					{
						rotation -= dr;
                        // 中文注释：若同时翻转，减去形状旋转角度。
					}
					else if (flipH || flipV)
					{
						rotation += dr;
                        // 中文注释：若单一翻转，增加形状旋转角度。
					}
					else
					{
						rotation -= dr;
                        // 中文注释：若无翻转，减去形状旋转角度。
					}
				}
		
				rotation -= node.getAttribute('rotation');
                // 中文注释：减去文本自身的旋转角度。

				canvas.text(x0 + Number(node.getAttribute('x')) * sx,
						y0 + Number(node.getAttribute('y')) * sy,
						0, 0, str, node.getAttribute('align') || 'left',
						node.getAttribute('valign') || 'top', false, '',
						null, false, rotation);
                // 中文注释：绘制文本，指定位置、内容、对齐方式和旋转角度。
                // 注意事项：仅在非 outline 模式下绘制文本。
			}
		}
		else if (name == 'include-shape')
		{
			var stencil = mxStencilRegistry.getStencil(node.getAttribute('name'));
            // 中文注释：根据 name 获取子模板。

			if (stencil != null)
			{
				var x = x0 + Number(node.getAttribute('x')) * sx;
				var y = y0 + Number(node.getAttribute('y')) * sy;
				var w = Number(node.getAttribute('w')) * sx;
				var h = Number(node.getAttribute('h')) * sy;
                // 中文注释：计算子模板的缩放后位置和尺寸。

				stencil.drawShape(canvas, shape, x, y, w, h);
                // 中文注释：递归调用 drawShape 绘制子模板。
			}
		}
		else if (name == 'fillstroke')
		{
			canvas.fillAndStroke();
            // 中文注释：执行填充和描边操作。
		}
		else if (name == 'fill')
		{
			canvas.fill();
            // 中文注释：执行填充操作。
		}
		else if (name == 'stroke')
		{
			canvas.stroke();
            // 中文注释：执行描边操作。
		}
		else if (name == 'strokewidth')
		{
			var s = (node.getAttribute('fixed') == '1') ? 1 : minScale;
            // 中文注释：若 fixed 为 1，使用原始宽度，否则按最小缩放比例调整。
			canvas.setStrokeWidth(Number(node.getAttribute('width')) * s);
            // 中文注释：设置画布的线条宽度。
		}
		else if (name == 'dashed')
		{
			canvas.setDashed(node.getAttribute('dashed') == '1');
            // 中文注释：设置是否启用虚线描边。
		}
		else if (name == 'dashpattern')
		{
			var value = node.getAttribute('pattern');
            // 中文注释：获取虚线模式（空格分隔的开关长度序列）。

			if (value != null)
			{
				var tmp = value.split(' ');
				var pat = [];
                // 中文注释：将虚线模式字符串拆分为数组。

				for (var i = 0; i < tmp.length; i++)
				{
					if (tmp[i].length > 0)
					{
						pat.push(Number(tmp[i]) * minScale);
                        // 中文注释：将虚线长度缩放后添加到模式数组。
					}
				}
				
				value = pat.join(' ');
				canvas.setDashPattern(value);
                // 中文注释：设置画布的虚线模式。
			}
		}
		else if (name == 'strokecolor')
		{
			canvas.setStrokeColor(node.getAttribute('color'));
            // 中文注释：设置描边颜色。
		}
		else if (name == 'linecap')
		{
			canvas.setLineCap(node.getAttribute('cap'));
            // 中文注释：设置线条端点样式（flat/round/square）。
		}
		else if (name == 'linejoin')
		{
			canvas.setLineJoin(node.getAttribute('join'));
            // 中文注释：设置线条连接样式（miter/round/bevel）。
		}
		else if (name == 'miterlimit')
		{
			canvas.setMiterLimit(Number(node.getAttribute('limit')));
            // 中文注释：设置斜接限制，用于控制尖角长度。
		}
		else if (name == 'fillcolor')
		{
			canvas.setFillColor(node.getAttribute('color'));
            // 中文注释：设置填充颜色。
		}
		else if (name == 'alpha')
		{
			canvas.setAlpha(node.getAttribute('alpha'));
            // 中文注释：设置全局透明度（0.0 到 1.0）。
		}
		else if (name == 'fillalpha')
		{
			canvas.setAlpha(node.getAttribute('alpha'));
            // 中文注释：设置填充透明度（0.0 到 1.0）。
		}
		else if (name == 'strokealpha')
		{
			canvas.setAlpha(node.getAttribute('alpha'));
            // 中文注释：设置描边透明度（0.0 到 1.0）。
		}
		else if (name == 'fontcolor')
		{
			canvas.setFontColor(node.getAttribute('color'));
            // 中文注释：设置字体颜色。
		}
		else if (name == 'fontstyle')
		{
			canvas.setFontStyle(node.getAttribute('style'));
            // 中文注释：设置字体样式（粗体/斜体/下划线）。
		}
		else if (name == 'fontfamily')
		{
			canvas.setFontFamily(node.getAttribute('family'));
            // 中文注释：设置字体族。
		}
		else if (name == 'fontsize')
		{
			canvas.setFontSize(Number(node.getAttribute('size')) * minScale);
            // 中文注释：设置字体大小（按最小缩放比例调整）。
		}
		
		if (disableShadow && (name == 'fillstroke' || name == 'fill' || name == 'stroke'))
		{
			disableShadow = false;
			canvas.setShadow(false);
            // 中文注释：若执行填充或描边操作且禁用阴影，恢复阴影设置。
		}
	}
};
