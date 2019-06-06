package com.dfsx.shop.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.shop.R;
import com.dfsx.shop.busniness.CreditShopApi;
import com.dfsx.shop.model.ShopExchgRecord;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang on 2017/7/25.
 */
public class ExchangeRcordFragment extends AbsListFragment {
    private MyLiveAdapter adapter;
    private EmptyView emptyView;
    private LinearLayout topView;
    private CustomeProgressDialog loading;
    private int page = 1;
    private long mUserId = -1;
    private TextView _totalScoreTxt;
    private CreditShopApi _creditShopApi;

    public static ExchangeRcordFragment newInstance(long cId) {
        Bundle bundle = new Bundle();
        bundle.putLong("uID", cId);
        ExchangeRcordFragment fragment = new ExchangeRcordFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUserId = bundle.getLong("uID");
        }
        emptyView.loadOver();
        pullToRefreshListView.onRefreshComplete();
        _creditShopApi = new CreditShopApi(getActivity());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                List<ShopExchgRecord> list = adapter.getData();
                if (position >= 0 && position < list.size()) {
                    ShopExchgRecord info = list.get(position);
                    if (info != null) {
//                        Intent intent = new Intent(getActivity(), QuestionInfoAct.class);
//                        intent.putExtra("tid", info.getId());
//                        startActivity(intent);
                    }
                }
            }
        });
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
                inflate(R.layout.no_question_attion_layout, null);

        emptyView.setLoadOverView(emptyLayout);
        emptyView.loading();
        Button btnStart = (Button) emptyLayout.findViewById(R.id.btn_start_live);
        btnStart.setVisibility(View.GONE);
    }

    @Override
    protected void setTopView(FrameLayout topListViewContainer) {
        super.setTopView(topListViewContainer);
        topView = new LinearLayout(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        topView.setLayoutParams(p);
        topListViewContainer.addView(topView, p);
        topView.setBackgroundResource(R.drawable.exchange_top_bankground);
        View topLayout = LayoutInflater.from(context).
                inflate(R.layout.top_exchange_record_layout, null);
        _totalScoreTxt = (TextView) topLayout.findViewById(R.id.head_attnumber);
        topView.addView(topLayout);
    }

    private DataFileCacheManager<ArrayList<ShopExchgRecord>> dataFileCacheManager = new
            DataFileCacheManager<ArrayList<ShopExchgRecord>>
                    (CoreApp.getInstance().getApplicationContext(),
                            4511 + "", CoreApp.getInstance().getPackageName() + "Miagment.txt") {
                @Override
                public ArrayList<ShopExchgRecord> jsonToBean(JSONObject jsonObject) {
                    ArrayList<ShopExchgRecord> socityNewsAarry = null;
                    try {
                        JSONArray result = jsonObject.getJSONArray("data");
                        if (result != null) {
                            socityNewsAarry = new ArrayList<ShopExchgRecord>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                ShopExchgRecord entry = new Gson().fromJson(item.toString(), ShopExchgRecord.class);
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
        String url = CoreApp.getInstance().getShoppServerUrl() + "/public/users/current/orders?";
        url += "page=" + page + "&size=20";
        dataFileCacheManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(CoreApp.getInstance().getCurrentToken())
                .build(), page > 1).setCallback(callback);
    }

    private DataRequest.DataCallback<ArrayList<ShopExchgRecord>> callback = new DataRequest.DataCallback<ArrayList<ShopExchgRecord>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<ShopExchgRecord> data) {
            emptyView.loadOver();
            pullToRefreshListView.onRefreshComplete();
            if (adapter != null) {
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

    public class MyLiveAdapter extends BaseAdapter {
        private final String STATE_LIST = "ListsAdapter.mlist";
        private ArrayList<ShopExchgRecord> items = new ArrayList<ShopExchgRecord>();
        private LayoutInflater inflater;
        public boolean bInit;
        Context mContext = null;

        public MyLiveAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            bInit = false;
            this.mContext = context;
        }

        public void init(Bundle savedInstanceState) {
            ArrayList<ShopExchgRecord> sList;
            sList = (ArrayList<ShopExchgRecord>) savedInstanceState.getSerializable(STATE_LIST);
            if (sList != null) {
                items = sList;
                notifyDataSetChanged();
                bInit = true;
            }
        }

        public ArrayList<ShopExchgRecord> getData() {
            return items;
        }

        public void SetInitStatus(boolean flag) {
            bInit = flag;
        }


        public void saveInstanceState(Bundle outState) {
            if (bInit) {
                outState.putSerializable(STATE_LIST, items);
            }
        }

        public boolean isInited() {
            return bInit;
        }

        public void update(ArrayList<ShopExchgRecord> data, boolean bAddTail) {
            if (data != null && !data.isEmpty()) {
                boolean noData = false;
                if (bAddTail)
                    /*
                    if(items.size() >= data.size()
                            && items.get(items.size() - data.size()).id == data.get(0).id)*/
                    if (items.size() >= data.size()
                            && items.get(items.size() - 1).getCommodity_id() == data.get(data.size() - 1).getCommodity_id())
                        noData = true;
                    else
                        items.addAll(data);
                else {
                    if (items != null) {
                        if (/*items.size() == data.size() && */
                                items.size() > 0 &&
                                        items.get(0).getCommodity_id() == data.get(0).getCommodity_id())
                            noData = false;
                    }
                    if (!noData) {
                        items = data;
                    }
                }
                bInit = true;
                if (!noData)
                    notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
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
            return R.layout.exchange_list_item;
        }

        protected void setViewHolderData(BaseViewHodler holder, int position) {
            ImageView shopImage = holder.getView(R.id.shop_thumb_image);
            TextView itemTiltle = holder.getView(R.id.shop_item_title);
            TextView price = holder.getView(R.id.shop_ingo_meney);
            ImageView exchangStatus = holder.getView(R.id.shop_exchange_status);
            TextView exchangTime = holder.getView(R.id.shop_exchange_time);
            ShopExchgRecord shop = items.get(position);
//            GlideImgManager.getInstance().showImg(context, shopImage, shop.get);
            itemTiltle.setText(shop.getCommodity_name());
            price.setText(shop.getPrice() + "");
            if (shop.getStatus() == 1) {
                exchangStatus.setImageResource(R.drawable.ex_shop_ok);
            } else if (shop.getStatus() == 2) {
                exchangStatus.setImageResource(R.drawable.ex_shop_failed);
            } else if (shop.getStatus() == 3) {
                exchangStatus.setImageResource(R.drawable.ex_shop_ing);
            }
            exchangTime.setText(Util.getTimeString("yyyy/mm/dd", shop.getCreation_time()));
        }
    }
}
