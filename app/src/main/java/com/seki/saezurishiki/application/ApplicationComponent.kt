package com.seki.saezurishiki.application

import com.seki.saezurishiki.model.impl.ModelModule
import com.seki.saezurishiki.view.ScreenModule
import com.seki.saezurishiki.view.fragment.*
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(modules = [
    ModelModule::class,
    ScreenModule::class
])
@Singleton
interface ApplicationComponent {

    fun conversationComponentBuilder(): ConversationSubComponent.Builder
    fun favoriteComponentBuilder(): FavoriteSubComponent.Builder
    fun homeComponentBuilder(): HomeSubComponent.Builder
    fun replyComponentBuilder(): ReplySubComponent.Builder
    fun searchComponentBuilder(): SearchSubComponent.Builder
    fun userTweetComponentBuilder(): UserTweetSubComponent.Builder
}