package com.seki.saezurishiki.model.impl

import com.seki.saezurishiki.model.*
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ModelModule {

    @Provides
    internal fun provideUseScreenModel(model: UserScreenModelImp): UserScreenModel {
        return model
    }

    @Provides
    @Named("home")
    internal fun provideHomeListModel(model: HomeTweetListModel): TweetListModel {
        return model
    }

    @Provides
    @Named("reply")
    internal fun provideReplyListModel(model: ReplyTweetListModel): TweetListModel {
        return model
    }

    @Provides
    @Named("favorite")
    internal fun provideFavoriteListModel(model: FavoriteListModel): TweetListModel {
        return model
    }

    @Provides
    @Named("user_tweet")
    internal fun provideUserTweetListModel(model: UserTweetListModel): TweetListModel {
        return model
    }

    @Provides
    @Named("conversation")
    internal fun provideConversationListModel(model: ConversationModel): TweetListModel {
        return model
    }

    @Provides
    @Named("search")
    internal fun provideSearchTweetModel(model: SearchTweetModel): TweetListModel {
        return model
    }

    @Provides
    internal fun provideGetTweetById(model: GetTweetByIdImp): GetTweetById {
        return model
    }

    @Provides
    internal fun provideGetUserById(model: GetUserByIdImp): GetUserById {
        return model
    }

    @Provides
    @Named("friend")
    internal fun provideFriendListModel(model: FriendListModel): UserListModel {
        return model
    }

    @Provides
    @Named("follower")
    internal fun provideFollowerListModel(model: FollowerListModel): UserListModel {
        return model
    }

    @Provides
    internal fun provideLoginUserModel(model: LoginUserScreenImp): LoginUserScreen {
        return model
    }

    @Provides
    internal fun provideTweetEditorModel(model: TweetEditorModelImp): TweetEditorModel {
        return model
    }
}