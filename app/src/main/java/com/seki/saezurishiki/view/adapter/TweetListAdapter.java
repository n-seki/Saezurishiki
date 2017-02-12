package com.seki.saezurishiki.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.entity.LoadButton;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.TwitterEntity;
import com.seki.saezurishiki.network.server.TwitterServer;
import com.seki.saezurishiki.view.adapter.viewholder.ViewHolder;
import com.seki.saezurishiki.view.adapter.viewholder.ViewHolderWithPicture;
import com.squareup.picasso.Picasso;

import java.util.List;

import twitter4j.User;

import static com.seki.saezurishiki.control.UIControlUtil.createMediaURLList;
import static com.seki.saezurishiki.control.UIControlUtil.formatDate;


public class TweetListAdapter extends ArrayAdapter<TweetEntity> {

    //getView()内でnewする良い方法を思いつけないので、
    //とりあえずフィールドとして保持
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private TweetListAdapter.ViewListener viewListener;

    private boolean backgroundChange = false;

    private TwitterServer repository;


    public TweetListAdapter(Context context, int resource, ViewListener listener) {
        super(context, resource);
        this.viewListener = listener;
    }

    public interface ViewListener {
        void onClickPicture(String pictureURL, TweetEntity status);
        void onClickUserIcon(User user);
        void onClickReplyButton(TweetEntity status);
        void onClickReTweetButton(final TweetEntity status, boolean isShowDialog);
        void onClickFavoriteButton(final TweetEntity status, boolean isShowDialog);
        void onClickQuotedTweet(final TweetEntity status);
    }


    private class PictureClickListener implements View.OnClickListener {

        private TweetEntity mStatus;

        PictureClickListener(TweetEntity status) {
            mStatus = status;
        }
        @Override
        public void onClick(final View v) {
            if (v instanceof ImageView) {
                viewListener.onClickPicture((String)(v.getTag()), mStatus);
            }
        }
    }


    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final TwitterEntity entity = getItem(position);

        if (entity == null) {
            return mLayoutInflater.inflate(R.layout.read_more_tweet, null);
        }

        if (entity.getItemType() == TwitterEntity.Type.LoadButton) {
            View view = createLoadButtonView();
            TextView text = (TextView)view.findViewById(R.id.read_more);
            text.setText(((LoadButton) entity).getLabelResId());
            return view;
        }

        View view = this.setStatusInformationToView((TweetEntity)entity, convertView);

        return view;
    }


    private View setStatusInformationToView(final TweetEntity status, View convertView) {
        ViewHolderWithPicture holder;

        if (convertView  == null || !(convertView.getTag() instanceof ViewHolderWithPicture)) {
            convertView = this.inflateTweetLayoutWithPicture();
            holder = new ViewHolderWithPicture(convertView);
            //Statusによって変化しないのはここでsetしておく
            holder.replyButtonArea.setOnClickListener(onReplyButtonClickListener);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderWithPicture)convertView.getTag();
        }

        this.setStatusWithPictureToViewHolder(status, holder);
        return convertView;
    }


    private void setFavoriteStarVisible(final TweetEntity status, ViewHolder holder) {
        if (status.isFavorited) {
            holder.favoriteStar.on();
        } else {
            holder.favoriteStar.off();
        }
    }


    private void setStatusWithPictureToViewHolder(final TweetEntity status, ViewHolderWithPicture holder) {
        if (status.isRetweet) {
            this.setReTweetStatusToViewHolder(status, holder);
            this.setPictureToViewHolder(repository.getStatus(status.retweetedStatusId), holder);
        } else {
            this.setStatusToViewHolder(status, holder);
            this.setPictureToViewHolder(status, holder);
        }
    }


    private void setStatusToViewHolder(final TweetEntity status, ViewHolder holder) {
        if (status.isRetweet) {
            this.setReTweetStatusToViewHolder(status, holder);
            return;
        }

        this.setUserNameToHolder(status, holder);
        String postTime = formatDate(status.createdAt);
        holder.mPostTime.setText(postTime);
        holder.mTweetText.setText(status.text);
        holder.mTweetText.setTag(status);

        this.setUserIcon(status, holder);
        this.setQuotedTweetLayout(status, holder);

        holder.mUserIcon.setTag(status.user);
        holder.mUserIcon.setOnClickListener(onUserIconClickListener);

        holder.favoriteButtonArea.setTag(status);
        holder.reTweetButtonArea.setTag(status);
        holder.replyButtonArea.setTag(status);

        this.setStatusBarColor(status, holder);
        this.setFavoriteStarVisible(status, holder);
        this.setReTweetButtonColor(status, holder);

        holder.mReTweeter_info.setVisibility(View.GONE);

        if (status.reTweetCount != 0) {
            holder.mReTweetCount.setText(String.valueOf(status.reTweetCount));
            holder.mReTweetCount.setVisibility(View.VISIBLE);
        } else {
            holder.mReTweetCount.setVisibility(View.GONE);
        }

        if (status.favoriteCount != 0) {
            holder.mFavoriteCount.setText(String.valueOf(status.favoriteCount));
            holder.mFavoriteCount.setVisibility(View.VISIBLE);
        } else {
            holder.mFavoriteCount.setVisibility(View.GONE);
        }
    }

    private void setReTweetButtonColor(TweetEntity status, ViewHolder holder) {
        if (status.user.isProtected() && !status.isSentByLoginUser) {
            holder.reTweetButtonArea.setVisibility(View.INVISIBLE);
            holder.reTweetButtonArea.setClickable(false);
            return;
        }

        holder.reTweetButtonArea.setVisibility(View.VISIBLE);
        holder.reTweetButtonArea.setClickable(true);

        if (status.isRetweetedbyLoginUser) {
            holder.mReTweetButtonMark.setBackgroundResource(R.drawable.retweet_mark_on);
        } else {
            holder.mReTweetButtonMark.setBackgroundResource(R.drawable.retweet_mark_off);
        }
    }


    private void setQuotedTweetLayout(final TweetEntity status, ViewHolder holder) {
        if (status.hasQuotedStatus) {
            final TweetEntity quotedStatus = this.repository.getStatus(status.quotedStatusId);
            Picasso.with(mContext).load(quotedStatus.user.getBiggerProfileImageURL()).into(holder.quotedUserIcon);
            holder.quotedUserName.setText(quotedStatus.user.getName());
            holder.quotedTweetText.setText(quotedStatus.text);
            holder.quotedTweetLayout.setVisibility(View.VISIBLE);
            holder.quotedTweetLayout.setTag(quotedStatus);
            holder.quotedTweetLayout.setOnClickListener(onQuotedTweetClickListener);
        } else {
            holder.quotedTweetLayout.setVisibility(View.GONE);
        }
    }

    private final View.OnClickListener onReplyButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //no check
            TweetEntity status = (TweetEntity) view.getTag();
            viewListener.onClickReplyButton(status);
        }
    };

    private final View.OnLongClickListener onReTweetButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            //no check
            TweetEntity status = (TweetEntity) view.getTag();
            viewListener.onClickReTweetButton(status, true);
            return true;
        }
    };

    private final View.OnClickListener onReTweetButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //no check
            TweetEntity status = (TweetEntity) view.getTag();
            viewListener.onClickReTweetButton(status, true);
        }
    };

    private final View.OnLongClickListener onFavoriteButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            TweetEntity status = (TweetEntity) view.getTag();
            viewListener.onClickFavoriteButton(status, true);
            return true;
        }
    };

    private final View.OnClickListener onFavoriteButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TweetEntity status = (TweetEntity) view.getTag();
            viewListener.onClickFavoriteButton(status, true);
        }
    };


    private final View.OnClickListener onUserIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            view.setEnabled(false);
            User user = (User)view.getTag();
            viewListener.onClickUserIcon(user);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setEnabled(true);
                }
            }, 1000L);
        }
    };


    private final View.OnClickListener onQuotedTweetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final TweetEntity status = (TweetEntity) view.getTag();
            viewListener.onClickQuotedTweet(status);
        }
    };




    private void setUserIcon(TweetEntity status, ViewHolder holder) {
        String iconURL = status.user.getBiggerProfileImageURL();

        if (holder.mUserIcon.getTag() == null || !holder.mUserIcon.getTag().equals(iconURL)) {
            Picasso.with(mContext).load(iconURL).resize(60, 60).into(holder.mUserIcon);
            holder.mUserIcon.setTag(iconURL);
        }
    }


    private void setReTweetStatusToViewHolder(final TweetEntity status, ViewHolder holder) {
        final TweetEntity reTweet = repository.getStatus(status.retweetedStatusId);

        this.setStatusToViewHolder(reTweet, holder);

        String postTime = formatDate(reTweet.createdAt);
        holder.mPostTime.setText(postTime);

        holder.mStatusBar.setReTweetColor(mContext);

        holder.mReTweeter_info.setVisibility(View.VISIBLE);
        holder.mReTweeter_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewListener.onClickUserIcon(status.user);
            }
        });
        Picasso.with(mContext).load(status.user.getBiggerProfileImageURL()).into(holder.mReTweeter_icon);
        holder.mReTweeter_name.setText(status.user.getName());

        if (status.isSentByLoginUser) {
            holder.mReTweetButtonMark.setBackgroundResource(R.drawable.retweet_mark_on);
        } else {
            holder.mReTweetButtonMark.setBackgroundResource(R.drawable.retweet_mark_off);
        }
    }



    private void setUserNameToHolder(final TweetEntity status, ViewHolder holder) {
        String userName = String.valueOf(status.user.getName()) + "@" + status.user.getScreenName();
        holder.mUserName.setText(userName);

        if (status.user.isProtected()) {
            holder.mLockIcon.setVisibility(View.VISIBLE);
        } else {
            holder.mLockIcon.setVisibility(View.INVISIBLE);
        }
    }


    private void setStatusBarColor(final TweetEntity status, ViewHolder holder) {
        if (status.isDeleted()) {
            holder.mStatusBar.setDeletedColor(mContext);
            return;
        }

        if (status.isSentToLoginUser) {
            holder.mStatusBar.setReplyToMeColor(mContext);
            return;
        }

        if (status.isSentByLoginUser) {
            holder.mStatusBar.setMyTweetColor(mContext);
            return;
        }

        holder.mStatusBar.setVisibility(View.INVISIBLE);
    }

    private void setPictureToViewHolder(final TweetEntity status, ViewHolderWithPicture holder) {

        List<String> URLs = createMediaURLList(status);

        final TweetListAdapter.PictureClickListener listener = new TweetListAdapter.PictureClickListener(status);

        int count;

        for (count = 0; count < URLs.size(); count++) {
            holder.mPictures[count].show(URLs.get(count), listener);
            Picasso.with(mContext).load(URLs.get(count)).resize(45, 45).centerInside().into(holder.mPictures[count]);
        }

        for (; count < 4; count++) {
            holder.mPictures[count].hide();
        }
    }

    private View inflateTweetLayoutWithPicture() {
        return mLayoutInflater.inflate(R.layout.tweet_layout_with_picture, null);
    }


    private View createLoadButtonView() {
        return this.mLayoutInflater.inflate(R.layout.read_more_tweet, null);
    }


    public int getLoadButtonPosition(long buttonID) {
        for( int i = 0; i < getCount(); i++ ) {
            if (getItem(i).getId() == buttonID) {
                return i;
            }
        }

        return -1;
    }



    public boolean containsUnreadItem(int first, int last) {
        for (int i = first; i < last; i++) {
            if (!getItem(i).isSeenByUser()) {
                return true;
            }
        }

        return false;
    }


    public boolean remove(long itemId) {
        for (int i = 0; i < getCount(); i++) {
            TweetEntity entity = getItem(i);
            if (getItem(i).getId() == itemId) {
                remove(entity);
                return true;
            }
        }

        return false;
    }


    public long getItemIdAtPosition(int position) {
        return getItem(position).getId();
    }


    public long lastReadId() {
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).isSeenByUser()) {
                return getItem(i).getId();
            }
        }

        return 0L;
    }

}
