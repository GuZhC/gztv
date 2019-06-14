package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.business.LanguageUtil;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.MyFragmentPagerAdapter;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.business.ColumnHelperManager;
import com.dfsx.ganzcms.app.business.UserEditWordPermissionHelper;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.dfsx.ganzcms.app.model.ScrollItem;
import com.dfsx.searchlibaray.SearchUtil;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang on 2016/9/16.
 */
public class ImportNewsFragment extends HeadLineFragment implements View.OnClickListener {
    private static final int BAR_TEXT_SIZE_SP = 14;
    private static final int ITEM_MIN_SPACE_DP = 10;
    private Activity act;
    private Context context;
    private View rootView, mImageViewContainer;
    private HorizontalScrollView mHorizontalScrollView;
    private ViewPager pager;
    private LinearLayout mLinearLayout;
    private int mScreenWidth, item_width;
    private ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private int endPosition, beginPosition, currentFragmentIndex, startScrollPosition;
    private boolean isEnd;
    private int oldSelectedPosition;
    private ArrayList<ScrollItem> itemList;
    //    ImageButton mAddVideoBtn;
    SearchWnd dialogWnd = null;
    ImageView _hsTopcRightBtn;
    private static final String PARENT_COLUMN = "parent_column";
    private UserEditWordPermissionHelper editWordPermissionHelper;
    private Subscription loginOkSubscription;
    private View topRightView;
    private ImageView topLeftView;
    private ColumnHelperManager colHelperManager;

    public static ImportNewsFragment newInstance() {
        ImportNewsFragment fragment = new ImportNewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //        rootView = inflater.inflate(R.layout.frag_news_layout, null);
        rootView = inflater.inflate(R.layout.frag_mulity_column_layout, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        act = getActivity();
        context = getContext();
        editWordPermissionHelper = new UserEditWordPermissionHelper(getContext());
        initView();
        iniData();
        initRegister();
    }

    private void initView() {
        topRightView = findViewById(R.id.top_rigth_btn);
        topLeftView = (ImageView) findViewById(R.id.weather_image_btn);
        rootView.setBackgroundColor(context.
                getResources().getColor(R.color.white));
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);

        colHelperManager = new ColumnHelperManager(getContext());
        colHelperManager.setCallback(callback);
        mScreenWidth = dm.widthPixels;
        int textLength = Util.dp2px(context, 2 * BAR_TEXT_SIZE_SP);//按每项为4字计算
        //        item_width /= (int) ((mScreenWidth-mScreenWidth*2/5) / 4.0 + 0.5f);
        if (item_width < textLength) {
            item_width = textLength + 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP);
        }
        //        _topScrollImg = (ImageView) findViewById(R.id.img1);
        //        _topScrollImg.setBackgroundColor(R.color.public_purple_bkg);
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.hsv_view);
        mLinearLayout = (LinearLayout) findViewById(R.id.hsv_content);
        mImageViewContainer = findViewById(R.id.scroll_bottom_view);
        mImageViewContainer.getLayoutParams().width = item_width;
        pager = (ViewPager) findViewById(R.id.pager);
        //        mAddVideoBtn = (ImageButton) findViewById(R.id.header_add_btn);
        //        mAddVideoBtn.setOnClickListener(this);
        _hsTopcRightBtn = (ImageView) findViewById(R.id.hs_right_btn);
        //        _hsTopcRightBtn.setOnClickListener(this);
        //        _hsTopcRightBtn.setVisibility(View.VISIBLE);
        topRightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchUtil.goSearch(context, AppSearchFragment.class.getName());

//                UpdateNewDialog2  dialog=new UpdateNewDialog2(getActivity());
//                dialog.show();


                //                view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_click));
                //                dialogWnd = new SearchWnd(getActivity());
                //                dialogWnd.setListViewAdapter(new ListViewAdapter(context));
                //                dialogWnd.setOnSearchClickListener(new ContentSearchImpl());
                //                dialogWnd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //                    @Override
                //                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //                        if (parent instanceof ListView) {
                //                            ListView searchListView = (ListView) parent;
                //                            int pos = position - searchListView.getHeaderViewsCount();
                //                            ListViewAdapter searchAdapter = (ListViewAdapter) dialogWnd.getListViewAdapter();
                //                            ContentCmsEntry entry = (ContentCmsEntry) searchAdapter.getItem(pos);
                //                            if (entry != null) {
                //                                newsDatailHelper.goDetail(entry);
                //                            }
                //                        }
                //                    }
                //                });
                //                dialogWnd.showDialog();
            }
        });
        topLeftView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                Bundle bundle = new Bundle();
                //                bundle.putString(BaseAndroidWebFragment.PARAMS_URL, "https://www.huabaike.com/hyjk/4415.html");
                //                  Intent intent = new Intent(getActivity(), SwitchLanguageTempActivity.class);
                //                  getActivity().startActivity(intent);
                LanguageUtil.switchLanguage(getActivity(), "tbt");
                DefaultFragmentActivity.start(getActivity(), MulityColumnFragment.class.getName());
            }
        });
    }

    private View findViewById(int id) {
        return rootView.findViewById(id);
    }

    private DataRequest.DataCallback<ArrayList<ColumnCmsEntry>>
            callback = new DataRequest.DataCallback<ArrayList<ColumnCmsEntry>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<ColumnCmsEntry> data) {
            //     LogUtils.e("TAG", "callback onSuccess()  data= " + data.size());
            if (data != null) {
                ArrayList<ColumnCmsEntry> list = (ArrayList<ColumnCmsEntry>) data;
                //                App.getInstance().setmHeaderCmsList(list);
                ColumnBasicListManager.getInstance().set_columnList(list);
                //         LogUtils.e("TAG", "callback onSuccess()  setmHeaderCmsList()= " + list.size());
                if (ImportNewsFragment.this.getContext() != null &&
                        ImportNewsFragment.this.getActivity() != null &&
                        !ImportNewsFragment.this.getActivity().isFinishing()) {
                    initAction();
                }
            }
        }

        @Override
        public void onFail(ApiException e) {
            e.printStackTrace();
        }
    };

    public void initRegister() {
        loginOkSubscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_LOGIN_OK)) {
                            checkWriteDoc();
                        }
                    }
                });
    }

    public void checkWriteDoc() {
        if (editWordPermissionHelper == null) return;
        editWordPermissionHelper.isEditWordPermission(new UserEditWordPermissionHelper.EditPermissionCallBack() {
            @Override
            public void callBack(boolean isPermission, ArrayList<Long> permissionColumnIds) {
                boolean isShowEditWordBtn = isPermission;
                ArrayList<Long> editWordColumnIdList = permissionColumnIds;
                ColumnBasicListManager.getInstance().setShowEditWordBtn(isShowEditWordBtn);
                ColumnBasicListManager.getInstance().setEditWordColumnIdList(editWordColumnIdList);
            }
        });
    }

    private void iniData() {
        checkWriteDoc();
        colHelperManager.getAllColumns();
    }

    private void initAction() {
        List<ColumnCmsEntry> dlisf = ColumnBasicListManager.getInstance().findColumnListByCodes("home,news");
        if (itemList == null) {
            itemList = new ArrayList<>();
        } else {
            itemList.clear();
            mLinearLayout.removeAllViews();
        }

        //        //测试
        //        ColumnCmsEntry hand = new ColumnCmsEntry();
        //        hand.setKey("handserver");
        //        hand.setName("便民服务");
        //        dlisf.add(hand);

        //        测试
        //        ColumnCmsEntry hand2 = new ColumnCmsEntry();
        //        hand2.setKey("shows");
        //        hand2.setName("秀场");
        //        dlisf.add(hand2);

        //        ColumnCmsEntry hand1 = new ColumnCmsEntry();
        //        hand1.setKey("bnvod");
        //        hand1.setName("点播11");
        //        dlisf.add(hand1);

        if (dlisf == null || dlisf.isEmpty()) return;
        if (dlisf != null && !dlisf.isEmpty()) {

//            ColumnCmsEntry zhuanti = ColumnBasicListManager.getInstance().findColumnByMachine("zhuanti");
//            if (zhuanti != null) {
//                dlisf.add(zhuanti);
//            }

            ColumnBasicListManager.getInstance().sort(dlisf);

            ScrollItem item = null;
            for (int i = 0; i < dlisf.size(); i++) {
                ColumnCmsEntry ce = dlisf.get(i);
                item = createScrollItem(ce);
                if (item != null) {
                    itemList.add(item);
                    item = null;
                }
            }
            itemList.add(new ScrollItem("直播", new LiveWebLinkFragment()));
        }
        //   LogUtils.e("TAG", "initAction()= " + itemList.size());
        fragments.clear();
        itemWidthList.clear();
        for (int i = 0; i < itemList.size(); i++) {
            ScrollItem scrollItem = itemList.get(i);
            RelativeLayout layout = new RelativeLayout(context);
            TextView titletxt = new TextView(context);
            //            titletxt.setPadding(0,0,Util.dp2px(context, ITEM_MIN_SPACE_DP),0);
            titletxt.setId(R.id.scroll_item_text_id);
            titletxt.setText(scrollItem.getItemTitle());
            titletxt.setTextSize(BAR_TEXT_SIZE_SP);
            if (this.isAdded()) {
                titletxt.setTextColor(getActivity().getResources().getColor(R.color.news_mulity_column_noraml));
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(titletxt, params);

            int size = titletxt.getLayoutParams().width;
            TextPaint FontPaint = titletxt.getPaint();
            //            FontPaint.setTextSize(BAR_TEXT_SIZE_SP);
            int nn = (int) FontPaint.measureText(scrollItem.getItemTitle());

            int textLength = Util.dp2px(context, scrollItem.getItemTitle().length() * BAR_TEXT_SIZE_SP);
            int itemWidth = 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP) + nn;
            if (i == 4) {
                //                itemWidth = titletxt.getLayoutParams().width;
                //            int itemWidth = 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP) +titletxt.getLayoutParams().width;
                //            int itemWidth = 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP) +
                //                    Util.dp2px(context, titletxt.getLayoutParams().width);
                //                itemWidth = 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP) + scrollItem.getItemTitle().length() * BAR_TEXT_SIZE_SP;
            }

            mLinearLayout.addView(layout, itemWidth, Util.dp2px(context, 32));
            //            mLinearLayout.addView(layout);
            itemWidthList.add(itemWidth);

            layout.setOnClickListener(this);
            layout.setTag(i);
            fragments.add(scrollItem.getFragment());
        }
        if (!isAdded()) return;
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments);
        pager.setAdapter(fragmentPagerAdapter);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());
        pager.setCurrentItem(0);
        setSelectedTextColor(0);
    }

    protected ScrollItem createScrollItem(ColumnCmsEntry ce) {
        ScrollItem item = null;
        boolean flag = ColumnBasicListManager.getInstance().isExist(ce.getName());
        if (flag) return null;
        if (TextUtils.equals("tv", ce.getKey())) {
            item = new ScrollItem(ce.getName(), LiveTvFragment.newInstance(ce.getId()));
        } else if (TextUtils.equals("radio", ce.getKey())) {
            item = new ScrollItem(ce.getName(), LiveRadioFragment.newInstance());
        } else if (TextUtils.equals("news", ce.getKey())
                || TextUtils.equals("recommend", ce.getKey()) || TextUtils.equals("home", ce.getKey())) {
            long id = ce.getId();
            String columnCode = ce.getMachine_code();
            if (TextUtils.equals("home", ce.getMachine_code())) {
                if (!(ce.getDlist() == null || ce.getDlist().isEmpty())) {
                    for (ColumnCmsEntry entry : ce.getDlist()) {
                        if (TextUtils.equals("list", entry.getMachine_code())) {
                            id = entry.getId();
                            break;
                        }
                    }
                }
                ColumnBasicListManager.getInstance().setHomeNewsId(id);
            }
            item = new ScrollItem(ce.getName(), HeadLineFragment.newInstance(id, columnCode, ce.getSliderId(), ce.getDynamicId()));
        } else if (TextUtils.equals(PARENT_COLUMN, ce.getKey())) {
            if (TextUtils.equals(ce.getType(), "radio")) {
                item = new ScrollItem(ce.getName(), LiveRadioFragment.newInstance());
            } else if (TextUtils.equals(ce.getType(), "tv")) {
                item = new ScrollItem(ce.getName(), LiveTvFragment.newInstance(ce.getId()));
            } else
                item = new ScrollItem(ce.getName(), ColumnPlayFragment.newInstance(ce.getMachine_code()));
        } else if (TextUtils.equals("shows", ce.getKey())) {
            item = new ScrollItem(ce.getName(), ColumnPlayFragment.newInstance(ce.getMachine_code()));
        } else if (TextUtils.equals("bianmin", ce.getKey())) {
            item = new ScrollItem(ce.getName(), HandServiceFragment.newInstance(ce.getId()));
        } else if (TextUtils.equals("bnvod", ce.getKey())) {
            item = new ScrollItem(ce.getName(), BnBunchVodFragment.newInstance(1));
        } else if (TextUtils.equals("juzhen", ce.getKey())) {
            item = new ScrollItem(ce.getName(), NewsTwoListFragment.newInstance(ce));
        } else if (TextUtils.equals("dianshiju", ce.getKey())) {
            item = new ScrollItem(ce.getName(), TVSeriesListFragment.newInstance(ce.getId()));
        }else if (TextUtils.equals("zhuanti", ce.getKey())){
            item = new ScrollItem(ce.getName(), SpecialTopicListFragment.newInstance(ce.getId()));
        }else {
            item = new ScrollItem(ce.getName(), HeadLineFragment.newInstance(ce.getId(), ce.getKey(), ce.getSliderId()));
        }
        return item;
    }

    private void setSelectedTextColor(int selectedPosition) {
        //        if (oldSelectedPosition != selectedPosition)
        if (itemList == null || itemList.size() == 0) return;
        Fragment framName = itemList.get(selectedPosition).getFragment();
        int defaultIndex = 4;
        if (framName instanceof LiveRadioFragment) {
            defaultIndex = 2;
        }
        if (framName instanceof LiveTvFragment) {
            defaultIndex = 1;
        }
        RxBus.getInstance().post(new Intent(IntentUtil.ACTION_SCROLL_ITEM_OK).putExtra("pos", defaultIndex));
        //        RxBus.getInstance().post(selectedPosition);
        TextView oldSelectedText = (TextView) mLinearLayout.findViewWithTag(oldSelectedPosition).
                findViewById(R.id.scroll_item_text_id);
        TextView selectedText = (TextView) mLinearLayout.findViewWithTag(selectedPosition).
                findViewById(R.id.scroll_item_text_id);
        oldSelectedText.setTextColor(getResources().getColor(R.color.news_mulity_column_noraml));
        selectedText.setTextColor(getResources().getColor(R.color.news_mulity_column_select));
        oldSelectedPosition = selectedPosition;
    }

    @Override
    public void onClick(View v) {
        //        if (v == mSearchBtn) {
        //        }
        if (v == _hsTopcRightBtn) {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginOkSubscription != null) {
            loginOkSubscription.unsubscribe();
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(final int position) {
            int dx = (itemWidthList.get(position) - item_width) / 2;
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
