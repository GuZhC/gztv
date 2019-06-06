package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.banner.BannerDataHelper;
import com.dfsx.core.common.view.banner.BannerItem;
import com.dfsx.core.common.view.banner.BaseBanner;
import com.dfsx.core.common.view.banner.SimpleImageBanner;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.ActGanziActivity;
import com.dfsx.ganzcms.app.act.GanZiTopBarActivity;
import com.dfsx.ganzcms.app.business.CommuntyDatailHelper;
import com.dfsx.ganzcms.app.business.HeadlineListManager;
import com.dfsx.ganzcms.app.business.TopicListManager;
import com.dfsx.ganzcms.app.business.TopicalApi;
import com.dfsx.ganzcms.app.model.Attachment;
import com.dfsx.ganzcms.app.model.ColumnEntry;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.ganzcms.app.util.UtilHelp;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.dfsx.ganzcms.app.act.GanZiTopBarActivity.KEY_TITLE_TEXT;

/**
 * Created by heyang on  2018/4/1
 */
public class CommunityCoverFragment extends AbsListFragment {
    private static final int SLIDER_PAGE_MAX_NUMBER = 5;
    private MyLiveAdapter adapter;
    private EmptyView emptyView;
    private int page = 1;
    private SimpleImageBanner simpleBanner;
    private TopicListManager looperDataRequester;
    private TopicalApi topicalApi;
    ListView list;
    private List<TopicalEntry> looperDataList;
    private CommuntyDatailHelper communtyHelper;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emptyView.loadOver();
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshListView.onRefreshComplete();
        list = ((ListView) pullToRefreshListView.getRefreshableView());
        communtyHelper = new CommuntyDatailHelper(getActivity());
        topicalApi = communtyHelper.getmTopicalApi();
        looperDataRequester = new TopicListManager(getActivity(), 211 + "", 123);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                List<ColumnEntry> list = adapter.getData();
                if (position >= 0 && position < list.size()) {
                    ColumnEntry entry = list.get(position);
                    if (entry != null) {
                        Intent intent = new Intent();
                        Bundle bundle = GanZiTopBarActivity.getTitleBundle(0, entry.getName());
                        bundle.putLong("type", entry.getId());
                        bundle.putSerializable("tags", (Serializable) entry.getTags());
                        intent.putExtras(bundle);
                        GanZiTopBarActivity.start(getActivity(), CommunityRecycleUpFragment.class.getName(), intent);
                    }
                }
            }
        });
        iniSimBanner();
        emptyView.loading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1000);
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
//        ImageView image = (ImageView) emptyLayout.findViewById(R.id.no_data_images);
//        image.setImageResource(R.drawable.affair_recent_no_data);
        emptyView.setLoadOverView(emptyLayout);
        emptyView.loading();
    }

//    protected void setTopView(FrameLayout topListViewContainer) {
//        LinearLayout view = new LinearLayout(getContext());
//        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        view.setLayoutParams(p);
//        topListViewContainer.addView(view);
//        View topLayout = LayoutInflater.from(context).
//                inflate(R.layout.community_cover_header_layout, null);
//        simpleBanner = (SimpleImageBanner) topLayout.findViewById(R.id.simple_img_banner);
//        view.addView(topLayout);
//    }

    private DataFileCacheManager<ArrayList<ColumnEntry>> dataFileCacheManager = new
            DataFileCacheManager<ArrayList<ColumnEntry>>
                    (App.getInstance().getApplicationContext(),
                            122 + "", App.getInstance().getPackageName() + "comunitycover.txt") {
                @Override
                public ArrayList<ColumnEntry> jsonToBean(JSONObject obj) {
                    ArrayList<ColumnEntry> dlist = null;
                    if (obj != null) {
                        JSONArray arr = obj.optJSONArray("result");
                        if (arr != null) {
                            dlist = new ArrayList<ColumnEntry>();
                            for (int i = 0; i < arr.length(); i++) {
                                try {
                                    JSONObject object = (JSONObject) arr.get(i);
                                    ColumnEntry entry = new Gson().fromJson(object.toString(), ColumnEntry.class);
                                    dlist.add(entry);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    return dlist;
                }
            };


    protected void getMoreData() {
        getData();
    }

    private void getData() {
        String requrl = App.getInstance().getmSession().getCommunityServerUrl() + "/public/columns";
        dataFileCacheManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(requrl).setToken(App.getInstance().getCurrentToken())
                .build(), false).setCallback(callback);
        getLooperImageData();
    }

    public void iniSimBanner() {
        list.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.banner_recommend_layout, null));
        simpleBanner = (SimpleImageBanner) list.findViewById(R.id.simple_img_banner);
        int destheight = Util.dp2px(getActivity(), 182);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) simpleBanner.getLayoutParams();
        int mScreenWidth = UtilHelp.getScreenWidth(getActivity());
        if (params == null) {
            params = new LinearLayout.LayoutParams(mScreenWidth, destheight);
        } else {
            params.width = mScreenWidth;
            params.height = destheight;
        }
        simpleBanner.setLayoutParams(params);
        simpleBanner.setDelay(4);
        simpleBanner.setPeriod(4);
        simpleBanner.setOnItemClickL(new BaseBanner.OnItemClickL() {
            @Override
            public void onItemClick(int position) {
                if (looperDataList != null && position >= 0 &&
                        position < looperDataList.size()) {
                    communtyHelper.gotoComunnityInfo(looperDataList.get(position));
                }
            }
        });
    }

    private void getLooperImageData() {
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/threads/recommended?";
        url += "page=1&size=10";
        DataRequest.HttpParams params = new DataRequest.HttpParamsBuilder().
                setUrl(url).setToken(null).build();
        looperDataRequester.getData(params, false).setCallback
                (new DataRequest.DataCallback<ArrayList<TopicalEntry>>() {
                    @Override
                    public void onSuccess(final boolean isAppend, final ArrayList<TopicalEntry> topicalies) {
                        if (topicalies == null || topicalies.isEmpty()) return;
                        Observable.from(topicalies)
                                .subscribeOn(Schedulers.io())
                                .map(new Func1<TopicalEntry, TopicalEntry>() {
                                    @Override
                                    public TopicalEntry call(TopicalEntry topicalEntry) {
                                        TopicalEntry tag = topicalApi.getTopicTopicalInfo(topicalEntry);
                                        return tag;
                                    }
                                })
                                .toList()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<List<TopicalEntry>>() {
                                    @Override
                                    public void onCompleted() {
                                        emptyView.loadOver();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onNext(final List<TopicalEntry> data) {
                                        if (!(data == null || data.isEmpty())) {
//                                            ArrayList<TopicalEntry> arr = new ArrayList<>();
//                                            int count = 0;
//                                            for (TopicalEntry entry : data) {
//                                                if (entry.getShowType() == 3) {
//                                                    if (count < SLIDER_PAGE_MAX_NUMBER) {
//                                                        arr.add(entry);
//                                                        count++;
//                                                    } else
//                                                        break;
//                                                }
//                                            }
//                                            if (!(arr == null || arr.isEmpty())) {
//                                                simpleBanner.setVisibility(View.VISIBLE);
//                                                simpleBanner.setSource(bannerDataHelper.getBannerItems(arr)).startScroll();
//                                            } else
//                                                simpleBanner.setVisibility(View.GONE);
                                            if (data.size() > SLIDER_PAGE_MAX_NUMBER) {
                                                int count = data.size();
                                                while (count > SLIDER_PAGE_MAX_NUMBER) {
                                                    count--;
                                                    data.remove(count);
                                                    count = data.size();
                                                }
                                            }
                                            looperDataList = data;
                                            simpleBanner.setVisibility(View.VISIBLE);
                                            simpleBanner.setSource(bannerDataHelper.getBannerItems(data)).startScroll();
                                        } else {
                                            simpleBanner.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        if (simpleBanner != null)
                            simpleBanner.setVisibility(View.GONE);
                    }
                });
    }

    private DataRequest.DataCallback<ArrayList<ColumnEntry>> callback = new DataRequest.DataCallback<ArrayList<ColumnEntry>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<ColumnEntry> data) {
            emptyView.loadOver();
            pullToRefreshListView.onRefreshComplete();
            if (!(data == null || data.isEmpty())) {
                adapter.update(data, isAppend);
            } else {
                //   simpleBanner.setVisibility(View.GONE);
            }
//            if (adapter != null) {
//                adapter.update(data, isAppend);
//            }
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
        getData();
//        pullToRefreshListView.onRefreshComplete();
    }


    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getMoreData();
//        pullToRefreshListView.onRefreshComplete();
    }

    private BannerDataHelper bannerDataHelper = new BannerDataHelper<TopicalEntry>() {
        @Override
        public BannerItem changeToBannerItem(TopicalEntry data) {
            BannerItem item = new BannerItem();
            List<Attachment> atts = data.getAttachmentInfos();
            if (!(atts == null || atts.isEmpty())) {
                item.imgUrl = atts.get(0).getUrl();
            }
            item.title = data.getContent();
//            item.title = data.getTitle();
            return item;
        }
    };

    protected class MyLiveAdapter extends BaseListViewAdapter<ColumnEntry> {

        public MyLiveAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.communtiy_cover_item;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, final int position) {
            ImageView thumbnailImageView = holder.getView(R.id.item_cover_img);
            TextView titleTextView = holder.getView(R.id.news_list_item_name);
            TextView contentTextView = holder.getView(R.id.news_list_item_describe);
            ColumnEntry roomInfo = list.get(position);
            titleTextView.setText(roomInfo.getName());
            contentTextView.setText(roomInfo.getDescription());
//            GlideImgManager.getInstance().
//                    showImg(context, thumbnailImageView, roomInfo.getIcon_url());
            Glide.with(context)
                    .load(roomInfo.getIcon_url())
                    .asBitmap()
                    .error(R.drawable.glide_default_image)
                    .into(thumbnailImageView);
        }
    }
}
