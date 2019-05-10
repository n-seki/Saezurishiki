package com.seki.saezurishiki.application

import com.seki.saezurishiki.model.GetUserById
import com.seki.saezurishiki.model.impl.ModelModule
import com.seki.saezurishiki.view.*
import com.seki.saezurishiki.view.fragment.dialog.TweetSelectDialog
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    ModelModule::class,
    ScreenModule::class
])
@Singleton
interface ApplicationComponent {

    fun getUserById(): GetUserById

    fun inject(dialog: TweetSelectDialog)

    fun loginUserComponentBuilder(): LoginUserSubComponent.Builder
    fun userComponentBuilder(): UserSubComponent.Builder
    fun conversationComponentBuilder(): ConversationSubComponent.Builder
    fun favoriteComponentBuilder(): FavoriteSubComponent.Builder
    fun homeComponentBuilder(): HomeSubComponent.Builder
    fun replyComponentBuilder(): ReplySubComponent.Builder
    fun searchComponentBuilder(): SearchSubComponent.Builder
    fun userTweetComponentBuilder(): UserTweetSubComponent.Builder
    fun friendComponentBuilder(): FriendSubComponent.Builder
    fun followerComponentBuilder(): FollowerSubComponent.Builder
    fun tweetEditorComponentBuilder(): TweetEditorSubComponent.Builder
}