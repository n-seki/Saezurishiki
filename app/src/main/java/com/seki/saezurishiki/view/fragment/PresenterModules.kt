package com.seki.saezurishiki.view.fragment

import com.seki.saezurishiki.model.TweetListModel
import com.seki.saezurishiki.presenter.list.*
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ConversationModule {

    @Provides
    fun provideConversationPresenter(
            view: TweetListPresenter.TweetListView,
            listOwnerId: Long,
            @Named("conversation") model: TweetListModel
    ): TweetListPresenter {
        return ConversationPresenter(view, listOwnerId, model)
    }
}

@Module
class FavoriteModule {

    @Provides
    fun provideFavoriteListPresenter(
            view: TweetListPresenter.TweetListView,
            listOwnerId: Long,
            @Named("favorite") model: TweetListModel
    ): TweetListPresenter {
        return FavoriteListPresenter(view, listOwnerId, model)
    }
}

@Module
class HomeModule {

    @Provides
    fun provideHomeTimeLinePresenter(
            view: TweetListPresenter.TweetListView,
            listOwnerId: Long,
            @Named("home") model: TweetListModel
    ): TweetListPresenter {
        return HomeTimeLinePresenter(view, listOwnerId, model)
    }
}

@Module
class ReplyModule {

    @Provides
    fun provideReplyTimeLinePresenter(
            view: TweetListPresenter.TweetListView,
            listOwnerId: Long,
            @Named("reply") model: TweetListModel
    ): TweetListPresenter {
        return ReplyTimeLinePresenter(view, listOwnerId, model)
    }
}

@Module
class SearchModule {

    @Provides
    fun provideSearchPresenter(
            view: TweetListPresenter.TweetListView,
            listOwnerId: Long,
            @Named("search") model: TweetListModel
    ): TweetListPresenter {
        return SearchPresenter(view, listOwnerId, model)
    }
}

@Module
class UserTweetModule {

    @Provides
    fun provideUserTweetListPresenter(
            view: TweetListPresenter.TweetListView,
            listOwnerId: Long,
            @Named("user_tweet") model: TweetListModel
    ): TweetListPresenter {
        return UserTweetListPresenter(view, listOwnerId, model)
    }
}
