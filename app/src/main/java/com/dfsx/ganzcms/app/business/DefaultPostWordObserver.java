package com.dfsx.ganzcms.app.business;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.WordData;
import com.dfsx.lzcms.liveroom.view.LXDialog;
import rx.Observer;

public class DefaultPostWordObserver implements Observer<WordData> {

    private WordData wordData;
    private FileUploadProgress fileUploadProgress;

    public DefaultPostWordObserver(FileUploadProgress fileProgress) {
        this.fileUploadProgress = fileProgress;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Log.e("TAG", "eeeeeeeeeeeeee");
        if (fileUploadProgress != null) {
            fileUploadProgress.hideProgressView();
        }
    }

    @Override
    public void onNext(WordData wordData) {
        this.wordData = wordData;
        Log.e("TAG", "onNext ssssssssss");
        if (fileUploadProgress != null) {
            fileUploadProgress.hideProgressView();
        }
        if (wordData != null && wordData.isPostSuccess()) {
            Toast.makeText(App.getInstance().getApplicationContext(),
                    "投稿成功", Toast.LENGTH_SHORT).show();
        } else {
            String titleMsg = wordData != null ?
                    wordData.getTitle() + " " : "";
            if (App.getInstance().getTopActivity() != null) {
                String msg = titleMsg + "投稿失败,是否重试?";
                showRepostDialog(App.getInstance().getTopActivity(), msg);
            } else {
                Toast.makeText(App.getInstance().getApplicationContext(),
                        titleMsg + "投稿失败,请稍后重新发布", Toast.LENGTH_SHORT).show();
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
                        PostWordManager.getInstance().repost(wordData);
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
}
