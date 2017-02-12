package com.seki.saezurishiki.view.control;

/**
 * Created by seki on 2016/10/01.
 */
public interface TabManagedView {
    int tabPosition();
//    void notifySelectedTabChange();
    RequestTabState getRequestTabState();
}
