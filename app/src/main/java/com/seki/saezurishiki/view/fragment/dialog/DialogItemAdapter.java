package com.seki.saezurishiki.view.fragment.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seki.saezurishiki.R;

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
        final int action = item.actionID;
        convertView = layoutInflater.inflate(R.layout.dialog_list_item, null, false);
        convertView.setTag(action);
        TextView textView = (TextView) convertView.findViewById(R.id.action_item_text);
        View icon = convertView.findViewById(R.id.item_icon);

        if (item.hasOtherText()) {
            textView.setText(item.otherText);
        } else  {
            textView.setText(action);
        }

        icon.setBackgroundResource(item.icon);

        return convertView;
    }


     class DialogItem {
        final int actionID;
        final String otherText;
        final int icon;

        private DialogItem(int stringID, String otherText, int icon) {
            this.actionID = stringID;
            this.otherText = otherText;
            this.icon = icon;
        }

        private DialogItem(int stringID, int icon) {
            this.actionID = stringID;
            this.otherText = null;
            this.icon = icon;
        }

        private boolean hasOtherText() {
            return this.otherText != null;
        }
    }


    public void add(int action, int icon) {
        DialogItem item = new DialogItem(action, icon);
        add(item);
    }


    public void add(int action, String otherText, int icon) {
        DialogItem item = new DialogItem(action, otherText, icon);
        add(item);
    }
}

