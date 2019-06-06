package com.dfsx.ganzcms.app.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.view.AbsPopupwindow;

public class QianDaoPopwindow extends AbsPopupwindow {

    private TextView qianDaoNumTextView;
    private Button btnQiandao;
    private MyDataManager dataManager;

    private IsLoginCheck loginCheck;

    private Handler handler = new Handler(Looper.getMainLooper());

    public QianDaoPopwindow(Context context) {
        super(context);
        dataManager = new MyDataManager(context);
    }

    @Override
    protected int getPopupwindowLayoutId() {
        return R.layout.qian_dao_pop_layout;
    }

    @Override
    protected int getPopAnimationStyle() {
        return R.style.DownEnterUpExitPopupStyle;
    }

    @Override
    protected void onInitWindowView(View popView) {
        qianDaoNumTextView = (TextView) popView.findViewById(R.id.qian_dao_day_num_text);
        btnQiandao = (Button) popView.findViewById(R.id.btn_qian_dao);

        btnQiandao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataManager.qianDao(new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        qianDaoNumTextView.setText("1");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                            }
                        }, 1000);
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
            }
        });
    }


    /**
     * 智能签到
     */
    public void autoQianDao(Activity activity, final View v) {
        if (activity != null && !activity.isFinishing()) {
            loginCheck = new IsLoginCheck(activity);
            boolean isLogin = loginCheck.checkLogin();
            if (isLogin) {
                dataManager.isQianDao(new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        if (data) {
                            Toast.makeText(context, "已签到", Toast.LENGTH_SHORT).show();
                        } else {
                            show(v);
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        show(v);
                    }
                });
            }
        }
    }

}
