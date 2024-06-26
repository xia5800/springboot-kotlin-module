package com.zeta.model.system.cacheKey

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import org.zetaframework.core.constants.RedisKeyConstants
import org.zetaframework.redis.model.StringCacheKey
import java.time.Duration

/**
 * 用户权限 String类型的缓存key
 *
 * @author gcc
 */
@Component
class SaPermissionStringCacheKey(private val redisTemplate: RedisTemplate<String, Any>): StringCacheKey {

    /**
     * key 前缀
     */
    override fun getPrefix(): String {
        return RedisKeyConstants.USER_PERMISSION_KEY
    }

    /**
     * key 过期时间
     */
    override fun getExpire(): Duration? {
        return Duration.ofDays(1)
    }

    /**
     * 获取redisTemplate
     */
    override fun getRedisTemplate(): RedisTemplate<String, Any> {
        return redisTemplate
    }
}
