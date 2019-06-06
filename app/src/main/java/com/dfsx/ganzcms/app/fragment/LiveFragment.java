package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.view.banner.BannerDataHelper;
import com.dfsx.core.common.view.banner.BannerItem;
import com.dfsx.core.common.view.banner.BaseBanner;
import com.dfsx.core.common.view.banner.SimpleImageBanner;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.LiveRoomAdapter;
import com.dfsx.ganzcms.app.business.ChannelManager;
import com.dfsx.ganzcms.app.business.HeadlineListManager;
import com.dfsx.ganzcms.app.business.LivePersonalRoomDataListHelper;
import com.dfsx.ganzcms.app.business.LiveSearchImpl;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.view.EditLiveRoomPasswordDialog;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.LiveBackPlayFullScreenRoomActivity;
import com.dfsx.lzcms.liveroom.LiveFullScreenRoomActivity;
import com.dfsx.lzcms.liveroom.LiveRoomActivity;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.model.BackPlayIntentData;
import com.dfsx.lzcms.liveroom.model.EnterRoomInfo;
import com.dfsx.lzcms.liveroom.model.ChatRoomIntentData;
import com.dfsx.lzcms.liveroom.model.FullScreenRoomIntentData;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2016/9/12.
 */
public class LiveFragment extends Fragment {

    public static final int PAGE_SIZE = 10;
    private ImageView searchImage;
    protected PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private Activity act;
    private Context context;

    private SearchWnd dialogWnd;

    private int mScreenWidth;
    private int mScreenHeight;

    private SimpleImageBanner topBanner;

    private ArrayList<SocirtyNewsChannel> looperDataList;

    protected LiveRoomAdapter roomAdapter;

    private ChannelManager channelManager;

    private Handler handler = new Handler();

    private int pageCount = 1;

    private EditLiveRoomPasswordDialog passwordDialog;

    private LivePersonalRoomDataListHelper dataListHelper;
    private FrameLayout listViewEmptyViewContainer;

    private EmptyView emptyView;
    protected View topBarView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = (mScreenWidth * 9) / 16;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = getActivity();
        context = getContext();
        return inflater.inflate(R.layout.frag_live_tab, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        initView(view);
        initAction();
        //        addBannerheader();
        initData();
    }

    private void initView(View v) {
        topBarView = v.findViewById(R.id.top_bar);
        searchImage = (ImageView) v.findViewById(R.id.icon_search);
        pullToRefreshListView = (PullToRefreshListView) v.findViewById(R.id.pull_list_view);
        listView = pullToRefreshListView.getRefreshableView();
        listViewEmptyViewContainer = (FrameLayout) v.findViewById(R.id.list_view_empty_container);
        listView.setEmptyView(listViewEmptyViewContainer);

        emptyView = EmptyView.newInstance(context);
        emptyView.loading();
        emptyView.setLoadOverView(R.layout.empty_live_room_layout);

        listViewEmptyViewContainer.addView(emptyView);
    }

    private void initAction() {
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWnd = new SearchWnd(getActivity());
                dialogWnd.setListViewAdapter(new LiveRoomAdapter(context, null));
                dialogWnd.setOnSearchClickListener(new LiveSearchImpl());
                dialogWnd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (parent instanceof ListView) {
                            ListView searchListView = (ListView) parent;
                            int pos = position - searchListView.getHeaderViewsCount();
                            LiveRoomAdapter searchRoomAdapter = (LiveRoomAdapter) dialogWnd.getListViewAdapter();
                            List<Room> searchList = searchRoomAdapter.getData();
                            if (searchRoomAdapter != null && searchList != null
                                    && pos >= 0 && pos < searchList.size()) {
                                if (searchList.get(pos).isNeedRoomPassword()) {
                                    showPasswordEdit(searchList.get(pos));
                                } else {
                                    goLiveRoom(searchList.get(pos), "");
                                }
                            }
                        }
                    }
                });
                dialogWnd.showDialog();
            }
        });
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageCount = 1;
                getData(false, 1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageCount++;
                getData(pageCount);
            }
        });

        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= listView.getHeaderViewsCount();
                onListViewItemClick(position);
            }
        });
    }

    /**
     * position是显示的数据列表的里真实的位置
     *
     * @param position
     */
    protected void onListViewItemClick(int position) {
        List<Room> list = null;
        if (roomAdapter != null) {
            list = roomAdapter.getData();
        }
        if (list != null &&
                position >= 0 &&
                position < list.size()) {
            //                    showPasswordEdit(list.get(position));
            goRoom(list.get(position));
        }
    }

    protected void goRoom(Room room) {
        if (room != null) {
            if (room.isNeedRoomPassword()) {
                showPasswordEdit(room);
            } else {
                goLiveRoom(room, "");
            }
        }
    }


    private void showPasswordEdit(final Room room) {
        passwordDialog = new EditLiveRoomPasswordDialog(act);
        passwordDialog.setOnPositiveButtonClickListener(new EditLiveRoomPasswordDialog.OnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(EditLiveRoomPasswordDialog dialog, View v) {
                verifyPassword(room,
                        dialog.getEditPassword().getText().toString());
            }
        });
        if (act != null && !act.isFinishing()) {
            passwordDialog.show();
        }
    }

    private void verifyPassword(final Room room, final String password) {
        channelManager.enterRoom(room.getRoomId() + "", password, new ICallBack<EnterRoomInfo>() {
            @Override
            public void callBack(EnterRoomInfo data) {
                if (data == null) {
                    String errorText = "密码有误，请重新输入";
                    passwordDialog.updateNoteText(errorText,
                            context.getResources().getColor(R.color.note_password_error));
                    passwordDialog.clearEditTextInput();
                } else {
                    passwordDialog.dismiss();
                    goLiveRoom(room, password);
                }
            }
        });
    }

    /**
     * 进入房间
     *
     * @param roomInfo
     */
    private void goLiveRoom(Room roomInfo, String password) {
        if (roomInfo.getRoomFlag() == Room.FLAG_BACK_PLAY) {//回放
            LiveInfo backInfo = (LiveInfo) roomInfo;
            goBackPlayRoom(LiveBackPlayFullScreenRoomActivity.class,
                    backInfo);
        } else {
            if (roomInfo.getRoomLivetype() == LiveType.EventLive) {
                Intent intent = new Intent(context, LiveRoomActivity.class);
                ChatRoomIntentData intentData = new ChatRoomIntentData();
                intentData.setRoomId(roomInfo.getRoomId());
                intentData.setRoomPassword(password);
                intentData.setRoomTitle(roomInfo.getRoomTitle());
                goLiveRoom(intent, intentData);
            } else {
                Intent intent = new Intent(context, LiveFullScreenRoomActivity.class);
                LiveInfo liveInfo = (LiveInfo) roomInfo;
                FullScreenRoomIntentData intentData = new FullScreenRoomIntentData();
                intentData.setRoomId(roomInfo.getRoomId());
                intentData.setRoomPassword(password);
                intentData.setRoomTitle(roomInfo.getRoomTitle());
                intentData.setFullScreenVideoImagePath(roomInfo.getRoomImagePath());
                intentData.setRoomOwnerId(liveInfo.getOwnerId());
                intentData.setLandVideo(liveInfo.getScreenMode() == 1);
                intentData.setYuGaoLive(roomInfo.getRoomFlag() == Room.FLAG_YUGAO);
                intentData.setStartTimestamp(liveInfo.getPlanStartTime());
                goLiveRoom(intent, intentData);
            }
        }
    }

    private void goBackPlayRoom(Class activity, LiveInfo backInfo) {
        Intent intent = new Intent();
        intent.setClass(context, activity);
        BackPlayIntentData intentData = new BackPlayIntentData();
        intent.putExtra(AbsChatRoomActivity.KEY_CHAT_ROOM_INTENT_DATA, intentData);
        intentData.setAutoJoinRoomAtOnce(true);
        intentData.setRoomId(backInfo.getRoomId());
        intentData.setRoomTitle(backInfo.getRoomTitle());
        intentData.setFullScreenVideoImagePath(backInfo.getRoomImagePath());
        intentData.setBackPlayId(backInfo.getId());
        intentData.setRoomOwnerId(backInfo.getOwnerId());
        intentData.setRoomOwnerAccountName(backInfo.getOwnerUserName());
        intentData.setRoomOwnerNickName(backInfo.getOwnerNickName());
        intentData.setRoomOwnerLogo(backInfo.getOwnerAvatarUrl());
        intentData.setRoomTotalCoins(0);
        intentData.setLandVideo(backInfo.getScreenMode() == 1);
        intentData.setMemberSize((int) backInfo.getCurrentVisitorCount());
        intentData.setLiveType(backInfo.getRoomLivetype() == LiveType.EventLive ? LiveBackPlayFullScreenRoomActivity.TYPE_LIVE_GUESS :
                LiveBackPlayFullScreenRoomActivity.TYPE_LIVE_SHOW);
        intentData.setShowId(backInfo.getRoomId());


        startActivity(intent);
    }

    private void goLiveRoom(@NonNull Intent intent, ChatRoomIntentData intentData) {
        if (intent == null) {
            return;
        }
        intentData.setAutoJoinRoomAtOnce(true);
        intent.putExtra(AbsChatRoomActivity.KEY_CHAT_ROOM_INTENT_DATA, intentData);
        //        intent.putExtra(AbsChatRoomActivity.KEY_ENTER_ROOM_AT_ONECE, true);
        //        intent.putExtra(AbsChatRoomActivity.KEY_ROOM_ID, roomId);
        //        intent.putExtra(AbsChatRoomActivity.KEY_ROOM_PASSWORD, password);
        //        intent.putExtra(AbsChatRoomActivity.KEY_ROOM_TITLE, title);
        startActivity(intent);
    }

    private void testComplete() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshListView.onRefreshComplete();
            }
        }, 2000);
    }

    private void initData() {
        channelManager = new ChannelManager(act);
        dataListHelper = new LivePersonalRoomDataListHelper(act);

        setListAdapter();
        getData(1);
    }

    protected void setListAdapter() {
        ArrayList<Room> testList = new ArrayList<Room>();
        roomAdapter = new LiveRoomAdapter(context, testList);
        pullToRefreshListView.setAdapter(roomAdapter);
    }

    private void getData(int page) {
        getData(true, page);
    }

    private void getData(boolean isReadCache, int page) {
        dataListHelper.getData(isReadCache, page, PAGE_SIZE, new DataRequest.DataCallback<List<Room>>() {
            @Override
            public void onSuccess(boolean isAppend, List<Room> data) {
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
                roomAdapter.update(data, isAppend);
            }

            @Override
            public void onFail(ApiException e) {
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
                LogUtils.e("TAG", "error === " + e.getMessage());
            }
        });
        //        channelManager.getLiveChannelDetailsList("", page, PAGE_SIZE, page > 1,
        //                new DataRequest.DataCallback<List<LiveRoomInfo>>() {
        //                    @Override
        //                    public void onSuccess(boolean isAppend, List<LiveRoomInfo> data) {
        //                        pullToRefreshListView.onRefreshComplete();
        //                        roomAdapter.update(data, isAppend);
        //                    }
        //
        //                    @Override
        //                    public void onFail(ApiException e) {
        //                        pullToRefreshListView.onRefreshComplete();
        //                        LogUtils.e("TAG", "error === " + e.getMessage());
        //                    }
        //                });
    }


    private void addBannerheader() {
        listView.addHeaderView(LayoutInflater.from(getActivity()).
                inflate(R.layout.banner_layout, null));
        topBanner = (SimpleImageBanner) listView.findViewById(R.id.simple_img_banner);
        int destheight = (mScreenWidth * 9) / 16;
        AbsListView.LayoutParams params = (AbsListView.LayoutParams) topBanner.getLayoutParams();
        if (params == null) {
            params = new AbsListView.LayoutParams(mScreenWidth, destheight);
        } else {
            params.width = mScreenWidth;
            params.height = destheight;
        }
        topBanner.setLayoutParams(params);
        topBanner.setDelay(4);
        topBanner.setPeriod(4);
        topBanner.setOnItemClickL(new BaseBanner.OnItemClickL() {
            @Override
            public void onItemClick(int position) {
                if (looperDataList != null && position >= 0 &&
                        position < looperDataList.size()) {
                    //                    goDetail(looperDataList.get(position), -1);
                }
            }
        });
        getLooperImageData();
    }

    private HeadlineListManager looperDataRequester;

    private void getLooperImageData() {
        String url = App.getInstance().getmSession().getBaseUrl() +
                "/services/service_main_slideshow" + ".json";
        DataRequest.HttpParams params = new DataRequest.HttpParamsBuilder().
                setUrl(url).setToken(null).build();
        if (looperDataRequester == null) {
            looperDataRequester = new HeadlineListManager(getActivity(), 1 + "", ColumnTYPE.POLITICS_NEWS_TYPE, true);
        }
        looperDataRequester.getData(params, false).setCallback
                (new DataRequest.DataCallback<ArrayList<SocirtyNewsChannel>>() {
                    @Override
                    public void onSuccess(boolean isAppend, ArrayList<SocirtyNewsChannel> data) {
                        looperDataList = data;
                        if (data != null && topBanner != null) {
                            topBanner.setSource(bannerDataHelper.getBannerItems(data)).startScroll();
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {

                    }
                });
    }

    private BannerDataHelper bannerDataHelper = new BannerDataHelper<SocirtyNewsChannel>() {

        @Override
        public BannerItem changeToBannerItem(SocirtyNewsChannel data) {
            BannerItem item = new BannerItem();
            item.imgUrl = data.newsThumb;
            item.title = data.newsTitle;
            return item;
        }
    };
}
