var mxGraphUtils = mxGraphUtils || {};

/**
 * 定义一个方法，用于在单个步骤中向模型添加单元
 * @param graph
 * @param parent
 * @param cb
 */
mxGraphUtils.insertToGraph = function(graph, parent, cb) {
    // 在模型中开始更新
    graph.getModel().beginUpdate();
    try {
        // 执行传入的回调函数，添加单元
        cb(parent);
    } finally {
        // 更新显示
        graph.getModel().endUpdate();
    }
}

/**
 * 检查浏览器
 */
mxGraphUtils.checkBrowserOrError = function(){
    if (!mxClient.isBrowserSupported()) {
        // 如果浏览器不受支持，则显示错误消息。
        // mxUtils.error('Browser is not supported!', 200, false);
        mxUtils.error('浏览器不支持!', 200, false);
        throw new Error("浏览器不受支持!");
    }
}

/**
 * 向指定ID的元素添加子节点
 * @param parentId
 * @param newNode
 *
 *
 // 示例用法
 var newElement = document.createElement('div');
 newElement.textContent = '这是新添加的子节点';

 mxGraphUtils.addNodeToElementById('yourParentElementId', newElement);
 */
mxGraphUtils.addNodeToElementById = function(parentId, newNode) {
    // 获取父元素
    var parentElement = document.getElementById(parentId);

    // 检查父元素是否存在
    if (!parentElement) {
        mxUtils.error('不存在父元素ID：' + parentId + '!', 200, false);
        throw new Error("不存在父元素ID" + parentId + "!");
    }

    // 添加新节点到父元素
    parentElement.appendChild(newNode);
}

/**
 * 获取边样式常量数组
 * @returns {*[]}
 */
mxGraphUtils.getAllEdgeStyles = function(){
    // 定义一个数组，包含mxConstants中的边样式常量
    var edgeStyles = [
        mxConstants.EDGESTYLE_ELBOW, // Name of the elbow edge style. 拐角边样式  EDGESTYLE_ELBOW: 'elbowEdgeStyle'
        mxConstants.EDGESTYLE_ENTITY_RELATION, // Name of the entity relation edge style. 实体关系边样式 EDGESTYLE_ENTITY_RELATION: 'entityRelationEdgeStyle'
        mxConstants.EDGESTYLE_LOOP, // Name of the loop edge style. 环形边样式 EDGESTYLE_LOOP: 'loopEdgeStyle'
        mxConstants.EDGESTYLE_SIDETOSIDE, // Name of the side to side edge style. 侧到侧边样式 EDGESTYLE_SIDETOSIDE: 'sideToSideEdgeStyle'
        mxConstants.EDGESTYLE_TOPTOBOTTOM, // Name of the top to bottom edge style. 顶到底边样式 EDGESTYLE_TOPTOBOTTOM: 'topToBottomEdgeStyle'
        mxConstants.EDGESTYLE_ORTHOGONAL, // Name of the generic orthogonal edge style. 通用正交边样式 EDGESTYLE_ORTHOGONAL: 'orthogonalEdgeStyle'
        mxConstants.EDGESTYLE_SEGMENT // Name of the generic segment edge style. 通用分段边样式 EDGESTYLE_SEGMENT: 'segmentEdgeStyle'
    ];
    return edgeStyles;
}

/**
 * 获取所有形状
 * @returns {*[]}
 */
mxGraphUtils.getAllShapes = function(){
    return [
        mxConstants.SHAPE_RECTANGLE, // 矩形 SHAPE_RECTANGLE: 'rectangle'
        mxConstants.SHAPE_ELLIPSE, // 椭圆 SHAPE_ELLIPSE: 'ellipse'
        mxConstants.SHAPE_DOUBLE_ELLIPSE, // 双椭圆 SHAPE_DOUBLE_ELLIPSE: 'doubleEllipse'
        mxConstants.SHAPE_RHOMBUS, // 菱形 SHAPE_RHOMBUS: 'rhombus'
        mxConstants.SHAPE_LINE, // 线条  SHAPE_LINE: 'line'
        mxConstants.SHAPE_IMAGE, // 图片 SHAPE_IMAGE: 'image'
        mxConstants.SHAPE_ARROW, // 箭头 SHAPE_ARROW: 'arrow'
        mxConstants.SHAPE_ARROW_CONNECTOR, // 箭头连接器 SHAPE_ARROW_CONNECTOR: 'arrowConnector'
        mxConstants.SHAPE_LABEL, // 标签 SHAPE_LABEL: 'label'
        mxConstants.SHAPE_CYLINDER, // 圆柱体  SHAPE_CYLINDER: 'cylinder'
        mxConstants.SHAPE_SWIMLANE, // 泳道  SHAPE_SWIMLANE: 'swimlane'
        mxConstants.SHAPE_CONNECTOR, // 连接器 SHAPE_CONNECTOR: 'connector'
        mxConstants.SHAPE_ACTOR, // 角色 SHAPE_ACTOR: 'actor'
        mxConstants.SHAPE_CLOUD, // 云 SHAPE_CLOUD: 'cloud'
        mxConstants.SHAPE_TRIANGLE, // 三角形 SHAPE_TRIANGLE: 'triangle'
        mxConstants.SHAPE_HEXAGON // 六边形  SHAPE_HEXAGON: 'hexagon'
    ]
}

/**
 * 为所有级别安装自动布局
 * // 参考 folding.html todo
 * @param graph
 */
mxGraphUtils.setStackLayout = function(graph){
    // 为所有级别安装自动布局
    var layout = new mxStackLayout(graph, true);
    // 设置布局的边框宽度与图的边框宽度一致
    layout.border = graph.border;
    // 创建一个布局管理器
    var layoutMgr = new mxLayoutManager(graph);
    // 定义获取布局的函数
    layoutMgr.getLayout = function (cell) {
        // 如果单元未折叠
        if (!cell.collapsed) {
            // 如果单元的父单元不是根单元
            if (cell.parent != graph.model.root) {
                // 设置布局属性
                layout.resizeParent = true;
                // 设置布局为垂直排列
                layout.horizontal = false;
                // 设置单元之间的间距
                layout.spacing = 10;
            }
            else {
                // 如果单元的父单元是根单元，设置不同的布局属性
                layout.resizeParent = true;
                // 设置布局为水平排列
                layout.horizontal = true;
                // 设置更大的单元间距
                layout.spacing = 40;
            }


            // 返回布局对象
            return layout;
        }

        // 如果单元已折叠，返回null表示不进行布局
        return null;
    };
    // 调整容器大小
    graph.setResizeContainer(true);
}

/**
 * 启用图的自动布局 CompactTreeLayout
 * @param graph
 */
mxGraphUtils.setCompactTreeLayout = function(graph){
    // 启用图的自动布局，并为所有组安装一个树布局
    // 当组的子节点被更改、添加或删除时，将应用此布局
    var layout = new mxCompactTreeLayout(graph, false);
    // 不使用边界框进行布局计算
    layout.useBoundingBox = false;
    // 不进行边的路由计算
    layout.edgeRouting = false;
    // 设置层级之间的距离为60
    layout.levelDistance = 60;
    // 设置节点之间的距离为16
    layout.nodeDistance = 16;

    // 允许布局移动节点，即使在图中节点不可移动
    layout.isVertexMovable = function (cell) {
        return true;
    };

    // 创建一个布局管理器
    var layoutMgr = new mxLayoutManager(graph);

    // 为布局管理器定义一个获取布局的方法
    layoutMgr.getLayout = function (cell) {
        // 如果节点有子节点，则返回之前定义的布局
        if (cell.getChildCount() > 0) {
            return layout;
        }
    };
    return layout;
}

/**
 * 定义一个自定义形状用于树节点，包括外出边的上半部分。
 */
mxGraphUtils.regTreeNodeShape = function(graph, layout){
    /*
        定义一个自定义形状用于树节点，包括外出边的上半部分。
    */
    function TreeNodeShape() {
    };

    TreeNodeShape.prototype = new mxCylinder();
    TreeNodeShape.prototype.constructor = TreeNodeShape;

    // 定义上边段的长度。
    TreeNodeShape.prototype.segment = 20;

    // 渲染时需要访问单元状态
    TreeNodeShape.prototype.apply = function (state) {
        mxCylinder.prototype.apply.apply(this, arguments);
        this.state = state;
    };

    TreeNodeShape.prototype.redrawPath = function (path, x, y, w, h, isForeground) {
        var graph = this.state.view.graph;
        var hasChildren = graph.model.getOutgoingEdges(this.state.cell).length > 0;

        if (isForeground) {
            if (hasChildren) {
                // 在顶点边界外绘制
                path.moveTo(w / 2, h + this.segment);
                path.lineTo(w / 2, h);
                path.end();
            }
        }
        else {
            path.moveTo(0, 0);
            path.lineTo(w, 0);
            path.lineTo(w, h);
            path.lineTo(0, h);
            path.close();
        }
    };


    mxCellRenderer.registerShape('treenode', TreeNodeShape);

    // 为树中的节点定义自定义周长
    mxGraphView.prototype.updateFloatingTerminalPoint = function (edge, start, end, source) {
        var pt = null;

        if (source) {
            pt = new mxPoint(start.x + start.width / 2,
                start.y + start.height + TreeNodeShape.prototype.segment);
        }
        else {
            pt = new mxPoint(start.x + start.width / 2, start.y);
        }

        edge.setAbsoluteTerminalPoint(pt, source);
    };

    // 设置折叠和展开图标。以下是默认值，但如果您需要替换它们，可以按照以下方式操作。
    mxGraph.prototype.collapsedImage = new mxImage(mxClient.imageBasePath + '/collapsed.gif', 9, 9);// todo 路径
    mxGraph.prototype.expandedImage = new mxImage(mxClient.imageBasePath + '/expanded.gif', 9, 9);// todo 路径

    // 避免边和折叠图标重叠
    graph.keepEdgesInBackground = true;
    // 设置一些样式表选项以控制视觉外观
    var style = graph.getStylesheet().getDefaultVertexStyle();
    style[mxConstants.STYLE_SHAPE] = 'treenode'; // 设置节点形状为树节点

    style = graph.getStylesheet().getDefaultEdgeStyle();
    style[mxConstants.STYLE_EDGE] = mxEdgeStyle.TopToBottom; // 设置边的样式为从上到下
    style[mxConstants.STYLE_ROUNDED] = true; // 设置边为圆角样式

    // 启用自动调整顶点大小、平移功能
    graph.setAutoSizeCells(true); // 编辑后自动调整顶点大小
    graph.setPanning(true); // 启用平移功能


    // 定义显示折叠图标的条件
    graph.isCellFoldable = function (cell) {
        return this.model.getOutgoingEdges(cell).length > 0;
    };

    // 定义折叠图标的显示位置
    graph.cellRenderer.getControlBounds = function (state) {
        if (state.control != null) {
            var oldScale = state.control.scale;
            var w = state.control.bounds.width / oldScale;
            var h = state.control.bounds.height / oldScale;
            var s = state.view.scale;

            return new mxRectangle(state.x + state.width / 2 - w / 2 * s,
                state.y + state.height + TreeNodeShape.prototype.segment * s - h / 2 * s,
                w * s, h * s);
        }

        return null;
    };


    // 更新给定子树的可见状态，考虑遍历分支的折叠状态
    function toggleSubtree(graph, cell, show) {
        show = (show != null) ? show : true; // 默认展开状态为true
        var cells = []; // 用于存储需要更新可见状态的单元格

        // 遍历子树
        graph.traverse(cell, true, function (vertex) {
            if (vertex != cell) {
                cells.push(vertex); // 将当前单元格添加到cells数组中
            }

            // 如果遇到折叠的单元格，则停止递归
            return vertex == cell || !graph.isCellCollapsed(vertex);
        });

        // 更新cells数组中单元格的可见状态
        graph.toggleCells(show, cells, true);
    };


    // 实现点击折叠图标的操作
    graph.foldCells = function (collapse, recurse, cells) {
        this.model.beginUpdate();
        try {
            toggleSubtree(this, cells[0], !collapse);
            this.model.setCollapsed(cells[0], collapse);

            // 执行新图的布局，因为对可见性和折叠状态的更改不会触发当前管理器中的布局。
            layout.execute(graph.getDefaultParent());
        } finally {
            this.model.endUpdate();
        }
    };

    return TreeNodeShape;
}