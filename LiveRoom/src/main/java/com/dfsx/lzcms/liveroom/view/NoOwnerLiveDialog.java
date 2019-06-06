package com.dfsx.lzcms.liveroom.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.LiveChannelManager;
import com.dfsx.lzcms.liveroom.util.RXBusUtil;

/**
 * Created by liuwb on 2016/12/7.
 */
public class NoOwnerLiveDialog {

    private Activity context;

    private AlertDialog dialog;

    private TextView concernText;

    private ImageButton btnCancel;

    private LiveChannelManager channelManager;

    private long needConcernUserId;
    private Handler handler = new Handler(Looper.getMainLooper());

    private CustomeProgressDialog loading;

    public NoOwnerLiveDialog(Activity context) {
        this.context = context;
        channelManager = new LiveChannelManager(context);
    }

    private void create() {
        dialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT).create();
        dialog.show();
        dialog.getWindow().setContentView(R.layout.item_no_live);
        concernText = (TextView) dialog.getWindow().findViewById(R.id.btn_concern);
        btnCancel = (ImageButton) dialog.getWindow().findViewById(R.id.btn_cancel);

        concernText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (needConcernUserId == 0) {
                    return;
                }
                loading = CustomeProgressDialog.show(context, "加载中...");
                channelManager.addConcern(needConcernUserId, new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        if (data) {
                            concernText.setText("已关注");
                            RXBusUtil.sendConcernChangeMessage(true, 1);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000);

                        } else {
                            Toast.makeText(context, "关注失败", Toast.LENGTH_SHORT).show();
                        }
                        if (loading != null) {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        if (loading != null) {
                            loading.dismiss();
                        }
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 要关注的人的Id
     *
     * @param needConcernUserId
     */
    public void show(long needConcernUserId) {
        this.needConcernUserId = needConcernUserId;
        if (context != null && !context.isFinishing()) {
            create();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
