package com.dfsx.procamera.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.lzcms.liveroom.view.CenterGroupChangeBar;
import com.dfsx.procamera.R;
import com.dfsx.procamera.busniness.IActivtiySelectItemiter;
import com.dfsx.procamera.model.ScrollItem;
import com.dfsx.procamera.view.PagerSlidingTabStrip;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.VideoFragmentActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;

import static com.dfsx.selectedmedia.activity.VideoFragmentActivity.KEY_SINGLE_MODE;

/**
 * Created by heyang on 2018/6/20.
 */
public class AcvityCameraTabFragment extends Fragment implements IActivtiySelectItemiter {
    private static final int BAR_TEXT_SIZE_SP = 15;
    private static final int ITEM_MIN_SPACE_DP = 20;
    private Activity context;
    protected CenterGroupChangeBar changeBar;
    protected ViewPager viewPager;
    private MyVideoGrid2Fragment myVideosFragment;
    private ActivityRecordFragment cameraFragment;
    private HorizontalScrollView mHorizontalScrollView;
    private LinearLayout mLinearLayout;
    private View mImageViewContainer;
    private int item_width;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    private PagerSlidingTabStrip tabs;
    private long actId;
    private boolean isCommunnity = false;

    protected static final int DEFAULT_SELECTED_COUNT = 0;

    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_tab_camera, null);
        context = getActivity();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            actId = getArguments().getLong("id");
            isCommunnity = getArguments().getBoolean("isCommunity");
        }
        initView(view);
        initAction();
        initDataView();
    }

    private void initView(View view) {
        changeBar = (CenterGroupChangeBar) view.findViewById(R.id.center_change_bar);
        changeBar.setBarTextArray(DEFAULT_SELECTED_COUNT, new String[]{"相册", "拍摄"});
        viewPager = (ViewPager) view.findViewById(R.id.change_viewpager);

        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.id_tab_strip);

    }

    private void initAction() {
        changeBar.setOnBarSelectedChangeListener(new CenterGroupChangeBar.OnBarSelectedChangeListener() {
            @Override
            public void onSelectedChange(int selectedIndex) {
                if (selectedIndex >= 0 && selectedIndex < 2
                        && viewPager.getCurrentItem() != selectedIndex) {
                    viewPager.setCurrentItem(selectedIndex, true);
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeBar.setCheckIndex(position);
                if (position == 0) {
                    tabs.setIndicatorColor(0xff000000);
                    tabs.setTextColor(0xff000000);
                    tabs.setSelectedTextColor(0xff000000);
                } else {
                    tabs.setIndicatorColor(0xffffffff);
                    tabs.setTextColor(0xffffffff);
                    tabs.setSelectedTextColor(0xffffffff);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public long getActivtiId() {
        return actId;
    }

    private void initDataView() {
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        if (myVideosFragment == null) {
            myVideosFragment = new MyVideoGrid2Fragment();
            myVideosFragment.setmCallback(this);
        }
        if (cameraFragment == null) {
//            cameraFragment = new ActivityPublishFragment();
            cameraFragment = new ActivityRecordFragment();
            cameraFragment.setSelectItemiter(this);
        }
        fragmentArrayList.add(myVideosFragment);
        fragmentArrayList.add(cameraFragment);

        String[] arrays = new String[]{"相册", "拍摄"};
        LiveTabPagerAdapter adapter = new LiveTabPagerAdapter(getChildFragmentManager(), fragmentArrayList, arrays);
        viewPager.setAdapter(adapter);
//        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setCurrentItem(DEFAULT_SELECTED_COUNT, true);
        tabs.setViewPager(viewPager);
    }

    @Override
    public void OnComplete(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (myVideosFragment != null)
                myVideosFragment.addItem(url);
            if (getActivity() != null)
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(url))));

            //刷新图库
            if (myVideosFragment != null) {
                myHander.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myVideosFragment.rarshData();
                    }
                }, 500);
            }
            if (!isCommunnity) {
//                MediaModel mode = new MediaModel();
//                mode.setType(1);
//                mode.setUrl(url);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("object", mode);
//                bundle.putLong("actId", actId);
//                DefaultFragmentActivity.start(getActivity(), ActivityPublishFragment.class.getName(), bundle);
            } else {
                IntentUtil.gotoCommunPubishAct(getActivity(), actId, url);
            }
            getActivity().finish();
        }
    }

    @Override
    public void onCancel() {
        if (viewPager != null) {
            viewPager.setCurrentItem(0, true);
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(final int position) {
            if (position == 0) {
                tabs.setIndicatorColor(0xff000000);
                tabs.setTextColor(0xff000000);
                tabs.setSelectedTextColor(0xff000000);
            } else {
                tabs.setIndicatorColor(0xffffffff);
                tabs.setTextColor(0xffffffff);
                tabs.setSelectedTextColor(0xffffffff);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    static class LiveTabPagerAdapter extends FragmentPagerAdapter {
        private String[] mPagerTitles;

        private ArrayList<Fragment> fragments;

        public LiveTabPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, String[] titles) {
            super(fm);
            this.fragments = fragments;
            this.mPagerTitles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPagerTitles[position];
        }


        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }
    }
}
