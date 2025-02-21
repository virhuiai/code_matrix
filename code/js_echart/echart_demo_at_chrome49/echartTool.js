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
