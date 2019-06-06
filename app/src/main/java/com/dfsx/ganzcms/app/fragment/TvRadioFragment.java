package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dfsx.core.common.Util.MessageData;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.MyFragmentPagerAdapter;
import com.dfsx.ganzcms.app.model.LiveEntity;
import com.dfsx.ganzcms.app.model.ScrollItem;
import com.dfsx.ganzcms.app.util.MessageIntents;
import rx.Subscription;

import java.util.ArrayList;

/**
 * Created by heyang  2018/1/5  达州的
 */
public class TvRadioFragment extends Fragment implements View.OnClickListener {
    private static final int BAR_TEXT_SIZE_SP = 16;
    private static final int ITEM_MIN_SPACE_DP = 8;

    private Activity act;
    private Context context;

    private View rootView;
    private HorizontalScrollView mHorizontalScrollView;
    private ViewPager pager;
    private LinearLayout mLinearLayout;
    private View mImageViewContainer;
    private int mScreenWidth;
    private int item_width;
    private ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private int endPosition;
    private int beginPosition;
    private int currentFragmentIndex;
    private int startScrollPosition;
    private boolean isEnd;
    private int oldSelectedPosition;
    private ArrayList<ScrollItem> itemList;
    private Subscription radioSubscription;
    private boolean isSwicthRad = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = getActivity();
        context = getContext();
        rootView = inflater.inflate(R.layout.frag_live_layout, null);
        initView();
        initAction();
//        initRegister();
        return rootView;
    }

    private void initView() {
        rootView.setBackgroundColor(context.
                getResources().getColor(R.color.white));
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);

        mScreenWidth = dm.widthPixels;
        int textLength = Util.dp2px(context, 2 * BAR_TEXT_SIZE_SP);//按每项为4字计算
        if (item_width < textLength) {
            item_width = textLength + 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP);
        }

        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.hsv_view);
        mLinearLayout = (LinearLayout) findViewById(R.id.hsv_content);
        mImageViewContainer = findViewById(R.id.scroll_bottom_view);
        mImageViewContainer.getLayoutParams().width = item_width;
        pager = (ViewPager) findViewById(R.id.pager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                break;
            case 2:
                if (data == null) return;
                LiveEntity.LiveChannel channel = (LiveEntity.LiveChannel) data.getSerializableExtra("key");
//                resetInit(channel);
                int a = 0;
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public int getCurrentPos() {
        return currentFragmentIndex;
    }

//    public void initRegister() {
//        radioSubscription = RxBus.getInstance().toObserverable(MsgData.class).
//                subscribe(new Observer<MsgData>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(MsgData msg) {
//                        if (msg.getType() == ColumnTYPE.SWITCH_RADIO_MSG) {
//                            isSwicthRad = true;
//                            pager.setCurrentItem(1);
//                            setSelectedTextColor(1);
//                        }
//                    }
//                });
//    }


    public void switchRadioFrg() {
        isSwicthRad = true;
        if (pager != null) {
            pager.setCurrentItem(1);
            setSelectedTextColor(1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSwicthRad) {
            isSwicthRad = false;
            switchRadioFrg();
        }
    }


    private View findViewById(int id) {
        return rootView.findViewById(id);
    }

    private void initAction() {
        itemList = new ArrayList<>();
//        ScrollItem item = new ScrollItem("直播", DzLiveTvFragment.newInstance(-1));
        ScrollItem item = new ScrollItem("电视", LiveTvFragment.newInstance(-1));
        itemList.add(item);
//        item = new ScrollItem("点播", BnBunchVodFragment.newInstance(-1));
//        itemList.add(item);
//        item = new ScrollItem("电台", DzBrodcastFragment.newInstance(1));
//        item = new ScrollItem("电台", BnBrodcastFragment.newInstance(1));
//        itemList.add(item);
        fragments.clear();
        itemWidthList.clear();
        for (int i = 0; i < itemList.size(); i++) {
            ScrollItem scrollItem = itemList.get(i);
            RelativeLayout layout = new RelativeLayout(context);
            TextView titletxt = new TextView(context);
            titletxt.setId(R.id.scroll_item_text_id);
            titletxt.setText(scrollItem.getItemTitle());
            titletxt.setTextSize(BAR_TEXT_SIZE_SP);
            titletxt.setTextColor(getResources().getColor(R.color.tv_column_nomal));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(titletxt, params);
//            if (i == 0) {
//                layout.setGravity(Gravity.RIGHT);
//                layout.setPadding(0, 0, 40, 0);
//            } else if (i == 1) {
//                layout.setGravity(Gravity.CENTER);
//                layout.setPadding(0, 0, 40, 0);
//            } else {
//                layout.setGravity(Gravity.LEFT);
//            }
            int textLength = Util.dp2px(context, scrollItem.getItemTitle().length() * BAR_TEXT_SIZE_SP);
            int itemWidth = 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP) + textLength;
//            int itemWidth = mScreenWidth / 4;
            mLinearLayout.addView(layout, itemWidth, Util.dp2px(context, 32));
            itemWidthList.add(itemWidth);
            layout.setOnClickListener(this);
            layout.setTag(i);
            fragments.add(scrollItem.getFragment());
        }
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments);
        pager.setAdapter(fragmentPagerAdapter);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());
        pager.setCurrentItem(0);
        setSelectedTextColor(0);
    }

    private void setSelectedTextColor(int selectedPosition) {
        TextView oldSelectedText = (TextView) mLinearLayout.findViewWithTag(oldSelectedPosition).
                findViewById(R.id.scroll_item_text_id);
        TextView selectedText = (TextView) mLinearLayout.findViewWithTag(selectedPosition).
                findViewById(R.id.scroll_item_text_id);
        oldSelectedText.setTextColor(getResources().getColor(R.color.tv_column_nomal));
        selectedText.setTextColor(getResources().getColor(R.color.tv_column_nomal));
        oldSelectedPosition = selectedPosition;
    }

    @Override
    public void onClick(View v) {
        int pos = (Integer) v.getTag();
        pager.setCurrentItem(pos);
        setSelectedTextColor(pos);
    }

    public int getItemLeftXPosition(int pos) {
        int xPos = 0;
        for (int i = 0; i < pos && i < itemWidthList.size(); i++) {
            xPos += itemWidthList.get(i);
        }
        return xPos;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (radioSubscription != null) {
            radioSubscription.unsubscribe();
        }
    }

//    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
//        private ArrayList<Fragment> fragments;
//        private FragmentManager fm;
//
//        public MyFragmentPagerAdapter(FragmentManager fm) {
//            super(fm);
//            this.fm = fm;
//        }
//
//        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
//            super(fm);
//            this.fm = fm;
//            this.fragments = fragments;
//        }
//
//        @Override
//        public int getCount() {
//            return fragments.size();
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return fragments.get(position);
//        }
//
//        @Override
//        public int getItemPosition(Object object) {
//            return POSITION_NONE;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, final int position) {
//            Object obj = super.instantiateItem(container, position);
//            return obj;
//        }
//    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(final int position) {
            int dx = (itemWidthList.get(position) - item_width) / 2;
            int toPostion = getItemLeftXPosition(position) + dx;//position * item_width;
            Animation animation = new TranslateAnimation(endPosition, toPostion, 0, 0);

            beginPosition = toPostion; //position * item_width;

            //关掉电视
            if (position != 0) {
                RxBus.getInstance().post(new MessageData<LiveEntity.LiveChannel>(MessageIntents.RX_ITME_TV_CLOSED, null));
            }
            //关掉广播
            if (position != 1) {
                RxBus.getInstance().post(new MessageData<LiveEntity.LiveChannel>(MessageIntents.RX_ITME_RADIO_CLOSED, null));
            }

            currentFragmentIndex = position;
            RxBus.getInstance().post(currentFragmentIndex);
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
                int dx = (itemWidthList.get(currentFragmentIndex) - item_width) / 2;
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
