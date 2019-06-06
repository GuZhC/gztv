package com.dfsx.core.common.act;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.dfsx.core.R;
import com.dfsx.core.common.Util.SystemBarTintManager;
import com.dfsx.core.common.Util.Util;

/**
 * 设置右边的点击事件样例
 * if (act instanceof WhiteTopBarActivity) {
 * ((WhiteTopBarActivity) act).getTopBarRightText().
 * setOnClickListener(new View.OnClickListener() {
 *
 * @Override public void onClick(View v) {
 * clear();
 * }
 * });
 * }
 * <p>
 * 增加URL方式启动
 * String url = "white_top_bar://" +
 * context.getPackageName()
 * + ".default" + "?title=" + title
 * + "#com.dfsx.lscms.app.fragment.AddMoneyFragment";
 * Created by liuwb on 2016/10/17.
 */
public class WhiteTopBarActivity extends DefaultFragmentActivity {

    public static final String KEY_TOPBAR_TITLE = "com.dfsx.lzcms.liveroom.WhiteTopBarActivity_title";
    public static final String KEY_TOPBAR_RIGHT_TEXT =
            "com.dfsx.lzcms.liveroom.WhiteTopBarActivity_KEY_TOPBAR_RIGHT_TEXT";
    public static final String KEY_TOPBAR_RIGHT_IMAGE =
            "com.dfsx.lzcms.liveroom.WhiteTopBarActivity_KEY_TOPBAR_RIGHT_TEXT";
    public static final String KEY_TOPBAR_BANKGROUND_IMAGE =
            "com.dfsx.lzcms.liveroom.WhiteTopBarActivity_KEY_TOPBAR_BANKGROUND_TEXT";
    //heyang  2016-11-7
    public static final String KEY_PARAM = "type";
    public static final String KEY_PARAM2 = "type2";
    private ImageView backImg;
    protected TextView titleText;

    private TextView rightText;

    protected View topbar;

    public static void startAct(Context context, String fragName, String title) {
        startAct(context, fragName, title, null);
    }

    /**
     * @param context
     * @param fragName
     * @param title      标题
     * @param rightTitle 右标题
     */
    public static void startAct(Context context, String fragName, String title, String rightTitle) {
        Intent intent = new Intent(context, WhiteTopBarActivity.class);
        intent.putExtra(KEY_FRAGMENT_NAME, fragName);
        intent.putExtra(KEY_TOPBAR_TITLE, title);
        intent.putExtra(KEY_TOPBAR_RIGHT_TEXT, rightTitle);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    //heyang  2016/11/7  扩展可以传递一个参数
    public static void startAct(Context context, String fragName, String title, String rightTitle, long param) {
        Intent intent = new Intent(context, WhiteTopBarActivity.class);
        intent.putExtra(KEY_FRAGMENT_NAME, fragName);
        intent.putExtra(KEY_TOPBAR_TITLE, title);
        intent.putExtra(KEY_TOPBAR_RIGHT_TEXT, rightTitle);
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_PARAM, param);
        intent.putExtras(bundle);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void startAct(Context context, String fragName, String title, String rightTitle, Bundle bundle) {
        Intent intent = new Intent(context, WhiteTopBarActivity.class);
        intent.putExtra(KEY_FRAGMENT_NAME, fragName);
        intent.putExtra(KEY_TOPBAR_TITLE, title);
        intent.putExtra(KEY_TOPBAR_RIGHT_TEXT, rightTitle);
        intent.putExtras(bundle);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 顶部右边不是文本是图片
     *
     * @param context
     * @param fragName
     * @param title
     * @param rightImage
     * @param bundle
     */
    public static void startAct(Context context, String fragName, String title, int rightImage, Bundle bundle) {
        Intent intent = new Intent(context, WhiteTopBarActivity.class);
        intent.putExtra(KEY_FRAGMENT_NAME, fragName);
        intent.putExtra(KEY_TOPBAR_TITLE, title);
        intent.putExtra(KEY_TOPBAR_RIGHT_IMAGE, rightImage);
        if (bundle != null)
            intent.putExtras(bundle);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backImg = (ImageView) findViewById(R.id.back_finish);
        titleText = (TextView) findViewById(R.id.title);
        rightText = (TextView) findViewById(R.id.right_text);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setTitle();
        setRightText();
        //heyang  2018/3/5  是否要求设置背景图片
//        setTopBarBackgroundRes();
    }

    public void applyKitKatTranslucency(Activity activity, int clr) {

        // KitKat translucent navigation/status bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
        }
        SystemBarTintManager mTintManager = new SystemBarTintManager(activity);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setTintColor(clr);
    }

    @Override
    protected void addTopView(LinearLayout topViewContainer) {
        topbar = LayoutInflater.from(context).
                inflate(R.layout.white_top_bar_layout, null);
        topViewContainer.addView(topbar);
    }

    private void setTitle() {
        String title = getIntent().getStringExtra(KEY_TOPBAR_TITLE);
        if (TextUtils.isEmpty(title)) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                title = uri.getQueryParameter("title");
            }
        }

        if (TextUtils.isEmpty(title)) {
            title = "";
        }


        titleText.setText(title);
    }

    private void setRightText() {
        int rightImag = getIntent().getIntExtra(KEY_TOPBAR_RIGHT_IMAGE, -1);
        String right = getIntent().getStringExtra(KEY_TOPBAR_RIGHT_TEXT);
        if (rightImag == -1) {
            rightText.setText(right);
        } else {
            ViewGroup.LayoutParams lp = rightText.getLayoutParams();
            lp.width = Util.dp2px(context, 18);
            lp.height = Util.dp2px(context, 17);
            rightText.setLayoutParams(lp);
            //            rightText.setCompoundDrawablesWithIntrinsicBounds(0, 0, rightImag, 0);
            rightText.setBackgroundResource(rightImag);
        }
    }

    /**
     * 获取顶部右边的TextView
     *
     * @return
     */
    public TextView getTopBarRightText() {
        return rightText;
    }

    /**
     * 获取title
     *
     * @return
     */
    public TextView getTopBarTitleText() {
        return titleText;
    }

    /**
     * 获取左边的ImageView
     *
     * @return
     */
    public ImageView getTopBarLeft() {
        return backImg;
    }

    /**
     * 设置标题栏的背景
     *
     * @param color
     */
    public void setTopBarBackground(int color) {
        topbar.setBackgroundColor(color);
    }

    /**
     * 设置标题栏的背景
     */
    public void setTopBarBackgroundRes() {
        int resourceId = getIntent().getIntExtra(KEY_TOPBAR_BANKGROUND_IMAGE, -1);
        if (resourceId != 0 && resourceId != -1) {
            RelativeLayout parent = (RelativeLayout) titleText.getParent();
            if (parent != null) {
                parent.setBackgroundResource(resourceId);
                titleText.setTextColor(0xffffffff);
            }
        }
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
