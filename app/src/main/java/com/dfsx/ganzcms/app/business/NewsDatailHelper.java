package com.dfsx.ganzcms.app.business;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.business.LanguageUtil;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.BuildConfig;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.ActNewsImageDetails;
import com.dfsx.ganzcms.app.act.CmsImageTextActivity;
import com.dfsx.ganzcms.app.act.CmsVideoActivity;
import com.dfsx.ganzcms.app.act.GanZiTopBarActivity;
import com.dfsx.ganzcms.app.fragment.*;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.view.EditTextEx;
import com.dfsx.lzcms.liveroom.fragment.BaseAndroidWebFragment;
import com.dfsx.lzcms.liveroom.model.FullScreenRoomIntentData;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.view.LiveServiceSharePopupwindow;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.shop.fragment.CreditShopFragment;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by liuwb on 2016/8/29.
 */
public class NewsDatailHelper {
    private static final String TAG = "NewsDatailHelper";

    private Context context;
    private ContentCmsApi mContentCmsApi = null;
    private IsLoginCheck mloginCheck;
    LiveServiceSharePopupwindow shareNewPopupwindow;
    PopupWindow mCommendPopupWindow;
    EditText mCommendEdt;
    ImageView mSendBtn;
    SharePopupwindow shareBottomPopupwindow;


    public IsLoginCheck getMloginCheck() {
        return mloginCheck;
    }

    public NewsDatailHelper(Context context) {
        this.context = context;
        mContentCmsApi = new ContentCmsApi(context);
        mloginCheck = new IsLoginCheck(context);
    }

    public ContentCmsApi getmContentCmsApi() {
        return mContentCmsApi;
    }

    public void postCommendContent(JSONObject requestJson,
                                   DataRequest.DataCallback callback) {
        String url = App.getInstance().getmSession().makeUrl("services/comment");

        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).setJsonParams(requestJson).setToken(App.getInstance().getCurrentToken()).build();

        new DataRequest<Boolean>(context) {

            @Override
            public Boolean jsonToBean(JSONObject json) {

                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    return true;
                }
                return false;
            }
        }.
                getData(httpParams, false).
                setCallback(callback);
    }

    public void postCommitThumbNumber(JSONObject requestJson,
                                      DataRequest.DataCallback callback) {
        String url = App.getInstance().getmSession().makeUrl("services/like_and_dislike/like.json");

        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).setJsonParams(requestJson).setToken(App.getInstance().getCurrentToken()).build();

        new DataRequest<Integer>(context) {

            @Override
            public Integer jsonToBean(JSONObject json) {

                if (json != null) {
                    JSONArray bs = json.optJSONArray("result");
                    if (bs != null) {
                        int number = (Integer) bs.opt(0);
                        return number;
                    }
                }
                return -1;
            }
        }
                .
                        getData(httpParams, false)
                .

                        setCallback(callback);
    }


    public void getThumbNumber(JSONObject requestJson,
                               DataRequest.DataCallback callback) {
        String url = App.getInstance().getmSession().makeUrl("services/like_and_dislike/get.json");

        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).setJsonParams(requestJson).
                setToken(App.getInstance().getCurrentToken()).build();

        new DataRequest<Integer>(context) {

            @Override
            public Integer jsonToBean(JSONObject json) {

                if (json != null) {
                    JSONArray bs = json.optJSONArray("result");
                    if (bs != null) {
                        int number = (Integer) bs.opt(0);
                        return number;
                    }
                }
                return -1;
            }
        }
                .
                        getData(httpParams, false)
                .

                        setCallback(callback);
    }


    public void postIsAddedFavorite(JSONObject requestJson,
                                    DataRequest.DataCallback callback) {
        String url = App.getInstance().getmSession().makeUrl("services/flag/is_flagged");

        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).setJsonParams(requestJson).
                setToken(App.getInstance().getCurrentToken()).build();

        new DataRequest<Boolean>(context) {

            @Override
            public Boolean jsonToBean(JSONObject result) {

                boolean flag = result.optBoolean("result");
                JSONArray arry = result.optJSONArray("result");
                if (arry != null && arry.length() > 0) {
                    flag = arry.optBoolean(0);
                    return flag;
                }
                return false;
            }
        }
                .
                        getData(httpParams, false)
                .

                        setCallback(callback);
    }

    public void postAddFavorite(JSONObject requestJson,
                                DataRequest.DataCallback callback) {
        String url = App.getInstance().getmSession().makeUrl("services/flag/flag.json");

        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).setJsonParams(requestJson).
                setToken(App.getInstance().getCurrentToken()).build();

        new DataRequest<Boolean>(context) {

            @Override
            public Boolean jsonToBean(JSONObject result) {

                boolean flag = result.optBoolean("result");
                JSONArray arry = result.optJSONArray("result");
                if (arry != null && arry.length() > 0) {
                    flag = arry.optBoolean(0);
                    return flag;
                }
                return false;
            }
        }
                .
                        getData(httpParams, false)
                .

                        setCallback(callback);
    }

    /**
     * ========================================================================
     * 2.0 newsAPi  heyang
     */

    public void setFavoritybtn(long id, boolean flag, DataRequest.DataCallback callback) {
        if (!mloginCheck.checkLogin()) return;
        mContentCmsApi.farityToptic(id, !flag, callback);
    }

    public void praiseforCmsCommend(View view, long commendId, DataRequest.DataCallbackTag callbackTag) {
        if (!mloginCheck.checkLogin()) return;
        mContentCmsApi.praiseforCmsCommend(view, commendId, callbackTag);
    }

    public void getCotentInfo(long id, final String type, Action1<ContentCmsInfoEntry> callback) {
        Observable.just(id)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<Long, Observable<ContentCmsInfoEntry>>() {
                    @Override
                    public Observable<ContentCmsInfoEntry> call(Long id) {
                        ContentCmsInfoEntry entry = null;
                        entry = mContentCmsApi.getEnteyFromJson(id);
                        return Observable.just(entry);
                    }
                })
                .map(new Func1<ContentCmsInfoEntry, ContentCmsInfoEntry>() {
                    @Override
                    public ContentCmsInfoEntry call(ContentCmsInfoEntry entry) {
                        if (entry == null) return null;
                        List<ContentCmsEntry> dlist = null;
                        dlist = mContentCmsApi.getRelationContenList(entry.getId(), type,4);
                        if (dlist != null && !dlist.isEmpty())
                            entry.setRaletionList(dlist);
                        String praiseList = mContentCmsApi.getPraiseNumberList(entry.getId());
                        entry.setPraiseList(praiseList);
                        boolean flag = mContentCmsApi.isFav(entry.getId());
                        entry.setIsFav(flag);
                        Account user = App.getInstance().getUser();
                        if (user == null || user.getUser() == null) {

                        } else {
                            if (entry.getAuthor_id() != user.getUser().getId()) {
                                int res = new TopicalApi(context).isAttentionOther(entry.getAuthor_id());
                                boolean isAttionAuthor = res == 1 ? true : false;
                                entry.setAttend(isAttionAuthor);
                            }
                        }
                        return entry;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    /**
     * 异步获取相关内容
     *
     * @param id
     * @param type
     * @param callback
     */
    public void getRelationCotentList(long id, final String type, DataRequest.DataCallback callback) {
        mContentCmsApi.getRelationCmsList(id, type, callback);
    }

    public void getCommendList(long id, int page, DataRequest.DataCallback<List<CommendCmsEntry>> callback) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/contents/" + id + "/root-comments?";
        url += "page=" + page + "&size=20&sub_comment_count=3";
        new DataRequest<List<CommendCmsEntry>>(context) {
            @Override
            public List<CommendCmsEntry> jsonToBean(JSONObject json) {
                return mContentCmsApi.getRootCommendListFromJson(json);
            }
        }
                .getData(new DataRequest.HttpParamsBuilder().setUrl(url)
                        .setRequestType(DataReuqestType.GET)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), page > 1)
                .setCallback(callback);
    }

    /***
     *  评论内容确定
     */
    public interface ICommendDialogLbtnlister {
        public boolean onParams(long id, long resf_id, String context);
    }

    /**
     * 弹出 评论对话框
     *
     * @param moreBtnView
     * @param resf_cid
     */
    public void showCommendDialog(View moreBtnView, long id, long resf_cid, final ICommendDialogLbtnlister lister) {
        if (!mloginCheck.checkLogin()) return;
        if (mCommendPopupWindow == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View content = li.inflate(R.layout.layout_more, null, false);
            mCommendPopupWindow = new PopupWindow(content, MATCH_PARENT,
                    WRAP_CONTENT);
            mCommendPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mCommendPopupWindow.setFocusable(true);
            mCommendPopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
            mCommendPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            View parent = mCommendPopupWindow.getContentView();
            mCommendEdt = (EditText) parent.findViewById(R.id.commentEdit_replay_edt);
            mSendBtn = (ImageButton) parent.findViewById(R.id.commentButton);
            mCommendEdt.addTextChangedListener(new EditChangedLister(new EditChangedLister.EditeTextStatuimpl() {
                @Override
                public void onTextStatusCHanged(boolean isHas) {
                    if (isHas) {
                        mSendBtn.setImageResource(R.drawable.video_send_select);
                    } else {
                        mSendBtn.setImageResource(R.drawable.video_send_normal);
                    }
                }
            }));
            mSendBtn.setTag(resf_cid);
            mSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contnt = mCommendEdt.getText().toString().trim();
                    long refId = -1;
                    long id = -1;
                    if (view.getTag() != null) {
                        id = (long) view.getTag(R.id.news_commend_cmsId);
                        refId = (long) view.getTag(R.id.news_commend_cms_refid);
                    }
                    boolean flag = lister.onParams(id, refId, contnt);
                    if (flag) {
                        onFocusChange(false, mCommendEdt);
                        mCommendEdt.setText("");
                        mCommendPopupWindow.dismiss();
                    } else {
                        ToastUtils.toastNoContentCommendFunction(App.getInstance().getApplicationContext());
                    }
                }
            });
        }
        if (mCommendPopupWindow.isShowing()) {
            mCommendPopupWindow.dismiss();
            mCommendEdt.setText("");
        } else {
            mSendBtn.setTag(R.id.news_commend_cmsId, id);
            mSendBtn.setTag(R.id.news_commend_cms_refid, resf_cid);
            mCommendPopupWindow.showAtLocation(moreBtnView, Gravity.BOTTOM,
                    0, 0);
            onFocusChange(true, mCommendEdt);
        }
    }

    /***
     * 新闻发表评论
     * @param id
     * @param refId
     * @param content
     * @param back
     * @return
     */
    public boolean pubCommnedReplay(long id, long refId, String content, DataRequest.DataCallback back) {
        if (!mloginCheck.checkLogin()) return false;
        mContentCmsApi.createCmsCommend(id, refId, content, back);
        return true;
    }

    /**
     * 保存网页 数据
     */
    private static final String FILEM_NAME = "web_cache_data";

    public ContentCmsInfoEntry readWebCacheData(Context context, long id) {
        ContentCmsInfoEntry entry = null;
        entry = (ContentCmsInfoEntry) FileUtil.getFile(getFileDic(context) + id + "");
        return entry;
    }

    public boolean saveWebCacheData(Context c, long id, ContentCmsInfoEntry entry) {
        if (entry == null) return false;
        FileUtil.saveFile(getFileDic(c), id + "", entry);
        return true;
    }

    public static final String dir_NAME = App.getInstance().getPackageName() + "_cache";

    public String getFileDic(Context context) {
        return context.getFilesDir().getPath() + File.separator + dir_NAME
                + File.separator;
    }

    /**
     * 跳转到详情
     */
    public void goDetail(ContentCmsEntry channel) {
        if (channel == null) return;
        //  2018-10-16  判断是不是 动态接口
        if (TextUtils.equals(channel.getType(), "quick-entry")) {
            gotoQueryDyniamict(channel.getId());
            return;
        }
        if (channel.getModeType() == 0)
            channel.setModeType(getModeType(channel.getType()));
        String thumb = "";
        if (channel.getThumbnail_urls() != null && channel.getThumbnail_urls().size() > 0)
            thumb = channel.getThumbnail_urls().get(0).toString();
        if (channel.getModeType() == 3) {    //live
            gotoLiveingRoom(channel.getShowExtends());
        } else {
            preDetail(channel.getId(), channel.getModeType(), channel.getTitle(), thumb,
                    channel.getComment_count(), channel.getUrl());
        }
    }

    public void gotoQueryDyniamict(long id) {
        rx.Observable.just(id)
                .observeOn(Schedulers.io())
                .map(new Func1<Long, ContentCmsInfoEntry>() {
                    @Override
                    public ContentCmsInfoEntry call(Long id) {
                        ContentCmsInfoEntry entry = mContentCmsApi.getContentCmsInfo(id);
                        return entry;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ContentCmsInfoEntry>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ContentCmsInfoEntry entry) {
                        if (entry != null) {
                            try {
                                gotoDyniamictsk(entry);
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 得到跳转的方式
     */
    public int getModeType(String type) {
        return mContentCmsApi.getModeType(type, 0);
    }

    /**
     * 跳转到 直播页面 程度
     *
     * @param showExtends
     */
    public void gotoLiveingRoom(ContentCmsEntry.ShowExtends showExtends) {
        if (showExtends == null) return;
//        FullScreenRoomIntentData intentData = new FullScreenRoomIntentData();
//        intentData.setRoomOwnerNickName(showExtends.getOwner_nickname());
//        intentData.setRoomId(showExtends.getId());
//        intentData.setRoomOwnerId(showExtends.getOwner_id());
//        intentData.setRoomOwnerLogo(showExtends.getOwner_avatar_url());
        long roomId = showExtends.getId();
        int type = showExtends.getType();
        int state = showExtends.getState();
        checkLiveingRoom(roomId, type, state);
    }

    /**
     * 判断房间
     *
     * @param roomId
     * @param type
     * @param state
     */
    public void checkLiveingRoom(long roomId, int type, int state) {
        FullScreenRoomIntentData intentData = new FullScreenRoomIntentData();
        intentData.setRoomId(roomId);
        //个人
        if (type == 1) {
            if (state == 3) {
                //  直播结束
                com.dfsx.lzcms.liveroom.util.IntentUtil.goBackPlayRoom(context, roomId);
            } else {
                com.dfsx.lzcms.liveroom.util.IntentUtil.goFullScreenLiveRoom(context, intentData);
            }
        } else {
            com.dfsx.lzcms.liveroom.util.IntentUtil.goLiveServiceRoom(context, roomId);
        }
    }

    public void preDetail(long id, int showType, String title, String thumb, long commndnuber, String url) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putLong("index", id);
         if (showType == 4) {
            intent.setClass(context, ActNewsImageDetails.class);
            intent.putExtra("title", title);
            intent.putExtra("comnuber", commndnuber);
        } else if (showType == 9) {
            WhiteTopBarActivity.startAct(context, LiveTvFragment.class.getName(),
                    "电视直播", "", id);
        } else if (showType == 6) {  //广告 外链
            ShareContent share = ObtainShareContent(id, title, url, thumb, false);
            checkUrlValild(share);
            return;
        } else if (showType == 7) {  //投票
            ShareContent share = ObtainShareContent(id, title, url, thumb, true);
            checkUrlValild(share);
            return;
        } else if (showType == 2) {
            intent.setClass(context, CmsVideoActivity.class);
        } else if (showType == 11) {
            if (TextUtils.isEmpty(url))
                url = App.getInstance().getContentShareUrl() + id;
            ShareContent share = ObtainShareContent(id, title, url, thumb, true);
            checkUrlValild(share);
            return;
        }else if (showType == 12) {
            if (id != -1) {
                // 针对横幅  id
                queryInfoById(id);
            }
            return;
        }  else
            intent.setClass(context, CmsImageTextActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /*
     * 查询cms 详情
     */
    protected void queryInfoById(long id) {
        rx.Observable.just(id)
                .observeOn(Schedulers.io())
                .map(new Func1<Long, ContentCmsInfoEntry>() {
                    @Override
                    public ContentCmsInfoEntry call(Long id) {
                        ContentCmsInfoEntry entry = mContentCmsApi.getEnteyFromJson(id);
                        return entry;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ContentCmsInfoEntry>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ContentCmsInfoEntry data) {
                        if (data != null) {
                            preDetail(data.getId(), data.getShowType(), "", "", 0, data.getUrl());
                        }
                    }
                });
    }

    /**
     * @param entry lottery 抽奖 contribute 写稿  sign 签到
     *              show 直播  shop 商城  cms 文章  news 爆料
     */
    public void gotoDyniamictsk(ContentCmsInfoEntry entry) throws ApiException {
        ContentCmsInfoEntry.Quickentry quickentry = entry.getQuickentry();
        if (quickentry == null) return;
        String type = quickentry.getType();
//        type = "column";
        switch (type) {
            case "vote":
            case "lottery": {
                String thumb = "";
                if (entry != null && entry.getThumbnail_urls().size() > 0)
                    thumb = entry.getThumbnail_urls().get(0);
                ShareContent share = ObtainShareContent(entry.getId(), entry.getTitle(), entry.getUrl(), thumb, true);
                checkUrlValild(share);
            }
            break;
            case "cms": {
                ContentCmsInfoEntry contentInfo = quickentry.getContentInfo();
                if (contentInfo != null) {
                    String thumb = "";
                    if (contentInfo.getThumbnail_urls() != null && contentInfo.getThumbnail_urls().size() > 0)
                        thumb = contentInfo.getThumbnail_urls().get(0);
                    preDetail(contentInfo.getId(), contentInfo.getShowType(), contentInfo.getTitle(), thumb, contentInfo.getComment_count(), contentInfo.getUrl());
                }
            }
            break;
            case "news":
                break;
            case "contribute": {
                ArrayList<Long> wieteflgas = ColumnBasicListManager.getInstance().getEditWordColumnIdList();
                Bundle bundle = new Bundle();
                bundle.putSerializable(EditWordsFragment.KEY_WORD_COLUMN_ID_LIST, wieteflgas);
                WhiteTopBarActivity.startAct(context,
                        EditWordsFragment.class.getName(), "快速投稿", "确定", bundle);
            }
            break;
            case "sign":
                WhiteTopBarActivity.startAct(context,
                        QianDaoFragment.class.getName(), "每日签到");
                break;
            case "shop":
                WhiteTopBarActivity.startAct(context,
                        CreditShopFragment.class.getName(), "积分商城", "兑换记录");
                break;
            case "show": {
                LiveInfo info = quickentry.getLivInfo();
                if (info != null) {
                    checkLiveingRoom(quickentry.getRelated_show_id(), info.getType(), info.getState());
                } else {
                    throw new ApiException("获取直播信息失败");
                }
            }
            break;
            case "column": {
                long contenid = quickentry.getRelated_content_id();
                Bundle bundle = GanZiTopBarActivity.getTitleBundle(0, entry.getTitle());
                ColumnCmsEntry ce = ColumnBasicListManager.getInstance().findEntryById(contenid);
                android.support.v4.app.Fragment frag = getFragmentById(ce);
                if (ce == null) {
                    throw new ApiException("没有相关栏目");
                } else {
                    if (TextUtils.equals("shujishizhang",ce.getMachine_code())){
                        gotoSpecialWebUrl(ce.getId(), ce.getName(),ce.getIcon_url());
                        return;
                    }
                    String fragmentClass = frag.getClass().getName();
                    if (TextUtils.equals("com.dfsx.ganzcms.app.fragment.HeadLineFragment", fragmentClass)) {
                        bundle.putLong("id", ce.getId());
                        bundle.putString("type", ce.getKey());
                        bundle.putLong("slideId", ce.getSliderId());
                        bundle.putLong("slideId", ce.getSliderId());
                        bundle.putLong("dynamicId", ce.getDynamicId());
                    } else if (TextUtils.equals("com.dfsx.ganzcms.app.fragment.LiveTvFragment", fragmentClass)) {
                    } else if (TextUtils.equals("com.dfsx.ganzcms.app.fragment.ColumnPlayFragment", fragmentClass)) {
                        bundle.putString("key", ce.getMachine_code());
                    } else if (TextUtils.equals("com.dfsx.ganzcms.app.fragment.HandServiceFragment", fragmentClass)) {
                        bundle.putLong("type", ce.getId());
                    } else if (TextUtils.equals("com.dfsx.ganzcms.app.fragment.NewsGridMenuListFragment", fragmentClass)) {
                        bundle.putSerializable(NewsGridMenuListFragment.KEY_CITY_MENU_DATA, ce);
                    } else if (TextUtils.equals("com.dfsx.ganzcms.app.fragment.TVSeriesListFragment", fragmentClass)) {
                        bundle.putLong(TVSeriesListFragment.KEY_CONTENT_ID, ce.getId());
                    } else if (TextUtils.equals("com.dfsx.ganzcms.app.fragment.LifeServicePlayFragment", fragmentClass)) {  //服务
                        bundle.putLong(LifeServicePlayFragment.KEY_CONTENT_ID, ce.getId());
                    }
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    GanZiTopBarActivity.start(context, frag.getClass().getName(), intent);
                }
            }
            break;
        }
//        context.startActivity(intent);
    }

    private static final String PARENT_COLUMN = "parent_column";

    protected android.support.v4.app.Fragment getFragmentById(ColumnCmsEntry ce) {
        android.support.v4.app.Fragment farg = null;
        if (ce == null) {
            return null;
        }
        if (TextUtils.equals("tv", ce.getKey())) {
            farg = LiveTvFragment.newInstance(ce.getId());
        } else if (TextUtils.equals("radio", ce.getKey())) {
            farg = LiveRadioFragment.newInstance();
        } else if (TextUtils.equals("news", ce.getKey())
                || TextUtils.equals("recommend", ce.getKey()) || TextUtils.equals("home", ce.getKey())) {
            farg = HeadLineFragment.newInstance(ce.getId(), ce.getKey(), ce.getSliderId(), ce.getDynamicId());
        } else if (TextUtils.equals(PARENT_COLUMN, ce.getKey())) {
            if (TextUtils.equals(ce.getType(), "radio")) {
                farg = LiveRadioFragment.newInstance();
            } else if (TextUtils.equals(ce.getType(), "tv")) {
                farg = LiveTvFragment.newInstance(ce.getId());
            } else
                farg = ColumnPlayFragment.newInstance(ce.getMachine_code());
        } else if (TextUtils.equals("shows", ce.getKey())) {
            farg = ColumnPlayFragment.newInstance(ce.getMachine_code());
        } else if (TextUtils.equals("bianmin", ce.getKey())) {
            if (TextUtils.equals(ce.getList_type(), "normal")) {
                farg = HandServiceFragment.newInstance(ce.getId());
            } else {
                farg = LifeServicePlayFragment.newInstance(ce.getMachine_code());
            }
        } else if (TextUtils.equals("bnvod", ce.getKey())) {
            farg = BnBunchVodFragment.newInstance(1);
        } else if (TextUtils.equals("juzhen", ce.getKey())) {
            farg = NewsTwoListFragment.newInstance(ce);
        } else if (TextUtils.equals("dianshiju", ce.getKey())) {
            farg = TVSeriesListFragment.newInstance(ce.getId());
        } else if (TextUtils.equals("fuwu", ce.getKey())) {
            if (TextUtils.equals(ce.getList_type(), "normal")) {
                farg = HandServiceFragment.newInstance(ce.getId());
            } else {
                farg = LifeServicePlayFragment.newInstance(ce.getId());
            }
        } else {
            farg = HeadLineFragment.newInstance(ce.getId(), ce.getKey(), ce.getSliderId());
        }
        return farg;
    }

    public ShareContent ObtainShareContent(long id, String title, String url, String thumb, boolean isVote) {
        ShareContent share = new ShareContent();
        share.setId(id);
        share.setTitle(title);
        share.setThumb(thumb);
        share.setUrl(url);
        share.setVote(isVote);
        return share;
    }

    public void checkUrlValild(ShareContent share) {
        if (share.getUrl() == null || TextUtils.isEmpty(share.getUrl()) ||
                !share.getUrl().startsWith("http")) {
            getWebUrl(share);
        } else {
            gotoWebUrl(share, share.getUrl());
        }
        return;
    }

    protected void getWebUrl(final ShareContent chanel) {
        rx.Observable.just(chanel.getId())
                .observeOn(Schedulers.io())
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long id) {
                        ContentCmsInfoEntry entry = new ContentCmsApi(context).getEnteyFromJson(id);
                        return entry.getUrl();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String updateUrl) {
                        gotoWebUrl(chanel, updateUrl);
                    }
                });
    }

    protected void gotoWebUrl(ShareContent share, String url) {
        if (url == null || TextUtils.isEmpty(url)) {
            Toast.makeText(context, "网页地址无效!!", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle bundles = new Bundle();
        int len = 0;
        String title = share.getTitle();
        if (title != null && !TextUtils.isEmpty(title)) {
            len = title.length() >= 15 ? 15 : title.length();
            title = title.substring(0, len);
        }
        bundles.putSerializable("object", share);
        if (share.isVote) {
            if (App.getInstance().getUser() != null) {
                url += "?token=" + App.getInstance().getUser().getToken() + "&client=android";
            } else {
                url += "?token=&client=android";
            }
            if (LanguageUtil.isTibetanLanguage(context)) {
                url += "&language=tibetan";
            }
//            bundles.putString(BaseAndroidWebFragment.PARAMS_URL, url);
//            bundles.putSerializable("object", share);
//            WhiteTopBarActivity.startAct(context, NewsWebVoteFragment.class.getName(), title, R.drawable.cvideo_share,
//                    bundles);
        } else {
            if (LanguageUtil.isTibetanLanguage(context)) {
                url += "?language=tibetan";
            }
//            bundles.putString(BaseAndroidWebFragment.PARAMS_URL, url);
//            bundles.putString(CommWebFragment.PARAMS_URL, url);
//            WhiteTopBarActivity.startAct(context, NewsWebVoteFragment.class.getName(), title, R.drawable.cvideo_share,
//                    bundles);
        }
        bundles.putString(BaseAndroidWebFragment.PARAMS_URL, url);
        WhiteTopBarActivity.startAct(context, NewsWebVoteFragment.class.getName(), title, R.drawable.cvideo_share,
                bundles);
    }

    /**
     * 针对书记 市长
     *
     * @param columnId
     */
    public void gotoSpecialWebUrl(long columnId, String columnName, String icon) {
        String wenUrl = App.getInstance().getmSession().getBaseMobileWebUrl();
//        String wenUrl = "http://192.168.6.85:8091";
        if (!TextUtils.isEmpty(wenUrl)) {
            wenUrl += "/cms/column/" + columnId;
        }
        ShareContent shareContent = new ShareContent();
        shareContent.setUrl(wenUrl);
        if (App.getInstance().getUser() != null) {
            wenUrl += "?token=" + App.getInstance().getUser().getToken() + "&client=android";
        } else {
            wenUrl += "?token=&client=android";
        }
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(icon))
            shareContent.setThumb(icon);
        shareContent.setTitle(columnName);
        shareContent.setVote(false);
        bundle.putSerializable("object", shareContent);
        bundle.putString(BaseAndroidWebFragment.PARAMS_URL, wenUrl);
        WhiteTopBarActivity.startAct(context, NewsWebVoteFragment.class.getName(), columnName, R.drawable.cvideo_share,
                bundle);
    }

    public void updateData(final ListAdapter adapter, List<ContentCmsEntry> data, final IListRequestNetDataface back) {
        Observable.from(data)
                .subscribeOn(Schedulers.io())
                .map(new Func1<ContentCmsEntry, ContentCmsInfoEntry>() {
                    @Override
                    public ContentCmsInfoEntry call(ContentCmsEntry topicalEntry) {
                        ContentCmsInfoEntry tag = null;
                        if (topicalEntry.getShowType() != 6) {
                            // 广告 链接没有详情页
                            tag = mContentCmsApi.getEnteyFromJson(topicalEntry.getId());
                        } else {
                            tag = new ContentCmsInfoEntry();
                            tag.setTitle(topicalEntry.getTitle());
                            tag.setCreation_time(topicalEntry.getCreation_time());
                            tag.setView_count(topicalEntry.getView_count());
                            tag.setId(topicalEntry.getId());
                            tag.setShowType(topicalEntry.getShowType());
                            tag.setSource(topicalEntry.getSource());
                        }
                        return tag;
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ContentCmsInfoEntry>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        back.onFail(new ApiException(e));
                    }

                    @Override
                    public void onNext(List<ContentCmsInfoEntry> data) {
                        back.onSucces(data);
                    }
                });
    }

    public void clearWebDDataCache() {
        cleareWebDataFile();
        cleareWebimageFile();
    }

    /**
     * 清除过期图片文件
     */
    private void cleareWebimageFile() {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                File[] allFiles = new File(FileUtil.getExternalWebimgCaheDirectory(context)).listFiles();
                if (allFiles != null && allFiles.length > 0) {
                    for (int i = 0; i < allFiles.length; i++) {
                        long cacheEntryAge = System.currentTimeMillis() - allFiles[i].lastModified();
                        if (cacheEntryAge > 1 * 24 * 60 * 60 * 1000) {
                            allFiles[i].delete();
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "Deleting from cache: " + allFiles[i].getPath());
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 清除过期网页数据
     */
    private void cleareWebDataFile() {
        final String dir = context.getFilesDir().getPath() + File.separator + NewsDatailHelper.dir_NAME
                + File.separator;
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                File[] allFiles = new File(dir).listFiles();
                if (allFiles != null && allFiles.length > 0) {
                    for (int i = 0; i < allFiles.length; i++) {
                        long cacheEntryAge = System.currentTimeMillis() - allFiles[i].lastModified();
                        if (cacheEntryAge > 1 * 24 * 60 * 60 * 1000) {
                            allFiles[i].delete();
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "Deleting from cache: " + allFiles[i].getPath());
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 显示  或 隐藏  输入法
     *
     * @param hasFocus
     * @param edt
     */
    public void onFocusChange(boolean hasFocus, final EditText edt) {
        final boolean isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) edt.getContext().getSystemService(context.INPUT_METHOD_SERVICE);
                if (isFocus) {
                    // 显示输入法
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    // 隐藏输入法
                    imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    /**
     * 颜色1回复颜色2：差点迟到的
     *
     * @param replyNickName
     * @param commentNickName
     * @param replyContentStr
     * @return
     */
    public View createChildReplay(String replyNickName, String commentNickName, String replyContentStr) {
        LinearLayout view = new LinearLayout(context);
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        view.setLayoutParams(lp);
        TextView txt = new TextView(context);
        txt.setTextSize(12);
        txt.setMaxLines(2);
        txt.setEllipsize(TextUtils.TruncateAt.END);
        SpannableString ss = null;
        if (replyNickName != null && !TextUtils.isEmpty(commentNickName)) {
            ss = new SpannableString(replyNickName + "回复" + commentNickName + ":" + replyContentStr);
        } else {
            ss = new SpannableString(replyNickName + "评论" + commentNickName + ":" + replyContentStr);
        }
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), replyNickName.length() + 2, replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//        ss.setSpan(new ForegroundColorSpan(Color.BLUE), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new ForegroundColorSpan(Color.BLUE), replyNickName.length() + 2, replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txt.setText(ss);
        view.addView(txt);
        return view;
    }

    /**
     * 颜色1：cdddd
     *
     * @param replyNickName
     * @param replyContentStr
     * @return
     */
    public View createSubReplay(String replyNickName, String replyContentStr) {
        LinearLayout view = new LinearLayout(context);
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        view.setLayoutParams(lp);
        TextView txt = new TextView(context);
        txt.setTextSize(16);
        txt.setMaxLines(2);
        txt.setEllipsize(TextUtils.TruncateAt.END);
        SpannableString ss = null;
        if (!(replyNickName == null || TextUtils.isEmpty(replyNickName))) {
            ss = new SpannableString(replyNickName + ":" + replyContentStr);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
//        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), replyNickName.length() + 2, replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//        ss.setSpan(new ForegroundColorSpan(Color.BLUE), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new ForegroundColorSpan(Color.BLUE), replyNickName.length() + 2, replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txt.setText(ss);
        view.addView(txt);
        return view;
    }

    /**
     * 分享
     */
    public void shareNewUiWnd(View rootView, final ShareContent content) {
//                ShareContent content = new ShareContent();
//        content.title = title;
//        content.thumb = thumb;
//        content.type = ShareContent.UrlType.WebPage;
//        content.url = App.getInstance().getContentShareUrl() + content.getId();
        if (shareNewPopupwindow == null) {
            shareNewPopupwindow = new LiveServiceSharePopupwindow(context);
            shareNewPopupwindow.setTag(content);
            shareNewPopupwindow.setOnShareItemClickListener(new LiveServiceSharePopupwindow.OnShareItemClickListener() {
                @Override
                public void onShareItemClick(SharePlatform platform) {
                    if (platform == platform.QQ) {
                        onSharePlatfrom(SharePlatform.QQ, content);
                    } else if (platform == platform.WeiBo) {
                        onSharePlatfrom(SharePlatform.WeiBo, content);
                    } else if (platform == platform.Wechat) {
                        onSharePlatfrom(SharePlatform.Wechat, content);
                    } else if (platform == platform.Wechat_FRIENDS) {
                        onSharePlatfrom(SharePlatform.Wechat_FRIENDS, content);
                    }
                }
            });
        }
        shareNewPopupwindow.show(rootView);
    }

    /**
     * 底部分享 UI
     *
     * @param rootView
     * @param content
     */
    public void shareBottomUiWnd(View rootView, final ShareContent content, Activity activity) {
        if (shareBottomPopupwindow == null) {
            shareBottomPopupwindow = new SharePopupwindow(activity);
            shareBottomPopupwindow.setOnShareClickListener(new SharePopupwindow.OnShareClickListener() {
                @Override
                public void onShareClick(View v) {
                    int vId = v.getId();
                    if (vId == com.dfsx.lzcms.liveroom.R.id.share_qq) {
                        onSharePlatfrom(SharePlatform.QQ, content);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wb) {
                        onSharePlatfrom(SharePlatform.WeiBo, content);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wx) {
                        onSharePlatfrom(SharePlatform.Wechat, content);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wxfriends) {
                        onSharePlatfrom(SharePlatform.Wechat_FRIENDS, content);
                    }
                }
            });
        }
        shareBottomPopupwindow.show(rootView);
    }

    public void onSharePlatfrom(SharePlatform platform, ShareContent content) {
        content.type = ShareContent.UrlType.WebPage;
        if (TextUtils.isEmpty(content.url)){
            content.url = App.getInstance().getContentShareUrl() + content.getId();
        }
//        if (!TextUtils.isEmpty(content.language))
//            content.url = App.getInstance().getContentShareUrl() + content.getId() + "alnguage=" + content.language;
        if (LanguageUtil.isTibetanLanguage(context)) {
            content.url = content.url + "?language=tibetan";
        }
        AbsShare share = ShareFactory.createShare(context, platform);
        share.share(content);
    }

    /**
     * @param adsEntry
     * @return
     */
    public ContentCmsEntry covertToContent(AdsEntry adsEntry) {
        ContentCmsEntry cms = null;
        if (adsEntry != null) {
            cms = new ContentCmsEntry();
            cms.setType("adtype");
            cms.setId(adsEntry.getId());
            int listmode = adsEntry.getList_mode();
            switch (listmode) {
                case 0:
                    cms.setShowType(1);
                    break;
                case 1:
                    cms.setShowType(4);
                    break;
                case 2:
                    cms.setShowType(2);
                    break;
                case 4:
                    cms.setShowType(6);
                    break;
            }
            cms.setTitle(adsEntry.getName());
            cms.setThumbnail_urls(adsEntry.getShowpicturesList());
            if (!(adsEntry.getAdItems() == null ||
                    adsEntry.getAdItems().isEmpty())) {
                AdsEntry.AdItem adItem = adsEntry.getAdItems().get(0);
                if (adItem != null) {
                    cms.setPoster_url(adItem.getLink_url());
                    cms.setShow_id(adItem.getId());
//                            if (adsEntry.getList_mode() != 1) {
//                                List<String> list = new ArrayList<>();
//                                if (!TextUtils.isEmpty(adItem.getPicture_url())) {
//                                    list.add(adItem.getPicture_url());
//                                    cms.setThumbnail_urls(list);
//                                }
//                            }
                    cms.setColumn_id(adItem.getType());
                    if (adItem.getType() == 1) {
                        if (adItem.getVideoAdItem() != null) {
                            if (adItem.getVideoAdItem().getVersions() != null) {
                                cms.setUrl(adItem.getVideoAdItem().getVersions().getUrl());
                            }
                        }
                    } else {
                        cms.setUrl(adItem.getLink_url());
                    }
                }
            }
        }
        return cms;
    }


}
