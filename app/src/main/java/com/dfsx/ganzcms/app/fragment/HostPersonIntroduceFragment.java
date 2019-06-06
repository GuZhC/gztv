package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.dfsx.core.common.adapter.BaseRecyclerViewAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewDataAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.IHostDetails;
import com.dfsx.ganzcms.app.util.WebSettingUtils;

import java.util.ArrayList;

public class HostPersonIntroduceFragment extends AbsRecyclerViewFragment {

    private HostIntroduceAdapter adapter;
    private IHostDetails hostDetails;
    private WebView introWebView;

    @Override
    public BaseRecyclerViewAdapter getRecyclerViewAdapter() {
        adapter = new HostIntroduceAdapter();
        return adapter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //        ArrayList<String> list = new ArrayList<>();
        //        list.add("");
        //        adapter.update(list, false);
    }

    public void setHostData(IHostDetails details) {
        this.hostDetails = details;
        if (hostDetails != null) {
            ArrayList<String> list = new ArrayList<>();
            list.add(hostDetails.getHostIntroduce());
            adapter.update(list, false);
        }
    }

    class HostIntroduceAdapter extends BaseRecyclerViewDataAdapter<String> {

        @Override
        public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_host_introduce, null);
            HostIntroduceViewHolder hostIntroduceViewHolder = new HostIntroduceViewHolder(v, viewType);
            introWebView = (WebView) v.findViewById(R.id.web_introduce);
            WebSettings settings = WebSettingUtils.toAgentWebSettings(introWebView);
            introWebView.setWebViewClient(new WebViewClient());
            hostIntroduceViewHolder.setWebView(introWebView);
            return hostIntroduceViewHolder;
        }

        @Override
        public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
            WebView webView = ((HostIntroduceViewHolder) holder).getWebView();
            webView.resumeTimers();
            String itemString = list.get(position);
            webView.loadDataWithBaseURL("file:///android_asset/",
                    itemString, "text/html", "utf-8", null);
        }
    }

    public void cleareWebView() {
        if (introWebView == null) return;

        introWebView.clearCache(true);
        introWebView.clearHistory();

        introWebView.removeAllViews();
        final ViewGroup viewGroup = (ViewGroup) introWebView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(introWebView);
        }
        introWebView.destroy();
        introWebView = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cleareWebView();
    }

    class HostIntroduceViewHolder extends BaseRecyclerViewHolder {

        private WebView webView;

        public HostIntroduceViewHolder(View v, int viewType) {
            super(v, viewType);
        }

        public HostIntroduceViewHolder(int layoutRes, ViewGroup parent, int viewType) {
            super(layoutRes, parent, viewType);
        }

        public HostIntroduceViewHolder(View itemView) {
            super(itemView);
        }

        public void setWebView(WebView webView) {
            this.webView = webView;
        }

        public WebView getWebView() {
            return webView;
        }
    }
}
