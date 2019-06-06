package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.business.UserTradeLogHelper;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by  heyang  2018/1/5
 */
public class TvChannerSelectFragment extends AbsListFragment {

    public static final int PAGE_SIZE = 20;
    private ChannelAdapter adapter;
    private UserTradeLogHelper tradeLogHelper;
    private int pageCount = 1;
    private EmptyView emptyView;
    private long id;
    private ContentCmsApi contentCmsApi;
    private int nCurrent = -1;
    private int nPreIndex = -1;
    private long defaultColId = -1;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            defaultColId = getArguments().getLong(DefaultFragmentActivity.KEY_FRAGMENT_PARAM);
        }
        contentCmsApi = new ContentCmsApi(getContext());
        initData();
    }

    public void initData() {
        ArrayList<LiveEntity.LiveChannel> list = ColumnBasicListManager.getInstance().getLiveTvChannelMap();
        if (defaultColId != -1 && list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                LiveEntity.LiveChannel channel = list.get(i);
                if (channel.channelID == defaultColId) {
                    channel.isLive = true;
                    nCurrent = i;
                    nPreIndex = i;
                } else {
                    channel.isLive = false;
                }
            }
        }
        adapter.update(list, false);
    }

    @Override
    public void setListAdapter(final ListView listView) {
        if (adapter == null) {
            adapter = new ChannelAdapter(context);
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= listView.getHeaderViewsCount();
                if (adapter != null && position >= 0 && position < adapter.getCount()) {
                    LiveEntity.LiveChannel itemData = adapter.get(position);
                    if (itemData != null) {
                        ColumnBasicListManager.getInstance().setSelectliveTvChanne(itemData);
//                        if (nPreIndex != -1) {
//                            LiveEntity.LiveChannel preDate = adapter.get(nPreIndex);
//                            preDate.isLive = false;
//                        }
//                        itemData.isLive = true;
//                        adapter.notifyDataSetChanged();
                    }
                }
                getActivity().finish();
                nCurrent = position;
                nPreIndex = position;
            }
        });
        pullToRefreshListView.setBackgroundColor(Color.WHITE);
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageCount = 1;
        getTvlistData(false);
//        pullToRefreshListView.onRefreshComplete();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pageCount++;
        getTvlistData(true);
//        pullToRefreshListView.onRefreshComplete();
    }

    private DataFileCacheManager<ArrayList<ContentCmsEntry>> dataTvRequest =
            new DataFileCacheManager<ArrayList<ContentCmsEntry>>(App.getInstance().getApplicationContext(),
                    "456" + id, App.getInstance().getPackageName() + "tvchannaelSel") {
                @Override
                public ArrayList<ContentCmsEntry> jsonToBean(JSONObject json) {
                    ArrayList<ContentCmsEntry> dlist = null;
                    try {
                        if (json != null && !TextUtils.isEmpty(json.toString())) {
                            JSONArray attr = json.optJSONArray("data");
                            if (attr != null && attr.length() > 0) {
                                dlist = new ArrayList<ContentCmsEntry>();
                                for (int i = 0; i < attr.length(); i++) {
                                    JSONObject obj = (JSONObject) attr.get(i);
                                    ContentCmsEntry entry = new Gson().fromJson(obj.toString(), ContentCmsEntry.class);
                                    if (!TextUtils.equals(entry.getType(), "live")) continue;
//                                    if (entry.getShowType() != 3) continue;
                                    dlist.add(entry);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return dlist;
                }
            };

    private void getTvlistData(boolean bAddTail) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/" + id + "/contents?";
        url += "page=" + pageCount;
        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.GET).
                setToken(App.getInstance().getCurrentToken()).build();
        dataTvRequest.getData(httpParams, bAddTail).
                setCallback(new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
                    @Override
                    public void onSuccess(final boolean isAppend, final ArrayList<ContentCmsEntry> liveChannelsAarry) {
                        pullToRefreshListView.onRefreshComplete();
                        if (liveChannelsAarry != null && !liveChannelsAarry.isEmpty()) {
                            Observable.from(liveChannelsAarry)
                                    .subscribeOn(Schedulers.io())
                                    .map(new Func1<ContentCmsEntry, ContentCmsInfoEntry>() {
                                        @Override
                                        public ContentCmsInfoEntry call(ContentCmsEntry topicalEntry) {
                                            ContentCmsInfoEntry info = contentCmsApi.getEnteyFromJson(topicalEntry.getId());
                                            return info;
                                        }
                                    })
                                    .toList()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<List<ContentCmsInfoEntry>>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            e.printStackTrace();
                                            //                                            if ((dataLoading != null) && (dataLoading.isShowing()))
                                            //                                                dataLoading.dismiss();
                                        }

                                        @Override
                                        public void onNext(List<ContentCmsInfoEntry> data) {
                                            if (data != null && !data.isEmpty()) {
                                                ArrayList<LiveEntity.LiveChannel> list = new ArrayList<>();
                                                for (int i = 0; i < data.size(); i++) {
                                                    ContentCmsInfoEntry enytry = data.get(i);
                                                    String thummb = "";
                                                    if (enytry.getThumbnail_urls() != null && !enytry.getThumbnail_urls().isEmpty())
                                                        thummb = enytry.getThumbnail_urls().get(0).toString();
                                                    LiveEntity.LiveChannel channel = new LiveEntity.LiveChannel(enytry.getLiveId(), enytry.getTitle(), enytry.getSummary(), enytry.getUrl(), thummb, "rfequency");
//                                                    columlist.add(channel);
                                                    list.add(channel);
                                                }
                                                adapter.update(list, isAppend);
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        pullToRefreshListView.onRefreshComplete();
                    }
                });
    }

    private Fragment getRootFragment() {
        Fragment fragment = getParentFragment();
        if (fragment == null) return null;
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment;
    }

    public void setResult() {
        if (nCurrent != -1 && nCurrent < adapter.getCount()) {
            LiveEntity.LiveChannel chal = adapter.get(nCurrent);
            if (chal != null) {
                ColumnBasicListManager.getInstance().setSelectliveTvChanne(chal);
            }
        }
        getActivity().finish();
    }

//    private void getData(int page) {
//        tradeLogHelper.getTradeRecordList(page, PAGE_SIZE, new DataRequest.DataCallback<TradeRecords>() {
//            @Override
//            public void onSuccess(boolean isAppend, TradeRecords data) {
//                emptyView.loadOver();
//                if (adapter != null && data != null) {
//                    adapter.update(data.getData(), isAppend);
//                }
//                pullToRefreshListView.onRefreshComplete();
//            }
//
//            @Override
//            public void onFail(ApiException e) {
//                pullToRefreshListView.onRefreshComplete();
//                emptyView.loadOver();
//                LogUtils.e("TAG", "error === " + e.getMessage());
//            }
//        });
//    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("没有数据");
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        container.addView(emptyView);
    }

    @Override
    protected void setTopView(FrameLayout topListViewContainer) {
        super.setTopView(topListViewContainer);
        LinearLayout topView = new LinearLayout(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Util.dp2px(getActivity(), 70));
//        p.setMargins(Util.dp2px(context,21),24,0,24);
        topView.setLayoutParams(p);
        topView.setBackgroundColor(0xffffffff);
        topListViewContainer.addView(topView, p);
        TextView topTitle = new TextView(getActivity());
        topTitle.setPadding(Util.dp2px(context, 21), 24, 0, 24);
        topView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        topTitle.setText("选择频道");
        topTitle.setTextColor(0xff333333);
        topTitle.setTextSize(18);
        topView.addView(topTitle);
    }

    @Override
    protected void setBottomView(FrameLayout topListViewContainer) {
        super.setBottomView(topListViewContainer);
        RelativeLayout bottomView = new RelativeLayout(context);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                Util.dp2px(context, 70));
        p.setMargins(0, Util.dp2px(context, 24), 0, Util.dp2px(context, 24));
        bottomView.setBackgroundColor(0xffffffff);
        bottomView.setLayoutParams(p);
        bottomView.setGravity(Gravity.CENTER);
        topListViewContainer.addView(bottomView, p);
        ImageView close = new ImageView(getActivity());
        close.setImageResource(R.drawable.tv_channe_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nCurrent != -1 && nCurrent < adapter.getCount()) {
                    LiveEntity.LiveChannel chal = adapter.get(nCurrent);
                    if (chal != null) {
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("key", chal);
//                        Intent intent = new Intent();
//                        intent.putExtras(bundle);
//                        getRootFragment().getActivity().setResult(2, intent);
//                        getActivity().setResult(2, intent);
                        ColumnBasicListManager.getInstance().setSelectliveTvChanne(chal);
                    }
                }
                getActivity().finish();
            }
        });
        bottomView.addView(close);
    }

    class ChannelAdapter extends BaseListViewAdapter<LiveEntity.LiveChannel> {

        public ChannelAdapter(Context context) {
            super(context);
        }

        public LiveEntity.LiveChannel get(int pos) {
            LiveEntity.LiveChannel entry = null;
            if (list != null && pos < list.size())
                entry = list.get(pos);
            return entry;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.live_program_channel_item_list;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            TextView title = holder.getView(R.id.tv_item_title_tx);
            ImageView selBtn = holder.getView(R.id.channerl_select_btn);
            LiveEntity.LiveChannel record = list.get(position);
            title.setText(record.channelName);
            if (record.isLive) {
                selBtn.setImageResource(R.drawable.tv_channel_sel);
            } else {
                selBtn.setImageResource(R.drawable.tv_channel_normal);
            }
        }
    }

}
