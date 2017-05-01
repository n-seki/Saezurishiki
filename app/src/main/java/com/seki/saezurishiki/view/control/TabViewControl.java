package com.seki.saezurishiki.view.control;

public interface TabViewControl {
    //void registTabManagedView(TabManagedView view);
    boolean isCurrentSelect(TabManagedView view);
    void requestChangeTabState(TabManagedView view);
}
