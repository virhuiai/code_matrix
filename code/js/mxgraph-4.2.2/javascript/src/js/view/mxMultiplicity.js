/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxMultiplicity
 * 
 * Defines invalid connections along with the error messages that they produce.
 * To add or remove rules on a graph, you must add/remove instances of this
 * class to <mxGraph.multiplicities>.
 * 
 * Example:
 * 
 * (code)
 * graph.multiplicities.push(new mxMultiplicity(
 *   true, 'rectangle', null, null, 0, 2, ['circle'],
 *   'Only 2 targets allowed',
 *   'Only circle targets allowed'));
 * (end)
 * 
 * Defines a rule where each rectangle must be connected to no more than 2
 * circles and no other types of targets are allowed.
 * 
 * Constructor: mxMultiplicity
 * 
 * Instantiate class mxMultiplicity in order to describe allowed
 * connections in a graph. Not all constraints can be enforced while
 * editing, some must be checked at validation time. The <countError> and
 * <typeError> are treated as resource keys in <mxResources>.
 * 
 * Parameters:
 * 
 * source - Boolean indicating if this rule applies to the source or target
 * terminal.
 * type - Type of the source or target terminal that this rule applies to.
 * See <type> for more information.
 * attr - Optional attribute name to match the source or target terminal.
 * value - Optional attribute value to match the source or target terminal.
 * min - Minimum number of edges for this rule. Default is 1.
 * max - Maximum number of edges for this rule. n means infinite. Default
 * is n.
 * validNeighbors - Array of types of the opposite terminal for which this
 * rule applies.
 * countError - Error to be displayed for invalid number of edges.
 * typeError - Error to be displayed for invalid opposite terminals.
 * validNeighborsAllowed - Optional boolean indicating if the array of
 * opposite types should be valid or invalid.
 */
// 中文注释：
// mxMultiplicity 类用于定义图中无效连接的规则及其错误信息。
// 通过向 <mxGraph.multiplicities> 添加或移除该类的实例来管理图的连接规则。
// 示例代码展示了一个规则：每个矩形最多连接两个圆形目标，且不允许其他类型的目标。
// 构造函数用于创建描述图中允许连接的规则实例。
// 重要参数说明：
// - source: 布尔值，决定规则应用于边的起点还是终点。
// - type: 起点或终点的类型，用于匹配节点类型。
// - attr: 可选的属性名，用于进一步匹配节点。
// - value: 可选的属性值，与 attr 配合使用。
// - min: 规则适用的最小连接数，默认值为 1。
// - max: 规则适用的最大连接数，'n' 表示无限制，默认值为 'n'。
// - validNeighbors: 对端节点允许的类型数组。
// - countError: 当连接数不符合 min 或 max 时显示的错误信息。
// - typeError: 当对端节点类型不匹配时显示的错误信息。
// - validNeighborsAllowed: 布尔值，决定 validNeighbors 是允许的类型还是禁止的类型。
function mxMultiplicity(source, type, attr, value, min, max,
	validNeighbors, countError, typeError, validNeighborsAllowed)
{
	this.source = source;
	this.type = type;
	this.attr = attr;
	this.value = value;
	this.min = (min != null) ? min : 0;
	this.max = (max != null) ? max : 'n';
	this.validNeighbors = validNeighbors;
	this.countError = mxResources.get(countError) || countError;
	this.typeError = mxResources.get(typeError) || typeError;
	this.validNeighborsAllowed = (validNeighborsAllowed != null) ?
		validNeighborsAllowed : true;
    // 中文注释：
    // 初始化 mxMultiplicity 实例，设置规则的各项属性。
    // 关键变量用途：
    // - this.source: 存储规则是应用于起点还是终点。
    // - this.type: 存储适用的节点类型。
    // - this.attr: 存储匹配节点的属性名（可选）。
    // - this.value: 存储匹配节点的属性值（可选）。
    // - this.min: 设置最小连接数，默认值为 0。
    // - this.max: 设置最大连接数，默认值为 'n'（无限制）。
    // - this.validNeighbors: 存储对端节点允许或禁止的类型数组。
    // - this.countError: 存储连接数错误的提示信息，支持国际化。
    // - this.typeError: 存储节点类型错误的提示信息，支持国际化。
    // - this.validNeighborsAllowed: 决定 validNeighbors 是允许还是禁止的类型，默认 true（允许）。
    // 特殊处理：
    // - 如果 min 或 max 未提供，则使用默认值（min=0, max='n'）。
    // - countError 和 typeError 支持通过 mxResources 获取国际化资源。
};

/**
 * Variable: type
 * 
 * Defines the type of the source or target terminal. The type is a string
 * passed to <mxUtils.isNode> together with the source or target vertex
 * value as the first argument.
 */
// 中文注释：
// type 变量定义了规则适用的起点或终点节点的类型。
// 该类型是一个字符串，与源节点或目标节点的值一起传递给 <mxUtils.isNode> 方法，用于检查节点是否符合规则。
mxMultiplicity.prototype.type = null;

/**
 * Variable: attr
 * 
 * Optional string that specifies the attributename to be passed to
 * <mxUtils.isNode> to check if the rule applies to a cell.
 */
// 中文注释：
// attr 变量存储一个可选的属性名，用于进一步匹配节点。
// 该属性名会传递给 <mxUtils.isNode> 方法，以验证节点是否符合规则。
mxMultiplicity.prototype.attr = null;

/**
 * Variable: value
 * 
 * Optional string that specifies the value of the attribute to be passed
 * to <mxUtils.isNode> to check if the rule applies to a cell.
 */
// 中文注释：
// value 变量存储一个可选的属性值，与 attr 配合使用。
// 用于在 <mxUtils.isNode> 方法中验证节点的属性值是否匹配规则要求。
mxMultiplicity.prototype.value = null;

/**
 * Variable: source
 * 
 * Boolean that specifies if the rule is applied to the source or target
 * terminal of an edge.
 */
// 中文注释：
// source 变量是一个布尔值，指定规则是应用于边的起点还是终点。
// true 表示规则应用于起点，false 表示规则应用于终点。
mxMultiplicity.prototype.source = null;

/**
 * Variable: min
 * 
 * Defines the minimum number of connections for which this rule applies.
 * Default is 0.
 */
// 中文注释：
// min 变量定义了规则适用的最小连接数。
// 默认值为 0，表示至少需要 0 条连接（即允许无连接）。
mxMultiplicity.prototype.min = null;

/**
 * Variable: max
 * 
 * Defines the maximum number of connections for which this rule applies.
 * A value of 'n' means unlimited times. Default is 'n'. 
 */
// 中文注释：
// max 变量定义了规则适用的最大连接数。
// 值 'n' 表示无限制，允许任意数量的连接，默认值为 'n'。
mxMultiplicity.prototype.max = null;

/**
 * Variable: validNeighbors
 * 
 * Holds an array of strings that specify the type of neighbor for which
 * this rule applies. The strings are used in <mxCell.is> on the opposite
 * terminal to check if the rule applies to the connection.
 */
// 中文注释：
// validNeighbors 变量存储一个字符串数组，指定规则适用的对端节点类型。
// 这些类型字符串会在对端节点上调用 <mxCell.is> 方法，以检查连接是否符合规则。
mxMultiplicity.prototype.validNeighbors = null;

/**
 * Variable: validNeighborsAllowed
 * 
 * Boolean indicating if the list of validNeighbors are those that are allowed
 * for this rule or those that are not allowed for this rule.
 */
// 中文注释：
// validNeighborsAllowed 变量是一个布尔值，决定 validNeighbors 数组中的类型是允许的还是禁止的。
// 默认值为 true，表示 validNeighbors 中的类型是允许的类型。
mxMultiplicity.prototype.validNeighborsAllowed = true;

/**
 * Variable: countError
 * 
declarative
 *
 * Holds the localized error message to be displayed if the number of
 * connections for which the rule applies is smaller than <min> or greater
 * than <max>.
 */
// 中文注释：
// countError 变量存储当连接数小于 min 或大于 max 时显示的本地化错误信息。
// 该信息支持通过 <mxResources> 获取国际化资源。
mxMultiplicity.prototype.countError = null;

/**
 * Variable: typeError
 * 
 * Holds the localized error message to be displayed if the type of the
 * neighbor for a connection does not match the rule.
 */
// 中文注释：
// typeError 变量存储当对端节点类型不符合规则时显示的本地化错误信息。
// 该信息支持通过 <mxResources> 获取国际化资源。
mxMultiplicity.prototype.typeError = null;

/**
 * Function: check
 * 
 * Checks the multiplicity for the given arguments and returns the error
 * for the given connection or null if the multiplicity does not apply.
 *  
 * Parameters:
 * 
 * graph - Reference to the enclosing <mxGraph> instance.
 * edge - <mxCell> that represents the edge to validate.
 * source - <mxCell> that represents the source terminal.
 * target - <mxCell> that represents the target terminal.
 * sourceOut - Number of outgoing edges from the source terminal.
 * targetIn - Number of incoming edges for the target terminal.
 */
// 中文注释：
// check 方法用于检查给定参数的连接是否符合多重性规则。
// 方法目的：验证边的连接是否满足规则要求，若不满足则返回错误信息，若满足则返回 null。
// 参数说明：
// - graph: 包含此规则的 <mxGraph> 实例引用。
// - edge: 表示要验证的边，类型为 <mxCell>。
// - source: 表示边的起点，类型为 <mxCell>。
// - target: 表示边的终点，类型为 <mxCell>。
// - sourceOut: 起点节点的出边数量。
// - targetIn: 终点节点的入边数量。
// 交互逻辑：
// - 方法首先检查规则是否适用于起点或终点（通过 checkTerminal 方法）。
// - 如果连接数不符合 min 或 max 限制，添加 countError 错误信息。
// - 如果对端节点类型不符合 validNeighbors 要求，添加 typeError 错误信息。
// - 最终返回错误信息字符串（若有）或 null（表示无错误）。
// 特殊处理：
// - 仅在规则适用于起点或终点时执行检查。
// - 错误信息累加后，只有当错误字符串不为空时才返回。
mxMultiplicity.prototype.check = function(graph, edge, source, target, sourceOut, targetIn)
{
	var error = '';

	if ((this.source && this.checkTerminal(graph, source, edge)) ||
		(!this.source && this.checkTerminal(graph, target, edge)))
	{
		if (this.countError != null && 
			((this.source && (this.max == 0 || (sourceOut >= this.max))) ||
			(!this.source && (this.max == 0 || (targetIn >= this.max)))))
		{
			error += this.countError + '\n';
		}

		if (this.validNeighbors != null && this.typeError != null && this.validNeighbors.length > 0)
		{
			var isValid = this.checkNeighbors(graph, edge, source, target);

			if (!isValid)
			{
				error += this.typeError + '\n';
			}
		}
	}
	
	return (error.length > 0) ? error : null;
};

/**
 * Function: checkNeighbors
 * 
 * Checks if there are any valid neighbours in <validNeighbors>. This is only
 * called if <validNeighbors> is a non-empty array.
 */
// 中文注释：
// checkNeighbors 方法检查对端节点是否在 validNeighbors 数组中。
// 方法目的：验证连接的对端节点类型是否符合规则要求。
// 参数说明：
// - graph: 包含此规则的 <mxGraph> 实例引用。
// - edge: 表示要验证的边，类型为 <mxCell>。
// - source: 表示边的起点，类型为 <mxCell>。
// - target: 表示边的终点，类型为 <mxCell>。
// 交互逻辑：
// - 获取起点和终点的值（graph.model.getValue）。
// - 根据 source 属性，检查对端节点（起点或终点）的类型是否在 validNeighbors 中。
// - 使用 checkType 方法验证节点类型是否匹配 validNeighbors 中的某一项。
// - 根据 validNeighborsAllowed 的值，决定匹配结果是否有效。
// 返回值：布尔值，表示对端节点类型是否符合规则。
// 特殊处理：
// - 仅在 validNeighbors 非空时调用此方法。
// - 循环检查 validNeighbors 中的每个类型，直到找到匹配项或检查结束。
mxMultiplicity.prototype.checkNeighbors = function(graph, edge, source, target)
{
	var sourceValue = graph.model.getValue(source);
	var targetValue = graph.model.getValue(target);
	var isValid = !this.validNeighborsAllowed;
	var valid = this.validNeighbors;
	
	for (var j = 0; j < valid.length; j++)
	{
		if (this.source &&
			this.checkType(graph, targetValue, valid[j]))
		{
			isValid = this.validNeighborsAllowed;
			break;
		}
		else if (!this.source && 
			this.checkType(graph, sourceValue, valid[j]))
		{
			isValid = this.validNeighborsAllowed;
			break;
		}
	}
	
	return isValid;
};

/**
 * Function: checkTerminal
 * 
 * Checks the given terminal cell and returns true if this rule applies. The
 * given cell is the source or target of the given edge, depending on
 * <source>. This implementation uses <checkType> on the terminal's value.
 */
// 中文注释：
// checkTerminal 方法检查给定的终端节点是否符合规则要求。
// 方法目的：验证边的起点或终点是否满足规则定义的类型和属性要求。
// 参数说明：
// - graph: 包含此规则的 <mxGraph> 实例引用。
// - terminal: 表示要检查的终端节点（起点或终点），类型为 <mxCell>。
// - edge: 表示要验证的边，类型为 <mxCell>。
// 交互逻辑：
// - 获取终端节点的值（graph.model.getValue）。
// - 调用 checkType 方法，验证节点的值是否符合规则的 type、attr 和 value 要求。
// 返回值：布尔值，表示终端节点是否符合规则。
mxMultiplicity.prototype.checkTerminal = function(graph, terminal, edge)
{
	var value = graph.model.getValue(terminal);
	
	return this.checkType(graph, value, this.type, this.attr, this.value);
};

/**
 * Function: checkType
 * 
 * Checks the type of the given value.
 */
// 中文注释：
// checkType 方法用于检查给定值是否符合规则的类型和属性要求。
// 方法目的：验证节点的类型、属性名和属性值是否匹配规则定义。
// 参数说明：
// - graph: 包含此规则的 <mxGraph> 实例引用。
// - value: 要检查的节点值。
// - type: 规则定义的节点类型。
// - attr: 规则定义的属性名（可选）。
// - attrValue: 规则定义的属性值（可选）。
// 交互逻辑：
// - 如果值不为空，检查其类型是否匹配规则要求。
// - 如果值是 DOM 节点，调用 <mxUtils.isNode> 方法验证类型、属性名和属性值。
// - 如果值不是 DOM 节点，直接比较值是否等于 type。
// 返回值：布尔值，表示值是否符合规则要求。
// 特殊处理：
// - 如果值为 null，直接返回 false。
// - 区分 DOM 节点和普通值，分别使用不同验证逻辑。
mxMultiplicity.prototype.checkType = function(graph, value, type, attr, attrValue)
{
	if (value != null)
	{
		if (!isNaN(value.nodeType)) // Checks if value is a DOM node
		{
			return mxUtils.isNode(value, type, attr, attrValue);
		}
		else
		{
			return value == type;
		}
	}
	
	return false;
};
