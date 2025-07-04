/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxStylesheet
 *
 * Defines the appearance of the cells in a graph. See <putCellStyle> for an
 * example of creating a new cell style. It is recommended to use objects, not
 * arrays for holding cell styles. Existing styles can be cloned using
 * <mxUtils.clone> and turned into a string for debugging using
 * <mxUtils.toString>.
 *
 * Default Styles:
 *
 * The stylesheet contains two built-in styles, which are used if no style is
 * defined for a cell:
 *
 *   defaultVertex - Default style for vertices
 *   defaultEdge - Default style for edges
 *
 * Example:
 *
 * (code)
 * var vertexStyle = stylesheet.getDefaultVertexStyle();
 * vertexStyle[mxConstants.STYLE_ROUNDED] = true;
 * var edgeStyle = stylesheet.getDefaultEdgeStyle();
 * edgeStyle[mxConstants.STYLE_EDGE] = mxEdgeStyle.EntityRelation;
 * (end)
 *
 * Modifies the built-in default styles.
 *
 * To avoid the default style for a cell, add a leading semicolon
 * to the style definition, eg.
 *
 * (code)
 * ;shadow=1
 * (end)
 *
 * Removing keys:
 *
 * For removing a key in a cell style of the form [stylename;|key=value;] the
 * special value none can be used, eg. highlight;fillColor=none
 *
 * See also the helper methods in mxUtils to modify strings of this format,
 * namely <mxUtils.setStyle>, <mxUtils.indexOfStylename>,
 * <mxUtils.addStylename>, <mxUtils.removeStylename>,
 * <mxUtils.removeAllStylenames> and <mxUtils.setStyleFlag>.
 *
 * Constructor: mxStylesheet
 *
 * Constructs a new stylesheet and assigns default styles.
 */
 // 中文注释：
 // 类：mxStylesheet
 // 定义图表中单元格的外观样式。推荐使用对象而非数组存储样式。
 // 默认样式：
 // - defaultVertex：顶点的默认样式
 // - defaultEdge：边的默认样式
 // 示例代码展示了如何修改默认顶点和边的样式。
 // 若要避免使用默认样式，可在样式定义前加分号。
 // 删除样式键时，可使用特殊值 'none'。
 // 构造函数：初始化样式表并设置默认样式。
function mxStylesheet()
{
	this.styles = new Object();

	this.putDefaultVertexStyle(this.createDefaultVertexStyle());
	this.putDefaultEdgeStyle(this.createDefaultEdgeStyle());
};
// 中文注释：
// 构造函数逻辑：
// 1. 初始化 styles 属性为一个空对象，用于存储样式。
// 2. 调用 putDefaultVertexStyle 设置默认顶点样式。
// 3. 调用 putDefaultEdgeStyle 设置默认边样式。

/**
 * Function: styles
 *
 * Maps from names to cell styles. Each cell style is a map of key,
 * value pairs.
 */
 // 中文注释：
 // 属性：styles
 // 功能：存储样式名称到单元格样式的映射，每个样式是一个键值对对象。
mxStylesheet.prototype.styles;

/**
 * Function: createDefaultVertexStyle
 *
 * Creates and returns the default vertex style.
 */
 // 中文注释：
 // 方法：createDefaultVertexStyle
 // 功能：创建并返回默认的顶点样式。
 // 返回值：包含顶点样式键值对的对象。
mxStylesheet.prototype.createDefaultVertexStyle = function()
{
	var style = new Object();

	style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
	style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
	style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
	style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
	style[mxConstants.STYLE_FILLCOLOR] = '#C3D9FF';
	style[mxConstants.STYLE_STROKECOLOR] = '#6482B9';
	style[mxConstants.STYLE_FONTCOLOR] = '#774400';

	return style;
};
// 中文注释：
// 方法逻辑：
// 1. 创建一个空对象 style 用于存储顶点样式。
// 2. 设置样式属性：
//    - SHAPE：矩形形状
//    - PERIMETER：矩形边界
//    - VERTICAL_ALIGN：垂直居中
//    - ALIGN：水平居中
//    - FILLCOLOR：填充颜色为浅蓝色 (#C3D9FF)
//    - STROKECOLOR：边框颜色为深蓝色 (#6482B9)
//    - FONTCOLOR：字体颜色为深棕色 (#774400)
// 3. 返回配置好的样式对象。

/**
 * Function: createDefaultEdgeStyle
 *
 * Creates and returns the default edge style.
 */
 // 中文注释：
 // 方法：createDefaultEdgeStyle
 // 功能：创建并返回默认的边样式。
 // 返回值：包含边样式键值对的对象。
mxStylesheet.prototype.createDefaultEdgeStyle = function()
{
	var style = new Object();

	style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
	style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_CLASSIC;
	style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
	style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
	style[mxConstants.STYLE_STROKECOLOR] = '#6482B9';
	style[mxConstants.STYLE_FONTCOLOR] = '#446299';

	return style;
};
// 中文注释：
// 方法逻辑：
// 1. 创建一个空对象 style 用于存储边样式。
// 2. 设置样式属性：
//    - SHAPE：连接线形状
//    - ENDARROW：经典箭头样式
//    - VERTICAL_ALIGN：垂直居中
//    - ALIGN：水平居中
//    - STROKECOLOR：边框颜色为深蓝色 (#6482B9)
//    - FONTCOLOR：字体颜色为深蓝色 (#446299)
// 3. 返回配置好的样式对象。

/**
 * Function: putDefaultVertexStyle
 *
 * Sets the default style for vertices using defaultVertex as the
 * stylename.
 *
 * Parameters:
 * style - Key, value pairs that define the style.
 */
 // 中文注释：
 // 方法：putDefaultVertexStyle
 // 功能：设置顶点的默认样式，使用 'defaultVertex' 作为样式名称。
 // 参数：
 // - style：定义样式的键值对对象。
mxStylesheet.prototype.putDefaultVertexStyle = function(style)
{
	this.putCellStyle('defaultVertex', style);
};
// 中文注释：
// 方法逻辑：
// 1. 调用 putCellStyle 方法，将样式存储为 'defaultVertex'。

/**
 * Function: putDefaultEdgeStyle
 *
 * Sets the default style for edges using defaultEdge as the stylename.
 */
 // 中文注释：
 // 方法：putDefaultEdgeStyle
 // 功能：设置边的默认样式，使用 'defaultEdge' 作为样式名称。
 // 参数：
 // - style：定义样式的键值对对象。
mxStylesheet.prototype.putDefaultEdgeStyle = function(style)
{
	this.putCellStyle('defaultEdge', style);
};
// 中文注释：
// 方法逻辑：
// 1. 调用 putCellStyle 方法，将样式存储为 'defaultEdge'。

/**
 * Function: getDefaultVertexStyle
 *
 * Returns the default style for vertices.
 */
 // 中文注释：
 // 方法：getDefaultVertexStyle
 // 功能：返回顶点的默认样式。
 // 返回值：存储在 styles 中的 'defaultVertex' 样式对象。
mxStylesheet.prototype.getDefaultVertexStyle = function()
{
	return this.styles['defaultVertex'];
};
// 中文注释：
// 方法逻辑：
// 1. 从 styles 对象中返回 'defaultVertex' 对应的样式。

/**
 * Function: getDefaultEdgeStyle
 *
 * Sets the default style for edges.
 */
 // 中文注释：
 // 方法：getDefaultEdgeStyle
 // 功能：返回边的默认样式。
 // 返回值：存储在 styles 中的 'defaultEdge' 样式对象。
mxStylesheet.prototype.getDefaultEdgeStyle = function()
{
	return this.styles['defaultEdge'];
};
// 中文注释：
// 方法逻辑：
// 1. 从 styles 对象中返回 'defaultEdge' 对应的样式。

/**
 * Function: putCellStyle
 *
 * Stores the given map of key, value pairs under the given name in
 * <styles>.
 *
 * Example:
 *
 * The following example adds a new style called 'rounded' into an
 * existing stylesheet:
 *
 * (code)
 * var style = new Object();
 * style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
 * style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
 * style[mxConstants.STYLE_ROUNDED] = true;
 * graph.getStylesheet().putCellStyle('rounded', style);
 * (end)
 *
 * In the above example, the new style is an object. The possible keys of
 * the object are all the constants in <mxConstants> that start with STYLE
 * and the values are either JavaScript objects, such as
 * <mxPerimeter.RightAngleRectanglePerimeter> (which is in fact a function)
 * or expressions, such as true. Note that not all keys will be
 * interpreted by all shapes (eg. the line shape ignores the fill color).
 * The final call to this method associates the style with a name in the
 * stylesheet. The style is used in a cell with the following code:
 *
 * (code)
 * model.setStyle(cell, 'rounded');
 * (end)
 *
 * Parameters:
 *
 * name - Name for the style to be stored.
 * style - Key, value pairs that define the style.
 */
 // 中文注释：
 // 方法：putCellStyle
 // 功能：将指定的键值对样式存储在 styles 中，使用指定的名称。
 // 参数：
 // - name：样式名称。
 // - style：定义样式的键值对对象。
 // 示例：展示了如何添加一个名为 'rounded' 的新样式，包含矩形形状、边界和圆角属性。
 // 注意事项：并非所有键都适用于所有形状（如线条形状忽略填充颜色）。
mxStylesheet.prototype.putCellStyle = function(name, style)
{
	this.styles[name] = style;
};
// 中文注释：
// 方法逻辑：
// 1. 将 style 对象存储到 styles 中，键为 name。

/**
 * Function: getCellStyle
 *
 * Returns the cell style for the specified stylename or the given
 * defaultStyle if no style can be found for the given stylename.
 *
 * Parameters:
 *
 * name - String of the form [(stylename|key=value);] that represents the
 * style.
 * defaultStyle - Default style to be returned if no style can be found.
 */
 // 中文注释：
 // 方法：getCellStyle
 // 功能：根据指定的样式名称返回单元格样式，若未找到则返回默认样式。
 // 参数：
 // - name：样式字符串，格式为 [(stylename|key=value);]。
 // - defaultStyle：未找到样式时返回的默认样式。
 // 返回值：合并后的样式对象。
 // 注意事项：支持解析键值对和命名样式，特殊值 'none' 用于删除样式键。
mxStylesheet.prototype.getCellStyle = function(name, defaultStyle)
{
	var style = defaultStyle;

	if (name != null && name.length > 0)
	{
		var pairs = name.split(';');

		if (style != null &&
			name.charAt(0) != ';')
		{
			style = mxUtils.clone(style);
		}
		else
		{
			style = new Object();
		}

		// Parses each key, value pair into the existing style
	 	for (var i = 0; i < pairs.length; i++)
	 	{
	 		var tmp = pairs[i];
	 		var pos = tmp.indexOf('=');

	 		if (pos >= 0)
	 		{
		 		var key = tmp.substring(0, pos);
		 		var value = tmp.substring(pos + 1);

		 		if (value == mxConstants.NONE)
		 		{
		 			delete style[key];
		 		}
		 		else if (mxUtils.isNumeric(value))
		 		{
		 			style[key] = parseFloat(value);
		 		}
		 		else
		 		{
			 		style[key] = value;
		 		}
			}
	 		else
	 		{
	 			// Merges the entries from a named style
				var tmpStyle = this.styles[tmp];

				if (tmpStyle != null)
				{
					for (var key in tmpStyle)
					{
						style[key] = tmpStyle[key];
					}
				}
	 		}
		}
	}

	return style;
};
// 中文注释：
// 方法逻辑：
// 1. 初始化 style 为 defaultStyle。
// 2. 如果 name 非空，解析样式字符串，拆分为键值对数组。
// 3. 若 name 不以分号开头且 defaultStyle 非空，克隆 defaultStyle；否则创建新对象。
// 4. 遍历键值对：
//    - 若包含 '='，提取键和值。
//    - 若值为 'none'，删除对应键。
//    - 若值为数字，转换为浮点数存储。
//    - 否则直接存储值。
//    - 若无 '='，尝试从 styles 中获取命名样式并合并。
// 5. 返回最终的样式对象。
// 注意事项：
// - 样式字符串格式需遵循 [(stylename|key=value);]。
// - 克隆 defaultStyle 以避免修改原始样式。