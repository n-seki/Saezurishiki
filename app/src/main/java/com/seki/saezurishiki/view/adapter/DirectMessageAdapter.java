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
import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.GetDirectMessageById;
import com.seki.saezurishiki.model.impl.ModelContainer;
import com.seki.saezurishiki.view.adapter.viewholder.DirectMessageViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.seki.saezurishiki.control.UIControlUtil.formatDate;

public class DirectMessageAdapter extends ArrayAdapter<ListElement> {

    private final LayoutInflater mInflater;
    private boolean changeBackground = false;

    private final GetDirectMessageById repositoryAccessor = ModelContainer.getDirectMessageById();

    public DirectMessageAdapter(Context context, int resourceId) {
        super(context, resourceId);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setBackgroundColor() {
        changeBackground = true;
    }


    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        final ListElement item = getItem(position);
        final DirectMessageEntity directMessage = this.repositoryAccessor.get(item.id);

        DirectMessageViewHolder holder;

        if ( convertView == null ) {
            convertView = mInflater.inflate(R.layout.direct_message_layout, null);
            holder = new DirectMessageViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (DirectMessageViewHolder)convertView.getTag();
        }

        final UserEntity sender = directMessage.sender;

        Picasso.with(getContext()).load(sender.getBiggerProfileImageURL()).into(holder.icon);

        String userName = sender.getScreenName() + " / " + String.valueOf(sender.getName());
        holder.userName.setText(userName);

        holder.sendText.setText(directMessage.text);

        String sendTime = formatDate(directMessage.createAt);
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


    @Override
    @NonNull
    public ListElement getItem(int position) {
        final ListElement item = super.getItem(position);
        if (item == null) {
            throw new NullPointerException("item is null, position :" + position);
        }

        return item;
    }

    public void addAll(List<? extends DirectMessageEntity> allMessage) {
        for(DirectMessageEntity message : allMessage) {
            final ListElement item = new ListElement(message.getId(), false);
            add(item);
        }
    }

    public void add(DirectMessageEntity message) {
        final ListElement item = new ListElement(message.getId(), false);
        add(item);
    }

    public void updateIfSameUserMessage(DirectMessageEntity message) {
        for (int position = 0; position < getCount(); position++) {
            final ListElement item = getItem(position);
            final DirectMessageEntity current = this.repositoryAccessor.get(item.id);
            if (current.sender.getId() == message.sender.getId()) {
                item.changeItem(message.getId());
                item.see();
                notifyDataSetChanged();
                return;
            }
        }

        insert(new ListElement(message.getId(), false), 0);
    }

    public void updateIfSameUserMessage(List<DirectMessageEntity> messageList) {
        Collections.sort(messageList);
        final List<DirectMessageEntity> distinctUserList = new ArrayList<>();
        for (DirectMessageEntity message : messageList) {
            boolean hasSameUserMessage = false;
            for (DirectMessageEntity tmp : distinctUserList) {
                if (message.sender.getId() == tmp.sender.getId()) {
                    hasSameUserMessage = true;
                    break;
                }
            }

            if (!hasSameUserMessage)
                distinctUserList.add(message);
        }

        for (DirectMessageEntity dm : distinctUserList) {
            this.add(dm);
        }
    }


    public void add(long id) {
        final ListElement item = new ListElement(id, true);
        add(item);
    }

    public long getLatestItemId() {
        if (isEmpty()) {
            return -1;
        }

        return getItem(0).id;
    }

    public boolean remove(long messageId) {
        for (int i = 0; i < getCount(); i++) {
            final ListElement item = getItem(i);
            if (item.id == messageId) {
                return remove(i);
            }
        }

        return false;
    }

    public boolean containsUnreadItem() {
        for (int i = 0; i < getCount(); i++) {
            final ListElement item = getItem(i);
            if (!item.isSeen) {
                return true;
            }
        }

        return false;
    }

    public long lastReadId() {
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).isSeen) {
                return getItem(i).id;
            }
        }

        return 0L;
    }
}


