package com.seki.saezurishiki.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.CustomToast;
import com.seki.saezurishiki.control.UIControlUtil;

/**
 * 設定変更Fragment<br>
 * テーマなどの設定を変更する画面
 * @author seki
 */
public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_THEME_COLOR = "pref_theme_color";

    public static PreferenceFragmentCompat getInstance() {
        return new SettingFragment();
    }


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            view.setBackgroundColor(UIControlUtil.backgroundColor(getActivity()));
        }

        return view;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        //if (key.equals(KEY_PREF_THEME_COLOR)) {
            CustomToast.show(getActivity(), R.string.please_re_start_to_apply, Toast.LENGTH_SHORT);
        //n}
    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                             .registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                             .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public String toString() {
        return "Setting";
    }
}
