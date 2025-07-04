/**
 * Copyright (c) 2006-2019, JGraph Ltd
 * Copyright (c) 2006-2017, draw.io AG
 */
/**
 * Class: mxPrintPreview
 * 
 * Implements printing of a diagram across multiple pages. The following opens
 * a print preview for an existing graph:
 * 
 * (code)
 * var preview = new mxPrintPreview(graph);
 * preview.open();
 * (end)
 * 
 * Use <mxUtils.getScaleForPageCount> as follows in order to print the graph
 * across a given number of pages:
 * 
 * (code)
 * var pageCount = mxUtils.prompt('Enter page count', '1');
 * 
 * if (pageCount != null)
 * {
 *   var scale = mxUtils.getScaleForPageCount(pageCount, graph);
 *   var preview = new mxPrintPreview(graph, scale);
 *   preview.open();
 * }
 * (end)
 * 
 * Additional pages:
 * 
 * To add additional pages before and after the output, <getCoverPages> and
 * <getAppendices> can be used, respectively.
 * 
 * (code)
 * var preview = new mxPrintPreview(graph, 1);
 * 
 * preview.getCoverPages = function(w, h)
 * {
 *   return [this.renderPage(w, h, 0, 0, mxUtils.bind(this, function(div)
 *   {
 *     div.innerHTML = '<div style="position:relative;margin:4px;">Cover Page</p>'
 *   }))];
 * };
 * 
 * preview.getAppendices = function(w, h)
 * {
 *   return [this.renderPage(w, h, 0, 0, mxUtils.bind(this, function(div)
 *   {
 *     div.innerHTML = '<div style="position:relative;margin:4px;">Appendix</p>'
 *   }))];
 * };
 * 
 * preview.open();
 * (end)
 * 
 * CSS:
 * 
 * The CSS from the original page is not carried over to the print preview.
 * To add CSS to the page, use the css argument in the <open> function or
 * override <writeHead> to add the respective link tags as follows:
 * 
 * (code)
 * var writeHead = preview.writeHead;
 * preview.writeHead = function(doc, css)
 * {
 *   writeHead.apply(this, arguments);
 *   doc.writeln('<link rel="stylesheet" type="text/css" href="style.css">');
 * };
 * (end)
 * 
 * Padding:
 * 
 * To add a padding to the page in the preview (but not the print output), use
 * the following code:
 * 
 * (code)
 * preview.writeHead = function(doc)
 * {
 *   writeHead.apply(this, arguments);
 *   
 *   doc.writeln('<style type="text/css">');
 *   doc.writeln('@media screen {');
 *   doc.writeln('  body > div { padding-top:30px;padding-left:40px;box-sizing:content-box; }');
 *   doc.writeln('}');
 *   doc.writeln('</style>');
 * };
 * (end)
 * 
 * Headers:
 * 
 * Apart from setting the title argument in the mxPrintPreview constructor you
 * can override <renderPage> as follows to add a header to any page:
 * 
 * (code)
 * var oldRenderPage = mxPrintPreview.prototype.renderPage;
 * mxPrintPreview.prototype.renderPage = function(w, h, x, y, content, pageNumber)
 * {
 *   var div = oldRenderPage.apply(this, arguments);
 *   
 *   var header = document.createElement('div');
 *   header.style.position = 'absolute';
 *   header.style.top = '0px';
 *   header.style.width = '100%';
 *   header.style.textAlign = 'right';
 *   mxUtils.write(header, 'Your header here');
 *   div.firstChild.appendChild(header);
 *   
 *   return div;
 * };
 * (end)
 * 
 * The pageNumber argument contains the number of the current page, starting at
 * 1. To display a header on the first page only, check pageNumber and add a
 * vertical offset in the constructor call for the height of the header.
 * 
 * Page Format:
 * 
 * For landscape printing, use <mxConstants.PAGE_FORMAT_A4_LANDSCAPE> as
 * the pageFormat in <mxUtils.getScaleForPageCount> and <mxPrintPreview>.
 * Keep in mind that one can not set the defaults for the print dialog
 * of the operating system from JavaScript so the user must manually choose
 * a page format that matches this setting.
 * 
 * You can try passing the following CSS directive to <open> to set the
 * page format in the print dialog to landscape. However, this CSS
 * directive seems to be ignored in most major browsers, including IE.
 * 
 * (code)
 * @page {
 *   size: landscape;
 * }
 * (end)
 * 
 * Note that the print preview behaves differently in IE when used from the
 * filesystem or via HTTP so printing should always be tested via HTTP.
 * 
 * If you are using a DOCTYPE in the source page you can override <getDoctype>
 * and provide the same DOCTYPE for the print preview if required. Here is
 * an example for IE8 standards mode.
 * 
 * (code)
 * var preview = new mxPrintPreview(graph);
 * preview.getDoctype = function()
 * {
 *   return '<!--[if IE]><meta http-equiv="X-UA-Compatible" content="IE=5,IE=8" ><![endif]-->';
 * };
 * preview.open();
 * (end)
 * 
 * Constructor: mxPrintPreview
 *
 * Constructs a new print preview for the given parameters.
 * 
 * Parameters:
 * 
 * graph - <mxGraph> to be previewed.
 * scale - Optional scale of the output. Default is 1 / <mxGraph.pageScale>.
 * pageFormat - <mxRectangle> that specifies the page format (in pixels).
 * border - Border in pixels along each side of every page. Note that the
 * actual print function in the browser will add another border for
 * printing.
 * This should match the page format of the printer. Default uses the
 * <mxGraph.pageFormat> of the given graph.
 * x0 - Optional left offset of the output. Default is 0.
 * y0 - Optional top offset of the output. Default is 0.
 * borderColor - Optional color of the page border. Default is no border.
 * Note that a border is sometimes useful to highlight the printed page
 * border in the print preview of the browser.
 * title - Optional string that is used for the window title. Default
 * is 'Printer-friendly version'.
 * pageSelector - Optional boolean that specifies if the page selector
 * should appear in the window with the print preview. Default is true.
 */
// 中文注释：
// 构造函数：mxPrintPreview
// 功能：创建一个用于预览图表打印的实例，支持多页打印。
// 参数说明：
// - graph: 要预览的 mxGraph 对象。
// - scale: 输出缩放比例，默认为 1 除以图表的页面缩放比例。
// - pageFormat: 定义页面格式的 mxRectangle 对象（以像素为单位），默认使用图表的页面格式。
// - border: 每页四周的边距（像素），需与打印机页面格式匹配，默认为 0。
// - x0: 输出内容的水平偏移量，默认为 0。
// - y0: 输出内容的垂直偏移量，默认为 0。
// - borderColor: 页面边框颜色，默认为无边框，可用于高亮显示打印预览边框。
// - title: 预览窗口标题，默认为“Printer-friendly version”。
// - pageSelector: 布尔值，控制是否显示页面选择器，默认为 true。
function mxPrintPreview(graph, scale, pageFormat, border, x0, y0, borderColor, title, pageSelector)
{
	this.graph = graph;
	this.scale = (scale != null) ? scale : 1 / graph.pageScale;
	this.border = (border != null) ? border : 0;
	this.pageFormat = mxRectangle.fromRectangle((pageFormat != null) ? pageFormat : graph.pageFormat);
	this.title = (title != null) ? title : 'Printer-friendly version';
	this.x0 = (x0 != null) ? x0 : 0;
	this.y0 = (y0 != null) ? y0 : 0;
	this.borderColor = borderColor;
	this.pageSelector = (pageSelector != null) ? pageSelector : true;
};

/**
 * Variable: graph
 * 
 * Reference to the <mxGraph> that should be previewed.
 */
// 中文注释：
// 变量：graph
// 用途：存储需要进行打印预览的 mxGraph 对象引用。
mxPrintPreview.prototype.graph = null;

/**
 * Variable: pageFormat
 *
 * Holds the <mxRectangle> that defines the page format.
 */
// 中文注释：
// 变量：pageFormat
// 用途：存储定义页面格式的 mxRectangle 对象，指定页面尺寸（以像素为单位）。
mxPrintPreview.prototype.pageFormat = null;

/**
 * Variable: scale
 * 
 * Holds the scale of the print preview.
 */
// 中文注释：
// 变量：scale
// 用途：存储打印预览的缩放比例，控制图表在页面上的显示大小。
mxPrintPreview.prototype.scale = null;

/**
 * Variable: border
 * 
 * The border inset around each side of every page in the preview. This is set
 * to 0 if autoOrigin is false.
 */
// 中文注释：
// 变量：border
// 用途：定义预览中每页四周的边距（像素），当 autoOrigin 为 false 时设为 0。
// 注意事项：此边距仅影响预览，不影响实际打印输出。
mxPrintPreview.prototype.border = 0;

/**
 * Variable: marginTop
 * 
 * The margin at the top of the page (number). Default is 0.
 */
// 中文注释：
// 变量：marginTop
// 用途：定义页面顶部的边距（数值，单位为像素），默认为 0。
mxPrintPreview.prototype.marginTop = 0;

/**
 * Variable: marginBottom
 * 
 * The margin at the bottom of the page (number). Default is 0.
 */
// 中文注释：
// 变量：marginBottom
// 用途：定义页面底部的边距（数值，单位为像素），默认为 0。
mxPrintPreview.prototype.marginBottom = 0;

/**
 * Variable: x0
 * 
 * Holds the horizontal offset of the output.
 */
// 中文注释：
// 变量：x0
// 用途：存储输出内容的水平偏移量（像素），用于调整图表在页面上的水平位置。
mxPrintPreview.prototype.x0 = 0;

/**
 * Variable: y0
 *
 * Holds the vertical offset of the output.
 */
// 中文注释：
// 变量：y0
// 用途：存储输出内容的垂直偏移量（像素），用于调整图表在页面上的垂直位置。
mxPrintPreview.prototype.y0 = 0;

/**
 * Variable: autoOrigin
 * 
 * Specifies if the origin should be automatically computed based on the top,
 * left corner of the actual diagram contents. The required offset will be added
 * to <x0> and <y0> in <open>. Default is true.
 */
// 中文注释：
// 变量：autoOrigin
// 用途：布尔值，控制是否根据图表内容的左上角自动计算原点。
// 说明：若为 true，open 方法会将计算的偏移量添加到 x0 和 y0，默认为 true。
mxPrintPreview.prototype.autoOrigin = true;

/**
 * Variable: printOverlays
 * 
 * Specifies if overlays should be printed. Default is false.
 */
// 中文注释：
// 变量：printOverlays
// 用途：布尔值，控制是否打印图表覆盖层（如选择框、控制点等），默认为 false。
mxPrintPreview.prototype.printOverlays = false;

/**
 * Variable: printControls
 * 
 * Specifies if controls (such as folding icons) should be printed. Default is
 * false.
 */
// 中文注释：
// 变量：printControls
// 用途：布尔值，控制是否打印图表的控制元素（如折叠图标），默认为 false。
mxPrintPreview.prototype.printControls = false;

/**
 * Variable: printBackgroundImage
 * 
 * Specifies if the background image should be printed. Default is false.
 */
// 中文注释：
// 变量：printBackgroundImage
// 用途：布尔值，控制是否打印图表的背景图片，默认为 false。
mxPrintPreview.prototype.printBackgroundImage = false;

/**
 * Variable: backgroundColor
 * 
 * Holds the color value for the page background color. Default is #ffffff.
 */
// 中文注释：
// 变量：backgroundColor
// 用途：存储页面背景颜色的值，默认为白色 (#ffffff)。
// 说明：用于设置预览页面的背景颜色。
mxPrintPreview.prototype.backgroundColor = '#ffffff';

/**
 * Variable: borderColor
 * 
 * Holds the color value for the page border.
 */
// 中文注释：
// 变量：borderColor
// 用途：存储页面边框的颜色值，默认为 null（无边框）。
// 说明：可用于在预览中高亮显示页面边框。
mxPrintPreview.prototype.borderColor = null;

/**
 * Variable: title
 * 
 * Holds the title of the preview window.
 */
// 中文注释：
// 变量：title
// 用途：存储预览窗口的标题，默认为“Printer-friendly version”。
mxPrintPreview.prototype.title = null;

/**
 * Variable: pageSelector
 * 
 * Boolean that specifies if the page selector should be
 * displayed. Default is true.
 */
// 中文注释：
// 变量：pageSelector
// 用途：布尔值，控制是否显示页面选择器，默认为 true。
// 说明：页面选择器用于导航多页预览内容。
mxPrintPreview.prototype.pageSelector = null;

/**
 * Variable: wnd
 * 
 * Reference to the preview window.
 */
// 中文注释：
// 变量：wnd
// 用途：存储打印预览窗口的引用。
mxPrintPreview.prototype.wnd = null;

/**
 * Variable: targetWindow
 * 
 * Assign any window here to redirect the rendering in <open>.
 */
// 中文注释：
// 变量：targetWindow
// 用途：指定用于渲染的窗口引用，用于 open 方法中的重定向渲染。
mxPrintPreview.prototype.targetWindow = null;

/**
 * Variable: pageCount
 * 
 * Holds the actual number of pages in the preview.
 */
// 中文注释：
// 变量：pageCount
// 用途：存储预览中实际的页面数量。
mxPrintPreview.prototype.pageCount = 0;

/**
 * Variable: clipping
 * 
 * Specifies is clipping should be used to avoid creating too many cell states
 * in large diagrams. The bounding box of the cells in the original diagram is
 * used if this is enabled. Default is true.
 */
// 中文注释：
// 变量：clipping
// 用途：布尔值，控制是否使用裁剪来减少大型图表中单元状态的创建。
// 说明：若启用，使用原始图表单元的边界框进行裁剪，默认为 true。
// 注意事项：裁剪可提高性能，但可能影响边缘单元的显示。
mxPrintPreview.prototype.clipping = true;

/**
 * Function: getWindow
 * 
 * Returns <wnd>.
 */
// 中文注释：
// 方法：getWindow
// 功能：返回预览窗口的引用。
// 返回值：wnd 属性，指向当前的预览窗口。
mxPrintPreview.prototype.getWindow = function()
{
	return this.wnd;
};

/**
 * Function: getDocType
 * 
 * Returns the string that should go before the HTML tag in the print preview
 * page. This implementation returns an X-UA meta tag for IE5 in quirks mode,
 * IE8 in IE8 standards mode and edge in IE9 standards mode.
 */
// 中文注释：
// 方法：getDocType
// 功能：返回打印预览页面 HTML 标签前的 DOCTYPE 或元标签字符串。
// 说明：根据浏览器的文档模式返回相应的 X-UA-Compatible 元标签，支持 IE5、IE8 和 IE9+ 的标准模式。
// 返回值：字符串，包含 DOCTYPE 或元标签内容。
mxPrintPreview.prototype.getDoctype = function()
{
	var dt = '';
	
	if (document.documentMode == 5)
	{
		dt = '<meta http-equiv="X-UA-Compatible" content="IE=5">';
	}
	else if (document.documentMode == 8)
	{
		dt = '<meta http-equiv="X-UA-Compatible" content="IE=8">';
	}
	else if (document.documentMode > 8)
	{
		// Comment needed to make standards doctype apply in IE
		dt = '<!--[if IE]><meta http-equiv="X-UA-Compatible" content="IE=edge"><![endif]-->';
	}
	
	return dt;
};

/**
 * Function: appendGraph
 * 
 * Adds the given graph to the existing print preview.
 * 
 * Parameters:
 * 
 * css - Optional CSS string to be used in the head section.
 * targetWindow - Optional window that should be used for rendering. If
 * this is specified then no HEAD tag, CSS and BODY tag will be written.
 */
// 中文注释：
// 方法：appendGraph
// 功能：将指定的图表添加到现有的打印预览中。
// 参数说明：
// - css: 可选的 CSS 字符串，用于 head 部分。
// - targetWindow: 可选的渲染窗口，若指定则不写入 HEAD、CSS 和 BODY 标签。
// - forcePageBreaks: 布尔值，强制分页。
// - keepOpen: 布尔值，控制是否保持窗口打开。
// 说明：更新 graph、scale、x0、y0 属性，并调用 open 方法追加内容。
mxPrintPreview.prototype.appendGraph = function(graph, scale, x0, y0, forcePageBreaks, keepOpen)
{
	this.graph = graph;
	this.scale = (scale != null) ? scale : 1 / graph.pageScale;
	this.x0 = x0;
	this.y0 = y0;
	this.open(null, null, forcePageBreaks, keepOpen);
};

/**
 * Function: open
 * 
 * Shows the print preview window. The window is created here if it does
 * not exist.
 * 
 * Parameters:
 * 
 * css - Optional CSS string to be used in the head section.
 * targetWindow - Optional window that should be used for rendering. If
 * this is specified then no HEAD tag, CSS and BODY tag will be written.
 */
// 中文注释：
// 方法：open
// 功能：显示打印预览窗口，若窗口不存在则创建。
// 参数说明：
// - css: 可选的 CSS 字符串，用于 head 部分。
// - targetWindow: 可选的渲染窗口，若指定则不写入 HEAD、CSS 和 BODY 标签。
// - forcePageBreaks: 布尔值，强制分页。
// - keepOpen: 布尔值，控制是否保持窗口打开。
// 主要步骤：
// 1. 临时修改覆盖层和控制元素的渲染方式。
// 2. 创建或使用指定的预览窗口。
// 3. 计算页面数量并渲染图表内容。
// 4. 添加封面、附录和页面选择器。
// 事件处理：处理窗口滚动和调整大小事件以更新页面选择器位置。
// 注意事项：IE 浏览器可能因窗口关闭引发异常，需在 try-catch 中处理。
mxPrintPreview.prototype.open = function(css, targetWindow, forcePageBreaks, keepOpen)
{
	// Closing the window while the page is being rendered may cause an
	// exception in IE. This and any other exceptions are simply ignored.
	var previousInitializeOverlay = this.graph.cellRenderer.initializeOverlay;
	var div = null;
	
	try
	{
		// Temporarily overrides the method to redirect rendering of overlays
		// to the draw pane so that they are visible in the printout
		if (this.printOverlays)
		{
			this.graph.cellRenderer.initializeOverlay = function(state, overlay)
			{
				overlay.init(state.view.getDrawPane());
			};
		}
		
        // 中文注释：
        // 逻辑：若 printOverlays 为 true，临时重写 initializeOverlay 方法。
        // 功能：将覆盖层（如选择框）渲染到绘制面板，确保其在打印输出中可见。

		if (this.printControls)
		{
			this.graph.cellRenderer.initControl = function(state, control, handleEvents, clickHandler)
			{
				control.dialect = state.view.graph.dialect;
				control.init(state.view.getDrawPane());
			};
		}
		
        // 中文注释：
        // 逻辑：若 printControls 为 true，临时重写 initControl 方法。
        // 功能：将控制元素（如折叠图标）渲染到绘制面板，确保其在打印输出中可见。

		this.wnd = (targetWindow != null) ? targetWindow : this.wnd;
		var isNewWindow = false;
		
		if (this.wnd == null)
		{
			isNewWindow = true;
			this.wnd = window.open();
		}
		
        // 中文注释：
        // 逻辑：检查是否指定了 targetWindow，若未指定且 wnd 为空，创建新窗口。
        // 功能：确保有一个可用的预览窗口。

		var doc = this.wnd.document;
		
		if (isNewWindow)
		{
			var dt = this.getDoctype();
			
			if (dt != null && dt.length > 0)
			{
				doc.writeln(dt);
			}
			
			if (mxClient.IS_VML)
			{
				doc.writeln('<html xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">');
			}
			else
			{
				if (document.compatMode === 'CSS1Compat')
				{
					doc.writeln('<!DOCTYPE html>');
				}
				
				doc.writeln('<html>');
			}
			
			doc.writeln('<head>');
			this.writeHead(doc, css);
			doc.writeln('</head>');
			doc.writeln('<body class="mxPage">');
		}

        // 中文注释：
        // 逻辑：若为新窗口，写入 DOCTYPE、HTML 标签、head 部分和 body 标签。
        // 功能：初始化预览窗口的文档结构，调用 writeHead 添加样式。

		// Computes the horizontal and vertical page count
		var bounds = this.graph.getGraphBounds().clone();
		var currentScale = this.graph.getView().getScale();
		var sc = currentScale / this.scale;
		var tr = this.graph.getView().getTranslate();
		
		// Uses the absolute origin with no offset for all printing
		if (!this.autoOrigin)
		{
			this.x0 -= tr.x * this.scale;
			this.y0 -= tr.y * this.scale;
			bounds.width += bounds.x;
			bounds.height += bounds.y;
			bounds.x = 0;
			bounds.y = 0;
			this.border = 0;
		}
		
        // 中文注释：
        // 逻辑：计算图表的边界并调整偏移量。
        // 功能：若 autoOrigin 为 false，调整 x0 和 y0，忽略图表原点偏移，确保绝对定位。

		// Store the available page area
		var availableWidth = this.pageFormat.width - (this.border * 2);
		var availableHeight = this.pageFormat.height - (this.border * 2);
	
		// Adds margins to page format
		this.pageFormat.height += this.marginTop + this.marginBottom;

		// Compute the unscaled, untranslated bounds to find
		// the number of vertical and horizontal pages
		bounds.width /= sc;
		bounds.height /= sc;

		var hpages = Math.max(1, Math.ceil((bounds.width + this.x0) / availableWidth));
		var vpages = Math.max(1, Math.ceil((bounds.height + this.y0) / availableHeight));
		this.pageCount = hpages * vpages;
		
        // 中文注释：
        // 逻辑：计算可用页面区域和页面数量。
        // 功能：根据图表边界和缩放比例，确定水平和垂直方向的页面数，存储在 pageCount 中。

		var writePageSelector = mxUtils.bind(this, function()
		{
			if (this.pageSelector && (vpages > 1 || hpages > 1))
			{
				var table = this.createPageSelector(vpages, hpages);
				doc.body.appendChild(table);
				
				// Implements position: fixed in IE quirks mode
				if (mxClient.IS_IE && doc.documentMode == null || doc.documentMode == 5 || doc.documentMode == 8 || doc.documentMode == 7)
				{
					table.style.position = 'absolute';
					
					var update = function()
					{
						table.style.top = ((doc.body.scrollTop || doc.documentElement.scrollTop) + 10) + 'px';
					};
					
					mxEvent.addListener(this.wnd, 'scroll', function(evt)
					{
						update();
					});
					
					mxEvent.addListener(this.wnd, 'resize', function(evt)
					{
						update();
					});
				}
			}
		});
		
        // 中文注释：
        // 方法：writePageSelector（内部函数）
        // 功能：创建并添加页面选择器，用于多页导航。
        // 逻辑：若 pageSelector 为 true 且页面数大于 1，调用 createPageSelector 创建表格。
        // 事件处理：在 IE 特定模式下，监听窗口滚动和调整大小事件，动态更新选择器位置。
        // 注意事项：IE 的怪异模式需要特殊处理以模拟 fixed 定位。

		var addPage = mxUtils.bind(this, function(div, addBreak)
		{
			// Border of the DIV (aka page) inside the document
			if (this.borderColor != null)
			{
				div.style.borderColor = this.borderColor;
				div.style.borderStyle = 'solid';
				div.style.borderWidth = '1px';
			}
			
			// Needs to be assigned directly because IE doesn't support
			// child selectors, eg. body > div { background: white; }
			div.style.background = this.backgroundColor;
			
			if (forcePageBreaks || addBreak)
			{
				div.style.pageBreakAfter = 'always';
			}

			// NOTE: We are dealing with cross-window DOM here, which
			// is a problem in IE, so we copy the HTML markup instead.
			// The underlying problem is that the graph display markup
			// creation (in mxShape, mxGraphView) is hardwired to using
			// document.createElement and hence we must use this document
			// to create the complete page and then copy it over to the
			// new window.document. This can be fixed later by using the
			// ownerDocument of the container in mxShape and mxGraphView.
			if (isNewWindow && (mxClient.IS_IE || document.documentMode >= 11 || mxClient.IS_EDGE))
			{
				// For some obscure reason, removing the DIV from the
				// parent before fetching its outerHTML has missing
				// fillcolor properties and fill children, so the div
				// must be removed afterwards to keep the fillcolors.
				doc.writeln(div.outerHTML);
				div.parentNode.removeChild(div);
			}
			else if (mxClient.IS_IE || document.documentMode >= 11 || mxClient.IS_EDGE)
			{
				var clone = doc.createElement('div');
				clone.innerHTML = div.outerHTML;
				clone = clone.getElementsByTagName('div')[0];
				doc.body.appendChild(clone);
				div.parentNode.removeChild(div);
			}
			else
			{
				div.parentNode.removeChild(div);
				doc.body.appendChild(div);
			}

			if (forcePageBreaks || addBreak)
			{
				this.addPageBreak(doc);
			}
		});
		
        // 中文注释：
        // 方法：addPage（内部函数）
        // 功能：将页面（DIV）添加到文档中，并设置边框、背景和分页。
        // 参数说明：
        // - div: 要添加的页面元素。
        // - addBreak: 布尔值，控制是否添加分页符。
        // 样式设置：设置页面边框颜色、背景颜色，若 forcePageBreaks 或 addBreak 为 true，添加分页符。
        // 注意事项：IE 浏览器的跨窗口 DOM 操作需要特殊处理，复制 HTML 标记以避免问题。

		var cov = this.getCoverPages(this.pageFormat.width, this.pageFormat.height);
		
		if (cov != null)
		{
			for (var i = 0; i < cov.length; i++)
			{
				addPage(cov[i], true);
			}
		}
		
        // 中文注释：
        // 逻辑：获取封面页面并添加到文档。
        // 功能：调用 getCoverPages 获取封面页面列表，并通过 addPage 方法逐个添加，每页强制分页。

		var apx = this.getAppendices(this.pageFormat.width, this.pageFormat.height);
		
		// Appends each page to the page output for printing, making
		// sure there will be a page break after each page (ie. div)
		for (var i = 0; i < vpages; i++)
		{
			var dy = i * availableHeight / this.scale - this.y0 / this.scale +
					(bounds.y - tr.y * currentScale) / currentScale;
			
			for (var j = 0; j < hpages; j++)
			{
				if (this.wnd == null)
				{
					return null;
				}
				
				var dx = j * availableWidth / this.scale - this.x0 / this.scale +
						(bounds.x - tr.x * currentScale) / currentScale;
				var pageNum = i * hpages + j + 1;
				var clip = new mxRectangle(dx, dy, availableWidth, availableHeight);
				div = this.renderPage(this.pageFormat.width, this.pageFormat.height, 0, 0, mxUtils.bind(this, function(div)
				{
					this.addGraphFragment(-dx, -dy, this.scale, pageNum, div, clip);
					
					if (this.printBackgroundImage)
					{
						this.insertBackgroundImage(div, -dx, -dy);
					}
				}), pageNum);

				// Gives the page a unique ID for later accessing the page
				div.setAttribute('id', 'mxPage-'+pageNum);

				addPage(div, apx != null || i < vpages - 1 || j < hpages - 1);
			}
		}

        // 中文注释：
        // 逻辑：循环渲染每一页图表内容。
        // 功能：根据水平和垂直页面数，计算每页的偏移量，调用 renderPage 渲染页面，并添加图表片段和背景图片。
        // 交互逻辑：为每页设置唯一 ID（mxPage-<pageNum>），便于页面选择器导航。
        // 注意事项：若窗口已关闭（wnd 为 null），返回 null 中止操作。

		if (apx != null)
		{
			for (var i = 0; i < apx.length; i++)
			{
				addPage(apx[i], i < apx.length - 1);
			}
		}

        // 中文注释：
        // 逻辑：获取附录页面并添加到文档。
        // 功能：调用 getAppendices 获取附录页面列表，并通过 addPage 方法逐个添加，最后一页不加分页符。

		if (isNewWindow && !keepOpen)
		{
			this.closeDocument();
			writePageSelector();
		}
		
        // 中文注释：
        // 逻辑：若为新窗口且不保持打开，关闭文档并添加页面选择器。
        // 功能：调用 closeDocument 写入结束标签，调用 writePageSelector 添加页面选择器。

		this.wnd.focus();
	}
	catch (e)
	{
		// Removes the DIV from the document in case of an error
		if (div != null && div.parentNode != null)
		{
			div.parentNode.removeChild(div);
		}
	}
	finally
	{
		this.graph.cellRenderer.initializeOverlay = previousInitializeOverlay;
	}

    // 中文注释：
    // 逻辑：异常处理和清理。
    // 功能：捕获渲染过程中的异常，移除临时 DIV，恢复原始的覆盖层渲染方法。
    // 注意事项：确保即使发生错误也能正确清理临时资源。

	return this.wnd;
};

/**
 * Function: addPageBreak
 * 
 * Adds a page break to the given document.
 */
// 中文注释：
// 方法：addPageBreak
// 功能：向指定文档添加分页符。
// 说明：创建一个带有 mxPageBreak 类名的 hr 元素，添加到文档的 body 中。
mxPrintPreview.prototype.addPageBreak = function(doc)
{
	var hr = doc.createElement('hr');
	hr.className = 'mxPageBreak';
	doc.body.appendChild(hr);
};

/**
 * Function: closeDocument
 * 
 * Writes the closing tags for body and page after calling <writePostfix>.
 */
// 中文注释：
// 方法：closeDocument
// 功能：写入 body 和 html 的结束标签，完成文档。
// 说明：调用 writePostfix 写入后缀内容，添加结束标签，并移除文档中所有事件处理程序。
// 注意事项：若窗口或文档不可用，捕获异常并忽略。
mxPrintPreview.prototype.closeDocument = function()
{
	try
	{
		if (this.wnd != null && this.wnd.document != null)
		{
			var doc = this.wnd.document;
			
			this.writePostfix(doc);
			doc.writeln('</body>');
			doc.writeln('</html>');
			doc.close();
			
			// Removes all event handlers in the print output
			mxEvent.release(doc.body);
		}
	}
	catch (e)
	{
		// ignore any errors resulting from wnd no longer being available
	}
};

/**
 * Function: writeHead
 * 
 * Writes the HEAD section into the given document, without the opening
 * and closing HEAD tags.
 */
// 中文注释：
// 方法：writeHead
// 功能：向文档写入 head 部分的样式和标题内容，不包括 head 标签。
// 参数说明：
// - doc: 目标文档对象。
// - css: 可选的 CSS 字符串。
// 样式设置：
// 1. 写入窗口标题（若 title 非空）。
// 2. 为 VML 添加命名空间样式。
// 3. 引入公共样式表（common.css）。
// 4. 定义打印和屏幕显示的样式规则，隐藏页面选择器和分页符。
// 注意事项：IE 浏览器的 fixed 定位需特殊处理。
mxPrintPreview.prototype.writeHead = function(doc, css)
{
	if (this.title != null)
	{
		doc.writeln('<title>' + this.title + '</title>');
	}
	
	// Adds required namespaces
	if (mxClient.IS_VML)
	{
		doc.writeln('<style type="text/css">v\\:*{behavior:url(#default#VML)}o\\:*{behavior:url(#default#VML)}</style>');
	}

	// Adds all required stylesheets
	mxClient.link('stylesheet', mxClient.basePath + '/css/common.css', doc);

	// Removes horizontal rules and page selector from print output
	doc.writeln('<style type="text/css">');
	doc.writeln('@media print {');
	doc.writeln('  * { -webkit-print-color-adjust: exact; }');
	doc.writeln('  table.mxPageSelector { display: none; }');
	doc.writeln('  hr.mxPageBreak { display: none; }');
	doc.writeln('}');
	doc.writeln('@media screen {');
	
	// NOTE: position: fixed is not supported in IE, so the page selector
	// position (absolute) needs to be updated in IE (see below)
	doc.writeln('  table.mxPageSelector { position: fixed; right: 10px; top: 10px;' +
			'font-family: Arial; font-size:10pt; border: solid 1px darkgray;' +
			'background: white; border-collapse:collapse; }');
	doc.writeln('  table.mxPageSelector td { border: solid 1px gray; padding:4px; }');
	doc.writeln('  body.mxPage { background: gray; }');
	doc.writeln('}');
	
	if (css != null)
	{
		doc.writeln(css);
	}
	
	doc.writeln('</style>');
};

/**
 * Function: writePostfix
 * 
 * Called before closing the body of the page. This implementation is empty.
 */
// 中文注释：
// 方法：writePostfix
// 功能：在关闭页面 body 前调用的方法，用于添加后缀内容。
// 说明：当前实现为空，可由子类重写以添加自定义内容。
mxPrintPreview.prototype.writePostfix = function(doc)
{
	// empty
};

/**
 * Function: createPageSelector
 * 
 * Creates the page selector table.
 */
// 中文注释：
// 方法：createPageSelector
// 功能：创建页面选择器表格，用于导航多页内容。
// 参数说明：
// - vpages: 垂直页面数。
// - hpages: 水平页面数。
// 样式设置：表格使用 fixed 定位，右上角显示，包含边框和内边距。
// 交互逻辑：为每个页面创建链接，点击可跳转到对应页面（mxPage-<pageNum>）。
// 注意事项：在 Firefox 中需特殊处理锚点链接，防止 URL 追加问题。
mxPrintPreview.prototype.createPageSelector = function(vpages, hpages)
{
	var doc = this.wnd.document;
	var table = doc.createElement('table');
	table.className = 'mxPageSelector';
	table.setAttribute('border', '0');

	var tbody = doc.createElement('tbody');
	
	for (var i = 0; i < vpages; i++)
	{
		var row = doc.createElement('tr');
		
		for (var j = 0; j < hpages; j++)
		{
			var pageNum = i * hpages + j + 1;
			var cell = doc.createElement('td');
			var a = doc.createElement('a');
			a.setAttribute('href', '#mxPage-' + pageNum);

			// Workaround for FF where the anchor is appended to the URL of the original document
			if (mxClient.IS_NS && !mxClient.IS_SF && !mxClient.IS_GC)
			{
				var js = 'var page = document.getElementById(\'mxPage-' + pageNum + '\');page.scrollIntoView(true);event.preventDefault();';
				a.setAttribute('onclick', js);
			}
			
			mxUtils.write(a, pageNum, doc);
			cell.appendChild(a);
			row.appendChild(cell);
		}
		
		tbody.appendChild(row);
	}
	
	table.appendChild(tbody);
	
	return table;
};

/**
 * Function: renderPage
 * 
 * Creates a DIV that prints a single page of the given
 * graph using the given scale and returns the DIV that
 * represents the page.
 * 
 * Parameters:
 * 
 * w - Width of the page in pixels.
 * h - Height of the page in pixels.
 * dx - Optional horizontal page offset in pixels (used internally).
 * dy - Optional vertical page offset in pixels (used internally).
 * content - Callback that adds the HTML content to the inner div of a page.
 * Takes the inner div as the argument.
 * pageNumber - Integer representing the page number.
 */
// 中文注释：
// 方法：renderPage
// 功能：创建并返回一个表示单页的 DIV 元素，用于打印图表内容。
// 参数说明：
// - w: 页面宽度（像素）。
// - h: 页面高度（像素）。
// - dx: 可选的水平页面偏移量（内部使用）。
// - dy: 可选的垂直页面偏移量（内部使用）。
// - content: 回调函数，向页面内部 div 添加 HTML 内容。
// - pageNumber: 页面编号（整数，从 1 开始）。
// 主要步骤：
// 1. 创建外层和内层 DIV，设置尺寸和样式。
// 2. 处理 IE 浏览器的兼容性问题，调整定位和裁剪。
// 3. 调用 content 回调添加图表内容。
// 样式设置：DIV 包含边框和溢出控制，确保内容在页面范围内。
// 注意事项：IE9 标准模式下裁剪可能被忽略，需特殊处理。
mxPrintPreview.prototype.renderPage = function(w, h, dx, dy, content, pageNumber)
{
	var doc = this.wnd.document;
	var div = document.createElement('div');
	var arg = null;

	try
	{
		// Workaround for ignored clipping in IE 9 standards
		// when printing with page breaks and HTML labels.
		if (dx != 0 || dy != 0)
		{
			div.style.position = 'relative';
			div.style.width = w + 'px';
			div.style.height = h + 'px';
			div.style.pageBreakInside = 'avoid';
			
			var innerDiv = document.createElement('div');
			innerDiv.style.position = 'relative';
			innerDiv.style.top = this.border + 'px';
			innerDiv.style.left = this.border + 'px';
			innerDiv.style.width = (w - 2 * this.border) + 'px';
			innerDiv.style.height = (h - 2 * this.border) + 'px';
			innerDiv.style.overflow = 'hidden';
			
			var viewport = document.createElement('div');
			viewport.style.position = 'relative';
			viewport.style.marginLeft = dx + 'px';
			viewport.style.marginTop = dy + 'px';

			// FIXME: IE8 standards output problems
			if (doc.documentMode == 8)
			{
				innerDiv.style.position = 'absolute';
				viewport.style.position = 'absolute';
			}
		
			if (doc.documentMode == 10)
			{
				viewport.style.width = '100%';
				viewport.style.height = '100%';
			}
			
			innerDiv.appendChild(viewport);
			div.appendChild(innerDiv);
			document.body.appendChild(div);
			arg = viewport;
		}
		// FIXME: IE10/11 too many pages
		else
		{
			div.style.width = w + 'px';
			div.style.height = h + 'px';
			div.style.overflow = 'hidden';
			div.style.pageBreakInside = 'avoid';
			
			// IE8 uses above branch currently
			if (doc.documentMode == 8)
			{
				div.style.position = 'relative';
			}
			
			var innerDiv = document.createElement('div');
			innerDiv.style.width = (w - 2 * this.border) + 'px';
			innerDiv.style.height = (h - 2 * this.border) + 'px';
			innerDiv.style.overflow = 'hidden';

			if (mxClient.IS_IE && (doc.documentMode == null || doc.documentMode == 5 ||
				doc.documentMode == 8 || doc.documentMode == 7))
			{
				innerDiv.style.marginTop = this.border + 'px';
				innerDiv.style.marginLeft = this.border + 'px';	
			}
			else
			{
				innerDiv.style.top = this.border + 'px';
				innerDiv.style.left = this.border + 'px';
			}
	
			if (this.graph.dialect == mxConstants.DIALECT_VML)
			{
				innerDiv.style.position = 'absolute';
			}

			div.appendChild(innerDiv);
			document.body.appendChild(div);
			arg = innerDiv;
		}
	}
	catch (e)
	{
		div.parentNode.removeChild(div);
		div = null;
		
		throw e;
	}

	content(arg);
	 
	return div;
};

/**
 * Function: getRoot
 * 
 * Returns the root cell for painting the graph.
 */
// 中文注释：
// 方法：getRoot
// 功能：返回用于绘制图表的根单元。
// 说明：优先使用当前视图的根节点，若不存在则使用图表模型的根节点。
// 返回值：图表的根单元对象。
mxPrintPreview.prototype.getRoot = function()
{
	var root = this.graph.view.currentRoot;
	
	if (root == null)
	{
		root = this.graph.getModel().getRoot();
	}
	
	return root;
};

/**
 * Function: useCssTransforms
 * 
 * Returns true if CSS transforms should be used for scaling content.
 * This returns true if foreignObject is supported and we're not in Safari
 * as it has clipping bugs for transformed CSS content with foreignObjects.
 */
// 中文注释：
// 方法：useCssTransforms
// 功能：判断是否使用 CSS 变换来缩放内容。
// 说明：若支持 foreignObject 且非 Safari 浏览器，返回 true。
// 注意事项：Safari 对变换后的 CSS 内容有裁剪错误，需避免使用。
// 返回值：布尔值，指示是否使用 CSS 变换。
mxPrintPreview.prototype.useCssTransforms = function()
{
	return !mxClient.NO_FO && !mxClient.IS_SF;
};

/**
 * Function: addGraphFragment
 * 
 * Adds a graph fragment to the given div.
 * 
 * Parameters:
 * 
 * dx - Horizontal translation for the diagram.
 * dy - Vertical translation for the diagram.
 * scale - Scale for the diagram.
 * pageNumber - Number of the page to be rendered.
 * div - Div that contains the output.
 * clip - Contains the clipping rectangle as an <mxRectangle>.
 */
// 中文注释：
// 方法：addGraphFragment
// 功能：将图表片段添加到指定的 DIV 中。
// 参数说明：
// - dx: 图表的水平平移量。
// - dy: 图表的垂直平移量。
// - scale: 图表的缩放比例。
// - pageNumber: 要渲染的页面编号。
// - div: 包含输出的 DIV 元素。
// - clip: 裁剪矩形（mxRectangle），定义渲染区域。
// 主要步骤：
// 1. 设置图表容器为指定 DIV。
// 2. 根据图表类型（SVG、VML、HTML）初始化视图。
// 3. 禁用事件和图表交互，调整视图缩放和平移。
// 4. 若启用裁剪，优化渲染以减少单元状态创建。
// 5. 渲染图表内容并清理临时状态。
// 注意事项：
// - IE 浏览器的 SVG 和 VML 输出需特殊处理。
// - 裁剪可提高大型图表的性能，但需确保边缘单元正确显示。
mxPrintPreview.prototype.addGraphFragment = function(dx, dy, scale, pageNumber, div, clip)
{
	var view = this.graph.getView();
	var previousContainer = this.graph.container;
	this.graph.container = div;
	
	var canvas = view.getCanvas();
	var backgroundPane = view.getBackgroundPane();
	var drawPane = view.getDrawPane();
	var overlayPane = view.getOverlayPane();
	var realScale = scale;

	if (this.graph.dialect == mxConstants.DIALECT_SVG)
	{
		view.createSvg();
		
		// Uses CSS transform for scaling
		if (this.useCssTransforms())
		{
			var g = view.getDrawPane().parentNode;
			var prev = g.getAttribute('transform');
			g.setAttribute('transformOrigin', '0 0');
			g.setAttribute('transform', 'scale(' + scale + ',' + scale + ')' +
				'translate(' + dx + ',' + dy + ')');
			
			scale = 1;
			dx = 0;
			dy = 0;
		}
	}
	else if (this.graph.dialect == mxConstants.DIALECT_VML)
	{
		view.createVml();
	}
	else
	{
		view.createHtml();
	}
	
	// Disables events on the view
	var eventsEnabled = view.isEventsEnabled();
	view.setEventsEnabled(false);
	
	// Disables the graph to avoid cursors
	var graphEnabled = this.graph.isEnabled();
	this.graph.setEnabled(false);

	// Resets the translation
	var translate = view.getTranslate();
	view.translate = new mxPoint(dx, dy);
	
	// Redraws only states that intersect the clip
	var redraw = this.graph.cellRenderer.redraw;
	var states = view.states;
	var s = view.scale;

	// Gets the transformed clip for intersection check below
	if (this.clipping)
	{
		var tempClip = new mxRectangle((clip.x + translate.x) * s, (clip.y + translate.y) * s,
				clip.width * s / realScale, clip.height * s / realScale);

		// Checks clipping rectangle for speedup
		// Must create terminal states for edge clipping even if terminal outside of clip
		this.graph.cellRenderer.redraw = function(state, force, rendering)
		{
			if (state != null)
			{
				// Gets original state from graph to find bounding box
				var orig = states.get(state.cell);
				
				if (orig != null)
				{
					var bbox = view.getBoundingBox(orig, false);
					
					// Stops rendering if outside clip for speedup but ignores
					// edge labels where width and height is set to 0
					if (bbox != null && bbox.width > 0 && bbox.height > 0 &&
						!mxUtils.intersects(tempClip, bbox))
					{
						return;
					}
				}
			}
			
			redraw.apply(this, arguments);
		};
	}
	
	var temp = null;
	
	try
	{
		// Creates the temporary cell states in the view and
		// draws them onto the temporary DOM nodes in the view
		var cells = [this.getRoot()];
		temp = new mxTemporaryCellStates(view, scale, cells, null, mxUtils.bind(this, function(state)
		{
			return this.getLinkForCellState(state);
		}));
	}
	finally
	{
		// Removes overlay pane with selection handles
		// controls and icons from the print output
		if (mxClient.IS_IE)
		{
			view.overlayPane.innerHTML = '';
			view.canvas.style.overflow = 'hidden';
			view.canvas.style.position = 'relative';
			view.canvas.style.top = this.marginTop + 'px';
			view.canvas.style.width = clip.width + 'px';
			view.canvas.style.height = clip.height + 'px';
		}
		else
		{
			// Removes everything but the SVG node
			var tmp = div.firstChild;

			while (tmp != null)
			{
				var next = tmp.nextSibling;
				var name = tmp.nodeName.toLowerCase();

				// Note: Width and height are required in FF 11
				if (name == 'svg')
				{
					tmp.style.overflow = 'hidden';
					tmp.style.position = 'relative';
					tmp.style.top = this.marginTop + 'px';
					tmp.setAttribute('width', clip.width);
					tmp.setAttribute('height', clip.height);
					tmp.style.width = '';
					tmp.style.height = '';
				}
				// Tries to fetch all text labels and only text labels
				else if (tmp.style.cursor != 'default' && name != 'div')
				{
					tmp.parentNode.removeChild(tmp);
				}
				
				tmp = next;
			}
		}
		
		// Puts background image behind SVG output
		if (this.printBackgroundImage)
		{
			var svgs = div.getElementsByTagName('svg');
			
			if (svgs.length > 0)
			{
				svgs[0].style.position = 'absolute';
			}
		}
		
		// Completely removes the overlay pane to remove more handles
		view.overlayPane.parentNode.removeChild(view.overlayPane);

		// Restores the state of the view
		this.graph.setEnabled(graphEnabled);
		this.graph.container = previousContainer;
		this.graph.cellRenderer.redraw = redraw;
		view.canvas = canvas;
		view.backgroundPane = backgroundPane;
		view.drawPane = drawPane;
		view.overlayPane = overlayPane;
		view.translate = translate;
		temp.destroy();
		view.setEventsEnabled(eventsEnabled);
	}
};

/**
 * Function: getLinkForCellState
 * 
 * Returns the link for the given cell state. This returns null.
 */
// 中文注释：
// 方法：getLinkForCellState
// 功能：返回指定单元状态的链接。
// 说明：当前实现返回 null，可由子类重写以返回特定单元的链接。
// 参数说明：
// - state: 图表单元的状态对象。
// 返回值：单元的链接字符串或 null。
mxPrintPreview.prototype.getLinkForCellState = function(state)
{
	return this.graph.getLinkForCell(state.cell);
};

/**
 * Function: insertBackgroundImage
 * 
 * Inserts the background image into the given div.
 */
// 中文注释：
// 方法：insertBackgroundImage
// 功能：将图表的背景图片插入到指定 DIV 中。
// 参数说明：
// - div: 目标 DIV 元素。
// - dx: 水平平移量。
// - dy: 垂直平移量。
// 说明：根据图表的背景图片，创建 img 元素并设置位置、尺寸和缩放比例。
// 注意事项：仅在 printBackgroundImage 为 true 时调用。
mxPrintPreview.prototype.insertBackgroundImage = function(div, dx, dy)
{
	var bg = this.graph.backgroundImage;
	
	if (bg != null)
	{
		var img = document.createElement('img');
		img.style.position = 'absolute';
		img.style.marginLeft = Math.round(dx * this.scale) + 'px';
		img.style.marginTop = Math.round(dy * this.scale) + 'px';
		img.setAttribute('width', Math.round(this.scale * bg.width));
		img.setAttribute('height', Math.round(this.scale * bg.height));
		img.src = bg.src;
		
		div.insertBefore(img, div.firstChild);
	}
};

/**
 * Function: getCoverPages
 * 
 * Returns the pages to be added before the print output. This returns null.
 */
// 中文注释：
// 方法：getCoverPages
// 功能：返回在打印输出前添加的封面页面。
// 说明：当前实现返回 null，可由子类重写以返回自定义封面页面。
// 参数说明：
// - w: 页面宽度（像素）。
// - h: 页面高度（像素）。
// 返回值：封面页面数组或 null。
mxPrintPreview.prototype.getCoverPages = function()
{
	return null;
};

/**
 * Function: getAppendices
 * 
 * Returns the pages to be added after the print output. This returns null.
 */
// 中文注释：
// 方法：getAppendices
// 功能：返回在打印输出后添加的附录页面。
// 说明：当前实现返回 null，可由子类重写以返回自定义附录页面。
// 参数说明：
// - w: 页面宽度（像素）。
// - h: 页面高度（像素）。
// 返回值：附录页面数组或 null。
mxPrintPreview.prototype.getAppendices = function()
{
	return null;
};

/**
 * Function: print
 * 
 * Opens the print preview and shows the print dialog.
 * 
 * Parameters:
 * 
 * css - Optional CSS string to be used in the head section.
 */
// 中文注释：
// 方法：print
// 功能：打开打印预览并显示打印对话框。
// 参数说明：
// - css: 可选的 CSS 字符串，用于 head 部分。
// 说明：调用 open 方法创建预览窗口，并触发打印对话框。
mxPrintPreview.prototype.print = function(css)
{
	var wnd = this.open(css);
	
	if (wnd != null)
	{
		wnd.print();
	}
};

/**
 * Function: close
 * 
 * Closes the print preview window.
 */
// 中文注释：
// 方法：close
// 功能：关闭打印预览窗口。
// 说明：若窗口存在，调用其 close 方法并清空 wnd 属性。
mxPrintPreview.prototype.close = function()
{
	if (this.wnd != null)
	{
		this.wnd.close();
		this.wnd = null;
	}
};
