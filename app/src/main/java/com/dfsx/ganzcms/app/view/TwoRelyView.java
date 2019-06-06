package com.dfsx.ganzcms.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dfsx.ganzcms.app.R;

/**
 * 两个依赖的View, First, two
 * Created by liuwb on 2016/10/21.
 */
public class TwoRelyView extends LinearLayout {
    private Context context;

    private TextView firstView;

    private TextView secondView;

    private int firstTextColor;
    private float firstTextSize;

    private int secondTextColor;
    private float secondTextSize;

    private int firstBackground;
    private int secondBackground;

    private int firstDrawableTop;
    private int firstDrawableBottom;
    private int firstDrawableLeft;
    private int firstDrawableRight;

    private int secondDrawableTop;
    private int secondDrawableBottom;
    private int secondDrawableLeft;
    private int secondDrawableRight;

    private float firstDrawablePading;

    private float secondDrawablePading;

    private float viewSpace;

    private CharSequence firstText, secondText;

    private int orientation = VERTICAL;


    public TwoRelyView(Context context) {
        this(context, null);
    }

    public TwoRelyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwoRelyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TwoRelyView);

            firstTextColor = ta.getColor(R.styleable.TwoRelyView_firstTextColor,
                    Color.parseColor("#363636"));
            firstTextSize = ta.getDimension(R.styleable.TwoRelyView_firstTextSize, sp2px(14));
            firstBackground = ta.getResourceId(R.styleable.TwoRelyView_firstBackground, 0);
            firstDrawableBottom = ta.getResourceId(R.styleable.TwoRelyView_firstDrawableBottom, 0);
            firstDrawableTop = ta.getResourceId(R.styleable.TwoRelyView_firstDrawableTop, 0);
            firstDrawableLeft = ta.getResourceId(R.styleable.TwoRelyView_firstDrawableLeft, 0);
            firstDrawableRight = ta.getResourceId(R.styleable.TwoRelyView_firstDrawableRight, 0);


            firstDrawablePading = ta.getDimension(R.styleable.TwoRelyView_firstDrawablePadding, 0);

            viewSpace = ta.getDimension(R.styleable.TwoRelyView_viewSpace, dp2px(5));
            orientation = ta.getInt(R.styleable.TwoRelyView_viewOrientation, VERTICAL);

            secondTextColor = ta.getColor(R.styleable.TwoRelyView_secondTextColor,
                    Color.parseColor("#363636"));
            secondTextSize = ta.getDimension(R.styleable.TwoRelyView_secondTextSize, sp2px(14));
            secondBackground = ta.getResourceId(R.styleable.TwoRelyView_secondBackground, 0);
            secondDrawableBottom = ta.getResourceId(R.styleable.TwoRelyView_secondDrawableBottom, 0);
            secondDrawableTop = ta.getResourceId(R.styleable.TwoRelyView_secondDrawableTop, 0);
            secondDrawableLeft = ta.getResourceId(R.styleable.TwoRelyView_secondDrawableLeft, 0);
            secondDrawableRight = ta.getResourceId(R.styleable.TwoRelyView_secondDrawableRight, 0);

            secondDrawablePading = ta.getDimension(R.styleable.TwoRelyView_secondDrawablePadding, 0);

            firstText = ta.getText(R.styleable.TwoRelyView_firstText);

            secondText = ta.getText(R.styleable.TwoRelyView_secondText);
        }

        firstView = createView(R.id.two_rely_first);
        secondView = createView(R.id.two_rely_second);

        firstView.setTextColor(firstTextColor);
        firstView.setTextSize(TypedValue.COMPLEX_UNIT_PX, firstTextSize);
        firstView.setText(firstText);
        firstView.setCompoundDrawablesWithIntrinsicBounds(firstDrawableLeft, firstDrawableTop,
                firstDrawableRight, firstDrawableBottom);
        firstView.setCompoundDrawablePadding((int) firstDrawablePading);
        if (firstBackground != 0) {
            firstView.setBackgroundResource(firstBackground);
        }

        secondView.setTextColor(secondTextColor);
        secondView.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondTextSize);
        secondView.setText(secondText);
        secondView.setCompoundDrawablesWithIntrinsicBounds(secondDrawableLeft, secondDrawableTop,
                secondDrawableRight, secondDrawableBottom);
        secondView.setCompoundDrawablePadding((int) secondDrawablePading);
        if (secondBackground != 0) {
            secondView.setBackgroundResource(secondBackground);
        }

        LayoutParams secondLayoutParams = (LayoutParams) secondView.getLayoutParams();

        secondLayoutParams.setMargins(0, (int) viewSpace, 0, 0);

        setOrientation(orientation == 0 ? HORIZONTAL : VERTICAL);

        setGravity(Gravity.CENTER_HORIZONTAL);

        addView(firstView);

        addView(secondView);
    }

    public void changeFirstViewDrawableRight(int NewViewID) {
        firstView.setCompoundDrawablesWithIntrinsicBounds(firstDrawableLeft, firstDrawableTop,
                NewViewID, firstDrawableBottom);
    }

    public void changeSecondText(String newText) {
        secondView.setText(newText);
    }


    public TextView getFirstView() {
        return firstView;
    }

    public TextView getSecondView() {
        return secondView;
    }

    protected int dp2px(float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    private float sp2px(float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    private TextView createView(int id) {
        TextView tv = new TextView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(params);
        tv.setId(id);
        return tv;
    }
}
