package com.seki.saezurishiki.view.adapter;

public class ListElement {
    public long id;
    final boolean isSeen;

    ListElement(long id, boolean isSeen) {
        this.id = id;
        this.isSeen = true;
    }

    public void see() {
        //this.isSeen = true;
    }

    public boolean isSeen() {
        return this.isSeen;
    }

    void changeItem(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ListElement)) {
            return false;
        }

        final ListElement le = (ListElement)o;
        return this.id == le.id;
    }
}