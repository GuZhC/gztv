package com.dfsx.ganzcms.app.act;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import com.dfsx.ganzcms.app.R;

import java.util.ArrayList;

/**
 * Created by liuwb on 2017/7/12.
 */
public class WhiteTopBarSwitchActivity extends DeepColorSwitchTopbarActivity {

    public static void start(Context context, String fragmentName, ArrayList<String> switchOptionList, int checkPos,
                             String rightTitile) {
        Intent intent = new Intent(context, WhiteTopBarSwitchActivity.class);
        intent.putExtra(DeepColorSwitchTopbarActivity.KEY_FRAGMENT_NAME,
                fragmentName);
        intent.putExtra(DeepColorSwitchTopbarActivity.KEY_SWITCH_STRING_LIST, switchOptionList);
        intent.putExtra(DeepColorSwitchTopbarActivity.KEY_DEFAULT_CHECK_POSITION, checkPos);
        intent.putExtra(DeepColorSwitchTopbarActivity.KEY_RIGHT_TITLE,
                rightTitile);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _topLeftCloseImage.setImageResource(R.drawable.icon_arrow_left);
        _topBarView.setBackgroundResource(R.drawable.shape_bg_white_topbar);
    }

    @Override
    protected ColorStateList getStateColor() {
        return getResources().getColorStateList(R.color.topbar_white_switch_color);
    }
}
