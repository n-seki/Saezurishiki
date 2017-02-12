package com.seki.saezurishiki.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seki.saezurishiki.BuildConfig;
import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.UIControlUtil;

import org.jetbrains.annotations.Contract;

public class LicenseFragment extends Fragment {


    @Contract(" -> !null")
    public static Fragment newInstance() {
        return new LicenseFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_license, container, false);

        TextView version = (TextView)v.findViewById(R.id.current_version);
        version.setText(getActivity().getString(R.string.version) + " " + BuildConfig.VERSION_NAME);

        v.setBackgroundColor(UIControlUtil.backgroundColor(getActivity()));

        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }


    @Override
    public String toString() {
        return "囀り式について";
    }
}
