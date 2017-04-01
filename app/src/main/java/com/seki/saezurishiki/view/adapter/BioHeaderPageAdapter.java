package com.seki.saezurishiki.view.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.view.fragment.util.DataType;
import com.squareup.picasso.Picasso;

import twitter4j.User;


public class BioHeaderPageAdapter extends FragmentPagerAdapter {

    private final User mUser;

    public BioHeaderPageAdapter(FragmentManager fm, User user) {
        super(fm);
        mUser = user;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0 :
                return BioThirdFragment.newInstance(mUser);

            case 1 :
                return BioFirstFragment.newInstance(mUser);

            case 2 :
                return BioSecondFragment.newInstance(mUser);

            default :
                throw new IllegalStateException();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


    public static class BioFirstFragment extends Fragment {
        private User mUser;
        public static Fragment newInstance(User user) {
            Bundle data = new Bundle();
            data.putSerializable(DataType.USER, user);
            Fragment f = new BioFirstFragment();
            f.setArguments(data);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mUser = (User)getArguments().getSerializable(DataType.USER);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View firstPage = inflater.inflate(R.layout.fragment_bio_first_page, null);
            initComponent(firstPage);
            return firstPage;
        }

        void initComponent(View view) {
            ImageView headerImage = (ImageView)view.findViewById(R.id.bio_header_icon);
            Picasso.with(getActivity()).load(mUser.getProfileBannerURL()).skipMemoryCache().fit().into(headerImage);

            ImageView userIcon = (ImageView)view.findViewById(R.id.bio_user_icon);
            Picasso.with(getActivity()).load(mUser.getBiggerProfileImageURL()).into(userIcon);

            TextView userName = (TextView)view.findViewById(R.id.bio_user_name);
            String concatName = "@" + mUser.getScreenName() + " / " + mUser.getName();
            userName.setText(concatName);

            if (mUser.isProtected()) {
                view.findViewById(R.id.protect_icon).setVisibility(View.VISIBLE);
            }
        }
    }


    public static class BioSecondFragment extends Fragment {
        private User mUser;
        public static Fragment newInstance(User user) {
            Bundle data = new Bundle();
            data.putSerializable(DataType.USER, user);
            Fragment f = new BioSecondFragment();
            f.setArguments(data);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mUser = (User)getArguments().getSerializable(DataType.USER);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View firstPage = inflater.inflate(R.layout.fragment_bio_second_page, null);
            initComponent(firstPage);
            return firstPage;
        }

        void initComponent(View view) {
            TextView bioText = (TextView)view.findViewById(R.id.bio_text);
            bioText.setText(mUser.getDescription());
        }
    }


    public static class BioThirdFragment extends Fragment {
        private User mUser;
        public static Fragment newInstance(User user) {
            Bundle data = new Bundle();
            data.putSerializable(DataType.USER, user);
            Fragment f = new BioThirdFragment();
            f.setArguments(data);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mUser = (User)getArguments().getSerializable(DataType.USER);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View firstPage = inflater.inflate(R.layout.fragment_bio_third_page, null);
            initComponent(firstPage);
            return firstPage;
        }

        void initComponent(View view) {
            TextView sinceText = (TextView)view.findViewById(R.id.bio_since);
            sinceText.setText(UIControlUtil.formatDate(mUser.getCreatedAt()));

            TextView locateText = (TextView)view.findViewById(R.id.bio_location);
            locateText.setText(mUser.getLocation());

            TextView webText = (TextView)view.findViewById(R.id.bio_web);
            webText.setText(mUser.getURL());
        }
    }

}
