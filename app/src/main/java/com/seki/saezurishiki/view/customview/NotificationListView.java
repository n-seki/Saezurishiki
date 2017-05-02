package com.seki.saezurishiki.view.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.TwitterEntity;
import com.seki.saezurishiki.view.adapter.TimeLineAdapter;


public class NotificationListView extends ListView {

    public NotificationListView(Context context) {
        super(context);
    }

    public NotificationListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotificationListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NotificationListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public boolean changeItemBackground(int firstVisibleItem, int visibleItemCount) {
        boolean isChangedColor = false;
        for (int position = firstVisibleItem; position < firstVisibleItem + visibleItemCount - 1; position++) {
            @SuppressWarnings("unchecked")
            TimeLineAdapter.ListElement listElement = (TimeLineAdapter.ListElement) getItemAtPosition(position);

            if (listElement == null) {
                continue;
            }

            if (!listElement.isSeen()) {
                listElement.see();
                View v = getChildAt(position - firstVisibleItem);
                if (v == null) {
                    continue;
                }
                v.setBackgroundColor(UIControlUtil.backgroundColor(getContext()));
                isChangedColor = true;
            }
        }

        return isChangedColor;
    }
}
