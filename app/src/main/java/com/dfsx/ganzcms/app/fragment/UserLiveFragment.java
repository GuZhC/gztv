package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.adapter.BaseRecyclerViewAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.PrepareLiveActivity;
import com.dfsx.ganzcms.app.adapter.IRoomAdapterMoreSettingClickListener;
import com.dfsx.ganzcms.app.adapter.UserLiveRoomAdapter;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.business.UserLiveDataHelper;
import com.dfsx.ganzcms.app.model.LiveInfo;
import com.dfsx.ganzcms.app.model.LiveType;
import com.dfsx.ganzcms.app.model.PlayBackInfo;
import com.dfsx.ganzcms.app.model.Room;
import com.dfsx.ganzcms.app.view.CustomeProgressDialog;
import com.dfsx.ganzcms.app.view.MyMorePopupwindow;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.LiveBackPlayFullScreenRoomActivity;
import com.dfsx.lzcms.liveroom.LiveFullScreenRoomActivity;
import com.dfsx.lzcms.liveroom.LiveServiceRoomActivity;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.model.BackPlayIntentData;
import com.dfsx.lzcms.liveroom.model.ChatRoomIntentData;
import com.dfsx.lzcms.liveroom.model.FullScreenRoomIntentData;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.List;

/**
 * 用户的直播的直播
 * 包含我的。
 * Created by liuwb on 2016/10/26.
 */
public class UserLiveFragment extends AbsPullRecyclerViewFragment {

    private static final String BUNDLE_USER_ID = "UserLiveFragment.user_id";

    private final int PAGE_SIZE = 10;
    private UserLiveRoomAdapter adapter;
    private MyDataManager dataManager;

    private MyMorePopupwindow popupwindow;

    private SharePopupwindow sharePopupwindow;

    private EmptyView emptyView;
    private CustomeProgressDialog loading;
    private int page = 1;

    private LiveInfo dispatchBackInfo = null;

    private UserLiveDataHelper dataHelper;

    private long userId = 0;

    //    private RecyclerItemClickListener recyclerItemClickListener;

    public static UserLiveFragment newInstance(long userId) {
        UserLiveFragment fragment = new UserLiveFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_USER_ID, userId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            userId = getArguments().getLong(BUNDLE_USER_ID, 0L);
        }
        if (userId == 0L) {
            long defaultId = App.getInstance().getUser() != null &&
                    App.getInstance().getUser().getUser() != null ?
                    App.getInstance().getUser().getUser().getId() :
                    0L;
            userId = defaultId;
        }
        super.onViewCreated(view, savedInstanceState);
        dataManager = new MyDataManager(context);
        dataHelper = new UserLiveDataHelper(context);
        popupwindow = new MyMorePopupwindow(context);
        page = 1;
        getData();
        if (getParentFragment() != null && getParentFragment() instanceof PullToRefreshRecyclerView.PullRecyclerHelper)
            pullToRefreshRecyclerView.setPullRecyclerHelper((PullToRefreshRecyclerView.PullRecyclerHelper) getParentFragment());
        popupwindow.setClickListener(new MyMorePopupwindow.OnMoreClickListener1() {
            @Override
            public void onSetNoBackPlay(Object tag) {
                loading = CustomeProgressDialog.show(context, "加载中...");
                if (tag instanceof LiveInfo) {
                    dispatchBackInfo = (LiveInfo) tag;
                }
                if (dispatchBackInfo == null) {
                    return;
                }
                boolean setIsPrivacy = !dispatchBackInfo.isPrivacy();
                dataManager.setMyBackPlayPrivacy(dispatchBackInfo.getId(), setIsPrivacy, new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        dispatchBackInfo.setPrivacy(!dispatchBackInfo.isPrivacy());
                        adapter.notifyDataSetChanged();
                        if (loading != null) {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        if (loading != null) {
                            loading.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onShare(SharePlatform platform, Object tag) {
                if (tag instanceof LiveInfo) {
                    LiveInfo backInfo = (LiveInfo) tag;
                    onSharePlatfrom(platform, backInfo);
                }
            }

            @Override
            public void onDelete(final Object tag) {
                loading = CustomeProgressDialog.show(context, "加载中...");
                if (tag instanceof LiveInfo) {
                    dispatchBackInfo = (LiveInfo) tag;
                }
                if (dispatchBackInfo == null) {
                    return;
                }
                dataManager.deleteMyBackPlay(dispatchBackInfo.getId(), new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        if (adapter != null && data) {
                            adapter.getData().remove(dispatchBackInfo);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                        if (loading != null) {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        if (loading != null) {
                            loading.dismiss();
                        }
                    }
                });
            }
        });

        //        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //            @Override
        //            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //                position = position - listView.getHeaderViewsCount();
        //                List<Room> list = adapter.getData();
        //                if (position >= 0 && position < list.size()) {
        //                    Room itemData = list.get(position);
        //                    goLiveRoom(itemData);
        //                }
        //            }
        //        });
        //        recyclerItemClickListener = new RecyclerItemClickListener(context);
        //        recyclerItemClickListener.setOnItemClickListener(new RecyclerItemClickListener.OnItemClickListener() {
        //            @Override
        //            public void onItemClick(View view, int position, float x, float y) {
        //                List<Room> list = adapter.getData();
        //                if (list != null && position >= 0 && position < list.size()) {
        //                    Room itemData = list.get(position);
        //                    goLiveRoom(itemData);
        //                }
        //            }
        //        });
        //        recyclerView.addOnItemTouchListener(recyclerItemClickListener);
    }

    @Override
    public BaseRecyclerViewAdapter getRecyclerViewAdapter() {
        if (adapter == null) {
            UserLiveRoomAdapter adapter1 = new UserLiveRoomAdapter(context, null);
            adapter1.setOnItemViewClickListener(new BaseRecyclerViewHolder.OnRecyclerItemViewClickListener() {
                @Override
                public void onItemViewClick(View v) {
                    try {
                        int position = (int) v.getTag();
                        List<Room> list = adapter.getData();
                        if (list != null && position >= 0 && position < list.size()) {
                            Room itemData = list.get(position);
                            goLiveRoom(itemData);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            adapter = adapter1;
            setListAdapter();
            setEmptyLayout();
        }
        return adapter;
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

    /**
     * 进入房间
     *
     * @param roomInfo
     */
    private void goLiveRoom(Room roomInfo) {
        if (roomInfo.getRoomFlag() == Room.FLAG_BACK_PLAY) {//回放
            LiveInfo backInfo = (LiveInfo) roomInfo;
            goBackPlayRoom(LiveBackPlayFullScreenRoomActivity.class,
                    backInfo);
        } else {
            if (roomInfo.getRoomLivetype() == LiveType.EventLive) {
                Intent intent = new Intent(context, LiveServiceRoomActivity.class);
                ChatRoomIntentData intentData = new ChatRoomIntentData();
                intentData.setRoomId(roomInfo.getRoomId());
                intentData.setRoomPassword("");
                intentData.setRoomTitle(roomInfo.getRoomTitle());
                goLiveRoom(intent, intentData);
            } else {
                Intent intent = new Intent(context, LiveFullScreenRoomActivity.class);
                LiveInfo liveInfo = (LiveInfo) roomInfo;
                FullScreenRoomIntentData intentData = new FullScreenRoomIntentData();
                intentData.setRoomId(roomInfo.getRoomId());
                intentData.setRoomPassword("");
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

    protected void onlySharePopupwindowShow(View v, final LiveInfo playBackInfo) {
        if (sharePopupwindow == null) {
            sharePopupwindow = new SharePopupwindow(getActivity());
            sharePopupwindow.setOnShareClickListener(new SharePopupwindow.OnShareClickListener() {
                @Override
                public void onShareClick(View v) {
                    int vId = v.getId();
                    if (vId == R.id.share_qq) {
                        onSharePlatfrom(SharePlatform.QQ, playBackInfo);
                    } else if (vId == R.id.share_wb) {
                        onSharePlatfrom(SharePlatform.WeiBo, playBackInfo);
                    } else if (vId == R.id.share_wx) {
                        onSharePlatfrom(SharePlatform.Wechat, playBackInfo);
                    } else if (vId == R.id.share_wxfriends) {
                        onSharePlatfrom(SharePlatform.Wechat_FRIENDS, playBackInfo);
                    }
                }
            });
        }
        sharePopupwindow.show(v);
    }

    private void onSharePlatfrom(SharePlatform platform, LiveInfo info) {
        ShareContent content = new ShareContent();
        content.title = info.getTitle();
        content.disc = StringUtil.getLiveShareContent(info.getOwnerNickName(),
                info.getRoomTitle());
        content.thumb = info.getCoverUrl();
        content.type = ShareContent.UrlType.WebPage;
        String shareApi = info.getRoomLivetype() == LiveType.PersonalLive ? "/live/personal/"
                : "/live/activity/";
        content.url = App.getInstance().getmSession().getBaseMobileWebUrl() + shareApi + info.getId();
        AbsShare share = ShareFactory.createShare(context, platform);
        share.share(content);
    }


    public void setListAdapter() {
        adapter.setIRoomAdapterMoreSettingClickListener(new IRoomAdapterMoreSettingClickListener() {
            @Override
            public void onMoreSettingClick(View v, Room room) {
                if (isMine(room.getRoomOwnerId())) {
                    if (popupwindow != null) {
                        popupwindow.setTag(room);
                        popupwindow.show(v);
                    }
                } else {
                    if (room instanceof LiveInfo) {
                        onlySharePopupwindowShow(v, (LiveInfo) room);
                    }
                }
            }
        });
    }

    private boolean isMine(long userId) {
        return App.getInstance().getUser() != null &&
                App.getInstance().getUser().getUser() != null &&
                App.getInstance().getUser().getUser().getId() == userId;
    }

    //    @Override
    //    protected PullToRefreshBase.Mode getListViewMode() {
    //        return PullToRefreshBase.Mode.BOTH;
    //    }

    protected void setEmptyLayout() {
        emptyView = new EmptyView(context);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        emptyView.setLayoutParams(p);
        View emptyLayout = LayoutInflater.from(context).
                inflate(R.layout.no_my_live_layout, null);

        emptyView.setLoadOverView(emptyLayout);
        emptyView.loading();
        Button btnStart = (Button) emptyLayout.findViewById(R.id.btn_start_live);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PrepareLiveActivity.class);
                startActivity(intent);
            }
        });

        adapter.setEmptyView(emptyView);
    }


    private void getData() {
        dataHelper.getData(userId, page, PAGE_SIZE, new DataRequest.DataCallback<List<Room>>() {
            @Override
            public void onSuccess(boolean isAppend, List<Room> data) {
                emptyView.loadOver();
                pullToRefreshRecyclerView.onRefreshComplete();
                if (data != null) {
                    adapter.update(data, isAppend);
                }
            }

            @Override
            public void onFail(ApiException e) {
                emptyView.loadOver();
                pullToRefreshRecyclerView.onRefreshComplete();
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        page++;
        getData();
    }

    private void getBackList() {
        //        dataManager.getMyBackPlay(page, PAGE_SIZE, new DataRequest.DataCallback<PlayBackListInfo>() {
        //            @Override
        //            public void onSuccess(boolean isAppend, PlayBackListInfo data) {
        //                emptyView.loadOver();
        //                pullToRefreshListView.onRefreshComplete();
        //                if (data != null) {
        //                    List<PlayBackInfo> list = data.getData();
        //                    if (list == null) {
        //                        list = new ArrayList<PlayBackInfo>();
        //                    }
        //                    adapter.update(list, isAppend);
        //                }
        //            }
        //
        //            @Override
        //            public void onFail(ApiException e) {
        //                emptyView.loadOver();
        //                pullToRefreshListView.onRefreshComplete();
        //                e.printStackTrace();
        //            }
        //        });
    }

    protected class MyLiveAdapter extends BaseListViewAdapter<PlayBackInfo> {

        public MyLiveAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_my_live_channel;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, final int position) {
            TextView title = holder.getView(R.id.title);
            TextView more = holder.getView(R.id.text_more);
            TextView backPlay = holder.getView(R.id.back_play);
            TextView userType = holder.getView(R.id.user_type_text);
            TextView numText = holder.getView(R.id.num_text);
            TextView timeText = holder.getView(R.id.time_text);
            ImageView roomImage = holder.getView(R.id.channel_img);

            PlayBackInfo roomInfo = list.get(position);
            title.setText(roomInfo.getTitle());
            userType.setText(roomInfo.getCategoryName());
            numText.setText(roomInfo.getPlayTimes() + "");
            timeText.setText(StringUtil.getTimeText(roomInfo.getCreationTime()));

            GlideImgManager.getInstance().
                    showImg(context, roomImage, roomInfo.getRoomImagePath());

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupwindow != null) {
                        popupwindow.setTag(list.get(position));
                        popupwindow.show(v);
                    }
                }
            });
        }
    }
}
