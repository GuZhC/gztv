package com.dfsx.ganzcms.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseRecyclerViewAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewDataAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.ganzcms.app.view.TwoRelyView;
import com.dfsx.lzcms.liveroom.fragment.AbsPullRecyclerViewFragment;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang 2018/5/20  frag_my_attention_list_item
 */
public class CmmunityPraiseFragment extends AbsPullRecyclerViewFragment {
    private RecycleAdapter adapter;
    private int page = 1;
    private long mId = 0;
    private Subscription commendUpdateSubscription;

    public static CmmunityPraiseFragment newInstance(long userId) {
        CmmunityPraiseFragment fragment = new CmmunityPraiseFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("mId", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mId = getArguments().getLong("mId");
        }
        super.onViewCreated(view, savedInstanceState);
        page = 1;
        if (getActivity() != null && getActivity() instanceof PullToRefreshRecyclerView.PullRecyclerHelper)
            pullToRefreshRecyclerView.setPullRecyclerHelper((PullToRefreshRecyclerView.PullRecyclerHelper) getActivity());
        getDatas(1);
    }

    public void initRegister() {
        commendUpdateSubscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_COMNITY_PRAISE_OK)) {
                            getDatas(1);
                        }
                    }
                });

    }

    private void setHeader(RecyclerView view) {
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.communvity_no_replay_layout, view, false);
        adapter.setHeaderView(header);
    }

    private DataFileCacheManager<ArrayList<TopicalEntry.PraiseBean>> dataFileCacheManager = new
            DataFileCacheManager<ArrayList<TopicalEntry.PraiseBean>>
                    (App.getInstance().getApplicationContext(),
                            mId + "", App.getInstance().getPackageName() + "cmypraiseFragment.txt") {
                @Override
                public ArrayList<TopicalEntry.PraiseBean> jsonToBean(JSONObject jsonObject) {
                    ArrayList<TopicalEntry.PraiseBean> socityNewsAarry = null;
                    try {
                        JSONArray result = jsonObject.getJSONArray("result");
                        if (result != null) {
                            socityNewsAarry = new ArrayList<TopicalEntry.PraiseBean>();
                            Gson gs = new Gson();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                TopicalEntry.PraiseBean entry = gs.fromJson(item.toString(), TopicalEntry.PraiseBean.class);
                                socityNewsAarry.add(entry);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return socityNewsAarry;
                }
            };


    protected void getDatas(int offset) {
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/threads/" + mId + "/attitudes?state=1";
        url += "&max=" + 100 + "";
        dataFileCacheManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), offset > 1).setCallback(callback);
    }

    public void resrashData() {
        page = 1;
        getDatas(page);
    }


    private DataRequest.DataCallback<ArrayList<TopicalEntry.PraiseBean>>
            callback = new DataRequest.DataCallback<ArrayList<TopicalEntry.PraiseBean>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<TopicalEntry.PraiseBean> data) {
            //teset
//            data = new ArrayList<>();
//            for (int i = 0; i < 5; i++) {
//                TopicalEntry.PraiseBean bean = new TopicalEntry.PraiseBean();
//                bean.setNickname("张三" + i);
//                data.add(bean);
//            }

            if (!(data == null || data.isEmpty())) {
                adapter.setHeaderView(null);
//                adapter.update(data, isAppend);
            } else {
                if (adapter.getHeaderView() == null)
                    setHeader(pullToRefreshRecyclerView.getRefreshableView());
            }
            adapter.update(data, isAppend);
            pullToRefreshRecyclerView.onRefreshComplete();
        }

        @Override
        public void onFail(ApiException e) {
            e.printStackTrace();
            if (adapter.getHeaderView() == null)
                setHeader(pullToRefreshRecyclerView.getRefreshableView());
            pullToRefreshRecyclerView.onRefreshComplete();
        }
    };

    @Override
    public BaseRecyclerViewAdapter getRecyclerViewAdapter() {
        if (adapter == null) {
            RecycleAdapter adapter1 = new RecycleAdapter();
            adapter1.setOnItemViewClickListener(new BaseRecyclerViewHolder.OnRecyclerItemViewClickListener() {
                @Override
                public void onItemViewClick(View v) {
                    try {
                        int position = (int) v.getTag();
                        List<TopicalEntry.PraiseBean> list = adapter.getData();
                        if (list != null && position >= 0 && position < list.size()) {
                            TopicalEntry.PraiseBean itemData = list.get(position);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            adapter = adapter1;
        }
        return adapter;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        page = 1;
        getDatas(page);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        page++;
        getDatas(page);
    }

    public class RecycleAdapter extends BaseRecyclerViewDataAdapter<TopicalEntry.PraiseBean> implements BaseRecyclerViewHolder.OnRecyclerItemViewClickListener {
        public boolean bInit;
        private final String STATE_LIST = "ListTleantAdapter.mlist";
        private BaseRecyclerViewHolder.OnRecyclerItemViewClickListener itemViewClickListener;
        public static final int TYPE_HEADER = 0;
        public static final int TYPE_NORMAL = 1;
        private View mHeaderView;

        public void saveInstanceState(Bundle outState) {
            if (bInit) {
                outState.putSerializable(STATE_LIST, (Serializable) list);
            }
        }

        private IButtonClickListenr callback;

        public void setCallback(IButtonClickListenr callback) {
            this.callback = callback;
        }

        public void init(Bundle savedInstanceState) {
            ArrayList<TopicalEntry.PraiseBean> sList;
            sList = (ArrayList<TopicalEntry.PraiseBean>) savedInstanceState.getSerializable(STATE_LIST);
            if (sList != null) {
                list = sList;
                notifyDataSetChanged();
                bInit = true;
            }
        }

        public void setHeaderView(View headerView) {
            mHeaderView = headerView;
            notifyItemInserted(0);
        }

        public View getHeaderView() {
            return mHeaderView;
        }


        public void update(List<TopicalEntry.PraiseBean> data, boolean isAdd) {
//            if (data == null || data.size() <= 0) {
//                return;
//            }
            if (!isAdd || list == null) {
                list = data;
                notifyDataSetChanged();
            } else {
                int index = list.size();
                list.addAll(data);
                notifyItemRangeChanged(index, data.size());
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mHeaderView == null) return TYPE_NORMAL;
            if (position == 0) return TYPE_HEADER;
            return TYPE_NORMAL;
        }

        @Override
        public MyBaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mHeaderView != null && viewType == TYPE_HEADER)
                return new RecycleAdapter.MyBaseRecyclerViewHolder(mHeaderView, viewType);
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_my_praise_list_item, parent, false);
            return new RecycleAdapter.MyBaseRecyclerViewHolder(layout, viewType);
        }

        @Override
        public void onBindViewHolder(BaseRecyclerViewHolder hodler, int position) {
            if (getItemViewType(position) == TYPE_HEADER) return;
            int pos = getRealPosition(hodler);
            View bodyView = hodler.getView(R.id.body_view);
            CircleButton logo = hodler.getView(R.id.head_view);
            TextView nameText = hodler.getView(R.id.user_name);
            TextView titleText = hodler.getView(R.id.user_signature);
            TwoRelyView addAttion = hodler.getView(R.id.add_attention);
            addAttion.setVisibility(View.INVISIBLE);
            TopicalEntry.PraiseBean info = list.get(pos);
            if (info == null) return;
            Util.LoadImageErrorUrl(logo, info.getAvatar_url(), null, R.drawable.user_default_commend);
            nameText.setText(info.getNickname());
            bodyView.setTag(R.id.tag_replay_cid, info);
            bodyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TopicalEntry.PraiseBean object = (TopicalEntry.PraiseBean) v.getTag(R.id.tag_replay_cid);
                    if (object != null) {
                        IntentUtil.gotoPersonHomeAct(v.getContext(), object.getUser_id());
//                        callback.onLbtClick(IButtonClickType.ITEM_CLICK, new IButtonClickData(v, object));
                    }
                }
            });
        }

        public int getRealPosition(RecyclerView.ViewHolder holder) {
            int position = holder.getLayoutPosition();
            return mHeaderView == null ? position : position - 1;
        }

        @Override
        public int getItemCount() {
//            return mHeaderView == null ? list == null ? 0 : list.size() : list == null ? 0 : list.size() + 1;
            if (mHeaderView == null) {
                return list == null ? 0 : list.size();
            } else {
                return list == null ? 1 : list.size() + 1;
            }
        }


        public void setOnItemViewClickListener(BaseRecyclerViewHolder.OnRecyclerItemViewClickListener listener) {
            itemViewClickListener = listener;
        }

        @Override
        public void onItemViewClick(View v) {
            if (itemViewClickListener != null) {
                itemViewClickListener.onItemViewClick(v);
            }
        }

        class MyBaseRecyclerViewHolder extends BaseRecyclerViewHolder {
            public MyBaseRecyclerViewHolder(View itemView, int viewType) {
                super(itemView, viewType);
                if (itemView == mHeaderView) return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (commendUpdateSubscription != null)
            commendUpdateSubscription.unsubscribe();
    }

}
