package com.seki.saezurishiki.repository

import com.seki.saezurishiki.entity.TweetEntity
import com.seki.saezurishiki.entity.mapper.EntityMapper
import twitter4j.*
import java.util.concurrent.ConcurrentHashMap

object TweetRepositoryKt {
    private lateinit var mTwitter: Twitter
    private lateinit var mMapper: EntityMapper

    fun setup(twitter: Twitter, mapper: EntityMapper) {
        mTwitter = twitter
        mMapper = mapper
    }

    private val tweetCache: MutableMap<Long, TweetEntity>
    private val deletedNotice: MutableMap<Long, StatusDeletionNotice>

    init {
        tweetCache = ConcurrentHashMap()
        deletedNotice = ConcurrentHashMap()
    }

    @Throws(TwitterException::class)
    fun getHomeTweetList(paging: Paging): List<TweetEntity> {
        val result = mTwitter.getHomeTimeline(paging)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun getReplyTweetList(paging: Paging): List<TweetEntity> {
        val result = mTwitter.getMentionsTimeline(paging)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun getUserTweets(userId: Long, paging: Paging): List<TweetEntity> {
        val result = mTwitter.getUserTimeline(userId, paging)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun getFavoritList(userId: Long, paging: Paging): List<TweetEntity> {
        val result = mTwitter.getFavorites(userId, paging)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun search(query: Query): List<TweetEntity> {
        val result = mTwitter.search(query)
        return mappingAdd(result.tweets)
    }

    @Throws(TwitterException::class)
    fun favorite(tweetId: Long): TweetEntity {
        val result = mTwitter.createFavorite(tweetId)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun unfavorite(tweetId: Long): TweetEntity {
        val result = mTwitter.destroyFavorite(tweetId)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun retweet(tweetId: Long): TweetEntity {
        val result = mTwitter.retweetStatus(tweetId)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun destroy(tweetId: Long): TweetEntity {
        val result = mTwitter.destroyStatus(tweetId)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun updateTweet(tweet: StatusUpdate): TweetEntity {
        val result = mTwitter.updateStatus(tweet)
        return mappingAdd(result)
    }

    fun get(tweetId: Long) = tweetCache.getValue(tweetId)

    @Throws(TwitterException::class)
    fun find(tweetId: Long): TweetEntity {
        return tweetCache.getOrPut(tweetId) {
            val result = mTwitter.showStatus(tweetId)
            mMapper.map(result)
        }
    }

    @Throws(TwitterException::class)
    fun findBetween(userId: Long, paging: Paging): List<TweetEntity> {
        if (tweetCache.containsKey(paging.sinceId) && tweetCache.containsKey(paging.maxId)) {
            return tweetCache.filter { it.value.user.id == userId }
                         .filter { it.value.id in paging.sinceId .. paging.maxId }
                         .values
                         .sortedBy { it.id }
        }
        return getUserTweets(userId, paging)
    }

    fun addStatusDeletionNotice(deletionNotice: StatusDeletionNotice) {
        deletedNotice.put(deletionNotice.statusId, deletionNotice)
        tweetCache[deletionNotice.statusId]?.onDelete()
    }

    fun hasDeletionNotice(statusID: Long): Boolean {
        return deletedNotice.containsKey(statusID)
    }

    fun getDeletionNotice(statusID: Long): StatusDeletionNotice {
        return deletedNotice.getValue(statusID)
    }

    @Synchronized private fun add(tweet: TweetEntity) {
        tweetCache.put(tweet.id, tweet)
        if (tweet.isRetweet) {
            add(tweet.retweet)
        }
        if (tweet.hasQuotedStatus) {
            add(tweet.quotedTweet)
        }
    }

    fun has(id: Long) = tweetCache.contains(id)

    private fun addAllTweet(tweetList: List<TweetEntity>) {
        tweetList.forEach { add(it) }
    }

    fun mappingAdd(status: Status): TweetEntity {
        val tweet = mMapper.map(status)
        add(tweet)
        return tweet
    }

    private fun mappingAdd(statusList: List<Status>): List<TweetEntity> {
        val tweets = mMapper.map(statusList)
        addAllTweet(tweets)
        return tweets
    }

    fun clear() {
        tweetCache.clear()
        deletedNotice.clear()
    }
}