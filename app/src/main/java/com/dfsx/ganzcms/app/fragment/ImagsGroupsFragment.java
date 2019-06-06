package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by heyang on 2016/9/16.
 */
public class ImagsGroupsFragment extends HeadLineFragment implements AdapterView.OnItemClickListener {

    protected Context context;
    private Activity act;
    protected PullToRefreshListView pullToRefreshListView;
    protected ListView listView;
    private View loadFailView;
    private ListImagesAdapter adapter;
    private int offset;
    private long mTypeId = -1;
    //    private DataFileCacheManager<ArrayList<ContentCmsEntry>> dataFileCacheManager;
    String mKey = "";
    private ContentCmsApi  contentCmsApi;

    public static ImagsGroupsFragment newInstance(long typeId) {
        ImagsGroupsFragment fragment = new ImagsGroupsFragment();
        Bundle bundel = new Bundle();
        bundel.putLong("type", typeId);
        fragment.setArguments(bundel);
        return fragment;
    }

    public static ImagsGroupsFragment newInstance(String key) {
        ImagsGroupsFragment fragment = new ImagsGroupsFragment();
        Bundle bundel = new Bundle();
        bundel.putString("key", key);
        fragment.setArguments(bundel);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        act = getActivity();
        return inflater.inflate(R.layout.news_imags_custom, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mTypeId = getArguments().getLong("type");
            mKey = getArguments().getString("key");
        }
        contentCmsApi=newsDatailHelper.getmContentCmsApi();
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.news_scroll_layout);
        listView = pullToRefreshListView.getRefreshableView();
        loadFailView = view.findViewById(R.id.load_fail_layout);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getMoreData();
            }
        });
        pullToRefreshListView.setOnItemClickListener(this);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        setListAdapter();
//        listView.setEmptyView(loadFailView);
//        if (mTypeId == -1) {
//            List<ColumnCmsEntry> dlist = App.getInstance().getmHeaderCmsList();
//            if (dlist != null && !dlist.isEmpty()) {
//                for (ColumnCmsEntry entry : dlist) {
//                    if (TextUtils.equals("沫水", entry.getName().toString().trim())) {
//                        mTypeId = entry.getId();
//                        break;
//                    }
//                }
//            }
//        }
        getData();
    }

    private DataFileCacheManager<ArrayList<ContentCmsEntry>> dataFileCacheManager = new
            DataFileCacheManager<ArrayList<ContentCmsEntry>>
                    (App.getInstance().getApplicationContext(),
                            mTypeId + "", App.getInstance().getPackageName() + "ImageHsrollFragment.txt") {
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
                                int thumn_size = entry.getThumbnail_urls() != null ? entry.getThumbnail_urls().size() : 0;
//                                entry.setShowType(contentCmsApi.getShowModeType(entry.getList_item_mode(), thumn_size));
                                int modeType = contentCmsApi.getModeType(entry.getType(), thumn_size);
                                if (modeType == 3) {
                                    // show 直播 特殊处理
                                    entry.setShowType(modeType);
                                    JSONObject extendsObj = item.optJSONObject("extension");
                                    if (extendsObj != null) {
                                        JSONObject showObj = extendsObj.optJSONObject("show");
                                        if (showObj != null) {
                                            ContentCmsEntry.ShowExtends showExtends = new Gson().fromJson(showObj.toString(), ContentCmsEntry.ShowExtends.class);
                                            entry.setShowExtends(showExtends);
                                        }
                                    }
                                }
                                entry.setModeType(modeType);
                                socityNewsAarry.add(entry);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return socityNewsAarry;
                }
            };


    private DataRequest.DataCallback<ArrayList<ContentCmsEntry>>
                callback = new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
            @Override
            public void onSuccess(boolean isAppend, ArrayList<ContentCmsEntry> data) {
                if (!isAppend && data == null) {
                    adapter.clear();
                } else {
                    adapter.update(data, isAppend);
                }
                pullToRefreshListView.onRefreshComplete();
            }

        @Override
        public void onFail(ApiException e) {
//            ArrayList<ContentCmsEntry> dlist = new ArrayList<ContentCmsEntry>();
//            for (int i = 0; i < 4; i++) {
//                ContentCmsEntry chne = new ContentCmsEntry();
//                chne.setTitle("【超级试驾员杯】十一山西东线深度自驾游~新路线，新高峰！");
//                dlist.add(chne);
//            }
//            adapter.update(dlist, false);
            pullToRefreshListView.onRefreshComplete();
        }
    };

    protected void getData() {
        offset = 1;
        getData(offset);
    }

    protected void getMoreData() {
        offset++;
        getData(offset);
    }

    private void getData(int offset) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/" + mTypeId + "/contents?";
        url += "&page=" + offset + "";
        dataFileCacheManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), offset > 0).setCallback(callback);
    }

    protected void setListAdapter() {
        if (adapter == null) {
            adapter = new ListImagesAdapter();
        }
        pullToRefreshListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - listView.getHeaderViewsCount();
        try {
            boolean isLeagle = position >= 0 && position < adapter.getCount();
            if (isLeagle) {
                ContentCmsEntry channel = adapter.getData().get(position);
                goDetail(channel);
//                if (!TextUtils.isEmpty(channel.newsThumb)) {
//                    Intent intent = new Intent(context, ActNewsImageDetails.class);
//                    intent.putExtra(ActNewsImageDetails.INTENT_DATA_NEWS_ID, channel.id);
//                    startActivity(intent);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ListImagesAdapter extends BaseAdapter {
        private ArrayList<ContentCmsEntry> list = new ArrayList<>();
        private Context context;

        public void update(ArrayList<ContentCmsEntry> data, boolean bAddTail) {
            if (data != null && !data.isEmpty()) {
                boolean noData = false;
                if (bAddTail)
                    if (list.size() >= data.size()
                            && list.get(list.size() - 1).getId() == data.get(data.size() - 1).getId())
                        noData = true;
                    else
                        list.addAll(data);
                else {
                    if (list != null) {
                        if (/*items.size() == data.size() && */
                                list.size() > 0 &&
                                        list.get(0).getId() == data.get(0).getId())
                            noData = true;
                    }
                    if (!noData) {
                        list = data;
                    }
                }
                notifyDataSetChanged();
            }
        }

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }

        public ArrayList<ContentCmsEntry> getData() {
            return list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
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
            return R.layout.news_video_list_hsrocll_item;
        }

        protected void setViewHolderData(BaseViewHodler holder, int position) {
            ImageView itemImage = holder.getView(R.id.item_img);
            TextView itemTiltle = holder.getView(R.id.news_list_item_title);
            TextView itemcolumn = holder.getView(R.id.item_autor);
            TextView viewcount = holder.getView(R.id.item_commeanuder_tx);
            TextView itemCreateTime = holder.getView(R.id.item_create_time);
            ImageView itemVideoMrk = holder.getView(R.id.item_video_mark);
            ContentCmsEntry channel = list.get(position);
            if (channel.getThumbnail_urls() != null && channel.getThumbnail_urls().size() > 0)
                GlideImgManager.getInstance().showImg(context, itemImage, channel.getThumbnail_urls().get(0));
            itemTiltle.setText(channel.getTitle());
            if (channel.getShowType() == 2) {
                //video
                itemVideoMrk.setVisibility(View.GONE);
            } else
                itemVideoMrk.setVisibility(View.GONE);
            viewcount.setText(channel.getView_count()+"浏览");
//            String timeStr = UtilHelp.getTimeString("yyy-MM-dd", channel.getCreation_time());
            itemCreateTime.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", channel.getPublish_time() * 1000));
//            String autorText = TextUtils.isEmpty(channel.author) ? "" : channel.author;
//            itemcolumn.setText(autorText);
//            itemCreateTime.setText(channel.newsTime);
        }
    }


}
