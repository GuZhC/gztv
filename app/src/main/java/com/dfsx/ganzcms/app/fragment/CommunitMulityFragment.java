package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.DefaultLoadFailedListen;
import com.dfsx.ganzcms.app.business.TopicalApi;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.model.ScrollItem.ScrollItemEx;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang on 2017/6/20
 */
public class CommunitMulityFragment extends DefaultAbsFragment implements View.OnClickListener {
    private static final int BAR_TEXT_SIZE_SP = 16;
    private static final int ITEM_MIN_SPACE_DP = 8;
    private View rootView;
    private HorizontalScrollView mHorizontalScrollView;
    private ViewPager pager;
    private LinearLayout mLinearLayout;
    private View mImageViewContainer;
    private int mScreenWidth;
    private int item_width;
    protected ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    protected ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private int endPosition;
    private int beginPosition;
    private int currentFragmentIndex;
    private int startScrollPosition;
    private boolean isEnd;
    private int oldSelectedPosition;
    protected ArrayList<ScrollItem> itemList;
    ImageButton mAddVideoBtn;
    SearchWnd dialogWnd = null;
    ImageView mSearchBtn;
    TopicalApi mTopicalApi;
    private TextView upFileBtn;
    private DisclurePublishFragment disPublishFrag;
    protected ArrayList<ColumnEntry> cmsColumnlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        act = getActivity();
//        context = getContext();
//        rootView = inflater.inflate(R.layout.frag_news_head_layout, null);
        mTopicalApi = new TopicalApi(context);
//        initView();
//        initAction();
//        initData();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView() {
        rootView.setBackgroundColor(context.
                getResources().getColor(R.color.white));
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);

        mScreenWidth = dm.widthPixels;
        int textLength = Util.dp2px(context, 2 * BAR_TEXT_SIZE_SP);//按每项为4字计算
//        item_width /= (int) ((mScreenWidth-mScreenWidth*2/5) / 4.0 + 0.5f);
        if (item_width < textLength) {
            item_width = textLength + 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP);
        }

        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.hsv_view);
        mLinearLayout = (LinearLayout) findViewById(R.id.hsv_content);
        mImageViewContainer = findViewById(R.id.scroll_bottom_view);
        mImageViewContainer.getLayoutParams().width = item_width;
        pager = (ViewPager) findViewById(R.id.pager);
//        mAddVideoBtn = (ImageButton) findViewById(R.id.header_add_btn);
//        mAddVideoBtn.setOnClickListener(this);
        mSearchBtn = (ImageView) findViewById(R.id.search_btn);
        mSearchBtn.setOnClickListener(this);
        upFileBtn = (TextView) findViewById(R.id.file_up_btn);
        upFileBtn.setOnClickListener(this);

        setGestureOnclick(new DefaultLoadFailedListen() {
            @Override
            public void onButtonFreshClick() {
                initData();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!upFileBtn.isEnabled() && App.getInstance().isDisclureCompalte()) {
            if (App.getInstance().isDisclureIsOk()) {
                clearPubtnStatus();
            }
        }
    }

    public void clearPubtnStatus() {
        upFileBtn.setBackgroundColor(0);
        upFileBtn.setText("上传");
        upFileBtn.setEnabled(true);
        if (App.getInstance().isDisclureCompalte()) {
            if (App.getInstance().isDisclureIsOk()) {
                //  清除 素材列表
                if (disPublishFrag != null)
                    disPublishFrag.clear();
            }
        }
    }

    private View findViewById(int id) {
        return rootView.findViewById(id);
    }

    @Override
    protected void setGesBodyViewLayout(LinearLayout container) {
        rootView = LayoutInflater.from(context).
                inflate(R.layout.frag_column_mulity_head_layout, null);
        //   super.setGesBodyViewLayout(container);
        container.addView(rootView);
    }

    @Override
    public void initData() {
        emptyView.loading();
        ArrayList<ColumnEntry> list = new ArrayList<>();
        ColumnEntry item = new ColumnEntry();
        list.add(item);
        initAction(list);
        hideEmptryView();
//        String requrl = App.getInstance().getmSession().getCommunityServerUrl() + "/public/columns";
//        new DataFileCacheManager<ArrayList<ColumnEntry>>(App.getInstance().getApplicationContext(),
//                "column_34", App.getInstance().getPackageName() + "columnallFragment.txt") {
//            @Override
//            public ArrayList<ColumnEntry> jsonToBean(JSONObject obj) {
//                ArrayList<ColumnEntry> dlist = null;
//                if (obj != null) {
//                    JSONArray arr = obj.optJSONArray("result");
//                    if (arr != null) {
//                        dlist = new ArrayList<ColumnEntry>();
//                        for (int i = 0; i < arr.length(); i++) {
//                            try {
//                                JSONObject object = (JSONObject) arr.get(i);
//                                ColumnEntry entry = new Gson().fromJson(object.toString(), ColumnEntry.class);
//                                dlist.add(entry);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//                return dlist;
//            }
//        }.getData(new DataRequest.HttpParamsBuilder()
//                .setRequestType(DataReuqestType.GET)
//                .setUrl(requrl).setToken(App.getInstance().getCurrentToken())
//                .build(), false).setCallback(new DataRequest.DataCallback<ArrayList<ColumnEntry>>() {
//            @Override
//            public void onSuccess(boolean isAppend, ArrayList<ColumnEntry> data) {
//                hideEmptryView();
//                if (data != null && data.size() > 0) {
////                    hideEmptryView();
//                    initAction(data);
//                } else {
////                    showError();
//                }
//            }
//
//            @Override
//            public void onFail(ApiException e) {
//                e.toString();
//                hideEmptryView();
////                showError();
//            }
//        });
    }

    private void initAction(List<ColumnEntry> list) {
        if (list == null || list.isEmpty()) return;
        if (itemList == null) {
            itemList = new ArrayList<>();
        } else {
            itemList.clear();
            mLinearLayout.removeAllViews();
        }
        ScrollItem item = new ScrollItem("发现", new CommunityCoverFragment());
//        ScrollItem item = new ScrollItem("问答", QustionAnsFragment2.newInstance(29));
        itemList.add(item);
//        disPublishFrag = new DisclurePublishFragment();
//        item = new ScrollItem("爆料", disPublishFrag);
//        ScrollItem item = new ScrollItem("问答", QustionAnsFragment2.newInstance(29));
//        itemList.add(item);
//        int length =  list.size();
//        for (int i = 0; i < length; i++) {
//            ColumnEntry entry = list.get(i);
//             item = new ScrollItemEx(entry.getName(), CommunityRecycleUpFragment.newInstance(entry), (int) entry.getId());
//            itemList.add(item);
//        }
        fragments.clear();
        itemWidthList.clear();
        for (int i = 0; i < itemList.size(); i++) {
            ScrollItem scrollItem = itemList.get(i);
            RelativeLayout layout = new RelativeLayout(context);
            TextView titletxt = new TextView(context);
            titletxt.setId(R.id.scroll_item_text_id);
            titletxt.setText(scrollItem.getItemTitle());
//            titletxt.setBackgroundColor(R.drawable.shape_head_button_bakground);
            titletxt.setTextSize(BAR_TEXT_SIZE_SP);
            titletxt.setTextColor(getResources().getColor(R.color.tv_column_nomal));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Util.dp2px(getActivity(), RelativeLayout.LayoutParams.WRAP_CONTENT),
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            titletxt.setGravity(Gravity.CENTER);
//            layout.setBackground(getResources().getDrawable(R.drawable.shape_head_button_bakground));
            layout.addView(titletxt, params);
            int textLength = Util.dp2px(context, scrollItem.getItemTitle().length() * BAR_TEXT_SIZE_SP);
            int itemWidth = 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP) + textLength;
//            int itemWidth = Util.dp2px(context, 63);
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
//        ((RelativeLayout) oldSelectedText.getParent()).setBackgroundDrawable(null);
        selectedText.setTextColor(getResources().getColor(R.color.tv_column_nomal));
//        ((RelativeLayout) selectedText.getParent()).setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_head_button_bakground));
        oldSelectedPosition = selectedPosition;
    }

    @Override
    public void onClick(View v) {
        if (v == mAddVideoBtn) {
            DefaultFragmentActivity.start(getActivity(), FileFragment.class.getName());
        } else if (v == mSearchBtn) {
            if (dialogWnd == null)
                dialogWnd = new SearchWnd(getActivity());
            dialogWnd.showDialog();
        } else if (v == upFileBtn) {
            if (!(fragments == null || fragments.isEmpty())) {
                if (fragments.size() > 1) {
                    DisclurePublishFragment db = (DisclurePublishFragment) fragments.get(1);
                    db.onComplateUpbtn((TextView) v);
                }

            }
        } else {
            int pos = (Integer) v.getTag();
            pager.setCurrentItem(pos);
            setSelectedTextColor(pos);
        }
    }

    public int getItemLeftXPosition(int pos) {
        int xPos = 0;
        for (int i = 0; i < pos && i < itemWidthList.size(); i++) {
            xPos += itemWidthList.get(i);
        }
        return xPos;
    }

    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> fragments;
        private FragmentManager fm;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
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

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(final int position) {
            int dx = (itemWidthList.get(position) - item_width) / 2;
            int toPostion = getItemLeftXPosition(position) + dx;//position * item_width;
            Animation animation = new TranslateAnimation(endPosition, toPostion, 0, 0);

            beginPosition = toPostion; //position * item_width;
            if (position == 1) {
                upFileBtn.setVisibility(View.VISIBLE);
            } else {
                upFileBtn.setVisibility(View.GONE);
            }

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
