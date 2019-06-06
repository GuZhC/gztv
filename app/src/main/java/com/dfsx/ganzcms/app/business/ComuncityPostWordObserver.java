package com.dfsx.ganzcms.app.business;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.file.upload.PublishData;
import com.dfsx.core.log.LogcatPushHelper;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.DiscleRevelEntry;
import com.dfsx.lzcms.liveroom.view.LXDialog;
import com.dfsx.procamera.model.UpdataModel;
import rx.Observer;

public class ComuncityPostWordObserver implements Observer<PublishData> {

    private PublishData wordData;
    private FileUploadProgress fileUploadProgress;
    private ICommPostObserInter callBack;

    public ComuncityPostWordObserver(FileUploadProgress fileProgress) {
        this.fileUploadProgress = fileProgress;
    }

    public void setCallBack(ICommPostObserInter callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Log.e("TAG", "eeeeeeeeeeeeee");
        checkError();
//        if (fileUploadProgress != null) {
//            fileUploadProgress.hideProgressView();
//        }
//        App.getInstance().setDisclureIsOk(true);
//        if (callBack != null)
//            callBack.onResult(true);
    }

    @Override
    public void onNext(PublishData wordData) {
        this.wordData = wordData;
        Log.e("TAG", "onNext ssssssssss");
        checkError();

//        if (fileUploadProgress != null) {
//            fileUploadProgress.hideProgressView();
//        }
//        App.getInstance().setDisclureIsOk(true);
//
//        String titleMsg = "圈子";
//        if (wordData != null && wordData.getData() != null) {
//            if (wordData.getData() instanceof DiscleRevelEntry) {
//                titleMsg = "爆料";
//                App.getInstance().setDisclureCompalte(true);
//                boolean flag = true;
//                if (!wordData.isPostSuccess())
//                    flag = false;
//                App.getInstance().setDisclureIsOk(flag);
//            }
//        }
//
//        if (callBack != null)
//            callBack.onResult(true);
//
//        if (wordData != null && wordData.isPostSuccess()) {
//            Toast.makeText(App.getInstance().getApplicationContext(),
//                    "发布成功", Toast.LENGTH_SHORT).show();
//        } else {
//            String error = wordData != null ?
//                    wordData.getErrorMessage() + " " : "";
//            if (App.getInstance().getTopActivity() != null) {
//                String msg = titleMsg + "发布失败";
//                if (error != null && !TextUtils.isEmpty(error))
//                    msg += error;
//                msg += ",是否重试?";
//                showRepostDialog(App.getInstance().getTopActivity(), msg);
//            } else {
//                Toast.makeText(App.getInstance().getApplicationContext(),
//                        titleMsg + "发布失败,请稍后重新发布", Toast.LENGTH_SHORT).show();
//            }
//        }

    }

    public void checkError() {
        if (fileUploadProgress != null) {
            fileUploadProgress.hideProgressView();
        }
        String titleMsg = "";
        if (!(wordData == null || wordData.getData() == null)) {
            if (wordData.getData() instanceof DiscleRevelEntry) {
                titleMsg = "爆料";
                App.getInstance().setDisclureCompalte(true);
                boolean flag = true;
                if (!wordData.isPostSuccess())
                    flag = false;
                App.getInstance().setDisclureIsOk(flag);
                LogcatPushHelper.getInstance(App.getInstance().getApplicationContext()).stop(true);
            } else if (wordData.getData() instanceof UpdataModel) {
                titleMsg = "视频";
            } else
                titleMsg = "圈子";
        }
        if (callBack != null)
            callBack.onResult(true);

        if (!(wordData == null || !wordData.isPostSuccess())) {
            Toast.makeText(App.getInstance().getApplicationContext(),
                    titleMsg + "发布成功", Toast.LENGTH_SHORT).show();
        } else {
            if (App.getInstance().getTopActivity() != null) {
                String error = "";
                if (wordData != null)
                    error = wordData.getErrorMessage();
                String msg = titleMsg + "发布失败：";
                if (error != null && !TextUtils.isEmpty(error))
                    msg += error;
                msg += ",是否重试?";
                showRepostDialog(App.getInstance().getTopActivity(), msg);
            } else {
                Toast.makeText(App.getInstance().getApplicationContext(),
                        titleMsg + "发布失败,请稍后重新发布", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showRepostDialog(Activity act, String msg) {
        LXDialog dialog = new LXDialog.Builder(act)
                .isEditMode(false)
                .setMessage(msg)
                .isHiddenCancleButton(false)
                .setPositiveButton("确定", new LXDialog.Builder.LXDialogInterface() {
                    @Override
                    public void onClick(DialogInterface dialog, View v) {
                        PublishDataManager.getInstance().repost(wordData);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new LXDialog.Builder.LXDialogInterface() {
                    @Override
                    public void onClick(DialogInterface dialog, View v) {
                        dialog.dismiss();
                    }
                })
                .create();
        if (act != null && !act.isFinishing()) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    public interface ICommPostObserInter {
        public void onResult(boolean isComplate);
    }


}
