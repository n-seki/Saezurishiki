package com.seki.saezurishiki.repository

import com.seki.saezurishiki.entity.DirectMessageEntity
import com.seki.saezurishiki.entity.mapper.EntityMapper
import twitter4j.DirectMessage
import twitter4j.Twitter
import twitter4j.TwitterException
import java.util.concurrent.ConcurrentHashMap

class DirectMessageRepository(private val twitter: Twitter, private val mapper: EntityMapper) {

    private val messageCache: MutableMap<Long, DirectMessageEntity>

    init {
        messageCache = ConcurrentHashMap()
    }

    fun addSentDM(message: DirectMessage): DirectMessageEntity {
        val dm = mapper.map(message)
        messageCache.put(dm.id, dm)
        return dm
    }


    fun getSentDMId(recipientUserId: Long): List<Long> {
         return messageCache.values
                .filter { it.recipientId == recipientUserId }
                .map { it.id }
    }

    fun addDM(list: List<DirectMessage>): List<DirectMessageEntity> {
        val result = list.map { this.mapper.map((it)) }
        messageCache.putAll(result.associateBy({it.id}))
        return result
    }

    fun addDM(message: DirectMessage): DirectMessageEntity {
        val dm = this.mapper.map(message)
        messageCache.put(dm.id, dm)
        return dm
    }


    fun getDMIdByUser(senderId: Long): List<Long> {
        return messageCache.values
                .filter { it.id == senderId }
                .map { it.id }
    }

    fun getDM(messageId: Long): DirectMessageEntity {
        return messageCache.getValue(messageId)
    }

    @Throws(TwitterException::class)
    fun findDM(messageId: Long): DirectMessageEntity {
        return messageCache.getOrElse(messageId) {
            val result = twitter.showDirectMessage(messageId)
            this.mapper.map(result)
        }
    }

    public fun clear() {
        messageCache.clear()
    }
}
