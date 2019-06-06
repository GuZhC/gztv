package com.dfsx.ganzcms.app.view;

/**
 * Created by heyang on 2017/7/26
 * 标签组
 */

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.*;
import android.widget.TextView;
import com.dfsx.core.common.Util.Util;
import com.dfsx.ganzcms.app.R;

import java.util.ArrayList;
import java.util.List;

public class TabGrouplayout extends ViewGroup {

    private static final String TAG = "TabGrouplayout";
    private ArrayList<String> allBodys;
    private ArrayList<String> selectodys;

    public TabGrouplayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
    }

    /**
     * 负责设置子控件的测量模式和大小 根据所有子控件设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

//      Log.e(TAG, sizeWidth + "," + sizeHeight);

        // 如果是warp_content情况下，记录宽和高
        int width = 0;
        int height = 0;
        /**
         * 记录每一行的宽度，width不断取最大宽度
         */
        int lineWidth = 0;
        /**
         * 每一行的高度，累加至height
         */
        int lineHeight = 0;

        int cCount = getChildCount();

        // 遍历每个子元素
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            // 测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 得到child的lp
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();
            // 当前子空间实际占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            // 当前子空间实际占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;
            /**
             * 如果加入当前child，则超出最大宽度，则的到目前最大宽度给width，类加height 然后开启新行
             */
            if (lineWidth + childWidth > sizeWidth) {
                width = Math.max(lineWidth, childWidth);// 取最大的
                lineWidth = childWidth; // 重新开启新行，开始记录
                // 叠加当前高度，
                height += lineHeight;
                // 开启记录下一行的高度
                lineHeight = childHeight;
            } else {
                // 否则累加值lineWidth,lineHeight取最大高度
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            // 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
            if (i == cCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }

        }
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width,
                (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);
    }

    /**
     * 存储所有的View，按行记录
     */
    private List<List<View>> mAllViews = new ArrayList<List<View>>();

    public List<List<View>> getmAllViews() {
        return mAllViews;
    }

    public void setmAllViews(List<List<View>> mAllViews) {
        this.mAllViews = mAllViews;
    }

    /**
     * 记录每一行的最大高度
     */
    private List<Integer> mLineHeight = new ArrayList<Integer>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;
        // 存储每一行所有的childView
        List<View> lineViews = new ArrayList<View>();
        int cCount = getChildCount();
        // 遍历所有的孩子
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // 如果已经需要换行
            if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {
                // 记录这一行所有的View以及最大高度
                mLineHeight.add(lineHeight);
                // 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
                mAllViews.add(lineViews);
                lineWidth = 0;// 重置行宽
                lineViews = new ArrayList<View>();
            }
            /**
             * 如果不需要换行，则累加
             */
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);
        }
        // 记录最后一行
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);

        int left = 0;
        int top = 0;
        // 得到总行数
        int lineNums = mAllViews.size();
        for (int i = 0; i < lineNums; i++) {
            // 每一行的所有的views
            lineViews = mAllViews.get(i);
            // 当前行的最大高度
            lineHeight = mLineHeight.get(i);

//          Log.e(TAG, "第" + i + "行 ：" + lineViews.size() + " , " + lineViews);
//          Log.e(TAG, "第" + i + "行， ：" + lineHeight);

            // 遍历当前行所有的View
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child
                        .getLayoutParams();

                //计算childView的left,top,right,bottom
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

//              Log.e(TAG, child + " , l = " + lc + " , t = " + t + " , r ="
//                      + rc + " , b = " + bc);

                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.rightMargin
                        + lp.leftMargin;
            }
            left = 0;
            top += lineHeight;
        }

    }

    public void initData(Context context, final ArrayList<String> arr, final ArrayList<String> select) {
        removeAllViewsInLayout();
        if (arr == null || arr.isEmpty()) return;
        if (allBodys != null) allBodys.clear();
        allBodys = arr;
        selectodys = select;

        /**
         * 创建 textView数组
         */
        final TextView[] textViews = new TextView[arr.size()];

        for (int i = 0; i < arr.size(); i++) {
            final int pos = i;
//            final View view = (View) LayoutInflater.from(context).inflate(R.layout.communti_tag_text, null, false);
//            final TextView text = (TextView) view.findViewById(R.id.txt_item);  //查找  到当前     textView

            MarginLayoutParams lp = new MarginLayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 20;
            lp.rightMargin = 20;
            lp.topMargin = 12;
            lp.bottomMargin = 12;

            /**
             * 将当前  textView  赋值给    textView数组
             */
            boolean iselect = false;
            if (selectodys != null && !selectodys.isEmpty()) {
                if (selectodys.contains(arr.get(i))) iselect = true;
            }

            TextView textView = createTextView(context, arr.get(i), iselect);

            textViews[i] = textView;

            addView(textView, lp);
        }
    }

    public TextView createTextView(Context context, String txt, boolean isSelect) {

        TextView textView = new TextView(context);
        textView.setText(txt);
        textView.setTag(txt);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(13);
        textView.setPadding(Util.dp2px(getContext(), 15), Util.dp2px(context, 8),
                Util.dp2px(context, 15), Util.dp2px(context, 8));

        setTextViewBackground(textView, isSelect);

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String mark = (String) v.getTag();
                if (mark != null && !TextUtils.isEmpty(mark)) {
                    if (selectodys == null) selectodys = new ArrayList<String>();
                    if (isExist(selectodys, mark)) {
                        selectodys.remove(mark);
                        setTextViewBackground((TextView) v, false);
                    } else {
                        selectodys.add(mark);
                        setTextViewBackground((TextView) v, true);
                    }
                }
            }
        });
        return textView;
    }

    protected void setTextViewBackground(TextView view, boolean isSelect) {
        if (isSelect) {
            view.setTextColor(getResources().getColor(R.color.COLOR_WHITE));
            view.setBackground(getResources().getDrawable(R.drawable.news_top_sel_bankground_shape));
        } else {
            view.setTextColor(getResources().getColor(R.color.public_red_bkg));
            view.setBackground(getResources().getDrawable(R.drawable.news_top_bankground_shape));
        }
    }

    public boolean isExist(ArrayList<String> list, String tag) {
        boolean isFlag = false;
        if (list == null || list.isEmpty()) return isFlag;
        return list.contains(tag);
    }

    public ArrayList<String> getCurrentSelection() {
        return selectodys;
    }

    protected ArrayList<String> getAllBody() {
        return allBodys;
    }

}