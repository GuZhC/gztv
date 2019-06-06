package com.dfsx.ganzcms.app.act;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dfsx.core.common.Util.SystemBarTintManager;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.ganzcms.app.R;

public class GanZiTopBarActivity extends DefaultFragmentActivity {

    public static final String KEY_TITLE_IMAGE_RES = "GanZiTopBarActivity_title_image_res";
    public static final String KEY_TITLE_TEXT = "GanZiTopBarActivity_title_text";

    private View topBar;
    private ImageView actFinish;

    private ImageView titleImage;
    private TextView titleTextView;

    private int titleImageRes;
    private String titleText;

    public static void start(Context context, String className, Intent intent) {
        if (intent == null) {
            intent = new Intent(context, GanZiTopBarActivity.class);
        } else {
            intent.setClass(context, GanZiTopBarActivity.class);
        }
        intent.putExtra(KEY_FRAGMENT_NAME, className);
        context.startActivity(intent);
    }

    public static Bundle getTitleBundle(int titleImageRes, String titleText) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TITLE_IMAGE_RES, titleImageRes);
        bundle.putString(KEY_TITLE_TEXT, titleText);
        return bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemBarTintManager systemBarTintManager = Util.applyKitKatTranslucency(this,
                Color.TRANSPARENT);
        systemBarTintManager.setStatusBarTintEnabled(true);
        systemBarTintManager.setNavigationBarTintEnabled(true);
        if (getIntent() != null) {
            titleImageRes = getIntent().getIntExtra(KEY_TITLE_IMAGE_RES, 0);
            titleText = getIntent().getStringExtra(KEY_TITLE_TEXT);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void addTopView(LinearLayout topViewContainer) {
        topBar = LayoutInflater.from(this)
                .inflate(R.layout.gan_zi_topbar_layout, null);
        topViewContainer.addView(topBar);
        actFinish = (ImageView) findViewById(R.id.act_finish);
        titleImage = (ImageView) findViewById(R.id.title_image);
        titleTextView = (TextView) findViewById(R.id.title_text);

        if (!TextUtils.isEmpty(titleText)){
            titleImage.setVisibility(View.GONE);
            titleTextView.setText(titleText);
        }else if (titleImageRes != 0){
            titleImage.setImageResource(titleImageRes);
        }

        actFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
