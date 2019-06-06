package com.dfsx.ganzcms.app.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.lzcms.liveroom.fragment.BaseAndroidWebFragment;
import org.json.JSONObject;

public class LiveWebLinkFragment extends BaseAndroidWebFragment {

    private String jsString;
    private Handler handler = new Handler();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topWebView.addView(LayoutInflater.from(getContext())
                .inflate(R.layout.web_live_header_layout, null));
        if (mAgentWeb != null) {
            mAgentWeb.getWebCreator().get().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected String getWebUrl() {
        String url = "about://blank";
        String exteLive = App.getInstance().getmSession().getExternalLive();
        try {
            JSONObject json = new JSONObject(exteLive);
            url = json.optString("url");
            String embed_js = json.optString("embed_js");
            JSONObject emBedJson = new JSONObject(embed_js);
            jsString = emBedJson.optString("list_inject_js");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }


    @Override
    protected void onWebPageStarted(WebView view, String url, Bitmap favicon) {
        super.onWebPageStarted(view, url, favicon);
        Log.e("TAG", "start url == " + url);
    }

    @Override
    protected void onWebPageFinished(WebView view, String url) {
        super.onWebPageFinished(view, url);
        Log.e("TAG", "end url == " + url);
        loadNoTitleJS();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAgentWeb != null) {
                    mAgentWeb.getWebCreator().get().setVisibility(View.VISIBLE);
                }
            }
        }, 1500);

    }

    @Override
    protected boolean shouldOverrideUrl(WebView view, String url) {
        boolean noHttpIntent = super.shouldOverrideUrl(view, url);
        if (!noHttpIntent) {
            Bundle bundle = new Bundle();
            bundle.putString(LiveWebDetailsFragment.PARAMS_URL, url);
            WhiteTopBarActivity.startAct(getActivity(), LiveWebDetailsFragment.class.getName(), "", R.drawable.common_head_share, bundle);
        }
        return true;
    }

    private void loadNoTitleJS() {
        Log.e("TAG", "JS == " + jsString);
        if (mAgentWeb != null && !TextUtils.isEmpty(jsString)) {
            mAgentWeb.getLoader().loadUrl("javascript:" + jsString);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("TAG", "live tab--- " + isVisibleToUser);
        if (mAgentWeb != null) {
            if (!isVisibleToUser) {
                mAgentWeb.getWebLifeCycle().onPause();
            } else {
                mAgentWeb.getWebLifeCycle().onResume();
            }
        }
    }

}
