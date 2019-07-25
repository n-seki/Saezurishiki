package com.seki.saezurishiki.view.adapter

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.seki.saezurishiki.R
import com.seki.saezurishiki.control.UIControlUtil.formatDate
import com.seki.saezurishiki.entity.UserEntity
import com.seki.saezurishiki.model.GetUserById
import com.squareup.picasso.Picasso

class UserListAdapter(
        private val context: Context,
        private val repositoryAccessor: GetUserById,
        private val listener: OnClickUserListener,
        private val onClickFooter: View.OnClickListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val userIds = mutableListOf<Long>()
    private val layoutInflater = LayoutInflater.from(context)

    var needFooter = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var isLoading = true
        set(value) {
            val previous = field
            field = value
            if ((previous != field) && needFooter) {
                notifyItemChanged(itemCount - 1)
            }
        }

    companion object {
        private const val VIEW_TYPE_USER = 0
        private const val VIEW_TYPE_FOOTER = 1
    }

    override fun getItemViewType(position: Int): Int {
        if (!needFooter) {
            return VIEW_TYPE_USER
        }

        return when (position) {
            itemCount - 1 -> VIEW_TYPE_FOOTER
            else -> VIEW_TYPE_USER
        }
    }

    override fun onCreateViewHolder(rootView: ViewGroup, viewType: Int) =
        if (viewType == VIEW_TYPE_USER) {
            val view = layoutInflater.inflate(R.layout.user_info_layout, rootView, false)
            UserViewHolder(view)
        } else  {
            val view = layoutInflater.inflate(R.layout.read_more_tweet, rootView, false)
            FooterViewHolder(view)
        }

    override fun getItemCount() = userIds.size + if (needFooter) 1 else 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> {
                val user = repositoryAccessor[userIds[position]]
                bindUserView(holder, user)
            }
            is FooterViewHolder -> {
                val textId = if (isLoading) R.string.now_loading else R.string.click_to_load
                bindFooterView(holder, textId)
            }
        }
    }

    private fun bindUserView(holder: UserViewHolder, user: UserEntity) {
        Picasso.with(context).load(user.biggerProfileImageURL).into(holder.userIcon)
        val concatName = "${user.screenName} / ${user.name}"
        holder.userName.text = concatName
        holder.bioText.text = user.description
        val since = "since : ${formatDate(user.createdAt)}"
        holder.since.text = since
        holder.itemView.setOnClickListener {
            listener.onClick(user)
        }
    }

    private fun bindFooterView(holder: FooterViewHolder, @StringRes textId: Int) {
        holder.text.setText(textId)
        holder.itemView.setOnClickListener(onClickFooter)
    }

    fun isEmpty() = userIds.isEmpty()

    fun addAll(users: List<UserEntity>) {
        userIds.addAll(users.map { it.id })
        notifyDataSetChanged()
    }

    interface OnClickUserListener {
        fun onClick(user: UserEntity)
    }
}

class UserViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val userIcon: ImageView = view.findViewById(R.id.user_user_icon)
    val userName: TextView = view.findViewById(R.id.user_user_name)
    val bioText: TextView = view.findViewById(R.id.user_bio_text)
    val since: TextView = view.findViewById(R.id.user_since)
}

class FooterViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val text: TextView = view.findViewById(R.id.read_more)
}