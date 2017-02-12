package com.seki.saezurishiki.entity;

import com.seki.saezurishiki.R;

import java.util.Date;


public class LoadButton implements TwitterEntity {

    public final Date createdAt;
    private final long id;

    private int labelResId = R.string.click_to_load;

    public LoadButton() {
        createdAt = new Date();
        id = -createdAt.hashCode();
    }

    public int getLabelResId() {
        return labelResId;
    }

    public void setLabelResId(int resId) {
        this.labelResId = resId;
    }

    @Override
    public Type getItemType() {
        return Type.LoadButton;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public boolean isSeenByUser() {
        return false;
    }

    @Override
    public void userSee() {}

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if ( !(obj instanceof LoadButton) ) {
            return false;
        }

        LoadButton lb = (LoadButton)obj;

        return lb.getId() == id && lb.createdAt.equals(createdAt);
    }
}
