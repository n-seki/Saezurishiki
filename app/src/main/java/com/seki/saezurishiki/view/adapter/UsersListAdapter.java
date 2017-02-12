package com.seki.saezurishiki.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.view.adapter.viewholder.UserViewHolder;
import com.squareup.picasso.Picasso;

import twitter4j.User;

import static com.seki.saezurishiki.control.UIControlUtil.formatDate;

/**
 * User一覧表示用Adapter<br>
 * @author seki
 */
public class UsersListAdapter extends ArrayAdapter<User> {

    private LayoutInflater mLayoutInflater;


    public UsersListAdapter(Context context, int resourceId) {
        super(context, resourceId);

        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);        //mContext = context;
    }



    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {

        UserViewHolder holder;

        User user = this.getItem( position );

        if ( convertView == null ) {
            convertView = mLayoutInflater.inflate( R.layout.user_info_layout, null, false );
            holder = new UserViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (UserViewHolder)convertView.getTag();
        }


        Picasso.with(getContext()).load(user.getBiggerProfileImageURL()).into(holder.mUserIcon);

        final String concatName = user.getScreenName() + " / " + String.valueOf(user.getName());
        holder.mUserName.setText(concatName);

        holder.mBioText.setText(user.getDescription());

        final String date = "since : " + formatDate(user.getCreatedAt());
        holder.mSince.setText(date);

        return convertView;
    }
}
