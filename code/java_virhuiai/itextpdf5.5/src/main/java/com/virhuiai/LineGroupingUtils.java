package com.virhuiai;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * 线段分组工具类
 * 提供将相连线段分组的功能，用于识别表格边框
 */
public class LineGroupingUtils {



    /**
     * 深度优先搜索算法，递归查找所有相连的线段
     * @param lineList 所有线段的列表
     * @param index 当前正在处理的线段索引
     * @param visited 访问标记数组
     * @param group 当前分组，用于收集相连的线段
     */
    private static void dfs(List<Line2D> lineList, int index, boolean[] visited, List<Line2D> group) {
        // 标记当前线段已被访问
        visited[index] = true;

        // 将当前线段添加到分组中
        group.add(lineList.get(index));

        // 获取当前线段对象
        Line2D currentLine = lineList.get(index);

        // 遍历所有其他线段
        for (int i = 0; i < lineList.size(); i++) {
            // 如果线段i未被访问过，且与当前线段相连
            if (!visited[i] && GeometryUtils.isConnected(currentLine, lineList.get(i))) {
                // 递归访问相连的线段，继续查找与它相连的其他线段
                dfs(lineList, i, visited, group);
            }
        }
    }

    /**
     * 将相连的线段分组，属于同一个表格的线段会被分到同一组
     * @param lineList 输入的线段列表
     * @return 分组后的线段列表，每个子列表代表一个表格的所有线段
     */
    public static List<List<Line2D>> groupConnectedLines(List<Line2D> lineList) {
        // 最终结果：存储所有分组后的线段集合
        List<List<Line2D>> result = new ArrayList<>();

        // 处理空输入的情况
        if (lineList == null || lineList.isEmpty()) {
            return result;
        }

        // 创建访问标记数组，用于记录每条线是否已被处理
        // visited[i] = true 表示第i条线已被访问过
        boolean[] visited = new boolean[lineList.size()];

        // 遍历所有线段
        for (int i = 0; i < lineList.size(); i++) {
            // 如果当前线段还未被访问过，说明它属于一个新的表格
            if (!visited[i]) {
                // 创建一个新的分组，用于存储当前表格的所有线段
                List<Line2D> connectedGroup = new ArrayList<>();

                // 使用深度优先搜索找出所有与当前线段相连的线段
                dfs(lineList, i, visited, connectedGroup);

                // 将这个表格的所有线段作为一个分组添加到结果中
                result.add(connectedGroup);
            }
        }

        return result;
    }

    /**
     * 计算线段组的边界框
     * @param lines 线段列表
     * @return 边界框 [minX, minY, maxX, maxY]
     */
    public static double[] getBoundingBox(List<Line2D> lines) {
        if (lines == null || lines.isEmpty()) {
            return null;
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Line2D line : lines) {
            minX = Math.min(minX, Math.min(line.getX1(), line.getX2()));
            minY = Math.min(minY, Math.min(line.getY1(), line.getY2()));
            maxX = Math.max(maxX, Math.max(line.getX1(), line.getX2()));
            maxY = Math.max(maxY, Math.max(line.getY1(), line.getY2()));
        }

        return new double[]{minX, minY, maxX, maxY};
    }
}
