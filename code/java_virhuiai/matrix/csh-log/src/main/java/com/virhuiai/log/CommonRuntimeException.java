package com.virhuiai.log;

/**
 * 通用运行时异常类，用于封装带有错误码的运行时异常。
 * <p>
 * 该类继承自 {@link RuntimeException}，用于在应用程序中抛出带有错误码和错误信息的异常。
 * 它适用于需要统一异常处理机制的场景，例如记录错误、返回给客户端或触发特定错误处理逻辑。
 * <b>注意</b>：在实际项目中，应优先使用项目中已定义的异常类，避免重复定义类似的异常类。
 * </p>
 *
 * @author [Your Name]
 * @since 1.0
 */
class CommonRuntimeException extends RuntimeException {

    /**
     * 错误码，用于标识具体的错误类型。
     * <p>
     * 错误码通常用于程序化处理异常，例如通过错误码映射到特定的错误处理逻辑或国际化消息。
     * 该字段不可变，通过构造函数设置后不可更改。
     * </p>
     */
    private final String errorCode;

    /**
     * 构造一个带有错误码和错误信息的运行时异常。
     * <p>
     * 该构造函数适用于无需指定异常原因（cause）的场景。
     * </p>
     *
     * @param errorCode 错误码，用于标识错误类型
     * @param message   错误信息，描述异常的详细信息
     */
    public CommonRuntimeException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造一个带有错误码、错误信息和异常原因的运行时异常。
     * <p>
     * 该构造函数适用于需要记录异常原因（例如底层异常）的场景。
     * 通过传入 cause，可以在异常堆栈中保留完整的异常链，便于调试和错误追踪。
     * </p>
     *
     * @param errorCode 错误码，标识错误类型
     * @param message   错误信息，描述异常的详细信息
     * @param cause     异常原因，通常是触发此异常的底层异常
     */
    public CommonRuntimeException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码。
     * <p>
     * 返回异常的错误码，用于程序化处理或错误分类。
     * </p>
     *
     * @return 错误码字符串
     */
    public String getErrorCode() {
        return errorCode;
    }
}
