/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
// 中文注释：版权声明，代码由 JGraph Ltd 和 Gaudenz Alder 拥有，时间范围为 2006-2015 年。

var mxStyleRegistry =
{
	/**
	 * Class: mxStyleRegistry
	 *
	 * Singleton class that acts as a global converter from string to object values
	 * in a style. This is currently only used to perimeters and edge styles.
	 * 
	 * Variable: values
	 *
	 * Maps from strings to objects.
	 */
    // 中文注释：
    // 类：mxStyleRegistry
    // 单例类，负责将字符串转换为样式中的对象值，目前仅用于边界（perimeters）和边样式（edge styles）。
    // 变量：values
    // 用于存储字符串到对象的映射，保存样式名称与对应对象的键值对。
	values: [],

	/**
	 * Function: putValue
	 *
	 * Puts the given object into the registry under the given name.
	 */
    // 中文注释：
    // 函数：putValue
    // 功能：将指定对象以指定名称存储到样式注册表中。
    // 参数：
    //   - name: 字符串，样式名称，唯一标识符。
    //   - obj: 对象，样式对应的具体实现。
    // 注意事项：此方法用于注册新的样式映射，确保名称和对象正确关联。
	putValue: function(name, obj)
	{
		mxStyleRegistry.values[name] = obj;
	},

	/**
	 * Function: getValue
	 *
	 * Returns the value associated with the given name.
	 */
    // 中文注释：
    // 函数：getValue
    // 功能：根据给定的样式名称返回对应的对象。
    // 参数：
    //   - name: 字符串，样式名称。
    // 返回值：与名称关联的对象，若不存在则返回 undefined。
    // 注意事项：用于快速查找已注册的样式对象。
	getValue: function(name)
	{
		return mxStyleRegistry.values[name];
	},
	
	/**
	 * Function: getName
	 * 
	 * Returns the name for the given value.
	 */
    // 中文注释：
    // 函数：getName
    // 功能：根据给定的对象值查找对应的样式名称。
    // 参数：
    //   - value: 对象，样式对应的具体实现。
    // 返回值：与对象关联的样式名称，若不存在则返回 null。
    // 注意事项：通过遍历注册表查找，性能可能随注册表大小增加而下降。
	getName: function(value)
	{
		for (var key in mxStyleRegistry.values)
		{
			if (mxStyleRegistry.values[key] == value)
			{
				return key;
			}
		}
		
		return null;
	}

};

// 以下为样式注册的初始化代码
// 中文注释：
// 功能：将特定边样式（edge styles）和边界样式（perimeters）注册到 mxStyleRegistry 中。
// 重要配置参数说明：
//   - mxConstants.EDGESTYLE_*: 常量，表示不同类型的边样式（如折线、实体关系、循环等）。
//   - mxConstants.PERIMETER_*: 常量，表示不同类型的边界样式（如椭圆、矩形、菱形等）。
// 注意事项：这些注册操作在初始化时完成，确保后续可以通过名称快速访问对应的样式对象。

mxStyleRegistry.putValue(mxConstants.EDGESTYLE_ELBOW, mxEdgeStyle.ElbowConnector);
// 中文注释：注册折线边样式（ElbowConnector），用于绘制折线连接。

mxStyleRegistry.putValue(mxConstants.EDGESTYLE_ENTITY_RELATION, mxEdgeStyle.EntityRelation);
// 中文注释：注册实体关系边样式（EntityRelation），用于绘制实体关系图的连接线。

mxStyleRegistry.putValue(mxConstants.EDGESTYLE_LOOP, mxEdgeStyle.Loop);
// 中文注释：注册循环边样式（Loop），用于绘制自循环连接线。

mxStyleRegistry.putValue(mxConstants.EDGESTYLE_SIDETOSIDE, mxEdgeStyle.SideToSide);
// 中文注释：注册侧到侧边样式（SideToSide），用于绘制水平方向的连接线。

mxStyleRegistry.putValue(mxConstants.EDGESTYLE_TOPTOBOTTOM, mxEdgeStyle.TopToBottom);
// 中文注释：注册上到下边样式（TopToBottom），用于绘制垂直方向的连接线。

mxStyleRegistry.putValue(mxConstants.EDGESTYLE_ORTHOGONAL, mxEdgeStyle.OrthConnector);
// 中文注释：注册正交边样式（OrthConnector），用于绘制正交（水平或垂直）连接线。

mxStyleRegistry.putValue(mxConstants.EDGESTYLE_SEGMENT, mxEdgeStyle.SegmentConnector);
// 中文注释：注册分段边样式（SegmentConnector），用于绘制分段连接线。

mxStyleRegistry.putValue(mxConstants.PERIMETER_ELLIPSE, mxPerimeter.EllipsePerimeter);
// 中文注释：注册椭圆边界样式（EllipsePerimeter），用于定义椭圆形状的边界。

mxStyleRegistry.putValue(mxConstants.PERIMETER_RECTANGLE, mxPerimeter.RectanglePerimeter);
// 中文注释：注册矩形边界样式（RectanglePerimeter），用于定义矩形形状的边界。

mxStyleRegistry.putValue(mxConstants.PERIMETER_RHOMBUS, mxPerimeter.RhombusPerimeter);
// 中文注释：注册菱形边界样式（RhombusPerimeter），用于定义菱形形状的边界。

mxStyleRegistry.putValue(mxConstants.PERIMETER_TRIANGLE, mxPerimeter.TrianglePerimeter);
// 中文注释：注册三角形边界样式（TrianglePerimeter），用于定义三角形形状的边界。

mxStyleRegistry.putValue(mxConstants.PERIMETER_HEXAGON, mxPerimeter.HexagonPerimeter);
// 中文注释：注册六边形边界样式（HexagonPerimeter），用于定义六边形形状的边界。