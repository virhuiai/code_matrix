/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxHexagon
 * 
 * Implementation of the hexagon shape.
 * 
 * Constructor: mxHexagon
 *
 * Constructs a new hexagon shape.
 */
// 类：mxHexagon
// 实现六边形形状。
// 构造函数：mxHexagon
// 创建一个新的六边形形状对象。
function mxHexagon()
{
	mxActor.call(this);
};

/**
 * Extends mxActor.
 */
// 继承 mxActor 类。
// 中文注释：mxHexagon 类通过继承 mxActor 类，获取其基础功能，用于实现六边形形状的绘制和操作。
mxUtils.extend(mxHexagon, mxActor);

/**
 * Function: redrawPath
 *
 * Draws the path for this shape.
 */
// 函数：redrawPath
// 绘制该六边形形状的路径。
// 中文注释：
// - 功能：该函数负责绘制六边形的路径，定义了六边形的顶点坐标并根据样式设置绘制形状。
// - 参数说明：
//   - c: 画布上下文对象，用于执行绘制操作。
//   - x: 六边形左上角的 x 坐标（水平偏移）。
//   - y: 六边形左上角的 y 坐标（垂直偏移）。
//   - w: 六边形的宽度。
//   - h: 六边形的高度。
// - 方法目的：根据给定的坐标和尺寸，绘制一个六边形路径，支持圆角和闭合路径。
// - 关键变量说明：
//   - arcSize: 圆角大小，从样式中获取，默认使用 mxConstants.LINE_ARCSIZE，除以 2 得到实际圆角半径。
//   - mxPoint: 用于定义六边形六个顶点的坐标点。
// - 特殊处理：
//   - isRounded: 布尔值，决定是否应用圆角效果。
//   - close: 布尔值，决定路径是否闭合（此处为 true，表示闭合路径）。
// - 样式设置说明：圆角大小由样式中的 mxConstants.STYLE_ARCSIZE 属性控制，默认值为 mxConstants.LINE_ARCSIZE。
mxHexagon.prototype.redrawPath = function(c, x, y, w, h)
{
	var arcSize = mxUtils.getValue(this.style, mxConstants.STYLE_ARCSIZE, mxConstants.LINE_ARCSIZE) / 2;
    // 中文注释：从样式中获取圆角大小，默认为 mxConstants.LINE_ARCSIZE 的一半，用于控制六边形角的圆滑程度。

	this.addPoints(c, [new mxPoint(0.25 * w, 0), new mxPoint(0.75 * w, 0), new mxPoint(w, 0.5 * h), new mxPoint(0.75 * w, h),
	                   new mxPoint(0.25 * w, h), new mxPoint(0, 0.5 * h)], this.isRounded, arcSize, true);
    // 中文注释：
    // - 功能：调用 addPoints 方法，将六边形的六个顶点添加到路径中，形成一个闭合的六边形。
    // - 顶点说明：
    //   - (0.25 * w, 0): 顶部左侧顶点，位于宽度的 1/4 处。
    //   - (0.75 * w, 0): 顶部右侧顶点，位于宽度的 3/4 处。
    //   - (w, 0.5 * h): 右侧中间顶点，位于宽度最右端，高度中间。
    //   - (0.75 * w, h): 底部右侧顶点，位于宽度的 3/4 处，高度最底端。
    //   - (0.25 * w, h): 底部左侧顶点，位于宽度的 1/4 处，高度最底端。
    //   - (0, 0.5 * h): 左侧中间顶点，位于宽度最左端，高度中间。
    // - 参数说明：
    //   - isRounded: 控制是否绘制圆角六边形。
    //   - arcSize: 圆角的半径大小。
    //   - true: 表示路径闭合，形成完整的六边形。
    // - 交互逻辑：通过 addPoints 方法将顶点连接成路径，支持动态调整圆角和闭合效果。
};
