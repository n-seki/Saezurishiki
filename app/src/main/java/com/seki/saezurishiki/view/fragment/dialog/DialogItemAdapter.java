package com.seki.saezurishiki.view.fragment.dialog;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;

class DialogItemAdapter extends ArrayAdapter<DialogItemAdapter.DialogItem> {

    final private LayoutInflater layoutInflater;

    DialogItemAdapter(Context context, LayoutInflater inflater, int resourceId) {
        super(context, resourceId);
        this.layoutInflater = inflater;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final DialogItem item = getItem(position);
        convertView = layoutInflater.inflate(R.layout.dialog_list_item, null, false);
        convertView.setTag(item.action);
        TextView textView = (TextView) convertView.findViewById(R.id.action_item_text);
        textView.setText(item.text);
        View icon = convertView.findViewById(R.id.item_icon);
        icon.setBackgroundResource(item.icon);

        return convertView;
    }


     static class DialogItem {
         final DialogSelectAction action;
         final String text;
         final int icon;

         public DialogItem(DialogSelectAction action, String text, int icon) {
             this.action = action;
             this.text = text;
             this.icon = icon;
         }
    }
}

