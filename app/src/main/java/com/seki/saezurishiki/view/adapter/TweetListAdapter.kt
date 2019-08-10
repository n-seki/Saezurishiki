package com.seki.saezurishiki.view.adapter

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.seki.saezurishiki.R
import com.seki.saezurishiki.control.Setting
import com.seki.saezurishiki.databinding.TweetLayoutWithPictureBinding
import com.seki.saezurishiki.entity.TweetEntity
import com.seki.saezurishiki.entity.UserEntity
import com.seki.saezurishiki.model.GetTweetById
import java.lang.IllegalStateException

class TweetListAdapter(
        context: Context,
        private val repositoryAccessor: GetTweetById,
        private val tweetListener: TweetListener,
        private val onClickButton: View.OnClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TWEET = 0
        private const val VIEW_TYPE_BUTTON = 1
    }

    private val layoutInflater = LayoutInflater.from(context)
    private val settings = Setting()

    private val tweetIds = mutableListOf<Long>()

    var needFooter = true
        set(value) {
            field = value
            if (field) {
                notifyItemInserted(itemCount - 1)
            } else {
                notifyItemRemoved(itemCount - 1)
            }
        }

    var isLoading = true
        set(value) {
            val previous = field
            field = value
            if ((previous != field) && needFooter) {
                notifyItemChanged(itemCount - 1)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TWEET -> TweetViewHolder(
                    layoutInflater.inflate(R.layout.tweet_layout_with_picture, parent, false))
            VIEW_TYPE_BUTTON -> ButtonViewHolder(
                    layoutInflater.inflate(R.layout.read_more_tweet, parent, false))
            else -> throw IllegalStateException("Unexpected view type: $viewType")
        }
    }

    override fun getItemCount() = if (needFooter) tweetIds.size + 1 else tweetIds.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is TweetViewHolder -> {
                val tweet = repositoryAccessor[tweetIds[position]]
                bindTweet(viewHolder, tweet)
            }

            is ButtonViewHolder -> {
                val textId = if (isLoading) R.string.now_loading else R.string.click_to_load
                bindButton(viewHolder, textId)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (!needFooter) {
            return VIEW_TYPE_TWEET
        }

        return when (position) {
            itemCount - 1 -> VIEW_TYPE_BUTTON
            else -> VIEW_TYPE_TWEET
        }
    }

    fun add(tweet: TweetEntity) {
        tweetIds.add(tweet.id)
        notifyItemInserted(itemCount - 1)
    }

    fun addAll(tweets: List<TweetEntity>) {
        tweetIds.addAll(tweets.map { it.id })
        notifyDataSetChanged()
    }

    fun addAllFirst(tweets: List<TweetEntity>) {
        tweetIds.addAll(0, tweets.map { it.id })
        notifyDataSetChanged()
    }

    fun remove(tweet: TweetEntity) {
        val position = tweetIds.indexOf(tweet.id)
        tweetIds.remove(tweet.id)
        notifyItemRemoved(position)
    }

    fun getTweetIdAt(position: Int): Long {
        return tweetIds[position]
    }

    fun getLastTweetId() = tweetIds.last()

    fun isEmpty() = tweetIds.isEmpty()

    private fun bindTweet(holder: TweetViewHolder, tweet: TweetEntity) {
        holder.binding.apply {
            quotedStatusLayout.visibility = View.GONE
            reTweeter.visibility = View.GONE
            lockIcon.visibility = View.GONE
            setting = settings
            listener = tweetListener

            if (tweet.isRetweet) {
                retweetUser = tweet.user
                setTweet(tweet.retweet)
            } else {
                retweetUser = null
                setTweet(tweet)
            }
            executePendingBindings()
        }
    }

    private fun bindButton(holder: ButtonViewHolder, @StringRes text: Int) {
        holder.text.setText(text)
        holder.itemView.setOnClickListener(onClickButton)
    }

    interface TweetListener {
        fun onClick(tweet: TweetEntity)
        fun onLongClick(tweet: TweetEntity): Boolean
        fun onClickPicture(position: Int, tweet: TweetEntity)
        fun onClickUserIcon(view: View, user: UserEntity)
        fun onClickReplyButton(tweet: TweetEntity)
        fun onClickReTweetButton(tweet: TweetEntity, action: Setting.ButtonActionPattern)
        fun onLongClickReTweetButton(tweet: TweetEntity, action: Setting.ButtonActionPattern): Boolean
        fun onClickFavoriteButton(tweet: TweetEntity, action: Setting.ButtonActionPattern)
        fun onLongClickFavoriteButton(tweet: TweetEntity, action: Setting.ButtonActionPattern): Boolean
        fun onClickQuotedTweet(tweet: TweetEntity)
    }
}

class TweetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = TweetLayoutWithPictureBinding.bind(view)!!
}

class ButtonViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val text: TextView = view.findViewById(R.id.read_more)
}