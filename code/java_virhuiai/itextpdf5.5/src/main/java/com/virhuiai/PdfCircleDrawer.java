package com.virhuiai;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.awt.geom.Point2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfCircleDrawer {
    /**
     * 在PDF的指定坐标位置画小圆
     * @param inputPdf 输入PDF文件路径
     * @param outputPdf 输出PDF文件路径
     * @param points 要画圆的坐标点列表
     * @param pageNumber 要在哪一页画圆（从1开始）
     */
    public static void drawCirclesOnPdf(String inputPdf, String outputPdf,
                                        List<Point2D.Float> points, int pageNumber)
            throws IOException, DocumentException {

        PdfReader reader = new PdfReader(inputPdf);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPdf));

        // 获取指定页面的画布
        PdfContentByte canvas = stamper.getOverContent(pageNumber);

        // 在每个坐标点画圆
        for (Point2D.Float point : points) {
            drawCircle(canvas, point.x, point.y);
        }

        stamper.close();
        reader.close();
    }

    /**
     * 画单个圆
     */
    private static void drawCircle(PdfContentByte canvas, float x, float y) {
        canvas.saveState();

        // 设置圆的颜色为红色
        canvas.setColorFill(BaseColor.RED);
        canvas.setColorStroke(BaseColor.RED);

        // 画一个半径为3的圆
        canvas.circle(x, y, 3f);

        // 填充圆
        canvas.fill();

        canvas.restoreState();
    }

    /**
     * 带自定义选项的画圆方法
     */
    public static void drawCirclesWithOptions(String inputPdf, String outputPdf,
                                              List<Point2D.Float> points,
                                              int pageNumber,
                                              float radius,
                                              BaseColor color,
                                              boolean filled)
            throws IOException, DocumentException {

        PdfReader reader = new PdfReader(inputPdf);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPdf));

        PdfContentByte canvas = stamper.getOverContent(pageNumber);

        for (Point2D.Float point : points) {
            canvas.saveState();

            if (filled) {
                // 画实心圆
                canvas.setColorFill(color);
                canvas.circle(point.x, point.y, radius);
                canvas.fill();
            } else {
                // 画空心圆
                canvas.setColorStroke(color);
                canvas.setLineWidth(1f);
                canvas.circle(point.x, point.y, radius);
                canvas.stroke();
            }

            canvas.restoreState();
        }

        stamper.close();
        reader.close();
    }

    /**
     * 使用示例
     */
    public static void main(String[] args) throws IOException, DocumentException {
//        // 假设您已经获得了坐标点
//        List<Point2D.Float> points = List.of(
//                new Point2D.Float(100f, 100f),
//                new Point2D.Float(200f, 200f),
//                new Point2D.Float(300f, 300f),
//                new Point2D.Float(400f, 400f)
//        );
//
//        // 示例1：在第1页画默认的红色小圆
//        drawCirclesOnPdf("input.pdf", "output1.pdf", points, 1);
//
//        // 示例2：在第1页画蓝色的大圆（空心）
//        drawCirclesWithOptions("input.pdf", "output2.pdf", points, 1,
//                5f, BaseColor.BLUE, false);
//
//        // 示例3：如果您有表格单元格的四个角点坐标
//        List<Point2D.Float> cellCorners = List.of(
//                new Point2D.Float(50f, 50f),    // 左上角
//                new Point2D.Float(150f, 50f),   // 右上角
//                new Point2D.Float(50f, 100f),   // 左下角
//                new Point2D.Float(150f, 100f)   // 右下角
//        );
//
//        // 在四个角点画不同颜色的圆
//        drawCellCorners("input.pdf", "output3.pdf", cellCorners, 1);
    }

    /**
     * 在表格单元格的四个角画不同颜色的圆
     */
    public static void drawCellCorners(String inputPdf, String outputPdf,
                                       List<Point2D.Float> corners, int pageNumber)
            throws IOException, DocumentException {

        if (corners.size() != 4) {
            throw new IllegalArgumentException("需要提供4个角点坐标");
        }

        PdfReader reader = new PdfReader(inputPdf);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPdf));

        PdfContentByte canvas = stamper.getOverContent(pageNumber);

        BaseColor[] colors = {
                BaseColor.RED,     // 左上角
                BaseColor.BLUE,    // 右上角
                BaseColor.GREEN,   // 左下角
                BaseColor.ORANGE   // 右下角
        };

        for (int i = 0; i < 4; i++) {
            Point2D.Float corner = corners.get(i);
            canvas.saveState();
            canvas.setColorFill(colors[i]);
            canvas.circle(corner.x, corner.y, 4f);
            canvas.fill();
            canvas.restoreState();
        }

        stamper.close();
        reader.close();
    }
}
