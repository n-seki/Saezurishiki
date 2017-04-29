package com.seki.saezurishiki.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.Setting;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.view.adapter.viewholder.ViewHolder;
import com.seki.saezurishiki.view.adapter.viewholder.ViewHolderWithPicture;
import com.seki.saezurishiki.entity.LoadButton;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.TwitterEntity;
import com.seki.saezurishiki.network.server.TwitterServer;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.view.fragment.list.SearchFragment;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import twitter4j.Status;
import twitter4j.User;

import static com.seki.saezurishiki.control.UIControlUtil.formatDate;
import static com.seki.saezurishiki.control.UIControlUtil.createMediaURLList;

/**
 * BaseAdapterでいいのなら後で変更する
 */
public class TimeLineAdapter extends ArrayAdapter<AdapterItem> {

    //getView()内でnewする良い方法を思いつけないので、
    //とりあえずフィールドとして保持
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private ViewListener mListener;

    private boolean backgroundChange = false;

    private TwitterServer repository;
    private final int TEXT_SIZE;
    private final boolean SHOW_THUMBNAIL;
    private final Setting.ButtonActionPattern FAVORITE_BUTTON_ACTION;
    private final Setting.ButtonActionPattern RETWEET_BUTTON_ACTION;

    public boolean hasUnreadItem() {
        for (int i = 0; i < getCount(); i++) {
            AdapterItem item = getItem(i);
            if (!item.isSeen) {
                return true;
            }
        }

        return false;
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
                mListener.onClickPicture((String)(v.getTag()), mStatus);
            }
        }
    }

    public TimeLineAdapter(Context context, int resourceId, ViewListener listener) {
        super(context, resourceId);
        TEXT_SIZE = 0;
        SHOW_THUMBNAIL = false;
        FAVORITE_BUTTON_ACTION = Setting.ButtonActionPattern.ONE_TAP;
        RETWEET_BUTTON_ACTION = Setting.ButtonActionPattern.ONE_TAP;
    }

    public TimeLineAdapter(Context context, int resourceId, ViewListener listener, TwitterAccount account) {
        super(context, resourceId);

        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mListener = listener;
        this.repository = account.getRepository();
        TEXT_SIZE = account.setting.getTextSize();
        SHOW_THUMBNAIL = account.setting.isShowThumbnail();
        FAVORITE_BUTTON_ACTION = account.setting.getFavoriteButtonAction();
        RETWEET_BUTTON_ACTION = account.setting.getReTweetButtonAction();
    }


    public void setBackgroundChange() {
        this.backgroundChange = true;
    }


    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final TwitterEntity entity = repository.getTwitterEntity(getItemIdAtPosition(position));

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

        if (backgroundChange) {
            if (getItem(position).isSeen) {
                view.setBackgroundColor(UIControlUtil.backgroundColor(mContext));
            } else {
                view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_889FD9F6));
            }
        }

        return  view;
    }


    private View setStatusInformationToView(final TweetEntity status, View convertView) {
        ViewHolderWithPicture holder;

        if (convertView  == null || !(convertView.getTag() instanceof ViewHolderWithPicture)) {
            convertView = this.inflateTweetLayoutWithPicture();
            holder = new ViewHolderWithPicture(convertView);
            //Statusによって変化しないのはここでsetしておく
            holder.replyButtonArea.setOnClickListener(onReplyButtonClickListener);

            if (RETWEET_BUTTON_ACTION == Setting.ButtonActionPattern.LONG) {
                holder.reTweetButtonArea.setOnLongClickListener(onReTweetButtonLongClickListener);
            } else {
                holder.reTweetButtonArea.setOnClickListener(onReTweetButtonClickListener);
            }

            if (FAVORITE_BUTTON_ACTION == Setting.ButtonActionPattern.LONG) {
                holder.favoriteButtonArea.setOnLongClickListener(onFavoriteButtonLongClickListener);
            } else {
                holder.favoriteButtonArea.setOnClickListener(onFavoriteButtonClickListener);
            }
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
        holder.mPostTime.setTextSize(TEXT_SIZE - 2);
        holder.mTweetText.setText(status.text);
        holder.mTweetText.setTextSize(TEXT_SIZE);
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
            holder.mReTweetCount.setTextSize(TEXT_SIZE - 2);
        } else {
            holder.mReTweetCount.setVisibility(View.GONE);
        }

        if (status.favoriteCount != 0) {
            holder.mFavoriteCount.setText(String.valueOf(status.favoriteCount));
            holder.mFavoriteCount.setVisibility(View.VISIBLE);
            holder.mFavoriteCount.setTextSize(TEXT_SIZE - 2);
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
            holder.quotedUserName.setTextSize(TEXT_SIZE - 2);
            holder.quotedTweetText.setTextSize(TEXT_SIZE - 2);
            holder.quotedTweetText.setText(quotedStatus.text);
            holder.quotedTweetText.setTextSize(TEXT_SIZE);
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
            mListener.onClickReplyButton(status);
        }
    };

    private final View.OnLongClickListener onReTweetButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            //no check
            TweetEntity status = (TweetEntity) view.getTag();
            mListener.onClickReTweetButton(status, RETWEET_BUTTON_ACTION == Setting.ButtonActionPattern.TAP_AND_DIALOG);
            return true;
        }
    };

    private final View.OnClickListener onReTweetButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //no check
            TweetEntity status = (TweetEntity) view.getTag();
            mListener.onClickReTweetButton(status, RETWEET_BUTTON_ACTION == Setting.ButtonActionPattern.TAP_AND_DIALOG);
        }
    };

    private final View.OnLongClickListener onFavoriteButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            TweetEntity status = (TweetEntity) view.getTag();
            mListener.onClickFavoriteButton(status, FAVORITE_BUTTON_ACTION == Setting.ButtonActionPattern.TAP_AND_DIALOG);
            return true;
        }
    };

    private final View.OnClickListener onFavoriteButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TweetEntity status = (TweetEntity) view.getTag();
            mListener.onClickFavoriteButton(status, FAVORITE_BUTTON_ACTION == Setting.ButtonActionPattern.TAP_AND_DIALOG);
        }
    };


    private final View.OnClickListener onUserIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            view.setEnabled(false);
            User user = (User)view.getTag();
            mListener.onClickUserIcon(user);
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
            mListener.onClickQuotedTweet(status);
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

        holder.retweetText.setTextSize(TEXT_SIZE - 2);

        String postTime = formatDate(reTweet.createdAt);
        holder.mPostTime.setText(postTime);
        holder.mPostTime.setTextSize(TEXT_SIZE - 2);

        holder.mStatusBar.setReTweetColor(mContext);

        holder.mReTweeter_info.setVisibility(View.VISIBLE);
        holder.mReTweeter_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickUserIcon(status.user);
            }
        });
        Picasso.with(mContext).load(status.user.getBiggerProfileImageURL()).into(holder.mReTweeter_icon);
        holder.mReTweeter_name.setText(status.user.getName());
        holder.mReTweeter_name.setTextSize(TEXT_SIZE - 2);

        if (status.isSentByLoginUser) {
            holder.mReTweetButtonMark.setBackgroundResource(R.drawable.retweet_mark_on);
        } else {
            holder.mReTweetButtonMark.setBackgroundResource(R.drawable.retweet_mark_off);
        }
    }



    private void setUserNameToHolder(final TweetEntity status, ViewHolder holder) {
        String userName = String.valueOf(status.user.getName()) + "@" + status.user.getScreenName();
        holder.mUserName.setText(userName);
        holder.mUserName.setTextSize(TEXT_SIZE - 2);

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

        if (!SHOW_THUMBNAIL) return;

        List<String> URLs = createMediaURLList(status);

        final PictureClickListener listener = new PictureClickListener(status);

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
           if (getItem(i).itemID == buttonID) {
               return i;
           }
        }

        return -1;
    }

    public void addAll(List<TweetEntity> tweets) {
        Collections.sort(tweets);

        if (this.isEmpty()) {
            addAll(tweets, 0);
            return;
        }

        final long oldestTweetID = tweets.get(tweets.size() - 1).getId();

        if (oldestTweetID >= getItemIdAtPosition(0)) {
            addAll(tweets, 0);
            return;
        }

        final long latestTweetID = tweets.get(0).getId();

        if (latestTweetID <= getItemIdAtPosition(getCount() - 1)) {
            addAll(tweets, getCount());
            return;
        }

        for (int position = 0; position < getCount(); position++) {
            final long tweetID = getItemIdAtPosition(position);
            if (latestTweetID <= tweetID) {
                addAll(tweets, position);
                return;
            }
        }
    }

    private void addAll(List<TweetEntity> tweets, int position) {
        int insertPosition = position;

        for (TweetEntity tweetEntity : tweets) {
            insert(tweetEntity.getId(), insertPosition++);
        }
    }

    public void add(Status status) {
        add(new AdapterItem(status.getId()));
    }

    public void add(long tweetId) {
        add(new AdapterItem(tweetId));
    }

    public void addSeenItem(Status status) {
        add(new AdapterItem(status.getId(), true));
    }


    public boolean containsUnreadItem(int first, int last) {
        for (int i = first; i < last; i++) {
            AdapterItem item = getItem(i);
            if (!item.isSeen) {
                return true;
            }
        }

        return false;
    }


    public boolean remove(long itemId) {
        for (int i = 0; i < getCount(); i++) {
            AdapterItem item = getItem(i);
            if (item.itemID == itemId) {
                remove(item);
                return true;
            }
        }

        return false;
    }


    public void insertSeenItem(Status status, int position) {
        insert(new AdapterItem(status.getId(), true), position);
    }


    public void insert(long statusId, int position) {
        AdapterItem item = new AdapterItem(statusId);
        insert(item, position);
    }


    public long getItemIdAtPosition(int position) {
        return getItem(position).itemID;
    }


    public long lastReadId() {
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).isSeen) {
                return getItem(i).itemID;
            }
        }

        return 0L;
    }


    public void insertButton(long id, int position) {
        AdapterItem button = AdapterItem.newButton(id);
        insert(button, position);
    }


    public boolean setStatusIntoList(List<Status> list, long buttonID, int maxSize) {
        if (list.isEmpty()) {
            return true;
        }

        int buttonPosition = getLoadButtonPosition(buttonID);

        for (Status status : list) {
            insert(status.getId(), buttonPosition++);
        }

        return list.size() < maxSize;

    }


}