package com.seki.saezurishiki.view.control;

import android.os.Bundle;

import com.seki.saezurishiki.control.ScreenNav;

public interface FragmentControl {
    void requestChangeScreen(ScreenNav screenNav, Bundle args);
}
