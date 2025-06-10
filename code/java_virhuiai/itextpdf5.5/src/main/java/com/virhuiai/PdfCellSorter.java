package com.virhuiai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 对PDF单元格进行排序，从上到下，从左到右。
 * >
 * > 1. PDF坐标系：左下角是原点
 * > 2. 这意味着：
 * >    - y值越大，位置越靠上
 * >    - x值越小，位置越靠左
 * >
 * > 排序规则：
 * > 1. 先按y坐标排序（从上到下），y值大的在前
 * > 2. 如果y坐标相同（在同一行），则按x坐标排序（从左到右），x值小的在前
 */
public class PdfCellSorter {


    /**
     * 对PDF单元格进行排序
     * 排序规则：从上到下，从左到右
     * @param cells 待排序的单元格列表
     * @return 排序后的单元格列表
     */
    public static List<PdfCellPos> sortCells(List<PdfCellPos> cells) {
        if (cells == null || cells.isEmpty()) {
            return cells;
        }

        // 创建一个新的列表，避免修改原列表
        List<PdfCellPos> sortedCells = new ArrayList<>(cells);

        // 定义排序规则
        sortedCells.sort(new Comparator<PdfCellPos>() {
            @Override
            public int compare(PdfCellPos cell1, PdfCellPos cell2) {
                // 定义一个阈值，用于判断两个单元格是否在同一行
                double rowThreshold = 5.0; // 可以根据实际情况调整

                // 获取两个单元格的中心y坐标
                double y1Center = (cell1.getyTop() + cell1.getyBtm()) / 2;
                double y2Center = (cell2.getyTop() + cell2.getyBtm()) / 2;

                // 判断是否在同一行
                if (Math.abs(y1Center - y2Center) < rowThreshold) {
                    // 在同一行，按x坐标从左到右排序（x值小的在前）
                    return Double.compare(cell1.getxLeft(), cell2.getxLeft());
                } else {
                    // 不在同一行，按y坐标从上到下排序（y值大的在前）
                    return Double.compare(cell2.getyTop(), cell1.getyTop());
                }
            }
        });

        return sortedCells;
    }


    /**
     * 使用Lambda表达式的简化版本
     */
    public static List<PdfCellPos> sortCellsLambda(List<PdfCellPos> cells) {
        if (cells == null || cells.isEmpty()) {
            return cells;
        }

        List<PdfCellPos> sortedCells = new ArrayList<>(cells);
        double rowThreshold = 5.0;

        sortedCells.sort((cell1, cell2) -> {
            double y1Center = (cell1.getyTop() + cell1.getyBtm()) / 2;
            double y2Center = (cell2.getyTop() + cell2.getyBtm()) / 2;

            if (Math.abs(y1Center - y2Center) < rowThreshold) {
                return Double.compare(cell1.getxLeft(), cell2.getxLeft());
            } else {
                return Double.compare(cell2.getyTop(), cell1.getyTop());
            }
        });

        return sortedCells;
    }



    /**
     * 更精确的排序方法，考虑单元格可能有重叠的情况
     */
    public static List<PdfCellPos> sortCellsAdvanced(List<PdfCellPos> cells) {
        if (cells == null || cells.isEmpty()) {
            return cells;
        }

        List<PdfCellPos> sortedCells = new ArrayList<>(cells);

        sortedCells.sort((cell1, cell2) -> {
            // 首先判断垂直位置关系
            // 如果cell1完全在cell2上方（没有垂直重叠）
            if (cell1.getyBtm() >= cell2.getyTop()) {
                return -1; // cell1在前
            }
            // 如果cell2完全在cell1上方
            if (cell2.getyBtm() >= cell1.getyTop()) {
                return 1; // cell2在前
            }

            // 如果有垂直重叠，认为在同一行，按水平位置排序
            return Double.compare(cell1.getxLeft(), cell2.getxLeft());
        });

        return sortedCells;
    }
}
