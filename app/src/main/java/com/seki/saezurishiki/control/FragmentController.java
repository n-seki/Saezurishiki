package com.seki.saezurishiki.control;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.seki.saezurishiki.R;

import org.jetbrains.annotations.Contract;

/**
 * Fragment管理クラス<br>
 * @author seki
 */
public final class FragmentController {


    public static final int FRAGMENT_ID_TWEET = 1;
    public static final int FRAGMENT_ID_FAVORITE = 2;

    private final FragmentManager mFragmentManager;

    public FragmentController(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }


    public void add(Fragment fragment, int containerViewId) {
        mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.activity_enter_in_anim, R.anim.activity_exit_out_anim, R.anim.activity_enter_in_anim, R.anim.activity_exit_out_anim)
                        .add(containerViewId, fragment)
                        .addToBackStack(null)
                        .commit();
    }

    public static void add(FragmentManager fragmentManager, Fragment fragment, int containerViewId) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.activity_enter_in_anim, R.anim.activity_exit_out_anim, R.anim.activity_enter_in_anim, R.anim.activity_exit_out_anim)
                .add(containerViewId, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Contract(pure = true)
    public boolean hasFragment() {
        return mFragmentManager.getBackStackEntryCount() != 0;
    }


    public Fragment getFragment(int viewId) {
        return mFragmentManager.findFragmentById(viewId);
    }

    public Fragment removeCurrentFragment(int viewId) {
        mFragmentManager.popBackStack(null, 0);
        mFragmentManager.executePendingTransactions();
        return mFragmentManager.findFragmentById(viewId);
    }

    public void removeAllFragment(int layout) {
        while(hasFragment()) {
            removeCurrentFragment(layout);
        }
    }
}
