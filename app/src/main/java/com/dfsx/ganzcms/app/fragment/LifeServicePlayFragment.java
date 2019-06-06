package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.business.ColumnHelperManager;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.view.MyGridView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang on  服务
 */
public class LifeServicePlayFragment extends ColumnPlayFragment implements PullToRefreshBase.OnRefreshListener2<ScrollView> {

    private ColumnHelperManager columnHelper;
    private long mId;

    public static final String KEY_CONTENT_ID = "LifeServicePlayFragment_dfsx.cms.content_id";

    public static LifeServicePlayFragment newInstance(String key) {
        LifeServicePlayFragment fragment = new LifeServicePlayFragment();
        Bundle bundel = new Bundle();
        bundel.putString("key", key);
        fragment.setArguments(bundel);
        return fragment;
    }

    public static LifeServicePlayFragment newInstance(long id) {
        LifeServicePlayFragment fragment = new LifeServicePlayFragment();
        Bundle bundel = new Bundle();
        bundel.putLong(KEY_CONTENT_ID, id);
        fragment.setArguments(bundel);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.service_native_custom, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view,savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mKey = bundle.getString("key");
            mId = bundle.getLong(KEY_CONTENT_ID);
        }
        context = getContext();
        columnHelper = new ColumnHelperManager(getActivity());
        _newsHelper = new NewsDatailHelper(getActivity());
        mMainGroupView = new LinearLayout(getActivity());
        mMainGroupView.setOrientation(1);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mMainGroupView.setLayoutParams(lp);
        pullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.news_scroll_layout);
        pullToRefreshScrollView.setOnRefreshListener(this);
        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshScrollView.addView(mMainGroupView);
        initData();
    }

    @Override
    public void initData() {
        if (mMainGroupView != null && mMainGroupView.getChildCount() > 0)
            mMainGroupView.removeAllViews();
        List<ColumnCmsEntry> sort = null;
        if (mId != 0){
            sort = ColumnBasicListManager.getInstance().getDListById(mId);
        } else if (TextUtils.isEmpty(mKey)) {
            sort = ColumnBasicListManager.getInstance().findDllListByKey("fuwu");
        } else {
            sort = ColumnBasicListManager.getInstance().findDllListByKey(mKey);
        }
        if (sort == null) {
            columnHelper.getAllColumns();
            columnHelper.setCallback(callback);
//            if (TextUtils.isEmpty(mKey)) {
//                sort = ColumnBasicListManager.getInstance().findDllListByKey("fuwu");
//            } else {
//                sort = ColumnBasicListManager.getInstance().findDllListByKey(mKey);
//            }
        }
        if (sort != null && !sort.isEmpty()) {
            for (ColumnCmsEntry ch : sort) {
                createColumnView(ch.getName(), ch.getId());
            }
        }
        pullToRefreshScrollView.onRefreshComplete();
    }

    private DataRequest.DataCallback<ArrayList<ColumnCmsEntry>>
            callback = new DataRequest.DataCallback<ArrayList<ColumnCmsEntry>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<ColumnCmsEntry> data) {
            //     LogUtils.e("TAG", "callback onSuccess()  data= " + data.size());
            if (data != null) {
                ArrayList<ColumnCmsEntry> list = (ArrayList<ColumnCmsEntry>) data;
                ColumnBasicListManager.getInstance().set_columnList(list);
                List<ColumnCmsEntry> sort = null;
                if (TextUtils.isEmpty(mKey)) {
                    sort = ColumnBasicListManager.getInstance().findDllListByKey("fuwu");
                } else {
                    sort = ColumnBasicListManager.getInstance().findDllListByKey(mKey);
                }
                if (sort != null && !sort.isEmpty()) {
                    for (ColumnCmsEntry ch : sort) {
                        createColumnView(ch.getName(), ch.getId());
                    }
                }
            }
        }

        @Override
        public void onFail(ApiException e) {
            e.printStackTrace();
        }
    };

    protected void getData(long id, final View v) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/" + id + "/contents?";
        new DataFileCacheManager<ArrayList<ContentCmsEntry>>(App.getInstance().getApplicationContext(),
                mKey + "colum11n_" + id, App.getInstance().getPackageName() + "columnPlayFragment.txt") {
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
                                int thumn_size = entry.getThumbnail_urls() != null ? entry.getThumbnail_urls().size() : 0;
                                int modeType = _newsHelper.getmContentCmsApi().getModeType(entry.getType(), thumn_size);
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
                .setUrl(url).setToken(App.getInstance().getCurrentToken()).setTagView(v)
                .build(), false).setCallback(callbackTag);
    }

    private DataRequest.DataCallbackTag callbackTag = new DataRequest.DataCallbackTag<ArrayList<ContentCmsEntry>>() {
        @Override
        public void onSuccess(Object object, boolean isAppend, ArrayList<ContentCmsEntry> data) {
            if (object != null) {
                MyGridView gridView = (MyGridView) object;
                if (gridView != null) {
                    ServiceGridAdapter mMyGridAdapter = new ServiceGridAdapter(getActivity());
                    gridView.setAdapter(mMyGridAdapter);
                    mMyGridAdapter.update(data, isAppend);
                }
            }
        }

        @Override
        public void onSuccess(boolean isAppend, ArrayList<ContentCmsEntry> data) {

        }

        @Override
        public void onFail(ApiException e) {
            e.printStackTrace();
            pullToRefreshScrollView.onRefreshComplete();
        }
    };

    public void createColumnView(String title, final long cid) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.service_column_item, null);
        if (view != null) {
            TextView titles = (TextView) view.findViewById(R.id.life_life_txt);
            MyGridView gridview1 = (MyGridView) view.findViewById(R.id.gridview_life);
            titles.setText(title);
            getData(cid, gridview1);
            mMainGroupView.addView(view);
        }
    }

    public class ServiceGridAdapter<T> extends BaseAdapter {
        private Context mContext;
        ArrayList<T> items = new ArrayList<T>();

        public ServiceGridAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return items == null ? 0 : items.size();
        }

        public void update(ArrayList<T> data, boolean bAddTail) {
            if (bAddTail)
                items.addAll(data);
            else
                items = data;
//        mbInit = true;
            notifyDataSetChanged();
        }

        public T getData(int position) {
            if (items == null || items.isEmpty()) return null;
            if (position >= items.size()) return null;
            return items.get(position);
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BaseViewHodler holeder = new BaseViewHodler(parent, R.layout.my_life_service_item, position);
            View body = holeder.getView(R.id.body_iew);
            TextView tv = holeder.getView(R.id.tv_item);
            ImageView iv = holeder.getView(R.id.iv_item);
            ContentCmsEntry entry = (ContentCmsEntry) items.get(position);
            tv.setText(entry.getTitle());
//            iv.setImageResource(entry.getModeType());
            String thumb = "";
            if (!(entry.getThumbnail_urls() == null || entry.getThumbnail_urls().isEmpty())) {
                thumb = entry.getThumbnail_urls().get(0);
            }
            Glide.with(mContext)
                    .load(thumb)
                    .asBitmap()
                    .error(R.drawable.glide_default_image)
                    .into(iv);
            body.setTag(entry);
            body.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentCmsEntry channel = (ContentCmsEntry) v.getTag();
                    if (channel != null) {
                        _newsHelper.goDetail(channel);
//                        Bundle bundles = new Bundle();
//                        bundles.putString(BaseAndroidWebFragment.PARAMS_URL, channel.getUrl());
//                        WhiteTopBarActivity.startAct(getActivity(), NewsWebVoteFragment.class.getName(), channel.getTitle(), 0, bundles);
                    }
                }
            });
            return holeder.getConvertView();
        }
    }

}
