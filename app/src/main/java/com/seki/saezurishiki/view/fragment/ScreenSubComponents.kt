package com.seki.saezurishiki.view.fragment

import com.seki.saezurishiki.presenter.list.TweetListPresenter
import com.seki.saezurishiki.view.fragment.list.*
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [ConversationModule::class])
interface ConversationSubComponent {
    fun inject(fragment: ConversationFragment)

    @Subcomponent.Builder
    interface Builder : BaseBuilder<ConversationModule, ConversationSubComponent>
}

@Subcomponent(modules = [FavoriteModule::class])
interface FavoriteSubComponent {
    fun inject(fragment: FavoritesFragment)

    @Subcomponent.Builder
    interface Builder : BaseBuilder<FavoriteModule, FavoriteSubComponent>
}

@Subcomponent(modules = [HomeModule::class])
interface HomeSubComponent {
    fun inject(fragment: HomeTimeLineFragment)

    @Subcomponent.Builder
    interface Builder : BaseBuilder<HomeModule, HomeSubComponent>
}

@Subcomponent(modules = [ReplyModule::class])
interface ReplySubComponent {
    fun inject(fragment: ReplyTimeLineFragment)

    @Subcomponent.Builder
    interface Builder : BaseBuilder<ReplyModule, ReplySubComponent>
}

@Subcomponent(modules = [SearchModule::class])
interface SearchSubComponent {
    fun inject(fragment: SearchFragment)

    @Subcomponent.Builder
    interface Builder : BaseBuilder<SearchModule, SearchSubComponent>
}

@Subcomponent(modules = [UserTweetModule::class])
interface UserTweetSubComponent {
    fun inject(fragment: UserTweetFragment)

    @Subcomponent.Builder
    interface Builder : BaseBuilder<UserTweetModule, UserTweetSubComponent>
}

interface BaseBuilder<M, C> {
    @BindsInstance
    fun presenterView(view: TweetListPresenter.TweetListView): BaseBuilder<M, C>
    @BindsInstance
    fun listOwnerId(listOwnerId: Long): BaseBuilder<M, C>
    fun module(module: M): BaseBuilder<M, C>
    fun build(): C
}