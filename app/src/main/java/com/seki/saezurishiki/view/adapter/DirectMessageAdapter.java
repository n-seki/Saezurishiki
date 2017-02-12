package com.seki.saezurishiki.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.view.adapter.viewholder.DirectMessageViewHolder;
import com.seki.saezurishiki.network.server.TwitterServer;
import com.squareup.picasso.Picasso;

import java.util.List;

import twitter4j.DirectMessage;

import static com.seki.saezurishiki.control.UIControlUtil.formatDate;

/**
 * ダイレクトメッセージ一覧表示用Adapter<br>
 * @author seki
 */
public class DirectMessageAdapter extends ArrayAdapter<AdapterItem> {

    private final LayoutInflater mInflater;
    private boolean changeBackground = false;
    private final TwitterServer repository;

    public DirectMessageAdapter(Context context, int resourceId, TwitterServer repository) {
        super(context, resourceId);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.repository =repository;
    }

    public void setBackgroundColor() {
        changeBackground = true;
    }


    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        final AdapterItem item = getItem(position);
        final DirectMessage directMessage = repository.findDM(getItemId(position));

        DirectMessageViewHolder holder;

        if ( convertView == null ) {
            convertView = mInflater.inflate(R.layout.direct_message_layout, null);
            holder = new DirectMessageViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (DirectMessageViewHolder)convertView.getTag();
        }

        Picasso.with(getContext()).load(directMessage.getSender().getBiggerProfileImageURL()).into(holder.icon);

        String userName = directMessage.getSender().getScreenName() + " / " + String.valueOf(directMessage.getSender().getName());
        holder.userName.setText(userName);

        holder.sendText.setText(directMessage.getText());

        String sendTime = formatDate(directMessage.getCreatedAt());
        holder.sendTime.setText(sendTime);

        if (changeBackground) {
            if (item.isSeen) {
                convertView.setBackgroundColor(UIControlUtil.backgroundColor(getContext()));
            } else {
                convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_889FD9F6));
            }
        }

         return convertView;
    }


    public void addAll(List<Long> allMessage) {
        for(long id : allMessage) {
            AdapterItem item = new AdapterItem(id);
            add(item);
        }
    }


    public void add(DirectMessage message) {
        AdapterItem item = new AdapterItem(message.getId());
        add(item);
    }


    @Override
    public long getItemId(int position) {
        return getItem(position).itemID;
    }


    public void insert(long id, int position) {
        AdapterItem item = new AdapterItem(id);
        insert(item, position);
    }


    public void add(long messageId) {
        AdapterItem item = new AdapterItem(messageId);
        add(item);
    }

    public void addSeenItem(long messageId) {
        AdapterItem item = new AdapterItem(messageId, true);
        add(item);
    }


    public boolean remove(long messageId) {
        for (int i = 0; i < getCount(); i++) {
            AdapterItem item = getItem(i);
            if (item.itemID == messageId) {
                return remove(i);
            }
        }

        return false;
    }


    public boolean containsUnreadItem() {
        for (int i = 0; i < getCount(); i++) {
            AdapterItem item = getItem(i);
            if (!item.isSeen) {
                return true;
            }
        }

        return false;
    }


    public long lastReadId() {
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).isSeen) {
                return getItem(i).itemID;
            }
        }

        return 0L;
    }
}


