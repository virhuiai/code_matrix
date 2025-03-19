package com.virhuiai.OfficeUtils.Excel;

import com.virhuiai.CshLogUtils.CshLogUtils;
import com.virhuiai.File.CshFileUtils;
import com.virhuiai.OfficeUtils.Excel.obj.DataBusConstants;
import com.virhuiai.OfficeUtils.Excel.obj.ExcelProcessingException;
import org.apache.commons.logging.Log;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CshExcelUtils {
    // 日志对象
    private static Log LOG = CshLogUtils.createLogExtended(CshExcelUtils.class);

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
