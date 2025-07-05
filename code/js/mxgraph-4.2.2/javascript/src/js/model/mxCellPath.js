/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
var mxCellPath =
{

	/**
	 * Class: mxCellPath
	 * 
	 * Implements a mechanism for temporary cell Ids.
     */
    // 中文注释：mxCellPath 类
    // 实现了一个用于临时单元格 ID 的机制，用于生成、解析和比较单元格路径。

    /**
	 * Variable: PATH_SEPARATOR
	 * 
	 * Defines the separator between the path components. Default is ".".
	 */
    // 中文注释：路径分隔符
    // 定义路径组件之间的分隔符，默认为 "."。
	PATH_SEPARATOR: '.',
	
	/**
	 * Function: create
	 * 
	 * Creates the cell path for the given cell. The cell path is a
	 * concatenation of the indices of all ancestors on the (finite) path to
	 * the root, eg. "0.0.0.1".
	 * 
	 * Parameters:
	 * 
	 * cell - Cell whose path should be returned.
	 */
    // 中文注释：创建单元格路径
    // 为给定的单元格生成路径，路径是由从该单元格到根节点的有限路径上所有祖先节点索引的连接，
    // 例如 "0.0.0.1"。
    // 参数：
    // cell - 需要生成路径的单元格对象。
	create: function(cell)
	{
		var result = '';
		
		if (cell != null)
		{
			var parent = cell.getParent();
			
			while (parent != null)
			{
				var index = parent.getIndex(cell);
				result = index + mxCellPath.PATH_SEPARATOR + result;
				
				cell = parent;
				parent = cell.getParent();
			}
		}
		
		// Removes trailing separator
		var n = result.length;
		
		if (n > 1)
		{
			result = result.substring(0, n - 1);
		}
		
		return result;
        // 中文注释：返回生成的路径字符串
        // 如果路径长度大于1，则移除末尾的分隔符后返回最终路径。
	},
	
	/**
	 * Function: getParentPath
	 * 
	 * Returns the path for the parent of the cell represented by the given
	 * path. Returns null if the given path has no parent.
	 * 
	 * Parameters:
	 * 
	 * path - Path whose parent path should be returned.
	 */
    // 中文注释：获取父路径
    // 返回给定路径表示的单元格的父路径。如果给定路径没有父路径，则返回 null。
    // 参数：
    // path - 需要获取父路径的路径字符串。
	getParentPath: function(path)
	{
		if (path != null)
		{
			var index = path.lastIndexOf(mxCellPath.PATH_SEPARATOR);

			if (index >= 0)
			{
				return path.substring(0, index);
			}
			else if (path.length > 0)
			{
				return '';
			}
		}

		return null;
        // 中文注释：返回父路径或 null
        // 通过查找最后一个分隔符位置截取父路径，若路径为空或无分隔符，返回空字符串或 null。
	},

	/**
	 * Function: resolve
	 * 
	 * Returns the cell for the specified cell path using the given root as the
	 * root of the path.
	 * 
	 * Parameters:
	 * 
	 * root - Root cell of the path to be resolved.
	 * path - String that defines the path.
	 */
    // 中文注释：解析路径
    // 根据给定的根单元格和路径字符串，解析并返回对应的单元格对象。
    // 参数：
    // root - 路径解析的根单元格。
    // path - 定义路径的字符串。
	resolve: function(root, path)
	{
		var parent = root;
		
		if (path != null)
		{
			var tokens = path.split(mxCellPath.PATH_SEPARATOR);
			
			for (var i=0; i<tokens.length; i++)
			{
				parent = parent.getChildAt(parseInt(tokens[i]));
			}
		}
		
		return parent;
        // 中文注释：返回解析后的单元格
        // 通过路径中的索引逐级查找子节点，返回最终的单元格对象。
	},
	
	/**
	 * Function: compare
	 * 
	 * Compares the given cell paths and returns -1 if p1 is smaller, 0 if
	 * p1 is equal and 1 if p1 is greater than p2.
	 */
    // 中文注释：比较路径
    // 比较两个单元格路径，返回 -1（p1 小于 p2）、0（p1 等于 p2）或 1（p1 大于 p2）。
    // 参数：
    // p1 - 第一个路径字符串。
    // p2 - 第二个路径字符串。
	compare: function(p1, p2)
	{
		var min = Math.min(p1.length, p2.length);
		var comp = 0;
		
		for (var i = 0; i < min; i++)
		{
			if (p1[i] != p2[i])
			{
				if (p1[i].length == 0 ||
					p2[i].length == 0)
				{
					comp = (p1[i] == p2[i]) ? 0 : ((p1[i] > p2[i]) ? 1 : -1);
				}
				else
				{
					var t1 = parseInt(p1[i]);
					var t2 = parseInt(p2[i]);
					
					comp = (t1 == t2) ? 0 : ((t1 > t2) ? 1 : -1);
				}
				
				break;
			}
		}
		
		// Compares path length if both paths are equal to this point
		if (comp == 0)
		{
			var t1 = p1.length;
			var t2 = p2.length;
			
			if (t1 != t2)
			{
				comp = (t1 > t2) ? 1 : -1;
			}
		}
		
		return comp;
        // 中文注释：返回比较结果
        // 比较路径的每个索引值，若索引值不同则返回比较结果；若索引值相同，则比较路径长度。
        // 注意事项：路径中的索引值必须是有效的整数，空字符串或无效值可能导致比较异常。
	}

};
