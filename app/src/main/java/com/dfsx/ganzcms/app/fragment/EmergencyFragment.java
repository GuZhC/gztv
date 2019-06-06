package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.dfsx.core.common.Util.Util;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 应急主界面
 */
public class EmergencyFragment extends Fragment implements View.OnClickListener,PullToRefreshListView.PullRecyclerHelper {

    private ViewPager pager;
    private TabLayout tab;
    private AppBarLayout appBarLayout;
    private ImageView imageBack,imageOtherBack;
    private int headerOffSetSize;
    private View floatView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emergency,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view){
        pager = (ViewPager)view.findViewById(R.id.pager);
        tab = (TabLayout) view.findViewById(R.id.tab);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        imageBack = (ImageView) view.findViewById(R.id.image_back);
        imageOtherBack = (ImageView) view.findViewById(R.id.image_other_back);
        floatView = view.findViewById(R.id.float_view);
        imageBack.setOnClickListener(this);
        imageOtherBack.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset < -100){
                    imageBack.setVisibility(View.GONE);
                    imageOtherBack.setVisibility(View.VISIBLE);
                }else {
                    imageBack.setVisibility(View.VISIBLE);
                    imageOtherBack.setVisibility(View.GONE);
                }
                headerOffSetSize = verticalOffset;
            }
        });
    }

    private void initData() {
        List<ColumnCmsEntry> emengyRadiolist = ColumnBasicListManager.getInstance().findColumnListByCodes("gz-yjgb");
        if (!(emengyRadiolist == null || emengyRadiolist.isEmpty())) {
            String[] arrays = new String[emengyRadiolist.size()];
            ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
            for (int i = 0; i < emengyRadiolist.size(); i++) {
                ColumnCmsEntry entry = emengyRadiolist.get(i);
                if (entry == null) continue;
                arrays[i] = entry.getName();
                Fragment frag = null;
                if (TextUtils.equals(entry.getMachine_code(), "gz-yjxx")) {
                    frag = EmergMessagesFragment.newInstance(entry.getId(), entry.getMachine_code(), -1, -1);
                } else if (TextUtils.equals(entry.getMachine_code(), "gz-zjkp")) {
                    frag = HeadLineFragment.newInstance(entry.getId(), entry.getMachine_code(), -1, -1);
                }
                fragmentArrayList.add(frag);
            }
            BasePasliderAdapter adapter = new BasePasliderAdapter(getChildFragmentManager(), fragmentArrayList, arrays);
            pager.setAdapter(adapter);
            tab.setupWithViewPager(pager);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_back || v.getId() == R.id.image_other_back){
            getActivity().finish();
        }
    }

    @Override
    public boolean isReadyForPullEnd() {
        return  Math.abs(headerOffSetSize) >= (appBarLayout.getHeight()  - floatView.getHeight());
    }

    @Override
    public boolean isReadyForPullStart() {
        return headerOffSetSize == 0;
    }

    public class BasePasliderAdapter extends FragmentStatePagerAdapter {
        private String[] mPagerTitles;
        private ArrayList<Fragment> fragments;

        public BasePasliderAdapter(FragmentManager fm, ArrayList<Fragment> fragments, String[] titles) {
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
