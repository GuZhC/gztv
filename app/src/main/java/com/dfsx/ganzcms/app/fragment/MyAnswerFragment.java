package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.CvideoPlayAct;
import com.dfsx.ganzcms.app.act.QuestionInfoAct;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.view.CustomeProgressDialog;
import com.dfsx.ganzcms.app.view.MyMorePopupwindow;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.LiveBackPlayFullScreenRoomActivity;
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
 * Created by heyang on 2017/7/21
 * 回答  页面
 */
public class MyAnswerFragment extends AbsListFragment {
    private MyLiveAdapter adapter;
    private MyDataManager dataManager;
    private ContentCmsApi mContentCmsApi;
    private MyMorePopupwindow popupwindow;
    private EmptyView emptyView;
    private CustomeProgressDialog loading;
    private int page = 1;
    private long mUserId = -1;

    public static MyAnswerFragment newInstance(long cId) {
        Bundle bundle = new Bundle();
        bundle.putLong("uID", cId);
        MyAnswerFragment fragment = new MyAnswerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUserId = bundle.getLong("uID");
        }
        popupwindow = new MyMorePopupwindow(context);
        emptyView.loadOver();
        pullToRefreshListView.onRefreshComplete();
        mContentCmsApi = new ContentCmsApi(getActivity());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                List<ContentCmsEntry> list = adapter.getData();
                if (position >= 0 && position < list.size()) {
                    ContentCmsEntry info = list.get(position);
                    if (info != null) {
                        Intent intent = new Intent(getActivity(), QuestionInfoAct.class);
                        intent.putExtra("tid", info.getId());
                        startActivity(intent);
                    }
                }
            }
        });
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
                inflate(R.layout.no_question_attion_layout, null);

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

    private DataFileCacheManager<ArrayList<ContentCmsEntry>> dataFileCacheManager = new
            DataFileCacheManager<ArrayList<ContentCmsEntry>>
                    (App.getInstance().getApplicationContext(),
                            1 + "", App.getInstance().getPackageName() + "MineVideoragment.txt") {
                @Override
                public ArrayList<ContentCmsEntry> jsonToBean(JSONObject jsonObject) {
                    ArrayList<ContentCmsEntry> socityNewsAarry = null;
                    try {
                        JSONArray result = jsonObject.getJSONArray("data");
                        if (result != null) {
                            socityNewsAarry = new ArrayList<ContentCmsEntry>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                ContentCmsEntry entry = new Gson().fromJson(item.toString(), ContentCmsEntry.class);
//                               //判断是不是多图
                                entry.setShowType(entry.getShowType());
                                if (entry.getShowType() == 1) {
                                    if (entry.getThumbnail_urls().size() >= 3) entry.setShowType(4);
                                    if (entry.getThumbnail_urls() == null || entry.getThumbnail_urls().size() == 0)
                                        entry.setShowType(5);
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
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/29/contents?";
        url += "page=" + page + "";
        dataFileCacheManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), page > 0).setCallback(callback);
    }

    private DataRequest.DataCallback<ArrayList<ContentCmsEntry>> callback = new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<ContentCmsEntry> data) {
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
        //   pullToRefreshListView.onRefreshComplete();
    }


    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getMoreData();
        //    pullToRefreshListView.onRefreshComplete();
    }

    public class MyLiveAdapter extends BaseAdapter {
        private static final int TYPE_NEWS = 0;
        private static final int TYPE_VIDEO = 1;
        private static final int TYPE_GROUP = 3;
        private static final int TYPE_ACTIVIRY = 2;
        private static final int TYPE_COUNT = 4;

        private final String STATE_LIST = "ListAdapter.mlist";
        private ArrayList<ContentCmsEntry> items = new ArrayList<ContentCmsEntry>();
        private LayoutInflater inflater;
        public boolean bInit;
        Context mContext = null;

        int playVideoIndex = -1;   //记录播放器的位置

        public MyLiveAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            bInit = false;
            this.mContext = context;
        }

        public void init(Bundle savedInstanceState) {
            ArrayList<ContentCmsEntry> sList;
            sList = (ArrayList<ContentCmsEntry>) savedInstanceState.getSerializable(STATE_LIST);
            if (sList != null) {
                items = sList;
                notifyDataSetChanged();
                bInit = true;
            }
        }

        public ArrayList<ContentCmsEntry> getData() {
            return items;
        }

        public void SetInitStatus(boolean flag) {
            bInit = flag;
        }

        public long getMinId() {
            return items.isEmpty() ? -1 : items.get(items.size() - 1).getId();
        }

        public long getMaxId() {
            return items.isEmpty() ? -1 : items.get(0).getId();
        }

        public void saveInstanceState(Bundle outState) {
            if (bInit) {
                outState.putSerializable(STATE_LIST, items);
            }
        }

        public boolean isInited() {
            return bInit;
        }

        public void update(ArrayList<ContentCmsEntry> data, boolean bAddTail) {
            if (data != null && !data.isEmpty()) {
                boolean noData = false;
                if (bAddTail)
                    /*
                    if(items.size() >= data.size()
                            && items.get(items.size() - data.size()).id == data.get(0).id)*/
                    if (items.size() >= data.size()
                            && items.get(items.size() - 1).getId() == data.get(data.size() - 1).getId())
                        noData = true;
                    else
                        items.addAll(data);
                else {
                    if (items != null) {
                        if (/*items.size() == data.size() && */
                                items.size() > 0 &&
                                        items.get(0).getId() == data.get(0).getId())
                            noData = false;
                    }
                    if (!noData) {
                        items = data;
                    }
                }
                bInit = true;
                if (!noData)
                    notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (context == null) {
                context = parent.getContext();
            }
            BaseViewHodler viewHodler = BaseViewHodler.
                    get(convertView, parent, getViewId(), position);
            setViewHolderData(viewHodler, position);
            return viewHodler.getConvertView();
        }

        protected int getViewId() {
            return R.layout.my_answer_item;
        }

        protected void setViewHolderData(BaseViewHodler holder, int position) {
            TextView itemTiltle = holder.getView(R.id.answer_list_item_title);
            TextView itemnumber = holder.getView(R.id.answer_list_item_number);
            TextView itemOperaton = holder.getView(R.id.answer_list_item_oper);
            ContentCmsEntry channel = items.get(position);
            itemTiltle.setText(channel.getTitle());
            itemnumber.setText("23" + "个回答");
        }

        private void goDetail(ContentCmsInfoEntry channel) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putLong("index", channel.getId());
            if (channel.getThumbnail_urls() != null && channel.getThumbnail_urls().size() > 0)
                bundle.putString("posterPath", channel.getThumbnail_urls().get(0));
            intent.setClass(mContext, CvideoPlayAct.class);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }

        public boolean remove(int position) {
            if (position < items.size()) {
                items.remove(position);
                return true;
            }
            return false;
        }

        /**
         * 添加列表项
         *
         * @param item
         */
        public void addItem(ContentCmsEntry item) {
            items.add(item);
        }

        /**
         * 添加指定列
         *
         * @param item
         */
        public void addItemByIndex(ContentCmsEntry item, int index) {
            items.add(index, item);
        }
    }
}
