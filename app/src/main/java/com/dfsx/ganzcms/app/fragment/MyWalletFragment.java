package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.DeepColorSwitchTopbarActivity;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.ganzcms.app.view.TwoRelyView;
import com.dfsx.lzcms.liveroom.model.UserMoneyInfo;
import com.dfsx.lzcms.liveroom.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by liuwb on 2016/10/27.
 */
public class MyWalletFragment extends Fragment {

    private TextView extraText;

    private Button btnBuy;

    private MyDataManager dataManager;

    private CustomeProgressDialog loading;

    private View moneyRecordLogView;

    private ImageView backImage;

    private TwoRelyView myBuyGlodView;

    private TwoRelyView myEarnGoldView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_my_wallet, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backImage = (ImageView) view.findViewById(R.id.back_wa_finish);
        extraText = (TextView) view.findViewById(R.id.my_extra);
        btnBuy = (Button) view.findViewById(R.id.btn_buy);
        myBuyGlodView = (TwoRelyView) view.findViewById(R.id.my_add_money);
        myEarnGoldView = (TwoRelyView) view.findViewById(R.id.my_receive_money);

        moneyRecordLogView = view.findViewById(R.id.look_record);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                WhiteTopBarActivity.startAct(getActivity(),
                //                        AddMoneyFragment.class.getName(),
                //                        "充值中心");
                LSUtils.toastNoFunction(getContext());
            }
        });

        moneyRecordLogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> logList = new ArrayList<>();
                logList.add("充值乐币");
                logList.add("收到乐币");
                DeepColorSwitchTopbarActivity.start(getActivity(), MyMoneyRecordLogList.class.getName(),
                        logList, "");
                //                Intent intent = new Intent(getActivity(), DeepColorSwitchTopbarActivity.class);
                //                intent.putExtra(DeepColorSwitchTopbarActivity.KEY_FRAGMENT_NAME,
                //                        MyMoneyRecordLogList.class.getName());
                //                intent.putExtra(DeepColorSwitchTopbarActivity.KEY_SWITCH_STRING_LIST, logList);
                //                intent.putExtra(DeepColorSwitchTopbarActivity.KEY_RIGHT_RESOURCE,
                //                        R.drawable.icon_topbar_delete);
                //                startActivity(intent);
            }
        });

        dataManager = new MyDataManager(getContext());
        getData();
    }

    public void getData() {
        loading = CustomeProgressDialog.show(getActivity(), "加载中...");
        dataManager.getMyMoneyInfo(new DataRequest.DataCallback<UserMoneyInfo>() {
            @Override
            public void onSuccess(boolean isAppend, UserMoneyInfo data) {
                if (data != null) {
                    String goldText = data.getCoins() > 10000 ?
                            StringUtil.moneyToString(data.getCoins()) :
                            (int) data.getCoins() + "";
                    extraText.setText(goldText);
                    myBuyGlodView.getSecondView().setText((int) data.getTotalChargeCoins() + "");
                    myEarnGoldView.getSecondView().setText((int) data.getTotalEarningCoins() + "");
                }
                if (loading != null) {
                    loading.dismiss();
                }
            }

            @Override
            public void onFail(ApiException e) {
                Log.e("TAG", "error == " + e.getMessage());
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "数据刷新失败", Toast.LENGTH_SHORT).show();
                }
                if (loading != null) {
                    loading.dismiss();
                }
            }
        });
    }
}
