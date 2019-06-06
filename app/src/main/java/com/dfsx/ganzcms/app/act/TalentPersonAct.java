package com.dfsx.ganzcms.app.act;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.core.common.view.banner.BannerItem;
import com.dfsx.core.common.view.banner.SimpleImageBanner;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.fragment.HostMessageFragment;
import com.dfsx.ganzcms.app.fragment.HostPersonIntroduceFragment;
import com.dfsx.ganzcms.app.view.zoom.IPullZoom;
import com.dfsx.ganzcms.app.view.zoom.ZoomHeaderCoordinatorLayout;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.lzcms.liveroom.view.CenterGroupChangeBar;

import java.util.ArrayList;

public class TalentPersonAct extends BaseActivity implements IPullZoom {

    private Activity act;

    private View topBar;
    private SimpleImageBanner imageBanner;
    private CenterGroupChangeBar groupChangeBar;
    private ViewPager viewPager;
    private ZoomHeaderCoordinatorLayout zoomHeaderCoordinatorLayout;
    private AppBarLayout barLayout;

    private int currentPage;
    private int headerOffSetSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        setContentView(R.layout.act_talent_person);
        initView();
        initPager();
        initData();
    }

    private void initData() {
        ArrayList<BannerItem> list = new ArrayList<>();
        BannerItem item = new BannerItem();
        item.imgUrl = "http://cpc.people.com.cn/NMediaFile/2017/1018/MAIN201710181352077766232230363.jpg";
        item.title = "";
        list.add(item);
        item = new BannerItem();
        item.imgUrl = "http://cpc.people.com.cn/NMediaFile/2017/1018/MAIN201710181036112161918566331.jpg";
        item.title = "";
        list.add(item);
        item = new BannerItem();
        item.imgUrl = "http://cpc.people.com.cn/NMediaFile/2017/1018/MAIN201710181036112161918566331.jpg";
        item.title = "";
        list.add(item);
        imageBanner.setPeriod(4);
        imageBanner.setDelay(4);
        imageBanner.setSource(list).startScroll();
    }

    private void initView() {
        zoomHeaderCoordinatorLayout = (ZoomHeaderCoordinatorLayout) findViewById(R.id.main_content);
        barLayout = (AppBarLayout) findViewById(R.id.appbar);
        topBar = findViewById(R.id.tool_bar_view);
        imageBanner = (SimpleImageBanner) findViewById(R.id.person_image_banner);
        groupChangeBar = (CenterGroupChangeBar) findViewById(R.id.center_indicator);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        groupChangeBar.setBarTextArray(0, "介绍", "留言");
        zoomHeaderCoordinatorLayout.setPullZoom(barLayout,
                PixelUtil.dp2px(this, 390),
                PixelUtil.dp2px(this, 500), this);

        groupChangeBar.setOnBarSelectedChangeListener(new CenterGroupChangeBar.OnBarSelectedChangeListener() {
            @Override
            public void onSelectedChange(int selectedIndex) {
                if (currentPage != selectedIndex) {
                    currentPage = selectedIndex;
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
                if (currentPage != position) {
                    currentPage = position;
                    groupChangeBar.setCheckIndex(position);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        barLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                headerOffSetSize = verticalOffset;
            }
        });
    }

    private void initPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new HostPersonIntroduceFragment());
        fragments.add(new HostMessageFragment());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean isReadyForPullStart() {
        return headerOffSetSize == 0;
    }

    @Override
    public void onPullZooming(int newScrollValue) {

    }

    @Override
    public void onPullZoomEnd() {

    }


    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;

        public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments != null ? fragments.size() : 0;
        }
    }
}
