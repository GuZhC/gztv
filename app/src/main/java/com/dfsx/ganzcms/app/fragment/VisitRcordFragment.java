package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.CommuntyDatailHelper;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.business.TopicalApi;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.CustomeProgressDialog;
import com.dfsx.ganzcms.app.view.MyMorePopupwindow;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.RXBusUtil;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang on 2017/7/24.
 * 到访记录
 */
public class VisitRcordFragment extends AbsListFragment {
    private MyLiveAdapter adapter;
    private MyDataManager dataManager;
    private ContentCmsApi mContentCmsApi;
    private MyMorePopupwindow popupwindow;
    private EmptyView emptyView;
    private LinearLayout topView;
    private CustomeProgressDialog loading;
    private int page = 1;
    private long mUserId = -1;
    private TopicalApi mTopicalApi;
    private CommuntyDatailHelper mCommuntyDatailHelper;

    public static VisitRcordFragment newInstance(long cId) {
        Bundle bundle = new Bundle();
        bundle.putLong("uID", cId);
        VisitRcordFragment fragment = new VisitRcordFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUserId = bundle.getLong(WhiteTopBarActivity.KEY_PARAM);
        }
        mTopicalApi = new TopicalApi(getActivity());
        popupwindow = new MyMorePopupwindow(context);
        emptyView.loadOver();
        pullToRefreshListView.onRefreshComplete();
        mContentCmsApi = new ContentCmsApi(getActivity());
        mCommuntyDatailHelper = new CommuntyDatailHelper(getContext());
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                position = position - listView.getHeaderViewsCount();
//                List<TopicalEntry.VisitorBean> list = adapter.getData();
//                if (position >= 0 && position < list.size()) {
//                    TopicalEntry.VisitorBean info = list.get(position);
//                    if (info != null) {
//                        Intent intent = new Intent(getActivity(), QuestionInfoAct.class);
//                        intent.putExtra("tid", info.getId());
//                        startActivity(intent);
//                    }
//                }
//            }
//        });
        getData(1);
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
                inflate(R.layout.no_default_frg_layout, null);

        emptyView.setLoadOverView(emptyLayout);
        emptyView.loading();
        ImageView btnStart = (ImageView) emptyLayout.findViewById(R.id.retyr_btn);
        View parent = (View) btnStart.getParent();
        parent.setVisibility(View.GONE);
//        btnStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getData(page);
//            }
//        });
    }

    private DataFileCacheManager<ArrayList<TopicalEntry.VisitorBean>> dataFileCacheManager = new
            DataFileCacheManager<ArrayList<TopicalEntry.VisitorBean>>
                    (App.getInstance().getApplicationContext(),
                            mUserId + "", this.getClass().getName() + "_visit.txt") {
                @Override
                public ArrayList<TopicalEntry.VisitorBean> jsonToBean(JSONObject jsonObject) {
                    ArrayList<TopicalEntry.VisitorBean> socityNewsAarry = null;
                    try {
                        JSONArray result = jsonObject.getJSONArray("result");
                        if (result != null) {
                            socityNewsAarry = new ArrayList<TopicalEntry.VisitorBean>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                TopicalEntry.VisitorBean entry = new Gson().fromJson(item.toString(), TopicalEntry.VisitorBean.class);
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
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/threads/" + mUserId + "/viewers?max=20";
//        url += "page=" + page + "";
        dataFileCacheManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), page > 1).setCallback(callback);
    }

    private DataRequest.DataCallback<ArrayList<TopicalEntry.VisitorBean>> callback = new DataRequest.DataCallback<ArrayList<TopicalEntry.VisitorBean>>() {
        @Override
        public void onSuccess(final boolean isAppend, ArrayList<TopicalEntry.VisitorBean> data) {
            emptyView.loadOver();
            pullToRefreshListView.onRefreshComplete();
            if (adapter != null) {
                if (data != null && !data.isEmpty()) {
                    Observable.from(data)
                            .subscribeOn(Schedulers.io())
                            .map(new Func1<TopicalEntry.VisitorBean, TopicalEntry.VisitorBean>() {
                                @Override
                                public TopicalEntry.VisitorBean call(TopicalEntry.VisitorBean topicalEntry) {
                                    if (App.getInstance().getUser() != null) {
                                        if (topicalEntry.getId() != App.getInstance().getUser().getUser().getId()) {
                                            int res = mTopicalApi.isAttentionOther(topicalEntry.getId());
                                            topicalEntry.setRelatRole(res);
                                        } else
                                            topicalEntry.setRelatRole(-1);
                                    } else
                                        topicalEntry.setRelatRole(-1);
                                    return topicalEntry;
                                }
                            })
                            .toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<List<TopicalEntry.VisitorBean>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(List<TopicalEntry.VisitorBean> data) {
                                    if (adapter != null && data != null) {
                                        adapter.update((ArrayList<TopicalEntry.VisitorBean>) data, isAppend);
                                    }
                                }
                            });
                }
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
        //   pullToRefreshListView.onRefreshComplete();
    }

    public class MyLiveAdapter extends BaseListViewAdapter<TopicalEntry.VisitorBean> {

        public MyLiveAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.visit_list_item;
        }

        @Override
        public void setItemViewData(final BaseViewHodler holder, final int position) {
            ImageView useimg = holder.getView(R.id.visit_user_imge);
            TextView itemTiltle = holder.getView(R.id.visit_item_title);
            ImageView addattion = holder.getView(R.id.visit_item_addttion);
            TopicalEntry.VisitorBean visit = list.get(position);
            UtilHelp.LoadImageErrorUrl(useimg,visit.getAvatar_url(), null, R.drawable.icon_defalut_no_login_logo);
            useimg.setTag(R.id.cirbutton_user_id, visit.getId());
            itemTiltle.setText(visit.getNickname());
            mCommuntyDatailHelper.setAttteonMarkStatus(visit.getRelatRole(), addattion);
            addattion.setTag(R.id.tag_colplay_cid, visit);
            addattion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TopicalEntry.VisitorBean bean = (TopicalEntry.VisitorBean) v.getTag(R.id.tag_colplay_cid);
                    if (bean != null) {
                        setAttion(bean.getId(), bean.getRelatRole());
                    }
                }
            });
        }
    }

    public void setAttion(long uID, final int role) {
        mCommuntyDatailHelper.upAttentionChanged(uID, role, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                getData(page);
                RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
                boolean flag = role == 0 ? true : false;
                RXBusUtil.sendConcernChangeMessage(flag, 1);
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
//        mTopicalApi.attentionAuthor(uID, role, new DataRequest.DataCallback() {
//            @Override
//            public void onSuccess(boolean isAppend, Object data) {
//                Observable.just((Boolean) data).
//                        subscribeOn(Schedulers.io()).
//                        observeOn(Schedulers.io()).
//                        map(new Func1<Boolean, Integer>() {
//                            @Override
//                            public Integer call(Boolean aBoolean) {
////                                int res = mTopicalApi.isAttentionOther(uId);
//                                return 0;
//                            }
//                        })
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<Integer>() {
//                                       @Override
//                                       public void onCompleted() {
//                                           RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
//                                           boolean flag = role == 0 ? true : false;
//                                           RXBusUtil.sendConcernChangeMessage(flag, 1);
//                                       }
//
//                                       @Override
//                                       public void onError(Throwable e) {
//                                           e.printStackTrace();
//                                       }
//
//                                       @Override
//                                       public void onNext(Integer result) {
//                                           getData(page);
//                                       }
//                                   }
//                        );
//            }
//
//            @Override
//            public void onFail(ApiException e) {
//                e.printStackTrace();
//            }
//        });


    }
}
