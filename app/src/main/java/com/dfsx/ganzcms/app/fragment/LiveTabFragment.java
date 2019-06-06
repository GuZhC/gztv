package com.dfsx.ganzcms.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.PrepareLiveActivity;
import com.dfsx.ganzcms.app.act.WhiteTopBarSwitchActivity;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.model.UserLivePlatformInfo;
import com.dfsx.ganzcms.app.view.LiveVideoPopupWindow;
import com.dfsx.lzcms.liveroom.view.CenterGroupChangeBar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuwb on 2017/6/14.
 */
public class LiveTabFragment extends Fragment {

    private Activity context;
    private CenterGroupChangeBar changeBar;
    private TextView rightTitltTextView;
    private ViewPager viewPager;
    private LiveUserRoomFragment liveUserFragment;
    private LiveWebLinkFragment serviceFragment;

    private static final int DEFAULT_SELECTED_COUNT = 0;

    private ImageView rightStartLiveImage;
    private int startLiveVisibleCount = -1;

    private MyDataManager dataManager;
    private LiveVideoPopupWindow livePopwidow;

    private int currentSelectedCount = DEFAULT_SELECTED_COUNT;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_tab_live, null);
        context = getActivity();
        dataManager = new MyDataManager(context);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initAction();
        initDataView();
    }

    private void initView(View view) {
        changeBar = (CenterGroupChangeBar) view.findViewById(R.id.center_change_bar);
        rightTitltTextView = (TextView) view.findViewById(R.id.right_text);
        rightStartLiveImage = (ImageView) view.findViewById(R.id.start_live_image);
        changeBar.setBarTextArray(DEFAULT_SELECTED_COUNT, new String[]{"现场", "主播"});
        //        changeBar.setBarTextArray(DEFAULT_SELECTED_COUNT, new String[]{"直播"});
        viewPager = (ViewPager) view.findViewById(R.id.change_viewpager);

        rightStartLiveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (livePopwidow == null) {
                    initLivePop();
                }
                livePopwidow.show(v);
            }
        });
    }

    private void initLivePop() {
        livePopwidow = new LiveVideoPopupWindow(context);
        livePopwidow.setOnPopViewClickListener(new LiveVideoPopupWindow.
                OnPopViewClickListener() {
            @Override
            public void onStartLiveClick() {
                gotoLivRecord();
            }

            @Override
            public void onUploadVideoClick() {
            }

            @Override
            public void onStartYuGaoLiveClick() {
                ArrayList<String> titleList = new ArrayList<String>();
                titleList.add("预告直播");
                titleList.add("预告列表");
                WhiteTopBarSwitchActivity.start(context,
                        YuGaoFragment.class.getName(), titleList, 0, "");
            }

            @Override
            public void onServiceLiveClick() {
                WhiteTopBarActivity.startAct(context,
                        MyLiveServiceListFragment.class.getName(), "活动列表");
            }

            @Override
            public void onCancleClick() {
            }
        });
    }

    public void gotoLivRecord() {
        new TedPermission(context).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(context, PrepareLiveActivity.class);
                startActivity(intent);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(context, "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(context.getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.CAMERA)
                .check();
    }

    private void setLivePlatformPermissionInfo() {
        dataManager.getMyLivePlatformInfo(new DataRequest.DataCallback<UserLivePlatformInfo>() {
            @Override
            public void onSuccess(boolean isAppend, UserLivePlatformInfo data) {
                boolean isShowStartLiveBtn = data != null ? data.isCreateLivePermission()
                        : false;
                startLiveVisibleCount = isShowStartLiveBtn ? 1 : 0;
                setRightViewVisible(currentSelectedCount);
            }

            @Override
            public void onFail(ApiException e) {
                Log.e("TAG", "live platform info error == " + e.getMessage());
                e.printStackTrace();
                startLiveVisibleCount = -1;
            }
        });
    }

    private void initAction() {
        RxView.clicks(rightTitltTextView)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        String text = rightTitltTextView.getText().toString();
                        int style = 0;
                        if (!TextUtils.isEmpty(text) &&
                                text.contains("小图")) {
                            text = text.replace("小图", "大图");
                            style = ILiveShowStyleChange.STYLE_GRID;
                        } else if (!TextUtils.isEmpty(text) &&
                                text.contains("大图")) {
                            text = text.replace("大图", "小图");
                            style = ILiveShowStyleChange.STYLE_LIST;
                        }
                        rightTitltTextView.setText(text);
                        if (getLiveShowStyleChange() != null) {
                            getLiveShowStyleChange().onStyleChange(style);
                        }
                    }
                });
        changeBar.setOnBarSelectedChangeListener(new CenterGroupChangeBar.OnBarSelectedChangeListener() {
            @Override
            public void onSelectedChange(int selectedIndex) {
                if (selectedIndex >= 0 && selectedIndex < 2
                        && viewPager.getCurrentItem() != selectedIndex) {
                    viewPager.setCurrentItem(selectedIndex, true);
                    setRightViewVisible(selectedIndex);
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setRightViewVisible(int selectedIndex) {
        this.currentSelectedCount = selectedIndex;
        if (selectedIndex == 1) {//表示主播
            rightTitltTextView.setVisibility(View.VISIBLE);
            rightStartLiveImage.setVisibility(View.GONE);
        } else {
            rightTitltTextView.setVisibility(View.GONE);
            if (startLiveVisibleCount > 0) {
                rightStartLiveImage.setVisibility(View.VISIBLE);
            } else if (startLiveVisibleCount == 0) {
                rightStartLiveImage.setVisibility(View.GONE);
            } else {
                rightStartLiveImage.setVisibility(View.GONE);
                if (App.getInstance().isLogin()) {
                    setLivePlatformPermissionInfo();
                }
            }
        }
    }

    private ILiveShowStyleChange getLiveShowStyleChange() {
        return liveUserFragment;
    }

    private void initDataView() {
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        if (liveUserFragment == null) {
            liveUserFragment = new LiveUserRoomFragment();
        }
        if (serviceFragment == null) {
            serviceFragment = new LiveWebLinkFragment();
        }
        fragmentArrayList.add(serviceFragment);
        fragmentArrayList.add(liveUserFragment);
        LiveTabPagerAdapter adapter = new LiveTabPagerAdapter(getChildFragmentManager(), fragmentArrayList);
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(DEFAULT_SELECTED_COUNT, true);
        setRightViewVisible(DEFAULT_SELECTED_COUNT);
    }

    static class LiveTabPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;

        public LiveTabPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
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
