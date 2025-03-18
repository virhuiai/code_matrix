package com.virhuiai.OfficeUtils.Excel;

import com.virhuiai.CshLogUtils.CshLogUtils;
import com.virhuiai.File.CshFileUtils;
import org.apache.commons.logging.Log;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
     * 获取Excel工作簿
     * CshExcelUtils.getExcelWb
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
