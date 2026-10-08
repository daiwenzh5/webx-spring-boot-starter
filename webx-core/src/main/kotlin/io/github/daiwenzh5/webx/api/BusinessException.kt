package io.github.daiwenzh5.webx.api

/**
 * 业务异常基类�? *
 * 全局异常处理器会捕获此异常并转换�?R.fail(code, message)�? * 调用方应通过 ErrorCode 表达可枚举的错误�? */
open class BusinessException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {

    constructor(code: Int, message: String, cause: Throwable? = null)
        : this(object : ErrorCode {
            override val code = code
            override val message = message
        }, message, cause)
}
