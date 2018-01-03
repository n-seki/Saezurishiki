package com.seki.saezurishiki.repository

import com.seki.saezurishiki.cache.DirectMessageCache
import com.seki.saezurishiki.entity.DirectMessageEntity
import com.seki.saezurishiki.entity.mapper.EntityMapper
import twitter4j.DirectMessage
import twitter4j.Paging
import twitter4j.Twitter
import twitter4j.TwitterException

object DirectMessageRepository {

    private lateinit var twitter: Twitter
    private lateinit var mapper: EntityMapper
    private lateinit var messageCache: DirectMessageCache


    fun setup(twitter: Twitter, mapper: EntityMapper, cache: DirectMessageCache) {
        this.twitter = twitter
        this.mapper = mapper
        this.messageCache = cache
    }

    @Throws(TwitterException::class)
    fun getSendMessages(paging: Paging): List<DirectMessageEntity> {
        val result = this.twitter.getSentDirectMessages(paging)
        return result.map { this.mapper.map(it) }.also { this.messageCache.put(it) }
    }

    @Throws(TwitterException::class)
    fun getReceivedMessages(paging: Paging): List<DirectMessageEntity> {
        val result = this.twitter.getDirectMessages(paging)
        return result.map { this.mapper.map(it) }.also { this.messageCache.put(it) }
    }

    @Throws(TwitterException::class)
    fun findMessage(messageId: Long): DirectMessageEntity {
        if (this.messageCache.has(messageId)) {
            return messageCache[messageId]
        }
        val result = this.twitter.showDirectMessage(messageId)
        return this.mapper.map(result).also { this.messageCache.put(it) }
    }

    fun getSendMessages(senderId: Long): List<DirectMessageEntity> {
        return this.messageCache.getMassageSentBy(senderId)
    }

    fun getReceivedMessages(receiptUserId: Long): List<DirectMessageEntity> {
        return this.messageCache.getMessageReceivedBy(receiptUserId)
    }

    @Throws(TwitterException::class)
    fun sendMessage(userId: Long, message: String): DirectMessageEntity {
        val result = this.twitter.sendDirectMessage(userId, message)
        return this.mapper.map(result).also { this.messageCache.put(it) }
    }

    fun add(message: DirectMessage): DirectMessageEntity {
        return this.mapper.map(message).also { this.messageCache.put(it) }
    }

    operator fun get(messageId: Long): DirectMessageEntity {
        return messageCache[messageId]
    }

    fun clear() {
        messageCache.clearAll()
    }
}
