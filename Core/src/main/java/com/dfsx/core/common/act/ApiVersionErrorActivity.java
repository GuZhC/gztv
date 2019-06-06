package com.dfsx.core.common.act;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.dfsx.core.CoreApp;
import com.dfsx.core.R;
import com.dfsx.core.network.CommonHttpErrorCodeHelper;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

/**
 * Created by liuwb on 2017/7/17.
 */
public class ApiVersionErrorActivity extends BaseActivity {

    private Button btnCancel;
    private Button btnOk;
    private Context context;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.act_api_version_error);
        btnCancel = (Button) findViewById(R.id.btn_cancle);
        btnOk = (Button) findViewById(R.id.btn_ok);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoreApp.getInstance().exitApp();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TedPermission(CoreApp.getInstance().getApplicationContext())
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                boolean isUpdated = CoreApp.getInstance().downloadAndUpdateApp();
                                CommonHttpErrorCodeHelper.setAPPUpdateState(isUpdated);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 100);
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> arrayList) {
                                Toast.makeText(context, "没有权限", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).setDeniedMessage(context.getResources().getString(R.string.denied_message)).
                        setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });
    }


}
