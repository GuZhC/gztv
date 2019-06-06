package com.dfsx.lzcms.liveroom.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.rx.RxBus;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.business.AppManager;
import com.dfsx.lzcms.liveroom.util.AndroidUtil;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class VoteWebFragment extends BaseAndroidWebFragment {

    private Handler handler = new Handler(Looper.getMainLooper());
    private Subscription logSub;
    private boolean isWebLoadFinish;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initRegister() {
        logSub = RxBus.getInstance().toObserverable(Intent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Intent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Intent intent) {
                        if (TextUtils.equals(IntentUtil.ACTION_LOGIN_OK, intent.getAction())) {
                            if (isWebLoadFinish) {
                                loadLoginInfoJS();
                            }
                        }
                    }
                });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isWebLoadFinish = false;
        initRegister();
        mAgentWeb.getJsInterfaceHolder()
                .addJavaObject("login", new LoginInterface(getActivity()));
    }


    @Override
    protected void onWebPageFinished(WebView view, String url) {
        super.onWebPageFinished(view, url);
        isWebLoadFinish = true;
        loadLoginInfoJS();
    }

    private void loadLoginInfoJS() {
        String jsString = "javascript:window.DfsxNative=" +
                "{" +
                "platform:'android'," +
                "version:'" + AndroidUtil.getAppVersionCode(getContext()) + "'," +
                " isLogin: " + AppManager.getInstance().getIApp().isLogin() + ", " +
                "userToken:'" + AppManager.getInstance().getIApp().getCurrentToken() + "'," +
                "login: function () " +
                "{  " +
                "window.login.loginApp()" +
                "} " +
                "};";
        Log.e("TAG", "JS == " + jsString);
        if (mAgentWeb != null) {
            mAgentWeb.getLoader().loadUrl(jsString);
        }
    }

    @Override
    protected String getWebUrl() {
        //http://192.168.6.85:8080
        String urlScheme = AppManager.getInstance().getIApp().getMobileWebUrl();

        String webUrl = urlScheme + "/live/vote/" + getRoomId() + "/" + getRoomEnterId() + getWebUrlParams();
        Log.e("TAG", "web url == " + webUrl);
        return webUrl;
    }

    protected String getWebUrlParams() {
        return "?token=" + AppManager.getInstance().getIApp().getCurrentToken() + "&client=android";
    }

    protected long getRoomId() {
        long id = 0;
        if (activity instanceof AbsChatRoomActivity) {
            id = ((AbsChatRoomActivity) activity).getShowId();
        }
        return id;
    }

    protected String getRoomEnterId() {
        String ids = "";

        if (activity instanceof AbsChatRoomActivity) {
            ids = ((AbsChatRoomActivity) activity).getRoomEnterId();
        }
        return ids;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (logSub != null) {
            logSub.unsubscribe();
        }
    }

    /**
     * web调用的登录接口
     */
    public class LoginInterface {

        private Context context;


        public LoginInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void loginApp() {
            Log.e("TAG", "JavascriptInterface loginApp------------");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    IntentUtil.goToLogin(context);
                }
            });
        }
    }
}
