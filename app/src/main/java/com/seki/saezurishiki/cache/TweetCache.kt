package com.seki.saezurishiki.cache

import com.seki.saezurishiki.entity.TweetEntity
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TweetCache @Inject constructor() {
    private val cache = ConcurrentHashMap<Long, TweetEntity>()
    operator fun get(id: Long) = cache.getValue(id)

    fun get(range: LongRange): List<TweetEntity> {
        return cache.filter { entry -> entry.key in range }
                    .values
                    .toList()
    }
    fun get(userId: Long, range: LongRange): List<TweetEntity> {
        return cache.filter { entry -> entry.value.user.id == userId }
                    .filter { entry -> entry.key in range }
                    .values
                    .toList()
    }
    fun put(tweet: TweetEntity) {
        cache[tweet.id] = tweet
    }

    fun has(id: Long) = cache.contains(id)

    fun clearAll() = cache.clear()
}
