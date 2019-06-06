package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.model.TalentRankInfo;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.MyFragmentPagerAdapter;
import com.dfsx.ganzcms.app.business.TopicalApi;
import com.dfsx.ganzcms.app.model.ScrollItem;
import com.dfsx.logonproject.busniness.AccountApi;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.view.CenterGroupChangeBar;
import android.support.v4.app.Fragment;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;

import java.util.ArrayList;

/*
 *  Created  by heyang  2017-3-12
 */

public class TalentPersonFragment extends Fragment implements View.OnClickListener , PullToRefreshRecyclerView.PullRecyclerHelper{
    protected static final int BAR_TEXT_SIZE_SP = 14;
    protected static final int ITEM_MIN_SPACE_DP = 5;
    TopicalApi mTopicalApi = null;
    private CenterGroupChangeBar groupChangeBar;
    private ViewPager viewPager;
    private CoordinatorLayout zoomHeaderCoordinatorLayout;
    private AppBarLayout barLayout;
    private int currentPage, oldSelectedPosition, headerOffSetSize;
    View rootView;
    protected HorizontalScrollView mHorizontalScrollView;
    protected LinearLayout mLinearLayout;
    protected View mImageViewContainer;
    protected int mScreenWidth;
    protected int item_width;
    private int endPosition, beginPosition, currentFragmentIndex, startScrollPosition;
    private boolean isEnd;
    protected ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    protected ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    protected ArrayList<ScrollItem> itemList;
    private CircleButton _userImage;
    private TextView _userNickName;
    private AccountApi _accountApi;
    private TextView _talPrioseTxt, _talcommendTxt, _talAttionTxt, _talGiftTxt;
    private TextView _talPrioseRankTxt, _talcommendRankTxt, _talAttionRankTxt, _talGiftRanTxt;
    private View _barTopFolatView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        super.onCreateView(inflater,container,savedInstanceState);
        rootView = inflater.inflate(R.layout.act_talent_person, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //   super.onViewCreated(view, savedInstanceState);
        initView();
//        initPager();
        initAction();
        _accountApi = new AccountApi(getActivity());
        getRankInfo();
    }

    protected void initView() {
//        mScreenWidth = dm.widthPixels;
        int textLength = Util.dp2px(getActivity(), 4 * BAR_TEXT_SIZE_SP);//按每项为4字计算
        if (item_width < textLength) {
            item_width = textLength + 4 * Util.dp2px(getActivity(), ITEM_MIN_SPACE_DP);
        }
        mHorizontalScrollView = (HorizontalScrollView) rootView.findViewById(R.id.hsv_view);
        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.hsv_content);
        mImageViewContainer = rootView.findViewById(R.id.scroll_bottom_view);
        mImageViewContainer.getLayoutParams().width = item_width;
        zoomHeaderCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.main_content);
        barLayout = (AppBarLayout) rootView.findViewById(R.id.appbar);
        _barTopFolatView = (View) rootView.findViewById(R.id.bar_top_float_vew);
        groupChangeBar = (CenterGroupChangeBar) rootView.findViewById(R.id.center_indicator);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        _userImage = (CircleButton) rootView.findViewById(R.id.talent_user_image);
        _userNickName = (TextView) rootView.findViewById(R.id.talent_user_txt);
        _talPrioseTxt = (TextView) rootView.findViewById(R.id.telephon_praise_btn);
        _talcommendTxt = (TextView) rootView.findViewById(R.id.talent_write_btn);
        _talAttionTxt = (TextView) rootView.findViewById(R.id.talent_attion_btn);
        _talGiftTxt = (TextView) rootView.findViewById(R.id.talent_gift_btn);
        _talPrioseRankTxt = (TextView) rootView.findViewById(R.id.talent_praise_rank);
        _talcommendRankTxt = (TextView) rootView.findViewById(R.id.talent_write_rank);
        _talAttionRankTxt = (TextView) rootView.findViewById(R.id.talent_attion_rank);
        _talGiftRanTxt = (TextView) rootView.findViewById(R.id.talent_gift_rank);

        groupChangeBar.setBarTextArray(0, "收赞达人", "评论达人", "明星达人", "送礼达人");
//        zoomHeaderCoordinatorLayout.setPullZoom(barLayout,
//                PixelUtil.dp2px(getActivity(), 390),
//                PixelUtil.dp2px(getActivity(), 500), this);

        groupChangeBar.setOnBarSelectedChangeListener(new CenterGroupChangeBar.OnBarSelectedChangeListener() {
            @Override
            public void onSelectedChange(int selectedIndex) {
                if (currentPage != selectedIndex) {
                    currentPage = selectedIndex;
                    viewPager.setCurrentItem(selectedIndex, true);
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
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
            public void onPageSelected(int position) {
                if (currentPage != position) {
                    currentPage = position;
                    groupChangeBar.setCheckIndex(position);
                }
                int dx = (itemWidthList.get(position) - item_width) / 4;
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
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    isEnd = false;
                    startScrollPosition = beginPosition;
                } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    isEnd = true;
                    int dx = (itemWidthList.get(currentFragmentIndex) - item_width) / 4;
                    beginPosition = getItemLeftXPosition(currentFragmentIndex) + dx;
                    if (viewPager.getCurrentItem() == currentFragmentIndex) {
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
        });

        barLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                headerOffSetSize = verticalOffset;
            }
        });
    }

    private void getRankInfo() {
        Account user = App.getInstance().getUser();
        if (user != null) {
            LSLiveUtils.showUserLogoImage(getActivity(), _userImage, user.getUser().getAvatar_url());
            _userNickName.setText(user.getUser().getNickname());
        }
        _accountApi.getMyRankInfo(new DataRequest.DataCallback<TalentRankInfo>() {
            @Override
            public void onSuccess(boolean isAppend, TalentRankInfo data) {
                if (data != null) {
                    _talPrioseTxt.setText(data.getReceive_agree_count() + "");
                    _talcommendTxt.setText(data.getComment_count() + "");
                    _talAttionTxt.setText(data.getFans_count() + "");
                    _talGiftTxt.setText(data.getSend_gift_count() + "");
                    setRankText(_talPrioseRankTxt, data.getReceive_agree_rank());
                    setRankText(_talcommendRankTxt, data.getComment_rank());
                    setRankText(_talAttionRankTxt, data.getFans_rank());
                    setRankText(_talGiftRanTxt, data.getSend_gift_rank());
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
    }

    public void setRankText(TextView text, long rank) {
        if (rank == 0) {
            text.setText("未进入榜单");
        } else {
            View  parant= (View) text.getParent();
            parant.setBackgroundResource(R.drawable.shape_talent_select_item_bankground);
            text.setText(rank + "");
        }
    }

    public int getItemLeftXPosition(int pos) {
        int xPos = 0;
        for (int i = 0; i < pos && i < itemWidthList.size(); i++) {
            xPos += itemWidthList.get(i);
        }
        return xPos;
    }

    @Override
    public boolean isReadyForPullEnd() {
        boolean is = Math.abs(headerOffSetSize) >= barLayout.getHeight() - _barTopFolatView.getHeight();
        return is;
    }

    @Override
    public boolean isReadyForPullStart() {
        return headerOffSetSize == 0;
    }

    private void initAction() {
        if (itemList == null) {
            itemList = new ArrayList<>();
        } else {
            itemList.clear();
            mLinearLayout.removeAllViews();
        }
        ScrollItem item1 = new ScrollItem("收赞达人", TalentAbsListFragment.newInstance(1));
        ScrollItem item2 = new ScrollItem("评论达人", TalentAbsListFragment.newInstance(2));
        ScrollItem item3 = new ScrollItem("明星达人", TalentAbsListFragment.newInstance(3));
        ScrollItem item4 = new ScrollItem("送礼达人", TalentAbsListFragment.newInstance(4));
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        itemList.add(item4);
        fragments.clear();
        itemWidthList.clear();
        for (int i = 0; i < itemList.size(); i++) {
            ScrollItem scrollItem = itemList.get(i);
            RelativeLayout layout = new RelativeLayout(getActivity());
            TextView titletxt = new TextView(getActivity());
            titletxt.setId(R.id.scroll_item_text_id);
            titletxt.setText(scrollItem.getItemTitle());
//            titletxt.setBackgroundColor(R.drawable.shape_head_button_bakground);
            titletxt.setTextSize(BAR_TEXT_SIZE_SP);
            titletxt.setTextColor(getResources().getColor(R.color.gray_75));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Util.dp2px(getActivity(), RelativeLayout.LayoutParams.WRAP_CONTENT),
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
//            titletxt.setGravity(Gravity.CENTER);
//            layout.setBackground(getResources().getDrawable(R.drawable.shape_head_button_bakground));
            layout.addView(titletxt, params);
            int textLength = Util.dp2px(getActivity(), scrollItem.getItemTitle().length() * BAR_TEXT_SIZE_SP);
            int itemWidth = 4 * Util.dp2px(getActivity(), ITEM_MIN_SPACE_DP) + textLength;
//            int itemWidth = Util.dp2px(context, 63);
            mLinearLayout.addView(layout, itemWidth, Util.dp2px(getActivity(), 32));
            itemWidthList.add(itemWidth);

            layout.setOnClickListener(this);
            layout.setTag(i);
            fragments.add(scrollItem.getFragment());
        }
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(fragmentPagerAdapter);
//        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setCurrentItem(0);
        setSelectedTextColor(0);
    }

    public void setSelectedTextColor(int selectedPosition) {
        TextView oldSelectedText = (TextView) mLinearLayout.findViewWithTag(oldSelectedPosition).
                findViewById(R.id.scroll_item_text_id);
        TextView selectedText = (TextView) mLinearLayout.findViewWithTag(selectedPosition).
                findViewById(R.id.scroll_item_text_id);
        oldSelectedText.setTextColor(getResources().getColor(R.color.gray_75));
//        ((RelativeLayout) oldSelectedText.getParent()).setBackgroundDrawable(null);
        selectedText.setTextColor(getResources().getColor(R.color.public_purple_bkg));
//        ((RelativeLayout) selectedText.getParent()).setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_head_button_bakground));
        oldSelectedPosition = selectedPosition;
    }

    @Override
    public void onClick(View v) {
        int pos = (Integer) v.getTag();
        if (pos != -1) {
            viewPager.setCurrentItem(pos);
            setSelectedTextColor(pos);
        }
    }

//    private void initPager() {
//        ArrayList<Fragment> fragments = new ArrayList<>();
//        fragments.add(TalentCommFragment.newInstance(-1));
//        fragments.add(TalentCommFragment.newInstance(-1));
//        fragments.add(TalentCommFragment.newInstance(-1));
//        fragments.add(TalentCommFragment.newInstance(-1));
//        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments);
//        viewPager.setAdapter(adapter);
//    }

}