package com.dfsx.ganzcms.app.act;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.ImageUtil;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.logonproject.act.LoginActivity;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.business.NewsStore;
import com.dfsx.ganzcms.app.fragment.CommentFragment;
import com.dfsx.ganzcms.app.model.ContentCmsInfoEntry;
import com.dfsx.ganzcms.app.model.ImageNewsChannel;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.openimage.GestureImageView;
import com.dfsx.thirdloginandshare.share.*;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by heyang on 2016/9/16.
 */
public class ActNewsImageDetails extends BaseActivity implements EditChangedLister.EditeTextStatuimpl {

    public static final String INTENT_DATA_NEWS_ID = "ActNewsImageDetails_intent_data_news_id";
    private static final String KEY_IMG = "ActNewsImageDetails_key_img";
    public static final String KEY_TEXT = "ActNewsImageDetails_key_text";
    private Context context;
    private ViewPager imagePager;
    private View topBack;
    private View comendView;
    private View commnedPubView;
    private TextView commitText;
    private TextView commitNum;
    private ImageView cimmitImg;
    private ImageView storeImg;
    private ImageView shareImg;
    private ImageView downLoadImg;
    private long newsId;
    private DataFileCacheManager<ContentCmsInfoEntry>
            fileCacheManager;
    private XmlPullParser parser;
    private NewsDatailHelper helper;
    PopupWindow mMorePopupWindow;
    EditText mReplyContent;
    ImageButton mBtnSend;
    ContentCmsApi mContentCmApi;
    long id = -1;
    SharePopupwindow sharePopupwindow;
    View rooView;
    private CustomeProgressDialog mLoading;
    private ImagePagerAdapter adapter;
    private View mViewtopm, mViewbottom;
    private ContentCmsInfoEntry mCotentInfoeny;
    long commnedNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        rooView = getLayoutInflater().inflate(R.layout.act_news_img, null);
//        setContentView(R.layout.act_news_img);
        setContentView(rooView);
        initView();
        doIntent();
        initAction();
        getData();
    }

    private void doIntent() {
        initDataRequest();

//        List<String> uls = (List<String>) getIntent().getSerializableExtra("uls");
        id = getIntent().getLongExtra("index", -1);
        String title = getIntent().getStringExtra("title");
        commnedNumber = getIntent().getLongExtra("comnuber", 0);
        commitNum.setText("" + commnedNumber);
        //    initData(uls, title);
    }

    public void initData(ArrayList<String> uls, String tile) {
        Observable.just(id)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long id) {
                        boolean flag = mContentCmApi.isFav(id);
//                            topicalEntry.setIsFav(flag);
                        return flag;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Boolean a) {
                        setFavStatus(a, false);
                    }
                });

        if (uls != null && !uls.isEmpty()) {
            ArrayList<ImageNewsChannel> dlist = new ArrayList<>();
            for (String url : uls) {
                ImageNewsChannel chenl = new ImageNewsChannel(url, tile);
                dlist.add(chenl);
            }
            adapter = new ImagePagerAdapter(dlist);
            imagePager.setAdapter(adapter);
        }
    }

    private void initView() {
        mViewtopm = findViewById(R.id.groups_header_view);
        mViewbottom = findViewById(R.id.groups_bottom_view);
        topBack = findViewById(R.id.top_back_view);
        comendView = findViewById(R.id.commend_view);
        commnedPubView = findViewById(R.id.pub_commend_view);
        imagePager = (ViewPager) findViewById(R.id.img_pager);
        commitText = (TextView) findViewById(R.id.edit_commit);
        commitNum = (TextView) findViewById(R.id.commit_num);
        //       cimmitImg = (ImageView) findViewById(R.id.commit_img);
        storeImg = (ImageView) findViewById(R.id.store_img);
        shareImg = (ImageView) findViewById(R.id.share_img);
        downLoadImg = (ImageView) findViewById(R.id.download_img);
    }

    public void isFullScreen(boolean flag, View view) {
        Animation shake = null;
        if (!flag) {
            shake = AnimationUtils.loadAnimation(this, R.anim.group_view_bottom_out);
            mViewbottom.startAnimation(shake);
            shake = AnimationUtils.loadAnimation(this, R.anim.group_view_top_out);
            mViewtopm.startAnimation(shake);
        } else {
            shake = AnimationUtils.loadAnimation(this, R.anim.group_view_bottom_in);
            mViewbottom.startAnimation(shake);
            shake = AnimationUtils.loadAnimation(this, R.anim.group_view_top_in);
            mViewtopm.startAnimation(shake);
        }
        mViewtopm.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        mViewbottom.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        view.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
    }

    private void initAction() {
        mContentCmApi = new ContentCmsApi(this);
        helper = new NewsDatailHelper(context);
        topBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        parser = Xml.newPullParser();
        commnedPubView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMore(v, -1);
            }
        });

        comendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commnedNumber == 0) {
                    ToastUtils.toastMsgFunction(v.getContext(), "暂无评论");
                    return;
                }
                WhiteTopBarActivity.startAct(ActNewsImageDetails.this, CommentFragment.class.getName(),
                        "所有评论", "", id);
            }
        });

        storeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                store();
                if (mCotentInfoeny != null) {
                    pubFavirty(!mCotentInfoeny.isFav());
                }
            }
        });
        shareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareClick();
            }
        });
        downLoadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadIamge();
            }
        });
    }

    protected void shareClick() {
        if (sharePopupwindow == null) {
            sharePopupwindow = new SharePopupwindow(this);
            sharePopupwindow.setOnShareClickListener(new SharePopupwindow.OnShareClickListener() {
                @Override
                public void onShareClick(View v) {
                    int vId = v.getId();
                    if (vId == com.dfsx.lzcms.liveroom.R.id.share_qq) {
                        onSharePlatfrom(SharePlatform.QQ);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wb) {
                        onSharePlatfrom(SharePlatform.WeiBo);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wx) {
                        onSharePlatfrom(SharePlatform.Wechat);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wxfriends) {
                        onSharePlatfrom(SharePlatform.Wechat_FRIENDS);
                    }
                }
            });
        }
        sharePopupwindow.show(rooView);
    }

    public void onSharePlatfrom(SharePlatform platform) {
        if (mCotentInfoeny == null) return;
        ShareContent content = new ShareContent();
        content.title = mCotentInfoeny.getTitle();
        content.disc = mCotentInfoeny.getSummary();
        if (mCotentInfoeny.getThumbnail_urls() != null && mCotentInfoeny.getThumbnail_urls().size() > 0)
            content.thumb = mCotentInfoeny.getThumbnail_urls().get(0);
        content.type = ShareContent.UrlType.WebPage;
        content.url = App.getInstance().getContentShareUrl() + mCotentInfoeny.getId();
        AbsShare share = ShareFactory.createShare(ActNewsImageDetails.this, platform);
        share.share(content);
    }

    public boolean isUserNull() {
        boolean isLogo = false;
        if (App.getInstance().getUser() == null) {
            isLogo = true;
            goToLogin();
        }
        return isLogo;
    }


    private void showMore(View moreBtnView, long resf_cid) {
        if (mMorePopupWindow == null) {
            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = li.inflate(R.layout.layout_more, null, false);
            mMorePopupWindow = new PopupWindow(content, MATCH_PARENT,
                    WRAP_CONTENT);
            mMorePopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mMorePopupWindow.setFocusable(true);
//            mMorePopupWindow.setAnimationStyle(R.style.LiveVideoPopupStyle);
            mMorePopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
            mMorePopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            //     mMorePopupWindow.setOutsideTouchable(true);
            //      mMorePopupWindow.setTouchable(true);
            //    content.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            //     mShowMorePopupWindowWidth = UtilHelp.getScreenWidth(this);
            //      mShowMorePopupWindowHeight = content.getMeasuredHeight();
            View parent = mMorePopupWindow.getContentView();
            mReplyContent = (EditText) parent.findViewById(R.id.commentEdit_replay_edt);
            mReplyContent.addTextChangedListener(new EditChangedLister(this));
            mBtnSend = (ImageButton) parent.findViewById(R.id.commentButton);
//            mReplyContent.addTextChangedListener(new EditChangedLister(this));
            mBtnSend.setTag(resf_cid);
            mBtnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isUserNull()) return;
                    String contnt = mReplyContent.getText().toString();
                    long refId = -1;
                    if (view.getTag() != null) refId = (long) view.getTag();
                    mContentCmApi.createCmsCommend(id, refId, contnt, new DataRequest.DataCallback() {
                        @Override
                        public void onSuccess(boolean isAppend, Object data) {
                            Toast.makeText(ActNewsImageDetails.this, "发表评论成功", Toast.LENGTH_SHORT).show();
                            mMorePopupWindow.dismiss();
                            UtilHelp.onFocusChange(false, mReplyContent);
                        }

                        @Override
                        public void onFail(ApiException e) {
                            Toast.makeText(ActNewsImageDetails.this, "发表评论失败:" + e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });
                }
            });
        }
        if (mMorePopupWindow.isShowing()) {
            mMorePopupWindow.dismiss();
        } else {
            mMorePopupWindow.showAtLocation(moreBtnView, Gravity.BOTTOM,
                    0, 0);
            mReplyContent.setText("");
            UtilHelp.onFocusChange(true, mReplyContent);
        }
    }

    public void setFavStatus(boolean flag, boolean isShowMsg) {
        String msg = "收藏成功";
        if (flag) {
            storeImg.setImageDrawable(getResources().getDrawable(R.drawable.img_group_detail_fav_select));
        } else {
            msg = "取消收藏成功";
            storeImg.setImageDrawable(getResources().getDrawable(R.drawable.img_group_detail_fav));
        }
        if (isShowMsg)
            Toast.makeText(ActNewsImageDetails.this, msg, Toast.LENGTH_SHORT).show();
    }


    private String saveImageUrl;
    private String saveFilePath;

    private void downLoadIamge() {
        if (adapter != null && adapter.getData() != null) {
            int pos = imagePager.getCurrentItem();
            if (pos >= 0 && pos < adapter.getData().size()) {
                saveImageUrl = adapter.getData().get(pos).getImg();
                if (!TextUtils.isEmpty(saveImageUrl)) {

                    String fileName = saveImageUrl.substring(saveImageUrl.lastIndexOf("/") + 1,
                            saveImageUrl.length());
                    Log.e("TAG", "fileName == " + fileName);
                    String dir = Environment.getExternalStorageDirectory().getPath() + "/" + "honghe";
                    File dirFile = new File(dir);
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }
                    saveFilePath = dir + "/download/" + fileName;
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... params) {
                            Looper.prepare();
                            try {
                                Bitmap saveTempBitmap = Glide.
                                        with(context).
                                        load(saveImageUrl).
                                        asBitmap().
                                        into(-1, -1).
                                        get();
                                ImageUtil.saveBitmapToFile(saveTempBitmap, saveFilePath);
                                return true;
                            } catch (final ExecutionException e) {
                                e.printStackTrace();
                            } catch (final InterruptedException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }

                        @Override
                        protected void onPostExecute(Boolean isSuccess) {
                            Toast.makeText(context, "图片保存到 " + saveFilePath, Toast.LENGTH_SHORT).show();
                        }
                    }.execute();

                }
            }
        }
    }

    private void store() {
        if (isUserNull()) return;
        new NewsStore(context).store(newsId, new NewsStore.StoreListener() {
            @Override
            public void hasStored() {
                storeImg.setImageResource(R.drawable.icon_store_red);
            }

            @Override
            public void storeFail(ApiException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void storeSuccess() {
                storeImg.setImageResource(R.drawable.icon_store_red);
            }
        });
    }

    public void pubFavirty(final boolean flag) {
        if (isUserNull()) return;
        mContentCmApi.farityToptic(id, flag, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                if ((boolean) data) {
                    mCotentInfoeny.setIsFav(flag);
                    setFavStatus(flag, true);
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                Toast.makeText(ActNewsImageDetails.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void share() {
        if (adapter != null &&
                adapter.getData() != null
                && adapter.getCount() > 0) {
            ImageNewsChannel channel = adapter.getData().get(0);
            ShareContent info = new ShareContent();
//            info.title = context.getResources().getString(R.string.share_default_title);
            info.title = "分享";
            info.type = ShareContent.UrlType.WebPage;
            String server = App.getInstance().getBaseUrl();
            server += "/?notitle/#/news/" + newsId + "";
            info.thumb = channel.getImg();
            info.url = server;
            info.disc = channel.getTitle();
            ShareUtil.share(context, info);
        }
    }

    private void goCommit() {
        Toast.makeText(context, "评论暂未开启", Toast.LENGTH_SHORT).show();
//        CommentsActivity.start(context, (int) newsId);
    }

    private void goToLogin() {
        if (ActNewsImageDetails.this != null) {
            AlertDialog adig = new AlertDialog.Builder(ActNewsImageDetails.this).setTitle("提示").setMessage("未登录，是否现在登录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent intent = new Intent();
                    intent.setClass(ActNewsImageDetails.this, LoginActivity.class);
                    startActivity(intent);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            }).create();
            adig.show();
        }
    }

    private void initDataRequest() {
        fileCacheManager = new DataFileCacheManager<ContentCmsInfoEntry>
                (App.getInstance().
                        getApplicationContext(),
                        "all", App.getInstance().getPackageName() + "ActNews" + id + ".txt") {
            @Override
            public ContentCmsInfoEntry jsonToBean(JSONObject json) {
                ContentCmsInfoEntry entry = null;
                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    if (json != null) {
                        entry = new Gson().fromJson(json.toString(), ContentCmsInfoEntry.class);
//                        String exts = json.optString("extension");
//                        JSONObject exObj = null;
//                        try {
//                            exObj = JsonCreater.jsonParseString(exts);
//                            if (exObj != null) {
//                                JSONArray arr = exObj.optJSONArray("pictures");
//                                if (arr != null && arr.length() > 0) {
//                                    List<ContentCmsInfoEntry.GroupImgsBean> list = new ArrayList<>();
//                                    for (int i = 0; i < arr.length(); i++) {
//                                        JSONObject itme = (JSONObject) arr.get(i);
//                                        ContentCmsInfoEntry.GroupImgsBean bean = new ContentCmsInfoEntry.GroupImgsBean();
//                                        bean.setTitle(itme.optString("introduction"));
//                                        bean.setUrl(itme.optString("url"));
//                                        list.add(bean);
//                                    }
//                                    entry.setGroupimgs(list);
//                                }
//                            }
//                        } catch (ApiException e) {
//                            e.printStackTrace();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
                return entry;
            }
        };
    }

    private void getCommitNum() {
        JSONObject obj = UtilHelp.getJsonObject("", "");
        try {
            obj.put("type", "node");
            obj.put("id", newsId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        helper.postCommitThumbNumber(obj, new DataRequest.DataCallback<Integer>() {
            @Override
            public void onSuccess(boolean isAppend, Integer data) {
                if (data <= 0) {
                    commitNum.setVisibility(View.GONE);
                } else {
                    commitNum.setText(data + "");
                    commitNum.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(ApiException e) {
                Log.e("TAG", "postCommitThumbNumber fail");
            }
        });
    }

    private void praseStringData(ArrayList<ImageNewsChannel> list,
                                 String data, String title) {
        ImageNewsChannel channel = new ImageNewsChannel();
        channel.setId(1);
        channel.setTitle(title);
        String xmlHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
        StringReader reader = new StringReader(xmlHeader + data);
        if (parser != null) {
            try {
                parser.setInput(reader);
                HashMap<String, String> map = parseXMLAndStoreIt(parser);
                channel.setImg(map.get(KEY_IMG));
                channel.setText(map.get(KEY_TEXT));
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        if (list != null) {
            list.add(channel);
        }
    }

    public HashMap<String, String> parseXMLAndStoreIt(XmlPullParser myParser) {
        HashMap<String, String> map = new HashMap<>();
        int event;
        String text = "";
        String content = "";
        String imgPath = "";
//        ArrayList<String>  alist=null;
//        alist=new ArrayList<String>();
        try {
            myParser.nextTag();
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        String name = myParser.getName();
                        if (myParser.getName().equals("div")) {
                            String temperature = myParser.getAttributeValue(null, "class");
                        }
                        break;

                    case XmlPullParser.START_TAG:
                        name = myParser.getName();
//                        classtag = myParser.getAttributeValue(null, "class");
                        if (name.equals("img")) {
                            imgPath = myParser.getAttributeValue(null, "src");
//                            alive.thumb = text;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText().toString().trim();
                        if (!("\n").equals(text)) {
                            content = text;
                        }
                        break;

                    case XmlPullParser.END_TAG:
//                        name = myParser.getName();
//                        if (name.equals("country")) {
//                            String country = text;
//                        }
                        break;
                }
                event = myParser.next();
                if ((imgPath.endsWith(".jpg") || imgPath.endsWith(".png"))
                        && !("").equals(content)) {
                    map.put(KEY_IMG, imgPath);
                    map.put(KEY_TEXT, content);
                    return map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(imgPath)) {
            map.put(KEY_IMG, imgPath);
        } else if (!TextUtils.isEmpty(content)) {
            map.put(KEY_TEXT, content);
        }
        return map;
    }

    private void getData() {
        if (context != null && !this.isFinishing()) {
            mLoading = CustomeProgressDialog.show(context, "加载中...");
        }
        String url = App.getInstance().getmSession().getContentcmsServerUrl() +
                "/public/contents/" + id;
        fileCacheManager.getData(new DataRequest.HttpParamsBuilder().setRequestType(DataReuqestType.GET)
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), false).setCallback(callback);

        //    getCommitNum();
    }

    private DataRequest.DataCallback<ContentCmsInfoEntry> callback =
            new DataRequest.DataCallback<ContentCmsInfoEntry>() {
                @Override
                public void onSuccess(boolean isAppend, ContentCmsInfoEntry data) {
                    Observable.just(data)
                            .subscribeOn(Schedulers.io())
                            .map(new Func1<ContentCmsInfoEntry, ContentCmsInfoEntry>() {
                                @Override
                                public ContentCmsInfoEntry call(ContentCmsInfoEntry topicalEntry) {
                                    topicalEntry = mContentCmApi.getEnteyFromJson(topicalEntry.getId());
                                    boolean flag = mContentCmApi.isFav(topicalEntry.getId());
                                    topicalEntry.setIsFav(flag);
                                    return topicalEntry;
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<ContentCmsInfoEntry>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    if (mLoading != null) {
                                        mLoading.dismiss();
                                    }
                                }

                                @Override
                                public void onNext(ContentCmsInfoEntry data) {
                                    mCotentInfoeny = data;
                                    ArrayList<ImageNewsChannel> list = new ArrayList<>();
                                    if (data.getGroupimgs() != null && !data.getGroupimgs().isEmpty()) {
                                        for (ContentCmsInfoEntry.GroupImgsBean bean : data.getGroupimgs()) {
                                            ImageNewsChannel chanle = new ImageNewsChannel(bean.getUrl(), bean.getIntroduction());
                                            list.add(chanle);
                                        }
                                    }
                                    setFavStatus(mCotentInfoeny.isFav(), false);
                                    if (adapter == null) {
                                        adapter = new ImagePagerAdapter(list);
                                        imagePager.setAdapter(adapter);
                                    } else {
                                        adapter.update(list);
                                    }
                                    if (mLoading != null) {
                                        mLoading.dismiss();
                                    }
                                }
                            });
                }

                @Override
                public void onFail(ApiException e) {
                    e.printStackTrace();
                    if (mLoading != null) {
                        mLoading.dismiss();
                    }
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    @Override
    public void onTextStatusCHanged(boolean isHas) {
        if (isHas) {
            mBtnSend.setImageDrawable(getResources().getDrawable(R.drawable.video_send_select));
        } else {
            mBtnSend.setImageDrawable(getResources().getDrawable(R.drawable.video_send_normal));
        }
    }

    class ImagePagerAdapter extends PagerAdapter {

        private ArrayList<ImageNewsChannel> showList;

        public ImagePagerAdapter(ArrayList<ImageNewsChannel> list) {
            showList = list;
        }

        public void update(ArrayList<ImageNewsChannel> data) {
            showList = data;
            notifyDataSetChanged();
        }

        public ArrayList<ImageNewsChannel> getData() {
            return showList;
        }

        private void setViewData(final View v, int position) {
            if (showList != null && position >= 0 && position < showList.size()) {
                ImageNewsChannel channel = showList.get(position);
                ImageView imageView = (GestureImageView) v.findViewById(R.id.item_img);
                TextView posText = (TextView) v.findViewById(R.id.item_pos_text);
                TextView infoText = (TextView) v.findViewById(R.id.item_info_text);
                ProgressBar loading = (ProgressBar) v.findViewById(R.id.loading);

//                UtilHelp.LoadImageFormUrl(imageView, channel.getImg(), loading);
                UtilHelp.LoadImageErrorUrl(imageView, channel.getImg(), loading, R.drawable.img_group_detail_bankground);
//                GlideImgManager.getInstance().showImg(context, imageView, channel.getImg());
                posText.setText((position + 1) + "/" + getCount());
                String info = TextUtils.isEmpty(channel.getText()) ? "" : channel.getText();
                infoText.setText(info);
                imageView.setTag(true);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View tag = null;
                        if (view != null) {
                            tag = ((RelativeLayout) view.getParent()).findViewById(R.id.group_dscribr_view);
                        }
                        boolean isfull = (boolean) view.getTag();
                        isFullScreen(!isfull, tag);
                        view.setTag(!isfull);
                    }
                });
            }
        }

        @Override
        public int getCount() {
            return showList == null ? 0 : showList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_img_pager, null);
            setViewData(v, position);
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
