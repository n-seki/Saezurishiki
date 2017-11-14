package com.seki.saezurishiki.repository

import com.seki.saezurishiki.entity.UserEntity
import com.seki.saezurishiki.entity.mapper.EntityMapper
import com.seki.saezurishiki.model.adapter.SupportCursorList
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.User
import java.util.concurrent.ConcurrentHashMap

class UserRepository(private val twitter: Twitter, private val mapper: EntityMapper) {

    private val users: MutableMap<Long, UserEntity>

    init {
        users = ConcurrentHashMap()
    }

    @Throws(TwitterException::class)
    fun getFriendList(userId: Long, nextCursor: Long): SupportCursorList<UserEntity> {
        val result = twitter.getFriendsList(userId, nextCursor)
        val users = addUsers(result)
        return SupportCursorList(users, userId, result.nextCursor)
    }

    @Throws(TwitterException::class)
    fun getFollowerList(userId: Long, nextCursor: Long): SupportCursorList<UserEntity> {
        val result = twitter.getFollowersList(userId, nextCursor)
        val users = addUsers(result)
        return SupportCursorList(users, userId, result.nextCursor)
    }

    fun find(userId: Long): UserEntity {
        return users.getOrPut(userId) {
            val result = twitter.showUser(userId)
            mapper.map(result)
        }
    }

    fun add(user: User): UserEntity {
        val userEntity = mapper.map(user)
        users.put(user.id, userEntity)
        return userEntity
    }

    private fun addUsers(users: List<User>): List<UserEntity> {
        return users.map { add(it) }
    }

    public fun clear() {
        users.clear()
    }


}