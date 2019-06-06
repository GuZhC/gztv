package com.dfsx.ganzcms.app.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.dfsx.ganzcms.app.util.CameraInterface;

/**
 * Created by liuwb on 2017/1/4.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "yanzi";
    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private boolean isStartPreview;
    private boolean isCameraOpened;
    private boolean isSurfaceCreated;

    private CameraInterface.CamOpenOverCallback callback = new CameraInterface.CamOpenOverCallback() {
        @Override
        public void cameraHasOpened() {
            isCameraOpened = true;
            if (isSurfaceCreated) {
                startPreview();
            }
        }
    };

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
        isStartPreview = false;
        isCameraOpened = false;
        isSurfaceCreated = false;
        openCamera();
    }

    private void openCamera() {
        Thread openThread = new Thread() {
            @Override
            public void run() {
                CameraInterface.getInstance().doOpenCamera(callback);
            }
        };
        openThread.start();
    }

    private void startPreview() {
        if (!isStartPreview) {
            isStartPreview = true;
            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            float h = dm.heightPixels;
            float w = dm.widthPixels;
            float rate = h / w;
            CameraInterface.getInstance().doStartPreview(getSurfaceHolder(), rate);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.i(TAG, "surfaceCreated...");
        isSurfaceCreated = true;
        if (isCameraOpened) {
            startPreview();
        } else {
            if (CameraInterface.getInstance().isReleasedCamera()) {
                openCamera();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        Log.i(TAG, "surfaceChanged...");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.i(TAG, "surfaceDestroyed...");
        isSurfaceCreated = false;
        isStartPreview = false;
        isCameraOpened = false;
        CameraInterface.getInstance().doStopCamera();
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

}

