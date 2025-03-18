package com.virhuiai.OfficeUtils.Excel;


/**
 * Excel处理异常类
 * 用于表示在Excel处理过程中出现的各种错误
 */
public class ExcelProcessingException extends RuntimeException {
    private String errorCode;

    /**
     * 构造函数
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    public ExcelProcessingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param cause 导致异常的原因
     */
    public ExcelProcessingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误代码
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }
}