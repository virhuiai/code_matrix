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

/////
/**
 * 根据步骤获取颜色
 * @param {number} step 步骤标识
 * @returns {string} 颜色值
 */
mxGraphUtils.getColorByStep = function(step) {
    switch (step) {
        case 0:
            return '#999999'; // 未开始
        case 1:
            return '#fd9a01'; // 进行中
        case 2:
            return '#3399fe'; // 办结了
        default:
            return '#000000'; // 默认颜色，可根据实际情况调整
    }
}

/**
 * 生成style名称
 * @param pre
 * @param step
 */
mxGraphUtils.getStyleNameByStep = function(pre, step){
    switch (step) {
        case 0:
            return pre + 'BeforeProcess'; // 未开始
        case 1:
            return pre + 'InProcess'; // 进行中
        case 2:
            return pre + 'AfterProcess'; // 办结了
        default:
            return pre + step; //
    }
}

/**
 * 初始化样式
 * @param graph
 */
mxGraphUtils.initStyles = function(graph){

    // 主节点容器样式
    graph.getStylesheet().putCellStyle('nodeContainer', {
        'fillColor': 'white',
        'strokeColor': '#E0E0E0',
        'strokeWidth': 1,
        'rounded': true,
        'arcSize': 1,
        // 'shadow': true
    });


    for (var step = 0; step < 3; step++){
        // top bg
        graph.getStylesheet().putCellStyle(
            mxGraphUtils.getStyleNameByStep("nodeTopBg", step),
            {
                'fillColor': mxGraphUtils.getColorByStep(step),// 蓝色
                'strokeColor': mxGraphUtils.getColorByStep(step),
                'strokeWidth': 1,
                'rounded': true,
                'arcSize': 1,
            });

        // 标题前 点 处理
        graph.getStylesheet().putCellStyle(mxGraphUtils.getStyleNameByStep('nodeTitlePoint', step), {
            'shape':'ellipse',
            'fillColor': mxGraphUtils.getColorByStep(step),
            'strokeColor': mxGraphUtils.getColorByStep(step),
        });


        graph.getStylesheet().putCellStyle(mxGraphUtils.getStyleNameByStep('nodeTitle', step), {
            'background':'transparent',
            'strokeColor': 'transparent',
            'fillColor': 'transparent',
            'fontColor': 'black',
            'fontSize': 16,
            // 'fillColor': 'white',
            // 'strokeColor': 'none',
            // 'fontStyle': 1,
            // 'rounded': true,
            // 'arcSize': 10,
            // 'align': 'left',
            // 'verticalAlign': 'middle',
            // 'spacingLeft': 10
        });

        // 标签样式
        graph.getStylesheet().putCellStyle(
            mxGraphUtils.getStyleNameByStep('nodeTitleTag', step)
            , {
                'fillColor': 'none',
                'strokeColor': 'none',
                'fontColor': mxGraphUtils.getColorByStep(step),
                'fontSize': 12,
                'align': 'right',
                'verticalAlign': 'middle',
                'spacingRight': 10
            });

    }

    //节点内容样式
    graph.getStylesheet().putCellStyle('nodeBodyBg', {
        'fillColor': '#f2f2f2',
        'strokeColor': '#f2f2f2',
        // 'strokeWidth': 1,
        'rounded': true,
        'arcSize': 1,
        'shape':'rectangle',
    });


    //
    graph.getStylesheet().putCellStyle('nodeBodySubTitle', {
        'fillColor': 'none',
        'strokeColor': 'none',
        'fontColor': '#5B9BD5',
        'fontSize': 18,
        'fontStyle': 1,
        'align': 'left',
        'verticalAlign': 'middle'
    });


    // 按钮样式
    graph.getStylesheet().putCellStyle('nodeBodySubTitleTag', {
        'fillColor': '#5B9BD5',
        'strokeColor': 'none',
        'fontColor': 'white',
        'fontSize': 14,
        'rounded': true,
        'arcSize': 20
    });

    // 文本
    graph.getStylesheet().putCellStyle('nodeBodyRow', {
        'fillColor': 'transparent',
        'strokeColor': 'transparent',
        'fontColor': 'black',
        'fontSize': 14,
        // 'fontStyle': 1,
        'align': 'left',
        'verticalAlign': 'middle'
    });


    // 边的样式
    var edgeStyle = graph.getStylesheet().getDefaultEdgeStyle();
    edgeStyle[mxConstants.STYLE_STROKECOLOR] = '#5B9BD5';
    edgeStyle[mxConstants.STYLE_STROKEWIDTH] = 2;
    edgeStyle[mxConstants.STYLE_ROUNDED] = true;
    edgeStyle[mxConstants.STYLE_EDGE] = mxEdgeStyle.OrthConnector;
}

// /**
//  * 插入相对位置节点
//  * @param parent
//  * @param id
//  * @param value
//  * @param x
//  * @param y
//  * @param width
//  * @param height
//  * @param style
//  * @param relative
//  * @returns {*}
//  */
// mxGraphUtils.insertVertexRelative = function (parent, id, value, x, y, width, height, style, relative) {
//     var newVertex = graph.insertVertex(parent, id, value, x, y, width, height, style, relative);
//     newVertex.geometry.relative = true;
//     return newVertex;
// }


mxGraphUtils.newMyNode = function(conf){
    var graph=conf.graph;
    var parent=conf.parent;
    var step = conf.step;

    var w1 = conf.w1;
    var h1 = conf.h1;


    var y1Current = 0;
    var lastVertex = null;
    // 主节点容器
    var oneNode = graph.insertVertex(parent, null, '', 0, 0, w1, h1, 'nodeContainer');

    //top 背景
    var mainHeaderBg = graph.insertVertex(oneNode, null, '', 0, 0, oneNode.geometry.width, 25,
        mxGraphUtils.getStyleNameByStep("nodeTopBg", step),true);
    // mainHeaderBg.geometry.offset = new mxPoint(0, 0);
    lastVertex = mainHeaderBg;
    y1Current += lastVertex.geometry.height;

    /////////////  标题
    var point = graph.insertVertex(oneNode, null, '', 0, 0, 20, 20,
        mxGraphUtils.getStyleNameByStep('nodeTitlePoint', step)
        , true);
    y1Current += 11;
    point.geometry.offset = new mxPoint(10,  y1Current);
    lastVertex = point;
    y1Current += lastVertex.geometry.height;

    // 主节点标题
    var title = graph.insertVertex(oneNode, null, '标题标题标题', 0, 0, 0, 0, mxGraphUtils.getStyleNameByStep('nodeTitle', step),true);
    graph.updateCellSize(title);
    title.geometry.offset = new mxPoint((2 * point.geometry.width) ,  y1Current - lastVertex.geometry.height);


    // 已办结标签
    var tag = graph.insertVertex(oneNode, null, '已办结', 0, 0, 0, 0, mxGraphUtils.getStyleNameByStep('nodeTitleTag', step), true);
    // 更新大小以适应其内容
    graph.updateCellSize(tag);
    // console.log("tag.geometry.width " + tag.geometry.width )
    // console.log("tag.geometry.height " + tag.geometry.height )
    tag.geometry.offset = new mxPoint(oneNode.geometry.width - tag.geometry.width ,  y1Current - lastVertex.geometry.height);
    ///////////////////////////////标题 END



    var marginLR = 10;
    var marginTB = 15;
    var y2current = 0;
    // ------------------
    var mainBodyBg = graph.insertVertex(oneNode, null, '', 0, 0, oneNode.geometry.width - (2 * marginTB) , oneNode.geometry.height - y1Current - (2 * marginLR), 'nodeBodyBg', true);
    mainBodyBg.geometry.offset = new mxPoint(marginTB,y1Current + marginLR);

    /// nodeBodySubTitle

    var bodyMarginLR = 10;
    var bodyMarginTB = 10;


    // // 流程号
    var subTitle = graph.insertVertex(mainBodyBg, null, 'ZQYW2024112956293', 0, 0, 0, 0,
        'nodeBodySubTitle',true);
    graph.updateCellSize(subTitle);
    subTitle.geometry.offset = new mxPoint(bodyMarginLR, bodyMarginTB);
    lastVertex = subTitle;
    y2current += lastVertex.geometry.height;

    // 可选的状态
    var subTitleTag = graph.insertVertex(mainBodyBg, null, '状态  ', 50, 0, 0, 0, 'nodeBodySubTitleTag');
    graph.updateCellSize(subTitleTag);
    subTitleTag.geometry.x = mainBodyBg.geometry.width - bodyMarginLR - subTitleTag.geometry.width;
    subTitleTag.geometry.y = bodyMarginTB;


    /////////以下是文字
    var cellVertex = null;
    // 一行一个的
    // 第一行
    cellVertex = graph.insertVertex(mainBodyBg, null, '标签1：内容1', 0, 0, 0, 14, 'nodeBodyRow', true);
    // graph.updateCellSize(cellVertex);
    console.log("cell.geometry.x:" + cellVertex.geometry.x);
    console.log("cell.geometry.y:" + cellVertex.geometry.y);
    // cellVertex.geometry.x = 0;
    // cellVertex.geometry.y = 10;
    cellVertex.geometry.offset = new mxPoint(bodyMarginLR, y2current + cellVertex.geometry.height);
    lastVertex = cellVertex;
    y2current += 2 * lastVertex.geometry.height;


    // 一行2个的
    cellVertex = graph.insertVertex(mainBodyBg, null, '标签2.1：内容2.1', 0, 0, 0, 14, 'nodeBodyRow', true);
    // graph.updateCellSize(cellVertex);
    console.log("cell.geometry.x:" + cellVertex.geometry.x);
    console.log("cell.geometry.y:" + cellVertex.geometry.y);
    // cellVertex.geometry.x = 0;
    // cellVertex.geometry.y = 10;
    cellVertex.geometry.offset = new mxPoint(bodyMarginLR, y2current + cellVertex.geometry.height);
    lastVertex = cellVertex;
    y2current += 2 * lastVertex.geometry.height;

    cellVertex = graph.insertVertex(mainBodyBg, null, '标签2.2：内容2.2', 0, 0, 0, 14, 'nodeBodyRow', true);
    // graph.updateCellSize(cellVertex);
    console.log("cell.geometry.x:" + cellVertex.geometry.x);
    console.log("cell.geometry.y:" + cellVertex.geometry.y);
    // cellVertex.geometry.x = 0;
    // cellVertex.geometry.y = 10;
    cellVertex.geometry.offset = new mxPoint(mainBodyBg.geometry.width / 2, y2current - lastVertex.geometry.height);

    ////
    // 一行2个的
    cellVertex = graph.insertVertex(mainBodyBg, null, '标签3.1：内容3.1', 0, 0, 0, 14, 'nodeBodyRow', true);
    // graph.updateCellSize(cellVertex);
    console.log("cell.geometry.x:" + cellVertex.geometry.x);
    console.log("cell.geometry.y:" + cellVertex.geometry.y);
    // cellVertex.geometry.x = 0;
    // cellVertex.geometry.y = 10;
    cellVertex.geometry.offset = new mxPoint(bodyMarginLR, y2current + cellVertex.geometry.height);
    lastVertex = cellVertex;
    y2current += 2 * lastVertex.geometry.height;

    cellVertex = graph.insertVertex(mainBodyBg, null, '标签3.2：内容3.2', 0, 0, 0, 14, 'nodeBodyRow', true);
    // graph.updateCellSize(cellVertex);
    console.log("cell.geometry.x:" + cellVertex.geometry.x);
    console.log("cell.geometry.y:" + cellVertex.geometry.y);
    // cellVertex.geometry.x = 0;
    // cellVertex.geometry.y = 10;
    cellVertex.geometry.offset = new mxPoint(mainBodyBg.geometry.width / 2, y2current - lastVertex.geometry.height);


    return oneNode;
}