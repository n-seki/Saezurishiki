package com.seki.saezurishiki.model.impl

import com.seki.saezurishiki.model.*
import dagger.Binds
import dagger.Module
import javax.inject.Named

@Module
abstract class ModelModule {

    @Binds
    internal abstract fun provideUseScreenModel(model: UserScreenModelImp): UserScreenModel

    @Binds
    @Named("home")
    internal abstract fun provideHomeListModel(model: HomeTweetListModel): TweetListModel

    @Binds
    @Named("reply")
    internal abstract fun provideReplyListModel(model: ReplyTweetListModel): TweetListModel

    @Binds
    @Named("favorite")
    internal abstract fun provideFavoriteListModel(model: FavoriteListModel): TweetListModel

    @Binds
    @Named("user_tweet")
    internal abstract fun provideUserTweetListModel(model: UserTweetListModel): TweetListModel

    @Binds
    @Named("conversation")
    internal abstract fun provideConversationListModel(model: ConversationModel): TweetListModel

    @Binds
    @Named("search")
    internal abstract fun provideSearchTweetModel(model: SearchTweetModel): TweetListModel

    @Binds
    internal abstract fun provideGetTweetById(model: GetTweetByIdImp): GetTweetById

    @Binds
    internal abstract fun provideGetUserById(model: GetUserByIdImp): GetUserById

    @Binds
    @Named("friend")
    internal abstract fun provideFriendListModel(model: FriendListModel): UserListModel

    @Binds
    @Named("follower")
    internal abstract fun provideFollowerListModel(model: FollowerListModel): UserListModel

    @Binds
    internal abstract fun provideLoginUserModel(model: LoginUserScreenImp): LoginUserScreen

    @Binds
    internal abstract fun provideTweetEditorModel(model: TweetEditorModelImp): TweetEditorModel
}