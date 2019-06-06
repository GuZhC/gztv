package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.DeepColorSwitchTopbarActivity;
import com.dfsx.ganzcms.app.business.UserTradeLogHelper;
import com.dfsx.ganzcms.app.model.TradeAction;
import com.dfsx.ganzcms.app.model.TradeRecords;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 充值的乐币和收到的礼物信息记录列表
 * Created by liuwb on 2017/3/10.
 */
public class MyMoneyRecordLogList extends AbsListFragment implements DeepColorSwitchTopbarActivity.ISwitchTopBarActionListener {

    public static final int PAGE_SIZE = 20;

    private EmptyView emptyView;

    private MyMoneyRecordAdapter adapter;

    private UserTradeLogHelper tradeLogHelper;

    private final MyMoneyType[] moneyTypes = new MyMoneyType[]
            {
                    new MyMoneyType(MyMoneyType.TYPE_BUY),
                    new MyMoneyType(MyMoneyType.TYPE_RECEIVE)
            };


    private int currentTypeIndex;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tradeLogHelper = new UserTradeLogHelper(context);

        currentTypeIndex = 0;
        refreshData();
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new MyMoneyRecordAdapter(context);
        listView.setAdapter(adapter);
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        refreshData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        moneyTypes[currentTypeIndex].pageCount++;
        getData();
    }

    private void refreshData() {
        moneyTypes[currentTypeIndex].pageCount = 1;
        getData();
    }

    private void getData() {
        tradeLogHelper.getTradeRecordList(moneyTypes[currentTypeIndex].pageCount, PAGE_SIZE,
                moneyTypes[currentTypeIndex].type, new DataRequest.DataCallbackTag<TradeRecords>() {
                    @Override
                    public void onSuccess(Object object, boolean isAppend, TradeRecords data) {
                        pullToRefreshListView.onRefreshComplete();
                        emptyView.loadOver();
                        boolean isBuyGoldType = false;
                        if (object instanceof Integer) {
                            int type = (int) object;
                            isBuyGoldType = type == MyMoneyType.TYPE_BUY;
                        }
                        if (isBuyGoldType) {
                            if (moneyTypes[0].records == null ||
                                    moneyTypes[0].records.getData() == null || !isAppend) {
                                moneyTypes[0].records = data;
                            } else {
                                if (data != null) {
                                    moneyTypes[0].records.getData().addAll(data.getData());
                                }
                            }
                        } else {
                            if (moneyTypes[1].records == null ||
                                    moneyTypes[1].records.getData() == null || !isAppend) {
                                moneyTypes[1].records = data;
                            } else {
                                if (data != null) {
                                    moneyTypes[1].records.getData().addAll(data.getData());
                                }
                            }
                        }
                        if (moneyTypes[currentTypeIndex].records != null) {
                            adapter.update(moneyTypes[currentTypeIndex].
                                    records.getData());
                        }
                    }

                    @Override
                    public void onSuccess(boolean isAppend, TradeRecords data) {
                        Log.e("TAG", "11111111111111111111111111");
                        pullToRefreshListView.onRefreshComplete();
                        emptyView.loadOver();
                    }

                    @Override
                    public void onFail(ApiException e) {
                        pullToRefreshListView.onRefreshComplete();
                        emptyView.loadOver();
                    }
                });
    }

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
    public void onCheckChange(int position, String optionString) {
        currentTypeIndex = position % moneyTypes.length;
        if (moneyTypes[currentTypeIndex].records != null) {
            adapter.update(moneyTypes[currentTypeIndex].records.getData());
        } else {
            adapter.update(null);
            emptyView.loading();
            getData();
        }
    }

    @Override
    public void onActFinish() {

    }

    @Override
    public void onRightViewClick(TextView textView) {

    }

    public class MyMoneyRecordAdapter extends BaseListViewAdapter<TradeRecords.TradeRecordItem> {

        public MyMoneyRecordAdapter(Context context) {
            super(context);
        }

        public void update(List data) {
            update(data, false);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_my_money_record;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            CircleButton logo = holder.getView(R.id.item_logo);
            ImageView viptag = holder.getView(R.id.item_vip_tag);
            TextView userName = holder.getView(R.id.item_user_name);
            TextView logTime = holder.getView(R.id.item_log_time);
            TextView recordLogInfo = holder.getView(R.id.item_record_log);
            TextView increaseMoneyNum = holder.getView(R.id.item_increase_num);

            TradeRecords.TradeRecordItem item = list.get(position);
            TradeAction details = item.getTradeActionInfo();
            String logoUrl = details == null ? "" : details.getTATagPersonLogo();
            LSLiveUtils.showUserLogoImage(context, logo, logoUrl);
            String name = details == null ? "null" : details.getTATagPersonNickName();
            userName.setText(name);
            logTime.setText(getTime(item.getTradeTime()));
            String logInfo = details == null ? "" : details.getTADescribeText();
            recordLogInfo.setText(logInfo);
            String num = details == null ? "0" : details.getTACoins() + "";
            increaseMoneyNum.setText(num);
            String action = item.getAction();
            if ("increase".equals(action) ||
                    "decrease".equals(action)) {
                viptag.setVisibility(View.VISIBLE);
            } else {
                viptag.setVisibility(View.GONE);
            }
        }
    }

    private String getTime(long t) {
        String pp = "yyyy年MM月dd日";
        SimpleDateFormat sdf = new SimpleDateFormat(pp);
        return sdf.format(new Date(t * 1000));
    }

    class MyMoneyType {
        static final int TYPE_BUY = 3;
        static final int TYPE_RECEIVE = 1;

        int type;
        int pageCount;
        TradeRecords records;

        /**
         * @param type #TYPE_BUY, #TYPE_RECEIVE
         */
        public MyMoneyType(int type) {
            this.type = type;
            pageCount = 1;
        }
    }
}
