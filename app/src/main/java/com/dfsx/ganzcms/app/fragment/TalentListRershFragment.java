package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseRecyclerViewDataAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.core.common.view.recyclerview.RefreshLayout;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.TalentEntry;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * by  create by  heyang  2017/c10/24
 */


public class TalentListRershFragment extends Fragment {
    private RecycleAdapter mAdapter;
    View rootView;
    int offset = 1;
    private int type;
    private RefreshLayout refreshLayout;
    protected Activity act;
    private DataFileCacheManager<ArrayList<TalentEntry>> dataRequest;

    public static TalentListRershFragment newInstance(int type) {
        TalentListRershFragment tabFragment = new TalentListRershFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_telent_rersh_list_custom, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        act = getActivity();
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshr);
//        mRecyclerView = (RecyclerViewUpRefresh) view.findViewById(R.id.recyclerview);
//        mSwipRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiprefreshlayout);
//        mSwipRefreshLayout.setOnRefreshListener(this);
//        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        refreshLayout.setLayoutManager(layoutManager);

        mAdapter = new RecycleAdapter();
        refreshLayout.setAdapter(mAdapter);
//        mRecyclerView.setCanloadMore(true);
//        mRecyclerView.setLoadMoreListener(this);

//        refreshLayout.getRefreshRecycleView().addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                refreshLayout.getSwipeRefreshLayout().setEnabled(layoutManager.findFirstVisibleItemPosition() == 0);
//            }
//        });

        refreshLayout.setOnRefreshListner(new RefreshLayout.RefreshListner() {
            @Override
            public void refresh() {
                pullDown();
            }

            @Override
            public void loadMore() {
                pullUp();
            }
        });

        dataRequest = new
                DataFileCacheManager<ArrayList<TalentEntry>>
                        (App.getInstance().getApplicationContext(),
                                type + "", App.getInstance().getPackageName() + "talentlist_commend.txt") {
                    @Override
                    public ArrayList<TalentEntry> jsonToBean(JSONObject jsonObject) {
                        ArrayList<TalentEntry> list = null;
                        JSONArray result = jsonObject.optJSONArray("data");
                        if (result != null) {
                            list = new ArrayList<TalentEntry>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = null;
                                try {
                                    item = (JSONObject) result.get(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                TalentEntry entry = new Gson().fromJson(item.toString(), TalentEntry.class);
                                list.add(entry);
                            }
                        }
                        return list;
                    }
                };

        getDatas(1);
    }


    protected void getDatas(int offset) {
        String url = App.getInstance().getmSession().getPortalServerUrl() + "/public/users/ranks?type=" + type;
        url += "&page=" + offset + "&size=20&max=50";
        dataRequest.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), offset > 1).setCallback(callback);
    }

    private DataRequest.DataCallback<ArrayList<TalentEntry>>
            callback = new DataRequest.DataCallback<ArrayList<TalentEntry>>() {
        @Override
        public void onSuccess(final boolean isAppend, final ArrayList<TalentEntry> data) {
            if (isAppend && data != null && !data.isEmpty())
                offset++;
            if (isAppend) {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.update(data, isAppend);
                        refreshLayout.setLoadMoreComplete();
                    }
                }, 2000);
            } else {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.update(data, isAppend);
                        refreshLayout.setRefreshComplete();

                    }
                }, 2000);
            }
        }

        @Override
        public void onFail(ApiException e) {
            e.printStackTrace();
            refreshLayout.setRefreshComplete();
        }
    };

    public void pullUp() {
        getDatas(offset);
    }

    public void pullDown() {
        getDatas(1);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            mAdapter.saveInstanceState(outState);
        }
    }

    public void resetData(Bundle outState) {
        if (mAdapter != null) {
            mAdapter.init(outState);
        }
    }

//    @Override
//    public void onRefresh() {
//        pullDown();
//    }

//    @Override
//    public void onLoadMore() {
//        pullUp();
//    }

    public class RecycleAdapter extends BaseRecyclerViewDataAdapter<TalentEntry> {
        public boolean bInit;
        private final String STATE_LIST = "ListTleantAdapter.mlist";

        @Override
        public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(act).inflate(R.layout.talent_item, viewGroup, false);
            return new BaseRecyclerViewHolder(view, viewType);
        }

        public void saveInstanceState(Bundle outState) {
            if (bInit) {
                outState.putSerializable(STATE_LIST, (Serializable) list);
            }
        }

        public void init(Bundle savedInstanceState) {
            ArrayList<TalentEntry> sList;
            sList = (ArrayList<TalentEntry>) savedInstanceState.getSerializable(STATE_LIST);
            if (sList != null) {
                list = sList;
                notifyDataSetChanged();
                bInit = true;
            }
        }

        public void update(List<TalentEntry> data, boolean isAdd) {
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
        public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
            View body = holder.getView(R.id.replay_item_hor);
            TextView title = holder.getView(R.id.replay_user_name);
            TalentEntry entry = list.get(position);
            title.setText(entry.getNickname());
            ImageView userImage = holder.getView(R.id.replay_user_logo);
            TextView postxt = holder.getView(R.id.talent_tx_pos);
            ImageView posImage = holder.getView(R.id.talent_imag_pos);
            posImage.setVisibility(View.VISIBLE);
            Util.LoadThumebImage(userImage, entry.getAvatar_url(), null);
            if (position < 3) {
                posImage.setVisibility(View.VISIBLE);
                postxt.setVisibility(View.GONE);
            } else {
                posImage.setVisibility(View.GONE);
                postxt.setVisibility(View.VISIBLE);
                position++;
                postxt.setText(position + "");
            }

            if (position == 0) {
                posImage.setImageResource(R.drawable.talent_person_firse);
            } else if (position == 1) {
                posImage.setImageResource(R.drawable.talent_person_two);
            } else if (position == 2) {
                posImage.setImageResource(R.drawable.talent_person_three);
            }
            body.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    callback.OnItemClick(v, "");
                }
            });
        }
    }

}
