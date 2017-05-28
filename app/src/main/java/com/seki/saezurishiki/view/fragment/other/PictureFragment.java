package com.seki.saezurishiki.view.fragment.other;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.view.fragment.util.DataType;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.seki.saezurishiki.control.UIControlUtil.createMediaURLList;

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
        if ( data == null ) throw new IllegalStateException("Argument is null");
        mStatus = (TweetEntity) data.getSerializable(DataType.STATUS);
        mTouchedPicturePosition = data.getInt(DataType.PIC_POSITION);
        mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);

        List<String> URLs = mStatus.mediaUrlList;
        mPicCount = URLs.size();
        PicturePager pageAdapter = new PicturePager(getChildFragmentManager(), URLs);
        ViewPager viewPager = (ViewPager)view.findViewById(R.id.pic_pager);
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(mPicCount - 1);

        viewPager.setCurrentItem(mTouchedPicturePosition);

        mActionBar.setTitle("Picture" + " " + String.valueOf(mTouchedPicturePosition + 1) + "/" + mPicCount);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //this.fragmentControl = (FragmentControl)getActivity();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //do nothing
    }

    @Override
    public void onPageSelected(int position) {
        mActionBar.setTitle("Picture" + " " + String.valueOf(position + 1) + "/" + mPicCount);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //do nothing
    }


    @Override
    public String toString() {
        return "Picture";
    }


    public static class PictureScreen extends Fragment {

        private String mUrl;
        private ImageView mPictureView;

        public static Fragment getInstance(String url) {
            Fragment fragment = new PictureScreen();
            Bundle data = new Bundle();
            data.putString(DataType.URL, url);
            fragment.setArguments(data);
            return fragment;
        }

        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mUrl = getArguments().getString(DataType.URL);
        }

        @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.picture_screen, container, false);
            mPictureView = (ImageView)view.findViewById(R.id.picture);
            Picasso.with(getActivity()).load(mUrl).skipMemoryCache().into(mPictureView);
            ScaleGestureDetector.SimpleOnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScaleBegin(ScaleGestureDetector detector) {
                    return super.onScaleBegin(detector);
                }
                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {
                    super.onScaleEnd(detector);
                }
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    super.onScale(detector);
                    final float currentSpan = detector.getCurrentSpan();
                    final float previousSpan = detector.getPreviousSpan();
                    final float distance = currentSpan - previousSpan;
                    final float scaleX = distance/100;
                    final float scaleY = distance/100;
                    if (distance >= 0) {
                        if (scaleX + mPictureView.getScaleX() >= 5) {
                            mPictureView.setScaleX(5f);
                            mPictureView.setScaleY(5f);
                            return false;
                        }
                    } else {
                        if (mPictureView.getScaleX() + scaleX <= 1) {
                            mPictureView.setScaleX(1f);
                            mPictureView.setScaleY(1f);
                            return false;
                        }
                    }
                    mPictureView.setScaleX(mPictureView.getScaleX() + scaleX);
                    mPictureView.setScaleY(mPictureView.getScaleY() + scaleY);
                    return true;
                }
            };

            final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getActivity(), scaleGestureListener);

            final GestureDetector.SimpleOnGestureListener doubleTapListener = new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    final float currentScale = mPictureView.getScaleX();
                    if (currentScale > 1f) {
                        mPictureView.setScaleX(1f);
                        mPictureView.setScaleY(1f);
                        mPictureView.layout(0, 0, mPictureView.getWidth(), mPictureView.getHeight());
                    } else if (currentScale == 1) {
                        mPictureView.setScaleX(2f);
                        mPictureView.setScaleY(2f);
                    }

                    return false;
                }
            };

            final GestureDetector doubleTapDetector = new GestureDetector(getActivity(), doubleTapListener);

            mPictureView.setOnTouchListener(new View.OnTouchListener() {

                float offsetX = 0f;
                float offsetY = 0f;
                int currentX = 0;
                int currentY = 0;

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    //TODO 仕様方針上ピンチによる拡大縮小は必要要件ではないためいったん削除
                    //scaleGestureDetector.onTouchEvent(motionEvent);

                    doubleTapDetector.onTouchEvent(motionEvent);

                    //タップされた座標を取得
                    float rawX = motionEvent.getRawX();
                    float rawY = motionEvent.getRawY();

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //タップ時点の左上の座標を取得
                            currentX = view.getLeft();
                            currentY = view.getTop();
                            //タップ座標をOffSet値に設定
                            offsetX = rawX;
                            offsetY = rawY;
                            break;

                        case MotionEvent.ACTION_MOVE :
                            //移動距離を算出
                            float distanceX = offsetX - rawX;
                            float distanceY = offsetY - rawY;

                            //基準となる左上座標の移動距離を算出
                            currentX -= distanceX;
                            currentY -= distanceY;

                            //移動
                            if (view.getScaleX() == 1) {
                                view.layout(view.getLeft(), currentY, view.getLeft() + view.getWidth(), currentY + view.getHeight());
                            } else {
                                view.layout(currentX, currentY, currentX + view.getWidth(), currentY + view.getHeight());
                            }

                            //タップ座標をOffSet値に設定
                            offsetX = rawX;
                            offsetY = rawY;
                            break;

                        case MotionEvent.ACTION_UP :
                            if (view.getScaleX() != 1) {
                                break;
                            }

                            // FIXME: 2016/10/16
                            if (currentY < -200 || 200 < currentY) {
                                getActivity().onBackPressed();
                            }
                            break;
                    }

                    return true;
                }
            });


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

        @Override public Fragment getItem(int position) {
            return PictureScreen.getInstance(mUrls.get(position));
        }

        @Override public int getCount() {
            return mUrls.size();
        }
    }
}
