package com.dfsx.lzcms.liveroom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.dfsx.lzcms.liveroom.R;

/**
 * Created by liuwb on 2017/6/14.
 */
public class CenterGroupChangeBar extends FrameLayout {

    private Context context;
    private RadioGroup radioGroup;
    private int radioButtonResource;
    private int radioGroupBkgResource;
    private OnBarSelectedChangeListener listener;

    public CenterGroupChangeBar(Context context) {
        this(context, null);
    }

    public CenterGroupChangeBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CenterGroupChangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAtrrs(attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CenterGroupChangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initAtrrs(attrs);
        init();
    }

    private void initAtrrs(AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CenterGroupChangeBar);
        radioButtonResource = ta.getResourceId(R.styleable.CenterGroupChangeBar_radioButtonRes, R.layout.center_bar_item_radio_button);
        radioGroupBkgResource = ta.getResourceId(R.styleable.CenterGroupChangeBar_radioGroupBackgroundRes, 0);
    }

    private void init() {
        LayoutInflater.from(context)
                .inflate(R.layout.center_group_change_bar_layout, this);
        radioGroup = (RadioGroup) findViewById(R.id.group_container);

        if (radioGroupBkgResource != 0) {
            radioGroup.setBackgroundResource(radioGroupBkgResource);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (listener != null) {
                    listener.onSelectedChange(checkedId);
                }
            }
        });
    }

    public void setBarTextArray(int defaultCheckedIndex, String... textArray) {
        if (textArray != null) {
            radioGroup.removeAllViews();
            for (int i = 0; i < textArray.length; i++) {
                RadioButton radioButton = createRadioGroupByResource(i);
                radioButton.setText(textArray[i]);
                if (i == defaultCheckedIndex) {
                    radioButton.setChecked(true);
                } else {
                    radioButton.setChecked(false);
                }
                radioGroup.addView(radioButton);
            }
        }
    }

    public int getCheckItemLeft() {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        return getItemLeft(checkedId);
    }

    public int getItemLeft(int pos) {
        View view = radioGroup.findViewById(pos);
        int left = view != null ? view.getLeft() : -1;
        return left;
    }

    public boolean setCheckIndex(int checkIndex) {
        RadioButton button = (RadioButton) radioGroup.findViewById(checkIndex);
        if (button != null) {
            radioGroup.check(checkIndex);
            return true;
        }
        return false;
    }

    private RadioButton createRadioGroupByResource(int id) {
        RadioButton radioButton = (RadioButton) LayoutInflater.
                from(context).inflate(radioButtonResource, null);
        radioButton.setId(id);

        return radioButton;
    }

    public void setOnBarSelectedChangeListener(OnBarSelectedChangeListener l) {
        this.listener = l;
    }

    public interface OnBarSelectedChangeListener {
        void onSelectedChange(int selectedIndex);
    }
}
