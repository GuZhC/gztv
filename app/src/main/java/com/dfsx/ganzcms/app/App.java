package com.dfsx.ganzcms.app;

import android.content.ContentResolver;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.dfsx.core.AppApi;
import com.dfsx.core.AppApiManager;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.UserAgreementManger;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.GetTokenManager;
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.ganzcms.app.push.MessagePushManager;
import com.dfsx.ganzcms.app.push.NotificationMessageStartManager;
import com.dfsx.ganzcms.app.push.OnMessageClickEvent;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Extension;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Message;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.logonproject.busniness.TokenHelper;
import com.dfsx.lzcms.liveroom.business.AppManager;
import com.dfsx.lzcms.liveroom.view.LiveUserAgreement;
import com.dfsx.push.aliyunpush.AliyunPushManager;
import com.dfsx.push.aliyunpush.OnAliyunPushReceiveListener;
import com.dfsx.searchlibaray.AppSearchManager;
import com.dfsx.statistics.StatisticUtils;
import com.google.gson.Gson;
import com.lody.turbodex.TurboDex;
import com.loveplusplus.update.DownLoadDatamanager;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.Map;
import java.util.TimeZone;

/**
 * Created by liuwb on 2016/7/8.
 */
public class App extends CoreApp implements AppApi, OnAliyunPushReceiveListener {
    private static App mApplication = null;
    private Account account;
    ContentCmsApi mContentCmsApi = null;
    private boolean isDisclureCompalte = true;  //  爆料发布是否完成
    private boolean isDisclureIsOk = true;  //  爆料发布是否成功
    private OnMessageClickEvent onMessageClickEvent;

    public static App getInstance() {
        return mApplication;
    }

    public AppSession getmSession() {
        return mSession;
    }

    public void setmSession(AppSession mSession) {
        this.mSession = mSession;
    }

    private AppSession mSession;

    public String getCommuityShareUrl() {
        String shareUlr = mSession.getBaseMobileWebUrl();
        if (shareUlr != null || !TextUtils.isEmpty(shareUlr)) {
            shareUlr += "/community/thread/";
            commuityShareUrl = shareUlr;
        }
        return commuityShareUrl;
    }

    //    public String commuityShareUrl = "http://m.dfsxcms.cn/sns/node/";   //  圈子分享地址
    //    public String commuityShareUrl = "http://m.leshantv.net/sns/node/";   //  圈子分享地址
    public String commuityShareUrl = "http://m.baview.cn:8011/community/thread/";   //  圈子分享地址

    public String getContentShareUrl() {
        String shareUlr = mSession.getBaseMobileWebUrl();
        if (shareUlr != null || !TextUtils.isEmpty(shareUlr)) {
            shareUlr += "/cms/content/";
            contentShareUrl = shareUlr;
        }
        return contentShareUrl;
    }

    //    public String contentShareUrl = "http://m.dfsxcms.cn/portal/node/";   //  首页分享地址
    //    public String contentShareUrl = "http://m.leshantv.net/portal/node/";   //  首页分享地址  2017-3-27 新修改地址
    public String contentShareUrl = "http://m.baview.cn:8011/cms/content/";   //  首页分享地址  2017-3-27 新修改地址

    //图片上传地址
    String pictureUrl = "";

    public String getLocalIp() {
        return localIp;
    }

    String localIp = "";
    private boolean isUpFileFlag = true;   //s是否继续上传文件标致

    @Override
    protected void attachBaseContext(Context base) {
        TurboDex.enableTurboDex();
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        StatisticUtils.initAndStart(this);
        StatisticUtils.onUserIPStatistic(this);
        GetTokenManager.getInstance().setIGetToken(new TokenHelper());
        mSession = new AppSession(getApplicationContext());
//        mSession.init();
        AppApiManager.getInstance().setAppApi(this);
        AppManager.getInstance().setIApp(new LiveRoomAppImp());
        AppSearchManager.getInstance().setSearchConfig(new SearchConfig());
//        GetTokenManager.getInstance().setIGetToken(new TokenHelper());
        localIp = UtilHelp.getHostIP();
        //初始化
        initCloudChannel(this);
//        CrashHandler.getInstance().init(getApplicationContext());
        initUserAgreement();
        isTimeZone();
        IGetPriseManager.getInstance().init();
        LocationManager.getInstance().init();
    }

    /**
     * 判断是不是24小时制
     */
    public void isTimeZone() {
        ContentResolver cv = this.getContentResolver();
        // 获取当前系统设置
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        //        if (!strTimeFormat.equals("24")) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        //        }
    }

    /**
     * 初始化直播协议的数据
     */
    private void initUserAgreement() {
        UserAgreementManger.getInstance().setUserAgreementWindow(new LiveUserAgreement());
    }

    /**
     * 初始化云推送通道,设置推送消息监听
     *
     * @param
     */
    private void initCloudChannel(Context applicationContext) {
        AliyunPushManager.getInstance().initAndRegister(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                //将DeviceId存储起来
                String deviceId = AliyunPushManager.getInstance().getDeviceId();
                setDeviceId(deviceId);
                if (getUser() != null && getUser().getUser() != null) {
                    PushApi.submitDeviceId(App.getInstance(), deviceId, callback);
                }
                Log.d("AliyunPush", "init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.d("AliyunPush", "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);

            }
        });
        AliyunPushManager.getInstance().regsiterThirdPush(applicationContext);
        //设置推送消息监听
        AliyunPushManager.getInstance().setListener(this);
    }

    DataRequest.DataCallback<Void> callback = new DataRequest.DataCallback<Void>() {
        @Override
        public void onSuccess(boolean isAppend, Void data) {
            Log.e("AliyunPush", "更新推送设备成功");
        }

        @Override
        public void onFail(ApiException e) {
            Log.e("AliyunPush", "更新推送设备失败");
        }
    };

    public String getPitureUpFileUrl() {
        return pictureUrl;
    }

    public String getBaseUrl() {
        return mSession.getBaseUrl();
    }

    public Account getUser() {
        return super.getUser();
        //heyang 2018/2/1 处理UserBean为空的情况
//        Account user = super.getUser();
//        if (user == null) {
//            return user;
//        } else {
//            Account.UserBean bean = user.getUser();
//            if (bean == null)
//                user = null;
//        }
//        return user;
    }

    public boolean checkIsVerity() {
//        boolean flag = false;
//        if (getUser() != null) {
//            if (getUser().getUser().is_verified())
//                flag = true;
//        }
//        if (!flag)
//            IntentUtil.gotoCheckteleVerify(CoreApp.getInstance().getApplicationContext());
//        return flag;
        return  true;
    }

    @Override
    public boolean downloadAndUpdateApp() {
        String checkUrl = getPotrtServerUrl() + "/public/apps";
        Observable.just(checkUrl)
                .observeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String url) {
                        String res = HttpUtil.executeGet(url, new HttpParameters(), null);
                        try {
                            JSONObject json = new JSONObject(res);
                            String apkUrl = json.optString("android_download_url");
                            return apkUrl;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
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
                        DownLoadDatamanager.getInstance(App.getInstance()).
                                setUrl(updateUrl);
                        DownLoadDatamanager.
                                getInstance(App.getInstance()).download();
                    }
                });
        return true;
    }

    public boolean isLogin() {
        return getUser() != null && getUser().getUser() != null;
    }

    public boolean isUpFileFlag() {
        return isUpFileFlag;
    }

    public void setUpFileFlag(boolean upFileFlag) {
        isUpFileFlag = upFileFlag;
    }

    public String getCurrentToken() {
        return getUser() == null ? null : getUser().getToken();
    }

    @Override
    public String getSession() {
        return getUser() == null ? null : getUser().sessionId;
    }

    @Override
    public String getSessionName() {
        return getUser() == null ? null : getUser().sessionName;
    }

    @Override
    public String getBaseServerUrl() {
        return mSession.getPortalServerUrl();
    }

    @Override
    public String getShopServerUrl() {
        return mSession.getShopServerUrl();
    }

    @Override
    public void onMessage(Context context, CPushMessage message) {
        Log.e("TAG", "onMessage--------------");
    }

    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        Log.e("TAG", "onNotification--------------summary === " + summary);
        //目前只处理通知
        MessagePushManager.getInstance().onNoticeReceive(title, summary, extraMap);
    }

    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.e("TAG", "onNotificationOpened--------------extraMap === " + extraMap);
        String json = null;
        if (!TextUtils.isEmpty(extraMap)) {
            try {
                JSONObject jsonObject = new JSONObject(extraMap);
                json = jsonObject.optString("info");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("TAG", "推送的信息规则错误");
            }
        }

        if (json == null) {
            Log.e("TAG", "推送的map中不含key 'info'");
            return;
        }
        Gson gson = new Gson();
        Extension extension = gson.fromJson(json, Extension.class);
        extension.setMessageType(extension.getMessageType());
        if (extension != null) {
            Message message = new Message();
            message.setExt(extension);

            NotificationMessageStartManager.
                    getInstance().startApp(message);

            //            if (onMessageClickEvent == null) {
            //                onMessageClickEvent = new OnMessageClickEvent(context,
            //                        new IPushMessageClickEvent.DefaultPushMessageClickEvent());
            //            }
            //            onMessageClickEvent.onMessageClick(message);
        }
    }

    @Override
    public void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.e("TAG", "onNotificationClickedWithNoAction--------------summary == " + summary);
    }

    @Override
    public void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.e("TAG", "onNotificationReceivedInApp-------------- summary " + summary);
    }

    @Override
    public void onNotificationRemoved(Context context, String messageId) {
        Log.e("TAG", "onNotificationRemoved--------------messageId == " + messageId);
    }

    public boolean isDisclureCompalte() {
        return isDisclureCompalte;
    }

    public void setDisclureCompalte(boolean disclureCompalte) {
        isDisclureCompalte = disclureCompalte;
    }

    public boolean isDisclureIsOk() {
        return isDisclureIsOk;
    }

    public void setDisclureIsOk(boolean disclureIsOk) {
        isDisclureIsOk = disclureIsOk;
    }
}
