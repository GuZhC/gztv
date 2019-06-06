package com.dfsx.lzcms.liveroom.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.model.GiftModel;
import com.dfsx.lzcms.liveroom.util.PixelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 1. 在xml文件里面设置高度。 最好300dp左右
 * 在使用getData之前调用setSelectGiftMinValue（）方法
 * 2. 使用getData() 方法从网络加载数据； 另外一种是setData().加载数据。 需要调用initViewData（）
 * 3. 实现 OnSendClickLisener接口送礼物
 *
 * @author Administrator
 */
public class SelectGiftView extends LinearLayout {

    private static final int PAGE_SIZE = 8;
    public static final int GET_GOLD_FAIL = 13;
    public static final int GET_GOLD_SUCCESS = 14;

    private Context context;
    private ViewPager giftPager;
    private View middleView;
    private View emptyDataView;
    private LinearLayout pagerPoint;
    private View giftInfoView;
    private int allHeight;

    private ArrayList<GiftModel> allGiftList = new ArrayList<GiftModel>();
    private ArrayList<GiftModel> giftList;
    private ArrayList<GridView> mGridViewList;
    private ImageView[] pointArray;// 存放pager的点
    private GiftViewPagerAdapter pagerAdapter;
    private int gridViewHeight;// 计算出来的gridview的高度
    protected GiftModel gift;// 当前选择的gift对象
    private OnEmptyViewClickListener emptyViewClickListener;
    private View empty;

    private GetGift giftGetter;

    private int minGiftPrice;
    private int restGold = 0;
    //是直接赠送还是只是选择
    private boolean isSendOrNot = false;

    private Runnable runnableWhenDown;
    private long officeId;
    private long accountId;

    private long defaultGiftId = -1;

    //这个用于表示当前启动的界面的类型，-1为默认的，允许所有操作
    //如果>=0，则只匹配相应类型的操作请求
    private int curFlag = -1;

    /**
     * 设置默认的你想要选中的礼物位置
     */
    private int defaultSelectedCount = -1;

    private boolean isInitFirst = true;

    private boolean isCouldCancelSelected = false;

    public SelectGiftView(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public SelectGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    @SuppressLint("NewApi")
    public SelectGiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    public interface OnEmptyViewClickListener {
        void onClickEmptyView();
    }


    public void setOnEmptyViewClickListener(OnEmptyViewClickListener l) {
        emptyViewClickListener = l;
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.select_gift_view, this);
        giftInfoView = findViewById(R.id.gift_info_layout);
        middleView = findViewById(R.id.item_middle_view);
        giftPager = (ViewPager) findViewById(R.id.gift_viewpager);
        pagerPoint = (LinearLayout) findViewById(R.id.gift_point_view_group);
        empty = findViewById(R.id.select_gift_empty_view);
        emptyDataView = findViewById(R.id.empty_view);
        emptyDataView.setVisibility(View.GONE);

        emptyDataView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (emptyListener != null) {
                    emptyListener.onEmptyDataViewClick();
                } else {
                    requestGiftData();
                }
            }
        });

        empty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emptyViewClickListener != null) {
                    emptyViewClickListener.onClickEmptyView();
                }
            }
        });

        //计算高度
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectGiftHeight);
        allHeight = (int) a.getDimension(R.styleable.SelectGiftHeight_mheight,
                PixelUtil.dp2px(context, 300));
        gridViewHeight = allHeight - PixelUtil.dp2px(context, 10);
        ViewGroup.LayoutParams middleParams = middleView.getLayoutParams();
        middleParams.height = allHeight;
        middleView.setLayoutParams(middleParams);
        ViewGroup.LayoutParams params = giftPager.getLayoutParams();
        params.height = gridViewHeight;
        giftPager.setLayoutParams(params);

        giftPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int point) {
                if (pointArray != null) {
                    for (int i = 0; i < pointArray.length; i++) {
                        if (point != i) {
                            pointArray[i]
                                    .setBackgroundResource(getPointUnfocusedImage());
                        } else {
                            pointArray[i]
                                    .setBackgroundResource(getPointFocusedImage());
                        }
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        this.getViewTreeObserver().
                addOnGlobalLayoutListener(new ViewTreeObserver.
                        OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (isInitFirst) {
                            requestGiftData();
                            isInitFirst = false;
                        }
                    }
                });
        //测试
        //		getData((int) LXApplication.getInstance().getOfficeId(), LXApplication
        //				.getInstance().getAccountId());
        //		initViewData();
    }

    private void hideGiftInfo() {
        giftInfoView.setVisibility(View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showGiftInfo(boolean isAnimation) {
        giftInfoView.setVisibility(View.VISIBLE);
        if (isAnimation) {
            int start = allHeight;
            final ObjectAnimator anim1 = ObjectAnimator.ofFloat(this, "alpha", 0, 1);
            anim1.setDuration(400);
            anim1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            anim1.start();
        } else {
            setVisibility(View.VISIBLE);
        }
    }


    private void setEmptyDataView() {
        if (giftList != null && giftList.size() > 0) {
            emptyDataView.setVisibility(View.GONE);
        } else {
            emptyDataView.setVisibility(View.VISIBLE);
        }
    }

    public void setGetter(GetGift getter) {
        this.giftGetter = getter;
    }

    public void isCouldCancelSelected(boolean isCouldCancel) {
        this.isCouldCancelSelected = isCouldCancel;
    }

    private void initGiftSelection() {
        if (giftList == null) {
            return;
        }

        for (int index = 0; index < giftList.size(); index++) {
            GiftModel gift = giftList.get(index);
            if (defaultGiftId >= 0 && defaultGiftId == gift.getId()) {
                gift.setSelected(true);
                this.gift = gift;
            } else {
                gift.setSelected(false);
            }
        }
    }

    /**
     * 设置选择的选项
     *
     * @param id
     */
    public void setSelectedGiftbyId(long id) {
        defaultGiftId = id;
        initGiftSelection();
        initViewData();
    }

    /**
     * 通过设置显示的位置来选中礼物
     *
     * @param count 从0号位置开始
     */
    public void setSelectedtGiftByViewCount(int count) {
        defaultSelectedCount = count;
        if (giftList != null && count >= 0 && count < giftList.size()) {
            setSelectedGiftbyId(giftList.get(count).getId());
        }
    }

    public void clearSelectedGift() {
        gift = null;
        setSelectedGiftbyId(-1);
    }

    /**
     * 设置是否是赠送，或者只是选择，在onSend中返回这个值
     *
     * @param send
     */
    public void setSendOrNot(boolean send) {
        isSendOrNot = send;
    }


    /**
     * 礼物数量变化
     *
     * @param giftId
     * @param delta
     */
    public void giftNumChange(int giftId, int delta) {
        for (int index = 0; index < allGiftList.size(); index++) {
            GiftModel gm = allGiftList.get(index);
            if (gm.getId() == giftId) {
                int dstCount = gm.getNum() + delta;
                gm.setNum(dstCount < 0 ? 0 : dstCount);
            }
        }

        gridNotify();
    }

    /**
     * 获取礼物数量
     *
     * @param giftId
     * @return
     */
    public int getGiftLeftNum(int giftId) {
        for (int index = 0; index < allGiftList.size(); index++) {
            GiftModel gm = allGiftList.get(index);
            if (gm.getId() == giftId) {
                return gm.getNum();
            }
        }

        return 0;
    }

    /**
     * 通过GetGift接口去获取礼物数据
     */
    public void requestGiftData() {
        if (giftGetter != null) {
            giftGetter.getGifts(new ICallBack<ArrayList<GiftModel>>() {
                @Override
                public void callBack(ArrayList<GiftModel> data) {
                    giftList = data;
                    if (giftList != null && giftList.size() > 0) {
                        if (defaultGiftId != -1) {
                            setSelectedGiftbyId(defaultGiftId);
                        } else if (defaultSelectedCount != -1) {
                            setSelectedtGiftByViewCount(defaultSelectedCount);
                        } else {
                            initViewData();
                        }
                    }
                }
            });

        }
    }

    /**
     * 直接设置显示的数据
     *
     * @param giftList
     */
    public void setGiftData(ArrayList<GiftModel> giftList) {
        this.giftList = giftList;
        initViewData();
    }

    public void initViewData() {
        if (giftList != null) {
            initPagerViews(giftList);
            pagerAdapter = new GiftViewPagerAdapter(context, mGridViewList);
            giftPager.setAdapter(pagerAdapter);
            if (mGridViewList != null && mGridViewList.size() > 0) {
                initPointViews(mGridViewList.size());
            }
        }
        setEmptyDataView();
    }

    /**
     * 禁止parent拦截手势
     *
     * @param run
     */
    public void disableParentInterception(final Runnable run) {
        this.runnableWhenDown = run;

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (runnableWhenDown != null && event.getAction() == MotionEvent.ACTION_DOWN) {
                    runnableWhenDown.run();
                }
                return false;
            }
        });

        if (giftPager != null) {
            giftPager.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (runnableWhenDown != null && event.getAction() == MotionEvent.ACTION_DOWN) {
                        runnableWhenDown.run();
                    }
                    return false;
                }
            });
        }

        if (mGridViewList != null) {
            for (int index = 0; index < mGridViewList.size(); index++) {
                GridView view = mGridViewList.get(index);
                view.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (runnableWhenDown != null && event.getAction() == MotionEvent.ACTION_DOWN) {
                            runnableWhenDown.run();
                        }
                        return false;
                    }
                });
            }
        }
    }

    public void show() {
        setVisibility(View.VISIBLE);
        giftPager.setVisibility(View.VISIBLE);
    }

    public void show(int incType) {
        curFlag = incType;
        setVisibility(View.VISIBLE);
        giftPager.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //		int pagerHeight = giftPager.getHeight() - giftPager.getPaddingBottom()
        //				- giftPager.getPaddingTop();
        //		Log.v("TAG", "pagerHeight == " + pagerHeight);
        //		gridViewHeight = pagerHeight;// 前提是gridView的高度设置是match_parent
    }

    private void initPagerViews(ArrayList<GiftModel> list) {
        int pageCount = (int) Math.ceil(list.size() / ((double) PAGE_SIZE));
        if (mGridViewList == null) {
            mGridViewList = new ArrayList<GridView>();
        } else {
            mGridViewList.clear();
        }
        for (int i = 0; i < pageCount; i++) {
            NoScrollGridView giftPage = (NoScrollGridView) LayoutInflater
                    .from(context).inflate(R.layout.select_gift_grid, null)
                    .findViewById(R.id.select_gift_grid);
            giftPage.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (runnableWhenDown != null) {
                        runnableWhenDown.run();
                    }
                    return false;
                }
            });
            GiftGridViewAdapter adapter = new GiftGridViewAdapter(context,
                    list, i);
            giftPage.setAdapter(adapter);
            giftPage.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    position = position + getCurrentPageCount() * PAGE_SIZE;
                    changeGiftCheckedState(position);
                }
            });
            mGridViewList.add(giftPage);
        }
    }

    private void changeGiftCheckedState(int pos) {
        if (mGridViewList != null && getCurrentPageCount() >= 0
                && getCurrentPageCount() < mGridViewList.size()) {
            if (giftList.get(pos).isSelected()) {
                if (isCouldCancelSelected) {
                    if (selectedChangeListener != null) {
                        selectedChangeListener.onSelectedChange();
                    }
                    giftList.get(pos).setSelected(false);
                    gift = null;
                }
            } else {
                if (selectedChangeListener != null) {
                    selectedChangeListener.onSelectedChange();
                }
                for (int i = 0; i < giftList.size(); i++) {
                    if (i == pos) {
                        giftList.get(pos).setSelected(true);
                        gift = giftList.get(pos);
                    } else {
                        giftList.get(i).setSelected(false);
                    }
                }
            }
            gridNotify();

        }
    }

    private void gridNotify() {
        for (int j = 0; j < mGridViewList.size(); j++) {//跟新view
            GiftGridViewAdapter curAdapter = (GiftGridViewAdapter) mGridViewList
                    .get(j).getAdapter();
            if (curAdapter != null) {
                curAdapter.setData(giftList, j);
                curAdapter.notifyDataSetChanged();
            }
        }
    }

    public GiftModel getSelectedGift() {
        return gift;
    }

    private int getCurrentPageCount() {
        return giftPager.getCurrentItem();
    }

    public ViewPager getViewPager() {
        return giftPager;
    }

    private void initPointViews(int pointNum) {
        ImageView point;
        pointArray = new ImageView[pointNum];
        pagerPoint.removeAllViews();
        for (int i = 0; i < pointNum; i++) {
            LayoutParams margin = new LayoutParams(
                    PixelUtil.dp2px(context, 4),
                    PixelUtil.dp2px(context, 4));
            // 设置每个小圆点距离左边的间距
            margin.setMargins(10, 0, 0, 0);
            point = new ImageView(context);
            // 设置每个小圆点的宽高
            point.setLayoutParams(new LayoutParams(PixelUtil.dp2px(context, 4), PixelUtil.dp2px(context, 4)));
            pointArray[i] = point;
            if (i == 0) {
                // 默认选中第一张图片
                pointArray[i].setBackgroundResource(getPointFocusedImage());
            } else {
                // 其他图片都设置未选中状态
                pointArray[i].setBackgroundResource(getPointUnfocusedImage());
            }
            pagerPoint.addView(pointArray[i], margin);
        }
    }

    public ArrayList<GiftModel> getGiftList() {
        return giftList;
    }

    private int getPointFocusedImage() {
        return R.drawable.public_point_select;
    }

    private int getPointUnfocusedImage() {
        return R.drawable.public_point_unselect;
    }

    class GiftViewPagerAdapter extends PagerAdapter {
        private List<GridView> array;

        public GiftViewPagerAdapter(Context context, List<GridView> pageViews) {
            // super(pageViews);
            this.array = pageViews;
        }

        public void setData(List<GridView> pageViews) {
            this.array = pageViews;
        }

        @Override
        public int getCount() {
            return array.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @SuppressLint("NewApi")
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            try {
                if (array.get(arg1).getParent() == null) {
                    ((ViewPager) arg0).addView(array.get(arg1));
                } else {
                    ((ViewGroup) array.get(arg1).getParent()).removeAllViews();
                    ((ViewPager) arg0).addView(array.get(arg1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return array.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }

    class GiftGridViewAdapter extends BaseAdapter {

        private ViewHolder holder;
        private ArrayList<GiftModel> mList;
        private Context mContext;

        public GiftGridViewAdapter(Context context, ArrayList<GiftModel> list,
                                   int page) {
            mContext = context;
            mList = new ArrayList<GiftModel>();
            int i = page * PAGE_SIZE;
            int iEnd = i + PAGE_SIZE;
            while ((i < list.size()) && (i < iEnd)) {
                mList.add(list.get(i));
                i++;
            }
        }

        public ArrayList<GiftModel> getData() {
            return mList;
        }

        public void setData(ArrayList<GiftModel> list, int page) {
            int i = page * PAGE_SIZE;
            int iEnd = i + PAGE_SIZE;
            mList.clear();
            while ((i < list.size()) && (i < iEnd)) {
                mList.add(list.get(i));
                i++;
            }
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_select_gift_grid, null);
                holder.itemBg = convertView.findViewById(R.id.gift_item_bg);
                holder.giftIcon = (ImageView) convertView
                        .findViewById(R.id.item_gift_icon);
                holder.giftName = (TextView) convertView
                        .findViewById(R.id.item_gift_name);
                holder.giftPrice = (TextView) convertView
                        .findViewById(R.id.item_gift_price);
                holder.giftNum = (TextView) convertView
                        .findViewById(R.id.item_gift_num);
                holder.giftCover = (ImageView) convertView
                        .findViewById(R.id.item_gift_img_cover);
                // 强制设置gridView的item的高度
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.itemBg
                        .getLayoutParams();
                params.height = gridViewHeight / 2;
                holder.itemBg.setLayoutParams(params);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            GiftModel gift = mList.get(position);
            //            gift.showImageView(context, holder.giftIcon);
            //修改加载图片跳动的问题
            Glide.with(context)
                    .load(gift.getImgPath())
                    .fitCenter()
                    .placeholder(R.drawable.glide_default_image)
                    .error(R.drawable.glide_default_image)
                    .dontAnimate()
                    .into(holder.giftIcon);
            holder.giftName.setText(gift.getName());
            holder.giftPrice.setText(getPriceString(gift.getPrice()));
            setGiftNumText(holder.giftNum, gift);
            if (gift.isSelected() && gift.getImgResId2() == 0 &&
                    TextUtils.isEmpty(gift.getGifPath())) {
                Animation scaleA = AnimationUtils.loadAnimation(context, R.anim.gift_selected_anim);
                holder.giftIcon.startAnimation(scaleA);

            } else {
                holder.giftIcon.clearAnimation();

            }
            if (gift.isSelected()) {
                holder.itemBg
                        .setBackgroundResource(R.drawable.bg_item_selected_gift);
            } else {
                holder.itemBg
                        .setBackgroundResource(R.color.transparent);
            }
            return convertView;
        }

        protected void setGiftNumText(TextView tv, GiftModel gift) {
            if (gift.getNum() == 0) {
                tv.setVisibility(View.GONE);
            } else {
                tv.setText("" + gift.getNum());
                tv.setVisibility(View.VISIBLE);
            }
        }

        private String getPriceString(int num) {
            if (num == 0) {
                return "免费";
            } else {
                return num + "乐币";
            }
        }

        class ViewHolder {
            ImageView giftIcon;
            TextView giftName;
            TextView giftPrice;
            TextView giftNum;
            View itemBg;
            ImageView giftCover;
        }
    }

    private FilterSelectGiftData filterData;

    public interface FilterSelectGiftData {
        void filterData(ArrayList<GiftModel> data);
    }

    private OnEmptyDataViewClickListener emptyListener;

    public void setOnEmptyDataViewClickListener(OnEmptyDataViewClickListener l) {
        emptyListener = l;
    }

    public interface OnEmptyDataViewClickListener {
        void onEmptyDataViewClick();
    }

    private OnSelectedChangeListener selectedChangeListener;

    public void setOnSelectedChangeListener(OnSelectedChangeListener l) {
        this.selectedChangeListener = l;
    }

    public interface OnSelectedChangeListener {
        void onSelectedChange();
    }
}
