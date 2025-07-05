/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxImageShape
 *
 * Extends <mxShape> to implement an image shape. This shape is registered
 * under <mxConstants.SHAPE_IMAGE> in <mxCellRenderer>.
 * 
 * Constructor: mxImageShape
 * 
 * Constructs a new image shape.
 * 
 * Parameters:
 * 
 * bounds - <mxRectangle> that defines the bounds. This is stored in
 * <mxShape.bounds>.
 * image - String that specifies the URL of the image. This is stored in
 * <image>.
 * fill - String that defines the fill color. This is stored in <fill>.
 * stroke - String that defines the stroke color. This is stored in <stroke>.
 * strokewidth - Optional integer that defines the stroke width. Default is
 * 0. This is stored in <strokewidth>.
 */
// 中文注释：
// mxImageShape 类继承自 mxShape，用于实现图像形状。
// 该类在 mxCellRenderer 中以 mxConstants.SHAPE_IMAGE 注册。
// 构造函数 mxImageShape 用于创建一个新的图像形状。
// 参数说明：
// - bounds: mxRectangle 类型，定义形状的边界，存储在 mxShape.bounds 中。
// - image: 字符串，指定图像的 URL，存储在 image 属性中。
// - fill: 字符串，定义填充颜色，存储在 fill 属性中。
// - stroke: 字符串，定义描边颜色，存储在 stroke 属性中。
// - strokewidth: 可选整数，定义描边宽度，默认值为 0，存储在 strokewidth 属性中。
// 重要配置参数：
// - strokewidth 默认值为 1（如果未提供），控制描边粗细。
// - shadow 属性默认设置为 false，表示默认不显示阴影。
function mxImageShape(bounds, image, fill, stroke, strokewidth)
{
	mxShape.call(this);
	this.bounds = bounds;
	this.image = image;
	this.fill = fill;
	this.stroke = stroke;
	this.strokewidth = (strokewidth != null) ? strokewidth : 1;
	this.shadow = false;
};

/**
 * Extends mxShape.
 */
// 中文注释：
// 使用 mxUtils.extend 方法使 mxImageShape 继承 mxRectangleShape，扩展其功能。
mxUtils.extend(mxImageShape, mxRectangleShape);

/**
 * Variable: preserveImageAspect
 *
 * Switch to preserve image aspect. Default is true.
 */
// 中文注释：
// preserveImageAspect 变量控制是否保持图像的宽高比，默认值为 true。
// 用途：确保图像在渲染时不被拉伸变形，保持原始比例。
mxImageShape.prototype.preserveImageAspect = true;

/**
 * Function: getSvgScreenOffset
 * 
 * Disables offset in IE9 for crisper image output.
 */
// 中文注释：
// getSvgScreenOffset 方法用于在 IE9 中禁用偏移量，以获得更清晰的图像输出。
// 返回值：始终返回 0，表示无偏移。
// 特殊处理注意事项：在 IE9 中调整偏移以优化图像渲染效果。
mxImageShape.prototype.getSvgScreenOffset = function()
{
	return 0;
};

/**
 * Function: apply
 * 
 * Overrides <mxShape.apply> to replace the fill and stroke colors with the
 * respective values from <mxConstants.STYLE_IMAGE_BACKGROUND> and
 * <mxConstants.STYLE_IMAGE_BORDER>.
 * 
 * Applies the style of the given <mxCellState> to the shape. This
 * implementation assigns the following styles to local fields:
 * 
 * - <mxConstants.STYLE_IMAGE_BACKGROUND> => fill
 * - <mxConstants.STYLE_IMAGE_BORDER> => stroke
 *
 * Parameters:
 *
 * state - <mxCellState> of the corresponding cell.
 */
// 中文注释：
// apply 方法重写 mxShape 的 apply 方法，用于将指定 mxCellState 的样式应用到形状。
// 关键步骤：
// 1. 调用父类的 apply 方法，应用基础样式。
// 2. 将 fill、stroke 和 gradient 属性置为 null，防止继承不必要的样式。
// 3. 根据 style 对象中的配置，设置 preserveImageAspect、flipH 和 flipV 属性。
// 参数说明：
// - state: mxCellState 类型，表示对应单元格的状态，包含样式信息。
// 重要配置参数：
// - mxConstants.STYLE_IMAGE_BACKGROUND：定义背景填充颜色，赋值给 fill。
// - mxConstants.STYLE_IMAGE_BORDER：定义边框颜色，赋值给 stroke。
// - mxConstants.STYLE_IMAGE_ASPECT：控制是否保持图像宽高比，默认值为 1（true）。
// 特殊处理注意事项：支持旧版 imageFlipH 和 imageFlipV 属性，用于图像水平/垂直翻转。
mxImageShape.prototype.apply = function(state)
{
	mxShape.prototype.apply.apply(this, arguments);
	
	this.fill = null;
	this.stroke = null;
	this.gradient = null;
	
	if (this.style != null)
	{
		this.preserveImageAspect = mxUtils.getNumber(this.style, mxConstants.STYLE_IMAGE_ASPECT, 1) == 1;
		
		// Legacy support for imageFlipH/V
		this.flipH = this.flipH || mxUtils.getValue(this.style, 'imageFlipH', 0) == 1;
		this.flipV = this.flipV || mxUtils.getValue(this.style, 'imageFlipV', 0) == 1;
	}
};

/**
 * Function: isHtmlAllowed
 * 
 * Returns true if HTML is allowed for this shape. This implementation always
 * returns false.
 */
// 中文注释：
// isHtmlAllowed 方法检查是否允许为此形状使用 HTML 渲染。
// 返回值：当 preserveImageAspect 为 false 时返回 true，否则返回 false。
// 用途：控制是否允许 HTML 渲染，通常用于非保持宽高比的场景。
mxImageShape.prototype.isHtmlAllowed = function()
{
	return !this.preserveImageAspect;
};

/**
 * Function: createHtml
 *
 * Creates and returns the HTML DOM node(s) to represent
 * this shape. This implementation falls back to <createVml>
 * so that the HTML creation is optional.
 */
// 中文注释：
// createHtml 方法创建并返回表示此形状的 HTML DOM 节点。
// 关键步骤：
// 1. 创建一个 div 元素，设置其 position 属性为 absolute。
// 2. 返回该 div 节点。
// 用途：为 HTML 渲染提供 DOM 结构，允许后续添加图像或其他样式。
// 特殊处理注意事项：如果需要 VML 渲染，会回退到 createVml 方法。
mxImageShape.prototype.createHtml = function()
{
	var node = document.createElement('div');
	node.style.position = 'absolute';

	return node;
};

/**
 * Function: isRoundable
 * 
 * Disables inherited roundable support.
 */
// 中文注释：
// isRoundable 方法禁用继承的圆角支持。
// 返回值：始终返回 false，表示此形状不支持圆角。
// 用途：确保图像形状不应用圆角效果，保持矩形边界。
mxImageShape.prototype.isRoundable = function(c, x, y, w, h)
{
	return false;
};

/**
 * Function: paintVertexShape
 * 
 * Generic background painting implementation.
 */
// 中文注释：
// paintVertexShape 方法实现通用的背景绘制逻辑。
// 参数说明：
// - c: 画布上下文，用于绘制形状。
// - x, y: 绘制起点的坐标。
// - w, h: 形状的宽度和高度。
// 关键步骤：
// 1. 如果存在图像（this.image 不为 null）：
//    - 获取 STYLE_IMAGE_BACKGROUND 和 STYLE_IMAGE_BORDER 样式值，分别作为填充和描边颜色。
//    - 如果有填充颜色，绘制填充矩形并描边（用于阴影渲染）。
//    - 调用 c.image 绘制图像，考虑宽高比和翻转设置。
//    - 如果有描边颜色，禁用阴影并绘制矩形边框。
// 2. 如果没有图像，调用 mxRectangleShape 的 paintBackground 方法绘制默认背景。
// 交互逻辑：通过 flipH 和 flipV 属性支持图像的水平和垂直翻转。
// 特殊处理注意事项：描边渲染需要考虑阴影效果，确保图像清晰。
mxImageShape.prototype.paintVertexShape = function(c, x, y, w, h)
{
	if (this.image != null)
	{
		var fill = mxUtils.getValue(this.style, mxConstants.STYLE_IMAGE_BACKGROUND, null);
		var stroke = mxUtils.getValue(this.style, mxConstants.STYLE_IMAGE_BORDER, null);
		
		if (fill != null)
		{
			// Stroke rendering required for shadow
			c.setFillColor(fill);
			c.setStrokeColor(stroke);
			c.rect(x, y, w, h);
			c.fillAndStroke();
		}

		// FlipH/V are implicit via mxShape.updateTransform
		c.image(x, y, w, h, this.image, this.preserveImageAspect, false, false);
		
		var stroke = mxUtils.getValue(this.style, mxConstants.STYLE_IMAGE_BORDER, null);
		
		if (stroke != null)
		{
			c.setShadow(false);
			c.setStrokeColor(stroke);
			c.rect(x, y, w, h);
			c.stroke();
		}
	}
	else
	{
		mxRectangleShape.prototype.paintBackground.apply(this, arguments);
	}
};

/**
 * Function: redraw
 * 
 * Overrides <mxShape.redraw> to preserve the aspect ratio of images.
 */
// 中文注释：
// redrawHtmlShape 方法重写 mxShape 的 redraw 方法，用于重新绘制 HTML 形状并保持图像宽高比。
// 关键步骤：
// 1. 设置节点的 left、top、width 和 height 样式，基于 bounds 属性。
// 2. 清空节点内容（innerHTML 置空）。
// 3. 如果存在图像：
//    - 获取 STYLE_IMAGE_BACKGROUND 和 STYLE_IMAGE_BORDER 样式值，设置背景和边框颜色。
//    - 根据浏览器（IE6 或 documentMode <= 8 且有旋转）决定使用 VML 或标准 img 元素。
//    - 设置图像的 border、position 和 src 属性。
//    - 应用透明度（opacity）和翻转（flipH/flipV）效果。
//    - 如果需要旋转，应用 CSS 变换或 VML 滤镜。
//    - 设置图像尺寸与节点一致。
//    - 将图像追加到节点。
// 4. 如果没有图像，设置透明背景图像。
// 样式设置说明：
// - 背景颜色和边框颜色通过 style 属性动态设置。
// - 透明度通过 filter 属性实现，值为 alpha(opacity=xxx)。
// - 翻转通过 VML 滤镜或 CSS 变换实现。
// 交互逻辑：支持图像的翻转和旋转，动态调整样式以匹配用户交互。
// 特殊处理注意事项：
// - 在 IE 中，某些角度可能导致图像顶部裁剪。
// - VML 用于兼容 IE6 或旧版浏览器。
// 方法目的：确保 HTML 渲染的图像形状与配置一致，保持宽高比和样式效果。
mxImageShape.prototype.redrawHtmlShape = function()
{
	this.node.style.left = Math.round(this.bounds.x) + 'px';
	this.node.style.top = Math.round(this.bounds.y) + 'px';
	this.node.style.width = Math.max(0, Math.round(this.bounds.width)) + 'px';
	this.node.style.height = Math.max(0, Math.round(this.bounds.height)) + 'px';
	this.node.innerHTML = '';

	if (this.image != null)
	{
		var fill = mxUtils.getValue(this.style, mxConstants.STYLE_IMAGE_BACKGROUND, '');
		var stroke = mxUtils.getValue(this.style, mxConstants.STYLE_IMAGE_BORDER, '');
		this.node.style.backgroundColor = fill;
		this.node.style.borderColor = stroke;
		
		// VML image supports PNG in IE6
		var useVml = mxClient.IS_IE6 || ((document.documentMode == null || document.documentMode <= 8) && this.rotation != 0);
		var img = document.createElement((useVml) ? mxClient.VML_PREFIX + ':image' : 'img');
		img.setAttribute('border', '0');
		img.style.position = 'absolute';
		img.src = this.image;

		var filter = (this.opacity < 100) ? 'alpha(opacity=' + this.opacity + ')' : '';
		this.node.style.filter = filter;
		
		if (this.flipH && this.flipV)
		{
			filter += 'progid:DXImageTransform.Microsoft.BasicImage(rotation=2)';
		}
		else if (this.flipH)
		{
			filter += 'progid:DXImageTransform.Microsoft.BasicImage(mirror=1)';
		}
		else if (this.flipV)
		{
			filter += 'progid:DXImageTransform.Microsoft.BasicImage(rotation=2, mirror=1)';
		}

		if (img.style.filter != filter)
		{
			img.style.filter = filter;
		}

		if (img.nodeName == 'image')
		{
			img.style.rotation = this.rotation;
		}
		else if (this.rotation != 0)
		{
			// LATER: Add flipV/H support
			mxUtils.setPrefixedStyle(img.style, 'transform', 'rotate(' + this.rotation + 'deg)');
		}
		else
		{
			mxUtils.setPrefixedStyle(img.style, 'transform', '');
		}

		// Known problem: IE clips top line of image for certain angles
		img.style.width = this.node.style.width;
		img.style.height = this.node.style.height;
		
		this.node.style.backgroundImage = '';
		this.node.appendChild(img);
	}
	else
	{
		this.setTransparentBackgroundImage(this.node);
	}
};
