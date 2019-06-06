package com.dfsx.ganzcms.app.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.ganzcms.app.App;
import com.dfsx.lzcms.liveroom.fragment.BaseAndroidWebFragment;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import org.json.JSONObject;

public class LiveWebDetailsFragment extends BaseAndroidWebFragment {

    private SharePopupwindow popupwindow;

    private String title;

    private String thumbImage = "";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTopBarClickEvent();
    }

    private void setTopBarClickEvent() {
        Activity act = getActivity();
        if (act instanceof WhiteTopBarActivity) {
            WhiteTopBarActivity topBarActivity = (WhiteTopBarActivity) act;
            topBarActivity.getTopBarRightText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShareClick(v);
                }
            });
        }
    }

    private void onShareClick(View v) {
        if (popupwindow == null) {
            popupwindow = new SharePopupwindow(activity);
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
        if (!getActivity().isFinishing()) {
            popupwindow.show(v);
        }
    }

    private void onSharePlatform(SharePlatform platform) {
        ShareContent content = new ShareContent();
        content.title = title;
        content.disc = "";
        content.thumb = thumbImage;
        content.type = ShareContent.UrlType.WebPage;
        content.url = mUrl;
        AbsShare share = ShareFactory.createShare(getContext(), platform);
        share.share(content);
    }

    @Override
    protected void onWebPageFinished(WebView view, String url) {
        super.onWebPageFinished(view, url);
        title = view.getTitle();
        try {
            String jsString = null;
            String exteLive = App.getInstance().getmSession().getExternalLive();
            if (!TextUtils.isEmpty(exteLive)) {
                JSONObject ext = new JSONObject(exteLive);
                JSONObject jsJson = new JSONObject(ext.optString("embed_js"));
                jsString = jsJson.optString("detail_inject_js");
                String thumbJs = jsJson.optString("detail_js_getThumbnail");
                jsString = jsString + ";" + thumbJs;
            }
            Log.e("TAG", "JS == " + jsString);
            if (mAgentWeb != null && jsString != null) {
                mAgentWeb.getLoader().loadUrl("javascript:" + jsString);
            }
            view.postDelayed(new Runnable() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    mAgentWeb.getJsEntraceAccess().quickCallJs("getVideoPoster", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            if (!TextUtils.isEmpty(value) && !TextUtils.equals(value, "null")) {
                                thumbImage = value.replace("\"", "");
                            } else {
                                thumbImage = "";
                            }
                        }
                    });
                }
            }, 1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
