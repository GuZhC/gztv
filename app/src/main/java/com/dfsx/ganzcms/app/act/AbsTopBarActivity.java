package com.dfsx.ganzcms.app.act;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.dfsx.ganzcms.app.R;

/**
 * 通用的Topbar的activity
 * Created by liuwb on 2016/9/18.
 */
public class AbsTopBarActivity extends SystemBarControllerFragmentActivity {

    protected Activity act;
    protected ImageView leftFinishImg;
    protected TextView titleText;
    protected FrameLayout contentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        setContentView(R.layout.act_top_bar);

        leftFinishImg = (ImageView) findViewById(R.id.left_finish);
        titleText = (TextView) findViewById(R.id.title);
        contentContainer = (FrameLayout) findViewById(R.id.container);

        leftFinishImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setTitle(CharSequence title) {
        titleText.setText(title);
    }

    public void setContainerContentView(int layoutId) {
        View v = LayoutInflater.from(act).inflate(layoutId, null);
        setContainerContentView(v);
    }

    public void setContainerContentView(View v) {
        if (v.getParent() != null && v.getParent() instanceof ViewGroup) {
            ((ViewGroup) (v.getParent())).removeView(v);
        }
        contentContainer.addView(v);
    }

    public int getContainerId() {
        return R.id.container;
    }
}
