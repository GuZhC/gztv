package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.util.PixelUtil;

/**
 * Created by liuwb on 2017/3/13.
 */
public class BeautyFaceSettingsWindow extends AbsPopupwindow implements View.OnClickListener {

    private CheckBox _level0;
    private CheckBox _level1;
    private CheckBox _level2;
    private CheckBox _level3;
    private CheckBox _level4;

    private ImageView _angleImageView;

    private OnLevelChangeListener _levelChangeListener;

    private static final int[] _levelIdArr = new int[]{
            R.id.level_0,
            R.id.level_1,
            R.id.level_2,
            R.id.level_3,
            R.id.level_4};

    private LinearLayout _levelGroup;

    private int currentLevel = -1;

    private int _popWidth;
    private int _popHeight;

    public BeautyFaceSettingsWindow(Context context) {
        super(context);
    }

    @Override
    protected int getPopupwindowLayoutId() {
        return R.layout.pop_beauty_layout;
    }

    @Override
    protected void onInitWindowView(View popView) {
        _popWidth = popView.getMeasuredWidth();
        _popHeight = popView.getMeasuredHeight();

        _level0 = (CheckBox) popView.findViewById(R.id.level_0);
        _level1 = (CheckBox) popView.findViewById(R.id.level_1);
        _level2 = (CheckBox) popView.findViewById(R.id.level_2);
        _level3 = (CheckBox) popView.findViewById(R.id.level_3);
        _level4 = (CheckBox) popView.findViewById(R.id.level_4);
        _levelGroup = (LinearLayout) popView.findViewById(R.id.level_group_view);
        _angleImageView = (ImageView) popView.findViewById(R.id.pop_angle);

        _level0.setOnClickListener(this);
        _level1.setOnClickListener(this);
        _level2.setOnClickListener(this);
        _level3.setOnClickListener(this);
        _level4.setOnClickListener(this);

        setCheckedLevel(0);
    }

    @Override
    protected void setPopupWindowSize() {
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof CheckBox) {
            CheckBox checkedTextView = (CheckBox) v;
            //            checkedTextView.setChecked(!checkedTextView.isChecked());

            int clickLevel = findLevelById(checkedTextView.getId());
            if (clickLevel != -1) {
                int checkLevel = clickLevel;
                setCheckedLevel(checkLevel);
            }
        }
    }

    public void showAtViewTop(View atView) {
        int[] location = new int[2];
        atView.getLocationOnScreen(location);
        int x = 0;
        if (_popHeight == 0) {
            _popHeight = PixelUtil.dp2px(context, 138);
        }
        int y = location[1] - _popHeight;
        int angleWidth = _angleImageView.getWidth() == 0 ? PixelUtil.dp2px(context, 18) :
                _angleImageView.getWidth();
        int maginLeft = location[0] + atView.getWidth() / 2 - angleWidth / 2;
        popupWindow.showAtLocation(atView, Gravity.NO_GRAVITY, x, y);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) _angleImageView.
                getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        params.setMargins(maginLeft, -1, 0, 0);
        _angleImageView.setLayoutParams(params);
    }

    public void setCheckedLevel(int level) {
        if (level < 0) {
            level = 0;
        }
        if (level >= _levelIdArr.length) {
            level = _levelIdArr.length - 1;
        }
        if (currentLevel != level) {
            currentLevel = level;
            if (_levelChangeListener != null) {
                _levelChangeListener.onLevelChange(currentLevel);
            }
        }
        int checkLevelId = _levelIdArr[level];
        boolean isFound = false;
        //因为这里的布局是从左往右排列，等级也是一次增加的
        for (int i = 0; i < _levelGroup.getChildCount(); i++) {
            View childView = _levelGroup.getChildAt(i);
            if (childView instanceof Checkable) {
                Checkable checkedV = (Checkable) childView;
                checkedV.setChecked(!isFound);
                if (childView.getId() == checkLevelId) {
                    isFound = true;
                }

            }
        }
    }

    private int findLevelById(int id) {
        for (int level = 0; level < _levelIdArr.length; level++) {
            if (id == _levelIdArr[level]) {
                return level;
            }
        }
        return -1;
    }

    public void setOnLevelChangeListener(OnLevelChangeListener l) {
        _levelChangeListener = l;
    }

    public interface OnLevelChangeListener {
        void onLevelChange(int level);
    }
}
