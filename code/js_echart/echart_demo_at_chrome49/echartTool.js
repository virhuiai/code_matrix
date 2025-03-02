var echartTool = echartTool || new function () {
};

/**
 * 防抖函数 - 用于优化频繁触发的事件,如resize、scroll等
 * @param {Function} fn - 需要防抖的函数
 * @param {number} delay - 延迟时间，单位毫秒,默认250ms
 * @return {Function} - 返回防抖处理后的函数
 *
 * 防抖原理:
 * 1. 在事件被触发n秒后再执行回调
 * 2. 如果在这n秒内又触发了事件,则重新计时
 * 3. 这样可以防止函数在短时间内频繁调用,提高性能
 *
 * 每次调用debounce都会创建一个新的timer变量实例
 *
 * 调用示例 例如
 * // 第1次触发resize（0ms）
 * timer = null
 * 清除timer（无效，因为是null）
 * 设置新timer，250ms后执行
 *
 * // 第2次触发resize（100ms）
 * timer = 上次设置的timer
 * 清除上次的timer（取消了原定在250ms的执行）
 * 设置新timer，250ms后执行
 *
 * // 第3次触发resize（200ms）
 * timer = 上次设置的timer
 * 清除上次的timer（取消了原定在350ms的执行）
 * 设置新timer，250ms后执行
 *
 * // 如果250ms内没有新的触发
 * 执行resize函数
 * timer被设为null
 *
 * 1. **为什么需要timer**
 *
 * - 没有timer的话，每次resize都会执行，可能会导致性能问题
 * - 使用timer可以确保在一段时间内的多次调用，最终只执行最后一次
 * - 通过清除和重设timer，实现了"重新计时"的功能
 *
 * 简单来说，`timer`就像一个"操作延迟执行的计时器"，每次触发事件时：
 *
 * - 如果已经存在计时器，就取消它
 * - 然后设置一个新的计时器
 * - 这样就保证了只有在停止触发事件一段时间后，才会真正执行操作
 */
echartTool.debounce = function(fn, delay) {
    // 定义定时器变量,用于清除上一次的定时器
    // 在闭包中声明timer变量，这意味着它会被返回的函数所共享和保持
    var timer = null;

    // 返回一个新函数,这个函数就是防抖处理后的函数
    return function() {
        // 如果已经存在定时器,则清除之前的定时器
        // 这样可以保证在delay时间内频繁触发,只会执行最后一次
        if (timer) {
            clearTimeout(timer);
        }

        // 设置新的定时器
        // delay如果没有传值则默认使用250毫秒
        timer = setTimeout(function() {
            // 调用原函数,确保正确的this上下文和参数传递
            // fn.apply(context, args);
            fn();
        }, delay || 250);
    };
};


/**
 * 初始化或重置echarts实例
 * @param {Object} belongNode - 所属节点对象，用于存储echarts实例
 * @param {string} nodeName - 节点名称，作为echarts实例的标识
 * @param {string} id - DOM元素的ID，用于初始化echarts的容器
 */
echartTool.init = function (belongNode, nodeName, id) {
    // 参数验证 1
    if (!belongNode || typeof belongNode !== 'object') {
        throw new Error('belongNode必须是一个对象');
    }
    // 参数验证 2
    if (!nodeName || typeof nodeName !== 'string') {
        throw new Error('nodeName必须是一个非空字符串');
    }
    // 参数验证 3
    if (!id || typeof id !== 'string') {
        throw new Error('id必须是一个非空字符串');
    }
    // 参数验证 4
    // 获取DOM元素
    var domElement = document.getElementById(id);
    if (!domElement) {
        throw new Error('未找到ID为' + id + '的DOM元素');
    }

    try {
        // 检查节点是否已存在
        if (belongNode.hasOwnProperty(nodeName)) {
            // 如果节点已存在，清除原有的echarts实例
            if (belongNode[nodeName] && typeof belongNode[nodeName].clear === 'function') {
                // 移除旧的resize监听器
                if (belongNode[nodeName]._resizeHandler) {
                    window.removeEventListener('resize', belongNode[nodeName]._resizeHandler);
                }
                belongNode[nodeName].clear();
            } else {
                throw new Error('原有节点不是有效的echarts实例');
            }
        } else {
            // 创建新的echarts实例
            // 确保echarts对象存在
            if (typeof echarts === 'undefined') {
                throw new Error('echarts库未加载');
            }
            // 在调用 echarts.init 时需保证容器已经有宽度和高度了。
            belongNode[nodeName] = echarts.init(domElement);
            // 响应容器大小的变化
            // 希望在浏览器宽度改变的时候，始终保持图表宽度是页面的 100%。
            // 监听页面的 resize 事件获取浏览器大小改变的事件，然后调用 echartsInstance.resize 改变图表的大小。
            // 创建防抖后的resize处理函数
            belongNode[nodeName]._resizeHandler = echartTool.debounce(function () {
                if (belongNode[nodeName]) {
                    belongNode[nodeName].resize();
                }
            }, 250);

            // 添加resize事件监听
            window.addEventListener('resize', belongNode[nodeName]._resizeHandler);
        }

        return belongNode[nodeName];
    } catch (error) {
        console.error('初始化echarts实例失败:', error);
        throw error;
    }
};
<<<<<<< HEAD
<<<<<<< HEAD
// 使用示例：
/*
try {
    // 创建一个对象来存储echarts实例
    var myCharts = {};

    // 初始化图表
    echartTool.init(myCharts, 'lineChart', 'chartContainerId');

    // 使用实例
    myCharts.lineChart.setOption({
        // echarts配置项
    });
} catch (error) {
    console.error('图表初始化失败:', error);
}
*/
=======
=======
>>>>>>> dev

/**
 * 为echarts配置项设置标题文本
 * @param {Object} option - echarts的配置项对象
 * @param {string|null} title_text - 标题文本，如果为null则不设置标题
 * @return {Object} - 返回更新后的配置项对象
 *
 * 使用示例:
 * var option = {};
 * echartTool.setTitle(option, '月度销售数据');
 */
echartTool.setTitle = function(option, title_text) {
    // 参数验证：确保option是对象
    if (!option || typeof option !== 'object') {
        throw new Error('option必须是一个对象');
    }

    // 如果传入了标题文本
    if (title_text) {
        // 参数验证：确保title_text是字符串
        if (typeof title_text !== 'string') {
            throw new Error('title_text必须是字符串类型');
        }

        // 如果option中没有title配置，则初始化为空对象
        if (!option.title) {
            option.title = {};
        }

        // 设置标题文本
        option.title.text = title_text;
    }

    // 返回更新后的配置项
    return option;
};

/**
 * 移除echarts配置项中的标题
 * @param {Object} option - echarts的配置项对象
 * @return {Object} - 返回更新后的配置项对象
 *
 * 使用示例:
 * var option = {
 *     title: { text: '月度销售数据' }
 * };
 * echartTool.unsetTitle(option);
 */
echartTool.unsetTitle = function(option) {
    // 参数验证：确保option是对象
    if (!option || typeof option !== 'object') {
        throw new Error('option必须是一个对象');
    }

    // 如果存在title配置，则移除它
    if (option.hasOwnProperty('title')) {
        delete option.title;
    }

    // 返回更新后的配置项
    return option;
};
/**
 * 交换图表的X轴和Y轴
 * @param {Object} option - echarts的配置项
 * @returns {Object} 返回交换后的配置项
 */
echartTool.swapXYAxis = function(option) {
    // 参数校验
    if (!option || typeof option !== 'object') {
        console.warn('swapXYAxis: option参数必须是一个对象');
        return option;
    }

    try {
        // 深拷贝配置项,避免修改原对象
        var newOption = JSON.parse(JSON.stringify(option));
        // 使用临时变量交换x轴和y轴配置
        var tempAxis = newOption.xAxis;
        newOption.xAxis = newOption.yAxis;
        newOption.yAxis = tempAxis;

        return newOption;
    } catch (error) {
        console.error('swapXYAxis执行出错:', error);
        return option;
    }
};

/**
 * 为echarts配置项设置最大值和最小值的标记点
 * @param {Object} option - echarts的配置项对象
 * @param {Object} [customConfig] - 自定义配置项
 * @param {string} [customConfig.maxName='最大值'] - 最大值标记的名称
 * @param {string} [customConfig.minName='最小值'] - 最小值标记的名称
 * @return {Object} - 返回更新后的配置项对象
 *
 * 使用示例:
 * var option = {};
 * echartTool.setMarkPoint(option, { maxName: '最高温', minName: '最低温' });
 */
echartTool.setMarkPoint = function(option, customConfig) {
    // 参数验证
    if (!option || typeof option !== 'object') {
        throw new Error('option必须是一个对象');
    }

    // 支持markPoint的图表类型
    var supportedTypes = ['line', 'bar', 'scatter', 'custom'];

    // 检查是否有series配置
    if (!option.series || !Array.isArray(option.series) || option.series.length === 0) {
        console.warn('没有找到series配置');
        return option;
    }

    // 默认配置
    var defaultConfig = {
        maxName: '最大值',
        minName: '最小值'
    };

    // 合并自定义配置
    var config = customConfig || {};
    config.maxName = config.maxName || defaultConfig.maxName;
    config.minName = config.minName || defaultConfig.minName;

    // 遍历所有series
    for (var i = 0; i < option.series.length; i++) {
        var series = option.series[i];
        debugger

        // 检查图表类型是否支持markPoint
        if (series.type && supportedTypes.indexOf(series.type) !== -1) {
            // 初始化markPoint配置
            if (!series.markPoint) {
                series.markPoint = {
                    data: []
                };
            }

            // 确保markPoint.data是数组
            if (!Array.isArray(series.markPoint.data)) {
                series.markPoint.data = [];
            }

            // 设置最大值和最小值标记
            var markPointData = [
                { type: 'max', name: config.maxName },
                { type: 'min', name: config.minName }
            ];

            // 更新或添加标记点数据
            series.markPoint.data = markPointData;
        } else {
            console.warn('图表类型 ' + (series.type || '未知') + ' 不支持标记点');
        }
    }

    return option;
};

// 添加新的addLegend方法
echartTool.addLegend = function(option) {
    // 深拷贝option以避免修改原对象
    var newOption = JSON.parse(JSON.stringify(option));

    // 确保series存在且是数组
    if (!newOption.series || !Array.isArray(newOption.series)) {
        return newOption;
    }

    // 从series中提取所有name
    var legendData = [];
    for (var i = 0; i < newOption.series.length; i++) {
        if (newOption.series[i].name) {
            legendData.push(newOption.series[i].name);
        }
    }

    // 如果没有legend配置，则创建一个
    if (!newOption.legend) {
        newOption.legend = {};
    }

    // 设置legend的data
    newOption.legend.data = legendData;

    return newOption;
}


/**
 * 生成瀑布图的占位数据
 * @param {Array} realData - 实际数据数组
 * @param {boolean} [isReverse=false] - 是否反向计算
 * @returns {Array} - 返回占位数据数组
 */
echartTool.generateWaterfallPlaceholder = function(realData, isReverse) {
    if (!Array.isArray(realData) || realData.length === 0) {
        return [];
    }

    if (isReverse) {
        var placeholder = [];
        var sum = 0;
        var temp = 0;

        // 处理最后一个位置
        placeholder[realData.length - 1] = 0;
        // 处理中间的数据位置（从倒数第二个位置开始，到第二个位置）
        for (var i = realData.length - 2; i > 0; i--) {
            sum += realData[i + 1];
            placeholder[i] = sum;
        }

        // 处理第一个位置（通常是总计）
        placeholder[0] = 0;

    }else{
        var placeholder = [];
        var sum = 0;
        var temp = 0;

        // 处理第一个位置（通常是总计）
        placeholder.push(0);

        // 处理中间的数据位置
        for (var i = 1; i < realData.length - 1; i++) {
            temp = sum;
            placeholder.push(temp);
            sum += realData[i];
        }

        // 处理最后一个位置
        placeholder.push(sum);
    }




    return placeholder;
}


/**
 * 生成完整的瀑布图配置
 * @param {Object} option - 原始配置对象
 * @returns {Object} - 返回处理后的配置对象
 */
echartTool.generateWaterfallOption = function (option,stackName) {
    if (!option || !option.series || !option.series[0] || !option.series[0].data) {
        return option;
    }

    if(option.series.length !=1){
        console.warn('只支持单个series的情况');
        return option;
    }


    var realData = option.series[0].data;
    var placeholder = this.generateWaterfallPlaceholder(realData,false);
    console.log(JSON.stringify(placeholder))

    // 深拷贝option以避免修改原对象
    var newSeries = {}
    newSeries.type = 'bar';           // 图表类型：柱状图
    newSeries.stack = stackName;         // 堆叠标识
    newSeries.data = placeholder;
    newSeries.name = '占位';
    newSeries.itemStyle = {
        borderColor: 'transparent',  // 边框颜色：透明
            color: 'transparent'         // 填充颜色：透明
    };
    newSeries.emphasis = {            // 高亮状态样式
        itemStyle: {
            borderColor: 'transparent',
                color: 'transparent'
        }
    };


    option.series.unshift(newSeries);

    return option;
}
<<<<<<< HEAD
>>>>>>> dev
=======
>>>>>>> dev
