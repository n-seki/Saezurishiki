package com.seki.saezurishiki.repository

import com.seki.saezurishiki.entity.UserEntity
import com.seki.saezurishiki.entity.mapper.EntityMapper
import com.seki.saezurishiki.model.adapter.SupportCursorList
import twitter4j.Relationship
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.User
import java.util.concurrent.ConcurrentHashMap

object UserRepository {
    private lateinit var mTwitter: Twitter
    private lateinit var mMapper: EntityMapper

    fun setup(twitter: Twitter, mapper: EntityMapper) {
        mTwitter = twitter
        mMapper = mapper
    }

    private val userCache: MutableMap<Long, UserEntity>

    init {
        userCache = ConcurrentHashMap()
    }

    @Throws(TwitterException::class)
    fun getFriendList(userId: Long, nextCursor: Long): SupportCursorList<UserEntity> {
        val result = mTwitter.getFriendsList(userId, nextCursor)
        val users = addUsers(result)
        return SupportCursorList(users, userId, result.nextCursor)
    }

    @Throws(TwitterException::class)
    fun getFollowerList(userId: Long, nextCursor: Long): SupportCursorList<UserEntity> {
        val result = mTwitter.getFollowersList(userId, nextCursor)
        val users = addUsers(result)
        return SupportCursorList(users, userId, result.nextCursor)
    }

    @Throws(TwitterException::class)
    fun find(userId: Long): UserEntity {
        return userCache.getOrPut(userId) {
            val result = mTwitter.showUser(userId)
            mMapper.map(result)
        }
    }

    @Throws(TwitterException::class)
    fun showFriendship(sourceId: Long, targetId: Long): Relationship {
        return mTwitter.showFriendship(sourceId, targetId)
    }

    @Throws(TwitterException::class)
    fun createFriendship(sourceId: Long): UserEntity {
        val user = mTwitter.createFriendship(sourceId)
        return add(user)
    }

    @Throws(TwitterException::class)
    fun destroyFriendship(targetId: Long): UserEntity {
        val user = mTwitter.destroyFriendship(targetId)
        return add(user)
    }

    @Throws(TwitterException::class)
    fun createBlock(targetId: Long): UserEntity {
        val user = mTwitter.createBlock(targetId)
        return add(user)
    }

    @Throws(TwitterException::class)
    fun destroyBlock(targetId: Long): UserEntity {
        val user = mTwitter.destroyBlock(targetId)
        return add(user)
    }

    fun getUser(userId: Long) = userCache.getValue(userId)

    fun add(user: User): UserEntity {
        val userEntity = mMapper.map(user)
        userCache.put(user.id, userEntity)
        return userEntity
    }

    private fun addUsers(users: List<User>): List<UserEntity> {
        return users.map { add(it) }
    }

    public fun clear() {
        userCache.clear()
    }


}