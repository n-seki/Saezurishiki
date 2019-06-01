package com.seki.saezurishiki.view

import dagger.Module

@Module(subcomponents = [
    LoginUserSubComponent::class,
    UserSubComponent::class,
    ConversationSubComponent::class,
    FavoriteSubComponent::class,
    HomeSubComponent::class,
    ReplySubComponent::class,
    SearchSubComponent::class,
    UserTweetSubComponent::class,
    FriendSubComponent::class,
    FollowerSubComponent::class,
    TweetEditorSubComponent::class
])
class ScreenModule