/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
/**
 * Class: mxConnectionConstraint
 * 
 * Defines an object that contains the constraints about how to connect one
 * side of an edge to its terminal.
 * 
 * Constructor: mxConnectionConstraint
 * 
 * Constructs a new connection constraint for the given point and boolean
 * arguments.
 * 
 * Parameters:
 * 
 * point - Optional <mxPoint> that specifies the fixed location of the point
 * in relative coordinates. Default is null.
 * perimeter - Optional boolean that specifies if the fixed point should be
 * projected onto the perimeter of the terminal. Default is true.
 */
// 中文注释：
// 类名：mxConnectionConstraint
// 功能：定义一个对象，用于描述如何将边的某一端连接到其终端的约束条件。
// 构造函数：mxConnectionConstraint
// 作用：根据给定的点和布尔参数，创建一个新的连接约束对象。
// 参数说明：
// - point：可选的mxPoint对象，指定连接点的固定位置，使用相对坐标，默认值为null。
// - perimeter：可选的布尔值，指定是否将固定点投影到终端的边界上，默认值为true。
// - name：可选的字符串，指定约束的名称。
// - dx：可选的浮点数，指定约束的水平偏移量，默认值为0。
// - dy：可选的浮点数，指定约束的垂直偏移量，默认值为0。
function mxConnectionConstraint(point, perimeter, name, dx, dy)
{
	this.point = point;
	this.perimeter = (perimeter != null) ? perimeter : true;
	this.name = name;
	this.dx = dx? dx : 0;
	this.dy = dy? dy : 0;
    // 中文注释：
    // 初始化逻辑：
    // 1. 将传入的point参数赋值给this.point，用于存储连接点的坐标。
    // 2. 如果perimeter参数未定义，则默认设置为true，表示点需要投影到终端边界。
    // 3. 将name参数赋值给this.name，用于标识约束的名称。
    // 4. dx和dy分别表示水平和垂直偏移量，若未提供则默认为0。
    // 注意事项：perimeter的默认值true确保了点通常会被投影到终端的边界上。
};

/**
 * Variable: point
 * 
 * <mxPoint> that specifies the fixed location of the connection point.
 */
// 中文注释：
// 变量：point
// 作用：存储一个mxPoint对象，表示连接点的固定位置，使用相对坐标。
// 用途：用于确定边连接到终端时的具体位置。
mxConnectionConstraint.prototype.point = null;

/**
 * Variable: perimeter
 * 
 * Boolean that specifies if the point should be projected onto the perimeter
 * of the terminal.
 */
// 中文注释：
// 变量：perimeter
// 作用：布尔值，决定连接点是否需要投影到终端的边界上。
// 用途：当为true时，连接点会自动调整到终端的边界，确保连接的视觉一致性。
mxConnectionConstraint.prototype.perimeter = null;

/**
 * Variable: name
 * 
 * Optional string that specifies the name of the constraint.
 */
// 中文注释：
// 变量：name
// 作用：字符串，用于标识该约束的名称，可选参数。
// 用途：便于在代码中引用或调试时识别特定的约束对象。
mxConnectionConstraint.prototype.name = null;

/**
 * Variable: dx
 * 
 * Optional float that specifies the horizontal offset of the constraint.
 */
// 中文注释：
// 变量：dx
// 作用：浮点数，表示约束的水平偏移量，可选参数。
// 用途：用于微调连接点在水平方向上的位置，默认值为0。
mxConnectionConstraint.prototype.dx = null;

/**
 * Variable: dy
 * 
 * Optional float that specifies the vertical offset of the constraint.
 */
// 中文注释：
// 变量：dy
// 作用：浮点数，表示约束的垂直偏移量，可选参数。
// 用途：用于微调连接点在垂直方向上的位置，默认值为0。
mxConnectionConstraint.prototype.dy = null;

