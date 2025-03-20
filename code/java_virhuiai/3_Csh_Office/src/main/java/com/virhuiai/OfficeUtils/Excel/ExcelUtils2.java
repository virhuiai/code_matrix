package com.virhuiai.OfficeUtils.Excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Excel工具类
 * 支持对Excel文件的基本读写操作，包括.xls和.xlsx格式
 * 提供普通API和流式API两种处理方式
 *
 * https://poi.apache.org/components/spreadsheet/how-to.html#xssf_sax_api
 */
public class ExcelUtils2 {

    /**
     * 工作簿类型枚举
     */
    public enum WorkbookType {
        /**
         * .xls格式，使用HSSF
         */
        XLS,
        /**
         * .xlsx格式，使用XSSF
         */
        XLSX,
        /**
         * .xlsx格式，使用流式SXSSF，适用于大数据量
         */
        STREAMING_XLSX
    }

    /**
     * 创建新的工作簿
     *
     * @param type 工作簿类型
     * @return 工作簿对象
     */
    public static Workbook createWorkbook(WorkbookType type) {
        switch (type) {
            case XLS:
                // 创建.xls格式的工作簿
                return new HSSFWorkbook();
            case XLSX:
                // 创建.xlsx格式的工作簿
                return new XSSFWorkbook();
            case STREAMING_XLSX:
                // 创建支持流式写入的工作簿，默认保留100行在内存中
                return new SXSSFWorkbook(100);
            default:
                throw new IllegalArgumentException("不支持的工作簿类型");
        }
    }

    /**
     * 创建流式工作簿，指定内存中保留的行数
     *
     * @param rowAccessWindowSize 内存中保留的行数，设置为-1表示不自动刷新行数据
     * @return 流式工作簿对象
     */
    public static SXSSFWorkbook createStreamingWorkbook(int rowAccessWindowSize) {
        return new SXSSFWorkbook(rowAccessWindowSize);
    }

    /**
     * 读取Excel文件
     *
     * @param filePath Excel文件路径
     * @return 工作簿对象
     * @throws IOException 读取文件异常
     */
    public static Workbook readExcel(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            // 根据文件扩展名自动判断工作簿类型
            if (filePath.toLowerCase().endsWith(".xlsx")) {
                return new XSSFWorkbook(fis);
            } else if (filePath.toLowerCase().endsWith(".xls")) {
                return new HSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("不支持的文件类型，仅支持.xls和.xlsx格式");
            }
        }
    }

    /**
     * 将工作簿写入文件
     *
     * @param workbook 工作簿对象
     * @param filePath 输出文件路径
     * @throws IOException 写入文件异常
     */
    public static void writeExcel(Workbook workbook, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            fos.flush();

            // 如果是流式工作簿，需要清理临时文件
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
        } finally {
            // 关闭工作簿，释放资源
            workbook.close();
        }
    }

    /**
     * 创建工作表
     *
     * @param workbook 工作簿对象
     * @param sheetName 工作表名称
     * @return 创建的工作表
     */
    public static Sheet createSheet(Workbook workbook, String sheetName) {
        return workbook.createSheet(sheetName);
    }

    /**
     * 设置单元格的值
     *
     * @param cell 单元格对象
     * @param value 单元格值
     */
    public static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }

        // 根据不同的数据类型设置单元格的值
        if (value instanceof String) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Double) value);
        } else if (value instanceof Integer) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Long) value);
        } else if (value instanceof Date) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Date) value);
        } else if (value instanceof Boolean) {
            cell.setCellType(CellType.BOOLEAN);
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 创建单元格样式
     *
     * @param workbook 工作簿对象
     * @return 单元格样式
     */
    public static CellStyle createCellStyle(Workbook workbook) {
        return workbook.createCellStyle();
    }

    /**
     * 创建标题行样式
     *
     * @param workbook 工作簿对象
     * @return 标题行样式
     */
    public static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置背景色为浅灰色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        // 创建字体
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        // 设置居中对齐
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    /**
     * 获取单元格的值
     *
     * @param cell 单元格对象
     * @return 单元格的值
     */
    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        // 根据单元格类型获取相应的值
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // 判断是否为日期类型
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                // 尝试获取公式计算结果
                try {
                    return cell.getNumericCellValue();
                } catch (Exception e) {
                    try {
                        return cell.getStringCellValue();
                    } catch (Exception ex) {
                        return cell.getCellFormula();
                    }
                }
            case BLANK:
                return "";
            default:
                return null;
        }
    }

    /**
     * 从Excel读取数据到List
     *
     * @param filePath Excel文件路径
     * @param sheetIndex 工作表索引
     * @param startRow 开始行（从0开始）
     * @param columnCount 列数
     * @return 数据列表，每行数据为一个List<Object>
     * @throws IOException 读取文件异常
     */
    public static List<List<Object>> readExcelToList(String filePath, int sheetIndex, int startRow, int columnCount) throws IOException {
        List<List<Object>> dataList = new ArrayList<>();

        try (Workbook workbook = readExcel(filePath)) {
            if (sheetIndex >= workbook.getNumberOfSheets()) {
                throw new IllegalArgumentException("工作表索引超出范围");
            }

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            // 循环读取每一行
            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                List<Object> rowData = new ArrayList<>();
                // 读取指定列数的数据
                for (int j = 0; j < columnCount; j++) {
                    Cell cell = row.getCell(j);
                    rowData.add(getCellValue(cell));
                }
                dataList.add(rowData);
            }
        }

        return dataList;
    }

    /**
     * 将数据写入Excel（普通方式）
     *
     * @param dataList 数据列表，每行数据为一个List<Object>
     * @param headers 表头列表
     * @param filePath 输出文件路径
     * @param sheetName 工作表名称
     * @param type 工作簿类型
     * @throws IOException 写入文件异常
     */
    public static void writeListToExcel(List<List<Object>> dataList, List<String> headers,
                                        String filePath, String sheetName, WorkbookType type) throws IOException {
        // 创建工作簿
        Workbook workbook = createWorkbook(type);
        // 创建工作表
        Sheet sheet = createSheet(workbook, sheetName);
        // 创建标题样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        // 创建普通单元格样式
        CellStyle cellStyle = createCellStyle(workbook);

        // 创建表头
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        // 写入数据
        for (int i = 0; i < dataList.size(); i++) {
            Row row = sheet.createRow(i + 1);  // 从第二行开始写入数据
            List<Object> rowData = dataList.get(i);

            for (int j = 0; j < rowData.size(); j++) {
                Cell cell = row.createCell(j);
                setCellValue(cell, rowData.get(j));
                cell.setCellStyle(cellStyle);
            }
        }

        // 自动调整列宽
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
            // 设置列宽度不超过50个字符
            int currentWidth = sheet.getColumnWidth(i);
            if (currentWidth > 255 * 256) {
                sheet.setColumnWidth(i, 255 * 256);
            }
        }

        // 写入文件
        writeExcel(workbook, filePath);
    }

    /**
     * 将数据写入Excel（流式写入，适用于大数据量）
     *
     * @param dataList 数据列表，每行数据为一个List<Object>
     * @param headers 表头列表
     * @param filePath 输出文件路径
     * @param sheetName 工作表名称
     * @param rowAccessWindowSize 内存中保留的行数
     * @throws IOException 写入文件异常
     */
    public static void writeListToExcelStreaming(List<List<Object>> dataList, List<String> headers,
                                                 String filePath, String sheetName, int rowAccessWindowSize) throws IOException {
        // 创建流式工作簿
        SXSSFWorkbook workbook = createStreamingWorkbook(rowAccessWindowSize);
        // 设置是否压缩临时文件，对于大文件建议开启
        workbook.setCompressTempFiles(true);

        // 创建工作表
        Sheet sheet = createSheet(workbook, sheetName);
        // 创建标题样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        // 创建普通单元格样式
        CellStyle cellStyle = createCellStyle(workbook);

        // 创建表头
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        // 写入数据
        for (int i = 0; i < dataList.size(); i++) {
            Row row = sheet.createRow(i + 1);  // 从第二行开始写入数据
            List<Object> rowData = dataList.get(i);

            for (int j = 0; j < rowData.size(); j++) {
                Cell cell = row.createCell(j);
                setCellValue(cell, rowData.get(j));
                cell.setCellStyle(cellStyle);
            }

            // 每写入1000行数据，手动刷新到磁盘
            if (rowAccessWindowSize == -1 && (i + 1) % 1000 == 0) {
                ((SXSSFSheet) sheet).flushRows(100);  // 保留最近的100行，其余写入磁盘
            }
        }

        // 写入文件
        writeExcel(workbook, filePath);
    }





    /**
     * exampleUsage

     * 示例方法：演示如何使用本工具类
     */
    public static void main(String[] args) throws IOException {
        // 创建测试数据
        List<List<Object>> data = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            List<Object> row = new ArrayList<>();
            row.add("测试" + i);
            row.add(i);
            row.add(i * 1.5);
            row.add(new Date());
            data.add(row);
        }

        // 创建表头
        List<String> headers = new ArrayList<>();
        headers.add("名称");
        headers.add("序号");
        headers.add("金额");
        headers.add("日期");

        // 1. 普通方式写入小数据量Excel
        writeListToExcel(data, headers, "普通测试.xlsx", "测试工作表", WorkbookType.XLSX);

        // 2. 流式方式写入大数据量Excel
        writeListToExcelStreaming(data, headers, "流式测试.xlsx", "测试工作表", 100);

        // 3. 读取Excel数据
        List<List<Object>> readData = readExcelToList("普通测试.xlsx", 0, 1, 4);
        System.out.println("读取的数据行数: " + readData.size());
    }
}
