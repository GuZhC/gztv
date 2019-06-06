package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.MainTabActivity;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.model.SocirtyNewsChannel;
import com.dfsx.lzcms.liveroom.view.adwareVIew.VideoAdwarePlayView;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by heyang on 2016/9/15.
 */
public class ColumnPlayFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2<ScrollView> {
    protected Context context;
    protected PullToRefreshScrollView pullToRefreshScrollView;
    protected String mKey = "";
    protected LinearLayout mMainGroupView;
    protected NewsDatailHelper _newsHelper;
    View rootView;

    public static ColumnPlayFragment newInstance(String key) {
        ColumnPlayFragment fragment = new ColumnPlayFragment();
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
        rootView = inflater.inflate(R.layout.news_video_custom, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mKey = bundle.getString("key");
        }
        context = getContext();
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

    public void initData() {
        if (mMainGroupView != null && mMainGroupView.getChildCount() > 0)
            mMainGroupView.removeAllViews();
        List<ColumnCmsEntry> sort = null;
        if (TextUtils.isEmpty(mKey)) {
            sort = ColumnBasicListManager.getInstance().findDllListByKey("vod");
        } else {
            sort = ColumnBasicListManager.getInstance().findDllListByKey(mKey);
        }
        if (sort != null && !sort.isEmpty()) {
            for (ColumnCmsEntry ch : sort) {
                createColumnView(ch.getName(), ch.getId());
            }
        }
        pullToRefreshScrollView.onRefreshComplete();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getVideoPlyer() != null) {
            getVideoPlyer().onDestroy();
        }
    }

    private void getData(long id, final View v) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/" + id + "/contents?";
        new DataFileCacheManager<ArrayList<ContentCmsEntry>>(App.getInstance().getApplicationContext(),
                mKey + "column_" + id, App.getInstance().getPackageName() + "columnPlayFragment.txt") {
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
                                int modeType = _newsHelper.getmContentCmsApi().getModeType(entry.getType(), 0);
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
                .build(), false).setCallback(new DataRequest.DataCallbackTag<ArrayList<ContentCmsEntry>>() {
            @Override
            public void onSuccess(Object object, boolean isAppend, ArrayList<ContentCmsEntry> data) {
                if (object != null) {
                    View view = (View) object;
                    int i = 0;
                    View bottom_space_line = (View) view.findViewById(R.id.bottom_space_line);
                    ImageView fir = (ImageView) view.findViewById(R.id.first_item_image);
                    TextView firtx = (TextView) view.findViewById(R.id.first_item_title);
                    ImageView ser = (ImageView) view.findViewById(R.id.second_item_image);
                    TextView sertx = (TextView) view.findViewById(R.id.second_item_title);
                    ImageView thir = (ImageView) view.findViewById(R.id.third_item_image);
                    TextView thirtx = (TextView) view.findViewById(R.id.third_item_title);
                    ImageView four = (ImageView) view.findViewById(R.id.four_item_image);
                    TextView fourtx = (TextView) view.findViewById(R.id.four_item_title);
                    if (data != null && !data.isEmpty()) {
                        if (view != null)
                            view.setVisibility(View.VISIBLE);
                        while (i < data.size() && i < 4) {
                            ContentCmsEntry entry = null;
                            if (data != null && i < data.size()) {
                                entry = data.get(i);
                            }
                            switch (i) {
                                case 0:
                                    getThumbe(fir, firtx, entry);
                                    break;
                                case 1:
                                    getThumbe(ser, sertx, entry);
                                    break;
                                case 2:
                                    getThumbe(thir, thirtx, entry);
                                    break;
                                case 3:
                                    getThumbe(four, fourtx, entry);
                                    break;
                            }
                            i++;
                        }
                    } else {
                        if (view != null)
                            view.setVisibility(View.GONE);
                    }
                    //加判断  2017-7-21
                    if (i == 0) {
                        View child1 = (View) fir.getParent().getParent();
//            View child2 = (View) thir.getParent().getParent();
                        ((View) child1.getParent()).setVisibility(View.GONE);
//            ((View) child2.getParent()).setVisibility(View.GONE);
                        bottom_space_line.setVisibility(View.GONE);
                    } else if (i == 1) {
                        ((View) ser.getParent()).setVisibility(View.INVISIBLE);
                        View child2 = (View) thir.getParent().getParent();
                        child2.setVisibility(View.GONE);
                    } else if (i == 2) {
                        View child2 = (View) thir.getParent().getParent();
                        child2.setVisibility(View.GONE);
                    } else if (i == 3) {
                        ((View) four.getParent()).setVisibility(View.INVISIBLE);
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
        });
    }

    public void getThumbe(ImageView view, TextView txt, ContentCmsEntry entry) {
        String imgpath = "";
        if (entry == null) {
            Util.LoadThumebImage(view, imgpath, null);
            return;
        }
        if (entry.getThumbnail_urls() != null && !entry.getThumbnail_urls().isEmpty()) {
            imgpath = entry.getThumbnail_urls().get(0).toString();
        }
        Glide.with(getActivity())
                .load(imgpath)
                .asBitmap()
                .error(R.drawable.glide_default_image)
                .into(view);
//        Util.LoadThumebImage(view, imgpath, null);
        txt.setText(entry.getTitle());
        view.setTag(R.id.tag_colplay_cid, entry);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentCmsEntry cms = (ContentCmsEntry) view.getTag(R.id.tag_colplay_cid);
                if (cms != null) {
                    _newsHelper.goDetail(cms);
                }
            }
        });
    }

    public VideoAdwarePlayView getVideoPlyer() {
        if (getActivity() instanceof MainTabActivity) {
            return ((MainTabActivity) getActivity()).getVideoPlayer();
        }
        return null;
    }

    private void removeVideoPlayer() {
        if (getVideoPlyer() != null) {
            ViewGroup view = (ViewGroup) getVideoPlyer().getParent();
            if (view != null) {
                view.removeView(getVideoPlyer());
            }
        }
    }

    public void createColumnView(final String title, final long cid) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.column_video_item, null);
        if (view != null) {
            TextView titles = (TextView) view.findViewById(R.id.clolumn_title);
            View moreBnt = (View) view.findViewById(R.id.more_btn_view);
            moreBnt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("type", cid);
                    bundle.putString("title",title);
//                    DefaultFragmentActivity.start(getActivity(), ImagsGroupsFragment.class.getName(), cid);
                    DefaultFragmentActivity.start(getActivity(), ImageVodListFragment.class.getName(), bundle);

//                    TimeBottomPopupwindow pp = new TimeBottomPopupwindow(getActivity(), new TimeBottomPopupwindow.DateChooseInterface() {
//                        @Override
//                        public void getDateTime(String time) {
//                            String op = time;
//                            int a = 0;
//                        }
//                    });
//                    pp.show(rootView);
                }
            });
            titles.setText(title);
            getData(cid, view);
            mMainGroupView.addView(view);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        initData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

    }

}
