package com.dfsx.ganzcms.app.act;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.dfsx.ganzcms.app.R;
import com.dfsx.lzcms.liveroom.util.PixelUtil;

import java.util.ArrayList;

/**
 * Created by liuwb on 2017/3/10.
 */
public class DeepColorSwitchTopbarActivity extends DeepColorTopbarActivity {

    public static final String KEY_SWITCH_STRING_LIST = "DeepColorSwitchTopbarActivity.topbar_title_list";
    public static final String KEY_DEFAULT_CHECK_POSITION = "DeepColorSwitchTopbarActivity.topbar_checked_position";

    public ArrayList<String> _switchList;
    private int _checkPosition;

    public static void start(Context context, String fragmentName, ArrayList<String> switchOptionList,
                             String rightTitile) {
        start(context, fragmentName, switchOptionList, 0, rightTitile);
    }

    public static void start(Context context, String fragmentName, ArrayList<String> switchOptionList, int checkPos,
                             String rightTitile) {
        Intent intent = new Intent(context, DeepColorSwitchTopbarActivity.class);
        intent.putExtra(DeepColorSwitchTopbarActivity.KEY_FRAGMENT_NAME,
                fragmentName);
        intent.putExtra(DeepColorSwitchTopbarActivity.KEY_SWITCH_STRING_LIST, switchOptionList);
        intent.putExtra(DeepColorSwitchTopbarActivity.KEY_DEFAULT_CHECK_POSITION, checkPos);
        intent.putExtra(DeepColorSwitchTopbarActivity.KEY_RIGHT_TITLE,
                rightTitile);
        context.startActivity(intent);
    }

    public static void start(Context context, String fragmentName, ArrayList<String> switchOptionList,
                             int rightResId) {
        start(context, fragmentName, switchOptionList, 0, rightResId);
    }

    public static void start(Context context, String fragmentName, ArrayList<String> switchOptionList, int checkPos,
                             int rightResId) {
        Intent intent = new Intent(context, DeepColorSwitchTopbarActivity.class);
        intent.putExtra(DeepColorSwitchTopbarActivity.KEY_FRAGMENT_NAME,
                fragmentName);
        intent.putExtra(DeepColorSwitchTopbarActivity.KEY_SWITCH_STRING_LIST, switchOptionList);
        intent.putExtra(DeepColorSwitchTopbarActivity.KEY_DEFAULT_CHECK_POSITION, checkPos);
        intent.putExtra(DeepColorSwitchTopbarActivity.KEY_RIGHT_RESOURCE,
                rightResId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        _switchList = getIntent().getStringArrayListExtra(KEY_SWITCH_STRING_LIST);
        _checkPosition = getIntent().getIntExtra(KEY_DEFAULT_CHECK_POSITION, 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setTopCenterView(LinearLayout topCenterView) {
        topCenterView.removeAllViews();

        RadioGroup radioGroup = new RadioGroup(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        radioGroup.setLayoutParams(layoutParams);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        topCenterView.addView(radioGroup);

        if (_switchList != null) {
            for (int i = 0; i < _switchList.size(); i++) {
                radioGroup.addView(createRadioButtonById(i, _switchList.get(i)));
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (fragment instanceof ISwitchTopBarActionListener) {
                    if (checkedId >= 0 && checkedId < _switchList.size()) {
                        ((ISwitchTopBarActionListener) fragment).onCheckChange(checkedId, _switchList.get(checkedId));
                    }
                }
            }
        });
    }

    private RadioButton createRadioButtonById(int id, String text) {
        RadioButton button = new RadioButton(this);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = PixelUtil.dp2px(this, 7);
        params.setMargins(margin, 0, margin, 0);
        button.setLayoutParams(params);
        button.setButtonDrawable(getResources().getDrawable(R.color.transparent));
        button.setTextColor(getStateColor());
        button.setTextSize(18);
        button.setId(id);
        button.setText(text);
        if (id == _checkPosition) {
            button.setChecked(true);
        } else {
            button.setChecked(false);
        }

        return button;
    }

    protected ColorStateList getStateColor() {
        return getResources().getColorStateList(R.color.topbar_switch_color);
    }

    public interface ISwitchTopBarActionListener extends ITopBarActionListener {
        void onCheckChange(int position, String optionString);
    }
}
