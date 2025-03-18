package com.virhuiai.OfficeUtils.Excel;

import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.file.Files;

public class CshExcelUtils {
    // 日志对象
    private static Log LOG = CshLogUtils.createLogExtended(CshExcelUtils.class);


    /**
     * 获取Excel工作簿
     * @param excelPath Excel文件路径
     * @return Workbook 工作簿对象
     *
     *
     */
    public static Workbook getExcelWb(String excelPath) {
        // 检查Excel文件路径是否有效
        if(null == excelPath){
            LOG.error("无效EXCEL:" + excelPath);
            throw new ExcelProcessingException("XXX","无效EXCEL:" + excelPath);
        }

        Workbook wb = null;
        // 使用try-with-resources自动关闭输入流
        try (BufferedInputStream inputStream = new BufferedInputStream(
                Files.newInputStream(new File(excelPath).toPath()))) {
            // 检查文件大小是否超过30M限制
            if (inputStream.available() > 30 * 1024 * 1024) {
                LOG.error("文件超过大小限制30M:" + excelPath);
                throw new Exception("文件超过大小限制30M");
            }

            // 根据文件扩展名判断Excel版本并处理
            if (excelPath.endsWith(DataBusConstants.FILE_EXCEL_XLS)) {
                // 处理.xls文件
                wb = new HSSFWorkbook(inputStream);
            } else if (excelPath.endsWith(DataBusConstants.FILE_EXCEL_XLSX)) {
                // 处理.xlsx文件
                wb = new XSSFWorkbook(inputStream);
            }else{
                LOG.error("无效EXCEL:" + excelPath);
                throw new ExcelProcessingException("XXX","无效EXCEL:" + excelPath);
            }
        } catch (Exception e) {
            LOG.error("EXCEL文件解析不成功", e);
            throw new ExcelProcessingException("XXX", "EXCEL文件解析不成功");
        }

        return wb;
    }

}
