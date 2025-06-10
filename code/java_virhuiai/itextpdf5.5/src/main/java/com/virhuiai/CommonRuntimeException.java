package com.virhuiai;


/**
 * 通用运行时异常类
 * 注：实际项目中应该使用项目中已定义的异常类
 */
class CommonRuntimeException extends RuntimeException {
    private final String errorCode;

    public CommonRuntimeException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public CommonRuntimeException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}