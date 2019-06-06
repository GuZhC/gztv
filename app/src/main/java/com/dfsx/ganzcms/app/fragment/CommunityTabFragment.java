package com.dfsx.ganzcms.app.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.dfsx.core.common.Util.MessageData;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.MyFragmentPagerAdapter;
import com.dfsx.ganzcms.app.business.DefaultLoadFailedListen;
import com.dfsx.ganzcms.app.business.TopicalApi;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.model.ScrollItem.ScrollItemEx;
import com.dfsx.ganzcms.app.util.MessageIntents;
import com.dfsx.lzcms.liveroom.view.CenterGroupChangeBar;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang on 2017/6/20
 */
public class CommunityTabFragment extends DefaultAbsFragment implements View.OnClickListener {
    protected static final int BAR_TEXT_SIZE_SP = 14;
    protected static final int ITEM_MIN_SPACE_DP = 12;
    //    private Activity act;
//    private Context context;
    protected View rootView;
    protected HorizontalScrollView mHorizontalScrollView;
    protected ViewPager pager;
    protected LinearLayout mLinearLayout;
    protected View mImageViewContainer;
    protected int mScreenWidth;
    protected int item_width;
    protected ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    protected ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private int endPosition;
    private int beginPosition;
    protected int currentFragmentIndex;
    private int startScrollPosition;
    private boolean isEnd;
    protected int oldSelectedPosition;
    protected ArrayList<ScrollItem> itemList;
    ImageButton mAddVideoBtn;
    SearchWnd dialogWnd = null;
    ImageButton mSearchBtn;
    TopicalApi mTopicalApi;
    protected CenterGroupChangeBar changeBar;
    protected static final int DEFAULT_SELECTED_COUNT = 0;


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        act = getActivity();
//        context = getContext();
//        rootView = inflater.inflate(R.layout.frag_news_head_layout, null);
//        mTopicalApi = new TopicalApi(context);
//        initView();
//        initAction();
//        initData();
//        return super.onCreateView(inflater, container, savedInstanceState);
//    }

    @Override
    protected void initView() {
        changeBar = (CenterGroupChangeBar) findViewById(R.id.center_change_bar);
//        changeBar.setBarTextArray(DEFAULT_SELECTED_COUNT, new String[]{"现场", "主播"});

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
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        mSearchBtn.setOnClickListener(this);

        setGestureOnclick(new DefaultLoadFailedListen() {
            @Override
            public void onButtonFreshClick() {
                initData();
            }
        });

        changeBar.setOnBarSelectedChangeListener(new CenterGroupChangeBar.OnBarSelectedChangeListener() {
            @Override
            public void onSelectedChange(int selectedIndex) {
                if (selectedIndex >= 0
                        && pager.getCurrentItem() != selectedIndex) {
                    pager.setCurrentItem(selectedIndex, true);
//                    setSelectedTextColor(selectedIndex);
                }
            }
        });
    }

    private View findViewById(int id) {
        return rootView.findViewById(id);
    }

    @Override
    protected void setGesBodyViewLayout(LinearLayout container) {
//        rootView = LayoutInflater.from(context).
//                inflate(R.layout.frag_news_head_layout, null);
        rootView = LayoutInflater.from(context).
                inflate(R.layout.frag_scroll_center_layout, null);
        container.addView(rootView);
    }

    @Override
    public void initData() {
        emptyView.loading();
        String requrl = App.getInstance().getmSession().getCommunityServerUrl() + "/public/columns";
        new DataFileCacheManager<ArrayList<ColumnEntry>>(App.getInstance().getApplicationContext(),
                "column_314", App.getInstance().getPackageName() + "columnallFragment.txt") {
            @Override
            public ArrayList<ColumnEntry> jsonToBean(JSONObject obj) {
                ArrayList<ColumnEntry> dlist = null;
                if (obj != null) {
                    JSONArray arr = obj.optJSONArray("result");
                    if (arr != null) {
                        dlist = new ArrayList<ColumnEntry>();
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                JSONObject object = (JSONObject) arr.get(i);
                                ColumnEntry entry = new Gson().fromJson(object.toString(), ColumnEntry.class);
                                dlist.add(entry);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return dlist;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setRequestType(DataReuqestType.GET)
                .setUrl(requrl).setToken(App.getInstance().getCurrentToken())
                .build(), false).setCallback(new DataRequest.DataCallback<ArrayList<ColumnEntry>>() {
            @Override
            public void onSuccess(boolean isAppend, ArrayList<ColumnEntry> data) {
                hideEmptryView();
                if (data != null && data.size() > 0) {
//                    hideEmptryView();
                    initAction(data);
                } else {
//                    showError();
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.toString();
                hideEmptryView();
//                showError();
            }
        });
    }

    private void initAction(List<ColumnEntry> list) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        } else {
            itemList.clear();
            mLinearLayout.removeAllViews();
        }
//        ScrollItem item = new ScrollItem("问答", QustionAnsFragment.newInstance(29));
//        ScrollItem item = new ScrollItem("问答", QustionAnsFragment2.newInstance(29));
//        itemList.add(item);
        ScrollItem item = null;
//        for (int i = 0; i < list.size(); i++) {
//        int length = list.size() > 3 ? 3 : list.size();
        int length = list.size();
        for (int i = 0; i < length; i++) {
            ColumnEntry entry = list.get(i);
            item = new ScrollItemEx(entry.getName(), CommunityRecycleUpFragment.newInstance(entry), (int) entry.getId());
//            item = new ScrollItemEx(entry.getName(), CommunityLocalRecycleUpFragment.newInstance(entry), (int) entry.getId());
//            item = new ScrollItemEx(entry.getName(), CommunityLocalFragment.newInstance(entry), (int) entry.getId());
            itemList.add(item);
        }
        fragments.clear();
        itemWidthList.clear();
//        int length = itemWidthList.size();
        String[] arr = new String[length];
        for (int i = 0; i < length; i++) {
            ScrollItem scrollItem = itemList.get(i);
            RelativeLayout layout = new RelativeLayout(context);
            TextView titletxt = new TextView(context);
            titletxt.setId(R.id.scroll_item_text_id);
            titletxt.setText(scrollItem.getItemTitle());
//            titletxt.setBackgroundColor(R.drawable.shape_head_button_bakground);
            titletxt.setTextSize(BAR_TEXT_SIZE_SP);
            titletxt.setTextColor(getResources().getColor(R.color.COLOR_WHITE_50));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Util.dp2px(getActivity(), RelativeLayout.LayoutParams.WRAP_CONTENT),
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
//            titletxt.setGravity(Gravity.CENTER);
//            layout.setBackground(getResources().getDrawable(R.drawable.shape_head_button_bakground));
            layout.addView(titletxt, params);
            int textLength = Util.dp2px(context, scrollItem.getItemTitle().length() * BAR_TEXT_SIZE_SP);
            int itemWidth = 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP) + textLength;
//            int itemWidth = Util.dp2px(context, 63);
            mLinearLayout.addView(layout, itemWidth, Util.dp2px(context, 32));
            itemWidthList.add(itemWidth);

            arr[i] = scrollItem.getItemTitle();

//            layout.setOnClickListener(this);
//            layout.setTag(i);
            fragments.add(scrollItem.getFragment());
        }

        changeBar.setBarTextArray(DEFAULT_SELECTED_COUNT, arr);

        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments);
        pager.setAdapter(fragmentPagerAdapter);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());
//        pager.setCurrentItem(0);
        pager.setCurrentItem(DEFAULT_SELECTED_COUNT, true);

//        setSelectedTextColor(0);
    }

    public void setSelectedTextColor(int selectedPosition) {
        TextView oldSelectedText = (TextView) mLinearLayout.findViewWithTag(oldSelectedPosition).
                findViewById(R.id.scroll_item_text_id);
        TextView selectedText = (TextView) mLinearLayout.findViewWithTag(selectedPosition).
                findViewById(R.id.scroll_item_text_id);
        oldSelectedText.setTextColor(getResources().getColor(R.color.COLOR_WHITE_50));
//        ((RelativeLayout) oldSelectedText.getParent()).setBackgroundDrawable(null);
        selectedText.setTextColor(getResources().getColor(R.color.white));
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
        } else {
//            int pos = (Integer) v.getTag();
//            pager.setCurrentItem(pos);
//            setSelectedTextColor(pos);
        }
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
            int dx = (itemWidthList.get(position) - item_width) / 2;
            int toPostion = getItemLeftXPosition(position) + dx;//position * item_width;
            Animation animation = new TranslateAnimation(endPosition, toPostion, 0, 0);

            beginPosition = toPostion; //position * item_width;

            changeBar.setCheckIndex(position);

            //关掉电视
            if (position != 0) {
                RxBus.getInstance().post(new MessageData<LiveEntity.LiveChannel>(MessageIntents.RX_ITME_TV_CLOSED, null));
            }
            //关掉广播
            if (position != 1) {
                RxBus.getInstance().post(new MessageData<LiveEntity.LiveChannel>(MessageIntents.RX_ITME_RADIO_CLOSED, null));
            }


            currentFragmentIndex = position;
//            setSelectedTextColor(position);
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
