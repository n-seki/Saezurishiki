package com.seki.saezurishiki.view.control;

import android.support.v4.app.Fragment;

/**
 * Created by seki on 2016/10/03.
 */
public interface FragmentControl {
    void onRemoveFragment(Fragment f);
    void requestShowUser(long userId);
    void requestShowFragment(Fragment fragment);
}
