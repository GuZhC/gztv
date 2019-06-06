package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.dfsx.core.file.upload.OkHttpUploadFile;
import com.dfsx.ganzcms.app.R;

/**
 * 发布上传文件的进度条显示类
 */
public class FileUploadProgress implements OkHttpUploadFile.UploadPercentListener {

    private Context context;
    private ViewGroup progressContainer;

    private ProgressBar progressBar;
    private View progressView;
    private boolean isAddProgressBar;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null && progressBar != null) {
                if (!isAddProgressBar) {
                    showProgressbar();
                }
                progressBar.setProgress(msg.what);
            }
        }
    };

    public FileUploadProgress(Context context) {
        this.context = context;
        initProgress();
    }


    @Override
    public void onUploadPercent(int percent, boolean isDone) {
        handler.sendEmptyMessage(percent);
        if (isDone) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    hideProgressView();
                }
            });
        }
    }

    public void setProgressContainer(ViewGroup progressContainer) {
        this.progressContainer = progressContainer;
    }

    private void initProgress() {
        progressView = LayoutInflater.from(context)
                .inflate(R.layout.file_upload_progress, null);
        progressBar = (ProgressBar) progressView.findViewById(R.id.upload_progress);
    }

    private boolean isHasProgressView() {
        if (progressContainer != null) {
            for (int i = 0; i < progressContainer.getChildCount(); i++) {
                int id = progressContainer.getChildAt(i)
                        .getId();
                if (id == R.id.upload_progress_layout) {
                    return true;
                }
            }
        }
        return false;
    }

    public void showProgressbar() {
        if (progressContainer != null && !isHasProgressView()) {
            //防止重复添加
            progressContainer.addView(progressView);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressContainer.requestLayout();
                }
            }, 1000);
        }
        isAddProgressBar = progressContainer != null;
    }

    public void hideProgressView() {
        if (progressContainer != null) {
            if (progressView != null) {
                progressContainer.removeView(progressView);
            }
        }
        isAddProgressBar = false;
    }
}
