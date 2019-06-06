package com.dfsx.ganzcms.app.business;

import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.util.StringUtil;

public class UploadHelper {

    public static String getCMSImageUploadUrl() throws ApiException {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/pictures/uploader";
        String res = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
        StringUtil.checkHttpResponseError(res);
        return UtilHelp.checkUrl(res);
    }

    public static String getCMSVideoUploadUrl() throws ApiException {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/videos/uploader";
        String res = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
        StringUtil.checkHttpResponseError(res);
        return UtilHelp.checkUrl(res);
    }
}
