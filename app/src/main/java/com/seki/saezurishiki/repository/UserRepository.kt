package com.seki.saezurishiki.repository

import com.seki.saezurishiki.cache.UserCache
import com.seki.saezurishiki.entity.UserEntity
import com.seki.saezurishiki.entity.mapper.EntityMapper
import com.seki.saezurishiki.model.adapter.SupportCursorList
import twitter4j.Relationship
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.User

object UserRepository {
    private lateinit var mTwitter: Twitter
    private lateinit var mMapper: EntityMapper
    private lateinit var mCache: UserCache

    fun setup(twitter: Twitter, mapper: EntityMapper, cache: UserCache) {
        mTwitter = twitter
        mMapper = mapper
        mCache = cache
    }

    @Throws(TwitterException::class)
    fun getFriendList(userId: Long, nextCursor: Long): SupportCursorList<UserEntity> {
        val result = mTwitter.getFriendsList(userId, nextCursor)
        val users = result.map { mMapper.map(it) }
                          .also { list -> mCache.put(list) }
        return SupportCursorList(users, userId, result.nextCursor)
    }

    @Throws(TwitterException::class)
    fun getFollowerList(userId: Long, nextCursor: Long): SupportCursorList<UserEntity> {
        val result = mTwitter.getFollowersList(userId, nextCursor)
        val users = result.map { mMapper.map(it) }
                          .also { list -> mCache.put(list) }
        return SupportCursorList(users, userId, result.nextCursor)
    }

    @Throws(TwitterException::class)
    fun find(userId: Long): UserEntity {
        if (mCache.has(userId)) {
            return mCache[userId]
        }

        return mTwitter.showUser(userId).let { mMapper.map(it) }.also { mCache.put(it) }
    }

    @Throws(TwitterException::class)
    fun showFriendship(sourceId: Long, targetId: Long): Relationship {
        return mTwitter.showFriendship(sourceId, targetId)
    }

    @Throws(TwitterException::class)
    fun createFriendship(sourceId: Long): UserEntity {
        val user = mTwitter.createFriendship(sourceId)
        return mMapper.map(user).also { mCache.put(it) }
    }

    @Throws(TwitterException::class)
    fun destroyFriendship(targetId: Long): UserEntity {
        val user = mTwitter.destroyFriendship(targetId)
        return mMapper.map(user).also { mCache.put(it) }
    }

    @Throws(TwitterException::class)
    fun createBlock(targetId: Long): UserEntity {
        val user = mTwitter.createBlock(targetId)
        return mMapper.map(user).also { mCache.put(it) }
    }

    @Throws(TwitterException::class)
    fun destroyBlock(targetId: Long): UserEntity {
        val user = mTwitter.destroyBlock(targetId)
        return mMapper.map(user).also { mCache.put(it) }
    }

    fun getUser(userId: Long) = mCache[userId]

    fun clear() {
        mCache.clearAll()
    }

    fun add(user: User): UserEntity {
        return mMapper.map(user).also { mCache.put(it) }
    }
}