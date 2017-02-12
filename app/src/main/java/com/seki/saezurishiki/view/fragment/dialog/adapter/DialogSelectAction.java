package com.seki.saezurishiki.view.fragment.dialog.adapter;


public class DialogSelectAction {

    public final Object item;
    public final Class<?> clazz;
    public final int action;

    public DialogSelectAction(Object item, Class<?> clazz, int action) {
        this.item = item;
        this.clazz = clazz;
        this.action = action;
    }
}
