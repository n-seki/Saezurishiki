package com.seki.saezurishiki.network.twitter

import android.content.Context
import com.seki.saezurishiki.cache.TweetCache
import com.seki.saezurishiki.cache.UserCache
import com.seki.saezurishiki.entity.mapper.EntityMapper
import com.seki.saezurishiki.repository.RemoteRepositoryImp
import com.seki.saezurishiki.repository.TweetRepository
import com.seki.saezurishiki.repository.UserRepository
import twitter4j.Twitter
import twitter4j.TwitterFactory
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TwitterProvider @Inject constructor(
        @Named("application") private val applicationContext: Context
) {

    companion object {
        private var instance: Twitter? = null
    }

    var loginUserId: Long = 0
        private set
        get() {
            if (instance == null) {
                throw IllegalStateException("Initialized is not finished")
            }
            return field
        }

    fun init() {
        if (instance != null) {
            return
        }
        val configuration = TwitterUtil.AccountConfig(applicationContext)
        instance = TwitterFactory(configuration.configuration).getInstance(configuration.token)
        loginUserId = configuration.loginUserId
        val mapper = EntityMapper(configuration.loginUserId)
        RemoteRepositoryImp.onCreate(instance, mapper)
        TweetRepository.setup(instance!!, mapper, TweetCache())
        UserRepository.setup(instance!!, mapper, UserCache())
    }

    fun provide(): Twitter {
        return instance!!
    }
}