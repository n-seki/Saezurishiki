package com.seki.saezurishiki.view.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.control.Setting;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.view.fragment.util.DataType;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;

public class TweetLongClickDialog extends DialogFragment {

    private TweetEntity mStatus;
    private LongClickDialogListener mListener;

    private int theme;

    public interface LongClickDialogListener {
        void onLongClickDialogItemSelect(DialogSelectAction<TweetEntity> action);
    }

    public static DialogFragment newInstance(TweetEntity status) {
        Bundle data = new Bundle();
        data.putSerializable(DataType.STATUS, status);
        DialogFragment dialog = new TweetLongClickDialog();
        dialog.setArguments(data);
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStatus = (TweetEntity) getArguments().getSerializable(DataType.STATUS);

        this.theme = new Setting().getTheme();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Fragment targetFragment = getTargetFragment();
        mListener = (LongClickDialogListener)targetFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_list_view);
        this.initDialog(dialog);
        return dialog;
    }


    public void initDialog(Dialog dialog) {
        ListView listView = (ListView) dialog.findViewById(R.id.list);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            @SuppressWarnings("unchecked")
            final DialogSelectAction<TweetEntity> action = (DialogSelectAction<TweetEntity>)view.getTag();
            mListener.onLongClickDialogItemSelect(action);
            dismiss();
        });

        DialogItemAdapter adapter =
                new DialogItemAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.dialog_list_item);
        listView.setAdapter(adapter);

        final boolean isThemeDark = this.theme == R.style.AppTheme_Dark;

        if (mStatus.isSentByLoginUser) {
            final int icon = isThemeDark ? R.drawable.delete_white : R.drawable.delete_black;
            final DialogSelectAction action = DialogSelectAction.delete(mStatus);
            adapter.add(new DialogItemAdapter.DialogItem(action, getString(R.string.do_delete), icon));
        }

        if (!mStatus.user.isProtected()) {
            final int icon = isThemeDark ? R.drawable.re_tweet_white : R.drawable.re_tweet_black;
            if (mStatus.isRetweetedbyLoginUser) {
                final DialogSelectAction action = DialogSelectAction.unRetweet(mStatus);
                adapter.add(new DialogItemAdapter.DialogItem(action, getString(R.string.do_un_retweet), icon));
            } else {
                final DialogSelectAction action = DialogSelectAction.retweet(mStatus);
                adapter.add(new DialogItemAdapter.DialogItem(action, getString(R.string.do_retweet), icon));
            }
        }

        final int favIcon = isThemeDark ? R.drawable.drawer_favorite_dark : R.drawable.drawer_favorite_light;
        if (mStatus.isFavorited) {
            final DialogSelectAction action = DialogSelectAction.unFavorite(mStatus);
            adapter.add(new DialogItemAdapter.DialogItem(action, getString(R.string.do_un_favorite), favIcon));
        } else {
            final DialogSelectAction action = DialogSelectAction.favorite(mStatus);
            adapter.add(new DialogItemAdapter.DialogItem(action, getString(R.string.do_favorite), favIcon));
        }
    }


}
