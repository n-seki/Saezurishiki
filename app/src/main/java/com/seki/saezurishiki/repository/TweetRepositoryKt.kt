package com.seki.saezurishiki.repository

import com.seki.saezurishiki.entity.TweetEntity
import com.seki.saezurishiki.entity.TwitterEntity
import com.seki.saezurishiki.entity.mapper.EntityMapper
import twitter4j.*
import java.util.concurrent.ConcurrentHashMap

class TweetRepositoryKt(private val twitter: Twitter, private val tweetMapper: EntityMapper) {

    private val tweets: MutableMap<Long, TweetEntity>
    private val deletedNotice: MutableMap<Long, StatusDeletionNotice>

    init {
        tweets = ConcurrentHashMap()
        deletedNotice = ConcurrentHashMap()
    }

    @Throws(TwitterException::class)
    fun getHomeTweetList(paging: Paging): List<TweetEntity> {
        val result = twitter.getHomeTimeline(paging)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun getReplyTweetList(paging: Paging): List<TweetEntity> {
        val result = twitter.getMentionsTimeline(paging)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun getFevoritList(userId: Long, paging: Paging): List<TweetEntity> {
        val result = twitter.getFavorites(userId, paging)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun search(query: Query): List<TweetEntity> {
        val result = twitter.search(query)
        return mappingAdd(result.tweets)
    }

    @Throws(TwitterException::class)
    fun favorite(tweetId: Long): TweetEntity {
        val result = twitter.createFavorite(tweetId)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun unfavorite(tweetId: Long): TweetEntity {
        val result = twitter.destroyFavorite(tweetId)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun retweet(tweetId: Long): TweetEntity {
        val result = twitter.retweetStatus(tweetId)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun destroy(tweetId: Long): TweetEntity {
        val result = twitter.destroyStatus(tweetId)
        return mappingAdd(result)
    }

    fun get(tweetId: Long) = tweets.getValue(tweetId)

    @Throws(TwitterException::class)
    fun find(tweetId: Long): TweetEntity {
        return tweets.getOrPut(tweetId) {
            val result = twitter.showStatus(tweetId)
            tweetMapper.map(result)
        }
    }

    fun addStatusDeletionNotice(deletionNotice: StatusDeletionNotice) {
        deletedNotice.put(deletionNotice.statusId, deletionNotice)
        tweets[deletionNotice.statusId]?.onDelete()
    }

    fun hasDeletionNotice(statusID: Long): Boolean {
        return deletedNotice.containsKey(statusID)
    }

    fun getDeletionNotice(statusID: Long): StatusDeletionNotice {
        return deletedNotice.getValue(statusID)
    }

    @Synchronized private fun add(tweet: TweetEntity) {
        tweets.put(tweet.id, tweet)
        if (tweet.isRetweet) {
            add(tweet.retweet)
        }
        if (tweet.hasQuotedStatus) {
            add(tweet.quotedTweet)
        }
    }

    private fun addAllTweet(tweetList: List<TweetEntity>) {
        tweetList.forEach { add(it) }
    }

    private fun mappingAdd(status: Status): TweetEntity {
        val tweet = tweetMapper.map(status)
        add(tweet)
        return tweet
    }

    private fun mappingAdd(statusList: List<Status>): List<TweetEntity> {
        val tweets = tweetMapper.map(statusList)
        addAllTweet(tweets)
        return tweets
    }

    public fun clear() {
        tweets.clear()
        deletedNotice.clear()
    }
}