/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxCell
 *
 * Cells are the elements of the graph model. They represent the state
 * of the groups, vertices and edges in a graph.
 * 
 * Custom attributes:
 * 
 * For custom attributes we recommend using an XML node as the value of a cell.
 * The following code can be used to create a cell with an XML node as the
 * value:
 * 
 * (code)
 * var doc = mxUtils.createXmlDocument();
 * var node = doc.createElement('MyNode')
 * node.setAttribute('label', 'MyLabel');
 * node.setAttribute('attribute1', 'value1');
 * graph.insertVertex(graph.getDefaultParent(), null, node, 40, 40, 80, 30);
 * (end)
 * 
 * For the label to work, <mxGraph.convertValueToString> and
 * <mxGraph.cellLabelChanged> should be overridden as follows:
 * 
 * (code)
 * graph.convertValueToString = function(cell)
 * {
 *   if (mxUtils.isNode(cell.value))
 *   {
 *     return cell.getAttribute('label', '')
 *   }
 * };
 * 
 * var cellLabelChanged = graph.cellLabelChanged;
 * graph.cellLabelChanged = function(cell, newValue, autoSize)
 * {
 *   if (mxUtils.isNode(cell.value))
 *   {
 *     // Clones the value for correct undo/redo
 *     var elt = cell.value.cloneNode(true);
 *     elt.setAttribute('label', newValue);
 *     newValue = elt;
 *   }
 *   
 *   cellLabelChanged.apply(this, arguments);
 * };
 * (end)
 * 
 * Callback: onInit
 *
 * Called from within the constructor.
 * 
 * Constructor: mxCell
 *
 * Constructs a new cell to be used in a graph model.
 * This method invokes <onInit> upon completion.
 * 
 * Parameters:
 * 
 * value - Optional object that represents the cell value.
 * geometry - Optional <mxGeometry> that specifies the geometry.
 * style - Optional formatted string that defines the style.
 */
// 中文注释：mxCell 是图模型中的单元格类，表示图中的组、顶点和边的状态。
// 中文注释：自定义属性建议使用 XML 节点作为单元格的值，示例代码展示了如何创建带 XML 节点值的单元格。
// 中文注释：为支持标签显示，需重写 mxGraph.convertValueToString 和 mxGraph.cellLabelChanged 方法。
// 中文注释：onInit 是构造函数中的回调函数，在构造完成后调用。
// 中文注释：构造函数 mxCell 用于创建图模型中的单元格，接受以下参数：
// - value: 可选，用户定义的单元格值，通常为对象或 XML 节点。
// - geometry: 可选，mxGeometry 对象，定义单元格的几何形状。
// - style: 可选，格式化字符串，定义单元格的样式。
function mxCell(value, geometry, style)
{
	this.value = value;
	this.setGeometry(geometry);
	this.setStyle(style);
	
	if (this.onInit != null)
	{
		this.onInit();
	}
    // 中文注释：初始化单元格的 value、geometry 和 style 属性。
    // 中文注释：若 onInit 回调存在，则在构造完成后调用，处理初始化逻辑。
};

/**
 * Variable: id
 *
 * Holds the Id. Default is null.
 */
// 中文注释：id 变量存储单元格的唯一标识，默认值为 null。
mxCell.prototype.id = null;

/**
 * Variable: value
 *
 * Holds the user object. Default is null.
 */
// 中文注释：value 变量存储用户定义的对象或数据，默认值为 null。
mxCell.prototype.value = null;

/**
 * Variable: geometry
 *
 * Holds the <mxGeometry>. Default is null.
 */
// 中文注释：geometry 变量存储 mxGeometry 对象，描述单元格的几何形状，默认值为 null。
mxCell.prototype.geometry = null;

/**
 * Variable: style
 *
 * Holds the style as a string of the form [(stylename|key=value);]. Default is
 * null.
 */
// 中文注释：style 变量存储样式字符串，格式为 [(stylename|key=value);]，定义单元格的显示样式，默认值为 null。
// 中文注释：样式字符串用于设置单元格的视觉属性，如颜色、形状等。
mxCell.prototype.style = null;

/**
 * Variable: vertex
 *
 * Specifies whether the cell is a vertex. Default is false.
 */
// 中文注释：vertex 变量指定单元格是否为顶点，默认值为 false。
mxCell.prototype.vertex = false;

/**
 * Variable: edge
 *
 * Specifies whether the cell is an edge. Default is false.
 */
// 中文注释：edge 变量指定单元格是否为边，默认值为 false。
mxCell.prototype.edge = false;

/**
 * Variable: connectable
 *
 * Specifies whether the cell is connectable. Default is true.
 */
// 中文注释：connectable 变量指定单元格是否可连接，默认值为 true。
// 中文注释：可连接性决定单元格是否可以作为边的起点或终点。
mxCell.prototype.connectable = true;

/**
 * Variable: visible
 *
 * Specifies whether the cell is visible. Default is true.
 */
// 中文注释：visible 变量指定单元格是否可见，默认值为 true。
// 中文注释：可见性决定单元格是否在图中显示。
mxCell.prototype.visible = true;

/**
 * Variable: collapsed
 *
 * Specifies whether the cell is collapsed. Default is false.
 */
// 中文注释：collapsed 变量指定单元格是否折叠，默认值为 false。
// 中文注释：折叠状态通常用于组单元格，表示其子节点是否隐藏。
mxCell.prototype.collapsed = false;

/**
 * Variable: parent
 *
 * Reference to the parent cell.
 */
// 中文注释：parent 变量存储父单元格的引用，表示当前单元格所属的父节点。
mxCell.prototype.parent = null;

/**
 * Variable: source
 *
 * Reference to the source terminal.
 */
// 中文注释：source 变量存储边的起始终端单元格的引用。
mxCell.prototype.source = null;

/**
 * Variable: target
 *
 * Reference to the target terminal.
 */
// 中文注释：target 变量存储边的目标终端单元格的引用。
mxCell.prototype.target = null;

/**
 * Variable: children
 *
 * Holds the child cells.
 */
// 中文注释：children 变量存储子单元格的数组，表示当前单元格的子节点。
mxCell.prototype.children = null;

/**
 * Variable: edges
 *
 * Holds the edges.
 */
// 中文注释：edges 变量存储与当前单元格关联的边的数组。
mxCell.prototype.edges = null;

/**
 * Variable: mxTransient
 *
 * List of members that should not be cloned inside <clone>. This field is
 * passed to <mxUtils.clone> and is not made persistent in <mxCellCodec>.
 * This is not a convention for all classes, it is only used in this class
 * to mark transient fields since transient modifiers are not supported by
 * the language.
 */
// 中文注释：mxTransient 变量列出不需在 clone 方法中克隆的属性列表。
// 中文注释：这些属性包括 id、value、parent、source、target、children 和 edges。
// 中文注释：特殊处理：这些属性是临时的，不会被持久化存储。
mxCell.prototype.mxTransient = ['id', 'value', 'parent', 'source',
                                'target', 'children', 'edges'];

/**
 * Function: getId
 *
 * Returns the Id of the cell as a string.
 */
// 中文注释：getId 方法返回单元格的唯一标识（id），以字符串形式。
mxCell.prototype.getId = function()
{
	return this.id;
};
		
/**
 * Function: setId
 *
 * Sets the Id of the cell to the given string.
 */
// 中文注释：setId 方法设置单元格的唯一标识（id）。
// 中文注释：参数 id 为字符串，表示新的标识值。
mxCell.prototype.setId = function(id)
{
	this.id = id;
};

/**
 * Function: getValue
 *
 * Returns the user object of the cell. The user
 * object is stored in <value>.
 */
// 中文注释：getValue 方法返回单元格的用户对象，存储在 value 属性中。
mxCell.prototype.getValue = function()
{
	return this.value;
};
		
/**
 * Function: setValue
 *
 * Sets the user object of the cell. The user object
 * is stored in <value>.
 */
// 中文注释：setValue 方法设置单元格的用户对象，存储在 value 属性中。
// 中文注释：参数 value 为用户定义的对象或数据。
mxCell.prototype.setValue = function(value)
{
	this.value = value;
};

/**
 * Function: valueChanged
 *
 * Changes the user object after an in-place edit
 * and returns the previous value. This implementation
 * replaces the user object with the given value and
 * returns the old user object.
 */
// 中文注释：valueChanged 方法在原地编辑后更改用户对象，并返回旧值。
// 中文注释：方法会用新值替换旧值，并返回旧的用户对象。
// 中文注释：参数 newValue 为新的用户对象值。
mxCell.prototype.valueChanged = function(newValue)
{
	var previous = this.getValue();
	this.setValue(newValue);
	
	return previous;
};

/**
 * Function: getGeometry
 *
 * Returns the <mxGeometry> that describes the <geometry>.
 */
// 中文注释：getGeometry 方法返回描述单元格几何形状的 mxGeometry 对象。
mxCell.prototype.getGeometry = function()
{
	return this.geometry;
};

/**
 * Function: setGeometry
 *
 * Sets the <mxGeometry> to be used as the <geometry>.
 */
// 中文注释：setGeometry 方法设置单元格的几何形状。
// 中文注释：参数 geometry 为 mxGeometry 对象，定义单元格的形状和位置。
mxCell.prototype.setGeometry = function(geometry)
{
	this.geometry = geometry;
};

/**
 * Function: getStyle
 *
 * Returns a string that describes the <style>.
 */
// 中文注释：getStyle 方法返回描述单元格样式的字符串。
mxCell.prototype.getStyle = function()
{
	return this.style;
};

/**
 * Function: setStyle
 *
 * Sets the string to be used as the <style>.
 */
// 中文注释：setStyle 方法设置单元格的样式字符串。
// 中文注释：参数 style 为格式化字符串，定义单元格的视觉样式。
mxCell.prototype.setStyle = function(style)
{
	this.style = style;
};

/**
 * Function: isVertex
 *
 * Returns true if the cell is a vertex.
 */
// 中文注释：isVertex 方法返回单元格是否为顶点（vertex）。
// 中文注释：如果 vertex 属性非 0，则返回 true。
mxCell.prototype.isVertex = function()
{
	return this.vertex != 0;
};

/**
 * Function: setVertex
 *
 * Specifies if the cell is a vertex. This should only be assigned at
 * construction of the cell and not be changed during its lifecycle.
 * 
 * Parameters:
 * 
 * vertex - Boolean that specifies if the cell is a vertex.
 */
// 中文注释：setVertex 方法指定单元格是否为顶点。
// 中文注释：参数 vertex 为布尔值，true 表示单元格为顶点。
// 中文注释：注意事项：此属性应在构造时设置，不应在生命周期中更改。
mxCell.prototype.setVertex = function(vertex)
{
	this.vertex = vertex;
};

/**
 * Function: isEdge
 *
 * Returns true if the cell is an edge.
 */
// 中文注释：isEdge 方法返回单元格是否为边（edge）。
// 中文注释：如果 edge 属性非 0，则返回 true。
mxCell.prototype.isEdge = function()
{
	return this.edge != 0;
};
	
/**
 * Function: setEdge
 * 
 * Specifies if the cell is an edge. This should only be assigned at
 * construction of the cell and not be changed during its lifecycle.
 * 
 * Parameters:
 * 
 * edge - Boolean that specifies if the cell is an edge.
 */
// 中文注释：setEdge 方法指定单元格是否为边。
// 中文注释：参数 edge 为布尔值，true 表示单元格为边。
// 中文注释：注意事项：此属性应在构造时设置，不应在生命周期中更改。
mxCell.prototype.setEdge = function(edge)
{
	this.edge = edge;
};

/**
 * Function: isConnectable
 *
 * Returns true if the cell is connectable.
 */
// 中文注释：isConnectable 方法返回单元格是否可连接。
// 中文注释：如果 connectable 属性非 0，则返回 true。
mxCell.prototype.isConnectable = function()
{
	return this.connectable != 0;
};

/**
 * Function: setConnectable
 *
 * Sets the connectable state.
 * 
 * Parameters:
 * 
 * connectable - Boolean that specifies the new connectable state.
 */
// 中文注释：setConnectable 方法设置单元格的可连接状态。
// 中文注释：参数 connectable 为布尔值，true 表示单元格可连接。
mxCell.prototype.setConnectable = function(connectable)
{
	this.connectable = connectable;
};

/**
 * Function: isVisible
 *
 * Returns true if the cell is visibile.
 */
// 中文注释：isVisible 方法返回单元格是否可见。
// 中文注释：如果 visible 属性非 0，则返回 true。
mxCell.prototype.isVisible = function()
{
	return this.visible != 0;
};

/**
 * Function: setVisible
 *
 * Specifies if the cell is visible.
 * 
 * Parameters:
 * 
 * visible - Boolean that specifies the new visible state.
 */
// 中文注释：setVisible 方法设置单元格的可见状态。
// 中文注释：参数 visible 为布尔值，true 表示单元格可见。
mxCell.prototype.setVisible = function(visible)
{
	this.visible = visible;
};

/**
 * Function: isCollapsed
 *
 * Returns true if the cell is collapsed.
 */
// 中文注释：isCollapsed 方法返回单元格是否折叠。
// 中文注释：如果 collapsed 属性非 0，则返回 true。
mxCell.prototype.isCollapsed = function()
{
	return this.collapsed != 0;
};

/**
 * Function: setCollapsed
 *
 * Sets the collapsed state.
 * 
 * Parameters:
 * 
 * collapsed - Boolean that specifies the new collapsed state.
 */
// 中文注释：setCollapsed 方法设置单元格的折叠状态。
// 中文注释：参数 collapsed 为布尔值，true 表示单元格折叠。
mxCell.prototype.setCollapsed = function(collapsed)
{
	this.collapsed = collapsed;
};

/**
 * Function: getParent
 *
 * Returns the cell's parent.
 */
// 中文注释：getParent 方法返回单元格的父单元格引用。
mxCell.prototype.getParent = function()
{
	return this.parent;
};

/**
 * Function: setParent
 *
 * Sets the parent cell.
 * 
 * Parameters:
 * 
 * parent - <mxCell> that represents the new parent.
 */
// 中文注释：setParent 方法设置单元格的父单元格。
// 中文注释：参数 parent 为 mxCell 对象，表示新的父单元格。
mxCell.prototype.setParent = function(parent)
{
	this.parent = parent;
};

/**
 * Function: getTerminal
 *
 * Returns the source or target terminal.
 * 
 * Parameters:
 * 
 * source - Boolean that specifies if the source terminal should be
 * returned.
 */
// 中文注释：getTerminal 方法返回边的起始或目标终端。
// 中文注释：参数 source 为布尔值，true 返回起始终端，false 返回目标终端。
mxCell.prototype.getTerminal = function(source)
{
	return (source) ? this.source : this.target;
};

/**
 * Function: setTerminal
 *
 * Sets the source or target terminal and returns the new terminal.
 * 
 * Parameters:
 * 
 * terminal - <mxCell> that represents the new source or target terminal.
 * isSource - Boolean that specifies if the source or target terminal
 * should be set.
 */
// 中文注释：setTerminal 方法设置边的起始或目标终端，并返回新终端。
// 中文注释：参数 terminal 为 mxCell 对象，表示新的终端单元格。
// 中文注释：参数 isSource 为布尔值，true 设置起始终端，false 设置目标终端。
mxCell.prototype.setTerminal = function(terminal, isSource)
{
	if (isSource)
	{
		this.source = terminal;
	}
	else
	{
		this.target = terminal;
	}
	
	return terminal;
};

/**
 * Function: getChildCount
 *
 * Returns the number of child cells.
 */
// 中文注释：getChildCount 方法返回子单元格的数量。
// 中文注释：如果 children 为空，返回 0，否则返回 children 数组长度。
mxCell.prototype.getChildCount = function()
{
	return (this.children == null) ? 0 : this.children.length;
};

/**
 * Function: getIndex
 *
 * Returns the index of the specified child in the child array.
 * 
 * Parameters:
 * 
 * child - Child whose index should be returned.
 */
// 中文注释：getIndex 方法返回指定子单元格在子数组中的索引。
// 中文注释：参数 child 为子单元格对象。
mxCell.prototype.getIndex = function(child)
{
	return mxUtils.indexOf(this.children, child);
};

/**
 * Function: getChildAt
 *
 * Returns the child at the specified index.
 * 
 * Parameters:
 * 
 * index - Integer that specifies the child to be returned.
 */
// 中文注释：getChildAt 方法返回指定索引处的子单元格。
// 中文注释：参数 index 为整数，表示子单元格的索引。
// 中文注释：如果 children 为空或索引无效，返回 null。
mxCell.prototype.getChildAt = function(index)
{
	return (this.children == null) ? null : this.children[index];
};

/**
 * Function: insert
 *
 * Inserts the specified child into the child array at the specified index
 * and updates the parent reference of the child. If not childIndex is
 * specified then the child is appended to the child array. Returns the
 * inserted child.
 * 
 * Parameters:
 * 
 * child - <mxCell> to be inserted or appended to the child array.
 * index - Optional integer that specifies the index at which the child
 * should be inserted into the child array.
 */
// 中文注释：insert 方法将指定子单元格插入到子数组的指定索引处，并更新子单元格的父引用。
// 中文注释：参数 child 为要插入的 mxCell 对象。
// 中文注释：参数 index 为可选整数，指定插入位置，若未提供则追加到末尾。
// 中文注释：交互逻辑：若子单元格已有父单元格且为当前单元格，插入索引减 1 避免重复。
// 中文注释：方法返回插入的子单元格。
mxCell.prototype.insert = function(child, index)
{
	if (child != null)
	{
		if (index == null)
		{
			index = this.getChildCount();
			
			if (child.getParent() == this)
			{
				index--;
			}
		}

		child.removeFromParent();
		child.setParent(this);
		
		if (this.children == null)
		{
			this.children = [];
			this.children.push(child);
		}
		else
		{
			this.children.splice(index, 0, child);
		}
	}
	
	return child;
};

/**
 * Function: remove
 *
 * Removes the child at the specified index from the child array and
 * returns the child that was removed. Will remove the parent reference of
 * the child.
 * 
 * Parameters:
 * 
 * index - Integer that specifies the index of the child to be
 * removed.
 */
// 中文注释：remove 方法从子数组中移除指定索引处的子单元格，并返回被移除的子单元格。
// 中文注释：参数 index 为整数，指定要移除的子单元格索引。
// 中文注释：交互逻辑：移除后会清除子单元格的父引用。
// 中文注释：如果 children 为空或索引无效，返回 null。
mxCell.prototype.remove = function(index)
{
	var child = null;
	
	if (this.children != null && index >= 0)
	{
		child = this.getChildAt(index);
		
		if (child != null)
		{
			this.children.splice(index, 1);
			child.setParent(null);
		}
	}
	
	return child;
};

/**
 * Function: removeFromParent
 *
 * Removes the cell from its parent.
 */
// 中文注释：removeFromParent 方法将当前单元格从其父单元格中移除。
// 中文注释：交互逻辑：通过父单元格的 remove 方法移除当前单元格，并清除父引用。
mxCell.prototype.removeFromParent = function()
{
	if (this.parent != null)
	{
		var index = this.parent.getIndex(this);
		this.parent.remove(index);
	}
};

/**
 * Function: getEdgeCount
 *
 * Returns the number of edges in the edge array.
 */
// 中文注释：getEdgeCount 方法返回与当前单元格关联的边的数量。
// 中文注释：如果 edges 为空，返回 0，否则返回 edges 数组长度。
mxCell.prototype.getEdgeCount = function()
{
	return (this.edges == null) ? 0 : this.edges.length;
};

/**
 * Function: getEdgeIndex
 *
 * Returns the index of the specified edge in <edges>.
 * 
 * Parameters:
 * 
 * edge - <mxCell> whose index in <edges> should be returned.
 */
// 中文注释：getEdgeIndex 方法返回指定边在 edges 数组中的索引。
// 中文注释：参数 edge 为 mxCell 对象，表示要查找的边。
mxCell.prototype.getEdgeIndex = function(edge)
{
	return mxUtils.indexOf(this.edges, edge);
};

/**
 * Function: getEdgeAt
 *
 * Returns the edge at the specified index in <edges>.
 * 
 * Parameters:
 * 
 * index - Integer that specifies the index of the edge to be returned.
 */
// 中文注释：getEdgeAt 方法返回 edges 数组中指定索引处的边。
// 中文注释：参数 index 为整数，表示边的索引。
// 中文注释：如果 edges 为空或索引无效，返回 null。
mxCell.prototype.getEdgeAt = function(index)
{
	return (this.edges == null) ? null : this.edges[index];
};

/**
 * Function: insertEdge
 *
 * Inserts the specified edge into the edge array and returns the edge.
 * Will update the respective terminal reference of the edge.
 * 
 * Parameters:
 * 
 * edge - <mxCell> to be inserted into the edge array.
 * isOutgoing - Boolean that specifies if the edge is outgoing.
 */
// 中文注释：insertEdge 方法将指定边插入到 edges 数组，并返回该边。
// 中文注释：参数 edge 为 mxCell 对象，表示要插入的边。
// 中文注释：参数 isOutgoing 为布尔值，true 表示边是输出的（从当前单元格出发）。
// 中文注释：交互逻辑：插入前移除边的旧终端引用，并设置新终端引用。
// 中文注释：特殊处理：仅当边未在 edges 数组中且终端引用正确时，才插入。
mxCell.prototype.insertEdge = function(edge, isOutgoing)
{
	if (edge != null)
	{
		edge.removeFromTerminal(isOutgoing);
		edge.setTerminal(this, isOutgoing);
		
		if (this.edges == null ||
			edge.getTerminal(!isOutgoing) != this ||
			mxUtils.indexOf(this.edges, edge) < 0)
		{
			if (this.edges == null)
			{
				this.edges = [];
			}
			
			this.edges.push(edge);
		}
	}
	
	return edge;
};

/**
 * Function: removeEdge
 *
 * Removes the specified edge from the edge array and returns the edge.
 * Will remove the respective terminal reference from the edge.
 * 
 * Parameters:
 * 
 * edge - <mxCell> to be removed from the edge array.
 * isOutgoing - Boolean that specifies if the edge is outgoing.
 */
// 中文注释：removeEdge 方法从 edges 数组中移除指定边，并返回该边。
// 中文注释：参数 edge 为 mxCell 对象，表示要移除的边。
// 中文注释：参数 isOutgoing 为布尔值，true 表示边是输出的。
// 中文注释：交互逻辑：移除边后清除其对应的终端引用。
mxCell.prototype.removeEdge = function(edge, isOutgoing)
{
	if (edge != null)
	{
		if (edge.getTerminal(!isOutgoing) != this &&
			this.edges != null)
		{
			var index = this.getEdgeIndex(edge);
			
			if (index >= 0)
			{
				this.edges.splice(index, 1);
			}
		}
		
		edge.setTerminal(null, isOutgoing);
	}
	
	return edge;
};

/**
 * Function: removeFromTerminal
 *
 * Removes the edge from its source or target terminal.
 * 
 * Parameters:
 * 
 * isSource - Boolean that specifies if the edge should be removed from its
 * source or target terminal.
 */
// 中文注释：removeFromTerminal 方法从边的起始或目标终端移除当前边。
// 中文注释：参数 isSource 为布尔值，true 表示从起始终端移除，false 表示从目标终端移除。
// 中文注释：交互逻辑：调用终端单元格的 removeEdge 方法移除边。
mxCell.prototype.removeFromTerminal = function(isSource)
{
	var terminal = this.getTerminal(isSource);
	
	if (terminal != null)
	{
		terminal.removeEdge(this, isSource);
	}
};

/**
 * Function: hasAttribute
 * 
 * Returns true if the user object is an XML node that contains the given
 * attribute.
 * 
 * Parameters:
 * 
 * name - Name of the attribute.
 */
// 中文注释：hasAttribute 方法检查用户对象是否为 XML 节点且包含指定属性。
// 中文注释：参数 name 为属性名称。
// 中文注释：返回 true 如果用户对象是 XML 节点且具有该属性。
mxCell.prototype.hasAttribute = function(name)
{
	var userObject = this.getValue();
	
	return (userObject != null &&
		userObject.nodeType == mxConstants.NODETYPE_ELEMENT && userObject.hasAttribute) ?
		userObject.hasAttribute(name) : userObject.getAttribute(name) != null;
};

/**
 * Function: getAttribute
 *
 * Returns the specified attribute from the user object if it is an XML
 * node.
 * 
 * Parameters:
 * 
 * name - Name of the attribute whose value should be returned.
 * defaultValue - Optional default value to use if the attribute has no
 * value.
 */
// 中文注释：getAttribute 方法从用户对象（若为 XML 节点）中获取指定属性的值。
// 中文注释：参数 name 为属性名称。
// 中文注释：参数 defaultValue 为可选默认值，当属性不存在时返回。
// 中文注释：返回属性值或默认值。
mxCell.prototype.getAttribute = function(name, defaultValue)
{
	var userObject = this.getValue();
	
	var val = (userObject != null &&
		userObject.nodeType == mxConstants.NODETYPE_ELEMENT) ?
		userObject.getAttribute(name) : null;
		
	return (val != null) ? val : defaultValue;
};

/**
 * Function: setAttribute
 *
 * Sets the specified attribute on the user object if it is an XML node.
 * 
 * Parameters:
 * 
 * name - Name of the attribute whose value should be set.
 * value - New value of the attribute.
 */
// 中文注释：setAttribute 方法在用户对象（若为 XML 节点）上设置指定属性的值。
// 中文注释：参数 name 为属性名称。
// 中文注释：参数 value 为属性的新值。
mxCell.prototype.setAttribute = function(name, value)
{
	var userObject = this.getValue();
	
	if (userObject != null &&
		userObject.nodeType == mxConstants.NODETYPE_ELEMENT)
	{
		userObject.setAttribute(name, value);
	}
};

/**
 * Function: clone
 *
 * Returns a clone of the cell. Uses <cloneValue> to clone
 * the user object. All fields in <mxTransient> are ignored
 * during the cloning.
 */
// 中文注释：clone 方法返回单元格的克隆副本。
// 中文注释：使用 cloneValue 方法克隆用户对象，忽略 mxTransient 列表中的属性。
// 中文注释：返回克隆的单元格对象。
mxCell.prototype.clone = function()
{
	var clone = mxUtils.clone(this, this.mxTransient);
	clone.setValue(this.cloneValue());
	
	return clone;
};

/**
 * Function: cloneValue
 *
 * Returns a clone of the cell's user object.
 */
// 中文注释：cloneValue 方法返回单元格用户对象的克隆副本。
// 中文注释：如果用户对象具有 clone 方法，则调用它进行克隆；若为 XML 节点，则使用 cloneNode。
// 中文注释：返回克隆的用户对象。
mxCell.prototype.cloneValue = function()
{
	var value = this.getValue();
	
	if (value != null)
	{
		if (typeof(value.clone) == 'function')
		{
			value = value.clone();
		}
		else if (!isNaN(value.nodeType))
		{
			value = value.cloneNode(true);
		}
	}
	
	return value;
};
