package com.seki.saezurishiki.repository

import com.seki.saezurishiki.cache.TweetCache
import com.seki.saezurishiki.entity.TweetEntity
import com.seki.saezurishiki.entity.mapper.EntityMapper
import com.seki.saezurishiki.network.twitter.TwitterProvider
import twitter4j.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TweetRepository @Inject constructor(
        private val twitterProvider: TwitterProvider,
        private val mapper: EntityMapper,
        private val cache: TweetCache
) {

    private val deletedNotice: MutableMap<Long, StatusDeletionNotice>

    init {
        deletedNotice = ConcurrentHashMap()
    }

    @Throws(TwitterException::class)
    fun getHomeTweetList(paging: Paging): List<TweetEntity> {
        val result = twitterProvider.getInstance().getHomeTimeline(paging)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun getReplyTweetList(paging: Paging): List<TweetEntity> {
        val result = twitterProvider.getInstance().getMentionsTimeline(paging)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun getUserTweets(userId: Long, paging: Paging): List<TweetEntity> {
        val result = twitterProvider.getInstance().getUserTimeline(userId, paging)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun getFavoriteList(userId: Long, paging: Paging): List<TweetEntity> {
        val result = twitterProvider.getInstance().getFavorites(userId, paging)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun search(query: Query): List<TweetEntity> {
        val result = twitterProvider.getInstance().search(query)
        return mappingAdd(result.tweets)
    }

    @Throws(TwitterException::class)
    fun favorite(tweetId: Long): TweetEntity {
        val result = twitterProvider.getInstance().createFavorite(tweetId)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun unfavorite(tweetId: Long): TweetEntity {
        val result = twitterProvider.getInstance().destroyFavorite(tweetId)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun retweet(tweetId: Long): TweetEntity {
        val result = twitterProvider.getInstance().retweetStatus(tweetId)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun destroy(tweetId: Long): TweetEntity {
        val result = twitterProvider.getInstance().destroyStatus(tweetId)
        return mappingAdd(result)
    }

    @Throws(TwitterException::class)
    fun updateTweet(tweet: StatusUpdate): TweetEntity {
        val result = twitterProvider.getInstance().updateStatus(tweet)
        return mappingAdd(result)
    }

    fun get(tweetId: Long) = cache[tweetId]

    @Throws(TwitterException::class)
    fun find(tweetId: Long): TweetEntity {
        if (cache.has(tweetId)) {
            return cache[tweetId]
        }
        val tweet = twitterProvider.getInstance().showStatus(tweetId)
        return mappingAdd(tweet)
    }

    fun hasDeletionNotice(statusID: Long): Boolean {
        return deletedNotice.containsKey(statusID)
    }

    @Synchronized private fun add(tweet: TweetEntity) {
        cache.put(tweet)
        if (tweet.isRetweet) {
            add(tweet.retweet)
        }
        if (tweet.hasQuotedStatus) {
            add(tweet.quotedTweet)
        }
    }

    fun has(id: Long) = cache.has(id)

    private fun addAllTweet(tweetList: List<TweetEntity>) {
        tweetList.forEach { add(it) }
    }

    fun mappingAdd(status: Status): TweetEntity {
        val tweet = mapper.map(status)
        add(tweet)
        return tweet
    }

    private fun mappingAdd(statusList: List<Status>): List<TweetEntity> {
        val tweets = mapper.map(statusList)
        addAllTweet(tweets)
        return tweets
    }

    fun clear() {
        cache.clearAll()
        deletedNotice.clear()
    }
}