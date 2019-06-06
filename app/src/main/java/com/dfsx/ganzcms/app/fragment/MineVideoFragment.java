package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.view.CustomeProgressDialog;
import com.dfsx.ganzcms.app.view.MyMorePopupwindow;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.LiveBackPlayFullScreenRoomActivity;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.model.BackPlayIntentData;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang on 2017/2/22.
 */
public class MineVideoFragment extends AbsListFragment {
    private final int PAGE_SIZE = 10;
    private MyLiveAdapter adapter;
    private MyDataManager dataManager;
    private ContentCmsApi mContentCmsApi;
    private MyMorePopupwindow popupwindow;
    private EmptyView emptyView;
    private CustomeProgressDialog loading;
    private int page = 1;
    private long mUserId = -1;

    public static MineVideoFragment newInstance(long cId) {
        Bundle bundle = new Bundle();
        bundle.putLong("uID", cId);
        MineVideoFragment fragment = new MineVideoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //     dataManager = new MyDataManager(context);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUserId = bundle.getLong("uID");
        }
        popupwindow = new MyMorePopupwindow(context);
        //  getData();
        emptyView.loadOver();
        pullToRefreshListView.onRefreshComplete();
//        pullToRefreshListView = (PullToRefreshListView) view.
//                findViewById(com.dfsx.lzcms.liveroom.R.id.frag_list_view);
//
//        emptyLayoutContainer = (LinearLayout) view.
//                findViewById(com.dfsx.lzcms.liveroom.R.id.empty_layout);
//        listView = pullToRefreshListView.getRefreshableView();
//
//        pullToRefreshListView.setMode(getListViewMode());
//
//        pullToRefreshListView.setOnRefreshListener(this);
//
//        setListAdapter(listView);
//        listView.setEmptyView(emptyLayoutContainer);
//        setEmptyLayout(emptyLayoutContainer);

//        popupwindow.setClickListener(new MyMorePopupwindow.OnMoreClickListener() {
//            @Override
//            public void onShare(SharePlatform platform, Object tag) {
//                if (tag instanceof PlayBackInfo) {
//                    PlayBackInfo backInfo = (PlayBackInfo) tag;
//                    onSharePlatfrom(platform, backInfo);
//                }
//            }
//
//            @Override
//            public void onDelete(final Object tag) {
//                loading = CustomeProgressDialog.show(context, "加载中...");
//                if (tag instanceof PlayBackInfo) {
//                    dispatchBackInfo = (PlayBackInfo) tag;
//                }
//                if (dispatchBackInfo == null) {
//                    return;
//                }
//                dataManager.deleteMyBackPlay(dispatchBackInfo.getId(), new DataRequest.DataCallback<Boolean>() {
//                    @Override
//                    public void onSuccess(boolean isAppend, Boolean data) {
//                        if (adapter != null && data) {
//                            adapter.getData().remove(dispatchBackInfo);
//                            adapter.notifyDataSetChanged();
//                        } else {
//                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
//                        }
//                        if (loading != null) {
//                            loading.dismiss();
//                        }
//                    }
//
//                    @Override
//                    public void onFail(ApiException e) {
//                        e.printStackTrace();
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        if (loading != null) {
//                            loading.dismiss();
//                        }
//                    }
//                });
//            }
//        });、
        mContentCmsApi = new ContentCmsApi(getActivity());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                List<MyVideoItem> list = adapter.getData();
                if (position >= 0 && position < list.size()) {
                    MyVideoItem info = list.get(position);
                    if (info != null) {
//                        if (!TextUtils.isEmpty(info.getVideoUrl()))
                        if (info.getVersions() != null && info.getVersions().size() > 0) {
                            Intent intent = new Intent("android.intent.action.VIEW");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("oneshot", 0);
                            intent.putExtra("configchange", 0);
                            Uri uri = Uri.parse(info.getVersions().get(0).getUrl());
                            intent.setDataAndType(uri, "video/*");
                        }
                    }
                }
            }
        });
        if (mUserId != -1)
            getData(1);
    }

    private void onSharePlatfrom(SharePlatform platform, PlayBackInfo backInfo) {
        ShareContent content = new ShareContent();
        content.title = StringUtil.getLiveShareTitle();
        content.disc = StringUtil.getLiveShareContent(backInfo.getOwnerNickname(), backInfo.getRoomTitle());
        content.thumb = backInfo.getThumbUrl();
        content.type = ShareContent.UrlType.WebPage;
        content.url = App.getInstance().getmSession().getBaseShareBackplayUrl() + backInfo.getId();
        AbsShare share = ShareFactory.createShare(context, platform);
        share.share(content);
    }

    private void goBackPlayRoom(PlayBackInfo backInfo) {
        Intent intent = new Intent();
        intent.setClass(context, LiveBackPlayFullScreenRoomActivity.class);
        BackPlayIntentData intentData = new BackPlayIntentData();
        intent.putExtra(AbsChatRoomActivity.KEY_CHAT_ROOM_INTENT_DATA, intentData);
        intentData.setAutoJoinRoomAtOnce(true);
        intentData.setRoomId(backInfo.getChannelId());
        intentData.setRoomTitle(backInfo.getRoomTitle());
        intentData.setFullScreenVideoImagePath(backInfo.getRoomImagePath());
        intentData.setBackPlayId(backInfo.getId());
        intentData.setRoomOwnerId(backInfo.getOwnerId());
        intentData.setRoomOwnerAccountName(backInfo.getOwnerUsername());
        intentData.setRoomOwnerNickName(backInfo.getOwnerNickname());
        intentData.setRoomOwnerLogo(backInfo.getOwnerAvatarUrl());
        intentData.setRoomTotalCoins(backInfo.getTotalCoins());
        intentData.setMemberSize((int) backInfo.getPlayTimes());
        intentData.setLiveType(backInfo.getRoomLivetype() == LiveType.EventLive ? LiveBackPlayFullScreenRoomActivity.TYPE_LIVE_GUESS :
                LiveBackPlayFullScreenRoomActivity.TYPE_LIVE_SHOW);
        intentData.setShowId(backInfo.getShowId());

        startActivity(intent);
    }

    @Override
    public void setListAdapter(ListView listView) {
        if (adapter == null) {
            adapter = new MyLiveAdapter(context);
        }
        listView.setAdapter(adapter);
    }

    @Override
    protected void setEmptyLayout(final LinearLayout container) {
        emptyView = new EmptyView(context);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        emptyView.setLayoutParams(p);

        container.addView(emptyView);
        View emptyLayout = LayoutInflater.from(context).
                inflate(R.layout.no_my_live_layout, null);

        emptyView.setLoadOverView(emptyLayout);
        emptyView.loading();
        Button btnStart = (Button) emptyLayout.findViewById(R.id.btn_start_live);
        btnStart.setText("开始上传视频");

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
                DefaultFragmentActivity.start(getActivity(), FileFragment.class.getName());
//                Intent intent = new Intent(context, PrepareLiveActivity.class);
//                startActivity(intent);
            }
        });
    }


    private DataFileCacheManager<ArrayList<MyVideoItem>> dataFileCacheManager = new
            DataFileCacheManager<ArrayList<MyVideoItem>>
                    (App.getInstance().getApplicationContext(),
                            1 + "", App.getInstance().getPackageName() + "MineVideoragment.txt") {
                @Override
                public ArrayList<MyVideoItem> jsonToBean(JSONObject jsonObject) {
                    ArrayList<MyVideoItem> socityNewsAarry = null;
                    try {
                        JSONArray result = jsonObject.getJSONArray("data");
                        if (result != null) {
                            socityNewsAarry = new ArrayList<MyVideoItem>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                MyVideoItem entry = new Gson().fromJson(item.toString(), MyVideoItem.class);
                                if (entry.getVersions() != null && entry.getVersions().size() > 0) {
                                    for (MyVideoItem.VersionsBean item1 : entry.getVersions()) {
                                        String subiffx = item1.getUrl();
                                        if (TextUtils.equals(subiffx, ".mp4")) {
                                            entry.setVideoUrl(item1.getUrl());
                                        }
                                    }
                                }
                                socityNewsAarry.add(entry);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return socityNewsAarry;
                }
            };


    protected void getMoreData() {
        page++;
        getData(page);
    }

    private void getData(int page) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/users/" + mUserId + "/videos?";
        url += "page=" + page + "";
        dataFileCacheManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), page > 1).setCallback(callback);
//        getBackList();
//               dataManager.getMyChannel(liveRoomCallback);
    }

    private DataRequest.DataCallback<ArrayList<MyVideoItem>> callback = new DataRequest.DataCallback<ArrayList<MyVideoItem>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<MyVideoItem> data) {
            emptyView.loadOver();
            pullToRefreshListView.onRefreshComplete();
            if (adapter != null) {
                adapter.update(data, isAppend);
            }
        }

        @Override
        public void onFail(ApiException e) {
            emptyView.loadOver();
            pullToRefreshListView.onRefreshComplete();
            Log.e("TAG", "error == " + e.getMessage());
        }
    };

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData(1);
        pullToRefreshListView.onRefreshComplete();
    }


    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getMoreData();
        pullToRefreshListView.onRefreshComplete();
    }

//    private void getBackList() {
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
//    }

    protected class MyLiveAdapter extends BaseListViewAdapter<MyVideoItem> {

        public MyLiveAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.news_video_list_hsrocll_item;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, final int position) {
            ImageView thumbnailImageView = holder.getView(R.id.item_img);
            TextView titleTextView = holder.getView(R.id.news_list_item_title);
            TextView contentTextView = holder.getView(R.id.item_autor);
            TextView ctimeTextView = holder.getView(R.id.item_create_time);
            FrameLayout videoContainer = holder.getView(R.id.video_container);
            ImageView viewImg = (ImageView) holder.getView(R.id.comglay_btn);
            TextView viewTxt = (TextView) holder.getView(R.id.item_commeanuder_tx);
            viewImg.setVisibility(View.GONE);
            viewTxt.setVisibility(View.GONE);

            MyVideoItem roomInfo = list.get(position);
            titleTextView.setText(roomInfo.getTitle());
//            userType.setText(roomInfo.getCategoryName());
//            numText.setText(roomInfo.getPlayTimes() + "");
            ctimeTextView.setText(StringUtil.getTimeText(roomInfo.getCreation_time()));
            GlideImgManager.getInstance().
                    showImg(context, thumbnailImageView, roomInfo.getCover_url());
//            more.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (popupwindow != null) {
//                        popupwindow.setTag(list.get(position));
//                        popupwindow.show(v);
//                    }
//                }
//            });
        }
    }
}
