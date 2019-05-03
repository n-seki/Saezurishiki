package com.seki.saezurishiki.view.fragment.other;

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
import org.jetbrains.annotations.NotNull;

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
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_license, container, false);

        TextView version = v.findViewById(R.id.current_version);
        String versionText = getString(R.string.version, BuildConfig.VERSION_NAME);
        version.setText(versionText);

        TextView releaseDate = v.findViewById(R.id.release_date);
        String releaseDateText = getString(R.string.last_update_date, BuildConfig.BUILD_DATE);
        releaseDate.setText(releaseDateText);

        v.setBackgroundColor(UIControlUtil.backgroundColor(container.getContext()));
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
