package com.dfsx.lzcms.liveroom.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.IGetLiveServiceIntroduce;
import com.dfsx.lzcms.liveroom.business.LiveRoomServiceIntroduceHelper;
import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuwb on 2017/6/20.
 */
public class LiveServiceIntroduceFragment extends Fragment implements ChromeClientCallbackManager.ReceivedTitleCallback {

    private Activity act;
    private Context context;
    private RelativeLayout viewContainer;
    private TextView introduceTextView;

    private AgentWeb agentWeb;

    private IGetLiveServiceIntroduce liveServiceIntroduceGetter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        act = getActivity();
        context = getContext();
        return inflater.inflate(R.layout.frag_live_service_introduce, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewContainer = (RelativeLayout) view.findViewById(R.id.introduce_layout);
        introduceTextView = (TextView) view.findViewById(R.id.text_introduce);
        liveServiceIntroduceGetter = new LiveRoomServiceIntroduceHelper(context, getRoomEnterId());
        getData();
    }

    private String getRoomEnterId() {
        String id = "";
        if (getActivity() instanceof AbsChatRoomActivity) {
            id = ((AbsChatRoomActivity) getActivity()).getRoomEnterId();
        }
        return id;
    }

    private void getData() {
        liveServiceIntroduceGetter.getLiveServiceIntroduce(getRoomId(), new DataRequest.DataCallback<String>() {
            @Override
            public void onSuccess(boolean isAppend, String data) {
                if (!TextUtils.isEmpty(data)) {
                    int index = isWebIndex(data);
                    if (index == 1) {
                        //网页链接
                        showWebLink(data);
                    } else {
                        showWebData(data);
                    }
                } else {
                    introduceTextView.setText("");
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
    }

    private void showWebLink(String link) {
        if (agentWeb == null) {
            agentWeb = AgentWeb.with(this)//传入Activity
                    .setAgentWebParent(viewContainer, new RelativeLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                    .useDefaultIndicator()// 使用默认进度条
                    .setReceivedTitleCallback(this) //设置 Web 页面的 title 回调
                    .createAgentWeb()//
                    .ready()
                    .go(link);
        } else {
            agentWeb.getLoader().loadUrl(link);
        }
    }

    private void showWebData(String webBody) {
        if (agentWeb == null) {
            agentWeb = AgentWeb.with(this)//传入Activity
                    .setAgentWebParent(viewContainer, new RelativeLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                    .useDefaultIndicator()// 使用默认进度条
                    .setReceivedTitleCallback(this) //设置 Web 页面的 title 回调
                    .createAgentWeb()//
                    .ready()
                    .go("about:blank");
        }
        agentWeb.getLoader().loadDataWithBaseURL("file:///android_asset/",
                getContenHtmlWeb(webBody),
                "text/html",
                "utf-8", null);
    }

    private int isWebIndex(String text) {
        Pattern pattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
        Matcher m = pattern.matcher(text);
        boolean isFind = m.find();
        if (isFind) {
            return 1;
        }
        if (text.contains("<body")) {
            return 2;
        }
        return 0;
    }

    private long getRoomId() {
        long id = 0;
        if (act instanceof AbsChatRoomActivity) {
            id = ((AbsChatRoomActivity) act).getRoomId();
        }
        return id;
    }

    public String getContenHtmlWeb(String body) {
        String txtWeb = "<html>\n" +
                "<meta name=\"viewport\" " +
                "content=\"width=device-width\"/>\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"main.css\" />\n" +
                "<script language=\"javascript\">\n" +
                "    function imgResize() {\n" +
                "        var imgs = document.getElementsByTagName(\"img\");\n" +
                "        var array = new Array();\n" +
                "        for (var j = 0; j < imgs.length; j++) {\n" +
                "         array[j] = imgs[j].attributes[\'src\'].value;\n" +
                "          }\n" +
                "        for (var i = 0; i < imgs.length; i++) {\n" +
                "            imgs[i].pos = i;\n" +
                "            imgs[i].onclick=function()" +
                "            {\n" +
                "              var pos = this.pos;\n" +
                "window.imagelistner.openImage(array.join(\",\"),pos);\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    </script>\n" +
                "<body>";


        txtWeb += "<p style=\"text-align:justify;font-size:17px;line-height:180%;text-indent:2em;\">";
        txtWeb += body;
        txtWeb += "</p>";
        txtWeb += "</body></html>";
        return txtWeb;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (agentWeb != null) {
            agentWeb.getWebLifeCycle().onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (agentWeb != null) {
            agentWeb.getWebLifeCycle().onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (agentWeb != null) {
            agentWeb.getWebLifeCycle().onDestroy();
        }
    }
}
