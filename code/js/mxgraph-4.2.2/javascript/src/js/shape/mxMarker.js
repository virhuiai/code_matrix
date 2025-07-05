/**
 * Copyright (c) 2006-2015, JGraph Ltd
 * Copyright (c) 2006-2015, Gaudenz Alder
 */
var mxMarker =
{
	/**
	 * Class: mxMarker
	 * 
	 * A static class that implements all markers for VML and SVG using a
	 * registry. NOTE: The signatures in this class will change.
	 * 
	 * Variable: markers
	 * 
	 * Maps from markers names to functions to paint the markers.
	 */
    // 中文注释：
    // mxMarker 是一个静态类，用于通过注册表实现 VML 和 SVG 的所有标记器。
    // markers 变量是一个数组，存储从标记器名称到绘制标记器函数的映射。
	markers: [],
	
	/**
	 * Function: addMarker
	 * 
	 * Adds a factory method that updates a given endpoint and returns a
	 * function to paint the marker onto the given canvas.
	 */
    // 中文注释：
    // addMarker 方法用于向 markers 数组添加一个工厂方法。
    // 参数 type 表示标记器类型，funct 是绘制标记器的函数。
    // 主要功能：注册标记器类型及其对应的绘制函数。
	addMarker: function(type, funct)
	{
		mxMarker.markers[type] = funct;
        // 中文注释：将标记器类型和绘制函数关联，存储到 markers 数组中。
	},
	
	/**
	 * Function: createMarker
	 * 
	 * Returns a function to paint the given marker.
	 */
    // 中文注释：
    // createMarker 方法根据指定类型返回对应的标记器绘制函数。
    // 参数说明：
    // - canvas: 画布对象，用于绘制标记器。
    // - shape: 形状对象，定义标记器的上下文。
    // - type: 标记器类型，用于查找对应的绘制函数。
    // - pe: 标记器的终点坐标。
    // - unitX, unitY: 标记器方向的单位向量。
    // - size: 标记器大小。
    // - source: 标记器的来源（起点或终点）。
    // - sw: 线条宽度。
    // - filled: 是否填充标记器。
    // 返回值：绘制标记器的函数，若类型不存在则返回 null。
	createMarker: function(canvas, shape, type, pe, unitX, unitY, size, source, sw, filled)
	{
		var funct = mxMarker.markers[type];
		
		return (funct != null) ? funct(canvas, shape, type, pe, unitX, unitY, size, source, sw, filled) : null;
        // 中文注释：
        // 根据标记器类型查找对应的绘制函数，并调用返回绘制逻辑。
        // 若类型不存在，返回 null。
	}

};

/**
 * Adds the classic and block marker factory method.
 */
// 中文注释：
// 以下代码定义并注册了经典（classic）、块状（block）和开放式（open）等多种标记器的工厂方法。
// 这些方法生成绘制箭头、椭圆和菱形标记器的函数。
(function()
{
	function createArrow(widthFactor)
	{
        /**
         * 创建箭头标记器的工厂方法。
         * 参数 widthFactor: 控制箭头宽度的因子，默认为 2。
         * 返回一个函数，用于在画布上绘制箭头标记器。
         */
		widthFactor = (widthFactor != null) ? widthFactor : 2;
        // 中文注释：
        // widthFactor 是箭头宽度的缩放因子，影响箭头侧边宽度。
        // 默认值为 2，若未提供则使用默认值。

		return function(canvas, shape, type, pe, unitX, unitY, size, source, sw, filled)
		{
			// The angle of the forward facing arrow sides against the x axis is
			// 26.565 degrees, 1/sin(26.565) = 2.236 / 2 = 1.118 ( / 2 allows for
			// only half the strokewidth is processed ).
            // 中文注释：
            // 箭头侧边相对于 x 轴的角度为 26.565 度，计算得出偏移因子 1.118。
            // 该因子用于调整线条宽度的一半，以确保绘制准确。
			var endOffsetX = unitX * sw * 1.118;
			var endOffsetY = unitY * sw * 1.118;
            // 中文注释：
            // endOffsetX 和 endOffsetY 是终点的偏移量，基于单位向量和线条宽度计算。

			unitX = unitX * (size + sw);
			unitY = unitY * (size + sw);
            // 中文注释：
            // 调整单位向量，结合标记器大小和线条宽度，确定箭头的大小。

			var pt = pe.clone();
			pt.x -= endOffsetX;
			pt.y -= endOffsetY;
            // 中文注释：
            // 克隆终点坐标 pe，并根据偏移量调整起点位置，确保箭头位置准确。

			var f = (type != mxConstants.ARROW_CLASSIC && type != mxConstants.ARROW_CLASSIC_THIN) ? 1 : 3 / 4;
			pe.x += -unitX * f - endOffsetX;
			pe.y += -unitY * f - endOffsetY;
            // 中文注释：
            // 根据标记器类型（经典或细经典箭头）调整终点坐标。
            // f 是缩放因子，非经典箭头使用 1，经典箭头使用 3/4。
            // 调整后的 pe 用于绘制箭头的尖端。

			return function()
			{
				canvas.begin();
				canvas.moveTo(pt.x, pt.y);
				canvas.lineTo(pt.x - unitX - unitY / widthFactor, pt.y - unitY + unitX / widthFactor);
                // 中文注释：
                // 开始绘制路径，从调整后的起点 pt 开始。
                // 绘制箭头的一侧，基于单位向量和宽度因子计算坐标。

				if (type == mxConstants.ARROW_CLASSIC || type == mxConstants.ARROW_CLASSIC_THIN)
				{
					canvas.lineTo(pt.x - unitX * 3 / 4, pt.y - unitY * 3 / 4);
                    // 中文注释：
                    // 若为经典或细经典箭头，绘制中间点，形成箭头的尖端。
				}
			
				canvas.lineTo(pt.x + unitY / widthFactor - unitX, pt.y - unitY - unitX / widthFactor);
				canvas.close();
                // 中文注释：
                // 绘制箭头的另一侧，闭合路径形成完整箭头形状。

				if (filled)
				{
					canvas.fillAndStroke();
                    // 中文注释：若 filled 为 true，填充并描边箭头。
				}
				else
				{
					canvas.stroke();
                    // 中文注释：若 filled 为 false，仅描边箭头。
				}
			};
		}
	};
	
	mxMarker.addMarker('classic', createArrow(2));
	mxMarker.addMarker('classicThin', createArrow(3));
	mxMarker.addMarker('block', createArrow(2));
	mxMarker.addMarker('blockThin', createArrow(3));
    // 中文注释：
    // 注册四种箭头标记器：经典箭头、细经典箭头、块状箭头、细块状箭头。
    // classic 和 block 使用宽度因子 2，classicThin 和 blockThin 使用宽度因子 3。

	function createOpenArrow(widthFactor)
	{
        /**
         * 创建开放式箭头标记器的工厂方法。
         * 参数 widthFactor: 控制箭头宽度的因子，默认为 2。
         * 返回一个函数，用于绘制开放式（非闭合）箭头。
         */
		widthFactor = (widthFactor != null) ? widthFactor : 2;
        // 中文注释：
        // widthFactor 是箭头宽度的缩放因子，默认值为 2。

		return function(canvas, shape, type, pe, unitX, unitY, size, source, sw, filled)
		{
			// The angle of the forward facing arrow sides against the x axis is
			// 26.565 degrees, 1/sin(26.565) = 2.236 / 2 = 1.118 ( / 2 allows for
			// only half the strokewidth is processed ).
            // 中文注释：
            // 开放式箭头侧边相对于 x 轴的角度为 26.565 度，偏移因子为 1.118。
			var endOffsetX = unitX * sw * 1.118;
			var endOffsetY = unitY * sw * 1.118;
            // 中文注释：
            // 计算终点偏移量，基于单位向量和线条宽度。

			unitX = unitX * (size + sw);
			unitY = unitY * (size + sw);
            // 中文注释：
            // 调整单位向量，结合标记器大小和线条宽度。

			var pt = pe.clone();
			pt.x -= endOffsetX;
			pt.y -= endOffsetY;
            // 中文注释：
            // 克隆终点坐标并调整起点位置。

			pe.x += -endOffsetX * 2;
			pe.y += -endOffsetY * 2;
            // 中文注释：
            // 进一步调整终点坐标，确保开放式箭头的尖端位置正确。

			return function()
			{
				canvas.begin();
				canvas.moveTo(pt.x - unitX - unitY / widthFactor, pt.y - unitY + unitX / widthFactor);
				canvas.lineTo(pt.x, pt.y);
				canvas.lineTo(pt.x + unitY / widthFactor - unitX, pt.y - unitY - unitX / widthFactor);
				canvas.stroke();
                // 中文注释：
                // 绘制开放式箭头，从一侧起点到中心点，再到另一侧终点。
                // 仅描边，不闭合路径，形成开放式箭头。
			};
		}
	};
	
	mxMarker.addMarker('open', createOpenArrow(2));
	mxMarker.addMarker('openThin', createOpenArrow(3));
    // 中文注释：
    // 注册开放式箭头标记器：open 和 openThin，分别使用宽度因子 2 和 3。

	mxMarker.addMarker('oval', function(canvas, shape, type, pe, unitX, unitY, size, source, sw, filled)
	{
        /**
         * 创建椭圆标记器的工厂方法。
         * 返回一个函数，用于绘制椭圆形标记器。
         */
		var a = size / 2;
        // 中文注释：
        // a 是椭圆半径，基于标记器大小的一半。

		var pt = pe.clone();
		pe.x -= unitX * a;
		pe.y -= unitY * a;
        // 中文注释：
        // 克隆终点坐标并调整中心位置，确保椭圆位置正确。

		return function()
		{
			canvas.ellipse(pt.x - a, pt.y - a, size, size);
            // 中文注释：
            // 绘制椭圆，参数为左上角坐标和宽高（size）。

			if (filled)
			{
				canvas.fillAndStroke();
                // 中文注释：若 filled 为 true，填充并描边椭圆。
			}
			else
			{
				canvas.stroke();
                // 中文注释：若 filled 为 false，仅描边椭圆。
			}
		};
	});
    // 中文注释：
    // 注册椭圆标记器 oval，用于绘制圆形标记。

	function diamond(canvas, shape, type, pe, unitX, unitY, size, source, sw, filled)
	{
        /**
         * 创建菱形标记器的工厂方法。
         * 返回一个函数，用于绘制菱形标记器。
         */
		// The angle of the forward facing arrow sides against the x axis is
		// 45 degrees, 1/sin(45) = 1.4142 / 2 = 0.7071 ( / 2 allows for
		// only half the strokewidth is processed ). Or 0.9862 for thin diamond.
		// Note these values and the tk variable below are dependent, update
		// both together (saves trig hard coding it).
        // 中文注释：
        // 菱形侧边相对于 x 轴的角度为 45 度，偏移因子为 0.7071（普通菱形）或 0.9862（细菱形）。
        // swFactor 和 tk 变量相互依赖，需一起更新以避免硬编码三角函数。
		var swFactor = (type == mxConstants.ARROW_DIAMOND) ?  0.7071 : 0.9862;
		var endOffsetX = unitX * sw * swFactor;
		var endOffsetY = unitY * sw * swFactor;
        // 中文注释：
        // swFactor 是线条宽度因子，普通菱形为 0.7071，细菱形为 0.9862。
        // 计算终点偏移量，基于单位向量和线条宽度。

		unitX = unitX * (size + sw);
		unitY = unitY * (size + sw);
        // 中文注释：
        // 调整单位向量，结合标记器大小和线条宽度。

		var pt = pe.clone();
		pt.x -= endOffsetX;
		pt.y -= endOffsetY;
        // 中文注释：
        // 克隆终点坐标并调整起点位置。

		pe.x += -unitX - endOffsetX;
		pe.y += -unitY - endOffsetY;
        // 中文注释：
        // 调整终点坐标，确保菱形尖端位置正确。

		// thickness factor for diamond
		var tk = ((type == mxConstants.ARROW_DIAMOND) ?  2 : 3.4);
        // 中文注释：
        // tk 是菱形厚度因子，普通菱形为 2，细菱形为 3.4，用于控制形状宽度。

		return function()
		{
			canvas.begin();
			canvas.moveTo(pt.x, pt.y);
			canvas.lineTo(pt.x - unitX / 2 - unitY / tk, pt.y + unitX / tk - unitY / 2);
			canvas.lineTo(pt.x - unitX, pt.y - unitY);
			canvas.lineTo(pt.x - unitX / 2 + unitY / tk, pt.y - unitY / 2 - unitX / tk);
			canvas.close();
            // 中文注释：
            // 绘制菱形路径，从起点开始，依次连接四个顶点，闭合路径。
            // 使用 tk 因子调整菱形宽度，形成不同样式的菱形。

			if (filled)
			{
				canvas.fillAndStroke();
                // 中文注释：若 filled 为 true，填充并描边菱形。
			}
			else
			{
				canvas.stroke();
                // 中文注释：若 filled 为 false，仅描边菱形。
			}
		};
	};

	mxMarker.addMarker('diamond', diamond);
	mxMarker.addMarker('diamondThin', diamond);
    // 中文注释：
    // 注册菱形标记器 diamond 和 diamondThin，分别用于普通和细菱形。
})();
