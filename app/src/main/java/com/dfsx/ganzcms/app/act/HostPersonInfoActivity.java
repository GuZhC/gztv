package com.dfsx.ganzcms.app.act;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.core.common.view.banner.BannerItem;
import com.dfsx.core.common.view.banner.BaseBanner;
import com.dfsx.core.common.view.banner.SimpleImageBanner;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.CMSContentCommentHelper;
import com.dfsx.ganzcms.app.business.HostDetailsHelper;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.business.RadioPlayManager;
import com.dfsx.ganzcms.app.fragment.HostMessageFragment;
import com.dfsx.ganzcms.app.fragment.HostPersonIntroduceFragment;
import com.dfsx.ganzcms.app.model.ContentComment;
import com.dfsx.ganzcms.app.model.IHostDetails;
import com.dfsx.ganzcms.app.view.CommentEditPopupwindow;
import com.dfsx.ganzcms.app.view.CommentEditWindow;
import com.dfsx.ganzcms.app.view.zoom.IPullZoom;
import com.dfsx.ganzcms.app.view.zoom.ZoomHeaderCoordinatorLayout;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.lzcms.liveroom.view.CenterGroupChangeBar;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.openimage.OpenImageUtils;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;

import java.util.ArrayList;
import java.util.List;

public class HostPersonInfoActivity extends BaseActivity implements IPullZoom, PullToRefreshRecyclerView.PullRecyclerHelper {

    public static final String KEY_HOST_ID = "HostPersonInfoActivity_host_id";

    private Activity act;

    private View topBar;
    private ImageView actFinish;
    private TextView topTitleText;
    private TextView topRightText;
    private ImageView btnConcern;
    private ImageView btnGoPersonHome;
    private SimpleImageBanner imageBanner;
    private CenterGroupChangeBar groupChangeBar;
    private ViewPager viewPager;
    private View indicatorBarContainer;
    private ZoomHeaderCoordinatorLayout zoomHeaderCoordinatorLayout;
    private AppBarLayout barLayout;

    private View audioView;
    private ImageView radioCicrcleImage;
    private TextView radioTitleText;
    private ImageView radioStateImage;
    private ImageView editMessageView;

    private int currentPage;
    private int headerOffSetSize;

    private long contentId;

    private IHostDetails hostDetails;

    private CommentEditWindow editPopupwindow;

    private CMSContentCommentHelper commentHelper;
    private HostDetailsHelper detailsHelper;

    private MyDataManager dataManager;

    private HostPersonIntroduceFragment introduceFragment;
    private HostMessageFragment messageFragment;

    private String audioUrl;

    private IsLoginCheck loginCheck;

    private RotateAnimation radioRoteAnim;
    private SharePopupwindow popupwindow;

    private boolean isHasAudioData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        contentId = getIntent() != null ?
                getIntent().getLongExtra(KEY_HOST_ID, 0L) : 0L;
        setContentView(R.layout.act_host_person_info);
        commentHelper = new CMSContentCommentHelper(act);
        loginCheck = new IsLoginCheck(act);
        dataManager = new MyDataManager(act);
        detailsHelper = new HostDetailsHelper(act);
        initView();
        initPager();
        //        initData();

        getData();
    }

    private void initEditPopwupwindow() {
        editPopupwindow = new CommentEditWindow();
        editPopupwindow.setOnBtnSendClickListener(new CommentEditWindow.OnBtnSendClickListener() {
            @Override
            public void onSendClick(long id, Object tag, String content) {
                long refId = (long) tag;
                commentHelper.addContentComment(id, content, refId, new DataRequest.DataCallback<Long>() {
                    @Override
                    public void onSuccess(boolean isAppend, Long data) {
                        Log.e("TAG", "add comment == " + data);
                        if (messageFragment != null) {
                            messageFragment.refreshData();
                        }
                        Toast.makeText(act, "留言成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        Toast.makeText(act, "留言失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initData() {
        ArrayList<BannerItem> list = new ArrayList<>();
        BannerItem item = new BannerItem();
        item.imgUrl = "http://cpc.people.com.cn/NMediaFile/2017/1018/MAIN201710181352077766232230363.jpg";
        item.title = "";
        list.add(item);
        item = new BannerItem();
        item.imgUrl = "http://cpc.people.com.cn/NMediaFile/2017/1018/MAIN201710181036112161918566331.jpg";
        item.title = "";
        list.add(item);
        item = new BannerItem();
        item.imgUrl = "http://cpc.people.com.cn/NMediaFile/2017/1018/MAIN201710181036112161918566331.jpg";
        item.title = "";
        list.add(item);
        imageBanner.setPeriod(4);
        imageBanner.setDelay(4);
        imageBanner.setSource(list).startScroll();
    }

    private void setBannerDataShow(final List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            return;
        }
        ArrayList<BannerItem> list = new ArrayList<>();
        for (String path : imageList) {
            BannerItem item = new BannerItem();
            item.imgUrl = path;
            item.title = "";
            list.add(item);
        }
        imageBanner.setPeriod(4);
        imageBanner.setDelay(4);
        imageBanner.setSource(list).startScroll();
        imageBanner.setOnItemClickL(new BaseBanner.OnItemClickL() {
            @Override
            public void onItemClick(int position) {
                OpenImageUtils.openImage(act, position, imageList.toArray(new String[0]));
            }
        });
    }

    private void initView() {
        zoomHeaderCoordinatorLayout = (ZoomHeaderCoordinatorLayout) findViewById(R.id.main_content);
        barLayout = (AppBarLayout) findViewById(R.id.appbar);
        topBar = findViewById(R.id.tool_bar_view);
        actFinish = (ImageView) findViewById(R.id.tool_back);
        indicatorBarContainer = findViewById(R.id.indicator_bar_container);
        topTitleText = (TextView) findViewById(R.id.tool_title_tv);
        topRightText = (TextView) findViewById(R.id.tool_right_tv);
        imageBanner = (SimpleImageBanner) findViewById(R.id.person_image_banner);
        groupChangeBar = (CenterGroupChangeBar) findViewById(R.id.center_indicator);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        editMessageView = (ImageView) findViewById(R.id.edit_image_view);
        audioView = findViewById(R.id.user_audio_view);
        radioCicrcleImage = (ImageView) findViewById(R.id.image_radio);
        radioTitleText = (TextView) findViewById(R.id.tv_radio_title);
        radioStateImage = (ImageView) findViewById(R.id.radio_status_image);
        btnConcern = (ImageView) findViewById(R.id.btn_concern);
        btnGoPersonHome = (ImageView) findViewById(R.id.btn_go_person);
        groupChangeBar.setBarTextArray(0, "介绍", "留言");
        zoomHeaderCoordinatorLayout.setPullZoom(barLayout,
                PixelUtil.dp2px(this, 390),
                PixelUtil.dp2px(this, 500), this);

        groupChangeBar.setOnBarSelectedChangeListener(new CenterGroupChangeBar.OnBarSelectedChangeListener() {
            @Override
            public void onSelectedChange(int selectedIndex) {
                if (currentPage != selectedIndex) {
                    currentPage = selectedIndex;
                    viewPager.setCurrentItem(selectedIndex, true);
                    setEditMessageViewVisible(currentPage == 1);
                    updateAudioViewVisiable();
                }
            }
        });

        RadioPlayManager.getInstance().setOnMediaPlayStateChangeListener(new RadioPlayManager.OnMediaPlayStateChangeListener() {
            @Override
            public void onStateChange(int currentState) {
                if (currentState == RadioPlayManager.STATE_PLAYING) {
                    radioStateImage.setImageResource(R.drawable.icon_radio_puase);
                    startRadioRoteAnim();
                } else {
                    radioStateImage.setImageResource(R.drawable.icon_radio_play);
                    topRadioRoteAnim();
                }
            }
        });

        btnConcern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hostDetails != null && loginCheck.checkLogin()) {
                    dataManager.addConcern(hostDetails.getHostUserId(), new DataRequest.DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(boolean isAppend, Boolean data) {
                            setConcernImage(data);
                        }

                        @Override
                        public void onFail(ApiException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
        btnGoPersonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hostDetails != null) {
                    IntentUtil.gotoPersonHomeAct(act, hostDetails.getHostUserId());
                }
            }
        });
        editMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginCheck.checkLogin()) {
                    if (editPopupwindow == null) {
                        initEditPopwupwindow();
                    }
                    editPopupwindow.setConentId(contentId);
                    editPopupwindow.setObjectTag(0L);
                    editPopupwindow.show(getSupportFragmentManager(), "host_comment_edit", v);
                }
            }
        });

        radioStateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!RadioPlayManager.getInstance().isPlaying()) {
                    if (RadioPlayManager.getInstance().isBackPlayState()) {
                        RadioPlayManager.getInstance().start();
                    } else {
                        RadioPlayManager.getInstance().play(audioUrl);
                    }
                } else {
                    RadioPlayManager.getInstance().pause();
                }
            }
        });
        actFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (currentPage != position) {
                    currentPage = position;
                    groupChangeBar.setCheckIndex(position);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        barLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                headerOffSetSize = verticalOffset;
            }
        });

        topRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareClick(v);
            }
        });
    }


    private void onShareClick(View v) {
        if (popupwindow == null) {
            popupwindow = new SharePopupwindow(this);
            popupwindow.setOnShareClickListener(new SharePopupwindow.OnShareClickListener() {
                @Override
                public void onShareClick(View v) {
                    SharePlatform platform = popupwindow.getSharePlatform(v);
                    if (platform != null) {
                        onSharePlatform(platform);
                    }
                }
            });
        }
        if (!isFinishing()) {
            popupwindow.show(v);
        }
    }

    private void onSharePlatform(SharePlatform platform) {
        if (hostDetails != null) {
            ShareContent content = new ShareContent();
            content.title = hostDetails.getHostNickName();
            content.disc = hostDetails.getHostNickName();
            content.thumb = hostDetails.getHostAudioImage();
            content.type = ShareContent.UrlType.WebPage;
            content.url = App.getInstance().getmSession().getBaseMobileWebUrl() + "/cms/content/" + hostDetails.getHostDetailsId();
            AbsShare share = ShareFactory.createShare(this, platform);
            share.share(content);
        }
    }

    private void isConCern(long id) {
        dataManager.isConcern(id, new DataRequest.DataCallback<Boolean>() {
            @Override
            public void onSuccess(boolean isAppend, Boolean data) {
                setConcernImage(data);
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 回复评论
     *
     * @param comment
     */
    public void replayContentComment(ContentComment comment) {
        if (comment != null) {
            if (editPopupwindow == null) {
                initEditPopwupwindow();
            }
            editPopupwindow.setConentId(contentId);
            editPopupwindow.setObjectTag(comment.getId());
            editPopupwindow.show(getSupportFragmentManager(), "host_comment_edit", barLayout, "回复" + comment.getAuthor_nickname());
        }
    }

    private void startRadioRoteAnim() {
        if (radioRoteAnim != null) {
            radioRoteAnim.cancel();
        }
        radioRoteAnim = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        radioRoteAnim.setInterpolator(lin);
        radioRoteAnim.setDuration(5000);//设置动画持续周期
        radioRoteAnim.setRepeatCount(-1);//设置重复次数
        radioRoteAnim.setFillAfter(false);//动画执行完后是否停留在执行完的状态
        radioRoteAnim.setStartOffset(10);//执行前的等待时间
        radioCicrcleImage.startAnimation(radioRoteAnim);
    }

    private void topRadioRoteAnim() {
        if (radioRoteAnim != null) {
            radioCicrcleImage.clearAnimation();
            radioRoteAnim = null;
        }
    }

    private void getData() {
        detailsHelper.getHostDetails(contentId, new ICallBack<IHostDetails>() {
            @Override
            public void callBack(IHostDetails data) {
                hostDetails = data;
                if (data != null) {
                    setBannerDataShow(hostDetails.getHostImageList());
                    setAudioView();
                    setTopBarData();
                    if (hostDetails.getHostUserId() != 0) {
                        isConCern(hostDetails.getHostUserId());
                    }
                    if (introduceFragment != null) {
                        introduceFragment.setHostData(data);
                    }
                    if (messageFragment != null) {
                        messageFragment.setHostData(data);
                    }
                }
            }
        });
    }

    private void setTopBarData() {
        if (hostDetails != null) {
            topTitleText.setText(hostDetails.getHostNickName());
        }
    }

    private void setAudioView() {
        if (hostDetails == null || TextUtils.isEmpty(hostDetails.getHostAudio())) {
            isHasAudioData = false;
            updateAudioViewVisiable();
            audioUrl = "";
        } else {
            isHasAudioData = true;
            updateAudioViewVisiable();
            GlideImgManager.getInstance().showImg(this,
                    radioCicrcleImage, hostDetails.getHostAudioImage());
            radioTitleText.setText(hostDetails.getHostAudioTitle());
            audioUrl = hostDetails.getHostAudio();
            //            if (!RadioPlayManager.getInstance().isPlaying()) {
            //                RadioPlayManager.getInstance().play(audioUrl);
            //            }
        }
    }

    private void updateAudioViewVisiable() {
        audioView.setVisibility(isHasAudioData && currentPage == 0 ?
                View.VISIBLE : View.GONE);
    }

    private void setConcernImage(boolean isConcern) {
        int res = isConcern ? R.drawable.icon_has_concerned : R.drawable.btn_concern_person;
        btnConcern.setImageResource(res);
    }

    private void initPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        introduceFragment = new HostPersonIntroduceFragment();
        messageFragment = new HostMessageFragment();
        fragments.add(introduceFragment);
        fragments.add(messageFragment);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

    public void setEditMessageViewVisible(boolean isVisible) {
        editMessageView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean isReadyForPullEnd() {
        boolean is = Math.abs(headerOffSetSize) >= barLayout.getHeight() - indicatorBarContainer.getHeight();
        return is;
    }

    @Override
    public boolean isReadyForPullStart() {
        return headerOffSetSize == 0;
    }

    @Override
    public void onPullZooming(int newScrollValue) {

    }

    @Override
    public void onPullZoomEnd() {

    }


    @Override
    protected void onPause() {
        super.onPause();
        RadioPlayManager.getInstance().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RadioPlayManager.getInstance().stop();
        RadioPlayManager.getInstance().release();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;

        public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments != null ? fragments.size() : 0;
        }
    }
}
