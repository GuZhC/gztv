package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.act.ApiVersionErrorActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.ganzcms.app.App;
import com.dfsx.logonproject.busniness.TokenHelper;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * heyang  2016-8-18
 */
public class AppSession {
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int SO_TIMEOUT = 5000;
    private static final int RETRY_TIME = 5;
    private String mServerHost = "";
    private int mServerPort = 0;
    private String mBaseUrl = "";
    boolean mIsLogin = false;
    boolean mSaveUser = false;
    //    ArrayList<Integer> mReadComplatelist;
    Context mContext;

    private boolean isForceUpdateApp = false;

    /**
     * 当前APP设置支持的API的版本
     */
    private static final int CURRENT_APP_API_VERSION = 2;
    /**
     * 控制使用内网测试，还是使用外网的域名
     */
    private static final boolean isTestHost = false;
    /**
     * 内网测试的host
     */
    //    private static final String TEST_HOST = "http://api.baview.cn:8011";
    /**
     * 外网服务器的host
     */
//    private static final String LESHAN_HOST = "http://api.dzxw.net";

//    private static final String LESHAN_HOST = "http://110.190.77.38:7001/api";

    private static final String LESHAN_HOST = "http://api.ganzitv.com";

//    private static final String LESHAN_HOST = "http://api.dzxw.net";


    //测试积分商城
//    private static final String TEST_HOST = "http://api.baview.cn:8011";

    private static final String TEST_HOST = "http://192.168.6.30:8001";

//    private static final String LESHAN_HOST = "http://api.leshantv.net";

    //    private static final String LESHAN_HOST = "http://api.bntv.cn:7001";

    //版纳
    //    private static final String LESHAN_HOST = "http://112.116.48.194:7001/api";

    private String PortalServerUrl = "http://api.ganzitv.com";   //门户登陆地址
    private String LiveServerUrl = "http://api.ganzitv.com/live";     //直播地址
    private String CommunityServerUrl = "http://api.ganzitv.com/community";   //社区地址
    private String ContentcmsServerUrl = "http://api.ganzitv.com/cms";   //内容发布
    //    private String shopServerUrl = "http://192.168.6.117:8601";   //  积分商城
    private String shopServerUrl = "http://api.dsyuncloud.com/shop";   //  积分商城
    private String adServerUrl = "http://api.ds.yatv.tv/ad";   //   广告

    private String baseShareLiveUrl = "http://m.ganzitv.com/live/player/";
    private String baseShareGuessLiveUrl = "http://m.ganzitv.com/live/player-quiz/";
    private String baseShareBackplayUrl = "http://m.ganzitv.com/live/playback/";

    private String baseMobileWebUrl = "http://m.ganzitv.com";

    private String externalLive;

    public String getContentcmsServerUrl() {
        return ContentcmsServerUrl;
    }

    public void setContentcmsServerUrl(String contentcmsServerUrl) {
        ContentcmsServerUrl = contentcmsServerUrl;
    }


    //    public AccountApi getmAccounApi() {
    //        return mAccounApi;
    //    }

    //    public void setmAccounApi(AccountApi mAccounApi) {
    //        this.mAccounApi = mAccounApi;
    //    }

    //    AccountApi mAccounApi = null;

    public AppSession(Context context) {
        mContext = context;
        init();
        //        mReadComplatelist = new ArrayList<Integer>();
    }

    public void init() {
        //        mAccounApi = new AccountApi(mContext);
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            //            mServerHost = sharedPref.getString("pref_url", "119.6.200.160");
            //            mServerPort = Integer.parseInt(sharedPref.getString("pref_port", "8099"));

            //            mServerHost = sharedPref.getString("pref_url", "112.113.230.4");
            //            mServerPort = Integer.parseInt(sharedPref.getString("pref_port", "9080"));

            mServerHost = sharedPref.getString("pref_url", "119.6.245.226");
            mServerPort = Integer.parseInt(sharedPref.getString("pref_port", "8055"));

            setServerUrl(mServerHost, mServerPort);
            //            mBaseUrl = "http://" + mServerHost + (mServerPort != 80 ? ":" + mServerPort : "") + "/API/";
            //@ read user.
            //            SharedPreferences mySharedPreferences = mContext.getSharedPreferences(KeyName.KEY_ACCOUNT_INFO, Activity.MODE_PRIVATE);
            //            if (mySharedPreferences != null) {
            //                mSaveUser = mySharedPreferences.getBoolean(KeyName.KEY_IS_SAVE_ACCOUNT, true);
            //                {
            //                    mAccount = new Account();
            //                    mAccount.loginName = mySharedPreferences.getString(KeyName.KEY_USER_NAME, "");
            //                    mAccount.password = mySharedPreferences.getString(KeyName.KEY_PASSWORD, "");
            //                    mAccount.token = mySharedPreferences.getString(KeyName.KEY_ACCESS_TOKEN, null);
            //                }
            //            }
            String baseUrl = getHost() + "/public/settings";
            getAppModelUrl(baseUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getHost() {
        return isTestHost ? TEST_HOST : LESHAN_HOST;
    }

    public void getAppModelUrl(String url) {
        Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String url) {
                        String respone = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
                        LogUtils.e("http", "url res == " + respone);
                        try {
                            if (!TextUtils.isEmpty(respone.toString().trim())) {
                                JSONObject json = JsonCreater.jsonParseString(respone);
                                int apiVersion = json.optInt("api_version");
                                int minApiVersion = json.optInt("min_bc_api_version");
                                isForceUpdateApp = handleApiVersion(apiVersion, minApiVersion);
                                JSONArray arr = json.optJSONArray("api");
                                JSONObject mWebJson = json.optJSONObject("mweb");
                                String external_live = json.optString("external_live");
                                if (arr != null && arr.length() > 0) {
                                    for (int i = 0; i < arr.length(); i++) {
                                        try {
                                            JSONObject obj = (JSONObject) arr.get(i);
                                            if (obj != null) getChildUrl(obj);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if (mWebJson != null) {//设置手机端显示的网站的域名
                                    String mWebUrl = mWebJson.optString("base_url");
                                    if (!TextUtils.isEmpty(mWebUrl)) {
                                        if (mWebUrl.endsWith("/")) {
                                            mWebUrl = mWebUrl.substring(0, mWebUrl.length() - 1);
                                        }
                                        setBaseMobileWebUrl(mWebUrl);
                                    }
                                }
                                if (!TextUtils.isEmpty(external_live)) {
                                    externalLive = external_live;
                                }
                            }
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                        getTopicColumsCount();
                        return true;
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
                    }
                });
    }

    /**
     * 判断API的版本是否支持当前的APP版本.
     * 不支持这强制升级
     *
     * @param apiVersion
     * @param minApi
     */
    /**
     * 判断API的版本是否支持当前的APP版本.
     * 不支持这强制升级
     *
     * @param apiVersion
     * @param minApi
     */
    public boolean handleApiVersion(final double apiVersion, final double minApi) {
        if (minApi > CURRENT_APP_API_VERSION) {
            Observable.just(null)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Object>() {
                        @Override
                        public void onCompleted() {
                            Intent intent = new Intent(App.getInstance(), ApiVersionErrorActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            App.getInstance().getApplicationContext().startActivity(intent);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Object o) {

                        }
                    });
            return true;
        }
        return false;
    }

    public int getTopicCount() {
        return topicCount;
    }

    private int topicCount = 3;

    public void getTopicColumsCount() {
        String url = getCommunityServerUrl() + "/public/columns";
        String respone = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
        int errorCode = StringUtil.getHttpResponseErrorCode(respone);
        if (errorCode == 401) {
            String newToken = new TokenHelper().getTokenSync();
            respone = HttpUtil.executeGet(url, new HttpParameters(),
                    newToken);
            Account user = App.getInstance().getUser();
            if (user != null) {
                user.setToken(newToken);
                App.getInstance().setCurrentAccount(user);
            }
        }
        try {
            JSONObject json = JsonCreater.jsonParseString(respone);
            if (json != null) {
                JSONArray arr = json.optJSONArray("result");
                if (arr != null && arr.length() > 0)
                    topicCount = arr.length();
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public boolean isForceUpdateApp() {
        return isForceUpdateApp;
    }

    public void getChildUrl(JSONObject obj) {
        String ksy = obj.optString("key");
        String url = obj.optString("base_url");
        if (TextUtils.equals("cms", ksy)) {
            setContentcmsServerUrl(url);
        } else if (TextUtils.equals("live", ksy)) {
            setLiveServerUrl(url);
        } else if (TextUtils.equals("community", ksy)) {
            setCommunityServerUrl(url);
        } else if (TextUtils.equals("general", ksy)) {
            setPortalServerUrl(url);
        } else if (TextUtils.equals("shop", ksy)) {
            setShopServerUrl(url);
        }else if (TextUtils.equals("ad", ksy)) {
            setAdServerUrl(url);
        }
    }

    //    public boolean isRead(int id) {
    //        if (mReadComplatelist.isEmpty()) return false;
    //        return mReadComplatelist.contains(id);
    //    }

    public String getServerHost() {
        return mServerHost;
    }

    public int getServerPort() {
        return mServerPort;
    }

    public void setServerUrl(String host, int port) {
        //check
        mServerHost = host;
        mServerPort = port;
        if (port == -1) {
            mBaseUrl = "http://" + mServerHost;
        } else {
            mBaseUrl = "http://" + mServerHost + (port != 80 ? ":" + port : "");
        }
        //        mBaseUrl+= "/services/";
        //        mBaseUrl = "http://" + mServerHost + (mServerPort != 80 ? ":" + mServerPort : "") + "/API/";
        //        mBaseUrl = "http://" + mServerHost + (mServerPort != 80 ? ":" + mServerPort : "") + "/";
        //        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        //        SharedPreferences.Editor editor = sharedPref.edit();
        //        editor.putString("pref_url", host);
        //        editor.putString("pref_port", "" + port);
        //        editor.commit();
    }

    public Context getContext() {
        return mContext;
    }

    public boolean IsLogin() {
        return mIsLogin;
    }

    public boolean IsSaveUser() {
        return mSaveUser;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public String makeUrl(String page, String... params) {
        String port = mServerPort != 80 ? ":" + String.valueOf(mServerPort) : "";

        String url = "http://" + mServerHost + port + "/";
        url += page;
        try {
            if (params != null && params.length > 0) {
                url += "?";
                for (int i = 0; i < params.length; i += 2) {
                    url += params[i].toString() + "=" + URLEncoder.encode(params[i + 1].toString(), "UTF-8") + "&";
                }
                url = url.substring(0, url.length() - 1);
            }
        } catch (UnsupportedEncodingException e) {
            Log.e("AppApi", e.getMessage());
        }
        return url;
    }

    public String getPortalServerUrl() {
        return PortalServerUrl;
    }

    public void setPortalServerUrl(String portalServerUrl) {
        PortalServerUrl = portalServerUrl;
    }

    public String getLiveServerUrl() {
        return LiveServerUrl;
    }

    public void setLiveServerUrl(String liveServerUrl) {
        LiveServerUrl = liveServerUrl;
    }

    public String getCommunityServerUrl() {
        return CommunityServerUrl;
    }

    public void setCommunityServerUrl(String communityServerUrl) {
        CommunityServerUrl = communityServerUrl;
    }

    public String getBaseShareLiveUrl() {
        return baseShareLiveUrl;
    }

    public String getBaseShareGuessLiveUrl() {
        return baseShareGuessLiveUrl;
    }

    public String getBaseShareBackplayUrl() {
        return baseShareBackplayUrl;
    }

    public String getShopServerUrl() {
        return shopServerUrl;
    }

    public void setShopServerUrl(String shopServerUrl) {
        this.shopServerUrl = shopServerUrl;
    }

    public String getBaseMobileWebUrl() {
        return baseMobileWebUrl;
    }

    public void setBaseMobileWebUrl(String baseMobileWebUrl) {
        this.baseMobileWebUrl = baseMobileWebUrl;
    }

    public String getExternalLive() {
        return externalLive;
    }

    public String getAdServerUrl() {
        return adServerUrl;
    }

    public void setAdServerUrl(String adServerUrl) {
        this.adServerUrl = adServerUrl;
    }
}

