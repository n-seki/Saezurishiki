package com.seki.saezurishiki.view

import com.seki.saezurishiki.model.*
import com.seki.saezurishiki.presenter.activity.LoginUserPresenter
import com.seki.saezurishiki.presenter.activity.UserPresenter
import com.seki.saezurishiki.presenter.editor.TweetEditorPresenter
import com.seki.saezurishiki.presenter.list.*
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class LoginUserModule {

    @Provides
    fun provideLoginUserPresenter(
            view: LoginUserPresenter.View,
            model: LoginUserScreen
    ): LoginUserPresenter {
        return LoginUserPresenter(model, view)
    }
}

@Module
class UserModule {

    @Provides
    fun provideUserPresenter(
            view: UserPresenter.View,
            userId: Long,
            model: UserScreenModel
    ): UserPresenter {
        return UserPresenter(view, model, userId)
    }
}

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

@Module
class FriendListModule {

    @Provides
    fun provideFriendUserListPresenter(
            view: UserListPresenter.View,
            userId: Long,
            @Named("friend") model: UserListModel
    ): UserListPresenter {
        return FriendListPresenter(view, model, userId)
    }
}

@Module
class FollowerListModule {

    @Provides
    fun provideFollowerUserListPresenter(
            view: UserListPresenter.View,
            userId: Long,
            @Named("follower") model: UserListModel
    ): UserListPresenter {
        return FollowerListPresenter(view, model, userId)
    }
}

@Module
class TweetEditorModule {

    @Provides
    fun provideTweetEditorPresenter(
            view: TweetEditorPresenter.View,
            model: TweetEditorModel
    ): TweetEditorPresenter {
        return TweetEditorPresenter(view, model)
    }
}