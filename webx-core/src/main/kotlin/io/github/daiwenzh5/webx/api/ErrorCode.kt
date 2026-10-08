package io.github.daiwenzh5.webx.api

/**
 * 业务错误码契约�? *
 * 实现方通常在业务项目中维护一�?enum 实现此接口；
 * starter 仅消�?code/message�? */
interface ErrorCode {
    val code: Int
    val message: String
}
