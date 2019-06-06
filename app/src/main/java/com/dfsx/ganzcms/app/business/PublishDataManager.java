package com.dfsx.ganzcms.app.business;

import android.util.Log;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.upload.OkHttpUploadFile;
import com.dfsx.core.file.upload.PublishData;
import com.dfsx.core.file.upload.UploadFileData;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 发布数据管理类
 * 队列发布数据的逻辑类
 * <p>
 * 发布的结果由 设置setPostWordObserver 返回
 */
public class PublishDataManager {
    private static PublishDataManager instance = new PublishDataManager();
    private OkHttpUploadFile uploadFileHelper;

    private Observer<PublishData> postWordObserver;

    private BlockingQueue<PublishData> postPublishDataQueue = new LinkedBlockingQueue<>();

    private PublishDataManager() {
        uploadFileHelper = new OkHttpUploadFile();
    }

    public String errorMessage = "";

    /**
     * 是否正在发布
     */
    private boolean isPosting = false;

    public static PublishDataManager getInstance() {
        return instance;
    }

    public void setUploadPercentListener(OkHttpUploadFile.UploadPercentListener uploadPercentListener) {
        if (uploadFileHelper != null) {
            uploadFileHelper.setUploadPercentListener(uploadPercentListener);
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void post(PublishData data) {
        postPublishDataQueue.add(data);
        startPost();
    }

    private void startPost() {
        if (!isPosting) {
            new PublishThread().start();
        }
    }

    class PublishThread extends Thread {
        @Override
        public void run() {
            super.run();
            PublishData data;
            isPosting = true;
            Log.e("TAG", "thread start----");
            while ((data = postPublishDataQueue.poll()) != null) {
                Observable<PublishData> dataObservable = postWordInThread(data);
                if (postWordObserver != null) {
                    dataObservable.observeOn(AndroidSchedulers.mainThread())
                            .subscribe(postWordObserver);
                } else {
                    dataObservable.subscribe();
                }
            }
            isPosting = false;
        }
    }

    private Observable<PublishData> postWordInThread(PublishData data) {
        if (data != null && !data.isPostSuccess()) {
            ArrayList<UploadFileData> list = data.getUploadFileDataList();
            boolean isPushlishOk = false;
            boolean is = uploadFileHelper.upload(data,list);
            Log.e("TAG", "file upload result == " + is);
            String errormsg="";
            if (!is) {
                int i = 3;
                do {
                    Log.e("TAG", "网络尝试 i= " + i);
                    is = uploadFileHelper.upload(data, list);
                    if (is) break;
                    i--;
                } while (i > 0);
            }
            if (is) {
                data.setErrorMessage("");
                if (data.getPushAction() != null) {
                    try {
                        isPushlishOk = data.getPushAction()
                                .onPublishData(list, data.getData());
                    } catch (ApiException e) {
                        e.printStackTrace();
                        errormsg = JsonCreater.getErrorMsg(e.toString());
                    }
                }
            } else {
                data.setErrorProgressType(PublishData.ERROR_UPLOAD_FILE);
                data.setPostSuccess(false);
//                data.setErrorMessage(errormsg);
                return Observable.just(data);
            }
            if (!isPushlishOk) {
                data.setErrorProgressType(PublishData.ERROR_PUBLISH_DATA);
                data.setPostSuccess(false);
                data.setErrorMessage(errormsg);
                return Observable.just(data);
            }
        }
        data.setErrorProgressType(PublishData.ERROR_NONE);
        data.setPostSuccess(true);
        return Observable.just(data);
    }


    /**
     * 重试功能
     */
    public void repost(PublishData data) {
        if (data != null && !data.isPostSuccess()) {
            post(data);
        }
    }

    public void setPostWordObserver(Observer<PublishData> postWordObserver) {
        this.postWordObserver = postWordObserver;
    }
}
