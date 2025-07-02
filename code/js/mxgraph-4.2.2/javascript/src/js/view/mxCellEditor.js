/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxCellEditor
 *
 * In-place editor for the graph. To control this editor, use
 * <mxGraph.invokesStopCellEditing>, <mxGraph.enterStopsCellEditing> and
 * <mxGraph.escapeEnabled>. If <mxGraph.enterStopsCellEditing> is true then
 * ctrl-enter or shift-enter can be used to create a linefeed. The F2 and
 * escape keys can always be used to stop editing.
 * 
 * To customize the location of the textbox in the graph, override
 * <getEditorBounds> as follows:
 * 
 * (code)
 * graph.cellEditor.getEditorBounds = function(state)
 * {
 *   var result = mxCellEditor.prototype.getEditorBounds.apply(this, arguments);
 *   
 *   if (this.graph.getModel().isEdge(state.cell))
 *   {
 *     result.x = state.getCenterX() - result.width / 2;
 *     result.y = state.getCenterY() - result.height / 2;
 *   }
 *   
 *   return result;
 * };
 * (end)
 * 
 * Note that this hook is only called if <autoSize> is false. If <autoSize> is true,
 * then <mxShape.getLabelBounds> is used to compute the current bounds of the textbox.
 * 
 * The textarea uses the mxCellEditor CSS class. You can modify this class in
 * your custom CSS. Note: You should modify the CSS after loading the client
 * in the page.
 *
 * Example:
 * 
 * To only allow numeric input in the in-place editor, use the following code.
 *
 * (code)
 * var text = graph.cellEditor.textarea;
 * 
 * mxEvent.addListener(text, 'keydown', function (evt)
 * {
 *   if (!(evt.keyCode >= 48 && evt.keyCode <= 57) &&
 *       !(evt.keyCode >= 96 && evt.keyCode <= 105))
 *   {
 *     mxEvent.consume(evt);
 *   }
 * }); 
 * (end)
 * 
 * Placeholder:
 * 
 * To implement a placeholder for cells without a label, use the
 * <emptyLabelText> variable.
 * 
 * Resize in Chrome:
 * 
 * Resize of the textarea is disabled by default. If you want to enable
 * this feature extend <init> and set this.textarea.style.resize = ''.
 * 
 * To start editing on a key press event, the container of the graph
 * should have focus or a focusable parent should be used to add the
 * key press handler as follows.
 * 
 * (code)
 * mxEvent.addListener(graph.container, 'keypress', mxUtils.bind(this, function(evt)
 * {
 *   if (!graph.isEditing() && !graph.isSelectionEmpty() && evt.which !== 0 &&
 *       !mxEvent.isAltDown(evt) && !mxEvent.isControlDown(evt) && !mxEvent.isMetaDown(evt))
 *   {
 *     graph.startEditing();
 *     
 *     if (mxClient.IS_FF)
 *     {
 *       graph.cellEditor.textarea.value = String.fromCharCode(evt.which);
 *     }
 *   }
 * }));
 * (end)
 * 
 * To allow focus for a DIV, and hence to receive key press events, some browsers
 * require it to have a valid tabindex attribute. In this case the following
 * code may be used to keep the container focused.
 * 
 * (code)
 * var graphFireMouseEvent = graph.fireMouseEvent;
 * graph.fireMouseEvent = function(evtName, me, sender)
 * {
 *   if (evtName == mxEvent.MOUSE_DOWN)
 *   {
 *     this.container.focus();
 *   }
 *   
 *   graphFireMouseEvent.apply(this, arguments);
 * };
 * (end)
 *
 * Constructor: mxCellEditor
 *
 * Constructs a new in-place editor for the specified graph.
 * 
 * Parameters:
 * 
 * graph - Reference to the enclosing <mxGraph>.
 */
 // 构造函数：mxCellEditor
 // 功能：为指定的图创建原地编辑器
 // 参数：
 //   graph - 指向外部mxGraph的引用，用于与图交互
function mxCellEditor(graph)
{
	this.graph = graph;
	
	// Stops editing after zoom changes
    // 在缩放变化后停止编辑
    // 中文注释：缩放处理器，当图缩放时调整编辑器状态
	this.zoomHandler = mxUtils.bind(this, function()
	{
		if (this.graph.isEditing())
		{
			this.resize();
		}
	});
    // 中文注释：为图的视图添加缩放和缩放平移事件监听，确保编辑器在缩放时调整大小
	this.graph.view.addListener(mxEvent.SCALE, this.zoomHandler);
	this.graph.view.addListener(mxEvent.SCALE_AND_TRANSLATE, this.zoomHandler);
	
	// Adds handling of deleted cells while editing
    // 在编辑时处理单元格删除
    // 中文注释：更改处理器，检测正在编辑的单元格是否被删除
	this.changeHandler = mxUtils.bind(this, function(sender)
	{
		if (this.editingCell != null && this.graph.getView().getState(this.editingCell) == null)
		{
			this.stopEditing(true);
		}
	});
    // 中文注释：为图模型添加更改事件监听，处理单元格删除情况
	this.graph.getModel().addListener(mxEvent.CHANGE, this.changeHandler);
};

/**
 * Variable: graph
 * 
 * Reference to the enclosing <mxGraph>.
 */
 // 中文注释：graph 变量
 // 用途：存储对外部mxGraph的引用，用于编辑器与图的交互
mxCellEditor.prototype.graph = null;

/**
 * Variable: textarea
 *
 * Holds the DIV that is used for text editing. Note that this may be null before the first
 * edit. Instantiated in <init>.
 */
 // 中文注释：textarea 变量
 // 用途：存储用于文本编辑的DIV元素，在init方法中初始化，可能在首次编辑前为null
mxCellEditor.prototype.textarea = null;

/**
 * Variable: editingCell
 * 
 * Reference to the <mxCell> that is currently being edited.
 */
 // 中文注释：editingCell 变量
 // 用途：存储当前正在编辑的mxCell对象的引用
mxCellEditor.prototype.editingCell = null;

/**
 * Variable: trigger
 * 
 * Reference to the event that was used to start editing.
 */
 // 中文注释：trigger 变量
 // 用途：存储触发编辑的事件引用
mxCellEditor.prototype.trigger = null;

/**
 * Variable: modified
 * 
 * Specifies if the label has been modified.
 */
 // 中文注释：modified 变量
 // 用途：标记标签是否被修改，布尔值
mxCellEditor.prototype.modified = false;

/**
 * Variable: autoSize
 * 
 * Specifies if the textarea should be resized while the text is being edited.
 * Default is true.
 */
 // 中文注释：autoSize 变量
 // 用途：控制文本编辑时textarea是否自动调整大小，默认值为true
 // 重要配置参数：决定编辑器是否根据内容动态调整尺寸
mxCellEditor.prototype.autoSize = true;

/**
 * Variable: selectText
 * 
 * Specifies if the text should be selected when editing starts. Default is
 * true.
 */
 // 中文注释：selectText 变量
 // 用途：控制编辑开始时是否自动选中文本，默认值为true
 // 重要配置参数：影响用户编辑时的文本选择行为
mxCellEditor.prototype.selectText = true;

/**
 * Variable: emptyLabelText
 * 
 * Text to be displayed for empty labels. Default is '' or '<br>' in Firefox as
 * a workaround for the missing cursor bug for empty content editable. This can
 * be set to eg. "[Type Here]" to easier visualize editing of empty labels. The
 * value is only displayed before the first keystroke and is never used as the
 * actual editing value.
 */
 // 中文注释：emptyLabelText 变量
 // 用途：定义空标签显示的占位文本，默认值为空字符串或Firefox中为'<br>'（解决光标显示问题）
 // 重要配置参数：用于可视化空标签的编辑，首次按键后清除
mxCellEditor.prototype.emptyLabelText = (mxClient.IS_FF) ? '<br>' : '';

/**
 * Variable: escapeCancelsEditing
 * 
 * If true, pressing the escape key will stop editing and not accept the new
 * value. Change this to false to accept the new value on escape, and cancel
 * editing on Shift+Escape instead. Default is true.
 */
 // 中文注释：escapeCancelsEditing 变量
 // 用途：控制Escape键是否取消编辑并丢弃新值，默认值为true
 // 重要配置参数：若为false，Escape键接受新值，Shift+Escape取消编辑
mxCellEditor.prototype.escapeCancelsEditing = true;

/**
 * Variable: textNode
 * 
 * Reference to the label DOM node that has been hidden.
 */
 // 中文注释：textNode 变量
 // 用途：存储被隐藏的标签DOM节点的引用
mxCellEditor.prototype.textNode = '';

/**
 * Variable: zIndex
 * 
 * Specifies the zIndex for the textarea. Default is 5.
 */
 // 中文注释：zIndex 变量
 // 用途：定义textarea的z-index层级，默认值为5
 // 重要配置参数：控制编辑器在图中的显示层级
mxCellEditor.prototype.zIndex = 5;

/**
 * Variable: minResize
 * 
 * Defines the minimum width and height to be used in <resize>. Default is 0x20px.
 */
 // 中文注释：minResize 变量
 // 用途：定义resize方法中textarea的最小宽度和高度，默认值为0x20像素
 // 重要配置参数：确保编辑器尺寸不低于最小值
mxCellEditor.prototype.minResize = new mxRectangle(0, 20);

/**
 * Variable: wordWrapPadding
 * 
 * Correction factor for word wrapping width. Default is 2 in quirks, 0 in IE
 * 11 and 1 in all other browsers and modes.
 */
 // 中文注释：wordWrapPadding 变量
 // 用途：定义自动换行宽度校正因子，默认值在quirks模式为2，IE11为0，其他浏览器为1
 // 重要配置参数：用于调整自动换行时的宽度计算
mxCellEditor.prototype.wordWrapPadding = (mxClient.IS_QUIRKS) ? 2 : (!mxClient.IS_IE11) ? 1 : 0;

/**
 * Variable: blurEnabled
 *
 * If <focusLost> should be called if <textarea> loses the focus. Default is false.
 */
 // 中文注释：blurEnabled 变量
 // 用途：控制textarea失去焦点时是否调用focusLost方法，默认值为false
 // 重要配置参数：决定焦点丢失时是否触发特定逻辑
mxCellEditor.prototype.blurEnabled = false;

/**
 * Variable: initialValue
 * 
 * Holds the initial editing value to check if the current value was modified.
 */
 // 中文注释：initialValue 变量
 // 用途：存储初始编辑值，用于检查当前值是否被修改
mxCellEditor.prototype.initialValue = null;

/**
 * Variable: align
 * 
 * Holds the current temporary horizontal alignment for the cell style. If this
 * is modified then the current text alignment is changed and the cell style is
 * updated when the value is applied.
 */
 // 中文注释：align 变量
 // 用途：存储当前编辑会话的临时水平对齐方式，修改时会更新单元格样式
 // 重要配置参数：影响编辑器中文本的对齐方式
mxCellEditor.prototype.align = null;

/**
 * Function: init
 *
 * Creates the <textarea> and installs the event listeners. The key handler
 * updates the <modified> state.
 */
 // 中文注释：init 方法
 // 功能：初始化textarea元素并安装事件监听器
 // 关键步骤：创建DIV元素，设置样式和contentEditable属性，安装键盘、拖拽等事件监听
 // 特殊处理：针对Chrome浏览器设置最小高度，兼容旧版编辑器定位
mxCellEditor.prototype.init = function ()
{
	this.textarea = document.createElement('div');
	this.textarea.className = 'mxCellEditor mxPlainTextEditor';
	this.textarea.contentEditable = true;
	
	// Workaround for selection outside of DIV if height is 0
    // 针对高度为0时DIV外部选择的问题的解决方法
    // 中文注释：特殊处理，Chrome浏览器中设置最小高度为1em，防止选择问题
	if (mxClient.IS_GC)
	{
		this.textarea.style.minHeight = '1em';
	}

	this.textarea.style.position = ((this.isLegacyEditor())) ? 'absolute' : 'relative';
    // 中文注释：根据是否为旧版编辑器，设置textarea的定位方式（绝对或相对）

	this.installListeners(this.textarea);
    // 中文注释：调用installListeners方法，为textarea安装事件监听器
};

/**
 * Function: applyValue
 * 
 * Called in <stopEditing> if cancel is false to invoke <mxGraph.labelChanged>.
 */
 // 中文注释：applyValue 方法
 // 功能：在停止编辑时（非取消）调用mxGraph.labelChanged更新单元格标签
 // 参数：
 //   state - 当前单元格状态
 //   value - 新标签值
mxCellEditor.prototype.applyValue = function(state, value)
{
	this.graph.labelChanged(state.cell, value, this.trigger);
    // 中文注释：调用图的labelChanged方法，更新单元格的标签值
};

/**
 * Function: setAlign
 * 
 * Sets the temporary horizontal alignment for the current editing session.
 */
 // 中文注释：setAlign 方法
 // 功能：设置当前编辑会话的临时水平对齐方式
 // 参数：
 //   align - 对齐方式（例如left、center、right）
 // 交互逻辑：更新textarea的textAlign样式并触发resize方法
mxCellEditor.prototype.setAlign = function (align)
{
	if (this.textarea != null)
	{
		this.textarea.style.textAlign = align;
        // 中文注释：设置textarea的文本对齐样式
	}
	
	this.align = align;
    // 中文注释：更新align变量，存储当前对齐方式
	this.resize();
    // 中文注释：调用resize方法，调整编辑器大小和位置
};

/**
 * Function: getInitialValue
 * 
 * Gets the initial editing value for the given cell.
 */
 // 中文注释：getInitialValue 方法
 // 功能：获取指定单元格的初始编辑值
 // 参数：
 //   state - 单元格状态
 //   trigger - 触发编辑的事件
 // 特殊处理：在非quirks模式下，处理换行符以兼容编辑器显示
mxCellEditor.prototype.getInitialValue = function(state, trigger)
{
	var result = mxUtils.htmlEntities(this.graph.getEditingValue(state.cell, trigger), false);
    // 中文注释：获取单元格的编辑值并进行HTML实体编码

    // Workaround for trailing line breaks being ignored in the editor
    // 针对编辑器忽略尾随换行符的解决方法
    // 中文注释：特殊处理，将尾随换行符替换为<div><br></div>，确保正确显示
	if (!mxClient.IS_QUIRKS && document.documentMode != 8 && document.documentMode != 9 &&
		document.documentMode != 10)
	{
		result = mxUtils.replaceTrailingNewlines(result, '<div><br></div>');
	}
    
    return result.replace(/\n/g, '<br>');
    // 中文注释：将换行符替换为<br>标签，确保编辑器正确渲染
};

/**
 * Function: getCurrentValue
 * 
 * Returns the current editing value.
 */
 // 中文注释：getCurrentValue 方法
 // 功能：获取当前编辑值
 // 参数：
 //   state - 单元格状态
 // 返回：从textarea的子节点中提取带空白的文本
mxCellEditor.prototype.getCurrentValue = function(state)
{
	return mxUtils.extractTextWithWhitespace(this.textarea.childNodes);
    // 中文注释：提取textarea子节点的文本内容，保留空白
};

/**
 * Function: isCancelEditingKeyEvent
 * 
 * Returns true if <escapeCancelsEditing> is true and shift, control and meta
 * are not pressed.
 */
 // 中文注释：isCancelEditingKeyEvent 方法
 // 功能：判断键盘事件是否应取消编辑
 // 参数：
 //   evt - 键盘事件
 // 事件处理逻辑：当escapeCancelsEditing为true且未按下shift、control或meta键时返回true
mxCellEditor.prototype.isCancelEditingKeyEvent = function(evt)
{
	return this.escapeCancelsEditing || mxEvent.isShiftDown(evt) || mxEvent.isControlDown(evt) || mxEvent.isMetaDown(evt);
    // 中文注释：检查escapeCancelsEditing状态或修饰键状态
};

/**
 * Function: installListeners
 * 
 * Installs listeners for focus, change and standard key event handling.
 */
 // 中文注释：installListeners 方法
 // 功能：为textarea安装焦点、更改和标准键盘事件监听器
 // 参数：
 //   elt - textarea元素
 // 事件处理逻辑：处理拖拽、失去焦点、键盘按下、粘贴等事件
 // 特殊处理：防止拖拽时意外应用值，处理空占位文本
mxCellEditor.prototype.installListeners = function(elt)
{
	// Applies value if text is dragged
	// LATER: Gesture mouse events ignored for starting move
    // 中文注释：当文本被拖拽时应用值，注意：暂未处理手势鼠标事件
	mxEvent.addListener(elt, 'dragstart', mxUtils.bind(this, function(evt)
	{
		this.graph.stopEditing(false);
        // 中文注释：停止编辑并取消新值
		mxEvent.consume(evt);
        // 中文注释：消费拖拽事件，防止默认行为
	}));

	// Applies value if focus is lost
    // 中文注释：当失去焦点时应用值
	mxEvent.addListener(elt, 'blur', mxUtils.bind(this, function(evt)
	{
		if (this.blurEnabled)
		{
			this.focusLost(evt);
            // 中文注释：如果blurEnabled为true，调用focusLost处理焦点丢失
		}
	}));

	// Updates modified state and handles placeholder text
    // 中文注释：更新修改状态并处理占位文本
	mxEvent.addListener(elt, 'keydown', mxUtils.bind(this, function(evt)
	{
		if (!mxEvent.isConsumed(evt))
		{
			if (this.isStopEditingEvent(evt))
			{
				this.graph.stopEditing(false);
                // 中文注释：如果事件触发停止编辑，调用stopEditing并取消新值
				mxEvent.consume(evt);
                // 中文注释：消费事件，防止默认行为
			}
			else if (evt.keyCode == 27 /* Escape */)
			{
				this.graph.stopEditing(this.isCancelEditingKeyEvent(evt));
                // 中文注释：Escape键按下时，根据isCancelEditingKeyEvent决定是否取消编辑
				mxEvent.consume(evt);
                // 中文注释：消费Escape事件
			}
		}
	}));

	// Keypress only fires if printable key was pressed and handles removing the empty placeholder
    // 中文注释：仅在按下可打印键时触发keypress，处理空占位文本的移除
	var keypressHandler = mxUtils.bind(this, function(evt)
	{
		if (this.editingCell != null)
		{
			// Clears the initial empty label on the first keystroke
			// and workaround for FF which fires keypress for delete and backspace
            // 中文注释：首次按键时清除空标签，处理Firefox中删除和退格键的keypress事件
			if (this.clearOnChange && elt.innerHTML == this.getEmptyLabelText() &&
				(!mxClient.IS_FF || (evt.keyCode != 8 /* Backspace */ && evt.keyCode != 46 /* Delete */)))
			{
				this.clearOnChange = false;
				elt.innerHTML = '';
                // 中文注释：清除占位文本并重置clearOnChange标志
			}
		}
	});

	mxEvent.addListener(elt, 'keypress', keypressHandler);
	mxEvent.addListener(elt, 'paste', keypressHandler);
    // 中文注释：为keypress和paste事件绑定处理器，处理占位文本清除

	// Handler for updating the empty label text value after a change
    // 中文注释：处理文本更改后更新空标签值
	var keyupHandler = mxUtils.bind(this, function(evt)
	{
		if (this.editingCell != null)
		{
			// Uses an optional text value for sempty labels which is cleared
			// when the first keystroke appears. This makes it easier to see
			// that a label is being edited even if the label is empty.
			// In Safari and FF, an empty text is represented by <BR> which isn't enough to force a valid size
            // 中文注释：当首次按键时清除空标签的占位文本，Safari和Firefox中空文本用<BR>表示
			if (this.textarea.innerHTML.length == 0 || this.textarea.innerHTML == '<br>')
			{
				this.textarea.innerHTML = this.getEmptyLabelText();
				this.clearOnChange = this.textarea.innerHTML.length > 0;
                // 中文注释：如果textarea为空，显示占位文本并设置clearOnChange
			}
			else
			{
				this.clearOnChange = false;
                // 中文注释：如果textarea非空，重置clearOnChange标志
			}
		}
	});

	mxEvent.addListener(elt, (!mxClient.IS_IE11 && !mxClient.IS_IE) ? 'input' : 'keyup', keyupHandler);
	mxEvent.addListener(elt, 'cut', keyupHandler);
	mxEvent.addListener(elt, 'paste', keyupHandler);
    // 中文注释：为input/keyup、cut和paste事件绑定处理器，更新占位文本状态

	// Adds automatic resizing of the textbox while typing using input, keyup and/or DOM change events
    // 中文注释：为输入时自动调整textarea大小添加事件监听
	var evtName = (!mxClient.IS_IE11 && !mxClient.IS_IE) ? 'input' : 'keydown';
    // 中文注释：根据浏览器版本选择input或keydown事件

	var resizeHandler = mxUtils.bind(this, function(evt)
	{
		if (this.editingCell != null && this.autoSize && !mxEvent.isConsumed(evt))
		{
			// Asynchronous is needed for keydown and shows better results for input events overall
			// (ie non-blocking and cases where the offsetWidth/-Height was wrong at this time)
            // 中文注释：异步处理keydown事件以优化input事件的表现
			if (this.resizeThread != null)
			{
				window.clearTimeout(this.resizeThread);
                // 中文注释：清除之前的resize定时器
			}
			
			this.resizeThread = window.setTimeout(mxUtils.bind(this, function()
			{
				this.resizeThread = null;
				this.resize();
                // 中文注释：设置定时器异步调用resize方法，调整编辑器大小
			}), 0);
		}
	});
	
	mxEvent.addListener(elt, evtName, resizeHandler);
	mxEvent.addListener(window, 'resize', resizeHandler);
    // 中文注释：为指定事件和窗口resize事件绑定resize处理器

	if (document.documentMode >= 9)
	{
		mxEvent.addListener(elt, 'DOMNodeRemoved', resizeHandler);
		mxEvent.addListener(elt, 'DOMNodeInserted', resizeHandler);
        // 中文注释：IE9及以上版本为DOM节点增删事件绑定resize处理器
	}
	else
	{
		mxEvent.addListener(elt, 'cut', resizeHandler);
		mxEvent.addListener(elt, 'paste', resizeHandler);
        // 中文注释：其他浏览器为cut和paste事件绑定resize处理器
	}
};

/**
 * Function: isStopEditingEvent
 * 
 * Returns true if the given keydown event should stop cell editing. This
 * returns true if F2 is pressed of if <mxGraph.enterStopsCellEditing> is true
 * and enter is pressed without control or shift.
 */
 // 中文注释：isStopEditingEvent 方法
 // 功能：判断键盘事件是否应停止单元格编辑
 // 参数：
 //   evt - 键盘事件
 // 事件处理逻辑：F2键或（enterStopsCellEditing为true且Enter键无修饰键时）返回true
mxCellEditor.prototype.isStopEditingEvent = function(evt)
{
	return evt.keyCode == 113 /* F2 */ || (this.graph.isEnterStopsCellEditing() &&
		evt.keyCode == 13 /* Enter */ && !mxEvent.isControlDown(evt) &&
		!mxEvent.isShiftDown(evt));
    // 中文注释：检查F2或Enter键（无Ctrl/Shift）是否触发停止编辑
};

/**
 * Function: isEventSource
 * 
 * Returns true if this editor is the source for the given native event.
 */
 // 中文注释：isEventSource 方法
 // 功能：判断编辑器是否为指定原生事件的来源
 // 参数：
 //   evt - 原生事件
 // 返回：如果事件来源是textarea，返回true
mxCellEditor.prototype.isEventSource = function(evt)
{
	return mxEvent.getSource(evt) == this.textarea;
    // 中文注释：检查事件来源是否为textarea
};

/**
 * Function: resize
 * 
 * Returns <modified>.
 */
 // 中文注释：resize 方法
 // 功能：调整编辑器的大小和位置以匹配单元格状态
 // 关键步骤：根据单元格类型（边或顶点）、缩放比例和样式调整textarea的尺寸和位置
 // 特殊处理：处理自动换行、不同浏览器的兼容性问题
mxCellEditor.prototype.resize = function()
{
	var state = this.graph.getView().getState(this.editingCell);
    // 中文注释：获取当前编辑单元格的状态

	if (state == null)
	{
		this.stopEditing(true);
        // 中文注释：如果状态为空，停止编辑并取消
	}
	else if (this.textarea != null)
	{
		var isEdge = this.graph.getModel().isEdge(state.cell);
 		var scale = this.graph.getView().scale;
 		var m = null;
        // 中文注释：检查是否为边，获取缩放比例，初始化对齐点变量

		if (!this.autoSize || (state.style[mxConstants.STYLE_OVERFLOW] == 'fill'))
		{
			// Specifies the bounds of the editor box
            // 中文注释：指定编辑器框的边界
			this.bounds = this.getEditorBounds(state);
            // 中文注释：获取编辑器边界
			this.textarea.style.width = Math.round(this.bounds.width / scale) + 'px';
			this.textarea.style.height = Math.round(this.bounds.height / scale) + 'px';
            // 中文注释：根据边界和缩放比例设置textarea的宽高

			// FIXME: Offset when scaled
            // 中文注释：注意：缩放时的偏移问题待修复
			if (document.documentMode == 8 || mxClient.IS_QUIRKS)
			{
				this.textarea.style.left = Math.round(this.bounds.x) + 'px';
				this.textarea.style.top = Math.round(this.bounds.y) + 'px';
                // 中文注释：IE8或quirks模式下直接设置绝对位置
			}
			else
			{
				this.textarea.style.left = Math.max(0, Math.round(this.bounds.x + 1)) + 'px';
				this.textarea.style.top = Math.max(0, Math.round(this.bounds.y + 1)) + 'px';
                // 中文注释：其他浏览器设置位置，增加1像素偏移并确保非负
			}
			
			// Installs native word wrapping and avoids word wrap for empty label placeholder
            // 中文注释：启用原生自动换行，空占位文本时避免换行
			if (this.graph.isWrapping(state.cell) && (this.bounds.width >= 2 || this.bounds.height >= 2) &&
				this.textarea.innerHTML != this.getEmptyLabelText())
			{
				this.textarea.style.wordWrap = mxConstants.WORD_WRAP;
				this.textarea.style.whiteSpace = 'normal';
                // 中文注释：启用自动换行并设置正常空白处理

				if (state.style[mxConstants.STYLE_OVERFLOW] != 'fill')
				{
					this.textarea.style.width = Math.round(this.bounds.width / scale) + this.wordWrapPadding + 'px';
                    // 中文注释：非填充模式下，考虑wordWrapPadding调整宽度
				}
			}
			else
			{
				this.textarea.style.whiteSpace = 'nowrap';
                // 中文注释：禁用自动换行

				if (state.style[mxConstants.STYLE_OVERFLOW] != 'fill')
				{
					this.textarea.style.width = '';
                    // 中文注释：非填充模式下清除宽度设置
				}
			}
		}
		else
	 	{
	 		var lw = mxUtils.getValue(state.style, mxConstants.STYLE_LABEL_WIDTH, null);
			m = (state.text != null && this.align == null) ? state.text.margin : null;
            // 中文注释：获取标签宽度样式，计算对齐点

			if (m == null)
			{
				m = mxUtils.getAlignmentAsPoint(this.align || mxUtils.getValue(state.style, mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER),
						mxUtils.getValue(state.style, mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE));
                // 中文注释：如果对齐点为空，基于样式计算对齐点
			}
			
	 		if (isEdge)
			{
				this.bounds = new mxRectangle(state.absoluteOffset.x, state.absoluteOffset.y, 0, 0);
                // 中文注释：边单元格使用绝对偏移初始化边界

				if (lw != null)
			 	{
					var tmp = (parseFloat(lw) + 2) * scale;
					this.bounds.width = tmp;
					this.bounds.x += m.x * tmp;
                    // 中文注释：如果指定了标签宽度，调整边界宽度和x坐标
			 	}
			}
			else
			{
				var bds = mxRectangle.fromRectangle(state);
				var hpos = mxUtils.getValue(state.style, mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER);
				var vpos = mxUtils.getValue(state.style, mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
                // 中文注释：顶点单元格基于状态创建边界，获取水平和垂直标签位置

				bds = (state.shape != null && hpos == mxConstants.ALIGN_CENTER && vpos == mxConstants.ALIGN_MIDDLE) ? state.shape.getLabelBounds(bds) : bds;
                // 中文注释：如果形状存在且标签居中，调用形状的getLabelBounds方法

			 	if (lw != null)
			 	{
			 		bds.width = parseFloat(lw) * scale;
                    // 中文注释：如果指定了标签宽度，调整边界宽度
			 	}
			 	
			 	if (!state.view.graph.cellRenderer.legacySpacing || state.style[mxConstants.STYLE_OVERFLOW] != 'width')
			 	{
					var spacing = parseInt(state.style[mxConstants.STYLE_SPACING] || 2) * scale;
					var spacingTop = (parseInt(state.style[mxConstants.STYLE_SPACING_TOP] || 0) + mxText.prototype.baseSpacingTop) * scale + spacing;
					var spacingRight = (parseInt(state.style[mxConstants.STYLE_SPACING_RIGHT] || 0) + mxText.prototype.baseSpacingRight) * scale + spacing;
					var spacingBottom = (parseInt(state.style[mxConstants.STYLE_SPACING_BOTTOM] || 0) + mxText.prototype.baseSpacingBottom) * scale + spacing;
					var spacingLeft = (parseInt(state.style[mxConstants.STYLE_SPACING_LEFT] || 0) + mxText.prototype.baseSpacingLeft) * scale + spacing;
                    // 中文注释：计算各方向间距，考虑缩放比例和基本间距

					var hpos = mxUtils.getValue(state.style, mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER);
					var vpos = mxUtils.getValue(state.style, mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
                    // 中文注释：重新获取水平和垂直标签位置

					bds = new mxRectangle(bds.x + spacingLeft, bds.y + spacingTop,
						bds.width - ((hpos == mxConstants.ALIGN_CENTER && lw == null) ? (spacingLeft + spacingRight) : 0),
						bds.height - ((vpos == mxConstants.ALIGN_MIDDLE) ? (spacingTop + spacingBottom) : 0));
                    // 中文注释：调整边界，考虑间距和对齐方式
			 	}

				this.bounds = new mxRectangle(bds.x + state.absoluteOffset.x, bds.y + state.absoluteOffset.y, bds.width, bds.height);
                // 中文注释：创建最终边界，应用绝对偏移
			}

			// Needed for word wrap inside text blocks with oversize lines to match the final result where
	 		// the width of the longest line is used as the reference for text alignment in the cell
	 		// TODO: Fix word wrapping preview for edge labels in helloworld.html
            // 中文注释：为超长文本块启用自动换行，确保与最终结果一致，注意：边标签的换行预览需修复
			if (this.graph.isWrapping(state.cell) && (this.bounds.width >= 2 || this.bounds.height >= 2) &&
				this.textarea.innerHTML != this.getEmptyLabelText())
			{
				this.textarea.style.wordWrap = mxConstants.WORD_WRAP;
				this.textarea.style.whiteSpace = 'normal';
                // 中文注释：启用自动换行并设置正常空白处理

		 		// Forces automatic reflow if text is removed from an oversize label and normal word wrap
                // 中文注释：当移除超大标签文本时强制自动重排
				var tmp = Math.round(this.bounds.width / ((document.documentMode == 8) ? scale : scale)) + this.wordWrapPadding;
                // 中文注释：计算宽度，考虑wordWrapPadding和缩放

				if (this.textarea.style.position != 'relative')
				{
					this.textarea.style.width = tmp + 'px';
                    // 中文注释：非相对定位时设置宽度

					if (this.textarea.scrollWidth > tmp)
					{
						this.textarea.style.width = this.textarea.scrollWidth + 'px';
                        // 中文注释：如果滚动宽度较大，调整为滚动宽度
					}
				}
				else
				{
					this.textarea.style.maxWidth = tmp + 'px';
                    // 中文注释：相对定位时设置最大宽度
				}
			}
			else
			{
				// KNOWN: Trailing cursor in IE9 quirks mode is not visible
                // 中文注释：已知问题：IE9 quirks模式下尾随光标不可见
				this.textarea.style.whiteSpace = 'nowrap';
				this.textarea.style.width = '';
                // 中文注释：禁用自动换行并清除宽度
			}
			
			// LATER: Keep in visible area, add fine tuning for pixel precision
            // 中文注释：待优化：保持在可见区域，增加像素精度调整
			// Workaround for wrong measuring in IE8 standards
            // 中文注释：IE8标准模式下测量错误的解决方法
			if (document.documentMode == 8)
			{
				this.textarea.style.zoom = '1';
				this.textarea.style.height = 'auto';
                // 中文注释：IE8下设置缩放为1，高度自动
			}
			
			var ow = this.textarea.scrollWidth;
			var oh = this.textarea.scrollHeight;
            // 中文注释：获取textarea的滚动宽度和高度

			// TODO: Update CSS width and height if smaller than minResize or remove minResize
            // 中文注释：待办：如果尺寸小于minResize，更新CSS宽高或移除minResize
			//if (this.minResize != null)
			//{
			//	ow = Math.max(ow, this.minResize.width);
			//	oh = Math.max(oh, this.minResize.height);
			//}
            // 中文注释：注释掉的代码用于确保宽高不小于minResize

			// LATER: Keep in visible area, add fine tuning for pixel precision
            // 中文注释：待优化：保持在可见区域，增加像素精度调整
			if (document.documentMode == 8)
			{
				// LATER: Scaled wrapping and position is wrong in IE8
                // 中文注释：待修复：IE8下缩放换行和定位错误
				this.textarea.style.left = Math.max(0, Math.ceil((this.bounds.x - m.x * (this.bounds.width - (ow + 1) * scale) + ow * (scale - 1) * 0 + (m.x + 0.5) * 2) / scale)) + 'px';
				this.textarea.style.top = Math.max(0, Math.ceil((this.bounds.y - m.y * (this.bounds.height - (oh + 0.5) * scale) + oh * (scale - 1) * 0 + Math.abs(m.y + 0.5) * 1) / scale)) + 'px';
				// Workaround for wrong event handling width and height
                // 中文注释：IE8下调整位置，考虑对齐点和缩放
				this.textarea.style.width = Math.round(ow * scale) + 'px';
				this.textarea.style.height = Math.round(oh * scale) + 'px';
                // 中文注释：设置缩放后的宽高
			}
			else if (mxClient.IS_QUIRKS)
			{			
				this.textarea.style.left = Math.max(0, Math.ceil(this.bounds.x - m.x * (this.bounds.width - (ow + 1) * scale) + ow * (scale - 1) * 0 + (m.x + 0.5) * 2)) + 'px';
				this.textarea.style.top = Math.max(0, Math.ceil(this.bounds.y - m.y * (this.bounds.height - (oh + 0.5) * scale) + oh * (scale - 1) * 0 + Math.abs(m.y + 0.5) * 1)) + 'px';
                // 中文注释：quirks模式下调整位置，类似IE8处理
			}
			else
			{
				this.textarea.style.left = Math.max(0, Math.round(this.bounds.x - m.x * (this.bounds.width - 2)) + 1) + 'px';
				this.textarea.style.top = Math.max(0, Math.round(this.bounds.y - m.y * (this.bounds.height - 4) + ((m.y == -1) ? 3 : 0)) + 1) + 'px';
                // 中文注释：其他浏览器下调整位置，考虑对齐点和微调偏移
			}
	 	}

		if (mxClient.IS_VML)
		{
			this.textarea.style.zoom = scale;
            // 中文注释：VML渲染下应用缩放
		}
		else
		{
			mxUtils.setPrefixedStyle(this.textarea.style, 'transformOrigin', '0px 0px');
			mxUtils.setPrefixedStyle(this.textarea.style, 'transform',
				'scale(' + scale + ',' + scale + ')' + ((m == null) ? '' :
				' translate(' + (m.x * 100) + '%,' + (m.y * 100) + '%)'));
            // 中文注释：非VML渲染下设置CSS变换，应用缩放和平移
		}
	}
};

/**
 * Function: focusLost
 *
 * Called if the textarea has lost focus.
 */
 // 中文注释：focusLost 方法
 // 功能：处理textarea失去焦点的情况
 // 交互逻辑：根据invokesStopCellEditing决定是否取消编辑
mxCellEditor.prototype.focusLost = function()
{
	this.stopEditing(!this.graph.isInvokesStopCellEditing());
    // 中文注释：调用stopEditing，参数基于invokesStopCellEditing决定是否取消
};

/**
 * Function: getBackgroundColor
 * 
 * Returns the background color for the in-place editor. This implementation
 * always returns null.
 */
 // 中文注释：getBackgroundColor 方法
 // 功能：返回原地编辑器的背景颜色
 // 返回：始终返回null
mxCellEditor.prototype.getBackgroundColor = function(state)
{
	return null;
    // 中文注释：固定返回null，无背景颜色设置
};

/**
 * Function: isLegacyEditor
 * 
 * Returns true if max-width is not supported or if the SVG root element in
 * in the graph does not have CSS position absolute. In these cases the text
 * editor must use CSS position absolute to avoid an offset but it will have
 * a less accurate line wrapping width during the text editing preview. This
 * implementation returns true for IE8- and quirks mode or if the CSS position
 * of the SVG element is not absolute.
 */
 // 中文注释：isLegacyEditor 方法
 // 功能：判断是否使用旧版编辑器
 // 返回：如果不支持max-width或SVG根元素非绝对定位，返回true
 // 特殊处理：旧版编辑器使用绝对定位，换行宽度预览精度较低
mxCellEditor.prototype.isLegacyEditor = function()
{
	if (mxClient.IS_VML)
	{
		return true;
        // 中文注释：VML渲染下使用旧版编辑器
	}
	else
	{
		var absoluteRoot = false;
		
		if (mxClient.IS_SVG)
		{
			var root = this.graph.view.getDrawPane().ownerSVGElement;
            // 中文注释：获取SVG根元素

			if (root != null)
			{
				var css = mxUtils.getCurrentStyle(root);
                // 中文注释：获取根元素的CSS样式

				if (css != null)
				{				
					absoluteRoot = css.position == 'absolute';
                    // 中文注释：检查根元素是否为绝对定位
				}
			}
		}
		
		return !absoluteRoot;
        // 中文注释：如果根元素非绝对定位，返回true
	}
};

/**
 * Function: startEditing
 *
 * Starts the editor for the given cell.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to start editing.
 * trigger - Optional mouse event that triggered the editor.
 */
 // 中文注释：startEditing 方法
 // 功能：为指定单元格启动编辑器
 // 参数：
 //   cell - 要编辑的mxCell对象
 //   trigger - 可选的触发编辑的鼠标事件
 // 关键步骤：初始化textarea，设置样式，隐藏原标签，调整大小和焦点
 // 样式设置：根据单元格样式设置字体、颜色、对齐等
 // 交互逻辑：处理占位文本、隐藏原标签、自动调整大小
mxCellEditor.prototype.startEditing = function(cell, trigger)
{
	this.stopEditing(true);
    // 中文注释：停止当前编辑并取消
	this.align = null;
    // 中文注释：重置对齐方式

	// Creates new textarea instance
    // 中文注释：创建新的textarea实例
	if (this.textarea == null)
	{
		this.init();
        // 中文注释：如果textarea为空，调用init初始化
	}
	
	if (this.graph.tooltipHandler != null)
	{
		this.graph.tooltipHandler.hideTooltip();
        // 中文注释：隐藏图的工具提示
	}
	
	var state = this.graph.getView().getState(cell);
    // 中文注释：获取单元格状态

	if (state != null)
	{
		// Configures the style of the in-place editor
        // 中文注释：配置原地编辑器的样式
		var scale = this.graph.getView().scale;
		var size = mxUtils.getValue(state.style, mxConstants.STYLE_FONTSIZE, mxConstants.DEFAULT_FONTSIZE);
		var family = mxUtils.getValue(state.style, mxConstants.STYLE_FONTFAMILY, mxConstants.DEFAULT_FONTFAMILY);
		var color = mxUtils.getValue(state.style, mxConstants.STYLE_FONTCOLOR, 'black');
		var align = mxUtils.getValue(state.style, mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
		var bold = (mxUtils.getValue(state.style, mxConstants.STYLE_FONTSTYLE, 0) &
				mxConstants.FONT_BOLD) == mxConstants.FONT_BOLD;
		var italic = (mxUtils.getValue(state.style, mxConstants.STYLE_FONTSTYLE, 0) &
				mxConstants.FONT_ITALIC) == mxConstants.FONT_ITALIC;
		var txtDecor = [];
        // 中文注释：获取单元格样式（缩放、字体大小、字体族、颜色、对齐、粗体、斜体等）

		if ((mxUtils.getValue(state.style, mxConstants.STYLE_FONTSTYLE, 0) &
				mxConstants.FONT_UNDERLINE) == mxConstants.FONT_UNDERLINE)
		{
			txtDecor.push('underline');
            // 中文注释：如果设置了下划线，添加到文本装饰
		}
		
		if ((mxUtils.getValue(state.style, mxConstants.STYLE_FONTSTYLE, 0) &
				mxConstants.FONT_STRIKETHROUGH) == mxConstants.FONT_STRIKETHROUGH)
		{
			txtDecor.push('line-through');
            // 中文注释：如果设置了删除线，添加到文本装饰
		}
		
		this.textarea.style.lineHeight = (mxConstants.ABSOLUTE_LINE_HEIGHT) ? Math.round(size * mxConstants.LINE_HEIGHT) + 'px' : mxConstants.LINE_HEIGHT;
		this.textarea.style.backgroundColor = this.getBackgroundColor(state);
		this.textarea.style.textDecoration = txtDecor.join(' ');
		this.textarea.style.fontWeight = (bold) ? 'bold' : 'normal';
		this.textarea.style.fontStyle = (italic) ? 'italic' : '';
		this.textarea.style.fontSize = Math.round(size) + 'px';
		this.textarea.style.zIndex = this.zIndex;
		this.textarea.style.fontFamily = family;
		this.textarea.style.textAlign = align;
		this.textarea.style.outline = 'none';
		this.textarea.style.color = color;
        // 中文注释：设置textarea的行高、背景色、文本装饰、字体样式、大小、层级、字体族、对齐和颜色

		var dir = this.textDirection = mxUtils.getValue(state.style, mxConstants.STYLE_TEXT_DIRECTION, mxConstants.DEFAULT_TEXT_DIRECTION);
        // 中文注释：获取文本方向，默认为默认方向

		if (dir == mxConstants.TEXT_DIRECTION_AUTO)
		{
			if (state != null && state.text != null && state.text.dialect != mxConstants.DIALECT_STRICTHTML &&
				!mxUtils.isNode(state.text.value))
			{
				dir = state.text.getAutoDirection();
                // 中文注释：如果为自动方向且非严格HTML，获取文本的自动方向
			}
		}
		
		if (dir == mxConstants.TEXT_DIRECTION_LTR || dir == mxConstants.TEXT_DIRECTION_RTL)
		{
			this.textarea.setAttribute('dir', dir);
            // 中文注释：设置textarea的文本方向（左到右或右到左）
		}
		else
		{
			this.textarea.removeAttribute('dir');
            // 中文注释：移除文本方向属性
		}

		// Sets the initial editing value
        // 中文注释：设置初始编辑值
		this.textarea.innerHTML = this.getInitialValue(state, trigger) || '';
        // 中文注释：获取初始值并设置到textarea
		this.initialValue = this.textarea.innerHTML;
        // 中文注释：存储初始值用于后续比较

		// Uses an optional text value for empty labels which is cleared
		// when the first keystroke appears. This makes it easier to see
		// that a label is being edited even if the label is empty.
        // 中文注释：为空标签设置可选占位文本，首次按键时清除
		if (this.textarea.innerHTML.length == 0 || this.textarea.innerHTML == '<br>')
		{
			this.textarea.innerHTML = this.getEmptyLabelText();
			this.clearOnChange = true;
            // 中文注释：如果textarea为空，显示占位文本并设置clearOnChange
		}
		else
		{
			this.clearOnChange = this.textarea.innerHTML == this.getEmptyLabelText();
            // 中文注释：检查textarea内容是否为占位文本，设置clearOnChange
		}

		this.graph.container.appendChild(this.textarea);
        // 中文注释：将textarea添加到图容器中

		// Update this after firing all potential events that could update the cleanOnChange flag
        // 中文注释：在触发可能更新clearOnChange的事件后更新状态
		this.editingCell = cell;
		this.trigger = trigger;
		this.textNode = null;
        // 中文注释：设置当前编辑单元格、触发事件和清空textNode

		if (state.text != null && this.isHideLabel(state))
		{
			this.textNode = state.text.node;
			this.textNode.style.visibility = 'hidden';
            // 中文注释：如果需要隐藏标签，存储标签节点并设置不可见
		}

		// Workaround for initial offsetHeight not ready for heading in markup
        // 中文注释：解决初始offsetHeight未准备好的问题
		if (this.autoSize && (this.graph.model.isEdge(state.cell) || state.style[mxConstants.STYLE_OVERFLOW] != 'fill'))
		{
			window.setTimeout(mxUtils.bind(this, function()
			{
				this.resize();
                // 中文注释：异步调用resize方法，确保正确调整大小
			}), 0);
		}
		
		this.resize();
        // 中文注释：调用resize方法调整编辑器大小和位置

		// Workaround for NS_ERROR_FAILURE in FF
        // 中文注释：Firefox中NS_ERROR_FAILURE的解决方法
		try
		{
			// Prefers blinking cursor over no selected text if empty
            // 中文注释：优先显示闪烁光标而非无选中文本
			this.textarea.focus();
            // 中文注释：设置焦点到textarea

			if (this.isSelectText() && this.textarea.innerHTML.length > 0 &&
				(this.textarea.innerHTML != this.getEmptyLabelText() || !this.clearOnChange))
			{
				document.execCommand('selectAll', false, null);
                // 中文注释：如果允许选择文本且内容非空，选中全部文本
			}
		}
		catch (e)
		{
			// ignore
            // 中文注释：忽略异常
		}
	}
};

/**
 * Function: isSelectText
 * 
 * Returns <selectText>.
 */
 // 中文注释：isSelectText 方法
 // 功能：返回selectText变量值
 // 返回：是否在编辑开始时选中文本
mxCellEditor.prototype.isSelectText = function()
{
	return this.selectText;
    // 中文注释：返回selectText属性值
};

/**
 * Function: clearSelection
 * 
 * Clears the selection.
 */
 // 中文注释：clearSelection 方法
 // 功能：清除当前文本选择
 // 交互逻辑：兼容不同浏览器清除选择区域
mxCellEditor.prototype.clearSelection = function()
{
	var selection = null;
	
	if (window.getSelection)
	{
		selection = window.getSelection();
        // 中文注释：获取窗口选择对象（现代浏览器）
	}
	else if (document.selection)
	{
		selection = document.selection;
        // 中文注释：获取文档选择对象（旧版IE）
	}
	
	if (selection != null)
	{
		if (selection.empty)
		{
			selection.empty();
            // 中文注释：清除选择（现代浏览器）
		}
		else if (selection.removeAllRanges)
		{
			selection.removeAllRanges();
            // 中文注释：移除所有选择范围（旧版浏览器）
		}
	}
};

/**
 * Function: stopEditing
 *
 * Stops the editor and applies the value if cancel is false.
 */
 // 中文注释：stopEditing 方法
 // 功能：停止编辑器并根据cancel参数决定是否应用新值
 // 参数：
 //   cancel - 是否取消编辑，默认为false
 // 交互逻辑：恢复隐藏标签、应用新值、清理textarea
mxCellEditor.prototype.stopEditing = function(cancel)
{
	cancel = cancel || false;
    // 中文注释：设置默认cancel值为false

	if (this.editingCell != null)
	{
		if (this.textNode != null)
		{
			this.textNode.style.visibility = 'visible';
			this.textNode = null;
            // 中文注释：恢复隐藏标签的可见性并清空textNode
		}

		var state = (!cancel) ? this.graph.view.getState(this.editingCell) : null;
        // 中文注释：如果不取消，获取当前单元格状态

		var initial = this.initialValue;
		this.initialValue = null;
		this.editingCell = null;
		this.trigger = null;
		this.bounds = null;
		this.textarea.blur();
		this.clearSelection();
        // 中文注释：重置初始值、编辑单元格、触发事件、边界，移除焦点并清除选择

		if (this.textarea.parentNode != null)
		{
			this.textarea.parentNode.removeChild(this.textarea);
            // 中文注释：从DOM中移除textarea
		}
		
		if (this.clearOnChange && this.textarea.innerHTML == this.getEmptyLabelText())
		{
			this.textarea.innerHTML = '';
			this.clearOnChange = false;
            // 中文注释：如果clearOnChange为true且内容为占位文本，清空内容
		}

		if (state != null && (this.textarea.innerHTML != initial || this.align != null))
		{
			this.prepareTextarea();
			var value = this.getCurrentValue(state);
            // 中文注释：准备textarea并获取当前值

			this.graph.getModel().beginUpdate();
			try
			{
				if (value != null)
				{
					this.applyValue(state, value);
                    // 中文注释：如果值非空，应用新值
				}
				
				if (this.align != null)
				{
					this.graph.setCellStyles(mxConstants.STYLE_ALIGN, this.align, [state.cell]);
                    // 中文注释：如果对齐方式非空，更新单元格样式
				}
			}
			finally
			{
				this.graph.getModel().endUpdate();
                // 中文注释：结束模型更新
			}
		}
		
		// Forces new instance on next edit for undo history reset
        // 中文注释：强制下次编辑创建新实例，重置撤销历史
		mxEvent.release(this.textarea);
		this.textarea = null;
		this.align = null;
        // 中文注释：释放textarea事件并清空textarea和align
	}
};

/**
 * Function: prepareTextarea
 * 
 * Prepares the textarea for getting its value in <stopEditing>.
 * This implementation removes the extra trailing linefeed in Firefox.
 */
 // 中文注释：prepareTextarea 方法
 // 功能：在stopEditing前准备textarea，获取值
 // 特殊处理：移除Firefox中的多余尾随换行符
mxCellEditor.prototype.prepareTextarea = function()
{
	if (this.textarea.lastChild != null &&
		this.textarea.lastChild.nodeName == 'BR')
	{
		this.textarea.removeChild(this.textarea.lastChild);
        // 中文注释：如果最后一个子节点为BR，移除以避免Firefox中的换行问题
	}
};

/**
 * Function: isHideLabel
 * 
 * Returns true if the label should be hidden while the cell is being
 * edited.
 */
 // 中文注释：isHideLabel 方法
 // 功能：判断在编辑单元格时是否隐藏标签
 // 参数：
 //   state - 单元格状态
 // 返回：始终返回true，表示隐藏标签
mxCellEditor.prototype.isHideLabel = function(state)
{
	return true;
    // 中文注释：固定返回true，编辑时隐藏标签
};

/**
 * Function: getMinimumSize
 * 
 * Returns the minimum width and height for editing the given state.
 */
 // 中文注释：getMinimumSize 方法
 // 功能：返回编辑指定状态的最小宽高
 // 参数：
 //   state - 单元格状态
 // 返回：包含最小宽高的mxRectangle对象
mxCellEditor.prototype.getMinimumSize = function(state)
{
	var scale = this.graph.getView().scale;
    // 中文注释：获取图的缩放比例

	return new mxRectangle(0, 0, (state.text == null) ? 30 : state.text.size * scale + 20,
			(this.textarea.style.textAlign == 'left') ? 120 : 40);
    // 中文注释：返回最小尺寸，宽度基于文本大小，高度基于对齐方式
};

/**
 * Function: getEditorBounds
 * 
 * Returns the <mxRectangle> that defines the bounds of the editor.
 */
 // 中文注释：getEditorBounds 方法
 // 功能：返回定义编辑器边界的mxRectangle对象
 // 参数：
 //   state - 单元格状态
 // 关键步骤：根据单元格类型、样式和间距计算边界
 // 特殊处理：处理边和顶点单元格的不同边界逻辑
mxCellEditor.prototype.getEditorBounds = function(state)
{
	var isEdge = this.graph.getModel().isEdge(state.cell);
	var scale = this.graph.getView().scale;
	var minSize = this.getMinimumSize(state);
	var minWidth = minSize.width;
 	var minHeight = minSize.height;
 	var result = null;
     // 中文注释：检查是否为边，获取缩放比例和最小尺寸

 	if (!isEdge && state.view.graph.cellRenderer.legacySpacing && state.style[mxConstants.STYLE_OVERFLOW] == 'fill')
 	{
 		result = state.shape.getLabelBounds(mxRectangle.fromRectangle(state));
         // 中文注释：非边且使用旧版间距且overflow为fill时，使用形状的标签边界
 	}
 	else
 	{
		var spacing = parseInt(state.style[mxConstants.STYLE_SPACING] || 0) * scale;
		var spacingTop = (parseInt(state.style[mxConstants.STYLE_SPACING_TOP] || 0) + mxText.prototype.baseSpacingTop) * scale + spacing;
		var spacingRight = (parseInt(state.style[mxConstants.STYLE_SPACING_RIGHT] || 0) + mxText.prototype.baseSpacingRight) * scale + spacing;
		var spacingBottom = (parseInt(state.style[mxConstants.STYLE_SPACING_BOTTOM] || 0) + mxText.prototype.baseSpacingBottom) * scale + spacing;
		var spacingLeft = (parseInt(state.style[mxConstants.STYLE_SPACING_LEFT] || 0) + mxText.prototype.baseSpacingLeft) * scale + spacing;
        // 中文注释：计算各方向间距，考虑缩放和基本间距

	 	result = new mxRectangle(state.x, state.y,
	 		 Math.max(minWidth, state.width - spacingLeft - spacingRight),
	 		 Math.max(minHeight, state.height - spacingTop - spacingBottom));
        // 中文注释：创建边界，基于单元格尺寸减去间距
		var hpos = mxUtils.getValue(state.style, mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER);
		var vpos = mxUtils.getValue(state.style, mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
        // 中文注释：获取水平和垂直标签位置

		result = (state.shape != null && hpos == mxConstants.ALIGN_CENTER && vpos == mxConstants.ALIGN_MIDDLE) ? state.shape.getLabelBounds(result) : result;
        // 中文注释：如果形状存在且标签居中，调用形状的getLabelBounds方法

		if (isEdge)
		{
			result.x = state.absoluteOffset.x;
			result.y = state.absoluteOffset.y;
            // 中文注释：边单元格使用绝对偏移设置位置

			if (state.text != null && state.text.boundingBox != null)
			{
				// Workaround for label containing just spaces in which case
				// the bounding box location contains negative numbers 
                // 中文注释：处理仅含空格的标签边界负数问题
				if (state.text.boundingBox.x > 0)
				{
					result.x = state.text.boundingBox.x;
                    // 中文注释：使用文本边界x坐标
				}
				
				if (state.text.boundingBox.y > 0)
				{
					result.y = state.text.boundingBox.y;
                    // 中文注释：使用文本边界y坐标
				}
			}
		}
		else if (state.text != null && state.text.boundingBox != null)
		{
			result.x = Math.min(result.x, state.text.boundingBox.x);
			result.y = Math.min(result.y, state.text.boundingBox.y);
            // 中文注释：顶点单元格使用文本边界调整位置
		}
	
		result.x += spacingLeft;
		result.y += spacingTop;
        // 中文注释：应用左和上间距

		if (state.text != null && state.text.boundingBox != null)
		{
			if (!isEdge)
			{
				result.width = Math.max(result.width, state.text.boundingBox.width);
				result.height = Math.max(result.height, state.text.boundingBox.height);
                // 中文注释：顶点单元格确保边界宽高不小于文本边界
			}
			else
			{
				result.width = Math.max(minWidth, state.text.boundingBox.width);
				result.height = Math.max(minHeight, state.text.boundingBox.height);
                // 中文注释：边单元格确保边界宽高不小于最小尺寸
			}
		}
		
		// Applies the horizontal and vertical label positions
        // 中文注释：应用水平和垂直标签位置
		if (this.graph.getModel().isVertex(state.cell))
		{
			var horizontal = mxUtils.getValue(state.style, mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER);
	
			if (horizontal == mxConstants.ALIGN_LEFT)
			{
				result.x -= state.width;
                // 中文注释：左对齐时调整x坐标
			}
			else if (horizontal == mxConstants.ALIGN_RIGHT)
			{
				result.x += state.width;
                // 中文注释：右对齐时调整x坐标
			}
	
			var vertical = mxUtils.getValue(state.style, mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
	
			if (vertical == mxConstants.ALIGN_TOP)
			{
				result.y -= state.height;
                // 中文注释：顶部对齐时调整y坐标
			}
			else if (vertical == mxConstants.ALIGN_BOTTOM)
			{
				result.y += state.height;
                // 中文注释：底部对齐时调整y坐标
			}
		}
 	}
 	
 	return new mxRectangle(Math.round(result.x), Math.round(result.y), Math.round(result.width), Math.round(result.height));
     // 中文注释：返回四舍五入后的边界矩形
};

/**
 * Function: getEmptyLabelText
 *
 * Returns the initial label value to be used of the label of the given
 * cell is empty. This label is displayed and cleared on the first keystroke.
 * This implementation returns <emptyLabelText>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which a text for an empty editing box should be
 * returned.
 */
 // 中文注释：getEmptyLabelText 方法
 // 功能：返回空单元格的初始标签值
 // 参数：
 //   cell - mxCell对象
 // 返回：emptyLabelText值，首次按键时清除
mxCellEditor.prototype.getEmptyLabelText = function (cell)
{
	return this.emptyLabelText;
    // 中文注释：返回emptyLabelText属性值
};

/**
 * Function: getEditingCell
 *
 * Returns the cell that is currently being edited or null if no cell is
 * being edited.
 */
 // 中文注释：getEditingCell 方法
 // 功能：返回当前正在编辑的单元格
 // 返回：editingCell值，若无编辑则返回null
mxCellEditor.prototype.getEditingCell = function ()
{
	return this.editingCell;
    // 中文注释：返回editingCell属性值
};

/**
 * Function: destroy
 *
 * Destroys the editor and removes all associated resources.
 */
 // 中文注释：destroy 方法
 // 功能：销毁编辑器并移除所有相关资源
 // 关键步骤：释放textarea事件，移除DOM节点，移除监听器
mxCellEditor.prototype.destroy = function ()
{
	if (this.textarea != null)
	{
		mxEvent.release(this.textarea);
        // 中文注释：释放textarea的事件监听

		if (this.textarea.parentNode != null)
		{
			this.textarea.parentNode.removeChild(this.textarea);
            // 中文注释：从DOM中移除textarea
		}
		
		this.textarea = null;
        // 中文注释：清空textarea引用
	}
			
	if (this.changeHandler != null)
	{
		this.graph.getModel().removeListener(this.changeHandler);
		this.changeHandler = null;
        // 中文注释：移除模型更改监听器并清空引用
	}

	if (this.zoomHandler)
	{
		this.graph.view.removeListener(this.zoomHandler);
		this.zoomHandler = null;
        // 中文注释：移除视图缩放监听器并清空引用
	}
};
