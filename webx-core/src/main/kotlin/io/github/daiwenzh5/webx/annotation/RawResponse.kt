package io.github.daiwenzh5.webx.annotation

/**
 * 标记 Controller 或方法返回原始响应，跳过 R<T> 包装�? *
 * 可同时标注类与方法；标注类时类下所有方法默认跳过，方法上的标注优先�? *
 * 路径级白名单通过 webx.response.skip-paths �?yml 中配置�? */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RawResponse
