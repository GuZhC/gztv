package com.dfsx.ganzcms.app.act;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.ganzcms.app.R;

/**
 * Created by liuwb on 2017/3/28.
 */
public class ReportActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private Activity activity;
    private LinearLayout reasonList;
    private CheckBox otherReasonCheckBox;

    private EditText editOtherText;

    private Button btnReport;

    private int checkId;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.act_report);

        reasonList = (LinearLayout) findViewById(R.id.report_reason_list);
        otherReasonCheckBox = (CheckBox) findViewById(R.id.check_other);
        editOtherText = (EditText) findViewById(R.id.edit_other_text);
        btnReport = (Button) findViewById(R.id.btn_report);

        findViewById(R.id.close_act_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for (int i = 0; i < reasonList.getChildCount(); i++) {
            View childView = reasonList.getChildAt(i);
            if (childView instanceof CheckBox) {
                ((CheckBox) childView).setOnCheckedChangeListener(this);
            }
        }

        otherReasonCheckBox.setOnCheckedChangeListener(this);

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLeagle()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            Toast.makeText(activity, "举报成功", Toast.LENGTH_SHORT).show();
                        }
                    }, 500);
                }
            }
        });
    }

    private boolean isLeagle() {
        if (checkId == 0) {
            Toast.makeText(activity, "请选择举报原因", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (checkId == otherReasonCheckBox.getId() &&
                TextUtils.isEmpty(editOtherText.getText().toString())) {
            Toast.makeText(activity, "请输入举报原因", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            if (buttonView.getId() == checkId) {
                checkId = 0;
            }
        } else {
            checkId = buttonView.getId();
            if (checkId != otherReasonCheckBox.getId()) {
                otherReasonCheckBox.setChecked(false);

            }
            for (int i = 0; i < reasonList.getChildCount(); i++) {
                View childView = reasonList.getChildAt(i);
                if (childView.getId() != buttonView.getId() && childView instanceof CheckBox) {
                    ((CheckBox) childView).setChecked(false);
                }
            }
        }
    }


}
