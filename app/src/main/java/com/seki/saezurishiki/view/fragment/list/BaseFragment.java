package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.seki.saezurishiki.view.control.FragmentControl;

public abstract class BaseFragment extends Fragment {

    FragmentControl fragmentControl;

    abstract String getTitle();
    abstract String getSubTitle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        this.setupTitle();
    }

    @Override
    public  void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof FragmentControl) {
            this.fragmentControl = (FragmentControl)getActivity();
        } else {
            throw new IllegalStateException("Activity is not implements FragmentControl!");
        }
    }

    private void setupTitle() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar == null) {
            throw new IllegalStateException("Action bar is null!");
        }

        final String title = getTitle();
        final String subtitle = getSubTitle();

        actionBar.setTitle(title);
        actionBar.setSubtitle(subtitle);
    }


}
