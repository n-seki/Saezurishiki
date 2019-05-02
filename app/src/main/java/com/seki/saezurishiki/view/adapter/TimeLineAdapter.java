package com.seki.saezurishiki.view.adapter;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.Setting;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.databinding.TweetLayoutWithPictureBinding;
import com.seki.saezurishiki.entity.LoadButton;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.TwitterEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.GetTweetById;
import com.seki.saezurishiki.model.impl.ModelContainer;
import com.seki.saezurishiki.view.customview.TweetStatusBar;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TimeLineAdapter extends ArrayAdapter<ListElement> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ViewListener mListener;
    private GetTweetById repositoryAccessor;
    private Setting setting = new Setting();
    private boolean backgroundChange = false;
    private Map<Long, LoadButton> buttons;

    public interface ViewListener {
        void onClickPicture(int position, TweetEntity status);
        void onClickUserIcon(View view, UserEntity user);
        void onClickReplyButton(TweetEntity status);
        void onClickReTweetButton(final TweetEntity status, Setting.ButtonActionPattern actionPattern);
        boolean onLongClickReTweetButton(final TweetEntity tweet, Setting.ButtonActionPattern actionPattern);
        void onClickFavoriteButton(final TweetEntity status, Setting.ButtonActionPattern actionPattern);
        boolean onLongClickFavoriteButton(final TweetEntity tweet, Setting.ButtonActionPattern actionPattern);
        void onClickQuotedTweet(final TweetEntity status);
    }

    public TimeLineAdapter(Context context, int resourceId, ViewListener listener) {
        super(context, resourceId);

        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mListener = listener;

        this.repositoryAccessor = ModelContainer.getRepositoryAccessor();

        this.buttons = new HashMap<>();
    }


    public void setBackgroundChange() {
        this.backgroundChange = true;
    }


    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final ListElement element = getItem(position);

        if (this.buttons.containsKey(element.id)) {
            View view = createLoadButtonView();
            TextView text = view.findViewById(R.id.read_more);
            text.setText((buttons.get(element.id)).getLabelResId());
            return view;
        }

        final TweetEntity tweet = this.repositoryAccessor.get(element.id);

        TweetLayoutWithPictureBinding binding;

        if (convertView  == null || !(convertView.getTag() instanceof TweetLayoutWithPictureBinding)) {
            binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.tweet_layout_with_picture, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
           binding = (TweetLayoutWithPictureBinding) convertView.getTag();
        }

        // FIXME: 2017/06/04
        binding.quotedStatusLayout.setVisibility(View.GONE);
        binding.reTweeter.setVisibility(View.GONE);
        binding.lockIcon.setVisibility(View.GONE);

        binding.setListener(mListener);
        binding.setSetting(setting);

        if (tweet.isRetweet) {
            binding.setReTweeter(tweet.user);
            binding.setTweet(tweet.retweet);
        } else {
            binding.setTweet(tweet);
            binding.setReTweeter(null);
        }

        binding.executePendingBindings();

        if (backgroundChange) {
            if (element.isSeen) {
                convertView.setBackgroundColor(UIControlUtil.backgroundColor(mContext));
            } else {
                convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_889FD9F6));
            }
        }

        return binding.getRoot();
    }

    private View createLoadButtonView() {
        return this.mLayoutInflater.inflate(R.layout.read_more_tweet, null);
    }


    public int getLoadButtonPosition(long buttonID) {
        final LoadButton button = this.buttons.get(buttonID);
        if (button == null) {
            throw new IllegalStateException("button is not registered, id : " + buttonID);
        }

        return getPosition(new ListElement(buttonID, false));
    }

    @NonNull
    @Override
    public ListElement getItem(int position) {
        final ListElement element = super.getItem(position);
        if (element == null) {
            throw new IllegalStateException("item is null, position :" + position);
        }
        return element;
    }


    public TwitterEntity getEntity(int position) {
        final long id = getItem(position).id;
        if (this.buttons.containsKey(id)) {
            return buttons.get(id);
        }

        return this.repositoryAccessor.get(id);
    }

    public LoadButton getButton(long id) {
        if (this.buttons.containsKey(id)) {
            return buttons.get(id);
        }

        throw new IllegalStateException("button is not exist, id : " + id);
    }

    public void add(TweetEntity tweet) {
        final ListElement newElement = new ListElement(tweet.getId(), false);
        add(newElement);
    }

    public void insert(TweetEntity tweet, int position) {
        final ListElement newElement = new ListElement(tweet.getId(), false);
        insert(newElement, position);
    }

    synchronized public void addAll(List<TweetEntity> tweets) {
        if (tweets == null || tweets.isEmpty()) {
            return;
        }

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

        for (TweetEntity tweet : tweets) {
            final ListElement newElement = new ListElement(tweet.getId(), false);
            insert(newElement, insertPosition++);
        }
    }


    public boolean containsUnreadItem(int first, int last) {
        for (int i = first; i < last; i++) {
            final ListElement listElement = getItem(i);
            if (!listElement.isSeen) {
                return true;
            }
        }

        return false;
    }

    public boolean hasUnreadItem() {
        return containsUnreadItem(0, getCount());
    }


    public boolean remove(long itemId) {
        for (int i = 0; i < getCount(); i++) {
            final ListElement listElement = getItem(i);
            if (listElement.id == itemId) {
                remove(listElement);
                return true;
            }
        }

        return false;
    }

    public void insertButton(int position) {
        final LoadButton button = new LoadButton();
        final ListElement buttonElement = new ListElement(button.getId(), false);
        buttons.put(button.getId(), button);
        insert(buttonElement, position);
    }


    public long getItemIdAtPosition(int position) {
        if (this.isEmpty()) return -1;
        final ListElement listElement = getItem(position);
        return listElement.id;
    }


    public long lastReadId() {
        for (int i = 0; i < getCount(); i++) {
            final ListElement listElement = getItem(i);
            if (listElement.isSeen) {
                return listElement.id;
            }
        }

        return 0L;
    }


    @BindingAdapter({"bind:imageUrl", "bind:imageSize"})
    public static void loadImage(ImageView view, String imageUrl, int size) {
        if (imageUrl == null) return;
        Picasso.with(view.getContext()).load(imageUrl).resize(size, size).centerInside().into(view);
    }


    @BindingAdapter("bind:tweet")
    public static void setStatusColor(TweetStatusBar view, TweetEntity tweet) {
        if (tweet.isDeleted()) {
            view.setDeletedColor(view.getContext());
            return;
        }

        if (tweet.isSentToLoginUser) {
            view.setReplyToMeColor(view.getContext());
            return;
        }

        if (tweet.isSentByLoginUser) {
            view.setMyTweetColor(view.getContext());
            return;
        }

        view.setVisibility(View.INVISIBLE);
    }

    @BindingAdapter("bind:textSize")
    public static void setTextSize(TextView textView, int rawSize) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, rawSize);
    }

}