package com.dfsx.ganzcms.app.act;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.ganzcms.app.R;

/**
 * Created by liuwb on 2017/3/10.
 */
public class DeepColorTopbarActivity extends DefaultFragmentActivity {

    private LinearLayout _topCenterView;
    protected ImageView _topLeftCloseImage;
    private TextView _topRightTextView;
    protected View _topBarView;

    public static final String KEY_TITLE = "DeepColorTopbarActivity.top_title";
    public static final String KEY_RIGHT_TITLE = "DeepColorTopbarActivity.top_right_title";
    public static final String KEY_RIGHT_RESOURCE = "DeepColorTopbarActivity.top_right_resource";

    private String _title;
    private String _rightTitlte;
    private int _rightRes;

    public static void start(Context context, String fragmentName, String title, String rightTitle) {
        Intent intent = new Intent(context, DeepColorTopbarActivity.class);
        intent.putExtra(DeepColorTopbarActivity.KEY_FRAGMENT_NAME,
                fragmentName);
        intent.putExtra(DeepColorTopbarActivity.KEY_TITLE, title);
        intent.putExtra(DeepColorTopbarActivity.KEY_RIGHT_TITLE,
                rightTitle);
        context.startActivity(intent);
    }

    public static void start(Context context, String fragmentName, String title, int rightResId) {
        Intent intent = new Intent(context, DeepColorTopbarActivity.class);
        intent.putExtra(DeepColorTopbarActivity.KEY_FRAGMENT_NAME,
                fragmentName);
        intent.putExtra(DeepColorTopbarActivity.KEY_TITLE, title);
        intent.putExtra(DeepColorTopbarActivity.KEY_RIGHT_RESOURCE,
                rightResId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        _title = getIntent().getStringExtra(KEY_TITLE);
        _rightTitlte = getIntent().getStringExtra(KEY_RIGHT_TITLE);
        _rightRes = getIntent().getIntExtra(KEY_RIGHT_RESOURCE, 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void addTopView(LinearLayout topViewContainer) {
        super.addTopView(topViewContainer);

        View topBarView = getLayoutInflater().inflate(R.layout.deep_black_topbar, null);
        topViewContainer.addView(topBarView);

        _topBarView = topBarView.findViewById(R.id.top_bar_layout);
        _topCenterView = (LinearLayout) topBarView.findViewById(R.id.top_center_view);
        _topLeftCloseImage = (ImageView) topBarView.findViewById(R.id.close_act_img);
        _topRightTextView = (TextView) topBarView.findViewById(R.id.top_right_text_view);

        _topLeftCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof ITopBarActionListener) {
                    ((ITopBarActionListener) fragment).onActFinish();
                }
                finish();

            }
        });

        setTopCenterView(_topCenterView);

        if (!TextUtils.isEmpty(_rightTitlte)) {
            _topRightTextView.setText(_rightTitlte);
        } else if (_rightRes != 0) {
            try {
                String typeName = getResources().getResourceTypeName(_rightRes);
                if (typeName.equalsIgnoreCase("drawable")) {
                    _topRightTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, _rightRes, 0);
                } else if (typeName.equalsIgnoreCase("string")) {
                    String text = context.getResources().getString(_rightRes);
                    _topRightTextView.setText(text);
                }
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        }

        _topRightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof ITopBarActionListener) {
                    ((ITopBarActionListener) fragment).onRightViewClick(_topRightTextView);
                }
            }
        });
    }


    protected void setTopCenterView(LinearLayout topCenterView) {
        TextView textView = (TextView) topCenterView.findViewById(R.id.title_text);
        if (textView != null && !TextUtils.isEmpty(_title)) {
            textView.setText(_title);
        }
    }

    public interface ITopBarActionListener {
        void onActFinish();

        void onRightViewClick(TextView textView);
    }
}
