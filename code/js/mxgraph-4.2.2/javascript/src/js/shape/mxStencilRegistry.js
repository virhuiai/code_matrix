/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 * 
 * Code to add stencils.
 * 
 * (code)
 * var req = mxUtils.load('test/stencils.xml');
 * var root = req.getDocumentElement();
 * var shape = root.firstChild;
 * 
 * while (shape != null)
 * {
 * 	 if (shape.nodeType == mxConstants.NODETYPE_ELEMENT)
 *   {
 *     mxStencilRegistry.addStencil(shape.getAttribute('name'), new mxStencil(shape));
 *   }
 *   
 *   shape = shape.nextSibling;
 * }
 * (end)
 */
// 中文注释：
// 加载并解析 stencils.xml 文件，用于添加模板（stencil）。
// 1. 使用 mxUtils.load 方法加载 XML 文件，获取其文档元素。
// 2. 遍历 XML 文件中的所有子节点，检查节点类型是否为元素节点。
// 3. 如果是元素节点，则从节点获取名称属性，并将其与对应的 mxStencil 实例添加到 mxStencilRegistry 中。
// 4. 继续处理下一个兄弟节点，直到所有节点处理完毕。
// 关键变量说明：
// - req: 存储加载的 XML 文件请求对象。
// - root: XML 文档的根元素。
// - shape: 当前处理的 XML 节点。
// 特殊处理注意事项：
// - 仅处理节点类型为 mxConstants.NODETYPE_ELEMENT 的节点，忽略其他类型的节点（如文本节点）。
var mxStencilRegistry =
{
	/**
	 * Class: mxStencilRegistry
	 * 
	 * A singleton class that provides a registry for stencils and the methods
	 * for painting those stencils onto a canvas or into a DOM.
	 */
    // 中文注释：
    // mxStencilRegistry 是一个单例类，用于存储和管理模板（stencil）的注册表。
    // 功能：提供方法将模板绘制到画布或 DOM 结构中。
    // 关键变量说明：
    // - stencils: 一个对象，用于存储模板名称与 mxStencil 实例的键值对。
	stencils: {},
	
	/**
	 * Function: addStencil
	 * 
	 * Adds the given <mxStencil>.
	 */
    // 中文注释：
    // 方法：addStencil
    // 功能：将指定的 mxStencil 实例添加到模板注册表中。
    // 参数说明：
    // - name: 模板的名称（字符串），用作注册表中的键。
    // - stencil: mxStencil 实例，包含模板的具体定义。
    // 方法目的：通过名称注册模板，以便后续通过名称查找和使用。
	addStencil: function(name, stencil)
	{
		mxStencilRegistry.stencils[name] = stencil;
	},
	
	/**
	 * Function: getStencil
	 * 
	 * Returns the <mxStencil> for the given name.
	 */
    // 中文注释：
    // 方法：getStencil
    // 功能：根据模板名称从注册表中获取对应的 mxStencil 实例。
    // 参数说明：
    // - name: 要查找的模板名称（字符串）。
    // 返回值：对应的 mxStencil 实例，若不存在则返回 undefined。
    // 方法目的：提供通过名称快速访问已注册模板的功能。
	getStencil: function(name)
	{
		return mxStencilRegistry.stencils[name];
	}

};
