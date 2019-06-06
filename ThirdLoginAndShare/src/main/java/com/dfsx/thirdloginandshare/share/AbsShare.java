package com.dfsx.thirdloginandshare.share;

import android.content.Context;

/**
 * Created by Administrator on 2015/12/24.
 */
public abstract class AbsShare {

    protected Context context;

    public AbsShare(Context context) {
        this.context = context;
    }

    protected OnShareCallBackListener callBackListener;

    public abstract void share(ShareContent content);

    /**
     * 设置回调监听事件
     *
     * @param l
     */
    public void setOnShareCallBackListener(OnShareCallBackListener l) {
        this.callBackListener = l;
    }

    public interface OnShareCallBackListener {
        void onComplete();

        void onError();

        void onCancle();
    }
}
