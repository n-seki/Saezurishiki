package com.seki.saezurishiki.view.adapter;

public class AdapterItem {

    public long itemID;
    public boolean isSeen = false;
    private boolean isButton;

    AdapterItem(long itemID) {
        this.itemID = itemID;
        this.isButton = false;
    }

    AdapterItem(long itemId, boolean isSeen) {
        this(itemId);
        this.isSeen = isSeen;
    }

    public void see() {
        isSeen = true;
    }

    static AdapterItem newButton(long id) {
        AdapterItem item = new AdapterItem(id, false);
        item.isButton = true;
        return item;
    }

    public boolean isButton() {
        return this.isButton;
    }
}
