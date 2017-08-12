package com.seki.saezurishiki.view.control;

import com.seki.saezurishiki.control.ScreenNav;

import java.util.Map;

public interface FragmentControl {
    void requestChangeScreen(ScreenNav screenNav, Map<String, Object> args);
}
