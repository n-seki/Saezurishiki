package com.seki.saezurishiki.view.adapter

import android.databinding.BindingAdapter
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.seki.saezurishiki.entity.TweetEntity
import com.seki.saezurishiki.view.customview.TweetStatusBar
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl", "imageSize")
fun loadImage(view: ImageView, imageUrl: String?, size: Int) {
    imageUrl ?: return
    Picasso.with(view.context)
            .load(imageUrl)
            .resize(size, size)
            .centerInside()
            .into(view)
}

@BindingAdapter("tweet")
fun setStatusColor(view: TweetStatusBar, tweet: TweetEntity) {
    if (tweet.isDeleted) {
        view.setDeletedColor(view.context)
        return;
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

@BindingAdapter("textSize")
fun setTextSize(textView: TextView, rawSize: Int) {
    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, rawSize.toFloat())
}