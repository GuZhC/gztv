package com.dfsx.ganzcms.app.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import com.dfsx.ganzcms.app.R;

//import android.os.PersistableBundle;

/**
 * Created by heyang on 2016/8/29
 */
public class CommWebFragment extends Fragment {

    String mUrl = "";
    public static final String PARAMS_URL = "CommWebFragment_url";
    ProgressDialog mLoading;

    public static CommWebFragment newInstance(String url) {
        CommWebFragment fragment = new CommWebFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAMS_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void CommWebFragment() {

    }

    WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.frag_web, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUrl = bundle.getString(PARAMS_URL);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView = (WebView) view.findViewById(R.id.webView);
        initWebView();

        mLoading = ProgressDialog.show(getActivity(), "加载中...", "");
        if (!TextUtils.isEmpty(mUrl) && mUrl.startsWith("www.")) {
            mUrl = "http://" + mUrl;
        }
        mWebView.loadUrl(mUrl);
    }

    public void initWebView() {
        WebSettings webSettings = mWebView.getSettings();


        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        }

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                }
                return false;
            }
        });

        webSettings.setJavaScriptEnabled(true);
        /***打开本地缓存提供JS调用**/
        mWebView.getSettings().setDomStorageEnabled(true);
        // Set cache size to 8 mb by default. should be more than enough
        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        String appCachePath = this.getActivity().getApplicationContext().getCacheDir().getAbsolutePath();
        mWebView.getSettings().setAppCachePath(appCachePath);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setSupportZoom(true);//设定支持缩放
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                if (!view.getSettings().getLoadsImagesAutomatically()) {
                    view.getSettings().setLoadsImagesAutomatically(true);
                }
                if (mLoading != null && mLoading.isShowing())
                    mLoading.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String message = consoleMessage.message() + " -- line " + consoleMessage.lineNumber();
                switch (consoleMessage.messageLevel()) {
                    case ERROR:
                        Log.e("JSTag", message);

                        break;
                    default:
                        Log.i("JSTag", message);

                        break;
                }
                return true;
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        final ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(mWebView);
        }
        mWebView.destroy();
        mWebView = null;
    }
}

