package io.github.daiwenzh5.webx.api

/**
 * 统一响应包装�? *
 * 设计原则�? *  - 不可�?data class，序列化友好�? *  - code == 0 表示业务成功；其他值代表业务错误码�? *  - message 同时承载人类可读描述，调用方可国际化覆盖�? *  - data �?null 时省略字段（Jackson 默认行为）�? */
data class R<T>(
    val code: Int = 0,
    val message: String = "ok",
    val data: T? = null,
    val traceId: String? = null,
) {
    companion object {
        @JvmStatic
        fun <T> ok(data: T? = null): R<T> = R(code = 0, message = "ok", data = data)

        @JvmStatic
        fun <T> fail(code: Int, message: String, data: T? = null): R<T> =
            R(code = code, message = message, data = data)

        @JvmStatic
        fun <T> fail(errorCode: ErrorCode, message: String? = null, data: T? = null): R<T> =
            R(
                code = errorCode.code,
                message = message ?: errorCode.message,
                data = data,
            )
    }
}
