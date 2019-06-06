package com.dfsx.procamera.busniness;

/**
 * Created by heyang on 2016/10/25.
 */
public interface OnDialogCallBack {

    /**
     * type:  1:  评论   2：自恢复
     *
     * @param type
     */

    public void onCall(int type,long actvivId,long replayId,long sumReplynumber,OnCommendDialoglister onCommendDialoglister);



}
