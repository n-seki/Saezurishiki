package com.seki.saezurishiki.cache

import com.seki.saezurishiki.entity.UserEntity
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserCache @Inject constructor() {
    private val cache = ConcurrentHashMap<Long, UserEntity>()
    operator fun get(id: Long) = cache.getValue(id)
    fun get(range: LongRange): List<UserEntity> {
        return cache.filter { entry -> entry.key in range }
                .values
                .toList()
    }

    fun get(userId: Long, range: LongRange): List<UserEntity> {
        return cache.filter { entry -> entry.value.id == userId }
                .filter { entry -> entry.key in range }
                .values
                .toList()
    }

    fun put(user: UserEntity) {
        cache[user.id] = user
    }

    fun put(users: List<UserEntity>) {
        users.forEach { cache[it.id] = it }
    }

    fun has(id: Long) = cache.contains(id)

    fun clearAll() = cache.clear()
}
