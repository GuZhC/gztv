package com.dfsx.procamera.busniness;

import android.content.Context;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.model.LoginParams;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.util.StringUtil;

/**
 * create by  heyang  2018-6-25
 */

public interface IActivtiySelectItemiter {
    /**
     * 录制的视频url
     *
     * @param url
     */
    public void OnComplete(String url);

    /**
     * 取消
     */
    public void onCancel();


}