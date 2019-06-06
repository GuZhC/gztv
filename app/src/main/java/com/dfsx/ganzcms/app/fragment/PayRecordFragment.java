package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
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

/**
 * Created by liuwb on 2016/10/27.
 */
public class PayRecordFragment extends AbsListFragment {

    public static final int PAGE_SIZE = 20;
    private PayRecordAdapter adapter;
    //    private MyDataManager dataManager;

    private UserTradeLogHelper tradeLogHelper;

    private int pageCount = 1;

    private EmptyView emptyView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (act instanceof WhiteTopBarActivity) {
            ((WhiteTopBarActivity) act).getTopBarRightText().
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clear();
                        }
                    });
        }

        //        dataManager = new MyDataManager(act);
        tradeLogHelper = new UserTradeLogHelper(act);
        getData(1);
    }

    @Override
    public void setListAdapter(ListView listView) {
        if (adapter == null) {
            adapter = new PayRecordAdapter(context);
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

    private void getData(int page) {
        tradeLogHelper.getTradeRecordList(page, PAGE_SIZE, new DataRequest.DataCallback<TradeRecords>() {
            @Override
            public void onSuccess(boolean isAppend, TradeRecords data) {
                emptyView.loadOver();
                if (adapter != null && data != null) {
                    adapter.update(data.getData(), isAppend);
                }

                pullToRefreshListView.onRefreshComplete();
            }

            @Override
            public void onFail(ApiException e) {
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
                LogUtils.e("TAG", "error === " + e.getMessage());
            }
        });
        //        dataManager.getMyTradeRecordList(page, PAGE_SIZE, new DataRequest.DataCallback<TradeRecords>() {
        //            @Override
        //            public void onSuccess(boolean isAppend, TradeRecords data) {
        //                emptyView.loadOver();
        //                if (adapter != null) {
        //                    adapter.update(data.getData(), isAppend);
        //                }
        //
        //                pullToRefreshListView.onRefreshComplete();
        //            }
        //
        //            @Override
        //            public void onFail(ApiException e) {
        //                pullToRefreshListView.onRefreshComplete();
        //                emptyView.loadOver();
        //                LogUtils.e("TAG", "error === " + e.getMessage());
        //            }
        //        });
    }

    private void clear() {

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

    class PayRecordAdapter extends BaseListViewAdapter<TradeRecords.TradeRecordItem> {

        public PayRecordAdapter(Context context) {
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

            TradeRecords.TradeRecordItem record = list.get(position);
            TradeAction itemActionInfo = record.getTradeActionInfo();

            String payTextStr = itemActionInfo == null ?
                    record.getAction() :
                    itemActionInfo.getTADescribeText();
            payText.setText(payTextStr);
            time.setText(getTime(record.getTradeTime()));
            String nameText = itemActionInfo == null ? "null" : itemActionInfo.getTATagPersonNickName();
            name.setText(nameText);
            String numText = itemActionInfo == null ? "0" : (int) itemActionInfo.getTACoins() + "";
            payNum.setText(numText);
            String logoUrl = itemActionInfo == null ? "null" : itemActionInfo.getTATagPersonLogo();
            LSLiveUtils.showUserLogoImage(act, logo, logoUrl);

            String action = record.getAction();
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
}
