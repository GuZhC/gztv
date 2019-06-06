package com.dfsx.ganzcms.app.act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.ganzcms.app.R;

/**
 * Created by heyang  2018/4/10
 */
public class ActGanziActivity extends WhiteTopBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titleText.setTextColor(getResources().getColor(R.color.red));
    }

    @Override
    protected void addTopView(LinearLayout topViewContainer) {
        super.addTopView(topViewContainer);
        topbar = LayoutInflater.from(context).
                inflate(R.layout.act_ganzi_custom, null);
        LinearLayout topView  = new LinearLayout(context);
        LinearLayout.LayoutParams  p=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Util.dp2px(this,45));
        topView.setLayoutParams(p);
        topViewContainer.addView(topView, p);
        topView.setBackgroundResource(R.drawable.top_bar_bankground);
        topView.addView(topbar);
    }


    public static void startAct(Context context, String fragName, String title, Bundle bundle) {
        Intent intent = new Intent(context, WhiteTopBarActivity.class);
        intent.putExtra(KEY_FRAGMENT_NAME, fragName);
        intent.putExtra(KEY_TOPBAR_TITLE, title);
        intent.putExtras(bundle);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
