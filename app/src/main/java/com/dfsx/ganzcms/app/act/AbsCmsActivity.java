package com.dfsx.ganzcms.app.act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.business.LanguageUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.CommuntyDatailHelper;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.ContentCmsInfoEntry;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.ganzcms.app.view.MoreTextView;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.videoijkplayer.util.NetworkChangeManager;
import com.dfsx.videoijkplayer.util.NetworkChangeReceiver;
import com.dfsx.videoijkplayer.util.NetworkUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by heyang on 2017/12/5.
 */
public abstract class AbsCmsActivity extends AbsVideoActivity implements PullToRefreshBase.OnRefreshListener2<ListView>
        , View.OnClickListener {
    protected Activity act;
    protected Context context;
    protected PullToRefreshListView pullToRefreshListView;
    protected ListView listView;
    protected LinearLayout emptyLayoutContainer;
    protected FrameLayout topListViewContainer;
    protected FrameLayout bottomListViewContainer;
    protected FrameLayout fullContainer;
    protected View portLayout;
    protected ContentCmsApi _contentApi;
    private IsLoginCheck mloginCheck;
    private android.view.animation.Animation animation;
    protected CommuntyDatailHelper _newsHelper;
    EmptyView itememptyView;
    boolean isWifi = true;
    protected boolean isConectNet = true;
    protected long _cmsId = -1;

    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            _cmsId = bundle.getLong("index");
        }
        View view = getLayoutInflater().inflate(R.layout.abs_cms_view, null);

        act = this;
        context = this;
        itememptyView = new EmptyView(this);
        _newsHelper = new CommuntyDatailHelper(this);
        _contentApi = _newsHelper.getmContentCmsApi();
        mloginCheck = new IsLoginCheck(this);
        int type = NetworkUtil.getConnectivityStatus(context);
        isWifi = type != NetworkUtil.TYPE_WIFI ? false : true;
        if (type == NetworkUtil.TYPE_NOT_CONNECTED) isConectNet = false;
        NetworkChangeManager.getInstance().addOnNetworkChangeListener(_onNetworkChangeListener);
        animation = AnimationUtils.loadAnimation(context, R.anim.nn);
        portLayout = (View) view.
                findViewById(R.id.portlayout_container);

        topListViewContainer = (FrameLayout) view.
                findViewById(R.id.top_list_view_layout);
        bottomListViewContainer = (FrameLayout) view.
                findViewById(R.id.bottom_list_view_layout);

        fullContainer = (FrameLayout) view.
                findViewById(R.id.full_list_view_layout);

        pullToRefreshListView = (PullToRefreshListView) view.
                findViewById(R.id.frag_list_view);

        emptyLayoutContainer = (LinearLayout) view.
                findViewById(R.id.empty_layout);
        listView = pullToRefreshListView.getRefreshableView();

        pullToRefreshListView.setMode(getListViewMode());

        pullToRefreshListView.setOnRefreshListener(this);

        setListAdapter(listView);
//        listView.setEmptyView(emptyLayoutContainer);
        setEmptyLayout(emptyLayoutContainer);

        setTopView(topListViewContainer);
        setBottomView(bottomListViewContainer);
        setFullView(fullContainer);

        setContentView(view);
    }

    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.PULL_FROM_END;
    }

    public abstract void setListAdapter(ListView listView);

    protected void setEmptyLayout(LinearLayout container) {

    }

    protected void setPortLayoutContainer(boolean isFull) {
        portLayout.setVisibility(isFull ? View.GONE : View.VISIBLE);
        fullContainer.setVisibility(isFull ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    protected void setTopView(FrameLayout topListViewContainer) {

    }

    protected void setBottomView(FrameLayout bottomListViewContainer) {

    }

    protected void setFullView(FrameLayout fullContainerViewContainer) {

    }

    public void addFollowed(final long useId, boolean isAttion, final DataRequest.DataCallback callback) {
        if (!mloginCheck.checkLogin()) return;
        int fouc = isAttion ? 1 : 0;
        _newsHelper.setNewAttentionUser(useId, fouc, callback);
    }

    public void setAttteonTextStatus(ImageView img, boolean falg, boolean isShowMsg) {
        String msg = "关注成功";
        if (falg) {
            img.setImageResource(R.drawable.cvideo_att_select);
        } else {
            msg = "已取消关注";
            img.setImageResource(R.drawable.cviddeo_info_att);
        }
        if (isShowMsg) {
            LSUtils.toastMsgFunction(context, msg);
        }
    }

    public void addFavoritybtn(long id, boolean flag, DataRequest.DataCallback callback) {
        if (!mloginCheck.checkLogin()) return;
        _contentApi.farityToptic(id, flag, callback);
    }

    public void addPraisebtn(long id, DataRequest.DataCallback callback) {
        if (!mloginCheck.checkLogin()) return;
        _contentApi.pubContentPraise(id, callback);
    }

    public void addStrampbtn(long id, DataRequest.DataCallback callback) {
        if (!mloginCheck.checkLogin()) return;
        _contentApi.pubContentStamp(id, callback);
    }

    public void onShareWnd(View v, ContentCmsInfoEntry getEntry) {
        if (getEntry == null) return;
        ShareContent content = new ShareContent();
        content.title = getEntry.getTitle();
        content.disc = getEntry.getSummary();
        content.setId(getEntry.getId());
        if (getEntry.getThumbnail_urls() != null && getEntry.getThumbnail_urls().size() > 0)
            content.thumb = getEntry.getThumbnail_urls().get(0);
//        if (LanguageUtil.isTibetanLanguage(this)) {
//            content.language = "zn_tbr";
//        }
        _newsHelper.shareBottomUiWnd(v, content,this);
    }

    public void writeCommendbtn(View v, long id, long ref_id, final DataRequest.DataCallback callback) {
        _newsHelper.showCommendDialog(v, id, ref_id, new NewsDatailHelper.ICommendDialogLbtnlister() {
            @Override
            public boolean onParams(long id, long ref_id, String context) {
                if (TextUtils.isEmpty(context)) {
                    ToastUtils.toastMsgFunction(act, "评论内容不能为空");
                    return false;
                }
                if (!mloginCheck.checkLogin()) return false;
                _contentApi.createCmsCommend(id, ref_id, context, callback);
                return true;
            }
        });
    }

    /**
     * 更新点赞列表
     *
     * @param id
     * @param callback
     */
    public void updatePraiseList(long id, Observer callback) {
        Observable.just(id).
                subscribeOn(Schedulers.io()).
                observeOn(Schedulers.io()).
                map(new Func1<Long, String>() {
                    @Override
                    public String call(Long id) {
                        String appendStr = _contentApi.getPraiseNumberList(id);
                        return appendStr;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback
                );
    }


    public void lbtnClickAnimal(final TextView view, final long number) {
        view.startAnimation(animation);
        view.setText("+1");
        new Handler().postDelayed(new Runnable() {
            public void run() {
                view.setText(number + "");
            }
        }, 50);
    }

    public void updatePraiseUiList(MoreTextView moreTextView, String list) {
        if (moreTextView == null) return;
        View parent = (View) moreTextView.getParent().getParent();
        if (!(list == null || TextUtils.isEmpty(list)
                || TextUtils.isEmpty("null"))) {
            parent.setVisibility(View.VISIBLE);
            moreTextView.setText(list);
        } else {
            parent.setVisibility(View.GONE);
        }
    }

    public void setFavStatus(ImageView image, boolean flag, boolean isShowMsg) {
        String msg = getResources().getString(R.string.favority_success_hit);
        if (flag) {
            image.setImageDrawable(getResources().getDrawable(R.drawable.communtiy_item_fal_sel));
        } else {
            msg = getResources().getString(R.string.favority_faild_hit);
            image.setImageDrawable(getResources().getDrawable(R.drawable.cvidoe_favirty_normal));
        }
        if (isShowMsg) {
            LSUtils.toastMsgFunction(context, msg);
            RxBus.getInstance().post(new Intent(IntentUtil.UPDATE_FAVIRITY_MSG));
        }
    }

    private NetworkChangeReceiver.OnNetworkChangeListener _onNetworkChangeListener = new NetworkChangeReceiver.OnNetworkChangeListener() {
        @Override
        public void onChange(int networkType) {
            if (networkType != NetworkUtil.TYPE_WIFI) {
                //不是wifi
                Log.e("TAG", "Cvideo=====不是wifi+===");
                isWifi = false;
                if (networkType == NetworkUtil.TYPE_NOT_CONNECTED) {
                    isConectNet = false;
                }
            } else {
                Log.e("TAG", "Cvideo=====是wifi=====");
                isWifi = true;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkChangeManager.getInstance().
                removeOnNetworkChangeListener(_onNetworkChangeListener);
    }

    public void LoadImagee(ImageView images, String filePath) {
        if (act == null) return;
        Glide.with(act)
                .load(filePath)
                .placeholder(R.color.transparent)
                .error(R.drawable.glide_default_image)
                .into(images);
    }

}
