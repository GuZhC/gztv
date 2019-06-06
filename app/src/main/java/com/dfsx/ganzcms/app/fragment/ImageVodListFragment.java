package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.ListViewAdapter;
import com.dfsx.ganzcms.app.business.HeadlineListManager;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.logonproject.view.TimeBottomPopupwindow;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.google.zxing.common.StringUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.dfsx.core.common.act.DefaultFragmentActivity.KEY_FRAGMENT_PARAM;

/**
 * Created by  heyang on 2018/4/19
 */


public class ImageVodListFragment extends AbsListFragment {

    public static final int PAGE_SIZE = 20;
    private ListViewAdapter adapter;
    private int pageCount = 1;
    private EmptyView emptyView;
    private NewsDatailHelper newsDatailHelper;
    private HeadlineListManager dateManager;
    private long mTypeId;
    private String title;
    private TextView titleTxt;
    private LinearLayout topView;
    //    TimeBottomPopupwindow timePopupwindow;
    private long startTime;
    private long endTime;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mTypeId = getArguments().getLong("type");
            title = getArguments().getString("title");
        }
        titleTxt.setText(title);
//        emptyView.loadOver();
//        timePopupwindow = new TimeBottomPopupwindow(getActivity(), new TimeBottomPopupwindow.DateChooseInterface() {
//            @Override
//            public void getDateTime(String start, String end) {
//                startTime = convert2long(start)/1000L;
//                endTime = convert2long(end)/1000L;
//                String  ss= Util.getTimeString("yyyy-MM-dd HH:mm:ss",startTime);
//                String  ed= Util.getTimeString("yyyy-MM-dd HH:mm:ss",endTime);
//                pageCount = 1;
//                getData(pageCount);
//            }
//        });
        dateManager = new HeadlineListManager(getActivity(), mTypeId + "", mTypeId, App.getInstance().getPackageName());
        newsDatailHelper = new NewsDatailHelper(getActivity());
        getData(1);
    }

    // 长日期格式
    public long convert2long(String date) {
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    @Override
    public void setListAdapter(final ListView listView) {
        if (adapter == null) {
            adapter = new ListViewAdapter(context);
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                boolean isLeagle = position >= 0 && position < adapter.getCount();
                if (isLeagle) {
                    ContentCmsEntry channel = null;
                    if (!(adapter.getData() == null || adapter.getData().isEmpty())) {
                        channel = adapter.getData().get(position);
                    }
                    newsDatailHelper.goDetail(channel);
                }
            }
        });
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageCount = 1;
        getData(1);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pageCount++;
        getData(pageCount);
    }

    private void getData(long id) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/" + mTypeId + "/contents?";
        url += "&page=" + pageCount;
        if (startTime != -1 && startTime != 0)
            url += "&start=" + startTime;
        if (endTime != -1 && endTime != 0)
            url += "&stop=" + endTime;
        DataRequest.HttpParams params = new DataRequest.HttpParamsBuilder().
                setUrl(url).setToken(App.getInstance().getCurrentToken()).build();
        dateManager.getData(params, pageCount > 1).setCallback
                (callback);
    }

    private DataRequest.DataCallback<ArrayList<ContentCmsEntry>>
            callback = new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<ContentCmsEntry> data) {
            emptyView.loadOver();
            if (!isAppend && data == null) {
                adapter.clear();
            } else {
                adapter.update(data, isAppend);
            }
            pullToRefreshListView.onRefreshComplete();
        }

        @Override
        public void onFail(ApiException e) {
            emptyView.loadOver();
            e.printStackTrace();
            pullToRefreshListView.onRefreshComplete();
        }
    };

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
        topView = new LinearLayout(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        topView.setLayoutParams(p);
        topListViewContainer.addView(topView, p);
        View topLayout = LayoutInflater.from(context).
                inflate(R.layout.image_vod_topbar_layout, null);
        ImageView back = (ImageView) topLayout.findViewById(R.id.act_finish);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        ImageView logo = (ImageView) topLayout.findViewById(R.id.title_image);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeBottomPopupwindow timePopupwindow = new TimeBottomPopupwindow(getActivity(), new TimeBottomPopupwindow.DateChooseInterface() {
                    @Override
                    public void getDateTime(String start, String end) {
                        startTime = convert2long(start) / 1000L;
                        endTime = convert2long(end) / 1000L;
//                        String ss = Util.getTimeString("yyyy-MM-dd HH:mm:ss", startTime);
//                        String ed = Util.getTimeString("yyyy-MM-dd HH:mm:ss", endTime);
                        pageCount = 1;
                        getData(pageCount);
                    }
                });
                timePopupwindow.show(topView.getRootView());
            }
        });
        titleTxt = (TextView) topLayout.findViewById(R.id.title_text);
        topView.addView(topLayout);
    }

    class MyAdapter extends BaseListViewAdapter<ContentCmsEntry> {

        public MyAdapter(Context context) {
            super(context);
        }

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.news_video_list_hsrocll_item;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            ImageView itemImage = holder.getView(R.id.item_img);
            TextView itemTiltle = holder.getView(R.id.news_list_item_title);
            TextView viewcount = holder.getView(R.id.item_commeanuder_tx);
            TextView itemCreateTime = holder.getView(R.id.item_create_time);
            ImageView itemVideoMrk = holder.getView(R.id.item_video_mark);
            ContentCmsEntry channel = list.get(position);
            if (channel.getThumbnail_urls() != null && channel.getThumbnail_urls().size() > 0)
                GlideImgManager.getInstance().showImg(context, itemImage, channel.getThumbnail_urls().get(0));
            itemTiltle.setText(channel.getTitle());
            if (channel.getShowType() == 2) {
                itemVideoMrk.setVisibility(View.GONE);
            } else
                itemVideoMrk.setVisibility(View.GONE);
            viewcount.setText(channel.getView_count() + "浏览");
            itemCreateTime.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", channel.getPublish_time() * 1000));
        }
    }
}
