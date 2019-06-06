package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.ganzcms.app.view.OnceDayShowInfoMananger;
import com.dfsx.ganzcms.app.view.OpenAppOKDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import org.json.JSONObject;

public class TaskManager {

    /**
     * 打开客户端
     *
     * @param context
     */
    public static void uploadOpenAppTask(Context context) {
        String url = App.getInstance().getmSession().getPortalServerUrl() + "/public/users/current/open-app";
        new DataRequest<Boolean>(context) {

            @Override
            public Boolean jsonToBean(JSONObject json) {
                return true;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder().setUrl(url)
                                .setRequestType(DataReuqestType.POST)
                                .setToken(App.getInstance().getCurrentToken())
                                .build()
                        , false)
                .setCallback(new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        if (data && !OnceDayShowInfoMananger.
                                isShowToday(OnceDayShowInfoMananger.KEY_TASK_OPEN_APP)) {
                            new OpenAppOKDialog(App.getInstance().getTopActivity())
                                    .autoShow();
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 分享新闻的任务。 这个接口是CMS里面的
     *
     * @param context
     */
    public static void uploadShareNewsTask(Context context) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/users/current/share-news";
        new DataRequest<Boolean>(context) {

            @Override
            public Boolean jsonToBean(JSONObject json) {
                return true;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder().setUrl(url)
                                .setRequestType(DataReuqestType.POST)
                                .setToken(App.getInstance().getCurrentToken())
                                .build()
                        , false)
                .setCallback(null);
    }
}
