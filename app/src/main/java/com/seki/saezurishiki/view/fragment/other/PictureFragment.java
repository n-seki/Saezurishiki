package com.seki.saezurishiki.view.fragment.other;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.view.customview.ZoomPicture;
import com.seki.saezurishiki.view.fragment.util.DataType;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 画像表示Fragment<br>
 * 選択された画像を全画面で表示します.
 * 1Statusに複数の画像が添付されている場合には左右scrollが可能です
 * @author seki
 */
public class PictureFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private TweetEntity mStatus;
    private int mTouchedPicturePosition;

    private int mPicCount;

    private ActionBar mActionBar;

    public static Fragment getInstance(int position, TweetEntity status) {
        Fragment fragment = new PictureFragment();
        Bundle data = new Bundle();
        data.putInt(DataType.PIC_POSITION, position);
        data.putSerializable(DataType.STATUS, status);
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getArguments();
        if (data == null) {
            throw new IllegalStateException("Argument is null");
        }
        mStatus = (TweetEntity) data.getSerializable(DataType.STATUS);
        mTouchedPicturePosition = data.getInt(DataType.PIC_POSITION);
        mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        List<String> URLs = mStatus.mediaUrlList;
        mPicCount = URLs.size();
        PicturePager pageAdapter = new PicturePager(getChildFragmentManager(), URLs);
        ViewPager viewPager = view.findViewById(R.id.pic_pager);
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(mPicCount - 1);

        viewPager.setCurrentItem(mTouchedPicturePosition);

        setActionBarTitle(mTouchedPicturePosition + 1);

        return view;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //do nothing
    }

    @Override
    public void onPageSelected(int position) {
        setActionBarTitle(position + 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //do nothing
    }

    private void setActionBarTitle(int picPosition) {
        mActionBar.setTitle("Picture" + " " + picPosition + "/" + mPicCount);
    }

    @Override
    public String toString() {
        return "Picture";
    }

    public static class PictureScreen extends Fragment {

        private String mUrl;
        private ZoomPicture mPictureView;

        public static Fragment getInstance(String url) {
            Fragment fragment = new PictureScreen();
            Bundle data = new Bundle();
            data.putString(DataType.URL, url);
            fragment.setArguments(data);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mUrl = getArguments().getString(DataType.URL);
            }
        }

        @Override
        public View onCreateView(@NotNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.picture_screen, container, false);
            mPictureView = view.findViewById(R.id.picture);
            Picasso.with(getActivity())
                    .load(mUrl)
                    .skipMemoryCache()
                    .into(mPictureView);

            return view;
        }
    }


    /**
     * 複数枚画像のscroll用pager
     */
    public static class PicturePager extends FragmentPagerAdapter {

        private final List<String> mUrls;

        PicturePager(FragmentManager fragmentManager, List<String> urls) {
            super(fragmentManager);
            mUrls = urls;
        }

        @Override
        public Fragment getItem(int position) {
            return PictureScreen.getInstance(mUrls.get(position));
        }

        @Override
        public int getCount() {
            return mUrls.size();
        }
    }
}
