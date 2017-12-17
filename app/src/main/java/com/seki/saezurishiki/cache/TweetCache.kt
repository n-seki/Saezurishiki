package com.seki.saezurishiki.cache

import com.seki.saezurishiki.entity.TweetEntity
import java.util.concurrent.ConcurrentHashMap

class TweetCache {
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
        cache.put(tweet.id, tweet)
    }
    fun put(tweets: List<TweetEntity>) {
        tweets.forEach {cache.put(it.id, it)}
    }
    fun has(id: Long) = cache.contains(id)
    fun deleteCacheIn(range: LongRange) {
        range.forEach {cache.remove(it)}
    }
    fun clearAll() = cache.clear()
}
