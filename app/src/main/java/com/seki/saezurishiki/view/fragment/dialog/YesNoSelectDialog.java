package com.seki.saezurishiki.view.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.view.fragment.list.TweetListFragment;

import java.io.Serializable;

public class YesNoSelectDialog<T extends Serializable> extends DialogFragment {

    private static final String ITEM_KEY = "item_key";
    private static final String TITLE_KEY = "title_key";
    private static final String SUMMARY_KEY = "summary_key";
    private static final String POSITIVE_ACTION_KEY = "positive_action_key";
    private static final String NEGATIVE_ACTION_KEY = "negative_action_key";

    public interface Listener<T extends Serializable> extends Serializable {
        void onItemClick(T item);
    }


    public static class Builder<T extends Serializable> {
        private T item;
        private int title;
        private String summary;
        private Listener<T> positiveAction;
        private Listener<T> negativeAction;

        public Builder(){}

        public Builder setItem(T item) {
            this.item = item;
            return this;
        }

        public Builder setTitle(@StringRes int title) {
            this.title = title;
            return this;
        }

        public Builder setSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder setPositiveAction(Listener<T> listener) {
            this.positiveAction = listener;
            return this;
        }

        public Builder setNegativeAction(Listener<T> listener) {
            this.negativeAction = listener;
            return this;
        }

        public DialogFragment build() {
            return YesNoSelectDialog.create(this);
        }
    }


    static DialogFragment create(Builder builder) {
        Bundle data = new Bundle();
        data.putSerializable(ITEM_KEY, builder.item);
        data.putInt(TITLE_KEY, builder.title);
        data.putString(SUMMARY_KEY, builder.summary);
        data.putSerializable(POSITIVE_ACTION_KEY, builder.positiveAction);
        data.putSerializable(NEGATIVE_ACTION_KEY, builder.negativeAction);

        DialogFragment fragment = new YesNoSelectDialog<>();
        fragment.setArguments(data);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle data = getArguments();
        final T item = (T)data.getSerializable(ITEM_KEY);
        final int title = data.getInt(TITLE_KEY);
        final String summary = data.getString(SUMMARY_KEY);
        final Listener<T> positiveAction = (Listener<T>) data.getSerializable(POSITIVE_ACTION_KEY);
        final Listener<T> negativeAction = (Listener<T>) data.getSerializable(NEGATIVE_ACTION_KEY);

        final AlertDialog.Builder dialogBuiler = new AlertDialog.Builder(getActivity());
        if (title != 0) {
            dialogBuiler.setTitle(title);
        }

        dialogBuiler.setMessage(summary)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        positiveAction.onItemClick(item);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        negativeAction.onItemClick(item);
                        dismiss();
                    }
                });

        return dialogBuiler.create();
    }


    @SuppressWarnings("unchecked")
    public static DialogFragment newFavoriteDialog(final TweetEntity tweet, Listener<TweetEntity> positiveAction) {
        return new YesNoSelectDialog.Builder<TweetEntity>()
                .setItem(tweet)
                .setTitle(tweet.isFavorited ? R.string.do_you_un_favorite : R.string.do_you_favorite)
                .setSummary(tweet.user.getName() + "\n" + tweet.text)
                .setPositiveAction(positiveAction)
                .setNegativeAction(new YesNoSelectDialog.Listener<TweetEntity>() {
                    @Override
                    public void onItemClick(TweetEntity item) {
                        //do nothing
                    }
                })
                .build();
    }

    @SuppressWarnings("unchecked")
    public static DialogFragment newRetweetDialog(final TweetEntity tweet, Listener<TweetEntity> action) {
        return new YesNoSelectDialog.Builder<TweetEntity>()
                .setItem(tweet)
                .setTitle(R.string.do_you_retweet)
                .setSummary(tweet.user.getName() + "\n" + tweet.text)
                .setPositiveAction(action)
                .setNegativeAction(new YesNoSelectDialog.Listener<TweetEntity>() {
                    @Override
                    public void onItemClick(TweetEntity item) {
                        //do nothing
                    }
                })
                .build();
    }


}
