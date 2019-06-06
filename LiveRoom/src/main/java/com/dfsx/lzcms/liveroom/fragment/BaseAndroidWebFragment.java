package com.dfsx.lzcms.liveroom.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.dfsx.core.CoreApp;
import com.dfsx.lzcms.liveroom.R;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;

public class BaseAndroidWebFragment extends Fragment implements ChromeClientCallbackManager.ReceivedTitleCallback, FragmentBackHandler {
    public static final String PARAMS_URL = "BaseAndroidWebFragment_web_url";

    protected Activity activity;
    protected String mUrl = "";

    public static BaseAndroidWebFragment newInstance(String url) {
        BaseAndroidWebFragment fragment = new BaseAndroidWebFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAMS_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    protected AgentWeb mAgentWeb;
    private RelativeLayout webViewContainer;
    protected LinearLayout topWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        View view;
        view = inflater.inflate(R.layout.frag_base_webview_layout, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUrl = bundle.getString(PARAMS_URL);
        }
        if (TextUtils.isEmpty(mUrl)) {
            mUrl = getWebUrl();
        }
        return view;
    }

    protected String getWebUrl() {
        return "http://m.ysxtv.cn/live";
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topWebView = (LinearLayout) view.findViewById(R.id.top_web_view);
        webViewContainer = (RelativeLayout) view.findViewById(R.id.web_view_container);

        mAgentWeb = AgentWeb.with(this)//传入Activity
                .setAgentWebParent(webViewContainer,
                        new RelativeLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .setReceivedTitleCallback(this) //设置 Web 页面的 title 回调
                .setWebViewClient(webViewClient)
                .setWebChromeClient(mWebChromeClient)
                .createAgentWeb()//
                .ready()
                .go(mUrl);

    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            onWebPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            onWebPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl().toString());
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.e("TAG", "web load url == " + url);
            super.onLoadResource(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("TAG", "web request url == " + url);
            if (shouldOverrideUrl(view, url)) {
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

    protected boolean shouldOverrideUrl(WebView view, String url) {
        if (!TextUtils.isEmpty(url) && !url.startsWith("http")) {
            try {
                Intent intent = new Intent();
                intent.setData(Uri.parse(url));
                getContext().startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
        }
    };

    protected void onWebPageStarted(WebView view, String url, Bitmap favicon) {
    }

    protected void onWebPageFinished(WebView view, String url) {
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mAgentWeb != null)
            mAgentWeb.uploadFileResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void releaseWeb() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseWeb();
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {

    }

    @Override
    public boolean onBackPressed() {
        //页面回退
        if (mAgentWeb != null) {
            return mAgentWeb.back();
        }
        return false;
    }
}
