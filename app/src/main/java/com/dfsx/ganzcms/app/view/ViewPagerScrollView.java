package com.dfsx.ganzcms.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.ScrollItem;

import java.util.ArrayList;

/**
 * Created by liuwb on 2016/9/8.
 */
public class ViewPagerScrollView extends FrameLayout implements View.OnClickListener {

    private static final int BAR_TEXT_SIZE_SP = 14;
    private static final int ITEM_MIN_SPACE_DP = 10;

    private static final int DEFAULT_BAR_HEIHT_DP = 32;//dp
    private int SELECTED_COLOR = getResources().getColor(R.color.news_mulity_column_select);
    private int UNSELECTED_COLOR = getResources().getColor(R.color.news_mulity_column_noraml);
    private int barHeghit = DEFAULT_BAR_HEIHT_DP;
    private int LINE_WIDTH;
    private int LINE_COLOR = getResources().getColor(R.color.red);

    private static final int DEFAULT_SELECTED_POSITION = 0;

    private Context context;
    private HorizontalScrollView mHorizontalScrollView;
    private LinearLayout mLinearLayout;
    private View mImageViewContainer;
    private View lineView;
    private ViewPager pager;

    private ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private int lineContainerWidth;
    private int endPosition;
    private int beginPosition;
    private int currentFragmentIndex;
    private int startScrollPosition;
    private boolean isEnd;
    private int oldSelectedPosition;

    public ViewPagerScrollView(Context context) {
        this(context, null);
    }

    public ViewPagerScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
        initAttr(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewPagerScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
        initAttr(attrs);
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.viewpager_scroll_layout, this);
        int textLength = Util.dp2px(context, 4 * BAR_TEXT_SIZE_SP);//按每项为4字计算
        if (lineContainerWidth < textLength) {
            lineContainerWidth = textLength + 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP);
        }

        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.srcoll_view);
        mLinearLayout = (LinearLayout) findViewById(R.id.hsv_content);
        mImageViewContainer = findViewById(R.id.scroll_bottom_view);
        lineView = findViewById(R.id.img_line);
        pager = (ViewPager) findViewById(R.id.pager);
        setLineContainerWidth(lineContainerWidth);
    }

    private void initAttr(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerScrollView);
        barHeghit = (int) ta.getDimension(R.styleable.ViewPagerScrollView_barHeight,
                Util.dp2px(context, DEFAULT_BAR_HEIHT_DP));
        ViewGroup.LayoutParams params = mHorizontalScrollView.getLayoutParams();
        params.height = barHeghit;
        mHorizontalScrollView.setLayoutParams(params);

        int barBackgroundColor = ta.getColor(R.styleable.ViewPagerScrollView_barBackground,
                Color.parseColor("#fff3f3f3"));
        mHorizontalScrollView.setBackgroundColor(barBackgroundColor);

        SELECTED_COLOR = ta.getColor(R.styleable.ViewPagerScrollView_barTextSelectedColor, SELECTED_COLOR);
        UNSELECTED_COLOR = ta.getColor(R.styleable.ViewPagerScrollView_barTextUnselectedColor, UNSELECTED_COLOR);

        LINE_COLOR = ta.getColor(R.styleable.ViewPagerScrollView_barLineColor, LINE_COLOR);
        LINE_WIDTH = (int) ta.getDimension(R.styleable.ViewPagerScrollView_barLineWidth, LINE_WIDTH);

        if (LINE_WIDTH != 0) {
            ViewGroup.LayoutParams lineP = lineView.getLayoutParams();
            lineP.width = LINE_WIDTH;
            lineView.setLayoutParams(lineP);
        }
        lineView.setBackgroundColor(LINE_COLOR);
    }

    private void setLineContainerWidth(int with) {
        ViewGroup.LayoutParams p = mImageViewContainer.getLayoutParams();
        lineContainerWidth = with;
        p.width = lineContainerWidth;

        mImageViewContainer.setLayoutParams(p);
    }

    public void setData(ArrayList<ScrollItem> itemList, FragmentManager fragmentManager) {
        if (itemList == null) {
            return;
        }
        fragments.clear();
        itemWidthList.clear();
        for (int i = 0; i < itemList.size(); i++) {
            ScrollItem scrollItem = itemList.get(i);
            RelativeLayout layout = new RelativeLayout(context);
            TextView titletxt = new TextView(context);
            titletxt.setId(R.id.scroll_item_text_id);
            titletxt.setText(scrollItem.getItemTitle());
            titletxt.setTextSize(BAR_TEXT_SIZE_SP);
            titletxt.setTextColor(UNSELECTED_COLOR);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(titletxt, params);

//            int textLength = Util.dp2px(context, scrollItem.getItemTitle().length() * BAR_TEXT_SIZE_SP);
//            int itemWidth = 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP) + textLength;
//            if (i == 0) {
//                setLineContainerWidth(itemWidth);
//            }

            int size = titletxt.getLayoutParams().width;
            TextPaint FontPaint = titletxt.getPaint();
//            FontPaint.setTextSize(BAR_TEXT_SIZE_SP);
            int nn = (int) FontPaint.measureText(scrollItem.getItemTitle());
            int textLength = Util.dp2px(context, scrollItem.getItemTitle().length() * BAR_TEXT_SIZE_SP);
            int itemWidth = 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP) + nn;


            mLinearLayout.addView(layout, itemWidth, barHeghit);
            itemWidthList.add(itemWidth);
            layout.setOnClickListener(this);
            layout.setTag(i);
            fragments.add(scrollItem.getFragment());
        }

        ScrollFragmentPagerAdapter fragmentPagerAdapter = new ScrollFragmentPagerAdapter(fragmentManager, fragments);
        pager.setAdapter(fragmentPagerAdapter);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());

        setSelectedPosition(DEFAULT_SELECTED_POSITION);
    }

    public void setSelectedPosition(int position) {
        pager.setCurrentItem(position);
        setSelectedTextColor(position);
    }

    @Override
    public void onClick(View v) {
        int pos = (Integer) v.getTag();
        setSelectedPosition(pos);
    }

    private class ScrollFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> fragments;
        private FragmentManager fm;

        public ScrollFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public ScrollFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fm = fm;
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Object obj = super.instantiateItem(container, position);
            return obj;
        }

    }

    private void setSelectedTextColor(int selectedPosition) {
        TextView oldSelectedText = (TextView) mLinearLayout.findViewWithTag(oldSelectedPosition).
                findViewById(R.id.scroll_item_text_id);
        TextView selectedText = (TextView) mLinearLayout.findViewWithTag(selectedPosition).
                findViewById(R.id.scroll_item_text_id);
        oldSelectedText.setTextColor(UNSELECTED_COLOR);
        selectedText.setTextColor(SELECTED_COLOR);
        oldSelectedPosition = selectedPosition;
    }

    public int getItemLeftXPosition(int pos) {
        int xPos = 0;
        for (int i = 0; i < pos && i < itemWidthList.size(); i++) {
            xPos += itemWidthList.get(i);
        }
        return xPos;
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(final int position) {
            int dx = (itemWidthList.get(position) - lineContainerWidth) / 2;
            int toPostion = getItemLeftXPosition(position) + dx;//position * item_width;
            Animation animation = new TranslateAnimation(endPosition, toPostion, 0, 0);

            beginPosition = toPostion; //position * item_width;

            currentFragmentIndex = position;
            setSelectedTextColor(position);
            if (animation != null) {
                animation.setFillAfter(true);
                animation.setDuration(0);
                mImageViewContainer.startAnimation(animation);
                mHorizontalScrollView.smoothScrollTo(getItemLeftXPosition(currentFragmentIndex - 1), 0);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            if (!isEnd) {
                int startPox = startScrollPosition != 0 ? startScrollPosition :
                        getItemLeftXPosition(currentFragmentIndex);
                if (currentFragmentIndex == position) {
                    endPosition = startPox +
                            (int) (itemWidthList.get(currentFragmentIndex) * positionOffset);
                }
                if (currentFragmentIndex == position + 1) {
                    endPosition = startPox -
                            (int) (itemWidthList.get(currentFragmentIndex) * (1 - positionOffset));
                }

                Animation mAnimation = new TranslateAnimation(beginPosition, endPosition, 0, 0);
                mAnimation.setFillAfter(true);
                mAnimation.setDuration(0);
                mImageViewContainer.startAnimation(mAnimation);
                mHorizontalScrollView.invalidate();
                beginPosition = endPosition;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                isEnd = false;
                startScrollPosition = beginPosition;
            } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                isEnd = true;
                int dx = (itemWidthList.get(currentFragmentIndex) - lineContainerWidth) / 2;
                beginPosition = getItemLeftXPosition(currentFragmentIndex) + dx;
                if (pager.getCurrentItem() == currentFragmentIndex) {
                    mImageViewContainer.clearAnimation();
                    Animation animation = null;
                    animation = new TranslateAnimation(endPosition, beginPosition, 0, 0);
                    animation.setFillAfter(true);
                    animation.setDuration(1);
                    mImageViewContainer.startAnimation(animation);
                    mHorizontalScrollView.invalidate();
                    endPosition = beginPosition;
                }
            }
        }

    }
}
