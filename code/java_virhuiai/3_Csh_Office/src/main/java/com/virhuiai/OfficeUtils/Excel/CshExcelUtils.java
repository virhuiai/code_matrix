package com.virhuiai.OfficeUtils.Excel;

import com.virhuiai.CshLogUtils.CshLogUtils;
import com.virhuiai.File.CshFileUtils;
import com.virhuiai.OfficeUtils.Excel.obj.BiMap;
import com.virhuiai.OfficeUtils.Excel.obj.DataBusConstants;
import com.virhuiai.OfficeUtils.Excel.obj.ExcelProcessingException;
import org.apache.commons.logging.Log;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiConsumer;

public class CshExcelUtils {
    // 日志对象
    private static Log LOG = CshLogUtils.createLogExtended(CshExcelUtils.class);

    /**
     * 处理Excel文件并生成字符串列表映射
     *
     * @param excel文件路径 Excel文件的完整路径
     * @param sheetName
     * @return 包含所有行列数据的二维列表
     * @throws ExcelProcessingException 当Excel处理出现异常时抛出
     *
     */
    public static List<List<String>> parseExcelToStringMatrix(String excel文件路径, String sheetName, int maxCol) {
        // 存储所有行的数据
        List<List<String>> rs = new ArrayList<>();

        try (Workbook wb = CshExcelUtils.get1Wb(excel文件路径)) {
            // 获取指定名称的工作表
            Sheet s = CshExcelUtils.get2SheetByName(wb, sheetName);

            // 获取工作表的最后一行的索引
            int rowIndexMax = s.getLastRowNum();

            // 遍历每一行
            for (int rowIndex = 0; rowIndex <= rowIndexMax; rowIndex++) {
                // 获取当前行对象
                Row cr = s.getRow(rowIndex);

                // 跳过空行
                if (null == cr) {
                    continue;
                }

                // 创建当前行的数据列表
                List<String> 行数据列表 = new ArrayList<>();
                rs.add(行数据列表);

                // 限制最大列数为，避免处理过多无用数据
                int 最大列数 = Math.min(maxCol, cr.getLastCellNum());

                // 处理行中的每个单元格
                CshExcelUtils.processRowCells(cr, 最大列数, (列索引, 单元格值) -> {
                    行数据列表.add(单元格值);
                });
            }
            return rs;

        } catch (IOException 输入输出异常) {
            throw new ExcelProcessingException(
                    "EXCEL_PROCESSING_ERROR",
                    "处理Excel文件时发生错误",
                    输入输出异常
            );
        }
    }

    /**
     * 解析Excel数据行到键值对列表
     * CshExcelUtils.parseExcelDataRowsToMapList
     * 将Excel工作表中的数据行解析为键值对映射列表
     * @param sheet Excel工作表对象
     * @param biMap 列索引和列名之间的映射关系
     * @param startRowNum 开始解析的行号(从0开始计数，而非文档注释中说的从1开始)
     * @return 包含每行数据的键值对列表
     */
    public static List<Map<String, String>> parseExcelDataRowsToMapList(Sheet sheet, BiMap biMap, int startRowNum) { // 修复：添加了泛型参数
        // 创建一个列表用于存储每行的数据映射
        List<Map<String, String>> dataRowList = new ArrayList<>();

        // 参数校验，防止空指针异常
        if (sheet == null || biMap == null) {
            LOG.error("工作表或双向映射为空"); // 修改：翻译了错误信息
            return dataRowList;  // 返回空列表而不是抛出异常
        }

        // 从BiMap中获取列索引到列名称的映射关系
        Map<Integer, String> indexToNameMap = biMap.getIndexToNameMap();

        // 如果映射为空，记录警告并返回
        if (indexToNameMap == null || indexToNameMap.isEmpty()) {
            LOG.warn("索引到名称的映射为空，不会处理任何数据"); // 修改：翻译了警告信息
            return dataRowList;
        }

        // 获取Excel表中的最大行号
        int maxRowNum = sheet.getLastRowNum();

        // 校正startRowNum，确保不小于0
        if (startRowNum < 0) {
            LOG.warn("起始行号为负数，设置为0");
            startRowNum = 0;
        }

        // 从指定的起始行开始遍历每一行
        for (int rowIndex = startRowNum; rowIndex <= maxRowNum; rowIndex++) {
            // 获取当前行
            Row row = sheet.getRow(rowIndex);
            // 如果行为空，则跳过此行
            if (null == row) {
                continue;
            }

            // 获取实际的列数，并限制最大列数为100（防止过大的数据集）
            // getLastCellNum返回的是最后一个单元格的索引+1，所以这里判断是否为0
            int lastCellNum = row.getLastCellNum();
            if (lastCellNum <= 0) {
                continue;  // 跳过没有单元格的行
            }

            int maxColumnNum = Math.min(100, lastCellNum);

            // 创建一个映射来存储当前行的数据（列名->值）
            // Java 8 引入了"effectively final"的概念，即如果一个变量在初始化后没有被重新赋值，它就被视为实际上是 final 的。
            Map<String, String> rowDataMap = new HashMap<>();
            // 创建一个final变量以便在lambda表达式中使用
            final int finalRowIndex = rowIndex;

            // 是否找到了至少一个有效的单元格值
            // 这里使用了一个只有一个元素的布尔数组，而不是普通的布尔变量，主要是因为：
            //    1。在 lambda 表达式中使用：Java 8 的 lambda 表达式要求使用的外部变量必须是 final 或实际上 final（effectively final）的。
            //    2。需要可变引用：在 lambda 表达式内部需要修改这个标志的值（将其设置为 true）。普通的 final 布尔变量一旦初始化就不能修改，但数组的元素可以修改，即使数组引用本身是 final 的。
//            final boolean[] hasValidData = {false};


            // 处理行中的每个单元格
            processRowCells(row, maxColumnNum, (columnIndex, cellValue) -> {
                // 只处理非空值
                if (null != cellValue && !cellValue.isEmpty()) {
                    // 检查该列索引是否在映射关系中定义
                    if (indexToNameMap.containsKey(columnIndex)) {
                        // 根据列索引获取列名
                        String columnName = indexToNameMap.get(columnIndex);
                        if (columnName != null && !columnName.isEmpty()) {
                            // 将列名和单元格值作为键值对添加到行数据映射中
                            rowDataMap.put(columnName, cellValue);
//                            hasValidData[0] = true;  // 标记找到了有效数据
                        }
                    } else {
                        // 如果列索引未在映射中定义，记录错误
                        LOG.debug("列索引:" + columnIndex + " 未在表头行中定义！"); // 修改：翻译了调试信息
                        // 这里使用debug级别而非error，因为这可能是预期行为
                    }
                } else {
                    // 记录空值的信息
                    LOG.debug("行索引:" + finalRowIndex + ", 列索引:" + columnIndex + ", 对应的值为空！"); // 修改：翻译了调试信息
                    // 这里也改为debug级别，因为空值是常见情况
                }
            });

            // 只有当行数据映射中有数据时，才将其添加到结果列表中
//            if (hasValidData[0]) {
            if(!rowDataMap.isEmpty()){
                dataRowList.add(rowDataMap);
            }

//            }
        }

        // 返回包含所有行数据的列表
        return dataRowList;
    }

    /**
     * 遍历Excel行中的单元格并进行处理
     * CshExcelUtils.processRowCells
     *
     * 该方法会遍历指定行中从0到最大列数-1的所有单元格，
     * 获取每个单元格的文本值，然后将列索引和单元格值传递给处理器进行处理。
     *
     * @param row Excel行对象
     * @param maxColumns 要处理的最大列数
     * @param processor 处理每个单元格的函数，接收列索引和单元格文本值作为参数
     */
    public static void processRowCells(Row row, int maxColumns, BiConsumer<Integer, String> processor) {
        // 检查行是否为空
        if (row == null) {
            return; // 如果行为空，则不进行处理
        }

        // 遍历指定数量的列
        for (int i = 0; i < maxColumns; i++) {
            // 获取当前列的单元格
            Cell cell = row.getCell(i);

            // 获取单元格的文本值
            String cellValue = get4CellTextValue(cell);

            // 调用处理器处理当前单元格的列索引和值
            processor.accept(i, cellValue);
        }
    }

    /**
     * 获取单元格的文本值
     * 根据不同的单元格类型返回相应的文本表示
     *
     * @param cell Excel单元格对象
     * @return 单元格的文本内容，如果单元格为null则返回null
     */
    public static String get4CellTextValue(Cell cell) {
        // 如果单元格为空，直接返回null
        if (cell == null) {
            return null;
        }

        // 根据单元格类型获取对应的文本值
        switch (cell.getCellType()) {
            case STRING:
                // 字符串类型：返回去除首尾空格后的字符串值
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // 判断是否为日期格式
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 日期类型：返回日期的字符串表示
                    return cell.getDateCellValue().toString();
                }
                // 数字类型：使用自定义格式化防止科学计数法
                DecimalFormat decimalFormat = new DecimalFormat("0.######");
                return decimalFormat.format(cell.getNumericCellValue());
            case BOOLEAN:
                // 布尔类型：转换为字符串"true"或"false"
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // 公式类型：返回公式内容而非计算结果
                return cell.getCellFormula();
            case BLANK:
                // 空白单元格：返回空字符串
                return "";
            case ERROR:
                // 错误单元格：返回空字符串
                return "";
            default:
                // 其他未知类型：返回空字符串
                return "";
        }
    }
    /**
     * 从Excel行中提取双向映射关系
     *
     * 该方法遍历Excel首行，建立列索引与列名之间的双向映射关系。
     * 只有非空的列名会被添加到映射中。
     *
     * @param row Excel的行对象，包含列标题
     * @param maxColumnLimit 最大处理的列数
     * @return 包含索引到列名和列名到索引两个映射的BiMap对象
     * @throws IllegalArgumentException 如果首行参数为null
     */
    public static BiMap extractColumn2Mappings(Row row, int maxColumnLimit) {
        // 验证输入参数
        if (row == null) {
            throw new IllegalArgumentException("Header row cannot be null");
        }

        // 创建两个映射：一个保持列索引到列名的映射（保持顺序），一个保持列名到列索引的映射
        Map<Integer, String> index2NameMap = new LinkedHashMap<>();
        Map<String, Integer> name2IndexMap = new HashMap<>();

        // 获取实际的列数，并限制最大列数
        // getLastCellNum()返回最后一个单元格的索引+1
        int effectiveColumnCount = Math.min(maxColumnLimit, row.getLastCellNum());

        // 遍历首行单元格并建立映射关系
        processRowCells(row, effectiveColumnCount, (columnIndex, columnName) -> {
            // 只处理非空列名
            if (columnName != null && !columnName.isEmpty()) {
                // 将列索引和列名添加到映射中
                index2NameMap.put(columnIndex, columnName);
                name2IndexMap.put(columnName, columnIndex);
            }
        });

        // 返回包含双向映射的BiMap对象
        return new BiMap(index2NameMap, name2IndexMap);
    }

    /**
     * 获取指定工作表的第一行
     *
     * @param sheet 需要获取行的工作表对象
     * @return 返回工作表的第一行（索引为0的行）
     */
    public static Row get3FstRow(Sheet sheet) {
        int rowIndex = 0;  // 初始化行索引为0，表示第一行
        return get3Row(sheet, rowIndex);  // 调用get3Row方法获取指定索引的行
    }


    /**
     * 获取工作表指定索引的行数据
     * @param sheet Excel工作表对象
     * @param rowIndex 要获取的行索引（从0开始）
     * @return 工作表中指定索引的行
     * @throws ExcelProcessingException 当工作表为空或指定行不存在时抛出异常
     */
    public static Row get3Row(Sheet sheet, int rowIndex) {
        // 检查工作表是否为空
        if (null == sheet) {
            LOG.error("工作表对象为空，无法获取数据");
            throw new ExcelProcessingException("INVALID_SHEET", "工作表对象为空，无法获取数据");
        }

        // 检查行索引是否有效
        if (rowIndex < 0) {
            LOG.error(String.format("行索引{}无效，行索引不能为负数", rowIndex));
            throw new ExcelProcessingException("INVALID_ROW_INDEX", "行索引无效，行索引不能为负数");
        }

        // 检查行索引是否超出范围
        int lastRowNum = sheet.getLastRowNum();
        if (rowIndex > lastRowNum) {
            LOG.error(String.format("行索引{}超出工作表最大行数{}，无法获取数据", rowIndex, lastRowNum));
            throw new ExcelProcessingException("ROW_INDEX_OUT_OF_RANGE",
                    String.format("行索引%d超出工作表最大行数%d，无法获取数据", rowIndex, lastRowNum));
        }

        // 获取指定索引的行
        Row row = sheet.getRow(rowIndex);
        if (null == row) {
            LOG.error(String.format("工作表中索引为{}的行不存在或已被删除", rowIndex));
            throw new ExcelProcessingException("ROW_MISSING",
                    String.format("工作表中索引为%d的行不存在或已被删除", rowIndex));
        }

        // 检查行是否包含有效数据
        int cellCount = row.getPhysicalNumberOfCells();
        if (0 == cellCount) {
            LOG.warn(String.format("工作表中索引为{}的行不包含任何数据", rowIndex));
            // 根据业务需求决定是否抛出异常
        }

        return row;
    }

    /**
     * 获取工作表的第一行数据（索引为0的行）
     * 这是对getRow方法的封装，简化了获取第一行的操作
     *
     * @param sheet Excel工作表对象
     * @return 工作表的第一行
     * @throws ExcelProcessingException 当工作表为空或第一行不存在时抛出异常
     */
    public static Row get3FirstRow(Sheet sheet) {
        // 调用getRow方法获取索引为0的行（第一行）
        return get3Row(sheet, 0);
    }


    /**
     * 根据工作表名称获取Sheet对象
     *
     * @param workbook Excel工作簿对象
     * @param sheetName 要获取的工作表名称
     * @return 指定名称的Sheet对象
     * @throws ExcelProcessingException 当未找到指定名称的工作表时抛出异常
     */
    public static Sheet get2SheetByName(Workbook workbook, String sheetName) {
        // 参数校验
        if (workbook == null) {
            throw new ExcelProcessingException("INVALID_WORKBOOK", "工作簿对象不能为空");
        }

        if (sheetName == null || sheetName.trim().isEmpty()) {
            throw new ExcelProcessingException("INVALID_SHEET_NAME", "工作表名称不能为空");
        }

        // 获取指定名称的工作表
        Sheet sheet = workbook.getSheet(sheetName);

        // 如果未找到指定工作表，抛出异常
        if (null == sheet || sheet.getPhysicalNumberOfRows() == 0) {
            LOG.error("Excel模板格式错误：未找到名称为 '{" + sheetName + "}' 的工作表,或不包含数据！");
            throw new ExcelProcessingException(
                    "SHEET_NOT_FOUND",
                    String.format("Excel模板格式错误：未找到名称为 '%s' 的工作表", sheetName));
        }

        return sheet;
    }


    /**
     * 获取工作簿中的第一个工作表（索引为0的工作表）
     * 该方法是对get2Sheet方法的封装，简化了获取第一个工作表的操作
     *
     * @param w Workbook对象，表示Excel工作簿
     * @return 返回工作簿中索引为0的工作表
     * @throws ExcelProcessingException 当工作簿为空、不包含工作表或第一个工作表不包含数据时抛出异常
     */
    public static Sheet get2FstSheet(Workbook w) {
        // 设置工作表索引为0，表示获取第一个工作表
        int sheetIndex = 0;

        // 调用get2Sheet方法获取指定索引的工作表
        // 注意：这依赖于get2Sheet方法的实现，它会检查工作表是否有效并包含数据
        return get2Sheet(w, sheetIndex);
    }

    /**
     * 获取指定索引的工作表
     * @param w Workbook对象，表示Excel工作簿
     * @param sheetIndex 需要获取的工作表索引
     * @return 返回找到的Sheet对象
     * @throws ExcelProcessingException 当找不到有效工作表时抛出异常
     */
    public static Sheet get2Sheet(Workbook w, int sheetIndex) {
        // 检查参数有效性：工作簿对象不为空且工作表索引在有效范围内
        boolean needContinue = (null != w && sheetIndex >= 0 && sheetIndex < w.getNumberOfSheets());

        // 如果参数无效，记录错误并抛出异常
        if (!needContinue) {
            LOG.error("无效的参数: 工作簿为空或工作表索引 " + sheetIndex + " 超出范围");
            throw new ExcelProcessingException("INVALID_PARAMS", "获取工作表失败：工作簿为空或索引无效");
        }

        try {
            // 根据索引获取工作表
            Sheet s = w.getSheetAt(sheetIndex);

            // 检查工作表是否有数据
            // 问题：s.getLastRowNum() == 0 不一定表示工作表没有数据，可能只有一行数据
            // 修复：检查物理行数而不是最后行的索引
            if (null == s || s.getPhysicalNumberOfRows() == 0) {
                LOG.error("Excel模板格式错误：工作表索引 " + sheetIndex + " 不包含数据");
                throw new ExcelProcessingException("INVALID_TEMPLATE", "Excel模板格式错误：未找到包含数据的工作表");
            }

            return s; // 返回找到的Sheet对象
        } catch (Exception e) {
            LOG.error("获取工作表时发生异常: " + e.getMessage(), e);
            throw new ExcelProcessingException("SHEET_ACCESS_ERROR", "获取工作表时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取Excel工作簿
     * CshExcelUtils.CshExcelUtils
     * @param excelPath Excel文件路径
     * @return Workbook 工作簿对象
     */
    public static Workbook get1Wb(String excelPath) {
        // 检查Excel文件路径是否为空
        if(null == excelPath){
            // 记录错误日志
            LOG.error("无效EXCEL:" + excelPath);
            // 抛出自定义异常
            throw new ExcelProcessingException("XXX","无效EXCEL:" + excelPath);
        }

        // 使用CshFileUtils工具类检查文件大小
        // 如果文件大小超过30MB（30 * 1024 * 1024字节），则抛出异常
        if(CshFileUtils.validateFileAndGetSize(excelPath) > 30 * 1024 * 1024){
            LOG.error("文件超过大小限制30M:" + excelPath);
            throw new ExcelProcessingException("XXX","文件超过大小限制30M");
        }

        // 声明工作簿对象
        Workbook wb = null;

        // 使用try-with-resources语法确保资源自动关闭
        // 创建带缓冲的输入流来读取Excel文件
        try (BufferedInputStream inputStream = new BufferedInputStream(
                Files.newInputStream(new File(excelPath).toPath()))) {

            // 根据文件扩展名判断Excel文件类型
            if (excelPath.endsWith(DataBusConstants.FILE_EXCEL_XLS)) {
                // 如果是.xls文件（Excel 97-2003格式）
                // 创建HSSFWorkbook对象
                wb = new HSSFWorkbook(inputStream);
            } else if (excelPath.endsWith(DataBusConstants.FILE_EXCEL_XLSX)) {
                // 如果是.xlsx文件（Excel 2007+格式）
                // 创建XSSFWorkbook对象
                wb = new XSSFWorkbook(inputStream);
            } else {
                // 如果既不是.xls也不是.xlsx，则报错
                LOG.error("无效EXCEL:" + excelPath);
                throw new ExcelProcessingException("XXX","无效EXCEL:" + excelPath);
            }
        } catch (Exception e) {
            // 捕获所有可能的异常
            LOG.error("EXCEL文件解析不成功", e);
            throw new ExcelProcessingException("XXX", "EXCEL文件解析不成功");
        }

        // 返回工作簿对象
        return wb;
    }

    public static void main(String[] args) {
        int type;
        type = 1;
        if(1 == type){
            // 使用 try-with-resources 确保资源正确关闭
            try(Workbook wb = CshExcelUtils.get1Wb("/Volumes/RamDisk/example.xlsx");){
                System.out.printf("a");
                // 在这里处理Excel文件
                System.out.println("成功打开Excel文件");
                System.out.println("工作表数量：" + wb.getNumberOfSheets());

            }catch (ExcelProcessingException | IOException e){
                LOG.error("Excel处理错误: " + e.getMessage());
                // 根据实际需求处理异常
            }

        }
    }

}
