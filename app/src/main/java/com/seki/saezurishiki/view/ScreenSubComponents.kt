package com.seki.saezurishiki.view

import com.seki.saezurishiki.presenter.activity.LoginUserPresenter
import com.seki.saezurishiki.presenter.activity.UserPresenter
import com.seki.saezurishiki.presenter.editor.TweetEditorPresenter
import com.seki.saezurishiki.presenter.list.TweetListPresenter
import com.seki.saezurishiki.presenter.list.UserListPresenter
import com.seki.saezurishiki.view.activity.LoginUserActivity
import com.seki.saezurishiki.view.activity.UserActivity
import com.seki.saezurishiki.view.fragment.editor.EditTweetFragment
import com.seki.saezurishiki.view.fragment.list.*
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [LoginUserModule::class])
interface LoginUserSubComponent {
    fun inject(activity: LoginUserActivity)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun presenterView(view: LoginUserPresenter.View): Builder
        fun module(module: LoginUserModule): Builder
        fun build(): LoginUserSubComponent
    }
}

@Subcomponent(modules = [UserModule::class])
interface UserSubComponent {
    fun inject(activity: UserActivity)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun presenterView(view: UserPresenter.View): Builder
        @BindsInstance
        fun userId(userId: Long): Builder
        fun module(module: UserModule): Builder
        fun build(): UserSubComponent
    }
}

@Subcomponent(modules = [ConversationModule::class])
interface ConversationSubComponent {
    fun inject(fragment: ConversationFragment)

    @Subcomponent.Builder
    interface Builder : TweetListComponentBuilder<ConversationModule, ConversationSubComponent>
}

@Subcomponent(modules = [FavoriteModule::class])
interface FavoriteSubComponent {
    fun inject(fragment: FavoritesFragment)

    @Subcomponent.Builder
    interface Builder : TweetListComponentBuilder<FavoriteModule, FavoriteSubComponent>
}

@Subcomponent(modules = [HomeModule::class])
interface HomeSubComponent {
    fun inject(fragment: HomeTimeLineFragment)

    @Subcomponent.Builder
    interface Builder : TweetListComponentBuilder<HomeModule, HomeSubComponent>
}

@Subcomponent(modules = [ReplyModule::class])
interface ReplySubComponent {
    fun inject(fragment: ReplyTimeLineFragment)

    @Subcomponent.Builder
    interface Builder : TweetListComponentBuilder<ReplyModule, ReplySubComponent>
}

@Subcomponent(modules = [SearchModule::class])
interface SearchSubComponent {
    fun inject(fragment: SearchFragment)

    @Subcomponent.Builder
    interface Builder : TweetListComponentBuilder<SearchModule, SearchSubComponent>
}

@Subcomponent(modules = [UserTweetModule::class])
interface UserTweetSubComponent {
    fun inject(fragment: UserTweetFragment)

    @Subcomponent.Builder
    interface Builder : TweetListComponentBuilder<UserTweetModule, UserTweetSubComponent>
}

@Subcomponent(modules = [FriendListModule::class])
interface FriendSubComponent {
    fun inject(fragment: FriendListFragment)

    @Subcomponent.Builder
    interface Builder : UserListComponentBuilder<FriendListModule, FriendSubComponent>
}

@Subcomponent(modules = [FollowerListModule::class])
interface FollowerSubComponent {
    fun inject(fragment: FollowerListFragment)

    @Subcomponent.Builder
    interface Builder : UserListComponentBuilder<FollowerListModule, FollowerSubComponent>
}

@Subcomponent(modules = [TweetEditorModule::class])
interface TweetEditorSubComponent {
    fun inject(fragment: EditTweetFragment)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun presenterView(view: TweetEditorPresenter.View): Builder
        fun module(module: TweetEditorModule): Builder
        fun build(): TweetEditorSubComponent
    }
}

interface TweetListComponentBuilder<M, C> {
    @BindsInstance
    fun presenterView(view: TweetListPresenter.TweetListView): TweetListComponentBuilder<M, C>
    @BindsInstance
    fun listOwnerId(listOwnerId: Long): TweetListComponentBuilder<M, C>
    fun module(module: M): TweetListComponentBuilder<M, C>
    fun build(): C
}

interface UserListComponentBuilder<M, C> {
    @BindsInstance
    fun presenterView(view: UserListPresenter.View): UserListComponentBuilder<M, C>
    @BindsInstance
    fun listOwnerId(listOwnerId: Long): UserListComponentBuilder<M, C>
    fun module(module: M): UserListComponentBuilder<M, C>
    fun build(): C
}