package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by  heyang on 2018/1/6.  甘孜点播
 */
public class ColumnPlayListFragment extends AbsListFragment {

    public static final int PAGE_SIZE = 20;
    private MyAdapter adapter;
    private int pageCount = 1;
    private EmptyView emptyView;
    private NewsDatailHelper newsDatailHelper;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newsDatailHelper = new NewsDatailHelper(getActivity());
        getData(1);
    }

    @Override
    public void setListAdapter(ListView listView) {
        if (adapter == null) {
            adapter = new MyAdapter(context);
        }
        listView.setAdapter(adapter);
        pullToRefreshListView.setBackgroundColor(Color.WHITE);
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
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/" + id + "/contents?";
        new DataFileCacheManager<ArrayList<ContentCmsEntry>>(App.getInstance().getApplicationContext(),
                1 + "column_" + id, App.getInstance().getPackageName() + "colulistFragment.txt") {
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
                                int modeType = newsDatailHelper.getmContentCmsApi().getModeType(entry.getType(), 0);
                                if (modeType == 3) {
                                    // show 直播 特殊处理
                                    entry.setShowType(modeType);
                                    JSONObject extendsObj = obj.optJSONObject("extension");
                                    if (extendsObj != null) {
                                        JSONObject showObj = extendsObj.optJSONObject("show");
                                        if (showObj != null) {
                                            ContentCmsEntry.ShowExtends showExtends = new Gson().fromJson(showObj.toString(), ContentCmsEntry.ShowExtends.class);
                                            entry.setShowExtends(showExtends);
                                        }
                                    }
                                }
                                entry.setModeType(modeType);
                                dlist.add(entry);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return dlist;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setRequestType(DataReuqestType.GET)
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), false).setCallback(new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {

            @Override
            public void onSuccess(boolean isAppend, ArrayList<ContentCmsEntry> data) {
                if (data != null) {
                    adapter.update(data, isAppend);
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                pullToRefreshListView.onRefreshComplete();
            }
        });
    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        View eV = LayoutInflater.from(context).
                inflate(R.layout.no_pay_record_layout, null);
        emptyView.setLoadOverView(eV);
        emptyView.loading();
        container.addView(emptyView);
    }

    class MyAdapter extends BaseListViewAdapter<ContentCmsEntry> {

        public MyAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_pay_record;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            TextView name = holder.getView(R.id.item_user_name);
            TextView payText = holder.getView(R.id.item_pay_text);
            TextView time = holder.getView(R.id.item_time);
            ImageView logo = holder.getView(R.id.logo_img);
            TextView payNum = holder.getView(R.id.item_decrease_num);
            ImageView viptag = holder.getView(R.id.item_vip_tag);
            ContentCmsEntry record = list.get(position);


        }
    }
}
