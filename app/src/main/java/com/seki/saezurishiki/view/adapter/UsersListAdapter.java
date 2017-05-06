package com.seki.saezurishiki.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.GetUserById;
import com.seki.saezurishiki.model.impl.ModelContainer;
import com.seki.saezurishiki.view.adapter.viewholder.UserViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.seki.saezurishiki.control.UIControlUtil.formatDate;


public class UsersListAdapter extends ArrayAdapter<Long> {

    private LayoutInflater mLayoutInflater;
    private GetUserById repositoryAccessor = ModelContainer.getUserById();


    public UsersListAdapter(Context context, int resourceId) {
        super(context, resourceId);
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final long id = getItem(position);
        final UserEntity user = this.repositoryAccessor.get(id);

        UserViewHolder holder;

        if (convertView == null) {
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

    @Override
    @NonNull
    public Long getItem(int position) {
        final Object o = super.getItem(position);
        if (o == null) {
            throw new NullPointerException("item is null, position :" + position);
        }

        return Long.class.cast(o);
    }


    public UserEntity getEntity(int position) {
        final long id = getItem(position);
        return this.repositoryAccessor.get(id);
    }


    public void addAll(List<UserEntity> users) {
        for (UserEntity user : users) {
            this.add(user.getId());
        }
    }
}
