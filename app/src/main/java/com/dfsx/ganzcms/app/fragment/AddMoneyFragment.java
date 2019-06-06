package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.model.LSGold;
import com.dfsx.lzcms.liveroom.model.UserMoneyInfo;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 充值中心
 * Created by liuwb on 2016/10/27.
 */
public class AddMoneyFragment extends Fragment {

    private Activity act;
    private Context context;

    private TextView myAccountText, myExtra;
    private GridView moneyGrid;
    private RadioGroup payMethod;

    private Button btnPay;

    private MoneyAdapter adapter;

    private MyDataManager dataManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = act = getActivity();
        dataManager = new MyDataManager(context);
        return inflater.inflate(R.layout.frag_add_money, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initAction();
        initData();

    }

    private void initView(View v) {
        myAccountText = (TextView) v.findViewById(R.id.my_account_text);
        myExtra = (TextView) v.findViewById(R.id.my_extra);
        payMethod = (RadioGroup) v.findViewById(R.id.pay_method);
        moneyGrid = (GridView) v.findViewById(R.id.grid_money);
        btnPay = (Button) v.findViewById(R.id.btn_pay);


    }

    private void initData() {
        myAccountText.setText(App.getInstance().getUser() == null ?
                null : App.getInstance().getUser().getUser().getNickname());
        getExtra();
    }

    private void initAction() {
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        adapter = new MoneyAdapter(getActivity());
        moneyGrid.setAdapter(adapter);

        adapter.update(createTest(), false);

        moneyGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null) {
                    List<LSGold> data = adapter.getData();
                    if (!data.get(position).isSelected()) {
                        clearSelectedStatus(data);
                        data.get(position).setIsSelected(true);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "充值渠道还没开通, 尽情期待后续版本!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearSelectedStatus(List<LSGold> data) {
        for (LSGold g : data) {
            g.setIsSelected(false);
        }
    }

    /**
     * 获取余额信息
     */
    private void getExtra() {
        dataManager.getMyMoneyInfo(new DataRequest.DataCallback<UserMoneyInfo>() {
            @Override
            public void onSuccess(boolean isAppend, UserMoneyInfo data) {
                myExtra.setText(data.getCoins() + "乐币");
            }

            @Override
            public void onFail(ApiException e) {
                myExtra.setText(0 + "乐币");
            }
        });
    }

    private ArrayList<LSGold> createTest() {
        ArrayList<LSGold> data = new ArrayList<>();
        data.add(new LSGold(true));
        data.add(new LSGold());
        data.add(new LSGold());
        data.add(new LSGold());
        data.add(new LSGold());
        data.add(new LSGold());
        data.add(new LSGold());
        data.add(new LSGold());
        return data;
    }

    class MoneyAdapter extends BaseListViewAdapter<LSGold> {

        public MoneyAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_add_money;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            View btn = holder.getView(R.id.item_view);
            TextView text = holder.getView(R.id.item_money);
            btn.setBackgroundResource(list.get(position).isSelected() ?
                    R.drawable.bg_money :
                    R.drawable.shape_money_bg);
            text.setText("10乐币/10元");
        }
    }
}
