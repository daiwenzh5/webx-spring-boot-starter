package io.github.daiwenzh5.webx.ratelimit

/**
 * 限流抽象�? *
 *  - core 仅定义接口与内存默认实现�? *  - 业务项目可提供自己的 RateLimiter Bean（如 Redis、Guava RateLimiter、Resilience4j 等）�? *    starter 自动通过 @ConditionalOnMissingBean 选用业务方的实现�? */
interface RateLimiter {
    /**
     * 尝试获取一个令牌�?     * @param key 限流维度键，通常是方法签名或 IP+path�?     * @param permitsPerSecond �?key 的速率�?     * @return true=放行，false=被限流�?     */
    fun tryAcquire(key: String, permitsPerSecond: Double): Boolean
}
