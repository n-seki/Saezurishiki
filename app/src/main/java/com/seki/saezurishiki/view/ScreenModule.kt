package com.seki.saezurishiki.view

import com.seki.saezurishiki.view.fragment.*
import dagger.Module

@Module(subcomponents = [
    ConversationSubComponent::class,
    FavoriteSubComponent::class,
    HomeSubComponent::class,
    ReplySubComponent::class,
    SearchSubComponent::class,
    UserTweetSubComponent::class
])
class ScreenModule