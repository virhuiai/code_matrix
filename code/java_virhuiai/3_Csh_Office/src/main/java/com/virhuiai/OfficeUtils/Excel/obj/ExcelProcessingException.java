package com.virhuiai.OfficeUtils.Excel.obj;

/**
 * Excel处理异常类
 * 用于表示在Excel处理过程中出现的各种错误
 *
 1. **类的目的**：
 - 这是一个专门用于处理Excel相关操作时的异常类
 - 继承自RuntimeException，属于非受检异常，不需要强制捕获

 2. **成员变量**：
 - errorCode：用于存储错误代码，便于识别具体的错误类型

 3. **构造函数**：
 - 提供了两个构造函数，支持不同的异常创建场景
 - 第一个构造函数：适用于只需要错误代码和错误消息的场景
 - 第二个构造函数：适用于需要包含导致异常的原因的场景

 4. **公共方法**：
 - getErrorCode()：提供了获取错误代码的方法
 - 继承自RuntimeException的其他方法（如getMessage()）也可使用

 5. **使用场景**：
 - 在处理Excel文件时遇到的错误（如文件读写错误、格式错误等）
 - 需要区分不同类型的Excel处理错误时
 - 需要携带具体错误信息和错误代码时

 这个异常类的设计遵循了Java异常处理的最佳实践，提供了足够的信息来帮助定位和处理Excel处理过程中的错误。
 */
// 声明一个自定义异常类，继承自RuntimeException（运行时异常）
public class ExcelProcessingException extends RuntimeException {
    // 声明私有成员变量，用于存储错误代码
    private String errorCode;

    /**
     * 构造函数
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    // 第一个构造函数：接收错误代码和错误消息
    public ExcelProcessingException(String errorCode, String message) {
        // 调用父类RuntimeException的构造函数，传入错误消息
        super(message);
        // 设置错误代码
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param cause 导致异常的原因
     */
    // 第二个构造函数：接收错误代码、错误消息和导致异常的原因
    public ExcelProcessingException(String errorCode, String message, Throwable cause) {
        // 调用父类RuntimeException的构造函数，传入错误消息和导致异常的原因
        super(message, cause);
        // 设置错误代码
        this.errorCode = errorCode;
    }

    /**
     * 获取错误代码
     * @return 错误代码
     */
    // 获取错误代码的公共方法
    public String getErrorCode() {
        return errorCode;
    }
}