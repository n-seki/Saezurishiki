package com.seki.saezurishiki.view.fragment.other;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.entity.Media;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.Video;
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
    private int mCurrentPosition;
    private Listener mListener;

    private int mPicCount;

    public interface Listener {
        void onChangePicture(int pictureNum, int position);
    }

    public static Fragment getInstance(int position, TweetEntity status) {
        Fragment fragment = new PictureFragment();
        Bundle data = new Bundle();
        data.putInt(DataType.PIC_POSITION, position);
        data.putSerializable(DataType.STATUS, status);
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof Listener) {
            mListener = (Listener)getActivity();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getArguments();
        if (data == null) {
            throw new IllegalStateException("Argument is null");
        }
        mStatus = (TweetEntity) data.getSerializable(DataType.STATUS);
        mCurrentPosition = data.getInt(DataType.PIC_POSITION);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        List<Media> URLs = mStatus.mediaUrlList;
        mPicCount = URLs.size();
        PicturePager pageAdapter = new PicturePager(getChildFragmentManager(), URLs);
        ViewPager viewPager = view.findViewById(R.id.pic_pager);
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(mPicCount - 1);
        viewPager.setCurrentItem(mCurrentPosition);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.onChangePicture(mPicCount, mCurrentPosition + 1);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //do nothing
    }

    @Override
    public void onPageSelected(int position) {
        if (mListener != null) {
            mListener.onChangePicture(mPicCount, position + 1);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //do nothing
    }

    public static class PictureScreen extends Fragment {

        private Media mMedia;
        private ZoomPicture mPictureView;

        public static Fragment getInstance(Media media) {
            Fragment fragment = new PictureScreen();
            Bundle data = new Bundle();
            data.putParcelable(DataType.MEDIA, media);
            fragment.setArguments(data);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mMedia = getArguments().getParcelable(DataType.MEDIA);
            }
        }

        @Override
        public View onCreateView(@NotNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.picture_screen, container, false);
            mPictureView = view.findViewById(R.id.picture);

            Picasso.with(container.getContext())
                    .load(mMedia.getThumbnail())
                    .skipMemoryCache()
                    .into(mPictureView);

            return view;
        }
    }


    /**
     * 複数枚画像のscroll用pager
     */
    public static class PicturePager extends FragmentPagerAdapter {

        private final List<Media> mUrls;

        PicturePager(FragmentManager fragmentManager, List<Media> urls) {
            super(fragmentManager);
            mUrls = urls;
        }

        @Override
        public Fragment getItem(int position) {
            if (mUrls.get(position) instanceof Video) {
                return VideoFragment.getInstance((Video)mUrls.get(position));
            } else {
                return PictureScreen.getInstance(mUrls.get(position));
            }
        }

        @Override
        public int getCount() {
            return mUrls.size();
        }
    }
}
