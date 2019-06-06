package com.dfsx.ganzcms.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.view.banner.BannerDataHelper;
import com.dfsx.core.common.view.banner.BannerItem;
import com.dfsx.core.common.view.banner.BaseBanner;
import com.dfsx.core.common.view.banner.SimpleImageBanner;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.LiveServiceRoomAdapter;
import com.dfsx.ganzcms.app.business.ChannelManager;
import com.dfsx.ganzcms.app.business.LiveServiceDataHelper;
import com.dfsx.ganzcms.app.model.LiveInfo;
import com.dfsx.ganzcms.app.model.SearchLiveData;
import com.dfsx.ganzcms.app.view.EditLiveRoomPasswordDialog;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.LiveServiceRoomActivity;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.model.ChatRoomIntentData;
import com.dfsx.lzcms.liveroom.model.EnterRoomInfo;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.List;

/**
 * 现场的直播
 * Created by liuwb on 2017/6/14.
 */
public class LiveServiceFragment extends AbsListFragment {

    public static final int PAGE_SIZE = 10;

    private LiveServiceRoomAdapter adapter;

    private LiveServiceDataHelper dataHelper;
    private EmptyView emptyView;
    private int currentPage = 1;

    private SimpleImageBanner liveServiceBanner;
    private List<LiveInfo> bannerDataList;

    private boolean isAddBannerHeader;
    private ChannelManager channelManager;
    private EditLiveRoomPasswordDialog passwordDialog;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataHelper = new LiveServiceDataHelper(context);
        channelManager = new ChannelManager(context);
        initAction();
        getData(true, 1);
        getBannerData(true);
    }

    @Override
    public void setListAdapter(ListView listView) {
        if (adapter == null) {
            adapter = new LiveServiceRoomAdapter(context);
        }
        listView.setAdapter(adapter);
        liveServiceBanner = (SimpleImageBanner) LayoutInflater.from(context)
                .inflate(R.layout.banner_live_service_layout, null);
        addBannerHeader();
        //        ArrayList<Integer> arrayList = new ArrayList<>();
        //        arrayList.add(1);
        //        arrayList.add(1);
        //        arrayList.add(1);
        //        arrayList.add(1);
        //        adapter.update(arrayList, false);
    }

    private void addBannerHeader() {
        if (!isAddBannerHeader) {
            listView.addHeaderView(liveServiceBanner);
            isAddBannerHeader = true;
        }
    }

    private void removeBannerHeader() {
        isAddBannerHeader = false;
        listView.removeHeaderView(liveServiceBanner);
    }

    private void getData(boolean isreadCache, int page) {
        this.currentPage = page;
        dataHelper.getData(isreadCache, page, PAGE_SIZE, new DataRequest.DataCallback<List<LiveInfo>>() {
            @Override
            public void onSuccess(boolean isAppend, List<LiveInfo> data) {
                if (adapter != null) {
                    adapter.update(data, isAppend);
                }
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
            }

            @Override
            public void onFail(ApiException e) {
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
            }
        });
    }

    private void getBannerData(boolean isreadCache) {
        dataHelper.getBannerLoopData(isreadCache, new DataRequest.DataCallback<SearchLiveData>() {
            @Override
            public void onSuccess(boolean isAppend, SearchLiveData data) {
                if (data != null && data.getLiveInfoList() != null && !data.getLiveInfoList().isEmpty()) {
                    addBannerHeader();
                    bannerDataList = data.getLiveInfoList();
                    liveServiceBanner.setSource(bannerDataHelper.
                            getBannerItems(data.getLiveInfoList()))
                            .startScroll();
                } else {
                    removeBannerHeader();
                    bannerDataList = null;
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                removeBannerHeader();
                bannerDataList = null;
            }
        });
    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("暂时未有数据");
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        container.addView(emptyView);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData(false, 1);
        getBannerData(false);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        getData(false, currentPage + 1);
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    private void initAction() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                List<LiveInfo> list = adapter.getData();
                if (list != null && position >= 0 && position < list.size()) {
                    LiveInfo info = list.get(position);
                    doLiveInfoClick(info);
                }
            }
        });

        liveServiceBanner.setOnItemClickL(new BaseBanner.OnItemClickL() {
            @Override
            public void onItemClick(int position) {
                if (bannerDataList != null &&
                        position >= 0 && position < bannerDataList.size()) {
                    doLiveInfoClick(bannerDataList.get(position));
                }
            }
        });
    }

    private void doLiveInfoClick(LiveInfo info) {
        if (info.isNeedRoomPassword()) {
            doPasswordLive(info);
        } else {
            goLiveServiceRoomAct(info);
        }
    }

    private void doPasswordLive(final LiveInfo info) {
        channelManager.livePasswordNoNeed(info.getId(), new DataRequest.DataCallback<Boolean>() {
            @Override
            public void onSuccess(boolean isAppend, Boolean data) {
                if (data) {
                    goLiveServiceRoomAct(info);
                } else {
                    showPasswordEdit(info);
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                showPasswordEdit(info);
            }
        });
    }

    private void goLiveServiceRoomAct(LiveInfo info) {
        goLiveServiceRoomAct(info, null);
    }

    private void goLiveServiceRoomAct(LiveInfo info, String passWord) {
        Intent intent = new Intent(context, LiveServiceRoomActivity.class);
        ChatRoomIntentData intentData = new ChatRoomIntentData();
        intentData.setRoomId(info.getId());
        if (!TextUtils.isEmpty(passWord)) {
            intentData.setRoomPassword(passWord);
        }
        intentData.setAutoJoinRoomAtOnce(true);
        intent.putExtra(AbsChatRoomActivity.KEY_CHAT_ROOM_INTENT_DATA, intentData);
        startActivity(intent);
    }

    private void showPasswordEdit(final LiveInfo info) {
        passwordDialog = new EditLiveRoomPasswordDialog(act);
        passwordDialog.setOnPositiveButtonClickListener(new EditLiveRoomPasswordDialog.OnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(EditLiveRoomPasswordDialog dialog, View v) {
                verifyPassword(info,
                        dialog.getEditPassword().getText().toString());
            }
        });
        if (act != null && !act.isFinishing()) {
            passwordDialog.show();
        }
    }

    private void verifyPassword(final LiveInfo info, final String password) {
        channelManager.enterRoom(info.getRoomId() + "", password, new ICallBack<EnterRoomInfo>() {
            @Override
            public void callBack(EnterRoomInfo data) {
                if (data == null || data.isError()) {
                    String errorText = "密码有误，请重新输入";
                    passwordDialog.updateNoteText(errorText,
                            context.getResources().getColor(R.color.note_password_error));
                    passwordDialog.clearEditTextInput();
                } else {
                    passwordDialog.dismiss();
                    goLiveServiceRoomAct(info, password);
                }
            }
        });
    }

    private BannerDataHelper<LiveInfo> bannerDataHelper = new BannerDataHelper<LiveInfo>() {
        @Override
        public BannerItem changeToBannerItem(LiveInfo data) {
            BannerItem item = new BannerItem();
            if (data != null) {
                int res = 0;
                if (data.getState() == 1) {
                    res = R.drawable.icon_live_no_start;
                } else if (data.getState() == 2) {//正在直播
                    res = R.drawable.icon_living_on;
                } else {//直播已结束
                    res = R.drawable.icon_back_live;
                }
                item.titleResource = res;
                item.title = data.getRoomTitle();
                item.imgUrl = data.getCoverUrl();
            }
            return item;
        }
    };
}
