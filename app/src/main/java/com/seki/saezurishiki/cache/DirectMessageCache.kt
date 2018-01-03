package com.seki.saezurishiki.cache

import com.seki.saezurishiki.entity.DirectMessageEntity
import java.util.concurrent.ConcurrentHashMap

class DirectMessageCache {

    private val cache = ConcurrentHashMap<Long, DirectMessageEntity>()

    operator fun get(id: Long) = cache.getValue(id)

    fun getMassageSentBy(userId: Long): List<DirectMessageEntity> {
        return cache
                .filter { entry -> entry.value.sender.id == userId }
                .values
                .toList()
    }

    fun getMessageReceivedBy(userId: Long): List<DirectMessageEntity> {
        return cache
                .filter { entry -> entry.value.recipientId == userId }
                .values
                .toList()
    }

    fun put(message: DirectMessageEntity) {
        cache.put(message.id, message)
    }

    fun put(messages: List<DirectMessageEntity>) {
        messages.forEach { cache.put(it.id, it) }
    }

    fun has(id: Long) = cache.contains(id)

    fun clearAll() = cache.clear()
}