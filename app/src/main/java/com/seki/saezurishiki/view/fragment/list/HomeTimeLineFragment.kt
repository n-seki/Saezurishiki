package com.seki.saezurishiki.view.fragment.list

import android.os.Bundle
import com.seki.saezurishiki.application.SaezurishikiApp
import com.seki.saezurishiki.view.HomeModule

class HomeTimeLineFragment : UserStreamTimeLineFragment() {

    companion object {
        @JvmStatic
        fun getInstance(userId: Long, tabPosition: Int, listName: String): TweetListFragment {
            return HomeTimeLineFragment().apply {
                arguments = Bundle().apply {
                    putLong(TweetListFragment.USER_ID, userId)
                    putInt(TAB_POSITION, tabPosition)
                    putString(LIST_NAME, listName)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listOwnerId = arguments!!.getLong(TweetListFragment.USER_ID)
        SaezurishikiApp.mApplicationComponent.homeComponentBuilder()
                .listOwnerId(listOwnerId)
                .presenterView(this)
                .module(HomeModule())
                .build()
                .inject(this)
    }
}