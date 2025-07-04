/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxGraph
 *
 * Extends <mxEventSource> to implement a graph component for
 * the browser. This is the main class of the package. To activate
 * panning and connections use <setPanning> and <setConnectable>.
 * For rubberband selection you must create a new instance of
 * <mxRubberband>. The following listeners are added to
 * <mouseListeners> by default:
 * 
 * - <tooltipHandler>: <mxTooltipHandler> that displays tooltips
 * - <panningHandler>: <mxPanningHandler> for panning and popup menus
 * - <connectionHandler>: <mxConnectionHandler> for creating connections
 * - <graphHandler>: <mxGraphHandler> for moving and cloning cells
 * 
 * These listeners will be called in the above order if they are enabled.
 *
 * Background Images:
 * 
 * To display a background image, set the image, image width and
 * image height using <setBackgroundImage>. If one of the
 * above values has changed then the <view>'s <mxGraphView.validate>
 * should be invoked.
 * 
 * Cell Images:
 * 
 * To use images in cells, a shape must be specified in the default
 * vertex style (or any named style). Possible shapes are
 * <mxConstants.SHAPE_IMAGE> and <mxConstants.SHAPE_LABEL>.
 * The code to change the shape used in the default vertex style,
 * the following code is used:
 * 
 * (code)
 * var style = graph.getStylesheet().getDefaultVertexStyle();
 * style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
 * (end)
 * 
 * For the default vertex style, the image to be displayed can be
 * specified in a cell's style using the <mxConstants.STYLE_IMAGE>
 * key and the image URL as a value, for example:
 * 
 * (code)
 * image=http://www.example.com/image.gif
 * (end)
 * 
 * For a named style, the the stylename must be the first element
 * of the cell style:
 * 
 * (code)
 * stylename;image=http://www.example.com/image.gif
 * (end)
 * 
 * A cell style can have any number of key=value pairs added, divided
 * by a semicolon as follows:
 * 
 * (code)
 * [stylename;|key=value;]
 * (end)
 *
 * Labels:
 * 
 * The cell labels are defined by <getLabel> which uses <convertValueToString>
 * if <labelsVisible> is true. If a label must be rendered as HTML markup, then
 * <isHtmlLabel> should return true for the respective cell. If all labels
 * contain HTML markup, <htmlLabels> can be set to true. NOTE: Enabling HTML
 * labels carries a possible security risk (see the section on security in
 * the manual).
 * 
 * If wrapping is needed for a label, then <isHtmlLabel> and <isWrapping> must
 * return true for the cell whose label should be wrapped. See <isWrapping> for
 * an example.
 * 
 * If clipping is needed to keep the rendering of a HTML label inside the
 * bounds of its vertex, then <isClipping> should return true for the
 * respective cell.
 * 
 * By default, edge labels are movable and vertex labels are fixed. This can be
 * changed by setting <edgeLabelsMovable> and <vertexLabelsMovable>, or by
 * overriding <isLabelMovable>.
 *
 * In-place Editing:
 * 
 * In-place editing is started with a doubleclick or by typing F2.
 * Programmatically, <edit> is used to check if the cell is editable
 * (<isCellEditable>) and call <startEditingAtCell>, which invokes
 * <mxCellEditor.startEditing>. The editor uses the value returned
 * by <getEditingValue> as the editing value.
 * 
 * After in-place editing, <labelChanged> is called, which invokes
 * <mxGraphModel.setValue>, which in turn calls
 * <mxGraphModel.valueForCellChanged> via <mxValueChange>.
 * 
 * The event that triggers in-place editing is passed through to the
 * <cellEditor>, which may take special actions depending on the type of the
 * event or mouse location, and is also passed to <getEditingValue>. The event
 * is then passed back to the event processing functions which can perform
 * specific actions based on the trigger event.
 * 
 * Tooltips:
 * 
 * Tooltips are implemented by <getTooltip>, which calls <getTooltipForCell>
 * if a cell is under the mousepointer. The default implementation checks if
 * the cell has a getTooltip function and calls it if it exists. Hence, in order
 * to provide custom tooltips, the cell must provide a getTooltip function, or 
 * one of the two above functions must be overridden.
 * 
 * Typically, for custom cell tooltips, the latter function is overridden as
 * follows:
 * 
 * (code)
 * graph.getTooltipForCell = function(cell)
 * {
 *   var label = this.convertValueToString(cell);
 *   return 'Tooltip for '+label;
 * }
 * (end)
 * 
 * When using a config file, the function is overridden in the mxGraph section
 * using the following entry:
 * 
 * (code)
 * <add as="getTooltipForCell"><![CDATA[
 *   function(cell)
 *   {
 *     var label = this.convertValueToString(cell);
 *     return 'Tooltip for '+label;
 *   }
 * ]]></add>
 * (end)
 * 
 * "this" refers to the graph in the implementation, so for example to check if 
 * a cell is an edge, you use this.getModel().isEdge(cell)
 *
 * For replacing the default implementation of <getTooltipForCell> (rather than 
 * replacing the function on a specific instance), the following code should be 
 * used after loading the JavaScript files, but before creating a new mxGraph 
 * instance using <mxGraph>:
 * 
 * (code)
 * mxGraph.prototype.getTooltipForCell = function(cell)
 * {
 *   var label = this.convertValueToString(cell);
 *   return 'Tooltip for '+label;
 * }
 * (end)
 * 
 * Shapes & Styles:
 * 
 * The implementation of new shapes is demonstrated in the examples. We'll assume
 * that we have implemented a custom shape with the name BoxShape which we want
 * to use for drawing vertices. To use this shape, it must first be registered in
 * the cell renderer as follows:
 * 
 * (code)
 * mxCellRenderer.registerShape('box', BoxShape);
 * (end)
 * 
 * The code registers the BoxShape constructor under the name box in the cell
 * renderer of the graph. The shape can now be referenced using the shape-key in
 * a style definition. (The cell renderer contains a set of additional shapes,
 * namely one for each constant with a SHAPE-prefix in <mxConstants>.)
 *
 * Styles are a collection of key, value pairs and a stylesheet is a collection
 * of named styles. The names are referenced by the cellstyle, which is stored
 * in <mxCell.style> with the following format: [stylename;|key=value;]. The
 * string is resolved to a collection of key, value pairs, where the keys are
 * overridden with the values in the string.
 *
 * When introducing a new shape, the name under which the shape is registered
 * must be used in the stylesheet. There are three ways of doing this:
 * 
 *   - By changing the default style, so that all vertices will use the new
 * 		shape
 *   - By defining a new style, so that only vertices with the respective
 * 		cellstyle will use the new shape
 *   - By using shape=box in the cellstyle's optional list of key, value pairs
 * 		to be overridden
 *
 * In the first case, the code to fetch and modify the default style for
 * vertices is as follows:
 * 
 * (code)
 * var style = graph.getStylesheet().getDefaultVertexStyle();
 * style[mxConstants.STYLE_SHAPE] = 'box';
 * (end)
 * 
 * The code takes the default vertex style, which is used for all vertices that
 * do not have a specific cellstyle, and modifies the value for the shape-key
 * in-place to use the new BoxShape for drawing vertices. This is done by
 * assigning the box value in the second line, which refers to the name of the
 * BoxShape in the cell renderer.
 * 
 * In the second case, a collection of key, value pairs is created and then
 * added to the stylesheet under a new name. In order to distinguish the
 * shapename and the stylename we'll use boxstyle for the stylename:
 * 
 * (code)
 * var style = new Object();
 * style[mxConstants.STYLE_SHAPE] = 'box';
 * style[mxConstants.STYLE_STROKECOLOR] = '#000000';
 * style[mxConstants.STYLE_FONTCOLOR] = '#000000';
 * graph.getStylesheet().putCellStyle('boxstyle', style);
 * (end)
 * 
 * The code adds a new style with the name boxstyle to the stylesheet. To use
 * this style with a cell, it must be referenced from the cellstyle as follows:
 * 
 * (code)
 * var vertex = graph.insertVertex(parent, null, 'Hello, World!', 20, 20, 80, 20,
 * 				'boxstyle');
 * (end)
 * 
 * To summarize, each new shape must be registered in the <mxCellRenderer> with
 * a unique name. That name is then used as the value of the shape-key in a
 * default or custom style. If there are multiple custom shapes, then there
 * should be a separate style for each shape.
 * 
 * Inheriting Styles:
 * 
 * For fill-, stroke-, gradient-, font- and indicatorColors special keywords
 * can be used. The inherit keyword for one of these colors will inherit the
 * color for the same key from the parent cell. The swimlane keyword does the
 * same, but inherits from the nearest swimlane in the ancestor hierarchy.
 * Finally, the indicated keyword will use the color of the indicator as the
 * color for the given key.
 * 
 * Scrollbars:
 * 
 * The <containers> overflow CSS property defines if scrollbars are used to
 * display the graph. For values of 'auto' or 'scroll', the scrollbars will
 * be shown. Note that the <resizeContainer> flag is normally not used
 * together with scrollbars, as it will resize the container to match the
 * size of the graph after each change.
 * 
 * Multiplicities and Validation:
 * 
 * To control the possible connections in mxGraph, <getEdgeValidationError> is
 * used. The default implementation of the function uses <multiplicities>,
 * which is an array of <mxMultiplicity>. Using this class allows to establish
 * simple multiplicities, which are enforced by the graph.
 * 
 * The <mxMultiplicity> uses <mxCell.is> to determine for which terminals it
 * applies. The default implementation of <mxCell.is> works with DOM nodes (XML
 * nodes) and checks if the given type parameter matches the nodeName of the
 * node (case insensitive). Optionally, an attributename and value can be
 * specified which are also checked.
 * 
 * <getEdgeValidationError> is called whenever the connectivity of an edge
 * changes. It returns an empty string or an error message if the edge is
 * invalid or null if the edge is valid. If the returned string is not empty
 * then it is displayed as an error message.
 * 
 * <mxMultiplicity> allows to specify the multiplicity between a terminal and
 * its possible neighbors. For example, if any rectangle may only be connected
 * to, say, a maximum of two circles you can add the following rule to
 * <multiplicities>:
 * 
 * (code)
 * graph.multiplicities.push(new mxMultiplicity(
 *   true, 'rectangle', null, null, 0, 2, ['circle'],
 *   'Only 2 targets allowed',
 *   'Only shape targets allowed'));
 * (end)
 * 
 * This will display the first error message whenever a rectangle is connected
 * to more than two circles and the second error message if a rectangle is
 * connected to anything but a circle.
 * 
 * For certain multiplicities, such as a minimum of 1 connection, which cannot
 * be enforced at cell creation time (unless the cell is created together with
 * the connection), mxGraph offers <validate> which checks all multiplicities
 * for all cells and displays the respective error messages in an overlay icon
 * on the cells.
 * 
 * If a cell is collapsed and contains validation errors, a respective warning
 * icon is attached to the collapsed cell.
 * 
 * Auto-Layout:
 * 
 * For automatic layout, the <getLayout> hook is provided in <mxLayoutManager>.
 * It can be overridden to return a layout algorithm for the children of a
 * given cell.
 * 
 * Unconnected edges:
 * 
 * The default values for all switches are designed to meet the requirements of
 * general diagram drawing applications. A very typical set of settings to
 * avoid edges that are not connected is the following:
 * 
 * (code)
 * graph.setAllowDanglingEdges(false);
 * graph.setDisconnectOnMove(false);
 * (end)
 * 
 * Setting the <cloneInvalidEdges> switch to true is optional. This switch
 * controls if edges are inserted after a copy, paste or clone-drag if they are
 * invalid. For example, edges are invalid if copied or control-dragged without 
 * having selected the corresponding terminals and allowDanglingEdges is
 * false, in which case the edges will not be cloned if the switch is false.
 * 
 * Output:
 * 
 * To produce an XML representation for a diagram, the following code can be
 * used.
 * 
 * (code)
 * var enc = new mxCodec(mxUtils.createXmlDocument());
 * var node = enc.encode(graph.getModel());
 * (end)
 * 
 * This will produce an XML node than can be handled using the DOM API or
 * turned into a string representation using the following code:
 * 
 * (code)
 * var xml = mxUtils.getXml(node);
 * (end)
 * 
 * To obtain a formatted string, mxUtils.getPrettyXml can be used instead.
 * 
 * This string can now be stored in a local persistent storage (for example
 * using Google Gears) or it can be passed to a backend using mxUtils.post as
 * follows. The url variable is the URL of the Java servlet, PHP page or HTTP
 * handler, depending on the server.
 * 
 * (code)
 * var xmlString = encodeURIComponent(mxUtils.getXml(node));
 * mxUtils.post(url, 'xml='+xmlString, function(req)
 * {
 *   // Process server response using req of type mxXmlRequest
 * });
 * (end)
 * 
 * Input:
 * 
 * To load an XML representation of a diagram into an existing graph object
 * mxUtils.load can be used as follows. The url variable is the URL of the Java
 * servlet, PHP page or HTTP handler that produces the XML string.
 * 
 * (code)
 * var xmlDoc = mxUtils.load(url).getXml();
 * var node = xmlDoc.documentElement;
 * var dec = new mxCodec(node.ownerDocument);
 * dec.decode(node, graph.getModel());
 * (end)
 * 
 * For creating a page that loads the client and a diagram using a single
 * request please refer to the deployment examples in the backends.
 * 
 * Functional dependencies:
 * 
 * (see images/callgraph.png)
 * 
 * Resources:
 *
 * resources/graph - Language resources for mxGraph
 *
 * Group: Events
 * 
 * Event: mxEvent.ROOT
 * 
 * Fires if the root in the model has changed. This event has no properties.
 * 
 * Event: mxEvent.ALIGN_CELLS
 * 
 * Fires between begin- and endUpdate in <alignCells>. The <code>cells</code>
 * and <code>align</code> properties contain the respective arguments that were
 * passed to <alignCells>.
 *
 * Event: mxEvent.FLIP_EDGE
 *
 * Fires between begin- and endUpdate in <flipEdge>. The <code>edge</code>
 * property contains the edge passed to <flipEdge>.
 * 
 * Event: mxEvent.ORDER_CELLS
 * 
 * Fires between begin- and endUpdate in <orderCells>. The <code>cells</code>
 * and <code>back</code> properties contain the respective arguments that were
 * passed to <orderCells>.
 *
 * Event: mxEvent.CELLS_ORDERED
 *
 * Fires between begin- and endUpdate in <cellsOrdered>. The <code>cells</code>
 * and <code>back</code> arguments contain the respective arguments that were
 * passed to <cellsOrdered>.
 * 
 * Event: mxEvent.GROUP_CELLS
 * 
 * Fires between begin- and endUpdate in <groupCells>. The <code>group</code>,
 * <code>cells</code> and <code>border</code> arguments contain the respective
 * arguments that were passed to <groupCells>.
 * 
 * Event: mxEvent.UNGROUP_CELLS
 * 
 * Fires between begin- and endUpdate in <ungroupCells>. The <code>cells</code>
 * property contains the array of cells that was passed to <ungroupCells>.
 * 
 * Event: mxEvent.REMOVE_CELLS_FROM_PARENT
 * 
 * Fires between begin- and endUpdate in <removeCellsFromParent>. The
 * <code>cells</code> property contains the array of cells that was passed to
 * <removeCellsFromParent>.
 * 
 * Event: mxEvent.ADD_CELLS
 * 
 * Fires between begin- and endUpdate in <addCells>. The <code>cells</code>,
 * <code>parent</code>, <code>index</code>, <code>source</code> and
 * <code>target</code> properties contain the respective arguments that were
 * passed to <addCells>.
 * 
 * Event: mxEvent.CELLS_ADDED
 * 
 * Fires between begin- and endUpdate in <cellsAdded>. The <code>cells</code>,
 * <code>parent</code>, <code>index</code>, <code>source</code>,
 * <code>target</code> and <code>absolute</code> properties contain the
 * respective arguments that were passed to <cellsAdded>.
 * 
 * Event: mxEvent.REMOVE_CELLS
 * 
 * Fires between begin- and endUpdate in <removeCells>. The <code>cells</code>
 * and <code>includeEdges</code> arguments contain the respective arguments
 * that were passed to <removeCells>.
 * 
 * Event: mxEvent.CELLS_REMOVED
 * 
 * Fires between begin- and endUpdate in <cellsRemoved>. The <code>cells</code>
 * argument contains the array of cells that was removed.
 * 
 * Event: mxEvent.SPLIT_EDGE
 * 
 * Fires between begin- and endUpdate in <splitEdge>. The <code>edge</code>
 * property contains the edge to be splitted, the <code>cells</code>,
 * <code>newEdge</code>, <code>dx</code> and <code>dy</code> properties contain
 * the respective arguments that were passed to <splitEdge>.
 * 
 * Event: mxEvent.TOGGLE_CELLS
 * 
 * Fires between begin- and endUpdate in <toggleCells>. The <code>show</code>,
 * <code>cells</code> and <code>includeEdges</code> properties contain the
 * respective arguments that were passed to <toggleCells>.
 * 
 * Event: mxEvent.FOLD_CELLS
 * 
 * Fires between begin- and endUpdate in <foldCells>. The
 * <code>collapse</code>, <code>cells</code> and <code>recurse</code>
 * properties contain the respective arguments that were passed to <foldCells>.
 * 
 * Event: mxEvent.CELLS_FOLDED
 * 
 * Fires between begin- and endUpdate in cellsFolded. The
 * <code>collapse</code>, <code>cells</code> and <code>recurse</code>
 * properties contain the respective arguments that were passed to
 * <cellsFolded>.
 * 
 * Event: mxEvent.UPDATE_CELL_SIZE
 * 
 * Fires between begin- and endUpdate in <updateCellSize>. The
 * <code>cell</code> and <code>ignoreChildren</code> properties contain the
 * respective arguments that were passed to <updateCellSize>.
 * 
 * Event: mxEvent.RESIZE_CELLS
 * 
 * Fires between begin- and endUpdate in <resizeCells>. The <code>cells</code>
 * and <code>bounds</code> properties contain the respective arguments that
 * were passed to <resizeCells>.
 * 
 * Event: mxEvent.CELLS_RESIZED
 * 
 * Fires between begin- and endUpdate in <cellsResized>. The <code>cells</code>
 * and <code>bounds</code> properties contain the respective arguments that
 * were passed to <cellsResized>.
 * 
 * Event: mxEvent.MOVE_CELLS
 * 
 * Fires between begin- and endUpdate in <moveCells>. The <code>cells</code>,
 * <code>dx</code>, <code>dy</code>, <code>clone</code>, <code>target</code>
 * and <code>event</code> properties contain the respective arguments that
 * were passed to <moveCells>.
 * 
 * Event: mxEvent.CELLS_MOVED
 * 
 * Fires between begin- and endUpdate in <cellsMoved>. The <code>cells</code>,
 * <code>dx</code>, <code>dy</code> and <code>disconnect</code> properties
 * contain the respective arguments that were passed to <cellsMoved>.
 * 
 * Event: mxEvent.CONNECT_CELL
 * 
 * Fires between begin- and endUpdate in <connectCell>. The <code>edge</code>,
 * <code>terminal</code> and <code>source</code> properties contain the
 * respective arguments that were passed to <connectCell>.
 * 
 * Event: mxEvent.CELL_CONNECTED
 * 
 * Fires between begin- and endUpdate in <cellConnected>. The
 * <code>edge</code>, <code>terminal</code> and <code>source</code> properties
 * contain the respective arguments that were passed to <cellConnected>.
 * 
 * Event: mxEvent.REFRESH
 * 
 * Fires after <refresh> was executed. This event has no properties.
 *
 * Event: mxEvent.CLICK
 * 
 * Fires in <click> after a click event. The <code>event</code> property
 * contains the original mouse event and <code>cell</code> property contains
 * the cell under the mouse or null if the background was clicked.
 * 
 * Event: mxEvent.DOUBLE_CLICK
 *
 * Fires in <dblClick> after a double click. The <code>event</code> property
 * contains the original mouse event and the <code>cell</code> property
 * contains the cell under the mouse or null if the background was clicked.
 * 
 * Event: mxEvent.GESTURE
 *
 * Fires in <fireGestureEvent> after a touch gesture. The <code>event</code>
 * property contains the original gesture end event and the <code>cell</code>
 * property contains the optional cell associated with the gesture.
 *
 * Event: mxEvent.TAP_AND_HOLD
 *
 * Fires in <tapAndHold> if a tap and hold event was detected. The <code>event</code>
 * property contains the initial touch event and the <code>cell</code> property
 * contains the cell under the mouse or null if the background was clicked.
 *
 * Event: mxEvent.FIRE_MOUSE_EVENT
 *
 * Fires in <fireMouseEvent> before the mouse listeners are invoked. The
 * <code>eventName</code> property contains the event name and the
 * <code>event</code> property contains the <mxMouseEvent>.
 *
 * Event: mxEvent.SIZE
 *
 * Fires after <sizeDidChange> was executed. The <code>bounds</code> property
 * contains the new graph bounds.
 *
 * Event: mxEvent.START_EDITING
 *
 * Fires before the in-place editor starts in <startEditingAtCell>. The
 * <code>cell</code> property contains the cell that is being edited and the
 * <code>event</code> property contains the optional event argument that was
 * passed to <startEditingAtCell>.
 * 
 * Event: mxEvent.EDITING_STARTED
 *
 * Fires after the in-place editor starts in <startEditingAtCell>. The
 * <code>cell</code> property contains the cell that is being edited and the
 * <code>event</code> property contains the optional event argument that was
 * passed to <startEditingAtCell>.
 * 
 * Event: mxEvent.EDITING_STOPPED
 *
 * Fires after the in-place editor stops in <stopEditing>.
 *
 * Event: mxEvent.LABEL_CHANGED
 *
 * Fires between begin- and endUpdate in <cellLabelChanged>. The
 * <code>cell</code> property contains the cell, the <code>value</code>
 * property contains the new value for the cell, the <code>old</code> property
 * contains the old value and the optional <code>event</code> property contains
 * the mouse event that started the edit.
 * 
 * Event: mxEvent.ADD_OVERLAY
 *
 * Fires after an overlay is added in <addCellOverlay>. The <code>cell</code>
 * property contains the cell and the <code>overlay</code> property contains
 * the <mxCellOverlay> that was added.
 *
 * Event: mxEvent.REMOVE_OVERLAY
 *
 * Fires after an overlay is removed in <removeCellOverlay> and
 * <removeCellOverlays>. The <code>cell</code> property contains the cell and
 * the <code>overlay</code> property contains the <mxCellOverlay> that was
 * removed.
 * 
 * Constructor: mxGraph
 * 
 * Constructs a new mxGraph in the specified container. Model is an optional
 * mxGraphModel. If no model is provided, a new mxGraphModel instance is 
 * used as the model. The container must have a valid owner document prior 
 * to calling this function in Internet Explorer. RenderHint is a string to
 * affect the display performance and rendering in IE, but not in SVG-based 
 * browsers. The parameter is mapped to <dialect>, which may 
 * be one of <mxConstants.DIALECT_SVG> for SVG-based browsers, 
 * <mxConstants.DIALECT_STRICTHTML> for fastest display mode,
 * <mxConstants.DIALECT_PREFERHTML> for faster display mode,
 * <mxConstants.DIALECT_MIXEDHTML> for fast and <mxConstants.DIALECT_VML> 
 * for exact display mode (slowest). The dialects are defined in mxConstants.
 * The default values are DIALECT_SVG for SVG-based browsers and
 * DIALECT_MIXED for IE.
 *
 * The possible values for the renderingHint parameter are explained below:
 * 
 * fast - The parameter is based on the fact that the display performance is 
 * highly improved in IE if the VML is not contained within a VML group 
 * element. The lack of a group element only slightly affects the display while 
 * panning, but improves the performance by almost a factor of 2, while keeping 
 * the display sufficiently accurate. This also allows to render certain shapes as HTML 
 * if the display accuracy is not affected, which is implemented by 
 * <mxShape.isMixedModeHtml>. This is the default setting and is mapped to
 * DIALECT_MIXEDHTML.
 * faster - Same as fast, but more expensive shapes are avoided. This is 
 * controlled by <mxShape.preferModeHtml>. The default implementation will 
 * avoid gradients and rounded rectangles, but more significant shapes, such 
 * as rhombus, ellipse, actor and cylinder will be rendered accurately. This 
 * setting is mapped to DIALECT_PREFERHTML.
 * fastest - Almost anything will be rendered in Html. This allows for 
 * rectangles, labels and images. This setting is mapped to
 * DIALECT_STRICTHTML.
 * exact - If accurate panning is required and if the diagram is small (up
 * to 100 cells), then this value should be used. In this mode, a group is 
 * created that contains the VML. This allows for accurate panning and is 
 * mapped to DIALECT_VML.
 *
 * Example:
 * 
 * To create a graph inside a DOM node with an id of graph:
 * (code)
 * var container = document.getElementById('graph');
 * var graph = new mxGraph(container);
 * (end)
 * 
 * Parameters:
 * 
 * container - Optional DOM node that acts as a container for the graph.
 * If this is null then the container can be initialized later using
 * <init>.
 * model - Optional <mxGraphModel> that constitutes the graph data.
 * renderHint - Optional string that specifies the display accuracy and
 * performance. Default is mxConstants.DIALECT_MIXEDHTML (for IE).
 * stylesheet - Optional <mxStylesheet> to be used in the graph.
 */
/**
 * 构造函数：mxGraph
 *
 * 在指定容器中创建一个新的 mxGraph 实例。model 参数是可选的 mxGraphModel，如果未提供，则使用新的 mxGraphModel 实例作为模型。
 * 在 Internet Explorer 中，调用此函数前，容器必须具有有效的 ownerDocument。
 * renderHint 是一个字符串，用于影响 IE 中的显示性能和渲染效果（对基于 SVG 的浏览器无影响）。
 * 该参数映射到 <dialect>，可能的值包括：
 * - mxConstants.DIALECT_SVG：用于基于 SVG 的浏览器
 * - mxConstants.DIALECT_STRICTHTML：最快的显示模式
 * - mxConstants.DIALECT_PREFERHTML：较快的显示模式
 * - mxConstants.DIALECT_MIXEDHTML：快速且默认用于 IE
 * - mxConstants.DIALECT_VML：精确的显示模式（最慢）
 * 默认值：SVG 浏览器为 DIALECT_SVG，IE 为 DIALECT_MIXEDHTML。
 *
 * 参数说明：
 * - container：可选的 DOM 节点，作为图表的容器。如果为 null，可稍后通过 <init> 初始化。
 * - model：可选的 mxGraphModel，定义图表的数据模型。
 * - renderHint：可选字符串，指定 IE 的显示精度和性能，默认值为 mxConstants.DIALECT_MIXEDHTML。
 * - stylesheet：可选的 mxStylesheet，定义图表的样式表。
 */
function mxGraph(container, model, renderHint, stylesheet)
{
	// Initializes the variable in case the prototype has been
	// modified to hold some listeners (which is possible because
	// the createHandlers call is executed regardless of the
	// arguments passed into the ctor).
	this.mouseListeners = null;
    // 中文注释：初始化 mouseListeners 变量，确保即使原型被修改也能正确处理鼠标事件监听器。
    // 说明：此变量用于存储鼠标事件监听器数组，确保 createHandlers 方法能够正确初始化事件处理。

	// Converts the renderHint into a dialect
	this.renderHint = renderHint;
    // 中文注释：将 renderHint 参数转换为对应的 dialect（渲染模式）。
    // 说明：根据浏览器类型和 renderHint 的值，设置图表的渲染模式（dialect），以优化显示性能或精度。

	if (mxClient.IS_SVG)
	{
		this.dialect = mxConstants.DIALECT_SVG;
	}
	else if (renderHint == mxConstants.RENDERING_HINT_EXACT && mxClient.IS_VML)
	{
		this.dialect = mxConstants.DIALECT_VML;
	}
	else if (renderHint == mxConstants.RENDERING_HINT_FASTEST)
	{
		this.dialect = mxConstants.DIALECT_STRICTHTML;
	}
	else if (renderHint == mxConstants.RENDERING_HINT_FASTER)
	{
		this.dialect = mxConstants.DIALECT_PREFERHTML;
	}
	else // default for VML
	{
		this.dialect = mxConstants.DIALECT_MIXEDHTML;
	}
    // 中文注释：根据浏览器类型和 renderHint 设置渲染模式（dialect）。
    // 说明：为 SVG 浏览器设置 DIALECT_SVG，为 VML 浏览器根据 renderHint 设置不同的 HTML 或 VML 模式。
    // 注意事项：SVG 浏览器优先，VML 浏览器根据性能需求选择合适的模式。

	// Initializes the main members that do not require a container
	this.model = (model != null) ? model : new mxGraphModel();
	this.multiplicities = [];
	this.imageBundles = [];
	this.cellRenderer = this.createCellRenderer();
	this.setSelectionModel(this.createSelectionModel());
	this.setStylesheet((stylesheet != null) ? stylesheet : this.createStylesheet());
	this.view = this.createGraphView();
    // 中文注释：初始化图表的主要成员，这些成员无需容器即可创建。
    // 说明：
    // - model：初始化图表的数据模型，默认创建新的 mxGraphModel。
    // - multiplicities：初始化连接约束数组，用于定义连接规则。
    // - imageBundles：初始化图片资源数组。
    // - cellRenderer：创建单元渲染器，负责绘制图表中的单元（顶点和边）。
    // - selectionModel：创建并设置选择模型，管理图表的选中状态。
    // - stylesheet：创建并设置样式表，定义单元的外观样式。
    // - view：创建图表视图，缓存单元的状态。

	// Adds a graph model listener to update the view
	this.graphModelChangeListener = mxUtils.bind(this, function(sender, evt)
	{
		this.graphModelChanged(evt.getProperty('edit').changes);
	});
    // 中文注释：添加图表模型监听器，用于在模型更改时更新视图。
    // 说明：当图表模型发生变化（通过 evt 获取 changes），调用 graphModelChanged 方法更新视图。
    // 事件处理逻辑：监听 mxEvent.CHANGE 事件，确保视图与模型同步。

	this.model.addListener(mxEvent.CHANGE, this.graphModelChangeListener);
    // 中文注释：为模型注册 CHANGE 事件监听器。
    // 说明：将 graphModelChangeListener 绑定到模型的 CHANGE 事件，确保模型变化时触发视图更新。

	// Installs basic event handlers with disabled default settings.
	this.createHandlers();
    // 中文注释：安装基本的事件处理器，并使用默认禁用设置。
    // 说明：调用 createHandlers 方法，初始化默认的事件处理逻辑（如鼠标、键盘等交互）。

	// Initializes the display if a container was specified
	if (container != null)
	{
		this.init(container);
	}
    // 中文注释：如果提供了容器，则初始化图表的显示。
    // 说明：调用 init 方法，将图表绑定到指定的 DOM 容器，并完成初始渲染设置。

	this.view.revalidate();
    // 中文注释：重新验证图表视图。
    // 说明：调用 view 的 revalidate 方法，确保视图的状态与模型一致，完成初始化。
};

/**
 * Installs the required language resources at class
 * loading time.
 */
if (mxLoadResources)
{
	mxResources.add(mxClient.basePath + '/resources/graph');
}
else
{
	mxClient.defaultBundles.push(mxClient.basePath + '/resources/graph');
}
/**
 * 中文注释：加载类所需的语言资源。
 * 说明：将语言资源（位于 /resources/graph）添加到 mxResources 或默认资源包，确保图表的国际化支持。
 */

/**
 * Extends mxEventSource.
 */
mxGraph.prototype = new mxEventSource();
mxGraph.prototype.constructor = mxGraph;
/**
 * 中文注释：扩展 mxEventSource 类。
 * 说明：mxGraph 继承自 mxEventSource，具备事件触发和监听功能，用于处理图表的交互事件。
 */

/**
 * Group: Variables
 */

/**
 * Variable: mouseListeners
 * 
 * Holds the mouse event listeners. See <fireMouseEvent>.
 */
/**
 * 中文注释：mouseListeners 变量
 * 说明：存储鼠标事件监听器的数组，用于在 fireMouseEvent 方法中处理鼠标相关事件（如点击、移动等）。
 */
mxGraph.prototype.mouseListeners = null;

/**
 * Variable: isMouseDown
 * 
 * Holds the state of the mouse button.
 */
/**
 * 中文注释：isMouseDown 变量
 * 说明：表示鼠标按钮是否被按下的状态，用于跟踪鼠标交互的当前状态。
 * 交互逻辑：用于判断鼠标按下/释放状态，影响事件处理逻辑。
 */
mxGraph.prototype.isMouseDown = false;

/**
 * Variable: model
 * 
 * Holds the <mxGraphModel> that contains the cells to be displayed.
 */
/**
 * 中文注释：model 变量
 * 说明：存储 mxGraphModel 实例，包含图表中所有单元（顶点和边）的数据，用于管理图表结构。
 * 关键变量用途：核心数据模型，定义图表的逻辑结构。
 */
mxGraph.prototype.model = null;

/**
 * Variable: view
 * 
 * Holds the <mxGraphView> that caches the <mxCellStates> for the cells.
 */
/**
 * 中文注释：view 变量
 * 说明：存储 mxGraphView 实例，缓存单元的状态（mxCellStates），用于优化图表渲染。
 * 关键变量用途：管理图表的视觉表示，提高渲染效率。
 */
mxGraph.prototype.view = null;

/**
 * Variable: stylesheet
 * 
 * Holds the <mxStylesheet> that defines the appearance of the cells.
 * 
 * 
 * Example:
 * 
 * Use the following code to read a stylesheet into an existing graph.
 * 
 * (code)
 * var req = mxUtils.load('stylesheet.xml');
 * var root = req.getDocumentElement();
 * var dec = new mxCodec(root.ownerDocument);
 * dec.decode(root, graph.stylesheet);
 * (end)
 */
/**
 * 中文注释：stylesheet 变量
 * 说明：存储 mxStylesheet 实例，定义单元的外观样式（如形状、颜色等）。
 * 样式设置：通过键值对形式定义样式，支持从 XML 文件加载样式表。
 */
mxGraph.prototype.stylesheet = null;
	
/**
 * Variable: selectionModel
 * 
 * Holds the <mxGraphSelectionModel> that models the current selection.
 */
/**
 * 中文注释：selectionModel 变量
 * 说明：存储 mxGraphSelectionModel 实例，管理图表的当前选中状态。
 * 交互逻辑：处理单元的选择、取消选择等操作。
 */
mxGraph.prototype.selectionModel = null;

/**
 * Variable: cellEditor
 * 
 * Holds the <mxCellEditor> that is used as the in-place editing.
 */
/**
 * 中文注释：cellEditor 变量
 * 说明：存储 mxCellEditor 实例，用于单元的原地编辑功能（如双击编辑标签）。
 * 交互逻辑：支持通过双击或 F2 键启动编辑，编辑后触发 labelChanged 事件。
 */
mxGraph.prototype.cellEditor = null;

/**
 * Variable: cellRenderer
 * 
 * Holds the <mxCellRenderer> for rendering the cells in the graph.
 */
/**
 * 中文注释：cellRenderer 变量
 * 说明：存储 mxCellRenderer 实例，负责渲染图表中的单元（顶点和边）。
 * 关键变量用途：控制图表的视觉绘制，支持自定义形状注册。
 */
mxGraph.prototype.cellRenderer = null;

/**
 * Variable: multiplicities
 * 
 * An array of <mxMultiplicities> describing the allowed
 * connections in a graph.
 */
/**
 * 中文注释：multiplicities 变量
 * 说明：存储 mxMultiplicity 数组，定义图表中允许的连接规则（如最大连接数、目标类型等）。
 * 用途：用于连接验证，确保图表的连接符合指定约束。
 */
mxGraph.prototype.multiplicities = null;

/**
 * Variable: renderHint
 * 
 * RenderHint as it was passed to the constructor.
 */
/**
 * 中文注释：renderHint 变量
 * 说明：存储构造函数中传入的 renderHint 参数，影响 IE 浏览器的渲染性能和精度。
 * 重要配置参数：支持 fast、faster、fastest 和 exact 模式，映射到对应的 dialect。
 */
mxGraph.prototype.renderHint = null;

/**
 * Variable: dialect
 * 
 * Dialect to be used for drawing the graph. Possible values are all
 * constants in <mxConstants> with a DIALECT-prefix.
 */
/**
 * 中文注释：dialect 变量
 * 说明：定义图表绘制使用的渲染模式（dialect），如 SVG、VML 或 HTML。
 * 重要配置参数：根据浏览器类型和 renderHint 确定，影响渲染性能和精度。
 */
mxGraph.prototype.dialect = null;

/**
 * Variable: gridSize
 * 
 * Specifies the grid size. Default is 10.
 */
/**
 * 中文注释：gridSize 变量
 * 说明：指定网格大小，默认值为 10 像素，用于对齐单元位置（snap 方法）。
 * 重要配置参数：影响图表的对齐精度。
 */
mxGraph.prototype.gridSize = 10;
	
/**
 * Variable: gridEnabled
 * 
 * Specifies if the grid is enabled. This is used in <snap>. Default is
 * true.
 */
/**
 * 中文注释：gridEnabled 变量
 * 说明：指定是否启用网格对齐功能，用于 snap 方法，默认值为 true。
 * 重要配置参数：启用后，单元移动或调整大小时会自动对齐到网格。
 */
mxGraph.prototype.gridEnabled = true;

/**
 * Variable: portsEnabled
 * 
 * Specifies if ports are enabled. This is used in <cellConnected> to update
 * the respective style. Default is true.
 */
/**
 * 中文注释：portsEnabled 变量
 * 说明：指定是否启用连接端口功能，用于 cellConnected 方法更新样式，默认值为 true。
 * 重要配置参数：影响边连接到顶点的样式更新。
 */
mxGraph.prototype.portsEnabled = true;

/**
 * Variable: nativeDblClickEnabled
 * 
 * Specifies if native double click events should be detected. Default is true.
 */
/**
 * 中文注释：nativeDblClickEnabled 变量
 * 说明：指定是否检测原生双击事件，默认值为 true。
 * 交互逻辑：启用后，双击事件将触发原生处理，否则可能由自定义逻辑处理。
 */
mxGraph.prototype.nativeDblClickEnabled = true;

/**
 * Variable: doubleTapEnabled
 * 
 * Specifies if double taps on touch-based devices should be handled as a
 * double click. Default is true.
 */
/**
 * 中文注释：doubleTapEnabled 变量
 * 说明：指定在触摸设备上是否将双击（double tap）视为双击事件，默认值为 true。
 * 交互逻辑：启用后，触摸设备上的快速双击将触发双击事件。
 */
mxGraph.prototype.doubleTapEnabled = true;

/**
 * Variable: doubleTapTimeout
 * 
 * Specifies the timeout for double taps and non-native double clicks. Default
 * is 500 ms.
 */
/**
 * 中文注释：doubleTapTimeout 变量
 * 说明：指定双击和非原生双击事件的超时时间，默认值为 500 毫秒。
 * 重要配置参数：控制双击事件的检测时间窗口。
 */
mxGraph.prototype.doubleTapTimeout = 500;

/**
 * Variable: doubleTapTolerance
 * 
 * Specifies the tolerance for double taps and double clicks in quirks mode.
 * Default is 25 pixels.
 */
/**
 * 中文注释：doubleTapTolerance 变量
 * 说明：指定双击和怪异模式下双击的容差，默认值为 25 像素。
 * 重要配置参数：控制双击检测的坐标范围，防止误判。
 */
mxGraph.prototype.doubleTapTolerance = 25;

/**
 * Variable: lastTouchX
 * 
 * Holds the x-coordinate of the last touch event for double tap detection.
 */
/**
 * 中文注释：lastTouchX 变量
 * 说明：存储上一次触摸事件的 X 坐标，用于双击检测。
 * 交互逻辑：与 doubleTapTolerance 配合，判断是否为有效双击。
 todo 这儿原来代码是Y，应该是写错了
 */
mxGraph.prototype.lastTouchX = 0;

/**
 * Variable: lastTouchY
 * 
 * Holds the y-coordinate of the last touch event for double tap detection.
 */
/**
 * 中文注释：lastTouchY 变量
 * 说明：存储上一次触摸事件的 Y 坐标，用于双击检测。
 * 交互逻辑：与 lastTouchX 一起用于判断双击事件的位置有效性。
 */
mxGraph.prototype.lastTouchY = 0;

/**
 * Variable: lastTouchTime
 * 
 * Holds the time of the last touch event for double click detection.
 */
/**
 * 中文注释：lastTouchTime 变量
 * 说明：存储上一次触摸事件的时间，用于双击检测。
 * 交互逻辑：与 doubleTapTimeout 配合，判断是否在时间窗口内发生双击。
 */
mxGraph.prototype.lastTouchTime = 0;

/**
 * Variable: tapAndHoldEnabled
 * 
 * Specifies if tap and hold should be used for starting connections on touch-based
 * devices. Default is true.
 */
/**
 * 中文注释：tapAndHoldEnabled 变量
 * 说明：指定在触摸设备上是否启用长按（tap and hold）以启动连接，默认值为 true。
 * 交互逻辑：启用后，长按可触发连接创建（如拖动创建边）。
 */
mxGraph.prototype.tapAndHoldEnabled = true;

/**
 * Variable: tapAndHoldDelay
 * 
 * Specifies the time for a tap and hold. Default is 500 ms.
 */
/**
 * 中文注释：tapAndHoldDelay 变量
 * 说明：指定长按（tap and hold）的时间，默认值为 500 毫秒。
 * 重要配置参数：控制长按事件的触发时间。
 */
mxGraph.prototype.tapAndHoldDelay = 500;

/**
 * Variable: tapAndHoldInProgress
 * 
 * True if the timer for tap and hold events is running.
 */
/**
 * 中文注释：tapAndHoldInProgress 变量
 * 说明：表示长按事件计时器是否正在运行。
 * 交互逻辑：用于跟踪长按事件的处理状态。
 */
mxGraph.prototype.tapAndHoldInProgress = false;

/**
 * Variable: tapAndHoldValid
 * 
 * True as long as the timer is running and the touch events
 * stay within the given <tapAndHoldTolerance>.
 */
/**
 * 中文注释：tapAndHoldValid 变量
 * 说明：表示长按事件计时器运行期间，触摸事件是否在 tapAndHoldTolerance 范围内。
 * 交互逻辑：确保长按期间触摸位置未超出容差范围。
 */
mxGraph.prototype.tapAndHoldValid = false;

/**
 * Variable: initialTouchX
 * 
 * Holds the x-coordinate of the initial touch event for tap and hold.
 */
/**
 * 中文注释：initialTouchX 变量
 * 说明：存储长按事件初始触摸的 X 坐标。
 * 交互逻辑：用于判断长按期间触摸位置是否有效。
 */
mxGraph.prototype.initialTouchX = 0;

/**
 * Variable: initialTouchY
 * 
 * Holds the y-coordinate of the initial touch event for tap and hold.
 */
/**
 * 中文注释：initialTouchY 变量
 * 说明：存储长按事件初始触摸的 Y 坐标。
 * 交互逻辑：与 initialTouchX 配合，判断长按事件的有效性。
 */
mxGraph.prototype.initialTouchY = 0;

/**
 * Variable: tolerance
 * 
 * Tolerance for a move to be handled as a single click.
 * Default is 4 pixels.
 */
/**
 * 中文注释：tolerance 变量
 * 说明：指定移动被视为单击的容差，默认值为 4 像素。
 * 重要配置参数：控制鼠标或触摸移动的范围，判断是否为有效单击。
 */
mxGraph.prototype.tolerance = 4;

/**
 * Variable: defaultOverlap
 * 
 * Value returned by <getOverlap> if <isAllowOverlapParent> returns
 * true for the given cell. <getOverlap> is used in <constrainChild> if
 * <isConstrainChild> returns true. The value specifies the
 * portion of the child which is allowed to overlap the parent.
 */
/**
 * 中文注释：defaultOverlap 变量
 * 说明：指定子单元允许与父单元重叠的比例，默认值为 0.5。
 * 用途：当 isAllowOverlapParent 返回 true 时，getOverlap 使用此值约束子单元位置。
 */
mxGraph.prototype.defaultOverlap = 0.5;

/**
 * Variable: defaultParent
 * 
 * Specifies the default parent to be used to insert new cells.
 * This is used in <getDefaultParent>. Default is null.
 */
/**
 * 中文注释：defaultParent 变量
 * 说明：指定插入新单元时使用的默认父单元，默认值为 null。
 * 用途：getDefaultParent 方法返回此值，作为新单元的默认容器。
 */
mxGraph.prototype.defaultParent = null;

/**
 * Variable: alternateEdgeStyle
 * 
 * Specifies the alternate edge style to be used if the main control point
 * on an edge is being doubleclicked. Default is null.
 */
/**
 * 中文注释：alternateEdgeStyle 变量
 * 说明：指定边的主控制点被双击时使用的备用样式，默认值为 null。
 * 交互逻辑：支持通过双击切换边的样式。
 */
mxGraph.prototype.alternateEdgeStyle = null;

/**
 * Variable: backgroundImage
 *
 * Specifies the <mxImage> to be returned by <getBackgroundImage>. Default
 * is null.
 * 
 * Example:
 *
 * (code)
 * var img = new mxImage('http://www.example.com/maps/examplemap.jpg', 1024, 768);
 * graph.setBackgroundImage(img);
 * graph.view.validate();
 * (end)
 */
/**
 * 中文注释：backgroundImage 变量
 * 说明：指定图表的背景图片（mxImage 对象），由 getBackgroundImage 方法返回，默认值为 null。
 * 样式设置：通过 setBackgroundImage 设置图片，需调用 view.validate() 刷新显示。
 */
mxGraph.prototype.backgroundImage = null;

/**
 * Variable: pageVisible
 *
 * Specifies if the background page should be visible. Default is false.
 * Not yet implemented.
 */
/**
 * 中文注释：pageVisible 变量
 * 说明：指定背景页面是否可见，默认值为 false（尚未实现）。
 * 注意事项：此功能未完全实现，可能不生效。
 */
mxGraph.prototype.pageVisible = false;

/**
 * Variable: pageBreaksVisible
 * 
 * Specifies if a dashed line should be drawn between multiple pages. Default
 * is false. If you change this value while a graph is being displayed then you
 * should call <sizeDidChange> to force an update of the display.
 */
/**
 * 中文注释：pageBreaksVisible 变量
 * 说明：指定是否在多页之间绘制虚线分隔线，默认值为 false。
 * 注意事项：更改此值后需调用 sizeDidChange 方法刷新显示。
 */
mxGraph.prototype.pageBreaksVisible = false;

/**
 * Variable: pageBreakColor
 * 
 * Specifies the color for page breaks. Default is 'gray'.
 */
/**
 * 中文注释：pageBreakColor 变量
 * 说明：指定页面分隔线的颜色，默认值为 'gray'。
 * 样式设置：影响页面分隔线的视觉效果。
 */
mxGraph.prototype.pageBreakColor = 'gray';

/**
 * Variable: pageBreakDashed
 * 
 * Specifies the page breaks should be dashed. Default is true.
 */
/**
 * 中文注释：pageBreakDashed 变量
 * 说明：指定页面分隔线是否为虚线，默认值为 true。
 * 样式设置：控制页面分隔线的样式（实线或虚线）。
 */
mxGraph.prototype.pageBreakDashed = true;

/**
 * Variable: minPageBreakDist
 * 
 * Specifies the minimum distance for page breaks to be visible. Default is
 * 20 (in pixels).
 */
/**
 * 中文注释：minPageBreakDist 变量
 * 说明：指定页面分隔线可见的最小距离，默认值为 20 像素。
 * 重要配置参数：控制分隔线的显示条件。
 */
mxGraph.prototype.minPageBreakDist = 20;

/**
 * Variable: preferPageSize
 * 
 * Specifies if the graph size should be rounded to the next page number in
 * <sizeDidChange>. This is only used if the graph container has scrollbars.
 * Default is false.
 */
/**
 * 中文注释：preferPageSize 变量
 * 说明：指定图表大小是否四舍五入到下一个页面大小（在 sizeDidChange 中使用），仅在容器有滚动条时生效，默认值为 false。
 * 注意事项：需结合滚动条使用，影响图表尺寸调整。
 */
mxGraph.prototype.preferPageSize = false;

/**
 * Variable: pageFormat
 *
 * Specifies the page format for the background page. Default is
 * <mxConstants.PAGE_FORMAT_A4_PORTRAIT>. This is used as the default in
 * <mxPrintPreview> and for painting the background page if <pageVisible> is
 * true and the pagebreaks if <pageBreaksVisible> is true.
 */
/**
 * 中文注释：pageFormat 变量
 * 说明：指定背景页面的页面格式，默认值为 mxConstants.PAGE_FORMAT_A4_PORTRAIT。
 * 用途：用于 mxPrintPreview 和绘制背景页面（当 pageVisible 和 pageBreaksVisible 为 true 时）。
 */
mxGraph.prototype.pageFormat = mxConstants.PAGE_FORMAT_A4_PORTRAIT;

/**
 * Variable: pageScale
 *
 * Specifies the scale of the background page. Default is 1.5.
 * Not yet implemented.
 */
/**
 * 中文注释：pageScale 变量
 * 说明：指定背景页面的缩放比例，默认值为 1.5（尚未实现）。
 * 注意事项：此功能未完全实现，可能不生效。
 */
mxGraph.prototype.pageScale = 1.5;

/**
 * Variable: enabled
 * 
 * Specifies the return value for <isEnabled>. Default is true.
 */
/**
 * 中文注释：enabled 变量
 * 说明：指定 isEnabled 方法的返回值，默认值为 true。
 * 交互逻辑：控制图表是否启用交互功能。
 */
mxGraph.prototype.enabled = true;

/**
 * Variable: escapeEnabled
 * 
 * Specifies if <mxKeyHandler> should invoke <escape> when the escape key
 * is pressed. Default is true.
 */
/**
 * 中文注释：escapeEnabled 变量
 * 说明：指定按下 Escape 键时是否调用 mxKeyHandler 的 escape 方法，默认值为 true。
 * 交互逻辑：启用后，Escape 键可触发退出操作（如取消编辑）。
 */
mxGraph.prototype.escapeEnabled = true;

/**
 * Variable: invokesStopCellEditing
 * 
 * If true, when editing is to be stopped by way of selection changing,
 * data in diagram changing or other means stopCellEditing is invoked, and
 * changes are saved. This is implemented in a focus handler in
 * <mxCellEditor>. Default is true.
 */
/**
 * 中文注释：invokesStopCellEditing 变量
 * 说明：指定当编辑因选择更改、图表数据变化或其他原因停止时，是否调用 stopCellEditing 并保存更改，默认值为 true。
 * 交互逻辑：在 mxCellEditor 的焦点处理器中实现，确保编辑状态正确终止。
 */
mxGraph.prototype.invokesStopCellEditing = true;

/**
 * Variable: enterStopsCellEditing
 * 
 * If true, pressing the enter key without pressing control or shift will stop
 * editing and accept the new value. This is used in <mxCellEditor> to stop
 * cell editing. Note: You can always use F2 and escape to stop editing.
 * Default is false.
 */
/**
 * 中文注释：enterStopsCellEditing 变量
 * 说明：指定是否在不按 Ctrl 或 Shift 的情况下按 Enter 键停止编辑并接受新值，默认值为 false。
 * 交互逻辑：在 mxCellEditor 中实现，控制 Enter 键的行为。
 * 注意事项：F2 和 Escape 键始终可用于停止编辑。
 */
mxGraph.prototype.enterStopsCellEditing = false;

/**
 * Variable: useScrollbarsForPanning
 * 
 * Specifies if scrollbars should be used for panning in <panGraph> if
 * any scrollbars are available. If scrollbars are enabled in CSS, but no
 * scrollbars appear because the graph is smaller than the container size,
 * then no panning occurs if this is true. Default is true.
 */
/**
 * 中文注释：useScrollbarsForPanning 变量
 * 说明：指定当容器有滚动条时，是否在 panGraph 方法中使用滚动条进行平移，默认值为 true。
 * 注意事项：如果容器无滚动条（图表小于容器），则不进行平移。
 */
mxGraph.prototype.useScrollbarsForPanning = true;

/**
 * Variable: exportEnabled
 * 
 * Specifies the return value for <canExportCell>. Default is true.
 */
/**
 * 中文注释：exportEnabled 变量
 * 说明：指定 canExportCell 方法的返回值，默认值为 true。
 * 用途：控制单元是否允许导出。
 */
mxGraph.prototype.exportEnabled = true;

/**
 * Variable: importEnabled
 * 
 * Specifies the return value for <canImportCell>. Default is true.
 */
/**
 * 中文注释：importEnabled 变量
 * 说明：指定 canImportCell 方法的返回值，默认值为 true。
 * 用途：控制单元是否允许导入。
 */
mxGraph.prototype.importEnabled = true;

/**
 * Variable: cellsLocked
 * 
 * Specifies the return value for <isCellLocked>. Default is false.
 */
/**
 * 中文注释：cellsLocked 变量
 * 说明：指定 isCellLocked 方法的返回值，默认值为 false。
 * 交互逻辑：控制单元是否锁定，锁定后不可编辑或移动。
 */
mxGraph.prototype.cellsLocked = false;

/**
 * Variable: cellsCloneable
 * 
 * Specifies the return value for <isCellCloneable>. Default is true.
 */
/**
 * 中文注释：cellsCloneable 变量
 * 说明：指定 isCellCloneable 方法的返回值，默认值为 true。
 * 交互逻辑：控制单元是否允许克隆。
 */
mxGraph.prototype.cellsCloneable = true;

/**
 * Variable: foldingEnabled
 * 
 * Specifies if folding (collapse and expand via an image icon in the graph
 * should be enabled). Default is true.
 */
/**
 * 中文注释：foldingEnabled 变量
 * 说明：指定是否启用单元的折叠功能（通过图标折叠/展开），默认值为 true。
 * 交互逻辑：启用后，单元可以通过图标折叠或展开。
 */
mxGraph.prototype.foldingEnabled = true;

/**
 * Variable: cellsEditable
 * 
 * Specifies the return value for <isCellEditable>. Default is true.
 */
/**
 * 中文注释：cellsEditable 变量
 * 说明：指定 isCellEditable 方法的返回值，默认值为 true。
 * 交互逻辑：控制单元是否允许编辑。
 */
mxGraph.prototype.cellsEditable = true;
		
/**
 * Variable: cellsDeletable
 * 
 * Specifies the return value for <isCellDeletable>. Default is true.
 */
/**
 * 中文注释：cellsDeletable 变量
 * 说明：指定 isCellDeletable 方法的返回值，默认值为 true。
 * 交互逻辑：控制单元是否允许删除。
 */
mxGraph.prototype.cellsDeletable = true;

/**
 * Variable: cellsMovable
 * 
 * Specifies the return value for <isCellMovable>. Default is true.
 */
/**
 * 中文注释：cellsMovable 变量
 * 说明：指定 isCellMovable 方法的返回值，默认值为 true。
 * 交互逻辑：控制单元是否允许移动。
 */
mxGraph.prototype.cellsMovable = true;
	
/**
 * Variable: edgeLabelsMovable
 * 
 * Specifies the return value for edges in <isLabelMovable>. Default is true.
 */
/**
 * 中文注释：edgeLabelsMovable 变量
 * 说明：指定边的标签是否允许移动（isLabelMovable 方法），默认值为 true。
 * 交互逻辑：控制边标签的拖动行为。
 */
mxGraph.prototype.edgeLabelsMovable = true;
	
/**
 * Variable: vertexLabelsMovable
 * 
 * Specifies the return value for vertices in <isLabelMovable>. Default is false.
 */
/**
 * 中文注释：vertexLabelsMovable 变量
 * 说明：指定顶点的标签是否允许移动（isLabelMovable 方法），默认值为 false。
 * 交互逻辑：默认情况下，顶点标签不可拖动。
 */
mxGraph.prototype.vertexLabelsMovable = false;

/**
 * Variable: dropEnabled
 * 
 * Specifies the return value for <isDropEnabled>. Default is false.
 */
/**
 * 中文注释：dropEnabled 变量
 * 说明：指定 isDropEnabled 方法的返回值，默认值为 false。
 * 交互逻辑：控制是否允许拖放操作（如拖放创建连接）。
 */
mxGraph.prototype.dropEnabled = false;

/**
 * Variable: splitEnabled
 * 
 * Specifies if dropping onto edges should be enabled. This is ignored if
 * <dropEnabled> is false. If enabled, it will call <splitEdge> to carry
 * out the drop operation. Default is true.
 */
/**
 * 中文注释：splitEnabled 变量
 * 说明：指定是否允许将元素拖放到边上（触发 splitEdge 方法），默认值为 true。
 * 交互逻辑：当 dropEnabled 为 true 时，启用后拖放操作可分割边。
 * 注意事项：需 dropEnabled 为 true 才生效。
 */
mxGraph.prototype.splitEnabled = true;

/**
 * Variable: cellsResizable
 * 
 * Specifies the return value for <isCellResizable>. Default is true.
 */
/**
 * 中文注释：cellsResizable 变量
 * 说明：指定 isCellResizable 方法的返回值，默认值为 true。
 * 交互逻辑：控制单元是否允许调整大小。
 */
mxGraph.prototype.cellsResizable = true;

/**
 * Variable: cellsBendable
 * 
 * Specifies the return value for <isCellsBendable>. Default is true.
 */
/**
 * 中文注释：cellsBendable 变量
 * 说明：指定 isCellsBendable 方法的返回值，默认值为 true。
 * 交互逻辑：控制边是否允许弯曲（如调整控制点）。
 */
mxGraph.prototype.cellsBendable = true;

/**
 * Variable: cellsSelectable
 * 
 * Specifies the return value for <isCellSelectable>. Default is true.
 */
/**
 * 中文注释：cellsSelectable 变量
 * 说明：指定 isCellSelectable 方法的返回值，默认值为 true。
 * 交互逻辑：控制单元是否允许被选中。
 */
mxGraph.prototype.cellsSelectable = true;

/**
 * Variable: cellsDisconnectable
 * 
 * Specifies the return value for <isCellDisconntable>. Default is true.
 */
/**
 * 中文注释：cellsDisconnectable 变量
 * 说明：指定 isCellDisconnectable 方法的返回值，默认值为 true。
 * 交互逻辑：控制单元是否允许断开连接。
 */
mxGraph.prototype.cellsDisconnectable = true;

/**
 * Variable: autoSizeCells
 * 
 * Specifies if the graph should automatically update the cell size after an
 * edit. This is used in <isAutoSizeCell>. Default is false.
 */
/**
 * 中文注释：autoSizeCells 变量
 * 说明：指定编辑后是否自动更新单元大小（isAutoSizeCell 方法），默认值为 false。
 * 交互逻辑：控制单元在编辑后是否自动调整尺寸。
 */
mxGraph.prototype.autoSizeCells = false;

/**
 * Variable: autoSizeCellsOnAdd
 * 
 * Specifies if autoSize style should be applied when cells are added. Default is false.
 */
/**
 * 中文注释：autoSizeCellsOnAdd 变量
 * 说明：指定添加单元时是否应用自动调整大小的样式，默认值为 false。
 * 交互逻辑：控制新添加的单元是否自动调整尺寸。
 */
mxGraph.prototype.autoSizeCellsOnAdd = false;

/**
 * Variable: autoScroll
 * 
 * Specifies if the graph should automatically scroll if the mouse goes near
 * the container edge while dragging. This is only taken into account if the
 * container has scrollbars. Default is true.
 * 
 * If you need this to work without scrollbars then set <ignoreScrollbars> to
 * true. Please consult the <ignoreScrollbars> for details. In general, with
 * no scrollbars, the use of <allowAutoPanning> is recommended.
 */
/**
 * 中文注释：autoScroll 变量
 * 说明：指定拖动时鼠标靠近容器边缘是否自动滚动，默认值为 true。
 * 注意事项：仅在容器有滚动条时生效；无滚动条时，建议设置 ignoreScrollbars 为 true 或启用 allowAutoPanning。
 */
mxGraph.prototype.autoScroll = true;

/**
 * Variable: ignoreScrollbars
 * 
 * Specifies if the graph should automatically scroll regardless of the
 * scrollbars. This will scroll the container using positive values for
 * scroll positions (ie usually only rightwards and downwards). To avoid
 * possible conflicts with panning, set <translateToScrollPosition> to true.
 */
/**
 * 中文注释：ignoreScrollbars 变量
 * 说明：指定是否忽略滚动条进行自动滚动，默认值为 false。
 * 注意事项：启用后，容器将使用正值滚动（通常向右或向下）；建议与 translateToScrollPosition 配合使用，避免平移冲突。
 */
mxGraph.prototype.ignoreScrollbars = false;

/**
 * Variable: translateToScrollPosition
 * 
 * Specifies if the graph should automatically convert the current scroll
 * position to a translate in the graph view when a mouseUp event is received.
 * This can be used to avoid conflicts when using <autoScroll> and
 * <ignoreScrollbars> with no scrollbars in the container.
 */
/**
 * 中文注释：translateToScrollPosition 变量
 * 说明：指定在接收到 mouseUp 事件时，是否将当前滚动位置转换为图表视图的平移，默认值为 false。
 * 注意事项：与 autoScroll 和 ignoreScrollbars 配合使用，避免无滚动条时的平移冲突。
 */
mxGraph.prototype.translateToScrollPosition = false;

/**
 * Variable: timerAutoScroll
 * 
 * Specifies if autoscrolling should be carried out via mxPanningManager even
 * if the container has scrollbars. This disables <scrollPointToVisible> and
 * uses <mxPanningManager> instead. If this is true then <autoExtend> is
 * disabled. It should only be used with a scroll buffer or when scollbars
 * are visible and scrollable in all directions. Default is false.
 */
/**
 * 中文注释：timerAutoScroll 变量
 * 说明：指定是否通过 mxPanningManager 进行自动滚动（即使容器有滚动条），默认值为 false。
 * 注意事项：启用后禁用 scrollPointToVisible，使用 mxPanningManager 进行滚动；建议在有滚动缓冲或滚动条可见时使用。
 */
mxGraph.prototype.timerAutoScroll = false;

/**
 * Variable: allowAutoPanning
 * 
 * Specifies if panning via <panGraph> should be allowed to implement autoscroll
 * if no scrollbars are available in <scrollPointToVisible>. To enable panning
 * inside the container, near the edge, set <mxPanningManager.border> to a
 * positive value. Default is false.
 */
/**
 * 中文注释：allowAutoPanning 变量
 * 说明：指定当 scrollPointToVisible 无滚动条时，是否允许通过 panGraph 进行自动平移，默认值为 false。
 * 注意事项：需设置 mxPanningManager.border 为正值以启用容器边缘的平移。
 */
mxGraph.prototype.allowAutoPanning = false;

/**
 * Variable: autoExtend
 * 
 * Specifies if the size of the graph should be automatically extended if the
 * mouse goes near the container edge while dragging. This is only taken into
 * account if the container has scrollbars. Default is true. See <autoScroll>.
 */
/**
 * 中文注释：autoExtend 变量
 * 说明：指定拖动时鼠标靠近容器边缘是否自动扩展图表大小，默认值为 true。
 * 注意事项：仅在容器有滚动条时生效，与 autoScroll 配合使用。
 */
mxGraph.prototype.autoExtend = true;

/**
 * Variable: maximumGraphBounds
 * 
 * <mxRectangle> that specifies the area in which all cells in the diagram
 * should be placed. Uses in <getMaximumGraphBounds>. Use a width or height of
 * 0 if you only want to give a upper, left corner.
 */
/**
 * 中文注释：maximumGraphBounds 变量
 * 说明：指定图表中所有单元的放置区域（mxRectangle 对象），用于 getMaximumGraphBounds 方法。
 * 注意事项：宽度或高度为 0 时，仅指定左上角位置。
 */
mxGraph.prototype.maximumGraphBounds = null;

/**
 * Variable: minimumGraphSize
 * 
 * <mxRectangle> that specifies the minimum size of the graph. This is ignored
 * if the graph container has no scrollbars. Default is null.
 */
/**
 * 中文注释：minimumGraphSize 变量
 * 说明：指定图表的最小尺寸（mxRectangle 对象），无滚动条时忽略，默认值为 null。
 * 用途：限制图表的最小边界。
 */
mxGraph.prototype.minimumGraphSize = null;

/**
 * Variable: minimumContainerSize
 * 
 * <mxRectangle> that specifies the minimum size of the <container> if
 * <resizeContainer> is true.
 */
/**
 * 中文注释：minimumContainerSize 变量
 * 说明：指定容器在 resizeContainer 为 true 时的最小尺寸（mxRectangle 对象）。
 * 用途：限制容器的最小尺寸。
 */
mxGraph.prototype.minimumContainerSize = null;
		
/**
 * Variable: maximumContainerSize
 * 
 * <mxRectangle> that specifies the maximum size of the container if
 * <resizeContainer> is true.
 */
/**
 * 中文注释：maximumContainerSize 变量
 * 说明：指定容器在 resizeContainer 为 true 时的最大尺寸（mxRectangle 对象）。
 * 用途：限制容器的最大尺寸。
 */
mxGraph.prototype.maximumContainerSize = null;

/**
 * Variable: resizeContainer
 * 
 * Specifies if the container should be resized to the graph size when
 * the graph size has changed. Default is false.
 */
/**
 * 中文注释：resizeContainer 变量
 * 说明：指定图表大小变化时是否调整容器大小，默认值为 false。
 * 注意事项：与 minimumContainerSize 和 maximumContainerSize 配合使用。
 */
mxGraph.prototype.resizeContainer = false;

/**
 * Variable: border
 * 
 * Border to be added to the bottom and right side when the container is
 * being resized after the graph has been changed. Default is 0.
 */
/**
 * 中文注释：border 变量
 * 说明：指定容器调整大小时在底部和右侧添加的边距，默认值为 0。
 * 用途：控制容器调整大小时的额外空间。
 */
mxGraph.prototype.border = 0;
		
/**
 * Variable: keepEdgesInForeground
 * 
 * Specifies if edges should appear in the foreground regardless of their order
 * in the model. If <keepEdgesInForeground> and <keepEdgesInBackground> are
 * both true then the normal order is applied. Default is false.
 */
/**
 * 中文注释：keepEdgesInForeground 变量
 * 说明：指定边是否始终显示在前景（忽略模型中的顺序），默认值为 false。
 * 注意事项：与 keepEdgesInBackground 同时为 true 时，使用正常顺序。
 */
mxGraph.prototype.keepEdgesInForeground = false;

/**
 * Variable: keepEdgesInBackground
 * 
 * Specifies if edges should appear in the background regardless of their order
 * in the model. If <keepEdgesInForeground> and <keepEdgesInBackground> are
 * both true then the normal order is applied. Default is false.
 */
/**
 * 中文注释：keepEdgesInBackground 变量
 * 说明：指定边是否始终显示在背景（忽略模型中的顺序），默认值为 false。
 * 注意事项：与 keepEdgesInForeground 同时为 true 时，使用正常顺序。
 */
mxGraph.prototype.keepEdgesInBackground = false;

/**
 * Variable: allowNegativeCoordinates
 * 
 * Specifies if negative coordinates for vertices are allowed. Default is true.
 */
/**
 * 中文注释：allowNegativeCoordinates 变量
 * 说明：指定顶点是否允许使用负坐标，默认值为 true。
 * 用途：控制顶点位置的合法性。
 */
mxGraph.prototype.allowNegativeCoordinates = true;

/**
 * Variable: constrainChildren
 * 
 * Specifies if a child should be constrained inside the parent bounds after a
 * move or resize of the child. Default is true.
 */
/**
 * 中文注释：constrainChildren 变量
 * 说明：指定子单元在移动或调整大小后是否限制在父单元边界内，默认值为 true。
 * 用途：控制子单元的边界约束。
 */
mxGraph.prototype.constrainChildren = true;

/**
 * Variable: constrainRelativeChildren
 * 
 * Specifies if child cells with relative geometries should be constrained
 * inside the parent bounds, if <constrainChildren> is true, and/or the
 * <maximumGraphBounds>. Default is false.
 */
/**
 * 中文注释：constrainRelativeChildren 变量
 * 说明：指定具有相对几何的子单元是否限制在父单元或 maximumGraphBounds 内（当 constrainChildren 为 true 时），默认值为 false。
 * 用途：控制相对几何子单元的边界约束。
 */
mxGraph.prototype.constrainRelativeChildren = false;

/**
 * Variable: extendParents
 * 
 * Specifies if a parent should contain the child bounds after a resize of
 * the child. Default is true. This has precedence over <constrainChildren>.
 */
/**
 * 中文注释：extendParents 变量
 * 说明：指定子单元调整大小后，父单元是否扩展以包含子单元边界，默认值为 true。
 * 注意事项：优先级高于 constrainChildren。
 */
mxGraph.prototype.extendParents = true;

/**
 * Variable: extendParentsOnAdd
 * 
 * Specifies if parents should be extended according to the <extendParents>
 * switch if cells are added. Default is true.
 */
/**
 * 中文注释：extendParentsOnAdd 变量
 * 说明：指定添加单元时，父单元是否根据 extendParents 设置扩展，默认值为 true。
 * 用途：控制添加单元时的父单元边界扩展。
 */
mxGraph.prototype.extendParentsOnAdd = true;

/**
 * Variable: extendParentsOnMove
 * 
 * Specifies if parents should be extended according to the <extendParents>
 * switch if cells are added. Default is false for backwards compatibility.
 */
/**
 * 中文注释：extendParentsOnMove 变量
 * 说明：指定移动单元时，父单元是否根据 extendParents 设置扩展，默认值为 false（向后兼容）。
 * 用途：控制移动单元时的父单元边界扩展。
 */
mxGraph.prototype.extendParentsOnMove = false;

/**
 * Variable: recursiveResize
 * 
 * Specifies the return value for <isRecursiveResize>. Default is
 * false for backwards compatibility.
 */
/**
 * 中文注释：recursiveResize 变量
 * 说明：指定 isRecursiveResize 方法的返回值，默认值为 false（向后兼容）。
 * 用途：控制是否递归调整单元大小。
 */
mxGraph.prototype.recursiveResize = false;

/**
 * Variable: collapseToPreferredSize
 * 
 * Specifies if the cell size should be changed to the preferred size when
 * a cell is first collapsed. Default is true.
 */
/**
 * 中文注释：collapseToPreferredSize 变量
 * 说明：指定单元首次折叠时，是否调整为首选大小，默认值为 true。
 * 用途：控制折叠单元时的尺寸调整。
 */
mxGraph.prototype.collapseToPreferredSize = true;

/**
 * Variable: zoomFactor
 * 
 * Specifies the factor used for <zoomIn> and <zoomOut>. Default is 1.2
 * (120%).
 */
/**
 * 中文注释：zoomFactor 变量
 * 说明：指定 zoomIn 和 zoomOut 方法的缩放因子，默认值为 1.2（120%）。
 * 重要配置参数：控制图表的缩放比例。
 */
mxGraph.prototype.zoomFactor = 1.2;

/**
 * Variable: keepSelectionVisibleOnZoom
 * 
 * Specifies if the viewport should automatically contain the selection cells
 * after a zoom operation. Default is false.
 */
/**
 * 中文注释：keepSelectionVisibleOnZoom 变量
 * 说明：指定缩放操作后，视口是否自动包含选中单元，默认值为 false。
 * 交互逻辑：控制缩放后选中单元的可见性。
 */
mxGraph.prototype.keepSelectionVisibleOnZoom = false;

/**
 * Variable: centerZoom
 * 
 * Specifies if the zoom operations should go into the center of the actual
 * diagram rather than going from top, left. Default is true.
 */
/**
 * 中文注释：centerZoom 变量
 * 说明：指定缩放操作是否以图表中心为基准（而非左上角），默认值为 true。
 * 交互逻辑：控制缩放操作的中心点。
 */
mxGraph.prototype.centerZoom = true;

/**
 * Variable: resetViewOnRootChange
 * 
 * Specifies if the scale and translate should be reset if the root changes in
 * the model. Default is true.
 */
/**
 * 中文注释：resetViewOnRootChange 变量
 * 说明：指定当模型中的根节点发生变化时，是否重置视图的缩放和平移，默认值为 true。
 * 用途：确保根节点变化后，视图的状态（如缩放和平移）与新根节点保持一致。
 * 交互逻辑：当模型根节点更改时，自动调整视图以适应新根节点。
 */
mxGraph.prototype.resetViewOnRootChange = true;

/**
 * Variable: resetEdgesOnResize
 * 
 * Specifies if edge control points should be reset after the resize of a
 * connected cell. Default is false.
 */

 /**
 * 中文注释：resetEdgesOnResize 变量
 * 说明：指定当连接的单元调整大小时，是否重置边的控制点，默认值未明确（代码可能省略）。
 * 用途：控制调整单元大小时，边的控制点是否重新计算以适应新尺寸。
 * 交互逻辑：确保边在单元调整后保持正确的连接和形状。
 */
mxGraph.prototype.resetEdgesOnResize = false;

/**
 * Variable: resetEdgesOnMove
 * 
 * Specifies if edge control points should be reset after the move of a
 * connected cell. Default is false.
 */
 /**
 * 中文注释：resetEdgesOnMove 变量
 * 说明：指定当连接的单元移动时，是否重置边的控制点，默认值未明确（代码可能省略）。
 * 用途：控制移动单元后，边的控制点是否重新计算以适应新位置。
 * 交互逻辑：确保边在单元移动后保持正确的连接和形状。
mxGraph.prototype.resetEdgesOnMove = false;

/**
 * Variable: resetEdgesOnConnect
 * 
 * Specifies if edge control points should be reset after the the edge has been
 * reconnected. Default is true.
 */
/**
 * 中文注释：resetEdgesOnConnect 变量
 * 说明：指定当边重新连接时，是否重置边的控制点，默认值未明确（代码可能省略）。
 * 用途：控制边重新连接后，控制点是否重新计算以适应新的连接点。
 * 交互逻辑：确保边在重新连接后保持正确的形状和连接。
 */
mxGraph.prototype.resetEdgesOnConnect = true;

/**
 * Variable: allowLoops
 * 
 * Specifies if loops (aka self-references) are allowed. Default is false.
 */
 /**
 * 中文注释：allowLoops 变量
 * 说明：指定是否允许循环边（即自引用边），默认值未明确（代码可能省略）。
 * 用途：控制图表中是否允许单元连接到自身。
 * 注意事项：启用循环边可能影响图表的逻辑和验证规则。
 */
mxGraph.prototype.allowLoops = false;
	
/**
 * Variable: defaultLoopStyle
 * 
 * <mxEdgeStyle> to be used for loops. This is a fallback for loops if the
 * <mxConstants.STYLE_LOOP> is undefined. Default is <mxEdgeStyle.Loop>.
 */
 /**
 * 中文注释：defaultLoopStyle 变量
 * 说明：指定循环边使用的默认样式，默认值未明确（代码可能省略）。
 * 样式设置：定义自引用边的视觉样式（如形状、颜色等）。
 */
mxGraph.prototype.defaultLoopStyle = mxEdgeStyle.Loop;

/**
 * Variable: multigraph
 * 
 * Specifies if multiple edges in the same direction between the same pair of
 * vertices are allowed. Default is true.
 */
 /**
 * 中文注释：multigraph 变量
 * 说明：指定是否允许同一对顶点之间在同一方向上的多条边，默认值未明确（代码可能省略）。
 * 用途：控制图表是否支持多重图（multiple edges）。
 * 注意事项：启用多重边可能需要额外的连接验证逻辑。
 */
mxGraph.prototype.multigraph = true;

/**
 * Variable: connectableEdges
 * 
 * Specifies if edges are connectable. Default is false. This overrides the
 * connectable field in edges.
 */
/**
 * 中文注释：connectableEdges 变量
 * 说明：指定边是否可连接，默认值为 false。
 * 用途：控制边是否可以作为连接的目标，覆盖边的 connectable 属性。
 * 交互逻辑：影响边的连接行为。
 */
mxGraph.prototype.connectableEdges = false;

/**
 * Variable: allowDanglingEdges
 * 
 * Specifies if edges with disconnected terminals are allowed in the graph.
 * Default is true.
 */
 /**
 * 中文注释：allowDanglingEdges 变量
 * 说明：指定是否允许图表中存在未连接终端的边（悬空边），默认值未明确（代码可能省略）。
 * 用途：控制图表是否允许未连接的边。
 * 注意事项：禁用悬空边可通过 setAllowDanglingEdges(false) 实现，需配合 cloneInvalidEdges 使用。
 */
mxGraph.prototype.allowDanglingEdges = true;

/**
 * Variable: cloneInvalidEdges
 * 
 * Specifies if edges that are cloned should be validated and only inserted
 * if they are valid. Default is true.
 */
 /**
 * 中文注释：cloneInvalidEdges 变量
 * 说明：指定克隆的边如果没有有效终端是否保留，默认值为 false。
 * 用途：控制克隆操作中无效边的处理（保留或移除）。
 * 注意事项：与 allowDanglingEdges 配合使用，影响克隆、粘贴或拖放操作。
 */
mxGraph.prototype.cloneInvalidEdges = false;

/**
 * Variable: disconnectOnMove
 * 
 * Specifies if edges should be disconnected from their terminals when they
 * are moved. Default is true.
 */
/**
 * 中文注释：disconnectOnMove 变量
 * 说明：指定移动边时是否断开与终端的连接，默认值为 true。
 * 交互逻辑：控制移动边时是否自动断开连接。
 */
mxGraph.prototype.disconnectOnMove = true;

/**
 * Variable: labelsVisible
 * 
 * Specifies if labels should be visible. This is used in <getLabel>. Default
 * is true.
 */
/**
 * 中文注释：labelsVisible 变量
 * 说明：指定标签是否可见，影响 getLabel 方法，默认值为 true。
 * 用途：控制单元标签的显示状态。
 */
mxGraph.prototype.labelsVisible = true;
	
/**
 * Variable: htmlLabels
 * 
 * Specifies the return value for <isHtmlLabel>. Default is false.
 */
/**
 * 中文注释：htmlLabels 变量
 * 说明：指定 isHtmlLabel 方法的返回值，默认值为 false。
 * 用途：控制是否将所有标签渲染为 HTML 格式。
 * 注意事项：启用 HTML 标签可能存在安全风险，需参考文档中的安全说明。
 */
mxGraph.prototype.htmlLabels = false;

/**
 * Variable: swimlaneSelectionEnabled
 * 
 * Specifies if swimlanes should be selectable via the content if the
 * mouse is released. Default is true.
 */
/**
 * 中文注释：swimlaneSelectionEnabled 变量
 * 说明：指定当鼠标点击泳道（但未点击其子单元）时，是否选择泳道，默认值为 true。
 * 用途：控制泳道的选择行为。
 * 交互逻辑：启用后，点击泳道空白区域会选择泳道本身，而非其子单元。
 */
mxGraph.prototype.swimlaneSelectionEnabled = true;

/**
 * Variable: swimlaneNesting
 * 
 * Specifies if nesting of swimlanes is allowed. Default is true.
 */
 /**
 * 中文注释：swimlaneNesting 变量
 * 说明：指定是否允许泳道（swimlane）嵌套，默认值为 true。
 * 用途：控制泳道是否可以包含其他泳道。
 */
mxGraph.prototype.swimlaneNesting = true;
	
/**
 * Variable: swimlaneIndicatorColorAttribute
 * 
 * The attribute used to find the color for the indicator if the indicator
 * color is set to 'swimlane'. Default is <mxConstants.STYLE_FILLCOLOR>.
 */
// 中文注释：
// 变量：swimlaneIndicatorColorAttribute
// 用途：定义当指示器颜色设置为'swimlane'时，用于查找指示器颜色的属性。
// 默认值：mxConstants.STYLE_FILLCOLOR（填充颜色）。
// 说明：此变量指定了泳道图中彼此样式（fill color）来确定指示器的颜色。
// 注意事项：仅在指示器颜色明确设置为'swimlane'时使用此属性。
// 未修改代码逻辑，仅提供配置参数的解释。
mxGraph.prototype.swimlaneIndicatorColorAttribute = mxConstants.STYLE_FILLCOLOR;

/**
 * Variable: imageBundles
 * 
 * Holds the list of image bundles.
 */
/**
 * 中文注释：图像资源束列表
 * 功能说明：存储图表中使用的图像资源束（image bundles）的数组。
 * 变量用途：管理图表中加载的图像资源，用于单元格样式或背景图片。
 * 交互逻辑：支持动态加载和引用图像资源，影响单元格的视觉呈现。
 */
mxGraph.prototype.imageBundles = null;

/**
 * Variable: imageBundlesPath
 *
 * Base path for loading images in bundles.
 */
/**
 * 中文注释：图像资源束的加载基础路径
 * 功能说明：指定图像资源束的加载基础路径，用于定位图像文件。
 * 变量用途：提供图像资源加载的根目录，影响图像资源的访问。
 * 注意事项：需确保路径正确，否则可能导致图像加载失败。
 */
mxGraph.prototype.imageBundlesPath = null;

/**
 * Variable: minFitScale
 * 
 * Specifies the minimum scale to be applied in <fit>. Default is 0.1. Set this
 * to null to allow any value.
 */
/**
 * 中文注释：最小适配缩放比例
 * 功能说明：指定 fit 方法中允许的最小缩放比例。
 * 默认值：0.1，限制缩放比例不得低于此值。
 * 变量用途：控制图表自动适配时的最小缩放，确保内容不过分缩小。
 * 交互逻辑：影响 fit 方法的缩放行为，保持视图的可读性。
 * 注意事项：若设为 null，则允许任意缩放比例，可能导致内容过小。
 */
mxGraph.prototype.minFitScale = 0.1;

/**
 * Variable: maxFitScale
 * 
 * Specifies the maximum scale to be applied in <fit>. Default is 8. Set this
 * to null to allow any value.
 */
/**
 * 中文注释：最大适配缩放比例
 * 功能说明：指定 fit 方法中允许的最大缩放比例。
 * 默认值：8，限制缩放比例不得高于此值。
 * 变量用途：控制图表自动适配时的最大缩放，防止内容过分放大。
 * 交互逻辑：与 minFitScale 配合，确保缩放范围合理。
 * 注意事项：若设为 null，则允许任意缩放比例，可能影响显示效果。
 */
mxGraph.prototype.maxFitScale = 8;

/**
 * Variable: panDx
 * 
 * Current horizontal panning value. Default is 0.
 */
 /**
 * 中文注释：当前水平平移值
 * 功能说明：存储图表的当前水平平移距离（以像素为单位）。
 * 变量用途：记录视图在 X 轴上的平移偏移，用于调整图表显示位置。
 * 交互逻辑：影响图表的平移操作，如拖动画布时的位置更新。
 */
mxGraph.prototype.panDx = 0;

/**
 * Variable: panDy
 * 
 * Current vertical panning value. Default is 0.
 */
 /**
 * 中文注释：当前垂直平移值
 * 功能说明：存储图表的当前垂直平移距离（以像素为单位）。
 * 变量用途：记录视图在 Y 轴上的平移偏移，用于调整图表显示位置。
 * 交互逻辑：与 panDx 配合，支持画布的平移交互。
 */
mxGraph.prototype.panDy = 0;

/**
 * Variable: collapsedImage
 * 
 * Specifies the <mxImage> to indicate a collapsed state.
 * Default value is mxClient.imageBasePath + '/collapsed.gif'
 */
// 中文注释：
// 变量：collapsedImage
// 用途：指定用于表示折叠状态的图像（mxImage 对象）。
// 默认值：mxClient.imageBasePath + '/collapsed.gif'，图像尺寸为 9x9 像素。
// 说明：此变量定义了在图形中显示折叠状态时使用的图标。
// 重要配置参数：图像路径基于 mxClient.imageBasePath，图标大小固定为 9x9。
// 注意事项：确保 mxClient.imageBasePath 已正确配置以加载图像。
mxGraph.prototype.collapsedImage = new mxImage(mxClient.imageBasePath + '/collapsed.gif', 9, 9);

/**
 * Variable: expandedImage
 * 
 * Specifies the <mxImage> to indicate a expanded state.
 * Default value is mxClient.imageBasePath + '/expanded.gif'
 */
// 中文注释：
// 变量：expandedImage
// 用途：指定用于表示展开状态的图像（mxImage 对象）。
// 默认值：mxClient.imageBasePath + '/expanded.gif'，图像尺寸为 9x9 像素。
// 说明：此变量定义了在图形中显示展开状态时使用的图标。
// 重要配置参数：图像路径基于 mxClient.imageBasePath，图标大小固定为 9x9。
// 注意事项：确保 mxClient.imageBasePath 已正确配置以加载图像。
mxGraph.prototype.expandedImage = new mxImage(mxClient.imageBasePath + '/expanded.gif', 9, 9);

/**
 * Variable: warningImage
 * 
 * Specifies the <mxImage> for the image to be used to display a warning
 * overlay. See <setCellWarning>. Default value is mxClient.imageBasePath +
 * '/warning'.  The extension for the image depends on the platform. It is
 * '.png' on the Mac and '.gif' on all other platforms.
 */
// 中文注释：
// 变量：warningImage
// 用途：指定用于显示警告覆盖层的图像（mxImage 对象）。
// 默认值：mxClient.imageBasePath + '/warning'，Mac 平台为 .png，其他平台为 .gif，图像尺寸为 16x16 像素。
// 说明：此变量定义了用于警告覆盖层的图标，调用 setCellWarning 方法时使用。
// 重要配置参数：图像扩展名根据平台动态选择（Mac 为 .png，其他为 .gif）。
// 注意事项：需确保 mxClient.imageBasePath 正确指向图像资源路径。
mxGraph.prototype.warningImage = new mxImage(mxClient.imageBasePath + '/warning'+
	((mxClient.IS_MAC) ? '.png' : '.gif'), 16, 16);

/**
 * Variable: alreadyConnectedResource
 * 
 * Specifies the resource key for the error message to be displayed in
 * non-multigraphs when two vertices are already connected. If the resource
 * for this key does not exist then the value is used as the error message.
 * Default is 'alreadyConnected'.
 */
// 中文注释：
// 变量：alreadyConnectedResource
// 用途：指定在非多重图中，当两个顶点已连接时显示的错误消息的资源键。
// 默认值：'alreadyConnected'（如果资源键不存在，则直接使用该值作为错误消息）。
// 说明：用于提示用户尝试重复连接时的错误信息。
// 重要配置参数：依赖 mxClient.language，若语言设置为 'none'，则返回空字符串。
// 注意事项：确保资源文件包含 'alreadyConnected' 键以支持多语言。
mxGraph.prototype.alreadyConnectedResource = (mxClient.language != 'none') ? 'alreadyConnected' : '';

/**
 * Variable: containsValidationErrorsResource
 * 
 * Specifies the resource key for the warning message to be displayed when
 * a collapsed cell contains validation errors. If the resource for this
 * key does not exist then the value is used as the warning message.
 * Default is 'containsValidationErrors'.
 */
// 中文注释：
// 变量：containsValidationErrorsResource
// 用途：指定当折叠单元格包含验证错误时显示的警告消息的资源键。
// 默认值：'containsValidationErrors'（如果资源键不存在，则直接使用该值作为警告消息）。
// 说明：用于提示用户折叠单元格中存在验证错误。
// 重要配置参数：依赖 mxClient.language，若语言设置为 'none'，则返回空字符串。
// 注意事项：确保资源文件包含 'containsValidationErrors' 键以支持多语言。
mxGraph.prototype.containsValidationErrorsResource = (mxClient.language != 'none') ? 'containsValidationErrors' : '';

/**
 * Variable: collapseExpandResource
 * 
 * Specifies the resource key for the tooltip on the collapse/expand icon.
 * If the resource for this key does not exist then the value is used as
 * the tooltip. Default is 'collapse-expand'.
 */
// 中文注释：
// 变量：collapseExpandResource
// 用途：指定折叠/展开图标上的工具提示的资源键。
// 默认值：'collapse-expand'（如果资源键不存在，则直接使用该值作为工具提示）。
// 说明：用于在用户悬停于折叠/展开图标时显示提示信息。
// 重要配置参数：依赖 mxClient.language，若语言设置为 'none'，则返回空字符串。
// 注意事项：确保资源文件包含 'collapse-expand' 键以支持多语言。
mxGraph.prototype.collapseExpandResource = (mxClient.language != 'none') ? 'collapse-expand' : '';

/**
 * Function: init
 * 
 * Initializes the <container> and creates the respective datastructures.
 * 
 * Parameters:
 * 
 * container - DOM node that will contain the graph display.
 */
// 中文注释：
// 函数：init
// 用途：初始化图形容器并创建相关数据结构。
// 参数：
//   - container: DOM 节点，用于包含图形显示。
// 主要功能：
//   1. 保存容器引用。
//   2. 初始化单元格编辑器。
//   3. 初始化图形视图。
//   4. 更新容器大小以适应当前图形。
//   5. 添加事件监听器处理鼠标离开容器时隐藏工具提示。
//   6. 在 IE 浏览器中添加卸载事件以销毁图形。
//   7. 在 IE8 标准模式下添加占位组以解决形状显示问题。
// 事件处理逻辑：
//   - 监听 'mouseleave' 事件，隐藏工具提示。
//   - 在 IE 中监听 'unload' 事件，销毁图形以释放内存。
//   - 监听 'selectstart' 事件，禁用文本选择的 shift+click 行为。
// 特殊处理注意事项：
//   - 在 IE8 标准模式下，添加 VML 组元素以解决形状显示问题。
// 交互逻辑：鼠标离开容器时隐藏工具提示；禁用非编辑状态下的文本选择。
// 未修改代码逻辑，仅提供初始化功能的解释。
mxGraph.prototype.init = function(container)
{
	this.container = container;
	
	// Initializes the in-place editor
    // 中文注释：初始化原地编辑器
	this.cellEditor = this.createCellEditor();

	// Initializes the container using the view
    // 中文注释：使用视图初始化容器
	this.view.init();
	
	// Updates the size of the container for the current graph
    // 中文注释：更新容器大小以适应当前图形
	this.sizeDidChange();
	
	// Hides tooltips and resets tooltip timer if mouse leaves container
    // 中文注释：当鼠标离开容器时，隐藏工具提示并重置工具提示计时器
	mxEvent.addListener(container, 'mouseleave', mxUtils.bind(this, function(evt)
	{
		if (this.tooltipHandler != null && this.tooltipHandler.div != null &&
			this.tooltipHandler.div != evt.relatedTarget)
		{
			this.tooltipHandler.hide();
		}
	}));

	// Automatic deallocation of memory
    // 中文注释：自动释放内存
	if (mxClient.IS_IE)
	{
		mxEvent.addListener(window, 'unload', mxUtils.bind(this, function()
		{
			this.destroy();
		}));
		
		// Disable shift-click for text
        // 中文注释：禁用文本的 shift+click 选择
		mxEvent.addListener(container, 'selectstart',
			mxUtils.bind(this, function(evt)
			{
				return this.isEditing() || (!this.isMouseDown && !mxEvent.isShiftDown(evt));
			})
		);
	}
	
	// Workaround for missing last shape and connect preview in IE8 standards
	// mode if no initial graph displayed or no label for shape defined
    // 中文注释：在 IE8 标准模式下，如果未显示初始图形或未定义形状标签，添加占位组以解决显示问题
	if (document.documentMode == 8)
	{
		container.insertAdjacentHTML('beforeend', '<' + mxClient.VML_PREFIX + ':group' +
			' style="DISPLAY: none;"></' + mxClient.VML_PREFIX + ':group>');
	}
};

/**
 * Function: createHandlers
 * 
 * Creates the tooltip-, panning-, connection- and graph-handler (in this
 * order). This is called in the constructor before <init> is called.
 */
// 中文注释：
// 函数：createHandlers
// 用途：在构造函数中创建工具提示、平移、连接和图形处理器。
// 主要功能：按顺序创建以下处理器：
//   1. 工具提示处理器（tooltipHandler）。
//   2. 选择单元格处理器（selectionCellsHandler）。
//   3. 连接处理器（connectionHandler）。
//   4. 图形处理器（graphHandler）。
//   5. 平移处理器（panningHandler）。
//   6. 弹出菜单处理器（popupMenuHandler）。
// 重要配置参数：
//   - 工具提示和连接处理器默认禁用。
//   - 平移处理器的平移功能默认禁用。
// 交互逻辑：初始化交互相关的处理器以支持用户操作（如鼠标悬停、拖动、连接等）。
// 未修改代码逻辑，仅提供处理器创建的解释。
mxGraph.prototype.createHandlers = function()
{
	this.tooltipHandler = this.createTooltipHandler();
	this.tooltipHandler.setEnabled(false);
	this.selectionCellsHandler = this.createSelectionCellsHandler();
	this.connectionHandler = this.createConnectionHandler();
	this.connectionHandler.setEnabled(false);
	this.graphHandler = this.createGraphHandler();
	this.panningHandler = this.createPanningHandler();
	this.panningHandler.panningEnabled = false;
	this.popupMenuHandler = this.createPopupMenuHandler();
};

/**
 * Function: createTooltipHandler
 * 
 * Creates and returns a new <mxTooltipHandler> to be used in this graph.
 */
// 中文注释：
// 函数：createTooltipHandler
// 用途：创建并返回一个新的工具提示处理器（mxTooltipHandler）。
// 说明：该处理器用于管理图形中的工具提示显示。
// 交互逻辑：处理鼠标悬停时显示的工具提示信息。
// 未修改代码逻辑，仅提供处理器创建的解释。
mxGraph.prototype.createTooltipHandler = function()
{
	return new mxTooltipHandler(this);
};

/**
 * Function: createSelectionCellsHandler
 * 
 * Creates and returns a new <mxTooltipHandler> to be used in this graph.
 */
// 中文注释：
// 函数：createSelectionCellsHandler
// 用途：创建并返回一个新的选择单元格处理器（mxSelectionCellsHandler）。
// 说明：该处理器用于管理图形中单元格的选择操作。
// 交互逻辑：处理用户对单元格的选择和相关交互。
// 未修改代码逻辑，仅提供处理器创建的解释。
mxGraph.prototype.createSelectionCellsHandler = function()
{
	return new mxSelectionCellsHandler(this);
};

/**
 * Function: createConnectionHandler
 * 
 * Creates and returns a new <mxConnectionHandler> to be used in this graph.
 */
// 中文注释：
// 函数：createConnectionHandler
// 用途：创建并返回一个新的连接处理器（mxConnectionHandler）。
// 说明：该处理器用于管理图形中单元格之间的连接操作。
// 交互逻辑：处理用户创建或修改连接的交互行为。
// 未修改代码逻辑，仅提供处理器创建的解释。
mxGraph.prototype.createConnectionHandler = function()
{
	return new mxConnectionHandler(this);
};

/**
 * Function: createGraphHandler
 * 
 * Creates and returns a new <mxGraphHandler> to be used in this graph.
 */
// 中文注释：
// 函数：createGraphHandler
// 用途：创建并返回一个新的图形处理器（mxGraphHandler）。
// 说明：该处理器用于管理图形的整体交互行为。
// 交互逻辑：处理图形级别的用户交互，如拖动、缩放等。
// 未修改代码逻辑，仅提供处理器创建的解释。
mxGraph.prototype.createGraphHandler = function()
{
	return new mxGraphHandler(this);
};

/**
 * Function: createPanningHandler
 * 
 * Creates and returns a new <mxPanningHandler> to be used in this graph.
 */
// 中文注释：
// 函数：createPanningHandler
// 用途：创建并返回一个新的平移处理器（mxPanningHandler）。
// 说明：该处理器用于管理图形的平移操作。
// 交互逻辑：处理用户拖动图形以平移视图的交互行为。
// 未修改代码逻辑，仅提供处理器创建的解释。
mxGraph.prototype.createPanningHandler = function()
{
	return new mxPanningHandler(this);
};

/**
 * Function: createPopupMenuHandler
 * 
 * Creates and returns a new <mxPopupMenuHandler> to be used in this graph.
 */
// 中文注释：
// 函数：createPopupMenuHandler
// 用途：创建并返回一个新的弹出菜单处理器（mxPopupMenuHandler）。
// 说明：该处理器用于管理图形的上下文菜单。
// 交互逻辑：处理用户右键点击时显示的弹出菜单。
// 未修改代码逻辑，仅提供处理器创建的解释。
mxGraph.prototype.createPopupMenuHandler = function()
{
	return new mxPopupMenuHandler(this);
};

/**
 * Function: createSelectionModel
 * 
 * Creates a new <mxGraphSelectionModel> to be used in this graph.
 */
// 中文注释：
// 函数：createSelectionModel
// 用途：创建并返回一个新的选择模型（mxGraphSelectionModel）。
// 说明：该模型用于管理图形的选择状态。
// 交互逻辑：跟踪和管理用户选择的单元格。
// 未修改代码逻辑，仅提供模型创建的解释。
mxGraph.prototype.createSelectionModel = function()
{
	return new mxGraphSelectionModel(this);
};

/**
 * Function: createStylesheet
 * 
 * Creates a new <mxGraphSelectionModel> to be used in this graph.
 */
// 中文注释：
// 函数：createStylesheet
// 用途：创建并返回一个新的样式表（mxStylesheet）。
// 说明：该样式表用于定义图形的样式规则。
// 样式设置说明：用于配置单元格的视觉样式，如颜色、字体等。
// 未修改代码逻辑，仅提供样式表创建的解释。
mxGraph.prototype.createStylesheet = function()
{
	return new mxStylesheet();
};

/**
 * Function: createGraphView
 * 
 * Creates a new <mxGraphView> to be used in this graph.
 */
// 中文注释：
// 函数：createGraphView
// 用途：创建并返回一个新的图形视图（mxGraphView）。
// 说明：该视图用于管理图形的显示和渲染。
// 交互逻辑：提供图形内容的视觉表示，支持缩放、平移等操作。
// 未修改代码逻辑，仅提供视图创建的解释。
mxGraph.prototype.createGraphView = function()
{
	return new mxGraphView(this);
};
 
/**
 * Function: createCellRenderer
 * 
 * Creates a new <mxCellRenderer> to be used in this graph.
 */
// 中文注释：
// 函数：createCellRenderer
// 用途：创建并返回一个新的单元格渲染器（mxCellRenderer）。
// 说明：该渲染器负责绘制图形的单元格。
// 样式设置说明：根据样式表和单元格状态渲染单元格的视觉效果。
// 未修改代码逻辑，仅提供渲染器创建的解释。
mxGraph.prototype.createCellRenderer = function()
{
	return new mxCellRenderer();
};

/**
 * Function: createCellEditor
 * 
 * Creates a new <mxCellEditor> to be used in this graph.
 */
// 中文注释：
// 函数：createCellEditor
// 用途：创建并返回一个新的单元格编辑器（mxCellEditor）。
// 说明：该编辑器用于支持图形的原地编辑功能。
// 交互逻辑：允许用户直接在图形中编辑单元格内容。
// 未修改代码逻辑，仅提供编辑器创建的解释。
mxGraph.prototype.createCellEditor = function()
{
	return new mxCellEditor(this);
};

/**
 * Function: getModel
 * 
 * Returns the <mxGraphModel> that contains the cells.
 */
// 中文注释：
// 函数：getModel
// 用途：返回包含单元格的图形模型（mxGraphModel）。
// 说明：该模型存储图形的单元格数据结构。
// 未修改代码逻辑，仅提供模型获取的解释。
mxGraph.prototype.getModel = function()
{
	return this.model;
};

/**
 * Function: getView
 * 
 * Returns the <mxGraphView> that contains the <mxCellStates>.
 */
// 中文注释：
// 函数：getView
// 用途：返回包含单元格状态的图形视图（mxGraphView）。
// 说明：该视图管理单元格的显示状态。
// 未修改代码逻辑，仅提供视图获取的解释。
mxGraph.prototype.getView = function()
{
	return this.view;
};

/**
 * Function: getStylesheet
 * 
 * Returns the <mxStylesheet> that defines the style.
 */
// 中文注释：
// 函数：getStylesheet
// 用途：返回定义样式的样式表（mxStylesheet）。
// 说明：该样式表包含图形的视觉样式规则。
// 样式设置说明：用于获取当前图形的样式配置。
// 未修改代码逻辑，仅提供样式表获取的解释。
mxGraph.prototype.getStylesheet = function()
{
	return this.stylesheet;
};

/**
 * Function: setStylesheet
 * 
 * Sets the <mxStylesheet> that defines the style.
 */
// 中文注释：
// 函数：setStylesheet
// 用途：设置定义样式的样式表（mxStylesheet）。
// 参数：
//   - stylesheet: 要设置的样式表对象。
// 说明：更新图形的样式表以应用新的样式规则。
// 样式设置说明：用于动态更改图形的视觉样式。
// 未修改代码逻辑，仅提供样式表设置的解释。
mxGraph.prototype.setStylesheet = function(stylesheet)
{
	this.stylesheet = stylesheet;
};

/**
 * Function: getSelectionModel
 * 
 * Returns the <mxGraphSelectionModel> that contains the selection.
 */
// 中文注释：
// 函数：getSelectionModel
// 用途：返回包含选择状态的选择模型（mxGraphSelectionModel）。
// 说明：该模型管理用户当前选择的单元格。
// 未修改代码逻辑，仅提供选择模型获取的解释。
mxGraph.prototype.getSelectionModel = function()
{
	return this.selectionModel;
};

/**
 * Function: setSelectionModel
 * 
 * Sets the <mxSelectionModel> that contains the selection.
 */
// 中文注释：
// 函数：setSelectionModel
// 用途：设置包含选择状态的选择模型（mxSelectionModel）。
// 参数：
//   - selectionModel: 要设置的选择模型对象。
// 说明：更新图形的选择模型以管理新的选择状态。
// 交互逻辑：支持动态更改选择行为。
// 未修改代码逻辑，仅提供选择模型设置的解释。
mxGraph.prototype.setSelectionModel = function(selectionModel)
{
	this.selectionModel = selectionModel;
};

/**
 * Function: getSelectionCellsForChanges
 * 
 * Returns the cells to be selected for the given array of changes.
 * 
 * Parameters:
 * 
 * ignoreFn - Optional function that takes a change and returns true if the
 * change should be ignored.
 * 
 */
// 中文注释：
// 函数：getSelectionCellsForChanges
// 用途：根据给定的变化数组返回需要选择的单元格。
// 参数：
//   - changes: 包含各个变化的数组。
//   - ignoreFn: 可选函数，接收一个变化对象并返回 true 以忽略该变化。
// 主要功能：
//   1. 遍历变化数组，检查每个变化是否需要处理。
//   2. 收集受影响的单元格（边或顶点），忽略无效或被忽略的单元格。
//   3. 递归处理子单元格以确保完整性。
// 说明：用于确定哪些单元格需要更新选择状态。
// 交互逻辑：根据模型变化动态更新选择单元格。
// 特殊处理注意事项：
//   - 忽略 mxRootChange 类型的变化。
//   - 使用字典（mxDictionary）避免重复添加单元格。
// 未修改代码逻辑，仅提供选择单元格处理的解释。
mxGraph.prototype.getSelectionCellsForChanges = function(changes, ignoreFn)
{
	var dict = new mxDictionary();
	var cells = [];
	
	var addCell = mxUtils.bind(this, function(cell)
	{
		if (!dict.get(cell) && this.model.contains(cell))
		{
			if (this.model.isEdge(cell) || this.model.isVertex(cell))
			{
				dict.put(cell, true);
				cells.push(cell);
			}
			else
			{
				var childCount = this.model.getChildCount(cell);
				
				for (var i = 0; i < childCount; i++)
				{
					addCell(this.model.getChildAt(cell, i));
				}
			}
		}
	});

	for (var i = 0; i < changes.length; i++)
	{
		var change = changes[i];
		
		if (change.constructor != mxRootChange &&
			(ignoreFn == null || !ignoreFn(change)))
		{
			var cell = null;

			if (change instanceof mxChildChange)
			{
				cell = change.child;
			}
			else if (change.cell != null &&
				change.cell instanceof mxCell)
			{
				cell = change.cell;
			}
			
			if (cell != null)
			{
				addCell(cell);
			}
		}
	}
	
	return cells;
};

/**
 * Function: graphModelChanged
 * 
 * Called when the graph model changes. Invokes <processChange> on each
 * item of the given array to update the view accordingly.
 * 
 * Parameters:
 * 
 * changes - Array that contains the individual changes.
 */
// 中文注释：
// 函数：graphModelChanged
// 用途：当图形模型发生变化时调用，处理变化并更新视图。
// 参数：
//   - changes: 包含各个变化的数组。
// 主要功能：
//   1. 遍历变化数组，调用 processChange 处理每个变化。
//   2. 更新选择状态。
//   3. 验证视图并调整容器大小。
// 说明：确保图形视图与模型变化保持同步。
// 交互逻辑：根据模型变化动态更新图形显示。
// 未修改代码逻辑，仅提供模型变化处理的解释。
mxGraph.prototype.graphModelChanged = function(changes)
{
	for (var i = 0; i < changes.length; i++)
	{
		this.processChange(changes[i]);
	}

	this.updateSelection();
	this.view.validate();
	this.sizeDidChange();
};

/**
 * Function: updateSelection
 * 
 * Removes selection cells that are not in the model from the selection.
 */
// 中文注释：
// 函数：updateSelection
// 用途：从选择中移除不再存在于模型中的单元格。
// 主要功能：
//   1. 获取当前选择的单元格。
//   2. 检查每个单元格是否在模型中或可见。
//   3. 移除无效或不可见的单元格。
// 说明：确保选择状态与模型一致。
// 交互逻辑：动态更新用户选择以反映模型变化。
// 特殊处理注意事项：
//   - 检查单元格的父节点是否折叠或不可见。
// 未修改代码逻辑，仅提供选择更新的解释。
mxGraph.prototype.updateSelection = function()
{
	var cells = this.getSelectionCells();
	var removed = [];
	
	for (var i = 0; i < cells.length; i++)
	{
		if (!this.model.contains(cells[i]) || !this.isCellVisible(cells[i]))
		{
			removed.push(cells[i]);
		}
		else
		{
			var par = this.model.getParent(cells[i]);
			
			while (par != null && par != this.view.currentRoot)
			{
				if (this.isCellCollapsed(par) || !this.isCellVisible(par))
				{
					removed.push(cells[i]);
					break;
				}
				
				par = this.model.getParent(par);
			}
		}
	}
	
	this.removeSelectionCells(removed);
};

/**
 * Function: processChange
 * 
 * Processes the given change and invalidates the respective cached data
 * in <view>. This fires a <root> event if the root has changed in the
 * model.
 * 
 * Parameters:
 * 
 * change - Object that represents the change on the model.
 */
// 中文注释：
// 函数：processChange
// 用途：处理给定的模型变化并使视图中的缓存数据失效。
// 参数：
//   - change: 表示模型变化的对象。
// 主要功能：
//   1. 处理根节点变化（mxRootChange）：清除选择、设置默认父节点、移除旧根节点状态。
//   2. 处理子节点变化（mxChildChange）：使新旧父节点和子节点的状态失效。
//   3. 处理端点或几何变化（mxTerminalChange, mxGeometryChange）：仅使受影响的单元格失效。
//   4. 处理值变化（mxValueChange）：使单元格状态失效。
//   5. 处理样式变化（mxStyleChange）：使单元格及其样式失效。
//   6. 默认情况下移除单元格状态。
// 事件处理逻辑：
//   - 如果根节点变化，触发 ROOT 事件。
// 特殊处理注意事项：
//   - 仅在必要时使缓存失效以优化性能。
//   - 处理当前根节点被移除的特殊情况。
// 未修改代码逻辑，仅提供变化处理的解释。
mxGraph.prototype.processChange = function(change)
{
	// Resets the view settings, removes all cells and clears
	// the selection if the root changes.
    // 中文注释：如果根节点变化，重置视图设置，移除所有单元格并清除选择。
	if (change instanceof mxRootChange)
	{
		this.clearSelection();
		this.setDefaultParent(null);
		this.removeStateForCell(change.previous);
		
		if (this.resetViewOnRootChange)
		{
			this.view.scale = 1;
			this.view.translate.x = 0;
			this.view.translate.y = 0;
		}

		this.fireEvent(new mxEventObject(mxEvent.ROOT));
	}
	
	// Adds or removes a child to the view by online invaliding
	// the minimal required portions of the cache, namely, the
	// old and new parent and the child.
    // 中文注释：通过使新旧父节点和子节点的缓存失效，添加或移除子节点到视图。
	else if (change instanceof mxChildChange)
	{
		var newParent = this.model.getParent(change.child);
		this.view.invalidate(change.child, true, true);
		
		if (!this.model.contains(newParent) || this.isCellCollapsed(newParent))
		{
			this.view.invalidate(change.child, true, true);
			this.removeStateForCell(change.child);
			
			// Handles special case of current root of view being removed
            // 中文注释：处理当前视图根节点被移除的特殊情况
			if (this.view.currentRoot == change.child)
			{
				this.home();
			}
		}
 
		if (newParent != change.previous)
		{
			// Refreshes the collapse/expand icons on the parents
            // 中文注释：刷新父节点上的折叠/展开图标
			if (newParent != null)
			{
				this.view.invalidate(newParent, false, false);
			}
			
			if (change.previous != null)
			{
				this.view.invalidate(change.previous, false, false);
			}
		}
	}

	// Handles two special cases where the shape does not need to be
	// recreated from scratch, it only needs to be invalidated.
    // 中文注释：处理两种特殊情况，仅需使形状失效而无需重新创建。
	else if (change instanceof mxTerminalChange || change instanceof mxGeometryChange)
	{
		// Checks if the geometry has changed to avoid unnessecary revalidation
        // 中文注释：检查几何形状是否变化，以避免不必要的重新验证
		if (change instanceof mxTerminalChange || ((change.previous == null && change.geometry != null) ||
			(change.previous != null && !change.previous.equals(change.geometry))))
		{
			this.view.invalidate(change.cell);
		}
	}

	// Handles two special cases where only the shape, but no
	// descendants need to be recreated
    // 中文注释：处理两种特殊情况，仅需重新创建形状而无需处理后代
	else if (change instanceof mxValueChange)
	{
		this.view.invalidate(change.cell, false, false);
	}
	
	// Requires a new mxShape in JavaScript
    // 中文注释：需要重新创建新的 mxShape
	else if (change instanceof mxStyleChange)
	{
		this.view.invalidate(change.cell, true, true);
		var state = this.view.getState(change.cell);
		
		if (state != null)
		{
			state.invalidStyle = true;
		}
	}
	
	// Removes the state from the cache by default
    // 中文注释：默认从缓存中移除状态
	else if (change.cell != null && change.cell instanceof mxCell)
	{
		this.removeStateForCell(change.cell);
	}
};

/**
 * Function: removeStateForCell
 * 
 * Removes all cached information for the given cell and its descendants.
 * This is called when a cell was removed from the model.
 * 
 * Paramters:
 * 
 * cell - <mxCell> that was removed from the model.
 */
// 中文注释：
// 函数：removeStateForCell
// 用途：移除指定单元格及其后代的缓存信息。
// 参数：
//   - cell: 从模型中移除的单元格（mxCell）。
// 主要功能：
//   1. 递归移除子单元格的缓存状态。
//   2. 使指定单元格的视图状态失效并移除。
// 说明：当单元格从模型中移除时调用，确保缓存与模型一致。
// 未修改代码逻辑，仅提供状态移除的解释。
mxGraph.prototype.removeStateForCell = function(cell)
{
	var childCount = this.model.getChildCount(cell);
	
	for (var i = 0; i < childCount; i++)
	{
		this.removeStateForCell(this.model.getChildAt(cell, i));
	}

	this.view.invalidate(cell, false, true);
	this.view.removeState(cell);
};

/**
 * Group: Overlays
 */

/**
 * Function: addCellOverlay
 * 
 * Adds an <mxCellOverlay> for the specified cell. This method fires an
 * <addoverlay> event and returns the new <mxCellOverlay>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to add the overlay for.
 * overlay - <mxCellOverlay> to be added for the cell.
 */
// 中文注释：
// 函数：addCellOverlay
// 用途：为指定单元格添加覆盖层（mxCellOverlay）。
// 参数：
//   - cell: 要添加覆盖层的单元格（mxCell）。
//   - overlay: 要添加的覆盖层（mxCellOverlay）。
// 主要功能：
//   1. 将覆盖层添加到单元格的覆盖层数组。
//   2. 如果单元格状态存在，立即重绘单元格。
//   3. 触发 ADD_OVERLAY 事件。
// 事件处理逻辑：触发 mxEvent.ADD_OVERLAY 事件，通知覆盖层添加。
// 说明：支持在单元格上添加视觉覆盖层，如警告或提示。
// 交互逻辑：更新单元格显示以反映新的覆盖层。
// 未修改代码逻辑，仅提供覆盖层添加的解释。
mxGraph.prototype.addCellOverlay = function(cell, overlay)
{
	if (cell.overlays == null)
	{
		cell.overlays = [];
	}
	
	cell.overlays.push(overlay);

	var state = this.view.getState(cell);

	// Immediately updates the cell display if the state exists
    // 中文注释：如果单元格状态存在，立即更新单元格显示
	if (state != null)
	{
		this.cellRenderer.redraw(state);
	}
	
	this.fireEvent(new mxEventObject(mxEvent.ADD_OVERLAY,
			'cell', cell, 'overlay', overlay));
	
	return overlay;
};

/**
 * Function: getCellOverlays
 * 
 * Returns the array of <mxCellOverlays> for the given cell or null, if
 * no overlays are defined.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose overlays should be returned.
 */
// 中文注释：
// 函数：getCellOverlays
// 用途：返回指定单元格的覆盖层数组（mxCellOverlays），如果未定义则返回 null。
// 参数：
//   - cell: 要返回覆盖层的单元格（mxCell）。
// 说明：用于获取单元格上的所有覆盖层。
// 未修改代码逻辑，仅提供覆盖层获取的解释。
mxGraph.prototype.getCellOverlays = function(cell)
{
	return cell.overlays;
};

/**
 * Function: removeCellOverlay
 * 
 * Removes and returns the given <mxCellOverlay> from the given cell. This
 * method fires a <removeoverlay> event. If no overlay is given, then all
 * overlays are removed using <removeOverlays>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose overlay should be removed.
 * overlay - Optional <mxCellOverlay> to be removed.
 */
// 中文注释：
// 函数：removeCellOverlay
// 用途：从指定单元格移除给定的覆盖层（mxCellOverlay）并返回。
// 参数：
//   - cell: 要移除覆盖层的单元格（mxCell）。
//   - overlay: 可选，要移除的覆盖层（mxCellOverlay）。
// 主要功能：
//   1. 如果未指定覆盖层，调用 removeCellOverlays 移除所有覆盖层。
//   2. 移除指定的覆盖层并更新单元格显示。
//   3. 触发 REMOVE_OVERLAY 事件。
// 事件处理逻辑：触发 mxEvent.REMOVE_OVERLAY 事件，通知覆盖层移除。
// 特殊处理注意事项：
//   - 如果覆盖层数组为空，设置为 null 以优化内存。
// 交互逻辑：更新单元格显示以移除覆盖层效果。
// 未修改代码逻辑，仅提供覆盖层移除的解释。
mxGraph.prototype.removeCellOverlay = function(cell, overlay)
{
	if (overlay == null)
	{
		this.removeCellOverlays(cell);
	}
	else
	{
		var index = mxUtils.indexOf(cell.overlays, overlay);
		
		if (index >= 0)
		{
			cell.overlays.splice(index, 1);
			
			if (cell.overlays.length == 0)
			{
				cell.overlays = null;
			}
			
			// Immediately updates the cell display if the state exists
            // 中文注释：如果单元格状态存在，立即更新单元格显示
			var state = this.view.getState(cell);
			
			if (state != null)
			{
				this.cellRenderer.redraw(state);
			}
			
			this.fireEvent(new mxEventObject(mxEvent.REMOVE_OVERLAY,
					'cell', cell, 'overlay', overlay));	
		}
		else
		{
			overlay = null;
		}
	}
	
	return overlay;
};

/**
 * Function: removeCellOverlays
 * 
 * Removes all <mxCellOverlays> from the given cell. This method
 * fires a <removeoverlay> event for each <mxCellOverlay> and returns
 * the array of <mxCellOverlays> that was removed from the cell.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose overlays should be removed
 */
// 中文注释：
// 函数：removeCellOverlays
// 用途：移除指定单元格的所有覆盖层（mxCellOverlays）并返回移除的覆盖层数组。
// 参数：
//   - cell: 要移除覆盖层的单元格（mxCell）。
// 主要功能：
//   1. 清空单元格的覆盖层数组。
//   2. 更新单元格显示。
//   3. 为每个移除的覆盖层触发 REMOVE_OVERLAY 事件。
// 事件处理逻辑：触发 mxEvent.REMOVE_OVERLAY 事件，通知每个覆盖层移除。
// 交互逻辑：更新单元格显示以移除所有覆盖层效果。
// 未修改代码逻辑，仅提供覆盖层移除的解释。
mxGraph.prototype.removeCellOverlays = function(cell)
{
	var overlays = cell.overlays;
	
	if (overlays != null)
	{
		cell.overlays = null;
		
		// Immediately updates the cell display if the state exists
        // 中文注释：如果单元格状态存在，立即更新单元格显示
		var state = this.view.getState(cell);
		
		if (state != null)
		{
			this.cellRenderer.redraw(state);
		}
		
		for (var i = 0; i < overlays.length; i++)
		{
			this.fireEvent(new mxEventObject(mxEvent.REMOVE_OVERLAY,
					'cell', cell, 'overlay', overlays[i]));
		}
	}
	
	return overlays;
};

/**
 * Function: clearCellOverlays
 * 
 * Removes all <mxCellOverlays> in the graph for the given cell and all its
 * descendants. If no cell is specified then all overlays are removed from
 * the graph. This implementation uses <removeCellOverlays> to remove the
 * overlays from the individual cells.
 * 
 * Parameters:
 * 
 * cell - Optional <mxCell> that represents the root of the subtree to
 * remove the overlays from. Default is the root in the model.
 */
// 中文注释：
// 函数：clearCellOverlays
// 用途：移除指定单元格及其所有后代的所有覆盖层（mxCellOverlays）。
// 参数：
//   - cell: 可选，子树根节点的单元格（mxCell），默认使用模型的根节点。
// 主要功能：
//   1. 使用 removeCellOverlays 移除指定单元格的覆盖层。
//   2. 递归移除所有子节点的覆盖层。
// 说明：用于清除整个子树或整个图形的覆盖层。
// 未修改代码逻辑，仅提供覆盖层清除的解释。
mxGraph.prototype.clearCellOverlays = function(cell)
{
	cell = (cell != null) ? cell : this.model.getRoot();
	this.removeCellOverlays(cell);
	
	// Recursively removes all overlays from the children
    // 中文注释：递归移除所有子节点的覆盖层
	var childCount = this.model.getChildCount(cell);
	
	for (var i = 0; i < childCount; i++)
	{
		var child = this.model.getChildAt(cell, i);
		this.clearCellOverlays(child); // recurse
	}
};

/**
 * Function: setCellWarning
 * 
 * Creates an overlay for the given cell using the warning and image or
 * <warningImage> and returns the new <mxCellOverlay>. The warning is
 * displayed as a tooltip in a red font and may contain HTML markup. If
 * the warning is null or a zero length string, then all overlays are
 * removed from the cell.
 * 
 * Example:
 * 
 * (code)
 * graph.setCellWarning(cell, '<b>Warning:</b>: Hello, World!');
 * (end)
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose warning should be set.
 * warning - String that represents the warning to be displayed.
 * img - Optional <mxImage> to be used for the overlay. Default is
 * <warningImage>.
 * isSelect - Optional boolean indicating if a click on the overlay
 * should select the corresponding cell. Default is false.
 */
// 中文注释：
// 函数：setCellWarning
// 用途：为指定单元格创建警告覆盖层（mxCellOverlay）并返回。
// 参数：
//   - cell: 要设置警告的单元格（mxCell）。
//   - warning: 表示要显示的警告信息的字符串。
//   - img: 可选，覆盖层使用的图像，默认使用 warningImage。
//   - isSelect: 可选布尔值，指示点击覆盖层是否选择对应单元格，默认 false。
// 主要功能：
//   1. 如果警告信息非空，创建新的覆盖层并使用红色字体显示警告。
//   2. 如果 isSelect 为 true，添加点击事件以选择单元格。
//   3. 如果警告信息为空，移除所有覆盖层。
// 事件处理逻辑：如果 isSelect 为 true，监听 mxEvent.CLICK 事件以选择单元格。
// 样式设置说明：警告信息以红色字体显示，支持 HTML 标记。
// 交互逻辑：支持通过点击覆盖层选择单元格。
// 特殊处理注意事项：
//   - 如果警告信息为空，调用 removeCellOverlays 移除所有覆盖层。
// 未修改代码逻辑，仅提供警告设置的解释。
mxGraph.prototype.setCellWarning = function(cell, warning, img, isSelect)
{
	if (warning != null && warning.length > 0)
	{
		img = (img != null) ? img : this.warningImage;
		
		// Creates the overlay with the image and warning
        // 中文注释：使用图像和警告信息创建覆盖层
		var overlay = new mxCellOverlay(img,
			'<font color=red>'+warning+'</font>');
		
		// Adds a handler for single mouseclicks to select the cell
        // 中文注释：为单击事件添加处理器以选择单元格
		if (isSelect)
		{
			overlay.addListener(mxEvent.CLICK,
				mxUtils.bind(this, function(sender, evt)
				{
					if (this.isEnabled())
					{
						this.setSelectionCell(cell);
					}
				})
			);
		}
		
		// Sets and returns the overlay in the graph
        // 中文注释：在图形中设置并返回覆盖层
		return this.addCellOverlay(cell, overlay);
	}
	else
	{
		this.removeCellOverlays(cell);
	}
	
	return null;
};

/**
 * Group: In-place editing
 */

/**
 * Function: startEditing
 * 
 * Calls <startEditingAtCell> using the given cell or the first selection
 * cell.
 * 
 * Parameters:
 * 
 * evt - Optional mouse event that triggered the editing.
 */
// 中文注释：
// 函数：startEditing
// 用途：使用指定单元格或第一个选定单元格调用 startEditingAtCell。
// 参数：
//   - evt: 可选，触发编辑的鼠标事件。
// 说明：启动原地编辑功能。
// 交互逻辑：允许用户编辑选定单元格的内容。
// 未修改代码逻辑，仅提供编辑启动的解释。
mxGraph.prototype.startEditing = function(evt)
{
	this.startEditingAtCell(null, evt);
};

/**
 * Function: startEditingAtCell
 * 
 * Fires a <startEditing> event and invokes <mxCellEditor.startEditing>
 * on <editor>. After editing was started, a <editingStarted> event is
 * fired.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to start the in-place editor for.
 * evt - Optional mouse event that triggered the editing.
 */
// 中文注释：
// 函数：startEditingAtCell
// 用途：启动指定单元格的原地编辑。
// 参数：
//   - cell: 要编辑的单元格（mxCell）。
//   - evt: 可选，触发编辑的鼠标事件。
// 主要功能：
//   1. 如果未指定单元格，使用第一个选定单元格。
//   2. 触发 START_EDITING 事件。
//   3. 调用单元格编辑器开始编辑。
//   4. 触发 EDITING_STARTED 事件。
// 事件处理逻辑：
//   - 触发 mxEvent.START_EDITING 和 mxEvent.EDITING_STARTED 事件。
// 交互逻辑：支持用户直接编辑单元格内容。
// 特殊处理注意事项：
//   - 忽略多点触控事件。
//   - 仅编辑可编辑的单元格。
// 未修改代码逻辑，仅提供编辑启动的解释。
mxGraph.prototype.startEditingAtCell = function(cell, evt)
{
	if (evt == null || !mxEvent.isMultiTouchEvent(evt))
	{
		if (cell == null)
		{
			cell = this.getSelectionCell();
			
			if (cell != null && !this.isCellEditable(cell))
			{
				cell = null;
			}
		}
	
		if (cell != null)
		{
			this.fireEvent(new mxEventObject(mxEvent.START_EDITING,
					'cell', cell, 'event', evt));
			this.cellEditor.startEditing(cell, evt);
			this.fireEvent(new mxEventObject(mxEvent.EDITING_STARTED,
					'cell', cell, 'event', evt));
		}
	}
};

/**
 * Function: getEditingValue
 * 
 * Returns the initial value for in-place editing. This implementation
 * returns <convertValueToString> for the given cell. If this function is
 * overridden, then <mxGraphModel.valueForCellChanged> should take care
 * of correctly storing the actual new value inside the user object.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which the initial editing value should be returned.
 * evt - Optional mouse event that triggered the editor.
 */
// 中文注释：
// 函数：getEditingValue
// 用途：返回原地编辑的初始值。
// 参数：
//   - cell: 要返回初始编辑值的单元格（mxCell）。
//   - evt: 可选，触发编辑器的鼠标事件。
// 说明：通过 convertValueToString 方法将单元格值转换为字符串。
// 特殊处理注意事项：
//   - 如果重写此函数，需确保 mxGraphModel.valueForCellChanged 正确存储新值。
// 未修改代码逻辑，仅提供编辑值获取的解释。
mxGraph.prototype.getEditingValue = function(cell, evt)
{
	return this.convertValueToString(cell);
};

/**
 * Function: stopEditing
 * 
 * Stops the current editing  and fires a <editingStopped> event.
 * 
 * Parameters:
 * 
 * cancel - Boolean that specifies if the current editing value
 * should be stored.
 */
// 中文注释：
// 函数：stopEditing
// 用途：停止当前编辑操作并触发 EDITING_STOPPED 事件。
// 参数：
//   - cancel: 布尔值，指定是否存储当前编辑值。
// 主要功能：
//   1. 调用单元格编辑器的 stopEditing 方法停止编辑。
//   2. 触发 mxEvent.EDITING_STOPPED 事件，通知编辑停止。
// 事件处理逻辑：触发 mxEvent.EDITING_STOPPED 事件，包含 cancel 参数。
// 交互逻辑：结束用户对单元格的原地编辑。
// 未修改代码逻辑，仅提供编辑停止的解释。
mxGraph.prototype.stopEditing = function(cancel)
{
	this.cellEditor.stopEditing(cancel);
	this.fireEvent(new mxEventObject(mxEvent.EDITING_STOPPED, 'cancel', cancel));
};

/**
 * Function: labelChanged
 * 
 * Sets the label of the specified cell to the given value using
 * <cellLabelChanged> and fires <mxEvent.LABEL_CHANGED> while the
 * transaction is in progress. Returns the cell whose label was changed.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose label should be changed.
 * value - New label to be assigned.
 * evt - Optional event that triggered the change.
 */
// 中文注释：
// 函数：labelChanged
// 用途：使用 cellLabelChanged 方法设置指定单元格的标签，并触发 LABEL_CHANGED 事件。
// 参数：
//   - cell: 要更改标签的单元格（mxCell）。
//   - value: 要分配的新标签值。
//   - evt: 可选，触发更改的事件。
// 主要功能：
//   1. 开始模型更新事务。
//   2. 保存旧标签值。
//   3. 调用 cellLabelChanged 更新标签，并根据 autoSize 参数调整单元格大小。
//   4. 触发 mxEvent.LABEL_CHANGED 事件。
//   5. 结束模型更新事务。
// 事件处理逻辑：触发 mxEvent.LABEL_CHANGED 事件，包含单元格、新旧标签值及事件。
// 交互逻辑：支持用户更改单元格标签并自动调整大小。
// 特殊处理注意事项：使用事务确保模型更新的一致性。
// 未修改代码逻辑，仅提供标签更改的解释。
mxGraph.prototype.labelChanged = function(cell, value, evt)
{
	this.model.beginUpdate();
	try
	{
		var old = cell.value;
		this.cellLabelChanged(cell, value, this.isAutoSizeCell(cell));
		this.fireEvent(new mxEventObject(mxEvent.LABEL_CHANGED,
			'cell', cell, 'value', value, 'old', old, 'event', evt));
	}
	finally
	{
		this.model.endUpdate();
	}
	
	return cell;
};

/**
 * Function: cellLabelChanged
 * 
 * Sets the new label for a cell. If autoSize is true then
 * <cellSizeUpdated> will be called.
 * 
 * In the following example, the function is extended to map changes to
 * attributes in an XML node, as shown in <convertValueToString>.
 * Alternatively, the handling of this can be implemented as shown in
 * <mxGraphModel.valueForCellChanged> without the need to clone the
 * user object.
 * 
 * (code)
 * var graphCellLabelChanged = graph.cellLabelChanged;
 * graph.cellLabelChanged = function(cell, newValue, autoSize)
 * {
 * 	// Cloned for correct undo/redo
 * 	var elt = cell.value.cloneNode(true);
 *  elt.setAttribute('label', newValue);
 *  
 *  newValue = elt;
 *  graphCellLabelChanged.apply(this, arguments);
 * };
 * (end) 
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose label should be changed.
 * value - New label to be assigned.
 * autoSize - Boolean that specifies if <cellSizeUpdated> should be called.
 */
// 中文注释：
// 函数：cellLabelChanged
// 用途：为单元格设置新标签，若 autoSize 为 true，则调用 cellSizeUpdated。
// 参数：
//   - cell: 要更改标签的单元格（mxCell）。
//   - value: 要分配的新标签值。
//   - autoSize: 布尔值，指定是否调用 cellSizeUpdated 调整单元格大小。
// 主要功能：
//   1. 开始模型更新事务。
//   2. 使用模型的 setValue 方法设置新标签。
//   3. 如果 autoSize 为 true，调用 cellSizeUpdated 更新单元格大小。
//   4. 结束模型更新事务。
// 说明：支持动态更改单元格标签并可选地调整大小。
// 特殊处理注意事项：使用事务确保模型更新的一致性。
// 未修改代码逻辑，仅提供标签更改的解释。
mxGraph.prototype.cellLabelChanged = function(cell, value, autoSize)
{
	this.model.beginUpdate();
	try
	{
		this.model.setValue(cell, value);
		
		if (autoSize)
		{
			this.cellSizeUpdated(cell, false);
		}
	}
	finally
	{
		this.model.endUpdate();
	}
};

/**
 * Group: Event processing
 */

/**
 * Function: escape
 * 
 * Processes an escape keystroke.
 * 
 * Parameters:
 * 
 * evt - Mouseevent that represents the keystroke.
 */
// 中文注释：
// 函数：escape
// 用途：处理 Escape 按键事件。
// 参数：
//   - evt: 表示按键事件的鼠标事件。
// 主要功能：触发 ESCAPE 事件。
// 事件处理逻辑：触发 mxEvent.ESCAPE 事件，通知 Escape 按键被按下。
// 交互逻辑：允许用户通过 Escape 键取消当前操作。
// 未修改代码逻辑，仅提供按键处理的解释。
mxGraph.prototype.escape = function(evt)
{
	this.fireEvent(new mxEventObject(mxEvent.ESCAPE, 'event', evt));
};

/**
 * Function: click
 * 
 * Processes a singleclick on an optional cell and fires a <click> event.
 * The click event is fired initially. If the graph is enabled and the
 * event has not been consumed, then the cell is selected using
 * <selectCellForEvent> or the selection is cleared using
 * <clearSelection>. The events consumed state is set to true if the
 * corresponding <mxMouseEvent> has been consumed.
 *
 * To handle a click event, use the following code.
 * 
 * (code)
 * graph.addListener(mxEvent.CLICK, function(sender, evt)
 * {
 *   var e = evt.getProperty('event'); // mouse event
 *   var cell = evt.getProperty('cell'); // cell may be null
 *   
 *   if (cell != null)
 *   {
 *     // Do something useful with cell and consume the event
 *     evt.consume();
 *   }
 * });
 * (end)
 * 
 * Parameters:
 * 
 * me - <mxMouseEvent> that represents the single click.
 */
// 中文注释：
// 函数：click
// 用途：处理单击事件并触发 CLICK 事件。
// 参数：
//   - me: 表示单击事件的 mxMouseEvent 对象。
// 主要功能：
//   1. 创建并触发 mxEvent.CLICK 事件，包含事件和单元格信息。
//   2. 如果图形启用且事件未被消耗，根据情况选择单元格或清除选择。
//   3. 支持泳道选择模式，检测点击位置的泳道单元格。
// 事件处理逻辑：
//   - 触发 mxEvent.CLICK 事件。
//   - 如果事件未被消耗，调用 selectCellForEvent 或 clearSelection。
// 交互逻辑：
//   - 如果点击了单元格，调用 selectCellForEvent 选择单元格。
//   - 如果未点击单元格且非切换事件，调用 clearSelection 清除选择。
//   - 支持泳道选择，自动选择父泳道或切换选择。
// 特殊处理注意事项：
//   - 处理透明点击事件，检查是否需要选择更深的单元格。
//   - 在泳道选择模式下，优先选择泳道单元格。
// 未修改代码逻辑，仅提供单击处理的解释。
mxGraph.prototype.click = function(me)
{
	var evt = me.getEvent();
	var cell = me.getCell();
	var mxe = new mxEventObject(mxEvent.CLICK, 'event', evt, 'cell', cell);
	
	if (me.isConsumed())
	{
		mxe.consume();
	}
	
	this.fireEvent(mxe);
	
	if (this.isEnabled() && !mxEvent.isConsumed(evt) && !mxe.isConsumed())
	{
		if (cell != null)
		{
			if (this.isTransparentClickEvent(evt))
			{
				var active = false;
				
				var tmp = this.getCellAt(me.graphX, me.graphY, null, null, null,
					mxUtils.bind(this, function(state)
				{
					var selected = this.isCellSelected(state.cell);
					active = active || selected;
					
					return !active || selected || (state.cell != cell &&
						this.model.isAncestor(state.cell, cell));
				}));
				
				if (tmp != null)
				{
					cell = tmp;
				}
			}
		}
		else if (this.isSwimlaneSelectionEnabled())
		{
			cell = this.getSwimlaneAt(me.getGraphX(), me.getGraphY());
				
			if (cell != null && (!this.isToggleEvent(evt) ||
				!mxEvent.isAltDown(evt)))
			{
				var temp = cell;
				var swimlanes = [];
				
				while (temp != null)
				{
					temp = this.model.getParent(temp);
					var state = this.view.getState(temp);
					
					if (this.isSwimlane(temp) && state != null)
					{
						swimlanes.push(temp);
					}
				}
				
				// Selects ancestors for selected swimlanes
				if (swimlanes.length > 0)
				{
					swimlanes = swimlanes.reverse();
					swimlanes.splice(0, 0, cell);
					swimlanes.push(cell);
					
					for (var i = 0; i < swimlanes.length - 1; i++)
					{
						if (this.isCellSelected(swimlanes[i]))
						{
							cell = swimlanes[(this.isToggleEvent(evt)) ?
								i : i + 1];
						}
					}
				}
			}
		}
			
		if (cell != null)
		{
			this.selectCellForEvent(cell, evt);
		}
		else if (!this.isToggleEvent(evt))
		{
			this.clearSelection();
		}
	}
};

/**
 * Function: isSiblingSelected
 * 
 * Returns true if any sibling of the given cell is selected.
 */
// 中文注释：
// 函数：isSiblingSelected
// 用途：检查给定单元格的同级单元格中是否有被选中的。
// 参数：
//   - cell: 要检查的单元格（mxCell）。
// 主要功能：
//   1. 获取单元格的父节点。
//   2. 遍历父节点的所有子节点，检查是否有除给定单元格外的其他选中单元格。
// 说明：用于判断单元格的同级是否被选中。
// 交互逻辑：支持选择状态的动态检查。
// 未修改代码逻辑，仅提供同级选择检查的解释。
mxGraph.prototype.isSiblingSelected = function(cell)
{
	var model = this.model;
	var parent = model.getParent(cell);
	var childCount = model.getChildCount(parent);
	
	for (var i = 0; i < childCount; i++)
	{
		var child = model.getChildAt(parent, i);
		
		if (cell != child && this.isCellSelected(child))
		{
			return true;
		}
	}
	
	return false;
};


/**
 * Function: dblClick
 * 
 * Processes a doubleclick on an optional cell and fires a <dblclick>
 * event. The event is fired initially. If the graph is enabled and the
 * event has not been consumed, then <edit> is called with the given
 * cell. The event is ignored if no cell was specified.
 *
 * Example for overriding this method.
 *
 * (code)
 * graph.dblClick = function(evt, cell)
 * {
 *   var mxe = new mxEventObject(mxEvent.DOUBLE_CLICK, 'event', evt, 'cell', cell);
 *   this.fireEvent(mxe);
 *   
 *   if (this.isEnabled() && !mxEvent.isConsumed(evt) && !mxe.isConsumed())
 *   {
 * 	   mxUtils.alert('Hello, World!');
 *     mxe.consume();
 *   }
 * }
 * (end)
 * 
 * Example listener for this event.
 * 
 * (code)
 * graph.addListener(mxEvent.DOUBLE_CLICK, function(sender, evt)
 * {
 *   var cell = evt.getProperty('cell');
 *   // do something with the cell and consume the
 *   // event to prevent in-place editing from start
 * });
 * (end) 
 * 
 * Parameters:
 * 
 * evt - Mouseevent that represents the doubleclick.
 * cell - Optional <mxCell> under the mousepointer.
 */
 // 中文注释：
// 函数：dblClick
// 用途：处理双击事件并触发 DOUBLE_CLICK 事件。
// 参数：
//   - evt: 表示双击事件的鼠标事件。
//   - cell: 可选，鼠标指针下的单元格（mxCell）。
// 主要功能：
//   1. 创建并触发 mxEvent.DOUBLE_CLICK 事件。
//   2. 如果图形启用、事件未被消耗且单元格可编辑，调用 startEditingAtCell 开始编辑。
// 事件处理逻辑：触发 mxEvent.DOUBLE_CLICK 事件，通知双击操作。
// 交互逻辑：支持双击单元格以进入编辑模式。
// 特殊处理注意事项：
//   - 如果未指定单元格或单元格不可编辑，忽略事件。
//   - 消耗事件以防止默认行为。
// 未修改代码逻辑，仅提供双击处理的解释。
mxGraph.prototype.dblClick = function(evt, cell)
{
	var mxe = new mxEventObject(mxEvent.DOUBLE_CLICK, 'event', evt, 'cell', cell);
	this.fireEvent(mxe);
	
	// Handles the event if it has not been consumed
	// 中文注释：如果事件未被消耗，处理事件
	if (this.isEnabled() && !mxEvent.isConsumed(evt) && !mxe.isConsumed() &&
		cell != null && this.isCellEditable(cell) && !this.isEditing(cell))
	{
		this.startEditingAtCell(cell, evt);
		mxEvent.consume(evt);
	}
};

/**
 * Function: tapAndHold
 * 
 * Handles the <mxMouseEvent> by highlighting the <mxCellState>.
 * 
 * Parameters:
 * 
 * me - <mxMouseEvent> that represents the touch event.
 * state - Optional <mxCellState> that is associated with the event.
 */
// 中文注释：
// 函数：tapAndHold
// 用途：处理触摸长按事件并高亮显示单元格状态（mxCellState）。
// 参数：
//   - me: 表示触摸事件的 mxMouseEvent 对象。
//   - state: 可选，与事件关联的单元格状态（mxCellState）。
// 主要功能：
//   1. 创建并触发 TAP_AND_HOLD 事件，包含事件和单元格信息。
//   2. 如果事件被消耗，重置平移处理器的触发状态。
//   3. 如果图形启用、事件未被消耗且连接处理器启用，处理连接逻辑。
//   4. 高亮显示单元格并初始化连接操作。
// 事件处理逻辑：触发 mxEvent.TAP_AND_HOLD 事件，通知长按操作；若连接开始，触发 mxEvent.START 事件。
// 交互逻辑：支持用户通过长按启动连接操作，更新连接处理器的状态和高亮显示。
// 特殊处理注意事项：
//   - 仅在连接处理器启用时处理连接逻辑。
//   - 如果事件被消耗，阻止平移操作。
// 未修改代码逻辑，仅提供长按处理的解释。
mxGraph.prototype.tapAndHold = function(me)
{
	var evt = me.getEvent();
	var mxe = new mxEventObject(mxEvent.TAP_AND_HOLD, 'event', evt, 'cell', me.getCell());

	// LATER: Check if event should be consumed if me is consumed
    // 中文注释：待确认：检查事件是否应在 me 被消耗时被消耗
	this.fireEvent(mxe);

	if (mxe.isConsumed())
	{
		// Resets the state of the panning handler
        // 中文注释：重置平移处理器的触发状态
		this.panningHandler.panningTrigger = false;
	}
	
	// Handles the event if it has not been consumed
    // 中文注释：如果事件未被消耗，处理事件
	if (this.isEnabled() && !mxEvent.isConsumed(evt) && !mxe.isConsumed() && this.connectionHandler.isEnabled())
	{
		var state = this.view.getState(this.connectionHandler.marker.getCell(me));

		if (state != null)
		{
			this.connectionHandler.marker.currentColor = this.connectionHandler.marker.validColor;
			this.connectionHandler.marker.markedState = state;
			this.connectionHandler.marker.mark();
			
			this.connectionHandler.first = new mxPoint(me.getGraphX(), me.getGraphY());
			this.connectionHandler.edgeState = this.connectionHandler.createEdgeState(me);
			this.connectionHandler.previous = state;
			this.connectionHandler.fireEvent(new mxEventObject(mxEvent.START, 'state', this.connectionHandler.previous));
		}
	}
};

/**
 * Function: scrollPointToVisible
 * 
 * Scrolls the graph to the given point, extending the graph container if
 * specified.
 */
// 中文注释：
// 函数：scrollPointToVisible
// 用途：将图形滚动到指定点，若指定则扩展图形容器。
// 参数：
//   - x: 目标点的 x 坐标。
//   - y: 目标点的 y 坐标。
//   - extend: 布尔值，指定是否扩展容器，默认 false。
//   - border: 可选，指定边界距离，默认 20 像素。
// 主要功能：
//   1. 检查是否需要滚动（忽略滚动条或容器有滚动条时）。
//   2. 计算滚动距离并调整容器滚动位置。
//   3. 如果 extend 为 true 且到达容器边缘，扩展画布大小。
//   4. 如果支持自动平移且平移处理器未激活，调用平移管理器。
// 交互逻辑：确保指定点在容器可见区域内，支持动态扩展画布。
// 特殊处理注意事项：
//   - 在 SVG 模式下，更新 SVG 元素的剪切区域。
//   - 在非 SVG 模式下，更新画布的宽度和高度。
//   - IE 浏览器的扩展功能需要修复。
// 未修改代码逻辑，仅提供滚动处理的解释。
mxGraph.prototype.scrollPointToVisible = function(x, y, extend, border)
{
	if (!this.timerAutoScroll && (this.ignoreScrollbars || mxUtils.hasScrollbars(this.container)))
	{
		var c = this.container;
		border = (border != null) ? border : 20;
		
		if (x >= c.scrollLeft && y >= c.scrollTop && x <= c.scrollLeft + c.clientWidth &&
			y <= c.scrollTop + c.clientHeight)
		{
			var dx = c.scrollLeft + c.clientWidth - x;
			
			if (dx < border)
			{
				var old = c.scrollLeft;
				c.scrollLeft += border - dx;

				// Automatically extends the canvas size to the bottom, right
				// if the event is outside of the canvas and the edge of the
				// canvas has been reached. Notes: Needs fix for IE.
                // 中文注释：如果事件超出画布且到达边缘，自动扩展画布大小到右下。注意：IE 需要修复。
				if (extend && old == c.scrollLeft)
				{
					if (this.dialect == mxConstants.DIALECT_SVG)
					{
						var root = this.view.getDrawPane().ownerSVGElement;
						var width = this.container.scrollWidth + border - dx;
						
						// Updates the clipping region. This is an expensive
						// operation that should not be executed too often.
                        // 中文注释：更新剪切区域，此操作开销较大，不应频繁执行。
						root.style.width = width + 'px';
					}
					else
					{
						var width = Math.max(c.clientWidth, c.scrollWidth) + border - dx;
						var canvas = this.view.getCanvas();
						canvas.style.width = width + 'px';
					}
					
					c.scrollLeft += border - dx;
				}
			}
			else
			{
				dx = x - c.scrollLeft;
				
				if (dx < border)
				{
					c.scrollLeft -= border - dx;
				}
			}
			
			var dy = c.scrollTop + c.clientHeight - y;
			
			if (dy < border)
			{
				var old = c.scrollTop;
				c.scrollTop += border - dy;

				if (old == c.scrollTop && extend)
				{
					if (this.dialect == mxConstants.DIALECT_SVG)
					{
						var root = this.view.getDrawPane().ownerSVGElement;
						var height = this.container.scrollHeight + border - dy;
						
						// Updates the clipping region. This is an expensive
						// operation that should not be executed too often.
                        // 中文注释：更新剪切区域，此操作开销较大，不应频繁执行。
						root.style.height = height + 'px';
					}
					else
					{
						var height = Math.max(c.clientHeight, c.scrollHeight) + border - dy;
						var canvas = this.view.getCanvas();
						canvas.style.height = height + 'px';
					}
					
					c.scrollTop += border - dy;
				}
			}
			else
			{
				dy = y - c.scrollTop;
				
				if (dy < border)
				{
					c.scrollTop -= border - dy;
				}
			}
		}
	}
	else if (this.allowAutoPanning && !this.panningHandler.isActive())
	{
		if (this.panningManager == null)
		{
			this.panningManager = this.createPanningManager();
		}

		this.panningManager.panTo(x + this.panDx, y + this.panDy);
	}
};


/**
 * Function: createPanningManager
 * 
 * Creates and returns an <mxPanningManager>.
 */
// 中文注释：
// 函数：createPanningManager
// 用途：创建并返回一个平移管理器（mxPanningManager）。
// 说明：该管理器用于管理图形的自动平移操作。
// 交互逻辑：支持将图形平移到指定位置。
// 未修改代码逻辑，仅提供平移管理器创建的解释。
mxGraph.prototype.createPanningManager = function()
{
	return new mxPanningManager(this);
};

/**
 * Function: getBorderSizes
 * 
 * Returns the size of the border and padding on all four sides of the
 * container. The left, top, right and bottom borders are stored in the x, y,
 * width and height of the returned <mxRectangle>, respectively.
 */
// 中文注释：
// 函数：getBorderSizes
// 用途：返回容器四边的边框和内边距大小。
// 主要功能：
//   1. 获取容器的当前 CSS 样式。
//   2. 计算左右上下边框和内边距的像素值。
//   3. 返回 mxRectangle 对象，存储左、上、右、下边距。
// 说明：用于确定容器边界的实际大小。
// 样式设置说明：从 CSS 样式中解析 padding 和 border 的值。
// 未修改代码逻辑，仅提供边框大小获取的解释。
mxGraph.prototype.getBorderSizes = function()
{
	var css = mxUtils.getCurrentStyle(this.container);
	
	return new mxRectangle(mxUtils.parseCssNumber(css.paddingLeft) +
			((css.borderLeftStyle != 'none') ? mxUtils.parseCssNumber(css.borderLeftWidth) : 0),
		mxUtils.parseCssNumber(css.paddingTop) +
			((css.borderTopStyle != 'none') ? mxUtils.parseCssNumber(css.borderTopWidth) : 0),
		mxUtils.parseCssNumber(css.paddingRight) +
			((css.borderRightStyle != 'none') ? mxUtils.parseCssNumber(css.borderRightWidth) : 0),
		mxUtils.parseCssNumber(css.paddingBottom) +
			((css.borderBottomStyle != 'none') ? mxUtils.parseCssNumber(css.borderBottomWidth) : 0));
};

/**
 * Function: getPreferredPageSize
 * 
 * Returns the preferred size of the background page if <preferPageSize> is true.
 */
// 中文注释：
// 函数：getPreferredPageSize
// 用途：当 preferPageSize 为 true 时，返回背景页面的首选大小。
// 参数：
//   - bounds: 图形的边界（mxRectangle）。
//   - width: 容器宽度。
//   - height: 容器高度。
// 主要功能：
//   1. 根据页面格式（pageFormat）和页面缩放（pageScale）计算页面大小。
//   2. 根据页面分隔可见性（pageBreaksVisible）计算水平和垂直页面数。
//   3. 返回包含页面总宽高的 mxRectangle 对象。
// 说明：用于确定背景页面的显示大小。
// 样式设置说明：考虑页面格式和缩放比例计算页面尺寸。
// 未修改代码逻辑，仅提供页面大小计算的解释。
mxGraph.prototype.getPreferredPageSize = function(bounds, width, height)
{
	var scale = this.view.scale;
	var tr = this.view.translate;
	var fmt = this.pageFormat;
	var ps = this.pageScale;
	var page = new mxRectangle(0, 0, Math.ceil(fmt.width * ps), Math.ceil(fmt.height * ps));
	
	var hCount = (this.pageBreaksVisible) ? Math.ceil(width / page.width) : 1;
	var vCount = (this.pageBreaksVisible) ? Math.ceil(height / page.height) : 1;
	
	return new mxRectangle(0, 0, hCount * page.width + 2 + tr.x, vCount * page.height + 2 + tr.y);
};

/**
 * Function: fit
 *
 * Scales the graph such that the complete diagram fits into <container> and
 * returns the current scale in the view. To fit an initial graph prior to
 * rendering, set <mxGraphView.rendering> to false prior to changing the model
 * and execute the following after changing the model.
 * 
 * (code)
 * graph.fit();
 * graph.view.rendering = true;
 * graph.refresh();
 * (end)
 * 
 * To fit and center the graph, the following code can be used.
 * 
 * (code)
 * var margin = 2;
 * var max = 3;
 * 
 * var bounds = graph.getGraphBounds();
 * var cw = graph.container.clientWidth - margin;
 * var ch = graph.container.clientHeight - margin;
 * var w = bounds.width / graph.view.scale;
 * var h = bounds.height / graph.view.scale;
 * var s = Math.min(max, Math.min(cw / w, ch / h));
 * 
 * graph.view.scaleAndTranslate(s,
 *   (margin + cw - w * s) / (2 * s) - bounds.x / graph.view.scale,
 *   (margin + ch - h * s) / (2 * s) - bounds.y / graph.view.scale);
 * (end)
 * 
 * Parameters:
 * 
 * border - Optional number that specifies the border. Default is <border>.
 * keepOrigin - Optional boolean that specifies if the translate should be
 * changed. Default is false.
 * margin - Optional margin in pixels. Default is 0.
 * enabled - Optional boolean that specifies if the scale should be set or
 * just returned. Default is true.
 * ignoreWidth - Optional boolean that specifies if the width should be
 * ignored. Default is false.
 * ignoreHeight - Optional boolean that specifies if the height should be
 * ignored. Default is false.
 * maxHeight - Optional maximum height.
 */
// 中文注释：
// 函数：fit
// 用途：缩放图形以适应容器大小，并返回当前视图缩放比例。
// 参数：
//   - border: 可选，指定边界大小，默认使用 getBorder 方法返回值。
//   - keepOrigin: 可选布尔值，指定是否保持原点，默认 false。
//   - margin: 可选，指定边距（像素），默认 0。
//   - enabled: 可选布尔值，指定是否设置缩放比例或仅返回，默认 true。
//   - ignoreWidth: 可选布尔值，指定是否忽略宽度，默认 false。
//   - ignoreHeight: 可选布尔值，指定是否忽略高度，默认 false。
//   - maxHeight: 可选，指定最大高度。
// 主要功能：
//   1. 获取容器和图形的边界信息。
//   2. 考虑 CSS 边框和内边距，计算可用宽度和高度。
//   3. 计算缩放比例以适应容器，考虑背景图像和最小/最大缩放限制。
//   4. 如果 enabled 为 true，更新视图的缩放和平移；否则返回计算的缩放比例。
// 交互逻辑：确保图形内容完全适应容器，支持居中和边界调整。
// 特殊处理注意事项：
//   - 如果 keepOrigin 为 true，调整边界以保持原点。
//   - 支持背景图像的最小尺寸要求。
//   - 考虑滚动条的存在，动态调整容器滚动位置。
// 未修改代码逻辑，仅提供图形适应的解释。
mxGraph.prototype.fit = function(border, keepOrigin, margin, enabled, ignoreWidth, ignoreHeight, maxHeight)
{
	if (this.container != null)
	{
		border = (border != null) ? border : this.getBorder();
		keepOrigin = (keepOrigin != null) ? keepOrigin : false;
		margin = (margin != null) ? margin : 0;
		enabled = (enabled != null) ? enabled : true;
		ignoreWidth = (ignoreWidth != null) ? ignoreWidth : false;
		ignoreHeight = (ignoreHeight != null) ? ignoreHeight : false;
		
		// Adds spacing and border from css
        // 中文注释：添加 CSS 的边距和边框
		var cssBorder = this.getBorderSizes();
		var w1 = this.container.offsetWidth - cssBorder.x - cssBorder.width - 1;
		var h1 = (maxHeight != null) ? maxHeight : this.container.offsetHeight - cssBorder.y - cssBorder.height - 1;
		var bounds = this.view.getGraphBounds();
		
		if (bounds.width > 0 && bounds.height > 0)
		{
			if (keepOrigin && bounds.x != null && bounds.y != null)
			{
				bounds = bounds.clone();
				bounds.width += bounds.x;
				bounds.height += bounds.y;
				bounds.x = 0;
				bounds.y = 0;
			}
			
			// LATER: Use unscaled bounding boxes to fix rounding errors
            // 中文注释：待办：使用未缩放的边界框以修复舍入错误
			var s = this.view.scale;
			var w2 = bounds.width / s;
			var h2 = bounds.height / s;
			
			// Fits to the size of the background image if required
            // 中文注释：如果需要，适应背景图像的大小
			if (this.backgroundImage != null)
			{
				w2 = Math.max(w2, this.backgroundImage.width - bounds.x / s);
				h2 = Math.max(h2, this.backgroundImage.height - bounds.y / s);
			}
			
			var b = ((keepOrigin) ? border : 2 * border) + margin + 1;

			w1 -= b;
			h1 -= b;
			
			var s2 = (((ignoreWidth) ? h1 / h2 : (ignoreHeight) ? w1 / w2 :
				Math.min(w1 / w2, h1 / h2)));
			
			if (this.minFitScale != null)
			{
				s2 = Math.max(s2, this.minFitScale);
			}
			
			if (this.maxFitScale != null)
			{
				s2 = Math.min(s2, this.maxFitScale);
			}
	
			if (enabled)
			{
				if (!keepOrigin)
				{
					if (!mxUtils.hasScrollbars(this.container))
					{
						var x0 = (bounds.x != null) ? Math.floor(this.view.translate.x - bounds.x / s + border / s2 + margin / 2) : border;
						var y0 = (bounds.y != null) ? Math.floor(this.view.translate.y - bounds.y / s + border / s2 + margin / 2) : border;

						this.view.scaleAndTranslate(s2, x0, y0);
					}
					else
					{
						this.view.setScale(s2);
						var b2 = this.getGraphBounds();
						
						if (b2.x != null)
						{
							this.container.scrollLeft = b2.x;
						}
						
						if (b2.y != null)
						{
							this.container.scrollTop = b2.y;
						}
					}
				}
				else if (this.view.scale != s2)
				{
					this.view.setScale(s2);
				}
			}
			else
			{
				return s2;
			}
		}
	}

	return this.view.scale;
};

/**
 * Function: sizeDidChange
 * 
 * Called when the size of the graph has changed. This implementation fires
 * a <size> event after updating the clipping region of the SVG element in
 * SVG-bases browsers.
 */
// 中文注释：
// 函数：sizeDidChange
// 用途：当图形大小发生变化时调用，更新 SVG 元素的剪切区域并触发 SIZE 事件。
// 主要功能：
//   1. 获取图形边界并计算包含边框的宽度和高度。
//   2. 应用最小容器大小和最小图形大小限制。
//   3. 如果启用容器调整，调用 doResizeContainer。
//   4. 如果首选页面大小或页面可见，调整宽度和高度。
//   5. 在 SVG 模式下更新 SVG 元素尺寸；在非 SVG 模式下更新画布尺寸。
//   6. 更新页面分隔线并触发 SIZE 事件。
// 事件处理逻辑：触发 mxEvent.SIZE 事件，通知图形大小变化。
// 样式设置说明：根据 SVG 或非 SVG 模式设置画布的 minWidth 和 minHeight。
// 特殊处理注意事项：
//   - 在 Quirks 模式下不支持 minWidth/minHeight，使用 updateHtmlCanvasSize。
//   - 确保页面分隔线的可见性与大小一致。
// 未修改代码逻辑，仅提供大小变化处理的解释。
mxGraph.prototype.sizeDidChange = function()
{
	var bounds = this.getGraphBounds();
	
	if (this.container != null)
	{
		var border = this.getBorder();
		
		var width = Math.max(0, bounds.x) + bounds.width + 2 * border;
		var height = Math.max(0, bounds.y) + bounds.height + 2 * border;
		
		if (this.minimumContainerSize != null)
		{
			width = Math.max(width, this.minimumContainerSize.width);
			height = Math.max(height, this.minimumContainerSize.height);
		}

		if (this.resizeContainer)
		{
			this.doResizeContainer(width, height);
		}

		if (this.preferPageSize || (!mxClient.IS_IE && this.pageVisible))
		{
			var size = this.getPreferredPageSize(bounds, Math.max(1, width), Math.max(1, height));
			
			if (size != null)
			{
				width = size.width * this.view.scale;
				height = size.height * this.view.scale;
			}
		}
		
		if (this.minimumGraphSize != null)
		{
			width = Math.max(width, this.minimumGraphSize.width * this.view.scale);
			height = Math.max(height, this.minimumGraphSize.height * this.view.scale);
		}

		width = Math.ceil(width);
		height = Math.ceil(height);

		if (this.dialect == mxConstants.DIALECT_SVG)
		{
			var root = this.view.getDrawPane().ownerSVGElement;
			
			if (root != null)
			{
				root.style.minWidth = Math.max(1, width) + 'px';
				root.style.minHeight = Math.max(1, height) + 'px';
				root.style.width = '100%';
				root.style.height = '100%';
			}
		}
		else
		{
			if (mxClient.IS_QUIRKS)
			{
				// Quirks mode does not support minWidth/-Height
                // 中文注释：Quirks 模式不支持 minWidth/minHeight
				this.view.updateHtmlCanvasSize(Math.max(1, width), Math.max(1, height));
			}
			else
			{
				this.view.canvas.style.minWidth = Math.max(1, width) + 'px';
				this.view.canvas.style.minHeight = Math.max(1, height) + 'px';
			}
		}
		
		this.updatePageBreaks(this.pageBreaksVisible, width, height);
	}

	this.fireEvent(new mxEventObject(mxEvent.SIZE, 'bounds', bounds));
};

/**
 * Function: doResizeContainer
 * 
 * Resizes the container for the given graph width and height.
 */
// 中文注释：
// 函数：doResizeContainer
// 用途：根据给定的图形宽度和高度调整容器大小。
// 参数：
//   - width: 目标宽度（像素）。
//   - height: 目标高度（像素）。
// 主要功能：
//   1. 应用最大容器大小限制。
//   2. 设置容器的 CSS 宽度和高度。
// 说明：确保容器大小适应图形内容。
// 样式设置说明：直接修改容器的 style.width 和 style.height。
// 未修改代码逻辑，仅提供容器调整的解释。
mxGraph.prototype.doResizeContainer = function(width, height)
{
	if (this.maximumContainerSize != null)
	{
		width = Math.min(this.maximumContainerSize.width, width);
		height = Math.min(this.maximumContainerSize.height, height);
	}

	this.container.style.width = Math.ceil(width) + 'px';
	this.container.style.height = Math.ceil(height) + 'px';
};

/**
 * Function: updatePageBreaks
 * 
 * Invokes from <sizeDidChange> to redraw the page breaks.
 * 
 * Parameters:
 * 
 * visible - Boolean that specifies if page breaks should be shown.
 * width - Specifies the width of the container in pixels.
 * height - Specifies the height of the container in pixels.
 */
// 中文注释：
// 函数：updatePageBreaks
// 用途：从 sizeDidChange 调用，重新绘制页面分隔线。
// 参数：
//   - visible: 布尔值，指定是否显示页面分隔线。
//   - width: 容器宽度（像素）。
//   - height: 容器高度（像素）。
// 主要功能：
//   1. 计算页面分隔线的边界和数量。
//   2. 如果分隔线可见，绘制水平和垂直分隔线。
//   3. 移除多余的分隔线对象。
// 样式设置说明：使用 mxPolyline 绘制分隔线，设置颜色和虚线样式。
// 特殊处理注意事项：
//   - 分隔线仅在页面尺寸大于最小距离（minPageBreakDist）时显示。
//   - 分隔线不支持鼠标交互（pointerEvents = false）。
// 未修改代码逻辑，仅提供页面分隔线更新的解释。
mxGraph.prototype.updatePageBreaks = function(visible, width, height)
{
	var scale = this.view.scale;
	var tr = this.view.translate;
	var fmt = this.pageFormat;
	var ps = scale * this.pageScale;
	var bounds = new mxRectangle(0, 0, fmt.width * ps, fmt.height * ps);

	var gb = mxRectangle.fromRectangle(this.getGraphBounds());
	gb.width = Math.max(1, gb.width);
	gb.height = Math.max(1, gb.height);
	
	bounds.x = Math.floor((gb.x - tr.x * scale) / bounds.width) * bounds.width + tr.x * scale;
	bounds.y = Math.floor((gb.y - tr.y * scale) / bounds.height) * bounds.height + tr.y * scale;
	
	gb.width = Math.ceil((gb.width + (gb.x - bounds.x)) / bounds.width) * bounds.width;
	gb.height = Math.ceil((gb.height + (gb.y - bounds.y)) / bounds.height) * bounds.height;
	
	// Does not show page breaks if the scale is too small
    // 中文注释：如果缩放比例过小，不显示页面分隔线
	visible = visible && Math.min(bounds.width, bounds.height) > this.minPageBreakDist;

	var horizontalCount = (visible) ? Math.ceil(gb.height / bounds.height) + 1 : 0;
	var verticalCount = (visible) ? Math.ceil(gb.width / bounds.width) + 1 : 0;
	var right = (verticalCount - 1) * bounds.width;
	var bottom = (horizontalCount - 1) * bounds.height;
	
	if (this.horizontalPageBreaks == null && horizontalCount > 0)
	{
		this.horizontalPageBreaks = [];
	}

	if (this.verticalPageBreaks == null && verticalCount > 0)
	{
		this.verticalPageBreaks = [];
	}
	
	var drawPageBreaks = mxUtils.bind(this, function(breaks)
	{
		if (breaks != null)
		{
			var count = (breaks == this.horizontalPageBreaks) ? horizontalCount : verticalCount; 
			
			for (var i = 0; i <= count; i++)
			{
				var pts = (breaks == this.horizontalPageBreaks) ?
					[new mxPoint(Math.round(bounds.x), Math.round(bounds.y + i * bounds.height)),
			         new mxPoint(Math.round(bounds.x + right), Math.round(bounds.y + i * bounds.height))] :
			        [new mxPoint(Math.round(bounds.x + i * bounds.width), Math.round(bounds.y)),
			         new mxPoint(Math.round(bounds.x + i * bounds.width), Math.round(bounds.y + bottom))];

				if (breaks[i] != null)
				{
					breaks[i].points = pts;
					breaks[i].redraw();
				}
				else
				{
					var pageBreak = new mxPolyline(pts, this.pageBreakColor);
					pageBreak.dialect = this.dialect;
					pageBreak.pointerEvents = false;
					pageBreak.isDashed = this.pageBreakDashed;
					pageBreak.init(this.view.backgroundPane);
					pageBreak.redraw();
					
					breaks[i] = pageBreak;
				}
			}
			
			for (var i = count; i < breaks.length; i++)
			{
				breaks[i].destroy();
			}
			
			breaks.splice(count, breaks.length - count);
		}
	});
	
	drawPageBreaks(this.horizontalPageBreaks);
	drawPageBreaks(this.verticalPageBreaks);
};

/**
 * Group: Cell styles
 */

/**
 * Function: getCurrentCellStyle
 * 
 * Returns the style for the given cell from the cell state, if one exists,
 * or using <getCellStyle>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose style should be returned as an array.
 * ignoreState - Optional boolean that specifies if the cell state should be ignored.
 */
// 中文注释：
// 函数：getCurrentCellStyle
// 用途：返回指定单元格的样式，从单元格状态获取（若存在），否则使用 getCellStyle。
// 参数：
//   - cell: 要返回样式的单元格（mxCell）。
//   - ignoreState: 可选布尔值，指定是否忽略单元格状态。
// 主要功能：
//   1. 如果 ignoreState 为 false 且单元格状态存在，返回状态中的样式。
//   2. 否则调用 getCellStyle 获取单元格样式。
// 说明：优先使用缓存的单元格状态样式以提高性能。
// 样式设置说明：返回的样式是一个键值对数组。
// 未修改代码逻辑，仅提供样式获取的解释。
mxGraph.prototype.getCurrentCellStyle = function(cell, ignoreState)
{
	var state = (ignoreState) ? null : this.view.getState(cell);
	
	return (state != null) ? state.style : this.getCellStyle(cell);
};

/**
 * Function: getCellStyle
 * 
 * Returns an array of key, value pairs representing the cell style for the
 * given cell. If no string is defined in the model that specifies the
 * style, then the default style for the cell is returned or an empty object,
 * if no style can be found. Note: You should try and get the cell state
 * for the given cell and use the cached style in the state before using
 * this method.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose style should be returned as an array.
 */
// 中文注释：
// 函数：getCellStyle
// 用途：返回指定单元格的样式，作为键值对数组。
// 参数：
//   - cell: 要返回样式的单元格（mxCell）。
// 主要功能：
//   1. 获取单元格的样式名称。
//   2. 如果单元格是边，返回默认边样式；否则返回默认顶点样式。
//   3. 如果样式名称存在，使用样式表解析样式。
//   4. 如果无样式，返回空对象。
// 说明：从模型中获取样式名称并解析为样式对象。
// 样式设置说明：返回的样式基于样式表中的默认样式或指定样式。
// 特殊处理注意事项：建议优先使用单元格状态中的缓存样式。
// 未修改代码逻辑，仅提供样式获取的解释。
mxGraph.prototype.getCellStyle = function(cell)
{
	var stylename = this.model.getStyle(cell);
	var style = null;
	
	// Gets the default style for the cell
    // 中文注释：获取单元格的默认样式
	if (this.model.isEdge(cell))
	{
		style = this.stylesheet.getDefaultEdgeStyle();
	}
	else
	{
		style = this.stylesheet.getDefaultVertexStyle();
	}
	
	// Resolves the stylename using the above as the default
    // 中文注释：使用上述默认样式解析样式名称
	if (stylename != null)
	{
		style = this.postProcessCellStyle(this.stylesheet.getCellStyle(stylename, style));
	}
	
	// Returns a non-null value if no style can be found
    // 中文注释：如果找不到样式，返回空对象
	if (style == null)
	{
		style = new Object();
	}
	
	return style;
};

/**
 * Function: postProcessCellStyle
 * 
 * Tries to resolve the value for the image style in the image bundles and
 * turns short data URIs as defined in mxImageBundle to data URIs as
 * defined in RFC 2397 of the IETF.
 */
// 中文注释：
// 函数：postProcessCellStyle
// 用途：处理单元格样式中的图像值，将图像包中的键解析为实际图像 URL，并将短数据 URI 转换为标准 RFC 2397 格式。
// 参数：
//   - style: 要处理的样式对象。
// 主要功能：
//   1. 从图像包中解析 STYLE_IMAGE 的值。
//   2. 处理短数据 URI，添加 base64 前缀或对 SVG 进行编码。
//   3. 返回处理后的样式对象。
// 说明：确保图像样式值符合标准格式。
// 特殊处理注意事项：
//   - 支持 SVG 和非 SVG 数据 URI 的不同处理方式。
//   - 针对 Firefox 和 IE11 的 SVG 数据 URI 进行特殊编码。
// 未修改代码逻辑，仅提供样式后处理的解释。
mxGraph.prototype.postProcessCellStyle = function(style)
{
	if (style != null)
	{
		var key = style[mxConstants.STYLE_IMAGE];
		var image = this.getImageFromBundles(key);

		if (image != null)
		{
			style[mxConstants.STYLE_IMAGE] = image;
		}
		else
		{
			image = key;
		}
		
		// Converts short data uris to normal data uris
        // 中文注释：将短数据 URI 转换为标准数据 URI
		if (image != null && image.substring(0, 11) == 'data:image/')
		{
			if (image.substring(0, 20) == 'data:image/svg+xml,<')
			{
				// Required for FF and IE11
                // 中文注释：为 Firefox 和 IE11 所需
				image = image.substring(0, 19) + encodeURIComponent(image.substring(19));
			}
			else if (image.substring(0, 22) != 'data:image/svg+xml,%3C')
			{
				var comma = image.indexOf(',');
				
				// Adds base64 encoding prefix if needed
                // 中文注释：如果需要，添加 base64 编码前缀
				if (comma > 0 && image.substring(comma - 7, comma + 1) != ';base64,')
				{
					image = image.substring(0, comma) + ';base64,'
						+ image.substring(comma + 1);
				}
			}
			
			style[mxConstants.STYLE_IMAGE] = image;
		}
	}

	return style;
};

/**
 * Function: setCellStyle
 * 
 * Sets the style of the specified cells. If no cells are given, then the
 * selection cells are changed.
 * 
 * Parameters:
 * 
 * style - String representing the new style of the cells.
 * cells - Optional array of <mxCells> to set the style for. Default is the
 * selection cells.
 */
// 中文注释：
// 函数：setCellStyle
// 用途：为指定单元格设置新样式，若未指定单元格，则更改选择单元格的样式。
// 参数：
//   - style: 表示新样式的字符串。
//   - cells: 可选，要设置样式的单元格数组（mxCell），默认使用选择单元格。
// 主要功能：
//   1. 如果未指定单元格，使用当前选择单元格。
//   2. 开始模型更新事务。
//   3. 为每个单元格设置新样式。
//   4. 结束模型更新事务。
// 说明：批量更新单元格样式。
// 样式设置说明：直接覆盖单元格的现有样式。
// 未修改代码逻辑，仅提供样式设置的解释。
mxGraph.prototype.setCellStyle = function(style, cells)
{
	cells = cells || this.getSelectionCells();
	
	if (cells != null)
	{
		this.model.beginUpdate();
		try
		{
			for (var i = 0; i < cells.length; i++)
			{
				this.model.setStyle(cells[i], style);
			}
		}
		finally
		{
			this.model.endUpdate();
		}
	}
};

/**
 * Function: toggleCellStyle
 * 
 * Toggles the boolean value for the given key in the style of the given cell
 * and returns the new value as 0 or 1. If no cell is specified then the
 * selection cell is used.
 * 
 * Parameter:
 * 
 * key - String representing the key for the boolean value to be toggled.
 * defaultValue - Optional boolean default value if no value is defined.
 * Default is false.
 * cell - Optional <mxCell> whose style should be modified. Default is
 * the selection cell.
 */
// 中文注释：
// 函数：toggleCellStyle
// 用途：切换指定单元格样式中给定键的布尔值，并返回新值（0 或 1）。
// 参数：
//   - key: 表示要切换的布尔值的键。
//   - defaultValue: 可选，默认布尔值，若键未定义则使用，默认 false。
//   - cell: 可选，要修改样式的单元格（mxCell），默认使用选择单元格。
// 主要功能：
//   1. 调用 toggleCellStyles 处理单个单元格的样式切换。
// 说明：简化对单个单元格样式的布尔值切换。
// 交互逻辑：支持动态切换样式属性（如圆角）。
// 未修改代码逻辑，仅提供样式切换的解释。
mxGraph.prototype.toggleCellStyle = function(key, defaultValue, cell)
{
	cell = cell || this.getSelectionCell();
	
	return this.toggleCellStyles(key, defaultValue, [cell]);
};

/**
 * Function: toggleCellStyles
 * 
 * Toggles the boolean value for the given key in the style of the given cells
 * and returns the new value as 0 or 1. If no cells are specified, then the
 * selection cells are used. For example, this can be used to toggle
 * <mxConstants.STYLE_ROUNDED> or any other style with a boolean value.
 * 
 * Parameter:
 * 
 * key - String representing the key for the boolean value to be toggled.
 * defaultValue - Optional boolean default value if no value is defined.
 * Default is false.
 * cells - Optional array of <mxCells> whose styles should be modified.
 * Default is the selection cells.
 */
// 中文注释：
// 函数：toggleCellStyles
// 用途：切换指定单元格样式中给定键的布尔值，并返回新值（0 或 1）。
// 参数：
//   - key: 表示要切换的布尔值的键。
//   - defaultValue: 可选，默认布尔值，若键未定义则使用，默认 false。
//   - cells: 可选，要修改样式的单元格数组（mxCell），默认使用选择单元格。
// 主要功能：
//   1. 如果单元格数组非空，获取第一个单元格的当前样式。
//   2. 根据当前样式值和默认值计算新值（0 或 1）。
//   3. 调用 setCellStyles 设置新值。
// 说明：批量切换单元格样式的布尔值。
// 交互逻辑：支持动态切换样式属性（如圆角、填充等）。
// 未修改代码逻辑，仅提供样式批量切换的解释。
mxGraph.prototype.toggleCellStyles = function(key, defaultValue, cells)
{
	defaultValue = (defaultValue != null) ? defaultValue : false;
	cells = cells || this.getSelectionCells();
	var value = null;
	
	if (cells != null && cells.length > 0)
	{
		var style = this.getCurrentCellStyle(cells[0]);
		value = (mxUtils.getValue(style, key, defaultValue)) ? 0 : 1;
		this.setCellStyles(key, value, cells);
	}
	
	return value;
};

/**
 * Function: setCellStyles
 * 
 * Sets the key to value in the styles of the given cells. This will modify
 * the existing cell styles in-place and override any existing assignment
 * for the given key. If no cells are specified, then the selection cells
 * are changed. If no value is specified, then the respective key is
 * removed from the styles.
 * 
 * Parameters:
 * 
 * key - String representing the key to be assigned.
 * value - String representing the new value for the key.
 * cells - Optional array of <mxCells> to change the style for. Default is
 * the selection cells.
 */
// 中文注释：
// 函数：setCellStyles
// 用途：为指定单元格的样式设置键值对，覆盖现有键值。
// 参数：
//   - key: 表示要设置的样式键。
//   - value: 表示键的新值，若为 null 则移除该键。
//   - cells: 可选，要更改样式的单元格数组（mxCell），默认使用选择单元格。
// 主要功能：
//   1. 如果未指定单元格，使用当前选择单元格。
//   2. 调用 mxUtils.setCellStyles 更新模型中的样式。
// 说明：批量修改或移除单元格样式的指定键值。
// 样式设置说明：直接修改现有样式，覆盖或移除指定键。
// 未修改代码逻辑，仅提供样式设置的解释。
mxGraph.prototype.setCellStyles = function(key, value, cells)
{
	cells = cells || this.getSelectionCells();
	mxUtils.setCellStyles(this.model, cells, key, value);
};

/**
 * Function: toggleCellStyleFlags
 * 
 * Toggles the given bit for the given key in the styles of the specified
 * cells.
 * 
 * Parameters:
 * 
 * key - String representing the key to toggle the flag in.
 * flag - Integer that represents the bit to be toggled.
 * cells - Optional array of <mxCells> to change the style for. Default is
 * the selection cells.
 */
mxGraph.prototype.toggleCellStyleFlags = function(key, flag, cells)
{
	this.setCellStyleFlags(key, flag, null, cells);
};

/**
 * Function: setCellStyleFlags
 * 
 * Sets or toggles the given bit for the given key in the styles of the
 * specified cells.
 * 
 * Parameters:
 * 
 * key - String representing the key to toggle the flag in.
 * flag - Integer that represents the bit to be toggled.
 * value - Boolean value to be used or null if the value should be toggled.
 * cells - Optional array of <mxCells> to change the style for. Default is
 * the selection cells.
 */
mxGraph.prototype.setCellStyleFlags = function(key, flag, value, cells)
{
	cells = cells || this.getSelectionCells();
	
	if (cells != null && cells.length > 0)
	{
		if (value == null)
		{
			var style = this.getCurrentCellStyle(cells[0]);
			var current = parseInt(style[key] || 0);
			value = !((current & flag) == flag);
		}

		mxUtils.setCellStyleFlags(this.model, cells, key, flag, value);
	}
};

/**
 * Group: Cell alignment and orientation
 */

/**
 * Function: alignCells
 * 
 * Aligns the given cells vertically or horizontally according to the given
 * alignment using the optional parameter as the coordinate.
 * 
 * Parameters:
 * 
 * align - Specifies the alignment. Possible values are all constants in
 * mxConstants with an ALIGN prefix.
 * cells - Array of <mxCells> to be aligned.
 * param - Optional coordinate for the alignment.
 */
mxGraph.prototype.alignCells = function(align, cells, param)
{
	if (cells == null)
	{
		cells = this.getSelectionCells();
	}
	
	if (cells != null && cells.length > 1)
	{
		// Finds the required coordinate for the alignment
		if (param == null)
		{
			for (var i = 0; i < cells.length; i++)
			{
				var state = this.view.getState(cells[i]);
				
				if (state != null && !this.model.isEdge(cells[i]))
				{
					if (param == null)
					{
						if (align == mxConstants.ALIGN_CENTER)
						{
							param = state.x + state.width / 2;
							break;
						}
						else if (align == mxConstants.ALIGN_RIGHT)
						{
							param = state.x + state.width;
						}
						else if (align == mxConstants.ALIGN_TOP)
						{
							param = state.y;
						}
						else if (align == mxConstants.ALIGN_MIDDLE)
						{
							param = state.y + state.height / 2;
							break;
						}
						else if (align == mxConstants.ALIGN_BOTTOM)
						{
							param = state.y + state.height;
						}
						else
						{
							param = state.x;
						}
					}
					else
					{
						if (align == mxConstants.ALIGN_RIGHT)
						{
							param = Math.max(param, state.x + state.width);
						}
						else if (align == mxConstants.ALIGN_TOP)
						{
							param = Math.min(param, state.y);
						}
						else if (align == mxConstants.ALIGN_BOTTOM)
						{
							param = Math.max(param, state.y + state.height);
						}
						else
						{
							param = Math.min(param, state.x);
						}
					}
				}
			}
		}

		// Aligns the cells to the coordinate
		if (param != null)
		{
			var s = this.view.scale;

			this.model.beginUpdate();
			try
			{
				for (var i = 0; i < cells.length; i++)
				{
					var state = this.view.getState(cells[i]);
					
					if (state != null)
					{
						var geo = this.getCellGeometry(cells[i]);
						
						if (geo != null && !this.model.isEdge(cells[i]))
						{
							geo = geo.clone();
							
							if (align == mxConstants.ALIGN_CENTER)
							{
								geo.x += (param - state.x - state.width / 2) / s;
							}
							else if (align == mxConstants.ALIGN_RIGHT)
							{
								geo.x += (param - state.x - state.width) / s;
							}
							else if (align == mxConstants.ALIGN_TOP)
							{
								geo.y += (param - state.y) / s;
							}
							else if (align == mxConstants.ALIGN_MIDDLE)
							{
								geo.y += (param - state.y - state.height / 2) / s;
							}
							else if (align == mxConstants.ALIGN_BOTTOM)
							{
								geo.y += (param - state.y - state.height) / s;
							}
							else
							{
								geo.x += (param - state.x) / s;
							}
							
							this.resizeCell(cells[i], geo);
						}
					}
				}
				
				this.fireEvent(new mxEventObject(mxEvent.ALIGN_CELLS,
						'align', align, 'cells', cells));
			}
			finally
			{
				this.model.endUpdate();
			}
		}
	}
	
	return cells;
};

/**
 * Function: flipEdge
 * 
 * Toggles the style of the given edge between null (or empty) and
 * <alternateEdgeStyle>. This method fires <mxEvent.FLIP_EDGE> while the
 * transaction is in progress. Returns the edge that was flipped.
 * 
 * Here is an example that overrides this implementation to invert the
 * value of <mxConstants.STYLE_ELBOW> without removing any existing styles.
 * 
 * (code)
 * graph.flipEdge = function(edge)
 * {
 *   if (edge != null)
 *   {
 *     var style = this.getCurrentCellStyle(edge);
 *     var elbow = mxUtils.getValue(style, mxConstants.STYLE_ELBOW,
 *         mxConstants.ELBOW_HORIZONTAL);
 *     var value = (elbow == mxConstants.ELBOW_HORIZONTAL) ?
 *         mxConstants.ELBOW_VERTICAL : mxConstants.ELBOW_HORIZONTAL;
 *     this.setCellStyles(mxConstants.STYLE_ELBOW, value, [edge]);
 *   }
 * };
 * (end)
 * 
 * Parameters:
 * 
 * edge - <mxCell> whose style should be changed.
 */
mxGraph.prototype.flipEdge = function(edge)
{
	if (edge != null &&
		this.alternateEdgeStyle != null)
	{
		this.model.beginUpdate();
		try
		{
			var style = this.model.getStyle(edge);

			if (style == null || style.length == 0)
			{
				this.model.setStyle(edge, this.alternateEdgeStyle);
			}
			else
			{
				this.model.setStyle(edge, null);
			}

			// Removes all existing control points
			this.resetEdge(edge);
			this.fireEvent(new mxEventObject(mxEvent.FLIP_EDGE, 'edge', edge));
		}
		finally
		{
			this.model.endUpdate();
		}
	}

	return edge;
};

/**
 * Function: addImageBundle
 *
 * Adds the specified <mxImageBundle>.
 */
mxGraph.prototype.addImageBundle = function(bundle)
{
	this.imageBundles.push(bundle);
};

/**
 * Function: removeImageBundle
 * 
 * Removes the specified <mxImageBundle>.
 */
mxGraph.prototype.removeImageBundle = function(bundle)
{
	var tmp = [];
	
	for (var i = 0; i < this.imageBundles.length; i++)
	{
		if (this.imageBundles[i] != bundle)
		{
			tmp.push(this.imageBundles[i]);
		}
	}
	
	this.imageBundles = tmp;
};

/**
 * Function: getImageFromBundles
 *
 * Searches all <imageBundles> for the specified key and returns the value
 * for the first match or null if the key is not found.
 */
mxGraph.prototype.getImageFromBundles = function(key)
{
	if (key != null)
	{
		for (var i = 0; i < this.imageBundles.length; i++)
		{
			var image = this.imageBundles[i].getImage(key);
			
			if (image != null)
			{
				return image;
			}
		}
	}
	
	return null;
};

/**
 * Group: Order
 */

/**
 * Function: orderCells
 * 
 * Moves the given cells to the front or back. The change is carried out
 * using <cellsOrdered>. This method fires <mxEvent.ORDER_CELLS> while the
 * transaction is in progress.
 * 
 * Parameters:
 * 
 * back - Boolean that specifies if the cells should be moved to back.
 * cells - Array of <mxCells> to move to the background. If null is
 * specified then the selection cells are used.
 */
// 中文注释：
// 函数：orderCells
// 用途：将指定单元格移到前景或背景。
// 参数：
//   - back: 布尔值，指定是否移到背景。
//   - cells: 要移动的单元格数组（mxCell），若为 null 则使用选择单元格。
// 主要功能：
//   1. 如果未指定单元格，使用排序后的选择单元格。
//   2. 开始模型更新事务。
//   3. 调用 cellsOrdered 执行移动操作。
//   4. 触发 ORDER_CELLS 事件。
//   5. 结束模型更新事务。
// 事件处理逻辑：触发 mxEvent.ORDER_CELLS 事件，通知单元格顺序变化。
// 交互逻辑：支持用户调整单元格的显示顺序。
// 未修改代码逻辑，仅提供单元格排序的解释。
mxGraph.prototype.orderCells = function(back, cells)
{
	if (cells == null)
	{
		cells = mxUtils.sortCells(this.getSelectionCells(), true);
	}

	this.model.beginUpdate();
	try
	{
		this.cellsOrdered(cells, back);
		this.fireEvent(new mxEventObject(mxEvent.ORDER_CELLS,
				'back', back, 'cells', cells));
	}
	finally
	{
		this.model.endUpdate();
	}

	return cells;
};

/**
 * Function: cellsOrdered
 * 
 * Moves the given cells to the front or back. This method fires
 * <mxEvent.CELLS_ORDERED> while the transaction is in progress.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> whose order should be changed.
 * back - Boolean that specifies if the cells should be moved to back.
 */
mxGraph.prototype.cellsOrdered = function(cells, back)
{
    // 中文注释：检查输入的单元数组是否有效
	if (cells != null)
	{
        // 中文注释：开始模型更新事务，确保操作原子性
		this.model.beginUpdate();
		try
		{
            // 中文注释：遍历单元数组，调整每个单元的层级顺序
			for (var i = 0; i < cells.length; i++)
			{
                // 中文注释：获取当前单元的父节点
				var parent = this.model.getParent(cells[i]);

                // 中文注释：根据 back 参数决定是将单元移到最底层还是最顶层
				if (back)
				{
                    // 中文注释：将单元添加到父节点的索引 i 处（最底层）
					this.model.add(parent, cells[i], i);
				}
				else
				{
                    // 中文注释：将单元添加到父节点的子节点末尾（最顶层）
					this.model.add(parent, cells[i],
							this.model.getChildCount(parent) - 1);
				}
			}
			
            // 中文注释：触发 CELLS_ORDERED 事件，通知层级调整完成，包含 back 和 cells 参数
			this.fireEvent(new mxEventObject(mxEvent.CELLS_ORDERED,
					'back', back, 'cells', cells));
		}
		finally
		{
            // 中文注释：结束模型更新事务，确保所有操作提交
			this.model.endUpdate();
		}
	}
};

/**
 * Group: Grouping
 */

/**
 * Function: groupCells
 * 
 * Adds the cells into the given group. The change is carried out using
 * <cellsAdded>, <cellsMoved> and <cellsResized>. This method fires
 * <mxEvent.GROUP_CELLS> while the transaction is in progress. Returns the
 * new group. A group is only created if there is at least one entry in the
 * given array of cells.
 * 
 * Parameters:
 * 
 * group - <mxCell> that represents the target group. If null is specified
 * then a new group is created using <createGroupCell>.
 * border - Optional integer that specifies the border between the child
 * area and the group bounds. Default is 0.
 * cells - Optional array of <mxCells> to be grouped. If null is specified
 * then the selection cells are used.
 */
mxGraph.prototype.groupCells = function(group, border, cells)
{
    // 中文注释：如果未提供单元数组，使用当前选中的单元并排序
	if (cells == null)
	{
        // 中文注释：调用 mxUtils.sortCells 对选中单元进行排序，true 表示升序
		cells = mxUtils.sortCells(this.getSelectionCells(), true);
	}

    // 中文注释：筛选适合分组的单元，确保与第一个单元有相同父节点
	cells = this.getCellsForGroup(cells);

    // 中文注释：如果未提供 group 参数，创建新的分组单元
	if (group == null)
	{
        // 中文注释：调用 createGroupCell 创建一个新的分组单元
		group = this.createGroupCell(cells);
	}

    // 中文注释：计算分组的边界，考虑子节点和边距
	var bounds = this.getBoundsForGroup(group, cells, border);

    // 中文注释：确保至少有两个单元且边界有效才进行分组操作
	if (cells.length > 1 && bounds != null)
	{
		// Uses parent of group or previous parent of first child
		// 中文注释：获取分组的父节点，若无则使用第一个单元的父节点
		var parent = this.model.getParent(group);
		
		if (parent == null)
		{
			parent = this.model.getParent(cells[0]);
		}

        // 中文注释：开始模型更新事务，确保分组操作的原子性
		this.model.beginUpdate();
		try
		{
            // 中文注释：检查分组单元是否具有几何信息，若无则初始化
			if (this.getCellGeometry(group) == null)
			{
                // 中文注释：为分组单元设置新的几何对象
				this.model.setGeometry(group, new mxGeometry());
			}

            // 中文注释：将分组单元添加到父节点
			var index = this.model.getChildCount(parent);
            // 中文注释：调用 cellsAdded 添加分组单元到父节点，不调整相对位置
			this.cellsAdded([group], parent, index, null, null, false, false, false);

            // 中文注释：将子单元添加到分组单元并移动位置
			index = this.model.getChildCount(group);
            // 中文注释：调用 cellsAdded 将子单元添加到分组单元
			this.cellsAdded(cells, group, index, null, null, false, false, false);
            // 中文注释：移动子单元以对齐分组边界
			this.cellsMoved(cells, -bounds.x, -bounds.y, false, false, false);

            // 中文注释：调整分组单元的大小以适应子单元
			this.cellsResized([group], [bounds], false);

            // 中文注释：触发 GROUP_CELLS 事件，通知分组完成，包含 group、border 和 cells 参数
			this.fireEvent(new mxEventObject(mxEvent.GROUP_CELLS,
					'group', group, 'border', border, 'cells', cells));
		}
		finally
		{
            // 中文注释：结束模型更新事务，提交所有操作
			this.model.endUpdate();
		}
	}

    // 中文注释：返回创建或使用的分组单元
	return group;
};

/**
 * Function: getCellsForGroup
 * 
 * Returns the cells with the same parent as the first cell
 * in the given array.
 */
mxGraph.prototype.getCellsForGroup = function(cells)
{
    // 中文注释：初始化结果数组，用于存储具有相同父节点的单元
	var result = [];

    // 中文注释：检查输入单元数组是否有效且不为空
	if (cells != null && cells.length > 0)
	{
        // 中文注释：获取第一个单元的父节点
		var parent = this.model.getParent(cells[0]);
        // 中文注释：将第一个单元添加到结果数组
		result.push(cells[0]);

        // 中文注释：遍历剩余单元，筛选出与第一个单元具有相同父节点的单元
		for (var i = 1; i < cells.length; i++)
		{
			if (this.model.getParent(cells[i]) == parent)
			{
				result.push(cells[i]);
			}
		}
	}

    // 中文注释：返回筛选后的单元数组
	return result;
};

/**
 * Function: getBoundsForGroup
 * 
 * Returns the bounds to be used for the given group and children.
 */
mxGraph.prototype.getBoundsForGroup = function(group, children, border)
{
    // 中文注释：计算子单元的几何边界，true 表示包含所有子节点
	var result = this.getBoundingBoxFromGeometry(children, true);
	
    // 中文注释：如果边界有效，进一步调整边界大小
	if (result != null)
	{
        // 中文注释：检查是否为泳道（swimlane），若为泳道则考虑起始区域
		if (this.isSwimlane(group))
		{
            // 中文注释：获取泳道的起始区域大小
			var size = this.getStartSize(group);
			
            // 中文注释：调整边界以包含泳道的标题区域
			result.x -= size.width;
			result.y -= size.height;
			result.width += size.width;
			result.height += size.height;
		}
		
        // 中文注释：如果提供了边距参数，扩展边界以包含边距
		if (border != null)
		{
			result.x -= border;
			result.y -= border;
			result.width += 2 * border;
			result.height += 2 * border;
		}
	}			
	
    // 中文注释：返回调整后的边界对象
	return result;
};

/**
 * Function: createGroupCell
 * 
 * Hook for creating the group cell to hold the given array of <mxCells> if
 * no group cell was given to the <group> function.
 * 
 * The following code can be used to set the style of new group cells.
 * 
 * (code)
 * var graphCreateGroupCell = graph.createGroupCell;
 * graph.createGroupCell = function(cells)
 * {
 *   var group = graphCreateGroupCell.apply(this, arguments);
 *   group.setStyle('group');
 *   
 *   return group;
 * };
 */
mxGraph.prototype.createGroupCell = function(cells)
{
    // 中文注释：创建新的分组单元，初始化为空单元
	var group = new mxCell('');
    // 中文注释：设置单元为顶点（vertex）类型
	group.setVertex(true);
    // 中文注释：禁止分组单元的可连接性
	group.setConnectable(false);
	
    // 中文注释：返回创建的分组单元
	return group;
};

/**
 * Function: ungroupCells
 * 
 * Ungroups the given cells by moving the children the children to their
 * parents parent and removing the empty groups. Returns the children that
 * have been removed from the groups.
 * 
 * Parameters:
 * 
 * cells - Array of cells to be ungrouped. If null is specified then the
 * selection cells are used.
 */
mxGraph.prototype.ungroupCells = function(cells)
{
    // 中文注释：初始化结果数组，存储从分组中移除的子单元
	var result = [];
	
    // 中文注释：如果未提供单元数组，使用可解组的选中单元
	if (cells == null)
	{
        // 中文注释：调用 getCellsForUngroup 获取可解组的单元
		cells = this.getCellsForUngroup();
	}
	
    // 中文注释：检查单元数组是否有效且不为空
	if (cells != null && cells.length > 0)
	{
        // 中文注释：开始模型更新事务，确保解组操作的原子性
		this.model.beginUpdate();
		try
		{
            // 中文注释：遍历每个分组单元
			for (var i = 0; i < cells.length; i++)
			{
                // 中文注释：获取分组单元的子节点
				var children = this.model.getChildren(cells[i]);
				
                // 中文注释：如果存在子节点，执行解组操作
				if (children != null && children.length > 0)
				{
                    // 中文注释：复制子节点数组以避免修改原始数据
					children = children.slice();
                    // 中文注释：获取分组单元的父节点
					var parent = this.model.getParent(cells[i]);
                    // 中文注释：获取父节点的子节点数量，用于确定插入位置
					var index = this.model.getChildCount(parent);

                    // 中文注释：将子节点添加到父节点，保持相对位置
					this.cellsAdded(children, parent, index, null, null, true);
                    // 中文注释：将子节点添加到结果数组
					result = result.concat(children);
					
                    // 中文注释：处理相对坐标的子节点，转换为绝对坐标
					for (var j = 0; j < children.length; j++)
					{
                        // 中文注释：获取子节点的状态和几何信息
						var state = this.view.getState(children[j]);
						var geo = this.getCellGeometry(children[j]);
						
                        // 中文注释：如果子节点具有相对坐标，转换为绝对坐标
						if (state != null && geo != null && geo.relative)
						{
							geo = geo.clone();
							geo.x = state.origin.x;
							geo.y = state.origin.y;
							geo.relative = false;
							
                            // 中文注释：更新子节点的几何信息
							this.model.setGeometry(children[j], geo);
						}
					}
				}
			}

            // 中文注释：移除空分组单元
			this.removeCellsAfterUngroup(cells);
            // 中文注释：触发 UNGROUP_CELLS 事件，通知解组完成
			this.fireEvent(new mxEventObject(mxEvent.UNGROUP_CELLS, 'cells', cells));
		}
		finally
		{
            // 中文注释：结束模型更新事务，提交所有操作
			this.model.endUpdate();
		}
	}
	
    // 中文注释：返回从分组中移除的子单元
	return result;
};

/**
 * Function: getCellsForUngroup
 * 
 * Returns the selection cells that can be ungrouped.
 */
mxGraph.prototype.getCellsForUngroup = function()
{
    // 中文注释：获取当前选中的单元
	var cells = this.getSelectionCells();

    // 中文注释：初始化临时数组，存储可解组的单元
	var tmp = [];
	
    // 中文注释：筛选具有子节点的顶点单元
	for (var i = 0; i < cells.length; i++)
	{
		if (this.model.isVertex(cells[i]) &&
			this.model.getChildCount(cells[i]) > 0)
		{
			tmp.push(cells[i]);
		}
	}

    // 中文注释：返回可解组的单元数组
	return tmp;
};

/**
 * Function: removeCellsAfterUngroup
 * 
 * Hook to remove the groups after <ungroupCells>.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> that were ungrouped.
 */
mxGraph.prototype.removeCellsAfterUngroup = function(cells)
{
    // 中文注释：移除解组后的空分组单元及其关联边
	this.cellsRemoved(this.addAllEdges(cells));
};

/**
 * Function: removeCellsFromParent
 * 
 * Removes the specified cells from their parents and adds them to the
 * default parent. Returns the cells that were removed from their parents.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to be removed from their parents.
 */
mxGraph.prototype.removeCellsFromParent = function(cells)
{
    // 中文注释：如果未提供单元数组，使用当前选中的单元
	if (cells == null)
	{
		cells = this.getSelectionCells();
	}
	
    // 中文注释：开始模型更新事务，确保操作原子性
	this.model.beginUpdate();
	try
	{
        // 中文注释：获取默认父节点
		var parent = this.getDefaultParent();
        // 中文注释：获取默认父节点的子节点数量，用于确定插入位置
		var index = this.model.getChildCount(parent);

        // 中文注释：将单元添加到默认父节点，保持相对位置
		this.cellsAdded(cells, parent, index, null, null, true);
        // 中文注释：触发 REMOVE_CELLS_FROM_PARENT 事件，通知操作完成
		this.fireEvent(new mxEventObject(mxEvent.REMOVE_CELLS_FROM_PARENT, 'cells', cells));
	}
	finally
	{
        // 中文注释：结束模型更新事务，提交所有操作
		this.model.endUpdate();
	}

    // 中文注释：返回从原父节点移除的单元
	return cells;
};

/**
 * Function: updateGroupBounds
 * 
 * Updates the bounds of the given groups to include all children and returns
 * the passed-in cells. Call this with the groups in parent to child order,
 * top-most group first, the cells are processed in reverse order and cells
 * with no children are ignored.
 * 
 * Parameters:
 * 
 * cells - The groups whose bounds should be updated. If this is null, then
 * the selection cells are used.
 * border - Optional border to be added in the group. Default is 0.
 * moveGroup - Optional boolean that allows the group to be moved. Default
 * is false.
 * topBorder - Optional top border to be added in the group. Default is 0.
 * rightBorder - Optional top border to be added in the group. Default is 0.
 * bottomBorder - Optional top border to be added in the group. Default is 0.
 * leftBorder - Optional top border to be added in the group. Default is 0.
 */
mxGraph.prototype.updateGroupBounds = function(cells, border, moveGroup, topBorder, rightBorder, bottomBorder, leftBorder)
{
    // 中文注释：如果未提供单元数组，使用当前选中的单元
	if (cells == null)
	{
		cells = this.getSelectionCells();
	}
	
    // 中文注释：设置默认值，处理可选参数
	border = (border != null) ? border : 0;
	moveGroup = (moveGroup != null) ? moveGroup : false;
	topBorder = (topBorder != null) ? topBorder : 0;
	rightBorder = (rightBorder != null) ? rightBorder : 0;
	bottomBorder = (bottomBorder != null) ? bottomBorder : 0;
	leftBorder = (leftBorder != null) ? leftBorder : 0;

    // 中文注释：开始模型更新事务，确保操作原子性
	this.model.beginUpdate();
	try
	{
        // 中文注释：倒序遍历分组单元，从子到父处理
		for (var i = cells.length - 1; i >= 0; i--)
		{
            // 中文注释：获取当前分组单元的几何信息
			var geo = this.getCellGeometry(cells[i]);
			
            // 中文注释：如果几何信息存在，进一步处理
			if (geo != null)
			{
                // 中文注释：获取分组单元的子节点
				var children = this.getChildCells(cells[i]);
				
                // 中文注释：如果存在子节点，计算边界
				if (children != null && children.length > 0)
				{
                    // 中文注释：计算子节点的几何边界，true 表示包含所有子节点
					var bounds = this.getBoundingBoxFromGeometry(children, true);
					
                    // 中文注释：如果边界有效，调整分组单元的几何信息
					if (bounds != null && bounds.width > 0 && bounds.height > 0)
					{
						// Adds the size of the title area for swimlanes
						// 中文注释：如果为泳道，考虑标题区域大小
						var size = (this.isSwimlane(cells[i])) ?
							this.getActualStartSize(cells[i], true) : new mxRectangle();
						geo = geo.clone();
						
                        // 中文注释：如果允许移动分组，调整分组位置
						if (moveGroup)
						{
							geo.x = Math.round(geo.x + bounds.x - border - size.x - leftBorder);
							geo.y = Math.round(geo.y + bounds.y - border - size.y - topBorder);
						}
						
                        // 中文注释：调整分组的宽度和高度，包含边距和泳道标题
						geo.width = Math.round(bounds.width + 2 * border + size.x + leftBorder + rightBorder + size.width);
						geo.height = Math.round(bounds.height + 2 * border + size.y + topBorder + bottomBorder + size.height);
						
                        // 中文注释：更新分组单元的几何信息
						this.model.setGeometry(cells[i], geo);
                        // 中文注释：移动子节点以对齐分组边界
						this.moveCells(children, border + size.x - bounds.x + leftBorder,
								border + size.y - bounds.y + topBorder);
					}
				}
			}
		}
	}
	finally
	{
        // 中文注释：结束模型更新事务，提交所有操作
		this.model.endUpdate();
	}

    // 中文注释：返回处理后的分组单元
	return cells;
};

/**
 * Function: getBoundingBox
 * 
 * Returns the bounding box for the given array of <mxCells>. The bounding box for
 * each cell and its descendants is computed using <mxGraphView.getBoundingBox>.
 *
 * Parameters:
 *
 * cells - Array of <mxCells> whose bounding box should be returned.
 */
mxGraph.prototype.getBoundingBox = function(cells)
{
    // 中文注释：初始化边界结果
	var result = null;
	
    // 中文注释：检查输入单元数组是否有效且不为空
	if (cells != null && cells.length > 0)
	{
        // 中文注释：遍历单元数组，计算每个单元及其子节点的边界
		for (var i = 0; i < cells.length; i++)
		{
            // 中文注释：仅处理顶点或边类型的单元
			if (this.model.isVertex(cells[i]) || this.model.isEdge(cells[i]))
			{
                // 中文注释：获取单元的状态边界，true 表示包含子节点
				var bbox = this.view.getBoundingBox(this.view.getState(cells[i]), true);
			
                // 中文注释：如果边界有效，合并到结果中
				if (bbox != null)
				{
					if (result == null)
					{
                        // 中文注释：初始化结果边界
						result = mxRectangle.fromRectangle(bbox);
					}
					else
					{
                        // 中文注释：合并当前边界到结果边界
						result.add(bbox);
					}
				}
			}
		}
	}
	
    // 中文注释：返回计算得到的边界对象
	return result;
};

/**
 * Group: Cell cloning, insertion and removal
 */
// 分组：单元格克隆、插入和移除

/**
 * Function: cloneCell
 * 
 * Returns the clone for the given cell. Uses <cloneCells>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to be cloned.
 * allowInvalidEdges - Optional boolean that specifies if invalid edges
 * should be cloned. Default is true.
 * mapping - Optional mapping for existing clones.
 * keepPosition - Optional boolean indicating if the position of the cells should
 * be updated to reflect the lost parent cell. Default is false.
 */
// 函数：cloneCell
// 返回指定单元格的克隆，使用 <cloneCells> 方法。
// 参数：
// cell - 要克隆的 <mxCell> 单元格。
// allowInvalidEdges - 可选布尔值，指定是否克隆无效边，默认值为 true。
// mapping - 可选的现有克隆映射。
// keepPosition - 可选布尔值，指示是否更新单元格位置以反映丢失的父单元格，默认值为 false。
// 功能说明：克隆单个单元格，调用 cloneCells 方法并返回第一个克隆结果。
// 注意事项：依赖 cloneCells 方法处理克隆逻辑，确保参数传递一致。
mxGraph.prototype.cloneCell = function(cell, allowInvalidEdges, mapping, keepPosition)
{
	return this.cloneCells([cell], allowInvalidEdges, mapping, keepPosition)[0];
};

/**
 * Function: cloneCells
 * 
 * Returns the clones for the given cells. The clones are created recursively
 * using <mxGraphModel.cloneCells>. If the terminal of an edge is not in the
 * given array, then the respective end is assigned a terminal point and the
 * terminal is removed.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to be cloned.
 * allowInvalidEdges - Optional boolean that specifies if invalid edges
 * should be cloned. Default is true.
 * mapping - Optional mapping for existing clones.
 * keepPosition - Optional boolean indicating if the position of the cells should
 * be updated to reflect the lost parent cell. Default is false.
 */
// 函数：cloneCells
// 返回指定单元格数组的克隆，通过 <mxGraphModel.cloneCells> 递归创建克隆。
// 如果边的端点不在给定数组中，则为该端点分配一个终端点并移除原端点。
// 参数：
// cells - 要克隆的 <mxCells> 数组。
// allowInvalidEdges - 可选布尔值，指定是否克隆无效边，默认值为 true。
// mapping - 可选的现有克隆映射。
// keepPosition - 可选布尔值，指示是否更新单元格位置以反映丢失的父单元格，默认值为 false。
// 功能说明：递归克隆单元格数组，处理边的端点，确保克隆结果有效。
// 关键步骤：
// 1. 创建字典以加速查找。
// 2. 调用模型的 cloneCells 方法生成克隆。
// 3. 根据 allowInvalidEdges 检查边有效性，无效边设为 null。
// 4. 调整克隆单元格的几何位置，考虑缩放和平移。
// 5. 处理边的控制点和端点坐标，确保位置正确。
// 事件处理：无直接事件触发，依赖模型更新。
// 注意事项：如果边的端点不在克隆数组中，会自动设置终端点；keepPosition 控制位置更新逻辑。
mxGraph.prototype.cloneCells = function(cells, allowInvalidEdges, mapping, keepPosition)
{
	allowInvalidEdges = (allowInvalidEdges != null) ? allowInvalidEdges : true;
	var clones = null;
	
	if (cells != null)
	{
		// Creates a dictionary for fast lookups
        // 创建字典以加速查找
		var dict = new mxDictionary();
		var tmp = [];
		
		for (var i = 0; i < cells.length; i++)
		{
			dict.put(cells[i], true);
			tmp.push(cells[i]);
		}
		
		if (tmp.length > 0)
		{
			var scale = this.view.scale;
			var trans = this.view.translate;
			clones = this.model.cloneCells(cells, true, mapping);
		
			for (var i = 0; i < cells.length; i++)
			{
				if (!allowInvalidEdges && this.model.isEdge(clones[i]) &&
					this.getEdgeValidationError(clones[i],
						this.model.getTerminal(clones[i], true),
						this.model.getTerminal(clones[i], false)) != null)
				{
					clones[i] = null;
                    // 如果不允许克隆无效边且边无效，则将克隆结果设为 null
				}
				else
				{
					var g = this.model.getGeometry(clones[i]);
					
					if (g != null)
					{
						var state = this.view.getState(cells[i]);
						var pstate = this.view.getState(this.model.getParent(cells[i]));
						
						if (state != null && pstate != null)
						{
							var dx = (keepPosition) ? 0 : pstate.origin.x;
							var dy = (keepPosition) ? 0 : pstate.origin.y;
                            // 根据 keepPosition 参数决定是否调整位置偏移

							if (this.model.isEdge(clones[i]))
							{
								var pts = state.absolutePoints;
								
								if (pts != null)
								{
									// Checks if the source is cloned or sets the terminal point
                                    // 检查源端点是否被克隆或设置终端点
									var src = this.model.getTerminal(cells[i], true);
									
									while (src != null && !dict.get(src))
									{
										src = this.model.getParent(src);
									}
									
									if (src == null && pts[0] != null)
									{
										g.setTerminalPoint(
											new mxPoint(pts[0].x / scale - trans.x,
												pts[0].y / scale - trans.y), true);
                                        // 为未克隆的源端点设置终端点坐标
									}
									
									// Checks if the target is cloned or sets the terminal point
                                    // 检查目标端点是否被克隆或设置终端点
									var trg = this.model.getTerminal(cells[i], false);
									
									while (trg != null && !dict.get(trg))
									{
										trg = this.model.getParent(trg);
									}

									var n = pts.length - 1;
									
									if (trg == null && pts[n] != null)
									{
										g.setTerminalPoint(
											new mxPoint(pts[n].x / scale - trans.x,
												pts[n].y / scale - trans.y), false);
                                        // 为未克隆的目标端点设置终端点坐标
									}
									
									// Translates the control points
                                    // 平移控制点
									var points = g.points;
									
									if (points != null)
									{
										for (var j = 0; j < points.length; j++)
										{
											points[j].x += dx;
											points[j].y += dy;
										}
									}
								}
							}
							else
							{
								g.translate(dx, dy);
                                // 对非边单元格进行位置平移
							}
						}
					}
				}
			}
		}
		else
		{
			clones = [];
            // 如果输入为空，返回空数组
		}
	}
	
	return clones;
};

/**
 * Function: insertVertex
 * 
 * Adds a new vertex into the given parent <mxCell> using value as the user
 * object and the given coordinates as the <mxGeometry> of the new vertex.
 * The id and style are used for the respective properties of the new
 * <mxCell>, which is returned.
 *
 * When adding new vertices from a mouse event, one should take into
 * account the offset of the graph container and the scale and translation
 * of the view in order to find the correct unscaled, untranslated
 * coordinates using <mxGraph.getPointForEvent> as follows:
 * 
 * (code)
 * var pt = graph.getPointForEvent(evt);
 * var parent = graph.getDefaultParent();
 * graph.insertVertex(parent, null,
 * 			'Hello, World!', x, y, 220, 30);
 * (end)
 * 
 * For adding image cells, the style parameter can be assigned as
 * 
 * (code)
 * stylename;image=imageUrl
 * (end)
 * 
 * See <mxGraph> for more information on using images.
 *
 * Parameters:
 * 
 * parent - <mxCell> that specifies the parent of the new vertex.
 * id - Optional string that defines the Id of the new vertex.
 * value - Object to be used as the user object.
 * x - Integer that defines the x coordinate of the vertex.
 * y - Integer that defines the y coordinate of the vertex.
 * width - Integer that defines the width of the vertex.
 * height - Integer that defines the height of the vertex.
 * style - Optional string that defines the cell style.
 * relative - Optional boolean that specifies if the geometry is relative.
 * Default is false.
 */
// 函数：insertVertex
// 在指定父 <mxCell> 中添加新顶点，使用给定的值作为用户对象，坐标作为新顶点的 <mxGeometry>。
// id 和 style 用于新 <mxCell> 的相应属性，返回新创建的单元格。
// 注意事项：通过鼠标事件添加顶点时，需考虑图容器偏移以及视图的缩放和平移，使用 <mxGraph.getPointForEvent> 获取正确坐标。
// 样式设置：可通过 style 参数指定图像单元格样式，如 `stylename;image=imageUrl`。
// 参数：
// parent - 指定新顶点父节点的 <mxCell>。
// id - 可选字符串，定义在新顶点的 ID。
// value - 用作用户对象的对象。
// x - 定义顶点 x 坐标的整数。
// y - 定义顶点 y 坐标的整数。
// width - 定义顶点宽度的整数。
// height - 定义顶点高度的整数。
// style - 可选字符串，定义单元格样式。
// relative - 可选布尔值，指定几何是否为相对坐标，默认值为 false。
// 功能说明：创建并插入新顶点到指定父节点，返回新顶点。
// 关键步骤：
// 1. 调用 createVertex 创建顶点。
// 2. 使用 addCell 将顶点添加到父节点。
// 事件处理：通过 addCell 触发 mxEvent.ADD_CELLS 事件。
// 交互逻辑：支持通过鼠标事件动态添加顶点，需正确转换坐标。
mxGraph.prototype.insertVertex = function(parent, id, value,
	x, y, width, height, style, relative)
{
	var vertex = this.createVertex(parent, id, value, x, y, width, height, style, relative);
    // 创建新顶点

	return this.addCell(vertex, parent);
    // 将顶点添加到父节点并返回
};

/**
 * Function: createVertex
 * 
 * Hook method that creates the new vertex for <insertVertex>.
 */
 // 函数：createVertex
// 为 <insertVertex> 创建新顶点的钩子方法。
// 功能说明：生成具有指定属性的新顶点单元格。
// 关键步骤：
// 1. 创建顶点的几何对象。
// 2. 设置顶点的属性（ID、用户对象、样式、可连接性等）。
// 参数：同 insertVertex。
// 注意事项：仅创建顶点，不负责插入到图中，插入由 insertVertex 的 addCell 完成。
mxGraph.prototype.createVertex = function(parent, id, value,
		x, y, width, height, style, relative)
{
	// Creates the geometry for the vertex
    // 为顶点创建几何对象
	var geometry = new mxGeometry(x, y, width, height);
	geometry.relative = (relative != null) ? relative : false;
    // 设置几何是否为相对坐标

	// Creates the vertex
    // 创建顶点
	var vertex = new mxCell(value, geometry, style);
	vertex.setId(id);
	vertex.setVertex(true);
	vertex.setConnectable(true);
    // 设置顶点属性：ID、顶点标志、可连接性

	return vertex;
};
	
/**
 * Function: insertEdge
 * 
 * Adds a new edge into the given parent <mxCell> using value as the user
 * object and the given source and target as the terminals of the new edge.
 * The id and style are used for the respective properties of the new
 * <mxCell>, which is returned.
 *
 * Parameters:
 * 
 * parent - <mxCell> that specifies the parent of the new edge.
 * id - Optional string that defines the Id of the new edge.
 * value - JavaScript object to be used as the user object.
 * source - <mxCell> that defines the source of the edge.
 * target - <mxCell> that defines the target of the edge.
 * style - Optional string that defines the cell style.
 */
// 函数：insertEdge
// 在指定父 <mxCell> 中添加新边，使用给定值作为用户对象，指定源和目标作为新边的端点。
// id 和 style 用于新 <mxCell> 的相应属性，返回新创建的边。
// 参数：
// parent - 指定新边父节点的 <mxCell>。
// id - 可选字符串，定义新边的 ID。
// value - 用作用户对象的 JavaScript 对象。
// source - 定义边源节点的 <mxCell>。
// target - 定义边目标节点的 <mxCell>。
// style - 可选字符串，定义单元格样式。
// 功能说明：创建并插入新边到指定父节点，返回新边。
// 关键步骤：
// 1. 调用 createEdge 创建边。
// 2. 使用 addEdge 将边添加到父节点并连接源和目标。
// 事件处理：通过 addEdge 触发 mxEvent.ADD_CELLS 事件。
// 交互逻辑：支持动态添加边，通常用于连接两个顶点。
mxGraph.prototype.insertEdge = function(parent, id, value, source, target, style)
{
	var edge = this.createEdge(parent, id, value, source, target, style);
    // 创建新边

	return this.addEdge(edge, parent, source, target);
    // 将边添加到父节点并连接源和目标，返回边
};

/**
 * Function: createEdge
 * 
 * Hook method that creates the new edge for <insertEdge>. This
 * implementation does not set the source and target of the edge, these
 * are set when the edge is added to the model.
 * 
 */
// 函数：createEdge
// 为 <insertEdge> 创建新边的钩子方法。
// 功能说明：生成具有指定属性的新边单元格，源和目标端点在添加时设置。
// 关键步骤：
// 1. 创建边的几何对象，设置为相对坐标。
// 2. 设置边属性（ID、用户对象、样式、边标志）。
// 参数：同 insertEdge。
// 注意事项：不设置源和目标端点，由 addEdge 负责连接。
mxGraph.prototype.createEdge = function(parent, id, value, source, target, style)
{
	// Creates the edge
    // 创建边
	var edge = new mxCell(value, new mxGeometry(), style);
	edge.setId(id);
	edge.setEdge(true);
	edge.geometry.relative = true;
    // 设置边属性：ID、边标志、相对几何

	return edge;
};

/**
 * Function: addEdge
 * 
 * Adds the edge to the parent and connects it to the given source and
 * target terminals. This is a shortcut method. Returns the edge that was
 * added.
 * 
 * Parameters:
 * 
 * edge - <mxCell> to be inserted into the given parent.
 * parent - <mxCell> that represents the new parent. If no parent is
 * given then the default parent is used.
 * source - Optional <mxCell> that represents the source terminal.
 * target - Optional <mxCell> that represents the target terminal.
 * index - Optional index to insert the cells at. Default is to append.
 */
// 函数：addEdge
// 将边添加到指定父节点，并连接到给定的源和目标端点。快捷方法，返回添加的边。
// 参数：
// edge - 要插入到父节点的 <mxCell> 边。
// parent - 表示新父节点的 <mxCell>，若未提供则使用默认父节点。
// source - 可选的表示源端点的 <mxCell>。
// target - 可选的表示目标端点的 <mxCell>。
// index - 可选的插入位置索引，默认追加到末尾。
// 功能说明：将边插入图中并设置其源和目标端点。
// 关键步骤：调用 addCell 方法完成插入和连接。
// 事件处理：通过 addCell 触发 mxEvent.ADD_CELLS 事件。
// 交互逻辑：支持动态连接两个顶点，简化边添加流程。
mxGraph.prototype.addEdge = function(edge, parent, source, target, index)
{
	return this.addCell(edge, parent, index, source, target);
};

/**
 * Function: addCell
 * 
 * Adds the cell to the parent and connects it to the given source and
 * target terminals. This is a shortcut method. Returns the cell that was
 * added.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to be inserted into the given parent.
 * parent - <mxCell> that represents the new parent. If no parent is
 * given then the default parent is used.
 * index - Optional index to insert the cells at. Default is to append.
 * source - Optional <mxCell> that represents the source terminal.
 * target - Optional <mxCell> that represents the target terminal.
 */
// 函数：addCell
// 将单元格添加到父节点并连接到给定的源和目标端点。快捷方法，返回添加的单元格。
// 参数：
// cell - 要插入到父节点的 <mxCell> 单元格。
// parent - 表示新父节点的 <mxCell>，若未提供则使用默认父节点。
// index - 可选的插入位置索引，默认追加到末尾。
// source - 可选的表示源端点的 <mxCell>。
// target - 可选的表示目标端点的 <mxCell>。
// 功能说明：将单个单元格插入图中并设置连接。
// 关键步骤：调用 addCells 方法处理插入逻辑，返回第一个添加的单元格。
// 事件处理：通过 addCells 触发 mxEvent.ADD_CELLS 事件。
mxGraph.prototype.addCell = function(cell, parent, index, source, target)
{
	return this.addCells([cell], parent, index, source, target)[0];
};

/**
 * Function: addCells
 * 
 * Adds the cells to the parent at the given index, connecting each cell to
 * the optional source and target terminal. The change is carried out using
 * <cellsAdded>. This method fires <mxEvent.ADD_CELLS> while the
 * transaction is in progress. Returns the cells that were added.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to be inserted.
 * parent - <mxCell> that represents the new parent. If no parent is
 * given then the default parent is used.
 * index - Optional index to insert the cells at. Default is to append.
 * source - Optional source <mxCell> for all inserted cells.
 * target - Optional target <mxCell> for all inserted cells.
 * absolute - Optional boolean indicating of cells should be kept at
 * their absolute position. Default is false.
 */
// 函数：addCells
// 在指定索引处将单元格数组添加到父节点，连接到可选的源和目标端点，使用 <cellsAdded> 执行更改。
// 在事务进行期间触发 <mxEvent.ADD_CELLS> 事件，返回添加的单元格。
// 参数：
// cells - 要插入的 <mxCells> 数组。
// parent - 表示新父节点的 <mxCell>，若未提供则使用默认父节点。
// index - 可选的插入位置索引，默认追加到末尾。
// source - 可选的为所有插入单元格设置的源端点 <mxCell>。
// target - 可选的为所有插入单元格设置的目标端点 <mxCell>。
// absolute - 可选布尔值，指示单元格是否保持绝对位置，默认值为 false。
// 功能说明：批量插入单元格并设置连接，支持绝对或相对位置。
// 关键步骤：
// 1. 设置默认父节点和索引。
// 2. 调用 cellsAdded 执行插入。
// 3. 触发 mxEvent.ADD_CELLS 事件。
// 事件处理：触发 mxEvent.ADD_CELLS 事件，通知单元格添加。
// 交互逻辑：支持批量添加顶点或边，自动处理父节点和端点连接。
// 注意事项：absolute 参数控制位置是否固定，需谨慎设置以避免位置冲突。
mxGraph.prototype.addCells = function(cells, parent, index, source, target, absolute)
{
	if (parent == null)
	{
		parent = this.getDefaultParent();
        // 设置默认父节点
	}
	
	if (index == null)
	{
		index = this.model.getChildCount(parent);
        // 设置默认插入索引为父节点子节点数量
	}
	
	this.model.beginUpdate();
	try
	{
		this.cellsAdded(cells, parent, index, source, target, (absolute != null) ? absolute : false, true);
		this.fireEvent(new mxEventObject(mxEvent.ADD_CELLS, 'cells', cells,
				'parent', parent, 'index', index, 'source', source, 'target', target));
        // 调用 cellsAdded 插入单元格并触发添加事件
	}
	finally
	{
		this.model.endUpdate();
        // 结束模型更新
	}

	return cells;
};

/**
 * Function: cellsAdded
 * 
 * Adds the specified cells to the given parent. This method fires
 * <mxEvent.CELLS_ADDED> while the transaction is in progress.
 */
// 函数：cellsAdded
// 将指定单元格添加到给定父节点，在事务进行期间触发 <mxEvent.CELLS_ADDED> 事件。
// 参数：
// cells - 要插入的 <mxCells> 数组。
// parent - 表示新父节点的 <mxCell>。
// index - 插入位置索引。
// source - 可选的源端点 <mxCell>。
// target - 可选的目标端点 <mxCell>。
// absolute - 布尔值，指示是否保持绝对位置。
// constrain - 布尔值，指示是否约束子节点。
// extend - 布尔值，指示是否扩展父节点。
// 功能说明：执行单元格插入逻辑，处理位置调整、父节点扩展和子节点约束。
// 关键步骤：
// 1. 调整单元格的几何位置，考虑父节点偏移。
// 2. 添加单元格到模型。
// 3. 根据配置自动调整大小、扩展父节点或约束子节点。
// 4. 设置源和目标端点。
// 事件处理：触发 mxEvent.CELLS_ADDED 事件，通知单元格添加。
// 注意事项：支持绝对位置、父节点扩展和子节点约束，需确保参数有效性。
mxGraph.prototype.cellsAdded = function(cells, parent, index, source, target, absolute, constrain, extend)
{
	if (cells != null && parent != null && index != null)
	{
		this.model.beginUpdate();
		try
		{
			var parentState = (absolute) ? this.view.getState(parent) : null;
			var o1 = (parentState != null) ? parentState.origin : null;
			var zero = new mxPoint(0, 0);
            // 获取父节点状态和原点，用于位置调整

			for (var i = 0; i < cells.length; i++)
			{
				if (cells[i] == null)
				{
					index--;
                    // 跳过空单元格并调整索引
				}
				else
				{
					var previous = this.model.getParent(cells[i]);
                    // 获取当前单元格的父节点

					// Keeps the cell at its absolute location
                    // 保持单元格的绝对位置
					if (o1 != null && cells[i] != parent && parent != previous)
					{
						var oldState = this.view.getState(previous);
						var o2 = (oldState != null) ? oldState.origin : zero;
						var geo = this.model.getGeometry(cells[i]);
	
						if (geo != null)
						{
							var dx = o2.x - o1.x;
							var dy = o2.y - o1.y;
                            // 计算位置偏移

							// FIXME: Cells should always be inserted first before any other edit
							// to avoid forward references in sessions.
							geo = geo.clone();
							geo.translate(dx, dy);
                            // 克隆并平移几何对象

							if (!geo.relative && this.model.isVertex(cells[i]) &&
								!this.isAllowNegativeCoordinates())
							{
								geo.x = Math.max(0, geo.x);
								geo.y = Math.max(0, geo.y);
                                // 禁止负坐标
							}
							
							this.model.setGeometry(cells[i], geo);
                            // 更新单元格几何
						}
					}
	
					// Decrements all following indices
					// if cell is already in parent
					if (parent == previous && index + i > this.model.getChildCount(parent))
					{
						index--;
                        // 调整索引以避免重复插入
					}

					this.model.add(parent, cells[i], index + i);
                    // 将单元格添加到父节点

					if (this.autoSizeCellsOnAdd)
					{
						this.autoSizeCell(cells[i], true);
                        // 自动调整单元格大小
					}

					// Extends the parent or constrains the child
                    // 扩展父节点或约束子节点
					if ((extend == null || extend) &&
						this.isExtendParentsOnAdd(cells[i]) && this.isExtendParent(cells[i]))
					{
						this.extendParent(cells[i]);
                        // 扩展父节点以包含子节点
					}
					
					// Additionally constrains the child after extending the parent
                    // 在扩展父节点后额外约束子节点
					if (constrain == null || constrain)
					{
						this.constrainChild(cells[i]);
                        // 约束子节点位置
					}
					
					// Sets the source terminal
                    // 设置源端点
					if (source != null)
					{
						this.cellConnected(cells[i], source, true);
					}
					
					// Sets the target terminal
                    // 设置目标端点
					if (target != null)
					{
						this.cellConnected(cells[i], target, false);
					}
				}
			}
			
			this.fireEvent(new mxEventObject(mxEvent.CELLS_ADDED, 'cells', cells,
				'parent', parent, 'index', index, 'source', source, 'target', target,
				'absolute', absolute));
            // 触发单元格添加事件
		}
		finally
		{
			this.model.endUpdate();
            // 结束模型更新
		}
	}
};

/**
 * Function: autoSizeCell
 * 
 * Resizes the specified cell to just fit around the its label and/or children
 * 
 * Parameters:
 * 
 * cell - <mxCells> to be resized.
 * recurse - Optional boolean which specifies if all descendants should be
 * autosized. Default is true.
 */
// 函数：autoSizeCell
// 调整指定单元格的大小以适应其标签和/或子节点。
// 参数：
// cell - 要调整大小的 <mxCells>。
// recurse - 可选布尔值，指定是否调整所有后代的大小，默认值为 true。
// 功能说明：自动调整单元格大小以适应内容。
// 关键步骤：
// 1. 递归调整子节点大小。
// 2. 如果是顶点且允许自动调整大小，调用 updateCellSize。
// 事件处理：通过 updateCellSize 触发 mxEvent.UPDATE_CELL_SIZE 事件。
// 注意事项：仅对顶点有效，依赖 isAutoSizeCell 方法检查是否允许调整。
mxGraph.prototype.autoSizeCell = function(cell, recurse)
{
	recurse = (recurse != null) ? recurse : true;
	
	if (recurse)
	{
		var childCount = this.model.getChildCount(cell);
		
		for (var i = 0; i < childCount; i++)
		{
			this.autoSizeCell(this.model.getChildAt(cell, i));
            // 递归调整子节点大小
		}
	}

	if (this.getModel().isVertex(cell) && this.isAutoSizeCell(cell))
	{
		this.updateCellSize(cell);
        // 更新单元格大小
	}
};

/**
 * Function: removeCells
 * 
 * Removes the given cells from the graph including all connected edges if
 * includeEdges is true. The change is carried out using <cellsRemoved>.
 * This method fires <mxEvent.REMOVE_CELLS> while the transaction is in
 * progress. The removed cells are returned as an array.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to remove. If null is specified then the
 * selection cells which are deletable are used.
 * includeEdges - Optional boolean which specifies if all connected edges
 * should be removed as well. Default is true.
 */
// 函数：removeCells
// 从图中移除指定单元格，如果 includeEdges 为 true，则包括所有连接的边，使用 <cellsRemoved> 执行更改。
// 在事务进行期间触发 <mxEvent.REMOVE_CELLS> 事件，返回移除的单元格数组。
// 参数：
// cells - 要移除的 <mxCells> 数组，若为 null 则使用可删除的选中单元格。
// includeEdges - 可选布尔值，指定是否移除所有连接的边，默认值为 true。
// 功能说明：批量移除单元格及相关边。
// 关键步骤：
// 1. 如果未指定单元格，使用可删除的选中单元格。
// 2. 如果 includeEdges 为 true，添加所有相关边。
// 3. 调用 cellsRemoved 执行移除。
// 事件处理：触发 mxEvent.REMOVE_CELLS 事件，通知单元格移除。
// 注意事项：确保只移除可删除的单元格，避免移除不可见边导致问题。
mxGraph.prototype.removeCells = function(cells, includeEdges)
{
	includeEdges = (includeEdges != null) ? includeEdges : true;
	
	if (cells == null)
	{
		cells = this.getDeletableCells(this.getSelectionCells());
        // 获取可删除的选中单元格
	}

	// Adds all edges to the cells
    // 添加所有相关边
	if (includeEdges)
	{
		// FIXME: Remove duplicate cells in result or do not add if
		// in cells or descendant of cells
		cells = this.getDeletableCells(this.addAllEdges(cells));
	}
	else
	{
		cells = cells.slice();
		
		// Removes edges that are currently not
		// visible as those cannot be updated
        // 移除当前不可见的边
		var edges = this.getDeletableCells(this.getAllEdges(cells));
		var dict = new mxDictionary();
		
		for (var i = 0; i < cells.length; i++)
		{
			dict.put(cells[i], true);
		}
		
		for (var i = 0; i < edges.length; i++)
		{
			if (this.view.getState(edges[i]) == null &&
				!dict.get(edges[i]))
			{
				dict.put(edges[i], true);
				cells.push(edges[i]);
                // 添加不可见的边到移除列表
			}
		}
	}

	this.model.beginUpdate();
	try
	{
		this.cellsRemoved(cells);
		this.fireEvent(new mxEventObject(mxEvent.REMOVE_CELLS, 
				'cells', cells, 'includeEdges', includeEdges));
        // 执行移除并触发移除事件
	}
	finally
	{
		this.model.endUpdate();
        // 结束模型更新
	}
	
	return cells;
};

/**
 * Function: cellsRemoved
 * 
 * Removes the given cells from the model. This method fires
 * <mxEvent.CELLS_REMOVED> while the transaction is in progress.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to remove.
 */
// 函数：cellsRemoved
// 从模型中移除指定单元格，在事务进行期间触发 <mxEvent.CELLS_REMOVED> 事件。
// 参数：
// cells - 要移除的 <mxCells> 数组。
// 功能说明：执行单元格移除逻辑，处理相关边的断开连接。
// 关键步骤：
// 1. 创建字典以加速查找。
// 2. 断开未移除边的连接。
// 3. 从模型中移除单元格。
// 事件处理：触发 mxEvent.CELLS_REMOVED 事件，通知单元格移除。
// 注意事项：确保断开未移除边的连接，避免悬空边。
mxGraph.prototype.cellsRemoved = function(cells)
{
	if (cells != null && cells.length > 0)
	{
		var scale = this.view.scale;
		var tr = this.view.translate;
		
		this.model.beginUpdate();
		try
		{
			// Creates hashtable for faster lookup
            // 创建哈希表以加速查找
			var dict = new mxDictionary();
			
			for (var i = 0; i < cells.length; i++)
			{
				dict.put(cells[i], true);
			}
			
			for (var i = 0; i < cells.length; i++)
			{
				// Disconnects edges which are not being removed
                // 断开未移除的边的连接
				var edges = this.getAllEdges([cells[i]]);
				
				var disconnectTerminal = mxUtils.bind(this, function(edge, source)
				{
					var geo = this.model.getGeometry(edge);

					if (geo != null)
					{
						// Checks if terminal is being removed
                        // 检查端点是否被移除
						var terminal = this.model.getTerminal(edge, source);
						var connected = false;
						var tmp = terminal;
						
						while (tmp != null)
						{
							if (cells[i] == tmp)
							{
								connected = true;
								break;
							}
							
							tmp = this.model.getParent(tmp);
						}

						if (connected)
						{
							geo = geo.clone();
							var state = this.view.getState(edge);

							if (state != null && state.absolutePoints != null)
							{
								var pts = state.absolutePoints;
								var n = (source) ? 0 : pts.length - 1;

								geo.setTerminalPoint(new mxPoint(
									pts[n].x / scale - tr.x - state.origin.x,
									pts[n].y / scale - tr.y - state.origin.y), source);
                                // 设置新的终端点坐标
							}
							else
							{
								// Fallback to center of terminal if routing
								// points are not available to add new point
								// KNOWN: Should recurse to find parent offset
								// of edge for nested groups but invisible edges
								// should be removed in removeCells step
								// 如果路由点不可用，回退到端点中心
								var tstate = this.view.getState(terminal);
								
								if (tstate != null)
								{
									geo.setTerminalPoint(new mxPoint(
										tstate.getCenterX() / scale - tr.x,
										tstate.getCenterY() / scale - tr.y), source);
								}
							}

							this.model.setGeometry(edge, geo);
							this.model.setTerminal(edge, null, source);
                            // 更新几何并移除端点连接
						}
					}
				});
				
				for (var j = 0; j < edges.length; j++)
				{
					if (!dict.get(edges[j]))
					{
						dict.put(edges[j], true);
						disconnectTerminal(edges[j], true);
						disconnectTerminal(edges[j], false);
                        // 断开边的源和目标端点
					}
				}

				this.model.remove(cells[i]);
                // 从模型中移除单元格
			}
			
			this.fireEvent(new mxEventObject(mxEvent.CELLS_REMOVED, 'cells', cells));
            // 触发单元格移除事件
		}
		finally
		{
			this.model.endUpdate();
            // 结束模型更新
		}
	}
};

/**
 * Function: splitEdge
 * 
 * Splits the given edge by adding the newEdge between the previous source
 * and the given cell and reconnecting the source of the given edge to the
 * given cell. This method fires <mxEvent.SPLIT_EDGE> while the transaction
 * is in progress. Returns the new edge that was inserted.
 * 
 * Parameters:
 * 
 * edge - <mxCell> that represents the edge to be splitted.
 * cells - <mxCells> that represents the cells to insert into the edge.
 * newEdge - <mxCell> that represents the edge to be inserted.
 * dx - Optional integer that specifies the vector to move the cells.
 * dy - Optional integer that specifies the vector to move the cells.
 * x - Integer that specifies the x-coordinate of the drop location.
 * y - Integer that specifies the y-coordinate of the drop location.
 * parent - Optional parent to insert the cell. If null the parent of
 * the edge is used.
 */
// 函数：splitEdge
// 通过在现有边和给定单元格之间添加新边来分割指定边，并将原边的源端点重新连接到给定单元格。
// 在事务进行期间触发 <mxEvent.SPLIT_EDGE> 事件，返回插入的新边。
// 参数：
// edge - 表示要分割的边的 <mxCell>。
// cells - 表示要插入边的单元格 <mxCells>。
// newEdge - 表示要插入的新边 <mxCell>。
// dx - 可选整数，指定单元格移动的 x 坐标向量。
// dy - 可选整数，指定单元格移动的 y 坐标向量。
// x - 指定放置位置 x 坐标的整数。
// y - 指定放置位置 y 坐标的整数。
// parent - 可选的插入单元格的父节点，若为 null 则使用边的父节点。
// 功能说明：分割边并插入新边，调整连接和位置。
// 关键步骤：
// 1. 如果未提供新边，克隆现有边。
// 2. 调整新边和原边的控制点。
// 3. 移动插入的单元格。
// 4. 添加新边和单元格到父节点。
// 5. 重新连接原边和新边。
// 事件处理：触发 mxEvent.SPLIT_EDGE 事件，通知边分割。
// 注意事项：需确保新边的控制点正确分割，避免几何错误。
mxGraph.prototype.splitEdge = function(edge, cells, newEdge, dx, dy, x, y, parent)
{
	dx = dx || 0;
	dy = dy || 0;

	parent = (parent != null) ? parent : this.model.getParent(edge);
	var source = this.model.getTerminal(edge, true);
	
	this.model.beginUpdate();
	try
	{
		if (newEdge == null)
		{
			newEdge = this.cloneCell(edge);
            // 克隆现有边作为新边

			// Removes waypoints before/after new cell
            // 移除新单元格前后控制点
			var state = this.view.getState(edge);
			var geo = this.getCellGeometry(newEdge);
			
			if (geo != null && geo.points != null && state != null)
			{
				var t = this.view.translate;
				var s = this.view.scale;
				var idx = mxUtils.findNearestSegment(state, (dx + t.x) * s, (dy + t.y) * s);
				geo.points = geo.points.slice(0, idx);
                // 分割新边的控制点

				geo = this.getCellGeometry(edge);
				
				if (geo != null && geo.points != null)
				{
					geo = geo.clone();
					geo.points = geo.points.slice(idx);
					this.model.setGeometry(edge, geo);
                    // 分割原边的控制点
				}
			}
		}
		
		this.cellsMoved(cells, dx, dy, false, false);
        // 移动插入的单元格
		this.cellsAdded(cells, parent, this.model.getChildCount(parent), null, null,
				true);
        // 添加单元格到父节点
		this.cellsAdded([newEdge], parent, this.model.getChildCount(parent),
				source, cells[0], false);
        // 添加新边并连接源端点
		this.cellConnected(edge, cells[0], true);
        // 重新连接原边到新单元格
		this.fireEvent(new mxEventObject(mxEvent.SPLIT_EDGE, 'edge', edge,
				'cells', cells, 'newEdge', newEdge, 'dx', dx, 'dy', dy));
        // 触发边分割事件
	}
	finally
	{
		this.model.endUpdate();
        // 结束模型更新
	}

	return newEdge;
};

/**
 * Group: Cell visibility
 */
// 分组：单元格可见性

/**
 * Function: toggleCells
 * 
 * Sets the visible state of the specified cells and all connected edges
 * if includeEdges is true. The change is carried out using <cellsToggled>.
 * This method fires <mxEvent.TOGGLE_CELLS> while the transaction is in
 * progress. Returns the cells whose visible state was changed.
 * 
 * Parameters:
 * 
 * show - Boolean that specifies the visible state to be assigned.
 * cells - Array of <mxCells> whose visible state should be changed. If
 * null is specified then the selection cells are used.
 * includeEdges - Optional boolean indicating if the visible state of all
 * connected edges should be changed as well. Default is true.
 */
// 函数：toggleCells
// 设置指定单元格及其连接边的可见状态（若 includeEdges 为 true），使用 <cellsToggled> 执行更改。
// 在事务进行期间触发 <mxEvent.TOGGLE_CELLS> 事件，返回更改了可见状态的单元格。
// 参数：
// show - 布尔值，指定要设置的可见状态。
// cells - 要更改可见状态的 <mxCells> 数组，若为 null 则使用选中单元格。
// includeEdges - 可选布尔值，指定是否更改所有连接边的可见状态，默认值为 true。
// 功能说明：切换单元格及其相关边的可见性。
// 关键步骤：
// 1. 如果未指定单元格，使用选中单元格。
// 2. 如果 includeEdges 为 true，添加所有相关边。
// 3. 调用 cellsToggled 执行可见性更改。
// 事件处理：触发 mxEvent.TOGGLE_CELLS 事件，通知可见性更改。
// 注意事项：确保只更改可切换的单元格和边，避免影响不可见元素。
mxGraph.prototype.toggleCells = function(show, cells, includeEdges)
{
	if (cells == null)
	{
		cells = this.getSelectionCells();
        // 获取选中单元格
	}

	// Adds all connected edges recursively
    // 递归添加所有连接边
	if (includeEdges)
	{
		cells = this.addAllEdges(cells);
	}

	this.model.beginUpdate();
	try
	{
		this.cellsToggled(cells, show);
		this.fireEvent(new mxEventObject(mxEvent.TOGGLE_CELLS,
			'show', show, 'cells', cells, 'includeEdges', includeEdges));
        // 执行可见性切换并触发事件
	}
	finally
	{
		this.model.endUpdate();
        // 结束模型更新
	}

	return cells;
};

/**
 * Function: cellsToggled
 * 
 * Sets the visible state of the specified cells.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> whose visible state should be changed.
 * show - Boolean that specifies the visible state to be assigned.
 */
// 函数：cellsToggled
// 设置指定单元格的可见状态。
// 参数：
// cells - 要更改可见状态的 <mxCells> 数组。
// show - 布尔值，指定要设置的可见状态。
// 功能说明：直接设置单元格的可见性。
// 关键步骤：遍历单元格并调用模型的 setVisible 方法。
// 事件处理：无直接事件触发，依赖 toggleCells 的 mxEvent.TOGGLE_CELLS 事件。
mxGraph.prototype.cellsToggled = function(cells, show)
{
	if (cells != null && cells.length > 0)
	{
		this.model.beginUpdate();
		try
		{
			for (var i = 0; i < cells.length; i++)
			{
				this.model.setVisible(cells[i], show);
                // 设置单元格可见性
			}
		}
		finally
		{
			this.model.endUpdate();
            // 结束模型更新
		}
	}
};

/**
 * Group: Folding
 */
// 分组：折叠

/**
 * Function: foldCells
 * 
 * Sets the collapsed state of the specified cells and all descendants
 * if recurse is true. The change is carried out using <cellsFolded>.
 * This method fires <mxEvent.FOLD_CELLS> while the transaction is in
 * progress. Returns the cells whose collapsed state was changed.
 * 
 * Parameters:
 * 
 * collapsed - Boolean indicating the collapsed state to be assigned.
 * recurse - Optional boolean indicating if the collapsed state of all
 * descendants should be set. Default is false.
 * cells - Array of <mxCells> whose collapsed state should be set. If
 * null is specified then the foldable selection cells are used.
 * checkFoldable - Optional boolean indicating of isCellFoldable should be
 * checked. Default is false.
 * evt - Optional native event that triggered the invocation.
 */
// 函数：foldCells
// 设置指定单元格及其后代的折叠状态（若 recurse 为 true），使用 <cellsFolded> 执行更改。
// 在事务进行期间触发 <mxEvent.FOLD_CELLS> 事件，返回更改了折叠状态的单元格。
// 参数：
// collapse - 布尔值，指定要设置的折叠状态。
// recurse - 可选布尔值，指定是否设置所有后代的折叠状态，默认值为 false。
// cells - 要设置折叠状态的 <mxCells> 数组，若为 null 则使用可折叠的选中单元格。
// checkFoldable - 可选布尔值，指定是否检查 isCellFoldable，默认值为 false。
// evt - 可选的触发调用的原生事件。
// 功能说明：切换单元格的折叠或展开状态。
// 关键步骤：
// 1. 如果未指定单元格，使用可折叠的选中单元格。
// 2. 停止编辑以避免冲突。
// 3. 调用 cellsFolded 执行折叠状态更改。
// 事件处理：触发 mxEvent.FOLD_CELLS 事件，通知折叠状态更改。
// 交互逻辑：支持通过事件（如鼠标点击）动态折叠或展开单元格。
mxGraph.prototype.foldCells = function(collapse, recurse, cells, checkFoldable, evt)
{
	recurse = (recurse != null) ? recurse : false;
	
	if (cells == null)
	{
		cells = this.getFoldableCells(this.getSelectionCells(), collapse);
        // 获取可折叠的选中单元格
	}

	this.stopEditing(false);
    // 停止编辑

	this.model.beginUpdate();
	try
	{
		this.cellsFolded(cells, collapse, recurse, checkFoldable);
		this.fireEvent(new mxEventObject(mxEvent.FOLD_CELLS,
			'collapse', collapse, 'recurse', recurse, 'cells', cells));
        // 执行折叠并触发事件
	}
	finally
	{
		this.model.endUpdate();
        // 结束模型更新
	}

	return cells;
};

/**
 * Function: cellsFolded
 * 
 * Sets the collapsed state of the specified cells. This method fires
 * <mxEvent.CELLS_FOLDED> while the transaction is in progress. Returns the
 * cells whose collapsed state was changed.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> whose collapsed state should be set.
 * collapsed - Boolean indicating the collapsed state to be assigned.
 * recurse - Boolean indicating if the collapsed state of all descendants
 * should be set.
 * checkFoldable - Optional boolean indicating of isCellFoldable should be
 * checked. Default is false.
 */
// 函数：cellsFolded
// 设置指定单元格的折叠状态，在事务进行期间触发 <mxEvent.CELLS_FOLDED> 事件。
// 返回更改了折叠状态的单元格。
// 参数：
// cells - 要设置折叠状态的 <mxCells> 数组。
// collapse - 布尔值，指定要设置的折叠状态。
// recurse - 布尔值，指定是否设置所有后代的折叠状态。
// checkFoldable - 可选布尔值，指定是否检查 isCellFoldable，默认值为 false。
// 功能说明：执行单元格折叠状态的更改，处理父节点扩展和子节点约束。
// 关键步骤：
// 1. 检查单元格是否可折叠且状态需要更改。
// 2. 设置折叠状态并交换边界。
// 3. 扩展父节点或约束子节点。
// 事件处理：触发 mxEvent.CELLS_FOLDED 事件，通知折叠状态更改。
mxGraph.prototype.cellsFolded = function(cells, collapse, recurse, checkFoldable)
{
	if (cells != null && cells.length > 0)
	{
		this.model.beginUpdate();
		try
		{
			for (var i = 0; i < cells.length; i++)
			{
				if ((!checkFoldable || this.isCellFoldable(cells[i], collapse)) &&
					collapse != this.isCellCollapsed(cells[i]))
				{
					this.model.setCollapsed(cells[i], collapse);
					this.swapBounds(cells[i], collapse);
                    // 设置折叠状态并交换边界

					if (this.isExtendParent(cells[i]))
					{
						this.extendParent(cells[i]);
                        // 扩展父节点
					}

					if (recurse)
					{
						var children = this.model.getChildren(cells[i]);
						this.cellsFolded(children, collapse, recurse);
                        // 递归折叠子节点
					}
					
					this.constrainChild(cells[i]);
                    // 约束子节点
				}
			}
			
			this.fireEvent(new mxEventObject(mxEvent.CELLS_FOLDED,
				'cells', cells, 'collapse', collapse, 'recurse', recurse));
            // 触发折叠事件
		}
		finally
		{
			this.model.endUpdate();
            // 结束模型更新
		}
	}
};

/**
 * Function: swapBounds
 * 
 * Swaps the alternate and the actual bounds in the geometry of the given
 * cell invoking <updateAlternateBounds> before carrying out the swap.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which the bounds should be swapped.
 * willCollapse - Boolean indicating if the cell is going to be collapsed.
 */
// 函数：swapBounds
// 在给定单元格的几何中交换备用边界和实际边界，在交换前调用 <updateAlternateBounds>。
// 参数：
// cell - 要交换边界的 <mxCell>。
// willCollapse - 布尔值，指示单元格是否将要折叠。
// 功能说明：更新并交换单元格的几何边界。
// 关键步骤：
// 1. 克隆几何对象。
// 2. 调用 updateAlternateBounds 更新备用边界。
// 3. 执行边界交换并更新几何。
// 注意事项：确保几何对象存在，避免无效操作。
mxGraph.prototype.swapBounds = function(cell, willCollapse)
{
	if (cell != null)
	{
		var geo = this.model.getGeometry(cell);
		
		if (geo != null)
		{
			geo = geo.clone();
			
			this.updateAlternateBounds(cell, geo, willCollapse);
			geo.swap();
            // 交换边界

			this.model.setGeometry(cell, geo);
            // 更新几何
		}
	}
};

/**
 * Function: updateAlternateBounds
 * 
 * Updates or sets the alternate bounds in the given geometry for the given
 * cell depending on whether the cell is going to be collapsed. If no
 * alternate bounds are defined in the geometry and
 * <collapseToPreferredSize> is true, then the preferred size is used for
 * the alternate bounds. The top, left corner is always kept at the same
 * location.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which the geometry is being udpated.
 * g - <mxGeometry> for which the alternate bounds should be updated.
 * willCollapse - Boolean indicating if the cell is going to be collapsed.
 */
// 函数：updateAlternateBounds
// 根据单元格是否将要折叠，更新或设置给定几何的备用边界。
// 如果几何中未定义备用边界且 <collapseToPreferredSize> 为 true，则使用首选大小作为备用边界。
// 左上角始终保持在同一位置。
// 参数：
// cell - 要更新几何的 <mxCell>。
// g - 要更新备用边界的 <mxGeometry>。
// willCollapse - 布尔值，指示单元格是否将要折叠。
// 功能说明：更新单元格的备用边界以支持折叠或展开。
// 关键步骤：
// 1. 如果没有备用边界，使用首选大小。
// 2. 设置备用边界的位置。
// 3. 处理旋转角度对边界的影响。
// 重要配置参数：collapseToPreferredSize 控制是否使用首选大小。
// 注意事项：确保左上角位置不变，考虑样式中的旋转角度。
mxGraph.prototype.updateAlternateBounds = function(cell, geo, willCollapse)
{
	if (cell != null && geo != null)
	{
		var style = this.getCurrentCellStyle(cell);

		if (geo.alternateBounds == null)
		{
			var bounds = geo;
			
			if (this.collapseToPreferredSize)
			{
				var tmp = this.getPreferredSizeForCell(cell);
				
				if (tmp != null)
				{
					bounds = tmp;

					var startSize = mxUtils.getValue(style, mxConstants.STYLE_STARTSIZE);

					if (startSize > 0)
					{
						bounds.height = Math.max(bounds.height, startSize);
                        // 确保高度不小于起始大小
					}
				}
			}
			
			geo.alternateBounds = new mxRectangle(0, 0, bounds.width, bounds.height);
            // 设置备用边界
		}
		
		if (geo.alternateBounds != null)
		{
			geo.alternateBounds.x = geo.x;
			geo.alternateBounds.y = geo.y;
            // 设置备用边界位置

			var alpha = mxUtils.toRadians(style[mxConstants.STYLE_ROTATION] || 0);
			
			if (alpha != 0)
			{
				var dx = geo.alternateBounds.getCenterX() - geo.getCenterX();
				var dy = geo.alternateBounds.getCenterY() - geo.getCenterY();
	
				var cos = Math.cos(alpha);
				var sin = Math.sin(alpha);
	
				var dx2 = cos * dx - sin * dy;
				var dy2 = sin * dx + cos * dy;
				
				geo.alternateBounds.x += dx2 - dx;
				geo.alternateBounds.y += dy2 - dy;
                // 处理旋转角度
			}
		}
	}
};

/**
 * Function: addAllEdges
 * 
 * Returns an array with the given cells and all edges that are connected
 * to a cell or one of its descendants.
 */
// 函数：addAllEdges
// 返回包含给定单元格及其所有连接边的数组，包括后代的边。
// 功能说明：收集单元格及其后代的所有相关边。
// 关键步骤：
// 1. 复制输入单元格数组。
// 2. 添加所有连接边并移除重复项。
// 注意事项：确保返回的数组不包含重复边。
mxGraph.prototype.addAllEdges = function(cells)
{
	var allCells = cells.slice();
	
	return mxUtils.removeDuplicates(allCells.concat(this.getAllEdges(cells)));
};

/**
 * Function: getAllEdges
 * 
 * Returns all edges connected to the given cells or its descendants.
 */
// 函数：getAllEdges
// 返回连接到给定单元格或其后代的所有边。
// 功能说明：递归获取所有相关边。
// 关键步骤：
// 1. 遍历单元格及其子节点。
// 2. 收集每个单元格的边。
// 注意事项：确保递归处理所有后代边。
mxGraph.prototype.getAllEdges = function(cells)
{
	var edges = [];
	
	if (cells != null)
	{
		for (var i = 0; i < cells.length; i++)
		{
			var edgeCount = this.model.getEdgeCount(cells[i]);
			
			for (var j = 0; j < edgeCount; j++)
			{
				edges.push(this.model.getEdgeAt(cells[i], j));
                // 添加当前单元格的边
			}

			// Recurses
            // 递归处理
			var children = this.model.getChildren(cells[i]);
			edges = edges.concat(this.getAllEdges(children));
		}
	}
	
	return edges;
};

/**
 * Group: Cell sizing
 */
// 分组：单元格大小调整

/**
 * Function: updateCellSize
 * 
 * Updates the size of the given cell in the model using <cellSizeUpdated>.
 * This method fires <mxEvent.UPDATE_CELL_SIZE> while the transaction is in
 * progress. Returns the cell whose size was updated.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose size should be updated.
 */
// 函数：updateCellSize
// 使用 <cellSizeUpdated> 更新模型中指定单元格的大小。
// 在事务进行期间触发 <mxEvent.UPDATE_CELL_SIZE> 事件，返回更新了大小的单元格。
// 参数：
// cell - 要更新大小的 <mxCell>。
// ignoreChildren - 可选布尔值，指定是否忽略子节点，默认值为 false。
// 功能说明：更新单元格大小以适应其内容。
// 关键步骤：调用 cellSizeUpdated 执行大小更新。
// 事件处理：触发 mxEvent.UPDATE_CELL_SIZE 事件，通知大小更新。
mxGraph.prototype.updateCellSize = function(cell, ignoreChildren)
{
	ignoreChildren = (ignoreChildren != null) ? ignoreChildren : false;
	
	this.model.beginUpdate();				
	try
	{
		this.cellSizeUpdated(cell, ignoreChildren);
		this.fireEvent(new mxEventObject(mxEvent.UPDATE_CELL_SIZE,
				'cell', cell, 'ignoreChildren', ignoreChildren));
        // 执行大小更新并触发事件
	}
	finally
	{
		this.model.endUpdate();
        // 结束模型更新
	}
	
	return cell;
};

/**
 * Function: cellSizeUpdated
 * 
 * Updates the size of the given cell in the model using
 * <getPreferredSizeForCell> to get the new size.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which the size should be changed.
 */
// 函数：cellSizeUpdated
// 使用 <getPreferredSizeForCell> 获取新大小并更新模型中指定单元格的大小。
// 参数：
// cell - 要更改大小的 <mxCell>。
// 功能说明：根据首选大小调整单元格的几何大小。
// 关键步骤：
// 1. 获取首选大小。
// 2. 根据单元格类型（泳道或普通顶点）调整几何。
// 3. 考虑子节点边界（若未忽略）。
// 4. 调用 cellsResized 设置新大小。
// 注意事项：支持泳道和普通顶点的不同大小调整逻辑，考虑对齐和折叠状态。
mxGraph.prototype.cellSizeUpdated = function(cell, ignoreChildren)
{
	if (cell != null)
	{
		this.model.beginUpdate();				
		try
		{
			var size = this.getPreferredSizeForCell(cell);
			var geo = this.model.getGeometry(cell);
			
			if (size != null && geo != null)
			{
				var collapsed = this.isCellCollapsed(cell);
				geo = geo.clone();

				if (this.isSwimlane(cell))
				{
					var style = this.getCellStyle(cell);
					var cellStyle = this.model.getStyle(cell);

					if (cellStyle == null)
					{
						cellStyle = '';
					}

					if (mxUtils.getValue(style, mxConstants.STYLE_HORIZONTAL, true))
					{
						cellStyle = mxUtils.setStyle(cellStyle,
								mxConstants.STYLE_STARTSIZE, size.height + 8);
                        // 设置泳道水平起始大小

						if (collapsed)
						{
							geo.height = size.height + 8;
						}

						geo.width = size.width;
					}
					else
					{
						cellStyle = mxUtils.setStyle(cellStyle,
								mxConstants.STYLE_STARTSIZE, size.width + 8);
                        // 设置泳道垂直起始大小

						if (collapsed)
						{
							geo.width = size.width + 8;
						}

						geo.height = size.height;
					}

					this.model.setStyle(cell, cellStyle);
                    // 更新单元格样式
				}
				else
				{
					var state = this.view.createState(cell);
					var align = (state.style[mxConstants.STYLE_ALIGN] || mxConstants.ALIGN_CENTER);
					
					if (align == mxConstants.ALIGN_RIGHT)
					{
						geo.x += geo.width - size.width;
					}
					else if (align == mxConstants.ALIGN_CENTER)
					{
						geo.x += Math.round((geo.width - size.width) / 2);
					}
                    // 调整水平对齐

					var valign = this.getVerticalAlign(state);
					
					if (valign == mxConstants.ALIGN_BOTTOM)
					{
						geo.y += geo.height - size.height;
					}
					else if (valign == mxConstants.ALIGN_MIDDLE)
					{
						geo.y += Math.round((geo.height - size.height) / 2);
					}
                    // 调整垂直对齐

					geo.width = size.width;
					geo.height = size.height;
				}

				if (!ignoreChildren && !collapsed)
				{
					var bounds = this.view.getBounds(this.model.getChildren(cell));

					if (bounds != null)
					{
						var tr = this.view.translate;
						var scale = this.view.scale;

						var width = (bounds.x + bounds.width) / scale - geo.x - tr.x;
						var height = (bounds.y + bounds.height) / scale - geo.y - tr.y;

						geo.width = Math.max(geo.width, width);
						geo.height = Math.max(geo.height, height);
                        // 考虑子节点边界调整大小
					}
				}

				this.cellsResized([cell], [geo], false);
                // 设置新几何大小
			}
		}
		finally
		{
			this.model.endUpdate();
            // 结束模型更新
		}
	}
};

/**
 * Function: getPreferredSizeForCell
 * 
 * Returns the preferred width and height of the given <mxCell> as an
 * <mxRectangle>. To implement a minimum width, add a new style eg.
 * minWidth in the vertex and override this method as follows.
 * 
 * (code)
 * var graphGetPreferredSizeForCell = graph.getPreferredSizeForCell;
 * graph.getPreferredSizeForCell = function(cell)
 * {
 *   var result = graphGetPreferredSizeForCell.apply(this, arguments);
 *   var style = this.getCellStyle(cell);
 *   
 *   if (style['minWidth'] > 0)
 *   {
 *     result.width = Math.max(style['minWidth'], result.width);
 *   }
 * 
 *   return result;
 * };
 * (end)
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which the preferred size should be returned.
 * textWidth - Optional maximum text width for word wrapping.
 */
// 函数：getPreferredSizeForCell
// 返回给定 <mxCell> 的首选宽度和高度，作为 <mxRectangle>。
// 参数：
// cell - 要返回首选大小的 <mxCell>。
// textWidth - 可选的最大文本宽度，用于换行。
// 功能说明：计算单元格的首选大小，考虑样式、图像和标签。
// 关键步骤：
// 1. 获取单元格样式和状态。
// 2. 计算图像、间距和标签的大小。
// 3. 根据网格对齐调整大小。
// 样式设置：支持图像大小、字体大小、间距和对齐设置。
// 注意事项：非边单元格支持标签换行和图像大小调整。
mxGraph.prototype.getPreferredSizeForCell = function(cell, textWidth)
{
	var result = null;
	
	if (cell != null)
	{
		var state = this.view.createState(cell);
		var style = state.style;

		if (!this.model.isEdge(cell))
		{
			var fontSize = style[mxConstants.STYLE_FONTSIZE] || mxConstants.DEFAULT_FONTSIZE;
			var dx = 0;
			var dy = 0;
			
			// Adds dimension of image if shape is a label
            // 如果形状是标签，添加图像尺寸
			if (this.getImage(state) != null || style[mxConstants.STYLE_IMAGE] != null)
			{
				if (style[mxConstants.STYLE_SHAPE] == mxConstants.SHAPE_LABEL)
				{
					if (style[mxConstants.STYLE_VERTICAL_ALIGN] == mxConstants.ALIGN_MIDDLE)
					{
						dx += parseFloat(style[mxConstants.STYLE_IMAGE_WIDTH]) || mxLabel.prototype.imageSize;
					}
					
					if (style[mxConstants.STYLE_ALIGN] != mxConstants.ALIGN_CENTER)
					{
						dy += parseFloat(style[mxConstants.STYLE_IMAGE_HEIGHT]) || mxLabel.prototype.imageSize;
					}
				}
			}

			// Adds spacings
            // 添加间距
			dx += 2 * (style[mxConstants.STYLE_SPACING] || 0);
			dx += style[mxConstants.STYLE_SPACING_LEFT] || 0;
			dx += style[mxConstants.STYLE_SPACING_RIGHT] || 0;

			dy += 2 * (style[mxConstants.STYLE_SPACING] || 0);
			dy += style[mxConstants.STYLE_SPACING_TOP] || 0;
			dy += style[mxConstants.STYLE_SPACING_BOTTOM] || 0;
			
			// Add spacing for collapse/expand icon
			// LATER: Check alignment and use constants
			// for image spacing
			// 为折叠/展开图标添加间距
			var image = this.getFoldingImage(state);
			
			if (image != null)
			{
				dx += image.width + 8;
			}

			// Adds space for label
            // 为标签添加空间
			var value = this.cellRenderer.getLabelValue(state);

			if (value != null && value.length > 0)
			{
				if (!this.isHtmlLabel(state.cell))
				{
					value = mxUtils.htmlEntities(value, false);
				}
				
				value = value.replace(/\n/g, '<br>');
				
				var size = mxUtils.getSizeForString(value, fontSize,
					style[mxConstants.STYLE_FONTFAMILY], textWidth,
					style[mxConstants.STYLE_FONTSTYLE]);
				var width = size.width + dx;
				var height = size.height + dy;
				
				if (!mxUtils.getValue(style, mxConstants.STYLE_HORIZONTAL, true))
				{
					var tmp = height;
					
					height = width;
					width = tmp;
                    // 垂直方向交换宽高
				}
			
				if (this.gridEnabled)
				{
					width = this.snap(width + this.gridSize / 2);
					height = this.snap(height + this.gridSize / 2);
                    // 按网格对齐
				}

				result = new mxRectangle(0, 0, width, height);
			}
			else
			{
				var gs2 = 4 * this.gridSize;
				result = new mxRectangle(0, 0, gs2, gs2);
                // 默认大小
			}
		}
	}
	
	return result;
};

/**
 * Function: resizeCell
 * 
 * Sets the bounds of the given cell using <resizeCells>. Returns the
 * cell which was passed to the function.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose bounds should be changed.
 * bounds - <mxRectangle> that represents the new bounds.
 */
// 函数：resizeCell
// 使用 <resizeCells> 设置给定单元格的边界，返回传入的单元格。
// 参数：
// cell - 要更改边界的 <mxCell>。
// bounds - 表示新边界的 <mxRectangle>。
// 功能说明：调整单个单元格的边界大小。
// 关键步骤：调用 resizeCells 处理边界调整。
// 事件处理：通过 resizeCells 触发 mxEvent.RESIZE_CELLS 事件。
mxGraph.prototype.resizeCell = function(cell, bounds, recurse)
{
	return this.resizeCells([cell], [bounds], recurse)[0];
};

/**
 * Function: resizeCells
 * 
 * Sets the bounds of the given cells and fires a <mxEvent.RESIZE_CELLS>
 * event while the transaction is in progress. Returns the cells which
 * have been passed to the function.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> whose bounds should be changed.
 * bounds - Array of <mxRectangles> that represent the new bounds.
 */
// 函数：resizeCells
// 设置给定单元格的边界并在事务进行期间触发 <mxEvent.RESIZE_CELLS> 事件。
// 返回传入的单元格。
// 参数：
// cells - 要更改边界的 <mxCells> 数组。
// bounds - 表示新边界的 <mxRectangles> 数组。
// recurse - 可选布尔值，指定是否调整子节点大小。
// 功能说明：批量调整单元格边界，支持递归调整。
// 关键步骤：
// 1. 调用 cellsResized 执行边界调整。
// 2. 触发 mxEvent.RESIZE_CELLS 事件。
// 事件处理：触发 mxEvent.RESIZE_CELLS 事件，通知边界更改。
// 注意事项：确保 cells 和 bounds 数组长度匹配。
mxGraph.prototype.resizeCells = function(cells, bounds, recurse)
{
	recurse = (recurse != null) ? recurse : this.isRecursiveResize();
	
	this.model.beginUpdate();
	try
	{
		var prev = this.cellsResized(cells, bounds, recurse);
		this.fireEvent(new mxEventObject(mxEvent.RESIZE_CELLS,
			'cells', cells, 'bounds', bounds, 'previous', prev));
        // 执行边界调整并触发事件
	}
	finally
	{
		this.model.endUpdate();
        // 结束模型更新
	}

	return cells;
};

/**
 * Function: cellsResized
 * 
 * Sets the bounds of the given cells and fires a <mxEvent.CELLS_RESIZED>
 * event. If <extendParents> is true, then the parent is extended if a
 * child size is changed so that it overlaps with the parent.
 * 
 * The following example shows how to control group resizes to make sure
 * that all child cells stay within the group.
 * 
 * (code)
 * graph.addListener(mxEvent.CELLS_RESIZED, function(sender, evt)
 * {
 *   var cells = evt.getProperty('cells');
 *   
 *   if (cells != null)
 *   {
 *     for (var i = 0; i < cells.length; i++)
 *     {
 *       if (graph.getModel().getChildCount(cells[i]) > 0)
 *       {
 *         var geo = graph.getCellGeometry(cells[i]);
 *         
 *         if (geo != null)
 *         {
 *           var children = graph.getChildCells(cells[i], true, true);
 *           var bounds = graph.getBoundingBoxFromGeometry(children, true);
 *           
 *           geo = geo.clone();
 *           geo.width = Math.max(geo.width, bounds.width);
 *           geo.height = Math.max(geo.height, bounds.height);
 *           
 *           graph.getModel().setGeometry(cells[i], geo);
 *         }
 *       }
 *     }
 *   }
 * });
 * (end)
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> whose bounds should be changed.
 * bounds - Array of <mxRectangles> that represent the new bounds.
 * recurse - Optional boolean that specifies if the children should be resized.
 */
// 函数：cellsResized
// 设置给定单元格的边界并触发 <mxEvent.CELLS_RESIZED> 事件。
// 如果 <extendParents> 为 true，则在子节点大小更改导致与父节点重叠时扩展父节点。
// 参数：
// cells - 要更改边界的 <mxCells> 数组。
// bounds - 表示新边界的 <mxRectangles> 数组。
// recurse - 可选布尔值，指定是否调整子节点大小。
// 功能说明：批量调整单元格边界，支持父节点扩展和子节点约束。
// 关键步骤：
// 1. 遍历单元格并调用 cellResized。
// 2. 如果需要，扩展父节点或约束子节点。
// 3. 如果 resetEdgesOnResize 为 true，重置相关边。
// 事件处理：触发 mxEvent.CELLS_RESIZED 事件，通知边界更改。
// 注意事项：确保父节点扩展和子节点约束逻辑正确执行。
mxGraph.prototype.cellsResized = function(cells, bounds, recurse)
{
	recurse = (recurse != null) ? recurse : false;
	var prev = [];

	if (cells != null && bounds != null && cells.length == bounds.length)
	{
		this.model.beginUpdate();
		try
		{
			for (var i = 0; i < cells.length; i++)
			{
				prev.push(this.cellResized(cells[i], bounds[i], false, recurse));
                // 调整单个单元格大小

				if (this.isExtendParent(cells[i]))
				{
					this.extendParent(cells[i]);
                    // 扩展父节点
				}
				
				this.constrainChild(cells[i]);
                // 约束子节点
			}

			if (this.resetEdgesOnResize)
			{
				this.resetEdges(cells);
                // 重置相关边
			}
			
			this.fireEvent(new mxEventObject(mxEvent.CELLS_RESIZED,
				'cells', cells, 'bounds', bounds, 'previous', prev));
            // 触发边界调整事件
		}
		finally
		{
			this.model.endUpdate();
            // 结束模型更新
		}
	}
	
	return prev;
};

/**
 * Function: cellResized
 * 
 * Resizes the parents recursively so that they contain the complete area
 * of the resized child cell.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose bounds should be changed.
 * bounds - <mxRectangles> that represent the new bounds.
 * ignoreRelative - Boolean that indicates if relative cells should be ignored.
 * recurse - Optional boolean that specifies if the children should be resized.
 */
 // 函数：cellResized
 // 递归调整父节点大小，确保包含调整后的子节点的完整区域
 // 参数：
 // cell - 要更改边界的 <mxCell> 对象
 // bounds - 表示新边界的 <mxRectangles> 对象
 // ignoreRelative - 布尔值，指示是否忽略相对定位的单元
 // recurse - 可选布尔值，指定是否调整子节点大小
mxGraph.prototype.cellResized = function(cell, bounds, ignoreRelative, recurse)
{
	var prev = this.model.getGeometry(cell);

	// 获取当前单元的几何信息
	// 中文：获取单元的当前几何信息并存储在 prev 中

	if (prev != null && (prev.x != bounds.x || prev.y != bounds.y ||
		prev.width != bounds.width || prev.height != bounds.height))
	{
		// 检查几何信息是否发生变化（位置或大小）
		// 中文：判断单元的位置或大小是否与新边界不同

		var geo = prev.clone();

		// 克隆当前几何信息以进行修改
		// 中文：克隆当前几何信息以便进行更改

		if (!ignoreRelative && geo.relative)
		{
			// 处理相对定位的单元
			// 中文：如果不忽略相对定位且单元为相对定位，调整偏移量
			var offset = geo.offset;

			if (offset != null)
			{
				// 更新偏移量以适应新位置
				// 中文：根据新边界更新偏移量的 x 和 y 值
				offset.x += bounds.x - geo.x;
				offset.y += bounds.y - geo.y;
			}
		}
		else
		{
			// 更新绝对定位单元的坐标
			// 中文：直接将新边界的 x 和 y 值赋给几何信息
			geo.x = bounds.x;
			geo.y = bounds.y;
		}

		geo.width = bounds.width;
		geo.height = bounds.height;

		// 更新几何信息的宽高
		// 中文：将新边界的宽度和高度赋给几何信息

		if (!geo.relative && this.model.isVertex(cell) && !this.isAllowNegativeCoordinates())
		{
			// 确保顶点单元的坐标非负
			// 中文：如果单元为顶点且不允许负坐标，则将坐标限制为非负值
			geo.x = Math.max(0, geo.x);
			geo.y = Math.max(0, geo.y);
		}

		this.model.beginUpdate();
		// 开始模型更新事务
		// 中文：开启模型更新事务以确保一致性
		try
		{
			if (recurse)
			{
				// 递归调整子节点大小
				// 中文：如果需要递归，则调用 resizeChildCells 调整子节点
				this.resizeChildCells(cell, geo);
			}
						
			this.model.setGeometry(cell, geo);
			// 更新单元的几何信息
			// 中文：将修改后的几何信息应用到单元

			this.constrainChildCells(cell);
			// 约束子节点的位置
			// 中文：调用 constrainChildCells 确保子节点符合约束
		}
		finally
		{
			this.model.endUpdate();
			// 结束模型更新事务
			// 中文：完成模型更新事务，提交更改
		}
	}
	
	return prev;
	// 返回原始几何信息
	// 中文：返回调整前的几何信息
};

/**
 * Function: resizeChildCells
 * 
 * Resizes the child cells of the given cell for the given new geometry with
 * respect to the current geometry of the cell.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that has been resized.
 * newGeo - <mxGeometry> that represents the new bounds.
 */
 // 函数：resizeChildCells
 // 根据给定单元的新几何信息调整子节点大小
 // 参数：
 // cell - 已调整大小的 <mxCell> 对象
 // newGeo - 表示新边界的 <mxGeometry> 对象
mxGraph.prototype.resizeChildCells = function(cell, newGeo)
{
	var geo = this.model.getGeometry(cell);
	// 获取当前单元的几何信息
	// 中文：获取给定单元的当前几何信息

	var dx = (geo.width != 0) ? newGeo.width / geo.width : 1;
	var dy = (geo.height != 0) ? newGeo.height / geo.height : 1;
	// 计算水平和垂直缩放因子
	// 中文：根据新旧几何信息计算宽度和高度的缩放比例

	var childCount = this.model.getChildCount(cell);
	// 获取子节点数量
	// 中文：获取给定单元的子节点数量

	for (var i = 0; i < childCount; i++)
	{
		// 遍历子节点并进行缩放
		// 中文：循环遍历所有子节点，调用 scaleCell 进行缩放
		this.scaleCell(this.model.getChildAt(cell, i), dx, dy, true);
	}
};

/**
 * Function: constrainChildCells
 * 
 * Constrains the children of the given cell using <constrainChild>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that has been resized.
 */
 // 函数：constrainChildCells
 // 使用 constrainChild 方法约束给定单元的子节点
 // 参数：
 // cell - 已调整大小的 <mxCell> 对象
mxGraph.prototype.constrainChildCells = function(cell)
{
	var childCount = this.model.getChildCount(cell);
	// 获取子节点数量
	// 中文：获取给定单元的子节点数量

	for (var i = 0; i < childCount; i++)
	{
		// 遍历子节点并应用约束
		// 中文：循环遍历所有子节点，调用 constrainChild 应用约束
		this.constrainChild(this.model.getChildAt(cell, i));
	}
};

/**
 * Function: scaleCell
 * 
 * Scales the points, position and size of the given cell according to the
 * given vertical and horizontal scaling factors.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose geometry should be scaled.
 * dx - Horizontal scaling factor.
 * dy - Vertical scaling factor.
 * recurse - Boolean indicating if the child cells should be scaled.
 */
 // 函数：scaleCell
 // 根据给定的水平和垂直缩放因子调整单元的点、位置和大小
 // 参数：
 // cell - 要缩放几何信息的 <mxCell> 对象
 // dx - 水平缩放因子
 // dy - 垂直缩放因子
 // recurse - 布尔值，指示是否递归缩放子节点
mxGraph.prototype.scaleCell = function(cell, dx, dy, recurse)
{
	var geo = this.model.getGeometry(cell);
	// 获取单元的几何信息
	// 中文：获取给定单元的几何信息

	if (geo != null)
	{
		var style = this.getCurrentCellStyle(cell);
		// 获取单元的当前样式
		// 中文：获取单元的当前样式配置

		geo = geo.clone();
		// 克隆几何信息以进行修改
		// 中文：克隆几何信息以便进行更改

		// Stores values for restoring based on style
		var x = geo.x;
		var y = geo.y
		var w = geo.width;
		var h = geo.height;
		// 保存原始位置和大小以便根据样式恢复
		// 中文：保存原始 x、y 坐标及宽度、高度以便根据样式配置恢复

		geo.scale(dx, dy, style[mxConstants.STYLE_ASPECT] == 'fixed');
		// 根据缩放因子调整几何信息
		// 中文：根据缩放因子和样式中的纵横比设置调整几何信息

		if (style[mxConstants.STYLE_RESIZE_WIDTH] == '1')
		{
			// 根据样式调整宽度
			// 中文：如果样式允许调整宽度，则按缩放因子更新宽度
			geo.width = w * dx;
		}
		else if (style[mxConstants.STYLE_RESIZE_WIDTH] == '0')
		{
			// 保持宽度不变
			// 中文：如果样式禁止调整宽度，则保持原始宽度
			geo.width = w;
		}
		
		if (style[mxConstants.STYLE_RESIZE_HEIGHT] == '1')
		{
			// 根据样式调整高度
			// 中文：如果样式允许调整高度，则按缩放因子更新高度
			geo.height = h * dy;
		}
		else if (style[mxConstants.STYLE_RESIZE_HEIGHT] == '0')
		{
			// 保持高度不变
			// 中文：如果样式禁止调整高度，则保持原始高度
			geo.height = h;
		}
		
		if (!this.isCellMovable(cell))
		{
			// 如果单元不可移动，恢复原始位置
			// 中文：如果单元不可移动，则将坐标恢复为原始值
			geo.x = x;
			geo.y = y;
		}
		
		if (!this.isCellResizable(cell))
		{
			// 如果单元不可调整大小，恢复原始尺寸
			// 中文：如果单元不可调整大小，则将宽高恢复为原始值
			geo.width = w;
			geo.height = h;
		}

		if (this.model.isVertex(cell))
		{
			// 如果是顶点，递归调整大小
			// 中文：如果单元是顶点，则调用 cellResized 递归调整
			this.cellResized(cell, geo, true, recurse);
		}
		else
		{
			// 直接更新几何信息
			// 中文：如果不是顶点，直接设置新的几何信息
			this.model.setGeometry(cell, geo);
		}
	}
};

/**
 * Function: extendParent
 * 
 * Resizes the parents recursively so that they contain the complete area
 * of the resized child cell.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that has been resized.
 */
 // 函数：extendParent
 // 递归调整父节点大小，确保包含调整后的子节点的完整区域
 // 参数：
 // cell - 已调整大小的 <mxCell> 对象
mxGraph.prototype.extendParent = function(cell)
{
	if (cell != null)
	{
		var parent = this.model.getParent(cell);
		var p = this.getCellGeometry(parent);
		// 获取父节点及其几何信息
		// 中文：获取给定单元的父节点及其几何信息

		if (parent != null && p != null && !this.isCellCollapsed(parent))
		{
			// 如果父节点存在且未折叠
			// 中文：检查父节点是否存在、几何信息是否有效且未折叠
			var geo = this.getCellGeometry(cell);
			// 获取子节点的几何信息
			// 中文：获取子节点的几何信息

			if (geo != null && !geo.relative &&
				(p.width < geo.x + geo.width ||
				p.height < geo.y + geo.height))
			{
				// 如果父节点区域不足以包含子节点
				// 中文：检查父节点是否需要扩展以包含子节点的区域
				p = p.clone();
				// 克隆父节点几何信息
				// 中文：克隆父节点几何信息以便修改

				p.width = Math.max(p.width, geo.x + geo.width);
				p.height = Math.max(p.height, geo.y + geo.height);
				// 扩展父节点尺寸
				// 中文：更新父节点的宽高以包含子节点的完整区域

				this.cellsResized([parent], [p], false);
				// 调整父节点大小
				// 中文：调用 cellsResized 更新父节点几何信息
			}
		}
	}
};

/**
 * Group: Cell moving
 */
 // 分组：单元移动
 // 中文：包含与单元移动相关的函数

/**
 * Function: importCells
 * 
 * Clones and inserts the given cells into the graph using the move
 * method and returns the inserted cells. This shortcut is used if
 * cells are inserted via datatransfer.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to be imported.
 * dx - Integer that specifies the x-coordinate of the vector. Default is 0.
 * dy - Integer that specifies the y-coordinate of the vector. Default is 0.
 * target - <mxCell> that represents the new parent of the cells.
 * evt - Mouseevent that triggered the invocation.
 * mapping - Optional mapping for existing clones.
 */
 // 函数：importCells
 // 克隆并插入给定单元到图形中，使用 move 方法，并返回插入的单元
 // 参数：
 // cells - 要导入的 <mxCells> 数组
 // dx - 指定移动向量的 x 坐标，默认为 0
 // dy - 指定移动向量的 y 坐标，默认为 0
 // target - 表示新父节点的 <mxCell> 对象
 // evt - 触发调用的鼠标事件
 // mapping - 可选的现有克隆映射
mxGraph.prototype.importCells = function(cells, dx, dy, target, evt, mapping)
{	
	return this.moveCells(cells, dx, dy, true, target, evt, mapping);
	// 调用 moveCells 执行导入操作
	// 中文：通过调用 moveCells 方法实现单元的克隆和插入
};

/**
 * Function: moveCells
 * 
 * Moves or clones the specified cells and moves the cells or clones by the
 * given amount, adding them to the optional target cell. The evt is the
 * mouse event as the mouse was released. The change is carried out using
 * <cellsMoved>. This method fires <mxEvent.MOVE_CELLS> while the
 * transaction is in progress. Returns the cells that were moved.
 * 
 * Use the following code to move all cells in the graph.
 * 
 * (code)
 * graph.moveCells(graph.getChildCells(null, true, true), 10, 10);
 * (end)
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to be moved, cloned or added to the target.
 * dx - Integer that specifies the x-coordinate of the vector. Default is 0.
 * dy - Integer that specifies the y-coordinate of the vector. Default is 0.
 * clone - Boolean indicating if the cells should be cloned. Default is false.
 * target - <mxCell> that represents the new parent of the cells.
 * evt - Mouseevent that triggered the invocation.
 * mapping - Optional mapping for existing clones.
 */
 // 函数：moveCells
 // 移动或克隆指定单元，并按给定量移动，添加到可选的目标单元
 // 参数：
 // cells - 要移动、克隆或添加的 <mxCells> 数组
 // dx - 指定移动向量的 x 坐标，默认为 0
 // dy - 指定移动向量的 y 坐标，默认为 0
 // clone - 布尔值，指示是否克隆单元，默认为 false
 // target - 表示新父节点的 <mxCell> 对象
 // evt - 触发调用的鼠标事件
 // mapping - 可选的现有克隆映射
mxGraph.prototype.moveCells = function(cells, dx, dy, clone, target, evt, mapping)
{
	dx = (dx != null) ? dx : 0;
	dy = (dy != null) ? dy : 0;
	clone = (clone != null) ? clone : false;
	// 设置默认值
	// 中文：为 dx、dy 和 clone 参数设置默认值

	if (cells != null && (dx != 0 || dy != 0 || clone || target != null))
	{
		// 检查是否需要执行移动操作
		// 中文：验证是否有单元需要移动、克隆或目标不为空

		// Removes descendants with ancestors in cells to avoid multiple moving
		cells = this.model.getTopmostCells(cells);
		var origCells = cells;
		// 移除包含祖先的子节点以避免重复移动
		// 中文：获取最顶层单元，保存原始单元列表

		this.model.beginUpdate();
		// 开始模型更新事务
		// 中文：开启模型更新事务以确保一致性
		try
		{
			// Faster cell lookups to remove relative edge labels with selected
			// terminals to avoid explicit and implicit move at same time
			var dict = new mxDictionary();
			// 创建字典以加速单元查找
			// 中文：使用字典存储单元以快速检查

			for (var i = 0; i < cells.length; i++)
			{
				dict.put(cells[i], true);
				// 将单元添加到字典
				// 中文：将当前单元标记为已处理
			}
			
			var isSelected = mxUtils.bind(this, function(cell)
			{
				// 检查单元是否在选中列表中
				// 中文：定义函数检查单元或其祖先是否在选中列表中
				while (cell != null)
				{
					if (dict.get(cell))
					{
						return true;
					}
					
					cell = this.model.getParent(cell);
				}
				
				return false;
			});
			
			// Removes relative edge labels with selected terminals
			var checked = [];
			// 移除包含选中端点的相对边标签
			// 中文：创建数组存储通过检查的单元

			for (var i = 0; i < cells.length; i++)
			{
				var geo = this.getCellGeometry(cells[i]);
				var parent = this.model.getParent(cells[i]);
				// 获取单元几何信息和父节点
				// 中文：获取当前单元的几何信息和父节点

				if ((geo == null || !geo.relative) || !this.model.isEdge(parent) ||
					(!isSelected(this.model.getTerminal(parent, true)) &&
					!isSelected(this.model.getTerminal(parent, false))))
				{
					// 过滤不需要移动的边标签
					// 中文：检查单元是否为非相对定位或非边标签
					checked.push(cells[i]);
				}
			}

			cells = checked;
			// 更新单元列表
			// 中文：将过滤后的单元列表赋值给 cells

			if (clone)
			{
				// 克隆单元
				// 中文：如果需要克隆，则调用 cloneCells 生成新单元
				cells = this.cloneCells(cells, this.isCloneInvalidEdges(), mapping);

				if (target == null)
				{
					target = this.getDefaultParent();
					// 设置默认父节点
					// 中文：如果未指定目标，则使用默认父节点
				}
			}

			// FIXME: Cells should always be inserted first before any other edit
			// to avoid forward references in sessions.
			// Need to disable allowNegativeCoordinates if target not null to
			// allow for temporary negative numbers until cellsAdded is called.
			var previous = this.isAllowNegativeCoordinates();
			// 保存当前是否允许负坐标的设置
			// 中文：记录当前是否允许负坐标的配置

			if (target != null)
			{
				this.setAllowNegativeCoordinates(true);
				// 临时允许负坐标
				// 中文：如果有目标节点，临时允许负坐标
			}
			
			this.cellsMoved(cells, dx, dy, !clone && this.isDisconnectOnMove()
					&& this.isAllowDanglingEdges(), target == null,
					this.isExtendParentsOnMove() && target == null);
			// 执行单元移动
			// 中文：调用 cellsMoved 方法移动单元，处理断开连接和扩展父节点

			this.setAllowNegativeCoordinates(previous);
			// 恢复负坐标设置
			// 中文：恢复原始的负坐标配置

			if (target != null)
			{
				var index = this.model.getChildCount(target);
				this.cellsAdded(cells, target, index, null, null, true);
				// 将单元添加到目标节点
				// 中文：将移动后的单元添加到目标父节点

				// Restores parent edge on cloned edge labels
				if (clone)
				{
					// 恢复克隆边标签的父节点
					// 中文：对克隆的边标签恢复其原始父节点
					for (var i = 0; i < cells.length; i++)
					{
						var geo = this.getCellGeometry(cells[i]);
						var parent = this.model.getParent(origCells[i]);
						// 获取几何信息和原始父节点
						// 中文：获取克隆单元的几何信息和原始单元的父节点

						if (geo != null && geo.relative &&
							this.model.isEdge(parent) &&
							this.model.contains(parent))
						{
							// 将克隆单元添加回原始父节点
							// 中文：如果单元为相对定位且父节点为边，则添加回父节点
							this.model.add(parent, cells[i]);
						}
					}
				}
			}

			// Dispatches a move event
			this.fireEvent(new mxEventObject(mxEvent.MOVE_CELLS, 'cells', cells,
				'dx', dx, 'dy', dy, 'clone', clone, 'target', target, 'event', evt));
			// 触发移动事件
			// 中文：触发 MOVE_CELLS 事件，传递单元、位移、克隆状态等信息
		}
		finally
		{
			this.model.endUpdate();
			// 结束模型更新事务
			// 中文：完成模型更新事务，提交所有更改
		}
	}

	return cells;
	// 返回移动后的单元
	// 中文：返回处理后的单元列表
};

/**
 * Function: cellsMoved
 * 
 * Moves the specified cells by the given vector, disconnecting the cells
 * using disconnectGraph is disconnect is true. This method fires
 * <mxEvent.CELLS_MOVED> while the transaction is in progress.
 */
 // 函数：cellsMoved
 // 按给定向量移动指定单元，如果 disconnect 为 true 则断开连接
 // 参数：
 // cells - 要移动的 <mxCells> 数组
 // dx - 移动向量的 x 坐标
 // dy - 移动向量的 y 坐标
 // disconnect - 布尔值，指示是否断开连接
 // constrain - 布尔值，指示是否约束子节点
 // extend - 布尔值，指示是否扩展父节点
mxGraph.prototype.cellsMoved = function(cells, dx, dy, disconnect, constrain, extend)
{
	if (cells != null && (dx != 0 || dy != 0))
	{
		// 检查是否需要移动
		// 中文：验证是否有单元需要移动且位移不为零

		extend = (extend != null) ? extend : false;
		// 设置扩展父节点的默认值
		// 中文：为 extend 参数设置默认值

		this.model.beginUpdate();
		// 开始模型更新事务
		// 中文：开启模型更新事务以确保一致性
		try
		{
			if (disconnect)
			{
				// 断开单元连接
				// 中文：如果需要断开连接，则调用 disconnectGraph
				this.disconnectGraph(cells);
			}

			for (var i = 0; i < cells.length; i++)
			{
				// 遍历单元并移动
				// 中文：循环遍历所有单元，调用 translateCell 移动
				this.translateCell(cells[i], dx, dy);
				
				if (extend && this.isExtendParent(cells[i]))
				{
					// 扩展父节点
					// 中文：如果需要扩展父节点，则调用 extendParent
					this.extendParent(cells[i]);
				}
				else if (constrain)
				{
					// 约束子节点
					// 中文：如果需要约束，则调用 constrainChild
					this.constrainChild(cells[i]);
				}
			}

			if (this.resetEdgesOnMove)
			{
				// 重置边
				// 中文：如果设置了 resetEdgesOnMove，则重置相关边
				this.resetEdges(cells);
			}
			
			this.fireEvent(new mxEventObject(mxEvent.CELLS_MOVED,
				'cells', cells, 'dx', dx, 'dy', dy, 'disconnect', disconnect));
			// 触发单元移动事件
			// 中文：触发 CELLS_MOVED 事件，传递单元、位移等信息
		}
		finally
		{
			this.model.endUpdate();
			// 结束模型更新事务
			// 中文：完成模型更新事务，提交所有更改
		}
	}
};

/**
 * Function: translateCell
 * 
 * Translates the geometry of the given cell and stores the new,
 * translated geometry in the model as an atomic change.
 */
// 方法目的：平移指定单元格的几何位置，并将新的几何信息存储到模型中，作为原子性变更。
// 关键变量说明：cell（待平移的单元格），dx（x轴平移距离），dy（y轴平移距离）。
mxGraph.prototype.translateCell = function(cell, dx, dy)
{
	var geo = this.model.getGeometry(cell);
    // 获取单元格的几何信息，存储在 geo 变量中。

	if (geo != null)
	{
		dx = parseFloat(dx);
		dy = parseFloat(dy);
        // 将输入的平移距离转换为浮点数，确保数值有效性。
		geo = geo.clone();
        // 克隆几何对象，防止直接修改原始数据。
		geo.translate(dx, dy);
        // 执行平移操作，将几何对象沿 x 和 y 轴移动 dx 和 dy。

		if (!geo.relative && this.model.isVertex(cell) && !this.isAllowNegativeCoordinates())
		{
			geo.x = Math.max(0, parseFloat(geo.x));
			geo.y = Math.max(0, parseFloat(geo.y));
            // 特殊处理：若几何对象不是相对定位且单元格是顶点，且不允许负坐标，
            // 则确保 x 和 y 坐标不小于 0。
		}
		
		if (geo.relative && !this.model.isEdge(cell))
		{
			var parent = this.model.getParent(cell);
			var angle = 0;
            // 若几何对象是相对定位且单元格不是边，则获取父单元格及其旋转角度。

			if (this.model.isVertex(parent))
			{
				var style = this.getCurrentCellStyle(parent);
				angle = mxUtils.getValue(style, mxConstants.STYLE_ROTATION, 0);
                // 获取父单元格的样式，提取旋转角度（默认为 0）。
			}
			
			if (angle != 0)
			{
				var rad = mxUtils.toRadians(-angle);
				var cos = Math.cos(rad);
				var sin = Math.sin(rad);
				var pt = mxUtils.getRotatedPoint(new mxPoint(dx, dy), cos, sin, new mxPoint(0, 0));
				dx = pt.x;
				dy = pt.y;
                // 若存在旋转角度，将平移距离 (dx, dy) 按父单元格的旋转角度进行旋转变换。
			}
			
			if (geo.offset == null)
			{
				geo.offset = new mxPoint(dx, dy);
                // 若几何对象无偏移量，则创建新的偏移点。
			}
			else
			{
				geo.offset.x = parseFloat(geo.offset.x) + dx;
				geo.offset.y = parseFloat(geo.offset.y) + dy;
                // 若存在偏移量，则累加平移距离。
			}
		}

		this.model.setGeometry(cell, geo);
        // 将更新后的几何信息存储到模型中。
	}
};

/**
 * Function: getCellContainmentArea
 * 
 * Returns the <mxRectangle> inside which a cell is to be kept.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which the area should be returned.
 */
// 方法目的：返回指定单元格应保持在内的矩形区域。
// 参数说明：cell（需要获取包含区域的单元格）。
mxGraph.prototype.getCellContainmentArea = function(cell)
{
	if (cell != null && !this.model.isEdge(cell))
	{
		var parent = this.model.getParent(cell);
        // 获取单元格的父节点，确保单元格不是边。

		if (parent != null && parent != this.getDefaultParent())
		{
			var g = this.model.getGeometry(parent);
            // 获取父节点的几何信息。

			if (g != null)
			{
				var x = 0;
				var y = 0;
				var w = g.width;
				var h = g.height;
                // 初始化包含区域的坐标和尺寸，基于父节点的几何信息。

				if (this.isSwimlane(parent))
				{
					var size = this.getStartSize(parent);
					var style = this.getCurrentCellStyle(parent);
					var dir = mxUtils.getValue(style, mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_EAST);
					var flipH = mxUtils.getValue(style, mxConstants.STYLE_FLIPH, 0) == 1;
					var flipV = mxUtils.getValue(style, mxConstants.STYLE_FLIPV, 0) == 1;
                    // 若父节点是泳道，获取其起始尺寸、样式、方向和翻转设置。
                    // 重要配置参数：STYLE_DIRECTION（泳道方向，默认为东），STYLE_FLIPH/V（水平/垂直翻转）。

					if (dir == mxConstants.DIRECTION_SOUTH || dir == mxConstants.DIRECTION_NORTH)
					{
						var tmp = size.width;
						size.width = size.height;
						size.height = tmp;
                        // 若方向为南北，交换宽度和高度以适应布局。
					}
					
					if ((dir == mxConstants.DIRECTION_EAST && !flipV) || (dir == mxConstants.DIRECTION_NORTH && !flipH) ||
						(dir == mxConstants.DIRECTION_WEST && flipV) || (dir == mxConstants.DIRECTION_SOUTH && flipH))
					{
						x = size.width;
						y = size.height;
                        // 根据方向和翻转设置调整包含区域的起始坐标。
					}

					w -= size.width;
					h -= size.height;
                    // 减去起始尺寸，得到实际可用的包含区域。
				}
				
				return new mxRectangle(x, y, w, h);
                // 返回计算得到的包含区域矩形。
			}
		}
	}
	
	return null;
    // 若无有效父节点或几何信息，返回 null。
};

/**
 * Function: getMaximumGraphBounds
 * 
 * Returns the bounds inside which the diagram should be kept as an
 * <mxRectangle>.
 */
// 方法目的：返回图表应保持在内的边界矩形。
mxGraph.prototype.getMaximumGraphBounds = function()
{
	return this.maximumGraphBounds;
    // 直接返回图表的边界属性 maximumGraphBounds。
};

/**
 * Function: constrainChild
 * 
 * Keeps the given cell inside the bounds returned by
 * <getCellContainmentArea> for its parent, according to the rules defined by
 * <getOverlap> and <isConstrainChild>. This modifies the cell's geometry
 * in-place and does not clone it.
 * 
 * Parameters:
 * 
 * cells - <mxCell> which should be constrained.
 * sizeFirst - Specifies if the size should be changed first. Default is true.
 */
// 方法目的：将指定单元格约束在父节点的包含区域内，遵循 getOverlap 和 isConstrainChild 规则，直接修改几何信息。
// 参数说明：cell（待约束的单元格），sizeFirst（是否优先调整尺寸，默认为 true）。
// 关键变量说明：geo（单元格几何信息），pgeo（父节点几何信息），max（最大边界矩形）。
mxGraph.prototype.constrainChild = function(cell, sizeFirst)
{
	sizeFirst = (sizeFirst != null) ? sizeFirst : true;
    // 设置 sizeFirst 参数，控制尺寸调整优先级，默认为 true。

	if (cell != null)
	{
		var geo = this.getCellGeometry(cell);
        // 获取单元格的几何信息。

		if (geo != null && (this.isConstrainRelativeChildren() || !geo.relative))
		{
			var parent = this.model.getParent(cell);
			var pgeo = this.getCellGeometry(parent);
			var max = this.getMaximumGraphBounds();
            // 获取父节点、父节点几何信息和图表最大边界。

			// Finds parent offset
			if (max != null)
			{
				var off = this.getBoundingBoxFromGeometry([parent], false);
                // 计算父节点的边界框，用于调整最大边界。

				if (off != null)
				{
					max = mxRectangle.fromRectangle(max);
					
					max.x -= off.x;
					max.y -= off.y;
                    // 调整最大边界，考虑父节点的偏移量。
				}
			}
			
			if (this.isConstrainChild(cell))
			{
				var tmp = this.getCellContainmentArea(cell);
                // 获取单元格的包含区域。

				if (tmp != null)
				{
					var overlap = this.getOverlap(cell);
                    // 获取单元格的重叠比例，用于调整包含区域。

					if (overlap > 0)
					{
						tmp = mxRectangle.fromRectangle(tmp);
						
						tmp.x -= tmp.width * overlap;
						tmp.y -= tmp.height * overlap;
						tmp.width += 2 * tmp.width * overlap;
						tmp.height += 2 * tmp.height * overlap;
                        // 若存在重叠比例，扩展包含区域以允许部分超出。
					}
					
					// Find the intersection between max and tmp
					if (max == null)
					{
						max = tmp;
                        // 若无最大边界，直接使用包含区域。
					}
					else
					{
						max = mxRectangle.fromRectangle(max);
						max.intersect(tmp);
                        // 计算最大边界与包含区域的交集。
					}
				}
			}
			
			if (max != null)
			{
				var cells = [cell];
                // 初始化包含当前单元格的数组。

				if (!this.isCellCollapsed(cell))
				{
					var desc = this.model.getDescendants(cell);
                    // 若单元格未折叠，获取其所有后代节点。

					for (var i = 0; i < desc.length; i++)
					{
						if (this.isCellVisible(desc[i]))
						{
							cells.push(desc[i]);
                            // 将可见的后代节点添加到单元格数组。
						}
					}
				}
				
				var bbox = this.getBoundingBoxFromGeometry(cells, false);
                // 计算包含所有单元格的边界框。

				if (bbox != null)
				{
					geo = geo.clone();
                    // 克隆几何对象，防止直接修改原始数据。

					// Cumulative horizontal movement
					var dx = 0;
                    // 初始化水平移动距离。

					if (geo.width > max.width)
					{
						dx = geo.width - max.width;
						geo.width -= dx;
                        // 若单元格宽度超出最大边界，调整宽度并记录移动距离。
					}
					
					if (bbox.x + bbox.width > max.x + max.width)
					{
						dx -= bbox.x + bbox.width - max.x - max.width - dx;
                        // 若边界框超出最大边界右边缘，计算水平调整距离。
					}
					
					// Cumulative vertical movement
					var dy = 0;
                    // 初始化垂直移动距离。

					if (geo.height > max.height)
					{
						dy = geo.height - max.height;
						geo.height -= dy;
                        // 若单元格高度超出最大边界，调整高度并记录移动距离。
					}
					
					if (bbox.y + bbox.height > max.y + max.height)
					{
						dy -= bbox.y + bbox.height - max.y - max.height - dy;
                        // 若边界框超出最大边界下边缘，计算垂直调整距离。
					}
					
					if (bbox.x < max.x)
					{
						dx -= bbox.x - max.x;
                        // 若边界框左侧超出最大边界，调整水平距离。
					}
					
					if (bbox.y < max.y)
					{
						dy -= bbox.y - max.y;
                        // 若边界框顶部超出最大边界，调整垂直距离。
					}
					
					if (dx != 0 || dy != 0)
					{
						if (geo.relative)
						{
							// Relative geometries are moved via absolute offset
							if (geo.offset == null)
							{
								geo.offset = new mxPoint();
                                // 若几何对象是相对定位且无偏移量，创建新的偏移点。
							}
						
							geo.offset.x += dx;
							geo.offset.y += dy;
                            // 累加偏移量以调整相对位置。
						}
						else
						{
							geo.x += dx;
							geo.y += dy;
                            // 直接调整绝对坐标。
						}
					}
					
					this.model.setGeometry(cell, geo);
                    // 将更新后的几何信息存储到模型中。
				}
			}
		}
	}
};

/**
 * Function: resetEdges
 * 
 * Resets the control points of the edges that are connected to the given
 * cells if not both ends of the edge are in the given cells array.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> for which the connected edges should be
 * reset.
 */
// 方法目的：重置连接到指定单元格的边的控制点，仅当边的两端不在给定单元格数组中时生效。
// 参数说明：cells（需要重置边的单元格数组）。
mxGraph.prototype.resetEdges = function(cells)
{
	if (cells != null)
	{
		// Prepares faster cells lookup
		var dict = new mxDictionary();
        // 创建字典以加速单元格查找。

		for (var i = 0; i < cells.length; i++)
		{
			dict.put(cells[i], true);
            // 将单元格添加到字典中，标记为存在。
		}
		
		this.model.beginUpdate();
        // 开始模型更新事务，确保原子性操作。
		try
		{
			for (var i = 0; i < cells.length; i++)
			{
				var edges = this.model.getEdges(cells[i]);
                // 获取与当前单元格连接的所有边。

				if (edges != null)
				{
					for (var j = 0; j < edges.length; j++)
					{
						var state = this.view.getState(edges[j]);
                        // 获取边的状态。

						var source = (state != null) ? state.getVisibleTerminal(true) : this.view.getVisibleTerminal(edges[j], true);
						var target = (state != null) ? state.getVisibleTerminal(false) : this.view.getVisibleTerminal(edges[j], false);
                        // 获取边的源端和目标端点。

						// Checks if one of the terminals is not in the given array
						if (!dict.get(source) || !dict.get(target))
						{
							this.resetEdge(edges[j]);
                            // 若边的任一端点不在给定数组中，重置该边的控制点。
						}
					}
				}
				
				this.resetEdges(this.model.getChildren(cells[i]));
                // 递归处理子节点的边。
			}
		}
		finally
		{
			this.model.endUpdate();
            // 结束模型更新事务。
		}
	}
};

/**
 * Function: resetEdge
 * 
 * Resets the control points of the given edge.
 * 
 * Parameters:
 * 
 * edge - <mxCell> whose points should be reset.
 */
// 方法目的：重置指定边的控制点。
// 参数说明：edge（需要重置控制点的边）。
mxGraph.prototype.resetEdge = function(edge)
{
	var geo = this.model.getGeometry(edge);
    // 获取边的几何信息。

	// Resets the control points
	if (geo != null && geo.points != null && geo.points.length > 0)
	{
		geo = geo.clone();
		geo.points = [];
        // 克隆几何对象并清空控制点数组。
		this.model.setGeometry(edge, geo);
        // 更新边的几何信息。
	}
	
	return edge;
    // 返回更新后的边。
};

/**
 * Group: Cell connecting and connection constraints
 */

/**
 * Function: getOutlineConstraint
 * 
 * Returns the constraint used to connect to the outline of the given state.
 */
// 方法目的：返回用于连接到指定状态轮廓的约束。
// 参数说明：point（连接点坐标），terminalState（终端状态），me（鼠标事件）。
mxGraph.prototype.getOutlineConstraint = function(point, terminalState, me)
{
	if (terminalState.shape != null)
	{
		var bounds = this.view.getPerimeterBounds(terminalState);
		var direction = terminalState.style[mxConstants.STYLE_DIRECTION];
        // 获取终端状态的边界和方向样式。
        // 重要配置参数：STYLE_DIRECTION（形状方向）。

		if (direction == mxConstants.DIRECTION_NORTH || direction == mxConstants.DIRECTION_SOUTH)
		{
			bounds.x += bounds.width / 2 - bounds.height / 2;
			bounds.y += bounds.height / 2 - bounds.width / 2;
			var tmp = bounds.width;
			bounds.width = bounds.height;
			bounds.height = tmp;
            // 若方向为南北，调整边界框，交换宽度和高度以适应方向。
		}
	
		var alpha = mxUtils.toRadians(terminalState.shape.getShapeRotation());
        // 获取形状的旋转角度并转换为弧度。

		if (alpha != 0)
		{
			var cos = Math.cos(-alpha);
			var sin = Math.sin(-alpha);
	
			var ct = new mxPoint(bounds.getCenterX(), bounds.getCenterY());
			point = mxUtils.getRotatedPoint(point, cos, sin, ct);
            // 若存在旋转角度，将连接点按旋转角度进行变换。
		}

		var sx = 1;
		var sy = 1;
		var dx = 0;
		var dy = 0;
        // 初始化缩放和偏移参数。

		// LATER: Add flipping support for image shapes
		if (this.getModel().isVertex(terminalState.cell))
		{
			var flipH = terminalState.style[mxConstants.STYLE_FLIPH];
			var flipV = terminalState.style[mxConstants.STYLE_FLIPV];
            // 获取水平和垂直翻转样式。
            // 重要配置参数：STYLE_FLIPH/V（水平/垂直翻转）。

			// Legacy support for stencilFlipH/V
			if (terminalState.shape != null && terminalState.shape.stencil != null)
			{
				flipH = mxUtils.getValue(terminalState.style, 'stencilFlipH', 0) == 1 || flipH;
				flipV = mxUtils.getValue(terminalState.style, 'stencilFlipV', 0) == 1 || flipV;
                // 支持旧版模板翻转样式，兼容 stencilFlipH/V。
			}
			
			if (direction == mxConstants.DIRECTION_NORTH || direction == mxConstants.DIRECTION_SOUTH)
			{
				var tmp = flipH;
				flipH = flipV;
				flipV = tmp;
                // 若方向为南北，交换水平和垂直翻转设置。
			}
			
			if (flipH)
			{
				sx = -1;
				dx = -bounds.width;
                // 若水平翻转，设置 x 轴缩放为 -1 并调整偏移。
			}
			
			if (flipV)
			{
				sy = -1;
				dy = -bounds.height ;
                // 若垂直翻转，设置 y 轴缩放为 -1 并调整偏移。
			}
		}
		
		point = new mxPoint((point.x - bounds.x) * sx - dx + bounds.x, (point.y - bounds.y) * sy - dy + bounds.y);
        // 根据缩放和偏移调整连接点坐标。

		var x = (bounds.width == 0) ? 0 : Math.round((point.x - bounds.x) * 1000 / bounds.width) / 1000;
		var y = (bounds.height == 0) ? 0 : Math.round((point.y - bounds.y) * 1000 / bounds.height) / 1000;
        // 计算连接点的相对坐标（归一化到 0-1 范围）。

		return new mxConnectionConstraint(new mxPoint(x, y), false);
        // 返回新的连接约束，包含相对坐标点，禁用周界约束。
	}
	
	return null;
    // 若无有效形状，返回 null。
};

/**
 * Function: getAllConnectionConstraints
 * 
 * Returns an array of all <mxConnectionConstraints> for the given terminal. If
 * the shape of the given terminal is a <mxStencilShape> then the constraints
 * of the corresponding <mxStencil> are returned.
 * 
 * Parameters:
 * 
 * terminal - <mxCellState> that represents the terminal.
 * source - Boolean that specifies if the terminal is the source or target.
 */
// 方法目的：返回指定终端的所有连接约束数组，若终端形状为模板形状，则返回对应模板的约束。
// 参数说明：terminal（终端状态），source（是否为源终端）。
mxGraph.prototype.getAllConnectionConstraints = function(terminal, source)
{
	if (terminal != null && terminal.shape != null && terminal.shape.stencil != null)
	{
		return terminal.shape.stencil.constraints;
        // 若终端存在且具有模板形状，返回模板的约束数组。
	}

	return null;
    // 否则返回 null。
};

/**
 * Function: getConnectionConstraint
 * 
 * Returns an <mxConnectionConstraint> that describes the given connection
 * point. This result can then be passed to <getConnectionPoint>.
 * 
 * Parameters:
 * 
 * edge - <mxCellState> that represents the edge.
 * terminal - <mxCellState> that represents the terminal.
 * source - Boolean indicating if the terminal is the source or target.
 */
// 方法目的：返回描述指定连接点的连接约束，可用于 getConnectionPoint 方法。
// 参数说明：edge（边状态），terminal（终端状态），source（是否为源终端）。
mxGraph.prototype.getConnectionConstraint = function(edge, terminal, source)
{
	var point = null;
	var x = edge.style[(source) ? mxConstants.STYLE_EXIT_X : mxConstants.STYLE_ENTRY_X];
    // 获取边的出口或入口 x 坐标。
    // 重要配置参数：STYLE_EXIT_X/ENTRY_X（边的出口/入口 x 坐标）。

	if (x != null)
	{
		var y = edge.style[(source) ? mxConstants.STYLE_EXIT_Y : mxConstants.STYLE_ENTRY_Y];
        // 获取边的出口或入口 y 坐标。
        // 重要配置参数：STYLE_EXIT_Y/ENTRY_Y（边的出口/入口 y 坐标）。

		if (y != null)
		{
			point = new mxPoint(parseFloat(x), parseFloat(y));
            // 若 x 和 y 坐标有效，创建连接点。
		}
	}
	
	var perimeter = false;
	var dx = 0, dy = 0;
    // 初始化周界约束和偏移量。

	if (point != null)
	{
		perimeter = mxUtils.getValue(edge.style, (source) ? mxConstants.STYLE_EXIT_PERIMETER :
			mxConstants.STYLE_ENTRY_PERIMETER, true);
        // 获取周界约束设置（默认为 true）。
        // 重要配置参数：STYLE_EXIT_PERIMETER/ENTRY_PERIMETER（是否使用周界约束）。

		//Add entry/exit offset
		dx = parseFloat(edge.style[(source) ? mxConstants.STYLE_EXIT_DX : mxConstants.STYLE_ENTRY_DX]);
		dy = parseFloat(edge.style[(source) ? mxConstants.STYLE_EXIT_DY : mxConstants.STYLE_ENTRY_DY]);
        // 获取出口或入口的偏移量。
        // 重要配置参数：STYLE_EXIT_DX/ENTRY_DX（x 轴偏移），STYLE_EXIT_DY/ENTRY_DY（y 轴偏移）。

		dx = isFinite(dx)? dx : 0;
		dy = isFinite(dy)? dy : 0;
        // 确保偏移量为有限值，否则设为 0。
	}

	return new mxConnectionConstraint(point, perimeter, null, dx, dy);
    // 返回新的连接约束，包含连接点、周界设置和偏移量。
};

/**
 * Function: setConnectionConstraint
 * 
 * Sets the <mxConnectionConstraint> that describes the given connection point.
 * If no constraint is given then nothing is changed. To remove an existing
 * constraint from the given edge, use an empty constraint instead.
 * 
 * Parameters:
 * 
 * edge - <mxCell> that represents the edge.
 * terminal - <mxCell> that represents the terminal.
 * source - Boolean indicating if the terminal is the source or target.
 * constraint - Optional <mxConnectionConstraint> to be used for this
 * connection.
 */
// 方法目的：设置指定连接点的连接约束，若无约束则不更改，若需移除约束则传入空约束。
// 参数说明：edge（边），terminal（终端），source（是否为源终端），constraint（可选的连接约束）。
// 事件处理逻辑：通过模型更新事务确保设置操作的原子性。
mxGraph.prototype.setConnectionConstraint = function(edge, terminal, source, constraint)
{
	if (constraint != null)
	{
		this.model.beginUpdate();
        // 开始模型更新事务。
		try
		{
			if (constraint == null || constraint.point == null)
			{
				this.setCellStyles((source) ? mxConstants.STYLE_EXIT_X :
					mxConstants.STYLE_ENTRY_X, null, [edge]);
				this.setCellStyles((source) ? mxConstants.STYLE_EXIT_Y :
					mxConstants.STYLE_ENTRY_Y, null, [edge]);
				this.setCellStyles((source) ? mxConstants.STYLE_EXIT_DX :
					mxConstants.STYLE_ENTRY_DX, null, [edge]);
				this.setCellStyles((source) ? mxConstants.STYLE_EXIT_DY :
					mxConstants.STYLE_ENTRY_DY, null, [edge]);
				this.setCellStyles((source) ? mxConstants.STYLE_EXIT_PERIMETER :
					mxConstants.STYLE_ENTRY_PERIMETER, null, [edge]);
                // 若约束为空或无连接点，移除所有相关样式。
			}
			else if (constraint.point != null)
			{
				this.setCellStyles((source) ? mxConstants.STYLE_EXIT_X :
					mxConstants.STYLE_ENTRY_X, constraint.point.x, [edge]);
				this.setCellStyles((source) ? mxConstants.STYLE_EXIT_Y :
					mxConstants.STYLE_ENTRY_Y, constraint.point.y, [edge]);
				this.setCellStyles((source) ? mxConstants.STYLE_EXIT_DX :
					mxConstants.STYLE_ENTRY_DX, constraint.dx, [edge]);
				this.setCellStyles((source) ? mxConstants.STYLE_EXIT_DY :
					mxConstants.STYLE_ENTRY_DY, constraint.dy, [edge]);
                // 设置连接点的 x、y 坐标及偏移量。
                // 样式设置说明：更新边的出口/入口坐标及偏移样式。

				// Only writes 0 since 1 is default
				if (!constraint.perimeter)
				{
					this.setCellStyles((source) ? mxConstants.STYLE_EXIT_PERIMETER :
						mxConstants.STYLE_ENTRY_PERIMETER, '0', [edge]);
                    // 若禁用周界约束，显式设置为 0。
				}
				else
				{
					this.setCellStyles((source) ? mxConstants.STYLE_EXIT_PERIMETER :
						mxConstants.STYLE_ENTRY_PERIMETER, null, [edge]);
                    // 若启用周界约束，移除显式设置（默认值为 1）。
				}
			}
		}
		finally
		{
			this.model.endUpdate();
            // 结束模型更新事务。
		}
	}
};

/**
 * Function: getConnectionPoint
 *
 * Returns the nearest point in the list of absolute points or the center
 * of the opposite terminal.
 * 
 * Parameters:
 * 
 * vertex - <mxCellState> that represents the vertex.
 * constraint - <mxConnectionConstraint> that represents the connection point
 * constraint as returned by <getConnectionConstraint>.
 */
// 方法目的：返回最接近的绝对连接点或对端终端的中心点。
// 参数说明：vertex（顶点状态），constraint（连接约束），round（是否四舍五入，默认为 true）。
// 关键变量说明：point（计算得到的连接点），bounds（顶点边界），cx（边界中心点）。
mxGraph.prototype.getConnectionPoint = function(vertex, constraint, round)
{
	round = (round != null) ? round : true;
    // 设置是否四舍五入连接点坐标，默认为 true。
	var point = null;
	
	if (vertex != null && constraint.point != null)
	{
		var bounds = this.view.getPerimeterBounds(vertex);
        var cx = new mxPoint(bounds.getCenterX(), bounds.getCenterY());
		var direction = vertex.style[mxConstants.STYLE_DIRECTION];
		var r1 = 0;
        // 获取顶点边界、中心点和方向样式。
        // 重要配置参数：STYLE_DIRECTION（顶点方向）。

		// Bounds need to be rotated by 90 degrees for further computation
		if (direction != null && mxUtils.getValue(vertex.style,
			mxConstants.STYLE_ANCHOR_POINT_DIRECTION, 1) == 1)
		{
			if (direction == mxConstants.DIRECTION_NORTH)
			{
				r1 += 270;
			}
			else if (direction == mxConstants.DIRECTION_WEST)
			{
				r1 += 180;
			}
			else if (direction == mxConstants.DIRECTION_SOUTH)
			{
				r1 += 90;
			}
            // 根据方向调整旋转角度（单位：度）。
            // 重要配置参数：STYLE_ANCHOR_POINT_DIRECTION（是否按方向旋转锚点，默认为 1）。

			// Bounds need to be rotated by 90 degrees for further computation
			if (direction == mxConstants.DIRECTION_NORTH ||
				direction == mxConstants.DIRECTION_SOUTH)
			{
				bounds.rotate90();
                // 若方向为南北，旋转边界框 90 度。
			}
		}

		var scale = this.view.scale;
        // 获取视图的缩放比例。
		point = new mxPoint(bounds.x + constraint.point.x * bounds.width + constraint.dx * scale,
				bounds.y + constraint.point.y * bounds.height + constraint.dy * scale);
        // 根据约束点和偏移量计算绝对连接点坐标，考虑缩放比例。

		// Rotation for direction before projection on perimeter
		var r2 = vertex.style[mxConstants.STYLE_ROTATION] || 0;
        // 获取顶点的旋转角度。
        // 重要配置参数：STYLE_ROTATION（顶点旋转角度，默认为 0）。

		if (constraint.perimeter)
		{
			if (r1 != 0)
			{
				// Only 90 degrees steps possible here so no trig needed
				var cos = 0;
				var sin = 0;
				
				if (r1 == 90)
				{
					sin = 1;
				}
				else if (r1 == 180)
				{
					cos = -1;
				}
				else if (r1 == 270)
				{
					sin = -1;
				}
                // 根据方向旋转角度设置余弦和正弦值（仅支持 90 度步进）。
		        point = mxUtils.getRotatedPoint(point, cos, sin, cx);
                // 对连接点进行旋转变换。
			}
	
			point = this.view.getPerimeterPoint(vertex, point, false);
            // 若启用周界约束，将连接点投影到顶点周界上。
		}
		else
		{
			r2 += r1;
            // 累加方向旋转角度和顶点旋转角度。

			if (this.getModel().isVertex(vertex.cell))
			{
				var flipH = vertex.style[mxConstants.STYLE_FLIPH] == 1;
				var flipV = vertex.style[mxConstants.STYLE_FLIPV] == 1;
                // 获取顶点的水平和垂直翻转样式。
                // 重要配置参数：STYLE_FLIPH/V（水平/垂直翻转）。

				// Legacy support for stencilFlipH/V
				if (vertex.shape != null && vertex.shape.stencil != null)
				{
					flipH = (mxUtils.getValue(vertex.style, 'stencilFlipH', 0) == 1) || flipH;
					flipV = (mxUtils.getValue(vertex.style, 'stencilFlipV', 0) == 1) || flipV;
                    // 支持旧版模板翻转样式。
				}
				
				if (direction == mxConstants.DIRECTION_NORTH ||
					direction == mxConstants.DIRECTION_SOUTH)
				{
					var temp = flipH;
					flipH = flipV
					flipV = temp;
                    // 若方向为南北，交换翻转设置。
				}
				
				if (flipH)
				{
					point.x = 2 * bounds.getCenterX() - point.x;
                    // 若水平翻转，调整 x 坐标。
				}
				
				if (flipV)
				{
					point.y = 2 * bounds.getCenterY() - point.y;
                    // 若垂直翻转，调整 y 坐标。
				}
			}
		}

		// Generic rotation after projection on perimeter
		if (r2 != 0 && point != null)
		{
	        var rad = mxUtils.toRadians(r2);
	        var cos = Math.cos(rad);
	        var sin = Math.sin(rad);
	        
	        point = mxUtils.getRotatedPoint(point, cos, sin, cx);
            // 若存在旋转角度，对连接点进行通用旋转变换。
		}
	}
	
	if (round && point != null)
	{
		point.x = Math.round(point.x);
		point.y = Math.round(point.y);
        // 若启用四舍五入，调整连接点坐标为整数。
	}

	return point;
    // 返回计算得到的连接点。
};

/**
 * Function: connectCell
 * 
 * Connects the specified end of the given edge to the given terminal
 * using <cellConnected> and fires <mxEvent.CONNECT_CELL> while the
 * transaction is in progress. Returns the updated edge.
 * 
 * Parameters:
 * 
 * edge - <mxCell> whose terminal should be updated.
 * terminal - <mxCell> that represents the new terminal to be used.
 * source - Boolean indicating if the new terminal is the source or target.
 * constraint - Optional <mxConnectionConstraint> to be used for this
 * connection.
 */
// 方法目的：将指定边的指定端连接到新终端，使用 cellConnected 方法并触发 CONNECT_CELL 事件。
// 参数说明：edge（待更新的边），terminal（新终端），source（是否为源终端），constraint（可选的连接约束）。
// 事件处理逻辑：触发 mxEvent.CONNECT_CELL 事件，记录连接变更。
mxGraph.prototype.connectCell = function(edge, terminal, source, constraint)
{
	this.model.beginUpdate();
    // 开始模型更新事务。
	try
	{
		var previous = this.model.getTerminal(edge, source);
        // 保存原终端信息。
		this.cellConnected(edge, terminal, source, constraint);
        // 调用 cellConnected 方法更新边终端。
		this.fireEvent(new mxEventObject(mxEvent.CONNECT_CELL,
			'edge', edge, 'terminal', terminal, 'source', source,
			'previous', previous));
        // 触发 CONNECT_CELL 事件，传递边、新终端、源标志和旧终端信息。
	}
	finally
	{
		this.model.endUpdate();
        // 结束模型更新事务。
	}

	return edge;
    // 返回更新后的边。
};

/**
 * Function: cellConnected
 * 
 * Sets the new terminal for the given edge and resets the edge points if
 * <resetEdgesOnConnect> is true. This method fires
 * <mxEvent.CELL_CONNECTED> while the transaction is in progress.
 * 
 * Parameters:
 * 
 * edge - <mxCell> whose terminal should be updated.
 * terminal - <mxCell> that represents the new terminal to be used.
 * source - Boolean indicating if the new terminal is the source or target.
 * constraint - <mxConnectionConstraint> to be used for this connection.
 */
// 方法目的：设置边的指定终端，并根据 resetEdgesOnConnect 重置边控制点，触发 CELL_CONNECTED 事件。
// 参数说明：edge（待更新的边），terminal（新终端），source（是否为源终端），constraint（连接约束）。
// 事件处理逻辑：触发 mxEvent.CELL_CONNECTED 事件，记录终端变更。
// 关键变量说明：resetEdgesOnConnect（是否在连接时重置边控制点）。
mxGraph.prototype.cellConnected = function(edge, terminal, source, constraint)
{
	if (edge != null)
	{
		this.model.beginUpdate();
        // 开始模型更新事务。
		try
		{
			var previous = this.model.getTerminal(edge, source);
            // 保存原终端信息。

			// Updates the constraint
			this.setConnectionConstraint(edge, terminal, source, constraint);
            // 设置连接约束。

			// Checks if the new terminal is a port, uses the ID of the port in the
			// style and the parent of the port as the actual terminal of the edge.
			if (this.isPortsEnabled())
			{
				var id = null;
                // 初始化端口 ID。

				if (this.isPort(terminal))
				{
					id = terminal.getId();
					terminal = this.getTerminalForPort(terminal, source);
                    // 若终端是端口，获取端口 ID 并使用其父节点作为实际终端。
				}
				
				// Sets or resets all previous information for connecting to a child port
				var key = (source) ? mxConstants.STYLE_SOURCE_PORT :
					mxConstants.STYLE_TARGET_PORT;
				this.setCellStyles(key, id, [edge]);
                // 设置或重置端口样式。
                // 重要配置参数：STYLE_SOURCE_PORT/TARGET_PORT（源/目标端口 ID）。
			}
			
			this.model.setTerminal(edge, terminal, source);
            // 更新边的终端。

			if (this.resetEdgesOnConnect)
			{
				this.resetEdge(edge);
                // 若启用 resetEdgesOnConnect，重置边控制点。
			}

			this.fireEvent(new mxEventObject(mxEvent.CELL_CONNECTED,
				'edge', edge, 'terminal', terminal, 'source', source,
				'previous', previous));
            // 触发 CELL_CONNECTED 事件，传递边、新终端、源标志和旧终端信息。
		}
		finally
		{
			this.model.endUpdate();
            // 结束模型更新事务。
		}
	}
};

/**
 * Function: disconnectGraph
 * 
 * Disconnects the given edges from the terminals which are not in the
 * given array.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to be disconnected.
 */
// 方法目的：断开与不在指定单元格数组中的终端连接的边。
// 参数说明：cells（需要断开连接的单元格数组）。
mxGraph.prototype.disconnectGraph = function(cells)
{
	if (cells != null)
	{
		this.model.beginUpdate();
        // 开始模型更新事务。
		try
		{							
			var scale = this.view.scale;
			var tr = this.view.translate;
            // 获取视图的缩放比例和平移量。

			// Fast lookup for finding cells in array
			var dict = new mxDictionary();
            // 创建字典以加速单元格查找。

			for (var i = 0; i < cells.length; i++)
			{
				dict.put(cells[i], true);
                // 将单元格添加到字典中，标记为存在。
			}
			
			for (var i = 0; i < cells.length; i++)
			{
				if (this.model.isEdge(cells[i]))
				{
					var geo = this.model.getGeometry(cells[i]);
                    // 获取边的几何信息。

					if (geo != null)
					{
						var state = this.view.getState(cells[i]);
						var pstate = this.view.getState(
							this.model.getParent(cells[i]));
                        // 获取边及其父节点的状态。

						if (state != null &&
							pstate != null)
						{
							geo = geo.clone();
                            // 克隆几何对象，防止直接修改原始数据。

							var dx = -pstate.origin.x;
							var dy = -pstate.origin.y;
							var pts = state.absolutePoints;
                            // 计算父节点的相对偏移量和边的绝对点数组。

							var src = this.model.getTerminal(cells[i], true);
                            // 获取边的源终端。

							if (src != null && this.isCellDisconnectable(cells[i], src, true))
							{
								while (src != null && !dict.get(src))
								{
									src = this.model.getParent(src);
                                    // 查找源终端的祖先，直到找到在给定数组中的终端或 null。
								}
								
								if (src == null)
								{
									geo.setTerminalPoint(
										new mxPoint(pts[0].x / scale - tr.x + dx,
											pts[0].y / scale - tr.y + dy), true);
									this.model.setTerminal(cells[i], null, true);
                                    // 若源终端不在数组中，设置边的源终端点并断开连接。
								}
							}
							
							var trg = this.model.getTerminal(cells[i], false);
                            // 获取边的目标终端。

							if (trg != null && this.isCellDisconnectable(cells[i], trg, false))
							{
								while (trg != null && !dict.get(trg))
								{
									trg = this.model.getParent(trg);
                                    // 查找目标终端的祖先，直到找到在给定数组中的终端或 null。
								}
								
								if (trg == null)
								{
									var n = pts.length - 1;
									geo.setTerminalPoint(
										new mxPoint(pts[n].x / scale - tr.x + dx,
											pts[n].y / scale - tr.y + dy), false);
									this.model.setTerminal(cells[i], null, false);
                                    // 若目标终端不在数组中，设置边的目标终端点并断开连接。
								}
							}

							this.model.setGeometry(cells[i], geo);
                            // 更新边的几何信息。
						}
					}
				}
			}
		}
		finally
		{
			this.model.endUpdate();
            // 结束模型更新事务。
		}
	}
};

/**
 * Group: Drilldown
 */

/**
 * Function: getCurrentRoot
 * 
 * Returns the current root of the displayed cell hierarchy. This is a
 * shortcut to <mxGraphView.currentRoot> in <view>.
 */
// 方法目的：返回当前显示的单元格层次结构的根节点。
mxGraph.prototype.getCurrentRoot = function()
{
	return this.view.currentRoot;
    // 直接返回视图的当前根节点。
};
 
/**
 * Function: getTranslateForRoot
 * 
 * Returns the translation to be used if the given cell is the root cell as
 * an <mxPoint>. This implementation returns null.
 * 
 * Example:
 * 
 * To keep the children at their absolute position while stepping into groups,
 * this function can be overridden as follows.
 * 
 * (code)
 * var offset = new mxPoint(0, 0);
 * 
 * while (cell != null)
 * {
 *   var geo = this.model.getGeometry(cell);
 * 
 *   if (geo != null)
 *   {
 *     offset.x -= geo.x;
 *     offset.y -= geo.y;
 *   }
 * 
 *   cell = this.model.getParent(cell);
 * }
 * 
 * return offset;
 * (end)
 * 
 * Parameters:
 * 
 * cell - <mxCell> that represents the root.
 */
// 方法目的：返回作为根节点的单元格所需的平移量（mxPoint），默认实现返回 null。
// 参数说明：cell（根节点单元格）。
// 特殊处理注意事项：可重写此方法以保持子节点的绝对位置。
mxGraph.prototype.getTranslateForRoot = function(cell)
{
	return null;
    // 默认返回 null，未定义平移量。
};

/**
 * Function: isPort
 * 
 * Returns true if the given cell is a "port", that is, when connecting to
 * it, the cell returned by getTerminalForPort should be used as the
 * terminal and the port should be referenced by the ID in either the
 * mxConstants.STYLE_SOURCE_PORT or the or the
 * mxConstants.STYLE_TARGET_PORT. Note that a port should not be movable.
 * This implementation always returns false.
 * 
 * A typical implementation is the following:
 * 
 * (code)
 * graph.isPort = function(cell)
 * {
 *   var geo = this.getCellGeometry(cell);
 *   
 *   return (geo != null) ? geo.relative : false;
 * };
 * (end)
 * 
 * Parameters:
 * 
 * cell - <mxCell> that represents the port.
 */
// 方法目的：判断指定单元格是否为端口（连接时使用其父节点作为终端）。
// 参数说明：cell（待判断的单元格）。
// 特殊处理注意事项：端口不可移动，默认实现始终返回 false，可重写以支持相对定位判断。
mxGraph.prototype.isPort = function(cell)
{
	return false;
    // 默认实现，始终认为单元格不是端口。
};

/**
 * Function: getTerminalForPort
 * 
 * Returns the terminal to be used for a given port. This implementation
 * always returns the parent cell.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that represents the port.
 * source - If the cell is the source or target port.
 */
// 方法目的：返回指定端口的实际终端，默认实现返回父节点。
// 参数说明：cell（端口单元格），source（是否为源端口）。
mxGraph.prototype.getTerminalForPort = function(cell, source)
{
	return this.model.getParent(cell);
    // 默认返回端口的父节点作为终端。
};

/**
 * Function: getChildOffsetForCell
 * 
 * Returns the offset to be used for the cells inside the given cell. The
 * root and layer cells may be identified using <mxGraphModel.isRoot> and
 * <mxGraphModel.isLayer>. For all other current roots, the
 * <mxGraphView.currentRoot> field points to the respective cell, so that
 * the following holds: cell == this.view.currentRoot. This implementation
 * returns null.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose offset should be returned.
 */
// 方法目的：返回指定单元格内子节点的偏移量，默认实现返回 null。
// 参数说明：cell（需要获取偏移量的单元格）。
// 特殊处理注意事项：可用于根节点或层节点的偏移计算，需配合 mxGraphModel.isRoot 和 isLayer 判断。
mxGraph.prototype.getChildOffsetForCell = function(cell)
{
	return null;
    // 默认返回 null，未定义偏移量。
};

/**
 * Function: enterGroup
 * 
 * Uses the given cell as the root of the displayed cell hierarchy. If no
 * cell is specified then the selection cell is used. The cell is only used
 * if <isValidRoot> returns true.
 * 
 * Parameters:
 * 
 * cell - Optional <mxCell> to be used as the new root. Default is the
 * selection cell.
 */
// 方法目的：将指定单元格设置为显示的单元格层次结构的根节点，若未指定则使用选中的单元格。
// 参数说明：cell（可选的新根节点，默认为选中单元格）。
// 交互逻辑：仅当 isValidRoot 返回 true 时更新根节点，并清除当前选择。
mxGraph.prototype.enterGroup = function(cell)
{
	cell = cell || this.getSelectionCell();
    // 获取指定单元格或默认选中单元格。

	if (cell != null && this.isValidRoot(cell))
	{
		this.view.setCurrentRoot(cell);
        // 设置视图的当前根节点。
		this.clearSelection();
        // 清除当前选择。
	}
};

/**
 * Function: exitGroup
 * 
 * Changes the current root to the next valid root in the displayed cell
 * hierarchy.
 */
// 方法目的：将当前根节点更改为显示层次结构中的下一个有效根节点。
// 交互逻辑：查找父节点直到找到有效根节点或模型根节点，更新视图并选择原根节点。
mxGraph.prototype.exitGroup = function()
{
	var root = this.model.getRoot();
	var current = this.getCurrentRoot();
    // 获取模型根节点和当前根节点。

	if (current != null)
	{
		var next = this.model.getParent(current);
        // 获取当前根节点的父节点。

		// Finds the next valid root in the hierarchy
		while (next != root && !this.isValidRoot(next) &&
				this.model.getParent(next) != root)
		{
			next = this.model.getParent(next);
            // 循环查找直到找到有效根节点或模型根节点的父节点。
		}
		
		// Clears the current root if the new root is
		// the model's root or one of the layers.
		if (next == root || this.model.getParent(next) == root)
		{
			this.view.setCurrentRoot(null);
            // 若新根节点是模型根节点或其子节点，清除当前根节点。
		}
		else
		{
			this.view.setCurrentRoot(next);
            // 否则将视图根节点设置为新根节点。
		}
		
		var state = this.view.getState(current);
        // 获取当前根节点的状态。

		// Selects the previous root in the graph
		if (state != null)
		{
			this.setSelectionCell(current);
            // 选择原根节点。
		}
	}
};

/**
 * Function: home
 * 
 * Uses the root of the model as the root of the displayed cell hierarchy
 * and selects the previous root.
 */
// 方法目的：将模型根节点设置为显示层次结构的根节点，并选择之前的根节点。
// 交互逻辑：清除当前根节点并恢复选择。
mxGraph.prototype.home = function()
{
	var current = this.getCurrentRoot();
    // 获取当前根节点。

	if (current != null)
	{
		this.view.setCurrentRoot(null);
        // 清除当前根节点。
		var state = this.view.getState(current);
        // 获取当前根节点的状态。

		if (state != null)
		{
			this.setSelectionCell(current);
            // 选择原根节点。
		}
	}
};

/**
 * Function: isValidRoot
 * 
 * Returns true if the given cell is a valid root for the cell display
 * hierarchy. This implementation returns true for all non-null values.
 * 
 * Parameters:
 * 
 * cell - <mxCell> which should be checked as a possible root.
 */
// 方法目的：判断指定单元格是否为有效的显示层次结构根节点。
// 参数说明：cell（待检查的单元格）。
// 特殊处理注意事项：默认实现仅检查单元格是否非空，可重写以添加更多验证逻辑。
mxGraph.prototype.isValidRoot = function(cell)
{
	return (cell != null);
    // 默认实现，非空单元格即为有效根节点。
};

/**
 * Group: Graph display
 */
 
/**
 * Function: getGraphBounds
 * 
 * Returns the bounds of the visible graph. Shortcut to
 * <mxGraphView.getGraphBounds>. See also: <getBoundingBoxFromGeometry>.
 */
// 方法目的：返回可见图表的边界，是 mxGraphView.getGraphBounds 的快捷方式。
 mxGraph.prototype.getGraphBounds = function()
 {
 	return this.view.getGraphBounds();
    // 直接调用视图的 getGraphBounds 方法返回图表边界。
 };

/**
 * Function: getCellBounds
 * 
 * Returns the scaled, translated bounds for the given cell. See
 * <mxGraphView.getBounds> for arrays.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose bounds should be returned.
 * includeEdge - Optional boolean that specifies if the bounds of
 * the connected edges should be included. Default is false.
 * includeDescendants - Optional boolean that specifies if the bounds
 * of all descendants should be included. Default is false.
 */
// 方法目的：返回指定单元格的缩放和平移后的边界。
// 参数说明：cell（需要获取边界的单元格），includeEdge（是否包含连接边，默认为 false），
// includeDescendants（是否包含所有后代，默认为 false）。
mxGraph.prototype.getCellBounds = function(cell, includeEdges, includeDescendants)
{
	var cells = [cell];
    // 初始化包含当前单元格的数组。

	// Includes all connected edges
	if (includeEdges)
	{
		cells = cells.concat(this.model.getEdges(cell));
        // 若包含连接边，将与单元格连接的边添加到数组。
	}
	
	var result = this.view.getBounds(cells);
    // 获取单元格数组的边界。

	// Recursively includes the bounds of the children
	if (includeDescendants)
	{
		var childCount = this.model.getChildCount(cell);
        // 获取子节点数量。

		for (var i = 0; i < childCount; i++)
		{
			var tmp = this.getCellBounds(this.model.getChildAt(cell, i),
				includeEdges, true);
            // 递归获取每个子节点的边界。
			if (result != null)
			{
				result.add(tmp);
                // 将子节点边界合并到结果中。
			}
			else
			{
				result = tmp;
                // 若结果为空，直接使用子节点边界。
			}
		}
	}
	
	return result;
    // 返回最终的边界。
};

/**
 * Function: getBoundingBoxFromGeometry
 * 
 * Returns the bounding box for the geometries of the vertices in the
 * given array of cells. This can be used to find the graph bounds during
 * a layout operation (ie. before the last endUpdate) as follows:
 * 
 * (code)
 * var cells = graph.getChildCells(graph.getDefaultParent(), true, true);
 * var bounds = graph.getBoundingBoxFromGeometry(cells, true);
 * (end)
 * 
 * This can then be used to move cells to the origin:
 * 
 * (code)
 * if (bounds.x < 0 || bounds.y < 0)
 * {
 *   graph.moveCells(cells, -Math.min(bounds.x, 0), -Math.min(bounds.y, 0))
 * }
 * (end)
 * 
 * Or to translate the graph view:
 * 
 * (code)
 * if (bounds.x < 0 || bounds.y < 0)
 * {
 *   graph.view.setTranslate(-Math.min(bounds.x, 0), -Math.min(bounds.y, 0));
 * }
 * (end)
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> whose bounds should be returned.
 * includeEdges - Specifies if edge bounds should be included by computing
 * the bounding box for all points in geometry. Default is false.
 */
// 方法目的：返回指定单元格数组的几何边界框，可用于布局操作中确定图表边界。
// 参数说明：cells（单元格数组），includeEdges（是否包含边边界，默认为 false）。
// 特殊处理注意事项：可用于移动单元格到原点或平移视图。
mxGraph.prototype.getBoundingBoxFromGeometry = function(cells, includeEdges)
{
	includeEdges = (includeEdges != null) ? includeEdges : false;
    // 设置是否包含边边界，默认为 false。
	var result = null;
	
	if (cells != null)
	{
		for (var i = 0; i < cells.length; i++)
		{
			if (includeEdges || this.model.isVertex(cells[i]))
			{
				// Computes the bounding box for the points in the geometry
				var geo = this.getCellGeometry(cells[i]);
                // 获取单元格的几何信息。

				if (geo != null)
				{
					var bbox = null;
					
					if (this.model.isEdge(cells[i]))
					{
						var addPoint = function(pt)
						{
							if (pt != null)
							{
								if (tmp == null)
								{
									tmp = new mxRectangle(pt.x, pt.y, 0, 0);
								}
								else
								{
									tmp.add(new mxRectangle(pt.x, pt.y, 0, 0));
								}
							}
						};
                        // 定义辅助函数，添加点到边界框。

						if (this.model.getTerminal(cells[i], true) == null)
						{
							addPoint(geo.getTerminalPoint(true));
                            // 若源终端为空，添加源终端点到边界框。
						}
						
						if (this.model.getTerminal(cells[i], false) == null)
						{
							addPoint(geo.getTerminalPoint(false));
                            // 若目标终端为空，添加目标终端点到边界框。
						}
												
						var pts = geo.points;
                        // 获取边的控制点数组。

						if (pts != null && pts.length > 0)
						{
							var tmp = new mxRectangle(pts[0].x, pts[0].y, 0, 0);
                            // 初始化边界框为第一个控制点。
							for (var j = 1; j < pts.length; j++)
							{
								addPoint(pts[j]);
                                // 添加其他控制点到边界框。
							}
						}
						
						bbox = tmp;
                        // 设置边的边界框。
					}
					else
					{
						var parent = this.model.getParent(cells[i]);
                        // 获取单元格的父节点。

						if (geo.relative)
						{
							if (this.model.isVertex(parent) && parent != this.view.currentRoot)
							{
								var tmp = this.getBoundingBoxFromGeometry([parent], false);
                                // 获取父节点的边界框。

								if (tmp != null)
								{
									bbox = new mxRectangle(geo.x * tmp.width, geo.y * tmp.height, geo.width, geo.height);
                                    // 计算相对定位的边界框。

									if (mxUtils.indexOf(cells, parent) >= 0)
									{
										bbox.x += tmp.x;
										bbox.y += tmp.y;
                                        // 若父节点在数组中，调整边界框坐标。
									}
								}
							}
						}
						else
						{
							bbox = mxRectangle.fromRectangle(geo);
                            // 使用绝对定位的几何信息创建边界框。

							if (this.model.isVertex(parent) && mxUtils.indexOf(cells, parent) >= 0)
							{
								var tmp = this.getBoundingBoxFromGeometry([parent], false);
                                // 获取父节点的边界框。
								if (tmp != null)
								{
									bbox.x += tmp.x;
									bbox.y += tmp.y;
                                    // 调整边界框坐标，考虑父节点位置。
								}
							}
						}
						
						if (bbox != null && geo.offset != null)
						{
							bbox.x += geo.offset.x;
							bbox.y += geo.offset.y;
                            // 应用偏移量调整边界框。
						}

						var style = this.getCurrentCellStyle(cells[i]);
                        // 获取单元格的当前样式。

						if (bbox != null)
						{
							var angle = mxUtils.getValue(style, mxConstants.STYLE_ROTATION, 0);
                            // 获取旋转角度。
                            // 重要配置参数：STYLE_ROTATION（旋转角度，默认为 0）。
							if (angle != 0)
							{
								bbox = mxUtils.getBoundingBox(bbox, angle);
                                // 若存在旋转角度，旋转边界框。
							}
						}
					}
					
					if (bbox != null)
					{
						if (result == null)
						{
							result = mxRectangle.fromRectangle(bbox);
                            // 初始化结果边界框。
						}
						else
						{
							result.add(bbox);
                            // 合并当前边界框到结果中。
						}
					}
				}
			}
		}
	}
	
	return result;
    // 返回最终的边界框。
};

/**
 * Function: refresh
 * 
 * Clears all cell states or the states for the hierarchy starting at the
 * given cell and validates the graph. This fires a refresh event as the
 * last step.
 * 
 * Parameters:
 * 
 * cell - Optional <mxCell> for which the cell states should be cleared.
 */
// 方法目的：清除指定单元格或其层次结构的单元格状态，验证图表并触发刷新事件。
// 参数说明：cell（可选的单元格，指定清除状态的起点）。
// 事件处理逻辑：触发 mxEvent.REFRESH 事件，通知图表刷新。
mxGraph.prototype.refresh = function(cell)
{
	this.view.clear(cell, cell == null);
    // 清除指定单元格或整个视图的状态。
	this.view.validate();
    // 验证图表视图，确保状态正确。
	this.sizeDidChange();
    // 更新图表尺寸。
	this.fireEvent(new mxEventObject(mxEvent.REFRESH));
    // 触发 REFRESH 事件，通知图表已刷新。
};

/**
 * Function: snap
 * 
 * Snaps the given numeric value to the grid if <gridEnabled> is true.
 * 
 * Parameters:
 * 
 * value - Numeric value to be snapped to the grid.
 */
// 方法目的：若启用网格，将指定数值对齐到网格。
// 参数说明：value（需要对齐的数值）。
// 关键变量说明：gridEnabled（是否启用网格），gridSize（网格大小）。
mxGraph.prototype.snap = function(value)
{
	if (this.gridEnabled)
	{
		value = Math.round(value / this.gridSize ) * this.gridSize;
        // 若网格启用，将值四舍五入到网格大小的倍数。
	}
	
	return value;
    // 返回对齐后的值。
};

/**
 * Function: snapDelta
 * 
 * Snaps the given delta with the given scaled bounds.
 */
// 方法目的：将指定增量对齐到网格，考虑缩放边界。
// 参数说明：delta（增量），bounds（缩放边界），ignoreGrid（是否忽略网格），
// ignoreHorizontal（是否忽略水平对齐），ignoreVertical（是否忽略垂直对齐）。
mxGraph.prototype.snapDelta = function(delta, bounds, ignoreGrid, ignoreHorizontal, ignoreVertical)
{
	var t = this.view.translate;
	var s = this.view.scale;
    // 获取视图的平移量和缩放比例。

	if (!ignoreGrid && this.gridEnabled)
	{
		var tol = this.gridSize * s * 0.5;
        // 计算网格对齐的容差，基于网格大小和缩放比例。

		if (!ignoreHorizontal)
		{
			var tx = bounds.x - (this.snap(bounds.x / s - t.x) + t.x) * s;
            // 计算水平对齐偏移。

			if (Math.abs(delta.x- tx) < tol)
			{
				delta.x = 0;
                // 若增量接近对齐点，设为 0。
			}
			else
			{
				delta.x = this.snap(delta.x / s) * s - tx;
                // 否则对增量进行网格对齐并调整偏移。
			}
		}
		
		if (!ignoreVertical)
		{
			var ty = bounds.y - (this.snap(bounds.y / s - t.y) + t.y) * s;
            // 计算垂直对齐偏移。

			if (Math.abs(delta.y - ty) < tol)
			{
				delta.y = 0;
                // 若增量接近对齐点，设为 0。
			}
			else
			{
				delta.y = this.snap(delta.y / s) * s - ty;
                // 否则对增量进行网格对齐并调整偏移。
			}
		}
	}
	else
	{
		var tol = 0.5 * s;
        // 若禁用网格，使用缩放比例计算容差。

		if (!ignoreHorizontal)
		{
			var tx = bounds.x - (Math.round(bounds.x / s - t.x) + t.x) * s;
            // 计算水平对齐偏移。

			if (Math.abs(delta.x - tx) < tol)
			{
				delta.x = 0;
                // 若增量接近对齐点，设为 0。
			}
			else
			{
				delta.x = Math.round(delta.x / s) * s - tx;
                // 否则对增量进行四舍五入并调整偏移。
			}
		}
		
		if (!ignoreVertical)
		{		
			var ty = bounds.y - (Math.round(bounds.y / s - t.y) + t.y) * s;
            // 计算垂直对齐偏移。

			if (Math.abs(delta.y - ty) < tol)
			{
				delta.y = 0;
                // 若增量接近对齐点，设为 0。
			}
			else
			{
				delta.y = Math.round(delta.y / s) * s - ty;
                // 否则对增量进行四舍五入并调整偏移。
			}
		}
	}
	
	return delta;
    // 返回对齐后的增量。
};

/**
 * Function: panGraph
 * 
 * Shifts the graph display by the given amount. This is used to preview
 * panning operations, use <mxGraphView.setTranslate> to set a persistent
 * translation of the view. Fires <mxEvent.PAN>.
 * 
 * Parameters:
 * 
 * dx - Amount to shift the graph along the x-axis.
 * dy - Amount to shift the graph along the y-axis.
 */
// 方法目的：将图表显示平移指定量，用于预览平移操作，触发 PAN 事件。
// 参数说明：dx（x 轴平移量），dy（y 轴平移量）。
// 事件处理逻辑：触发 mxEvent.PAN 事件，通知图表平移。
// 关键变量说明：useScrollbarsForPanning（是否使用滚动条平移）。
mxGraph.prototype.panGraph = function(dx, dy)
{
	if (this.useScrollbarsForPanning && mxUtils.hasScrollbars(this.container))
	{
		this.container.scrollLeft = -dx;
		this.container.scrollTop = -dy;
        // 若启用滚动条平移，调整容器滚动位置。
	}
	else
	{
		var canvas = this.view.getCanvas();
        // 获取视图的画布。

		if (this.dialect == mxConstants.DIALECT_SVG)
		{
			// Puts everything inside the container in a DIV so that it
			// can be moved without changing the state of the container
			if (dx == 0 && dy == 0)
			{
				// Workaround for ignored removeAttribute on SVG element in IE9 standards
				if (mxClient.IS_IE)
				{
					canvas.setAttribute('transform', 'translate(' + dx + ',' + dy + ')');
				}
				else
				{
					canvas.removeAttribute('transform');
                    // 若平移量为 0，移除 SVG 变换属性。
				}
				
				if (this.shiftPreview1 != null)
				{
					var child = this.shiftPreview1.firstChild;
                    // 获取第一个预览 DIV 的子节点。

					while (child != null)
					{
						var next = child.nextSibling;
						this.container.appendChild(child);
						child = next;
                        // 将预览 DIV 的子节点移回容器。
					}

					if (this.shiftPreview1.parentNode != null)
					{
						this.shiftPreview1.parentNode.removeChild(this.shiftPreview1);
                        // 移除第一个预览 DIV。
					}
					
					this.shiftPreview1 = null;
                    // 清空第一个预览 DIV。

					this.container.appendChild(canvas.parentNode);
                    // 将画布的父节点添加回容器。

					child = this.shiftPreview2.firstChild;
                    // 获取第二个预览 DIV 的子节点。

					while (child != null)
					{
						var next = child.nextSibling;
						this.container.appendChild(child);
						child = next;
                        // 将预览 DIV 的子节点移回容器。
					}

					if (this.shiftPreview2.parentNode != null)
					{
						this.shiftPreview2.parentNode.removeChild(this.shiftPreview2);
                        // 移除第二个预览 DIV。
					}
					
					this.shiftPreview2 = null;
                    // 清空第二个预览 DIV。
				}
			}
			else
			{
				canvas.setAttribute('transform', 'translate(' + dx + ',' + dy + ')');
                // 设置 SVG 画布的平移变换。

				if (this.shiftPreview1 == null)
				{
					// Needs two divs for stuff before and after the SVG element
					this.shiftPreview1 = document.createElement('div');
					this.shiftPreview1.style.position = 'absolute';
					this.shiftPreview1.style.overflow = 'visible';
                    // 创建第一个预览 DIV，用于存放 SVG 元素前的节点。
                    // 样式设置说明：绝对定位，允许溢出。

					this.shiftPreview2 = document.createElement('div');
					this.shiftPreview2.style.position = 'absolute';
					this.shiftPreview2.style.overflow = 'visible';
                    // 创建第二个预览 DIV，用于存放 SVG 元素后的节点。
                    // 样式设置说明：绝对定位，允许溢出。

					var current = this.shiftPreview1;
					var child = this.container.firstChild;
                    // 初始化当前预览 DIV 和容器子节点。

					while (child != null)
					{
						var next = child.nextSibling;
                        // 获取下一个子节点。

						// SVG element is moved via transform attribute
						if (child != canvas.parentNode)
						{
							current.appendChild(child);
                            // 若不是 SVG 元素，将子节点添加到当前预览 DIV。
						}
						else
						{
							current = this.shiftPreview2;
                            // 切换到第二个预览 DIV。
						}
						
						child = next;
                        // 继续处理下一个子节点。
					}
					
					// Inserts elements only if not empty
					if (this.shiftPreview1.firstChild != null)
					{
						this.container.insertBefore(this.shiftPreview1, canvas.parentNode);
                        // 若第一个预览 DIV 不为空，插入到画布父节点前。
					}
					
					if (this.shiftPreview2.firstChild != null)
					{
						this.container.appendChild(this.shiftPreview2);
                        // 若第二个预览 DIV 不为空，添加到容器末尾。
					}
				}
				
				this.shiftPreview1.style.left = dx + 'px';
				this.shiftPreview1.style.top = dy + 'px';
				this.shiftPreview2.style.left = dx + 'px';
				this.shiftPreview2.style.top = dy + 'px';
                // 设置预览 DIV 的平移位置。
                // 样式设置说明：通过 left 和 top 属性实现平移。
			}
		}
		else
		{
			canvas.style.left = dx + 'px';
			canvas.style.top = dy + 'px';
            // 非 SVG 模式下，直接设置画布的 left 和 top 属性。
            // 样式设置说明：通过 CSS 位置属性实现平移。
		}
		
		this.panDx = dx;
		this.panDy = dy;
        // 记录平移量。

		this.fireEvent(new mxEventObject(mxEvent.PAN));
        // 触发 PAN 事件，通知图表平移。
	}
};

/**
 * Function: zoomIn
 * 
 * Zooms into the graph by <zoomFactor>.
 */
// 方法目的：按 zoomFactor 放大图表。
// 关键变量说明：zoomFactor（缩放因子）。
mxGraph.prototype.zoomIn = function()
{
	this.zoom(this.zoomFactor);
    // 调用 zoom 方法，使用缩放因子放大。
};

/**
 * Function: zoomOut
 * 
 * Zooms out of the graph by <zoomFactor>.
 */
// 方法目的：按 zoomFactor 缩小图表。
// 关键变量说明：zoomFactor（缩放因子）。
mxGraph.prototype.zoomOut = function()
{
	this.zoom(1 / this.zoomFactor);
    // 调用 zoom 方法，使用倒数因子缩小。
};

/**
 * Function: zoomActual
 * 
 * Resets the zoom and panning in the view.
 */
// 方法目的：重置视图的缩放和平移。
// 交互逻辑：若当前缩放为 1，重置平移；否则重置平移和缩放比例。
mxGraph.prototype.zoomActual = function()
{
	if (this.view.scale == 1)
	{
		this.view.setTranslate(0, 0);
        // 若缩放比例为 1，仅重置平移量。
	}
	else
	{
		this.view.translate.x = 0;
		this.view.translate.y = 0;
        // 重置平移量。
		this.view.setScale(1);
        // 设置缩放比例为 1。
	}
};

/**
 * Function: zoomTo
 * 
 * Zooms the graph to the given scale with an optional boolean center
 * argument, which is passd to <zoom>.
 */
// 方法目的：将图表缩放到指定比例，可选择是否居中。
// 参数说明：scale（目标缩放比例），center（是否居中，传递给 zoom 方法）。
mxGraph.prototype.zoomTo = function(scale, center)
{
	this.zoom(scale / this.view.scale, center);
    // 调用 zoom 方法，计算相对缩放因子并传递居中参数。
};

/**
 * Function: center
 * 
 * Centers the graph in the container.
 * 
 * Parameters:
 * 
 * horizontal - Optional boolean that specifies if the graph should be centered
 * horizontally. Default is true.
 * vertical - Optional boolean that specifies if the graph should be centered
 * vertically. Default is true.
 * cx - Optional float that specifies the horizontal center. Default is 0.5.
 * cy - Optional float that specifies the vertical center. Default is 0.5.
 */
// 方法目的：将图表居中显示在容器中。
// 中文：将图形居中显示在容器内，根据参数控制水平和垂直居中。
// 参数说明：
//   horizontal - 可选布尔值，指定是否水平居中，默认为 true。
//   vertical - 可选布尔值，指定是否垂直居中，默认为 true。
//   cx - 可选浮点数，指定水平居中比例，默认为 0.5（中心点）。
//   cy - 可选浮点数，指定垂直居中比例，默认为 0.5（中心点）。
// 交互逻辑：根据容器尺寸和图表边界，调整视图平移以实现居中。
// 特殊处理：考虑容器是否有滚动条，分别处理无滚动条和有滚动条的情况。
mxGraph.prototype.center = function(horizontal, vertical, cx, cy)
{
	horizontal = (horizontal != null) ? horizontal : true;
	vertical = (vertical != null) ? vertical : true;
	cx = (cx != null) ? cx : 0.5;
	cy = (cy != null) ? cy : 0.5;
	// 设置默认值
	// 中文：为参数设置默认值，确保未提供时使用默认配置。

	var hasScrollbars = mxUtils.hasScrollbars(this.container);
	// 检查容器是否有滚动条
	// 中文：检测容器是否具有滚动条，影响居中逻辑。

	var padding = 2 * this.getBorder();
	// 获取容器边框的padding值
	// 中文：计算边框的padding（两倍边框宽度）。

	var cw = this.container.clientWidth - padding;
	var ch = this.container.clientHeight - padding;
	// 计算容器有效宽高
	// 中文：获取容器减去padding后的可用宽度和高度。

	var bounds = this.getGraphBounds();
	// 获取图表的边界
	// 中文：获取当前图表的边界信息，用于计算居中偏移量。

	var t = this.view.translate;
	var s = this.view.scale;
	// 获取视图的平移和缩放比例
	// 中文：获取当前视图的平移（translate）和缩放（scale）参数。

	var dx = (horizontal) ? cw - bounds.width : 0;
	var dy = (vertical) ? ch - bounds.height : 0;
	// 计算水平和垂直方向的偏移量
	// 中文：根据是否需要水平/垂直居中，计算容器与图表边界之间的差值。

	if (!hasScrollbars)
	{
		// 无滚动条时的居中处理
		// 中文：当容器没有滚动条时，直接调整视图平移实现居中。
		this.view.setTranslate((horizontal) ? Math.floor(t.x - bounds.x * s + dx * cx / s) : t.x,
			(vertical) ? Math.floor(t.y - bounds.y * s + dy * cy / s) : t.y);
		// 设置新的平移值
		// 中文：根据居中比例（cx, cy）调整视图平移，确保图表居中。
	}
	else
	{
		// 有滚动条时的居中处理
		// 中文：当容器有滚动条时，调整滚动条位置和视图平移。
		bounds.x -= t.x;
		bounds.y -= t.y;
		// 调整边界坐标，考虑当前平移
		// 中文：将图表边界坐标转换为相对于视图的坐标。

		var sw = this.container.scrollWidth;
		var sh = this.container.scrollHeight;
		// 获取容器的滚动宽高
		// 中文：获取容器的可滚动宽度和高度。

		if (sw > cw)
		{
			dx = 0;
			// 如果滚动宽度大于容器宽度，禁用水平偏移
			// 中文：当容器可滚动宽度大于有效宽度时，水平居中偏移设为0。
		}
		
		if (sh > ch)
		{
			dy = 0;
			// 如果滚动高度大于容器高度，禁用垂直偏移
			// 中文：当容器可滚动高度大于有效高度时，垂直居中偏移设为0。
		}

		this.view.setTranslate(Math.floor(dx / 2 - bounds.x), Math.floor(dy / 2 - bounds.y));
		// 设置视图平移
		// 中文：调整视图平移，使图表在容器中居中显示。

		this.container.scrollLeft = (sw - cw) / 2;
		this.container.scrollTop = (sh - ch) / 2;
		// 设置滚动条位置
		// 中文：将滚动条置于中间，确保图表居中显示。
	}
};

/**
 * Function: zoom
 * 
 * Zooms the graph using the given factor. Center is an optional boolean
 * argument that keeps the graph scrolled to the center. If the center argument
 * is omitted, then <centerZoom> will be used as its value.
 */
// 方法目的：缩放图表到指定比例。
// 中文：根据给定因子缩放图表，并可选择保持居中。
// 参数说明：
//   factor - 缩放因子，决定缩放比例。
//   center - 可选布尔值，指定是否保持图表居中，默认为 centerZoom 属性值。
// 交互逻辑：根据缩放因子调整视图比例，并根据是否居中调整平移或滚动条。
// 特殊处理：如果保持选中单元可见，优先调整以显示选中单元；否则根据容器是否有滚动条分别处理。
mxGraph.prototype.zoom = function(factor, center)
{
	center = (center != null) ? center : this.centerZoom;
	// 设置居中参数默认值
	// 中文：如果未提供 center 参数，使用默认的 centerZoom 属性。

	var scale = Math.round(this.view.scale * factor * 100) / 100;
	// 计算新的缩放比例
	// 中文：将当前缩放比例与因子相乘，四舍五入到两位小数。

	var state = this.view.getState(this.getSelectionCell());
	// 获取选中单元的状态
	// 中文：获取当前选中单元的状态信息，用于保持可见性。

	factor = scale / this.view.scale;
	// 计算实际缩放因子
	// 中文：计算新比例与当前比例的相对因子。

	if (this.keepSelectionVisibleOnZoom && state != null)
	{
		// 保持选中单元可见的缩放逻辑
		// 中文：当启用 keepSelectionVisibleOnZoom 且有选中单元时，优先确保选中单元可见。
		var rect = new mxRectangle(state.x * factor, state.y * factor,
			state.width * factor, state.height * factor);
		// 计算选中单元的新边界
		// 中文：根据缩放因子调整选中单元的坐标和尺寸。

		// Refreshes the display only once if a scroll is carried out
		this.view.scale = scale;
		// 设置新的缩放比例
		// 中文：更新视图的缩放比例。

		if (!this.scrollRectToVisible(rect))
		{
			// 如果不需要滚动，重新验证视图
			// 中文：如果调整后矩形区域不可见，调用 revalidate 刷新视图。
			this.view.revalidate();
			
			// Forces an event to be fired but does not revalidate again
			this.view.setScale(scale);
			// 设置缩放比例并触发事件
			// 中文：再次设置缩放比例以触发事件，避免重复验证。
		}
	}
	else
	{
		// 普通缩放逻辑
		// 中文：当不需保持选中单元可见时，执行普通缩放逻辑。
		var hasScrollbars = mxUtils.hasScrollbars(this.container);
		// 检查容器是否有滚动条
		// 中文：检测容器是否具有滚动条，决定缩放处理方式。

		if (center && !hasScrollbars)
		{
			// 无滚动条且需要居中的缩放
			// 中文：当需要居中且无滚动条时，调整视图平移以保持居中。
			var dx = this.container.offsetWidth;
			var dy = this.container.offsetHeight;
			// 获取容器宽高
			// 中文：获取容器的实际宽度和高度。

			if (factor > 1)
			{
				// 放大时的偏移计算
				// 中文：当缩放因子大于1（放大）时，计算向内偏移量。
				var f = (factor - 1) / (scale * 2);
				dx *= -f;
				dy *= -f;
			}
			else
			{
				// 缩小时的偏移计算
				// 中文：当缩放因子小于1（缩小）时，计算向外偏移量。
				var f = (1 / factor - 1) / (this.view.scale * 2);
				dx *= f;
				dy *= f;
			}

			this.view.scaleAndTranslate(scale,
				this.view.translate.x + dx,
				this.view.translate.y + dy);
			// 同时设置缩放和平移
			// 中文：更新缩放比例并调整平移以保持图表居中。
		}
		else
		{
			// 有滚动条或不居中的缩放
			// 中文：当有滚动条或不需居中时，调整缩放比例和滚动条位置。
			var tx = this.view.translate.x;
			var ty = this.view.translate.y;
			var sl = this.container.scrollLeft;
			var st = this.container.scrollTop;
			// 保存当前平移和滚动条位置
			// 中文：记录当前的视图平移和滚动条位置以便计算调整量。

			this.view.setScale(scale);
			// 设置新的缩放比例
			// 中文：更新视图的缩放比例。

			if (hasScrollbars)
			{
				var dx = 0;
				var dy = 0;
				// 初始化滚动偏移量
				// 中文：初始化滚动条的水平和垂直偏移量。

				if (center)
				{
					// 居中时的滚动调整
					// 中文：如果需要居中，计算滚动条的居中偏移量。
					dx = this.container.offsetWidth * (factor - 1) / 2;
					dy = this.container.offsetHeight * (factor - 1) / 2;
				}
				
				this.container.scrollLeft = (this.view.translate.x - tx) * this.view.scale + Math.round(sl * factor + dx);
				this.container.scrollTop = (this.view.translate.y - ty) * this.view.scale + Math.round(st * factor + dy);
				// 更新滚动条位置
				// 中文：根据缩放后的平移和居中偏移调整滚动条位置。
			}
		}
	}
};

/**
 * Function: zoomToRect
 * 
 * Zooms the graph to the specified rectangle. If the rectangle does not have same aspect
 * ratio as the display container, it is increased in the smaller relative dimension only
 * until the aspect match. The original rectangle is centralised within this expanded one.
 * 
 * Note that the input rectangular must be un-scaled and un-translated.
 * 
 * Parameters:
 * 
 * rect - The un-scaled and un-translated rectangluar region that should be just visible 
 * after the operation
 */
// 方法目的：缩放图表以显示指定矩形区域。
// 中文：调整图表缩放比例和位置，使指定矩形区域刚好可见。
// 参数说明：
//   rect - 未缩放和未平移的矩形区域，定义需要显示的区域。
// 交互逻辑：根据容器和矩形比例调整缩放和平移，确保矩形区域居中显示。
// 特殊处理：如果矩形比例与容器不匹配，扩展较小维度以匹配比例，并确保不超出容器边界。
mxGraph.prototype.zoomToRect = function(rect)
{
	var scaleX = this.container.clientWidth / rect.width;
	var scaleY = this.container.clientHeight / rect.height;
	// 计算水平和垂直缩放比例
	// 中文：根据容器宽高和矩形宽高计算水平和垂直缩放比例。

	var aspectFactor = scaleX / scaleY;
	// 计算矩形与容器的纵横比因子
	// 中文：计算矩形与容器的纵横比，用于调整矩形尺寸。

	// Remove any overlap of the rect outside the client area
	rect.x = Math.max(0, rect.x);
	rect.y = Math.max(0, rect.y);
	// 确保矩形坐标非负
	// 中文：将矩形坐标限制为非负，避免超出容器边界。

	var rectRight = Math.min(this.container.scrollWidth, rect.x + rect.width);
	var rectBottom = Math.min(this.container.scrollHeight, rect.y + rect.height);
	// 计算矩形右下角坐标
	// 中文：限制矩形右下角不超过容器的可滚动范围。

	rect.width = rectRight - rect.x;
	rect.height = rectBottom - rect.y;
	// 更新矩形宽高
	// 中文：根据限制后的右下角坐标更新矩形宽高。

	// The selection area has to be increased to the same aspect
	// ratio as the container, centred around the centre point of the 
	// original rect passed in.
	if (aspectFactor < 1.0)
	{
		// 高度需要扩展
		// 中文：当纵横比小于1时，扩展矩形高度以匹配容器比例。
		var newHeight = rect.height / aspectFactor;
		var deltaHeightBuffer = (newHeight - rect.height) / 2.0;
		rect taus
		rect.height = newHeight;
		// 计算并设置新高度
		// 中文：根据纵横比调整矩形高度。

		// Assign up to half the buffer to the upper part of the rect, not crossing 0
		// put the rest on the bottom
		var upperBuffer = Math.min(rect.y , deltaHeightBuffer);
		rect.y = rect.y - upperBuffer;
		// 调整矩形y坐标以居中
		// 中文：将扩展的高度均匀分配到矩形顶部和底部，确保居中。

		// Check if the bottom has extended too far
		rectBottom = Math.min(this.container.scrollHeight, rect.y + rect.height);
		rect.height = rectBottom - rect.y;
		// 确保矩形底部不超出容器
		// 中文：限制矩形高度不超出容器的可滚动高度。
	}
	else
	{
		// 宽度需要扩展
		// 中文：当纵横比大于1时，扩展矩形宽度以匹配容器比例。
		var newWidth = rect.width * aspectFactor;
		var deltaWidthBuffer = (newWidth - rect.width) / 2.0;
		rect.width = newWidth;
		// 计算并设置新宽度
		// 中文：根据纵横比调整矩形宽度。

		// Assign up to half the buffer to the upper part of the rect, not crossing 0
		// put the rest on the bottom
		var leftBuffer = Math.min(rect.x , deltaWidthBuffer);
		rect.x = rect.x - leftBuffer;
		// 调整矩形x坐标以居中
		// 中文：将扩展的宽度均匀分配到矩形左右两侧，确保居中。

		// Check if the right hand side has extended too far
		rectRight = Math.min(this.container.scrollWidth, rect.x + rect.width);
		rect.width = rectRight - rect.x;
		// 确保矩形右侧不超出容器
		// 中文：限制矩形宽度不超出容器的可滚动宽度。
	}

	var scale = this.container.clientWidth / rect.width;
	var newScale = this.view.scale * scale;
	// 计算新的缩放比例
	// 中文：根据容器宽度和调整后的矩形宽度计算最终缩放比例。

	if (!mxUtils.hasScrollbars(this.container))
	{
		// 无滚动条时的缩放和平移
		// 中文：当容器没有滚动条时，直接设置缩放和平移。
		this.view.scaleAndTranslate(newScale, (this.view.translate.x - rect.x / this.view.scale), (this.view.translate.y - rect.y / this.view.scale));
		// 设置缩放和平移
		// 中文：更新视图的缩放比例和平移值，使矩形区域可见。
	}
	else
	{
		// 有滚动条时的缩放和滚动
		// 中文：当容器有滚动条时，设置缩放并调整滚动条位置。
		this.view.setScale(newScale);
		// 设置缩放比例
		// 中文：更新视图的缩放比例。

		this.container.scrollLeft = Math.round(rect.x * scale);
		this.container.scrollTop = Math.round(rect.y * scale);
		// 设置滚动条位置
		// 中文：调整滚动条位置，使矩形区域位于容器可见区域。
	}
};

/**
 * Function: scrollCellToVisible
 * 
 * Pans the graph so that it shows the given cell. Optionally the cell may
 * be centered in the container.
 * 
 * To center a given graph if the <container> has no scrollbars, use the following code.
 * 
 * [code]
 * var bounds = graph.getGraphBounds();
 * graph.view.setTranslate(-bounds.x - (bounds.width - container.clientWidth) / 2,
 * 						   -bounds.y - (bounds.height - container.clientHeight) / 2);
 * [/code]
 * 
 * Parameters:
 * 
 * cell - <mxCell> to be made visible.
 * center - Optional boolean flag. Default is false.
 */
// 方法目的：平移图表以显示指定单元。
// 中文：调整图表视图，使指定单元可见，可选择居中显示。
// 参数说明：
//   cell - 需要显示的 <mxCell> 对象。
//   center - 可选布尔值，指定是否居中显示，默认为 false。
// 交互逻辑：根据单元位置调整视图平移或滚动条，确保单元可见。
mxGraph.prototype.scrollCellToVisible = function(cell, center)
{
	var x = -this.view.translate.x;
	var y = -this.view.translate.y;
	// 获取当前视图平移的相反值
	// 中文：计算当前视图平移的负值，用于坐标转换。

	var state = this.view.getState(cell);
	// 获取单元的状态
	// 中文：获取指定单元的状态信息，包括位置和尺寸。

	if (state != null)
	{
		var bounds = new mxRectangle(x + state.x, y + state.y, state.width,
			state.height);
		// 创建单元的边界矩形
		// 中文：根据单元状态和视图平移创建边界矩形。

		if (center && this.container != null)
		{
			// 居中显示的处理
			// 中文：如果需要居中，调整矩形以居中于容器。
			var w = this.container.clientWidth;
			var h = this.container.clientHeight;
			// 获取容器宽高
			// 中文：获取容器的有效宽度和高度。

			bounds.x = bounds.getCenterX() - w / 2;
			bounds.width = w;
			bounds.y = bounds.getCenterY() - h / 2;
			bounds.height = h;
			// 调整矩形位置和大小以居中
			// 中文：将矩形调整为容器大小并居中于容器中心。
		}
		
		var tr = new mxPoint(this.view.translate.x, this.view.translate.y);
		// 保存当前平移值
		// 中文：记录当前视图的平移值以便比较。

		if (this.scrollRectToVisible(bounds))
		{
			// 如果需要调整视图
			// 中文：调用 scrollRectToVisible 调整视图以显示矩形区域。
			// Triggers an event to be fired but does not revalidate again
			var tr2 = new mxPoint(this.view.translate.x, this.view.translate.y);
			this.view.translate.x = tr.x;
			this.view.translate.y = tr.y;
			this.view.setTranslate(tr2.x, tr2.y);
			// 恢复并更新平移值
			// 中文：如果视图发生变化，恢复原始平移并设置新平移值。
		}
	}
};

/**
 * Function: scrollRectToVisible
 * 
 * Pans the graph so that it shows the given rectangle.
 * 
 * Parameters:
 * 
 * rect - <mxRectangle> to be made visible.
 */
// 方法目的：平移图表以显示指定矩形区域。
// 中文：调整图表视图，使指定矩形区域可见。
// 参数说明：
//   rect - 需要显示的 <mxRectangle> 对象。
// 交互逻辑：根据容器是否有滚动条，调整视图平移或滚动条位置。
// 特殊处理：确保矩形区域不超出容器边界，必要时刷新视图。
mxGraph.prototype.scrollRectToVisible = function(rect)
{
	var isChanged = false;
	// 记录视图是否发生变化
	// 中文：初始化标志变量，记录视图是否被调整。

	if (rect != null)
	{
		var w = this.container.offsetWidth;
		var h = this.container.offsetHeight;
		// 获取容器宽高
		// 中文：获取容器的实际宽度和高度。

        var widthLimit = Math.min(w, rect.width);
        var heightLimit = Math.min(h, rect.height);
		// 限制矩形宽高不超过容器
		// 中文：确保矩形宽高不超过容器尺寸。

		if (mxUtils.hasScrollbars(this.container))
		{
			// 有滚动条时的处理
			// 中文：当容器有滚动条时，调整滚动条位置以显示矩形。
			var c = this.container;
			rect.x += this.view.translate.x;
			rect.y += this.view.translate.y;
			// 调整矩形坐标，考虑视图平移
			// 中文：将矩形坐标转换为视图坐标系。

			var dx = c.scrollLeft - rect.x;
			var ddx = Math.max(dx - c.scrollLeft, 0);
			// 计算水平滚动偏移
			// 中文：计算矩形与当前滚动位置的水平差值。

			if (dx > 0)
			{
				c.scrollLeft -= dx + 2;
				// 向左滚动
				// 中文：如果矩形在滚动区域左侧，左移滚动条。
			}
			else
			{
				dx = rect.x + widthLimit - c.scrollLeft - c.clientWidth;
				// 计算右侧超出的偏移
				// 中文：检查矩形是否超出容器右侧。

				if (dx > 0)
				{
					c.scrollLeft += dx + 2;
					// 向右滚动
					// 中文：右移滚动条以显示矩形区域。
				}
			}

			var dy = c.scrollTop - rect.y;
			var ddy = Math.max(0, dy - c.scrollTop);
			// 计算垂直滚动偏移
			// 中文：计算矩形与当前滚动位置的垂直差值。

			if (dy > 0)
			{
				c.scrollTop -= dy + 2;
				// 向上滚动
				// 中文：如果矩形在滚动区域上方，上移滚动条。
			}
			else
			{
				dy = rect.y + heightLimit - c.scrollTop - c.clientHeight;
				// 计算底部超出的偏移
				// 中文：检查矩形是否超出容器底部。

				if (dy > 0)
				{
					c.scrollTop += dy + 2;
					// 向下滚动
					// 中文：下移滚动条以显示矩形区域。
				}
			}

			if (!this.useScrollbarsForPanning && (ddx != 0 || ddy != 0))
			{
				// 如果不使用滚动条进行平移
				// 中文：当不使用滚动条平移时，调整视图平移值。
				this.view.setTranslate(ddx, ddy);
				// 设置新的平移值
				// 中文：更新视图平移以显示矩形区域。
			}
		}
		else
		{
			// 无滚动条时的处理
			// 中文：当容器没有滚动条时，通过调整视图平移显示矩形。
			var x = -this.view.translate.x;
			var y = -this.view.translate.y;
			// 获取当前平移的相反值
			// 中文：计算当前视图平移的负值，用于坐标转换。

			var s = this.view.scale;
			// 获取当前缩放比例
			// 中文：获取视图的当前缩放比例。

			if (rect.x + widthLimit > x + w)
			{
				// 矩形超出容器右侧
				// 中文：检查矩形是否超出容器右侧边界。
				this.view.translate.x -= (rect.x + widthLimit - w - x) / s;
				isChanged = true;
				// 调整水平平移
				// 中文：左移视图以显示矩形右侧。
			}

			if (rect.y + heightLimit > y + h)
			{
				// 矩形超出容器底部
				// 中文：检查矩形是否超出容器底部边界。
				this.view.translate.y -= (rect.y + heightLimit - h - y) / s;
				isChanged = true;
				// 调整垂直平移
				// 中文：上移视图以显示矩形底部。
			}

			if (rect.x < x)
			{
				// 矩形在容器左侧
				// 中文：检查矩形是否在容器左侧边界之外。
				this.view.translate.x += (x - rect.x) / s;
				isChanged = true;
				// 调整水平平移
				// 中文：右移视图以显示矩形左侧。
			}

			if (rect.y  < y)
			{
				// 矩形在容器顶部
				// 中文：检查矩形是否在容器顶部边界之外。
				this.view.translate.y += (y - rect.y) / s;
				isChanged = true;
				// 调整垂直平移
				// 中文：下移视图以显示矩形顶部。
			}

			if (isChanged)
			{
				// 视图发生变化时刷新
				// 中文：如果视图平移被调整，刷新视图以更新显示。
				this.view.refresh();
				
				// Repaints selection marker (ticket 18)
				if (this.selectionCellsHandler != null)
				{
					// 刷新选中标记
					// 中文：如果存在选中单元处理器，刷新选中标记。
					this.selectionCellsHandler.refresh();
				}
			}
		}
	}

	return isChanged;
	// 返回视图是否发生变化
	// 中文：返回布尔值，指示视图是否被调整。
};

/**
 * Function: getCellGeometry
 * 
 * Returns the <mxGeometry> for the given cell. This implementation uses
 * <mxGraphModel.getGeometry>. Subclasses can override this to implement
 * specific geometries for cells in only one graph, that is, it can return
 * geometries that depend on the current state of the view.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose geometry should be returned.
 */
// 中文注释：
// 获取指定单元格的几何信息。
// 功能：调用模型的getGeometry方法返回单元格的<mxGeometry>对象。
// 参数说明：cell - 要获取几何信息的<mxCell>单元格对象。
// 注意事项：子类可重写此方法以实现特定于某个图的几何信息，依赖于视图的当前状态。
mxGraph.prototype.getCellGeometry = function(cell)
{
	return this.model.getGeometry(cell);
};

/**
 * Function: isCellVisible
 * 
 * Returns true if the given cell is visible in this graph. This
 * implementation uses <mxGraphModel.isVisible>. Subclassers can override
 * this to implement specific visibility for cells in only one graph, that
 * is, without affecting the visible state of the cell.
 * 
 * When using dynamic filter expressions for cell visibility, then the
 * graph should be revalidated after the filter expression has changed.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose visible state should be returned.
 */
// 中文注释：
// 判断指定单元格在图中是否可见。
// 功能：调用模型的isVisible方法返回单元格的可见状态。
// 参数说明：cell - 要检查可见状态的<mxCell>单元格对象。
// 注意事项：子类可重写此方法以实现特定于某个图的可见性，不影响单元格的模型状态。
// 特殊处理：当使用动态过滤表达式控制可见性时，过滤表达式更改后需重新验证图。
mxGraph.prototype.isCellVisible = function(cell)
{
	return this.model.isVisible(cell);
};

/**
 * Function: isCellCollapsed
 * 
 * Returns true if the given cell is collapsed in this graph. This
 * implementation uses <mxGraphModel.isCollapsed>. Subclassers can override
 * this to implement specific collapsed states for cells in only one graph,
 * that is, without affecting the collapsed state of the cell.
 * 
 * When using dynamic filter expressions for the collapsed state, then the
 * graph should be revalidated after the filter expression has changed.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose collapsed state should be returned.
 */
// 中文注释：
// 判断指定单元格在图中是否处于折叠状态。
// 功能：调用模型的isCollapsed方法返回单元格的折叠状态。
// 参数说明：cell - 要检查折叠状态的<mxCell>单元格对象。
// 注意事项：子类可重写此方法以实现特定于某个图的折叠状态，不影响单元格的模型状态。
// 特殊处理：当使用动态过滤表达式控制折叠状态时，过滤表达式更改后需重新验证图。
mxGraph.prototype.isCellCollapsed = function(cell)
{
	return this.model.isCollapsed(cell);
};

/**
 * Function: isCellConnectable
 * 
 * Returns true if the given cell is connectable in this graph. This
 * implementation uses <mxGraphModel.isConnectable>. Subclassers can override
 * this to implement specific connectable states for cells in only one graph,
 * that is, without affecting the connectable state of the cell in the model.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose connectable state should be returned.
 */
// 中文注释：
// 判断指定单元格在图中是否可连接。
// 功能：调用模型的isConnectable方法返回单元格的可连接状态。
// 参数说明：cell - 要检查可连接状态的<mxCell>单元格对象。
// 注意事项：子类可重写此方法以实现特定于某个图的可连接状态，不影响单元格的模型状态。
mxGraph.prototype.isCellConnectable = function(cell)
{
	return this.model.isConnectable(cell);
};

/**
 * Function: isOrthogonal
 * 
 * Returns true if perimeter points should be computed such that the
 * resulting edge has only horizontal or vertical segments.
 * 
 * Parameters:
 * 
 * edge - <mxCellState> that represents the edge.
 */
// 中文注释：
// 判断边是否应计算为仅包含水平或垂直段的正交边。
// 功能：检查边的样式是否指定为正交，或边样式是否为特定连接器类型。
// 参数说明：edge - 表示边的<mxCellState>对象。
// 关键变量：orthogonal - 边的样式中是否设置了正交属性。
// 样式设置：检查STYLE_ORTHOGONAL属性或特定边样式（如SegmentConnector、ElbowConnector等）。
// 注意事项：返回true表示边应为正交样式，仅包含水平或垂直段。
mxGraph.prototype.isOrthogonal = function(edge)
{
	var orthogonal = edge.style[mxConstants.STYLE_ORTHOGONAL];
	
	if (orthogonal != null)
	{
		return orthogonal;
	}
	
	var tmp = this.view.getEdgeStyle(edge);
	
	return tmp == mxEdgeStyle.SegmentConnector ||
		tmp == mxEdgeStyle.ElbowConnector ||
		tmp == mxEdgeStyle.SideToSide ||
		tmp == mxEdgeStyle.TopToBottom ||
		tmp == mxEdgeStyle.EntityRelation ||
		tmp == mxEdgeStyle.OrthConnector;
};

/**
 * Function: isLoop
 * 
 * Returns true if the given cell state is a loop.
 * 
 * Parameters:
 * 
 * state - <mxCellState> that represents a potential loop.
 */
// 中文注释：
// 判断给定的单元格状态是否为自环。
// 功能：检查边的源和目标终端是否相同。
// 参数说明：state - 表示潜在自环的<mxCellState>对象。
// 关键变量：src - 边的可见源终端状态；trg - 边的可见目标终端状态。
// 注意事项：返回true表示源和目标终端相同，即为自环。
mxGraph.prototype.isLoop = function(state)
{
	var src = state.getVisibleTerminalState(true);
	var trg = state.getVisibleTerminalState(false);
	
	return (src != null && src == trg);
};

/**
 * Function: isCloneEvent
 * 
 * Returns true if the given event is a clone event. This implementation
 * returns true if control is pressed.
 */
// 中文注释：
// 判断给定事件是否为克隆事件。
// 功能：检查事件中是否按下了Control键。
// 参数说明：evt - 要检查的鼠标事件对象。
// 事件处理逻辑：当Control键被按下时，返回true，表示触发克隆操作。
mxGraph.prototype.isCloneEvent = function(evt)
{
	return mxEvent.isControlDown(evt);
};

/**
 * Function: isTransparentClickEvent
 * 
 * Hook for implementing click-through behaviour on selected cells. If this
 * returns true the cell behind the selected cell will be selected. This
 * implementation returns false;
 */
// 中文注释：
// 判断是否为透明点击事件（点击穿透行为）。
// 功能：实现点击选中单元格时，是否选择其后面的单元格。
// 参数说明：evt - 要检查的鼠标事件对象。
// 注意事项：默认实现返回false，表示不启用点击穿透。
// 交互逻辑：子类可重写以实现特定的点击穿透行为。
mxGraph.prototype.isTransparentClickEvent = function(evt)
{
	return false;
};

/**
 * Function: isToggleEvent
 * 
 * Returns true if the given event is a toggle event. This implementation
 * returns true if the meta key (Cmd) is pressed on Macs or if control is
 * pressed on any other platform.
 */
// 中文注释：
// 判断给定事件是否为切换事件。
// 功能：检查事件中是否在Mac上按下Meta键（Cmd）或在其他平台上按下Control键。
// 参数说明：evt - 要检查的鼠标事件对象。
// 事件处理逻辑：Mac平台检查Meta键，其他平台检查Control键，返回true表示触发切换操作。
mxGraph.prototype.isToggleEvent = function(evt)
{
	return (mxClient.IS_MAC) ? mxEvent.isMetaDown(evt) : mxEvent.isControlDown(evt);
};

/**
 * Function: isGridEnabledEvent
 * 
 * Returns true if the given mouse event should be aligned to the grid.
 */
// 中文注释：
// 判断鼠标事件是否应启用网格对齐。
// 功能：检查事件中是否未按下Alt键。
// 参数说明：evt - 要检查的鼠标事件对象。
// 事件处理逻辑：当未按下Alt键时，返回true，表示鼠标事件应与网格对齐。
mxGraph.prototype.isGridEnabledEvent = function(evt)
{
	return evt != null && !mxEvent.isAltDown(evt);
};

/**
 * Function: isConstrainedEvent
 * 
 * Returns true if the given mouse event should be aligned to the grid.
 */
// 中文注释：
// 判断鼠标事件是否为约束事件（限制移动）。
// 功能：检查事件中是否按下了Shift键。
// 参数说明：evt - 要检查的鼠标事件对象。
// 事件处理逻辑：当Shift键被按下时，返回true，表示触发约束移动操作。
mxGraph.prototype.isConstrainedEvent = function(evt)
{
	return mxEvent.isShiftDown(evt);
};

/**
 * Function: isIgnoreTerminalEvent
 * 
 * Returns true if the given mouse event should not allow any connections to be
 * made. This implementation returns false.
 */
// 中文注释：
// 判断鼠标事件是否应忽略连接操作。
// 功能：默认实现返回false，表示允许连接操作。
// 参数说明：evt - 要检查的鼠标事件对象。
// 注意事项：子类可重写以实现特定的连接限制逻辑。
mxGraph.prototype.isIgnoreTerminalEvent = function(evt)
{
	return false;
};

/**
 * Group: Validation
 */

/**
 * Function: validationAlert
 * 
 * Displays the given validation error in a dialog. This implementation uses
 * mxUtils.alert.
 */
// 中文注释：
// 显示验证错误信息。
// 功能：通过mxUtils.alert方法在对话框中显示给定的验证错误信息。
// 参数说明：message - 要显示的错误信息字符串。
// 交互逻辑：使用对话框向用户展示验证错误。
mxGraph.prototype.validationAlert = function(message)
{
	mxUtils.alert(message);
};

/**
 * Function: isEdgeValid
 * 
 * Checks if the return value of <getEdgeValidationError> for the given
 * arguments is null.
 *  
 * Parameters:
 * 
 * edge - <mxCell> that represents the edge to validate.
 * source - <mxCell> that represents the source terminal.
 * target - <mxCell> that represents the target terminal.
 */
// 中文注释：
// 检查边的验证错误是否为空。
// 功能：调用getEdgeValidationError方法，检查返回值为null表示边有效。
// 参数说明：
//   - edge - 表示要验证的边的<mxCell>对象。
//   - source - 表示源终端的<mxCell>对象。
//   - target - 表示目标终端的<mxCell>对象。
// 注意事项：返回true表示边有效，无验证错误。
mxGraph.prototype.isEdgeValid = function(edge, source, target)
{
	return this.getEdgeValidationError(edge, source, target) == null;
};

/**
 * Function: getEdgeValidationError
 * 
 * Returns the validation error message to be displayed when inserting or
 * changing an edges' connectivity. A return value of null means the edge
 * is valid, a return value of '' means it's not valid, but do not display
 * an error message. Any other (non-empty) string returned from this method
 * is displayed as an error message when trying to connect an edge to a
 * source and target. This implementation uses the <multiplicities>, and
 * checks <multigraph>, <allowDanglingEdges> and <allowLoops> to generate
 * validation errors.
 * 
 * For extending this method with specific checks for source/target cells,
 * the method can be extended as follows. Returning an empty string means
 * the edge is invalid with no error message, a non-null string specifies
 * the error message, and null means the edge is valid.
 * 
 * (code)
 * graph.getEdgeValidationError = function(edge, source, target)
 * {
 *   if (source != null && target != null &&
 *     this.model.getValue(source) != null &&
 *     this.model.getValue(target) != null)
 *   {
 *     if (target is not valid for source)
 *     {
 *       return 'Invalid Target';
 *     }
 *   }
 *   
 *   // "Supercall"
 *   return mxGraph.prototype.getEdgeValidationError.apply(this, arguments);
 * }
 * (end)
 *  
 * Parameters:
 * 
 * edge - <mxCell> that represents the edge to validate.
 * source - <mxCell> that represents the source terminal.
 * target - <mxCell> that represents the target terminal.
 */
// 中文注释：
// 获取边的验证错误信息。
// 功能：验证边的连接有效性，返回错误信息字符串。
// 参数说明：
//   - edge - 表示要验证的边的<mxCell>对象。
//   - source - 表示源终端的<mxCell>对象。
//   - target - 表示目标终端的<mxCell>对象。
// 关键变量：
//   - allowDanglingEdges - 是否允许悬空边（无源或目标终端的边）。
//   - allowLoops - 是否允许自环。
//   - multigraph - 是否允许同一对节点间存在多条边。
//   - multiplicities - 验证规则集合，用于检查边的连接限制。
// 验证逻辑：
//   1. 检查悬空边：如果不允许悬空边且源或目标为空，返回空字符串（无效但不显示错误）。
//   2. 检查完全悬空边：如果边没有源和目标终端，返回null（有效）。
//   3. 检查自环：如果不允许自环且源和目标相同，返回空字符串（无效）。
//   4. 检查连接有效性：调用isValidConnection验证源和目标是否可连接。
//   5. 检查多重边：如果不允许多重边，检查源和目标间是否已存在其他边。
//   6. 检查多重性规则：遍历multiplicities，验证边的源和目标是否符合规则。
//   7. 检查自定义验证：调用validateEdge进行额外验证。
// 注意事项：
//   - 返回null表示边有效。
//   - 返回空字符串表示边无效但不显示错误信息。
//   - 返回非空字符串表示边无效并显示错误信息。
// 特殊处理：子类可重写此方法以添加特定的源/目标验证逻辑。
mxGraph.prototype.getEdgeValidationError = function(edge, source, target)
{
	if (edge != null && !this.isAllowDanglingEdges() && (source == null || target == null))
	{
		return '';
	}
	
	if (edge != null && this.model.getTerminal(edge, true) == null &&
		this.model.getTerminal(edge, false) == null)	
	{
		return null;
	}
	
	// Checks if we're dealing with a loop
	if (!this.allowLoops && source == target && source != null)
	{
		return '';
	}
	
	// Checks if the connection is generally allowed
	if (!this.isValidConnection(source, target))
	{
		return '';
	}

	if (source != null && target != null)
	{
		var error = '';

		// Checks if the cells are already connected
		// and adds an error message if required			
		if (!this.multigraph)
		{
			var tmp = this.model.getEdgesBetween(source, target, true);
			
			// Checks if the source and target are not connected by another edge
			if (tmp.length > 1 || (tmp.length == 1 && tmp[0] != edge))
			{
				error += (mxResources.get(this.alreadyConnectedResource) ||
					this.alreadyConnectedResource)+'\n';
			}
		}

		// Gets the number of outgoing edges from the source
		// and the number of incoming edges from the target
		// without counting the edge being currently changed.
		var sourceOut = this.model.getDirectedEdgeCount(source, true, edge);
		var targetIn = this.model.getDirectedEdgeCount(target, false, edge);

		// Checks the change against each multiplicity rule
		if (this.multiplicities != null)
		{
			for (var i = 0; i < this.multiplicities.length; i++)
			{
				var err = this.multiplicities[i].check(this, edge, source,
					target, sourceOut, targetIn);
				
				if (err != null)
				{
					error += err;
				}
			}
		}

		// Validates the source and target terminals independently
		var err = this.validateEdge(edge, source, target);
		
		if (err != null)
		{
			error += err;
		}
		
		return (error.length > 0) ? error : null;
	}
	
	return (this.allowDanglingEdges) ? null : '';
};

/**
 * Function: validateEdge
 * 
 * Hook method for subclassers to return an error message for the given
 * edge and terminals. This implementation returns null.
 * 
 * Parameters:
 * 
 * edge - <mxCell> that represents the edge to validate.
 * source - <mxCell> that represents the source terminal.
 * target - <mxCell> that represents the target terminal.
 */
// 中文注释：
// 验证边的自定义错误信息。
// 功能：提供钩子方法，子类可重写以返回特定边的验证错误信息。
// 参数说明：
//   - edge - 表示要验证的边的<mxCell>对象。
//   - source - 表示源终端的<mxCell>对象。
//   - target - 表示目标终端的<mxCell>对象。
// 注意事项：默认实现返回null，表示无错误。子类可实现特定验证逻辑。
mxGraph.prototype.validateEdge = function(edge, source, target)
{
	return null;
};

/**
 * Function: validateGraph
 * 
 * Validates the graph by validating each descendant of the given cell or
 * the root of the model. Context is an object that contains the validation
 * state for the complete validation run. The validation errors are
 * attached to their cells using <setCellWarning>. Returns null in the case of
 * successful validation or an array of strings (warnings) in the case of
 * failed validations.
 * 
 * Paramters:
 * 
 * cell - Optional <mxCell> to start the validation recursion. Default is
 * the graph root.
 * context - Object that represents the global validation state.
 */
// 中文注释：
// 验证整个图或指定单元格及其后代。
// 功能：递归验证图中指定单元格或模型根节点的所有后代，验证错误通过setCellWarning附加到单元格。
// 参数说明：
//   - cell - 可选的<mxCell>对象，用于开始递归验证，默认为图的根节点。
//   - context - 表示全局验证状态的对象。
// 关键变量：
//   - isValid - 布尔值，记录验证是否成功。
//   - childCount - 子节点数量，用于遍历子节点。
// 验证逻辑：
//   1. 遍历所有子节点，递归调用validateGraph进行验证。
//   2. 如果子节点是有效根节点，创建新的上下文对象。
//   3. 将验证错误附加到单元格，并更新isValid状态。
//   4. 检查折叠单元格的子节点错误，添加警告信息。
//   5. 对边执行getEdgeValidationError验证，对节点执行getCellValidationError验证。
//   6. 调用validateCell进行自定义验证。
//   7. 验证完成后更新视图以显示警告图标。
// 注意事项：
//   - 返回null表示验证成功。
//   - 返回字符串数组表示验证失败，包含警告信息。
// 特殊处理：折叠单元格的子节点错误会附加特定警告信息。
mxGraph.prototype.validateGraph = function(cell, context)
{
	cell = (cell != null) ? cell : this.model.getRoot();
	context = (context != null) ? context : new Object();
	
	var isValid = true;
	var childCount = this.model.getChildCount(cell);
	
	for (var i = 0; i < childCount; i++)
	{
		var tmp = this.model.getChildAt(cell, i);
		var ctx = context;
		
		if (this.isValidRoot(tmp))
		{
			ctx = new Object();
		}
		
		var warn = this.validateGraph(tmp, ctx);
		
		if (warn != null)
		{
			this.setCellWarning(tmp, warn.replace(/\n/g, '<br>'));
		}
		else
		{
			this.setCellWarning(tmp, null);
		}
		
		isValid = isValid && warn == null;
	}
	
	var warning = '';
	
	// Adds error for invalid children if collapsed (children invisible)
	if (this.isCellCollapsed(cell) && !isValid)
	{
		warning += (mxResources.get(this.containsValidationErrorsResource) ||
			this.containsValidationErrorsResource) + '\n';
	}
	
	// Checks edges and cells using the defined multiplicities
	if (this.model.isEdge(cell))
	{
		warning += this.getEdgeValidationError(cell,
		this.model.getTerminal(cell, true),
		this.model.getTerminal(cell, false)) || '';
	}
	else
	{
		warning += this.getCellValidationError(cell) || '';
	}
	
	// Checks custom validation rules
	var err = this.validateCell(cell, context);
	
	if (err != null)
	{
		warning += err;
	}
	
	// Updates the display with the warning icons
	// before any potential alerts are displayed.
	// LATER: Move this into addCellOverlay. Redraw
	// should check if overlay was added or removed.
	if (this.model.getParent(cell) == null)
	{
		this.view.validate();
	}

	return (warning.length > 0 || !isValid) ? warning : null;
};

/**
 * Function: getCellValidationError
 * 
 * Checks all <multiplicities> that cannot be enforced while the graph is
 * being modified, namely, all multiplicities that require a minimum of
 * 1 edge.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which the multiplicities should be checked.
 */
mxGraph.prototype.getCellValidationError = function(cell)
{
	var outCount = this.model.getDirectedEdgeCount(cell, true);
	var inCount = this.model.getDirectedEdgeCount(cell, false);
	var value = this.model.getValue(cell);
	var error = '';

	if (this.multiplicities != null)
	{
		for (var i = 0; i < this.multiplicities.length; i++)
		{
			var rule = this.multiplicities[i];
			
			if (rule.source && mxUtils.isNode(value, rule.type,
				rule.attr, rule.value) && (outCount > rule.max ||
				outCount < rule.min))
			{
				error += rule.countError + '\n';
			}
			else if (!rule.source && mxUtils.isNode(value, rule.type,
					rule.attr, rule.value) && (inCount > rule.max ||
					inCount < rule.min))
			{
				error += rule.countError + '\n';
			}
		}
	}

	return (error.length > 0) ? error : null;
};

/**
 * Function: validateCell
 * 
 * Hook method for subclassers to return an error message for the given
 * cell and validation context. This implementation returns null. Any HTML
 * breaks will be converted to linefeeds in the calling method.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that represents the cell to validate.
 * context - Object that represents the global validation state.
 */
mxGraph.prototype.validateCell = function(cell, context)
{
	return null;
};

/**
 * Group: Graph appearance
 */

/**
 * Function: getBackgroundImage
 * 
 * Returns the <backgroundImage> as an <mxImage>.
 */
mxGraph.prototype.getBackgroundImage = function()
{
	return this.backgroundImage;
};

/**
 * Function: setBackgroundImage
 * 
 * Sets the new <backgroundImage>.
 * 
 * Parameters:
 * 
 * image - New <mxImage> to be used for the background.
 */
mxGraph.prototype.setBackgroundImage = function(image)
{
	this.backgroundImage = image;
};

/**
 * Function: getFoldingImage
 * 
 * Returns the <mxImage> used to display the collapsed state of
 * the specified cell state. This returns null for all edges.
 */
mxGraph.prototype.getFoldingImage = function(state)
{
	if (state != null && this.foldingEnabled && !this.getModel().isEdge(state.cell))
	{
		var tmp = this.isCellCollapsed(state.cell);
		
		if (this.isCellFoldable(state.cell, !tmp))
		{
			return (tmp) ? this.collapsedImage : this.expandedImage;
		}
	}
	
	return null;
};

/**
 * Function: convertValueToString
 * 
 * Returns the textual representation for the given cell. This
 * implementation returns the nodename or string-representation of the user
 * object.
 *
 * Example:
 * 
 * The following returns the label attribute from the cells user
 * object if it is an XML node.
 * 
 * (code)
 * graph.convertValueToString = function(cell)
 * {
 * 	return cell.getAttribute('label');
 * }
 * (end)
 * 
 * See also: <cellLabelChanged>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose textual representation should be returned.
 */
mxGraph.prototype.convertValueToString = function(cell)
{
	var value = this.model.getValue(cell);
	
	if (value != null)
	{
		if (mxUtils.isNode(value))
		{
			return value.nodeName;
		}
		else if (typeof(value.toString) == 'function')
		{
			return value.toString();
		}
	}
	
	return '';
};

/**
 * Function: getLabel
 * 
 * Returns a string or DOM node that represents the label for the given
 * cell. This implementation uses <convertValueToString> if <labelsVisible>
 * is true. Otherwise it returns an empty string.
 * 
 * To truncate a label to match the size of the cell, the following code
 * can be used.
 * 
 * (code)
 * graph.getLabel = function(cell)
 * {
 *   var label = mxGraph.prototype.getLabel.apply(this, arguments);
 * 
 *   if (label != null && this.model.isVertex(cell))
 *   {
 *     var geo = this.getCellGeometry(cell);
 * 
 *     if (geo != null)
 *     {
 *       var max = parseInt(geo.width / 8);
 * 
 *       if (label.length > max)
 *       {
 *         label = label.substring(0, max)+'...';
 *       }
 *     }
 *   } 
 *   return mxUtils.htmlEntities(label);
 * }
 * (end)
 * 
 * A resize listener is needed in the graph to force a repaint of the label
 * after a resize.
 * 
 * (code)
 * graph.addListener(mxEvent.RESIZE_CELLS, function(sender, evt)
 * {
 *   var cells = evt.getProperty('cells');
 * 
 *   for (var i = 0; i < cells.length; i++)
 *   {
 *     this.view.removeState(cells[i]);
 *   }
 * });
 * (end)
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose label should be returned.
 */
mxGraph.prototype.getLabel = function(cell)
{
	var result = '';
	
	if (this.labelsVisible && cell != null)
	{
		var style = this.getCurrentCellStyle(cell);
		
		if (!mxUtils.getValue(style, mxConstants.STYLE_NOLABEL, false))
		{
			result = this.convertValueToString(cell);
		}
	}
	
	return result;
};

/**
 * Function: isHtmlLabel
 * 
 * Returns true if the label must be rendered as HTML markup. The default
 * implementation returns <htmlLabels>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose label should be displayed as HTML markup.
 */
mxGraph.prototype.isHtmlLabel = function(cell)
{
	return this.isHtmlLabels();
};
 
/**
 * Function: isHtmlLabels
 * 
 * Returns <htmlLabels>.
 */
mxGraph.prototype.isHtmlLabels = function()
{
	return this.htmlLabels;
};
 
/**
 * Function: setHtmlLabels
 * 
 * Sets <htmlLabels>.
 */
mxGraph.prototype.setHtmlLabels = function(value)
{
	this.htmlLabels = value;
};

/**
 * Function: isWrapping
 * 
 * This enables wrapping for HTML labels.
 * 
 * Returns true if no white-space CSS style directive should be used for
 * displaying the given cells label. This implementation returns true if
 * <mxConstants.STYLE_WHITE_SPACE> in the style of the given cell is 'wrap'.
 * 
 * This is used as a workaround for IE ignoring the white-space directive
 * of child elements if the directive appears in a parent element. It
 * should be overridden to return true if a white-space directive is used
 * in the HTML markup that represents the given cells label. In order for
 * HTML markup to work in labels, <isHtmlLabel> must also return true
 * for the given cell.
 * 
 * Example:
 * 
 * (code)
 * graph.getLabel = function(cell)
 * {
 *   var tmp = mxGraph.prototype.getLabel.apply(this, arguments); // "supercall"
 *   
 *   if (this.model.isEdge(cell))
 *   {
 *     tmp = '<div style="width: 150px; white-space:normal;">'+tmp+'</div>';
 *   }
 *   
 *   return tmp;
 * }
 * 
 * graph.isWrapping = function(state)
 * {
 * 	 return this.model.isEdge(state.cell);
 * }
 * (end)
 * 
 * Makes sure no edge label is wider than 150 pixels, otherwise the content
 * is wrapped. Note: No width must be specified for wrapped vertex labels as
 * the vertex defines the width in its geometry.
 * 
 * Parameters:
 * 
 * state - <mxCell> whose label should be wrapped.
 */
mxGraph.prototype.isWrapping = function(cell)
{
	return this.getCurrentCellStyle(cell)[mxConstants.STYLE_WHITE_SPACE] == 'wrap';
};

/**
 * Function: isLabelClipped
 * 
 * Returns true if the overflow portion of labels should be hidden. If this
 * returns true then vertex labels will be clipped to the size of the vertices.
 * This implementation returns true if <mxConstants.STYLE_OVERFLOW> in the
 * style of the given cell is 'hidden'.
 * 
 * Parameters:
 * 
 * state - <mxCell> whose label should be clipped.
 */
mxGraph.prototype.isLabelClipped = function(cell)
{
	return this.getCurrentCellStyle(cell)[mxConstants.STYLE_OVERFLOW] == 'hidden';
};

/**
 * Function: getTooltip
 * 
 * Returns the string or DOM node that represents the tooltip for the given
 * state, node and coordinate pair. This implementation checks if the given
 * node is a folding icon or overlay and returns the respective tooltip. If
 * this does not result in a tooltip, the handler for the cell is retrieved
 * from <selectionCellsHandler> and the optional getTooltipForNode method is
 * called. If no special tooltip exists here then <getTooltipForCell> is used
 * with the cell in the given state as the argument to return a tooltip for the
 * given state.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose tooltip should be returned.
 * node - DOM node that is currently under the mouse.
 * x - X-coordinate of the mouse.
 * y - Y-coordinate of the mouse.
 */
mxGraph.prototype.getTooltip = function(state, node, x, y)
{
	var tip = null;
	
	if (state != null)
	{
		// Checks if the mouse is over the folding icon
		if (state.control != null && (node == state.control.node ||
			node.parentNode == state.control.node))
		{
			tip = this.collapseExpandResource;
			tip = mxUtils.htmlEntities(mxResources.get(tip) || tip).replace(/\\n/g, '<br>');
		}

		if (tip == null && state.overlays != null)
		{
			state.overlays.visit(function(id, shape)
			{
				// LATER: Exit loop if tip is not null
				if (tip == null && (node == shape.node || node.parentNode == shape.node))
				{
					tip = shape.overlay.toString();
				}
			});
		}
		
		if (tip == null)
		{
			var handler = this.selectionCellsHandler.getHandler(state.cell);
			
			if (handler != null && typeof(handler.getTooltipForNode) == 'function')
			{
				tip = handler.getTooltipForNode(node);
			}
		}
		
		if (tip == null)
		{
			tip = this.getTooltipForCell(state.cell);
		}
	}
	
	return tip;
};

/**
 * Function: getTooltipForCell
 * 
 * Returns the string or DOM node to be used as the tooltip for the given
 * cell. This implementation uses the cells getTooltip function if it
 * exists, or else it returns <convertValueToString> for the cell.
 * 
 * Example:
 * 
 * (code)
 * graph.getTooltipForCell = function(cell)
 * {
 *   return 'Hello, World!';
 * }
 * (end)
 * 
 * Replaces all tooltips with the string Hello, World!
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose tooltip should be returned.
 */
mxGraph.prototype.getTooltipForCell = function(cell)
{
	var tip = null;
	
	if (cell != null && cell.getTooltip != null)
	{
		tip = cell.getTooltip();
	}
	else
	{
		tip = this.convertValueToString(cell);
	}
	
	return tip;
};

/**
 * Function: getLinkForCell
 * 
 * Returns the string to be used as the link for the given cell. This
 * implementation returns null.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose tooltip should be returned.
 */
mxGraph.prototype.getLinkForCell = function(cell)
{
	return null;
};

/**
 * Function: getCursorForMouseEvent
 * 
 * Returns the cursor value to be used for the CSS of the shape for the
 * given event. This implementation calls <getCursorForCell>.
 * 
 * Parameters:
 * 
 * me - <mxMouseEvent> whose cursor should be returned.
 */
mxGraph.prototype.getCursorForMouseEvent = function(me)
{
	return this.getCursorForCell(me.getCell());
};

/**
 * Function: getCursorForCell
 * 
 * Returns the cursor value to be used for the CSS of the shape for the
 * given cell. This implementation returns null.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose cursor should be returned.
 */
mxGraph.prototype.getCursorForCell = function(cell)
{
	return null;
};

/**
 * Function: getStartSize
 * 
 * Returns the start size of the given swimlane, that is, the width or
 * height of the part that contains the title, depending on the
 * horizontal style. The return value is an <mxRectangle> with either
 * width or height set as appropriate.
 * 
 * Parameters:
 * 
 * swimlane - <mxCell> whose start size should be returned.
 * ignoreState - Optional boolean that specifies if cell state should be ignored.
 */
mxGraph.prototype.getStartSize = function(swimlane, ignoreState)
{
	var result = new mxRectangle();
	var style = this.getCurrentCellStyle(swimlane, ignoreState);
	var size = parseInt(mxUtils.getValue(style,
		mxConstants.STYLE_STARTSIZE, mxConstants.DEFAULT_STARTSIZE));
	
	if (mxUtils.getValue(style, mxConstants.STYLE_HORIZONTAL, true))
	{
		result.height = size;
	}
	else
	{
		result.width = size;
	}
	
	return result;
};

/**
 * Function: getSwimlaneDirection
 * 
 * Returns the direction for the given swimlane style.
 */
mxGraph.prototype.getSwimlaneDirection = function(style)
{
	var dir = mxUtils.getValue(style, mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_EAST);
	var flipH = mxUtils.getValue(style, mxConstants.STYLE_FLIPH, 0) == 1;
	var flipV = mxUtils.getValue(style, mxConstants.STYLE_FLIPV, 0) == 1;
	var h = mxUtils.getValue(style, mxConstants.STYLE_HORIZONTAL, true);
	var n = (h) ? 0 : 3;
	
	if (dir == mxConstants.DIRECTION_NORTH)
	{
		n--;
	}
	else if (dir == mxConstants.DIRECTION_WEST)
	{
		n += 2;
	}
	else if (dir == mxConstants.DIRECTION_SOUTH)
	{
		n += 1;
	}
	
	var mod = mxUtils.mod(n, 2);
	
	if (flipH && mod == 1)
	{
		n += 2;
	}
	
	if (flipV && mod == 0)
	{
		n += 2;
	}
	
	return [mxConstants.DIRECTION_NORTH, mxConstants.DIRECTION_EAST,
		mxConstants.DIRECTION_SOUTH, mxConstants.DIRECTION_WEST]
		[mxUtils.mod(n, 4)];
};

/**
 * Function: getActualStartSize
 * 
 * Returns the actual start size of the given swimlane taking into account
 * direction and horizontal and vertial flip styles. The start size is
 * returned as an <mxRectangle> where top, left, bottom, right start sizes
 * are returned as x, y, height and width, respectively.
 * 
 * Parameters:
 * 
 * swimlane - <mxCell> whose start size should be returned.
 * ignoreState - Optional boolean that specifies if cell state should be ignored.
 */
mxGraph.prototype.getActualStartSize = function(swimlane, ignoreState)
{
	var result = new mxRectangle();
	
	if (this.isSwimlane(swimlane, ignoreState))
	{
		var style = this.getCurrentCellStyle(swimlane, ignoreState);
		var size = parseInt(mxUtils.getValue(style, mxConstants.STYLE_STARTSIZE,
			mxConstants.DEFAULT_STARTSIZE));
		var dir = this.getSwimlaneDirection(style);
		
		if (dir == mxConstants.DIRECTION_NORTH)
		{
			result.y = size;
		}
		else if (dir == mxConstants.DIRECTION_WEST)
		{
			result.x = size;
		}
		else if (dir == mxConstants.DIRECTION_SOUTH)
		{
			result.height = size;
		}
		else
		{
			result.width = size;
		}
	}
	
	return result;
};

/**
 * Function: getImage
 * 
 * Returns the image URL for the given cell state. This implementation
 * returns the value stored under <mxConstants.STYLE_IMAGE> in the cell
 * style.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose image URL should be returned.
 */
mxGraph.prototype.getImage = function(state)
{
	return (state != null && state.style != null) ? state.style[mxConstants.STYLE_IMAGE] : null;
};

/**
 * Function: isTransparentState
 * 
 * Returns true if the given state has no stroke- or fillcolor and no image.
 * 
 * Parameters:
 * 
 * state - <mxCellState> to check.
 */
mxGraph.prototype.isTransparentState = function(state)
{
	var result = false;
	
	if (state != null)
	{
		var stroke = mxUtils.getValue(state.style, mxConstants.STYLE_STROKECOLOR, mxConstants.NONE);
		var fill = mxUtils.getValue(state.style, mxConstants.STYLE_FILLCOLOR, mxConstants.NONE);
		
		result = stroke == mxConstants.NONE && fill == mxConstants.NONE && this.getImage(state) == null;
		
	}
	
	return result;
};

/**
 * Function: getVerticalAlign
 * 
 * Returns the vertical alignment for the given cell state. This
 * implementation returns the value stored under
 * <mxConstants.STYLE_VERTICAL_ALIGN> in the cell style.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose vertical alignment should be
 * returned.
 */
mxGraph.prototype.getVerticalAlign = function(state)
{
	return (state != null && state.style != null) ?
		(state.style[mxConstants.STYLE_VERTICAL_ALIGN] ||
		mxConstants.ALIGN_MIDDLE) : null;
};

/**
 * Function: getIndicatorColor
 * 
 * Returns the indicator color for the given cell state. This
 * implementation returns the value stored under
 * <mxConstants.STYLE_INDICATOR_COLOR> in the cell style.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose indicator color should be
 * returned.
 */
mxGraph.prototype.getIndicatorColor = function(state)
{
	return (state != null && state.style != null) ? state.style[mxConstants.STYLE_INDICATOR_COLOR] : null;
};

/**
 * Function: getIndicatorGradientColor
 * 
 * Returns the indicator gradient color for the given cell state. This
 * implementation returns the value stored under
 * <mxConstants.STYLE_INDICATOR_GRADIENTCOLOR> in the cell style.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose indicator gradient color should be
 * returned.
 */
mxGraph.prototype.getIndicatorGradientColor = function(state)
{
	return (state != null && state.style != null) ? state.style[mxConstants.STYLE_INDICATOR_GRADIENTCOLOR] : null;
};

/**
 * Function: getIndicatorShape
 * 
 * Returns the indicator shape for the given cell state. This
 * implementation returns the value stored under
 * <mxConstants.STYLE_INDICATOR_SHAPE> in the cell style.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose indicator shape should be returned.
 */
mxGraph.prototype.getIndicatorShape = function(state)
{
	return (state != null && state.style != null) ? state.style[mxConstants.STYLE_INDICATOR_SHAPE] : null;
};

/**
 * Function: getIndicatorImage
 * 
 * Returns the indicator image for the given cell state. This
 * implementation returns the value stored under
 * <mxConstants.STYLE_INDICATOR_IMAGE> in the cell style.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose indicator image should be returned.
 */
mxGraph.prototype.getIndicatorImage = function(state)
{
	return (state != null && state.style != null) ? state.style[mxConstants.STYLE_INDICATOR_IMAGE] : null;
};

/**
 * Function: getBorder
 * 
 * Returns the value of <border>.
 */
mxGraph.prototype.getBorder = function()
{
	return this.border;
};

/**
 * Function: setBorder
 * 
 * Sets the value of <border>.
 * 
 * Parameters:
 * 
 * value - Positive integer that represents the border to be used.
 */
mxGraph.prototype.setBorder = function(value)
{
	this.border = value;
};

/**
 * Function: isSwimlane
 * 
 * Returns true if the given cell is a swimlane in the graph. A swimlane is
 * a container cell with some specific behaviour. This implementation
 * checks if the shape associated with the given cell is a <mxSwimlane>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to be checked.
 * ignoreState - Optional boolean that specifies if the cell state should be ignored.
 */
mxGraph.prototype.isSwimlane = function(cell, ignoreState)
{
	if (cell != null && this.model.getParent(cell) != this.model.getRoot() &&
		!this.model.isEdge(cell))
	{
		return this.getCurrentCellStyle(cell, ignoreState)
			[mxConstants.STYLE_SHAPE] == mxConstants.SHAPE_SWIMLANE;
	}
	
	return false;
};

/**
 * Group: Graph behaviour
 */

/**
 * Function: isResizeContainer
 * 
 * Returns <resizeContainer>.
 */
mxGraph.prototype.isResizeContainer = function()
{
	return this.resizeContainer;
};

/**
 * Function: setResizeContainer
 * 
 * Sets <resizeContainer>.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if the container should be resized.
 */
mxGraph.prototype.setResizeContainer = function(value)
{
	this.resizeContainer = value;
};

/**
 * Function: isEnabled
 * 
 * Returns true if the graph is <enabled>.
 */
mxGraph.prototype.isEnabled = function()
{
	return this.enabled;
};

/**
 * Function: setEnabled
 * 
 * Specifies if the graph should allow any interactions. This
 * implementation updates <enabled>.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if the graph should be enabled.
 */
mxGraph.prototype.setEnabled = function(value)
{
	this.enabled = value;
};

/**
 * Function: isEscapeEnabled
 * 
 * Returns <escapeEnabled>.
 */
mxGraph.prototype.isEscapeEnabled = function()
{
	return this.escapeEnabled;
};

/**
 * Function: setEscapeEnabled
 * 
 * Sets <escapeEnabled>.
 * 
 * Parameters:
 * 
 * enabled - Boolean indicating if escape should be enabled.
 */
mxGraph.prototype.setEscapeEnabled = function(value)
{
	this.escapeEnabled = value;
};

/**
 * Function: isInvokesStopCellEditing
 * 
 * Returns <invokesStopCellEditing>.
 */
mxGraph.prototype.isInvokesStopCellEditing = function()
{
	return this.invokesStopCellEditing;
};

/**
 * Function: setInvokesStopCellEditing
 * 
 * Sets <invokesStopCellEditing>.
 */
mxGraph.prototype.setInvokesStopCellEditing = function(value)
{
	this.invokesStopCellEditing = value;
};

/**
 * Function: isEnterStopsCellEditing
 * 
 * Returns <enterStopsCellEditing>.
 */
mxGraph.prototype.isEnterStopsCellEditing = function()
{
	return this.enterStopsCellEditing;
};

/**
 * Function: setEnterStopsCellEditing
 * 
 * Sets <enterStopsCellEditing>.
 */
mxGraph.prototype.setEnterStopsCellEditing = function(value)
{
	this.enterStopsCellEditing = value;
};

/**
 * Function: isCellLocked
 * 
 * Returns true if the given cell may not be moved, sized, bended,
 * disconnected, edited or selected. This implementation returns true for
 * all vertices with a relative geometry if <locked> is false.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose locked state should be returned.
 */
mxGraph.prototype.isCellLocked = function(cell)
{
	var geometry = this.model.getGeometry(cell);
	
	return this.isCellsLocked() || (geometry != null && this.model.isVertex(cell) && geometry.relative);
};

/**
 * Function: isCellsLocked
 * 
 * Returns true if the given cell may not be moved, sized, bended,
 * disconnected, edited or selected. This implementation returns true for
 * all vertices with a relative geometry if <locked> is false.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose locked state should be returned.
 */
mxGraph.prototype.isCellsLocked = function()
{
	return this.cellsLocked;
};

/**
 * Function: setCellsLocked
 * 
 * Sets if any cell may be moved, sized, bended, disconnected, edited or
 * selected.
 * 
 * Parameters:
 * 
 * value - Boolean that defines the new value for <cellsLocked>.
 */
mxGraph.prototype.setCellsLocked = function(value)
{
	this.cellsLocked = value;
};

/**
 * Function: getCloneableCells
 * 
 * Returns the cells which may be exported in the given array of cells.
 */
mxGraph.prototype.getCloneableCells = function(cells)
{
	return this.model.filterCells(cells, mxUtils.bind(this, function(cell)
	{
		return this.isCellCloneable(cell);
	}));
};

/**
 * Function: isCellCloneable
 * 
 * Returns true if the given cell is cloneable. This implementation returns
 * <isCellsCloneable> for all cells unless a cell style specifies
 * <mxConstants.STYLE_CLONEABLE> to be 0. 
 * 
 * Parameters:
 * 
 * cell - Optional <mxCell> whose cloneable state should be returned.
 */
mxGraph.prototype.isCellCloneable = function(cell)
{
	var style = this.getCurrentCellStyle(cell);

	return this.isCellsCloneable() && style[mxConstants.STYLE_CLONEABLE] != 0;
};

/**
 * Function: isCellsCloneable
 * 
 * Returns <cellsCloneable>, that is, if the graph allows cloning of cells
 * by using control-drag.
 */
mxGraph.prototype.isCellsCloneable = function()
{
	return this.cellsCloneable;
};

/**
 * Function: setCellsCloneable
 * 
 * Specifies if the graph should allow cloning of cells by holding down the
 * control key while cells are being moved. This implementation updates
 * <cellsCloneable>.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if the graph should be cloneable.
 */
mxGraph.prototype.setCellsCloneable = function(value)
{
	this.cellsCloneable = value;
};

/**
 * Function: getExportableCells
 * 
 * Returns the cells which may be exported in the given array of cells.
 */
mxGraph.prototype.getExportableCells = function(cells)
{
	return this.model.filterCells(cells, mxUtils.bind(this, function(cell)
	{
		return this.canExportCell(cell);
	}));
};

/**
 * Function: canExportCell
 * 
 * Returns true if the given cell may be exported to the clipboard. This
 * implementation returns <exportEnabled> for all cells.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that represents the cell to be exported.
 */
mxGraph.prototype.canExportCell = function(cell)
{
	return this.exportEnabled;
};

/**
 * Function: getImportableCells
 * 
 * Returns the cells which may be imported in the given array of cells.
 */
mxGraph.prototype.getImportableCells = function(cells)
{
	return this.model.filterCells(cells, mxUtils.bind(this, function(cell)
	{
		return this.canImportCell(cell);
	}));
};

/**
 * Function: canImportCell
 * 
 * Returns true if the given cell may be imported from the clipboard.
 * This implementation returns <importEnabled> for all cells.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that represents the cell to be imported.
 */
mxGraph.prototype.canImportCell = function(cell)
{
	return this.importEnabled;
};

/**
 * Function: isCellSelectable
 *
 * Returns true if the given cell is selectable. This implementation
 * returns <cellsSelectable>.
 * 
 * To add a new style for making cells (un)selectable, use the following code.
 * 
 * (code)
 * mxGraph.prototype.isCellSelectable = function(cell)
 * {
 *   var style = this.getCurrentCellStyle(cell);
 *   
 *   return this.isCellsSelectable() && !this.isCellLocked(cell) && style['selectable'] != 0;
 * };
 * (end)
 * 
 * You can then use the new style as shown in this example.
 * 
 * (code)
 * graph.insertVertex(parent, null, 'Hello,', 20, 20, 80, 30, 'selectable=0');
 * (end)
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose selectable state should be returned.
 */
mxGraph.prototype.isCellSelectable = function(cell)
{
	return this.isCellsSelectable();
};

/**
 * Function: isCellsSelectable
 *
 * Returns <cellsSelectable>.
 */
mxGraph.prototype.isCellsSelectable = function()
{
	return this.cellsSelectable;
};

/**
 * Function: setCellsSelectable
 *
 * Sets <cellsSelectable>.
 */
mxGraph.prototype.setCellsSelectable = function(value)
{
	this.cellsSelectable = value;
};

/**
 * Function: getDeletableCells
 * 
 * Returns the cells which may be exported in the given array of cells.
 */
mxGraph.prototype.getDeletableCells = function(cells)
{
	return this.model.filterCells(cells, mxUtils.bind(this, function(cell)
	{
		return this.isCellDeletable(cell);
	}));
};

/**
 * Function: isCellDeletable
 *
 * Returns true if the given cell is moveable. This returns
 * <cellsDeletable> for all given cells if a cells style does not specify
 * <mxConstants.STYLE_DELETABLE> to be 0.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose deletable state should be returned.
 */
mxGraph.prototype.isCellDeletable = function(cell)
{
	var style = this.getCurrentCellStyle(cell);
	
	return this.isCellsDeletable() && style[mxConstants.STYLE_DELETABLE] != 0;
};

/**
 * Function: isCellsDeletable
 *
 * Returns <cellsDeletable>.
 */
mxGraph.prototype.isCellsDeletable = function()
{
	return this.cellsDeletable;
};

/**
 * Function: setCellsDeletable
 * 
 * Sets <cellsDeletable>.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if the graph should allow deletion of cells.
 */
mxGraph.prototype.setCellsDeletable = function(value)
{
	this.cellsDeletable = value;
};

/**
 * Function: isLabelMovable
 *
 * Returns true if the given edges's label is moveable. This returns
 * <movable> for all given cells if <isLocked> does not return true
 * for the given cell.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose label should be moved.
 */
mxGraph.prototype.isLabelMovable = function(cell)
{
	return !this.isCellLocked(cell) &&
		((this.model.isEdge(cell) && this.edgeLabelsMovable) ||
		(this.model.isVertex(cell) && this.vertexLabelsMovable));
};

/**
 * Function: isCellRotatable
 *
 * Returns true if the given cell is rotatable. This returns true for the given
 * cell if its style does not specify <mxConstants.STYLE_ROTATABLE> to be 0.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose rotatable state should be returned.
 */
mxGraph.prototype.isCellRotatable = function(cell)
{
	var style = this.getCurrentCellStyle(cell);
	
	return style[mxConstants.STYLE_ROTATABLE] != 0;
};

/**
 * Function: getMovableCells
 * 
 * Returns the cells which are movable in the given array of cells.
 */
mxGraph.prototype.getMovableCells = function(cells)
{
	return this.model.filterCells(cells, mxUtils.bind(this, function(cell)
	{
		return this.isCellMovable(cell);
	}));
};

/**
 * Function: isCellMovable
 *
 * Returns true if the given cell is moveable. This returns <cellsMovable>
 * for all given cells if <isCellLocked> does not return true for the given
 * cell and its style does not specify <mxConstants.STYLE_MOVABLE> to be 0.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose movable state should be returned.
 */
mxGraph.prototype.isCellMovable = function(cell)
{
	var style = this.getCurrentCellStyle(cell);
	
	return this.isCellsMovable() && !this.isCellLocked(cell) && style[mxConstants.STYLE_MOVABLE] != 0;
};

/**
 * Function: isCellsMovable
 *
 * Returns <cellsMovable>.
 */
mxGraph.prototype.isCellsMovable = function()
{
	return this.cellsMovable;
};

/**
 * Function: setCellsMovable
 * 
 * Specifies if the graph should allow moving of cells. This implementation
 * updates <cellsMsovable>.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if the graph should allow moving of cells.
 */
mxGraph.prototype.setCellsMovable = function(value)
{
	this.cellsMovable = value;
};

/**
 * Function: isGridEnabled
 *
 * Returns <gridEnabled> as a boolean.
 */
mxGraph.prototype.isGridEnabled = function()
{
	return this.gridEnabled;
};

/**
 * Function: setGridEnabled
 * 
 * Specifies if the grid should be enabled.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if the grid should be enabled.
 */
mxGraph.prototype.setGridEnabled = function(value)
{
	this.gridEnabled = value;
};

/**
 * Function: isPortsEnabled
 *
 * Returns <portsEnabled> as a boolean.
 */
mxGraph.prototype.isPortsEnabled = function()
{
	return this.portsEnabled;
};

/**
 * Function: setPortsEnabled
 * 
 * Specifies if the ports should be enabled.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if the ports should be enabled.
 */
mxGraph.prototype.setPortsEnabled = function(value)
{
	this.portsEnabled = value;
};

/**
 * Function: getGridSize
 *
 * Returns <gridSize>.
 */
mxGraph.prototype.getGridSize = function()
{
	return this.gridSize;
};

/**
 * Function: setGridSize
 * 
 * Sets <gridSize>.
 */
mxGraph.prototype.setGridSize = function(value)
{
	this.gridSize = value;
};

/**
 * Function: getTolerance
 *
 * Returns <tolerance>.
 */
mxGraph.prototype.getTolerance = function()
{
	return this.tolerance;
};

/**
 * Function: setTolerance
 * 
 * Sets <tolerance>.
 */
mxGraph.prototype.setTolerance = function(value)
{
	this.tolerance = value;
};

/**
 * Function: isVertexLabelsMovable
 *
 * Returns <vertexLabelsMovable>.
 */
mxGraph.prototype.isVertexLabelsMovable = function()
{
	return this.vertexLabelsMovable;
};

/**
 * Function: setVertexLabelsMovable
 * 
 * Sets <vertexLabelsMovable>.
 */
mxGraph.prototype.setVertexLabelsMovable = function(value)
{
	this.vertexLabelsMovable = value;
};

/**
 * Function: isEdgeLabelsMovable
 *
 * Returns <edgeLabelsMovable>.
 */
mxGraph.prototype.isEdgeLabelsMovable = function()
{
	return this.edgeLabelsMovable;
};

/**
 * Function: isEdgeLabelsMovable
 * 
 * Sets <edgeLabelsMovable>.
 */
mxGraph.prototype.setEdgeLabelsMovable = function(value)
{
	this.edgeLabelsMovable = value;
};

/**
 * Function: isSwimlaneNesting
 *
 * Returns <swimlaneNesting> as a boolean.
 */
mxGraph.prototype.isSwimlaneNesting = function()
{
	return this.swimlaneNesting;
};

/**
 * Function: setSwimlaneNesting
 * 
 * Specifies if swimlanes can be nested by drag and drop. This is only
 * taken into account if dropEnabled is true.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if swimlanes can be nested.
 */
mxGraph.prototype.setSwimlaneNesting = function(value)
{
	this.swimlaneNesting = value;
};

/**
 * Function: isSwimlaneSelectionEnabled
 *
 * Returns <swimlaneSelectionEnabled> as a boolean.
 */
mxGraph.prototype.isSwimlaneSelectionEnabled = function()
{
	return this.swimlaneSelectionEnabled;
};

/**
 * Function: setSwimlaneSelectionEnabled
 * 
 * Specifies if swimlanes should be selected if the mouse is released
 * over their content area.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if swimlanes content areas
 * should be selected when the mouse is released over them.
 */
mxGraph.prototype.setSwimlaneSelectionEnabled = function(value)
{
	this.swimlaneSelectionEnabled = value;
};

/**
 * Function: isMultigraph
 *
 * Returns <multigraph> as a boolean.
 */
mxGraph.prototype.isMultigraph = function()
{
	return this.multigraph;
};

/**
 * Function: setMultigraph
 * 
 * Specifies if the graph should allow multiple connections between the
 * same pair of vertices.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if the graph allows multiple connections
 * between the same pair of vertices.
 */
mxGraph.prototype.setMultigraph = function(value)
{
	this.multigraph = value;
};

/**
 * Function: isAllowLoops
 *
 * Returns <allowLoops> as a boolean.
 */
mxGraph.prototype.isAllowLoops = function()
{
	return this.allowLoops;
};

/**
 * Function: setAllowDanglingEdges
 * 
 * Specifies if dangling edges are allowed, that is, if edges are allowed
 * that do not have a source and/or target terminal defined.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if dangling edges are allowed.
 */
mxGraph.prototype.setAllowDanglingEdges = function(value)
{
	this.allowDanglingEdges = value;
};

/**
 * Function: isAllowDanglingEdges
 *
 * Returns <allowDanglingEdges> as a boolean.
 */
mxGraph.prototype.isAllowDanglingEdges = function()
{
	return this.allowDanglingEdges;
};

/**
 * Function: setConnectableEdges
 * 
 * Specifies if edges should be connectable.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if edges should be connectable.
 */
mxGraph.prototype.setConnectableEdges = function(value)
{
	this.connectableEdges = value;
};

/**
 * Function: isConnectableEdges
 *
 * Returns <connectableEdges> as a boolean.
 */
mxGraph.prototype.isConnectableEdges = function()
{
	return this.connectableEdges;
};

/**
 * Function: setCloneInvalidEdges
 * 
 * Specifies if edges should be inserted when cloned but not valid wrt.
 * <getEdgeValidationError>. If false such edges will be silently ignored.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if cloned invalid edges should be
 * inserted into the graph or ignored.
 */
mxGraph.prototype.setCloneInvalidEdges = function(value)
{
	this.cloneInvalidEdges = value;
};

/**
 * Function: isCloneInvalidEdges
 *
 * Returns <cloneInvalidEdges> as a boolean.
 */
mxGraph.prototype.isCloneInvalidEdges = function()
{
	return this.cloneInvalidEdges;
};

/**
 * Function: setAllowLoops
 * 
 * Specifies if loops are allowed.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if loops are allowed.
 */
mxGraph.prototype.setAllowLoops = function(value)
{
	this.allowLoops = value;
};

/**
 * Function: isDisconnectOnMove
 *
 * Returns <disconnectOnMove> as a boolean.
 */
mxGraph.prototype.isDisconnectOnMove = function()
{
	return this.disconnectOnMove;
};

/**
 * Function: setDisconnectOnMove
 * 
 * Specifies if edges should be disconnected when moved. (Note: Cloned
 * edges are always disconnected.)
 * 
 * Parameters:
 * 
 * value - Boolean indicating if edges should be disconnected
 * when moved.
 */
mxGraph.prototype.setDisconnectOnMove = function(value)
{
	this.disconnectOnMove = value;
};

/**
 * Function: isDropEnabled
 *
 * Returns <dropEnabled> as a boolean.
 */
mxGraph.prototype.isDropEnabled = function()
{
	return this.dropEnabled;
};

/**
 * Function: setDropEnabled
 * 
 * Specifies if the graph should allow dropping of cells onto or into other
 * cells.
 * 
 * Parameters:
 * 
 * dropEnabled - Boolean indicating if the graph should allow dropping
 * of cells into other cells.
 */
mxGraph.prototype.setDropEnabled = function(value)
{
	this.dropEnabled = value;
};

/**
 * Function: isSplitEnabled
 *
 * Returns <splitEnabled> as a boolean.
 */
mxGraph.prototype.isSplitEnabled = function()
{
	return this.splitEnabled;
};

/**
 * Function: setSplitEnabled
 * 
 * Specifies if the graph should allow dropping of cells onto or into other
 * cells.
 * 
 * Parameters:
 * 
 * dropEnabled - Boolean indicating if the graph should allow dropping
 * of cells into other cells.
 */
mxGraph.prototype.setSplitEnabled = function(value)
{
	this.splitEnabled = value;
};

/**
 * Function: isCellResizable
 *
 * Returns true if the given cell is resizable. This returns
 * <cellsResizable> for all given cells if <isCellLocked> does not return
 * true for the given cell and its style does not specify
 * <mxConstants.STYLE_RESIZABLE> to be 0.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose resizable state should be returned.
 */
mxGraph.prototype.isCellResizable = function(cell)
{
	var style = this.getCurrentCellStyle(cell);

	return this.isCellsResizable() && !this.isCellLocked(cell) &&
		mxUtils.getValue(style, mxConstants.STYLE_RESIZABLE, '1') != '0';
};

/**
 * Function: isCellsResizable
 *
 * Returns <cellsResizable>.
 */
mxGraph.prototype.isCellsResizable = function()
{
	return this.cellsResizable;
};

/**
 * Function: setCellsResizable
 * 
 * Specifies if the graph should allow resizing of cells. This
 * implementation updates <cellsResizable>.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if the graph should allow resizing of
 * cells.
 */
mxGraph.prototype.setCellsResizable = function(value)
{
	this.cellsResizable = value;
};

/**
 * Function: isTerminalPointMovable
 *
 * Returns true if the given terminal point is movable. This is independent
 * from <isCellConnectable> and <isCellDisconnectable> and controls if terminal
 * points can be moved in the graph if the edge is not connected. Note that it
 * is required for this to return true to connect unconnected edges. This
 * implementation returns true.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose terminal point should be moved.
 * source - Boolean indicating if the source or target terminal should be moved.
 */
mxGraph.prototype.isTerminalPointMovable = function(cell, source)
{
	return true;
};

/**
 * Function: isCellBendable
 *
 * Returns true if the given cell is bendable. This returns <cellsBendable>
 * for all given cells if <isLocked> does not return true for the given
 * cell and its style does not specify <mxConstants.STYLE_BENDABLE> to be 0.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose bendable state should be returned.
 */
mxGraph.prototype.isCellBendable = function(cell)
{
	var style = this.getCurrentCellStyle(cell);
	
	return this.isCellsBendable() && !this.isCellLocked(cell) && style[mxConstants.STYLE_BENDABLE] != 0;
};

/**
 * Function: isCellsBendable
 *
 * Returns <cellsBenadable>.
 */
mxGraph.prototype.isCellsBendable = function()
{
	return this.cellsBendable;
};

/**
 * Function: setCellsBendable
 * 
 * Specifies if the graph should allow bending of edges. This
 * implementation updates <bendable>.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if the graph should allow bending of
 * edges.
 */
mxGraph.prototype.setCellsBendable = function(value)
{
	this.cellsBendable = value;
};

/**
 * Function: isCellEditable
 *
 * Returns true if the given cell is editable. This returns <cellsEditable> for
 * all given cells if <isCellLocked> does not return true for the given cell
 * and its style does not specify <mxConstants.STYLE_EDITABLE> to be 0.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose editable state should be returned.
 */
mxGraph.prototype.isCellEditable = function(cell)
{
	var style = this.getCurrentCellStyle(cell);
	
	return this.isCellsEditable() && !this.isCellLocked(cell) && style[mxConstants.STYLE_EDITABLE] != 0;
};

/**
 * Function: isCellsEditable
 *
 * Returns <cellsEditable>.
 */
mxGraph.prototype.isCellsEditable = function()
{
	return this.cellsEditable;
};

/**
 * Function: setCellsEditable
 * 
 * Specifies if the graph should allow in-place editing for cell labels.
 * This implementation updates <cellsEditable>.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if the graph should allow in-place
 * editing.
 */
mxGraph.prototype.setCellsEditable = function(value)
{
	this.cellsEditable = value;
};

/**
 * Function: isCellDisconnectable
 *
 * Returns true if the given cell is disconnectable from the source or
 * target terminal. This returns <isCellsDisconnectable> for all given
 * cells if <isCellLocked> does not return true for the given cell.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose disconnectable state should be returned.
 * terminal - <mxCell> that represents the source or target terminal.
 * source - Boolean indicating if the source or target terminal is to be
 * disconnected.
 */
mxGraph.prototype.isCellDisconnectable = function(cell, terminal, source)
{
	return this.isCellsDisconnectable() && !this.isCellLocked(cell);
};

/**
 * Function: isCellsDisconnectable
 *
 * Returns <cellsDisconnectable>.
 */
mxGraph.prototype.isCellsDisconnectable = function()
{
	return this.cellsDisconnectable;
};

/**
 * Function: setCellsDisconnectable
 *
 * Sets <cellsDisconnectable>.
 */
mxGraph.prototype.setCellsDisconnectable = function(value)
{
	this.cellsDisconnectable = value;
};

/**
 * Function: isValidSource
 * 
 * Returns true if the given cell is a valid source for new connections.
 * This implementation returns true for all non-null values and is
 * called by is called by <isValidConnection>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that represents a possible source or null.
 */
mxGraph.prototype.isValidSource = function(cell)
{
	return (cell == null && this.allowDanglingEdges) ||
		(cell != null && (!this.model.isEdge(cell) ||
		this.connectableEdges) && this.isCellConnectable(cell));
};
	
/**
 * Function: isValidTarget
 * 
 * Returns <isValidSource> for the given cell. This is called by
 * <isValidConnection>.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that represents a possible target or null.
 */
mxGraph.prototype.isValidTarget = function(cell)
{
	return this.isValidSource(cell);
};

/**
 * Function: isValidConnection
 * 
 * Returns true if the given target cell is a valid target for source.
 * This is a boolean implementation for not allowing connections between
 * certain pairs of vertices and is called by <getEdgeValidationError>.
 * This implementation returns true if <isValidSource> returns true for
 * the source and <isValidTarget> returns true for the target.
 * 
 * Parameters:
 * 
 * source - <mxCell> that represents the source cell.
 * target - <mxCell> that represents the target cell.
 */
mxGraph.prototype.isValidConnection = function(source, target)
{
	return this.isValidSource(source) && this.isValidTarget(target);
};

/**
 * Function: setConnectable
 * 
 * Specifies if the graph should allow new connections. This implementation
 * updates <mxConnectionHandler.enabled> in <connectionHandler>.
 * 
 * Parameters:
 * 
 * connectable - Boolean indicating if new connections should be allowed.
 */
mxGraph.prototype.setConnectable = function(connectable)
{
	this.connectionHandler.setEnabled(connectable);
};
	
/**
 * Function: isConnectable
 * 
 * Returns true if the <connectionHandler> is enabled.
 */
mxGraph.prototype.isConnectable = function()
{
	return this.connectionHandler.isEnabled();
};

/**
 * Function: setTooltips
 * 
 * Specifies if tooltips should be enabled. This implementation updates
 * <mxTooltipHandler.enabled> in <tooltipHandler>.
 * 
 * Parameters:
 * 
 * enabled - Boolean indicating if tooltips should be enabled.
 */
mxGraph.prototype.setTooltips = function (enabled)
{
	this.tooltipHandler.setEnabled(enabled);
};

/**
 * Function: setPanning
 * 
 * Specifies if panning should be enabled. This implementation updates
 * <mxPanningHandler.panningEnabled> in <panningHandler>.
 * 
 * Parameters:
 * 
 * enabled - Boolean indicating if panning should be enabled.
 */
mxGraph.prototype.setPanning = function(enabled)
{
	this.panningHandler.panningEnabled = enabled;
};

/**
 * Function: isEditing
 * 
 * Returns true if the given cell is currently being edited.
 * If no cell is specified then this returns true if any
 * cell is currently being edited.
 *
 * Parameters:
 * 
 * cell - <mxCell> that should be checked.
 */
mxGraph.prototype.isEditing = function(cell)
{
	if (this.cellEditor != null)
	{
		var editingCell = this.cellEditor.getEditingCell();
		
		return (cell == null) ? editingCell != null : cell == editingCell;
	}
	
	return false;
};

/**
 * Function: isAutoSizeCell
 * 
 * Returns true if the size of the given cell should automatically be
 * updated after a change of the label. This implementation returns
 * <autoSizeCells> or checks if the cell style does specify
 * <mxConstants.STYLE_AUTOSIZE> to be 1.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that should be resized.
 */
mxGraph.prototype.isAutoSizeCell = function(cell)
{
	var style = this.getCurrentCellStyle(cell);
	
	return this.isAutoSizeCells() || style[mxConstants.STYLE_AUTOSIZE] == 1;
};

/**
 * Function: isAutoSizeCells
 * 
 * Returns <autoSizeCells>.
 */
mxGraph.prototype.isAutoSizeCells = function()
{
	return this.autoSizeCells;
};

/**
 * Function: setAutoSizeCells
 * 
 * Specifies if cell sizes should be automatically updated after a label
 * change. This implementation sets <autoSizeCells> to the given parameter.
 * To update the size of cells when the cells are added, set
 * <autoSizeCellsOnAdd> to true.
 * 
 * Parameters:
 * 
 * value - Boolean indicating if cells should be resized
 * automatically.
 */
mxGraph.prototype.setAutoSizeCells = function(value)
{
	this.autoSizeCells = value;
};

/**
 * Function: isExtendParent
 * 
 * Returns true if the parent of the given cell should be extended if the
 * child has been resized so that it overlaps the parent. This
 * implementation returns <isExtendParents> if the cell is not an edge.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that has been resized.
 */
mxGraph.prototype.isExtendParent = function(cell)
{
	return !this.getModel().isEdge(cell) && this.isExtendParents();
};

/**
 * Function: isExtendParents
 * 
 * Returns <extendParents>.
 */
mxGraph.prototype.isExtendParents = function()
{
	return this.extendParents;
};

/**
 * Function: setExtendParents
 * 
 * Sets <extendParents>.
 * 
 * Parameters:
 * 
 * value - New boolean value for <extendParents>.
 */
mxGraph.prototype.setExtendParents = function(value)
{
	this.extendParents = value;
};

/**
 * Function: isExtendParentsOnAdd
 * 
 * Returns <extendParentsOnAdd>.
 */
mxGraph.prototype.isExtendParentsOnAdd = function(cell)
{
	return this.extendParentsOnAdd;
};

/**
 * Function: setExtendParentsOnAdd
 * 
 * Sets <extendParentsOnAdd>.
 * 
 * Parameters:
 * 
 * value - New boolean value for <extendParentsOnAdd>.
 */
mxGraph.prototype.setExtendParentsOnAdd = function(value)
{
	this.extendParentsOnAdd = value;
};

/**
 * Function: isExtendParentsOnMove
 * 
 * Returns <extendParentsOnMove>.
 */
mxGraph.prototype.isExtendParentsOnMove = function()
{
	return this.extendParentsOnMove;
};

/**
 * Function: setExtendParentsOnMove
 * 
 * Sets <extendParentsOnMove>.
 * 
 * Parameters:
 * 
 * value - New boolean value for <extendParentsOnAdd>.
 */
mxGraph.prototype.setExtendParentsOnMove = function(value)
{
	this.extendParentsOnMove = value;
};

/**
 * Function: isRecursiveResize
 * 
 * Returns <recursiveResize>.
 * 
 * Parameters:
 * 
 * state - <mxCellState> that is being resized.
 */
mxGraph.prototype.isRecursiveResize = function(state)
{
	return this.recursiveResize;
};

/**
 * Function: setRecursiveResize
 * 
 * Sets <recursiveResize>.
 * 
 * Parameters:
 * 
 * value - New boolean value for <recursiveResize>.
 */
mxGraph.prototype.setRecursiveResize = function(value)
{
	this.recursiveResize = value;
};

/**
 * Function: isConstrainChild
 * 
 * Returns true if the given cell should be kept inside the bounds of its
 * parent according to the rules defined by <getOverlap> and
 * <isAllowOverlapParent>. This implementation returns false for all children
 * of edges and <isConstrainChildren> otherwise.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that should be constrained.
 */
mxGraph.prototype.isConstrainChild = function(cell)
{
	return this.isConstrainChildren() && !this.getModel().isEdge(this.getModel().getParent(cell));
};

/**
 * Function: isConstrainChildren
 * 
 * Returns <constrainChildren>.
 */
mxGraph.prototype.isConstrainChildren = function()
{
	return this.constrainChildren;
};

/**
 * Function: setConstrainChildren
 * 
 * Sets <constrainChildren>.
 */
mxGraph.prototype.setConstrainChildren = function(value)
{
	this.constrainChildren = value;
};

/**
 * Function: isConstrainRelativeChildren
 * 
 * Returns <constrainRelativeChildren>.
 */
mxGraph.prototype.isConstrainRelativeChildren = function()
{
	return this.constrainRelativeChildren;
};

/**
 * Function: setConstrainRelativeChildren
 * 
 * Sets <constrainRelativeChildren>.
 */
mxGraph.prototype.setConstrainRelativeChildren = function(value)
{
	this.constrainRelativeChildren = value;
};

/**
 * Function: isConstrainChildren
 * 
 * Returns <allowNegativeCoordinates>.
 */
mxGraph.prototype.isAllowNegativeCoordinates = function()
{
	return this.allowNegativeCoordinates;
};

/**
 * Function: setConstrainChildren
 * 
 * Sets <allowNegativeCoordinates>.
 */
mxGraph.prototype.setAllowNegativeCoordinates = function(value)
{
	this.allowNegativeCoordinates = value;
};

/**
 * Function: getOverlap
 * 
 * Returns a decimal number representing the amount of the width and height
 * of the given cell that is allowed to overlap its parent. A value of 0
 * means all children must stay inside the parent, 1 means the child is
 * allowed to be placed outside of the parent such that it touches one of
 * the parents sides. If <isAllowOverlapParent> returns false for the given
 * cell, then this method returns 0.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which the overlap ratio should be returned.
 */
mxGraph.prototype.getOverlap = function(cell)
{
	return (this.isAllowOverlapParent(cell)) ? this.defaultOverlap : 0;
};
	
/**
 * Function: isAllowOverlapParent
 * 
 * Returns true if the given cell is allowed to be placed outside of the
 * parents area.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that represents the child to be checked.
 */
mxGraph.prototype.isAllowOverlapParent = function(cell)
{
	return false;
};

/**
 * Function: getFoldableCells
 * 
 * Returns the cells which are movable in the given array of cells.
 */
mxGraph.prototype.getFoldableCells = function(cells, collapse)
{
	return this.model.filterCells(cells, mxUtils.bind(this, function(cell)
	{
		return this.isCellFoldable(cell, collapse);
	}));
};

/**
 * Function: isCellFoldable
 * 
 * Returns true if the given cell is foldable. This implementation
 * returns true if the cell has at least one child and its style
 * does not specify <mxConstants.STYLE_FOLDABLE> to be 0.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose foldable state should be returned.
 */
mxGraph.prototype.isCellFoldable = function(cell, collapse)
{
	var style = this.getCurrentCellStyle(cell);
	
	return this.model.getChildCount(cell) > 0 && style[mxConstants.STYLE_FOLDABLE] != 0;
};

/**
 * Function: isValidDropTarget
 *
 * Returns true if the given cell is a valid drop target for the specified
 * cells. If <splitEnabled> is true then this returns <isSplitTarget> for
 * the given arguments else it returns true if the cell is not collapsed
 * and its child count is greater than 0.
 * 
 * Parameters:
 * 
 * cell - <mxCell> that represents the possible drop target.
 * cells - <mxCells> that should be dropped into the target.
 * evt - Mouseevent that triggered the invocation.
 */
mxGraph.prototype.isValidDropTarget = function(cell, cells, evt)
{
	return cell != null && ((this.isSplitEnabled() &&
		this.isSplitTarget(cell, cells, evt)) || (!this.model.isEdge(cell) &&
		(this.isSwimlane(cell) || (this.model.getChildCount(cell) > 0 &&
		!this.isCellCollapsed(cell)))));
};

/**
 * Function: isSplitTarget
 *
 * Returns true if the given edge may be splitted into two edges with the
 * given cell as a new terminal between the two.
 * 
 * Parameters:
 * 
 * target - <mxCell> that represents the edge to be splitted.
 * cells - <mxCells> that should split the edge.
 * evt - Mouseevent that triggered the invocation.
 */
mxGraph.prototype.isSplitTarget = function(target, cells, evt)
{
	if (this.model.isEdge(target) && cells != null && cells.length == 1 &&
		this.isCellConnectable(cells[0]) && this.getEdgeValidationError(target,
			this.model.getTerminal(target, true), cells[0]) == null)
	{
		var src = this.model.getTerminal(target, true);
		var trg = this.model.getTerminal(target, false);

		return (!this.model.isAncestor(cells[0], src) &&
				!this.model.isAncestor(cells[0], trg));
	}

	return false;
};

/**
 * Function: getDropTarget
 * 
 * Returns the given cell if it is a drop target for the given cells or the
 * nearest ancestor that may be used as a drop target for the given cells.
 * If the given array contains a swimlane and <swimlaneNesting> is false
 * then this always returns null. If no cell is given, then the bottommost
 * swimlane at the location of the given event is returned.
 * 
 * This function should only be used if <isDropEnabled> returns true.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> which are to be dropped onto the target.
 * evt - Mouseevent for the drag and drop.
 * cell - <mxCell> that is under the mousepointer.
 * clone - Optional boolean to indicate of cells will be cloned.
 */
mxGraph.prototype.getDropTarget = function(cells, evt, cell, clone)
{
	if (!this.isSwimlaneNesting())
	{
		for (var i = 0; i < cells.length; i++)
		{
			if (this.isSwimlane(cells[i]))
			{
				return null;
			}
		}
	}

	var pt = mxUtils.convertPoint(this.container,
		mxEvent.getClientX(evt), mxEvent.getClientY(evt));
	pt.x -= this.panDx;
	pt.y -= this.panDy;
	var swimlane = this.getSwimlaneAt(pt.x, pt.y);
	
	if (cell == null)
	{
		cell = swimlane;
	}
	else if (swimlane != null)
	{
		// Checks if the cell is an ancestor of the swimlane
		// under the mouse and uses the swimlane in that case
		var tmp = this.model.getParent(swimlane);
		
		while (tmp != null && this.isSwimlane(tmp) && tmp != cell)
		{
			tmp = this.model.getParent(tmp);
		}
		
		if (tmp == cell)
		{
			cell = swimlane;
		}
	}
	
	while (cell != null && !this.isValidDropTarget(cell, cells, evt) &&
		!this.model.isLayer(cell))
	{
		cell = this.model.getParent(cell);
	}
	
	// Checks if parent is dropped into child if not cloning
	if (clone == null || !clone)
	{
		var parent = cell;
		
		while (parent != null && mxUtils.indexOf(cells, parent) < 0)
		{
			parent = this.model.getParent(parent);
		}
	}

	return (!this.model.isLayer(cell) && parent == null) ? cell : null;
};

/**
 * Group: Cell retrieval
 */

/**
 * Function: getDefaultParent
 * 
 * Returns <defaultParent> or <mxGraphView.currentRoot> or the first child
 * child of <mxGraphModel.root> if both are null. The value returned by
 * this function should be used as the parent for new cells (aka default
 * layer).
 */
mxGraph.prototype.getDefaultParent = function()
{
	var parent = this.getCurrentRoot();
	
	if (parent == null)
	{
		parent = this.defaultParent;
		
		if (parent == null)
		{
			var root = this.model.getRoot();
			parent = this.model.getChildAt(root, 0);
		}
	}
	
	return parent;
};

/**
 * Function: setDefaultParent
 * 
 * Sets the <defaultParent> to the given cell. Set this to null to return
 * the first child of the root in getDefaultParent.
 */
mxGraph.prototype.setDefaultParent = function(cell)
{
	this.defaultParent = cell;
};

/**
 * Function: getSwimlane
 * 
 * Returns the nearest ancestor of the given cell which is a swimlane, or
 * the given cell, if it is itself a swimlane.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which the ancestor swimlane should be returned.
 */
mxGraph.prototype.getSwimlane = function(cell)
{
	while (cell != null && !this.isSwimlane(cell))
	{
		cell = this.model.getParent(cell);
	}
	
	return cell;
};

/**
 * Function: getSwimlaneAt
 * 
 * Returns the bottom-most swimlane that intersects the given point (x, y)
 * in the cell hierarchy that starts at the given parent.
 * 
 * Parameters:
 * 
 * x - X-coordinate of the location to be checked.
 * y - Y-coordinate of the location to be checked.
 * parent - <mxCell> that should be used as the root of the recursion.
 * Default is <defaultParent>.
 */
mxGraph.prototype.getSwimlaneAt = function (x, y, parent)
{
	if (parent == null)
	{
		parent = this.getCurrentRoot();
		
		if (parent == null)
		{
			parent = this.model.getRoot();
		}
	}
	
	if (parent != null)
	{
		var childCount = this.model.getChildCount(parent);
		
		for (var i = 0; i < childCount; i++)
		{
			var child = this.model.getChildAt(parent, i);
			
			if (child != null)
			{
				var result = this.getSwimlaneAt(x, y, child);
				
				if (result != null)
				{
					return result;
				}
				else if (this.isCellVisible(child) && this.isSwimlane(child))
				{
					var state = this.view.getState(child);
					
					if (this.intersects(state, x, y))
					{
						return child;
					}
				}
			}
		}
	}
	
	return null;
};

/**
 * Function: getCellAt
 * 
 * Returns the bottom-most cell that intersects the given point (x, y) in
 * the cell hierarchy starting at the given parent. This will also return
 * swimlanes if the given location intersects the content area of the
 * swimlane. If this is not desired, then the <hitsSwimlaneContent> may be
 * used if the returned cell is a swimlane to determine if the location
 * is inside the content area or on the actual title of the swimlane.
 * 
 * Parameters:
 * 
 * x - X-coordinate of the location to be checked.
 * y - Y-coordinate of the location to be checked.
 * parent - <mxCell> that should be used as the root of the recursion.
 * Default is current root of the view or the root of the model.
 * vertices - Optional boolean indicating if vertices should be returned.
 * Default is true.
 * edges - Optional boolean indicating if edges should be returned. Default
 * is true.
 * ignoreFn - Optional function that returns true if cell should be ignored.
 * The function is passed the cell state and the x and y parameter.
 */
mxGraph.prototype.getCellAt = function(x, y, parent, vertices, edges, ignoreFn)
{
	vertices = (vertices != null) ? vertices : true;
	edges = (edges != null) ? edges : true;

	if (parent == null)
	{
		parent = this.getCurrentRoot();
		
		if (parent == null)
		{
			parent = this.getModel().getRoot();
		}
	}

	if (parent != null)
	{
		var childCount = this.model.getChildCount(parent);
		
		for (var i = childCount - 1; i >= 0; i--)
		{
			var cell = this.model.getChildAt(parent, i);
			var result = this.getCellAt(x, y, cell, vertices, edges, ignoreFn);
			
			if (result != null)
			{
				return result;
			}
			else if (this.isCellVisible(cell) && (edges && this.model.isEdge(cell) ||
				vertices && this.model.isVertex(cell)))
			{
				var state = this.view.getState(cell);

				if (state != null && (ignoreFn == null || !ignoreFn(state, x, y)) &&
					this.intersects(state, x, y))
				{
					return cell;
				}
			}
		}
	}
	
	return null;
};

/**
 * Function: intersects
 * 
 * Returns the bottom-most cell that intersects the given point (x, y) in
 * the cell hierarchy that starts at the given parent.
 * 
 * Parameters:
 * 
 * state - <mxCellState> that represents the cell state.
 * x - X-coordinate of the location to be checked.
 * y - Y-coordinate of the location to be checked.
 */
mxGraph.prototype.intersects = function(state, x, y)
{
	if (state != null)
	{
		var pts = state.absolutePoints;

		if (pts != null)
		{
			var t2 = this.tolerance * this.tolerance;
			var pt = pts[0];
			
			for (var i = 1; i < pts.length; i++)
			{
				var next = pts[i];
				var dist = mxUtils.ptSegDistSq(pt.x, pt.y, next.x, next.y, x, y);
				
				if (dist <= t2)
				{
					return true;
				}
				
				pt = next;
			}
		}
		else
		{
			var alpha = mxUtils.toRadians(mxUtils.getValue(state.style, mxConstants.STYLE_ROTATION) || 0);
			
			if (alpha != 0)
			{
				var cos = Math.cos(-alpha);
				var sin = Math.sin(-alpha);
				var cx = new mxPoint(state.getCenterX(), state.getCenterY());
				var pt = mxUtils.getRotatedPoint(new mxPoint(x, y), cos, sin, cx);
				x = pt.x;
				y = pt.y;
			}
			
			if (mxUtils.contains(state, x, y))
			{
				return true;
			}
		}
	}
	
	return false;
};

/**
 * Function: hitsSwimlaneContent
 * 
 * Returns true if the given coordinate pair is inside the content
 * are of the given swimlane.
 * 
 * Parameters:
 * 
 * swimlane - <mxCell> that specifies the swimlane.
 * x - X-coordinate of the mouse event.
 * y - Y-coordinate of the mouse event.
 */
mxGraph.prototype.hitsSwimlaneContent = function(swimlane, x, y)
{
	var state = this.getView().getState(swimlane);
	var size = this.getStartSize(swimlane);
	
	if (state != null)
	{
		var scale = this.getView().getScale();
		x -= state.x;
		y -= state.y;
		
		if (size.width > 0 && x > 0 && x > size.width * scale)
		{
			return true;
		}
		else if (size.height > 0 && y > 0 && y > size.height * scale)
		{
			return true;
		}
	}
	
	return false;
};

/**
 * Function: getChildVertices
 * 
 * Returns the visible child vertices of the given parent.
 * 
 * Parameters:
 * 
 * parent - <mxCell> whose children should be returned.
 */
mxGraph.prototype.getChildVertices = function(parent)
{
	return this.getChildCells(parent, true, false);
};
	
/**
 * Function: getChildEdges
 * 
 * Returns the visible child edges of the given parent.
 * 
 * Parameters:
 * 
 * parent - <mxCell> whose child vertices should be returned.
 */
mxGraph.prototype.getChildEdges = function(parent)
{
	return this.getChildCells(parent, false, true);
};

/**
 * Function: getChildCells
 * 
 * Returns the visible child vertices or edges in the given parent. If
 * vertices and edges is false, then all children are returned.
 * 
 * Parameters:
 * 
 * parent - <mxCell> whose children should be returned.
 * vertices - Optional boolean that specifies if child vertices should
 * be returned. Default is false.
 * edges - Optional boolean that specifies if child edges should
 * be returned. Default is false.
 */
mxGraph.prototype.getChildCells = function(parent, vertices, edges)
{
	parent = (parent != null) ? parent : this.getDefaultParent();
	vertices = (vertices != null) ? vertices : false;
	edges = (edges != null) ? edges : false;

	var cells = this.model.getChildCells(parent, vertices, edges);
	var result = [];

	// Filters out the non-visible child cells
	for (var i = 0; i < cells.length; i++)
	{
		if (this.isCellVisible(cells[i]))
		{
			result.push(cells[i]);
		}
	}

	return result;
};
	
/**
 * Function: getConnections
 * 
 * Returns all visible edges connected to the given cell without loops.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose connections should be returned.
 * parent - Optional parent of the opposite end for a connection to be
 * returned.
 */
mxGraph.prototype.getConnections = function(cell, parent)
{
	return this.getEdges(cell, parent, true, true, false);
};
	
/**
 * Function: getIncomingEdges
 * 
 * Returns the visible incoming edges for the given cell. If the optional
 * parent argument is specified, then only child edges of the given parent
 * are returned.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose incoming edges should be returned.
 * parent - Optional parent of the opposite end for an edge to be
 * returned.
 */
mxGraph.prototype.getIncomingEdges = function(cell, parent)
{
	return this.getEdges(cell, parent, true, false, false);
};
	
/**
 * Function: getOutgoingEdges
 * 
 * Returns the visible outgoing edges for the given cell. If the optional
 * parent argument is specified, then only child edges of the given parent
 * are returned.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose outgoing edges should be returned.
 * parent - Optional parent of the opposite end for an edge to be
 * returned.
 */
mxGraph.prototype.getOutgoingEdges = function(cell, parent)
{
	return this.getEdges(cell, parent, false, true, false);
};
	
/**
 * Function: getEdges
 * 
 * Returns the incoming and/or outgoing edges for the given cell.
 * If the optional parent argument is specified, then only edges are returned
 * where the opposite is in the given parent cell. If at least one of incoming
 * or outgoing is true, then loops are ignored, if both are false, then all
 * edges connected to the given cell are returned including loops.
 * 
 * Parameters:
 * 
 * cell - <mxCell> whose edges should be returned.
 * parent - Optional parent of the opposite end for an edge to be
 * returned.
 * incoming - Optional boolean that specifies if incoming edges should
 * be included in the result. Default is true.
 * outgoing - Optional boolean that specifies if outgoing edges should
 * be included in the result. Default is true.
 * includeLoops - Optional boolean that specifies if loops should be
 * included in the result. Default is true.
 * recurse - Optional boolean the specifies if the parent specified only 
 * need be an ancestral parent, true, or the direct parent, false.
 * Default is false
 */
mxGraph.prototype.getEdges = function(cell, parent, incoming, outgoing, includeLoops, recurse)
{
	incoming = (incoming != null) ? incoming : true;
	outgoing = (outgoing != null) ? outgoing : true;
	includeLoops = (includeLoops != null) ? includeLoops : true;
	recurse = (recurse != null) ? recurse : false;
	
	var edges = [];
	var isCollapsed = this.isCellCollapsed(cell);
	var childCount = this.model.getChildCount(cell);

	for (var i = 0; i < childCount; i++)
	{
		var child = this.model.getChildAt(cell, i);

		if (isCollapsed || !this.isCellVisible(child))
		{
			edges = edges.concat(this.model.getEdges(child, incoming, outgoing));
		}
	}

	edges = edges.concat(this.model.getEdges(cell, incoming, outgoing));
	var result = [];
	
	for (var i = 0; i < edges.length; i++)
	{
		var state = this.view.getState(edges[i]);
		
		var source = (state != null) ? state.getVisibleTerminal(true) : this.view.getVisibleTerminal(edges[i], true);
		var target = (state != null) ? state.getVisibleTerminal(false) : this.view.getVisibleTerminal(edges[i], false);

		if ((includeLoops && source == target) || ((source != target) && ((incoming &&
			target == cell && (parent == null || this.isValidAncestor(source, parent, recurse))) ||
			(outgoing && source == cell && (parent == null ||
					this.isValidAncestor(target, parent, recurse))))))
		{
			result.push(edges[i]);
		}
	}

	return result;
};

/**
 * Function: isValidAncestor
 * 
 * Returns whether or not the specified parent is a valid
 * ancestor of the specified cell, either direct or indirectly
 * based on whether ancestor recursion is enabled.
 * 
 * Parameters:
 * 
 * cell - <mxCell> the possible child cell
 * parent - <mxCell> the possible parent cell
 * recurse - boolean whether or not to recurse the child ancestors
 */
mxGraph.prototype.isValidAncestor = function(cell, parent, recurse)
{
	return (recurse ? this.model.isAncestor(parent, cell) : this.model
			.getParent(cell) == parent);
};

/**
 * Function: getOpposites
 * 
 * Returns all distinct visible opposite cells for the specified terminal
 * on the given edges.
 * 
 * Parameters:
 * 
 * edges - Array of <mxCells> that contains the edges whose opposite
 * terminals should be returned.
 * terminal - Terminal that specifies the end whose opposite should be
 * returned.
 * sources - Optional boolean that specifies if source terminals should be
 * included in the result. Default is true.
 * targets - Optional boolean that specifies if targer terminals should be
 * included in the result. Default is true.
 */
mxGraph.prototype.getOpposites = function(edges, terminal, sources, targets)
{
	sources = (sources != null) ? sources : true;
	targets = (targets != null) ? targets : true;
	
	var terminals = [];
	
	// Fast lookup to avoid duplicates in terminals array
	var dict = new mxDictionary();
	
	if (edges != null)
	{
		for (var i = 0; i < edges.length; i++)
		{
			var state = this.view.getState(edges[i]);
			
			var source = (state != null) ? state.getVisibleTerminal(true) : this.view.getVisibleTerminal(edges[i], true);
			var target = (state != null) ? state.getVisibleTerminal(false) : this.view.getVisibleTerminal(edges[i], false);
			
			// Checks if the terminal is the source of the edge and if the
			// target should be stored in the result
			if (source == terminal && target != null && target != terminal && targets)
			{
				if (!dict.get(target))
				{
					dict.put(target, true);
					terminals.push(target);
				}
			}
			
			// Checks if the terminal is the taget of the edge and if the
			// source should be stored in the result
			else if (target == terminal && source != null && source != terminal && sources)
			{
				if (!dict.get(source))
				{
					dict.put(source, true);
					terminals.push(source);
				}
			}
		}
	}
	
	return terminals;
};

/**
 * Function: getEdgesBetween
 * 
 * Returns the edges between the given source and target. This takes into
 * account collapsed and invisible cells and returns the connected edges
 * as displayed on the screen.
 * 
 * Parameters:
 * 
 * source -
 * target -
 * directed -
 */
mxGraph.prototype.getEdgesBetween = function(source, target, directed)
{
	directed = (directed != null) ? directed : false;
	var edges = this.getEdges(source);
	var result = [];

	// Checks if the edge is connected to the correct
	// cell and returns the first match
	for (var i = 0; i < edges.length; i++)
	{
		var state = this.view.getState(edges[i]);
		
		var src = (state != null) ? state.getVisibleTerminal(true) : this.view.getVisibleTerminal(edges[i], true);
		var trg = (state != null) ? state.getVisibleTerminal(false) : this.view.getVisibleTerminal(edges[i], false);

		if ((src == source && trg == target) || (!directed && src == target && trg == source))
		{
			result.push(edges[i]);
		}
	}

	return result;
};

/**
 * Function: getPointForEvent
 * 
 * Returns an <mxPoint> representing the given event in the unscaled,
 * non-translated coordinate space of <container> and applies the grid.
 * 
 * Parameters:
 * 
 * evt - Mousevent that contains the mouse pointer location.
 * addOffset - Optional boolean that specifies if the position should be
 * offset by half of the <gridSize>. Default is true.
 */
 mxGraph.prototype.getPointForEvent = function(evt, addOffset)
 {
	var p = mxUtils.convertPoint(this.container,
		mxEvent.getClientX(evt), mxEvent.getClientY(evt));
	
	var s = this.view.scale;
	var tr = this.view.translate;
	var off = (addOffset != false) ? this.gridSize / 2 : 0;
	
	p.x = this.snap(p.x / s - tr.x - off);
	p.y = this.snap(p.y / s - tr.y - off);
	
	return p;
};

/**
 * Function: getCells
 * 
 * Returns the child vertices and edges of the given parent that are contained
 * in the given rectangle. The result is added to the optional result array,
 * which is returned. If no result array is specified then a new array is
 * created and returned.
 * 
 * Parameters:
 * 
 * x - X-coordinate of the rectangle.
 * y - Y-coordinate of the rectangle.
 * width - Width of the rectangle.
 * height - Height of the rectangle.
 * parent - <mxCell> that should be used as the root of the recursion.
 * Default is current root of the view or the root of the model.
 * result - Optional array to store the result in.
 * intersection - Optional <mxRectangle> to check vertices for intersection.
 * ignoreFn - Optional function to check if a cell state is ignored.
 * includeDescendants - Optional boolean flag to add descendants to the result.
 * Default is false.
 */
mxGraph.prototype.getCells = function(x, y, width, height, parent, result, intersection, ignoreFn, includeDescendants)
{
	result = (result != null) ? result : [];
	
	if (width > 0 || height > 0 || intersection != null)
	{
		var model = this.getModel();
		var right = x + width;
		var bottom = y + height;

		if (parent == null)
		{
			parent = this.getCurrentRoot();
			
			if (parent == null)
			{
				parent = model.getRoot();
			}
		}
		
		if (parent != null)
		{
			var childCount = model.getChildCount(parent);
			
			for (var i = 0; i < childCount; i++)
			{
				var cell = model.getChildAt(parent, i);
				var state = this.view.getState(cell);
				
				if (state != null && this.isCellVisible(cell) &&
					(ignoreFn == null || !ignoreFn(state)))
				{
					var deg = mxUtils.getValue(state.style, mxConstants.STYLE_ROTATION) || 0;
					var box = state;
					
					if (deg != 0)
					{
						box = mxUtils.getBoundingBox(box, deg);
					}
					
					var hit = (intersection != null && model.isVertex(cell) && mxUtils.intersects(intersection, box)) ||
						(intersection == null && (model.isEdge(cell) || model.isVertex(cell)) &&
						box.x >= x && box.y + box.height <= bottom &&
						box.y >= y && box.x + box.width <= right);
					
					if (hit)
					{
						result.push(cell);
					}
					
					if (!hit || includeDescendants)
					{
						this.getCells(x, y, width, height, cell, result, intersection, ignoreFn, includeDescendants);
					}
				}
			}
		}
	}
	
	return result;
};

/**
 * Function: getCellsBeyond
 * 
 * Returns the children of the given parent that are contained in the
 * halfpane from the given point (x0, y0) rightwards or downwards
 * depending on rightHalfpane and bottomHalfpane.
 * 
 * Parameters:
 * 
 * x0 - X-coordinate of the origin.
 * y0 - Y-coordinate of the origin.
 * parent - Optional <mxCell> whose children should be checked. Default is
 * <defaultParent>.
 * rightHalfpane - Boolean indicating if the cells in the right halfpane
 * from the origin should be returned.
 * bottomHalfpane - Boolean indicating if the cells in the bottom halfpane
 * from the origin should be returned.
 */
mxGraph.prototype.getCellsBeyond = function(x0, y0, parent, rightHalfpane, bottomHalfpane)
{
	var result = [];
	
	if (rightHalfpane || bottomHalfpane)
	{
		if (parent == null)
		{
			parent = this.getDefaultParent();
		}
		
		if (parent != null)
		{
			var childCount = this.model.getChildCount(parent);
			
			for (var i = 0; i < childCount; i++)
			{
				var child = this.model.getChildAt(parent, i);
				var state = this.view.getState(child);
				
				if (this.isCellVisible(child) && state != null)
				{
					if ((!rightHalfpane || state.x >= x0) &&
						(!bottomHalfpane || state.y >= y0))
					{
						result.push(child);
					}
				}
			}
		}
	}
	
	return result;
};

/**
 * Function: findTreeRoots
 * 
 * Returns all children in the given parent which do not have incoming
 * edges. If the result is empty then the with the greatest difference
 * between incoming and outgoing edges is returned.
 * 
 * Parameters:
 * 
 * parent - <mxCell> whose children should be checked.
 * isolate - Optional boolean that specifies if edges should be ignored if
 * the opposite end is not a child of the given parent cell. Default is
 * false.
 * invert - Optional boolean that specifies if outgoing or incoming edges
 * should be counted for a tree root. If false then outgoing edges will be
 * counted. Default is false.
 */
mxGraph.prototype.findTreeRoots = function(parent, isolate, invert)
{
	isolate = (isolate != null) ? isolate : false;
	invert = (invert != null) ? invert : false;
	var roots = [];
	
	if (parent != null)
	{
		var model = this.getModel();
		var childCount = model.getChildCount(parent);
		var best = null;
		var maxDiff = 0;
		
		for (var i=0; i<childCount; i++)
		{
			var cell = model.getChildAt(parent, i);
			
			if (this.model.isVertex(cell) && this.isCellVisible(cell))
			{
				var conns = this.getConnections(cell, (isolate) ? parent : null);
				var fanOut = 0;
				var fanIn = 0;
				
				for (var j = 0; j < conns.length; j++)
				{
					var src = this.view.getVisibleTerminal(conns[j], true);

                    if (src == cell)
                    {
                        fanOut++;
                    }
                    else
                    {
                        fanIn++;
                    }
				}
				
				if ((invert && fanOut == 0 && fanIn > 0) ||
					(!invert && fanIn == 0 && fanOut > 0))
				{
					roots.push(cell);
				}
				
				var diff = (invert) ? fanIn - fanOut : fanOut - fanIn;
				
				if (diff > maxDiff)
				{
					maxDiff = diff;
					best = cell;
				}
			}
		}
		
		if (roots.length == 0 && best != null)
		{
			roots.push(best);
		}
	}
	
	return roots;
};

/**
 * Function: traverse
 * 
 * Traverses the (directed) graph invoking the given function for each
 * visited vertex and edge. The function is invoked with the current vertex
 * and the incoming edge as a parameter. This implementation makes sure
 * each vertex is only visited once. The function may return false if the
 * traversal should stop at the given vertex.
 * 
 * Example:
 * 
 * (code)
 * mxLog.show();
 * var cell = graph.getSelectionCell();
 * graph.traverse(cell, false, function(vertex, edge)
 * {
 *   mxLog.debug(graph.getLabel(vertex));
 * });
 * (end)
 * 
 * Parameters:
 * 
 * vertex - <mxCell> that represents the vertex where the traversal starts.
 * directed - Optional boolean indicating if edges should only be traversed
 * from source to target. Default is true.
 * func - Visitor function that takes the current vertex and the incoming
 * edge as arguments. The traversal stops if the function returns false.
 * edge - Optional <mxCell> that represents the incoming edge. This is
 * null for the first step of the traversal.
 * visited - Optional <mxDictionary> from cells to true for the visited cells.
 * inverse - Optional boolean to traverse in inverse direction. Default is false.
 * This is ignored if directed is false.
 */
mxGraph.prototype.traverse = function(vertex, directed, func, edge, visited, inverse)
{
	if (func != null && vertex != null)
	{
		directed = (directed != null) ? directed : true;
		inverse = (inverse != null) ? inverse : false;
		visited = visited || new mxDictionary();
		
		if (!visited.get(vertex))
		{
			visited.put(vertex, true);
			var result = func(vertex, edge);
			
			if (result == null || result)
			{
				var edgeCount = this.model.getEdgeCount(vertex);
				
				if (edgeCount > 0)
				{
					for (var i = 0; i < edgeCount; i++)
					{
						var e = this.model.getEdgeAt(vertex, i);
						var isSource = this.model.getTerminal(e, true) == vertex;
						
						if (!directed || (!inverse == isSource))
						{
							var next = this.model.getTerminal(e, !isSource);
							this.traverse(next, directed, func, e, visited, inverse);
						}
					}
				}
			}
		}
	}
};

/**
 * Group: Selection
 */

/**
 * Function: isCellSelected
 * 
 * Returns true if the given cell is selected.
 * 
 * Parameters:
 * 
 * cell - <mxCell> for which the selection state should be returned.
 */
mxGraph.prototype.isCellSelected = function(cell)
{
	return this.getSelectionModel().isSelected(cell);
};

/**
 * Function: isSelectionEmpty
 * 
 * Returns true if the selection is empty.
 */
mxGraph.prototype.isSelectionEmpty = function()
{
	return this.getSelectionModel().isEmpty();
};

/**
 * Function: clearSelection
 * 
 * Clears the selection using <mxGraphSelectionModel.clear>.
 */
mxGraph.prototype.clearSelection = function()
{
	return this.getSelectionModel().clear();
};

/**
 * Function: getSelectionCount
 * 
 * Returns the number of selected cells.
 */
mxGraph.prototype.getSelectionCount = function()
{
	return this.getSelectionModel().cells.length;
};
	
/**
 * Function: getSelectionCell
 * 
 * Returns the first cell from the array of selected <mxCells>.
 */
mxGraph.prototype.getSelectionCell = function()
{
	return this.getSelectionModel().cells[0];
};

/**
 * Function: getSelectionCells
 * 
 * Returns the array of selected <mxCells>.
 */
mxGraph.prototype.getSelectionCells = function()
{
	return this.getSelectionModel().cells.slice();
};

/**
 * Function: setSelectionCell
 * 
 * Sets the selection cell.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to be selected.
 */
mxGraph.prototype.setSelectionCell = function(cell)
{
	this.getSelectionModel().setCell(cell);
};

/**
 * Function: setSelectionCells
 * 
 * Sets the selection cell.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to be selected.
 */
mxGraph.prototype.setSelectionCells = function(cells)
{
	this.getSelectionModel().setCells(cells);
};

/**
 * Function: addSelectionCell
 * 
 * Adds the given cell to the selection.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to be add to the selection.
 */
mxGraph.prototype.addSelectionCell = function(cell)
{
	this.getSelectionModel().addCell(cell);
};

/**
 * Function: addSelectionCells
 * 
 * Adds the given cells to the selection.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to be added to the selection.
 */
mxGraph.prototype.addSelectionCells = function(cells)
{
	this.getSelectionModel().addCells(cells);
};

/**
 * Function: removeSelectionCell
 * 
 * Removes the given cell from the selection.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to be removed from the selection.
 */
mxGraph.prototype.removeSelectionCell = function(cell)
{
	this.getSelectionModel().removeCell(cell);
};

/**
 * Function: removeSelectionCells
 * 
 * Removes the given cells from the selection.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to be removed from the selection.
 */
mxGraph.prototype.removeSelectionCells = function(cells)
{
	this.getSelectionModel().removeCells(cells);
};

/**
 * Function: selectRegion
 * 
 * Selects and returns the cells inside the given rectangle for the
 * specified event.
 * 
 * Parameters:
 * 
 * rect - <mxRectangle> that represents the region to be selected.
 * evt - Mouseevent that triggered the selection.
 */
mxGraph.prototype.selectRegion = function(rect, evt)
{
	var cells = this.getCells(rect.x, rect.y, rect.width, rect.height);
	this.selectCellsForEvent(cells, evt);
	
	return cells;
};

/**
 * Function: selectNextCell
 * 
 * Selects the next cell.
 */
mxGraph.prototype.selectNextCell = function()
{
	this.selectCell(true);
};

/**
 * Function: selectPreviousCell
 * 
 * Selects the previous cell.
 */
mxGraph.prototype.selectPreviousCell = function()
{
	this.selectCell();
};

/**
 * Function: selectParentCell
 * 
 * Selects the parent cell.
 */
mxGraph.prototype.selectParentCell = function()
{
	this.selectCell(false, true);
};

/**
 * Function: selectChildCell
 * 
 * Selects the first child cell.
 */
mxGraph.prototype.selectChildCell = function()
{
	this.selectCell(false, false, true);
};

/**
 * Function: selectCell
 * 
 * Selects the next, parent, first child or previous cell, if all arguments
 * are false.
 * 
 * Parameters:
 * 
 * isNext - Boolean indicating if the next cell should be selected.
 * isParent - Boolean indicating if the parent cell should be selected.
 * isChild - Boolean indicating if the first child cell should be selected.
 */
mxGraph.prototype.selectCell = function(isNext, isParent, isChild)
{
	var sel = this.selectionModel;
	var cell = (sel.cells.length > 0) ? sel.cells[0] : null;
	
	if (sel.cells.length > 1)
	{
		sel.clear();
	}
	
	var parent = (cell != null) ?
		this.model.getParent(cell) :
		this.getDefaultParent();
	
	var childCount = this.model.getChildCount(parent);
	
	if (cell == null && childCount > 0)
	{
		var child = this.model.getChildAt(parent, 0);
		this.setSelectionCell(child);
	}
	else if ((cell == null || isParent) &&
		this.view.getState(parent) != null &&
		this.model.getGeometry(parent) != null)
	{
		if (this.getCurrentRoot() != parent)
		{
			this.setSelectionCell(parent);
		}
	}
	else if (cell != null && isChild)
	{
		var tmp = this.model.getChildCount(cell);
		
		if (tmp > 0)
		{
			var child = this.model.getChildAt(cell, 0);
			this.setSelectionCell(child);
		}
	}
	else if (childCount > 0)
	{
		var i = parent.getIndex(cell);
		
		if (isNext)
		{
			i++;
			var child = this.model.getChildAt(parent, i % childCount);
			this.setSelectionCell(child);
		}
		else
		{
			i--;
			var index =  (i < 0) ? childCount - 1 : i;
			var child = this.model.getChildAt(parent, index);
			this.setSelectionCell(child);
		}
	}
};

/**
 * Function: selectAll
 * 
 * Selects all children of the given parent cell or the children of the
 * default parent if no parent is specified. To select leaf vertices and/or
 * edges use <selectCells>.
 * 
 * Parameters:
 * 
 * parent - Optional <mxCell> whose children should be selected.
 * Default is <defaultParent>.
 * descendants - Optional boolean specifying whether all descendants should be
 * selected. Default is false.
 */
mxGraph.prototype.selectAll = function(parent, descendants)
{
	parent = parent || this.getDefaultParent();
	
	var cells = (descendants) ? this.model.filterDescendants(mxUtils.bind(this, function(cell)
	{
		return cell != parent && this.view.getState(cell) != null;
	}), parent) : this.model.getChildren(parent);
	
	if (cells != null)
	{
		this.setSelectionCells(cells);
	}
};

/**
 * Function: selectVertices
 * 
 * Select all vertices inside the given parent or the default parent.
 */
mxGraph.prototype.selectVertices = function(parent, selectGroups)
{
	this.selectCells(true, false, parent, selectGroups);
};

/**
 * Function: selectVertices
 * 
 * Select all vertices inside the given parent or the default parent.
 */
mxGraph.prototype.selectEdges = function(parent)
{
	this.selectCells(false, true, parent);
};

/**
 * Function: selectCells
 * 
 * Selects all vertices and/or edges depending on the given boolean
 * arguments recursively, starting at the given parent or the default
 * parent if no parent is specified. Use <selectAll> to select all cells.
 * For vertices, only cells with no children are selected.
 * 
 * Parameters:
 * 
 * vertices - Boolean indicating if vertices should be selected.
 * edges - Boolean indicating if edges should be selected.
 * parent - Optional <mxCell> that acts as the root of the recursion.
 * Default is <defaultParent>.
 * selectGroups - Optional boolean that specifies if groups should be
 * selected. Default is false.
 */
mxGraph.prototype.selectCells = function(vertices, edges, parent, selectGroups)
{
	parent = parent || this.getDefaultParent();
	
	var filter = mxUtils.bind(this, function(cell)
	{
		return this.view.getState(cell) != null &&
			(((selectGroups || this.model.getChildCount(cell) == 0) &&
			this.model.isVertex(cell) && vertices
			&& !this.model.isEdge(this.model.getParent(cell))) ||
			(this.model.isEdge(cell) && edges));
	});
	
	var cells = this.model.filterDescendants(filter, parent);
	
	if (cells != null)
	{
		this.setSelectionCells(cells);
	}
};

/**
 * Function: selectCellForEvent
 * 
 * Selects the given cell by either adding it to the selection or
 * replacing the selection depending on whether the given mouse event is a
 * toggle event.
 * 
 * Parameters:
 * 
 * cell - <mxCell> to be selected.
 * evt - Optional mouseevent that triggered the selection.
 */
mxGraph.prototype.selectCellForEvent = function(cell, evt)
{
	var isSelected = this.isCellSelected(cell);
	
	if (this.isToggleEvent(evt))
	{
		if (isSelected)
		{
			this.removeSelectionCell(cell);
		}
		else
		{
			this.addSelectionCell(cell);
		}
	}
	else if (!isSelected || this.getSelectionCount() != 1)
	{
		this.setSelectionCell(cell);
	}
};

/**
 * Function: selectCellsForEvent
 * 
 * Selects the given cells by either adding them to the selection or
 * replacing the selection depending on whether the given mouse event is a
 * toggle event.
 * 
 * Parameters:
 * 
 * cells - Array of <mxCells> to be selected.
 * evt - Optional mouseevent that triggered the selection.
 */
mxGraph.prototype.selectCellsForEvent = function(cells, evt)
{
	if (this.isToggleEvent(evt))
	{
		this.addSelectionCells(cells);
	}
	else
	{
		this.setSelectionCells(cells);
	}
};

/**
 * Group: Selection state
 */

/**
 * Function: createHandler
 * 
 * Creates a new handler for the given cell state. This implementation
 * returns a new <mxEdgeHandler> of the corresponding cell is an edge,
 * otherwise it returns an <mxVertexHandler>.
 * 
 * Parameters:
 * 
 * state - <mxCellState> whose handler should be created.
 */
mxGraph.prototype.createHandler = function(state)
{
	var result = null;
	
	if (state != null)
	{
		if (this.model.isEdge(state.cell))
		{
			var source = state.getVisibleTerminalState(true);
			var target = state.getVisibleTerminalState(false);
			var geo = this.getCellGeometry(state.cell);
			
			var edgeStyle = this.view.getEdgeStyle(state, (geo != null) ? geo.points : null, source, target);
			result = this.createEdgeHandler(state, edgeStyle);
		}
		else
		{
			result = this.createVertexHandler(state);
		}
	}
	
	return result;
};

/**
 * Function: createVertexHandler
 * 
 * Hooks to create a new <mxVertexHandler> for the given <mxCellState>.
 * 
 * Parameters:
 * 
 * state - <mxCellState> to create the handler for.
 */
mxGraph.prototype.createVertexHandler = function(state)
{
	return new mxVertexHandler(state);
};

/**
 * Function: createEdgeHandler
 * 
 * Hooks to create a new <mxEdgeHandler> for the given <mxCellState>.
 * 
 * Parameters:
 * 
 * state - <mxCellState> to create the handler for.
 */
mxGraph.prototype.createEdgeHandler = function(state, edgeStyle)
{
	var result = null;
	
	if (edgeStyle == mxEdgeStyle.Loop ||
		edgeStyle == mxEdgeStyle.ElbowConnector ||
		edgeStyle == mxEdgeStyle.SideToSide ||
		edgeStyle == mxEdgeStyle.TopToBottom)
	{
		result = this.createElbowEdgeHandler(state);
	}
	else if (edgeStyle == mxEdgeStyle.SegmentConnector || 
			edgeStyle == mxEdgeStyle.OrthConnector)
	{
		result = this.createEdgeSegmentHandler(state);
	}
	else
	{
		result = new mxEdgeHandler(state);
	}
	
	return result;
};

/**
 * Function: createEdgeSegmentHandler
 * 
 * Hooks to create a new <mxEdgeSegmentHandler> for the given <mxCellState>.
 * 
 * Parameters:
 * 
 * state - <mxCellState> to create the handler for.
 */
mxGraph.prototype.createEdgeSegmentHandler = function(state)
{
	return new mxEdgeSegmentHandler(state);
};

/**
 * Function: createElbowEdgeHandler
 * 
 * Hooks to create a new <mxElbowEdgeHandler> for the given <mxCellState>.
 * 
 * Parameters:
 * 
 * state - <mxCellState> to create the handler for.
 */
mxGraph.prototype.createElbowEdgeHandler = function(state)
{
	return new mxElbowEdgeHandler(state);
};

/**
 * Group: Graph events
 */

/**
 * Function: addMouseListener
 * 
 * Adds a listener to the graph event dispatch loop. The listener
 * must implement the mouseDown, mouseMove and mouseUp methods
 * as shown in the <mxMouseEvent> class.
 * 
 * Parameters:
 * 
 * listener - Listener to be added to the graph event listeners.
 */
mxGraph.prototype.addMouseListener = function(listener)
{
	if (this.mouseListeners == null)
	{
		this.mouseListeners = [];
	}
	
	this.mouseListeners.push(listener);
};

/**
 * Function: removeMouseListener
 * 
 * Removes the specified graph listener.
 * 
 * Parameters:
 * 
 * listener - Listener to be removed from the graph event listeners.
 */
mxGraph.prototype.removeMouseListener = function(listener)
{
	if (this.mouseListeners != null)
	{
		for (var i = 0; i < this.mouseListeners.length; i++)
		{
			if (this.mouseListeners[i] == listener)
			{
				this.mouseListeners.splice(i, 1);
				break;
			}
		}
	}
};

/**
 * Function: updateMouseEvent
 * 
 * Sets the graphX and graphY properties if the given <mxMouseEvent> if
 * required and returned the event.
 * 
 * Parameters:
 * 
 * me - <mxMouseEvent> to be updated.
 * evtName - Name of the mouse event.
 */
mxGraph.prototype.updateMouseEvent = function(me, evtName)
{
	if (me.graphX == null || me.graphY == null)
	{
		var pt = mxUtils.convertPoint(this.container, me.getX(), me.getY());
		
		me.graphX = pt.x - this.panDx;
		me.graphY = pt.y - this.panDy;
		
		// Searches for rectangles using method if native hit detection is disabled on shape
		if (me.getCell() == null && this.isMouseDown && evtName == mxEvent.MOUSE_MOVE)
		{
			me.state = this.view.getState(this.getCellAt(pt.x, pt.y, null, null, null, function(state)
			{
				return state.shape == null || state.shape.paintBackground != mxRectangleShape.prototype.paintBackground ||
					mxUtils.getValue(state.style, mxConstants.STYLE_POINTER_EVENTS, '1') == '1' ||
					(state.shape.fill != null && state.shape.fill != mxConstants.NONE);
			}));
		}
	}
	
	return me;
};

/**
 * Function: getStateForEvent
 * 
 * Returns the state for the given touch event.
 */
mxGraph.prototype.getStateForTouchEvent = function(evt)
{
	var x = mxEvent.getClientX(evt);
	var y = mxEvent.getClientY(evt);
	
	// Dispatches the drop event to the graph which
	// consumes and executes the source function
	var pt = mxUtils.convertPoint(this.container, x, y);

	return this.view.getState(this.getCellAt(pt.x, pt.y));
};

/**
 * Function: isEventIgnored
 * 
 * Returns true if the event should be ignored in <fireMouseEvent>.
 */
mxGraph.prototype.isEventIgnored = function(evtName, me, sender)
{
	var mouseEvent = mxEvent.isMouseEvent(me.getEvent());
	var result = false;

	// Drops events that are fired more than once
	if (me.getEvent() == this.lastEvent)
	{
		result = true;
	}
	else
	{
		this.lastEvent = me.getEvent();
	}

	// Installs event listeners to capture the complete gesture from the event source
	// for non-MS touch events as a workaround for all events for the same geture being
	// fired from the event source even if that was removed from the DOM.
	if (this.eventSource != null && evtName != mxEvent.MOUSE_MOVE)
	{
		mxEvent.removeGestureListeners(this.eventSource, null, this.mouseMoveRedirect, this.mouseUpRedirect);
		this.mouseMoveRedirect = null;
		this.mouseUpRedirect = null;
		this.eventSource = null;
	}
	else if (!mxClient.IS_GC && this.eventSource != null && me.getSource() != this.eventSource)
	{
		result = true;
	}
	else if (mxClient.IS_TOUCH && evtName == mxEvent.MOUSE_DOWN &&
			!mouseEvent && !mxEvent.isPenEvent(me.getEvent()))
	{
		this.eventSource = me.getSource();

		this.mouseMoveRedirect = mxUtils.bind(this, function(evt)
		{
			this.fireMouseEvent(mxEvent.MOUSE_MOVE, new mxMouseEvent(evt, this.getStateForTouchEvent(evt)));
		});
		this.mouseUpRedirect = mxUtils.bind(this, function(evt)
		{
			this.fireMouseEvent(mxEvent.MOUSE_UP, new mxMouseEvent(evt, this.getStateForTouchEvent(evt)));
		});
		
		mxEvent.addGestureListeners(this.eventSource, null, this.mouseMoveRedirect, this.mouseUpRedirect);
	}

	// Factored out the workarounds for FF to make it easier to override/remove
	// Note this method has side-effects!
	if (this.isSyntheticEventIgnored(evtName, me, sender))
	{
		result = true;
	}

	// Never fires mouseUp/-Down for double clicks
	if (!mxEvent.isPopupTrigger(this.lastEvent) && evtName != mxEvent.MOUSE_MOVE && this.lastEvent.detail == 2)
	{
		return true;
	}
	
	// Filters out of sequence events or mixed event types during a gesture
	if (evtName == mxEvent.MOUSE_UP && this.isMouseDown)
	{
		this.isMouseDown = false;
	}
	else if (evtName == mxEvent.MOUSE_DOWN && !this.isMouseDown)
	{
		this.isMouseDown = true;
		this.isMouseTrigger = mouseEvent;
	}
	// Drops mouse events that are fired during touch gestures as a workaround for Webkit
	// and mouse events that are not in sync with the current internal button state
	else if (!result && (((!mxClient.IS_FF || evtName != mxEvent.MOUSE_MOVE) &&
		this.isMouseDown && this.isMouseTrigger != mouseEvent) ||
		(evtName == mxEvent.MOUSE_DOWN && this.isMouseDown) ||
		(evtName == mxEvent.MOUSE_UP && !this.isMouseDown)))
	{
		result = true;
	}
	
	if (!result && evtName == mxEvent.MOUSE_DOWN)
	{
		this.lastMouseX = me.getX();
		this.lastMouseY = me.getY();
	}

	return result;
};

/**
 * Function: isSyntheticEventIgnored
 * 
 * Hook for ignoring synthetic mouse events after touchend in Firefox.
 */
mxGraph.prototype.isSyntheticEventIgnored = function(evtName, me, sender)
{
	var result = false;
	var mouseEvent = mxEvent.isMouseEvent(me.getEvent());
	
	// LATER: This does not cover all possible cases that can go wrong in FF
	if (this.ignoreMouseEvents && mouseEvent && evtName != mxEvent.MOUSE_MOVE)
	{
		this.ignoreMouseEvents = evtName != mxEvent.MOUSE_UP;
		result = true;
	}
	else if (mxClient.IS_FF && !mouseEvent && evtName == mxEvent.MOUSE_UP)
	{
		this.ignoreMouseEvents = true;
	}
	
	return result;
};

/**
 * Function: isEventSourceIgnored
 * 
 * Returns true if the event should be ignored in <fireMouseEvent>. This
 * implementation returns true for select, option and input (if not of type
 * checkbox, radio, button, submit or file) event sources if the event is not
 * a mouse event or a left mouse button press event.
 * 
 * Parameters:
 * 
 * evtName - The name of the event.
 * me - <mxMouseEvent> that should be ignored.
 */
mxGraph.prototype.isEventSourceIgnored = function(evtName, me)
{
	var source = me.getSource();
	var name = (source.nodeName != null) ? source.nodeName.toLowerCase() : '';
	var candidate = !mxEvent.isMouseEvent(me.getEvent()) || mxEvent.isLeftMouseButton(me.getEvent());
	
	return evtName == mxEvent.MOUSE_DOWN && candidate && (name == 'select' || name == 'option' ||
		(name == 'input' && source.type != 'checkbox' && source.type != 'radio' &&
		source.type != 'button' && source.type != 'submit' && source.type != 'file'));
};

/**
 * Function: getEventState
 * 
 * Returns the <mxCellState> to be used when firing the mouse event for the
 * given state. This implementation returns the given state.
 * 
 * Parameters:
 * 
 * <mxCellState> - State whose event source should be returned.
 */
mxGraph.prototype.getEventState = function(state)
{
	return state;
};

/**
 * Function: fireMouseEvent
 * 
 * Dispatches the given event in the graph event dispatch loop. Possible
 * event names are <mxEvent.MOUSE_DOWN>, <mxEvent.MOUSE_MOVE> and
 * <mxEvent.MOUSE_UP>. All listeners are invoked for all events regardless
 * of the consumed state of the event.
 * 
 * Parameters:
 * 
 * evtName - String that specifies the type of event to be dispatched.
 * me - <mxMouseEvent> to be fired.
 * sender - Optional sender argument. Default is this.
 */
mxGraph.prototype.fireMouseEvent = function(evtName, me, sender)
{
	if (this.isEventSourceIgnored(evtName, me))
	{
		if (this.tooltipHandler != null)
		{
			this.tooltipHandler.hide();
		}
		
		return;
	}
	
	if (sender == null)
	{
		sender = this;
	}

	// Updates the graph coordinates in the event
	me = this.updateMouseEvent(me, evtName);

	// Detects and processes double taps for touch-based devices which do not have native double click events
	// or where detection of double click is not always possible (quirks, IE10+). Note that this can only handle
	// double clicks on cells because the sequence of events in IE prevents detection on the background, it fires
	// two mouse ups, one of which without a cell but no mousedown for the second click which means we cannot
	// detect which mouseup(s) are part of the first click, ie we do not know when the first click ends.
	if ((!this.nativeDblClickEnabled && !mxEvent.isPopupTrigger(me.getEvent())) || (this.doubleTapEnabled &&
		mxClient.IS_TOUCH && (mxEvent.isTouchEvent(me.getEvent()) || mxEvent.isPenEvent(me.getEvent()))))
	{
		var currentTime = new Date().getTime();
		
		// NOTE: Second mouseDown for double click missing in quirks mode
		if ((!mxClient.IS_QUIRKS && evtName == mxEvent.MOUSE_DOWN) || (mxClient.IS_QUIRKS && evtName == mxEvent.MOUSE_UP && !this.fireDoubleClick))
		{
			if (this.lastTouchEvent != null && this.lastTouchEvent != me.getEvent() &&
				currentTime - this.lastTouchTime < this.doubleTapTimeout &&
				Math.abs(this.lastTouchX - me.getX()) < this.doubleTapTolerance &&
				Math.abs(this.lastTouchY - me.getY()) < this.doubleTapTolerance &&
				this.doubleClickCounter < 2)
			{
				this.doubleClickCounter++;
				var doubleClickFired = false;
				
				if (evtName == mxEvent.MOUSE_UP)
				{
					if (me.getCell() == this.lastTouchCell && this.lastTouchCell != null)
					{
						this.lastTouchTime = 0;
						var cell = this.lastTouchCell;
						this.lastTouchCell = null;

						// Fires native dblclick event via event source
						// NOTE: This fires two double click events on edges in quirks mode. While
						// trying to fix this, we realized that nativeDoubleClick can be disabled for
						// quirks and IE10+ (or we didn't find the case mentioned above where it
						// would not work), ie. all double clicks seem to be working without this.
						if (mxClient.IS_QUIRKS)
						{
							me.getSource().fireEvent('ondblclick');
						}
						
						this.dblClick(me.getEvent(), cell);
						doubleClickFired = true;
					}
				}
				else
				{
					this.fireDoubleClick = true;
					this.lastTouchTime = 0;
				}

				// Do not ignore mouse up in quirks in this case
				if (!mxClient.IS_QUIRKS || doubleClickFired)
				{
					mxEvent.consume(me.getEvent());
					return;
				}
			}
			else if (this.lastTouchEvent == null || this.lastTouchEvent != me.getEvent())
			{
				this.lastTouchCell = me.getCell();
				this.lastTouchX = me.getX();
				this.lastTouchY = me.getY();
				this.lastTouchTime = currentTime;
				this.lastTouchEvent = me.getEvent();
				this.doubleClickCounter = 0;
			}
		}
		else if ((this.isMouseDown || evtName == mxEvent.MOUSE_UP) && this.fireDoubleClick)
		{
			this.fireDoubleClick = false;
			var cell = this.lastTouchCell;
			this.lastTouchCell = null;
			this.isMouseDown = false;
			
			// Workaround for Chrome/Safari not firing native double click events for double touch on background
			var valid = (cell != null) || ((mxEvent.isTouchEvent(me.getEvent()) || mxEvent.isPenEvent(me.getEvent())) &&
				(mxClient.IS_GC || mxClient.IS_SF));
			
			if (valid && Math.abs(this.lastTouchX - me.getX()) < this.doubleTapTolerance &&
				Math.abs(this.lastTouchY - me.getY()) < this.doubleTapTolerance)
			{
				this.dblClick(me.getEvent(), cell);
			}
			else
			{
				mxEvent.consume(me.getEvent());
			}
			
			return;
		}
	}

	if (!this.isEventIgnored(evtName, me, sender))
	{
		// Updates the event state via getEventState
		me.state = this.getEventState(me.getState());
		this.fireEvent(new mxEventObject(mxEvent.FIRE_MOUSE_EVENT, 'eventName', evtName, 'event', me));
		
		if ((mxClient.IS_OP || mxClient.IS_SF || mxClient.IS_GC || mxClient.IS_IE11 ||
			(mxClient.IS_IE && mxClient.IS_SVG) || me.getEvent().target != this.container))
		{
			if (evtName == mxEvent.MOUSE_MOVE && this.isMouseDown && this.autoScroll && !mxEvent.isMultiTouchEvent(me.getEvent))
			{
				this.scrollPointToVisible(me.getGraphX(), me.getGraphY(), this.autoExtend);
			}
			else if (evtName == mxEvent.MOUSE_UP && this.ignoreScrollbars && this.translateToScrollPosition &&
					(this.container.scrollLeft != 0 || this.container.scrollTop != 0))
			{
				var s = this.view.scale;
				var tr = this.view.translate;
				this.view.setTranslate(tr.x - this.container.scrollLeft / s, tr.y - this.container.scrollTop / s);
				this.container.scrollLeft = 0;
				this.container.scrollTop = 0;
			}
			
			if (this.mouseListeners != null)
			{
				var args = [sender, me];
	
				// Does not change returnValue in Opera
				if (!me.getEvent().preventDefault)
				{
					me.getEvent().returnValue = true;
				}
				
				for (var i = 0; i < this.mouseListeners.length; i++)
				{
					var l = this.mouseListeners[i];
					
					if (evtName == mxEvent.MOUSE_DOWN)
					{
						l.mouseDown.apply(l, args);
					}
					else if (evtName == mxEvent.MOUSE_MOVE)
					{
						l.mouseMove.apply(l, args);
					}
					else if (evtName == mxEvent.MOUSE_UP)
					{
						l.mouseUp.apply(l, args);
					}
				}
			}
			
			// Invokes the click handler
			if (evtName == mxEvent.MOUSE_UP)
			{
				this.click(me);
			}
		}
		
		// Detects tapAndHold events using a timer
		if ((mxEvent.isTouchEvent(me.getEvent()) || mxEvent.isPenEvent(me.getEvent())) &&
			evtName == mxEvent.MOUSE_DOWN && this.tapAndHoldEnabled && !this.tapAndHoldInProgress)
		{
			this.tapAndHoldInProgress = true;
			this.initialTouchX = me.getGraphX();
			this.initialTouchY = me.getGraphY();
			
			var handler = function()
			{
				if (this.tapAndHoldValid)
				{
					this.tapAndHold(me);
				}
				
				this.tapAndHoldInProgress = false;
				this.tapAndHoldValid = false;
			};
			
			if (this.tapAndHoldThread)
			{
				window.clearTimeout(this.tapAndHoldThread);
			}
	
			this.tapAndHoldThread = window.setTimeout(mxUtils.bind(this, handler), this.tapAndHoldDelay);
			this.tapAndHoldValid = true;
		}
		else if (evtName == mxEvent.MOUSE_UP)
		{
			this.tapAndHoldInProgress = false;
			this.tapAndHoldValid = false;
		}
		else if (this.tapAndHoldValid)
		{
			this.tapAndHoldValid =
				Math.abs(this.initialTouchX - me.getGraphX()) < this.tolerance &&
				Math.abs(this.initialTouchY - me.getGraphY()) < this.tolerance;
		}

		// Stops editing for all events other than from cellEditor
		if (evtName == mxEvent.MOUSE_DOWN && this.isEditing() && !this.cellEditor.isEventSource(me.getEvent()))
		{
			this.stopEditing(!this.isInvokesStopCellEditing());
		}

		this.consumeMouseEvent(evtName, me, sender);
	}
};

/**
 * Function: consumeMouseEvent
 * 
 * Consumes the given <mxMouseEvent> if it's a touchStart event.
 */
mxGraph.prototype.consumeMouseEvent = function(evtName, me, sender)
{
	// Workaround for duplicate click in Windows 8 with Chrome/FF/Opera with touch
	if (evtName == mxEvent.MOUSE_DOWN && mxEvent.isTouchEvent(me.getEvent()))
	{
		me.consume(false);
	}
};

/**
 * Function: fireGestureEvent
 * 
 * Dispatches a <mxEvent.GESTURE> event. The following example will resize the
 * cell under the mouse based on the scale property of the native touch event.
 * 
 * (code)
 * graph.addListener(mxEvent.GESTURE, function(sender, eo)
 * {
 *   var evt = eo.getProperty('event');
 *   var state = graph.view.getState(eo.getProperty('cell'));
 *   
 *   if (graph.isEnabled() && graph.isCellResizable(state.cell) && Math.abs(1 - evt.scale) > 0.2)
 *   {
 *     var scale = graph.view.scale;
 *     var tr = graph.view.translate;
 *     
 *     var w = state.width * evt.scale;
 *     var h = state.height * evt.scale;
 *     var x = state.x - (w - state.width) / 2;
 *     var y = state.y - (h - state.height) / 2;
 *     
 *     var bounds = new mxRectangle(graph.snap(x / scale) - tr.x,
 *     		graph.snap(y / scale) - tr.y, graph.snap(w / scale), graph.snap(h / scale));
 *     graph.resizeCell(state.cell, bounds);
 *     eo.consume();
 *   }
 * });
 * (end)
 * 
 * Parameters:
 * 
 * evt - Gestureend event that represents the gesture.
 * cell - Optional <mxCell> associated with the gesture.
 */
mxGraph.prototype.fireGestureEvent = function(evt, cell)
{
	// Resets double tap event handling when gestures take place
	this.lastTouchTime = 0;
	this.fireEvent(new mxEventObject(mxEvent.GESTURE, 'event', evt, 'cell', cell));
};

/**
 * Function: destroy
 * 
 * Destroys the graph and all its resources.
 */
mxGraph.prototype.destroy = function()
{
	if (!this.destroyed)
	{
		this.destroyed = true;
		
		if (this.tooltipHandler != null)
		{
			this.tooltipHandler.destroy();
		}
		
		if (this.selectionCellsHandler != null)
		{
			this.selectionCellsHandler.destroy();
		}

		if (this.panningHandler != null)
		{
			this.panningHandler.destroy();
		}

		if (this.popupMenuHandler != null)
		{
			this.popupMenuHandler.destroy();
		}
		
		if (this.connectionHandler != null)
		{
			this.connectionHandler.destroy();
		}
		
		if (this.graphHandler != null)
		{
			this.graphHandler.destroy();
		}
		
		if (this.cellEditor != null)
		{
			this.cellEditor.destroy();
		}
		
		if (this.view != null)
		{
			this.view.destroy();
		}

		if (this.model != null && this.graphModelChangeListener != null)
		{
			this.model.removeListener(this.graphModelChangeListener);
			this.graphModelChangeListener = null;
		}

		this.container = null;
	}
};
