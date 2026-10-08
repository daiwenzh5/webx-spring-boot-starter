package io.github.daiwenzh5.webx.ratelimit

import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

/**
 * 基于 ConcurrentHashMap + 令牌桶的内存实现�? *
 * 单实例内精度高；分布式场景请业务方提�?Redis RateLimiter Bean�? *
 * 注意：每�?tryAcquire 会进行一�?CAS-like 更新；高并发下需要考虑 GC�? * 此处保留 simple 实现以满�?starter 默认依赖最小化的要求�? */
class InMemoryRateLimiter : RateLimiter {

    private data class Bucket(
        var tokens: Double,
        var lastRefillNanos: Long,
        val permitsPerSecond: Double,
    )

    private val buckets = ConcurrentHashMap<String, Bucket>()

    override fun tryAcquire(key: String, permitsPerSecond: Double): Boolean {
        if (permitsPerSecond <= 0.0) return true
        val now = System.nanoTime()
        val bucket = buckets.computeIfAbsent(key) {
            Bucket(tokens = permitsPerSecond, lastRefillNanos = now, permitsPerSecond = permitsPerSecond)
        }
        synchronized(bucket) {
            val elapsed = (now - bucket.lastRefillNanos).coerceAtLeast(0L)
            val refill = elapsed / 1_000_000_000.0 * bucket.permitsPerSecond
            bucket.tokens = max(0.0, bucket.tokens + refill)
            bucket.lastRefillNanos = now
            if (bucket.tokens >= 1.0) {
                bucket.tokens -= 1.0
                return true
            }
            return false
        }
    }
}
