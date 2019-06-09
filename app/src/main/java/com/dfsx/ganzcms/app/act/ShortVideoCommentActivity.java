package com.dfsx.ganzcms.app.act;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.dfsx.core.common.Util.SystemBarTintManager;
import com.dfsx.core.common.Util.Util;
import com.dfsx.ganzcms.app.R;

public class ShortVideoCommentActivity extends AppCompatActivity {

    private TextView tvBack;
    private SystemBarTintManager systemBarTintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_video_comment);
        systemBarTintManager = Util.applyKitKatTranslucency(this, ContextCompat.getColor(this,R.color.transparent));
        initView();
    }

    private void initView() {
        tvBack = (TextView) findViewById(R.id.iv_video_comt_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
