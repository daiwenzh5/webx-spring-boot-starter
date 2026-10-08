package io.github.daiwenzh5.webx.api

/**
 * starter 内置错误码�? *
 * 业务项目应在自己模块定义 enum 实现 ErrorCode，不要直接使�?INTERNAL_ERROR 之外的码值�? */
enum class BuiltInErrorCodes(override val code: Int, override val message: String) : ErrorCode {
    INTERNAL_ERROR(5000, "Internal server error"),
    INVALID_PARAMETER(4000, "Invalid parameter"),
    UNAUTHORIZED(4001, "Unauthorized"),
    FORBIDDEN(4003, "Forbidden"),
    NOT_FOUND(4004, "Resource not found"),
    TOO_MANY_REQUESTS(4029, "Too many requests"),
    METHOD_NOT_ALLOWED(4005, "Method not allowed"),
}
