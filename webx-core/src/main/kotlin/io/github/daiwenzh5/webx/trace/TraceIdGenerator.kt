package io.github.daiwenzh5.webx.trace

import java.util.UUID

/**
 * TraceId 生成器�? *
 * 默认 UUID 32 位无连字符版本。业务方可注入自己的实现�? */
object TraceIdGenerator {
    fun next(): String = UUID.randomUUID().toString().replace("-", "")
}
