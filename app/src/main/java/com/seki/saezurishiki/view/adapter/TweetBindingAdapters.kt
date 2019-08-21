package com.seki.saezurishiki.view.adapter

import androidx.databinding.BindingAdapter
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.seki.saezurishiki.entity.TweetEntity
import com.seki.saezurishiki.view.customview.TweetStatusBar
import com.squareup.picasso.Picasso

@BindingAdapter(value = ["imageUrl", "imageSize"])
fun loadImage(view: ImageView, imageUrl: String?, size: Int) {
    imageUrl ?: return
    Picasso.with(view.context)
            .load(imageUrl)
            .resize(size, size)
            .centerInside()
            .into(view)
}

@BindingAdapter(value = [
    "thumbnail_tweet",
    "thumbnail_position",
    "thumbnail_imageSize",
    "thumbnail_isShowThumbnail"
])
fun loadMediaThumbnail(
        view: ImageView,
        tweet: TweetEntity,
        position: Int,
        size: Int,
        isShowThumbnail: Boolean
) {
    if (tweet.mediaUrlList == null || tweet.mediaUrlList.size <= position || !isShowThumbnail) {
        view.visibility = View.GONE
        return
    }
    Picasso.with(view.context)
            .load(tweet.mediaUrlList[position].thumbnail)
            .resize(size, size)
            .centerInside()
            .into(view)
    view.visibility = View.VISIBLE
}

@BindingAdapter(value = ["tweet"])
fun setStatusColor(view: TweetStatusBar, tweet: TweetEntity) {
    if (tweet.isDeleted) {
        view.setDeletedColor(view.context)
        return
    }

    if (tweet.isSentToLoginUser) {
        view.setReplyToMeColor(view.context)
        return
    }

    if (tweet.isSentByLoginUser) {
        view.setMyTweetColor(view.context)
        return
    }

    view.visibility = View.INVISIBLE
}

@BindingAdapter(value = ["textSize"])
fun setTextSize(textView: TextView, rawSize: Int) {
    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, rawSize.toFloat())
}