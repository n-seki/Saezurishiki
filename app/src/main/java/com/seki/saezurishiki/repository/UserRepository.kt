package com.seki.saezurishiki.repository

import com.seki.saezurishiki.cache.UserCache
import com.seki.saezurishiki.entity.UserEntity
import com.seki.saezurishiki.entity.mapper.EntityMapper
import com.seki.saezurishiki.model.adapter.SupportCursorList
import com.seki.saezurishiki.network.twitter.TwitterProvider
import twitter4j.Relationship
import twitter4j.TwitterException
import twitter4j.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
        private val twitterProvider: TwitterProvider,
        private val mapper: EntityMapper,
        private val cache: UserCache
) {

    @Throws(TwitterException::class)
    fun getFriendList(userId: Long, nextCursor: Long): SupportCursorList<UserEntity> {
        val result = twitterProvider.getInstance().getFriendsList(userId, nextCursor)
        val users = result.map { mapper.map(it) }
                          .also { list -> cache.put(list) }
        return SupportCursorList(users, userId, result.nextCursor)
    }

    @Throws(TwitterException::class)
    fun getFollowerList(userId: Long, nextCursor: Long): SupportCursorList<UserEntity> {
        val result = twitterProvider.getInstance().getFollowersList(userId, nextCursor)
        val users = result.map { mapper.map(it) }
                          .also { list -> cache.put(list) }
        return SupportCursorList(users, userId, result.nextCursor)
    }

    @Throws(TwitterException::class)
    fun find(userId: Long): UserEntity {
        if (cache.has(userId)) {
            return cache[userId]
        }

        return twitterProvider.getInstance().showUser(userId)
                .let { mapper.map(it) }
                .also { cache.put(it) }
    }

    @Throws(TwitterException::class)
    fun showFriendship(sourceId: Long, targetId: Long): Relationship {
        return twitterProvider.getInstance().showFriendship(sourceId, targetId)
    }

    @Throws(TwitterException::class)
    fun createFriendship(sourceId: Long): UserEntity {
        val user = twitterProvider.getInstance().createFriendship(sourceId)
        return mapper.map(user).also { cache.put(it) }
    }

    @Throws(TwitterException::class)
    fun destroyFriendship(targetId: Long): UserEntity {
        val user = twitterProvider.getInstance().destroyFriendship(targetId)
        return mapper.map(user).also { cache.put(it) }
    }

    @Throws(TwitterException::class)
    fun createBlock(targetId: Long): UserEntity {
        val user = twitterProvider.getInstance().createBlock(targetId)
        return mapper.map(user).also { cache.put(it) }
    }

    @Throws(TwitterException::class)
    fun destroyBlock(targetId: Long): UserEntity {
        val user = twitterProvider.getInstance().destroyBlock(targetId)
        return mapper.map(user).also { cache.put(it) }
    }

    fun getUser(userId: Long) = cache[userId]

    fun clear() {
        cache.clearAll()
    }

    fun add(user: User): UserEntity {
        return mapper.map(user).also { cache.put(it) }
    }
}